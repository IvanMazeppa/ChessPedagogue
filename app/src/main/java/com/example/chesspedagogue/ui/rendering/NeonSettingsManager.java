package com.example.chesspedagogue.ui.rendering;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

// Using direct SharedPreferences access consistent with existing SettingsActivity

import com.example.chesspedagogue.ChessBoardView;
import com.example.chesspedagogue.ui.rendering.shaders.ShaderConfig;

/**
 * Manager class for applying neon settings from SharedPreferences to ChessBoardView.
 * Handles color themes, intensity, and pulsing preferences.
 */
public class NeonSettingsManager {
    
    private static final String TAG = "NeonSettingsManager";
    
    // Preference keys (must match those in preferences.xml)
    public static final String PREF_NEON_MODE_ENABLED = "neon_mode_enabled";
    public static final String PREF_NEON_GLOW_INTENSITY = "neon_glow_intensity";
    public static final String PREF_NEON_PULSING_ENABLED = "neon_pulsing_enabled";
    public static final String PREF_NEON_COLOR_THEME = "neon_color_theme";
    
    // Color theme constants
    public static final String THEME_ELECTRIC_BLUE_GREEN = "electric_blue_green";
    public static final String THEME_CYBERPUNK_PINK_PURPLE = "cyberpunk_pink_purple";
    public static final String THEME_ARCTIC_BLUE_WHITE = "arctic_blue_white";
    public static final String THEME_FIRE_ORANGE_RED = "fire_orange_red";
    public static final String THEME_MATRIX_GREEN_LIME = "matrix_green_lime";
    public static final String THEME_PURPLE_GOLD = "purple_gold";
    public static final String THEME_TEAL_SILVER = "teal_silver";
    public static final String THEME_SUNSET_AMBER = "sunset_amber";
    public static final String THEME_NEON_RAINBOW = "neon_rainbow";
    
    /**
     * Color theme definitions
     */
    public static class ColorTheme {
        public final int lightSquareColor;
        public final int darkSquareColor;
        public final int gridColor;
        public final String name;
        
        public ColorTheme(int lightSquareColor, int darkSquareColor, int gridColor, String name) {
            this.lightSquareColor = lightSquareColor;
            this.darkSquareColor = darkSquareColor;
            this.gridColor = gridColor;
            this.name = name;
        }
    }
    
    /**
     * Get the ColorTheme object for a given theme key.
     */
    public static ColorTheme getColorTheme(String themeKey) {
        switch (themeKey) {
            case THEME_ELECTRIC_BLUE_GREEN:
                return new ColorTheme(
                    0xFF00FFFF, // Electric blue (cyan)
                    0xFF00FF00, // Neon green
                    0xFF66FFFF, // Soft cyan
                    "Electric Blue & Green"
                );
                
            case THEME_CYBERPUNK_PINK_PURPLE:
                return new ColorTheme(
                    0xFFFF00FF, // Magenta
                    0xFF9400D3, // Dark violet
                    0xFFFF69B4, // Hot pink
                    "Cyberpunk Pink & Purple"
                );
                
            case THEME_ARCTIC_BLUE_WHITE:
                return new ColorTheme(
                    0xFF87CEEB, // Sky blue
                    0xFFFFFFFF, // White
                    0xFFB0E0E6, // Powder blue
                    "Arctic Blue & White"
                );
                
            case THEME_FIRE_ORANGE_RED:
                return new ColorTheme(
                    0xFFFF4500, // Orange red
                    0xFFDC143C, // Crimson
                    0xFFFF6347, // Tomato
                    "Fire Orange & Red"
                );
                
            case THEME_MATRIX_GREEN_LIME:
                return new ColorTheme(
                    0xFF32CD32, // Lime green
                    0xFF00FF00, // Green
                    0xFF7FFF00, // Chartreuse
                    "Matrix Green & Lime"
                );
                
            case THEME_PURPLE_GOLD:
                return new ColorTheme(
                    0xFFFFD700, // Gold
                    0xFF8A2BE2, // Blue violet
                    0xFFDDA0DD, // Plum
                    "Royal Purple & Gold"
                );
                
            case THEME_TEAL_SILVER:
                return new ColorTheme(
                    0xFF00CED1, // Dark turquoise
                    0xFFC0C0C0, // Silver
                    0xFF48D1CC, // Medium turquoise
                    "Teal & Silver"
                );
                
            case THEME_SUNSET_AMBER:
                return new ColorTheme(
                    0xFFFFBF00, // Amber
                    0xFFFF4500, // Orange red
                    0xFFFFD700, // Gold
                    "Sunset Amber"
                );
                
            case THEME_NEON_RAINBOW:
                return new ColorTheme(
                    0xFFFF1493, // Deep pink
                    0xFF00FFFF, // Cyan
                    0xFF7FFF00, // Chartreuse
                    "Neon Rainbow"
                );
                
            default:
                // Default to electric blue & green
                return getColorTheme(THEME_ELECTRIC_BLUE_GREEN);
        }
    }
    
    /**
     * Apply all neon settings from SharedPreferences to the given ChessBoardView.
     */
    public static void applyNeonSettings(Context context, ChessBoardView chessBoardView) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        
        // Get neon mode enabled state
        boolean neonEnabled = prefs.getBoolean(PREF_NEON_MODE_ENABLED, false);
        chessBoardView.setNeonModeEnabled(neonEnabled);
        
        if (neonEnabled) {
            // Get glow intensity (0-100 from SeekBar, convert to 0.0-1.0)
            int intensityPercent = prefs.getInt(PREF_NEON_GLOW_INTENSITY, 80);
            float intensity = intensityPercent / 100.0f;
            chessBoardView.setNeonGlowIntensity(intensity);
            
            // Get pulsing enabled state
            boolean pulsingEnabled = prefs.getBoolean(PREF_NEON_PULSING_ENABLED, false);
            chessBoardView.setNeonPulsingEnabled(pulsingEnabled);
            
            // Get and apply color theme
            String themeKey = prefs.getString(PREF_NEON_COLOR_THEME, THEME_ELECTRIC_BLUE_GREEN);
            ColorTheme theme = getColorTheme(themeKey);
            chessBoardView.setNeonColors(theme.lightSquareColor, theme.darkSquareColor, theme.gridColor);
            
            Log.d(TAG, "🌈 Applied neon settings: " +
                "enabled=" + neonEnabled + 
                ", intensity=" + intensity + 
                ", pulsing=" + pulsingEnabled + 
                ", theme=" + theme.name);
        } else {
            Log.d(TAG, "🌈 Neon mode disabled");
        }
    }
    
    /**
     * Check if neon mode is enabled in preferences.
     */
    public static boolean isNeonModeEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_NEON_MODE_ENABLED, false);
    }
    
    /**
     * Get the current neon intensity from preferences (0.0 - 1.0).
     */
    public static float getNeonIntensity(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        int intensityPercent = prefs.getInt(PREF_NEON_GLOW_INTENSITY, 80);
        return intensityPercent / 100.0f;
    }
    
    /**
     * Get the current color theme from preferences.
     */
    public static ColorTheme getCurrentColorTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        String themeKey = prefs.getString(PREF_NEON_COLOR_THEME, THEME_ELECTRIC_BLUE_GREEN);
        return getColorTheme(themeKey);
    }
    
    /**
     * Save neon mode enabled state to preferences.
     */
    public static void setNeonModeEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_NEON_MODE_ENABLED, enabled).apply();
        Log.d(TAG, "🌈 Neon mode preference saved: " + enabled);
    }
    
    /**
     * Save neon intensity to preferences.
     */
    public static void setNeonIntensity(Context context, float intensity) {
        int intensityPercent = Math.round(intensity * 100);
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt(PREF_NEON_GLOW_INTENSITY, intensityPercent).apply();
        Log.d(TAG, "🌈 Neon intensity preference saved: " + intensityPercent + "%");
    }
    
    /**
     * Save pulsing enabled state to preferences.
     */
    public static void setPulsingEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_NEON_PULSING_ENABLED, enabled).apply();
        Log.d(TAG, "🌈 Neon pulsing preference saved: " + enabled);
    }
    
    /**
     * Save color theme to preferences.
     */
    public static void setColorTheme(Context context, String themeKey) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_NEON_COLOR_THEME, themeKey).apply();
        ColorTheme theme = getColorTheme(themeKey);
        Log.d(TAG, "🌈 Neon color theme preference saved: " + theme.name);
    }
    
    /**
     * Sync preferences with AGSL ShaderConfig for unified configuration
     */
    public static void syncWithShaderConfig(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        ShaderConfig config = ShaderConfig.getInstance();
        
        // Load all neon preferences into shader config
        boolean neonEnabled = prefs.getBoolean(PREF_NEON_MODE_ENABLED, false);
        float intensity = prefs.getInt(PREF_NEON_GLOW_INTENSITY, 80) / 100.0f;
        boolean pulsingEnabled = prefs.getBoolean(PREF_NEON_PULSING_ENABLED, false);
        String themeKey = prefs.getString(PREF_NEON_COLOR_THEME, THEME_ELECTRIC_BLUE_GREEN);
        
        // Apply to shader config
        config.setIntensity(intensity);
        config.setAnimationType(pulsingEnabled ? ShaderConfig.AnimationType.PULSE : ShaderConfig.AnimationType.STATIC);
        
        // Update theme colors
        ColorTheme theme = getColorTheme(themeKey);
        config.updateThemeColors(theme.lightSquareColor, theme.darkSquareColor, theme.gridColor);
        
        // Save shader config to its own preferences
        config.saveToPreferences(context);
        
        Log.d(TAG, "🚀 Synced preferences with AGSL ShaderConfig");
    }
    
    /**
     * Enhanced settings application with AGSL support
     */
    public static void applyNeonSettingsWithAGSL(Context context, ChessBoardView chessBoardView) {
        // First sync preferences with shader config
        syncWithShaderConfig(context);
        
        // Then apply standard settings
        applyNeonSettings(context, chessBoardView);
        
        Log.d(TAG, "✅ Applied enhanced neon settings with AGSL support");
    }
    
    /**
     * Get shader configuration for current neon settings
     */
    public static ShaderConfig getShaderConfigForCurrentSettings(Context context) {
        syncWithShaderConfig(context);
        return ShaderConfig.getInstance();
    }
}