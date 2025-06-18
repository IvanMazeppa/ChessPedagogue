package com.example.chesspedagogue;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 🎭 PHASE 2: MULTI-LAYERED EMOTIONAL COMPLEXITY
 * 
 * Represents the psychological depth of chess masters - what they show publicly
 * versus what they actually feel underneath. Creates authentic emotional masks,
 * defensive responses, and personality-specific coping mechanisms.
 * 
 * This enables masters to:
 * - Hide true emotions behind defensive masks
 * - Have emotional breakthroughs when masks slip
 * - Display personality-consistent emotional defenses
 * - React differently to surface vs underlying emotions
 */
public class EmotionalLayer {
    private static final String TAG = "🎭 EmotionalLayer";
    
    // The two-layer emotional system
    private String surfaceEmotion;          // What they show publicly
    private String underlyingEmotion;       // What they actually feel
    private String defensiveResponse;       // How they cope with stress
    private float emotionalMask;            // Intensity of hiding (0.0 = transparent, 1.0 = completely masked)
    private String emotionalTrigger;        // What caused this layered response
    private String masterName;              // Which chess master this belongs to
    
    // Mask stability and breakthrough tracking
    private float maskStability;            // How well they can maintain the mask (0.0-1.0)
    private float stressLevel;             // Current psychological pressure (0.0-1.0)
    private long lastBreakthroughTime;     // When did their mask last slip?
    private boolean maskSlipping;          // Is their emotional defense currently failing?
    
    // Master-specific psychological profiles
    private final Map<String, DefensiveMechanism> defensiveMechanisms;
    
    public EmotionalLayer(String masterName, String initialEmotion) {
        this.masterName = masterName;
        this.surfaceEmotion = initialEmotion;
        this.underlyingEmotion = initialEmotion;
        this.defensiveResponse = "none";
        this.emotionalMask = 0.0f;  // Start transparent
        this.emotionalTrigger = "game_start";
        this.maskStability = getMasterMaskStability(masterName);
        this.stressLevel = 0.0f;
        this.lastBreakthroughTime = 0;
        this.maskSlipping = false;
        this.defensiveMechanisms = initializeDefensiveMechanisms();
        
        Log.d(TAG, String.format("🎭 Created emotional layer for %s: surface=%s, underlying=%s", 
                                masterName, surfaceEmotion, underlyingEmotion));
    }
    
    /**
     * Master-specific defensive mechanisms and psychological patterns
     */
    public static class DefensiveMechanism {
        public final String mechanism;         // "aggression", "analysis", "humor", "withdrawal"
        public final String triggerCondition;  // What triggers this defense
        public final String maskEmotion;       // What emotion they show when defending
        public final float activationThreshold; // Stress level needed to trigger (0.0-1.0)
        public final String[] typicalResponses; // What they typically say when defending
        
        public DefensiveMechanism(String mechanism, String triggerCondition, String maskEmotion, 
                                float activationThreshold, String[] typicalResponses) {
            this.mechanism = mechanism;
            this.triggerCondition = triggerCondition;
            this.maskEmotion = maskEmotion;
            this.activationThreshold = activationThreshold;
            this.typicalResponses = typicalResponses;
        }
    }
    
    /**
     * Initialize master-specific defensive mechanisms
     */
    private Map<String, DefensiveMechanism> initializeDefensiveMechanisms() {
        Map<String, DefensiveMechanism> mechanisms = new HashMap<>();
        
        switch (masterName.toLowerCase()) {
            case "fischer":
                // Fischer hides insecurity behind aggression
                mechanisms.put("insecurity_defense", new DefensiveMechanism(
                    "aggression", "feeling_insecure", "confident", 0.3f,
                    new String[]{"That's obviously wrong!", "I see everything clearly!", "This is the only move!"}
                ));
                mechanisms.put("criticism_defense", new DefensiveMechanism(
                    "dismissal", "being_criticized", "dismissive", 0.2f,
                    new String[]{"You don't understand the position.", "That's amateur thinking.", "Wrong approach entirely."}
                ));
                break;
                
            case "carlsen":
                // Carlsen masks frustration with analytical detachment
                mechanisms.put("frustration_defense", new DefensiveMechanism(
                    "analytical_detachment", "feeling_frustrated", "analytical", 0.4f,
                    new String[]{"Let me calculate this objectively.", "We need to look at the facts.", "The position requires careful analysis."}
                ));
                mechanisms.put("pressure_defense", new DefensiveMechanism(
                    "understatement", "feeling_pressured", "calm", 0.3f,
                    new String[]{"It's an interesting position.", "There are possibilities.", "We'll see what happens."}
                ));
                break;
                
            case "tal":
                // Tal covers disappointment with humor and creativity
                mechanisms.put("disappointment_defense", new DefensiveMechanism(
                    "humor", "feeling_disappointed", "playful", 0.2f,
                    new String[]{"Well, that's chess for you!", "Time for some magic!", "Let's make things interesting!"}
                ));
                mechanisms.put("pressure_defense", new DefensiveMechanism(
                    "creative_escape", "feeling_pressured", "creative", 0.3f,
                    new String[]{"This calls for imagination!", "Let's find the beautiful solution!", "There's always a brilliant move!"}
                ));
                break;
                
            case "anand":
                // Anand hides competitiveness behind politeness
                mechanisms.put("competitive_defense", new DefensiveMechanism(
                    "diplomatic_politeness", "feeling_competitive", "diplomatic", 0.5f,
                    new String[]{"That's an interesting approach.", "I can see the merit in that.", "Both sides have chances."}
                ));
                mechanisms.put("frustration_defense", new DefensiveMechanism(
                    "gracious_acceptance", "feeling_frustrated", "gracious", 0.4f,
                    new String[]{"These positions are always complex.", "Chess teaches us patience.", "Every move has its logic."}
                ));
                break;
                
            case "kasparov":
                // Kasparov masks doubt with philosophical intensity
                mechanisms.put("doubt_defense", new DefensiveMechanism(
                    "philosophical_intensity", "feeling_doubtful", "philosophical", 0.3f,
                    new String[]{"This is what chess is about!", "Great positions demand great understanding!", "The struggle reveals truth!"}
                ));
                mechanisms.put("criticism_defense", new DefensiveMechanism(
                    "intellectual_superiority", "being_criticized", "intellectual", 0.2f,
                    new String[]{"You're missing the deeper point.", "This requires higher understanding.", "The position speaks for itself."}
                ));
                break;
                
            case "karpov":
                // Karpov hides emotion behind technical precision
                mechanisms.put("emotion_defense", new DefensiveMechanism(
                    "technical_precision", "feeling_emotional", "technical", 0.6f,
                    new String[]{"The position has specific demands.", "Technique is everything here.", "Precision is required."}
                ));
                break;
                
            default:
                // Default defensive mechanism
                mechanisms.put("general_defense", new DefensiveMechanism(
                    "analytical_withdrawal", "feeling_stressed", "analytical", 0.4f,
                    new String[]{"Let me think about this.", "This needs careful consideration.", "The position is complex."}
                ));
                break;
        }
        
        return mechanisms;
    }
    
    /**
     * Get master-specific mask stability (how well they can hide emotions)
     */
    private float getMasterMaskStability(String masterName) {
        switch (masterName.toLowerCase()) {
            case "karpov": return 0.9f;      // Extremely controlled emotional expression
            case "anand": return 0.8f;       // Diplomatic, rarely shows true feelings
            case "carlsen": return 0.7f;     // Generally calm but can show frustration
            case "botvinnik": return 0.7f;   // Scientific approach to emotions
            case "kasparov": return 0.5f;    // Passionate but tries to control it
            case "kramnik": return 0.6f;     // Philosophical detachment
            case "capablanca": return 0.8f;  // Natural elegance and composure
            case "alekhine": return 0.4f;    // Emotional and expressive
            case "morphy": return 0.6f;      // Gentleman's composure
            case "lasker": return 0.5f;      // Philosophical but passionate
            case "fischer": return 0.3f;     // Emotional volatility, poor emotional control
            case "tal": return 0.2f;         // Highly expressive, wears emotions openly
            default: return 0.5f;            // Average emotional control
        }
    }
    
    /**
     * Apply emotional stress and potentially trigger defensive mechanisms
     */
    public void applyEmotionalStress(String trigger, float stressIntensity) {
        this.stressLevel = Math.min(1.0f, this.stressLevel + stressIntensity);
        this.emotionalTrigger = trigger;
        
        // Check if stress breaks through current mask
        if (stressLevel > maskStability && !maskSlipping) {
            triggerMaskBreakthrough(trigger);
        }
        
        // Check if defensive mechanisms should activate
        checkDefensiveMechanisms(trigger, stressLevel);
        
        Log.d(TAG, String.format("🔥 %s stress applied: trigger=%s, intensity=%.2f, total_stress=%.2f", 
                                masterName, trigger, stressIntensity, stressLevel));
    }
    
    /**
     * Trigger a mask breakthrough - their true emotions show through
     */
    private void triggerMaskBreakthrough(String trigger) {
        this.maskSlipping = true;
        this.lastBreakthroughTime = System.currentTimeMillis();
        
        // Surface emotion briefly becomes underlying emotion
        String originalSurface = surfaceEmotion;
        this.surfaceEmotion = underlyingEmotion;
        
        // Reduce mask effectiveness
        this.emotionalMask = Math.max(0.0f, emotionalMask - 0.4f);
        
        Log.d(TAG, String.format("💥 MASK BREAKTHROUGH: %s's %s mask slipped, revealing %s! (trigger: %s)", 
                                masterName, originalSurface, underlyingEmotion, trigger));
    }
    
    /**
     * Check if defensive mechanisms should activate
     */
    private void checkDefensiveMechanisms(String trigger, float currentStress) {
        for (DefensiveMechanism mechanism : defensiveMechanisms.values()) {
            if (currentStress >= mechanism.activationThreshold && 
                isRelevantTrigger(trigger, mechanism.triggerCondition)) {
                
                activateDefensiveMechanism(mechanism);
                break; // Only one mechanism at a time
            }
        }
    }
    
    /**
     * Activate a defensive mechanism
     */
    private void activateDefensiveMechanism(DefensiveMechanism mechanism) {
        // Create emotional mask
        this.defensiveResponse = mechanism.mechanism;
        this.emotionalMask = Math.min(1.0f, emotionalMask + 0.5f);
        
        // Surface emotion becomes the defensive mask
        String originalSurface = surfaceEmotion;
        this.surfaceEmotion = mechanism.maskEmotion;
        
        Log.d(TAG, String.format("🛡️ %s activated %s defense: %s -> %s (mask: %.2f)", 
                                masterName, mechanism.mechanism, originalSurface, 
                                surfaceEmotion, emotionalMask));
    }
    
    /**
     * Check if a trigger is relevant to a defensive mechanism
     */
    private boolean isRelevantTrigger(String actualTrigger, String mechanismTrigger) {
        // Simple keyword matching - could be enhanced with more sophisticated logic
        return actualTrigger.toLowerCase().contains(mechanismTrigger.toLowerCase()) ||
               mechanismTrigger.toLowerCase().contains(actualTrigger.toLowerCase());
    }
    
    /**
     * Update underlying emotion (what they actually feel)
     */
    public void updateUnderlyingEmotion(String newEmotion, String trigger) {
        this.underlyingEmotion = newEmotion;
        this.emotionalTrigger = trigger;
        
        // If no mask is active, surface emotion matches underlying
        if (emotionalMask < 0.2f) {
            this.surfaceEmotion = newEmotion;
        }
        
        Log.d(TAG, String.format("💭 %s underlying emotion: %s (surface: %s, mask: %.2f)", 
                                masterName, underlyingEmotion, surfaceEmotion, emotionalMask));
    }
    
    /**
     * Gradually restore emotional mask over time
     */
    public void restoreEmotionalEquilibrium(float recoveryRate) {
        // Reduce stress level
        this.stressLevel = Math.max(0.0f, stressLevel - recoveryRate);
        
        // Restore mask if stress is low
        if (stressLevel < 0.3f && maskSlipping) {
            this.maskSlipping = false;
            this.emotionalMask = Math.min(maskStability, emotionalMask + recoveryRate * 2);
            
            Log.d(TAG, String.format("🔄 %s emotional equilibrium restoring: stress=%.2f, mask=%.2f", 
                                    masterName, stressLevel, emotionalMask));
        }
    }
    
    /**
     * Get the current emotional complexity for conversation generation
     */
    public EmotionalComplexity getCurrentComplexity() {
        return new EmotionalComplexity(
            surfaceEmotion, underlyingEmotion, defensiveResponse,
            emotionalMask, stressLevel, maskSlipping, emotionalTrigger
        );
    }
    
    /**
     * Represents the full emotional complexity of a master at any moment
     */
    public static class EmotionalComplexity {
        public final String surfaceEmotion;
        public final String underlyingEmotion;
        public final String defensiveResponse;
        public final float maskIntensity;
        public final float stressLevel;
        public final boolean isVulnerable;  // mask slipping
        public final String emotionalTrigger;
        
        public EmotionalComplexity(String surfaceEmotion, String underlyingEmotion, 
                                 String defensiveResponse, float maskIntensity, 
                                 float stressLevel, boolean isVulnerable, String emotionalTrigger) {
            this.surfaceEmotion = surfaceEmotion;
            this.underlyingEmotion = underlyingEmotion;
            this.defensiveResponse = defensiveResponse;
            this.maskIntensity = maskIntensity;
            this.stressLevel = stressLevel;
            this.isVulnerable = isVulnerable;
            this.emotionalTrigger = emotionalTrigger;
        }
        
        /**
         * Generate context for Responses API that includes emotional complexity
         */
        public String generateEmotionalContext() {
            StringBuilder context = new StringBuilder();
            
            if (maskIntensity > 0.3f) {
                context.append(String.format("You are showing %s on the surface but actually feeling %s underneath. ", 
                                            surfaceEmotion, underlyingEmotion));
                
                if (!defensiveResponse.equals("none")) {
                    context.append(String.format("You're using %s as a defense mechanism. ", defensiveResponse));
                }
                
                if (isVulnerable) {
                    context.append("Your emotional mask is slipping - your true feelings might show through. ");
                }
            } else {
                context.append(String.format("You are genuinely feeling %s. ", surfaceEmotion));
            }
            
            if (stressLevel > 0.5f) {
                context.append("You're under significant emotional pressure. ");
            }
            
            return context.toString();
        }
        
        /**
         * Check if this master might have an emotional breakthrough
         */
        public boolean shouldTriggerBreakthrough() {
            return isVulnerable && stressLevel > 0.6f && maskIntensity > 0.4f;
        }
        
        /**
         * Get the contrast between surface and underlying emotions
         */
        public float getEmotionalContrast() {
            if (surfaceEmotion.equals(underlyingEmotion)) {
                return 0.0f;
            }
            return maskIntensity; // Higher mask = more contrast
        }
    }
    
    // Getters for the emotional layer properties
    public String getSurfaceEmotion() { return surfaceEmotion; }
    public String getUnderlyingEmotion() { return underlyingEmotion; }
    public String getDefensiveResponse() { return defensiveResponse; }
    public float getEmotionalMask() { return emotionalMask; }
    public float getStressLevel() { return stressLevel; }
    public boolean isMaskSlipping() { return maskSlipping; }
    public String getEmotionalTrigger() { return emotionalTrigger; }
    public String getMasterName() { return masterName; }
    
    /**
     * Force an emotional mask (for testing or special situations)
     */
    public void forceEmotionalMask(String surfaceEmotion, String underlyingEmotion, String defensiveResponse) {
        this.surfaceEmotion = surfaceEmotion;
        this.underlyingEmotion = underlyingEmotion;
        this.defensiveResponse = defensiveResponse;
        this.emotionalMask = 0.8f;
        
        Log.d(TAG, String.format("🎭 Forced emotional mask for %s: %s hiding %s (%s)", 
                                masterName, surfaceEmotion, underlyingEmotion, defensiveResponse));
    }
}