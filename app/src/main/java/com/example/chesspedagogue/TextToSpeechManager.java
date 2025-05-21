package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Manages text-to-speech operations with OpenAI's TTS service
 * Enhanced for low latency response
 */
public class TextToSpeechManager {
    private static final String TAG = "TextToSpeechManager";
    private final Context context;
    private OpenAITTSService openAITTS;
    private boolean isSpeaking = false;
    private boolean interrupted = false;
    private SpeechCallback speechCallback;

    /**
     * Interface for receiving speech completion notifications
     */
    public interface OnSpeechCompletedListener {
        void onSpeechCompleted();
    }

    /**
     * Interface for speech callbacks
     */
    public interface SpeechCallback {
        void onSpeechCompleted(String text);
        void onSpeechInterrupted();
    }

    /**
     * Constructor
     */
    public TextToSpeechManager(Context context) {
        this.context = context;

        // Initialize the OpenAI TTS service
        String apiKey = ApiKeyConfig.getApiKey(context);
        this.openAITTS = OpenAITTSService.getInstance(context);
        this.openAITTS.setApiKey(apiKey);

        // Set the voice to a chess grandmaster style
        this.openAITTS.setVoice(OpenAITTSService.VOICE_GRANDMASTER);
    }

    /**
     * Set the speech callback
     */
    public void setSpeechCallback(SpeechCallback callback) {
        this.speechCallback = callback;
    }

    /**
     * Interrupt ongoing speech
     */
    public void interrupt() {
        interrupted = true;
        if (isSpeaking) {
            openAITTS.stopPlayback();
            Log.d(TAG, "Speech interrupted by user");
            isSpeaking = false;

            if (speechCallback != null) {
                speechCallback.onSpeechInterrupted();
            }
        }
    }



    /**
     * Speak text with completion listener
     */
    /**
     * Speak text with completion listener
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speech started with listener - text length: " + (text != null ? text.length() : 0));
        isSpeaking = true;
        interrupted = false;

        // Add some debugging info
        String apiKey = ApiKeyConfig.getApiKey(context);
        Log.d(TAG, "API key available: " + (apiKey != null && !apiKey.isEmpty()));

        openAITTS.speakWithChunking(text, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "TTS Speech STARTED callback received");
                // Already set isSpeaking = true above
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "TTS Speech READY callback received - file: " +
                        (audioFile != null ? audioFile.getAbsolutePath() : "null"));
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "TTS Speech COMPLETED callback received");
                isSpeaking = false;
                if (listener != null && !interrupted) {
                    listener.onSpeechCompleted();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS ERROR callback received: " + errorMessage);
                isSpeaking = false;
                if (listener != null) {
                    listener.onSpeechCompleted(); // Still call callback on error
                }
            }
        });
    }

    /**
     * Speak text
     */
    public void speak(String text) {
        Log.d(TAG, "Speech started");
        isSpeaking = true;
        interrupted = false;

        openAITTS.speakWithChunking(text, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                // Already set isSpeaking = true above
            }

            @Override
            public void onSpeechReady(File audioFile) {
                // Nothing to do here
            }

            @Override
            public void onSpeechCompleted() {
                isSpeaking = false;
                Log.d(TAG, "Speech completed");

                if (speechCallback != null && !interrupted) {
                    speechCallback.onSpeechCompleted(text);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS error: " + errorMessage);
                isSpeaking = false;
            }
        });
    }

    /**
     * Check if currently speaking
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }

    /**
     * Stop speaking
     */
    public void stopSpeech() {
        stop();
    }

    /**
     * Stop speaking (internal method)
     */
    public void stop() {
        if (isSpeaking) {
            openAITTS.stopPlayback();
            isSpeaking = false;

            // Notify via callback if available
            if (speechCallback != null) {
                speechCallback.onSpeechInterrupted();
            }
        }
    }

    /**
     * Release resources
     */
    public void shutdown() {
        stop();
        if (openAITTS != null) {
            openAITTS.shutdown();
        }
    }
}