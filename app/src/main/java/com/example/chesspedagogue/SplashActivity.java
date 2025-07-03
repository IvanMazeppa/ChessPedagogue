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
import com.example.chesspedagogue.ui.AdvancedGlassEffects;
import android.animation.ValueAnimator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

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
        Log.d(TAG, "🚀 Modern SplashActivity with Android 15 features starting...");
        
        // Set modern glassmorphic layout
        setContentView(R.layout.activity_splash_modern);
        
        // Enable edge-to-edge immersive experience for Android 15
        setupEdgeToEdgeDisplay();
        
        // Initialize Material 3 UI elements
        initializeViews();
        
        // Apply glassmorphism effects for Samsung S23 Ultra (after layout is complete)
        findViewById(android.R.id.content).post(() -> {
            applyGlassmorphismEffects();
            startEntranceAnimations();
        });
        
        // Setup functionality
        setupColorSelection();
        setupStrengthSlider();
        setupStartButton();
        
        // Set initial state - ensure white is selected by default
        colorRadioGroup.check(R.id.radioWhite);
        updateSelectionVisuals();
        
        Log.d(TAG, "✅ Modern SplashActivity initialization completed with glassmorphism");
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
            // Highlight white card with enhanced glass effect
            whiteSelectionCard.setCardElevation(16f);
            
            // Enhanced glass effect for selected card - Blue-teal theme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WorkingGlassEffects.applyWorkingGlass(whiteSelectionCard, 15f, 0.9f, new float[]{0.8f, 0.9f, 1.0f});
            }
            
            // Reset black card
            blackSelectionCard.setCardElevation(8f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WorkingGlassEffects.applyWorkingGlass(blackSelectionCard, 10f, 0.75f, new float[]{0.3f, 0.4f, 0.6f});
            }
            
            // Add selection animation
            animateCardSelection(whiteSelectionCard, true);
            animateCardSelection(blackSelectionCard, false);
            
            Log.d(TAG, "🎨 Enhanced glass feedback: WHITE card highlighted with shimmer");
        } else if (selectedColorId == R.id.radioBlack) {
            // Highlight black card with enhanced glass effect
            blackSelectionCard.setCardElevation(16f);
            
            // Enhanced glass effect for selected card - Blue-teal theme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WorkingGlassEffects.applyWorkingGlass(blackSelectionCard, 15f, 0.9f, new float[]{0.2f, 0.3f, 0.5f});
            }
            
            // Reset white card
            whiteSelectionCard.setCardElevation(8f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WorkingGlassEffects.applyWorkingGlass(whiteSelectionCard, 10f, 0.75f, new float[]{0.8f, 0.9f, 1.0f});
            }
            
            // Add selection animation
            animateCardSelection(blackSelectionCard, true);
            animateCardSelection(whiteSelectionCard, false);
            
            Log.d(TAG, "🎨 Enhanced glass feedback: BLACK card highlighted with shimmer");
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
     * Apply glassmorphism effects for Android 15 - Enhanced Implementation
     * Advanced visual effects optimized for Samsung S23 Ultra
     */
    private void applyGlassmorphismEffects() {
        Log.d(TAG, "✨ Applying enhanced glassmorphism with advanced effects...");
        
        try {
            // Apply working glass effects to all cards for enhanced visual appeal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(TAG, "🔮 Applying advanced glass effects to cards...");
                
                // Hero card with blue-teal glow
                WorkingGlassEffects.applyWorkingGlass(heroCard, 15f, 0.85f, new float[]{0.6f, 0.8f, 1.0f});
                
                // Color selection cards with blue-teal glass
                WorkingGlassEffects.applyWorkingGlass(colorSelectionCard, 12f, 0.8f, new float[]{0.7f, 0.85f, 0.95f});
                WorkingGlassEffects.applyWorkingGlass(whiteSelectionCard, 10f, 0.75f, new float[]{0.8f, 0.9f, 1.0f});
                WorkingGlassEffects.applyWorkingGlass(blackSelectionCard, 10f, 0.75f, new float[]{0.3f, 0.4f, 0.6f});
                
                // Strength card with blue-teal glass
                WorkingGlassEffects.applyWorkingGlass(strengthCard, 12f, 0.8f, new float[]{0.7f, 0.85f, 0.95f});
                
                // Enhanced crown icon with blue-teal entrance
                if (splashCrownIcon != null) {
                    WorkingGlassEffects.animateGlassEntrance(splashCrownIcon, 0.3f, new float[]{0.8f, 0.9f, 1.0f});
                }
                
                Log.d(TAG, "✅ Advanced glass effects applied successfully");
            } else {
                Log.d(TAG, "ℹ️ Glass effects not available on this Android version");
            }
            
            // Apply enhanced button effects
            applyButtonGlassEffects();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error applying glassmorphism effects", e);
        }
        
        Log.d(TAG, "✅ Enhanced glassmorphism system initialized:");
        Log.d(TAG, "   🔹 Advanced glass panels with dynamic tinting");
        Log.d(TAG, "   🔹 Shimmer effects on crown icon");
        Log.d(TAG, "   🔹 Interactive glass response to touches");
        Log.d(TAG, "   🔹 Samsung S23 Ultra 120Hz optimized");
    }
    
    /**
     * Apply special glass effects to action buttons
     */
    private void applyButtonGlassEffects() {
        Log.d(TAG, "⚡ Applying button glass effects...");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Enhanced start button with blue-teal glass
            WorkingGlassEffects.applyWorkingGlass(startButton, 18f, 0.9f, new float[]{0.4f, 0.7f, 1.0f});
            
            // Modern competitive mode button with teal accent
            WorkingGlassEffects.applyWorkingGlass(modernCompetitiveModeButton, 15f, 0.85f, new float[]{0.3f, 0.6f, 0.9f});
            
            Log.d(TAG, "✅ Button glass effects applied");
        }
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
        Intent intent = new Intent(SplashActivity.this, ModernCompetitiveModeActivity.class);
        intent.putExtra("PLAYER_COLOR", playerColor);
        intent.putExtra("SLIDER_POSITION", sliderPosition);
        intent.putExtra("ENGINE_ELO", engineElo);
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
