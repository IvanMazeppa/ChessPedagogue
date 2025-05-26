package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * REVOLUTIONARY PersonalityEngine - Makes Stockfish play like chess legends!
 *
 * This is the breakthrough innovation: instead of just analyzing positions,
 * we actually make the engine PLAY like Tal, Fischer, Kasparov, etc. by:
 * 1. Finding similar positions from their actual games
 * 2. Boosting moves they would have played
 * 3. Balancing engine strength with historical personality
 *
 * Ben's vision brought to life! 🚀♟️
 */
public class PersonalityEngine {
    private static final String TAG = "PersonalityEngine";

    // Personality tuning parameters
    private static final float DEFAULT_PERSONALITY_WEIGHT = 0.3f; // How much to favor historical moves
    private static final float MIN_ENGINE_THRESHOLD = -1.0f; // Don't play moves worse than this
    private static final int MAX_SIMILAR_POSITIONS = 5; // Top N similar positions to consider
    private static final int STOCKFISH_CANDIDATES = 8; // Top moves from engine

    private static PersonalityEngine instance;

    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;

    // Core services
    private final FineTunedModelManager modelManager;
    private final OpenAIService openAIService;
    private final StockfishManager stockfishManager;

    // Personality settings
    private float personalityWeight = DEFAULT_PERSONALITY_WEIGHT;
    private String currentMaster = "tal";
    private boolean enablePersonalityPlay = true;

    /**
     * Move candidate with both engine and personality scoring
     */
    public static class PersonalityMove {
        public final String move;
        public final float engineScore;
        public final float personalityBonus;
        public final float finalScore;
        public final String historicalContext; // Why this move matches the master's style
        public final boolean isHistoricalMatch; // Did the master actually play this?

        public PersonalityMove(String move, float engineScore, float personalityBonus,
                               String historicalContext, boolean isHistoricalMatch) {
            this.move = move;
            this.engineScore = engineScore;
            this.personalityBonus = personalityBonus;
            this.finalScore = engineScore + personalityBonus;
            this.historicalContext = historicalContext;
            this.isHistoricalMatch = isHistoricalMatch;
        }

        @Override
        public String toString() {
            return String.format("%s (Engine: %.2f, Personality: +%.2f, Final: %.2f)%s",
                    move, engineScore, personalityBonus, finalScore,
                    isHistoricalMatch ? " [HISTORICAL]" : "");
        }
    }

    /**
     * Callback for personality-guided move selection
     */
    public interface PersonalityMoveCallback {
        void onPersonalityMoveSelected(PersonalityMove selectedMove, List<PersonalityMove> allCandidates);
        void onPersonalityAnalysisComplete(String analysis, String masterQuote);
        void onError(String errorMessage);
    }

    private PersonalityEngine(Context context, StockfishManager stockfishManager) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();
        this.stockfishManager = stockfishManager;

        // Initialize services
        this.modelManager = FineTunedModelManager.getInstance(context);
        this.openAIService = OpenAIService.getInstance();

        Log.d(TAG, "🎭 PersonalityEngine initialized - Ready to bring chess legends to life!");
    }

    public static synchronized PersonalityEngine getInstance(Context context, StockfishManager stockfishManager) {
        if (instance == null) {
            instance = new PersonalityEngine(context, stockfishManager);
        }
        return instance;
    }

    /**
     * THE CORE METHOD: Select a move that balances engine strength with master personality!
     * This is where the magic happens! ✨
     */
    public void selectPersonalityMove(String currentFen, PersonalityMoveCallback callback) {
        Log.d(TAG, "🎯 Selecting personality move for " + currentMaster);
        Log.d(TAG, "📋 Position: " + currentFen.substring(0, Math.min(30, currentFen.length())) + "...");

        if (!enablePersonalityPlay) {
            // Fall back to pure engine play
            selectPureEngineMove(currentFen, callback);
            return;
        }

        executorService.execute(() -> {
            try {
                // Step 1: Get top move candidates from Stockfish
                List<PersonalityMove> engineCandidates = getEngineCandidates(currentFen);
                Log.d(TAG, "🤖 Got " + engineCandidates.size() + " engine candidates");

                // Step 2: Find similar positions from master's games
                findSimilarPositions(currentFen, engineCandidates, callback);

            } catch (Exception e) {
                Log.e(TAG, "❌ Error in personality move selection", e);
                mainHandler.post(() -> callback.onError("Failed to analyze position: " + e.getMessage()));
            }
        });
    }

    /**
     * Get top move candidates from Stockfish with evaluations
     */
    private List<PersonalityMove> getEngineCandidates(String currentFen) {
        List<PersonalityMove> candidates = new ArrayList<>();

        try {
            // Set the position
            stockfishManager.setPosition(currentFen);

            // Get detailed analysis with multiple principal variations
            String analysis = stockfishManager.getDetailedAnalysis(2000); // 2 seconds

            // Parse the analysis to extract moves and scores
            candidates = parseStockfishAnalysis(analysis);

            // If parsing failed, get at least the best move
            if (candidates.isEmpty()) {
                String bestMove = stockfishManager.getBestMove(1000);
                if (bestMove != null && !bestMove.isEmpty()) {
                    candidates.add(new PersonalityMove(bestMove, 0.0f, 0.0f, "Engine's top choice", false));
                }
            }

            Log.d(TAG, "🔍 Parsed " + candidates.size() + " moves from engine analysis");

        } catch (Exception e) {
            Log.e(TAG, "Error getting engine candidates", e);
        }

        return candidates;
    }

    /**
     * Parse Stockfish analysis output to extract moves and evaluations
     */
    private List<PersonalityMove> parseStockfishAnalysis(String analysis) {
        List<PersonalityMove> moves = new ArrayList<>();

        try {
            String[] lines = analysis.split("\n");

            for (String line : lines) {
                if (line.contains("info depth") && line.contains("score") && line.contains("pv")) {
                    try {
                        // Extract score
                        float score = 0.0f;
                        if (line.contains("score cp")) {
                            int cpIndex = line.indexOf("score cp") + 8;
                            int nextSpace = line.indexOf(" ", cpIndex);
                            if (nextSpace > cpIndex) {
                                int centipawns = Integer.parseInt(line.substring(cpIndex, nextSpace).trim());
                                score = centipawns / 100.0f; // Convert to pawns
                            }
                        } else if (line.contains("score mate")) {
                            // Handle mate scores
                            score = 10.0f; // High value for mate
                        }

                        // Extract first move from principal variation
                        int pvIndex = line.indexOf("pv ") + 3;
                        if (pvIndex > 2) {
                            String pvSection = line.substring(pvIndex);
                            String[] pvMoves = pvSection.split(" ");
                            if (pvMoves.length > 0 && !pvMoves[0].isEmpty()) {
                                String move = pvMoves[0].trim();
                                if (move.length() >= 4) {
                                    moves.add(new PersonalityMove(move, score, 0.0f, "Stockfish analysis", false));
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.w(TAG, "Error parsing analysis line: " + line, e);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing Stockfish analysis", e);
        }

        // Remove duplicates and sort by score
        Map<String, PersonalityMove> uniqueMoves = new HashMap<>();
        for (PersonalityMove move : moves) {
            if (!uniqueMoves.containsKey(move.move) ||
                    uniqueMoves.get(move.move).engineScore < move.engineScore) {
                uniqueMoves.put(move.move, move);
            }
        }

        List<PersonalityMove> result = new ArrayList<>(uniqueMoves.values());
        Collections.sort(result, (a, b) -> Float.compare(b.engineScore, a.engineScore));

        // Limit to top candidates
        return result.subList(0, Math.min(STOCKFISH_CANDIDATES, result.size()));
    }

    /**
     * THE HEART OF THE INNOVATION: Find similar positions and boost historical moves! 🎯
     */
    private void findSimilarPositions(String currentFen, List<PersonalityMove> engineCandidates,
                                      PersonalityMoveCallback callback) {

        Log.d(TAG, "🔍 Searching for similar positions in " + currentMaster + "'s games...");

        // Create search query for vector store
        String searchQuery = createFenSearchQuery(currentFen);

        // Search the vector store for similar positions
        modelManager.searchVectorStoreForPositions(searchQuery, MAX_SIMILAR_POSITIONS,
                new FineTunedModelManager.VectorSearchCallback() {
                    @Override
                    public void onSearchResults(List<FineTunedModelManager.VectorSearchResult> results) {
                        Log.d(TAG, "📚 Found " + results.size() + " similar positions");

                        // Apply personality scoring
                        List<PersonalityMove> scoredMoves = applyPersonalityScoring(engineCandidates, results);

                        // Select the best move
                        PersonalityMove selectedMove = selectBestPersonalityMove(scoredMoves);

                        // Generate explanation
                        generateMoveExplanation(selectedMove, results, callback);

                        // Return the result
                        mainHandler.post(() -> {
                            callback.onPersonalityMoveSelected(selectedMove, scoredMoves);
                        });
                    }

                    @Override
                    public void onSearchError(String error) {
                        Log.e(TAG, "❌ Vector search failed: " + error);

                        // Fall back to pure engine selection
                        PersonalityMove fallbackMove = engineCandidates.isEmpty() ?
                                null : engineCandidates.get(0);

                        mainHandler.post(() -> {
                            if (fallbackMove != null) {
                                callback.onPersonalityMoveSelected(fallbackMove, engineCandidates);
                            } else {
                                callback.onError("No moves available");
                            }
                        });
                    }
                });
    }

    /**
     * Create a search query optimized for finding similar chess positions
     */
    private String createFenSearchQuery(String fen) {
        // Extract key position features for better matching
        String[] fenParts = fen.split(" ");
        if (fenParts.length < 2) return fen;

        String boardPosition = fenParts[0];
        String activeColor = fenParts[1];

        // Create a rich query that will match similar tactical/positional themes
        return String.format("chess position %s %s turn tactical position strategic",
                boardPosition, activeColor.equals("w") ? "white" : "black");
    }

    /**
     * Apply personality scoring to engine candidates based on historical data
     */
    private List<PersonalityMove> applyPersonalityScoring(List<PersonalityMove> engineCandidates,
                                                          List<FineTunedModelManager.VectorSearchResult> historicalPositions) {

        List<PersonalityMove> scoredMoves = new ArrayList<>();

        for (PersonalityMove candidate : engineCandidates) {
            float personalityBonus = 0.0f;
            String historicalContext = "";
            boolean isHistoricalMatch = false;

            // Check if this move matches any historical moves
            for (FineTunedModelManager.VectorSearchResult historical : historicalPositions) {
                String historicalMove = extractMoveFromMetadata(historical.metadata);

                if (historicalMove != null && movesMatch(candidate.move, historicalMove)) {
                    // BOOST! This move matches what the master played
                    float boost = personalityWeight * (1.0f + historical.similarity);
                    personalityBonus = Math.max(personalityBonus, boost);

                    isHistoricalMatch = true;
                    historicalContext = String.format("In %s vs %s (%s): %s",
                            getCurrentMasterDisplayName(),
                            getOpponentFromMetadata(historical.metadata),
                            getYearFromMetadata(historical.metadata),
                            getAnnotationFromMetadata(historical.metadata));

                    Log.d(TAG, "🎯 HISTORICAL MATCH: " + candidate.move + " -> +" + boost + " bonus");
                    break;
                }
            }

            // Apply style bonuses based on position characteristics
            if (!isHistoricalMatch) {
                personalityBonus = calculateStyleBonus(candidate, historicalPositions);
                historicalContext = "Matches " + getCurrentMasterDisplayName() + "'s playing style";
            }

            PersonalityMove scoredMove = new PersonalityMove(
                    candidate.move,
                    candidate.engineScore,
                    personalityBonus,
                    historicalContext,
                    isHistoricalMatch
            );

            scoredMoves.add(scoredMove);
        }

        // Sort by final score (engine + personality)
        Collections.sort(scoredMoves, (a, b) -> Float.compare(b.finalScore, a.finalScore));

        return scoredMoves;
    }

    /**
     * Calculate style bonus for moves that match the master's general approach
     */
    private float calculateStyleBonus(PersonalityMove candidate, List<FineTunedModelManager.VectorSearchResult> historicalPositions) {
        float styleBonus = 0.0f;

        // Analyze the historical positions to understand the master's preferences
        Map<String, Integer> tagCounts = new HashMap<>();
        for (FineTunedModelManager.VectorSearchResult result : historicalPositions) {
            List<String> tags = getTagsFromMetadata(result.metadata);
            for (String tag : tags) {
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
            }
        }

        // Apply master-specific style bonuses
        switch (currentMaster.toLowerCase()) {
            case "tal":
                // Tal loves sacrifices and attacks
                if (tagCounts.getOrDefault("sacrifice", 0) > 0 ||
                        tagCounts.getOrDefault("attack", 0) > 0) {
                    styleBonus += personalityWeight * 0.5f;
                }
                break;

            case "fischer":
                // Fischer prefers precise, principled moves
                if (tagCounts.getOrDefault("positional", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f;
                }
                break;

            case "kasparov":
                // Kasparov likes initiative and dynamic play
                if (tagCounts.getOrDefault("initiative", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f;
                }
                break;
        }

        return styleBonus;
    }

    /**
     * Select the best move considering both engine strength and personality
     */
    private PersonalityMove selectBestPersonalityMove(List<PersonalityMove> candidates) {
        if (candidates.isEmpty()) {
            Log.e(TAG, "No move candidates available!");
            return null;
        }

        // Filter out moves that are too weak
        List<PersonalityMove> viableMoves = new ArrayList<>();
        for (PersonalityMove move : candidates) {
            if (move.engineScore >= MIN_ENGINE_THRESHOLD) {
                viableMoves.add(move);
            }
        }

        if (viableMoves.isEmpty()) {
            Log.w(TAG, "All moves below threshold, using best available");
            viableMoves = candidates;
        }

        // Return the highest scoring viable move
        PersonalityMove selected = viableMoves.get(0);

        Log.d(TAG, "🎭 SELECTED MOVE: " + selected);
        Log.d(TAG, "🎯 Reasoning: " + selected.historicalContext);

        return selected;
    }

    /**
     * Generate an explanation for why this move was chosen
     */
    private void generateMoveExplanation(PersonalityMove selectedMove,
                                         List<FineTunedModelManager.VectorSearchResult> historicalContext,
                                         PersonalityMoveCallback callback) {

        if (selectedMove == null) return;

        executorService.execute(() -> {
            try {
                String explanation = createMoveExplanation(selectedMove, historicalContext);
                String masterQuote = generateMasterQuote(selectedMove);

                mainHandler.post(() -> {
                    callback.onPersonalityAnalysisComplete(explanation, masterQuote);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error generating explanation", e);
            }
        });
    }

    /**
     * Create a detailed explanation of the move choice
     */
    private String createMoveExplanation(PersonalityMove selectedMove,
                                         List<FineTunedModelManager.VectorSearchResult> historicalContext) {

        StringBuilder explanation = new StringBuilder();

        explanation.append(String.format("I chose %s ", selectedMove.move));

        if (selectedMove.isHistoricalMatch) {
            explanation.append("because this is exactly the type of move I would play! ");
            explanation.append(selectedMove.historicalContext);
        } else {
            explanation.append(String.format("as it scores %.2f on the engine evaluation ", selectedMove.engineScore));
            if (selectedMove.personalityBonus > 0) {
                explanation.append(String.format("plus %.2f for matching my playing style. ", selectedMove.personalityBonus));
            }
            explanation.append(selectedMove.historicalContext);
        }

        return explanation.toString();
    }

    /**
     * Generate a quote in the master's voice about the move
     */
    private String generateMasterQuote(PersonalityMove selectedMove) {
        switch (currentMaster.toLowerCase()) {
            case "tal":
                return selectedMove.isHistoricalMatch ?
                        "Ah yes, I remember playing this exact move! It has that special spark." :
                        "This move has the kind of dynamic potential I always look for.";

            case "fischer":
                return selectedMove.isHistoricalMatch ?
                        "This is the objectively correct move - I've proven it before." :
                        "After careful calculation, this is clearly the strongest continuation.";

            case "kasparov":
                return selectedMove.isHistoricalMatch ?
                        "I played this move to seize the initiative - it's pure Kasparov!" :
                        "This move maximizes our chances and keeps the tension high.";

            default:
                return "This move aligns with my chess philosophy and style.";
        }
    }

    /**
     * Helper methods for metadata extraction
     */
    private String extractMoveFromMetadata(String metadata) {
        try {
            JSONObject json = new JSONObject(metadata);
            // Look for move information in the JSON
            // This depends on your vector store structure
            return json.optString("move", "");
        } catch (Exception e) {
            return "";
        }
    }

    private String getOpponentFromMetadata(String metadata) {
        try {
            JSONObject json = new JSONObject(metadata);
            return json.optString("opponent", "");
        } catch (Exception e) {
            return "";
        }
    }

    private String getYearFromMetadata(String metadata) {
        try {
            JSONObject json = new JSONObject(metadata);
            return json.optString("year", "");
        } catch (Exception e) {
            return "";
        }
    }

    private String getAnnotationFromMetadata(String metadata) {
        try {
            JSONObject json = new JSONObject(metadata);
            return json.optString("annotation", "");
        } catch (Exception e) {
            return "";
        }
    }

    private List<String> getTagsFromMetadata(String metadata) {
        try {
            JSONObject json = new JSONObject(metadata);
            JSONArray tagsArray = json.optJSONArray("tags");
            List<String> tags = new ArrayList<>();
            if (tagsArray != null) {
                for (int i = 0; i < tagsArray.length(); i++) {
                    tags.add(tagsArray.getString(i));
                }
            }
            return tags;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Check if two moves are the same (handles different notations)
     */
    private boolean movesMatch(String move1, String move2) {
        if (move1 == null || move2 == null) return false;

        // Simple exact match for now - could be enhanced for different notations
        return move1.equals(move2);
    }

    /**
     * Fall back to pure engine move selection
     */
    private void selectPureEngineMove(String currentFen, PersonalityMoveCallback callback) {
        executorService.execute(() -> {
            try {
                stockfishManager.setPosition(currentFen);
                String bestMove = stockfishManager.getBestMove(1000);

                PersonalityMove engineMove = new PersonalityMove(
                        bestMove, 0.0f, 0.0f, "Pure engine analysis", false);

                List<PersonalityMove> candidates = new ArrayList<>();
                candidates.add(engineMove);

                mainHandler.post(() -> {
                    callback.onPersonalityMoveSelected(engineMove, candidates);
                    callback.onPersonalityAnalysisComplete(
                            "Playing the engine's top choice with full strength.",
                            "Sometimes the computer knows best.");
                });

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Engine analysis failed: " + e.getMessage()));
            }
        });
    }

    // Configuration methods
    public void setPersonalityWeight(float weight) {
        this.personalityWeight = Math.max(0.0f, Math.min(1.0f, weight));
        Log.d(TAG, "🎚️ Personality weight set to: " + this.personalityWeight);
    }

    public void setCurrentMaster(String master) {
        this.currentMaster = master.toLowerCase();
        Log.d(TAG, "🎭 Current master set to: " + getCurrentMasterDisplayName());
    }

    public void setPersonalityPlayEnabled(boolean enabled) {
        this.enablePersonalityPlay = enabled;
        Log.d(TAG, "🎭 Personality play " + (enabled ? "ENABLED" : "DISABLED"));
    }

    private String getCurrentMasterDisplayName() {
        return modelManager.getMasterDisplayName(currentMaster);
    }

    // Getters
    public float getPersonalityWeight() { return personalityWeight; }
    public String getCurrentMaster() { return currentMaster; }
    public boolean isPersonalityPlayEnabled() { return enablePersonalityPlay; }
}