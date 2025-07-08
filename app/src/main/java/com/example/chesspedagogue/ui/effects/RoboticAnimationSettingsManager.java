package com.example.chesspedagogue.ui.effects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * RoboticAnimationSettingsManager - Manages robotic piece movement animation preferences
 * 
 * Features:
 * - Enable/disable robotic piece animations
 * - Animation style selection (Mechanical, Servo, Industrial, Precise, Experimental)
 * - Speed control (speed multiplier)
 * - Mechanical effects toggles (overshoot, jitter, rotation)
 * - Sound effects integration
 * - Integration with existing ChessPedagoguePrefs
 */
public class RoboticAnimationSettingsManager {
    
    private static final String TAG = "RoboticAnimationSettings";
    
    // SharedPreferences keys
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_ROBOTIC_ANIMATION_ENABLED = "robotic_animation_enabled";
    private static final String KEY_ROBOTIC_ANIMATION_STYLE = "robotic_animation_style";
    private static final String KEY_ROBOTIC_ANIMATION_SPEED = "robotic_animation_speed";
    private static final String KEY_ROBOTIC_OVERSHOOT_ENABLED = "robotic_overshoot_enabled";
    private static final String KEY_ROBOTIC_JITTER_ENABLED = "robotic_jitter_enabled";
    private static final String KEY_ROBOTIC_ROTATION_ENABLED = "robotic_rotation_enabled";
    private static final String KEY_ROBOTIC_SOUND_ENABLED = "robotic_sound_enabled";
    private static final String KEY_ROBOTIC_PIECE_PROFILES_ENABLED = "robotic_piece_profiles_enabled";
    
    // Default values
    private static final boolean DEFAULT_ENABLED = true;
    private static final String DEFAULT_STYLE = "mechanical";
    private static final int DEFAULT_SPEED = 100; // 100% = normal speed
    private static final boolean DEFAULT_OVERSHOOT_ENABLED = true;
    private static final boolean DEFAULT_JITTER_ENABLED = true;
    private static final boolean DEFAULT_ROTATION_ENABLED = true;
    private static final boolean DEFAULT_SOUND_ENABLED = false; // Optional feature
    private static final boolean DEFAULT_PIECE_PROFILES_ENABLED = true;
    
    // Animation style constants
    public static final String STYLE_MECHANICAL = "mechanical";
    public static final String STYLE_SERVO = "servo";
    public static final String STYLE_INDUSTRIAL = "industrial";
    public static final String STYLE_PRECISE = "precise";
    public static final String STYLE_EXPERIMENTAL = "experimental";
    
    private final SharedPreferences prefs;
    private final Context context;
    
    public RoboticAnimationSettingsManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "🤖 RoboticAnimationSettingsManager initialized");
    }
    
    // ==================== ENABLED STATE ====================
    
    public boolean isRoboticAnimationEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ROBOTIC_ANIMATION_ENABLED, DEFAULT_ENABLED);
        Log.d(TAG, "🤖 Robotic animation enabled: " + enabled);
        return enabled;
    }
    
    public void setRoboticAnimationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ROBOTIC_ANIMATION_ENABLED, enabled).apply();
        Log.d(TAG, "🤖 Robotic animation " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== ANIMATION STYLE ====================
    
    public String getRoboticAnimationStyle() {
        String style = prefs.getString(KEY_ROBOTIC_ANIMATION_STYLE, DEFAULT_STYLE);
        Log.d(TAG, "🎨 Robotic animation style: " + style);
        return style;
    }
    
    public void setRoboticAnimationStyle(String style) {
        prefs.edit().putString(KEY_ROBOTIC_ANIMATION_STYLE, style).apply();
        Log.d(TAG, "🎨 Robotic animation style set to: " + style);
    }
    
    // ==================== SPEED CONTROL ====================
    
    /**
     * Get animation speed multiplier (50-200%)
     */
    public int getRoboticAnimationSpeed() {
        int speed = prefs.getInt(KEY_ROBOTIC_ANIMATION_SPEED, DEFAULT_SPEED);
        Log.d(TAG, "⚡ Robotic animation speed: " + speed + "%");
        return speed;
    }
    
    public void setRoboticAnimationSpeed(int speed) {
        // Clamp to valid range (50% to 200%)
        speed = Math.max(50, Math.min(200, speed));
        prefs.edit().putInt(KEY_ROBOTIC_ANIMATION_SPEED, speed).apply();
        Log.d(TAG, "⚡ Robotic animation speed set to: " + speed + "%");
    }
    
    /**
     * Get speed as multiplier factor (0.5 - 2.0)
     */
    public float getSpeedMultiplier() {
        return getRoboticAnimationSpeed() / 100f;
    }
    
    // ==================== MECHANICAL EFFECTS ====================
    
    public boolean isOvershootEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ROBOTIC_OVERSHOOT_ENABLED, DEFAULT_OVERSHOOT_ENABLED);
        Log.d(TAG, "🎯 Overshoot effect enabled: " + enabled);
        return enabled;
    }
    
    public void setOvershootEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ROBOTIC_OVERSHOOT_ENABLED, enabled).apply();
        Log.d(TAG, "🎯 Overshoot effect " + (enabled ? "enabled" : "disabled"));
    }
    
    public boolean isJitterEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ROBOTIC_JITTER_ENABLED, DEFAULT_JITTER_ENABLED);
        Log.d(TAG, "📳 Mechanical jitter enabled: " + enabled);
        return enabled;
    }
    
    public void setJitterEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ROBOTIC_JITTER_ENABLED, enabled).apply();
        Log.d(TAG, "📳 Mechanical jitter " + (enabled ? "enabled" : "disabled"));
    }
    
    public boolean isRotationEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ROBOTIC_ROTATION_ENABLED, DEFAULT_ROTATION_ENABLED);
        Log.d(TAG, "🔄 Scanning rotation enabled: " + enabled);
        return enabled;
    }
    
    public void setRotationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ROBOTIC_ROTATION_ENABLED, enabled).apply();
        Log.d(TAG, "🔄 Scanning rotation " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== SOUND EFFECTS ====================
    
    public boolean isSoundEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ROBOTIC_SOUND_ENABLED, DEFAULT_SOUND_ENABLED);
        Log.d(TAG, "🔊 Robotic sound effects enabled: " + enabled);
        return enabled;
    }
    
    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ROBOTIC_SOUND_ENABLED, enabled).apply();
        Log.d(TAG, "🔊 Robotic sound effects " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== PIECE PROFILES ====================
    
    public boolean isPieceProfilesEnabled() {
        boolean enabled = prefs.getBoolean(KEY_ROBOTIC_PIECE_PROFILES_ENABLED, DEFAULT_PIECE_PROFILES_ENABLED);
        Log.d(TAG, "♟️ Piece-specific profiles enabled: " + enabled);
        return enabled;
    }
    
    public void setPieceProfilesEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ROBOTIC_PIECE_PROFILES_ENABLED, enabled).apply();
        Log.d(TAG, "♟️ Piece-specific profiles " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== STYLE APPLICATION ====================
    
    /**
     * Apply current settings to RoboticPieceAnimator (configuration only)
     * Note: RoboticPieceAnimator doesn't have theme methods like other renderers,
     * so we provide configuration data instead
     */
    public RoboticAnimationConfig getAnimationConfig() {
        if (!isRoboticAnimationEnabled()) {
            return new RoboticAnimationConfig(false);
        }
        
        RoboticAnimationConfig config = new RoboticAnimationConfig(true);
        config.style = getRoboticAnimationStyle();
        config.speedMultiplier = getSpeedMultiplier();
        config.overshootEnabled = isOvershootEnabled();
        config.jitterEnabled = isJitterEnabled();
        config.rotationEnabled = isRotationEnabled();
        config.soundEnabled = isSoundEnabled();
        config.pieceProfilesEnabled = isPieceProfilesEnabled();
        
        Log.d(TAG, "🎨 Generated animation config: " + config.toString());
        return config;
    }
    
    /**
     * Configuration data for robotic animations
     */
    public static class RoboticAnimationConfig {
        public boolean enabled;
        public String style;
        public float speedMultiplier;
        public boolean overshootEnabled;
        public boolean jitterEnabled;
        public boolean rotationEnabled;
        public boolean soundEnabled;
        public boolean pieceProfilesEnabled;
        
        public RoboticAnimationConfig(boolean enabled) {
            this.enabled = enabled;
            this.style = DEFAULT_STYLE;
            this.speedMultiplier = 1f;
            this.overshootEnabled = DEFAULT_OVERSHOOT_ENABLED;
            this.jitterEnabled = DEFAULT_JITTER_ENABLED;
            this.rotationEnabled = DEFAULT_ROTATION_ENABLED;
            this.soundEnabled = DEFAULT_SOUND_ENABLED;
            this.pieceProfilesEnabled = DEFAULT_PIECE_PROFILES_ENABLED;
        }
        
        @Override
        public String toString() {
            return String.format("RoboticConfig(enabled=%s, style=%s, speed=%.1fx, overshoot=%s, jitter=%s, rotation=%s)", 
                               enabled, style, speedMultiplier, overshootEnabled, jitterEnabled, rotationEnabled);
        }
    }
    
    // ==================== STYLE DEFINITIONS ====================
    
    /**
     * Apply style-specific parameters to animation config
     */
    public void applyStyleToConfig(RoboticAnimationConfig config) {
        switch (config.style) {
            case STYLE_MECHANICAL:
                // Default mechanical style - all effects enabled
                config.overshootEnabled = true;
                config.jitterEnabled = true;
                config.rotationEnabled = true;
                Log.d(TAG, "🔧 Applied mechanical style");
                break;
                
            case STYLE_SERVO:
                // Precise servo movement - minimal overshoot, more rotation
                config.overshootEnabled = false;
                config.jitterEnabled = true;
                config.rotationEnabled = true;
                Log.d(TAG, "⚙️ Applied servo style");
                break;
                
            case STYLE_INDUSTRIAL:
                // Heavy industrial movement - more pronounced effects
                config.overshootEnabled = true;
                config.jitterEnabled = true;
                config.rotationEnabled = false; // Industrial machines don't scan
                Log.d(TAG, "🏭 Applied industrial style");
                break;
                
            case STYLE_PRECISE:
                // High-precision movement - minimal effects
                config.overshootEnabled = false;
                config.jitterEnabled = false;
                config.rotationEnabled = true; // Only scanning for precision
                Log.d(TAG, "🎯 Applied precise style");
                break;
                
            case STYLE_EXPERIMENTAL:
                // Experimental robotic movement - exaggerated effects
                config.overshootEnabled = true;
                config.jitterEnabled = true;
                config.rotationEnabled = true;
                Log.d(TAG, "🧪 Applied experimental style");
                break;
                
            default:
                Log.w(TAG, "⚠️ Unknown style: " + config.style + ", using mechanical");
                config.style = STYLE_MECHANICAL;
                break;
        }
    }
    
    // ==================== CONVENIENCE METHODS ====================
    
    /**
     * Get all available animation styles for UI selection
     */
    public static String[] getAvailableStyles() {
        return new String[]{
            STYLE_MECHANICAL,
            STYLE_SERVO,
            STYLE_INDUSTRIAL,
            STYLE_PRECISE,
            STYLE_EXPERIMENTAL
        };
    }
    
    /**
     * Get user-friendly style names
     */
    public static String[] getStyleDisplayNames() {
        return new String[]{
            "Mechanical Robot",
            "Precision Servo",
            "Industrial Machine",
            "Laboratory Precise",
            "Experimental AI"
        };
    }
    
    /**
     * Reset all settings to defaults
     */
    public void resetToDefaults() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ROBOTIC_ANIMATION_ENABLED, DEFAULT_ENABLED);
        editor.putString(KEY_ROBOTIC_ANIMATION_STYLE, DEFAULT_STYLE);
        editor.putInt(KEY_ROBOTIC_ANIMATION_SPEED, DEFAULT_SPEED);
        editor.putBoolean(KEY_ROBOTIC_OVERSHOOT_ENABLED, DEFAULT_OVERSHOOT_ENABLED);
        editor.putBoolean(KEY_ROBOTIC_JITTER_ENABLED, DEFAULT_JITTER_ENABLED);
        editor.putBoolean(KEY_ROBOTIC_ROTATION_ENABLED, DEFAULT_ROTATION_ENABLED);
        editor.putBoolean(KEY_ROBOTIC_SOUND_ENABLED, DEFAULT_SOUND_ENABLED);
        editor.putBoolean(KEY_ROBOTIC_PIECE_PROFILES_ENABLED, DEFAULT_PIECE_PROFILES_ENABLED);
        editor.apply();
        Log.d(TAG, "🔄 Robotic animation settings reset to defaults");
    }
    
    /**
     * Log current settings state
     */
    public void logCurrentSettings() {
        Log.d(TAG, "📊 Current Robotic Animation Settings:");
        Log.d(TAG, "  Enabled: " + isRoboticAnimationEnabled());
        Log.d(TAG, "  Style: " + getRoboticAnimationStyle());
        Log.d(TAG, "  Speed: " + getRoboticAnimationSpeed() + "%");
        Log.d(TAG, "  Overshoot: " + isOvershootEnabled());
        Log.d(TAG, "  Jitter: " + isJitterEnabled());
        Log.d(TAG, "  Rotation: " + isRotationEnabled());
        Log.d(TAG, "  Sound: " + isSoundEnabled());
        Log.d(TAG, "  Piece Profiles: " + isPieceProfilesEnabled());
    }
    
    /**
     * Check if any robotic effects are enabled
     */
    public boolean hasAnyEffectsEnabled() {
        return isRoboticAnimationEnabled() && 
               (isOvershootEnabled() || isJitterEnabled() || isRotationEnabled() || isPieceProfilesEnabled());
    }
    
    /**
     * Get complexity level based on settings (for performance optimization)
     * @return 0 = disabled, 1 = basic, 2 = standard, 3 = full effects
     */
    public int getComplexityLevel() {
        if (!isRoboticAnimationEnabled()) return 0;
        
        int effectCount = 0;
        if (isOvershootEnabled()) effectCount++;
        if (isJitterEnabled()) effectCount++;
        if (isRotationEnabled()) effectCount++;
        if (isPieceProfilesEnabled()) effectCount++;
        
        if (effectCount >= 4) return 3; // Full effects
        if (effectCount >= 2) return 2; // Standard effects
        if (effectCount >= 1) return 1; // Basic effects
        return 0; // No effects
    }
}