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
 * 🎭 ENHANCED ViewModel for Spectator Game Mode with Natural Conversations
 * Now with flowing dialogue between chess masters!
 */
public class SpectatorGameViewModel extends AndroidViewModel {
    private static final String TAG = "SpectatorGameViewModel";

    // Conversation management - ENHANCED
    private boolean isDialoguePlaying = false;
    private final Queue<Runnable> pendingDialogue = new LinkedList<>();
    private String currentConversationSpeaker = "";
    private long lastDialogueTime = 0;
    private static final long MIN_DIALOGUE_INTERVAL = 3000; // 3 seconds between utterances

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

    // ENHANCED: Conversation tracking
    private final MutableLiveData<String> conversationSpeaker = new MutableLiveData<>();
    private final MutableLiveData<Boolean> conversationActive = new MutableLiveData<>();

    // Game control with better state tracking
    private boolean isPaused = false;
    private int gameSpeed = 3000;
    private String whitePlayer;
    private String blackPlayer;
    private int moveCount = 0;
    private boolean gameInProgress = false;

    public SpectatorGameViewModel(Application application) {
        super(application);

        this.gameRepository = new GameRepository(application);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.dialogueManager = new AIDialogueManager(application);
        this.gameManager = new AIvsAIGameManager(application);

        // Initialize state
        resetGameState();

        Log.d(TAG, "✅ ENHANCED SpectatorGameViewModel initialized with conversation support!");
    }

    private void resetGameState() {
        currentFEN.setValue("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        moveHistory.setValue(new ArrayList<>());
        currentEvaluation.setValue(0.0f);
        currentPlayer.setValue("white");
        gameStatus.setValue("ready");
        isThinking.setValue(false);
        conversationActive.setValue(false);
        moveCount = 0;
        gameInProgress = false;

        Log.d(TAG, "🔄 Game state reset to starting position");
    }

    public void startSpectatorGame(String whitePlayer, String blackPlayer) {
        Log.d(TAG, "🎭 STARTING ENHANCED SPECTATOR GAME: " + whitePlayer + " vs " + blackPlayer);

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

        // Set game state to in_progress AFTER everything is set up
        this.gameInProgress = true;
        gameStatus.setValue("in_progress");

        // ENHANCED: Generate opening dialogue with conversation support
        generateEnhancedOpeningDialogue();

        Log.d(TAG, "✅ Enhanced spectator game initialized with conversation system!");
    }

    private void setupGameManagerCallbacks() {
        Log.d(TAG, "🔧 Setting up enhanced game manager callbacks");

        gameManager.setGameCallback(new AIvsAIGameManager.GameCallback() {
            @Override
            public void onMoveCalculated(String move, String newFen, List<String> history) {
                Log.d(TAG, "🎯 MOVE CALLBACK: move=" + move + ", moveCount=" + (moveCount + 1) + ", historySize=" + history.size());

                mainHandler.post(() -> {
                    // Update move count FIRST
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

                    // ENHANCED: Generate dialogue with conversation potential
                    generateEnhancedMoveDialogue(move, history);

                    // Schedule next move with proper state checking
                    scheduleNextMoveWithStateCheck();
                });
            }

            @Override
            public void onGameEnd(String result) {
                Log.d(TAG, "🏁 GAME END: " + result);
                mainHandler.post(() -> {
                    gameInProgress = false;
                    gameStatus.setValue(result);
                    generateEnhancedEndGameDialogue(result);
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

        Log.d(TAG, "✅ Enhanced game manager callbacks set up");
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
     * 🎬 ENHANCED: Generate opening dialogue with conversation support
     */
    private void generateEnhancedOpeningDialogue() {
        Log.d(TAG, "🎬 Generating enhanced opening dialogue with conversation potential");

        dialogueManager.generateOpeningDialogue(whitePlayer, blackPlayer,
                new EnhancedDialogueCallback("opening"));
    }

    /**
     * 🎯 ENHANCED: Generate move dialogue with better frequency control
     */
    private void generateEnhancedMoveDialogue(String move, List<String> history) {
        Log.d(TAG, "🎯 Checking if move " + history.size() + " should generate enhanced dialogue");

        // More selective dialogue generation to prevent queuing issues
        if (history.size() <= 6) {
            // Opening moves - but not every single one
            if (history.size() % 2 == 0) { // Every other move in opening
                Log.d(TAG, "🎬 Opening move - generating selective dialogue");
                scheduleEnhancedDialogue(() -> generateMoveDialogueWithConversation(move, history));
            }
        } else if (history.size() % 12 == 0) { // Every 12th move instead of 8th
            Log.d(TAG, "🎬 Periodic dialogue (every 12 moves) with conversation potential");
            scheduleEnhancedDialogue(() -> generateMoveDialogueWithConversation(move, history));
        } else if (isInterestingMove(move)) { // Add logic for interesting moves
            Log.d(TAG, "🎬 Interesting move detected - generating dialogue");
            scheduleEnhancedDialogue(() -> generateMoveDialogueWithConversation(move, history));
        } else {
            Log.d(TAG, "🤫 Quiet move - no dialogue");
        }
    }

    /**
     * 🎯 NEW: Detect if a move is worth commenting on
     */
    private boolean isInterestingMove(String move) {
        // Simple heuristics for interesting moves
        if (move == null || move.length() < 4) return false;

        // Castling is always interesting
        if (move.equals("e1g1") || move.equals("e1c1") || move.equals("e8g8") || move.equals("e8c8")) {
            return true;
        }

        // Captures (if we can detect them - this is simplified)
        // You could enhance this with proper move parsing

        return false; // Default to not interesting
    }

    /**
     * 🎬 Generate move dialogue with conversation potential
     */
    private void generateMoveDialogueWithConversation(String move, List<String> history) {
        // Determine which player made the move
        boolean wasWhiteMove = history.size() % 2 == 1;
        String playerWhoMoved = wasWhiteMove ? whitePlayer : blackPlayer;

        dialogueManager.generateMoveDialogue(
                move, playerWhoMoved, whitePlayer, blackPlayer, history.size(),
                new EnhancedDialogueCallback("move_" + history.size()));
    }

    /**
     * 🏁 ENHANCED: Generate end game dialogue with conversation
     */
    private void generateEnhancedEndGameDialogue(String result) {
        Log.d(TAG, "🏁 Generating enhanced end game dialogue for: " + result);

        dialogueManager.generateEndGameDialogue(
                result, whitePlayer, blackPlayer,
                new EnhancedDialogueCallback("endgame"));
    }

    /**
     * 🎭 ENHANCED: Dialogue callback that supports conversations
     */
    private class EnhancedDialogueCallback implements AIDialogueManager.DialogueCallback {
        private final String context;

        public EnhancedDialogueCallback(String context) {
            this.context = context;
        }

        @Override
        public void onDialogueGenerated(String speaker, String dialogue) {
            Log.d(TAG, "🎭 Enhanced dialogue from " + speaker + ": " + dialogue);

            // Update the UI with the dialogue
            String speakerName = FineTunedModelManager.getInstance(getApplication())
                    .getMasterDisplayName(speaker);
            String formattedDialogue = speakerName + ": \"" + dialogue + "\"";

            aiDialogue.setValue(formattedDialogue);
            conversationSpeaker.setValue(speakerName);

            // Update conversation state
            currentConversationSpeaker = speaker;
            lastDialogueTime = System.currentTimeMillis();

            Log.d(TAG, "✅ Enhanced dialogue displayed: " + formattedDialogue);
        }

        @Override
        public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
            Log.d(TAG, "🎉 CONVERSATION STARTED! " + respondingSpeaker + " responding to: \"" + triggerStatement + "\"");

            // Update conversation state
            conversationActive.setValue(true);

            // You could show a special UI indicator here
            String responderName = FineTunedModelManager.getInstance(getApplication())
                    .getMasterDisplayName(respondingSpeaker);

            Log.d(TAG, "💬 " + responderName + " is responding to the conversation...");
        }

        @Override
        public void onConversationComplete(String finalSpeaker, String finalStatement) {
            Log.d(TAG, "🎭 CONVERSATION COMPLETED with " + finalSpeaker + ": " + finalStatement);

            // Update conversation state
            conversationActive.setValue(false);

            Log.d(TAG, "✅ Conversation has ended naturally");
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, "❌ Enhanced dialogue error (" + context + "): " + error);

            // Fallback dialogue
            String fallbackDialogue = "The chess masters continue their fascinating game!";
            aiDialogue.setValue(fallbackDialogue);
            conversationActive.setValue(false);
        }
    }

    /**
     * 🎬 ENHANCED: Schedule dialogue with improved timing for conversations
     */
    private void scheduleEnhancedDialogue(Runnable dialogueAction) {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastDialogue = currentTime - lastDialogueTime;

        if (timeSinceLastDialogue < MIN_DIALOGUE_INTERVAL) {
            // Queue the dialogue for later
            pendingDialogue.offer(dialogueAction);
            Log.d(TAG, "⏸️ Enhanced dialogue queued - waiting for proper timing");

            // Schedule it for when the interval has passed
            long delay = MIN_DIALOGUE_INTERVAL - timeSinceLastDialogue;
            mainHandler.postDelayed(() -> {
                Runnable nextDialogue = pendingDialogue.poll();
                if (nextDialogue != null) {
                    nextDialogue.run();
                }
            }, delay);

        } else {
            // Execute immediately
            dialogueAction.run();
        }
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

    // ENHANCED: New getters for conversation state
    public LiveData<String> getConversationSpeaker() { return conversationSpeaker; }
    public LiveData<Boolean> isConversationActive() { return conversationActive; }

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
        Log.d(TAG, "🧹 ENHANCED SpectatorGameViewModel cleaned up");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }
}