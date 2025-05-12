package com.example.chesspedagogue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Manages speech recognition functionality with support for both active and passive listening
 */
public class SpeechRecognitionManager {
    private static final String TAG = "SpeechRecognition";

    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private boolean backgroundListeningActive = false;
    private SpeechActivityDetector speechActivityListener;
    private ConversationStateListener stateListener;

    /**
     * Callback interface for speech recognition results
     */
    public interface SpeechRecognitionCallback {
        void onSpeechRecognized(String text);
        void onSpeechError(String error);
    }

    /**
     * Interface for conversation state updates
     */
    public interface ConversationStateListener {
        void onListening();
        void onProcessing();
        void onSpeaking(String text);
        void onPassiveListening();
        void onError(String message);
        void onConversationEnded();
    }

    /**
     * Interface for detecting speech activity in passive mode
     */
    public interface SpeechActivityDetector {
        void onSpeechDetected();
    }

    public SpeechRecognitionManager(Context context) {
        this.context = context;
        initializeSpeechRecognizer();
    }

    /**
     * Set listener for conversation state changes
     */
    public void setConversationStateListener(ConversationStateListener listener) {
        this.stateListener = listener;
    }

    /**
     * Set listener for speech activity detection
     */
    public void setSpeechActivityListener(SpeechActivityDetector listener) {
        this.speechActivityListener = listener;
    }

    /**
     * Initialize the speech recognizer
     */
    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        } else {
            Log.e(TAG, "Speech recognition not available on this device");
        }
    }

    /**
     * Start active listening for speech with callback
     */
    public void startListening(SpeechRecognitionCallback callback) {
        if (speechRecognizer == null) {
            Log.e(TAG, "Speech recognizer is not available");
            if (callback != null) {
                callback.onSpeechError("Speech recognition not available on this device");
            }
            return;
        }

        if (isListening) {
            stopListening();
        }

        try {
            // Notify UI that we're listening
            if (stateListener != null) {
                stateListener.onListening();
            }

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening = true;
                    Log.d(TAG, "Ready for speech");
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "Beginning of speech");
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    // Sound level changed
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    // Buffer received
                }

                @Override
                public void onEndOfSpeech() {
                    isListening = false;
                    Log.d(TAG, "End of speech");
                }

                @Override
                public void onError(int error) {
                    isListening = false;
                    String errorMessage = getErrorMessage(error);
                    Log.e(TAG, "Speech recognition error: " + errorMessage);
                    if (callback != null) {
                        callback.onSpeechError(errorMessage);
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    isListening = false;
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String text = matches.get(0);
                        Log.d(TAG, "Speech recognized: " + text);
                        if (callback != null) {
                            callback.onSpeechRecognized(text);
                        }
                    } else if (callback != null) {
                        callback.onSpeechError("No speech detected");
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    // Partial results available
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    // Event occurred
                }
            });

            speechRecognizer.startListening(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error starting speech recognition", e);
            if (callback != null) {
                callback.onSpeechError("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Start background listening mode
     */
    public void startBackgroundListening() {
        Log.d(TAG, "Starting background listening mode");

        // Notify the UI of passive listening mode
        if (stateListener != null) {
            stateListener.onPassiveListening();
        }

        // Set background listening flag
        backgroundListeningActive = true;

        // Here's where you would implement the actual continuous speech detection
        // This would depend on your specific approach, but could involve:
        // 1. Using a specific recognition mode that detects activity
        // 2. Periodically sampling audio levels
        // 3. Using a third-party library for voice activity detection

        // For now, we'll implement a simple approach that just starts standard
        // recognition but with a special callback for activity detection
        startListening(new SpeechRecognitionCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                // If any speech is detected, notify the listener
                if (speechActivityListener != null) {
                    speechActivityListener.onSpeechDetected();
                }
            }

            @Override
            public void onSpeechError(String error) {
                // On error, restart background listening after a short delay
                // unless we've been told to stop
                if (backgroundListeningActive) {
                    Log.d(TAG, "Background listening error, restarting: " + error);
                    new android.os.Handler().postDelayed(() -> {
                        if (backgroundListeningActive) {
                            startBackgroundListening();
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * Stop background listening
     */
    public void stopBackgroundListening() {
        backgroundListeningActive = false;
        if (speechRecognizer != null) {
            speechRecognizer.cancel();
        }
        Log.d(TAG, "Background listening stopped");
    }

    /**
     * Stop active listening
     */
    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.stopListening();
            isListening = false;
        }
    }

    /**
     * Start continuous listening with speech activity detection
     */
    public void startContinuousListening(SpeechActivityDetector detector) {
        // Store the detector to be called when speech is detected
        this.speechActivityListener = detector;

        // Start the background listening process
        startBackgroundListening();
    }

    /**
     * Stop continuous listening mode
     */
    public void stopContinuousListening() {
        stopBackgroundListening();
    }

    /**
     * Convert error code to readable message
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
     * Check if actively listening
     */
    public boolean isListening() {
        return isListening;
    }

    /**
     * Release resources
     */
    public void release() {
        backgroundListeningActive = false;
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        isListening = false;
    }
}