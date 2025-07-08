package com.example.chesspedagogue.ui.effects;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * RoboticPieceAnimator - Creates robotic/mechanical movement animations for chess pieces
 * 
 * Features:
 * - Stepped movement with mechanical pauses (recalibration effect)
 * - Motor spin-up and brake-down easing curves
 * - Overshoot and settling movements
 * - Rotation/scanning animations
 * - Piece-specific animation profiles (Rook rolls, Knight hops, etc.)
 * - Mechanical jitter and vibration effects
 * 
 * Based on INTERFACE_REBUILD_NEW_FEATURES_GUIDE_II.md Feature #4 specification
 */
public class RoboticPieceAnimator {
    
    private static final String TAG = "RoboticPieceAnimator";
    
    // Animation timing constants (in milliseconds)
    private static final int DEFAULT_ANIMATION_DURATION = 600;
    private static final int PAUSE_DURATION = 150; // Mechanical recalibration pause
    private static final int SETTLING_DURATION = 200; // Final settling animation
    private static final int ROTATION_DURATION = 100; // Pre-move scanning rotation
    
    // Movement parameters
    private static final float OVERSHOOT_DISTANCE = 8f; // Pixels beyond target
    private static final float ROTATION_ANGLE = 15f; // Degrees for scanning effect
    private static final float JITTER_AMPLITUDE = 2f; // Pixels for mechanical settling
    private static final int JITTER_CYCLES = 3; // Number of settling vibrations
    
    // Animation phases
    private static final float PHASE_1_END = 0.35f; // First movement segment
    private static final float PAUSE_START = 0.35f; // Pause start
    private static final float PAUSE_END = 0.45f; // Pause end
    private static final float OVERSHOOT_POINT = 0.85f; // When to overshoot
    
    private final Random random = new Random();
    
    // Animation callback interface
    public interface RoboticAnimationCallback {
        void onAnimationUpdate(float x, float y, float rotation, float alpha);
        void onAnimationComplete();
        void onAnimationStart();
        void onMechanicalPause(); // Triggered during recalibration pause
        void onSettlingStart(); // Triggered when final settling begins
    }
    
    // Piece movement profiles for different chess pieces
    public enum PieceProfile {
        ROOK_ROLL,      // Smooth rolling with mechanical rumble
        KNIGHT_HOP,     // Angular robotic leap with L-shaped path
        BISHOP_GLIDE,   // Diagonal glide with servo precision
        QUEEN_POWER,    // Powerful movement with dramatic pauses
        KING_CAUTIOUS,  // Slow, deliberate movement with extra scanning
        PAWN_MARCH      // Simple forward march with mechanical steps
    }
    
    private RoboticAnimationCallback callback;
    private ValueAnimator currentAnimator;
    private boolean isAnimating = false;
    
    // Animation state tracking for lambda expressions
    private boolean pauseTriggered = false;
    private boolean settlingTriggered = false;
    
    public RoboticPieceAnimator() {
        Log.d(TAG, "🤖 RoboticPieceAnimator initialized");
    }
    
    // ==================== PUBLIC API ====================
    
    /**
     * Start robotic movement animation from source to destination
     */
    public void animateRoboticMove(float fromX, float fromY, float toX, float toY, 
                                  PieceProfile profile, RoboticAnimationCallback callback) {
        if (isAnimating) {
            Log.w(TAG, "⚠️ Animation already in progress, canceling previous");
            cancelAnimation();
        }
        
        this.callback = callback;
        this.isAnimating = true;
        
        // Reset animation state flags
        this.pauseTriggered = false;
        this.settlingTriggered = false;
        
        Log.d(TAG, String.format("🤖 Starting robotic animation: (%.1f,%.1f) -> (%.1f,%.1f) profile=%s", 
                                fromX, fromY, toX, toY, profile.name()));
        
        // Calculate movement parameters
        float deltaX = toX - fromX;
        float deltaY = toY - fromY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        // Choose animation duration based on distance and piece profile
        int duration = calculateAnimationDuration(distance, profile);
        
        // Create the main animation
        createRoboticMovementAnimation(fromX, fromY, deltaX, deltaY, profile, duration);
    }
    
    /**
     * Cancel current animation
     */
    public void cancelAnimation() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
            currentAnimator = null;
        }
        isAnimating = false;
        Log.d(TAG, "🛑 Robotic animation canceled");
    }
    
    /**
     * Check if animation is currently running
     */
    public boolean isAnimating() {
        return isAnimating;
    }
    
    // ==================== ANIMATION CREATION ====================
    
    private void createRoboticMovementAnimation(float startX, float startY, 
                                              float deltaX, float deltaY, 
                                              PieceProfile profile, int duration) {
        
        currentAnimator = ValueAnimator.ofFloat(0f, 1f);
        currentAnimator.setDuration(duration);
        currentAnimator.setInterpolator(new LinearInterpolator()); // We'll handle easing manually
        
        currentAnimator.addUpdateListener(animation -> {
            float progress = (Float) animation.getAnimatedValue();
            
            // Calculate position and effects based on progress and profile
            AnimationFrame frame = calculateAnimationFrame(
                startX, startY, deltaX, deltaY, progress, profile
            );
            
            // Trigger phase callbacks using class fields
            if (!this.pauseTriggered && progress >= PAUSE_START && progress <= PAUSE_END) {
                this.pauseTriggered = true;
                if (callback != null) callback.onMechanicalPause();
                Log.d(TAG, "⏸️ Mechanical recalibration pause");
            }
            
            if (!this.settlingTriggered && progress >= OVERSHOOT_POINT) {
                this.settlingTriggered = true;
                if (callback != null) callback.onSettlingStart();
                Log.d(TAG, "🔧 Mechanical settling started");
            }
            
            // Update piece position and effects
            if (callback != null) {
                callback.onAnimationUpdate(frame.x, frame.y, frame.rotation, frame.alpha);
            }
        });
        
        currentAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                if (callback != null) callback.onAnimationStart();
                Log.d(TAG, "🚀 Robotic animation started");
            }
            
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                isAnimating = false;
                if (callback != null) callback.onAnimationComplete();
                Log.d(TAG, "✅ Robotic animation completed");
            }
            
            @Override
            public void onAnimationCancel(android.animation.Animator animation) {
                isAnimating = false;
                Log.d(TAG, "🛑 Robotic animation was canceled");
            }
        });
        
        currentAnimator.start();
    }
    
    // ==================== ANIMATION CALCULATION ====================
    
    /**
     * Calculate animation frame data for given progress
     */
    private AnimationFrame calculateAnimationFrame(float startX, float startY, 
                                                 float deltaX, float deltaY, 
                                                 float progress, PieceProfile profile) {
        
        AnimationFrame frame = new AnimationFrame();
        
        // Calculate position based on animation phase
        if (progress < PHASE_1_END) {
            // Phase 1: Motor spin-up with deceleration
            float phaseProgress = progress / PHASE_1_END;
            float easedProgress = applySpinUpEasing(phaseProgress) * PHASE_1_END;
            frame.x = startX + deltaX * easedProgress;
            frame.y = startY + deltaY * easedProgress;
            
        } else if (progress < PAUSE_END) {
            // Pause phase: Hold position during recalibration
            frame.x = startX + deltaX * PHASE_1_END;
            frame.y = startY + deltaY * PHASE_1_END;
            
        } else if (progress < OVERSHOOT_POINT) {
            // Phase 2: Resume movement with acceleration
            float phaseProgress = (progress - PAUSE_END) / (OVERSHOOT_POINT - PAUSE_END);
            float resumeProgress = PHASE_1_END + (1f - PHASE_1_END) * applyAccelerationEasing(phaseProgress);
            frame.x = startX + deltaX * resumeProgress;
            frame.y = startY + deltaY * resumeProgress;
            
        } else {
            // Phase 3: Overshoot and settling
            float settlingProgress = (progress - OVERSHOOT_POINT) / (1f - OVERSHOOT_POINT);
            calculateSettlingPosition(startX, startY, deltaX, deltaY, settlingProgress, frame);
        }
        
        // Apply piece-specific effects
        applyPieceProfileEffects(frame, progress, profile);
        
        // Apply mechanical rotation (scanning effect)
        frame.rotation = calculateMechanicalRotation(progress, profile);
        
        // Standard alpha (pieces don't fade)
        frame.alpha = 1f;
        
        return frame;
    }
    
    /**
     * Calculate overshoot and settling motion
     */
    private void calculateSettlingPosition(float startX, float startY, float deltaX, float deltaY, 
                                         float settlingProgress, AnimationFrame frame) {
        
        float targetX = startX + deltaX;
        float targetY = startY + deltaY;
        
        if (settlingProgress < 0.6f) {
            // Overshoot phase: Move slightly beyond target
            float overshootProgress = settlingProgress / 0.6f;
            float overshootFactor = 1f + (OVERSHOOT_DISTANCE / Math.max(Math.abs(deltaX), Math.abs(deltaY))) * 
                                   (1f - overshootProgress);
            frame.x = startX + deltaX * overshootFactor;
            frame.y = startY + deltaY * overshootFactor;
            
        } else {
            // Settling phase: Return to target with jitter
            float jitterProgress = (settlingProgress - 0.6f) / 0.4f;
            float jitterX = calculateMechanicalJitter(jitterProgress) * (deltaX != 0 ? 1 : 0);
            float jitterY = calculateMechanicalJitter(jitterProgress) * (deltaY != 0 ? 1 : 0);
            
            frame.x = targetX + jitterX;
            frame.y = targetY + jitterY;
        }
    }
    
    /**
     * Calculate mechanical jitter for settling effect
     */
    private float calculateMechanicalJitter(float progress) {
        if (progress >= 1f) return 0f;
        
        // Damped oscillation
        float amplitude = JITTER_AMPLITUDE * (1f - progress);
        float frequency = JITTER_CYCLES * 2f * (float) Math.PI;
        return amplitude * (float) Math.sin(frequency * progress) * (random.nextFloat() * 0.5f + 0.5f);
    }
    
    /**
     * Calculate mechanical rotation for scanning effect
     */
    private float calculateMechanicalRotation(float progress, PieceProfile profile) {
        if (progress < 0.1f) {
            // Initial scanning rotation
            float scanProgress = progress / 0.1f;
            return ROTATION_ANGLE * scanProgress;
        } else if (progress > 0.9f) {
            // Return to upright
            float returnProgress = (progress - 0.9f) / 0.1f;
            return ROTATION_ANGLE * (1f - returnProgress);
        } else {
            // Maintain slight rotation during movement
            return ROTATION_ANGLE * (profile == PieceProfile.KNIGHT_HOP ? 1.5f : 1f);
        }
    }
    
    // ==================== PIECE PROFILE EFFECTS ====================
    
    /**
     * Apply piece-specific movement characteristics
     */
    private void applyPieceProfileEffects(AnimationFrame frame, float progress, PieceProfile profile) {
        switch (profile) {
            case ROOK_ROLL:
                // Add slight rolling motion
                frame.y += Math.sin(progress * Math.PI * 4) * 1f;
                break;
                
            case KNIGHT_HOP:
                // L-shaped hop with arc
                if (progress > 0.2f && progress < 0.8f) {
                    float hopProgress = (progress - 0.2f) / 0.6f;
                    float hopHeight = 12f * (float) Math.sin(hopProgress * Math.PI);
                    frame.y -= hopHeight; // Negative for upward hop
                }
                break;
                
            case BISHOP_GLIDE:
                // Smooth diagonal glide (no modifications needed)
                break;
                
            case QUEEN_POWER:
                // Dramatic movement with slight scale effect
                frame.alpha = 0.9f + 0.1f * (float) Math.sin(progress * Math.PI * 2);
                break;
                
            case KING_CAUTIOUS:
                // Extra careful movement (already handled by longer duration)
                break;
                
            case PAWN_MARCH:
                // Simple forward march with slight bounce
                frame.y += Math.abs(Math.sin(progress * Math.PI * 2)) * 0.5f;
                break;
        }
    }
    
    // ==================== EASING FUNCTIONS ====================
    
    /**
     * Motor spin-up easing (slow start, then accelerate)
     */
    private float applySpinUpEasing(float t) {
        // Cubic ease-out for motor starting
        return 1f - (float) Math.pow(1 - t, 3);
    }
    
    /**
     * Acceleration easing (quick pickup after pause)
     */
    private float applyAccelerationEasing(float t) {
        // Quadratic ease-in for resuming movement
        return t * t;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Calculate animation duration based on distance and piece profile
     */
    private int calculateAnimationDuration(float distance, PieceProfile profile) {
        int baseDuration = DEFAULT_ANIMATION_DURATION;
        
        // Adjust for piece profile
        switch (profile) {
            case ROOK_ROLL:
                return (int) (baseDuration * 0.8f); // Faster rolling
            case KNIGHT_HOP:
                return (int) (baseDuration * 1.2f); // Longer for hop
            case BISHOP_GLIDE:
                return baseDuration;
            case QUEEN_POWER:
                return (int) (baseDuration * 1.1f); // Slightly dramatic
            case KING_CAUTIOUS:
                return (int) (baseDuration * 1.5f); // Much slower
            case PAWN_MARCH:
                return (int) (baseDuration * 0.9f); // Simple march
            default:
                return baseDuration;
        }
    }
    
    /**
     * Get piece profile from chess piece character
     */
    public static PieceProfile getPieceProfile(char piece) {
        switch (Character.toLowerCase(piece)) {
            case 'r': return PieceProfile.ROOK_ROLL;
            case 'n': return PieceProfile.KNIGHT_HOP;
            case 'b': return PieceProfile.BISHOP_GLIDE;
            case 'q': return PieceProfile.QUEEN_POWER;
            case 'k': return PieceProfile.KING_CAUTIOUS;
            case 'p': return PieceProfile.PAWN_MARCH;
            default: return PieceProfile.PAWN_MARCH;
        }
    }
    
    // ==================== ANIMATION FRAME DATA ====================
    
    /**
     * Container for animation frame data
     */
    public static class AnimationFrame {
        public float x;
        public float y;
        public float rotation; // In degrees
        public float alpha;    // 0.0 to 1.0
        
        public AnimationFrame() {
            this.x = 0f;
            this.y = 0f;
            this.rotation = 0f;
            this.alpha = 1f;
        }
        
        @Override
        public String toString() {
            return String.format("Frame(x=%.1f, y=%.1f, rot=%.1f°, alpha=%.2f)", 
                               x, y, rotation, alpha);
        }
    }
}