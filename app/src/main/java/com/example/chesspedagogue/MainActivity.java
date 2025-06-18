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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.chesspedagogue.viewmodel.GameViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements VoiceControlManager.VoiceCommandListener {
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;
    private static final int REQUEST_CHESS_SET_SELECTION = 1002;
    
    // 🎤 Always-listening voice control
    private VoiceControlManager voiceControlManager;

    // Add this near your other class members
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    // Handler for UI updates
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    // State tracking
    private final boolean conversationActive = false; // tracks if service is running

    private AnimatorSet pulseAnimatorSet;
    private OpenAIService openAIService;
    
    // NEW: Responses API integration for enhanced challenge generation
    private ResponsesAPIIntegrationHelper integrationHelper;
    private ChessMasterResponseManager responsesManager;
    
    // NEW: Live monitoring integration with configurator
    private LiveMonitorClient liveMonitorClient;
    
    private String selectedSquare = null;
    private boolean isSpeaking = false;

    // UI elements - FIXED: All changed to FloatingActionButton to match XML
    private FloatingActionButton conversationButton;
    private ChallengeData currentChallenge;
    private boolean inChallengeMode = false;

    private FloatingActionButton speakButton;
    private FloatingActionButton gameAnalysisButton;  // FIXED: Changed from Button to FloatingActionButton
    private FloatingActionButton coachButton;         // FIXED: Changed from Button to FloatingActionButton
    private FloatingActionButton competitiveModeButton;  // NEW: Competitive mode vs chess masters
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
    
    // Voice status indicator
    private VoiceStatusIndicator voiceStatusIndicator;

    // Add this field to MainActivity
    private long lastMoveHistoryUpdate = 0;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "🎯 onServiceConnected called!");
            try {
                SimpleRecordService.LocalBinder binder = (SimpleRecordService.LocalBinder) service;
                recordService = binder.getService();
                isServiceBound = true;
                Log.d(TAG, "🎉 Service connected successfully!");
                Log.d(TAG, "📋 Service instance: " + (recordService != null ? "VALID" : "NULL"));

                updateVoiceForCurrentMaster();

            if (recordService != null) {
                recordService.refreshVoiceSettings();
            }

            // Set up the callback to handle responses
            Log.d(TAG, "🔗 Setting up ServiceCallback...");
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
                public void onTranscriptionReceived(String transcribedText) {
                    Log.d(TAG, "📝 Transcription received: " + transcribedText);
                    // Update UI state directly (transcription popup removed)
                    mainHandler.post(() -> {
                        updateUIState(ProcessingState.THINKING);
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
            
            Log.d(TAG, "✅ ServiceCallback setup completed!");
            
            } catch (Exception e) {
                Log.e(TAG, "💥 Exception in onServiceConnected", e);
            }
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
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "🚀 MainActivity onCreate starting...");
            setContentView(R.layout.activity_main);
            Log.d(TAG, "✅ setContentView completed!");
            
            // FIXED: Bind hardware volume buttons to media stream for TTS control
            setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);
            
            // Initialize log throttling system early
            LogThrottlerConfig.initialize(this);
            
            preWarmSpeechServices();
            LogThrottler.d(TAG, "✅ preWarmSpeechServices completed!");

        // NEW: Get configuration from splash screen
        loadGameConfiguration();

        // In onCreate or similar initialization method
        ApiKeyConfig.initializeOpenAIClient(this);

        // Configure ElevenLabs API key - ALTERNATE ACCOUNT
        // TODO: Move this to a secure configuration file
        ElevenLabsConfig.setApiKey(this, "sk_788fa3710ea8bb4363f71f110a7b360a50f48beb4168fbf4");
        
        // Enable ElevenLabs TTS if API key is set
        if (ElevenLabsConfig.hasApiKey(this)) {
            TTSServiceManager.setUseElevenLabs(this, true);
            Log.d(TAG, "✅ ElevenLabs TTS enabled");
        }

        // Initialize all UI elements
        Log.d(TAG, "🎯 About to call initializeViews...");
        initializeViews();
        Log.d(TAG, "✅ initializeViews completed!");

        // ENHANCED: Add null check for chessBoardView before proceeding
        if (chessBoardView == null) {
            Log.e(TAG, "❌ CRITICAL: ChessBoardView is null after initializeViews!");
            Toast.makeText(this, "Error initializing chess board. Please restart the app.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "✅ ChessBoardView validated successfully!");

        // Initialize game ViewModel with configuration
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // FIXED: Initialize OpenAI service asynchronously to prevent ANR
        initializeOpenAIServiceAsync();

        initializeChessMasterDatabase();
        
        // FIXED: Initialize Responses API services asynchronously to prevent ANR
        initializeResponsesAPIServicesAsync();
        Log.d(TAG, "✅ Responses API services initializing asynchronously");

        Button debugButton = new Button(this);
        // Set up click listeners - this is what was missing!
        setupAllButtonClickListeners();
        // Add these lines in your onCreate() method after setupAllButtonClickListeners();
        setupPersonalityEngineButton();
        setupPersonalityObservers();

        // Add this line after setupPersonalityObservers();
        setupAutoCommentaryObservers();

// Enable auto-commentary by default
        gameViewModel.configureAutoCommentary(true);

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

        // NEW: Initialize live monitoring client
        initializeLiveMonitoring();
        
        // Bind to the SimpleRecordService
        Log.d(TAG, "📞 About to call bindRecordService...");
        bindRecordService();
        
        // 🎤 Initialize always-listening voice control manager
        initializeVoiceControlManager();
        
        // 👤 Initialize user profile system and check for profile creation
        initializeUserProfileSystem();
        
        Log.d(TAG, "✅ MainActivity onCreate completed!");
        
        } catch (Exception e) {
            Log.e(TAG, "💥 CRITICAL: Exception in onCreate!", e);
            // Try to show error to user
            Toast.makeText(this, "App initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 👤 Initialize user profile system and prompt for profile creation if needed
     */
    private void initializeUserProfileSystem() {
        try {
            Log.d(TAG, "👤 Initializing user profile system...");
            
            UserProfileManager profileManager = UserProfileManager.getInstance(this);
            
            // Check if user has a profile
            if (!profileManager.hasProfile()) {
                Log.d(TAG, "👤 No user profile found - checking if we should prompt");
                
                // Check if user has dismissed the profile prompt permanently
                SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                boolean promptDismissed = prefs.getBoolean("profile_prompt_dismissed", false);
                
                if (!promptDismissed) {
                    // Show profile creation prompt after a short delay to ensure UI is ready
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            UserProfileActivity.showProfilePrompt(this);
                        }
                    }, 2000); // 2 second delay
                }
            } else {
                Log.d(TAG, "👤 User profile found: " + profileManager.getProfile().getDisplayName());
                // Profile exists - masters already know about the user
            }
            
            Log.d(TAG, "✅ User profile system initialized");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing user profile system", e);
        }
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
                Log.d(TAG, "🎤 MODIFIED SPEAK BUTTON CLICKED - SERVICE BOUND: " + isServiceBound);

                OpenAITTSService tts = TTSServiceManager.getOpenAITTSService(this);
                
                // Set context for main game screen (ultra-fast eleven_flash_v2_5)
                TTSServiceManager.setUsageContext(this, "main_game");
                
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

        // NEW: Competitive Mode Button - launch competitive mode vs chess masters
        if (competitiveModeButton != null) {
            competitiveModeButton.setOnClickListener(v -> {
                Log.d(TAG, "🏆 Competitive Mode button clicked");
                launchCompetitiveMode();
            });
            
            // Add long-press for red highlight toggle
            competitiveModeButton.setOnLongClickListener(v -> {
                Log.d(TAG, "🔥 Competitive Mode button long-pressed - toggling highlight");
                toggleCompetitiveModeHighlight();
                return true;
            });
            
            Log.d(TAG, "✅ Competitive Mode button listener set");
        } else {
            Log.w(TAG, "⚠️ competitiveModeButton is null!");
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
     * 🎭 PERSONALITY ENGINE TOGGLE & 🎤 ALWAYS-LISTENING TOGGLE - The Magic Button!
     * Add this method to your MainActivity.java class
     */
    private void setupPersonalityEngineButton() {
        // We'll add this as a long-press action on the coach button
        // Or you can add a new button - your choice!

        if (speakButton != null) {
            // Add long-press listener for dual functionality
            speakButton.setOnLongClickListener(v -> {
                Log.d(TAG, "🎭 Long press detected - showing options");
                showLongPressOptionsDialog();
                return true; // Consume the long press
            });
        }
    }

    /**
     * 🎤 Show options dialog for long press (personality toggle + always-listening toggle)
     */
    private void showLongPressOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎤 Voice & AI Options");
        
        // Get current states
        Boolean personalityEnabled = gameViewModel.getPersonalityEngineEnabled().getValue();
        boolean isPersonalityOn = personalityEnabled != null && personalityEnabled;
        boolean alwaysListeningEnabled = voiceControlManager != null && voiceControlManager.isAlwaysListeningEnabled();
        
        String message = "Choose an option:\n\n" +
                        "🎭 Personality Engine: " + (isPersonalityOn ? "ON" : "OFF") + "\n" +
                        "🎤 Always-Listening: " + (alwaysListeningEnabled ? "ON" : "OFF");
        
        builder.setMessage(message);
        
        builder.setPositiveButton("🎭 Toggle Personality", (dialog, which) -> {
            togglePersonalityEngine();
        });
        
        builder.setNeutralButton("🎤 Toggle Always-Listening", (dialog, which) -> {
            if (voiceControlManager != null) {
                voiceControlManager.toggleAlwaysListening();
            }
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        builder.show();
    }

    /**
     * 🎭 Enhanced personality observers with progress feedback
     * Add this to your MainActivity.java (replace existing setupPersonalityObservers)
     */
    private void setupPersonalityObservers() {
        // Observe personality engine state
        gameViewModel.getPersonalityEngineEnabled().observe(this, enabled -> {
            if (enabled != null) {
                Log.d(TAG, "🎭 Personality engine state: " + (enabled ? "ENABLED" : "DISABLED"));

                // Update speak button appearance based on personality state
                if (speakButton != null) {
                    if (enabled) {
                        // Golden glow when personality is active
                        speakButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                                getResources().getColor(R.color.purple_700)));
                        // You could add a subtle animation here too
                    } else {
                        // Normal appearance for vanilla Stockfish
                        speakButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                                getResources().getColor(R.color.bronze)));
                    }
                }
            }
        });

        // Show progress when status changes
        gameViewModel.getStatusMessage().observe(this, message -> {
            if (message != null && message.contains("searching") && message.contains("archive")) {
                // Show progress for personality search
                Toast.makeText(this, "🔍 Searching chess history...", Toast.LENGTH_SHORT).show();
            }
        });

        // Celebrate historical moves!
        gameViewModel.getIsHistoricalMove().observe(this, isHistorical -> {
            if (Boolean.TRUE.equals(isHistorical)) {
                showHistoricalMoveEffect();
            }
        });
    }

    /**
     * NEW: Set up automatic commentary observers
     */
    private void setupAutoCommentaryObservers() {
        Log.d(TAG, "🎙️ Setting up automatic commentary observers");

        // Observe evaluation swings for UI feedback
        gameViewModel.getLastEvaluationSwing().observe(this, swing -> {
            if (swing != null && swing.isSignificant()) {
                Log.d(TAG, "🎯 Significant evaluation swing detected: " + swing);
                showEvaluationSwingFeedback(swing);
            }
        });

        // Observe move explanations
        gameViewModel.getLastMoveExplanation().observe(this, explanation -> {
            if (explanation != null && !explanation.isEmpty()) {
                Log.d(TAG, "💭 Move explanation: " + explanation);
                // You could display this in a toast or status bar if desired
            }
        });

        // Observe historical moves for special effects
        gameViewModel.getIsHistoricalMove().observe(this, isHistorical -> {
            if (Boolean.TRUE.equals(isHistorical)) {
                Log.d(TAG, "🏛️ HISTORICAL MOVE DETECTED!");
                showHistoricalMoveEffect(); // This method already exists
            }
        });

        // Observe master quotes (automatic commentary)
        gameViewModel.getMasterQuote().observe(this, quote -> {
            if (quote != null && !quote.isEmpty()) {
                Log.d(TAG, "🎭 Auto-commentary received: " + quote.substring(0, Math.min(50, quote.length())));
                displayAutoCommentary(quote);
            }
        });
    }

    // Add this method to MainActivity.java
    private void initializeChessMasterDatabase() {
        Log.d("MainActivity", "🚀 Initializing chess master database...");

        // Show a subtle loading indicator while we check the database
        showDatabaseLoadingIndicator(true);
        
        // Run EVERYTHING on background thread, including the initial check
        executorService.execute(() -> {
            try {
                GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
                
                // Check if we already have data (this was blocking the UI!)
                boolean hasData = dbHelper.hasMasterData("tal");
                
                // 🎯 FORCE REIMPORT: Clear existing data if master names don't match
                // This fixes the "Mikhail Tal" vs "tal" mismatch issue
                if (hasData) {
                    // Check if data was imported with wrong master names
                    Map<String, Integer> stats = dbHelper.getDatabaseStats();
                    boolean hasWrongNames = false;
                    for (String masterName : stats.keySet()) {
                        if (masterName.contains(" ") || masterName.length() > 10) {
                            // Found full names like "Mikhail Tal" instead of "tal"
                            hasWrongNames = true;
                            break;
                        }
                    }
                    
                    if (hasWrongNames) {
                        Log.d("MainActivity", "🔄 Found data with incorrect master names - forcing reimport...");
                        dbHelper.clearAndReimportAllData();
                        hasData = false; // Force reimport
                    }
                }
                
                if (!hasData) {
                    Log.d("MainActivity", "📥 First run - importing comprehensive chess data...");
                    
                    // Update UI to show import progress
                    runOnUiThread(() -> {
                        updateStatusMessage("Loading chess master data...");
                    });
                    // Import all your master data files (using correct _full_positions.json format)
                    // Only include masters that actually have data files available
                    String[] masters = {"tal", "fischer", "kasparov", "carlsen", "anand", "kramnik", "karpov", "alekhine", "capablanca"};

                    for (String master : masters) {
                        // Use consistent _full_positions.json naming convention
                        String filename = master + "_full_positions.json";
                        Log.d("MainActivity", "📥 Importing " + filename + "...");
                        
                        // Update progress on UI thread
                        final String currentMaster = master;
                        runOnUiThread(() -> {
                            updateStatusMessage("Loading " + currentMaster + " data...");
                        });

                        boolean success = dbHelper.importMasterPositionsFromAssets(filename);

                        if (success) {
                            Log.d("MainActivity", "✅ Successfully imported " + master + " data!");
                        } else {
                            Log.w("MainActivity", "⚠️ Could not import " + filename);
                        }
                    }

                    // Log final database stats and diagnostics
                    Map<String, Integer> stats = dbHelper.getDatabaseStats();
                    Log.d("MainActivity", "🎉 Database ready! Stats: " + stats.toString());
                    
                    // 🔍 Run comprehensive diagnostics
                    String diagnostics = dbHelper.getDatabaseDiagnostics();
                    Log.d("MainActivity", "🔬 " + diagnostics);
                    
                } else {
                    Log.d("MainActivity", "✅ Chess master database already initialized!");
                    
                    // Log current stats and diagnostics
                    Map<String, Integer> stats = dbHelper.getDatabaseStats();
                    Log.d("MainActivity", "📊 Current database stats: " + stats.toString());
                    
                    // 🔍 Run comprehensive diagnostics
                    String diagnostics = dbHelper.getDatabaseDiagnostics();
                    Log.d("MainActivity", "🔬 " + diagnostics);
                }
                
                // Update UI on main thread - database is ready!
                runOnUiThread(() -> {
                    showDatabaseLoadingIndicator(false);
                    Log.d("MainActivity", "🎯 Chess master personalities loaded and ready!");
                    // Enable personality engine button now that data is loaded
                    if (speakButton != null) {
                        speakButton.setEnabled(true);
                    }
                });
                
            } catch (Exception e) {
                Log.e("MainActivity", "❌ Error during database initialization", e);
                runOnUiThread(() -> {
                    showDatabaseLoadingIndicator(false);
                    Toast.makeText(MainActivity.this, 
                        "Error loading chess data. Some features may be limited.", 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // Add this to MainActivity.onCreate() - one-time database setup
    private void initializePersonalityDatabase() {
        // This method appears to be duplicate functionality - using the async version above
        // Keeping it for compatibility but redirecting to the async version
        initializeChessMasterDatabase();
    }

    /**
     * 🎭 Updated toggle for speak button
     * Add this to replace your existing toggle method
     */
    private void togglePersonalityEngine() {
        Boolean currentState = gameViewModel.getPersonalityEngineEnabled().getValue();
        boolean isEnabled = currentState != null && currentState;

        String selectedMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        if (isEnabled) {
            // Turn OFF personality engine
            gameViewModel.configurePersonalityEngine(selectedMaster, 0.3f, false);
            Toast.makeText(this, "🤖 Fast Stockfish mode", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "✅ Switched to vanilla Stockfish mode");

        } else {
            // Turn ON personality engine
            gameViewModel.configurePersonalityEngine(selectedMaster, 0.3f, true);
            String masterName = FineTunedModelManager.getInstance(this).getMasterDisplayName(selectedMaster);

            Toast.makeText(this, "🎭 " + masterName + " mode activated! Moves may take longer as we search game history.", Toast.LENGTH_LONG).show();
            showPersonalityActivationMessage(masterName);
            Log.d(TAG, "✅ Switched to " + masterName + " personality mode");
        }
    }

    /**
     * 🎯 Enhanced historical move effect
     */
    private void showHistoricalMoveEffect() {
        // Flash the evaluation bar with gold color
        if (evaluationBarView != null) {
            // Create a brief golden flash effect
            ObjectAnimator flashAnimator = ObjectAnimator.ofFloat(evaluationBarView, "alpha", 1.0f, 0.3f, 1.0f);
            flashAnimator.setDuration(800);
            flashAnimator.start();
        }

        // Show exciting toast message
        Toast.makeText(this, "🎯 Historical move! Pure chess legend!", Toast.LENGTH_LONG).show();

        // You could also make the board briefly glow or add particle effects here
    }

    /**
     * NEW: Display automatic commentary using fine-tuned model directly
     */
    private void displayAutoCommentary(String commentary) {
        // Update the coach message text with the automatic commentary
        updateCoachMessageText(commentary);

        // Briefly show the coach panel if it's not visible
        showCoachConversation();

        // Don't auto-hide for auto-commentary - let user dismiss manually
        Log.d(TAG, "🎭 Auto-commentary displayed using fine-tuned model");
    }

    /**
     * NEW: Show visual feedback for evaluation swings
     */
    private void showEvaluationSwingFeedback(EvaluationTracker.EvaluationSwing swing) {
        String message = "";
        int color = Color.BLUE;

        switch (swing.quality) {
            case BRILLIANT:
                message = "Brilliant move! ⭐";
                color = Color.parseColor("#FFD700"); // Gold
                break;
            case EXCELLENT:
                message = "Excellent!";
                color = Color.GREEN;
                break;
            case BLUNDER:
                message = "Blunder detected";
                color = Color.RED;
                break;
            case MISTAKE:
                message = "Mistake";
                color = Color.parseColor("#FF8C00"); // Dark orange
                break;
            case INACCURACY:
                message = "Inaccuracy";
                color = Color.parseColor("#FFA500"); // Orange
                break;
            default:
                return; // Don't show feedback for normal moves
        }

        // Show brief toast with evaluation change
        String fullMessage = String.format("%s (%.1f → %.1f)",
                message,
                swing.previousEval.getEffectiveEvaluation(),
                swing.currentEval.getEffectiveEvaluation());

        Toast.makeText(this, fullMessage, Toast.LENGTH_SHORT).show();

        // Optional: Highlight the evaluation bar briefly with the appropriate color
        if (evaluationBarView != null) {
            // Create a brief flash effect with the swing color
            ObjectAnimator colorFlash = ObjectAnimator.ofInt(evaluationBarView, "backgroundColor",
                    Color.TRANSPARENT, color, Color.TRANSPARENT);
            colorFlash.setDuration(800);
            colorFlash.start();
        }
    }

    /**
     * 🎭 Show exciting message when personality is activated
     */
    private void showPersonalityActivationMessage(String masterName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎭 Revolutionary Chess AI Activated!")
                .setMessage("You're now playing against " + masterName + "! " +
                        "The engine will play moves based on " + masterName + "'s actual games and style. " +
                        "Watch for historical moves marked with 🎯!")
                .setPositiveButton("Let's Play!", (dialog, id) -> dialog.dismiss())
                .show();
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
            
            // NEW: Send live monitoring update for game state
            sendLiveGameStateUpdate();
        });
    }


    /**
     * OPTIMIZED: Move history display with reduced updates
     */
    private void updateMoveHistoryDisplay() {
        // OPTIMIZATION: Skip update if we're not visible
        if (moveHistoryTextView == null || moveHistoryTextView.getVisibility() != View.VISIBLE) {
            return;
        }

        // OPTIMIZATION: Debounce rapid updates
        if (System.currentTimeMillis() - lastMoveHistoryUpdate < 100) {
            return;
        }
        lastMoveHistoryUpdate = System.currentTimeMillis();

        // Get moves from GameHistoryManager
        List<String> moves = GameHistoryManager.getInstance().getCurrentGameMoves();
        Log.d(TAG, "📋 Updating move history display with " + moves.size() + " moves");

        if (moves.isEmpty()) {
            moveHistoryTextView.setText("Game begins...");
            return;
        }

        StringBuilder historyBuilder = new StringBuilder(moves.size() * 10);

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

        moveHistoryTextView.setText(historyBuilder.toString());
        Log.d(TAG, "✅ Move history display updated efficiently");
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
                    
            // CRITICAL FIX: Synchronize master selection to fix SharedPreferences inconsistencies
            synchronizeMasterSelectionOnStartup();
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
     * NEW METHOD: Send live game state update to configurator monitor
     */
    private void sendLiveGameStateUpdate() {
        if (liveMonitorClient == null || !liveMonitorClient.isConnected()) {
            return;
        }
        
        try {
            // Get current game state
            List<String> moves = GameHistoryManager.getInstance().getCurrentGameMoves();
            String currentMove = moves.isEmpty() ? "Game not started" : moves.get(moves.size() - 1);
            String currentFen = chessBoardView != null ? chessBoardView.getCurrentFEN() : "Unknown";
            
            // Get evaluation if available (you might need to add this from your evaluation system)
            double evaluation = 0.0; // TODO: Get from evaluation system
            
            // Determine game mode
            String gameMode = "Main Game";
            boolean isGameActive = !moves.isEmpty();
            String currentPlayer = (moves.size() % 2 == 0) ? "White" : "Black";
            int moveNumber = (moves.size() / 2) + 1;
            
            liveMonitorClient.sendGameState(
                currentMove, 
                currentFen, 
                evaluation, 
                gameMode, 
                isGameActive, 
                currentPlayer, 
                moveNumber
            );
            
            Log.d(TAG, "📡 Live monitoring: Game state sent");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending live game state: " + e.getMessage());
        }
    }
    
    /**
     * NEW METHOD: Send live conversation update to configurator monitor
     */
    private void sendLiveConversationUpdate(String speaker, String message, String messageType) {
        if (liveMonitorClient == null || !liveMonitorClient.isConnected()) {
            return;
        }
        
        try {
            // Get current master for emotion context
            SharedPreferences prefs = getSharedPreferences("chess_master_prefs", MODE_PRIVATE);
            String currentMaster = prefs.getString("selected_master", "Tal");
            
            // Determine emotion based on message content (basic sentiment analysis)
            String emotion = "neutral";
            if (message.contains("excellent") || message.contains("brilliant")) {
                emotion = "excited";
            } else if (message.contains("mistake") || message.contains("error")) {
                emotion = "critical";
            } else if (message.contains("think") || message.contains("consider")) {
                emotion = "contemplative";
            }
            
            liveMonitorClient.sendConversation(
                speaker.equals("Coach") ? currentMaster : speaker,
                message,
                emotion,
                messageType
            );
            
            Log.d(TAG, "📡 Live monitoring: Conversation sent from " + speaker);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending live conversation: " + e.getMessage());
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
        
        // 🎤 Re-register with voice control manager
        if (voiceControlManager != null) {
            voiceControlManager.registerVoiceCommandListener(this);
            Log.d(TAG, "🎤 Re-registered with voice control manager");
        }
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
        
        // 🚨 CRITICAL FIX: Update PersonalityEngine with the current master selection
        LogThrottler.force("MainActivity", "🎯 UPDATING PERSONALITY ENGINE: Switching to master: " + currentMaster);
        if (gameViewModel != null) {
            gameViewModel.configurePersonalityEngine(currentMaster, 1.0f, true);
            LogThrottler.force("MainActivity", "✅ PERSONALITY ENGINE UPDATED: Now using " + currentMaster);
        } else {
            LogThrottler.force("MainActivity", "⚠️ WARNING: gameViewModel is null, couldn't update PersonalityEngine");
        }
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
        Log.d(TAG, "🔄 Starting bindRecordService process...");
        Intent serviceIntent = new Intent(this, SimpleRecordService.class);
        
        try {
            // Start and bind the service
            Log.d(TAG, "🚀 Starting SimpleRecordService...");
            startService(serviceIntent);
            
            Log.d(TAG, "🔗 Binding to SimpleRecordService...");
            boolean bindResult = bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            
            Log.d(TAG, "📊 Service binding result: " + (bindResult ? "SUCCESS" : "FAILED"));
            if (!bindResult) {
                Log.e(TAG, "❌ Failed to bind to SimpleRecordService!");
            }
        } catch (Exception e) {
            Log.e(TAG, "💥 Exception during service binding", e);
        }
    }

    // Add this method to your existing MainActivity.java class
    private void launchSpectatorMode() {
        Log.d(TAG, "🎭 Launching spectator mode selection");

        // For now, create a simple selection dialog
        // You can enhance this with a proper selection activity later
        String[] masters = {"tal", "fischer", "kramnik", "kasparov", "karpov", "alekhine", "capablanca", "carlsen", "anand", "morphy", "lasker", "botvinnik"};
        String[] displayNames = new String[masters.length];

        for (int i = 0; i < masters.length; i++) {
            displayNames[i] = FineTunedModelManager.getInstance(this).getMasterDisplayName(masters[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select White Player");
        builder.setItems(displayNames, (dialog, which) -> {
            String whitePlayer = masters[which];

            // Now select black player
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("Select Black Player");
            builder2.setItems(displayNames, (dialog2, which2) -> {
                String blackPlayer = masters[which2];

                if (whitePlayer.equals(blackPlayer)) {
                    Toast.makeText(this, "Please select different players!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Launch spectator game
                Intent intent = new Intent(this, SpectatorGameActivity.class);
                intent.putExtra("WHITE_PLAYER", whitePlayer);
                intent.putExtra("BLACK_PLAYER", blackPlayer);
                startActivity(intent);
            });
            builder2.show();
        });
        builder.show();
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
                TTSServiceManager.getOpenAITTSService(this).setApiKey(ApiKeyConfig.getApiKey(this));

                // Pre-initialize unified service
                OpenAIService.getInstance().setApiKey(ApiKeyConfig.getApiKey(this));

                Log.d(TAG, "✅ Speech services pre-warmed");
            } catch (Exception e) {
                Log.e(TAG, "Error pre-warming services", e);
            }
        });
    }

    /**
     * ADDED: Async initialization for OpenAI service to prevent ANR
     */
    private void initializeOpenAIServiceAsync() {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🤖 Initializing OpenAI service asynchronously...");
                openAIService = OpenAIService.getInstance();
                openAIService.init(this);
                Log.d(TAG, "✅ OpenAI service initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error initializing OpenAI service", e);
            }
        });
    }

    /**
     * ADDED: Async initialization for Responses API services to prevent ANR
     */
    private void initializeResponsesAPIServicesAsync() {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "📡 Initializing Responses API services asynchronously...");
                integrationHelper = ResponsesAPIIntegrationHelper.getInstance(this);
                responsesManager = ChessMasterResponseManager.getInstance(this);
                Log.d(TAG, "✅ Responses API services initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error initializing Responses API services", e);
                // Initialize fallback services on UI thread if needed
                mainHandler.post(() -> {
                    Toast.makeText(this, "Some AI features may be limited", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void initializeLiveMonitoring() {
        try {
            liveMonitorClient = LiveMonitorClient.getInstance(this);
            
            // Try to connect to the configurator (if it's running)
            // Users can configure the IP address in settings
            SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
            String serverIp = prefs.getString("live_monitor_server_ip", "192.168.0.237"); // Default to your IP
            String serverUrl = "ws://" + serverIp + ":8080";
            
            liveMonitorClient.connect(serverUrl);
            
            Log.i(TAG, "🔗 Live monitoring client initialized and attempting connection");
            
            // Send initial system log
            liveMonitorClient.sendSystemLog("MainActivity initialized", "INFO");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize live monitoring: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_spectator_mode) {
            launchSpectatorMode();
            return true;

        } else if (itemId == R.id.action_save_game) {
            // Implement save game functionality
            Toast.makeText(this, "Save game feature coming soon!", Toast.LENGTH_SHORT).show();
            return true;

        } else if (itemId == R.id.action_load_game) {
            // Launch saved games activity
            Intent intent = new Intent(this, SavedGamesActivity.class);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_game_analysis) {
            openAnalysisScreen();
            return true;

        } else if (itemId == R.id.action_tournament_simulation) {
            Log.d(TAG, "🏆 Opening tournament simulation");
            WorkingMastersTournamentDemo.showTournamentSelectionDialog(this);
            return true;

        } else if (itemId == R.id.action_coach_conversation) {
            // Launch coach conversation
            Intent intent = new Intent(this, ChessConversationActivity.class);
            intent.putExtra("FEN", chessBoardView.getCurrentFEN());
            intent.putStringArrayListExtra("MOVE_HISTORY",
                    new ArrayList<>(GameHistoryManager.getInstance().getCurrentGameMoves()));
            intent.putExtra("PLAYER_COLOR", configuredPlayerColor);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_chess_master_selection) {
            Intent intent = new Intent(this, ChessMasterSelectionActivity.class);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_user_profile) {
            Log.d(TAG, "👤 Opening user profile");
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_about) {
            showAboutDialog();
            return true;

        } else if (itemId == R.id.action_chess_set_selection) {
            Log.d(TAG, "🎨 Opening chess set selection");
            Intent intent = new Intent(this, ChessSetSelectionActivity.class);
            startActivityForResult(intent, REQUEST_CHESS_SET_SELECTION);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Add this helper method for the about dialog
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About ChessPedagogue")
                .setMessage("ChessPedagogue - Revolutionary AI Chess Learning\n\n" +
                        "Features:\n" +
                        "• Play against legendary chess masters\n" +
                        "• AI-powered coaching and analysis\n" +
                        "• Spectator mode with live commentary\n" +
                        "• Voice interaction and personality engine\n\n" +
                        "Created with passion for chess education.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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
                    String baseMove = algebraicNotation(fromRow, fromCol) + algebraicNotation(row, col);

                    boolean isPlayerWhite = "white".equals(configuredPlayerColor);
                    boolean isPlayersTurn = (isPlayerWhite && chessBoardView.isWhiteTurn()) ||
                            (!isPlayerWhite && !chessBoardView.isWhiteTurn());

                    // Check for pawn promotion
                    if (isPawnPromotion(fromRow, fromCol, row)) {
                        Log.d(TAG, "♟️ PAWN PROMOTION DETECTED: " + baseMove);
                        showPromotionDialog(baseMove, fromRow, fromCol, row, col, isPlayersTurn);
                        return;
                    }

                    String move = baseMove;
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

    // Add this comprehensive debugging method to MainActivity.java
    private void deepDatabaseDebugging() {
        Log.d("MainActivity", "🔬 DEEP DATABASE ANALYSIS STARTING...");

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);

        // First, let's see what's actually in the database
        Map<String, Integer> stats = dbHelper.getDatabaseStats();
        Log.d("MainActivity", "📊 Overall stats: " + stats.toString());

        // Test multiple name variations
        String[] nameVariations = {
                "tal",
                "Tal",
                "mikhail tal",
                "Mikhail Tal",
                "MIKHAIL TAL"
        };

        for (String name : nameVariations) {
            boolean hasData = dbHelper.hasMasterData(name);
            Log.d("MainActivity", "🎭 Name '" + name + "' has data: " + hasData);
        }

        // Test the exact starting position
        String startingFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        for (String name : nameVariations) {
            List<GameDatabaseHelper.HistoricalPosition> results =
                    dbHelper.findSimilarPositions(startingFEN, name, 3);
            Log.d("MainActivity", "🎯 Starting position results for '" + name + "': " + results.size());

            if (results.size() > 0) {
                Log.d("MainActivity", "   ✅ FOUND RESULTS! First result: " + results.get(0).toString());
            }
        }

        // Let's also check what the database actually contains by doing a raw query
        testRawDatabaseQuery(dbHelper);
    }

    // Add this helper method too
    private void testRawDatabaseQuery(GameDatabaseHelper dbHelper) {
        Log.d("MainActivity", "📋 TESTING RAW DATABASE CONTENTS...");

        try {
            // This is a bit of a hack, but let's see what's actually in there
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Check if the table exists and has data
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM master_positions", null);
            if (cursor.moveToFirst()) {
                int totalRows = cursor.getInt(0);
                Log.d("MainActivity", "📊 Total rows in master_positions table: " + totalRows);
            }
            cursor.close();

            // Get some sample data to see the actual format
            Cursor sampleCursor = db.rawQuery("SELECT master_name, fen, annotation FROM master_positions LIMIT 5", null);

            Log.d("MainActivity", "📋 Sample database contents:");
            while (sampleCursor.moveToNext()) {
                String masterName = sampleCursor.getString(0);
                String fen = sampleCursor.getString(1);
                String annotation = sampleCursor.getString(2);

                Log.d("MainActivity", "   📝 Master: '" + masterName + "'");
                Log.d("MainActivity", "   📝 FEN: " + fen.substring(0, Math.min(30, fen.length())) + "...");
                Log.d("MainActivity", "   📝 Annotation: " + annotation.substring(0, Math.min(50, annotation.length())) + "...");
                Log.d("MainActivity", "   ---");
            }
            sampleCursor.close();

        } catch (Exception e) {
            Log.e("MainActivity", "❌ Raw database query failed: " + e.getMessage());
        }
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
                Log.e(TAG, "❌ RECORD_AUDIO permission not granted!");
                Toast.makeText(this, "Microphone permission required for voice input", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.RECORD_AUDIO}, 
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
                return;
            }
            Log.d(TAG, "✅ RECORD_AUDIO permission granted, starting recording...");
            recordService.startRecording();
        } else {
            Log.e(TAG, "❌ Voice service not ready - isServiceBound: " + isServiceBound + ", recordService: " + (recordService != null ? "not null" : "null"));
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
        
        // Get current selected master for personality-appropriate challenge generation
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        String selectedMaster = prefs.getString("selected_master", "tal");

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

        // NEW: Use Responses API for enhanced challenge generation with master personality
        boolean useResponsesAPI = integrationHelper.shouldUseResponsesAPI(selectedMaster);
        
        if (useResponsesAPI) {
            Log.d(TAG, "🚀 Generating challenge with Responses API: " + selectedMaster);
            generateChallengeWithResponsesAPI(selectedMaster, prompt, currentFEN);
        } else {
            Log.d(TAG, "⚠️ Fallback to Chat Completions for challenge: " + selectedMaster);
            generateChallengeWithChatCompletions(prompt);
        }
    }
    
    /**
     * NEW: Generate challenge using Responses API with master personality
     */
    private void generateChallengeWithResponsesAPI(String masterName, String prompt, String currentFEN) {
        executorService.execute(() -> {
            try {
                // Build game context for the challenge
                String gameContext = "POSITION: " + currentFEN + "\nCONTEXT: challenge_generation";
                
                // Create a session for challenge generation
                responsesManager.createResponseSession(masterName, "challenge_generation",
                    new ChessMasterResponseManager.ResponseCallback() {
                        @Override
                        public void onResponseStart(String sessionId) {
                            Log.d(TAG, "✅ Challenge session created: " + sessionId);
                            // Send the challenge prompt
                            responsesManager.sendMessage(sessionId, prompt, gameContext,
                                new ChessMasterResponseManager.ResponseCallback() {
                                    private StringBuilder challengeResponse = new StringBuilder();
                                    
                                    @Override
                                    public void onResponseStart(String sessionId) {
                                        Log.d(TAG, "📡 Challenge generation started");
                                    }
                                    
                                    @Override
                                    public void onResponseChunk(String chunk, boolean isFirst) {
                                        challengeResponse.append(chunk);
                                    }
                                    
                                    @Override
                                    public void onResponseComplete(String fullResponse) {
                                        String finalChallenge = !challengeResponse.toString().trim().isEmpty() 
                                            ? challengeResponse.toString().trim() 
                                            : fullResponse;
                                            
                                        if (finalChallenge != null && !finalChallenge.trim().isEmpty()) {
                                            Log.d(TAG, "✅ Responses API challenge generated");
                                            mainHandler.post(() -> {
                                                showChallengeLoading(false);
                                                parseChallengeResponse(finalChallenge);
                                            });
                                        } else {
                                            Log.w(TAG, "⚠️ Empty challenge from Responses API, falling back");
                                            generateChallengeWithChatCompletions(prompt);
                                        }
                                    }
                                    
                                    @Override
                                    public void onConversationTurn(String speaker, String message) {
                                        // Not needed for challenge generation
                                    }
                                    
                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "❌ Challenge generation error: " + error);
                                        generateChallengeWithChatCompletions(prompt);
                                    }
                                });
                        }
                        
                        @Override
                        public void onResponseChunk(String chunk, boolean isFirst) {
                            // Not used for session creation
                        }
                        
                        @Override
                        public void onResponseComplete(String fullResponse) {
                            // Not used for session creation
                        }
                        
                        @Override
                        public void onConversationTurn(String speaker, String message) {
                            // Not used for session creation
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Challenge session creation failed: " + error);
                            generateChallengeWithChatCompletions(prompt);
                        }
                    });
                    
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in Responses API challenge generation", e);
                generateChallengeWithChatCompletions(prompt);
            }
        });
    }
    
    /**
     * LEGACY: Generate challenge using Chat Completions (fallback)
     */
    private void generateChallengeWithChatCompletions(String prompt) {
        executorService.execute(() -> {
            try {
                // Get the selected master for system message
                SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
                String selectedMaster = prefs.getString("selected_master", "tal");
                String masterName = getMasterDisplayName(selectedMaster);
                
                // Get challenge from OpenAI (synchronous call)
                String response = openAIService.getChatCompletion(
                        "You are " + masterName + ", a chess grandmaster creating engaging, instructive tactical puzzles.",
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
    
    /**
     * NEW: Get display name for selected master
     */
    private String getMasterDisplayName(String selectedMaster) {
        switch (selectedMaster.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "fischer": return "Bobby Fischer";
            case "carlsen": return "Magnus Carlsen";
            case "kasparov": return "Garry Kasparov";
            case "karpov": return "Anatoly Karpov";
            case "kramnik": return "Vladimir Kramnik";
            case "anand": return "Viswanathan Anand";
            case "alekhine": return "Alexander Alekhine";
            case "capablanca": return "José Raúl Capablanca";
            case "lasker": return "Emanuel Lasker";
            case "morphy": return "Paul Morphy";
            case "botvinnik": return "Mikhail Botvinnik";
            default: return "Coach Tal";
        }
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
            new Handler().postDelayed(() -> {
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
                    
                    // NEW: Send live monitoring update for conversation
                    sendLiveConversationUpdate("Coach", text, "response");
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
     * NEW: Toggle automatic commentary on/off
     */
    private void toggleAutoCommentary() {
        // Get current state (you could store this in SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        boolean currentlyEnabled = prefs.getBoolean("auto_commentary_enabled", true);

        // Toggle the state
        boolean newState = !currentlyEnabled;
        prefs.edit().putBoolean("auto_commentary_enabled", newState).apply();

        // Update the ViewModel
        gameViewModel.configureAutoCommentary(newState);

        // Show feedback
        String message = newState ? "Auto-commentary enabled" : "Auto-commentary disabled";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "🎙️ Auto-commentary toggled: " + (newState ? "ON" : "OFF"));
    }
    /**
     * Set up click listeners for all buttons
     */

    // Replace your setupSpeakButton method with this:
    private void setupSpeakButton() {

        FloatingActionButton speakButton = findViewById(R.id.speakButton);
        if (speakButton != null) {
            speakButton.setOnClickListener(v -> {
                Log.d(TAG, "Speak button clicked");

                // FIXED: Handle case where TTS service might not be initialized yet
                try {
                    OpenAITTSService tts = TTSServiceManager.getOpenAITTSService(this);
                    
                    // Set context for main game screen (ultra-fast eleven_flash_v2_5)
                    TTSServiceManager.setUsageContext(this, "main_game");
                    
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
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error in speak button handler", e);
                    Toast.makeText(this, "Voice services starting up... Please try again in a moment", Toast.LENGTH_SHORT).show();
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

            competitiveModeButton = findViewById(R.id.competitiveModeButton);
            Log.d(TAG, "competitiveModeButton: " + (competitiveModeButton != null ? "✅ Found" : "❌ NULL"));

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
            
            // Initialize Voice Status Indicator
            initializeVoiceStatusIndicator();

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
        TTSServiceManager.getOpenAITTSService(this).speak(message);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CHESS_SET_SELECTION) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "🎨 Chess set selection completed successfully");
                // Clear the piece cache to force redraw with new chess set
                if (chessBoardView != null) {
                    chessBoardView.clearPieceCache();
                }
                Toast.makeText(this, "Chess set updated! ♛", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "🎨 Chess set selection cancelled");
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

        // 🎤 Cleanup voice control manager
        if (voiceControlManager != null) {
            voiceControlManager.cleanup();
            Log.d(TAG, "🎤 Voice control manager cleaned up");
        }

        super.onDestroy();
    }

    // Add this enum for tracking processing states
    private enum ProcessingState {
        IDLE, LISTENING, TRANSCRIBING, THINKING, SPEAKING
    }
    
    /**
     * Show a subtle loading indicator for database operations
     */
    private void showDatabaseLoadingIndicator(boolean show) {
        // Use the existing status text view for now
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (statusTextView != null) {
            if (show) {
                statusTextView.setText("Loading chess masters...");
                statusTextView.setVisibility(View.VISIBLE);
            } else {
                statusTextView.setText("Ready to play");
            }
        }
        
        // You could also add a progress bar if you have one in your layout
        ProgressBar progressBar = findViewById(R.id.evaluationProgressBar);
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * Update status message during initialization
     */
    private void updateStatusMessage(String message) {
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (statusTextView != null) {
            statusTextView.setText(message);
        }
    }


    // ==================== Always-Listening Voice Control Manager ====================

    /**
     * 🎤 Initialize always-listening voice control manager
     */
    private void initializeVoiceControlManager() {
        try {
            Log.d(TAG, "🎤 Initializing always-listening voice control manager");
            
            // Get VoiceControlManager instance
            voiceControlManager = VoiceControlManager.getInstance(this);
            
            // Register this activity as a voice command listener
            voiceControlManager.registerVoiceCommandListener(this);
            
            // Start always-listening if enabled in settings
            SharedPreferences prefs = getSharedPreferences("VoiceControlPrefs", Context.MODE_PRIVATE);
            boolean alwaysListeningEnabled = prefs.getBoolean("always_listening_enabled", false);
            
            if (alwaysListeningEnabled) {
                Log.d(TAG, "🎤 Always-listening enabled - starting service");
                voiceControlManager.startAlwaysListening();
            } else {
                Log.d(TAG, "🎤 Always-listening disabled - available on demand");
            }
            
            Log.d(TAG, "✅ Voice control manager initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing voice control manager", e);
        }
    }

    // ==================== VoiceCommandListener Interface Implementation ====================

    @Override
    public void onWakeWordDetected(String wakeWord) {
        Log.d(TAG, "🎯 Wake word detected: " + wakeWord);
        
        // Show visual feedback
        runOnUiThread(() -> {
            Toast.makeText(this, "🎤 " + wakeWord + " detected", Toast.LENGTH_SHORT).show();
            
            // Start visual listening indicator
            updateUIState(ProcessingState.LISTENING);
        });
    }

    @Override
    public void onVoiceCommand(String command) {
        Log.d(TAG, "🗣️ Voice command received: " + command);
        
        runOnUiThread(() -> {
            if (command.startsWith("start_voice_recording")) {
                // Start voice recording for main game
                Log.d(TAG, "🎤 Starting voice recording from wake word");
                startVoiceRecording();
                
            } else if (command.startsWith("switch_master:")) {
                // Switch to specific master and start voice
                String masterName = command.substring("switch_master:".length()).split(";")[0];
                Log.d(TAG, "🎭 Switching to master: " + masterName);
                
                // Switch master
                FineTunedModelManager.getInstance(this).setSelectedChessMaster(masterName);
                updateVoiceForCurrentMaster();
                
                // Show feedback
                String displayName = FineTunedModelManager.getInstance(this).getMasterDisplayName(masterName);
                Toast.makeText(this, "🎭 Switched to " + displayName, Toast.LENGTH_SHORT).show();
                
                // Start voice recording if commanded
                if (command.contains("start_voice")) {
                    startVoiceRecording();
                }
                
            } else if (command.startsWith("voice_input:")) {
                // Process direct voice input
                String voiceText = command.substring("voice_input:".length());
                Log.d(TAG, "🎤 Processing voice input: " + voiceText);
                
                // Process voice input directly (transcription popup removed)
                updateUIState(ProcessingState.THINKING, voiceText);
                
                // 🎯 TRIGGER COACH RESPONSE: Connect STT to coach response system
                triggerCoachResponseFromVoiceInput(voiceText);
                
            } else if (command.equals("show_voice_options")) {
                // Show voice options in menu context
                showVoiceOptionsDialog();
                
            } else {
                Log.w(TAG, "⚠️ Unknown voice command: " + command);
            }
        });
    }

    @Override
    public void onVoiceError(String error) {
        Log.e(TAG, "❌ Voice control error: " + error);
        
        runOnUiThread(() -> {
            Toast.makeText(this, "Voice error: " + error, Toast.LENGTH_SHORT).show();
            updateUIState(ProcessingState.IDLE);
        });
    }

    @Override
    public String getActivityType() {
        return "main_game";
    }
    
    @Override
    public void onVoiceStatusChanged(AlwaysListeningService.VoiceStatus status) {
        Log.d(TAG, "🚦 Voice status changed: " + status);
        
        runOnUiThread(() -> {
            if (voiceStatusIndicator != null) {
                voiceStatusIndicator.updateStatus(status);
            }
        });
    }
    
    /**
     * 🚦 Initialize Voice Status Indicator and add to UI
     */
    private void initializeVoiceStatusIndicator() {
        try {
            voiceStatusIndicator = new VoiceStatusIndicator(this);
            
            // Add to the main layout in top-right corner
            FrameLayout mainLayout = findViewById(android.R.id.content);
            if (mainLayout != null) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
                params.setMargins(0, 100, 16, 0); // Top margin to avoid status bar
                
                mainLayout.addView(voiceStatusIndicator, params);
                Log.d(TAG, "✅ Voice status indicator added to UI");
            } else {
                Log.e(TAG, "❌ Could not find main layout for voice status indicator");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing voice status indicator", e);
        }
    }

    /**
     * 🎤 Show voice options dialog for wake word configuration
     */
    private void showVoiceOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎤 Voice Control Options");
        
        // Check current always-listening state
        boolean alwaysListeningEnabled = voiceControlManager != null && voiceControlManager.isAlwaysListeningEnabled();
        
        String message = "Always-listening: " + (alwaysListeningEnabled ? "ON" : "OFF") + "\n\n" +
                        "Wake words:\n" +
                        "• 'Hey Coach' - Start voice recording\n" +
                        "• 'Hey [Master]' - Switch master and start voice\n" +
                        "• Long press speak button - Toggle always-listening";
        
        builder.setMessage(message);
        
        builder.setPositiveButton("Toggle Always-Listening", (dialog, which) -> {
            if (voiceControlManager != null) {
                voiceControlManager.toggleAlwaysListening();
            }
        });
        
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        
        builder.show();
    }

    // ==================== Activity Lifecycle for Voice Control ====================

    @Override
    protected void onPause() {
        super.onPause();
        
        // Unregister from voice control manager but keep service running
        if (voiceControlManager != null) {
            voiceControlManager.unregisterVoiceCommandListener();
            Log.d(TAG, "🎤 Unregistered from voice control manager");
        }
    }
    
    /**
     * 🎯 Trigger coach response from already-transcribed voice input
     */
    private void triggerCoachResponseFromVoiceInput(String voiceText) {
        Log.d(TAG, "🎤 Triggering coach response for: " + voiceText);
        
        if (recordService != null && isServiceBound) {
            try {
                // Create a simple game context for the coach
                String gameContext = "Current position: " + gameViewModel.getCurrentFEN().getValue() + 
                                   "\nUser question: " + voiceText;
                
                // Get the current master
                String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
                Log.d(TAG, "🎭 Current master: " + currentMaster);
                
                // Create a ThreeStageResponseManager instance and process the voice input
                ThreeStageResponseManager threeStageManager = ThreeStageResponseManager.getInstance(this);
                threeStageManager.processThreeStageResponse(voiceText, gameContext,
                        new ThreeStageResponseManager.ThreeStageCallback() {
                            @Override
                            public void onStageResponse(ThreeStageResponseManager.ResponseStage stage, String response, boolean isFinal) {
                                Log.d(TAG, "✨ " + stage.getDisplayName() + " response: " + response.substring(0, Math.min(50, response.length())) + "...");
                                
                                // Update UI to show coach is responding
                                runOnUiThread(() -> {
                                    updateUIState(ProcessingState.THINKING, "Coach is thinking...");
                                    // Update voice status to show processing (red light)
                                    if (voiceControlManager != null) {
                                        voiceControlManager.updateVoiceStatus(AlwaysListeningService.VoiceStatus.PROCESSING_SPEECH);
                                    }
                                });
                                
                                if (isFinal) {
                                    // Final response - trigger TTS and update UI
                                    runOnUiThread(() -> {
                                        updateUIState(ProcessingState.SPEAKING, response);
                                        // Voice status returns to listening while coach speaks
                                        if (voiceControlManager != null) {
                                            voiceControlManager.updateVoiceStatus(AlwaysListeningService.VoiceStatus.LISTENING_FOR_INITIATION);
                                        }
                                        // Trigger TTS for the coach response
                                        ChessCoachManager.getInstance(MainActivity.this).sendMessage(response, new ChessCoachManager.ChessCoachCallback() {
                                            @Override
                                            public void onResponseReceived(String response) {
                                                Log.d(TAG, "🗣️ Coach response received: " + response.substring(0, Math.min(50, response.length())));
                                            }
                                            
                                            @Override
                                            public void onError(String error) {
                                                Log.e(TAG, "🗣️ Coach speech error: " + error);
                                            }
                                            
                                            @Override
                                            public void onSpeechCompleted() {
                                                Log.d(TAG, "🗣️ Coach finished speaking");
                                                // Coach is done speaking - ready for next input
                                            }
                                        });
                                    });
                                }
                            }
                            
                            @Override
                            public void onStageError(ThreeStageResponseManager.ResponseStage stage, String error) {
                                Log.e(TAG, "❌ Stage " + stage.getDisplayName() + " error: " + error);
                                runOnUiThread(() -> {
                                    updateUIState(ProcessingState.IDLE);
                                    // Reset voice status to listening on error
                                    if (voiceControlManager != null) {
                                        voiceControlManager.updateVoiceStatus(AlwaysListeningService.VoiceStatus.LISTENING_FOR_INITIATION);
                                    }
                                    Toast.makeText(MainActivity.this, "Error getting coach response: " + error, Toast.LENGTH_SHORT).show();
                                });
                            }
                            
                            @Override
                            public void onAllStagesComplete(String finalResponse) {
                                Log.d(TAG, "🏆 All stages complete: " + finalResponse.substring(0, Math.min(100, finalResponse.length())) + "...");
                                runOnUiThread(() -> {
                                    updateUIState(ProcessingState.IDLE);
                                    // Voice status should already be LISTENING_FOR_INITIATION from the final response
                                });
                            }
                        });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error triggering coach response", e);
                runOnUiThread(() -> {
                    updateUIState(ProcessingState.IDLE);
                    // Reset voice status to listening on exception
                    if (voiceControlManager != null) {
                        voiceControlManager.updateVoiceStatus(AlwaysListeningService.VoiceStatus.LISTENING_FOR_INITIATION);
                    }
                    Toast.makeText(this, "Error connecting to coach", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            Log.e(TAG, "❌ Record service not available for voice input processing");
            Toast.makeText(this, "Voice service not ready", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== COMPETITIVE MODE IMPLEMENTATION ====================
    
    /**
     * 🏆 Launch competitive mode - play against a chess master with full emotional intelligence
     */
    private void launchCompetitiveMode() {
        Log.d(TAG, "🏆 Launching competitive mode!");
        
        // Create competitive mode selection dialog
        String[] masters = {
            "Tal - The Magician of Riga", 
            "Fischer - The American Chess Legend",
            "Carlsen - The Modern Chess Machine",
            "Kasparov - The Beast from Baku",
            "Karpov - The Python",
            "Kramnik - The Stone Wall",
            "Alekhine - The Attacking Genius",
            "Capablanca - The Chess Machine",
            "Morphy - The Pride and Sorrow",
            "Lasker - The Fighting Machine",
            "Anand - The Lightning Kid",
            "Botvinnik - The Patriarch"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🏆 Choose Your Opponent")
               .setItems(masters, (dialog, which) -> {
                   String selectedMaster = masters[which].split(" - ")[0].toLowerCase();
                   Log.d(TAG, "🎯 Selected master: " + selectedMaster);
                   startCompetitiveGame(selectedMaster);
               })
               .setNegativeButton("Cancel", null);
        
        builder.create().show();
    }
    
    /**
     * 🔥 Toggle competitive mode button highlight (red when active)
     */
    private void toggleCompetitiveModeHighlight() {
        if (competitiveModeButton != null) {
            // Get current background tint
            boolean isHighlighted = competitiveModeButton.getTag() != null && 
                                   competitiveModeButton.getTag().equals("highlighted");
            
            if (isHighlighted) {
                // Remove highlight - back to normal
                competitiveModeButton.setBackgroundTintList(getColorStateList(R.color.chess_light_square));
                competitiveModeButton.setTag(null);
                Log.d(TAG, "🔥 Competitive mode highlight REMOVED");
                Toast.makeText(this, "Competitive mode highlight OFF", Toast.LENGTH_SHORT).show();
            } else {
                // Add bright red highlight
                competitiveModeButton.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light));
                competitiveModeButton.setTag("highlighted");
                Log.d(TAG, "🔥 Competitive mode highlight ACTIVATED");
                Toast.makeText(this, "Competitive mode highlight ON - Ready to battle!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 🔄 Synchronize master selection across SharedPreferences stores on app startup
     * This fixes the common issue where voice system reads from a different SharedPreferences than other parts
     */
    private void synchronizeMasterSelectionOnStartup() {
        try {
            Log.d(TAG, "🔄 Synchronizing master selection on startup...");
            
            // Read from both SharedPreferences stores
            SharedPreferences chessAppPrefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
            SharedPreferences fineTunedPrefs = getSharedPreferences("ChessFineTunedModels", MODE_PRIVATE);
            
            String appMaster = chessAppPrefs.getString("selected_master", null);
            String voiceMaster = fineTunedPrefs.getString("selected_master", null);
            String fineTunedMaster = null;
            
            // Get FineTunedModelManager master (if available)
            try {
                fineTunedMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
            } catch (Exception e) {
                Log.w(TAG, "Could not get FineTunedModelManager master: " + e.getMessage());
            }
            
            Log.d(TAG, "📋 Current master selections:");
            Log.d(TAG, "  - ChessAppPrefs: " + appMaster);
            Log.d(TAG, "  - ChessFineTunedModels: " + voiceMaster);
            Log.d(TAG, "  - FineTunedModelManager: " + fineTunedMaster);
            
            // Determine which master to use (priority: FineTunedModelManager > ChessAppPrefs > default)
            String masterToUse = null;
            if (fineTunedMaster != null && !fineTunedMaster.isEmpty()) {
                masterToUse = fineTunedMaster.toLowerCase();
            } else if (appMaster != null && !appMaster.isEmpty()) {
                masterToUse = appMaster.toLowerCase();
            } else if (voiceMaster != null && !voiceMaster.isEmpty()) {
                masterToUse = voiceMaster.toLowerCase();
            } else {
                masterToUse = "tal"; // Default
            }
            
            Log.d(TAG, "🎯 Using master: " + masterToUse);
            
            // Synchronize ALL stores to use the same master
            chessAppPrefs.edit().putString("selected_master", masterToUse).apply();
            fineTunedPrefs.edit().putString("selected_master", masterToUse).apply();
            
            if (fineTunedMaster == null || !fineTunedMaster.toLowerCase().equals(masterToUse)) {
                try {
                    FineTunedModelManager.getInstance(this).setSelectedChessMaster(masterToUse);
                } catch (Exception e) {
                    Log.w(TAG, "Could not set FineTunedModelManager master: " + e.getMessage());
                }
            }
            
            Log.d(TAG, "✅ Master selection synchronized to: " + masterToUse);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to synchronize master selection", e);
        }
    }

    /**
     * 🎯 Start competitive game against selected master
     */
    private void startCompetitiveGame(String masterName) {
        Log.d(TAG, "🏁 Starting competitive game vs " + masterName);
        
        try {
            // Create intent for competitive mode activity
            Intent competitiveIntent = new Intent(this, CompetitiveModeActivity.class);
            competitiveIntent.putExtra("selectedMaster", masterName);
            competitiveIntent.putExtra("enableEmotionalIntelligence", true);
            competitiveIntent.putExtra("enableEmergentBehavior", true);
            competitiveIntent.putExtra("enableAdaptiveLearning", true);
            competitiveIntent.putExtra("enableResponsesAPI", true);
            
            // Pass current game configuration
            competitiveIntent.putExtra("playerColor", configuredPlayerColor);
            competitiveIntent.putExtra("skillLevel", configuredSkillLevel);
            competitiveIntent.putExtra("engineElo", configuredEngineElo);
            
            Log.d(TAG, "🚀 Launching CompetitiveModeActivity with master: " + masterName);
            startActivity(competitiveIntent);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error launching competitive mode", e);
            Toast.makeText(this, "Error launching competitive mode: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ♟️ Check if a move is a pawn promotion
     */
    private boolean isPawnPromotion(int fromRow, int fromCol, int toRow) {
        try {
            char piece = chessBoardView.getPieceAt(fromRow, fromCol);
            boolean isPawn = (piece == 'P' || piece == 'p');
            
            if (!isPawn) return false;
            
            // White pawn reaching rank 8 (row 0) or black pawn reaching rank 1 (row 7)
            boolean reachesPromotionRank = (Character.isUpperCase(piece) && toRow == 0) || 
                                          (Character.isLowerCase(piece) && toRow == 7);
            
            Log.d(TAG, "♟️ Promotion check: piece=" + piece + ", fromRow=" + fromRow + ", toRow=" + toRow + ", reaches=" + reachesPromotionRank);
            return reachesPromotionRank;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking pawn promotion", e);
            return false;
        }
    }

    /**
     * 👑 Show pawn promotion dialog
     */
    private void showPromotionDialog(String baseMove, int fromRow, int fromCol, int toRow, int toCol, boolean isPlayersTurn) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("♟️ Promote Pawn");
            builder.setMessage("Choose piece to promote to:");
            builder.setCancelable(false);
            
            String[] promotionPieces = {"👑 Queen", "🏰 Rook", "⛪ Bishop", "🐴 Knight"};
            String[] promotionCodes = {"q", "r", "b", "n"};
            
            builder.setItems(promotionPieces, (dialog, which) -> {
                String promotionMove = baseMove + promotionCodes[which];
                Log.d(TAG, "♟️ PAWN PROMOTION: " + promotionMove);
                
                // Proceed with the promotion move
                makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
            });
            
            builder.show();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error showing promotion dialog", e);
            // Fallback to queen promotion
            String promotionMove = baseMove + "q";
            makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
        }
    }

    /**
     * ♛ Execute the pawn promotion move
     */
    private void makePromotionMove(String promotionMove, int fromRow, int fromCol, int toRow, int toCol, boolean isPlayersTurn) {
        try {
            Log.d(TAG, "🎯 ATTEMPTING PROMOTION MOVE: " + promotionMove);
            Log.d(TAG, "📝 Player color: " + configuredPlayerColor);
            Log.d(TAG, "🔄 Is player's turn: " + isPlayersTurn);
            Log.d(TAG, "🔄 Is white's turn: " + chessBoardView.isWhiteTurn());

            if (!isPlayersTurn) {
                Log.d(TAG, "❌ Not player's turn, ignoring promotion move");
                return;
            }

            // Reset selection
            chessBoardView.setSelectedSquare(-1, -1);

            // Make the promotion move
            gameViewModel.makePlayerMove(promotionMove);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error making promotion move", e);
        }
    }
}