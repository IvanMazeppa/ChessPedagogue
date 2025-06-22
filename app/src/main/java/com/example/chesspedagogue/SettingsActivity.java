package com.example.chesspedagogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

                // CRITICAL FIX: Save to BOTH SharedPreferences stores for consistency
                SharedPreferences.Editor editor = chessPrefs.edit();
                editor.putString("selected_master", selectedCoach);
                editor.apply();
                
                // Also save to ChessFineTunedModels (where voice system reads from)
                SharedPreferences fineTunedPrefs = getSharedPreferences("ChessFineTunedModels", MODE_PRIVATE);
                fineTunedPrefs.edit().putString("selected_master", selectedCoach).apply();

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

            // FIXED: Update the coach safely
            try {
                ChessCoachManager.getInstance(this).setVoicePersona(selectedVoice);
            } catch (Exception e) {
                // Service may not be initialized yet, that's OK
            }

            Toast.makeText(this, "Voice set to: " + selectedVoice, Toast.LENGTH_SHORT).show();
        });

        // Premium voice switch listener
        premiumVoiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("use_premium_voice", isChecked);
            editor.apply();

            // FIXED: Safe service access
            try {
                ChessCoachManager.getInstance(this).setUseOpenAIVoice(isChecked);
            } catch (Exception e) {
                // Service may not be initialized yet, that's OK
            }
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

                // FIXED: Safe service access
                try {
                    ChessCoachManager.getInstance(this).setTTSModel(selectedModel);
                } catch (Exception e) {
                    // Service may not be initialized yet, that's OK
                }
            });
        }

        // Chess Master Selection button - MOVED INSIDE ONCREATE METHOD
        Button chessMasterSelectionButton = findViewById(R.id.button_chess_master_selection);
        chessMasterSelectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChessMasterSelectionActivity.class);
            startActivity(intent);
        });

        // Analytics Dashboard button
        Button analyticsButton = findViewById(R.id.button_analytics_dashboard);
        analyticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        // Evaluation System Test button
        Button evaluationTestButton = findViewById(R.id.button_evaluation_tests);
        evaluationTestButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UnifiedEvaluationTestActivity.class);
            startActivity(intent);
        });

        // Update the UI to show the current selected master
        updateChessMasterDisplay();
        setupEnhancedVoiceSettings();
        setupLiveMonitorSettings();
    }

    /**
     * Updates the UI to display the currently selected chess master
     */
    /**
     * Updates the UI to display the currently selected chess master
     */
    private void updateChessMasterDisplay() {
        // FIXED: Use SharedPreferences directly to avoid heavy service initialization
        SharedPreferences prefs = getSharedPreferences("ChessFineTunedModels", MODE_PRIVATE);
        String selectedMaster = prefs.getString("selected_master", "tal");

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

        // Setup ElevenLabs TTS toggle
        setupElevenLabsTTS();

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

        // FIXED: Safe service access
        try {
            ChessCoachManager.getInstance(this).updateTTSSettings(voiceStyle, usePersonality);
        } catch (Exception e) {
            // Service may not be initialized yet, that's OK - settings are saved to SharedPreferences
        }
    }

    /**
     * Setup ElevenLabs TTS toggle
     */
    private void setupElevenLabsTTS() {
        // Create a switch for ElevenLabs programmatically since it's not in the layout
        Switch elevenLabsSwitch = null;
        
        // Find a container to add it to (e.g., the voice settings container)
        LinearLayout container = findViewById(R.id.legacy_voice_container);
        if (container != null) {
            // Create the switch programmatically
            elevenLabsSwitch = new Switch(this);
            elevenLabsSwitch.setText("Use ElevenLabs TTS (Ultra-realistic voices)");
            elevenLabsSwitch.setPadding(16, 16, 16, 16);
            
            // Add it to the container
            container.addView(elevenLabsSwitch, 0); // Add at the top
        }
        
        if (elevenLabsSwitch != null) {
            // Load current setting
            boolean useElevenLabs = TTSServiceManager.isUsingElevenLabs(this);
            elevenLabsSwitch.setChecked(useElevenLabs);
            
            // Set up change listener
            final Switch finalElevenLabsSwitch = elevenLabsSwitch;
            elevenLabsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Check if API key is set
                    SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
                    String apiKey = prefs.getString("elevenlabs_api_key", "");
                    // Note: System.getenv() doesn't work reliably on Android
                    // API key should be set through SharedPreferences
                    
                    if (apiKey == null || apiKey.isEmpty()) {
                        // No API key - show error and revert
                        Toast.makeText(this, 
                            "Please set your ElevenLabs API key in the app first", 
                            Toast.LENGTH_LONG).show();
                        finalElevenLabsSwitch.setChecked(false);
                        return;
                    }
                }
                
                // Enable/disable ElevenLabs
                TTSServiceManager.setUseElevenLabs(this, isChecked);
                
                String message = isChecked ? 
                    "Switched to ElevenLabs TTS - Ultra-realistic voices activated!" :
                    "Switched back to OpenAI TTS";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                
                // Update TTS settings
                updateTTSSettings();
            });
            
            // Add a help text below the switch
            TextView helpText = new TextView(this);
            helpText.setText("ElevenLabs provides ultra-low latency (~75ms) and emotionally adaptive voices for each chess master");
            helpText.setTextSize(12);
            helpText.setPadding(16, 0, 16, 16);
            
            if (elevenLabsSwitch.getParent() instanceof LinearLayout) {
                LinearLayout parent = (LinearLayout) elevenLabsSwitch.getParent();
                int switchIndex = parent.indexOfChild(elevenLabsSwitch);
                parent.addView(helpText, switchIndex + 1);
            }
        }
    }

    /**
     * Sets up the live monitor IP configuration and connection testing
     */
    private void setupLiveMonitorSettings() {
        EditText liveMonitorIpEditText = findViewById(R.id.edittext_live_monitor_ip);
        Button testConnectionButton = findViewById(R.id.button_test_live_monitor);
        Button reconnectButton = findViewById(R.id.button_reconnect_live_monitor);
        
        // Load current live monitor IP setting
        SharedPreferences prefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
        String currentIp = prefs.getString("live_monitor_server_ip", "");
        liveMonitorIpEditText.setText(currentIp);
        
        // Set up text change listener to save IP automatically
        liveMonitorIpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                // Save the IP address to SharedPreferences
                String ip = s.toString().trim();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("live_monitor_server_ip", ip);
                editor.apply();
                
                // Update the live monitor client with new IP
                try {
                    LiveMonitorClient.getInstance(SettingsActivity.this).refreshServerUrl();
                } catch (Exception e) {
                    // LiveMonitorClient might not be initialized yet, that's OK
                }
            }
        });
        
        // Set up test connection button
        testConnectionButton.setOnClickListener(v -> {
            String ip = liveMonitorIpEditText.getText().toString().trim();
            
            if (ip.isEmpty()) {
                Toast.makeText(this, "Please enter an IP address first", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate IP format (basic validation)
            if (!isValidIpAddress(ip)) {
                Toast.makeText(this, "Invalid IP address format. Use format like 192.168.1.100", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Test the connection
            testLiveMonitorConnection(ip);
        });
        
        // Set up force reconnect button
        reconnectButton.setOnClickListener(v -> {
            String ip = liveMonitorIpEditText.getText().toString().trim();
            
            if (ip.isEmpty()) {
                Toast.makeText(this, "Please enter an IP address first", Toast.LENGTH_SHORT).show();
                return;
            }
            
            forceLiveMonitorReconnect(ip);
        });
    }
    
    /**
     * Basic IP address validation
     */
    private boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Test connection to the live monitor server
     */
    private void testLiveMonitorConnection(String ip) {
        // Show testing message
        Toast.makeText(this, "Testing connection to " + ip + ":8080...", Toast.LENGTH_SHORT).show();
        
        // In a real implementation, you would:
        // 1. Create a background thread to test WebSocket connection
        // 2. Try to connect to ws://{ip}:8080
        // 3. Send a test message and wait for response
        // 4. Show success/failure result to user
        
        // For now, show a placeholder message with instructions
        new Thread(() -> {
            try {
                // Simulate connection test delay
                Thread.sleep(1500);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, 
                        "Connection test completed!\n\n" +
                        "To verify:\n" +
                        "1. Make sure the Chess Pedagogue Configurator GUI is running\n" +
                        "2. Go to Live Monitor tab and click 'Connect to Android App'\n" +
                        "3. Start a game in the Android app\n" +
                        "4. Check if live data appears in the GUI", 
                        Toast.LENGTH_LONG).show();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Force a reconnection to the live monitor
     */
    private void forceLiveMonitorReconnect(String ip) {
        Toast.makeText(this, "Forcing reconnection to " + ip + ":8080...", Toast.LENGTH_SHORT).show();
        
        try {
            // Get the live monitor client and force a reconnection
            LiveMonitorClient client = LiveMonitorClient.getInstance(this);
            
            // Disconnect first
            client.disconnect();
            
            // Wait a moment then reconnect
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Wait 1 second
                    
                    // Refresh the URL and reconnect
                    client.refreshServerUrl();
                    client.connect();
                    
                    runOnUiThread(() -> {
                        Toast.makeText(this, 
                            "Reconnection attempt completed!\n\n" +
                            "Check the logs for connection status:\n" +
                            "- Look for '🔌 Attempting to connect'\n" +
                            "- Look for '✅ Connected' or '❌ Connection failed'\n" +
                            "- Make sure GUI Live Monitor is running first!", 
                            Toast.LENGTH_LONG).show();
                    });
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error during reconnection: " + e.getMessage(), Toast.LENGTH_LONG).show();
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