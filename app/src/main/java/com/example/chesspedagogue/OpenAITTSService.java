package com.example.chesspedagogue;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service for generating natural-sounding speech using OpenAI's TTS API.
 */
public class OpenAITTSService {
    private static final String TAG = "OpenAITTSService";
    private static final String API_URL = "https://api.openai.com/v1/audio/speech";

    // Voice options to match different personas
    public static final String VOICE_GRANDMASTER = "onyx";  // Deeper, authoritative voice
    public static final String VOICE_TUTOR = "nova";        // Warmer, encouraging voice
    public static final String VOICE_ALLOY = "alloy";       // Neutral voice
    public static final String VOICE_SHIMMER = "shimmer";   // Cheerful voice for younger players
    public static final String VOICE_ECHO = "echo";         // Another option

    // Voice quality models
    public static final String MODEL_STANDARD = "tts-1";            // Lower cost, good quality
    public static final String MODEL_PREMIUM = "tts-1-hd";          // Higher quality, higher cost

    private final OkHttpClient client;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context;
    private String apiKey;

    // Callbacks for TTS operations
    public interface TTSCallback {
        void onSpeechReady(File audioFile);
        void onError(String errorMessage);
    }

    /**
     * Create a new OpenAI TTS Service instance
     */
    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.client = new OkHttpClient.Builder().build();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Set the API key for OpenAI
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Generate speech from text using OpenAI's API
     */
    public void synthesizeSpeech(String text, String voice, String model, TTSCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            mainHandler.post(() -> callback.onError("API key not set"));
            return;
        }

        // Run on background thread
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

                // Execute the request
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "API Error: " + errorBody);
                    mainHandler.post(() -> callback.onError("Failed to generate speech: " + response.code()));
                    return;
                }

                // Save the audio to a file
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    mainHandler.post(() -> callback.onError("Empty response from TTS API"));
                    return;
                }

                // Create a temporary file to store the audio
                File outputDir = context.getCacheDir();
                File outputFile = File.createTempFile("tts_", ".mp3", outputDir);

                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(responseBody.bytes());
                fos.close();

                // Return the file on the main thread
                mainHandler.post(() -> callback.onSpeechReady(outputFile));

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error synthesizing speech", e);
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    /**
     * Helper method to play audio file
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
     * Clean up resources
     */
    public void shutdown() {
        executorService.shutdown();
    }
}