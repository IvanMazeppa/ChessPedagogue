package com.example.chesspedagogue.ui.effects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * ElectricArcSettingsManager - Manages electric arc move trail preferences
 * 
 * Features:
 * - Enable/disable electric arc trails
 * - Theme selection (Electric Blue, Lightning White, Neon Orange)
 * - Intensity control (affects glow and visibility)
 * - Duration settings
 * - Integration with existing ChessPedagoguePrefs
 */
public class ElectricArcSettingsManager {
    
    private static final String TAG = "ElectricArcSettings";
    
    // SharedPreferences keys
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_ELECTRIC_ARC_ENABLED = "electric_arc_enabled";
    private static final String KEY_ELECTRIC_ARC_THEME = "electric_arc_theme";
    private static final String KEY_ELECTRIC_ARC_INTENSITY = "electric_arc_intensity";
    private static final String KEY_ELECTRIC_ARC_DURATION = "electric_arc_duration";
    private static final String KEY_ELECTRIC_ARC_GLOW_ENABLED = "electric_arc_glow_enabled";
    
    // Default values
    private static final boolean DEFAULT_ENABLED = true;
    private static final String DEFAULT_THEME = "electric_blue";
    private static final int DEFAULT_INTENSITY = 75; // 0-100
    private static final int DEFAULT_DURATION = 200; // milliseconds
    private static final boolean DEFAULT_GLOW_ENABLED = true;
    
    // Theme constants
    public static final String THEME_ELECTRIC_BLUE = "electric_blue";
    public static final String THEME_LIGHTNING_WHITE = "lightning_white";
    public static final String THEME_NEON_ORANGE = "neon_orange";
    public static final String THEME_MATRIX_GREEN = "matrix_green";
    public static final String THEME_CYBER_PURPLE = "cyber_purple";
    
    private final SharedPreferences prefs;
    private final Context context;
    
    public ElectricArcSettingsManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "🎛️ ElectricArcSettingsManager initialized");
    }
    
    // ==================== ENABLED STATE ====================
    
    public boolean isElectricArcEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ELECTRIC_ARC_ENABLED, DEFAULT_ENABLED);
        return enabled;
    }
    
    public void setElectricArcEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ELECTRIC_ARC_ENABLED, enabled).apply();
        Log.d(TAG, "⚡ Electric arc " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== THEME SELECTION ====================
    
    public String getElectricArcTheme() {
        String theme = prefs.getString(KEY_ELECTRIC_ARC_THEME, DEFAULT_THEME);
        return theme;
    }
    
    public void setElectricArcTheme(String theme) {
        prefs.edit().putString(KEY_ELECTRIC_ARC_THEME, theme).apply();
        Log.d(TAG, "🎨 Electric arc theme set to: " + theme);
    }
    
    // ==================== INTENSITY CONTROL ====================
    
    /**
     * Get arc intensity (0-100)
     */
    public int getElectricArcIntensity() {
        int intensity = prefs.getInt(KEY_ELECTRIC_ARC_INTENSITY, DEFAULT_INTENSITY);
        return intensity;
    }
    
    public void setElectricArcIntensity(int intensity) {
        // Clamp to valid range
        intensity = Math.max(0, Math.min(100, intensity));
        prefs.edit().putInt(KEY_ELECTRIC_ARC_INTENSITY, intensity).apply();
        Log.d(TAG, "💪 Electric arc intensity set to: " + intensity + "%");
    }
    
    /**
     * Get intensity as alpha multiplier (0.0 - 1.0)
     */
    public float getIntensityAlpha() {
        return getElectricArcIntensity() / 100f;
    }
    
    // ==================== DURATION SETTINGS ====================
    
    /**
     * Get arc display duration in milliseconds
     */
    public int getElectricArcDuration() {
        int duration = prefs.getInt(KEY_ELECTRIC_ARC_DURATION, DEFAULT_DURATION);
        return duration;
    }
    
    public void setElectricArcDuration(int durationMs) {
        // Clamp to reasonable range (50ms - 1000ms)
        durationMs = Math.max(50, Math.min(1000, durationMs));
        prefs.edit().putInt(KEY_ELECTRIC_ARC_DURATION, durationMs).apply();
        Log.d(TAG, "⏱️ Electric arc duration set to: " + durationMs + "ms");
    }
    
    // ==================== GLOW SETTINGS ====================
    
    public boolean isGlowEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ELECTRIC_ARC_GLOW_ENABLED, DEFAULT_GLOW_ENABLED);
        return enabled;
    }
    
    public void setGlowEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ELECTRIC_ARC_GLOW_ENABLED, enabled).apply();
        Log.d(TAG, "✨ Electric arc glow " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== THEME APPLICATION ====================
    
    /**
     * Apply current settings to ElectricArcRenderer
     */
    public void applySettingsToRenderer(ElectricArcRenderer renderer) {
        if (!isElectricArcEnabled()) {
            return;
        }
        
        String theme = getElectricArcTheme();
        switch (theme) {
            case THEME_ELECTRIC_BLUE:
                renderer.applyElectricBlueTheme();
                break;
            case THEME_LIGHTNING_WHITE:
                renderer.applyLightningWhiteTheme();
                break;
            case THEME_NEON_ORANGE:
                renderer.applyNeonOrangeTheme();
                break;
            case THEME_MATRIX_GREEN:
                applyMatrixGreenTheme(renderer);
                break;
            case THEME_CYBER_PURPLE:
                applyCyberPurpleTheme(renderer);
                break;
            default:
                renderer.applyElectricBlueTheme();
                Log.w(TAG, "⚠️ Unknown theme: " + theme + ", using electric blue");
                break;
        }
        
        // Apply glow setting
        renderer.setGlowEnabled(isGlowEnabled());
        
        Log.d(TAG, "🎨 Applied " + theme + " theme to renderer (glow=" + isGlowEnabled() + 
              ", intensity=" + getElectricArcIntensity() + "%)");
    }
    
    private void applyMatrixGreenTheme(ElectricArcRenderer renderer) {
        renderer.setArcColor(0xFF00FF00); // Bright green
        renderer.setGlowColor(0x4000FF00); // Semi-transparent green
        renderer.setGlowEnabled(true);
        Log.d(TAG, "🎨 Applied matrix green arc theme");
    }
    
    private void applyCyberPurpleTheme(ElectricArcRenderer renderer) {
        renderer.setArcColor(0xFF9C27B0); // Material purple
        renderer.setGlowColor(0x409C27B0); // Semi-transparent purple
        renderer.setGlowEnabled(true);
        Log.d(TAG, "🎨 Applied cyber purple arc theme");
    }
    
    // ==================== CONVENIENCE METHODS ====================
    
    /**
     * Get all theme names for UI selection
     */
    public static String[] getAvailableThemes() {
        return new String[]{
            THEME_ELECTRIC_BLUE,
            THEME_LIGHTNING_WHITE, 
            THEME_NEON_ORANGE,
            THEME_MATRIX_GREEN,
            THEME_CYBER_PURPLE
        };
    }
    
    /**
     * Get user-friendly theme names
     */
    public static String[] getThemeDisplayNames() {
        return new String[]{
            "Electric Blue",
            "Lightning White",
            "Neon Orange", 
            "Matrix Green",
            "Cyber Purple"
        };
    }
    
    /**
     * Reset all settings to defaults
     */
    public void resetToDefaults() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ELECTRIC_ARC_ENABLED, DEFAULT_ENABLED);
        editor.putString(KEY_ELECTRIC_ARC_THEME, DEFAULT_THEME);
        editor.putInt(KEY_ELECTRIC_ARC_INTENSITY, DEFAULT_INTENSITY);
        editor.putInt(KEY_ELECTRIC_ARC_DURATION, DEFAULT_DURATION);
        editor.putBoolean(KEY_ELECTRIC_ARC_GLOW_ENABLED, DEFAULT_GLOW_ENABLED);
        editor.apply();
        Log.d(TAG, "🔄 Electric arc settings reset to defaults");
    }
    
    /**
     * Log current settings state
     */
    public void logCurrentSettings() {
        Log.d(TAG, "📊 Current Electric Arc Settings:");
        Log.d(TAG, "  Enabled: " + isElectricArcEnabled());
        Log.d(TAG, "  Theme: " + getElectricArcTheme());
        Log.d(TAG, "  Intensity: " + getElectricArcIntensity() + "%");
        Log.d(TAG, "  Duration: " + getElectricArcDuration() + "ms");
        Log.d(TAG, "  Glow: " + isGlowEnabled());
    }
}