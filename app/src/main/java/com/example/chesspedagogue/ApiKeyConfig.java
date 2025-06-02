package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Secure handling of API keys
 */
public class ApiKeyConfig {
    private static final String TAG = "ApiKeyConfig";
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_OPENAI_API_KEY = "sk-pr-";

    /**
     * Save the OpenAI API key securely
     */
    public static void saveApiKey(Context context, String apiKey) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_OPENAI_API_KEY, apiKey);
            editor.apply();

            Log.d(TAG, "API key saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving API key", e);
        }
    }

    // Add this method to ApiKeyConfig class
    public static void initializeOpenAIClient(Context context) {
        String apiKey = getApiKey(context);
        if (apiKey != null && !apiKey.isEmpty()) {
            OpenAIService.getInstance().setApiKey(apiKey);
            Log.d(TAG, "Initialized OpenAIClient with API key from config");
        } else {
            Log.e(TAG, "Failed to initialize OpenAIClient - no API key available");
        }
    }

    /**
     * Retrieve the OpenAI API key
     */
    public static String getApiKey(Context context) {
        try {
            // First try SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String apiKey = prefs.getString(KEY_OPENAI_API_KEY, null);
            
            // Then try environment variable
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getenv("OPENAI_API_KEY");
                if (apiKey != null && !apiKey.isEmpty()) {
                    Log.d(TAG, "Using environment variable OPENAI_API_KEY");
                }
            }
            
            // Fallback to hardcoded key from ApiKeys class
            if (apiKey == null || apiKey.isEmpty()) {
                try {
                    if (ApiKeys.isConfigured()) {
                        apiKey = ApiKeys.getOpenAIKey();
                        Log.d(TAG, "Using API key from ApiKeys class");
                    } else {
                        Log.w(TAG, "No API key found - check SharedPreferences, environment, or ApiKeys.java");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing ApiKeys class: " + e.getMessage());
                }
            }
            
            return apiKey;
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving API key", e);
            // Return hardcoded key as final fallback
            return null;
        }
    }

    /**
     * Check if an API key is already saved
     */
    public static boolean hasApiKey(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.contains(KEY_OPENAI_API_KEY);
        } catch (Exception e) {
            Log.e(TAG, "Error checking for API key", e);
            return false;
        }
    }

    /**
     * Clear the saved API key
     */
    public static void clearApiKey(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(KEY_OPENAI_API_KEY);
            editor.apply();

            Log.d(TAG, "API key cleared successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing API key", e);
        }
    }
}