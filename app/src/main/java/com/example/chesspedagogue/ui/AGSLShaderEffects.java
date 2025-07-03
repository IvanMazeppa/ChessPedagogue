package com.example.chesspedagogue.ui;

import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;
import android.view.View;

/**
 * Advanced Android Graphics Shading Language (AGSL) effects for Android 15/API 35
 * Provides hardware-accelerated visual effects for chess piece highlights and captures
 */
public class AGSLShaderEffects {
    private static final String TAG = "AGSLShaderEffects";
    
    // AGSL shader for move highlighting with radial pulse effect
    private static final String MOVE_HIGHLIGHT_SHADER = 
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
        "    // Calculate distance from center\n" +
        "    float dist = distance(uv, centerNorm);\n" +
        "    \n" +
        "    // Create pulsing effect\n" +
        "    float pulse = sin(time * 6.0) * 0.5 + 0.5;\n" +
        "    float radius = 0.1 + pulse * 0.05;\n" +
        "    \n" +
        "    // Soft radial gradient\n" +
        "    float alpha = smoothstep(radius + 0.02, radius, dist) * intensity;\n" +
        "    \n" +
        "    // Create glow effect\n" +
        "    float glow = exp(-dist * 8.0) * pulse * 0.3;\n" +
        "    \n" +
        "    return half4(highlightColor, alpha + glow);\n" +
        "}\n";
    
    // AGSL shader for capture flash effect
    private static final String CAPTURE_FLASH_SHADER = 
        "uniform float2 resolution;\n" +
        "uniform float time;\n" +
        "uniform float2 center;\n" +
        "uniform float intensity;\n" +
        "uniform float3 flashColor;\n" +
        "\n" +
        "half4 main(float2 fragCoord) {\n" +
        "    float2 uv = fragCoord / resolution;\n" +
        "    float2 centerNorm = center / resolution;\n" +
        "    \n" +
        "    // Calculate distance from center\n" +
        "    float dist = distance(uv, centerNorm);\n" +
        "    \n" +
        "    // Create expanding flash\n" +
        "    float flash = 1.0 - smoothstep(0.0, time * 0.5, dist);\n" +
        "    flash *= (1.0 - time); // Fade out over time\n" +
        "    \n" +
        "    // Add sparkle effect\n" +
        "    float sparkle = sin(dist * 20.0 - time * 10.0) * 0.5 + 0.5;\n" +
        "    sparkle *= exp(-dist * 5.0);\n" +
        "    \n" +
        "    float alpha = (flash + sparkle * 0.3) * intensity;\n" +
        "    \n" +
        "    return half4(flashColor, alpha);\n" +
        "}\n";
    
    // AGSL shader for adaptive board tinting based on game state
    private static final String ADAPTIVE_TINT_SHADER = 
        "uniform shader sourceTexture;\n" +
        "uniform float2 resolution;\n" +
        "uniform float gameProgress;  // 0.0 = opening, 0.5 = midgame, 1.0 = endgame\n" +
        "uniform float evaluation;    // -1.0 to 1.0 (black advantage to white advantage)\n" +
        "\n" +
        "half4 main(float2 fragCoord) {\n" +
        "    half4 color = sourceTexture.eval(fragCoord);\n" +
        "    \n" +
        "    // Create tint based on game progress\n" +
        "    float3 openingTint = float3(0.2, 0.8, 0.9);   // Teal\n" +
        "    float3 midgameTint = float3(0.9, 0.7, 0.2);   // Gold\n" +
        "    float3 endgameTint = float3(0.8, 0.2, 0.4);   // Red\n" +
        "    \n" +
        "    float3 progressTint = mix(\n" +
        "        mix(openingTint, midgameTint, smoothstep(0.0, 0.5, gameProgress)),\n" +
        "        endgameTint,\n" +
        "        smoothstep(0.5, 1.0, gameProgress)\n" +
        "    );\n" +
        "    \n" +
        "    // Add evaluation-based tint\n" +
        "    float3 whiteTint = float3(1.0, 1.0, 0.9);     // Warm white\n" +
        "    float3 blackTint = float3(0.9, 0.9, 1.0);     // Cool blue\n" +
        "    \n" +
        "    float3 evalTint = mix(blackTint, whiteTint, (evaluation + 1.0) * 0.5);\n" +
        "    \n" +
        "    // Combine tints\n" +
        "    float3 finalTint = mix(progressTint, evalTint, 0.3);\n" +
        "    \n" +
        "    // Apply subtle tint overlay\n" +
        "    color.rgb = mix(color.rgb, color.rgb * finalTint, 0.1);\n" +
        "    \n" +
        "    return color;\n" +
        "}\n";
    
    /**
     * Check if AGSL shaders are supported (Android 13+/API 33+)
     */
    public static boolean isAGSLSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }
    
    /**
     * Create move highlight effect with radial pulse
     * @param view Target view to apply effect
     * @param centerX X coordinate of highlight center (0-1)
     * @param centerY Y coordinate of highlight center (0-1)
     * @param intensity Effect intensity (0-1)
     * @param colorR Red component (0-1)
     * @param colorG Green component (0-1)
     * @param colorB Blue component (0-1)
     */
    public static void applyMoveHighlight(View view, float centerX, float centerY, 
                                        float intensity, float colorR, float colorG, float colorB) {
        if (!isAGSLSupported()) {
            Log.w(TAG, "AGSL not supported, skipping move highlight effect");
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(MOVE_HIGHLIGHT_SHADER);
            
            // Set shader uniforms
            shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
            shader.setFloatUniform("time", System.currentTimeMillis() / 1000.0f);
            shader.setFloatUniform("center", centerX * view.getWidth(), centerY * view.getHeight());
            shader.setFloatUniform("intensity", intensity);
            shader.setFloatUniform("highlightColor", colorR, colorG, colorB);
            
            // Apply shader as render effect
            RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "input");
            view.setRenderEffect(effect);
            
            Log.d(TAG, "✨ Applied AGSL move highlight effect");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply move highlight shader", e);
        }
    }
    
    /**
     * Create capture flash effect
     * @param view Target view to apply effect
     * @param centerX X coordinate of flash center (0-1)
     * @param centerY Y coordinate of flash center (0-1)
     * @param intensity Effect intensity (0-1)
     * @param durationMs Effect duration in milliseconds
     */
    public static void applyCaptureFlash(View view, float centerX, float centerY, 
                                       float intensity, long durationMs) {
        if (!isAGSLSupported()) {
            Log.w(TAG, "AGSL not supported, skipping capture flash effect");
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(CAPTURE_FLASH_SHADER);
            
            // Animate the flash effect
            long startTime = System.currentTimeMillis();
            Runnable animator = new Runnable() {
                @Override
                public void run() {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = Math.min(1.0f, elapsed / (float) durationMs);
                    
                    // Set shader uniforms
                    shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
                    shader.setFloatUniform("time", progress);
                    shader.setFloatUniform("center", centerX * view.getWidth(), centerY * view.getHeight());
                    shader.setFloatUniform("intensity", intensity * (1.0f - progress));
                    shader.setFloatUniform("flashColor", 1.0f, 1.0f, 0.8f); // Bright yellow-white
                    
                    // Apply shader effect
                    RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "input");
                    view.setRenderEffect(effect);
                    
                    if (progress < 1.0f) {
                        view.post(this); // Continue animation
                    } else {
                        view.setRenderEffect(null); // Clear effect when done
                        Log.d(TAG, "✨ Capture flash effect completed");
                    }
                }
            };
            
            view.post(animator);
            Log.d(TAG, "✨ Started AGSL capture flash effect");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply capture flash shader", e);
        }
    }
    
    /**
     * Apply adaptive tinting based on game state
     * @param view Target view to apply effect
     * @param gameProgress Game progress (0.0 = opening, 0.5 = midgame, 1.0 = endgame)
     * @param evaluation Position evaluation (-1.0 to 1.0)
     */
    public static void applyAdaptiveTint(View view, float gameProgress, float evaluation) {
        if (!isAGSLSupported()) {
            Log.w(TAG, "AGSL not supported, skipping adaptive tint effect");
            return;
        }
        
        try {
            RuntimeShader shader = new RuntimeShader(ADAPTIVE_TINT_SHADER);
            
            // Set shader uniforms
            shader.setFloatUniform("resolution", view.getWidth(), view.getHeight());
            shader.setFloatUniform("gameProgress", Math.max(0.0f, Math.min(1.0f, gameProgress)));
            shader.setFloatUniform("evaluation", Math.max(-1.0f, Math.min(1.0f, evaluation)));
            
            // Apply shader effect
            RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "sourceTexture");
            view.setRenderEffect(effect);
            
            Log.d(TAG, "✨ Applied AGSL adaptive tint effect (progress=" + gameProgress + ", eval=" + evaluation + ")");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply adaptive tint shader", e);
        }
    }
    
    /**
     * Clear all shader effects from a view
     */
    public static void clearEffects(View view) {
        if (view != null) {
            view.setRenderEffect(null);
            Log.d(TAG, "🧹 Cleared AGSL shader effects");
        }
    }
    
    /**
     * Create a hardware-accelerated blur effect (simpler alternative)
     * @param view Target view
     * @param radiusPx Blur radius in pixels
     */
    public static void applyHardwareBlur(View view, float radiusPx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                RenderEffect blurEffect = RenderEffect.createBlurEffect(
                    radiusPx, radiusPx, 
                    android.graphics.Shader.TileMode.CLAMP
                );
                view.setRenderEffect(blurEffect);
                Log.d(TAG, "✨ Applied hardware blur effect (radius=" + radiusPx + "px)");
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to apply hardware blur", e);
            }
        }
    }
}