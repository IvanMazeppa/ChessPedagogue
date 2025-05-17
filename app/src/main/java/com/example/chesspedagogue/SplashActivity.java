package com.example.chesspedagogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private RadioGroup colorRadioGroup;
    private SeekBar strengthSeekBar;
    private TextView strengthValueTextView;
    private Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find UI elements
        colorRadioGroup = findViewById(R.id.radioGroupColor);
        strengthSeekBar = findViewById(R.id.seekBarStrength);
        strengthValueTextView = findViewById(R.id.textViewStrengthValue);
        startGameButton = findViewById(R.id.buttonStartGame);

        // Configure engine strength slider (0 = weakest, 20 = strongest)
        strengthSeekBar.setMax(20);
        strengthSeekBar.setProgress(10);  // default mid-level

        // Show initial strength value with more appropriate Elo calculation
        int initialSkill = strengthSeekBar.getProgress();
        int initialElo = 300 + initialSkill * 145; // Ranges from 300 to 3200
        strengthValueTextView.setText("Engine Strength: ~" + initialElo + " Elo (Level " + initialSkill + ")");

        // Update displayed strength as the user adjusts the slider
        strengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int approxElo = 300 + progress * 145; // Lower minimum, better spread
                strengthValueTextView.setText("Engine Strength: ~" + approxElo + " Elo (Level " + progress + ")");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Start Game button launches the main game activity
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if this is first run
                SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
                boolean isFirstRun = prefs.getBoolean("is_first_run", true);

                if (isFirstRun) {
                    // First run - go to master selection first
                    prefs.edit().putBoolean("is_first_run", false).apply();

                    // Store the game settings for later use
                    String playerColor = "white";
                    int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
                    if (selectedColorId == R.id.radioBlack) {
                        playerColor = "black";
                    }
                    int skillLevel = strengthSeekBar.getProgress();
                    int engineElo = 300 + skillLevel * 145;

                    // Save these settings
                    prefs.edit()
                            .putString("PLAYER_COLOR", playerColor)
                            .putInt("SKILL_LEVEL", skillLevel)
                            .putInt("ENGINE_ELO", engineElo)
                            .apply();

                    // Go to chess master selection
                    Intent intent = new Intent(SplashActivity.this, ChessMasterSelectionActivity.class);
                    intent.putExtra("first_time", true);
                    startActivity(intent);
                } else {
                    // Not first run, proceed as normal
                    String playerColor = "white";
                    int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
                    if (selectedColorId == R.id.radioBlack) {
                        playerColor = "black";
                    }
                    int skillLevel = strengthSeekBar.getProgress();
                    int engineElo = 300 + skillLevel * 145;

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("PLAYER_COLOR", playerColor);
                    intent.putExtra("SKILL_LEVEL", skillLevel);
                    intent.putExtra("ENGINE_ELO", engineElo);
                    startActivity(intent);
                }
                finish(); // close splash screen
            }
        });


    }


    // In your launcher activity
    private void proceedToNextScreen() {
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("is_first_run", true);

        if (isFirstRun) {
            // First run - go to master selection
            prefs.edit().putBoolean("is_first_run", false).apply();
            Intent intent = new Intent(this, ChessMasterSelectionActivity.class);
            intent.putExtra("first_time", true);
            startActivity(intent);
        } else {
            // Normal flow - go to main activity
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

}
