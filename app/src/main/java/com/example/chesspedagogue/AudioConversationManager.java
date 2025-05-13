package com.example.chesspedagogue;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class AudioConversationManager {
    private static final String TAG = "AudioConversation";
    private static final String WEBSOCKET_URL = "wss://api.openai.com/v1/audio/conversations";

    // Audio configuration
    private static final int SAMPLE_RATE = 16000;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int BUFFER_SIZE = 8192;
    private static final int PLAYBACK_SAMPLE_RATE = 24000;

    // State management
    public enum State { IDLE, CONNECTING, LISTENING, PROCESSING, SPEAKING }
    private State currentState = State.IDLE;

    // Core components
    private final Context context;
    private final Handler mainHandler;
    private final OkHttpClient client;
    private final ExecutorService executorService;
    private WebSocket webSocket;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private final AtomicBoolean isSpeaking = new AtomicBoolean(false);
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private String apiKey;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int INITIAL_RECONNECT_DELAY_MS = 1000; // 1 second

    // Missing variables that caused compilation errors
    private ConversationListener listener;
    private String systemPrompt = "You are Coach Tal, a brilliant attacking chess master and the 8th World Chess Champion.";
    private boolean pendingStartConversation = false;
    private GameStateInfo currentGameState = null;
    private StringBuilder currentResponseText = new StringBuilder();
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);
    private OpenAITTSService ttsService;

    // Add this field declaration near your other class variables
    private ExoPlayer player;

    // You'll also need these for managing audio playback
    private final ConcurrentLinkedQueue<File> audioQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private ConversationListener stateListener;
    // Then add this method to set the listener
    public void setConversationListener(ConversationListener listener) {
        this.stateListener = listener;
    }


    // Listener interface
    public interface ConversationListener {
        void onStateChanged(State newState);
        void onConnected();
        void onError(String message);
        void onTextResponse(String text);
        void onPartialResponse(String partialText);
    }

    public AudioConversationManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.client = new OkHttpClient();
        this.executorService = Executors.newCachedThreadPool();
        this.ttsService = OpenAITTSService.getInstance(context);

        // Initialize audio playback
        initAudioPlayback();

        // Initialize the audio player
        player = new SimpleExoPlayer.Builder(context).build();

        // Set up completion listener for continuous playback
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    playNextInQueue(); // You'll need to implement this method
                }
            }
        });
    }

    /**
     * Play the next audio file in the queue
     */
    private void playNextInQueue() {
        File nextFile = audioQueue.poll();
        if (nextFile == null) {
            isPlaying.set(false);
            return;
        }

        isPlaying.set(true);

        try {
            // Create media source
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, "ChessPedagogue");
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.fromFile(nextFile)));

            // Prepare and play
            player.setMediaSource(mediaSource);
            player.prepare();
            player.play();
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio file", e);
            // Try the next file on error
            isPlaying.set(false);
            playNextInQueue();
        }
    }
    private void initAudioPlayback() {
        int minBufferSize = AudioTrack.getMinBufferSize(
                PLAYBACK_SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                ENCODING);

        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setSampleRate(PLAYBACK_SAMPLE_RATE)
                        .setEncoding(ENCODING)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build();
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        if (ttsService != null) {
            ttsService.setApiKey(apiKey);
        }
    }

     public void setSystemPrompt(String prompt) {
        this.systemPrompt = prompt;
    }

    /**
     * Establishes a WebSocket connection to OpenAI's Realtime API
     * This method handles the complete connection lifecycle
     */
    private void connectWebSocket() {
        if (isConnected.get()) {
            Log.d(TAG, "WebSocket already connected, not connecting again");
            return;
        }

        setState(State.CONNECTING);

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "Cannot connect - API key not set");
            if (listener != null) {
                listener.onError("API key not set or invalid");
            }
            return;
        }

        // The current correct URL format for GPT-4o Realtime
        String wsUrl = "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview";

        Log.d(TAG, "Connecting to WebSocket: " + wsUrl);

// In your connection request, add audio configuration
        Request request = new Request.Builder()
                .url("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "realtime=v1")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.i(TAG, "WebSocket connected: " + response.message());

                mainHandler.post(() -> {
                    isConnected.set(true);

                    if (listener != null) {
                        listener.onConnected();
                    }

                    // Send system prompt to establish the coach persona
                    sendSystemMessage(systemPrompt);

                    // Now that we're connected, we can start the conversation
                    if (pendingStartConversation) {
                        startConversationInternal();
                    }
                });
            }


            // In AudioConversationManager, modify onMessage method
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    // Skip "DONE" messages
                    if (text.equals("[\"DONE\"]")) {
                        mainHandler.post(() -> handleStreamDone());
                        return;
                    }

                    JSONObject message = new JSONObject(text);
                    String type = message.getString("type");
                    Log.d(TAG, "Processing message of type: " + type);

                    mainHandler.post(() -> {
                        try {
                            switch(type) {
                                case "session.created":
                                    Log.d(TAG, "Session created successfully");
                                    break;
                                case "conversation.item.created":
                                    Log.d(TAG, "Message successfully created");
                                    break;
                                case "response.text.delta":
                                    String delta = message.getString("delta");
                                    handleTextDelta(delta);
                                    break;
                                case "response.text.done":
                                    handleTextDone();
                                    break;
                                case "response.done":
                                    handleResponseDone();
                                    break;
                                // For now, let's just log audio chunks but not try to play them yet
                                case "response.audio.chunk":
                                    Log.d(TAG, "Received audio chunk - will implement playback later");
                                    break;
                                default:
                                    Log.d(TAG, "Received message of type: " + type + " (handled gracefully)");
                                    break;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing message", e);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing message: " + text, e);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + code + " / " + reason);
                webSocket.close(1000, "Client closing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + code + " / " + reason);
                mainHandler.post(() -> {
                    isConnected.set(false);
                    setState(State.IDLE);

                    if (listener != null) {
                        listener.onError("Connection closed: " + reason);
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                String responseCode = response != null ?
                        String.valueOf(response.code()) : "no response";

                Log.e(TAG, "WebSocket failure: " + t.getMessage() +
                        " with response code: " + responseCode, t);

                mainHandler.post(() -> {
                    isConnected.set(false);
                    setState(State.IDLE);

                    // Attempt reconnection with exponential backoff
                    attemptReconnect();

                    if (listener != null) {
                        listener.onError("Connection error: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void attemptReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnection attempts reached (" + MAX_RECONNECT_ATTEMPTS + ")");
            reconnectAttempts = 0; // Reset for next time

            mainHandler.post(() -> {
                if (listener != null) {
                    listener.onError("Failed to reconnect after multiple attempts. Please try again later.");
                }
            });
            return;
        }

        // Calculate delay with exponential backoff (1s, 2s, 4s, 8s, 16s)
        int delayMs = INITIAL_RECONNECT_DELAY_MS * (1 << reconnectAttempts);
        reconnectAttempts++;

        Log.d(TAG, "Attempting to reconnect in " + delayMs + "ms (attempt " + reconnectAttempts + ")");

        mainHandler.postDelayed(() -> {
            if (!isConnected.get()) {
                Log.d(TAG, "Reconnect attempt " + reconnectAttempts);
                connectWebSocket();
            }
        }, delayMs);
    }

    /**
     * Process audio data chunks from the realtime API
     * This method handles incoming audio from GPT-4o
     */
    private void handleAudioChunk(byte[] audioData) {
        Log.d(TAG, "Received audio chunk of size: " + (audioData != null ? audioData.length : 0) + " bytes");

        try {
            // Create a temporary file for this audio chunk
            File tempFile = File.createTempFile("coach_audio_", ".mp3", context.getCacheDir());

            // Write the audio data to the file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(audioData);
            fos.close();

            // Play this audio file using a simple MediaPlayer approach
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(tempFile.getPath());
            player.prepare();
            player.start();

            // Set a completion listener to clean up
            player.setOnCompletionListener(mp -> {
                mp.release();
                tempFile.delete();
            });

        } catch (IOException e) {
            Log.e(TAG, "Error handling audio chunk", e);
        }
    }


    /**
     * Play an audio chunk using ExoPlayer
     */
    private void playAudioChunk(File audioFile) {
        try {
            // Create media source
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, "ChessPedagogue");
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.fromFile(audioFile)));

            // Prepare and play
            player.setMediaSource(mediaSource);
            player.prepare();
            player.play();
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio chunk", e);
        }
    }


    /**
     * Send a system message to establish the conversation context
     */
    private void sendSystemMessage(String content) {
        if (!isConnected.get()) {
            Log.e(TAG, "Cannot send message - not connected");
            return;
        }

        try {
            // Create the message with the correct type: conversation.item.create
            JSONObject messageJson = new JSONObject();
            messageJson.put("type", "conversation.item.create");

            // Create the item object
            JSONObject item = new JSONObject();
            item.put("type", "message");
            item.put("role", "system");

            // Create content as an array of objects with the correct type: input_text
            JSONArray contentArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            contentObject.put("type", "input_text");
            contentObject.put("text", content);
            contentArray.put(contentObject);

            // Add the content array to the item
            item.put("content", contentArray);

            // Add the item to the main message
            messageJson.put("item", item);

            String json = messageJson.toString();
            Log.d(TAG, "Sending system message: " + json);
            webSocket.send(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating system message", e);
        }
    }

    /**
     * Send a message from the assistant
     */
    private void sendAssistantMessage(String content) {
        Log.d(TAG, "📤 Sending assistant message: " + content.substring(0, Math.min(30, content.length())) + "...");
        if (!isConnected.get()) {
            Log.e(TAG, "Cannot send message - not connected");
            return;
        }

        try {
            // Create the message with the correct structure
            JSONObject messageJson = new JSONObject();
            messageJson.put("type", "conversation.item.create");

            // Create the item object
            JSONObject item = new JSONObject();
            item.put("type", "message");
            item.put("role", "assistant");

            // Create content as an array of objects with the correct type: input_text
            JSONArray contentArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            contentObject.put("type", "input_text");
            contentObject.put("text", content);
            contentArray.put(contentObject);

            // Add the content array to the item
            item.put("content", contentArray);

            // Add the item to the main message
            messageJson.put("item", item);

            String json = messageJson.toString();
            Log.d(TAG, "Sending assistant message: " + json);
            webSocket.send(json);

            // Begin speaking this message
            speakResponse(content);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating assistant message", e);
        }
    }


    /**
     * Send a user message to the API
     * This method should be public so it can be called from MainActivity
     */
    // In AudioConversationManager.java
    // Add this to your AudioConversationManager class
    public void sendUserMessage(String content) {
        Log.d(TAG, "📤 Attempting to send user message: " + content.substring(0, Math.min(30, content.length())) + "...");
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

            JSONObject item = new JSONObject();
            item.put("type", "message");
            item.put("role", "user");

            // Create content as an array of objects
            JSONArray contentArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            contentObject.put("type", "input_text");
            contentObject.put("text", content);
            contentArray.put(contentObject);

            // Add the content array to the item
            item.put("content", contentArray);

            // Add the item to the main message
            createEventJson.put("item", item);

            // Send the message
            webSocket.send(createEventJson.toString());

            // Then request a response with audio
            JSONObject responseEventJson = new JSONObject();
            responseEventJson.put("type", "response.create");

            // Request both text and audio response
            JSONObject responseOptions = new JSONObject();
            responseOptions.put("output_audio", true);
            responseOptions.put("output_audio_format", "mp3");
            responseEventJson.put("options", responseOptions);

            webSocket.send(responseEventJson.toString());
            Log.d(TAG, "📤 Successfully sent user message and requested response");

        } catch (JSONException e) {
            Log.e(TAG, "Error sending user message", e);
            setState(State.IDLE);
        }
    }




    private void startConversationInternal() {
        Log.d(TAG, "🔄 Starting conversation internally - about to generate greeting");
        pendingStartConversation = false;
        Log.d(TAG, "Starting conversation internally");

        // Generate a greeting based on game state
        String greeting = generateGreeting(currentGameState);

        // Send the greeting and transition to speaking
        sendAssistantMessage(greeting);
        // After sending the greeting
        Log.d(TAG, "🧪 Testing direct message sending...");
        new Handler().postDelayed(() -> {
            sendUserMessage("Analyze my opening moves in this chess game please.");
        }, 3000); // Send a test message after 3 seconds
    }

    /**
     * Generate an appropriate greeting based on game state
     */
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

    /**
     * Handle a text delta event from the API
     */
    private void handleTextDelta(String delta) {
        // Append to the current response
        currentResponseText.append(delta);

        // If this is the first delta, transition to SPEAKING state
        if (state.get() == State.PROCESSING) {
            setState(State.SPEAKING);
        }

        // Update UI with the partial text
        if (stateListener != null) {
            stateListener.onPartialResponse(currentResponseText.toString());
        }

        // Log for debugging
        Log.d(TAG, "📥 Received text delta: '" + delta + "'");
    }

    /**
     * Handle text completion event
     */
    private void handleTextDone() {
        // Full response is now in currentResponseText
        Log.d(TAG, "Response complete: " + currentResponseText.toString());

        // Begin speaking the response
        speakResponse(currentResponseText.toString());
    }

    /**
     * Handle stream completion
     */
    private void handleStreamDone() {
        Log.d(TAG, "Stream completed");
    }

    /**
     * Handle response completion
     */
    private void handleResponseDone() {
        Log.d(TAG, "Response completed");
    }

    /**
     * Start a new conversation
     */
    public void startConversation() {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "Cannot start conversation - API key not set");
            mainHandler.post(() -> {
                if (listener != null) {
                    listener.onError("API key not set. Please configure OpenAI API key in settings.");
                }
            });
            return;
        }

        // Add debug logging to verify API key
        Log.d(TAG, "Starting conversation with API key: " +
                (apiKey != null ? (apiKey.substring(0, Math.min(5, apiKey.length())) + "...") : "null"));

        setState(State.CONNECTING);
        connectWebSocket();
    }

    /**
     * Start a conversation with a specific game state
     */
    public void startConversation(GameStateInfo gameState) {
        this.currentGameState = gameState;
        this.pendingStartConversation = true;
        startConversation();
    }

    /**
     * End the current conversation
     */
    public void endConversation() {
        if (webSocket != null) {
            try {
                webSocket.close(1000, "User ended conversation");
            } catch (Exception e) {
                Log.e(TAG, "Error closing WebSocket", e);
            }
        }

        isConnected.set(false);
        setState(State.IDLE);
    }



    /**
     * Set the current state and notify listeners
     */
    private void setState(State newState) {
        Log.d(TAG, "State changing: " + currentState + " -> " + newState);
        currentState = newState;
        state.set(newState);

        mainHandler.post(() -> {
            if (listener != null) {
                listener.onStateChanged(newState);
            }
        });
    }



    /**
     * Speak the provided text using TTS
     */
    private void speakResponse(String text) {
        if (ttsService != null) {
            ttsService.speak(text, OpenAITTSService.VOICE_GRANDMASTER,
                    OpenAITTSService.MODEL_STANDARD,
                    new OpenAITTSService.TTSCallback() {
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
                            Log.d(TAG, "TTS speech completed");
                            setState(State.LISTENING);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "TTS error: " + errorMessage);
                            setState(State.LISTENING);
                        }
                    });
        } else {
            Log.e(TAG, "TTS service not initialized");
            setState(State.LISTENING);
        }
    }

    /**
     * Get the current state
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Clean up resources when done
     */
    public void release() {
        endConversation();

        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }

        executorService.shutdown();
    }
}