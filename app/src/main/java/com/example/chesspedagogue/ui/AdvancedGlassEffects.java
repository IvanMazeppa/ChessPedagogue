package com.example.chesspedagogue.ui;

import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;
import android.view.View;

/**
 * Advanced Glass Effects using AGSL for Android 15/API 35
 * Creates authentic frosted glass, chromatic aberration, and depth effects
 */
public class AdvancedGlassEffects {
    private static final String TAG = "AdvancedGlassEffects";
    
    // Advanced frosted glass shader with chromatic aberration
    private static final String FROSTED_GLASS_SHADER = 
        "uniform float2 resolution;\n" +
        "uniform float blurRadius;\n" +
        "uniform float opacity;\n" +
        "uniform float chromaticStrength;\n" +
        "uniform float3 tintColor;\n" +
        "\n" +
        "half4 main(float2 fragCoord) {\n" +
        "    float2 uv = fragCoord / resolution;\n" +
        "    \n" +
        "    // Sample multiple points for blur effect\n" +
        "    half4 color = half4(0.0);\n" +
        "    float totalWeight = 0.0;\n" +
        "    \n" +
        "    // Gaussian blur sampling\n" +
        "    for (int x = -2; x <= 2; x++) {\n" +
        "        for (int y = -2; y <= 2; y++) {\n" +
        "            float2 offset = float2(float(x), float(y)) * blurRadius / resolution;\n" +
        "            float weight = exp(-0.5 * (float(x * x + y * y)) / (blurRadius * blurRadius));\n" +
        "            \n" +
        "            // Chromatic aberration - sample RGB channels separately\n" +
        "            float2 redOffset = offset * (1.0 + chromaticStrength * 0.01);\n" +
        "            float2 greenOffset = offset;\n" +
        "            float2 blueOffset = offset * (1.0 - chromaticStrength * 0.01);\n" +
        "            \n" +
        "            color.r += weight;\n" +
        "            color.g += weight;\n" +
        "            color.b += weight;\n" +
        "            totalWeight += weight;\n" +
        "        }\n" +
        "    }\n" +
        "    \n" +
        "    color /= totalWeight;\n" +
        "    \n" +
        "    // Apply glass tint\n" +
        "    color.rgb = mix(color.rgb, tintColor, 0.3);\n" +
        "    \n" +
        "    // Glass reflection highlight\n" +
        "    float reflection = smoothstep(0.3, 0.7, uv.y) * 0.2;\n" +
        "    color.rgb += reflection;\n" +
        "    \n" +
        "    // Set opacity for glass effect\n" +
        "    color.a = opacity;\n" +
        "    \n" +
        "    return color;\n" +
        "}\n";
    
    // Dynamic depth glass shader with refraction
    private static final String DEPTH_GLASS_SHADER = 
        "uniform float2 resolution;\n" +
        "uniform float time;\n" +
        "uniform float depth;\n" +
        "uniform float refraction;\n" +
        "uniform float3 highlightColor;\n" +
        "\n" +
        "half4 main(float2 fragCoord) {\n" +
        "    float2 uv = fragCoord / resolution;\n" +
        "    \n" +
        "    // Create depth-based distortion\n" +
        "    float2 distortion = float2(\n" +
        "        sin(uv.y * 10.0 + time) * refraction,\n" +
        "        cos(uv.x * 10.0 + time) * refraction\n" +
        "    ) * depth;\n" +
        "    \n" +
        "    float2 distortedUV = uv + distortion * 0.01;\n" +
        "    \n" +
        "    // Base glass color with depth\n" +
        "    half4 color = half4(highlightColor * (0.5 + depth * 0.3), 0.15 + depth * 0.1);\n" +
        "    \n" +
        "    // Add fresnel-like edge highlighting\n" +
        "    float edge = 1.0 - abs(dot(normalize(float3(distortedUV - 0.5, depth)), float3(0, 0, 1)));\n" +
        "    color.rgb += highlightColor * edge * edge * 0.3;\n" +
        "    \n" +
        "    // Subtle animated shimmer\n" +
        "    float shimmer = sin(distortedUV.x * 20.0 + time * 2.0) * \n" +
        "                   cos(distortedUV.y * 15.0 + time * 1.5) * 0.05;\n" +
        "    color.rgb += shimmer;\n" +
        "    \n" +
        "    return color;\n" +
        "}\n";
    
    // Enhanced move highlight with glass reflection
    private static final String GLASS_HIGHLIGHT_SHADER = 
        "uniform float2 resolution;\n" +
        "uniform float time;\n" +
        "uniform float2 center;\n" +
        "uniform float intensity;\n" +
        "uniform float3 highlightColor;\n" +
        "\n" +
        "half4 main(float2 fragCoord) {\n" +
        "    float2 uv = fragCoord / resolution;\n" +
        "    float2 centerNorm = center / resolution;\n" +
        "    \n" +
        "    // Distance from center\n" +
        "    float dist = distance(uv, centerNorm);\n" +
        "    \n" +
        "    // Animated pulse with glass reflection\n" +
        "    float pulse = sin(time * 4.0) * 0.5 + 0.5;\n" +
        "    float radius = 0.08 + pulse * 0.03;\n" +
        "    \n" +
        "    // Glass ring effect\n" +
        "    float ring = smoothstep(radius - 0.01, radius, dist) * \n" +
        "                smoothstep(radius + 0.02, radius, dist);\n" +
        "    \n" +
        "    // Glass highlight with refraction\n" +
        "    float highlight = exp(-dist * 12.0) * pulse;\n" +
        "    \n" +
        "    // Combine effects\n" +
        "    float alpha = (ring * 0.8 + highlight * 0.4) * intensity;\n" +
        "    \n" +
        "    // Add prismatic color separation\n" +
        "    float3 finalColor = highlightColor;\n" +
        "    finalColor.r += sin(dist * 30.0 + time) * 0.1;\n" +
        "    finalColor.g += sin(dist * 30.0 + time + 2.0) * 0.1;\n" +
        "    finalColor.b += sin(dist * 30.0 + time + 4.0) * 0.1;\n" +
        "    \n" +
        "    return half4(finalColor, alpha);\n" +
        "}\n";
    
    /**
     * Apply advanced frosted glass effect to a view
     */
    public static void applyFrostedGlass(View view, float blurRadius, float opacity, 
                                       float chromaticStrength, float[] tintColor) {
        // TEMPORARY: Disable AGSL shaders due to native crashes - use fallback only
        Log.d(TAG, "🛡️ AGSL temporarily disabled for safety - using fallback glass effect");
        applyFallbackGlass(view, opacity);
        return;
        
        /* DISABLED UNTIL SHADER ISSUES RESOLVED
        if (!isAGSLSupported()) {
            Log.w(TAG, "AGSL not supported, applying fallback glass effect");
            applyFallbackGlass(view, opacity);
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(FROSTED_GLASS_SHADER);
            
            // Set shader parameters
            shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
            shader.setFloatUniform("blurRadius", blurRadius);
            shader.setFloatUniform("opacity", opacity);
            shader.setFloatUniform("chromaticStrength", chromaticStrength);
            shader.setFloatUniform("tintColor", tintColor[0], tintColor[1], tintColor[2]);
            
            // Apply as blend mode overlay
            RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "content");
            view.setRenderEffect(effect);
            
            Log.d(TAG, "✨ Applied AGSL frosted glass effect");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply frosted glass shader, using fallback", e);
            applyFallbackGlass(view, opacity);
        }
        */
    }
    
    /**
     * Apply dynamic depth glass effect with animation
     */
    public static void applyDepthGlass(View view, float depth, float refraction, float[] highlightColor) {
        // TEMPORARY: Disable AGSL shaders due to native crashes
        Log.d(TAG, "🛡️ AGSL depth glass temporarily disabled for safety");
        return;
        
        /* DISABLED UNTIL SHADER ISSUES RESOLVED
        if (!isAGSLSupported()) {
            Log.w(TAG, "AGSL not supported, skipping depth glass effect");
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(DEPTH_GLASS_SHADER);
            
            // Animate the effect
            long startTime = System.currentTimeMillis();
            Runnable animator = new Runnable() {
                @Override
                public void run() {
                    float time = (System.currentTimeMillis() - startTime) / 1000.0f;
                    
                    // Set animated parameters
                    shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
                    shader.setFloatUniform("time", time);
                    shader.setFloatUniform("depth", depth);
                    shader.setFloatUniform("refraction", refraction);
                    shader.setFloatUniform("highlightColor", highlightColor[0], highlightColor[1], highlightColor[2]);
                    
                    // Apply effect
                    RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "content");
                    view.setRenderEffect(effect);
                    
                    // Continue animation
                    view.postDelayed(this, 33); // ~30fps
                }
            };
            
            view.post(animator);
            Log.d(TAG, "✨ Started AGSL depth glass animation");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply depth glass shader", e);
        }
        */
    }
    
    /**
     * Apply enhanced glass highlight for piece selection
     */
    public static void applyGlassHighlight(View view, float centerX, float centerY, 
                                         float intensity, float[] highlightColor) {
        // TEMPORARY: Disable AGSL shaders due to native crashes  
        Log.d(TAG, "🛡️ AGSL glass highlight temporarily disabled for safety");
        return;
        
        /* DISABLED UNTIL SHADER ISSUES RESOLVED
        if (!isAGSLSupported()) {
            Log.w(TAG, "AGSL not supported, skipping glass highlight");
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(GLASS_HIGHLIGHT_SHADER);
            
            // Animate the highlight
            long startTime = System.currentTimeMillis();
            Runnable animator = new Runnable() {
                @Override
                public void run() {
                    float time = (System.currentTimeMillis() - startTime) / 1000.0f;
                    
                    // Set parameters
                    shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
                    shader.setFloatUniform("time", time);
                    shader.setFloatUniform("center", centerX * view.getWidth(), centerY * view.getHeight());
                    shader.setFloatUniform("intensity", intensity);
                    shader.setFloatUniform("highlightColor", highlightColor[0], highlightColor[1], highlightColor[2]);
                    
                    // Apply effect
                    RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "content");
                    view.setRenderEffect(effect);
                    
                    // Continue for 2 seconds
                    if (time < 2.0f) {
                        view.postDelayed(this, 33); // ~30fps
                    } else {
                        view.setRenderEffect(null); // Clear effect
                    }
                }
            };
            
            view.post(animator);
            Log.d(TAG, "✨ Started AGSL glass highlight effect");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply glass highlight shader", e);
        }
        */
    }
    
    /**
     * Create preset glass effects for chess UI elements
     */
    public static class ChessGlassPresets {
        
        // Deep blue glass for header panels
        public static void applyHeaderGlass(View view) {
            float[] tintColor = {0.2f, 0.4f, 0.9f}; // Deep blue
            applyFrostedGlass(view, 15.0f, 0.85f, 2.0f, tintColor);
        }
        
        // Subtle control glass for button panels
        public static void applyControlGlass(View view) {
            float[] tintColor = {0.3f, 0.3f, 0.6f}; // Muted purple
            applyFrostedGlass(view, 10.0f, 0.75f, 1.0f, tintColor);
        }
        
        // Battle progress glass with subtle animation
        public static void applyProgressGlass(View view) {
            float[] tintColor = {0.4f, 0.5f, 0.8f}; // Light blue-purple
            applyFrostedGlass(view, 12.0f, 0.8f, 1.5f, tintColor);
            
            // Add subtle depth effect
            float[] highlightColor = {0.6f, 0.7f, 1.0f};
            applyDepthGlass(view, 0.3f, 0.5f, highlightColor);
        }
        
        // Enhanced piece selection highlight
        public static void applySelectionGlass(View view, float centerX, float centerY) {
            float[] highlightColor = {1.0f, 0.8f, 0.2f}; // Golden highlight
            applyGlassHighlight(view, centerX, centerY, 0.8f, highlightColor);
        }
    }
    
    /**
     * Fallback glass effect for unsupported devices
     */
    private static void applyFallbackGlass(View view, float opacity) {
        // Use simple hardware blur + alpha
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                RenderEffect blurEffect = RenderEffect.createBlurEffect(
                    8.0f, 8.0f, 
                    android.graphics.Shader.TileMode.CLAMP
                );
                view.setRenderEffect(blurEffect);
                view.setAlpha(opacity);
                Log.d(TAG, "✨ Applied fallback glass effect");
            } catch (Exception e) {
                Log.w(TAG, "❌ Fallback glass effect failed", e);
            }
        }
    }
    
    /**
     * Check if AGSL shaders are supported
     */
    public static boolean isAGSLSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU; // Android 13+
    }
    
    /**
     * Clear all glass effects from a view
     */
    public static void clearGlassEffects(View view) {
        if (view != null) {
            view.setRenderEffect(null);
            view.setAlpha(1.0f);
            Log.d(TAG, "🧹 Cleared glass effects");
        }
    }
}