package com.example.chesspedagogue;

import static com.example.chesspedagogue.ChessMasterVoiceManager.VOICE_ALLOY;
import static com.example.chesspedagogue.ChessMasterVoiceManager.VOICE_ECHO;
import static com.example.chesspedagogue.ChessMasterVoiceManager.VOICE_FABLE;
import static com.example.chesspedagogue.ChessMasterVoiceManager.VOICE_ONYX;

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

    public void speak(String text) {
        Log.d(TAG, "Speech started");
        isSpeaking = true;
        interrupted = false;

        // Create a flag to track if this is the first speech completed callback
        final boolean[] isFirstCompletion = {true};

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
                // VERY IMPORTANT: Only set isSpeaking to false if this isn't the first completion
                // The first completion is just the first chunk finishing
                if (!isFirstCompletion[0]) {
                    isSpeaking = false;
                    Log.d(TAG, "All speech chunks completed");
                } else {
                    // This is just the first chunk completing - don't stop speaking!
                    isFirstCompletion[0] = false;
                    Log.d(TAG, "First chunk completed, continuing with next chunks");
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
     * Get the best voice for a specific chess master
     */
    public static String getVoiceForMaster(String master) {
        // Default to a neutral voice if master is unknown
        if (master == null) return VOICE_ALLOY;

        switch (master.toLowerCase()) {
            // Deep, authoritative voice for the commanding Russian players
            case "karpov":
                return VOICE_ONYX;
            case "kasparov":
                return VOICE_ONYX;

            // American voice for American players
            case "fischer":
                return VOICE_ECHO;

            // British voice for classical/elegant players
            case "capablanca":
                return VOICE_FABLE;
            case "lasker":
                return VOICE_FABLE;
            case "morphy":
                return VOICE_FABLE;

            // More energetic, modern players with American voice
            case "tal":
                return VOICE_ECHO;
            case "carlsen":
                return VOICE_ECHO;
            case "anand":
                return VOICE_ECHO;

            // Measured, analytical players with British voice
            case "kramnik":
                return VOICE_FABLE;

            // Default fallback
            default:
                return VOICE_ALLOY;
        }
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

    // Enhance speak method to support chess master personalities
    // Add this method to TextToSpeechManager.java
    private String getInstructionsForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "Speak with a Latvian accent. Sound enthusiastic about chess.";
            case "kramnik":
                return "Speak with a Russian accent. Sound calm and thoughtful.";
            case "karpov":
                return "Speak with a Russian accent. Sound methodical and patient.";
            case "fischer":
                return "Speak with an American accent. Sound confident and direct.";
            case "lasker":
                return "Speak with a German accent. Sound philosophical and wise.";
            case "kasparov":
                return "Speak with a Russian accent. Sound energetic and passionate.";
            // Add other masters as needed
            default:
                return "Speak as an experienced chess coach.";
        }
    }

    // IMPORTANT: Fix the speak method to use the right signature
    public void speak(String text, String chessMaster, OnSpeechCompletedListener listener) {
        // Get the appropriate voice for this master
        String voice = getVoiceForMaster(chessMaster);

        // Use just the text and callback with proper implementation
        openAITTS.speakWithChunking(text, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                // Implementation
            }

            @Override
            public void onSpeechReady(File audioFile) {
                // Implementation
            }

            @Override
            public void onSpeechCompleted() {
                if (listener != null) {
                    listener.onSpeechCompleted();
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Add this missing method!
                Log.e("TextToSpeechManager", "TTS error: " + errorMessage);
                if (listener != null) {
                    listener.onSpeechCompleted(); // Still notify completion on error
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