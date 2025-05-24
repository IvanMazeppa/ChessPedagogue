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
 * Consolidated TTS service with proper chunk ordering and simplified voice instructions
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

    // Chunk management
    private final ConcurrentLinkedQueue<ChunkPlaybackItem> chunkQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isPlayingChunks = new AtomicBoolean(false);
    private final AtomicInteger chunkIdCounter = new AtomicInteger(0);
    private final Map<Integer, ChunkPlaybackItem> pendingChunks = new HashMap<>();
    private int nextChunkToPlay = 0;

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
     * Main speak method with proper chunk ordering
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speaking text, length: " + (text != null ? text.length() : 0));

        if (text == null || text.isEmpty()) {
            if (listener != null) {
                listener.onSpeechCompleted();
            }
            return;
        }

        isSpeaking = true;
        interruptRequested = false;

        // Reset chunk counter for this speech session
        chunkIdCounter.set(0);
        nextChunkToPlay = 0;
        chunkQueue.clear();
        pendingChunks.clear();

        // Use a single TTSCallback that manages ordering
        TTSCallback orderingCallback = new TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "Speech started");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                // This is handled in the chunk management
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "All chunks completed");
                mainHandler.post(() -> {
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS Error: " + errorMessage);
                mainHandler.post(() -> {
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

                Log.d(TAG, "Generating chunk " + chunkId + " with voice: " + voiceToUse);

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

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String error = "TTS API error: " + response.code();
                        Log.e(TAG, error);
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError(error));
                        }
                        return;
                    }

                    byte[] audioData = response.body().bytes();
                    File audioFile = saveAudioToFile(audioData);

                    // Create chunk item and add to pending
                    ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(
                            chunkId, audioFile, text, callback, isFinalChunk);

                    // Add to pending chunks
                    synchronized (pendingChunks) {
                        pendingChunks.put(chunkId, chunkItem);

                        // Check if we can play any chunks now
                        tryPlayNextChunks();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "TTS error: " + e.getMessage(), e);
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
    private void tryPlayNextChunks() {
        synchronized (pendingChunks) {
            // Add any ready chunks to the queue
            while (pendingChunks.containsKey(nextChunkToPlay)) {
                ChunkPlaybackItem chunk = pendingChunks.remove(nextChunkToPlay);
                chunkQueue.offer(chunk);
                nextChunkToPlay++;
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
                Log.d(TAG, "Playing chunk " + chunk.chunkId);

                if (chunk.callback != null) {
                    chunk.callback.onSpeechReady(chunk.audioFile);
                }

                MediaPlayer player = new MediaPlayer();
                player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
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
                    Log.d(TAG, "Chunk " + chunk.chunkId + " completed");
                    mp.release();
                    currentPlayer = null;

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
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    mp.release();
                    currentPlayer = null;

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
                Log.e(TAG, "Error playing chunk " + chunk.chunkId, e);
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
        Log.d(TAG, "Speaking streaming text with chunking");

        if (text == null || text.isEmpty()) {
            if (callback != null) {
                callback.onError("Empty text");
            }
            return;
        }

        isSpeaking = true;
        interruptRequested = false;

        // Reset chunk management
        chunkIdCounter.set(0);
        nextChunkToPlay = 0;
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
     * Interrupt current speech
     */
    public void interrupt() {
        Log.d(TAG, "Interrupting speech");
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
                currentPlayer.release();
                currentPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping player", e);
            }
        }

        isSpeaking = false;
        isPlayingChunks.set(false);

        if (speechCallback != null) {
            speechCallback.onSpeechInterrupted();
        }
    }

    /**
     * Cleanup playback resources
     */
    private void cleanupPlayback() {
        chunkQueue.clear();
        pendingChunks.clear();
        isPlayingChunks.set(false);
        isSpeaking = false;
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

    public void shutdown() {
        stopPlayback();
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