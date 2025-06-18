package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONObject;

/**
 * 🚀 ELEVENLABS V3 EMOTIONAL ENHANCER
 * 
 * Integrates with your existing ElevenLabsTTSService to provide:
 * - Extreme emotional voice modulation for demonstrative masters
 * - Enhanced SSML integration for dramatic pauses and emphasis
 * - Real-time emotional state visualization
 * - Master-specific emotional amplification
 * 
 * HOW TO USE:
 * 1. Call enableExtremeEmotionsForMaster("tal") to activate
 * 2. The system will automatically enhance TAL's voice with extreme emotional expression
 * 3. Watch the logs for emotional state visualization
 * 4. Compare with Carlsen's stoic delivery for dramatic contrast
 */
public class ElevenLabsV3EmotionalEnhancer {
    private static final String TAG = "🚀 V3Enhancer";
    private static final String PREFS_NAME = "ChessPedagoguePrefs";
    private static final String KEY_EXTREME_EMOTIONS_ENABLED = "extreme_emotions_enabled";
    private static final String KEY_EXTREME_EMOTIONS_MASTER = "extreme_emotions_master";
    
    private static ElevenLabsV3EmotionalEnhancer instance;
    private final Context context;
    private final EnhancedEmotionalVoiceEngine voiceEngine;
    private SharedPreferences prefs;
    
    // Current extreme emotions configuration
    private boolean extremeEmotionsEnabled = false;
    private String extremeEmotionsMaster = "tal"; // Default to TAL for best demonstration
    
    private ElevenLabsV3EmotionalEnhancer(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.voiceEngine = EnhancedEmotionalVoiceEngine.getInstance(context);
        
        // Load saved settings
        this.extremeEmotionsEnabled = prefs.getBoolean(KEY_EXTREME_EMOTIONS_ENABLED, false);
        this.extremeEmotionsMaster = prefs.getString(KEY_EXTREME_EMOTIONS_MASTER, "tal");
        
        Log.d(TAG, "🎭 V3 Emotional Enhancer initialized - Extreme emotions " + 
              (extremeEmotionsEnabled ? "ENABLED" : "DISABLED") + " for " + extremeEmotionsMaster);
    }
    
    public static synchronized ElevenLabsV3EmotionalEnhancer getInstance(Context context) {
        if (instance == null) {
            instance = new ElevenLabsV3EmotionalEnhancer(context);
        }
        return instance;
    }
    
    /**
     * 🔥 ENABLE EXTREME EMOTIONS FOR SPECIFIC MASTER
     * Recommended: "tal" (most expressive), "kasparov" (passionate), or "fischer" (intense)
     */
    public void enableExtremeEmotionsForMaster(String masterName) {
        this.extremeEmotionsEnabled = true;
        this.extremeEmotionsMaster = masterName.toLowerCase();
        
        // Save settings
        prefs.edit()
             .putBoolean(KEY_EXTREME_EMOTIONS_ENABLED, true)
             .putString(KEY_EXTREME_EMOTIONS_MASTER, extremeEmotionsMaster)
             .apply();
        
        Log.d(TAG, "🔥 EXTREME EMOTIONS ENABLED for " + masterName.toUpperCase() + 
              " - Prepare for dramatic voice expression!");
        
        // Log recommendation
        if ("carlsen".equals(extremeEmotionsMaster)) {
            Log.w(TAG, "⚠️ WARNING: Carlsen is stoic - consider TAL, Kasparov, or Fischer for better emotional demonstration");
        }
    }
    
    /**
     * 🎭 DISABLE EXTREME EMOTIONS
     */
    public void disableExtremeEmotions() {
        this.extremeEmotionsEnabled = false;
        prefs.edit().putBoolean(KEY_EXTREME_EMOTIONS_ENABLED, false).apply();
        
        Log.d(TAG, "🔄 Extreme emotions disabled - returning to standard voice modulation");
    }
    
    /**
     * 🎯 CHECK IF MASTER SHOULD USE EXTREME EMOTIONAL ENHANCEMENT
     */
    public boolean shouldUseExtremeEmotions(String masterName) {
        return extremeEmotionsEnabled && extremeEmotionsMaster.equals(masterName.toLowerCase());
    }
    
    /**
     * 🚀 GET ENHANCED V3 VOICE SETTINGS
     * This method replaces the standard v3 enhancement in ElevenLabsTTSService
     */
    public JSONObject getEnhancedV3VoiceSettings(String masterName, 
                                                EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) throws Exception {
        
        if (shouldUseExtremeEmotions(masterName)) {
            Log.d(TAG, "🔥 Applying EXTREME emotional voice settings for " + masterName);
            
            // Get extreme emotional state visualization for debugging
            String emotionalVisualization = voiceEngine.getEmotionalStateVisualization(masterName, emotionalState);
            Log.d(TAG, "🎭 EMOTIONAL STATE: " + emotionalVisualization);
            
            // Return extreme voice settings
            return voiceEngine.getExtremeEmotionalVoiceSettings(masterName, emotionalState);
        } else {
            // Use standard v3 settings - delegate to original implementation
            Log.d(TAG, "📱 Using standard v3 voice settings for " + masterName);
            return getStandardV3VoiceSettings(masterName, emotionalState);
        }
    }
    
    /**
     * 🎭 ENHANCE TEXT WITH EMOTIONAL MARKUP
     * Adds SSML tags for dramatic pauses, emphasis, and prosody
     */
    public String enhanceTextForExtremeEmotions(String originalText, String masterName, 
                                               EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) {
        
        if (shouldUseExtremeEmotions(masterName) && emotionalState != null) {
            String enhancedText = voiceEngine.enhanceTextWithEmotionalMarkup(originalText, masterName, emotionalState);
            
            Log.d(TAG, String.format("🎭 Enhanced text for %s: %d chars -> %d chars with emotional markup", 
                  masterName, originalText.length(), enhancedText.length()));
            
            return enhancedText;
        }
        
        return originalText; // No enhancement for non-extreme masters
    }
    
    /**
     * 📊 GET CURRENT EMOTIONAL STATE DISPLAY
     * For UI display or debugging
     */
    public String getCurrentEmotionalStateDisplay(String masterName) {
        if (!shouldUseExtremeEmotions(masterName)) {
            return masterName + ": Standard voice mode";
        }
        
        // Get current emotional state from the system
        EmotionalIntelligenceManager emotionalManager = EmotionalIntelligenceManager.getInstance(context);
        EmotionalIntelligenceManager.EmotionalAnalysisResult currentState = emotionalManager.getCurrentEmotionalAnalysis(masterName);
        
        if (currentState != null) {
            String visualization = voiceEngine.getEmotionalStateVisualization(masterName, currentState);
            return masterName.toUpperCase() + ": " + visualization;
        } else {
            return masterName.toUpperCase() + ": 😐 No emotional data";
        }
    }
    
    /**
     * 🔧 INTEGRATION HELPER: Update ElevenLabsTTSService shouldUseV3Enhancement method
     * Add this logic to your existing ElevenLabsTTSService
     */
    public boolean shouldMasterUseV3Enhancement(String masterName) {
        // Original logic: only Carlsen
        boolean originalV3Logic = "carlsen".equalsIgnoreCase(masterName);
        
        // Enhanced logic: Carlsen OR extreme emotions master
        boolean enhancedV3Logic = shouldUseExtremeEmotions(masterName);
        
        boolean result = originalV3Logic || enhancedV3Logic;
        
        if (result) {
            Log.d(TAG, String.format("🚀 V3 Enhancement active for %s: %s%s", 
                  masterName, 
                  originalV3Logic ? "[ORIGINAL CARLSEN]" : "",
                  enhancedV3Logic ? "[EXTREME EMOTIONS]" : ""));
        }
        
        return result;
    }
    
    /**
     * 📈 GET EMOTION AMPLIFICATION FACTOR
     * For display/debugging purposes
     */
    public float getEmotionAmplificationFactor(String masterName) {
        if (shouldUseExtremeEmotions(masterName)) {
            // Get amplification factor from the voice engine
            switch (masterName.toLowerCase()) {
                case "tal": return 2.5f;      // Maximum drama
                case "kasparov": return 2.0f; // High passion
                case "fischer": return 1.8f;  // Intense focus
                case "alekhine": return 1.9f; // Artistic passion
                default: return 1.5f;         // Enhanced default
            }
        }
        return 1.0f; // Standard
    }
    
    /**
     * 🎯 STANDARD V3 VOICE SETTINGS (fallback)
     * Replicate the original logic for non-extreme masters
     */
    private JSONObject getStandardV3VoiceSettings(String masterName, 
                                                 EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) throws Exception {
        JSONObject voiceSettings = new JSONObject();
        
        // Use your existing stability/similarity methods
        double baseStability = getStabilityForMaster(masterName);
        double baseSimilarity = getSimilarityBoostForMaster(masterName);
        double baseStyle = 0.0; // Conservative style
        
        if (emotionalState != null && emotionalState.intensity > 0.2f) {
            // Mild emotional modulation for standard masters
            float intensityFactor = emotionalState.intensity * 1.2f; // Slight amplification
            
            // Conservative adjustments
            float stabilityAdjustment = 0.0f;
            float styleAdjustment = 0.0f;
            
            switch (emotionalState.emotion) {
                case ECSTATIC:
                case THRILLED:
                    stabilityAdjustment = -0.15f * intensityFactor;
                    styleAdjustment = 0.2f * intensityFactor;
                    break;
                case EXCITED:
                case PLEASED:
                    stabilityAdjustment = -0.08f * intensityFactor;
                    styleAdjustment = 0.1f * intensityFactor;
                    break;
                case ANALYTICAL:
                case FOCUSED:
                    stabilityAdjustment = 0.05f * intensityFactor;
                    styleAdjustment = 0.05f * intensityFactor;
                    break;
            }
            
            voiceSettings.put("stability", Math.max(0.0, Math.min(1.0, baseStability + stabilityAdjustment)));
            voiceSettings.put("similarity_boost", Math.max(0.0, Math.min(1.0, baseSimilarity)));
            voiceSettings.put("style", Math.max(0.0, Math.min(1.0, baseStyle + styleAdjustment)));
            voiceSettings.put("use_speaker_boost", true);
        } else {
            // Default settings
            voiceSettings.put("stability", baseStability);
            voiceSettings.put("similarity_boost", baseSimilarity);
            voiceSettings.put("style", baseStyle);
            voiceSettings.put("use_speaker_boost", true);
        }
        
        return voiceSettings;
    }
    
    // Helper methods - these should match your existing ElevenLabsTTSService methods
    private double getStabilityForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal": return 0.45;
            case "fischer": return 0.60;
            case "kasparov": return 0.50;
            case "carlsen": return 0.55;
            default: return 0.55;
        }
    }
    
    private double getSimilarityBoostForMaster(String master) {
        return 0.80; // High similarity to maintain voice identity
    }
    
    /**
     * 🎭 GET RECOMMENDED MASTERS FOR DEMONSTRATION
     */
    public static String[] getRecommendedEmotionalMasters() {
        return new String[]{"tal", "kasparov", "fischer", "alekhine"};
    }
    
    /**
     * 🎯 GET CURRENT CONFIGURATION SUMMARY
     */
    public String getConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("🚀 V3 EMOTIONAL ENHANCER STATUS:\n");
        summary.append("Extreme Emotions: ").append(extremeEmotionsEnabled ? "ENABLED" : "DISABLED").append("\n");
        if (extremeEmotionsEnabled) {
            summary.append("Active Master: ").append(extremeEmotionsMaster.toUpperCase()).append("\n");
            summary.append("Amplification: ").append(String.format("%.1fx", getEmotionAmplificationFactor(extremeEmotionsMaster))).append("\n");
            summary.append("Current State: ").append(getCurrentEmotionalStateDisplay(extremeEmotionsMaster)).append("\n");
        }
        summary.append("Recommended Masters: ").append(String.join(", ", getRecommendedEmotionalMasters()));
        
        return summary.toString();
    }
}