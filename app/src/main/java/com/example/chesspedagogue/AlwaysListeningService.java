package com.example.chesspedagogue;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🎤 Always-Listening Wake Word Service for Chess Coach
 * 
 * Modern voice control system that continuously listens for wake words like:
 * - "Hey Coach"
 * - "Chess Coach" 
 * - Master names: "Hey Fischer", "Hey Tal", etc.
 */
public class AlwaysListeningService extends Service {
    private static final String TAG = "AlwaysListeningService";
    
    // Audio configuration
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    
    // Wake word detection
    private static final String[] WAKE_WORDS = {
        "hey coach", "chess coach", "coach", 
        "hey fischer", "hey tal", "hey carlsen", "hey anand", "hey alekhine"
    };
    
    // INDUSTRY-STANDARD THREE-TIER VOICE SYSTEM based on research
    private static final double INITIATION_THRESHOLD = 2500.0; // Higher threshold to avoid false triggers
    private static final double CONVERSATION_THRESHOLD = 1500.0; // Higher - clearer speech required in conversation
    private static final double BACKGROUND_NOISE_FLOOR = 1000.0; // Raised to avoid background sounds like typing
    private static final long SPEECH_TIMEOUT = 3000; // 3 seconds of speech max
    private static final long SILENCE_TIMEOUT = 1500; // 1.5 seconds of silence to stop
    private static final long MIN_SPEECH_DURATION = 500; // Minimum 500ms of speech before processing
    private static final long COOLDOWN_PERIOD = 2000; // 2 second cooldown between detections
    private static final long CONVERSATION_MODE_DURATION = 30000; // 30 seconds of conversation mode
    
    // Service components
    private final IBinder binder = new LocalBinder();
    private final AtomicBoolean isListening = new AtomicBoolean(false);
    private final AtomicBoolean isActive = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // Audio recording
    private AudioRecord audioRecord;
    private int bufferSize;
    
    // Conversation mode state tracking
    private long lastProcessedTime = 0;
    private long conversationModeStartTime = 0;
    private boolean inConversationMode = false;
    
    // 🎤 TTS FEEDBACK PREVENTION
    private boolean ttsIsSpeaking = false;
    private final Object ttsStateLock = new Object();
    
    // Wake word detection callback
    private WakeWordCallback callback;
    
    // Voice status states for visual feedback
    public enum VoiceStatus {
        LISTENING_FOR_INITIATION,  // White - waiting for you to speak up
        INITIATION_TRIGGERED,      // Yellow - you spoke up, entering conversation
        RECORDING_ACTIVE,          // Green - recording your voice or AI responding
        PROCESSING_SPEECH          // Red - transcribing your speech
    }
    
    // Notification
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "always_listening_channel";
    
    /**
     * Callback interface for wake word detection
     */
    public interface WakeWordCallback {
        void onWakeWordDetected(String wakeWord);
        void onSpeechRecognized(String transcribedText);
        void onListeningStateChanged(boolean isListening);
        void onVoiceStatusChanged(VoiceStatus status); // NEW: Visual status updates
        void onError(String error);
    }
    
    /**
     * Binder for client communication
     */
    public class LocalBinder extends Binder {
        public AlwaysListeningService getService() {
            return AlwaysListeningService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "🎤 Always-Listening Service created");
        
        createNotificationChannel();
        
        // Calculate buffer size
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }
        
        // 🎤 Set up TTS state tracking to prevent voice feedback loops
        setupTTSFeedbackPrevention();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "🎤 Always-Listening Service started");
        
        // Start as foreground service for continuous operation
        startForeground(NOTIFICATION_ID, createNotification("📢 Speak UP to chat with your chess master..."));
        
        // Start listening automatically
        startListening();
        
        return START_STICKY; // Restart if killed
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "🎤 Always-Listening Service destroyed");
        
        stopListening();
        executorService.shutdown();
    }
    
    /**
     * 🎤 Set up TTS feedback prevention system
     */
    private void setupTTSFeedbackPrevention() {
        TTSServiceManager.setTTSStateListener(new TTSServiceManager.TTSStateListener() {
            @Override
            public void onTTSStarted() {
                synchronized (ttsStateLock) {
                    ttsIsSpeaking = true;
                    Log.d(TAG, "🔊 TTS STARTED - Pausing voice recognition to prevent feedback");
                    updateNotification("🔊 AI is speaking...");
                }
            }
            
            @Override
            public void onTTSStopped() {
                synchronized (ttsStateLock) {
                    ttsIsSpeaking = false;
                    Log.d(TAG, "🔇 TTS STOPPED - Resuming voice recognition");
                    
                    // Resume normal notification based on conversation mode
                    if (inConversationMode) {
                        updateNotification("💬 Conversation active - speak normally...");
                    } else {
                        updateNotification("📢 Speak UP to chat with your chess master...");
                    }
                }
            }
        });
        
        Log.d(TAG, "✅ TTS feedback prevention system initialized");
    }
    
    /**
     * 🎤 Check if we should ignore audio due to TTS speaking
     */
    private boolean shouldIgnoreAudioDueToTTS() {
        synchronized (ttsStateLock) {
            return ttsIsSpeaking;
        }
    }
    
    /**
     * 🎤 Start always-listening for wake words
     */
    public void startListening() {
        if (isListening.get()) {
            Log.d(TAG, "Already listening");
            return;
        }
        
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ No microphone permission for always-listening");
            if (callback != null) {
                callback.onError("Microphone permission required for always-listening");
            }
            return;
        }
        
        Log.d(TAG, "🎤 Starting Industry-Standard Voice System");
        isListening.set(true);
        updateNotification("📢 Speak UP to chat with your chess master...");
        updateVoiceStatus(VoiceStatus.LISTENING_FOR_INITIATION);
        
        executorService.execute(this::listeningLoop);
        
        if (callback != null) {
            callback.onListeningStateChanged(true);
        }
    }
    
    /**
     * 🛑 Stop always-listening
     */
    public void stopListening() {
        if (!isListening.get()) {
            return;
        }
        
        Log.d(TAG, "🛑 Stopping always-listening");
        isListening.set(false);
        isActive.set(false);
        
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping AudioRecord", e);
            }
        }
        
        updateNotification("🔇 Not listening");
        
        if (callback != null) {
            callback.onListeningStateChanged(false);
        }
    }
    
    /**
     * 🎤 Main listening loop with wake word detection
     */
    private void listeningLoop() {
        try {
            // Initialize AudioRecord
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "❌ Permission check failed in listening loop");
                return;
            }
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
                    SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
            
            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "❌ AudioRecord not initialized");
                return;
            }
            
            audioRecord.startRecording();
            byte[] buffer = new byte[bufferSize];
            StringBuilder speechBuffer = new StringBuilder();
            long lastSoundTime = 0;
            long speechStartTime = 0;
            boolean inSpeech = false;
            
            Log.d(TAG, "🎤 Industry-Standard Three-Tier Voice System Ready!");
            Log.d(TAG, "   📢 Speak UP (RMS > " + INITIATION_THRESHOLD + ") to initiate conversation");
            Log.d(TAG, "   💬 Normal volume (RMS > " + CONVERSATION_THRESHOLD + ") during conversation mode");
            Log.d(TAG, "   🚫 Background noise filter (RMS " + BACKGROUND_NOISE_FLOOR + "-" + CONVERSATION_THRESHOLD + ") = ignored");
            
            while (isListening.get()) {
                int read = audioRecord.read(buffer, 0, bufferSize);
                
                if (read > 0) {
                    // Analyze audio for voice activity
                    double rms = calculateRMS(buffer, read);
                    long currentTime = System.currentTimeMillis();
                    
                    // 🎤 CRITICAL: Skip audio processing if TTS is speaking to prevent feedback
                    if (shouldIgnoreAudioDueToTTS()) {
                        // Log throttled to avoid spam
                        if (currentTime % 1000 < 100) { // Log roughly once per second
                            Log.d(TAG, "🔊 Ignoring audio input - TTS is speaking (feedback prevention)");
                        }
                        continue; // Skip this audio frame entirely
                    }
                    
                    // Check if we're still in conversation mode
                    if (inConversationMode && currentTime - conversationModeStartTime > CONVERSATION_MODE_DURATION) {
                        inConversationMode = false;
                        Log.d(TAG, "💬 Conversation mode expired - returning to initiation mode");
                        updateNotification("📢 Speak UP to chat with your chess master...");
                        updateVoiceStatus(VoiceStatus.LISTENING_FOR_INITIATION);
                    }
                    
                    // Determine current threshold based on mode
                    double currentThreshold = inConversationMode ? CONVERSATION_THRESHOLD : INITIATION_THRESHOLD;
                    boolean isSpeech = rms > currentThreshold;
                    
                    // INDUSTRY FILTER: Reject obvious background noise/typing
                    if (rms > BACKGROUND_NOISE_FLOOR && rms < currentThreshold) {
                        // This is likely typing, mouse clicks, or other non-speech sounds
                        continue; // Skip processing this audio frame
                    }
                    
                    if (isSpeech) {
                        lastSoundTime = currentTime;
                        if (!inSpeech) {
                            // Speech started - record start time
                            inSpeech = true;
                            speechStartTime = currentTime;
                            speechBuffer.setLength(0); // Clear buffer
                            
                            String mode = inConversationMode ? "conversation" : "initiation";
                            Log.d(TAG, "🎙️ Speech detected in " + mode + " mode (RMS: " + String.format("%.1f", rms) + ")");
                        }
                        
                    } else if (inSpeech) {
                        // Check if we've had enough silence to end speech
                        if (currentTime - lastSoundTime > SILENCE_TIMEOUT) {
                            inSpeech = false;
                            
                            // Check minimum speech duration and cooldown period
                            long speechDuration = currentTime - speechStartTime;
                            long timeSinceLastProcessed = currentTime - lastProcessedTime;
                            
                            if (speechDuration >= MIN_SPEECH_DURATION && timeSinceLastProcessed >= COOLDOWN_PERIOD) {
                                Log.d(TAG, "✅ Valid speech detected (duration: " + speechDuration + "ms, mode: " + 
                                      (inConversationMode ? "conversation" : "initiation") + ")");
                                lastProcessedTime = currentTime;
                                
                                // If not in conversation mode, entering conversation mode now
                                if (!inConversationMode) {
                                    inConversationMode = true;
                                    conversationModeStartTime = currentTime;
                                    Log.d(TAG, "🎉 ENTERING CONVERSATION MODE - You can now speak normally for 30 seconds!");
                                    updateNotification("💬 Conversation active - speak normally...");
                                    updateVoiceStatus(VoiceStatus.INITIATION_TRIGGERED);
                                    
                                    // After brief yellow flash, go to green recording mode
                                    mainHandler.postDelayed(() -> {
                                        updateVoiceStatus(VoiceStatus.RECORDING_ACTIVE);
                                    }, 800);
                                } else {
                                    // Extend conversation mode
                                    conversationModeStartTime = currentTime;
                                    Log.d(TAG, "💬 Conversation mode extended for another 30 seconds");
                                    updateVoiceStatus(VoiceStatus.RECORDING_ACTIVE);
                                }
                                
                                // Process the captured speech
                                processPotentialWakeWord(speechBuffer.toString());
                            } else {
                                Log.d(TAG, "🚫 Speech filtered out (duration: " + speechDuration + "ms, cooldown: " + timeSinceLastProcessed + "ms)");
                            }
                            
                            speechBuffer.setLength(0);
                        }
                    }
                    
                    // Timeout protection - don't capture speech forever
                    if (inSpeech && currentTime - speechStartTime > SPEECH_TIMEOUT) {
                        inSpeech = false;
                        speechBuffer.setLength(0);
                        Log.d(TAG, "⏱️ Speech timeout, resetting");
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in listening loop", e);
            if (callback != null) {
                callback.onError("Listening error: " + e.getMessage());
            }
        }
    }
    
    /**
     * 🧠 Process potential wake word using Groq STT
     */
    private void processPotentialWakeWord(String speechData) {
        // 🎤 DOUBLE-CHECK: Don't process if TTS is speaking
        if (shouldIgnoreAudioDueToTTS()) {
            Log.d(TAG, "🔊 Skipping wake word processing - TTS is speaking");
            return;
        }
        
        Log.d(TAG, "🧠 Processing potential wake word with Groq STT...");
        updateVoiceStatus(VoiceStatus.PROCESSING_SPEECH); // Red - transcribing
        
        // Check if wake-word-free mode is enabled (default true as user prefers no wake word)
        SharedPreferences prefs = getSharedPreferences("VoiceControlPrefs", Context.MODE_PRIVATE);
        boolean wakeWordFreeMode = prefs.getBoolean("wake_word_free_mode", true);
        
        if (wakeWordFreeMode) {
            Log.d(TAG, "🎤 Wake-word-free mode: Processing all speech as commands");
            processDirectSpeechCommand();
        } else {
            Log.d(TAG, "🎤 Wake word mode: Looking for specific wake words");
            processWakeWordDetection();
        }
    }
    
    /**
     * 🎤 Process all speech directly without wake word requirement
     */
    private void processDirectSpeechCommand() {
        // 🎤 FINAL CHECK: Abort if TTS started speaking during processing
        if (shouldIgnoreAudioDueToTTS()) {
            Log.d(TAG, "🔊 Aborting direct speech - TTS started speaking");
            return;
        }
        
        Log.d(TAG, "🎤 Direct speech command mode - capturing audio for transcription");
        
        try {
            // Get Groq speech recognizer instance
            GroqSpeechRecognizer speechRecognizer = new GroqSpeechRecognizer(this);
            
            // Start listening for user command directly
            speechRecognizer.startListening(new GroqSpeechRecognizer.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String transcribedText) {
                    Log.d(TAG, "🗣️ Direct speech transcribed: " + transcribedText);
                    
                    // 🎤 LAST DEFENSE: Ignore transcription if TTS is speaking
                    if (shouldIgnoreAudioDueToTTS()) {
                        Log.d(TAG, "🔊 Ignoring transcription - TTS is speaking: '" + transcribedText + "'");
                        return;
                    }
                    
                    // Enhanced noise filtering to reduce false triggers
                    if (isTranscriptionNoise(transcribedText)) {
                        Log.d(TAG, "🚫 Transcription filtered as noise: '" + transcribedText + "'");
                        return;
                    }
                    
                    mainHandler.post(() -> {
                        if (callback != null) {
                            // Treat all transcribed speech as a command
                            callback.onSpeechRecognized(transcribedText);
                        }
                        
                        // Update notification
                        updateNotification("🎯 Command: '" + transcribedText + "'");
                        
                        // Green - AI is now responding, you can interrupt
                        updateVoiceStatus(VoiceStatus.RECORDING_ACTIVE);
                        
                        // Reset notification after 5 seconds based on conversation mode
                        mainHandler.postDelayed(() -> {
                            if (inConversationMode) {
                                updateNotification("💬 Conversation active - speak normally...");
                            } else {
                                updateNotification("📢 Speak UP to chat with your chess master...");
                                updateVoiceStatus(VoiceStatus.LISTENING_FOR_INITIATION);
                            }
                        }, 5000);
                    });
                }
                
                @Override
                public void onSpeechError(String error) {
                    Log.d(TAG, "🔍 Direct speech processing completed: " + error);
                    // This is normal - return to listening mode
                }
            });
            
            // 🎯 SMART TIMEOUT: Instead of fixed 3 seconds, use silence detection
            startSmartSpeechTimeout(speechRecognizer);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in direct speech command processing", e);
        }
    }
    
    /**
     * 🧠 Smart timeout that extends recording time based on natural speech patterns
     */
    private void startSmartSpeechTimeout(GroqSpeechRecognizer speechRecognizer) {
        final long maxRecordingTime = 15000; // 15 seconds absolute maximum (instead of 3)
        final long initialTimeout = 5000; // Start with 5 seconds (instead of 3)
        final long extensionTime = 3000; // Extend by 3 seconds if still active
        final int maxExtensions = 3; // Maximum 3 extensions = 15 seconds total
        
        final AtomicBoolean isMonitoring = new AtomicBoolean(true);
        final AtomicBoolean hasExtended = new AtomicBoolean(false);
        final long startTime = System.currentTimeMillis();
        
        // Smart timeout that can extend if the user seems to still be speaking
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        
        Runnable timeoutRunnable = new Runnable() {
            private int extensionCount = 0;
            
            @Override
            public void run() {
                if (!isMonitoring.get()) return;
                
                long currentTime = System.currentTimeMillis();
                long totalRecordingTime = currentTime - startTime;
                
                // Check if we've exceeded absolute maximum recording time
                if (totalRecordingTime > maxRecordingTime || extensionCount >= maxExtensions) {
                    Log.d(TAG, "🔄 Smart timeout reached (" + totalRecordingTime + "ms, extensions: " + extensionCount + ") - stopping");
                    speechRecognizer.stopListening();
                    isMonitoring.set(false);
                    return;
                }
                
                // Check if we're still in conversation mode (indicates active speech session)
                if (inConversationMode && speechRecognizer.isListening()) {
                    extensionCount++;
                    hasExtended.set(true);
                    Log.d(TAG, "🎤 Conversation mode active - extending recording (extension " + extensionCount + "/" + maxExtensions + ")");
                    
                    // Schedule next check
                    timeoutHandler.postDelayed(this, extensionTime);
                } else {
                    // Natural completion or conversation mode ended
                    Log.d(TAG, "🔄 Natural speech completion detected - stopping recording");
                    speechRecognizer.stopListening();
                    isMonitoring.set(false);
                }
            }
        };
        
        // Start with initial timeout
        timeoutHandler.postDelayed(timeoutRunnable, initialTimeout);
        
        Log.d(TAG, "🧠 Smart timeout active - " + initialTimeout + "ms initial, up to " + maxRecordingTime + "ms max");
    }
    
    /**
     * 🎯 Process speech for wake word detection
     */
    private void processWakeWordDetection() {
        Log.d(TAG, "🎯 Wake word detection mode - checking for specific phrases");
        
        try {
            // Get Groq speech recognizer instance
            GroqSpeechRecognizer speechRecognizer = new GroqSpeechRecognizer(this);
            
            // Record a short sample and check for wake words
            recordShortSampleAndDetectWakeWord(speechRecognizer);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in wake word detection", e);
        }
    }

    /**
     * 🎤 Record a short sample and check for wake words
     */
    private void recordShortSampleAndDetectWakeWord(GroqSpeechRecognizer speechRecognizer) {
        try {
            Log.d(TAG, "🎤 Recording short sample for wake word detection");
            
            // Start a short recording session (2 seconds max)
            speechRecognizer.startListening(new GroqSpeechRecognizer.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String transcribedText) {
                    Log.d(TAG, "🎯 Transcribed audio: " + transcribedText);
                    
                    // Check if transcription contains any wake words
                    String lowerText = transcribedText.toLowerCase().trim();
                    String detectedWakeWord = null;
                    
                    for (String wakeWord : WAKE_WORDS) {
                        if (lowerText.contains(wakeWord)) {
                            detectedWakeWord = wakeWord;
                            break;
                        }
                    }
                    
                    if (detectedWakeWord != null) {
                        final String finalWakeWord = detectedWakeWord;
                        Log.d(TAG, "✅ Wake word detected: " + finalWakeWord);
                        
                        mainHandler.post(() -> {
                            updateNotification("🎯 '" + finalWakeWord + "' detected!");
                            if (callback != null) {
                                callback.onWakeWordDetected(finalWakeWord);
                                
                                // Continue listening for the full command after wake word
                                continueListeningForCommand(speechRecognizer);
                            }
                            
                            // Reset notification after 3 seconds
                            mainHandler.postDelayed(() -> {
                                updateNotification("🎤 Listening for voice commands...");
                            }, 3000);
                        });
                    } else {
                        Log.d(TAG, "🔍 No wake word detected in: " + transcribedText);
                    }
                }
                
                @Override
                public void onSpeechError(String error) {
                    Log.d(TAG, "🔍 Speech recognition completed (no speech detected)");
                    // This is normal - most audio won't contain wake words
                }
            });
            
            // Stop the short recording after 2 seconds
            mainHandler.postDelayed(() -> {
                speechRecognizer.stopListening();
            }, 2000);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in short wake word recording", e);
        }
    }

    /**
     * 🎤 Continue listening for full command after wake word detected
     */
    private void continueListeningForCommand(GroqSpeechRecognizer speechRecognizer) {
        Log.d(TAG, "🎤 Continuing to listen for full voice command...");
        
        try {
            // Listen for up to 5 seconds for the full command
            speechRecognizer.startListening(new GroqSpeechRecognizer.SpeechRecognitionCallback() {
                @Override
                public void onSpeechRecognized(String commandText) {
                    Log.d(TAG, "🗣️ Full command received: " + commandText);
                    
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onSpeechRecognized(commandText);
                        }
                    });
                }
                
                @Override
                public void onSpeechError(String error) {
                    Log.d(TAG, "🔍 Command listening completed: " + error);
                    // Command phase finished, return to wake word detection
                }
            });
            
            // Stop command listening after 5 seconds
            mainHandler.postDelayed(() -> {
                speechRecognizer.stopListening();
                Log.d(TAG, "🔄 Returning to wake word detection mode");
            }, 5000);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in command listening phase", e);
        }
    }
    
    /**
     * 📊 Calculate RMS (Root Mean Square) for voice activity detection
     */
    private double calculateRMS(byte[] buffer, int size) {
        long sum = 0;
        for (int i = 0; i < size; i += 2) {
            short sample = (short) (((buffer[i + 1] & 0xff) << 8) | (buffer[i] & 0xff));
            sum += sample * sample;
        }
        return Math.sqrt(sum / (size / 2));
    }
    
    /**
     * 🔔 Create notification channel for Android O+
     */
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Always Listening Chess Coach",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Continuous voice wake word detection for chess coaching");
            channel.setSound(null, null); // Silent
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * 🔔 Create notification for foreground service
     */
    private Notification createNotification(String text) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Chess Coach Voice Control")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    /**
     * 🔔 Update notification text
     */
    private void updateNotification(String text) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createNotification(text));
        }
    }
    
    /**
     * 📋 Set wake word callback
     */
    public void setWakeWordCallback(WakeWordCallback callback) {
        this.callback = callback;
    }
    
    /**
     * 🚦 Update voice status for visual feedback
     */
    private void updateVoiceStatus(VoiceStatus status) {
        Log.d(TAG, "🚦 Voice status: " + status);
        if (callback != null) {
            mainHandler.post(() -> callback.onVoiceStatusChanged(status));
        }
    }
    
    /**
     * 📊 Check if currently listening
     */
    public boolean isListening() {
        return isListening.get();
    }
    
    /**
     * 📊 Check if service is active
     */
    public boolean isActive() {
        return isActive.get();
    }
    
    /**
     * 🔍 Enhanced noise filtering to reduce false transcription triggers
     */
    private boolean isTranscriptionNoise(String transcribedText) {
        if (transcribedText == null) return true;
        
        String trimmed = transcribedText.trim().toLowerCase();
        
        // Empty or very short transcriptions
        if (trimmed.length() < 2) return true;
        
        // Common noise patterns from background sounds
        String[] noisePatterns = {
            ".", "..", "...", "....", // Dots from background noise
            "the", "a", "an", "and", "or", "to", "it", "is", // Single filler words
            "uh", "um", "hmm", "mm", "mhm", "hm", // Vocal fillers
            "oh", "ah", "eh", "er", // Interjections
            "00", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", // Numbers from noise
            "[blank_audio]", "[no_speech]", "[silence]", // Groq silence indicators
            "thank you", "you", "thank" // Accidental repeated phrases that don't mean anything
        };
        
        // Check exact matches for noise patterns
        for (String noise : noisePatterns) {
            if (trimmed.equals(noise)) {
                return true;
            }
        }
        
        // Very repetitive single characters (like "a a a a")
        if (trimmed.length() > 5 && trimmed.matches("^(.)\\s*\\1(\\s*\\1)*$")) {
            return true;
        }
        
        // Only punctuation or spaces
        if (trimmed.matches("^[\\s\\p{Punct}]+$")) {
            return true;
        }
        
        // Too many repeated spaces or dots
        if (trimmed.matches(".*\\.{3,}.*") || trimmed.matches(".*\\s{5,}.*")) {
            return true;
        }
        
        return false;
    }
}