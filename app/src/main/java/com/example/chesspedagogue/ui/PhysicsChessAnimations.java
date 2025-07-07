package com.example.chesspedagogue.ui;

import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

/**
 * Physics-Based Chess Animation System for Samsung S23 Ultra
 * Implements realistic piece movement, captures, and physics interactions
 */
public class PhysicsChessAnimations {
    private static final String TAG = "PhysicsChessAnimations";
    
    // Animation constants optimized for 120Hz S23 Ultra display
    private static final float FLING_FRICTION = 1.2f;
    private static final float SPRING_DAMPENING = 0.7f;
    private static final float SPRING_STIFFNESS = 800f;
    private static final int SMOOTH_MOVE_DURATION = 400;
    private static final int CAPTURE_FLING_DURATION = 600;
    
    /**
     * Animate piece movement with smooth Bezier curve
     */
    public static void animatePieceMovement(View pieceView, float fromX, float fromY, 
                                          float toX, float toY, Runnable onComplete) {
        if (pieceView == null) {
            Log.w(TAG, "Cannot animate null piece view");
            return;
        }
        
        Log.d(TAG, "🎭 Animating piece movement with physics curve");
        
        // Create curved path for natural movement
        Path movePath = createBezierMovePath(fromX, fromY, toX, toY);
        PathMeasure pathMeasure = new PathMeasure(movePath, false);
        float[] position = new float[2];
        
        // Animate along the curved path
        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0f, 1f);
        pathAnimator.setDuration(SMOOTH_MOVE_DURATION);
        pathAnimator.setInterpolator(new FastOutSlowInInterpolator());
        
        pathAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            pathMeasure.getPosTan(progress * pathMeasure.getLength(), position, null);
            
            pieceView.setX(position[0]);
            pieceView.setY(position[1]);
            
            // Add subtle rotation during movement
            float rotation = (progress - 0.5f) * 10f; // Max 5 degrees each way
            pieceView.setRotation(rotation);
        });
        
        pathAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Restore rotation and trigger completion
                pieceView.setRotation(0f);
                if (onComplete != null) {
                    onComplete.run();
                }
                Log.d(TAG, "✅ Piece movement animation completed");
            }
        });
        
        pathAnimator.start();
    }
    
    /**
     * Animate piece capture with realistic physics fling
     */
    public static void animatePieceCapture(View capturedPiece, View boardView, 
                                         CapturedPiecesManager capturedManager, 
                                         boolean isWhitePiece, Runnable onComplete) {
        if (capturedPiece == null || boardView == null) {
            Log.w(TAG, "Cannot animate capture - missing views");
            return;
        }
        
        Log.d(TAG, "💥 Animating piece capture with physics fling");
        
        // Calculate fling direction based on piece color
        float flingDirectionX = (Math.random() > 0.5) ? 1200f : -1200f; // Random left/right
        float flingDirectionY = isWhitePiece ? -1500f : 1500f; // Up for white, down for black
        
        // Add dramatic scale and rotation during fling
        SpringAnimation scaleXAnim = new SpringAnimation(capturedPiece, DynamicAnimation.SCALE_X, 0.3f);
        SpringAnimation scaleYAnim = new SpringAnimation(capturedPiece, DynamicAnimation.SCALE_Y, 0.3f);
        // DISABLED: Rotation causing board spinning bug
        // SpringAnimation rotationAnim = new SpringAnimation(capturedPiece, DynamicAnimation.ROTATION, 360f * 2);
        
        // Configure spring physics
        configureSpring(scaleXAnim, SPRING_STIFFNESS, SPRING_DAMPENING);
        configureSpring(scaleYAnim, SPRING_STIFFNESS, SPRING_DAMPENING);
        // configureSpring(rotationAnim, SPRING_STIFFNESS * 0.5f, SPRING_DAMPENING);
        
        // Fling animation for realistic physics
        FlingAnimation flingX = new FlingAnimation(capturedPiece, DynamicAnimation.X)
            .setStartVelocity(flingDirectionX)
            .setFriction(FLING_FRICTION);
            
        FlingAnimation flingY = new FlingAnimation(capturedPiece, DynamicAnimation.Y)
            .setStartVelocity(flingDirectionY)
            .setFriction(FLING_FRICTION);
        
        // When fling completes, move to captured pieces area
        flingX.addEndListener((animation, canceled, value, velocity) -> {
            if (!canceled) {
                // Hide the flung piece
                capturedPiece.setVisibility(View.INVISIBLE);
                
                // Add to captured pieces display
                if (capturedManager != null) {
                    capturedManager.addCapturedPiece(capturedPiece, isWhitePiece);
                }
                
                if (onComplete != null) {
                    onComplete.run();
                }
                
                Log.d(TAG, "✅ Piece capture fling completed");
            }
        });
        
        // Start all animations simultaneously (rotation disabled to fix board spinning)
        scaleXAnim.start();
        scaleYAnim.start();
        // rotationAnim.start(); // DISABLED
        flingX.start();
        flingY.start();
        
        // Trigger capture flash effect
        WorkingGlassEffects.applyCaptureFlash(
            boardView, 
            capturedPiece.getX() + capturedPiece.getWidth() / 2f, 
            capturedPiece.getY() + capturedPiece.getHeight() / 2f, 
            new float[]{1.0f, 0.4f, 0.1f} // Orange flash
        );
    }
    
    /**
     * Animate piece placement with satisfying bounce
     */
    public static void animatePiecePlacement(View pieceView, float targetX, float targetY) {
        if (pieceView == null) return;
        
        Log.d(TAG, "🎯 Animating piece placement with bounce");
        
        // Start piece slightly above target and scaled down
        pieceView.setX(targetX);
        pieceView.setY(targetY - 50f);
        pieceView.setScaleX(0.8f);
        pieceView.setScaleY(0.8f);
        pieceView.setAlpha(0.8f);
        
        // Spring to final position
        SpringAnimation posYAnim = new SpringAnimation(pieceView, DynamicAnimation.Y, targetY);
        SpringAnimation scaleXAnim = new SpringAnimation(pieceView, DynamicAnimation.SCALE_X, 1.0f);
        SpringAnimation scaleYAnim = new SpringAnimation(pieceView, DynamicAnimation.SCALE_Y, 1.0f);
        SpringAnimation alphaAnim = new SpringAnimation(pieceView, DynamicAnimation.ALPHA, 1.0f);
        
        // Configure bouncy springs
        configureSpring(posYAnim, SPRING_STIFFNESS * 1.2f, SPRING_DAMPENING * 0.6f);
        configureSpring(scaleXAnim, SPRING_STIFFNESS, SPRING_DAMPENING);
        configureSpring(scaleYAnim, SPRING_STIFFNESS, SPRING_DAMPENING);
        configureSpring(alphaAnim, SPRING_STIFFNESS, SPRING_DAMPENING);
        
        // Start all animations
        posYAnim.start();
        scaleXAnim.start();
        scaleYAnim.start();
        alphaAnim.start();
    }
    
    /**
     * Create curved Bezier path for piece movement
     */
    private static Path createBezierMovePath(float fromX, float fromY, float toX, float toY) {
        Path path = new Path();
        path.moveTo(fromX, fromY);
        
        // Calculate control points for natural arc
        float midX = (fromX + toX) / 2f;
        float midY = (fromY + toY) / 2f;
        
        // Add arc height based on distance
        float distance = (float) Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2));
        float arcHeight = Math.min(distance * 0.2f, 100f);
        
        // Create quadratic Bezier curve
        path.quadTo(midX, midY - arcHeight, toX, toY);
        
        return path;
    }
    
    /**
     * Configure spring animation properties
     */
    private static void configureSpring(SpringAnimation animation, float stiffness, float dampingRatio) {
        animation.getSpring()
            .setStiffness(stiffness)
            .setDampingRatio(dampingRatio);
    }
    
    /**
     * Animate king shake when in check
     */
    public static void animateKingInCheck(View kingView) {
        if (kingView == null) return;
        
        Log.d(TAG, "👑 Animating king check shake");
        
        // Create shake animation
        float originalX = kingView.getX();
        ValueAnimator shakeAnimator = ValueAnimator.ofFloat(-10f, 10f, -8f, 8f, -5f, 5f, 0f);
        shakeAnimator.setDuration(500);
        
        shakeAnimator.addUpdateListener(animation -> {
            float offsetX = (float) animation.getAnimatedValue();
            kingView.setX(originalX + offsetX);
        });
        
        shakeAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                kingView.setX(originalX); // Restore original position
            }
        });
        
        // Add subtle red tint during shake
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(0x00FF0000, 0x80FF0000, 0x00FF0000);
        colorAnimator.setDuration(500);
        colorAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            kingView.setBackgroundColor(color);
        });
        
        shakeAnimator.start();
        colorAnimator.start();
    }
    
    /**
     * Animate piece hover effect during selection
     */
    public static void animatePieceHover(View pieceView, boolean isHovering) {
        if (pieceView == null) return;
        
        float targetScale = isHovering ? 1.1f : 1.0f;
        float targetElevation = isHovering ? 12f : 0f;
        
        SpringAnimation scaleXAnim = new SpringAnimation(pieceView, DynamicAnimation.SCALE_X, targetScale);
        SpringAnimation scaleYAnim = new SpringAnimation(pieceView, DynamicAnimation.SCALE_Y, targetScale);
        
        configureSpring(scaleXAnim, SPRING_STIFFNESS * 1.5f, SPRING_DAMPENING);
        configureSpring(scaleYAnim, SPRING_STIFFNESS * 1.5f, SPRING_DAMPENING);
        
        scaleXAnim.start();
        scaleYAnim.start();
        
        // Animate elevation for shadow effect
        pieceView.animate()
            .translationZ(targetElevation)
            .setDuration(200)
            .setInterpolator(new FastOutSlowInInterpolator())
            .start();
    }
    
    /**
     * Create piece promotion celebration animation
     */
    public static void animatePromotion(View promotedPiece, Runnable onComplete) {
        if (promotedPiece == null) return;
        
        Log.d(TAG, "👑 Animating piece promotion celebration");
        
        // Scale up dramatically then back to normal
        ValueAnimator celebrationAnim = ValueAnimator.ofFloat(1.0f, 1.5f, 1.0f);
        celebrationAnim.setDuration(800);
        celebrationAnim.setInterpolator(new android.view.animation.OvershootInterpolator(2.0f));
        
        celebrationAnim.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            promotedPiece.setScaleX(scale);
            promotedPiece.setScaleY(scale);
        });
        
        celebrationAnim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        
        // Add rotation during celebration
        promotedPiece.animate()
            .rotation(360f)
            .setDuration(800)
            .setInterpolator(new FastOutSlowInInterpolator())
            .start();
        
        celebrationAnim.start();
        
        // Trigger celebration flash
        WorkingGlassEffects.applyCaptureFlash(
            promotedPiece,
            promotedPiece.getWidth() / 2f,
            promotedPiece.getHeight() / 2f,
            new float[]{1.0f, 0.8f, 0.2f} // Golden flash
        );
    }
}