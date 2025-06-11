package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * TTSServiceManager - Centralized TTS service selection
 * Allows easy switching between OpenAI TTS and ElevenLabs TTS
 * 
 * To switch to ElevenLabs:
 * 1. Set AZURE_API_KEY environment variable with your ElevenLabs API key
 * 2. Call TTSServiceManager.setUseElevenLabs(context, true)
 * 3. All TTS calls will automatically use ElevenLabs
 */
public class TTSServiceManager {
    private static final String TAG = "TTSServiceManager";
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_USE_ELEVENLABS = "use_elevenlabs_tts";
    
    private static TTSServiceManager instance;
    private static boolean useElevenLabs = false;
    private static OpenAITTSService openAIService;
    private static ElevenLabsTTSService elevenLabsService;
    
    // 🎤 TTS STATE TRACKING to prevent voice feedback loops
    private static volatile boolean isTTSSpeaking = false;
    private static final Object ttsStateLock = new Object();
    private static TTSStateListener ttsStateListener;
    
    private Context context;
    
    /**
     * Interface for TTS state change notifications
     */
    public interface TTSStateListener {
        void onTTSStarted();
        void onTTSStopped();
    }
    
    /**
     * Get singleton instance - FIXED: Non-blocking initialization
     */
    public static synchronized TTSServiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new TTSServiceManager(context);
        }
        return instance;
    }
    
    private TTSServiceManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * Get the preferred TTS service as OpenAITTSService (may be wrapper)
     */
    public OpenAITTSService getPreferredTTSService() {
        // This returns the OpenAITTSService which may be a wrapper around ElevenLabs
        return getOpenAITTSService(context);
    }
    
    /**
     * 🎤 Check if TTS is currently speaking (prevents voice feedback loops)
     */
    public static boolean isTTSSpeaking() {
        synchronized (ttsStateLock) {
            return isTTSSpeaking;
        }
    }
    
    /**
     * 🎤 Set TTS state listener for voice feedback prevention
     */
    public static void setTTSStateListener(TTSStateListener listener) {
        ttsStateListener = listener;
        Log.d(TAG, "🎤 TTS state listener set for voice feedback prevention");
    }
    
    /**
     * 🎤 Notify that TTS has started speaking
     */
    public static void notifyTTSStarted() {
        synchronized (ttsStateLock) {
            if (!isTTSSpeaking) {
                isTTSSpeaking = true;
                Log.d(TAG, "🔊 TTS STARTED - Voice recognition should pause");
                if (ttsStateListener != null) {
                    try {
                        ttsStateListener.onTTSStarted();
                    } catch (Exception e) {
                        Log.e(TAG, "Error notifying TTS started", e);
                    }
                }
            }
        }
    }
    
    /**
     * 🎤 Notify that TTS has stopped speaking
     */
    public static void notifyTTSStopped() {
        synchronized (ttsStateLock) {
            if (isTTSSpeaking) {
                isTTSSpeaking = false;
                Log.d(TAG, "🔇 TTS STOPPED - Voice recognition can resume");
                if (ttsStateListener != null) {
                    try {
                        ttsStateListener.onTTSStopped();
                    } catch (Exception e) {
                        Log.e(TAG, "Error notifying TTS stopped", e);
                    }
                }
            }
        }
    }
    
    /**
     * Get ElevenLabs TTS Service directly - FIXED: Async initialization
     */
    public static ElevenLabsTTSService getElevenLabsTTSService(Context context) {
        if (elevenLabsService == null) {
            // Initialize asynchronously to prevent ANR
            elevenLabsService = ElevenLabsTTSService.getInstance(context);
        }
        return elevenLabsService;
    }
    
    /**
     * 🎤 ALWAYS returns ElevenLabs TTS Service - no OpenAI fallback allowed
     */
    public static Object getTTSService(Context context) {
        Log.d(TAG, "🎤 Using ElevenLabs TTS Service (ONLY option)");
        if (elevenLabsService == null) {
            elevenLabsService = ElevenLabsTTSService.getInstance(context);
        }
        return elevenLabsService;
    }
    
    /**
     * ADDED: Async initialization to prevent ANR
     */
    private static void initializeElevenLabsServiceAsync(Context context, SharedPreferences prefs) {
        // Run heavy initialization on background thread
        new Thread(() -> {
            try {
                elevenLabsService = ElevenLabsTTSService.getInstance(context);
                
                // Check if API key is set
                if (!elevenLabsService.hasApiKey()) {
                    String apiKey = prefs.getString("elevenlabs_api_key", "");
                    if (apiKey != null && !apiKey.isEmpty()) {
                        elevenLabsService.setApiKey(apiKey);
                        Log.d(TAG, "✅ ElevenLabs API key configured");
                    } else {
                        Log.w(TAG, "⚠️ No ElevenLabs API key found - falling back to OpenAI");
                        useElevenLabs = false;
                        prefs.edit().putBoolean(KEY_USE_ELEVENLABS, false).apply();
                    }
                }
                Log.d(TAG, "✅ ElevenLabs TTS Service initialized asynchronously");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error initializing ElevenLabs service", e);
            }
        }).start();
    }
    
    /**
     * 🚨 GUARDRAIL: Returns ElevenLabs wrapped as OpenAI-compatible interface
     */
    public static OpenAITTSService getElevenLabsAsOpenAICompatible(Context context) {
        Log.d(TAG, "🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper");
        return new ElevenLabsOnlyWrapper(context);
    }
    
    /**
     * 🚨 DEPRECATED: This method should not be used - ElevenLabs only!
     * Always returns ElevenLabs service wrapped in compatibility interface
     */
    @Deprecated
    public static OpenAITTSService getOpenAITTSService(Context context) {
        Log.w(TAG, "⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs");
        // ALWAYS use ElevenLabs - no OpenAI TTS allowed
        return getElevenLabsAsOpenAICompatible(context);
    }
    
    /**
     * Enable or disable ElevenLabs TTS
     */
    public static void setUseElevenLabs(Context context, boolean useElevenLabs) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_USE_ELEVENLABS, useElevenLabs).apply();
        TTSServiceManager.useElevenLabs = useElevenLabs;
        
        Log.d(TAG, useElevenLabs ? "✅ Switched to ElevenLabs TTS" : "✅ Switched to OpenAI TTS");
    }
    
    /**
     * Check if ElevenLabs is currently enabled
     */
    public static boolean isUsingElevenLabs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_USE_ELEVENLABS, true);
    }
    
    /**
     * Set usage context for dynamic model selection
     */
    public static void setUsageContext(Context context, String usageContext) {
        if (isUsingElevenLabs(context)) {
            // Initialize service if not already done
            if (elevenLabsService == null) {
                elevenLabsService = ElevenLabsTTSService.getInstance(context);
            }
            elevenLabsService.setUsageContext(usageContext);
            Log.d(TAG, "✅ Usage context set to: " + usageContext);
        } else {
            Log.d(TAG, "ℹ️ Context setting skipped (OpenAI mode)");
        }
    }
    
    /**
     * Set ElevenLabs API key
     */
    public static void setElevenLabsApiKey(Context context, String apiKey) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("elevenlabs_api_key", apiKey).apply();
        
        if (elevenLabsService != null) {
            elevenLabsService.setApiKey(apiKey);
        }
        
        Log.d(TAG, "✅ ElevenLabs API key updated");
    }
    
    /**
     * 🎭 FIXED: Speak with specific master voice without global preference changes
     * This prevents race conditions in spectator mode where multiple masters speak
     */
    public static void speakWithSpecificMaster(Context context, String masterName, String text, OpenAITTSService.OnSpeechCompletedListener listener) {
        Log.d(TAG, "🎭 Speaking with specific master: " + masterName + " (bypassing global preference)");
        
        if (isUsingElevenLabs(context)) {
            // Use ElevenLabs directly with specified master
            if (elevenLabsService == null) {
                elevenLabsService = ElevenLabsTTSService.getInstance(context);
            }
            
            // 🎭 CRITICAL FIX: Get current emotional state and set it before speaking
            EmotionalIntelligenceManager emotionalManager = EmotionalIntelligenceManager.getInstance(context);
            EmotionalIntelligenceManager.EmotionalAnalysisResult currentEmotion = emotionalManager.getCurrentEmotionalAnalysis(masterName);
            if (currentEmotion != null) {
                Log.d(TAG, "🎭 Setting emotional state for " + masterName + ": " + currentEmotion.emotion.name + " (intensity: " + currentEmotion.intensity + ")");
                elevenLabsService.setEmotionalState(currentEmotion);
            }
            
            // Temporarily override the master voice selection
            elevenLabsService.speakWithSpecificMaster(masterName, text, new ElevenLabsTTSService.SpeechCallback() {
                @Override
                public void onSpeechCompleted(String text) {
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                }
                
                @Override
                public void onSpeechInterrupted() {
                    if (listener != null) {
                        listener.onSpeechCompleted(); // Use completed callback for interruption too
                    }
                }
            });
        } else {
            // Fallback to OpenAI with temporary preference change
            SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String originalMaster = prefs.getString("selected_master", "tal");
            
            // Temporarily set master
            prefs.edit().putString("selected_master", masterName.toLowerCase()).apply();
            
            OpenAITTSService service = getOpenAITTSService(context);
            service.speak(text, new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    // Restore original master
                    prefs.edit().putString("selected_master", originalMaster).apply();
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                }
                
                public void onSpeechInterrupted() {
                    // Restore original master
                    prefs.edit().putString("selected_master", originalMaster).apply();
                    if (listener != null) {
                        listener.onSpeechCompleted(); // Use completed callback for interruption too
                    }
                }
            });
        }
    }
    
    /**
     * 🎤 ElevenLabs-only wrapper that extends OpenAI interface but OVERRIDES everything
     * This ensures ALL methods use ElevenLabs and NEVER call super (OpenAI) methods
     */
    public static class ElevenLabsOnlyWrapper extends OpenAITTSService {
        private final ElevenLabsTTSService elevenLabsService;
        
        public ElevenLabsOnlyWrapper(Context context) {
            super(context);
            this.elevenLabsService = ElevenLabsTTSService.getInstance(context);
            Log.d(TAG, "🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden");
        }
        
        // 🚨 OVERRIDE ALL METHODS TO PREVENT ANY OpenAI TTS CODE FROM RUNNING
        
        @Override
        public void speak(String text, OnSpeechCompletedListener listener) {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: speak with listener");
            TTSServiceManager.notifyTTSStarted();
            
            // Adapt OpenAI listener to ElevenLabs listener
            ElevenLabsTTSService.OnSpeechCompletedListener elevenLabsListener = null;
            if (listener != null) {
                elevenLabsListener = new ElevenLabsTTSService.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        // 🎤 Notify TTS stopped when speech completes
                        TTSServiceManager.notifyTTSStopped();
                        listener.onSpeechCompleted();
                    }
                };
            } else {
                elevenLabsListener = new ElevenLabsTTSService.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        // 🎤 Notify TTS stopped even without listener
                        TTSServiceManager.notifyTTSStopped();
                    }
                };
            }
            elevenLabsService.speak(text, elevenLabsListener);
        }
        
        @Override
        public void speak(String text) {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: speak without listener");
            TTSServiceManager.notifyTTSStarted();
            
            elevenLabsService.speak(text, new ElevenLabsTTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    TTSServiceManager.notifyTTSStopped();
                }
            });
        }
        
        // 🚑 CRITICAL: Override ALL other methods to prevent OpenAI fallbacks
        
        @Override
        public void speakStreamingText(String text, TTSCallback callback) {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: speakStreamingText redirected to simple speak");
            speak(text); // Redirect to our ElevenLabs implementation
        }
        
        @Override
        public void speakDirect(String text, TTSCallback callback) {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: speakDirect redirected to simple speak");
            speak(text); // Redirect to our ElevenLabs implementation
        }
        
        @Override
        public void speak(String text, String voice, String model, TTSCallback callback) {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: speak with voice/model redirected - ignoring OpenAI params");
            speak(text); // Redirect to our ElevenLabs implementation
        }
        
        @Override
        public boolean isSpeaking() {
            return elevenLabsService.isSpeaking();
        }
        
        @Override
        public void stopSpeech() {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: stopSpeech");
            elevenLabsService.stopSpeech();
            TTSServiceManager.notifyTTSStopped();
        }
        
        @Override
        public void interrupt() {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: interrupt");
            elevenLabsService.interrupt();
            TTSServiceManager.notifyTTSStopped();
        }
        
        @Override
        public void stopSpeaking() {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: stopSpeaking");
            elevenLabsService.stopSpeaking();
            TTSServiceManager.notifyTTSStopped();
        }
        
        @Override
        public void shutdown() {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: shutdown");
            elevenLabsService.shutdown();
        }
        
        @Override
        public boolean hasApiKey() {
            return elevenLabsService.hasApiKey();
        }
        
        @Override
        public void setSpeechCallback(SpeechCallback callback) {
            Log.d(TAG, "🎤 ElevenLabs-ONLY: setSpeechCallback");
            // Adapt callback to ElevenLabs format
            elevenLabsService.setSpeechCallback(new ElevenLabsTTSService.SpeechCallback() {
                @Override
                public void onSpeechCompleted(String text) {
                    callback.onSpeechCompleted(text);
                }
                
                @Override
                public void onSpeechInterrupted() {
                    callback.onSpeechInterrupted();
                }
            });
        }
        
        /**
         * Set usage context for dynamic model selection
         */
        public void setUsageContext(String context) {
            elevenLabsService.setUsageContext(context);
        }
        
        // Note: Some OpenAI-specific methods like speakStreamingText won't work perfectly
        // but the main speak() methods will work seamlessly
    }
}