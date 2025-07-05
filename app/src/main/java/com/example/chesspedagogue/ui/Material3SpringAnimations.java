package com.example.chesspedagogue.ui;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

/**
 * Material 3 Spring Animations - Per BUILDING-A-MODERN-CHESS-APP-UI.md specifications
 * 
 * Features:
 * - Spring-based button animations (API 35)
 * - Micro-interactions for enhanced user feedback
 * - Material You motion principles
 */
public class Material3SpringAnimations {
    
    private static final String TAG = "Material3Spring";
    
    // Material 3 spring configuration values
    private static final float SPRING_DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
    private static final float SPRING_STIFFNESS = SpringForce.STIFFNESS_LOW;
    
    /**
     * Apply spring-based button press animation
     * Per design doc: "Spring-based button animations (API 35)"
     */
    public static void applySpringButtonAnimation(View button) {
        try {
            Log.d(TAG, "🌊 Applying Material 3 spring button animation...");
            
            // Create spring animations for scale
            SpringAnimation scaleXAnim = new SpringAnimation(button, DynamicAnimation.SCALE_X, 1.0f);
            SpringAnimation scaleYAnim = new SpringAnimation(button, DynamicAnimation.SCALE_Y, 1.0f);
            
            // Configure spring properties per Material 3 guidelines
            scaleXAnim.getSpring()
                .setDampingRatio(SPRING_DAMPING_RATIO)
                .setStiffness(SPRING_STIFFNESS);
            
            scaleYAnim.getSpring()
                .setDampingRatio(SPRING_DAMPING_RATIO)
                .setStiffness(SPRING_STIFFNESS);
            
            // Set up touch listeners for interactive feedback
            button.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        // Spring down to 95% scale
                        scaleXAnim.animateToFinalPosition(0.95f);
                        scaleYAnim.animateToFinalPosition(0.95f);
                        return false; // Don't consume the event
                        
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        // Spring back to original size
                        scaleXAnim.animateToFinalPosition(1.0f);
                        scaleYAnim.animateToFinalPosition(1.0f);
                        return false; // Don't consume the event
                }
                return false;
            });
            
            Log.d(TAG, "✅ Spring button animation applied successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply spring button animation", e);
        }
    }
    
    /**
     * Apply entrance animation with spring physics
     * For panels appearing on screen
     */
    public static void applySpringEntranceAnimation(View view) {
        try {
            Log.d(TAG, "🚀 Applying spring entrance animation...");
            
            // Start from scaled down and transparent
            view.setScaleX(0.8f);
            view.setScaleY(0.8f);
            view.setAlpha(0f);
            
            // Spring to full size and opacity
            SpringAnimation scaleXAnim = new SpringAnimation(view, DynamicAnimation.SCALE_X, 1.0f);
            SpringAnimation scaleYAnim = new SpringAnimation(view, DynamicAnimation.SCALE_Y, 1.0f);
            SpringAnimation alphaAnim = new SpringAnimation(view, DynamicAnimation.ALPHA, 1.0f);
            
            // Configure springs with overshoot for dramatic entrance
            float stiffness = SpringForce.STIFFNESS_MEDIUM;
            float damping = SpringForce.DAMPING_RATIO_LOW_BOUNCY; // More bouncy for entrance
            
            scaleXAnim.getSpring().setStiffness(stiffness).setDampingRatio(damping);
            scaleYAnim.getSpring().setStiffness(stiffness).setDampingRatio(damping);
            alphaAnim.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH).setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
            
            // Start animations with slight delay for staggered effect
            alphaAnim.start();
            
            // Use postDelayed for staggered effect since SpringAnimation doesn't have setStartDelay
            view.postDelayed(() -> {
                scaleXAnim.start();
                scaleYAnim.start();
            }, 50);
            
            Log.d(TAG, "✅ Spring entrance animation started");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply spring entrance animation", e);
        }
    }
    
    /**
     * Apply micro-interaction animation for UI feedback
     * Per design doc: "micro-interactions"
     */
    public static void applyMicroInteraction(View view, float intensity) {
        try {
            // Quick spring animation for feedback
            SpringAnimation feedbackAnim = new SpringAnimation(view, DynamicAnimation.SCALE_X, 1.0f);
            feedbackAnim.getSpring()
                .setStiffness(SpringForce.STIFFNESS_HIGH)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
            
            // Brief scale up then back to normal
            view.setScaleX(1.0f + intensity);
            view.setScaleY(1.0f + intensity);
            
            feedbackAnim.animateToFinalPosition(1.0f);
            new SpringAnimation(view, DynamicAnimation.SCALE_Y, 1.0f)
                .getSpring()
                .setStiffness(SpringForce.STIFFNESS_HIGH)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
            
            Log.d(TAG, "💫 Micro-interaction animation triggered");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply micro-interaction", e);
        }
    }
    
    /**
     * Apply spring animation to move list updates
     * Per design doc: move list with enhanced animations
     */
    public static void applyMoveListUpdateAnimation(View moveListItem) {
        try {
            // Fade in with spring scale
            moveListItem.setAlpha(0f);
            moveListItem.setScaleX(0.9f);
            
            SpringAnimation scaleAnim = new SpringAnimation(moveListItem, DynamicAnimation.SCALE_X, 1.0f);
            SpringAnimation alphaAnim = new SpringAnimation(moveListItem, DynamicAnimation.ALPHA, 1.0f);
            
            scaleAnim.getSpring()
                .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);
            
            alphaAnim.getSpring()
                .setStiffness(SpringForce.STIFFNESS_HIGH)
                .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
            
            alphaAnim.start();
            
            // Use postDelayed for staggered effect
            moveListItem.postDelayed(() -> scaleAnim.start(), 100);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply move list animation", e);
        }
    }
    
    /**
     * Check if spring animations are fully supported
     */
    public static boolean isFullySupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM; // API 35
    }
}