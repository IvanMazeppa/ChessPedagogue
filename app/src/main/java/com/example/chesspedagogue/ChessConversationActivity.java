package com.example.chesspedagogue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChessConversationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton voiceButton;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    private ChessCoachManager chessCoach;
    private SpeechRecognitionManager speechRecognitionManager;
    
    // NEW: Responses API integration for better responses
    private ChessMasterResponseManager responsesManager;
    private ResponsesAPIIntegrationHelper integrationHelper;
    private String currentSessionId = null;

    // Game state passed from main activity
    private String currentFen;
    private List<String> moveHistory;
    private String playerColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_conversation);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Instead, set the navigation icon manually:
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(view -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get game state from intent
        Intent intent = getIntent();
        currentFen = intent.getStringExtra("FEN");
        moveHistory = intent.getStringArrayListExtra("MOVE_HISTORY");
        playerColor = intent.getStringExtra("PLAYER_COLOR");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewChat);
        messageInput = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        voiceButton = findViewById(R.id.buttonVoiceInput);

        // Set up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);

        // Initialize coach and speech recognition
        chessCoach = ChessCoachManager.getInstance(this);
        speechRecognitionManager = new SpeechRecognitionManager(this);
        
        // NEW: Initialize Responses API integration for enhanced conversations
        responsesManager = ChessMasterResponseManager.getInstance(this);
        integrationHelper = ResponsesAPIIntegrationHelper.getInstance(this);

        // Set up click listeners
        sendButton.setOnClickListener(v -> sendMessage());
        voiceButton.setOnClickListener(v -> startVoiceRecognition());

        // Add welcome message with dynamic master name
        String selectedMaster = getSelectedMasterDisplayName();
        addCoachMessage("Hello! I'm " + selectedMaster + ". I'm here to help with your chess game. What would you like to know?");
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        // Add user message to chat
        addUserMessage(text);
        messageInput.setText("");

        // NEW: Enhanced message processing with Responses API integration
        processMessageWithResponsesAPI(text);
    }
    
    /**
     * NEW: Process message using Responses API when available, fallback to ChessCoachManager
     */
    private void processMessageWithResponsesAPI(String userMessage) {
        android.content.SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        String selectedMaster = prefs.getString("selected_master", "tal");
        
        // Check if we should use Responses API for this master
        boolean useResponsesAPI = integrationHelper.shouldUseResponsesAPI(selectedMaster);
        
        if (useResponsesAPI) {
            Log.d("ChessConversation", "🚀 Using Responses API for " + selectedMaster);
            processWithResponsesAPI(selectedMaster, userMessage);
        } else {
            Log.d("ChessConversation", "⚠️ Fallback to ChessCoachManager for " + selectedMaster);
            // Fallback to original ChessCoachManager
            chessCoach.sendMessage(userMessage, new CoachResponseCallback());
        }
    }
    
    /**
     * NEW: Process message using Responses API with proper session management
     */
    private void processWithResponsesAPI(String masterName, String userMessage) {
        // Build game context if position-related
        String gameContext = buildGameContext(userMessage);
        
        if (currentSessionId == null) {
            // Create new session first
            responsesManager.createResponseSession(masterName, "conversation", 
                new ChessMasterResponseManager.ResponseCallback() {
                    @Override
                    public void onResponseStart(String sessionId) {
                        currentSessionId = sessionId;
                        Log.d("ChessConversation", "✅ Session created: " + sessionId);
                        // Now send the message
                        sendMessageToSession(sessionId, userMessage, gameContext);
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
                        Log.e("ChessConversation", "❌ Session creation failed: " + error);
                        // Fallback to ChessCoachManager
                        chessCoach.sendMessage(userMessage, new CoachResponseCallback());
                    }
                });
        } else {
            // Use existing session
            sendMessageToSession(currentSessionId, userMessage, gameContext);
        }
    }
    
    /**
     * NEW: Send message to established Responses API session
     */
    private void sendMessageToSession(String sessionId, String userMessage, String gameContext) {
        responsesManager.sendMessage(sessionId, userMessage, gameContext,
            new ChessMasterResponseManager.ResponseCallback() {
                private StringBuilder fullResponse = new StringBuilder();
                
                @Override
                public void onResponseStart(String sessionId) {
                    Log.d("ChessConversation", "📡 Response processing started");
                    // Show "thinking" indicator if needed
                }
                
                @Override
                public void onResponseChunk(String chunk, boolean isFirst) {
                    fullResponse.append(chunk);
                    // For conversation mode, we'll show the complete response when done
                    // rather than streaming individual chunks for better readability
                }
                
                @Override
                public void onResponseComplete(String response) {
                    String finalResponse = !fullResponse.toString().trim().isEmpty() 
                        ? fullResponse.toString().trim() 
                        : response;
                        
                    if (finalResponse != null && !finalResponse.trim().isEmpty()) {
                        runOnUiThread(() -> addCoachMessage(finalResponse));
                        Log.d("ChessConversation", "✅ Responses API response delivered");
                    } else {
                        // Fallback if empty response
                        chessCoach.sendMessage(userMessage, new CoachResponseCallback());
                    }
                }
                
                @Override
                public void onConversationTurn(String speaker, String message) {
                    Log.d("ChessConversation", "💬 Conversation turn: " + speaker + " - " + message.substring(0, Math.min(50, message.length())));
                }
                
                @Override
                public void onError(String error) {
                    Log.e("ChessConversation", "❌ Responses API error: " + error);
                    // Fallback to ChessCoachManager
                    chessCoach.sendMessage(userMessage, new CoachResponseCallback());
                }
            });
    }
    
    /**
     * NEW: Build game context for position-related questions
     */
    private String buildGameContext(String userMessage) {
        // Check if the question is position-related
        if (isPositionRelated(userMessage) && currentFen != null) {
            StringBuilder context = new StringBuilder();
            context.append("POSITION: ").append(currentFen);
            
            if (playerColor != null) {
                context.append("\nPLAYER: ").append(playerColor);
            }
            
            if (moveHistory != null && !moveHistory.isEmpty()) {
                context.append("\nRECENT_MOVES: ");
                // Include last 6 moves for context
                int startIndex = Math.max(0, moveHistory.size() - 6);
                for (int i = startIndex; i < moveHistory.size(); i += 2) {
                    int moveNumber = (startIndex / 2) + (i - startIndex) / 2 + 1;
                    context.append(moveNumber).append(".");
                    context.append(moveHistory.get(i));
                    if (i + 1 < moveHistory.size()) {
                        context.append(" ").append(moveHistory.get(i + 1));
                    }
                    context.append(" ");
                }
            }
            
            return context.toString();
        }
        
        return ""; // No game context needed for general questions
    }
    
    /**
     * NEW: Check if user question is position-related
     */
    private boolean isPositionRelated(String question) {
        if (question == null) return false;
        
        String lowerQuestion = question.toLowerCase();
        String[] positionKeywords = {
            "this position", "current position", "this move", "what should i play",
            "best move", "analyze", "evaluation", "this board", "here",
            "what do you think of", "how about", "should i take", "can i play",
            "is it good to", "what if i", "in this position"
        };
        
        for (String keyword : positionKeywords) {
            if (lowerQuestion.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }

    private void startVoiceRecognition() {
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
        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                messageInput.setText(text);
                sendMessage();
            }

            @Override
            public void onSpeechError(String error) {
                Toast.makeText(ChessConversationActivity.this,
                        "Speech recognition error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserMessage(String text) {
        ChatMessage message = new ChatMessage(ChatMessage.TYPE_USER, text);
        adapter.addMessage(message);
        scrollToBottom();
    }

    private void addCoachMessage(String text) {
        ChatMessage message = new ChatMessage(ChatMessage.TYPE_COACH, text);
        adapter.addMessage(message);
        scrollToBottom();
    }

    private void scrollToBottom() {
        recyclerView.post(() -> recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1));
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
        if (speechRecognitionManager != null) {
            speechRecognitionManager.release();
        }
    }

    // Callback for coach responses
    private class CoachResponseCallback implements ChessCoachManager.ChessCoachCallback {
        @Override
        public void onResponseReceived(String response) {
            addCoachMessage(response);
        }

        @Override
        public void onError(String errorMessage) {
            Toast.makeText(ChessConversationActivity.this,
                    errorMessage, Toast.LENGTH_LONG).show();
            addCoachMessage("Sorry, I had trouble with that. Could you try asking again?");
        }

        @Override
        public void onSpeechCompleted() {
            // Nothing needed here
        }
    }

    /**
     * Get the display name of the currently selected chess master
     */
    private String getSelectedMasterDisplayName() {
        android.content.SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        String selectedMaster = prefs.getString("selected_master", "tal");
        
        switch (selectedMaster.toLowerCase()) {
            case "tal":
                return "Mikhail Tal";
            case "fischer":
                return "Bobby Fischer";
            case "carlsen":
                return "Magnus Carlsen";
            case "kasparov":
                return "Garry Kasparov";
            case "karpov":
                return "Anatoly Karpov";
            case "kramnik":
                return "Vladimir Kramnik";
            case "anand":
                return "Viswanathan Anand";
            case "alekhine":
                return "Alexander Alekhine";
            case "capablanca":
                return "José Raúl Capablanca";
            case "lasker":
                return "Emanuel Lasker";
            case "morphy":
                return "Paul Morphy";
            case "botvinnik":
                return "Mikhail Botvinnik";
            default:
                return "Chess Master";
        }
    }
}