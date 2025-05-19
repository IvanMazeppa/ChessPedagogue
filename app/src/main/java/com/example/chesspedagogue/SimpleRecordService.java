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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    private static final boolean DEBUG_MODE = true;
    private static final long SILENCE_THRESHOLD_MS = 2000; // 2 seconds of silence stops recording
    // Service binding
    private final IBinder binder = new LocalBinder();
    // Recording state
    private AudioRecord recorder;
    private boolean isRecording = false;
    private int bufferSize;
    private File outputFile;
    // Managing conversation
    private String currentThreadId = null;
    private EnhancedConversationManager conversationManager;
    private TextToSpeechManager textToSpeechManager;
    private OpenAIService openAIService;
    private String apiKey;
    // UI references
    private TextView responseTextView;
    private View loadingIndicator;
    private TextView coachThinkingText;
    private ServiceCallback callback;
    // Auto-stop recording after a period of silence
    private ScheduledExecutorService silenceDetector;
    private long lastSoundTimestamp = 0;
    private boolean isFollowUpQuestion = false;

    private ChessMasterAgentManager agentManager;
    private String currentAssistantId;

    public boolean isCurrentlySpeaking() {
        return textToSpeechManager != null && textToSpeechManager.isSpeaking();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SimpleRecordService created");

        // Initialize OpenAI service
        openAIService = OpenAIService.getInstance();
        apiKey = getApiKeyFromPreferences();

        // Initialize the agent manager
        agentManager = new ChessMasterAgentManager(OpenAIService.getInstance(), this);

        Log.d(TAG, "🚀🚀🚀 ATTEMPTING TO CREATE CHESS MASTER AGENT 🚀🚀🚀");
        Log.d(TAG, "💫💫💫 ChessMasterAgentManager initialized 💫💫💫");

        // THIS IS THE IMPORTANT CHANGE - Move network calls to a background thread
        new Thread(() -> {
            try {
                Log.d(TAG, "🧙‍♂️🧙‍♂️🧙‍♂️ INITIALIZING BOTVINNIK ASSISTANT 🧙‍♂️🧙‍♂️🧙‍♂️");
                agentManager.getBotvinnikAssistantIdFullyAsync(new ChessMasterAgentManager.Callback<String>() {
                    @Override
                    public void onSuccess(String assistantId) {
                        currentAssistantId = assistantId;
                        Log.d(TAG, "📝📝📝 Botvinnik Assistant ID: " + currentAssistantId);

                        // Now that we have the assistant ID, create the thread
                        agentManager.createConversationThreadAsync(new ChessMasterAgentManager.Callback<String>() {
                            @Override
                            public void onSuccess(String threadId) {
                                currentThreadId = threadId;
                                Log.d(TAG, "📝📝📝 Thread ID result: " + currentThreadId);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Error creating thread: " + errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error getting Botvinnik assistant ID: " + errorMessage);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error initializing assistant: " + e.getMessage(), e);
            }
        }).start();

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

    public void setIsFollowUpQuestion(boolean isFollowUp) {
        this.isFollowUpQuestion = isFollowUp;
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
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
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

                // Get transcription
                String transcribedText = getTranscriptionFromFile(wavFile, apiKey);
                Log.d(TAG, "Transcribed text: " + transcribedText);

                // -------------------------------
                // CHESS CONTEXT INTEGRATION
                // -------------------------------

                // Get current game state with null check
                GameStateInfo gameState = GameStateRepository.getCurrentState();

                // Create an enhanced prompt with ALL chess details
                StringBuilder enhancedPrompt = new StringBuilder();

                // 1. Add detailed chess position context first
                // Add position info in a more compact format
                enhancedPrompt.append("CHESS POSITION: ");

                // Store FEN position for Assistants API
                String currentFenPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Default

                if (gameState != null) {
                    // Include only essential information
                    currentFenPosition = gameState.getCurrentFen();
                    enhancedPrompt.append("FEN=").append(currentFenPosition);
                    enhancedPrompt.append(", Color=").append(gameState.getPlayerColor());

                    // Only include the last few moves
                    List<String> moves = gameState.getMoveHistory();
                    if (moves != null && !moves.isEmpty()) {
                        int startIndex = Math.max(0, moves.size() - 3);
                        enhancedPrompt.append(", LastMoves=");
                        for (int i = startIndex; i < moves.size(); i++) {
                            enhancedPrompt.append(moves.get(i));
                            if (i < moves.size() - 1) enhancedPrompt.append(",");
                        }
                    }

                    // Add critical game state in compact form
                    ChessBoardView boardView = getChessBoardView();
                    if (boardView != null) {
                        if (boardView.isCheck()) enhancedPrompt.append(", InCheck=true");
                        enhancedPrompt.append(", Phase=").append(determineGamePhase(boardView));
                    }
                } else {
                    enhancedPrompt.append("No active game");
                }

                // And right BEFORE you add the user's question, add this:
                if (isFollowUpQuestion) {
                    enhancedPrompt.append("NOTE: This is a follow-up question to the previous advice.\n\n");
                    isFollowUpQuestion = false; // Reset for next time
                }

                enhancedPrompt.append("\n\nQUESTION: ").append(transcribedText);

                // Get the selected chess master
                String selectedMaster = getSelectedChessMaster();

                // Check if we should use Assistants API (for Botvinnik)
                if ("botvinnik".equals(selectedMaster)) {
                    // *** NEW ASSISTANTS API PATH FOR BOTVINNIK ***
                    Log.d(TAG, "Using Assistants API for Botvinnik");

                    // Get the ChessMasterAgentManager
                    ChessMasterAgentManager agentManager = new ChessMasterAgentManager(
                            OpenAIService.getInstance(), SimpleRecordService.this);

                    // Get Botvinnik's assistant ID
                    String assistantId = agentManager.getBotvinnikAssistantId();

                    if (assistantId == null) {
                        Log.e(TAG, "Failed to get Botvinnik Assistant ID");
                        return "I'm having trouble connecting to my chess memory. Please try again.";
                    }

                    // Create thread if needed
                    if (currentThreadId == null) {
                        currentThreadId = agentManager.createConversationThread();
                        Log.d(TAG, "Created new thread: " + currentThreadId);
                    }

                    if (currentThreadId == null) {
                        Log.e(TAG, "Failed to create conversation thread");
                        return "I'm having trouble starting our conversation. Please try again.";
                    }

                    // Send message with position context
                    String runId = agentManager.sendMessageWithPosition(
                            currentThreadId, assistantId, transcribedText, currentFenPosition);

                    if (runId == null) {
                        Log.e(TAG, "Failed to create run");
                        return "I'm having trouble analyzing your question. Please try again.";
                    }

                    Log.d(TAG, "Created run: " + runId);

                    // Keep the original line that gets the response
                    String response = agentManager.getChessMasterResponse(currentThreadId, runId);

// And add your async version to improve future responses
                    agentManager.getChessMasterResponseAsync(currentThreadId, runId, new ChessMasterAgentManager.Callback<String>() {
                        @Override
                        public void onSuccess(String asyncResponse) {
                            // Maybe log to compare with the synchronous response
                            Log.d(TAG, "Async response matches sync? " + asyncResponse.equals(response));
                            // You could do additional processing here if needed
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Async approach had error: " + errorMessage);
                        }
                    });
                    // Add response to conversation history
                    conversationManager.addMessage("assistant", response);

                    // Save the conversation
                    conversationManager.saveCurrentConversation();

                    // Clean up temp file
                    wavFile.delete();

                    return response;
                } else {
                    // *** ORIGINAL FINE-TUNED MODEL PATH FOR OTHER MASTERS ***
                    String coachName = selectedMaster.equals("kramnik") ? "Kramnik" : "Tal";
                    String coachStyle = selectedMaster.equals("kramnik")
                            ? "emphasizing positional understanding, prophylaxis, and long-term planning"
                            : "emphasizing tactical vision, creative sacrifices, and dynamic attacking play";

                    String systemPrompt = "You are Coach " + coachName + ", a chess grandmaster " + coachStyle + ". " +
                            "Be extremely concise and focused - limit to 2-3 sentences maximum. " +
                            "Don't repeat information like FEN or move lists that I already know. " +
                            "Get straight to the point with the best move or plan, using clear chess notation. " +
                            "Speak naturally as if we're in the middle of a game with time pressure.";

                    // Log the complete prompt for debugging
                    Log.d(TAG, "SYSTEM PROMPT: " + systemPrompt);
                    Log.d(TAG, "ENHANCED PROMPT: " + enhancedPrompt);

                    // Make the API call with BOTH the system prompt and enhanced prompt
                    String response = openAIService.getChatCompletion(systemPrompt, enhancedPrompt.toString());

                    // Add response to conversation history
                    conversationManager.addMessage("assistant", response);

                    // Save the conversation
                    conversationManager.saveCurrentConversation();

                    // Clean up temp file
                    wavFile.delete();

                    return response;
                }
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
     * Resets the current conversation and starts a new one
     */
    // In SimpleRecordService.java - Make sure resetConversation() is thorough:
    public void resetConversation() {
        // Stop any ongoing speech
        if (textToSpeechManager != null) {
            textToSpeechManager.interrupt();
        }

        // Clear ALL conversation history
        conversationManager.startNewConversation();


        String sessionId = "new-session-" + System.currentTimeMillis();
        Log.d(TAG, "✨ Conversation fully reset - new session created: " + sessionId);


        // Add an initial system message to set the coach's personality
        // Get the selected chess master
        String selectedMaster = getSelectedChessMaster();
        String coachName = selectedMaster.equals("kramnik") ? "Kramnik" : "Tal";
        String coachSpecialty = selectedMaster.equals("kramnik")
                ? "Vladimir Kramnik's strategic approach, the Berlin Defense, and positional masterpieces"
                : "Mikhail Tal's brilliant tactical games, sacrificial attacks, and creative combinations";

        conversationManager.addMessage("system",
                "You are Coach " + coachName + ", a chess grandmaster with deep knowledge of chess history, " +
                        "especially about " + coachSpecialty + ". " +
                        "You can discuss chess positions, history, players, and strategies.");
    }

    /**
     * Interrupts any ongoing speech from the coach
     */
    public void interruptSpeech() {
        if (textToSpeechManager != null) {
            textToSpeechManager.interrupt();
            Log.d(TAG, "Speech interrupted by user tap");
        }
    }

    /**
     * Simple implementation of audio transcription
     * For debugging/development, you can return a fixed string
     */
    private String getTranscriptionFromFile(File audioFile, String apiKey) {
        try {
            Log.d(TAG, "About to transcribe with Whisper API: " + audioFile.getAbsolutePath());

            // Read the WAV file into a byte array
            byte[] audioData = readFileToBytes(audioFile);

            // Use OpenAI's Whisper API through your service
            OpenAIWhisperService whisperService = new OpenAIWhisperService(apiKey);
            String transcribedText = whisperService.transcribeAudio(audioData);

            Log.d(TAG, "Whisper API returned: " + transcribedText);
            return transcribedText;
        } catch (Exception e) {
            Log.e(TAG, "Error using Whisper API", e);
            return "Error transcribing speech: " + e.getMessage();
        }
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
        writeShort(out, (short) 1); // audio format 1 = PCM
        writeShort(out, (short) channels);
        writeInt(out, sampleRate);
        writeInt(out, byteRate);
        writeShort(out, blockAlign);
        writeShort(out, (short) bitsPerSample);

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

    private String determineQuestionType(String question) {
        question = question.toLowerCase(); // Make case-insensitive for better matching

        // Chess position/move questions
        if (question.contains("best move") || question.contains("position") ||
                question.contains("play") || question.contains("should i") ||
                question.contains("what move") || question.contains("next move")) {
            return "CHESS_POSITION";
        }

        // Expanded chess history/people questions - much more inclusive
        if (question.contains("tal") || question.contains("kramnik") || question.contains("botvinnik") ||
                question.contains("kasparov") || question.contains("fischer") ||
                question.contains("capablanca") || question.contains("karpov") ||
                question.contains("anand") || question.contains("carlsen") ||
                question.contains("history") || question.contains("story") ||
                question.contains("stories") || question.contains("famous") ||
                question.contains("championship") || question.contains("match") ||
                question.contains("tournament") || question.contains("grandmaster") ||
                question.contains("who was") || question.contains("tell me about")) {
            return "CHESS_HISTORY";
        }

        // Default
        return "GENERAL";
    }

    // Add to SimpleRecordService class
    private boolean shouldUseAssistantsApi() {
        String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
        return "botvinnik".equals(currentMaster);
    }

    // In SimpleRecordService.java
    public void refreshVoiceSettings() {
        // Get current master
        String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Update the TTS service
        if (textToSpeechManager != null) {
            // Get the voice style and use from shared preferences
            SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
            String voiceStyle = prefs.getString("voice_style", "auto");
            boolean usePersonality = prefs.getBoolean("use_master_personality", true);

            // Update TTS settings
            ChessCoachManager.getInstance(this).updateTTSSettings(voiceStyle, usePersonality);
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
    /**
     * Process transcribed speech with AI response.
     * Note: Currently unused but available for future speech processing.
     */
    private void processTranscription(String transcribedText) {
        // Log the incoming transcription for debugging
        Log.d(TAG, "Processing transcription: " + transcribedText);

        // Get the selected chess master
        String selectedMaster = getSelectedChessMaster();
        String coachName = selectedMaster.equals("kramnik") ? "Kramnik" : "Tal";
        String systemPrompt = "You are Coach " + coachName + ", a chess grandmaster providing guidance.";

        // Determine question type and set appropriate prompt
        String questionType = determineQuestionType(transcribedText);
        if (questionType.equals("CHESS_HISTORY")) {
            systemPrompt = "You are Coach Tal, a chess grandmaster with encyclopedic knowledge " +
                    "of chess history and famous players. Share engaging, colorful stories about " +
                    "any chess player, match, tournament or historical moment mentioned. " +
                    "Be particularly vivid when describing famous games and the personalities " +
                    "of the players. If specific players like Botvinnik, Fischer, Kasparov, etc. " +
                    "are mentioned, focus your response on them specifically.";
        } else if (questionType.equals("CHESS_POSITION")) {
            systemPrompt = "You are Coach Tal, analyzing the current chess position...";
        }

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

                    // Get transcription
                    String transcribedSpeech = getTranscriptionFromFile(wavFile, apiKey);
                    Log.d(TAG, "Transcribed text: " + transcribedSpeech);

                    // Get current FEN position
                    String fenPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Default
                    ChessBoardView boardView = getChessBoardView();
                    if (boardView != null) {
                        fenPosition = boardView.getCurrentFEN();
                    }

                    // ✨ HERE'S THE MAGIC - USE THE ASSISTANT INSTEAD OF DIRECT CHAT ✨
                    // Send message to the Assistant with chess position context
                    JSONObject messageRequest = new JSONObject();
                    messageRequest.put("role", "user");
                    messageRequest.put("content", "CHESS POSITION: " + fenPosition + "\n\nQUESTION: " + transcribedSpeech);

                    // Create message in the thread
                    String messageResponse = agentManager.createMessage(currentThreadId, messageRequest.toString());
                    JSONObject messageJson = new JSONObject(messageResponse);
                    String messageId = messageJson.getString("id");

                    // Create run request
                    JSONObject runRequest = new JSONObject();
                    runRequest.put("assistant_id", currentAssistantId);

                    // Start the run

                    // ✨ Use the sendMessageWithPosition method which handles both creating the message and running
                    agentManager.sendMessageWithPosition(currentThreadId, currentAssistantId,
                            transcribedSpeech, fenPosition);

                    String response = agentManager.getChessMasterResponse(currentThreadId, "latest");
                    agentManager.getChessMasterResponseAsync(currentThreadId, "latest", new ChessMasterAgentManager.Callback<String>() {
                        @Override
                        public void onSuccess(String asyncResponse) {
                            // Maybe log to compare with the synchronous response
                            Log.d(TAG, "Async response matches sync? " + asyncResponse.equals(response));
                            // You could do additional processing here if needed
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Async approach had error: " + errorMessage);
                        }
                    });


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
            }).thenAccept(coachResponse -> {
                // Rest of your code remains the same...
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
     * Gets the selected chess coach profile (Tal or Kramnik)
     */
    private String getSelectedChessMaster() {
        // Use ChessAppPrefs to match the key used in the settings
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        return prefs.getString("selected_master", "tal"); // Default to Tal if not set
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

    /**
     * Binder for client communication
     */
    public class LocalBinder extends Binder {
        public SimpleRecordService getService() {
            return SimpleRecordService.this;
        }
    }
}