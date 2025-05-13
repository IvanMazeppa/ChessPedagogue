package com.example.chesspedagogue;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
// Add these imports at the top of MainActivity
import com.example.chesspedagogue.repository.GameRepository;
import com.example.chesspedagogue.viewmodel.GameViewModel;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import com.example.chesspedagogue.GameStateRepository;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Core components
    private StockfishManager engine;
    private ChessGameManager gameManager;
    private AudioConversationManager audioConversationManager;
    private static final int PERMISSION_REQUEST_MICROPHONE = 101;
    // Add the voice controller right here, with your other field declarations:
    private ChessCoachManager.VoiceRecognitionController voiceController =
            new ChessCoachManager.VoiceRecognitionController() {
                @Override
                public void startListening() {
                    startVoiceRecognition();
                }

                @Override
                public boolean isInConversationMode() {
                    return inConversationMode;
                }
            };
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
    private FloatingActionButton conversationButton;

    private Vibrator vibrator;

    // Move history tracking
    private TextView moveHistoryTextView;
    private StringBuilder moveHistoryBuilder = new StringBuilder();
    private int moveNumber = 1;
    private List<String> algebraicMoveHistory = new ArrayList<>();

    // Chat panel variables
    private View chatPanel;
    private View dragHandle;
    private float initialY;
    private float initialTouchY;
    private boolean isPanelVisible = false;
    private Animation slideUpAnimation;
    private Animation slideDownAnimation;
    private LinearLayout coachBottomSheet;
    private TextView bottomSheetMessageText;
    private Button bottomSheetRespondButton;
    private Button bottomSheetDismissButton;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    // Add these variables to your MainActivity class
    private boolean inConversationMode = false;
    private int conversationTurns = 0;
    private GameViewModel gameViewModel;
    private static final int MAX_CONVERSATION_TURNS = 5; // Prevent infinite loops
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Set up observers for LiveData
        setupObservers();

        playerColorChoice = getIntent().getStringExtra("PLAYER_COLOR");
        if (playerColorChoice == null) playerColorChoice = "white";


        // Connect chess board view click listener
        boardView = findViewById(R.id.chessBoardView);
        boardView.setOnSquareTapListener(new ChessBoardView.OnSquareTapListener() {
            @Override
            public void onSquareTapped(int row, int col) {
                handleBoardTap(row, col);
            }
        });

        // Get settings from SplashActivity
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
        conversationButton = findViewById(R.id.conversationButton);
        coachBottomSheet = findViewById(R.id.coachBottomSheet);
        bottomSheetMessageText = findViewById(R.id.bottomSheetMessageText);
        bottomSheetRespondButton = findViewById(R.id.bottomSheetRespondButton);
        bottomSheetDismissButton = findViewById(R.id.bottomSheetDismissButton);

        // Initialize the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(coachBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Get or create chessCoach
        chessCoach = ChessCoachManager.getInstance(this);
        chessCoach.connectToGameViewModel(gameViewModel);

        // In MainActivity.java - in onCreate() after initializing chessCoach
        chessCoach.setConversationStateListener(new RealtimeConversationManager.ConversationStateListener() {
            @Override
            public void onPassiveListening() {
                runOnUiThread(() -> {
                    bottomSheetMessageText.setText("I'm listening in the background... just start talking");
                });
            }

            @Override
            public void onListening() {
                runOnUiThread(() -> {
                    bottomSheetMessageText.setText("I'm listening actively now...");
                });
            }

            @Override
            public void onProcessing() {
                runOnUiThread(() -> {
                    bottomSheetMessageText.setText("Thinking...");
                });
            }

            @Override
            public void onSpeaking(String text) {
                runOnUiThread(() -> {
                    bottomSheetMessageText.setText(text);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onConversationEnded() {
                runOnUiThread(() -> {
                    inConversationMode = false;
                    conversationButton.setImageResource(android.R.drawable.ic_media_play);
                    bottomSheetMessageText.setText("Conversation ended. Tap play to start again.");
                });
            }

            @Override
            public void onPartialResponse(String partialText) {
                runOnUiThread(() -> {
                    bottomSheetMessageText.setText(partialText);
                });
            }
        });

        // In MainActivity.java - add this to your onCreate() method
        View bottomSheet = findViewById(R.id.coachBottomSheet);
        if (bottomSheet != null) {
            bottomSheet.setOnClickListener(v -> {
                if (inConversationMode && chessCoach.isAlwaysListening()) {
                    // If the AI is speaking, interrupt it
                    if (chessCoach.isCoachSpeaking()) {
                        chessCoach.interruptCurrentSpeech();
                        Toast.makeText(this, "Interrupting...", Toast.LENGTH_SHORT).show();
                    }
                    // If it's in passive listening mode, transition to active listening
                    else {
                        chessCoach.transitionToActiveListening();
                        Toast.makeText(this, "I'm listening!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // In onCreate, call the initialization:
        initializeAudioConversation();

        // Set up bottom sheet click listener
        coachBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Interrupt speech and start listening when user taps anywhere on the sheet
                if (chessCoach.interruptAndListen()) {
                    Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add this to handle tapping on the coach's message to interrupt
        coachBottomSheet.setOnClickListener(v -> {
            if (inConversationMode) {
                // Interrupt current speech and start listening
                chessCoach.interruptCurrentSpeech();
                Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }
        });

        // For even better response, also make the text area specifically respond to taps
        bottomSheetMessageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Interrupt speech and start listening when user taps the message
                if (chessCoach.interruptAndListen()) {
                    Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // In onCreate or wherever you set up your menu/UI
        Button settingsButton = findViewById(R.id.settingsButton); // You might need to add this to your layout
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Catch and display any error for debugging
                    Toast.makeText(MainActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Error launching settings", e);
                }
            });
        }

        // Set up bottom sheet drag handle
        View bottomSheetDragHandle = findViewById(R.id.bottomSheetDragHandle);
        bottomSheetDragHandle.setOnTouchListener(new View.OnTouchListener() {
            private float initialY;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = coachBottomSheet.getY();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getRawY();
                        float deltaY = currentY - initialTouchY;

                        // Convert to bottom sheet state
                        if (deltaY < -50) { // Dragging up
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else if (deltaY > 50) { // Dragging down
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;
                }
                return false;
            }
        });

        // Set a lower peek height that won't block the board
        int peekHeightDp = 72; // Just enough for buttons and a line of text
        int peekHeightPx = (int) (peekHeightDp * getResources().getDisplayMetrics().density);
        bottomSheetBehavior.setPeekHeight(peekHeightPx);

        // Set maximum height to prevent full coverage of board
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int maxHeight = screenHeight / 3; // Maximum 1/3 of screen height
        bottomSheetBehavior.setMaxHeight(maxHeight);

        // Add this right after initializing coach components
        coachMessageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Interrupt speech and start listening when user taps the message
                if (chessCoach.interruptAndListen()) {
                    Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize chat panel components
        chatPanel = findViewById(R.id.chatPanel);
        if (chatPanel != null) {
            dragHandle = chatPanel.findViewById(R.id.dragHandle);

            // Set up slide animations
            slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

            // Set up drag handle touch listener
            setupDragHandleTouchListener();

            // Set up conversation button for continuous voice interaction
            if (conversationButton != null) {
                conversationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startVoiceConversation();
                    }
                });
            }
        }

        // Set up respond button
        bottomSheetRespondButton.setOnClickListener(v -> {
            startVoiceRecognition();
        });

        // Set up dismiss button
        bottomSheetDismissButton.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            chessCoach.stopSpeaking();
        });

        // Initialize chess coach
        initializeChessCoach();

        voiceInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a simple dialog with options
                String[] options = {"Voice command", "Type a question"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Chess Coach Input");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Voice input
                            startVoiceRecognition();
                        } else {
                            // Text input - show the chat panel
                            toggleChatPanel();
                        }
                    }
                });
                builder.show();
            }
        });

        chessCoachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current game analysis
                String fen = engine.getCurrentFEN();
                showLoading("Coach is analyzing your position...");
                GameStateInfo gameState = new GameStateInfo(fen, algebraicMoveHistory, playerColorChoice);
                chessCoach.getEnhancedChessAdvice(gameState, new ChessCoachCallback());
            }
        });

        askFollowUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForFollowUpQuestion();
            }
        });

        dismissCoachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCoachMessage();
            }
        });

        conversationButton.setOnClickListener(v -> {
            Log.d(TAG, "Top button clicked, starting conversation");
            if (inConversationMode) {
                stopVoiceConversation();
            } else {
                startVoiceConversation();
            }
        });

        // Initialize sound manager
        SoundManager.initialize(this);

        // Get vibrator service
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Show initial status
        updateStatusText("Starting game...");

        // Initialize speech recognition
        initializeSpeechRecognition();

        // Load saved game after engine initialization
        loadSavedGameIfNeeded();

        // Check for and prompt for API key if needed
        promptForApiKeyIfNeeded();

        // Initialize the engine using the native library approach
        initializeStockfishEngine(skillLevel);

        // Setup game analysis button
        Button gameAnalysisButton = findViewById(R.id.gameAnalysisButton);
        if (gameAnalysisButton != null) {
            gameAnalysisButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GameAnalysisActivity.class);
                    intent.putStringArrayListExtra("MOVE_HISTORY", new ArrayList<>(algebraicMoveHistory));
                    startActivity(intent);
                }
            });
        }
    }
    private void initializeAudioConversation() {
        // Create the manager
        audioConversationManager = new AudioConversationManager(this);

        // Get API key from secure storage if available
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey != null && !apiKey.isEmpty()) {
            audioConversationManager.setApiKey(apiKey);
        }

        // Add this to your onCreate() in MainActivity, after initializing gameViewModel
        gameViewModel.getCurrentFEN().observe(this, fen -> {
            if (fen != null) {
                Log.d(TAG, "📋 Updating board with FEN: " + fen);
                boardView.updateBoardFromFen(fen);
            }
        });

        // In onCreate, add this initialization:

        // Set the system prompt
        String systemPrompt = "You are Coach Tal, a brilliant attacking chess master and the 8th World Chess Champion. " +
                "Your tactical vision and creativity are legendary. When analyzing positions, you MUST:\n\n" +
                "- Begin your VERY FIRST SENTENCE by directly referencing a specific move from the history\n" +
                "- If at starting position: \"I see we're at the starting position with no moves played yet.\"\n" +
                "- NEVER give generic advice without tying it to specific moves in THIS game\n" +
                "- Look for tactical opportunities and creative possibilities, just as Tal would";

        audioConversationManager.setSystemPrompt(systemPrompt);

        // Set up listener for conversation events
        audioConversationManager.setConversationListener(new AudioConversationManager.ConversationListener() {
            @Override
            public void onStateChanged(AudioConversationManager.State newState) {
                runOnUiThread(() -> {
                    switch (newState) {
                        case IDLE:
                            bottomSheetMessageText.setText("Tap to start conversation");
                            break;
                        case CONNECTING:
                            bottomSheetMessageText.setText("Connecting to Chess Coach...");
                            break;
                        case LISTENING:
                            bottomSheetMessageText.setText("I'm listening... Go ahead!");
                            break;
                        case PROCESSING:
                            bottomSheetMessageText.setText("Processing your question...");
                            break;
                        case SPEAKING:
                            // Text will be updated by onTextResponse
                            break;
                    }
                });
            }
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connected to Chess Coach!", Toast.LENGTH_SHORT).show();
                    // Show the bottom sheet
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    bottomSheetMessageText.setText("Error: " + message);
                });
            }

            @Override
            public void onTextResponse(String text) {
                runOnUiThread(() -> {
                    bottomSheetMessageText.setText(text);
                });
            }

            // Add this method inside each of your ConversationListener anonymous classes
            @Override
            public void onPartialResponse(String partialText) {
                // Update UI with partial text as it comes in
                runOnUiThread(() -> {
                    // Update the bottomSheetMessageText or other UI elements
                    if (bottomSheetMessageText != null) {
                        bottomSheetMessageText.setText(partialText);
                    }
                });
            }

        });


        // Set up respond button
        bottomSheetRespondButton.setOnClickListener(v -> {
            startVoiceRecognition();
        });

        // Set up dismiss button
        bottomSheetDismissButton.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            chessCoach.stopSpeaking();
        });

        // Initialize the chess coach
        initializeChessCoach();





        // Setup other coach UI elements

        // In your onCreate method, after initializing other buttons

        FloatingActionButton conversationButton = findViewById(R.id.conversationButton);
        // Top-right  conversation button click handler









    }

    /**
     * Initialize the Chess Coach manager
     */
    private void initializeChessCoach() {
        chessCoach = ChessCoachManager.getInstance(this);
        chessCoach.connectToGameViewModel(gameViewModel);

        // Add this line to pass the controller
        chessCoach.setVoiceController(voiceController);

        // Get API key from secure storage if available
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey != null && !apiKey.isEmpty()) {
            chessCoach.setApiKey(apiKey);
        }
    }

    // Add these as complete methods inside your MainActivity class, NOT inside other methods

    /**
     * Ensure the API key is properly loaded before starting conversations
     */
    private void ensureApiKeyIsSet() {
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey != null && !apiKey.isEmpty()) {
            chessCoach.setApiKey(apiKey);
            Log.d(TAG, "API key loaded successfully");
        } else {
            Log.w(TAG, "No API key found - need to prompt user");
            promptForApiKeyIfNeeded();
        }
    }

    /**
     * Start a voice conversation with the chess coach
     */


    /**
     * Loads a saved game if one was requested when launching the activity.
     * This method should be called after the engine has been initialized.
     */
    private void loadSavedGameIfNeeded() {
        // Check if we're loading a saved game
        long gameId = getIntent().getLongExtra("LOAD_GAME_ID", -1);
        if (gameId != -1) {
            try {
                Log.d(TAG, "Starting to load saved game #" + gameId);

                // We're loading a saved game
                playerColorChoice = getIntent().getStringExtra("PLAYER_COLOR");
                ArrayList<String> loadedMoves = getIntent().getStringArrayListExtra("MOVE_HISTORY");
                String finalFen = getIntent().getStringExtra("FINAL_FEN");

                // For now, use the existing method to load the game
                // We'll implement loadGame in the ViewModel later
                if (loadedMoves != null && !loadedMoves.isEmpty()) {
                    // Store the moves in our history
                    algebraicMoveHistory = new ArrayList<>(loadedMoves);

                    // Make sure we start with a fresh game
                    engine.newGame();

                    Log.d(TAG, "Applying moves to rebuild game state...");

                    // Apply each move to rebuild the game state
                    for (String move : loadedMoves) {
                        // Make the move
                        gameManager.makeMove(move);
                        Log.d(TAG, "Applied move: " + move);
                    }
                    // ADD THIS CODE HERE - Update repository with loaded state
                    GameStateRepository.updateState(
                            finalFen != null ? finalFen : engine.getCurrentFEN(),
                            algebraicMoveHistory,
                            playerColorChoice
                    );
                    Log.d(TAG, "Game state repository updated with loaded game data");

                    // Rest of the existing implementation...
                    updateBoardDisplay();
                    moveHistoryBuilder = new StringBuilder();
                    moveNumber = 1;
                    // (rest of your existing code)
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading game: " + e.getMessage(), e);
                Toast.makeText(this, "Error loading game: " + e.getMessage(), Toast.LENGTH_LONG).show();

                // Fall back to a new game if loading fails
                setupChessBoard();
            }
        } else {
            Log.d(TAG, "No saved game to load, setting up new game");
            // No saved game to load, so set up a new game
            setupChessBoard();
        }
    }
    private void setupDragHandleTouchListener() {
        if (dragHandle == null) return;

        dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = chatPanel.getY();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newY = initialY + (event.getRawY() - initialTouchY);
                        // Limit panel height between 150dp and screen height - 100dp
                        float minY = getWindowManager().getDefaultDisplay().getHeight() -
                                convertDpToPx(450);
                        float maxY = getWindowManager().getDefaultDisplay().getHeight() -
                                convertDpToPx(150);

                        if (newY < minY) newY = minY;
                        if (newY > maxY) newY = maxY;

                        chatPanel.setY(newY);
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    // In MainActivity.java, update the toggleChatPanel method:
    private void toggleChatPanel() {
        if (chatPanel == null) return;

        if (isPanelVisible) {
            // Hide panel
            chatPanel.startAnimation(slideDownAnimation);
            chatPanel.setVisibility(View.GONE);
        } else {
            // Show panel
            chatPanel.setVisibility(View.VISIBLE);
            chatPanel.startAnimation(slideUpAnimation);

            // Initialize chat components if needed
            RecyclerView recyclerView = chatPanel.findViewById(R.id.recyclerViewChat);
            if (recyclerView.getAdapter() == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<ChatMessage> messages = new ArrayList<>();
                messages.add(new ChatMessage(ChatMessage.TYPE_COACH,
                        "Hello! I'm Coach Tal. How can I help with your chess game?"));
                ChatAdapter adapter = new ChatAdapter(messages);
                recyclerView.setAdapter(adapter);

                // Set up message input
                EditText messageInput = chatPanel.findViewById(R.id.editTextMessage);
                ImageButton sendButton = chatPanel.findViewById(R.id.buttonSend);

                sendButton.setOnClickListener(v -> {
                    String message = messageInput.getText().toString().trim();
                    if (!message.isEmpty()) {
                        // Add user message
                        messages.add(new ChatMessage(ChatMessage.TYPE_USER, message));
                        // Create a coach response based on the current game state
                        String response = "I'm analyzing your position...";
                        messages.add(new ChatMessage(ChatMessage.TYPE_COACH, response));
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size() - 1);

                        // Process the message with your coach AI
                        chessCoach.sendMessage(message, new ChessCoachCallback());

                        // Clear input
                        messageInput.setText("");
                    }
                });
            }
        }
        isPanelVisible = !isPanelVisible;
    }

    private float convertDpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    /**
     * Start voice recognition to interact with the coach
     */



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

    /**
     * Set up the speech recognition manager
     */
    private void setupSpeechRecognition() {
        speechRecognitionManager = new SpeechRecognitionManager(this);
    }

    // In MainActivity.java - update the toggleChessConversation method
    private void toggleChessConversation() {
        if (inConversationMode) {
            // Stop conversation
            stopVoiceConversation();
            conversationButton.setImageResource(android.R.drawable.ic_media_play);
            bottomSheetMessageText.setText("Conversation paused. Tap the button to start again.");
        } else {
            // Start conversation with always-listening mode
            startVoiceConversation();
            conversationButton.setImageResource(android.R.drawable.ic_media_pause);
            bottomSheetMessageText.setText("I'm listening! Ask me about your chess game...");
        }
    }

    /**
     * Starts a voice conversation with the AI chess coach
     * This method handles the complete flow from connection to listening
     */
    private void startVoiceConversation() {
        Log.d(TAG, "Starting voice conversation");

        // Make sure we have the API key
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(this, "Please set your OpenAI API key in settings first", Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize audio conversation manager if needed
        if (audioConversationManager == null) {
            audioConversationManager = new AudioConversationManager(this);
            audioConversationManager.setApiKey(apiKey);
        }

        // Set up system prompt for chess coaching
        String systemPrompt = "You are Coach Tal, a brilliant attacking chess master and the 8th World Chess Champion. " +
                "Your tactical vision and creativity are legendary. When analyzing positions, you MUST:\n\n" +
                "- Begin your VERY FIRST SENTENCE by directly referencing a specific move from the history\n" +
                "- If at starting position: \"I see we're at the starting position with no moves played yet.\"\n" +
                "- NEVER give generic advice without tying it to specific moves in THIS game\n" +
                "- Look for tactical opportunities and creative possibilities, just as Tal would";

        audioConversationManager.setSystemPrompt(systemPrompt);

        // Set up conversation listener to handle various states and events
        audioConversationManager.setConversationListener(new AudioConversationManager.ConversationListener() {
            @Override
            public void onStateChanged(AudioConversationManager.State newState) {
                Log.d(TAG, "Conversation state changed to: " + newState);

                // When we enter LISTENING state, start speech recognition
                if (newState == AudioConversationManager.State.LISTENING) {
                    startSpeechRecognition();
                }
            }

            @Override
            public void onConnected() {
                Log.d(TAG, "WebSocket connected successfully!");
                Toast.makeText(MainActivity.this, "Connected to Chess Coach", Toast.LENGTH_SHORT).show();

                // Connection established, but don't start listening yet.
                // The system will automatically transition to LISTENING when ready
            }

            @Override
            public void onTextResponse(String text) {
                // Update UI with text responses as they arrive
                Log.d(TAG, "Received text: " + text);
                // You can update a TextView here if you want to show the responses
                // responseTextView.setText(text);
            }

            // Add this method inside each of your ConversationListener anonymous classes
            @Override
            public void onPartialResponse(String partialText) {
                // Update UI with partial text as it comes in
                runOnUiThread(() -> {
                    // Update the bottomSheetMessageText or other UI elements
                    if (bottomSheetMessageText != null) {
                        bottomSheetMessageText.setText(partialText);
                    }
                });
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Conversation error: " + message);
                Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize speech recognition if needed
        if (speechRecognitionManager == null) {
            speechRecognitionManager = new SpeechRecognitionManager(this);
        }

        // Start the conversation!
        audioConversationManager.startConversation();
    }

    /**
     * Starts speech recognition to listen for user input
     */
    private void startSpeechRecognition() {
        if (speechRecognitionManager == null) {
            speechRecognitionManager = new SpeechRecognitionManager(this);
        }

        Log.d(TAG, "Starting speech recognition");
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();

        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                Log.d("MainActivity", "🎤 Speech recognized, sending to conversation: " + text);

                // Forward recognized speech to the conversation manager
                if (audioConversationManager != null) {
                    // Here's where the magic happens - sending user speech to the API
                    audioConversationManager.sendUserMessage(text);
                }
            }

            @Override
            public void onSpeechError(String error) {
                Log.e(TAG, "Speech recognition error: " + error);
                Toast.makeText(MainActivity.this, "Speech error: " + error, Toast.LENGTH_SHORT).show();

                // Try to restart listening after a delay
                new Handler().postDelayed(() -> {
                    if (audioConversationManager != null &&
                            audioConversationManager.getCurrentState() == AudioConversationManager.State.LISTENING) {
                        startSpeechRecognition();
                    }
                }, 2000);
            }
        });
    }

    // Add this method to handle permission results:
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_MICROPHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceConversation();
            } else {
                Toast.makeText(this, "Microphone permission required for voice conversation",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Update your stopVoiceConversation method:
    private void stopVoiceConversation() {
        audioConversationManager.endConversation();
        inConversationMode = false;
        conversationButton.setImageResource(android.R.drawable.ic_media_play);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


    /**
     * Start voice recognition specifically for conversation mode
     */
    private void startVoiceRecognitionForConversation() {
        // Visual feedback that we're listening
        showLoading("Listening...");

        // Start speech recognition
        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                if (!text.isEmpty()) {
                    // For first turn, process normally
                    if (conversationTurns == 0) {
                        processVoiceCommand(text);
                    } else {
                        // For follow-ups, process with context
                        processFollowUpCommand(text);
                    }
                    conversationTurns++;
                } else {
                    // End conversation if no speech detected
                    endConversation("I didn't catch that.");
                }
            }

            @Override
            public void onSpeechError(String error) {
                endConversation("Sorry, I had trouble hearing you.");
            }
        });
    }

    // Modify your existing startVoiceRecognition method or create this new one
    private void startVoiceRecognition() {
        // Update UI to show we're listening
        showLoading("Listening...");

        // Start speech recognition
        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {

                if (!text.isEmpty()) {
                    // For first turn, process normally
                    if (conversationTurns == 0) {
                        processVoiceCommand(text);
                    } else {
                        // For follow-ups, process with context
                        processFollowUpCommand(text);
                    }
                    conversationTurns++;
                } else {
                    // End conversation if no speech detected
                    endConversation("I didn't catch that.");
                }
            }

            @Override
            public void onSpeechError(String error) {
                endConversation("Sorry, I had trouble hearing you.");
            }
        });
    }


    /**
     * Process voice commands from the user, connecting them to the current chess position
     * and providing contextual responses.
     *
     * @param command The recognized speech command from the user
     */
    private void processVoiceCommand(String command) {
        Log.d(TAG, "Voice command: " + command);
        Toast.makeText(this, "You said: " + command, Toast.LENGTH_SHORT).show();

        // Get current position and move history for context
        String currentFen = engine.getCurrentFEN();

        // This is critical - use actual move history from the ViewModel
        List<String> moveHistory = gameViewModel.getMoveHistory().getValue();
        if (moveHistory == null) {
            moveHistory = new ArrayList<>();
        }

        // Create a GameStateInfo with all context
        GameStateInfo gameState = new GameStateInfo(
                currentFen,
                moveHistory,
                playerColorChoice
        );

        // Pass the complete game state to the coach
        showLoading("Coach is analyzing your request...");
        chessCoach.getEnhancedChessAdvice(gameState, new ChessCoachCallback());
    }



    // Add this method if you don't already have it:
    private void updateMoveHistoryView(List<String> moves) {
        // Convert the List<String> moves to your existing history format
        moveHistoryBuilder = new StringBuilder();
        moveNumber = 1;

        for (int i = 0; i < moves.size(); i++) {
            boolean isWhiteMove = (i % 2 == 0);
            if (isWhiteMove) {
                moveHistoryBuilder.append(moveNumber).append(". ").append(moves.get(i));
            } else {
                moveHistoryBuilder.append(" ").append(moves.get(i)).append("\n");
                moveNumber++;
            }
        }

        if (moveHistoryTextView != null) {
            moveHistoryTextView.setText(moveHistoryBuilder.toString());
        }
    }


    /**
     * Process a follow-up command in an ongoing conversation
     */
    private void processFollowUpCommand(String command) {
        Log.d(TAG, "Follow-up command: " + command);

        // Show what was recognized
        Toast.makeText(this, "You said: " + command, Toast.LENGTH_SHORT).show();

        // Create a context-aware prompt for the follow-up
        String contextualPrompt =
                "This is a follow-up question in our conversation.\n\n" +
                        "Current board position: " + engine.getCurrentFEN() + "\n" +
                        "I'm playing as " + playerColorChoice + ".\n" +
                        "My follow-up question is: " + command;

        // Send to the coach with our special callback that continues the conversation
        showLoading("Coach is thinking...");
        chessCoach.sendMessage(contextualPrompt, new ConversationContinuingCallback());
    }

    /**
     * End the conversation gracefully
     */
    private void endConversation(String message) {
        inConversationMode = false;
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        // Reset any conversation-specific UI elements
    }

    

    /**
     * Special callback that continues the conversation after the coach responds
     */
    private class ConversationContinuingCallback implements ChessCoachManager.ChessCoachCallback {
        @Override
        public void onResponseReceived(String response) {
            // Show the coach's response
            showCoachMessage(response);
        }

        @Override
        public void onError(String errorMessage) {
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            endConversation(null);
        }

        @Override
        public void onSpeechCompleted() {
            // This is the magic moment - when the coach finishes speaking,
            // we start listening again to continue the conversation!
            Log.d(TAG, "ConversationContinuingCallback.onSpeechCompleted called. inConversationMode="
                    + inConversationMode + ", conversationTurns=" + conversationTurns);
            if (inConversationMode && conversationTurns < MAX_CONVERSATION_TURNS) {
                // Add a small delay so the user has time to think
                new Handler().postDelayed(() -> {
                    runOnUiThread(() -> {
                        // Start listening again
                        startVoiceRecognition();
                    });
                }, 1500); // 1.5 second pause
            } else {
                // We've reached our turn limit, end gracefully
                endConversation("Thanks for the conversation!");
            }
        }
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
                    GameStateInfo gameState = new GameStateInfo(fen, algebraicMoveHistory, playerColorChoice);
                    chessCoach.getEnhancedChessAdvice(gameState, new ChessCoachCallback());
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
        // Update the bottom sheet with the loading message
        bottomSheetMessageText.setText(message);

        // Show the bottom sheet in collapsed state
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Hide the respond button while loading
        if (bottomSheetRespondButton != null) {
            bottomSheetRespondButton.setVisibility(View.GONE);
        }

        // DON'T use the old message card anymore
        // coachMessageCard.setVisibility(View.VISIBLE); - REMOVE this line
        // coachMessageText.setText(message); - REMOVE this line
        // askFollowUpButton.setVisibility(View.GONE); - REMOVE this line
    }
    private void showScrollingMessage(String message) {
        // Set message text
        bottomSheetMessageText.setText(message);

        // Reset scroll position
        ScrollView scrollView = (ScrollView) bottomSheetMessageText.getParent();
        scrollView.scrollTo(0, 0);

        // Start scrolling animation after a small delay
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Calculate scroll speed based on text length
                int duration = Math.min(message.length() * 30, 12000); // Max 12 seconds

                // Create smooth scroll animation
                ObjectAnimator animator = ObjectAnimator.ofInt(
                        scrollView, "scrollY",
                        0, bottomSheetMessageText.getHeight());
                animator.setDuration(duration);
                animator.setInterpolator(new LinearInterpolator());
                animator.start();
            }
        }, 2000); // 2 second delay before starting scroll
    }

    /**
     * Display the coach's message
     */
    private void showCoachMessage(String message) {
        // ONLY update the bottom sheet, not the popup card
        bottomSheetMessageText.setText(message);

        // Show the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // DON'T do anything with the old coachMessageCard
        // coachMessageCard.setVisibility(View.VISIBLE); - REMOVE this line
        // coachMessageText.setText(message); - REMOVE this line

        // Make sure the follow-up button in the BOTTOM SHEET is visible
        if (bottomSheetRespondButton != null) {
            bottomSheetRespondButton.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Hide the coach message card
     */
    private void hideCoachMessage() {
        // Hide the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Don't worry about the old card anymore
        // coachMessageCard.setVisibility(View.GONE); - REMOVE this line

        // Stop speaking
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
                // Get the Elo value passed from SplashActivity
                int engineElo = getIntent().getIntExtra("ENGINE_ELO", 1500); // Default if missing

                // Configure the engine - use both methods for better control
                engine.setSkillLevel(skillLevel);

                // Implement proper strength limiting for easier levels
                if (skillLevel < 10) {
                    // For lower skill levels, enforce Elo limit to make engine beatable
                    engine.setEngineStrength(engineElo);
                } else {
                    // For higher skill levels, disable limiting for maximum strength
                    engine.setOption("UCI_LimitStrength", "false");
                }
                // In the same method above
                if (skillLevel < 5) {
                    // Very easy - restrict depth severely
                    engine.setOption("Depth", "2");
                } else if (skillLevel < 10) {
                    // Easy to medium - moderate depth
                    engine.setOption("Depth", "5");
                } else if (skillLevel < 15) {
                    // Medium to hard - decent depth
                    engine.setOption("Depth", "8");
                } else {
                    // Expert level - unrestricted depth
                    engine.setOption("Depth", "16");
                }

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

    // Simplified setupChessBoard method
    // In your setupChessBoard() method in MainActivity
    // In MainActivity.java - find the setupChessBoard method (around line 2018)
    // In MainActivity.java - find the setupChessBoard method
    private void setupChessBoard() {
        // Flip the board if the player is playing as black
        boardView.setFlipped(playerColorChoice.equalsIgnoreCase("black"));

        // Update the board with the starting position
        String startPos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Log.d(TAG, "📋 Setting up board with starting position: " + startPos);
        boardView.updateBoardFromFen(startPos);

        // Reset the algebraic move history
        algebraicMoveHistory = new ArrayList<>();
        Log.d(TAG, "Move history reset for new game");

        // Set up board click listener
        boardView.setOnSquareTapListener(new ChessBoardView.OnSquareTapListener() {
            @Override
            public void onSquareTapped(int row, int col) {
                Log.d(TAG, "BOARD TAP EVENT RECEIVED at " + row + "," + col);
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

        // Simple move
        return "move";
    }

    // Add this overloaded version that accepts FEN as well
    private String determineMoveType(String moveUci, String fen) {
        // First check if it's a capture using the original method
        String baseType = determineMoveType(moveUci);

        // Then check for check/checkmate if we have a FEN string
        if (fen != null) {
            if (fen.contains("#")) {
                return "checkmate";
            } else if (fen.contains("+")) {
                return "check";
            }
        }

        // Return the base type if no check detected
        return baseType;
    }

    /**
     * Show legal moves for the selected piece
     */
// Updated to use ViewModel
    private void showLegalMovesFor(int row, int col) {
        // Clear previous highlights
        boardView.clearHighlightedSquares();

        // Keep using engine directly for now
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

    // Helper method to convert board position to algebraic square notation (e.g., "e4")
    private String convertToAlgebraicSquare(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    // Replace the setupObservers method with this more comprehensive version
    private void setupObservers() {
        // Observe chess board updates - this method exists in your ViewModel
        // In MainActivity.java - ensure board is updated when FEN changes
        gameViewModel.getCurrentFEN().observe(this, fen -> {
            if (fen != null) {
                boardView.updateBoardFromFen(fen);
                Log.d(TAG, "Board updated with FEN: " + fen);
            }
        });

        // Observe status messages - this method exists in your ViewModel
        gameViewModel.getStatusMessage().observe(this, message -> {
            if (message != null) {
                statusTextView.setText(message);
            }
        });

        // Observe player turn changes - this method exists in your ViewModel
        gameViewModel.isPlayerTurn().observe(this, isPlayerTurn -> {
            // You could update UI elements based on whose turn it is
            this.isPlayerTurn = isPlayerTurn != null && isPlayerTurn;
        });

        // Observe game over state - this method exists in your ViewModel
        gameViewModel.isGameOver().observe(this, isGameOver -> {
            if (isGameOver != null && isGameOver) {
                // For now, just show a simple toast instead of using getWinner()
                Toast.makeText(this, "Game over!", Toast.LENGTH_LONG).show();
            }
        });
        // Add to your setupObservers() method
        gameViewModel.getLastMoveEvent().observe(this, coordinates -> {
            if (coordinates != null && coordinates.length == 4) {
                boardView.setLastMove(
                        coordinates[0], coordinates[1],
                        coordinates[2], coordinates[3]
                );
            }
        });
        // Observe move history updates - this method exists in your ViewModel
        gameViewModel.getMoveHistory().observe(this, moves -> {
            if (moves != null) {
                updateMoveHistoryView(moves);

                // Also update our algebraic move history for the coach
                algebraicMoveHistory = new ArrayList<>(moves);
            }
        });
        // Add to your setupObservers() method
        gameViewModel.getKingInCheckEvent().observe(this, kingPosition -> {
            if (kingPosition != null) {
                // King is in check!
                boardView.setKingInCheck(true, kingPosition[0], kingPosition[1]);

                // Play a check sound and haptic feedback
                SoundManager.playSound("check");
                performHapticFeedback("check");
            } else {
                // No king in check
                boardView.setKingInCheck(false, -1, -1);
            }
        });
        // In MainActivity.java, add this to your setupObservers() method
        gameViewModel.getAnimateMoveEvent().observe(this, coordinates -> {
            if (coordinates != null && coordinates.length == 4) {
                // Animate the piece movement
                boardView.animateMove(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);

                // Also play sound and haptic feedback
                String moveUci = convertToUCI(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                String moveType = determineMoveType(moveUci, gameViewModel.getCurrentFEN().getValue());

                SoundManager.playSound(moveType);
                performHapticFeedback(moveType);
            }
        });

    }

    // Add this method to show game over dialog
    private void showGameOverDialog(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");

        if (winner != null) {
            builder.setMessage(winner + " wins! Would you like to play again?");
        } else {
            builder.setMessage("Game ended in a draw. Would you like to play again?");
        }

        builder.setPositiveButton("New Game", (dialog, which) -> {
            // Start a new game
            gameViewModel.newGame(playerColorChoice);
        });

        builder.setNegativeButton("Analyze Game", (dialog, which) -> {
            // Launch analysis activity
            Intent intent = new Intent(MainActivity.this, GameAnalysisActivity.class);
            intent.putStringArrayListExtra("MOVE_HISTORY", new ArrayList<>(algebraicMoveHistory));
            startActivity(intent);
        });

        builder.setCancelable(true);
        builder.show();
    }



    private void handleBoardTap(int row, int col) {
        Log.d(TAG, "Tap received at: " + row + "," + col);
        Log.d(TAG, "Selected square: row=" + boardView.getSelectedRow() + ", col=" + boardView.getSelectedCol());
        Log.d(TAG, "Is player turn? " + (gameViewModel.isPlayerTurn().getValue() == null ? "null" : gameViewModel.isPlayerTurn().getValue()));

        // Get info about tapped piece
        char tappedPiece = boardView.getPieceAt(row, col);
        boolean isTappingOwnPiece = isPlayerPiece(tappedPiece);
        Log.d(TAG, "Tapped piece: '" + tappedPiece + "', isPlayerPiece: " + isTappingOwnPiece);

        // Only allow moves if it's the player's turn
        if (!gameViewModel.isPlayerTurn().getValue()) {
            statusTextView.setText("Please wait for the engine to move");
            return;
        }

        // Get the currently selected square, if any
        int selectedRow = boardView.getSelectedRow();
        int selectedCol = boardView.getSelectedCol();

        // IMPORTANT: Add this new case to handle selecting a different piece
        // Case 1: A piece is already selected AND tapping another own piece - change selection
        if (selectedRow != -1 && isTappingOwnPiece) {
            Log.d(TAG, "Changing selection to piece at " + row + "," + col);
            boardView.clearHighlightedSquares(); // Clear previous highlights
            boardView.setSelectedSquare(row, col); // Select the new piece
            showLegalMovesFor(row, col); // Show legal moves for the new piece
            return;
        }

        // Case 2: No piece selected yet, and tapping own piece
        if (selectedRow == -1 && isTappingOwnPiece && gameViewModel.isPlayerTurn().getValue()) {
            Log.d(TAG, "✅ Selecting piece at " + row + "," + col);
            boardView.setSelectedSquare(row, col);
            showLegalMovesFor(row, col);
            return;
        }

        // Case 3: Making a move (piece already selected and tapping destination)
        if (selectedRow != -1) {
            String moveUci = convertToUCI(selectedRow, selectedCol, row, col);
            Log.d(TAG, "Attempting move: " + moveUci);

            // Use ViewModel to make move
            gameViewModel.makePlayerMove(moveUci);
        }
    }




    /**
     * Check if a piece belongs to the player
     */
    private boolean isPlayerPiece(char piece) {
        if (piece == ' ') return false;

        boolean isPieceWhite = Character.isUpperCase(piece);
        boolean isPlayerWhite = playerColorChoice.equalsIgnoreCase("white");

        Log.d(TAG, "isPlayerPiece check: piece='" + piece + "', isPieceWhite=" + isPieceWhite +
                ", isPlayerWhite=" + isPlayerWhite);

        return isPieceWhite == isPlayerWhite;
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

    // In MainActivity, when calling the coach
    // In MainActivity.java - Update the getAdviceFromCoach method

    private void getAdviceFromCoach() {
        // First, make sure we have the CURRENT FEN from the engine
        String currentFen = engine.getCurrentFEN();

        // Log for debugging
        Log.d(TAG, "Getting advice for position: " + currentFen);
        Log.d(TAG, "Move history: " + algebraicMoveHistory.toString());

        // Create a complete game state object with the CURRENT information
        GameStateInfo gameState = new GameStateInfo(
                currentFen,                  // Current position
                new ArrayList<>(algebraicMoveHistory),  // Copy of move history
                playerColorChoice            // Player's color
        );

        showLoading("Coach is analyzing your position...");
        chessCoach.getEnhancedChessAdvice(gameState, new ChessCoachCallback());
    }

    // In your callback/handler where you want AI analysis after a move
    private void getAdviceAfterMove(String fen, List<String> moveHistory) {
        // Create a complete game state object with all necessary context
        GameStateInfo gameState = new GameStateInfo(
                fen,               // Current board position
                moveHistory,       // Complete move history (crucial!)
                playerColorChoice  // Whether playing as white/black
        );

        showLoading("Coach is analyzing your position...");
        chessCoach.getEnhancedChessAdvice(gameState, new ChessCoachCallback());
    }


    private void startChessConversation() {
        // Ensure API key is set
        ensureApiKeyIsSet();

        // Get current game state
        GameStateInfo gameState = new GameStateInfo(
                engine.getCurrentFEN(),
                algebraicMoveHistory,
                playerColorChoice
        );

        // Log what we're doing
        Log.d(TAG, "Starting conversation with FEN: " + gameState.getCurrentFen());
        Log.d(TAG, "Move history size: " + gameState.getMoveHistory().size());

        // Start conversation with this game state
        chessCoach.startRealtimeConversation(gameState);

        // Update UI state
        inConversationMode = true;
        // Update button appearance as needed
    }

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
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Add this to MainActivity
    private void testConversationFlow() {
        // Get current game state
        String currentFen = engine.getCurrentFEN();
        GameStateInfo gameState = new GameStateInfo(
                currentFen,
                algebraicMoveHistory,
                playerColorChoice
        );

        // Log status
        Log.d(TAG, "Starting conversation test with game state:");
        Log.d(TAG, "FEN: " + currentFen);
        Log.d(TAG, "Move count: " + algebraicMoveHistory.size());

        // Show toast for user
        Toast.makeText(this, "Starting conversation test...", Toast.LENGTH_SHORT).show();

        // Start conversation
        chessCoach.startRealtimeConversation(gameState);
    }

// Add a test button to the menu or create a debug menu option

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioConversationManager != null) {
            audioConversationManager.release();
        }
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