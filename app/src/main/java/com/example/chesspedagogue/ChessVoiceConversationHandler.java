package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Integrated handler for complete voice conversations with the chess coach.
 * This class manages the full cycle:
 * 1. Audio recording
 * 2. Speech-to-text via Whisper API
 * 3. Context-aware AI response generation
 * 4. Text-to-speech playback
 */
public class ChessVoiceConversationHandler {
    private static final String TAG = "ChessVoiceConversation";

    private final Context context;
    private final Handler mainHandler;
    private String apiKey;

    // Voice settings from preferences
    private String currentVoice = OpenAITTSService.VOICE_GRANDMASTER;
    private String currentModel = OpenAITTSService.MODEL_STANDARD;

    // Service components
    private OpenAIWhisperService whisperService;
    private OpenAIService chatService;
    private OpenAITTSService ttsService;

    // Conversation state
    private final boolean isListening = false;
    private boolean isProcessing = false;
    private boolean isSpeaking = false;
    private ConversationCallback callback;

    /**
     * Create a new conversation handler
     */
    public ChessVoiceConversationHandler(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize API key from saved preferences
        apiKey = ApiKeyConfig.getApiKey(context);

        // Load voice preferences
        loadVoicePreferences();

        // Initialize service components if API key is available
        if (apiKey != null && !apiKey.isEmpty()) {
            initializeServices();
        }
    }

    /**
     * Set the callback for conversation events
     */
    public void setConversationCallback(ConversationCallback callback) {
        this.callback = callback;
    }

    /**
     * Set or update the API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        initializeServices();
    }

    /**
     * Load voice preferences from SharedPreferences
     */
    private void loadVoicePreferences() {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        currentVoice = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);
        currentModel = prefs.getString("tts_model", OpenAITTSService.MODEL_STANDARD);
    }

    /**
     * Initialize API service components
     */
    private void initializeServices() {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "Cannot initialize services: API key not set");
            return;
        }

        whisperService = new OpenAIWhisperService(apiKey);
        chatService = OpenAIService.getInstance();
        chatService.setApiKey(apiKey);
        ttsService = new OpenAITTSService(context, apiKey);
    }

    /**
     * Handle recorded audio data and process the full conversation
     */
    public void handleRecordedAudio(byte[] audioData) {
        if (isProcessing) {
            Log.d(TAG, "Already processing a request, ignoring new audio");
            return;
        }

        if (apiKey == null || apiKey.isEmpty()) {
            notifyError("API key not set. Please configure in settings.");
            return;
        }

        isProcessing = true;

        // Notify processing started
        notifyProcessingStarted();

        // Process on a background thread
        new Thread(() -> {
            try {
                // Step 1: Transcribe audio to text
                String transcribedText = transcribeAudio(audioData);
                if (transcribedText == null || transcribedText.isEmpty()) {
                    notifyError("Could not transcribe speech. Please try again.");
                    isProcessing = false;
                    return;
                }

                // Notify transcription success
                notifySpeechTranscribed(transcribedText);

                // Step 2: Enhance with game context
                String enhancedPrompt = enhancePromptWithGameState(transcribedText);

                // Step 3: Get AI response
                String response = getChessCoachResponse(enhancedPrompt);
                if (response == null || response.isEmpty()) {
                    notifyError("Could not get response from coach. Please try again.");
                    isProcessing = false;
                    return;
                }

                // Notify response ready
                notifyResponseReady(response);

                // Step 4: Speak response
                speakResponse(response);

            } catch (Exception e) {
                Log.e(TAG, "Error processing conversation", e);
                notifyError("Error: " + e.getMessage());
                isProcessing = false;
            }
        }).start();
    }

    /**
     * Transcribe audio using Whisper API
     */
    private String transcribeAudio(byte[] audioData) {
        try {
            return whisperService.transcribeAudio(audioData);
        } catch (Exception e) {
            Log.e(TAG, "Transcription error", e);
            return null;
        }
    }

    /**
     * Enhance the user prompt with current game state
     */
    private String enhancePromptWithGameState(String userMessage) {
        // Get current game state
        GameStateInfo gameState = GameStateRepository.getCurrentState();

        // Create enhanced prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("User question: ").append(userMessage).append("\n\n");
        prompt.append("Current board position (FEN): ").append(gameState.getCurrentFen()).append("\n");
        prompt.append("Player is playing as: ").append(gameState.getPlayerColor()).append("\n");
        prompt.append("Game phase: ").append(gameState.getGamePhase()).append("\n");

        // If the king is in check, note that
        if (gameState.isCheck()) {
            prompt.append("IMPORTANT: The king is in check.\n");
        }

        // Add move history if available
        List<String> moves = gameState.getMoveHistory();
        if (moves != null && !moves.isEmpty()) {
            prompt.append("\nMove history:\n");
            int moveNumber = 1;
            for (int i = 0; i < moves.size(); i += 2) {
                prompt.append(moveNumber).append(". ").append(moves.get(i));
                if (i + 1 < moves.size()) {
                    prompt.append(" ").append(moves.get(i + 1));
                }
                prompt.append("\n");
                moveNumber++;
            }
        }

        return prompt.toString();
    }

    /**
     * Get response from the chess coach
     */
    private String getChessCoachResponse(String enhancedPrompt) {
        try {
            return chatService.sendMessage(enhancedPrompt);
        } catch (Exception e) {
            Log.e(TAG, "Error getting coach response", e);
            return null;
        }
    }

    /**
     * Speak the response using TTS
     */
    private void speakResponse(String text) {
        // Make sure we're on the main thread for audio playback
        mainHandler.post(() -> {
            // Notify speaking started
            notifySpeakingStarted();

            // Speak using TTS service
            ttsService.speak(text, currentVoice, currentModel, new OpenAITTSService.TTSCallback() {
                @Override
                public void onSpeechStarted() {
                    isSpeaking = true;
                }

                @Override
                public void onSpeechReady(File audioFile) {
                    // Audio file ready for playback (handled internally by TTS service)
                }

                @Override
                public void onSpeechCompleted() {
                    isSpeaking = false;
                    isProcessing = false;
                    notifySpeakingCompleted();
                }

                @Override
                public void onError(String errorMessage) {
                    isSpeaking = false;
                    isProcessing = false;
                    notifyError("TTS error: " + errorMessage);
                }
            });
        });
    }

    /**
     * Stop any ongoing speech
     */
    public void stopSpeaking() {
        if (ttsService != null && isSpeaking) {
            ttsService.stopPlayback();
            isSpeaking = false;
            isProcessing = false;
        }
    }

    /**
     * Test the voice system with a synthetic phrase
     */
    public void testVoice(String testPhrase) {
        if (ttsService == null) {
            if (apiKey == null || apiKey.isEmpty()) {
                notifyError("API key not set. Please configure in settings.");
            } else {
                initializeServices();
            }
        }

        if (ttsService != null) {
            speakResponse(testPhrase);
        }
    }

    /**
     * Update voice settings
     */
    public void updateVoiceSettings() {
        loadVoicePreferences();
    }

    private void notifyListeningStarted() {
        if (callback != null) {
            mainHandler.post(() -> callback.onListeningStarted());
        }
    }

    // Notification helper methods to call callbacks on main thread

    private void sendForTranscription(String audioFilePath) {
        // Simply log that this would process the recording
        Log.d(TAG, "Would process recording from: " + audioFilePath);

        // For testing, simulate a successful processing
        showToast("Processing audio recording...");

        // This can be expanded later to use the proper APIs
    }

    // Add this method to ChessVoiceConversationHandler class
    private void showToast(String message) {
        mainHandler.post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    private void notifySpeechTranscribed(String text) {
        if (callback != null) {
            mainHandler.post(() -> callback.onSpeechTranscribed(text));
        }
    }

    private void notifyProcessingStarted() {
        if (callback != null) {
            mainHandler.post(() -> callback.onProcessingStarted());
        }
    }

    private void notifyResponseReady(String response) {
        if (callback != null) {
            mainHandler.post(() -> callback.onResponseReady(response));
        }
    }

    private void notifySpeakingStarted() {
        if (callback != null) {
            mainHandler.post(() -> callback.onSpeakingStarted());
        }
    }

    private void notifySpeakingCompleted() {
        if (callback != null) {
            mainHandler.post(() -> callback.onSpeakingCompleted());
        }
    }

    private void notifyError(String message) {
        if (callback != null) {
            mainHandler.post(() -> callback.onError(message));
        }
    }

    /**
     * Release resources
     */
    public void shutdown() {
        stopSpeaking();
    }

    // Callback interface for UI updates
    public interface ConversationCallback {
        void onListeningStarted();

        void onSpeechTranscribed(String transcribedText);

        void onProcessingStarted();

        void onResponseReady(String responseText);

        void onSpeakingStarted();

        void onSpeakingCompleted();

        void onError(String errorMessage);
    }
}