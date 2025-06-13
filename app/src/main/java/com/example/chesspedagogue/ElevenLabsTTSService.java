package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ElevenLabs TTS Service with ultra-low latency and emotional voice synthesis
 * Features:
 * - Ultra-low latency Flash v2.5 model (~75ms)
 * - Context-aware emotional adaptation
 * - Natural accent support for 12 chess masters
 * - Voice cloning capabilities
 */
public class ElevenLabsTTSService {
    private static final String TAG = "ElevenLabsTTSService";
    
    // ElevenLabs API endpoints
    private static final String TTS_URL = "https://api.elevenlabs.io/v1/text-to-speech/";
    private static final String VOICES_URL = "https://api.elevenlabs.io/v1/voices";
    
    // Model IDs for different use cases with latency info
    public static final String MODEL_FLASH = "eleven_flash_v2_5"; // Ultra-low latency (~75ms) - best for real-time
    public static final String MODEL_TURBO = "eleven_turbo_v2_5"; // Enhanced quality (~100-150ms) - recommended default
    //public static final String MODEL_MULTILINGUAL = "eleven_multilingual_v2"; // Highest quality (~200-300ms) - best for non-interactive
    
    // Voice IDs for chess masters - using carefully selected voices from ElevenLabs
    private static final Map<String, String> MASTER_VOICE_IDS = new HashMap<>();
    static {
        // FIXED: Consistent ElevenLabs voice IDs - no more built-in names that cause volume/quality issues
        MASTER_VOICE_IDS.put("tal", "WczBIOau2qV9z7nLeDqq"); // Tal - passionate and expressive
        MASTER_VOICE_IDS.put("fischer", "KLjqUZMleyr58nTJqW99"); // Fischer - intense and precise
        MASTER_VOICE_IDS.put("kasparov", "rT6zdbVnOt0GO9v5OiWr"); // Kasparov - dynamic and energetic
        MASTER_VOICE_IDS.put("carlsen", "9pRpxWU0T7UFt2oEMH6n"); // Carlsen - calm and modern
        
        // FIXED: Convert built-in names to proper ElevenLabs voice IDs for consistent quality
        MASTER_VOICE_IDS.put("karpov", "pNInz6obpgDQGcFmaJgB"); // Adam - refined, measured (was "Charli")
        MASTER_VOICE_IDS.put("kramnik", "ErXwobaYiN019PkySvjV"); // Antoni - analytical, precise
        MASTER_VOICE_IDS.put("capablanca", "VR6AewLTigWG4xSOukaG"); // Arnold - elegant, smooth
        MASTER_VOICE_IDS.put("alekhine", "3EuKHIEZbSzrHGNmdYsx"); // Ivan - Russian, calm
        MASTER_VOICE_IDS.put("morphy", "VR6AewLTigWG4xSOukaG"); // Arnold - gentlemanly American
        MASTER_VOICE_IDS.put("lasker", "pNInz6obpgDQGcFmaJgB"); // Adam - wise, philosophical  
        MASTER_VOICE_IDS.put("anand", "Mgih2jslgx7pUv85yYYU"); // Maksud - friendly, optimistic
        MASTER_VOICE_IDS.put("botvinnik", "JBFqnCBsd6RMkjVDRZzb"); // George - methodical British (was "Harry")
    }

    private static ElevenLabsTTSService instance;
    
    private final OkHttpClient httpClient;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private String apiKey;
    private MediaPlayer currentPlayer;
    private boolean interruptRequested = false;
    private boolean isSpeaking = false;
    private SharedPreferences prefs;
    
    // Usage context for dynamic model selection
    private String currentContext = "default";
    
    // 🎭 Emotional intelligence integration
    private EmotionalIntelligenceManager.EmotionalAnalysisResult currentEmotionalState;
    
    // 🎤 PHASE 1: Voice-Emotion Feedback Integration
    private VoiceEmotionalAnalyzer voiceEmotionalAnalyzer;
    private String currentSpeakingMaster = null;
    private final List<String> currentListeningMasters = new ArrayList<>();
    
    // Chunk management
    private final ConcurrentLinkedQueue<ChunkPlaybackItem> chunkQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isPlayingChunks = new AtomicBoolean(false);
    private final AtomicInteger chunkIdCounter = new AtomicInteger(0);
    private final Map<Integer, ChunkPlaybackItem> pendingChunks = new HashMap<>();
    private final AtomicInteger nextChunkToPlay = new AtomicInteger(0);
    
    // Track active players for cleanup
    private final Map<Integer, MediaPlayer> activePlayers = new HashMap<>();
    
    // Queue for pending speech requests
    private final Queue<PendingSpeech> pendingSpeechQueue = new ConcurrentLinkedQueue<>();
    
    // Recent speech cache for duplicate detection
    private final Map<String, Long> recentSpeechCache = new HashMap<>();
    private static final long SPEECH_DUPLICATE_WINDOW = 30000; // 30 seconds
    
    // Inner class for queued speech
    private static class PendingSpeech {
        final String text;
        final OnSpeechCompletedListener listener;
        final String masterName; // Added to preserve master context
        final SpeechCallback speechCallback; // Added to preserve callback context
        
        PendingSpeech(String text, OnSpeechCompletedListener listener) {
            this.text = text;
            this.listener = listener;
            this.masterName = null; // Generic speech
            this.speechCallback = null;
        }
        
        PendingSpeech(String text, OnSpeechCompletedListener listener, String masterName, SpeechCallback speechCallback) {
            this.text = text;
            this.listener = listener;
            this.masterName = masterName;
            this.speechCallback = speechCallback;
        }
    }
    
    private SpeechCallback speechCallback;
    
    // Inner class for chunk management
    private static class ChunkPlaybackItem {
        final int chunkId;
        final File audioFile;
        final String text;
        final TTSCallback callback;
        final boolean isFinalChunk;
        
        ChunkPlaybackItem(int chunkId, File audioFile, String text, TTSCallback callback, boolean isFinalChunk) {
            this.chunkId = chunkId;
            this.audioFile = audioFile;
            this.text = text;
            this.callback = callback;
            this.isFinalChunk = isFinalChunk;
        }
    }
    
    public ElevenLabsTTSService(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
        
        // 🎤 Initialize Voice-Emotion Feedback System
        this.voiceEmotionalAnalyzer = VoiceEmotionalAnalyzer.getInstance(context);
        
        // FIXED: Get API key from preferences only - removed hardcoded fallback
        // Note: System.getenv() doesn't work on Android - use SharedPreferences instead
        this.apiKey = prefs.getString("elevenlabs_api_key", "");
        
        // Warn if no API key is set
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            Log.w(TAG, "⚠️ No ElevenLabs API key found in preferences - TTS may not work properly");
        } else {
            Log.d(TAG, "✅ ElevenLabs API key loaded from preferences");
        }
    }
    
    public static synchronized ElevenLabsTTSService getInstance(Context context) {
        if (instance == null) {
            instance = new ElevenLabsTTSService(context);
        }
        return instance;
    }
    
    // Simple speech callback interface
    public interface SpeechCallback {
        void onSpeechCompleted(String text);
        void onSpeechInterrupted();
    }
    
    public interface OnSpeechCompletedListener {
        void onSpeechCompleted();
    }
    
    public void setSpeechCallback(SpeechCallback callback) {
        this.speechCallback = callback;
    }
    
    /**
     * Set usage context for dynamic model selection
     */
    public void setUsageContext(String context) {
        this.currentContext = context;
        // Context updated
    }
    
    /**
     * 🎭 NEW: Set emotional state for voice modulation
     */
    public void setEmotionalState(EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) {
        this.currentEmotionalState = emotionalState;
        if (emotionalState != null) {
            // Emotional state updated
        }
    }
    
    /**
     * Main speak method with ElevenLabs API
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        // Process TTS request
        
        if (text == null || text.isEmpty()) {
            if (listener != null) {
                listener.onSpeechCompleted();
            }
            return;
        }
        
        // Don't clean up if currently speaking - queue instead
        if (isSpeaking && !interruptRequested) {
            // Check for duplicate speech in queue to prevent repetition
            if (isDuplicateSpeech(text)) {
                Log.w(TAG, "🚫 Duplicate speech detected, skipping: " + text.substring(0, Math.min(50, text.length())) + "...");
                if (listener != null) {
                    listener.onSpeechCompleted(); // Complete immediately for duplicates
                }
                return;
            }
            
            Log.d(TAG, "⚠️ Speech in progress - queueing for later playback");
            pendingSpeechQueue.add(new PendingSpeech(text, listener));
            return;
        }
        
        // Only clean up if we're not currently speaking
        cleanupPreviousSession();
        
        isSpeaking = true;
        interruptRequested = false;
        
        // Reset chunk counter for this speech session
        chunkIdCounter.set(0);
        nextChunkToPlay.set(0);
        chunkQueue.clear();
        pendingChunks.clear();
        
        // Use a single TTSCallback that manages ordering
        TTSCallback orderingCallback = new TTSCallback() {
            @Override
            public void onSpeechStarted() {
                // Speech started
                
                // 🎤 Process voice emotional feedback when speech starts
                processVoiceEmotionalFeedback(text);
            }
            
            @Override
            public void onSpeechReady(File audioFile) {
                // Audio ready
            }
            
            @Override
            public void onSpeechCompleted() {
                // All chunks completed
                mainHandler.post(() -> {
                    cleanupCurrentSession();
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                    // Process any pending speech from the queue
                    processPendingSpeechQueue();
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ ElevenLabs TTS Error: " + errorMessage);
                mainHandler.post(() -> {
                    cleanupCurrentSession();
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                    // Process any pending speech from the queue
                    processPendingSpeechQueue();
                });
            }
        };
        
        // Format text with TTS controls based on context
        String tempFormattedText = text;
        try {
            // CRITICAL FIX: Don't use global master preference, let speakWithSpecificMaster handle it
            String currentMaster = "default"; // Will be overridden by speakWithSpecificMaster if needed
            boolean isEmotional = currentContext != null && 
                (currentContext.contains("BRILLIANT") || 
                 currentContext.contains("BLUNDER") || 
                 currentContext.contains("SWING"));
            
            tempFormattedText = ElevenLabsTTSFormatter.formatForTTS(text, currentMaster, isEmotional);
        } catch (Exception e) {
            Log.w(TAG, "Error formatting text for TTS, using original", e);
        }
        final String formattedText = tempFormattedText;
        
        // Generate a single chunk for the formatted text
        generateTTSChunk(formattedText, 0, true, orderingCallback);
    }
    
    /**
     * Process any pending speech from the queue
     */
    private void processPendingSpeechQueue() {
        if (!pendingSpeechQueue.isEmpty()) {
            PendingSpeech pending = pendingSpeechQueue.poll();
            if (pending != null) {
                Log.d(TAG, "📢 Processing queued speech: " + pending.text.substring(0, Math.min(50, pending.text.length())) + "...");
                
                // Use specific master method if master is specified, otherwise use generic
                if (pending.masterName != null && pending.speechCallback != null) {
                    Log.d(TAG, "🎭 Processing queued speech for specific master: " + pending.masterName);
                    speakWithSpecificMaster(pending.masterName, pending.text, pending.speechCallback);
                } else {
                    Log.d(TAG, "🔊 Processing queued speech with generic method");
                    speak(pending.text, pending.listener);
                }
            }
        }
    }
    
    /**
     * 🎭 FIXED: Speak with specific master voice to prevent race conditions
     * This bypasses the global selected_master preference
     */
    public void speakWithSpecificMaster(String masterName, String text, SpeechCallback callback) {
        Log.d(TAG, "🎭 Speaking with specific master: " + masterName + " (bypassing global preference)");
        
        if (text == null || text.isEmpty()) {
            if (callback != null) {
                callback.onSpeechCompleted(text);
            }
            return;
        }
        
        // Store original callback for restoring later
        SpeechCallback originalCallback = this.speechCallback;
        
        // Temporarily set the callback for this specific speech
        this.speechCallback = callback;
        
        // Get voice ID directly for the specified master
        String voiceId = getVoiceIdForMaster(masterName);
        Log.d(TAG, "🎭 Using voice ID for " + masterName + ": " + voiceId);
        
        // Format text with TTS controls based on the SPECIFIC master (not global preference)
        String tempText = text;
        try {
            boolean isEmotional = currentContext != null && 
                (currentContext.contains("BRILLIANT") || 
                 currentContext.contains("BLUNDER") || 
                 currentContext.contains("SWING"));
            
            tempText = ElevenLabsTTSFormatter.formatForTTS(text, masterName, isEmotional);
            Log.d(TAG, "📝 Formatted text for TTS (specific master " + masterName + "): " + tempText.substring(0, Math.min(100, tempText.length())) + "...");
        } catch (Exception e) {
            Log.w(TAG, "Error formatting text for TTS, using original", e);
        }
        final String formattedText = tempText;
        
        // Don't clean up if currently speaking - queue instead
        if (isSpeaking && !interruptRequested) {
            // Check for duplicate speech in queue to prevent repetition
            if (isDuplicateSpeech(text)) {
                Log.w(TAG, "🚫 Duplicate speech detected for " + masterName + ", skipping: " + text.substring(0, Math.min(50, text.length())) + "...");
                if (callback != null) {
                    callback.onSpeechCompleted(text); // Complete immediately for duplicates
                }
                return;
            }
            
            Log.d(TAG, "⚠️ Speech in progress - queueing specific master speech for later playback");
            pendingSpeechQueue.add(new PendingSpeech(text, new OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    if (callback != null) {
                        callback.onSpeechCompleted(text);
                    }
                }
                
                public void onSpeechInterrupted() {
                    if (callback != null) {
                        callback.onSpeechInterrupted();
                    }
                }
            }, masterName, callback)); // FIXED: Include master name and callback to preserve context
            return;
        }
        
        // Only clean up if we're not currently speaking
        cleanupPreviousSession();
        
        isSpeaking = true;
        interruptRequested = false;
        
        // Reset chunk counter for this speech session
        chunkIdCounter.set(0);
        nextChunkToPlay.set(0);
        chunkQueue.clear();
        pendingChunks.clear();
        
        // Use a special TTSCallback that restores the original callback when done
        TTSCallback masterSpecificCallback = new TTSCallback() {
            @Override
            public void onSpeechStarted() {
                // Speech started for specific master
                
                // 🎤 Process voice emotional feedback when speech starts
                processVoiceEmotionalFeedback(formattedText);
            }
            
            @Override
            public void onSpeechReady(File audioFile) {
                // Audio ready for specific master
            }
            
            @Override
            public void onSpeechCompleted() {
                // Speech completed for master
                mainHandler.post(() -> {
                    cleanupCurrentSession();
                    isSpeaking = false;
                    
                    // Restore original callback
                    speechCallback = originalCallback;
                    
                    if (callback != null) {
                        callback.onSpeechCompleted(text);
                    }
                    // Process any pending speech from the queue
                    processPendingSpeechQueue();
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Speech error for " + masterName + ": " + error);
                mainHandler.post(() -> {
                    cleanupCurrentSession();
                    isSpeaking = false;
                    
                    // Restore original callback
                    speechCallback = originalCallback;
                    
                    if (callback != null) {
                        callback.onSpeechInterrupted();
                    }
                    // Process any pending speech from the queue
                    processPendingSpeechQueue();
                });
            }
        };
        
        // Generate TTS with the specific master's voice
        generateTTSChunkWithSpecificVoice(formattedText, 0, true, masterSpecificCallback, voiceId, masterName);
    }
    
    /**
     * Clean up any previous session's resources
     */
    private void cleanupPreviousSession() {
        try {
            Log.d(TAG, "🧹 Cleaning up previous session...");
            
            // Stop any active playback
            if (currentPlayer != null) {
                try {
                    if (currentPlayer.isPlaying()) {
                        currentPlayer.stop();
                    }
                    currentPlayer.release();
                    currentPlayer = null;
                } catch (Exception e) {
                    Log.w(TAG, "Error cleaning up current player", e);
                }
            }
            
            // Clean up all active players
            synchronized (activePlayers) {
                for (MediaPlayer player : activePlayers.values()) {
                    try {
                        if (player.isPlaying()) {
                            player.stop();
                        }
                        player.release();
                    } catch (Exception e) {
                        Log.w(TAG, "Error releasing active player", e);
                    }
                }
                activePlayers.clear();
            }
            
            // Clear all pending work
            chunkIdCounter.set(0);
            nextChunkToPlay.set(0);
            chunkQueue.clear();
            pendingChunks.clear();
            isPlayingChunks.set(false);
            
            Log.d(TAG, "✅ Previous session cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Error during session cleanup", e);
        }
    }
    
    /**
     * Clean up current session's resources
     */
    private void cleanupCurrentSession() {
        try {
            // Clean up temporary audio files
            File cacheDir = new File(context.getCacheDir(), "tts_cache");
            if (cacheDir.exists()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().startsWith("elevenlabs_") &&
                                System.currentTimeMillis() - file.lastModified() > 60000) { // Older than 1 minute
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error cleaning up audio files", e);
        }
    }
    
    /**
     * Generate TTS chunk using ElevenLabs API
     */
    private void generateTTSChunk(String text, int chunkId, boolean isFinalChunk, TTSCallback callback) {
        executorService.execute(() -> {
            try {
                // CRITICAL FIX: Check if we're in spectator mode to avoid wrong master selection
                android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
                boolean isSpectatorMode = prefs.getBoolean("is_spectator_mode", false);
                
                String selectedMaster;
                if (isSpectatorMode) {
                    // In spectator mode, use a default or let specific methods handle master selection
                    selectedMaster = "tal"; // Default fallback - should be overridden by speakWithSpecificMaster
                    Log.w(TAG, "⚠️ generateTTSChunk called in spectator mode - using fallback master: " + selectedMaster);
                } else {
                    selectedMaster = getCurrentChessMaster();
                }
                
                String voiceId = getVoiceIdForMaster(selectedMaster);
                String model = getModelForContext(currentContext);
                
                // Generate TTS request
                
                // Build the ElevenLabs request
                JSONObject payload = new JSONObject();
                payload.put("text", text);
                payload.put("model_id", model);
                
                // 🎭 ENHANCED: Voice settings with emotional intelligence
                JSONObject voiceSettings = getEmotionallyAwareVoiceSettings(selectedMaster);
                
                payload.put("voice_settings", voiceSettings);
                
                // 🔧 ENHANCED DEBUGGING: Log voice configuration details
                Log.d(TAG, String.format("🎤 TTS Request Details:\n" +
                    "  Master: %s\n" +
                    "  Voice ID: %s\n" +
                    "  Model: %s\n" +
                    "  Stability: %.2f\n" +
                    "  Similarity: %.2f\n" +
                    "  Text length: %d chars", 
                    selectedMaster, voiceId, model,
                    voiceSettings.getDouble("stability"),
                    voiceSettings.getDouble("similarity_boost"),
                    text.length()));
                
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );
                
                String url = TTS_URL + voiceId;
                Request request = new Request.Builder()
                        .url(url)
                        .header("xi-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .header("Accept", "audio/mpeg")
                        .post(body)
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    
                    if (!response.isSuccessful()) {
                        String error = "ElevenLabs API error: " + response.code();
                        String errorBody = "";
                        if (response.body() != null) {
                            errorBody = response.body().string();
                            Log.e(TAG, "❌ ElevenLabs Error Details: " + errorBody);
                        }
                        
                        final String fullError = error + " - " + errorBody;
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError(fullError));
                        }
                        return;
                    }
                    
                    if (response.body() == null) {
                        Log.e(TAG, "❌ ElevenLabs returned null body");
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError("Empty response from ElevenLabs"));
                        }
                        return;
                    }
                    
                    byte[] audioData = response.body().bytes();
                    File audioFile = saveAudioToFile(audioData);
                    
                    // TTS generation completed
                    
                    // Create chunk item and add to pending
                    ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(
                            chunkId, audioFile, text, callback, isFinalChunk);
                    
                    synchronized (pendingChunks) {
                        if (chunkId == 0) {
                            nextChunkToPlay.set(0);
                        }
                        
                        pendingChunks.put(chunkId, chunkItem);
                        
                        tryPlayNextChunks();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Critical ElevenLabs TTS error", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("ElevenLabs TTS error: " + e.getMessage()));
                }
            }
        });
    }
    
    /**
     * 🎭 FIXED: Generate TTS chunk with specific voice to prevent race conditions
     */
    private void generateTTSChunkWithSpecificVoice(String text, int chunkId, boolean isFinalChunk, TTSCallback callback, String voiceId, String masterName) {
        executorService.execute(() -> {
            try {
                String model = getModelForContext(currentContext);
                
                // Generate TTS with specific voice
                
                // Build the ElevenLabs request
                JSONObject payload = new JSONObject();
                payload.put("text", text);
                payload.put("model_id", model);
                
                // 🎭 ENHANCED: Voice settings with emotional intelligence for specific master
                JSONObject voiceSettings = getEmotionallyAwareVoiceSettings(masterName);
                
                payload.put("voice_settings", voiceSettings);
                
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );
                
                String url = TTS_URL + voiceId;
                Request request = new Request.Builder()
                        .url(url)
                        .header("xi-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .header("Accept", "audio/mpeg")
                        .post(body)
                        .build();
                
                Response response = httpClient.newCall(request).execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    // Save audio to cache
                    File cacheDir = new File(context.getCacheDir(), "tts_cache");
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    
                    String audioFileName = "elevenlabs_" + UUID.randomUUID().toString() + ".mp3";
                    File audioFile = new File(cacheDir, audioFileName);
                    
                    byte[] audioData = response.body().bytes();
                    FileOutputStream fos = new FileOutputStream(audioFile);
                    fos.write(audioData);
                    fos.close();
                    
                    // TTS generation completed
                    
                    // Queue for playback
                    ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(chunkId, audioFile, text, callback, isFinalChunk);
                    
                    synchronized (pendingChunks) {
                        if (chunkId == 0) {
                            nextChunkToPlay.set(0);
                        }
                        
                        pendingChunks.put(chunkId, chunkItem);
                        
                        tryPlayNextChunks();
                    }
                    
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "❌ ElevenLabs API Error: " + response.code() + " - " + errorBody);
                    if (callback != null) {
                        mainHandler.post(() -> callback.onError("ElevenLabs API error: " + response.code()));
                    }
                }
                
                response.close();
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error generating ElevenLabs TTS for " + masterName, e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("ElevenLabs TTS error: " + e.getMessage()));
                }
            }
        });
    }
    
    /**
     * Get ElevenLabs model based on usage context
     */
    private String getModelForContext(String context) {
        switch (context.toLowerCase()) {
            case "main_game":
            case "main":
            case "game":
                return MODEL_FLASH; // eleven_flash_v2_5 for main game screen
                
            case "spectator_mode":
            case "spectator":
            case "ai_dialogue":
            case "dialogue":
                return MODEL_TURBO; // eleven_turbo_v2_5 for spectator mode
                
            case "analysis":
                return MODEL_TURBO; // eleven_turbo_v2_5 for analysis
                
            default:
                return MODEL_TURBO; // Default to turbo for backwards compatibility
        }
    }
    
    /**
     * Get voice ID for chess master
     */
    private String getVoiceIdForMaster(String master) {
        String voiceId = MASTER_VOICE_IDS.get(master.toLowerCase());
        if (voiceId == null) {
            // Default to Adam if master not found
            voiceId = "pNInz6obpgDQGcFmaJgB"; // Adam - versatile male voice
        }
        return voiceId;
    }
    
    /**
     * Get stability setting for master (0.0 - 1.0)
     * Lower = more expressive, Higher = more consistent
     * FIXED: Optimized for better volume and consistency
     */
    private double getStabilityForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return 0.45; // FIXED: Slightly more stable for better audio quality
            case "fischer":
                return 0.60; // FIXED: Reduced for better expressiveness 
            case "kasparov":
                return 0.50; // FIXED: More balanced for consistent volume
            case "carlsen":
                return 0.55; // FIXED: Slightly increased for clarity
            case "karpov":
                return 0.55; // FIXED: Optimal balance
            case "kramnik":
                return 0.55; // FIXED: Consistent with others
            case "alekhine":
                return 0.50; // FIXED: Added explicit setting for Alekhine
            case "capablanca":
                return 0.55; // FIXED: Elegant and clear
            case "anand":
                return 0.50; // FIXED: Friendly and accessible
            case "morphy":
                return 0.55; // FIXED: Gentlemanly and clear
            case "lasker":
                return 0.60; // FIXED: Wise and measured
            case "botvinnik":
                return 0.60; // FIXED: Methodical and precise
            default:
                return 0.55; // FIXED: Better default for all voices
        }
    }
    
    /**
     * Get similarity boost for master (0.0 - 1.0)
     * Higher = more similar to original voice
     * FIXED: Optimized settings for better voice quality and consistency
     */
    private double getSimilarityBoostForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
            case "fischer":
            case "kasparov":
            case "carlsen":
                return 0.80; // FIXED: Higher similarity for custom voices
            case "karpov":
            case "kramnik":
            case "capablanca":
            case "alekhine":
            case "anand":
            case "morphy":
            case "lasker":
            case "botvinnik":
                return 0.75; // FIXED: Standard similarity for built-in voices
            default:
                return 0.75; // Standard default
        }
    }
    
    /**
     * Get style setting for master (0.0 - 1.0)
     * ElevenLabs recommends keeping this at 0 for stability
     * Only use sparingly for special effects
     */
    private double getStyleForMaster(String master) {
        // As per ElevenLabs docs: "we recommend keeping this setting at 0 at all times"
        // Style exaggeration makes the model less stable and increases latency
        return 0.0;
    }
    
    /**
     * 🎭 NEW: Get emotionally-aware voice settings based on current emotional state
     */
    private JSONObject getEmotionallyAwareVoiceSettings(String master) throws Exception {
        JSONObject voiceSettings = new JSONObject();
        
        // Base settings for the master
        double baseStability = getStabilityForMaster(master);
        double baseSimilarity = getSimilarityBoostForMaster(master);
        double baseStyle = getStyleForMaster(master);
        
        // Apply emotional modulations if we have emotional state
        if (currentEmotionalState != null && currentEmotionalState.intensity > 0.2f) {
            // Modify voice settings based on emotional state
            EmotionalVoiceModulation modulation = getEmotionalVoiceModulation(currentEmotionalState);
            
            voiceSettings.put("stability", Math.max(0.0, Math.min(1.0, baseStability + modulation.stabilityAdjustment)));
            voiceSettings.put("similarity_boost", Math.max(0.0, Math.min(1.0, baseSimilarity + modulation.similarityAdjustment)));
            voiceSettings.put("style", Math.max(0.0, Math.min(1.0, baseStyle + modulation.styleAdjustment)));
            voiceSettings.put("use_speaker_boost", true);
            
            Log.d(TAG, String.format("🎭 Emotional voice settings for %s (%s): stability=%.2f, similarity=%.2f, style=%.2f", 
                  master, currentEmotionalState.emotion.name, 
                  voiceSettings.getDouble("stability"), 
                  voiceSettings.getDouble("similarity_boost"),
                  voiceSettings.getDouble("style")));
        } else {
            // Use default settings
            voiceSettings.put("stability", baseStability);
            voiceSettings.put("similarity_boost", baseSimilarity);
            voiceSettings.put("style", baseStyle);
            voiceSettings.put("use_speaker_boost", true);
            
            Log.d(TAG, String.format("🎤 Default voice settings for %s: stability=%.2f, similarity=%.2f", 
                  master, baseStability, baseSimilarity));
        }
        
        return voiceSettings;
    }
    
    /**
     * 🎭 Calculate voice modulation based on emotional state with master-specific enhancements
     */
    private EmotionalVoiceModulation getEmotionalVoiceModulation(EmotionalIntelligenceManager.EmotionalAnalysisResult emotional) {
        EmotionalVoiceModulation modulation = new EmotionalVoiceModulation();
        
        // Intensity-based base adjustments
        float intensityFactor = emotional.intensity;
        
        switch (emotional.emotion) {
            case ECSTATIC:
            case THRILLED:
                // Very excited - less stable, more expressive
                modulation.stabilityAdjustment = -0.2f * intensityFactor;
                modulation.styleAdjustment = 0.3f * intensityFactor; // More style for high excitement
                break;
                
            case EXCITED:
            case PLEASED:
                // Moderately excited - slightly less stable
                modulation.stabilityAdjustment = -0.1f * intensityFactor;
                modulation.styleAdjustment = 0.1f * intensityFactor;
                break;
                
            case DEVASTATED:
            case FRUSTRATED:
                // Negative emotions - less stable, more variation
                modulation.stabilityAdjustment = -0.15f * intensityFactor;
                modulation.styleAdjustment = 0.2f * intensityFactor;
                break;
                
            case CONCERNED:
            case UNEASY:
                // Worry - slight stability reduction
                modulation.stabilityAdjustment = -0.05f * intensityFactor;
                modulation.styleAdjustment = 0.05f * intensityFactor;
                break;
                
            case ANALYTICAL:
            case FOCUSED:
                // ENHANCED: Alekhine-specific analytical expressiveness
                // Analytical - more stable, but with subtle emotional undertones for Alekhine
                modulation.stabilityAdjustment = 0.05f * intensityFactor; // Less stable than before
                modulation.styleAdjustment = 0.1f * intensityFactor; // More expressive for Alekhine's passionate analysis
                break;
                
            case CONFIDENT:
                // Confidence - balanced with slight expressiveness
                modulation.stabilityAdjustment = 0.05f * intensityFactor;
                modulation.styleAdjustment = 0.05f * intensityFactor;
                break;
                
            default:
                // Neutral emotions - minimal adjustments
                modulation.stabilityAdjustment = 0.0f;
                modulation.styleAdjustment = 0.0f;
                break;
        }
        
        // Apply momentum factor - emotional streaks can amplify effects
        if (Math.abs(emotional.momentum) > 0.5f) {
            float momentumMultiplier = 1.0f + (Math.abs(emotional.momentum) * 0.3f);
            modulation.stabilityAdjustment *= momentumMultiplier;
            modulation.styleAdjustment *= momentumMultiplier;
        }
        
        // 🎭 ALEKHINE ENHANCEMENT: Master-specific emotional amplification
        // Apply master-specific emotional enhancement based on current selected master
        String currentMaster = getCurrentChessMaster();
        if ("alekhine".equals(currentMaster.toLowerCase())) {
            // Alekhine is passionate and intense - amplify all emotional expressions
            float alekhineFactor = 1.4f; // 40% more emotional expression
            modulation.stabilityAdjustment *= alekhineFactor;
            modulation.styleAdjustment *= alekhineFactor;
            
            // For analytical states, add more passion to Alekhine's voice
            if (emotional.emotion.name.equals("analytical") || emotional.emotion.name.equals("focused")) {
                modulation.stabilityAdjustment -= 0.1f; // Make analytical less stable (more passionate)
                modulation.styleAdjustment += 0.15f;    // Add artistic flair to analysis
            }
            
            Log.d(TAG, String.format("🏛️ ALEKHINE VOICE ENHANCEMENT: Applied passionate amplification (factor: %.1f)", alekhineFactor));
        }
        
        return modulation;
    }
    
    /**
     * 🎭 Emotional voice modulation parameters
     */
    private static class EmotionalVoiceModulation {
        float stabilityAdjustment = 0.0f;
        float similarityAdjustment = 0.0f;
        float styleAdjustment = 0.0f;
    }
    
    /**
     * Get current chess master from preferences
     */
    private String getCurrentChessMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        String master = masterPrefs.getString("selected_master", "tal");
        return master;
    }
    
    /**
     * Try to play the next chunks in order
     */
    private void tryPlayNextChunks() {
        synchronized (pendingChunks) {
            // Silent chunk processing for performance
            
            // Add any ready chunks to the queue
            while (pendingChunks.containsKey(nextChunkToPlay.get())) {
                ChunkPlaybackItem chunk = pendingChunks.remove(nextChunkToPlay.get());
                chunkQueue.offer(chunk);
                nextChunkToPlay.incrementAndGet();
            }
            
            // Start playback if not already playing
            if (!isPlayingChunks.get() && !chunkQueue.isEmpty()) {
                playNextChunk();
            }
        }
    }
    
    /**
     * Play the next chunk in the queue
     */
    private void playNextChunk() {
        if (interruptRequested) {
            cleanupPlayback();
            return;
        }
        
        ChunkPlaybackItem chunk = chunkQueue.poll();
        if (chunk == null) {
            isPlayingChunks.set(false);
            return;
        }
        
        isPlayingChunks.set(true);
        
        mainHandler.post(() -> {
            try {
                // Minimal chunk playback logging
                
                if (chunk.callback != null) {
                    chunk.callback.onSpeechReady(chunk.audioFile);
                }
                
                MediaPlayer player = new MediaPlayer();
                
                // Track this player for cleanup
                synchronized (activePlayers) {
                    activePlayers.put(chunk.chunkId, player);
                }
                
                // FIXED: Use media stream for proper volume control
                player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA) // Use media stream for volume control
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                );
                
                player.setOnPreparedListener(mp -> {
                    currentPlayer = mp;
                    mp.start();
                    if (chunk.callback != null) {
                        chunk.callback.onSpeechStarted();
                    }
                });
                
                player.setOnCompletionListener(mp -> {
                    // Chunk completed - minimal logging
                    
                    // Proper cleanup
                    try {
                        mp.release();
                        synchronized (activePlayers) {
                            activePlayers.remove(chunk.chunkId);
                        }
                        currentPlayer = null;
                    } catch (Exception e) {
                        Log.w(TAG, "Error during player cleanup", e);
                    }
                    
                    // Play next chunk or finish
                    if (chunk.isFinalChunk && chunkQueue.isEmpty()) {
                        isPlayingChunks.set(false);
                        isSpeaking = false;
                        if (chunk.callback != null) {
                            chunk.callback.onSpeechCompleted();
                        }
                    } else {
                        // Small delay between chunks for natural speech
                        mainHandler.postDelayed(() -> playNextChunk(), 50);
                    }
                });
                
                player.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "❌ MediaPlayer error for chunk " + chunk.chunkId + ": " + what + ", " + extra);
                    
                    // Proper error cleanup
                    try {
                        mp.release();
                        synchronized (activePlayers) {
                            activePlayers.remove(chunk.chunkId);
                        }
                        currentPlayer = null;
                    } catch (Exception e) {
                        Log.w(TAG, "Error during error cleanup", e);
                    }
                    
                    if (chunk.callback != null) {
                        chunk.callback.onError("Playback error: " + what);
                    }
                    
                    // Try to continue with next chunk
                    mainHandler.postDelayed(() -> playNextChunk(), 100);
                    return true;
                });
                
                player.setDataSource(context, Uri.fromFile(chunk.audioFile));
                player.prepareAsync();
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error playing chunk " + chunk.chunkId, e);
                if (chunk.callback != null) {
                    chunk.callback.onError("Playback error: " + e.getMessage());
                }
                
                // Try to continue with next chunk
                mainHandler.postDelayed(() -> playNextChunk(), 100);
            }
        });
    }
    
    /**
     * Save audio data to file
     */
    private File saveAudioToFile(byte[] audioData) throws IOException {
        File cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        
        String fileName = "elevenlabs_" + UUID.randomUUID().toString() + ".mp3";
        File audioFile = new File(cacheDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioData);
        }
        
        return audioFile;
    }
    
    /**
     * Check if currently speaking
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }
    
    /**
     * Stop/interrupt speech
     */
    public void stopSpeech() {
        interrupt();
    }
    
    /**
     * Interrupt current speech
     */
    public void interrupt() {
        Log.d(TAG, "🛑 Interrupting speech with full cleanup");
        interruptRequested = true;
        
        // Clear all pending chunks
        chunkQueue.clear();
        pendingChunks.clear();
        
        // Stop current playback
        if (currentPlayer != null) {
            try {
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop();
                }
            } catch (IllegalStateException e) {
                Log.w(TAG, "MediaPlayer in invalid state during interrupt, releasing");
            } finally {
                try {
                    currentPlayer.release();
                } catch (Exception e) {
                    Log.w(TAG, "Error releasing MediaPlayer during interrupt", e);
                }
                currentPlayer = null;
            }
        }
        
        // Stop all active players
        synchronized (activePlayers) {
            for (MediaPlayer player : activePlayers.values()) {
                try {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                } catch (IllegalStateException e) {
                    Log.w(TAG, "Active player in invalid state, skipping stop");
                } finally {
                    try {
                        player.release();
                    } catch (Exception e) {
                        Log.w(TAG, "Error releasing active player", e);
                    }
                }
            }
            activePlayers.clear();
        }
        
        isSpeaking = false;
        isPlayingChunks.set(false);
        
        if (speechCallback != null) {
            speechCallback.onSpeechInterrupted();
        }
        
        Log.d(TAG, "✅ Speech interruption completed safely");
    }
    
    /**
     * Cleanup playback resources
     */
    private void cleanupPlayback() {
        chunkQueue.clear();
        pendingChunks.clear();
        isPlayingChunks.set(false);
        isSpeaking = false;
        
        synchronized (activePlayers) {
            activePlayers.clear();
        }
    }
    
    // 🎤 ========== VOICE-EMOTION FEEDBACK INTEGRATION METHODS ==========
    
    /**
     * Set the current speaking master and listening masters for voice-emotion feedback
     */
    public void setVoiceEmotionalContext(String speakingMaster, List<String> listeningMasters) {
        this.currentSpeakingMaster = speakingMaster;
        this.currentListeningMasters.clear();
        if (listeningMasters != null) {
            this.currentListeningMasters.addAll(listeningMasters);
        }
        
        Log.d(TAG, String.format("🎭 Voice emotional context set: %s speaking to %d listeners", 
                                speakingMaster, this.currentListeningMasters.size()));
    }
    
    /**
     * Add a voice emotional callback to receive reactions
     */
    public void addVoiceEmotionalCallback(VoiceEmotionalAnalyzer.VoiceEmotionalCallback callback) {
        if (voiceEmotionalAnalyzer != null) {
            voiceEmotionalAnalyzer.addVoiceEmotionalCallback(callback);
        }
    }
    
    /**
     * Remove a voice emotional callback
     */
    public void removeVoiceEmotionalCallback(VoiceEmotionalAnalyzer.VoiceEmotionalCallback callback) {
        if (voiceEmotionalAnalyzer != null) {
            voiceEmotionalAnalyzer.removeVoiceEmotionalCallback(callback);
        }
    }
    
    /**
     * Process voice emotional feedback when speech starts
     */
    private void processVoiceEmotionalFeedback(String text) {
        if (voiceEmotionalAnalyzer != null && currentSpeakingMaster != null && !currentListeningMasters.isEmpty()) {
            try {
                // Get current emotional intensity (if available)
                float currentIntensity = 0.5f; // Default
                if (currentEmotionalState != null) {
                    currentIntensity = currentEmotionalState.intensity;
                }
                
                // Process voice emotional feedback
                voiceEmotionalAnalyzer.processVoiceEmotionalFeedback(
                    text, 
                    currentSpeakingMaster, 
                    currentIntensity, 
                    currentListeningMasters
                );
                
                Log.d(TAG, String.format("🎭 Processed voice emotional feedback: %s → %d listeners", 
                                        currentSpeakingMaster, currentListeningMasters.size()));
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error processing voice emotional feedback", e);
            }
        }
    }
    
    /**
     * Enhanced speak method with voice-emotion integration for spectator mode
     */
    public void speakWithVoiceEmotionalFeedback(String masterName, String text, 
                                              List<String> listeningMasters, 
                                              SpeechCallback callback) {
        // Set the voice emotional context
        setVoiceEmotionalContext(masterName, listeningMasters);
        
        // Use the existing speakWithSpecificMaster method
        speakWithSpecificMaster(masterName, text, callback);
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        // Save for future use
        prefs.edit().putString("elevenlabs_api_key", apiKey).apply();
    }
    
    /**
     * Legacy method support
     */
    public void speak(String text) {
        speak(text, (OnSpeechCompletedListener) null);
    }
    
    /**
     * Force stop all speech immediately to prevent ANR
     */
    public void stopSpeaking() {
        Log.d(TAG, "🛑 FORCE STOPPING ALL TTS");
        
        interruptRequested = true;
        isSpeaking = false;
        
        // Stop all audio immediately
        cleanupPreviousSession();
        
        // Clear all pending work
        pendingSpeechQueue.clear();
        
        // TTS force stopped
    }
    
    /**
     * Check if speech is a duplicate of recent speech to prevent repetition
     */
    private boolean isDuplicateSpeech(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        String normalizedText = text.trim().toLowerCase();
        long currentTime = System.currentTimeMillis();
        
        // Clean up old entries
        recentSpeechCache.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > SPEECH_DUPLICATE_WINDOW);
        
        // Check if this text was spoken recently
        if (recentSpeechCache.containsKey(normalizedText)) {
            long lastTime = recentSpeechCache.get(normalizedText);
            if (currentTime - lastTime < SPEECH_DUPLICATE_WINDOW) {
                return true; // Duplicate detected
            }
        }
        
        // Add/update this text in the cache
        recentSpeechCache.put(normalizedText, currentTime);
        
        return false;
    }
    
    /**
     * Shutdown with thorough cleanup
     */
    public void shutdown() {
        Log.d(TAG, "🛑 Shutting down ElevenLabs TTS service");
        stopSpeech();
        cleanupCurrentSession();
        recentSpeechCache.clear();
        executorService.shutdown();
    }
    
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }
    
    /**
     * 🔧 DIAGNOSTIC: Test ElevenLabs voice configuration and report issues
     */
    public void diagnoseVoiceConfiguration() {
        Log.i(TAG, "🔧 ============ ELEVENLABS VOICE DIAGNOSTIC ============");
        
        // Check API key
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ CRITICAL: No ElevenLabs API key configured");
        } else {
            Log.i(TAG, "✅ API Key: Configured (length: " + apiKey.length() + ")");
        }
        
        // Check voice mappings
        Log.i(TAG, "🎭 Voice ID Mappings:");
        for (Map.Entry<String, String> entry : MASTER_VOICE_IDS.entrySet()) {
            String master = entry.getKey();
            String voiceId = entry.getValue();
            double stability = getStabilityForMaster(master);
            double similarity = getSimilarityBoostForMaster(master);
            
            Log.i(TAG, String.format("  %s: %s (stability: %.2f, similarity: %.2f)", 
                master, voiceId, stability, similarity));
        }
        
        // Check current settings
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        boolean useElevenLabs = prefs.getBoolean("use_elevenlabs_tts", true);
        Log.i(TAG, "⚙️ ElevenLabs enabled: " + useElevenLabs);
        
        String currentMaster = getCurrentChessMaster();
        Log.i(TAG, "👤 Current master: " + currentMaster);
        Log.i(TAG, "🎤 Current voice ID: " + getVoiceIdForMaster(currentMaster));
        
        // Check emotional state
        if (currentEmotionalState != null) {
            Log.i(TAG, String.format("🎭 Emotional state: %s (intensity: %.2f)", 
                currentEmotionalState.emotion.name, currentEmotionalState.intensity));
        } else {
            Log.i(TAG, "🎭 No emotional state set");
        }
        
        Log.i(TAG, "🔧 ================================================");
    }
    
    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechReady(File audioFile);
        void onSpeechCompleted();
        void onError(String errorMessage);
    }
}