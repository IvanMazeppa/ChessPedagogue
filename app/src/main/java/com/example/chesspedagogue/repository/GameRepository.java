// GameRepository.java - FIXED VERSION WITH PERSONALITY ENGINE
package com.example.chesspedagogue.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Context;

import com.example.chesspedagogue.ChessMasterRatings;
import com.example.chesspedagogue.GameHistoryManager;
import com.example.chesspedagogue.LogThrottler;
import com.example.chesspedagogue.PersonalityEngine;
import com.example.chesspedagogue.FineTunedModelManager;
import com.example.chesspedagogue.StockfishManager;
import com.example.chesspedagogue.UnifiedEvaluationSystem;
import com.example.chesspedagogue.EvaluationSystemMigration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    // 🎯 UNIFIED EVALUATION SYSTEM - Single source of truth
    private UnifiedEvaluationSystem unifiedEvaluationSystem;
    private EvaluationSystemMigration evaluationMigration;

    public GameRepository(Context context) {
        this.context = context;
        stockfishManager = new StockfishManager();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // 🎯 Initialize unified evaluation system
        initializeUnifiedEvaluationSystem();

        // FIXED: Initialize engine first, then personality engine
        initializeEngine();
        initializePersonalityEngine();
    }

    /**
     * 🎯 Initialize the UnifiedEvaluationSystem
     */
    private void initializeUnifiedEvaluationSystem() {
        try {
            unifiedEvaluationSystem = UnifiedEvaluationSystem.getInstance(context);
            evaluationMigration = new EvaluationSystemMigration(context);
            Log.d(TAG, "🎯 UnifiedEvaluationSystem initialized successfully!");
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize UnifiedEvaluationSystem", e);
        }
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

                // 🎯 Initialize unified evaluation system with engine path
                if (unifiedEvaluationSystem != null) {
                    boolean unifiedSuccess = unifiedEvaluationSystem.initializeEngine(engineFile.getAbsolutePath());
                    if (unifiedSuccess) {
                        Log.d(TAG, "🎯 UnifiedEvaluationSystem engine initialized successfully!");
                    } else {
                        Log.e(TAG, "❌ Failed to initialize UnifiedEvaluationSystem engine");
                    }
                }

                // Initialize migration helper engine
                if (evaluationMigration != null) {
                    boolean migrationSuccess = evaluationMigration.initializeEngine(engineFile.getAbsolutePath());
                    if (migrationSuccess) {
                        Log.d(TAG, "🔄 Migration helper engine initialized successfully!");
                    } else {
                        Log.e(TAG, "❌ Failed to initialize migration helper engine");
                    }
                }

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
        // 🚨 MULTIPLE LOG METHODS to ensure visibility
        android.util.Log.e("GameRepository", "🚨🚨🚨 METHOD ENTRY: calculatePersonalityMove() CALLED! 🚨🚨🚨");
        System.out.println("🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨");
        LogThrottler.force("GameRepository", "🚨🚨🚨 METHOD ENTRY: calculatePersonalityMove() CALLED! 🚨🚨🚨");
        Log.d(TAG, "🔧 DEBUG: calculatePersonalityMove called - usePersonalityEngine=" + usePersonalityEngine + ", personalityEngine=" + (personalityEngine != null ? "initialized" : "NULL"));
        LogThrottler.force("GameRepository", "🚨 CALCULATE PERSONALITY MOVE ENTRY: usePersonalityEngine=" + usePersonalityEngine + ", personalityEngine=" + (personalityEngine != null ? "initialized" : "NULL"));
        
        // ADDITIONAL DEBUG: Log thread info and stack trace
        LogThrottler.force("GameRepository", "🧵 THREAD INFO: " + Thread.currentThread().getName() + " (ID: " + Thread.currentThread().getId() + ")");
        LogThrottler.force("GameRepository", "📊 CALLBACK INFO: " + (callback != null ? "NOT NULL" : "NULL"));
        
        // Log the first few lines of stack trace for debugging
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        LogThrottler.force("GameRepository", "📍 CALLED FROM: " + (stack.length > 3 ? stack[3].toString() : "unknown"));
        
        if (!usePersonalityEngine || personalityEngine == null) {
            Log.d(TAG, "🎭 Personality engine not available - falling back to regular engine");
            Log.d(TAG, "🔧 DEBUG: usePersonalityEngine=" + usePersonalityEngine + ", personalityEngine=" + (personalityEngine != null ? "exists" : "NULL"));
            LogThrottler.force("GameRepository", "⚠️ FALLBACK TO REGULAR ENGINE: usePersonalityEngine=" + usePersonalityEngine + ", personalityEngine=" + (personalityEngine != null ? "exists" : "NULL"));
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

                // CRITICAL FIX: Add timeout mechanism to prevent hanging
                final boolean[] callbackCalled = {false};
                
                // Set up timeout fallback (10 seconds)
                mainHandler.postDelayed(() -> {
                    synchronized (callbackCalled) {
                        if (!callbackCalled[0]) {
                            callbackCalled[0] = true;
                            Log.w(TAG, "⚠️ Personality engine timed out - falling back to regular engine");
                            calculateBestMove(callback);
                        }
                    }
                }, 10000);

                // 🚨 CRITICAL DEBUG: About to call PersonalityEngine.selectPersonalityMove()
                LogThrottler.force("GameRepository", "🎯 CALLING PersonalityEngine.selectPersonalityMove() with FEN: " + currentFen.substring(0, Math.min(50, currentFen.length())));
                
                personalityEngine.selectPersonalityMove(currentFen, new PersonalityEngine.PersonalityMoveCallback() {
                    @Override
                    public void onPersonalityMoveSelected(PersonalityEngine.PersonalityMove selectedMove,
                                                          List<PersonalityEngine.PersonalityMove> allCandidates) {

                        // CRITICAL FIX: Check if we already timed out
                        synchronized (callbackCalled) {
                            if (callbackCalled[0]) {
                                Log.d(TAG, "🎭 Personality move result received but callback already called (timeout)");
                                return;
                            }
                            callbackCalled[0] = true;
                        }

                        Log.d(TAG, "🎭 PERSONALITY MOVE SELECTED: " + selectedMove);
                        LogThrottler.force("GameRepository", "✅ PERSONALITY ENGINE RESPONDED: " + (selectedMove != null ? selectedMove.toString() : "NULL"));

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
                        // CRITICAL FIX: Check if we already timed out
                        synchronized (callbackCalled) {
                            if (callbackCalled[0]) {
                                Log.d(TAG, "🎭 Personality error received but callback already called (timeout)");
                                return;
                            }
                            callbackCalled[0] = true;
                        }

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
        Log.d(TAG, String.format("🔧 DEBUG ENTRY: configurePersonalityEngine - master=%s, weight=%.2f, enabled=%s", 
                master, personalityWeight, enabled));
        Log.d(TAG, "🔧 DEBUG: Current personalityEngine state = " + (personalityEngine != null ? "initialized" : "NULL"));
        Log.d(TAG, "🔧 DEBUG: Current usePersonalityEngine = " + usePersonalityEngine);
        
        if (personalityEngine == null) {
            Log.d(TAG, "🎭 Personality engine is null, attempting to initialize...");
            initializePersonalityEngine();
            Log.d(TAG, "🔧 DEBUG: After initialization attempt, personalityEngine = " + (personalityEngine != null ? "initialized" : "still NULL"));
        }

        if (personalityEngine != null) {
            Log.d(TAG, "🔧 DEBUG: About to call personalityEngine.setCurrentMaster(" + master + ")");
            personalityEngine.setCurrentMaster(master);
            Log.d(TAG, "🔧 DEBUG: About to call personalityEngine.setPersonalityWeight(" + personalityWeight + ")");
            personalityEngine.setPersonalityWeight(personalityWeight);
            Log.d(TAG, "🔧 DEBUG: About to call personalityEngine.setPersonalityPlayEnabled(" + enabled + ")");
            personalityEngine.setPersonalityPlayEnabled(enabled);

            this.usePersonalityEngine = enabled;

            Log.d(TAG, String.format("✅ Personality engine configured successfully: master=%s, weight=%.2f, enabled=%s, usePersonalityEngine=%s",
                    master, personalityWeight, enabled, this.usePersonalityEngine));
            
            // 🔧 DEBUG: Test database immediately after configuration
            Log.d(TAG, "🔧 DEBUG: Testing database access immediately after configuration...");
            testDatabaseAccess(master);
        } else {
            Log.e(TAG, "❌ Cannot configure personality engine - initialization failed");
            Log.e(TAG, "❌ This will cause personality moves to fall back to regular engine");
        }
    }
    
    /**
     * 🔧 DEBUG: Test database access immediately after configuration
     */
    private void testDatabaseAccess(String master) {
        try {
            if (personalityEngine != null) {
                // Force a quick database test
                executorService.execute(() -> {
                    try {
                        Log.d(TAG, "🔧 DEBUG TEST: Attempting immediate database test for " + master);
                        
                        // Get current FEN to test position lookup
                        String testFEN = getCurrentFEN();
                        Log.d(TAG, "🔧 DEBUG TEST: Using FEN: " + testFEN.substring(0, Math.min(50, testFEN.length())));
                        
                        // This should trigger the PersonalityEngine's database lookup
                        personalityEngine.selectPersonalityMove(testFEN, new PersonalityEngine.PersonalityMoveCallback() {
                            @Override
                            public void onPersonalityMoveSelected(PersonalityEngine.PersonalityMove selectedMove, List<PersonalityEngine.PersonalityMove> allCandidates) {
                                Log.d(TAG, "🔧 DEBUG TEST SUCCESS: Database lookup completed, found " + allCandidates.size() + " candidates");
                                Log.d(TAG, "🔧 DEBUG TEST: Selected move: " + selectedMove.move + " (historical: " + selectedMove.isHistoricalMatch + ")");
                            }

                            @Override
                            public void onPersonalityAnalysisComplete(String analysis, String masterQuote) {
                                Log.d(TAG, "🔧 DEBUG TEST: Analysis complete - " + analysis.substring(0, Math.min(100, analysis.length())));
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "🔧 DEBUG TEST ERROR: " + errorMessage);
                            }
                        });
                        
                    } catch (Exception e) {
                        Log.e(TAG, "🔧 DEBUG TEST EXCEPTION: " + e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "🔧 DEBUG: Error in database test", e);
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
        try {
            // Try to acquire lock with timeout to prevent deadlock
            if (!engineLock.tryLock(2, TimeUnit.SECONDS)) {
                Log.e(TAG, "❌ Failed to acquire lock for move validation within timeout: " + move);
                return false;
            }
            try {
                Log.d(TAG, "🔒 Acquired lock for move validation: " + move);
                boolean result = stockfishManager.isLegalMove(move);
                Log.d(TAG, "⚖️ Move " + move + " validation result: " + result);
                return result;
            } finally {
                engineLock.unlock();
                Log.d(TAG, "🔓 Released lock for move validation: " + move);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "❌ Interrupted while waiting for lock: " + move, e);
            Thread.currentThread().interrupt();
            return false;
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
        try {
            // Try to acquire lock with timeout to prevent deadlock
            if (!engineLock.tryLock(1, TimeUnit.SECONDS)) {
                Log.w(TAG, "❌ Failed to acquire lock for getCurrentFEN - returning cached FEN");
                return cachedFEN;
            }
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
        } catch (InterruptedException e) {
            Log.e(TAG, "❌ Interrupted while waiting for lock in getCurrentFEN", e);
            Thread.currentThread().interrupt();
            return cachedFEN;
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
     * 🎯 UNIFIED SYSTEM: Thread-safe evaluation using UnifiedEvaluationSystem
     * This replaces the old fragmented evaluation approach
     */
    public void getCurrentEvaluation(EvaluationCallback callback) {
        Log.d(TAG, "🎯 UNIFIED: Starting position evaluation...");

        // Use unified evaluation system instead of direct Stockfish access
        if (unifiedEvaluationSystem != null) {
            unifiedEvaluationSystem.evaluateCurrentPosition(500, new UnifiedEvaluationSystem.EvaluationCallback() {
                @Override
                public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                    Log.d(TAG, "✅ UNIFIED: Evaluation completed: " + result.toString());
                    // Convert to legacy format for compatibility
                    callback.onEvaluationReceived(result.toStockfishResult());
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "❌ UNIFIED: Evaluation failed: " + error);
                    callback.onEvaluationError(error);
                }
            });
        } else {
            // Fallback to migration helper if unified system not available
            Log.w(TAG, "⚠️ FALLBACK: Using migration helper for evaluation");
            
            if (evaluationMigration != null) {
                evaluationMigration.getCurrentEvaluationAsync(cachedFEN, 500, result -> {
                    if (result != null) {
                        callback.onEvaluationReceived(result);
                    } else {
                        callback.onEvaluationError("Migration evaluation failed");
                    }
                });
            } else {
                // Final fallback to old system (should rarely happen)
                Log.e(TAG, "❌ No evaluation system available - using legacy fallback");
                legacyGetCurrentEvaluation(callback);
            }
        }
    }

    /**
     * 🔄 LEGACY FALLBACK: Keep old evaluation method as emergency fallback
     */
    private void legacyGetCurrentEvaluation(EvaluationCallback callback) {
        // Prevent multiple evaluations from running simultaneously
        if (isEngineProcessing.compareAndSet(false, true)) {
            executorService.execute(() -> {
                engineLock.lock();
                try {
                    Log.d(TAG, "🔄 LEGACY: Starting fallback evaluation...");

                    // Get evaluation from Stockfish (500ms should be enough for quick eval)
                    StockfishManager.EvaluationResult result = stockfishManager.getCurrentEvaluation(500);

                    // Return result on main thread
                    mainHandler.post(() -> {
                        Log.d(TAG, "✅ LEGACY: Evaluation completed: " + result.toString());
                        callback.onEvaluationReceived(result);
                    });

                } catch (Exception e) {
                    Log.e(TAG, "❌ LEGACY: Error getting evaluation", e);
                    mainHandler.post(() -> callback.onEvaluationError("Failed to get evaluation: " + e.getMessage()));
                } finally {
                    engineLock.unlock();
                    isEngineProcessing.set(false);
                    Log.d(TAG, "🔓 LEGACY: Evaluation lock released");
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
        try {
            // Try to acquire lock with timeout to prevent deadlock
            if (!engineLock.tryLock(2, TimeUnit.SECONDS)) {
                Log.e(TAG, "❌ Failed to acquire lock for applyMoves within timeout");
                return false;
            }
            try {
                Log.d(TAG, "🔄 THREAD " + Thread.currentThread().getId() + ": Applying " + (moves != null ? moves.size() : 0) + " moves");

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
            Log.d(TAG, "Applying moves: " + moves);

            // Use StockfishManager's optimized setPositionFromMoves method
            String[] moveArray = moves.toArray(new String[0]);
            boolean success = stockfishManager.setPositionFromMoves(moveArray);
            
            if (!success) {
                Log.e(TAG, "Failed to apply moves: " + moves);
                return false;
            }

            // Update the cached FEN AND verify it changed
            String previousFEN = cachedFEN;
            cachedFEN = stockfishManager.getCurrentFEN();
            
            // Verify the position actually changed (unless we're applying the same moves)
            if (moves.size() > 0 && cachedFEN.equals(previousFEN) && lastMoveCount != moves.size()) {
                Log.w(TAG, "⚠️ Position may not have updated correctly!");
                Log.w(TAG, "Previous: " + previousFEN);
                Log.w(TAG, "Current:  " + cachedFEN);
            }
            
            // Update move count after verification
            lastMoveCount = moves.size();

            Log.d(TAG, "✅ Successfully applied " + moves.size() + " moves. Final position: " + cachedFEN);
            return true;
            } finally {
                engineLock.unlock();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "❌ Interrupted while waiting for lock in applyMoves", e);
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error applying moves: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * BULLETPROOF: Single move execution with comprehensive error handling
     * This fixes the state synchronization issue Benjamin is experiencing
     */
    public boolean makeMove(String move) {
        engineLock.lock();
        try {
            Log.d(TAG, "🎯 BULLETPROOF makeMove starting: " + move);

            // Step 1: Get current game history from the authoritative source
            List<String> currentHistory = GameHistoryManager.getInstance().getCurrentGameMoves();
            Log.d(TAG, "📋 Current game history has " + currentHistory.size() + " moves");

            // Step 2: Create new history with the additional move
            List<String> newHistory = new ArrayList<>(currentHistory);
            newHistory.add(move);
            Log.d(TAG, "🔄 New history will have " + newHistory.size() + " moves");

            // Step 3: Apply the complete game from start with error recovery
            boolean success = applyCompleteGame(newHistory);

            if (success) {
                // Step 4: Verify the move was actually applied
                String newFEN = stockfishManager.getCurrentFEN();

                if (newFEN != null && !newFEN.equals(cachedFEN)) {
                    // Success! Update our state
                    cachedFEN = newFEN;
                    lastMoveCount = newHistory.size();

                    Log.d(TAG, "✅ BULLETPROOF makeMove SUCCESS!");
                    Log.d(TAG, "📍 New position: " + newFEN);
                    Log.d(TAG, "📊 Total moves: " + lastMoveCount);

                    return true;
                } else {
                    Log.e(TAG, "❌ Position verification failed after move");
                    return false;
                }
            } else {
                Log.e(TAG, "❌ Failed to apply complete game with new move");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception in BULLETPROOF makeMove", e);
            return false;
        } finally {
            engineLock.unlock();
        }
    }

    /**
     * BULLETPROOF: Apply complete game from start with multiple recovery attempts
     */
    private boolean applyCompleteGame(List<String> moves) {
        Log.d(TAG, "🔄 BULLETPROOF applying " + moves.size() + " moves from start");

        // Attempt 1: Standard application
        if (attemptGameApplication(moves, "STANDARD")) {
            return true;
        }

        Log.w(TAG, "⚠️ Standard application failed, trying recovery method 1...");

        // Attempt 2: With engine reset
        if (attemptGameApplicationWithReset(moves)) {
            return true;
        }

        Log.w(TAG, "⚠️ Recovery method 1 failed, trying recovery method 2...");

        // Attempt 3: Move-by-move with verification
        if (attemptMoveByMoveApplication(moves)) {
            return true;
        }

        Log.e(TAG, "❌ All recovery attempts failed!");
        return false;
    }

    /**
     * Attempt 1: Standard game application
     */
    private boolean attemptGameApplication(List<String> moves, String method) {
        try {
            Log.d(TAG, "🎯 Attempting " + method + " application...");

            // Ensure engine is ready
            if (!stockfishManager.waitForReady(1000)) {
                Log.e(TAG, "❌ Engine not ready for " + method + " application");
                return false;
            }

            // Build and send command
            StringBuilder command = new StringBuilder("position startpos");
            if (!moves.isEmpty()) {
                command.append(" moves");
                for (String move : moves) {
                    command.append(" ").append(move);
                }
            }

            Log.d(TAG, "📤 " + method + " command: " + command.toString());
            stockfishManager.sendCommand(command.toString());

            // Wait for processing with generous timeout
            boolean ready = stockfishManager.waitForReady(2000);

            if (ready) {
                Log.d(TAG, "✅ " + method + " application succeeded");
                return true;
            } else {
                Log.w(TAG, "⚠️ " + method + " application - engine not ready after command");
                return false;
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ IOException in " + method + " application: " + e.getMessage());
            return false;
        }
    }

    /**
     * Attempt 2: Game application with engine reset
     */
    private boolean attemptGameApplicationWithReset(List<String> moves) {
        try {
            Log.d(TAG, "🔄 Attempting application with engine reset...");

            // Reset engine state
            stockfishManager.sendCommand("ucinewgame");
            Thread.sleep(100); // Brief pause for reset

            stockfishManager.sendCommand("isready");
            if (!stockfishManager.waitForReady(2000)) {
                Log.e(TAG, "❌ Engine not ready after reset");
                return false;
            }

            // Now try standard application
            return attemptGameApplication(moves, "RESET");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in reset application: " + e.getMessage());
            return false;
        }
    }

    /**
     * Attempt 3: Move-by-move application with verification
     */
    private boolean attemptMoveByMoveApplication(List<String> moves) {
        try {
            Log.d(TAG, "🐌 Attempting move-by-move application...");

            // Start fresh
            stockfishManager.sendCommand("position startpos");
            if (!stockfishManager.waitForReady(1000)) {
                Log.e(TAG, "❌ Engine not ready for move-by-move");
                return false;
            }

            // Apply moves one by one
            for (int i = 0; i < moves.size(); i++) {
                List<String> partialMoves = moves.subList(0, i + 1);

                StringBuilder command = new StringBuilder("position startpos moves");
                for (String move : partialMoves) {
                    command.append(" ").append(move);
                }

                Log.d(TAG, "📤 Move " + (i + 1) + ": " + command.toString());
                stockfishManager.sendCommand(command.toString());

                if (!stockfishManager.waitForReady(1000)) {
                    Log.e(TAG, "❌ Engine not ready after move " + (i + 1) + ": " + moves.get(i));
                    return false;
                }
            }

            Log.d(TAG, "✅ Move-by-move application succeeded");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in move-by-move application: " + e.getMessage());
            return false;
        }
    }

    /**
     * NEW: Attempt to recover from move execution failure
     */
    private boolean attemptMoveRecovery(String move, String originalPosition) {
        Log.d(TAG, "🔄 Attempting move recovery for: " + move);

        try {
            // Reset to starting position and rebuild game state
            stockfishManager.sendCommand("position startpos");
            stockfishManager.waitForReady(500);

            // Get current game history and apply all moves including the new one
            List<String> currentMoves = GameHistoryManager.getInstance().getCurrentGameMoves();
            List<String> allMoves = new ArrayList<>(currentMoves);
            allMoves.add(move);

            // Apply all moves from the beginning
            StringBuilder moveCommand = new StringBuilder("position startpos moves");
            for (String historyMove : allMoves) {
                moveCommand.append(" ").append(historyMove);
            }

            Log.d(TAG, "🔄 Recovery command: " + moveCommand.toString());
            stockfishManager.sendCommand(moveCommand.toString());

            boolean success = stockfishManager.waitForReady(1000);

            if (success) {
                String newPosition = stockfishManager.getCurrentFEN();
                if (newPosition != null && !newPosition.equals(originalPosition)) {
                    cachedFEN = newPosition;
                    Log.d(TAG, "✅ Move recovery successful!");
                    return true;
                }
            }

            Log.e(TAG, "❌ Move recovery failed");
            return false;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error during move recovery", e);
            return false;
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
     * CRITICAL: Force stop all operations immediately to prevent ANR
     */
    public void forceStop() {
        Log.d(TAG, "🚨 FORCE STOPPING GameRepository");
        
        // Stop processing immediately
        isEngineProcessing.set(false);
        
        // Force shutdown executors
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        
        // Clear main handler
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        
        // Force stop Stockfish
        engineLock.lock();
        try {
            if (stockfishManager != null) {
                stockfishManager.forceStop();
            }
        } finally {
            engineLock.unlock();
        }
        
        // Force stop personality engine
        if (personalityEngine != null) {
            personalityEngine.forceStop();
        }
        
        Log.d(TAG, "✅ GameRepository FORCE STOPPED");
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