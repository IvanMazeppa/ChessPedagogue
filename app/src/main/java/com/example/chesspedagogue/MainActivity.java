package com.example.chesspedagogue;

import android.widget.Button;
import java.util.ArrayList;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.os.Vibrator;
import android.os.VibrationEffect;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Core components
    private StockfishManager engine;
    private ChessGameManager gameManager;
    private ChessBoardView boardView;

    // Game state
    private String playerColorChoice;
    private boolean isPlayerTurn = true;
    private String lastMove = "";

    // UI components
    private TextView statusTextView;

    private Vibrator vibrator;

    // Move history tracking
    private TextView moveHistoryTextView;
    private StringBuilder moveHistoryBuilder = new StringBuilder();
    private int moveNumber = 1;
    private Button analyzeGameButton;


    // Combine the two onCreate methods - keep just one:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get settings from SplashActivity
        playerColorChoice = getIntent().getStringExtra("PLAYER_COLOR");
        if (playerColorChoice == null) playerColorChoice = "white";
        int skillLevel = getIntent().getIntExtra("SKILL_LEVEL", 10);

        // Find UI components
        boardView = findViewById(R.id.chessBoardView);
        statusTextView = findViewById(R.id.statusTextView);

        // Add this to your onCreate method, after finding boardView:
        moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
        moveHistoryBuilder = new StringBuilder();
        moveNumber = 1;

        // Add this to your onCreate method after finding other UI elements
        analyzeGameButton = findViewById(R.id.analyzeGameButton);
        analyzeGameButton.setOnClickListener(v -> openGameAnalysis());
        // Initially disable the button until we have moves to analyze
        analyzeGameButton.setEnabled(false);

        // Initialize sound manager
        SoundManager.initialize(this);

        // Get vibrator service
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Show initial status
        updateStatusText("Starting game...");

        // Initialize the engine using the native library approach
        initializeStockfishEngine(skillLevel);
    }

    /**
     * Open the game analysis activity with the current move history
     */
    private void openGameAnalysis() {
        if (gameManager == null) {
            Toast.makeText(this, "No game data available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an ArrayList from the move history
        ArrayList<String> movesForAnalysis = new ArrayList<>();

        // Log what we're passing
        Log.d(TAG, "Passing move history to analysis screen:");
        for (String move : gameManager.getMoveHistory()) {
            Log.d(TAG, "Move: " + move);
            movesForAnalysis.add(move);
        }

        if (movesForAnalysis.isEmpty()) {
            Toast.makeText(this, "No moves to analyze yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Launch the analysis activity
        Intent intent = new Intent(this, GameAnalysisActivity.class);
        intent.putStringArrayListExtra("MOVE_HISTORY", movesForAnalysis);
        startActivity(intent);
    }


    // Update the updateMoveHistory method
    private void updateMoveHistory(String move, boolean isWhiteMove) {
        Log.d(TAG, "Updating move history: " + move + ", White move: " + isWhiteMove);

        if (moveHistoryTextView == null) {
            moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
            if (moveHistoryTextView == null) {
                Log.e(TAG, "Could not find moveHistoryTextView!");
                return;
            }
        }

        if (isWhiteMove) {
            // Start a new move pair
            moveHistoryBuilder.append(moveNumber).append(". ").append(move);
        } else {
            // Complete the move pair and increment move number
            moveHistoryBuilder.append(" ").append(move).append("\n");
            moveNumber++;
        }

        final String historyText = moveHistoryBuilder.toString();
        Log.d(TAG, "Setting move history text: " + historyText);

        // Update on UI thread
        runOnUiThread(() -> {
            if (moveHistoryTextView != null) {
                moveHistoryTextView.setText(historyText);
            }
        });
    }

    /**
     * Initialize the Stockfish chess engine
     */
    private void initializeStockfishEngine(int skillLevel) {
        try {
            // Try using the native library first
            File engineFile = new File(getApplicationInfo().nativeLibraryDir, "libstockfish.so");

            // If not found, try extracting from assets as fallback
            if (!engineFile.exists()) {
                Log.d(TAG, "Stockfish not found in native library dir, trying assets fallback");
                try {
                    engineFile = Utils.copyAssetToExecutableDir(this, "stockfish", "stockfish");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to extract Stockfish from assets", e);
                }
            }

            if (!engineFile.exists() || !engineFile.canExecute()) {
                Toast.makeText(this, "Stockfish engine not found", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "Found Stockfish at: " + engineFile.getAbsolutePath() +
                    ", can execute: " + engineFile.canExecute());

            // Initialize the engine
            engine = new StockfishManager();
            if (engine.startEngine(engineFile.getAbsolutePath())) {
                // Configure the engine
                engine.setSkillLevel(skillLevel);
                engine.newGame();

                // Initialize game manager
                gameManager = new ChessGameManager(engine);

                // Set up the board based on player color
                setupChessBoard();

                Toast.makeText(this, "Stockfish engine initialized successfully",
                        Toast.LENGTH_SHORT).show();

                updateStatusText("Game ready! " +
                        (playerColorChoice.equalsIgnoreCase("white") ? "White" : "Black") +
                        " to move");
            } else {
                Toast.makeText(this, "Failed to start Stockfish engine",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Stockfish", e);
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Set up the chess board and move handling
     */
    private void setupChessBoard() {
        // Flip the board if the player is playing as black
        if (playerColorChoice.equalsIgnoreCase("black")) {
            boardView.setFlipped(true);
            isPlayerTurn = false;

            // If player is black, let engine (white) make the first move
            makeEngineMove();
        } else {
            boardView.setFlipped(false);
            isPlayerTurn = true;
        }

        // Set up board click listener with proper selection handling
        boardView.setOnSquareTapListener(new ChessBoardView.OnSquareTapListener() {
            @Override
            public void onSquareTapped(int row, int col) {
                handleBoardTap(row, col);
            }
        });
    }

    // Create a helper method for haptic feedback
    private void performHapticFeedback(String moveType) {
        if (vibrator != null && vibrator.hasVibrator()) {
            switch (moveType) {
                case "move":
                    vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
                    break;
                case "capture":
                    // Stronger vibration for captures
                    vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
                    break;
                case "check":
                    // Pattern for check - two quick pulses
                    long[] pattern = {0, 30, 80, 30};
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                    break;
            }
        }
    }

    // Add this helper method to determine move type
    private String determineMoveType(String moveUci) {
        // Convert coordinates
        int fromCol = moveUci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(moveUci.charAt(1));
        int toCol = moveUci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(moveUci.charAt(3));

        // Check if it's a capture
        char pieceAtDestination = boardView.getPieceAt(toRow, toCol);
        if (pieceAtDestination != ' ') {
            return "capture";
        }

        // We would need to check if this move puts opponent in check
        // This requires more game state knowledge, but for now we can simplify
        return "move";
    }

    /**
     * Show legal moves for the selected piece
     */
    private void showLegalMovesFor(int row, int col) {
        // Clear previous highlights
        boardView.clearHighlightedSquares();

        // Get legal moves from the engine
        List<String> legalMoves = engine.getLegalMovesForPiece(row, col);

        // Add highlight for each legal move
        for (String move : legalMoves) {
            if (move.length() >= 4) {
                // Convert the target square coordinates
                int targetCol = move.charAt(2) - 'a';
                int targetRow = 8 - (move.charAt(3) - '0');
                boardView.addHighlightedSquare(targetRow, targetCol);
            }
        }

        // Refresh the board view
        boardView.invalidate();
    }

    private void handleBoardTap(int row, int col) {
        Log.d(TAG, "Tap received at: " + row + "," + col);

        // Get the currently selected square, if any
        int selectedRow = boardView.getSelectedRow();
        int selectedCol = boardView.getSelectedCol();

        // Get the piece at the tapped square
        char tappedPiece = boardView.getPieceAt(row, col);
        boolean isTappingOwnPiece = isPlayerPiece(tappedPiece);

        Log.d(TAG, "Selected: " + selectedRow + "," + selectedCol +
                " Tapped: " + row + "," + col +
                " Own piece: " + isTappingOwnPiece);

        // Case 1: Tapping the already selected piece - deselect it
        if (selectedRow == row && selectedCol == col) {
            Log.d(TAG, "Deselecting piece");
            boardView.clearSelectionHighlight();
            boardView.clearHighlightedSquares();
            return;
        }

        // Case 2: No piece selected yet, and tapping own piece
        if (selectedRow == -1 && isTappingOwnPiece && isPlayerTurn) {
            Log.d(TAG, "Selecting new piece");
            boardView.setSelectedSquare(row, col);
            showLegalMovesFor(row, col);
            return;
        }

        // Case 3: A piece is already selected
        if (selectedRow != -1) {
            // Case 3a: Tapping own piece - change selection
            if (isTappingOwnPiece && isPlayerTurn) {
                Log.d(TAG, "Changing selection to new piece");
                boardView.clearSelectionHighlight();
                boardView.clearHighlightedSquares();
                boardView.setSelectedSquare(row, col);
                showLegalMovesFor(row, col);
                return;
            }

            // Case 3b: Tapping destination square - attempt move
            String moveUci = convertToUCI(selectedRow, selectedCol, row, col);
            Log.d(TAG, "Attempting move: " + moveUci);

            if (engine.isLegalMove(moveUci)) {
                Log.d(TAG, "Move is legal, executing");
                executeMoveAndRespond(moveUci);
            } else {
                Log.d(TAG, "Move is illegal, ignoring");
                // Keep the current selection
            }
        }
    }


    /**
     * Check if a piece belongs to the player
     */
    private boolean isPlayerPiece(char piece) {
        if (piece == ' ') return false;

        boolean isPieceWhite = Character.isUpperCase(piece);
        boolean isPlayerWhite = playerColorChoice.equalsIgnoreCase("white");

        return isPieceWhite == isPlayerWhite;
    }

    /**
     * Execute a player move and get the engine's response
     */
    private void executeMoveAndRespond(String moveUci) {
        // Clear highlights
        boardView.clearSelectionHighlight();
        boardView.clearHighlightedSquares();

        // Determine move type for appropriate feedback
        String moveType = determineMoveType(moveUci);

        // Provide feedback based on move type
        SoundManager.playSound(moveType);
        performHapticFeedback(moveType);

        // Make the player's move
        gameManager.makeMove(moveUci);
        // ► grab coords before board refresh
        int fCol = moveUci.charAt(0)-'a', fRow = 8-(moveUci.charAt(1)-'0');
        int tCol = moveUci.charAt(2)-'a', tRow = 8-(moveUci.charAt(3)-'0');

        updateBoardDisplay();              // refresh pieces on their new squares
        boardView.animateMove(fRow,fCol,tRow,tCol);   // ► slide the piece

        // Rest of your existing code...

         updateBoardDisplay();


        // Update game state
        isPlayerTurn = false;
        lastMove = moveUci;

        // Update move history with the player's move
        boolean isWhiteMove = playerColorChoice.equalsIgnoreCase("white");
        updateMoveHistory(convertToAlgebraic(moveUci), isWhiteMove);

        updateStatusText("You moved " + convertToAlgebraic(moveUci) + ". Engine thinking...");

        // Let the engine respond
        makeEngineMove();
    }

    /**
     * Have the engine make a move
     */
    private void makeEngineMove() {
        // Have the engine make its move
        String engineMove = engine.getBestMove(1000);

        if (engineMove != null && !engineMove.isEmpty()) {
            gameManager.makeMove(engineMove);
            // ► coords for animation
            int fCol = engineMove.charAt(0)-'a', fRow = 8-(engineMove.charAt(1)-'0');
            int tCol = engineMove.charAt(2)-'a', tRow = 8-(engineMove.charAt(3)-'0');

            updateBoardDisplay();
            boardView.animateMove(fRow,fCol,tRow,tCol);   // ► animate engine move

            // Update move history with the engine's move
            boolean isWhiteMove = !playerColorChoice.equalsIgnoreCase("white");
            updateMoveHistory(convertToAlgebraic(engineMove), isWhiteMove);

            // Update game state
            isPlayerTurn = true;
            lastMove = engineMove;
            updateStatusText("Engine moved " + convertToAlgebraic(engineMove) + ". Your turn.");

            // Check game status (checkmate, stalemate, etc.)
            checkGameStatus();
        } else {
            updateStatusText("Engine couldn't find a move. Game may be over.");
        }
    }
    /**
     * Update the chess board display
     */
    private void updateBoardDisplay() {
        boardView.updateBoardFromFen(engine.getCurrentFEN());
    }

    /**
     * Convert board coordinates to UCI format
     */
    private String convertToUCI(int fromRow, int fromCol, int toRow, int toCol) {
        // Convert board coordinates to UCI format (e.g., "e2e4")
        char fromFile = (char) ('a' + fromCol);
        int fromRank = 8 - fromRow;
        char toFile = (char) ('a' + toCol);
        int toRank = 8 - toRow;
        return "" + fromFile + fromRank + toFile + toRank;
    }


    /**
     * Convert UCI format to algebraic notation (improved)
     */
    private String convertToAlgebraic(String uciMove) {
        if (uciMove == null || uciMove.length() < 4) return "?";

        // Get the piece at the start square
        int fromCol = uciMove.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(uciMove.charAt(1));
        char piece = boardView.getPieceAt(fromRow, fromCol);

        // Get letter for the piece (uppercase)
        String pieceLetter = "";
        switch (Character.toUpperCase(piece)) {
            case 'R': pieceLetter = "R"; break;
            case 'N': pieceLetter = "N"; break;
            case 'B': pieceLetter = "B"; break;
            case 'Q': pieceLetter = "Q"; break;
            case 'K': pieceLetter = "K"; break;
            // No letter for pawns
        }

        // Get the destination square
        String destSquare = uciMove.substring(2, 4);

        return pieceLetter + destSquare;
    }

    /**
     * Check if the game has ended (checkmate, stalemate, etc.)
     */
    // Update your checkGameStatus method to enable analysis when game ends
    private void checkGameStatus() {
        // Get and parse the FEN to check for check/checkmate
        String fen = engine.getCurrentFEN();
        if (fen == null) return;

        // Check if the game has ended
        boolean gameEnded = false;
        String endReason = "";

        // Add your existing game end detection logic here

        // For now, we'll just enable the analyze button once there are some moves
        if (moveNumber > 1) {
            analyzeGameButton.setEnabled(true);
        }

        // If game ended, show a dialog offering analysis
        if (gameEnded) {
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage(endReason + "\nWould you like to analyze this game?")
                    .setPositiveButton("Analyze", (dialog, which) -> openGameAnalysis())
                    .setNegativeButton("New Game", (dialog, which) -> {
                        // Reset the game
                        gameManager.newGame();
                        updateBoardDisplay();
                        moveHistoryBuilder = new StringBuilder();
                        moveNumber = 1;
                        moveHistoryTextView.setText("");
                        isPlayerTurn = playerColorChoice.equalsIgnoreCase("white");
                        if (!isPlayerTurn) {
                            makeEngineMove();
                        }
                    })
                    .show();
        }
    }

    /**
     * Check if the current player has any legal moves
     */
    private boolean hasLegalMoves() {
        // This is a simplified implementation
        // A real one would ask the engine if there are any legal moves

        // For now, assume there are legal moves
        return true;
    }

    /**
     * Update the status text view
     */
    private void updateStatusText(String message) {
        if (statusTextView != null) {
            statusTextView.setText(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.stopEngine();
        }
        SoundManager.release(); // Add this line
    }
}