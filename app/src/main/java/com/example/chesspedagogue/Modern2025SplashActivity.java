package com.example.chesspedagogue;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.example.chesspedagogue.ui.Material3ExpressiveGlassmorphism;

/**
 * Modern Material 3 Expressive Splash Screen (June 2025)
 * 
 * Features:
 * - True glassmorphism with RenderEffect
 * - Material You dynamic theming
 * - Spring-based animations
 * - Edge-to-edge design
 * - Hardware-accelerated effects
 */
public class Modern2025SplashActivity extends AppCompatActivity {
    
    private static final String TAG = "Modern2025Splash";
    
    // UI Components
    private View backgroundBlurLayer;
    private MaterialCardView heroCard;
    private MaterialCardView configurationPanel;
    private MaterialCardView whiteSelectionCard;
    private MaterialCardView blackSelectionCard;
    private MaterialButton startGameButton;
    private MaterialButton competitiveModeButton;
    private MaterialTextView strengthValueDisplay;
    private Slider aiStrengthSlider;
    private MaterialRadioButton radioWhite;
    private MaterialRadioButton radioBlack;
    private ImageView appIconGlow;
    
    // Game Configuration
    private String selectedColor = "white";
    private int aiStrength = 12;
    private Handler animationHandler = new Handler(Looper.getMainLooper());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable Material 3 Expressive + Material You
        enableMaterial3ExpressiveTheming();
        
        // Enable edge-to-edge design
        enableEdgeToEdgeDesign();
        
        setContentView(R.layout.activity_splash_modern_2025);
        
        // Initialize UI components
        initializeViews();
        
        // Apply Material 3 Expressive glassmorphism
        applyModernGlassmorphismEffects();
        
        // Setup interactions
        setupUserInteractions();
        
        // Start entrance animations
        startEntranceAnimations();
        
        Log.d(TAG, "🚀 Modern 2025 Splash Screen initialized with Material 3 Expressive");
    }
    
    /**
     * Enable Material 3 Expressive + Material You dynamic theming
     */
    private void enableMaterial3ExpressiveTheming() {
        try {
            // Apply Material You dynamic colors
            DynamicColors.applyToActivityIfAvailable(this);
            
            // Get dynamic colors from system (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                int primaryColor = getColor(android.R.color.system_accent1_500);
                int surfaceColor = getColor(android.R.color.system_neutral1_50);
                
                Log.d(TAG, "🎨 Material You colors applied: primary=" + 
                      Integer.toHexString(primaryColor) + ", surface=" + Integer.toHexString(surfaceColor));
            }
            
            Log.d(TAG, "✅ Material 3 Expressive theming enabled");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to enable Material 3 theming", e);
        }
    }
    
    /**
     * Enable edge-to-edge design with proper system bars
     */
    private void enableEdgeToEdgeDesign() {
        try {
            // Enable edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            
            // Set system bar appearance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                );
            }
            
            // Make status bar transparent
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
            
            Log.d(TAG, "✅ Edge-to-edge design enabled");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to enable edge-to-edge design", e);
        }
    }
    
    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        backgroundBlurLayer = findViewById(R.id.backgroundBlurLayer);
        heroCard = findViewById(R.id.heroCard);
        configurationPanel = findViewById(R.id.configurationPanel);
        whiteSelectionCard = findViewById(R.id.whiteSelectionCard);
        blackSelectionCard = findViewById(R.id.blackSelectionCard);
        startGameButton = findViewById(R.id.startGameButton);
        competitiveModeButton = findViewById(R.id.competitiveModeButton);
        strengthValueDisplay = findViewById(R.id.strengthValueDisplay);
        aiStrengthSlider = findViewById(R.id.aiStrengthSlider);
        radioWhite = findViewById(R.id.radioWhite);
        radioBlack = findViewById(R.id.radioBlack);
        appIconGlow = findViewById(R.id.appIconGlow);
        
        // Set background blur layer tag for glassmorphism targeting
        if (backgroundBlurLayer != null) {
            backgroundBlurLayer.setTag("backgroundBlurLayer");
        }
    }
    
    /**
     * Apply Material 3 Expressive glassmorphism effects
     */
    private void applyModernGlassmorphismEffects() {
        try {
            Log.d(TAG, "🔮 Applying Material 3 Expressive glassmorphism effects...");
            
            // Apply glassmorphism to main components
            if (heroCard != null) {
                Material3ExpressiveGlassmorphism.applyExpressiveGlassmorphism(
                    heroCard, Material3ExpressiveGlassmorphism.GlassmorphismLevel.STANDARD);
            }
            
            if (configurationPanel != null) {
                Material3ExpressiveGlassmorphism.applyExpressiveGlassmorphism(
                    configurationPanel, Material3ExpressiveGlassmorphism.GlassmorphismLevel.STANDARD);
            }
            
            if (whiteSelectionCard != null) {
                Material3ExpressiveGlassmorphism.applyExpressiveGlassmorphism(
                    whiteSelectionCard, Material3ExpressiveGlassmorphism.GlassmorphismLevel.SUBTLE);
            }
            
            if (blackSelectionCard != null) {
                Material3ExpressiveGlassmorphism.applyExpressiveGlassmorphism(
                    blackSelectionCard, Material3ExpressiveGlassmorphism.GlassmorphismLevel.SUBTLE);
            }
            
            // Apply to Material buttons
            if (startGameButton != null) {
                Material3ExpressiveGlassmorphism.applyToMaterialComponent(startGameButton);
            }
            
            if (competitiveModeButton != null) {
                Material3ExpressiveGlassmorphism.applyToMaterialComponent(competitiveModeButton);
            }
            
            // Apply background blur effect
            applyBackgroundBlur();
            
            Log.d(TAG, "✅ Material 3 Expressive glassmorphism applied successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply glassmorphism effects", e);
        }
    }
    
    /**
     * Apply background blur for glassmorphism
     */
    private void applyBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && backgroundBlurLayer != null) {
            try {
                // Create subtle background blur
                RenderEffect backgroundBlur = RenderEffect.createBlurEffect(
                    8f, 8f, Shader.TileMode.CLAMP);
                backgroundBlurLayer.setRenderEffect(backgroundBlur);
                
                Log.d(TAG, "🌊 Background blur applied with RenderEffect");
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to apply background blur", e);
            }
        }
    }
    
    /**
     * Setup user interactions with modern animations
     */
    private void setupUserInteractions() {
        // Color selection with spring animations
        setupColorSelection();
        
        // AI strength slider
        setupAIStrengthSlider();
        
        // Button interactions
        setupButtonInteractions();
    }
    
    /**
     * Setup color selection with animations
     */
    private void setupColorSelection() {
        if (whiteSelectionCard != null) {
            whiteSelectionCard.setOnClickListener(v -> {
                selectColor("white");
                animateCardSelection(whiteSelectionCard, true);
                animateCardSelection(blackSelectionCard, false);
            });
        }
        
        if (blackSelectionCard != null) {
            blackSelectionCard.setOnClickListener(v -> {
                selectColor("black");
                animateCardSelection(blackSelectionCard, true);
                animateCardSelection(whiteSelectionCard, false);
            });
        }
        
        // Radio button sync
        if (radioWhite != null) {
            radioWhite.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) selectColor("white");
            });
        }
        
        if (radioBlack != null) {
            radioBlack.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) selectColor("black");
            });
        }
    }
    
    /**
     * Select piece color with visual feedback
     */
    private void selectColor(String color) {
        selectedColor = color;
        
        // Update radio buttons
        if (radioWhite != null) radioWhite.setChecked("white".equals(color));
        if (radioBlack != null) radioBlack.setChecked("black".equals(color));
        
        Log.d(TAG, "🎯 Color selected: " + color);
    }
    
    /**
     * Animate card selection with spring physics
     */
    private void animateCardSelection(MaterialCardView card, boolean isSelected) {
        if (card == null) return;
        
        // Scale animation
        float targetScale = isSelected ? 1.05f : 1.0f;
        float targetElevation = isSelected ? 12f : 4f;
        
        // Spring animation for scale
        SpringAnimation scaleXAnim = new SpringAnimation(card, SpringAnimation.SCALE_X, targetScale);
        SpringAnimation scaleYAnim = new SpringAnimation(card, SpringAnimation.SCALE_Y, targetScale);
        
        scaleXAnim.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM)
                 .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        scaleYAnim.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM)
                 .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        
        scaleXAnim.start();
        scaleYAnim.start();
        
        // Elevation animation
        card.animate().translationZ(targetElevation).setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }
    
    /**
     * Setup AI strength slider
     */
    private void setupAIStrengthSlider() {
        if (aiStrengthSlider != null) {
            aiStrengthSlider.setValue(aiStrength);
            
            aiStrengthSlider.addOnChangeListener((slider, value, fromUser) -> {
                aiStrength = (int) value;
                updateStrengthDisplay();
            });
        }
        
        updateStrengthDisplay();
    }
    
    /**
     * Update strength value display
     */
    private void updateStrengthDisplay() {
        if (strengthValueDisplay == null) return;
        
        String strengthText;
        int elo = 800 + (aiStrength * 100); // 800-3200 ELO range
        
        if (aiStrength <= 6) {
            strengthText = "Beginner • ~" + elo + " ELO";
        } else if (aiStrength <= 12) {
            strengthText = "Intermediate • ~" + elo + " ELO";
        } else if (aiStrength <= 18) {
            strengthText = "Advanced • ~" + elo + " ELO";
        } else {
            strengthText = "Master • ~" + elo + " ELO";
        }
        
        strengthValueDisplay.setText(strengthText);
    }
    
    /**
     * Setup button interactions
     */
    private void setupButtonInteractions() {
        if (startGameButton != null) {
            startGameButton.setOnClickListener(v -> {
                animateButtonPress(startGameButton);
                animationHandler.postDelayed(this::startRegularGame, 200);
            });
        }
        
        if (competitiveModeButton != null) {
            competitiveModeButton.setOnClickListener(v -> {
                animateButtonPress(competitiveModeButton);
                animationHandler.postDelayed(this::startCompetitiveMode, 200);
            });
        }
    }
    
    /**
     * Animate button press with spring physics
     */
    private void animateButtonPress(MaterialButton button) {
        // Quick scale down and back up
        button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
              .setInterpolator(new AccelerateDecelerateInterpolator())
              .withEndAction(() -> {
                  button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150)
                        .setInterpolator(new OvershootInterpolator()).start();
              }).start();
    }
    
    /**
     * Start entrance animations
     */
    private void startEntranceAnimations() {
        // EXAGGERATED entrance animations from different angles
        animateViewEntranceFromTop(heroCard, 0);
        animateViewEntranceFromLeft(configurationPanel, 200);
        animateViewEntranceFromRight(startGameButton, 400);
        animateViewEntranceFromBottom(competitiveModeButton, 600);
        
        // Icon glow animation
        if (appIconGlow != null) {
            startIconGlowAnimation();
        }
        
        Log.d(TAG, "🎬 Exaggerated entrance animations started with different angles");
    }
    
    /**
     * 🎬 EXAGGERATED: Animate view entrance from TOP with dramatic effect
     */
    private void animateViewEntranceFromTop(View view, long delay) {
        if (view == null) return;
        
        // Start FAR above screen, scaled down and transparent
        view.setTranslationY(-800f);
        view.setScaleX(0.3f);
        view.setScaleY(0.3f);
        view.setAlpha(0f);
        view.setRotation(-15f);
        
        animationHandler.postDelayed(() -> {
            // Quick motion then STRONG deceleration
            view.animate()
                .translationY(0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1f)
                .rotation(0f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator(2.0f))
                .start();
        }, delay);
    }
    
    /**
     * 🎬 EXAGGERATED: Animate view entrance from LEFT with dramatic effect  
     */
    private void animateViewEntranceFromLeft(View view, long delay) {
        if (view == null) return;
        
        // Start FAR left, scaled and rotated
        view.setTranslationX(-1200f);
        view.setScaleX(0.2f);
        view.setScaleY(0.2f);
        view.setAlpha(0f);
        view.setRotation(45f);
        
        animationHandler.postDelayed(() -> {
            view.animate()
                .translationX(0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1f)
                .rotation(0f)
                .setDuration(900)
                .setInterpolator(new OvershootInterpolator(1.8f))
                .start();
        }, delay);
    }
    
    /**
     * 🎬 EXAGGERATED: Animate view entrance from RIGHT with dramatic effect
     */
    private void animateViewEntranceFromRight(View view, long delay) {
        if (view == null) return;
        
        // Start FAR right, scaled and rotated
        view.setTranslationX(1200f);
        view.setScaleX(0.2f);
        view.setScaleY(0.2f);
        view.setAlpha(0f);
        view.setRotation(-45f);
        
        animationHandler.postDelayed(() -> {
            view.animate()
                .translationX(0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1f)
                .rotation(0f)
                .setDuration(850)
                .setInterpolator(new OvershootInterpolator(1.9f))
                .start();
        }, delay);
    }
    
    /**
     * 🎬 EXAGGERATED: Animate view entrance from BOTTOM with dramatic effect
     */
    private void animateViewEntranceFromBottom(View view, long delay) {
        if (view == null) return;
        
        // Start FAR below screen, scaled and rotated
        view.setTranslationY(1000f);
        view.setScaleX(0.1f);
        view.setScaleY(0.1f);
        view.setAlpha(0f);
        view.setRotation(30f);
        
        animationHandler.postDelayed(() -> {
            view.animate()
                .translationY(0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1f)
                .rotation(0f)
                .setDuration(750)
                .setInterpolator(new OvershootInterpolator(2.2f))
                .start();
        }, delay);
    }
    
    /**
     * Start icon glow animation
     */
    private void startIconGlowAnimation() {
        if (appIconGlow == null) return;
        
        // Gentle pulsing glow
        ObjectAnimator glowAnimator = ObjectAnimator.ofFloat(appIconGlow, "alpha", 0.7f, 1.0f);
        glowAnimator.setDuration(2000);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        glowAnimator.start();
    }
    
    /**
     * Start regular game mode
     */
    private void startRegularGame() {
        Log.d(TAG, "🎮 Starting regular game: " + selectedColor + ", strength: " + aiStrength);
        
        Intent intent = new Intent(this, TacticalPuzzleActivity.class);
        intent.putExtra("playerColor", selectedColor);
        intent.putExtra("aiStrength", aiStrength);
        startActivity(intent);
        
        // Material 3 transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    /**
     * Start competitive mode
     */
    private void startCompetitiveMode() {
        Log.d(TAG, "🏆 Starting competitive mode: " + selectedColor + ", strength: " + aiStrength);
        
        Intent intent = new Intent(this, CompetitiveModeActivity.class);
        intent.putExtra("playerColor", selectedColor);
        intent.putExtra("aiStrength", aiStrength);
        startActivity(intent);
        
        // Material 3 transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up animations
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }
    }
}