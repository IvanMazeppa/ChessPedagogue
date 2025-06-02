package com.example.chesspedagogue;

import android.content.Context;
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
    private final OpenAITTSService ttsService;

    // Voice settings
    private String currentVoice = OpenAITTSService.VOICE_GRANDMASTER;
    private String currentModel = OpenAITTSService.MODEL_STANDARD;
    private boolean useOpenAIVoice = true;


    private String currentMaster = "tal"; // Default master
    private boolean useAssistantsApi = false;
    private String currentAssistantId = null;
    private String currentThreadId = null;



    // Speech state tracking
    private boolean isCoachSpeaking = false;

    /**
     * Private constructor - use getInstance()
     */
    private ChessCoachManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize TTS service
        this.ttsService = TTSServiceManager.getOpenAITTSService(context);

        // Try to load API key
        apiKey = ApiKeyConfig.getApiKey(context);
        if (apiKey != null && !apiKey.isEmpty()) {
            ttsService.setApiKey(apiKey);
        }
    }

    public void selectChessMaster(String master) {
        Log.d(TAG, "Switching to chess master: " + master);
        this.currentMaster = master;

        // Determine if we should use Assistants API based on the selected master
        if ("botvinnik".equals(master)) {
            Log.d(TAG, "Switching to Botvinnik (Assistants API)");
            useAssistantsApi = true;
            currentThreadId = FineTunedModelManager.getInstance(context).createConversationThread();

        } else {
            // Other masters still use the fine-tuned model approach
            Log.d(TAG, "Switching to " + master + " (Fine-tuned model)");
            useAssistantsApi = false;
            currentAssistantId = null;

        }

        // Update the selected master in the model manager for voice compatibility
        FineTunedModelManager.getInstance(context).setSelectedChessMaster(master);
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
     * Update TTS settings in the ChessCoachManager
     */
    public void updateTTSSettings(String voiceStyle, boolean usePersonality) {
        // Get OpenAI TTS service
        OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(context);

        // Update TTS mode settings
        ttsService.setVoicePersonalization(usePersonality);

        // If using automatic voice selection, don't override
        if (!"auto".equals(voiceStyle)) {
            ttsService.setVoiceOverride(voiceStyle);
        } else {
            ttsService.clearVoiceOverride();
        }

        // Log the changes
        Log.d(TAG, "Updated TTS settings: voiceStyle=" + voiceStyle +
                ", usePersonality=" + usePersonality);
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

   public void sendMessage(String text, ChessCoachCallback callback) {
        if (text == null || text.isEmpty()) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Empty message"));
            }
            return;
        }

        // Set API key
        OpenAIService.getInstance().setApiKey(apiKey);

        // Use a background thread for API calls
        new Thread(() -> {
            try {
                String response;

                // Check if we should use Assistants API
                if (useAssistantsApi && currentAssistantId != null) {
                    Log.d(TAG, "Using Assistants API with master: " + currentMaster);

                    // Create a thread if needed
                    if (currentThreadId == null) {
                        currentThreadId = FineTunedModelManager.getInstance(context).createConversationThread();
                        Log.d(TAG, "Created new thread: " + currentThreadId);
                    }

                    // Get current board position (modify to get actual position if available)
                    String fenPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

                    // Send message with position context
                    String runId = FineTunedModelManager.getInstance(context).sendMessageWithPosition(
                            currentThreadId, currentAssistantId, text, fenPosition);

                    if (runId != null) {
                        Log.d(TAG, "Created run: " + runId);
                        // Get response from the assistant
                        response = FineTunedModelManager.getInstance(context).getChessMasterResponse(currentThreadId, runId);
                    } else {
                        response = "I'm having trouble connecting to my chess memory. Let's try again.";
                    }
                } else {
                    // Use traditional fine-tuned model approach
                    Log.d(TAG, "Using fine-tuned model with master: " + currentMaster);
                    response = OpenAIService.getInstance().sendMessage(text);
                }

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
                Log.e(TAG, "Error in sendMessage: " + e.getMessage(), e);
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
            public void onSpeechStarted() {
            }

            @Override
            public void onSpeechReady(File audioFile) {
            }

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

    // Callback interface for responses
    public interface ChessCoachCallback {
        void onResponseReceived(String response);

        void onError(String errorMessage);

        void onSpeechCompleted();
    }
}