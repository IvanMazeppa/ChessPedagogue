package com.example.chesspedagogue;
import com.example.chesspedagogue.GameStateRepository;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages real-time conversations between the user and the chess coach
 * using streaming responses from GPT-4o
 */
public class RealtimeConversationManager {
    private static final String TAG = "RealtimeConversation";

    private final Context context;
    private final SpeechRecognitionManager speechRecognitionManager;
    private final RealtimeOpenAIService openAIService;
    private final OpenAITTSService ttsService;

    private boolean conversationActive = false;
    private int conversationTurns = 0;
    private static final int MAX_TURNS = 10; // Can be adjusted
    private String voicePersona = OpenAITTSService.VOICE_GRANDMASTER;
    private String savedVoicePersona;

    public interface ConversationStateListener {
        void onListening();
        void onProcessing();
        void onSpeaking(String text);
        void onError(String message);
        void onConversationEnded();
    }

    private ConversationStateListener stateListener;

    public RealtimeConversationManager(Context context) {
        this.context = context;
        this.speechRecognitionManager = new SpeechRecognitionManager(context);
        this.openAIService = new RealtimeOpenAIService();
        this.ttsService = OpenAITTSService.getInstance(context);
    }

    public void setVoicePersona(String voicePersona) {
        this.voicePersona = voicePersona;
        Log.d(TAG, "Voice persona set to: " + voicePersona);
    }

    public void setConversationStateListener(ConversationStateListener listener) {
        this.stateListener = listener;
    }

    /**
     * Start a new conversation with a greeting
     */
    // In RealtimeConversationManager.java - update the startConversation method:
    public void startConversation(GameStateInfo gameState) {
        conversationActive = true;
        conversationTurns = 0;

        // If no game state is provided, get it from the repository
        if (gameState == null) {
            gameState = GameStateRepository.getCurrentState();
        }

        // Restore saved voice if available
        if (savedVoicePersona != null) {
            setVoicePersona(savedVoicePersona);
        }

        // Log what we're sending to verify correct state
        Log.d(TAG, "Starting conversation with state: FEN=" + gameState.getCurrentFen() +
                ", moves=" + (gameState.getMoveHistory() != null ? gameState.getMoveHistory().size() : 0));

        // Start with a contextual greeting based on the game state
        String greeting = generateContextualGreeting(gameState);

        // Speak greeting, then listen for response
        speakThenListen(greeting);
    }

    /**
     * Generate a greeting based on the current game state
     */
    private String generateContextualGreeting(GameStateInfo gameState) {
        // If early in the game
        if (gameState.getMoveCount() < 6) {
            return "Hi! I see we're in the opening phase. What would you like to know about your position?";
        }
        // If middle game
        else if (gameState.getMoveCount() < 30) {
            return "Hello! We're in the middle game now. How can I help with your strategy?";
        }
        // If endgame
        else {
            return "I see we're in the endgame. Would you like some advice on your position?";
        }
    }

    /**
     * Speak the given text and then listen for user response
     */
    private void speakThenListen(String text) {
        if (!conversationActive) return;

        if (stateListener != null) {
            stateListener.onSpeaking(text);
        }

        // Get voice preference from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        String voicePersona = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);

        // Log the voice we're using
        Log.d(TAG, "Speaking with voice: " + voicePersona);

        // Use TTS to speak the text
        ttsService.speak(text, voicePersona, OpenAITTSService.MODEL_STANDARD,
                new OpenAITTSService.TTSCallback() {
                    @Override
                    public void onSpeechStarted() {
                        // Speech started, update UI if needed
                    }

                    @Override
                    public void onSpeechReady(File audioFile) {
                        // Speech file ready
                    }

                    @Override
                    public void onSpeechCompleted() {
                        // Speech completed, start listening for user input
                        startListening();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (stateListener != null) {
                            stateListener.onError("TTS error: " + errorMessage);
                        }
                        // Try to recover by listening anyway
                        startListening();
                    }
                });
    }

    /**
     * Start listening for user input
     */
    private void startListening() {
        if (!conversationActive) return;

        // Check if we've reached the maximum turns
        if (conversationTurns >= MAX_TURNS) {
            endConversation("We've had a great conversation! If you need more coaching, just let me know.");
            return;
        }

        if (stateListener != null) {
            stateListener.onListening();
        }

        // Start speech recognition
        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                processUserInput(text);
            }

            @Override
            public void onSpeechError(String error) {
                if (stateListener != null) {
                    stateListener.onError("Recognition error: " + error);
                }

                // Speak error message and try again
                speakThenListen("I'm sorry, I didn't catch that. Could you try again?");
            }
        });
    }

    // In RealtimeConversationManager.java

    // Add this method to set the API key for both services
    public void setApiKey(String apiKey) {
        if (apiKey != null && !apiKey.isEmpty()) {
            // Set the API key for both services
            openAIService.setApiKey(apiKey);
            ttsService.setApiKey(apiKey);
            Log.d(TAG, "API key set for conversation services");
        } else {
            Log.e(TAG, "Attempted to set empty API key");
        }
    }

    /**
     * Process user input and generate a response
     */
    private void processUserInput(String input) {
        if (!conversationActive) return;

        if (stateListener != null) {
            stateListener.onProcessing();
        }

        // Get the current game state
        GameStateInfo gameState = ChessCoachManager.getInstance(context).getCurrentGameState();

        // Generate response using the OpenAI service
        openAIService.generateStreamingResponse(input, gameState, new RealtimeOpenAIService.StreamingResponseCallback() {
            @Override
            public void onResponseStarted() {
                // Could show a "thinking" animation here
            }

            @Override
            public void onResponseChunk(String chunk) {
                // Could display chunks as they arrive for a typing effect
            }

            @Override
            public void onResponseComplete(String fullResponse) {
                // Increment conversation turns
                conversationTurns++;

                // Speak the response and continue the conversation
                speakThenListen(fullResponse);
            }

            @Override
            public void onError(String errorMessage) {
                if (stateListener != null) {
                    stateListener.onError("AI error: " + errorMessage);
                }

                // Try to recover
                speakThenListen("I'm having trouble thinking about that. Could we try a different question?");
            }
        });
    }

    /**
     * End the conversation with a final message
     */
    public void endConversation(String finalMessage) {
        conversationActive = false;

        // Get the current voice before ending
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        savedVoicePersona = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);

        // Speak final message
        ttsService.speak(finalMessage, OpenAITTSService.VOICE_GRANDMASTER,
                OpenAITTSService.MODEL_STANDARD, new OpenAITTSService.TTSCallback() {
                    @Override
                    public void onSpeechStarted() {}

                    @Override
                    public void onSpeechReady(File audioFile) {}

                    @Override
                    public void onSpeechCompleted() {
                        if (stateListener != null) {
                            stateListener.onConversationEnded();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (stateListener != null) {
                            stateListener.onError("TTS error: " + errorMessage);
                            stateListener.onConversationEnded();
                        }
                    }
                });
    }

    /**
     * Interrupt the current conversation (e.g., when user wants to speak)
     */
    public void interruptCurrentSpeech() {
        ttsService.stopPlayback();
        startListening();
    }

    /**
     * Cleanup resources
     */
    public void shutdown() {
        conversationActive = false;
        speechRecognitionManager.release();
        ttsService.shutdown();
    }
}