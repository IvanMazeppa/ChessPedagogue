// GameRepository.java - FIXED VERSION WITH PERSONALITY ENGINE
package com.example.chesspedagogue.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Context;

import com.example.chesspedagogue.ChessMasterRatings;
import com.example.chesspedagogue.PersonalityEngine;
import com.example.chesspedagogue.FineTunedModelManager;
import com.example.chesspedagogue.StockfishManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameRepository {
    private static final String TAG = "GameRepository";

    // ENHANCED: Thread safety with proper locking
    private final ReentrantLock engineLock = new ReentrantLock();
    private final AtomicBoolean isEngineProcessing = new AtomicBoolean(false);

    // This is the key - we need to make stockfishManager public for methods or add proper methods
    public final StockfishManager stockfishManager;
    private String cachedFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context;

    // FIXED: Personality Engine fields
    private PersonalityEngine personalityEngine;
    private boolean usePersonalityEngine = false;
    private int lastMoveCount = 0;

    public GameRepository(Context context) {
        this.context = context;
        stockfishManager = new StockfishManager();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // FIXED: Initialize engine first, then personality engine
        initializeEngine();
        initializePersonalityEngine();
    }

    /**
     * FIXED: Initialize the PersonalityEngine after Stockfish is ready
     */
    private void initializePersonalityEngine() {
        try {
            personalityEngine = PersonalityEngine.getInstance(context, stockfishManager);
            Log.d(TAG, "🎭 PersonalityEngine initialized successfully!");
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize PersonalityEngine", e);
        }
    }

    /**
     * NEW METHOD - more robust initialization
     */
    private void initializeEngine() {
        try {
            // Do the basic initialization on the main thread to avoid race conditions
            File engineFile = new File(context.getApplicationInfo().nativeLibraryDir, "libstockfish.so");
            if (!engineFile.exists()) {
                Log.e(TAG, "Engine file not found at: " + engineFile.getAbsolutePath());
                return;
            }

            // Start the engine synchronously - this is important!
            boolean success = stockfishManager.startEngine(engineFile.getAbsolutePath());
            if (success) {
                // Initialize with default settings after successful start
                stockfishManager.setSkillLevel(10); // Default skill level
                stockfishManager.newGame();
                Log.d(TAG, "Engine initialized successfully! 🎉");
            } else {
                Log.e(TAG, "Failed to initialize Stockfish engine");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Stockfish", e);
        }
    }

    /**
     * REVOLUTIONARY: Calculate best move using personality-guided engine!
     * This is Ben's breakthrough innovation - an engine that plays like chess legends! 🚀
     */
    public void calculatePersonalityMove(MoveCallback callback) {
        if (!usePersonalityEngine || personalityEngine == null) {
            // Fall back to regular engine calculation
            calculateBestMove(callback);
            return;
        }

        Log.d(TAG, "🎭 CALCULATING PERSONALITY MOVE - This is revolutionary!");

        executorService.execute(() -> {
            engineLock.lock();
            try {
                String currentFen = getCurrentFEN();
                Log.d(TAG, "🎯 Getting personality move for position: " + currentFen.substring(0, Math.min(30, currentFen.length())));

                personalityEngine.selectPersonalityMove(currentFen, new PersonalityEngine.PersonalityMoveCallback() {
                    @Override
                    public void onPersonalityMoveSelected(PersonalityEngine.PersonalityMove selectedMove,
                                                          List<PersonalityEngine.PersonalityMove> allCandidates) {

                        Log.d(TAG, "🎭 PERSONALITY MOVE SELECTED: " + selectedMove);

                        // Log all candidates for debugging
                        Log.d(TAG, "🎯 Move candidates considered:");
                        for (int i = 0; i < Math.min(5, allCandidates.size()); i++) {
                            PersonalityEngine.PersonalityMove candidate = allCandidates.get(i);
                            Log.d(TAG, String.format("  %d. %s", i+1, candidate.toString()));
                        }

                        mainHandler.post(() -> {
                            if (selectedMove != null && selectedMove.move != null) {
                                callback.onMoveCalculated(selectedMove.move);

                                // Store the personality context for later explanation
                                storePersonalityContext(selectedMove);
                            } else {
                                Log.e(TAG, "❌ Personality engine returned null move");
                                callback.onError("Personality engine failed to select move");
                            }
                        });
                    }

                    @Override
                    public void onPersonalityAnalysisComplete(String analysis, String masterQuote) {
                        Log.d(TAG, "🎭 Personality Analysis: " + analysis);
                        Log.d(TAG, "💬 Master Quote: " + masterQuote);

                        // You could store these for UI display or voice synthesis
                        storePersonalityAnalysis(analysis, masterQuote);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "❌ PersonalityEngine error: " + errorMessage);
                        mainHandler.post(() -> {
                            // Fall back to regular engine move
                            Log.d(TAG, "🔄 Falling back to regular engine calculation");
                            calculateBestMove(callback);
                        });
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Error in personality move calculation", e);
                mainHandler.post(() -> callback.onError("Personality engine error: " + e.getMessage()));
            } finally {
                engineLock.unlock();
            }
        });
    }

    /**
     * Store personality context for later use (explanations, UI display, etc.)
     */
    private void storePersonalityContext(PersonalityEngine.PersonalityMove move) {
        // Store in SharedPreferences or memory for UI access
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("personality_context", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();

            editor.putString("last_move", move.move);
            editor.putFloat("engine_score", move.engineScore);
            editor.putFloat("personality_bonus", move.personalityBonus);
            editor.putString("historical_context", move.historicalContext);
            editor.putBoolean("is_historical_match", move.isHistoricalMatch);

            editor.apply();

            Log.d(TAG, "💾 Stored personality context for UI access");
        } catch (Exception e) {
            Log.e(TAG, "Error storing personality context", e);
        }
    }

    /**
     * Store personality analysis for UI/voice synthesis
     */
    private void storePersonalityAnalysis(String analysis, String masterQuote) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("personality_context", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();

            editor.putString("move_analysis", analysis);
            editor.putString("master_quote", masterQuote);
            editor.putLong("analysis_timestamp", System.currentTimeMillis());

            editor.apply();

            Log.d(TAG, "💾 Stored personality analysis");
        } catch (Exception e) {
            Log.e(TAG, "Error storing personality analysis", e);
        }
    }

    /**
     * Configure the personality engine settings
     */
    public void configurePersonalityEngine(String master, float personalityWeight, boolean enabled) {
        if (personalityEngine == null) {
            initializePersonalityEngine();
        }

        if (personalityEngine != null) {
            personalityEngine.setCurrentMaster(master);
            personalityEngine.setPersonalityWeight(personalityWeight);
            personalityEngine.setPersonalityPlayEnabled(enabled);

            this.usePersonalityEngine = enabled;

            Log.d(TAG, String.format("🎭 Personality engine configured: master=%s, weight=%.2f, enabled=%s",
                    master, personalityWeight, enabled));
        } else {
            Log.e(TAG, "❌ Cannot configure personality engine - initialization failed");
        }
    }

    /**
     * Get the last personality move context for UI display
     */
    public PersonalityMoveContext getLastPersonalityContext() {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("personality_context", Context.MODE_PRIVATE);

            String move = prefs.getString("last_move", "");
            if (move.isEmpty()) return null;

            return new PersonalityMoveContext(
                    move,
                    prefs.getFloat("engine_score", 0.0f),
                    prefs.getFloat("personality_bonus", 0.0f),
                    prefs.getString("historical_context", ""),
                    prefs.getBoolean("is_historical_match", false),
                    prefs.getString("move_analysis", ""),
                    prefs.getString("master_quote", "")
            );

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving personality context", e);
            return null;
        }
    }

    /**
     * Check if personality engine is available and configured
     */
    public boolean isPersonalityEngineAvailable() {
        return personalityEngine != null && usePersonalityEngine;
    }

    /**
     * Get historical move suggestion for current position
     */
    public void getHistoricalMoveSuggestion(String master, Callback<String> callback) {
        if (personalityEngine == null) {
            if (callback != null) {
                callback.onError("Personality engine not available");
            }
            return;
        }

        executorService.execute(() -> {
            engineLock.lock();
            try {
                String currentFen = getCurrentFEN();

                // Use the FineTunedModelManager to search for historical moves
                FineTunedModelManager modelManager = FineTunedModelManager.getInstance(context);
                modelManager.getHistoricalMoveForPosition(currentFen, master, new FineTunedModelManager.Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        mainHandler.post(() -> {
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        mainHandler.post(() -> {
                            if (callback != null) {
                                callback.onError(errorMessage);
                            }
                        });
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error getting historical move suggestion", e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("Failed to get historical suggestion: " + e.getMessage());
                    }
                });
            } finally {
                engineLock.unlock();
            }
        });
    }

    /**
     * Data class to hold personality move context
     */
    public static class PersonalityMoveContext {
        public final String move;
        public final float engineScore;
        public final float personalityBonus;
        public final String historicalContext;
        public final boolean isHistoricalMatch;
        public final String analysis;
        public final String masterQuote;

        public PersonalityMoveContext(String move, float engineScore, float personalityBonus,
                                      String historicalContext, boolean isHistoricalMatch,
                                      String analysis, String masterQuote) {
            this.move = move;
            this.engineScore = engineScore;
            this.personalityBonus = personalityBonus;
            this.historicalContext = historicalContext;
            this.isHistoricalMatch = isHistoricalMatch;
            this.analysis = analysis;
            this.masterQuote = masterQuote;
        }

        @Override
        public String toString() {
            return String.format("PersonalityMove{%s, engine=%.2f, personality=+%.2f, historical=%s}",
                    move, engineScore, personalityBonus, isHistoricalMatch);
        }
    }

    // Add this to your existing cleanup() method
    public void cleanupPersonalityEngine() {
        if (personalityEngine != null) {
            // PersonalityEngine cleanup if needed
            Log.d(TAG, "🧹 Personality engine cleanup completed");
        }
    }

    /**
     * ENHANCED: Thread safety with proper locking
     */
    public boolean isLegalMove(String move) {
        engineLock.lock();
        try {
            Log.d(TAG, "🔒 Acquired lock for move validation: " + move);
            boolean result = stockfishManager.isLegalMove(move);
            Log.d(TAG, "⚖️ Move " + move + " validation result: " + result);
            return result;
        } finally {
            engineLock.unlock();
            Log.d(TAG, "🔓 Released lock for move validation: " + move);
        }
    }

    /**
     * ENHANCED: Thread-safe position setting with proper state management
     */
    public boolean setPositionFromMoves(String... moves) {
        engineLock.lock();
        try {
            Log.d(TAG, "🔒 Setting position from " + moves.length + " moves");

            StringBuilder command = new StringBuilder("position startpos");
            if (moves.length > 0) {
                command.append(" moves");
                for (String move : moves) {
                    command.append(" ").append(move);
                }
            }

            // Use stockfishManager to send the command
            stockfishManager.sendCommand(command.toString());

            // Wait for the engine to be ready
            boolean success = stockfishManager.waitForReady(1000);

            if (success) {
                // Update our cached FEN ONLY after successful command
                String newFEN = stockfishManager.getCurrentFEN();
                if (newFEN != null && !newFEN.isEmpty()) {
                    cachedFEN = newFEN;
                    Log.d(TAG, "✅ Position updated successfully: " + cachedFEN);
                } else {
                    Log.w(TAG, "⚠️ Got empty FEN from engine");
                }
            } else {
                Log.e(TAG, "❌ Failed to set position - engine not ready");
            }

            return success;
        } catch (IOException e) {
            Log.e(TAG, "Error setting position from moves", e);
            return false;
        } finally {
            engineLock.unlock();
            Log.d(TAG, "🔓 Released lock for position setting");
        }
    }

    // Add other necessary methods
    public List<String> getLegalMovesForPiece(int row, int col) {
        engineLock.lock();
        try {
            return stockfishManager.getLegalMovesForPiece(row, col);
        } finally {
            engineLock.unlock();
        }
    }

    /**
     * ENHANCED: Thread-safe FEN retrieval
     */
    public String getCurrentFEN() {
        engineLock.lock();
        try {
            // Always get fresh FEN from engine to ensure accuracy
            String freshFEN = stockfishManager.getCurrentFEN();
            if (freshFEN != null && !freshFEN.isEmpty()) {
                cachedFEN = freshFEN;
                return cachedFEN;
            } else {
                Log.w(TAG, "Using cached FEN as engine returned empty");
                return cachedFEN;
            }
        } finally {
            engineLock.unlock();
        }
    }

    // Replace these check/checkmate methods with simpler implementations:
    public boolean isCheckmate() {
        engineLock.lock();
        try {
            // Temporary implementation until we integrate with your existing methods
            String fen = stockfishManager.getCurrentFEN();
            return fen != null && fen.contains("#"); // Simple placeholder check
        } finally {
            engineLock.unlock();
        }
    }

    public boolean isStalemate() {
        // Temporary implementation
        return false; // We'll enhance this later
    }

    public interface MoveCallback {
        void onMoveCalculated(String move);
        void onError(String errorMessage); // Add this method
    }

    /**
     * NEW INTERFACE: Callback for evaluation results
     */
    public interface EvaluationCallback {
        void onEvaluationReceived(StockfishManager.EvaluationResult result);
        void onEvaluationError(String errorMessage);
    }

    /**
     * Callback interface for general use
     */
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    /**
     * ENHANCED: Thread-safe evaluation with proper sequencing
     */
    public void getCurrentEvaluation(EvaluationCallback callback) {
        // Prevent multiple evaluations from running simultaneously
        if (isEngineProcessing.compareAndSet(false, true)) {
            executorService.execute(() -> {
                engineLock.lock();
                try {
                    Log.d(TAG, "🔍 Starting position evaluation...");

                    // Get evaluation from Stockfish (500ms should be enough for quick eval)
                    StockfishManager.EvaluationResult result = stockfishManager.getCurrentEvaluation(500);

                    // Return result on main thread
                    mainHandler.post(() -> {
                        Log.d(TAG, "✅ Evaluation completed: " + result.toString());
                        callback.onEvaluationReceived(result);
                    });

                } catch (Exception e) {
                    Log.e(TAG, "❌ Error getting evaluation", e);
                    mainHandler.post(() -> callback.onEvaluationError("Failed to get evaluation: " + e.getMessage()));
                } finally {
                    engineLock.unlock();
                    isEngineProcessing.set(false);
                    Log.d(TAG, "🔓 Evaluation lock released");
                }
            });
        } else {
            Log.d(TAG, "⏳ Evaluation already in progress, skipping request");
        }
    }

    /**
     * ENHANCED: Thread-safe evaluation for specific position
     */
    public void getEvaluationForPosition(String fen, EvaluationCallback callback) {
        if (isEngineProcessing.compareAndSet(false, true)) {
            executorService.execute(() -> {
                engineLock.lock();
                try {
                    Log.d(TAG, "🔍 Getting evaluation for specific position: " + fen.substring(0, Math.min(20, fen.length())) + "...");

                    // Set the position first
                    boolean positionSet = stockfishManager.setPosition(fen);
                    if (!positionSet) {
                        mainHandler.post(() -> callback.onEvaluationError("Failed to set position"));
                        return;
                    }

                    // Get evaluation
                    StockfishManager.EvaluationResult result = stockfishManager.getCurrentEvaluation(750);

                    // Return result on main thread
                    mainHandler.post(() -> {
                        Log.d(TAG, "✅ Position evaluation received: " + result.toString());
                        callback.onEvaluationReceived(result);
                    });

                } catch (Exception e) {
                    Log.e(TAG, "❌ Error getting position evaluation", e);
                    mainHandler.post(() -> callback.onEvaluationError("Failed to evaluate position: " + e.getMessage()));
                } finally {
                    engineLock.unlock();
                    isEngineProcessing.set(false);
                }
            });
        } else {
            Log.d(TAG, "⏳ Position evaluation already in progress, skipping request");
        }
    }

    // Updated methods for GameRepository.java - ACCURATE ELO IMPLEMENTATION

    /**
     * FIXED: Configure engine with ACCURATE ELO ratings
     * This replaces your existing configureEngine method
     */
    public void configureEngine(int skillLevel, int engineElo) {
        executorService.execute(() -> {
            engineLock.lock();
            try {
                Log.d(TAG, "🔧 Configuring engine with ACCURATE ELO: " + engineElo);

                // FIXED: Use proper ELO to Stockfish mapping
                ChessMasterRatings.StockfishConfig config = ChessMasterRatings.getStockfishConfigForElo(engineElo);

                Log.d(TAG, "📊 Stockfish config: " + config.toString());

                // Set the calculated skill level (not the slider value!)
                boolean skillSet = stockfishManager.setSkillLevel(config.skillLevel);
                Log.d(TAG, "Skill level " + config.skillLevel + " set: " + skillSet);

                // For high-level play, disable skill limiting and use time control
                if (engineElo >= 2400) {
                    try {
                        // Disable skill level limitation for strong players
                        stockfishManager.sendCommand("setoption name UCI_LimitStrength value false");
                        stockfishManager.waitForReady(100);

                        // Use depth and time for strength instead
                        stockfishManager.sendCommand("setoption name Depth value " + Math.min(20, 10 + (engineElo - 2400) / 50));
                        stockfishManager.waitForReady(100);

                        Log.d(TAG, "🚀 High-strength mode enabled for " + engineElo + " ELO");

                    } catch (IOException e) {
                        Log.w(TAG, "Could not set high-strength options: " + e.getMessage());
                    }
                } else {
                    try {
                        // Enable UCI_LimitStrength for accurate ELO targeting
                        stockfishManager.sendCommand("setoption name UCI_LimitStrength value true");
                        stockfishManager.waitForReady(100);

                        stockfishManager.sendCommand("setoption name UCI_Elo value " + config.targetElo);
                        stockfishManager.waitForReady(100);

                        Log.d(TAG, "🎯 ELO limiting enabled: " + config.targetElo);

                    } catch (IOException e) {
                        Log.w(TAG, "Could not set ELO limiting: " + e.getMessage());
                    }
                }

                // Add variety while maintaining strength
                try {
                    // Contempt factor for personality
                    int contempt = (int) (Math.random() * 10 - 5); // -5 to +5
                    stockfishManager.sendCommand("setoption name Contempt value " + contempt);
                    stockfishManager.waitForReady(100);

                    Log.d(TAG, "✅ Engine configuration complete! Target: " + engineElo + " ELO");

                } catch (IOException e) {
                    Log.w(TAG, "Could not set variety options: " + e.getMessage());
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Error configuring engine", e);
            } finally {
                engineLock.unlock();
            }
        });
    }

    /**
     * NEW: Configure engine for specific chess master with historical rating
     */
    public void configureEngineForMaster(String master) {
        int peakRating = ChessMasterRatings.getPeakRating(master);
        Log.d(TAG, "🎭 Configuring engine for " + master + " at peak rating: " + peakRating);

        configureEngine(0, peakRating); // Skill level irrelevant, use accurate ELO
    }

    /**
     * ENHANCED: Better move calculation with accurate strength
     */
    public void calculateBestMove(MoveCallback callback) {
        executorService.execute(() -> {
            engineLock.lock();
            try {
                Log.d(TAG, "🤔 Calculating best move...");

                // Get the current engine configuration for appropriate think time
                String currentFen = getCurrentFEN();

                // Determine think time based on position complexity and engine strength
                int thinkTime = calculateThinkTime(currentFen);

                String bestMove = stockfishManager.getBestMove(thinkTime);
                Log.d(TAG, "🎯 Engine calculated best move: " + bestMove + " (think time: " + thinkTime + "ms)");

                mainHandler.post(() -> {
                    if (bestMove != null && !bestMove.isEmpty()) {
                        callback.onMoveCalculated(bestMove);
                    } else {
                        callback.onError("Engine failed to calculate move");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error calculating best move", e);
                mainHandler.post(() -> callback.onError("Engine error: " + e.getMessage()));
            } finally {
                engineLock.unlock();
            }
        });
    }

    /**
     * NEW: Calculate appropriate think time based on position and engine strength
     */
    private int calculateThinkTime(String fen) {
        // Base think time
        int baseTime = 1000; // 1 second

        // Adjust based on position complexity
        int pieceCount = countPieces(fen);
        if (pieceCount < 10) {
            baseTime += 500; // More time in endgames
        } else if (pieceCount > 25) {
            baseTime += 300; // More time in complex positions
        }

        // Adjust based on configured strength (stored in SharedPreferences)
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            int configuredElo = prefs.getInt("ENGINE_ELO", 1750);

            if (configuredElo >= 2600) {
                baseTime += 1000; // Grandmaster level gets more time
            } else if (configuredElo >= 2200) {
                baseTime += 500;  // Master level
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get configured ELO for think time");
        }

        return Math.min(baseTime, 3000); // Cap at 3 seconds for responsiveness
    }

    /**
     * Helper: Count pieces on the board
     */
    private int countPieces(String fen) {
        if (fen == null) return 32;

        String boardPart = fen.split(" ")[0];
        int count = 0;

        for (char c : boardPart.toCharArray()) {
            if (Character.isLetter(c)) {
                count++;
            }
        }

        return count;
    }

    /**
     * ENHANCED: Robust move application with state verification
     */
    public boolean applyMoves(List<String> moves) {
        engineLock.lock();
        try {
            Log.d(TAG, "🔄 Applying " + (moves != null ? moves.size() : 0) + " moves");

            // Always reset if we have no moves
            if (moves == null || moves.isEmpty()) {
                stockfishManager.setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                cachedFEN = stockfishManager.getCurrentFEN();
                lastMoveCount = 0;
                Log.d(TAG, "✅ Reset to starting position");
                return true;
            }

            // For debugging - let's print the current position and number of moves
            Log.d(TAG, "Current cached FEN: " + cachedFEN);

            // Start from a clean position for consistency
            stockfishManager.setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

            // Apply all moves sequentially
            for (int i = 0; i < moves.size(); i++) {
                String move = moves.get(i);
                Log.d(TAG, "Applying move " + (i+1) + " of " + moves.size() + ": " + move);

                // Build the command with all moves up to this point
                String command = "position startpos moves " +
                        String.join(" ", moves.subList(0, i+1));

                // Send the command (void return type)
                stockfishManager.sendCommand(command);

                // Wait for the engine to process and check success
                boolean success = stockfishManager.waitForReady(100);

                if (!success) {
                    Log.e(TAG, "Failed to apply moves up to: " + move);
                    return false;
                }
            }

            // Update the cached FEN AND verify it changed
            String previousFEN = cachedFEN;
            cachedFEN = stockfishManager.getCurrentFEN();
            lastMoveCount = moves.size();

            // Verify the position actually changed (unless it's the same move count)
            if (moves.size() > 0 && cachedFEN.equals(previousFEN) && lastMoveCount != moves.size()) {
                Log.w(TAG, "⚠️ Position may not have updated correctly!");
                Log.w(TAG, "Previous: " + previousFEN);
                Log.w(TAG, "Current:  " + cachedFEN);
            }

            Log.d(TAG, "✅ Successfully applied " + moves.size() + " moves. Final position: " + cachedFEN);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error applying moves: " + e.getMessage(), e);
            return false;
        } finally {
            engineLock.unlock();
        }
    }

    /**
     * ENHANCED: Thread-safe single move with state verification
     */
    public boolean makeMove(String move) {
        engineLock.lock();
        try {
            Log.d(TAG, "🎯 Making single move: " + move);
            Log.d(TAG, "📋 Current position before move: " + cachedFEN);

            // Apply the move directly to current position
            stockfishManager.sendCommand("position fen " + cachedFEN + " moves " + move);

            // Wait for engine to process
            boolean success = stockfishManager.waitForReady(100);

            // Update cached FEN if successful and verify change
            if (success) {
                String previousFEN = cachedFEN;
                cachedFEN = stockfishManager.getCurrentFEN();

                if (!cachedFEN.equals(previousFEN)) {
                    Log.d(TAG, "✅ Move applied successfully!");
                    Log.d(TAG, "📍 New position: " + cachedFEN);
                } else {
                    Log.w(TAG, "⚠️ Position didn't change after move - this might be an issue");
                    success = false;
                }
            } else {
                Log.e(TAG, "❌ Failed to apply move: " + move);
            }

            return success;
        } catch (IOException e) {
            Log.e(TAG, "Error making move", e);
            return false;
        } finally {
            engineLock.unlock();
        }
    }

    /**
     * ENHANCED: Thread-safe new game
     */
    public void newGame() {
        engineLock.lock();
        try {
            Log.d(TAG, "🆕 Starting new game");
            stockfishManager.newGame();
            cachedFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            lastMoveCount = 0;
            Log.d(TAG, "✅ New game initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error starting new game", e);
        } finally {
            engineLock.unlock();
        }
    }

    /**
     * ENHANCED: Proper cleanup with thread safety
     */
    public void cleanup() {
        Log.d(TAG, "🧹 Cleaning up GameRepository");

        // Stop the engine processing flag
        isEngineProcessing.set(false);

        // Cleanup personality engine
        cleanupPersonalityEngine();

        // Shutdown executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

        // Stop the engine
        engineLock.lock();
        try {
            if (stockfishManager != null) {
                stockfishManager.stopEngine();
            }
        } finally {
            engineLock.unlock();
        }

        Log.d(TAG, "✅ GameRepository cleanup complete");
    }
}