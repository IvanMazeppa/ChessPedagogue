package com.example.chesspedagogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
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


        // Get references to UI components
        Switch premiumVoiceSwitch = findViewById(R.id.switch_premium_voice);
        RadioGroup voiceGroup = findViewById(R.id.radio_group_voice);
        RadioGroup modelGroup = findViewById(R.id.radio_group_model);
        Button testButton = findViewById(R.id.button_test_voice);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);

        // Set initial state from preferences
        //boolean usePremiumVoice = prefs.getBoolean("use_premium_voice", true);
        //premiumVoiceSwitch.setChecked(usePremiumVoice);

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

        // Chess Coach selection
        RadioGroup coachGroup = findViewById(R.id.radio_group_coach);
        if (coachGroup != null) {
            // Load the current selection
            SharedPreferences chessPrefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
            String currentCoach = chessPrefs.getString("selected_master", "tal");

            // Set the appropriate radio button
            if ("kramnik".equals(currentCoach)) {
                coachGroup.check(R.id.radio_coach_kramnik);
            } else {
                coachGroup.check(R.id.radio_coach_tal);
            }

            // Set up change listener
            coachGroup.setOnCheckedChangeListener((group, checkedId) -> {


                String selectedCoach = checkedId == R.id.radio_coach_kramnik ? "kramnik" : "tal";

                // Save the selection
                SharedPreferences.Editor editor = chessPrefs.edit();
                editor.putString("selected_master", selectedCoach);
                editor.apply();

                // Show feedback to the user
                String coachName = selectedCoach.equals("tal") ? "Mikhail Tal" : "Vladimir Kramnik";
                Toast.makeText(this, "Chess coach changed to " + coachName, Toast.LENGTH_SHORT).show();

                // Update the coach in your app - if you have a coach manager
                // ChessCoachManager.getInstance(this).setCoachProfile(selectedCoach);
            });
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

        // Chess Master Selection button - MOVED INSIDE ONCREATE METHOD
        Button chessMasterSelectionButton = findViewById(R.id.button_chess_master_selection);
        chessMasterSelectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChessMasterSelectionActivity.class);
            startActivity(intent);
        });

        // Update the UI to show the current selected master
        updateChessMasterDisplay();
        setupEnhancedVoiceSettings();
    }

    /**
     * Updates the UI to display the currently selected chess master
     */
    /**
     * Updates the UI to display the currently selected chess master
     */
    private void updateChessMasterDisplay() {
        // Get the current selected master
        String selectedMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Get the display text view
        TextView chessMasterTextView = findViewById(R.id.text_selected_chess_master);

        // Set the display text based on the selection
        switch (selectedMaster.toLowerCase()) {
            case "tal":
                chessMasterTextView.setText("Current Coach: Mikhail Tal (The Magician from Riga)");
                break;
            case "kramnik":
                chessMasterTextView.setText("Current Coach: Vladimir Kramnik (The Berlin Wall)");
                break;
            case "karpov":
                chessMasterTextView.setText("Current Coach: Anatoly Karpov (The Positional Genius)");
                break;
            case "fischer":
                chessMasterTextView.setText("Current Coach: Bobby Fischer (The American Prodigy)");
                break;
            case "lasker":
                chessMasterTextView.setText("Current Coach: Emanuel Lasker (The Psychological Master)");
                break;
            case "kasparov":
                chessMasterTextView.setText("Current Coach: Garry Kasparov (The Dynamic Dominator)");
                break;
            case "capablanca":
                chessMasterTextView.setText("Current Coach: Jose Raul Capablanca (The Chess Machine)");
                break;
            case "carlsen":
                chessMasterTextView.setText("Current Coach: Magnus Carlsen (The Modern King)");
                break;
            case "morphy":
                chessMasterTextView.setText("Current Coach: Paul Morphy (The Pride and Sorrow of Chess)");
                break;
            case "anand":
                chessMasterTextView.setText("Current Coach: Viswanathan Anand (The Lightning Kid)");
                break;
            case "alekhine": // NEW: Add Alekhine case
                chessMasterTextView.setText("Current Coach: Alexander Alekhine (The Combinational Artist)");
                break;
            default:
                chessMasterTextView.setText("Current Coach: " + selectedMaster);
                break;
        }
    }

    // Add this to your SettingsActivity.java class


    /**
     * Set up the new voice style settings
     */
    private void setupEnhancedVoiceSettings() {
        // Get UI references
        TextView showAdvancedSettings = findViewById(R.id.show_advanced_settings);
        LinearLayout legacyCoachContainer = findViewById(R.id.legacy_coach_container);
        LinearLayout legacyVoiceContainer = findViewById(R.id.legacy_voice_container);

        // Advanced settings toggle
        showAdvancedSettings.setOnClickListener(v -> {
            boolean isVisible = legacyCoachContainer.getVisibility() == View.VISIBLE;
            int newVisibility = isVisible ? View.GONE : View.VISIBLE;

            legacyCoachContainer.setVisibility(newVisibility);
            legacyVoiceContainer.setVisibility(newVisibility);

            // Update text
            showAdvancedSettings.setText(isVisible ?
                    "Show Advanced Settings" : "Hide Advanced Settings");
        });

        // Now call the voice style settings setup
        setupVoiceStyleSettings();
    }

    /**
     * Sets up the voice style radio buttons and related settings
     */
    private void setupVoiceStyleSettings() {
        // Get reference to voice style radio group
        RadioGroup voiceStyleGroup = findViewById(R.id.radio_group_voice_style);

        // Get reference to personality switch
        Switch masterPersonalitySwitch = findViewById(R.id.switch_master_personality);

        // Load current preferences
        SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
        String currentVoiceStyle = prefs.getString("voice_style", "auto");
        boolean usePersonality = prefs.getBoolean("use_master_personality", true);

        // Set initial UI state
        masterPersonalitySwitch.setChecked(usePersonality);

        // Set the appropriate radio button
        switch (currentVoiceStyle) {
            case "onyx":
                voiceStyleGroup.check(R.id.radio_voice_onyx);
                break;
            case "echo":
                voiceStyleGroup.check(R.id.radio_voice_echo);
                break;
            case "fable":
                voiceStyleGroup.check(R.id.radio_voice_fable);
                break;
            case "alloy":
                voiceStyleGroup.check(R.id.radio_voice_alloy);
                break;
            case "nova":
                voiceStyleGroup.check(R.id.radio_voice_nova);
                break;
            case "shimmer":
                voiceStyleGroup.check(R.id.radio_voice_shimmer);
                break;
            default:
                voiceStyleGroup.check(R.id.radio_voice_auto);
                break;
        }

        // Set up change listener for voice style
        voiceStyleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedVoiceStyle;

            if (checkedId == R.id.radio_voice_onyx) {
                selectedVoiceStyle = "onyx";
            } else if (checkedId == R.id.radio_voice_echo) {
                selectedVoiceStyle = "echo";
            } else if (checkedId == R.id.radio_voice_fable) {
                selectedVoiceStyle = "fable";
            } else if (checkedId == R.id.radio_voice_alloy) {
                selectedVoiceStyle = "alloy";
            } else if (checkedId == R.id.radio_voice_nova) {
                selectedVoiceStyle = "nova";
            } else if (checkedId == R.id.radio_voice_shimmer) {
                selectedVoiceStyle = "shimmer";
            } else {
                selectedVoiceStyle = "auto";
            }

            // Save the selection
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("voice_style", selectedVoiceStyle);
            editor.apply();

            // Provide feedback
            String displayName = selectedVoiceStyle.equals("auto") ?
                    "Automatic (based on Chess Master)" :
                    selectedVoiceStyle.substring(0, 1).toUpperCase() + selectedVoiceStyle.substring(1);

            Toast.makeText(this, "Voice set to: " + displayName, Toast.LENGTH_SHORT).show();

            // Update the TTS service
            updateTTSSettings();
        });

        // Set up change listener for personality switch
        masterPersonalitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the setting
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("use_master_personality", isChecked);
            editor.apply();

            // Provide feedback
            String message = isChecked ?
                    "Chess master personality applied to voice" :
                    "Using natural voice without personality";

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // Update the TTS service
            updateTTSSettings();
        });
    }

    /**
     * Updates TTS-related settings in the app
     */
    private void updateTTSSettings() {
        SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
        String voiceStyle = prefs.getString("voice_style", "auto");
        boolean usePersonality = prefs.getBoolean("use_master_personality", true);

        // Update your TTS service
        ChessCoachManager.getInstance(this).updateTTSSettings(voiceStyle, usePersonality);
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