package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LIGHTNING-FAST PersonalityEngine - Now with instant local database lookups!
 *
 * This revolutionary engine makes Stockfish play like chess legends using
 * blazing-fast local FEN lookups instead of slow API calls.
 *
 * Ben's vision brought to life with ZERO network delays! 🚀♟️
 */
public class PersonalityEngine {
    private static final String TAG = "PersonalityEngine";

    // Personality tuning parameters
    private static final float DEFAULT_PERSONALITY_WEIGHT = 0.3f;
    private static final float MIN_ENGINE_THRESHOLD = -1.0f;
    private static final int MAX_SIMILAR_POSITIONS = 5;
    private static final int STOCKFISH_CANDIDATES = 8;

    private static PersonalityEngine instance;

    private static Map<String, String[]> MASTER_NAME_CACHE = new HashMap<>();
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;

    // Core services
    private final StockfishManager stockfishManager;
    private final GameDatabaseHelper databaseHelper; // 🚀 Now using your enhanced database!
    private final FENCommentaryHooks commentaryHooks; // NEW: FEN-driven commentary system

    // Personality settings
    private float personalityWeight = DEFAULT_PERSONALITY_WEIGHT;
    private String currentMaster = "tal";
    private boolean enablePersonalityPlay = true;

    static {
        MASTER_NAME_CACHE.put("tal", new String[]{"Mikhail Tal", "tal", "Tal"});
        MASTER_NAME_CACHE.put("fischer", new String[]{"Bobby Fischer", "fischer", "Fischer"});
        MASTER_NAME_CACHE.put("carlsen", new String[]{"Magnus Carlsen", "carlsen", "Carlsen"});
        MASTER_NAME_CACHE.put("kasparov", new String[]{"Garry Kasparov", "kasparov", "Kasparov"});
        MASTER_NAME_CACHE.put("alekhine", new String[]{"Alexander Alekhine", "alekhine", "Alekhine"});
        MASTER_NAME_CACHE.put("karpov", new String[]{"Anatoly Karpov", "karpov", "Karpov"});
        MASTER_NAME_CACHE.put("kramnik", new String[]{"Vladimir Kramnik", "kramnik", "Kramnik"});
        MASTER_NAME_CACHE.put("capablanca", new String[]{"José Raúl Capablanca", "capablanca", "Capablanca"});
        MASTER_NAME_CACHE.put("lasker", new String[]{"Emanuel Lasker", "lasker", "Lasker"});
        MASTER_NAME_CACHE.put("morphy", new String[]{"Paul Morphy", "morphy", "Morphy"});
        MASTER_NAME_CACHE.put("anand", new String[]{"Viswanathan Anand", "anand", "Anand"});
        MASTER_NAME_CACHE.put("botvinnik", new String[]{"Mikhail Botvinnik", "botvinnik", "Botvinnik"});
    }

    /**
     * Move candidate with both engine and personality scoring
     */
    public static class PersonalityMove {
        public final String move;
        public final float engineScore;
        public final float personalityBonus;
        public final float finalScore;
        public final String historicalContext;
        public final boolean isHistoricalMatch;

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

    /**
     * ENHANCED: Vector search result class for compatibility
     */
    public static class VectorSearchResult {
        public final String content;
        public final String metadata;
        public final float similarity;

        public VectorSearchResult(String content, String metadata, float similarity) {
            this.content = content;
            this.metadata = metadata;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return String.format("VectorResult(similarity=%.3f, content=%s)",
                    similarity, content.substring(0, Math.min(50, content.length())) + "...");
        }
    }

    private PersonalityEngine(Context context, StockfishManager stockfishManager) {
        Log.d(TAG, "🔧 PersonalityEngine constructor starting...");
        
        this.context = context.getApplicationContext();
        Log.d(TAG, "✅ Context initialized");
        
        this.mainHandler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "✅ MainHandler initialized");
        
        this.executorService = Executors.newCachedThreadPool();
        Log.d(TAG, "✅ ExecutorService initialized");
        
        this.stockfishManager = stockfishManager;
        Log.d(TAG, "✅ StockfishManager assigned");

        // 🚀 Initialize with your enhanced database helper!
        Log.d(TAG, "🗄️ Initializing GameDatabaseHelper...");
        this.databaseHelper = new GameDatabaseHelper(context);
        Log.d(TAG, "✅ GameDatabaseHelper initialized");
        
        // Initialize FEN commentary hooks
        Log.d(TAG, "🎯 Initializing FEN Commentary Hooks...");
        this.commentaryHooks = new FENCommentaryHooks(context);
        Log.d(TAG, "✅ FEN Commentary Hooks initialized");

        // 🚨 CRITICAL FIX: Force database initialization for common masters at startup
        Log.d(TAG, "🔧 STARTUP: Forcing database initialization for key masters...");
        String[] keyMasters = {"tal", "fischer", "carlsen", "kasparov", "karpov", "kramnik"};
        for (String master : keyMasters) {
            if (!databaseHelper.hasMasterData(master)) {
                Log.d(TAG, "🔧 STARTUP: No data for " + master + " - forcing initialization");
                initializeMasterData(master);
            } else {
                int count = databaseHelper.getMasterPositionCount(master);
                Log.d(TAG, "🔧 STARTUP: " + master + " already has " + count + " positions");
            }
        }

        Log.d(TAG, "🎭 PersonalityEngine initialized with LIGHTNING-FAST local database!");
    }

    public static synchronized PersonalityEngine getInstance(Context context, StockfishManager stockfishManager) {
        Log.d(TAG, "🎯 ENTRY: PersonalityEngine.getInstance called");
        
        if (instance == null) {
            Log.d(TAG, "🎭 Creating new PersonalityEngine instance...");
            instance = new PersonalityEngine(context, stockfishManager);
            Log.d(TAG, "✅ PersonalityEngine instance created successfully");
        } else {
            Log.d(TAG, "♻️ Returning existing PersonalityEngine instance");
        }
        
        return instance;
    }

    /**
     * 🚀 THE CORE METHOD: Select a move with INSTANT local database lookups!
     * No more network delays - everything happens locally at lightning speed!
     */
    public void selectPersonalityMove(String currentFen, PersonalityMoveCallback callback) {
        Log.d(TAG, "⚡ LIGHTNING-FAST personality move selection for " + currentMaster);
        Log.d(TAG, "🔧 DEBUG: enablePersonalityPlay=" + enablePersonalityPlay + ", currentMaster=" + currentMaster);
        Log.d(TAG, "🔧 DEBUG: databaseHelper=" + (databaseHelper != null ? "initialized" : "NULL"));
        Log.d(TAG, "🔧 DEBUG: executorService=" + (executorService != null && !executorService.isShutdown() ? "healthy" : "NOT HEALTHY"));

        // 🚨 CRITICAL DEBUG: Check database status immediately
        if (databaseHelper != null) {
            boolean hasData = databaseHelper.hasMasterData(currentMaster);
            int positionCount = databaseHelper.getMasterPositionCount(currentMaster);
            Log.d(TAG, "🔍 IMMEDIATE DB CHECK: " + currentMaster + " hasData=" + hasData + ", positions=" + positionCount);
            
            if (!hasData) {
                Log.w(TAG, "⚠️ NO DATABASE DATA for " + currentMaster + " - forcing immediate initialization");
                initializeMasterData(currentMaster);
                // Continue anyway - the lookup will use fallback logic
            }
        }

        if (!enablePersonalityPlay) {
            Log.d(TAG, "🚫 Personality play DISABLED - falling back to pure engine");
            selectPureEngineMove(currentFen, callback);
            return;
        }

        executorService.execute(() -> {
            try {
                // Step 1: Get top move candidates from Stockfish
                Log.d(TAG, "🤖 Getting engine candidates for FEN: " + currentFen.substring(0, Math.min(50, currentFen.length())));
                List<PersonalityMove> engineCandidates = getEngineCandidates(currentFen);
                Log.d(TAG, "🤖 Got " + engineCandidates.size() + " engine candidates");

                // Step 2: INSTANT local database lookup - no network delays!
                Log.d(TAG, "⚡ Starting database lookup for " + currentMaster);
                findSimilarPositionsLocally(currentFen, engineCandidates, callback);

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
            stockfishManager.setPosition(currentFen);
            String analysis = stockfishManager.getDetailedAnalysis(2000);
            candidates = parseStockfishAnalysis(analysis);

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
                        float score = 0.0f;
                        if (line.contains("score cp")) {
                            int cpIndex = line.indexOf("score cp") + 8;
                            int nextSpace = line.indexOf(" ", cpIndex);
                            if (nextSpace > cpIndex) {
                                int centipawns = Integer.parseInt(line.substring(cpIndex, nextSpace).trim());
                                score = centipawns / 100.0f;
                            }
                        } else if (line.contains("score mate")) {
                            score = 10.0f;
                        }

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

        Map<String, PersonalityMove> uniqueMoves = new HashMap<>();
        for (PersonalityMove move : moves) {
            if (!uniqueMoves.containsKey(move.move) ||
                    uniqueMoves.get(move.move).engineScore < move.engineScore) {
                uniqueMoves.put(move.move, move);
            }
        }

        List<PersonalityMove> result = new ArrayList<>(uniqueMoves.values());
        Collections.sort(result, (a, b) -> Float.compare(b.engineScore, a.engineScore));

        return result.subList(0, Math.min(STOCKFISH_CANDIDATES, result.size()));
    }

    private void findSimilarPositionsLocally(String currentFen, List<PersonalityMove> engineCandidates,
                                             PersonalityMoveCallback callback) {

        Log.d(TAG, "⚡ OPTIMIZED local database lookup for " + currentMaster + "...");
        Log.d(TAG, "🔧 DEBUG: About to call databaseHelper.findSimilarPositions() with FEN: " + currentFen.substring(0, Math.min(50, currentFen.length())));

        try {
            // Get cached name variations (most likely first)
            String[] masterVariations = MASTER_NAME_CACHE.getOrDefault(currentMaster.toLowerCase(),
                    new String[]{currentMaster});

            List<GameDatabaseHelper.HistoricalPosition> historicalPositions = new ArrayList<>();

            // Try most successful name first
            for (String nameVariation : masterVariations) {
                Log.d(TAG, "🔧 DEBUG: Trying master name variation: '" + nameVariation + "'");
                
                // 🚨 CRITICAL DEBUG: Check database state before each call
                if (databaseHelper == null) {
                    Log.e(TAG, "❌ CRITICAL: databaseHelper is NULL during lookup!");
                    break;
                }
                
                Log.d(TAG, "📊 PRE-LOOKUP: Database stats for " + nameVariation);
                boolean hasDataPreLookup = databaseHelper.hasMasterData(nameVariation);
                int countPreLookup = databaseHelper.getMasterPositionCount(nameVariation);
                Log.d(TAG, "📊 PRE-LOOKUP: hasData=" + hasDataPreLookup + ", count=" + countPreLookup);
                
                List<GameDatabaseHelper.HistoricalPosition> results =
                        databaseHelper.findSimilarPositions(currentFen, nameVariation, MAX_SIMILAR_POSITIONS);
                Log.d(TAG, "🔧 DEBUG: Database returned " + results.size() + " results for '" + nameVariation + "'");

                if (!results.isEmpty()) {
                    historicalPositions = results;
                    Log.d(TAG, "✅ OPTIMIZED SUCCESS with: '" + nameVariation + "' (" + results.size() + " positions)");
                    break; // Stop on first success
                } else {
                    Log.d(TAG, "🔧 DEBUG: No results for '" + nameVariation + "', trying next variation...");
                }
            }

            Log.d(TAG, "✅ FINAL lookup result: Found " + historicalPositions.size() + " similar positions");

            // Rest of your existing logic...
            List<VectorSearchResult> compatibleResults = new ArrayList<>();
            for (GameDatabaseHelper.HistoricalPosition pos : historicalPositions) {
                compatibleResults.add(pos.toVectorSearchResult());
            }

            // Continue with existing logic...
            List<PersonalityMove> scoredMoves = applyPersonalityScoring(engineCandidates, compatibleResults);
            PersonalityMove selectedMove = selectBestPersonalityMove(scoredMoves);
            generateMoveExplanation(selectedMove, compatibleResults, callback);
            
            // NEW: Trigger FEN-driven commentary hooks
            triggerFENCommentaryHooks(currentFen, selectedMove.move, 
                currentMaster, 0.0, 0.0); // TODO: Add evaluation parameters

            mainHandler.post(() -> {
                callback.onPersonalityMoveSelected(selectedMove, scoredMoves);
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in local database lookup", e);
            // Fallback to pure engine move
            Log.d(TAG, "🔄 Falling back to pure engine move due to database error");
            selectPureEngineMove(currentFen, callback);
        }
    }

    // Add these helper methods to PersonalityEngine.java
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String getFullMasterName(String shortName) {
        switch (shortName.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "fischer": return "Bobby Fischer";
            case "kasparov": return "Garry Kasparov";
            case "kramnik": return "Vladimir Kramnik";
            case "karpov": return "Anatoly Karpov";
            default: return shortName;
        }
    }

    // Add this helper method to PersonalityEngine.java
    private String convertUciToAlgebraic(String uciMove) {
        if (uciMove == null || uciMove.length() < 4) return uciMove;

        try {
            // Basic UCI to algebraic conversion for common moves
            String from = uciMove.substring(0, 2);
            String to = uciMove.substring(2, 4);

            // For simple pawn moves like d2d4 -> d4
            char fromFile = from.charAt(0);
            char fromRank = from.charAt(1);
            char toFile = to.charAt(0);
            char toRank = to.charAt(1);

            // Simple pawn move (same file, no capture)
            if (fromFile == toFile) {
                return String.valueOf(toFile) + String.valueOf(toRank);
            }

            // For more complex moves, we'd need piece type detection
            // For now, return the destination square
            return String.valueOf(toFile) + String.valueOf(toRank);

        } catch (Exception e) {
            Log.w(TAG, "Move conversion failed for: " + uciMove);
            return uciMove;
        }
    }

    // Also add the reverse conversion
    private String convertAlgebraicToUci(String algebraicMove) {
        // This is a simplified version - you might want to enhance it
        if (algebraicMove == null || algebraicMove.length() < 2) return algebraicMove;

        // For moves like "d4", we can't fully convert without board context
        // But we can try to match destination squares
        return algebraicMove;
    }

    /**
     * Apply personality scoring to engine candidates based on local historical data
     */
    private List<PersonalityMove> applyPersonalityScoring(List<PersonalityMove> engineCandidates,
                                                          List<VectorSearchResult> historicalPositions) {

        List<PersonalityMove> scoredMoves = new ArrayList<>();

        for (PersonalityMove candidate : engineCandidates) {
            float personalityBonus = 0.0f;
            String historicalContext = "";
            boolean isHistoricalMatch = false;

            // Check if this move matches any historical moves
            for (VectorSearchResult historical : historicalPositions) {
                String historicalMove = extractMoveFromMetadata(historical.metadata);

                if (historicalMove != null && movesMatch(candidate.move, historicalMove)) {
                    // BOOST! This move matches what the master played
                    float boost = personalityWeight * (1.0f + historical.similarity);
                    personalityBonus = Math.max(personalityBonus, boost);

                    isHistoricalMatch = true;
                    historicalContext = String.format("Historical match from %s's games: %s",
                            getCurrentMasterDisplayName(), historical.content);

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

        Collections.sort(scoredMoves, (a, b) -> Float.compare(b.finalScore, a.finalScore));

        return scoredMoves;
    }

    /**
     * Calculate style bonus for moves that match the master's general approach
     */
    private float calculateStyleBonus(PersonalityMove candidate, List<VectorSearchResult> historicalPositions) {
        float styleBonus = 0.0f;

        // Analyze the historical positions to understand the master's preferences
        Map<String, Integer> tagCounts = new HashMap<>();
        for (VectorSearchResult result : historicalPositions) {
            List<String> tags = getTagsFromMetadata(result.metadata);
            for (String tag : tags) {
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
            }
        }

        // Apply master-specific style bonuses
        switch (currentMaster.toLowerCase()) {
            case "tal":
                if (tagCounts.getOrDefault("sacrifice", 0) > 0 ||
                        tagCounts.getOrDefault("attack", 0) > 0) {
                    styleBonus += personalityWeight * 0.5f;
                }
                break;

            case "fischer":
                if (tagCounts.getOrDefault("positional", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f;
                }
                break;

            case "kasparov":
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

        PersonalityMove selected = viableMoves.get(0);

        Log.d(TAG, "🎭 SELECTED MOVE: " + selected);
        Log.d(TAG, "🎯 Reasoning: " + selected.historicalContext);

        return selected;
    }

    /**
     * Generate an explanation for why this move was chosen
     */
    private void generateMoveExplanation(PersonalityMove selectedMove,
                                         List<VectorSearchResult> historicalContext,
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
                                         List<VectorSearchResult> historicalContext) {

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



    private List<String> getTagsFromMetadata(String metadata) {
        List<String> tags = new ArrayList<>();

        // Extract tactical themes from the metadata string
        String[] commonTags = {"sacrifice", "attack", "defense", "endgame", "positional", "initiative"};

        for (String tag : commonTags) {
            if (metadata.toLowerCase().contains(tag)) {
                tags.add(tag);
            }
        }

        return tags;
    }

    private String extractMoveFromMetadata(String metadata) {
        try {
            String[] words = metadata.split("\\s+");
            for (String word : words) {
                // Look for UCI format moves (e2e4, g1f3, etc.)
                if (word.matches("[a-h][1-8][a-h][1-8]")) {
                    return word;
                }
                // Look for algebraic notation
                if (word.matches("[KQRBN]?[a-h]?[1-8]?x?[a-h][1-8][+#]?")) {
                    return word;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error extracting move from metadata: " + metadata);
        }
        return null;
    }

    // Enhanced move matching that tries both notations
    private boolean movesMatch(String move1, String move2) {
        if (move1 == null || move2 == null) return false;

        // Direct match
        if (move1.equals(move2)) return true;

        // Try converting UCI to algebraic and compare
        String algebraic1 = convertUciToAlgebraic(move1);
        String algebraic2 = convertUciToAlgebraic(move2);

        if (algebraic1.equals(move2) || algebraic2.equals(move1)) {
            Log.d(TAG, "🎯 MOVE MATCH via conversion: " + move1 + " matches " + move2);
            return true;
        }

        return false;
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

        // Check if we have data for this master
        boolean hasData = databaseHelper.hasMasterData(master.toLowerCase());
        if (!hasData) {
            Log.w(TAG, "⚠️ No local data found for " + master + " - AUTOMATICALLY IMPORTING!");
            // 🔧 FIX: Automatically initialize master data when it's missing
            initializeMasterData(master.toLowerCase());
        }

        Log.d(TAG, "🎭 Current master set to: " + getCurrentMasterDisplayName() +
                (hasData ? " (data available)" : " (importing...)"));
    }

    public void setPersonalityPlayEnabled(boolean enabled) {
        this.enablePersonalityPlay = enabled;
        Log.d(TAG, "🎭 Personality play " + (enabled ? "ENABLED" : "DISABLED"));
    }

    private String getCurrentMasterDisplayName() {
        // Simple display name mapping
        switch (currentMaster.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "fischer": return "Bobby Fischer";
            case "kasparov": return "Garry Kasparov";
            case "kramnik": return "Vladimir Kramnik";
            case "karpov": return "Anatoly Karpov";
            default: return currentMaster;
        }
    }

    // Getters
    public float getPersonalityWeight() { return personalityWeight; }
    public String getCurrentMaster() { return currentMaster; }
    public boolean isPersonalityPlayEnabled() { return enablePersonalityPlay; }

    /**
     * 🚀 NEW: Check database status and import data if needed
     */
    public void initializeMasterData(String masterName) {
        // 🚨 CRITICAL FIX: Add immediate logging to verify method is called
        Log.d(TAG, "🎯 ENTRY: initializeMasterData called for master: " + masterName);
        
        // 🚨 CRITICAL FIX: Check executor service state
        if (executorService == null) {
            Log.e(TAG, "❌ CRITICAL: ExecutorService is NULL! Cannot initialize master data.");
            return;
        }
        
        if (executorService.isShutdown()) {
            Log.e(TAG, "❌ CRITICAL: ExecutorService is SHUT DOWN! Cannot initialize master data.");
            return;
        }
        
        Log.d(TAG, "✅ ExecutorService is healthy, submitting background task...");
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🔍 BACKGROUND TASK STARTED: Initializing master data for: " + masterName);
                
                // 🚨 CRITICAL FIX: Check database helper state
                if (databaseHelper == null) {
                    Log.e(TAG, "❌ CRITICAL: DatabaseHelper is NULL! Cannot proceed.");
                    return;
                }
                
                Log.d(TAG, "✅ DatabaseHelper is healthy, checking for existing data...");
                
                boolean hasData = databaseHelper.hasMasterData(masterName);
                Log.d(TAG, "📊 Has existing data for " + masterName + ": " + hasData);

                if (!hasData) {
                    Log.d(TAG, "📥 No data for " + masterName + " - attempting to import from assets...");

                    // Use consistent filename mapping for *surname*_full_positions.json convention
                    String filename = getMasterPositionFilename(masterName);
                    Log.d(TAG, "📁 Looking for file: " + filename);
                    
                    boolean imported = databaseHelper.importMasterPositionsFromAssets(filename);

                    if (imported) {
                        Log.d(TAG, "✅ Successfully imported data for " + masterName + " from " + filename);
                        
                        // Verify the import worked
                        int positionCount = databaseHelper.getMasterPositionCount(masterName);
                        Log.d(TAG, "📊 Imported " + positionCount + " positions for " + masterName);
                    } else {
                        Log.e(TAG, "❌ FAILED to import data for " + masterName + " from " + filename);
                        
                        // Run database diagnostics to help debug
                        String diagnostics = databaseHelper.getDatabaseDiagnostics();
                        Log.e(TAG, "🔬 Database diagnostics: " + diagnostics);
                    }
                } else {
                    int positionCount = databaseHelper.getMasterPositionCount(masterName);
                    Log.d(TAG, "✅ Found existing data for " + masterName + ": " + positionCount + " positions");
                }

                // Log current database stats
                Map<String, Integer> stats = databaseHelper.getDatabaseStats();
                Log.d(TAG, "📊 Final database stats: " + stats.toString());
                
                Log.d(TAG, "🎯 BACKGROUND TASK COMPLETED for master: " + masterName);

            } catch (Exception e) {
                Log.e(TAG, "❌ Exception initializing master data for " + masterName, e);
            }
        });
        
        Log.d(TAG, "🎯 EXIT: initializeMasterData method completed (background task submitted)");
    }

    /**
     * 🚨 Force stop all PersonalityEngine operations for ANR prevention
     */
    public void forceStop() {
        Log.d(TAG, "🚨 PersonalityEngine FORCE STOP initiated");
        
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdownNow();
                Log.d(TAG, "✅ PersonalityEngine executor shut down");
            }
            
            if (databaseHelper != null) {
                databaseHelper.close();
                Log.d(TAG, "✅ PersonalityEngine database closed");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error during PersonalityEngine force stop", e);
        }
    }
    
    /**
     * 🎯 Get the correct filename for master position data
     * Maps master names to the consistent *surname*_full_positions.json convention
     */
    private String getMasterPositionFilename(String masterName) {
        switch (masterName.toLowerCase()) {
            case "alekhine":
                return "alekhine_full_positions.json";
            case "capablanca":
                return "capablanca_full_positions.json";
            case "anand":
                return "anand_full_positions.json";
            case "kramnik":
                return "kramnik_full_positions.json";
            case "carlsen":
                return "carlsen_full_positions.json";
            case "fischer":
                return "fischer_full_positions.json";
            case "tal":
                return "tal_full_positions.json";
            case "kasparov":
                return "kasparov_full_positions.json";
            case "karpov":
                return "karpov_full_positions.json";
            case "lasker":
                return "lasker_full_positions.json";
            case "morphy":
                return "morphy_full_positions.json";
            case "botvinnik":
                return "botvinnik_full_positions.json";
            default:
                // Fallback to old naming convention
                return masterName.toLowerCase() + "_positions.json";
        }
    }
    
    /**
     * NEW: Trigger FEN-driven commentary hooks
     */
    private void triggerFENCommentaryHooks(String currentFEN, String lastMove, 
                                         String masterName, double currentEval, double previousEval) {
        LogThrottler.d(TAG, "🎯 Triggering FEN commentary hooks for position analysis");
        
        if (commentaryHooks != null) {
            try {
                commentaryHooks.analyzePosition(currentFEN, lastMove, masterName, 
                                              currentEval, previousEval);
            } catch (Exception e) {
                LogThrottler.e(TAG, "Error in FEN commentary hooks", e);
            }
        }
    }
    
    /**
     * Set callback for FEN commentary events
     */
    public void setCommentaryCallback(FENCommentaryHooks.CommentaryCallback callback) {
        if (commentaryHooks != null) {
            commentaryHooks.setCommentaryCallback(callback);
        }
    }
}