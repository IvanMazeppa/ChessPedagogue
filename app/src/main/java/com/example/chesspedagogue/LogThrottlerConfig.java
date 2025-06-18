package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Configuration utility for LogThrottler
 * Handles initialization and preference management for log verbosity
 */
public class LogThrottlerConfig {
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_LOG_VERBOSITY = "log_verbosity_level";
    private static final String DEFAULT_VERBOSITY = "VERBOSE";
    
    /**
     * Initialize LogThrottler with user preferences
     */
    public static void initialize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String verbosityString = prefs.getString(KEY_LOG_VERBOSITY, DEFAULT_VERBOSITY);
        
        LogThrottler.VerbosityLevel level;
        try {
            level = LogThrottler.VerbosityLevel.valueOf(verbosityString);
        } catch (IllegalArgumentException e) {
            // Fallback to normal if invalid value
            level = LogThrottler.VerbosityLevel.NORMAL;
        }
        
        LogThrottler.setVerbosityLevel(level);
        
        // Log the initialization (using force to ensure it shows)
        LogThrottler.force("LogThrottlerConfig", "📊 Log throttling initialized: " + level);
    }
    
    /**
     * Set verbosity level and save to preferences
     */
    public static void setVerbosityLevel(Context context, LogThrottler.VerbosityLevel level) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOG_VERBOSITY, level.name());
        editor.apply();
        
        LogThrottler.setVerbosityLevel(level);
        LogThrottler.force("LogThrottlerConfig", "📊 Log verbosity changed to: " + level);
    }
    
    /**
     * Get current verbosity level from preferences
     */
    public static LogThrottler.VerbosityLevel getVerbosityLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String verbosityString = prefs.getString(KEY_LOG_VERBOSITY, DEFAULT_VERBOSITY);
        
        try {
            return LogThrottler.VerbosityLevel.valueOf(verbosityString);
        } catch (IllegalArgumentException e) {
            return LogThrottler.VerbosityLevel.NORMAL;
        }
    }
    
    /**
     * Get available verbosity levels for UI display
     */
    public static String[] getVerbosityLevelNames() {
        LogThrottler.VerbosityLevel[] levels = LogThrottler.VerbosityLevel.values();
        String[] names = new String[levels.length];
        for (int i = 0; i < levels.length; i++) {
            names[i] = levels[i].name();
        }
        return names;
    }
    
    /**
     * Get human-readable descriptions for verbosity levels
     */
    public static String[] getVerbosityDescriptions() {
        return new String[]{
            "Silent - No debug logs",
            "Minimal - Errors and important info only",
            "Normal - Standard logging (recommended)",
            "Verbose - All logs including debug details",
            "Ultra - Everything including throttling internals"
        };
    }
}