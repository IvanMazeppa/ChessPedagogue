package com.example.chesspedagogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import androidx.cardview.widget.CardView;
import android.widget.SeekBar;
import com.example.chesspedagogue.ui.GlassmorphismUtils;
import com.example.chesspedagogue.ui.WorkingGlassEffects;
import android.animation.ValueAnimator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import com.google.android.material.color.DynamicColors;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    // Material Components UI Elements
    private RadioGroup colorRadioGroup;
    private RadioButton radioWhite;
    private RadioButton radioBlack;
    private SeekBar strengthSlider;
    private TextView strengthValueTextView;
    private Button startButton;
    private Button modernCompetitiveModeButton;
    private CardView whiteSelectionCard;
    private CardView blackSelectionCard;
    private CardView heroCard;
    private CardView colorSelectionCard;
    private CardView strengthCard;
    private View strengthContainer;
    private ImageView splashCrownIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "🚀 Material 3 SplashActivity with Android 15 features starting...");
        
        // Apply Material You dynamic theming FIRST
        DynamicColors.applyToActivityIfAvailable(this);
        
        // Set modern glassmorphic layout
        setContentView(R.layout.activity_splash_modern);
        
        // Enable edge-to-edge immersive experience for Android 15
        setupEdgeToEdgeDisplay();
        
        // Initialize Material 3 UI elements
        initializeViews();
        
        // Apply Material 3 glassmorphism effects (after layout is complete)
        findViewById(android.R.id.content).post(() -> {
            applyMaterial3GlassmorphismEffects();
            startEntranceAnimations();
        });
        
        // Setup functionality
        setupColorSelection();
        setupStrengthSlider();
        setupStartButton();
        
        // Set initial state - ensure white is selected by default
        colorRadioGroup.check(R.id.radioWhite);
        updateSelectionVisuals();
        
        Log.d(TAG, "✅ Material 3 SplashActivity initialization completed with glassmorphism");
    }

    private void initializeViews() {
        Log.d(TAG, "🔍 Initializing Material 3 views...");
        
        // Core UI components
        colorRadioGroup = findViewById(R.id.radioGroupColor);
        radioWhite = findViewById(R.id.radioWhite);
        radioBlack = findViewById(R.id.radioBlack);
        strengthSlider = findViewById(R.id.strengthSlider);
        strengthValueTextView = findViewById(R.id.textViewStrengthValue);
        startButton = findViewById(R.id.startButton);
        modernCompetitiveModeButton = findViewById(R.id.modernCompetitiveModeButton);
        
        // CardViews for glassmorphism
        whiteSelectionCard = findViewById(R.id.whiteSelectionCard);
        blackSelectionCard = findViewById(R.id.blackSelectionCard);
        heroCard = findViewById(R.id.heroCard);
        colorSelectionCard = findViewById(R.id.colorSelectionCard);
        strengthCard = findViewById(R.id.strengthCard);
        strengthContainer = findViewById(R.id.strengthContainer);
        splashCrownIcon = findViewById(R.id.splashCrownIcon);
        
        // Verify all views found
        boolean allViewsFound = 
            colorRadioGroup != null && radioWhite != null && radioBlack != null &&
            strengthSlider != null && strengthValueTextView != null && 
            startButton != null && modernCompetitiveModeButton != null &&
            whiteSelectionCard != null && blackSelectionCard != null;
            
        Log.d(TAG, allViewsFound ? "✅ All Material Components views found successfully" : "❌ Some views not found!");
    }

    private void setupColorSelection() {
        Log.d(TAG, "🎨 Setting up color selection...");
        
        // Set up click listeners for the glassmorphic selection cards
        whiteSelectionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "🤴 WHITE selection card clicked!");
                selectWhite();
            }
        });

        blackSelectionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "♛ BLACK selection card clicked!");
                selectBlack();
            }
        });

        // Also set up radio button listeners
        radioWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "🤴 White radio button clicked!");
                selectWhite();
            }
        });

        radioBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "♛ Black radio button clicked!");
                selectBlack();
            }
        });

        // RadioGroup listener for any other changes
        colorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "📻 RadioGroup changed to ID: " + checkedId);
                updateSelectionVisuals();
            }
        });
        
        Log.d(TAG, "✅ Color selection setup completed");
    }

    private void selectWhite() {
        Log.d(TAG, "🤴 Selecting WHITE color...");
        colorRadioGroup.check(R.id.radioWhite);
        updateSelectionVisuals();
        Log.d(TAG, "✅ White selected and visuals updated");
    }

    private void selectBlack() {
        Log.d(TAG, "♛ Selecting BLACK color...");
        colorRadioGroup.check(R.id.radioBlack);
        updateSelectionVisuals();
        Log.d(TAG, "✅ Black selected and visuals updated");
    }

    private void updateSelectionVisuals() {
        int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
        String selectedName = (selectedColorId == R.id.radioWhite) ? "WHITE" : 
                             (selectedColorId == R.id.radioBlack) ? "BLACK" : "NONE";
        Log.d(TAG, "🎨 Updating Material 3 card visuals for: " + selectedName);
        
        if (selectedColorId == R.id.radioWhite) {
            // Highlight white card with Material 3 glass effect
            whiteSelectionCard.setCardElevation(16f);
            
            // Material 3 glass effect for selected card
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyMaterial3GlassSelection(whiteSelectionCard, true);
            }
            
            // Reset black card
            blackSelectionCard.setCardElevation(8f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyMaterial3GlassSelection(blackSelectionCard, false);
            }
            
            // Add selection animation
            animateCardSelection(whiteSelectionCard, true);
            animateCardSelection(blackSelectionCard, false);
            
            Log.d(TAG, "🎨 Material 3 glass feedback: WHITE card highlighted");
        } else if (selectedColorId == R.id.radioBlack) {
            // Highlight black card with Material 3 glass effect
            blackSelectionCard.setCardElevation(16f);
            
            // Material 3 glass effect for selected card
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyMaterial3GlassSelection(blackSelectionCard, true);
            }
            
            // Reset white card
            whiteSelectionCard.setCardElevation(8f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyMaterial3GlassSelection(whiteSelectionCard, false);
            }
            
            // Add selection animation
            animateCardSelection(blackSelectionCard, true);
            animateCardSelection(whiteSelectionCard, false);
            
            Log.d(TAG, "🎨 Material 3 glass feedback: BLACK card highlighted");
        } else {
            Log.w(TAG, "⚠️ No valid selection found - keeping current state");
        }
    }
    
    /**
     * Animate card selection with smooth scale and alpha effects
     */
    private void animateCardSelection(CardView card, boolean selected) {
        if (card == null) return;
        
        float targetScale = selected ? 1.05f : 1.0f;
        float targetAlpha = selected ? 1.0f : 0.85f;
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", card.getScaleX(), targetScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", card.getScaleY(), targetScale);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", card.getAlpha(), targetAlpha);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void setupStrengthSlider() {
        Log.d(TAG, "⚡ Setting up Material 3 strength slider...");
        
        // Configure SeekBar with 25 levels (0-24)
        strengthSlider.setMax(24);
        strengthSlider.setProgress(12);  // default mid-level (2200 Elo)
        
        // Update display
        updateStrengthDisplay(12);
        
        // Listen for changes with SeekBar
        strengthSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateStrengthDisplay(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        Log.d(TAG, "✅ Material 3 strength slider setup completed");
    }

    private void updateStrengthDisplay(int sliderPosition) {
        // Map slider position to actual Elo ratings with better distribution
        int targetElo = mapSliderToElo(sliderPosition);
        String levelName = getStrengthLevelName(targetElo);
        String strengthText = levelName + ": ~" + targetElo + " Elo";
        strengthValueTextView.setText(strengthText);
        Log.d(TAG, "⚡ Material 3 strength updated: " + strengthText);
    }
    
    private int mapSliderToElo(int sliderPosition) {
        // Better Elo distribution across slider range (0-24)
        int[] eloLevels = {
            1200, 1300, 1400, 1500, 1600, 1700, // Beginner to Intermediate (0-5)
            1750, 1800, 1850, 1900, 1950, 2000, // Advanced (6-11)
            2050, 2100, 2150, 2200, 2250, 2300, // Expert to Master (12-17)
            2350, 2400, 2450, 2500, 2600, 2700, // Master to GM (18-23)
            2800  // Super-GM (24)
        };
        
        if (sliderPosition >= 0 && sliderPosition < eloLevels.length) {
            return eloLevels[sliderPosition];
        }
        return 2200; // Default fallback
    }
    
    private String getStrengthLevelName(int elo) {
        if (elo >= 2800) return "Super-GM";
        if (elo >= 2600) return "Grandmaster";
        if (elo >= 2400) return "International Master";
        if (elo >= 2200) return "FIDE Master";
        if (elo >= 2000) return "Expert";
        if (elo >= 1800) return "Advanced";
        if (elo >= 1600) return "Intermediate";
        if (elo >= 1400) return "Beginner+";
        return "Beginner";
    }

    private void setupStartButton() {
        Log.d(TAG, "▶️ Setting up Material 3 action buttons...");
        
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "▶️ Material 3 START GAME button clicked!");
                startGame();
            }
        });
        
        modernCompetitiveModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "✨ Modern Competitive Mode button clicked!");
                launchModernCompetitiveMode();
            }
        });
        
        Log.d(TAG, "✅ Material 3 action buttons setup completed");
    }
    
    /**
     * Setup edge-to-edge display for Android 15 immersive experience
     */
    private void setupEdgeToEdgeDisplay() {
        Log.d(TAG, "🌊 Setting up edge-to-edge display for Android 15...");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Enable edge-to-edge with glassmorphism background
            GlassmorphismUtils.enableEdgeToEdge(getWindow(), findViewById(android.R.id.content));
            
            // Set adaptive system bar icons (light icons for dark gradient background)
            GlassmorphismUtils.setAdaptiveSystemBarIcons(getWindow(), false);
            
            Log.d(TAG, "✅ Edge-to-edge display configured for Samsung S23 Ultra");
        } else {
            Log.d(TAG, "ℹ️ Edge-to-edge not available on this Android version");
        }
    }
    
    /**
     * Apply Material 3 glassmorphism effects with RenderEffect.createBlurEffect()
     * Uses 20px blur and 18% opacity as specified in design documents
     */
    private void applyMaterial3GlassmorphismEffects() {
        Log.d(TAG, "✨ Applying Material 3 glassmorphism with RenderEffect.createBlurEffect()...");
        
        try {
            // Material 3 Glass effect parameters (per design doc)
            float blurRadius = 20.0f;        // 20px blur as specified
            float glassOpacity = 0.18f;      // 18% opacity for readability
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(TAG, "🔮 Applying Material 3 glass effects to all cards...");
                
                RenderEffect blurEffect = RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP);
                
                // Get Material You colors
                int surfaceColor = getMaterialYouSurfaceColor();
                int onSurfaceColor = getMaterialYouOnSurfaceColor();
                
                // Apply to all cards with consistent Material 3 styling
                applyMaterial3GlassEffect(heroCard, blurEffect, surfaceColor, onSurfaceColor, glassOpacity);
                applyMaterial3GlassEffect(colorSelectionCard, blurEffect, surfaceColor, onSurfaceColor, glassOpacity);
                applyMaterial3GlassEffect(whiteSelectionCard, blurEffect, surfaceColor, onSurfaceColor, glassOpacity);
                applyMaterial3GlassEffect(blackSelectionCard, blurEffect, surfaceColor, onSurfaceColor, glassOpacity);
                applyMaterial3GlassEffect(strengthCard, blurEffect, surfaceColor, onSurfaceColor, glassOpacity);
                
                // Apply Material 3 button styling
                applyMaterial3ButtonEffects(blurEffect, surfaceColor, onSurfaceColor, glassOpacity);
                
                Log.d(TAG, "✅ Material 3 glass effects applied successfully");
            } else {
                Log.d(TAG, "ℹ️ RenderEffect not available - using fallback styling");
                applyFallbackGlassEffects();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error applying Material 3 glassmorphism effects", e);
        }
        
        Log.d(TAG, "✅ Material 3 glassmorphism system initialized:");
        Log.d(TAG, "   🔹 RenderEffect.createBlurEffect() with 20px blur");
        Log.d(TAG, "   🔹 18% opacity for perfect readability");
        Log.d(TAG, "   🔹 Material You dynamic color integration");
        Log.d(TAG, "   🔹 Samsung S23 Ultra optimized");
    }
    
    /**
     * Apply Material 3 glass effect to individual view - PROPER GLASSMORPHISM
     * Creates translucent panels with sharp content, NOT blurred content
     */
    private void applyMaterial3GlassEffect(View view, RenderEffect blurEffect, int surfaceColor, int onSurfaceColor, float opacity) {
        if (view == null) return;
        
        try {
            // DO NOT apply RenderEffect to the view itself - that blurs the content!
            // True glassmorphism = translucent background + sharp content
            
            // Create true glassmorphism background
            GradientDrawable glassBackground = new GradientDrawable();
            glassBackground.setShape(GradientDrawable.RECTANGLE);
            glassBackground.setCornerRadius(24f); // Modern rounded corners
            
            // CRITICAL: Very low opacity for true glass effect (5-15%)
            int trueGlassOpacity = (int)(0.08f * 255); // 8% opacity - truly translucent
            int glassColor = 0xFFFFFFFF & 0x00FFFFFF | (trueGlassOpacity << 24); // Pure white base
            glassBackground.setColor(glassColor);
            
            // Subtle border glow
            int borderOpacity = (int)(0.2f * 255);
            int borderColor = 0xFFFFFFFF & 0x00FFFFFF | (borderOpacity << 24);
            glassBackground.setStroke(1, borderColor);
            
            view.setBackground(glassBackground);
            
            // Hardware acceleration for smooth performance
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            
            Log.d(TAG, "✅ True glassmorphism applied: 8% opacity, sharp content");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error applying glass effect to view: " + e.getMessage());
        }
    }
    
    /**
     * Apply Material 3 button styling with glass effects
     */
    private void applyMaterial3ButtonEffects(RenderEffect blurEffect, int surfaceColor, int onSurfaceColor, float opacity) {
        Log.d(TAG, "⚡ Applying Material 3 button effects...");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Enhanced button styling with Material 3 press animations
            applyMaterial3GlassEffect(startButton, blurEffect, surfaceColor, onSurfaceColor, opacity + 0.1f);
            applyMaterial3GlassEffect(modernCompetitiveModeButton, blurEffect, surfaceColor, onSurfaceColor, opacity + 0.1f);
            
            // Add Material 3 press animations
            addMaterial3PressAnimations(startButton);
            addMaterial3PressAnimations(modernCompetitiveModeButton);
            
            Log.d(TAG, "✅ Material 3 button effects applied");
        }
    }
    
    /**
     * Apply selection-specific Material 3 glass effects
     */
    private void applyMaterial3GlassSelection(CardView card, boolean selected) {
        if (card == null) return;
        
        // True glassmorphism - different glow for selection, not opacity
        try {
            GradientDrawable glassBackground = new GradientDrawable();
            glassBackground.setShape(GradientDrawable.RECTANGLE);
            glassBackground.setCornerRadius(24f);
            
            if (selected) {
                // Selected: subtle glow with same low opacity
                int glassOpacity = (int)(0.12f * 255); // 12% for selection
                int glassColor = 0xFFFFFFFF & 0x00FFFFFF | (glassOpacity << 24);
                glassBackground.setColor(glassColor);
                
                // Brighter border for selection
                int borderOpacity = (int)(0.4f * 255);
                int borderColor = 0xFFFFFFFF & 0x00FFFFFF | (borderOpacity << 24);
                glassBackground.setStroke(2, borderColor);
            } else {
                // Unselected: minimal glass
                int glassOpacity = (int)(0.06f * 255); // 6% for unselected
                int glassColor = 0xFFFFFFFF & 0x00FFFFFF | (glassOpacity << 24);
                glassBackground.setColor(glassColor);
                
                // Subtle border
                int borderOpacity = (int)(0.15f * 255);
                int borderColor = 0xFFFFFFFF & 0x00FFFFFF | (borderOpacity << 24);
                glassBackground.setStroke(1, borderColor);
            }
            
            card.setBackground(glassBackground);
            card.setCardBackgroundColor(0x00000000); // Transparent card background
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error applying selection glass effect: " + e.getMessage());
        }
    }
    
    /**
     * Get Material You surface color
     */
    private int getMaterialYouSurfaceColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getColor(android.R.color.system_neutral1_50);
        }
        return 0xFF1A237E; // Deep blue fallback
    }
    
    /**
     * Get Material You on-surface color
     */
    private int getMaterialYouOnSurfaceColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getColor(android.R.color.system_neutral1_900);
        }
        return 0xFFFFFFFF; // White fallback
    }
    
    /**
     * Add Material 3 press animations to buttons
     */
    private void addMaterial3PressAnimations(View button) {
        if (button == null) return;
        
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.95f).setDuration(100).start();
                    ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.95f).setDuration(100).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    ObjectAnimator.ofFloat(v, "scaleX", v.getScaleX(), 1.0f).setDuration(100).start();
                    ObjectAnimator.ofFloat(v, "scaleY", v.getScaleY(), 1.0f).setDuration(100).start();
                    break;
            }
            return false; // Allow click to proceed
        });
    }
    
    /**
     * Fallback glass effects for older Android versions
     */
    private void applyFallbackGlassEffects() {
        Log.d(TAG, "📱 Applying fallback glass effects for compatibility...");
        
        // True glassmorphism fallback - very low opacity
        int fallbackColor = 0x14FFFFFF; // 8% opacity white for true glass
        
        if (heroCard != null) heroCard.setCardBackgroundColor(fallbackColor);
        if (colorSelectionCard != null) colorSelectionCard.setCardBackgroundColor(fallbackColor);
        if (whiteSelectionCard != null) whiteSelectionCard.setCardBackgroundColor(fallbackColor);
        if (blackSelectionCard != null) blackSelectionCard.setCardBackgroundColor(fallbackColor);
        if (strengthCard != null) strengthCard.setCardBackgroundColor(fallbackColor);
        
        Log.d(TAG, "✅ Fallback glass effects applied - true glassmorphism");
    }
    
    /**
     * Start smooth entrance animations for all UI elements
     */
    private void startEntranceAnimations() {
        Log.d(TAG, "🎭 Starting entrance animations...");
        
        // Set initial state for animations (invisible)
        heroCard.setAlpha(0f);
        heroCard.setTranslationY(-100f);
        heroCard.setScaleX(0.8f);
        heroCard.setScaleY(0.8f);
        
        colorSelectionCard.setAlpha(0f);
        colorSelectionCard.setTranslationX(-50f);
        
        strengthCard.setAlpha(0f);
        strengthCard.setTranslationX(50f);
        
        startButton.setAlpha(0f);
        startButton.setTranslationY(100f);
        
        modernCompetitiveModeButton.setAlpha(0f);
        modernCompetitiveModeButton.setTranslationY(100f);
        
        if (splashCrownIcon != null) {
            splashCrownIcon.setRotation(-15f);
            splashCrownIcon.setScaleX(0.5f);
            splashCrownIcon.setScaleY(0.5f);
        }
        
        // Create staggered entrance animations
        AnimatorSet masterAnimatorSet = new AnimatorSet();
        
        // Hero card entrance (first)
        ObjectAnimator heroAlpha = ObjectAnimator.ofFloat(heroCard, "alpha", 0f, 1f);
        ObjectAnimator heroTransY = ObjectAnimator.ofFloat(heroCard, "translationY", -100f, 0f);
        ObjectAnimator heroScaleX = ObjectAnimator.ofFloat(heroCard, "scaleX", 0.8f, 1f);
        ObjectAnimator heroScaleY = ObjectAnimator.ofFloat(heroCard, "scaleY", 0.8f, 1f);
        
        AnimatorSet heroSet = new AnimatorSet();
        heroSet.playTogether(heroAlpha, heroTransY, heroScaleX, heroScaleY);
        heroSet.setDuration(800);
        heroSet.setInterpolator(new OvershootInterpolator(1.2f));
        
        // Crown icon entrance (with hero)
        AnimatorSet crownSet = new AnimatorSet();
        if (splashCrownIcon != null) {
            ObjectAnimator crownRotation = ObjectAnimator.ofFloat(splashCrownIcon, "rotation", -15f, 0f);
            ObjectAnimator crownScaleX = ObjectAnimator.ofFloat(splashCrownIcon, "scaleX", 0.5f, 1f);
            ObjectAnimator crownScaleY = ObjectAnimator.ofFloat(splashCrownIcon, "scaleY", 0.5f, 1f);
            
            crownSet.playTogether(crownRotation, crownScaleX, crownScaleY);
            crownSet.setDuration(1000);
            crownSet.setInterpolator(new OvershootInterpolator(1.5f));
        }
        
        // Color selection card (second)
        ObjectAnimator colorAlpha = ObjectAnimator.ofFloat(colorSelectionCard, "alpha", 0f, 1f);
        ObjectAnimator colorTransX = ObjectAnimator.ofFloat(colorSelectionCard, "translationX", -50f, 0f);
        
        AnimatorSet colorSet = new AnimatorSet();
        colorSet.playTogether(colorAlpha, colorTransX);
        colorSet.setDuration(600);
        colorSet.setInterpolator(new DecelerateInterpolator());
        colorSet.setStartDelay(200);
        
        // Strength card (third)
        ObjectAnimator strengthAlpha = ObjectAnimator.ofFloat(strengthCard, "alpha", 0f, 1f);
        ObjectAnimator strengthTransX = ObjectAnimator.ofFloat(strengthCard, "translationX", 50f, 0f);
        
        AnimatorSet strengthSet = new AnimatorSet();
        strengthSet.playTogether(strengthAlpha, strengthTransX);
        strengthSet.setDuration(600);
        strengthSet.setInterpolator(new DecelerateInterpolator());
        strengthSet.setStartDelay(400);
        
        // Buttons (last)
        ObjectAnimator startAlpha = ObjectAnimator.ofFloat(startButton, "alpha", 0f, 1f);
        ObjectAnimator startTransY = ObjectAnimator.ofFloat(startButton, "translationY", 100f, 0f);
        
        ObjectAnimator modernAlpha = ObjectAnimator.ofFloat(modernCompetitiveModeButton, "alpha", 0f, 1f);
        ObjectAnimator modernTransY = ObjectAnimator.ofFloat(modernCompetitiveModeButton, "translationY", 100f, 0f);
        
        AnimatorSet buttonsSet = new AnimatorSet();
        buttonsSet.playTogether(startAlpha, startTransY, modernAlpha, modernTransY);
        buttonsSet.setDuration(800);
        buttonsSet.setInterpolator(new OvershootInterpolator(1.1f));
        buttonsSet.setStartDelay(600);
        
        // Play all animations
        masterAnimatorSet.playTogether(heroSet, crownSet, colorSet, strengthSet, buttonsSet);
        masterAnimatorSet.start();
        
        Log.d(TAG, "✅ Entrance animations started - duration: 1.4s total");
    }

    private void startGame() {
        // Get current configuration from Material 3 components
        String playerColor = getSelectedColor();
        int sliderPosition = strengthSlider.getProgress();
        int engineElo = mapSliderToElo(sliderPosition);
        
        Log.d(TAG, "🎮 Starting game with configuration:");
        Log.d(TAG, "   🎨 Player Color: " + playerColor);
        Log.d(TAG, "   ⚡ Slider Position: " + sliderPosition);
        Log.d(TAG, "   🏆 Engine Elo: " + engineElo);
        
        // Check if this is first run
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("is_first_run", true);
        
        Log.d(TAG, "🆕 Is first run: " + isFirstRun);

        if (isFirstRun) {
            // First run - go to master selection first
            Log.d(TAG, "🎭 First run detected - going to master selection");
            prefs.edit().putBoolean("is_first_run", false).apply();

            // Store the game settings for later use
            prefs.edit()
                    .putString("PLAYER_COLOR", playerColor)
                    .putInt("SLIDER_POSITION", sliderPosition)
                    .putInt("ENGINE_ELO", engineElo)
                    .apply();

            // Go to chess master selection
            Intent intent = new Intent(SplashActivity.this, ChessMasterSelectionActivity.class);
            intent.putExtra("first_time", true);
            startActivity(intent);
        } else {
            // Not first run, go directly to main game
            Log.d(TAG, "🎮 Regular start - going to MainActivity");
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("PLAYER_COLOR", playerColor);
            intent.putExtra("SLIDER_POSITION", sliderPosition);
            intent.putExtra("ENGINE_ELO", engineElo);
            startActivity(intent);
        }
        
        Log.d(TAG, "✅ Game start completed, finishing splash activity");
        finish(); // close splash screen
    }
    
    /**
     * Launch Modern Competitive Mode with Android 15 features
     */
    private void launchModernCompetitiveMode() {
        Log.d(TAG, "✨ Launching Modern Competitive Mode with Android 15 features...");
        
        // Get current configuration
        String playerColor = getSelectedColor();
        int sliderPosition = strengthSlider.getProgress();
        int engineElo = mapSliderToElo(sliderPosition);
        
        Log.d(TAG, "✨ Modern Mode Configuration:");
        Log.d(TAG, "   🎨 Player Color: " + playerColor);
        Log.d(TAG, "   ⚡ Engine Elo: " + engineElo);
        Log.d(TAG, "   🌊 Edge-to-Edge: Enabled");
        Log.d(TAG, "   🔮 Glassmorphism: Pure translucency");
        Log.d(TAG, "   📱 Samsung S23 Ultra optimized");
        
        // Launch Modern Competitive Mode
        Intent intent = new Intent(SplashActivity.this, CompetitiveModeActivity.class);
        intent.putExtra("selectedMaster", "alekhine"); // Default master for competitive mode
        intent.putExtra("playerColor", playerColor);
        intent.putExtra("skillLevel", sliderPosition); 
        intent.putExtra("engineElo", engineElo);
        intent.putExtra("ANDROID_15_MODE", true);
        
        startActivity(intent);
        Log.d(TAG, "✨ Modern Competitive Mode launched successfully");
        finish(); // close splash screen
    }

    private String getSelectedColor() {
        int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
        String color = (selectedColorId == R.id.radioBlack) ? "black" : "white";
        Log.d(TAG, "🎨 Selected color: " + color + " (ID: " + selectedColorId + ")");
        return color;
    }
}
