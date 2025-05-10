package com.example.chesspedagogue;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Settings activity for ChessPedagogue app, allowing users to customize
 * the AI model, voice, and other preferences.
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*
        // Set up the action bar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.settings_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        } */

        // Get references to UI components
        Switch premiumVoiceSwitch = findViewById(R.id.switch_premium_voice);
        RadioGroup voiceGroup = findViewById(R.id.radio_group_voice);
        RadioGroup modelGroup = findViewById(R.id.radio_group_model);
        Button testButton = findViewById(R.id.button_test_voice);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);

        // Set initial state from preferences
        boolean usePremiumVoice = prefs.getBoolean("use_premium_voice", true);
        premiumVoiceSwitch.setChecked(usePremiumVoice);

        // Set up voice selection
        String voicePersona = prefs.getString("voice_persona", OpenAITTSService.VOICE_GRANDMASTER);

        // Set the correct radio button based on saved preference
        if (voicePersona.equals(OpenAITTSService.VOICE_TUTOR)) {
            voiceGroup.check(R.id.radio_voice_tutor);
        } else if (voicePersona.equals(OpenAITTSService.VOICE_ALLOY)) {
            voiceGroup.check(R.id.radio_voice_friendly);
        } else if (voicePersona.equals(OpenAITTSService.VOICE_SHIMMER)) {
            voiceGroup.check(R.id.radio_voice_neutral);
        } else {
            voiceGroup.check(R.id.radio_voice_grandmaster);
        }

        // Set up listeners
        voiceGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedVoice;
            if (checkedId == R.id.radio_voice_tutor) {
                selectedVoice = OpenAITTSService.VOICE_TUTOR;
            } else if (checkedId == R.id.radio_voice_friendly) {
                selectedVoice = OpenAITTSService.VOICE_ALLOY;
            } else if (checkedId == R.id.radio_voice_neutral) {
                selectedVoice = OpenAITTSService.VOICE_SHIMMER;
            } else {
                selectedVoice = OpenAITTSService.VOICE_GRANDMASTER;
            }

            // Save the selection
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("voice_persona", selectedVoice);
            editor.apply();

            // Update the coach
            ChessCoachManager.getInstance(this).setVoicePersona(selectedVoice);

            Toast.makeText(this, "Voice set to: " + selectedVoice, Toast.LENGTH_SHORT).show();
        });

        // Premium voice switch listener
        premiumVoiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("use_premium_voice", isChecked);
            editor.apply();

            ChessCoachManager.getInstance(this).setUseOpenAIVoice(isChecked);
        });

        // Test button
        testButton.setOnClickListener(v -> {
            String testPhrase = "Hello, I'm your chess coach. Let me help you improve your game.";
            ChessCoachManager coach = ChessCoachManager.getInstance(this);
            coach.testVoice(testPhrase, new ChessCoachManager.ChessCoachCallback() {
                @Override
                public void onResponseReceived(String response) {
                    // Not used for voice test
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(SettingsActivity.this,
                            "Voice test failed: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSpeechCompleted() {
                    Toast.makeText(SettingsActivity.this,
                            "Voice test complete!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Set up model quality selection (if your layout has this)
        if (modelGroup != null) {
            String model = prefs.getString("tts_model", OpenAITTSService.MODEL_STANDARD);
            if (OpenAITTSService.MODEL_PREMIUM.equals(model)) {
                modelGroup.check(R.id.radio_model_premium);
            } else {
                modelGroup.check(R.id.radio_model_standard);
            }

            modelGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String selectedModel = checkedId == R.id.radio_model_premium ?
                        OpenAITTSService.MODEL_PREMIUM : OpenAITTSService.MODEL_STANDARD;

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("tts_model", selectedModel);
                editor.apply();

                ChessCoachManager.getInstance(this).setTTSModel(selectedModel);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Simply finish this activity to return to the previous one
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}