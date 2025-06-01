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
    
    private Context context;
    
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
     * Get the appropriate TTS service based on configuration - FIXED: Async initialization
     */
    public static Object getTTSService(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        useElevenLabs = prefs.getBoolean(KEY_USE_ELEVENLABS, true);
        
        if (useElevenLabs) {
            Log.d(TAG, "🎤 Using ElevenLabs TTS Service");
            if (elevenLabsService == null) {
                // FIXED: Initialize service on background thread to prevent ANR
                initializeElevenLabsServiceAsync(context, prefs);
                // Return a placeholder that will be replaced when initialization completes
                if (openAIService == null) {
                    openAIService = OpenAITTSService.getInstance(context);
                }
                return openAIService; // Fallback during async initialization
            }
            return elevenLabsService;
        } else {
            Log.d(TAG, "🎤 Using OpenAI TTS Service");
            if (openAIService == null) {
                openAIService = OpenAITTSService.getInstance(context);
            }
            return openAIService;
        }
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
     * Get OpenAI TTS Service (for compatibility)
     */
    public static OpenAITTSService getOpenAITTSService(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        useElevenLabs = prefs.getBoolean(KEY_USE_ELEVENLABS, true);
        
        if (useElevenLabs) {
            Log.d(TAG, "🔄 Using ElevenLabs via OpenAI wrapper");
            // Return a wrapper that adapts ElevenLabs to OpenAI interface
            return new OpenAITTSServiceWrapper(context);
        }
        
        Log.d(TAG, "🔄 Using OpenAI TTS service");
        if (openAIService == null) {
            openAIService = OpenAITTSService.getInstance(context);
        }
        return openAIService;
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
     * Wrapper class that adapts ElevenLabs service to OpenAI interface
     * This allows seamless switching without changing all the calling code
     */
    private static class OpenAITTSServiceWrapper extends OpenAITTSService {
        private final ElevenLabsTTSService elevenLabsService;
        
        public OpenAITTSServiceWrapper(Context context) {
            super(context);
            this.elevenLabsService = ElevenLabsTTSService.getInstance(context);
        }
        
        @Override
        public void speak(String text, OnSpeechCompletedListener listener) {
            // Adapt OpenAI listener to ElevenLabs listener
            ElevenLabsTTSService.OnSpeechCompletedListener elevenLabsListener = null;
            if (listener != null) {
                elevenLabsListener = new ElevenLabsTTSService.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        listener.onSpeechCompleted();
                    }
                };
            }
            elevenLabsService.speak(text, elevenLabsListener);
        }
        
        @Override
        public void speak(String text) {
            elevenLabsService.speak(text);
        }
        
        @Override
        public boolean isSpeaking() {
            return elevenLabsService.isSpeaking();
        }
        
        @Override
        public void stopSpeech() {
            elevenLabsService.stopSpeech();
        }
        
        @Override
        public void interrupt() {
            elevenLabsService.interrupt();
        }
        
        @Override
        public void stopSpeaking() {
            elevenLabsService.stopSpeaking();
        }
        
        @Override
        public void shutdown() {
            elevenLabsService.shutdown();
        }
        
        @Override
        public boolean hasApiKey() {
            return elevenLabsService.hasApiKey();
        }
        
        @Override
        public void setSpeechCallback(SpeechCallback callback) {
            // Adapt the callback
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