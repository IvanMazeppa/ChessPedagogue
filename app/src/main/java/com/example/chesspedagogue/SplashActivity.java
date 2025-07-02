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
        findViewById(android.R.id.content).post(() -> applyGlassmorphismEffects());
        
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
            // Highlight white card with elevation
            whiteSelectionCard.setCardElevation(12f);
            
            // Reset black card
            blackSelectionCard.setCardElevation(4f);
            
            Log.d(TAG, "🎨 Material Components visual feedback: WHITE card highlighted");
        } else if (selectedColorId == R.id.radioBlack) {
            // Highlight black card with elevation
            blackSelectionCard.setCardElevation(12f);
            
            // Reset white card
            whiteSelectionCard.setCardElevation(4f);
            
            Log.d(TAG, "🎨 Material Components visual feedback: BLACK card highlighted");
        } else {
            Log.w(TAG, "⚠️ No valid selection found - keeping current state");
        }
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
     * Apply glassmorphism effects for Android 15 - Fixed Implementation
     * Pure translucent backgrounds without RenderEffect blur for crisp content
     */
    private void applyGlassmorphismEffects() {
        Log.d(TAG, "✨ Applying proper glassmorphism - translucent panels with sharp content...");
        
        // The glassmorphism effect is achieved purely through:
        // 1. Translucent glass_surface backgrounds (already applied in XML)
        // 2. Rich gradient background showing through panels  
        // 3. NO RenderEffect blur on content (keeps text/icons sharp)
        
        Log.d(TAG, "✅ Glassmorphism achieved via translucent backgrounds:");
        Log.d(TAG, "   🔹 Glass panels: translucent with sharp content");
        Log.d(TAG, "   🔹 Background gradient: rich colors showing through");
        Log.d(TAG, "   🔹 Material 3 elevation: subtle shadows for depth");
        Log.d(TAG, "   🔹 Samsung S23 Ultra optimized: smooth performance");
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
