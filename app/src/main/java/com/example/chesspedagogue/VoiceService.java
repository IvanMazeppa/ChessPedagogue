package com.example.chesspedagogue;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A foreground service that continuously listens for speech, processes it,
 * and responds with AI-generated speech.
 */
public class VoiceService extends Service {
    private static final String TAG = "VoiceService";

    // Conversation state enum
    private enum State { IDLE, LISTENING, PROCESSING, SPEAKING }
    private State state = State.IDLE;

    private AudioRecord audioRecord;
    private boolean running = false;
    private Thread audioThread;
    private ConversationManager conversationManager;
    private UnifiedOpenAIService unifiedService;
    private AudioTrack ttsAudioTrack;
    private volatile boolean userInterrupted = false;  // flag for barge-in

    // Audio/VAD parameters
    private static final int SAMPLE_RATE = 16000;  // 16 kHz
    private static final int CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN, AUDIO_FORMAT);
    private static final short VAD_THRESHOLD = 16000;  // amplitude threshold for voice detection
    private static final int SILENCE_DURATION_MS = 1500;  // silence gap to consider speech ended

    private static final int MAX_SPEECH_LENGTH_MS = 10000; // New safety - max 10 seconds

    @Override
    public void onCreate() {
        super.onCreate();
        unifiedService = UnifiedOpenAIService.getInstance(this);

        unifiedService.setApiKey(ApiKeyConfig.getApiKey(this));
        // CRITICAL NEW CODE - Check permission first!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ RECORD_AUDIO permission not granted - cannot start service properly");
            // Show a toast to let the user know why it's not working
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_LONG).show();// Stop the service since we can't function without the permission
            stopSelf();
            return;
        }

        // Only continue initialization if we have permission
        Log.d(TAG, "✓ RECORD_AUDIO permission granted - initializing service");

        // Initialize the conversation manager with OpenAI API clients
        String apiKey = ApiKeyConfig.getApiKey(this); // Get API key from secure storage
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not available");
            stopSelf();
            return;
        }

        try {

            ChatService chatService = new OpenAIChatService(apiKey);
            TextToSpeechService ttsService = new OpenAITTSService(this, apiKey);

            conversationManager = new ConversationManager();

            conversationManager.setSystemMessage("You are Coach Tal, a brilliant chess coach. " +
                    "Provide helpful, witty advice about chess moves. Keep responses concise " +
                    "and friendly. You love to teach tactics and strategy.");

            // Setup and start AudioRecord for continuous listening
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    SAMPLE_RATE, CHANNEL_IN, AUDIO_FORMAT, BUFFER_SIZE);

            running = true;
            audioRecord.startRecording();

            audioThread = new Thread(this::audioLoop, "AudioLoopThread");
            audioThread.start();

            // Promote to foreground service with a persistent notification
            Notification notification = createNotification();
            startForeground(1, notification);

            Log.d(TAG, "VoiceService started (foreground) and listening...");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing service", e);
            stopSelf();
        }
    }

    private Notification createNotification() {
        String channelId = "VoiceServiceChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chess Coach Voice Service";
            String description = "Voice interaction with Chess Coach";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Chess Coach")
                .setContentText("Listening for voice commands...")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now) // Using Android system icon
                .setOngoing(true)
                .build();
    }

    private void audioLoop() {
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream speechBuffer = null;
        long lastVoiceHeardTime = 0;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ No microphone permission in audioLoop - stopping");
            running = false;
            return;
        }

        while (running) {
            // Be defensive - check if audioRecord is still valid
            if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                break; // Exit the loop if audioRecord is no longer valid
            }

            int read = audioRecord.read(buffer, 0, buffer.length);
            if (read <= 0) continue;

            // Compute amplitude of this chunk (basic VAD)
            int amplitude = 0;
            for (int i = 0; i < read; i += 2) {  // 16-bit samples, little endian
                int sample = (buffer[i] & 0xFF) | ((buffer[i+1] & 0xFF) << 8);
                amplitude += Math.abs(sample);
            }
            amplitude /= (read / 2);

            switch (state) {
                case IDLE:
                    // Waiting for user to start speaking
                    if (amplitude > VAD_THRESHOLD) {
                        // Voice detected -- start recording speech
                        speechBuffer = new ByteArrayOutputStream();
                        lastVoiceHeardTime = System.currentTimeMillis();
                        try {
                            // include the initial chunk that triggered detection
                            speechBuffer.write(buffer, 0, read);
                        } catch (Exception e) { /* shouldn't happen with ByteArrayOutputStream */ }
                        state = State.LISTENING;
                        Log.d(TAG, "Voice detected. State -> LISTENING");
                    }
                    break;

                case LISTENING:
                    // User is speaking, accumulate audio data
                    try {
                        speechBuffer.write(buffer, 0, read);
                    } catch (Exception e) { }

                    // NEW CODE: Add a maximum recording time safety
                    long recordingTime = System.currentTimeMillis() - lastVoiceHeardTime;
                    if (recordingTime > MAX_SPEECH_LENGTH_MS) {
                        Log.d(TAG, "⚠️ Max recording time reached (" + recordingTime + "ms) - processing speech");
                        byte[] userSpeech = speechBuffer.toByteArray();
                        Log.d(TAG, "📊 Captured " + userSpeech.length + " bytes of audio data");
                        speechBuffer = null;
                        state = State.PROCESSING;

                        // Process on a separate thread
                        new Thread(() -> processUserSpeech(userSpeech), "ProcessThread").start();
                        break;
                    }

                    if (amplitude > VAD_THRESHOLD) {
                        // Still speaking
                        lastVoiceHeardTime = System.currentTimeMillis();
                    } else {
                        // Silence - check if long enough to consider speech ended
                        long silenceTime = System.currentTimeMillis() - lastVoiceHeardTime;

                        if (silenceTime > SILENCE_DURATION_MS) {
                            // User finished talking
                            byte[] userSpeech = speechBuffer.toByteArray();
                            Log.d(TAG, "📊 Captured " + userSpeech.length + " bytes after " + silenceTime + "ms silence");
                            speechBuffer = null;
                            state = State.PROCESSING;

                            // CRITICAL CHECKPOINT - is this code executing?
                            Log.d(TAG, "🎯 Speech ended. State -> PROCESSING");

                            // Process on a separate thread with explicit logging
                            new Thread(() -> {
                                Log.d(TAG, "🚀 Starting speech processing thread");
                                processUserSpeech(userSpeech);
                            }, "ProcessThread").start();
                        }
                    }
                    break;



                case PROCESSING:
                    // Ignoring microphone input while waiting for response
                    break;

                case SPEAKING:
                    // Currently playing AI speech; check for user interruption
                    if (amplitude > VAD_THRESHOLD) {
                        // User started talking while AI is speaking -> barge-in
                        // Your existing barge-in code...
                    }
                    break;
            }
        }

        // Cleanup if loop ends - carefully handle the AudioRecord
        if (audioRecord != null) {
            try {
                // Only call stop() if currently recording
                if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop();
                }
            } catch (IllegalStateException e) {
                // Already stopped or already released, that's okay
                Log.w(TAG, "Error stopping audioRecord: " + e.getMessage());
            }
        }
        // Don't release here - let onDestroy handle that
    }

    private void processUserSpeech(byte[] audioData) {
        try {
            Log.d(TAG, "⚡ Starting speech processing with " + audioData.length + " bytes");

            // Get API key with better debugging
            String apiKey = ApiKeyConfig.getApiKey(this);
            if (apiKey == null || apiKey.isEmpty()) {
                Log.e(TAG, "❌ API key missing or empty");
                showToastOnMainThread("API key not set - please configure in settings");
                state = State.IDLE;
                return;
            }

            // Log partial API key for debugging (never log full keys!)
            String safeKeyPart = apiKey.substring(0, 4) + "..." +
                    apiKey.substring(apiKey.length() - 4);
            Log.d(TAG, "🔑 Using API key: " + safeKeyPart);


            // Transcribe with error catching
            try {

                UnifiedOpenAIService unifiedService = UnifiedOpenAIService.getInstance(this);
                unifiedService.setApiKey(apiKey);

                String result = unifiedService.transcribeAudioSync(audioData);

                Log.d(TAG, "🎯 Transcription result: \"" + result + "\"");
                showToastOnMainThread("I heard: " + result);
            } catch (Exception e) {
                Log.e(TAG, "❌ Transcription failed", e);
                showToastOnMainThread("Couldn't transcribe: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Speech processing error", e);
        }
    }

    // Helper method to show toast on main thread
    private void showToastOnMainThread(final String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    // Add this helper method to show results on screen
    private void showTranscriptionToast(final String text) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> Toast.makeText(
                getApplicationContext(),
                "I heard: " + text,
                Toast.LENGTH_LONG).show());
    }

    // Play the TTS audio and handle state transitions and potential interruption
    private void playTTS(byte[] audioData) {
        // Configure AudioTrack for playback (16 kHz mono PCM)
        ttsAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AUDIO_FORMAT,
                audioData.length,
                AudioTrack.MODE_STREAM);
        ttsAudioTrack.play();
        state = State.SPEAKING;
        Log.d(TAG, "Playing TTS audio. State -> SPEAKING");

        // Write audio data in chunks to allow interrupt checking
        int offset = 0;
        int chunkSize = 1024;
        userInterrupted = false;

        while (offset < audioData.length && !userInterrupted) {
            int bytesToWrite = Math.min(chunkSize, audioData.length - offset);
            ttsAudioTrack.write(audioData, offset, bytesToWrite);
            offset += bytesToWrite;
        }

        // If not interrupted, ensure the rest of the data is played
        if (!userInterrupted) {
            // Flush remaining audio if any (in case chunk loop didn't write all)
            if (offset < audioData.length) {
                ttsAudioTrack.write(audioData, offset, audioData.length - offset);
            }
            Log.d(TAG, "TTS playback complete.");
        } else {
            Log.d(TAG, "TTS playback interrupted before completion.");
        }

        // Clean up audio track
        try { ttsAudioTrack.stop(); } catch (IllegalStateException e) { /* ignore */ }
        ttsAudioTrack.release();
        ttsAudioTrack = null;

        // If interrupted, the state was already set to LISTENING and a new speech is being recorded.
        // If not interrupted, return to IDLE state to listen for next user input.
        if (!userInterrupted) {
            state = State.IDLE;
            Log.d(TAG, "State -> IDLE (awaiting next user query)");
        }

        // Reset interruption flag for next cycle
        userInterrupted = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");

        // Check permission again - just to be super safe
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ Cannot start service - permission not granted");
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_LONG).show();
            stopSelf();
            return START_NOT_STICKY;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");

        // IMPORTANT: Set running to false FIRST before touching AudioRecord
        running = false;

        // Give the audio thread a moment to notice running = false
        try {
            if (audioThread != null) {
                audioThread.join(100); // Wait up to 100ms for thread to exit naturally
            }
        } catch (InterruptedException e) {
            // Thread was interrupted, that's okay
        }

        // NOW it's safer to release AudioRecord
        if (audioRecord != null) {
            try {
                // Only call stop() if currently recording
                if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop();
                }
            } catch (IllegalStateException e) {
                // Already stopped, that's okay
                Log.w(TAG, "AudioRecord was already stopped");
            }

            audioRecord.release();
            audioRecord = null;
        }

        // Rest of your cleanup code...

        Log.d(TAG, "VoiceService destroyed, stopped listening.");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not using binding in this service
        return null;
    }
}