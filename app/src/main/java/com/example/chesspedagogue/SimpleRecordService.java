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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimpleRecordService extends Service {
    private static final String TAG = "SimpleRecordService";
    private static final String groqApiKey = "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";
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final boolean DEBUG_MODE = true;
    private static final long SILENCE_THRESHOLD_MS = 1500; // Reduced for faster response

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
    private final ExecutorService executorService = Executors.newFixedThreadPool(8); // More threads
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // NEW: Pre-warming for ultra-fast response
    private UnifiedOpenAIService unifiedService;
    private OpenAITTSService ttsService;
    private volatile boolean servicesWarmed = false;

    // Add as class member in SimpleRecordService
    private static final OkHttpClient groqClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)  // Even faster!
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
            .build();

    public boolean isCurrentlySpeaking() {
        return textToSpeechManager != null && textToSpeechManager.isSpeaking();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SimpleRecordService created - starting optimizations");

        // Pre-warm all services for instant response
        preWarmServices();

        // Initialize OpenAI service
        openAIService = OpenAIService.getInstance();
        apiKey = getApiKeyFromPreferences();

        // Initialize the agent manager
        agentManager = new ChessMasterAgentManager(OpenAIService.getInstance(), this);

        // Initialize services in parallel
        CompletableFuture<Void> initFuture = CompletableFuture.runAsync(() -> {
            Log.d(TAG, "🚀 Initializing services in parallel");

            // Initialize unified service
            unifiedService = UnifiedOpenAIService.getInstance(this);
            unifiedService.setApiKey(apiKey);

            // Initialize TTS service
            ttsService = OpenAITTSService.getInstance(this);
            ttsService.setApiKey(apiKey);

            servicesWarmed = true;
        }, executorService);

        // In onCreate() of SimpleRecordService
        executorService.execute(() -> {
            try {
                // Pre-warm Groq connection
                Request warmupRequest = new Request.Builder()
                        .url("https://api.groq.com/openai/v1/models")
                        .header("Authorization", "Bearer " + groqApiKey)
                        .build();
                groqClient.newCall(warmupRequest).execute();
                Log.d(TAG, "✅ Groq connection pre-warmed!");
            } catch (Exception e) {
                Log.w(TAG, "Groq pre-warm failed: " + e.getMessage());
            }
        });

        // Initialize assistant in background
        new Thread(() -> {
            try {
                agentManager.getBotvinnikAssistantIdFullyAsync(new ChessMasterAgentManager.Callback<String>() {
                    @Override
                    public void onSuccess(String assistantId) {
                        currentAssistantId = assistantId;
                        Log.d(TAG, "✅ Assistant ready: " + currentAssistantId);

                        agentManager.createConversationThreadAsync(new ChessMasterAgentManager.Callback<String>() {
                            @Override
                            public void onSuccess(String threadId) {
                                currentThreadId = threadId;
                                Log.d(TAG, "✅ Thread ready: " + currentThreadId);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Error creating thread: " + errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error getting assistant ID: " + errorMessage);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error initializing assistant: " + e.getMessage(), e);
            }
        }).start();

        openAIService.setApiKey(apiKey);

        // Initialize text-to-speech
        textToSpeechManager = new TextToSpeechManager(this);

        // Initialize conversation manager
        conversationManager = EnhancedConversationManager.getInstance(this);

        // Check if we should resume an existing conversation or start new
        if (shouldResumeConversation()) {
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
            bufferSize = SAMPLE_RATE * 2;
        }
    }

    /**
     * Pre-warm services for instant response
     */
    private void preWarmServices() {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🔥 Pre-warming services for instant response");

                // Pre-initialize HTTP connections
                String testApiKey = getApiKeyFromPreferences();
                if (testApiKey != null && !testApiKey.isEmpty()) {
                    // This creates the HTTP client and connection pool
                    UnifiedOpenAIService.getInstance(this).setApiKey(testApiKey);
                    OpenAITTSService.getInstance(this).setApiKey(testApiKey);
                }

                Log.d(TAG, "✅ Services pre-warmed successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error pre-warming services", e);
            }
        });
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
        executorService.shutdown();
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
            executorService.execute(this::recordingLoop);

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
            processRecordingUltraFast();
        } else {
            Log.d(TAG, "No recording to process");
        }
    }

    /**
     * NEW: Ultra-fast parallel processing pipeline
     */
    private void processRecordingUltraFast() {
        Log.d(TAG, "⚡ Starting ULTRA-FAST parallel processing pipeline");

        if (callback != null) {
            callback.onProcessingStateChanged(true);
        }

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Recording permission not granted!");
            if (callback != null) {
                callback.onProcessingStateChanged(false);
                callback.onResponseReceived("I need permission to hear your voice. Please grant microphone access in settings.");
            }
            return;
        }

        // Start parallel processing
        executorService.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();

                // NEW: Use Groq for ultra-fast transcription!
                byte[] pcmData = readFileToBytes(outputFile);

                String transcribedText = transcribeWithGroq(pcmData);

                long transcriptionTime = System.currentTimeMillis() - startTime;
                Log.d(TAG, "⏱️ GROQ Transcription completed in " + transcriptionTime + "ms");

                // Build context in parallel
                String gameContext = buildEnhancedContext();

                // Process with streaming response
                processWithUltraFastStreaming(transcribedText, gameContext);

            } catch (Exception e) {
                Log.e(TAG, "Error in ultra-fast processing", e);
                mainHandler.post(() -> {
                    updateUIForProcessing(false);
                    String errorMsg = "I'm having trouble connecting to my chess brain. Let's try again.";
                    textToSpeechManager.speak(errorMsg);
                    updateResponseUI(errorMsg);
                });
            }
        });
    }

    private String transcribeWithGroq(byte[] pcmData) {
        long groqStart = System.currentTimeMillis();
        try {
            Log.d(TAG, "🚀 Starting GROQ transcription - audio size: " + pcmData.length + " bytes");

            // Convert PCM to WAV
            byte[] wavData = convertPcmToWav(pcmData, SAMPLE_RATE, 1, 16);

            // Create multipart request for Groq
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "audio.wav",
                            RequestBody.create(MediaType.parse("audio/wav"), wavData))
                    .addFormDataPart("model", "distil-whisper-large-v3-en")
                    .addFormDataPart("response_format", "json")
                    .addFormDataPart("language", "en")
                    .addFormDataPart("temperature", "0.0")
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/audio/transcriptions")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .post(requestBody)
                    .build();

            // Execute request with proper resource management
            Response response = null;
            String transcribedText = null; // Declare it here so it's visible everywhere!

            try {
                response = groqClient.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseJson = response.body().string();
                    JSONObject json = new JSONObject(responseJson);
                    transcribedText = json.getString("text");

                    // Log success with timing
                    long groqTime = System.currentTimeMillis() - groqStart;
                    Log.d(TAG, "✅ GROQ transcribed in " + groqTime + "ms: " + transcribedText);

                    return transcribedText;
                } else {
                    Log.e(TAG, "Groq API error: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Error details: " + response.body().string());
                    }
                    return "Error transcribing speech";
                }
            } finally {
                // ALWAYS close the response to prevent connection leaks
                if (response != null) {
                    response.close();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in Groq transcription", e);
            return "Error transcribing speech";
        }
    }

    /**
     * NEW: Ultra-fast streaming with immediate speech synthesis
     */
    private void processWithUltraFastStreaming(String transcribedText, String gameContext) {
        try {
            Log.d(TAG, "🚀 Starting ULTRA-FAST streaming response");

            String selectedMaster = getSelectedChessMaster();

            if ("botvinnik".equals(selectedMaster)) {
                // Use Assistants API for Botvinnik
                processWithAssistantsAPIUltraFast(transcribedText, gameContext);
            } else {
                // Use streaming for other masters
                String systemPrompt = FineTunedModelManager.getInstance(this)
                        .getEnhancedSystemPromptForSelectedMaster();

                String enhancedUserMessage = gameContext + "\n\nQUESTION: " + transcribedText;

                // Track partial responses
                StringBuilder fullResponse = new StringBuilder();
                AtomicReference<Boolean> firstChunkSpoken = new AtomicReference<>(false);

                // Start streaming with ultra-aggressive chunking
                unifiedService.generateStreamingChatResponse(systemPrompt, enhancedUserMessage,
                        new UnifiedOpenAIService.StreamingChatCallback() {
                            private StringBuilder currentSentence = new StringBuilder();
                            private long lastChunkTime = System.currentTimeMillis();

                            @Override
                            public void onPartialResponse(String partialText, boolean isFirst) {
                                Log.d(TAG, "📝 Partial: " + partialText.substring(0, Math.min(20, partialText.length())) + "...");

                                fullResponse.append(partialText);
                                currentSentence.append(partialText);

                                // Ultra-aggressive: Speak as soon as we have 15+ chars or punctuation
                                if (currentSentence.length() > 15 ||
                                        partialText.contains(".") ||
                                        partialText.contains("!") ||
                                        partialText.contains("?") ||
                                        partialText.contains(",") ||
                                        (System.currentTimeMillis() - lastChunkTime > 300)) {

                                    String toSpeak = currentSentence.toString();
                                    currentSentence.setLength(0);
                                    lastChunkTime = System.currentTimeMillis();

                                    mainHandler.post(() -> {
                                        if (!firstChunkSpoken.get()) {
                                            firstChunkSpoken.set(true);
                                            updateUIForProcessing(false);
                                            updateResponseUI(toSpeak);
                                        }

                                        // Debug log
                                        Log.d(TAG, "🎤 About to speak chunk: " + toSpeak.substring(0, Math.min(30, toSpeak.length())) + "...");

                                        // USE YOUR PROVEN textToSpeechManager!
                                        if (textToSpeechManager != null) {
                                            textToSpeechManager.speak(toSpeak, new TextToSpeechManager.OnSpeechCompletedListener() {
                                                @Override
                                                public void onSpeechCompleted() {
                                                    Log.d(TAG, "✅ Chunk spoken successfully");
                                                }
                                            });
                                        } else {
                                            Log.e(TAG, "❌ textToSpeechManager is null!");
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onComplete(String fullResponseText) {
                                Log.d(TAG, "✅ Complete response received");

                                // Speak any remaining text
                                if (currentSentence.length() > 0) {
                                    String remaining = currentSentence.toString();
                                    mainHandler.post(() -> {
                                        Log.d(TAG, "🎤 Speaking final chunk: " + remaining.substring(0, Math.min(30, remaining.length())) + "...");

                                        if (textToSpeechManager != null) {
                                            textToSpeechManager.speak(remaining, new TextToSpeechManager.OnSpeechCompletedListener() {
                                                @Override
                                                public void onSpeechCompleted() {
                                                    Log.d(TAG, "✅ Final chunk spoken");
                                                    if (callback != null) {
                                                        callback.onResponseCompleted(fullResponseText);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    // No remaining text, just signal completion
                                    if (callback != null) {
                                        mainHandler.postDelayed(() -> {
                                            callback.onResponseCompleted(fullResponseText);
                                        }, 500);
                                    }
                                }

                                // Save to conversation history
                                conversationManager.addMessage("user", transcribedText);
                                conversationManager.addMessage("assistant", fullResponseText);
                                conversationManager.saveCurrentConversation();

                                // Update UI with full response
                                mainHandler.post(() -> {
                                    updateResponseUI(fullResponseText);
                                    if (callback != null) {
                                        callback.onResponseReceived(fullResponseText);
                                    }
                                });
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "❌ Streaming error: " + e.getMessage());
                                mainHandler.post(() -> {
                                    updateUIForProcessing(false);
                                    String errorMsg = "I'm having trouble with my analysis. Let me try again.";
                                    textToSpeechManager.speak(errorMsg);
                                    updateResponseUI(errorMsg);
                                });
                            }
                        });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in ultra-fast streaming", e);
            mainHandler.post(() -> {
                updateUIForProcessing(false);
                String errorMsg = "I'm having trouble analyzing that position. Could you try rephrasing?";
                textToSpeechManager.speak(errorMsg);
                updateResponseUI(errorMsg);
            });
        }
    }

    /**
     * NEW: Ultra-fast Assistants API processing
     */
    private void processWithAssistantsAPIUltraFast(String transcribedText, String gameContext) {
        executorService.execute(() -> {
            try {
                // Get current FEN
                GameStateInfo gameState = GameStateRepository.getCurrentState();
                String fenPosition = gameState != null ? gameState.getCurrentFen() :
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

                String response = processWithAssistantsAPI(transcribedText, fenPosition, gameContext);

                // Use ultra-fast TTS
                mainHandler.post(() -> {
                    updateUIForProcessing(false);
                    updateResponseUI(response);

                    // Use streaming TTS for faster response
                    ttsService.speakStreamingText(response, new OpenAITTSService.TTSCallback() {
                        @Override
                        public void onSpeechStarted() {
                            Log.d(TAG, "🎵 TTS started");
                        }

                        @Override
                        public void onSpeechReady(File audioFile) {
                            // Not needed
                        }

                        @Override
                        public void onSpeechCompleted() {
                            if (callback != null) {
                                callback.onResponseCompleted(response);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "TTS error: " + errorMessage);
                        }
                    });

                    if (callback != null) {
                        callback.onResponseReceived(response);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in Assistants API ultra-fast processing", e);
                mainHandler.post(() -> {
                    updateUIForProcessing(false);
                    String errorMsg = "I'm having trouble with my advanced analysis. Let me try a simpler approach.";
                    textToSpeechManager.speak(errorMsg);
                    updateResponseUI(errorMsg);
                });
            }
        });
    }

    /**
     * Build enhanced context quickly
     */
    private String buildEnhancedContext() {
        GameStateInfo gameState = GameStateRepository.getCurrentState();
        String currentFenPosition = gameState != null ? gameState.getCurrentFen() :
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        StringBuilder enhancedContext = new StringBuilder();
        enhancedContext.append("POSITION: ").append(currentFenPosition);

        if (gameState != null) {
            enhancedContext.append("\nPLAYER: ").append(gameState.getPlayerColor());
            enhancedContext.append("\nPHASE: ").append(gameState.getGamePhase());

            List<String> moves = gameState.getMoveHistory();
            if (moves != null && !moves.isEmpty()) {
                enhancedContext.append("\nRECENT_MOVES: ");
                int startIndex = Math.max(0, moves.size() - 6);
                for (int i = startIndex; i < moves.size(); i += 2) {
                    int moveNumber = (startIndex / 2) + (i - startIndex) / 2 + 1;
                    enhancedContext.append(moveNumber).append(".");
                    enhancedContext.append(moves.get(i));
                    if (i + 1 < moves.size()) {
                        enhancedContext.append(" ").append(moves.get(i + 1));
                    }
                    enhancedContext.append(" ");
                }
            }
        }

        return enhancedContext.toString();
    }

    public void testTTS() {
        mainHandler.post(() -> {
            if (textToSpeechManager != null) {
                textToSpeechManager.speak("Testing speech output. Can you hear me?");
                Log.d(TAG, "🔊 Test TTS triggered");
            } else {
                Log.e(TAG, "❌ textToSpeechManager is null in test!");
            }
        });
    }

    // [Keep all the existing helper methods like processWithAssistantsAPI, convertPcmToWav, etc.]
    // I'm not repeating them here to save space, but they remain unchanged in your implementation

    /**
     * Process using Assistants API (for Botvinnik)
     */
    private String processWithAssistantsAPI(String transcribedText, String fenPosition, String gameContext) {
        try {
            ChessMasterAgentManager agentManager = new ChessMasterAgentManager(
                    OpenAIService.getInstance(), this);

            String assistantId = agentManager.getBotvinnikAssistantId();
            if (assistantId == null) {
                return "I'm having trouble connecting to my chess memory. Please try again.";
            }

            if (currentThreadId == null) {
                currentThreadId = agentManager.createConversationThread();
            }

            if (currentThreadId == null) {
                return "I'm having trouble starting our conversation. Please try again.";
            }

            // Enhanced message with full context for Assistants API
            String enhancedMessage = gameContext + "\n\nQUESTION: " + transcribedText;

            String runId = agentManager.sendMessageWithPosition(
                    currentThreadId, assistantId, enhancedMessage, fenPosition);

            if (runId == null) {
                return "I'm having trouble analyzing your question. Please try again.";
            }

            String response = agentManager.getChessMasterResponse(currentThreadId, runId);

            // Add response to conversation history
            conversationManager.addMessage("assistant", response);
            conversationManager.saveCurrentConversation();

            return response;
        } catch (Exception e) {
            Log.e(TAG, "Error in Assistants API processing", e);
            return "I'm having trouble with my advanced analysis. Let me try a simpler approach.";
        }
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
            Log.e(TAG, "Error writing bytes", e);
        }
    }

    private String determineQuestionType(String question) {
        question = question.toLowerCase();

        // Chess position/move questions
        if (question.contains("best move") || question.contains("position") ||
                question.contains("play") || question.contains("should i") ||
                question.contains("what move") || question.contains("next move")) {
            return "CHESS_POSITION";
        }

        // Expanded chess history/people questions
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

    private boolean shouldUseAssistantsApi() {
        String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();
        return "botvinnik".equals(currentMaster);
    }

    public void refreshVoiceSettings() {
        // Get current master
        String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Update the TTS service
        if (textToSpeechManager != null) {
            SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
            String voiceStyle = prefs.getString("voice_style", "auto");
            boolean usePersonality = prefs.getBoolean("use_master_personality", true);

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
     * Updates UI elements when processing starts/stops
     */
    private void updateUIForProcessing(boolean isProcessing) {
        if (callback != null) {
            callback.onProcessingStateChanged(isProcessing);
        }

        try {
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

        try {
            if (responseTextView != null) {
                responseTextView.setText(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating response UI", e);
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
                    new Handler(Looper.getMainLooper()).post(this::stopRecording);
                }
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Check if the audio buffer represents silence
     */
    private boolean isSilence(byte[] buffer, int size) {
        short[] shorts = new short[size / 2];
        ByteBuffer.wrap(buffer, 0, size).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        double sum = 0;
        for (short value : shorts) {
            sum += value * value;
        }
        double rms = Math.sqrt(sum / shorts.length);

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
        return ChessBoardManager.getInstance().getCurrentBoardView();
    }

    /**
     * Gets the selected chess coach profile
     */
    private String getSelectedChessMaster() {
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        return prefs.getString("selected_master", "tal");
    }

    /**
     * Gets the player's color
     */
    private String getPlayerColor() {
        SharedPreferences prefs = getSharedPreferences("chess_prefs", MODE_PRIVATE);
        return prefs.getString("player_color", "White");
    }

    /**
     * Gets the move history from the current game
     */
    private List<String> getMoveHistory() {
        return GameHistoryManager.getInstance().getCurrentGameMoves();
    }

    private String getApiKeyFromPreferences() {
        String apiKey = ApiKeyConfig.getApiKey(this);

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ API Key is empty or null! Check your ApiKeyConfig class.");
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
        SharedPreferences prefs = getSharedPreferences("conversation_prefs", MODE_PRIVATE);
        long lastConversationTime = prefs.getLong("last_conversation_time", 0);

        long thirtyMinutesInMillis = 30 * 60 * 1000;
        boolean shouldResume = System.currentTimeMillis() - lastConversationTime < thirtyMinutesInMillis;

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