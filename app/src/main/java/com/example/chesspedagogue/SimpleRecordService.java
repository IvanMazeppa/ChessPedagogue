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
import java.lang.ref.WeakReference;
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
    // Groq API key now retrieved from centralized ApiKeys class
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
    private String apiKey = "OPENAI_API_KEY";

    // UI references - Using WeakReferences to prevent memory leaks
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

    // NEW: Three-stage response manager
    private ThreeStageResponseManager threeStageManager;
    
    // NEW: Direct Responses API integration for enhanced voice responses
    private ChessMasterResponsesManager responsesManager;
    private ResponsesAPIIntegrationHelper integrationHelper;
    private String currentVoiceSessionId = null;
    private String lastSessionMaster = null; // Track master consistency
    private boolean conversationThreadActive = false; // Track if auto-conversation is ongoing

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

    // NEW: Track current response stage for UI updates
    private ThreeStageResponseManager.ResponseStage currentStage = null;
    private String latestResponse = "";
    
    // ANTI-DUPLICATE: Prevent multiple fallback calls for same input
    private volatile boolean isProcessingInput = false;
    private String lastProcessedInput = "";
    private long lastProcessingTime = 0;
    private static final long PROCESSING_COOLDOWN_MS = 3000; // 3 seconds

    public boolean isCurrentlySpeaking() {
        return TTSServiceManager.getOpenAITTSService(this) != null && TTSServiceManager.getOpenAITTSService(this).isSpeaking();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SimpleRecordService created - starting optimizations with 3-stage system");

        // Pre-warm all services for instant response
        preWarmServices();

        // Initialize OpenAI service
        openAIService = OpenAIService.getInstance();
        apiKey = getApiKeyFromPreferences();

        // NEW: Initialize 3-stage response manager
        threeStageManager = ThreeStageResponseManager.getInstance(this);
        
        // NEW: Initialize Responses API integration for enhanced voice interactions
        responsesManager = ChessMasterResponsesManager.getInstance(this);
        integrationHelper = ResponsesAPIIntegrationHelper.getInstance(this);
        
        // CONVERSATION THREADING FIX: Restore session state for conversation continuity
        restoreSessionState();

        // Initialize services in parallel
        CompletableFuture<Void> initFuture = CompletableFuture.runAsync(() -> {
            Log.d(TAG, "🚀 Initializing services in parallel");

            // Initialize unified service
            openAIService = OpenAIService.getInstance();
            openAIService.setApiKey(apiKey);

            // Initialize TTS service
            ttsService = TTSServiceManager.getOpenAITTSService(this);
            ttsService.setApiKey(apiKey);

            servicesWarmed = true;
        }, executorService);

        // FIXED: Pre-warm Groq connection properly
        executorService.execute(() -> {
            try {
                // Pre-warm Groq connection with proper resource management
                Request warmupRequest = new Request.Builder()
                        .url("https://api.groq.com/openai/v1/models")
                        .header("Authorization", "Bearer " + ApiKeys.getGroqKey())
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
                    TTSServiceManager.getOpenAITTSService(this).setApiKey(testApiKey);
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
        // Clean up recording
        stopRecording();
        
        // Clean up handlers to prevent leaks
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        
        // Clear callback reference
        if (callback != null) {
            callback = null;
        }
        
        // Shutdown executor service properly
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    Log.w(TAG, "ExecutorService did not terminate in time");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Clean up silence detector
        if (silenceDetector != null && !silenceDetector.isShutdown()) {
            silenceDetector.shutdownNow();
        }

        // CRITICAL: Shutdown the Groq client's connection pool
        try {
            groqClient.dispatcher().executorService().shutdown();
            groqClient.connectionPool().evictAll();
        } catch (Exception e) {
            Log.e(TAG, "Error shutting down Groq client", e);
        }
        
        // Clean up temp files
        cleanupTempFiles();

        Log.d(TAG, "SimpleRecordService destroyed - all resources cleaned up");
        super.onDestroy();
    }
    
    /**
     * Clean up temporary recording files
     */
    private void cleanupTempFiles() {
        try {
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                File[] tempFiles = cacheDir.listFiles((dir, name) -> 
                    name.startsWith("recording_") && (name.endsWith(".pcm") || name.endsWith(".wav")));
                if (tempFiles != null) {
                    for (File file : tempFiles) {
                        if (!file.delete()) {
                            Log.w(TAG, "Failed to delete temp file: " + file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up temp files", e);
        }
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
            ServiceCallback callback = getCallback();
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
        byte[] buffer = null;
        try {
            outputStream = new FileOutputStream(outputFile);
            buffer = new byte[bufferSize];

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
        ServiceCallback callback = getCallback();
        if (callback != null) {
            callback.onRecordingStopped();
        }

        // Process the recording if file exists and has content
        if (outputFile != null && outputFile.exists() && outputFile.length() > 0) {
            processRecordingWithThreeStages();
        } else {
            Log.d(TAG, "No recording to process");
        }
    }

    /**
     * NEW: Process recording using the 3-stage system
     */
    private void processRecordingWithThreeStages() {
        Log.d(TAG, "⚡ Starting 3-stage processing pipeline");

        ServiceCallback callback = getCallback();
        if (callback != null) {
            callback.onProcessingStateChanged(true);
        }

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Recording permission not granted!");
            ServiceCallback cb = getCallback();
            if (cb != null) {
                cb.onProcessingStateChanged(false);
                cb.onResponseReceived("I need permission to hear your voice. Please grant microphone access in settings.");
            }
            return;
        }

        // Start processing
        executorService.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();

                // Transcribe with Groq
                byte[] pcmData = readFileToBytes(outputFile);
                String transcribedText = transcribeWithGroq(pcmData);

                // CRITICAL FIX: Handle null transcription results to prevent sending to AI
                if (transcribedText == null || transcribedText.trim().isEmpty()) {
                    Log.w(TAG, "❌ Transcription failed or returned empty - stopping processing");
                    mainHandler.post(() -> {
                        updateUIForProcessing(false);
                        ServiceCallback transcriptionCallback = getCallback();
                        if (transcriptionCallback != null) {
                            transcriptionCallback.onTranscriptionReceived(""); // Send empty string, not null
                        }
                        // Don't show error to user, just silently fail
                        Log.d(TAG, "🔇 Transcription failed silently - no user message");
                    });
                    return; // Exit processing here - don't send null to AI
                }

                long transcriptionTime = System.currentTimeMillis() - startTime;
                Log.d(TAG, "⏱️ GROQ Transcription completed in " + transcriptionTime + "ms");

                // Show transcribed text to user immediately
                mainHandler.post(() -> {
                    ServiceCallback transcriptionCallback = getCallback();
                    Log.d(TAG, "🔍 Transcription callback check: " + (transcriptionCallback != null ? "FOUND" : "NULL"));
                    Log.d(TAG, "🔍 Service callback instance: " + transcriptionCallback);
                    Log.d(TAG, "🔍 Service callback: " + (callback != null ? "EXISTS" : "NULL"));
                    if (transcriptionCallback != null) {
                        Log.d(TAG, "📝 Calling onTranscriptionReceived with: " + transcribedText);
                        transcriptionCallback.onTranscriptionReceived(transcribedText);
                    } else {
                        Log.e(TAG, "❌ No callback registered - transcription UI will not show!");
                        Log.e(TAG, "❌ Service bound but callback missing - registration failed!");
                    }
                });

                // RESTORED: Check if question is relevant before processing
                if (!isRelevantChessQuestion(transcribedText)) {
                    Log.d(TAG, "🚫 Filtering out irrelevant question: " + transcribedText);
                    mainHandler.post(() -> {
                        updateUIForProcessing(false);
                        String filterMsg = "I'm your chess coach! Please ask me about chess positions, moves, or chess history.";
                        TTSServiceManager.getOpenAITTSService(this).speak(filterMsg);
                        updateResponseUI(filterMsg);
                    });
                    return;
                }

                // FIXED: Only include game context for position-related questions
                String gameContext = "";
                if (checkIfPositionRelated(transcribedText)) {
                    Log.d(TAG, "🎯 Position-related question - including board context");
                    gameContext = buildEnhancedContext();
                } else {
                    Log.d(TAG, "💭 General chess question - no board context needed");
                }

                // NEW: Enhanced processing with direct Responses API option
                Log.d(TAG, "🚀 Starting enhanced voice processing with Responses API integration");
                processEnhancedVoiceResponse(transcribedText, gameContext);

            } catch (Exception e) {
                Log.e(TAG, "Error in 3-stage processing", e);
                mainHandler.post(() -> {
                    updateUIForProcessing(false);
                    String errorMsg = "I'm having trouble connecting to my chess brain. Let's try again.";
                    TTSServiceManager.getOpenAITTSService(this).speak(errorMsg);
                    updateResponseUI(errorMsg);
                });
            }
        });
    }

    /**
     * NEW: Enhanced voice processing with intelligent Responses API routing
     */
    private void processEnhancedVoiceResponse(String transcribedText, String gameContext) {
        String selectedMaster = getSelectedChessMaster();
        boolean useResponsesAPI = integrationHelper.shouldUseResponsesAPI(selectedMaster);
        
        // 👤 ENHANCED: Add user profile context to game context
        String enhancedGameContext = addUserProfileContext(selectedMaster, gameContext);
        
        if (useResponsesAPI) {
            Log.d(TAG, "🚀 Using direct Responses API for voice: " + selectedMaster);
            processVoiceWithResponsesAPI(selectedMaster, transcribedText, enhancedGameContext);
        } else {
            Log.d(TAG, "⚠️ Fallback to 3-stage system for: " + selectedMaster);
            processWithThreeStageSystem(transcribedText, enhancedGameContext);
        }
    }
    
    /**
     * 👤 ENHANCED: Add user profile context to game context for personalized AI responses
     */
    private String addUserProfileContext(String masterName, String gameContext) {
        try {
            UserProfileManager profileManager = UserProfileManager.getInstance(this);
            
            if (profileManager.hasProfile()) {
                String userContext = profileManager.getAIContextForMaster(masterName);
                Log.d(TAG, "👤 Adding user profile context for " + masterName);
                
                // Prepend user context to game context
                return userContext + "\n\n" + gameContext;
            } else {
                Log.d(TAG, "👤 No user profile found - using standard context");
                return gameContext;
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error adding user profile context", e);
            // Return original context if there's an error
            return gameContext;
        }
    }
    
    /**
     * ENHANCED: Voice processing with Responses API and CONVERSATION THREADING (FIXED!)
     */
    private void processVoiceWithResponsesAPI(String masterName, String transcribedText, String gameContext) {
        try {
            Log.d(TAG, "🎙️ Processing voice with Responses API: " + masterName);
            
            // 🔄 CRITICAL FIX: Check session validity and master consistency
            boolean needNewSession = shouldCreateNewSession(masterName);
            
            if (needNewSession) {
                Log.d(TAG, "🆕 Creating new voice session - reason: " + 
                           (currentVoiceSessionId == null ? "no_session" : 
                            !masterName.equals(lastSessionMaster) ? "master_changed" : "session_invalid"));
                
                // Create new voice session with conversation threading enabled
                createNewVoiceSession(masterName, transcribedText, gameContext);
            } else {
                Log.d(TAG, "🔄 Continuing existing voice session: " + currentVoiceSessionId);
                // Use existing session for conversation threading
                continueVoiceConversation(currentVoiceSessionId, transcribedText, gameContext);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in voice Responses API processing", e);
            // Fallback to 3-stage system
            processWithThreeStageSystem(transcribedText, gameContext);
        }
    }
    
    /**
     * 🔍 Check if we need to create a new session
     */
    private boolean shouldCreateNewSession(String masterName) {
        // Need new session if no current session exists
        if (currentVoiceSessionId == null) {
            return true;
        }
        
        // Need new session if master changed
        if (!masterName.equals(lastSessionMaster)) {
            Log.d(TAG, "🔄 Master changed: " + lastSessionMaster + " → " + masterName);
            return true;
        }
        
        // Check if session is still valid in responsesManager
        if (responsesManager != null && !responsesManager.isSessionActive(currentVoiceSessionId)) {
            Log.d(TAG, "💀 Current session expired or invalid: " + currentVoiceSessionId);
            return true;
        }
        
        // Session is valid and master is consistent
        return false;
    }
    
    /**
     * 🆕 Create new voice session with threading support
     */
    private void createNewVoiceSession(String masterName, String transcribedText, String gameContext) {
        // Save session state for persistence
        saveSessionState(masterName);
        
        responsesManager.createResponseSession(masterName, "voice_conversation_thread",
            new ChessMasterResponsesManager.ResponseCallback() {
                @Override
                public void onResponseStart(String sessionId) {
                    currentVoiceSessionId = sessionId;
                    lastSessionMaster = masterName;
                    conversationThreadActive = true;
                    
                    Log.d(TAG, "✅ Voice conversation thread started: " + sessionId);
                    saveSessionState(masterName); // Persist immediately
                    
                    // Send initial message to start the conversation thread
                    sendVoiceMessageToSession(sessionId, transcribedText, gameContext);
                }
                
                @Override
                public void onResponseChunk(String chunk, boolean isFirst) {
                    // Handle streaming response in conversation thread
                    handleConversationChunk(chunk, isFirst);
                }
                
                @Override
                public void onResponseComplete(String fullResponse) {
                    // Mark conversation turn complete, but keep thread active for auto-continuation
                    Log.d(TAG, "🎯 Conversation turn complete, thread remains active for auto-replies");
                    notifyConversationTurnComplete(masterName, fullResponse);
                }
                
                @Override
                public void onConversationTurn(String speaker, String message) {
                    Log.d(TAG, "💬 Conversation turn: " + speaker + " → " + message);
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "❌ Voice conversation thread failed: " + error);
                    conversationThreadActive = false;
                    // Fallback to 3-stage system
                    processWithThreeStageSystem(transcribedText, gameContext);
                }
            });
    }
    
    /**
     * 🔄 Continue existing voice conversation thread
     */
    private void continueVoiceConversation(String sessionId, String transcribedText, String gameContext) {
        Log.d(TAG, "🔄 Continuing conversation thread: " + sessionId);
        conversationThreadActive = true;
        
        // Send message to existing session with threading context
        sendVoiceMessageToSession(sessionId, transcribedText, gameContext);
    }
    
    /**
     * 💾 Save session state for persistence across service restarts
     */
    private void saveSessionState(String masterName) {
        try {
            SharedPreferences prefs = getSharedPreferences("VoiceConversationState", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putString("currentVoiceSessionId", currentVoiceSessionId);
            editor.putString("lastSessionMaster", masterName);
            editor.putBoolean("conversationThreadActive", conversationThreadActive);
            editor.putLong("sessionTimestamp", System.currentTimeMillis());
            
            editor.apply();
            Log.d(TAG, "💾 Voice session state saved for master: " + masterName);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to save session state", e);
        }
    }
    
    /**
     * 📂 Restore session state from persistence
     */
    private void restoreSessionState() {
        try {
            SharedPreferences prefs = getSharedPreferences("VoiceConversationState", MODE_PRIVATE);
            
            String savedSessionId = prefs.getString("currentVoiceSessionId", null);
            String savedMaster = prefs.getString("lastSessionMaster", null);
            boolean savedThreadActive = prefs.getBoolean("conversationThreadActive", false);
            long sessionTimestamp = prefs.getLong("sessionTimestamp", 0);
            
            // Check if saved session is recent (within last 10 minutes)
            long ageMs = System.currentTimeMillis() - sessionTimestamp;
            boolean isRecentSession = ageMs < 10 * 60 * 1000; // 10 minutes
            
            if (savedSessionId != null && savedMaster != null && isRecentSession) {
                // Validate that the session still exists
                if (responsesManager != null && responsesManager.isSessionActive(savedSessionId)) {
                    currentVoiceSessionId = savedSessionId;
                    lastSessionMaster = savedMaster;
                    conversationThreadActive = savedThreadActive;
                    
                    Log.d(TAG, "📂 Voice session state restored: " + savedSessionId + " (master: " + savedMaster + ")");
                } else {
                    Log.d(TAG, "💀 Saved session no longer active, will create new one");
                    clearSessionState();
                }
            } else {
                Log.d(TAG, "⏰ No recent session to restore");
                clearSessionState();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to restore session state", e);
            clearSessionState();
        }
    }
    
    /**
     * 🗑️ Clear saved session state
     */
    private void clearSessionState() {
        currentVoiceSessionId = null;
        lastSessionMaster = null;
        conversationThreadActive = false;
        
        SharedPreferences prefs = getSharedPreferences("VoiceConversationState", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * 🔄 Handle conversation chunk streaming
     */
    private void handleConversationChunk(String chunk, boolean isFirst) {
        if (callback != null) {
            if (isFirst) {
                callback.onProcessingStateChanged(true);
            }
            // Stream the chunk to the UI
            mainHandler.post(() -> {
                if (callback != null) {
                    // Pass chunk to existing callback system
                    callback.onResponseReceived(chunk);
                }
            });
        }
    }
    
    /**
     * 🎯 Notify when conversation turn is complete
     */
    private void notifyConversationTurnComplete(String masterName, String fullResponse) {
        if (callback != null) {
            mainHandler.post(() -> {
                callback.onResponseCompleted(fullResponse);
            });
        }
        
        // Note: Thread remains active for potential follow-up questions
        Log.d(TAG, "💬 " + masterName + " conversation turn complete, thread active for follow-ups");
    }
    
    /**
     * NEW: Send voice message to established Responses API session
     */
    private void sendVoiceMessageToSession(String sessionId, String transcribedText, String gameContext) {
        responsesManager.sendMessage(sessionId, transcribedText, gameContext,
            new ChessMasterResponsesManager.ResponseCallback() {
                private StringBuilder responseBuilder = new StringBuilder();
                private boolean hasSpoken = false;
                
                @Override
                public void onResponseStart(String sessionId) {
                    Log.d(TAG, "🎤 Voice response processing started");
                    mainHandler.post(() -> updateUIForProcessing(false));
                }
                
                @Override
                public void onResponseChunk(String chunk, boolean isFirst) {
                    responseBuilder.append(chunk);
                    // For voice mode, collect all chunks and speak the complete response
                    Log.d(TAG, "📝 Voice chunk received: " + chunk.substring(0, Math.min(20, chunk.length())) + "... (accumulated: " + responseBuilder.length() + " chars)");
                }
                
                @Override
                public void onResponseComplete(String fullResponse) {
                    String finalResponse = !responseBuilder.toString().trim().isEmpty() 
                        ? responseBuilder.toString().trim() 
                        : fullResponse;
                    
                    if (finalResponse != null && !finalResponse.trim().isEmpty()) {
                        Log.d(TAG, "✅ Voice Responses API completed: " + finalResponse.substring(0, Math.min(50, finalResponse.length())));
                        Log.d(TAG, "🎵 Speaking COMPLETE response: " + finalResponse);
                        
                        mainHandler.post(() -> {
                            updateResponseUI(finalResponse);
                            ServiceCallback callback = getCallback();
                            if (callback != null) {
                                callback.onResponseCompleted(finalResponse);
                            }
                            
                            // 🎭 FIXED: Use master-specific voice to maintain accent and personality
                            String selectedMaster = getSelectedChessMaster();
                            Log.d(TAG, "🎭 Speaking response with " + selectedMaster + "'s voice");
                            TTSServiceManager.speakWithSpecificMaster(SimpleRecordService.this, selectedMaster, finalResponse, 
                                new OpenAITTSService.OnSpeechCompletedListener() {
                                    @Override
                                    public void onSpeechCompleted() {
                                        Log.d(TAG, "🎵 Complete voice response speech finished with " + selectedMaster + "'s accent");
                                    }
                                });
                        });
                        
                        // Save to conversation manager
                        conversationManager.addMessage("user", transcribedText);
                        conversationManager.addMessage("assistant", finalResponse);
                        conversationManager.saveCurrentConversation();
                        
                    } else {
                        // Fallback if empty response
                        Log.w(TAG, "⚠️ Empty response from voice Responses API, falling back");
                        processWithThreeStageSystem(transcribedText, gameContext);
                    }
                }
                
                @Override
                public void onConversationTurn(String speaker, String message) {
                    Log.d(TAG, "💬 Voice conversation turn: " + speaker);
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "❌ Voice Responses API error: " + error);
                    // Fallback to 3-stage system
                    processWithThreeStageSystem(transcribedText, gameContext);
                }
            });
    }

    /**
     * LEGACY: Process with the 3-stage system (fallback)
     */
    private void processWithThreeStageSystem(String transcribedText, String gameContext) {
        // ANTI-DUPLICATE: Check if we're already processing the same input
        long currentTime = System.currentTimeMillis();
        if (isProcessingInput && 
            transcribedText.equals(lastProcessedInput) && 
            (currentTime - lastProcessingTime) < PROCESSING_COOLDOWN_MS) {
            Log.w(TAG, "🚫 DUPLICATE FALLBACK BLOCKED: Same input within " + PROCESSING_COOLDOWN_MS + "ms");
            return;
        }
        
        // Mark as processing
        isProcessingInput = true;
        lastProcessedInput = transcribedText;
        lastProcessingTime = currentTime;
        
        try {
            Log.d(TAG, "🎭 Starting 3-stage response system");

            // Reset stage tracking
            currentStage = null;
            latestResponse = "";

            // Use ThreeStageResponseManager
            threeStageManager.processThreeStageResponse(transcribedText, gameContext,
                    new ThreeStageResponseManager.ThreeStageCallback() {

                        @Override
                        public void onStageResponse(ThreeStageResponseManager.ResponseStage stage, String response, boolean isFinal) {
                            Log.d(TAG, "✨ " + stage.getDisplayName() + " completed: " + response.substring(0, Math.min(50, response.length())) + "...");

                            currentStage = stage;
                            latestResponse = response;

                            // Update UI based on stage
                            switch (stage) {
                                case STAGE_1_QUICK:
                                    // Quick response - show immediately and start speaking
                                    updateUIForProcessing(false);
                                    updateResponseUI(response);
                                    updateStageIndicator("Quick Response");
                                    break;

                                case STAGE_2_ENHANCED:
                                    // Enhanced response - update UI with better response
                                    updateResponseUI(response);
                                    updateStageIndicator("Enhanced Analysis");

                                    // 🎭 FIXED: Interrupt current TTS and speak enhanced response with master voice
                                    if (ttsService != null) {
                                        ttsService.interrupt();
                                    }
                                    String selectedMaster = getSelectedChessMaster();
                                    Log.d(TAG, "🎭 Speaking Stage 2 response with " + selectedMaster + "'s voice");
                                    TTSServiceManager.speakWithSpecificMaster(SimpleRecordService.this, selectedMaster, response,
                                        new OpenAITTSService.OnSpeechCompletedListener() {
                                            @Override
                                            public void onSpeechCompleted() {
                                                Log.d(TAG, "🎵 Stage 2 speech completed with " + selectedMaster + "'s accent");
                                            }
                                        });
                                    break;

                                case STAGE_3_DEEP:
                                    // Deep analysis - final response
                                    updateResponseUI(response);
                                    updateStageIndicator("Master Insights");

                                    // 🎭 FIXED: Use the final response for TTS if it's significantly different with master voice
                                    if (isFinal && isSignificantlyDifferent(latestResponse, response)) {
                                        if (ttsService != null) {
                                            ttsService.interrupt();
                                        }
                                        selectedMaster = getSelectedChessMaster();
                                        Log.d(TAG, "🎭 Speaking Stage 3 final response with " + selectedMaster + "'s voice");
                                        TTSServiceManager.speakWithSpecificMaster(SimpleRecordService.this, selectedMaster, response,
                                            new OpenAITTSService.OnSpeechCompletedListener() {
                                                @Override
                                                public void onSpeechCompleted() {
                                                    Log.d(TAG, "🎵 Final stage speech completed with " + selectedMaster + "'s accent");
                                                }
                                            });
                                    }
                                    break;
                            }

                            // Notify callback
                            ServiceCallback cb = getCallback();
                            if (cb != null) {
                                cb.onResponseReceived(response);
                            }
                        }

                        @Override
                        public void onStageError(ThreeStageResponseManager.ResponseStage stage, String error) {
                            Log.e(TAG, "❌ " + stage.getDisplayName() + " error: " + error);

                            // Don't show errors for later stages if we already have a response
                            if (stage == ThreeStageResponseManager.ResponseStage.STAGE_1_QUICK || latestResponse.isEmpty()) {
                                String selectedMaster = getSelectedChessMaster();
                                String errorMsg = getPersonalityErrorMessage(selectedMaster);
                                updateResponseUI(errorMsg);
                                // 🎭 FIXED: Use master-specific voice for error messages
                                Log.d(TAG, "🎭 Speaking error message with " + selectedMaster + "'s voice");
                                TTSServiceManager.speakWithSpecificMaster(SimpleRecordService.this, selectedMaster, errorMsg, null);
                            }
                        }

                        @Override
                        public void onAllStagesComplete(String finalResponse) {
                            Log.d(TAG, "🎉 All 3 stages completed successfully!");

                            // Save conversation
                            String selectedMaster = getSelectedChessMaster();
                            String enrichedResponse = FineTunedModelManager.getInstance(SimpleRecordService.this)
                                    .enrichResponseWithPersonality(finalResponse, selectedMaster);

                            // Save to conversation manager
                            conversationManager.addMessage("user", transcribedText);
                            conversationManager.addMessage("assistant", enrichedResponse);
                            conversationManager.saveCurrentConversation();

                            // Clear stage indicator
                            updateStageIndicator("");
                            
                            // ANTI-DUPLICATE: Reset processing flag
                            isProcessingInput = false;
                            Log.d(TAG, "🔓 SimpleRecordService processing flag reset after completion");

                            // Final callback
                            ServiceCallback callback = getCallback();
                            if (callback != null) {
                                callback.onResponseCompleted(finalResponse);
                            }
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in 3-stage system", e);
            
            // ANTI-DUPLICATE: Reset processing flag on error
            isProcessingInput = false;
            Log.d(TAG, "🔓 SimpleRecordService processing flag reset due to error");
            
            mainHandler.post(() -> {
                updateUIForProcessing(false);
                String selectedMaster = getSelectedChessMaster();
                String errorMsg = getPersonalityErrorMessage(selectedMaster);
                // 🎭 FIXED: Use master-specific voice for error messages
                Log.d(TAG, "🎭 Speaking fallback error message with " + selectedMaster + "'s voice");
                TTSServiceManager.speakWithSpecificMaster(SimpleRecordService.this, selectedMaster, errorMsg, null);
                updateResponseUI(errorMsg);
            });
        }
    }

    /**
     * NEW: Update stage indicator in UI
     */
    private void updateStageIndicator(String stageText) {
        // You can implement this to show which stage is currently active
        // For now, we'll use the existing status text
        ServiceCallback callback = getCallback();
        if (callback != null) {
            mainHandler.post(() -> {
                // This could be enhanced to show stage-specific UI
                Log.d(TAG, "📍 Stage indicator: " + stageText);
            });
        }
    }

    /**
     * NEW: Check if responses are significantly different
     */
    private boolean isSignificantlyDifferent(String response1, String response2) {
        if (response1 == null || response2 == null) return true;
        if (response1.equals(response2)) return false;

        // Simple check - if the new response is significantly longer or has different key words
        return Math.abs(response1.length() - response2.length()) > 100 ||
                !response1.toLowerCase().contains(response2.toLowerCase().substring(0, Math.min(50, response2.length())));
    }

    /**
     * ENHANCED: Check if question is relevant to chess coaching
     */
    private boolean isRelevantChessQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            return false;
        }

        String lowerQuestion = question.toLowerCase().trim();

        // Always allow chess-related questions
        String[] chessKeywords = {
            "chess", "position", "move", "piece", "board", "game", "tactic", "strategy",
            "opening", "endgame", "middlegame", "checkmate", "check", "castle", "capture",
            "pawn", "rook", "knight", "bishop", "queen", "king", 
            "master", "grandmaster", "tournament", "rating", "elo",
            "tal", "fischer", "kasparov", "carlsen", "kramnik", "karpov", 
            "alekhine", "capablanca", "morphy", "lasker", "anand", "botvinnik"
        };

        for (String keyword : chessKeywords) {
            if (lowerQuestion.contains(keyword)) {
                return true;
            }
        }

        // Filter out clearly non-chess questions
        String[] nonChessKeywords = {
            "weather", "restaurant", "movie", "music", "sports", "politics",
            "cooking", "recipe", "shopping", "travel", "news", "celebrity",
            "health", "medicine", "programming", "code", "mathematics",
            "science", "physics", "chemistry", "biology"
        };

        for (String keyword : nonChessKeywords) {
            if (lowerQuestion.contains(keyword)) {
                return false;
            }
        }

        // If unclear, allow it (to avoid filtering too aggressively)
        return true;
    }

    /**
     * LEGACY: Check if the user's question is about the current chess position
     */
    private boolean checkIfPositionRelated(String question) {
        if (question == null) return false;

        String lowerQuestion = question.toLowerCase();

        // CRITICAL FIX: Check position-specific keywords FIRST (they have priority)
        String[] positionKeywords = {
                "this position", "current position", "this move", "what should i play",
                "best move", "analyze", "evaluation", "this board", "here",
                "what do you think of", "how about", "should i take", "can i play",
                "is it good to", "what if i", "better in this position", "who is better",
                "advantage", "who has", "position evaluation", "better here", "winning",
                "losing", "equal position", "assessment"
        };

        // PRIORITY: Position context always takes precedence
        for (String keyword : positionKeywords) {
            if (lowerQuestion.contains(keyword)) {
                Log.d(TAG, "🎯 Position-related question detected: '" + keyword + "' in '" + question + "'");
                return true;
            }
        }

        // Keywords that indicate general chess questions (only checked if no position context)
        String[] generalKeywords = {
                "tell me about", "who won", "championship", "history", "explain",
                "what is", "how to", "rules", "opening theory", "endgame theory",
                "famous game", "chess master", "biography", "when did", "where was",
                "who was the", "greatest player", "world champion"
        };

        // Only check general keywords if no position context was found
        for (String keyword : generalKeywords) {
            if (lowerQuestion.contains(keyword)) {
                Log.d(TAG, "💭 General chess question detected: '" + keyword + "' in '" + question + "'");
                return false;
            }
        }

        // IMPROVED: Default to position-related for ambiguous questions in competitive mode
        Log.d(TAG, "🤔 Ambiguous question, defaulting to position-related: '" + question + "'");
        return true;
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
                    .header("Authorization", "Bearer " + ApiKeys.getGroqKey())
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
                        return null; // Return null instead of error message
                    }
                } else {
                    Log.e(TAG, "Groq API error: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Error details: " + response.body().string());
                    }
                    return null; // Return null instead of error message
                }
            } // Response is automatically closed here by try-with-resources

        } catch (Exception e) {
            Log.e(TAG, "Error in Groq transcription", e);
            return null; // Return null instead of error message
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
     * Resets the current conversation and starts a new one
     */
    public void resetConversation() {
        OpenAITTSService tts = TTSServiceManager.getOpenAITTSService(this);
        if (tts != null) {
            tts.interrupt();
        }

        // NEW: Interrupt current 3-stage processing
        if (threeStageManager != null) {
            threeStageManager.interruptCurrentResponse();
        }
        
        // NEW: Clear voice session for fresh start
        currentVoiceSessionId = null;

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
        if (TTSServiceManager.getOpenAITTSService(this) != null) {
            TTSServiceManager.getOpenAITTSService(this).interrupt();
            Log.d(TAG, "Speech interrupted by user tap");
        }

        // NEW: Also interrupt 3-stage processing
        if (threeStageManager != null) {
            threeStageManager.interruptCurrentResponse();
        }
        
        // ANTI-DUPLICATE: Reset processing flag on interrupt
        isProcessingInput = false;
        Log.d(TAG, "🔓 SimpleRecordService processing flag reset due to speech interrupt");
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
        ServiceCallback callback = getCallback();
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
        ServiceCallback callback = getCallback();
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
     * Clean up recording resources
     */
    private void cleanup() {
        // Clean up output file
        if (outputFile != null) {
            if (outputFile.exists() && !outputFile.delete()) {
                Log.w(TAG, "Failed to delete output file: " + outputFile.getName());
            }
            outputFile = null;
        }
        
        // Clean up recorder
        if (recorder != null) {
            try {
                if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    recorder.stop();
                }
                recorder.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing recorder in cleanup", e);
            } finally {
                recorder = null;
            }
        }
        
        // Reset recording state
        isRecording = false;
    }

    /**
     * Gets the selected chess coach profile - FIXED to use correct SharedPreferences
     */
    private String getSelectedChessMaster() {
        // CRITICAL: Use the same SharedPreferences that FineTunedModelManager uses
        SharedPreferences prefs = getSharedPreferences("ChessFineTunedModels", MODE_PRIVATE);
        String master = prefs.getString("selected_master", "tal");
        
        Log.d(TAG, "🔍 SimpleRecordService getting master from SharedPreferences: " + master);
        return master;
    }

    private String getApiKeyFromPreferences() {
        String apiKey = ApiKeyConfig.getApiKey(this);

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ API Key is empty or null! Check your ApiKeyConfig class.");
            SharedPreferences prefs = getSharedPreferences("api_prefs", MODE_PRIVATE);
            apiKey = prefs.getString("openai_api_key", null);
            if (apiKey != null) {
                Log.d(TAG, "Fallback API key retrieved, length: " + apiKey.length());
            } else {
                Log.w(TAG, "No API key found in SharedPreferences either");
            }
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
        Log.d(TAG, "📋 SimpleRecordService setCallback called with: " + (callback != null ? "VALID CALLBACK" : "NULL"));
        this.callback = callback;
        Log.d(TAG, "✅ Callback stored as strong reference");
    }
    
    /**
     * Get the callback
     */
    private ServiceCallback getCallback() {
        return callback;
    }

    /**
     * Callback interface for service events
     */
    public interface ServiceCallback {
        void onRecordingStarted();
        void onRecordingStopped();
        void onProcessingStateChanged(boolean isProcessing);
        void onTranscriptionReceived(String transcribedText);
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