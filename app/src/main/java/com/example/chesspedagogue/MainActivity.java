package com.example.chesspedagogue;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.chesspedagogue.viewmodel.GameViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private AnimatorSet pulseAnimatorSet;
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;
    private OpenAIService openAIService;
    private String selectedSquare = null;

    private boolean isSpeaking = false;

    private TextToSpeechManager textToSpeechManager;

    // UI elements
    private FloatingActionButton conversationButton;
    private Button recordButton;
    private Button settingsButton;


    private ChallengeData currentChallenge;
    private boolean inChallengeMode = false;

    private FloatingActionButton gameAnalysisButton;
    private FloatingActionButton coachButton;
    private FloatingActionButton speakButton;
    private ChessBoardView chessBoardView;
    private TextView coachMessageText;

    // SimpleRecordService connection
    private SimpleRecordService recordService;
    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SimpleRecordService.LocalBinder binder = (SimpleRecordService.LocalBinder) service;
            recordService = binder.getService();
            isServiceBound = true;
            Log.d(TAG, "🎉 Service connected successfully!");

            // Set up the callback to handle responses
            recordService.setCallback(new SimpleRecordService.ServiceCallback() {
                @Override
                public void onRecordingStarted() {
                    Log.d(TAG, "Recording started callback received!");
                    // Show listening animation
                    runOnUiThread(() -> showListeningFeedback());
                }

                @Override
                public void onRecordingStopped() {
                    Log.d(TAG, "Recording stopped callback received!");
                    // Stop listening animation
                    runOnUiThread(() -> stopListeningFeedback());
                }

                @Override
                public void onProcessingStateChanged(boolean isProcessing) {
                    runOnUiThread(() -> {
                        // Update status text
                        TextView statusTextView = findViewById(R.id.statusTextView);
                        if (statusTextView != null) {
                            statusTextView.setText(isProcessing ? "Coach Tal is thinking..." : "Ready");
                        }
                    });
                }

                @Override
                public void onResponseReceived(String response) {
                    updateCoachMessageText(response);
                    updateResponseUI(response);

                    processAdviceForHighlights(response);

                    // Display the coach's response
                    runOnUiThread(() -> showCoachResponse(response));
                }

                @Override
                public void onResponseCompleted(String response) {
                    // Coach has finished speaking
                    Log.d(TAG, "Speech completed: " + response);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service disconnected unexpectedly");
            isServiceBound = false;
            recordService = null;
        }
    };

    // Add this method to update coach message text
    private void updateCoachMessageText(String message) {
        TextView messageText = findViewById(R.id.coachMessageText);
        if (messageText != null) {
            messageText.setText(message);
        }
    }

    // Add this method for updating response UI (if needed)
    private void updateResponseUI(String response) {
        // This might be redundant with updateCoachMessageText,
        // but we'll add it to fix the compilation error
        // You can customize this if you want to update other UI elements
        View coachCard = findViewById(R.id.coachMessageCard);
        if (coachCard != null && coachCard.getVisibility() != View.VISIBLE) {
            coachCard.setVisibility(View.VISIBLE);
        }
    }
    // In your SimpleRecordService or MainActivity
    public void processAdviceForHighlights(String coachAdvice) {
        // Define highlight colors
        int goodMoveColor = Color.GREEN;
        int badMoveColor = Color.RED;
        int interestingSquareColor = Color.YELLOW;

        // Extract squares from the advice using regex
        List<String> foundSquares = extractChessSquares(coachAdvice);

        // Clear existing highlights
        chessBoardView.clearHighlights();

        // Determine the type of highlight for each square
        for (String square : foundSquares) {
            // Basic sentiment analysis
            if (coachAdvice.contains("best move") && coachAdvice.contains(square)) {
                chessBoardView.highlightSquare(square, goodMoveColor, 5000); // 5 seconds
            } else if (coachAdvice.contains("weak") || coachAdvice.contains("mistake")) {
                chessBoardView.highlightSquare(square, badMoveColor, 5000);
            } else {
                chessBoardView.highlightSquare(square, interestingSquareColor, 5000);
            }
        }
    }

    private List<String> extractChessSquares(String text) {
        List<String> squares = new ArrayList<>();

        // Pattern for individual squares (like a3, b4)
        Pattern squarePattern = Pattern.compile("\\b[a-h][1-8]\\b");
        Matcher squareMatcher = squarePattern.matcher(text);

        while (squareMatcher.find()) {
            squares.add(squareMatcher.group());
        }

        // Pattern for move notation with capture (like cxd5)
        Pattern capturePattern = Pattern.compile("\\b[a-h]x[a-h][1-8]\\b");
        Matcher captureMatcher = capturePattern.matcher(text);

        while (captureMatcher.find()) {
            String captureMove = captureMatcher.group();
            // Extract the destination square (like "d5" from "cxd5")
            String destSquare = captureMove.substring(2);

            // Add destination square if not already included
            if (!squares.contains(destSquare)) {
                squares.add(destSquare);
            }

            // Try to infer the source square (this is a guess for pawns)
            char sourceFile = captureMove.charAt(0);
            char destFile = destSquare.charAt(0);
            char destRank = destSquare.charAt(1);

            // For pawn captures, we can guess the source square
            // This is a simplification, but works for common cases
            String sourceSquare = sourceFile + String.valueOf(Character.getNumericValue(destRank) - 1);
            if (!squares.contains(sourceSquare)) {
                squares.add(sourceSquare);
            }
        }

        // Pattern for regular move notation (like Qc2, Nf3)
        Pattern movePattern = Pattern.compile("\\b[KQRBN]?[a-h][1-8]\\b");
        Matcher moveMatcher = movePattern.matcher(text);

        while (moveMatcher.find()) {
            String move = moveMatcher.group();
            // Extract just the square part (like "c2" from "Qc2")
            String square = move.replaceAll("[KQRBN]", "");

            if (!squares.contains(square)) {
                squares.add(square);
            }
        }

        return squares;
    }

    // Game logic
    private GameViewModel gameViewModel;

    // State tracking
    private boolean conversationActive = false; // tracks if service is running

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all UI elements
        initializeViews();

        // Initialize game ViewModel
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);


        textToSpeechManager = new TextToSpeechManager(this);

        openAIService = OpenAIService.getInstance();
        OpenAIService.getInstance().init(this);

        // Set up click listeners
        setupClickListeners();

        // Check API key
        checkApiKey();

        // Set up the chess board gameplay
        setupChessBoard();

        // Set the player color (white or black)
        String playerColor = "white";  // You can change this or get from settings

        // Initialize a new game
        gameViewModel.newGame(playerColor);

        // Bind to the SimpleRecordService
        bindRecordService();
    }

    /**
     * Bind to the SimpleRecordService
     */
    private void bindRecordService() {
        Intent serviceIntent = new Intent(this, SimpleRecordService.class);
        // Start and bind the service
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Binding to SimpleRecordService...");
    }


    private void setupSpeakButton() {
        FloatingActionButton speakButton = findViewById(R.id.speakButton);
        if (speakButton != null) {
            speakButton.setOnClickListener(v -> {
                Log.d(TAG, "Speak button clicked");

                if (textToSpeechManager != null && textToSpeechManager.isSpeaking()) {
                    // If Coach Tal is speaking, interrupt him
                    textToSpeechManager.stopSpeech();

                    // Set callback to be notified when speech is interrupted
                    textToSpeechManager.setSpeechCallback(new TextToSpeechManager.SpeechCallback() {
                        @Override
                        public void onSpeechCompleted(String text) {
                            // Not used here
                        }

                        @Override
                        public void onSpeechInterrupted() {
                            onSpeechInterrupted(); // Call our method
                        }
                    });
                } else {
                    // Normal recording flow
                    showMicIndicator(true);
                    startRecording();
                }
            });
        }
    }

    // Add these methods to your MainActivity class

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_settings) {
            // Launch settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the chess board and game observers
     */
    private void setupChessBoard() {
        // Set up observers to watch for changes in the game state
        gameViewModel.getCurrentFEN().observe(this, fen -> {
            // Update the board view when the FEN changes
            chessBoardView.updateBoardFromFen(fen);
        });

        gameViewModel.getAnimateMoveEvent().observe(this, coords -> {
            if (coords != null && coords.length == 4) {
                // Animate the piece movement
                chessBoardView.animateMove(coords[0], coords[1], coords[2], coords[3]);
            }
        });

        // In setupChessBoard() method in MainActivity.java
        gameViewModel.getClearSelectionEvent().observe(this, shouldClear -> {
            if (shouldClear != null && shouldClear) {
                Log.d(TAG, "Clearing selection due to invalid move");
                chessBoardView.clearSelectionHighlight();
                chessBoardView.clearHighlightedSquares();
                // Call the reset method instead of setting value directly
                gameViewModel.resetClearSelectionEvent();
            }
        });

        gameViewModel.getLastMoveEvent().observe(this, coords -> {
            if (coords != null && coords.length == 4) {
                // Highlight the last move
                chessBoardView.setLastMove(coords[0], coords[1], coords[2], coords[3]);
            }
        });

        gameViewModel.getKingInCheckEvent().observe(this, coords -> {
            if (coords != null && coords.length == 2) {
                // Highlight the king in check
                chessBoardView.setKingInCheck(true, coords[0], coords[1]);
            } else {
                chessBoardView.setKingInCheck(false, -1, -1);
            }
        });

        // In MainActivity.java - Add to the setupChessBoard method after setting up other listeners
        chessBoardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // ONLY interrupt if the coach is actively speaking
                    if (isServiceBound && recordService != null && recordService.isCurrentlySpeaking()) {
                        // Interrupt only when speaking
                        recordService.interruptSpeech();
                        Toast.makeText(MainActivity.this, "Coach advice interrupted", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                }
                // IMPORTANT: Let normal chess moves work when not interrupting
                return false; // Allow the event to be handled by the chess board
            }
        });

        chessBoardView.setOnSquareTapListener((row, col) -> {
            // If we already have a piece selected...
            if (inChallengeMode) {
                handleChallengeMove(row, col);
            } else {
                if (chessBoardView.getSelectedRow() != -1) {
                    int fromRow = chessBoardView.getSelectedRow();
                    int fromCol = chessBoardView.getSelectedCol();

                    // If tapping the same square, deselect it
                    if (fromRow == row && fromCol == col) {
                        chessBoardView.clearSelectionHighlight();
                        chessBoardView.clearHighlightedSquares();
                        return;
                    }

                    // Check if the tapped square has one of our pieces
                    char tappedPiece = chessBoardView.getPieceAt(row, col);
                    boolean isOurPiece = Character.isUpperCase(tappedPiece) == chessBoardView.isWhiteTurn();

                    if (isOurPiece) {
                        // Tapped another of our pieces, so select this one instead
                        chessBoardView.clearSelectionHighlight();
                        chessBoardView.clearHighlightedSquares();
                        chessBoardView.setSelectedSquare(row, col);

                        // Show legal moves for newly selected piece
                        gameViewModel.getLegalMovesForSquare(algebraicNotation(row, col),
                                moves -> {
                                    for (String move : moves) {
                                        int destRow = 8 - Character.getNumericValue(move.charAt(3));
                                        int destCol = move.charAt(2) - 'a';
                                        chessBoardView.addHighlightedSquare(destRow, destCol);
                                    }
                                });
                        return;
                    }

                    // Otherwise, try to make a move from the selected piece to this square
                    String move = algebraicNotation(fromRow, fromCol) + algebraicNotation(row, col);
                    gameViewModel.makePlayerMove(move);
                    chessBoardView.clearSelectionHighlight();
                    chessBoardView.clearHighlightedSquares();
                } else {
                    // No piece is selected yet, select if it's a piece and our turn
                    char piece = chessBoardView.getPieceAt(row, col);
                    boolean isOurPiece = Character.isUpperCase(piece) == chessBoardView.isWhiteTurn();

                    if (piece != ' ' && isOurPiece) {
                        chessBoardView.setSelectedSquare(row, col);
                        gameViewModel.getLegalMovesForSquare(algebraicNotation(row, col),
                                moves -> {
                                    chessBoardView.clearHighlightedSquares();
                                    for (String move : moves) {
                                        int destRow = 8 - Character.getNumericValue(move.charAt(3));
                                        int destCol = move.charAt(2) - 'a';
                                        chessBoardView.addHighlightedSquare(destRow, destCol);
                                    }
                                });
                    }
                }

            }
        });
    }

    // Add these methods to MainActivity class

    // Show microphone indicator
    private void showMicIndicator(boolean show) {
        View micIndicator = findViewById(R.id.micIndicator);
        if (micIndicator != null) {
            micIndicator.setVisibility(show ? View.VISIBLE : View.GONE);

            if (show) {
                // Start pulse animation if showing
                if (pulseAnimatorSet != null) {
                    pulseAnimatorSet.cancel();
                }

                // Create pulse animation
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(micIndicator, "scaleX", 1.0f, 1.2f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(micIndicator, "scaleY", 1.0f, 1.2f);

                scaleX.setRepeatCount(ValueAnimator.INFINITE);
                scaleX.setRepeatMode(ValueAnimator.REVERSE);
                scaleX.setDuration(500);

                scaleY.setRepeatCount(ValueAnimator.INFINITE);
                scaleY.setRepeatMode(ValueAnimator.REVERSE);
                scaleY.setDuration(500);

                pulseAnimatorSet = new AnimatorSet();
                pulseAnimatorSet.playTogether(scaleX, scaleY);
                pulseAnimatorSet.start();
            } else if (pulseAnimatorSet != null) {
                pulseAnimatorSet.cancel();
            }
        }
    }

    // Start recording method
    private void startRecording() {
        if (isServiceBound && recordService != null) {
            recordService.startRecording();
        } else {
            Toast.makeText(this, "Voice service not ready", Toast.LENGTH_SHORT).show();
            bindRecordService();
        }
    }

    // In MainActivity.java
    private void offerChallenge() {
        // Show a beautiful dialog offering a challenge
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Coach Tal's Challenge")
                .setMessage("Would you like to practice a tactic from this position?")
                .setPositiveButton("Yes, let's practice!", (dialog, id) -> {
                    generateChallenge();
                })
                .setNegativeButton("Not now", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void generateChallenge() {
        // Get current board position
        String currentFEN = chessBoardView.getCurrentFEN();

        // Create a special prompt with VERY specific formatting instructions
        String prompt = "Create a chess puzzle starting from this position: " + currentFEN +
                ". Design a 2-move tactical challenge with clear instructional value.\n\n" +
                "FORMAT YOUR RESPONSE EXACTLY LIKE THIS TEMPLATE:\n" +
                "Description: [Brief description of the puzzle situation]\n" +
                "Correct Move: [The first move in algebraic notation like e2e4 or g1f3]\n" +
                "Expected Response: [How the opponent will respond]\n" +
                "Winning Second Move: [Your winning second move]\n" +
                "Explanation: [Brief explanation of why this works]\n" +
                "Tactical Motif: [Name of the tactical pattern - fork, pin, etc.]";



        // Show loading indicator
        showChallengeLoading(true);

        // Use a background thread to avoid blocking the UI
        new Thread(() -> {
            try {
                // Get challenge from OpenAI (synchronous call)
                String response = openAIService.getChatCompletion(
                        "You are Coach Tal, a chess grandmaster creating engaging, instructive tactical puzzles.",
                        prompt);

                // Process the response on the UI thread
                runOnUiThread(() -> {
                    showChallengeLoading(false);
                    parseChallengeResponse(response);
                });
            } catch (Exception e) {
                // Handle errors on the UI thread
                runOnUiThread(() -> {
                    showChallengeLoading(false);
                    Toast.makeText(MainActivity.this,
                            "Couldn't create a challenge right now. Let's try again later!",
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void showChallenge(ChallengeData challenge) {
        try {
            // Validate the challenge before proceeding
            if (challenge == null || challenge.getDescription().isEmpty() ||
                    challenge.getCorrectMove().isEmpty()) {
                throw new Exception("Invalid challenge data");
            }
                // Inflate challenge view
                View challengeView = getLayoutInflater().inflate(R.layout.challenge_panel, null);

                // Set up UI elements
                TextView descriptionText = challengeView.findViewById(R.id.challengeDescription);
                descriptionText.setText(challenge.getDescription());

                Button hintButton = challengeView.findViewById(R.id.hintButton);
                hintButton.setOnClickListener(v -> showHint(challenge));

                Button solveButton = challengeView.findViewById(R.id.solveButton);
                solveButton.setOnClickListener(v -> showSolution(challenge));

                // Show the challenge panel
                FrameLayout container = findViewById(R.id.challengeContainer);
                container.removeAllViews();
                container.addView(challengeView);
                container.setVisibility(View.VISIBLE);

                // Add beautiful animation
                challengeView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));

                // Highlight relevant squares for this challenge
                highlightChallengeSquares(challenge);
        } catch (Exception e) {
            Log.e(TAG, "Error showing challenge: " + e.getMessage());
            Toast.makeText(this, "Something went wrong with the challenge. Let's try again!", Toast.LENGTH_SHORT).show();
            inChallengeMode = false;
        }
    }

    // Override the chess board's move handling for challenges
    private void handleSquareTapped(int row, int col) {
        if (currentChallenge != null) {
            // Convert to algebraic notation
            String square = algebraicNotation(row, col);

            // If a piece is already selected, try to make a move
            if (selectedSquare != null) {
                String move = selectedSquare + square;

                if (move.equals(currentChallenge.getCorrectMove())) {
                    // Correct move!
                    playCorrectMoveAnimation();
                    speakEncouragement("Excellent move! That's exactly right!");
                    advanceChallenge();
                } else {
                    // Incorrect move
                    playIncorrectMoveAnimation();
                    speakEncouragement("That's not quite it. Want to try again or see a hint?");
                }

                selectedSquare = null;
            } else {
                // Select this square
                selectedSquare = square;
                highlightSelectedSquare(row, col);
            }
        }
        // No else/super call needed since we're not overriding anything
    }

    // Implement the onSpeechInterrupted method
    public void onSpeechInterrupted() {
        isSpeaking = false;

        // Update UI to show we're now in listening mode
        runOnUiThread(() -> {
            TextView statusTextView = findViewById(R.id.statusTextView);
            if (statusTextView != null) {
                statusTextView.setText("Listening to you...");
            }

            // Show microphone indicator
            showMicIndicator(true);
        });

        // Automatically start listening for user input
        startRecording();
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        try {
            gameAnalysisButton = findViewById(R.id.gameAnalysisButton);
            coachButton = findViewById(R.id.coachButton);
            speakButton = findViewById(R.id.speakButton);
            recordButton = findViewById(R.id.recordButton);
            chessBoardView = findViewById(R.id.chessBoardView);
        } catch (Exception e) {
            Log.e(TAG, "Error finding view: " + e.getMessage());
        }

    }

    // Shows the listening animation
    private void showListeningFeedback() {
        // Show coach message card if not visible

        View coachCard = findViewById(R.id.coachMessageCard);
        if (coachCard != null && coachCard.getVisibility() != View.VISIBLE) {
            coachCard.setVisibility(View.VISIBLE);
        }

        // Show microphone indicator - using ID string to avoid R.id issues
        View micIndicator = findViewById(getResources().getIdentifier("micIndicator", "id", getPackageName()));
        if (micIndicator != null) {
            micIndicator.setVisibility(View.VISIBLE);

            // Create beautiful pulse animation for the microphone
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(micIndicator, "scaleX", 1.0f, 1.2f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(micIndicator, "scaleY", 1.0f, 1.2f);

            // Apply duration and repeat settings to each individual animator
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setDuration(500);

            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setDuration(500);

            // Group them together in the animator set
            pulseAnimatorSet = new AnimatorSet();
            pulseAnimatorSet.playTogether(scaleX, scaleY);
            pulseAnimatorSet.start();
        }

        // Update status
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (statusTextView != null) {
            statusTextView.setText("Listening to you...");
        }
    }

    // Add this method to SimpleRecordService
    public void interruptSpeech() {
        if (textToSpeechManager != null) {
            textToSpeechManager.interrupt();
            Log.d(TAG, "Speech interrupted by user tap");
        }
    }

    // Stops the listening animation
    private void stopListeningFeedback() {
        // In stopListeningFeedback() method - Replace the micIndicator line with:
        View micIndicator = findViewById(getResources().getIdentifier("micIndicator", "id", getPackageName()));
        if (micIndicator != null) {
            if (pulseAnimatorSet != null) {
                pulseAnimatorSet.cancel();
            }
            micIndicator.setVisibility(View.GONE);
        }

        // Reset status
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (statusTextView != null) {
            statusTextView.setText("Ready");
        }
    }

    /**
     * Start voice recording using the bound service
     */
    private void startVoiceRecording() {
        // Check for recording permission first
        if (!checkMicrophonePermission()) {
            return;
        }

        // Use the bound service to start recording
        if (isServiceBound && recordService != null) {
            Log.d(TAG, "🎤 Starting recording via bound service...");
            recordService.startRecording();
        } else {
            Log.e(TAG, "❌ Service not bound yet, trying to bind now...");
            Toast.makeText(this, "Connecting to voice service...", Toast.LENGTH_SHORT).show();

            // Try to bind the service
            bindRecordService();

            // Schedule a retry after a short delay
            new android.os.Handler().postDelayed(() -> {
                if (isServiceBound && recordService != null) {
                    recordService.startRecording();
                } else {
                    Toast.makeText(MainActivity.this, "Could not connect to voice service. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }, 1000); // 1 second delay
        }
    }

    // Show coach's response
    private void showCoachResponse(String message) {
        View coachCard = findViewById(R.id.coachMessageCard);
        if (coachCard != null) {
            coachCard.setVisibility(View.VISIBLE);
        }

        // In showCoachResponse() method - Replace the messageText line with:
        TextView messageText = findViewById(getResources().getIdentifier("coachMessageText", "id", getPackageName()));
        if (messageText != null) {
            messageText.setText(message);
        }
    }

    // Show coach advice about the current position
    private void showCoachAdvice() {
        // Get current FEN position from the board
        String currentFen = chessBoardView.getCurrentFEN();

        if (isServiceBound && recordService != null) {
            // Instead of hardcoded advice, ask the service to analyze the position
            // This is a placeholder for now - you might want to implement a method in the service
            // that takes a FEN string and returns analysis
            showCoachResponse("I see you're playing the King's Pawn opening. " +
                    "This is a classic way to begin, establishing control of the center. " +
                    "Consider developing your knights and bishops next to support your central pawn.");
        } else {
            // Fallback to hardcoded advice
            showCoachResponse("I see you're playing the King's Pawn opening. " +
                    "This is a classic way to begin, establishing control of the center. " +
                    "Consider developing your knights and bishops next to support your central pawn.");
        }
    }

    /**
     * Set up click listeners for all buttons
     */
    private void setupClickListeners() {
        // Set up click listeners using your class variables

        setupSpeakButton();

        recordButton.setOnClickListener(v -> {
            Log.d(TAG, "Record button clicked");
            if (isServiceBound && recordService != null) {
                Log.d(TAG, "Starting recording via bound service");
                recordService.startRecording();
                // Show feedback that we're listening
                showListeningFeedback();
            } else {
                Log.e(TAG, "Service not bound, can't start recording");
                Toast.makeText(this, "Voice service not ready. Trying to connect...",
                        Toast.LENGTH_SHORT).show();
                bindRecordService();
            }
        });


        Button resetButton = findViewById(R.id.resetButton); // Add this button to your layout
        if (resetButton != null) {
            resetButton.setOnClickListener(v -> {
                if (isServiceBound && recordService != null) {
                    // Stop any ongoing speech
                    if (textToSpeechManager != null) {
                        textToSpeechManager.interrupt();
                    }

                    // Reset the conversation
                    Toast.makeText(MainActivity.this, "Conversation reset", Toast.LENGTH_SHORT).show();

                    // Start a new conversation (add this method to SimpleRecordService)
                    recordService.resetConversation();
                }
            });
        }

        // In your setupClickListeners method:
        Button askFollowUpButton = findViewById(R.id.askFollowUpButton);
        if (askFollowUpButton != null) {
            askFollowUpButton.setOnClickListener(v -> {
                // Show listening feedback
                showListeningFeedback();
                // Start recording just like the speak button does
                startVoiceRecording();

                // Add context that this is a follow-up
                if (isServiceBound && recordService != null) {
                    recordService.setIsFollowUpQuestion(true);
                }
            });
        }


        gameAnalysisButton.setOnClickListener(v -> {
            // Open game analysis activity
            Intent intent = new Intent(MainActivity.this, GameAnalysisActivity.class);
            startActivity(intent);
        });

        coachButton.setOnClickListener(v -> {
                    offerChallenge();
                });

        // Replace the resetButton section with this:
        coachButton.setOnLongClickListener(v -> {
            offerChallenge();
            if (isServiceBound && recordService != null) {
                // Stop any ongoing speech
                recordService.interruptSpeech();

                // Reset the conversation
                Toast.makeText(MainActivity.this, "Conversation reset", Toast.LENGTH_SHORT).show();

                // Start a new conversation
                recordService.resetConversation();
            }
            return true; // Consume the long click
        });

        speakButton.setOnClickListener(v -> {
            Log.d(TAG, "Speak button clicked");
            // Show listening feedback
            showListeningFeedback();
            // Start recording
            startVoiceRecording();
        });

        // Setup dismiss button for coach card]

// In setupClickListeners() method - Replace the dismissButton line with:
        View dismissButton = findViewById(getResources().getIdentifier("dismissCoachButton", "id", getPackageName()));
        if (dismissButton != null) {
            dismissButton.setOnClickListener(v -> {
                View coachCard = findViewById(R.id.coachMessageCard);
                if (coachCard != null) {
                    coachCard.setVisibility(View.GONE);
                }
            });
        }
    }

    // For parsing AI response into a challenge
    private void parseChallengeResponse(String response) {
        try {
            // Debug the full response first
            Log.d(TAG, "Full challenge response: " + response);

            // Simple parsing strategy - extract key parts
            String description = extractBetween(response, "Description:", "Correct Move:");
            Log.d(TAG, "Extracted description: '" + description + "'");

            String correctMove = extractBetween(response, "Correct Move:", "Expected Response:");
            Log.d(TAG, "Extracted correctMove: '" + correctMove + "'");

            String opponentResponse = extractBetween(response, "Expected Response:", "Winning Second Move:");
            Log.d(TAG, "Extracted opponentResponse: '" + opponentResponse + "'");

            String winningSecondMove = extractBetween(response, "Winning Second Move:", "Explanation:");
            Log.d(TAG, "Extracted winningSecondMove: '" + winningSecondMove + "'");

            String explanation = extractBetween(response, "Explanation:", "Tactical Motif:");
            Log.d(TAG, "Extracted explanation: '" + explanation + "'");

            String tacticalMotif = extractAfter(response, "Tactical Motif:");
            Log.d(TAG, "Extracted tacticalMotif: '" + tacticalMotif + "'");

            // Add validation - don't proceed if critical fields are empty
            if (correctMove.trim().isEmpty() || winningSecondMove.trim().isEmpty()) {
                throw new Exception("Critical challenge data missing - correct move or winning move is empty");
            }

            // Create a challenge object
            currentChallenge = new ChallengeData(
                    description.trim(),
                    correctMove.trim(),
                    opponentResponse.trim(),
                    winningSecondMove.trim(),
                    explanation.trim(),
                    tacticalMotif.trim());

            // Set challenge mode and display
            inChallengeMode = true;
            showChallenge(currentChallenge);

        } catch (Exception e) {
            Log.e(TAG, "Error parsing challenge: " + e.getMessage());
            Toast.makeText(this, "Couldn't create a good challenge. Let's try again!", Toast.LENGTH_SHORT).show();
            inChallengeMode = false;
        }
    }

    // Helper for parsing
    private String extractBetween(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) return "";
        startIndex += start.length();

        int endIndex = text.indexOf(end, startIndex);
        if (endIndex == -1) return text.substring(startIndex).trim();

        return text.substring(startIndex, endIndex).trim();
    }

    // Helper for parsing the last section
    private String extractAfter(String text, String start) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) return "";
        startIndex += start.length();

        return text.substring(startIndex).trim();
    }

    // Show loading indicator
    private void showChallengeLoading(boolean isLoading) {
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (statusTextView != null) {
            statusTextView.setText(isLoading ? "Creating a challenge..." : "Ready");
        }
    }

    // Methods for handling challenge moves
    private void handleChallengeMove(int row, int col) {
        // Convert to algebraic notation
        String square = algebraicNotation(row, col);

        // If a piece is already selected, try to make a move
        if (selectedSquare != null) {
            String move = selectedSquare + square;

            if (currentChallenge != null && move.equals(currentChallenge.getCorrectMove())) {
                // Correct move!
                playCorrectMoveAnimation();
                speakEncouragement("Excellent move! That's exactly right!");
                advanceChallenge();
            } else {
                // Incorrect move
                playIncorrectMoveAnimation();
                speakEncouragement("That's not quite it. Want to try again or see a hint?");
            }

            selectedSquare = null;
        } else {
            // Select this square
            selectedSquare = square;
            highlightSelectedSquare(row, col);
        }
    }

    // Animation and feedback methods
    private void playCorrectMoveAnimation() {
        // Play a success sound
        // Show a green flash or animation
        Toast.makeText(this, "Correct move!", Toast.LENGTH_SHORT).show();
    }

    private void playIncorrectMoveAnimation() {
        // Play an error sound
        // Show a red flash or animation
        Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
    }

    private void speakEncouragement(String message) {
        if (textToSpeechManager != null) {
            textToSpeechManager.speak(message);
        }
    }

    private void highlightSelectedSquare(int row, int col) {
        // Highlight the selected square differently
        chessBoardView.setSelectedSquare(row, col);
    }

    private void highlightChallengeSquares(ChallengeData challenge) {
        // Highlight squares relevant to the challenge
        chessBoardView.clearHighlights();

        // Extract squares from the challenge description
        List<String> squares = extractChessSquares(challenge.getDescription());

        // Add the correct move squares
        String correctMove = challenge.getCorrectMove();
        if (correctMove.length() >= 4) {
            String fromSquare = correctMove.substring(0, 2);
            String toSquare = correctMove.substring(2, 4);

            if (!squares.contains(fromSquare)) squares.add(fromSquare);
            if (!squares.contains(toSquare)) squares.add(toSquare);
        }

        // Highlight each square with a special color
        int challengeColor = Color.parseColor("#9C27B0"); // Purple
        for (String square : squares) {
            chessBoardView.highlightSquare(square, challengeColor, 30000); // 30 seconds
        }
    }

    private void advanceChallenge() {
        // In a more complex implementation, this would move to the next step
        // For now, we'll just exit challenge mode after a correct move
        new Handler().postDelayed(() -> {
            inChallengeMode = false;
            currentChallenge = null;

            // Hide the challenge panel
            FrameLayout container = findViewById(R.id.challengeContainer);
            if (container != null) {
                container.setVisibility(View.GONE);
            }

            // Show success message
            Toast.makeText(this, "Challenge completed! Well done!", Toast.LENGTH_LONG).show();
        }, 2000); // Show success for 2 seconds before hiding
    }

    private void showHint(ChallengeData challenge) {
        // Show a hint for the challenge
        Toast.makeText(this, "Hint: " + challenge.getExplanation(), Toast.LENGTH_LONG).show();

        // Highlight the from square of the correct move
        String correctMove = challenge.getCorrectMove();
        if (correctMove.length() >= 2) {
            String fromSquare = correctMove.substring(0, 2);
            chessBoardView.highlightSquare(fromSquare, Color.YELLOW, 5000); // Yellow hint for 5 seconds
        }
    }

    private void showSolution(ChallengeData challenge) {
        try {
            // Validate the challenge before proceeding
            if (challenge == null || challenge.getCorrectMove().isEmpty()) {
                throw new Exception("Invalid challenge data for solution");
        }
            // Show the solution
            String solution = "The correct move is " + challenge.getCorrectMove() +
                    ". " + challenge.getExplanation();

            // Speak the solution
            speakEncouragement(solution);

            // Show visual indication
            String correctMove = challenge.getCorrectMove();
            if (correctMove.length() >= 4) {
                String fromSquare = correctMove.substring(0, 2);
                String toSquare = correctMove.substring(2, 4);

                chessBoardView.highlightSquare(fromSquare, Color.GREEN, 5000);
                chessBoardView.highlightSquare(toSquare, Color.GREEN, 5000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing solution: " + e.getMessage());
            speakEncouragement("I'm having trouble with this puzzle right now.");
        }
    }

    /**
     * Convert board coordinates to algebraic notation
     */
    private String algebraicNotation(int row, int col) {
        char file = (char)('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Check if microphone permission is granted, request if not
     * @return true if permission is already granted
     */
    private boolean checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
            return false;
        }
        return true;
    }

    /**
     * Check if API key is set
     */
    private void checkApiKey() {
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(this, "Please set your OpenAI API key in settings", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start recording
                startVoiceRecording();
            } else {
                // Permission denied
                Toast.makeText(this, "Microphone permission is required for voice interaction",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Unbind from the service
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }

        super.onDestroy();
    }
}