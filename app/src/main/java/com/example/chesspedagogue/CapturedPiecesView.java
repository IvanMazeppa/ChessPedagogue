package com.example.chesspedagogue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 🎨 Captured Pieces Display
 * Shows captured pieces in order: King, Queen, Rook, Bishop, Knight, Pawn
 * Visual feedback for spectator mode
 */
public class CapturedPiecesView extends View {
    private static final String TAG = "CapturedPiecesView";
    
    private List<Character> capturedPieces = new ArrayList<>();
    private Paint textPaint;
    private Paint backgroundPaint;
    private boolean isWhitePlayer;
    
    // Chess piece Unicode characters
    private static final char WHITE_KING = '\u2654';
    private static final char WHITE_QUEEN = '\u2655';
    private static final char WHITE_ROOK = '\u2656';
    private static final char WHITE_BISHOP = '\u2657';
    private static final char WHITE_KNIGHT = '\u2658';
    private static final char WHITE_PAWN = '\u2659';
    
    private static final char BLACK_KING = '\u265A';
    private static final char BLACK_QUEEN = '\u265B';
    private static final char BLACK_ROOK = '\u265C';
    private static final char BLACK_BISHOP = '\u265D';
    private static final char BLACK_KNIGHT = '\u265E';
    private static final char BLACK_PAWN = '\u265F';
    
    public CapturedPiecesView(Context context) {
        super(context);
        init();
    }
    
    public CapturedPiecesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(24f);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.TRANSPARENT);
    }
    
    /**
     * 🎯 Add captured piece in priority order
     */
    public void addCapturedPiece(char piece) {
        capturedPieces.add(piece);
        sortCapturedPieces();
        invalidate(); // Trigger redraw
        
        android.util.Log.d(TAG, "🎯 Captured piece added: " + piece + " (total: " + capturedPieces.size() + ")");
    }
    
    /**
     * Sort pieces by value: King > Queen > Rook > Bishop > Knight > Pawn
     */
    private void sortCapturedPieces() {
        capturedPieces.sort((piece1, piece2) -> {
            return Integer.compare(getPieceValue(piece2), getPieceValue(piece1));
        });
    }
    
    private int getPieceValue(char piece) {
        switch (Character.toLowerCase(piece)) {
            case '\u2654': case '\u265A': return 1000; // King
            case '\u2655': case '\u265B': return 9;    // Queen
            case '\u2656': case '\u265C': return 5;    // Rook
            case '\u2657': case '\u265D': return 3;    // Bishop
            case '\u2658': case '\u265E': return 3;    // Knight
            case '\u2659': case '\u265F': return 1;    // Pawn
            default: return 0;
        }
    }
    
    /**
     * Set whether this view shows pieces captured FROM white (so showing black pieces)
     * or captured FROM black (so showing white pieces)
     */
    public void setWhitePlayer(boolean isWhite) {
        this.isWhitePlayer = isWhite;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (capturedPieces.isEmpty()) {
            return;
        }
        
        float x = 8f;
        float y = getHeight() / 2f + textPaint.getTextSize() / 3f; // Center vertically
        float spacing = textPaint.getTextSize() + 4f;
        
        // Draw captured pieces in a line
        for (int i = 0; i < capturedPieces.size() && i < 8; i++) { // Limit to 8 pieces to fit
            char piece = capturedPieces.get(i);
            canvas.drawText(String.valueOf(piece), x, y, textPaint);
            x += spacing;
        }
        
        // Show count if more than 8 pieces
        if (capturedPieces.size() > 8) {
            canvas.drawText("+" + (capturedPieces.size() - 8), x + 8, y, textPaint);
        }
    }
    
    /**
     * Clear all captured pieces (for new game)
     */
    public void clearCapturedPieces() {
        capturedPieces.clear();
        invalidate();
        android.util.Log.d(TAG, "🔄 Captured pieces cleared");
    }
    
    /**
     * Get captured pieces count for debugging
     */
    public int getCapturedCount() {
        return capturedPieces.size();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set fixed height, dynamic width
        int desiredWidth = (int)(textPaint.getTextSize() * 10); // Space for ~10 pieces
        int desiredHeight = (int)(textPaint.getTextSize() * 1.5f);
        
        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        
        setMeasuredDimension(width, height);
    }
}