package com.example.chesspedagogue.ui.rendering.shaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;

/**
 * AGSL-powered neon renderer for incredible visual effects
 * Replaces BlurMaskFilter with hardware-accelerated shaders
 * Targets Android 13+ with fallback to Canvas rendering
 */
public class AGSLNeonRenderer {
    private static final String TAG = "AGSLNeonRenderer";
    
    private final Context context;
    private final NeonShaderManager shaderManager;
    private final ShaderConfig config;
    private final Paint shaderPaint;
    private final RectF tempRect = new RectF();
    
    // Performance monitoring
    private long lastFrameTime = 0;
    private int frameCount = 0;
    private boolean isEnabled = true;
    private boolean fallbackMode = false;
    
    // Animation timing
    private long animationStartTime;
    private float currentTime = 0.0f;
    
    public AGSLNeonRenderer(Context context) {
        this.context = context;
        this.shaderManager = NeonShaderManager.getInstance(context);
        this.config = ShaderConfig.getInstance();
        this.shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.animationStartTime = System.currentTimeMillis();
        
        // Load configuration from preferences
        config.loadFromPreferences(context);
        
        // Check AGSL support
        if (!shaderManager.isAGSLSupported()) {
            Log.w(TAG, "⚠️ AGSL not supported, enabling fallback mode");
            fallbackMode = true;
        } else {
            Log.d(TAG, "✅ AGSL renderer initialized for incredible neon effects");
        }
        
        setupPaint();
    }
    
    private void setupPaint() {
        shaderPaint.setStyle(Paint.Style.FILL);
        shaderPaint.setAntiAlias(true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            shaderPaint.setBlendMode(android.graphics.BlendMode.SCREEN); // Additive blending for glow
        }
    }
    
    /**
     * Render neon glow effect for a chessboard square
     */
    public void drawNeonSquare(Canvas canvas, float left, float top, float right, float bottom, boolean isLightSquare) {
        if (!isEnabled) return;
        
        long frameStart = System.nanoTime();
        
        // Update animation time
        updateAnimationTime();
        
        if (fallbackMode || !shaderManager.isAGSLSupported()) {
            drawFallbackNeon(canvas, left, top, right, bottom, isLightSquare);
            return;
        }
        
        try {
            // Get appropriate shader for current config
            RuntimeShader shader = shaderManager.getShaderForConfig(config);
            if (shader == null) {
                Log.w(TAG, "⚠️ Shader not available, using fallback");
                drawFallbackNeon(canvas, left, top, right, bottom, isLightSquare);
                return;
            }
            
            // Calculate square properties
            float squareWidth = right - left;
            float squareHeight = bottom - top;
            float centerX = left + squareWidth * 0.5f;
            float centerY = top + squareHeight * 0.5f;
            
            // Update thermal and battery state
            config.updateThermalState(context);
            config.updateBatteryMode(context);
            
            // Update shader uniforms with power-aware values
            shaderManager.updateShaderUniforms(
                shader, config,
                centerX, centerY,
                squareWidth, squareHeight,
                canvas.getWidth(), canvas.getHeight(),
                currentTime
            );
            
            // Apply shader as RenderEffect (API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                RenderEffect shaderEffect = RenderEffect.createRuntimeShaderEffect(shader, "background");
                // Note: Paint.setRenderEffect() was added in API 35, use View.setRenderEffect instead
                // For now, we'll use direct Canvas drawing with shader
                shaderPaint.setShader(shader);
            }
            
            // Draw the neon effect
            tempRect.set(
                left - config.getGlowRadius(),
                top - config.getGlowRadius(),
                right + config.getGlowRadius(),
                bottom + config.getGlowRadius()
            );
            
            canvas.drawRect(tempRect, shaderPaint);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error rendering AGSL neon effect: " + e.getMessage());
            // Fall back to basic rendering
            drawFallbackNeon(canvas, left, top, right, bottom, isLightSquare);
        }
        
        // Performance monitoring
        monitorPerformance(frameStart);
    }
    
    /**
     * Fallback rendering using traditional Canvas methods
     */
    private void drawFallbackNeon(Canvas canvas, float left, float top, float right, float bottom, boolean isLightSquare) {
        // Use existing BlurMaskFilter approach as fallback
        Paint fallbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        // Get colors from current theme
        int[] colors = config.getCurrentThemeColors();
        int glowColor = isLightSquare ? colors[0] : colors[1];
        
        // Apply intensity
        int alpha = (int)(255 * config.getIntensity());
        glowColor = (glowColor & 0x00FFFFFF) | (alpha << 24);
        
        fallbackPaint.setColor(glowColor);
        fallbackPaint.setStyle(Paint.Style.FILL);
        
        // Simple glow effect
        tempRect.set(left - 5, top - 5, right + 5, bottom + 5);
        canvas.drawRect(tempRect, fallbackPaint);
        
        // Inner square
        fallbackPaint.setAlpha(alpha / 2);
        tempRect.set(left, top, right, bottom);
        canvas.drawRect(tempRect, fallbackPaint);
    }
    
    /**
     * Update animation timing
     */
    private void updateAnimationTime() {
        long currentMillis = System.currentTimeMillis();
        currentTime = (currentMillis - animationStartTime) / 1000.0f;
        
        // Prevent overflow after long runtime
        if (currentTime > 3600.0f) { // Reset after 1 hour
            animationStartTime = currentMillis;
            currentTime = 0.0f;
        }
    }
    
    /**
     * Monitor rendering performance
     */
    private void monitorPerformance(long frameStartNs) {
        long frameTime = System.nanoTime() - frameStartNs;
        frameCount++;
        
        // Adaptive performance adjustment
        config.adjustPerformanceForFrameTime(frameTime);
        
        // Log performance stats every 120 frames (1 second at 120fps)
        if (frameCount % 120 == 0) {
            float avgFrameTime = frameTime / 1_000_000.0f; // Convert to milliseconds
            Log.d(TAG, String.format("🎯 Frame %d: %.2fms, Config: %s", 
                  frameCount, avgFrameTime, config.getPerformanceLevel()));
        }
        
        lastFrameTime = frameTime;
    }
    
    /**
     * Update configuration and apply settings
     */
    public void updateConfiguration(ShaderConfig newConfig) {
        // Update theme colors if changed
        int[] currentColors = config.getCurrentThemeColors();
        int[] newColors = newConfig.getCurrentThemeColors();
        
        if (!java.util.Arrays.equals(currentColors, newColors)) {
            config.updateThemeColors(newColors[0], newColors[1], newColors[2]);
            Log.d(TAG, "🎨 Updated theme colors");
        }
        
        // Apply other settings
        config.setIntensity(newConfig.getIntensity());
        config.setGlowRadius(newConfig.getGlowRadius());
        config.setPulseFrequency(newConfig.getPulseFrequency());
        config.setPulseAmplitude(newConfig.getPulseAmplitude());
        config.setColorCycleSpeed(newConfig.getColorCycleSpeed());
        config.setEnergyLevel(newConfig.getEnergyLevel());
        config.setAnimationType(newConfig.getAnimationType());
        config.setPerformanceLevel(newConfig.getPerformanceLevel());
        
        // Save to preferences
        config.saveToPreferences(context);
        
        Log.d(TAG, "⚙️ Configuration updated");
    }
    
    /**
     * Enable/disable neon rendering
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        Log.d(TAG, enabled ? "✅ Neon rendering enabled" : "❌ Neon rendering disabled");
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    /**
     * Force fallback mode (for testing or compatibility)
     */
    public void setFallbackMode(boolean fallback) {
        this.fallbackMode = fallback;
        Log.d(TAG, fallback ? "⚠️ Fallback mode enabled" : "🚀 AGSL mode enabled");
    }
    
    public boolean isFallbackMode() {
        return fallbackMode;
    }
    
    /**
     * Get current performance stats
     */
    public String getPerformanceStats() {
        float avgFrameTime = lastFrameTime / 1_000_000.0f;
        return String.format("Frames: %d, Avg: %.2fms, Level: %s, AGSL: %s",
                           frameCount, avgFrameTime, 
                           config.getPerformanceLevel(),
                           !fallbackMode ? "✅" : "❌");
    }
    
    /**
     * Get shader manager for advanced operations
     */
    public NeonShaderManager getShaderManager() {
        return shaderManager;
    }
    
    /**
     * Get current configuration
     */
    public ShaderConfig getConfig() {
        return config;
    }
    
    /**
     * Reset performance counters
     */
    public void resetPerformanceCounters() {
        frameCount = 0;
        animationStartTime = System.currentTimeMillis();
        Log.d(TAG, "📊 Performance counters reset");
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        // Clear shader from paint
        shaderPaint.setShader(null);
        shaderManager.clearCache();
        Log.d(TAG, "🗑️ AGSL renderer cleanup complete");
    }
}