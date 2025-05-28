package com.example.chesspedagogue;

public interface SpeechRecognizer {
    // Start listening with a callback for results
    void startListening(SpeechCallback callback);

    // Stop listening
    void stopListening();

    // Check if currently listening
    boolean isListening();

    // Release resources
    void release();

    // Callback interface
    interface SpeechCallback {
        void onPartialResult(String partialText);
        void onFinalResult(String finalText);
        void onError(String errorMessage);
    }
}