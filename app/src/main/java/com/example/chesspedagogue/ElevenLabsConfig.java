package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Configuration helper for ElevenLabs API
 * Since Android doesn't support environment variables, use this to set your API key
 * 
 * Voice IDs for Chess Masters:
 * - Tal: WczBIOau2qV9z7nLeDqq (passionate, expressive voice)
 * - Fischer: TxvUy8tvDazkNBlnGcpU (intense, precise American voice)
 * - Carlsen: ygiXC2Oa1BiHksD3WkJZ (modern, confident Norwegian-accented voice)
 * 
 * Model Performance:
 * - eleven_flash_v2_5: ~75ms latency (best for real-time)
 * - eleven_turbo_v2_5: ~100-150ms latency (recommended - best quality/speed balance)
 * - eleven_multilingual_v2: ~200-300ms latency (highest quality for non-interactive)
 */
public class ElevenLabsConfig {
    
    /**
     * Set your ElevenLabs API key programmatically
     * Call this from your MainActivity.onCreate() or Application class
     * 
     * Example:
     * ElevenLabsConfig.setApiKey(this, "your_elevenlabs_api_key_here");
     */
    public static void setApiKey(Context context, String apiKey) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("elevenlabs_api_key", apiKey).apply();
    }
    
    /**
     * Check if API key is set
     */
    public static boolean hasApiKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        String apiKey = prefs.getString("elevenlabs_api_key", "");
        return !apiKey.isEmpty();
    }
    
    /**
     * Get the stored API key
     */
    public static String getApiKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        return prefs.getString("elevenlabs_api_key", "");
    }
}