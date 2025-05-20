package com.example.chesspedagogue;

import java.io.File;

/**
 * Interface for text-to-speech services that can convert text to spoken audio.
 */
public interface TextToSpeechService {
    /**
     * Speak the provided text and notify through callbacks
     */
    void speak(String text, TTSCallback callback);

    /**
     * Stop any ongoing speech
     */
    void stopSpeaking();

    /**
     * Check if currently speaking
     */
    boolean isSpeaking();

    /**
     * Release resources
     */
    void release();

    /**
     * Callback interface for TTS operations
     */
    interface TTSCallback {
        void onSpeechStarted();
        void onSpeechCompleted();
        void onAudioReady(File audioFile); // Optional - for caching/reuse
        void onError(String errorMessage);
    }
}