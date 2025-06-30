package com.example.chesspedagogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private RadioGroup colorRadioGroup;
    private RadioButton radioWhite;
    private RadioButton radioBlack;
    private SeekBar strengthSeekBar;
    private TextView strengthValueTextView;
    private Button startGameButton;
    private LinearLayout whiteSelectionLayout;
    private LinearLayout blackSelectionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "🚀 SplashActivity onCreate starting...");
        
        setContentView(R.layout.activity_splash);

        // Initialize UI elements
        initializeViews();
        
        // Setup functionality
        setupColorSelection();
        setupStrengthSlider();
        setupStartButton();
        
        // Set initial state - ensure white is selected by default
        colorRadioGroup.check(R.id.radioWhite);
        updateSelectionVisuals();
        
        Log.d(TAG, "✅ SplashActivity initialization completed");
    }

    private void initializeViews() {
        Log.d(TAG, "🔍 Initializing views...");
        
        colorRadioGroup = findViewById(R.id.radioGroupColor);
        radioWhite = findViewById(R.id.radioWhite);
        radioBlack = findViewById(R.id.radioBlack);
        strengthSeekBar = findViewById(R.id.seekBarStrength);
        strengthValueTextView = findViewById(R.id.textViewStrengthValue);
        startGameButton = findViewById(R.id.buttonStartGame);
        whiteSelectionLayout = findViewById(R.id.whiteSelectionLayout);
        blackSelectionLayout = findViewById(R.id.blackSelectionLayout);
        
        // Verify all views found
        boolean allViewsFound = 
            colorRadioGroup != null && radioWhite != null && radioBlack != null &&
            strengthSeekBar != null && strengthValueTextView != null && 
            startGameButton != null && whiteSelectionLayout != null && blackSelectionLayout != null;
            
        Log.d(TAG, allViewsFound ? "✅ All views found successfully" : "❌ Some views not found!");
    }

    private void setupColorSelection() {
        Log.d(TAG, "🎨 Setting up color selection...");
        
        // Set up click listeners for the selection areas
        whiteSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "🤴 WHITE selection area clicked!");
                selectWhite();
            }
        });

        blackSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "♛ BLACK selection area clicked!");
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
        Log.d(TAG, "🎨 Updating visuals for selected ID: " + selectedColorId + " (" + selectedName + ")");
        
        if (selectedColorId == R.id.radioWhite) {
            whiteSelectionLayout.setBackgroundResource(R.drawable.side_selection_active_bg);
            blackSelectionLayout.setBackgroundResource(R.drawable.side_selection_bg);
            Log.d(TAG, "🎨 Visual feedback: WHITE highlighted");
        } else if (selectedColorId == R.id.radioBlack) {
            blackSelectionLayout.setBackgroundResource(R.drawable.side_selection_active_bg);
            whiteSelectionLayout.setBackgroundResource(R.drawable.side_selection_bg);
            Log.d(TAG, "🎨 Visual feedback: BLACK highlighted");
        } else {
            Log.w(TAG, "⚠️ No valid selection found - keeping current state");
        }
    }

    private void setupStrengthSlider() {
        Log.d(TAG, "⚡ Setting up strength slider...");
        
        // Configure slider - expand range for better granularity
        strengthSeekBar.setMax(24);  // Increased from 20 to support more levels
        strengthSeekBar.setProgress(12);  // default mid-level (2200 Elo)
        
        // Update display
        updateStrengthDisplay(12);
        
        // Listen for changes
        strengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateStrengthDisplay(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        Log.d(TAG, "✅ Strength slider setup completed");
    }

    private void updateStrengthDisplay(int sliderPosition) {
        // Map slider position to actual Elo ratings with better distribution
        int targetElo = mapSliderToElo(sliderPosition);
        String levelName = getStrengthLevelName(targetElo);
        String strengthText = "Engine Strength: " + targetElo + " Elo (" + levelName + ")";
        strengthValueTextView.setText(strengthText);
        Log.d(TAG, "⚡ Strength updated: " + strengthText);
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
        Log.d(TAG, "▶️ Setting up start button...");
        
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "▶️ START GAME button clicked!");
                startGame();
            }
        });
        
        Log.d(TAG, "✅ Start button setup completed");
    }

    private void startGame() {
        // Get current configuration
        String playerColor = getSelectedColor();
        int sliderPosition = strengthSeekBar.getProgress();
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

    private String getSelectedColor() {
        int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
        String color = (selectedColorId == R.id.radioBlack) ? "black" : "white";
        Log.d(TAG, "🎨 Selected color: " + color + " (ID: " + selectedColorId + ")");
        return color;
    }
}
