package com.example.chesspedagogue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;

/**
 * 🏆 Game Result Display
 * Shows win/lose/draw with animated display
 */
public class GameResultView extends View {
    private static final String TAG = "GameResultView";
    
    public enum GameResult {
        NONE("", Color.TRANSPARENT),
        WHITE_WINS("1-0", Color.GREEN),
        BLACK_WINS("0-1", Color.RED),
        DRAW("½-½", Color.YELLOW);
        
        public final String display;
        public final int color;
        
        GameResult(String display, int color) {
            this.display = display;
            this.color = color;
        }
    }
    
    private GameResult currentResult = GameResult.NONE;
    private String winnerName = "";
    private String resultDescription = "";
    
    private Paint textPaint;
    private Paint backgroundPaint;
    private Paint borderPaint;
    private RectF backgroundRect;
    
    private float animationAlpha = 0f;
    private ValueAnimator showAnimator;
    
    public GameResultView(Context context) {
        super(context);
        init();
    }
    
    public GameResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(28f);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setAlpha(180);
        
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);
        borderPaint.setColor(Color.WHITE);
        
        backgroundRect = new RectF();
        
        setVisibility(View.GONE);
    }
    
    /**
     * 🏆 Display game result with animation
     */
    public void showResult(GameResult result, String winner, String description) {
        this.currentResult = result;
        this.winnerName = winner;
        this.resultDescription = description;
        
        if (result == GameResult.NONE) {
            hideResult();
            return;
        }
        
        // Update colors based on result
        backgroundPaint.setColor(result.color);
        backgroundPaint.setAlpha(180);
        borderPaint.setColor(result.color);
        
        // Show with animation
        setVisibility(View.VISIBLE);
        animateShow();
        
        android.util.Log.d(TAG, String.format("🏆 Game result: %s - %s (%s)", 
                                            result.display, winner, description));
    }
    
    /**
     * Hide result display
     */
    public void hideResult() {
        currentResult = GameResult.NONE;
        setVisibility(View.GONE);
        android.util.Log.d(TAG, "🔄 Game result hidden");
    }
    
    private void animateShow() {
        if (showAnimator != null) {
            showAnimator.cancel();
        }
        
        showAnimator = ValueAnimator.ofFloat(0f, 1f);
        showAnimator.setDuration(500);
        showAnimator.addUpdateListener(animation -> {
            animationAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
        showAnimator.start();
        
        // Auto-hide after 10 seconds
        postDelayed(this::hideResult, 10000);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (currentResult == GameResult.NONE) {
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw background with animation alpha
        backgroundPaint.setAlpha((int)(180 * animationAlpha));
        borderPaint.setAlpha((int)(255 * animationAlpha));
        textPaint.setAlpha((int)(255 * animationAlpha));
        
        // Center background rectangle
        float rectWidth = width * 0.8f;
        float rectHeight = height * 0.6f;
        float left = (width - rectWidth) / 2f;
        float top = (height - rectHeight) / 2f;
        
        backgroundRect.set(left, top, left + rectWidth, top + rectHeight);
        
        // Draw background and border
        canvas.drawRoundRect(backgroundRect, 12f, 12f, backgroundPaint);
        canvas.drawRoundRect(backgroundRect, 12f, 12f, borderPaint);
        
        // Draw result text
        float centerX = width / 2f;
        float centerY = height / 2f;
        
        // Main result (1-0, 0-1, ½-½)
        textPaint.setTextSize(36f);
        canvas.drawText(currentResult.display, centerX, centerY - 20, textPaint);
        
        // Winner name
        if (!winnerName.isEmpty()) {
            textPaint.setTextSize(20f);
            canvas.drawText(winnerName + " wins!", centerX, centerY + 15, textPaint);
        }
        
        // Description (checkmate, resignation, etc.)
        if (!resultDescription.isEmpty()) {
            textPaint.setTextSize(16f);
            canvas.drawText(resultDescription, centerX, centerY + 40, textPaint);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Fixed size for result display
        int desiredWidth = 300;
        int desiredHeight = 120;
        
        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        
        setMeasuredDimension(width, height);
    }
    
    /**
     * Quick result display methods
     */
    public void showWhiteWins(String winnerName, String method) {
        showResult(GameResult.WHITE_WINS, winnerName, method);
    }
    
    public void showBlackWins(String winnerName, String method) {
        showResult(GameResult.BLACK_WINS, winnerName, method);
    }
    
    public void showDraw(String reason) {
        showResult(GameResult.DRAW, "", reason);
    }
}