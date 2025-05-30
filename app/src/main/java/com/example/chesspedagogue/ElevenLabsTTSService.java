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
import java.util.HashMap;
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
    public static final String MODEL_MULTILINGUAL = "eleven_multilingual_v2"; // Highest quality (~200-300ms) - best for non-interactive
    
    // Voice IDs for chess masters - using carefully selected voices from ElevenLabs
    private static final Map<String, String> MASTER_VOICE_IDS = new HashMap<>();
    static {
        // Voices selected to match each chess master's personality and accent
        MASTER_VOICE_IDS.put("tal", "l1TTYDn50ppSCqvuVlKY"); // Selected voice for Tal - passionate, expressive

        MASTER_VOICE_IDS.put("fischer", "KLjqUZMleyr58nTJqW99"); // Selected voice for Fischer - intense, precise
        //MASTER_VOICE_IDS.put("fischer", "mrmh5i7zNpOwftrj8xdS"); // Selected voice for Fischer - intense, precise
        MASTER_VOICE_IDS.put("kasparov", "TxGEqnHWrfWFTfGW9XjX"); // Josh - dynamic, passionate
        MASTER_VOICE_IDS.put("carlsen", "ygiXC2Oa1BiHksD3WkJZ"); // Selected voice for Carlsen - modern, confident Norwegian-accented
        MASTER_VOICE_IDS.put("karpov", "IKne3meq5aSn9XLyUdCD"); // Charlie - refined, measured
        MASTER_VOICE_IDS.put("kramnik", "ErXwobaYiN019PkySvjV"); // Antoni - analytical, precise
        MASTER_VOICE_IDS.put("capablanca", "VR6AewLTigWG4xSOukaG"); // Arnold - elegant, natural
        MASTER_VOICE_IDS.put("alekhine", "EXAVITQu4vr4xnSDxMaL"); // Sam - sophisticated
        MASTER_VOICE_IDS.put("morphy", "yoZ06aMxZJJ28mfd3POQ"); // Sam - gentlemanly American
        MASTER_VOICE_IDS.put("lasker", "t0jbNlBVZ17f02VDIeMI"); // Adam - wise, philosophical
        MASTER_VOICE_IDS.put("anand", "g5CIjZEefAph4nQFvHAz"); // Premade - friendly, optimistic
        MASTER_VOICE_IDS.put("botvinnik", "SOYHLrjzK2X1ezoPC6cr"); // Harry - methodical British
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
    
    // Inner class for queued speech
    private static class PendingSpeech {
        final String text;
        final OnSpeechCompletedListener listener;
        
        PendingSpeech(String text, OnSpeechCompletedListener listener) {
            this.text = text;
            this.listener = listener;
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
        
        // Get API key from preferences
        // Note: System.getenv() doesn't work on Android - use SharedPreferences instead
        this.apiKey = prefs.getString("elevenlabs_api_key", "sk_78213d87bcdfcdb50e74b2a1c3944fabb71db48eb5ccbfb5");
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
        Log.d(TAG, "🎯 ElevenLabs context set to: " + context);
    }
    
    /**
     * Main speak method with ElevenLabs API
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "🎤 Speaking with ElevenLabs, length: " + (text != null ? text.length() : 0));
        
        if (text == null || text.isEmpty()) {
            if (listener != null) {
                listener.onSpeechCompleted();
            }
            return;
        }
        
        // Don't clean up if currently speaking - queue instead
        if (isSpeaking && !interruptRequested) {
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
                Log.d(TAG, "✅ ElevenLabs speech started successfully");
            }
            
            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "✅ ElevenLabs audio file ready: " + audioFile.getName());
            }
            
            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "✅ All chunks completed successfully");
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
        
        // Generate a single chunk for the entire text
        generateTTSChunk(text, 0, true, orderingCallback);
    }
    
    /**
     * Process any pending speech from the queue
     */
    private void processPendingSpeechQueue() {
        if (!pendingSpeechQueue.isEmpty()) {
            PendingSpeech pending = pendingSpeechQueue.poll();
            if (pending != null) {
                Log.d(TAG, "📢 Processing queued speech: " + pending.text.substring(0, Math.min(50, pending.text.length())) + "...");
                speak(pending.text, pending.listener);
            }
        }
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
                String selectedMaster = getCurrentChessMaster();
                String voiceId = getVoiceIdForMaster(selectedMaster);
                String model = getModelForContext(currentContext);
                
                Log.d(TAG, "🎯 ElevenLabs TTS Generation:");
                Log.d(TAG, "   Master: " + selectedMaster);
                Log.d(TAG, "   Voice ID: " + voiceId);
                Log.d(TAG, "   Model: " + model);
                Log.d(TAG, "   Text: " + text.substring(0, Math.min(50, text.length())) + "...");
                
                // Build the ElevenLabs request
                JSONObject payload = new JSONObject();
                payload.put("text", text);
                payload.put("model_id", model);
                
                // Voice settings for optimal quality
                JSONObject voiceSettings = new JSONObject();
                voiceSettings.put("stability", getStabilityForMaster(selectedMaster));
                voiceSettings.put("similarity_boost", getSimilarityBoostForMaster(selectedMaster));
                voiceSettings.put("style", 0); // Keep at 0 as recommended by ElevenLabs docs
                voiceSettings.put("use_speaker_boost", true);
                
                payload.put("voice_settings", voiceSettings);
                
                // Log payload for debugging
                Log.d(TAG, "📝 ElevenLabs Payload: " + payload.toString(2));
                
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
                
                Log.d(TAG, "🚀 Sending request to ElevenLabs API");
                
                try (Response response = httpClient.newCall(request).execute()) {
                    Log.d(TAG, "📡 ElevenLabs Response Code: " + response.code());
                    
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
                    
                    Log.d(TAG, "✅ Successfully generated ElevenLabs audio for " + selectedMaster);
                    Log.d(TAG, "   File: " + audioFile.getName() + " (" + audioData.length + " bytes)");
                    
                    // Create chunk item and add to pending
                    ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(
                            chunkId, audioFile, text, callback, isFinalChunk);
                    
                    synchronized (pendingChunks) {
                        if (chunkId == 0) {
                            nextChunkToPlay.set(0);
                            Log.d(TAG, "🔄 Reset nextChunkToPlay to 0 for new speech");
                        }
                        
                        pendingChunks.put(chunkId, chunkItem);
                        Log.d(TAG, "📦 Added chunk " + chunkId + " for " + selectedMaster + " to pending queue");
                        
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
     * Get ElevenLabs model based on usage context
     */
    private String getModelForContext(String context) {
        switch (context.toLowerCase()) {
            case "main_game":
            case "main":
            case "game":
                Log.d(TAG, "🏃‍♂️ Using FLASH model for main game (ultra-low latency)");
                return MODEL_FLASH; // eleven_flash_v2_5 for main game screen
                
            case "spectator_mode":
            case "spectator":
            case "ai_dialogue":
            case "dialogue":
                Log.d(TAG, "🎭 Using TURBO model for spectator mode (enhanced quality)");
                return MODEL_TURBO; // eleven_turbo_v2_5 for spectator mode
                
            case "analysis":
                Log.d(TAG, "🔬 Using TURBO model for analysis (balanced quality)");
                return MODEL_TURBO; // eleven_turbo_v2_5 for analysis
                
            default:
                Log.d(TAG, "🔄 Using TURBO model for default context");
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
     * Based on ElevenLabs docs: most common setting is around 0.5
     */
    private double getStabilityForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return 0.35; // More expressive for Tal's passionate style
            case "fischer":
                return 0.70; // More consistent for Fischer's precision
            case "kasparov":
                return 0.4; // Dynamic and intense
            case "carlsen":
                return 0.5; // Balanced modern style
            case "karpov":
                return 0.6; // Controlled and measured
            case "kramnik":
                return 0.65; // Methodical and precise
            default:
                return 0.5; // Recommended default
        }
    }
    
    /**
     * Get similarity boost for master (0.0 - 1.0)
     * Higher = more similar to original voice
     * Based on ElevenLabs docs: most common setting is around 0.75
     */
    private double getSimilarityBoostForMaster(String master) {
        // Keep at recommended 0.75 for all masters
        return 0.75;
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
     * Get current chess master from preferences
     */
    private String getCurrentChessMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        String master = masterPrefs.getString("selected_master", "tal");
        Log.d(TAG, "🎭 Current Chess Master: " + master);
        return master;
    }
    
    /**
     * Try to play the next chunks in order
     */
    private void tryPlayNextChunks() {
        synchronized (pendingChunks) {
            Log.d(TAG, "🔄 tryPlayNextChunks - pendingChunks size: " + pendingChunks.size() +
                    ", nextChunkToPlay: " + nextChunkToPlay.get() +
                    ", chunkQueue size: " + chunkQueue.size() +
                    ", isPlayingChunks: " + isPlayingChunks.get());
            
            // Add any ready chunks to the queue
            while (pendingChunks.containsKey(nextChunkToPlay.get())) {
                ChunkPlaybackItem chunk = pendingChunks.remove(nextChunkToPlay.get());
                chunkQueue.offer(chunk);
                Log.d(TAG, "✅ Added chunk " + nextChunkToPlay.get() + " to playback queue");
                nextChunkToPlay.incrementAndGet();
            }
            
            // Start playback if not already playing
            if (!isPlayingChunks.get() && !chunkQueue.isEmpty()) {
                Log.d(TAG, "🎵 Starting playback - queue size: " + chunkQueue.size());
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
                Log.d(TAG, "▶️ Playing chunk " + chunk.chunkId + " (" + chunk.audioFile.getName() + ")");
                
                if (chunk.callback != null) {
                    chunk.callback.onSpeechReady(chunk.audioFile);
                }
                
                MediaPlayer player = new MediaPlayer();
                
                // Track this player for cleanup
                synchronized (activePlayers) {
                    activePlayers.put(chunk.chunkId, player);
                }
                
                player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                );
                
                player.setOnPreparedListener(mp -> {
                    currentPlayer = mp;
                    mp.start();
                    Log.d(TAG, "▶️ Started playing chunk " + chunk.chunkId);
                    if (chunk.callback != null) {
                        chunk.callback.onSpeechStarted();
                    }
                });
                
                player.setOnCompletionListener(mp -> {
                    Log.d(TAG, "✅ Chunk " + chunk.chunkId + " completed successfully");
                    
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
        
        Log.d(TAG, "✅ TTS FORCE STOPPED");
    }
    
    /**
     * Shutdown with thorough cleanup
     */
    public void shutdown() {
        Log.d(TAG, "🛑 Shutting down ElevenLabs TTS service");
        stopSpeech();
        cleanupCurrentSession();
        executorService.shutdown();
    }
    
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }
    
    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechReady(File audioFile);
        void onSpeechCompleted();
        void onError(String errorMessage);
    }
}