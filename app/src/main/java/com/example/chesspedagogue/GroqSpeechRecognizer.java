package com.example.chesspedagogue;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Consolidated ultra-fast speech recognizer using Groq
 * Merged functionality from SpeechRecognitionManager
 */
public class GroqSpeechRecognizer implements SpeechRecognizer {
    private static final String TAG = "GroqSpeechRecognizer";
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // Groq API endpoint
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/audio/transcriptions";
    private static final String GROQ_MODEL = "whisper-large-v3-turbo";
    private static final String GROQ_API_KEY_FALLBACK = "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";

    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final OkHttpClient httpClient;
    
    /**
     * Get Groq API key from environment variable or fallback to hardcoded
     */
    private String getGroqApiKey() {
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            Log.d(TAG, "Using environment variable GROQ_API_KEY");
            return apiKey;
        } else {
            Log.d(TAG, "Using hardcoded fallback GROQ_API_KEY");
            return GROQ_API_KEY_FALLBACK;
        }
    }

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private SpeechCallback callback;
    private int bufferSize;

    // Old callback interface support for backward compatibility
    public interface SpeechRecognitionCallback {
        void onSpeechRecognized(String text);
        void onSpeechError(String error);
    }

    public GroqSpeechRecognizer(Context context) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Use shared HTTP client if available
        this.httpClient = OpenAIService.getInstance().getHttpClient();

        // Calculate buffer size
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }
    }

    // Support old API for backward compatibility
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void startListening(SpeechRecognitionCallback oldCallback) {
        startListening(new SpeechCallback() {
            @Override
            public void onPartialResult(String partialText) {
                // Old API didn't have partial results
            }

            @Override
            public void onFinalResult(String finalText) {
                mainHandler.post(() -> oldCallback.onSpeechRecognized(finalText));
            }

            @Override
            public void onError(String errorMessage) {
                mainHandler.post(() -> oldCallback.onSpeechError(errorMessage));
            }
        });
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @Override
    public void startListening(SpeechCallback callback) {
        if (isRecording) {
            Log.d(TAG, "Already recording, ignoring start request");
            return;
        }

        String apiKey = getGroqApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Groq API key not configured"));
            }
            return;
        }

        this.callback = callback;

        executorService.execute(() -> {
            try {
                // Initialize audio recorder
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

                if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onError("Failed to initialize AudioRecord");
                        }
                    });
                    return;
                }

                // Start recording
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[bufferSize];
                audioRecord.startRecording();
                isRecording = true;

                Log.d(TAG, "Started ultra-fast Groq recording");

                // Recording loop
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, bufferSize);
                    if (read > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                }

                // Process the recorded audio with Groq
                byte[] audioData = outputStream.toByteArray();
                processAudioWithGroq(audioData);

            } catch (Exception e) {
                Log.e(TAG, "Error recording audio", e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("Recording error: " + e.getMessage());
                    }
                });
            }
        });
    }

    private void processAudioWithGroq(byte[] audioData) {
        if (audioData.length == 0) {
            Log.w(TAG, "No audio data to process");
            return;
        }

        Log.d(TAG, "Processing " + audioData.length + " bytes with Groq");
        long startTime = System.currentTimeMillis();

        try {
            // Convert PCM to WAV format
            byte[] wavData = convertPcmToWav(audioData, SAMPLE_RATE, 1, 16);

            // Save to temporary file
            File tempFile = File.createTempFile("groq_audio_", ".wav", context.getCacheDir());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(wavData);
            }

            // Create multipart request for Groq
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", tempFile.getName(),
                            RequestBody.create(MediaType.parse("audio/wav"), tempFile))
                    .addFormDataPart("model", GROQ_MODEL)
                    .addFormDataPart("response_format", "json")
                    .addFormDataPart("language", "en")
                    .addFormDataPart("temperature", "0.0")
                    .addFormDataPart("prompt", "Chess game conversation")
                    .build();

            Request request = new Request.Builder()
                    .url(GROQ_API_URL)
                    .header("Authorization", "Bearer " + getGroqApiKey())
                    .post(requestBody)
                    .build();

            // Execute request
            try (Response response = httpClient.newCall(request).execute()) {
                long processingTime = System.currentTimeMillis() - startTime;
                Log.d(TAG, "Groq transcription completed in " + processingTime + "ms!");

                if (response.isSuccessful() && response.body() != null) {
                    String responseJson = response.body().string();
                    JSONObject json = new JSONObject(responseJson);
                    String transcribedText = json.getString("text");

                    Log.d(TAG, "Transcribed: " + transcribedText);

                    if (callback != null) {
                        mainHandler.post(() -> callback.onFinalResult(transcribedText));
                    }
                } else {
                    String error = "Groq API error: " + response.code();
                    if (response.body() != null) {
                        error += " - " + response.body().string();
                    }
                    Log.e(TAG, error);
                    final String errorMsg = error;

                    if (callback != null) {
                        mainHandler.post(() -> callback.onError(errorMsg));
                    }
                }
            }

            // Clean up temp file
            tempFile.delete();

        } catch (Exception e) {
            Log.e(TAG, "Error processing with Groq", e);
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Groq processing error: " + e.getMessage()));
            }
        }
    }

    @Override
    public void stopListening() {
        if (!isRecording) {
            return;
        }

        isRecording = false;

        // Stop and release AudioRecord
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping AudioRecord", e);
            } finally {
                audioRecord = null;
            }
        }

        Log.d(TAG, "Stopped recording audio");
    }

    @Override
    public boolean isListening() {
        return isRecording;
    }

    @Override
    public void release() {
        stopListening();
        executorService.shutdown();
    }

    /**
     * Simulate recognized speech (for testing)
     */
    public void simulateRecognizedSpeech(String text) {
        if (text == null || text.isEmpty() || callback == null) {
            return;
        }

        mainHandler.post(() -> callback.onFinalResult(text));
    }

    /**
     * Convert PCM audio data to WAV format
     */
    private byte[] convertPcmToWav(byte[] pcmData, int sampleRate, int channels, int bitsPerSample) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WAV header fields
        int dataLength = pcmData.length;
        int riffChunkSize = 36 + dataLength;
        int byteRate = sampleRate * channels * (bitsPerSample / 8);
        short blockAlign = (short) (channels * (bitsPerSample / 8));

        // Write RIFF header
        writeBytes(out, "RIFF".getBytes());
        writeInt(out, riffChunkSize);
        writeBytes(out, "WAVE".getBytes());

        // fmt subchunk
        writeBytes(out, "fmt ".getBytes());
        writeInt(out, 16); // PCM format chunk size
        writeShort(out, (short) 1); // audio format 1 = PCM
        writeShort(out, (short) channels);
        writeInt(out, sampleRate);
        writeInt(out, byteRate);
        writeShort(out, blockAlign);
        writeShort(out, (short) bitsPerSample);

        // data subchunk
        writeBytes(out, "data".getBytes());
        writeInt(out, dataLength);
        writeBytes(out, pcmData);

        return out.toByteArray();
    }

    // Helper methods for WAV header writing
    private void writeInt(ByteArrayOutputStream out, int value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    private void writeShort(ByteArrayOutputStream out, short value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    private void writeBytes(ByteArrayOutputStream out, byte[] data) {
        try {
            out.write(data);
        } catch (IOException e) {
            Log.e(TAG, "Error writing bytes", e);
        }
    }
}