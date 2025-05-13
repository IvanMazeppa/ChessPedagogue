package com.example.chesspedagogue;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleRecordService extends Service {
    private static final String TAG = "SimpleRecordService";
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // Recording state
    private AudioRecord recorder;
    private boolean isRecording = false;
    private int bufferSize;
    private File outputFile;

    // Managing conversation
    private EnhancedConversationManager conversationManager;
    private TextToSpeechManager textToSpeechManager;
    private OpenAIService openAIService;
    private String apiKey;

    // UI references
    private TextView responseTextView;
    private View loadingIndicator;
    private TextView coachThinkingText;


    private static final boolean DEBUG_MODE = true;

    // Service binding
    private final IBinder binder = new LocalBinder();
    private ServiceCallback callback;

    // Auto-stop recording after a period of silence
    private ScheduledExecutorService silenceDetector;
    private long lastSoundTimestamp = 0;
    private static final long SILENCE_THRESHOLD_MS = 2000; // 2 seconds of silence stops recording

    /**
     * Binder for client communication
     */
    public class LocalBinder extends Binder {
        public SimpleRecordService getService() {
            return SimpleRecordService.this;
        }
    }

    /**
     * Callback interface for service events
     */
    public interface ServiceCallback {
        void onRecordingStarted();
        void onRecordingStopped();
        void onProcessingStateChanged(boolean isProcessing);
        void onResponseReceived(String response);
        void onResponseCompleted(String response);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SimpleRecordService created");

        // Initialize OpenAI service
        openAIService = OpenAIService.getInstance();
        apiKey = getApiKeyFromPreferences();
        // Add this log to check if the API key is retrieved correctly
        Log.d(TAG, "API Key retrieved, length: " + (apiKey != null ? apiKey.length() : 0));


        openAIService.setApiKey(apiKey);

        // Initialize text-to-speech
        textToSpeechManager = new TextToSpeechManager(this);

        // Initialize conversation manager
        conversationManager = EnhancedConversationManager.getInstance(this);

        // Check if we should resume an existing conversation or start new
        if (shouldResumeConversation()) {
            // Get the most recent session ID
            List<String> sessions = conversationManager.getAvailableSessions();
            if (!sessions.isEmpty()) {
                conversationManager.resumeConversation(sessions.get(sessions.size() - 1));
                Log.d(TAG, "Resuming previous conversation");
            }
        } else {
            conversationManager.startNewConversation();
            Log.d(TAG, "Starting new conversation");
        }

        // Initialize recording buffer
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2; // Default if error
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started with command");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        if (textToSpeechManager != null) {
            textToSpeechManager.shutdown();
        }
        Log.d(TAG, "SimpleRecordService destroyed");
        super.onDestroy();
    }

    /**
     * Start recording audio
     */
    public void startRecording() {
        if (isRecording) {
            Log.d(TAG, "Already recording, ignoring start request");
            return;
        }

        Log.d(TAG, "Starting recording");

        try {
            // Create temporary file for recording
            File cacheDir = getCacheDir();
            outputFile = File.createTempFile("recording_", ".pcm", cacheDir);

            // Initialize recorder
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord not initialized");
                return;
            }

            // Start recording
            isRecording = true;
            recorder.startRecording();

            // Start silence detector
            startSilenceDetector();

            // Notify callback
            if (callback != null) {
                callback.onRecordingStarted();
            }

            // Start recording thread
            Executors.newSingleThreadExecutor().execute(this::recordingLoop);

        } catch (Exception e) {
            Log.e(TAG, "Error starting recording", e);
            cleanup();
        }
    }

    /**
     * Main recording loop
     */
    private void recordingLoop() {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[bufferSize];

            while (isRecording) {
                int read = recorder.read(buffer, 0, bufferSize);

                if (read > 0) {
                    // Check for sound vs silence
                    boolean isSilence = isSilence(buffer, read);
                    if (!isSilence) {
                        lastSoundTimestamp = System.currentTimeMillis();
                    }

                    // Write to file
                    outputStream.write(buffer, 0, read);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Error writing audio data", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing output stream", e);
            }
        }
    }

    /**
     * Stop recording and process the audio
     */
    public void stopRecording() {
        if (!isRecording) {
            return;
        }

        Log.d(TAG, "Stopping recording");
        isRecording = false;

        if (silenceDetector != null) {
            silenceDetector.shutdown();
            silenceDetector = null;
        }

        if (recorder != null) {
            try {
                if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    recorder.stop();
                }
                recorder.release();
                recorder = null;
            } catch (Exception e) {
                Log.e(TAG, "Error releasing recorder", e);
            }
        }

        // Notify callback
        if (callback != null) {
            callback.onRecordingStopped();
        }

        // Process the recording if file exists and has content
        if (outputFile != null && outputFile.exists() && outputFile.length() > 0) {
            processRecording();
        } else {
            Log.d(TAG, "No recording to process");
        }
    }

    /**
     * Process the recorded audio file with proper permission handling and chess context
     */
    private void processRecording() {
        Log.d(TAG, "Processing recording");

        // Show processing state
        if (callback != null) {
            callback.onProcessingStateChanged(true);
        }

        // IMPORTANT: Check audio permissions explicitly
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Recording permission not granted!");
            if (callback != null) {
                callback.onProcessingStateChanged(false);
                callback.onResponseReceived("I need permission to hear your voice. Please grant microphone access in settings.");
            }
            return;
        }

        // Transcribe audio on background thread
        CompletableFuture.supplyAsync(() -> {
            try {
                // Get API key
                String apiKey = ApiKeyConfig.getApiKey(this);
                if (apiKey == null || apiKey.isEmpty()) {
                    return "Error: API key not configured.";
                }

                // PCM to WAV conversion
                byte[] pcmData = readFileToBytes(outputFile);
                byte[] wavData = convertPcmToWav(pcmData, SAMPLE_RATE, 1, 16);

                File wavFile = File.createTempFile("recording_", ".wav", getCacheDir());
                try (FileOutputStream fos = new FileOutputStream(wavFile)) {
                    fos.write(wavData);
                }

                // Get transcription (replace with your actual transcription code)
                String transcribedText = getTranscriptionFromFile(wavFile, apiKey);
                Log.d(TAG, "Transcribed text: " + transcribedText);

                // -------------------------------
                // CHESS CONTEXT INTEGRATION - FIXED
                // -------------------------------

                // Get current game state with null check
                GameStateInfo gameState = GameStateRepository.getCurrentState();

                // Create an enhanced prompt with ALL chess details
                StringBuilder enhancedPrompt = new StringBuilder();

                // 1. Add detailed chess position context first
                enhancedPrompt.append("CURRENT CHESS POSITION:\n");
                if (gameState != null) {
                    enhancedPrompt.append("- FEN: ").append(gameState.getCurrentFen()).append("\n");
                    enhancedPrompt.append("- Player is: ").append(gameState.getPlayerColor()).append("\n");

                    // Check if we have move history
                    List<String> moves = gameState.getMoveHistory();
                    if (moves != null && !moves.isEmpty()) {
                        enhancedPrompt.append("- Move History: ").append(String.join(", ", moves)).append("\n");

                        // Attempt to identify opening
                        String opening = determineOpening(moves);
                        if (opening != null) {
                            enhancedPrompt.append("- Opening: ").append(opening).append("\n");
                        }
                    }

                    // Check for special conditions like check, material advantage, etc.
                    ChessBoardView boardView = getChessBoardView();
                    if (boardView != null) {
                        if (boardView.isCheck()) {
                            enhancedPrompt.append("- Special: ").append(boardView.isWhiteTurn() ? "White" : "Black")
                                    .append(" is in check\n");
                        }

                        String gamePhase = determineGamePhase(boardView);
                        enhancedPrompt.append("- Game Phase: ").append(gamePhase).append("\n");
                    }
                } else {
                    // Fallback for debugging
                    enhancedPrompt.append("No active game found. Analysis may be limited.\n");
                }
                enhancedPrompt.append("\n");

                // 2. Add the user's transcribed question
                enhancedPrompt.append("USER QUESTION: ").append(transcribedText);

                // 3. Create a chess-focused system prompt that emphasizes using the position
                String systemPrompt = "You are Coach Tal, a supportive chess grandmaster. " +
                        "IMPORTANT: The chess position details above are critical - analyze this specific " +
                        "position thoroughly. Refer to concrete pieces, squares, and variations in your response. " +
                        "Be encouraging and helpful, suggesting the best moves and explaining the strategic ideas. " +
                        "If the position is from a known opening, share insights about typical plans.";

                // Log the complete prompt for debugging
                Log.d(TAG, "SYSTEM PROMPT: " + systemPrompt);
                Log.d(TAG, "ENHANCED PROMPT: " + enhancedPrompt.toString());

                // 4. Make the API call with BOTH the system prompt and enhanced prompt
                String response = openAIService.getChatCompletion(systemPrompt, enhancedPrompt.toString());

                // Add response to conversation history
                conversationManager.addMessage("assistant", response);

                // Save the conversation
                conversationManager.saveCurrentConversation();

                // Clean up temp file
                wavFile.delete();

                return response;
            } catch (Exception e) {
                Log.e(TAG, "Error in speech-to-speech process", e);
                return "I'm sorry, I had trouble analyzing your question. Could you try again?";
            }
        }).thenAccept(response -> {
            // Process the response on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                // Update UI
                updateUIForProcessing(false);
                updateResponseUI(response);

                // Speak the response
                textToSpeechManager.speak(response, new TextToSpeechManager.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        notifyResponseCompleted(response);
                    }
                });
            });
        }).exceptionally(e -> {
            Log.e(TAG, "Error in async processing", e);
            new Handler(Looper.getMainLooper()).post(() -> {
                updateUIForProcessing(false);
                String errorMsg = "I'm having trouble connecting to my chess brain. Let's try again in a moment.";
                textToSpeechManager.speak(errorMsg);
                updateResponseUI(errorMsg);
            });
            return null;
        });
    }

    /**
     * Placeholder for determining opening from move history
     */
    private String determineOpening(List<String> moves) {
        // This is a very simple implementation - you can expand this
        // to recognize more openings based on move patterns
        if (moves.size() >= 4) {
            String firstFourMoves = String.join(" ", moves.subList(0, Math.min(4, moves.size())));
            if (firstFourMoves.startsWith("1. d4 Nf6 2. c4 e6")) {
                return "Nimzo-Indian Defense";
            } else if (firstFourMoves.startsWith("1. e4 e5 2. Nf3")) {
                return "Open Game";
            } else if (firstFourMoves.startsWith("1. e4 c5")) {
                return "Sicilian Defense";
            }
        }
        return null;
    }

    /**
     * Simple implementation of audio transcription
     * For debugging/development, you can return a fixed string
     */
    private String getTranscriptionFromFile(File audioFile, String apiKey) {
        // For debugging/development
        boolean useFixedText = false;  // Set to true for testing without actual transcription

        if (useFixedText) {
            Log.d(TAG, "Using fixed test transcription");
            return "What's the best continuation in this position?";
        }

        // TODO: Implement actual transcription using OpenAI Whisper API or your preferred method
        // This is a placeholder that should be replaced with your actual implementation
        Log.d(TAG, "Transcribing audio file: " + audioFile.getAbsolutePath());

        // Return placeholder text until you implement actual transcription
        return "What's the best move in this position?";
    }





    // Add this helper method to convert PCM to WAV
    private byte[] convertPcmToWav(byte[] pcmData, int sampleRate, int channels, int bitsPerSample) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WAV header fields
        int dataLength = pcmData.length;
        int riffChunkSize = 36 + dataLength;
        int byteRate = sampleRate * channels * (bitsPerSample / 8);
        short blockAlign = (short) (channels * (bitsPerSample / 8));

        // Write RIFF header
        writeBytes(out, "RIFF".getBytes());
        writeInt(out, riffChunkSize);
        writeBytes(out, "WAVE".getBytes());

        // fmt subchunk
        writeBytes(out, "fmt ".getBytes());
        writeInt(out, 16); // PCM format chunk size
        writeShort(out, (short)1); // audio format 1 = PCM
        writeShort(out, (short)channels);
        writeInt(out, sampleRate);
        writeInt(out, byteRate);
        writeShort(out, blockAlign);
        writeShort(out, (short)bitsPerSample);

        // data subchunk
        writeBytes(out, "data".getBytes());
        writeInt(out, dataLength);
        writeBytes(out, pcmData);

        return out.toByteArray();
    }

    // Helper methods for WAV header writing
    private void writeInt(ByteArrayOutputStream out, int value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    private void writeShort(ByteArrayOutputStream out, short value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    private void writeBytes(ByteArrayOutputStream out, byte[] data) {
        try {
            out.write(data);
        } catch (IOException e) {
            // This shouldn't happen with ByteArrayOutputStream
            Log.e(TAG, "Error writing bytes", e);
        }
    }

    // Helper to read file to byte array
    private byte[] readFileToBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }
        return bytes;
    }

    /**
     * Processes the user's speech transcription, enhances it with chess context,
     * and generates a response from Coach Tal.
     *
     * @param transcribedText The user's speech converted to text
     */
    private void processTranscription(String transcribedText) {
        // Log the incoming transcription for debugging
        Log.d(TAG, "Processing transcription: " + transcribedText);

        try {
            // 1. Create a rich context package that includes game state
            String enhancedPrompt = createEnhancedPromptWithContext(transcribedText);

            // 2. Add the user's message to conversation history
            conversationManager.addMessage("user", enhancedPrompt);

            // 3. Show a visual indicator that Coach Tal is thinking
            updateUIForProcessing(true);

            // 4. Get response from OpenAI with full conversation history
            // 4. Get response from OpenAI with full conversation history
            CompletableFuture.supplyAsync(() -> {
                        try {
                            // Split the conversation into system message and user content
                            String systemMessage = "You are Coach Tal, a chess grandmaster providing guidance. " +
                                    "IMPORTANT: Use the provided chess position (FEN), move history, and game phase " +
                                    "details to analyze the specific position. Never ask for the position if it's " +
                                    "already provided in the context. Always reference specific moves or positions " +
                                    "in your responses.";

                            // Build the user content from conversation history
                            StringBuilder userContent = new StringBuilder();

                            // Add conversation history
                            List<ConversationManager.Message> history = conversationManager.getConversationHistory();
                            for (ConversationManager.Message message : history) {
                                userContent.append(message.getRole())
                                        .append(": ")
                                        .append(message.getContent())
                                        .append("\n\n");
                            }

                            // Use your existing OpenAIService method with both required parameters
                            return openAIService.getChatCompletion(systemMessage, userContent.toString());
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting chat completion", e);
                            return "I'm sorry, I couldn't process that request. Can you try again?";
                        }
                    }).thenAccept(coachResponse -> {
                // 5. Handle the coach's response on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Add assistant response to history
                    conversationManager.addMessage("assistant", coachResponse);

                    // Save conversation after every exchange
                    conversationManager.saveCurrentConversation();

                    // Convert text to speech
                    textToSpeechManager.speak(coachResponse, new TextToSpeechManager.OnSpeechCompletedListener() {
                        @Override
                        public void onSpeechCompleted() {
                            // When speech is done, update UI and notify listeners
                            updateUIForProcessing(false);
                            notifyResponseCompleted(coachResponse);
                        }
                    });

                    // Also update any visual representation of the response
                    updateResponseUI(coachResponse);
                });
            }).exceptionally(e -> {
                Log.e(TAG, "Error in async processing", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    updateUIForProcessing(false);
                    String errorMsg = "I'm having trouble connecting to my chess brain. Let's try again in a moment.";
                    textToSpeechManager.speak(errorMsg);
                    updateResponseUI(errorMsg);
                });
                return null;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in processTranscription", e);
            updateUIForProcessing(false);
        }

        if (transcribedText.startsWith("Error")) {
            Log.e(TAG, "Transcription error: " + transcribedText);
            updateUIForProcessing(false);
            return;
        }
    }

    /**
     * Creates an enhanced prompt that includes the chess context
     */
    private String createEnhancedPromptWithContext(String userQuestion) {
        StringBuilder contextBuilder = new StringBuilder();

        // Add the user's original question
        contextBuilder.append("User question: ").append(userQuestion).append("\n\n");

        try {
            // Add current board state if available
            ChessBoardView boardView = getChessBoardView();
            if (boardView != null) {
                // Get FEN notation of current position
                String fenPosition = boardView.getCurrentFEN();
                contextBuilder.append("Current board position (FEN): ").append(fenPosition).append("\n");

                // Add who's turn it is
                boolean isWhiteTurn = boardView.isWhiteTurn();
                contextBuilder.append("Current turn: ").append(isWhiteTurn ? "White" : "Black").append("\n");

                // Add player's color
                String playerColor = getPlayerColor();
                contextBuilder.append("Player is playing as: ").append(playerColor).append("\n");

                // Determine game phase (opening, middlegame, endgame)
                String gamePhase = determineGamePhase(boardView);
                contextBuilder.append("Game phase: ").append(gamePhase).append("\n\n");

                // Add recent moves if available
                List<String> moveHistory = getMoveHistory();
                if (moveHistory != null && !moveHistory.isEmpty()) {
                    contextBuilder.append("Recent moves:\n");

                    // Limit to last 10 moves to keep context concise
                    int startIdx = Math.max(0, moveHistory.size() - 10);
                    int moveNumber = (startIdx / 2) + 1;

                    for (int i = startIdx; i < moveHistory.size(); i += 2) {
                        contextBuilder.append(moveNumber).append(". ").append(moveHistory.get(i));
                        if (i + 1 < moveHistory.size()) {
                            contextBuilder.append(" ").append(moveHistory.get(i + 1));
                        }
                        contextBuilder.append("\n");
                        moveNumber++;
                    }
                }

                // Add any special game conditions
                if (boardView.isCheck()) {
                    contextBuilder.append("Special condition: ").append(isWhiteTurn ? "White" : "Black")
                            .append(" is in check.\n");
                }

                if (boardView.isCheckmate()) {
                    contextBuilder.append("Special condition: Checkmate. Game over.\n");
                }

                if (boardView.isStalemate()) {
                    contextBuilder.append("Special condition: Stalemate. Game drawn.\n");
                }
            }

            // Add conversation history length as context
            int messageCount = conversationManager.getConversationHistory().size();
            if (messageCount > 0) {
                contextBuilder.append("\nThis is message #").append(messageCount / 2 + 1)
                        .append(" in our conversation.\n");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating enhanced prompt", e);
            // If we can't get context, just use the original question
            return userQuestion;
        }

        return contextBuilder.toString();
    }

    /**
     * Determines the current phase of the chess game
     */
    private String determineGamePhase(ChessBoardView boardView) {
        // This is a simple heuristic - you can make it more sophisticated
        int pieceCount = boardView.getPieceCount();

        if (pieceCount > 28) {  // Most pieces still on board
            return "Opening";
        } else if (pieceCount > 10) {  // Some pieces captured
            return "Middlegame";
        } else {  // Few pieces remain
            return "Endgame";
        }
    }

    /**
     * Updates UI elements when processing starts/stops
     */
    private void updateUIForProcessing(boolean isProcessing) {
        if (callback != null) {
            callback.onProcessingStateChanged(isProcessing);
        }

        // You can also update UI elements directly if they're accessible
        try {
            // This assumes you have a way to access these UI elements
            if (loadingIndicator != null) {
                loadingIndicator.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
            }

            if (coachThinkingText != null) {
                coachThinkingText.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }

    /**
     * Updates any visual representation of Coach Tal's response
     */
    private void updateResponseUI(String response) {
        if (callback != null) {
            callback.onResponseReceived(response);
        }

        // If you have direct access to UI elements, update them here
        try {
            if (responseTextView != null) {
                responseTextView.setText(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating response UI", e);
        }
    }

    /**
     * Notify listeners that a response has completed
     */
    private void notifyResponseCompleted(String response) {
        if (callback != null) {
            callback.onResponseCompleted(response);
        }
    }

    /**
     * Start the silence detector to automatically stop recording after silence
     */
    private void startSilenceDetector() {
        lastSoundTimestamp = System.currentTimeMillis();
        silenceDetector = Executors.newSingleThreadScheduledExecutor();
        silenceDetector.scheduleAtFixedRate(() -> {
            if (isRecording) {
                long now = System.currentTimeMillis();
                if (now - lastSoundTimestamp > SILENCE_THRESHOLD_MS) {
                    Log.d(TAG, "Silence detected, stopping recording");
                    // Stop on main thread
                    new Handler(Looper.getMainLooper()).post(this::stopRecording);
                }
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Check if the audio buffer represents silence
     */
    private boolean isSilence(byte[] buffer, int size) {
        // Convert byte array to short array
        short[] shorts = new short[size / 2];
        ByteBuffer.wrap(buffer, 0, size).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        // Calculate RMS (root mean square)
        double sum = 0;
        for (short value : shorts) {
            sum += value * value;
        }
        double rms = Math.sqrt(sum / shorts.length);

        // Threshold for silence (adjust as needed)
        return rms < 200;
    }

    /**
     * Clean up resources
     */
    private void cleanup() {
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
            outputFile = null;
        }
    }

    /**
     * Helper method to get the ChessBoardView
     */
    private ChessBoardView getChessBoardView() {
        // This should be implemented to get access to your chess board
        // For example, through a singleton or by passing it to the service
        return ChessBoardManager.getInstance().getCurrentBoardView();
    }

    /**
     * Gets the player's color (white or black)
     */
    private String getPlayerColor() {
        // Get this from your game settings or preferences
        SharedPreferences prefs = getSharedPreferences("chess_prefs", MODE_PRIVATE);
        return prefs.getString("player_color", "White");
    }

    /**
     * Gets the move history from the current game
     */
    private List<String> getMoveHistory() {
        // This should access your move history storage
        return GameHistoryManager.getInstance().getCurrentGameMoves();
    }



    private String getApiKeyFromPreferences() {
        // You mentioned you have this working elsewhere in your app,
        // so let's make sure we're using the same method:
        String apiKey = ApiKeyConfig.getApiKey(this);

        // Double-check that it's not empty
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ API Key is empty or null! Check your ApiKeyConfig class.");
            // Fallback to preferences if ApiKeyConfig doesn't work in service context
            SharedPreferences prefs = getSharedPreferences("api_prefs", MODE_PRIVATE);
            apiKey = prefs.getString("openai_api_key", "");
            Log.d(TAG, "Fallback API key retrieved, length: " + apiKey.length());
        }

        return apiKey;
    }

    /**
     * Determine if we should resume a conversation from before
     */
    private boolean shouldResumeConversation() {
        // Get the last conversation time
        SharedPreferences prefs = getSharedPreferences("conversation_prefs", MODE_PRIVATE);
        long lastConversationTime = prefs.getLong("last_conversation_time", 0);

        // If less than 30 minutes have passed, resume the conversation
        long thirtyMinutesInMillis = 30 * 60 * 1000;
        boolean shouldResume = System.currentTimeMillis() - lastConversationTime < thirtyMinutesInMillis;

        // Update the last conversation time
        prefs.edit().putLong("last_conversation_time", System.currentTimeMillis()).apply();

        return shouldResume;
    }

    /**
     * Set the callback for service events
     */
    public void setCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    /**
     * Set UI elements for direct updates
     */
    public void setUIElements(TextView responseTextView, View loadingIndicator, TextView thinkingText) {
        this.responseTextView = responseTextView;
        this.loadingIndicator = loadingIndicator;
        this.coachThinkingText = thinkingText;
    }
}