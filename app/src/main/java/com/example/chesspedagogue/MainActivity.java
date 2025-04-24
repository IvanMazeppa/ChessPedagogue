package com.example.chesspedagogue;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Core components
    private StockfishManager engine;
    private ChessGameManager gameManager;
    private ChessBoardView boardView;
    private ChessCoachManager chessCoach;
    private SpeechRecognitionManager speechRecognitionManager;

    // Game state
    private String playerColorChoice;
    private boolean isPlayerTurn = true;
    private String lastMove = "";

    // UI components
    private TextView statusTextView;
    private CardView coachMessageCard;
    private TextView coachMessageText;
    private Button askFollowUpButton;
    private Button dismissCoachButton;
    private FloatingActionButton chessCoachButton;
    private FloatingActionButton voiceInputButton;

    private Vibrator vibrator;
    private static final int PERMISSION_REQUEST_MICROPHONE = 101;

    // Move history tracking
    private TextView moveHistoryTextView;
    private StringBuilder moveHistoryBuilder = new StringBuilder();
    private int moveNumber = 1;
    private List<String> algebraicMoveHistory = new ArrayList<>();


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
        moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
        moveHistoryBuilder = new StringBuilder();
        moveNumber = 1;

        // Chess coach components
        coachMessageCard = findViewById(R.id.coachMessageCard);
        coachMessageText = findViewById(R.id.coachMessageText);
        askFollowUpButton = findViewById(R.id.askFollowUpButton);
        dismissCoachButton = findViewById(R.id.dismissCoachButton);
        chessCoachButton = findViewById(R.id.chessCoachButton);
        voiceInputButton = findViewById(R.id.voiceInputButton);

        // Initialize the chess coach
        initializeChessCoach();

        // Setup coach button click listener
        chessCoachButton.setOnClickListener(v -> showCoachDialog());

        // Setup voice input button
        voiceInputButton.setOnClickListener(v -> startVoiceRecognition());

        // Setup other coach UI elements
        askFollowUpButton.setOnClickListener(v -> promptForFollowUpQuestion());
        dismissCoachButton.setOnClickListener(v -> hideCoachMessage());

        // Add this code right around where your other buttons are initialized
        FloatingActionButton conversationButton = findViewById(R.id.conversationButton);
        if (conversationButton != null) {
            conversationButton.setOnClickListener(v -> startChessConversation());
        }

        // Initialize sound manager
        SoundManager.initialize(this);

        // Get vibrator service
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Show initial status
        updateStatusText("Starting game...");

        updateMainMenuOptions();

        // Initialize speech recognition
        initializeSpeechRecognition();

        // Check for and prompt for API key if needed
        promptForApiKeyIfNeeded();

        // Initialize the engine using the native library approach
        initializeStockfishEngine(skillLevel);
    }

    /**
     * Initialize the Chess Coach manager
     */
    private void initializeChessCoach() {
        chessCoach = ChessCoachManager.getInstance(this);

        // Get API key from secure storage if available
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey != null && !apiKey.isEmpty()) {
            chessCoach.setApiKey(apiKey);
        }
    }

    /**
     * Initialize speech recognition
     */
    private void initializeSpeechRecognition() {
        // Check for microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_MICROPHONE);
        } else {
            setupSpeechRecognition();
        }
    }

    private void setupSpeechRecognition() {
        speechRecognitionManager = new SpeechRecognitionManager(this);
    }

    /**
     * Start voice recognition to interact with the coach
     */
    private void startVoiceRecognition() {
        // Stop any ongoing TTS first
        chessCoach.stopSpeaking();

        // Show feedback to user
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();

        // Start speech recognition
        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                // Process the recognized speech
                processVoiceCommand(text);
            }

            @Override
            public void onSpeechError(String error) {
                Toast.makeText(MainActivity.this,
                        "Speech recognition error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Process voice commands from the user
     */
    private void processVoiceCommand(String command) {
        Log.d(TAG, "Voice command: " + command);

        // Show what was recognized
        Toast.makeText(this, "You said: " + command, Toast.LENGTH_SHORT).show();

        // Process chess move commands (e.g., "move pawn to e4")
        if (command.toLowerCase().contains("move ")) {
            // Extract the move information and try to execute it
            // This would require natural language parsing to UCI format
            // For now, we'll just pass it to the AI coach
            showLoading("Coach is analyzing your request...");
            chessCoach.sendMessage(command, new ChessCoachCallback());
            return;
        }

        // Process coach questions
        if (command.toLowerCase().contains("coach") ||
                command.toLowerCase().contains("advice") ||
                command.toLowerCase().contains("help")) {
            // Send to the coach
            showLoading("Coach is thinking...");
            chessCoach.sendMessage(command, new ChessCoachCallback());
            return;
        }

        // Default: treat as a general question to the coach
        showLoading("Coach is considering your question...");
        chessCoach.sendMessage(command, new ChessCoachCallback());
    }

    /**
     * Show dialog for interacting with the chess coach
     */
    /**
     * Show dialog for interacting with the chess coach
     */
    private void showCoachDialog() {
        String[] options = {
                "Get advice on my position",
                "Ask about my last move",
                "General chess question",
                "Cancel"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chess Coach");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Get advice
                    String fen = engine.getCurrentFEN();
                    showLoading("Coach is analyzing your game...");
                    // Use the enhanced method with move history
                    chessCoach.getEnhancedChessAdvice(fen, algebraicMoveHistory,
                            playerColorChoice, new ChessCoachCallback());
                    break;

                case 1: // Ask about last move
                    if (lastMove.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No moves played yet", Toast.LENGTH_SHORT).show();
                    } else {
                        showLoading("Coach is analyzing your move...");
                        // We can also send move history here for better context
                        String currentFen = engine.getCurrentFEN();
                        String moveQuestion = "Was my last move " + convertToAlgebraic(lastMove) + " good? Why or why not?";

                        // Create context string with board state and move history
                        StringBuilder context = new StringBuilder(moveQuestion);
                        context.append("\n\nCurrent position (FEN): ").append(currentFen);

                        if (!algebraicMoveHistory.isEmpty()) {
                            context.append("\n\nGame moves so far:\n");
                            int moveNum = 1;
                            for (int i = 0; i < algebraicMoveHistory.size(); i += 2) {
                                context.append(moveNum).append(". ");
                                context.append(algebraicMoveHistory.get(i));
                                if (i + 1 < algebraicMoveHistory.size()) {
                                    context.append(" ").append(algebraicMoveHistory.get(i + 1));
                                }
                                context.append("\n");
                                moveNum++;
                            }
                        }

                        chessCoach.sendMessage(context.toString(), new ChessCoachCallback());
                    }
                    break;

                case 2: // General question
                    promptForGeneralQuestion();
                    break;

                case 3: // Cancel
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    /**
     * Prompt for a general chess question
     */
    private void promptForGeneralQuestion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ask the Coach");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("What would you like to ask?");
        builder.setView(input);

        builder.setPositiveButton("Ask", (dialog, which) -> {
            String question = input.getText().toString().trim();
            if (!question.isEmpty()) {
                showLoading("Coach is thinking...");
                chessCoach.sendMessage(question, new ChessCoachCallback());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Prompt for a follow-up question
     */
    private void promptForFollowUpQuestion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Follow-up Question");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Ask your follow-up question");
        builder.setView(input);

        builder.setPositiveButton("Ask", (dialog, which) -> {
            String question = input.getText().toString().trim();
            if (!question.isEmpty()) {
                showLoading("Coach is thinking...");
                chessCoach.sendMessage(question, new ChessCoachCallback());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Show the loading indicator while waiting for the coach
     */
    private void showLoading(String message) {
        coachMessageCard.setVisibility(View.VISIBLE);
        coachMessageText.setText(message);
        askFollowUpButton.setVisibility(View.GONE);
    }

    /**
     * Display the coach's message
     */
    private void showCoachMessage(String message) {
        coachMessageCard.setVisibility(View.VISIBLE);
        coachMessageText.setText(message);
        askFollowUpButton.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the coach message card
     */
    private void hideCoachMessage() {
        coachMessageCard.setVisibility(View.GONE);
        chessCoach.stopSpeaking();
    }

    /**
     * Callback for chess coach responses
     */
    private class ChessCoachCallback implements ChessCoachManager.ChessCoachCallback {
        @Override
        public void onResponseReceived(String response) {
            showCoachMessage(response);
        }

        @Override
        public void onError(String errorMessage) {
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            hideCoachMessage();
        }

        @Override
        public void onSpeechCompleted() {
            // Speech has completed, can perform any needed actions here
        }
    }

    /**
     * Prompt for API key if not already configured
     */
    private void promptForApiKeyIfNeeded() {
        if (!ApiKeyConfig.hasApiKey(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("OpenAI API Key Required");
            builder.setMessage("To use the chess coach feature, you need to enter your OpenAI API key.");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Enter your OpenAI API key");
            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String apiKey = input.getText().toString().trim();
                if (!apiKey.isEmpty()) {
                    ApiKeyConfig.saveApiKey(this, apiKey);
                    chessCoach.setApiKey(apiKey);
                    Toast.makeText(this, "API key saved", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
                Toast.makeText(this, "Chess coach features will be unavailable", Toast.LENGTH_LONG).show();
            });

            builder.show();
        } else {
            // Use the saved API key
            String apiKey = ApiKeyConfig.getApiKey(this);
            if (apiKey != null && !apiKey.isEmpty()) {
                chessCoach.setApiKey(apiKey);
            }
        }
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

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_MICROPHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSpeechRecognition();
            } else {
                Toast.makeText(this,
                        "Microphone permission is required for voice commands",
                        Toast.LENGTH_LONG).show();
            }
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

        // Add move to history in algebraic notation
        String algebraicMove = convertToAlgebraic(moveUci);
        algebraicMoveHistory.add(algebraicMove);

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

            // Add engine's move to history
            String algebraicMove = convertToAlgebraic(engineMove);
            algebraicMoveHistory.add(algebraicMove);

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
    private void checkGameStatus() {
        // Get and parse the FEN to check for check/checkmate
        String fen = engine.getCurrentFEN();
        if (fen == null) return;

        // This is where you'd add logic to detect checkmate, etc.
        // For now, we'll leave this as a placeholder
    }

    /**
     * Update the move history TextView
     */
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

    // Add this method to MainActivity.java
    private void startChessConversation() {
        Intent intent = new Intent(this, ChessConversationActivity.class);
        intent.putExtra("FEN", engine.getCurrentFEN());
        intent.putStringArrayListExtra("MOVE_HISTORY", new ArrayList<>(algebraicMoveHistory));
        intent.putExtra("PLAYER_COLOR", playerColorChoice);
        startActivity(intent);
    }

    // Add a new conversation button to your existing layout or
// update your existing menu options to include it:
    private void updateMainMenuOptions() {
        // Find your menu button or create a new one
    }

    /**
     * Update the status text view
     */
    private void updateStatusText(String message) {
        if (statusTextView != null) {
            statusTextView.setText(message);
        }
    }

    // Add this method to MainActivity.java
    private void showSaveGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Game");

        // Add an edit text for the game description
        final EditText input = new EditText(this);
        input.setHint("Enter a description for this game");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = input.getText().toString().trim();
                if (description.isEmpty()) {
                    description = "Game on " + new Date().toString();
                }

                // Save the game
                GameDatabaseHelper dbHelper = new GameDatabaseHelper(MainActivity.this);
                dbHelper.saveGame(
                        playerColorChoice,
                        algebraicMoveHistory,
                        engine.getCurrentFEN(),
                        description
                );

                Toast.makeText(MainActivity.this, "Game saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Also add this method to view saved games
    private void openSavedGamesScreen() {
        Intent intent = new Intent(this, SavedGamesActivity.class);
        startActivity(intent);
    }

    // In MainActivity.java, add:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_game) {
            showSaveGameDialog();
            return true;
        }
        else if (id == R.id.action_load_game) {
            openSavedGamesScreen();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.stopEngine();
        }
        SoundManager.release();

        // Clean up chess coach resources
        if (chessCoach != null) {
            chessCoach.shutdown();
        }

        // Clean up speech recognition resources
        if (speechRecognitionManager != null) {
            speechRecognitionManager.release();
        }
    }
}