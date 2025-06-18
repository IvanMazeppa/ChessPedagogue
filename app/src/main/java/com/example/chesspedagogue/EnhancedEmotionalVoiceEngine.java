package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 🎭 ENHANCED EMOTIONAL VOICE ENGINE v3
 * 
 * Takes your existing emotional intelligence system and cranks it up to 11!
 * Features:
 * - Extreme emotional voice parameter modulation
 * - SSML tags for dramatic pauses, emphasis, and prosody
 * - Master-specific emotional amplification factors
 * - Dynamic emotional state visualization
 * - Voice "breakthrough moments" for peak emotions
 * 
 * Recommended Masters for Demonstration:
 * 1. TAL - Most expressive, high emotional susceptibility (1.2f)
 * 2. KASPAROV - Passionate escalation patterns
 * 3. FISCHER - Intense focused emotions
 */
public class EnhancedEmotionalVoiceEngine {
    private static final String TAG = "🎭 Enhanced Voice";
    
    private static EnhancedEmotionalVoiceEngine instance;
    private final Context context;
    
    // Enhanced emotional ranges for v3
    private static final float EXTREME_EMOTION_THRESHOLD = 1.5f;  // When to use extreme voice effects
    private static final float BREAKTHROUGH_THRESHOLD = 1.8f;    // When emotions "break through" composure
    
    // Master-specific emotional amplification factors
    private static final Map<String, Float> MASTER_EMOTIONAL_AMPLIFICATION = new HashMap<>();
    static {
        MASTER_EMOTIONAL_AMPLIFICATION.put("tal", 2.5f);       // MAXIMUM expressiveness
        MASTER_EMOTIONAL_AMPLIFICATION.put("kasparov", 2.0f);  // Very high expressiveness  
        MASTER_EMOTIONAL_AMPLIFICATION.put("fischer", 1.8f);   // High when triggered
        MASTER_EMOTIONAL_AMPLIFICATION.put("alekhine", 1.9f);  // Passionate and artistic
        MASTER_EMOTIONAL_AMPLIFICATION.put("capablanca", 1.2f); // More measured but still expressive
        MASTER_EMOTIONAL_AMPLIFICATION.put("carlsen", 1.0f);   // Baseline stoic (for comparison)
        MASTER_EMOTIONAL_AMPLIFICATION.put("karpov", 1.1f);    // Slightly more expressive than Carlsen
        MASTER_EMOTIONAL_AMPLIFICATION.put("kramnik", 1.3f);   // Analytical but can get excited
        MASTER_EMOTIONAL_AMPLIFICATION.put("anand", 1.4f);     // Friendly and expressive
        MASTER_EMOTIONAL_AMPLIFICATION.put("morphy", 1.6f);    // Gentlemanly but passionate
        MASTER_EMOTIONAL_AMPLIFICATION.put("lasker", 1.5f);    // Wise but can be intense
        MASTER_EMOTIONAL_AMPLIFICATION.put("botvinnik", 1.3f); // Methodical but passionate about chess
    }
    
    private EnhancedEmotionalVoiceEngine(Context context) {
        this.context = context.getApplicationContext();
        Log.d(TAG, "🚀 Enhanced Emotional Voice Engine v3 initialized");
    }
    
    public static synchronized EnhancedEmotionalVoiceEngine getInstance(Context context) {
        if (instance == null) {
            instance = new EnhancedEmotionalVoiceEngine(context);
        }
        return instance;
    }
    
    /**
     * 🎭 GET EXTREME EMOTIONAL VOICE SETTINGS
     * Much more aggressive than the current system!
     */
    public JSONObject getExtremeEmotionalVoiceSettings(String masterName, 
                                                      EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) throws Exception {
        JSONObject voiceSettings = new JSONObject();
        
        // Base settings
        double baseStability = getEnhancedStabilityForMaster(masterName);
        double baseSimilarity = getEnhancedSimilarityForMaster(masterName);
        double baseStyle = getEnhancedStyleForMaster(masterName);
        
        if (emotionalState != null && emotionalState.intensity > 0.1f) {
            // Get master amplification factor
            float amplificationFactor = MASTER_EMOTIONAL_AMPLIFICATION.getOrDefault(masterName.toLowerCase(), 1.0f);
            
            // Calculate extreme emotional modulation
            ExtremeEmotionalModulation modulation = getExtremeEmotionalModulation(emotionalState, masterName, amplificationFactor);
            
            // Apply extreme settings
            voiceSettings.put("stability", Math.max(0.0, Math.min(1.0, baseStability + modulation.stabilityAdjustment)));
            voiceSettings.put("similarity_boost", Math.max(0.0, Math.min(1.0, baseSimilarity + modulation.similarityAdjustment)));
            voiceSettings.put("style", Math.max(0.0, Math.min(1.0, baseStyle + modulation.styleAdjustment)));
            voiceSettings.put("use_speaker_boost", true);
            
            // V3 ENHANCEMENT: Add advanced voice controls
            if (emotionalState.intensity > EXTREME_EMOTION_THRESHOLD) {
                voiceSettings.put("experimental_enhance", true);
                voiceSettings.put("emotional_depth", Math.min(1.0, emotionalState.intensity / 2.0f));
            }
            
            Log.d(TAG, String.format("🔥 EXTREME voice settings for %s (%s intensity %.2f): stability=%.2f, similarity=%.2f, style=%.2f [AMPLIFIED %.1fx]", 
                  masterName, emotionalState.emotion.name, emotionalState.intensity,
                  voiceSettings.getDouble("stability"), 
                  voiceSettings.getDouble("similarity_boost"),
                  voiceSettings.getDouble("style"),
                  amplificationFactor));
        } else {
            // Default settings but enhanced
            voiceSettings.put("stability", baseStability);
            voiceSettings.put("similarity_boost", baseSimilarity);
            voiceSettings.put("style", baseStyle);
            voiceSettings.put("use_speaker_boost", true);
        }
        
        return voiceSettings;
    }
    
    /**
     * 🎭 EXTREME EMOTIONAL MODULATION
     * Much more aggressive parameter changes!
     */
    private ExtremeEmotionalModulation getExtremeEmotionalModulation(
            EmotionalIntelligenceManager.EmotionalAnalysisResult emotional, 
            String masterName, 
            float amplificationFactor) {
        
        ExtremeEmotionalModulation modulation = new ExtremeEmotionalModulation();
        
        // Base intensity with master amplification
        float amplifiedIntensity = emotional.intensity * amplificationFactor;
        
        // Determine if this is a "breakthrough" emotional moment
        boolean isBreakthrough = amplifiedIntensity > BREAKTHROUGH_THRESHOLD;
        
        switch (emotional.emotion) {
            case ECSTATIC:
            case THRILLED:
                // EXTREME joy - voice should be almost uncontrolled
                modulation.stabilityAdjustment = isBreakthrough ? -0.6f : -0.4f;
                modulation.styleAdjustment = isBreakthrough ? 0.8f : 0.6f;
                modulation.speedMultiplier = isBreakthrough ? 1.3f : 1.1f;
                modulation.emphasisLevel = "strong";
                break;
                
            case EXCITED:
            case PLEASED:
                // High excitement - noticeably more expressive
                modulation.stabilityAdjustment = isBreakthrough ? -0.4f : -0.3f;
                modulation.styleAdjustment = isBreakthrough ? 0.6f : 0.4f;
                modulation.speedMultiplier = 1.1f;
                modulation.emphasisLevel = "moderate";
                break;
                
            case DEVASTATED:
            case FRUSTRATED:
                // Intense negative emotions - dramatic voice changes
                modulation.stabilityAdjustment = isBreakthrough ? -0.5f : -0.3f;
                modulation.styleAdjustment = isBreakthrough ? 0.7f : 0.5f;
                modulation.speedMultiplier = isBreakthrough ? 0.8f : 0.9f; // Slower when devastated
                modulation.emphasisLevel = "strong";
                modulation.pauseDuration = isBreakthrough ? "long" : "medium";
                break;
                
            case ANALYTICAL:
            case FOCUSED:
                // For passionate masters like Tal/Alekhine, even "analytical" can be expressive
                if ("tal".equals(masterName.toLowerCase()) || "alekhine".equals(masterName.toLowerCase())) {
                    modulation.stabilityAdjustment = -0.2f; // Less stable = more passionate analysis
                    modulation.styleAdjustment = 0.3f;
                    modulation.emphasisLevel = "moderate";
                } else {
                    modulation.stabilityAdjustment = 0.1f; // More controlled
                    modulation.styleAdjustment = 0.1f;
                    modulation.emphasisLevel = "reduced";
                }
                break;
                
            case CONFIDENT:
                // Confidence with master-specific flair
                modulation.stabilityAdjustment = isBreakthrough ? -0.2f : 0.1f;
                modulation.styleAdjustment = isBreakthrough ? 0.4f : 0.2f;
                modulation.speedMultiplier = 1.05f; // Slightly faster when confident
                modulation.emphasisLevel = "moderate";
                break;
                
            default:
                // Even neutral emotions get some amplification for expressive masters
                if (amplificationFactor > 1.5f) {
                    modulation.stabilityAdjustment = -0.1f;
                    modulation.styleAdjustment = 0.1f;
                }
                break;
        }
        
        // Apply momentum factor for emotional streaks
        if (Math.abs(emotional.momentum) > 0.5f) {
            float momentumMultiplier = 1.0f + (Math.abs(emotional.momentum) * 0.5f);
            modulation.stabilityAdjustment *= momentumMultiplier;
            modulation.styleAdjustment *= momentumMultiplier;
        }
        
        Log.d(TAG, String.format("🎯 Modulation for %s: stability=%+.2f, style=%+.2f, speed=%.2f, emphasis=%s %s", 
              masterName, modulation.stabilityAdjustment, modulation.styleAdjustment, 
              modulation.speedMultiplier, modulation.emphasisLevel,
              isBreakthrough ? "[BREAKTHROUGH!]" : ""));
        
        return modulation;
    }
    
    /**
     * 🎭 ENHANCED TEXT WITH SSML AND EMOTIONAL MARKUP
     * Adds dramatic pauses, emphasis, and prosody based on emotional state
     */
    public String enhanceTextWithEmotionalMarkup(String originalText, String masterName, 
                                                EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) {
        
        if (emotionalState == null || emotionalState.intensity < 0.3f) {
            return originalText; // Not enough emotion to enhance
        }
        
        StringBuilder enhanced = new StringBuilder();
        
        // Get amplification factor
        float amplificationFactor = MASTER_EMOTIONAL_AMPLIFICATION.getOrDefault(masterName.toLowerCase(), 1.0f);
        float amplifiedIntensity = emotionalState.intensity * amplificationFactor;
        boolean isBreakthrough = amplifiedIntensity > BREAKTHROUGH_THRESHOLD;
        
        // Add opening pause for dramatic effect in extreme emotions
        if (isBreakthrough) {
            enhanced.append("<break time=\"500ms\"/> ");
        } else if (amplifiedIntensity > EXTREME_EMOTION_THRESHOLD) {
            enhanced.append("<break time=\"300ms\"/> ");
        }
        
        // Process text with emotional enhancement
        String[] sentences = originalText.split("\\. ");
        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i];
            
            // Add emotional prosody based on state
            enhanced.append(wrapWithEmotionalProsody(sentence, emotionalState, amplificationFactor));
            
            if (i < sentences.length - 1) {
                enhanced.append(". ");
                
                // Add dramatic pauses between sentences for high emotions
                if (isBreakthrough) {
                    enhanced.append("<break time=\"400ms\"/> ");
                } else if (amplifiedIntensity > EXTREME_EMOTION_THRESHOLD) {
                    enhanced.append("<break time=\"200ms\"/> ");
                }
            }
        }
        
        Log.d(TAG, String.format("🎭 Enhanced text for %s (%s %.2f): %d chars -> %d chars", 
              masterName, emotionalState.emotion.name, amplifiedIntensity, 
              originalText.length(), enhanced.length()));
        
        return enhanced.toString();
    }
    
    /**
     * 🎭 WRAP SENTENCE WITH EMOTIONAL PROSODY
     */
    private String wrapWithEmotionalProsody(String sentence, EmotionalIntelligenceManager.EmotionalAnalysisResult emotional, float amplificationFactor) {
        float amplifiedIntensity = emotional.intensity * amplificationFactor;
        boolean isExtreme = amplifiedIntensity > EXTREME_EMOTION_THRESHOLD;
        boolean isBreakthrough = amplifiedIntensity > BREAKTHROUGH_THRESHOLD;
        
        StringBuilder prosody = new StringBuilder();
        
        // Build prosody attributes
        prosody.append("<prosody");
        
        // Rate adjustment based on emotion
        switch (emotional.emotion) {
            case ECSTATIC:
            case EXCITED:
            case THRILLED:
                prosody.append(" rate=\"").append(isBreakthrough ? "fast" : "medium").append("\"");
                break;
            case DEVASTATED:
            case FRUSTRATED:
                prosody.append(" rate=\"").append(isBreakthrough ? "slow" : "medium").append("\"");
                break;
            case ANALYTICAL:
            case FOCUSED:
                prosody.append(" rate=\"medium\"");
                break;
        }
        
        // Pitch adjustment for emotional impact
        if (isBreakthrough) {
            switch (emotional.emotion) {
                case ECSTATIC:
                case EXCITED:
                case THRILLED:
                    prosody.append(" pitch=\"+15%\"");
                    break;
                case DEVASTATED:
                case FRUSTRATED:
                    prosody.append(" pitch=\"-10%\"");
                    break;
            }
        } else if (isExtreme) {
            switch (emotional.emotion) {
                case ECSTATIC:
                case EXCITED:
                    prosody.append(" pitch=\"+8%\"");
                    break;
                case DEVASTATED:
                case FRUSTRATED:
                    prosody.append(" pitch=\"-5%\"");
                    break;
            }
        }
        
        // Volume adjustment for intensity
        if (isBreakthrough) {
            prosody.append(" volume=\"+6dB\"");
        } else if (isExtreme) {
            prosody.append(" volume=\"+3dB\"");
        }
        
        prosody.append(">");
        
        // Add emphasis to key words in extreme emotions
        if (isExtreme) {
            sentence = addEmphasisToKeyWords(sentence);
        }
        
        prosody.append(sentence).append("</prosody>");
        
        return prosody.toString();
    }
    
    /**
     * 🎭 ADD EMPHASIS TO KEY WORDS
     */
    private String addEmphasisToKeyWords(String sentence) {
        // Emphasize emotional words
        sentence = sentence.replaceAll("\\b(brilliant|genius|magnificent|terrible|disaster|incredible|amazing)\\b", 
                                     "<emphasis level=\"strong\">$1</emphasis>");
        
        // Emphasize chess terms for dramatic effect
        sentence = sentence.replaceAll("\\b(sacrifice|attack|blunder|checkmate|victory)\\b", 
                                     "<emphasis level=\"moderate\">$1</emphasis>");
        
        return sentence;
    }
    
    /**
     * 🎭 GET ENHANCED MASTER SETTINGS
     * More extreme baseline settings for emotional masters
     */
    private double getEnhancedStabilityForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return 0.25; // VERY unstable = maximum expressiveness
            case "kasparov":
                return 0.35; // High expressiveness
            case "fischer":
                return 0.45; // Intense when triggered
            case "alekhine":
                return 0.30; // Passionate and artistic
            default:
                return 0.50; // Default
        }
    }
    
    private double getEnhancedSimilarityForMaster(String master) {
        // Keep similarity high to maintain voice identity
        return 0.85; // Higher than current system
    }
    
    private double getEnhancedStyleForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return 0.4; // HIGH style for maximum drama
            case "kasparov":
                return 0.3; // High style for passion
            case "fischer":
                return 0.2; // Moderate style
            case "alekhine":
                return 0.35; // Artistic style
            default:
                return 0.1; // Conservative default
        }
    }
    
    /**
     * 🎭 EMOTIONAL STATE VISUALIZATION
     * Creates a visual/text representation of current emotional state
     */
    public String getEmotionalStateVisualization(String masterName, EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalState) {
        if (emotionalState == null) return "😐 Neutral";
        
        float amplificationFactor = MASTER_EMOTIONAL_AMPLIFICATION.getOrDefault(masterName.toLowerCase(), 1.0f);
        float amplifiedIntensity = emotionalState.intensity * amplificationFactor;
        
        String intensityBar = getIntensityBar(amplifiedIntensity);
        String emotionEmoji = getEmotionEmoji(emotionalState.emotion);
        String breaktroughIndicator = amplifiedIntensity > BREAKTHROUGH_THRESHOLD ? " 🔥BREAKTHROUGH🔥" : "";
        
        return String.format("%s %s %s (%.1fx)%s", 
                           emotionEmoji, 
                           emotionalState.emotion.name.toUpperCase(), 
                           intensityBar, 
                           amplificationFactor,
                           breaktroughIndicator);
    }
    
    private String getIntensityBar(float intensity) {
        int bars = (int) Math.min(10, intensity * 5); // Scale to 10 bars max
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i < bars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        return bar.toString();
    }
    
    private String getEmotionEmoji(EmotionalIntelligenceManager.EmotionalState emotion) {
        switch (emotion) {
            case ECSTATIC: return "🤩";
            case THRILLED: return "😱";
            case EXCITED: return "😃";
            case PLEASED: return "😊";
            case CONFIDENT: return "😎";
            case ANALYTICAL: return "🤔";
            case FOCUSED: return "😤";
            case DEVASTATED: return "😭";
            case FRUSTRATED: return "😡";
            default: return "😐";
        }
    }
    
    /**
     * 🎭 EXTREME EMOTIONAL MODULATION DATA CLASS
     */
    private static class ExtremeEmotionalModulation {
        float stabilityAdjustment = 0.0f;
        float similarityAdjustment = 0.0f;
        float styleAdjustment = 0.0f;
        float speedMultiplier = 1.0f;
        String emphasisLevel = "none";
        String pauseDuration = "none";
    }
}