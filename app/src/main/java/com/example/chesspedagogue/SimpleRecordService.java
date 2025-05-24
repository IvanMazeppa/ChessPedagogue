package com.example.chesspedagogue;

import com.example.chesspedagogue.OpenAITTSService.OnSpeechCompletedListener;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
    private ConversationManager conversationManager;
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
    private String currentAssistantId;
    private final ExecutorService executorService = Executors.newFixedThreadPool(8); // More threads
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // NEW: Pre-warming for ultra-fast response
    private OpenAITTSService ttsService;
    private volatile boolean servicesWarmed = false;

    // FIXED: Properly configured Groq client with connection management
    private static final OkHttpClient groqClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
            .build();

    public boolean isCurrentlySpeaking() {
        return OpenAITTSService.getInstance(this) != null && OpenAITTSService.getInstance(this).isSpeaking();
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

        // Initialize services in parallel
        CompletableFuture<Void> initFuture = CompletableFuture.runAsync(() -> {
            Log.d(TAG, "🚀 Initializing services in parallel");

            // Initialize unified service
            openAIService = OpenAIService.getInstance();
            openAIService.setApiKey(apiKey);

            // Initialize TTS service
            ttsService = OpenAITTSService.getInstance(this);
            ttsService.setApiKey(apiKey);

            servicesWarmed = true;
        }, executorService);

        // FIXED: Pre-warm Groq connection properly
        executorService.execute(() -> {
            try {
                // Pre-warm Groq connection with proper resource management
                Request warmupRequest = new Request.Builder()
                        .url("https://api.groq.com/openai/v1/models")
                        .header("Authorization", "Bearer " + groqApiKey)
                        .build();

                // CRITICAL: Use try-with-resources to ensure connection is closed
                try (Response warmupResponse = groqClient.newCall(warmupRequest).execute()) {
                    if (warmupResponse.isSuccessful()) {
                        Log.d(TAG, "✅ Groq connection pre-warmed successfully!");
                    } else {
                        Log.w(TAG, "Groq pre-warm returned: " + warmupResponse.code());
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Groq pre-warm failed: " + e.getMessage());
            }
        });

        // Initialize assistant in background
        new Thread(() -> {
            try {
                FineTunedModelManager.getInstance(this).getBotvinnikAssistantIdFullyAsync(new FineTunedModelManager.Callback<String>() {
                    @Override
                    public void onSuccess(String assistantId) {
                        currentAssistantId = assistantId;
                        Log.d(TAG, "✅ Assistant ready: " + currentAssistantId);

                        FineTunedModelManager.getInstance(SimpleRecordService.this).createConversationThreadAsync(new FineTunedModelManager.Callback<String>() {
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

        // Initialize conversation manager
        conversationManager = ConversationManager.getInstance(this);

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
                    OpenAIService.getInstance().setApiKey(testApiKey);
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
        executorService.shutdown();

        // CRITICAL: Shutdown the Groq client's connection pool
        try {
            groqClient.dispatcher().executorService().shutdown();
            groqClient.connectionPool().evictAll();
        } catch (Exception e) {
            Log.e(TAG, "Error shutting down Groq client", e);
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
                    OpenAITTSService.getInstance(this).speak(errorMsg);
                    updateResponseUI(errorMsg);
                });
            }
        });
    }

    private void processWithUltraFastStreaming(String transcribedText, String gameContext) {
        try {
            Log.d(TAG, "🚀 Starting ULTRA-FAST streaming response with personality enhancement");

            String selectedMaster = getSelectedChessMaster();
            FineTunedModelManager modelManager = FineTunedModelManager.getInstance(this);

            if ("botvinnik".equals(selectedMaster)) {
                processWithAssistantsAPIUltraFast(transcribedText, gameContext);
            } else {
                String systemPrompt = modelManager.getEnhancedSystemPromptForSelectedMaster();

                // SMART CONTEXT: Check if the question is about chess positions or general chess topics
                String enhancedUserMessage;

                // Check if the question is about the current position
                boolean isPositionQuestion = checkIfPositionRelated(transcribedText);

                if (isPositionQuestion && gameContext != null && !gameContext.trim().isEmpty()) {
                    // Include game context for position-specific questions
                    enhancedUserMessage = modelManager.generateContextualPrompt(
                            transcribedText, gameContext);
                } else {
                    // For general chess questions, don't include position context
                    enhancedUserMessage = modelManager.generateContextualPrompt(
                            transcribedText, null);
                }

                // Track response state
                StringBuilder fullResponse = new StringBuilder();
                AtomicReference<StringBuilder> currentChunk = new AtomicReference<>(new StringBuilder());
                AtomicBoolean firstChunkSent = new AtomicBoolean(false);
                AtomicInteger chunkCounter = new AtomicInteger(0);
                OpenAITTSService tts = OpenAITTSService.getInstance(this);
                AtomicInteger ttsChunkCounter = new AtomicInteger(0);

                openAIService.generateStreamingChatResponse(systemPrompt, enhancedUserMessage,
                        new OpenAIService.StreamingChatCallback() {
                            private long lastChunkTime = System.currentTimeMillis();

                            @Override
                            public void onPartialResponse(String partialText, boolean isFirst) {
                                Log.d(TAG, "📝 Partial: " + partialText.substring(0, Math.min(20, partialText.length())) + "...");

                                fullResponse.append(partialText);
                                currentChunk.get().append(partialText);

                                // Determine when to speak a chunk
                                boolean shouldSpeak = false;
                                String chunkToSpeak = null;

                                // OPTIMIZATION: Increase chunk size for fewer API calls
                                // Check for natural breaking points
                                if (currentChunk.get().toString().contains(".") ||
                                        currentChunk.get().toString().contains("!") ||
                                        currentChunk.get().toString().contains("?")) {
                                    // Find the last complete sentence
                                    String text = currentChunk.get().toString();
                                    int lastPeriod = Math.max(text.lastIndexOf('.'),
                                            Math.max(text.lastIndexOf('!'),
                                                    text.lastIndexOf('?')));

                                    if (lastPeriod > 0) {
                                        chunkToSpeak = text.substring(0, lastPeriod + 1).trim();
                                        currentChunk.set(new StringBuilder(text.substring(lastPeriod + 1)));
                                        shouldSpeak = true;
                                    }
                                }
                                // OPTIMIZATION: Increase minimum chunk size from 80 to 150 characters
                                else if (currentChunk.get().length() > 150) {
                                    // Find a good breaking point (comma, space)
                                    String text = currentChunk.get().toString();
                                    int breakPoint = text.lastIndexOf(',');
                                    if (breakPoint < 80) {
                                        breakPoint = text.lastIndexOf(' ', 120);
                                    }

                                    if (breakPoint > 40) {
                                        chunkToSpeak = text.substring(0, breakPoint).trim();
                                        currentChunk.set(new StringBuilder(text.substring(breakPoint)));
                                        shouldSpeak = true;
                                    }
                                }
                                // OPTIMIZATION: Increase timeout from 1500ms to 2500ms
                                else if (System.currentTimeMillis() - lastChunkTime > 2500 && currentChunk.get().length() > 40) {
                                    chunkToSpeak = currentChunk.get().toString().trim();
                                    currentChunk.set(new StringBuilder());
                                    shouldSpeak = true;
                                }

                                // Then update the speaking section to use this counter:
                                if (shouldSpeak && chunkToSpeak != null && !chunkToSpeak.isEmpty()) {
                                    lastChunkTime = System.currentTimeMillis();
                                    final String textToSpeak = chunkToSpeak;
                                    final int chunkId = chunkCounter.getAndIncrement();
                                    final boolean isFirstChunk = !firstChunkSent.getAndSet(true);
                                    final int ttsChunkId = ttsChunkCounter.getAndIncrement(); // NEW LINE!

                                    mainHandler.post(() -> {
                                        if (isFirstChunk) {
                                            updateUIForProcessing(false);
                                            updateResponseUI(textToSpeak);
                                        }

                                        Log.d(TAG, "🎤 Speaking chunk " + chunkId + ": " +
                                                textToSpeak.substring(0, Math.min(30, textToSpeak.length())) + "...");

                                        // Instead of speakDirect, use the streaming method with proper chunk ID
                                        tts.speakChunk(textToSpeak, ttsChunkId, false, new OpenAITTSService.TTSCallback() {
                                            @Override
                                            public void onSpeechStarted() {
                                                Log.d(TAG, "Started speaking chunk " + chunkId);
                                            }

                                            @Override
                                            public void onSpeechReady(File audioFile) {
                                                // Audio ready
                                            }

                                            @Override
                                            public void onSpeechCompleted() {
                                                Log.d(TAG, "Completed speaking chunk " + chunkId);
                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                Log.e(TAG, "Error speaking chunk " + chunkId + ": " + errorMessage);
                                            }
                                        });
                                    });
                                }
                            }
                            @Override
                            public void onComplete(String fullResponseText) {
                                Log.d(TAG, "✅ Complete response received");

                                // Speak any remaining text
                                if (currentChunk.get().length() > 0) {
                                    String remainingText = currentChunk.get().toString().trim();
                                    if (!remainingText.isEmpty()) {
                                        final int finalChunkId = chunkCounter.get();
                                        final int ttsFinalChunkId = ttsChunkCounter.get();

                                        mainHandler.post(() -> {
                                            Log.d(TAG, "🎤 Speaking final chunk " + finalChunkId);

                                            tts.speakChunk(remainingText, ttsFinalChunkId, true, new OpenAITTSService.TTSCallback() {
                                                @Override
                                                public void onSpeechStarted() {
                                                    Log.d(TAG, "Started speaking final chunk");
                                                }

                                                @Override
                                                public void onSpeechReady(File audioFile) {
                                                    // Audio ready
                                                }

                                                @Override
                                                public void onSpeechCompleted() {
                                                    Log.d(TAG, "✅ All speech completed");
                                                    if (callback != null) {
                                                        callback.onResponseCompleted(fullResponseText);
                                                    }
                                                }

                                                @Override
                                                public void onError(String errorMessage) {
                                                    Log.e(TAG, "Error speaking final chunk: " + errorMessage);
                                                }
                                            });
                                        });
                                    }
                                }

                                // CRITICAL FIX: Save conversation on main thread
                                String enrichedResponse = modelManager.enrichResponseWithPersonality(
                                        fullResponseText, selectedMaster);

                                // Save conversation using thread-safe methods
                                mainHandler.post(() -> {
                                    try {
                                        Log.d(TAG, "💾 Saving conversation - User: " + transcribedText.substring(0, Math.min(30, transcribedText.length())) + "...");
                                        Log.d(TAG, "💾 Saving conversation - Assistant: " + enrichedResponse.substring(0, Math.min(30, enrichedResponse.length())) + "...");

                                        conversationManager.addMessage("user", transcribedText);
                                        conversationManager.addMessage("assistant", enrichedResponse);
                                        conversationManager.saveCurrentConversation();

                                        Log.d(TAG, "✅ Conversation saved successfully! Session: " + conversationManager.getCurrentSessionId());
                                        Log.d(TAG, "✅ Conversation size: " + conversationManager.getConversationSize() + " messages");
                                    } catch (Exception e) {
                                        Log.e(TAG, "❌ Error saving conversation", e);
                                    }

                                    updateResponseUI(enrichedResponse);
                                    if (callback != null) {
                                        callback.onResponseReceived(enrichedResponse);
                                    }
                                });
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "❌ Streaming error: " + e.getMessage());
                                mainHandler.post(() -> {
                                    updateUIForProcessing(false);
                                    String errorMsg = getPersonalityErrorMessage(selectedMaster);
                                    tts.speak(errorMsg);
                                    updateResponseUI(errorMsg);
                                });
                            }
                        });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in ultra-fast streaming", e);
            mainHandler.post(() -> {
                updateUIForProcessing(false);
                String errorMsg = getPersonalityErrorMessage(getSelectedChessMaster());
                OpenAITTSService.getInstance(this).speak(errorMsg);
                updateResponseUI(errorMsg);
            });
        }
    }

    /**
     * Check if the user's question is about the current chess position
     */
    private boolean checkIfPositionRelated(String question) {
        if (question == null) return false;

        String lowerQuestion = question.toLowerCase();

        // Keywords that indicate position-specific questions
        String[] positionKeywords = {
                "this position", "current position", "this move", "what should i play",
                "best move", "analyze", "evaluation", "this board", "here",
                "what do you think of", "how about", "should i take", "can i play",
                "is it good to", "what if i"
        };

        // Keywords that indicate general chess questions
        String[] generalKeywords = {
                "tell me about", "who won", "championship", "history", "explain",
                "what is", "how to", "rules", "opening theory", "endgame theory",
                "famous game", "chess master", "biography", "when did", "where was"
        };

        // Check for general questions first
        for (String keyword : generalKeywords) {
            if (lowerQuestion.contains(keyword)) {
                return false;
            }
        }

        // Then check for position-specific questions
        for (String keyword : positionKeywords) {
            if (lowerQuestion.contains(keyword)) {
                return true;
            }
        }

        // Default to false for ambiguous questions
        return false;
    }

    /**
     * FIXED: Ultra-fast Groq transcription with proper resource management
     */
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

            // CRITICAL FIX: Use try-with-resources to ensure proper connection cleanup
            try (Response response = groqClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String transcribedText = json.getString("text");

                        long groqTime = System.currentTimeMillis() - groqStart;
                        Log.d(TAG, "✅ GROQ transcribed in " + groqTime + "ms: " + transcribedText);

                        return transcribedText;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Groq response", e);
                        return "Error transcribing speech";
                    }
                } else {
                    Log.e(TAG, "Groq API error: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Error details: " + response.body().string());
                    }
                    return "Error transcribing speech";
                }
            } // Response is automatically closed here by try-with-resources

        } catch (Exception e) {
            Log.e(TAG, "Error in Groq transcription", e);
            return "Error transcribing speech";
        }
    }

    /**
     * Get personality-specific error messages
     */
    private String getPersonalityErrorMessage(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "Ah, my friend, the position has confused even me! Let's try again.";
            case "fischer":
                return "This is unacceptable. Let me recalculate.";
            case "kasparov":
                return "Technical difficulties! But we never give up - try again!";
            case "kramnik":
                return "I need to think more deeply about this. Please rephrase your question.";
            default:
                return "I'm having trouble analyzing that position. Could you try rephrasing?";
        }
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

    /**
     * Ultra-fast Assistants API processing with enhanced Botvinnik personality
     */
    private void processWithAssistantsAPIUltraFast(String transcribedText, String gameContext) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🤖 Starting Botvinnik's scientific analysis via Assistants API");

                // Get current FEN position
                GameStateInfo gameState = GameStateRepository.getCurrentState();
                String fenPosition = gameState != null ? gameState.getCurrentFen() :
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

                // Get the enhanced response from Botvinnik
                String response = processWithEnhancedBotvinnikAssistant(transcribedText, fenPosition, gameContext);

                // Use ultra-fast TTS with Botvinnik's personality
                mainHandler.post(() -> {
                    updateUIForProcessing(false);
                    updateResponseUI(response);

                    // Get Botvinnik's specific voice instructions
                    FineTunedModelManager modelManager = FineTunedModelManager.getInstance(this);
                    String voiceInstructions = modelManager.getEnhancedVoiceInstructions("botvinnik", true);

                    // Set the voice personality
                    ttsService.setVoicePersonalizationInstructions(voiceInstructions);

                    // Speak with Botvinnik's characteristic measured pace
                    ttsService.speakStreamingText(response, new OpenAITTSService.TTSCallback() {
                        @Override
                        public void onSpeechStarted() {
                            Log.d(TAG, "🎓 Professor Botvinnik begins his analysis...");
                        }

                        @Override
                        public void onSpeechReady(File audioFile) {
                            // Audio file ready for playback
                        }

                        @Override
                        public void onSpeechCompleted() {
                            Log.d(TAG, "✅ Botvinnik's wisdom delivered successfully");
                            if (callback != null) {
                                callback.onResponseCompleted(response);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "TTS error: " + errorMessage);
                            // Use Botvinnik's personality even in errors
                            String botvinnikError = "My apologies, there seems to be a technical issue. " +
                                    "As I always taught my students - preparation prevents such problems.";
                            updateResponseUI(botvinnikError);
                        }
                    });

                    if (callback != null) {
                        callback.onResponseReceived(response);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error in Botvinnik's Assistants API processing", e);
                mainHandler.post(() -> {
                    updateUIForProcessing(false);

                    // Botvinnik-specific error message
                    String errorMsg = "It seems my analysis system needs recalibration. " +
                            "In the Soviet Chess School, we would have backup methods. " +
                            "Please rephrase your question, and I'll apply a different approach.";

                    // Set Botvinnik's voice for the error message too
                    FineTunedModelManager modelManager = FineTunedModelManager.getInstance(this);
                    String voiceInstructions = modelManager.getEnhancedVoiceInstructions("botvinnik", true);
                    OpenAITTSService.getInstance(this).setVoicePersonalizationInstructions(voiceInstructions);
                    OpenAITTSService.getInstance(this).speak(errorMsg);

                    updateResponseUI(errorMsg);
                });
            }
        });
    }

    /**
     * Enhanced Botvinnik processing with rich personality
     */
    private String processWithEnhancedBotvinnikAssistant(String transcribedText, String fenPosition, String gameContext) {
        try {
            FineTunedModelManager modelManager = FineTunedModelManager.getInstance(this);

            // Get or create Botvinnik's assistant
            String assistantId = modelManager.getBotvinnikAssistantId();
            if (assistantId == null) {
                return "I apologize, but my analytical systems are not responding. " +
                        "This reminds me of the importance of systematic preparation - " +
                        "something seems to have been overlooked.";
            }

            // Create or reuse conversation thread
            if (currentThreadId == null) {
                currentThreadId = modelManager.createConversationThread();
                Log.d(TAG, "Created new thread for Botvinnik: " + currentThreadId);
            }

            if (currentThreadId == null) {
                return "My analytical framework requires initialization. " +
                        "In the Soviet Chess School, we always had our systems ready. " +
                        "Please give me a moment and try again.";
            }

            // Build Botvinnik's characteristic analytical message
            StringBuilder enhancedMessage = new StringBuilder();

            // Add Botvinnik's personality context
            enhancedMessage.append("Professor Botvinnik analyzing the position with systematic precision.\n\n");

            // Add the game context with Botvinnik's analytical style
            enhancedMessage.append("POSITION FOR SCIENTIFIC ANALYSIS:\n");
            enhancedMessage.append("FEN: ").append(fenPosition).append("\n");

            if (gameContext != null && !gameContext.trim().isEmpty()) {
                enhancedMessage.append("\nGAME CONTEXT:\n").append(gameContext).append("\n");
            }

            // Add Botvinnik's methodical approach reminder
            enhancedMessage.append("\n(Apply the Botvinnik method: systematic evaluation of material, ");
            enhancedMessage.append("pawn structure, piece activity, king safety, and concrete variations.)\n\n");

            // Add the student's question
            enhancedMessage.append("STUDENT'S QUESTION: ").append(transcribedText);

            // Add personality reminder for the response
            enhancedMessage.append("\n\n(Respond as Botvinnik would: scientifically precise, ");
            enhancedMessage.append("referencing chess principles, Soviet training methods, ");
            enhancedMessage.append("and personal experiences from world championship matches.)");

            // Send to Assistants API with Botvinnik's personality
            String runId = modelManager.sendMessageWithPosition(
                    currentThreadId, assistantId, enhancedMessage.toString(), fenPosition);

            if (runId == null) {
                return "My calculation engine has encountered an anomaly. " +
                        "This never happened during my matches with Tal! " +
                        "Let's recalibrate and try again.";
            }

            // Get Botvinnik's response
            String response = modelManager.getChessMasterResponse(currentThreadId, runId);

            // Add to conversation history with Botvinnik's context
            conversationManager.addMessage("user", transcribedText);
            conversationManager.addMessage("assistant", "[Botvinnik] " + response);
            conversationManager.saveCurrentConversation();

            // Apply final Botvinnik personality touches if needed
            if (!response.contains("Soviet") && !response.contains("systematic") &&
                    !response.contains("method") && !response.contains("students")) {
                // If the response lacks Botvinnik's characteristic elements, add a subtle touch
                response += " This analysis follows the systematic approach I've always advocated.";
            }

            return response;

        } catch (Exception e) {
            Log.e(TAG, "Error in enhanced Botvinnik processing", e);

            // Return a very Botvinnik-like error message
            return "My dear student, even the most rigorous systems can encounter difficulties. " +
                    "As I taught Kasparov and Kramnik - when facing the unexpected, " +
                    "return to fundamental principles. Please restate your question, " +
                    "and I'll apply a different analytical framework.";
        }
    }

    /**
     * Resets the current conversation and starts a new one
     */
    public void resetConversation() {
        OpenAITTSService tts = OpenAITTSService.getInstance(this);
        if (tts != null) {
            tts.interrupt();
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
        if (OpenAITTSService.getInstance(this) != null) {
            OpenAITTSService.getInstance(this).interrupt();
            Log.d(TAG, "Speech interrupted by user tap");
        }
    }

    // Helper method to convert PCM to WAV
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

    public void refreshVoiceSettings() {
        // Get current master
        String currentMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Update the TTS service
        SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
        String voiceStyle = prefs.getString("voice_style", "auto");
        boolean usePersonality = prefs.getBoolean("use_master_personality", true);

        ChessCoachManager.getInstance(this).updateTTSSettings(voiceStyle, usePersonality);
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
     * Gets the selected chess coach profile
     */
    private String getSelectedChessMaster() {
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        return prefs.getString("selected_master", "tal");
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