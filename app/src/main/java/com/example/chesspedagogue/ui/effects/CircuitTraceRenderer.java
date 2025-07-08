package com.example.chesspedagogue.ui.effects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * CircuitTraceRenderer - Modular circuit board move highlighting system
 * 
 * Features:
 * - Circuit board style pathways for move highlights
 * - Right-angle traces connecting pieces to legal moves
 * - Glowing nodes at destination squares (PCB pad style)
 * - Animated light flow along circuit paths
 * - Last-move circuit traces showing "power flow"
 * - Works across any ChessBoardView implementation
 * 
 * Usage:
 * CircuitTraceRenderer traceRenderer = new CircuitTraceRenderer();
 * traceRenderer.showLegalMoves(pieceRow, pieceCol, legalMoves, boardView);
 */
public class CircuitTraceRenderer {
    
    private static final String TAG = "CircuitTraceRenderer";
    
    // Animation constants
    private static final int FLOW_ANIMATION_DURATION_MS = 1500;
    private static final int FLOW_ANIMATION_FRAMES = 30;
    private static final int FLOW_ANIMATION_FRAME_DELAY_MS = 50;
    
    // Visual constants
    private static final float TRACE_STROKE_WIDTH = 3f;
    private static final float TRACE_GLOW_RADIUS = 4f;
    private static final float NODE_RADIUS = 8f;
    private static final float NODE_GLOW_RADIUS = 6f;
    private static final float CORNER_RADIUS = 4f;
    
    // Default colors
    private static final int DEFAULT_TRACE_COLOR = 0xFF00FFFF; // Cyan
    private static final int DEFAULT_GLOW_COLOR = 0x4000FFFF; // Semi-transparent cyan
    private static final int DEFAULT_NODE_COLOR = 0xFF00FF00; // Green
    private static final int DEFAULT_FLOW_COLOR = 0xFFFFFFFF; // White
    
    // Circuit configuration
    private int traceColor = DEFAULT_TRACE_COLOR;
    private int glowColor = DEFAULT_GLOW_COLOR;
    private int nodeColor = DEFAULT_NODE_COLOR;
    private int flowColor = DEFAULT_FLOW_COLOR;
    private float strokeWidth = TRACE_STROKE_WIDTH;
    private float glowRadius = TRACE_GLOW_RADIUS;
    private boolean glowEnabled = true;
    private boolean flowAnimationEnabled = true;
    
    // Active circuit state
    private List<CircuitTrace> activeTraces = new ArrayList<>();
    private List<CircuitNode> activeNodes = new ArrayList<>();
    private CircuitTrace lastMoveTrace = null;
    private Handler animationHandler;
    private CircuitCallback callback;
    private float currentFlowPosition = 0f;
    private boolean flowAnimationActive = false;
    
    public interface CircuitCallback {
        void onInvalidate(); // Request view redraw
        void onAnimationComplete(); // Animation finished
    }
    
    public CircuitTraceRenderer() {
        animationHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Circuit trace - represents a path from piece to legal move square
     */
    private static class CircuitTrace {
        Path path;
        float length;
        int fromRow, fromCol, toRow, toCol;
        boolean isLinear; // true for rook/bishop/queen, false for knight/king
        
        CircuitTrace(Path path, float length, int fromRow, int fromCol, int toRow, int toCol, boolean isLinear) {
            this.path = path;
            this.length = length;
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.isLinear = isLinear;
        }
    }
    
    /**
     * Circuit node - represents a PCB-style pad at legal move destinations
     */
    private static class CircuitNode {
        float x, y;
        int row, col;
        boolean isCapture; // true if this move captures a piece
        
        CircuitNode(float x, float y, int row, int col, boolean isCapture) {
            this.x = x;
            this.y = y;
            this.row = row;
            this.col = col;
            this.isCapture = isCapture;
        }
    }
    
    /**
     * Show legal moves with circuit board traces
     * @param pieceRow Row of selected piece
     * @param pieceCol Column of selected piece  
     * @param legalMoves List of legal move coordinates [row, col]
     * @param squareSize Size of each board square in pixels
     * @param flipped Whether board is flipped
     * @param callback Interface for view updates
     */
    public void showLegalMoves(int pieceRow, int pieceCol, List<int[]> legalMoves, 
                              float squareSize, boolean flipped, CircuitCallback callback) {
        
        this.callback = callback;
        
        Log.d(TAG, "🔌 Creating circuit traces from (" + pieceRow + "," + pieceCol + 
              ") to " + legalMoves.size() + " legal moves");
        
        // Clear existing traces
        clearCircuits();
        
        // Convert piece position to pixel coordinates
        float pieceX = (flipped ? 7 - pieceCol : pieceCol) * squareSize + squareSize / 2f;
        float pieceY = (flipped ? 7 - pieceRow : pieceRow) * squareSize + squareSize / 2f;
        
        // Create circuit traces for each legal move
        for (int[] move : legalMoves) {
            int toRow = move[0];
            int toCol = move[1];
            
            // Convert destination to pixel coordinates
            float toX = (flipped ? 7 - toCol : toCol) * squareSize + squareSize / 2f;
            float toY = (flipped ? 7 - toRow : toRow) * squareSize + squareSize / 2f;
            
            // Create circuit trace path
            CircuitTrace trace = createCircuitTrace(pieceX, pieceY, toX, toY, 
                                                   pieceRow, pieceCol, toRow, toCol);
            activeTraces.add(trace);
            
            // Create circuit node at destination
            // TODO: Detect if this is a capture move by checking board state
            boolean isCapture = false; // Would need board state to determine
            CircuitNode node = new CircuitNode(toX, toY, toRow, toCol, isCapture);
            activeNodes.add(node);
        }
        
        // Start flow animation if enabled
        if (flowAnimationEnabled && !activeTraces.isEmpty()) {
            startFlowAnimation();
        }
        
        // Request initial draw
        if (callback != null) {
            callback.onInvalidate();
        }
    }
    
    /**
     * Create circuit trace path with right-angle connections
     */
    private CircuitTrace createCircuitTrace(float fromX, float fromY, float toX, float toY,
                                           int fromRow, int fromCol, int toRow, int toCol) {
        
        Path path = new Path();
        boolean isLinear = isLinearMove(fromRow, fromCol, toRow, toCol);
        
        if (isLinear) {
            // Straight line for linear moves (rook, bishop, queen)
            createLinearTrace(path, fromX, fromY, toX, toY);
        } else {
            // Right-angle path for non-linear moves (knight, king)
            createRightAngleTrace(path, fromX, fromY, toX, toY);
        }
        
        // Calculate path length for animation
        PathMeasure measure = new PathMeasure(path, false);
        float length = measure.getLength();
        
        Log.d(TAG, "🔌 Created " + (isLinear ? "linear" : "right-angle") + 
              " trace: " + length + "px");
        
        return new CircuitTrace(path, length, fromRow, fromCol, toRow, toCol, isLinear);
    }
    
    /**
     * Create linear trace with subtle circuit styling
     */
    private void createLinearTrace(Path path, float fromX, float fromY, float toX, float toY) {
        path.moveTo(fromX, fromY);
        
        // Add slight stepping for circuit board look
        float dx = toX - fromX;
        float dy = toY - fromY;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 40) {
            // Add intermediate steps for longer paths
            int steps = (int)(distance / 20);
            for (int i = 1; i < steps; i++) {
                float t = i / (float)steps;
                float x = fromX + dx * t;
                float y = fromY + dy * t;
                
                // Add slight offset for circuit trace effect
                if (i % 2 == 0) {
                    x += 2f;
                } else {
                    x -= 2f;
                }
                
                path.lineTo(x, y);
            }
        }
        
        path.lineTo(toX, toY);
    }
    
    /**
     * Create right-angle trace like circuit board routing
     */
    private void createRightAngleTrace(Path path, float fromX, float fromY, float toX, float toY) {
        path.moveTo(fromX, fromY);
        
        float dx = toX - fromX;
        float dy = toY - fromY;
        
        // Choose routing direction based on distance
        if (Math.abs(dx) > Math.abs(dy)) {
            // Route horizontally first, then vertically
            float midX = fromX + dx * 0.7f;
            path.lineTo(midX, fromY);
            addCornerRadius(path, midX, fromY, midX, toY, CORNER_RADIUS);
            path.lineTo(midX, toY);
            addCornerRadius(path, midX, toY, toX, toY, CORNER_RADIUS);
            path.lineTo(toX, toY);
        } else {
            // Route vertically first, then horizontally
            float midY = fromY + dy * 0.7f;
            path.lineTo(fromX, midY);
            addCornerRadius(path, fromX, midY, toX, midY, CORNER_RADIUS);
            path.lineTo(toX, midY);
            addCornerRadius(path, toX, midY, toX, toY, CORNER_RADIUS);
            path.lineTo(toX, toY);
        }
    }
    
    /**
     * Add rounded corner to path for professional PCB look
     */
    private void addCornerRadius(Path path, float x1, float y1, float x2, float y2, float radius) {
        // Simplified corner - in production could use proper arc calculation
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        
        if (length > radius * 2) {
            float unitX = dx / length;
            float unitY = dy / length;
            
            path.lineTo(x1 + unitX * radius, y1 + unitY * radius);
            path.lineTo(x2 - unitX * radius, y2 - unitY * radius);
        }
    }
    
    /**
     * Determine if move is linear (rook, bishop, queen style)
     */
    private boolean isLinearMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // Linear if moving in straight line (horizontal, vertical, diagonal)
        return (rowDiff == 0 || colDiff == 0 || rowDiff == colDiff);
    }
    
    /**
     * Show last move trace with persistent circuit
     */
    public void showLastMoveTrace(int fromRow, int fromCol, int toRow, int toCol, 
                                 float squareSize, boolean flipped) {
        
        // Convert to pixel coordinates
        float fromX = (flipped ? 7 - fromCol : fromCol) * squareSize + squareSize / 2f;
        float fromY = (flipped ? 7 - fromRow : fromRow) * squareSize + squareSize / 2f;
        float toX = (flipped ? 7 - toCol : toCol) * squareSize + squareSize / 2f;
        float toY = (flipped ? 7 - toRow : toRow) * squareSize + squareSize / 2f;
        
        // Create persistent trace for last move
        lastMoveTrace = createCircuitTrace(fromX, fromY, toX, toY, fromRow, fromCol, toRow, toCol);
        
        Log.d(TAG, "🔌 Created last move trace from (" + fromRow + "," + fromCol + 
              ") to (" + toRow + "," + toCol + ")");
        
        if (callback != null) {
            callback.onInvalidate();
        }
    }
    
    /**
     * Draw all active circuits on canvas
     */
    public void drawCircuits(Canvas canvas) {
        if (activeTraces.isEmpty() && lastMoveTrace == null) {
            return;
        }
        
        // Create paints for different elements
        Paint tracePaint = createTracePaint();
        Paint glowPaint = createGlowPaint();
        Paint nodePaint = createNodePaint();
        Paint flowPaint = createFlowPaint();
        
        // Draw last move trace (dimmer, persistent)
        if (lastMoveTrace != null) {
            Paint dimTracePaint = createTracePaint();
            dimTracePaint.setAlpha(100); // 40% opacity
            
            if (glowEnabled) {
                Paint dimGlowPaint = createGlowPaint();
                dimGlowPaint.setAlpha(50); // 20% opacity
                canvas.drawPath(lastMoveTrace.path, dimGlowPaint);
            }
            canvas.drawPath(lastMoveTrace.path, dimTracePaint);
        }
        
        // Draw active legal move traces
        for (CircuitTrace trace : activeTraces) {
            // Draw glow layer first
            if (glowEnabled) {
                canvas.drawPath(trace.path, glowPaint);
            }
            
            // Draw main trace
            canvas.drawPath(trace.path, tracePaint);
            
            // Draw flow animation if active
            if (flowAnimationActive && flowAnimationEnabled) {
                drawFlowAnimation(canvas, trace, flowPaint);
            }
        }
        
        // Draw circuit nodes
        for (CircuitNode node : activeNodes) {
            drawCircuitNode(canvas, node, nodePaint, glowPaint);
        }
    }
    
    /**
     * Draw flowing light animation along trace
     */
    private void drawFlowAnimation(Canvas canvas, CircuitTrace trace, Paint flowPaint) {
        PathMeasure measure = new PathMeasure(trace.path, false);
        float[] pos = new float[2];
        float[] tan = new float[2];
        
        // Calculate current position along path
        float distance = (currentFlowPosition * trace.length) % trace.length;
        
        if (measure.getPosTan(distance, pos, tan)) {
            // Draw flowing dot
            canvas.drawCircle(pos[0], pos[1], 3f, flowPaint);
            
            // Draw trailing dots for comet effect
            for (int i = 1; i <= 3; i++) {
                float trailDistance = distance - (i * 10f);
                if (trailDistance < 0) trailDistance += trace.length;
                
                if (measure.getPosTan(trailDistance, pos, tan)) {
                    Paint trailPaint = new Paint(flowPaint);
                    trailPaint.setAlpha(255 / (i + 1)); // Fade out
                    canvas.drawCircle(pos[0], pos[1], 3f - i, trailPaint);
                }
            }
        }
    }
    
    /**
     * Draw circuit node (PCB pad style)
     */
    private void drawCircuitNode(Canvas canvas, CircuitNode node, Paint nodePaint, Paint glowPaint) {
        // Draw glow if enabled
        if (glowEnabled) {
            Paint nodeGlowPaint = new Paint(glowPaint);
            nodeGlowPaint.setMaskFilter(new BlurMaskFilter(NODE_GLOW_RADIUS, BlurMaskFilter.Blur.OUTER));
            canvas.drawCircle(node.x, node.y, NODE_RADIUS + 2, nodeGlowPaint);
        }
        
        // Draw main node
        canvas.drawCircle(node.x, node.y, NODE_RADIUS, nodePaint);
        
        // Draw inner detail for PCB look
        Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setColor(Color.BLACK);
        innerPaint.setStyle(Paint.Style.STROKE);
        innerPaint.setStrokeWidth(1f);
        canvas.drawCircle(node.x, node.y, NODE_RADIUS - 2, innerPaint);
        
        // Special styling for capture nodes
        if (node.isCapture) {
            Paint capturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            capturePaint.setColor(Color.RED);
            capturePaint.setStyle(Paint.Style.STROKE);
            capturePaint.setStrokeWidth(2f);
            canvas.drawCircle(node.x, node.y, NODE_RADIUS + 1, capturePaint);
        }
    }
    
    // Paint creation methods
    private Paint createTracePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(traceColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }
    
    private Paint createGlowPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(glowColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth * 2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));
        return paint;
    }
    
    private Paint createNodePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(nodeColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }
    
    private Paint createFlowPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(flowColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }
    
    /**
     * Start flow animation along traces
     */
    private void startFlowAnimation() {
        if (flowAnimationActive) return;
        
        flowAnimationActive = true;
        currentFlowPosition = 0f;
        
        Runnable animationRunnable = new Runnable() {
            private int frame = 0;
            
            @Override
            public void run() {
                if (!flowAnimationActive || frame >= FLOW_ANIMATION_FRAMES) {
                    flowAnimationActive = false;
                    if (callback != null) {
                        callback.onAnimationComplete();
                    }
                    return;
                }
                
                currentFlowPosition = frame / (float)FLOW_ANIMATION_FRAMES;
                frame++;
                
                if (callback != null) {
                    callback.onInvalidate();
                }
                
                animationHandler.postDelayed(this, FLOW_ANIMATION_FRAME_DELAY_MS);
            }
        };
        
        animationHandler.post(animationRunnable);
        Log.d(TAG, "🔌 Started circuit flow animation");
    }
    
    /**
     * Clear all circuits and stop animations
     */
    public void clearCircuits() {
        activeTraces.clear();
        activeNodes.clear();
        flowAnimationActive = false;
        animationHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "🔌 Cleared all circuits");
    }
    
    /**
     * Clear last move trace
     */
    public void clearLastMoveTrace() {
        lastMoveTrace = null;
    }
    
    /**
     * Check if circuits are active
     */
    public boolean hasActiveCircuits() {
        return !activeTraces.isEmpty() || !activeNodes.isEmpty() || lastMoveTrace != null;
    }
    
    // Configuration methods
    public void setTraceColor(int color) { this.traceColor = color; }
    public void setGlowColor(int color) { this.glowColor = color; }
    public void setNodeColor(int color) { this.nodeColor = color; }
    public void setFlowColor(int color) { this.flowColor = color; }
    public void setStrokeWidth(float width) { this.strokeWidth = width; }
    public void setGlowRadius(float radius) { this.glowRadius = radius; }
    public void setGlowEnabled(boolean enabled) { this.glowEnabled = enabled; }
    public void setFlowAnimationEnabled(boolean enabled) { this.flowAnimationEnabled = enabled; }
    
    /**
     * Apply cyberpunk circuit theme (cyan traces, green nodes)
     */
    public void applyCyberpunkCircuitTheme() {
        setTraceColor(0xFF00FFFF); // Cyan
        setGlowColor(0x4000FFFF); // Semi-transparent cyan
        setNodeColor(0xFF00FF00); // Green
        setFlowColor(0xFFFFFFFF); // White
        setGlowEnabled(true);
        setFlowAnimationEnabled(true);
        Log.d(TAG, "🎨 Applied cyberpunk circuit theme");
    }
    
    /**
     * Apply matrix circuit theme (green traces and nodes)
     */
    public void applyMatrixCircuitTheme() {
        setTraceColor(0xFF00FF00); // Green
        setGlowColor(0x4000FF00); // Semi-transparent green
        setNodeColor(0xFF00AA00); // Dark green
        setFlowColor(0xFF88FF88); // Light green
        setGlowEnabled(true);
        setFlowAnimationEnabled(true);
        Log.d(TAG, "🎨 Applied matrix circuit theme");
    }
    
    /**
     * Apply neon circuit theme (orange traces, blue nodes)
     */
    public void applyNeonCircuitTheme() {
        setTraceColor(0xFFFF6600); // Neon orange
        setGlowColor(0x40FF6600); // Semi-transparent orange
        setNodeColor(0xFF0066FF); // Blue
        setFlowColor(0xFFFFFFFF); // White
        setGlowEnabled(true);
        setFlowAnimationEnabled(true);
        Log.d(TAG, "🎨 Applied neon circuit theme");
    }
}