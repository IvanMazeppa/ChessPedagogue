package com.example.chesspedagogue;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        // Create clean, sophisticated colors that match your wood theme
        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setColor(0xFFF5F5DC); // Elegant ivory/cream color
        lightPaint.setStyle(Paint.Style.FILL);

        darkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        darkPaint.setColor(0xFF8B5E3C); // Rich wood brown that matches your UI
        darkPaint.setStyle(Paint.Style.FILL);

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
    }

    /* ─────────   DRAW   ───────── */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        squareSize = getWidth() / 8;
        int pad = squareSize / 16;

        /* 1) squares */
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                int br = flipped ? 7 - r : r, bc = flipped ? 7 - c : c;
                Paint p = ((br + bc) & 1) == 0 ? lightPaint : darkPaint;
                float l = c * squareSize - 0.5f, t = r * squareSize - 0.5f;
                canvas.drawRect(l, t, l + squareSize + 1, t + squareSize + 1, p);
            }

        /* 1a) last move highlights */
        if (lastMoveFrom[0] != -1) {
            // Draw source square highlight
            int fr = flipped ? 7 - lastMoveFrom[0] : lastMoveFrom[0];
            int fc = flipped ? 7 - lastMoveFrom[1] : lastMoveFrom[1];
            float l = fc * squareSize, t = fr * squareSize;
            canvas.drawRect(l, t, l + squareSize, t + squareSize, lastMovePaint);

            // Draw destination square highlight
            int tr = flipped ? 7 - lastMoveTo[0] : lastMoveTo[0];
            int tc = flipped ? 7 - lastMoveTo[1] : lastMoveTo[1];
            l = tc * squareSize;
            t = tr * squareSize;
            canvas.drawRect(l, t, l + squareSize, t + squareSize, lastMovePaint);
        }

        /* 2) legal‑move dots */
        for (int[] s : highlightSquares) {
            int dr = flipped ? 7 - s[0] : s[0], dc = flipped ? 7 - s[1] : s[1];
            canvas.drawCircle(dc * squareSize + squareSize / 2f,
                    dr * squareSize + squareSize / 2f,
                    squareSize / 8f, legalMovePaint);
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
                Drawable d = ContextCompat.getDrawable(getContext(), getDrawableForPiece(pc));
                if (d == null) continue;
                d.setBounds(vc * squareSize + pad, vr * squareSize + pad,
                        vc * squareSize + squareSize - pad, vr * squareSize + squareSize - pad);
                d.draw(canvas);
            }

        /* 4) glow */
        if (selectedRow != -1) {
            int l = selectedCol * squareSize, t = selectedRow * squareSize;
            canvas.drawRoundRect(l, t, l + squareSize, t + squareSize,
                    squareSize * 0.1f, squareSize * 0.1f, selectedPaint);
        }

        /* 5) moving sprite */
        if (!movingPieces.isEmpty()) {
            Iterator<MovingPiece> it = movingPieces.iterator();
            while (it.hasNext()) if (it.next().draw(canvas, squareSize, pad)) it.remove();
            if (!movingPieces.isEmpty()) postInvalidateOnAnimation();
        }


        /* 6) advice highlights */
        drawHighlights(canvas);

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
        if (fen == null || fen.isEmpty()) return;

        // Create a temporary copy of the current board state
        char[][] newBoardState = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(boardState[i], 0, newBoardState[i], 0, 8);
        }

        // Split off just the square‑placement part
        String[] parts = fen.split("\\s+");
        String[] ranks = parts[0].split("/");

        // For each of the 8 ranks...
        for (int r = 0; r < 8; r++) {
            int c = 0;
            for (char ch : ranks[r].toCharArray()) {
                if (Character.isDigit(ch)) {
                    // e.g. '3' means three empty squares
                    int empty = ch - '0';
                    for (int i = 0; i < empty; i++) {
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
        selectedRow = flipped ? 7 - br : br;
        selectedCol = flipped ? 7 - bc : bc;
        invalidate();
    }

    public void clearSelectionHighlight() {
        selectedRow = selectedCol = -1;
        invalidate();
    }

    public void addHighlightedSquare(int r, int c) {
        highlightSquares.add(new int[]{r, c});
        if (highlightSquares.size() == 1) startLegalMoveAnimation();
    }

    public void clearHighlightedSquares() {
        highlightSquares.clear();
        legalMovePaint.setAlpha(0);
        legalAlpha = 0;
        invalidate();
    }

    /**
     * Call after updateBoardFromFen to slide a piece.
     */
    // In ChessBoardView.java
    public void animateMove(int fromR, int fromC, int toR, int toC) {
        // Log to help diagnose issues
        Log.d("ChessBoardView", "Animating move from " + fromR + "," + fromC + " to " + toR + "," + toC);

        int vfr = flipped ? 7 - fromR : fromR;
        int vfc = flipped ? 7 - fromC : fromC;
        int vtr = flipped ? 7 - toR : toR;
        int vtc = flipped ? 7 - toC : toC;

        char pc = boardState[toR][toC];
        int res = getDrawableForPiece(pc);
        if (res == 0) {
            Log.d("ChessBoardView", "No drawable found for piece " + pc);
            return;
        }

        Drawable d = ContextCompat.getDrawable(getContext(), res);
        if (d == null) {
            Log.d("ChessBoardView", "Failed to get drawable resource");
            return;
        }

        // Add the moving piece to the list
        movingPieces.add(new MovingPiece(
                d,
                vfc * squareSize, vfr * squareSize,
                vtc * squareSize, vtr * squareSize,
                toR, toC));

        // This is crucial - tell the view to animate
        Log.d("ChessBoardView", "Added animation, calling postInvalidateOnAnimation");
        postInvalidateOnAnimation();
    }

    // Method to draw all highlights - ADD THIS ENTIRE METHOD
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

        // Draw each active highlight
        for (SquareHighlight highlight : activeHighlights) {
            // Convert algebraic notation to board coordinates
            int[] coords = algebraicToCoordinates(highlight.getSquare());
            int row = coords[0];
            int col = coords[1];

            // Calculate square size and position
            float squareSize = getWidth() / 8.0f;
            float left = col * squareSize;
            float top = row * squareSize;

            // Create a more transparent color (only 40% opacity)
            int originalColor = highlight.getColor();
            int alpha = 102; // 40% of 255
            int transparentColor = Color.argb(
                    alpha,
                    Color.red(originalColor),
                    Color.green(originalColor),
                    Color.blue(originalColor)
            );

            // Set the color for this highlight with proper transparency
            highlightPaint.setColor(transparentColor);

            // Draw the highlight using SRC_OVER blend mode (preserves what's underneath)
            highlightPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

            // Draw the highlight
            canvas.drawRect(left, top, left + squareSize, top + squareSize, highlightPaint);
        }

        // Restore canvas state
        canvas.restoreToCount(savedCount);
    }

    // Helper method to convert algebraic notation to board coordinates - ADD THIS
    private int[] algebraicToCoordinates(String algebraic) {
        char file = algebraic.charAt(0);
        int rank = Character.getNumericValue(algebraic.charAt(1));

        int col = file - 'a'; // 'a' to 0, 'b' to 1, etc.
        int row = 8 - rank;   // Invert because 1 is the bottom rank

        // Apply flipping if the board is flipped
        if (flipped) {
            row = 7 - row;
            col = 7 - col;
        }

        return new int[]{row, col};
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
        invalidate(); // Request redraw
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
        final long startT;
        final long dur = 250;   // ms

        MovingPiece(Drawable d, float sx, float sy, float ex, float ey,
                    int toRow, int toCol) {
            this.d = d;
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
            this.toRow = toRow;
            this.toCol = toCol;
            startT = System.currentTimeMillis();
        }

        // Add smooth easing for more natural movement
        private float easeOutQuad(float t) {
            return 1 - (1 - t) * (1 - t);
        }

        /**
         * Draws at interpolated position; returns true when finished.
         */
        boolean draw(Canvas c, int sq, int pad) {
            float t = Math.min(1f, (System.currentTimeMillis() - startT) / (float) dur);

            // Apply easing function for smoother motion
            float easedT = easeOutQuad(t);

            float x = sx + (ex - sx) * easedT;
            float y = sy + (ey - sy) * easedT;

            d.setBounds(Math.round(x) + pad, Math.round(y) + pad,
                    Math.round(x) + sq - pad, Math.round(y) + sq - pad);
            d.draw(c);
            return t == 1f;
        }
    }
}
