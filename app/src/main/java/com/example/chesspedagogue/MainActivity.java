package com.example.chesspedagogue;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.chesspedagogue.repository.GameRepository;
import com.example.chesspedagogue.viewmodel.GameViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;

    // Add this near your other class members
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    // Handler for UI updates
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    // State tracking
    private final boolean conversationActive = false; // tracks if service is running

    private AnimatorSet pulseAnimatorSet;
    private OpenAIService openAIService;
    private String selectedSquare = null;
    private boolean isSpeaking = false;

    // UI elements - FIXED: All changed to FloatingActionButton to match XML
    private FloatingActionButton conversationButton;
    private ChallengeData currentChallenge;
    private boolean inChallengeMode = false;

    private FloatingActionButton speakButton;
    private FloatingActionButton gameAnalysisButton;  // FIXED: Changed from Button to FloatingActionButton
    private FloatingActionButton coachButton;         // FIXED: Changed from Button to FloatingActionButton
    private ChessBoardView chessBoardView;

    // NEW: Evaluation Bar UI Elements! 🎯
    private EvaluationBarView evaluationBarView;
    private ProgressBar evaluationProgressBar;

    // ===== CRITICAL: Move History UI Elements =====
    private TextView moveHistoryTextView;

    // NEW: Game configuration from splash screen
    private String configuredPlayerColor = "white";
    private int configuredSkillLevel = 10;
    private int configuredEngineElo = 1750;

    // SimpleRecordService connection
    private SimpleRecordService recordService;
    private boolean isServiceBound = false;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SimpleRecordService.LocalBinder binder = (SimpleRecordService.LocalBinder) service;
            recordService = binder.getService();
            isServiceBound = true;
            Log.d(TAG, "🎉 Service connected successfully!");

            updateVoiceForCurrentMaster();

            if (recordService != null) {
                recordService.refreshVoiceSettings();
            }

            // Set up the callback to handle responses
            recordService.setCallback(new SimpleRecordService.ServiceCallback() {
                @Override
                public void onRecordingStarted() {
                    Log.d(TAG, "Recording started callback received!");
                    // Update UI on main thread
                    mainHandler.post(() -> updateUIState(ProcessingState.LISTENING));
                }

                @Override
                public void onRecordingStopped() {
                    Log.d(TAG, "Recording stopped callback received!");
                    // Update UI to show we're transcribing
                    mainHandler.post(() -> updateUIState(ProcessingState.TRANSCRIBING));
                }

                @Override
                public void onProcessingStateChanged(boolean isProcessing) {
                    mainHandler.post(() -> {
                        // Update status based on processing state
                        updateUIState(isProcessing ? ProcessingState.THINKING : ProcessingState.IDLE);
                    });
                }

                @Override
                public void onResponseReceived(String response) {
                    // Update UI to show we're speaking the response
                    mainHandler.post(() -> {
                        updateUIState(ProcessingState.SPEAKING, response);
                        processAdviceForHighlights(response);
                    });
                }

                @Override
                public void onResponseCompleted(String response) {
                    // Coach has finished speaking, return to IDLE state
                    mainHandler.post(() -> updateUIState(ProcessingState.IDLE));
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

    // Game logic
    private GameViewModel gameViewModel;

    // Add this method to update coach message text
    private void updateCoachMessageText(String message) {
        TextView messageText = findViewById(R.id.coachMessageText);
        if (messageText != null) {
            messageText.setText(message);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preWarmSpeechServices();

        // NEW: Get configuration from splash screen
        loadGameConfiguration();

        // In onCreate or similar initialization method
        ApiKeyConfig.initializeOpenAIClient(this);

        // Initialize all UI elements
        initializeViews();

        // ENHANCED: Add null check for chessBoardView before proceeding
        if (chessBoardView == null) {
            Log.e(TAG, "❌ CRITICAL: ChessBoardView is null after initializeViews!");
            Toast.makeText(this, "Error initializing chess board. Please restart the app.", Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize game ViewModel with configuration
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        openAIService = com.example.chesspedagogue.OpenAIService.getInstance();
        com.example.chesspedagogue.OpenAIService.getInstance().init(this);

        Button debugButton = new Button(this);
        // Set up click listeners - this is what was missing!
        setupAllButtonClickListeners();

        // Check API key
        checkApiKey();

        // Set up the chess board gameplay
        setupChessBoard();

        // NEW: Set up the evaluation bar! 🎯
        setupEvaluationBar();

        // ===== CRITICAL: Set up move history observation! =====
        setupMoveHistoryObserver();

        // NEW: Initialize game with proper configuration
        initializeGameWithConfiguration();

        // Bind to the SimpleRecordService
        bindRecordService();
    }

    /**
     * Set up all button click listeners - this connects your beautiful UI to your existing methods!
     */
    private void setupAllButtonClickListeners() {
        Log.d(TAG, "🎯 Setting up all button click listeners");

        // Game Analysis Button - connects to your existing openAnalysisScreen() method
        if (gameAnalysisButton != null) {
            gameAnalysisButton.setOnClickListener(v -> {
                Log.d(TAG, "🔍 Game Analysis button clicked");
                openAnalysisScreen();
            });
            Log.d(TAG, "✅ Game Analysis button listener set");
        } else {
            Log.w(TAG, "⚠️ gameAnalysisButton is null!");
        }

        // Coach Button - connects to your existing showCoachAdvice() method
        if (coachButton != null) {
            coachButton.setOnClickListener(v -> {
                Log.d(TAG, "🎓 Coach button clicked");
                showCoachAdvice();
                showCoachConversation(); // Also show the coach panel
            });
            Log.d(TAG, "✅ Coach button listener set");
        } else {
            Log.w(TAG, "⚠️ coachButton is null!");
        }

        // Speak Button (FAB) - using your existing voice recording logic
        if (speakButton != null) {
            speakButton.setOnClickListener(v -> {
                Log.d(TAG, "🎤 Speak button clicked");

                OpenAITTSService tts = OpenAITTSService.getInstance(this);
                if (tts != null && tts.isSpeaking()) {
                    tts.stopSpeech();
                    tts.setSpeechCallback(new OpenAITTSService.SpeechCallback() {
                        @Override
                        public void onSpeechCompleted(String text) {
                            // Not used here
                        }

                        @Override
                        public void onSpeechInterrupted() {
                            onSpeechInterrupted(); // Call your existing method
                        }
                    });
                } else {
                    showMicIndicator(true);
                    startRecording(); // Use your existing method
                }
            });
            Log.d(TAG, "✅ Speak button listener set");
        } else {
            Log.w(TAG, "⚠️ speakButton is null!");
        }

        // Additional buttons in the coach panel
        Button askFollowUpButton = findViewById(R.id.askFollowUpButton);
        if (askFollowUpButton != null) {
            askFollowUpButton.setOnClickListener(v -> {
                Log.d(TAG, "🗣️ Ask follow-up button clicked");
                startVoiceRecording();
            });
        }

        Button dismissCoachButton = findViewById(R.id.dismissCoachButton);
        if (dismissCoachButton != null) {
            dismissCoachButton.setOnClickListener(v -> {
                Log.d(TAG, "❌ Dismiss coach button clicked");
                hideCoachConversation();
            });
        }

        Log.d(TAG, "🎉 All button click listeners set up successfully!");
    }

    /**
     * ===== CRITICAL NEW METHOD: Set up move history observation =====
     * This is what was missing - connecting your move history to the UI!
     */
    private void setupMoveHistoryObserver() {
        Log.d(TAG, "🎯 Setting up move history observer");

        // Observe the move history from ViewModel
        gameViewModel.getMoveHistory().observe(this, moves -> {
            Log.d(TAG, "📝 Move history changed, updating display: " + (moves != null ? moves.size() : 0) + " moves");
            updateMoveHistoryDisplay();
        });
    }

    /**
     * ===== ENHANCED: Better move history display method =====
     */
    private void updateMoveHistoryDisplay() {
        if (moveHistoryTextView == null) {
            Log.w(TAG, "⚠️ moveHistoryTextView is null - trying to find it again");
            moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
            if (moveHistoryTextView == null) {
                Log.e(TAG, "❌ Still can't find moveHistoryTextView!");
                return;
            }
        }

        // Get moves from GameHistoryManager
        List<String> moves = GameHistoryManager.getInstance().getCurrentGameMoves();
        Log.d(TAG, "📋 Updating move history display with " + moves.size() + " moves");

        if (moves.isEmpty()) {
            moveHistoryTextView.setText("Game begins...");
            return;
        }

        // Create beautiful formatted move history
        StringBuilder historyBuilder = new StringBuilder();

        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                // White move - start new line with move number
                int moveNumber = (i / 2) + 1;
                historyBuilder.append(moveNumber).append(". ").append(moves.get(i));

                // Add space after white's move if there's a black move coming
                if (i + 1 < moves.size()) {
                    historyBuilder.append(" ");
                }
            } else {
                // Black move - complete the line
                historyBuilder.append(moves.get(i));

                // Add newline after black's move (except for the last move)
                if (i + 1 < moves.size()) {
                    historyBuilder.append("\n");
                }
            }
        }

        String formattedHistory = historyBuilder.toString();
        moveHistoryTextView.setText(formattedHistory);

        Log.d(TAG, "✅ Move history display updated: " + formattedHistory);
    }

    /**
     * NEW METHOD: Load game configuration from splash screen or SharedPreferences
     */
    private void loadGameConfiguration() {
        // First try to get from intent (coming from splash screen)
        Intent intent = getIntent();
        if (intent.hasExtra("PLAYER_COLOR")) {
            configuredPlayerColor = intent.getStringExtra("PLAYER_COLOR");
            configuredSkillLevel = intent.getIntExtra("SKILL_LEVEL", 10);
            configuredEngineElo = intent.getIntExtra("ENGINE_ELO", 1750);

            Log.d(TAG, "📝 Configuration from splash: Color=" + configuredPlayerColor +
                    ", Skill=" + configuredSkillLevel + ", Elo=" + configuredEngineElo);
        } else {
            // Fallback to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
            configuredPlayerColor = prefs.getString("PLAYER_COLOR", "white");
            configuredSkillLevel = prefs.getInt("SKILL_LEVEL", 10);
            configuredEngineElo = prefs.getInt("ENGINE_ELO", 1750);

            Log.d(TAG, "📝 Configuration from prefs: Color=" + configuredPlayerColor +
                    ", Skill=" + configuredSkillLevel + ", Elo=" + configuredEngineElo);
        }
    }

    /**
     * NEW METHOD: Initialize game with proper configuration
     */
    private void initializeGameWithConfiguration() {
        Log.d(TAG, "🎮 Initializing game with configuration...");

        // Configure the board orientation based on player color
        if (chessBoardView != null) {
            boolean shouldFlipBoard = "black".equals(configuredPlayerColor);
            chessBoardView.setFlipped(shouldFlipBoard);
            Log.d(TAG, "🔄 Board flipped: " + shouldFlipBoard + " (player: " + configuredPlayerColor + ")");
        }

        // Set evaluation bar player color
        if (evaluationBarView != null) {
            boolean isPlayerWhite = "white".equals(configuredPlayerColor);
            evaluationBarView.setPlayerColor(isPlayerWhite);
            Log.d(TAG, "📊 Evaluation bar configured for: " + configuredPlayerColor);
        }

        // Initialize the game with proper color and difficulty
        gameViewModel.newGameWithConfiguration(configuredPlayerColor, configuredSkillLevel, configuredEngineElo);

        // Show welcome message
        String welcomeMsg = String.format("Game initialized! Playing as %s against %d Elo engine.",
                configuredPlayerColor, configuredEngineElo);
        Toast.makeText(this, welcomeMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * NEW METHOD: Set up the evaluation bar with observers! 🎯
     * This is where the magic happens - connecting your ViewModel to the UI
     */
    private void setupEvaluationBar() {
        Log.d(TAG, "🎯 Setting up evaluation bar with LiveData observers");

        // Set up observers for evaluation data
        gameViewModel.getCurrentEvaluation().observe(this, evaluation -> {
            if (evaluation != null && evaluationBarView != null) {
                Log.d(TAG, "📊 Updating evaluation bar: " + evaluation + " pawns");
                evaluationBarView.setEvaluation(evaluation);
            }
        });

        gameViewModel.getIsMatePosition().observe(this, isMate -> {
            if (isMate != null && isMate && evaluationBarView != null) {
                Integer mateInMoves = gameViewModel.getMateInMoves().getValue();
                if (mateInMoves != null) {
                    Log.d(TAG, "♔ Updating evaluation bar: MATE IN " + Math.abs(mateInMoves));
                    evaluationBarView.setMateEvaluation(mateInMoves);
                }
            }
        });

        gameViewModel.getEvaluationLoading().observe(this, isLoading -> {
            if (isLoading != null && evaluationProgressBar != null) {
                evaluationProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                Log.d(TAG, "🔄 Evaluation loading: " + (isLoading ? "Started" : "Completed"));
            }
        });

        // Set player color for the evaluation bar
        String playerColor = gameViewModel.getPlayerColor();
        boolean isPlayerWhite = "white".equalsIgnoreCase(playerColor);
        if (evaluationBarView != null) {
            evaluationBarView.setPlayerColor(isPlayerWhite);
            Log.d(TAG, "🎨 Set evaluation bar player color: " + (isPlayerWhite ? "White" : "Black"));
        }

    }

    // In your MainActivity.java
    @Override
    protected void onResume() {
        super.onResume();
        updateVoiceForCurrentMaster();
        updateCoachPortrait();

        // ===== CRITICAL: Refresh move history display on resume =====
        updateMoveHistoryDisplay();
    }

    private void updateCoachPortrait() {
        // Get the currently selected master
        String selectedMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Find the coach portrait in the message card
        ImageView coachPortrait = findViewById(R.id.coachPortraitImageView);
        TextView coachName = findViewById(R.id.coachNameTextView);

        if (coachPortrait != null && coachName != null) {
            // Set the appropriate portrait based on selected master
            switch (selectedMaster.toLowerCase()) {
                case "alekhine":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_alekhine);
                    coachName.setText("Coach Alekhine");
                    break;
                case "tal":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_tal);
                    coachName.setText("Coach Tal");
                    break;
                case "kramnik":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_kramnik);
                    coachName.setText("Coach Kramnik");
                    break;
                case "botvinnik":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_botvinnik);
                    coachName.setText("Coach Botvinnik");
                    break;
                case "fischer":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_fischer);
                    coachName.setText("Coach Fischer");
                    break;
                case "karpov":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_karpov);
                    coachName.setText("Coach Karpov");
                    break;
                case "lasker":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_lasker);
                    coachName.setText("Coach Lasker");
                    break;
                case "kasparov":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_kasparov);
                    coachName.setText("Coach Kasparov");
                    break;
                case "capablanca":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_capablanca);
                    coachName.setText("Coach Capablanca");
                    break;
                case "carlsen":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_carlsen);
                    coachName.setText("Coach Carlsen");
                    break;
                case "morphy":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_morphy);
                    coachName.setText("Coach Morphy");
                    break;
                case "anand":
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_anand);
                    coachName.setText("Coach Anand");
                    break;
                default:
                    coachPortrait.setImageResource(R.drawable.portrait_speaking_tal);
                    coachName.setText("Coach Tal");
                    break;
            }
        }
    }

    private void updateVoiceForCurrentMaster() {
        // Get the current selected master
        String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Update TTS settings to auto (which will use master-appropriate voice)
        ChessCoachManager.getInstance(this).updateTTSSettings("auto", true);
    }

    /**
     * Show the coach conversation panel over the move history
     */
    private void showCoachConversation() {
        View moveHistoryPanel = findViewById(R.id.moveHistoryPanel);
        View coachPanel = findViewById(R.id.coachConversationPanel);

        if (moveHistoryPanel != null && coachPanel != null) {
            // Fade out move history, fade in coach panel
            moveHistoryPanel.setVisibility(View.GONE);
            coachPanel.setVisibility(View.VISIBLE);

            // Beautiful slide-in animation
            coachPanel.setAlpha(0f);
            coachPanel.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        }
    }

    /**
     * Hide the coach conversation panel and show move history
     */
    private void hideCoachConversation() {
        View moveHistoryPanel = findViewById(R.id.moveHistoryPanel);
        View coachPanel = findViewById(R.id.coachConversationPanel);

        if (moveHistoryPanel != null && coachPanel != null) {
            // Fade out coach panel, fade in move history
            coachPanel.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        coachPanel.setVisibility(View.GONE);
                        moveHistoryPanel.setVisibility(View.VISIBLE);
                        moveHistoryPanel.setAlpha(0f);
                        moveHistoryPanel.animate().alpha(1f).setDuration(300).start();

                        // ===== CRITICAL: Refresh move history when panel becomes visible =====
                        updateMoveHistoryDisplay();
                    })
                    .start();
        }
    }

    // In MainActivity, when launching the analysis activity:
    public void openAnalysisScreen() {
        Intent intent = new Intent(this, GameAnalysisActivity.class);

        // Pass the current FEN
        String currentFen = chessBoardView.getCurrentFEN();
        intent.putExtra("FEN", currentFen);

        // Pass the move history
        ArrayList<String> moveHistoryList = new ArrayList<>(GameHistoryManager.getInstance().getCurrentGameMoves());
        intent.putStringArrayListExtra("MOVE_HISTORY", moveHistoryList);

        // Log what we're sending for debugging
        Log.d("MainActivity", "Sending to analysis: FEN=" + currentFen);
        Log.d("MainActivity", "Sending moves: " + moveHistoryList.size() + " moves");

        startActivity(intent);
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

    /**
     * Pre-warm the speech services on app launch for instant response
     */
    private void preWarmSpeechServices() {
        // Run on background thread to avoid blocking UI
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🔥 Pre-warming speech services");

                // Initialize OpenAI client early
                ApiKeyConfig.initializeOpenAIClient(this);

                // Pre-initialize TTS service
                OpenAITTSService.getInstance(this).setApiKey(ApiKeyConfig.getApiKey(this));

                // Pre-initialize unified service
                OpenAIService.getInstance().setApiKey(ApiKeyConfig.getApiKey(this));

                Log.d(TAG, "✅ Speech services pre-warmed");
            } catch (Exception e) {
                Log.e(TAG, "Error pre-warming services", e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Add these methods to your MainActivity class

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
        // ENHANCED: Add null check at the beginning
        if (chessBoardView == null) {
            Log.e(TAG, "❌ setupChessBoard called but chessBoardView is null!");
            return;
        }

        // Set up observers to watch for changes in the game state
        gameViewModel.getCurrentFEN().observe(this, fen -> {
            // Update the board view when the FEN changes
            chessBoardView.updateBoardFromFen(fen);

            // NEW: Also update the real FEN for turn checking! 🎯
            chessBoardView.setRealCurrentFEN(fen);

            Log.d(TAG, "📝 Updated board with new FEN: " + fen);
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

        chessBoardView.setOnSquareTapListener((row, col) -> {

            Log.d(TAG, "🎯 SQUARE TAPPED: row=" + row + ", col=" + col);
            Log.d(TAG, "📝 Player color: " + configuredPlayerColor);
            Log.d(TAG, "🔍 Selected row/col: " + chessBoardView.getSelectedRow() + "/" + chessBoardView.getSelectedCol());

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

                    // FIXED: Only check piece ownership for actual pieces, not empty squares
                    if (tappedPiece != ' ') {
                        // NEW: Improved piece ownership logic for player perspective
                        boolean isPlayerWhite = "white".equals(configuredPlayerColor);
                        boolean isPieceWhite = Character.isUpperCase(tappedPiece);
                        boolean isOurPiece = (isPlayerWhite && isPieceWhite) || (!isPlayerWhite && !isPieceWhite);

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
                    }

                    // Otherwise, try to make a move from the selected piece to this square
                    String move = algebraicNotation(fromRow, fromCol) + algebraicNotation(row, col);

                    boolean isPlayerWhite = "white".equals(configuredPlayerColor);
                    boolean isPlayersTurn = (isPlayerWhite && chessBoardView.isWhiteTurn()) ||
                            (!isPlayerWhite && !chessBoardView.isWhiteTurn());

                    Log.d(TAG, "🎯 ATTEMPTING MOVE: " + move);
                    Log.d(TAG, "📝 Player color: " + configuredPlayerColor);
                    Log.d(TAG, "🔄 Is player's turn: " + isPlayersTurn);
                    Log.d(TAG, "🔄 Is white's turn: " + chessBoardView.isWhiteTurn());

                    if (isPlayersTurn) {
                        Log.d(TAG, "✅ Turn validation passed, making move: " + move);
                        gameViewModel.makePlayerMove(move);
                    } else {
                        Log.d(TAG, "❌ Turn validation failed - not player's turn!");
                        Toast.makeText(MainActivity.this, "Wait for your turn!", Toast.LENGTH_SHORT).show();
                    }

                    chessBoardView.clearSelectionHighlight();
                    chessBoardView.clearHighlightedSquares();
                } else {
                    // No piece is selected yet, select if it's our piece and our turn
                    char piece = chessBoardView.getPieceAt(row, col);

                    // NEW: Improved piece ownership and turn logic
                    boolean isPlayerWhite = "white".equals(configuredPlayerColor);
                    boolean isPieceWhite = Character.isUpperCase(piece);
                    boolean isOurPiece = (isPlayerWhite && isPieceWhite) || (!isPlayerWhite && !isPieceWhite);
                    boolean isPlayersTurn = (isPlayerWhite && chessBoardView.isWhiteTurn()) ||
                            (!isPlayerWhite && !chessBoardView.isWhiteTurn());

                    if (piece != ' ' && isOurPiece && isPlayersTurn) {
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

    // Add these methods to MainActivity class

    // Start recording method
    private void startRecording() {
        if (isServiceBound && recordService != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
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

        // Use our ExecutorService instead of creating a new Thread
        executorService.execute(() -> {
            try {
                // Get challenge from OpenAI (synchronous call)
                String response = openAIService.getChatCompletion(
                        "You are Coach Tal, a chess grandmaster creating engaging, instructive tactical puzzles.",
                        prompt);

                // Process the response on the UI thread
                mainHandler.post(() -> {
                    showChallengeLoading(false);
                    parseChallengeResponse(response);
                });
            } catch (Exception e) {
                // Handle errors on the UI thread
                mainHandler.post(() -> {
                    showChallengeLoading(false);
                    Toast.makeText(MainActivity.this,
                            "Couldn't create a challenge right now. Let's try again later!",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
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


    // Shows the listening animation
    private void showListeningFeedback() {
        // Show coach message card if not visible

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

        // Update UI to show we're listening
        updateUIState(ProcessingState.LISTENING);

        // Use the bound service to start recording
        if (isServiceBound && recordService != null) {
            executorService.execute(() -> {
                try {
                    Log.d(TAG, "🎤 Starting recording via bound service...");

                    // This will trigger onRecordingStarted callback when it begins
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        recordService.startRecording();
                    }

                    // Note: The rest of the pipeline is handled by the service callbacks
                    // We'll update those next

                } catch (Exception e) {
                    Log.e(TAG, "Error in voice recording: " + e.getMessage());
                    mainHandler.post(() -> {
                        updateUIState(ProcessingState.IDLE);
                        Toast.makeText(MainActivity.this,
                                "Voice recording error. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Log.e(TAG, "❌ Service not bound yet, trying to bind now...");
            Toast.makeText(this, "Connecting to voice service...", Toast.LENGTH_SHORT).show();

            // Try to bind the service
            bindRecordService();

            // Schedule a retry after a short delay
            new android.os.Handler().postDelayed(() -> {
                if (isServiceBound && recordService != null) {
                    startVoiceRecording();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Could not connect to voice service. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }, 1000); // 1 second delay
        }
    }

    private void updateUIState(ProcessingState state, String text) {
        // Update UI elements based on state
        TextView statusTextView = findViewById(R.id.statusTextView);
        View micIndicator = findViewById(R.id.micIndicator);

        switch (state) {
            case IDLE:
                if (statusTextView != null) {
                    statusTextView.setText("Ready to assist you with your chess journey");
                }
                if (micIndicator != null) {
                    micIndicator.setVisibility(View.GONE);
                    if (pulseAnimatorSet != null) {
                        pulseAnimatorSet.cancel();
                    }
                }
                if (speakButton != null) {
                    speakButton.setEnabled(true);
                }
                break;

            case LISTENING:
                if (statusTextView != null) {
                    statusTextView.setText("Listening to you...");
                }
                if (micIndicator != null) {
                    micIndicator.setVisibility(View.VISIBLE);

                    // Create pulse animation if needed
                    if (pulseAnimatorSet == null || pulseAnimatorSet.isStarted()) {
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
                    }
                }
                if (speakButton != null) {
                    speakButton.setEnabled(false);
                }
                break;

            case TRANSCRIBING:
                if (statusTextView != null) {
                    statusTextView.setText("Processing what you said...");
                }
                break;

            case THINKING:
                if (statusTextView != null) {
                    statusTextView.setText("Coach Tal is thinking...");
                }
                if (micIndicator != null) {
                    micIndicator.setVisibility(View.GONE);
                    if (pulseAnimatorSet != null) {
                        pulseAnimatorSet.cancel();
                    }
                }
                break;
            case SPEAKING:
                if (statusTextView != null) {
                    statusTextView.setText("Coach is responding...");
                }
                // Show the coach conversation panel instead of the old card
                showCoachConversation();
                break;
        }

        // Update text if provided
        if (text != null) {
            TextView coachMessageText = findViewById(R.id.coachMessageText);
            if (coachMessageText != null) {
                if (state == ProcessingState.THINKING) {
                    coachMessageText.setText("I heard: " + text + "\nThinking...");
                } else if (state == ProcessingState.SPEAKING) {
                    coachMessageText.setText(text);
                }
            }
        }
    }

    // Overload for when no text is provided
    private void updateUIState(ProcessingState state) {
        updateUIState(state, null);
    }

    // Show coach's response
    private void showCoachResponse(String message) {

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
    /**
     * Set up click listeners for all buttons
     */

    // Replace your setupSpeakButton method with this:
    private void setupSpeakButton() {

        FloatingActionButton speakButton = findViewById(R.id.speakButton);
        if (speakButton != null) {
            speakButton.setOnClickListener(v -> {
                Log.d(TAG, "Speak button clicked");

                OpenAITTSService tts = OpenAITTSService.getInstance(this);
                if (tts != null && tts.isSpeaking()) {
                    tts.stopSpeech();
                    tts.setSpeechCallback(new OpenAITTSService.SpeechCallback() {
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

    // And update your initializeViews method to use Button instead of FloatingActionButton:
    private void initializeViews() {
        try {
            Log.d(TAG, "🔍 Starting to find views...");

            // Find all views once - no duplicates!
            // FIXED: All changed to FloatingActionButton to match XML
            gameAnalysisButton = findViewById(R.id.gameAnalysisButton);
            Log.d(TAG, "gameAnalysisButton: " + (gameAnalysisButton != null ? "✅ Found" : "❌ NULL"));

            coachButton = findViewById(R.id.coachButton);
            Log.d(TAG, "coachButton: " + (coachButton != null ? "✅ Found" : "❌ NULL"));

            speakButton = findViewById(R.id.speakButton);
            Log.d(TAG, "speakButton: " + (speakButton != null ? "✅ Found" : "❌ NULL"));

            // The critical one - let's see what happens here
            Log.d(TAG, "🎯 Looking for chessBoardView...");
            chessBoardView = findViewById(R.id.chessBoardView);
            Log.d(TAG, "chessBoardView: " + (chessBoardView != null ? "✅ Found" : "❌ NULL"));

            if (chessBoardView == null) {
                Log.e(TAG, "❌ ChessBoardView is NULL! This suggests a layout inflation issue.");
            }

            // NEW: Initialize evaluation bar UI elements! 🎯
            evaluationBarView = findViewById(R.id.evaluationBarView);
            Log.d(TAG, "evaluationBarView: " + (evaluationBarView != null ? "✅ Found" : "❌ NULL"));

            evaluationProgressBar = findViewById(R.id.evaluationProgressBar);
            Log.d(TAG, "evaluationProgressBar: " + (evaluationProgressBar != null ? "✅ Found" : "❌ NULL"));

            // ===== CRITICAL: Initialize move history UI element =====
            moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
            Log.d(TAG, "moveHistoryTextView: " + (moveHistoryTextView != null ? "✅ Found" : "❌ NULL"));

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception during view finding: " + e.getMessage(), e);
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
        OpenAITTSService.getInstance(this).speak(message);
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
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Check if microphone permission is granted, request if not
     *
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
        // Shutdown our executor service
        executorService.shutdown();

        // Unbind from the service
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }

        super.onDestroy();
    }

    // Add this enum for tracking processing states
    private enum ProcessingState {
        IDLE, LISTENING, TRANSCRIBING, THINKING, SPEAKING
    }
}