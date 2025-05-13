package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class TextToSpeechManager {
    private static final String TAG = "TextToSpeechManager";
    private final Context context;
    private OpenAITTSService openAITTS;
    private boolean isSpeaking = false;
    private boolean interrupted = false;

    private android.media.MediaPlayer mediaPlayer;
    private java.util.Queue<File> remainingChunks = new java.util.LinkedList<>();
    private SpeechCallback speechCallback;

    // Define the interface for speech completion callbacks
    public interface OnSpeechCompletedListener {
        void onSpeechCompleted();
    }

    public TextToSpeechManager(Context context) {
        this.context = context;

        // Initialize the OpenAI TTS service
        String apiKey = ApiKeyConfig.getApiKey(context);
        this.openAITTS = OpenAITTSService.getInstance(context);
        this.openAITTS.setApiKey(apiKey);

        // Set the voice to a chess grandmaster style
        this.openAITTS.setVoice(OpenAITTSService.VOICE_GRANDMASTER);
    }


    // Fixed interrupt method for TextToSpeechManager.java
    public void interrupt() {
        interrupted = true;
        if (isSpeaking) {
            openAITTS.stopPlayback(); // Use your existing openAITTS
            Log.d(TAG, "Speech interrupted by user");
            isSpeaking = false; // Make sure we reset the speaking state
        }
    }
    // Basic speak method without callback
    public void speak(String text) {
        Log.d(TAG, "Speech started");
        isSpeaking = true;

        interrupted = false;

        // Use chunking for better responsiveness
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
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS error: " + errorMessage);
                isSpeaking = false;
            }
        });
    }


    public void stopSpeech() {
        stop();
    }

    // Add this interface method to your callback
    public interface SpeechCallback {
        void onSpeechCompleted(String text);
        void onSpeechInterrupted(); // New method


    }

    // Add this method to set the callback
    public void setSpeechCallback(SpeechCallback callback) {
        this.speechCallback = callback;
    }


    // Speak method with OnSpeechCompletedListener callback
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speech started");
        isSpeaking = true;

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
                if (listener != null) {
                    listener.onSpeechCompleted();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS error: " + errorMessage);
                isSpeaking = false;
                if (listener != null) {
                    listener.onSpeechCompleted(); // Still call callback on error
                }
            }
        });
    }

    // Speak method with Runnable callback (for backward compatibility)
    public void speak(String text, Runnable onComplete) {
        Log.d(TAG, "Speech started");
        isSpeaking = true;

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
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS error: " + errorMessage);
                isSpeaking = false;
                if (onComplete != null) {
                    onComplete.run(); // Still run callback on error
                }
            }
        });
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void stop() {
        if (isSpeaking) {
            openAITTS.stopPlayback();

            // Clear any remaining chunks
            if (remainingChunks != null) {
                remainingChunks.clear();
            }

            // Stop and release MediaPlayer if it exists
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                } catch (Exception e) {
                    Log.e(TAG, "Error stopping MediaPlayer", e);
                }
                mediaPlayer = null;
            }

            isSpeaking = false;

            // Notify via callback if available
            if (speechCallback != null) {
                speechCallback.onSpeechInterrupted();
            }
        }
    }

    // Add the shutdown method that was missing
    public void shutdown() {
        stop();
        if (openAITTS != null) {
            openAITTS.shutdown();
        }
    }
}