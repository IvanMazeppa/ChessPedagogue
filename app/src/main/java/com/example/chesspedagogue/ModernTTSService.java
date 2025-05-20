package com.example.chesspedagogue;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModernTTSService implements TextToSpeechService {
    private static final String TAG = "ModernTTSService";

    private final Context context;
    private final UnifiedOpenAIService openAIService;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    private MediaPlayer mediaPlayer;
    private boolean isSpeaking = false;
    private String defaultVoice = "onyx"; // Deep, authoritative voice

    public ModernTTSService(Context context) {
        this.context = context.getApplicationContext();
        this.openAIService = UnifiedOpenAIService.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Set the default voice to use
     */
    public void setDefaultVoice(String voice) {
        this.defaultVoice = voice;
    }

    @Override
    public void speak(String text, TTSCallback callback) {
        // Stop any currently playing speech
        stopSpeaking();

        if (text == null || text.isEmpty()) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Empty text provided"));
            }
            return;
        }

        // Notify that speech is starting
        if (callback != null) {
            mainHandler.post(callback::onSpeechStarted);
        }

        isSpeaking = true;

        // Generate and play speech
        executorService.execute(() -> {
            try {
                // Use the UnifiedOpenAIService to generate speech
                openAIService.generateSpeech(text, defaultVoice, new UnifiedOpenAIService.OpenAICallback<File>() {
                    @Override
                    public void onSuccess(File audioFile) {
                        // Notify that audio is ready
                        if (callback != null) {
                            mainHandler.post(() -> callback.onAudioReady(audioFile));
                        }

                        // Play the audio
                        playAudio(audioFile, () -> {
                            isSpeaking = false;
                            if (callback != null) {
                                mainHandler.post(callback::onSpeechCompleted);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        isSpeaking = false;
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError("TTS error: " + e.getMessage()));
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error generating speech", e);
                isSpeaking = false;
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("TTS error: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * Play audio file and call completion handler when done
     */
    private void playAudio(File audioFile, Runnable onCompletion) {
        try {
            // Make sure we're on the main thread for MediaPlayer operations
            mainHandler.post(() -> {
                try {
                    // Create and configure MediaPlayer
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                    mediaPlayer.setOnCompletionListener(mp -> {
                        mp.release();
                        mediaPlayer = null;
                        if (onCompletion != null) {
                            onCompletion.run();
                        }
                    });
                    mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                        Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                        mp.release();
                        mediaPlayer = null;
                        if (onCompletion != null) {
                            onCompletion.run();
                        }
                        return true;
                    });

                    // Prepare and start
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    Log.e(TAG, "Error playing audio", e);
                    if (onCompletion != null) {
                        onCompletion.run();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up audio playback", e);
            if (onCompletion != null) {
                onCompletion.run();
            }
        }
    }

    @Override
    public void stopSpeaking() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping MediaPlayer", e);
            }
        }
        isSpeaking = false;
    }

    @Override
    public boolean isSpeaking() {
        return isSpeaking;
    }

    @Override
    public void release() {
        stopSpeaking();
        executorService.shutdown();
    }
}