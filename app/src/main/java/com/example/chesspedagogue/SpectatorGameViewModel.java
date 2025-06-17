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
    private final SpectatorConversationOrchestrator conversationOrchestrator;
    private final AIvsAIGameManager gameManager;
    
    // 🎭 EMOTIONAL CONTEXT: Inter-master awareness system
    private EmotionalContext emotionalContext;

    // Game state with better tracking
    private final MutableLiveData<String> currentFEN = new MutableLiveData<>();
    private final MutableLiveData<List<String>> moveHistory = new MutableLiveData<>();
    private final MutableLiveData<Float> currentEvaluation = new MutableLiveData<>();
    protected final MutableLiveData<String> aiDialogue = new MutableLiveData<>();
    private final MutableLiveData<String> currentPlayer = new MutableLiveData<>();
    protected final MutableLiveData<String> gameStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isThinking = new MutableLiveData<>();
    private final MutableLiveData<int[]> lastMove = new MutableLiveData<>();

    // ENHANCED: Conversation tracking
    protected final MutableLiveData<String> conversationSpeaker = new MutableLiveData<>();
    protected final MutableLiveData<Boolean> conversationActive = new MutableLiveData<>();

    // Game control with better state tracking
    private boolean isPaused = false;
    private int gameSpeed = 3000;
    protected String whitePlayer;
    protected String blackPlayer;
    private int moveCount = 0;
    private boolean gameInProgress = false;
    private Float lastEvaluationForEmotions = null;
    
    // 🎭 CONVERSATION COOLDOWN: Prevent API spam while allowing emergent behavior
    private long lastConversationTime = 0;
    private static final long MIN_CONVERSATION_COOLDOWN = 8000; // 8 seconds between any conversations
    private boolean conversationInProgress = false; // Prevent overlapping conversations
    
    // CRITICAL FIX: Prevent concurrent move requests
    private boolean moveRequestInProgress = false;
    private int lastProcessedMoveCount = 0;

    public SpectatorGameViewModel(Application application) {
        super(application);

        this.gameRepository = new GameRepository(application);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.conversationOrchestrator = SpectatorConversationOrchestrator.getInstance(application);
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

        // Initialize conversation orchestrator for new game
        // Note: SpectatorConversationOrchestrator handles emotional state internally

        // Store player information
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        
        // 🎭 INITIALIZE EMOTIONAL CONTEXT for inter-master awareness
        this.emotionalContext = new EmotionalContext(whitePlayer, blackPlayer);
        Log.d(TAG, "🎭 EmotionalContext initialized for " + whitePlayer + " vs " + blackPlayer);

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

        // Schedule opening dialogue AFTER game initialization to ensure proper state
        mainHandler.postDelayed(() -> {
            if (gameInProgress && "in_progress".equals(gameStatus.getValue())) {
                Log.d(TAG, "🎬 Generating opening dialogue now that game is initialized...");
                lastConversationTime = System.currentTimeMillis(); // Set opening as first conversation
                generateEnhancedOpeningDialogue();
            }
        }, 500); // Short delay to ensure game state is ready

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
                long currentTime = System.currentTimeMillis();
                boolean cooldownPassed = (currentTime - lastConversationTime) >= MIN_CONVERSATION_COOLDOWN;
                boolean canStartConversation = cooldownPassed && !conversationInProgress;
                
                if (shouldGenerateEmotionalDialogue(currentEval, lastEvaluationForEmotions)) {
                    if (canStartConversation) {
                        Log.d(TAG, "🎭 TRIGGERING EMOTIONAL DIALOGUE due to evaluation swing!");
                        lastConversationTime = currentTime;
                        conversationInProgress = true;
                        
                        // Force emotional dialogue generation for emergent behavior
                        generateEnhancedMoveDialogue(move, history);
                    } else if (conversationInProgress) {
                        Log.d(TAG, "🎭 EMOTIONAL DIALOGUE BLOCKED: Conversation already in progress");
                    } else {
                        long timeLeft = MIN_CONVERSATION_COOLDOWN - (currentTime - lastConversationTime);
                        Log.d(TAG, "🎭 EMOTIONAL DIALOGUE COOLDOWN: " + timeLeft + "ms remaining");
                    }

                } else {
                    // 🎯 Regular dialogue check (also respects cooldown and non-overlap)
                    if (shouldGenerateDialogue(history.size()) && canStartConversation) {
                        Log.d(TAG, "🎬 Generating regular dialogue with emotional potential");
                        lastConversationTime = currentTime;
                        conversationInProgress = true;
                        generateEnhancedMoveDialogue(move, history);
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
                        float previousEval = (lastEvaluationForEmotions != null) ? lastEvaluationForEmotions : 0.0f;
                        currentEvaluation.setValue(result.evaluation);
                        Log.d(TAG, "📊 Evaluation updated: " + result.evaluation);
                        
                        // 🚀 NEW: Update emotional momentum system
                        try {
                            Phase2EmotionalIntegrationBridge phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(getApplication());
                            String currentPlayerName = getCurrentPlayerName();
                            
                            if (phase2Bridge != null && currentPlayerName != null) {
                                // Update game progression momentum
                                phase2Bridge.processGameProgression(currentPlayerName, moveCount, result.evaluation, previousEval);
                                
                                // Update evaluation-based emotions
                                String gamePhase = determineGamePhase(moveCount);
                                phase2Bridge.processEvaluationChange(currentPlayerName, result.evaluation, previousEval, gamePhase);
                                
                                Log.d(TAG, "🚀 Momentum systems updated for " + currentPlayerName + 
                                      " (move " + moveCount + ", eval: " + previousEval + " -> " + result.evaluation + ")");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error updating momentum systems", e);
                        }
                        
                        // Update last evaluation for next comparison
                        lastEvaluationForEmotions = result.evaluation;
                        
                        // CRITICAL FIX: Feed evaluation data to EvaluationTracker for emotional intelligence
                        try {
                            EvaluationTracker evaluationTracker = EvaluationTracker.getInstance(getApplication());
                            String currentFen = currentFEN.getValue();
                            List<String> history = moveHistory.getValue();
                            String lastMove = (history != null && !history.isEmpty()) ? 
                                history.get(history.size() - 1) : null;
                            
                            Log.d(TAG, "🎭 FEEDING EVALUATION TO TRACKER: eval=" + result.evaluation + ", move=" + lastMove);
                            evaluationTracker.trackEvaluation(result, currentFen, lastMove);
                            Log.d(TAG, "✅ EvaluationTracker.trackEvaluation() called successfully");
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error feeding evaluation to tracker", e);
                        }
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
    protected void generateEnhancedOpeningDialogue() {
        Log.d(TAG, "🎬 Generating enhanced opening dialogue with conversation potential");
        Log.d(TAG, "🔍 White Player: " + whitePlayer + ", Black Player: " + blackPlayer);
        Log.d(TAG, "🎮 Game in progress: " + gameInProgress + ", Status: " + gameStatus.getValue());

        // Check if we have valid players
        if (whitePlayer == null || blackPlayer == null) {
            Log.e(TAG, "❌ Cannot generate opening dialogue - players not set!");
            return;
        }

        // Check conversation orchestrator
        if (conversationOrchestrator == null) {
            Log.e(TAG, "❌ Cannot generate opening dialogue - conversation orchestrator is null!");
            return;
        }

        // FIXED: Use proper game context with position for opening dialogue
        String currentFen = currentFEN.getValue();
        String gameContext = String.format("Opening game between %s and %s\nPOSITION: %s\nMove number: 1", 
            whitePlayer, blackPlayer, currentFen != null ? currentFen : "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        
        Log.d(TAG, "🎭 About to call conversationOrchestrator.startConversation with context:");
        Log.d(TAG, "   📄 Game Context: " + gameContext.substring(0, Math.min(100, gameContext.length())) + "...");
        
        try {
            conversationOrchestrator.startConversation(
                    "opening",
                    whitePlayer,
                    blackPlayer,
                    gameContext,
                    new EnhancedConversationCallback("opening"));
            Log.d(TAG, "✅ Opening conversation startConversation() called successfully");
            
            // FALLBACK: If no conversation starts within 10 seconds, try a simple dialogue
            mainHandler.postDelayed(() -> {
                String currentDialogue = aiDialogue.getValue();
                if (currentDialogue == null || currentDialogue.isEmpty()) {
                    Log.w(TAG, "⚠️ FALLBACK: No opening conversation detected, generating simple greeting");
                    generateSimpleFallbackGreeting();
                }
            }, 10000); // 10 second timeout
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error calling startConversation(): " + e.getMessage(), e);
            // Immediate fallback
            generateSimpleFallbackGreeting();
        }
    }

    /**
     * 🚀 NEW: Get current player name for momentum system
     */
    private String getCurrentPlayerName() {
        String currentPlayerColor = currentPlayer.getValue();
        if ("white".equals(currentPlayerColor)) {
            return whitePlayer;
        } else if ("black".equals(currentPlayerColor)) {
            return blackPlayer;
        }
        return null;
    }
    
    /**
     * 🚀 NEW: Determine game phase for emotional context
     */
    private String determineGamePhase(int moveCount) {
        if (moveCount <= 12) {
            return "opening";
        } else if (moveCount <= 40) {
            return "middlegame";
        } else if (moveCount <= 60) {
            return "endgame";
        } else {
            return "endgame_critical";
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

    /**
     * 🏁 ENHANCED: Generate end game dialogue with conversation
     */
    private void generateEnhancedEndGameDialogue(String result) {
        Log.d(TAG, "🏁 Generating enhanced end game dialogue for: " + result);

        // ENHANCED: Create detailed context that explicitly identifies winner and loser roles
        String currentFen = currentFEN.getValue();
        List<String> history = moveHistory.getValue();
        
        // Parse the game result to determine winner and specific player roles
        String winnerContext = parseGameResultForContext(result, whitePlayer, blackPlayer);
        
        String gameContext = String.format("GAME RESULT: %s\n\nPLAYER ROLES:\n%s\n\nFINAL_POSITION: %s\nTotal moves: %d\n\nThis is your post-game conversation. React according to whether you won or lost.", 
            result, 
            winnerContext,
            currentFen != null ? currentFen : "unknown", 
            history != null ? history.size() : 0);
        
        Log.d(TAG, "🏁 Enhanced endgame context: " + gameContext);
        
        conversationOrchestrator.startConversation(
                "endgame",
                whitePlayer,
                blackPlayer,
                gameContext,
                new EnhancedConversationCallback("endgame"));
    }
    
    /**
     * 🏁 Parse game result to create explicit winner/loser context for each player
     */
    private String parseGameResultForContext(String result, String whitePlayer, String blackPlayer) {
        StringBuilder context = new StringBuilder();
        
        // Determine winner from result string
        String winner = null;
        String loser = null;
        String endType = "unknown";
        
        if (result.toLowerCase().contains("checkmate")) {
            endType = "checkmate";
            // Parse winner from result like "Checkmate! Alekhine wins!"
            if (result.contains(FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(whitePlayer))) {
                winner = whitePlayer;
                loser = blackPlayer;
            } else if (result.contains(FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(blackPlayer))) {
                winner = blackPlayer;
                loser = whitePlayer;
            }
        } else if (result.toLowerCase().contains("resigns") || result.toLowerCase().contains("resignation")) {
            endType = "resignation";
            // Parse from resignation messages like "Carlsen resigns. Alekhine wins by resignation!"
            String whiteDisplayName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(whitePlayer);
            String blackDisplayName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(blackPlayer);
            
            if (result.contains(whiteDisplayName + " resigns") || 
                (result.contains("resigns") && result.contains(blackDisplayName + " wins"))) {
                winner = blackPlayer;
                loser = whitePlayer;
            } else if (result.contains(blackDisplayName + " resigns") || 
                      (result.contains("resigns") && result.contains(whiteDisplayName + " wins"))) {
                winner = whitePlayer;
                loser = blackPlayer;
            }
        } else if (result.toLowerCase().contains("stalemate") || result.toLowerCase().contains("draw")) {
            endType = "draw";
        }
        
        // Build explicit context for each player
        String whiteDisplayName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(whitePlayer);
        String blackDisplayName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(blackPlayer);
        
        if (endType.equals("draw")) {
            context.append(String.format("- %s (White): You drew this game\n", whiteDisplayName));
            context.append(String.format("- %s (Black): You drew this game\n", blackDisplayName));
        } else if (winner != null && loser != null) {
            String winnerDisplayName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(winner);
            String loserDisplayName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(loser);
            String winnerColor = winner.equals(whitePlayer) ? "White" : "Black";
            String loserColor = loser.equals(whitePlayer) ? "White" : "Black";
            
            context.append(String.format("- %s (%s): YOU WON by %s\n", winnerDisplayName, winnerColor, endType));
            context.append(String.format("- %s (%s): You lost by %s\n", loserDisplayName, loserColor, endType));
        } else {
            // Fallback - couldn't parse winner clearly
            context.append(String.format("- %s (White): Game ended - %s\n", whiteDisplayName, result));
            context.append(String.format("- %s (Black): Game ended - %s\n", blackDisplayName, result));
        }
        
        Log.d(TAG, "🏁 Parsed game result context: " + context.toString());
        return context.toString();
    }

    /**
     * 🎭 ENHANCED: Dialogue callback with EmotionalContext awareness
     */
    private class EnhancedEmotionalConversationCallback implements SpectatorConversationOrchestrator.ConversationCallback {
        private final String context;

        public EnhancedEmotionalConversationCallback(String context) {
            this.context = context;
        }

        @Override
        public void onConversationStart(String speaker1, String speaker2) {
            Log.d(TAG, "🎬 Enhanced emotional conversation started: " + speaker1 + " vs " + speaker2);
            // Update EmotionalContext with conversation start
            if (emotionalContext != null) {
                emotionalContext.setLastSpeaker(null); // Reset for new conversation
            }
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

            // 🎭 UPDATE EMOTIONAL CONTEXT with speaker info
            if (emotionalContext != null) {
                emotionalContext.setLastSpeaker(speaker);
            }

            Log.d(TAG, "✅ Enhanced emotional dialogue displayed: " + formattedDialogue);
        }

        @Override
        public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
            Log.d(TAG, "😮 EMOTIONAL RESPONSE! " + speaker + " (" + emotion + "): " + dialogue);

            // Handle emotional dialogue specially
            String speakerName = FineTunedModelManager.getInstance(getApplication())
                    .getMasterDisplayName(speaker);
            String emotionalDialogue = speakerName + " (" + emotion + "): \"" + dialogue + "\"";
            
            aiDialogue.setValue(emotionalDialogue);
            conversationSpeaker.setValue(speakerName);
            
            // 🎭 UPDATE EMOTIONAL CONTEXT with emotional response
            if (emotionalContext != null) {
                emotionalContext.setLastSpeaker(speaker);
                // Note: The emotional state should already be updated by EmotionalIntelligenceManager
            }
        }

        @Override
        public void onConversationEnd(String finalSpeaker, String finalMessage) {
            Log.d(TAG, "🎭 EMOTIONAL CONVERSATION ENDED with " + finalSpeaker + ": " + finalMessage);

            // Update conversation state
            conversationActive.postValue(false);
            conversationInProgress = false; // Reset conversation lock

            Log.d(TAG, "✅ Enhanced emotional conversation has ended naturally");
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, "❌ Enhanced emotional dialogue error (" + context + "): " + error);
            
            // NO FALLBACK - Responses API only approach
            // Just mark conversation as inactive and log the error
            
            // Check if we're still active before updating UI
            if (!gameInProgress) {
                Log.d(TAG, "⚠️ Game stopped, ignoring error callback");
                return;
            }
            
            conversationActive.setValue(false);
            conversationInProgress = false; // CRITICAL: Reset conversation lock on error
            
            // Show that we're waiting for the next conversation attempt
            Log.d(TAG, "⏳ Waiting for next conversation opportunity via Responses API");
        }
    }
    
    /**
     * 🎭 LEGACY: Dialogue callback that supports conversations (keeping for compatibility)
     */
    private class EnhancedConversationCallback implements SpectatorConversationOrchestrator.ConversationCallback {
        private final String context;

        public EnhancedConversationCallback(String context) {
            this.context = context;
        }

        @Override
        public void onConversationStart(String speaker1, String speaker2) {
            Log.d(TAG, "🎬 Enhanced conversation started: " + speaker1 + " vs " + speaker2);
        }

        @Override
        public void onDialogueGenerated(String speaker, String dialogue) {
            Log.d(TAG, "🎭 Enhanced dialogue from " + speaker + ": " + dialogue);

            // 🚀 NEW: Update conversation momentum
            try {
                Phase2EmotionalIntegrationBridge phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(getApplication());
                String opponentName = speaker.equals(whitePlayer) ? blackPlayer : whitePlayer;
                
                if (phase2Bridge != null) {
                    phase2Bridge.processConversationMomentum(speaker, dialogue, opponentName);
                    Log.d(TAG, "🚀 Conversation momentum updated for " + speaker);
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error updating conversation momentum", e);
            }

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
        public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
            Log.d(TAG, "😮 EMOTIONAL RESPONSE from " + speaker + " (" + emotion + "): " + dialogue);

            // 🚀 NEW: Update conversation momentum with emotional intensity boost
            try {
                Phase2EmotionalIntegrationBridge phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(getApplication());
                String opponentName = speaker.equals(whitePlayer) ? blackPlayer : whitePlayer;
                
                if (phase2Bridge != null) {
                    // Emotional responses get extra momentum boost
                    phase2Bridge.processConversationMomentum(speaker, dialogue + " [EMOTIONAL:" + emotion + "]", opponentName);
                    Log.d(TAG, "🚀 Emotional momentum updated for " + speaker + " (" + emotion + ")");
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error updating emotional momentum", e);
            }

            // Handle emotional dialogue specially
            String speakerName = FineTunedModelManager.getInstance(getApplication())
                    .getMasterDisplayName(speaker);
            String emotionalDialogue = speakerName + " (" + emotion + "): \"" + dialogue + "\"";
            
            aiDialogue.setValue(emotionalDialogue);
            conversationSpeaker.setValue(speakerName);
        }

        @Override
        public void onConversationEnd(String finalSpeaker, String finalMessage) {
            Log.d(TAG, "🎭 CONVERSATION ENDED with " + finalSpeaker + ": " + finalMessage);

            // Update conversation state
            conversationActive.postValue(false);

            Log.d(TAG, "✅ Conversation has ended naturally");
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, "❌ Enhanced dialogue error (" + context + "): " + error);
            
            // NO FALLBACK - Responses API only approach
            // Just mark conversation as inactive and log the error
            
            // Check if we're still active before updating UI
            if (!gameInProgress) {
                Log.d(TAG, "⚠️ Game stopped, ignoring error callback");
                return;
            }
            
            conversationActive.setValue(false);
            conversationInProgress = false; // CRITICAL: Reset conversation lock on error
            
            // Show that we're waiting for the next conversation attempt
            Log.d(TAG, "⏳ Waiting for next conversation opportunity via Responses API");
        }
    }

    /**
     * 🎭 SELECTIVE: Higher threshold for emotional dialogue - only major swings
     */
    private boolean shouldGenerateEmotionalDialogue(Float currentEval, Float previousEval) {
        if (currentEval == null || previousEval == null) {
            return false;
        }

        float evalChange = Math.abs(currentEval - previousEval);

        // SELECTIVE: Only trigger on major evaluation swings (blunders/brilliancies)
        if (evalChange > 1.5f) {  // RESTORED to 1.5f - only major swings
            Log.d(TAG, "🎭 MAJOR EVAL SWING: " + previousEval + " → " + currentEval + " (Δ" + evalChange + ")");
            return true;
        }

        return false;
    }

    /**
     * 🎭 SELECTIVE: Restore proper dialogue frequency - opening, middlegame, endgame only
     */
    private boolean shouldGenerateDialogue(int moveNumber) {
        // SELECTIVE: Only key moments get regular commentary
        
        // Opening conversation: Move 8 only (after opening development)
        if (moveNumber <= 15) {
            return moveNumber == 8;  // Single opening commentary
        }

        // Middlegame conversation: Move 25 only (peak tactical phase)
        if (moveNumber <= 35) {
            return moveNumber == 25;  // Single middlegame commentary
        }

        // Endgame conversation: Move 45+ (critical endgame)
        return moveNumber == 45;  // Single endgame commentary
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

            if (evalChange > 0.5f) {  // Evaluation swing - more frequent via Responses API
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

        // Use the conversation orchestrator for emotional responses

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

            // Trigger enhanced emotional conversation using the orchestrator
            String gameContext = String.format("Emotional response to evaluation change: %.2f\nContext: %s", 
                evalChange, emotionalContext);
            
            // 🎭 CRITICAL FIX: Use enhanced method with evaluation data
            conversationOrchestrator.startConversationWithEvaluation(
                    "emotional_response",
                    whitePlayer,
                    blackPlayer,
                    gameContext,
                    new EnhancedConversationCallback("emotional"),
                    currentEval,
                    lastEvaluationForEmotions);
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

            // 🎭 FIXED: Include FULL game context with position and move history for masters
            String currentFen = currentFEN.getValue();
            StringBuilder gameContextBuilder = new StringBuilder();
            gameContextBuilder.append(String.format("Move: %s by %s\nEvaluation: %.2f\nMove number: %d\n", 
                move, playerWhoMoved, currentEval != null ? currentEval : 0.0f, history.size()));
            
            // CRITICAL: Add FEN position so masters can see the board
            if (currentFen != null) {
                gameContextBuilder.append("POSITION: ").append(currentFen).append("\n");
            }
            
            // CRITICAL: Add recent move history so masters have context
            if (history.size() > 0) {
                // Include last 10 moves for context
                int startIdx = Math.max(0, history.size() - 10);
                List<String> recentMoves = history.subList(startIdx, history.size());
                gameContextBuilder.append("RECENT_MOVES: ").append(String.join(" ", recentMoves)).append("\n");
            }
            
            String gameContext = gameContextBuilder.toString();
                
            // 🎭 CRITICAL FIX: Use enhanced method with evaluation data AND EmotionalContext for inter-master awareness
            conversationOrchestrator.startConversationWithEvaluation(
                    "brilliant_move",
                    whitePlayerName,
                    blackPlayerName,
                    gameContext,
                    new EnhancedEmotionalConversationCallback("brilliant_move"),
                    currentEval,
                    lastEvaluationForEmotions,
                    emotionalContext  // 🌟 Pass EmotionalContext for emergent behavior!
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
    
    /**
     * 🧪 DEBUG: Force an opening conversation for testing
     */
    public void forceOpeningConversation() {
        Log.d(TAG, "🧪 FORCE: Manually triggering opening conversation for testing");
        if (whitePlayer != null && blackPlayer != null && conversationOrchestrator != null) {
            generateEnhancedOpeningDialogue();
        } else {
            Log.e(TAG, "❌ FORCE: Cannot force conversation - missing requirements");
            Log.e(TAG, "   whitePlayer: " + whitePlayer);
            Log.e(TAG, "   blackPlayer: " + blackPlayer);
            Log.e(TAG, "   conversationOrchestrator: " + conversationOrchestrator);
        }
    }
    
    /**
     * 🧪 DEBUG: Force a move commentary for testing
     */
    public void forceMoveCommentary() {
        Log.d(TAG, "🧪 FORCE: Manually triggering move commentary for testing");
        List<String> currentHistory = moveHistory.getValue();
        if (currentHistory != null && !currentHistory.isEmpty()) {
            String lastMove = currentHistory.get(currentHistory.size() - 1);
            generateEnhancedMoveDialogue(lastMove, currentHistory);
        } else {
            Log.e(TAG, "❌ FORCE: No moves to comment on");
        }
    }
    
    /**
     * 🔄 FALLBACK: Generate a simple greeting when conversation system fails
     */
    private void generateSimpleFallbackGreeting() {
        Log.d(TAG, "🔄 FALLBACK: Generating simple greeting as conversation fallback");
        
        if (whitePlayer == null || blackPlayer == null) {
            return;
        }
        
        // Simple personality-based greetings
        String greeting;
        String speaker;
        
        // Randomly choose who speaks first
        boolean whiteFirst = Math.random() > 0.5;
        speaker = whiteFirst ? whitePlayer : blackPlayer;
        String opponent = whiteFirst ? blackPlayer : whitePlayer;
        
        switch (speaker.toLowerCase()) {
            case "tal":
                greeting = "Ah, " + opponent + "! Ready for some beautiful tactical fireworks?";
                break;
            case "fischer":
                greeting = "Playing " + opponent + " today. Let's see who plays the most accurate chess.";
                break;
            case "carlsen":
                greeting = "Good to play " + opponent + " again. Should be an interesting game.";
                break;
            case "kasparov":
                greeting = "Facing " + opponent + "! Time to show the power of dynamic play!";
                break;
            case "karpov":
                greeting = "A game with " + opponent + ". I'll play solid, positional chess.";
                break;
            default:
                greeting = "Ready to begin against " + opponent + ". May the best player win!";
                break;
        }
        
        // Display the fallback greeting
        String formattedGreeting = FineTunedModelManager.getInstance(getApplication())
                .getMasterDisplayName(speaker) + ": \"" + greeting + "\"";
        
        aiDialogue.setValue(formattedGreeting);
        conversationSpeaker.setValue(speaker);
        
        Log.d(TAG, "✅ FALLBACK: Simple greeting displayed: " + formattedGreeting);
    }

    /**
     * 🎯 RESPONSES API ONLY APPROACH: No fallbacks, pure Responses API conversations
     */

    /**
     * CRITICAL: Force stop all operations immediately to prevent ANR
     */
    public void forceStop() {
        Log.d(TAG, "🚨 FORCE STOPPING SpectatorGameViewModel");
        
        // Stop game immediately
        gameInProgress = false;
        isPaused = true;
        
        // Clear all pending operations
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        
        // Stop executors aggressively
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        
        // Force stop game manager
        if (gameManager != null) {
            gameManager.forceStop();
        }
        
        // Force stop conversation orchestrator
        if (conversationOrchestrator != null) {
            conversationOrchestrator.forceStop();
        }
        
        Log.d(TAG, "✅ FORCE STOP completed");
    }

    public void cleanup() {
        gameInProgress = false;

        // 🚀 NEW: Reset emotional momentum for cleanup
        try {
            Phase2EmotionalIntegrationBridge phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(getApplication());
            if (phase2Bridge != null) {
                phase2Bridge.clearEmotionalState();
                Log.d(TAG, "🚀 Emotional momentum reset during cleanup");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error resetting momentum during cleanup", e);
        }

        if (gameManager != null) {
            gameManager.cleanup();
        }
        if (conversationOrchestrator != null) {
            conversationOrchestrator.cleanup();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "🧹 ENHANCED SpectatorGameViewModel cleaned up with momentum reset");
    }
    
    /**
     * 🚀 NEW: Get momentum statistics for debugging
     */
    public String getMomentumStatistics() {
        try {
            Phase2EmotionalIntegrationBridge phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(getApplication());
            if (phase2Bridge != null) {
                return phase2Bridge.getMomentumStatistics();
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting momentum statistics", e);
        }
        return "Momentum statistics unavailable";
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }
}