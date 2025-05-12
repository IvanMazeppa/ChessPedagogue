// GameViewModel.java

package com.example.chesspedagogue.viewmodel;
import com.example.chesspedagogue.GameStateRepository;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chesspedagogue.GameStateRepository;
import com.example.chesspedagogue.MoveHistoryObserver;
import com.example.chesspedagogue.model.GameState;
import com.example.chesspedagogue.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private static final String TAG = "GameViewModel";
    // Add these fields to your GameViewModel class
    private final MoveHistoryObserver moveHistoryObserver = new MoveHistoryObserver();

    // Add this method to your GameViewModel class
    public void addMoveHistoryListener(MoveHistoryObserver.MoveHistoryListener listener) {
        moveHistoryObserver.addListener(listener);
    }

    // Add this interface inside the class
    public interface Callback<T> {
        void onResult(T result);
    }

    private final GameRepository gameRepository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Add the missing playerColor field here
    private String playerColor = "white"; // Default to white

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
                    return new int[] {r, c};
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
    }

    // Public getter methods for the LiveData objects
    public LiveData<GameState> getGameState() { return gameStateLiveData; }
    public LiveData<String> getCurrentFEN() { return currentFEN; }
    public LiveData<List<String>> getMoveHistory() { return moveHistory; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> isPlayerTurn() { return isPlayerTurn; }
    public LiveData<Boolean> isGameOver() { return isGameOver; }
    public LiveData<String> getWinner() { return winner; }

    // Methods to handle game actions
    // In GameViewModel.java - make sure state is properly updated
    // Fix the makePlayerMove method
    // In GameViewModel.java - find the makePlayerMove method
    public void makePlayerMove(String move) {
        if (!isEngineReady()) {
            statusMessage.setValue("Engine not ready. Please restart the game.");
            return;
        }

        // Validate move first
        if (gameRepository.isLegalMove(move)) {
            // Get current move history
            List<String> history = moveHistory.getValue();
            if (history == null) {
                history = new ArrayList<>();
            }

            // Add the new move to history
            history.add(move);

            // Apply ALL moves to the repository
            boolean success = gameRepository.applyMoves(history);

            if (success) {
                // Important: Update LiveData with new state
                String newFen = gameRepository.getCurrentFEN();
                currentFEN.setValue(newFen);

                // Create a new list to trigger observers
                List<String> updatedHistory = new ArrayList<>(history);
                moveHistory.setValue(updatedHistory);

                // Notify move history observers
                moveHistoryObserver.notifyMoveMade(move, newFen, updatedHistory);

                // IMPORTANT NEW CODE - Update the central game state repository
                GameStateRepository.updateState(newFen, updatedHistory, playerColor);

                // Trigger the animation
                triggerMoveAnimation(move);

                isPlayerTurn.setValue(false);
                statusMessage.setValue("You moved " + move + ". Engine thinking...");

                // Request engine to make its move
                requestEngineMove();
            } else {
                statusMessage.setValue("Move failed to execute. Please try again.");
            }
        } else {
            statusMessage.setValue("Invalid move. Please try again.");
        }
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

        // Apply each move
        for (String move : moves) {
            gameRepository.makeMove(move);
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

        // If it's the engine's turn, make it move
        if (!isPlayersTurn) {
            requestEngineMove();
        }
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

    // In GameViewModel.java - find the requestEngineMove method
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

                    // IMPORTANT NEW CODE - Update the central game state repository
                    GameStateRepository.updateState(newFen, updatedHistory, playerColor);

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
        currentFEN.setValue(gameRepository.getCurrentFEN());
        moveHistory.setValue(new ArrayList<>());
        isGameOver.setValue(false);
        winner.setValue(null);

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
}