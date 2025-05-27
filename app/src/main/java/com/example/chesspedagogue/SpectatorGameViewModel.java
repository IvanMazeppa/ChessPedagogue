package com.example.chesspedagogue;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chesspedagogue.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🎭 DEBUGGED ViewModel for Spectator Game Mode
 * Now with detailed logging to fix the move progression issue!
 */
public class SpectatorGameViewModel extends AndroidViewModel {
    private static final String TAG = "SpectatorGameViewModel";

    private boolean isDialoguePlaying = false;
    private final Queue<Runnable> pendingDialogue = new LinkedList<>();

    private final GameRepository gameRepository;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final AIDialogueManager dialogueManager;
    private final AIvsAIGameManager gameManager;

    // Game state with better tracking
    private final MutableLiveData<String> currentFEN = new MutableLiveData<>();
    private final MutableLiveData<List<String>> moveHistory = new MutableLiveData<>();
    private final MutableLiveData<Float> currentEvaluation = new MutableLiveData<>();
    private final MutableLiveData<String> aiDialogue = new MutableLiveData<>();
    private final MutableLiveData<String> currentPlayer = new MutableLiveData<>();
    private final MutableLiveData<String> gameStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isThinking = new MutableLiveData<>();
    private final MutableLiveData<int[]> lastMove = new MutableLiveData<>();

    // Game control with better state tracking
    private boolean isPaused = false;
    private int gameSpeed = 3000;
    private String whitePlayer;
    private String blackPlayer;
    private int moveCount = 0;  // NEW: Track move count for debugging
    private boolean gameInProgress = false;  // NEW: Better game state tracking

    public SpectatorGameViewModel(Application application) {
        super(application);

        this.gameRepository = new GameRepository(application);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.dialogueManager = new AIDialogueManager(application);
        this.gameManager = new AIvsAIGameManager(application);

        // Initialize state
        resetGameState();

        Log.d(TAG, "✅ DEBUGGED SpectatorGameViewModel initialized");
    }

    private void resetGameState() {
        currentFEN.setValue("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        moveHistory.setValue(new ArrayList<>());
        currentEvaluation.setValue(0.0f);
        currentPlayer.setValue("white");
        gameStatus.setValue("ready");
        isThinking.setValue(false);
        moveCount = 0;
        gameInProgress = false;

        Log.d(TAG, "🔄 Game state reset to starting position");
    }

    public void startSpectatorGame(String whitePlayer, String blackPlayer) {
        Log.d(TAG, "🎭 STARTING SPECTATOR GAME: " + whitePlayer + " vs " + blackPlayer);

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;

        // Reset everything properly
        resetGameState();
        dialogueManager.resetConversation();

        // Initialize game manager
        gameManager.initializeGame(whitePlayer, blackPlayer);

        // Initialize game repository
        gameRepository.newGame();

        // Verify starting position
        String startingFEN = gameRepository.getCurrentFEN();
        Log.d(TAG, "📋 Game repository starting FEN: " + startingFEN);
        currentFEN.setValue(startingFEN);

        // Set up game manager callbacks FIRST
        setupGameManagerCallbacks();

        // CRITICAL FIX: Set game state to in_progress AFTER everything is set up
        this.gameInProgress = true;
        gameStatus.setValue("in_progress");

        // Generate opening dialogue
        generateOpeningDialogue();

        Log.d(TAG, "✅ Spectator game initialized - GAME IS NOW IN PROGRESS and ready for moves!");
    }

    private void setupGameManagerCallbacks() {
        Log.d(TAG, "🔧 Setting up game manager callbacks");

        gameManager.setGameCallback(new AIvsAIGameManager.GameCallback() {
            @Override
            public void onMoveCalculated(String move, String newFen, List<String> history) {
                Log.d(TAG, "🎯 MOVE CALLBACK: move=" + move + ", moveCount=" + (moveCount + 1) + ", historySize=" + history.size());
                Log.d(TAG, "📋 New FEN: " + newFen);

                mainHandler.post(() -> {
                    // CRITICAL: Update move count FIRST
                    moveCount++;

                    // Update game state
                    currentFEN.setValue(newFen);
                    moveHistory.setValue(new ArrayList<>(history));

                    // Trigger move animation
                    triggerMoveAnimation(move);

                    // Update current player based on the NEW position
                    boolean isWhiteTurn = newFen.contains(" w ");
                    String nextPlayer = isWhiteTurn ? "white" : "black";
                    currentPlayer.setValue(nextPlayer);

                    Log.d(TAG, "🔄 Updated to move " + moveCount + ", next player: " + nextPlayer);

                    // Get evaluation
                    requestEvaluation(newFen);

                    // Generate dialogue (selective)
                    generateSelectiveDialogue(move, history);

                    // CRITICAL: Schedule next move with proper state checking
                    scheduleNextMoveWithStateCheck();
                });
            }

            @Override
            public void onGameEnd(String result) {
                Log.d(TAG, "🏁 GAME END: " + result);
                mainHandler.post(() -> {
                    gameInProgress = false;
                    gameStatus.setValue(result);
                    generateEndGameDialogue(result);
                });
            }

            @Override
            public void onThinkingStateChanged(boolean thinking) {
                mainHandler.post(() -> {
                    Log.d(TAG, "🤔 Thinking state: " + thinking);
                    isThinking.setValue(thinking);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ GAME ERROR: " + error);
                mainHandler.post(() -> {
                    gameInProgress = false;
                    gameStatus.setValue("error: " + error);
                });
            }
        });

        Log.d(TAG, "✅ Game manager callbacks set up");
    }

    public void requestNextMove() {
        if (!gameInProgress || isPaused) {
            Log.d(TAG, "⏸️ Skipping move request - game not in progress or paused");
            return;
        }

        String currentStatus = gameStatus.getValue();
        if (currentStatus == null || !currentStatus.equals("in_progress")) {
            Log.d(TAG, "⏸️ Skipping move request - game status: " + currentStatus);
            return;
        }

        Log.d(TAG, "🎯 REQUESTING NEXT MOVE (move " + (moveCount + 1) + ")");
        isThinking.setValue(true);

        // Get current state
        String currentFen = currentFEN.getValue();
        List<String> history = moveHistory.getValue();

        if (currentFen == null || history == null) {
            Log.e(TAG, "❌ Invalid game state for next move - FEN or history is null");
            return;
        }

        // Determine whose turn it is
        boolean isWhiteTurn = currentFen.contains(" w ");
        String activePlayer = isWhiteTurn ? whitePlayer : blackPlayer;

        Log.d(TAG, "🎯 Move " + (moveCount + 1) + ": " + activePlayer + "'s turn");
        Log.d(TAG, "📋 Current FEN: " + currentFen);
        Log.d(TAG, "📜 Move history: " + history);

        // Request move from game manager
        gameManager.requestMove(currentFen, history, activePlayer);
    }

    private void scheduleNextMoveWithStateCheck() {
        if (!gameInProgress || isPaused) {
            Log.d(TAG, "⏸️ Not scheduling next move - game not in progress or paused");
            return;
        }

        String currentStatus = gameStatus.getValue();
        if (currentStatus == null || !currentStatus.equals("in_progress")) {
            Log.d(TAG, "⏸️ Not scheduling next move - game status: " + currentStatus);
            return;
        }

        Log.d(TAG, "⏰ Scheduling next move in " + gameSpeed + "ms...");

        mainHandler.postDelayed(() -> {
            if (gameInProgress && "in_progress".equals(gameStatus.getValue()) && !isPaused) {
                Log.d(TAG, "▶️ Timer triggered - requesting next move");
                requestNextMove();
            } else {
                Log.d(TAG, "⏸️ Timer triggered but game state changed - not requesting move");
            }
        }, gameSpeed);
    }

    private void requestEvaluation(String fen) {
        gameRepository.getEvaluationForPosition(fen, new GameRepository.EvaluationCallback() {
            @Override
            public void onEvaluationReceived(StockfishManager.EvaluationResult result) {
                mainHandler.post(() -> {
                    if (!result.isMate) {
                        currentEvaluation.setValue(result.evaluation);
                        Log.d(TAG, "📊 Evaluation updated: " + result.evaluation);
                    }
                });
            }

            @Override
            public void onEvaluationError(String errorMessage) {
                Log.w(TAG, "📊 Evaluation error: " + errorMessage);
            }
        });
    }

    /**
     * Generate opening dialogue between AI masters
     */
    private void generateOpeningDialogue() {
        Log.d(TAG, "💬 Generating opening dialogue");

        dialogueManager.generateOpeningDialogue(whitePlayer, blackPlayer,
                new AIDialogueManager.DialogueCallback() {
                    @Override
                    public void onDialogueGenerated(String speaker, String dialogue) {
                        mainHandler.post(() -> {
                            String speakerName = FineTunedModelManager.getInstance(getApplication())
                                    .getMasterDisplayName(speaker);
                            String formattedDialogue = speakerName + ": \"" + dialogue + "\"";
                            aiDialogue.setValue(formattedDialogue);
                            Log.d(TAG, "✅ Opening dialogue: " + formattedDialogue);
                        });
                    }

                    @Override
                    public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                        Log.d(TAG, "🎉 CONVERSATION! " + respondingSpeaker + " responding to: \"" + triggerStatement + "\"");
                        // You could show this in the UI if you wanted!
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Opening dialogue error: " + error);
                    }
                }
        );
    }

    /**
     * Generate dialogue about moves (much simpler than commentary)
     */
    private void generateMoveDialogue(String move, List<String> history) {
        Log.d(TAG, "💬 Generating move dialogue for: " + move + " (move " + history.size() + ")");

        // Determine which player made the move
        boolean wasWhiteMove = history.size() % 2 == 1;
        String playerWhoMoved = wasWhiteMove ? whitePlayer : blackPlayer;

        dialogueManager.generateMoveDialogue(
                move, playerWhoMoved, whitePlayer, blackPlayer, history.size(),
                new AIDialogueManager.DialogueCallback() {
                    @Override
                    public void onDialogueGenerated(String speaker, String dialogue) {
                        mainHandler.post(() -> {
                            String speakerName = FineTunedModelManager.getInstance(getApplication())
                                    .getMasterDisplayName(speaker);
                            String formattedDialogue = speakerName + ": \"" + dialogue + "\"";
                            aiDialogue.setValue(formattedDialogue);
                            Log.d(TAG, "✅ Move dialogue: " + formattedDialogue);
                        });
                    }

                    @Override
                    public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                        Log.d(TAG, "🎉 CONVERSATION! " + respondingSpeaker + " responding to: \"" + triggerStatement + "\"");
                        // This is where the magic happens - one master responding to another!
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Move dialogue error: " + error);
                        // Fallback to simple dialogue
                        String fallbackDialogue = "The game continues with interesting moves!";
                        aiDialogue.setValue(fallbackDialogue);
                    }
                }
        );
    }

    /**
     * Generate end game dialogue
     */
    private void generateEndGameDialogue(String result) {
        Log.d(TAG, "🏁 Generating end game dialogue for: " + result);

        dialogueManager.generateEndGameDialogue(
                result, whitePlayer, blackPlayer,
                new AIDialogueManager.DialogueCallback() {
                    @Override
                    public void onDialogueGenerated(String speaker, String dialogue) {
                        mainHandler.post(() -> {
                            String speakerName = FineTunedModelManager.getInstance(getApplication())
                                    .getMasterDisplayName(speaker);
                            String formattedDialogue = speakerName + ": \"" + dialogue + "\"";
                            aiDialogue.setValue(formattedDialogue);
                            Log.d(TAG, "✅ End game dialogue generated");
                        });
                    }

                    @Override
                    public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                        Log.d(TAG, "🎉 FINAL CONVERSATION! " + respondingSpeaker + " responding to: \"" + triggerStatement + "\"");
                        // Even end-game conversations!
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "End game dialogue error: " + error);
                        aiDialogue.setValue("What an incredible game between these masters!");
                    }
                }
        );
    }

    /**
     * Trigger move animation by converting UCI to board coordinates
     */
    private void triggerMoveAnimation(String move) {
        if (move == null || move.length() < 4) {
            Log.w(TAG, "Invalid move for animation: " + move);
            return;
        }

        try {
            // Convert UCI notation (e2e4) to board coordinates
            int fromCol = move.charAt(0) - 'a';
            int fromRow = 8 - Character.getNumericValue(move.charAt(1));
            int toCol = move.charAt(2) - 'a';
            int toRow = 8 - Character.getNumericValue(move.charAt(3));

            // Trigger animation
            int[] moveData = {fromRow, fromCol, toRow, toCol};
            lastMove.setValue(moveData);

            Log.d(TAG, "🎯 Move animation triggered: " + move + " -> [" + fromRow + "," + fromCol + " -> " + toRow + "," + toCol + "]");

        } catch (Exception e) {
            Log.e(TAG, "Error parsing move for animation: " + move, e);
        }
    }

    /**
     * FIXED: Generate dialogue with proper timing to prevent cutting off
     */
    private void generateSelectiveDialogue(String move, List<String> history) {
        Log.d(TAG, "🎭 Checking if move " + history.size() + " should generate dialogue");

        // Generate dialogue for:
        // 1. First few moves (opening)
        // 2. Every 10th move (periodic check-ins)
        // 3. End game (handled separately)

        if (history.size() <= 6) {
            Log.d(TAG, "💬 Opening move - generating dialogue with timing");
            scheduleDialogueWithTiming(() -> generateMoveDialogue(move, history));
        } else if (history.size() % 10 == 0) {
            Log.d(TAG, "💬 Periodic dialogue (every 10 moves)");
            scheduleDialogueWithTiming(() -> generateMoveDialogue(move, history));
        } else {
            Log.d(TAG, "🤫 Quiet move - no dialogue");
        }
    }

    /**
     * NEW: Schedule dialogue with proper timing to prevent overlap
     */
    private void scheduleDialogueWithTiming(Runnable dialogueAction) {
        if (isDialoguePlaying) {
            // Queue the dialogue for later
            pendingDialogue.offer(dialogueAction);
            Log.d(TAG, "⏸️ Dialogue queued - waiting for current speaker to finish");
        } else {
            // Start dialogue immediately
            isDialoguePlaying = true;
            dialogueAction.run();

            // Schedule cleanup and check for pending dialogue
            mainHandler.postDelayed(() -> {
                isDialoguePlaying = false;

                // Process any queued dialogue
                Runnable nextDialogue = pendingDialogue.poll();
                if (nextDialogue != null) {
                    Log.d(TAG, "🎤 Starting queued dialogue");
                    isDialoguePlaying = true;
                    nextDialogue.run();

                    // Schedule another cleanup
                    mainHandler.postDelayed(() -> {
                        isDialoguePlaying = false;
                    }, 8000); // Give 8 seconds for speech
                }
            }, 8000); // Assume 8 seconds for typical dialogue
        }
    }


    // Control methods
    public void pauseGame() {
        isPaused = true;
        gameManager.pauseGame();
        Log.d(TAG, "⏸️ Game paused");
    }

    public void resumeGame() {
        isPaused = false;
        gameManager.resumeGame();
        if (gameInProgress) {
            scheduleNextMoveWithStateCheck();
        }
        Log.d(TAG, "▶️ Game resumed");
    }

    public void setGameSpeed(int speedMs) {
        this.gameSpeed = speedMs;
        gameManager.setMoveDelay(speedMs);
        Log.d(TAG, "⚡ Game speed set to: " + speedMs + "ms");
    }

    // Getters for LiveData
    public LiveData<String> getCurrentFEN() { return currentFEN; }
    public LiveData<List<String>> getMoveHistory() { return moveHistory; }
    public LiveData<Float> getCurrentEvaluation() { return currentEvaluation; }
    public LiveData<String> getAIDialogue() { return aiDialogue; }
    public LiveData<String> getCurrentPlayer() { return currentPlayer; }
    public LiveData<String> getGameStatus() { return gameStatus; }
    public LiveData<Boolean> isThinking() { return isThinking; }
    public LiveData<int[]> getLastMove() { return lastMove; }

    public void cleanup() {
        gameInProgress = false;

        if (gameManager != null) {
            gameManager.cleanup();
        }
        if (dialogueManager != null) {
            dialogueManager.cleanup();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "🧹 DEBUGGED SpectatorGameViewModel cleaned up");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }
}