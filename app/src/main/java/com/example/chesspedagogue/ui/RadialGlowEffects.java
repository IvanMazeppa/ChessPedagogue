package com.example.chesspedagogue.ui;

import android.animation.ValueAnimator;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Radial Glow Effects - Per BUILDING-A-MODERN-CHESS-APP-UI.md specifications
 * 
 * Features:
 * - Radial glow behind active panels with pulsing animation
 * - Material You accent colors for glow
 * - Subtle animation that draws attention on user's turn
 */
public class RadialGlowEffects {
    
    private static final String TAG = "RadialGlowEffects";
    
    /**
     * Add radial glow behind a panel that pulses subtly
     * Per design doc: "Radial glow behind the move list panel, which subtly pulses to draw attention on your turn"
     */
    public static void addRadialGlowBehindPanel(View panel, int glowColor) {
        try {
            Log.d(TAG, "🌟 Adding radial glow behind panel...");
            
            ViewGroup parent = (ViewGroup) panel.getParent();
            if (parent == null) {
                Log.e(TAG, "❌ Panel has no parent for glow effect");
                return;
            }
            
            // Create glow view with radial gradient
            View glowView = new View(panel.getContext());
            
            // Position glow behind panel
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                panel.getWidth() + 100, // Slightly larger than panel
                panel.getHeight() + 100
            );
            glowView.setLayoutParams(layoutParams);
            
            // Create radial gradient shader
            RadialGradient radialGradient = new RadialGradient(
                (panel.getWidth() + 100) / 2f, // centerX
                (panel.getHeight() + 100) / 2f, // centerY
                Math.max(panel.getWidth(), panel.getHeight()) / 2f + 50, // radius
                new int[]{glowColor, 0x00000000}, // colors: glow to transparent
                new float[]{0.0f, 1.0f}, // positions
                Shader.TileMode.CLAMP
            );
            
            // Apply gradient to drawable
            ShapeDrawable glowDrawable = new ShapeDrawable(new OvalShape());
            glowDrawable.getPaint().setShader(radialGradient);
            glowView.setBackground(glowDrawable);
            
            // Position glow view behind panel
            glowView.setX(panel.getX() - 50);
            glowView.setY(panel.getY() - 50);
            glowView.setElevation(panel.getElevation() - 1);
            
            // Add to parent
            parent.addView(glowView, 0); // Add at index 0 to be behind panel
            
            // Start subtle pulsing animation
            startPulsingAnimation(glowView);
            
            Log.d(TAG, "✅ Radial glow effect added successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to add radial glow effect", e);
        }
    }
    
    /**
     * Start subtle pulsing animation for glow effect
     * Per design doc: "subtly pulses to draw attention"
     */
    private static void startPulsingAnimation(View glowView) {
        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(0.3f, 0.8f);
        pulseAnimator.setDuration(2000); // 2 second pulse cycle
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        pulseAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            glowView.setAlpha(alpha);
        });
        
        pulseAnimator.start();
        
        Log.d(TAG, "🌊 Pulsing animation started for radial glow");
    }
    
    /**
     * Add radial glow for move list panel specifically
     * Per design doc specification for move list enhancement
     */
    public static void addMoveListRadialGlow(View moveListPanel) {
        // Material You accent color for glow (teal/blue theme)
        int glowColor = 0x4040A0E0; // Teal glow with 25% alpha
        addRadialGlowBehindPanel(moveListPanel, glowColor);
    }
    
    /**
     * Add radial glow for active button/panel
     */
    public static void addActiveRadialGlow(View activePanel) {
        // Brighter glow for active elements
        int glowColor = 0x6060C0FF; // Bright blue glow with 38% alpha
        addRadialGlowBehindPanel(activePanel, glowColor);
    }
    
    /**
     * Remove radial glow effect from a panel
     */
    public static void removeRadialGlow(View panel) {
        try {
            ViewGroup parent = (ViewGroup) panel.getParent();
            if (parent == null) return;
            
            // Find and remove glow views (they should be behind the panel)
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getTag() != null && child.getTag().equals("radial_glow")) {
                    parent.removeView(child);
                    Log.d(TAG, "🗑️ Removed radial glow effect");
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to remove radial glow", e);
        }
    }
}