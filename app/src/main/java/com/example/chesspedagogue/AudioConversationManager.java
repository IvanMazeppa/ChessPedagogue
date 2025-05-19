package com.example.chesspedagogue;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Manages audio conversations with OpenAI's real-time API.
 * Handles WebSocket connection, audio capture, and audio playback.
 */
public class AudioConversationManager {

    private static final String TAG = "AudioConversationManager";
    private static final int SAMPLE_RATE = 16000; // 16 kHz as required by OpenAI
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE_FACTOR = 2; // Multiplier for the buffer size

    // ────────────────────────────────────────────────────────────
    //  State and configuration
    // ────────────────────────────────────────────────────────────
    // Core state
    private final Context appCtx;
    private final TextView tv;
    // Concurrent execution
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    // WebSocket components
    private final OkHttpClient client;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private final int bufferSize;
    private ConversationListener listener;
    private volatile State current = State.IDLE;
    private String apiKey = "";
    private String systemPrompt = "";
    private volatile boolean pendingStart = false;
    private volatile WebSocket webSocket = null;
    // Audio recording
    private AudioRecord audioRecord;
    // Audio playback
    private AudioTrack audioTrack;
    private int audioTrackState = AudioTrack.STATE_UNINITIALIZED;
    private StringBuilder currentResponseText = new StringBuilder();
    // Add this field to the class
    private String currentAudioItemId;

    /**
     * Convenience: supply a TextView that should mirror the live transcript.
     */
    public AudioConversationManager(@NonNull TextView transcriptView) {
        appCtx = transcriptView.getContext().getApplicationContext();
        this.tv = transcriptView;

        // Initialize OkHttpClient with proper timeout settings
        client = new OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Calculate buffer size for audio recording
        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            minBufferSize = SAMPLE_RATE * 2; // Default to 2 seconds buffer
        }
        bufferSize = minBufferSize * BUFFER_SIZE_FACTOR;
    }

    /**
     * Or just any Context if you don't need a live TextView.
     */
    public AudioConversationManager(@NonNull Context ctx) {
        appCtx = ctx.getApplicationContext();
        tv = null;

        // Initialize OkHttpClient with proper timeout settings
        client = new OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Calculate buffer size for audio recording
        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            minBufferSize = SAMPLE_RATE * 2; // Default to 2 seconds buffer
        }
        bufferSize = minBufferSize * BUFFER_SIZE_FACTOR;
    }

    public void setApiKey(@NonNull String key) {
        apiKey = key;
    }

    // ----- configuration -----------------------------------------------------

    public void setSystemPrompt(@NonNull String p) {
        systemPrompt = p;
    }

    /**
     * Opens the websocket; safe to call repeatedly.
     */
    public void connect(@NonNull String key) {
        apiKey = key;
        if (isConnected()) {
            if (listener != null) listener.onConnected();
            return;
        }
        changeState(State.CONNECTING);
        io.execute(this::connectWebSocket);
    }

    /**
     * Start a conversation with proper format for OpenAI's realtime API
     */

    public void startConversation() {
        Log.d(TAG, "⭐ Starting simplified conversation approach");
        try {
            if (!isConnected()) {
                // Connection code stays the same
                connect(apiKey);
                pendingStart = true;
                return;
            }

            // Change state to LISTENING
            changeState(State.LISTENING);

            // Try sending a simple direct message without any item references
            JSONObject message = new JSONObject();
            message.put("type", "message");
            message.put("message", "Hello Coach Tal, can you help me analyze my chess position?");

            webSocket.send(message.toString());
            Log.d(TAG, "→ Sent direct message request without item reference");

        } catch (Exception e) {
            Log.e(TAG, "Error starting conversation", e);
            if (listener != null)
                listener.onError("Error starting conversation: " + e.getMessage());
        }
    }

    // ----- lifecycle ---------------------------------------------------------

    /**
     * Start capturing audio from the microphone with the correct item ID
     */
    private void startMicrophoneCapture(String itemId) {
        // Check permission first
        if (ActivityCompat.checkSelfPermission(appCtx, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ Missing RECORD_AUDIO permission");
            if (listener != null) {
                listener.onError("Microphone permission not granted");
            }
            return;
        }

        // Stop any existing recording
        stopMicrophoneCapture();

        try {
            // Initialize AudioRecord
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
            );

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "❌ AudioRecord initialization failed");
                if (listener != null) {
                    listener.onError("Failed to initialize microphone");
                }
                return;
            }

            // Start recording
            audioRecord.startRecording();
            isRecording.set(true);
            Log.d(TAG, "✅ Microphone capture started successfully for item: " + itemId);

            // Start a thread to read audio data and send it to the API
            // THIS LINE NEEDED UPDATING - Change from method reference to lambda with parameter
            io.execute(() -> processAudioData(itemId));
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting microphone capture", e);
            if (listener != null) {
                listener.onError("Error starting microphone: " + e.getMessage());
            }
        }
    }

    /** Starts (or queues) a conversation. */

    /**
     * Send an initial text message to get the conversation started
     * This is easier to debug than starting with audio directly
     */
    private void sendInitialTextMessage() {
        try {
            Log.d(TAG, "Starting conversation with initial text message");

            // Generate a unique item ID
            String itemId = "item_" + System.currentTimeMillis();

            // Create a conversation item with ID
            JSONObject createItem = new JSONObject();
            createItem.put("type", "conversation.item.create");
            createItem.put("id", itemId);  // Add the unique ID here
            JSONObject itemData = new JSONObject();
            itemData.put("role", "user");
            itemData.put("content", "Hello Coach Tal, can you help me analyze my chess position?");
            createItem.put("data", itemData);

            // Send the item creation message
            webSocket.send(createItem.toString());
            Log.d(TAG, "→ Created conversation item with ID: " + itemId);

            // Now request a response with reference to the item ID
            JSONObject createResponse = new JSONObject();
            createResponse.put("type", "response.create");
            JSONObject responseData = new JSONObject();
            responseData.put("item", itemId);  // Reference the item ID here
            createResponse.put("data", responseData);
            webSocket.send(createResponse.toString());
            Log.d(TAG, "→ Requested response for item: " + itemId);


        } catch (Exception e) {
            Log.e(TAG, "Error sending initial message", e);
        }
    }

    /**
     * Sends user text – MainActivity also calls the alias sendUserText().
     */
    public void sendUserMessage(@NonNull String text) {
        ensureConnected();
        changeState(State.PROCESSING);
        io.execute(() -> mockStreamingResponse(
                "✓ Got it! (echo)\n\n\"" + text + "\""));
    }

    /**
     * Get the conversation listener
     *
     * @return The current conversation listener
     */
    public ConversationListener getConversationListener() {
        return this.listener;
    }

    public void setConversationListener(ConversationListener l) {
        listener = l;
    }

    /**
     * Start the websocket connection
     *
     * @param apiKey The OpenAI API key
     */
    public void startWebSocketConnection(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not provided");
            return;
        }

        this.apiKey = apiKey;
        connect(apiKey);
    }

    // Add these methods to your AudioConversationManager.java class

    /**
     * Alias for sendUserMessage
     */
    public void sendUserText(@NonNull String text) {
        if (!isConnected()) {
            Log.e(TAG, "Cannot send message - not connected");
            if (listener != null) {
                listener.onError("Not connected to server. Please try again.");
            }
            return;
        }

        changeState(State.PROCESSING);
        currentResponseText = new StringBuilder();

        try {
            // Send message using the correct format
            JSONObject textMessage = new JSONObject();
            textMessage.put("type", "message");
            textMessage.put("message", text);

            String messageJson = textMessage.toString();
            Log.d(TAG, "→ Sending text message: " + messageJson);
            webSocket.send(messageJson);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending text message", e);
            changeState(State.IDLE);
            if (listener != null) {
                listener.onError("Failed to send message: " + e.getMessage());
            }
        }
    }

    public State getCurrentState() {
        return current;
    }

    public void endConversation() {
        stopMicrophoneCapture();
        changeState(State.IDLE);
    }

    public void close() {
        endConversation();
        tearDown();
    }

    public void release() {
        close();
        io.shutdownNow();
    }

    private void changeState(State s) {
        current = s;
        if (listener != null) mainHandler.post(() -> listener.onStateChanged(s));
    }

    private boolean isConnected() {
        return webSocket != null && isConnected.get();
    }

    // ────────────────────────────────────────────────────────────
    //  Private implementation methods
    // ────────────────────────────────────────────────────────────

    private void ensureConnected() {
        if (!isConnected()) throw new IllegalStateException("call connect() first");
    }

    private void connectWebSocket() {
        if (isConnected.get()) {
            Log.d(TAG, "Already connected, skipping connection");
            return;
        }

        if (!isNetworkAvailable()) {
            Log.e(TAG, "❌ No network connection available");
            if (listener != null) {
                mainHandler.post(() -> listener.onError("No internet connection. Please check your network settings."));
            }
            return;
        }

        Log.d(TAG, "→ CONNECTING to WebSocket...");

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ Cannot connect - API key not set");
            if (listener != null) {
                mainHandler.post(() -> listener.onError("API key not set or invalid"));
            }
            return;
        }

        // Create the WebSocket request with proper headers
        Request request = new Request.Builder()
                .url("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-12-17")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "realtime=v1")
                .build();

        // Log the request details (excluding the full API key)
        Log.d(TAG, "WebSocket request: " + request.url() + " with API key: " +
                apiKey.substring(0, 5) + "..." +
                (apiKey.length() > 10 ? apiKey.substring(apiKey.length() - 5) : ""));

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "✅ WebSocket CONNECTED: " + response.code() + " " + response.message());

                mainHandler.post(() -> {
                    isConnected.set(true);

                    // Send system prompt to establish the coach persona
                    if (systemPrompt != null && !systemPrompt.isEmpty()) {
                        try {
                            sendSystemMessage(systemPrompt);
                            Log.d(TAG, "✅ System prompt sent successfully");
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Failed to send system prompt", e);
                        }
                    }

                    if (listener != null) {
                        listener.onConnected();
                    }

                    // Now that we're connected, we can start the conversation
                    if (pendingStart) {
                        Log.d(TAG, "Executing pending conversation start");
                        pendingStart = false;
                        startConversation();
                    }
                });
            }


            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "⬇️ RECEIVED RAW MESSAGE: " + text);

                try {
                    if (text.equals("[\"DONE\"]")) {
                        Log.d(TAG, "← Received DONE marker");
                        mainHandler.post(() -> handleStreamDone());
                        return;
                    }

                    JSONObject jsonMessage = new JSONObject(text);
                    String messageType = jsonMessage.optString("type", "unknown");
                    Log.d(TAG, "← Message type: " + messageType);

                    // Handle different message types on the main thread
                    mainHandler.post(() -> {
                        try {
                            switch (messageType) {
                                case "session.created":
                                    Log.d(TAG, "Session created successfully");
                                    break;

                                // THIS IS THE MAGICAL PART WE WERE MISSING!
                                case "conversation.item.created":
                                    try {
                                        Log.d(TAG, "🎉 ITEM CREATED! Extracting server-provided ID...");
                                        JSONObject item = jsonMessage.getJSONObject("item");
                                        String serverItemId = item.getString("id");
                                        Log.d(TAG, "🔑 Server created item with ID: " + serverItemId);

                                        // Now we can request a response using this server-provided ID
                                        requestResponseForItem(serverItemId);

                                        // And also start audio capture if needed
                                        startMicrophoneCapture(serverItemId);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing item creation", e);
                                    }
                                    break;

                                case "response.audio.delta":
                                    try {
                                        String base64Audio = jsonMessage.optString("audio", "");
                                        if (!base64Audio.isEmpty()) {
                                            Log.d(TAG, "← Received audio chunk to play!");
                                            playAudioChunk(base64Audio);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing audio data", e);
                                    }
                                    break;

                                case "response.text.delta":
                                    String delta = jsonMessage.optString("delta", "");
                                    Log.d(TAG, "← Text delta: " + delta);
                                    handleTextDelta(delta);
                                    break;

                                case "error":
                                    JSONObject error = jsonMessage.optJSONObject("error");
                                    if (error != null) {
                                        String errorType = error.optString("type", "unknown");
                                        String errorCode = error.optString("code", "unknown");
                                        String errorMessage = error.optString("message", "No error message");
                                        Log.e(TAG, "⛔ Error: " + errorType + " - " + errorCode + ": " + errorMessage);

                                        if (listener != null) {
                                            listener.onError("Server error: " + errorMessage);
                                        }
                                    }
                                    break;

                                default:
                                    Log.d(TAG, "← Unhandled message type: " + messageType);
                                    break;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error processing message", e);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "❌ Error parsing message: " + text, e);
                }
            }

            // Helper method to request a response for a specific item
            private void requestResponseForItem(String itemId) {
                try {
                    JSONObject createResponse = new JSONObject();
                    createResponse.put("type", "response.create");

                    JSONObject responseData = new JSONObject();
                    responseData.put("item", itemId);
                    createResponse.put("data", responseData);

                    String jsonRequest = createResponse.toString();
                    Log.d(TAG, "→ Requesting response for item " + itemId + ": " + jsonRequest);
                    webSocket.send(jsonRequest);
                } catch (Exception e) {
                    Log.e(TAG, "Error requesting response", e);
                }
            }


            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + code + " " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + code + " " + reason);
                mainHandler.post(() -> {
                    isConnected.set(false);
                    if (code != 1000) { // Not a normal closure
                        if (listener != null) {
                            listener.onError("Connection closed: " + reason);
                        }
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                String responseCode = response != null ? " (" + response.code() + ")" : "";
                Log.e(TAG, "❌ WebSocket failure" + responseCode, t);

                mainHandler.post(() -> {
                    isConnected.set(false);

                    String errorMsg = t.getMessage();
                    if (response != null) {
                        try {
                            ResponseBody body = response.body();
                            if (body != null) {
                                errorMsg = body.string();
                            }
                        } catch (Exception e) {
                            // Ignore
                        }
                    }

                    if (listener != null) {
                        listener.onError("Connection error: " + errorMsg);
                    }

                    // Attempt reconnection with exponential backoff
                    attemptReconnect();
                });
            }
        });

        Log.d(TAG, "WebSocket connection initiated");
    }

    // Log all properties of the error object
    private void logDetailedError(JSONObject errorObj) {
        try {
            StringBuilder details = new StringBuilder("ERROR DETAILS:\n");
            Iterator<String> keys = errorObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                details.append(key).append(": ").append(errorObj.opt(key)).append("\n");
            }
            Log.e(TAG, details.toString());

            // Log specific fields if they exist
            if (errorObj.has("type")) {
                Log.e(TAG, "Error type: " + errorObj.getString("type"));
            }
            if (errorObj.has("code")) {
                Log.e(TAG, "Error code: " + errorObj.getString("code"));
            }
            if (errorObj.has("message")) {
                Log.e(TAG, "Error message: " + errorObj.getString("message"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error details", e);
        }
    }

    private void sendSystemMessage(String content) {
        if (!isConnected.get()) {
            Log.e(TAG, "❌ Cannot send system message - not connected");
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
            Log.d(TAG, "→ Sending system message: " +
                    (json.length() > 100 ? json.substring(0, 100) + "..." : json));

            webSocket.send(json);
        } catch (JSONException e) {
            Log.e(TAG, "❌ Error creating system message", e);
        }
    }

    /**
     * Stop microphone capture
     */
    private void stopMicrophoneCapture() {
        isRecording.set(false);
        if (audioRecord != null) {
            try {
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.stop();
                }
                audioRecord.release();
            } catch (Exception e) {
                Log.e(TAG, "❌ Error stopping microphone capture", e);
            }
            audioRecord = null;
        }
    }

    /**
     * Process audio data from the microphone and send it to the API
     * Following OpenAI's real-time API requirements
     */
    private void processAudioData(String serverItemId) {
        if (audioRecord == null || !isRecording.get()) {
            Log.e(TAG, "❌ AudioRecord not initialized or not recording");
            return;
        }

        // Create a buffer for reading audio data
        byte[] buffer = new byte[bufferSize / 4];

        try {
            // Read and send audio data while recording
            while (isRecording.get() && isConnected.get()) {
                int bytesRead = audioRecord.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    // Base64 encode the raw PCM data
                    String base64Audio = Base64.encodeToString(
                            Arrays.copyOf(buffer, bytesRead), Base64.NO_WRAP);

                    // Create audio message with the CORRECT format using server ID
                    JSONObject audioMessage = new JSONObject();
                    audioMessage.put("type", "input_audio_buffer.append");
                    audioMessage.put("item", serverItemId);  // Use server-provided ID
                    audioMessage.put("audio", base64Audio);

                    // Send the audio data
                    webSocket.send(audioMessage.toString());
                    Log.d(TAG, "→ Sent audio chunk for item: " + serverItemId);

                    // Small delay to avoid flooding
                    Thread.sleep(20);
                }
            }

            // When done, finalize the audio buffer
            JSONObject audioEnd = new JSONObject();
            audioEnd.put("type", "input_audio_buffer.done");
            audioEnd.put("item", serverItemId);  // Use server-provided ID
            webSocket.send(audioEnd.toString());
            Log.d(TAG, "→ Finalized audio buffer for item: " + serverItemId);

            // Request a response
            JSONObject createResponse = new JSONObject();
            createResponse.put("type", "response.create");
            JSONObject responseData = new JSONObject();
            responseData.put("item", serverItemId);  // Use server-provided ID
            createResponse.put("data", responseData);
            webSocket.send(createResponse.toString());
            Log.d(TAG, "→ Requested response for item: " + serverItemId);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error processing audio data", e);
        }
    }

    /**
     * Start capturing audio from the microphone and send it to the API
     */

    // Helper method to check network availability
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) appCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }

    /**
     * Process audio data from the microphone and send it to the API
     * Based on B4X forum and Microsoft documentation for OpenAI real-time API
     */

    private void playAudioChunk(String base64Audio) {
        try {
            // Decode base64 audio data
            byte[] audioData = Base64.decode(base64Audio, Base64.DEFAULT);

            // Initialize audio track if needed
            if (audioTrack == null || audioTrackState != AudioTrack.STATE_INITIALIZED) {
                initializeAudioTrack();
            }

            // Play the audio chunk
            if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                int result = audioTrack.write(audioData, 0, audioData.length);
                if (result < 0) {
                    Log.e(TAG, "❌ Error writing to AudioTrack: " + result);
                } else {
                    Log.d(TAG, "✓ Wrote " + result + " bytes to AudioTrack");

                    // Start playback if not already playing
                    if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                        audioTrack.play();
                        Log.d(TAG, "▶ Started AudioTrack playback");
                    }
                }
            } else {
                Log.e(TAG, "❌ AudioTrack not initialized");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error playing audio chunk", e);
        }
    }

    // Initialize audio track for playback
    private void initializeAudioTrack() {
        try {
            // Release any existing audio track
            if (audioTrack != null) {
                audioTrack.release();
            }

            // Create a new audio track
            int sampleRate = 24000; // Default for OpenAI audio
            int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

            int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            if (bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                Log.e(TAG, "❌ Invalid audio track parameters");
                return;
            }

            // Double the buffer size for safety
            bufferSize *= 2;

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            AudioFormat format = new AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(audioFormat)
                    .setChannelMask(channelConfig)
                    .build();

            audioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build();

            audioTrackState = audioTrack.getState();

            if (audioTrackState != AudioTrack.STATE_INITIALIZED) {
                Log.e(TAG, "❌ AudioTrack initialization failed");
            } else {
                Log.d(TAG, "✅ AudioTrack initialized successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing AudioTrack", e);
            audioTrack = null;
            audioTrackState = AudioTrack.STATE_UNINITIALIZED;
        }
    }

    // Handler methods for WebSocket events
    private void handleTextDelta(String delta) {
        // Append to current response text
        currentResponseText.append(delta);

        // Notify about partial text
        if (listener != null) {
            listener.onPartialResponse(currentResponseText.toString());
        }

        // Update textview if available
        if (tv != null) {
            tv.setText(currentResponseText.toString());
        }
    }

    private void handleTextDone() {
        // Notify about complete text
        if (listener != null) {
            listener.onTextResponse(currentResponseText.toString());
        }
    }

    private void handleAudioDone() {
        // Audio streaming is complete
        Log.d(TAG, "Audio streaming completed");
    }

    private void handleResponseDone() {
        // Complete response received
        Log.d(TAG, "Response completed");
        changeState(State.LISTENING);
    }

    private void handleStreamDone() {
        Log.d(TAG, "Stream completed");
    }

    // Reconnection logic
    private void attemptReconnect() {
        mainHandler.postDelayed(() -> {
            if (!isConnected.get()) {
                Log.d(TAG, "Attempting to reconnect...");
                connectWebSocket();
            }
        }, 5000); // Wait 5 seconds before trying to reconnect
    }

    private void tearDown() {
        stopMicrophoneCapture();

        if (webSocket != null) {
            webSocket.close(1000, "User closed");
            webSocket = null;
        }

        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }

        isConnected.set(false);
    }

    /**
     * Dummy streaming: emits partials then a final answer.
     */
    private void mockStreamingResponse(String full) {
        try {
            String[] parts = full.split("\\s+");
            StringBuilder partial = new StringBuilder();
            for (String p : parts) {
                partial.append(p).append(' ');
                String now = partial.toString().trim();
                mainHandler.post(() -> {
                    if (listener != null) listener.onPartialResponse(now);
                    if (tv != null) tv.setText(now);
                });
                Thread.sleep(90);        // stream speed
            }
            mainHandler.post(() -> {
                if (listener != null) listener.onTextResponse(full);
                changeState(State.LISTENING);
            });
        } catch (InterruptedException ignored) { /* shutdown */ }
    }

    public enum State {IDLE, CONNECTING, LISTENING, PROCESSING, SPEAKING}

    public interface ConversationListener {
        void onStateChanged(State newState);

        void onConnected();

        void onTextResponse(@NonNull String text);      // final answer

        void onPartialResponse(@NonNull String partial); // streaming partials

        void onError(@NonNull String message);

        default void onSpeechCompleted() {
        }             // for TTS, optional
    }
}