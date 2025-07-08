package com.example.chesspedagogue.ui.effects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ElectricArcRenderer - Modular electric arc effects for chess move trails
 * 
 * Features:
 * - Lightning-like bolts for piece movement
 * - L-shaped arcs for knight moves
 * - Customizable colors and intensity
 * - Auto-cleanup after brief display
 * - Works across any ChessBoardView implementation
 * 
 * Usage:
 * ElectricArcRenderer arcRenderer = new ElectricArcRenderer();
 * arcRenderer.createMoveTrail(fromX, fromY, toX, toY, isKnightMove, boardView);
 */
public class ElectricArcRenderer {
    
    private static final String TAG = "ElectricArcRenderer";
    
    // Animation constants
    private static final int ARC_DISPLAY_DURATION_MS = 200;
    private static final int ARC_FADE_STEPS = 3;
    private static final int ARC_FADE_STEP_DURATION_MS = 67; // 200ms / 3 steps
    
    // Visual constants
    private static final float ARC_STROKE_WIDTH = 4f;
    private static final float ARC_GLOW_RADIUS = 6f;
    private static final int ARC_REFINEMENT_ITERATIONS = 3;
    private static final int ARC_JITTER_RANGE = 15;
    
    // Default colors
    private static final int DEFAULT_ARC_COLOR = Color.CYAN;
    private static final int DEFAULT_GLOW_COLOR = 0x80FFFFFF; // Semi-transparent white
    
    // Arc configuration
    private int arcColor = DEFAULT_ARC_COLOR;
    private int glowColor = DEFAULT_GLOW_COLOR;
    private float strokeWidth = ARC_STROKE_WIDTH;
    private float glowRadius = ARC_GLOW_RADIUS;
    private boolean glowEnabled = true;
    
    // Active arc state
    private List<Point> currentArc = null;
    private float currentAlpha = 1.0f;
    private Handler fadeHandler;
    private ArcCallback callback;
    private Random random = new Random();
    
    public interface ArcCallback {
        void onInvalidate(); // Request view redraw
        void onArcComplete(); // Arc animation finished
    }
    
    public ElectricArcRenderer() {
        fadeHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Create electric arc trail for piece movement
     * @param fromX Starting X coordinate (in pixels)
     * @param fromY Starting Y coordinate (in pixels) 
     * @param toX Ending X coordinate (in pixels)
     * @param toY Ending Y coordinate (in pixels)
     * @param isKnightMove True for L-shaped knight moves
     * @param callback Interface for view updates
     */
    public void createMoveTrail(float fromX, float fromY, float toX, float toY, 
                               boolean isKnightMove, ArcCallback callback) {
        
        this.callback = callback;
        
        Log.d(TAG, "⚡ Creating electric arc from (" + fromX + "," + fromY + 
              ") to (" + toX + "," + toY + ") knight=" + isKnightMove);
        
        // Clear any existing arc
        clearCurrentArc();
        
        // Generate arc path
        if (isKnightMove) {
            currentArc = generateKnightArc(fromX, fromY, toX, toY);
        } else {
            currentArc = generateStraightArc(fromX, fromY, toX, toY);
        }
        
        currentAlpha = 1.0f;
        
        // Start fade sequence
        startFadeSequence();
        
        // Request initial draw
        if (callback != null) {
            callback.onInvalidate();
        }
    }
    
    /**
     * Generate L-shaped arc for knight moves
     */
    private List<Point> generateKnightArc(float fromX, float fromY, float toX, float toY) {
        List<Point> arc = new ArrayList<>();
        
        // Knight moves in L-shape: horizontal then vertical, or vertical then horizontal
        // Choose path based on which direction is longer
        float deltaX = toX - fromX;
        float deltaY = toY - fromY;
        
        Point start = new Point((int)fromX, (int)fromY);
        Point end = new Point((int)toX, (int)toY);
        Point corner;
        
        // Determine L-shape direction (prioritize longer movement)
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Horizontal first, then vertical
            corner = new Point((int)toX, (int)fromY);
        } else {
            // Vertical first, then horizontal  
            corner = new Point((int)fromX, (int)toY);
        }
        
        // Generate first segment (start to corner)
        List<Point> segment1 = generateJaggedLine(start, corner);
        
        // Generate second segment (corner to end)
        List<Point> segment2 = generateJaggedLine(corner, end);
        
        // Combine segments (remove duplicate corner point)
        arc.addAll(segment1);
        if (segment2.size() > 1) {
            arc.addAll(segment2.subList(1, segment2.size()));
        }
        
        Log.d(TAG, "🎯 Generated knight arc with " + arc.size() + " points via corner " + corner);
        return arc;
    }
    
    /**
     * Generate straight jagged arc for linear moves
     */
    private List<Point> generateStraightArc(float fromX, float fromY, float toX, float toY) {
        Point start = new Point((int)fromX, (int)fromY);
        Point end = new Point((int)toX, (int)toY);
        
        List<Point> arc = generateJaggedLine(start, end);
        
        Log.d(TAG, "⚡ Generated straight arc with " + arc.size() + " points");
        return arc;
    }
    
    /**
     * Generate jagged lightning line between two points
     */
    private List<Point> generateJaggedLine(Point start, Point end) {
        List<Point> line = new ArrayList<>();
        line.add(new Point(start.x, start.y));
        line.add(new Point(end.x, end.y));
        
        // Recursive subdivision to create jagged effect
        for (int iteration = 0; iteration < ARC_REFINEMENT_ITERATIONS; iteration++) {
            List<Point> refinedLine = new ArrayList<>();
            
            for (int i = 0; i < line.size() - 1; i++) {
                Point p1 = line.get(i);
                Point p2 = line.get(i + 1);
                
                // Add start point
                refinedLine.add(new Point(p1.x, p1.y));
                
                // Calculate midpoint with jitter
                float midX = (p1.x + p2.x) / 2f;
                float midY = (p1.y + p2.y) / 2f;
                
                // Add random displacement perpendicular to line direction
                float dx = p2.x - p1.x;
                float dy = p2.y - p1.y;
                float length = (float)Math.sqrt(dx * dx + dy * dy);
                
                if (length > 0) {
                    // Perpendicular vector
                    float perpX = -dy / length;
                    float perpY = dx / length;
                    
                    // Random jitter amount (decreases with iterations)
                    float jitterAmount = (ARC_JITTER_RANGE / (float)(iteration + 1)) * 
                                       (random.nextFloat() * 2 - 1);
                    
                    midX += perpX * jitterAmount;
                    midY += perpY * jitterAmount;
                }
                
                // Add jittered midpoint
                refinedLine.add(new Point((int)midX, (int)midY));
            }
            
            // Add final point
            refinedLine.add(new Point(line.get(line.size() - 1).x, line.get(line.size() - 1).y));
            line = refinedLine;
        }
        
        return line;
    }
    
    /**
     * Draw the current arc on canvas
     */
    public void drawArc(Canvas canvas) {
        if (currentArc == null || currentArc.size() < 2) {
            return;
        }
        
        // Create paints for glow and main arc
        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        // Configure glow layer
        if (glowEnabled) {
            glowPaint.setColor(glowColor);
            glowPaint.setStrokeWidth(strokeWidth * 2);
            glowPaint.setStyle(Paint.Style.STROKE);
            glowPaint.setAlpha((int)(currentAlpha * 127)); // 50% of current alpha
            glowPaint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));
        }
        
        // Configure main arc
        arcPaint.setColor(arcColor);
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setAlpha((int)(currentAlpha * 255));
        
        // Draw glow layer first (if enabled)
        if (glowEnabled) {
            drawArcPath(canvas, glowPaint);
        }
        
        // Draw main arc
        drawArcPath(canvas, arcPaint);
    }
    
    private void drawArcPath(Canvas canvas, Paint paint) {
        for (int i = 0; i < currentArc.size() - 1; i++) {
            Point p1 = currentArc.get(i);
            Point p2 = currentArc.get(i + 1);
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }
    }
    
    /**
     * Start fade-out sequence
     */
    private void startFadeSequence() {
        fadeHandler.postDelayed(new Runnable() {
            private int step = 0;
            
            @Override
            public void run() {
                step++;
                currentAlpha = 1.0f - (step / (float)ARC_FADE_STEPS);
                
                if (currentAlpha <= 0) {
                    // Animation complete
                    clearCurrentArc();
                    if (callback != null) {
                        callback.onArcComplete();
                    }
                    Log.d(TAG, "✅ Electric arc animation complete");
                } else {
                    // Continue fading
                    if (callback != null) {
                        callback.onInvalidate();
                    }
                    fadeHandler.postDelayed(this, ARC_FADE_STEP_DURATION_MS);
                }
            }
        }, ARC_FADE_STEP_DURATION_MS);
    }
    
    /**
     * Clear current arc and stop any animations
     */
    public void clearCurrentArc() {
        currentArc = null;
        currentAlpha = 0f;
        fadeHandler.removeCallbacksAndMessages(null);
    }
    
    /**
     * Check if arc is currently active
     */
    public boolean isArcActive() {
        return currentArc != null && currentAlpha > 0;
    }
    
    // Configuration methods
    public void setArcColor(int color) {
        this.arcColor = color;
    }
    
    public void setGlowColor(int color) {
        this.glowColor = color;
    }
    
    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }
    
    public void setGlowRadius(float radius) {
        this.glowRadius = radius;
    }
    
    public void setGlowEnabled(boolean enabled) {
        this.glowEnabled = enabled;
    }
    
    /**
     * Configure electric blue theme
     */
    public void applyElectricBlueTheme() {
        setArcColor(Color.CYAN);
        setGlowColor(0x4000FFFF); // Semi-transparent cyan
        setGlowEnabled(true);
        Log.d(TAG, "🎨 Applied electric blue arc theme");
    }
    
    /**
     * Configure lightning white theme  
     */
    public void applyLightningWhiteTheme() {
        setArcColor(Color.WHITE);
        setGlowColor(0x40FFFFFF); // Semi-transparent white
        setGlowEnabled(true);
        Log.d(TAG, "🎨 Applied lightning white arc theme");
    }
    
    /**
     * Configure neon orange theme (complementary to neon chessboard)
     */
    public void applyNeonOrangeTheme() {
        setArcColor(0xFFFF6600); // Neon orange
        setGlowColor(0x40FF6600); // Semi-transparent orange
        setGlowEnabled(true);
        Log.d(TAG, "🎨 Applied neon orange arc theme");
    }
}