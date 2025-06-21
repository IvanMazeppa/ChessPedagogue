package com.example.chesspedagogue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import com.example.chesspedagogue.viewmodel.GameViewModel;
import com.example.chesspedagogue.repository.GameRepository;

/**
 * 🏆 COMPETITIVE MODE - Face legendary chess masters with full emotional intelligence!
 * Features all Phase 1-3 emotional systems, emergent behavior, and adaptive learning
 */
public class CompetitiveModeActivity extends AppCompatActivity implements VoiceControlManager.VoiceCommandListener {
    private static final String TAG = "CompetitiveModeActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;

    /**
     * Callback interface for async emotional response generation
     */
    private interface EmotionalResponseCallback {
        void onResponseGenerated(String response);
    }

    // UI Elements
    private ChessBoardView chessBoardView;
    private EvaluationBarView evaluationBarView;
    private TextView moveHistoryTextView;
    private TextView masterDialogueTextView;
    private TextView masterNameTextView;
    private TextView playerNameTextView;
    private TextView masterDialogueHeaderTextView;
    private ImageView masterPortraitImageView;
    private TextView gameResultTextView;
    private Button dismissDialogueButton;
    private CardView masterDialogueCard;
    private ProgressBar thinkingProgressBar;
    private Button surrenderButton;
    private Button pauseButton;
    private Button ttsToggleButton;
    private Button voiceCommentButton;
    private Button difficultyToggleButton;
    private Button personalityToggleButton;
    private Button voiceSettingsButton;

    // Game state
    private GameViewModel gameViewModel;
    private String selectedMaster;
    private String playerColor;
    private int skillLevel;
    private int engineElo;
    private boolean isGamePaused = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();
    
    // 🎤 Voice control integration
    private VoiceControlManager voiceControlManager;
    private VoiceStatusIndicator voiceStatusIndicator;
    
    // 🎭 FULL SYSTEM INTEGRATION
    private EmotionalIntelligenceManager emotionalManager;
    
    // 🧠 PHASE 3: ADAPTIVE EMOTIONAL LEARNING INTEGRATION
    private AdaptiveConversationStrategyManager adaptiveManager;
    private String currentConversationId;
    
    // 🎲 PERSONALITY ENGINE & GAME INTELLIGENCE
    private PersonalityEngine personalityEngine;
    private FineTunedModelManager fineTunedModelManager;
    private StockfishManager stockfishManager;
    
    // 🎤 VOICE SERVICES
    private SimpleRecordService recordService;
    private boolean isServiceBound = false;
    private final android.content.ServiceConnection serviceConnection = new android.content.ServiceConnection() {
        @Override
        public void onServiceConnected(android.content.ComponentName name, android.os.IBinder service) {
            SimpleRecordService.LocalBinder binder = (SimpleRecordService.LocalBinder) service;
            recordService = binder.getService();
            isServiceBound = true;
            Log.d(TAG, "🎤 Voice service connected to competitive mode");
            setupVoiceCallback();
        }

        @Override
        public void onServiceDisconnected(android.content.ComponentName name) {
            isServiceBound = false;
            recordService = null;
            Log.d(TAG, "🎤 Voice service disconnected");
        }
    };
    
    // 🎯 COMPETITIVE MODE SETTINGS
    private boolean useMaxDifficulty = false; // Toggle for max ELO vs splash difficulty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "🏆 Starting competitive mode against chess master...");
            setContentView(R.layout.activity_competitive_mode);

            // Get configuration from intent
            loadCompetitiveConfiguration();

            // Initialize all systems
            if (setupActionBar() &&
                initializeViews() &&
                initializeEmotionalIntelligence() &&
                initializePersonalityEngine() &&
                initializeVoiceServices() &&
                setupGameViewModel() &&
                setupObservers() &&
                setupControls()) {

                Log.d(TAG, "✅ All competitive mode systems initialized!");
                startCompetitiveGame();
                
            } else {
                Log.e(TAG, "❌ Failed to initialize competitive mode");
                showErrorAndExit("Failed to initialize competitive mode");
            }

        } catch (Exception e) {
            Log.e(TAG, "💥 Exception in competitive mode onCreate", e);
            showErrorAndExit("Error starting competitive mode: " + e.getMessage());
        }
    }

    /**
     * 📋 Load competitive mode configuration from intent
     */
    private void loadCompetitiveConfiguration() {
        Intent intent = getIntent();
        selectedMaster = intent.getStringExtra("selectedMaster");
        playerColor = intent.getStringExtra("playerColor");
        skillLevel = intent.getIntExtra("skillLevel", 10);
        engineElo = intent.getIntExtra("engineElo", 1750);
        
        // CRITICAL: Ensure master selection consistency across ALL SharedPreferences stores
        synchronizeMasterSelection(selectedMaster);
        
        Log.d(TAG, "🎯 Competitive config - Master: " + selectedMaster + 
                   ", Color: " + playerColor + 
                   ", Skill: " + skillLevel + 
                   ", ELO: " + engineElo);
        Log.d(TAG, "✅ Master synchronized across all SharedPreferences stores");
    }
    
    /**
     * 🔄 Synchronize master selection across ALL SharedPreferences stores
     * This fixes the SharedPreferences inconsistency that causes voice system to use wrong master
     */
    private void synchronizeMasterSelection(String masterName) {
        if (masterName == null) {
            Log.w(TAG, "⚠️ Master name is null, using default 'tal'");
            masterName = "tal";
        }
        
        String normalizedMaster = masterName.toLowerCase();
        Log.d(TAG, "🔄 Synchronizing master selection: " + normalizedMaster);
        
        // Save to ChessFineTunedModels (where voice system reads from)
        getSharedPreferences("ChessFineTunedModels", Context.MODE_PRIVATE)
            .edit()
            .putString("selected_master", normalizedMaster)
            .apply();
            
        // Also save to ChessAppPrefs (for compatibility with other parts of app)
        getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE)
            .edit()
            .putString("selected_master", normalizedMaster)
            .apply();
            
        // Update FineTunedModelManager to ensure consistency
        FineTunedModelManager.getInstance(this).setSelectedChessMaster(normalizedMaster);
        
        // Verify synchronization worked
        String voiceSystemMaster = getSharedPreferences("ChessFineTunedModels", Context.MODE_PRIVATE)
            .getString("selected_master", "unknown");
        String appMaster = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE)
            .getString("selected_master", "unknown");
        String fineTunedMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
        
        Log.d(TAG, "🔍 Master sync verification:");
        Log.d(TAG, "  - Voice system (ChessFineTunedModels): " + voiceSystemMaster);
        Log.d(TAG, "  - App system (ChessAppPrefs): " + appMaster);
        Log.d(TAG, "  - FineTunedModelManager: " + fineTunedMaster);
        
        if (!normalizedMaster.equals(voiceSystemMaster) || !normalizedMaster.equals(appMaster) || 
            !normalizedMaster.equals(fineTunedMaster.toLowerCase())) {
            Log.e(TAG, "❌ Master synchronization FAILED! Inconsistent state detected.");
        } else {
            Log.d(TAG, "✅ Master synchronization SUCCESS! All systems consistent.");
        }
    }

    /**
     * 🎭 Initialize emotional intelligence system
     */
    private boolean initializeEmotionalIntelligence() {
        try {
            Log.d(TAG, "🎭 Initializing emotional intelligence systems...");
            
            emotionalManager = EmotionalIntelligenceManager.getInstance(this);
            
            // 🔧 CRITICAL FIX: Initialize EQ system with historical data
            Log.d(TAG, "🧠 Initializing EQ system with historical emotional data...");
            emotionalManager.initializeWithHistory(selectedMaster, "player");
            Log.d(TAG, "✅ EQ system initialized - emotional reactions and relationships should now work!");
            
            // 🧠 PHASE 3: Initialize adaptive conversation strategy manager
            adaptiveManager = AdaptiveConversationStrategyManager.getInstance(this);
            Log.d(TAG, "🧠 Phase 3: Adaptive conversation strategy manager initialized");
            
            Log.d(TAG, "✅ Emotional intelligence systems initialized");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize emotional intelligence", e);
            return true; // Don't fail completely
        }
    }

    /**
     * 🎲 Initialize personality engine and chess intelligence
     */
    private boolean initializePersonalityEngine() {
        try {
            Log.d(TAG, "🎲 Initializing personality engine for " + selectedMaster + "...");
            
            // Initialize FineTunedModelManager for master-specific responses
            fineTunedModelManager = FineTunedModelManager.getInstance(this);
            fineTunedModelManager.setSelectedChessMaster(selectedMaster);
            
            // Initialize StockfishManager for engine moves
            stockfishManager = new StockfishManager();
            
            // Initialize PersonalityEngine for master-specific move selection
            Log.d(TAG, "🎲 Creating PersonalityEngine instance...");
            personalityEngine = PersonalityEngine.getInstance(this, stockfishManager);
            
            Log.d(TAG, "🎯 Setting current master to: " + selectedMaster);
            personalityEngine.setCurrentMaster(selectedMaster);
            
            Log.d(TAG, "🚀 CALLING initializeMasterData for: " + selectedMaster);
            personalityEngine.initializeMasterData(selectedMaster);
            
            Log.d(TAG, "🎭 Enabling personality play...");
            personalityEngine.setPersonalityPlayEnabled(true);
            
            // Configure difficulty based on toggle
            configureDifficulty();
            
            Log.d(TAG, "✅ Personality engine initialized for " + selectedMaster);
            
            // 🔧 TEMPORARY DIAGNOSTIC: Check and fix database import if needed
            Log.d(TAG, "🔍 Running personality engine database diagnostic...");
            PersonalityEngineDiagnostic.diagnoseMasterDatabase(this, selectedMaster);
            
            // NEW: Run comprehensive system diagnostic
            Log.d(TAG, "🔬 Running comprehensive PersonalityEngine system diagnostic...");
            String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            PersonalityEngineSystemDiagnostic.DiagnosticResult systemDiag = 
                PersonalityEngineSystemDiagnostic.runFullSystemDiagnostic(this, startingFen, selectedMaster, engineElo);
            Log.d(TAG, "📊 System diagnostic result: " + systemDiag.toString());
            
            // Quick name diagnostic
            PersonalityEngineSystemDiagnostic.runQuickNameDiagnostic(this);
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize personality engine", e);
            return false;
        }
    }

    /**
     * 🎤 Initialize voice services (STT + TTS)
     */
    private boolean initializeVoiceServices() {
        try {
            Log.d(TAG, "🎤 Initializing voice services...");
            
            // Initialize voice control manager
            voiceControlManager = VoiceControlManager.getInstance(this);
            voiceControlManager.registerVoiceCommandListener(this);
            
            // Bind to SimpleRecordService for voice processing
            Intent serviceIntent = new Intent(this, SimpleRecordService.class);
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            
            // Update voice settings for current master
            updateVoiceForCurrentMaster();
            
            Log.d(TAG, "✅ Voice services initializing...");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize voice services", e);
            return true; // Don't fail completely
        }
    }

    /**
     * 🎭 Configure GameRepository to use personality engine (CRITICAL FIX!)
     */
    private void configureGameRepositoryPersonality() {
        try {
            Log.d(TAG, "🎭 Configuring GameRepository personality engine for " + selectedMaster);
            
            if (gameViewModel != null && gameViewModel.getGameRepository() != null) {
                // Configure the GameRepository's personality engine with our settings
                float personalityWeight = 0.3f; // 30% personality influence
                boolean personalityEnabled = true;
                
                gameViewModel.getGameRepository().configurePersonalityEngine(
                    selectedMaster, personalityWeight, personalityEnabled);
                
                Log.d(TAG, "✅ GameRepository personality engine configured successfully!");
                Log.d(TAG, "🎯 Master: " + selectedMaster + ", Weight: " + personalityWeight + ", Enabled: " + personalityEnabled);
                
                // Verify the configuration worked
                boolean available = gameViewModel.getGameRepository().isPersonalityEngineAvailable();
                Log.d(TAG, "🔍 Personality engine availability check: " + available);
                
                if (!available) {
                    Log.e(TAG, "❌ CRITICAL: GameRepository personality engine not available after configuration!");
                    Log.e(TAG, "❌ This will cause vanilla Stockfish to be used instead of personality moves!");
                } else {
                    Log.d(TAG, "✅ CONFIRMED: GameRepository personality engine is properly configured and available!");
                }
                
            } else {
                Log.e(TAG, "❌ Cannot configure GameRepository personality - gameViewModel or repository is null");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to configure GameRepository personality engine", e);
        }
    }

    /**
     * ⚙️ Configure difficulty based on toggle setting
     */
    private void configureDifficulty() {
        try {
            if (useMaxDifficulty) {
                // Use maximum difficulty for the selected master
                int maxElo = getMasterMaxELO(selectedMaster);
                stockfishManager.setEngineStrength(maxElo);
                stockfishManager.setSkillLevel(20); // Maximum skill
                Log.d(TAG, "🔥 Using MAX difficulty - ELO: " + maxElo + ", Skill: 20");
            } else {
                // Use splash screen configuration
                stockfishManager.setEngineStrength(engineElo);
                stockfishManager.setSkillLevel(skillLevel);
                Log.d(TAG, "⚖️ Using splash difficulty - ELO: " + engineElo + ", Skill: " + skillLevel);
            }
            
            // Enable personality-driven move selection
            personalityEngine.setPersonalityWeight(0.3f); // 30% personality influence
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to configure difficulty", e);
        }
    }

    /**
     * 🎯 Get maximum ELO for each master (historical peak ratings)
     */
    private int getMasterMaxELO(String master) {
        switch (master.toLowerCase()) {
            case "tal": return 2705;     // Peak: 2705 (1980)
            case "fischer": return 2785; // Peak: 2785 (1972)
            case "carlsen": return 2882; // Peak: 2882 (2014)
            case "kasparov": return 2851; // Peak: 2851 (1999)
            case "karpov": return 2780;  // Peak: 2780 (1994)
            case "kramnik": return 2817; // Peak: 2817 (2016)
            case "alekhine": return 2690; // Estimated peak
            case "capablanca": return 2720; // Estimated peak
            case "morphy": return 2750;  // Estimated peak
            case "lasker": return 2720;  // Estimated peak
            case "anand": return 2817;   // Peak: 2817 (2011)
            case "botvinnik": return 2700; // Estimated peak
            default: return 2800; // Default high rating
        }
    }

    /**
     * 🗣️ Update voice settings for current master (FIXED: Force TTS refresh)
     */
    private void updateVoiceForCurrentMaster() {
        try {
            Log.d(TAG, "🗣️ Updating voice for master: " + selectedMaster);
            
            // 🚨 CRITICAL FIX: Force set master in ALL systems first
            getSharedPreferences("ChessFineTunedModels", Context.MODE_PRIVATE)
                .edit()
                .putString("selected_master", selectedMaster.toLowerCase())
                .apply();
            
            // CRITICAL: Ensure ALL voice components use the same master
            FineTunedModelManager.getInstance(this).setSelectedChessMaster(selectedMaster);
            
            // TRIPLE-CHECK: Verify the master was set correctly in ALL systems
            String verifyMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
            Log.d(TAG, "🔍 FineTunedModelManager master: " + verifyMaster);
            
            // Also verify SharedPreferences directly
            String prefsCheck = getSharedPreferences("ChessFineTunedModels", Context.MODE_PRIVATE)
                .getString("selected_master", "unknown");
            Log.d(TAG, "🔍 SharedPreferences master: " + prefsCheck);
            
            if (!selectedMaster.toLowerCase().equals(verifyMaster.toLowerCase()) || 
                !selectedMaster.toLowerCase().equals(prefsCheck.toLowerCase())) {
                Log.e(TAG, "❌ VOICE MASTER MISMATCH! Expected: " + selectedMaster + 
                           ", FineTuned: " + verifyMaster + ", Prefs: " + prefsCheck);
                
                // Force set in ALL locations AGAIN
                FineTunedModelManager.getInstance(this).setSelectedChessMaster(selectedMaster);
                getSharedPreferences("ChessFineTunedModels", Context.MODE_PRIVATE)
                    .edit().putString("selected_master", selectedMaster.toLowerCase()).apply();
                    
                Log.d(TAG, "🔧 Force-updated master to: " + selectedMaster);
            }
            
            // Set master-specific voice and accent settings
            ChessCoachManager.getInstance(this).updateTTSSettings("auto", true);
            
            // Set usage context for competitive mode
            TTSServiceManager.setUsageContext(this, "competitive_mode");
            
            // FINAL VERIFICATION
            String finalCheck = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
            Log.d(TAG, "✅ Final verification - Voice system master: " + finalCheck);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to update voice settings", e);
        }
    }

    /**
     * 🎤 Setup voice callback for processing user speech
     */
    private void setupVoiceCallback() {
        if (recordService != null) {
            recordService.setCallback(new SimpleRecordService.ServiceCallback() {
                @Override
                public void onRecordingStarted() {
                    mainHandler.post(() -> {
                        voiceCommentButton.setText("🎤 Listening...");
                        voiceCommentButton.setEnabled(false);
                    });
                }

                @Override
                public void onRecordingStopped() {
                    mainHandler.post(() -> {
                        voiceCommentButton.setText("🤔 Processing...");
                    });
                }

                @Override
                public void onProcessingStateChanged(boolean isProcessing) {
                    // Update UI based on processing state
                }

                @Override
                public void onTranscriptionReceived(String transcribedText) {
                    Log.d(TAG, "📝 Player said: " + transcribedText);
                    // Process player's voice input
                    processPlayerVoiceInput(transcribedText);
                }

                @Override
                public void onResponseReceived(String response) {
                    mainHandler.post(() -> {
                        displayMasterDialogue(response);
                        // DON'T speak here - the voice pipeline already handles TTS
                        voiceCommentButton.setText("🎤 Comment");
                        voiceCommentButton.setEnabled(true);
                    });
                }

                @Override
                public void onResponseCompleted(String response) {
                    // Master finished speaking - voice pipeline handles all TTS
                }
            });
        }
    }

    /**
     * 🎯 Setup action bar with competitive mode title
     */
    private boolean setupActionBar() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("🏆 vs " + formatMasterName(selectedMaster));
                getSupportActionBar().setSubtitle("Competitive Mode - Full AI Complexity");
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to setup action bar", e);
            return false;
        }
    }

    /**
     * 🎨 Initialize all UI views
     */
    private boolean initializeViews() {
        try {
            Log.d(TAG, "🎨 Initializing views...");
            
            chessBoardView = findViewById(R.id.competitiveChessBoardView);
            evaluationBarView = findViewById(R.id.competitiveEvaluationBarView);
            moveHistoryTextView = findViewById(R.id.competitiveMoveHistoryTextView);
            masterDialogueTextView = findViewById(R.id.masterDialogueTextView);
            masterNameTextView = findViewById(R.id.masterNameTextView);
            playerNameTextView = findViewById(R.id.playerNameTextView);
            masterDialogueHeaderTextView = findViewById(R.id.masterDialogueHeaderTextView);
            masterPortraitImageView = findViewById(R.id.masterPortraitImageView);
            gameResultTextView = findViewById(R.id.gameResultTextView);
            dismissDialogueButton = findViewById(R.id.dismissDialogueButton);
            masterDialogueCard = findViewById(R.id.masterDialogueCard);
            thinkingProgressBar = findViewById(R.id.masterThinkingProgressBar);
            surrenderButton = findViewById(R.id.surrenderButton);
            pauseButton = findViewById(R.id.pauseButton);
            ttsToggleButton = findViewById(R.id.ttsToggleButton);
            voiceCommentButton = findViewById(R.id.voiceCommentButton);
            difficultyToggleButton = findViewById(R.id.difficultyToggleButton);
            personalityToggleButton = findViewById(R.id.personalityToggleButton);
            voiceSettingsButton = findViewById(R.id.voiceSettingsButton);

            // Set master and player names dynamically
            masterNameTextView.setText(formatMasterName(selectedMaster));
            playerNameTextView.setText("You (" + playerColor.toUpperCase() + ")");
            
            // Set dynamic dialogue header and portrait
            setDynamicMasterElements();

            // Initialize voice status indicator
            initializeVoiceStatusIndicator();

            Log.d(TAG, "✅ Views initialized");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize views", e);
            return false;
        }
    }

    /**
     * 🎨 Set dynamic master elements (portrait and dialogue header)
     */
    private void setDynamicMasterElements() {
        try {
            // Set dynamic dialogue header
            if (masterDialogueHeaderTextView != null) {
                masterDialogueHeaderTextView.setText(formatMasterName(selectedMaster) + " speaks:");
            }
            
            // Set dynamic master portrait
            if (masterPortraitImageView != null) {
                int portraitResource = getMasterPortraitResource(selectedMaster);
                masterPortraitImageView.setImageResource(portraitResource);
            }
            
            Log.d(TAG, "✅ Dynamic master elements set for: " + selectedMaster);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to set dynamic master elements", e);
        }
    }
    
    /**
     * 🎭 Get portrait resource for master
     */
    private int getMasterPortraitResource(String master) {
        switch (master.toLowerCase()) {
            case "tal": return R.drawable.portrait_speaking_tal;
            case "fischer": return R.drawable.portrait_speaking_fischer;
            case "carlsen": return R.drawable.portrait_speaking_carlsen;
            case "kasparov": return R.drawable.portrait_speaking_kasparov;
            case "karpov": return R.drawable.portrait_speaking_karpov;
            case "kramnik": return R.drawable.portrait_speaking_kramnik;
            case "alekhine": return R.drawable.portrait_speaking_alekhine;
            case "capablanca": return R.drawable.portrait_speaking_capablanca;
            case "morphy": return R.drawable.portrait_speaking_morphy;
            case "lasker": return R.drawable.portrait_speaking_lasker;
            case "anand": return R.drawable.portrait_speaking_anand;
            case "botvinnik": return R.drawable.portrait_speaking_botvinnik;
            default: return R.drawable.portrait_speaking_tal; // fallback
        }
    }

    /**
     * 🎤 Initialize voice status indicator
     */
    private void initializeVoiceStatusIndicator() {
        try {
            voiceStatusIndicator = new VoiceStatusIndicator(this);
            // Note: attachToActivity method might not exist, so just log for now
            Log.d(TAG, "✅ Voice status indicator initialized");
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize voice status indicator", e);
        }
    }

    /**
     * 🎯 Setup game view model with competitive configuration
     */
    private boolean setupGameViewModel() {
        try {
            gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
            
            // Configure for competitive mode
            // Note: Using basic configuration for now
            gameViewModel.configureAutoCommentary(false); // We handle commentary through emotional systems
            
            // CRITICAL FIX: Configure GameRepository to use personality engine
            configureGameRepositoryPersonality();
            
            Log.d(TAG, "✅ Game view model configured for competitive mode");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to setup game view model", e);
            return false;
        }
    }

    /**
     * 👀 Setup observers for game state and emotional reactions (COMPLETE PATTERN FROM MAIN GAME)
     */
    private boolean setupObservers() {
        try {
            Log.d(TAG, "👀 Setting up observers...");
            
            // Chess board observers (EXACT PATTERN FROM MAIN GAME)
            gameViewModel.getCurrentFEN().observe(this, fen -> {
                if (fen != null && chessBoardView != null) {
                    chessBoardView.updateBoardFromFen(fen);
                    Log.d(TAG, "🎯 Board updated with FEN: " + fen);
                }
            });

            // Move animation observers (CRITICAL FOR SMOOTH MOVES)
            gameViewModel.getAnimateMoveEvent().observe(this, moveCoords -> {
                if (moveCoords != null && chessBoardView != null) {
                    int fromRow = moveCoords[0];
                    int fromCol = moveCoords[1];
                    int toRow = moveCoords[2];
                    int toCol = moveCoords[3];
                    chessBoardView.animateMove(fromRow, fromCol, toRow, toCol);
                }
            });

            // Last move highlighting observers (PREVENTS CONFUSION)
            gameViewModel.getLastMoveEvent().observe(this, lastMoveCoords -> {
                if (lastMoveCoords != null && chessBoardView != null) {
                    int fromRow = lastMoveCoords[0];
                    int fromCol = lastMoveCoords[1];
                    int toRow = lastMoveCoords[2];
                    int toCol = lastMoveCoords[3];
                    chessBoardView.setLastMove(fromRow, fromCol, toRow, toCol);
                }
            });

            // King in check observers (IMPORTANT FOR VISUAL FEEDBACK)
            gameViewModel.getKingInCheckEvent().observe(this, checkData -> {
                if (checkData != null && chessBoardView != null) {
                    boolean inCheck = checkData[0] == 1;
                    int kingRow = checkData[1];
                    int kingCol = checkData[2];
                    chessBoardView.setKingInCheck(inCheck, kingRow, kingCol);
                }
            });

            // Selection clearing observers (PREVENTS STUCK SELECTION)
            gameViewModel.getClearSelectionEvent().observe(this, shouldClear -> {
                if (shouldClear != null && shouldClear && chessBoardView != null) {
                    chessBoardView.clearSelectionHighlight();
                }
            });

            // Evaluation observers with emotional triggers
            gameViewModel.getCurrentEvaluation().observe(this, evaluation -> {
                if (evaluation != null && evaluationBarView != null) {
                    evaluationBarView.setEvaluation(evaluation);
                    Log.d(TAG, "📊 Evaluation updated: " + evaluation);
                    
                    // Trigger emotional reactions to evaluation changes
                    triggerEmotionalReactionToEvaluation(evaluation);
                }
            });

            // Move history observer (FIXED: No automatic AI triggering)
            gameViewModel.getMoveHistory().observe(this, moveHistory -> {
                if (moveHistory != null && moveHistoryTextView != null) {
                    updateMoveHistoryDisplay(moveHistory);
                    Log.d(TAG, "📜 Move history updated: " + moveHistory.size() + " moves");
                    
                    // FIXED: Only update display, don't trigger AI moves automatically
                    // AI moves are now handled properly by the game engine after valid player moves
                    // This prevents cascading automatic move issues
                }
            });

            // Winner observers
            gameViewModel.getWinner().observe(this, winner -> {
                if (winner != null) {
                    updateGameResultDisplay(getCurrentGameResult());
                    Log.d(TAG, "🏆 Winner: " + winner);
                }
            });

            // CRITICAL: Setup chess board interaction
            setupChessBoardInteraction();

            Log.d(TAG, "✅ Observers setup complete");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to setup observers", e);
            return false;
        }
    }

    /**
     * 🎯 Setup chess board interaction for competitive mode (COPIED FROM MAIN GAME)
     */
    private void setupChessBoardInteraction() {
        if (chessBoardView != null && gameViewModel != null) {
            Log.d(TAG, "🎯 Setting up chess board interaction...");
            
            // Set square tap listener to handle player moves (EXACT PATTERN FROM MAIN GAME)
            chessBoardView.setOnSquareTapListener((row, col) -> {
                Log.d(TAG, "🎯 COMPETITIVE MODE SQUARE TAPPED: row=" + row + ", col=" + col);
                handleSquareTapProper(row, col);
            });
            
            Log.d(TAG, "✅ Chess board interaction setup complete");
        } else {
            Log.e(TAG, "❌ Cannot setup chess board interaction - missing components");
        }
    }

    /**
     * 🎯 Handle square tap in competitive mode (EXACT PATTERN FROM MAIN GAME)
     */
    private void handleSquareTapProper(int row, int col) {
        Log.d(TAG, "🎯 SQUARE TAPPED: row=" + row + ", col=" + col);
        Log.d(TAG, "📝 Player color: " + playerColor);
        Log.d(TAG, "🔍 Selected row/col: " + chessBoardView.getSelectedRow() + "/" + chessBoardView.getSelectedCol());

        // If we already have a piece selected...
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

            // Only check piece ownership for actual pieces, not empty squares
            if (tappedPiece != ' ') {
                boolean isPlayerWhite = "white".equals(playerColor);
                boolean isPieceWhite = Character.isUpperCase(tappedPiece);
                boolean isOurPiece = (isPlayerWhite && isPieceWhite) || (!isPlayerWhite && !isPieceWhite);

                if (isOurPiece) {
                    // Tapped another of our pieces, so select this one instead
                    chessBoardView.clearSelectionHighlight();
                    chessBoardView.clearHighlightedSquares();
                    chessBoardView.setSelectedSquare(row, col);

                    // Show legal moves for newly selected piece
                    gameViewModel.getLegalMovesForSquare(algebraicNotation(row, col), moves -> {
                        for (String move : moves) {
                            if (move.length() >= 4) {
                                int destRow = 8 - Character.getNumericValue(move.charAt(3));
                                int destCol = move.charAt(2) - 'a';
                                chessBoardView.addHighlightedSquare(destRow, destCol);
                            }
                        }
                    });
                    return;
                }
            }

            // Otherwise, try to make a move from the selected piece to this square
            String baseMove = algebraicNotation(fromRow, fromCol) + algebraicNotation(row, col);

            boolean isPlayerWhite = "white".equals(playerColor);
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
            Log.d(TAG, "📝 Player color: " + playerColor);
            Log.d(TAG, "🔄 Is player's turn: " + isPlayersTurn);
            Log.d(TAG, "🔄 Is white's turn: " + chessBoardView.isWhiteTurn());

            if (isPlayersTurn) {
                Log.d(TAG, "✅ Turn validation passed, making move: " + move);
                gameViewModel.makePlayerMove(move);
            } else {
                Log.d(TAG, "❌ Turn validation failed - not player's turn!");
                Toast.makeText(CompetitiveModeActivity.this, "Wait for your turn!", Toast.LENGTH_SHORT).show();
            }

            chessBoardView.clearSelectionHighlight();
            chessBoardView.clearHighlightedSquares();
            
        } else {
            // No piece is selected yet, select if it's our piece and our turn
            char piece = chessBoardView.getPieceAt(row, col);

            // Improved piece ownership and turn logic
            boolean isPlayerWhite = "white".equals(playerColor);
            boolean isPieceWhite = Character.isUpperCase(piece);
            boolean isOurPiece = (isPlayerWhite && isPieceWhite) || (!isPlayerWhite && !isPieceWhite);
            boolean isPlayersTurn = (isPlayerWhite && chessBoardView.isWhiteTurn()) ||
                    (!isPlayerWhite && !chessBoardView.isWhiteTurn());

            if (piece != ' ' && isOurPiece && isPlayersTurn) {
                chessBoardView.setSelectedSquare(row, col);
                gameViewModel.getLegalMovesForSquare(algebraicNotation(row, col), moves -> {
                    chessBoardView.clearHighlightedSquares();
                    for (String move : moves) {
                        if (move.length() >= 4) {
                            int destRow = 8 - Character.getNumericValue(move.charAt(3));
                            int destCol = move.charAt(2) - 'a';
                            chessBoardView.addHighlightedSquare(destRow, destCol);
                        }
                    }
                });
                Log.d(TAG, "🎯 Selected piece: " + piece + " at " + row + ", " + col);
            } else {
                if (piece == ' ') {
                    Log.d(TAG, "🎯 Invalid selection: empty square");
                } else if (!isOurPiece) {
                    Log.d(TAG, "🎯 Invalid selection: opponent's piece");
                } else if (!isPlayersTurn) {
                    Log.d(TAG, "🎯 Invalid selection: not your turn");
                    Toast.makeText(CompetitiveModeActivity.this, "Wait for your turn!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    /**
     * 🎯 Helper method: Convert coordinates to algebraic notation (FROM MAIN GAME)
     */
    private String algebraicNotation(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
    
    /**
     * 🎯 Helper method: Check if piece belongs to player (FROM MAIN GAME)
     */
    private boolean isPlayersPiece(char piece) {
        if (piece == ' ') return false;
        
        boolean isWhitePiece = Character.isUpperCase(piece);
        boolean isPlayerWhite = "white".equals(playerColor);
        
        return isWhitePiece == isPlayerWhite;
    }
    
    // 🚨 CRITICAL FIX: Add flag to prevent AI response loops
    private boolean isAIMovePending = false;
    
    /**
     * 🎯 Trigger AI response after player move (FIXED: NO MORE DOUBLE MOVES!)
     */
    private void triggerAIResponseAfterPlayerMove(List<String> moveHistory) {
        if (moveHistory == null || moveHistory.isEmpty()) {
            return;
        }
        
        // 🚨 CRITICAL FIX: Prevent AI response loops
        if (isAIMovePending) {
            Log.d(TAG, "🚫 AI move already pending - skipping duplicate trigger");
            return;
        }
        
        // Check if it's the AI's turn to move
        boolean isWhiteTurn = (moveHistory.size() % 2 == 0);
        boolean isPlayerWhite = "white".equals(playerColor);
        boolean isAITurn = (isWhiteTurn && !isPlayerWhite) || (!isWhiteTurn && isPlayerWhite);
        
        if (isAITurn && !isGamePaused) {
            Log.d(TAG, "🤖 AI turn detected - generating response move...");
            
            // 🚨 CRITICAL FIX: Set flag to prevent duplicate triggers
            isAIMovePending = true;
            
            // Show thinking indicator
            if (thinkingProgressBar != null) {
                thinkingProgressBar.setVisibility(View.VISIBLE);
            }
            
            // Generate AI response with delay for better UX
            mainHandler.postDelayed(() -> {
                generateAIResponseMove();
            }, 500); // Small delay to show thinking
        } else {
            Log.d(TAG, "🎯 Player turn - waiting for player input");
        }
    }

    /**
     * 🤖 Generate AI response move after player move
     */
    private void generateAIResponseMove() {
        Log.d(TAG, "🤖 Generating AI response move...");
        
        executorService.execute(() -> {
            try {
                if (gameViewModel.getGameRepository() != null) {
                    // Check if personality engine is enabled
                    if (personalityEngine != null && personalityEngine.isPersonalityPlayEnabled()) {
                        // Use personality-guided move
                        gameViewModel.getGameRepository().calculatePersonalityMove(new GameRepository.MoveCallback() {
                            @Override
                            public void onMoveCalculated(String aiMove) {
                                mainHandler.post(() -> {
                                    applyAIMove(aiMove);
                                });
                            }
                            
                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "❌ Personality move calculation failed: " + errorMessage);
                                // Fallback to regular engine move
                                generateRegularEngineMove();
                            }
                        });
                    } else {
                        // Use regular Stockfish move
                        generateRegularEngineMove();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error generating AI response", e);
                mainHandler.post(() -> {
                    if (thinkingProgressBar != null) {
                        thinkingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    
    /**
     * 🤖 Generate regular engine move (fallback)
     */
    private void generateRegularEngineMove() {
        if (gameViewModel.getGameRepository() != null) {
            gameViewModel.getGameRepository().calculateBestMove(new GameRepository.MoveCallback() {
                @Override
                public void onMoveCalculated(String aiMove) {
                    mainHandler.post(() -> {
                        applyAIMove(aiMove);
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "❌ Engine move calculation failed: " + errorMessage);
                    mainHandler.post(() -> {
                        if (thinkingProgressBar != null) {
                            thinkingProgressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(CompetitiveModeActivity.this, "AI failed to calculate move", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
    
    /**
     * 🤖 Apply AI move to the board with enhanced error recovery
     */
    private void applyAIMove(String aiMove) {
        Log.d(TAG, "🤖 Applying AI move: " + aiMove);
        
        executorService.execute(() -> {
            try {
                if (gameViewModel.getGameRepository() != null) {
                    // Get current position before attempting move
                    String currentFEN = gameViewModel.getGameRepository().getCurrentFEN();
                    Log.d(TAG, "📋 Current FEN before AI move: " + currentFEN);
                    
                    boolean success = gameViewModel.getGameRepository().makeMove(aiMove);
                    
                    mainHandler.post(() -> {
                        if (success) {
                            Log.d(TAG, "✅ AI move applied successfully: " + aiMove);
                            
                            // 🚨 CRITICAL FIX: Clear the AI pending flag
                            isAIMovePending = false;
                            
                            // Update the board from the new FEN
                            String newFEN = gameViewModel.getGameRepository().getCurrentFEN();
                            if (newFEN != null) {
                                chessBoardView.updateBoardFromFen(newFEN);
                                Log.d(TAG, "📋 New FEN after AI move: " + newFEN);
                            }
                            
                            // 🔄 CRITICAL FIX: Request evaluation after AI move
                            // This was missing and causing competitive mode evaluation issues
                            gameViewModel.requestPositionEvaluation();
                            Log.d(TAG, "🎯 Evaluation requested after AI move: " + aiMove);
                            
                            // Hide thinking indicator
                            if (thinkingProgressBar != null) {
                                thinkingProgressBar.setVisibility(View.GONE);
                            }
                            
                            // Generate master comment about their own move
                            generateMasterMoveReaction("I played " + aiMove + ". " + generateMoveSpecificReaction(aiMove));
                            
                        } else {
                            Log.e(TAG, "❌ AI move was REJECTED by engine: " + aiMove);
                            // 🚨 CRITICAL FIX: Clear flag even on failure
                            isAIMovePending = false;
                            handleFailedAIMove(aiMove, currentFEN);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "💥 Exception applying AI move: " + aiMove, e);
                mainHandler.post(() -> {
                    // 🚨 CRITICAL FIX: Clear flag on exception
                    isAIMovePending = false;
                    if (thinkingProgressBar != null) {
                        thinkingProgressBar.setVisibility(View.GONE);
                    }
                    handleFailedAIMove(aiMove, "unknown");
                });
            }
        });
    }
    
    /**
     * 🚨 Handle failed AI move with recovery mechanism
     */
    private void handleFailedAIMove(String failedMove, String currentFEN) {
        Log.e(TAG, "🚨 HANDLING FAILED AI MOVE: " + failedMove + " at position: " + currentFEN);
        
        // Hide thinking indicator
        if (thinkingProgressBar != null) {
            thinkingProgressBar.setVisibility(View.GONE);
        }
        
        // Try to recover by generating a different move
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🔄 Attempting AI move recovery...");
                
                // Force a fresh move calculation with different parameters
                if (gameViewModel.getGameRepository() != null) {
                    gameViewModel.getGameRepository().calculateBestMove(new GameRepository.MoveCallback() {
                        @Override
                        public void onMoveCalculated(String recoveryMove) {
                            mainHandler.post(() -> {
                                if (!recoveryMove.equals(failedMove)) {
                                    Log.d(TAG, "🔄 Recovery move generated: " + recoveryMove);
                                    applyAIMove(recoveryMove);
                                } else {
                                    Log.e(TAG, "💥 Recovery move is same as failed move - game state corrupted");
                                    displayMasterDialogue("I'm having trouble calculating my next move. The position is complex!");
                                }
                            });
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "💥 Recovery move calculation also failed: " + errorMessage);
                            mainHandler.post(() -> {
                                displayMasterDialogue("Something's wrong with my calculations. Let's continue from here.");
                            });
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "💥 Failed to recover from AI move failure", e);
                mainHandler.post(() -> {
                    displayMasterDialogue("I'm having technical difficulties. Your move!");
                });
            }
        });
    }

    /**
     * 🎭 Generate master reaction to player's move
     */
    private void generateMasterMoveReaction(String move) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🎭 Generating master reaction to move: " + move);
                
                // Hide thinking indicator
                mainHandler.post(() -> {
                    if (thinkingProgressBar != null) {
                        thinkingProgressBar.setVisibility(View.GONE);
                    }
                });
                
                // Generate move-specific reaction based on personality
                String reaction = generateMoveSpecificReaction(move);
                
                mainHandler.post(() -> {
                    displayMasterDialogue(reaction);
                    speakMasterDialogue(reaction);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to generate move reaction", e);
            }
        });
    }

    /**
     * 🎭 Generate move-specific reaction based on master personality
     */
    private String generateMoveSpecificReaction(String move) {
        String master = selectedMaster.toLowerCase();
        
        // Master-specific move reactions
        switch (master) {
            case "alekhine":
                return "Interesting choice! I see you're trying to control the center. Let me show you how it's really done.";
            case "tal":
                return "A solid move, but where's the fire? Chess is about passion and sacrifice!";
            case "fischer":
                return "Predictable. I've seen this pattern a thousand times. Try to surprise me.";
            case "carlsen":
                return "Good technique. But let me demonstrate why precision beats aggression.";
            case "kasparov":
                return "You're playing like a computer. Chess is art, not just calculation!";
            case "karpov":
                return "A reasonable move. I'll slowly squeeze you until you can't breathe.";
            default:
                return "Noted. Let's see how you handle my response.";
        }
    }

    /**
     * 🎮 Setup control buttons
     */
    private boolean setupControls() {
        try {
            Log.d(TAG, "🎮 Setting up controls...");
            
            // Surrender button
            surrenderButton.setOnClickListener(v -> showSurrenderDialog());
            
            // Pause button
            pauseButton.setOnClickListener(v -> toggleGamePause());
            
            // TTS toggle
            ttsToggleButton.setOnClickListener(v -> toggleTTS());
            
            // Voice comment button - start voice recording
            voiceCommentButton.setOnClickListener(v -> startVoiceComment());
            
            // Difficulty toggle button - switch between normal and max difficulty
            difficultyToggleButton.setOnClickListener(v -> toggleDifficulty());
            
            // Personality toggle button - enable/disable personality engine
            personalityToggleButton.setOnClickListener(v -> togglePersonality());
            
            // Voice settings button - configure voice options
            voiceSettingsButton.setOnClickListener(v -> showVoiceSettings());
            
            // Dismiss dialogue button - hide master dialogue overlay
            dismissDialogueButton.setOnClickListener(v -> {
                if (masterDialogueCard != null) {
                    masterDialogueCard.setVisibility(View.GONE);
                }
            });
            
            // Update button states
            updateButtonStates();
            
            // Request audio permissions for voice features
            requestAudioPermissions();
            
            Log.d(TAG, "✅ Controls setup complete");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to setup controls", e);
            return false;
        }
    }

    /**
     * 🔄 Update button states to reflect current settings
     */
    private void updateButtonStates() {
        try {
            // Update difficulty button
            difficultyToggleButton.setText(useMaxDifficulty ? "🔥 MAX" : "⚙️ Normal");
            difficultyToggleButton.setBackgroundTintList(getColorStateList(
                useMaxDifficulty ? android.R.color.holo_orange_dark : R.color.chess_light_square));
            
            // Update personality button
            boolean personalityEnabled = personalityEngine != null && personalityEngine.isPersonalityPlayEnabled();
            personalityToggleButton.setText(personalityEnabled ? "🎭 Personality On" : "🎭 Personality Off");
            personalityToggleButton.setBackgroundTintList(getColorStateList(
                personalityEnabled ? R.color.warning_amber : R.color.chess_light_square));
            
            // Update TTS button
            updateTTSButtonState();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to update button states", e);
        }
    }

    /**
     * 🏁 Start the competitive game
     */
    private void startCompetitiveGame() {
        Log.d(TAG, "🏁 Starting competitive game vs " + selectedMaster);
        
        try {
            // Start the game with proper ELO configuration
            gameViewModel.newGameWithConfiguration(playerColor, skillLevel, engineElo);
            
            // 🧠 PHASE 3: Start adaptive conversation monitoring
            if (adaptiveManager != null) {
                currentConversationId = "competitive_" + System.currentTimeMillis();
                AdaptiveConversationStrategyManager.ConversationContext initialContext = 
                    new AdaptiveConversationStrategyManager.ConversationContext(
                        "competitive_game_start",
                        "neutral", 
                        new java.util.ArrayList<>(),
                        "opening_phase",
                        0.5f
                    );
                
                adaptiveManager.startConversationMonitoring(
                    currentConversationId, selectedMaster, "player", initialContext
                );
                
                Log.d(TAG, "🧠 Phase 3: Started adaptive conversation monitoring for " + selectedMaster);
            }
            
            // Initialize dialogue card as gone to prevent layout space usage
            if (masterDialogueCard != null) {
                masterDialogueCard.setVisibility(View.GONE);
            }
            
            // Show initial master greeting with emotional intelligence
            if (masterDialogueTextView != null && masterDialogueCard != null) {
                String greeting = generateMasterGreetingWithAdaptiveLearning();
                displayMasterDialogue(greeting);
                speakMasterDialogue(greeting);
                updateEmotionIndicator("😎"); // Confident start
            }
            
            Log.d(TAG, "✅ Competitive game started successfully!");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to start competitive game", e);
            showErrorAndExit("Failed to start game: " + e.getMessage());
        }
    }

    /**
     * 🎭 Generate master-specific greeting for competitive mode
     */
    private String generateMasterGreeting() {
        String master = selectedMaster.toLowerCase();
        
        switch (master) {
            case "tal": 
                return "Welcome to competitive mode! I'm the Magician from Riga. Prepare for tactical fireworks!";
            case "fischer": 
                return "I'm Bobby Fischer. You're about to face the greatest chess player who ever lived. Good luck.";
            case "carlsen": 
                return "I'm Magnus Carlsen. Let's see if you can match the precision of a world champion.";
            case "kasparov": 
                return "Garry Kasparov here! Ready to experience the power of dynamic, aggressive chess?";
            case "alekhine": 
                return "Alexander Alekhine at your service. Prepare for a combinatorial masterclass!";
            case "karpov": 
                return "I'm Anatoly Karpov. I'll slowly squeeze your position until victory is inevitable.";
            case "kramnik": 
                return "Vladimir Kramnik here. Let's play some solid, strategic chess.";
            case "capablanca": 
                return "José Raúl Capablanca. I'll demonstrate the art of simple, elegant play.";
            case "morphy": 
                return "Paul Morphy here! Time for some brilliant classical combinations.";
            case "lasker": 
                return "Emanuel Lasker. I'll show you chess psychology at its finest.";
            case "anand": 
                return "Viswanathan Anand here. Ready for some rapid, intuitive play?";
            case "botvinnik": 
                return "Mikhail Botvinnik. Let's engage in deep, scientific chess analysis.";
            default: 
                return "Welcome to competitive mode! I'm " + formatMasterName(selectedMaster) + ". Let's play!";
        }
    }

    /**
     * 🧠 PHASE 3: Generate master greeting with adaptive emotional learning
     */
    private String generateMasterGreetingWithAdaptiveLearning() {
        if (adaptiveManager == null || currentConversationId == null) {
            // Fallback to basic greeting if adaptive system not available
            return generateMasterGreeting();
        }
        
        try {
            // Create conversation context for greeting
            AdaptiveConversationStrategyManager.ConversationContext context = 
                new AdaptiveConversationStrategyManager.ConversationContext(
                    "game_opening",
                    "neutral",
                    new java.util.ArrayList<>(),
                    "greeting_phase",
                    0.3f
                );
            
            // Get optimal emotional strategy for this greeting
            AdaptiveConversationStrategyManager.OptimalStrategyRecommendation strategy = 
                adaptiveManager.getOptimalStrategy(selectedMaster, "player", context);
            
            Log.d(TAG, String.format("🧠 Phase 3: Using adaptive strategy '%s' (confidence: %.2f) for greeting - %s",
                    strategy.strategy, strategy.confidence, strategy.reasoning));
            
            // Generate greeting based on learned strategy
            String baseGreeting = generateMasterGreeting();
            return enhanceGreetingWithStrategy(baseGreeting, strategy);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to generate adaptive greeting", e);
            return generateMasterGreeting(); // Fallback
        }
    }
    
    /**
     * 🧠 PHASE 3: Enhance greeting with adaptive strategy
     */
    private String enhanceGreetingWithStrategy(String baseGreeting, 
            AdaptiveConversationStrategyManager.OptimalStrategyRecommendation strategy) {
        
        // Apply strategy-specific enhancements to the greeting
        String enhancedGreeting = baseGreeting;
        
        // Add contextual adaptations based on learned preferences
        if (strategy.contextualAdaptations != null && !strategy.contextualAdaptations.isEmpty()) {
            for (String adaptation : strategy.contextualAdaptations.values()) {
                Log.d(TAG, "🎯 Applying greeting adaptation: " + adaptation);
            }
        }
        
        // For high-confidence strategies, add a confidence modifier
        if (strategy.confidence > 0.8f) {
            switch (strategy.strategy.toLowerCase()) {
                case "diplomatic_approach":
                    enhancedGreeting += " I look forward to our strategic battle.";
                    break;
                case "technical_analysis":
                    enhancedGreeting += " Let's see some precise, calculated play.";
                    break;
                case "dramatic_excitement":
                    enhancedGreeting += " This is going to be an incredible fight!";
                    break;
                case "encouraging_mentor":
                    enhancedGreeting += " I'll help you learn while we compete.";
                    break;
                default:
                    enhancedGreeting += " May the best player win.";
            }
        }
        
        return enhancedGreeting;
    }

    // 🚫 ANTI-SPAM: Prevent repetitive emotional reactions (FIXED: More responsive settings)
    private Float lastEmotionalEvaluation = null;
    private long lastEmotionalReactionTime = 0;
    private static final long EMOTIONAL_REACTION_COOLDOWN_MS = 25000; // Increased to 25 seconds to reduce spam
    private static final float EVALUATION_CHANGE_THRESHOLD = 0.8f; // Increased to 0.8 to require more significant changes

    /**
     * 🎭 Trigger emotional reaction to evaluation changes (FULL EMOTIONAL INTELLIGENCE)
     */
    private void triggerEmotionalReactionToEvaluation(Float evaluation) {
        Log.d(TAG, "🎭 triggerEmotionalReactionToEvaluation called with evaluation: " + evaluation + ", emotionalManager: " + (emotionalManager != null ? "INITIALIZED" : "NULL"));
        
        if (evaluation == null) {
            Log.w(TAG, "🚫 Emotional reaction skipped - evaluation is null");
            return;
        }
        
        if (emotionalManager == null) {
            Log.e(TAG, "🚫 Emotional reaction skipped - emotionalManager is null! This should not happen.");
            return;
        }
        
        try {
            // 🚫 ANTI-SPAM: Check for significant change and cooldown
            long currentTime = System.currentTimeMillis();
            boolean hasSignificantChange = false;
            
            if (lastEmotionalEvaluation == null) {
                // FIXED: First evaluation - be more responsive, react to any meaningful position
                hasSignificantChange = Math.abs(evaluation) > 1.5f; // Increased to 1.5 to reduce reactions to normal positions
                Log.d(TAG, "🎯 First emotional evaluation: " + evaluation + " (threshold: 1.5)");
            } else {
                // Check if evaluation has changed significantly
                float evaluationDelta = Math.abs(evaluation - lastEmotionalEvaluation);
                hasSignificantChange = evaluationDelta >= EVALUATION_CHANGE_THRESHOLD;
                Log.d(TAG, "🎯 Evaluation change: " + evaluationDelta + " (threshold: " + EVALUATION_CHANGE_THRESHOLD + ")");
            }
            
            // Check cooldown period
            boolean cooldownExpired = (currentTime - lastEmotionalReactionTime) >= EMOTIONAL_REACTION_COOLDOWN_MS;
            long timeSinceLastReaction = currentTime - lastEmotionalReactionTime;
            
            Log.d(TAG, "🔍 Emotional reaction check - Change: " + hasSignificantChange + 
                      ", Cooldown: " + cooldownExpired + 
                      " (time since last: " + (timeSinceLastReaction/1000) + "s/" + (EMOTIONAL_REACTION_COOLDOWN_MS/1000) + "s)");
            
            if (!hasSignificantChange || !cooldownExpired) {
                Log.d(TAG, "🚫 Emotional reaction blocked - Change: " + hasSignificantChange + ", Cooldown: " + cooldownExpired);
                return;
            }
            
            // Determine emotional context based on evaluation swing
            String emotionalTrigger;
            final String emotionEmoji; // Make final for lambda
            
            if (evaluation > 2.5f) {
                emotionalTrigger = "significant_disadvantage";
                emotionEmoji = "😤"; // Frustrated/determined
            } else if (evaluation > 1.0f) {
                emotionalTrigger = "slight_disadvantage"; 
                emotionEmoji = "🤔"; // Thoughtful
            } else if (evaluation < -2.5f) {
                emotionalTrigger = "significant_advantage";
                emotionEmoji = "😏"; // Confident
            } else if (evaluation < -1.0f) {
                emotionalTrigger = "slight_advantage";
                emotionEmoji = "🙂"; // Pleased
            } else {
                // Even with significant change, don't react to neutral positions unless very dramatic
                if (Math.abs(evaluation) < 0.3f) {
                    return;
                }
                emotionalTrigger = "position_shift";
                emotionEmoji = "🧐"; // Analytical
            }
            
            Log.d(TAG, "✅ Emotional reaction triggered for evaluation: " + evaluation + " (trigger: " + emotionalTrigger + ")");
            
            // Update tracking variables
            lastEmotionalEvaluation = evaluation;
            lastEmotionalReactionTime = currentTime;
            
            // Generate emotional response using the full system (ASYNC)
            generateEmotionalResponseAsync(emotionalTrigger, evaluation, (emotionalResponse) -> {
                // Update UI with emotion indicator on main thread
                mainHandler.post(() -> {
                    displayMasterDialogue(emotionalResponse);
                    updateEmotionIndicator(emotionEmoji);
                    speakMasterDialogue(emotionalResponse);
                });
            });
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to trigger emotional reaction", e);
        }
    }
    
    /**
     * 🎭 Generate emotional response using master personality
     */
    private String generateEmotionalResponse(String trigger, float evaluation) {
        // 🧠 PHASE 3: Try to use adaptive learning for emotional responses
        if (adaptiveManager != null && currentConversationId != null) {
            try {
                return generateAdaptiveEmotionalResponse(trigger, evaluation);
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to generate adaptive emotional response, using fallback", e);
            }
        }
        
        // Fallback to basic personality-based responses
        return generateBasicEmotionalResponse(trigger, evaluation);
    }

    /**
     * 🔧 ASYNC VERSION: Generate emotional response using async chain
     */
    private void generateEmotionalResponseAsync(String trigger, float evaluation, EmotionalResponseCallback callback) {
        // Try adaptive response first if manager is available
        if (adaptiveManager != null) {
            try {
                // NOTE: For now, falling back to basic response since adaptive requires more complex async handling
                generateBasicEmotionalResponseAsync(trigger, evaluation, callback);
                return;
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to generate adaptive emotional response, using fallback", e);
            }
        }
        
        // Fallback to basic personality-based responses (ASYNC)
        generateBasicEmotionalResponseAsync(trigger, evaluation, callback);
    }
    
    /**
     * 🧠 PHASE 3: Generate adaptive emotional response using learned strategies
     */
    private String generateAdaptiveEmotionalResponse(String trigger, float evaluation) {
        // Create conversation context for this emotional trigger
        AdaptiveConversationStrategyManager.ConversationContext context = 
            new AdaptiveConversationStrategyManager.ConversationContext(
                "position_evaluation_" + trigger,
                trigger.contains("advantage") ? "confident" : "challenged",
                new java.util.ArrayList<>(),
                "middlegame_phase",
                Math.abs(evaluation) / 10.0f  // Convert evaluation to intensity
            );
        
        // Get optimal strategy for this emotional situation
        AdaptiveConversationStrategyManager.OptimalStrategyRecommendation strategy = 
            adaptiveManager.getOptimalStrategy(selectedMaster, "player", context);
        
        Log.d(TAG, String.format("🧠 Phase 3: Using adaptive strategy '%s' for emotional response to %s",
                strategy.strategy, trigger));
        
        // Check if we should adapt mid-conversation
        float recentQuality = calculateRecentConversationQuality();
        AdaptiveConversationStrategyManager.StrategyAdaptation adaptation = 
            adaptiveManager.getConversationAdaptation(currentConversationId, strategy.strategy, recentQuality);
        
        if (adaptation.shouldAdapt) {
            Log.d(TAG, String.format("🔄 Adapting strategy: %s → %s (reason: %s)",
                    strategy.strategy, adaptation.newStrategy, adaptation.reason));
            strategy = new AdaptiveConversationStrategyManager.OptimalStrategyRecommendation(
                adaptation.newStrategy, adaptation.confidence, adaptation.reason,
                strategy.alternativeStrategies, strategy.contextualAdaptations);
        }
        
        // Generate response based on learned strategy
        String baseResponse = generateBasicEmotionalResponse(trigger, evaluation);
        return enhanceResponseWithStrategy(baseResponse, strategy, trigger);
    }
    
    /**
     * 🧠 PHASE 3: Calculate recent conversation quality for adaptation
     */
    private float calculateRecentConversationQuality() {
        // Simple quality metric based on recent interactions
        // In a real implementation, this could track user engagement, response timing, etc.
        return 0.7f; // Default reasonable quality
    }
    
    /**
     * 🧠 PHASE 3: Enhance response with adaptive strategy
     */
    private String enhanceResponseWithStrategy(String baseResponse, 
            AdaptiveConversationStrategyManager.OptimalStrategyRecommendation strategy, String trigger) {
        
        String enhanced = baseResponse;
        
        // Apply strategy-specific enhancements
        switch (strategy.strategy.toLowerCase()) {
            case "diplomatic_approach":
                enhanced = makeDiplomatic(enhanced);
                break;
            case "technical_analysis":
                enhanced = makeTechnical(enhanced);
                break;
            case "dramatic_excitement":
                enhanced = makeDramatic(enhanced);
                break;
            case "encouraging_mentor":
                enhanced = makeEncouraging(enhanced);
                break;
            case "competitive_edge":
                enhanced = makeCompetitive(enhanced);
                break;
        }
        
        return enhanced;
    }
    
    private String makeDiplomatic(String response) {
        return response.replace("!", ".") + " A fascinating position to analyze together.";
    }
    
    private String makeTechnical(String response) {
        return response + " Let me calculate the key variations here.";
    }
    
    private String makeDramatic(String response) {
        return response + " The tension is absolutely electric!";
    }
    
    private String makeEncouraging(String response) {
        return response + " You're playing well - keep thinking deeply.";
    }
    
    private String makeCompetitive(String response) {
        return response + " But I'm not giving you any easy breaks!";
    }
    
    /**
     * 🎭 Generate basic emotional response using master personality (original logic)
     */
    private void generateBasicEmotionalResponseAsync(String trigger, float evaluation, EmotionalResponseCallback callback) {
        String master = selectedMaster.toLowerCase();
        
        // 🚀 AI-POWERED RESPONSE GENERATION - No more hardcoded responses!
        try {
            String contextualPrompt = buildEmotionalResponsePrompt(trigger, evaluation, master);
            generateAIEmotionalResponseAsync(contextualPrompt, master, callback);
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to generate AI emotional response", e);
            // Emergency fallback - but this should rarely be used
            callback.onResponseGenerated(generateEmergencyFallbackResponse(trigger, master));
        }
    }

    // Keep sync version for backward compatibility
    private String generateBasicEmotionalResponse(String trigger, float evaluation) {
        String master = selectedMaster.toLowerCase();
        
        // 🚀 AI-POWERED RESPONSE GENERATION - No more hardcoded responses!
        try {
            String contextualPrompt = buildEmotionalResponsePrompt(trigger, evaluation, master);
            return generateAIEmotionalResponse(contextualPrompt, master);
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to generate AI emotional response", e);
            // Emergency fallback - but this should rarely be used
            return generateEmergencyFallbackResponse(trigger, master);
        }
    }
    
    /**
     * 🎭 Build contextual prompt for AI emotional response generation
     */
    private String buildEmotionalResponsePrompt(String trigger, float evaluation, String master) {
        StringBuilder prompt = new StringBuilder();
        
        // Current game context
        String currentFEN = getCurrentFEN();
        String moveHistory = getMoveHistoryContext();
        int moveNumber = getMoveNumber();
        
        prompt.append("You are ").append(master.substring(0, 1).toUpperCase()).append(master.substring(1))
              .append(" in a competitive chess game. ");
        
        // Emotional trigger context
        switch (trigger) {
            case "significant_disadvantage":
                prompt.append("You're in a difficult position (evaluation: ").append(evaluation)
                      .append("). React with your characteristic style - ");
                break;
            case "slight_disadvantage":
                prompt.append("You're slightly behind (evaluation: ").append(evaluation)
                      .append("). Show your fighting spirit - ");
                break;
            case "significant_advantage":
                prompt.append("You have a winning advantage (evaluation: ").append(evaluation)
                      .append("). Express your confidence - ");
                break;
            case "slight_advantage":
                prompt.append("You're slightly ahead (evaluation: ").append(evaluation)
                      .append("). Show measured optimism - ");
                break;
            default:
                prompt.append("The position is balanced. Share your perspective - ");
        }
        
        // Game context
        prompt.append("This is move ").append(moveNumber)
              .append(". Respond in 1-2 sentences with your distinctive personality and style. ");
        
        // Master-specific traits reminder
        switch (master) {
            case "alekhine":
                prompt.append("Be artistic, creative, and speak of combinations and harmony.");
                break;
            case "tal":
                prompt.append("Be dynamic, aggressive, and love tactical complications.");
                break;
            case "fischer":
                prompt.append("Be confident, direct, and focus on truth and precision.");
                break;
            case "carlsen":
                prompt.append("Be calm, practical, and focus on technique and precision.");
                break;
            case "kasparov":
                prompt.append("Be passionate, strategic, and emphasize dynamic play.");
                break;
        }
        
        return prompt.toString();
    }
    
    /**
     * 🔧 ASYNC VERSION: Generate AI emotional response using fine-tuned model
     */
    private void generateAIEmotionalResponseAsync(String prompt, String master, EmotionalResponseCallback callback) {
        try {
            // Use existing OpenAI service with master's fine-tuned model
            OpenAIService openAIService = OpenAIService.getInstance();
            
            // Get the fine-tuned model for this master
            FineTunedModelManager modelManager = FineTunedModelManager.getInstance(this);
            String modelId = modelManager.getModelIdForMaster(master);
            
            // System prompt for emotional reactions
            String systemPrompt = "You are a chess master providing quick emotional reactions during competitive play. " +
                                "Respond with your authentic personality in 1-2 sentences. Be natural and engaging.";
            
            // Generate response using master's specific model with variety management (ASYNC)
            openAIService.getChatCompletionWithModelAndVarietyAsync(
                modelId, systemPrompt, prompt, master, "competitive_emotional_reaction",
                new OpenAIService.ChatCompletionCallback() {
                    @Override
                    public void onComplete(String response) {
                        Log.d(TAG, "🧠 AI-generated emotional response for " + master + ": " + response);
                        callback.onResponseGenerated(response != null ? response.trim() : generateEmergencyFallbackResponse("default", master));
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "❌ Failed to generate AI response for " + master, e);
                        callback.onResponseGenerated(generateEmergencyFallbackResponse("default", master));
                    }
                }
            );
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to setup AI response for " + master, e);
            callback.onResponseGenerated(generateEmergencyFallbackResponse("default", master));
        }
    }

    /**
     * 🧠 Generate AI emotional response using fine-tuned model (SYNC VERSION - BACKUP ONLY)
     */
    private String generateAIEmotionalResponse(String prompt, String master) {
        try {
            // Use existing OpenAI service with master's fine-tuned model
            OpenAIService openAIService = OpenAIService.getInstance();
            
            // Get the fine-tuned model for this master
            FineTunedModelManager modelManager = FineTunedModelManager.getInstance(this);
            String modelId = modelManager.getModelIdForMaster(master);
            
            // System prompt for emotional reactions
            String systemPrompt = "You are a chess master providing quick emotional reactions during competitive play. " +
                                "Respond with your authentic personality in 1-2 sentences. Be natural and engaging.";
            
            // Generate response using master's specific model with variety management
            String response = openAIService.getChatCompletionWithModelAndVariety(
                modelId, systemPrompt, prompt, master, "competitive_emotional_reaction"
            );
            
            Log.d(TAG, "🧠 AI-generated emotional response for " + master + ": " + response);
            return response != null ? response.trim() : generateEmergencyFallbackResponse("default", master);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to generate AI response for " + master, e);
            throw e;
        }
    }
    
    /**
     * 📋 Get current FEN position for AI context
     */
    private String getCurrentFEN() {
        try {
            if (gameViewModel != null && gameViewModel.getCurrentFEN().getValue() != null) {
                return gameViewModel.getCurrentFEN().getValue();
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get current FEN from gameViewModel", e);
        }
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Starting position fallback
    }
    
    /**
     * 🔢 Get current move number for AI context
     */
    private int getMoveNumber() {
        try {
            if (gameViewModel != null && gameViewModel.getMoveHistory().getValue() != null) {
                List<String> moves = gameViewModel.getMoveHistory().getValue();
                return (moves.size() + 1) / 2; // Convert half-moves to full moves
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get move number from gameViewModel", e);
        }
        return 1; // Default to move 1
    }
    
    /**
     * 📜 Get recent move history for AI context
     */
    private String getMoveHistoryContext() {
        try {
            if (gameViewModel != null && gameViewModel.getMoveHistory().getValue() != null) {
                List<String> moves = gameViewModel.getMoveHistory().getValue();
                if (moves.isEmpty()) return "Game just started";
                
                // Get last 6-8 moves for context (3-4 full moves)
                int startIndex = Math.max(0, moves.size() - 8);
                List<String> recentMoves = moves.subList(startIndex, moves.size());
                
                // Format as "1.e4 e5 2.Nf3 Nc6" etc.
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < recentMoves.size(); i += 2) {
                    int moveNum = (startIndex + i) / 2 + 1;
                    formatted.append(moveNum).append(".");
                    formatted.append(recentMoves.get(i));
                    if (i + 1 < recentMoves.size()) {
                        formatted.append(" ").append(recentMoves.get(i + 1));
                    }
                    if (i + 2 < recentMoves.size()) {
                        formatted.append(" ");
                    }
                }
                return formatted.toString();
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get move history from gameViewModel", e);
        }
        return "Recent moves not available";
    }
    
    /**
     * 🚨 Emergency fallback for rare AI generation failures
     */
    private String generateEmergencyFallbackResponse(String trigger, String master) {
        // Very simple fallbacks - should rarely be used
        switch (trigger) {
            case "significant_disadvantage":
                return "The position demands careful calculation.";
            case "significant_advantage":
                return "The position looks promising for me.";
            default:
                return "An interesting position to analyze.";
        }
    }
    
    /**
     * 🎭 Update emotion indicator in dialogue header
     */
    private void updateEmotionIndicator(String emoji) {
        try {
            TextView emotionIndicator = findViewById(R.id.masterEmotionIndicator);
            if (emotionIndicator != null) {
                emotionIndicator.setText(emoji);
                Log.d(TAG, "🎭 Updated emotion indicator: " + emoji);
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to update emotion indicator", e);
        }
    }

    /**
     * 💬 Display master dialogue in UI
     */
    private void displayMasterDialogue(String dialogue) {
        if (masterDialogueTextView != null && masterDialogueCard != null) {
            masterDialogueTextView.setText(dialogue);
            masterDialogueCard.setVisibility(View.VISIBLE);
            
            // Auto-hide after 10 seconds
            mainHandler.postDelayed(() -> {
                if (masterDialogueCard != null) {
                    masterDialogueCard.setVisibility(View.GONE);
                }
            }, 10000);
        }
    }

    /**
     * 🗣️ Speak master dialogue with emotional voice (ENHANCED: Respects TTS toggle)
     */
    private void speakMasterDialogue(String dialogue) {
        try {
            // Check if TTS is enabled
            SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
            
            if (!ttsEnabled) {
                Log.d(TAG, "🔇 TTS disabled - skipping speech: " + dialogue);
                return;
            }
            
            Log.d(TAG, "🗣️ Speaking master dialogue: " + dialogue);
            
            // Use the proper TTS pattern from main game
            OpenAITTSService tts = TTSServiceManager.getOpenAITTSService(this);
            if (tts != null) {
                // Set master-specific voice settings
                TTSServiceManager.setUsageContext(this, "competitive_mode");
                
                // Set the current master for voice selection
                FineTunedModelManager.getInstance(this).setSelectedChessMaster(selectedMaster);
                
                // Speak with master-specific voice
                TTSServiceManager.speakWithSpecificMaster(this, selectedMaster, dialogue, 
                    new OpenAITTSService.OnSpeechCompletedListener() {
                        @Override
                        public void onSpeechCompleted() {
                            Log.d(TAG, "🗣️ Master dialogue speech completed");
                        }
                    });
                    
                Log.d(TAG, "✅ TTS request sent for master: " + selectedMaster);
            } else {
                Log.w(TAG, "⚠️ TTS service not available");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to speak master dialogue", e);
        }
    }

    /**
     * 🤔 Start voice comment from player
     */
    private void startVoiceComment() {
        Log.d(TAG, "🎤 Starting player voice comment...");
        
        if (recordService != null && isServiceBound) {
            try {
                // Start voice recording through the service
                recordService.startRecording();
                voiceCommentButton.setText("🎤 Listening...");
                voiceCommentButton.setEnabled(false);
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to start voice recording", e);
                Toast.makeText(this, "Voice recording failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "⚠️ Voice service not available");
            Toast.makeText(this, "Voice service not ready. Please wait...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 💭 Process player's voice input and generate master response
     */
    private void processPlayerVoiceInput(String transcribedText) {
        // DON'T generate duplicate response - the voice processing pipeline already handles this
        Log.d(TAG, "💭 Player voice input processed by voice pipeline: " + transcribedText);
    }

    /**
     * 🎭 Generate master response to player comment using personality
     */
    private String generateMasterResponseToComment(String playerComment) {
        // 🧠 PHASE 3: Try to use adaptive learning for comment responses
        if (adaptiveManager != null && currentConversationId != null) {
            try {
                return generateAdaptiveMasterResponseToComment(playerComment);
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to generate adaptive response to comment, using fallback", e);
            }
        }
        
        // Fallback to basic personality response
        return generateBasicMasterResponseToComment(playerComment);
    }
    
    /**
     * 🧠 PHASE 3: Generate adaptive master response to player comment
     */
    private String generateAdaptiveMasterResponseToComment(String playerComment) {
        // Create conversation context for player comment
        AdaptiveConversationStrategyManager.ConversationContext context = 
            new AdaptiveConversationStrategyManager.ConversationContext(
                "player_comment",
                "interactive",
                java.util.Arrays.asList("player_input", "voice_comment"),
                "dialogue_phase",
                0.6f
            );
        
        // Get optimal strategy for responding to this comment
        AdaptiveConversationStrategyManager.OptimalStrategyRecommendation strategy = 
            adaptiveManager.getOptimalStrategy(selectedMaster, "player", context);
        
        Log.d(TAG, String.format("🧠 Phase 3: Using adaptive strategy '%s' for comment response",
                strategy.strategy));
        
        // Generate base response
        String baseResponse = generateBasicMasterResponseToComment(playerComment);
        
        // Enhance with learned strategy
        return enhanceCommentResponseWithStrategy(baseResponse, strategy, playerComment);
    }
    
    /**
     * 🧠 PHASE 3: Enhance comment response with adaptive strategy
     */
    private String enhanceCommentResponseWithStrategy(String baseResponse, 
            AdaptiveConversationStrategyManager.OptimalStrategyRecommendation strategy, String playerComment) {
        
        String enhanced = baseResponse;
        
        // Apply strategy-specific enhancements based on comment type
        switch (strategy.strategy.toLowerCase()) {
            case "diplomatic_approach":
                enhanced = "I appreciate your perspective. " + enhanced;
                break;
            case "technical_analysis":
                enhanced = enhanced + " Let's focus on the concrete variations.";
                break;
            case "dramatic_excitement":
                enhanced = enhanced + " The passion for chess is infectious!";
                break;
            case "encouraging_mentor":
                enhanced = "That's good thinking! " + enhanced;
                break;
            case "competitive_edge":
                enhanced = enhanced + " But talk is cheap - show me on the board!";
                break;
        }
        
        // Add contextual adaptations if available
        if (strategy.contextualAdaptations != null && !strategy.contextualAdaptations.isEmpty()) {
            for (String adaptation : strategy.contextualAdaptations.values()) {
                Log.d(TAG, "🎯 Applying comment response adaptation: " + adaptation);
            }
        }
        
        return enhanced;
    }
    
    /**
     * 🎭 Generate basic master response to comment (original logic)
     */
    private String generateBasicMasterResponseToComment(String playerComment) {
        try {
            // Use fine-tuned model for master-specific response
            if (fineTunedModelManager != null) {
                String prompt = String.format(
                    "You are %s in a competitive chess match. The player just said: \"%s\". " +
                    "Respond in character with your typical personality and style. Keep it brief and engaging.",
                    formatMasterName(selectedMaster), playerComment
                );
                
                // This would normally call the fine-tuned model
                // For now, return a personality-based response
                return generatePersonalityResponse(playerComment);
            }
            
            return "Interesting perspective. Let's see how you play!";
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to generate master response", e);
            return "I hear you. Let's continue the game!";
        }
    }

    /**
     * 🎭 Generate personality-based response
     */
    private String generatePersonalityResponse(String playerComment) {
        String master = selectedMaster.toLowerCase();
        
        // Master-specific response patterns
        switch (master) {
            case "tal":
                return "Ah, a philosopher! But can you sacrifice like one? Let's see some magic on the board!";
            case "fischer":
                return "Talk is cheap. Show me your moves, not your words.";
            case "carlsen":
                return "I appreciate your insight. Now let's see if your play matches your analysis.";
            case "kasparov":
                return "Interesting observation! But chess is about calculation, not conversation.";
            case "karpov":
                return "Your comment is noted. I prefer to let my position speak for itself.";
            default:
                return "Well said! Now let's see how you handle the position.";
        }
    }

    /**
     * ⚙️ Toggle between normal and maximum difficulty
     */
    private void toggleDifficulty() {
        useMaxDifficulty = !useMaxDifficulty;
        
        try {
            configureDifficulty();
            updateButtonStates();
            
            String message = useMaxDifficulty ? 
                "🔥 MAX DIFFICULTY ENABLED! Good luck!" : 
                "⚙️ Normal difficulty restored";
            
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            
            // Show master reaction to difficulty change
            String reaction = useMaxDifficulty ?
                "You want my full strength? Be careful what you wish for!" :
                "Playing it safe? Perhaps a wise choice...";
            displayMasterDialogue(reaction);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to toggle difficulty", e);
            Toast.makeText(this, "Failed to change difficulty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎭 Toggle personality engine on/off
     */
    private void togglePersonality() {
        try {
            if (personalityEngine != null) {
                boolean currentState = personalityEngine.isPersonalityPlayEnabled();
                personalityEngine.setPersonalityPlayEnabled(!currentState);
                updateButtonStates();
                
                String message = !currentState ? 
                    "🎭 Personality engine ENABLED - Masters will play in their style!" : 
                    "🎭 Personality engine DISABLED - Pure engine play";
                
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                
                // CRITICAL FIX: Update GameRepository configuration when toggling
                if (gameViewModel != null && gameViewModel.getGameRepository() != null) {
                    gameViewModel.getGameRepository().configurePersonalityEngine(
                        selectedMaster, 0.3f, !currentState);
                    Log.d(TAG, "🔄 Updated GameRepository personality setting: " + !currentState);
                }
                
                // Master reaction
                String reaction = !currentState ?
                    "Now you'll face the real me! Prepare yourself!" :
                    "Removing my personality? How... sterile. But if you insist.";
                displayMasterDialogue(reaction);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to toggle personality", e);
            Toast.makeText(this, "Failed to toggle personality", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎙️ Show voice settings dialog (ENHANCED: Full control menu)
     */
    private void showVoiceSettings() {
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
        boolean alwaysListeningEnabled = prefs.getBoolean("always_listening_enabled", false);
        boolean voiceCommandsEnabled = prefs.getBoolean("voice_commands_enabled", true);
        
        String settingsMessage = String.format(
            "🎭 Master: %s\n\n" +
            "🔊 TTS (Master Voice): %s\n" +
            "👂 Always Listening: %s\n" +
            "🎤 Voice Commands: %s\n" +
            "⚡ STT Engine: Groq (Ultra-fast)\n\n" +
            "Voice Commands:\n" +
            "• \"What do you think?\"\n" +
            "• \"Explain that move\"\n" +
            "• \"Why did you play that?\"\n" +
            "• \"How is my position?\"",
            formatMasterName(selectedMaster),
            ttsEnabled ? "✅ Enabled" : "❌ Disabled",
            alwaysListeningEnabled ? "✅ Enabled" : "❌ Disabled", 
            voiceCommandsEnabled ? "✅ Enabled" : "❌ Disabled"
        );
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("🎙️ Voice Settings & Commands")
            .setMessage(settingsMessage)
            .setPositiveButton("Test Voice", (dialog, which) -> {
                if (ttsEnabled) {
                    speakMasterDialogue("Voice test successful! I'm ready for our competitive match.");
                } else {
                    Toast.makeText(this, "TTS is disabled. Enable it first to test voice.", Toast.LENGTH_SHORT).show();
                }
            })
            .setNeutralButton("Toggle Always-Listening", (dialog, which) -> {
                toggleAlwaysListening();
            })
            .setNegativeButton("Close", null)
            .show();
    }
    
    /**
     * 👂 Toggle always-listening feature
     */
    private void toggleAlwaysListening() {
        try {
            SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean currentState = prefs.getBoolean("always_listening_enabled", false);
            boolean newState = !currentState;
            
            prefs.edit().putBoolean("always_listening_enabled", newState).apply();
            
            if (newState) {
                // Enable always-listening
                if (voiceControlManager != null) {
                    voiceControlManager.startAlwaysListening();
                    Log.d(TAG, "👂 Always-listening ENABLED");
                    Toast.makeText(this, "👂 Always-listening ENABLED - Say 'Hey Coach' to interact", Toast.LENGTH_LONG).show();
                    displayMasterDialogue("I'm now listening for your voice commands. Just say 'Hey Coach' followed by your question!");
                } else {
                    Log.w(TAG, "⚠️ VoiceControlManager not available for always-listening");
                    Toast.makeText(this, "Voice control not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Disable always-listening
                if (voiceControlManager != null) {
                    voiceControlManager.stopAlwaysListening();
                    Log.d(TAG, "👂 Always-listening DISABLED");
                    Toast.makeText(this, "👂 Always-listening DISABLED - Use voice button for comments", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "⚠️ VoiceControlManager not available to stop always-listening");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to toggle always-listening", e);
            Toast.makeText(this, "Failed to toggle always-listening", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ⏸️ Toggle game pause
     */
    private void toggleGamePause() {
        isGamePaused = !isGamePaused;
        pauseButton.setText(isGamePaused ? "Resume" : "Pause");
        Log.d(TAG, "⏸️ Game " + (isGamePaused ? "paused" : "resumed"));
    }

    /**
     * 🔊 Toggle TTS (ENHANCED: Full functionality like spectator mode)
     */
    private void toggleTTS() {
        try {
            SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean currentTTSEnabled = prefs.getBoolean("tts_enabled", true);
            boolean newTTSEnabled = !currentTTSEnabled;
            
            // Save new setting
            prefs.edit().putBoolean("tts_enabled", newTTSEnabled).apply();
            
            // Update button appearance
            updateTTSButtonState();
            
            // Show feedback
            String message = newTTSEnabled ? 
                "🔊 Master voice ENABLED - " + formatMasterName(selectedMaster) + " will speak!" : 
                "🔇 Master voice DISABLED - Silent mode";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            
            // If disabled, stop any current TTS
            if (!newTTSEnabled) {
                OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(this);
                if (ttsService != null) {
                    ttsService.stopSpeaking();
                    Log.d(TAG, "🔇 Stopped current TTS playback");
                }
            } else {
                // Test the voice when enabled
                speakMasterDialogue("TTS enabled! I'll speak my thoughts during our match.");
            }
            
            Log.d(TAG, "🔊 TTS toggle: " + (newTTSEnabled ? "ENABLED" : "DISABLED"));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to toggle TTS", e);
            Toast.makeText(this, "TTS toggle failed", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 🔊 Update TTS button appearance based on current state
     */
    private void updateTTSButtonState() {
        try {
            if (ttsToggleButton != null) {
                SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
                boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
                
                if (ttsEnabled) {
                    ttsToggleButton.setText("🔊 TTS");
                    ttsToggleButton.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
                } else {
                    ttsToggleButton.setText("🔇 TTS");
                    ttsToggleButton.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to update TTS button state", e);
        }
    }

    /**
     * 🏳️ Show surrender confirmation dialog
     */
    private void showSurrenderDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Surrender?")
            .setMessage("Are you sure you want to surrender to " + formatMasterName(selectedMaster) + "?")
            .setPositiveButton("Surrender", (dialog, which) -> {
                Log.d(TAG, "🏳️ Player surrendered");
                endGame("surrender");
            })
            .setNegativeButton("Continue Fighting", null)
            .show();
    }

    /**
     * 🏁 End the competitive game
     */
    private void endGame(String result) {
        Log.d(TAG, "🏁 Ending competitive game - Result: " + result);
        
        // Generate final master comment based on result
        generateFinalMasterComment(result);
        
        // 🧠 PHASE 3: Record conversation outcome for adaptive learning
        recordConversationOutcomeForLearning(result);
        
        // Record the game result for adaptive learning
        recordGameResultForLearning(result);
        
        // Return to main activity after delay
        mainHandler.postDelayed(() -> {
            finish();
        }, 5000);
    }

    /**
     * 🧠 PHASE 3: Record conversation outcome for adaptive learning
     */
    private void recordConversationOutcomeForLearning(String gameResult) {
        if (adaptiveManager == null || currentConversationId == null) {
            return;
        }
        
        try {
            // Determine if the conversation was successful based on game outcome and quality
            boolean conversationSuccess = determineConversationSuccess(gameResult);
            float overallQuality = calculateOverallConversationQuality(gameResult);
            
            // Create quality metrics for the conversation
            CrossMasterEffectivenessTracker.ConversationQualityMetrics qualityMetrics = 
                new CrossMasterEffectivenessTracker.ConversationQualityMetrics(
                    0.8f,                     // engagement (good in competitive mode)
                    0.8f,                     // resonance (emotional appropriateness)
                    0.7f,                     // continuity (naturalness)
                    0.9f                      // novelty (personality consistency)
                );
            
            // Create conversation outcome
            AdaptiveConversationStrategyManager.ConversationOutcome outcome = 
                new AdaptiveConversationStrategyManager.ConversationOutcome(
                    "adaptive_learning",      // finalStrategy (we used adaptive throughout)
                    "competitive_game_" + gameResult,  // gameContext
                    "competitive_mode",       // conversationContext
                    conversationSuccess,      // success
                    qualityMetrics           // qualityMetrics
                );
            
            // Record the outcome for learning
            adaptiveManager.recordConversationOutcome(currentConversationId, outcome);
            
            Log.d(TAG, String.format("🧠 Phase 3: Recorded conversation outcome - Success: %s, Quality: %.2f", 
                    conversationSuccess, overallQuality));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to record conversation outcome for learning", e);
        }
    }
    
    /**
     * 🧠 PHASE 3: Determine if conversation was successful based on various factors
     */
    private boolean determineConversationSuccess(String gameResult) {
        // In competitive mode, success is measured by engagement and interaction quality
        // rather than just game outcome
        switch (gameResult.toLowerCase()) {
            case "surrender":
                return false; // Player gave up, might indicate poor experience
            case "win":
            case "loss":
                return true; // Game completed naturally
            case "draw":
                return true; // Good competitive game
            default:
                return true; // Default to successful unless explicitly negative
        }
    }
    
    /**
     * 🧠 PHASE 3: Calculate overall conversation quality for this session
     */
    private float calculateOverallConversationQuality(String gameResult) {
        float baseQuality = 0.7f; // Default good quality
        
        // Adjust based on game outcome
        switch (gameResult.toLowerCase()) {
            case "surrender":
                baseQuality -= 0.2f; // Reduce quality if player surrendered
                break;
            case "win":
            case "loss":
                baseQuality += 0.1f; // Bonus for completed games
                break;
            case "draw":
                baseQuality += 0.05f; // Slight bonus for draws (good competition)
                break;
        }
        
        // Ensure quality stays within bounds
        return Math.max(0.0f, Math.min(1.0f, baseQuality));
    }
    
    /**
     * 🧠 PHASE 3: Calculate conversation length for metrics
     */
    private int calculateConversationLength() {
        // Estimate conversation length based on game duration
        // In a real implementation, this could track actual dialogue exchanges
        return 5; // Default reasonable conversation length
    }

    /**
     * 💭 Generate final master comment (simplified)
     */
    private void generateFinalMasterComment(String result) {
        String finalComment;
        if ("surrender".equals(result)) {
            finalComment = "Good game! You fought well, but I've seen this pattern before. Better luck next time!";
        } else {
            finalComment = "What an interesting game! Thank you for the challenge.";
        }
        
        displayMasterDialogue(finalComment);
        speakMasterDialogue(finalComment);
    }

    /**
     * 📊 Record game result for adaptive learning (simplified)
     */
    private void recordGameResultForLearning(String result) {
        try {
            Log.d(TAG, "📊 Recording game result: " + result + " vs " + selectedMaster);
            // TODO: Implement adaptive learning recording when APIs are stable
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to record game result", e);
        }
    }

    /**
     * 📜 Update move history display with enhanced notation and symbols
     */
    private void updateMoveHistoryDisplay(List<String> moveHistory) {
        if (moveHistory != null && moveHistoryTextView != null) {
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < moveHistory.size(); i += 2) {
                int moveNumber = (i / 2) + 1;
                String whiteMove = moveHistory.get(i);
                String blackMove = i + 1 < moveHistory.size() ? moveHistory.get(i + 1) : "";
                
                // Format move number
                sb.append(String.format("%2d. ", moveNumber));
                
                // Add white move with annotation
                sb.append(formatMoveWithAnnotation(whiteMove));
                
                // Add black move if exists
                if (!blackMove.isEmpty()) {
                    sb.append("  ").append(formatMoveWithAnnotation(blackMove));
                } else if (i + 1 == moveHistory.size()) {
                    // If we're on the last move and it's white's turn, show it's black to move
                    sb.append("  ...");
                }
                
                sb.append("\n");
            }
            
            // Add game result if available
            String gameResult = getCurrentGameResult();
            if (!gameResult.isEmpty()) {
                sb.append("\n").append(gameResult);
            }
            
            moveHistoryTextView.setText(sb.toString());
            
            // Auto-scroll to bottom
            moveHistoryTextView.post(() -> {
                if (moveHistoryTextView.getParent() instanceof ScrollView) {
                    ScrollView scrollView = (ScrollView) moveHistoryTextView.getParent();
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }
    
    /**
     * 🎯 Format move with chess annotation symbols
     */
    private String formatMoveWithAnnotation(String move) {
        if (move == null || move.isEmpty()) {
            return "";
        }
        
        // For now, return the basic move - can be enhanced with evaluation-based annotations
        // TODO: Add real-time evaluation to determine !, ?, !!, ?? symbols
        return move;
    }
    
    /**
     * 🏁 Get current game result
     */
    private String getCurrentGameResult() {
        try {
            if (gameViewModel != null) {
                String winner = gameViewModel.getWinner().getValue();
                if (winner != null && !winner.isEmpty()) {
                    switch (winner.toLowerCase()) {
                        case "white": return "1-0";
                        case "black": return "0-1"; 
                        case "draw": return "½-½";
                        default: return "*";
                    }
                }
            }
            return "";
        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting game result", e);
            return "";
        }
    }
    
    /**
     * 🎮 Update game result display
     */
    private void updateGameResultDisplay(String result) {
        if (gameResultTextView != null) {
            if (result != null && !result.isEmpty()) {
                gameResultTextView.setText(result);
            } else {
                gameResultTextView.setText("In Progress");
            }
        }
    }

    /**
     * 🎭 Format master name for display
     */
    private String formatMasterName(String masterName) {
        return masterName.substring(0, 1).toUpperCase() + masterName.substring(1);
    }

    /**
     * 🎤 Request audio permissions for voice features
     */
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.RECORD_AUDIO}, 
                PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    /**
     * ❌ Show error and exit activity
     */
    private void showErrorAndExit(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        mainHandler.postDelayed(this::finish, 3000);
    }

    // Voice control implementation
    @Override
    public void onVoiceCommand(String command) {
        Log.d(TAG, "🎤 Voice command received: " + command);
        // TODO: Process voice commands in competitive mode
    }

    @Override
    public String getActivityType() {
        return "competitive_mode";
    }

    @Override
    public void onVoiceStatusChanged(AlwaysListeningService.VoiceStatus status) {
        Log.d(TAG, "🎤 Voice status changed: " + status);
        // TODO: Handle voice status changes
    }

    @Override
    public void onVoiceError(String error) {
        Log.e(TAG, "🎤 Voice error: " + error);
        // TODO: Handle voice errors
    }

    @Override
    public void onWakeWordDetected(String wakeWord) {
        Log.d(TAG, "🎤 Wake word detected: " + wakeWord);
        // TODO: Handle wake word detection
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
    protected void onDestroy() {
        super.onDestroy();
        
        // 🧠 PHASE 3: Cleanup adaptive learning session if needed
        if (adaptiveManager != null && currentConversationId != null) {
            try {
                // Record a final outcome if the game is ending unexpectedly
                recordConversationOutcomeForLearning("interrupted");
                Log.d(TAG, "🧠 Phase 3: Adaptive learning session cleaned up");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error cleaning up adaptive learning session", e);
            }
        }
        
        // Cleanup voice services
        if (isServiceBound && recordService != null) {
            try {
                unbindService(serviceConnection);
                isServiceBound = false;
                Log.d(TAG, "🎤 Voice service unbound");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error unbinding voice service", e);
            }
        }
        
        // Cleanup voice control manager
        if (voiceControlManager != null) {
            try {
                voiceControlManager.unregisterVoiceCommandListener();
                Log.d(TAG, "🎤 Voice command listener unregistered");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error unregistering voice listener", e);
            }
        }
        
        // Cleanup executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        // Reset spectator mode flag
        getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE)
            .edit().putBoolean("is_spectator_mode", false).apply();
        
        Log.d(TAG, "🏆 Competitive mode activity destroyed with full cleanup");
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
            Log.d(TAG, "🔧 DEBUG: Creating promotion dialog for move: " + baseMove);
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("♟️ Promote Pawn");
            builder.setMessage("Choose piece to promote to:");
            builder.setCancelable(false);
            
            // FIXED: Use button-based approach instead of setItems for better reliability
            builder.setPositiveButton("👑 Queen", (dialog, which) -> {
                String promotionMove = baseMove + "q";
                Log.d(TAG, "♟️ PAWN PROMOTION TO QUEEN: " + promotionMove);
                makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
            });
            
            builder.setNegativeButton("🏰 Rook", (dialog, which) -> {
                String promotionMove = baseMove + "r";
                Log.d(TAG, "♟️ PAWN PROMOTION TO ROOK: " + promotionMove);
                makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
            });
            
            builder.setNeutralButton("⛪ Bishop", (dialog, which) -> {
                String promotionMove = baseMove + "b";
                Log.d(TAG, "♟️ PAWN PROMOTION TO BISHOP: " + promotionMove);
                makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
            });
            
            // Add a fourth option via a separate dialog
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                // Add Knight option as a custom button
                dialog.setButton(AlertDialog.BUTTON3, "🐴 Knight", (d, w) -> {
                    String promotionMove = baseMove + "n";
                    Log.d(TAG, "♟️ PAWN PROMOTION TO KNIGHT: " + promotionMove);
                    makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
                });
            });
            
            Log.d(TAG, "🔧 DEBUG: About to show promotion dialog");
            dialog.show();
            Log.d(TAG, "🔧 DEBUG: Promotion dialog shown");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error showing promotion dialog", e);
            // Fallback to queen promotion
            String promotionMove = baseMove + "q";
            Log.d(TAG, "🔧 FALLBACK: Auto-promoting to queen: " + promotionMove);
            makePromotionMove(promotionMove, fromRow, fromCol, toRow, toCol, isPlayersTurn);
        }
    }

    /**
     * ♛ Execute the pawn promotion move
     */
    private void makePromotionMove(String promotionMove, int fromRow, int fromCol, int toRow, int toCol, boolean isPlayersTurn) {
        try {
            Log.d(TAG, "🎯 ATTEMPTING PROMOTION MOVE: " + promotionMove);
            Log.d(TAG, "📝 Player color: " + playerColor);
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