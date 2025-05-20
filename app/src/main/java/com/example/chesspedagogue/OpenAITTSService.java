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
import java.util.List;
import java.util.UUID;
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
public class OpenAITTSService {
    public static final String MODEL_TTS = "gpt-4o-mini-tts";
    // Voice options
    public static final String VOICE_GRANDMASTER = "onyx";  // Deeper, authoritative voice
    public static final String VOICE_TUTOR = "nova";        // Warmer, encouraging voice
    public static final String VOICE_SHIMMER = "shimmer";   // Cheerful voice
    public static final String VOICE_ECHO = "echo";         // Another option
    public static final String VOICE_ALLOY = "alloy";       // Another option
    // Add these to OpenAITTSService.java
    public static final String VOICE_FABLE = "fable";     // British male
    public static final String VOICE_ONYX = "onyx";       // Deep, authoritative
    public static final String VOICE_NOVA = "nova";       // Female voice
    public static final String MASTER = "onyx";             // Alias for backwards compatibility
    // Model options
    public static final String MODEL_STANDARD = "tts-1";    // Standard quality

    // Request both text and voice in parallel
    //CompletableFuture<String> textFuture = CompletableFuture.supplyAsync(() -> getTextResponse());
    //CompletableFuture<File> audioFuture = CompletableFuture.supplyAsync(() -> getAudioResponse());
    public static final String MODEL_PREMIUM = "tts-1-hd";  // Higher quality
    private static final String TAG = "OpenAITTSService";
    // Change this line in OpenAITTSService.java
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    // In the splitTextIntoChunks method or at the class level
    private static final int MAX_CHARS_PER_CHUNK = 250; // Increase from current value
    // or
    private static final int MAX_SENTENCES_PER_CHUNK = 3; // If you're splitting by sentences
    // Singleton instance
    private static OpenAITTSService instance;
    private final OkHttpClient httpClient;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private final String responseFormat = "pcm";  // raw PCM for direct playback
    private final int sampleRate = 16000;   // match with AudioTrack configuration
    private String apiKey; // Not final so it can be set later
    // Currently playing audio
    private MediaPlayer currentPlayer;
    private AudioTrack currentAudioTrack;
    // Voice settings - default to grandmaster voice
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
        this.httpClient = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Constructor with API key
     */
    // In OpenAITTSService constructor
    public OpenAITTSService(Context context, String apiKey) {
        this.context = context.getApplicationContext();

        // First, set the API key in the shared client
        OpenAIClient sharedClient = OpenAIClient.getInstance();
        sharedClient.setApiKey(apiKey);

        // Store it locally too, for safety during transition
        this.apiKey = apiKey;

        // Use the shared HTTP client
        this.httpClient = sharedClient.getHttpClient();

        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newSingleThreadExecutor();

        Log.d(TAG, "OpenAITTSService initialized with API key length: " +
                (apiKey != null ? apiKey.length() : 0));
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
    // Add this method
    public void stopPlayback() {

        Log.d(TAG, "🚫 stopPlayback() called from: " +
                new Exception().getStackTrace()[1].getClassName() + " line " +
                new Exception().getStackTrace()[1].getLineNumber());

        // Set the interrupt flag
        interruptRequested = true;

        // Stop any currently playing audio
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

    /**
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
    **/

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
     * Get the appropriate voice instructions based on settings
     */
    private String getVoiceInstructions(String selectedMaster) {
        // If personality is disabled, use minimal instructions
        if (!usePersonalityInstructions) {
            return "Speak naturally and clearly.";
        }

        // Otherwise use master-specific instructions
        return ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
    }

    /**
    // Replace this problematic method with a proper implementation
    public void generateSpeech(String text, TTSCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onError("API key not set");
            return;
        }

        // Use executorService instead of executor
        executorService.execute(() -> {
            try {
                // Get the selected chess master
                String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();

                // Get the appropriate voice based on settings
                String voiceId = getVoiceId(selectedMaster);

                // Get appropriate instructions based on settings
                String voiceInstructions = getVoiceInstructions(selectedMaster);

                // Log for debugging
                Log.d(TAG, "Using voice: " + voiceId + " with instructions: " + voiceInstructions);

                // Use the chunking approach we've already implemented
                speakWithChunking(text, callback);

            } catch (Exception e) {
                Log.e(TAG, "Error generating speech: " + e.getMessage());
                // Use mainHandler instead of handler
                mainHandler.post(() -> callback.onError("Error generating speech: " + e.getMessage()));
            }
        });
    }

    // In your OpenAITTSService.java generateSpeech method

    /**
     * Speak text using the selected voice and model
     */
    public void speak(String text, String voice, String model, TTSCallback callback) {

        Log.d(TAG, "🔍 METHOD CALLED: speak");
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


    /**
     * Speak text using chunking for improved responsiveness
     */
    public void speakWithChunking(String text, TTSCallback masterCallback) {
        Log.d(TAG, "🔴 PATH CHECK: speakWithChunking called - uses SpeechChunker");

        // Create the chunker that will manage speaking segments
        SpeechChunker chunker = new SpeechChunker(text, masterCallback);
        chunker.startSpeaking();
    }

    /**
     * Play audio file and call completion handler when done
     **/
    /**
     * Improved audio playback for smoother transitions
     */
    public void playAudio(File audioFile, Runnable onCompletion) {
        try {
            // Stop any currently playing audio
            stopPlayback();

            interruptRequested = false;

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

            // IMPROVED: Optimized completion handling for smoother transitions
            player.setOnCompletionListener(mp -> {
                // Important: release resources properly
                mp.release();
                currentPlayer = null;
                // IMPROVEMENT: Remove delay for faster transitions between chunks
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
    **/

    public byte[] synthesizeSpeech(String text) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return new byte[0];
        }

        String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();

        // Create JSON payload for TTS request with your custom settings
        JSONObject payload = new JSONObject();
        try {
            // Use the exact model name for gpt-4o-mini-tts
            payload.put("model", "gpt-4o-mini-tts");
            payload.put("input", text);
            payload.put("voice", voice);
            payload.put("response_format", "mp3");  // Changed to mp3 for better compatibility

            // Add your custom instructions for the chess grandmaster character
            String masterInstructions = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
            payload.put("instructions", masterInstructions);

            // Log the exact payload for debugging
            Log.d(TAG, "TTS API payload: " + selectedMaster + ": " + payload);
        } catch (JSONException e) {
            Log.e(TAG, "TTS JSON construction error", e);
            return new byte[0];
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());


        // Change to use both for safety during transition:
        Request request = new Request.Builder()
                .url(TTS_URL)
                .header("Authorization", "Bearer " + (apiKey != null ? apiKey : OpenAIClient.getInstance().getAuthorizationHeader()))
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

    /**
     * Callback interface for TTS operations
     */
    public interface TTSCallback {
        void onSpeechStarted();

        void onSpeechReady(File audioFile);

        void onSpeechCompleted();

        void onError(String errorMessage);
    }

    private class SpeechChunker {
        private final String[] chunks;
        private final TTSCallback masterCallback;
        private final String currentVoice;
        private final String currentModel;
        private int currentChunkIndex = 0;
        private boolean retryAttempted = false;

        // NEW: Track the end of each chunk for better continuity
        private String lastChunkEnding = "";

        public SpeechChunker(String text, TTSCallback callback) {
            this.masterCallback = callback;
            this.currentVoice = voice;
            this.currentModel = model;

            List<String> chunkList = new ArrayList<>();

            // IMPROVED: Better sentence boundary detection for natural chunks
            // Don't split mid-thought or at awkward points
            String[] sentences = text.split("(?<=[.!?])\\s+");
            StringBuilder currentChunk = new StringBuilder();
            int currentSize = 0;
            int targetSize = 150; // Target around 150 characters per chunk

            for (String sentence : sentences) {
                // Don't split mid-sentence, and ensure chunks aren't too small
                if (currentSize + sentence.length() > targetSize && currentSize > 50) {
                    chunkList.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                    currentSize = 0;
                }

                currentChunk.append(sentence).append(" ");
                currentSize += sentence.length();
            }

            // Add the final chunk
            if (currentChunk.length() > 0) {
                chunkList.add(currentChunk.toString().trim());
            }

            // Add log for better debugging
            this.chunks = chunkList.toArray(new String[0]);
            Log.d(TAG, "Split speech into " + chunkList.size() + " natural chunks");
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
            // Check if we're done or interrupted
            if (interruptRequested || currentChunkIndex >= chunks.length) {
                if (interruptRequested) {
                    Log.d(TAG, "Speech interrupted, stopping chunk processing");
                    interruptRequested = false; // Reset for next speech
                }

                if (masterCallback != null) {
                    mainHandler.post(masterCallback::onSpeechCompleted);
                }
                return;
            }

            // Get current chunk text (with ellipses removed)
            String currentText = chunks[currentChunkIndex].replaceAll("\\.{3,}", "");

            // NEW: Save the last ~30 characters of this chunk for continuity
            if (currentText.length() > 30) {
                lastChunkEnding = currentText.substring(currentText.length() - 30);
            } else {
                lastChunkEnding = currentText;
            }

            // Create a callback for this chunk
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
                    // Move to next chunk with minimal delay
                    mainHandler.post(() -> {
                        Log.d(TAG, "Completed chunk " + currentChunkIndex + ", preparing next chunk");
                        currentChunkIndex++;
                        // IMPROVED: Reduced delay between chunks from 100ms to 50ms
                        mainHandler.postDelayed(() -> speakNextChunk(), 50);
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Error speaking chunk " + currentChunkIndex + ": " + errorMessage);

                    // Add retry mechanism
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

            // Execute the TTS request on a background thread
            executorService.execute(() -> {
                try {
                    // IMPROVED: Get selected chess master for voice personalization
                    String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();

                    // Create TTS request
                    JSONObject payload = new JSONObject();
                    payload.put("model", "gpt-4o-mini-tts");
                    payload.put("input", currentText);

                    // Get the master and appropriate voice
                    String voiceId = getVoiceId(selectedMaster);
                    payload.put("voice", voiceId);
                    payload.put("response_format", "mp3");

                    // IMPROVED: Enhanced continuity instructions
                    String baseInstruction = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
                    String instructions;

                    if (currentChunkIndex == 0) {
                        // First chunk - basic instruction
                        instructions = baseInstruction;
                        Log.d(TAG, "Using basic instruction for first chunk: " + instructions);
                    } else {
                        // GREATLY IMPROVED: Detailed continuity instructions with last chunk ending
                        instructions = baseInstruction +
                                " CRITICAL: Continue with the EXACT same voice, accent, pacing, and emotional tone." +
                                " This is a direct continuation where you just finished saying: \"" + lastChunkEnding + "\"";
                        Log.d(TAG, "Enhanced continuity for chunk " + currentChunkIndex + " with reference to previous speech");
                    }

                    payload.put("instructions", instructions);

                    // Build the HTTP request
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
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "TTS API error: " + response.code());
                            if (response.body() != null) {
                                Log.e(TAG, "Error response: " + response.body().string());
                            }
                            mainHandler.post(() -> chunkCallback.onError("TTS error: " + response.code()));
                            return;
                        }

                        // Save the audio to a temporary file
                        File audioFile = saveAudioToFile(response.body().bytes());

                        // Notify that audio is ready
                        mainHandler.post(() -> chunkCallback.onSpeechReady(audioFile));

                        // Play the audio
                        Log.d(TAG, "🔍 METHOD CALLED: playAudio");
                        playAudio(audioFile, () -> mainHandler.post(chunkCallback::onSpeechCompleted));
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error in TTS processing: " + e.getMessage(), e);
                    mainHandler.post(() -> chunkCallback.onError("TTS error: " + e.getMessage()));
                }
            });
        }
    }
}