// GameViewModel.java - COMPLETE UPDATED VERSION

package com.example.chesspedagogue.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chesspedagogue.GameStateRepository;
import com.example.chesspedagogue.GameHistoryManager; // NEW IMPORT
import com.example.chesspedagogue.MoveHistoryObserver;
import com.example.chesspedagogue.StockfishManager;
import com.example.chesspedagogue.model.GameState;
import com.example.chesspedagogue.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private static final String TAG = "GameViewModel";
    // Add these fields to your GameViewModel class
    private final MoveHistoryObserver moveHistoryObserver = new MoveHistoryObserver();
    private final GameRepository gameRepository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
    private Handler evaluationHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingEvaluationUpdate;

    // Add the missing playerColor field here
    private String playerColor = "white"; // Default to white

    public GameViewModel(Application application) {
        super(application);
        // Initialize the repository - this will handle Stockfish and game data
        gameRepository = new GameRepository(application);

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

        // Cancel any pending evaluation update
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
     * NEW METHOD: Execute a validated move
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
                // Update the FEN string from the repository
                String newFen = gameRepository.getCurrentFEN();
                Log.d(TAG, "📍 New position after move: " + newFen);

                if (newFen.equals(currentFEN.getValue())) {
                    Log.w(TAG, "⚠️ Position did not change after move - trying alternative approach");

                    // Try applying all moves from scratch
                    boolean altSuccess = gameRepository.applyMoves(newHistory);
                    if (altSuccess) {
                        newFen = gameRepository.getCurrentFEN();
                        Log.d(TAG, "✅ Alternative approach succeeded: " + newFen);
                    } else {
                        Log.e(TAG, "❌ Alternative approach also failed");
                        handleMoveExecutionFailure(move);
                        return;
                    }
                }

                // ===== CRITICAL FIX: Update both systems! =====
                // 1. Update ViewModel's LiveData
                currentFEN.setValue(newFen);
                moveHistory.setValue(newHistory);

                // 2. Update GameHistoryManager (THIS WAS MISSING!)
                GameHistoryManager.getInstance().addMove(move);

                // 3. Notify move history observers
                moveHistoryObserver.notifyMoveMade(move, newFen, newHistory);

                // 4. Update the central game state repository
                GameStateRepository.updateState(newFen, newHistory, playerColor);

                // Trigger the animation
                triggerMoveAnimation(move);

                // Request evaluation after player move
                requestPositionEvaluation();

                isPlayerTurn.setValue(false);
                statusMessage.setValue("You moved " + move + ". Engine thinking...");

                // Request engine to make its move
                requestEngineMove();

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

    // ===== CRITICAL FIX: Engine moves also update GameHistoryManager =====
    private void requestEngineMove() {
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

                    // Check for checkmate, stalemate, etc.
                    if (gameRepository.isCheckmate()) {
                        isGameOver.setValue(true);
                        String winnerStr = isPlayerTurn.getValue() ? "Engine" : "You";
                        winner.setValue(winnerStr);
                        statusMessage.setValue("Checkmate! " + winnerStr + " wins!");
                    } else if (gameRepository.isStalemate()) {
                        isGameOver.setValue(true);
                        winner.setValue("Draw");
                        statusMessage.setValue("Stalemate! Game over.");
                    } else {
                        statusMessage.setValue("Engine moved " + engineMove + ". Your turn.");
                    }
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

    public void newGame(String playerColor) {
        // Update the player color
        this.playerColor = playerColor;

        // Reset game state for a new game
        gameRepository.newGame();

        // ===== CRITICAL FIX: Reset GameHistoryManager! =====
        GameHistoryManager.getInstance().clearHistory();

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

    @Override
    protected void onCleared() {
        // Clean up resources when ViewModel is destroyed
        gameRepository.cleanup();
        super.onCleared();
    }

    // Add this interface inside the class
    public interface Callback<T> {
        void onResult(T result);
    }
}