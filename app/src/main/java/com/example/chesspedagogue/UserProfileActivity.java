package com.example.chesspedagogue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 👤 User Profile Activity - Create and edit user profiles for AI relationship building
 * 
 * Allows users to create personalized profiles that AI chess masters can reference
 * in conversations, building authentic relationships and memories over time.
 */
public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";
    
    // UI Elements
    private EditText nameEditText;
    private EditText titleEditText;
    private Spinner experienceSpinner;
    private Spinner playingStyleSpinner;
    private EditText goalsEditText;
    private Button personalityTraitsButton;
    private Button favoriteMastersButton;
    private Button saveButton;
    private Button cancelButton;
    
    // Data
    private UserProfileManager profileManager;
    private UserProfileManager.UserProfile currentProfile;
    private List<String> selectedPersonalityTraits;
    private List<String> selectedFavoriteMasters;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        try {
            Log.d(TAG, "👤 Starting User Profile Activity");
            
            // Initialize profile manager
            profileManager = UserProfileManager.getInstance(this);
            selectedPersonalityTraits = new ArrayList<>();
            selectedFavoriteMasters = new ArrayList<>();
            
            // Setup action bar
            setupActionBar();
            
            // Initialize views
            initializeViews();
            
            // Load existing profile if available
            loadExistingProfile();
            
            // Setup controls
            setupControls();
            
            Log.d(TAG, "✅ User Profile Activity initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing User Profile Activity", e);
            Toast.makeText(this, "Error loading profile form", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("👤 Create Your Chess Profile");
        }
    }
    
    private void initializeViews() {
        Log.d(TAG, "🔍 Initializing views...");
        
        // Find all views
        nameEditText = findViewById(R.id.nameEditText);
        titleEditText = findViewById(R.id.titleEditText);
        experienceSpinner = findViewById(R.id.experienceSpinner);
        playingStyleSpinner = findViewById(R.id.playingStyleSpinner);
        goalsEditText = findViewById(R.id.goalsEditText);
        personalityTraitsButton = findViewById(R.id.personalityTraitsButton);
        favoriteMastersButton = findViewById(R.id.favoriteMastersButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        
        // Setup spinners
        setupSpinners();
        
        Log.d(TAG, "✅ Views initialized");
    }
    
    private void setupSpinners() {
        // Chess Experience Spinner
        List<String> experienceLevels = UserProfileManager.getChessExperienceLevels();
        ArrayAdapter<String> experienceAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, experienceLevels);
        experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experienceSpinner.setAdapter(experienceAdapter);
        
        // Playing Style Spinner
        List<String> playingStyles = UserProfileManager.getPlayingStyles();
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, playingStyles);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playingStyleSpinner.setAdapter(styleAdapter);
        
        Log.d(TAG, "✅ Spinners configured");
    }
    
    private void loadExistingProfile() {
        if (profileManager.hasProfile()) {
            Log.d(TAG, "📄 Loading existing profile");
            
            currentProfile = profileManager.getProfile();
            
            // Populate fields with existing data
            if (currentProfile.name != null) {
                nameEditText.setText(currentProfile.name);
            }
            if (currentProfile.title != null) {
                titleEditText.setText(currentProfile.title);
            }
            if (currentProfile.chessGoals != null) {
                goalsEditText.setText(currentProfile.chessGoals);
            }
            
            // Set spinner selections
            setSpinnerSelection(experienceSpinner, currentProfile.chessExperience);
            setSpinnerSelection(playingStyleSpinner, currentProfile.playingStyle);
            
            // Copy existing selections
            selectedPersonalityTraits.addAll(currentProfile.personalityTraits);
            selectedFavoriteMasters.addAll(currentProfile.favoriteMasters);
            
            // Update button texts
            updateSelectionButtons();
            
            // Change action bar title for editing
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("👤 Edit Your Chess Profile");
            }
            
            Log.d(TAG, "✅ Existing profile loaded: " + currentProfile.getDisplayName());
        } else {
            Log.d(TAG, "📄 No existing profile found - creating new profile");
            currentProfile = new UserProfileManager.UserProfile();
        }
    }
    
    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null && spinner.getAdapter() != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }
    
    private void setupControls() {
        // Personality Traits Button
        personalityTraitsButton.setOnClickListener(v -> showPersonalityTraitsDialog());
        
        // Favorite Masters Button
        favoriteMastersButton.setOnClickListener(v -> showFavoriteMastersDialog());
        
        // Save Button
        saveButton.setOnClickListener(v -> saveProfile());
        
        // Cancel Button
        cancelButton.setOnClickListener(v -> finish());
        
        // Update button texts
        updateSelectionButtons();
        
        Log.d(TAG, "✅ Controls configured");
    }
    
    private void showPersonalityTraitsDialog() {
        Log.d(TAG, "🎭 Showing personality traits selection dialog");
        
        List<String> availableTraits = UserProfileManager.getAvailablePersonalityTraits();
        boolean[] checkedItems = new boolean[availableTraits.size()];
        
        // Mark currently selected traits
        for (int i = 0; i < availableTraits.size(); i++) {
            checkedItems[i] = selectedPersonalityTraits.contains(availableTraits.get(i));
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎭 Select Your Personality Traits");
        builder.setMultiChoiceItems(
            availableTraits.toArray(new String[0]),
            checkedItems,
            (dialog, which, isChecked) -> {
                String trait = availableTraits.get(which);
                if (isChecked) {
                    if (!selectedPersonalityTraits.contains(trait)) {
                        selectedPersonalityTraits.add(trait);
                    }
                } else {
                    selectedPersonalityTraits.remove(trait);
                }
            }
        );
        
        builder.setPositiveButton("✅ Done", (dialog, which) -> {
            updateSelectionButtons();
            Log.d(TAG, "🎭 Selected traits: " + selectedPersonalityTraits);
        });
        
        builder.setNegativeButton("❌ Cancel", null);
        builder.show();
    }
    
    private void showFavoriteMastersDialog() {
        Log.d(TAG, "🏆 Showing favorite masters selection dialog");
        
        List<String> availableMasters = UserProfileManager.getAvailableMasters();
        boolean[] checkedItems = new boolean[availableMasters.size()];
        
        // Mark currently selected masters
        for (int i = 0; i < availableMasters.size(); i++) {
            checkedItems[i] = selectedFavoriteMasters.contains(availableMasters.get(i));
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🏆 Select Your Favorite Chess Masters");
        builder.setMessage("Choose masters you admire - they'll be more friendly towards you!");
        builder.setMultiChoiceItems(
            availableMasters.toArray(new String[0]),
            checkedItems,
            (dialog, which, isChecked) -> {
                String master = availableMasters.get(which);
                if (isChecked) {
                    if (!selectedFavoriteMasters.contains(master)) {
                        selectedFavoriteMasters.add(master);
                    }
                } else {
                    selectedFavoriteMasters.remove(master);
                }
            }
        );
        
        builder.setPositiveButton("✅ Done", (dialog, which) -> {
            updateSelectionButtons();
            Log.d(TAG, "🏆 Selected masters: " + selectedFavoriteMasters);
        });
        
        builder.setNegativeButton("❌ Cancel", null);
        builder.show();
    }
    
    private void updateSelectionButtons() {
        // Update personality traits button
        if (selectedPersonalityTraits.isEmpty()) {
            personalityTraitsButton.setText("🎭 Select Personality Traits");
        } else {
            personalityTraitsButton.setText("🎭 Traits (" + selectedPersonalityTraits.size() + " selected)");
        }
        
        // Update favorite masters button
        if (selectedFavoriteMasters.isEmpty()) {
            favoriteMastersButton.setText("🏆 Select Favorite Masters");
        } else {
            favoriteMastersButton.setText("🏆 Masters (" + selectedFavoriteMasters.size() + " selected)");
        }
    }
    
    private void saveProfile() {
        try {
            Log.d(TAG, "💾 Saving user profile...");
            
            // Validate required fields
            String name = nameEditText.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                nameEditText.requestFocus();
                return;
            }
            
            // Create profile from form data
            UserProfileManager.UserProfile profile = new UserProfileManager.UserProfile();
            profile.name = name;
            profile.title = titleEditText.getText().toString().trim();
            profile.chessExperience = experienceSpinner.getSelectedItem().toString();
            profile.playingStyle = playingStyleSpinner.getSelectedItem().toString();
            profile.chessGoals = goalsEditText.getText().toString().trim();
            profile.personalityTraits = new ArrayList<>(selectedPersonalityTraits);
            profile.favoriteMasters = new ArrayList<>(selectedFavoriteMasters);
            
            // Save profile
            profileManager.saveProfile(profile);
            
            // Show success message
            String message = "✅ Profile saved! AI masters will now recognize you as " + profile.getDisplayName();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            Log.d(TAG, "✅ Profile saved successfully: " + profile.getDisplayName());
            
            // Close activity
            setResult(RESULT_OK);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error saving profile", e);
            Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // Check if there are unsaved changes
        if (hasUnsavedChanges()) {
            new AlertDialog.Builder(this)
                .setTitle("❓ Unsaved Changes")
                .setMessage("You have unsaved changes. Are you sure you want to leave?")
                .setPositiveButton("✅ Leave", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("❌ Stay", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }
    
    private boolean hasUnsavedChanges() {
        // Simple check - in a real app you'd compare all fields
        String currentName = nameEditText.getText().toString().trim();
        String existingName = (currentProfile != null && currentProfile.name != null) ? currentProfile.name : "";
        
        return !currentName.equals(existingName) || 
               !selectedPersonalityTraits.equals(currentProfile.personalityTraits) ||
               !selectedFavoriteMasters.equals(currentProfile.favoriteMasters);
    }
    
    public static void showProfilePrompt(AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
            .setTitle("👤 Create Your Chess Profile")
            .setMessage("Would you like to create a personal profile? AI masters will remember you and build relationships based on your preferences!")
            .setPositiveButton("✅ Create Profile", (dialog, which) -> {
                Intent intent = new Intent(activity, UserProfileActivity.class);
                activity.startActivity(intent);
            })
            .setNegativeButton("⏭️ Maybe Later", null)
            .setNeutralButton("❌ Don't Ask Again", (dialog, which) -> {
                // Store preference to not show again
                activity.getSharedPreferences("UserProfile", MODE_PRIVATE)
                    .edit()
                    .putBoolean("profile_prompt_dismissed", true)
                    .apply();
            })
            .show();
    }
}