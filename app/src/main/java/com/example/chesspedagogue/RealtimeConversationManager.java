package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Manager for real-time voice conversations with OpenAI's API.
 * This bridge fixes compatibility issues between old and new code.
 */
public class RealtimeConversationManager {
    private static final String TAG = "RealtimeConversation";

    // Interface for listening to conversation state changes
    public interface ConversationStateListener {
        void onPassiveListening();
        void onListening();
        void onProcessing();
        void onSpeaking(String text);
        void onPartialResponse(String partialText);
        void onError(String message);
        void onConversationEnded();
    }

    private Context context;
    private String apiKey;
    private Handler mainHandler;
    private ConversationStateListener stateListener;
    private SpeechRecognitionManager speechRecognitionManager;
    private OpenAITTSService ttsService;
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private AtomicBoolean isListening = new AtomicBoolean(false);
    private AtomicBoolean isSpeaking = new AtomicBoolean(false);
    private StringBuilder currentResponseText = new StringBuilder();

    /**
     * Create a new conversation manager
     */
    public RealtimeConversationManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize the TTS service
        this.ttsService = new OpenAITTSService(context);
    }

    /**
     * Set the API key for OpenAI
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        if (ttsService != null) {
            ttsService.setApiKey(apiKey);
        }
    }

    /**
     * Set speech recognition manager
     */
    public void setSpeechRecognitionManager(SpeechRecognitionManager manager) {
        this.speechRecognitionManager = manager;
    }

    /**
     * Set conversation state listener
     */
    public void setConversationStateListener(ConversationStateListener listener) {
        this.stateListener = listener;
    }

    /**
     * Start a conversation with game context
     */
    public void startConversation(GameStateInfo gameState) {
        // Generate a greeting based on the game state
        String greeting = generateGreeting(gameState);

        // Start with coach greeting
        speakResponse(greeting);
    }

    /**
     * Generate a contextual greeting based on game state
     */
    private String generateGreeting(GameStateInfo gameState) {
        if (gameState == null) {
            return "Hello! I'm Coach Tal. How can I help with your chess game?";
        }

        // Customize based on game phase
        if (gameState.getMoveCount() < 6) {
            return "Hi! I see we're in the opening phase. What would you like to know about your position?";
        } else if (gameState.getMoveCount() < 30) {
            return "Hello! We're in the middle game now. How can I help with your strategy?";
        } else {
            return "I see we're in the endgame. Would you like some advice on your position?";
        }
    }

    /**
     * Speak a text response
     */
    private void speakResponse(String text) {
        isSpeaking.set(true);

        // Use TTS to speak the text
        ttsService.speak(text, OpenAITTSService.VOICE_GRANDMASTER, OpenAITTSService.MODEL_STANDARD,
                new OpenAITTSService.TTSCallback() {
                    @Override
                    public void onSpeechStarted() {
                        Log.d(TAG, "TTS speech started");
                        if (stateListener != null) {
                            mainHandler.post(() -> stateListener.onSpeaking(text));
                        }
                    }

                    @Override
                    public void onSpeechReady(File audioFile) {
                        Log.d(TAG, "TTS speech ready");
                    }

                    @Override
                    public void onSpeechCompleted() {
                        Log.d(TAG, "TTS speech completed");
                        mainHandler.post(() -> {
                            isSpeaking.set(false);
                            transitionToListening();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "TTS error: " + errorMessage);
                        mainHandler.post(() -> {
                            isSpeaking.set(false);
                            if (stateListener != null) {
                                stateListener.onError("TTS error: " + errorMessage);
                            }
                            transitionToListening();
                        });
                    }
                });
    }

    /**
     * Transition to listening mode
     */
    private void transitionToListening() {
        isListening.set(true);

        if (stateListener != null) {
            stateListener.onListening();
        }

        // Start speech recognition
        if (speechRecognitionManager != null) {
            speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String text) {
                    Log.d(TAG, "Speech recognized: " + text);
                    handleUserInput(text);
                }

                @Override
                public void onSpeechError(String error) {
                    Log.e(TAG, "Speech recognition error: " + error);
                    mainHandler.postDelayed(RealtimeConversationManager.this::transitionToListening, 1000);
                }
            });
        } else {
            Log.e(TAG, "Speech recognition manager not initialized");
        }
    }

    /**
     * Make sure listening has started
     */
    public void ensureListeningStarted() {
        if (!isListening.get() && !isSpeaking.get()) {
            transitionToListening();
        }
    }

    /**
     * Handle user input text
     */
    private void handleUserInput(String text) {
        if (text == null || text.isEmpty()) {
            transitionToListening();
            return;
        }

        // Stop listening and start processing
        isListening.set(false);

        if (stateListener != null) {
            stateListener.onProcessing();
        }

        // For this simplified version, we'll just echo the input
        String response = "You said: " + text + ". This is a placeholder response while we implement the full AI logic.";

        // In a real implementation, you would call the OpenAI API here
        speakResponse(response);
    }

    /**
     * Interrupt current speech and start listening
     */
    public void interruptCurrentSpeech() {
        if (isSpeaking.get()) {
            // Stop TTS
            ttsService.stopPlayback();
            isSpeaking.set(false);
            transitionToListening();
        }
    }

    /**
     * End the conversation
     */
    public void endConversation(String finalMessage) {
        // Speak a final message if provided
        if (finalMessage != null && !finalMessage.isEmpty()) {
            speakResponse(finalMessage);
        }

        if (stateListener != null) {
            stateListener.onConversationEnded();
        }
    }

    /**
     * Clean up resources
     */
    public void shutdown() {
        if (ttsService != null) {
            ttsService.stopPlayback();
        }
    }
}