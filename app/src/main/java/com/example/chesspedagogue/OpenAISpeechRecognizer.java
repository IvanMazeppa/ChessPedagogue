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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenAISpeechRecognizer implements SpeechRecognizer {
    private static final String TAG = "OpenAISpeechRecognizer";
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private final Context context;
    private final UnifiedOpenAIService openAIService;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private SpeechCallback callback;
    private int bufferSize;

    public OpenAISpeechRecognizer(Context context) {
        this.context = context.getApplicationContext();
        this.openAIService = UnifiedOpenAIService.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Calculate buffer size
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2; // Default if error
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @Override
    public void startListening(SpeechCallback callback) {
        if (isRecording) {
            Log.d(TAG, "Already recording, ignoring start request");
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

                Log.d(TAG, "Started recording audio");

                // Recording loop
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, bufferSize);
                    if (read > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                }

                // Process the recorded audio
                byte[] audioData = outputStream.toByteArray();
                processAudio(audioData);

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

    private void processAudio(byte[] audioData) {
        if (audioData.length == 0) {
            Log.w(TAG, "No audio data to process");
            return;
        }

        Log.d(TAG, "Processing " + audioData.length + " bytes of audio data");

        // Use the UnifiedOpenAIService to transcribe the audio
        openAIService.transcribeAudio(audioData, new UnifiedOpenAIService.OpenAICallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onFinalResult(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("Transcription error: " + e.getMessage()));
                }
            }
        });
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
}