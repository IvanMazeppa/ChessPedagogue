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
    
    private static boolean useElevenLabs = false;
    private static OpenAITTSService openAIService;
    private static ElevenLabsTTSService elevenLabsService;
    
    /**
     * Get the appropriate TTS service based on configuration
     */
    public static Object getTTSService(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        useElevenLabs = prefs.getBoolean(KEY_USE_ELEVENLABS, true);
        
        if (useElevenLabs) {
            Log.d(TAG, "🎤 Using ElevenLabs TTS Service");
            if (elevenLabsService == null) {
                elevenLabsService = ElevenLabsTTSService.getInstance(context);
                
                // Check if API key is set
                if (!elevenLabsService.hasApiKey()) {
                    // Try to get from environment or SharedPreferences
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
     * Get OpenAI TTS Service (for compatibility)
     */
    public static OpenAITTSService getOpenAITTSService(Context context) {
        if (useElevenLabs) {
            Log.d(TAG, "🔄 Wrapping ElevenLabs service as OpenAI service");
            // Return a wrapper that adapts ElevenLabs to OpenAI interface
            return new OpenAITTSServiceWrapper(context);
        }
        return OpenAITTSService.getInstance(context);
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
        
        // Note: Some OpenAI-specific methods like speakStreamingText won't work perfectly
        // but the main speak() methods will work seamlessly
    }
}