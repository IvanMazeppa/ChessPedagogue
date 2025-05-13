package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;

/**
 * A simplified bridge class to handle compatibility between the old and new implementations.
 * This ensures we can still test voice functionality in the app.
 */
public class ChessCoachManager {
    private static final String TAG = "ChessCoachManager";

    private static ChessCoachManager instance;
    private final Context context;
    private final Handler mainHandler;
    private String apiKey;
    private OpenAITTSService ttsService;

    // Voice settings
    private String currentVoice = OpenAITTSService.VOICE_GRANDMASTER;
    private String currentModel = OpenAITTSService.MODEL_STANDARD;
    private boolean useOpenAIVoice = true;

    // Speech state tracking
    private boolean isCoachSpeaking = false;

    // Callback interface for responses
    public interface ChessCoachCallback {
        void onResponseReceived(String response);
        void onError(String errorMessage);
        void onSpeechCompleted();
    }

    /**
     * Get singleton instance of ChessCoachManager
     */
    public static synchronized ChessCoachManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChessCoachManager(context);
        }
        return instance;
    }

    /**
     * Private constructor - use getInstance()
     */
    private ChessCoachManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize TTS service
        this.ttsService = new OpenAITTSService(context);

        // Try to load API key
        apiKey = ApiKeyConfig.getApiKey(context);
        if (apiKey != null && !apiKey.isEmpty()) {
            ttsService.setApiKey(apiKey);
        }
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
     * Set the voice persona to use for TTS
     */
    public void setVoicePersona(String voice) {
        this.currentVoice = voice;
        if (ttsService != null) {
            ttsService.setVoice(voice);
        }
    }

    /**
     * Set the TTS model quality (standard or premium)
     */
    public void setTTSModel(String model) {
        this.currentModel = model;
        if (ttsService != null) {
            ttsService.setModel(model);
        }
    }

    /**
     * Enable or disable using OpenAI's premium voices
     */
    public void setUseOpenAIVoice(boolean useOpenAIVoice) {
        this.useOpenAIVoice = useOpenAIVoice;
    }

    /**
     * Test the current voice settings with a sample phrase
     */
    public void testVoice(String testPhrase, ChessCoachCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("API key not set"));
            }
            return;
        }

        // Load the latest voice settings
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        currentVoice = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);

        // Set the voice and model
        ttsService.setVoice(currentVoice);
        ttsService.setModel(currentModel);

        isCoachSpeaking = true;

        // Use our updated TTSCallback interface
        ttsService.speak(testPhrase, currentVoice, currentModel, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "Test speech started");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "Test speech ready in file: " + audioFile.getPath());
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "Test speech completed");
                isCoachSpeaking = false;

                // Notify callback
                if (callback != null) {
                    mainHandler.post(callback::onSpeechCompleted);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Test speech error: " + errorMessage);
                isCoachSpeaking = false;

                // Notify callback
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(errorMessage));
                }
            }
        });
    }

    /**
     * Send a text message to the chess coach and get a response
     */
    public void sendMessage(String text, ChessCoachCallback callback) {
        if (text == null || text.isEmpty()) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Empty message"));
            }
            return;
        }

        // For now, we'll use the OpenAI service to generate a response
        // This is a simplified implementation that you can expand later
        OpenAIService.getInstance().setApiKey(apiKey);

        // Use a background thread for API calls
        new Thread(() -> {
            try {
                // Generate response using OpenAI
                String response = OpenAIService.getInstance().sendMessage(text);

                // Speak the response if needed
                if (useOpenAIVoice) {
                    speakResponse(response, callback);
                } else {
                    // Just return the text response without speaking
                    if (callback != null) {
                        mainHandler.post(() -> callback.onResponseReceived(response));
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
                }
            }
        }).start();
    }

    /**
     * Helper method to speak a response using TTS
     */
    private void speakResponse(String text, ChessCoachCallback callback) {
        if (callback != null) {
            mainHandler.post(() -> callback.onResponseReceived(text));
        }

        isCoachSpeaking = true;

        // Use TTS to speak the response
        ttsService.speak(text, currentVoice, currentModel, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {}

            @Override
            public void onSpeechReady(File audioFile) {}

            @Override
            public void onSpeechCompleted() {
                isCoachSpeaking = false;
                if (callback != null) {
                    mainHandler.post(callback::onSpeechCompleted);
                }
            }

            @Override
            public void onError(String errorMessage) {
                isCoachSpeaking = false;
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(errorMessage));
                }
            }
        });
    }

    /**
     * Check if the coach is currently speaking
     */
    public boolean isCoachSpeaking() {
        return isCoachSpeaking;
    }

    /**
     * Stop any active speech
     */
    public void stopSpeaking() {
        if (isCoachSpeaking && ttsService != null) {
            ttsService.stopPlayback();
            isCoachSpeaking = false;
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

    /**
     * For compatibility with existing code - get the string representation of player color
     */
    public String getPlayerColor() {
        return "white"; // Default value for compatibility
    }
}