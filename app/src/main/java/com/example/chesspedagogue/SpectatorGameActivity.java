package com.example.chesspedagogue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 🎭 SIMPLIFIED SPECTATOR MODE - AI Masters Talking to Each Other!
 * Focused on AI dialogue instead of commentary
 */
public class SpectatorGameActivity extends AppCompatActivity implements VoiceControlManager.VoiceCommandListener {
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
    private Button ttsToggleButton;
    private TextView currentSpeakerTextView;
    private Button userCommentButton;
    private Button startGameButton;

    // Game state
    private SpectatorGameViewModel viewModel;
    private String whitePlayer;
    private String blackPlayer;
    private boolean isPaused = false;
    private boolean gameStarted = false;
    private int gameSpeed = 3000;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    // 🎤 Always-listening voice control
    private VoiceControlManager voiceControlManager;
    
    // Voice status indicator
    private VoiceStatusIndicator voiceStatusIndicator;
    
    // 🎭 Phase 2: Multi-layered emotional complexity integration
    private Phase2EmotionalIntegrationBridge phase2Bridge;
    private MultiLayeredEmotionalManager multiLayeredManager;
    private VoiceEmotionalAnalyzer voiceEmotionalAnalyzer;
    
    // Animation tracking for delayed board updates
    private boolean isAnimationInProgress = false;
    private String pendingFenUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "🎭 Starting simplified spectator mode...");
            setContentView(R.layout.activity_spectator_game);

            // CRITICAL FIX: Set spectator mode flag to prevent duplicate Chat Completions calls
            android.content.SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("is_spectator_mode", true).apply();
            Log.d(TAG, "✅ Spectator mode flag set - EvaluationTracker auto-commentary disabled");

            // Get players from intent with fallbacks
            getPlayersFromIntent();

            // Initialize everything with proper error handling
            if (setupActionBar() &&
                    initializeViews() &&
                    setupObservers() &&
                    setupControls()) {

                Log.d(TAG, "✅ All components initialized successfully!");
                
                // 🎤 Initialize always-listening voice control manager
                initializeVoiceControlManager();
                
                // 🎭 Initialize Phase 2 emotional complexity system
                initializePhase2EmotionalSystem();
                
                // 🎮 IMMEDIATE AUTO-START: Start the game right away
                Log.d(TAG, "🚀 Starting spectator game immediately...");
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
            
            // Initialize Voice Status Indicator
            initializeVoiceStatusIndicator();

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

            // TTS Toggle button setup - CRITICAL for quota management
            ttsToggleButton = findViewById(R.id.ttsToggleButton);
            if (ttsToggleButton != null) {
                ttsToggleButton.setOnClickListener(v -> toggleTTS());
                updateTTSButtonText();
                Log.d(TAG, "✅ TTS toggle button initialized");
            } else {
                Log.w(TAG, "⚠️ TTS toggle button not found in layout");
            }

            // User comment button setup
            userCommentButton = findViewById(R.id.userCommentButton);
            if (userCommentButton != null) {
                userCommentButton.setOnClickListener(v -> handleUserInterruption("dialog"));
                Log.d(TAG, "✅ User comment button initialized");
            } else {
                Log.w(TAG, "⚠️ User comment button not found in layout");
            }

            // Quick voice button setup
            Button quickVoiceButton = findViewById(R.id.quickVoiceButton);
            if (quickVoiceButton != null) {
                quickVoiceButton.setOnClickListener(v -> handleUserInterruption("voice"));
                Log.d(TAG, "✅ Quick voice button initialized");
            } else {
                Log.w(TAG, "⚠️ Quick voice button not found in layout");
            }

            // Quick type button setup
            Button quickTypeButton = findViewById(R.id.quickTypeButton);
            if (quickTypeButton != null) {
                quickTypeButton.setOnClickListener(v -> handleUserInterruption("type"));
                Log.d(TAG, "✅ Quick type button initialized");
            } else {
                Log.w(TAG, "⚠️ Quick type button not found in layout");
            }

            // Start game button setup - CRITICAL FIX
            // Button doesn't exist in layout, so add it dynamically
            Log.d(TAG, "🎯 Adding start game button dynamically");
            addStartButtonDynamically();

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

    /**
     * 🚀 FIXED: Add dynamic start button if not found in layout
     */
    private void addStartButtonDynamically() {
        try {
            Log.d(TAG, "🎯 Adding start button dynamically to controls");
            
            // Find the controls container
            LinearLayout controlsContainer = findViewById(R.id.controlsContainer);
            if (controlsContainer != null) {
                // Create start button
                startGameButton = new Button(this);
                startGameButton.setText("🎬 Start AI Battle");
                startGameButton.setTextSize(14f);
                startGameButton.setTextColor(getColor(android.R.color.white));
                startGameButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark)));
                startGameButton.setOnClickListener(v -> startSpectatorGame());
                
                // Set layout params
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (48 * getResources().getDisplayMetrics().density) // 48dp
                );
                params.setMargins(16, 8, 16, 8);
                
                // Add to controls container
                controlsContainer.addView(startGameButton, 0, params); // Add at top
                
                Log.d(TAG, "✅ Start button added dynamically");
            } else {
                Log.e(TAG, "❌ Could not find controls container for dynamic start button");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error adding start button dynamically", e);
        }
    }
    
    /**
     * 🎮 Update start button text based on game state
     */
    private void updateStartButtonText() {
        if (startGameButton != null) {
            if (gameStarted) {
                startGameButton.setText("🎭 Game Running");
                startGameButton.setEnabled(false);
                startGameButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.darker_gray)));
            } else {
                startGameButton.setText("🎬 Start AI Battle");
                startGameButton.setEnabled(true);
                startGameButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark)));
            }
        }
    }

    private void startSpectatorGame() {
        Log.d(TAG, "🚀 Starting spectator game!");
        
        // Prevent multiple starts
        if (gameStarted) {
            Log.d(TAG, "⚠️ Game already started, ignoring duplicate start request");
            return;
        }
        
        gameStarted = true;
        updateStartButtonText();

        try {
            if (viewModel != null) {
                // Show initial dialogue
                String openingDialogue = String.format(
                        "Welcome to this epic battle! %s and %s are about to begin their game.",
                        FineTunedModelManager.getInstance(this).getMasterDisplayName(whitePlayer),
                        FineTunedModelManager.getInstance(this).getMasterDisplayName(blackPlayer)
                );

                displayAIDialogue(openingDialogue);

                // Initialize Phase 2 emotional layers for both masters
                if (phase2Bridge != null) {
                    phase2Bridge.initializeSpectatorMode(whitePlayer, blackPlayer);
                    Log.d(TAG, "🎭 Phase 2 emotional layers initialized for " + whitePlayer + " vs " + blackPlayer);
                    
                    // 🧪 Test Phase 2 integration
                    try {
                        Phase2EmotionalDemo demo = new Phase2EmotionalDemo(this);
                        boolean testPassed = demo.testPhase2Integration(whitePlayer, blackPlayer);
                        Log.d(TAG, "🧪 Phase 2 integration test result: " + (testPassed ? "PASSED" : "FAILED"));
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error running Phase 2 integration test", e);
                    }
                }
                
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
     * 🛑 Handle user interruption with immediate AI reactions
     */
    private void handleUserInterruption(String interactionType) {
        Log.d(TAG, "🛑 User interrupting AI conversation - type: " + interactionType);
        
        try {
            // IMMEDIATE STOP - Stop all AI activities instantly
            SpectatorConversationOrchestrator orchestrator = SpectatorConversationOrchestrator.getInstance(this);
            if (orchestrator != null) {
                orchestrator.setUserRecordingInProgress(true); // This stops TTS and pauses conversations
                Log.d(TAG, "✅ AI conversations stopped immediately");
            }
            
            // Stop TTS immediately
            OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(this);
            if (ttsService != null) {
                ttsService.stopSpeaking();
                Log.d(TAG, "✅ TTS stopped immediately");
            }
            
            // Generate master-specific interruption reactions
            generateInterruptionReaction();
            
            // Handle the specific interaction type
            switch (interactionType) {
                case "voice":
                    startVoiceComment();
                    break;
                case "type":
                    showTextCommentDialog();
                    break;
                case "dialog":
                default:
                    showUserCommentDialog();
                    break;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error handling user interruption", e);
            // Fallback to original dialog
            showUserCommentDialog();
        }
    }
    
    /**
     * 🎭 Generate master-specific reactions to user interruption
     */
    private void generateInterruptionReaction() {
        try {
            // Get current speaker info
            SpectatorConversationOrchestrator orchestrator = SpectatorConversationOrchestrator.getInstance(this);
            String currentSpeaker = orchestrator != null ? orchestrator.getCurrentSpeaker() : null;
            String otherPlayer = currentSpeaker != null ? 
                (currentSpeaker.equals(whitePlayer) ? blackPlayer : whitePlayer) : null;
            
            Log.d(TAG, "🎭 Generating interruption reaction for " + currentSpeaker);
            
            // Master-specific interruption personalities
            String interruptionReaction = getInterruptionReaction(currentSpeaker);
            String otherPlayerReaction = getOtherPlayerReaction(otherPlayer, currentSpeaker);
            
            // Display the interruption reaction briefly
            if (interruptionReaction != null) {
                String speakerDisplay = "💬 " + FineTunedModelManager.getInstance(this).getMasterDisplayName(currentSpeaker);
                displayAIDialogue(speakerDisplay + "\n\n" + interruptionReaction);
                
                // Show other player's reaction after a brief delay
                if (otherPlayerReaction != null) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        String otherDisplay = "💬 " + FineTunedModelManager.getInstance(this).getMasterDisplayName(otherPlayer);
                        displayAIDialogue(otherDisplay + "\n\n" + otherPlayerReaction);
                    }, 2000);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error generating interruption reaction", e);
        }
    }
    
    /**
     * 🎭 Get master-specific reaction to being interrupted
     */
    private String getInterruptionReaction(String masterName) {
        if (masterName == null) return null;
        
        switch (masterName.toLowerCase()) {
            case "fischer":
                return "Hold on... what? *slightly annoyed* You're interrupting my analysis. This better be important.";
            
            case "tal":
                return "*chuckles warmly* Ah, a spectator wants to join our conversation! How delightful!";
            
            case "carlsen":
                return "*pauses calmly* Sure, let's hear what you have to say. I'm always interested in different perspectives.";
            
            case "anand":
                return "*smiles graciously* Of course! I love discussing chess with fellow enthusiasts. What's your question?";
            
            case "kasparov":
                return "*raises eyebrow* An interruption? Bold move. Let's see what insight you bring to the table.";
            
            case "karpov":
                return "*stops mid-sentence, slightly formal* Very well. I'll listen to your observation.";
            
            case "kramnik":
                return "*analytical pause* Interesting timing. What position aspect caught your attention?";
            
            case "capablanca":
                return "*elegantly pauses* Ah, a fellow chess lover joins us. Please, share your thoughts.";
            
            case "morphy":
                return "*courteous bow* My apologies, good sir. You have the floor.";
            
            case "lasker":
                return "*philosophical smile* The student becomes the teacher? I'm listening with great interest.";
            
            case "alekhine":
                return "*intense focus shifts* You dare interrupt? This calculation was reaching a critical point...";
            
            case "botvinnik":
                return "*methodical pause* I see. Let me note where we were and address your inquiry systematically.";
            
            default:
                return "*pauses thoughtfully* Ah, you'd like to join our discussion. Please, go ahead.";
        }
    }
    
    /**
     * 🎭 Get other player's reaction to the interruption
     */
    private String getOtherPlayerReaction(String masterName, String interruptedMaster) {
        if (masterName == null || interruptedMaster == null) return null;
        
        // Special interactions based on who was interrupted
        if ("fischer".equals(interruptedMaster.toLowerCase())) {
            switch (masterName.toLowerCase()) {
                case "tal":
                    return "*grins* Bobby doesn't like interruptions, but I think this could be fun!";
                case "carlsen":
                    return "*amused* Fischer's perfectionist nature showing. Let's see what our friend wants to know.";
                case "anand":
                    return "*diplomatic* Bobby, perhaps our spectator friend has noticed something we missed?";
                default:
                    return "*observes Fischer's reaction with interest*";
            }
        } else if ("anand".equals(interruptedMaster.toLowerCase())) {
            return "*nods approvingly* Anand always makes time for chess education. Wise approach.";
        } else if ("tal".equals(interruptedMaster.toLowerCase())) {
            return "*smiles* Trust Tal to welcome an interruption with such enthusiasm!";
        }
        
        return null; // Many won't react to preserve natural flow
    }

    /**
     * 🎤 Show user comment dialog with voice recording option
     */
    private void showUserCommentDialog() {
        // REDESIGNED: Skip choice dialog and go straight to voice (modern UX)
        startVoiceComment();
    }

    /**
     * 🎤 Start voice recording for comment - REDESIGNED for seamless UX
     */
    private void startVoiceComment() {
        Log.d(TAG, "🎤 Starting seamless voice comment recording");
        
        // CRITICAL FIX: Stop all TTS and pause AI conversations during user recording
        OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(this);
        if (ttsService != null && ttsService.isSpeaking()) {
            Log.d(TAG, "🛑 Stopping AI speech for user voice comment");
            ttsService.stopSpeaking();
        }
        
        // Pause AI conversations during user recording
        SpectatorConversationOrchestrator orchestrator = SpectatorConversationOrchestrator.getInstance(this);
        orchestrator.setUserRecordingInProgress(true);
        
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

        // REDESIGNED: Simple listening indicator with automatic silence detection
        AlertDialog.Builder listeningBuilder = new AlertDialog.Builder(this);
        listeningBuilder.setTitle("🎤 Listening...");
        listeningBuilder.setMessage("Speak your comment about the game");
        listeningBuilder.setCancelable(true);
        
        // Add pulsing progress indicator
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        listeningBuilder.setView(progressBar);
        
        AlertDialog listeningDialog = listeningBuilder.create();
        listeningDialog.show();
        
        // Start automatic STT with silence detection
        executorService.execute(() -> {
            try {
                // Use improved STT with automatic silence detection
                String transcribedText = performAutomaticSpeechToText(listeningDialog);
                
                mainHandler.post(() -> {
                    listeningDialog.dismiss();
                    
                    // Resume AI conversations after recording is done
                    orchestrator.setUserRecordingInProgress(false);
                    
                    if (transcribedText != null && !transcribedText.trim().isEmpty()) {
                        // REDESIGNED: Skip confirmation, process directly
                        Log.d(TAG, "🎯 Processing voice comment directly: " + transcribedText);
                        submitUserComment(transcribedText);
                    } else {
                        Toast.makeText(this, "Couldn't understand. Try speaking again or type your comment.", Toast.LENGTH_SHORT).show();
                        // Offer to try again or type
                        showVoiceOrTypeChoice();
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in voice recording", e);
                mainHandler.post(() -> {
                    listeningDialog.dismiss();
                    
                    // Resume AI conversations after recording error
                    SpectatorConversationOrchestrator.getInstance(this).setUserRecordingInProgress(false);
                    
                    Toast.makeText(this, "Voice failed. Try typing your comment.", Toast.LENGTH_SHORT).show();
                    showTextCommentDialog();
                });
            }
        });
    }

    /**
     * 🎤 NEW: Perform automatic speech-to-text with silence detection (like main game)
     */
    private String performAutomaticSpeechToText(AlertDialog listeningDialog) {
        try {
            Log.d(TAG, "🎤 Starting automatic speech recognition with silence detection");
            
            // Use GroqSpeechRecognizer with automatic silence detection
            GroqSpeechRecognizer speechRecognizer = new GroqSpeechRecognizer(this);
            
            // Create a synchronization object to wait for result
            final Object lock = new Object();
            final String[] result = new String[1];
            final boolean[] completed = new boolean[1];
            final AtomicBoolean isListening = new AtomicBoolean(true);
            
            // REDESIGNED: No stop button - automatic silence detection only
            mainHandler.post(() -> {
                listeningDialog.setOnCancelListener(dialog -> {
                    Log.d(TAG, "🚫 User cancelled voice input");
                    isListening.set(false);
                    speechRecognizer.stopListening();
                    synchronized (lock) {
                        completed[0] = true;
                        lock.notify();
                    }
                });
            });
            
            // CRITICAL FIX: Add silence detection timer
            final long SILENCE_THRESHOLD = 2000; // 2 seconds of silence
            final AtomicReference<Long> lastSoundTime = new AtomicReference<>(System.currentTimeMillis());
            
            // Silence detection thread
            Thread silenceDetector = new Thread(() -> {
                while (isListening.get() && !completed[0]) {
                    try {
                        Thread.sleep(500); // Check every 500ms
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastSoundTime.get() > SILENCE_THRESHOLD) {
                            Log.d(TAG, "🔇 Silence detected - stopping recording automatically");
                            isListening.set(false);
                            speechRecognizer.stopListening();
                            // Don't notify lock here - let the success callback handle it
                            break;
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            silenceDetector.start();
            
            // Start listening with callback
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "⚠️ Audio recording permission not granted");
                synchronized (lock) {
                    completed[0] = true;
                    lock.notify();
                }
                return null;
            }
            speechRecognizer.startListening(new GroqSpeechRecognizer.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String text) {
                    Log.d(TAG, "✅ Speech recognized: " + text);
                    synchronized (lock) {
                        result[0] = text;
                        completed[0] = true;
                        isListening.set(false);
                        lock.notify();
                    }
                    silenceDetector.interrupt();
                }
                
                @Override
                public void onSpeechError(String error) {
                    Log.e(TAG, "❌ Speech recognition error: " + error);
                    synchronized (lock) {
                        result[0] = null;
                        completed[0] = true;
                        isListening.set(false);
                        lock.notify();
                    }
                    silenceDetector.interrupt();
                }
            });
            
            // Update last sound time (simulate audio activity detection)
            // In a real implementation, this would be updated by audio level monitoring
            lastSoundTime.set(System.currentTimeMillis());
            
            // Wait for result with timeout
            synchronized (lock) {
                if (!completed[0]) {
                    lock.wait(10000); // 10 second total timeout
                }
            }
            
            // Cleanup
            isListening.set(false);
            speechRecognizer.stopListening();
            silenceDetector.interrupt();
            
            return result[0];
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Automatic STT error", e);
            return null;
        }
    }
    
    /**
     * 🎤 LEGACY: Perform speech-to-text conversion using Groq (for fallback)
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
            final String[] error = new String[1];
            
            // Update dialog button to allow stopping
            mainHandler.post(() -> {
                recordingDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                    Log.d(TAG, "⏹️ User stopped recording");
                    speechRecognizer.stopListening();
                    synchronized (lock) {
                        // CRITICAL FIX: Only set completed if we don't already have a result
                        if (result[0] == null) {
                            completed[0] = true;
                            lock.notify();
                        } else {
                            Log.d(TAG, "✅ User stopped but we already have transcription result");
                        }
                    }
                });
            });
            
            // Start listening with callback
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "⚠️ Audio recording permission not granted");
                synchronized (lock) {
                    error[0] = "Audio recording permission not granted";
                    completed[0] = true;
                    lock.notify();
                }
                return null;
            }
            speechRecognizer.startListening(new GroqSpeechRecognizer.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String text) {
                    Log.d(TAG, "✅ Speech recognized: " + text);
                    synchronized (lock) {
                        // CRITICAL FIX: Always prioritize successful transcription
                        result[0] = text;
                        completed[0] = true;
                        lock.notify();
                        Log.d(TAG, "🎯 STT result stored and notification sent");
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
     * 🔄 Show choice between voice and type (only shown on voice failure)
     */
    private void showVoiceOrTypeChoice() {
        AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(this);
        choiceBuilder.setTitle("💬 Try Again?");
        choiceBuilder.setMessage("Would you like to try speaking again or type your comment?");
        
        choiceBuilder.setPositiveButton("🎤 Try Speaking Again", (dialog, which) -> {
            startVoiceComment();
        });
        
        choiceBuilder.setNegativeButton("⌨️ Type Comment", (dialog, which) -> {
            showTextCommentDialog();
        });
        
        choiceBuilder.setNeutralButton("❌ Cancel", (dialog, which) -> dialog.cancel());
        
        choiceBuilder.show();
    }
    
    /**
     * ✅ REMOVED: Confirm transcribed text before sending (now processes directly)
     */
    private void confirmTranscription(String transcribedText) {
        // REDESIGNED: Skip confirmation and process directly
        Log.d(TAG, "🎯 Direct processing (no confirmation): " + transcribedText);
        submitUserComment(transcribedText);
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

            // Send comment to enhanced conversation orchestrator for AI response
            if (viewModel != null) {
                SpectatorConversationOrchestrator orchestrator = SpectatorConversationOrchestrator.getInstance(this);

                // 👤 ENHANCED: Create game context with user profile information
                String baseGameContext = String.format("Game: %s vs %s\nPhase: %s\nUser comment: %s", 
                    whitePlayer, blackPlayer, gamePhase, comment);
                String gameContext = addUserProfileContextToSpectator(baseGameContext);

                orchestrator.startConversation(
                        "user_comment",
                        whitePlayer,
                        blackPlayer,
                        gameContext,
                        new SpectatorConversationOrchestrator.ConversationCallback() {
                            @Override
                            public void onConversationStart(String speaker1, String speaker2) {
                                Log.d(TAG, "🎬 Enhanced conversation started between " + speaker1 + " and " + speaker2);
                            }

                            @Override
                            public void onDialogueGenerated(String speaker, String dialogue) {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "🎭 Master " + speaker + " responded to user comment with enhanced AI");
                                    displayAIDialogue(dialogue);

                                    // Show special indicator that this is a response to user
                                    String userResponseIndicator = "👤➡️🎭 " +
                                            FineTunedModelManager.getInstance(SpectatorGameActivity.this).getMasterDisplayName(speaker) +
                                            " responds: \"" + dialogue + "\"";
                                    displayAIDialogue(userResponseIndicator);
                                });
                            }

                            @Override
                            public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "😮 Emotional response from " + speaker + ": " + emotion);
                                    String emotionalIndicator = "💭 " + 
                                            FineTunedModelManager.getInstance(SpectatorGameActivity.this).getMasterDisplayName(speaker) +
                                            " (" + emotion + "): \"" + dialogue + "\"";
                                    displayAIDialogue(emotionalIndicator);
                                });
                            }

                            @Override
                            public void onConversationEnd(String finalSpeaker, String finalMessage) {
                                Log.d(TAG, "✅ User-triggered conversation completed by " + finalSpeaker);
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
     * 👤 ENHANCED: Add user profile context for spectator mode conversations
     */
    private String addUserProfileContextToSpectator(String gameContext) {
        try {
            UserProfileManager profileManager = UserProfileManager.getInstance(this);
            
            if (profileManager.hasProfile()) {
                UserProfileManager.UserProfile profile = profileManager.getProfile();
                Log.d(TAG, "👤 Adding user profile context for spectator conversation");
                
                // Add user context for both masters
                String userContext = String.format(
                    "USER CONTEXT: %s is watching this game. %s",
                    profile.getDisplayName(),
                    profile.generateAIContext()
                );
                
                return userContext + "\n\n" + gameContext;
            } else {
                Log.d(TAG, "👤 No user profile found - using standard spectator context");
                return gameContext;
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error adding user profile context to spectator mode", e);
            return gameContext;
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
            Log.d(TAG, "📜 FULL RESPONSE: " + dialogue);
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

    /**
     * 🔊 Toggle TTS on/off for spectator mode - CRITICAL for quota management
     */
    private void toggleTTS() {
        try {
            // Get current TTS setting from SharedPreferences
            android.content.SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean currentTTSEnabled = prefs.getBoolean("tts_enabled", true);
            
            // Toggle the setting
            boolean newTTSEnabled = !currentTTSEnabled;
            prefs.edit().putBoolean("tts_enabled", newTTSEnabled).apply();
            
            // Update button appearance
            updateTTSButtonText();
            
            // Stop current TTS if disabling
            if (!newTTSEnabled) {
                OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(this);
                if (ttsService != null) {
                    ttsService.stopSpeaking();
                }
                Log.d(TAG, "🔇 TTS disabled - stopped current speech");
            } else {
                Log.d(TAG, "🔊 TTS enabled");
            }
            
            // Show feedback
            String statusMessage = newTTSEnabled ? "🔊 Voice enabled" : "🔇 Voice disabled";
            Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
            displayAIDialogue(statusMessage + " - Masters will " + (newTTSEnabled ? "speak" : "be silent"));
            
            Log.d(TAG, "🔊 TTS toggled: " + (newTTSEnabled ? "ON" : "OFF"));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error toggling TTS", e);
            Toast.makeText(this, "Error toggling voice", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🔊 Update TTS button text based on current setting
     */
    private void updateTTSButtonText() {
        if (ttsToggleButton != null) {
            android.content.SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
            
            if (ttsEnabled) {
                ttsToggleButton.setText("🔊 TTS");
                ttsToggleButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark)));
            } else {
                ttsToggleButton.setText("🔇 TTS");
                ttsToggleButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_dark)));
            }
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
        
        // Unregister from voice control manager but keep service running
        if (voiceControlManager != null) {
            voiceControlManager.unregisterVoiceCommandListener();
            Log.d(TAG, "🎤 Unregistered from voice control manager for spectator mode");
        }
        
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

            // 0. Reset game state
            gameStarted = false;
            updateStartButtonText();

            // 1. CRITICAL FIX: Clear spectator mode flag to re-enable EvaluationTracker in normal mode
            android.content.SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("is_spectator_mode", false).apply();
            Log.d(TAG, "✅ Spectator mode flag cleared - EvaluationTracker auto-commentary re-enabled for normal mode");

            // 2. Stop ViewModel immediately
            if (viewModel != null) {
                Log.d(TAG, "🛑 Stopping ViewModel");
                viewModel.forceStop();
                viewModel.cleanup();
            }

            // 3. Stop all executors immediately
            if (executorService != null && !executorService.isShutdown()) {
                Log.d(TAG, "🛑 Shutting down executors");
                executorService.shutdownNow();
            }

            // 4. Clear all handlers
            if (mainHandler != null) {
                Log.d(TAG, "🛑 Clearing all handler callbacks");
                mainHandler.removeCallbacksAndMessages(null);
            }

            // 5. Stop TTS services
            OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(this);
            if (ttsService != null) {
                Log.d(TAG, "🛑 Stopping TTS service");
                ttsService.stopSpeaking();
            }

            // 6. Stop all AI dialogue (via ViewModel since it has the instance)
            Log.d(TAG, "🛑 Stopping dialogue via ViewModel");
            // AIDialogueManager will be stopped via ViewModel.forceStop()
            
            // 7. Clear Phase 2 emotional state
            if (phase2Bridge != null) {
                Log.d(TAG, "🛑 Clearing Phase 2 emotional state");
                phase2Bridge.clearEmotionalState();
            }

            Log.d(TAG, "✅ EMERGENCY CLEANUP COMPLETED");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error during emergency cleanup", e);
        }
    }

    // ==================== Phase 2 Emotional Complexity System ====================

    /**
     * 🎭 Initialize Phase 2 multi-layered emotional complexity system
     */
    private void initializePhase2EmotionalSystem() {
        try {
            Log.d(TAG, "🎭 Initializing Phase 2 emotional complexity system");
            
            // Initialize core components
            multiLayeredManager = MultiLayeredEmotionalManager.getInstance(this);
            voiceEmotionalAnalyzer = VoiceEmotionalAnalyzer.getInstance(this);
            phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(this);
            
            // Get other required components
            SpectatorConversationOrchestrator spectatorOrchestrator = SpectatorConversationOrchestrator.getInstance(this);
            EmotionalIntelligenceManager emotionalIntelligenceManager = EmotionalIntelligenceManager.getInstance(this);
            EvaluationTracker evaluationTracker = EvaluationTracker.getInstance(this);
            
            // Initialize full integration
            phase2Bridge.initializeIntegration(
                multiLayeredManager,
                spectatorOrchestrator,
                emotionalIntelligenceManager,
                voiceEmotionalAnalyzer,
                evaluationTracker
            );
            
            Log.d(TAG, "✅ Phase 2 emotional complexity system initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing Phase 2 emotional system", e);
            // Don't fail spectator mode for this - it can work without Phase 2
        }
    }

    // ==================== Always-Listening Voice Control Manager ====================

    /**
     * 🎤 Initialize always-listening voice control manager for spectator mode
     */
    private void initializeVoiceControlManager() {
        try {
            Log.d(TAG, "🎤 Initializing always-listening voice control for spectator mode");
            
            // Get VoiceControlManager instance
            voiceControlManager = VoiceControlManager.getInstance(this);
            
            // Register this activity as a voice command listener
            voiceControlManager.registerVoiceCommandListener(this);
            
            Log.d(TAG, "✅ Voice control manager initialized for spectator mode");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing voice control manager", e);
        }
    }

    // ==================== VoiceCommandListener Interface Implementation ====================

    @Override
    public void onWakeWordDetected(String wakeWord) {
        Log.d(TAG, "🎯 Wake word detected in spectator mode: " + wakeWord);
        
        // Show visual feedback
        runOnUiThread(() -> {
            displayAIDialogue("🎤 " + wakeWord + " detected! Listening for your comment...");
        });
    }

    @Override
    public void onVoiceCommand(String command) {
        Log.d(TAG, "🗣️ Voice command received in spectator mode: " + command);
        
        runOnUiThread(() -> {
            if (command.startsWith("interrupt_for_comment")) {
                // Start voice comment directly (modern UX)
                Log.d(TAG, "🎤 Starting voice comment from wake word");
                startVoiceComment();
                
            } else if (command.startsWith("focus_master:")) {
                // Focus on specific master in conversation
                String masterName = command.substring("focus_master:".length());
                Log.d(TAG, "🎭 Focusing on master: " + masterName);
                
                String displayName = FineTunedModelManager.getInstance(this).getMasterDisplayName(masterName);
                displayAIDialogue("👁️ Focusing on " + displayName + "'s commentary...");
                
                // TODO: Implement master focus in spectator conversation
                
            } else if (command.startsWith("voice_input:")) {
                // Process direct voice input as comment
                String voiceText = command.substring("voice_input:".length());
                Log.d(TAG, "🎤 Processing voice comment: " + voiceText);
                
                // Submit the voice input as user comment
                submitUserComment(voiceText);
                
            } else {
                Log.w(TAG, "⚠️ Unknown voice command in spectator mode: " + command);
            }
        });
    }

    @Override
    public void onVoiceError(String error) {
        Log.e(TAG, "❌ Voice control error in spectator mode: " + error);
        
        runOnUiThread(() -> {
            displayAIDialogue("❌ Voice error: " + error);
        });
    }

    @Override
    public String getActivityType() {
        return "spectator";
    }
    
    @Override
    public void onVoiceStatusChanged(AlwaysListeningService.VoiceStatus status) {
        Log.d(TAG, "🚦 Voice status changed in spectator mode: " + status);
        
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
                Log.d(TAG, "✅ Voice status indicator added to spectator UI");
            } else {
                Log.e(TAG, "❌ Could not find main layout for voice status indicator");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing voice status indicator in spectator mode", e);
        }
    }

    // ==================== Activity Lifecycle for Voice Control ====================

    @Override
    protected void onResume() {
        super.onResume();
        
        // Re-register with voice control manager
        if (voiceControlManager != null) {
            voiceControlManager.registerVoiceCommandListener(this);
            Log.d(TAG, "🎤 Re-registered with voice control manager for spectator mode");
        }
    }
}