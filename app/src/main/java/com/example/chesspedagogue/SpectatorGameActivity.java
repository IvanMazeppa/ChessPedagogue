package com.example.chesspedagogue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🎭 SIMPLIFIED SPECTATOR MODE - AI Masters Talking to Each Other!
 * Focused on AI dialogue instead of commentary
 */
public class SpectatorGameActivity extends AppCompatActivity {
    private static final String TAG = "SpectatorGameActivity";

    // UI Elements - Simplified!
    private ChessBoardView chessBoardView;
    private EvaluationBarView evaluationBarView;
    private TextView moveHistoryTextView;
    private TextView dialogueTextView;
    private TextView whitePlayerNameTextView;
    private TextView blackPlayerNameTextView;
    private CardView dialogueCard;
    private ProgressBar thinkingProgressBar;
    private Button pauseResumeButton;
    private Button speedControlButton;
    private TextView currentSpeakerTextView;
    private Button userCommentButton;

    // Game state
    private SpectatorGameViewModel viewModel;
    private String whitePlayer;
    private String blackPlayer;
    private boolean isPaused = false;
    private int gameSpeed = 3000;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    // Animation tracking for delayed board updates
    private boolean isAnimationInProgress = false;
    private String pendingFenUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "🎭 Starting simplified spectator mode...");
            setContentView(R.layout.activity_spectator_game);

            // Get players from intent with fallbacks
            getPlayersFromIntent();

            // Initialize everything with proper error handling
            if (setupActionBar() &&
                    initializeViews() &&
                    setupObservers() &&
                    setupControls()) {

                Log.d(TAG, "✅ All components initialized successfully!");
                startSpectatorGame();

            } else {
                Log.e(TAG, "❌ Failed to initialize components");
                showErrorAndFinish("Failed to initialize spectator mode");
            }

        } catch (Exception e) {
            Log.e(TAG, "💥 Exception in onCreate", e);
            showErrorAndFinish("Error starting spectator mode: " + e.getMessage());
        }
    }

    private void getPlayersFromIntent() {
        Intent intent = getIntent();
        whitePlayer = intent.getStringExtra("WHITE_PLAYER");
        blackPlayer = intent.getStringExtra("BLACK_PLAYER");

        // Fallbacks if something went wrong
        if (whitePlayer == null || whitePlayer.trim().isEmpty()) {
            whitePlayer = "tal";
            Log.w(TAG, "Using fallback white player: " + whitePlayer);
        }
        if (blackPlayer == null || blackPlayer.trim().isEmpty()) {
            blackPlayer = "fischer";
            Log.w(TAG, "Using fallback black player: " + blackPlayer);
        }

        Log.d(TAG, "🎭 Players: " + whitePlayer + " vs " + blackPlayer);
    }

    private boolean setupActionBar() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("🎭 AI Masters: " +
                        FineTunedModelManager.getInstance(this).getMasterDisplayName(whitePlayer) +
                        " vs " +
                        FineTunedModelManager.getInstance(this).getMasterDisplayName(blackPlayer));

                Log.d(TAG, "✅ Action bar set up successfully");
                return true;
            } else {
                Log.w(TAG, "⚠️ No action bar available, continuing without");
                return true; // Continue anyway
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up action bar", e);
            return true; // Don't fail just because of action bar
        }
    }

    private boolean initializeViews() {
        try {
            Log.d(TAG, "🔍 Finding views...");

            // Find all views with null checks
            chessBoardView = findViewById(R.id.chessBoardView);
            evaluationBarView = findViewById(R.id.evaluationBarView);
            whitePlayerNameTextView = findViewById(R.id.whitePlayerName);
            blackPlayerNameTextView = findViewById(R.id.blackPlayerName);
            moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
            dialogueTextView = findViewById(R.id.dialogueTextView);
            dialogueCard = findViewById(R.id.dialogueCard);
            thinkingProgressBar = findViewById(R.id.thinkingProgressBar);
            pauseResumeButton = findViewById(R.id.pauseResumeButton);
            speedControlButton = findViewById(R.id.speedControlButton);
            currentSpeakerTextView = findViewById(R.id.currentSpeakerTextView);

            // Check critical views
            if (chessBoardView == null) {
                Log.e(TAG, "❌ ChessBoardView not found!");
                return false;
            }
            if (whitePlayerNameTextView == null || blackPlayerNameTextView == null) {
                Log.e(TAG, "❌ Player name TextViews not found!");
                return false;
            }

            // Set player names
            setupPlayerInfo();

            Log.d(TAG, "✅ Views initialized successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing views", e);
            return false;
        }
    }



    private boolean setupObservers() {
        try {
            // Initialize ViewModel
            viewModel = new ViewModelProvider(this).get(SpectatorGameViewModel.class);

            if (viewModel == null) {
                Log.e(TAG, "❌ ViewModel is null, cannot set up observers");
                return false;
            }

            // Observe game state changes
            viewModel.getCurrentFEN().observe(this, fen -> {
                if (fen != null && chessBoardView != null) {
                    // Store the FEN for delayed update if animation is in progress
                    pendingFenUpdate = fen;
                    
                    // If no animation is in progress, update immediately
                    if (!isAnimationInProgress) {
                        chessBoardView.updateBoardFromFen(fen);
                        pendingFenUpdate = null;
                        Log.d(TAG, "📋 Board updated immediately");
                    } else {
                        Log.d(TAG, "📋 Board update deferred - animation in progress");
                    }
                }
            });

            viewModel.getMoveHistory().observe(this, moves -> {
                if (moves != null && moveHistoryTextView != null) {
                    updateMoveHistoryDisplay(moves);
                }
            });

            viewModel.getCurrentEvaluation().observe(this, evaluation -> {
                if (evaluation != null && evaluationBarView != null) {
                    evaluationBarView.setEvaluation(evaluation);
                }
            });

            // NEW: Observe AI dialogue instead of commentary
            viewModel.getAIDialogue().observe(this, dialogueData -> {
                if (dialogueData != null && !dialogueData.trim().isEmpty()) {
                    displayAIDialogue(dialogueData);
                }
            });

            viewModel.getCurrentPlayer().observe(this, currentPlayer -> {
                updateCurrentPlayerIndicator(currentPlayer);
            });

            viewModel.getGameStatus().observe(this, status -> {
                if (status != null) {
                    updateGameStatus(status);
                }
            });

            viewModel.isThinking().observe(this, isThinking -> {
                if (thinkingProgressBar != null) {
                    thinkingProgressBar.setVisibility(isThinking ? View.VISIBLE : View.GONE);
                }
            });

            // NEW: Observe move animations so pieces actually move!
            viewModel.getLastMove().observe(this, moveData -> {
                if (moveData != null && moveData.length >= 4 && chessBoardView != null) {
                    Log.d(TAG, "🎯 Animating move: " + moveData[0] + "," + moveData[1] + " -> " + moveData[2] + "," + moveData[3]);
                    
                    // CRITICAL FIX: Clear any previous animation state first
                    isAnimationInProgress = false;
                    if (pendingFenUpdate != null && chessBoardView != null) {
                        chessBoardView.updateBoardFromFen(pendingFenUpdate);
                        pendingFenUpdate = null;
                    }
                    
                    // Set animation in progress
                    isAnimationInProgress = true;
                    
                    // Start the animation
                    chessBoardView.animateMove(moveData[0], moveData[1], moveData[2], moveData[3]);
                    chessBoardView.setLastMove(moveData[0], moveData[1], moveData[2], moveData[3]);
                    
                    // Schedule board update after animation completes (shorter duration)
                    mainHandler.postDelayed(() -> {
                        isAnimationInProgress = false;
                        
                        // Apply any pending FEN update
                        if (pendingFenUpdate != null && chessBoardView != null) {
                            chessBoardView.updateBoardFromFen(pendingFenUpdate);
                            pendingFenUpdate = null;
                            Log.d(TAG, "📋 Board updated after animation");
                        }
                    }, 300); // Reduced from 500ms to 300ms for faster updates
                }
            });

            // Add this to your setupObservers() method in SpectatorGameActivity.java
            viewModel.isConversationActive().observe(this, isActive -> {
                if (isActive) {
                    // Show conversation indicator (optional)
                    Log.d(TAG, "💬 Conversation is active between masters!");
                }
            });

            viewModel.getConversationSpeaker().observe(this, speaker -> {
                if (speaker != null) {
                    Log.d(TAG, "🎭 Current speaker: " + speaker);
                    // You could update UI to show who's speaking
                }
            });

            Log.d(TAG, "✅ Observers set up successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up observers", e);
            return false;
        }
    }

    private boolean setupControls() {
        try {
            // Pause/Resume button
            if (pauseResumeButton != null) {
                pauseResumeButton.setOnClickListener(v -> {
                    if (isPaused) {
                        resumeGame();
                    } else {
                        pauseGame();
                    }
                });
            }

            // Speed control button
            if (speedControlButton != null) {
                speedControlButton.setOnClickListener(v -> cycleGameSpeed());
                updateSpeedButtonText();
            }

            // NEW: User comment button setup
            userCommentButton = findViewById(R.id.userCommentButton);
            if (userCommentButton != null) {
                userCommentButton.setOnClickListener(v -> showUserCommentDialog());
                Log.d(TAG, "✅ User comment button initialized");
            } else {
                Log.w(TAG, "⚠️ User comment button not found in layout");
            }

            Log.d(TAG, "✅ Controls set up successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up controls", e);
            return false;
        }
    }

    private void setupPlayerInfo() {
        if (whitePlayerNameTextView != null && blackPlayerNameTextView != null) {
            String whiteName = FineTunedModelManager.getInstance(this).getMasterDisplayName(whitePlayer);
            String blackName = FineTunedModelManager.getInstance(this).getMasterDisplayName(blackPlayer);

            whitePlayerNameTextView.setText("⚪ " + whiteName);
            blackPlayerNameTextView.setText("⚫ " + blackName);

            // Set evaluation bar player color
            if (evaluationBarView != null) {
                evaluationBarView.setPlayerColor(true); // Always show from white's perspective in spectator mode
            }

            Log.d(TAG, "✅ Player info set: " + whiteName + " vs " + blackName);
        }
    }

    private void startSpectatorGame() {
        Log.d(TAG, "🚀 Starting spectator game!");

        try {
            if (viewModel != null) {
                // Show initial dialogue
                String openingDialogue = String.format(
                        "Welcome to this epic battle! %s and %s are about to begin their game.",
                        FineTunedModelManager.getInstance(this).getMasterDisplayName(whitePlayer),
                        FineTunedModelManager.getInstance(this).getMasterDisplayName(blackPlayer)
                );

                displayAIDialogue(openingDialogue);

                // Initialize the game AND wait for it to be ready
                viewModel.startSpectatorGame(whitePlayer, blackPlayer);

                // FIXED: Wait a bit longer and check game status before requesting first move
                mainHandler.postDelayed(() -> {
                    if (!isPaused && viewModel != null) {
                        Log.d(TAG, "🎯 Requesting first move after proper initialization...");

                        // CRITICAL FIX: Ensure the game is actually ready
                        if ("in_progress".equals(viewModel.getGameStatus().getValue())) {
                            viewModel.requestNextMove();
                        } else {
                            Log.d(TAG, "🔄 Game not ready yet, trying again in 1 second...");
                            // Try again if not ready
                            mainHandler.postDelayed(() -> {
                                if (!isPaused && viewModel != null && "in_progress".equals(viewModel.getGameStatus().getValue())) {
                                    Log.d(TAG, "🎯 Second attempt - requesting first move...");
                                    viewModel.requestNextMove();
                                }
                            }, 1000);
                        }
                    }
                }, 4000); // Longer delay for proper initialization

                Log.d(TAG, "✅ Spectator game started successfully!");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting spectator game", e);
            showErrorAndFinish("Failed to start spectator game: " + e.getMessage());
        }
    }




    /**
     * 🎤 Show user comment dialog with voice recording option
     */
    private void showUserCommentDialog() {
        try {
            Log.d(TAG, "🎤 Showing user comment dialog with voice option");

            // Create dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("💬 Chat with the Chess Masters");
            builder.setMessage("Choose how you'd like to comment on the game:");

            // Create custom view with both options
            View dialogView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            
            // Option 1: Voice recording (primary)
            builder.setPositiveButton("🎤 Speak", (dialog, which) -> {
                startVoiceComment();
            });

            // Option 2: Type (fallback)
            builder.setNeutralButton("⌨️ Type", (dialog, which) -> {
                showTextCommentDialog();
            });

            builder.setNegativeButton("❌ Cancel", (dialog, which) -> dialog.cancel());

            // Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "❌ Error showing comment dialog", e);
            Toast.makeText(this, "Error opening comment dialog", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎤 Start voice recording for comment
     */
    private void startVoiceComment() {
        Log.d(TAG, "🎤 Starting voice comment recording");
        
        // Check microphone permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "⚠️ No microphone permission");
            
            // Request permission
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
            } else {
                Toast.makeText(this, "Microphone permission required for voice comments", Toast.LENGTH_LONG).show();
                showTextCommentDialog(); // Fallback to text
            }
            return;
        }

        // Show recording dialog
        AlertDialog.Builder recordingBuilder = new AlertDialog.Builder(this);
        recordingBuilder.setTitle("🎤 Recording Your Comment");
        recordingBuilder.setMessage("Speak your comment about the game...");
        recordingBuilder.setCancelable(false);
        
        // Add progress indicator
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        recordingBuilder.setView(progressBar);
        
        recordingBuilder.setNegativeButton("⏹️ Stop", null); // Will be set later
        
        AlertDialog recordingDialog = recordingBuilder.create();
        recordingDialog.show();
        
        // Start STT using OpenAI Whisper via SimpleRecordService pattern
        executorService.execute(() -> {
            try {
                // Initialize Groq/OpenAI STT service
                String transcribedText = performSpeechToText(recordingDialog);
                
                mainHandler.post(() -> {
                    recordingDialog.dismiss();
                    if (transcribedText != null && !transcribedText.trim().isEmpty()) {
                        // Show what was transcribed and confirm
                        confirmTranscription(transcribedText);
                    } else {
                        Toast.makeText(this, "Couldn't understand. Please try again or type instead.", Toast.LENGTH_SHORT).show();
                        showTextCommentDialog();
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in voice recording", e);
                mainHandler.post(() -> {
                    recordingDialog.dismiss();
                    Toast.makeText(this, "Voice recording failed. Please type your comment instead.", Toast.LENGTH_SHORT).show();
                    showTextCommentDialog();
                });
            }
        });
    }

    /**
     * 🎤 Perform speech-to-text conversion using Groq
     */
    private String performSpeechToText(AlertDialog recordingDialog) {
        try {
            Log.d(TAG, "🎤 Starting speech recognition with Groq");
            
            // Use GroqSpeechRecognizer for fast STT
            GroqSpeechRecognizer speechRecognizer = new GroqSpeechRecognizer(this);
            
            // Create a synchronization object to wait for result
            final Object lock = new Object();
            final String[] result = new String[1];
            final boolean[] completed = new boolean[1];
            
            // Update dialog button to allow stopping
            mainHandler.post(() -> {
                recordingDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                    Log.d(TAG, "⏹️ User stopped recording");
                    speechRecognizer.stopListening();
                    synchronized (lock) {
                        completed[0] = true;
                        lock.notify();
                    }
                });
            });
            
            // Start listening with callback
            speechRecognizer.startListening(new GroqSpeechRecognizer.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String text) {
                    Log.d(TAG, "✅ Speech recognized: " + text);
                    synchronized (lock) {
                        result[0] = text;
                        completed[0] = true;
                        lock.notify();
                    }
                }
                
                @Override
                public void onSpeechError(String error) {
                    Log.e(TAG, "❌ Speech recognition error: " + error);
                    synchronized (lock) {
                        result[0] = null;
                        completed[0] = true;
                        lock.notify();
                    }
                }
            });
            
            // Wait for result with timeout
            synchronized (lock) {
                if (!completed[0]) {
                    lock.wait(15000); // 15 second timeout
                }
            }
            
            // Stop listening if still active
            speechRecognizer.stopListening();
            
            return result[0];
            
        } catch (Exception e) {
            Log.e(TAG, "❌ STT error", e);
            return null;
        }
    }

    /**
     * ✅ Confirm transcribed text before sending
     */
    private void confirmTranscription(String transcribedText) {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("✅ Confirm Your Comment");
        confirmBuilder.setMessage("You said: \"" + transcribedText + "\"\n\nSend this to the chess masters?");
        
        confirmBuilder.setPositiveButton("✅ Send", (dialog, which) -> {
            submitUserComment(transcribedText);
        });
        
        confirmBuilder.setNeutralButton("🔄 Try Again", (dialog, which) -> {
            startVoiceComment();
        });
        
        confirmBuilder.setNegativeButton("⌨️ Type Instead", (dialog, which) -> {
            showTextCommentDialog();
        });
        
        confirmBuilder.show();
    }

    /**
     * ⌨️ Show text input dialog (fallback option)
     */
    private void showTextCommentDialog() {
        try {
            Log.d(TAG, "⌨️ Showing text comment dialog");

            // Create dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("💬 Type Your Comment");
            builder.setMessage("What would you like to say about the game?");

            // Create input field
            final EditText input = new EditText(this);
            input.setHint("Type your comment here...");
            input.setMinLines(2);
            input.setMaxLines(4);
            input.setPadding(32, 16, 32, 16);
            builder.setView(input);

            // Add buttons
            builder.setPositiveButton("💬 Send", (dialog, which) -> {
                String comment = input.getText().toString().trim();
                if (!comment.isEmpty()) {
                    submitUserComment(comment);
                } else {
                    Toast.makeText(this, "Please enter a comment first!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("❌ Cancel", (dialog, which) -> dialog.cancel());

            // Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            // Focus on input and show keyboard
            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error showing text comment dialog", e);
            Toast.makeText(this, "Error opening comment dialog", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎤 Submit user comment to AI masters
     */
    private void submitUserComment(String comment) {
        try {
            Log.d(TAG, "🎤 User submitted comment: " + comment);

            // Show feedback to user
            Toast.makeText(this, "💬 Sent to chess masters: \"" + comment + "\"", Toast.LENGTH_LONG).show();

            // Determine current game phase for context
            String gamePhase = determineGamePhase();

            // Send comment to dialogue manager for AI response
            if (viewModel != null) {
                AIDialogueManager dialogueManager = new AIDialogueManager(this);

                dialogueManager.handleUserComment(
                        comment,
                        whitePlayer,
                        blackPlayer,
                        gamePhase,
                        new AIDialogueManager.DialogueCallback() {
                            @Override
                            public void onDialogueGenerated(String speaker, String dialogue) {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "🎭 Master " + speaker + " responded to user comment");
                                    displayAIDialogue(dialogue);

                                    // Show special indicator that this is a response to user
                                    String userResponseIndicator = "👤➡️🎭 " +
                                            FineTunedModelManager.getInstance(SpectatorGameActivity.this).getMasterDisplayName(speaker) +
                                            " responds: \"" + dialogue + "\"";
                                    displayAIDialogue(userResponseIndicator);
                                });
                            }

                            @Override
                            public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "🎉 User comment triggered conversation!");
                                    String conversationIndicator = "💭 " +
                                            FineTunedModelManager.getInstance(SpectatorGameActivity.this).getMasterDisplayName(respondingSpeaker) +
                                            " is thinking about your comment...";
                                    displayAIDialogue(conversationIndicator);
                                });
                            }

                            @Override
                            public void onConversationComplete(String finalSpeaker, String finalStatement) {
                                Log.d(TAG, "✅ User-triggered conversation completed");
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Log.e(TAG, "❌ Error processing user comment: " + error);
                                    Toast.makeText(SpectatorGameActivity.this,
                                            "Masters are too focused on the game right now",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error submitting user comment", e);
            Toast.makeText(this, "Error sending comment to masters", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎯 Determine current game phase for context
     */
    private String determineGamePhase() {
        try {
            if (viewModel != null && viewModel.getMoveHistory().getValue() != null) {
                int moveCount = viewModel.getMoveHistory().getValue().size();

                if (moveCount < 12) {
                    return "opening";
                } else if (moveCount < 40) {
                    return "middlegame";
                } else if (moveCount < 60) {
                    return "endgame";
                } else {
                    return "endgame_critical";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error determining game phase", e);
        }

        return "middlegame"; // Default
    }

    /**
     * 🎤 Enhanced dialogue display for user interactions
     */
    private void displayUserInteractionDialogue(String dialogue, boolean isUserResponse) {
        if (dialogueTextView != null && dialogueCard != null) {
            String prefix = isUserResponse ? "👤➡️🎭 " : "💬 ";
            dialogueTextView.setText(prefix + dialogue);
            dialogueCard.setVisibility(View.VISIBLE);

            // Special styling for user interaction responses
            if (isUserResponse && dialogueCard != null) {
                dialogueCard.setCardBackgroundColor(getColor(R.color.user_interaction_bg)); // Add this color
            } else {
                dialogueCard.setCardBackgroundColor(getColor(R.color.default_dialogue_bg)); // Add this color
            }

            Log.d(TAG, "💬 User interaction dialogue displayed");
        }
    }

    private void updateMoveHistoryDisplay(List<String> moves) {
        if (moveHistoryTextView == null) return;

        if (moves.isEmpty()) {
            moveHistoryTextView.setText("🎭 Epic battle begins...");
            return;
        }

        StringBuilder historyBuilder = new StringBuilder();
        historyBuilder.append("🎭 Live Game:\n\n");

        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                // White move
                int moveNumber = (i / 2) + 1;
                historyBuilder.append(moveNumber).append(". ").append(moves.get(i));
                if (i + 1 < moves.size()) {
                    historyBuilder.append(" ");
                }
            } else {
                // Black move
                historyBuilder.append(moves.get(i));
                if (i + 1 < moves.size()) {
                    historyBuilder.append("\n");
                }
            }
        }

        moveHistoryTextView.setText(historyBuilder.toString());
    }

    /**
     * NEW: Display AI dialogue (much simpler than commentary)
     */
    private void displayAIDialogue(String dialogue) {
        if (dialogueTextView != null && dialogueCard != null) {
            dialogueTextView.setText("💬 " + dialogue);
            dialogueCard.setVisibility(View.VISIBLE);

            Log.d(TAG, "💬 AI Dialogue displayed: " + dialogue.substring(0, Math.min(50, dialogue.length())));
        }
    }

    private void updateCurrentPlayerIndicator(String currentPlayer) {
        if (whitePlayerNameTextView != null && blackPlayerNameTextView != null) {
            if ("white".equals(currentPlayer)) {
                whitePlayerNameTextView.setAlpha(1.0f);
                blackPlayerNameTextView.setAlpha(0.6f);
                whitePlayerNameTextView.setTextSize(18f);
                blackPlayerNameTextView.setTextSize(16f);
            } else {
                whitePlayerNameTextView.setAlpha(0.6f);
                blackPlayerNameTextView.setAlpha(1.0f);
                whitePlayerNameTextView.setTextSize(16f);
                blackPlayerNameTextView.setTextSize(18f);
            }
        }
    }

    private void updateGameStatus(String status) {
        Log.d(TAG, "📊 Game status: " + status);

        if (status.contains("checkmate") || status.contains("stalemate") || status.contains("draw")) {
            if (pauseResumeButton != null) {
                pauseResumeButton.setEnabled(false);
                pauseResumeButton.setText("🏁 Game Over");
            }

            String finalDialogue = "🏁 What an incredible game! " + status + " Both masters played brilliantly!";
            displayAIDialogue(finalDialogue);
        }
    }

    private void pauseGame() {
        isPaused = true;
        if (pauseResumeButton != null) {
            pauseResumeButton.setText("▶️ Resume");
        }
        if (viewModel != null) {
            viewModel.pauseGame();
        }

        displayAIDialogue("⏸️ Game paused. The masters are taking a break!");
        Log.d(TAG, "⏸️ Game paused by user");
    }

    private void resumeGame() {
        isPaused = false;
        if (pauseResumeButton != null) {
            pauseResumeButton.setText("⏸️ Pause");
        }
        if (viewModel != null) {
            viewModel.resumeGame();
        }

        displayAIDialogue("▶️ Game resumed! The battle continues...");
        Log.d(TAG, "▶️ Game resumed by user");
    }

    private void cycleGameSpeed() {
        switch (gameSpeed) {
            case 5000: gameSpeed = 3000; break; // Slow to Normal
            case 3000: gameSpeed = 1500; break; // Normal to Fast
            case 1500: gameSpeed = 5000; break; // Fast to Slow
        }

        if (viewModel != null) {
            viewModel.setGameSpeed(gameSpeed);
        }
        updateSpeedButtonText();

        String speedName = gameSpeed == 5000 ? "Slow" : gameSpeed == 1500 ? "Fast" : "Normal";
        displayAIDialogue("⚡ Game speed changed to " + speedName + " mode!");
    }

    private void updateSpeedButtonText() {
        if (speedControlButton != null) {
            String speedText;
            String emoji;
            switch (gameSpeed) {
                case 5000: speedText = "Slow"; emoji = "🐌"; break;
                case 1500: speedText = "Fast"; emoji = "⚡"; break;
                default: speedText = "Normal"; emoji = "⏱️"; break;
            }
            speedControlButton.setText(emoji + " " + speedText);
        }
    }

    private void showErrorAndFinish(String message) {
        Log.e(TAG, "💥 Showing error and finishing: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == 1001) { // Voice comment permission request
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "✅ Microphone permission granted");
                // Permission granted, try voice comment again
                startVoiceComment();
            } else {
                Log.w(TAG, "⚠️ Microphone permission denied");
                Toast.makeText(this, "Microphone permission denied. Using text input instead.", Toast.LENGTH_LONG).show();
                showTextCommentDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "⏸️ SpectatorGameActivity pausing - stopping all processes");
        performImmediateCleanup();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "🛑 SpectatorGameActivity stopping - aggressive cleanup");
        performImmediateCleanup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "💀 SpectatorGameActivity destroying - final cleanup");
        performImmediateCleanup();
        Log.d(TAG, "🧹 SpectatorGameActivity destroyed");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "🔙 Back button pressed - cleaning up before exit");
        performImmediateCleanup();
        super.onBackPressed();
    }

    /**
     * CRITICAL: Immediate cleanup to prevent ANR
     */
    private void performImmediateCleanup() {
        try {
            Log.d(TAG, "🚨 EMERGENCY CLEANUP STARTING");

            // 1. Stop ViewModel immediately
            if (viewModel != null) {
                Log.d(TAG, "🛑 Stopping ViewModel");
                viewModel.forceStop();
                viewModel.cleanup();
            }

            // 2. Stop all executors immediately
            if (executorService != null && !executorService.isShutdown()) {
                Log.d(TAG, "🛑 Shutting down executors");
                executorService.shutdownNow();
            }

            // 3. Clear all handlers
            if (mainHandler != null) {
                Log.d(TAG, "🛑 Clearing all handler callbacks");
                mainHandler.removeCallbacksAndMessages(null);
            }

            // 4. Stop TTS services
            OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(this);
            if (ttsService != null) {
                Log.d(TAG, "🛑 Stopping TTS service");
                ttsService.stopSpeaking();
            }

            // 5. Stop all AI dialogue (via ViewModel since it has the instance)
            Log.d(TAG, "🛑 Stopping dialogue via ViewModel");
            // AIDialogueManager will be stopped via ViewModel.forceStop()

            Log.d(TAG, "✅ EMERGENCY CLEANUP COMPLETED");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error during emergency cleanup", e);
        }
    }
}