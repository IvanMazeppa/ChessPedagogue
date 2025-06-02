package com.example.chesspedagogue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 🎨 Unicode Chess Piece Drawable
 * 
 * Creates drawable chess pieces from Unicode symbols with proper styling
 * and size adaptation for consistent board appearance.
 */
public class UnicodeChessPieceDrawable extends Drawable {
    private final Paint textPaint;
    private final Paint outlinePaint;
    private final String symbol;
    private final boolean isWhite;
    private final Context context;
    
    // Text size will be dynamically calculated based on bounds
    private float textSize;
    private Rect textBounds = new Rect();
    
    public UnicodeChessPieceDrawable(Context context, String unicodeSymbol, boolean isWhitePiece) {
        this.context = context;
        this.symbol = unicodeSymbol;
        this.isWhite = isWhitePiece;
        
        // Initialize paint for the chess piece text
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        // Initialize outline paint for better visibility
        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setTextAlign(Paint.Align.CENTER);
        outlinePaint.setStrokeWidth(dpToPx(1.5f));
        
        updateColors();
    }
    
    /**
     * Update colors based on piece color
     */
    private void updateColors() {
        if (isWhite) {
            // White pieces: white fill with dark outline
            textPaint.setColor(Color.WHITE);
            textPaint.setStyle(Paint.Style.FILL);
            outlinePaint.setColor(Color.BLACK);
        } else {
            // Black pieces: black fill
            textPaint.setColor(Color.BLACK);
            textPaint.setStyle(Paint.Style.FILL);
            outlinePaint.setColor(Color.WHITE);
        }
    }
    
    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) return;
        
        // Calculate optimal text size based on bounds
        calculateTextSize(bounds);
        
        // Calculate vertical center position for text
        textPaint.getTextBounds(symbol, 0, symbol.length(), textBounds);
        float textHeight = textBounds.height();
        float y = bounds.centerY() + (textHeight / 2) - textBounds.bottom;
        
        // Draw outline first (for white pieces)
        if (isWhite) {
            outlinePaint.setTextSize(textSize);
            canvas.drawText(symbol, bounds.centerX(), y, outlinePaint);
        }
        
        // Draw the main text
        textPaint.setTextSize(textSize);
        canvas.drawText(symbol, bounds.centerX(), y, textPaint);
    }
    
    /**
     * Calculate optimal text size to fit within bounds and match vector pieces
     */
    private void calculateTextSize(Rect bounds) {
        // Make Unicode pieces match the size of existing vector pieces
        float maxWidth = bounds.width() * 0.90f; // Use more space for better visibility
        float maxHeight = bounds.height() * 0.90f;
        
        // Start with a larger base size for better readability
        float testSize = Math.min(bounds.width(), bounds.height()) * 0.85f;
        textPaint.setTextSize(testSize);
        
        // Measure text and adjust size if needed
        textPaint.getTextBounds(symbol, 0, symbol.length(), textBounds);
        
        // Scale to fit, but prioritize readability
        if (textBounds.width() > maxWidth) {
            testSize = testSize * (maxWidth / textBounds.width());
        }
        if (textBounds.height() > maxHeight) {
            testSize = testSize * (maxHeight / textBounds.height());
        }
        
        // Ensure minimum readable size
        float minSize = Math.min(bounds.width(), bounds.height()) * 0.6f;
        textSize = Math.max(testSize, minSize);
    }
    
    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
        outlinePaint.setAlpha(alpha);
        invalidateSelf();
    }
    
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        textPaint.setColorFilter(colorFilter);
        outlinePaint.setColorFilter(colorFilter);
        invalidateSelf();
    }
    
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    
    @Override
    public int getIntrinsicWidth() {
        return dpToPx(45); // Match the size of vector drawables
    }
    
    @Override
    public int getIntrinsicHeight() {
        return dpToPx(45); // Match the size of vector drawables
    }
    
    /**
     * Convert dp to pixels
     */
    private int dpToPx(float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
    
    /**
     * Get the Unicode symbol this drawable represents
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * Check if this represents a white piece
     */
    public boolean isWhitePiece() {
        return isWhite;
    }
}