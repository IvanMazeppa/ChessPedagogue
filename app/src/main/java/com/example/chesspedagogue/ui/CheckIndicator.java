package com.example.chesspedagogue.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

/**
 * 3D Check Indicator for Samsung S23 Ultra Chess Game
 * Creates a dramatic 3D "+" sign that appears when king is in check
 */
public class CheckIndicator extends View {
    private static final String TAG = "CheckIndicator";
    
    // Visual constants optimized for S23 Ultra
    private static final int INDICATOR_SIZE_DP = 32;
    private static final float SHADOW_OFFSET = 8f;
    private static final float DEPTH_LAYERS = 6f;
    
    private Paint mainPaint;
    private Paint shadowPaint;
    private Paint glowPaint;
    private Path crossPath;
    private RectF bounds;
    
    // Animation properties
    private float animationProgress = 0f;
    private float rotationAngle = 0f;
    private float pulseScale = 1f;
    private boolean isVisible = false;
    
    // Colors for dramatic effect
    private int checkColor = Color.parseColor("#FF4444"); // Bright red
    private int shadowColor = Color.parseColor("#88000000"); // Dark shadow
    private int glowColor = Color.parseColor("#FFFF4444"); // Red glow
    
    public CheckIndicator(Context context) {
        super(context);
        init();
    }
    
    public CheckIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    /**
     * Initialize the 3D check indicator
     */
    private void init() {
        Log.d(TAG, "🚨 Initializing 3D check indicator");
        
        // Initialize paints
        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainPaint.setStyle(Paint.Style.FILL);
        mainPaint.setStrokeWidth(4f);
        
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(shadowColor);
        
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(8f);
        glowPaint.setColor(glowColor);
        
        // Initialize paths
        crossPath = new Path();
        bounds = new RectF();
        
        // Set initial visibility
        setVisibility(INVISIBLE);
        setAlpha(0f);
        
        Log.d(TAG, "✅ 3D check indicator initialized");
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Update bounds and create 3D cross path
        bounds.set(0, 0, w, h);
        createCrossPath();
        updateGradients();
    }
    
    /**
     * Create the 3D cross path with depth
     */
    private void createCrossPath() {
        crossPath.reset();
        
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        float size = Math.min(bounds.width(), bounds.height()) * 0.4f;
        float thickness = size * 0.3f;
        
        // Create cross shape with rounded ends
        // Vertical bar
        RectF verticalBar = new RectF(
            centerX - thickness / 2f,
            centerY - size / 2f,
            centerX + thickness / 2f,
            centerY + size / 2f
        );
        
        // Horizontal bar
        RectF horizontalBar = new RectF(
            centerX - size / 2f,
            centerY - thickness / 2f,
            centerX + size / 2f,
            centerY + thickness / 2f
        );
        
        // Add rounded rectangles to path
        crossPath.addRoundRect(verticalBar, thickness / 4f, thickness / 4f, Path.Direction.CW);
        crossPath.addRoundRect(horizontalBar, thickness / 4f, thickness / 4f, Path.Direction.CW);
    }
    
    /**
     * Update gradients for 3D effect
     */
    private void updateGradients() {
        // Create 3D gradient for main cross
        LinearGradient mainGradient = new LinearGradient(
            0, 0, bounds.width(), bounds.height(),
            new int[]{
                Color.parseColor("#FF6666"), // Bright red
                checkColor,                   // Main red
                Color.parseColor("#CC2222")   // Dark red
            },
            new float[]{0f, 0.5f, 1f},
            Shader.TileMode.CLAMP
        );
        
        mainPaint.setShader(mainGradient);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!isVisible || animationProgress <= 0f) {
            return;
        }
        
        canvas.save();
        
        // Apply rotation and scale transformations
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        
        canvas.translate(centerX, centerY);
        canvas.rotate(rotationAngle);
        canvas.scale(pulseScale * animationProgress, pulseScale * animationProgress);
        canvas.translate(-centerX, -centerY);
        
        // Draw 3D layers for depth effect
        drawDepthLayers(canvas);
        
        // Draw main cross with glow
        drawMainCross(canvas);
        
        canvas.restore();
    }
    
    /**
     * Draw depth layers for 3D effect
     */
    private void drawDepthLayers(Canvas canvas) {
        for (int i = 0; i < DEPTH_LAYERS; i++) {
            float layerOffset = (i + 1) * (SHADOW_OFFSET / DEPTH_LAYERS);
            float layerAlpha = 1f - (i / DEPTH_LAYERS) * 0.7f;
            
            canvas.save();
            canvas.translate(layerOffset, layerOffset);
            
            shadowPaint.setAlpha((int) (layerAlpha * 180 * animationProgress));
            canvas.drawPath(crossPath, shadowPaint);
            
            canvas.restore();
        }
    }
    
    /**
     * Draw the main cross with glow effect
     */
    private void drawMainCross(Canvas canvas) {
        // Draw outer glow
        float glowAlpha = (float) (Math.sin(System.currentTimeMillis() / 200.0) * 0.3 + 0.7);
        glowPaint.setAlpha((int) (glowAlpha * 150 * animationProgress));
        canvas.drawPath(crossPath, glowPaint);
        
        // Draw main cross
        mainPaint.setAlpha((int) (255 * animationProgress));
        canvas.drawPath(crossPath, mainPaint);
    }
    
    /**
     * Show check indicator with dramatic animation
     */
    public void showCheckIndicator() {
        Log.d(TAG, "🚨 Showing check indicator with 3D animation");
        
        isVisible = true;
        setVisibility(VISIBLE);
        
        // Create dramatic entrance animation
        AnimatorSet showAnimSet = new AnimatorSet();
        
        // Scale and rotation entrance
        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 1f);
        scaleAnim.setDuration(600);
        scaleAnim.setInterpolator(new OvershootInterpolator(2.0f));
        
        // Rotation for dramatic effect
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(this, "rotationAngle", -180f, 0f);
        rotationAnim.setDuration(600);
        rotationAnim.setInterpolator(new FastOutSlowInInterpolator());
        
        // Alpha fade in
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        alphaAnim.setDuration(400);
        
        // Start pulsing animation
        startPulseAnimation();
        
        showAnimSet.playTogether(scaleAnim, rotationAnim, alphaAnim);
        showAnimSet.start();
        
        // Trigger physics animation on associated king piece
        triggerKingShakeEffect();
    }
    
    /**
     * Hide check indicator
     */
    public void hideCheckIndicator() {
        Log.d(TAG, "✅ Hiding check indicator");
        
        // Stop pulsing
        stopPulseAnimation();
        
        // Fade out animation
        animate()
            .alpha(0f)
            .scaleX(0.3f)
            .scaleY(0.3f)
            .setDuration(300)
            .setInterpolator(new FastOutSlowInInterpolator())
            .withEndAction(() -> {
                setVisibility(INVISIBLE);
                isVisible = false;
                animationProgress = 0f;
                rotationAngle = 0f;
                pulseScale = 1f;
            })
            .start();
    }
    
    /**
     * Start continuous pulse animation while in check
     */
    private void startPulseAnimation() {
        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.2f, 1f);
        pulseAnimator.setDuration(800);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        
        pulseAnimator.addUpdateListener(animation -> {
            pulseScale = (float) animation.getAnimatedValue();
            invalidate();
        });
        
        pulseAnimator.start();
        setTag(pulseAnimator); // Store for later stopping
    }
    
    /**
     * Stop pulse animation
     */
    private void stopPulseAnimation() {
        Object tag = getTag();
        if (tag instanceof ValueAnimator) {
            ((ValueAnimator) tag).cancel();
        }
    }
    
    /**
     * Trigger king shake effect (needs reference to king view)
     */
    private void triggerKingShakeEffect() {
        // This would need a reference to the king view
        // For now, just log the trigger
        Log.d(TAG, "👑 Triggering king shake effect");
    }
    
    /**
     * Position check indicator next to specified view (king)
     */
    public void positionNextToKing(View kingView, ViewGroup parentContainer) {
        if (kingView == null || parentContainer == null) {
            Log.w(TAG, "Cannot position check indicator - missing king or container");
            return;
        }
        
        // Calculate position next to king
        float kingX = kingView.getX();
        float kingY = kingView.getY();
        float kingWidth = kingView.getWidth();
        
        // Position to the right of the king
        float indicatorX = kingX + kingWidth + 8f; // 8dp offset
        float indicatorY = kingY;
        
        // Set size and position
        int sizePixels = (int) (INDICATOR_SIZE_DP * getContext().getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(sizePixels, sizePixels);
        setLayoutParams(params);
        
        setX(indicatorX);
        setY(indicatorY);
        
        // Add to container if not already added
        if (getParent() == null) {
            parentContainer.addView(this);
        }
        
        Log.d(TAG, "📍 Check indicator positioned next to king at (" + indicatorX + ", " + indicatorY + ")");
    }
    
    /**
     * Update animation progress (for custom animations)
     */
    public void setAnimationProgress(float progress) {
        this.animationProgress = Math.max(0f, Math.min(1f, progress));
        invalidate();
    }
    
    public float getAnimationProgress() {
        return animationProgress;
    }
    
    /**
     * Update rotation angle (for custom animations)
     */
    public void setRotationAngle(float angle) {
        this.rotationAngle = angle;
        invalidate();
    }
    
    public float getRotationAngle() {
        return rotationAngle;
    }
}