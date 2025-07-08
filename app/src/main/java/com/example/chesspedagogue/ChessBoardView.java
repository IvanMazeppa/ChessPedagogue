package com.example.chesspedagogue;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;

import androidx.core.content.ContextCompat;

import com.example.chesspedagogue.ui.rendering.NeonChessboardRenderer;
import com.example.chesspedagogue.ui.rendering.NeonSettingsManager;
import com.example.chesspedagogue.ui.effects.ElectricArcRenderer;
import com.example.chesspedagogue.ui.effects.ElectricArcSettingsManager;
import com.example.chesspedagogue.ui.effects.CircuitTraceRenderer;
import com.example.chesspedagogue.ui.effects.CircuitTraceSettingsManager;
import com.example.chesspedagogue.ui.effects.RoboticPieceAnimator;
import com.example.chesspedagogue.ui.effects.RoboticAnimationSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Custom View that draws the chessboard and animates moves.
 */
public class ChessBoardView extends View {

    private final int[] checkKingPosition = {-1, -1}; // Position of the king in check
    private final int[] lastMoveFrom = {-1, -1}; // From coordinates
    private final int[] lastMoveTo = {-1, -1};   // To coordinates
    private final char[][] boardState = {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };
    /* ───── legal‑move highlights ───── */
    private final List<int[]> highlightSquares = new ArrayList<>();
    private final List<SquareHighlight> activeHighlights = new ArrayList<>();
    private final List<MovingPiece> movingPieces = new ArrayList<>();
    /* ───── paints / shaders ───── */
    private Paint lightPaint, darkPaint, selectedPaint, legalMovePaint;
    private Paint lastMovePaint; // For highlighting the last move
    // Add these class variables near your other declarations
    private Paint checkPaint; // For highlighting the king in check
    private boolean kingInCheck = false;
    private BitmapShader lightShader, darkShader;
    /* ───── board state ───── */
    private int squareSize;
    private boolean flipped = false;
    private int selectedRow = -1, selectedCol = -1;
    private ValueAnimator legalAnimator;
    private int legalAlpha = 0;
    private Paint highlightPaint = new Paint();
    private OnSquareTapListener squareTapListener;
    
    /* ───── Performance optimization: Pre-allocated objects ───── */
    private final RectF reusableRectF = new RectF();  // For drawing squares
    private final Rect reusableBounds = new Rect();   // For drawable bounds
    private final int[] reusableCoords = new int[2];  // For coordinate conversions
    private final PorterDuffXfermode srcOverXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
    private final Map<Character, Drawable> pieceDrawableCache = new HashMap<>();  // Cache drawables
    
    // 🎯 BOARD SCALING FIX: Track when size is properly calculated
    private boolean sizeInitialized = false;
    private int lastMeasuredWidth = -1;
    private int lastMeasuredHeight = -1;
    
    // 🌈 NEON RENDERING: Modular neon effects renderer
    private NeonChessboardRenderer neonRenderer;
    
    // ⚡ ELECTRIC ARC RENDERING: Modular electric arc move trails
    private ElectricArcRenderer electricArcRenderer;
    private ElectricArcSettingsManager electricArcSettings;
    
    // 🔌 CIRCUIT TRACE RENDERING: Modular circuit board move highlights
    private CircuitTraceRenderer circuitTraceRenderer;
    private CircuitTraceSettingsManager circuitTraceSettings;
    
    // 🤖 ROBOTIC PIECE ANIMATION: Modular mechanical movement system
    private RoboticPieceAnimator roboticAnimator;
    private RoboticAnimationSettingsManager roboticAnimationSettings;

    /* ───── ctor ───── */
    public ChessBoardView(Context c) {
        super(c);
        init();
    }

    public ChessBoardView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    /* ───────── init ───────── */
    private void init() {
        // Warm-tinted squares for better piece contrast as specified in design doc
        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setColor(0xFFF5F2E8); // Off-white with 5-10% warm teal tint
        lightPaint.setStyle(Paint.Style.FILL);

        darkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        darkPaint.setColor(0xFF5D7C9A); // Deep blue-grey that complements the gradient
        darkPaint.setStyle(Paint.Style.FILL);

        // 🌈 Initialize neon renderer
        neonRenderer = new NeonChessboardRenderer();
        
        // ⚡ Initialize electric arc renderer and settings
        electricArcRenderer = new ElectricArcRenderer();
        electricArcSettings = new ElectricArcSettingsManager(getContext());
        
        // 🔌 Initialize circuit trace renderer and settings
        circuitTraceRenderer = new CircuitTraceRenderer();
        circuitTraceSettings = new CircuitTraceSettingsManager(getContext());
        
        // 🤖 Initialize robotic piece animator and settings
        roboticAnimator = new RoboticPieceAnimator();
        roboticAnimationSettings = new RoboticAnimationSettingsManager(getContext());
        
        // Keep all your other paint setups the same
        float dp = getResources().getDisplayMetrics().density;
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setStrokeWidth(dp * 4);
        selectedPaint.setColor(0xFFFFC107);
        selectedPaint.setShadowLayer(dp * 6, 0, 0, 0x66FFC107);

        // TEMPORARY FIX: Comment out hardware layer to fix inflation issue
        // setLayerType(LAYER_TYPE_HARDWARE, selectedPaint);

        lastMovePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lastMovePaint.setColor(0x334B69FF);
        lastMovePaint.setStyle(Paint.Style.FILL);

        legalMovePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        legalMovePaint.setColor(0x660000FF);

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setAlpha(80);
        
        // Enable software rendering for BlurMaskFilter to work properly
        // BlurMaskFilter effects are not supported with hardware acceleration
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        Log.d("ChessBoardView", "🎨 Set to software layer for neon glow effects");
    }
    
    /* ───────── 🎯 BOARD SCALING FIX ───────── */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        int desiredSize;
        
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            // Both dimensions fixed - use the smaller one
            desiredSize = Math.min(widthSize, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            // Width is fixed - make square
            desiredSize = widthSize;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            // Height is fixed - make square  
            desiredSize = heightSize;
        } else {
            // wrap_content - use maximum horizontal width for tactical puzzles
            int availableWidth = widthMode == MeasureSpec.AT_MOST ? widthSize : 1200;
            int availableHeight = heightMode == MeasureSpec.AT_MOST ? heightSize : 1200;
            
            // Prioritize horizontal space - use 95% of available width
            desiredSize = Math.max((int)(availableWidth * 0.95), availableHeight - 150);
            
            if (desiredSize <= 0) {
                desiredSize = 900; // Even larger fallback
            }
        }
        
        // Use nearly full horizontal width - only leave minimal margins
        desiredSize = Math.max(800, Math.min(desiredSize, 1600));
        
        setMeasuredDimension(desiredSize, desiredSize);
        
        Log.d("ChessBoardView", String.format("🎯 Measured: %dx%d (modes: w=%d h=%d)", 
            desiredSize, desiredSize, widthMode, heightMode));
    }
    
    /* ───────── 🎯 BOARD SCALING FIX ───────── */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        if (w > 0 && h > 0) {
            // Only recalculate if size actually changed significantly
            int minDimension = Math.min(w, h);
            int newSquareSize = minDimension / 8;
            
            if (Math.abs(newSquareSize - squareSize) > 2) { // Only update if difference > 2px
                squareSize = newSquareSize;
                sizeInitialized = true;
                Log.d("ChessBoardView", "🎯 Board size recalculated: " + squareSize + "px squares (view: " + w + "x" + h + ")");
            } else if (!sizeInitialized) {
                squareSize = newSquareSize;
                sizeInitialized = true;
                Log.d("ChessBoardView", "🎯 Board size initialized: " + squareSize + "px squares (view: " + w + "x" + h + ")");
            }
        }
    }

    /* ─────────   DRAW   ───────── */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 🎯 BOARD SCALING FIX: Only calculate size if not already initialized
        if (!sizeInitialized && getWidth() > 0 && getHeight() > 0) {
            int minDimension = Math.min(getWidth(), getHeight());
            squareSize = minDimension / 8;
            sizeInitialized = true;
            Log.d("ChessBoardView", "🎯 Board size calculated in onDraw fallback: " + squareSize + "px squares");
        }
        
        // Safety check - don't draw if not ready
        if (squareSize <= 0) {
            Log.d("ChessBoardView", "⚠️ Board not ready for drawing (squareSize=" + squareSize + ")");
            return;
        }
        
        int pad = squareSize / 16;

        /* 1) squares - Enhanced with neon effects */
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int br = flipped ? 7 - r : r, bc = flipped ? 7 - c : c;
                boolean isLightSquare = ((br + bc) & 1) == 0;
                
                // Apply sliding puzzle offsets if in puzzle mode
                float offsetX = 0f, offsetY = 0f;
                if (slidingPuzzleMode) {
                    offsetX = squareOffsets[br][bc][0];
                    offsetY = squareOffsets[br][bc][1];
                }
                
                // Calculate square bounds
                float left = c * squareSize + offsetX - 0.5f;
                float top = r * squareSize + offsetY - 0.5f;
                float right = c * squareSize + squareSize + offsetX + 0.5f;
                float bottom = r * squareSize + squareSize + offsetY + 0.5f;
                
                // Draw neon square if enabled, otherwise use traditional rendering
                if (neonRenderer.isNeonModeEnabled()) {
                    neonRenderer.drawNeonSquare(canvas, left, top, right, bottom, isLightSquare);
                } else {
                    // Traditional square rendering
                    Paint p = isLightSquare ? lightPaint : darkPaint;
                    reusableRectF.set(left, top, right, bottom);
                    canvas.drawRect(reusableRectF, p);
                }
            }
        }
        
        // 🌈 Draw neon grid lines if enabled
        if (neonRenderer.isNeonModeEnabled()) {
            neonRenderer.drawNeonGridLines(canvas, 8, squareSize, 0, 0);
        }

        /* 1a) last move highlights - Using pre-allocated RectF */
        if (lastMoveFrom[0] != -1) {
            // Draw source square highlight
            int fr = flipped ? 7 - lastMoveFrom[0] : lastMoveFrom[0];
            int fc = flipped ? 7 - lastMoveFrom[1] : lastMoveFrom[1];
            reusableRectF.set(fc * squareSize, fr * squareSize, 
                             (fc + 1) * squareSize, (fr + 1) * squareSize);
            canvas.drawRect(reusableRectF, lastMovePaint);

            // Draw destination square highlight
            int tr = flipped ? 7 - lastMoveTo[0] : lastMoveTo[0];
            int tc = flipped ? 7 - lastMoveTo[1] : lastMoveTo[1];
            reusableRectF.set(tc * squareSize, tr * squareSize,
                             (tc + 1) * squareSize, (tr + 1) * squareSize);
            canvas.drawRect(reusableRectF, lastMovePaint);
        }

        /* 2) legal‑move dots (traditional blue dots - always available as option) */
        if (shouldShowTraditionalLegalMoveDots()) {
            for (int[] s : highlightSquares) {
                int dr = flipped ? 7 - s[0] : s[0], dc = flipped ? 7 - s[1] : s[1];
                canvas.drawCircle(dc * squareSize + squareSize / 2f,
                        dr * squareSize + squareSize / 2f,
                        squareSize / 8f, legalMovePaint);
            }
        }

        /* 3) static pieces (skip squares with active sprite) */
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                boolean covered = false;
                for (MovingPiece mp : movingPieces) {
                    if (mp.toRow == r && mp.toCol == c) {
                        covered = true;
                        break;
                    }
                }
                if (covered) continue;

                char pc = boardState[r][c];
                if (pc == ' ') continue;
                int vr = flipped ? 7 - r : r, vc = flipped ? 7 - c : c;
                
                // Apply sliding puzzle offsets if in puzzle mode
                float offsetX = 0f, offsetY = 0f;
                if (slidingPuzzleMode) {
                    offsetX = squareOffsets[r][c][0];
                    offsetY = squareOffsets[r][c][1];
                }
                
                // Use cached drawable to avoid repeated resource loading
                Drawable d = getCachedPieceDrawable(pc);
                if (d == null) continue;
                
                // Reuse bounds object
                reusableBounds.set((int)(vc * squareSize + offsetX + pad), (int)(vr * squareSize + offsetY + pad),
                        (int)(vc * squareSize + squareSize + offsetX - pad), (int)(vr * squareSize + squareSize + offsetY - pad));
                d.setBounds(reusableBounds);
                d.draw(canvas);
            }

        /* 4) glow - Using pre-allocated RectF */
        if (selectedRow != -1) {
            reusableRectF.set(selectedCol * squareSize, selectedRow * squareSize,
                             selectedCol * squareSize + squareSize, selectedRow * squareSize + squareSize);
            float cornerRadius = squareSize * 0.1f;
            canvas.drawRoundRect(reusableRectF, cornerRadius, cornerRadius, selectedPaint);
        }

        /* 5) moving sprite */
        if (!movingPieces.isEmpty()) {
            Iterator<MovingPiece> it = movingPieces.iterator();
            while (it.hasNext()) if (it.next().draw(canvas, squareSize, pad, boardState)) it.remove();
            if (!movingPieces.isEmpty()) postInvalidateOnAnimation();
        }

        /* 5.5) physics-based captured piece animation */
        if (capturedPieceBeingAnimated != ' ') {
            Drawable capturedDrawable = getCachedPieceDrawable(capturedPieceBeingAnimated);
            if (capturedDrawable != null) {
                // Save canvas state for alpha blending
                int saveCount = canvas.save();
                
                // Apply alpha for fade-out effect
                canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int)(capturedPieceAlpha * 255), Canvas.ALL_SAVE_FLAG);
                
                // Draw captured piece at physics animation position
                int pieceSize = squareSize - pad * 2;
                reusableBounds.set(
                    (int)(capturedPieceAnimX - pieceSize / 2f), 
                    (int)(capturedPieceAnimY - pieceSize / 2f),
                    (int)(capturedPieceAnimX + pieceSize / 2f), 
                    (int)(capturedPieceAnimY + pieceSize / 2f)
                );
                capturedDrawable.setBounds(reusableBounds);
                capturedDrawable.draw(canvas);
                
                canvas.restoreToCount(saveCount);
            }
        }

        /* 6) advice highlights */
        drawHighlights(canvas);
        
        /* 7) move indicator overlays (check, blunder, brilliant) */
        drawMoveIndicatorOverlays(canvas);
        
        /* 8) ⚡ Electric arc move trails */
        if (electricArcRenderer != null && electricArcSettings.isElectricArcEnabled()) {
            electricArcRenderer.drawArc(canvas);
        }
        
        /* 9) 🔌 Circuit trace move highlights */
        if (circuitTraceRenderer != null && circuitTraceSettings.isCircuitTraceEnabled()) {
            circuitTraceRenderer.drawCircuits(canvas);
        }

    }

    /* ───────── external API ───────── */
    public int getSelectedRow() {
        return selectedRow == -1 ? -1 : (flipped ? 7 - selectedRow : selectedRow);
    }

    public int getSelectedCol() {
        return selectedCol == -1 ? -1 : (flipped ? 7 - selectedCol : selectedCol);
    }

    public char getPieceAt(int r, int c) {
        return boardState[r][c];
    }

    public void updateBoardFromFen(String fen) {
        if (fen == null || fen.isEmpty()) {
            Log.w("ChessBoardView", "⚠️ Received null/empty FEN");
            return;
        }

        Log.d("ChessBoardView", "🔄 Updating board from FEN: " + fen);

        // CRITICAL: Store current real FEN for proper turn detection
        setRealCurrentFEN(fen);

        // Create a temporary copy of the current board state
        char[][] newBoardState = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(boardState[i], 0, newBoardState[i], 0, 8);
        }

        // Split off just the square‑placement part
        String[] parts = fen.split("\\s+");
        String[] ranks = parts[0].split("/");

        // CRITICAL: Validate FEN structure before parsing
        if (ranks.length != 8) {
            Log.e("ChessBoardView", "❌ Invalid FEN - wrong number of ranks: " + ranks.length);
            return;
        }

        // For each of the 8 ranks...
        for (int r = 0; r < 8; r++) {
            int c = 0;
            for (char ch : ranks[r].toCharArray()) {
                if (c >= 8) {
                    Log.e("ChessBoardView", "❌ FEN rank " + r + " has too many squares");
                    break;
                }
                
                if (Character.isDigit(ch)) {
                    // e.g. '3' means three empty squares
                    int empty = ch - '0';
                    for (int i = 0; i < empty && c < 8; i++) {
                        newBoardState[r][c++] = ' ';
                    }
                } else {
                    // a letter is a piece
                    newBoardState[r][c++] = ch;
                }
            }
        }

        // Check if anything actually changed
        boolean boardChanged = false;
        for (int r = 0; r < 8 && !boardChanged; r++) {
            for (int c = 0; c < 8; c++) {
                if (boardState[r][c] != newBoardState[r][c]) {
                    boardChanged = true;
                    break;
                }
            }
        }

        // Only update if something changed
        if (boardChanged) {
            Log.d("ChessBoardView", "📋 Board state changed - updating display");
            
            // CRITICAL: Clear animations BEFORE updating board state to prevent ghost pieces
            movingPieces.clear();
            
            // Update the board state
            for (int r = 0; r < 8; r++) {
                System.arraycopy(newBoardState[r], 0, boardState[r], 0, 8);
            }

            // Clear any selection/highlights now that the board has re‑drawn
            selectedRow = selectedCol = -1;
            highlightSquares.clear();

            // Finally, redraw the view
            invalidate();
        }
    }

    public void setFlipped(boolean f) {
        flipped = f;
        invalidate();
    }

    public void setSelectedSquare(int br, int bc) {
        int newRow = flipped ? 7 - br : br;
        int newCol = flipped ? 7 - bc : bc;
        
        // Only invalidate if selection actually changed
        if (selectedRow != newRow || selectedCol != newCol) {
            selectedRow = newRow;
            selectedCol = newCol;
            postInvalidate(); // Use postInvalidate to avoid immediate layout calculations
        }
    }

    public void clearSelectionHighlight() {
        if (selectedRow != -1 || selectedCol != -1) {
            selectedRow = selectedCol = -1;
            postInvalidate(); // Use postInvalidate to avoid immediate layout calculations
        }
    }

    public void addHighlightedSquare(int r, int c) {
        highlightSquares.add(new int[]{r, c});
        if (highlightSquares.size() == 1) startLegalMoveAnimation();
    }

    public void clearHighlightedSquares() {
        if (!highlightSquares.isEmpty()) {
            highlightSquares.clear();
            legalMovePaint.setAlpha(0);
            legalAlpha = 0;
            
            // 🔌 Clear circuit traces when legal moves are cleared
            clearCircuitTraces();
            
            postInvalidate(); // Use postInvalidate to avoid immediate layout calculations
        }
    }

    /**
     * Call after updateBoardFromFen to slide a piece.
     */
    // In ChessBoardView.java
    public void animateMove(int fromR, int fromC, int toR, int toC) {
        // Log to help diagnose issues
        Log.d("ChessBoardView", "🎬 Animating move from " + fromR + "," + fromC + " to " + toR + "," + toC);

        // CRITICAL: Clear any existing animations to prevent ghost pieces
        movingPieces.clear();

        int vfr = flipped ? 7 - fromR : fromR;
        int vfc = flipped ? 7 - fromC : fromC;
        int vtr = flipped ? 7 - toR : toR;
        int vtc = flipped ? 7 - toC : toC;

        // CRITICAL: Get piece from DESTINATION (post-move state) since FEN was already updated
        char pc = boardState[toR][toC];
        if (pc == ' ') {
            Log.w("ChessBoardView", "⚠️ No piece at destination " + toR + "," + toC + " for animation");
            return;
        }
        
        int res = getDrawableForPiece(pc);
        if (res == 0) {
            Log.w("ChessBoardView", "⚠️ No drawable found for piece '" + pc + "'");
            return;
        }

        Drawable d = ContextCompat.getDrawable(getContext(), res);
        if (d == null) {
            Log.w("ChessBoardView", "⚠️ Failed to get drawable resource");
            return;
        }

        // CRITICAL: Temporarily remove piece from destination during animation
        char originalPiece = boardState[toR][toC];
        boardState[toR][toC] = ' ';

        Log.d("ChessBoardView", "✅ Starting animation for piece '" + pc + "' from " + fromR + "," + fromC + " to " + toR + "," + toC);

        // 🤖 Check if robotic animation is enabled
        RoboticAnimationSettingsManager.RoboticAnimationConfig roboticConfig = roboticAnimationSettings.getAnimationConfig();
        boolean useRoboticAnimation = roboticConfig.enabled;
        
        MovingPiece movingPiece;
        if (useRoboticAnimation) {
            // Apply style-specific configuration
            roboticAnimationSettings.applyStyleToConfig(roboticConfig);
            
            // Create robotic moving piece
            movingPiece = new MovingPiece(
                    d,
                    vfc * squareSize, vfr * squareSize,
                    vtc * squareSize, vtr * squareSize,
                    toR, toC, originalPiece,
                    true, roboticAnimator, roboticConfig);
            
            Log.d("ChessBoardView", String.format("🤖 Using robotic animation: style=%s, speed=%.1fx, effects=[overshoot=%s, jitter=%s, rotation=%s]",
                                                roboticConfig.style, roboticConfig.speedMultiplier,
                                                roboticConfig.overshootEnabled, roboticConfig.jitterEnabled, roboticConfig.rotationEnabled));
        } else {
            // Create traditional moving piece
            movingPiece = new MovingPiece(
                    d,
                    vfc * squareSize, vfr * squareSize,
                    vtc * squareSize, vtr * squareSize,
                    toR, toC, originalPiece);
            
            Log.d("ChessBoardView", "🎬 Using traditional smooth animation");
        }
        
        movingPieces.add(movingPiece);

        // This is crucial - tell the view to animate
        Log.d("ChessBoardView", "✅ Added animation, calling postInvalidateOnAnimation");
        postInvalidateOnAnimation();
    }

    // Method to draw all highlights - Optimized version
    private void drawHighlights(Canvas canvas) {
        // Remove expired highlights first
        Iterator<SquareHighlight> iterator = activeHighlights.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isExpired()) {
                iterator.remove();
                invalidate(); // Ensure we redraw after removing
            }
        }

        // Save canvas state before drawing highlights
        int savedCount = canvas.save();
        
        // Set xfermode once for all highlights
        highlightPaint.setXfermode(srcOverXfermode);

        // Draw each active highlight
        for (SquareHighlight highlight : activeHighlights) {
            // Convert algebraic notation to board coordinates using pre-allocated array
            algebraicToCoordinates(highlight.getSquare(), reusableCoords);
            int row = reusableCoords[0];
            int col = reusableCoords[1];

            // Calculate square position using already-computed squareSize
            float left = col * squareSize;
            float top = row * squareSize;

            // Create a more transparent color (only 40% opacity)
            int originalColor = highlight.getColor();
            int transparentColor = (originalColor & 0x00FFFFFF) | 0x66000000; // 40% alpha

            // Set the color for this highlight
            highlightPaint.setColor(transparentColor);

            // Draw the highlight using pre-allocated RectF
            reusableRectF.set(left, top, left + squareSize, top + squareSize);
            canvas.drawRect(reusableRectF, highlightPaint);
        }

        // Restore canvas state
        canvas.restoreToCount(savedCount);
    }

    // Helper method to convert algebraic notation to board coordinates - Optimized
    private int[] algebraicToCoordinates(String algebraic) {
        algebraicToCoordinates(algebraic, reusableCoords);
        return reusableCoords;
    }
    
    // Overloaded version that fills provided array (avoids allocation)
    private void algebraicToCoordinates(String algebraic, int[] result) {
        char file = algebraic.charAt(0);
        int rank = Character.getNumericValue(algebraic.charAt(1));

        int col = file - 'a'; // 'a' to 0, 'b' to 1, etc.
        int row = 8 - rank;   // Invert because 1 is the bottom rank

        // Apply flipping if the board is flipped
        if (flipped) {
            row = 7 - row;
            col = 7 - col;
        }

        result[0] = row;
        result[1] = col;
    }

    // Public method to add a highlight - ADD THIS
    public void highlightSquare(String square, int color, int durationMs) {
        activeHighlights.add(new SquareHighlight(square, color, durationMs));
        invalidate(); // Trigger redraw
    }

    // Public method to clear all highlights - ADD THIS
    public void clearHighlights() {
        activeHighlights.clear();
        invalidate();
    }

    /**
     * Get the current board position in FEN notation
     */
    public String getCurrentFEN() {
        // This is a simplified implementation - you'll need to convert your actual board state
        // In a real implementation, you would convert your pieces and positions to FEN
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Starting position
    }



    /**
     * Check if it's white's turn
     */
    /**
     * NEW METHOD: Determine whose turn it is based on the current FEN
     */
    /**
     * NEW METHOD: Determine whose turn it is based on the current FEN
     * This now properly parses the actual game state!
     */
    public boolean isWhiteTurn() {
        // Try to get the real FEN from the game state first
        String currentFen = getRealCurrentFEN();

        if (currentFen != null && currentFen.length() > 10) {
            // FEN format: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
            // The character after the board position indicates whose turn: 'w' for white, 'b' for black
            String[] parts = currentFen.split(" ");
            if (parts.length > 1) {
                boolean isWhitesTurn = "w".equals(parts[1]);
                Log.d("ChessBoardView", "🔄 Current turn from FEN: " + (isWhitesTurn ? "White" : "Black"));
                return isWhitesTurn;
            }
        }

        // Fallback to white's turn if we can't determine
        Log.w("ChessBoardView", "⚠️ Could not determine turn from FEN, defaulting to white");
        return true;
    }

    /**
     * Get the real current FEN from the connected ViewModel
     */
    private String getRealCurrentFEN() {
        // This will be set by MainActivity when the FEN changes
        return this.realCurrentFEN;
    }

    // Add this field to store the real FEN
    private String realCurrentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /**
     * NEW METHOD: Update the real FEN when the game state changes
     * This will be called by MainActivity
     */
    public void setRealCurrentFEN(String fen) {
        this.realCurrentFEN = fen;
        Log.d("ChessBoardView", "📝 Updated real FEN: " + fen);
    }


    /**
     * Check if the current player is in check
     */
    public boolean isCheck() {
        // In a real implementation, you would check your game logic
        return false; // Default to no check
    }

    /**
     * Check if the current position is checkmate
     */
    public boolean isCheckmate() {
        // In a real implementation, you would check your game logic
        return false; // Default to no checkmate
    }

    /**
     * Check if the current position is stalemate
     */
    public boolean isStalemate() {
        // In a real implementation, you would check your game logic
        return false; // Default to no stalemate
    }

    /**
     * Get the total number of pieces on the board
     */
    public int getPieceCount() {
        // In a real implementation, you would count your pieces
        return 32; // Default to full set of pieces (starting position)
    }

    /* touch handling */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_DOWN) return super.onTouchEvent(e);
        
        // 🎯 BOARD SCALING FIX: Prevent touch events when size isn't stable
        if (squareSize <= 0 || !sizeInitialized) {
            Log.w("ChessBoardView", "⚠️ Ignoring touch - board not ready (squareSize=" + squareSize + ")");
            return true;
        }
        
        int vc = (int) (e.getX() / squareSize), vr = (int) (e.getY() / squareSize);
        if (vr < 0 || vr > 7 || vc < 0 || vc > 7) return true;
        int br = flipped ? 7 - vr : vr, bc = flipped ? 7 - vc : vc;
        if (squareTapListener != null) {
            squareTapListener.onSquareTapped(br, bc);
        }
        return true;
    }

    public void setOnSquareTapListener(OnSquareTapListener l) {
        squareTapListener = l;
    }

    /* helpers */
    private void startLegalMoveAnimation() {
        if (legalAnimator != null && legalAnimator.isRunning()) legalAnimator.cancel();
        legalAnimator = ValueAnimator.ofInt(0, 255);
        legalAnimator.setDuration(200);
        legalAnimator.addUpdateListener(a -> {
            legalAlpha = (int) a.getAnimatedValue();
            legalMovePaint.setAlpha(legalAlpha);
            invalidate();
        });
        legalAnimator.start();
    }

    /**
     * Sets the squares to highlight for the last move
     */
    public void setLastMove(int fromRow, int fromCol, int toRow, int toCol) {
        lastMoveFrom[0] = fromRow;
        lastMoveFrom[1] = fromCol;
        lastMoveTo[0] = toRow;
        lastMoveTo[1] = toCol;
        
        // ⚡ Trigger electric arc effect for the move
        createElectricArcForMove(fromRow, fromCol, toRow, toCol);
        
        // 🔌 Show last move circuit trace 
        createLastMoveCircuitTrace(fromRow, fromCol, toRow, toCol);
        
        invalidate(); // Request redraw
    }
    
    /**
     * ⚡ Create electric arc effect for piece movement
     * Converts board coordinates to pixel coordinates and triggers arc animation
     */
    private void createElectricArcForMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (electricArcRenderer == null || electricArcSettings == null) {
            return;
        }
        
        if (!electricArcSettings.isElectricArcEnabled()) {
            Log.d("ChessBoardView", "⚡ Electric arc disabled, skipping effect");
            return;
        }
        
        // Convert board coordinates to pixel coordinates
        float fromX = (flipped ? 7 - fromCol : fromCol) * squareSize + squareSize / 2f;
        float fromY = (flipped ? 7 - fromRow : fromRow) * squareSize + squareSize / 2f;
        float toX = (flipped ? 7 - toCol : toCol) * squareSize + squareSize / 2f;
        float toY = (flipped ? 7 - toRow : toRow) * squareSize + squareSize / 2f;
        
        // Determine if this is a knight move (L-shaped)
        boolean isKnightMove = isKnightMovePattern(fromRow, fromCol, toRow, toCol);
        
        // Apply current theme settings to renderer
        electricArcSettings.applySettingsToRenderer(electricArcRenderer);
        
        // Create arc effect with callback for view updates
        electricArcRenderer.createMoveTrail(fromX, fromY, toX, toY, isKnightMove, 
            new ElectricArcRenderer.ArcCallback() {
                @Override
                public void onInvalidate() {
                    // Request view redraw for animation frame
                    invalidate();
                }
                
                @Override
                public void onArcComplete() {
                    Log.d("ChessBoardView", "⚡ Electric arc animation completed");
                }
            });
        
        Log.d("ChessBoardView", "⚡ Electric arc created from (" + fromRow + "," + fromCol + 
              ") to (" + toRow + "," + toCol + ") knight=" + isKnightMove + 
              " pixels=(" + fromX + "," + fromY + ")->(" + toX + "," + toY + ")");
    }
    
    /**
     * Determine if move follows knight movement pattern (L-shaped)
     */
    private boolean isKnightMovePattern(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // Knight moves: 2 squares in one direction, 1 square perpendicular
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    /**
     * 🔌 Create last move circuit trace
     */
    private void createLastMoveCircuitTrace(int fromRow, int fromCol, int toRow, int toCol) {
        if (circuitTraceRenderer == null || circuitTraceSettings == null) {
            return;
        }
        
        if (!circuitTraceSettings.isCircuitTraceEnabled() || 
            !circuitTraceSettings.isLastMoveTraceEnabled()) {
            return;
        }
        
        // Apply current theme settings
        circuitTraceSettings.applySettingsToRenderer(circuitTraceRenderer);
        
        // Show last move trace
        circuitTraceRenderer.showLastMoveTrace(fromRow, fromCol, toRow, toCol, 
                                             squareSize, flipped);
        
        Log.d("ChessBoardView", "🔌 Created last move circuit trace from (" + fromRow + "," + fromCol + 
              ") to (" + toRow + "," + toCol + ")");
    }
    
    /**
     * 🔌 Show circuit traces for legal moves
     */
    public void showCircuitTracesForLegalMoves(int pieceRow, int pieceCol, List<int[]> legalMoves) {
        Log.d("ChessBoardView", String.format("🔌 showCircuitTracesForLegalMoves called: piece(%d,%d), %d legal moves, renderer=%s, settings=%s", 
                                            pieceRow, pieceCol, legalMoves.size(), 
                                            circuitTraceRenderer != null ? "OK" : "NULL", 
                                            circuitTraceSettings != null ? "OK" : "NULL"));
        
        if (circuitTraceRenderer == null || circuitTraceSettings == null) {
            Log.w("ChessBoardView", "🔌 Circuit trace renderer or settings is null, returning");
            return;
        }
        
        boolean enabled = circuitTraceSettings.isCircuitTraceEnabled();
        Log.d("ChessBoardView", "🔌 Circuit traces enabled: " + enabled);
        
        if (!enabled) {
            Log.d("ChessBoardView", "🔌 Circuit traces disabled, skipping legal moves");
            return;
        }
        
        // Apply current theme settings
        circuitTraceSettings.applySettingsToRenderer(circuitTraceRenderer);
        
        // Show circuit traces with callback for view updates
        Log.d("ChessBoardView", "🔌 Calling circuitTraceRenderer.showLegalMoves()");
        circuitTraceRenderer.showLegalMoves(pieceRow, pieceCol, legalMoves, squareSize, flipped,
            new CircuitTraceRenderer.CircuitCallback() {
                @Override
                public void onInvalidate() {
                    // Request view redraw for animation frame
                    invalidate();
                }
                
                @Override
                public void onAnimationComplete() {
                    Log.d("ChessBoardView", "🔌 Circuit trace animation completed");
                }
            });
        
        Log.d("ChessBoardView", "🔌 Created circuit traces from (" + pieceRow + "," + pieceCol + 
              ") to " + legalMoves.size() + " legal moves");
    }
    
    /**
     * 🔌 Clear all circuit traces
     */
    public void clearCircuitTraces() {
        if (circuitTraceRenderer != null) {
            circuitTraceRenderer.clearCircuits();
        }
    }
    
    /**
     * 🔌 Clear last move circuit trace
     */
    public void clearLastMoveCircuitTrace() {
        if (circuitTraceRenderer != null) {
            circuitTraceRenderer.clearLastMoveTrace();
        }
    }

    /**
     * Updates the check status of the king
     *
     * @param inCheck Whether a king is in check
     * @param row     Row of the king in check, or -1 if no king in check
     * @param col     Column of the king in check, or -1 if no king in check
     */
    public void setKingInCheck(boolean inCheck, int row, int col) {
        kingInCheck = inCheck;
        checkKingPosition[0] = row;
        checkKingPosition[1] = col;
        invalidate(); // Request redraw
    }
    
    /**
     * Clear piece drawable cache - call when chess set changes
     */
    public void clearPieceCache() {
        pieceDrawableCache.clear();
        invalidate(); // Redraw with new pieces
        Log.d("ChessBoardView", "🎨 Piece cache cleared for new chess set");
    }
    
    // ==================== 🤖 ROBOTIC ANIMATION PUBLIC API ====================
    
    /**
     * 🤖 Enable or disable robotic piece movement animations
     */
    public void setRoboticAnimationEnabled(boolean enabled) {
        if (roboticAnimationSettings != null) {
            roboticAnimationSettings.setRoboticAnimationEnabled(enabled);
            Log.d("ChessBoardView", "🤖 Robotic animation " + (enabled ? "enabled" : "disabled"));
        }
    }
    
    /**
     * 🤖 Check if robotic animations are enabled
     */
    public boolean isRoboticAnimationEnabled() {
        return roboticAnimationSettings != null && roboticAnimationSettings.isRoboticAnimationEnabled();
    }
    
    /**
     * 🤖 Set robotic animation style
     */
    public void setRoboticAnimationStyle(String style) {
        if (roboticAnimationSettings != null) {
            roboticAnimationSettings.setRoboticAnimationStyle(style);
            Log.d("ChessBoardView", "🤖 Robotic animation style set to: " + style);
        }
    }
    
    /**
     * 🤖 Set robotic animation speed (50-200%)
     */
    public void setRoboticAnimationSpeed(int speedPercent) {
        if (roboticAnimationSettings != null) {
            roboticAnimationSettings.setRoboticAnimationSpeed(speedPercent);
            Log.d("ChessBoardView", "🤖 Robotic animation speed set to: " + speedPercent + "%");
        }
    }
    
    /**
     * 🤖 Apply settings from preferences
     */
    public void applyRoboticAnimationSettingsFromPreferences() {
        if (roboticAnimationSettings != null) {
            roboticAnimationSettings.logCurrentSettings();
            Log.d("ChessBoardView", "🤖 Applied robotic animation settings from preferences");
        }
    }
    
    /**
     * 🤖 Get current robotic animation configuration for debugging
     */
    public String getRoboticAnimationStatus() {
        if (roboticAnimationSettings == null) {
            return "Robotic animation not initialized";
        }
        
        RoboticAnimationSettingsManager.RoboticAnimationConfig config = roboticAnimationSettings.getAnimationConfig();
        return String.format("Robotic Animation Status: enabled=%s, style=%s, speed=%.1fx, complexity=%d",
                           config.enabled, config.style, config.speedMultiplier, 
                           roboticAnimationSettings.getComplexityLevel());
    }
    
    // ==================== 🎯 LEGAL MOVE DISPLAY OPTIONS ====================
    
    /**
     * 🎯 Determine if traditional blue legal move dots should be shown
     */
    private boolean shouldShowTraditionalLegalMoveDots() {
        // Check user preference for traditional legal move dots
        SharedPreferences prefs = getContext().getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("traditional_legal_move_dots_enabled", true); // Default enabled
    }
    
    /**
     * 🔌 Determine if circuit traces should be shown for legal moves
     */
    private boolean shouldShowCircuitTraces() {
        return circuitTraceSettings != null && circuitTraceSettings.isCircuitTraceEnabled();
    }
    
    /**
     * 🎯 Set whether traditional blue legal move dots should be shown
     */
    public void setTraditionalLegalMoveDotsEnabled(boolean enabled) {
        SharedPreferences prefs = getContext().getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("traditional_legal_move_dots_enabled", enabled).apply();
        invalidate(); // Redraw to reflect changes
        Log.d("ChessBoardView", "🎯 Traditional legal move dots " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * 🎯 Check if traditional blue legal move dots are enabled
     */
    public boolean isTraditionalLegalMoveDotsEnabled() {
        return shouldShowTraditionalLegalMoveDots();
    }

    /**
     * Get cached drawable for a piece - avoids repeated resource loading
     * Now integrates with ChessSetManager for personalized piece selection
     */
    private Drawable getCachedPieceDrawable(char piece) {
        Drawable cached = pieceDrawableCache.get(piece);
        if (cached == null) {
            // Use ChessSetManager for personalized piece selection
            ChessSetManager chessSetManager = ChessSetManager.getInstance(getContext());
            cached = chessSetManager.getPieceDrawable(piece);
            
            if (cached != null) {
                // Clone the drawable so each piece has its own instance
                try {
                    cached = cached.getConstantState().newDrawable().mutate();
                } catch (Exception e) {
                    // For custom drawables that might not support mutate()
                    Log.w("ChessBoardView", "Could not mutate drawable for piece: " + piece);
                }
                pieceDrawableCache.put(piece, cached);
            } else {
                // Fallback to hardcoded resources if ChessSetManager fails
                int resourceId = getDrawableForPiece(piece);
                if (resourceId != 0) {
                    cached = ContextCompat.getDrawable(getContext(), resourceId);
                    if (cached != null) {
                        cached = cached.getConstantState().newDrawable().mutate();
                        pieceDrawableCache.put(piece, cached);
                    }
                }
            }
        }
        return cached;
    }
    
    private int getDrawableForPiece(char p) {
        switch (p) {
            case 'P':
                return R.drawable.ic_white_pawn;
            case 'R':
                return R.drawable.ic_white_rook;
            case 'N':
                return R.drawable.ic_white_knight;
            case 'B':
                return R.drawable.ic_white_bishop;
            case 'Q':
                return R.drawable.ic_white_queen;
            case 'K':
                return R.drawable.ic_white_king;
            case 'p':
                return R.drawable.ic_black_pawn;
            case 'r':
                return R.drawable.ic_black_rook;
            case 'n':
                return R.drawable.ic_black_knight;
            case 'b':
                return R.drawable.ic_black_bishop;
            case 'q':
                return R.drawable.ic_black_queen;
            case 'k':
                return R.drawable.ic_black_king;
            default:
                return 0;
        }
    }

    /* ───── tap listener ───── */
    public interface OnSquareTapListener {
        void onSquareTapped(int row, int col);
    }

    /* ───── moving‑piece sprite ───── */
    private static class MovingPiece {
        final Drawable d;
        final float sx, sy, ex, ey;
        final int toRow, toCol;
        final char originalPiece;
        final long startT;
        final long dur; // Duration now depends on animation type
        
        // 🤖 ROBOTIC ANIMATION SUPPORT
        private final boolean useRoboticAnimation;
        private final RoboticPieceAnimator roboticAnimator;
        private final RoboticAnimationSettingsManager.RoboticAnimationConfig roboticConfig;
        
        // Current robotic animation state
        private float currentX, currentY;
        private float currentRotation = 0f;
        private boolean roboticAnimationStarted = false;

        // Traditional animation constructor
        MovingPiece(Drawable d, float sx, float sy, float ex, float ey,
                    int toRow, int toCol, char originalPiece) {
            this(d, sx, sy, ex, ey, toRow, toCol, originalPiece, false, null, null);
        }
        
        // Enhanced constructor with robotic animation support
        MovingPiece(Drawable d, float sx, float sy, float ex, float ey,
                    int toRow, int toCol, char originalPiece, 
                    boolean useRoboticAnimation, RoboticPieceAnimator roboticAnimator,
                    RoboticAnimationSettingsManager.RoboticAnimationConfig roboticConfig) {
            this.d = d;
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
            this.toRow = toRow;
            this.toCol = toCol;
            this.originalPiece = originalPiece;
            this.useRoboticAnimation = useRoboticAnimation;
            this.roboticAnimator = roboticAnimator;
            this.roboticConfig = roboticConfig;
            
            // Set duration based on animation type
            if (useRoboticAnimation && roboticConfig != null) {
                int baseDuration = 600; // Robotic animations are longer
                this.dur = Math.round(baseDuration / roboticConfig.speedMultiplier);
            } else {
                this.dur = 250; // Traditional animation duration
            }
            
            // Initialize current position
            this.currentX = sx;
            this.currentY = sy;
            
            startT = System.currentTimeMillis();
            
            Log.d("ChessBoardView", String.format("🎬 MovingPiece created: %s animation, piece='%c', duration=%dms", 
                                                useRoboticAnimation ? "robotic" : "traditional", originalPiece, dur));
        }

        // Traditional smooth easing for non-robotic movement
        private float easeOutQuad(float t) {
            return 1 - (1 - t) * (1 - t);
        }

        /**
         * Draws at interpolated position; returns true when finished.
         */
        boolean draw(Canvas c, int sq, int pad, char[][] boardState) {
            float t = Math.min(1f, (System.currentTimeMillis() - startT) / (float) dur);

            if (useRoboticAnimation && roboticAnimator != null && roboticConfig != null && roboticConfig.enabled) {
                return drawRoboticAnimation(c, sq, pad, boardState, t);
            } else {
                return drawTraditionalAnimation(c, sq, pad, boardState, t);
            }
        }
        
        /**
         * Draw traditional smooth animation
         */
        private boolean drawTraditionalAnimation(Canvas c, int sq, int pad, char[][] boardState, float t) {
            // Apply easing function for smoother motion
            float easedT = easeOutQuad(t);

            float x = sx + (ex - sx) * easedT;
            float y = sy + (ey - sy) * easedT;

            d.setBounds(Math.round(x) + pad, Math.round(y) + pad,
                    Math.round(x) + sq - pad, Math.round(y) + sq - pad);
            d.draw(c);
            
            // CRITICAL: Restore piece when animation completes
            if (t == 1f) {
                boardState[toRow][toCol] = originalPiece;
                Log.d("ChessBoardView", "🎬 Traditional animation completed - restored piece '" + originalPiece + "' to " + toRow + "," + toCol);
            }
            
            return t == 1f;
        }
        
        /**
         * Draw robotic mechanical animation
         */
        private boolean drawRoboticAnimation(Canvas c, int sq, int pad, char[][] boardState, float t) {
            // Start robotic animation if not already started
            if (!roboticAnimationStarted) {
                roboticAnimationStarted = true;
                
                // Determine piece profile
                RoboticPieceAnimator.PieceProfile profile = RoboticPieceAnimator.getPieceProfile(originalPiece);
                if (!roboticConfig.pieceProfilesEnabled) {
                    profile = RoboticPieceAnimator.PieceProfile.PAWN_MARCH; // Generic movement
                }
                
                // Start robotic animation with callback
                roboticAnimator.animateRoboticMove(sx, sy, ex, ey, profile, new RoboticPieceAnimator.RoboticAnimationCallback() {
                    @Override
                    public void onAnimationUpdate(float x, float y, float rotation, float alpha) {
                        currentX = x;
                        currentY = y;
                        if (roboticConfig.rotationEnabled) {
                            currentRotation = rotation;
                        }
                        // Note: We handle alpha in the drawing code below
                    }
                    
                    @Override
                    public void onAnimationComplete() {
                        Log.d("ChessBoardView", "🤖 Robotic animation callback: completed");
                    }
                    
                    @Override
                    public void onAnimationStart() {
                        Log.d("ChessBoardView", "🤖 Robotic animation callback: started");
                    }
                    
                    @Override
                    public void onMechanicalPause() {
                        Log.d("ChessBoardView", "⏸️ Robotic animation callback: mechanical pause");
                    }
                    
                    @Override
                    public void onSettlingStart() {
                        Log.d("ChessBoardView", "🔧 Robotic animation callback: settling started");
                    }
                });
                
                Log.d("ChessBoardView", String.format("🤖 Started robotic animation for piece '%c' with profile %s", 
                                                    originalPiece, profile.name()));
            }
            
            // Draw piece at current robotic animation position
            c.save();
            
            // Apply rotation if enabled
            if (roboticConfig.rotationEnabled && currentRotation != 0f) {
                float centerX = currentX + sq / 2f;
                float centerY = currentY + sq / 2f;
                c.rotate(currentRotation, centerX, centerY);
            }
            
            // Set piece bounds and draw
            d.setBounds(Math.round(currentX) + pad, Math.round(currentY) + pad,
                    Math.round(currentX) + sq - pad, Math.round(currentY) + sq - pad);
            d.draw(c);
            
            c.restore();
            
            // CRITICAL: Restore piece when animation completes
            if (t >= 1f) {
                boardState[toRow][toCol] = originalPiece;
                roboticAnimator.cancelAnimation(); // Clean up
                Log.d("ChessBoardView", "🤖 Robotic animation completed - restored piece '" + originalPiece + "' to " + toRow + "," + toCol);
            }
            
            return t >= 1f;
        }
    }

    /**
     * 🎯 PHASE 2: Physics-based capture animations (BUILDING-A-MODERN-CHESS-APP-UI.md lines 381-409)
     * Adds realistic physics motion when pieces are captured using FlingAnimation
     */
    public void animateCaptureWithPhysics(int fromR, int fromC, int toR, int toC, char capturedPiece) {
        Log.d("ChessBoardView", "🎯 Starting physics-based capture animation");
        
        // Convert to visual coordinates
        int vfr = flipped ? 7 - fromR : fromR;
        int vfc = flipped ? 7 - fromC : fromC;
        int vtr = flipped ? 7 - toR : toR;
        int vtc = flipped ? 7 - toC : toC;
        
        if (capturedPiece != ' ') {
            // Create captured piece view for physics animation
            animateCapturedPiecePhysics(vtr, vtc, capturedPiece);
        }
        
        // Normal move animation for the capturing piece
        animateMove(fromR, fromC, toR, toC);
    }
    
    /**
     * 🌪️ Animate captured piece with physics (FlingAnimation for realistic motion)
     */
    private void animateCapturedPiecePhysics(int visualRow, int visualCol, char capturedPiece) {
        try {
            // 🐛 FIX: Prevent multiple capture animations running simultaneously
            if (capturedPieceBeingAnimated != ' ') {
                Log.d("ChessBoardView", "⚠️ Capture animation already running, skipping duplicate for piece '" + capturedPiece + "'");
                return;
            }
            
            float startX = visualCol * squareSize + squareSize / 2f;
            float startY = visualRow * squareSize + squareSize / 2f;
            
            // Create physics properties for captured piece animation
            FloatPropertyCompat<ChessBoardView> capturedPieceX = new FloatPropertyCompat<ChessBoardView>("capturedPieceX") {
                @Override
                public float getValue(ChessBoardView object) {
                    return object.capturedPieceAnimX;
                }
                
                @Override
                public void setValue(ChessBoardView object, float value) {
                    object.capturedPieceAnimX = value;
                    object.postInvalidateOnAnimation();
                }
            };
            
            FloatPropertyCompat<ChessBoardView> capturedPieceY = new FloatPropertyCompat<ChessBoardView>("capturedPieceY") {
                @Override
                public float getValue(ChessBoardView object) {
                    return object.capturedPieceAnimY;
                }
                
                @Override
                public void setValue(ChessBoardView object, float value) {
                    object.capturedPieceAnimY = value;
                    object.postInvalidateOnAnimation();
                }
            };
            
            // Set initial position
            capturedPieceAnimX = startX;
            capturedPieceAnimY = startY;
            capturedPieceBeingAnimated = capturedPiece;
            capturedPieceAlpha = 1.0f;
            
            // Create fling animations with realistic physics
            FlingAnimation flingX = new FlingAnimation(this, capturedPieceX);
            FlingAnimation flingY = new FlingAnimation(this, capturedPieceY);
            
            // Set physics parameters (BUILDING-A-MODERN-CHESS-APP-UI.md: "with a slight ease-out bounce")
            float velocityX = (Math.random() > 0.5 ? 1 : -1) * (800 + (float)(Math.random() * 400)); // Random horizontal velocity
            float velocityY = -600 - (float)(Math.random() * 200); // Upward velocity (negative Y)
            
            flingX.setStartVelocity(velocityX)
                  .setFriction(1.2f) // Higher friction for quicker settling
                  .setMinValue(-squareSize * 2) // Allow off-screen movement
                  .setMaxValue(getWidth() + squareSize * 2);
                  
            flingY.setStartVelocity(velocityY)
                  .setFriction(1.5f) // Gravity-like effect
                  .setMinValue(-squareSize * 3) // Allow upward movement
                  .setMaxValue(getHeight() + squareSize);
            
            // Add completion listener to fade out captured piece
            flingY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    // Fade out the captured piece
                    ValueAnimator fadeOut = ValueAnimator.ofFloat(1.0f, 0.0f);
                    fadeOut.setDuration(300);
                    fadeOut.addUpdateListener(animator -> {
                        capturedPieceAlpha = (Float) animator.getAnimatedValue();
                        postInvalidateOnAnimation();
                    });
                    fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(android.animation.Animator animation) {
                            capturedPieceBeingAnimated = ' '; // Clear the animated piece
                        }
                    });
                    fadeOut.start();
                }
            });
            
            // Start physics animations
            flingX.start();
            flingY.start();
            
            Log.d("ChessBoardView", "🌪️ Physics capture animation started for piece '" + capturedPiece + "'");
            
        } catch (Exception e) {
            Log.e("ChessBoardView", "❌ Error in physics capture animation", e);
        }
    }
    
    // Animation state for captured piece physics
    private float capturedPieceAnimX = 0f;
    private float capturedPieceAnimY = 0f;
    private char capturedPieceBeingAnimated = ' ';
    private float capturedPieceAlpha = 0f;
    
    /**
     * 🎯 PHASE 2: Check/Blunder/Brilliant Move Animations (BUILDING-A-MODERN-CHESS-APP-UI.md lines 415-471)
     * Animated overlays with color coding and 3D spin effects
     */
    
    // Animation states for move indicators
    private boolean showingCheckIndicator = false;
    private boolean showingBlunderIndicator = false;
    private boolean showingBrilliantIndicator = false;
    private float moveIndicatorAlpha = 0f;
    private float moveIndicatorRotation = 0f;
    private String moveIndicatorText = "";
    private int moveIndicatorColor = 0;
    private Paint moveIndicatorPaint;
    private Paint moveIndicatorTextPaint;
    
    /**
     * 🚨 Animate king in check - flashing red warning
     */
    public void animateCheckIndicator(int kingRow, int kingCol) {
        Log.d("ChessBoardView", "🚨 Animating check indicator for king at " + kingRow + "," + kingCol);
        
        // Set check position
        checkKingPosition[0] = kingRow;
        checkKingPosition[1] = kingCol;
        kingInCheck = true;
        
        // Shake animation for the king
        ValueAnimator shakeAnimator = ValueAnimator.ofFloat(0f, 15f, -15f, 10f, -10f, 5f, -5f, 0f);
        shakeAnimator.setDuration(500);
        shakeAnimator.addUpdateListener(animator -> {
            // This could be used to shake the king piece specifically
            invalidate();
        });
        
        // Flashing red overlay
        ValueAnimator flashAnimator = ValueAnimator.ofFloat(0f, 1f, 0f, 1f, 0f);
        flashAnimator.setDuration(1200);
        flashAnimator.addUpdateListener(animator -> {
            // Flash intensity stored for rendering
            postInvalidateOnAnimation();
        });
        
        shakeAnimator.start();
        flashAnimator.start();
        
        // Auto-clear after animation
        postDelayed(() -> {
            kingInCheck = false;
            checkKingPosition[0] = checkKingPosition[1] = -1;
            invalidate();
        }, 1500);
    }
    
    /**
     * ❌ Animate blunder alert with spinning "??" overlay
     */
    public void animateBlunderAlert() {
        Log.d("ChessBoardView", "❌ Animating blunder alert with spinning overlay");
        
        showingBlunderIndicator = true;
        moveIndicatorText = "??";
        moveIndicatorColor = 0xFFFF4444; // Red for blunder
        
        initializeMoveIndicatorPaints();
        
        // 3D spin animation as specified in design doc
        ValueAnimator spinAnimator = ValueAnimator.ofFloat(0f, 720f); // Two full spins
        spinAnimator.setDuration(800);
        spinAnimator.addUpdateListener(animator -> {
            moveIndicatorRotation = (Float) animator.getAnimatedValue();
            postInvalidateOnAnimation();
        });
        
        // Fade in/out animation
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0f, 1f, 1f, 0f);
        alphaAnimator.setDuration(1500);
        alphaAnimator.addUpdateListener(animator -> {
            moveIndicatorAlpha = (Float) animator.getAnimatedValue();
            postInvalidateOnAnimation();
        });
        alphaAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                showingBlunderIndicator = false;
            }
        });
        
        spinAnimator.start();
        alphaAnimator.start();
    }
    
    /**
     * ✨ Animate brilliant move with sparkling "!!" overlay
     */
    public void animateBrilliantMove() {
        Log.d("ChessBoardView", "✨ Animating brilliant move with sparkling overlay");
        
        showingBrilliantIndicator = true;
        moveIndicatorText = "!!";
        moveIndicatorColor = 0xFF44FF44; // Green for brilliant
        
        initializeMoveIndicatorPaints();
        
        // Scale bounce animation
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0f, 1.2f, 1f);
        scaleAnimator.setDuration(600);
        scaleAnimator.setInterpolator(new android.view.animation.OvershootInterpolator(2.0f));
        scaleAnimator.addUpdateListener(animator -> {
            // Scale could be applied during draw
            postInvalidateOnAnimation();
        });
        
        // Fade animation
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0f, 1f, 1f, 0f);
        alphaAnimator.setDuration(2000); // Longer duration for brilliant moves
        alphaAnimator.addUpdateListener(animator -> {
            moveIndicatorAlpha = (Float) animator.getAnimatedValue();
            postInvalidateOnAnimation();
        });
        alphaAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                showingBrilliantIndicator = false;
            }
        });
        
        scaleAnimator.start();
        alphaAnimator.start();
    }
    
    /**
     * 🎨 Initialize paints for move indicator overlays
     */
    private void initializeMoveIndicatorPaints() {
        if (moveIndicatorPaint == null) {
            moveIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            moveIndicatorPaint.setStyle(Paint.Style.FILL);
            
            moveIndicatorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            moveIndicatorTextPaint.setTextAlign(Paint.Align.CENTER);
            moveIndicatorTextPaint.setTextSize(squareSize * 1.5f); // Large overlay text
            moveIndicatorTextPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            moveIndicatorTextPaint.setColor(0xFFFFFFFF); // White text
            moveIndicatorTextPaint.setShadowLayer(8f, 0f, 0f, 0xFF000000); // Black shadow
        }
    }
    
    /**
     * 🎭 Draw move indicator overlays (BUILDING-A-MODERN-CHESS-APP-UI.md specification)
     */
    private void drawMoveIndicatorOverlays(Canvas canvas) {
        // Draw enhanced check indicator
        if (kingInCheck && checkKingPosition[0] != -1 && checkKingPosition[1] != -1) {
            int vKingRow = flipped ? 7 - checkKingPosition[0] : checkKingPosition[0];
            int vKingCol = flipped ? 7 - checkKingPosition[1] : checkKingPosition[1];
            
            // Flashing red overlay on king's square
            Paint checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkPaint.setColor(0x88FF0000); // Semi-transparent red
            checkPaint.setStyle(Paint.Style.FILL);
            
            reusableRectF.set(vKingCol * squareSize, vKingRow * squareSize,
                             vKingCol * squareSize + squareSize, vKingRow * squareSize + squareSize);
            canvas.drawRoundRect(reusableRectF, squareSize * 0.1f, squareSize * 0.1f, checkPaint);
        }
        
        // Draw blunder/brilliant move overlays
        if ((showingBlunderIndicator || showingBrilliantIndicator) && moveIndicatorAlpha > 0) {
            initializeMoveIndicatorPaints();
            
            // Save canvas state for 3D transformations
            int saveCount = canvas.save();
            
            // Apply alpha
            moveIndicatorTextPaint.setAlpha((int)(moveIndicatorAlpha * 255));
            moveIndicatorPaint.setColor(moveIndicatorColor);
            moveIndicatorPaint.setAlpha((int)(moveIndicatorAlpha * 128)); // Semi-transparent background
            
            // Center of the board for overlay
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;
            
            // Apply rotation for 3D spin effect (blunder animation)
            if (showingBlunderIndicator) {
                canvas.rotate(moveIndicatorRotation, centerX, centerY);
            }
            
            // Draw background circle
            float circleRadius = squareSize * 1.2f;
            canvas.drawCircle(centerX, centerY, circleRadius, moveIndicatorPaint);
            
            // Draw text overlay
            Paint.FontMetrics fontMetrics = moveIndicatorTextPaint.getFontMetrics();
            float textY = centerY - (fontMetrics.top + fontMetrics.bottom) / 2f;
            canvas.drawText(moveIndicatorText, centerX, textY, moveIndicatorTextPaint);
            
            canvas.restoreToCount(saveCount);
        }
    }
    
    // ============================================================================
    // 🧩 SLIDING PUZZLE ANIMATION SYSTEM
    // ============================================================================
    
    /**
     * Sliding puzzle animation state and methods
     * Enables individual square animations for the sliding puzzle reveal effect
     */
    private boolean slidingPuzzleMode = false;
    private final float[][][] squareOffsets = new float[8][8][2]; // [row][col][x,y] offsets
    private final List<ValueAnimator> activeSquareAnimations = new ArrayList<>();
    
    /**
     * Check if sliding puzzle capability is enabled
     */
    public boolean hasSlidingPuzzleCapability() {
        return true; // ChessBoardView now supports sliding puzzle animations
    }
    
    /**
     * Enable sliding puzzle mode - allows individual square positioning
     */
    public void enableSlidingPuzzleMode() {
        Log.d("ChessBoardView", "🧩 Enabling sliding puzzle mode");
        slidingPuzzleMode = true;
        
        // Initialize all square offsets to zero (normal positions)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squareOffsets[row][col][0] = 0f; // x offset
                squareOffsets[row][col][1] = 0f; // y offset
            }
        }
    }
    
    /**
     * Disable sliding puzzle mode and return to normal drawing
     */
    public void disableSlidingPuzzleMode() {
        Log.d("ChessBoardView", "🧩 Disabling sliding puzzle mode");
        slidingPuzzleMode = false;
        
        // Cancel any active animations
        for (ValueAnimator animator : activeSquareAnimations) {
            if (animator.isRunning()) {
                animator.cancel();
            }
        }
        activeSquareAnimations.clear();
        
        // Reset all offsets
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squareOffsets[row][col][0] = 0f;
                squareOffsets[row][col][1] = 0f;
            }
        }
        
        invalidate(); // Redraw in normal mode
    }
    
    /**
     * Set scrambled positions for sliding puzzle (called before animation starts)
     */
    public void setSquarePositions(java.util.List<Integer> scrambledOrder) {
        Log.d("ChessBoardView", "🎲 Setting scrambled square positions");
        
        if (scrambledOrder.size() != 64) {
            Log.e("ChessBoardView", "❌ Invalid position list size: " + scrambledOrder.size());
            return;
        }
        
        // Apply scrambled positions as offsets
        for (int i = 0; i < 64; i++) {
            int currentRow = i / 8;
            int currentCol = i % 8;
            
            // Get scrambled target index and convert to row/col
            int scrambledIndex = scrambledOrder.get(i);
            int scrambledRow = scrambledIndex / 8;
            int scrambledCol = scrambledIndex % 8;
            
            // Calculate offset from normal position to scrambled position
            float offsetX = (scrambledCol - currentCol) * squareSize;
            float offsetY = (scrambledRow - currentRow) * squareSize;
            
            squareOffsets[currentRow][currentCol][0] = offsetX;
            squareOffsets[currentRow][currentCol][1] = offsetY;
        }
        
        invalidate(); // Show scrambled board
    }
    
    /**
     * Create animation for a single square to slide to its target position
     */
    public ValueAnimator createSquareSlideAnimation(int squareIndex, int targetRow, int targetCol, long duration) {
        int currentRow = squareIndex / 8;
        int currentCol = squareIndex % 8;
        
        Log.d("ChessBoardView", "🎬 Creating slide animation for square (" + currentRow + "," + currentCol + 
               ") -> (" + targetRow + "," + targetCol + ")");
        
        // Current offset values
        float startOffsetX = squareOffsets[currentRow][currentCol][0];
        float startOffsetY = squareOffsets[currentRow][currentCol][1];
        
        // Target is normal position (zero offset)
        float endOffsetX = 0f;
        float endOffsetY = 0f;
        
        // Create value animator
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);
        
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            
            // Interpolate between start and end offsets
            float currentOffsetX = startOffsetX + (endOffsetX - startOffsetX) * progress;
            float currentOffsetY = startOffsetY + (endOffsetY - startOffsetY) * progress;
            
            // Update square position
            squareOffsets[currentRow][currentCol][0] = currentOffsetX;
            squareOffsets[currentRow][currentCol][1] = currentOffsetY;
            
            // Trigger redraw
            postInvalidateOnAnimation();
        });
        
        // Track this animation
        activeSquareAnimations.add(animator);
        
        // Clean up when done
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                activeSquareAnimations.remove(animator);
            }
        });
        
        return animator;
    }
    
    /**
     * Check if we're in sliding puzzle mode during drawing
     */
    private boolean isInSlidingPuzzleMode() {
        return slidingPuzzleMode;
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // 🌈 NEON EFFECTS API - Modular control for futuristic board rendering
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Enable or disable neon glow effects on the chessboard.
     * When enabled, squares will render with electric blue/green glow effects.
     */
    public void setNeonModeEnabled(boolean enabled) {
        if (neonRenderer != null) {
            neonRenderer.setNeonModeEnabled(enabled);
            invalidate(); // Trigger redraw
            Log.d("ChessBoardView", "🌈 Neon mode " + (enabled ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Check if neon mode is currently enabled.
     */
    public boolean isNeonModeEnabled() {
        return neonRenderer != null && neonRenderer.isNeonModeEnabled();
    }
    
    /**
     * Set the intensity of the neon glow effects (0.0 - 1.0).
     * Higher values create more intense glows.
     */
    public void setNeonGlowIntensity(float intensity) {
        if (neonRenderer != null) {
            neonRenderer.setGlowIntensity(intensity);
            if (neonRenderer.isNeonModeEnabled()) {
                invalidate(); // Only redraw if neon mode is active
            }
        }
    }
    
    /**
     * Enable or disable pulsing animation for neon effects.
     * When enabled, the glow will pulse rhythmically.
     */
    public void setNeonPulsingEnabled(boolean enabled) {
        if (neonRenderer != null) {
            neonRenderer.setPulsingEnabled(enabled);
            if (neonRenderer.isNeonModeEnabled()) {
                invalidate(); // Trigger redraw
            }
        }
    }
    
    /**
     * Update neon colors for custom theming.
     * @param lightSquareColor Color for light squares (default: electric blue)
     * @param darkSquareColor Color for dark squares (default: neon green)  
     * @param gridColor Color for grid lines (default: soft cyan)
     */
    public void setNeonColors(int lightSquareColor, int darkSquareColor, int gridColor) {
        if (neonRenderer != null) {
            neonRenderer.updateNeonColors(lightSquareColor, darkSquareColor, gridColor);
            if (neonRenderer.isNeonModeEnabled()) {
                invalidate(); // Redraw with new colors
            }
        }
    }
    
    /**
     * Get access to the neon renderer for advanced configuration.
     * Use with caution - prefer the public API methods above.
     */
    public NeonChessboardRenderer getNeonRenderer() {
        return neonRenderer;
    }
    
    /**
     * Apply neon settings from SharedPreferences.
     * Call this method when the view is created or when settings change.
     */
    public void applyNeonSettingsFromPreferences() {
        NeonSettingsManager.applyNeonSettings(getContext(), this);
    }
    
    // ⚡ ELECTRIC ARC PUBLIC API
    
    /**
     * Enable or disable electric arc move trails
     */
    public void setElectricArcEnabled(boolean enabled) {
        if (electricArcSettings != null) {
            electricArcSettings.setElectricArcEnabled(enabled);
        }
    }
    
    /**
     * Check if electric arc effects are enabled
     */
    public boolean isElectricArcEnabled() {
        return electricArcSettings != null && electricArcSettings.isElectricArcEnabled();
    }
    
    /**
     * Set electric arc theme
     * @param theme One of: electric_blue, lightning_white, neon_orange, matrix_green, cyber_purple
     */
    public void setElectricArcTheme(String theme) {
        if (electricArcSettings != null) {
            electricArcSettings.setElectricArcTheme(theme);
        }
    }
    
    /**
     * Set electric arc intensity (0-100)
     */
    public void setElectricArcIntensity(int intensity) {
        if (electricArcSettings != null) {
            electricArcSettings.setElectricArcIntensity(intensity);
        }
    }
    
    /**
     * Get access to the electric arc settings manager for advanced configuration
     */
    public ElectricArcSettingsManager getElectricArcSettings() {
        return electricArcSettings;
    }
    
    /**
     * Get access to the electric arc renderer for advanced configuration
     */
    public ElectricArcRenderer getElectricArcRenderer() {
        return electricArcRenderer;
    }
    
    /**
     * Apply electric arc settings from SharedPreferences
     * Call this method when the view is created or when settings change
     */
    public void applyElectricArcSettingsFromPreferences() {
        if (electricArcSettings != null && electricArcRenderer != null) {
            electricArcSettings.applySettingsToRenderer(electricArcRenderer);
            Log.d("ChessBoardView", "⚡ Applied electric arc settings from preferences");
        }
    }
    
    /**
     * Manually trigger electric arc effect (for testing or special cases)
     */
    public void createElectricArc(int fromRow, int fromCol, int toRow, int toCol) {
        createElectricArcForMove(fromRow, fromCol, toRow, toCol);
    }
    
    // 🔌 CIRCUIT TRACE PUBLIC API
    
    /**
     * Enable or disable circuit trace move highlights
     */
    public void setCircuitTraceEnabled(boolean enabled) {
        if (circuitTraceSettings != null) {
            circuitTraceSettings.setCircuitTraceEnabled(enabled);
        }
    }
    
    /**
     * Check if circuit trace effects are enabled
     */
    public boolean isCircuitTraceEnabled() {
        return circuitTraceSettings != null && circuitTraceSettings.isCircuitTraceEnabled();
    }
    
    /**
     * Set circuit trace theme
     * @param theme One of: cyberpunk, matrix, neon, classic, stealth
     */
    public void setCircuitTraceTheme(String theme) {
        if (circuitTraceSettings != null) {
            circuitTraceSettings.setCircuitTraceTheme(theme);
        }
    }
    
    /**
     * Set flow animation enabled/disabled
     */
    public void setCircuitFlowAnimationEnabled(boolean enabled) {
        if (circuitTraceSettings != null) {
            circuitTraceSettings.setFlowAnimationEnabled(enabled);
        }
    }
    
    /**
     * Set glow effects enabled/disabled
     */
    public void setCircuitGlowEnabled(boolean enabled) {
        if (circuitTraceSettings != null) {
            circuitTraceSettings.setGlowEnabled(enabled);
        }
    }
    
    /**
     * Get access to the circuit trace settings manager for advanced configuration
     */
    public CircuitTraceSettingsManager getCircuitTraceSettings() {
        return circuitTraceSettings;
    }
    
    /**
     * Get access to the circuit trace renderer for advanced configuration
     */
    public CircuitTraceRenderer getCircuitTraceRenderer() {
        return circuitTraceRenderer;
    }
    
    /**
     * Apply circuit trace settings from SharedPreferences
     * Call this method when the view is created or when settings change
     */
    public void applyCircuitTraceSettingsFromPreferences() {
        if (circuitTraceSettings != null && circuitTraceRenderer != null) {
            circuitTraceSettings.applySettingsToRenderer(circuitTraceRenderer);
            Log.d("ChessBoardView", "🔌 Applied circuit trace settings from preferences");
        }
    }
    
}
