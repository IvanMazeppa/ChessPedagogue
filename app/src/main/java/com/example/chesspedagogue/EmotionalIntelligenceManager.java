package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * ENHANCED EMOTIONAL INTELLIGENCE SYSTEM
 * 
 * Builds on the existing emotional detection to create a more nuanced, 
 * master-specific emotional response system that considers:
 * - Master personality traits and playing styles
 * - Long-term emotional trends and momentum
 * - Situational context (opening, middlegame, endgame)
 * - Historical emotional patterns
 */
public class EmotionalIntelligenceManager {
    private static final String TAG = "EmotionalIntelligence";
    private static EmotionalIntelligenceManager instance;
    
    private final Context context;
    private final EvaluationTracker evaluationTracker;
    private final Map<String, EmotionalProfile> masterProfiles;
    private final Map<String, EmotionalHistory> sessionHistory;
    
    // Enhanced emotional states with intensity levels
    public enum EmotionalState {
        // Positive emotions with intensity
        ECSTATIC(5, "ecstatic", "This is pure chess magic!"),
        THRILLED(4, "thrilled", "What an incredible sequence!"),
        EXCITED(3, "excited", "This is getting interesting!"),
        PLEASED(2, "pleased", "A solid move."),
        CONTENT(1, "content", "Things are going well."),
        
        // Negative emotions with intensity  
        DEVASTATED(5, "devastated", "This is a disaster!"),
        FRUSTRATED(4, "frustrated", "How could this happen?"),
        CONCERNED(3, "concerned", "This doesn't look good."),
        UNEASY(2, "uneasy", "Something feels off."),
        FOCUSED(1, "focused", "Time to concentrate."),
        
        // Contextual emotions
        ANALYTICAL(0, "analytical", "Let me think about this."),
        IMPRESSED(0, "impressed", "That's quite clever!"),
        INTRIGUED(0, "intrigued", "Interesting choice..."),
        CONFIDENT(0, "confident", "I know what to do here."),
        NOSTALGIC(0, "nostalgic", "This reminds me of..."),
        COMPETITIVE(0, "competitive", "Game on!"),
        PHILOSOPHICAL(0, "philosophical", "Chess teaches us..."),
        PLAYFUL(0, "playful", "Let's have some fun!");
        
        public final int intensity;
        public final String name;
        public final String defaultExpression;
        
        EmotionalState(int intensity, String name, String defaultExpression) {
            this.intensity = intensity;
            this.name = name;
            this.defaultExpression = defaultExpression;
        }
    }
    
    /**
     * Master-specific emotional characteristics
     */
    public static class EmotionalProfile {
        public final String masterName;
        public final Map<String, Float> emotionalTendencies;
        public final Map<String, String[]> masterSpecificExpressions;
        
        public EmotionalProfile(String masterName) {
            this.masterName = masterName;
            this.emotionalTendencies = new HashMap<>();
            this.masterSpecificExpressions = new HashMap<>();
            initializeMasterProfile();
        }
        
        private void initializeMasterProfile() {
            switch (masterName.toLowerCase()) {
                case "tal":
                    // Tal: Highly expressive, dramatic, loves tactical brilliancies
                    emotionalTendencies.put("excitement_multiplier", 1.5f);
                    emotionalTendencies.put("drama_factor", 2.0f);
                    emotionalTendencies.put("tactical_bonus", 1.3f);
                    
                    masterSpecificExpressions.put("thrilled", new String[]{
                        "This is pure magic on the board!",
                        "Beautiful, absolutely beautiful!",
                        "The pieces are dancing!"
                    });
                    masterSpecificExpressions.put("frustrated", new String[]{
                        "No, no, this doesn't feel right...",
                        "The position has lost its fire.",
                        "Where is the beauty in this?"
                    });
                    break;
                    
                case "fischer":
                    // Fischer: Intense, perfectionist, confidence swings dramatically
                    emotionalTendencies.put("confidence_swings", 2.0f);
                    emotionalTendencies.put("perfectionism", 1.8f);
                    emotionalTendencies.put("intensity", 1.7f);
                    
                    masterSpecificExpressions.put("confident", new String[]{
                        "This is the only move.",
                        "Chess is mental torture - and I'm winning.",
                        "I see everything clearly now."
                    });
                    masterSpecificExpressions.put("frustrated", new String[]{
                        "This is completely wrong!",
                        "How did I miss that?",
                        "Unacceptable. Simply unacceptable."
                    });
                    break;
                    
                case "carlsen":
                    // Carlsen: Calm, strategic, measured emotional responses
                    emotionalTendencies.put("emotional_stability", 1.3f);
                    emotionalTendencies.put("endgame_confidence", 1.5f);
                    emotionalTendencies.put("practical_focus", 1.4f);
                    
                    masterSpecificExpressions.put("analytical", new String[]{
                        "Let's find the most accurate continuation.",
                        "This requires precise calculation.",
                        "I need to understand the position better."
                    });
                    masterSpecificExpressions.put("confident", new String[]{
                        "I'm comfortable in this position.",
                        "The endgame looks promising.",
                        "Step by step, we'll get there."
                    });
                    break;
                    
                case "anand":
                    // Anand: Humble, encouraging, balanced emotional responses
                    emotionalTendencies.put("encouragement_factor", 1.6f);
                    emotionalTendencies.put("humility", 1.4f);
                    emotionalTendencies.put("adaptability", 1.5f);
                    
                    masterSpecificExpressions.put("pleased", new String[]{
                        "That's a very nice move!",
                        "I appreciate the creativity here.",
                        "This shows good understanding."
                    });
                    masterSpecificExpressions.put("concerned", new String[]{
                        "Perhaps we should reconsider...",
                        "This might not be the best path.",
                        "Let's think about alternatives."
                    });
                    break;
                    
                default:
                    // Default profile for other masters
                    emotionalTendencies.put("balance", 1.0f);
            }
        }
        
        public float getEmotionalModifier(String emotionType) {
            return emotionalTendencies.getOrDefault(emotionType, 1.0f);
        }
        
        public String[] getExpressions(String emotionType) {
            return masterSpecificExpressions.getOrDefault(emotionType, new String[]{});
        }
    }
    
    /**
     * Track emotional patterns over time
     */
    public static class EmotionalHistory {
        private final List<EmotionalEvent> events;
        private EmotionalState currentDominantEmotion;
        private float emotionalMomentum; // -1.0 to 1.0
        private int consecutiveSimilarEmotions;
        
        public EmotionalHistory() {
            this.events = new ArrayList<>();
            this.currentDominantEmotion = EmotionalState.ANALYTICAL;
            this.emotionalMomentum = 0.0f;
            this.consecutiveSimilarEmotions = 0;
        }
        
        public static class EmotionalEvent {
            public final EmotionalState emotion;
            public final float intensity;
            public final long timestamp;
            public final String trigger;
            
            public EmotionalEvent(EmotionalState emotion, float intensity, String trigger) {
                this.emotion = emotion;
                this.intensity = intensity;
                this.timestamp = System.currentTimeMillis();
                this.trigger = trigger;
            }
        }
        
        public void addEmotionalEvent(EmotionalState emotion, float intensity, String trigger) {
            events.add(new EmotionalEvent(emotion, intensity, trigger));
            updateEmotionalMomentum(emotion, intensity);
            updateDominantEmotion(emotion);
            
            // Keep history manageable
            if (events.size() > 20) {
                events.remove(0);
            }
        }
        
        private void updateEmotionalMomentum(EmotionalState emotion, float intensity) {
            float emotionValue = 0;
            
            if (emotion.intensity > 0) {
                emotionValue = emotion.intensity * intensity / 5.0f; // Positive emotions
            } else if (emotion.intensity < 0) {
                emotionValue = emotion.intensity * intensity / 5.0f; // Negative emotions  
            }
            
            // Apply momentum with decay
            emotionalMomentum = (emotionalMomentum * 0.8f) + (emotionValue * 0.2f);
            emotionalMomentum = Math.max(-1.0f, Math.min(1.0f, emotionalMomentum));
        }
        
        private void updateDominantEmotion(EmotionalState newEmotion) {
            if (currentDominantEmotion == newEmotion) {
                consecutiveSimilarEmotions++;
            } else {
                consecutiveSimilarEmotions = 1;
                currentDominantEmotion = newEmotion;
            }
        }
        
        public EmotionalState getCurrentDominantEmotion() {
            return currentDominantEmotion;
        }
        
        public float getEmotionalMomentum() {
            return emotionalMomentum;
        }
        
        public boolean isEmotionallyStable() {
            return Math.abs(emotionalMomentum) < 0.3f;
        }
        
        public boolean isOnEmotionalStreak() {
            return consecutiveSimilarEmotions >= 3;
        }
    }
    
    private EmotionalIntelligenceManager(Context context) {
        this.context = context.getApplicationContext();
        this.evaluationTracker = EvaluationTracker.getInstance(context);
        this.masterProfiles = new HashMap<>();
        this.sessionHistory = new HashMap<>();
        
        initializeMasterProfiles();
        Log.d(TAG, "🎭 Enhanced Emotional Intelligence Manager initialized");
    }
    
    public static synchronized EmotionalIntelligenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new EmotionalIntelligenceManager(context);
        }
        return instance;
    }
    
    private void initializeMasterProfiles() {
        String[] masters = {"tal", "fischer", "carlsen", "anand", "kasparov", "karpov", 
                           "kramnik", "alekhine", "capablanca", "morphy", "lasker", "botvinnik"};
        
        for (String master : masters) {
            masterProfiles.put(master, new EmotionalProfile(master));
        }
    }
    
    /**
     * MAIN METHOD: Analyze current emotional state with enhanced intelligence
     */
    public EmotionalAnalysisResult analyzeEmotionalState(String masterName, String gameContext, 
                                                        String conversationContext) {
        return analyzeEmotionalState(masterName, gameContext, conversationContext, null, null);
    }
    
    /**
     * ENHANCED: Analyze emotional state with direct evaluation data (for spectator mode)
     */
    public EmotionalAnalysisResult analyzeEmotionalState(String masterName, String gameContext, 
                                                        String conversationContext, Float currentEval, Float evalChange) {
        String masterKey = masterName.toLowerCase();
        EmotionalProfile profile = masterProfiles.getOrDefault(masterKey, 
                                    new EmotionalProfile("default"));
        
        // Get or create emotional history for this session
        EmotionalHistory history = sessionHistory.computeIfAbsent(masterKey, 
                                    k -> new EmotionalHistory());
        
        // Step 1: Analyze evaluation-based emotions (building on existing system)
        EmotionalState baseEmotion = analyzeEvaluationBasedEmotion(currentEval, evalChange);
        
        // Step 2: Apply master-specific modifications
        EmotionalState adjustedEmotion = applyMasterPersonality(baseEmotion, profile, gameContext);
        
        // Step 3: Consider emotional momentum and history
        EmotionalState finalEmotion = considerEmotionalMomentum(adjustedEmotion, history);
        
        // Step 4: Generate contextual emotional response
        String emotionalExpression = generateEmotionalExpression(finalEmotion, profile, 
                                                                conversationContext);
        
        // Step 5: Update emotional history
        float intensity = calculateEmotionalIntensity(baseEmotion, profile);
        history.addEmotionalEvent(finalEmotion, intensity, "evaluation_change");
        
        Log.d(TAG, String.format("🎭 Emotional analysis for %s: %s (intensity: %.2f, momentum: %.2f)", 
               masterName, finalEmotion.name, intensity, history.getEmotionalMomentum()));
        
        return new EmotionalAnalysisResult(finalEmotion, emotionalExpression, intensity, 
                                         history.getEmotionalMomentum());
    }
    
    /**
     * Analyze emotions based on evaluation changes (integrates with existing system)
     */
    private EmotionalState analyzeEvaluationBasedEmotion(Float currentEval, Float evalChange) {
        // Use passed parameters first, then fallback to tracker
        if (currentEval == null || evalChange == null) {
            evalChange = evaluationTracker.getRecentEvaluationChange();
            currentEval = evaluationTracker.getCurrentEvaluation();
        }
        
        if (evalChange == null || currentEval == null) {
            Log.d(TAG, "🎭 No evaluation data available - defaulting to ANALYTICAL");
            return EmotionalState.ANALYTICAL;
        }
        
        Log.d(TAG, String.format("🎭 Evaluation analysis: current=%.2f, change=%.2f", currentEval, evalChange));
        
        float absChange = Math.abs(evalChange);
        boolean isPositiveChange = evalChange > 0;
        
        if (isPositiveChange) {
            if (absChange > 4.0f || currentEval > 5.0f) {
                return EmotionalState.ECSTATIC;
            } else if (absChange > 2.5f || currentEval > 3.0f) {
                return EmotionalState.THRILLED;
            } else if (absChange > 1.5f || currentEval > 1.5f) {
                return EmotionalState.EXCITED;
            } else if (absChange > 0.8f) {
                return EmotionalState.PLEASED;
            } else if (absChange > 0.3f) {
                return EmotionalState.CONTENT;
            }
        } else {
            if (absChange > 4.0f || currentEval < -5.0f) {
                return EmotionalState.DEVASTATED;
            } else if (absChange > 2.5f || currentEval < -3.0f) {
                return EmotionalState.FRUSTRATED;
            } else if (absChange > 1.5f || currentEval < -1.5f) {
                return EmotionalState.CONCERNED;
            } else if (absChange > 0.8f) {
                return EmotionalState.UNEASY;
            } else if (absChange > 0.3f) {
                return EmotionalState.FOCUSED;
            }
        }
        
        return EmotionalState.ANALYTICAL;
    }
    
    /**
     * Apply master-specific personality modifications to emotions
     */
    private EmotionalState applyMasterPersonality(EmotionalState baseEmotion, 
                                                 EmotionalProfile profile, String gameContext) {
        
        // Tal: Amplifies excitement for tactical positions
        if (profile.masterName.equals("tal")) {
            if (gameContext != null && (gameContext.contains("tactic") || gameContext.contains("sacrifice"))) {
                if (baseEmotion == EmotionalState.PLEASED) {
                    return EmotionalState.EXCITED;
                } else if (baseEmotion == EmotionalState.EXCITED) {
                    return EmotionalState.THRILLED;
                }
            }
        }
        
        // Fischer: More intense emotional swings
        else if (profile.masterName.equals("fischer")) {
            float intensityMod = profile.getEmotionalModifier("intensity");
            if (intensityMod > 1.5f) {
                switch (baseEmotion) {
                    case PLEASED: return EmotionalState.CONFIDENT;
                    case CONCERNED: return EmotionalState.FRUSTRATED;
                    case CONTENT: return EmotionalState.CONFIDENT;
                }
            }
        }
        
        // Carlsen: More stable, less extreme emotions
        else if (profile.masterName.equals("carlsen")) {
            switch (baseEmotion) {
                case ECSTATIC: return EmotionalState.CONFIDENT;
                case DEVASTATED: return EmotionalState.FOCUSED;
                case THRILLED: return EmotionalState.PLEASED;
                case FRUSTRATED: return EmotionalState.ANALYTICAL;
            }
        }
        
        // Anand: Tends towards encouraging and balanced emotions
        else if (profile.masterName.equals("anand")) {
            switch (baseEmotion) {
                case FRUSTRATED: return EmotionalState.CONCERNED;
                case DEVASTATED: return EmotionalState.FOCUSED;
                case ECSTATIC: return EmotionalState.PLEASED;
            }
        }
        
        return baseEmotion;
    }
    
    /**
     * Consider emotional momentum and recent history
     */
    private EmotionalState considerEmotionalMomentum(EmotionalState currentEmotion, 
                                                    EmotionalHistory history) {
        
        float momentum = history.getEmotionalMomentum();
        
        // If on an emotional streak, consider dampening or amplifying
        if (history.isOnEmotionalStreak()) {
            if (momentum > 0.5f && currentEmotion.intensity < 3) {
                // Positive streak - amplify positive emotions
                return amplifyEmotion(currentEmotion, 1);
            } else if (momentum < -0.5f && currentEmotion.intensity < 3) {
                // Negative streak - might dampen to show resilience
                return dampenEmotion(currentEmotion, 1);
            }
        }
        
        return currentEmotion;
    }
    
    private EmotionalState amplifyEmotion(EmotionalState emotion, int levels) {
        // Implementation to return a more intense version of the emotion
        switch (emotion) {
            case CONTENT: return EmotionalState.PLEASED;
            case PLEASED: return EmotionalState.EXCITED;
            case EXCITED: return EmotionalState.THRILLED;
            case UNEASY: return EmotionalState.CONCERNED;
            case CONCERNED: return EmotionalState.FRUSTRATED;
            default: return emotion;
        }
    }
    
    private EmotionalState dampenEmotion(EmotionalState emotion, int levels) {
        // Implementation to return a less intense version of the emotion
        switch (emotion) {
            case THRILLED: return EmotionalState.EXCITED;
            case EXCITED: return EmotionalState.PLEASED;
            case PLEASED: return EmotionalState.CONTENT;
            case FRUSTRATED: return EmotionalState.CONCERNED;
            case CONCERNED: return EmotionalState.UNEASY;
            default: return emotion;
        }
    }
    
    /**
     * Generate master-specific emotional expressions
     */
    private String generateEmotionalExpression(EmotionalState emotion, EmotionalProfile profile, 
                                             String conversationContext) {
        
        String[] expressions = profile.getExpressions(emotion.name);
        
        if (expressions.length > 0) {
            // Use master-specific expression
            java.util.Random rand = new java.util.Random();
            return expressions[rand.nextInt(expressions.length)];
        }
        
        // Fallback to default expression
        return emotion.defaultExpression;
    }
    
    /**
     * Calculate emotional intensity based on evaluation and master personality
     */
    private float calculateEmotionalIntensity(EmotionalState baseEmotion, EmotionalProfile profile) {
        float baseIntensity = Math.abs(baseEmotion.intensity) / 5.0f; // Normalize to 0-1
        
        // Apply master-specific modifiers
        if (profile.masterName.equals("tal")) {
            baseIntensity *= profile.getEmotionalModifier("excitement_multiplier");
        } else if (profile.masterName.equals("fischer")) {
            baseIntensity *= profile.getEmotionalModifier("intensity");
        }
        
        return Math.max(0.0f, Math.min(1.0f, baseIntensity));
    }
    
    /**
     * Result class containing all emotional analysis data
     */
    public static class EmotionalAnalysisResult {
        public final EmotionalState emotion;
        public final String expression;
        public final float intensity;
        public final float momentum;
        
        public EmotionalAnalysisResult(EmotionalState emotion, String expression, 
                                     float intensity, float momentum) {
            this.emotion = emotion;
            this.expression = expression;
            this.intensity = intensity;
            this.momentum = momentum;
        }
        
        public boolean shouldInfluenceVoice() {
            return intensity > 0.3f; // Only affect voice for significant emotions
        }
        
        public boolean shouldInterruptConversation() {
            return intensity > 0.7f; // High intensity emotions can interrupt
        }
        
        @Override
        public String toString() {
            return String.format("EmotionalState{%s, intensity=%.2f, momentum=%.2f, expression='%s'}", 
                               emotion.name, intensity, momentum, expression);
        }
    }
    
    /**
     * Reset emotional history for a new game
     */
    public void resetEmotionalHistory(String masterName) {
        sessionHistory.put(masterName.toLowerCase(), new EmotionalHistory());
        Log.d(TAG, "🔄 Emotional history reset for " + masterName);
    }
    
    /**
     * Get current emotional state for a master
     */
    public EmotionalState getCurrentEmotionalState(String masterName) {
        EmotionalHistory history = sessionHistory.get(masterName.toLowerCase());
        return history != null ? history.getCurrentDominantEmotion() : EmotionalState.ANALYTICAL;
    }
    
    /**
     * Get emotional momentum for a master (-1.0 to 1.0)
     */
    public float getEmotionalMomentum(String masterName) {
        EmotionalHistory history = sessionHistory.get(masterName.toLowerCase());
        return history != null ? history.getEmotionalMomentum() : 0.0f;
    }
}