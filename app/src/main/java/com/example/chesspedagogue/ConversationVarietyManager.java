package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Manages conversation variety and prevents repetitive responses
 * Features:
 * - Dynamic parameter adjustment (temperature, penalties)
 * - Context rotation strategies
 * - Response seed diversification  
 * - Phrase tracking and avoidance
 * - Personality-aware variety settings
 */
public class ConversationVarietyManager {
    private static final String TAG = "ConversationVariety";
    private static final String PREFS_NAME = "conversation_variety_prefs";
    
    private static ConversationVarietyManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final Random random;
    
    // Variety tracking
    private final Map<String, Integer> phraseCounts = new HashMap<>();
    private final Set<String> recentPhrases = new HashSet<>();
    private final List<String> conversationSeeds = new ArrayList<>();
    private int contextRotationIndex = 0;
    
    // Dynamic parameters
    public static class VarietyParams {
        float temperature;
        float presencePenalty;
        float frequencyPenalty;
        float topP;
        int maxTokens;
        String seedSuffix;
        
        VarietyParams(float temp, float presence, float frequency, float top, int tokens, String seed) {
            this.temperature = temp;
            this.presencePenalty = presence;
            this.frequencyPenalty = frequency;
            this.topP = top;
            this.maxTokens = tokens;
            this.seedSuffix = seed;
        }
    }
    
    // Master-specific variety profiles
    private final Map<String, VarietyParams[]> masterVarietyProfiles = new HashMap<>();
    
    private ConversationVarietyManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.random = new Random(System.currentTimeMillis());
        
        initializeMasterProfiles();
        loadPersistedData();
    }
    
    public static synchronized ConversationVarietyManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConversationVarietyManager(context);
        }
        return instance;
    }
    
    /**
     * Initialize variety profiles for each chess master
     * Each master has multiple parameter sets to rotate through
     */
    private void initializeMasterProfiles() {
        // Tal - Tactical and energetic variety
        masterVarietyProfiles.put("tal", new VarietyParams[]{
            new VarietyParams(0.8f, 0.7f, 0.4f, 0.9f, 120, "_energetic"),
            new VarietyParams(0.9f, 0.8f, 0.5f, 0.85f, 140, "_explosive"),
            new VarietyParams(0.7f, 0.6f, 0.3f, 0.95f, 100, "_focused"),
            new VarietyParams(0.85f, 0.75f, 0.45f, 0.9f, 130, "_artistic")
        });
        
        // Fischer - Analytical precision with variety
        masterVarietyProfiles.put("fischer", new VarietyParams[]{
            new VarietyParams(0.6f, 0.5f, 0.3f, 0.9f, 150, "_precise"),
            new VarietyParams(0.7f, 0.6f, 0.4f, 0.85f, 130, "_analytical"),
            new VarietyParams(0.75f, 0.7f, 0.35f, 0.9f, 140, "_confident"),
            new VarietyParams(0.65f, 0.55f, 0.25f, 0.95f, 160, "_methodical")
        });
        
        // Carlsen - Modern adaptability
        masterVarietyProfiles.put("carlsen", new VarietyParams[]{
            new VarietyParams(0.75f, 0.6f, 0.35f, 0.9f, 140, "_adaptive"),
            new VarietyParams(0.8f, 0.65f, 0.4f, 0.85f, 125, "_versatile"),
            new VarietyParams(0.7f, 0.55f, 0.3f, 0.95f, 155, "_strategic"),
            new VarietyParams(0.85f, 0.7f, 0.45f, 0.9f, 135, "_intuitive")
        });
        
        // Kasparov - Dynamic and aggressive variety
        masterVarietyProfiles.put("kasparov", new VarietyParams[]{
            new VarietyParams(0.85f, 0.75f, 0.5f, 0.9f, 160, "_aggressive"),
            new VarietyParams(0.9f, 0.8f, 0.55f, 0.85f, 140, "_dynamic"),
            new VarietyParams(0.8f, 0.7f, 0.45f, 0.95f, 150, "_theoretical"),
            new VarietyParams(0.75f, 0.65f, 0.4f, 0.9f, 170, "_calculating")
        });
        
        // Default profile for other masters
        VarietyParams[] defaultProfile = {
            new VarietyParams(0.75f, 0.6f, 0.35f, 0.9f, 140, "_balanced"),
            new VarietyParams(0.8f, 0.65f, 0.4f, 0.85f, 130, "_expressive"),
            new VarietyParams(0.7f, 0.55f, 0.3f, 0.95f, 150, "_thoughtful"),
            new VarietyParams(0.85f, 0.7f, 0.45f, 0.9f, 135, "_creative")
        };
        
        // Apply default to remaining masters
        String[] otherMasters = {"kramnik", "karpov", "alekhine", "capablanca", 
                               "morphy", "lasker", "anand", "botvinnik"};
        for (String master : otherMasters) {
            masterVarietyProfiles.put(master, defaultProfile);
        }
    }
    
    /**
     * Get variety parameters for a specific master and conversation state
     */
    public VarietyParams getVarietyParams(String master, String conversationContext) {
        master = master.toLowerCase();
        
        // Get master's variety profile
        VarietyParams[] profile = masterVarietyProfiles.get(master);
        if (profile == null) {
            Log.w(TAG, "No variety profile for master: " + master + ", using default");
            profile = masterVarietyProfiles.get("carlsen"); // Use Carlsen as default
        }
        
        // Select parameter set based on conversation state and rotation
        int paramIndex = determineParameterIndex(conversationContext, profile.length);
        VarietyParams params = profile[paramIndex];
        
        // Apply dynamic adjustments based on recent repetition
        return adjustForRepetition(params, master);
    }
    
    /**
     * Determine which parameter set to use based on conversation context
     */
    private int determineParameterIndex(String context, int profileLength) {
        // Rotate through parameters to ensure variety
        contextRotationIndex = (contextRotationIndex + 1) % profileLength;
        
        // Add context-based selection logic
        if (context != null) {
            if (context.contains("tactical") || context.contains("attack")) {
                return 1; // More energetic/aggressive parameters
            } else if (context.contains("positional") || context.contains("strategic")) {
                return 2; // More focused/strategic parameters
            } else if (context.contains("endgame") || context.contains("technical")) {
                return 3; // More precise/methodical parameters
            }
        }
        
        return contextRotationIndex;
    }
    
    /**
     * Adjust parameters based on recent repetition patterns
     */
    private VarietyParams adjustForRepetition(VarietyParams base, String master) {
        float repetitionFactor = calculateRepetitionFactor(master);
        
        // Increase variety when repetition is detected
        if (repetitionFactor > 0.7f) {
            Log.d(TAG, "🔄 High repetition detected for " + master + ", increasing variety");
            return new VarietyParams(
                Math.min(1.0f, base.temperature + 0.15f),
                Math.min(2.0f, base.presencePenalty + 0.3f),
                Math.min(2.0f, base.frequencyPenalty + 0.2f),
                Math.max(0.7f, base.topP - 0.1f),
                base.maxTokens,
                base.seedSuffix + "_varied"
            );
        } else if (repetitionFactor > 0.5f) {
            Log.d(TAG, "🎯 Moderate repetition for " + master + ", slight variety boost");
            return new VarietyParams(
                Math.min(1.0f, base.temperature + 0.1f),
                Math.min(2.0f, base.presencePenalty + 0.2f),
                Math.min(2.0f, base.frequencyPenalty + 0.15f),
                base.topP,
                base.maxTokens,
                base.seedSuffix + "_tuned"
            );
        }
        
        return base; // No adjustment needed
    }
    
    /**
     * Calculate repetition factor based on recent phrases
     */
    private float calculateRepetitionFactor(String master) {
        if (recentPhrases.isEmpty()) return 0.0f;
        
        int totalPhrases = recentPhrases.size();
        int repeatedPhrases = 0;
        
        for (String phrase : recentPhrases) {
            Integer count = phraseCounts.get(master + ":" + phrase);
            if (count != null && count > 1) {
                repeatedPhrases++;
            }
        }
        
        return (float) repeatedPhrases / totalPhrases;
    }
    
    /**
     * Track a response to monitor for repetition
     */
    public void trackResponse(String master, String response) {
        if (response == null || response.trim().isEmpty()) return;
        
        // Extract key phrases (sentences or significant chunks)
        String[] phrases = extractKeyPhrases(response);
        
        for (String phrase : phrases) {
            String key = master.toLowerCase() + ":" + phrase.toLowerCase().trim();
            
            // Update phrase count
            phraseCounts.put(key, phraseCounts.getOrDefault(key, 0) + 1);
            
            // Add to recent phrases (with size limit)
            recentPhrases.add(phrase.toLowerCase().trim());
            if (recentPhrases.size() > 50) {
                // Remove oldest phrases
                String[] recentArray = recentPhrases.toArray(new String[0]);
                recentPhrases.clear();
                recentPhrases.addAll(Arrays.asList(recentArray).subList(25, recentArray.length));
            }
            
            // Log repetition warnings
            int count = phraseCounts.get(key);
            if (count > 2) {
                Log.w(TAG, "⚠️ Phrase repetition detected for " + master + ": \"" + 
                     phrase.substring(0, Math.min(30, phrase.length())) + "...\" (count: " + count + ")");
            }
        }
        
        persistData();
    }
    
    /**
     * Extract key phrases from a response for tracking
     */
    private String[] extractKeyPhrases(String response) {
        // Split by sentences and filter meaningful phrases
        String[] sentences = response.split("[.!?]+");
        List<String> phrases = new ArrayList<>();
        
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 10 && trimmed.length() < 100) {
                phrases.add(trimmed);
            }
        }
        
        // Also extract common patterns that might repeat
        String[] commonPatterns = {
            "(?i)\\b(that's an? \\w+ move)\\b",
            "(?i)\\b(interesting choice)\\b", 
            "(?i)\\b(i would suggest)\\b",
            "(?i)\\b(this position)\\b",
            "(?i)\\b(the key is)\\b"
        };
        
        for (String pattern : commonPatterns) {
            if (response.matches(".*" + pattern + ".*")) {
                phrases.add(response.replaceAll(".*(" + pattern + ").*", "$1"));
            }
        }
        
        return phrases.toArray(new String[0]);
    }
    
    /**
     * Generate a conversation seed to add uniqueness
     */
    public String generateConversationSeed(String master, String context) {
        String baseSeed = master + "_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
        
        // Add context-specific elements
        if (context != null) {
            if (context.contains("opening")) baseSeed += "_opening";
            else if (context.contains("middlegame")) baseSeed += "_middle";
            else if (context.contains("endgame")) baseSeed += "_ending";
            else if (context.contains("tactical")) baseSeed += "_tactics";
        }
        
        conversationSeeds.add(baseSeed);
        
        // Limit seed history
        if (conversationSeeds.size() > 20) {
            conversationSeeds.remove(0);
        }
        
        return baseSeed;
    }
    
    /**
     * Check if a phrase pattern might lead to repetition
     */
    public boolean isPotentialRepetition(String master, String candidateResponse) {
        if (candidateResponse == null) return false;
        
        String[] phrases = extractKeyPhrases(candidateResponse);
        for (String phrase : phrases) {
            String key = master.toLowerCase() + ":" + phrase.toLowerCase().trim();
            Integer count = phraseCounts.get(key);
            if (count != null && count >= 2) {
                Log.d(TAG, "🚫 Potential repetition detected: " + phrase.substring(0, Math.min(30, phrase.length())));
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get context rotation suggestions for variety
     */
    public Map<String, String> getContextRotationSuggestions(String master) {
        Map<String, String> suggestions = new HashMap<>();
        
        switch (master.toLowerCase()) {
            case "tal":
                suggestions.put("style_rotation", "tactical_brilliance,sacrificial_combinations,attacking_flair");
                suggestions.put("mood_variation", "enthusiastic,contemplative,explosive");
                break;
            case "fischer":
                suggestions.put("style_rotation", "precise_analysis,confident_assessment,methodical_breakdown");
                suggestions.put("mood_variation", "analytical,assertive,instructional");
                break;
            case "carlsen":
                suggestions.put("style_rotation", "modern_pragmatism,strategic_depth,adaptive_thinking");
                suggestions.put("mood_variation", "casual,focused,innovative");
                break;
            default:
                suggestions.put("style_rotation", "classical_wisdom,modern_insights,practical_advice");
                suggestions.put("mood_variation", "thoughtful,encouraging,instructional");
        }
        
        return suggestions;
    }
    
    /**
     * Reset tracking for a specific master or all
     */
    public void resetTracking(String master) {
        if (master == null) {
            Log.d(TAG, "🔄 Resetting all conversation tracking");
            phraseCounts.clear();
            recentPhrases.clear();
            conversationSeeds.clear();
            contextRotationIndex = 0;
        } else {
            Log.d(TAG, "🔄 Resetting tracking for " + master);
            phraseCounts.entrySet().removeIf(entry -> entry.getKey().startsWith(master.toLowerCase() + ":"));
        }
        persistData();
    }
    
    /**
     * Persist tracking data to preferences
     */
    private void persistData() {
        SharedPreferences.Editor editor = prefs.edit();
        
        // Save phrase counts (limit to prevent bloat)
        StringBuilder phraseData = new StringBuilder();
        int saved = 0;
        for (Map.Entry<String, Integer> entry : phraseCounts.entrySet()) {
            if (saved++ > 200) break; // Limit stored phrases
            
            phraseData.append(entry.getKey())
                     .append(":")
                     .append(entry.getValue())
                     .append(";");
        }
        editor.putString("phrase_counts", phraseData.toString());
        
        // Save rotation index
        editor.putInt("context_rotation_index", contextRotationIndex);
        
        editor.apply();
    }
    
    /**
     * Load persisted tracking data
     */
    private void loadPersistedData() {
        String phraseData = prefs.getString("phrase_counts", "");
        if (!phraseData.isEmpty()) {
            String[] entries = phraseData.split(";");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    try {
                        phraseCounts.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Error parsing phrase count: " + entry);
                    }
                }
            }
        }
        
        contextRotationIndex = prefs.getInt("context_rotation_index", 0);
        
        Log.d(TAG, "✅ Loaded " + phraseCounts.size() + " tracked phrases");
    }
    
    /**
     * Get variety statistics for debugging
     */
    public Map<String, Object> getVarietyStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("tracked_phrases", phraseCounts.size());
        stats.put("recent_phrases", recentPhrases.size());
        stats.put("conversation_seeds", conversationSeeds.size());
        stats.put("context_rotation_index", contextRotationIndex);
        
        // Calculate repetition stats per master
        Map<String, Integer> masterStats = new HashMap<>();
        for (String key : phraseCounts.keySet()) {
            String master = key.split(":")[0];
            masterStats.put(master, masterStats.getOrDefault(master, 0) + 1);
        }
        stats.put("master_phrase_counts", masterStats);
        
        return stats;
    }
}