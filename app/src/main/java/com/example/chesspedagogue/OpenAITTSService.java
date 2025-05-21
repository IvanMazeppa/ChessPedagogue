package com.example.chesspedagogue;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of TextToSpeechService using OpenAI's TTS API.
 * Optimized for low latency with preloading and parallel processing.
 */
public class OpenAITTSService {
    public static final String MODEL_TTS = "gpt-4o-mini-tts";
    // Voice options
    public static final String VOICE_GRANDMASTER = "onyx";  // Deeper, authoritative voice
    public static final String VOICE_TUTOR = "nova";        // Warmer, encouraging voice
    public static final String VOICE_SHIMMER = "shimmer";   // Cheerful voice
    public static final String VOICE_ECHO = "echo";         // Another option
    public static final String VOICE_ALLOY = "alloy";       // Another option
    public static final String VOICE_FABLE = "fable";       // British male
    public static final String VOICE_ONYX = "onyx";         // Deep, authoritative
    public static final String VOICE_NOVA = "nova";         // Female voice
    public static final String MASTER = "onyx";             // Alias for backwards compatibility

    // Model options
    public static final String MODEL_STANDARD = "tts-1";    // Standard quality
    public static final String MODEL_PREMIUM = "tts-1-hd";  // Higher quality

    private static final String TAG = "OpenAITTSService";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    private static final int MAX_CHARS_PER_CHUNK = 200;      // Smaller chunks for faster initial response
    private static final int PREFETCH_COUNT = 2;             // Number of chunks to prefetch

    // Singleton instance
    private static OpenAITTSService instance;

    private final OkHttpClient httpClient;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private final String responseFormat = "mp3";
    private final int sampleRate = 16000;
    private final long startTime = System.currentTimeMillis();
    private String apiKey;
    private MediaPlayer currentPlayer;
    private String voice = VOICE_GRANDMASTER;
    private String model = MODEL_STANDARD;
    private boolean interruptRequested = false;
    private boolean usePersonalityInstructions = true;
    private String voiceIdOverride = null;

    /**
     * Constructor for the service
     */
    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.apiKey = null; // Will be set later

        // Create an optimized OkHttpClient with connection pooling
        this.httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        this.mainHandler = new Handler(Looper.getMainLooper());

        // Use a thread pool for better resource management
        this.executorService = Executors.newFixedThreadPool(3);
    }

    /**
     * Get the singleton instance
     */
    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context);
        }
        return instance;
    }

    /**
     * Set the API key for OpenAI
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Set the voice to use
     */
    public void setVoice(String voice) {
        this.voice = voice;
    }

    /**
     * Set the model quality
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Set whether to use personality-based instructions
     */
    public void setVoicePersonalization(boolean usePersonality) {
        this.usePersonalityInstructions = usePersonality;
        Log.d(TAG, "Voice personalization set to: " + usePersonality);
    }

    /**
     * Override the automatic voice selection
     */
    public void setVoiceOverride(String voiceId) {
        this.voiceIdOverride = voiceId;
        Log.d(TAG, "Voice override set to: " + voiceId);
    }

    /**
     * Clear any voice override
     */
    public void clearVoiceOverride() {
        this.voiceIdOverride = null;
        Log.d(TAG, "Voice override cleared");
    }

    /**
     * Stop any ongoing audio playback
     */
    public void stopPlayback() {
        Log.d(TAG, "stopPlayback() called");

        // Set the interrupt flag
        interruptRequested = true;

        // Stop any currently playing audio
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
    }

    /**
     * Speak text using the selected voice and model
     */
    public void speak(String text, String voice, String model, TTSCallback callback) {
        Log.d(TAG, "speak() called");

        if (callback != null) {
            mainHandler.post(callback::onSpeechStarted);
        }

        // Use optimized chunking for faster response
        speakWithOptimizedChunking(text, callback);
    }

    /**
     * Speak text using chunking with prefetching for low latency
     */
    public void speakWithChunking(String text, TTSCallback callback) {
        Log.d(TAG, "speakWithChunking() called");

        // Switch to the optimized implementation
        speakWithOptimizedChunking(text, callback);
    }

    /**
     * Optimized chunking with prefetching and parallel processing
     */
    private void speakWithOptimizedChunking(String text, TTSCallback masterCallback) {
        // Reset interrupt flag
        interruptRequested = false;

        // Clear any previous playback
        stopPlayback();

        if (masterCallback != null) {
            mainHandler.post(masterCallback::onSpeechStarted);
        }

        // Create the optimized chunker
        OptimizedSpeechChunker chunker = new OptimizedSpeechChunker(text, masterCallback);
        chunker.startSpeaking();
    }

    /**
     * Optimized speech chunker with prefetching and parallel processing
     */
    private class OptimizedSpeechChunker {
        private final String[] chunks;
        private final TTSCallback masterCallback;
        private final Queue<ChunkData> readyChunks = new LinkedList<>();
        private final String currentVoice;
        private int currentChunkIndex = 0;
        private boolean isPlaying = false;
        private String lastChunkEnding = "";

        public OptimizedSpeechChunker(String text, TTSCallback callback) {
            this.masterCallback = callback;
            this.currentVoice = getVoiceId(FineTunedModelManager.getInstance(context).getSelectedChessMaster());

            // Split text into natural chunks
            List<String> chunkList = splitTextIntoNaturalChunks(text);
            this.chunks = chunkList.toArray(new String[0]);

            Log.d(TAG, "Split speech into " + chunkList.size() + " chunks");
        }

        private List<String> splitTextIntoNaturalChunks(String text) {
            List<String> chunks = new ArrayList<>();

            // Split by sentences
            String[] sentences = text.split("(?<=[.!?])\\s+");
            StringBuilder currentChunk = new StringBuilder();
            int currentSize = 0;

            for (String sentence : sentences) {
                // Don't split mid-sentence, and ensure chunks aren't too small
                if (currentSize + sentence.length() > MAX_CHARS_PER_CHUNK && currentSize > 50) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                    currentSize = 0;
                }

                currentChunk.append(sentence).append(" ");
                currentSize += sentence.length();
            }

            // Add the final chunk
            if (currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
            }

            return chunks;
        }

        public void startSpeaking() {
            // CRITICAL: Reset state variables
            interruptRequested = false;
            isPlaying = false;

            // Clear any existing chunks
            synchronized (readyChunks) {
                readyChunks.clear();
            }

            // Log that we're starting
            Log.d(TAG, "🚀 STARTING speech with " + chunks.length + " chunks");

            // Notify that speech is starting
            if (masterCallback != null) {
                mainHandler.post(masterCallback::onSpeechStarted);
            }

            // Start by prefetching the first few chunks
            prefetchChunks();

            // CRITICAL: Explicitly kick off the playback process with a delay to allow prefetch to start
            mainHandler.postDelayed(this::checkAndPlayNextChunk, 100);
        }

        private void prefetchChunks() {
            int endIndex = Math.min(currentChunkIndex + PREFETCH_COUNT, chunks.length);

            Log.d(TAG, "🔄 Prefetching chunks " + currentChunkIndex + " to " + (endIndex-1));

            for (int i = currentChunkIndex; i < endIndex; i++) {
                final int chunkIndex = i;

                executorService.execute(() -> {
                    try {
                        // Skip if interrupted
                        if (interruptRequested) return;

                        // Clean up chunk text (remove ellipses)
                        String chunkText = chunks[chunkIndex].replaceAll("\\.{3,}", "");

                        // Save ending for continuity
                        if (chunkText.length() > 30) {
                            lastChunkEnding = chunkText.substring(chunkText.length() - 30);
                        } else {
                            lastChunkEnding = chunkText;
                        }

                        // Get appropriate instructions
                        String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();
                        String instructions;

                        if (chunkIndex == 0) {
                            // First chunk - basic instruction
                            instructions = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
                        } else {
                            // Continuity instructions
                            instructions = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster) +
                                    " CRITICAL: Continue with the EXACT same voice, accent, pacing, and emotional tone." +
                                    " This is a direct continuation where you just finished saying: \"" + lastChunkEnding + "\"";
                        }

                        Log.d(TAG, "🔄 Generating speech for chunk " + chunkIndex);

                        // Create API request
                        JSONObject payload = new JSONObject();
                        payload.put("model", "gpt-4o-mini-tts");
                        payload.put("input", chunkText);
                        payload.put("voice", currentVoice);
                        payload.put("response_format", "mp3");
                        payload.put("instructions", instructions);

                        RequestBody body = RequestBody.create(
                                MediaType.parse("application/json"),
                                payload.toString()
                        );

                        Request request = new Request.Builder()
                                .url(TTS_URL)
                                .header("Authorization", "Bearer " + apiKey)
                                .post(body)
                                .build();

                        // Execute the request
                        try (Response response = httpClient.newCall(request).execute()) {
                            if (!response.isSuccessful() || response.body() == null) {
                                Log.e(TAG, "❌ TTS API error: " + response.code());
                                return;
                            }

                            // Get response data
                            byte[] audioData = response.body().bytes();

                            Log.d(TAG, "✅ Received " + audioData.length + " bytes of audio data for chunk " + chunkIndex);

                            // Save the audio to a temporary file
                            File audioFile = saveAudioToFile(audioData);

                            Log.d(TAG, "💾 Saved audio to file: " + audioFile.getAbsolutePath());

                            // Add to ready chunks queue
                            synchronized (readyChunks) {
                                readyChunks.add(new ChunkData(chunkIndex, audioFile));

                                // Sort chunks if needed
                                if (readyChunks.size() > 1) {
                                    List<ChunkData> sorted = new ArrayList<>(readyChunks);
                                    sorted.sort((a, b) -> Integer.compare(a.index, b.index));
                                    readyChunks.clear();
                                    readyChunks.addAll(sorted);
                                }

                                // Notify main thread to check and play if needed
                                Log.d(TAG, "📢 Chunk " + chunkIndex + " ready, notifying player");
                                mainHandler.post(this::checkAndPlayNextChunk);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error prefetching chunk " + chunkIndex + ": " + e.getMessage(), e);
                    }
                });
            }
        }

        private void checkAndPlayNextChunk() {
            synchronized (readyChunks) {
                Log.d(TAG, "⏳ Checking for chunks... isPlaying=" + isPlaying +
                        ", interruptRequested=" + interruptRequested +
                        ", currentChunkIndex=" + currentChunkIndex +
                        ", readyChunks.size()=" + readyChunks.size());

                // Skip if already playing or interrupted
                if (isPlaying || interruptRequested) {
                    Log.d(TAG, "⏸️ Not playing next chunk: " +
                            (isPlaying ? "already playing" : "interrupted"));
                    return;
                }

                // IMPROVEMENT: If this is our first chunk (nothing playing yet)
                // and we've been waiting more than 2 seconds, play ANY ready chunk
                if (currentChunkIndex == 0 && !readyChunks.isEmpty()) {
                    long now = System.currentTimeMillis();
                    if (now - startTime > 2000) { // 2 second threshold
                        // Play the first available chunk, whatever it is
                        ChunkData nextChunk = readyChunks.iterator().next();
                        Log.d(TAG, "⚡ Playing available chunk " + nextChunk.index + " to reduce latency!");

                        // Remove from queue
                        readyChunks.remove(nextChunk);

                        // Start playing
                        isPlaying = true;
                        playChunk(nextChunk);

                        // Update current index
                        currentChunkIndex = nextChunk.index + 1;

                        // Continue prefetching
                        prefetchChunks();

                        return;
                    }
                }

                // Normal sequential playback logic
                ChunkData nextChunk = null;
                for (ChunkData chunk : readyChunks) {
                    if (chunk.index == currentChunkIndex) {
                        nextChunk = chunk;
                        break;
                    }
                }

                if (nextChunk != null) {
                    Log.d(TAG, "✅ Found chunk " + currentChunkIndex + " ready to play!");

                    // Remove from queue
                    readyChunks.remove(nextChunk);

                    // Start playing
                    isPlaying = true;
                    playChunk(nextChunk);

                    // Prefetch more chunks if needed
                    if (currentChunkIndex + PREFETCH_COUNT < chunks.length) {
                        prefetchChunks();
                    }
                } else {
                    Log.d(TAG, "⏳ Chunk " + currentChunkIndex + " not ready yet, scheduling another check");

                    // Schedule another check soon if we're still waiting for the chunk
                    if (currentChunkIndex < chunks.length) {
                        mainHandler.postDelayed(this::checkAndPlayNextChunk, 200);
                    } else {
                        // We've reached the end
                        Log.d(TAG, "✅ All chunks complete!");
                        if (masterCallback != null) {
                            mainHandler.post(masterCallback::onSpeechCompleted);
                        }
                    }
                }
            }
        }

        private void playChunk(ChunkData chunk) {
            try {
                Log.d(TAG, "▶️ Playing chunk " + chunk.index);

                // Notify speech ready
                if (masterCallback != null) {
                    mainHandler.post(() -> masterCallback.onSpeechReady(chunk.audioFile));
                }

                // Create MediaPlayer
                MediaPlayer player = new MediaPlayer();
                player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                );

                // Set error listener
                player.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "❌ MediaPlayer error: what=" + what + ", extra=" + extra);
                    handleChunkCompletion();
                    return true;
                });

                // IMPORTANT: Log file details to help troubleshoot
                Log.d(TAG, "📊 Audio file: " + chunk.audioFile.getAbsolutePath() +
                        ", exists: " + chunk.audioFile.exists() +
                        ", size: " + chunk.audioFile.length() + " bytes");

                // Set data source
                player.setDataSource(context, Uri.fromFile(chunk.audioFile));
                player.prepare();

                // Store the player
                currentPlayer = player;

                // Set completion listener
                player.setOnCompletionListener(mp -> {
                    Log.d(TAG, "✅ Chunk " + chunk.index + " playback completed");
                    // Release resources
                    mp.release();
                    currentPlayer = null;

                    // Handle completion
                    handleChunkCompletion();
                });

                // Start playback
                player.start();

                // CRITICAL: Verify playback started
                if (player.isPlaying()) {
                    Log.d(TAG, "🎵 Playback confirmed started for chunk " + chunk.index);
                } else {
                    Log.e(TAG, "❌ Playback failed to start for chunk " + chunk.index);
                    player.release();
                    currentPlayer = null;
                    handleChunkCompletion();
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error playing chunk: " + e.getMessage(), e);
                handleChunkCompletion();
            }
        }

        private void handleChunkCompletion() {
            // Check if this was the last chunk
            boolean isLastChunk = currentChunkIndex >= chunks.length - 1;

            // Update state
            isPlaying = false;
            currentChunkIndex++;

            // If we're done, notify callback
            if (isLastChunk) {
                if (masterCallback != null) {
                    mainHandler.post(masterCallback::onSpeechCompleted);
                }
            } else {
                // Otherwise, play the next chunk
                mainHandler.post(this::checkAndPlayNextChunk);
            }
        }
    }

    private static class ChunkData {
        final int index;
        final File audioFile;

        ChunkData(int index, File audioFile) {
            this.index = index;
            this.audioFile = audioFile;
        }
    }

    /**
     * Get the appropriate voice ID based on settings
     */
    private String getVoiceId(String selectedMaster) {
        // If there's an override, use it
        if (voiceIdOverride != null && !voiceIdOverride.isEmpty()) {
            return voiceIdOverride;
        }

        // Otherwise use automatic selection based on master
        return ChessMasterVoiceManager.getVoiceForMaster(selectedMaster);
    }

    /**
     * Save audio bytes to a temporary file
     */
    private File saveAudioToFile(byte[] audioData) throws IOException {
        // Create a temporary file to store the audio
        File cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
        File audioFile = new File(cacheDir, fileName);

        // Write the audio data to the file
        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioData);
        }

        return audioFile;
    }

    /**
     * Get audio data directly from OpenAI TTS API
     */
    public byte[] synthesizeSpeech(String text) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return new byte[0];
        }

        String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();

        // Create JSON payload for TTS request
        JSONObject payload = new JSONObject();
        try {
            payload.put("model", "gpt-4o-mini-tts");
            payload.put("input", text);
            payload.put("voice", voice);
            payload.put("response_format", "mp3");

            // Add instructions for the chess grandmaster character
            String masterInstructions = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
            payload.put("instructions", masterInstructions);

            Log.d(TAG, "TTS API payload for " + selectedMaster + ": " + payload);
        } catch (JSONException e) {
            Log.e(TAG, "TTS JSON construction error", e);
            return new byte[0];
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

        Request request = new Request.Builder()
                .url(TTS_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            String errorBody = response.body() != null ? response.body().string() : "No error body";
            Log.e(TAG, "TTS API error: " + response.code() + " - " + errorBody);
            return new byte[0];
        }

        // The TTS API responds with audio data
        byte[] audioData = response.body().bytes();
        response.close();

        return audioData;
    }

    /**
     * Release resources when the service is no longer needed
     */
    public void shutdown() {
        stopPlayback();
        executorService.shutdown();
    }

    /**
     * Callback interface for TTS operations
     */
    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechReady(File audioFile);
        void onSpeechCompleted();
        void onError(String errorMessage);
    }
}