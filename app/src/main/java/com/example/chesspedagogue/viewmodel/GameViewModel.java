// GameViewModel.java - COMPLETE FIXED VERSION
package com.example.chesspedagogue.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chesspedagogue.EvaluationTracker;
import com.example.chesspedagogue.FineTunedModelManager;
import com.example.chesspedagogue.GameStateRepository;
import com.example.chesspedagogue.LogThrottler;
import com.example.chesspedagogue.GameHistoryManager;
import com.example.chesspedagogue.MoveHistoryObserver;
import com.example.chesspedagogue.StockfishManager;
import com.example.chesspedagogue.model.GameState;
import com.example.chesspedagogue.repository.GameRepository;
import com.example.chesspedagogue.reasoning.ReasoningEngineManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameViewModel extends AndroidViewModel {

    private static final String TAG = "GameViewModel";

    // FIXED: Add missing executor service and handler
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Add these fields to your GameViewModel class
    private final MoveHistoryObserver moveHistoryObserver = new MoveHistoryObserver();
    private final GameRepository gameRepository;

    // LiveData objects that the UI will observe
    private final MutableLiveData<GameState> gameStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> currentFEN = new MutableLiveData<>();
    private final MutableLiveData<List<String>> moveHistory = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlayerTurn = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGameOver = new MutableLiveData<>();
    private final MutableLiveData<String> winner = new MutableLiveData<>();
    private final MutableLiveData<int[]> _animateMoveEvent = new MutableLiveData<>();
    private final MutableLiveData<int[]> _lastMoveEvent = new MutableLiveData<>();
    private final MutableLiveData<int[]> _kingInCheckEvent = new MutableLiveData<>();

    // Add this field to GameViewModel.java class
    private final MutableLiveData<Boolean> _clearSelectionEvent = new MutableLiveData<>();

    // NEW: Evaluation-related LiveData for your evaluation bar! 🎉
    private final MutableLiveData<Float> currentEvaluation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isMatePosition = new MutableLiveData<>();
    private final MutableLiveData<Integer> mateInMoves = new MutableLiveData<>();
    private final MutableLiveData<Boolean> evaluationLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> personalityEngineEnabled = new MutableLiveData<>();
    private final MutableLiveData<String> personalityMaster = new MutableLiveData<>();
    private final MutableLiveData<Float> personalityWeight = new MutableLiveData<>();
    private final MutableLiveData<String> lastMoveExplanation = new MutableLiveData<>();
    private final MutableLiveData<String> masterQuote = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isHistoricalMove = new MutableLiveData<>();

    private Handler evaluationHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingEvaluationUpdate;

    // Add these fields after your existing ones
    private final MutableLiveData<EvaluationTracker.EvaluationSwing> _lastEvaluationSwing = new MutableLiveData<>();
    private final MutableLiveData<String> _moveExplanation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isHistoricalMove = new MutableLiveData<>();
    private final MutableLiveData<String> _masterQuote = new MutableLiveData<>();


    private long lastEvaluationRequest = 0;
    private static final long EVALUATION_DEBOUNCE_MS = 500;

    // Auto-commentary state tracking
    private EvaluationTracker evaluationTracker;
    private boolean autoCommentaryEnabled = true;

    // Add the missing playerColor field here
    private String playerColor = "white"; // Default to white
    
    // NEW: Reasoning engine integration
    private ReasoningEngineManager reasoningEngineManager;
    private int targetElo = 2200; // Default target ELO

    public GameViewModel(Application application) {
        super(application);
        // Initialize the repository - this will handle Stockfish and game data
        gameRepository = new GameRepository(application);
        
        // Initialize reasoning engine and set the Stockfish manager
        reasoningEngineManager = ReasoningEngineManager.getInstance(application);
        reasoningEngineManager.setStockfishManager(gameRepository.stockfishManager);
        reasoningEngineManager.initialize();
        
        initializePersonalityLiveData();

        // Set initial values
        gameStateLiveData.setValue(new GameState());
        currentFEN.setValue("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        isPlayerTurn.setValue(true);
        isGameOver.setValue(false);
        statusMessage.setValue("Game ready! Make your move.");
        moveHistory.setValue(new ArrayList<>());

        // NEW: Initialize evaluation data
        currentEvaluation.setValue(0.0f);
        isMatePosition.setValue(false);
        mateInMoves.setValue(0);
        evaluationLoading.setValue(false);

        // Get initial evaluation for starting position
        requestPositionEvaluation();
    }

    /**
     * MISSING METHOD 1: Initialize personality-related LiveData
     * CRITICAL FIX: Default to personality engine - this is the core feature!
     */
    private void initializePersonalityLiveData() {
        Log.d(TAG, "🎭 Initializing personality LiveData...");

        personalityEngineEnabled.setValue(true); // FIXED: Default to personality engine
        personalityMaster.setValue("tal"); // Default to Tal when enabled
        personalityWeight.setValue(0.3f); // Balanced personality influence
        lastMoveExplanation.setValue("");
        masterQuote.setValue("");
        isHistoricalMove.setValue(false);

        Log.d(TAG, "✅ Personality LiveData initialized - Default: Tal Personality Engine");
    }

    /**
     * MISSING METHOD 2: Check for game end conditions
     */
    private void checkGameEndConditions() {
        Log.d(TAG, "🔍 Checking game end conditions...");

        try {
            // Check for checkmate
            if (gameRepository.isCheckmate()) {
                isGameOver.setValue(true);
                // Determine winner based on whose turn it is
                // If it's white's turn and checkmate, then black wins (white is mated)
                String currentFen = gameRepository.getCurrentFEN();
                boolean isWhiteTurn = currentFen != null && currentFen.contains(" w ");

                String winnerStr;
                if (isWhiteTurn) {
                    // White to move but in checkmate = Black wins
                    winnerStr = "black".equals(playerColor) ? "You" : "Engine";
                } else {
                    // Black to move but in checkmate = White wins
                    winnerStr = "white".equals(playerColor) ? "You" : "Engine";
                }

                winner.setValue(winnerStr);
                statusMessage.setValue("Checkmate! " + winnerStr + " wins!");
                Log.d(TAG, "♔ Checkmate detected - Winner: " + winnerStr);

            } else if (gameRepository.isStalemate()) {
                isGameOver.setValue(true);
                winner.setValue("Draw");
                statusMessage.setValue("Stalemate! Game drawn.");
                Log.d(TAG, "🤝 Stalemate detected");

            } else {
                // Game continues
                Log.d(TAG, "✅ Game continues - no end condition detected");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking game end conditions", e);
        }
    }

    /**
     * MISSING METHOD 3: Update game state
     */
    private void updateGameState() {
        Log.d(TAG, "🔄 Updating game state...");

        // Update FEN
        String newFen = gameRepository.getCurrentFEN();
        currentFEN.setValue(newFen);

        // Check for game end conditions
        checkGameEndConditions();

        // Request evaluation for new position
        requestPositionEvaluation();
    }

    /**
     * MISSING METHOD 4: Track move evaluation for auto-commentary
     */
    private void trackMoveEvaluation(String move, String previousFen) {
        // Get current evaluation after the move
        gameRepository.getCurrentEvaluation(new GameRepository.EvaluationCallback() {
            @Override
            public void onEvaluationReceived(StockfishManager.EvaluationResult result) {
                if (evaluationTracker != null) {
                    String currentFen = getCurrentFEN().getValue();
                    EvaluationTracker.EvaluationSwing swing = evaluationTracker.trackEvaluation(
                            result, currentFen, move);

                    if (swing != null) {
                        mainHandler.post(() -> {
                            _lastEvaluationSwing.setValue(swing);

                            // Store explanation and historical context if available
                            if (swing.isSignificant()) {
                                _moveExplanation.setValue(swing.quality.comment);
                                _isHistoricalMove.setValue(swing.quality == EvaluationTracker.MoveQuality.BRILLIANT);
                            }
                        });
                    }
                }
            }

            @Override
            public void onEvaluationError(String errorMessage) {
                Log.w(TAG, "Could not get evaluation for move tracking: " + errorMessage);
            }
        });
    }

    // Add this method to your GameViewModel class
    public void addMoveHistoryListener(MoveHistoryObserver.MoveHistoryListener listener) {
        moveHistoryObserver.addListener(listener);
    }

    // Add this getter method
    public LiveData<Boolean> getClearSelectionEvent() {
        return _clearSelectionEvent;
    }

    // Add this method to reset the event
    public void resetClearSelectionEvent() {
        _clearSelectionEvent.setValue(false);
    }

    // Getter method for king in check event
    public LiveData<int[]> getKingInCheckEvent() {
        return _kingInCheckEvent;
    }

    // Method to check if a king is in check based on the FEN string
    private void checkForKingInCheck(String fen) {
        boolean isCheck = fen != null && fen.contains("+");

        if (isCheck) {
            // For simplicity in this version, we'll find the king position
            // by scanning the board state
            findAndSignalKingInCheck(fen);
        } else {
            // No check - clear highlighting
            _kingInCheckEvent.setValue(null);
        }
    }

    // Find the king that's in check and signal the UI
    private void findAndSignalKingInCheck(String fen) {
        // We know there's a check, but we need to find which king
        // This is a simplification - we'll check if the current player is in check
        boolean isWhiteTurn = fen.contains(" w ");
        boolean isPlayerTurn = (isPlayerWhite() && isWhiteTurn) ||
                (!isPlayerWhite() && !isWhiteTurn);

        // If it's the player's turn and there's a check, then the player's king is in check
        if (isPlayerTurn) {
            char kingChar = isPlayerWhite() ? 'K' : 'k';
            int[] kingPos = findPiecePosition(fen, kingChar);
            if (kingPos != null) {
                _kingInCheckEvent.setValue(kingPos);
            }
        } else {
            // Opponent's king in check
            // For now, we'll only highlight the player's king in check
            _kingInCheckEvent.setValue(null);
        }
    }

    // Helper to find a piece position from FEN
    private int[] findPiecePosition(String fen, char pieceChar) {
        String[] parts = fen.split(" ");
        String boardPart = parts[0];
        String[] rows = boardPart.split("/");

        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            int c = 0;

            for (int i = 0; i < row.length(); i++) {
                char ch = row.charAt(i);

                if (Character.isDigit(ch)) {
                    // Skip empty squares
                    c += Character.getNumericValue(ch);
                } else if (ch == pieceChar) {
                    // Found the piece!
                    return new int[]{r, c};
                } else {
                    // Another piece
                    c++;
                }
            }
        }

        return null; // Piece not found
    }

    // Helper to determine if player is white
    private boolean isPlayerWhite() {
        return playerColor.equals("white");
    }

    // Add getter for player color
    public String getPlayerColor() {
        return playerColor;
    }

    // Add getter method
    public LiveData<int[]> getLastMoveEvent() {
        return _lastMoveEvent;
    }

    // Then add this getter method
    public LiveData<int[]> getAnimateMoveEvent() {
        return _animateMoveEvent;
    }

    // NEW: Getter methods for evaluation data - this is what your UI will observe! 🎯
    public LiveData<Float> getCurrentEvaluation() {
        return currentEvaluation;
    }

    public LiveData<Boolean> getIsMatePosition() {
        return isMatePosition;
    }

    public LiveData<Integer> getMateInMoves() {
        return mateInMoves;
    }

    public LiveData<Boolean> getEvaluationLoading() {
        return evaluationLoading;
    }

    /**
     * NEW METHOD: Request evaluation for the current position
     * This is called automatically after moves, but you can also call it manually
     */
    /**
     * Enhanced evaluation request with debouncing to prevent flicker
     */
    public void requestPositionEvaluation() {
        Log.d(TAG, "🔍 Requesting position evaluation...");
        long now = System.currentTimeMillis();
        if (now - lastEvaluationRequest < EVALUATION_DEBOUNCE_MS) {
            Log.d(TAG, "🔄 Skipping evaluation - too soon since last request");
            return;
        }
        lastEvaluationRequest = now;
        // Can
        // cel any pending evaluation update
        if (pendingEvaluationUpdate != null) {
            evaluationHandler.removeCallbacks(pendingEvaluationUpdate);
        }

        evaluationLoading.setValue(true);

        // Debounce rapid evaluation requests
        pendingEvaluationUpdate = () -> {
            gameRepository.getCurrentEvaluation(new GameRepository.EvaluationCallback() {
                @Override
                public void onEvaluationReceived(StockfishManager.EvaluationResult result) {
                    Log.d(TAG, "✅ Evaluation received: " + result.toString());

                    // Update UI on main thread with small delay for smoothness
                    mainHandler.postDelayed(() -> {
                        evaluationLoading.setValue(false);

                        if (result.isMate) {
                            isMatePosition.setValue(true);
                            mateInMoves.setValue(result.mateInMoves);
                            currentEvaluation.setValue(0.0f);
                        } else {
                            isMatePosition.setValue(false);
                            mateInMoves.setValue(0);
                            currentEvaluation.setValue(result.evaluation);
                        }
                    }, 100); // 100ms delay for smooth visual experience
                }

                @Override
                public void onEvaluationError(String errorMessage) {
                    Log.e(TAG, "❌ Evaluation error: " + errorMessage);
                    mainHandler.post(() -> evaluationLoading.setValue(false));
                }
            });
        };

        // Execute after short delay to debounce rapid calls
        evaluationHandler.postDelayed(pendingEvaluationUpdate, 200);
    }

    /**
     * NEW METHOD: Get evaluation for a specific FEN position
     * Useful for analysis mode or reviewing positions
     */
    public void requestEvaluationForPosition(String fen) {
        Log.d(TAG, "🔍 Requesting evaluation for specific position...");
        evaluationLoading.setValue(true);

        gameRepository.getEvaluationForPosition(fen, new GameRepository.EvaluationCallback() {
            @Override
            public void onEvaluationReceived(StockfishManager.EvaluationResult result) {
                Log.d(TAG, "✅ Position evaluation received: " + result.toString());

                mainHandler.post(() -> {
                    evaluationLoading.setValue(false);

                    if (result.isMate) {
                        isMatePosition.setValue(true);
                        mateInMoves.setValue(result.mateInMoves);
                        currentEvaluation.setValue(0.0f);
                    } else {
                        isMatePosition.setValue(false);
                        mateInMoves.setValue(0);
                        currentEvaluation.setValue(result.evaluation);
                    }
                });
            }

            @Override
            public void onEvaluationError(String errorMessage) {
                Log.e(TAG, "❌ Position evaluation error: " + errorMessage);

                mainHandler.post(() -> {
                    evaluationLoading.setValue(false);
                });
            }
        });
    }

    // Public getter methods for the LiveData objects
    public LiveData<GameState> getGameState() {
        return gameStateLiveData;
    }

    public LiveData<String> getCurrentFEN() {
        return currentFEN;
    }

    public LiveData<List<String>> getMoveHistory() {
        return moveHistory;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<Boolean> isPlayerTurn() {
        return isPlayerTurn;
    }

    public LiveData<Boolean> isGameOver() {
        return isGameOver;
    }

    public LiveData<String> getWinner() {
        return winner;
    }

    // Add these getter methods after your existing ones
    public LiveData<EvaluationTracker.EvaluationSwing> getLastEvaluationSwing() {
        return _lastEvaluationSwing;
    }

    public LiveData<String> getLastMoveExplanation() {
        return _moveExplanation;
    }

    public LiveData<Boolean> getIsHistoricalMove() {
        return _isHistoricalMove;
    }

    public LiveData<String> getMasterQuote() {
        return _masterQuote;
    }

    // ===== CRITICAL FIX: This method now properly updates both systems =====
    /**
     * ENHANCED: Make player move with robust error handling and retry logic
     */
    public void makePlayerMove(String move) {
        Log.d(TAG, "🎯 makePlayerMove called with: " + move);

        if (!isEngineReady()) {
            statusMessage.setValue("Engine not ready. Please restart the game.");
            return;
        }

        // Get current move history
        List<String> history = moveHistory.getValue();
        if (history == null) {
            history = new ArrayList<>();
        }

        // ENHANCED: Try move validation with retry logic
        validateAndExecuteMove(move, history, 0);
    }

    /**
     * NEW METHOD: Validate and execute move with retry logic
     */
    private void validateAndExecuteMove(String move, List<String> history, int retryCount) {
        final int MAX_RETRIES = 2;

        Log.d(TAG, "🔍 Validating move: " + move + " (attempt " + (retryCount + 1) + ")");

        // First, let's verify the current position
        String currentPosition = gameRepository.getCurrentFEN();
        Log.d(TAG, "📋 Current position: " + currentPosition);

        // ENHANCED: Check if move is legal with better error handling
        try {
            boolean isLegal = gameRepository.isLegalMove(move);
            Log.d(TAG, "⚖️ Move " + move + " legality check: " + (isLegal ? "LEGAL" : "ILLEGAL"));

            if (isLegal) {
                // Move is legal, execute it
                executeValidatedMove(move, history);
            } else {
                // Move appears illegal
                if (retryCount < MAX_RETRIES) {
                    Log.w(TAG, "⚠️ Move rejected, retrying... (attempt " + (retryCount + 1) + " of " + MAX_RETRIES + ")");

                    // Wait a moment and retry
                    mainHandler.postDelayed(() -> {
                        validateAndExecuteMove(move, history, retryCount + 1);
                    }, 100); // 100ms delay
                } else {
                    // All retries exhausted
                    Log.e(TAG, "❌ Move " + move + " definitively rejected after " + MAX_RETRIES + " attempts");
                    handleInvalidMove(move);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "💥 Exception during move validation: " + e.getMessage(), e);

            if (retryCount < MAX_RETRIES) {
                Log.w(TAG, "🔄 Retrying due to exception...");
                mainHandler.postDelayed(() -> {
                    validateAndExecuteMove(move, history, retryCount + 1);
                }, 150); // Slightly longer delay for exceptions
            } else {
                Log.e(TAG, "❌ Move validation failed permanently due to exceptions");
                handleInvalidMove(move);
            }
        }
    }

    /**
     * OPTIMIZED: Execute validated move with batched UI updates
     */
    private void executeValidatedMove(String move, List<String> history) {
        Log.d(TAG, "✅ Executing validated move: " + move);

        try {
            // Add the new move to history
            List<String> newHistory = new ArrayList<>(history);
            newHistory.add(move);

            // Apply the single move directly
            boolean success = gameRepository.makeMove(move);

            if (success) {
                // Get new state
                String newFen = gameRepository.getCurrentFEN();
                Log.d(TAG, "📍 New position after move: " + newFen);

                // OPTIMIZATION: Batch all UI updates in a single post
                mainHandler.post(() -> {
                    // Update all LiveData atomically
                    currentFEN.setValue(newFen);
                    moveHistory.setValue(newHistory);

                    // Update GameHistoryManager
                    GameHistoryManager.getInstance().addMove(move);

                    // Notify observers
                    moveHistoryObserver.notifyMoveMade(move, newFen, newHistory);

                    // Update central repository
                    GameStateRepository.updateState(newFen, newHistory, playerColor);

                    // Trigger animation AFTER other updates
                    triggerMoveAnimation(move);

                    // Update turn state
                    isPlayerTurn.setValue(false);
                    statusMessage.setValue("You moved " + move + ". Engine thinking...");
                });

                // OPTIMIZATION: Delay evaluation request to avoid congestion
                mainHandler.postDelayed(() -> {
                    // Request evaluation after UI settles
                    requestPositionEvaluation();

                    // NEW: Track evaluation for auto-commentary AFTER evaluation completes
                    if (autoCommentaryEnabled && evaluationTracker != null) {
                        trackMoveEvaluation(move, currentFEN.getValue());
                    }

                    // Request engine move
                    requestEngineMove();
                }, 100); // Small delay to let UI settle

            } else {
                Log.e(TAG, "❌ Move execution failed in gameRepository.makeMove()");
                handleMoveExecutionFailure(move);
            }
        } catch (Exception e) {
            Log.e(TAG, "💥 Exception during move execution: " + e.getMessage(), e);
            handleMoveExecutionFailure(move);
        }
    }

    /**
     * NEW METHOD: Handle invalid move
     */
    private void handleInvalidMove(String move) {
        Log.w(TAG, "🚫 Handling invalid move: " + move);
        statusMessage.setValue("Invalid move: " + move + ". Please try again.");
        _clearSelectionEvent.setValue(true);
    }

    /**
     * NEW METHOD: Handle move execution failure
     */
    private void handleMoveExecutionFailure(String move) {
        Log.e(TAG, "💔 Handling move execution failure: " + move);
        statusMessage.setValue("Move failed to execute: " + move + ". Please try again.");
        _clearSelectionEvent.setValue(true);
    }

    // In GameViewModel.java, after a successful move
    // This could be in makePlayerMove or in a separate method like onMoveExecuted
    private void onMoveExecuted(String moveUci) {
        // Convert UCI format to board coordinates
        int fromCol = moveUci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(moveUci.charAt(1));
        int toCol = moveUci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(moveUci.charAt(3));

        // Trigger animation on UI thread via LiveData event
        _animateMoveEvent.setValue(new int[]{fromRow, fromCol, toRow, toCol});
    }

    // Add this helper method
    private boolean isEngineReady() {
        try {
            // A simple check to see if the engine is responding
            return gameRepository.stockfishManager.waitForReady(100);
        } catch (Exception e) {
            Log.e(TAG, "Engine not ready", e);
            return false;
        }
    }

    // Fixed implementation that won't cause compilation errors
    public void loadGame(String playerColor, List<String> moves, String finalFen) {
        // Update player color field
        this.playerColor = playerColor;

        // Reset game
        gameRepository.newGame();

        // ===== CRITICAL FIX: Reset GameHistoryManager too! =====
        GameHistoryManager.getInstance().clearHistory();

        // Apply each move
        for (String move : moves) {
            gameRepository.makeMove(move);
            // ===== CRITICAL FIX: Add to GameHistoryManager =====
            GameHistoryManager.getInstance().addMove(move);
        }

        // Update LiveData with loaded game state
        currentFEN.setValue(gameRepository.getCurrentFEN());
        moveHistory.setValue(new ArrayList<>(moves));
        isGameOver.setValue(false);

        // Determine whose turn it is
        boolean isPlayersTurn = (moves.size() % 2 == 0 && playerColor.equalsIgnoreCase("white")) ||
                (moves.size() % 2 == 1 && playerColor.equalsIgnoreCase("black"));

        isPlayerTurn.setValue(isPlayersTurn);
        statusMessage.setValue("Game loaded. " + (isPlayersTurn ? "Your turn." : "Engine thinking..."));

        // NEW: Get evaluation for loaded position! 🎯
        requestPositionEvaluation();

        // If it's the engine's turn, make it move
        if (!isPlayersTurn) {
            requestEngineMove();
        }
    }

    /**
     * NEW METHOD: Initialize game with full configuration from splash screen
     */
    public void newGameWithConfiguration(String playerColor, int skillLevel, int engineElo) {
        Log.d(TAG, "🎮 Starting new game with configuration: " + playerColor + ", skill=" + skillLevel + ", elo=" + engineElo);

        // Update the player color
        this.playerColor = playerColor;

        // Reset game state for a new game
        gameRepository.newGame();

        // ===== CRITICAL FIX: Reset GameHistoryManager! =====
        GameHistoryManager.getInstance().clearHistory();

        // NEW: Configure engine with splash screen settings
        gameRepository.configureEngine(skillLevel, engineElo);

        currentFEN.setValue(gameRepository.getCurrentFEN());
        moveHistory.setValue(new ArrayList<>());
        isGameOver.setValue(false);
        winner.setValue(null);

        // NEW: Reset evaluation for new game! 🎯
        currentEvaluation.setValue(0.0f);
        isMatePosition.setValue(false);
        mateInMoves.setValue(0);

        // Get evaluation for starting position
        requestPositionEvaluation();

        if (playerColor.equalsIgnoreCase("white")) {
            isPlayerTurn.setValue(true);
            statusMessage.setValue("New game started. You're playing as White. Your move.");
        } else {
            isPlayerTurn.setValue(false);
            statusMessage.setValue("New game started. You're playing as Black. Engine thinking...");
            requestEngineMove();
        }

        Log.d(TAG, "✅ Game initialized with " + playerColor + " vs " + engineElo + " Elo engine");
    }

    // Fixed implementation for legal moves
    public void getLegalMovesForSquare(String square, Callback<List<String>> callback) {
        // Extract row and column from algebraic notation (e.g., "e4")
        char file = square.charAt(0);
        int rank = Character.getNumericValue(square.charAt(1));

        // Convert to board coordinates
        int col = file - 'a';
        int row = 8 - rank;

        // Get legal moves synchronously for now (can be improved later)
        List<String> legalMoves = gameRepository.getLegalMovesForPiece(row, col);

        // Return the result via callback
        callback.onResult(legalMoves);
    }

    // ===== CRITICAL FIX: Engine moves now use PERSONALITY ENGINE! =====
    /**
     * REVOLUTIONARY: Request engine move - now uses personality when enabled! 🎭
     * FIXED: Proper fallback handling to prevent engine from stopping
     */
    private void requestEngineMove() {
        // Check if personality engine is enabled
        if (Boolean.TRUE.equals(personalityEngineEnabled.getValue())) {
            Log.d(TAG, "🎭 Using PERSONALITY ENGINE for move calculation!");
            requestPersonalityEngineMove();
        } else {
            Log.d(TAG, "🤖 Using VANILLA STOCKFISH for move calculation");
            requestStandardEngineMove();
        }
    }

    /**
     * ENHANCED: Request engine move using personality guidance!
     * FIXED: Better error handling and proper fallback to prevent engine stopping
     */
    private void requestPersonalityEngineMove() {
        LogThrottler.force("GameViewModel", "🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=" + personalityEngineEnabled.getValue());
        if (!Boolean.TRUE.equals(personalityEngineEnabled.getValue())) {
            // Fall back to regular engine
            Log.d(TAG, "🔄 Personality disabled, falling back to standard engine");
            requestStandardEngineMove();
            return;
        }

        String currentMaster = personalityMaster.getValue();
        if (currentMaster == null) currentMaster = "tal";

        Log.d(TAG, String.format("🎭 REQUESTING MOVE FOR MASTER: %s", currentMaster));

        // Check if master supports reasoning engine
        if (ReasoningEngineManager.supportsReasoning(currentMaster)) {
            Log.d(TAG, "🧠 Using reasoning engine for " + currentMaster);
            requestReasoningEngineMove(currentMaster);
        } else {
            Log.d(TAG, "🎭 Using traditional personality engine for " + currentMaster);
            requestTraditionalPersonalityMove(currentMaster);
        }
    }

    /**
     * Request move using new reasoning engine for supported masters
     */
    private void requestReasoningEngineMove(String masterName) {
        String masterDisplayName = FineTunedModelManager.getInstance(getApplication())
                .getMasterDisplayName(masterName);
        statusMessage.setValue(String.format("🧠 %s is analyzing with reasoning model...", masterDisplayName));

        // Set timeout for reasoning engine
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            Log.w(TAG, "⏰ Reasoning engine timeout, falling back to traditional personality");
            statusMessage.setValue(String.format("🔄 %s switching to faster mode...", masterDisplayName));
            requestTraditionalPersonalityMove(masterName);
        };
        timeoutHandler.postDelayed(timeoutRunnable, 120000); // 2 minute timeout for reasoning

        String currentFen = currentFEN.getValue();
        if (currentFen == null) currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        // Build game context from move history
        String gameContext = buildGameContext();

        reasoningEngineManager.calculateMove(currentFen, masterName, targetElo, gameContext, 
            new ReasoningEngineManager.ReasoningCallback() {
                @Override
                public void onMoveSelected(String move, String explanation, float confidence) {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    Log.d(TAG, String.format("🧠 REASONING MOVE: %s (confidence: %.2f)", move, confidence));
                    
                    mainHandler.post(() -> {
                        handleMoveResult(move, explanation, true, confidence >= 0.7f);
                    });
                }

                @Override
                public void onError(String error) {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    Log.w(TAG, "⚠️ Reasoning engine error: " + error + ", falling back to traditional");
                    mainHandler.post(() -> {
                        requestTraditionalPersonalityMove(masterName);
                    });
                }

                @Override
                public void onProgress(String status) {
                    mainHandler.post(() -> {
                        statusMessage.setValue(String.format("🧠 %s: %s", masterDisplayName, status));
                    });
                }
            });
    }

    /**
     * Request move using traditional personality engine
     */
    private void requestTraditionalPersonalityMove(String masterName) {
        String masterDisplayName = FineTunedModelManager.getInstance(getApplication())
                .getMasterDisplayName(masterName);
        statusMessage.setValue(String.format("🎭 %s is searching his game archive...", masterDisplayName));

        // Set a longer timeout for personality engine (AI database search takes time)
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            Log.w(TAG, "⏰ Personality engine timeout (90s), falling back to standard engine");
            statusMessage.setValue("🔄 Switching to faster mode...");
            requestStandardEngineMove();
        };
        timeoutHandler.postDelayed(timeoutRunnable, 90000); // 90 second timeout for reasoning models

        LogThrottler.force("GameViewModel", "🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()");
        LogThrottler.force("GameViewModel", "🔧 DEBUG: gameRepository instance = " + (gameRepository != null ? "NOT NULL" : "NULL"));
        
        try {
            LogThrottler.force("GameViewModel", "🎯 CALLING gameRepository.calculatePersonalityMove() NOW...");
            LogThrottler.force("GameViewModel", "🔧 gameRepository class: " + gameRepository.getClass().getSimpleName());
            LogThrottler.force("GameViewModel", "🔧 Current thread: " + Thread.currentThread().getName());
            
            gameRepository.calculatePersonalityMove(new GameRepository.MoveCallback() {
            @Override
            public void onMoveCalculated(String engineMove) {
                // Cancel timeout since we got a result
                timeoutHandler.removeCallbacks(timeoutRunnable);

                Log.d(TAG, "🎯 PERSONALITY MOVE CALCULATED: " + engineMove);

                if (engineMove == null || engineMove.trim().isEmpty()) {
                    Log.w(TAG, "⚠️ Personality engine returned empty move, using standard engine");
                    requestStandardEngineMove();
                    return;
                }

                // Get current move history
                List<String> history = moveHistory.getValue();
                if (history == null) {
                    history = new ArrayList<>();
                }

                // Add the engine's move to history
                history.add(engineMove);

                // Apply ALL moves to keep state in sync
                boolean success = gameRepository.applyMoves(history);

                if (success) {
                    // Update LiveData with new state
                    String newFen = gameRepository.getCurrentFEN();
                    currentFEN.setValue(newFen);

                    // Create a defensive copy to trigger LiveData
                    List<String> updatedHistory = new ArrayList<>(history);
                    moveHistory.setValue(updatedHistory);

                    // Update GameHistoryManager for engine moves
                    GameHistoryManager.getInstance().addMove(engineMove);

                    // Update the central game state repository
                    GameStateRepository.updateState(newFen, updatedHistory, playerColor);

                    // Request evaluation after engine move
                    requestPositionEvaluation();

                    // Get the personality context for this move
                    GameRepository.PersonalityMoveContext context = gameRepository.getLastPersonalityContext();
                    if (context != null) {
                        updatePersonalityContext(context, engineMove); // Pass the actual move
                    }

                    isPlayerTurn.setValue(true);

                    // Create amazing status message
                    String masterName = FineTunedModelManager.getInstance(getApplication())
                            .getMasterDisplayName(personalityMaster.getValue());

                    if (context != null && context.isHistoricalMatch) {
                        statusMessage.setValue(String.format("🎯 %s played %s - a historical move! Your turn.",
                                masterName, engineMove));
                        isHistoricalMove.setValue(true);
                    } else {
                        statusMessage.setValue(String.format("🎭 %s played %s. Your turn.",
                                masterName, engineMove));
                        isHistoricalMove.setValue(false);
                    }

                    // Check for game end conditions
                    checkGameEndConditions();

                } else {
                    // Handle error - fall back to standard engine
                    Log.e(TAG, "❌ Failed to apply personality move, using standard engine");
                    requestStandardEngineMove();
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Cancel timeout
                timeoutHandler.removeCallbacks(timeoutRunnable);

                Log.e(TAG, "❌ Personality engine error: " + errorMessage);
                Log.d(TAG, "🔄 Falling back to standard engine due to error");

                // Always fall back to standard engine on error
                requestStandardEngineMove();
            }
        });
        
        } catch (Exception e) {
            LogThrottler.force("GameViewModel", "🚨 EXCEPTION calling gameRepository.calculatePersonalityMove(): " + e.getMessage());
            LogThrottler.force("GameViewModel", "🚨 Exception Class: " + e.getClass().getSimpleName());
            LogThrottler.force("GameViewModel", "🚨 Exception Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "none"));
            LogThrottler.force("GameViewModel", "🚨 Thread: " + Thread.currentThread().getName());
            Log.e(TAG, "❌ Exception calling calculatePersonalityMove", e);
            // Fall back to standard engine
            requestStandardEngineMove();
        }
    }

    /**
     * Standard engine move calculation (fallback)
     */
    private void requestStandardEngineMove() {
        gameRepository.calculateBestMove(new GameRepository.MoveCallback() {
            @Override
            public void onMoveCalculated(String engineMove) {
                // Get current move history
                List<String> history = moveHistory.getValue();
                if (history == null) {
                    history = new ArrayList<>();
                }

                // Add the engine's move to history
                history.add(engineMove);

                // Apply ALL moves to keep state in sync
                boolean success = gameRepository.applyMoves(history);

                if (success) {
                    // Update LiveData with new state
                    String newFen = gameRepository.getCurrentFEN();
                    currentFEN.setValue(newFen);

                    // Create a defensive copy to trigger LiveData
                    List<String> updatedHistory = new ArrayList<>(history);
                    moveHistory.setValue(updatedHistory);

                    // ===== CRITICAL FIX: Update GameHistoryManager for engine moves too! =====
                    GameHistoryManager.getInstance().addMove(engineMove);

                    // IMPORTANT NEW CODE - Update the central game state repository
                    GameStateRepository.updateState(newFen, updatedHistory, playerColor);

                    // NEW: Request evaluation after engine move! 🎯
                    requestPositionEvaluation();

                    isPlayerTurn.setValue(true);

                    // Check for game end conditions
                    checkGameEndConditions();

                    statusMessage.setValue("Engine moved " + engineMove + ". Your turn.");
                } else {
                    // Handle error
                    statusMessage.setValue("Engine move failed to apply. Please restart the game.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Engine error: " + errorMessage);
                statusMessage.setValue("Engine error: " + errorMessage);
            }
        });
    }

    /**
     * Update UI with personality context from the last move
     * FIXED: Use actual move instead of stale data
     */
    private void updatePersonalityContext(GameRepository.PersonalityMoveContext context, String actualMove) {
        Log.d(TAG, "🎭 Updating personality context for move: " + actualMove);

        // Update LiveData with rich personality information
        lastMoveExplanation.setValue(context.analysis);
        masterQuote.setValue(context.masterQuote);
        isHistoricalMove.setValue(context.isHistoricalMatch);

        // Create detailed move analysis for voice synthesis using the ACTUAL move
        StringBuilder detailedAnalysis = new StringBuilder();

        if (context.isHistoricalMatch) {
            detailedAnalysis.append("This is a historical move! ");
            detailedAnalysis.append("I played ").append(actualMove).append(" ");
            detailedAnalysis.append(context.historicalContext);
        } else {
            detailedAnalysis.append("I chose ").append(actualMove).append(" ");
            detailedAnalysis.append(String.format("with engine evaluation: %.2f", context.engineScore));
            if (context.personalityBonus > 0) {
                detailedAnalysis.append(String.format(", personality bonus: +%.2f", context.personalityBonus));
            }
            detailedAnalysis.append(". ").append(context.analysis);
        }

        // Store for potential voice synthesis
        lastMoveExplanation.setValue(detailedAnalysis.toString());

        Log.d(TAG, "✨ Personality context updated for move " + actualMove + " - This is revolutionary!");
    }

    /**
     * DEPRECATED: Keep for backward compatibility but prefer the version with actualMove
     */
    private void updatePersonalityContext(GameRepository.PersonalityMoveContext context) {
        // Fallback to original method if no move provided
        updatePersonalityContext(context, context != null ? context.move : "unknown");
    }

    /**
     * Get historical move suggestion for current position
     */
    public void getHistoricalMoveSuggestion(Callback<String> callback) {
        String master = personalityMaster.getValue();
        if (master == null) master = "tal";

        gameRepository.getHistoricalMoveSuggestion(master, new GameRepository.Callback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) {
                    callback.onResult(result);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Historical suggestion error: " + errorMessage);
                if (callback != null) {
                    callback.onResult(null);
                }
            }
        });
    }

    /**
     * 🎭 PERSONALITY ENGINE CONFIGURATION METHODS
     */
    public void configurePersonalityEngine(String master, float weight, boolean enabled) {
        Log.d(TAG, String.format("🎭 CONFIGURING PERSONALITY ENGINE: %s (weight=%.2f, enabled=%s)",
                master, weight, enabled));

        // Update LiveData
        personalityMaster.setValue(master);
        personalityWeight.setValue(weight);
        personalityEngineEnabled.setValue(enabled);

        // Configure the repository
        gameRepository.configurePersonalityEngine(master, weight, enabled);

        // Update status message to show the magic happening
        String masterName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(master);
        if (enabled) {
            statusMessage.setValue(String.format("🎭 Playing like %s! (Personality: %.0f%%)",
                    masterName, weight * 100));
        } else {
            statusMessage.setValue("🤖 Pure engine mode");
        }
    }

    /**
     * Toggle between personality engine and pure engine mode
     */
    public void togglePersonalityEngine() {
        boolean currentState = Boolean.TRUE.equals(personalityEngineEnabled.getValue());
        boolean newState = !currentState;

        String master = personalityMaster.getValue();
        float weight = personalityWeight.getValue() != null ? personalityWeight.getValue() : 0.3f;

        configurePersonalityEngine(master != null ? master : "tal", weight, newState);

        String mode = newState ? "🎭 Personality mode" : "🤖 Pure engine mode";
        statusMessage.setValue(mode + " activated!");
    }

    /**
     * Adjust personality influence (0.0 = pure engine, 1.0 = maximum personality)
     */
    public void adjustPersonalityWeight(float weight) {
        float clampedWeight = Math.max(0.0f, Math.min(1.0f, weight));

        String master = personalityMaster.getValue();
        boolean enabled = Boolean.TRUE.equals(personalityEngineEnabled.getValue());

        configurePersonalityEngine(master != null ? master : "tal", clampedWeight, enabled);

        statusMessage.setValue(String.format("🎚️ Personality influence: %.0f%%", clampedWeight * 100));
    }

    /**
     * Switch to a different chess master personality
     */
    public void switchPersonalityMaster(String newMaster) {
        float weight = personalityWeight.getValue() != null ? personalityWeight.getValue() : 0.3f;
        boolean enabled = Boolean.TRUE.equals(personalityEngineEnabled.getValue());

        configurePersonalityEngine(newMaster, weight, enabled);

        String masterName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(newMaster);
        statusMessage.setValue(String.format("🎭 Now playing like %s!", masterName));
    }

    /**
     * ENHANCED: Start new game with evaluation tracking reset
     */
    public void newGame() {
        Log.d(TAG, "Starting new game");

        executorService.execute(() -> {
            gameRepository.newGame();

            // Reset evaluation tracking
            if (evaluationTracker != null) {
                evaluationTracker.resetTracking();
            }

            mainHandler.post(() -> {
                moveHistory.setValue(new ArrayList<>());
                currentFEN.setValue("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                currentEvaluation.setValue(0.0f);
                isMatePosition.setValue(false);
                mateInMoves.setValue(0);
                isPlayerTurn.setValue(true);
                isGameOver.setValue(false);

                // Reset commentary-related LiveData
                _lastEvaluationSwing.setValue(null);
                _moveExplanation.setValue(null);
                _isHistoricalMove.setValue(false);
                _masterQuote.setValue(null);

                Log.d(TAG, "New game initialized with evaluation tracking reset");
            });
        });
    }

    public void triggerMoveAnimation(String moveUci) {
        // Convert UCI format to board coordinates
        int fromCol = moveUci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(moveUci.charAt(1));
        int toCol = moveUci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(moveUci.charAt(3));

        // Trigger animation
        _animateMoveEvent.setValue(new int[]{fromRow, fromCol, toRow, toCol});

        // Also trigger last move highlight
        _lastMoveEvent.setValue(new int[]{fromRow, fromCol, toRow, toCol});
    }

    /**
     * Enhanced game initialization that includes personality setup
     */
    public void newGameWithPersonality(String playerColor, int skillLevel, int engineElo,
                                       String masterPersonality, float personalityWeight) {
        Log.d(TAG, String.format("🎮 Starting new game with PERSONALITY: %s playing like %s (%.0f%% influence)",
                playerColor, masterPersonality, personalityWeight * 100));

        // Standard game initialization
        newGameWithConfiguration(playerColor, skillLevel, engineElo);

        // Configure personality engine
        configurePersonalityEngine(masterPersonality, personalityWeight, true);

        String masterName = FineTunedModelManager.getInstance(getApplication()).getMasterDisplayName(masterPersonality);
        statusMessage.setValue(String.format("🎭 New game: You vs %s! Revolutionary AI gameplay activated.", masterName));
    }

    /**
     * NEW: Configure automatic commentary system
     */
    public void configureAutoCommentary(boolean enabled) {
        this.autoCommentaryEnabled = enabled;

        if (enabled && evaluationTracker == null) {
            // Initialize evaluation tracker
            Context context = getApplication().getApplicationContext();
            evaluationTracker = EvaluationTracker.getInstance(context);

            // Set up swing listener for additional processing
            evaluationTracker.setEvaluationSwingListener(new EvaluationTracker.EvaluationSwingListener() {
                @Override
                public void onEvaluationSwingDetected(EvaluationTracker.EvaluationSwing swing) {
                    // Additional processing if needed
                    Log.d(TAG, "Evaluation swing detected: " + swing);
                }

                @Override
                public void onSignificantSwingDetected(EvaluationTracker.EvaluationSwing swing, String autoCommentary) {
                    // Update LiveData with the automatic commentary
                    mainHandler.post(() -> {
                        _masterQuote.setValue(autoCommentary);
                    });
                }
            });

            Log.d(TAG, "✅ Auto-commentary system enabled");
        } else if (!enabled && evaluationTracker != null) {
            evaluationTracker.setTrackingEnabled(false);
            Log.d(TAG, "❌ Auto-commentary system disabled");
        }
    }

    // Add these getter methods for the UI to observe personality state
    public LiveData<Boolean> getPersonalityEngineEnabled() { return personalityEngineEnabled; }
    public LiveData<String> getPersonalityMaster() { return personalityMaster; }
    public LiveData<Float> getPersonalityWeight() { return personalityWeight; }
    
    // Add getter for game repository access
    public GameRepository getGameRepository() { return gameRepository; }

    /**
     * Build game context from move history for reasoning engine
     */
    private String buildGameContext() {
        List<String> history = moveHistory.getValue();
        if (history == null || history.isEmpty()) {
            return "Starting position";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("Move history: ");
        for (int i = 0; i < Math.min(10, history.size()); i++) {
            if (i > 0) context.append(" ");
            context.append(String.format("%d.%s", (i/2) + 1, i % 2 == 0 ? "" : ".."));
            context.append(history.get(i));
        }
        
        if (history.size() > 10) {
            context.append(" (").append(history.size() - 10).append(" more moves)");
        }
        
        return context.toString();
    }

    /**
     * Handle move result from reasoning or traditional engine
     */
    private void handleMoveResult(String move, String explanation, boolean isReasoningMove, boolean isHighConfidence) {
        if (move == null || move.trim().isEmpty()) {
            Log.w(TAG, "⚠️ Engine returned empty move, using standard engine");
            requestStandardEngineMove();
            return;
        }

        // Get current move history
        List<String> history = moveHistory.getValue();
        if (history == null) {
            history = new ArrayList<>();
        }

        // Add the engine's move to history
        history.add(move);

        // Apply ALL moves to keep state in sync
        boolean success = gameRepository.applyMoves(history);

        if (success) {
            // Update LiveData with new state
            String newFen = gameRepository.getCurrentFEN();
            currentFEN.setValue(newFen);

            // Create a defensive copy to trigger LiveData
            List<String> updatedHistory = new ArrayList<>(history);
            moveHistory.setValue(updatedHistory);

            // Update GameHistoryManager for engine moves
            GameHistoryManager.getInstance().addMove(move);

            // Update the central game state repository
            GameStateRepository.updateState(newFen, updatedHistory, playerColor);

            // Request evaluation after engine move
            requestPositionEvaluation();

            // Update personality context
            if (isReasoningMove) {
                lastMoveExplanation.setValue(explanation);
                isHistoricalMove.setValue(false); // Reasoning moves are not historical by default
                
                String masterName = FineTunedModelManager.getInstance(getApplication())
                        .getMasterDisplayName(personalityMaster.getValue());
                
                if (isHighConfidence) {
                    statusMessage.setValue(String.format("🧠 %s played %s - high confidence reasoning! Your turn.",
                            masterName, move));
                } else {
                    statusMessage.setValue(String.format("🧠 %s played %s - analyzed move. Your turn.",
                            masterName, move));
                }
            } else {
                // Get the personality context for traditional moves
                GameRepository.PersonalityMoveContext context = gameRepository.getLastPersonalityContext();
                if (context != null) {
                    updatePersonalityContext(context, move);
                }
                
                String masterName = FineTunedModelManager.getInstance(getApplication())
                        .getMasterDisplayName(personalityMaster.getValue());
                
                if (context != null && context.isHistoricalMatch) {
                    statusMessage.setValue(String.format("🎯 %s played %s - a historical move! Your turn.",
                            masterName, move));
                } else {
                    statusMessage.setValue(String.format("🎭 %s played %s. Your turn.",
                            masterName, move));
                }
            }

            isPlayerTurn.setValue(true);

            // Check for game end conditions
            checkGameEndConditions();

        } else {
            // Handle error - fall back to standard engine
            Log.e(TAG, "❌ Failed to apply move, using standard engine");
            requestStandardEngineMove();
        }
    }

    /**
     * Set target ELO for reasoning engine
     */
    public void setTargetElo(int elo) {
        this.targetElo = Math.max(1200, Math.min(3200, elo));
        Log.d(TAG, String.format("🎯 Target ELO set to %d", this.targetElo));
    }
    
    public int getTargetElo() {
        return targetElo;
    }

    @Override
    protected void onCleared() {
        // Clean up resources when ViewModel is destroyed
        gameRepository.cleanup();
        executorService.shutdown();
        super.onCleared();
    }

    // Add this interface inside the class
    public interface Callback<T> {
        void onResult(T result);
    }
}