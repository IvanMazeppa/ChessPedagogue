package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🎭 EMOTIONAL MOMENTUM MANAGER
 * 
 * Creates dynamic emotional escalation in chess master conversations through:
 * - Game progression momentum (early calm -> late game intensity)
 * - Evaluation swing reactions (dramatic position changes)
 * - Cross-master emotional contagion (emotions spreading between masters)
 * - Conversation intensity building (longer dialogues = higher emotions)
 * - Memory-based emotional persistence (past emotions influence current state)
 * 
 * This system transforms static "analytical" conversations into dynamic,
 * emotionally evolving dialogues that feel authentic and engaging.
 */
public class EmotionalMomentumManager {
    private static final String TAG = "🎭 EmotionalMomentum";
    
    private static EmotionalMomentumManager instance;
    private final Context context;
    
    // Core momentum tracking
    private final Map<String, EmotionalMomentumState> masterMomentum;
    private final Map<String, Float> gameProgressionIntensity;
    private float globalEmotionalTension = 0.0f;
    
    // Configuration constants
    private static final float BASE_MOMENTUM_DECAY = 0.95f;  // Emotions naturally fade
    private static final float CONTAGION_RATE = 0.25f;      // How much masters influence each other
    private static final float MAX_INTENSITY = 2.0f;        // Cap emotional intensity
    private static final float GAME_PROGRESSION_FACTOR = 0.03f;  // Move count influence
    private static final float EVALUATION_SENSITIVITY = 0.8f;   // Reaction to position changes
    
    // Emotional momentum state for each master
    private static class EmotionalMomentumState {
        float currentIntensity = 0.5f;           // Current emotional intensity (0.0-2.0)
        float momentumVelocity = 0.0f;           // Rate of emotional change
        String dominantEmotion = "analytical";    // Current primary emotion
        long lastUpdateTime = System.currentTimeMillis();
        int conversationTurns = 0;               // Number of recent conversation exchanges
        float accumulatedStress = 0.0f;          // Built-up tension from game events
        
        // Emotional history for persistence
        float previousIntensity = 0.5f;
        String previousEmotion = "analytical";
        
        @Override
        public String toString() {
            return String.format("Intensity: %.2f, Emotion: %s, Turns: %d, Stress: %.2f", 
                currentIntensity, dominantEmotion, conversationTurns, accumulatedStress);
        }
    }
    
    private EmotionalMomentumManager(Context context) {
        this.context = context.getApplicationContext();
        this.masterMomentum = new ConcurrentHashMap<>();
        this.gameProgressionIntensity = new ConcurrentHashMap<>();
        
        Log.d(TAG, "🚀 Emotional Momentum Manager initialized");
    }
    
    public static synchronized EmotionalMomentumManager getInstance(Context context) {
        if (instance == null) {
            instance = new EmotionalMomentumManager(context);
        }
        return instance;
    }
    
    // ==================== CORE MOMENTUM METHODS ====================
    
    /**
     * Initialize emotional momentum for a master
     */
    public void initializeMaster(String masterName) {
        if (!masterMomentum.containsKey(masterName)) {
            EmotionalMomentumState state = new EmotionalMomentumState();
            
            // Set master-specific starting emotions based on personality
            switch (masterName.toLowerCase()) {
                case "tal":
                    state.currentIntensity = 0.8f;  // Tal starts more excited
                    state.dominantEmotion = "excited";
                    break;
                case "fischer":
                    state.currentIntensity = 0.7f;  // Fischer starts focused
                    state.dominantEmotion = "focused";
                    break;
                case "kasparov":
                    state.currentIntensity = 0.6f;  // Kasparov starts confident
                    state.dominantEmotion = "confident";
                    break;
                case "carlsen":
                    state.currentIntensity = 0.4f;  // Carlsen starts calm
                    state.dominantEmotion = "calm";
                    break;
                default:
                    state.currentIntensity = 0.5f;  // Default analytical
                    state.dominantEmotion = "analytical";
                    break;
            }
            
            masterMomentum.put(masterName, state);
            gameProgressionIntensity.put(masterName, 0.0f);
            
            Log.d(TAG, "🎭 Initialized momentum for " + masterName + ": " + state);
        }
    }
    
    /**
     * Update emotional momentum based on game progression
     */
    public void updateGameProgression(String masterName, int moveCount, float currentEvaluation, float previousEvaluation) {
        EmotionalMomentumState state = masterMomentum.get(masterName);
        if (state == null) {
            initializeMaster(masterName);
            state = masterMomentum.get(masterName);
        }
        
        // 1. Game progression intensity (moves 1-60)
        float gameProgressionBoost = Math.min(moveCount * GAME_PROGRESSION_FACTOR, 1.0f);
        gameProgressionIntensity.put(masterName, gameProgressionBoost);
        
        // 2. Evaluation swing reaction
        float evaluationChange = Math.abs(currentEvaluation - previousEvaluation);
        float evaluationBoost = Math.min(evaluationChange * EVALUATION_SENSITIVITY, 0.8f);
        
        // 3. Update emotional state
        state.accumulatedStress += evaluationBoost;
        updateMasterIntensity(masterName, gameProgressionBoost + evaluationBoost);
        
        // 4. Determine emotion based on position and intensity
        updateEmotionBasedOnPosition(masterName, currentEvaluation, evaluationChange, state.currentIntensity);
        
        Log.d(TAG, "🎯 Game progression update for " + masterName + 
              " (move " + moveCount + ", eval change: " + String.format("%.2f", evaluationChange) + "): " + state);
    }
    
    /**
     * Update momentum based on conversation dynamics
     */
    public void updateConversationMomentum(String masterName, String dialogueContent, String opponentName) {
        EmotionalMomentumState state = masterMomentum.get(masterName);
        if (state == null) {
            initializeMaster(masterName);
            state = masterMomentum.get(masterName);
        }
        
        // Increment conversation turns
        state.conversationTurns++;
        
        // Conversation intensity builds over time
        float conversationIntensityBoost = Math.min(state.conversationTurns * 0.05f, 0.5f);
        
        // Analyze dialogue for emotional content
        float dialogueEmotionalBoost = analyzeDialogueEmotionalContent(dialogueContent);
        
        // Apply momentum update
        updateMasterIntensity(masterName, conversationIntensityBoost + dialogueEmotionalBoost);
        
        // Trigger emotional contagion with opponent
        triggerEmotionalContagion(masterName, opponentName);
        
        Log.d(TAG, "💬 Conversation momentum for " + masterName + 
              " (turn " + state.conversationTurns + ", boost: " + 
              String.format("%.2f", conversationIntensityBoost + dialogueEmotionalBoost) + "): " + state);
    }
    
    /**
     * Core intensity update method
     */
    private void updateMasterIntensity(String masterName, float intensityBoost) {
        EmotionalMomentumState state = masterMomentum.get(masterName);
        if (state == null) return;
        
        // Store previous state
        state.previousIntensity = state.currentIntensity;
        state.previousEmotion = state.dominantEmotion;
        
        // Calculate new intensity with momentum
        state.momentumVelocity = (state.momentumVelocity * 0.8f) + (intensityBoost * 0.2f);
        state.currentIntensity += state.momentumVelocity;
        
        // Apply natural decay
        state.currentIntensity *= BASE_MOMENTUM_DECAY;
        
        // Clamp to valid range
        state.currentIntensity = Math.max(0.1f, Math.min(MAX_INTENSITY, state.currentIntensity));
        
        // Update timestamp
        state.lastUpdateTime = System.currentTimeMillis();
    }
    
    // ==================== EMOTIONAL CONTAGION ====================
    
    /**
     * Implement emotional contagion between masters
     */
    private void triggerEmotionalContagion(String sourceMaster, String targetMaster) {
        EmotionalMomentumState sourceState = masterMomentum.get(sourceMaster);
        EmotionalMomentumState targetState = masterMomentum.get(targetMaster);
        
        if (sourceState == null || targetState == null) return;
        
        // Calculate contagion effect based on intensity difference
        float intensityDifference = sourceState.currentIntensity - targetState.currentIntensity;
        float contagionEffect = intensityDifference * CONTAGION_RATE;
        
        // Apply contagion with master-specific susceptibility
        float susceptibility = getMasterEmotionalSusceptibility(targetMaster);
        float finalContagionEffect = contagionEffect * susceptibility;
        
        updateMasterIntensity(targetMaster, finalContagionEffect);
        
        // Emotion can spread too (with reduced probability)
        if (Math.abs(finalContagionEffect) > 0.1f && Math.random() < 0.3f) {
            EmotionalMomentumState newTargetState = masterMomentum.get(targetMaster);
            if (newTargetState != null) {
                newTargetState.dominantEmotion = blendEmotions(newTargetState.dominantEmotion, sourceState.dominantEmotion);
            }
        }
        
        Log.d(TAG, "🧬 Emotional contagion: " + sourceMaster + " (" + 
              String.format("%.2f", sourceState.currentIntensity) + ") -> " + targetMaster + 
              " (effect: " + String.format("%.2f", finalContagionEffect) + ")");
    }
    
    /**
     * Get master-specific emotional susceptibility
     */
    private float getMasterEmotionalSusceptibility(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal": return 1.2f;        // Very susceptible to others' emotions
            case "fischer": return 0.3f;    // Highly resistant to emotional influence
            case "kasparov": return 0.8f;   // Moderately influenced by strong emotions
            case "carlsen": return 0.6f;    // Somewhat resistant but not immune
            case "anand": return 1.0f;      // Balanced emotional responsiveness
            default: return 0.7f;           // Default moderate susceptibility
        }
    }
    
    // ==================== EMOTION DETERMINATION ====================
    
    /**
     * Update emotion based on position and intensity
     */
    private void updateEmotionBasedOnPosition(String masterName, float evaluation, float evaluationChange, float intensity) {
        EmotionalMomentumState state = masterMomentum.get(masterName);
        if (state == null) return;
        
        String newEmotion = determineEmotionFromContext(masterName, evaluation, evaluationChange, intensity);
        
        // Only change emotion if there's a significant shift or intensity spike
        if (!newEmotion.equals(state.dominantEmotion) && 
            (intensity > 1.0f || Math.abs(evaluationChange) > 0.5f)) {
            state.dominantEmotion = newEmotion;
            Log.d(TAG, "🎭 " + masterName + " emotional shift: " + newEmotion + 
                  " (intensity: " + String.format("%.2f", intensity) + 
                  ", eval change: " + String.format("%.2f", evaluationChange) + ")");
        }
    }
    
    /**
     * Determine emotion from game context and master personality
     */
    private String determineEmotionFromContext(String masterName, float evaluation, float evaluationChange, float intensity) {
        // Base emotion determination on evaluation and change
        boolean isWinning = evaluation > 0.5f;
        boolean isLosing = evaluation < -0.5f;
        boolean bigSwing = Math.abs(evaluationChange) > 0.3f;
        boolean highIntensity = intensity > 1.0f;
        
        // Master-specific emotional patterns
        switch (masterName.toLowerCase()) {
            case "tal":
                if (bigSwing) return highIntensity ? "exhilarated" : "excited";
                if (isWinning) return "mischievous";
                if (isLosing) return highIntensity ? "defiant" : "determined";
                return "curious";
                
            case "fischer":
                if (isWinning && highIntensity) return "triumphant";
                if (isLosing && highIntensity) return "frustrated";
                if (bigSwing) return "intense";
                return "focused";
                
            case "kasparov":
                if (bigSwing && highIntensity) return "passionate";
                if (isWinning) return "confident";
                if (isLosing) return highIntensity ? "combative" : "determined";
                return "analytical";
                
            case "carlsen":
                if (highIntensity && bigSwing) return "alert";
                if (isWinning) return "satisfied";
                if (isLosing) return "patient";
                return "calm";
                
            default:
                if (highIntensity) return bigSwing ? "excited" : "focused";
                return "analytical";
        }
    }
    
    /**
     * Analyze dialogue content for emotional indicators
     */
    private float analyzeDialogueEmotionalContent(String dialogueContent) {
        if (dialogueContent == null) return 0.0f;
        
        String content = dialogueContent.toLowerCase();
        float emotionalBoost = 0.0f;
        
        // Positive emotional words
        if (content.contains("brilliant") || content.contains("genius") || 
            content.contains("magnificent")) {
            emotionalBoost += 0.15f;
        }
        
        // Negative emotional words
        if (content.contains("terrible") || content.contains("blunder") || 
            content.contains("mistake")) {
            emotionalBoost += 0.12f;
        }
        
        // Intensity indicators
        if (content.contains("!") || content.contains("incredible") || 
            content.contains("amazing")) {
            emotionalBoost += 0.08f;
        }
        
        // Conflict indicators
        if (content.contains("wrong") || content.contains("disagree") || 
            content.contains("nonsense")) {
            emotionalBoost += 0.10f;
        }
        
        return Math.min(emotionalBoost, 0.3f); // Cap at 0.3
    }
    
    /**
     * Blend two emotions for contagion effects
     */
    private String blendEmotions(String currentEmotion, String influencingEmotion) {
        // Simple emotion blending - can be made more sophisticated
        if (currentEmotion.equals(influencingEmotion)) return currentEmotion;
        
        // High-energy emotions tend to dominate
        String[] highEnergyEmotions = {"excited", "passionate", "exhilarated", "triumphant"};
        for (String emotion : highEnergyEmotions) {
            if (influencingEmotion.equals(emotion)) return emotion;
        }
        
        return currentEmotion; // Default to keeping current emotion
    }
    
    // ==================== PUBLIC INTERFACE ====================
    
    /**
     * Get current emotional state for a master
     */
    public EmotionalState getCurrentEmotionalState(String masterName) {
        EmotionalMomentumState state = masterMomentum.get(masterName);
        if (state == null) {
            initializeMaster(masterName);
            state = masterMomentum.get(masterName);
        }
        
        return new EmotionalState(state.dominantEmotion, state.currentIntensity);
    }
    
    /**
     * Get enhanced context for AI conversations
     */
    public String getEnhancedEmotionalContext(String masterName) {
        EmotionalMomentumState state = masterMomentum.get(masterName);
        if (state == null) return "";
        
        StringBuilder context = new StringBuilder();
        context.append("EMOTIONAL STATE: You are feeling ").append(state.dominantEmotion);
        
        if (state.currentIntensity > 1.2f) {
            context.append(" very intensely");
        } else if (state.currentIntensity > 0.8f) {
            context.append(" moderately");
        } else {
            context.append(" mildly");
        }
        
        context.append(" (intensity: ").append(String.format("%.1f", state.currentIntensity)).append("/2.0). ");
        
        if (state.conversationTurns > 5) {
            context.append("This conversation has been going on for a while and you're becoming more engaged. ");
        }
        
        if (state.accumulatedStress > 0.5f) {
            context.append("The game tension is building and affecting your emotional state. ");
        }
        
        return context.toString();
    }
    
    /**
     * Reset momentum for new game
     */
    public void resetGameMomentum() {
        for (EmotionalMomentumState state : masterMomentum.values()) {
            state.conversationTurns = 0;
            state.accumulatedStress = 0.0f;
            state.currentIntensity = Math.max(0.3f, state.currentIntensity * 0.7f); // Partial reset
            state.momentumVelocity = 0.0f;
        }
        
        globalEmotionalTension = 0.0f;
        Log.d(TAG, "🔄 Reset game momentum for new game");
    }
    
    /**
     * Get momentum statistics for debugging
     */
    public String getMomentumStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("🎭 EMOTIONAL MOMENTUM STATISTICS:\n");
        
        for (Map.Entry<String, EmotionalMomentumState> entry : masterMomentum.entrySet()) {
            stats.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        stats.append("• Global tension: ").append(String.format("%.2f", globalEmotionalTension));
        return stats.toString();
    }
    
    // ==================== DATA CLASSES ====================
    
    /**
     * Simple emotional state container
     */
    public static class EmotionalState {
        public final String emotion;
        public final float intensity;
        
        public EmotionalState(String emotion, float intensity) {
            this.emotion = emotion;
            this.intensity = intensity;
        }
        
        @Override
        public String toString() {
            return emotion + " (" + String.format("%.2f", intensity) + ")";
        }
    }
}