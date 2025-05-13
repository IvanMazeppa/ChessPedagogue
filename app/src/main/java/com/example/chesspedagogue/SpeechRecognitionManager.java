package com.example.chesspedagogue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enhanced speech recognition manager with support for continuous listening,
 * voice activity detection, and interruption capabilities
 */
public class SpeechRecognitionManager {
    private static final String TAG = "SpeechRecognition";

    private final Context context;
    private final Handler mainHandler;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private final CopyOnWriteArrayList<SpeechRecognitionCallback> callbacks = new CopyOnWriteArrayList<>();

    // Speech recognition callback interface
    public interface SpeechRecognitionCallback {
        void onSpeechRecognized(String text);
        void onSpeechError(String error);
    }

    /**
     * Create a new speech recognition manager
     */
    public SpeechRecognitionManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        initializeSpeechRecognizer();
    }

    /**
     * Initialize the speech recognizer
     */
    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            Log.d(TAG, "Speech recognition available and initialized");
        } else {
            Log.e(TAG, "Speech recognition not available on this device");
        }
    }

    /**
     * Start listening for speech
     */
    public void startListening(SpeechRecognitionCallback callback) {
        if (callback != null && !callbacks.contains(callback)) {
            callbacks.add(callback);
        }

        startListeningInternal();
    }

    /**
     * Internal method to start the speech recognizer
     */
    private void startListeningInternal() {
        if (speechRecognizer == null) {
            Log.e(TAG, "Speech recognizer not initialized");
            notifyError("Speech recognition not available on this device");
            return;
        }

        if (isListening) {
            stopListening();
        }

        try {
            // Configure recognition intent
            Intent intent = createRecognizerIntent();

            // Set up recognition listener
            speechRecognizer.setRecognitionListener(createRecognitionListener());

            // Start listening
            speechRecognizer.startListening(intent);
            isListening = true;
            Log.d(TAG, "Started listening for speech");
        } catch (Exception e) {
            Log.e(TAG, "Error starting speech recognition", e);
            notifyError("Error: " + e.getMessage());
        }
    }

    /**
     * Create the speech recognition intent
     */
    private Intent createRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        // Don't end on silence - we'll control this manually
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);

        return intent;
    }

    /**
     * Create the recognition listener
     */
    private RecognitionListener createRecognitionListener() {
        return new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "Beginning of speech detected");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Audio level changed - useful for visualizations
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Buffer of audio data received
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "End of speech detected");
                isListening = false;
            }

            @Override
            public void onError(int error) {
                isListening = false;
                String errorMessage = getErrorMessage(error);
                Log.e(TAG, "Speech recognition error: " + errorMessage);

                // Don't treat "no match" as a true error - just restart listening
                if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                        error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Log.d(TAG, "Restarting listening after no match or timeout");
                    mainHandler.postDelayed(() -> startListeningInternal(), 300);
                    return;
                }

                notifyError(errorMessage);
            }

            @Override
            public void onResults(Bundle results) {
                isListening = false;

                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    Log.d(TAG, "Speech recognized: " + text);
                    notifyRecognized(text);

                    // Auto-restart listening after a short delay
                    mainHandler.postDelayed(() -> startListeningInternal(), 500);
                } else {
                    Log.d(TAG, "No speech detected, restarting...");
                    mainHandler.postDelayed(() -> startListeningInternal(), 300);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String partialText = matches.get(0);
                    Log.d(TAG, "Partial result: " + partialText);

                    // We could notify of partial results if needed
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Other recognition events
            }
        };
    }

    /**
     * Stop listening
     */
    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.stopListening();
            isListening = false;
            Log.d(TAG, "Stopped listening");
        }
    }

    /**
     * Restart listening (useful after errors)
     */
    public void restartListening() {
        stopListening();
        mainHandler.postDelayed(this::startListeningInternal, 300);
    }

    /**
     * Simulate recognized speech (for handling text input)
     */
    public void simulateRecognizedSpeech(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        notifyRecognized(text);
    }

    /**
     * Convert error code to human-readable message
     */
    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No matching speech";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech detected";
            default:
                return "Unknown error";
        }
    }

    /**
     * Notify all callbacks of recognized speech
     */
    private void notifyRecognized(String text) {
        for (SpeechRecognitionCallback callback : callbacks) {
            mainHandler.post(() -> callback.onSpeechRecognized(text));
        }
    }

    /**
     * Notify all callbacks of an error
     */
    private void notifyError(String error) {
        for (SpeechRecognitionCallback callback : callbacks) {
            mainHandler.post(() -> callback.onSpeechError(error));
        }
    }

    /**
     * Check if actively listening
     */
    public boolean isListening() {
        return isListening;
    }

    /**
     * Release resources
     */
    /**
     * Stop background listening
     * This method is used by ChessCoachManager at line 666
     */
    public void stopBackgroundListening() {
        Log.d(TAG, "Stopping background listening");
        stopListening();
    }

    /**
     * Release resources
     */
    public void release() {
        stopListening();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        callbacks.clear();
        isListening = false;
    }
}