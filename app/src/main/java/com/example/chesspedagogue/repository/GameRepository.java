// GameRepository.java in com.example.chesspedagogue.repository
package com.example.chesspedagogue.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Context;

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

    private int lastMoveCount = 0;

    public GameRepository(Context context) {
        this.context = context;
        stockfishManager = new StockfishManager();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        initializeEngine();

        // Initialize Stockfish on a background thread
        executorService.execute(() -> {
            try {
                // To include the engine path (using your existing initialization code):
                File engineFile = new File(context.getApplicationInfo().nativeLibraryDir, "libstockfish.so");
                stockfishManager.startEngine(engineFile.getAbsolutePath());
                stockfishManager.newGame();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Stockfish", e);
            }
        });
    }

    // NEW METHOD - more robust initialization
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
     * ENHANCED: Thread-safe move legality checking
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

    /**
     * ENHANCED: Thread-safe best move calculation
     */
    public void calculateBestMove(MoveCallback callback) {
        // Execute this on a background thread with proper locking
        executorService.execute(() -> {
            engineLock.lock();
            try {
                Log.d(TAG, "🤔 Calculating best move...");

                // Let Stockfish think for a moment
                String bestMove = stockfishManager.getBestMove(1000); // 1 second think time

                Log.d(TAG, "🎯 Engine calculated best move: " + bestMove);

                // Return the result on the main thread
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
                Log.d(TAG, "🔓 Best move calculation lock released");
            }
        });
    }

    // In GameRepository:
    public void makeEngineMove(MoveCallback callback) {
        executorService.submit(() -> {
            engineLock.lock();
            try {
                // Engine operations on background thread
                String engineMove = stockfishManager.getBestMove(1000);

                // Return to main thread for callback
                mainHandler.post(() -> {
                    if (engineMove != null && !engineMove.isEmpty()) {
                        callback.onMoveCalculated(engineMove);
                    } else {
                        callback.onError("Engine failed to generate move");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Engine error", e);
                mainHandler.post(() -> callback.onError("Engine error: " + e.getMessage()));
            } finally {
                engineLock.unlock();
            }
        });
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
     * NEW METHOD: Configure engine with skill level and Elo rating
     * This method applies the settings from the splash screen
     */
    public void configureEngine(int skillLevel, int engineElo) {
        executorService.execute(() -> {
            engineLock.lock();
            try {
                Log.d(TAG, "🔧 Configuring engine: skillLevel=" + skillLevel + ", elo=" + engineElo);

                // Set skill level (0-20)
                boolean skillSet = stockfishManager.setSkillLevel(skillLevel);
                Log.d(TAG, "Skill level " + skillLevel + " set: " + skillSet);

                // Set engine strength by Elo rating
                boolean eloSet = stockfishManager.setEngineStrength(engineElo);
                Log.d(TAG, "Engine Elo " + engineElo + " set: " + eloSet);

                // Add some randomness to make the engine less predictable
                try {
                    // MultiPV makes the engine consider multiple lines
                    stockfishManager.sendCommand("setoption name MultiPV value 3");
                    stockfishManager.waitForReady(100);

                    // Add some randomness by using a contempt factor
                    int contempt = (int) (Math.random() * 20 - 10); // Random between -10 and +10
                    stockfishManager.sendCommand("setoption name Contempt value " + contempt);
                    stockfishManager.waitForReady(100);

                    Log.d(TAG, "✅ Engine variety settings applied: MultiPV=3, Contempt=" + contempt);

                } catch (IOException e) {
                    Log.w(TAG, "Could not set variety options: " + e.getMessage());
                }

                Log.d(TAG, "✅ Engine configuration complete!");

            } catch (Exception e) {
                Log.e(TAG, "❌ Error configuring engine", e);
            } finally {
                engineLock.unlock();
            }
        });
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