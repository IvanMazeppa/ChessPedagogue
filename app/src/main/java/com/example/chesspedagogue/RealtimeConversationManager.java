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
    private boolean alwaysListening = false;

    // Update the interface in RealtimeConversationManager.java
    public interface ConversationStateListener {
        void onListening();
        void onProcessing();
        void onSpeaking(String text);
        void onError(String message);
        void onConversationEnded();
        // Add this new method
        void onPassiveListening();
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
        // Prevent duplicate conversations
        if (conversationActive) {
            Log.d(TAG, "Conversation already active, not starting a new one");
            return;
        }

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
        Log.d(TAG, "Speaking text and then will listen: " + text.substring(0, Math.min(20, text.length())) + "...");
        if (!conversationActive) return;

        if (stateListener != null) {
            stateListener.onSpeaking(text);
        }

        // Get voice preference from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        String voicePersona = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);

        // Log the voice we're using
        Log.d(TAG, "Speaking with voice: " + voicePersona);

        // ✨ IMPORTANT: Add failsafe timer to ensure we transition to listening
        // Calculate a reasonable timeout based on text length
        int estimatedSpeechMs = Math.min(10000, 1000 + (text.length() * 67)); // Cap at 10 seconds

        // Set a timer to ensure we start listening even if callback fails
        new android.os.Handler().postDelayed(() -> {
            Log.d(TAG, "⏰ Failsafe timer expired - ensuring listening mode starts");
            if (conversationActive) {
                if (alwaysListening) {
                    Log.d(TAG, "Failsafe starting background listening mode");
                    startBackgroundListening();
                } else {
                    Log.d(TAG, "Failsafe starting active listening mode");
                    startListening();
                }
            }
        }, estimatedSpeechMs);

        // Use TTS to speak the text - no changes to this part
        ttsService.speak(text, voicePersona, OpenAITTSService.MODEL_STANDARD,
                new OpenAITTSService.TTSCallback() {
                    @Override
                    public void onSpeechStarted() {
                        // Speech started
                        Log.d(TAG, "TTS speech started");
                    }

                    @Override
                    public void onSpeechReady(File audioFile) {
                        // Speech file ready
                        Log.d(TAG, "TTS speech ready in file: " + audioFile.getName());
                    }

                    @Override
                    public void onSpeechCompleted() {
                        // Speech completed, try transitioning to listening
                        Log.d(TAG, "✓ TTS callback: Speech completed, transitioning to listening");
                        if (conversationActive) {
                            if (alwaysListening) {
                                startBackgroundListening();
                            } else {
                                startListening();
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "TTS error: " + errorMessage);
                        if (stateListener != null) {
                            stateListener.onError("TTS error: " + errorMessage);
                        }
                        // Try to recover by listening anyway
                        if (alwaysListening) {
                            startBackgroundListening();
                        } else {
                            startListening();
                        }
                    }
                });
    }

    /**
     * Start listening for user input
     */
    public void startListening() {
        Log.d(TAG, "Starting active listening mode");
        if (!conversationActive) return;

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

                // Go back to background listening if in always-listening mode
                if (alwaysListening) {
                    startBackgroundListening();
                }
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

    public void ensureListeningStarted() {
        if (!conversationActive) return;

        Log.d(TAG, "Ensuring listening has started (failsafe check)");
        if (alwaysListening) {
            startBackgroundListening();
        } else {
            startListening();
        }
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
                        Log.d(TAG, "TTS speech completed, transitioning to listening mode");
                        // Speech completed, now let's check if we should use background listening
                        if (alwaysListening) {
                            // Start background listening
                            Log.d(TAG, "Using background listening mode");
                            startBackgroundListening();
                        } else {
                            // Normal flow - start active listening
                            Log.d(TAG, "Using active listening mode");
                            startListening();
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

    public void enableAlwaysListening(boolean enable) {
        this.alwaysListening = enable;
        Log.d(TAG, "Always listening mode " + (enable ? "enabled" : "disabled"));

        if (enable && conversationActive) {
            // Start background listening immediately
            startBackgroundListening();
        } else if (!enable) {
            // Stop background listening
            speechRecognitionManager.stopBackgroundListening();
        }
    }

    private void startBackgroundListening() {
        if (!alwaysListening || !conversationActive) {
            Log.d(TAG, "Not starting background listening - conditions not met: alwaysListening=" +
                    alwaysListening + ", conversationActive=" + conversationActive);
            return;
        }

        Log.d(TAG, "Starting background listening mode");

        if (stateListener != null) {
            stateListener.onPassiveListening();
        }

        // Create and set the listener right here using an anonymous implementation
        speechRecognitionManager.setSpeechActivityListener(new SpeechRecognitionManager.SpeechActivityDetector() {
            @Override
            public void onSpeechDetected() {
                Log.d(TAG, "Speech detected in background - switching to active listening");
                startActiveListening();
            }
        });

        // Then call the no-parameter version
        speechRecognitionManager.startBackgroundListening();
    }

    private void startActiveListening() {
        // Start normal speech recognition after detecting speech in background
        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                processUserInput(text);
            }

            @Override
            public void onSpeechError(String error) {
                // If speech recognition fails, go back to background listening
                startBackgroundListening();
            }
        });

        if (stateListener != null) {
            stateListener.onListening();
        }
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