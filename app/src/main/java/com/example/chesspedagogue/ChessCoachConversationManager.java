package com.example.chesspedagogue;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Manages the chess coach conversational experience, integrating voice
 * recognition, realtime API, context awareness, and TTS
 */
public class ChessCoachConversationManager {
    private static final String TAG = "ChessCoachConversation";

    private final Context context;
    private final Handler mainHandler;
    private RealtimeConversationManager realtimeManager;
    private SpeechRecognitionManager speechRecognitionManager;
    private GameStateInfo currentGameState;
    private boolean isConversationActive = false;

    // Conversation state listener implementation
    private final RealtimeConversationManager.ConversationStateListener stateListener =
            new RealtimeConversationManager.ConversationStateListener() {
                @Override
                public void onPassiveListening() {
                    Log.d(TAG, "Passive listening mode activated");
                    showToast("I'm listening in the background...");
                }

                @Override
                public void onListening() {
                    Log.d(TAG, "Active listening mode activated");
                    showToast("I'm listening!");
                }

                @Override
                public void onProcessing() {
                    Log.d(TAG, "Processing your question...");
                    showToast("Thinking...");
                }

                @Override
                public void onSpeaking(String text) {
                    Log.d(TAG, "Coach is speaking: " + (text.length() > 30 ?
                            text.substring(0, 30) + "..." : text));
                }

                @Override
                public void onPartialResponse(String partialText) {
                    // Update UI with the partial response
                    if (conversationListener != null) {
                        conversationListener.onPartialResponse(partialText);
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "Conversation error: " + message);
                    showToast("Error: " + message);

                    if (conversationListener != null) {
                        conversationListener.onError(message);
                    }
                }

                @Override
                public void onConversationEnded() {
                    Log.d(TAG, "Conversation ended");
                    isConversationActive = false;

                    if (conversationListener != null) {
                        conversationListener.onConversationEnded();
                    }
                }
            };

    // Interface for UI updates
    public interface ConversationListener {
        void onConversationStarted();
        void onPartialResponse(String partialText);
        void onError(String message);
        void onConversationEnded();
    }

    private ConversationListener conversationListener;

    /**
     * Create a new chess coach conversation manager
     */
    public ChessCoachConversationManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Create managers
        this.realtimeManager = new RealtimeConversationManager(context);
        this.speechRecognitionManager = new SpeechRecognitionManager(context);

        // Configure managers
        realtimeManager.setConversationStateListener(stateListener);
        realtimeManager.setSpeechRecognitionManager(speechRecognitionManager);
    }

    /**
     * Set the API key for OpenAI
     */
    public void setApiKey(String apiKey) {
        realtimeManager.setApiKey(apiKey);
    }

    /**
     * Set the conversation listener for UI updates
     */
    public void setConversationListener(ConversationListener listener) {
        this.conversationListener = listener;
    }

    /**
     * Update the current game state
     */
    public void updateGameState(GameStateInfo gameState) {
        this.currentGameState = gameState;
    }

    /**
     * Start a conversation with the chess coach
     */
    public void startConversation() {
        if (!checkPermissions()) {
            Log.e(TAG, "Microphone permission not granted");
            showToast("Microphone permission required");
            return;
        }

        if (isConversationActive) {
            Log.d(TAG, "Conversation already active");
            return;
        }

        isConversationActive = true;

        if (conversationListener != null) {
            conversationListener.onConversationStarted();
        }

        // Start the conversation with current game state
        realtimeManager.startConversation(currentGameState);
    }

    /**
     * Stop the active conversation
     */
    public void stopConversation() {
        if (!isConversationActive) {
            return;
        }

        realtimeManager.endConversation("Thanks for chatting! I'm here if you need more help with your game.");
        isConversationActive = false;
    }

    /**
     * Interrupt the coach's speech and start listening
     */
    public void interruptAndListen() {
        if (isConversationActive) {
            realtimeManager.interruptCurrentSpeech();
            showToast("I'm listening!");
            return;
        }

        // If no active conversation, start one
        startConversation();
    }

    /**
     * Send a text message to the coach
     */
    public void sendTextMessage(String message) {
        if (!isConversationActive) {
            // Start conversation first
            startConversation();

            // Add a slight delay to ensure connection is established
            mainHandler.postDelayed(() -> {
                // Process text as if it were spoken
                speechRecognitionManager.simulateRecognizedSpeech(message);
            }, 1000);
        } else {
            // Process text as if it were spoken
            speechRecognitionManager.simulateRecognizedSpeech(message);
        }
    }

    /**
     * Check if the conversation is active
     */
    public boolean isActive() {
        return isConversationActive;
    }

    /**
     * Clean up resources when app is closing
     */
    public void shutdown() {
        if (realtimeManager != null) {
            realtimeManager.shutdown();
        }

        if (speechRecognitionManager != null) {
            speechRecognitionManager.release();
        }
    }

    /**
     * Check for required permissions
     */
    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Microphone permission not granted");
            return false;
        }
        return true;
    }

    /**
     * Show a toast message on the main thread
     */
    private void showToast(String message) {
        mainHandler.post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }
}