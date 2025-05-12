package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import com.example.chesspedagogue.GameStateRepository;
import com.example.chesspedagogue.viewmodel.GameViewModel;

import java.io.File;
import java.util.ArrayList;
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

    // System prompt that forces move references in analysis
    private static final String SYSTEM_PROMPT =
            "You are Coach Tal, a brilliant attacking chess master and the 8th World Chess Champion. Your tactical vision and creativity are legendary. When analyzing positions, you MUST:\n\n" +
                    "CRITICAL INSTRUCTIONS - YOU ABSOLUTELY MUST START EVERY RESPONSE WITH A MOVE REFERENCE:\n" +
                    "- Begin your VERY FIRST SENTENCE by directly referencing a specific move from the history\n" +
                    "- Example: \"After 1.e4, you established central control...\"\n" +
                    "- If at starting position: \"I see we're at the starting position with no moves played yet.\"\n" +
                    "- NEVER give generic advice without tying it to specific moves in THIS game\n" +
                    "- Look for tactical opportunities and creative possibilities, just as Tal would\n\n" +
                    "Your analysis should feel personally tailored to this exact position and move history.";

    private static ChessCoachManager instance;
    private final Context context;
    private final OpenAIService openAIService;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private String apiKey;

    // Text-to-Speech engine
    private TextToSpeech textToSpeech;
    private boolean ttsReady = false;
    private SpeechRecognitionManager speechRecognitionManager;
    private OpenAITTSService openAITTSService;
    private String currentVoice = OpenAITTSService.VOICE_GRANDMASTER;
    private String currentModel = OpenAITTSService.MODEL_STANDARD;
    private boolean useOpenAIVoice = true;

    // Conversation state tracking
    private boolean inActiveConversation = false;
    private int conversationTurns = 0;
    private RealtimeConversationManager realtimeConversationManager;
    private Runnable onSpeechCompletedListener = null;
    private static final int MAX_CONVERSATION_TURNS = 5; // Prevent infinite loops
    private boolean isInAlwaysListeningMode = false;
    private boolean isCoachSpeaking = false;

    public boolean isCoachSpeaking() {
        return isCoachSpeaking;
    }


    // Callback interface for responses
    public interface ChessCoachCallback {
        void onResponseReceived(String response);
        void onError(String errorMessage);
        void onSpeechCompleted();
    }

    // Interface for voice recognition control
    public interface VoiceRecognitionController {
        void startListening();
        boolean isInConversationMode();
    }

    private VoiceRecognitionController voiceController;
    private ChessCoachCallback currentCallback;

    public void setVoiceController(VoiceRecognitionController controller) {
        this.voiceController = controller;
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
     * Private constructor - use getInstance()
     */
    public ChessCoachManager(Context context) {
        this.context = context.getApplicationContext();
        this.openAIService = OpenAIService.getInstance();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        realtimeConversationManager = new RealtimeConversationManager(context);
        realtimeConversationManager.setConversationStateListener(new RealtimeConversationManager.ConversationStateListener() {
            @Override
            public void onListening() {
                // Update UI to show listening state
            }

            @Override
            public void onProcessing() {
                // Update UI to show processing state
            }

            // Add this method to the anonymous ConversationStateListener implementation in ChessCoachManager
            @Override
            public void onPassiveListening() {
                // If we're handling passive listening state, just log it for now
                Log.d(TAG, "Chess coach is in passive listening mode");
            }

            @Override
            public void onSpeaking(String text) {
                // Update UI to show speaking state
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Conversation error: " + message);
            }

            @Override
            public void onConversationEnded() {
                // Update UI when conversation ends
            }
        });


        // Initialize OpenAI TTS
        this.openAITTSService = new OpenAITTSService(context);
        initTextToSpeech();
    }

    /**
     * Connect this ChessCoachManager to the GameViewModel to receive move updates
     */
    public void connectToGameViewModel(GameViewModel gameViewModel) {
        if (gameViewModel != null) {
            Log.d(TAG, "Connecting ChessCoachManager to GameViewModel");

            gameViewModel.addMoveHistoryListener(new MoveHistoryObserver.MoveHistoryListener() {
                @Override
                public void onMoveMade(String move, String fen, List<String> fullHistory) {
                    Log.d(TAG, "Move made: " + move + ", total moves: " + fullHistory.size());

                    // Auto-analysis logic
                    if (shouldAnalyzeAutomatically(move, fullHistory.size())) {
                        GameStateInfo gameState = new GameStateInfo(
                                fen, fullHistory, gameViewModel.getPlayerColor()
                        );
                        getEnhancedChessAdvice(gameState, new QuietChessCoachCallback());
                    }
                }

                @Override
                public void onGameReset() {
                    Log.d(TAG, "Game reset detected");
                    resetConversation();
                }
            });

            Log.d(TAG, "Successfully connected to GameViewModel");
        } else {
            Log.w(TAG, "Cannot connect to GameViewModel - it's null");
        }
    }

    public void toggleAlwaysListeningMode() {
        isInAlwaysListeningMode = !isInAlwaysListeningMode;

        if (isInAlwaysListeningMode) {
            // Start a conversation in always-listening mode
            if (!inActiveConversation) {
                startRealtimeConversation(null);
            }
        } else {
            // Stop the current conversation
            stopRealtimeConversation();
        }
    }

    public boolean isAlwaysListening() {
        return isInAlwaysListeningMode;
    }

    // Add this to connect to RealtimeConversationManager state listener
    public void setConversationStateListener(RealtimeConversationManager.ConversationStateListener listener) {
        realtimeConversationManager.setConversationStateListener(listener);
    }

    // Add this to allow interruption to active listening
    public void transitionToActiveListening() {
        if (realtimeConversationManager != null) {
            realtimeConversationManager.startListening();
        }
    }



    /**
     * Determine if we should automatically analyze a position
     */
    private boolean shouldAnalyzeAutomatically(String move, int moveCount) {
        // Analyze after every 10 moves or if the move leads to check/mate
        return moveCount % 10 == 0 || move.endsWith("+") || move.endsWith("#");
    }

    /**
     * Simplified callback that doesn't speak aloud for automatic analysis
     */
    private class QuietChessCoachCallback implements ChessCoachCallback {
        @Override
        public void onResponseReceived(String response) {
            // Just log it, don't speak it
            Log.d(TAG, "Auto-analysis complete: " +
                    response.substring(0, Math.min(50, response.length())) + "...");
        }

        @Override
        public void onError(String errorMessage) {
            Log.e(TAG, "Error in auto-analysis: " + errorMessage);
        }

        @Override
        public void onSpeechCompleted() {
            // No action needed
        }
    }

    /**
     * Set the voice persona to use for TTS
     */
    public void setVoicePersona(String voice) {
        this.currentVoice = voice;
    }

    /**
     * Set the TTS model quality (standard or premium)
     */
    public void setTTSModel(String model) {
        this.currentModel = model;
    }

    /**
     * Enable or disable using OpenAI's premium voices
     */
    public void setUseOpenAIVoice(boolean useOpenAIVoice) {
        this.useOpenAIVoice = useOpenAIVoice;
    }

    /**
     * Speak a response using the appropriate TTS system
     */
    private void speakResponse(String response) {
        // Get latest voice settings from preferences
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        currentVoice = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);

        // Log which voice we're using
        Log.d(TAG, "Speaking with voice: " + currentVoice);

        if (useOpenAIVoice && apiKey != null && !apiKey.isEmpty()) {
            // Use OpenAI's premium voice
            openAITTSService.setApiKey(apiKey);

            // Create callback for the TTS service
            OpenAITTSService.TTSCallback ttsCallback = new OpenAITTSService.TTSCallback() {
                @Override
                public void onSpeechStarted() {
                    isCoachSpeaking = true;
                }

                @Override
                public void onSpeechReady(File audioFile) {
                    // The audio file is ready but we don't need to do anything
                    // The service will handle playback
                }

                @Override
                public void onSpeechCompleted() {
                    // Handle completion, maybe update UI or state
                    isCoachSpeaking = false;

                    // If you need to do something after speech completes
                    if (onSpeechCompletedListener != null) {
                        onSpeechCompletedListener.run();
                    }

                    // Notify the callback
                    if (currentCallback != null) {
                        mainHandler.post(currentCallback::onSpeechCompleted);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "TTS error: " + errorMessage);
                    isCoachSpeaking = false;
                }
            };

            // Call the service with our callback
            openAITTSService.synthesizeSpeech(response, currentVoice, currentModel, ttsCallback);
        } else {
            // Use regular Android TTS
            useFallbackTTS(response);
        }
    }

    /**
     * Use Android's built-in TTS as fallback
     */
    private void useFallbackTTS(String response) {
        if (ttsReady) {
            // Use the existing Android TTS
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ChessCoach");
            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, params, "ChessCoach");
        }
    }

    /**
     * Set the OpenAI API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        openAIService.setApiKey(apiKey);

        // Also set the key for TTS if needed
        if (openAITTSService != null) {
            openAITTSService.setApiKey(apiKey);
        }
    }

    /**
     * Test the current voice settings with a sample phrase
     */
    public void testVoice(String testPhrase, ChessCoachCallback callback) {
        this.currentCallback = callback;
        speakResponse(testPhrase);
    }

    /**
     * Initialize the text-to-speech engine
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

                    // Add this line to make callbacks more reliable
                    textToSpeech.setOnUtteranceProgressListener(createUtteranceProgressListener());

                    ttsReady = true;
                    Log.d(TAG, "TTS initialization complete, callbacks registered");
                }
            } else {
                Log.e(TAG, "TTS Initialization failed with status: " + status);
            }
        });
    }

    /**
     * Create a robust utterance progress listener
     */
    private UtteranceProgressListener createUtteranceProgressListener() {
        return new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "TTS started speaking utterance: " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d(TAG, "TTS finished speaking utterance: " + utteranceId);

                // Important: Always check on main thread to avoid issues
                mainHandler.post(() -> {
                    if (currentCallback != null) {
                        Log.d(TAG, "Notifying callback of speech completion: " +
                                currentCallback.getClass().getSimpleName());
                        currentCallback.onSpeechCompleted();
                    } else {
                        Log.d(TAG, "No callback to notify for speech completion");
                    }

                    // If we're in an active conversation, continue it
                    checkContinueConversation();
                });
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "TTS Error with utterance: " + utteranceId);

                // Even on error, notify callback to keep conversation flowing
                mainHandler.post(() -> {
                    if (currentCallback != null) {
                        Log.d(TAG, "Notifying callback of speech error");
                        currentCallback.onSpeechCompleted();
                    }

                    // Try to continue conversation despite error
                    checkContinueConversation();
                });
            }
        };
    }

    // Add this method
    // In ChessCoachManager.java
    // In ChessCoachManager.java - update the startRealtimeConversation method
    public void startRealtimeConversation(GameStateInfo gameState) {
        // If no game state is provided, get it from the internal method
        if (gameState == null) {
            gameState = getCurrentGameState();
        }

        // Make sure to set the API key before starting
        if (apiKey != null && !apiKey.isEmpty()) {
            realtimeConversationManager.setApiKey(apiKey);

            // NEW: Also pass voice preferences to the conversation manager
            SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
            String voicePersona = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);
            // If there's a method to set voice in your RealtimeConversationManager, call it here
            // realtimeConversationManager.setVoicePersona(voicePersona);
        } else {
            Log.e(TAG, "Cannot start conversation - API key not set");
            return; // Don't even try to start if no API key
        }

        // Now start the conversation with the game state
        realtimeConversationManager.startConversation(gameState);

        // Add this fallback check to ensure we move to listening mode
        new android.os.Handler().postDelayed(() -> {
            Log.d(TAG, "Checking if conversation transitioned to listening phase...");
            if (realtimeConversationManager != null) {
                realtimeConversationManager.ensureListeningStarted();
            }
        }, 10000); // 10 seconds should be enough for greeting to finish
    }

    // Add this method
    public void stopRealtimeConversation() {
        realtimeConversationManager.endConversation("Thanks for the conversation! I'm here if you need more help.");
    }

    // Add this method
    public void interruptCurrentSpeech() {
        realtimeConversationManager.interruptCurrentSpeech();
    }

    // Update the shutdown method
    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (realtimeConversationManager != null) {
            realtimeConversationManager.shutdown();
        }

        executorService.shutdown();
    }

    public GameStateInfo getCurrentGameState() {
        // Get the state from our central repository
        return GameStateRepository.getCurrentState();
    }



    /**
     * Get enhanced chess advice with game state context
     */
    public void getEnhancedChessAdvice(GameStateInfo gameState, ChessCoachCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            if (callback != null) {
                callback.onError("API key not set. Please configure OpenAI API key in settings.");
            }
            return;
        }

        this.currentCallback = callback;

        // Set a strong system prompt that FORCES the AI to use move history
        String systemPrompt =
                "You are Coach Tal, a FIDE-rated World Chess Champion analyzing the current position after " +
                        gameState.getMoveCount() + " moves. " +
                        "IMPORTANT INSTRUCTIONS:\n" +
                        "1. ALWAYS reference specific moves from the game history by move number\n" +
                        "2. Begin your analysis with 'Looking at the position after move X...'\n" +
                        "3. Analyze how previous moves created the current position\n" +
                        "4. Evaluate alternatives to key moves that were played\n" +
                        "5. Your analysis is considered incomplete if you don't discuss specific moves\n\n" +
                        "The player is " + gameState.getPlayerColor() + " and we're in the " +
                        gameState.getGamePhase() + " phase.";

        openAIService.setSystemPrompt(systemPrompt);

        // Format the game state in a way that maximizes the chance of good analysis
        String formattedGameState = gameState.formatForAI();

        // Send to OpenAI with a specific instruction to analyze the move history
        final String analysisRequest = "Analyze this chess position WITH REFERENCE TO THE SPECIFIC MOVES PLAYED:\n\n" +
                formattedGameState;

        // Execute the request in a background thread
        executorService.execute(() -> {
            try {
                String response = openAIService.sendMessage(analysisRequest);

                // Process response on main thread
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onResponseReceived(response);
                        speakResponse(response);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("Error getting chess advice: " + e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Start an interactive voice conversation session with the coach
     */
    public void startVoiceConversation() {
        inActiveConversation = true;
        conversationTurns = 0;

        // Start listening
        if (voiceController != null) {
            voiceController.startListening();
        }
    }

    /**
     * Check whether to continue the conversation based on current state
     */
    private void checkContinueConversation() {
        if (inActiveConversation && conversationTurns < MAX_CONVERSATION_TURNS) {
            // Add a small delay before listening again
            mainHandler.postDelayed(() -> {
                if (voiceController != null) {
                    voiceController.startListening();
                }
            }, 1000); // 1 second pause before listening again
        } else if (inActiveConversation) {
            // End the conversation if we've reached max turns
            inActiveConversation = false;
            conversationTurns = 0;
        }
    }

    /**
     * Process a follow-up voice command in conversation
     */
    public void processVoiceFollowUp(String command, ChessCoachCallback callback) {
        this.currentCallback = callback;
        conversationTurns++;

        executorService.execute(() -> {
            try {
                // Create a context-aware prompt for the follow-up
                String contextualPrompt =
                        "This is a follow-up question in our ongoing conversation.\n" +
                                command;

                String response = openAIService.sendMessage(contextualPrompt);

                mainHandler.post(() -> {
                    callback.onResponseReceived(response);
                    speakResponse(response);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error processing follow-up", e);
                mainHandler.post(() -> callback.onError("Sorry, I had trouble with that follow-up."));
            }
        });
    }

    /**
     * Send a message to the chess coach
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
     * Immediately stop speech and start listening again
     * @return true if speech was stopped, false if no speech was happening
     */
    public boolean interruptAndListen() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            Log.d(TAG, "Interrupting ongoing speech");
            textToSpeech.stop();

            // If we have a voice controller, start listening again
            if (voiceController != null && voiceController.isInConversationMode()) {
                voiceController.startListening();
                return true;
            }
        }
        return false;
    }

    /**
     * Stop any active speech
     */
    public void stopSpeaking() {
        if (ttsReady && textToSpeech.isSpeaking()) {
            textToSpeech.stop();

            // Also stop background listening
            if (speechRecognitionManager != null) {
                speechRecognitionManager.stopBackgroundListening();
            }
        }
    }

    /**
     * Set a listener to be called when speech completes
     * @param listener The Runnable to execute when speech is done
     */
    public void setOnSpeechCompletedListener(Runnable listener) {
        this.onSpeechCompletedListener = listener;
    }

    /**
     * Reset the conversation history
     */
    public void resetConversation() {
        openAIService.resetConversation();
        inActiveConversation = false;
        conversationTurns = 0;
    }

    /**
     * Set the speech recognition manager
     */
    public void setSpeechRecognitionManager(SpeechRecognitionManager manager) {
        this.speechRecognitionManager = manager;
    }
}