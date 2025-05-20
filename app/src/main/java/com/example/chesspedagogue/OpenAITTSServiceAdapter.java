package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;

/**
 * Compatibility class that bridges between old and new TTS implementations.
 * This wraps the OpenAITTSService while implementing our new TextToSpeechService interface.
 */
public class OpenAITTSServiceAdapter implements TextToSpeechService {
    private static final String TAG = "OpenAITTSServiceAdapter";

    private final Context context;
    private final OpenAITTSService legacyService;
    private final Handler mainHandler;

    public OpenAITTSServiceAdapter(Context context) {
        this.context = context.getApplicationContext();
        this.legacyService = OpenAITTSService.getInstance(context);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void speak(String text, TTSCallback callback) {
        if (callback != null) {
            mainHandler.post(callback::onSpeechStarted);
        }

        // Use the legacy service with our adapter
        legacyService.speak(text, legacyService.VOICE_GRANDMASTER, legacyService.MODEL_STANDARD,
                new OpenAITTSService.TTSCallback() {
                    @Override
                    public void onSpeechStarted() {
                        // Already called above
                    }

                    @Override
                    public void onSpeechReady(File audioFile) {
                        if (callback != null) {
                            mainHandler.post(() -> callback.onAudioReady(audioFile));
                        }
                    }

                    @Override
                    public void onSpeechCompleted() {
                        if (callback != null) {
                            mainHandler.post(callback::onSpeechCompleted);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError(errorMessage));
                        }
                    }
                });
    }

    @Override
    public void stopSpeaking() {
        legacyService.stopPlayback();
    }

    @Override
    public boolean isSpeaking() {
        // This is a bit of a guess since the legacy service doesn't expose this directly
        return false; // We'll always assume not speaking
    }

    @Override
    public void release() {
        legacyService.shutdown();
    }
}