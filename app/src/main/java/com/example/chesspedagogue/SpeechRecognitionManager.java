package com.example.chesspedagogue;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Compatibility class that bridges between old and new speech recognition implementations.
 * This class implements the new SpeechRecognizer interface while maintaining
 * the old SpeechRecognitionManager API.
 */
public class SpeechRecognitionManager implements SpeechRecognizer {
    private static final String TAG = "SpeechRecognitionManager";

    private final Context context;
    private final OpenAISpeechRecognizer modernRecognizer;
    private final Handler mainHandler;
    private final List<SpeechRecognitionCallback> callbacks = new ArrayList<>();
    private boolean isListening = false;

    // Old callback interface - kept for backward compatibility
    public interface SpeechRecognitionCallback {
        void onSpeechRecognized(String text);
        void onSpeechError(String error);
    }

    public SpeechRecognitionManager(Context context) {
        this.context = context;
        this.modernRecognizer = new OpenAISpeechRecognizer(context);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // Old API method
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void startListening(SpeechRecognitionCallback callback) {
        if (callback != null && !callbacks.contains(callback)) {
            callbacks.add(callback);
        }

        // Use the new implementation
        modernRecognizer.startListening(new SpeechCallback() {
            @Override
            public void onPartialResult(String partialText) {
                // Old API didn't have partial results, so we ignore this
            }

            @Override
            public void onFinalResult(String finalText) {
                // Notify all registered callbacks
                for (SpeechRecognitionCallback callback : callbacks) {
                    mainHandler.post(() -> callback.onSpeechRecognized(finalText));
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Notify all registered callbacks
                for (SpeechRecognitionCallback callback : callbacks) {
                    mainHandler.post(() -> callback.onSpeechError(errorMessage));
                }
            }
        });

        isListening = true;
    }

    // New API method from SpeechRecognizer interface
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @Override
    public void startListening(SpeechCallback callback) {
        modernRecognizer.startListening(callback);
        isListening = true;
    }

    @Override
    public void stopListening() {
        modernRecognizer.stopListening();
        isListening = false;
    }

    @Override
    public boolean isListening() {
        return isListening;
    }

    @Override
    public void release() {
        stopListening();
        callbacks.clear();
        modernRecognizer.release();
    }

    /**
     * Simulate recognized speech (for text input)
     */
    public void simulateRecognizedSpeech(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        // Notify all registered callbacks
        for (SpeechRecognitionCallback callback : callbacks) {
            mainHandler.post(() -> callback.onSpeechRecognized(text));
        }
    }
}