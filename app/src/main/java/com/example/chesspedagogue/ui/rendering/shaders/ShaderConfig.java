package com.example.chesspedagogue.ui.rendering.shaders;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Centralized configuration for AGSL neon shader parameters
 * Manages shader uniforms, performance settings, and theme integration
 */
public class ShaderConfig {
    private static final String TAG = "ShaderConfig";
    
    // Shader performance levels
    public enum PerformanceLevel {
        LOW(1),      // Basic glow only
        MEDIUM(2),   // Glow + pulse
        HIGH(3),     // Full multi-layer effects
        ULTRA(4);    // All effects + advanced features
        
        private final int level;
        PerformanceLevel(int level) { this.level = level; }
        public int getLevel() { return level; }
    }
    
    // Animation types
    public enum AnimationType {
        STATIC,           // No animation
        PULSE,            // Breathing effect
        COLOR_CYCLE,      // Color transitions
        HARMONIC_PULSE,   // Multiple frequency pulsing
        MULTI_LAYER,      // Complex layered effects
        ELECTRIC_STORM    // Advanced crackling effects
    }
    
    // Thermal management levels
    public enum ThermalState {
        COOL(1.0f),       // Full performance
        WARM(0.8f),       // Slightly reduced
        HOT(0.6f),        // Significantly reduced
        CRITICAL(0.3f);   // Minimal effects
        
        private final float performanceScale;
        ThermalState(float scale) { this.performanceScale = scale; }
        public float getPerformanceScale() { return performanceScale; }
    }
    
    // Battery optimization modes
    public enum BatteryMode {
        UNLIMITED,        // Plugged in or high battery
        CONSERVATIVE,     // Moderate battery saving
        AGGRESSIVE       // Maximum battery saving
    }
    
    // Default shader parameters
    public static final float DEFAULT_INTENSITY = 0.8f;
    public static final float DEFAULT_GLOW_RADIUS = 20.0f;
    public static final float DEFAULT_PULSE_FREQUENCY = 2.0f;
    public static final float DEFAULT_PULSE_AMPLITUDE = 0.3f;
    public static final float DEFAULT_COLOR_CYCLE_SPEED = 1.5f;
    public static final float DEFAULT_ENERGY_LEVEL = 1.0f;
    
    // Performance thresholds (for auto-adjustment)
    public static final int TARGET_FPS = 120;
    public static final int MIN_FPS = 60;
    public static final long MAX_FRAME_TIME_NS = 8_333_333L; // ~120fps
    
    // Current configuration
    private float intensity = DEFAULT_INTENSITY;
    private float glowRadius = DEFAULT_GLOW_RADIUS;
    private float pulseFrequency = DEFAULT_PULSE_FREQUENCY;
    private float pulseAmplitude = DEFAULT_PULSE_AMPLITUDE;
    private float colorCycleSpeed = DEFAULT_COLOR_CYCLE_SPEED;
    private float energyLevel = DEFAULT_ENERGY_LEVEL;
    private PerformanceLevel performanceLevel = PerformanceLevel.HIGH;
    private AnimationType animationType = AnimationType.PULSE;
    private boolean adaptivePerformance = true;
    
    // Thermal and battery management
    private ThermalState currentThermalState = ThermalState.COOL;
    private BatteryMode batteryMode = BatteryMode.UNLIMITED;
    private float thermalScale = 1.0f;
    private long lastThermalCheck = 0;
    private long lastBatteryCheck = 0;
    private boolean powerOptimizationEnabled = true;
    
    // Color theme integration
    private int[] currentThemeColors = {0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00}; // Default electric blue/green
    
    private static ShaderConfig instance;
    
    public static ShaderConfig getInstance() {
        if (instance == null) {
            instance = new ShaderConfig();
        }
        return instance;
    }
    
    private ShaderConfig() {
        // Private constructor for singleton
    }
    
    /**
     * Load configuration from SharedPreferences
     */
    public void loadFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        
        intensity = prefs.getFloat("shader_intensity", DEFAULT_INTENSITY);
        glowRadius = prefs.getFloat("shader_glow_radius", DEFAULT_GLOW_RADIUS);
        pulseFrequency = prefs.getFloat("shader_pulse_freq", DEFAULT_PULSE_FREQUENCY);
        pulseAmplitude = prefs.getFloat("shader_pulse_amp", DEFAULT_PULSE_AMPLITUDE);
        colorCycleSpeed = prefs.getFloat("shader_color_cycle", DEFAULT_COLOR_CYCLE_SPEED);
        energyLevel = prefs.getFloat("shader_energy", DEFAULT_ENERGY_LEVEL);
        adaptivePerformance = prefs.getBoolean("shader_adaptive", true);
        
        String perfLevel = prefs.getString("shader_performance", "HIGH");
        performanceLevel = PerformanceLevel.valueOf(perfLevel);
        
        String animType = prefs.getString("shader_animation", "PULSE");
        animationType = AnimationType.valueOf(animType);
    }
    
    /**
     * Save configuration to SharedPreferences
     */
    public void saveToPreferences(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE).edit();
        
        editor.putFloat("shader_intensity", intensity);
        editor.putFloat("shader_glow_radius", glowRadius);
        editor.putFloat("shader_pulse_freq", pulseFrequency);
        editor.putFloat("shader_pulse_amp", pulseAmplitude);
        editor.putFloat("shader_color_cycle", colorCycleSpeed);
        editor.putFloat("shader_energy", energyLevel);
        editor.putBoolean("shader_adaptive", adaptivePerformance);
        editor.putString("shader_performance", performanceLevel.name());
        editor.putString("shader_animation", animationType.name());
        
        editor.apply();
    }
    
    /**
     * Update colors from current neon theme
     */
    public void updateThemeColors(int primaryColor, int secondaryColor, int accentColor) {
        currentThemeColors[0] = primaryColor;
        currentThemeColors[1] = secondaryColor;
        currentThemeColors[2] = accentColor;
    }
    
    /**
     * Convert Android color int to shader float3
     */
    public float[] getColorAsFloat3(int colorInt) {
        return new float[] {
            ((colorInt >> 16) & 0xFF) / 255.0f,  // Red
            ((colorInt >> 8) & 0xFF) / 255.0f,   // Green
            (colorInt & 0xFF) / 255.0f           // Blue
        };
    }
    
    /**
     * Automatically adjust performance based on frame timing
     */
    public void adjustPerformanceForFrameTime(long frameTimeNs) {
        if (!adaptivePerformance) return;
        
        if (frameTimeNs > MAX_FRAME_TIME_NS * 2) {
            // Frame time too slow, reduce effects
            if (performanceLevel.getLevel() > 1) {
                performanceLevel = PerformanceLevel.values()[performanceLevel.getLevel() - 2];
                android.util.Log.d(TAG, "🔧 Reduced performance level to " + performanceLevel);
            }
        } else if (frameTimeNs < MAX_FRAME_TIME_NS * 0.7) {
            // Frame time good, can potentially increase effects
            if (performanceLevel.getLevel() < 4) {
                performanceLevel = PerformanceLevel.values()[performanceLevel.getLevel()];
                android.util.Log.d(TAG, "🚀 Increased performance level to " + performanceLevel);
            }
        }
    }
    
    // Getters and setters
    public float getIntensity() { return intensity; }
    public void setIntensity(float intensity) { this.intensity = Math.max(0.0f, Math.min(1.0f, intensity)); }
    
    public float getGlowRadius() { return glowRadius; }
    public void setGlowRadius(float glowRadius) { this.glowRadius = Math.max(5.0f, Math.min(100.0f, glowRadius)); }
    
    public float getPulseFrequency() { return pulseFrequency; }
    public void setPulseFrequency(float freq) { this.pulseFrequency = Math.max(0.1f, Math.min(10.0f, freq)); }
    
    public float getPulseAmplitude() { return pulseAmplitude; }
    public void setPulseAmplitude(float amp) { this.pulseAmplitude = Math.max(0.0f, Math.min(1.0f, amp)); }
    
    public float getColorCycleSpeed() { return colorCycleSpeed; }
    public void setColorCycleSpeed(float speed) { this.colorCycleSpeed = Math.max(0.0f, Math.min(5.0f, speed)); }
    
    public float getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(float energy) { this.energyLevel = Math.max(0.1f, Math.min(2.0f, energy)); }
    
    public PerformanceLevel getPerformanceLevel() { return performanceLevel; }
    public void setPerformanceLevel(PerformanceLevel level) { this.performanceLevel = level; }
    
    public AnimationType getAnimationType() { return animationType; }
    public void setAnimationType(AnimationType type) { this.animationType = type; }
    
    public boolean isAdaptivePerformance() { return adaptivePerformance; }
    public void setAdaptivePerformance(boolean adaptive) { this.adaptivePerformance = adaptive; }
    
    public int[] getCurrentThemeColors() { return currentThemeColors.clone(); }
    
    /**
     * Get recommended shader based on performance level, animation type, and thermal state
     */
    public String getRecommendedShaderAsset() {
        // Apply thermal throttling
        PerformanceLevel effectiveLevel = getEffectivePerformanceLevel();
        
        switch (effectiveLevel) {
            case LOW:
                return "shaders/neon_glow.agsl";
            case MEDIUM:
                if (animationType == AnimationType.STATIC) {
                    return "shaders/neon_glow.agsl";
                } else if (animationType == AnimationType.COLOR_CYCLE) {
                    return "shaders/neon_color_cycle.agsl";
                } else {
                    return "shaders/neon_pulse.agsl";
                }
            case HIGH:
                switch (animationType) {
                    case STATIC:
                        return "shaders/neon_glow.agsl";
                    case PULSE:
                        return "shaders/neon_pulse.agsl";
                    case COLOR_CYCLE:
                        return "shaders/neon_color_cycle.agsl";
                    case HARMONIC_PULSE:
                        return "shaders/neon_harmonic_pulse.agsl";
                    case MULTI_LAYER:
                        return "shaders/neon_multi_layer.agsl";
                    case ELECTRIC_STORM:
                        return "shaders/neon_electric_storm.agsl";
                    default:
                        return "shaders/neon_pulse.agsl";
                }
            case ULTRA:
            default:
                switch (animationType) {
                    case STATIC:
                        return "shaders/neon_glow.agsl";
                    case PULSE:
                        return "shaders/neon_harmonic_pulse.agsl"; // Upgrade to harmonic
                    case COLOR_CYCLE:
                        return "shaders/neon_color_cycle.agsl";
                    case HARMONIC_PULSE:
                        return "shaders/neon_harmonic_pulse.agsl";
                    case MULTI_LAYER:
                        return "shaders/neon_multi_layer.agsl";
                    case ELECTRIC_STORM:
                        return "shaders/neon_electric_storm.agsl";
                    default:
                        return "shaders/neon_harmonic_pulse.agsl";
                }
        }
    }
    
    /**
     * Get effective performance level considering thermal and battery state
     */
    private PerformanceLevel getEffectivePerformanceLevel() {
        if (!powerOptimizationEnabled) {
            return performanceLevel;
        }
        
        // Calculate thermal throttling
        int thermalReduction = 0;
        switch (currentThermalState) {
            case WARM:
                thermalReduction = 1;
                break;
            case HOT:
                thermalReduction = 2;
                break;
            case CRITICAL:
                thermalReduction = 3;
                break;
        }
        
        // Calculate battery throttling
        int batteryReduction = 0;
        switch (batteryMode) {
            case CONSERVATIVE:
                batteryReduction = 1;
                break;
            case AGGRESSIVE:
                batteryReduction = 2;
                break;
        }
        
        // Apply reductions
        int targetLevel = performanceLevel.getLevel() - Math.max(thermalReduction, batteryReduction);
        targetLevel = Math.max(1, Math.min(4, targetLevel)); // Clamp to valid range
        
        return PerformanceLevel.values()[targetLevel - 1];
    }
    
    /**
     * Update thermal state based on device temperature
     */
    public void updateThermalState(Context context) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThermalCheck < 5000) return; // Check every 5 seconds
        
        lastThermalCheck = currentTime;
        
        // Estimate thermal state based on frame timing and device characteristics
        if (adaptivePerformance) {
            // Simple heuristic: if we've had to reduce performance multiple times, assume thermal throttling
            long frameDrops = getCurrentFrameDrops();
            if (frameDrops > 10) {
                currentThermalState = ThermalState.HOT;
                android.util.Log.w("ShaderConfig", "🔥 Thermal throttling detected - reducing effects");
            } else if (frameDrops > 5) {
                currentThermalState = ThermalState.WARM;
                android.util.Log.i("ShaderConfig", "🌡️ Device warming - moderate throttling");
            } else {
                currentThermalState = ThermalState.COOL;
            }
        }
        
        thermalScale = currentThermalState.getPerformanceScale();
    }
    
    /**
     * Update battery optimization mode
     */
    public void updateBatteryMode(Context context) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBatteryCheck < 30000) return; // Check every 30 seconds
        
        lastBatteryCheck = currentTime;
        
        try {
            android.content.IntentFilter filter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
            android.content.Intent batteryStatus = context.registerReceiver(null, filter);
            
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1);
                int status = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1);
                
                boolean isCharging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING ||
                                   status == android.os.BatteryManager.BATTERY_STATUS_FULL;
                
                float batteryPct = (level * 100) / (float) scale;
                
                if (isCharging || batteryPct > 80) {
                    batteryMode = BatteryMode.UNLIMITED;
                } else if (batteryPct > 30) {
                    batteryMode = BatteryMode.CONSERVATIVE;
                } else {
                    batteryMode = BatteryMode.AGGRESSIVE;
                    android.util.Log.w("ShaderConfig", "🔋 Low battery - aggressive power saving");
                }
            }
        } catch (Exception e) {
            android.util.Log.e("ShaderConfig", "Error checking battery status: " + e.getMessage());
        }
    }
    
    /**
     * Apply thermal and battery scaling to intensity
     */
    public float getEffectiveIntensity() {
        float baseIntensity = intensity;
        
        if (powerOptimizationEnabled) {
            // Apply thermal scaling
            baseIntensity *= thermalScale;
            
            // Apply battery scaling
            switch (batteryMode) {
                case CONSERVATIVE:
                    baseIntensity *= 0.8f;
                    break;
                case AGGRESSIVE:
                    baseIntensity *= 0.5f;
                    break;
            }
        }
        
        return Math.max(0.1f, Math.min(1.0f, baseIntensity));
    }
    
    /**
     * Apply thermal and battery scaling to animation speed
     */
    public float getEffectivePulseFrequency() {
        float baseFreq = pulseFrequency;
        
        if (powerOptimizationEnabled) {
            // Slower animations save power
            switch (batteryMode) {
                case CONSERVATIVE:
                    baseFreq *= 0.8f;
                    break;
                case AGGRESSIVE:
                    baseFreq *= 0.5f;
                    break;
            }
            
            // Thermal throttling also reduces animation speed
            baseFreq *= thermalScale;
        }
        
        return Math.max(0.1f, Math.min(10.0f, baseFreq));
    }
    
    /**
     * Simple frame drop counter for thermal estimation
     */
    private long getCurrentFrameDrops() {
        // This would be implemented with actual frame timing data
        // For now, return 0 as placeholder
        return 0;
    }
}