package com.example.chesspedagogue;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Sleek vertical evaluation bar with smooth animations
 * Perfect companion to your chess board!
 */
public class EvaluationBarView extends View {
    private static final String TAG = "EvaluationBarView";

    // Evaluation state
    private float currentEvaluation = 0.0f; // Current displayed evaluation
    private float targetEvaluation = 0.0f;  // Target evaluation to animate to
    private boolean isMatePosition = false;
    private int mateInMoves = 0;
    private boolean isPlayerWhite = true;

    // Animation
    private ValueAnimator evaluationAnimator;
    private boolean isAnimating = false;

    // Visual state
    private Paint whitePaint;
    private Paint blackPaint;
    private Paint borderPaint;

    // Elegant colors that blend with your modern blue/purple/teal theme
    private static final int WHITE_COLOR = Color.parseColor("#F5F5DC"); // Cream
    private static final int BLACK_COLOR = Color.parseColor("#2E4A6B");  // Deep blue-teal to match theme
    private static final int BORDER_COLOR = Color.parseColor("#506080"); // Teal-blue border

    public EvaluationBarView(Context context) {
        super(context);
        init();
    }

    public EvaluationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EvaluationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.d(TAG, "🎯 Initializing smooth evaluation bar");

        // Create elegant paints
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(WHITE_COLOR);
        whitePaint.setStyle(Paint.Style.FILL);

        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setColor(BLACK_COLOR);
        blackPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f); // Thinner border for sleek look

        Log.d(TAG, "✅ Smooth evaluation bar initialized");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        // Create the bar rectangle with minimal padding
        RectF barRect = new RectF(1, 1, width - 1, height - 1);

        if (isMatePosition) {
            // Mate: solid color for winning side
            Paint matePaint = (mateInMoves > 0) ? whitePaint : blackPaint;
            canvas.drawRect(barRect, matePaint);
        } else {
            // Use current (animated) evaluation for smooth transitions
            float displayEval = currentEvaluation;

            // Convert to display ratio - CORRECTED LOGIC
            float clampedEval = Math.max(-5f, Math.min(5f, displayEval));

            // Calculate ratio where:
            // - Positive evaluation (white advantage) = more white (top section larger)
            // - Negative evaluation (black advantage) = more black (bottom section larger)
            float ratio = (clampedEval + 5f) / 10f; // Maps -5..+5 to 0..1
            ratio = Math.max(0.02f, Math.min(0.98f, ratio)); // Ensure visibility

            // 🔄 PERSPECTIVE FIX: Adjust display based on player orientation for better UX
            // When playing as Black, evaluation bar should match board perspective:
            // - Player's advantage should appear near their pieces
            // - Black player expects to see their advantage at bottom (where their pieces are)
            
            float whiteSectionHeight = ratio * barRect.height();
            float splitPoint;
            
            if (isPlayerWhite) {
                // White player: Normal display (white advantage at top)
                splitPoint = barRect.top + whiteSectionHeight;
            } else {
                // Black player: Flip display so player's advantage appears near their pieces
                splitPoint = barRect.top + (barRect.height() - whiteSectionHeight);
            }

            // Draw white section (top) - size determined by ratio
            if (whiteSectionHeight > 1) {
                RectF whiteRect = new RectF(barRect.left, barRect.top, barRect.right, splitPoint);
                canvas.drawRect(whiteRect, whitePaint);
            }

            // Draw black section (bottom) - gets remaining space
            if (splitPoint < barRect.bottom - 1) {
                RectF blackRect = new RectF(barRect.left, splitPoint, barRect.right, barRect.bottom);
                canvas.drawRect(blackRect, blackPaint);
            }
        }

        // Draw subtle border
        canvas.drawRect(barRect, borderPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Lock in the width - never changes, prevents layout shifts
        int width = (int) (9 * getResources().getDisplayMetrics().density); // Fixed 9dp (half width)

        // Use the height exactly as provided by ConstraintLayout - no modifications
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // This prevents any measurement changes that could cause board shifts
        setMeasuredDimension(width, height);
    }

    /**
     * ✨ Smooth evaluation update - waits for evaluation to stabilize
     */
    public void setEvaluation(float evaluation) {
        Log.d(TAG, "🎯 Setting target evaluation: " + evaluation + " (current: " + currentEvaluation + ")");

        this.targetEvaluation = evaluation;
        this.isMatePosition = false;
        this.mateInMoves = 0;

        // Start smooth animation to new evaluation
        animateToEvaluation(evaluation);
    }

    /**
     * ✨ Smooth mate evaluation update
     */
    public void setMateEvaluation(int mateInMoves) {
        Log.d(TAG, "🏆 Setting mate: M" + mateInMoves);

        // Cancel any ongoing animation
        if (evaluationAnimator != null && evaluationAnimator.isRunning()) {
            evaluationAnimator.cancel();
        }

        this.isMatePosition = true;
        this.mateInMoves = mateInMoves;
        this.currentEvaluation = 0f;
        this.targetEvaluation = 0f;

        invalidate();
    }

    /**
     * 🎨 Smooth animation to new evaluation value
     */
    private void animateToEvaluation(float newEvaluation) {
        // Don't animate tiny changes (reduces flicker)
        if (Math.abs(newEvaluation - currentEvaluation) < 0.1f) {
            currentEvaluation = newEvaluation;
            invalidate();
            return;
        }

        // Cancel any existing animation
        if (evaluationAnimator != null && evaluationAnimator.isRunning()) {
            evaluationAnimator.cancel();
        }

        // Create smooth animation
        evaluationAnimator = ValueAnimator.ofFloat(currentEvaluation, newEvaluation);
        evaluationAnimator.setDuration(600); // 600ms for smooth, elegant movement
        evaluationAnimator.setInterpolator(new DecelerateInterpolator()); // Smooth deceleration

        evaluationAnimator.addUpdateListener(animation -> {
            currentEvaluation = (float) animation.getAnimatedValue();
            invalidate(); // Redraw with new position
        });

        evaluationAnimator.start();
        isAnimating = true;

        Log.d(TAG, "🎬 Started smooth animation: " + currentEvaluation + " → " + newEvaluation);
    }

    /**
     * 🔄 PERSPECTIVE FIX: Set player color to adjust evaluation bar orientation
     * This ensures the evaluation bar matches the board perspective for better UX
     */
    public void setPlayerColor(boolean isWhite) {
        Log.d(TAG, "🎨 Player color set: " + (isWhite ? "White" : "Black") + " - adjusting perspective");
        
        if (this.isPlayerWhite != isWhite) {
            this.isPlayerWhite = isWhite;
            invalidate(); // Redraw with new perspective
        }
    }

    /**
     * Reset to starting position with smooth animation
     */
    public void reset() {
        Log.d(TAG, "🔄 Resetting to starting position");
        setEvaluation(0.0f);
    }

    /**
     * Check if currently animating (useful for preventing rapid updates)
     */
    public boolean isAnimating() {
        return isAnimating && evaluationAnimator != null && evaluationAnimator.isRunning();
    }

    /**
     * Cancel any ongoing animation
     */
    public void cancelAnimation() {
        if (evaluationAnimator != null && evaluationAnimator.isRunning()) {
            evaluationAnimator.cancel();
            isAnimating = false;
        }
    }
}