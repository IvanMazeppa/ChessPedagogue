package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the chess coach AI functionality, including API communication,
 * text-to-speech, and managing the conversation flow.
 */
public class ChessCoachManager {
    private static final String TAG = "ChessCoachManager";

    private static ChessCoachManager instance;
    private final Context context;
    private final OpenAIService openAIService;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    // Text-to-Speech engine
    private TextToSpeech textToSpeech;
    private boolean ttsReady = false;

    // Callback interface for responses
    public interface ChessCoachCallback {
        void onResponseReceived(String response);
        void onError(String errorMessage);
        void onSpeechCompleted();
    }

    private ChessCoachManager(Context context) {
        this.context = context.getApplicationContext();
        this.openAIService = OpenAIService.getInstance();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        initTextToSpeech();
    }

    /**
     * Get the singleton instance of ChessCoachManager
     */
    public static synchronized ChessCoachManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChessCoachManager(context);
        }
        return instance;
    }

    /**
     * Set the OpenAI API key
     */
    public void setApiKey(String apiKey) {
        openAIService.setApiKey(apiKey);
    }

    /**
     * Initialize the Text-to-Speech engine
     */
    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported");
                } else {
                    textToSpeech.setSpeechRate(0.95f); // Slightly slower for clarity
                    textToSpeech.setPitch(1.0f);       // Normal pitch
                    ttsReady = true;
                }
            } else {
                Log.e(TAG, "TTS Initialization failed");
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Speech started
            }

            @Override
            public void onDone(String utteranceId) {
                // Speech completed
                if (currentCallback != null) {
                    mainHandler.post(() -> currentCallback.onSpeechCompleted());
                }
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "TTS Error with utterance: " + utteranceId);
            }
        });
    }

    // Track the current callback
    private ChessCoachCallback currentCallback;

    /**
     * Get chess advice based on the current position
     */
    public void getChessAdvice(String fen, String lastMove, String playerColor, ChessCoachCallback callback) {
        this.currentCallback = callback;

        executorService.execute(() -> {
            try {
                String response = openAIService.generateChessAdvice(fen, lastMove, playerColor);
                mainHandler.post(() -> {
                    callback.onResponseReceived(response);
                    speakResponse(response);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error getting chess advice", e);
                mainHandler.post(() -> callback.onError("Failed to get advice: " + e.getMessage()));
            }
        });
    }

    /**
     * Get enhanced chess advice with full game context
     */
    public void getEnhancedChessAdvice(String fen, List<String> moveHistory,
                                       String playerColor, ChessCoachCallback callback) {
        this.currentCallback = callback;

        executorService.execute(() -> {
            try {
                String response = openAIService.generateEnhancedChessAdvice(fen, moveHistory, playerColor);
                mainHandler.post(() -> {
                    callback.onResponseReceived(response);
                    speakResponse(response);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error getting enhanced chess advice", e);
                mainHandler.post(() -> callback.onError("Failed to get advice: " + e.getMessage()));
            }
        });
    }

    /**
     * Send a user message to the chess coach
     */
    public void sendMessage(String message, ChessCoachCallback callback) {
        this.currentCallback = callback;

        executorService.execute(() -> {
            try {
                String response = openAIService.sendMessage(message);
                mainHandler.post(() -> {
                    callback.onResponseReceived(response);
                    speakResponse(response);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
                mainHandler.post(() -> callback.onError("Failed to send message: " + e.getMessage()));
            }
        });
    }

    /**
     * Speak a response using Text-to-Speech
     */
    private void speakResponse(String response) {
        if (ttsReady) {
            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, "ChessCoach");
        } else {
            Log.w(TAG, "TTS not ready, couldn't speak: " + response);
        }
    }

    /**
     * Stop any ongoing speech
     */
    public void stopSpeaking() {
        if (ttsReady && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    /**
     * Reset the conversation history
     */
    public void resetConversation() {
        openAIService.resetConversation();
    }

    /**
     * Clean up resources when no longer needed
     */
    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        executorService.shutdown();
    }
}