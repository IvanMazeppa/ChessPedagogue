package com.example.chesspedagogue.ui;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Enhanced Adaptive Tinting System for Chess Game State
 * Creates sophisticated color transitions based on game progression and evaluation
 */
public class EnhancedAdaptiveTinting {
    private static final String TAG = "EnhancedAdaptiveTinting";
    
    /**
     * Enhanced color schemes for different game phases
     */
    public static class ColorSchemes {
        
        // Opening phase: Fresh, theoretical, hopeful
        public static class Opening {
            public static final int PRIMARY = Color.parseColor("#2E4F8B");      // Deep royal blue
            public static final int SECONDARY = Color.parseColor("#4A90E2");    // Bright blue
            public static final int ACCENT = Color.parseColor("#87CEEB");       // Sky blue
        }
        
        // Midgame phase: Complex, tactical, intense
        public static class Midgame {
            public static final int PRIMARY = Color.parseColor("#8B4513");      // Saddle brown
            public static final int SECONDARY = Color.parseColor("#CD853F");    // Peru
            public static final int ACCENT = Color.parseColor("#DAA520");       // Goldenrod
        }
        
        // Endgame phase: Precise, decisive, elegant
        public static class Endgame {
            public static final int PRIMARY = Color.parseColor("#722F37");      // Dark red
            public static final int SECONDARY = Color.parseColor("#A0522D");    // Sienna
            public static final int ACCENT = Color.parseColor("#DC143C");       // Crimson
        }
        
        // Evaluation-based modifiers
        public static class Evaluation {
            public static final int WHITE_ADVANTAGE = Color.parseColor("#F5F5DC"); // Beige (warm)
            public static final int BLACK_ADVANTAGE = Color.parseColor("#2F4F4F"); // Dark slate gray (cool)
            public static final int BALANCED = Color.parseColor("#DCDCDC");        // Gainsboro (neutral)
        }
    }
    
    /**
     * Apply sophisticated adaptive tinting with smooth transitions
     */
    public static void applyEnhancedGameStateTinting(View view, float gameProgress, 
                                                   float evaluation, long animationDuration) {
        if (view == null) return;
        
        Log.d(TAG, "🎨 Applying enhanced adaptive tinting - Progress: " + gameProgress + ", Eval: " + evaluation);
        
        // Get current game phase colors
        int[] phaseColors = getGamePhaseColors(gameProgress);
        
        // Apply evaluation modifier
        int finalColor = applyEvaluationModifier(phaseColors[0], evaluation);
        
        // Get current color for smooth transition
        android.content.res.ColorStateList currentTint = view.getBackgroundTintList();
        int currentColor = currentTint != null ? currentTint.getDefaultColor() : Color.TRANSPARENT;
        
        // Animate color transition
        animateColorTransition(view, currentColor, finalColor, animationDuration);
        
        Log.d(TAG, "✨ Applied enhanced tinting - Final color: " + Integer.toHexString(finalColor));
    }
    
    /**
     * Get interpolated colors for current game phase
     */
    private static int[] getGamePhaseColors(float gameProgress) {
        // Clamp progress
        gameProgress = Math.max(0.0f, Math.min(1.0f, gameProgress));
        
        int primaryColor;
        int secondaryColor;
        int accentColor;
        
        if (gameProgress < 0.33f) {
            // Opening phase (0-33%)
            float factor = gameProgress / 0.33f;
            primaryColor = interpolateColor(ColorSchemes.Opening.PRIMARY, ColorSchemes.Midgame.PRIMARY, factor);
            secondaryColor = interpolateColor(ColorSchemes.Opening.SECONDARY, ColorSchemes.Midgame.SECONDARY, factor);
            accentColor = interpolateColor(ColorSchemes.Opening.ACCENT, ColorSchemes.Midgame.ACCENT, factor);
        } else if (gameProgress < 0.66f) {
            // Midgame phase (33-66%)
            float factor = (gameProgress - 0.33f) / 0.33f;
            primaryColor = interpolateColor(ColorSchemes.Midgame.PRIMARY, ColorSchemes.Endgame.PRIMARY, factor);
            secondaryColor = interpolateColor(ColorSchemes.Midgame.SECONDARY, ColorSchemes.Endgame.SECONDARY, factor);
            accentColor = interpolateColor(ColorSchemes.Midgame.ACCENT, ColorSchemes.Endgame.ACCENT, factor);
        } else {
            // Endgame phase (66-100%)
            float factor = (gameProgress - 0.66f) / 0.34f;
            // Intensify endgame colors
            primaryColor = interpolateColor(ColorSchemes.Endgame.PRIMARY, 
                darkenColor(ColorSchemes.Endgame.PRIMARY, 0.2f), factor);
            secondaryColor = interpolateColor(ColorSchemes.Endgame.SECONDARY, 
                darkenColor(ColorSchemes.Endgame.SECONDARY, 0.2f), factor);
            accentColor = interpolateColor(ColorSchemes.Endgame.ACCENT, 
                darkenColor(ColorSchemes.Endgame.ACCENT, 0.2f), factor);
        }
        
        return new int[]{primaryColor, secondaryColor, accentColor};
    }
    
    /**
     * Apply evaluation-based color modifier
     */
    private static int applyEvaluationModifier(int baseColor, float evaluation) {
        // Clamp evaluation
        evaluation = Math.max(-1.0f, Math.min(1.0f, evaluation));
        
        int modifierColor;
        float mixStrength;
        
        if (evaluation > 0.1f) {
            // White advantage
            modifierColor = ColorSchemes.Evaluation.WHITE_ADVANTAGE;
            mixStrength = Math.min(evaluation * 0.3f, 0.4f); // Max 40% mix
        } else if (evaluation < -0.1f) {
            // Black advantage
            modifierColor = ColorSchemes.Evaluation.BLACK_ADVANTAGE;
            mixStrength = Math.min(Math.abs(evaluation) * 0.3f, 0.4f); // Max 40% mix
        } else {
            // Balanced position
            modifierColor = ColorSchemes.Evaluation.BALANCED;
            mixStrength = 0.1f; // Subtle neutral tint
        }
        
        return interpolateColor(baseColor, modifierColor, mixStrength);
    }
    
    /**
     * Animate smooth color transition
     */
    private static void animateColorTransition(View view, int fromColor, int toColor, long duration) {
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimator.setDuration(duration);
        colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        colorAnimator.addUpdateListener(animation -> {
            int animatedColor = (int) animation.getAnimatedValue();
            view.setBackgroundTintList(android.content.res.ColorStateList.valueOf(animatedColor));
        });
        
        colorAnimator.start();
        
        Log.d(TAG, "🎬 Started color transition animation (" + duration + "ms)");
    }
    
    /**
     * Create preset tinting configurations for chess UI elements
     */
    public static class ChessTintingPresets {
        
        // Header panel: Prominent, reflects game state clearly
        public static void applyHeaderTinting(View view, float gameProgress, float evaluation) {
            applyEnhancedGameStateTinting(view, gameProgress, evaluation * 0.6f, 800);
        }
        
        // Control panel: Subtle, doesn't distract from gameplay
        public static void applyControlTinting(View view, float gameProgress, float evaluation) {
            applyEnhancedGameStateTinting(view, gameProgress, evaluation * 0.3f, 600);
        }
        
        // Battle progress: Moderate intensity, shows progression clearly
        public static void applyProgressTinting(View view, float gameProgress, float evaluation) {
            applyEnhancedGameStateTinting(view, gameProgress, evaluation * 0.4f, 1000);
        }
        
        // Board highlights: Dynamic, responds to current position
        public static void applyBoardTinting(View view, float gameProgress, float evaluation) {
            applyEnhancedGameStateTinting(view, gameProgress, evaluation * 0.8f, 400);
        }
    }
    
    /**
     * Utility method to interpolate between two colors
     */
    private static int interpolateColor(int colorA, int colorB, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));
        
        int aA = Color.alpha(colorA);
        int aR = Color.red(colorA);
        int aG = Color.green(colorA);
        int aB = Color.blue(colorA);
        
        int bA = Color.alpha(colorB);
        int bR = Color.red(colorB);
        int bG = Color.green(colorB);
        int bB = Color.blue(colorB);
        
        int resultA = (int) (aA + factor * (bA - aA));
        int resultR = (int) (aR + factor * (bR - aR));
        int resultG = (int) (aG + factor * (bG - aG));
        int resultB = (int) (aB + factor * (bB - aB));
        
        return Color.argb(resultA, resultR, resultG, resultB);
    }
    
    /**
     * Darken a color by a specified factor
     */
    private static int darkenColor(int color, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));
        
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = Color.alpha(color);
        
        r = (int) (r * (1.0f - factor));
        g = (int) (g * (1.0f - factor));
        b = (int) (b * (1.0f - factor));
        
        return Color.argb(a, r, g, b);
    }
    
    /**
     * Lighten a color by a specified factor
     */
    private static int lightenColor(int color, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));
        
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = Color.alpha(color);
        
        r = (int) (r + (255 - r) * factor);
        g = (int) (g + (255 - g) * factor);
        b = (int) (b + (255 - b) * factor);
        
        return Color.argb(a, r, g, b);
    }
}