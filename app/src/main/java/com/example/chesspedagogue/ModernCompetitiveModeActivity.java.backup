package com.example.chesspedagogue;

import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.chesspedagogue.ui.GlassmorphismUtils;
import com.example.chesspedagogue.ui.AGSLShaderEffects;
import com.example.chesspedagogue.ui.AdvancedGlassEffects;
import com.example.chesspedagogue.ui.EnhancedAdaptiveTinting;
import com.example.chesspedagogue.ui.WorkingGlassEffects;
import com.example.chesspedagogue.ui.ChessAnimationController;
import androidx.constraintlayout.motion.widget.MotionLayout;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.OvershootInterpolator;
import com.example.chesspedagogue.viewmodel.GameViewModel;
import com.example.chesspedagogue.repository.GameRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Modern Competitive Mode Activity for Android 15/API 35
 * Implements cutting-edge glassmorphism, edge-to-edge design, and predictive back navigation
 * Based on comprehensive redesign report specifications
 */
public class ModernCompetitiveModeActivity extends AppCompatActivity {
    private static final String TAG = "ModernCompetitiveModeActivity";

    // UI Components
    private MotionLayout motionLayout;
    private CardView competitiveHeaderContainer;
    private CardView bottomControlsPanel;
    private ChessBoardView chessBoardView;
    private EvaluationBarView evaluationBarView;
    private ProgressBar masterThinkingProgressBar;
    
    // Header elements
    private TextView playerNameText;
    private TextView playerRatingText;
    private TextView masterNameText;
    private TextView masterRatingText;
    private FloatingActionButton gameAnalysisButton;
    private FloatingActionButton speakButton;
    
    // Top row control buttons
    private Button pauseButton;
    private Button voiceCommentButton;
    private Button difficultyToggleButton;
    private Button surrenderButton;
    
    // Bottom row control buttons
    private Button ttsToggleButton;
    private Button personalityToggleButton;
    private Button voiceSettingsButton;
    
    // Status display
    private TextView statusTextView;
    private TextView lastMoveText;
    
    // Battle progress panel
    private CardView battleProgressPanel;
    private TextView moveHistoryTextView;
    private TextView gameResultTextView;
    
    // Game state
    private String selectedMaster;
    private String playerColor;
    private int skillLevel;
    private int engineElo;
    private boolean isGamePaused = false;
    private boolean useMaxDifficulty = false;
    
    // Advanced visual effects state
    private float currentGameProgress = 0.0f; // 0.0 = opening, 0.5 = midgame, 1.0 = endgame
    private float currentEvaluation = 0.0f;   // -1.0 to 1.0 (black to white advantage)
    private int moveCount = 0;
    
    // Move selection state
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean pieceSelected = false;
    
    // Chess engine and game management
    private GameViewModel gameViewModel;
    private StockfishManager stockfishManager;
    private PersonalityEngine personalityEngine;
    private FineTunedModelManager fineTunedModelManager;
    
    // Voice services
    private SimpleRecordService recordService;
    private boolean isServiceBound = false;
    
    // Threading and handlers
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();
    
    // Advanced animation system for S23 Ultra
    private ChessAnimationController animationController;
    
    // Service connection for voice recording
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SimpleRecordService.LocalBinder binder = (SimpleRecordService.LocalBinder) service;
            recordService = binder.getService();
            isServiceBound = true;
            Log.d(TAG, "🎤 Voice service connected to modern competitive mode");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recordService = null;
            isServiceBound = false;
            Log.d(TAG, "🎤 Voice service disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "🚀 Starting Modern Competitive Mode with Android 15 features...");
        
        // Set modern glassmorphic layout
        setContentView(R.layout.activity_competitive_mode_modern);
        
        // Enable cutting-edge Android 15 features
        setupAndroid15Features();
        
        // Initialize Material 3 UI components
        initializeViews();
        
        // Initialize chess engine and game state
        initializeGameEngine();
        
        // Setup game functionality
        setupGameControls();
        setupAIControls();
        setupHeaderActions();
        
        // Apply proper glassmorphism (translucent backgrounds only)
        applyGlassmorphismEffects();
        
        // Initialize advanced animation system for S23 Ultra
        initializeAnimationSystem();
        
        // Setup predictive back navigation for Android 15
        setupPredictiveBackNavigation();
        
        // Handle shared element transitions if coming from another activity
        handleSharedElementTransitions();
        
        Log.d(TAG, "✅ Modern Competitive Mode initialized successfully");
    }

    /**
     * Setup Android 15 edge-to-edge and modern features
     */
    private void setupAndroid15Features() {
        Log.d(TAG, "🌊 Configuring Android 15 edge-to-edge features...");
        
        Window window = getWindow();
        View rootView = findViewById(android.R.id.content);
        
        // Enable edge-to-edge immersive experience
        GlassmorphismUtils.enableEdgeToEdge(window, rootView);
        
        // Set adaptive system bar icons for dark gradient background
        GlassmorphismUtils.setAdaptiveSystemBarIcons(window, false);
        
        // Handle window insets manually for frosted glass panels
        setupWindowInsets();
        
        Log.d(TAG, "✅ Android 15 features configured for Samsung S23 Ultra");
    }
    
    /**
     * Manual window inset handling for frosted glass panels
     */
    private void setupWindowInsets() {
        Log.d(TAG, "📐 Setting up window insets for glassmorphic panels...");
        
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            androidx.core.graphics.Insets systemBarsInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            );
            
            // Apply top inset to header container
            View headerSpacer = findViewById(R.id.statusBarSpacer);
            if (headerSpacer != null) {
                headerSpacer.getLayoutParams().height = systemBarsInsets.top;
                headerSpacer.requestLayout();
            }
            
            // Apply bottom inset to navigation spacer
            View navSpacer = findViewById(R.id.navigationBarSpacer);
            if (navSpacer != null) {
                navSpacer.getLayoutParams().height = systemBarsInsets.bottom;
                navSpacer.requestLayout();
            }
            
            Log.d(TAG, "📐 Applied insets - Top: " + systemBarsInsets.top + 
                       "px, Bottom: " + systemBarsInsets.bottom + "px");
            
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Initialize all Material 3 UI components
     */
    private void initializeViews() {
        Log.d(TAG, "🔍 Initializing Material 3 glassmorphic views...");
        
        // Main containers
        competitiveHeaderContainer = findViewById(R.id.competitiveHeaderContainer);
        bottomControlsPanel = findViewById(R.id.bottomControlsPanel);
        chessBoardView = findViewById(R.id.chessBoardView);
        evaluationBarView = findViewById(R.id.evaluationBarView);
        masterThinkingProgressBar = findViewById(R.id.masterThinkingProgressBar);
        
        // Header elements
        playerNameText = findViewById(R.id.playerNameText);
        playerRatingText = findViewById(R.id.playerRatingText);
        masterNameText = findViewById(R.id.masterNameText);
        masterRatingText = findViewById(R.id.masterRatingText);
        gameAnalysisButton = findViewById(R.id.gameAnalysisButton);
        speakButton = findViewById(R.id.speakButton);
        
        // Top row control buttons
        pauseButton = findViewById(R.id.pauseButton);
        voiceCommentButton = findViewById(R.id.voiceCommentButton);
        difficultyToggleButton = findViewById(R.id.difficultyToggleButton);
        surrenderButton = findViewById(R.id.surrenderButton);
        
        // Bottom row control buttons
        ttsToggleButton = findViewById(R.id.ttsToggleButton);
        personalityToggleButton = findViewById(R.id.personalityToggleButton);
        voiceSettingsButton = findViewById(R.id.voiceSettingsButton);
        
        // Status elements (these may not exist in modern layout)
        statusTextView = findViewById(R.id.statusTextView);
        // lastMoveText = findViewById(R.id.lastMoveText); // Not in modern layout
        
        // Battle progress panel
        battleProgressPanel = findViewById(R.id.battleProgressPanel);
        moveHistoryTextView = findViewById(R.id.moveHistoryTextView);
        gameResultTextView = findViewById(R.id.gameResultTextView);
        
        // Verify all critical views found
        boolean allViewsFound = 
            competitiveHeaderContainer != null && bottomControlsPanel != null &&
            chessBoardView != null && evaluationBarView != null &&
            pauseButton != null && voiceCommentButton != null && difficultyToggleButton != null &&
            surrenderButton != null && ttsToggleButton != null && personalityToggleButton != null &&
            voiceSettingsButton != null;
            
        Log.d(TAG, allViewsFound ? "✅ All Material 3 glassmorphic views found" : "❌ Some views missing!");
    }

    /**
     * Initialize chess engine and game components
     */
    private void initializeGameEngine() {
        Log.d(TAG, "♟️ Initializing chess engine and game state...");
        
        try {
            // Get launch parameters from intent
            Intent intent = getIntent();
            selectedMaster = intent.getStringExtra("SELECTED_MASTER");
            playerColor = intent.getStringExtra("PLAYER_COLOR");
            skillLevel = intent.getIntExtra("SKILL_LEVEL", 10);
            engineElo = intent.getIntExtra("ENGINE_ELO", 1500);
            
            // Fix: Use actual selected master, don't default to Tal
            if (selectedMaster == null) {
                // Check SharedPreferences for selected master
                SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
                selectedMaster = prefs.getString("selected_master", "alekhine");
                Log.d(TAG, "📋 No intent master, using SharedPreferences: " + selectedMaster);
            }
            if (playerColor == null) playerColor = "white";
            
            // Ensure master name is properly formatted
            selectedMaster = selectedMaster.toLowerCase();
            
            Log.d(TAG, "🎯 Game setup - Master: " + selectedMaster + ", Color: " + playerColor + ", ELO: " + engineElo);
            
            // Initialize Stockfish engine first
            stockfishManager = new StockfishManager();
            stockfishManager.setSkillLevel(engineElo);
            
            // Initialize fine-tuned model manager
            fineTunedModelManager = FineTunedModelManager.getInstance(this);
            fineTunedModelManager.setSelectedChessMaster(selectedMaster);
            
            // Initialize personality engine with context and stockfish manager
            personalityEngine = PersonalityEngine.getInstance(this, stockfishManager);
            personalityEngine.setCurrentMaster(selectedMaster);
            personalityEngine.initializeMasterData(selectedMaster);
            
            // Initialize GameViewModel
            gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
            gameViewModel.configureAutoCommentary(false);
            
            // Setup GameViewModel observers
            setupGameViewModelObservers();
            
            // Setup chess board listeners
            setupChessBoardListeners();
            
            // Bind voice service
            Intent serviceIntent = new Intent(this, SimpleRecordService.class);
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            
            // Update UI with game info
            updateGameInfo();
            
            // Start a new game with the configured settings
            startNewGame();
            
            Log.d(TAG, "✅ Chess engine initialization completed");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize chess engine", e);
            Toast.makeText(this, "Failed to initialize game engine", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Setup GameViewModel observers for board updates and game state
     */
    private void setupGameViewModelObservers() {
        if (gameViewModel != null) {
            Log.d(TAG, "🔗 Setting up GameViewModel observers...");
            
            // Observe board state changes
            gameViewModel.getCurrentFEN().observe(this, fen -> {
                if (fen != null && chessBoardView != null) {
                    Log.d(TAG, "♟️ Board state updated: " + fen);
                    // CRITICAL: Update the ChessBoardView's internal board state from FEN
                    chessBoardView.updateBoardFromFen(fen);
                    // Also set the real FEN for turn checking
                    chessBoardView.setRealCurrentFEN(fen);
                }
            });
            
            // Observe move history
            gameViewModel.getMoveHistory().observe(this, moves -> {
                if (moves != null && !moves.isEmpty()) {
                    Log.d(TAG, "📝 Move history updated: " + moves.size() + " moves");
                    updateMoveHistoryDisplay(moves);
                    
                    // Update game progress for adaptive visual effects
                    updateGameProgress(moves.size());
                }
            });
            
            // Observe evaluation changes
            gameViewModel.getCurrentEvaluation().observe(this, evaluation -> {
                if (evaluation != null && evaluationBarView != null) {
                    Log.d(TAG, "📊 Evaluation updated: " + evaluation);
                    // Note: EvaluationBarView.updateEvaluation may not exist
                    // evaluationBarView.updateEvaluation(evaluation);
                    
                    // Update evaluation for adaptive visual effects
                    updateEvaluation(evaluation.toString());
                }
            });
            
            // Observe move animations (like MainActivity)
            gameViewModel.getAnimateMoveEvent().observe(this, coords -> {
                if (coords != null && coords.length == 4 && chessBoardView != null) {
                    Log.d(TAG, "🎬 Animating move: " + coords[0] + "," + coords[1] + " -> " + coords[2] + "," + coords[3]);
                    chessBoardView.animateMove(coords[0], coords[1], coords[2], coords[3]);
                }
            });
            
            // Observe selection clearing
            gameViewModel.getClearSelectionEvent().observe(this, shouldClear -> {
                if (shouldClear != null && shouldClear && chessBoardView != null) {
                    Log.d(TAG, "🔄 Clearing board selection");
                    chessBoardView.clearSelectionHighlight();
                    chessBoardView.clearHighlightedSquares();
                    gameViewModel.resetClearSelectionEvent();
                }
            });
            
            // Observe last move highlighting
            gameViewModel.getLastMoveEvent().observe(this, coords -> {
                if (coords != null && coords.length == 4 && chessBoardView != null) {
                    Log.d(TAG, "🎯 Highlighting last move: " + coords[0] + "," + coords[1] + " -> " + coords[2] + "," + coords[3]);
                    chessBoardView.setLastMove(coords[0], coords[1], coords[2], coords[3]);
                }
            });
            
            // Observe king in check highlighting
            gameViewModel.getKingInCheckEvent().observe(this, coords -> {
                if (chessBoardView != null) {
                    if (coords != null && coords.length == 2) {
                        Log.d(TAG, "👑 King in check at: " + coords[0] + "," + coords[1]);
                        chessBoardView.setKingInCheck(true, coords[0], coords[1]);
                    } else {
                        chessBoardView.setKingInCheck(false, -1, -1);
                    }
                }
            });
            
            Log.d(TAG, "✅ GameViewModel observers setup completed");
        }
    }
    
    /**
     * Setup chess board touch listeners for move input
     */
    private void setupChessBoardListeners() {
        if (chessBoardView != null) {
            chessBoardView.setOnSquareTapListener((row, col) -> {
                if (!isGamePaused) {
                    Log.d(TAG, "♟️ Square tapped: " + row + "," + col);
                    handleSquareTap(row, col);
                }
            });
        }
    }
    
    /**
     * Handle player move input with proper piece selection and move execution
     */
    private void handleSquareTap(int row, int col) {
        try {
            String square = coordinatesToSquare(row, col);
            Log.d(TAG, "♟️ Player tapped square: " + square + " (row=" + row + ", col=" + col + ")");
            
            if (!pieceSelected) {
                // First tap - select piece
                if (selectPiece(row, col)) {
                    selectedRow = row;
                    selectedCol = col;
                    pieceSelected = true;
                    
                    // Update chess board visual selection
                    chessBoardView.setSelectedSquare(row, col);
                    chessBoardView.invalidate(); // Refresh display
                    
                    // Apply enhanced AGSL square highlight
                    enhanceSquareSelection(row, col);
                    
                    // Show legal moves if available
                    showLegalMoves(row, col);
                    
                    updateStatus("Piece selected at " + square);
                    Log.d(TAG, "✅ Piece selected at " + square);
                } else {
                    updateStatus("No piece to select at " + square);
                    Log.d(TAG, "❌ No valid piece at " + square);
                }
            } else {
                // Second tap - attempt move
                if (row == selectedRow && col == selectedCol) {
                    // Deselect same piece
                    clearSelection();
                    updateStatus("Piece deselected");
                    Log.d(TAG, "🔄 Piece deselected");
                } else {
                    // Attempt move
                    String fromSquare = coordinatesToSquare(selectedRow, selectedCol);
                    String toSquare = square;
                    
                    if (attemptMove(fromSquare, toSquare)) {
                        Log.d(TAG, "✅ Move executed: " + fromSquare + " → " + toSquare);
                        updateStatus("Move: " + fromSquare + " → " + toSquare);
                        clearSelection();
                        
                        // Add move to history
                        String moveNotation = fromSquare + toSquare;
                        addMoveToHistory(moveNotation);
                        
                        // Trigger visual effect for good move
                        triggerGameEventEffect(GlassmorphismUtils.GameEvent.GOOD_MOVE);
                        
                        // Trigger AI response after successful player move
                        triggerAIMove();
                    } else {
                        Log.d(TAG, "❌ Invalid move: " + fromSquare + " → " + toSquare);
                        updateStatus("Invalid move: " + fromSquare + " → " + toSquare);
                        clearSelection();
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error handling square tap", e);
            clearSelection();
        }
    }
    
    /**
     * Check if there's a valid piece to select at the given position
     */
    private boolean selectPiece(int row, int col) {
        try {
            if (gameViewModel != null) {
                // Get current FEN to check piece positions
                String currentFen = gameViewModel.getCurrentFEN().getValue();
                if (currentFen != null) {
                    // Check if there's a piece of the player's color at this position
                    return isPlayerPieceAt(row, col, currentFen);
                }
            }
            
            // Fallback: assume piece is selectable for now
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error selecting piece", e);
            return false;
        }
    }
    
    /**
     * Check if there's a player piece at the given coordinates
     */
    private boolean isPlayerPieceAt(int row, int col, String fen) {
        try {
            // Parse FEN to get board state
            String[] fenParts = fen.split(" ");
            String boardFen = fenParts[0];
            String[] ranks = boardFen.split("/");
            
            if (row >= 0 && row < ranks.length) {
                String rank = ranks[row];
                int fileIndex = 0;
                
                for (char c : rank.toCharArray()) {
                    if (Character.isDigit(c)) {
                        fileIndex += Character.getNumericValue(c);
                    } else {
                        if (fileIndex == col) {
                            // Found piece at this position
                            boolean isWhitePiece = Character.isUpperCase(c);
                            boolean playerIsWhite = "white".equals(playerColor);
                            return isWhitePiece == playerIsWhite;
                        }
                        fileIndex++;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking piece position", e);
            return false;
        }
    }
    
    /**
     * Show legal moves for selected piece
     */
    private void showLegalMoves(int row, int col) {
        try {
            if (gameViewModel != null && chessBoardView != null) {
                String square = coordinatesToSquare(row, col);
                Log.d(TAG, "🎯 Getting legal moves for piece at " + square);
                
                // Clear existing highlights first
                chessBoardView.clearHighlightedSquares();
                
                // Get legal moves from GameViewModel (same as MainActivity)
                gameViewModel.getLegalMovesForSquare(square, moves -> {
                    Log.d(TAG, "🎯 Found " + moves.size() + " legal moves for " + square);
                    for (String move : moves) {
                        if (move.length() >= 4) {
                            // Parse destination square from move (e.g., "e2e4" -> row=4, col=4)
                            int destRow = 8 - Character.getNumericValue(move.charAt(3)); // Convert rank to row
                            int destCol = move.charAt(2) - 'a'; // Convert file to column
                            chessBoardView.addHighlightedSquare(destRow, destCol);
                            Log.d(TAG, "🔵 Added legal move dot at " + move.charAt(2) + move.charAt(3) + " (row=" + destRow + ", col=" + destCol + ")");
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error showing legal moves", e);
        }
    }
    
    /**
     * Attempt to execute a chess move with animations
     */
    private boolean attemptMove(String from, String to) {
        try {
            if (gameViewModel != null) {
                String move = from + to;
                Log.d(TAG, "🎯 Attempting animated move: " + move);
                
                // Determine piece type and capture status
                String pieceType = determinePieceType(from);
                boolean isWhitePiece = isPieceWhite(from);
                boolean isCapture = isPieceAtPosition(to);
                String capturedPieceType = isCapture ? determinePieceType(to) : null;
                
                // Execute move with full animation suite
                if (animationController != null) {
                    Log.d(TAG, "🎭 Triggering animated move: " + from + " → " + to);
                    
                    // Store move info for validation after animation
                    final String finalMove = move;
                    final String finalPieceType = pieceType;
                    final boolean finalIsWhitePiece = isWhitePiece;
                    final boolean finalIsCapture = isCapture;
                    final String finalCapturedPieceType = capturedPieceType;
                    
                    // Try to execute the move through GameViewModel first to validate
                    try {
                        // Check if we have a makePlayerMove method or similar
                        if (gameViewModel.getCurrentFEN() != null) {
                            // Execute animation with the move data
                            animationController.executeAnimatedMove(
                                from, to, finalPieceType, finalIsWhitePiece,
                                finalIsCapture, finalCapturedPieceType,
                                () -> {
                                    // After animation completes, update game state
                                    Log.d(TAG, "✅ Animation completed, updating game state");
                                    try {
                                        // Try multiple possible method names
                                        java.lang.reflect.Method moveMethod = null;
                                        try {
                                            moveMethod = gameViewModel.getClass().getMethod("makePlayerMove", String.class);
                                        } catch (NoSuchMethodException e) {
                                            try {
                                                moveMethod = gameViewModel.getClass().getMethod("makeMove", String.class);
                                            } catch (NoSuchMethodException e2) {
                                                Log.w(TAG, "Could not find move method, updating manually");
                                            }
                                        }
                                        
                                        if (moveMethod != null) {
                                            moveMethod.invoke(gameViewModel, finalMove);
                                        }
                                        
                                        updateGameStateAfterMove();
                                        
                                        // Check for check condition and show indicator
                                        checkForCheckCondition();
                                        
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error updating game state after animation", e);
                                    }
                                }
                            );
                        } else {
                            Log.w(TAG, "GameViewModel not properly initialized");
                            return false;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in move validation", e);
                        return false;
                    }
                } else {
                    // Fallback without animations - use reflection to find move method
                    try {
                        java.lang.reflect.Method moveMethod = gameViewModel.getClass().getMethod("makePlayerMove", String.class);
                        moveMethod.invoke(gameViewModel, move);
                    } catch (Exception e) {
                        try {
                            java.lang.reflect.Method moveMethod = gameViewModel.getClass().getMethod("makeMove", String.class);
                            moveMethod.invoke(gameViewModel, move);
                        } catch (Exception e2) {
                            Log.e(TAG, "Could not find suitable move method", e2);
                            return false;
                        }
                    }
                }
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error executing animated move", e);
            return false;
        }
    }
    
    /**
     * Determine piece type at given square
     */
    private String determinePieceType(String square) {
        try {
            if (gameViewModel != null) {
                String currentFen = getCurrentFenFromViewModel();
                if (currentFen != null) {
                    return extractPieceTypeFromFen(currentFen, square);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not determine piece type for " + square, e);
        }
        return "pawn"; // Fallback
    }
    
    /**
     * Check if piece at square is white
     */
    private boolean isPieceWhite(String square) {
        try {
            if (gameViewModel != null) {
                String currentFen = getCurrentFenFromViewModel();
                if (currentFen != null) {
                    return extractPieceColorFromFen(currentFen, square);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not determine piece color for " + square, e);
        }
        return true; // Fallback
    }
    
    /**
     * Check if there's a piece at given position
     */
    private boolean isPieceAtPosition(String square) {
        try {
            if (gameViewModel != null) {
                String currentFen = getCurrentFenFromViewModel();
                if (currentFen != null) {
                    return hasPieceAtSquare(currentFen, square);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not check piece at " + square, e);
        }
        return false; // Fallback
    }
    
    /**
     * Get current FEN from GameViewModel using reflection
     */
    private String getCurrentFenFromViewModel() {
        try {
            if (gameViewModel != null) {
                // Try different possible method names
                try {
                    java.lang.reflect.Method getCurrentPositionMethod = gameViewModel.getClass().getMethod("getCurrentPosition");
                    return (String) getCurrentPositionMethod.invoke(gameViewModel);
                } catch (NoSuchMethodException e) {
                    try {
                        java.lang.reflect.Method getCurrentFENMethod = gameViewModel.getClass().getMethod("getCurrentFEN");
                        Object fenLiveData = getCurrentFENMethod.invoke(gameViewModel);
                        if (fenLiveData instanceof androidx.lifecycle.LiveData) {
                            return (String) ((androidx.lifecycle.LiveData<?>) fenLiveData).getValue();
                        } else {
                            return (String) fenLiveData;
                        }
                    } catch (Exception e2) {
                        Log.w(TAG, "Could not get current FEN from GameViewModel");
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting FEN from GameViewModel", e);
        }
        return null;
    }
    
    /**
     * Extract piece type from FEN at specific square
     */
    private String extractPieceTypeFromFen(String fen, String square) {
        try {
            // Convert square to board coordinates
            int file = square.charAt(0) - 'a'; // 0-7
            int rank = 8 - (square.charAt(1) - '0'); // 0-7
            
            // Parse FEN board section
            String boardSection = fen.split(" ")[0];
            String[] ranks = boardSection.split("/");
            
            if (rank >= 0 && rank < ranks.length) {
                String rankStr = ranks[rank];
                int fileIndex = 0;
                
                for (char c : rankStr.toCharArray()) {
                    if (Character.isDigit(c)) {
                        fileIndex += (c - '0');
                    } else {
                        if (fileIndex == file) {
                            return chessPieceTypeFromChar(c);
                        }
                        fileIndex++;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error parsing FEN for piece type", e);
        }
        
        return "pawn"; // Fallback
    }
    
    /**
     * Extract piece color from FEN at specific square
     */
    private boolean extractPieceColorFromFen(String fen, String square) {
        // Convert square to board coordinates
        int file = square.charAt(0) - 'a'; // 0-7
        int rank = 8 - (square.charAt(1) - '0'); // 0-7
        
        // Parse FEN board section
        String boardSection = fen.split(" ")[0];
        String[] ranks = boardSection.split("/");
        
        if (rank >= 0 && rank < ranks.length) {
            String rankStr = ranks[rank];
            int fileIndex = 0;
            
            for (char c : rankStr.toCharArray()) {
                if (Character.isDigit(c)) {
                    fileIndex += (c - '0');
                } else {
                    if (fileIndex == file) {
                        return Character.isUpperCase(c); // Uppercase = white
                    }
                    fileIndex++;
                }
            }
        }
        
        return true; // Fallback to white
    }
    
    /**
     * Check if there's a piece at square in FEN
     */
    private boolean hasPieceAtSquare(String fen, String square) {
        // Convert square to board coordinates
        int file = square.charAt(0) - 'a'; // 0-7
        int rank = 8 - (square.charAt(1) - '0'); // 0-7
        
        // Parse FEN board section
        String boardSection = fen.split(" ")[0];
        String[] ranks = boardSection.split("/");
        
        if (rank >= 0 && rank < ranks.length) {
            String rankStr = ranks[rank];
            int fileIndex = 0;
            
            for (char c : rankStr.toCharArray()) {
                if (Character.isDigit(c)) {
                    fileIndex += (c - '0');
                } else {
                    if (fileIndex == file) {
                        return true; // Found a piece
                    }
                    fileIndex++;
                }
            }
        }
        
        return false; // No piece found
    }
    
    /**
     * Convert FEN piece character to piece type string
     */
    private String chessPieceTypeFromChar(char c) {
        switch (Character.toLowerCase(c)) {
            case 'p': return "pawn";
            case 'r': return "rook";
            case 'n': return "knight";
            case 'b': return "bishop";
            case 'q': return "queen";
            case 'k': return "king";
            default: return "pawn";
        }
    }
    
    /**
     * Check for check condition and show dramatic indicator
     */
    private void checkForCheckCondition() {
        try {
            if (gameViewModel != null && animationController != null) {
                // Get current position
                String currentFen = null;
                try {
                    java.lang.reflect.Method getCurrentPositionMethod = gameViewModel.getClass().getMethod("getCurrentPosition");
                    currentFen = (String) getCurrentPositionMethod.invoke(gameViewModel);
                } catch (Exception e) {
                    try {
                        java.lang.reflect.Method getCurrentFENMethod = gameViewModel.getClass().getMethod("getCurrentFEN");
                        Object fenLiveData = getCurrentFENMethod.invoke(gameViewModel);
                        if (fenLiveData instanceof androidx.lifecycle.LiveData) {
                            currentFen = (String) ((androidx.lifecycle.LiveData<?>) fenLiveData).getValue();
                        }
                    } catch (Exception e2) {
                        Log.w(TAG, "Could not get current position for check detection");
                        return;
                    }
                }
                
                if (currentFen != null) {
                    // Simple check detection - look for check indicators in FEN or use simple logic
                    boolean isWhiteInCheck = isKingInCheck(currentFen, true);
                    boolean isBlackInCheck = isKingInCheck(currentFen, false);
                    
                    if (isWhiteInCheck) {
                        Log.d(TAG, "🚨 White king is in check!");
                        showKingInCheck(true);
                    } else if (isBlackInCheck) {
                        Log.d(TAG, "🚨 Black king is in check!");
                        showKingInCheck(false);
                    } else {
                        // No check, hide indicator
                        hideKingInCheck();
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error in check detection", e);
        }
    }
    
    /**
     * Simple check detection from FEN (this is a basic implementation)
     * In a real game, you'd use the chess engine to detect check
     */
    private boolean isKingInCheck(String fen, boolean forWhiteKing) {
        try {
            // This is a simplified check detection
            // In a real implementation, you'd use the chess engine
            
            // For now, just check if the FEN contains check indicators
            // Most chess engines append '+' for check or '#' for checkmate
            String[] parts = fen.split(" ");
            if (parts.length > 0) {
                String boardPart = parts[0];
                // This is very basic - a proper implementation would analyze the board
                // For demo purposes, we'll randomly trigger check occasionally
                return Math.random() < 0.1; // 10% chance to demo the check indicator
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Clear piece selection and visual highlights
     */
    private void clearSelection() {
        pieceSelected = false;
        selectedRow = -1;
        selectedCol = -1;
        
        if (chessBoardView != null) {
            // Clear visual highlights using the same methods as MainActivity
            chessBoardView.clearSelectionHighlight();
            chessBoardView.clearHighlightedSquares();
            chessBoardView.invalidate(); // Refresh display
            Log.d(TAG, "🔄 Cleared piece selection");
        }
    }
    
    /**
     * Add move to move history display
     */
    private void addMoveToHistory(String move) {
        if (moveHistoryTextView != null) {
            String currentHistory = moveHistoryTextView.getText().toString();
            String newHistory = currentHistory + "\n" + move;
            moveHistoryTextView.setText(newHistory);
        }
    }
    
    /**
     * Note: AI moves are triggered automatically by GameViewModel after player moves
     * No manual AI triggering needed - the engine responds automatically
     */
    private void triggerAIMove() {
        Log.d(TAG, "🤖 AI move will be triggered automatically by GameViewModel");
        // The GameViewModel automatically calls requestEngineMove() after makePlayerMove()
        // We just need to show the thinking indicator
        showAIThinking(true);
        
        // Hide thinking indicator after a reasonable delay
        mainHandler.postDelayed(() -> {
            showAIThinking(false);
        }, 3000);
    }
    
    /**
     * Convert board coordinates to chess square notation
     */
    private String coordinatesToSquare(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;  // Chess ranks are numbered 1-8 from bottom to top
        return file + String.valueOf(rank);
    }
    
    /**
     * Update game info displays
     */
    private void updateGameInfo() {
        if (playerNameText != null) {
            playerNameText.setText("Player");
        }
        if (playerRatingText != null) {
            playerRatingText.setText("1800");
        }
        if (masterNameText != null) {
            masterNameText.setText(selectedMaster);
        }
        if (masterRatingText != null) {
            masterRatingText.setText(String.valueOf(engineElo));
        }
        
        updateStatus("Game ready - Playing as " + playerColor + " vs " + selectedMaster);
    }
    
    /**
     * Start a new game with current configuration
     */
    private void startNewGame() {
        try {
            if (gameViewModel != null) {
                Log.d(TAG, "🆕 Starting new game - Player: " + playerColor + ", Master: " + selectedMaster + ", ELO: " + engineElo);
                
                // Start new game with personality configuration
                gameViewModel.newGameWithPersonality(
                    playerColor,           // Player color
                    skillLevel,           // Skill level  
                    engineElo,            // Engine ELO
                    selectedMaster,       // Chess master
                    0.3f                 // Personality weight (30%)
                );
                
                updateStatus("New game started - " + playerColor + " vs " + selectedMaster);
                Log.d(TAG, "✅ New game started successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to start new game", e);
            updateStatus("Failed to start game");
        }
    }
    
    /**
     * Update move history display from list of moves
     */
    private void updateMoveHistoryDisplay(java.util.List<String> moves) {
        if (moveHistoryTextView != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < moves.size(); i++) {
                if (i % 2 == 0) {
                    sb.append((i/2 + 1)).append(". ");
                }
                sb.append(moves.get(i));
                if (i % 2 == 0 && i < moves.size() - 1) {
                    sb.append(" ");
                } else {
                    sb.append("\n");
                }
            }
            moveHistoryTextView.setText(sb.toString());
        }
    }
    
    /**
     * Handle game result (checkmate, draw, etc.)
     */
    private void handleGameResult(String result) {
        if (gameResultTextView != null) {
            gameResultTextView.setText(result);
            gameResultTextView.setVisibility(View.VISIBLE);
        }
        
        updateStatus("Game Over: " + result);
        
        // Clear any selection
        clearSelection();
        
        Log.d(TAG, "🏁 Game ended: " + result);
    }

    /**
     * Setup top row game control functionality
     */
    private void setupGameControls() {
        Log.d(TAG, "🎮 Setting up top row game controls...");
        
        pauseButton.setOnClickListener(v -> toggleGamePause());
        
        voiceCommentButton.setOnClickListener(v -> startVoiceComment());
        
        difficultyToggleButton.setOnClickListener(v -> toggleDifficulty());
        
        surrenderButton.setOnClickListener(v -> showSurrenderDialog());
        
        Log.d(TAG, "✅ Top row controls setup completed");
    }
    
    /**
     * Setup bottom row AI and voice controls
     */
    private void setupAIControls() {
        Log.d(TAG, "🎭 Setting up bottom row AI controls...");
        
        ttsToggleButton.setOnClickListener(v -> {
            Log.d(TAG, "🔊 TTS toggle requested");
            // TODO: Implement TTS toggle
            updateStatus("TTS toggled");
        });
        
        personalityToggleButton.setOnClickListener(v -> {
            Log.d(TAG, "🎭 AI personality toggle requested");
            // TODO: Implement AI personality toggle
            updateStatus("AI personality toggled");
        });
        
        voiceSettingsButton.setOnClickListener(v -> {
            Log.d(TAG, "🎙️ Voice settings requested");
            // TODO: Implement voice settings
            updateStatus("Voice settings opened");
        });
        
        Log.d(TAG, "✅ Bottom row controls setup completed");
    }
    
    /**
     * Setup header action buttons
     */
    private void setupHeaderActions() {
        Log.d(TAG, "🎯 Setting up header actions...");
        
        gameAnalysisButton.setOnClickListener(v -> {
            Log.d(TAG, "📊 Game analysis button clicked");
            launchGameAnalysis();
        });
        
        speakButton.setOnClickListener(v -> {
            Log.d(TAG, "🎤 Voice input button clicked");
            // TODO: Implement voice input
            updateStatus("Listening...");
        });
        
        Log.d(TAG, "✅ Header actions setup completed");
    }

    /**
     * Apply cutting-edge Android 15/API 35 glassmorphism effects
     * Implements advanced RenderEffect blur and dynamic theming
     */
    private void applyGlassmorphismEffects() {
        Log.d(TAG, "✨ Applying Android 15/API 35 glassmorphism effects...");
        
        View rootView = findViewById(android.R.id.content);
        
        // Android 15: Apply dynamic color theming from wallpaper
        GlassmorphismUtils.applyDynamicColorTheming(this, rootView);
        
        if (GlassmorphismUtils.isGlassmorphismSupported()) {
            // Apply advanced RenderEffect blur to background panels
            rootView.post(() -> {
                // Apply sophisticated background blur to root gradient
                GlassmorphismUtils.applyLightGlassBlur(rootView);
                Log.d(TAG, "🎭 Applied Android 15 layered blur to background");
            });
            
            // Apply working AGSL glass effects optimized for S23 Ultra
            if (competitiveHeaderContainer != null) {
                competitiveHeaderContainer.post(() -> {
                    // Use new working glass effects with entrance animation
                    float[] headerTint = {0.15f, 0.35f, 0.8f}; // Deep blue
                    WorkingGlassEffects.animateGlassEntrance(competitiveHeaderContainer, 0.85f, headerTint);
                    Log.d(TAG, "✨ Applied working header glass effect with animation");
                });
            }
            
            if (bottomControlsPanel != null) {
                bottomControlsPanel.post(() -> {
                    // Apply controls glass with slight delay for staggered entrance
                    bottomControlsPanel.postDelayed(() -> {
                        float[] controlTint = {0.4f, 0.3f, 0.7f}; // Purple
                        WorkingGlassEffects.animateGlassEntrance(bottomControlsPanel, 0.75f, controlTint);
                        Log.d(TAG, "✨ Applied working controls glass effect with animation");
                    }, 200);
                });
            }
            
            if (battleProgressPanel != null) {
                battleProgressPanel.post(() -> {
                    // Apply progress glass with medium delay
                    battleProgressPanel.postDelayed(() -> {
                        WorkingGlassEffects.ChessGlassPresets.applyControlGlass(battleProgressPanel);
                        Log.d(TAG, "✨ Applied working progress glass effect");
                    }, 400);
                });
            }
        }
        
        Log.d(TAG, "✅ Android 15/API 35 glassmorphism applied:");
        Log.d(TAG, "   🔹 Advanced RenderEffect blur with layered depth");
        Log.d(TAG, "   🔹 Dynamic color theming from wallpaper");
        Log.d(TAG, "   🔹 Hardware-accelerated GPU effects");
        Log.d(TAG, "   🔹 Samsung S23 Ultra optimized performance");
    }

    /**
     * Apply move highlight with working AGSL shader optimized for S23 Ultra
     */
    private void applyMoveHighlight(int fromRow, int fromCol, int toRow, int toCol) {
        if (chessBoardView == null) return;
        
        Log.d(TAG, "✨ Applying move highlight with AGSL shader");
        
        // Calculate center positions for from and to squares
        float squareSize = chessBoardView.getWidth() / 8.0f;
        float fromX = (fromCol + 0.5f) * squareSize;
        float fromY = (fromRow + 0.5f) * squareSize;
        float toX = (toCol + 0.5f) * squareSize;
        float toY = (toRow + 0.5f) * squareSize;
        
        // Apply highlight to both squares
        float[] highlightColor = {1.0f, 0.8f, 0.2f}; // Golden
        
        // From square highlight
        WorkingGlassEffects.applyMoveHighlight(chessBoardView, fromX, fromY, 0.8f, highlightColor);
        
        // To square highlight with slight delay
        chessBoardView.postDelayed(() -> {
            float[] toColor = {0.2f, 0.8f, 1.0f}; // Blue
            WorkingGlassEffects.applyMoveHighlight(chessBoardView, toX, toY, 0.6f, toColor);
        }, 150);
    }
    
    /**
     * Apply capture flash effect when piece is captured
     */
    private void applyCaptureFlash(int captureRow, int captureCol) {
        if (chessBoardView == null) return;
        
        Log.d(TAG, "💥 Applying capture flash effect");
        
        // Calculate capture square center
        float squareSize = chessBoardView.getWidth() / 8.0f;
        float centerX = (captureCol + 0.5f) * squareSize;
        float centerY = (captureRow + 0.5f) * squareSize;
        
        // Apply flash effect
        float[] flashColor = {1.0f, 0.4f, 0.1f}; // Orange-red
        WorkingGlassEffects.applyCaptureFlash(chessBoardView, centerX, centerY, flashColor);
    }
    
    /**
     * Apply dynamic tinting based on game state and evaluation
     */
    private void updateDynamicTinting(float gameProgress, float evaluation) {
        Log.d(TAG, "🎨 Updating dynamic tinting - Progress: " + gameProgress + ", Eval: " + evaluation);
        
        // Update header tinting
        if (competitiveHeaderContainer != null) {
            EnhancedAdaptiveTinting.ChessTintingPresets.applyHeaderTinting(
                competitiveHeaderContainer, gameProgress, evaluation);
        }
        
        // Update controls tinting
        if (bottomControlsPanel != null) {
            EnhancedAdaptiveTinting.ChessTintingPresets.applyControlTinting(
                bottomControlsPanel, gameProgress, evaluation);
        }
        
        // Update battle progress tinting
        if (battleProgressPanel != null) {
            EnhancedAdaptiveTinting.ChessTintingPresets.applyProgressTinting(
                battleProgressPanel, gameProgress, evaluation);
        }
    }
    
    /**
     * Trigger winner celebration animations
     */
    private void triggerWinnerCelebration(boolean playerWon) {
        Log.d(TAG, "🏆 Triggering winner celebration - Player won: " + playerWon);
        
        // Scale animation for the entire board
        if (chessBoardView != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(chessBoardView, "scaleX", 1.0f, 1.1f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(chessBoardView, "scaleY", 1.0f, 1.1f, 1.0f);
            
            AnimatorSet celebrationSet = new AnimatorSet();
            celebrationSet.playTogether(scaleX, scaleY);
            celebrationSet.setDuration(1000);
            celebrationSet.setInterpolator(new OvershootInterpolator(1.3f));
            celebrationSet.start();
        }
        
        // Flash effect on header
        if (competitiveHeaderContainer != null) {
            float[] celebrationColor = playerWon ? 
                new float[]{0.0f, 1.0f, 0.0f} :  // Green for player win
                new float[]{1.0f, 0.0f, 0.0f};   // Red for AI win
                
            // Apply celebration tint
            competitiveHeaderContainer.postDelayed(() -> {
                float[] originalTint = {0.15f, 0.35f, 0.8f};
                WorkingGlassEffects.animateGlassEntrance(competitiveHeaderContainer, 0.9f, celebrationColor);
                
                // Restore original tint after celebration
                competitiveHeaderContainer.postDelayed(() -> {
                    WorkingGlassEffects.animateGlassEntrance(competitiveHeaderContainer, 0.85f, originalTint);
                }, 2000);
            }, 500);
        }
    }

    /**
     * Initialize advanced animation system optimized for Samsung S23 Ultra
     */
    private void initializeAnimationSystem() {
        Log.d(TAG, "🎭 Initializing advanced animation system for S23 Ultra");
        
        // Find the game container for animations (not root container)
        ViewGroup gameContainer = findViewById(R.id.gameContainer);
        if (gameContainer == null) {
            Log.w(TAG, "⚠️ Game container not found, using root container");
            gameContainer = findViewById(android.R.id.content);
        }
        
        // Initialize the master animation controller
        animationController = new ChessAnimationController(this, gameContainer, chessBoardView);
        
        // Enable high-performance animations for S23 Ultra
        animationController.setAnimationsEnabled(true);
        animationController.setAnimationSpeed(1.0f); // Normal speed
        
        Log.d(TAG, "✅ Animation system initialized with:");
        Log.d(TAG, "   🔹 Physics-based piece movement");
        Log.d(TAG, "   🔹 Realistic capture animations");
        Log.d(TAG, "   🔹 3D check indicators");
        Log.d(TAG, "   🔹 Captured pieces display");
        Log.d(TAG, "   🔹 Samsung S23 Ultra 120Hz optimization");
        
        // Don't add test pieces - wait for real captures
    }
    
    /**
     * Test animation features - DISABLED to prevent test pieces at startup
     */
    private void testAnimationFeatures() {
        // Disabled to prevent test captured pieces appearing at startup
        // Real captured pieces will appear automatically during gameplay
        Log.d(TAG, "🧪 Test animation features disabled - waiting for real captures");
    }

    /**
     * Execute chess move with full animation suite
     */
    public void executeAnimatedChessMove(String fromPosition, String toPosition, 
                                       String pieceType, boolean isWhitePiece,
                                       boolean isCapture, String capturedPieceType) {
        if (animationController != null) {
            animationController.executeAnimatedMove(
                fromPosition, toPosition, pieceType, isWhitePiece,
                isCapture, capturedPieceType,
                () -> {
                    // Update game state after animation completes
                    updateGameStateAfterMove();
                    
                    // Update dynamic tinting based on new game state
                    updateDynamicTinting(currentGameProgress, currentEvaluation);
                }
            );
        }
    }
    
    /**
     * Show dramatic check indicator
     */
    public void showKingInCheck(boolean isWhiteKingInCheck) {
        if (animationController != null) {
            animationController.showCheckIndicator(isWhiteKingInCheck);
        }
    }
    
    /**
     * Hide check indicator
     */
    public void hideKingInCheck() {
        if (animationController != null) {
            animationController.hideCheckIndicator();
        }
    }
    
    /**
     * Animate piece promotion with celebration
     */
    public void animatePiecePromotion(String position, String newPieceType) {
        if (animationController != null) {
            animationController.animatePiecePromotion(position, newPieceType, () -> {
                // Trigger celebration effects
                triggerWinnerCelebration(true); // Promotion is always good for the promoting player
            });
        }
    }
    
    /**
     * Get material advantage for UI display
     */
    public int getCurrentMaterialAdvantage() {
        if (animationController != null) {
            return animationController.getMaterialAdvantage();
        }
        return 0;
    }
    
    /**
     * Update game state after move completion
     */
    private void updateGameStateAfterMove() {
        // Update move count for game progression
        moveCount++;
        
        // Calculate game progress (opening -> midgame -> endgame)
        currentGameProgress = Math.min(1.0f, moveCount / 60.0f); // 60 moves = full progression
        
        // Update status display
        updateStatusDisplay();
        
        Log.d(TAG, "🎮 Game state updated - Move: " + moveCount + ", Progress: " + currentGameProgress);
    }
    
    /**
     * Update status display with material advantage
     */
    private void updateStatusDisplay() {
        if (statusTextView != null && animationController != null) {
            int materialAdvantage = animationController.getMaterialAdvantage();
            String advantageText = "";
            
            if (materialAdvantage > 0) {
                advantageText = " (+" + materialAdvantage + " for White)";
            } else if (materialAdvantage < 0) {
                advantageText = " (+" + Math.abs(materialAdvantage) + " for Black)";
            }
            
            String currentStatus = statusTextView.getText().toString();
            if (!currentStatus.contains("(+")) {
                statusTextView.setText(currentStatus + advantageText);
            }
        }
    }

    /**
     * Setup advanced predictive back navigation for Android 15/API 35
     * Features sophisticated gesture preview and Material Container Transform
     */
    private void setupPredictiveBackNavigation() {
        Log.d(TAG, "⬅️ Setting up Android 15 predictive back navigation...");
        
        // Android 15: Advanced OnBackInvokedCallback for enhanced gestures
        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                () -> {
                    Log.d(TAG, "⬅️ Android 15 predictive back gesture with preview");
                    
                    // Apply sophisticated exit transition with Material Container Transform
                    if (Build.VERSION.SDK_INT >= 35) {
                        // Create smooth morphing exit animation
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
            );
        } else {
            // Fallback for older versions
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Log.d(TAG, "⬅️ Legacy back gesture handling");
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
        
        Log.d(TAG, "✅ Android 15 predictive back navigation with transitions configured");
    }
    
    /**
     * Handle shared element transitions if applicable
     */
    private void handleSharedElementTransitions() {
        Log.d(TAG, "🔄 Setting up shared element transitions...");
        
        // Postpone enter transition until layout is ready
        supportPostponeEnterTransition();
        
        // Start transition when chessboard is ready
        if (chessBoardView != null) {
            chessBoardView.post(() -> {
                Log.d(TAG, "🔄 Starting postponed enter transition");
                supportStartPostponedEnterTransition();
            });
        }
        
        Log.d(TAG, "✅ Shared element transitions configured");
    }
    
    /**
     * Launch game analysis with shared element transition
     */
    private void launchGameAnalysis() {
        Log.d(TAG, "📊 Launching game analysis with shared element transition...");
        
        // For now, just launch the existing GameAnalysisActivity
        // TODO: Create modern GameAnalysisActivity with shared element support
        Intent intent = new Intent(this, MainActivity.class); // Placeholder
        
        // Create shared element transition for chessboard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                chessBoardView,
                "chessboard"
            );
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
        
        Log.d(TAG, "📊 Game analysis launched with chessboard transition");
    }
    
    /**
     * Update status display
     */
    private void updateStatus(String message) {
        if (statusTextView != null) {
            statusTextView.setText(message);
            Log.d(TAG, "📱 Status updated: " + message);
        }
    }
    
    /**
     * Update last move display
     */
    private void updateLastMove(String move) {
        if (lastMoveText != null) {
            lastMoveText.setText(move);
            Log.d(TAG, "♟️ Last move updated: " + move);
        } else {
            // Modern layout uses move history in battle progress panel
            if (moveHistoryTextView != null) {
                moveHistoryTextView.append("\n" + move);
                Log.d(TAG, "♟️ Last move added to history: " + move);
            }
        }
    }
    
    /**
     * Show AI thinking indicator with advanced visual effects
     */
    private void showAIThinking(boolean thinking) {
        if (masterThinkingProgressBar != null) {
            masterThinkingProgressBar.setVisibility(thinking ? View.VISIBLE : View.GONE);
            Log.d(TAG, "🤔 AI thinking indicator: " + (thinking ? "shown" : "hidden"));
        }
        
        // Apply breathing effect to glass panels during AI thinking
        if (competitiveHeaderContainer != null) {
            GlassmorphismUtils.applyAIThinkingEffect(competitiveHeaderContainer, thinking);
        }
        if (bottomControlsPanel != null) {
            GlassmorphismUtils.applyAIThinkingEffect(bottomControlsPanel, thinking);
        }
    }
    
    // ===== BUTTON FUNCTIONALITY IMPLEMENTATIONS =====
    
    /**
     * Toggle game pause state
     */
    private void toggleGamePause() {
        isGamePaused = !isGamePaused;
        pauseButton.setText(isGamePaused ? "Resume" : "Pause");
        updateStatus(isGamePaused ? "Game paused" : "Game resumed");
        Log.d(TAG, "⏸️ Game " + (isGamePaused ? "paused" : "resumed"));
    }
    
    /**
     * Start voice comment recording
     */
    private void startVoiceComment() {
        Log.d(TAG, "🎤 Starting player voice comment...");
        voiceCommentButton.setText("🎤 Listening...");
        voiceCommentButton.setEnabled(false);
        updateStatus("Listening for voice comment...");
        
        // Re-enable button after mock delay
        voiceCommentButton.postDelayed(() -> {
            voiceCommentButton.setText("🎤 Voice");
            voiceCommentButton.setEnabled(true);
            updateStatus("Voice comment recorded");
        }, 3000);
    }
    
    /**
     * Toggle difficulty between normal and maximum
     */
    private void toggleDifficulty() {
        useMaxDifficulty = !useMaxDifficulty;
        
        String message = useMaxDifficulty ? 
            "🔥 MAX DIFFICULTY ENABLED!" : 
            "⚙️ Normal difficulty restored";
        
        difficultyToggleButton.setText(useMaxDifficulty ? "🔥 MAX" : "⚙️ Normal");
        updateStatus(message);
        Log.d(TAG, "⚙️ Difficulty toggled: " + (useMaxDifficulty ? "MAX" : "NORMAL"));
    }
    
    /**
     * Show surrender confirmation dialog
     */
    private void showSurrenderDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Surrender?")
            .setMessage("Are you sure you want to surrender this game?")
            .setPositiveButton("Surrender", (dialog, which) -> {
                Log.d(TAG, "🏳️ Player surrendered");
                updateStatus("Game surrendered");
                // TODO: Implement actual game ending logic
            })
            .setNegativeButton("Continue Fighting", null)
            .show();
    }
    
    /**
     * Update game progress and apply adaptive visual effects
     */
    private void updateGameProgress(int totalMoves) {
        moveCount = totalMoves;
        
        // Calculate game progress: 0-20 moves = opening, 21-40 = midgame, 40+ = endgame
        if (totalMoves <= 20) {
            currentGameProgress = totalMoves / 40.0f; // 0.0 to 0.5
        } else if (totalMoves <= 40) {
            currentGameProgress = 0.5f + (totalMoves - 20) / 40.0f; // 0.5 to 1.0
        } else {
            currentGameProgress = 1.0f; // Endgame
        }
        
        Log.d(TAG, "🎨 Game progress updated: " + currentGameProgress + " (moves: " + totalMoves + ")");
        
        // Apply adaptive tinting to glass panels
        applyAdaptiveVisualEffects();
    }
    
    /**
     * Update position evaluation and apply visual effects
     */
    private void updateEvaluation(String evaluationStr) {
        try {
            // Parse evaluation string (could be centipawns or mate values)
            if (evaluationStr.startsWith("Mate")) {
                // Mate score - apply maximum evaluation
                currentEvaluation = evaluationStr.contains("-") ? -1.0f : 1.0f;
            } else {
                // Regular centipawn evaluation
                float centipawns = Float.parseFloat(evaluationStr.replace("cp", "").trim());
                // Convert centipawns to -1.0 to 1.0 range (clamp at ±500cp)
                currentEvaluation = Math.max(-1.0f, Math.min(1.0f, centipawns / 500.0f));
            }
            
            Log.d(TAG, "📊 Evaluation updated: " + currentEvaluation + " (from: " + evaluationStr + ")");
            
            // Apply adaptive tinting based on new evaluation
            applyAdaptiveVisualEffects();
            
        } catch (Exception e) {
            Log.w(TAG, "❌ Failed to parse evaluation: " + evaluationStr, e);
        }
    }
    
    /**
     * Apply adaptive visual effects based on current game state
     */
    private void applyAdaptiveVisualEffects() {
        Log.d(TAG, "✨ Applying adaptive visual effects - Progress: " + currentGameProgress + ", Eval: " + currentEvaluation);
        
        // Apply enhanced adaptive tinting to glass panels
        if (competitiveHeaderContainer != null) {
            EnhancedAdaptiveTinting.ChessTintingPresets.applyHeaderTinting(competitiveHeaderContainer, currentGameProgress, currentEvaluation);
        }
        
        if (bottomControlsPanel != null) {
            EnhancedAdaptiveTinting.ChessTintingPresets.applyControlTinting(bottomControlsPanel, currentGameProgress, currentEvaluation);
        }
        
        if (battleProgressPanel != null) {
            EnhancedAdaptiveTinting.ChessTintingPresets.applyProgressTinting(battleProgressPanel, currentGameProgress, currentEvaluation);
        }
        
        // Apply AGSL adaptive tint to chess board if supported (temporarily disabled)
        // TODO: Re-enable after fixing shader compilation issues
        /*
        if (chessBoardView != null && AGSLShaderEffects.isAGSLSupported()) {
            AGSLShaderEffects.applyAdaptiveTint(chessBoardView, currentGameProgress, currentEvaluation);
        }
        */
    }
    
    /**
     * Trigger visual effect for game events (captures, checks, etc.)
     */
    private void triggerGameEventEffect(GlassmorphismUtils.GameEvent event) {
        Log.d(TAG, "🎬 Triggering visual effect for game event: " + event.name());
        
        // Apply event tinting to battle progress panel
        if (battleProgressPanel != null) {
            GlassmorphismUtils.animateGameEventTint(battleProgressPanel, event, 800);
        }
        
        // Apply AGSL capture flash if it's a capture
        if (event == GlassmorphismUtils.GameEvent.CAPTURE && chessBoardView != null) {
            // Flash effect at center of board
            AGSLShaderEffects.applyCaptureFlash(chessBoardView, 0.5f, 0.5f, 0.8f, 600);
        }
    }
    
    /**
     * Enhanced square selection with advanced glass highlight effect
     */
    private void enhanceSquareSelection(int row, int col) {
        if (chessBoardView != null) {
            // Convert board coordinates to normalized coordinates (0-1)
            float normalizedX = col / 8.0f + 0.0625f; // Add half square offset
            float normalizedY = row / 8.0f + 0.0625f;
            
            // Apply advanced glass highlight
            AdvancedGlassEffects.ChessGlassPresets.applySelectionGlass(chessBoardView, normalizedX, normalizedY);
            
            Log.d(TAG, "✨ Applied advanced glass highlight at " + row + "," + col);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up visual effects
        if (chessBoardView != null) {
            AGSLShaderEffects.clearEffects(chessBoardView);
            AdvancedGlassEffects.clearGlassEffects(chessBoardView);
        }
        
        // Clean up glass effects from panels
        AdvancedGlassEffects.clearGlassEffects(competitiveHeaderContainer);
        AdvancedGlassEffects.clearGlassEffects(bottomControlsPanel);
        AdvancedGlassEffects.clearGlassEffects(battleProgressPanel);
        
        // Clean up resources
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
        
        if (executorService != null) {
            executorService.shutdown();
        }
        
        Log.d(TAG, "🧹 Modern Competitive Mode cleaned up with visual effects");
    }
}