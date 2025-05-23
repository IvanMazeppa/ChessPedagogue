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
     * Speak text with completion listener - using proven direct method
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speech started with listener - text length: " + (text != null ? text.length() : 0));
        isSpeaking = true;
        interrupted = false;

        // Make sure we have API key
        String apiKey = ApiKeyConfig.getApiKey(context);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "No API key available!");
            if (listener != null) {
                listener.onSpeechCompleted();
            }
            return;
        }

        // Use the PROVEN direct method!
        openAITTS.setApiKey(apiKey);
        openAITTS.speakDirect(text, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "TTS Speech STARTED");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "TTS Speech READY - file: " + audioFile.getName());
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "TTS Speech COMPLETED");
                isSpeaking = false;
                if (listener != null && !interrupted) {
                    listener.onSpeechCompleted();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS ERROR: " + errorMessage);
                isSpeaking = false;
                if (listener != null) {
                    listener.onSpeechCompleted();
                }
            }
        });
    }

    /**
     * Speak text without listener
     */
    public void speak(String text) {
        speak(text, null);
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