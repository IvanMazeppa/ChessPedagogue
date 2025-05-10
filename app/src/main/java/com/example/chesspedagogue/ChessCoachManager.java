package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

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

    private static ChessCoachManager instance;
    private final Context context;
    private final OpenAIService openAIService;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private String apiKey; // Add missing apiKey field

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
    private static final int MAX_CONVERSATION_TURNS = 5; // Prevent infinite loops

    // Callback interface for responses
    public interface ChessCoachCallback {
        void onResponseReceived(String response);
        void onError(String errorMessage);
        void onSpeechCompleted();
    }

    // Add this interface for voice recognition control
    public interface VoiceRecognitionController {
        void startListening();
        boolean isInConversationMode();
    }

    private VoiceRecognitionController voiceController;
    private ChessCoachCallback currentCallback;

    public void setVoiceController(VoiceRecognitionController controller) {
        this.voiceController = controller;
    }

    private ChessCoachManager(Context context) {
        this.context = context.getApplicationContext();
        this.openAIService = OpenAIService.getInstance();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        // Initialize OpenAI TTS
        this.openAITTSService = new OpenAITTSService(context);
        initTextToSpeech();
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
                public void onSpeechReady(File audioFile) {
                    openAITTSService.playAudio(audioFile, () -> {
                        // When speech completes, notify any listeners
                        mainHandler.post(() -> {
                            if (currentCallback != null) {
                                currentCallback.onSpeechCompleted();
                            }
                        });
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "TTS error: " + errorMessage);
                    // Fall back to regular TTS
                    useFallbackTTS(response);
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
        this.apiKey = apiKey;
        openAIService.setApiKey(apiKey);
    }

    /**
     * Test the current voice settings with a sample phrase
     */
// In ChessCoachManager.java
    public void testVoice(String testPhrase, ChessCoachCallback callback) {
        this.currentCallback = callback;
        speakResponse(testPhrase);
    }

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

    // Create a robust utterance progress listener
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
     * Get enhanced chess advice with game state context
     */
    /**
     * Get enhanced chess advice with game state context
     */
    public void getEnhancedChessAdvice(GameStateInfo gameState, ChessCoachCallback callback) {
        this.currentCallback = callback;

        executorService.execute(() -> {
            try {
                String fen = gameState.getCurrentFen();
                List<String> moveHistory = gameState.getMoveHistory();
                String playerColor = gameState.getPlayerColor();

                Log.d(TAG, "Generating enhanced chess advice for position: " + fen);

                // 1. First, analyze the position and update our context tracking
                analyzeGameContext(fen, moveHistory, playerColor);

                // 2. Generate the advice with all the enhanced context
                String response = openAIService.generateEnhancedChessAdvice(fen, moveHistory, playerColor);

                // 3. Deliver the response on the main thread
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
     * Process voice commands from the user, connecting them to the current chess position
     * and providing contextual responses.
     *
     * @param command The recognized speech command from the user
     */
    /*
    private void processVoiceCommand(String command) {
        Log.d(TAG, "Voice command: " + command);

        // Show what was recognized
        Toast.makeText(this, "You said: " + command, Toast.LENGTH_SHORT).show();

        // Create a GameStateInfo object with the current state
        GameStateInfo gameState = new GameStateInfo(
                engine.getCurrentFEN(),
                algebraicMoveHistory,
                playerColorChoice
        );

        // Convert command to lowercase for easier parsing
        String lowercaseCommand = command.toLowerCase();

        // Check for move commands
        if (lowercaseCommand.contains("move ")) {
            // This would need natural language parsing to convert to UCI
            // For now, we'll just pass it to the coach
            showLoading("Coach is analyzing your move request...");
            chessCoach.sendMessage(command, gameState, new ChessCoachCallback());
            return;
        }

        // Check for position advice requests
        if (lowercaseCommand.contains("what should i do") ||
                lowercaseCommand.contains("what's my best move") ||
                lowercaseCommand.contains("help me") ||
                lowercaseCommand.contains("advice") ||
                lowercaseCommand.contains("analyze") ||
                lowercaseCommand.contains("suggestion")) {

            // This is a request for position advice - use the current board state!
            showLoading("Coach is analyzing your position...");
            chessCoach.getEnhancedChessAdvice(gameState, new ChessCoachCallback());
            return;
        }

        // Check for evaluation requests
        if (lowercaseCommand.contains("who's winning") ||
                lowercaseCommand.contains("who is winning") ||
                lowercaseCommand.contains("evaluation") ||
                lowercaseCommand.contains("am i winning") ||
                lowercaseCommand.contains("score")) {

            showLoading("Coach is evaluating the position...");
            chessCoach.sendMessage("Please evaluate who's winning in this position and by approximately how much.",
                    gameState, new ChessCoachCallback());
            return;
        }

        // Default: treat as a general question, but INCLUDE the board context
        showLoading("Coach is considering your question...");
        chessCoach.sendMessage(command, gameState, new ChessCoachCallback());
    }

    */

    /**
     * Process a follow-up command in an ongoing conversation
     */
   /*
    private void processFollowUpCommand(String command) {
        Log.d(TAG, "Follow-up command: " + command);

        // Show what was recognized
        Toast.makeText(this, "You said: " + command, Toast.LENGTH_SHORT).show();

        // Create a GameStateInfo object with the current state
        GameStateInfo gameState = new GameStateInfo(
                engine.getCurrentFEN(),
                algebraicMoveHistory,
                playerColorChoice
        );

        // Send to the coach with our special callback that continues the conversation
        showLoading("Coach is thinking...");
        chessCoach.sendMessage(command, gameState, new ConversationContinuingCallback());
    } */

    // Keep the old method for compatibility
    public void getEnhancedChessAdvice(String fen, List<String> moveHistory,
                                       String playerColor, ChessCoachCallback callback) {
        GameStateInfo gameState = new GameStateInfo(fen, moveHistory, playerColor);
        getEnhancedChessAdvice(gameState, callback);
    }






    /**
     * Analyze the game context to enhance future responses
     */
    private void analyzeGameContext(String fen, List<String> moveHistory, String playerColor) {
        try {
            // Determine game phase
            String gamePhase = "opening";
            if (moveHistory != null) {
                int moveCount = moveHistory.size();
                if (moveCount < 10) {
                    gamePhase = "opening";
                } else if (moveCount < 30) {
                    gamePhase = "middlegame";
                } else {
                    gamePhase = "endgame";
                }
            }

            // Update context with current game state
            openAIService.updateContext("currentPosition", fen);
            openAIService.updateContext("playerColor", playerColor);
            openAIService.updateContext("gamePhase", gamePhase);

            // Check for chess patterns and store them
            checkPositionPatterns(fen, playerColor, gamePhase);

            Log.d(TAG, "Game context analysis complete for phase: " + gamePhase);
        } catch (Exception e) {
            Log.e(TAG, "Error in analyzeGameContext", e);
            // Continue execution despite analysis errors
        }
    }

    /**
     * Analyze position for common chess patterns
     */
    private void checkPositionPatterns(String fen, String playerColor, String gamePhase) {
        // Check for undeveloped pieces in opening/middlegame
        if (gamePhase.equals("opening") || gamePhase.equals("middlegame")) {
            if (fen.contains("N1") || fen.contains("B1") ||
                    fen.contains("n8") || fen.contains("b8")) {
                openAIService.recordPlayerMistake("undeveloped pieces");
            }
        }

        // Check for king safety issues
        if (fen.contains("+") || isKingExposed(fen, playerColor)) {
            openAIService.recordPlayerMistake("king safety");
        }
    }

    /**
     * Helper method to check if king is exposed (simplified)
     */
    private boolean isKingExposed(String fen, String playerColor) {
        // Simple check - look for lack of pawns near king
        if (playerColor.equalsIgnoreCase("white")) {
            return fen.contains("K") && !fen.contains("KP");
        } else {
            return fen.contains("k") && !fen.contains("kp");
        }
    }

    /**
     * Send a user message to the chess coach
     */
    /**
     * Send a user message to the chess coach with the current game state
     */
    public void sendMessage(String message, GameStateInfo gameState, ChessCoachCallback callback) {
        this.currentCallback = callback;

        executorService.execute(() -> {
            try {
                // Use the game state info instead of trying to access variables directly
                String currentFen = gameState.getCurrentFen();
                List<String> moveHistory = gameState.getMoveHistory();
                String playerColor = gameState.getPlayerColor();

                // Create enriched prompt with game context
                String enrichedMessage =
                        "Current board position (FEN): " + currentFen + "\n\n" +
                                "Move history: " + formatMoveHistory(moveHistory) + "\n\n" +
                                "I'm playing as " + playerColor + ".\n\n" +
                                "User question: " + message;

                // Send the enriched message
                String response = openAIService.sendMessage(enrichedMessage);
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

    // Keep the original method for compatibility with existing code
    public void sendMessage(String message, ChessCoachCallback callback) {
        // Use empty game state or default values
        GameStateInfo emptyState = new GameStateInfo(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", // Starting position
                new ArrayList<>(),
                "white"
        );
        sendMessage(message, emptyState, callback);
    }

    private String formatMoveHistory(List<String> moves) {
        if (moves == null || moves.isEmpty()) return "No moves played yet.";

        StringBuilder sb = new StringBuilder();
        int moveNum = 1;
        for (int i = 0; i < moves.size(); i += 2) {
            sb.append(moveNum).append(". ");
            sb.append(moves.get(i));
            if (i + 1 < moves.size()) {
                sb.append(" ").append(moves.get(i + 1));
            }
            sb.append(" ");
            moveNum++;
        }
        return sb.toString();
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