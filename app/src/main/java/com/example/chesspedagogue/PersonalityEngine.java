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
    private static final float DEFAULT_PERSONALITY_WEIGHT = 0.15f; // Reduced from 0.3 to let engine strength dominate more
    private static final float MIN_ENGINE_THRESHOLD = -0.5f; // Tighter threshold for personality influence
    private static final int MAX_SIMILAR_POSITIONS = 50;
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
    private final AIStyleAdvisor aiStyleAdvisor; // 🧠 NEW: AI-enhanced personality system

    // Personality settings
    private float personalityWeight = DEFAULT_PERSONALITY_WEIGHT;
    private String currentMaster = "tal";
    private boolean enablePersonalityPlay = true;
    
    // Current game state for AI analysis
    private String currentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    static {
        MASTER_NAME_CACHE.put("tal", new String[]{"tal", "Mikhail Tal", "Tal"});
        MASTER_NAME_CACHE.put("fischer", new String[]{"fischer", "Bobby Fischer", "Fischer"});
        MASTER_NAME_CACHE.put("carlsen", new String[]{"carlsen", "Magnus Carlsen", "Carlsen"});
        MASTER_NAME_CACHE.put("kasparov", new String[]{"kasparov", "Garry Kasparov", "Kasparov"});
        MASTER_NAME_CACHE.put("alekhine", new String[]{"alekhine", "Alexander Alekhine", "Alekhine"});
        MASTER_NAME_CACHE.put("karpov", new String[]{"karpov", "Anatoly Karpov", "Karpov"});
        MASTER_NAME_CACHE.put("kramnik", new String[]{"kramnik", "Vladimir Kramnik", "Kramnik"});
        MASTER_NAME_CACHE.put("capablanca", new String[]{"capablanca", "José Raúl Capablanca", "Capablanca"});
        MASTER_NAME_CACHE.put("lasker", new String[]{"lasker", "Emanuel Lasker", "Lasker"});
        MASTER_NAME_CACHE.put("morphy", new String[]{"morphy", "Paul Morphy", "Morphy"});
        MASTER_NAME_CACHE.put("anand", new String[]{"anand", "Viswanathan Anand", "Anand"});
        MASTER_NAME_CACHE.put("botvinnik", new String[]{"botvinnik", "Mikhail Botvinnik", "Botvinnik"});
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
        
        // Initialize AI Style Advisor
        Log.d(TAG, "🧠 Initializing AI Style Advisor...");
        this.aiStyleAdvisor = AIStyleAdvisor.getInstance(context);
        Log.d(TAG, "✅ AI Style Advisor initialized");

        // 🚨 CRITICAL FIX: Force database initialization for common masters at startup
        Log.d(TAG, "🔧 STARTUP: Forcing database initialization for key masters...");
        String[] keyMasters = {"tal", "fischer", "carlsen", "kasparov", "karpov", "kramnik", "alekhine"};
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
        // Store current FEN for AI analysis
        this.currentFEN = currentFen;
        
        Log.d(TAG, "🚨 ==> ENTRY: selectPersonalityMove() called for " + currentMaster + " <=== 🚨");
        Log.d(TAG, "⚡ LIGHTNING-FAST personality move selection for " + currentMaster);
        Log.d(TAG, "🔧 DEBUG: enablePersonalityPlay=" + enablePersonalityPlay + ", currentMaster=" + currentMaster);
        Log.d(TAG, "🔧 DEBUG: databaseHelper=" + (databaseHelper != null ? "initialized" : "NULL"));
        Log.d(TAG, "🔧 DEBUG: executorService=" + (executorService != null && !executorService.isShutdown() ? "healthy" : "NOT HEALTHY"));
        
        // 🚨 FORCE VISIBLE LOG through LogThrottler for immediate debugging
        LogThrottler.force("PersonalityEngine", "🎯 PERSONALITY ENGINE ENTRY: " + currentMaster + " analyzing FEN: " + currentFen.substring(0, Math.min(50, currentFen.length())));

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
                // DIAGNOSTIC: Log complete pipeline entry
                Log.d(TAG, "🔬 === PERSONALITY ENGINE PIPELINE START ===");
                Log.d(TAG, "🎯 Position: " + currentFen);
                Log.d(TAG, "🎭 Master: " + currentMaster);
                Log.d(TAG, "⚖️ Personality Weight: " + personalityWeight);
                Log.d(TAG, "🎮 Enable Personality Play: " + enablePersonalityPlay);
                
                // Step 1: Get top move candidates from Stockfish
                Log.d(TAG, "🤖 Getting engine candidates for FEN: " + currentFen.substring(0, Math.min(50, currentFen.length())));
                List<PersonalityMove> engineCandidates = getEngineCandidates(currentFen);
                Log.d(TAG, "🤖 Got " + engineCandidates.size() + " engine candidates");
                
                // DIAGNOSTIC: Log engine candidates
                for (int i = 0; i < Math.min(3, engineCandidates.size()); i++) {
                    PersonalityMove candidate = engineCandidates.get(i);
                    Log.d(TAG, "   🤖 Candidate " + (i+1) + ": " + candidate.move + " (score: " + candidate.engineScore + ")");
                }

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
        // CRITICAL DIAGNOSTIC: Force log to verify this method is called
        Log.e(TAG, "🚨🚨🚨 CRITICAL: getEngineCandidates() METHOD ENTRY - THIS LOG MUST APPEAR! 🚨🚨🚨");
        System.out.println("🚨🚨🚨 SYSTEM.OUT: getEngineCandidates() called for FEN: " + currentFen.substring(0, Math.min(30, currentFen.length())));
        
        List<PersonalityMove> candidates = new ArrayList<>();

        try {
            // CRITICAL: Add checkpoint logging to track execution flow
            Log.e(TAG, "🔍 CHECKPOINT 1: About to configure MultiPV");
            System.out.println("🔍 CHECKPOINT 1: About to configure MultiPV");
            // CRITICAL FIX: Configure Stockfish for multiple candidates BEFORE analysis
            Log.d(TAG, String.format("🔧 Configuring Stockfish for %d candidates", STOCKFISH_CANDIDATES));
            boolean optionSet = stockfishManager.setOption("MultiPV", String.valueOf(STOCKFISH_CANDIDATES));
            Log.e(TAG, String.format("🔧 MultiPV setOption result: %s", optionSet ? "SUCCESS" : "FAILED"));
            System.out.println("🔧 MultiPV setOption result: " + (optionSet ? "SUCCESS" : "FAILED"));
            
            if (!optionSet) {
                Log.e(TAG, "❌ CRITICAL: Failed to set MultiPV option - falling back to single move");
                System.out.println("❌ CRITICAL: Failed to set MultiPV option");
            }
            
            Log.e(TAG, "🔍 CHECKPOINT 2: About to wait for ready");
            System.out.println("🔍 CHECKPOINT 2: About to wait for ready");
            
            // Wait longer for option to take effect
            boolean ready = stockfishManager.waitForReady(1000);
            Log.e(TAG, String.format("🔍 Stockfish ready after MultiPV config: %s", ready ? "YES" : "NO"));
            System.out.println("🔍 Stockfish ready: " + (ready ? "YES" : "NO"));
            
            // DEBUG: Verify MultiPV setting by testing with a simple command
            Log.d(TAG, "🔍 MultiPV configuration verification - testing engine responsiveness");
            
            Log.e(TAG, "🔍 CHECKPOINT 3: About to set position and get analysis");
            System.out.println("🔍 CHECKPOINT 3: About to set position and get analysis");
            
            stockfishManager.setPosition(currentFen);
            String analysis = stockfishManager.getDetailedAnalysis(2000);
            
            Log.e(TAG, "🔍 CHECKPOINT 4: Analysis received, length: " + analysis.length());
            System.out.println("🔍 CHECKPOINT 4: Analysis received, length: " + analysis.length());
            
            // CRITICAL DEBUG: Comprehensive analysis logging
            Log.e(TAG, "🔍 RAW STOCKFISH ANALYSIS LENGTH: " + analysis.length() + " characters");
            System.out.println("🔍 RAW STOCKFISH ANALYSIS LENGTH: " + analysis.length() + " characters");
            String[] analysisLines = analysis.split("\n");
            Log.e(TAG, "🔍 TOTAL ANALYSIS LINES: " + analysisLines.length);
            System.out.println("🔍 TOTAL ANALYSIS LINES: " + analysisLines.length);
            
            // Count relevant analysis lines with "info" and "pv"
            int relevantLines = 0;
            for (String line : analysisLines) {
                if (line.contains("info depth") && line.contains("score") && line.contains("pv")) {
                    relevantLines++;
                }
            }
            Log.e(TAG, String.format("🔍 RELEVANT ANALYSIS LINES: %d (expected: %d)", relevantLines, STOCKFISH_CANDIDATES));
            System.out.println("🔍 RELEVANT ANALYSIS LINES: " + relevantLines + " (expected: " + STOCKFISH_CANDIDATES + ")");
            
            // Show first 15 lines of analysis
            Log.e(TAG, "🔍 Raw Stockfish analysis preview:");
            System.out.println("🔍 Raw Stockfish analysis preview:");
            for (int i = 0; i < Math.min(15, analysisLines.length); i++) {
                Log.e(TAG, "  Line " + i + ": " + analysisLines[i]);
                if (i < 5) System.out.println("  Line " + i + ": " + analysisLines[i]);
            }
            
            // CRITICAL: Check if analysis contains multiple PV lines
            if (relevantLines < 2) {
                Log.e(TAG, "❌ CRITICAL: Only " + relevantLines + " relevant analysis lines found!");
                Log.e(TAG, "❌ This suggests MultiPV setting failed or Stockfish didn't respond correctly");
                Log.e(TAG, "🔧 TROUBLESHOOTING: Let's check if analysis contains any useful data");
                
                // Show ALL lines to debug what Stockfish actually returned
                for (int i = 0; i < analysisLines.length; i++) {
                    if (analysisLines[i].contains("info") || analysisLines[i].contains("depth") || 
                        analysisLines[i].contains("score") || analysisLines[i].contains("pv") ||
                        analysisLines[i].contains("bestmove")) {
                        Log.d(TAG, "🔧 DEBUG Line " + i + ": " + analysisLines[i]);
                    }
                }
            }
            
            candidates = parseStockfishAnalysis(analysis);

            Log.e(TAG, String.format("🔍 Raw analysis yielded %d candidate moves", candidates.size()));
            System.out.println("🔍 Raw analysis yielded " + candidates.size() + " candidate moves");
            
            if (candidates.isEmpty()) {
                Log.w(TAG, "⚠️ No candidates from analysis - falling back to best move");
                String bestMove = stockfishManager.getBestMove(1000);
                if (bestMove != null && !bestMove.isEmpty()) {
                    candidates.add(new PersonalityMove(bestMove, 0.0f, 0.0f, "Engine's top choice", false));
                }
            } else if (candidates.size() == 1) {
                Log.w(TAG, String.format("⚠️ Only 1 candidate found: %s (expected %d)", 
                        candidates.get(0).move, STOCKFISH_CANDIDATES));
                Log.w(TAG, "🔍 This suggests MultiPV setting didn't take effect");
                
                // FALLBACK: Try to get more moves by adjusting depth/time
                Log.w(TAG, "🔧 FALLBACK: Attempting alternative method to get more candidates");
                try {
                    // Reset MultiPV and try again with longer analysis
                    stockfishManager.setOption("MultiPV", "4"); // Try smaller number first
                    stockfishManager.waitForReady(1000);
                    String fallbackAnalysis = stockfishManager.getDetailedAnalysis(3000); // Longer time
                    List<PersonalityMove> fallbackCandidates = parseStockfishAnalysis(fallbackAnalysis);
                    
                    if (fallbackCandidates.size() > candidates.size()) {
                        Log.d(TAG, String.format("✅ Fallback worked! Got %d candidates instead of %d", 
                                fallbackCandidates.size(), candidates.size()));
                        candidates = fallbackCandidates;
                    } else {
                        Log.w(TAG, "❌ Fallback didn't help - still only " + fallbackCandidates.size() + " candidates");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "❌ Fallback attempt failed", e);
                }
            } else {
                Log.d(TAG, String.format("✅ Good! Found %d candidates from MultiPV analysis", candidates.size()));
            }

            Log.d(TAG, "✅ Final candidate count: " + candidates.size());

        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR in getEngineCandidates!", e);
            System.out.println("❌ CRITICAL ERROR in getEngineCandidates: " + e.getMessage());
            e.printStackTrace();
        }

        Log.e(TAG, "🔍 CHECKPOINT 5: Method ending, returning " + candidates.size() + " candidates");
        System.out.println("🔍 CHECKPOINT 5: Method ending, returning " + candidates.size() + " candidates");
        return candidates;
    }

    /**
     * Parse Stockfish analysis output to extract moves and evaluations
     */
    private List<PersonalityMove> parseStockfishAnalysis(String analysis) {
        List<PersonalityMove> moves = new ArrayList<>();

        Log.e(TAG, "🔍 PARSING: Starting to parse " + analysis.length() + " character analysis");
        System.out.println("🔍 PARSING: Starting analysis parsing");

        try {
            String[] lines = analysis.split("\n");
            Log.e(TAG, "🔍 PARSING: Split into " + lines.length + " lines");
            
            int relevantLineCount = 0;

            for (String line : lines) {
                if (line.contains("info depth") && line.contains("score") && line.contains("pv")) {
                    relevantLineCount++;
                    Log.e(TAG, "🔍 PARSING: Processing relevant line " + relevantLineCount + ": " + line.substring(0, Math.min(100, line.length())));
                    System.out.println("🔍 PARSING: Processing relevant line " + relevantLineCount);
                    try {
                        float score = 0.0f;
                        if (line.contains("score cp")) {
                            int cpIndex = line.indexOf("score cp") + 8;
                            int nextSpace = line.indexOf(" ", cpIndex);
                            if (nextSpace > cpIndex) {
                                String scoreStr = line.substring(cpIndex, nextSpace).trim();
                                int centipawns = Integer.parseInt(scoreStr);
                                score = centipawns / 100.0f;
                                Log.e(TAG, "🔍 PARSING: Extracted centipawn score: " + centipawns + " -> " + score);
                            } else {
                                Log.e(TAG, "❌ PARSING: Could not find next space after 'score cp'");
                            }
                        } else if (line.contains("score mate")) {
                            score = 10.0f;
                            Log.e(TAG, "🔍 PARSING: Found mate score, setting to: " + score);
                        } else {
                            Log.e(TAG, "❌ PARSING: No recognized score format in line");
                        }

                        // Extract move from "time 700 pv e7e6" -> get "e7e6"
                        // CRITICAL: Find the LAST "pv " not the first (multipv vs pv)
                        int pvIndex = line.lastIndexOf(" pv ");
                        // Extract move from "time 700 pv e7e6" -> get "e7e6"
                        if (pvIndex != -1) {
                            String pvSection = line.substring(pvIndex + 4); // Skip " pv "
                            String[] pvMoves = pvSection.trim().split("\\s+");
                            if (pvMoves.length > 0 && !pvMoves[0].isEmpty()) {
                                String move = pvMoves[0].trim();
                                
                                // Check if move is valid UCI format (e2e4, g1f3, etc.)
                                if (move.length() >= 4 && move.matches("[a-h][1-8][a-h][1-8].*")) {
                                    moves.add(new PersonalityMove(move, score, 0.0f, "Stockfish analysis", false));
                                    System.out.println("✅ Added: " + move + " (" + score + ")");
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "❌ CRITICAL PARSING ERROR for line: " + line, e);
                        System.out.println("❌ CRITICAL PARSING ERROR: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR in parseStockfishAnalysis!", e);
            System.out.println("❌ CRITICAL ERROR in parseStockfishAnalysis: " + e.getMessage());
            e.printStackTrace();
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

        Log.e(TAG, "🔍 PARSING: Found " + moves.size() + " total moves, " + uniqueMoves.size() + " unique moves");
        System.out.println("🔍 PARSING: Found " + moves.size() + " total moves, " + uniqueMoves.size() + " unique moves");
        
        if (moves.size() == 0) {
            Log.e(TAG, "🚨 CRITICAL: NO MOVES EXTRACTED! Check UCI format and PV parsing above!");
            System.out.println("🚨 CRITICAL: NO MOVES EXTRACTED FROM STOCKFISH ANALYSIS!");
        }
        
        int finalCount = Math.min(STOCKFISH_CANDIDATES, result.size());
        Log.e(TAG, "🔍 PARSING: Returning " + finalCount + " candidates (limit: " + STOCKFISH_CANDIDATES + ")");
        System.out.println("🔍 PARSING: Returning " + finalCount + " candidates");

        return finalCount > 0 ? result.subList(0, finalCount) : result;
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
                
                // Dynamic limit based on game phase and position complexity
                int dynamicLimit = calculateOptimalPositionLimit(currentFen);
                Log.d(TAG, "🎯 Dynamic limit calculated: " + dynamicLimit + " positions for current game phase");
                List<GameDatabaseHelper.HistoricalPosition> results =
                        databaseHelper.findSimilarPositions(currentFen, nameVariation, dynamicLimit);
                Log.d(TAG, "🔧 DEBUG: Database returned " + results.size() + " results for '" + nameVariation + "'");
                
                // 🚨 FORCE VISIBLE LOG for database results
                System.out.println("📊 DATABASE QUERY RESULT: " + nameVariation + " returned " + results.size() + " historical positions");
                LogThrottler.force("PersonalityEngine", "📊 DATABASE QUERY RESULT: " + nameVariation + " returned " + results.size() + " historical positions");

                if (!results.isEmpty()) {
                    historicalPositions = results;
                    Log.d(TAG, "✅ OPTIMIZED SUCCESS with: '" + nameVariation + "' (" + results.size() + " positions)");
                    break; // Stop on first success
                } else {
                    Log.d(TAG, "🔧 DEBUG: No results for '" + nameVariation + "', trying next variation...");
                }
            }

            Log.d(TAG, "✅ FINAL lookup result: Found " + historicalPositions.size() + " similar positions");
            
            // DIAGNOSTIC: Analyze what was actually matched
            if (!historicalPositions.isEmpty()) {
                Log.d(TAG, "🔍 === DATABASE MATCH ANALYSIS ===");
                for (int i = 0; i < Math.min(3, historicalPositions.size()); i++) {
                    GameDatabaseHelper.HistoricalPosition pos = historicalPositions.get(i);
                    Log.d(TAG, String.format("   📋 Match %d: %s vs %s (%s)", 
                        i+1, pos.masterName, pos.opponent, pos.year));
                    Log.d(TAG, String.format("      🏆 Tournament: %s", pos.tournament));
                    Log.d(TAG, String.format("      ♟️ Move #%d: %s", pos.moveNumber, pos.annotation));
                    if (pos.similarity > 0) {
                        Log.d(TAG, String.format("      📊 Similarity: %.3f", pos.similarity));
                    }
                }
                
                // CRITICAL: Check if these matches make logical sense
                boolean logicallySound = validateMatchQuality(historicalPositions, currentFen);
                Log.d(TAG, "🧠 Matches logically sound: " + logicallySound);
            }

            // Rest of your existing logic...
            List<VectorSearchResult> compatibleResults = new ArrayList<>();
            for (GameDatabaseHelper.HistoricalPosition pos : historicalPositions) {
                compatibleResults.add(pos.toVectorSearchResult());
            }

            // Continue with existing logic...
            List<PersonalityMove> scoredMoves = applyPersonalityScoring(engineCandidates, compatibleResults);
            PersonalityMove selectedMove = selectBestPersonalityMove(scoredMoves);
            
            // DIAGNOSTIC: Log final move selection reasoning
            Log.d(TAG, "🎯 === FINAL MOVE SELECTION ===");
            Log.d(TAG, "🎭 Selected move: " + selectedMove.move);
            Log.d(TAG, "🤖 Engine score: " + selectedMove.engineScore);
            Log.d(TAG, "🎨 Personality bonus: " + selectedMove.personalityBonus);
            Log.d(TAG, "📊 Final score: " + selectedMove.finalScore);
            Log.d(TAG, "🏆 Historical context: " + selectedMove.historicalContext);
            
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

    /**
     * Calculate optimal number of positions to fetch based on game phase and complexity
     */
    private int calculateOptimalPositionLimit(String fen) {
        try {
            String[] fenParts = fen.split(" ");
            String position = fenParts[0];
            int moveNumber = Integer.parseInt(fenParts[5]);
            
            // Count pieces to determine game phase
            int pieceCount = 0;
            for (char c : position.toCharArray()) {
                if (Character.isLetter(c)) {
                    pieceCount++;
                }
            }
            
            // ✅ OPTIMIZED: Reduced limits to improve match quality
            // Quality over quantity - fewer but more accurate matches
            if (moveNumber <= 10) {
                // Opening: Standard patterns, moderate matches
                return 12;
            } else if (moveNumber <= 25 && pieceCount >= 20) {
                // Early middlegame: Reduce to avoid low-quality matches
                return 15;
            } else if (pieceCount >= 12) {
                // Complex middlegame: SIGNIFICANTLY REDUCED from 35→18
                // This fixes the "35 matches for queen sacrifice" issue
                return 18;
            } else {
                // Endgame: Keep precise
                return 8;
            }
            
        } catch (Exception e) {
            Log.w(TAG, "Error calculating dynamic limit, using default", e);
            return 12; // Reduced safe default
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
     * 🧠 NOW ENHANCED WITH AI STYLE ADVICE!
     */
    private List<PersonalityMove> applyPersonalityScoring(List<PersonalityMove> engineCandidates,
                                                          List<VectorSearchResult> historicalPositions) {

        List<PersonalityMove> scoredMoves = new ArrayList<>();
        
        // 🧠 AI ENHANCEMENT: Get AI style advice for this position
        AIStyleAdvisor.AIStyleAdvice aiAdvice = getAIStyleAdviceSync(engineCandidates);

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
            
            // 🧠 AI ENHANCEMENT: Apply AI style advice bonus
            float aiBonus = applyAIStyleBonus(candidate, aiAdvice);
            if (aiBonus > 0) {
                personalityBonus += aiBonus;
                if (!historicalContext.isEmpty()) {
                    historicalContext += " + ";
                }
                historicalContext += "AI-recommended style preference";
                Log.d(TAG, "🧠 AI STYLE BONUS: " + candidate.move + " -> +" + aiBonus + " AI bonus");
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

            case "alekhine":
                if (tagCounts.getOrDefault("attack", 0) > 0 ||
                        tagCounts.getOrDefault("positional", 0) > 0) {
                    styleBonus += personalityWeight * 0.45f; // Balanced combinational style
                }
                break;

            case "kramnik":
                if (tagCounts.getOrDefault("positional", 0) > 0 ||
                        tagCounts.getOrDefault("endgame", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f; // Solid positional play
                }
                break;

            case "karpov":
                if (tagCounts.getOrDefault("positional", 0) > 0 ||
                        tagCounts.getOrDefault("endgame", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f; // Positional squeeze
                }
                break;

            case "capablanca":
                if (tagCounts.getOrDefault("endgame", 0) > 0 ||
                        tagCounts.getOrDefault("positional", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f; // Natural positional play
                }
                break;

            case "carlsen":
                // Magnus adapts to any style - bonus for any recognizable pattern
                if (tagCounts.size() > 0) {
                    styleBonus += personalityWeight * 0.3f; // Universal adaptability
                }
                break;

            case "anand":
                if (tagCounts.getOrDefault("attack", 0) > 0 ||
                        tagCounts.getOrDefault("positional", 0) > 0) {
                    styleBonus += personalityWeight * 0.35f; // Versatile style
                }
                break;

            case "morphy":
                if (tagCounts.getOrDefault("attack", 0) > 0 ||
                        tagCounts.getOrDefault("sacrifice", 0) > 0) {
                    styleBonus += personalityWeight * 0.5f; // Classical attacking play
                }
                break;

            case "lasker":
                if (tagCounts.getOrDefault("endgame", 0) > 0 ||
                        tagCounts.getOrDefault("defense", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f; // Practical fighting
                }
                break;

            case "botvinnik":
                if (tagCounts.getOrDefault("positional", 0) > 0) {
                    styleBonus += personalityWeight * 0.4f; // Scientific approach
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

            case "alekhine":
                return selectedMove.isHistoricalMatch ?
                        "This exact combination appeared in my games! The position unfolds beautifully." :
                        "A sophisticated move - let me show you the hidden tactical motifs.";

            case "kramnik":
                return selectedMove.isHistoricalMatch ?
                        "I played this precise move in my preparation. Modern chess demands such accuracy." :
                        "Solid and principled - this move improves our position systematically.";

            case "karpov":
                return selectedMove.isHistoricalMatch ?
                        "Patience pays off - I've played this slow squeeze before." :
                        "Small improvements accumulate. This move enhances our long-term prospects.";

            case "capablanca":
                return selectedMove.isHistoricalMatch ?
                        "Naturally! This simple, strong move is exactly my style." :
                        "The most natural continuation - elegant and effective.";

            case "carlsen":
                return selectedMove.isHistoricalMatch ?
                        "I remember this position - there's always a way to squeeze for more." :
                        "Practical chess. This gives our opponent the most problems to solve.";

            case "anand":
                return selectedMove.isHistoricalMatch ?
                        "Quick recognition - I've analyzed this pattern thoroughly." :
                        "Fast and accurate calculation shows this is the right path.";

            case "morphy":
                return selectedMove.isHistoricalMatch ?
                        "A principled move from the good old days of chess!" :
                        "Rapid development and sound principles guide this choice.";

            case "lasker":
                return selectedMove.isHistoricalMatch ?
                        "Psychology and technique combined - I've used this approach before." :
                        "Practical wisdom suggests this creates the most difficulties.";

            case "botvinnik":
                return selectedMove.isHistoricalMatch ?
                        "Scientific analysis confirms this is the correct continuation." :
                        "Methodical preparation leads to systematic improvement.";

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
        // Complete display name mapping for all masters
        switch (currentMaster.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "fischer": return "Bobby Fischer";
            case "kasparov": return "Garry Kasparov";
            case "kramnik": return "Vladimir Kramnik";
            case "karpov": return "Anatoly Karpov";
            case "alekhine": return "Alexander Alekhine";
            case "capablanca": return "José Raúl Capablanca";
            case "carlsen": return "Magnus Carlsen";
            case "anand": return "Viswanathan Anand";
            case "morphy": return "Paul Morphy";
            case "lasker": return "Emanuel Lasker";
            case "botvinnik": return "Mikhail Botvinnik";
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
            // NEW: Add the unmapped masters that have files
            case "dommaraju":
            case "gukesh":
                return "dommaraju_full_positions.json";
            case "nakamura":
            case "hikaru":
                return "nakamura_full_positions.json";
            case "short":
            case "nigel":
                return "short_full_positions.json";
            default:
                // Fallback to old naming convention
                return masterName.toLowerCase() + "_positions.json";
        }
    }
    
    /**
     * Validate whether database matches make logical sense for the position
     */
    private boolean validateMatchQuality(List<GameDatabaseHelper.HistoricalPosition> matches, String currentFen) {
        if (matches.isEmpty()) {
            Log.d(TAG, "🔍 No matches to validate");
            return true;
        }
        
        try {
            // Extract game context from FEN
            String[] fenParts = currentFen.split(" ");
            int moveNumber = Integer.parseInt(fenParts[5]);
            
            // Count total pieces for strength estimation
            String position = fenParts[0];
            int pieceCount = 0;
            for (char c : position.toCharArray()) {
                if (Character.isLetter(c)) {
                    pieceCount++;
                }
            }
            
            // Estimate playing strength based on position complexity
            int estimatedElo = estimatePositionStrength(pieceCount, moveNumber);
            
            // Check if matches are from appropriate strength games
            boolean hasHighQualityMatches = false;
            int masterLevelMatches = 0;
            
            for (GameDatabaseHelper.HistoricalPosition match : matches) {
                // Check tournament quality indicators
                String tournament = match.tournament != null ? match.tournament.toLowerCase() : "";
                String significance = match.significance != null ? match.significance.toLowerCase() : "";
                
                boolean isHighQuality = tournament.contains("world") || 
                                      tournament.contains("championship") ||
                                      tournament.contains("olympiad") ||
                                      significance.contains("brilliant") ||
                                      significance.contains("masterpiece");
                
                if (isHighQuality) {
                    hasHighQualityMatches = true;
                    masterLevelMatches++;
                }
            }
            
            // Log analysis results
            Log.d(TAG, String.format("🧠 Match Quality Analysis:"));
            Log.d(TAG, String.format("   📊 Position estimated ELO: %d", estimatedElo));
            Log.d(TAG, String.format("   🏆 High-quality matches: %d/%d", masterLevelMatches, matches.size()));
            Log.d(TAG, String.format("   ✨ Has master-level games: %s", hasHighQualityMatches));
            
            // Warn if too many matches for low-strength positions
            if (estimatedElo < 2200 && matches.size() > 10) {
                Log.w(TAG, String.format("⚠️ QUALITY WARNING: %d ELO position has %d super-GM matches (suspicious)", 
                      estimatedElo, matches.size()));
                return false;
            }
            
            // Validate we have at least some quality matches for complex positions
            if (estimatedElo > 2500 && !hasHighQualityMatches) {
                Log.w(TAG, "⚠️ QUALITY WARNING: High-level position has no master-level game matches");
                return false;
            }
            
            Log.d(TAG, "✅ Match quality validation passed");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error validating match quality", e);
            return false;
        }
    }
    
    /**
     * Estimate the playing strength of a position based on complexity
     */
    private int estimatePositionStrength(int pieceCount, int moveNumber) {
        // Base estimation algorithm
        int baseElo = 1500;
        
        // More pieces = more complex tactical possibilities
        if (pieceCount > 28) baseElo += 300; // Opening complexity
        else if (pieceCount < 15) baseElo += 200; // Endgame precision required
        
        // Later moves in game suggest deeper preparation
        if (moveNumber > 30) baseElo += 400; // Deep middlegame/endgame
        else if (moveNumber > 15) baseElo += 200; // Developed middlegame
        
        return Math.min(baseElo, 2800); // Cap at super-GM level
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
    
    // ======================== AI STYLE ADVICE METHODS ========================
    
    /**
     * 🧠 Get AI style advice synchronously for current position
     * This method provides AI-enhanced personality insights for move selection
     */
    private AIStyleAdvisor.AIStyleAdvice getAIStyleAdviceSync(List<PersonalityMove> engineCandidates) {
        if (aiStyleAdvisor == null) {
            Log.w(TAG, "⚠️ AI Style Advisor not available - using fallback");
            return createFallbackAIAdvice();
        }
        
        // Only use AI for masters with OpenAI assistants
        if (!hasAIAssistant(currentMaster)) {
            Log.d(TAG, "🤖 No AI assistant for " + currentMaster + " - using fallback");
            return createFallbackAIAdvice();
        }
        
        try {
            // Extract move names for AI consultation
            List<String> candidateMoves = new ArrayList<>();
            for (PersonalityMove move : engineCandidates) {
                candidateMoves.add(move.move);
            }
            
            Log.d(TAG, "🧠 Requesting AI style advice for " + currentMaster + " with " + candidateMoves.size() + " candidates");
            
            // Use blocking wait for AI advice (we're already in background thread)
            final AIStyleAdvisor.AIStyleAdvice[] result = {null};
            final boolean[] completed = {false};
            
            // Get current FEN from context (this is simplified - you may need to pass it differently)
            String currentFEN = getCurrentFEN();
            
            aiStyleAdvisor.getStyleAdvice(currentFEN, currentMaster, candidateMoves, 
                new AIStyleAdvisor.AIStyleCallback() {
                    @Override
                    public void onAdviceReceived(AIStyleAdvisor.AIStyleAdvice advice) {
                        result[0] = advice;
                        synchronized (completed) {
                            completed[0] = true;
                            completed.notify();
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "🧠 AI advice error: " + error);
                        result[0] = createFallbackAIAdvice();
                        synchronized (completed) {
                            completed[0] = true;
                            completed.notify();
                        }
                    }
                    
                    @Override
                    public void onCacheHit(AIStyleAdvisor.AIStyleAdvice cachedAdvice) {
                        Log.d(TAG, "⚡ AI advice from cache");
                        result[0] = cachedAdvice;
                        synchronized (completed) {
                            completed[0] = true;
                            completed.notify();
                        }
                    }
                });
            
            // Wait for AI response with timeout
            synchronized (completed) {
                if (!completed[0]) {
                    completed.wait(5000); // 5 second timeout for AI
                }
            }
            
            return result[0] != null ? result[0] : createFallbackAIAdvice();
            
        } catch (Exception e) {
            Log.e(TAG, "💥 Error getting AI style advice", e);
            return createFallbackAIAdvice();
        }
    }
    
    /**
     * 🎯 Apply AI style bonus to a candidate move
     */
    private float applyAIStyleBonus(PersonalityMove candidate, AIStyleAdvisor.AIStyleAdvice aiAdvice) {
        if (aiAdvice == null || aiAdvice.preferredMoves.isEmpty()) {
            return 0.0f;
        }
        
        // Find move's ranking in AI preferences (0 = top choice, 1 = second, etc.)
        int moveRank = aiAdvice.preferredMoves.indexOf(candidate.move);
        if (moveRank >= 0) {
            float aiWeight = Math.min(aiAdvice.styleWeight, 0.8f); // Cap AI influence
            
            // Weight bonus by AI ranking: top choice gets full bonus, others get diminishing bonus
            float rankingMultiplier = 1.0f - (moveRank * 0.15f); // 1.0, 0.85, 0.70, 0.55, etc.
            rankingMultiplier = Math.max(rankingMultiplier, 0.25f); // Minimum 25% bonus for any AI-preferred move
            
            float bonus = personalityWeight * aiWeight * 0.6f * rankingMultiplier; // Increased base bonus
            
            Log.d(TAG, "🧠 AI: " + candidate.move + " #" + (moveRank + 1) + " bonus=" + bonus);
            System.out.println("🧠 AI: " + candidate.move + " #" + (moveRank + 1) + " +" + bonus);
            return bonus;
        }
        
        return 0.0f;
    }
    
    /**
     * 🛡️ Create fallback AI advice when real AI is unavailable
     */
    private AIStyleAdvisor.AIStyleAdvice createFallbackAIAdvice() {
        List<String> emptyMoves = new ArrayList<>();
        return new AIStyleAdvisor.AIStyleAdvice(emptyMoves, 0.0f, 
                "Fallback advice", "", false, false);
    }
    
    /**
     * 🤖 Check if master has an OpenAI assistant
     */
    private boolean hasAIAssistant(String master) {
        switch (master.toLowerCase()) {
            case "alekhine":
            case "tal":
            case "fischer":
            case "carlsen":
                return true;
            default:
                return false;
        }
    }
    
    /**
     * 📋 Get current FEN for AI analysis
     */
    private String getCurrentFEN() {
        return this.currentFEN;
    }
    
    /**
     * 🧹 Clear AI cache between games
     */
    public void clearAICache() {
        if (aiStyleAdvisor != null) {
            aiStyleAdvisor.clearCache();
            Log.d(TAG, "🧹 AI Style Advisor cache cleared");
        }
    }
}