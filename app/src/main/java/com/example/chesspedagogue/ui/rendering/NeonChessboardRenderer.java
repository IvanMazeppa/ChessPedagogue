package com.example.chesspedagogue.ui.rendering;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * Modular renderer for neon-glowing chessboard effects.
 * Implements Feature #1: Neon-Glowing Chessboard Grid and Tiles
 * 
 * This class handles the Tron-like aesthetic with electric blue/neon green squares
 * using BlurMaskFilter for glow effects.
 */
public class NeonChessboardRenderer {
    
    private static final String TAG = "NeonChessboardRenderer";
    
    // 🌈 Neon Color Configuration
    public static class NeonColors {
        public static final int ELECTRIC_BLUE = 0xFF00FFFF;     // Cyan neon
        public static final int NEON_GREEN = 0xFF00FF00;        // Bright green
        public static final int LIGHT_SQUARE_GLOW = ELECTRIC_BLUE;
        public static final int DARK_SQUARE_GLOW = NEON_GREEN;
        public static final int GRID_LINE_COLOR = 0xFF66FFFF;   // Softer cyan for grid
        
        // Glow intensity levels
        public static final float GLOW_RADIUS_SMALL = 8f;
        public static final float GLOW_RADIUS_MEDIUM = 12f;
        public static final float GLOW_RADIUS_LARGE = 16f;
    }
    
    // Paint objects for different glow effects
    private Paint lightSquareGlowPaint;
    private Paint darkSquareGlowPaint;
    private Paint lightSquareFillPaint;
    private Paint darkSquareFillPaint;
    private Paint gridLinePaint;
    
    // Configuration
    private boolean neonModeEnabled = false;
    private float glowIntensity = 1.0f;
    private boolean pulsingEnabled = false;
    private long animationStartTime = 0;
    
    // Reusable objects for performance
    private final RectF reusableGlowRect = new RectF();
    private final RectF reusableSquareRect = new RectF();
    
    public NeonChessboardRenderer() {
        initializePaints();
        Log.d(TAG, "🎨 NeonChessboardRenderer initialized");
    }
    
    private void initializePaints() {
        // Light square glow effect
        lightSquareGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightSquareGlowPaint.setColor(NeonColors.LIGHT_SQUARE_GLOW);
        lightSquareGlowPaint.setStyle(Paint.Style.FILL);
        lightSquareGlowPaint.setMaskFilter(new BlurMaskFilter(
            NeonColors.GLOW_RADIUS_MEDIUM, BlurMaskFilter.Blur.OUTER));
        
        // Dark square glow effect  
        darkSquareGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        darkSquareGlowPaint.setColor(NeonColors.DARK_SQUARE_GLOW);
        darkSquareGlowPaint.setStyle(Paint.Style.FILL);
        darkSquareGlowPaint.setMaskFilter(new BlurMaskFilter(
            NeonColors.GLOW_RADIUS_MEDIUM, BlurMaskFilter.Blur.OUTER));
        
        // Light square solid fill (drawn on top of glow)
        lightSquareFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightSquareFillPaint.setColor(Color.argb(180, 0, 255, 255)); // Semi-transparent cyan
        lightSquareFillPaint.setStyle(Paint.Style.FILL);
        
        // Dark square solid fill
        darkSquareFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        darkSquareFillPaint.setColor(Color.argb(180, 0, 200, 0)); // Semi-transparent green
        darkSquareFillPaint.setStyle(Paint.Style.FILL);
        
        // Grid lines (circuit traces)
        gridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridLinePaint.setColor(NeonColors.GRID_LINE_COLOR);
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(2f);
        gridLinePaint.setMaskFilter(new BlurMaskFilter(
            NeonColors.GLOW_RADIUS_SMALL, BlurMaskFilter.Blur.OUTER));
    }
    
    /**
     * Renders a single neon square with glow effect.
     * Uses two-step drawing: glow layer first, then solid square on top.
     */
    public void drawNeonSquare(Canvas canvas, float left, float top, float right, float bottom, 
                              boolean isLightSquare) {
        if (!neonModeEnabled) {
            return;
        }
        
        // Calculate current glow intensity (with optional pulsing)
        float currentIntensity = calculateCurrentGlowIntensity();
        
        // Step 1: Draw glow layer (slightly larger than the square)
        float glowExpansion = NeonColors.GLOW_RADIUS_MEDIUM * currentIntensity;
        reusableGlowRect.set(
            left - glowExpansion, 
            top - glowExpansion, 
            right + glowExpansion, 
            bottom + glowExpansion
        );

        Paint glowPaint = isLightSquare ? lightSquareGlowPaint : darkSquareGlowPaint;
        
        // Adjust glow opacity based on intensity
        int glowAlpha = (int) (255 * currentIntensity * 0.6f); // Max 60% opacity for glow
        glowPaint.setAlpha(glowAlpha);
        
        canvas.drawRect(reusableGlowRect, glowPaint);
        
        // Step 2: Draw solid square on top (crisp neon outline)
        reusableSquareRect.set(left, top, right, bottom);
        
        Paint fillPaint = isLightSquare ? lightSquareFillPaint : darkSquareFillPaint;
        int fillAlpha = (int) (255 * currentIntensity * 0.7f); // Max 70% opacity for fill
        fillPaint.setAlpha(fillAlpha);
        
        canvas.drawRect(reusableSquareRect, fillPaint);
    }
    
    /**
     * Draws glowing grid lines between squares (circuit trace effect).
     */
    public void drawNeonGridLines(Canvas canvas, int boardSize, float squareSize, 
                                 float boardLeft, float boardTop) {
        if (!neonModeEnabled) {
            return;
        }
        
        float currentIntensity = calculateCurrentGlowIntensity();
        int gridAlpha = (int) (255 * currentIntensity * 0.8f);
        gridLinePaint.setAlpha(gridAlpha);
        
        // Draw vertical grid lines
        for (int i = 0; i <= 8; i++) {
            float x = boardLeft + i * squareSize;
            canvas.drawLine(x, boardTop, x, boardTop + 8 * squareSize, gridLinePaint);
        }
        
        // Draw horizontal grid lines
        for (int i = 0; i <= 8; i++) {
            float y = boardTop + i * squareSize;
            canvas.drawLine(boardLeft, y, boardLeft + 8 * squareSize, y, gridLinePaint);
        }
    }
    
    /**
     * Calculates current glow intensity, including pulsing effect if enabled.
     */
    private float calculateCurrentGlowIntensity() {
        float baseIntensity = glowIntensity;
        
        if (pulsingEnabled && animationStartTime > 0) {
            long elapsed = System.currentTimeMillis() - animationStartTime;
            float pulsePhase = (float) Math.sin(elapsed * 0.003); // ~3 second pulse cycle
            float pulseMultiplier = 0.8f + 0.2f * pulsePhase; // Pulse between 80%-100%
            baseIntensity *= pulseMultiplier;
        }
        
        return Math.max(0f, Math.min(1f, baseIntensity));
    }
    
    /**
     * Enhanced square rendering for special effects (piece movement, highlights, etc.)
     */
    public void drawEnhancedSquare(Canvas canvas, float left, float top, float right, float bottom,
                                  boolean isLightSquare, SquareEffectType effectType) {
        if (!neonModeEnabled) {
            return;
        }
        
        // Adjust glow based on effect type
        float intensityMultiplier = 1.0f;
        float radiusMultiplier = 1.0f;
        
        switch (effectType) {
            case NORMAL:
                // Standard rendering
                break;
            case HIGHLIGHTED:
                intensityMultiplier = 1.5f;
                radiusMultiplier = 1.3f;
                break;
            case LAST_MOVE:
                intensityMultiplier = 1.2f;
                radiusMultiplier = 1.1f;
                break;
            case POWER_UP:
                intensityMultiplier = 2.0f;
                radiusMultiplier = 1.8f;
                break;
        }
        
        // Temporarily modify paint properties
        Paint glowPaint = isLightSquare ? lightSquareGlowPaint : darkSquareGlowPaint;
        BlurMaskFilter originalFilter = (BlurMaskFilter) glowPaint.getMaskFilter();
        
        if (intensityMultiplier != 1.0f || radiusMultiplier != 1.0f) {
            float enhancedRadius = NeonColors.GLOW_RADIUS_MEDIUM * radiusMultiplier;
            glowPaint.setMaskFilter(new BlurMaskFilter(enhancedRadius, BlurMaskFilter.Blur.OUTER));
        }
        
        // Draw enhanced square
        drawNeonSquare(canvas, left, top, right, bottom, isLightSquare);
        
        // Restore original filter
        glowPaint.setMaskFilter(originalFilter);
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // Public Configuration Methods
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    public void setNeonModeEnabled(boolean enabled) {
        this.neonModeEnabled = enabled;
        Log.d(TAG, "🎨 Neon mode " + (enabled ? "enabled" : "disabled"));
    }
    
    public boolean isNeonModeEnabled() {
        return neonModeEnabled;
    }
    
    public void setGlowIntensity(float intensity) {
        this.glowIntensity = Math.max(0f, Math.min(1f, intensity));
        Log.d(TAG, "🎨 Glow intensity set to " + this.glowIntensity);
    }
    
    public void setPulsingEnabled(boolean enabled) {
        this.pulsingEnabled = enabled;
        if (enabled && animationStartTime == 0) {
            animationStartTime = System.currentTimeMillis();
        } else if (!enabled) {
            animationStartTime = 0;
        }
        Log.d(TAG, "🎨 Pulsing " + (enabled ? "enabled" : "disabled"));
    }
    
    public void updateNeonColors(int lightSquareColor, int darkSquareColor, int gridColor) {
        lightSquareGlowPaint.setColor(lightSquareColor);
        lightSquareFillPaint.setColor(Color.argb(180, 
            Color.red(lightSquareColor), 
            Color.green(lightSquareColor), 
            Color.blue(lightSquareColor)));
            
        darkSquareGlowPaint.setColor(darkSquareColor);
        darkSquareFillPaint.setColor(Color.argb(180,
            Color.red(darkSquareColor), 
            Color.green(darkSquareColor), 
            Color.blue(darkSquareColor)));
            
        gridLinePaint.setColor(gridColor);
        Log.d(TAG, "🎨 Neon colors updated");
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // Square Effect Types
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    public enum SquareEffectType {
        NORMAL,      // Standard neon glow
        HIGHLIGHTED, // Enhanced glow for selected/possible moves
        LAST_MOVE,   // Moderate enhancement for move history
        POWER_UP     // Maximum glow for special events
    }
}