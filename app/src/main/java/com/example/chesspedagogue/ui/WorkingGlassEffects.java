package com.example.chesspedagogue.ui;

import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.animation.ValueAnimator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.OvershootInterpolator;

/**
 * Working Glass Effects with Simplified AGSL for Samsung S23 Ultra
 * Optimized for high-end Android 15 devices with proper texture sampling
 */
public class WorkingGlassEffects {
    private static final String TAG = "WorkingGlassEffects";
    
    // Simplified frosted glass shader that actually works
    private static final String WORKING_GLASS_SHADER = 
        "uniform shader sourceTexture;\n" +
        "uniform float2 resolution;\n" +
        "uniform float blurIntensity;\n" +
        "uniform float glassOpacity;\n" +
        "uniform float3 tintColor;\n" +
        "\n" +
        "half4 main(float2 coord) {\n" +
        "    float2 uv = coord / resolution;\n" +
        "    \n" +
        "    // Simple blur with 3x3 kernel for performance\n" +
        "    half4 blur = half4(0.0);\n" +
        "    float blurStep = blurIntensity / resolution.x;\n" +
        "    \n" +
        "    // 9-point blur sampling\n" +
        "    blur += sourceTexture.eval(coord + float2(-blurStep, -blurStep)) * 0.0625;\n" +
        "    blur += sourceTexture.eval(coord + float2(0.0, -blurStep)) * 0.125;\n" +
        "    blur += sourceTexture.eval(coord + float2(blurStep, -blurStep)) * 0.0625;\n" +
        "    blur += sourceTexture.eval(coord + float2(-blurStep, 0.0)) * 0.125;\n" +
        "    blur += sourceTexture.eval(coord) * 0.25;\n" +
        "    blur += sourceTexture.eval(coord + float2(blurStep, 0.0)) * 0.125;\n" +
        "    blur += sourceTexture.eval(coord + float2(-blurStep, blurStep)) * 0.0625;\n" +
        "    blur += sourceTexture.eval(coord + float2(0.0, blurStep)) * 0.125;\n" +
        "    blur += sourceTexture.eval(coord + float2(blurStep, blurStep)) * 0.0625;\n" +
        "    \n" +
        "    // Apply glass tint\n" +
        "    blur.rgb = mix(blur.rgb, tintColor, 0.2);\n" +
        "    \n" +
        "    // Add glass reflection\n" +
        "    float reflection = smoothstep(0.2, 0.8, uv.y) * 0.15;\n" +
        "    blur.rgb += reflection;\n" +
        "    \n" +
        "    blur.a = glassOpacity;\n" +
        "    return blur;\n" +
        "}";
    
    // Move highlight shader with radial pulse - blends with source texture
    private static final String MOVE_HIGHLIGHT_SHADER = 
        "uniform shader sourceTexture;\n" +
        "uniform float2 resolution;\n" +
        "uniform float2 center;\n" +
        "uniform float time;\n" +
        "uniform float intensity;\n" +
        "uniform float3 highlightColor;\n" +
        "\n" +
        "half4 main(float2 coord) {\n" +
        "    float2 uv = coord / resolution;\n" +
        "    float2 centerUV = center / resolution;\n" +
        "    \n" +
        "    // Get original color\n" +
        "    half4 original = sourceTexture.eval(coord);\n" +
        "    \n" +
        "    float dist = distance(uv, centerUV);\n" +
        "    \n" +
        "    // Animated pulse\n" +
        "    float pulse = sin(time * 6.0) * 0.5 + 0.5;\n" +
        "    float ringRadius = 0.1 + pulse * 0.05;\n" +
        "    \n" +
        "    // Ring glow effect\n" +
        "    float ring = exp(-abs(dist - ringRadius) * 20.0) * pulse;\n" +
        "    \n" +
        "    // Center glow\n" +
        "    float centerGlow = exp(-dist * 8.0) * 0.3;\n" +
        "    \n" +
        "    float highlightAlpha = (ring + centerGlow) * intensity;\n" +
        "    \n" +
        "    // Blend highlight with original\n" +
        "    half4 highlight = half4(highlightColor, highlightAlpha);\n" +
        "    return mix(original, highlight, highlightAlpha);\n" +
        "}";
    
    // Capture flash shader - blends with source texture
    private static final String CAPTURE_FLASH_SHADER = 
        "uniform shader sourceTexture;\n" +
        "uniform float2 resolution;\n" +
        "uniform float2 center;\n" +
        "uniform float flashTime;\n" +
        "uniform float3 flashColor;\n" +
        "\n" +
        "half4 main(float2 coord) {\n" +
        "    float2 uv = coord / resolution;\n" +
        "    float2 centerUV = center / resolution;\n" +
        "    \n" +
        "    // Get original color\n" +
        "    half4 original = sourceTexture.eval(coord);\n" +
        "    \n" +
        "    float dist = distance(uv, centerUV);\n" +
        "    \n" +
        "    // Flash and fade\n" +
        "    float flash = exp(-flashTime * 3.0) * exp(-dist * 5.0);\n" +
        "    \n" +
        "    // Blend flash with original\n" +
        "    half4 flashEffect = half4(flashColor, flash);\n" +
        "    return mix(original, flashEffect, flash);\n" +
        "}";
    
    /**
     * Apply working frosted glass effect optimized for S23 Ultra
     */
    public static void applyWorkingGlass(View view, float blurIntensity, float opacity, float[] tintColor) {
        if (!isAGSLSupported() || view == null) {
            Log.w(TAG, "AGSL not supported or view is null, using fallback");
            applyFallbackGlass(view, opacity);
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(WORKING_GLASS_SHADER);
            
            // Set shader uniforms
            shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
            shader.setFloatUniform("blurIntensity", blurIntensity);
            shader.setFloatUniform("glassOpacity", opacity);
            shader.setFloatUniform("tintColor", tintColor[0], tintColor[1], tintColor[2]);
            
            // Apply with proper input binding
            RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "sourceTexture");
            view.setRenderEffect(effect);
            
            Log.d(TAG, "✨ Applied working AGSL glass effect");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ AGSL shader failed, using fallback: " + e.getMessage());
            applyFallbackGlass(view, opacity);
        }
    }
    
    /**
     * Apply move highlight with radial pulse animation
     */
    public static void applyMoveHighlight(View view, float centerX, float centerY, 
                                        float intensity, float[] highlightColor) {
        if (!isAGSLSupported() || view == null) {
            Log.w(TAG, "AGSL not supported, skipping move highlight");
            return;
        }
        
        // Safety checks
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            Log.w(TAG, "View has invalid dimensions, skipping move highlight");
            return;
        }
        
        final float[] finalHighlightColor;
        if (highlightColor == null || highlightColor.length < 3) {
            Log.w(TAG, "Invalid highlight color, using default");
            finalHighlightColor = new float[]{1.0f, 0.8f, 0.2f}; // Golden fallback
        } else {
            finalHighlightColor = highlightColor;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(MOVE_HIGHLIGHT_SHADER);
            
            // Animate the highlight for 2 seconds
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 2f);
            animator.setDuration(2000);
            animator.addUpdateListener(animation -> {
                try {
                    // Additional safety check during animation
                    if (view.getWidth() <= 0 || view.getHeight() <= 0) {
                        return;
                    }
                    
                    float time = (float) animation.getAnimatedValue();
                    
                    shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
                    shader.setFloatUniform("center", centerX, centerY);
                    shader.setFloatUniform("time", time * 3.14159f); // Convert to radians
                    shader.setFloatUniform("intensity", intensity);
                    shader.setFloatUniform("highlightColor", finalHighlightColor[0], finalHighlightColor[1], finalHighlightColor[2]);
                    
                    RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "sourceTexture");
                    view.setRenderEffect(effect);
                } catch (Exception e) {
                    Log.e(TAG, "❌ Animation update failed: " + e.getMessage());
                    animation.cancel(); // Stop the problematic animation
                }
            });
            
            // Clear effect when animation ends
            animator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    try {
                        view.setRenderEffect(null);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to clear render effect: " + e.getMessage());
                    }
                }
            });
            
            animator.start();
            Log.d(TAG, "✨ Started move highlight animation");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Move highlight shader failed: " + e.getMessage());
            // Apply a simple fallback effect
            applyFallbackHighlight(view, centerX, centerY, intensity);
        }
    }
    
    /**
     * Fallback highlight effect without shaders
     */
    private static void applyFallbackHighlight(View view, float centerX, float centerY, float intensity) {
        try {
            // Simple alpha animation as fallback
            view.animate()
                .alpha(0.5f)
                .setDuration(300)
                .withEndAction(() -> view.animate().alpha(1.0f).setDuration(300))
                .start();
            Log.d(TAG, "✨ Applied fallback highlight effect");
        } catch (Exception e) {
            Log.e(TAG, "❌ Even fallback highlight failed: " + e.getMessage());
        }
    }
    
    /**
     * Apply capture flash effect
     */
    public static void applyCaptureFlash(View view, float centerX, float centerY, float[] flashColor) {
        if (!isAGSLSupported() || view == null) {
            Log.w(TAG, "AGSL not supported, skipping capture flash");
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(CAPTURE_FLASH_SHADER);
            
            // Quick flash animation (0.5 seconds)
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(500);
            animator.addUpdateListener(animation -> {
                float flashTime = (float) animation.getAnimatedValue();
                
                shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
                shader.setFloatUniform("center", centerX, centerY);
                shader.setFloatUniform("flashTime", flashTime);
                shader.setFloatUniform("flashColor", flashColor[0], flashColor[1], flashColor[2]);
                
                RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "sourceTexture");
                view.setRenderEffect(effect);
                
                // Clear effect when done
                if (flashTime >= 0.95f) {
                    view.postDelayed(() -> view.setRenderEffect(null), 50);
                }
            });
            
            animator.start();
            Log.d(TAG, "✨ Started capture flash animation");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Capture flash shader failed: " + e.getMessage());
        }
    }
    
    /**
     * Chess-specific glass presets optimized for gameplay
     */
    public static class ChessGlassPresets {
        
        // Header glass with deep blue tint
        public static void applyHeaderGlass(View view) {
            float[] tint = {0.15f, 0.35f, 0.8f}; // Deep blue
            applyWorkingGlass(view, 12.0f, 0.85f, tint);
        }
        
        // Control panel glass with purple tint
        public static void applyControlGlass(View view) {
            float[] tint = {0.4f, 0.3f, 0.7f}; // Purple
            applyWorkingGlass(view, 8.0f, 0.75f, tint);
        }
        
        // Board highlight for last move
        public static void applyLastMoveHighlight(View view, float centerX, float centerY) {
            float[] color = {1.0f, 0.8f, 0.2f}; // Golden
            applyMoveHighlight(view, centerX, centerY, 0.6f, color);
        }
        
        // Capture flash effect
        public static void applyCaptureEffect(View view, float centerX, float centerY) {
            float[] color = {1.0f, 0.4f, 0.1f}; // Orange-red
            applyCaptureFlash(view, centerX, centerY, color);
        }
    }
    
    /**
     * Animated glass entrance effect
     */
    public static void animateGlassEntrance(View view, float finalOpacity, float[] tintColor) {
        // Start with no effect
        view.setRenderEffect(null);
        view.setScaleX(0.9f);
        view.setScaleY(0.9f);
        view.setAlpha(0.0f);
        
        // Animate scale and opacity
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        
        animSet.playTogether(scaleX, scaleY, alpha);
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator(1.2f));
        
        // Apply glass effect after scale animation starts
        alpha.addUpdateListener(animation -> {
            float progress = animation.getAnimatedFraction();
            if (progress > 0.3f) {
                float glassOpacity = finalOpacity * ((progress - 0.3f) / 0.7f);
                applyWorkingGlass(view, 10.0f, glassOpacity, tintColor);
            }
        });
        
        animSet.start();
        Log.d(TAG, "✨ Started glass entrance animation");
    }
    
    /**
     * Fallback glass effect for unsupported devices
     */
    private static void applyFallbackGlass(View view, float opacity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                RenderEffect blurEffect = RenderEffect.createBlurEffect(
                    12.0f, 12.0f, 
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
     * Check AGSL support
     */
    public static boolean isAGSLSupported() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return false;
        }
        
        // Additional device-specific safety checks
        try {
            // Test basic AGSL functionality with a simple shader
            String testShader = "half4 main(float2 coord) { return half4(1.0); }";
            RuntimeShader test = new RuntimeShader(testShader);
            Log.d(TAG, "✅ AGSL basic functionality test passed");
            
            // Log detailed test results
            VisualEffectsDebugger.logAGSLTest(true, null);
            return true;
        } catch (Exception e) {
            Log.w(TAG, "⚠️ AGSL test failed, using fallback effects: " + e.getMessage());
            
            // Log test failure
            VisualEffectsDebugger.logAGSLTest(false, e.getMessage());
            return false;
        }
    }
    
    /**
     * Clear all effects
     */
    public static void clearEffects(View view) {
        if (view != null) {
            view.setRenderEffect(null);
            view.setAlpha(1.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        }
    }
}