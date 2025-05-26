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
import java.util.LinkedList;
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
 * Consolidated TTS service with proper chunk ordering and ENHANCED cleanup
 * FIXED: Proper resource management to prevent silent failures after multiple uses
 */
public class OpenAITTSService {
    private static final String TAG = "OpenAITTSService";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";

    // Voice constants
    public static final String VOICE_GRANDMASTER = "onyx";
    public static final String VOICE_TUTOR = "nova";
    public static final String VOICE_SHIMMER = "shimmer";
    public static final String VOICE_ECHO = "echo";
    public static final String VOICE_ALLOY = "alloy";
    public static final String VOICE_FABLE = "fable";
    public static final String VOICE_ONYX = "onyx";
    public static final String VOICE_NOVA = "nova";

    // Model constants
    public static final String MODEL_STANDARD = "tts-1";
    public static final String MODEL_PREMIUM = "tts-1-hd";

    private static OpenAITTSService instance;

    private final OkHttpClient httpClient;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private String apiKey;
    private MediaPlayer currentPlayer;
    private String voice = VOICE_GRANDMASTER;
    private String model = MODEL_STANDARD;
    private boolean interruptRequested = false;
    private boolean isSpeaking = false;
    private SharedPreferences prefs;

    // ENHANCED: Chunk management with better cleanup
    private final ConcurrentLinkedQueue<ChunkPlaybackItem> chunkQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isPlayingChunks = new AtomicBoolean(false);
    private final AtomicInteger chunkIdCounter = new AtomicInteger(0);
    private final Map<Integer, ChunkPlaybackItem> pendingChunks = new HashMap<>();
    private final AtomicInteger nextChunkToPlay = new AtomicInteger(0); // FIXED: Use AtomicInteger

    // ADDED: Track active players for cleanup
    private final Map<Integer, MediaPlayer> activePlayers = new HashMap<>();

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

    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        this.httpClient = OpenAIService.getInstance().getHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context);
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
     * ENHANCED: Main speak method with proper cleanup between sessions
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speaking text, length: " + (text != null ? text.length() : 0));

        if (text == null || text.isEmpty()) {
            if (listener != null) {
                listener.onSpeechCompleted();
            }
            return;
        }

        // CRITICAL: Clean up any previous session before starting new one
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
                Log.d(TAG, "✅ Speech started successfully");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "✅ Audio file ready: " + audioFile.getName());
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "✅ All chunks completed successfully");
                mainHandler.post(() -> {
                    cleanupCurrentSession(); // ADDED: Clean up after completion
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ TTS Error: " + errorMessage);
                mainHandler.post(() -> {
                    cleanupCurrentSession(); // ADDED: Clean up after error
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                });
            }
        };

        // Generate a single chunk for the entire text
        generateTTSChunk(text, 0, true, orderingCallback);
    }

    /**
     * ADDED: Clean up any previous session's resources
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
            chunkQueue.clear();
            pendingChunks.clear();
            isPlayingChunks.set(false);

            Log.d(TAG, "✅ Previous session cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Error during session cleanup", e);
        }
    }

    /**
     * ADDED: Clean up current session's resources
     */
    private void cleanupCurrentSession() {
        try {
            // Clean up temporary audio files
            File cacheDir = new File(context.getCacheDir(), "tts_cache");
            if (cacheDir.exists()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().startsWith("tts_") &&
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
     * Generate TTS for a chunk with simplified voice instructions
     */
    private void generateTTSChunk(String text, int chunkId, boolean isFinalChunk, TTSCallback callback) {
        executorService.execute(() -> {
            try {
                String voiceToUse = getVoiceForCurrentMaster();
                String selectedMaster = getCurrentChessMaster();

                // Build the TTS request with SIMPLIFIED approach
                JSONObject payload = new JSONObject();
                payload.put("model", "gpt-4o-mini-tts");  // Using the new model
                payload.put("voice", voiceToUse);
                payload.put("speed", 1.0);

                // SIMPLIFIED: Combine the instruction with the text itself
                String enhancedText = createEnhancedText(text, selectedMaster);
                payload.put("input", enhancedText);

                Log.d(TAG, "🎤 Generating chunk " + chunkId + " with voice: " + voiceToUse);

                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );

                Request request = new Request.Builder()
                        .url(TTS_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                // CRITICAL: Use try-with-resources for proper connection cleanup
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String error = "TTS API error: " + response.code();
                        Log.e(TAG, error);
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError(error));
                        }
                        return;
                    }

                    if (response.body() == null) {
                        Log.e(TAG, "TTS API returned null body");
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError("Empty response from TTS API"));
                        }
                        return;
                    }

                    byte[] audioData = response.body().bytes();
                    File audioFile = saveAudioToFile(audioData);

                    Log.d(TAG, "✅ Generated audio file: " + audioFile.getName() + " (" + audioData.length + " bytes)");

                    // ALSO FIND this section in generateTTSChunk method (around line 350)
                    // REPLACE the chunk creation section with this:

                    // Create chunk item and add to pending
                    ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(
                            chunkId, audioFile, text, callback, isFinalChunk);

                    // CRITICAL FIX: Add to pending chunks AND trigger playback
                    synchronized (pendingChunks) {
                        pendingChunks.put(chunkId, chunkItem);
                        Log.d(TAG, "📦 Added chunk " + chunkId + " to pending, total pending: " + pendingChunks.size());

                        // IMMEDIATE: Check if we can play any chunks now
                        tryPlayNextChunks();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ TTS error: " + e.getMessage(), e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("TTS error: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * Creates enhanced text with simple voice instructions embedded naturally
     */
    private String createEnhancedText(String originalText, String master) {
        // Get simple voice instruction from FineTunedModelManager
        String voiceInstruction = FineTunedModelManager.getInstance(context)
                .getSimplifiedInstructionsForMaster(master);

        // For gpt-4o-mini-tts, embed the instruction naturally at the beginning
        return "[" + voiceInstruction + "] " + originalText;
    }

    /**
     * Try to play the next chunks in order
     */
    // FIND this method in your OpenAITTSService.java (around line 290)
// REPLACE the tryPlayNextChunks method with this FIXED version:

    /**
     * FIXED: Try to play the next chunks in order with proper debugging
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
            } else if (isPlayingChunks.get()) {
                Log.d(TAG, "⏳ Already playing chunks, queue size: " + chunkQueue.size());
            } else if (chunkQueue.isEmpty()) {
                Log.d(TAG, "📭 Queue is empty, waiting for more chunks");
            }
        }
    }



    /**
     * ENHANCED: Play the next chunk in the queue with better error handling
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

                // ADDED: Track this player for cleanup
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

                    // ENHANCED: Proper cleanup
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

                    // ENHANCED: Proper error cleanup
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
     * Enhanced streaming TTS with proper chunk ordering
     */
    public void speakStreamingText(String text, TTSCallback callback) {
        Log.d(TAG, "🎤 Speaking streaming text with chunking");

        if (text == null || text.isEmpty()) {
            if (callback != null) {
                callback.onError("Empty text");
            }
            return;
        }

        // CRITICAL: Clean up before starting
        cleanupPreviousSession();

        isSpeaking = true;
        interruptRequested = false;

        // Reset chunk management
        chunkIdCounter.set(0);
        nextChunkToPlay.set(0);
        chunkQueue.clear();
        pendingChunks.clear();

        // Split text into natural chunks
        String[] sentences = text.split("(?<=[.!?])\\s+");

        for (int i = 0; i < sentences.length; i++) {
            String chunk = sentences[i];
            boolean isFinal = (i == sentences.length - 1);

            // Generate TTS for each chunk with proper ordering
            generateTTSChunk(chunk, i, isFinal, callback);
        }
    }

    /**
     * Simple direct TTS without chunking complexity
     */
    public void speakDirect(String text, TTSCallback callback) {
        generateTTSChunk(text, 0, true, callback);
    }

    /**
     * Get voice for current chess master
     */
    private String getVoiceForCurrentMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        String currentMaster = masterPrefs.getString("selected_master", "tal");
        String voiceOverride = prefs.getString("voice_style", "auto");

        if (!"auto".equals(voiceOverride)) {
            return voiceOverride;
        }

        return FineTunedModelManager.getInstance(context).getVoiceForMaster(currentMaster);
    }

    /**
     * Get current chess master
     */
    private String getCurrentChessMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        return masterPrefs.getString("selected_master", "tal");
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
     * Public method to generate and queue a TTS chunk with proper ordering
     */
    public void speakChunk(String text, int chunkId, boolean isFinalChunk, TTSCallback callback) {
        generateTTSChunk(text, chunkId, isFinalChunk, callback);
    }

    /**
     * ENHANCED: Interrupt current speech with thorough cleanup
     */

    /**
     * ENHANCED: Interrupt current speech with thorough cleanup and better error handling
     */
    public void interrupt() {
        Log.d(TAG, "🛑 Interrupting speech with full cleanup");
        interruptRequested = true;

        // Clear all pending chunks
        chunkQueue.clear();
        pendingChunks.clear();

        // Stop current playback with better error handling
        if (currentPlayer != null) {
            try {
                // SAFER: Check state before calling isPlaying()
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop();
                }
            } catch (IllegalStateException e) {
                // Player might be in an invalid state - just release it
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

        // ENHANCED: Stop all active players with better error handling
        synchronized (activePlayers) {
            for (MediaPlayer player : activePlayers.values()) {
                try {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                } catch (IllegalStateException e) {
                    // Player in invalid state, skip to release
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
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Save audio data to file
     */
    private File saveAudioToFile(byte[] audioData) throws IOException {
        File cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
        File audioFile = new File(cacheDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioData);
        }

        return audioFile;
    }

    /**
     * Legacy method support
     */
    public void speak(String text) {
        speak(text, (OnSpeechCompletedListener) null);
    }

    public void speak(String text, String voice, String model, TTSCallback callback) {
        this.voice = voice;
        this.model = model;
        speakDirect(text, callback);
    }

    public void speakWithChunking(String text, TTSCallback callback) {
        speakStreamingText(text, callback);
    }

    public void setVoicePersonalization(boolean usePersonality) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("use_master_personality", usePersonality);
        editor.apply();
    }

    public void setVoiceOverride(String voiceStyle) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("voice_style", voiceStyle);
        editor.apply();
    }

    public void clearVoiceOverride() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("voice_style", "auto");
        editor.apply();
    }

    public void setVoicePersonalizationInstructions(String instructions) {
        // Not used with simplified approach - keeping for compatibility
    }

    public void stopPlayback() {
        interrupt();
    }

    /**
     * ENHANCED: Shutdown with thorough cleanup
     */
    public void shutdown() {
        Log.d(TAG, "🛑 Shutting down TTS service");
        stopPlayback();
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