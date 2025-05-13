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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of TextToSpeechService using OpenAI's TTS API.
 */
public class OpenAITTSService implements TextToSpeechService {
    private static final String TAG = "OpenAITTSService";
    // Change this line in OpenAITTSService.java
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    public static final String MODEL_TTS = "gpt-4o-mini-tts";

    private final OkHttpClient httpClient;
    private String apiKey; // Not final so it can be set later
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;

    // Singleton instance
    private static OpenAITTSService instance;

    // Currently playing audio
    private MediaPlayer currentPlayer;
    private AudioTrack currentAudioTrack;

    // Request both text and voice in parallel
    //CompletableFuture<String> textFuture = CompletableFuture.supplyAsync(() -> getTextResponse());
    //CompletableFuture<File> audioFuture = CompletableFuture.supplyAsync(() -> getAudioResponse());

    // Voice options
    public static final String VOICE_GRANDMASTER = "onyx";  // Deeper, authoritative voice
    public static final String VOICE_TUTOR = "nova";        // Warmer, encouraging voice
    public static final String VOICE_SHIMMER = "shimmer";   // Cheerful voice
    public static final String VOICE_ECHO = "echo";         // Another option
    public static final String VOICE_ALLOY = "alloy";       // Another option
    public static final String MASTER = "onyx";             // Alias for backwards compatibility

    // Model options
    public static final String MODEL_STANDARD = "tts-1";    // Standard quality
    public static final String MODEL_PREMIUM = "tts-1-hd";  // Higher quality

    // Voice settings - default to grandmaster voice
    private String voice = VOICE_GRANDMASTER;
    private String model = MODEL_STANDARD;
    private final String responseFormat = "pcm";  // raw PCM for direct playback
    private final int sampleRate = 16000;   // match with AudioTrack configuration



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
     * Get the singleton instance of OpenAITTSService
     */
    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context);
        }
        return instance;
    }

    /**
     * Helper method to notify callback of errors on the main thread
     */
    private void notifyError(String message, TTSCallback callback) {
        if (callback != null) {
            mainHandler.post(() -> callback.onError(message));
        }
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
     * Constructor for the service
     */
    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.apiKey = null; // Will be set later
        this.httpClient = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void speakWithGPT4oMini(String text, TTSCallback callback) {
        // Show we're processing
        if (callback != null) {
            mainHandler.post(callback::onSpeechStarted);
        }

        // Process in background
        new Thread(() -> {
            try {
                // The correct endpoint for TTS
                String TTS_API_URL = "https://api.openai.com/v1/audio/speech";

                // Create the JSON payload for the TTS API - this is the key fix!
                JSONObject payload = new JSONObject();
                payload.put("model", "gpt-4o-mini-tts");
                payload.put("input", text);  // Use 'input' instead of messages array
                payload.put("voice", "echo");  // Specify a voice
                payload.put("response_format", "mp3");  // Request MP3 format for better compatibility
                // In your OpenAITTSService.java, modify the TTS API payload:
                payload.put("instructions",
                        "Speak with a deep, authoritative voice of an older chess grandmaster. Use a warm, natural tone with smooth inflections. Maintain consistent volume and clarity throughout. keep answers short and concise."
                );

                Log.d(TAG, "TTS request payload: " + payload.toString());

                // Build the HTTP request
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );

                Request request = new Request.Builder()
                        .url(TTS_API_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .post(body)
                        .build();

                // Execute the request
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "TTS API error: " + response.code());
                        if (response.body() != null) {
                            Log.e(TAG, "Error response: " + response.body().string());
                        }
                        notifyError("TTS error: " + response.code(), callback);
                        return;
                    }

                    // Save the audio to a temporary file
                    File audioFile = saveAudioToFile(response.body().bytes());

                    // Notify that we have the speech ready
                    if (callback != null) {
                        mainHandler.post(() -> callback.onSpeechReady(audioFile));
                    }

                    // Play the audio
                    playAudio(audioFile, () -> {
                        if (callback != null) {
                            mainHandler.post(callback::onSpeechCompleted);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in TTS", e);
                notifyError("TTS error: " + e.getMessage(), callback);
            }
        }).start();
    }

    /**
     * Constructor with API key
     */
    public OpenAITTSService(Context context, String apiKey) {
        this.context = context.getApplicationContext();
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Give more time to connect
                .writeTimeout(30, TimeUnit.SECONDS)    // Give more time to send data
                .readTimeout(90, TimeUnit.SECONDS)     // Give more time to receive the audio
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newSingleThreadExecutor();
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
     * Stop any ongoing audio playback
     */
    public void stopPlayback() {
        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {
                currentPlayer.stop();
            }
            currentPlayer.release();
            currentPlayer = null;
        }

        if (currentAudioTrack != null) {
            currentAudioTrack.stop();
            currentAudioTrack.release();
            currentAudioTrack = null;
        }
    }

    // In OpenAITTSService.java, let's try a different approach to playing audio
    private void playMp3Audio(File audioFile, Runnable onCompletion) {
        try {
            // Stop any currently playing audio
            stopPlayback();

            // Create a more robust MediaPlayer setup
            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
            );

            // Set data source with better error handling
            try {
                player.setDataSource(context, Uri.fromFile(audioFile));
                player.prepare();
            } catch (IOException e) {
                Log.e(TAG, "MediaPlayer preparation failed", e);
                return;
            }

            currentPlayer = player;

            // Set completion listener
            player.setOnCompletionListener(mp -> {
                // Important: release resources properly
                mp.release();
                currentPlayer = null;
                if (onCompletion != null) {
                    mainHandler.post(onCompletion);
                }
            });

            // Start playback
            player.start();
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio", e);
            if (onCompletion != null) {
                mainHandler.post(onCompletion);
            }
        }
    }



    /**
     * Speak text using the selected voice and model
     */
    public void speak(String text, String voice, String model, TTSCallback callback) {
        if (callback != null) {
            mainHandler.post(callback::onSpeechStarted);
        }

        executorService.execute(() -> {
            try {
                // Generate a temporary file to store the audio
                File cacheDir = new File(context.getCacheDir(), "tts_cache");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }

                String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
                File audioFile = new File(cacheDir, fileName);

                // Get audio data
                byte[] audioData = synthesizeSpeech(text);

                if (audioData == null || audioData.length == 0) {
                    if (callback != null) {
                        mainHandler.post(() -> callback.onError("Failed to generate speech audio"));
                    }
                    return;
                }

                // Save audio data to file
                try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                    fos.write(audioData);
                }

                if (callback != null) {
                    mainHandler.post(() -> callback.onSpeechReady(audioFile));
                }

                // Play the audio
                playAudio(audioFile, () -> {
                    if (callback != null) {
                        mainHandler.post(callback::onSpeechCompleted);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error in speak method", e);
                if (callback != null) {
                    final String errorMsg = e.getMessage();
                    mainHandler.post(() -> callback.onError("TTS Error: " + errorMsg));
                }
            }
        });
    }

    // Add this method to your OpenAITTSService class
    public void speakWithChunkingForGPT4oMini(String text, TTSCallback masterCallback) {
        // Create the chunker specifically for GPT-4o-mini-tts
        GPT4oMiniSpeechChunker chunker = new GPT4oMiniSpeechChunker(text, masterCallback);
        chunker.startSpeaking();
    }

    // Add this new inner class for GPT-4o-mini-tts chunking
    private class GPT4oMiniSpeechChunker {
        private final String[] chunks;
        private final TTSCallback masterCallback;
        private int currentChunkIndex = 0;
        private boolean retryAttempted = false;

        public GPT4oMiniSpeechChunker(String text, TTSCallback callback) {
            this.masterCallback = callback;

            // Split text into natural sentence boundaries
            String[] sentences = text.split("(?<=[.!?])\\s+");

            // Group sentences into chunks (1-2 sentences per chunk)
            List<String> chunkList = new ArrayList<>();
            StringBuilder currentChunk = new StringBuilder();

            for (String sentence : sentences) {
                // If adding this sentence would make chunk too large, finish current chunk
                if (currentChunk.length() + sentence.length() > 80) {
                    chunkList.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
                currentChunk.append(sentence).append(" ");
            }

            // Add the final chunk if not empty
            if (currentChunk.length() > 0) {
                chunkList.add(currentChunk.toString());
            }

            this.chunks = chunkList.toArray(new String[0]);

            // For debugging
            Log.d(TAG, "Split speech into " + chunks.length + " chunks for GPT-4o-mini-tts");
        }

        public void startSpeaking() {
            // Notify that speech is starting
            if (masterCallback != null) {
                mainHandler.post(masterCallback::onSpeechStarted);
            }

            // Start with the first chunk
            speakNextChunk();
        }

        private void speakNextChunk() {
            if (currentChunkIndex >= chunks.length) {
                // We've finished all chunks
                if (masterCallback != null) {
                    mainHandler.post(masterCallback::onSpeechCompleted);
                }
                return;
            }

            // This is the chunk we'll speak now
            String currentText = chunks[currentChunkIndex].replaceAll("\\.{3,}", ""); // Remove ellipses

            // Create a chunk callback
            TTSCallback chunkCallback = new TTSCallback() {
                @Override
                public void onSpeechStarted() {
                    // Only report start for the first chunk
                    if (currentChunkIndex == 0 && masterCallback != null) {
                        mainHandler.post(masterCallback::onSpeechStarted);
                    }
                }

                @Override
                public void onSpeechReady(File audioFile) {
                    // Pass through to master callback
                    if (masterCallback != null) {
                        mainHandler.post(() -> masterCallback.onSpeechReady(audioFile));
                    }
                }

                @Override
                public void onSpeechCompleted() {
                    // Move to next chunk after a short delay
                    mainHandler.post(() -> {
                        Log.d(TAG, "Completed GPT-4o-mini-tts chunk " + currentChunkIndex + ", preparing next chunk");
                        currentChunkIndex++;
                        mainHandler.postDelayed(() -> speakNextChunk(), 250);
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Error speaking GPT-4o-mini-tts chunk " + currentChunkIndex + ": " + errorMessage);

                    // Add retry mechanism
                    if (!retryAttempted) {
                        retryAttempted = true;
                        Log.d(TAG, "Attempting to retry GPT-4o-mini-tts chunk " + currentChunkIndex);
                        mainHandler.postDelayed(() -> speakNextChunk(), 500);
                    } else {
                        // Move to next chunk if retry failed
                        currentChunkIndex++;
                        retryAttempted = false;
                        speakNextChunk();
                    }
                }
            };

            // Use the GPT-4o-mini-tts specific method for this chunk
            speakWithGPT4oMini(currentText, chunkCallback);
        }
    }

    /**
     * Speak text using chunking for improved responsiveness
     */
    public void speakWithChunking(String text, TTSCallback masterCallback) {
        // Create the chunker that will manage speaking segments
        SpeechChunker chunker = new SpeechChunker(text, masterCallback);
        chunker.startSpeaking();
    }

    /**
     * Helper class to manage chunked speech
     */
    // Then add the SpeechChunker inner class:
    private class SpeechChunker {
        private final String[] chunks;
        private final TTSCallback masterCallback;
        private int currentChunkIndex = 0;
        private boolean retryAttempted = false;
        private final String currentVoice;  // Add this!
        private final String currentModel;  // Add this!

        public SpeechChunker(String text, TTSCallback callback) {
            this.masterCallback = callback;
            this.currentVoice = voice;
            this.currentModel = model;

            List<String> chunkList = new ArrayList<>();

            // First, convert the text to be more conversational
            String conversationalText = text
                    // Convert headings to conversational phrases
                    .replaceAll("\\*\\*(.+):\\*\\*", "Let me tell you about $1.")
                    .replaceAll("\\*\\*(.+)\\*\\*", "$1")

                    // Convert bullet points to natural speech
                    .replaceAll("\\n\\s*-\\s*\\*(.+)\\*\\s*", ". First, $1. ")
                    .replaceAll("\\n\\s*-\\s*", ". Also, ")

                    // Remove markdown formatting
                    .replaceAll("\\*", "")

                    // Convert newlines to spaces
                    .replaceAll("\\n\\s*", " ")

                    // Fix any double periods
                    .replaceAll("\\.\\.", ".")
                    .replaceAll("\\. \\.", ".")

                    // Add natural pauses after sentences
                    .replaceAll("\\. ", ". [pause] ")

                    .trim();

            // Split into natural speaking segments (aim for ~15 second chunks)
            if (conversationalText.length() < 150) {
                // Short response - just use as one chunk
                chunkList.add(conversationalText);
            } else {
                // Split at sentence boundaries for longer responses
                String[] sentences = conversationalText.split("\\[pause\\]\\s+");

                StringBuilder currentChunk = new StringBuilder();
                for (String sentence : sentences) {
                    // Start a new chunk if this would make it too long
                    // Aim for chunks that would take about 10-15 seconds to speak
                    if (currentChunk.length() > 0 &&
                            currentChunk.length() + sentence.length() > 200) {

                        chunkList.add(currentChunk.toString().trim());
                        currentChunk = new StringBuilder();
                    }

                    currentChunk.append(sentence).append(" ");
                }

                // Add the final chunk
                if (currentChunk.length() > 0) {
                    chunkList.add(currentChunk.toString().trim());
                }
            }

            // Final safety check - never allow empty chunks
            for (int i = chunkList.size() - 1; i >= 0; i--) {
                if (chunkList.get(i).isEmpty()) {
                    chunkList.remove(i);
                }
            }

            this.chunks = chunkList.toArray(new String[0]);
            Log.d(TAG, "Split speech into " + chunkList.size() + " conversational chunks");
        }

        public void startSpeaking() {
            // Notify that speech is starting
            if (masterCallback != null) {
                mainHandler.post(masterCallback::onSpeechStarted);
            }

            // Start with the first chunk
            speakNextChunk();
        }

        private void speakNextChunk() {
            if (currentChunkIndex >= chunks.length) {
                // We've finished all chunks
                if (masterCallback != null) {
                    mainHandler.post(masterCallback::onSpeechCompleted);
                }
                return;
            }

            // This is the chunk we'll speak now - REMOVE ELLIPSES
            String currentText = chunks[currentChunkIndex].replaceAll("\\.{3,}", ""); // Remove ellipses

            // Create a new MediaPlayer for each chunk to avoid resource issues
            OpenAITTSService ttsService = new OpenAITTSService(context, apiKey);

            // Set up a callback for just this chunk
            TTSCallback chunkCallback = new TTSCallback() {
                @Override
                public void onSpeechStarted() {
                    // Only report start for the first chunk
                    if (currentChunkIndex == 0 && masterCallback != null) {
                        mainHandler.post(masterCallback::onSpeechStarted);
                    }
                }

                @Override
                public void onSpeechReady(File audioFile) {
                    // Pass through to master callback
                    if (masterCallback != null) {
                        mainHandler.post(() -> masterCallback.onSpeechReady(audioFile));
                    }
                }

                @Override
                public void onSpeechCompleted() {
                    // Use a handler to ensure we're on the main thread
                    mainHandler.post(() -> {
                        Log.d(TAG, "Completed chunk " + currentChunkIndex + ", preparing next chunk");
                        currentChunkIndex++;
                        // Important: add a longer delay to ensure resources are properly released
                        mainHandler.postDelayed(() -> speakNextChunk(), 100); // Increased from 100ms to 250ms
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Error speaking chunk " + currentChunkIndex + ": " + errorMessage);

                    // Add RETRY mechanism - try this chunk again once before giving up
                    if (!retryAttempted) {
                        retryAttempted = true;
                        Log.d(TAG, "Attempting to retry chunk " + currentChunkIndex);
                        mainHandler.postDelayed(() -> speakNextChunk(), 500);
                    } else {
                        // Move to next chunk if retry failed
                        currentChunkIndex++;
                        retryAttempted = false;
                        speakNextChunk();
                    }
                }
            };

            // Speak this chunk
            ttsService.speak(currentText, currentVoice, currentModel, chunkCallback);
        }
    }

    /**
     * Play audio file and call completion handler when done
     */
    public void playAudio(File audioFile, Runnable onCompletion) {
        try {
            // Stop any currently playing audio
            stopPlayback();

            // Log file details for debugging
            Log.d(TAG, "Playing audio file: " + audioFile.getAbsolutePath() +
                    ", size: " + audioFile.length() + " bytes, exists: " + audioFile.exists());

            // Create and configure MediaPlayer with better error handling
            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
            );

            // Set on error listener BEFORE preparing
            player.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
                mp.release();
                currentPlayer = null;
                if (onCompletion != null) {
                    mainHandler.post(onCompletion);
                }
                return true; // Error handled
            });

            // Set data source with better error handling
            player.setDataSource(context, Uri.fromFile(audioFile));
            player.prepare();

            currentPlayer = player;

            // Set completion listener
            player.setOnCompletionListener(mp -> {
                // Important: release resources properly
                mp.release();
                currentPlayer = null;
                if (onCompletion != null) {
                    mainHandler.post(onCompletion);
                }
            });

            // Start playback
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "Error playing audio", e);
            if (onCompletion != null) {
                mainHandler.post(onCompletion);
            }
        }
    }

    /**
     * Play PCM audio data directly using AudioTrack
     */
    public void playPCMAudio(byte[] audioData, int sampleRate, Runnable onCompletion) {
        try {
            // Stop any currently playing audio
            stopPlayback();

            // Create and configure AudioTrack
            int minBufferSize = AudioTrack.getMinBufferSize(sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            currentAudioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .setBufferSizeInBytes(Math.max(minBufferSize, audioData.length))
                    .build();

            currentAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack track) {
                    track.release();
                    currentAudioTrack = null;
                    if (onCompletion != null) {
                        mainHandler.post(onCompletion);
                    }
                }

                @Override
                public void onPeriodicNotification(AudioTrack track) {
                    // Not used
                }
            });

            // Start playback
            currentAudioTrack.play();
            currentAudioTrack.write(audioData, 0, audioData.length);
            currentAudioTrack.setNotificationMarkerPosition(audioData.length / 4); // Position is in frames

        } catch (Exception e) {
            Log.e(TAG, "Error playing PCM audio", e);
            if (onCompletion != null) {
                mainHandler.post(onCompletion);
            }
        }
    }

    @Override
    public byte[] synthesizeSpeech(String text) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return new byte[0];
        }

        // Create JSON payload for TTS request with your custom settings
        JSONObject payload = new JSONObject();
        try {
            // Use the exact model name for gpt-4o-mini-tts
            payload.put("model", "gpt-4o-mini-tts");
            payload.put("input", text);
            payload.put("voice", voice);
            payload.put("response_format", "mp3");  // Changed to mp3 for better compatibility

            // Add your custom instructions for the chess grandmaster character
            payload.put("instructions", "Speak with a deep, authoritative voice of an older chess grandmaster");

            // Log the exact payload for debugging
            Log.d(TAG, "TTS API payload: " + payload.toString());
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
        if (!response.isSuccessful()) {
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
}