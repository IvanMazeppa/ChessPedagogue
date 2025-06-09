package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 🎤 VOICE-EMOTION FEEDBACK LOOPS - PHASE 1 IMPLEMENTATION
 * 
 * Analyzes HOW masters say things, not just WHAT they say.
 * Creates emergent emotional reactions based on voice delivery patterns,
 * timing, emphasis, and emotional undertones in speech.
 * 
 * This enables masters to react to emotional delivery and create
 * authentic voice-based emotional interactions.
 */
public class VoiceEmotionalAnalyzer {
    private static final String TAG = "🎭 VoiceEmotionalAnalyzer";
    private static VoiceEmotionalAnalyzer instance;
    
    private final Context context;
    private final Map<String, VoiceDeliveryProfile> masterVoiceProfiles;
    private final Map<String, RecentVoiceHistory> recentDeliveries;
    
    public static VoiceEmotionalAnalyzer getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceEmotionalAnalyzer(context);
        }
        return instance;
    }
    
    public VoiceEmotionalAnalyzer(Context context) {
        this.context = context.getApplicationContext();
        this.masterVoiceProfiles = new HashMap<>();
        this.recentDeliveries = new HashMap<>();
        initializeMasterVoiceProfiles();
        
        Log.d(TAG, "🚀 Voice-Emotion Feedback System initialized");
    }
    
    /**
     * Represents the emotional cues detected from voice delivery
     */
    public static class EmotionalCue {
        public final String sourceText;
        public final String speakingMaster;
        public final String detectedEmotion;
        public final float emotionalIntensity;      // 0.0 to 1.0
        public final VoiceCharacteristics voiceChars;
        public final long timestamp;
        
        // Specific delivery cues that triggered reactions
        public final boolean hasEmphasis;           // CAPS, exclamation points
        public final boolean hasHesitation;        // Ellipses, long pauses  
        public final boolean hasUrgency;           // Short sentences, rapid fire
        public final boolean hasDrama;             // Extended punctuation, theatrical
        public final boolean hasGentleness;       // Soft language, questions
        
        public EmotionalCue(String sourceText, String speakingMaster, String detectedEmotion, 
                           float emotionalIntensity, VoiceCharacteristics voiceChars) {
            this.sourceText = sourceText;
            this.speakingMaster = speakingMaster;
            this.detectedEmotion = detectedEmotion;
            this.emotionalIntensity = emotionalIntensity;
            this.voiceChars = voiceChars;
            this.timestamp = System.currentTimeMillis();
            
            // Analyze specific delivery patterns
            this.hasEmphasis = detectEmphasis(sourceText);
            this.hasHesitation = detectHesitation(sourceText);
            this.hasUrgency = detectUrgency(sourceText);
            this.hasDrama = detectDrama(sourceText);
            this.hasGentleness = detectGentleness(sourceText);
        }
        
        private boolean detectEmphasis(String text) {
            return text.contains("!") || text.matches(".*[A-Z]{2,}.*") || 
                   text.contains("absolutely") || text.contains("definitely") ||
                   text.contains("brilliant") || text.contains("perfect");
        }
        
        private boolean detectHesitation(String text) {
            return text.contains("...") || text.contains("well,") || 
                   text.contains("hmm") || text.contains("perhaps") ||
                   text.startsWith("I think") || text.contains("maybe");
        }
        
        private boolean detectUrgency(String text) {
            String[] sentences = text.split("[.!?]");
            boolean hasShortSentences = false;
            for (String sentence : sentences) {
                if (sentence.trim().split("\\s+").length <= 4 && sentence.trim().length() > 0) {
                    hasShortSentences = true;
                    break;
                }
            }
            return hasShortSentences || text.contains("quickly") || text.contains("now");
        }
        
        private boolean detectDrama(String text) {
            return text.contains("!!") || text.contains("...!") || 
                   text.contains("incredible") || text.contains("magnificent") ||
                   text.contains("disaster") || text.contains("magic");
        }
        
        private boolean detectGentleness(String text) {
            return text.contains("?") || text.contains("please") || 
                   text.contains("consider") || text.contains("interesting") ||
                   text.toLowerCase().contains("nice") || text.contains("appreciate");
        }
    }
    
    /**
     * Voice characteristics that contribute to emotional detection
     */
    public static class VoiceCharacteristics {
        public final int textLength;
        public final int punctuationDensity;       // Emotional punctuation per word
        public final float sentenceVariability;    // Mix of long/short sentences
        public final boolean hasQuestions;
        public final boolean hasExclamations;
        public final String dominantTone;          // "aggressive", "gentle", "analytical", etc.
        
        public VoiceCharacteristics(String text) {
            this.textLength = text.length();
            this.punctuationDensity = calculatePunctuationDensity(text);
            this.sentenceVariability = calculateSentenceVariability(text);
            this.hasQuestions = text.contains("?");
            this.hasExclamations = text.contains("!");
            this.dominantTone = analyzeDominantTone(text);
        }
        
        private int calculatePunctuationDensity(String text) {
            String[] words = text.split("\\s+");
            int emotionalPunctuation = 0;
            
            // Count emotional punctuation
            emotionalPunctuation += countOccurrences(text, "!");
            emotionalPunctuation += countOccurrences(text, "?");
            emotionalPunctuation += countOccurrences(text, "...");
            emotionalPunctuation += countOccurrences(text, "--");
            
            return words.length > 0 ? (emotionalPunctuation * 100) / words.length : 0;
        }
        
        private float calculateSentenceVariability(String text) {
            String[] sentences = text.split("[.!?]");
            if (sentences.length <= 1) return 0.0f;
            
            int[] lengths = new int[sentences.length];
            int total = 0;
            
            for (int i = 0; i < sentences.length; i++) {
                lengths[i] = sentences[i].trim().split("\\s+").length;
                total += lengths[i];
            }
            
            float avg = (float) total / sentences.length;
            float variance = 0;
            
            for (int length : lengths) {
                variance += Math.pow(length - avg, 2);
            }
            
            return (float) Math.sqrt(variance / sentences.length);
        }
        
        private String analyzeDominantTone(String text) {
            String lower = text.toLowerCase();
            
            // Aggressive indicators
            if (lower.contains("wrong") || lower.contains("terrible") || 
                lower.contains("disaster") || lower.contains("stupid")) {
                return "aggressive";
            }
            
            // Gentle indicators
            if (lower.contains("nice") || lower.contains("interesting") || 
                lower.contains("consider") || lower.contains("perhaps")) {
                return "gentle";
            }
            
            // Analytical indicators
            if (lower.contains("because") || lower.contains("therefore") || 
                lower.contains("analysis") || lower.contains("calculate")) {
                return "analytical";
            }
            
            // Dramatic indicators
            if (lower.contains("brilliant") || lower.contains("magnificent") || 
                lower.contains("incredible") || lower.contains("magic")) {
                return "dramatic";
            }
            
            // Confident indicators
            if (lower.contains("obviously") || lower.contains("clearly") || 
                lower.contains("definitely") || lower.contains("absolutely")) {
                return "confident";
            }
            
            return "neutral";
        }
        
        private int countOccurrences(String text, String pattern) {
            return text.length() - text.replace(pattern, "").length();
        }
    }
    
    /**
     * Master-specific voice delivery patterns and emotional triggers
     */
    public static class VoiceDeliveryProfile {
        public final String masterName;
        public final Map<String, Float> emotionalTriggerSensitivity;
        public final Map<String, String[]> typicalEmotionalDeliveries;
        public final float baseEmotionalReactivity;
        
        public VoiceDeliveryProfile(String masterName) {
            this.masterName = masterName;
            this.emotionalTriggerSensitivity = new HashMap<>();
            this.typicalEmotionalDeliveries = new HashMap<>();
            this.baseEmotionalReactivity = getMasterBaseReactivity();
            initializeEmotionalTriggers();
        }
        
        private float getMasterBaseReactivity() {
            switch (masterName.toLowerCase()) {
                case "tal": return 0.9f;        // Highly reactive to emotional delivery
                case "fischer": return 0.8f;    // Strong reactions, especially to criticism
                case "kasparov": return 0.7f;   // Passionate but controlled
                case "anand": return 0.4f;      // Diplomatic, measured reactions
                case "carlsen": return 0.3f;    // Calm, analytical responses
                case "karpov": return 0.2f;     // Extremely measured and controlled
                default: return 0.5f;
            }
        }
        
        private void initializeEmotionalTriggers() {
            switch (masterName.toLowerCase()) {
                case "tal":
                    // Tal gets infected by excitement, but calmed by analytical talk
                    emotionalTriggerSensitivity.put("excitement_boost", 1.5f);
                    emotionalTriggerSensitivity.put("drama_amplification", 1.8f);
                    emotionalTriggerSensitivity.put("analytical_dampening", 0.7f);
                    
                    typicalEmotionalDeliveries.put("excited", new String[]{
                        "This is pure magic!", "Incredible!", "Beautiful sacrifice!"
                    });
                    break;
                    
                case "fischer":
                    // Fischer reacts strongly to criticism, gets competitive with confidence
                    emotionalTriggerSensitivity.put("criticism_amplification", 2.0f);
                    emotionalTriggerSensitivity.put("confidence_challenge", 1.7f);
                    emotionalTriggerSensitivity.put("gentleness_suspicion", 1.2f);
                    
                    typicalEmotionalDeliveries.put("aggressive", new String[]{
                        "That's completely wrong!", "Are you serious?", "This is the only move!"
                    });
                    break;
                    
                case "carlsen":
                    // Carlsen becomes more analytical when faced with drama
                    emotionalTriggerSensitivity.put("drama_analytical_response", 1.3f);
                    emotionalTriggerSensitivity.put("urgency_calm_counter", 1.2f);
                    emotionalTriggerSensitivity.put("emphasis_understatement", 1.1f);
                    
                    typicalEmotionalDeliveries.put("analytical", new String[]{
                        "Let's look at this objectively.", "The position requires careful calculation.",
                        "I think there's a more precise approach."
                    });
                    break;
                    
                case "anand":
                    // Anand becomes more diplomatic when faced with aggression
                    emotionalTriggerSensitivity.put("aggression_diplomacy_boost", 1.4f);
                    emotionalTriggerSensitivity.put("conflict_mediation", 1.3f);
                    emotionalTriggerSensitivity.put("appreciation_amplification", 1.2f);
                    
                    typicalEmotionalDeliveries.put("diplomatic", new String[]{
                        "I can see your point.", "That's an interesting perspective.",
                        "Both approaches have merit."
                    });
                    break;
                    
                case "kasparov":
                    // Kasparov matches intensity and gets philosophical
                    emotionalTriggerSensitivity.put("intensity_matching", 1.6f);
                    emotionalTriggerSensitivity.put("philosophical_trigger", 1.3f);
                    emotionalTriggerSensitivity.put("competitive_escalation", 1.5f);
                    
                    typicalEmotionalDeliveries.put("intense", new String[]{
                        "This is what chess is about!", "The position demands everything!",
                        "True understanding comes from struggle!"
                    });
                    break;
                    
                default:
                    // Default emotional triggers for other masters
                    emotionalTriggerSensitivity.put("general_reactivity", 0.8f);
                    break;
            }
        }
    }
    
    /**
     * Tracks recent voice deliveries for pattern detection
     */
    public static class RecentVoiceHistory {
        private final List<EmotionalCue> recentCues;
        private static final int MAX_HISTORY = 10;
        
        public RecentVoiceHistory() {
            this.recentCues = new ArrayList<>();
        }
        
        public void addCue(EmotionalCue cue) {
            recentCues.add(0, cue); // Add to beginning
            if (recentCues.size() > MAX_HISTORY) {
                recentCues.remove(recentCues.size() - 1);
            }
        }
        
        public List<EmotionalCue> getRecentCues() {
            return new ArrayList<>(recentCues);
        }
        
        public float getRecentEmotionalTrend() {
            if (recentCues.isEmpty()) return 0.0f;
            
            float total = 0.0f;
            for (EmotionalCue cue : recentCues) {
                total += cue.emotionalIntensity;
            }
            return total / recentCues.size();
        }
    }
    
    /**
     * Emotional response that a listening master should have
     */
    public static class EmotionalResponse {
        public final String listeningMaster;
        public final String triggeredEmotion;
        public final float responseIntensity;
        public final String responseReason;
        public final String suggestedResponse;
        public final boolean shouldInterrupt;
        
        public EmotionalResponse(String listeningMaster, String triggeredEmotion, 
                               float responseIntensity, String responseReason, 
                               String suggestedResponse, boolean shouldInterrupt) {
            this.listeningMaster = listeningMaster;
            this.triggeredEmotion = triggeredEmotion;
            this.responseIntensity = responseIntensity;
            this.responseReason = responseReason;
            this.suggestedResponse = suggestedResponse;
            this.shouldInterrupt = shouldInterrupt;
        }
    }
    
    /**
     * Initialize voice profiles for all chess masters
     */
    private void initializeMasterVoiceProfiles() {
        String[] masters = {"tal", "fischer", "carlsen", "kasparov", "anand", 
                           "karpov", "kramnik", "capablanca", "alekhine", 
                           "morphy", "lasker", "botvinnik"};
        
        for (String master : masters) {
            masterVoiceProfiles.put(master, new VoiceDeliveryProfile(master));
            recentDeliveries.put(master, new RecentVoiceHistory());
        }
        
        Log.d(TAG, "✅ Initialized voice profiles for " + masters.length + " masters");
    }
    
    /**
     * MAIN ANALYSIS METHOD: Analyze voice delivery for emotional cues
     */
    public EmotionalCue analyzeVoiceDelivery(String text, String masterName, 
                                           float currentEmotionalIntensity) {
        if (text == null || text.trim().isEmpty() || masterName == null) {
            return null;
        }
        
        VoiceCharacteristics voiceChars = new VoiceCharacteristics(text);
        String detectedEmotion = detectEmotionFromDelivery(text, masterName, voiceChars);
        float enhancedIntensity = enhanceIntensityFromVoice(currentEmotionalIntensity, 
                                                           voiceChars, masterName);
        
        EmotionalCue cue = new EmotionalCue(text, masterName, detectedEmotion, 
                                          enhancedIntensity, voiceChars);
        
        // Record in history
        if (recentDeliveries.containsKey(masterName)) {
            recentDeliveries.get(masterName).addCue(cue);
        }
        
        Log.d(TAG, String.format("🎤 Voice analysis for %s: emotion=%s, intensity=%.2f, tone=%s", 
                                masterName, detectedEmotion, enhancedIntensity, voiceChars.dominantTone));
        
        return cue;
    }
    
    /**
     * Generate emotional response for a listening master
     */
    public EmotionalResponse generateCounterEmotion(EmotionalCue cue, String listeningMaster) {
        if (cue == null || listeningMaster == null || 
            listeningMaster.equals(cue.speakingMaster)) {
            return null;
        }
        
        VoiceDeliveryProfile listeningProfile = masterVoiceProfiles.get(listeningMaster.toLowerCase());
        if (listeningProfile == null) {
            return null;
        }
        
        // Analyze the emotional cue and generate appropriate response
        String triggeredEmotion = calculateTriggeredEmotion(cue, listeningProfile);
        float responseIntensity = calculateResponseIntensity(cue, listeningProfile);
        String responseReason = generateResponseReason(cue, listeningProfile);
        String suggestedResponse = generateSuggestedResponse(cue, listeningProfile, triggeredEmotion);
        boolean shouldInterrupt = shouldInterruptBasedOnCue(cue, listeningProfile);
        
        EmotionalResponse response = new EmotionalResponse(listeningMaster, triggeredEmotion,
                                                         responseIntensity, responseReason,
                                                         suggestedResponse, shouldInterrupt);
        
        Log.d(TAG, String.format("🎭 %s reacting to %s's %s delivery: %s (intensity=%.2f)", 
                                listeningMaster, cue.speakingMaster, cue.voiceChars.dominantTone,
                                triggeredEmotion, responseIntensity));
        
        return response;
    }
    
    /**
     * Detect emotion from voice delivery patterns
     */
    private String detectEmotionFromDelivery(String text, String masterName, VoiceCharacteristics voiceChars) {
        // Analyze specific delivery patterns
        if (voiceChars.dominantTone.equals("aggressive")) {
            return "competitive";
        } else if (voiceChars.dominantTone.equals("dramatic")) {
            return "thrilled";
        } else if (voiceChars.dominantTone.equals("gentle")) {
            return "philosophical";
        } else if (voiceChars.dominantTone.equals("analytical")) {
            return "focused";
        } else if (voiceChars.dominantTone.equals("confident")) {
            return "confident";
        }
        
        // Fall back to punctuation analysis
        if (voiceChars.hasExclamations && voiceChars.punctuationDensity > 10) {
            return "excited";
        } else if (voiceChars.hasQuestions) {
            return "intrigued";
        } else if (voiceChars.sentenceVariability > 3.0f) {
            return "passionate";
        }
        
        return "analytical"; // Default neutral emotional state
    }
    
    /**
     * Enhance emotional intensity based on voice characteristics
     */
    private float enhanceIntensityFromVoice(float baseIntensity, VoiceCharacteristics voiceChars, String masterName) {
        float enhancement = 1.0f;
        
        // Punctuation intensity boost
        enhancement += voiceChars.punctuationDensity * 0.02f;
        
        // Sentence variability boost
        enhancement += voiceChars.sentenceVariability * 0.05f;
        
        // Master-specific voice intensity modifiers
        VoiceDeliveryProfile profile = masterVoiceProfiles.get(masterName.toLowerCase());
        if (profile != null) {
            enhancement *= profile.baseEmotionalReactivity;
        }
        
        // Cap the enhancement
        enhancement = Math.min(enhancement, 2.0f);
        
        return Math.min(baseIntensity * enhancement, 1.0f);
    }
    
    /**
     * Calculate what emotion is triggered in the listening master
     */
    private String calculateTriggeredEmotion(EmotionalCue cue, VoiceDeliveryProfile listeningProfile) {
        String speakerTone = cue.voiceChars.dominantTone;
        String listeningMaster = listeningProfile.masterName.toLowerCase();
        
        // Master-specific emotional triggers
        switch (listeningMaster) {
            case "tal":
                if (speakerTone.equals("dramatic") || cue.hasDrama) {
                    return "thrilled"; // Gets excited by drama
                } else if (speakerTone.equals("analytical")) {
                    return "playful"; // Wants to inject creativity into analysis
                }
                break;
                
            case "fischer":
                if (speakerTone.equals("aggressive") || cue.hasEmphasis) {
                    return "competitive"; // Rises to challenges
                } else if (speakerTone.equals("gentle")) {
                    return "confident"; // Interprets gentleness as weakness
                }
                break;
                
            case "carlsen":
                if (speakerTone.equals("dramatic") || cue.hasDrama) {
                    return "analytical"; // Counters drama with precision
                } else if (cue.hasUrgency) {
                    return "focused"; // Responds to urgency with calm focus
                }
                break;
                
            case "anand":
                if (speakerTone.equals("aggressive")) {
                    return "philosophical"; // Responds to aggression diplomatically
                } else if (cue.hasGentleness) {
                    return "pleased"; // Appreciates diplomatic approach
                }
                break;
                
            case "kasparov":
                if (cue.emotionalIntensity > 0.7f) {
                    return "passionate"; // Matches high intensity
                } else if (speakerTone.equals("confident")) {
                    return "competitive"; // Challenges confidence
                }
                break;
        }
        
        // Default emotional responses based on voice characteristics
        if (cue.hasDrama) return "impressed";
        if (cue.hasEmphasis) return "intrigued";
        if (cue.hasGentleness) return "analytical";
        if (cue.hasUrgency) return "focused";
        
        return "content"; // Default response
    }
    
    /**
     * Calculate response intensity based on master's reactivity
     */
    private float calculateResponseIntensity(EmotionalCue cue, VoiceDeliveryProfile listeningProfile) {
        float baseIntensity = cue.emotionalIntensity * listeningProfile.baseEmotionalReactivity;
        
        // Amplify based on specific triggers
        String speakerTone = cue.voiceChars.dominantTone;
        if (listeningProfile.emotionalTriggerSensitivity.containsKey(speakerTone + "_amplification")) {
            float multiplier = listeningProfile.emotionalTriggerSensitivity.get(speakerTone + "_amplification");
            baseIntensity *= multiplier;
        }
        
        return Math.min(baseIntensity, 1.0f);
    }
    
    /**
     * Generate explanation for why this emotional response was triggered
     */
    private String generateResponseReason(EmotionalCue cue, VoiceDeliveryProfile listeningProfile) {
        String speakerTone = cue.voiceChars.dominantTone;
        String masterName = listeningProfile.masterName;
        
        if (speakerTone.equals("aggressive")) {
            return masterName + " reacted to aggressive delivery tone";
        } else if (speakerTone.equals("dramatic")) {
            return masterName + " responding to dramatic voice emphasis";
        } else if (cue.hasEmphasis) {
            return masterName + " triggered by emphatic delivery";
        } else if (cue.hasGentleness) {
            return masterName + " responding to gentle tone";
        } else if (cue.hasUrgency) {
            return masterName + " reacting to urgent delivery";
        }
        
        return masterName + " voice-emotion feedback triggered";
    }
    
    /**
     * Generate suggested response text based on emotional trigger
     */
    private String generateSuggestedResponse(EmotionalCue cue, VoiceDeliveryProfile listeningProfile, 
                                           String triggeredEmotion) {
        String masterName = listeningProfile.masterName.toLowerCase();
        
        // Use master-specific delivery patterns if available
        if (listeningProfile.typicalEmotionalDeliveries.containsKey(triggeredEmotion)) {
            String[] responses = listeningProfile.typicalEmotionalDeliveries.get(triggeredEmotion);
            return responses[0]; // Use first response for now
        }
        
        // Generate contextual responses based on the trigger
        switch (triggeredEmotion) {
            case "thrilled":
                return "This is getting exciting!";
            case "competitive":
                return "Let's see about that!";
            case "analytical":
                return "Let me think about this more carefully.";
            case "philosophical":
                return "There's wisdom in that approach.";
            case "focused":
                return "Time to concentrate on what's important.";
            case "impressed":
                return "That's quite remarkable!";
            case "intrigued":
                return "Interesting perspective...";
            default:
                return "I see your point.";
        }
    }
    
    /**
     * Determine if the listening master should interrupt based on voice cues
     */
    private boolean shouldInterruptBasedOnCue(EmotionalCue cue, VoiceDeliveryProfile listeningProfile) {
        // High-reactivity masters more likely to interrupt
        float interruptThreshold = 1.0f - listeningProfile.baseEmotionalReactivity;
        
        // Strong triggers increase interrupt probability
        if (cue.voiceChars.dominantTone.equals("aggressive") && cue.emotionalIntensity > 0.8f) {
            return Math.random() < 0.3f; // 30% chance of interruption
        }
        
        if (cue.hasDrama && listeningProfile.masterName.toLowerCase().equals("tal")) {
            return Math.random() < 0.4f; // Tal loves to jump into dramatic moments
        }
        
        if (cue.hasEmphasis && listeningProfile.masterName.toLowerCase().equals("fischer")) {
            return Math.random() < 0.3f; // Fischer challenges emphatic statements
        }
        
        return false; // Default: no interruption
    }
    
    /**
     * Get the recent emotional trend for a master
     */
    public float getRecentEmotionalTrend(String masterName) {
        RecentVoiceHistory history = recentDeliveries.get(masterName.toLowerCase());
        return history != null ? history.getRecentEmotionalTrend() : 0.0f;
    }
    
    /**
     * Clear voice history for a master (useful for new games)
     */
    public void clearVoiceHistory(String masterName) {
        RecentVoiceHistory history = recentDeliveries.get(masterName.toLowerCase());
        if (history != null) {
            history.recentCues.clear();
            Log.d(TAG, "🧹 Cleared voice history for " + masterName);
        }
    }
    
    /**
     * Get emotional reactivity callback for integration with existing emotional system
     */
    public interface VoiceEmotionalCallback {
        void onVoiceEmotionalReaction(String listeningMaster, EmotionalResponse response);
        void onVoiceEmotionalCueDetected(EmotionalCue cue);
    }
    
    private final List<VoiceEmotionalCallback> callbacks = new ArrayList<>();
    
    public void addVoiceEmotionalCallback(VoiceEmotionalCallback callback) {
        callbacks.add(callback);
    }
    
    public void removeVoiceEmotionalCallback(VoiceEmotionalCallback callback) {
        callbacks.remove(callback);
    }
    
    /**
     * Notify all listeners of voice emotional reactions
     */
    private void notifyVoiceEmotionalReaction(String listeningMaster, EmotionalResponse response) {
        for (VoiceEmotionalCallback callback : callbacks) {
            try {
                callback.onVoiceEmotionalReaction(listeningMaster, response);
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in voice emotional callback", e);
            }
        }
    }
    
    /**
     * Process voice emotional analysis for multiple listening masters
     */
    public void processVoiceEmotionalFeedback(String text, String speakingMaster, 
                                            float currentIntensity, List<String> listeningMasters) {
        EmotionalCue cue = analyzeVoiceDelivery(text, speakingMaster, currentIntensity);
        if (cue == null) return;
        
        // Notify that a cue was detected
        for (VoiceEmotionalCallback callback : callbacks) {
            try {
                callback.onVoiceEmotionalCueDetected(cue);
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in voice cue callback", e);
            }
        }
        
        // Generate responses for all listening masters
        for (String listeningMaster : listeningMasters) {
            EmotionalResponse response = generateCounterEmotion(cue, listeningMaster);
            if (response != null) {
                notifyVoiceEmotionalReaction(listeningMaster, response);
            }
        }
    }
}