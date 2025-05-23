package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Consolidated TTS service that handles all text-to-speech operations
 * Merged from TextToSpeechManager and OpenAITTSService
 */
public class OpenAITTSService {
    private static final String TAG = "OpenAITTSService";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";

    // Voice constants
    public static final String VOICE_GRANDMASTER = "onyx";
    public static final String VOICE_TUTOR = "nova";
    public static final String VOICE_SHIMMER = "shimmer";
    public static final String VOICE_ECHO = "echo";
    public static final String VOICE_ALLOY = "alloy";
    public static final String VOICE_FABLE = "fable";
    public static final String VOICE_ONYX = "onyx";
    public static final String VOICE_NOVA = "nova";

    // Model constants
    public static final String MODEL_STANDARD = "tts-1";
    public static final String MODEL_PREMIUM = "tts-1-hd";

    private static OpenAITTSService instance;

    private final OkHttpClient httpClient;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private String apiKey;
    private MediaPlayer currentPlayer;
    private String voice = VOICE_GRANDMASTER;
    private String model = MODEL_STANDARD;
    private boolean interruptRequested = false;
    private boolean isSpeaking = false;
    private SharedPreferences prefs;

    // Audio playback queue
    private final Queue<AudioPlaybackTask> playbackQueue = new LinkedList<>();
    private boolean isCurrentlyPlaying = false;
    private final Object playbackLock = new Object();

    // Speech callbacks (from TextToSpeechManager)
    private SpeechCallback speechCallback;

    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);

        // Use shared HTTP client if available, otherwise create one
        this.httpClient = OpenAIClient.getInstance().getHttpClient();

        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context);
        }
        return instance;
    }

    /**
     * Set voice personalization (personality usage)
     */
    public void setVoicePersonalization(boolean usePersonality) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("use_master_personality", usePersonality);
        editor.apply();
    }

    /**
     * Override the voice selection
     */
    public void setVoiceOverride(String voiceStyle) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("voice_style", voiceStyle);
        editor.apply();
    }

    /**
     * Clear any voice override (return to automatic)
     */
    public void clearVoiceOverride() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("voice_style", "auto");
        editor.apply();
    }

    // Simple speech callback interface (from TextToSpeechManager)
    public interface SpeechCallback {
        void onSpeechCompleted(String text);
        void onSpeechInterrupted();
    }

    public interface OnSpeechCompletedListener {
        void onSpeechCompleted();
    }

    // Set speech callback
    public void setSpeechCallback(SpeechCallback callback) {
        this.speechCallback = callback;
    }

    /**
     * Main speak method - simplified from TextToSpeechManager
     */
    public void speak(String text) {
        speak(text, (OnSpeechCompletedListener) null);
    }

    /**
     * Speak with completion listener
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speaking text, length: " + (text != null ? text.length() : 0));
        isSpeaking = true;
        interruptRequested = false;

        // Get voice for current chess master
        String voiceToUse = getVoiceForCurrentMaster();

        speakDirect(text, new TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "Speech started");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "Audio ready: " + audioFile.getName());
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "Speech completed");
                isSpeaking = false;
                if (listener != null && !interruptRequested) {
                    listener.onSpeechCompleted();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "TTS error: " + errorMessage);
                isSpeaking = false;
                if (listener != null) {
                    listener.onSpeechCompleted();
                }
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
     * Stop/interrupt speech
     */
    public void stopSpeech() {
        interrupt();
    }

    /**
     * Interrupt ongoing speech
     */
    public void interrupt() {
        interruptRequested = true;
        if (isSpeaking) {
            stopPlayback();
            Log.d(TAG, "Speech interrupted");
            isSpeaking = false;

            if (speechCallback != null) {
                speechCallback.onSpeechInterrupted();
            }
        }
    }

    /**
     * Get voice for current chess master
     */
    private String getVoiceForCurrentMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        String currentMaster = masterPrefs.getString("selected_master", "tal");
        String voiceOverride = prefs.getString("voice_style", "auto");

        // If user has selected a specific voice, use that
        if (!"auto".equals(voiceOverride)) {
            return voiceOverride;
        }

        // Otherwise use the master's default voice
        return ChessMasterVoiceManager.getVoiceForMaster(currentMaster);
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Direct TTS - simplified and working
     */
    public void speakDirect(String text, TTSCallback callback) {
        Log.d(TAG, "Direct TTS starting");

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "No API key set!");
            if (callback != null) {
                callback.onError("API key not set");
            }
            return;
        }

        executorService.execute(() -> {
            try {
                // Get voice and instructions
                String voiceToUse = getVoiceForCurrentMaster();
                String selectedMaster = getCurrentChessMaster();

                JSONObject payload = new JSONObject();
                payload.put("model", MODEL_STANDARD);
                payload.put("input", text);
                payload.put("voice", voiceToUse);
                payload.put("response_format", "mp3");

                // Add personality if enabled
                if (shouldUsePersonality()) {
                    String instructions = ChessMasterVoiceManager.getSimplifiedInstructionsForMaster(selectedMaster);
                    if (instructions != null) {
                        payload.put("instructions", instructions);
                    }
                }

                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );

                Request request = new Request.Builder()
                        .url(TTS_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String error = "TTS API error: " + response.code();
                        Log.e(TAG, error);
                        mainHandler.post(() -> callback.onError(error));
                        return;
                    }

                    byte[] audioData = response.body().bytes();
                    File audioFile = saveAudioToFile(audioData);

                    // Queue for playback
                    queueAudioPlayback(audioFile, callback);
                }
            } catch (Exception e) {
                Log.e(TAG, "TTS error: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("TTS error: " + e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Legacy method names for compatibility
     */
    public void speak(String text, String voice, String model, TTSCallback callback) {
        this.voice = voice;
        this.model = model;
        speakDirect(text, callback);
    }

    public void speakWithChunking(String text, TTSCallback callback) {
        speakDirect(text, callback);
    }

    public void speakStreamingText(String text, TTSCallback callback) {
        speakDirect(text, callback);
    }

    private void queueAudioPlayback(File audioFile, TTSCallback callback) {
        synchronized (playbackLock) {
            playbackQueue.offer(new AudioPlaybackTask(audioFile, callback));

            if (!isCurrentlyPlaying) {
                playNextInQueue();
            }
        }
    }

    private void playNextInQueue() {
        synchronized (playbackLock) {
            if (playbackQueue.isEmpty()) {
                isCurrentlyPlaying = false;
                return;
            }

            isCurrentlyPlaying = true;
            AudioPlaybackTask task = playbackQueue.poll();

            mainHandler.post(() -> {
                try {
                    if (task.callback != null) {
                        task.callback.onSpeechReady(task.audioFile);
                    }

                    MediaPlayer player = new MediaPlayer();
                    player.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build()
                    );

                    player.setOnPreparedListener(mp -> {
                        Log.d(TAG, "Playing audio");
                        currentPlayer = mp;
                        mp.start();
                    });

                    player.setOnCompletionListener(mp -> {
                        Log.d(TAG, "Audio playback completed");
                        mp.release();
                        currentPlayer = null;

                        if (task.callback != null) {
                            task.callback.onSpeechCompleted();
                        }

                        synchronized (playbackLock) {
                            isCurrentlyPlaying = false;
                            playNextInQueue();
                        }
                    });

                    player.setOnErrorListener((mp, what, extra) -> {
                        Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                        mp.release();
                        currentPlayer = null;

                        if (task.callback != null) {
                            task.callback.onError("Playback error: " + what);
                        }

                        synchronized (playbackLock) {
                            isCurrentlyPlaying = false;
                            playNextInQueue();
                        }

                        return true;
                    });

                    player.setDataSource(context, Uri.fromFile(task.audioFile));
                    player.prepareAsync();

                } catch (Exception e) {
                    Log.e(TAG, "Error playing audio", e);

                    if (task.callback != null) {
                        task.callback.onError("Playback error: " + e.getMessage());
                    }

                    synchronized (playbackLock) {
                        isCurrentlyPlaying = false;
                        playNextInQueue();
                    }
                }
            });
        }
    }

    public void stopPlayback() {
        interruptRequested = true;

        if (currentPlayer != null) {
            try {
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop();
                }
                currentPlayer.release();
                currentPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping media player", e);
            }
        }

        isSpeaking = false;
    }

    private String getCurrentChessMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        return masterPrefs.getString("selected_master", "tal");
    }

    private boolean shouldUsePersonality() {
        return prefs.getBoolean("use_master_personality", true);
    }

    private File saveAudioToFile(byte[] audioData) throws IOException {
        File cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
        File audioFile = new File(cacheDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioData);
        }

        return audioFile;
    }

    public void shutdown() {
        stopPlayback();
        executorService.shutdown();
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    private static class AudioPlaybackTask {
        final File audioFile;
        final TTSCallback callback;

        AudioPlaybackTask(File audioFile, TTSCallback callback) {
            this.audioFile = audioFile;
            this.callback = callback;
        }
    }

    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechReady(File audioFile);
        void onSpeechCompleted();
        void onError(String errorMessage);
    }
}