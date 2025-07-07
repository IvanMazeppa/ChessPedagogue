package com.example.chesspedagogue.ui;

import android.animation.ValueAnimator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enhanced capture effects system with spectacular particle explosions
 * Optimized for Samsung S23 Ultra with advanced visual flair
 */
public class EnhancedCaptureEffects {
    
    private static final String TAG = "EnhancedCaptureEffects";
    
    // Enhanced capture explosion shader with safety improvements
    private static final String CAPTURE_EXPLOSION_SHADER = 
        "uniform shader sourceTexture;\n" +
        "uniform float2 resolution;\n" +
        "uniform float2 center;\n" +
        "uniform float time;\n" +
        "uniform float3 pieceColor;\n" +
        "uniform float intensity;\n" +
        "\n" +
        "half4 main(float2 coord) {\n" +
        "    // Safety checks\n" +
        "    if (resolution.x <= 0.0 || resolution.y <= 0.0) {\n" +
        "        return sourceTexture.eval(coord);\n" +
        "    }\n" +
        "    \n" +
        "    float2 uv = coord / resolution;\n" +
        "    float2 centerUV = center / resolution;\n" +
        "    \n" +
        "    // Get original color safely\n" +
        "    half4 original = sourceTexture.eval(coord);\n" +
        "    \n" +
        "    float dist = distance(uv, centerUV);\n" +
        "    \n" +
        "    // Particle explosion effect\n" +
        "    float explosion = exp(-time * 2.0) * exp(-dist * 8.0);\n" +
        "    \n" +
        "    // Shockwave ring\n" +
        "    float ringRadius = time * 0.3;\n" +
        "    float ring = exp(-abs(dist - ringRadius) * 25.0) * exp(-time * 1.5);\n" +
        "    \n" +
        "    // Sparkling particles\n" +
        "    float sparkle = 0.0;\n" +
        "    for (int i = 0; i < 8; i++) {\n" +
        "        float angle = float(i) * 0.785398; // π/4\n" +
        "        float2 sparklePos = centerUV + float2(cos(angle), sin(angle)) * time * 0.2;\n" +
        "        float sparkDist = distance(uv, sparklePos);\n" +
        "        sparkle += exp(-sparkDist * 50.0) * exp(-time * 3.0);\n" +
        "    }\n" +
        "    \n" +
        "    // Combine effects\n" +
        "    float totalEffect = (explosion + ring + sparkle * 0.5) * intensity;\n" +
        "    \n" +
        "    // Apply piece-colored explosion\n" +
        "    half4 effectColor = half4(pieceColor, totalEffect);\n" +
        "    return mix(original, effectColor, totalEffect);\n" +
        "}";
    
    // Particle trail shader for piece movement
    private static final String PARTICLE_TRAIL_SHADER = 
        "uniform shader sourceTexture;\n" +
        "uniform float2 resolution;\n" +
        "uniform float2 startPos;\n" +
        "uniform float2 endPos;\n" +
        "uniform float progress;\n" +
        "uniform float3 trailColor;\n" +
        "\n" +
        "half4 main(float2 coord) {\n" +
        "    if (resolution.x <= 0.0 || resolution.y <= 0.0) {\n" +
        "        return sourceTexture.eval(coord);\n" +
        "    }\n" +
        "    \n" +
        "    half4 original = sourceTexture.eval(coord);\n" +
        "    \n" +
        "    float2 uv = coord / resolution;\n" +
        "    float2 currentPos = mix(startPos / resolution, endPos / resolution, progress);\n" +
        "    \n" +
        "    // Create trailing particles\n" +
        "    float trail = 0.0;\n" +
        "    for (float t = 0.0; t < 1.0; t += 0.1) {\n" +
        "        if (t > progress) break;\n" +
        "        \n" +
        "        float2 trailPos = mix(startPos / resolution, endPos / resolution, t);\n" +
        "        float dist = distance(uv, trailPos);\n" +
        "        float fade = (1.0 - t) * (progress - t) * 10.0;\n" +
        "        trail += exp(-dist * 100.0) * fade;\n" +
        "    }\n" +
        "    \n" +
        "    half4 trailEffect = half4(trailColor, trail * 0.6);\n" +
        "    return mix(original, trailEffect, trail * 0.6);\n" +
        "}";
    
    /**
     * Create spectacular capture explosion with enhanced particle effects
     */
    public static void createCaptureExplosion(ViewGroup boardContainer, float centerX, float centerY, 
                                            String capturedPieceType, boolean isWhitePiece) {
        if (boardContainer == null) {
            Log.w(TAG, "Board container is null, skipping capture explosion");
            return;
        }
        
        Log.d(TAG, "💥 Creating spectacular capture explosion at (" + centerX + ", " + centerY + ")");
        
        // Get piece-specific colors
        float[] pieceColor = getPieceExplosionColor(capturedPieceType, isWhitePiece);
        
        // Check AGSL support using unified manager
        boolean agslSupported = AGSLManager.getInstance().isSupported();
        
        // Log detailed capture information for debugging
        VisualEffectsDebugger.logCaptureEffect(capturedPieceType, isWhitePiece, centerX, centerY, agslSupported);
        
        // Create AGSL shader explosion if supported
        if (agslSupported) {
            createAGSLCaptureExplosion(boardContainer, centerX, centerY, pieceColor);
        }
        
        // 🚀 TEMPORARY DISABLE: Flying piece causing board spinning bug  
        // createDramaticFlyingPiece(boardContainer, centerX, centerY, capturedPieceType, isWhitePiece);
        
        // Always create particle burst animation (works on all Android versions)
        createParticleExplosion(boardContainer, centerX, centerY, pieceColor, capturedPieceType);
        
        // Add shockwave ring effect
        createShockwaveRing(boardContainer, centerX, centerY, pieceColor);
        
        Log.d(TAG, "✨ DRAMATIC capture explosion effects initiated with flying piece!");
    }
    
    /**
     * Create AGSL shader-based capture explosion with enhanced safety
     */
    private static void createAGSLCaptureExplosion(ViewGroup boardContainer, float centerX, float centerY, float[] pieceColor) {
        try {
            Log.d(TAG, "🎆 Creating AGSL capture explosion");
            
            // Create overlay view for shader effect
            View explosionOverlay = new View(boardContainer.getContext());
            explosionOverlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.MATCH_PARENT
            ));
            
            boardContainer.addView(explosionOverlay);
            
            // Wait for layout to complete before applying shader
            explosionOverlay.post(() -> {
                try {
                    if (explosionOverlay.getWidth() <= 0 || explosionOverlay.getHeight() <= 0) {
                        Log.w(TAG, "Explosion overlay has invalid dimensions, removing");
                        boardContainer.removeView(explosionOverlay);
                        return;
                    }
                    
                    RuntimeShader shader = new RuntimeShader(CAPTURE_EXPLOSION_SHADER);
                    
                    // Animate explosion over 1.5 seconds
                    ValueAnimator explosionAnimator = ValueAnimator.ofFloat(0f, 1.5f);
                    explosionAnimator.setDuration(1500);
                    explosionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    
                    explosionAnimator.addUpdateListener(animation -> {
                        try {
                            // Additional safety checks during animation
                            if (explosionOverlay.getWidth() <= 0 || explosionOverlay.getHeight() <= 0) {
                                return;
                            }
                            
                            float time = (float) animation.getAnimatedValue();
                            
                            shader.setFloatUniform("resolution", explosionOverlay.getWidth(), explosionOverlay.getHeight());
                            shader.setFloatUniform("center", centerX, centerY);
                            shader.setFloatUniform("time", time);
                            shader.setFloatUniform("pieceColor", pieceColor[0], pieceColor[1], pieceColor[2]);
                            shader.setFloatUniform("intensity", Math.max(0f, 1.5f - time)); // Fade out
                            
                            RenderEffect effect = RenderEffect.createRuntimeShaderEffect(shader, "sourceTexture");
                            explosionOverlay.setRenderEffect(effect);
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error during AGSL explosion animation: " + e.getMessage());
                            animation.cancel();
                        }
                    });
                    
                    explosionAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Clean up overlay
                            boardContainer.removeView(explosionOverlay);
                            Log.d(TAG, "✅ AGSL explosion animation completed");
                        }
                        
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            boardContainer.removeView(explosionOverlay);
                            Log.d(TAG, "🚫 AGSL explosion animation cancelled");
                        }
                    });
                    
                    explosionAnimator.start();
                    
                } catch (Exception e) {
                    Log.e(TAG, "❌ AGSL explosion setup failed: " + e.getMessage());
                    boardContainer.removeView(explosionOverlay);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "❌ AGSL capture explosion failed: " + e.getMessage());
        }
    }
    
    /**
     * Create particle explosion using traditional Android animations
     */
    private static void createParticleExplosion(ViewGroup boardContainer, float centerX, float centerY, 
                                              float[] pieceColor, String capturedPieceType) {
        Log.d(TAG, "🎇 Creating particle explosion burst");
        
        List<ImageView> particles = new ArrayList<>();
        Random random = new Random();
        
        // Create 15-25 particles based on piece importance
        int particleCount = getParticleCount(capturedPieceType);
        long animationDuration = 800 + random.nextInt(400); // 800-1200ms
        
        // Log particle performance
        VisualEffectsDebugger.logParticlePerformance(particleCount, animationDuration, capturedPieceType);
        
        for (int i = 0; i < particleCount; i++) {
            ImageView particle = createParticle(boardContainer.getContext(), pieceColor, capturedPieceType);
            
            // Position particle at explosion center
            particle.setX(centerX - particle.getWidth() / 2f);
            particle.setY(centerY - particle.getHeight() / 2f);
            particle.setAlpha(0.8f);
            particle.setScaleX(0.5f);
            particle.setScaleY(0.5f);
            
            boardContainer.addView(particle);
            particles.add(particle);
            
            // Animate particle with random velocity
            float angle = (float) (random.nextFloat() * 2 * Math.PI);
            float velocity = 100f + random.nextFloat() * 200f; // 100-300px
            float targetX = centerX + (float) Math.cos(angle) * velocity;
            float targetY = centerY + (float) Math.sin(angle) * velocity;
            
            AnimatorSet particleAnimation = new AnimatorSet();
            
            ObjectAnimator moveX = ObjectAnimator.ofFloat(particle, "x", particle.getX(), targetX);
            ObjectAnimator moveY = ObjectAnimator.ofFloat(particle, "y", particle.getY(), targetY);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(particle, "alpha", 0.8f, 0f);
            ObjectAnimator scaleDown = ObjectAnimator.ofFloat(particle, "scaleX", 0.5f, 0.1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(particle, "scaleY", 0.5f, 0.1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(particle, "rotation", 0f, 360f * (1 + random.nextFloat()));
            
            particleAnimation.playTogether(moveX, moveY, fadeOut, scaleDown, scaleDownY, rotation);
            particleAnimation.setDuration(800 + random.nextInt(400)); // 800-1200ms
            particleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            
            particleAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    boardContainer.removeView(particle);
                }
            });
            
            // Stagger particle start times slightly for more natural effect
            particleAnimation.setStartDelay(random.nextInt(100));
            particleAnimation.start();
        }
        
        Log.d(TAG, "✅ Created " + particleCount + " particles for " + capturedPieceType + " explosion");
    }
    
    /**
     * Create shockwave ring effect
     */
    private static void createShockwaveRing(ViewGroup boardContainer, float centerX, float centerY, float[] pieceColor) {
        Log.d(TAG, "💫 Creating shockwave ring effect");
        
        View shockwaveView = new View(boardContainer.getContext());
        shockwaveView.setBackgroundResource(android.R.drawable.btn_default); // Temporary circular background
        
        int initialSize = 20;
        shockwaveView.setLayoutParams(new ViewGroup.LayoutParams(initialSize, initialSize));
        shockwaveView.setX(centerX - initialSize / 2f);
        shockwaveView.setY(centerY - initialSize / 2f);
        shockwaveView.setAlpha(0.6f);
        
        boardContainer.addView(shockwaveView);
        
        // Animate ring expansion
        AnimatorSet shockwaveAnimation = new AnimatorSet();
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(shockwaveView, "scaleX", 1f, 8f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(shockwaveView, "scaleY", 1f, 8f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(shockwaveView, "alpha", 0.6f, 0f);
        
        shockwaveAnimation.playTogether(scaleX, scaleY, fadeOut);
        shockwaveAnimation.setDuration(600);
        shockwaveAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        
        shockwaveAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                boardContainer.removeView(shockwaveView);
                Log.d(TAG, "✅ Shockwave ring completed");
            }
        });
        
        shockwaveAnimation.start();
    }
    
    /**
     * Create particle trail effect for piece movement
     */
    public static void createMovementTrail(ViewGroup boardContainer, float startX, float startY, 
                                         float endX, float endY, String pieceType, boolean isWhitePiece) {
        if (boardContainer == null) {
            Log.w(TAG, "Board container is null, skipping movement trail");
            return;
        }
        
        Log.d(TAG, "✨ Creating movement trail from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")");
        
        // Log movement trail for debugging
        VisualEffectsDebugger.logMovementTrail(pieceType, isWhitePiece, startX, startY, endX, endY);
        
        float[] trailColor = getPieceTrailColor(pieceType, isWhitePiece);
        
        // Create multiple trail particles
        for (int i = 0; i < 8; i++) {
            ImageView trailParticle = createTrailParticle(boardContainer.getContext(), trailColor);
            
            // Position at start
            trailParticle.setX(startX);
            trailParticle.setY(startY);
            trailParticle.setAlpha(0.4f);
            trailParticle.setScaleX(0.3f);
            trailParticle.setScaleY(0.3f);
            
            boardContainer.addView(trailParticle);
            
            // Animate along path with delay
            ObjectAnimator moveX = ObjectAnimator.ofFloat(trailParticle, "x", startX, endX);
            ObjectAnimator moveY = ObjectAnimator.ofFloat(trailParticle, "y", startY, endY);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(trailParticle, "alpha", 0.4f, 0f);
            
            AnimatorSet trailAnimation = new AnimatorSet();
            trailAnimation.playTogether(moveX, moveY, fadeOut);
            trailAnimation.setDuration(400);
            trailAnimation.setStartDelay(i * 20); // Stagger particles
            trailAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            
            trailAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    boardContainer.removeView(trailParticle);
                }
            });
            
            trailAnimation.start();
        }
    }
    
    /**
     * Get explosion color based on piece type and color
     */
    private static float[] getPieceExplosionColor(String pieceType, boolean isWhitePiece) {
        if (isWhitePiece) {
            switch (pieceType.toLowerCase()) {
                case "queen": return new float[]{1.0f, 0.9f, 0.2f}; // Golden
                case "rook": return new float[]{0.8f, 0.9f, 1.0f}; // Light blue
                case "bishop": return new float[]{0.9f, 0.7f, 1.0f}; // Light purple
                case "knight": return new float[]{0.9f, 1.0f, 0.7f}; // Light green
                case "pawn": return new float[]{1.0f, 1.0f, 0.9f}; // Cream
                default: return new float[]{1.0f, 0.8f, 0.2f}; // Golden default
            }
        } else {
            switch (pieceType.toLowerCase()) {
                case "queen": return new float[]{0.8f, 0.2f, 0.2f}; // Deep red
                case "rook": return new float[]{0.2f, 0.4f, 0.8f}; // Deep blue
                case "bishop": return new float[]{0.6f, 0.2f, 0.8f}; // Deep purple
                case "knight": return new float[]{0.2f, 0.6f, 0.2f}; // Deep green
                case "pawn": return new float[]{0.4f, 0.4f, 0.4f}; // Dark gray
                default: return new float[]{0.8f, 0.3f, 0.1f}; // Orange default
            }
        }
    }
    
    /**
     * Get trail color for piece movement
     */
    private static float[] getPieceTrailColor(String pieceType, boolean isWhitePiece) {
        if (isWhitePiece) {
            return new float[]{0.9f, 0.9f, 1.0f}; // Light blue-white
        } else {
            return new float[]{0.8f, 0.6f, 0.4f}; // Warm brown
        }
    }
    
    /**
     * Get particle count based on piece importance
     */
    private static int getParticleCount(String pieceType) {
        switch (pieceType.toLowerCase()) {
            case "queen": return 25;
            case "rook": return 20;
            case "bishop": return 18;
            case "knight": return 16;
            case "pawn": return 12;
            default: return 15;
        }
    }
    
    /**
     * Create a particle view for explosion effects
     */
    private static ImageView createParticle(android.content.Context context, float[] color, String pieceType) {
        ImageView particle = new ImageView(context);
        particle.setLayoutParams(new ViewGroup.LayoutParams(12, 12));
        
        // Set particle appearance based on piece type
        switch (pieceType.toLowerCase()) {
            case "queen":
                particle.setImageResource(android.R.drawable.star_big_on); // Star for queen
                break;
            case "rook":
                particle.setImageResource(android.R.drawable.btn_star); // Square-ish for rook
                break;
            default:
                particle.setImageResource(android.R.drawable.presence_online); // Circle for others
                break;
        }
        
        // Apply color tint
        particle.setColorFilter(android.graphics.Color.rgb(
            (int)(color[0] * 255), 
            (int)(color[1] * 255), 
            (int)(color[2] * 255)
        ));
        
        return particle;
    }
    
    /**
     * Create a trail particle view
     */
    private static ImageView createTrailParticle(android.content.Context context, float[] color) {
        ImageView particle = new ImageView(context);
        particle.setLayoutParams(new ViewGroup.LayoutParams(8, 8));
        particle.setImageResource(android.R.drawable.presence_online);
        
        // Apply color tint
        particle.setColorFilter(android.graphics.Color.rgb(
            (int)(color[0] * 255), 
            (int)(color[1] * 255), 
            (int)(color[2] * 255)
        ));
        
        return particle;
    }
    
    /**
     * 🚀 DRAMATIC FLYING PIECE - The spectacular physics effect you want to see!
     * Creates a realistic piece that flies off the board with physics simulation
     */
    private static void createDramaticFlyingPiece(ViewGroup boardContainer, float centerX, float centerY, 
                                                String capturedPieceType, boolean isWhitePiece) {
        Log.d(TAG, "🚀💥 Creating DRAMATIC flying " + capturedPieceType + " off the board!");
        
        // Create a realistic piece ImageView that will fly off
        ImageView flyingPiece = new ImageView(boardContainer.getContext());
        
        // Set the actual piece image based on type and color
        int pieceResourceId = getPieceImageResource(capturedPieceType, isWhitePiece);
        if (pieceResourceId != 0) {
            flyingPiece.setImageResource(pieceResourceId);
        }
        
        // Size the flying piece (make it board-square sized for realism)
        int pieceSize = (int) (64 * boardContainer.getContext().getResources().getDisplayMetrics().density); // 64dp
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(pieceSize, pieceSize);
        flyingPiece.setLayoutParams(params);
        
        // Position at capture location
        flyingPiece.setX(centerX - pieceSize / 2f);
        flyingPiece.setY(centerY - pieceSize / 2f);
        flyingPiece.setElevation(20f); // High elevation so it flies over everything
        
        boardContainer.addView(flyingPiece);
        
        // 🎯 PHYSICS SIMULATION: Calculate dramatic trajectory off the screen
        Random random = new Random();
        
        // Random direction but always off the screen dramatically
        float angle = random.nextFloat() * 360f; // Random direction
        float force = 800f + random.nextFloat() * 400f; // 800-1200px flight distance
        
        // Calculate target position WAY off screen for dramatic effect
        float targetX = centerX + (float) Math.cos(Math.toRadians(angle)) * force;
        float targetY = centerY + (float) Math.sin(Math.toRadians(angle)) * force;
        
        // Add gravity effect (pieces fall down as they fly)
        targetY += 200f + random.nextFloat() * 300f; // Gravity pulls down
        
        // 🎬 DRAMATIC ANIMATION: Multiple physics effects combined
        AnimatorSet dramaticFlight = new AnimatorSet();
        
        // Main trajectory
        ObjectAnimator moveX = ObjectAnimator.ofFloat(flyingPiece, "x", flyingPiece.getX(), targetX);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(flyingPiece, "y", flyingPiece.getY(), targetY);
        
        // Tumbling rotation (pieces tumble realistically)
        ObjectAnimator tumbleX = ObjectAnimator.ofFloat(flyingPiece, "rotationX", 0f, 360f * (2 + random.nextFloat()));
        ObjectAnimator tumbleY = ObjectAnimator.ofFloat(flyingPiece, "rotationY", 0f, 360f * (2 + random.nextFloat()));
        ObjectAnimator spin = ObjectAnimator.ofFloat(flyingPiece, "rotation", 0f, 720f + random.nextFloat() * 720f);
        
        // Scale changes during flight (piece gets smaller as it "flies away")
        ObjectAnimator scaleOut = ObjectAnimator.ofFloat(flyingPiece, "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(flyingPiece, "scaleY", 1.0f, 0.2f);
        
        // Fade out as it flies away
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(flyingPiece, "alpha", 1.0f, 0.0f);
        
        // Combine all animations for spectacular effect
        dramaticFlight.playTogether(moveX, moveY, tumbleX, tumbleY, spin, scaleOut, scaleOutY, fadeOut);
        dramaticFlight.setDuration(1500 + random.nextInt(500)); // 1.5-2 seconds of dramatic flight
        dramaticFlight.setInterpolator(new AccelerateDecelerateInterpolator()); // Natural physics curve
        
        // Add physics-based bounce effect at the start (piece "pops" off board)
        flyingPiece.setScaleX(0.8f);
        flyingPiece.setScaleY(0.8f);
        
        ObjectAnimator bounceOut = ObjectAnimator.ofFloat(flyingPiece, "scaleX", 0.8f, 1.2f, 1.0f);
        ObjectAnimator bounceOutY = ObjectAnimator.ofFloat(flyingPiece, "scaleY", 0.8f, 1.2f, 1.0f);
        AnimatorSet bounceEffect = new AnimatorSet();
        bounceEffect.playTogether(bounceOut, bounceOutY);
        bounceEffect.setDuration(200); // Quick bounce
        bounceEffect.setInterpolator(new OvershootInterpolator(2.0f));
        
        // Clean up when animation completes
        dramaticFlight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                boardContainer.removeView(flyingPiece);
                Log.d(TAG, "🎯 Dramatic flying piece completed and cleaned up");
            }
        });
        
        // 🚀 LAUNCH SEQUENCE: Bounce then dramatic flight
        bounceEffect.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dramaticFlight.start(); // Launch dramatic flight after bounce
            }
        });
        
        bounceEffect.start(); // Start with bounce effect
        
        Log.d(TAG, "🚀 DRAMATIC flying piece launched! Flying " + capturedPieceType + 
                   " to (" + targetX + ", " + targetY + ") with full physics simulation!");
    }
    
    /**
     * Get piece image resource for flying piece effect
     */
    private static int getPieceImageResource(String pieceType, boolean isWhite) {
        if (pieceType == null) return 0;
        
        String prefix = isWhite ? "ic_white_" : "ic_black_";
        String resourceName = prefix + pieceType.toLowerCase();
        
        // Map to actual drawable resources
        switch (resourceName) {
            case "ic_white_pawn": return com.example.chesspedagogue.R.drawable.ic_white_pawn;
            case "ic_white_rook": return com.example.chesspedagogue.R.drawable.ic_white_rook;
            case "ic_white_knight": return com.example.chesspedagogue.R.drawable.ic_white_knight;
            case "ic_white_bishop": return com.example.chesspedagogue.R.drawable.ic_white_bishop;
            case "ic_white_queen": return com.example.chesspedagogue.R.drawable.ic_white_queen;
            case "ic_white_king": return com.example.chesspedagogue.R.drawable.ic_white_king;
            case "ic_black_pawn": return com.example.chesspedagogue.R.drawable.ic_black_pawn;
            case "ic_black_rook": return com.example.chesspedagogue.R.drawable.ic_black_rook;
            case "ic_black_knight": return com.example.chesspedagogue.R.drawable.ic_black_knight;
            case "ic_black_bishop": return com.example.chesspedagogue.R.drawable.ic_black_bishop;
            case "ic_black_queen": return com.example.chesspedagogue.R.drawable.ic_black_queen;
            case "ic_black_king": return com.example.chesspedagogue.R.drawable.ic_black_king;
            default:
                Log.w(TAG, "Unknown piece type for flying effect: " + resourceName);
                return com.example.chesspedagogue.R.drawable.ic_white_pawn; // Default fallback
        }
    }
    
}