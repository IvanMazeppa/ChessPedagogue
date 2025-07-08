package com.example.chesspedagogue.ui.effects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * CircuitTraceSettingsManager - Manages circuit board move highlighting preferences
 * 
 * Features:
 * - Enable/disable circuit trace move highlights
 * - Theme selection (Cyberpunk, Matrix, Neon, Classic, Stealth)
 * - Flow animation control
 * - Glow effects control
 * - Last move trace persistence
 * - Integration with existing ChessPedagoguePrefs
 */
public class CircuitTraceSettingsManager {
    
    private static final String TAG = "CircuitTraceSettings";
    
    // SharedPreferences keys
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_CIRCUIT_TRACE_ENABLED = "circuit_trace_enabled";
    private static final String KEY_CIRCUIT_TRACE_THEME = "circuit_trace_theme";
    private static final String KEY_CIRCUIT_FLOW_ENABLED = "circuit_flow_enabled";
    private static final String KEY_CIRCUIT_GLOW_ENABLED = "circuit_glow_enabled";
    private static final String KEY_CIRCUIT_LAST_MOVE_ENABLED = "circuit_last_move_enabled";
    private static final String KEY_CIRCUIT_TRACE_OPACITY = "circuit_trace_opacity";
    
    // Default values
    private static final boolean DEFAULT_ENABLED = true;
    private static final String DEFAULT_THEME = "cyberpunk";
    private static final boolean DEFAULT_FLOW_ENABLED = true;
    private static final boolean DEFAULT_GLOW_ENABLED = true;
    private static final boolean DEFAULT_LAST_MOVE_ENABLED = true;
    private static final int DEFAULT_OPACITY = 85; // 0-100
    
    // Theme constants
    public static final String THEME_CYBERPUNK = "cyberpunk";
    public static final String THEME_MATRIX = "matrix";
    public static final String THEME_NEON = "neon";
    public static final String THEME_CLASSIC = "classic";
    public static final String THEME_STEALTH = "stealth";
    
    private final SharedPreferences prefs;
    private final Context context;
    
    public CircuitTraceSettingsManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "🔌 CircuitTraceSettingsManager initialized");
    }
    
    // ==================== ENABLED STATE ====================
    
    public boolean isCircuitTraceEnabled() {
        boolean enabled = prefs.getBoolean(KEY_CIRCUIT_TRACE_ENABLED, DEFAULT_ENABLED);
        return enabled;
    }
    
    public void setCircuitTraceEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_CIRCUIT_TRACE_ENABLED, enabled).apply();
        Log.d(TAG, "🔌 Circuit trace " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== THEME SELECTION ====================
    
    public String getCircuitTraceTheme() {
        String theme = prefs.getString(KEY_CIRCUIT_TRACE_THEME, DEFAULT_THEME);
        return theme;
    }
    
    public void setCircuitTraceTheme(String theme) {
        prefs.edit().putString(KEY_CIRCUIT_TRACE_THEME, theme).apply();
        Log.d(TAG, "🎨 Circuit trace theme set to: " + theme);
    }
    
    // ==================== FLOW ANIMATION ====================
    
    public boolean isFlowAnimationEnabled() {
        boolean enabled = prefs.getBoolean(KEY_CIRCUIT_FLOW_ENABLED, DEFAULT_FLOW_ENABLED);
        return enabled;
    }
    
    public void setFlowAnimationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_CIRCUIT_FLOW_ENABLED, enabled).apply();
        Log.d(TAG, "🌊 Circuit flow animation " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== GLOW EFFECTS ====================
    
    public boolean isGlowEnabled() {
        boolean enabled = prefs.getBoolean(KEY_CIRCUIT_GLOW_ENABLED, DEFAULT_GLOW_ENABLED);
        return enabled;
    }
    
    public void setGlowEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_CIRCUIT_GLOW_ENABLED, enabled).apply();
        Log.d(TAG, "✨ Circuit glow " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== LAST MOVE TRACE ====================
    
    public boolean isLastMoveTraceEnabled() {
        boolean enabled = prefs.getBoolean(KEY_CIRCUIT_LAST_MOVE_ENABLED, DEFAULT_LAST_MOVE_ENABLED);
        return enabled;
    }
    
    public void setLastMoveTraceEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_CIRCUIT_LAST_MOVE_ENABLED, enabled).apply();
        Log.d(TAG, "🔗 Last move trace " + (enabled ? "enabled" : "disabled"));
    }
    
    // ==================== OPACITY CONTROL ====================
    
    /**
     * Get trace opacity (0-100)
     */
    public int getCircuitTraceOpacity() {
        int opacity = prefs.getInt(KEY_CIRCUIT_TRACE_OPACITY, DEFAULT_OPACITY);
        return opacity;
    }
    
    public void setCircuitTraceOpacity(int opacity) {
        // Clamp to valid range
        opacity = Math.max(0, Math.min(100, opacity));
        prefs.edit().putInt(KEY_CIRCUIT_TRACE_OPACITY, opacity).apply();
        Log.d(TAG, "👁️ Circuit trace opacity set to: " + opacity + "%");
    }
    
    /**
     * Get opacity as alpha multiplier (0.0 - 1.0)
     */
    public float getOpacityAlpha() {
        return getCircuitTraceOpacity() / 100f;
    }
    
    // ==================== THEME APPLICATION ====================
    
    /**
     * Apply current settings to CircuitTraceRenderer
     */
    public void applySettingsToRenderer(CircuitTraceRenderer renderer) {
        if (!isCircuitTraceEnabled()) {
            return;
        }
        
        String theme = getCircuitTraceTheme();
        switch (theme) {
            case THEME_CYBERPUNK:
                renderer.applyCyberpunkCircuitTheme();
                break;
            case THEME_MATRIX:
                renderer.applyMatrixCircuitTheme();
                break;
            case THEME_NEON:
                renderer.applyNeonCircuitTheme();
                break;
            case THEME_CLASSIC:
                applyClassicCircuitTheme(renderer);
                break;
            case THEME_STEALTH:
                applyStealthCircuitTheme(renderer);
                break;
            default:
                renderer.applyCyberpunkCircuitTheme();
                Log.w(TAG, "⚠️ Unknown theme: " + theme + ", using cyberpunk");
                break;
        }
        
        // Apply feature settings
        renderer.setFlowAnimationEnabled(isFlowAnimationEnabled());
        renderer.setGlowEnabled(isGlowEnabled());
        
        Log.d(TAG, "🎨 Applied " + theme + " theme to renderer (flow=" + isFlowAnimationEnabled() + 
              ", glow=" + isGlowEnabled() + ", opacity=" + getCircuitTraceOpacity() + "%)");
    }
    
    private void applyClassicCircuitTheme(CircuitTraceRenderer renderer) {
        renderer.setTraceColor(0xFF4169E1); // Royal blue
        renderer.setGlowColor(0x404169E1); // Semi-transparent blue
        renderer.setNodeColor(0xFF228B22); // Forest green
        renderer.setFlowColor(0xFFFFD700); // Gold
        renderer.setGlowEnabled(true);
        renderer.setFlowAnimationEnabled(true);
        Log.d(TAG, "🎨 Applied classic circuit theme");
    }
    
    private void applyStealthCircuitTheme(CircuitTraceRenderer renderer) {
        renderer.setTraceColor(0xFF696969); // Dim gray
        renderer.setGlowColor(0x20696969); // Very faint gray
        renderer.setNodeColor(0xFF2F4F4F); // Dark slate gray
        renderer.setFlowColor(0xFF808080); // Gray
        renderer.setGlowEnabled(false); // Minimal visibility
        renderer.setFlowAnimationEnabled(false);
        Log.d(TAG, "🎨 Applied stealth circuit theme");
    }
    
    // ==================== CONVENIENCE METHODS ====================
    
    /**
     * Get all theme names for UI selection
     */
    public static String[] getAvailableThemes() {
        return new String[]{
            THEME_CYBERPUNK,
            THEME_MATRIX,
            THEME_NEON,
            THEME_CLASSIC,
            THEME_STEALTH
        };
    }
    
    /**
     * Get user-friendly theme names
     */
    public static String[] getThemeDisplayNames() {
        return new String[]{
            "Cyberpunk Circuit",
            "Matrix Grid",
            "Neon Traces",
            "Classic PCB",
            "Stealth Mode"
        };
    }
    
    /**
     * Reset all settings to defaults
     */
    public void resetToDefaults() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_CIRCUIT_TRACE_ENABLED, DEFAULT_ENABLED);
        editor.putString(KEY_CIRCUIT_TRACE_THEME, DEFAULT_THEME);
        editor.putBoolean(KEY_CIRCUIT_FLOW_ENABLED, DEFAULT_FLOW_ENABLED);
        editor.putBoolean(KEY_CIRCUIT_GLOW_ENABLED, DEFAULT_GLOW_ENABLED);
        editor.putBoolean(KEY_CIRCUIT_LAST_MOVE_ENABLED, DEFAULT_LAST_MOVE_ENABLED);
        editor.putInt(KEY_CIRCUIT_TRACE_OPACITY, DEFAULT_OPACITY);
        editor.apply();
        Log.d(TAG, "🔄 Circuit trace settings reset to defaults");
    }
    
    /**
     * Log current settings state
     */
    public void logCurrentSettings() {
        Log.d(TAG, "📊 Current Circuit Trace Settings:");
        Log.d(TAG, "  Enabled: " + isCircuitTraceEnabled());
        Log.d(TAG, "  Theme: " + getCircuitTraceTheme());
        Log.d(TAG, "  Flow Animation: " + isFlowAnimationEnabled());
        Log.d(TAG, "  Glow Effects: " + isGlowEnabled());
        Log.d(TAG, "  Last Move Trace: " + isLastMoveTraceEnabled());
        Log.d(TAG, "  Opacity: " + getCircuitTraceOpacity() + "%");
    }
    
    /**
     * Check if any circuit effects are enabled
     */
    public boolean hasAnyEffectsEnabled() {
        return isCircuitTraceEnabled() && 
               (isFlowAnimationEnabled() || isGlowEnabled() || isLastMoveTraceEnabled());
    }
    
    /**
     * Get performance level based on settings (for optimization)
     * @return 0 = minimal, 1 = moderate, 2 = full effects
     */
    public int getPerformanceLevel() {
        if (!isCircuitTraceEnabled()) return 0;
        
        boolean hasFlow = isFlowAnimationEnabled();
        boolean hasGlow = isGlowEnabled();
        
        if (hasFlow && hasGlow) return 2; // Full effects
        if (hasFlow || hasGlow) return 1; // Moderate effects
        return 0; // Minimal effects
    }
}