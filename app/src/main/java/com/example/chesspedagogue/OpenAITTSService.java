package com.example.chesspedagogue;

import android.content.Context;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Ultra-fast TTS service optimized for minimal latency
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
    public static final String MASTER = "onyx";

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
    private boolean usePersonalityInstructions = true;
    private String voiceIdOverride = null;

    // Ultra-fast processing queue
    private final Queue<ChunkData> readyChunks = new LinkedList<>();
    private final List<ProcessingChunk> processingChunks = new ArrayList<>();
    private boolean isPlaying = false;
    private int currentChunkIndex = 0;

    private final Queue<AudioPlaybackTask> playbackQueue = new LinkedList<>();
    private boolean isCurrentlyPlaying = false;
    private final Object playbackLock = new Object();


    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.apiKey = null;

        // Hyper-optimized HTTP client for minimal latency
        this.httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(15, 30, TimeUnit.SECONDS))
                .connectTimeout(3, TimeUnit.SECONDS)    // Reduced from 5
                .readTimeout(8, TimeUnit.SECONDS)       // Reduced from 10
                .writeTimeout(3, TimeUnit.SECONDS)      // Reduced from 5
                .dispatcher(new Dispatcher(Executors.newFixedThreadPool(12))) // More threads
                .build();

        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(6); // More threads for parallel processing
    }

    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context);
        }
        return instance;
    }

    private class AudioPlaybackTask {
        final File audioFile;
        final TTSCallback callback;

        AudioPlaybackTask(File audioFile, TTSCallback callback) {
            this.audioFile = audioFile;
            this.callback = callback;
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

    public void setVoicePersonalization(boolean usePersonality) {
        this.usePersonalityInstructions = usePersonality;
    }

    public void setVoiceOverride(String voiceId) {
        this.voiceIdOverride = voiceId;
    }

    public void clearVoiceOverride() {
        this.voiceIdOverride = null;
    }

    /**
     * NEW: Ultra-fast streaming TTS that processes and plays chunks as they arrive
     */
    public void speakStreamingText(String text, TTSCallback callback) {
        Log.d(TAG, "🚀 Starting ultra-fast streaming TTS");

        // Reset state
        interruptRequested = false;
        stopPlayback();

        synchronized (readyChunks) {
            readyChunks.clear();
        }
        processingChunks.clear();
        currentChunkIndex = 0;
        isPlaying = false;

        if (callback != null) {
            mainHandler.post(callback::onSpeechStarted);
        }

        // Create ultra-aggressive chunks for fastest response
        List<String> chunks = createUltraFastChunks(text);
        Log.d(TAG, "Created " + chunks.size() + " ultra-fast chunks");

        // Process first chunk immediately with highest priority
        if (!chunks.isEmpty()) {
            processChunkWithPriority(chunks.get(0), 0, true, callback);
        }

        // Process remaining chunks in parallel
        for (int i = 1; i < chunks.size(); i++) {
            final int chunkIndex = i;
            final String chunkText = chunks.get(i);

            // Small delay to prioritize first chunk
            mainHandler.postDelayed(() -> {
                if (!interruptRequested) {
                    processChunkWithPriority(chunkText, chunkIndex, false, callback);
                }
            }, i * 50); // 50ms stagger
        }

        // Start checking for ready chunks
        checkAndPlayNextChunk(callback);
    }

    /**
     * Create ultra-aggressive chunks for fastest possible first response
     */
    private List<String> createUltraFastChunks(String text) {
        List<String> chunks = new ArrayList<>();

        // First chunk: Get SOMETHING playing ASAP (even just a few words)
        String firstSentence = extractFirstMeaningfulChunk(text);
        if (firstSentence.length() > 0) {
            chunks.add(firstSentence);
            text = text.substring(firstSentence.length()).trim();
        }

        // Remaining chunks: Balance between speed and naturalness
        while (text.length() > 0) {
            String chunk = extractNextChunk(text, 80); // Small chunks for speed
            chunks.add(chunk);
            text = text.substring(chunk.length()).trim();
        }

        return chunks;
    }

    private String extractFirstMeaningfulChunk(String text) {
        // Find first sentence or clause, but cap at 40 chars for ultra-fast response
        int maxLength = Math.min(40, text.length());

        for (int i = 15; i < maxLength; i++) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?' || c == ',' || c == ';') {
                return text.substring(0, i + 1);
            }
        }

        // If no punctuation, take first 40 chars or until space
        for (int i = maxLength - 1; i >= 20; i--) {
            if (text.charAt(i) == ' ') {
                return text.substring(0, i);
            }
        }

        return text.substring(0, Math.min(40, text.length()));
    }

    private String extractNextChunk(String text, int preferredLength) {
        if (text.length() <= preferredLength) {
            return text;
        }

        // Look for natural break points
        for (int i = preferredLength; i >= preferredLength / 2; i--) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                return text.substring(0, i + 1);
            }
        }

        // Look for comma or semicolon
        for (int i = preferredLength; i >= preferredLength / 2; i--) {
            char c = text.charAt(i);
            if (c == ',' || c == ';') {
                return text.substring(0, i + 1);
            }
        }

        // Look for space
        for (int i = preferredLength; i >= preferredLength / 2; i--) {
            if (text.charAt(i) == ' ') {
                return text.substring(0, i);
            }
        }

        return text.substring(0, preferredLength);
    }

    /**
     * Process a chunk with priority handling
     */
    private void processChunkWithPriority(String chunkText, int chunkIndex, boolean isFirst, TTSCallback callback) {
        executorService.execute(() -> {
            try {
                if (interruptRequested) return;

                Log.d(TAG, "🎯 Processing chunk " + chunkIndex + (isFirst ? " (PRIORITY)" : "") + ": " +
                        chunkText.substring(0, Math.min(20, chunkText.length())) + "...");

                // Get voice settings
                String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();
                String voiceToUse = getVoiceId(selectedMaster);
                String instructions = ChessMasterVoiceManager.getInstructionsForChunk(selectedMaster, chunkIndex);

                // Create API request
                JSONObject payload = new JSONObject();
                payload.put("model", isFirst ? "tts-1" : model); // Use fastest model for first chunk
                payload.put("input", chunkText);
                payload.put("voice", voiceToUse);
                payload.put("response_format", "mp3");

                if (usePersonalityInstructions && instructions != null) {
                    payload.put("instructions", instructions);
                }

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

                long startTime = System.currentTimeMillis();

                Log.d(TAG, "🌐 Sending chunk " + chunkIndex + " to API...");

                try (Response response = httpClient.newCall(request).execute()) {
                    Log.d(TAG, "📡 Response for chunk " + chunkIndex + ": " + response.code());

                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e(TAG, "❌ TTS API error for chunk " + chunkIndex + ": " + response.code());
                        if (response.body() != null) {
                            Log.e(TAG, "Error details: " + response.body().string());
                        }
                        return;
                    }

                    byte[] audioData = response.body().bytes();
                    long processingTime = System.currentTimeMillis() - startTime;

                    Log.d(TAG, "✅ Chunk " + chunkIndex + " completed in " + processingTime + "ms, " +
                            audioData.length + " bytes");

                    File audioFile = saveAudioToFile(audioData);

                    synchronized (readyChunks) {
                        readyChunks.add(new ChunkData(chunkIndex, audioFile));

                        // Sort chunks by index
                        List<ChunkData> sortedChunks = new ArrayList<>(readyChunks);
                        sortedChunks.sort((a, b) -> Integer.compare(a.index, b.index));
                        readyChunks.clear();
                        readyChunks.addAll(sortedChunks);
                    }

                    // Immediately check if we can play this chunk
                    mainHandler.post(() -> checkAndPlayNextChunk(callback));
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error processing chunk " + chunkIndex + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Check and play the next available chunk
     */
    private void checkAndPlayNextChunk(TTSCallback callback) {
        synchronized (readyChunks) {
            if (isPlaying || interruptRequested) {
                return;
            }

            // Look for the next chunk in sequence
            ChunkData nextChunk = null;
            for (ChunkData chunk : readyChunks) {
                if (chunk.index == currentChunkIndex) {
                    nextChunk = chunk;
                    break;
                }
            }

            if (nextChunk != null) {
                readyChunks.remove(nextChunk);
                isPlaying = true;
                playChunk(nextChunk, callback);
            } else {
                // Schedule another check
                mainHandler.postDelayed(() -> checkAndPlayNextChunk(callback), 50);
            }
        }
    }

    /**
     * Play a chunk of audio
     */
    private void playChunk(ChunkData chunk, TTSCallback callback) {
        try {
            Log.d(TAG, "▶️ Playing chunk " + chunk.index);

            if (callback != null) {
                mainHandler.post(() -> callback.onSpeechReady(chunk.audioFile));
            }

            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                            .build()
            );

            player.setOnCompletionListener(mp -> {
                Log.d(TAG, "✅ Chunk " + chunk.index + " playback completed");
                mp.release();
                currentPlayer = null;

                isPlaying = false;
                currentChunkIndex++;

                // Check if there are more chunks or if we're done
                synchronized (readyChunks) {
                    boolean hasMoreChunks = readyChunks.stream().anyMatch(c -> c.index >= currentChunkIndex);
                    if (!hasMoreChunks && callback != null) {
                        // We're done!
                        mainHandler.post(callback::onSpeechCompleted);
                    } else {
                        // Play next chunk
                        mainHandler.post(() -> checkAndPlayNextChunk(callback));
                    }
                }
            });

            player.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "❌ MediaPlayer error: what=" + what + ", extra=" + extra);
                mp.release();
                currentPlayer = null;
                isPlaying = false;
                currentChunkIndex++;
                mainHandler.post(() -> checkAndPlayNextChunk(callback));
                return true;
            });

            player.setDataSource(context, Uri.fromFile(chunk.audioFile));
            player.prepare();
            currentPlayer = player;
            player.start();

        } catch (Exception e) {
            Log.e(TAG, "❌ Error playing chunk: " + e.getMessage(), e);
            isPlaying = false;
            currentChunkIndex++;
            checkAndPlayNextChunk(callback);
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public void speakWithChunking(String text, TTSCallback callback) {
        speakStreamingText(text, callback);
    }

    public void speak(String text, String voice, String model, TTSCallback callback) {
        this.voice = voice;
        this.model = model;
        speakStreamingText(text, callback);
    }

    /**
     * Direct TTS for testing - bypasses chunking for simplicity
     */
    public void speakDirect(String text, TTSCallback callback) {
        Log.d(TAG, "🎯 Direct TTS - Starting immediate processing");

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ No API key set!");
            if (callback != null) {
                callback.onError("API key not set");
            }
            return;
        }

        executorService.execute(() -> {
            try {
                // Get voice for current master
                String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();
                String voiceToUse = getVoiceId(selectedMaster);

                Log.d(TAG, "🎯 Making TTS API call with voice: " + voiceToUse);

                // Create API request WITH personality instructions
                JSONObject payload = new JSONObject();
                payload.put("model", MODEL_STANDARD);
                payload.put("input", text);
                payload.put("voice", voiceToUse);
                payload.put("response_format", "mp3");

                // ADD THE PERSONALITY INSTRUCTIONS!
                if (usePersonalityInstructions) {
                    String instructions = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
                    if (instructions != null) {
                        payload.put("instructions", instructions);
                        Log.d(TAG, "📝 Using voice instructions: " + instructions);
                    }
                }

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

                Log.d(TAG, "🌐 Sending request to OpenAI TTS API...");

                try (Response response = httpClient.newCall(request).execute()) {
                    Log.d(TAG, "📡 Response received: " + response.code());

                    if (!response.isSuccessful()) {
                        String errorMessage = "TTS API error: " + response.code();
                        if (response.body() != null) {
                            errorMessage += " - " + response.body().string();
                        }
                        final String finalError = errorMessage;
                        Log.e(TAG, "❌ " + finalError);
                        mainHandler.post(() -> callback.onError(finalError));
                        return;
                    }

                    if (response.body() == null) {
                        Log.e(TAG, "❌ Empty response body");
                        mainHandler.post(() -> callback.onError("Empty response"));
                        return;
                    }

                    byte[] audioData = response.body().bytes();
                    Log.d(TAG, "✅ Received " + audioData.length + " bytes of audio");

                    // Save to file
                    File audioFile = saveAudioToFile(audioData);
                    Log.d(TAG, "💾 Saved to: " + audioFile.getAbsolutePath());

                    // Queue for playback instead of playing immediately
                    queueAudioPlayback(audioFile, callback);

                }
            } catch (Exception e) {
                Log.e(TAG, "❌ TTS error: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("TTS error: " + e.getMessage());
                    }
                });
            }
        });
    }

    // Add this method to queue and play audio in order
    private void queueAudioPlayback(File audioFile, TTSCallback callback) {
        synchronized (playbackLock) {
            playbackQueue.offer(new AudioPlaybackTask(audioFile, callback));

            // If not currently playing, start playback
            if (!isCurrentlyPlaying) {
                playNextInQueue();
            }
        }
    }

    // Add this method to play the next audio in queue
    private void playNextInQueue() {
        synchronized (playbackLock) {
            if (playbackQueue.isEmpty()) {
                isCurrentlyPlaying = false;
                return;
            }

            isCurrentlyPlaying = true;
            AudioPlaybackTask task = playbackQueue.poll();

            mainHandler.post(() -> {
                try {
                    if (task.callback != null) {
                        task.callback.onSpeechReady(task.audioFile);
                    }

                    MediaPlayer player = new MediaPlayer();
                    player.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build()
                    );

                    player.setOnPreparedListener(mp -> {
                        Log.d(TAG, "🔊 Playing queued audio");
                        mp.start();
                    });

                    player.setOnCompletionListener(mp -> {
                        Log.d(TAG, "✅ Audio playback completed");
                        mp.release();

                        if (task.callback != null) {
                            task.callback.onSpeechCompleted();
                        }

                        // Play next in queue
                        synchronized (playbackLock) {
                            isCurrentlyPlaying = false;
                            playNextInQueue();
                        }
                    });

                    player.setOnErrorListener((mp, what, extra) -> {
                        Log.e(TAG, "❌ MediaPlayer error: " + what + ", " + extra);
                        mp.release();

                        if (task.callback != null) {
                            task.callback.onError("Playback error: " + what);
                        }

                        // Continue with next in queue
                        synchronized (playbackLock) {
                            isCurrentlyPlaying = false;
                            playNextInQueue();
                        }

                        return true;
                    });

                    player.setDataSource(context, Uri.fromFile(task.audioFile));
                    player.prepareAsync();

                } catch (Exception e) {
                    Log.e(TAG, "❌ Error playing audio", e);

                    if (task.callback != null) {
                        task.callback.onError("Playback error: " + e.getMessage());
                    }

                    // Continue with next in queue
                    synchronized (playbackLock) {
                        isCurrentlyPlaying = false;
                        playNextInQueue();
                    }
                }
            });
        }
    }

    public void stopPlayback() {
        interruptRequested = true;

        if (currentPlayer != null) {
            try {
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop();
                }
                currentPlayer.release();
                currentPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping media player", e);
            }
        }

        isPlaying = false;
    }

    private String getVoiceId(String selectedMaster) {
        if (voiceIdOverride != null && !voiceIdOverride.isEmpty()) {
            return voiceIdOverride;
        }
        return ChessMasterVoiceManager.getVoiceForMaster(selectedMaster);
    }

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

    public void shutdown() {
        stopPlayback();
        executorService.shutdown();
    }

    // Data classes
    private static class ChunkData {
        final int index;
        final File audioFile;

        ChunkData(int index, File audioFile) {
            this.index = index;
            this.audioFile = audioFile;
        }
    }

    private static class ProcessingChunk {
        final int index;
        final String text;
        final long startTime;

        ProcessingChunk(int index, String text) {
            this.index = index;
            this.text = text;
            this.startTime = System.currentTimeMillis();
        }
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