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
     * 🏆 Display game result with animation and celebration
     */
    public void showResult(GameResult result, String winner, String description) {
        this.currentResult = result;
        this.winnerName = winner;
        this.resultDescription = description;
        
        if (result == GameResult.NONE) {
            hideResult();
            return;
        }
        
        // 🎉 Enhanced colors based on result with celebration effect
        switch (result) {
            case WHITE_WINS:
                backgroundPaint.setColor(Color.parseColor("#4CAF50")); // Victory green
                borderPaint.setColor(Color.parseColor("#388E3C"));
                break;
            case BLACK_WINS:
                backgroundPaint.setColor(Color.parseColor("#2196F3")); // Victory blue
                borderPaint.setColor(Color.parseColor("#1976D2"));
                break;
            case DRAW:
                backgroundPaint.setColor(Color.parseColor("#FF9800")); // Draw orange
                borderPaint.setColor(Color.parseColor("#F57C00"));
                break;
            default:
                backgroundPaint.setColor(result.color);
                borderPaint.setColor(result.color);
        }
        
        backgroundPaint.setAlpha(200); // Slightly more opaque for celebration
        
        // Show with enhanced animation
        setVisibility(View.VISIBLE);
        animateShowWithCelebration();
        
        android.util.Log.d(TAG, String.format("🏆 Game result with celebration: %s - %s (%s)", 
                                            result.display, winner, description));
    }
    
    /**
     * 🎊 Enhanced animation with celebration effect
     */
    private void animateShowWithCelebration() {
        if (showAnimator != null) {
            showAnimator.cancel();
        }
        
        // Create a bouncy entrance effect
        showAnimator = ValueAnimator.ofFloat(0f, 1f);
        showAnimator.setDuration(800); // Longer duration for celebration
        showAnimator.setInterpolator(new android.view.animation.BounceInterpolator());
        showAnimator.addUpdateListener(animation -> {
            animationAlpha = (float) animation.getAnimatedValue();
            
            // Add scale effect during animation
            float scale = 0.5f + (animationAlpha * 0.5f);
            setScaleX(scale);
            setScaleY(scale);
            
            invalidate();
        });
        showAnimator.start();
        
        // Auto-hide after 8 seconds (longer for celebration)
        postDelayed(this::hideResult, 8000);
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
        
        // Draw result text with celebration formatting
        float centerX = width / 2f;
        float centerY = height / 2f;
        
        // 🎊 Enhanced celebration text based on result type
        String celebrationEmoji = getCelebrationEmoji();
        
        // Main result (1-0, 0-1, ½-½) with celebration emoji
        textPaint.setTextSize(32f);
        textPaint.setFakeBoldText(true);
        String mainResultText = celebrationEmoji + " " + currentResult.display + " " + celebrationEmoji;
        canvas.drawText(mainResultText, centerX, centerY - 25, textPaint);
        
        // Winner name with enhanced formatting
        if (!winnerName.isEmpty()) {
            textPaint.setTextSize(18f);
            textPaint.setFakeBoldText(false);
            String winnerText = currentResult == GameResult.DRAW ? 
                "Great game by both players!" : winnerName + " wins!";
            canvas.drawText(winnerText, centerX, centerY + 5, textPaint);
        }
        
        // Description (checkmate, resignation, etc.) with victory context
        if (!resultDescription.isEmpty()) {
            textPaint.setTextSize(14f);
            String enhancedDescription = getEnhancedDescription();
            canvas.drawText(enhancedDescription, centerX, centerY + 30, textPaint);
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
    
    /**
     * 🎊 Get celebration emoji based on result
     */
    private String getCelebrationEmoji() {
        switch (currentResult) {
            case WHITE_WINS:
                return "🏆";
            case BLACK_WINS:
                return "🎉";
            case DRAW:
                return "🤝";
            default:
                return "♟️";
        }
    }
    
    /**
     * 🎭 Get enhanced description with celebration context
     */
    private String getEnhancedDescription() {
        if (resultDescription.contains("checkmate")) {
            return "Magnificent checkmate! " + resultDescription;
        } else if (resultDescription.contains("resignation")) {
            return "Decisive victory " + resultDescription;
        } else if (resultDescription.contains("stalemate")) {
            return "Clever stalemate! " + resultDescription;
        } else if (resultDescription.contains("time")) {
            return "Victory on time! " + resultDescription;
        } else {
            return resultDescription;
        }
    }
    
    /**
     * 🎪 Show victory celebration with custom message
     */
    public void showVictoryCelebration(GameResult result, String celebrationMessage) {
        this.currentResult = result;
        this.winnerName = "";
        this.resultDescription = celebrationMessage;
        
        // Extra flashy colors for celebration
        switch (result) {
            case WHITE_WINS:
                backgroundPaint.setColor(Color.parseColor("#4CAF50"));
                borderPaint.setColor(Color.parseColor("#FFD700")); // Gold border
                break;
            case BLACK_WINS:
                backgroundPaint.setColor(Color.parseColor("#2196F3"));
                borderPaint.setColor(Color.parseColor("#FFD700")); // Gold border
                break;
            case DRAW:
                backgroundPaint.setColor(Color.parseColor("#FF9800"));
                borderPaint.setColor(Color.parseColor("#4CAF50")); // Green border
                break;
        }
        
        backgroundPaint.setAlpha(220);
        setVisibility(View.VISIBLE);
        animateShowWithCelebration();
        
        android.util.Log.d(TAG, String.format("🎪 Victory celebration: %s - %s", 
                                            result.display, celebrationMessage));
    }
}