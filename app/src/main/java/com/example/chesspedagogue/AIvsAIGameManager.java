package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chesspedagogue.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🤖 AI vs AI Game Manager - FIXED POSITION STATE VERSION
 * Now properly maintains game state between moves!
 */
public class AIvsAIGameManager {
    private static final String TAG = "AIvsAIGameManager";

    private final Context context;
    private final GameRepository gameRepository;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    // Game state - ENHANCED with better tracking
    private String whitePlayer;
    private String blackPlayer;
    private List<String> gameHistory;
    private boolean isPaused = false;
    private int moveDelay = 3000;
    private String lastValidFEN;  // NEW: Track the last known good position

    // Callbacks
    private GameCallback gameCallback;

    public interface GameCallback {
        void onMoveCalculated(String move, String newFen, List<String> history);
        void onGameEnd(String result);
        void onThinkingStateChanged(boolean thinking);
        void onError(String error);
    }

    public AIvsAIGameManager(Context context) {
        this.context = context.getApplicationContext();
        this.gameRepository = new GameRepository(context);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gameHistory = new ArrayList<>();

        Log.d(TAG, "✅ FIXED AIvsAIGameManager initialized with position state tracking");
    }

    public void initializeGame(String whitePlayer, String blackPlayer) {
        Log.d(TAG, "🎮 Initializing FIXED AI vs AI game: " + whitePlayer + " vs " + blackPlayer);

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameHistory = new ArrayList<>();

        // Reset game repository using existing method
        gameRepository.newGame();

        // CRITICAL FIX: Store the starting position
        this.lastValidFEN = gameRepository.getCurrentFEN();
        Log.d(TAG, "🎯 Starting position stored: " + lastValidFEN);

        Log.d(TAG, "✅ FIXED Game initialized with proper state tracking");
    }

    public void requestMove(String fen, List<String> history, String activePlayer) {
        if (isPaused) {
            Log.d(TAG, "Game is paused, skipping move request");
            return;
        }

        Log.d(TAG, "🎯 FIXED: Requesting move from " + activePlayer + " (move " + (history.size() + 1) + ")");
        Log.d(TAG, "📋 Input FEN: " + fen);
        Log.d(TAG, "📜 Input history: " + history);

        if (gameCallback != null) {
            gameCallback.onThinkingStateChanged(true);
        }

        // CRITICAL FIX: Make a defensive copy and ensure state consistency
        this.gameHistory = new ArrayList<>(history);

        executorService.execute(() -> {
            try {
                // STEP 1: Ensure the GameRepository is in the correct state
                Log.d(TAG, "🔄 FIXED: Synchronizing game repository state...");

                if (gameHistory.isEmpty()) {
                    // Starting position - reset to beginning
                    gameRepository.newGame();
                    lastValidFEN = gameRepository.getCurrentFEN();
                    Log.d(TAG, "🆕 Reset to starting position: " + lastValidFEN);
                } else {
                    // Apply ALL moves from the beginning to ensure consistency
                    boolean positionSet = gameRepository.applyMoves(gameHistory);
                    if (positionSet) {
                        lastValidFEN = gameRepository.getCurrentFEN();
                        Log.d(TAG, "✅ Applied " + gameHistory.size() + " moves, position: " + lastValidFEN);
                    } else {
                        Log.e(TAG, "❌ Failed to apply move history - falling back to last valid position");
                        if (lastValidFEN != null) {
                            gameRepository.setPositionFromMoves(lastValidFEN);
                        }
                    }
                }

                // STEP 2: Configure personality engine AND historical rating for the active player
                Log.d(TAG, "🎭 Configuring personality for: " + activePlayer);

                // NEW: Use historical peak rating for authentic strength!
                int peakRating = ChessMasterRatings.getPeakRating(activePlayer);
                Log.d(TAG, "🏆 Setting " + activePlayer + " to peak rating: " + peakRating);

                // Configure engine with historical strength
                gameRepository.configureEngineForMaster(activePlayer);

                // Configure personality with enhanced weight for historical accuracy
                float personalityWeight = calculatePersonalityWeight(activePlayer, peakRating);
                gameRepository.configurePersonalityEngine(activePlayer, personalityWeight, true);

                // STEP 3: Calculate the move using the personality engine
                gameRepository.calculatePersonalityMove(new GameRepository.MoveCallback() {
                    @Override
                    public void onMoveCalculated(String move) {
                        Log.d(TAG, "🎯 " + activePlayer + " calculated move: " + move);

                        if (move != null && !move.trim().isEmpty()) {
                            applyMoveWithProperStateTracking(move);
                        } else {
                            Log.e(TAG, "Empty move from personality engine");
                            handleMoveError("Empty move calculated");
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Personality engine error: " + errorMessage);
                        // Fall back to regular engine
                        calculateFallbackMove();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error in move calculation", e);
                handleMoveError(e.getMessage());
            }
        });
    }

    /**
     * CRITICAL FIX: Apply move with proper state tracking
     */
    private void applyMoveWithProperStateTracking(String move) {
        try {
            Log.d(TAG, "🎯 FIXED: Applying move with state tracking: " + move);

            // STEP 1: Add the move to our history FIRST
            List<String> newHistory = new ArrayList<>(gameHistory);
            newHistory.add(move);

            // STEP 2: Apply the COMPLETE history to ensure consistency
            boolean success = gameRepository.applyMoves(newHistory);

            if (success) {
                // STEP 3: Get and verify the new position
                String newFen = gameRepository.getCurrentFEN();

                if (newFen != null && !newFen.equals(lastValidFEN)) {
                    // Success! Update our state tracking
                    gameHistory = newHistory;
                    lastValidFEN = newFen;

                    Log.d(TAG, "✅ FIXED: Move applied successfully!");
                    Log.d(TAG, "📋 New FEN: " + newFen);
                    Log.d(TAG, "📜 New history: " + gameHistory);

                    // Check for game end
                    if (checkGameEnd(newFen)) {
                        return; // Game ended, callback already called
                    }

                    // Notify callback on main thread
                    mainHandler.post(() -> {
                        if (gameCallback != null) {
                            gameCallback.onThinkingStateChanged(false);
                            gameCallback.onMoveCalculated(move, newFen, new ArrayList<>(gameHistory));
                        }
                    });

                } else {
                    Log.e(TAG, "❌ Position didn't change after move - likely failed to apply");
                    handleMoveError("Position state didn't update after move: " + move);
                }

            } else {
                Log.e(TAG, "❌ Failed to apply complete move history");
                handleMoveError("Failed to apply move: " + move);
            }

        } catch (Exception e) {
            Log.e(TAG, "💥 Exception applying move: " + move, e);
            handleMoveError("Exception applying move: " + e.getMessage());
        }
    }

    private void calculateFallbackMove() {
        Log.d(TAG, "🔄 Using fallback move calculation");

        gameRepository.calculateBestMove(new GameRepository.MoveCallback() {
            @Override
            public void onMoveCalculated(String move) {
                if (move != null && !move.trim().isEmpty()) {
                    Log.d(TAG, "✅ Fallback move calculated: " + move);
                    applyMoveWithProperStateTracking(move);
                } else {
                    handleMoveError("Empty fallback move");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Fallback move error: " + errorMessage);
                handleMoveError(errorMessage);
            }
        });
    }

    /**
     * NEW: Calculate personality weight based on historical rating and style
     */
    private float calculatePersonalityWeight(String player, int peakRating) {
        // Higher rated players can afford more personality (they're strong enough)
        // Lower rated players need more engine help for accuracy

        float baseWeight;
        if (peakRating >= 2800) {
            baseWeight = 0.4f; // Strong masters can be more expressive
        } else if (peakRating >= 2700) {
            baseWeight = 0.35f; // Good balance for strong players
        } else if (peakRating >= 2600) {
            baseWeight = 0.3f; // Standard weight
        } else {
            baseWeight = 0.25f; // More engine help for weaker players
        }

        // Adjust for individual playing styles
        switch (player.toLowerCase()) {
            case "tal":
                // Tal was known for intuitive, creative play - higher personality
                return Math.min(0.5f, baseWeight + 0.1f);

            case "fischer":
                // Fischer was precise and calculated - balanced approach
                return baseWeight;

            case "kasparov":
                // Kasparov was aggressive and dynamic - higher personality
                return Math.min(0.45f, baseWeight + 0.05f);

            case "karpov":
                // Karpov was positional and precise - slightly lower personality
                return Math.max(0.2f, baseWeight - 0.05f);

            case "kramnik":
                // Kramnik was solid and technical - engine-focused
                return Math.max(0.2f, baseWeight - 0.1f);

            default:
                return baseWeight;
        }
    }

    private boolean checkGameEnd(String fen) {
        try {
            // Use existing GameRepository methods
            if (gameRepository.isCheckmate()) {
                boolean isWhiteTurn = fen.contains(" w ");
                String winner = isWhiteTurn ? blackPlayer : whitePlayer;
                String result = "Checkmate! " + FineTunedModelManager.getInstance(context).getMasterDisplayName(winner) + " wins!";

                mainHandler.post(() -> {
                    if (gameCallback != null) {
                        gameCallback.onThinkingStateChanged(false);
                        gameCallback.onGameEnd(result);
                    }
                });
                return true;

            } else if (gameRepository.isStalemate()) {
                String result = "Stalemate! The game is drawn.";

                mainHandler.post(() -> {
                    if (gameCallback != null) {
                        gameCallback.onThinkingStateChanged(false);
                        gameCallback.onGameEnd(result);
                    }
                });
                return true;

            } else if (gameHistory.size() >= 100) {
                // Long game draw rule
                String result = "Draw by length! What an epic battle!";

                mainHandler.post(() -> {
                    if (gameCallback != null) {
                        gameCallback.onThinkingStateChanged(false);
                        gameCallback.onGameEnd(result);
                    }
                });
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error checking game end", e);
        }

        return false;
    }

    private void handleMoveError(String error) {
        Log.e(TAG, "Move calculation error: " + error);

        mainHandler.post(() -> {
            if (gameCallback != null) {
                gameCallback.onThinkingStateChanged(false);
                gameCallback.onError(error);
            }
        });
    }

    // Control methods
    public void pauseGame() {
        isPaused = true;
        Log.d(TAG, "⏸️ AI vs AI game paused");
    }

    public void resumeGame() {
        isPaused = false;
        Log.d(TAG, "▶️ AI vs AI game resumed");
    }

    public void setMoveDelay(int delayMs) {
        this.moveDelay = delayMs;
        Log.d(TAG, "⚡ Move delay set to: " + delayMs + "ms");
    }

    public void setGameCallback(GameCallback callback) {
        this.gameCallback = callback;
    }

    public void cleanup() {
        isPaused = true;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (gameRepository != null) {
            gameRepository.cleanup();
        }
        Log.d(TAG, "🧹 FIXED AIvsAIGameManager cleaned up");
    }
}