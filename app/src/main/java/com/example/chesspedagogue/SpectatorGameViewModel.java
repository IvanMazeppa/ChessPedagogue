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
 * FIXED: Complete implementation that actually starts the game!
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
    private Float lastEvaluationForEmotions = null;
    
    // CRITICAL FIX: Prevent concurrent move requests
    private boolean moveRequestInProgress = false;
    private int lastProcessedMoveCount = 0;

    public SpectatorGameViewModel(Application application) {
        super(application);

        this.gameRepository = new GameRepository(application);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.dialogueManager = new AIDialogueManager(application);
        this.gameManager = new AIvsAIGameManager(application);

        // Initialize state
        resetGameState();

        // CRITICAL: Set up game manager callbacks immediately
        setupGameManagerCallbacks();

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
        
        // CRITICAL FIX: Reset concurrency controls
        moveRequestInProgress = false;
        lastProcessedMoveCount = 0;

        Log.d(TAG, "🔄 Game state reset to starting position");
    }

    /**
     * 🎭 FIXED: Complete implementation that actually starts the game!
     */
    public void startSpectatorGame(String whitePlayer, String blackPlayer) {
        Log.d(TAG, "🎭 Starting emotional spectator game: " + whitePlayer + " vs " + blackPlayer);

        // Reset emotional state for new game
        lastEvaluationForEmotions = null;

        // Initialize dialogue manager with emotional reset
        dialogueManager.resetEmotionalState();  // 🎭 RESET emotions for new game

        // Store player information
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;

        // CRITICAL: Initialize the AI vs AI game manager
        Log.d(TAG, "🎮 Initializing AI vs AI game...");
        gameManager.initializeGame(whitePlayer, blackPlayer);

        // CRITICAL: Set game as in progress
        gameInProgress = true;
        gameStatus.setValue("in_progress");

        // Reset move count
        moveCount = 0;

        // Set initial turn (white always starts)
        currentPlayer.setValue("white");

        Log.d(TAG, "✅ Game initialized successfully!");
        Log.d(TAG, "📊 Game status set to: " + gameStatus.getValue());
        Log.d(TAG, "🎯 Ready for first move request");

        // Generate opening dialogue
        generateEnhancedOpeningDialogue();

        // Schedule the first move with a small delay to let everything initialize
        mainHandler.postDelayed(() -> {
            if (gameInProgress && "in_progress".equals(gameStatus.getValue())) {
                Log.d(TAG, "🚀 Auto-requesting first move...");
                requestNextMove();
            }
        }, 1000); // 1 second delay
    }

    private void setupGameManagerCallbacks() {
        Log.d(TAG, "🔧 Setting up enhanced game manager callbacks");

        gameManager.setGameCallback(new AIvsAIGameManager.GameCallback() {
            @Override
            public void onMoveCalculated(String move, String newFen, List<String> history) {
                Log.d(TAG, "🎯 MOVE CALLBACK: move=" + move + ", moveCount=" + (history.size()) + ", historySize=" + history.size());
                Log.d(TAG, "📜 Received history: " + history);
                Log.d(TAG, "📋 Received FEN: " + newFen);

                // CRITICAL FIX: Release move request lock immediately
                moveRequestInProgress = false;
                lastProcessedMoveCount = history.size();

                // Update game state
                currentFEN.postValue(newFen);
                moveHistory.postValue(new ArrayList<>(history));
                isThinking.postValue(false);

                // Update move count and current player - FIXED
                moveCount = history.size();

                // 🎯 THE SOLUTION: Use SpectatorGameViewModel.this to access the outer class field!
                SpectatorGameViewModel.this.currentPlayer.postValue((moveCount % 2 == 0) ? "white" : "black");

                // Request evaluation for the new position
                requestEvaluation(newFen);

                // 🎭 EMOTIONAL: Check for evaluation changes that should trigger emotional responses
                Float currentEval = currentEvaluation.getValue();
                if (shouldGenerateEmotionalDialogue(currentEval, lastEvaluationForEmotions)) {
                    Log.d(TAG, "🎭 TRIGGERING EMOTIONAL DIALOGUE due to evaluation swing!");

                    // Force emotional dialogue generation
                    generateEnhancedMoveDialogue(move, history);

                } else {
                    // 🎯 Regular dialogue check
                    if (shouldGenerateDialogue(history.size())) {
                        Log.d(TAG, "🎬 Generating regular dialogue with emotional potential");
                        generateEnhancedMoveDialogue(move, history);  // Still use enhanced version for emotional readiness
                    }
                }

                // Trigger move animation
                triggerMoveAnimation(move);

                // Continue with regular move processing - FIXED
                scheduleNextMoveWithStateCheck();
            }

            @Override
            public void onGameEnd(String result) {
                Log.d(TAG, "🏁 GAME END: " + result);
                // CRITICAL FIX: Release lock on game end
                moveRequestInProgress = false;
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
                // CRITICAL FIX: Release lock on error
                moveRequestInProgress = false;
                mainHandler.post(() -> {
                    gameInProgress = false;
                    gameStatus.setValue("error: " + error);
                });
            }
        });

        Log.d(TAG, "✅ Enhanced game manager callbacks set up");
    }

    /**
     * 🎭 Generate emotional statements for evaluation swings
     */
    private String generateEmotionalStatement(String player, String emotion, float evalChange) {
        // Quick emotional responses based on player personality and emotion
        switch (player.toLowerCase()) {
            case "tal":
                switch (emotion) {
                    case "thrilled": return "Beautiful! The position is flowering with possibilities!";
                    case "pleased": return "Ah, this is developing nicely!";
                    case "frustrated": return "Hmm, not quite what I hoped for...";
                    case "desperate": return "Time for some magic - desperate times call for brilliant measures!";
                    default: return "The position has shifted - let's see what develops.";
                }
            case "fischer":
                switch (emotion) {
                    case "thrilled": return "Perfect! This is exactly what I calculated.";
                    case "pleased": return "Good. The position improves.";
                    case "frustrated": return "This is not acceptable. I must find the best move.";
                    case "desperate": return "I need to find the most accurate defense here.";
                    default: return "The evaluation has changed significantly.";
                }
            default:
                return "The position has taken an interesting turn.";
        }
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

        // CRITICAL FIX: Prevent concurrent move requests
        if (moveRequestInProgress) {
            Log.d(TAG, "⏸️ Skipping move request - another request already in progress");
            return;
        }

        // Get current move history
        List<String> history = moveHistory.getValue();
        int currentMoveCount = (history != null) ? history.size() : 0;
        
        // Don't request the same move twice - but this should only apply when
        // we're at the same position, not when we need the next move
        // Remove this check entirely - moveRequestInProgress handles duplicates
        
        // Log what we're about to do
        if (history == null || history.size() == 0) {
            Log.d(TAG, "🚀 Requesting first move - starting game");
        } else {
            Log.d(TAG, "🔄 Requesting next move - history size: " + history.size() + ", last processed: " + lastProcessedMoveCount);
        }

        Log.d(TAG, "🎯 REQUESTING NEXT MOVE (move " + (moveCount + 1) + ")");
        moveRequestInProgress = true;
        isThinking.setValue(true);

        // Get current state
        String currentFen = currentFEN.getValue();
        Log.d(TAG, "🔍 Current FEN from LiveData: " + currentFen);

        if (currentFen == null || history == null) {
            Log.e(TAG, "❌ Invalid game state for next move - FEN or history is null");
            moveRequestInProgress = false;
            return;
        }

        // Determine whose turn it is
        boolean isWhiteTurn = currentFen.contains(" w ");
        String activePlayer = isWhiteTurn ? whitePlayer : blackPlayer;

        Log.d(TAG, "🎯 Move " + (moveCount + 1) + ": " + activePlayer + "'s turn");

        // CRITICAL DEBUG: Check gameManager state before calling
        if (gameManager == null) {
            Log.e(TAG, "❌ CRITICAL ERROR: gameManager is null!");
            moveRequestInProgress = false;
            return;
        }

        Log.d(TAG, "🔗 Calling gameManager.requestMove() with:");
        Log.d(TAG, "   📄 FEN: " + currentFen);
        Log.d(TAG, "   📜 History: " + history + " (size: " + (history != null ? history.size() : 0) + ")");
        Log.d(TAG, "   👤 Player: " + activePlayer);

        // Request move from game manager
        try {
            gameManager.requestMove(currentFen, history, activePlayer);
            Log.d(TAG, "✅ gameManager.requestMove() called successfully");
        } catch (Exception e) {
            Log.e(TAG, "❌ ERROR calling gameManager.requestMove(): " + e.getMessage(), e);
            moveRequestInProgress = false;
        }
    }

    private Runnable pendingMoveRequest = null;
    
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

        // Cancel any pending move request to prevent duplicates
        if (pendingMoveRequest != null) {
            mainHandler.removeCallbacks(pendingMoveRequest);
            Log.d(TAG, "🔄 Cancelled previous pending move request");
        }

        Log.d(TAG, "⏰ Scheduling next move in " + gameSpeed + "ms...");

        pendingMoveRequest = () -> {
            pendingMoveRequest = null; // Clear the reference
            if (gameInProgress && "in_progress".equals(gameStatus.getValue()) && !isPaused) {
                Log.d(TAG, "▶️ Timer triggered - requesting next move");
                requestNextMove();
            } else {
                Log.d(TAG, "⏸️ Timer triggered but game state changed - not requesting move");
            }
        };

        mainHandler.postDelayed(pendingMoveRequest, Math.max(gameSpeed, 2000));
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
     * 🎭 Determine if we should generate emotional dialogue based on evaluation changes
     */
    private boolean shouldGenerateEmotionalDialogue(Float currentEval, Float previousEval) {
        if (currentEval == null || previousEval == null) {
            return false;
        }

        float evalChange = Math.abs(currentEval - previousEval);

        // Generate emotional dialogue for significant evaluation swings
        if (evalChange > 0.8f) {  // Significant change
            Log.d(TAG, "🎭 SIGNIFICANT EVAL CHANGE DETECTED: " + previousEval + " → " + currentEval + " (Δ" + evalChange + ")");
            return true;
        }

        return false;
    }

    /**
     * 🎭 Enhanced dialogue checking that considers emotional triggers
     */
    private boolean shouldGenerateDialogue(int moveNumber) {
        // Always generate for significant evaluation changes (handled separately)

        // Opening moves (moves 1-12): Selective dialogue
        if (moveNumber <= 12) {
            return moveNumber % 4 == 0;  // Every 4th move in opening
        }

        // Middlegame (moves 13-40): More frequent, especially for tactical positions
        if (moveNumber <= 40) {
            return moveNumber % 6 == 0;  // Every 6th move
        }

        // Endgame (moves 40+): Less frequent but more emotional
        return moveNumber % 8 == 0;  // Every 8th move
    }

    /**
     * 📊 Enhanced evaluation update with emotional trigger detection
     */
    private void updateEvaluationWithEmotionalCheck(float newEvaluation) {
        Float previousEval = currentEvaluation.getValue();
        currentEvaluation.postValue(newEvaluation);

        // 🎭 EMOTIONAL: Check if this evaluation change should trigger emotional dialogue
        if (previousEval != null) {
            float evalChange = Math.abs(newEvaluation - previousEval);

            if (evalChange > 1.5f) {  // Major swing
                Log.d(TAG, "🎭 MAJOR EVALUATION SWING: " + previousEval + " → " + newEvaluation);

                // Determine which player this affects
                String affectedPlayer = (newEvaluation > previousEval) ? "white" : "black";
                String playerName = affectedPlayer.equals("white") ? whitePlayer : blackPlayer;

                // Trigger immediate emotional commentary
                triggerEmotionalCommentary(playerName, evalChange, newEvaluation);
            }
        }
    }

    /**
     * 🎭 Trigger immediate emotional commentary for evaluation swings
     */
    private void triggerEmotionalCommentary(String affectedPlayer, float evalChange, float currentEval) {
        Log.d(TAG, "🎭 TRIGGERING EMOTIONAL COMMENTARY for " + affectedPlayer + " (change: " + evalChange + ")");

        AIDialogueManager dialogueManager = new AIDialogueManager(getApplication());

        // Create emotional context based on evaluation change
        String emotionalContext = determineEmotionalContext(evalChange, currentEval);

        // Generate immediate emotional response
        String emotionalStatement = generateEmotionalStatement(affectedPlayer, emotionalContext, evalChange);

        if (emotionalStatement != null) {
            Log.d(TAG, "🎭 EMOTIONAL STATEMENT: " + emotionalStatement);

            // Display emotional dialogue immediately
            aiDialogue.postValue(FineTunedModelManager.getInstance(getApplication())
                    .getMasterDisplayName(affectedPlayer) + ": \"" + emotionalStatement + "\"");

            conversationSpeaker.postValue(affectedPlayer);

            // Speak with emotion
            dialogueManager.speakDialogueWithPersonalityAndEmotion(
                    affectedPlayer,
                    emotionalStatement,
                    emotionalContext,
                    currentEval
            );
        }
    }

    /**
     * 🎭 Determine emotional context from evaluation change
     */
    private String determineEmotionalContext(float evalChange, float currentEval) {
        if (evalChange > 2.0f) {
            return currentEval > 0 ? "thrilled" : "desperate";
        } else if (evalChange > 1.0f) {
            return currentEval > 0 ? "pleased" : "frustrated";
        } else {
            return currentEval > 0 ? "satisfied" : "concerned";
        }
    }

    /**
     * 🎭 Enhanced dialogue generation with emotional evaluation tracking
     */
    private void generateEnhancedMoveDialogue(String move, List<String> history) {
        try {
            // Don't generate dialogue for every move to avoid spam
            if (!shouldGenerateDialogue(history.size())) {
                return;
            }

            String whitePlayerName = whitePlayer;
            String blackPlayerName = blackPlayer;
            String playerWhoMoved = (history.size() % 2 == 1) ? whitePlayerName : blackPlayerName;

            // 🎭 CRITICAL: Get current evaluation for emotional analysis
            Float currentEval = currentEvaluation.getValue();

            Log.d(TAG, "🎭 EMOTIONAL DIALOGUE: move=" + move + ", eval=" + currentEval + ", lastEval=" + lastEvaluationForEmotions);

            // Create dialogue manager
            AIDialogueManager dialogueManager = new AIDialogueManager(getApplication());

            // 🎭 EMOTIONAL: Use the emotional dialogue method instead of regular one
            dialogueManager.generateMoveDialogueWithEmotion(
                    move,
                    playerWhoMoved,
                    whitePlayerName,
                    blackPlayerName,
                    history.size(),
                    currentEval,  // 🎭 CRITICAL: Pass evaluation for emotional analysis
                    new AIDialogueManager.DialogueCallback() {
                        @Override
                        public void onDialogueGenerated(String speaker, String dialogue) {
                            Log.d(TAG, "🎭 Enhanced dialogue from " + speaker + ": " + dialogue);

                            // Update UI with emotional dialogue
                            aiDialogue.postValue(FineTunedModelManager.getInstance(getApplication())
                                    .getMasterDisplayName(speaker) + ": \"" + dialogue + "\"");

                            conversationSpeaker.postValue(speaker);
                        }

                        @Override
                        public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                            Log.d(TAG, "🎉 CONVERSATION STARTED! " + respondingSpeaker + " responding to: \"" + triggerStatement + "\"");
                            conversationActive.postValue(true);
                        }

                        @Override
                        public void onConversationComplete(String finalSpeaker, String finalStatement) {
                            Log.d(TAG, "✅ Conversation completed with " + finalSpeaker);
                            conversationActive.postValue(false);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Enhanced dialogue error: " + error);
                        }
                    }
            );

            // 🎭 UPDATE: Store current evaluation for next emotional comparison
            lastEvaluationForEmotions = currentEval;

        } catch (Exception e) {
            Log.e(TAG, "Error generating enhanced dialogue", e);
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