package com.example.chesspedagogue.ui.rendering.shaders;

import android.content.Context;
import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages AGSL shader compilation, caching, and lifecycle
 * Handles RuntimeShader creation for Android 13+ neon effects
 */
public class NeonShaderManager {
    private static final String TAG = "NeonShaderManager";
    
    // Shader cache for performance
    private static final Map<String, RuntimeShader> shaderCache = new HashMap<>();
    private static final Map<String, String> shaderSourceCache = new HashMap<>();
    
    private static NeonShaderManager instance;
    private Context context;
    private boolean isAGSLSupported;
    
    public static NeonShaderManager getInstance(Context context) {
        if (instance == null) {
            instance = new NeonShaderManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private NeonShaderManager(Context context) {
        this.context = context;
        this.isAGSLSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU; // API 33+
        
        if (isAGSLSupported) {
            Log.d(TAG, "✅ AGSL supported on Android " + Build.VERSION.SDK_INT);
            preloadShaders();
        } else {
            Log.w(TAG, "⚠️ AGSL not supported, falling back to Canvas rendering");
        }
    }
    
    /**
     * Check if AGSL shaders are supported on this device
     */
    public boolean isAGSLSupported() {
        return isAGSLSupported;
    }
    
    /**
     * Load and compile a shader from assets
     */
    public RuntimeShader loadShader(String assetPath) {
        if (!isAGSLSupported) {
            Log.w(TAG, "🚫 AGSL not supported, cannot load shader: " + assetPath);
            return null;
        }
        
        // Check cache first
        if (shaderCache.containsKey(assetPath)) {
            Log.d(TAG, "📦 Using cached shader: " + assetPath);
            return shaderCache.get(assetPath);
        }
        
        try {
            // Load shader source
            String shaderSource = loadShaderSource(assetPath);
            if (shaderSource == null) {
                Log.e(TAG, "❌ Failed to load shader source: " + assetPath);
                return null;
            }
            
            // Compile shader
            RuntimeShader shader = new RuntimeShader(shaderSource);
            
            // Cache the compiled shader
            shaderCache.put(assetPath, shader);
            Log.d(TAG, "✅ Compiled and cached shader: " + assetPath);
            
            return shader;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error compiling shader " + assetPath + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load shader source code from assets
     */
    private String loadShaderSource(String assetPath) {
        // Check source cache first
        if (shaderSourceCache.containsKey(assetPath)) {
            return shaderSourceCache.get(assetPath);
        }
        
        try {
            InputStream inputStream = context.getAssets().open(assetPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\\n");
            }
            
            reader.close();
            inputStream.close();
            
            String source = sb.toString();
            shaderSourceCache.put(assetPath, source);
            
            Log.d(TAG, "📖 Loaded shader source: " + assetPath + " (" + source.length() + " chars)");
            return source;
            
        } catch (IOException e) {
            Log.e(TAG, "❌ Error loading shader source " + assetPath + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Preload commonly used shaders for better performance
     */
    private void preloadShaders() {
        Log.d(TAG, "🔄 Preloading AGSL shaders...");
        
        // Preload all neon shaders
        loadShader("shaders/neon_glow.agsl");
        loadShader("shaders/neon_pulse.agsl");
        loadShader("shaders/neon_multi_layer.agsl");
        
        Log.d(TAG, "✅ Preloaded " + shaderCache.size() + " shaders");
    }
    
    /**
     * Get shader for current configuration
     */
    public RuntimeShader getShaderForConfig(ShaderConfig config) {
        String assetPath = config.getRecommendedShaderAsset();
        return loadShader(assetPath);
    }
    
    /**
     * Update shader uniforms based on current configuration
     */
    public void updateShaderUniforms(RuntimeShader shader, ShaderConfig config, 
                                   float squareCenterX, float squareCenterY, 
                                   float squareWidth, float squareHeight,
                                   float resolutionX, float resolutionY,
                                   float currentTime) {
        if (shader == null || !isAGSLSupported) return;
        
        try {
            // Basic uniforms for all shaders (with power optimization)
            shader.setFloatUniform("iResolution", resolutionX, resolutionY);
            shader.setFloatUniform("iTime", currentTime);
            shader.setFloatUniform("intensity", config.getEffectiveIntensity()); // Power-aware intensity
            shader.setFloatUniform("glowRadius", config.getGlowRadius());
            shader.setFloatUniform("squareCenter", squareCenterX, squareCenterY);
            shader.setFloatUniform("squareSize", squareWidth, squareHeight);
            
            // Color uniforms
            int[] colors = config.getCurrentThemeColors();
            shader.setFloatUniform("neonColor", config.getColorAsFloat3(colors[0]));
            
            // Advanced uniforms for pulse and multi-layer shaders
            if (config.getAnimationType() != ShaderConfig.AnimationType.STATIC) {
                shader.setFloatUniform("pulseFreq", config.getEffectivePulseFrequency()); // Power-aware frequency
                shader.setFloatUniform("pulseAmplitude", config.getPulseAmplitude());
            }
            
            // Harmonic pulse specific uniforms
            if (config.getAnimationType() == ShaderConfig.AnimationType.HARMONIC_PULSE) {
                shader.setFloatUniform("baseFrequency", config.getEffectivePulseFrequency());
                shader.setFloatUniform("harmonicRatio", config.getPulseAmplitude() * 0.8f);
                shader.setFloatUniform("resonanceStrength", config.getEffectiveIntensity() * 0.6f);
            }
            
            // Color cycling specific uniforms
            if (config.getAnimationType() == ShaderConfig.AnimationType.COLOR_CYCLE) {
                // Pass all theme colors for cycling
                for (int i = 0; i < Math.min(colors.length, 3); i++) {
                    String uniformName = "themeColors[" + i + "]";
                    shader.setFloatUniform(uniformName, config.getColorAsFloat3(colors[i]));
                }
                shader.setFloatUniform("cycleSpeed", config.getColorCycleSpeed());
                shader.setFloatUniform("cycleAmplitude", config.getPulseAmplitude());
                shader.setIntUniform("currentThemeIndex", 0); // Default theme index
            }
            
            // Electric storm specific uniforms
            if (config.getAnimationType() == ShaderConfig.AnimationType.ELECTRIC_STORM) {
                shader.setFloatUniform("stormIntensity", config.getEffectiveIntensity() * 1.2f);
                shader.setFloatUniform("lightningFreq", config.getEffectivePulseFrequency() * 2.0f);
                shader.setFloatUniform("electricField", config.getEnergyLevel() * config.getEffectiveIntensity());
            }
            
            // Multi-layer specific uniforms
            if (config.getAnimationType() == ShaderConfig.AnimationType.MULTI_LAYER ||
                config.getAnimationType() == ShaderConfig.AnimationType.ELECTRIC_STORM) {
                shader.setFloatUniform("primaryColor", config.getColorAsFloat3(colors[0]));
                shader.setFloatUniform("secondaryColor", config.getColorAsFloat3(colors[1]));
                shader.setFloatUniform("accentColor", config.getColorAsFloat3(colors[2]));
                shader.setFloatUniform("colorCycleSpeed", config.getColorCycleSpeed());
                shader.setFloatUniform("energyLevel", config.getEnergyLevel());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating shader uniforms: " + e.getMessage());
        }
    }
    
    /**
     * Clear shader cache (for memory management)
     */
    public void clearCache() {
        shaderCache.clear();
        shaderSourceCache.clear();
        Log.d(TAG, "🗑️ Shader cache cleared");
    }
    
    /**
     * Get cache statistics for debugging
     */
    public String getCacheStats() {
        return String.format("Shader cache: %d compiled, %d sources", 
                           shaderCache.size(), shaderSourceCache.size());
    }
    
    /**
     * Validate shader compilation status
     */
    public boolean validateShader(String assetPath) {
        RuntimeShader shader = loadShader(assetPath);
        return shader != null;
    }
    
    /**
     * Hot reload shader (for development)
     */
    public RuntimeShader reloadShader(String assetPath) {
        // Remove from cache
        shaderCache.remove(assetPath);
        shaderSourceCache.remove(assetPath);
        
        // Load fresh
        return loadShader(assetPath);
    }
}