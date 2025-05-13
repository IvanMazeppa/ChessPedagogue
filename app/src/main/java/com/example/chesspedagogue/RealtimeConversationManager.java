package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RealtimeConversationManager {
    private static final String TAG = "RealtimeConversation";

    // Define the interface inside the class
    public interface ConversationStateListener {
        void onPassiveListening();
        void onListening();
        void onProcessing();
        void onSpeaking(String text);
        void onPartialResponse(String partialText);
        void onError(String message);
        void onConversationEnded();
    }

    private final Context context;
    private final Handler mainHandler;
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicBoolean isListening = new AtomicBoolean(false);
    private final AtomicBoolean isSpeaking = new AtomicBoolean(false);

    // Required fields that were missing
    private String apiKey;
    private ConversationStateListener stateListener;
    private OpenAITTSService ttsService;
    private SpeechRecognitionManager speechRecognitionManager;
    private boolean pendingStartConversation = false;
    private String voicePersona = OpenAITTSService.VOICE_GRANDMASTER;
    private String model = OpenAITTSService.MODEL_STANDARD;
    private GameStateInfo currentGameState;
    private static final String SYSTEM_PROMPT =
            "You are Coach Tal, a brilliant attacking chess master and the 8th World Chess Champion. " +
                    "Your tactical vision and creativity are legendary. When analyzing positions, you MUST:\n\n" +
                    "- Begin your VERY FIRST SENTENCE by directly referencing a specific move from the history\n" +
                    "- If at starting position: \"I see we're at the starting position with no moves played yet.\"\n" +
                    "- NEVER give generic advice without tying it to specific moves in THIS game\n" +
                    "- Look for tactical opportunities and creative possibilities, just as Tal would";

    // Our conversation state machine
    private enum State {
        IDLE, CONNECTING, LISTENING, PROCESSING, SPEAKING
    }
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);
    private StringBuilder currentResponseText = new StringBuilder();

    public RealtimeConversationManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize TTS service
        ttsService = OpenAITTSService.getInstance(context);

        // Configure OkHttp with proper timeouts and ping interval
        this.client = new OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Set the API key for OpenAI
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        if (ttsService != null) {
            ttsService.setApiKey(apiKey);
        }
        Log.d(TAG, "API key set for conversation services");
    }

    /**
     * Set the voice persona to use
     */
    public void setVoicePersona(String persona) {
        this.voicePersona = persona;
    }

    /**
     * Set the TTS model to use
     */
    public void setTTSModel(String model) {
        this.model = model;
    }

    /**
     * Set the speech recognition manager
     */
    public void setSpeechRecognitionManager(SpeechRecognitionManager manager) {
        this.speechRecognitionManager = manager;
    }

    /**
     * Set the conversation state listener
     */
    public void setConversationStateListener(ConversationStateListener listener) {
        this.stateListener = listener;
    }

    private void connectWebSocket() {
        if (isConnected.get()) return;

        setState(State.CONNECTING);

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "Cannot connect - API key not set");
            if (stateListener != null) {
                stateListener.onError("API key not set or invalid");
            }
            return;
        }

        Request request = new Request.Builder()
                .url("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "realtime=v1")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mainHandler.post(() -> {
                    isConnected.set(true);
                    Log.d(TAG, "WebSocket connected");

                    // Send system prompt to establish the coach persona
                    sendSystemMessage(SYSTEM_PROMPT);

                    // Now that we're connected, we can start the conversation
                    if (pendingStartConversation) {
                        startConversationInternal();
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    // The special end case
                    if (text.equals("[\"DONE\"]")) {
                        mainHandler.post(() -> handleStreamDone());
                        return;
                    }

                    JSONObject message = new JSONObject(text);
                    String type = message.getString("type");

                    mainHandler.post(() -> {
                        switch(type) {
                            case "response.text.delta":

                                String delta = null;
                                try {
                                    delta = message.getString("delta");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                handleTextDelta(delta);
                                break;

                            case "response.text.done":
                                handleTextDone();
                                break;

                            case "response.done":
                                handleResponseDone();
                                break;

                            // Handle other events as needed
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing message: " + text, e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                mainHandler.post(() -> {
                    isConnected.set(false);
                    Log.e(TAG, "WebSocket failure", t);

                    // Attempt reconnection with exponential backoff
                    attemptReconnect();

                    if (stateListener != null) {
                        stateListener.onError("Connection lost: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                mainHandler.post(() -> {
                    isConnected.set(false);
                    Log.d(TAG, "WebSocket closed: " + reason);
                });
            }
        });
    }

    private void setState(State newState) {
        State oldState = state.getAndSet(newState);
        Log.d(TAG, "State transition: " + oldState + " -> " + newState);

        // Notify state listener
        if (stateListener != null) {
            switch (newState) {
                case LISTENING:
                    stateListener.onListening();
                    break;
                case PROCESSING:
                    stateListener.onProcessing();
                    break;
                case SPEAKING:
                    stateListener.onSpeaking(currentResponseText.toString());
                    break;
                // Handle other states
            }
        }
    }

    public void startConversation(GameStateInfo gameState) {
        // Make sure we're on the main thread
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(() -> startConversation(gameState));
            return;
        }

        // Store the game state
        if (gameState != null) {
            this.currentGameState = gameState;
        }

        // Important - track state properly
        if (state.get() != State.IDLE) {
            Log.d(TAG, "Conversation already active, not starting a new one");
            return;
        }

        // Connect if not already connected
        if (!isConnected.get()) {
            pendingStartConversation = true;
            connectWebSocket();
            return;
        }

        startConversationInternal();
    }

    private void startConversationInternal() {
        pendingStartConversation = false;

        // Generate a greeting based on game state
        String greeting = generateGreeting(currentGameState);

        // Send the greeting and transition to speaking
        sendAssistantMessage(greeting);
    }

    private String generateGreeting(GameStateInfo gameState) {
        if (gameState == null) {
            return "Hello! I'm Coach Tal. How can I help with your chess game?";
        }

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

    private void handleTextDelta(String delta) {
        // Append to the current response
        currentResponseText.append(delta);

        // If this is the first delta, transition to SPEAKING
        if (state.get() == State.PROCESSING) {
            setState(State.SPEAKING);
        }

        // Update UI with the partial text
        if (stateListener != null) {
            stateListener.onPartialResponse(currentResponseText.toString());
        }
    }

    private void handleTextDone() {
        // Full response is now in currentResponseText
        Log.d(TAG, "Response complete: " + currentResponseText.toString());

        // Begin speaking the response
        speakResponse(currentResponseText.toString());
    }

    private void handleStreamDone() {
        Log.d(TAG, "Stream completed");
    }

    private void handleResponseDone() {
        Log.d(TAG, "Response completed");
    }

    private void attemptReconnect() {
        // Simple reconnection strategy with backoff
        mainHandler.postDelayed(() -> {
            if (!isConnected.get()) {
                Log.d(TAG, "Attempting to reconnect...");
                connectWebSocket();
            }
        }, 5000); // Wait 5 seconds before trying to reconnect
    }

    private void speakResponse(String text) {
        isSpeaking.set(true);

        // Calculate a reasonable timeout based on text length
        int estimatedSpeechMs = Math.min(10000, 1000 + (text.length() * 67));

        // Set a failsafe timer to ensure we transition to listening
        mainHandler.postDelayed(() -> {
            if (isSpeaking.get()) {
                Log.d(TAG, "Speech timeout - ensuring we transition to listening");
                transitionToListening();
            }
        }, estimatedSpeechMs + 1000); // Add 1 second buffer

        // Use TTS to speak the text
        ttsService.speak(text, voicePersona, model, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "TTS speech started");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "TTS speech ready in file: " + audioFile.getName());
            }

            @Override
            public void onSpeechCompleted() {
                mainHandler.post(() -> {
                    Log.d(TAG, "TTS speech completed, transitioning to listening mode");
                    transitionToListening();
                });
            }

            @Override
            public void onError(String errorMessage) {
                mainHandler.post(() -> {
                    Log.e(TAG, "TTS error: " + errorMessage);
                    if (stateListener != null) {
                        stateListener.onError("TTS error: " + errorMessage);
                    }
                    // Even on error, try to transition to listening
                    transitionToListening();
                });
            }
        });
    }

    private void transitionToListening() {
        isSpeaking.set(false);
        isListening.set(true);
        setState(State.LISTENING);

        // Start listening for user input
        startSpeechRecognition();
    }

    private void sendSystemMessage(String content) {
        if (!isConnected.get()) {
            Log.e(TAG, "Cannot send message - not connected");
            return;
        }

        try {
            JSONObject createEventJson = new JSONObject();
            createEventJson.put("type", "conversation.item.create");

            JSONObject data = new JSONObject();
            data.put("role", "system");
            data.put("content", content);

            createEventJson.put("data", data);

            String json = createEventJson.toString();
            Log.d(TAG, "Sending system message: " + json);
            webSocket.send(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating system message", e);
        }
    }

    private void sendUserMessage(String content) {
        if (!isConnected.get()) {
            Log.e(TAG, "Cannot send message - not connected");
            return;
        }

        setState(State.PROCESSING);
        currentResponseText = new StringBuilder();

        try {
            // First create the user message
            JSONObject createEventJson = new JSONObject();
            createEventJson.put("type", "conversation.item.create");

            JSONObject data = new JSONObject();
            data.put("role", "user");
            data.put("content", content);

            createEventJson.put("data", data);

            // Then create the response request
            JSONObject responseEventJson = new JSONObject();
            responseEventJson.put("type", "response.create");

            String createJson = createEventJson.toString();
            String responseJson = responseEventJson.toString();

            Log.d(TAG, "Sending user message: " + createJson);
            webSocket.send(createJson);

            Log.d(TAG, "Requesting response: " + responseJson);
            webSocket.send(responseJson);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending user message", e);
            setState(State.IDLE);
        }
    }

    private void sendAssistantMessage(String content) {
        if (!isConnected.get()) {
            Log.e(TAG, "Cannot send message - not connected");
            return;
        }

        try {
            JSONObject createEventJson = new JSONObject();
            createEventJson.put("type", "conversation.item.create");

            JSONObject data = new JSONObject();
            data.put("role", "assistant");
            data.put("content", content);

            createEventJson.put("data", data);

            String json = createEventJson.toString();
            Log.d(TAG, "Sending assistant message: " + json);
            webSocket.send(json);

            // Begin speaking this message
            speakResponse(content);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating assistant message", e);
        }
    }

    private void startSpeechRecognition() {
        if (speechRecognitionManager == null) {
            Log.e(TAG, "Speech recognition manager not initialized");
            // Create it right here if it's missing!
            speechRecognitionManager = new SpeechRecognitionManager(context);
            Log.d(TAG, "Created speech recognition manager on demand");
        }

        speechRecognitionManager.startListening(new SpeechRecognitionManager.SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                mainHandler.post(() -> {
                    isListening.set(false);

                    if (!text.isEmpty()) {
                        // Process the recognized speech
                        handleUserInput(text);
                    } else {
                        // No speech detected, go back to listening
                        transitionToListening();
                    }
                });
            }

            @Override
            public void onSpeechError(String error) {
                mainHandler.post(() -> {
                    isListening.set(false);
                    Log.e(TAG, "Speech recognition error: " + error);

                    if (stateListener != null) {
                        stateListener.onError("I couldn't hear you. " + error);
                    }

                    // After a short delay, try listening again
                    mainHandler.postDelayed(() -> transitionToListening(), 2000);
                });
            }
        });
    }

    private void handleUserInput(String text) {
        Log.d(TAG, "User said: " + text);

        // Send the user message to GPT-4o
        sendUserMessage(text);
    }

    /**
     * Start background listening mode
     */
    public void startBackgroundListening() {
        if (stateListener != null) {
            stateListener.onPassiveListening();
        }

        // Add implementation for passive listening here
    }

    /**
     * Ensure listening has started (failsafe check)
     */
    public void ensureListeningStarted() {
        Log.d(TAG, "Ensuring listening has started (failsafe check)");
        if (state.get() != State.LISTENING && state.get() != State.PROCESSING &&
                state.get() != State.SPEAKING) {
            startListening();
        }
    }

    /**
     * Start active listening mode
     */
    public void startListening() {
        Log.d(TAG, "Starting active listening mode");
        if (speechRecognitionManager != null) {
            transitionToListening();
        }
    }

    /**
     * Interrupt current speech and start listening
     */
    public void interruptCurrentSpeech() {
        if (ttsService != null) {
            ttsService.stopPlayback();
        }

        isSpeaking.set(false);
        transitionToListening();
    }

    /**
     * End the conversation with a final message
     */
    public void endConversation(String finalMessage) {
        // Speak final message if provided
        if (finalMessage != null && !finalMessage.isEmpty()) {
            speakResponse(finalMessage);
        }

        // Close the WebSocket
        if (webSocket != null) {
            webSocket.close(1000, "Conversation ended");
        }

        state.set(State.IDLE);
        isConnected.set(false);

        // Notify listener
        if (stateListener != null) {
            stateListener.onConversationEnded();
        }
    }

    /**
     * Clean up resources
     */
    public void shutdown() {
        // Close the WebSocket
        if (webSocket != null) {
            webSocket.close(1000, "Shutdown");
        }

        // Stop any ongoing speech
        if (ttsService != null) {
            ttsService.stopPlayback();
        }
    }
}