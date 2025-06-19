package com.example.chesspedagogue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 🎤 Voice Control Manager - Central hub for always-listening voice control
 * 
 * Manages the AlwaysListeningService and integrates wake word detection 
 * with the chess game activities and spectator mode.
 */
public class VoiceControlManager implements AlwaysListeningService.WakeWordCallback {
    private static final String TAG = "VoiceControlManager";
    
    private static VoiceControlManager instance;
    private final Context context;
    
    // Service connection
    private AlwaysListeningService alwaysListeningService;
    private boolean serviceBound = false;
    
    // Current activity context for voice commands
    private VoiceCommandListener currentListener;
    
    // Settings
    private SharedPreferences preferences;
    private boolean alwaysListeningEnabled = false;
    
    /**
     * Interface for activities to receive voice commands
     */
    public interface VoiceCommandListener {
        void onWakeWordDetected(String wakeWord);
        void onVoiceCommand(String command);
        void onVoiceError(String error);
        void onVoiceStatusChanged(AlwaysListeningService.VoiceStatus status); // NEW: Visual status updates
        String getActivityType(); // "main_game", "spectator", "menu"
    }
    
    private VoiceControlManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences("VoiceControlPrefs", Context.MODE_PRIVATE);
        this.alwaysListeningEnabled = preferences.getBoolean("always_listening_enabled", false);
        
        Log.d(TAG, "🎤 VoiceControlManager initialized");
    }
    
    public static synchronized VoiceControlManager getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceControlManager(context);
        }
        return instance;
    }
    
    /**
     * 🎤 Start always-listening voice control
     */
    public void startAlwaysListening() {
        if (!alwaysListeningEnabled) {
            Log.d(TAG, "Always-listening disabled in settings");
            return;
        }
        
        Log.d(TAG, "🎤 Starting always-listening voice control");
        
        Intent serviceIntent = new Intent(context, AlwaysListeningService.class);
        context.startForegroundService(serviceIntent);
        
        // Bind to service
        Intent bindIntent = new Intent(context, AlwaysListeningService.class);
        context.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    /**
     * 🛑 Stop always-listening voice control
     */
    public void stopAlwaysListening() {
        Log.d(TAG, "🛑 Stopping always-listening voice control");
        
        if (serviceBound && alwaysListeningService != null) {
            alwaysListeningService.stopListening();
            context.unbindService(serviceConnection);
            serviceBound = false;
        }
        
        Intent serviceIntent = new Intent(context, AlwaysListeningService.class);
        context.stopService(serviceIntent);
    }
    
    /**
     * 🔄 Toggle always-listening on/off
     */
    public void toggleAlwaysListening() {
        alwaysListeningEnabled = !alwaysListeningEnabled;
        preferences.edit().putBoolean("always_listening_enabled", alwaysListeningEnabled).apply();
        
        if (alwaysListeningEnabled) {
            startAlwaysListening();
            Toast.makeText(context, "🎤 Always-listening enabled", Toast.LENGTH_SHORT).show();
        } else {
            stopAlwaysListening();
            Toast.makeText(context, "🔇 Always-listening disabled", Toast.LENGTH_SHORT).show();
        }
        
        Log.d(TAG, "Always-listening: " + (alwaysListeningEnabled ? "ENABLED" : "DISABLED"));
    }
    
    /**
     * 📋 Register activity for voice commands
     */
    public void registerVoiceCommandListener(VoiceCommandListener listener) {
        this.currentListener = listener;
        Log.d(TAG, "📋 Registered voice command listener: " + 
              (listener != null ? listener.getActivityType() : "null"));
    }
    
    /**
     * 📋 Unregister activity from voice commands
     */
    public void unregisterVoiceCommandListener() {
        Log.d(TAG, "📋 Unregistered voice command listener");
        this.currentListener = null;
    }

    // ==================== AlwaysListeningService.WakeWordCallback Implementation ====================

    @Override
    public void onWakeWordDetected(String wakeWord) {
        Log.d(TAG, "🎯 Wake word detected: " + wakeWord);
        
        if (currentListener != null) {
            currentListener.onWakeWordDetected(wakeWord);
            
            // Determine action based on wake word and current context
            String activityType = currentListener.getActivityType();
            processWakeWordCommand(wakeWord, activityType);
        } else {
            Log.w(TAG, "⚠️ Wake word detected but no active listener");
        }
    }
    
    @Override
    public void onSpeechRecognized(String transcribedText) {
        Log.d(TAG, "🗣️ Speech recognized: " + transcribedText);
        
        if (currentListener != null) {
            // Process as voice command
            processVoiceCommand(transcribedText);
        }
    }
    
    @Override
    public void onListeningStateChanged(boolean isListening) {
        Log.d(TAG, "🎤 Listening state changed: " + (isListening ? "LISTENING" : "STOPPED"));
    }
    
    @Override
    public void onVoiceStatusChanged(AlwaysListeningService.VoiceStatus status) {
        Log.d(TAG, "🚦 Voice status changed: " + status);
        
        if (currentListener != null) {
            currentListener.onVoiceStatusChanged(status);
        }
    }
    
    @Override
    public void onError(String error) {
        Log.e(TAG, "❌ Voice control error: " + error);
        
        if (currentListener != null) {
            currentListener.onVoiceError(error);
        }
    }

    // ==================== Wake Word Processing ====================

    /**
     * 🧠 Process wake word based on context
     */
    private void processWakeWordCommand(String wakeWord, String activityType) {
        Log.d(TAG, "🧠 Processing wake word '" + wakeWord + "' in context: " + activityType);
        
        switch (wakeWord.toLowerCase()) {
            case "hey coach":
            case "chess coach":
                handleGeneralCoachCommand(activityType);
                break;
                
            case "hey fischer":
                handleMasterSpecificCommand("fischer", activityType);
                break;
                
            case "hey tal":
                handleMasterSpecificCommand("tal", activityType);
                break;
                
            case "hey carlsen":
                handleMasterSpecificCommand("carlsen", activityType);
                break;
                
            case "hey anand":
                handleMasterSpecificCommand("anand", activityType);
                break;
                
            case "hey alekhine":
                handleMasterSpecificCommand("alekhine", activityType);
                break;
                
            default:
                Log.w(TAG, "⚠️ Unknown wake word: " + wakeWord);
        }
    }
    
    /**
     * 🎭 Handle general coach commands
     */
    private void handleGeneralCoachCommand(String activityType) {
        switch (activityType) {
            case "main_game":
                // Start voice recording in main game
                Log.d(TAG, "🎯 Starting voice interaction in main game");
                if (currentListener != null) {
                    currentListener.onVoiceCommand("start_voice_recording");
                }
                break;
                
            case "spectator":
                // Interrupt spectator conversation
                Log.d(TAG, "🎯 Interrupting spectator conversation for user comment");
                if (currentListener != null) {
                    currentListener.onVoiceCommand("interrupt_for_comment");
                }
                break;
                
            case "menu":
                // Navigate to game or show options
                Log.d(TAG, "🎯 Voice command in menu context");
                if (currentListener != null) {
                    currentListener.onVoiceCommand("show_voice_options");
                }
                break;
        }
    }
    
    /**
     * 🎭 Handle master-specific commands
     */
    private void handleMasterSpecificCommand(String masterName, String activityType) {
        Log.d(TAG, "🎭 Master-specific command for " + masterName + " in " + activityType);
        
        switch (activityType) {
            case "main_game":
                // Switch to specific master and start voice
                if (currentListener != null) {
                    currentListener.onVoiceCommand("switch_master:" + masterName + ";start_voice");
                }
                break;
                
            case "spectator":
                // Focus on specific master in spectator mode
                if (currentListener != null) {
                    currentListener.onVoiceCommand("focus_master:" + masterName);
                }
                break;
                
            case "menu":
                // Navigate to game with specific master
                if (currentListener != null) {
                    currentListener.onVoiceCommand("start_game_with_master:" + masterName);
                }
                break;
        }
    }
    
    /**
     * 🗣️ Process general voice commands after wake word
     */
    private void processVoiceCommand(String command) {
        Log.d(TAG, "🗣️ Processing voice command: " + command);
        
        if (currentListener != null) {
            currentListener.onVoiceCommand("voice_input:" + command);
        }
    }

    // ==================== Service Connection ====================

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "🔗 Connected to AlwaysListeningService");
            
            AlwaysListeningService.LocalBinder binder = (AlwaysListeningService.LocalBinder) service;
            alwaysListeningService = binder.getService();
            serviceBound = true;
            
            // Set callback
            alwaysListeningService.setWakeWordCallback(VoiceControlManager.this);
            
            // Start listening
            alwaysListeningService.startListening();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "🔗 Disconnected from AlwaysListeningService");
            serviceBound = false;
            alwaysListeningService = null;
        }
    };

    // ==================== Public API ====================

    /**
     * 📊 Check if always-listening is enabled
     */
    public boolean isAlwaysListeningEnabled() {
        return alwaysListeningEnabled;
    }
    
    /**
     * 🚫 Disable always-listening (for debugging/troubleshooting)
     */
    public void disableAlwaysListening() {
        Log.d(TAG, "🚫 Disabling always-listening for troubleshooting");
        alwaysListeningEnabled = false;
        preferences.edit().putBoolean("always_listening_enabled", false).apply();
        stopAlwaysListening();
        Toast.makeText(context, "🔇 Always-listening disabled for debugging", Toast.LENGTH_LONG).show();
    }
    
    /**
     * 🔧 Force disable always-listening immediately (for database troubleshooting)
     */
    public void forceDisableAlwaysListening() {
        Log.d(TAG, "🔧 FORCE disabling always-listening for database troubleshooting");
        alwaysListeningEnabled = false;
        preferences.edit().putBoolean("always_listening_enabled", false).apply();
        
        // Force stop any running service
        if (serviceBound && alwaysListeningService != null) {
            alwaysListeningService.stopListening();
            context.unbindService(serviceConnection);
            serviceBound = false;
        }
        
        // Kill the service entirely
        Intent serviceIntent = new Intent(context, AlwaysListeningService.class);
        context.stopService(serviceIntent);
        
        Log.d(TAG, "✅ Always-listening forcibly disabled - service stopped");
    }
    
    /**
     * 📊 Check if service is currently listening
     */
    public boolean isCurrentlyListening() {
        return serviceBound && alwaysListeningService != null && alwaysListeningService.isListening();
    }
    
    /**
     * 🚦 Manually update voice status (for coach response integration)
     */
    public void updateVoiceStatus(AlwaysListeningService.VoiceStatus status) {
        Log.d(TAG, "🚦 Manually updating voice status to: " + status);
        
        if (currentListener != null) {
            currentListener.onVoiceStatusChanged(status);
        }
    }
    
    /**
     * 🧹 Cleanup when app is destroyed
     */
    public void cleanup() {
        Log.d(TAG, "🧹 Cleaning up VoiceControlManager");
        
        unregisterVoiceCommandListener();
        stopAlwaysListening();
    }
}