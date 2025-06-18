package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 👤 UserProfileManager - Integrates user as a full personality in the AI ecosystem
 * 
 * Creates a user profile that participates in the same relationship and emotional
 * systems as the chess masters, allowing for emergent behavior and authentic interactions.
 */
public class UserProfileManager {
    private static final String TAG = "UserProfileManager";
    private static final String PREFS_NAME = "UserProfile";
    
    // Profile keys
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_TITLE = "user_title";
    private static final String KEY_CHESS_EXPERIENCE = "chess_experience";
    private static final String KEY_PLAYING_STYLE = "playing_style";
    private static final String KEY_PERSONALITY_TRAITS = "personality_traits";
    private static final String KEY_FAVORITE_MASTERS = "favorite_masters";
    private static final String KEY_CHESS_GOALS = "chess_goals";
    private static final String KEY_PROFILE_CREATED = "profile_created";
    private static final String KEY_CREATION_DATE = "creation_date";
    
    // User "master" identifier for integration with existing systems
    public static final String USER_MASTER_ID = "user_player";
    
    private static UserProfileManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final GameDatabaseHelper dbHelper;
    
    // Cached profile data
    private UserProfile currentProfile;
    
    public static class UserProfile {
        public String name;
        public String title;
        public String chessExperience;
        public String playingStyle;
        public List<String> personalityTraits;
        public List<String> favoriteMasters;
        public String chessGoals;
        public boolean isCreated;
        public long creationDate;
        
        public UserProfile() {
            this.personalityTraits = new ArrayList<>();
            this.favoriteMasters = new ArrayList<>();
            this.isCreated = false;
        }
        
        /**
         * Get display name for UI and AI interactions
         */
        public String getDisplayName() {
            if (name != null && !name.trim().isEmpty()) {
                if (title != null && !title.trim().isEmpty()) {
                    return title + " " + name;
                }
                return name;
            }
            return "Chess Student";
        }
        
        /**
         * Get personality description for AI context
         */
        public String getPersonalityDescription() {
            StringBuilder desc = new StringBuilder();
            desc.append("A ").append(chessExperience).append(" chess player");
            
            if (playingStyle != null && !playingStyle.isEmpty()) {
                desc.append(" with a ").append(playingStyle).append(" playing style");
            }
            
            if (!personalityTraits.isEmpty()) {
                desc.append(". Personality traits: ").append(String.join(", ", personalityTraits));
            }
            
            if (chessGoals != null && !chessGoals.isEmpty()) {
                desc.append(". Goals: ").append(chessGoals);
            }
            
            return desc.toString();
        }
        
        /**
         * Generate AI context prompt including user information
         */
        public String generateAIContext() {
            StringBuilder context = new StringBuilder();
            context.append("You are interacting with ").append(getDisplayName());
            
            if (!name.equals("Chess Student")) {
                context.append(" (call them ").append(name).append(")");
            }
            
            context.append(", ").append(getPersonalityDescription().toLowerCase());
            
            if (!favoriteMasters.isEmpty()) {
                context.append(". They particularly admire: ").append(String.join(", ", favoriteMasters));
            }
            
            context.append(". Treat them as a real person with genuine chess aspirations.");
            context.append(" Remember past interactions and build an authentic relationship over time.");
            
            return context.toString();
        }
    }
    
    private UserProfileManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.dbHelper = new GameDatabaseHelper(context);
        loadProfile();
    }
    
    public static synchronized UserProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserProfileManager(context);
        }
        return instance;
    }
    
    /**
     * Check if user has created a profile
     */
    public boolean hasProfile() {
        return currentProfile != null && currentProfile.isCreated;
    }
    
    /**
     * Get current user profile
     */
    public UserProfile getProfile() {
        return currentProfile;
    }
    
    /**
     * Create or update user profile
     */
    public void saveProfile(UserProfile profile) {
        Log.d(TAG, "👤 Saving user profile: " + profile.getDisplayName());
        
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString(KEY_USER_NAME, profile.name);
        editor.putString(KEY_USER_TITLE, profile.title);
        editor.putString(KEY_CHESS_EXPERIENCE, profile.chessExperience);
        editor.putString(KEY_PLAYING_STYLE, profile.playingStyle);
        editor.putString(KEY_CHESS_GOALS, profile.chessGoals);
        
        // Save arrays as JSON
        try {
            JSONArray traitsArray = new JSONArray(profile.personalityTraits);
            JSONArray mastersArray = new JSONArray(profile.favoriteMasters);
            
            editor.putString(KEY_PERSONALITY_TRAITS, traitsArray.toString());
            editor.putString(KEY_FAVORITE_MASTERS, mastersArray.toString());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error saving profile arrays", e);
        }
        
        // Mark as created
        if (!profile.isCreated) {
            profile.creationDate = System.currentTimeMillis();
            profile.isCreated = true;
        }
        
        editor.putBoolean(KEY_PROFILE_CREATED, profile.isCreated);
        editor.putLong(KEY_CREATION_DATE, profile.creationDate);
        
        editor.apply();
        
        // Update cached profile
        this.currentProfile = profile;
        
        // 🎭 INTEGRATE: Register user in emotional intelligence systems
        integrateWithEmotionalSystems();
        
        Log.d(TAG, "✅ User profile saved and integrated into AI ecosystem");
    }
    
    /**
     * Load user profile from storage
     */
    private void loadProfile() {
        UserProfile profile = new UserProfile();
        
        profile.name = prefs.getString(KEY_USER_NAME, "");
        profile.title = prefs.getString(KEY_USER_TITLE, "");
        profile.chessExperience = prefs.getString(KEY_CHESS_EXPERIENCE, "beginner");
        profile.playingStyle = prefs.getString(KEY_PLAYING_STYLE, "");
        profile.chessGoals = prefs.getString(KEY_CHESS_GOALS, "");
        profile.isCreated = prefs.getBoolean(KEY_PROFILE_CREATED, false);
        profile.creationDate = prefs.getLong(KEY_CREATION_DATE, System.currentTimeMillis());
        
        // Load arrays from JSON
        try {
            String traitsJson = prefs.getString(KEY_PERSONALITY_TRAITS, "[]");
            String mastersJson = prefs.getString(KEY_FAVORITE_MASTERS, "[]");
            
            JSONArray traitsArray = new JSONArray(traitsJson);
            JSONArray mastersArray = new JSONArray(mastersJson);
            
            for (int i = 0; i < traitsArray.length(); i++) {
                profile.personalityTraits.add(traitsArray.getString(i));
            }
            
            for (int i = 0; i < mastersArray.length(); i++) {
                profile.favoriteMasters.add(mastersArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(TAG, "❌ Error loading profile arrays", e);
        }
        
        this.currentProfile = profile;
        
        if (profile.isCreated) {
            Log.d(TAG, "👤 Loaded user profile: " + profile.getDisplayName());
            integrateWithEmotionalSystems();
        }
    }
    
    /**
     * 🎭 CRITICAL: Integrate user into existing emotional intelligence systems
     */
    private void integrateWithEmotionalSystems() {
        if (!hasProfile()) return;
        
        try {
            Log.d(TAG, "🎭 Integrating user profile with emotional intelligence systems...");
            
            // 1. Register user in relationship database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            // Initialize relationships with all masters
            String[] allMasters = {"tal", "fischer", "carlsen", "kasparov", "anand", "alekhine", 
                                 "kramnik", "karpov", "capablanca", "morphy", "lasker", "botvinnik"};
            
            for (String master : allMasters) {
                // Set initial relationship values based on user's favorite masters
                float initialRespect = currentProfile.favoriteMasters.contains(master) ? 0.8f : 0.5f;
                float initialFriendship = currentProfile.favoriteMasters.contains(master) ? 0.6f : 0.3f;
                float initialRivalry = 0.1f; // Start low, will evolve
                
                String insertSql = "INSERT OR REPLACE INTO master_relationships " +
                                 "(master1, master2, respect_level, rivalry_intensity, friendship_bond, last_updated) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
                
                db.execSQL(insertSql, new Object[]{
                    master, USER_MASTER_ID, initialRespect, initialRivalry, initialFriendship, System.currentTimeMillis()
                });
                
                db.execSQL(insertSql, new Object[]{
                    USER_MASTER_ID, master, initialRespect, initialRivalry, initialFriendship, System.currentTimeMillis()
                });
            }
            
            // 2. Initialize emotional memory for user
            initializeEmotionalMemory();
            
            Log.d(TAG, "✅ User successfully integrated into AI emotional ecosystem");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error integrating user with emotional systems", e);
        }
    }
    
    /**
     * Initialize emotional memory entries for the user
     */
    private void initializeEmotionalMemory() {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            // Create initial emotional memories based on user profile
            Map<String, String> initialEmotions = new HashMap<>();
            initialEmotions.put("first_meeting", "curious");
            initialEmotions.put("chess_improvement", "encouraging");
            initialEmotions.put("good_moves", "impressed");
            initialEmotions.put("mistakes", "patient");
            
            for (Map.Entry<String, String> entry : initialEmotions.entrySet()) {
                // Use the existing emotional_reactions table instead of non-existent emotional_memory table
                String insertSql = "INSERT OR REPLACE INTO emotional_reactions " +
                                 "(master, opponent, topic, emotion, intensity, timestamp) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
                
                db.execSQL(insertSql, new Object[]{
                    USER_MASTER_ID, "general", entry.getKey(), entry.getValue(), 0.5f, System.currentTimeMillis()
                });
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing emotional memory for user", e);
        }
    }
    
    /**
     * Get available personality traits for selection
     */
    public static List<String> getAvailablePersonalityTraits() {
        return Arrays.asList(
            "Analytical", "Creative", "Aggressive", "Patient", "Tactical", "Strategic",
            "Intuitive", "Methodical", "Competitive", "Collaborative", "Curious", "Confident",
            "Humble", "Determined", "Flexible", "Persistent", "Innovative", "Traditional"
        );
    }
    
    /**
     * Get available chess experience levels
     */
    public static List<String> getChessExperienceLevels() {
        return Arrays.asList(
            "Complete beginner", "Novice player", "Casual player", "Club player", 
            "Tournament player", "Advanced player", "Expert player"
        );
    }
    
    /**
     * Get available playing styles
     */
    public static List<String> getPlayingStyles() {
        return Arrays.asList(
            "Aggressive attacker", "Solid defender", "Tactical genius", "Strategic mastermind",
            "Endgame specialist", "Opening expert", "Positional player", "Sacrificial artist",
            "Calculating machine", "Intuitive improviser", "Balanced all-rounder"
        );
    }
    
    /**
     * Get all available chess masters for favorite selection
     */
    public static List<String> getAvailableMasters() {
        return Arrays.asList(
            "Mikhail Tal", "Bobby Fischer", "Magnus Carlsen", "Garry Kasparov",
            "Viswanathan Anand", "Alexander Alekhine", "Vladimir Kramnik", "Anatoly Karpov",
            "José Raúl Capablanca", "Paul Morphy", "Emanuel Lasker", "Mikhail Botvinnik"
        );
    }
    
    /**
     * 🎯 CRITICAL: Generate AI context for any master interaction
     */
    public String getAIContextForMaster(String masterName) {
        if (!hasProfile()) {
            return "You are facing a worthy chess opponent. Play with your full competitive spirit and signature style.";
        }
        
        UserProfile profile = getProfile();
        StringBuilder context = new StringBuilder();
        
        context.append(profile.generateAIContext());
        
        // Add relationship-specific context
        if (profile.favoriteMasters.contains(masterName)) {
            context.append(" Note: ").append(profile.name)
                   .append(" has studied your games and particularly admires your style - they see you as a role model. ");
            context.append("Show them why they chose well by demonstrating your legendary prowess!");
        } else {
            context.append(" This opponent doesn't favor your style - prove them wrong with your brilliant play!");
        }
        
        // Add competitive context based on experience
        if (profile.chessExperience.contains("beginner") || profile.chessExperience.contains("novice")) {
            context.append(" Don't underestimate them - play seriously but show your mastery naturally through superior moves.");
        } else if (profile.chessExperience.contains("advanced") || profile.chessExperience.contains("expert")) {
            context.append(" They're experienced - this will be a true test of skill. Bring your A-game and competitive fire!");
        }
        
        context.append(" Maintain your authentic personality and legendary competitive spirit. This is a serious chess battle!");
        
        return context.toString();
    }
    
    /**
     * Clear user profile (for testing or reset)
     */
    public void clearProfile() {
        prefs.edit().clear().apply();
        currentProfile = new UserProfile();
        Log.d(TAG, "👤 User profile cleared");
    }
}