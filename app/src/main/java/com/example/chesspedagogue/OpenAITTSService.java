package com.example.chesspedagogue;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service for generating natural-sounding speech using OpenAI's TTS API
 * with optimizations for low latency and efficient resource usage.
 */
public class OpenAITTSService {
    private static final String TAG = "OpenAITTSService";
    private static final String API_URL = "https://api.openai.com/v1/audio/speech";

    // Voice options to match different personas
    public static final String VOICE_GRANDMASTER = "onyx";  // Deeper, authoritative voice
    public static final String VOICE_COACH = "echo";        // Optional additional voices
    public static final String VOICE_BEGINNER = "alloy";    // Optional additional voices
    public static final String VOICE_TUTOR = "nova";        // Warmer, encouraging voice
    public static final String VOICE_SHIMMER = "shimmer";   // Cheerful voice for younger players
    public static final String VOICE_ECHO = "echo";         // Another option
    public static final String VOICE_ALLOY = "alloy";       // Required for compatibility
    public static final String MASTER = "onyx";             // Deeper voice (compatibility)

    // Voice quality models
    public static final String MODEL_STANDARD = "tts-1";    // Lower cost, good quality
    public static final String MODEL_PREMIUM = "tts-1-hd";  // Higher quality, higher cost

    // Cache configuration
    private static final int MEMORY_CACHE_SIZE = 10;        // Number of audio files to keep in memory
    private static final int DISK_CACHE_MAX_SIZE = 100;     // Max number of files in disk cache
    private static final long DISK_CACHE_MAX_BYTES = 100 * 1024 * 1024; // 100 MB max cache size
    private static final int MAX_PARALLEL_REQUESTS = 1;     // Max parallel API requests

    // Sentence splitting pattern (end of sentence followed by space or end of text)
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("([.!?])\\s+|([.!?])$");

    // Frequently used chess phrases for preloading
    private static final String[] COMMON_CHESS_PHRASES = {
            "Check", "Checkmate", "Stalemate", "Your move", "Good move",
            "Let's analyze this position", "That's a mistake", "Consider this alternative",
            "Well played", "The best move here is", "Remember the principles",
            "Control the center", "Develop your pieces", "Castle for safety",
            "Watch for tactics", "Think about the endgame"
    };

    private final OkHttpClient client;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context;
    private String apiKey;
    private static OpenAITTSService instance;

    // Audio playback components
    private final ExoPlayer player;
    private final ConcurrentLinkedQueue<TTSRequest> requestQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isProcessingQueue = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<File> audioQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);

    // Add this class definition:
    private static class TTSRequest {
        final String text;
        final String voice;
        final String model;
        final String cacheKey;
        final TTSCallback callback;

        TTSRequest(String text, String voice, String model, String cacheKey, TTSCallback callback) {
            this.text = text;
            this.voice = voice;
            this.model = model;
            this.cacheKey = cacheKey;
            this.callback = callback;
        }
    }

    private void queueTTSRequest(String text, String voice, String model, String cacheKey, TTSCallback callback) {
        // Add to queue
        requestQueue.add(new TTSRequest(text, voice, model, cacheKey, callback));

        // Start processing if not already running
        if (isProcessingQueue.compareAndSet(false, true)) {
            processNextInQueue();
        }
    }

    private void processNextInQueue() {
        TTSRequest request = requestQueue.poll();
        if (request == null) {
            isProcessingQueue.set(false);
            return;
        }

        // Process the request
        requestTTS(request.text, request.voice, request.model, request.cacheKey, request.callback);

        // We'll continue processing the queue after this request completes
        // That logic should be in the callback from requestTTS
    }



    // Caching components
    private final LruCache<String, File> memoryCache;
    private final Map<String, Long> diskCacheIndex = new ConcurrentHashMap<>();
    private long diskCacheSize = 0;
    private final File cacheDir;

    // Speech requests tracking
    private final AtomicInteger activeRequests = new AtomicInteger(0);
    private final Map<String, List<TTSCallback>> pendingCallbacks = new ConcurrentHashMap<>();

    /**
     * Callback interface for TTS operations
     */
    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechReady(File audioFile);
        void onSpeechCompleted();
        void onError(String errorMessage);
    }

    /**
     * Create a new OpenAI TTS Service instance
     */
    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.client = new OkHttpClient.Builder().build();
        this.executorService = Executors.newFixedThreadPool(MAX_PARALLEL_REQUESTS + 1); // +1 for management tasks
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.player = new SimpleExoPlayer.Builder(context).build();

        // Initialize cache
        this.memoryCache = new LruCache<>(MEMORY_CACHE_SIZE);
        this.cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        // Load disk cache index
        loadCacheIndex();

        // Set up player completion listener
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    playNextInQueue();
                }
            }
        });
    }

    /**
     * Get singleton instance
     */
    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Set the API key for OpenAI
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;

        // Preload common phrases when API key is set
        preloadCommonPhrases();
    }

    /**
     * Compatibility method - maps to speak() method
     * This maintains backward compatibility with existing code
     */
    public void synthesizeSpeech(String text, String voice, String model, TTSCallback callback) {
        speak(text, voice, model, callback);
    }

    /**
     * Main method to speak text with optimized latency
     *
     * @param text The text to synthesize
     * @param voice The voice to use
     * @param model The model quality to use
     * @param callback Callback for events
     */

    public void speak(String text, String voice, String model, TTSCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            mainHandler.post(() -> callback.onError("API key not set"));
            return;
        }

        // Notify that speech is starting
        mainHandler.post(callback::onSpeechStarted);

        // Trim and validate text
        text = text.trim();
        if (text.isEmpty()) {
            mainHandler.post(() -> callback.onError("Empty text provided"));
            return;
        }


        // Calculate cache key
        String cacheKey = generateCacheKey(text, voice, model);

        // Try to get from memory cache first (fastest)
        File cachedFile = memoryCache.get(cacheKey);
        if (cachedFile != null && cachedFile.exists()) {
            Log.d(TAG, "Memory cache hit for: " + text.substring(0, Math.min(20, text.length())));
            playAudioFile(cachedFile, callback);
            return;
        }

        // Try disk cache next
        File diskCacheFile = getCacheFile(cacheKey);
        if (diskCacheFile.exists()) {
            Log.d(TAG, "Disk cache hit for: " + text.substring(0, Math.min(20, text.length())));
            // Add to memory cache for future use
            memoryCache.put(cacheKey, diskCacheFile);
            playAudioFile(diskCacheFile, callback);
            return;
        }

        // Check if we already have a pending request for this exact text/voice/model
        if (pendingCallbacks.containsKey(cacheKey)) {
            // Add this callback to the list of waiting callbacks
            pendingCallbacks.get(cacheKey).add(callback);
            Log.d(TAG, "Added to pending callbacks for: " + text.substring(0, Math.min(20, text.length())));
            return;
        }

        // Create a new list of callbacks for this request
        List<TTSCallback> callbacks = new ArrayList<>();
        callbacks.add(callback);
        pendingCallbacks.put(cacheKey, callbacks);

        // For longer text, split and process in parallel for lower perceived latency
        if (text.length() > 100) {
            speakLongText(text, voice, model, cacheKey);
        } else {
            // For shorter text, make a single request
            requestTTS(text, voice, model, cacheKey);
        }

        // If not in cache, queue the request
        queueTTSRequest(text, voice, model, cacheKey, callback);
    }

    /**
     * Handle longer text by splitting into sentences and processing in parallel
     */
    private void speakLongText(String text, String voice, String model, String originalCacheKey) {
        // Split into sentences
        String[] parts = SENTENCE_PATTERN.split(text);
        List<String> sentences = new ArrayList<>();

        // Rebuild sentences with their ending punctuation
        StringBuilder currentSentence = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isEmpty()) continue;

            if (currentSentence.length() > 0) {
                // Add a period if this looks like the end of a sentence
                if (part.length() > 0 && Character.isUpperCase(part.charAt(0))) {
                    currentSentence.append(". ");
                    sentences.add(currentSentence.toString());
                    currentSentence = new StringBuilder(part);
                } else {
                    currentSentence.append(" ").append(part);
                }
            } else {
                currentSentence.append(part);
            }
        }

        // Add the last sentence if anything remains
        if (currentSentence.length() > 0) {
            sentences.add(currentSentence.toString());
        }

        // If we couldn't split properly, just use the original text
        if (sentences.isEmpty()) {
            sentences.add(text);
        }

        // Create temporary files for each sentence
        final File[] sentenceFiles = new File[sentences.size()];
        final AtomicInteger completedCount = new AtomicInteger(0);

        // Process each sentence
        for (int i = 0; i < sentences.size(); i++) {
            final int index = i;
            final String sentence = sentences.get(i).trim();
            if (sentence.isEmpty()) {
                completedCount.incrementAndGet();
                continue;
            }

            // Create a cache key for this sentence
            String sentenceCacheKey = generateCacheKey(sentence, voice, model);

            // Try cache first
            File cachedFile = memoryCache.get(sentenceCacheKey);
            if (cachedFile == null) {
                cachedFile = getCacheFile(sentenceCacheKey);
            }

            if (cachedFile != null && cachedFile.exists()) {
                // Cache hit for this sentence
                sentenceFiles[index] = cachedFile;

                // Check if all sentences are complete
                if (completedCount.incrementAndGet() == sentences.size()) {
                    // All sentences ready, combine and play
                    combineSentenceFiles(sentenceFiles, originalCacheKey);
                }
            } else {
                // Need to request this sentence
                final String finalSentenceCacheKey = sentenceCacheKey;
                requestTTS(sentence, voice, model, sentenceCacheKey, new TTSCallback() {
                    @Override
                    public void onSpeechStarted() {
                        // Not used here
                    }

                    @Override
                    public void onSpeechReady(File audioFile) {
                        // Store this sentence file
                        sentenceFiles[index] = audioFile;

                        // Cache this sentence
                        memoryCache.put(finalSentenceCacheKey, audioFile);

                        // Check if all sentences are complete
                        if (completedCount.incrementAndGet() == sentences.size()) {
                            // All sentences ready, combine and play
                            combineSentenceFiles(sentenceFiles, originalCacheKey);
                        }
                    }

                    @Override
                    public void onSpeechCompleted() {
                        // Not used here
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Propagate error to all waiting callbacks
                        notifyCallbacksOfError(originalCacheKey, "Error with sentence " + index + ": " + errorMessage);
                    }
                });
            }
        }
    }

    /**
     * Combine multiple audio files into one
     */
    private void combineSentenceFiles(File[] files, String cacheKey) {
        executorService.execute(() -> {
            try {
                // Create a single output file
                File outputFile = getCacheFile(cacheKey);
                FileOutputStream fos = new FileOutputStream(outputFile);

                // Combine all files
                byte[] buffer = new byte[4096];
                for (File file : files) {
                    if (file == null || !file.exists()) continue;

                    try (InputStream is = context.getContentResolver().openInputStream(Uri.fromFile(file))) {
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                fos.close();

                // Add to memory cache
                memoryCache.put(cacheKey, outputFile);

                // Add to disk cache index
                updateCacheIndex(cacheKey, outputFile.length());

                // Notify all callbacks
                notifyCallbacksOfSuccess(cacheKey, outputFile);

            } catch (IOException e) {
                Log.e(TAG, "Error combining audio files", e);
                notifyCallbacksOfError(cacheKey, "Error combining audio: " + e.getMessage());
            }
        });
    }

    /**
     * Make the actual API request for a TTS conversion
     */
    private void requestTTS(String text, String voice, String model, String cacheKey) {
        requestTTS(text, voice, model, cacheKey, null);
    }

    private void requestTTS(String text, String voice, String model, String cacheKey, TTSCallback singleCallback) {
        // Find this in requestTTS or a similar method
        if (activeRequests.incrementAndGet() > MAX_PARALLEL_REQUESTS) {
            // If we have too many active requests, wait and retry
            activeRequests.decrementAndGet();
            mainHandler.postDelayed(() -> requestTTS(text, voice, model, cacheKey, singleCallback), 500); // Simple delay and retry
            return;
        }


        // Execute the request on a background thread
        executorService.execute(() -> {
            try {
                // Create request body
                JSONObject requestData = new JSONObject();
                requestData.put("model", model);
                requestData.put("input", text);
                requestData.put("voice", voice);

                // Build the request
                RequestBody body = RequestBody.create(
                        requestData.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                // Execute the request asynchronously
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activeRequests.decrementAndGet();
                        Log.e(TAG, "API request failed", e);

                        if (singleCallback != null) {
                            mainHandler.post(() -> singleCallback.onError("Network error: " + e.getMessage()));
                        } else {
                            notifyCallbacksOfError(cacheKey, "Network error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        activeRequests.decrementAndGet();

                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            Log.e(TAG, "API Error: " + errorBody);

                            if (singleCallback != null) {
                                mainHandler.post(() -> singleCallback.onError("API error: " + response.code()));
                            } else {
                                notifyCallbacksOfError(cacheKey, "API error: " + response.code());
                            }
                            return;
                        }

                        // Save the audio to a file
                        ResponseBody responseBody = response.body();
                        if (responseBody == null) {
                            if (singleCallback != null) {
                                mainHandler.post(() -> singleCallback.onError("Empty response from TTS API"));
                            } else {
                                notifyCallbacksOfError(cacheKey, "Empty response from TTS API");
                            }
                            return;
                        }

                        try {
                            // Create the cache file
                            File outputFile = getCacheFile(cacheKey);

                            // Save the audio data
                            FileOutputStream fos = new FileOutputStream(outputFile);
                            fos.write(responseBody.bytes());
                            fos.close();

                            // Add to memory cache
                            memoryCache.put(cacheKey, outputFile);

                            // Update disk cache index
                            updateCacheIndex(cacheKey, outputFile.length());

                            // Report success
                            if (singleCallback != null) {
                                mainHandler.post(() -> singleCallback.onSpeechReady(outputFile));
                            } else {
                                notifyCallbacksOfSuccess(cacheKey, outputFile);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error saving audio file", e);
                            if (singleCallback != null) {
                                mainHandler.post(() -> singleCallback.onError("Error saving audio: " + e.getMessage()));
                            } else {
                                notifyCallbacksOfError(cacheKey, "Error saving audio: " + e.getMessage());
                            }
                        }
                    }
                });

            } catch (JSONException e) {
                activeRequests.decrementAndGet();
                Log.e(TAG, "Error creating request", e);

                if (singleCallback != null) {
                    mainHandler.post(() -> singleCallback.onError("Error: " + e.getMessage()));
                } else {
                    notifyCallbacksOfError(cacheKey, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Compatibility method for playAudio
     * Uses the same signature as the original for compatibility
     */
    public void playAudio(File audioFile, Runnable onCompletion) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile.getPath());
            mediaPlayer.prepare();

            // Set completion listener
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                if (onCompletion != null) {
                    onCompletion.run();
                }
            });

            // Start playback
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Error playing audio", e);
        }
    }

    /**
     * Play an audio file and notify the callback
     */
    private void playAudioFile(File audioFile, TTSCallback callback) {
        // Notify that file is ready
        mainHandler.post(() -> callback.onSpeechReady(audioFile));

        // Add to the playback queue
        audioQueue.add(audioFile);

        // Start playing if not already playing
        if (!isPlaying.get()) {
            playNextInQueue();
        }
    }

    /**
     * Play the next audio file in the queue
     */
    private void playNextInQueue() {
        File nextFile = audioQueue.poll();
        if (nextFile == null) {
            isPlaying.set(false);
            return;
        }

        isPlaying.set(true);

        try {
            // Set up the media source
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, "ChessPedagogue");
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.fromFile(nextFile)));

            // Prepare and play
            player.setMediaSource(mediaSource);
            player.prepare();
            player.play();

            // Set a listener to notify when completed
            // In OpenAITTSService.java - find playNextInQueue()
            player.addListener(new Player.Listener() {
                boolean hasNotified = false;

                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_ENDED && !hasNotified) {
                        hasNotified = true;
                        Log.d(TAG, "🎵 Playback COMPLETED");
                        mainHandler.post(() -> {
                            // Try to get any pending callbacks

                            playNextInQueue(); // Continue with any queued audio
                        });
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error playing audio", e);
            isPlaying.set(false);
            playNextInQueue(); // Try the next file
        }
    }

    /**
     * Notify all waiting callbacks of success
     */
    private void notifyCallbacksOfSuccess(String cacheKey, File audioFile) {
        mainHandler.post(() -> {
            List<TTSCallback> callbacks = pendingCallbacks.remove(cacheKey);
            if (callbacks != null) {
                for (TTSCallback callback : callbacks) {
                    callback.onSpeechReady(audioFile);

                    // Queue the audio file for playback
                    audioQueue.add(audioFile);
                }

                // Start playing if not already
                if (!isPlaying.get()) {
                    playNextInQueue();
                }
            }
        });
    }

    /**
     * Notify all waiting callbacks of an error
     */
    private void notifyCallbacksOfError(String cacheKey, String errorMessage) {
        mainHandler.post(() -> {
            List<TTSCallback> callbacks = pendingCallbacks.remove(cacheKey);
            if (callbacks != null) {
                for (TTSCallback callback : callbacks) {
                    callback.onError(errorMessage);
                }
            }
        });
    }

    /**
     * Preload common chess phrases for instant playback
     */
    public void preloadCommonPhrases() {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.w(TAG, "Cannot preload phrases: API key not set");
            return;
        }

        executorService.execute(() -> {
            Log.d(TAG, "Preloading common chess phrases...");

            // Use different voices for variety
            String[] voices = {VOICE_GRANDMASTER, VOICE_COACH, VOICE_TUTOR};
            String model = MODEL_STANDARD; // Use standard model for preloading to save costs

            for (String phrase : COMMON_CHESS_PHRASES) {
                for (String voice : voices) {
                    String cacheKey = generateCacheKey(phrase, voice, model);

                    // Skip if already in cache
                    if (memoryCache.get(cacheKey) != null) continue;
                    File cacheFile = getCacheFile(cacheKey);
                    if (cacheFile.exists()) {
                        memoryCache.put(cacheKey, cacheFile);
                        continue;
                    }

                    // Make a low-priority request for this phrase
                    final String finalVoice = voice;
                    mainHandler.postDelayed(() -> {
                        requestTTS(phrase, finalVoice, model, cacheKey);
                    }, 2000); // Delay to spread out requests
                }
            }
        });
    }

    /**
     * Generate a cache key for a specific text/voice/model combination
     */
    private String generateCacheKey(String text, String voice, String model) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = text + voice + model;
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.NO_WRAP).replace('/', '_').replace('+', '-');
        } catch (Exception e) {
            Log.e(TAG, "Error generating cache key", e);
            return text.hashCode() + "_" + voice + "_" + model; // Fallback
        }
    }

    /**
     * Get a cache file for a specific key
     */
    private File getCacheFile(String cacheKey) {
        return new File(cacheDir, cacheKey + ".mp3");
    }

    /**
     * Load the disk cache index
     */
    private void loadCacheIndex() {
        executorService.execute(() -> {
            File[] files = cacheDir.listFiles((dir, name) -> name.endsWith(".mp3"));
            if (files != null) {
                for (File file : files) {
                    String key = file.getName().replace(".mp3", "");
                    long size = file.length();
                    diskCacheIndex.put(key, size);
                    diskCacheSize += size;
                }
            }

            // Clean up if cache is too large
            if (diskCacheIndex.size() > DISK_CACHE_MAX_SIZE || diskCacheSize > DISK_CACHE_MAX_BYTES) {
                cleanupCache();
            }
        });
    }

    /**
     * Update the disk cache index with a new file
     */
    private void updateCacheIndex(String key, long size) {
        diskCacheIndex.put(key, size);
        diskCacheSize += size;

        // Check if we need to clean up
        if (diskCacheIndex.size() > DISK_CACHE_MAX_SIZE || diskCacheSize > DISK_CACHE_MAX_BYTES) {
            executorService.execute(this::cleanupCache);
        }
    }

    /**
     * Clean up old cache files
     */
    private void cleanupCache() {
        // Simple LRU strategy - just remove oldest files
        File[] files = cacheDir.listFiles((dir, name) -> name.endsWith(".mp3"));
        if (files == null) return;

        // Sort by last modified time
        Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

        // Remove oldest files until we're under limits
        for (File file : files) {
            if (diskCacheIndex.size() <= DISK_CACHE_MAX_SIZE &&
                    diskCacheSize <= DISK_CACHE_MAX_BYTES) {
                break;
            }

            String key = file.getName().replace(".mp3", "");
            Long size = diskCacheIndex.remove(key);
            if (size != null) {
                diskCacheSize -= size;
            }

            if (file.delete()) {
                Log.d(TAG, "Removed cache file: " + file.getName());
            }
        }
    }

    /**
     * Stop all audio playback
     */
    public void stopPlayback() {
        player.stop();
        audioQueue.clear();
        isPlaying.set(false);
    }

    /**
     * Clean up resources
     */
    public void shutdown() {
        stopPlayback();
        player.release();
        executorService.shutdown();
    }
}