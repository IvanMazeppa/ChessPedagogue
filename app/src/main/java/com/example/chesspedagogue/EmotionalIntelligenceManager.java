package com.example.chesspedagogue;

import android.content.Context;
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
    
    // 🧠 ENHANCED: Persistent memory integration
    private RelationshipPersistenceManager persistenceManager;
    private final List<EmotionalMemoryCallback> callbacks = new ArrayList<>();
    private String currentMaster;
    private String opponentMaster;
    
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
                    
                case "alekhine":
                    // Alekhine: ENHANCED - Intense, competitive, artistic yet volatile with passionate expressiveness
                    emotionalTendencies.put("intensity_multiplier", 2.0f); // INCREASED for more emotion
                    emotionalTendencies.put("competitive_drive", 1.9f);
                    emotionalTendencies.put("artistic_passion", 1.8f); // INCREASED for artistic expression
                    emotionalTendencies.put("volatility", 1.6f); // INCREASED for emotional range
                    emotionalTendencies.put("self_criticism", 1.5f);
                    emotionalTendencies.put("passionate_analysis", 1.9f); // NEW: Passionate analytical style
                    
                    masterSpecificExpressions.put("thrilled", new String[]{
                        "This is pure chess artistry!",
                        "Magnificent! A true masterpiece!",
                        "The position breathes with life and possibility!"
                    });
                    masterSpecificExpressions.put("frustrated", new String[]{
                        "This is unacceptable... I must do better.",
                        "The position mocks my inadequacy.",
                        "Where is the beauty I seek?"
                    });
                    masterSpecificExpressions.put("competitive", new String[]{
                        "Victory demands nothing less than perfection!",
                        "I will not yield. Not here, not ever!",
                        "Every move must serve the pursuit of excellence."
                    });
                    masterSpecificExpressions.put("analytical", new String[]{
                        "First, self-knowledge... then, understanding.",
                        "The position requires both science and art.",
                        "I must see more deeply than any other.",
                        "Every fiber of my being seeks the perfect move!", // ADDED: More passionate analysis
                        "This position speaks to my very soul!"
                    });
                    // NEW: Additional emotional states for enhanced expression
                    masterSpecificExpressions.put("excited", new String[]{
                        "Yes! The pieces come alive before my eyes!",
                        "Such dynamic harmony in this position!",
                        "The chess gods smile upon this moment!"
                    });
                    masterSpecificExpressions.put("pleased", new String[]{
                        "Ah, this satisfies my artistic sensibilities.",
                        "A move worthy of the great masters!",
                        "Beauty and logic unite in perfect harmony."
                    });
                    masterSpecificExpressions.put("confident", new String[]{
                        "I feel the position's truth flowing through me!",
                        "This is my domain - the realm of pure chess!",
                        "Victory shall be mine through superior understanding!"
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
        
        // 🧠 NEW: Methods for enhanced memory integration
        public void addHistoricalEvent(EmotionalIntelligenceManager.EmotionalEvent event) {
            // Convert external EmotionalEvent to internal EmotionalEvent
            addEmotionalEvent(event.emotion, event.intensity, event.topic);
        }
        
        public void setInitialMomentum(float momentum) {
            this.emotionalMomentum = Math.max(-1.0f, Math.min(1.0f, momentum));
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
        return analyzeEmotionalState(masterName, gameContext, conversationContext, null, null, null);
    }
    
    /**
     * ENHANCED: Analyze emotional state with inter-master context awareness
     */
    public EmotionalAnalysisResult analyzeEmotionalState(String masterName, String gameContext, 
                                                        String conversationContext, EmotionalContext emotionalContext) {
        return analyzeEmotionalState(masterName, gameContext, conversationContext, null, null, emotionalContext);
    }
    
    /**
     * ENHANCED: Analyze emotional state with direct evaluation data (for spectator mode)
     */
    public EmotionalAnalysisResult analyzeEmotionalState(String masterName, String gameContext, 
                                                        String conversationContext, Float currentEval, Float evalChange, 
                                                        EmotionalContext emotionalContext) {
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
        
        // Step 3: 🎭 NEW! Apply inter-master emotional context awareness
        EmotionalState contextAwareEmotion = applyInterMasterContext(adjustedEmotion, masterName, emotionalContext);
        
        // Step 4: Consider emotional momentum and history
        EmotionalState finalEmotion = considerEmotionalMomentum(contextAwareEmotion, history);
        
        // Step 5: Generate contextual emotional response
        String emotionalExpression = generateEmotionalExpression(finalEmotion, profile, 
                                                                conversationContext);
        
        // Step 6: Update emotional history
        float intensity = calculateEmotionalIntensity(baseEmotion, profile);
        history.addEmotionalEvent(finalEmotion, intensity, "evaluation_change");
        
        // Step 7: 🎭 UPDATE EMOTIONAL CONTEXT for inter-master awareness
        if (emotionalContext != null) {
            emotionalContext.updateEmotionalState(masterName, finalEmotion.name, intensity, 
                                                 history.getEmotionalMomentum());
        }
        
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
            // Realistic chess evaluation thresholds (0.1 = slight advantage)
            if (absChange > 2.0f || currentEval > 3.0f) {
                return EmotionalState.ECSTATIC;     // Major swing or winning
            } else if (absChange > 1.0f || currentEval > 2.0f) {
                return EmotionalState.THRILLED;     // Significant advantage
            } else if (absChange > 0.5f || currentEval > 1.0f) {
                return EmotionalState.EXCITED;      // Clear advantage
            } else if (absChange > 0.25f || currentEval > 0.5f) {
                return EmotionalState.PLEASED;      // Good position
            } else if (absChange > 0.1f || currentEval > 0.2f) {
                return EmotionalState.CONTENT;      // Small improvement
            }
        } else {
            // Negative evaluation changes (getting worse)
            if (absChange > 2.0f || currentEval < -3.0f) {
                return EmotionalState.DEVASTATED;   // Major loss or losing
            } else if (absChange > 1.0f || currentEval < -2.0f) {
                return EmotionalState.FRUSTRATED;   // Significant disadvantage
            } else if (absChange > 0.5f || currentEval < -1.0f) {
                return EmotionalState.CONCERNED;    // Clear disadvantage
            } else if (absChange > 0.25f || currentEval < -0.5f) {
                return EmotionalState.UNEASY;       // Poor position
            } else if (absChange > 0.1f || currentEval < -0.2f) {
                return EmotionalState.FOCUSED;      // Small decline
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
     * 🎭 BREAKTHROUGH: Apply inter-master emotional context awareness
     * This is where the magic happens - masters react to each other's emotions!
     */
    private EmotionalState applyInterMasterContext(EmotionalState currentEmotion, String masterName, 
                                                   EmotionalContext emotionalContext) {
        if (emotionalContext == null) {
            return currentEmotion;
        }
        
        String otherMasterEmotion = emotionalContext.getOtherMasterEmotion();
        float otherMasterIntensity = emotionalContext.getOtherMasterIntensity();
        String lastSpeaker = emotionalContext.getLastSpeaker();
        
        Log.d(TAG, String.format("🌟 INTER-MASTER CONTEXT: %s feeling %s, other master (%s) feeling %s (%.2f intensity)", 
               masterName, currentEmotion.name, emotionalContext.getOtherMaster(), otherMasterEmotion, otherMasterIntensity));
        
        // Only apply context if the other master has significant emotional state
        if (otherMasterIntensity < 0.4f) {
            return currentEmotion;
        }
        
        // Get master personalities for context-aware reactions
        String masterKey = masterName.toLowerCase();
        String otherMasterKey = emotionalContext.getOtherMaster().toLowerCase();
        
        // Apply emergent emotional dynamics based on master personalities
        EmotionalState modifiedEmotion = applyEmergentDynamics(currentEmotion, masterKey, otherMasterKey, 
                                                              otherMasterEmotion, otherMasterIntensity, lastSpeaker);
        
        if (modifiedEmotion != currentEmotion) {
            Log.d(TAG, String.format("🌟 EMERGENT BEHAVIOR: %s's emotion shifted from %s to %s due to %s being %s", 
                   masterName, currentEmotion.name, modifiedEmotion.name, 
                   emotionalContext.getOtherMaster(), otherMasterEmotion));
        }
        
        return modifiedEmotion;
    }
    
    /**
     * 🎭 EMERGENT DYNAMICS: Define how masters react to each other emotionally
     */
    private EmotionalState applyEmergentDynamics(EmotionalState myEmotion, String masterKey, String otherMasterKey,
                                                String otherEmotion, float otherIntensity, String lastSpeaker) {
        
        // Fischer vs Carlsen: Modern precision vs classical intensity
        if (masterKey.equals("fischer") && otherMasterKey.equals("carlsen")) {
            return applyFischerVsCarlsenDynamics(myEmotion, otherEmotion, otherIntensity);
        } else if (masterKey.equals("carlsen") && otherMasterKey.equals("fischer")) {
            return applyCarlsenVsFischerDynamics(myEmotion, otherEmotion, otherIntensity);
        }
        
        // Tal vs Anyone: Creative chaos meeting other styles
        else if (masterKey.equals("tal")) {
            return applyTalDynamics(myEmotion, otherMasterKey, otherEmotion, otherIntensity);
        }
        
        // Generic master dynamics - rival reactions
        else {
            return applyGenericRivalDynamics(myEmotion, otherEmotion, otherIntensity);
        }
    }
    
    /**
     * Fischer reacting to Carlsen's emotions
     */
    private EmotionalState applyFischerVsCarlsenDynamics(EmotionalState fischerEmotion, String carlsenEmotion, float intensity) {
        switch (carlsenEmotion) {
            case "confident":
                // Fischer gets more intense when facing Carlsen's calm confidence
                if (fischerEmotion == EmotionalState.PLEASED) return EmotionalState.CONFIDENT;
                if (fischerEmotion == EmotionalState.ANALYTICAL) return EmotionalState.COMPETITIVE;
                break;
                
            case "focused":
                // Fischer respects focus but wants to disrupt it
                if (fischerEmotion == EmotionalState.EXCITED) return EmotionalState.COMPETITIVE;
                break;
                
            case "devastated":
                // Fischer might show slight satisfaction but quickly refocus
                if (fischerEmotion == EmotionalState.PLEASED) return EmotionalState.CONFIDENT;
                break;
        }
        return fischerEmotion;
    }
    
    /**
     * Carlsen reacting to Fischer's emotions  
     */
    private EmotionalState applyCarlsenVsFischerDynamics(EmotionalState carlsenEmotion, String fischerEmotion, float intensity) {
        switch (fischerEmotion) {
            case "confident":
                // Carlsen remains calm but becomes more focused when Fischer is confident
                if (carlsenEmotion == EmotionalState.ANALYTICAL) return EmotionalState.FOCUSED;
                if (carlsenEmotion == EmotionalState.CONTENT) return EmotionalState.ANALYTICAL;
                break;
                
            case "frustrated":
                // Carlsen might become slightly more confident when Fischer struggles
                if (carlsenEmotion == EmotionalState.ANALYTICAL) return EmotionalState.CONFIDENT;
                break;
                
            case "ecstatic":
                // Carlsen becomes more cautious/analytical when Fischer is ecstatic
                if (carlsenEmotion == EmotionalState.PLEASED) return EmotionalState.ANALYTICAL;
                break;
        }
        return carlsenEmotion;
    }
    
    /**
     * Tal's unique reactions - he loves chaos and brings out emotions in others
     */
    private EmotionalState applyTalDynamics(EmotionalState talEmotion, String otherMasterKey, String otherEmotion, float intensity) {
        switch (otherEmotion) {
            case "analytical":
                // Tal gets excited when facing analytical opponents - wants to shake them up
                if (talEmotion == EmotionalState.PLEASED) return EmotionalState.EXCITED;
                if (talEmotion == EmotionalState.CONTENT) return EmotionalState.PLAYFUL;
                break;
                
            case "confident": 
                // Tal loves to challenge confidence with creative chaos
                if (talEmotion == EmotionalState.ANALYTICAL) return EmotionalState.INTRIGUED;
                if (talEmotion == EmotionalState.PLEASED) return EmotionalState.COMPETITIVE;
                break;
                
            case "devastated":
                // Tal might feel a mix of satisfaction and sympathy
                if (talEmotion == EmotionalState.THRILLED) return EmotionalState.IMPRESSED;
                break;
        }
        return talEmotion;
    }
    
    /**
     * Generic rivalry dynamics for other master combinations
     */
    private EmotionalState applyGenericRivalDynamics(EmotionalState myEmotion, String otherEmotion, float intensity) {
        // High intensity emotions can influence the other master
        if (intensity > 0.7f) {
            switch (otherEmotion) {
                case "ecstatic":
                    // Opponent's ecstasy makes you more focused/competitive
                    if (myEmotion == EmotionalState.ANALYTICAL) return EmotionalState.FOCUSED;
                    if (myEmotion == EmotionalState.CONTENT) return EmotionalState.COMPETITIVE;
                    break;
                    
                case "devastated":
                    // Opponent's devastation might increase confidence
                    if (myEmotion == EmotionalState.PLEASED) return EmotionalState.CONFIDENT;
                    if (myEmotion == EmotionalState.ANALYTICAL) return EmotionalState.PLEASED;
                    break;
                    
                case "confident":
                    // Opponent's confidence triggers competitive response
                    if (myEmotion == EmotionalState.ANALYTICAL) return EmotionalState.FOCUSED;
                    break;
            }
        }
        return myEmotion;
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
    
    /**
     * 🎭 NEW: Get current complete emotional analysis result for TTS voice modulation
     */
    public EmotionalAnalysisResult getCurrentEmotionalAnalysis(String masterName) {
        String masterKey = masterName.toLowerCase();
        EmotionalHistory history = sessionHistory.get(masterKey);
        
        if (history != null) {
            EmotionalState currentEmotion = history.getCurrentDominantEmotion();
            float currentIntensity = Math.max(0.3f, Math.min(1.0f, history.getEmotionalMomentum() + 0.5f)); // Convert momentum to intensity
            float currentMomentum = history.getEmotionalMomentum();
            
            // Create a simple expression for the current state
            String expression = currentEmotion.name().toLowerCase();
            
            return new EmotionalAnalysisResult(currentEmotion, expression, currentIntensity, currentMomentum);
        }
        
        // Default to analytical state if no history
        return new EmotionalAnalysisResult(EmotionalState.ANALYTICAL, "focused and analytical", 0.5f, 0.0f);
    }
    
    // =========================== 🧠 ENHANCED MEMORY INTEGRATION ===========================
    
    /**
     * 🧠 Initialize with persistent emotional history from database
     */
    public void initializeWithHistory(String masterName, String opponentName) {
        this.currentMaster = masterName;
        this.opponentMaster = opponentName;
        this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        
        try {
            // Load recent emotional history from database
            List<RelationshipPersistenceManager.EmotionalReaction> recentReactions = persistenceManager.getEmotionalHistoryBetweenMasters(
                masterName, opponentName, 20 // Last 20 emotional reactions
            );
            
            // Convert database reactions to EmotionalEvent objects and load into session history
            EmotionalHistory history = sessionHistory.computeIfAbsent(masterName.toLowerCase(), 
                                        k -> new EmotionalHistory());
            
            for (RelationshipPersistenceManager.EmotionalReaction reaction : recentReactions) {
                EmotionalEvent event = new EmotionalEvent(
                    reaction.getTopic(),
                    EmotionalState.valueOf(reaction.getEmotion().toUpperCase()),
                    reaction.getIntensity(),
                    reaction.getTimestamp()
                );
                history.addHistoricalEvent(event);
            }
            
            // Calculate initial emotional momentum from history
            float historicalMomentum = calculateHistoricalMomentum(masterName, opponentName);
            history.setInitialMomentum(historicalMomentum);
            
            Log.d(TAG, String.format("🧠 Loaded emotional history for %s vs %s: %d events, momentum: %.2f", 
                   masterName, opponentName, recentReactions.size(), historicalMomentum));
                   
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to load emotional history, using session-only mode", e);
            // Continue without historical data - graceful degradation
        }
    }
    
    /**
     * 💾 Persist significant emotional events to database
     */
    public void persistEmotionalEvent(EmotionalEvent event, String conversationSnippet) {
        if (persistenceManager == null || currentMaster == null) return;
        
        // Only persist significant emotional events (high intensity or breakthroughs)
        if (event.intensity > 0.7f || event.isBreakthrough) {
            try {
                persistenceManager.recordEmotionalReaction(
                    currentMaster,
                    opponentMaster,
                    event.topic,
                    event.emotion.name,
                    event.intensity,
                    calculateCurrentMomentum(),
                    "spectator_mode", // gameContext
                    0.0f, // positionEval
                    conversationSnippet.substring(0, Math.min(200, conversationSnippet.length()))
                );
                
                Log.d(TAG, String.format("💾 Persisted emotional event: %s feels %s about %s (intensity: %.2f)", 
                       currentMaster, event.emotion.name, event.topic, event.intensity));
                       
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to persist emotional event", e);
            }
        }
    }
    
    /**
     * 🎯 Get emotional pattern for topic and emotion combination
     */
    public EmotionalPattern getEmotionalPattern(String topic, String emotion) {
        if (persistenceManager == null || currentMaster == null) return null;
        
        try {
            // TODO: Implement getEmotionalPattern in RelationshipPersistenceManager
            // For now, return a simple pattern based on session data
            EmotionalHistory history = sessionHistory.get(currentMaster.toLowerCase());
            if (history != null) {
                // Create a simple pattern from session data
                EmotionalPattern pattern = new EmotionalPattern();
                // Pattern analysis could be implemented here
                return pattern;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to get emotional pattern", e);
            return null;
        }
    }
    
    /**
     * 📊 Calculate historical emotional momentum from database
     */
    private float calculateHistoricalMomentum(String masterName, String opponentName) {
        if (persistenceManager == null) return 0.0f;
        
        try {
            // Get recent emotional reactions and calculate trend
            List<RelationshipPersistenceManager.EmotionalReaction> recentReactions = persistenceManager.getEmotionalHistoryBetweenMasters(
                masterName, opponentName, 10
            );
            
            if (recentReactions.size() < 3) return 0.0f;
            
            float totalMomentum = 0.0f;
            float weightSum = 0.0f;
            
            for (int i = 0; i < recentReactions.size(); i++) {
                RelationshipPersistenceManager.EmotionalReaction reaction = recentReactions.get(i);
                float weight = (float) (i + 1) / recentReactions.size(); // More recent = higher weight
                
                // Convert emotion to momentum value
                float emotionValue = getEmotionMomentumValue(reaction.getEmotion());
                totalMomentum += emotionValue * reaction.getIntensity() * weight;
                weightSum += weight;
            }
            
            return weightSum > 0 ? totalMomentum / weightSum : 0.0f;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to calculate historical momentum", e);
            return 0.0f;
        }
    }
    
    /**
     * Convert emotion name to momentum value (-1.0 to 1.0)
     */
    private float getEmotionMomentumValue(String emotion) {
        try {
            EmotionalState state = EmotionalState.valueOf(emotion.toUpperCase());
            switch (state) {
                case ECSTATIC: return 1.0f;
                case THRILLED: return 0.8f;
                case EXCITED: return 0.6f;
                case PLEASED: return 0.4f;
                case CONTENT: return 0.2f;
                case ANALYTICAL: return 0.0f;
                case FOCUSED: return 0.0f;
                case UNEASY: return -0.2f;
                case CONCERNED: return -0.4f;
                case FRUSTRATED: return -0.6f;
                case DEVASTATED: return -1.0f;
                default: return 0.0f;
            }
        } catch (Exception e) {
            return 0.0f;
        }
    }
    
    /**
     * 📈 Calculate current session momentum
     */
    private float calculateCurrentMomentum() {
        if (currentMaster == null) return 0.0f;
        
        EmotionalHistory history = sessionHistory.get(currentMaster.toLowerCase());
        return history != null ? history.getEmotionalMomentum() : 0.0f;
    }
    
    /**
     * 🎭 Callback interface for emotional memory events
     */
    public interface EmotionalMemoryCallback {
        void onEmotionalBreakthrough(String master, String emotion, String topic, float intensity);
        void onEmotionalPatternDetected(String master, EmotionalPattern pattern);
        void onRelationshipEvolution(String master1, String master2, float oldValue, float newValue);
    }
    
    /**
     * 📋 Register callback for emotional memory events
     */
    public void registerCallback(EmotionalMemoryCallback callback) {
        callbacks.add(callback);
    }
    
    /**
     * 🚨 Notify callbacks of emotional breakthrough
     */
    private void notifyBreakthrough(EmotionalEvent event) {
        for (EmotionalMemoryCallback callback : callbacks) {
            try {
                callback.onEmotionalBreakthrough(
                    currentMaster, 
                    event.emotion.name, 
                    event.topic, 
                    event.intensity
                );
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in breakthrough callback", e);
            }
        }
    }
    
    /**
     * 🎯 Enhanced emotional event class with persistence support
     */
    public static class EmotionalEvent {
        public final String topic;
        public final EmotionalState emotion;
        public final float intensity;
        public final long timestamp;
        public final boolean isBreakthrough;
        
        public EmotionalEvent(String topic, EmotionalState emotion, float intensity, long timestamp) {
            this.topic = topic;
            this.emotion = emotion;
            this.intensity = intensity;
            this.timestamp = timestamp;
            this.isBreakthrough = intensity > 0.8f; // High intensity = breakthrough
        }
        
        public EmotionalEvent(String topic, EmotionalState emotion, float intensity) {
            this(topic, emotion, intensity, System.currentTimeMillis());
        }
    }
    
    /**
     * 📊 Emotional pattern class for trend analysis
     */
    public static class EmotionalPattern {
        private int occurrences = 0;
        private float averageIntensity = 0;
        private long lastOccurrence = 0;
        private float trend = 0; // Positive = intensifying, Negative = diminishing
        
        public void addOccurrence(EmotionalEvent event) {
            // Update average intensity
            averageIntensity = (averageIntensity * occurrences + event.intensity) / (occurrences + 1);
            
            // Calculate trend based on time and intensity
            if (lastOccurrence > 0) {
                long timeDiff = event.timestamp - lastOccurrence;
                float intensityDiff = event.intensity - averageIntensity;
                trend = trend * 0.8f + (intensityDiff / timeDiff) * 0.2f;
            }
            
            occurrences++;
            lastOccurrence = event.timestamp;
        }
        
        public boolean isRecurring() { return occurrences >= 3; }
        public boolean isIntensifying() { return trend > 0.1f && occurrences >= 3; }
        public boolean isDiminishing() { return trend < -0.1f && occurrences >= 3; }
        public int getOccurrences() { return occurrences; }
        public float getAverageIntensity() { return averageIntensity; }
        public float getTrend() { return trend; }
    }
    
    // =========================== 🔥 ENHANCED EMOTIONAL CONTAGION SYSTEM ===========================
    
    /**
     * 🔥 ADVANCED: Calculate emotional contagion effect between masters with relationship awareness
     */
    public float calculateContagionEffect(String sourceEmotion, float sourceIntensity, 
                                         String targetPersonality, String sourceMaster, String targetMaster) {
        // High-intensity emotions are more contagious
        if (sourceIntensity < 0.5f) return 0.0f;
        
        float baseContagion = sourceIntensity * 0.3f; // Base contagion rate
        
        // Get relationship context for enhanced contagion
        float relationshipMultiplier = 1.0f;
        if (persistenceManager != null) {
            RelationshipPersistenceManager.MasterRelationship relationship = 
                persistenceManager.getRelationship(sourceMaster, targetMaster);
            
            // Friendship increases emotional contagion
            relationshipMultiplier += relationship.friendshipBond * 0.4f;
            
            // High rivalry can either amplify or dampen emotions
            if (relationship.rivalryIntensity > 0.7f) {
                if (isNegativeEmotion(sourceEmotion)) {
                    relationshipMultiplier += 0.3f; // Rivals amplify each other's frustration
                } else {
                    relationshipMultiplier *= 0.8f; // Rivals resist each other's joy
                }
            }
        }
        
        // Master-specific contagion responses
        float personalityMultiplier = getMasterContagionMultiplier(sourceEmotion, targetPersonality);
        
        float finalContagion = baseContagion * relationshipMultiplier * personalityMultiplier;
        
        Log.d(TAG, String.format("🔥 Emotional contagion: %s (%.2f) → %s = %.2f effect (relationship: %.2f, personality: %.2f)", 
               sourceEmotion, sourceIntensity, targetPersonality, finalContagion, relationshipMultiplier, personalityMultiplier));
        
        return Math.max(0.0f, Math.min(1.0f, finalContagion));
    }
    
    /**
     * 🎭 Get master-specific contagion multipliers
     */
    private float getMasterContagionMultiplier(String sourceEmotion, String targetPersonality) {
        switch (targetPersonality.toLowerCase()) {
            case "tal":
                // Tal is highly susceptible to excitement, resistant to negativity
                if (sourceEmotion.contains("excited") || sourceEmotion.contains("thrilled") || sourceEmotion.contains("ecstatic")) {
                    return 1.8f; // Tal catches excitement like wildfire
                } else if (sourceEmotion.contains("frustrated") || sourceEmotion.contains("devastated")) {
                    return 0.4f; // Tal maintains optimism even when others are down
                } else if (sourceEmotion.contains("analytical")) {
                    return 0.6f; // Tal is less influenced by cold analysis
                }
                return 1.0f;
                
            case "fischer":
                // Fischer is generally resistant to emotional influence but can be triggered by incompetence
                if (sourceEmotion.contains("frustrated") && sourceEmotion.contains("blunder")) {
                    return 1.4f; // Fischer gets more frustrated by others' mistakes
                } else if (sourceEmotion.contains("excited") || sourceEmotion.contains("pleased")) {
                    return 0.6f; // Fischer is skeptical of others' optimism
                } else if (sourceEmotion.contains("analytical") || sourceEmotion.contains("focused")) {
                    return 1.1f; // Fischer respects analytical thinking
                }
                return 0.7f; // Generally resistant
                
            case "carlsen":
                // Carlsen has balanced emotional responses but can be influenced by respect
                if (sourceEmotion.contains("analytical") || sourceEmotion.contains("focused")) {
                    return 1.2f; // Carlsen appreciates analytical minds
                } else if (sourceEmotion.contains("frustrated")) {
                    return 0.8f; // Carlsen stays relatively calm under pressure
                } else if (sourceEmotion.contains("excited") && sourceEmotion.contains("tactical")) {
                    return 1.1f; // Carlsen can get excited about good tactics
                }
                return 1.0f; // Balanced responses
                
            case "kasparov":
                // Kasparov amplifies all emotions - he's passionate
                if (sourceEmotion.contains("competitive") || sourceEmotion.contains("analytical")) {
                    return 1.5f; // Kasparov loves intellectual battles
                } else if (sourceEmotion.contains("frustrated")) {
                    return 1.3f; // Kasparov can get heated quickly
                } else if (sourceEmotion.contains("excited")) {
                    return 1.4f; // Kasparov loves dynamic chess
                }
                return 1.2f; // Generally amplifies emotions
                
            case "anand":
                // Anand is encouraging and balances emotions
                if (sourceEmotion.contains("frustrated") || sourceEmotion.contains("devastated")) {
                    return 0.7f; // Anand has a calming influence on negative emotions
                } else if (sourceEmotion.contains("pleased") || sourceEmotion.contains("impressed")) {
                    return 1.3f; // Anand appreciates good play and spreads positivity
                } else if (sourceEmotion.contains("analytical")) {
                    return 1.1f; // Anand enjoys thoughtful analysis
                }
                return 1.0f;
                
            case "alekhine":
                // Alekhine is intense and artistic - amplifies passionate emotions
                if (sourceEmotion.contains("excited") && (sourceEmotion.contains("artistic") || sourceEmotion.contains("creative"))) {
                    return 1.7f; // Alekhine loves artistic chess passion
                } else if (sourceEmotion.contains("analytical") && sourceEmotion.contains("deep")) {
                    return 1.4f; // Alekhine appreciates deep analysis
                } else if (sourceEmotion.contains("frustrated") && sourceEmotion.contains("imperfection")) {
                    return 1.5f; // Alekhine gets frustrated by imperfect play
                }
                return 1.1f; // Generally passionate responses
                
            default:
                return 1.0f;
        }
    }
    
    /**
     * 🌊 Apply emotional contagion to a master's current emotional state
     */
    public EmotionalState applyEmotionalContagion(String targetMaster, EmotionalState currentEmotion, 
                                                 String sourceMaster, String sourceEmotion, float sourceIntensity) {
        float contagionEffect = calculateContagionEffect(sourceEmotion, sourceIntensity, targetMaster, sourceMaster, targetMaster);
        
        // Only apply contagion if effect is significant
        if (contagionEffect < 0.3f) {
            return currentEmotion; // No change
        }
        
        // Determine contagion direction based on source emotion
        EmotionalState newEmotion = currentEmotion;
        
        if (isPositiveEmotion(sourceEmotion)) {
            // Positive emotions can lift others up
            switch (currentEmotion) {
                case FOCUSED:
                    if (contagionEffect > 0.5f) newEmotion = EmotionalState.PLEASED;
                    break;
                case UNEASY:
                    if (contagionEffect > 0.6f) newEmotion = EmotionalState.FOCUSED;
                    break;
                case CONCERNED:
                    if (contagionEffect > 0.7f) newEmotion = EmotionalState.UNEASY;
                    break;
                case PLEASED:
                    if (contagionEffect > 0.6f) newEmotion = EmotionalState.EXCITED;
                    break;
                case EXCITED:
                    if (contagionEffect > 0.7f) newEmotion = EmotionalState.THRILLED;
                    break;
            }
        } else if (isNegativeEmotion(sourceEmotion)) {
            // Negative emotions can bring others down or make them defensive
            switch (currentEmotion) {
                case CONTENT:
                    if (contagionEffect > 0.5f) newEmotion = EmotionalState.FOCUSED;
                    break;
                case PLEASED:
                    if (contagionEffect > 0.6f) newEmotion = EmotionalState.UNEASY;
                    break;
                case EXCITED:
                    if (contagionEffect > 0.7f) newEmotion = EmotionalState.CONCERNED;
                    break;
                case FOCUSED:
                    if (contagionEffect > 0.8f) newEmotion = EmotionalState.UNEASY;
                    break;
            }
        }
        
        // Log significant emotional contagion events
        if (newEmotion != currentEmotion) {
            Log.d(TAG, String.format("🌊 EMOTIONAL CONTAGION: %s's %s (%.2f) changed %s from %s → %s", 
                   sourceMaster, sourceEmotion, sourceIntensity, targetMaster, 
                   currentEmotion.name, newEmotion.name));
            
            // Record this as an emergent event if it's significant
            if (contagionEffect > 0.7f && persistenceManager != null) {
                persistenceManager.recordEmergentEvent(
                    "emotional_contagion",
                    sourceMaster,
                    targetMaster,
                    String.format("%s's %s emotion infected %s, changing from %s to %s", 
                                sourceMaster, sourceEmotion, targetMaster, currentEmotion.name, newEmotion.name),
                    String.format("{\"source_emotion\": \"%s\", \"source_intensity\": %.2f, \"contagion_effect\": %.2f}", 
                                sourceEmotion, sourceIntensity, contagionEffect),
                    String.format("Emotional contagion caused %s to shift emotional state", targetMaster),
                    contagionEffect
                );
            }
        }
        
        return newEmotion;
    }
    
    /**
     * 🔍 Helper method to determine if emotion is positive
     */
    private boolean isPositiveEmotion(String emotion) {
        return emotion.contains("excited") || emotion.contains("thrilled") || emotion.contains("pleased") || 
               emotion.contains("ecstatic") || emotion.contains("content") || emotion.contains("impressed") ||
               emotion.contains("confident") || emotion.contains("analytical");
    }
    
    /**
     * 🔍 Helper method to determine if emotion is negative
     */
    private boolean isNegativeEmotion(String emotion) {
        return emotion.contains("frustrated") || emotion.contains("devastated") || emotion.contains("concerned") || 
               emotion.contains("uneasy") || emotion.contains("angry") || emotion.contains("disappointed");
    }
    
    /**
     * 🎭 BREAKTHROUGH: Cascade emotional contagion through multiple masters in conversations
     */
    public void cascadeEmotionalContagion(String originMaster, String originEmotion, float originIntensity, 
                                        List<String> otherMasters, EmotionalContext emotionalContext) {
        if (otherMasters == null || otherMasters.isEmpty() || originIntensity < 0.6f) {
            return; // Not significant enough for cascading
        }
        
        Log.d(TAG, String.format("🌊🌊 EMOTIONAL CASCADE: %s's %s (%.2f) affecting %d masters", 
               originMaster, originEmotion, originIntensity, otherMasters.size()));
        
        for (String targetMaster : otherMasters) {
            if (targetMaster.equals(originMaster)) continue;
            
            // Get current emotional state of target
            EmotionalState currentState = getCurrentEmotionalState(targetMaster);
            
            // Apply contagion
            EmotionalState newState = applyEmotionalContagion(targetMaster, currentState, 
                                                            originMaster, originEmotion, originIntensity);
            
            // Update emotional context if state changed
            if (newState != currentState && emotionalContext != null) {
                float newIntensity = Math.min(1.0f, originIntensity * 0.7f); // Cascaded emotions are slightly weaker
                emotionalContext.updateEmotionalState(targetMaster, newState.name, newIntensity, 
                                                    getEmotionalMomentum(targetMaster));
                
                // Update session history
                EmotionalHistory history = sessionHistory.computeIfAbsent(targetMaster.toLowerCase(), 
                                            k -> new EmotionalHistory());
                history.addEmotionalEvent(newState, newIntensity, "emotional_contagion_from_" + originMaster);
            }
        }
    }
    
    // =========================== ⚡ SITUATIONAL EMOTIONAL AMPLIFIERS ===========================
    
    /**
     * ⚡ Apply situational amplifiers to emotional intensity based on game context
     */
    public float applySituationalAmplifiers(String masterName, EmotionalState emotion, float baseIntensity, 
                                          String gameContext, String positionThemes, int timeRemaining) {
        float amplifiedIntensity = baseIntensity;
        EmotionalProfile profile = masterProfiles.get(masterName.toLowerCase());
        
        if (profile == null) return baseIntensity;
        
        // Time pressure amplification
        amplifiedIntensity *= getTimePressureMultiplier(masterName, timeRemaining, emotion);
        
        // Position-specific amplification
        amplifiedIntensity *= getPositionalAmplifier(masterName, positionThemes, emotion);
        
        // Game phase amplification
        amplifiedIntensity *= getGamePhaseAmplifier(masterName, gameContext, emotion);
        
        // Critical moment amplification
        amplifiedIntensity *= getCriticalMomentAmplifier(gameContext, emotion);
        
        // Ensure we don't exceed maximum intensity
        amplifiedIntensity = Math.max(0.1f, Math.min(1.0f, amplifiedIntensity));
        
        if (Math.abs(amplifiedIntensity - baseIntensity) > 0.15f) {
            Log.d(TAG, String.format("⚡ SITUATIONAL AMPLIFIER: %s's %s emotion %.2f → %.2f in %s context", 
                   masterName, emotion.name, baseIntensity, amplifiedIntensity, gameContext));
        }
        
        return amplifiedIntensity;
    }
    
    /**
     * ⏰ Time pressure emotional amplification
     */
    private float getTimePressureMultiplier(String masterName, int timeRemaining, EmotionalState emotion) {
        if (timeRemaining > 300) return 1.0f; // No pressure above 5 minutes
        
        float pressureLevel = 1.0f - (timeRemaining / 300.0f); // 0.0 to 1.0
        
        switch (masterName.toLowerCase()) {
            case "tal":
                // Tal thrives under pressure for tactical emotions
                if (emotion == EmotionalState.EXCITED || emotion == EmotionalState.THRILLED) {
                    return 1.0f + (pressureLevel * 0.3f); // Up to 30% boost
                } else if (emotion == EmotionalState.FRUSTRATED) {
                    return 1.0f + (pressureLevel * 0.2f); // Tal gets more emotional under pressure
                }
                break;
                
            case "fischer":
                // Fischer gets more intense under pressure
                if (emotion == EmotionalState.FRUSTRATED || emotion == EmotionalState.FOCUSED) {
                    return 1.0f + (pressureLevel * 0.4f); // Up to 40% boost
                } else if (emotion == EmotionalState.CONFIDENT) {
                    return 1.0f + (pressureLevel * 0.2f); // Controlled confidence under pressure
                }
                break;
                
            case "carlsen":
                // Carlsen stays relatively calm but becomes more focused
                if (emotion == EmotionalState.FOCUSED || emotion == EmotionalState.ANALYTICAL) {
                    return 1.0f + (pressureLevel * 0.15f); // Slight focus boost
                } else {
                    return 1.0f - (pressureLevel * 0.1f); // Slight dampening of other emotions
                }
                
            case "kasparov":
                // Kasparov becomes more intense across all emotions
                return 1.0f + (pressureLevel * 0.35f); // General intensity boost
                
            case "alekhine":
                // Alekhine becomes more passionate and volatile under pressure
                if (emotion == EmotionalState.EXCITED || emotion == EmotionalState.FRUSTRATED) {
                    return 1.0f + (pressureLevel * 0.5f); // Up to 50% boost for passionate emotions
                }
                return 1.0f + (pressureLevel * 0.2f); // General intensity increase
        }
        
        return 1.0f + (pressureLevel * 0.1f); // Default minor amplification
    }
    
    /**
     * 🎯 Position-specific emotional amplification
     */
    private float getPositionalAmplifier(String masterName, String positionThemes, EmotionalState emotion) {
        if (positionThemes == null || positionThemes.isEmpty()) return 1.0f;
        
        String themes = positionThemes.toLowerCase();
        
        switch (masterName.toLowerCase()) {
            case "tal":
                // Tal gets excited by tactical complications
                if (themes.contains("sacrifice") || themes.contains("attack") || themes.contains("tactical")) {
                    if (emotion == EmotionalState.EXCITED || emotion == EmotionalState.THRILLED) {
                        return 1.6f; // Major boost for tactical excitement
                    }
                } else if (themes.contains("endgame") || themes.contains("positional")) {
                    if (emotion == EmotionalState.EXCITED) {
                        return 0.8f; // Tal less excited by pure positional play
                    }
                }
                break;
                
            case "fischer":
                // Fischer appreciates precision and strong positions
                if (themes.contains("precise") || themes.contains("technique") || themes.contains("winning")) {
                    if (emotion == EmotionalState.CONFIDENT || emotion == EmotionalState.PLEASED) {
                        return 1.4f; // Fischer loves being in control
                    }
                } else if (themes.contains("unclear") || themes.contains("chaotic")) {
                    if (emotion == EmotionalState.FRUSTRATED || emotion == EmotionalState.UNEASY) {
                        return 1.3f; // Fischer dislikes unclear positions
                    }
                }
                break;
                
            case "carlsen":
                // Carlsen excels in endgames and complex positions
                if (themes.contains("endgame") || themes.contains("complex") || themes.contains("maneuvering")) {
                    if (emotion == EmotionalState.CONFIDENT || emotion == EmotionalState.ANALYTICAL) {
                        return 1.3f; // Carlsen's specialty
                    }
                }
                break;
                
            case "kasparov":
                // Kasparov loves dynamic, attacking positions
                if (themes.contains("dynamic") || themes.contains("initiative") || themes.contains("attack")) {
                    if (emotion == EmotionalState.EXCITED || emotion == EmotionalState.COMPETITIVE) {
                        return 1.5f; // Kasparov thrives on dynamics
                    }
                }
                break;
                
            case "alekhine":
                // Alekhine loves artistic, creative positions
                if (themes.contains("creative") || themes.contains("artistic") || themes.contains("deep")) {
                    if (emotion == EmotionalState.EXCITED || emotion == EmotionalState.IMPRESSED) {
                        return 1.7f; // Alekhine's artistic passion
                    }
                } else if (themes.contains("routine") || themes.contains("simple")) {
                    if (emotion == EmotionalState.FRUSTRATED || emotion == EmotionalState.UNEASY) {
                        return 1.3f; // Alekhine dislikes simple positions
                    }
                }
                break;
        }
        
        return 1.0f;
    }
    
    /**
     * 🏁 Game phase emotional amplification
     */
    private float getGamePhaseAmplifier(String masterName, String gameContext, EmotionalState emotion) {
        if (gameContext == null) return 1.0f;
        
        String context = gameContext.toLowerCase();
        
        switch (masterName.toLowerCase()) {
            case "tal":
                // Tal loves opening innovations and middlegame tactics
                if (context.contains("opening") && emotion == EmotionalState.INTRIGUED) {
                    return 1.3f;
                } else if (context.contains("middlegame") && emotion == EmotionalState.EXCITED) {
                    return 1.4f;
                }
                break;
                
            case "carlsen":
                // Carlsen dominates endgames
                if (context.contains("endgame")) {
                    if (emotion == EmotionalState.CONFIDENT || emotion == EmotionalState.FOCUSED) {
                        return 1.4f; // Carlsen's endgame confidence
                    }
                }
                break;
                
            case "kasparov":
                // Kasparov excels in middlegame complications
                if (context.contains("middlegame") && emotion == EmotionalState.ANALYTICAL) {
                    return 1.3f;
                }
                break;
        }
        
        return 1.0f;
    }
    
    /**
     * 🚨 Critical moment amplification
     */
    private float getCriticalMomentAmplifier(String gameContext, EmotionalState emotion) {
        if (gameContext == null) return 1.0f;
        
        String context = gameContext.toLowerCase();
        
        // Critical moments amplify all emotions
        if (context.contains("critical") || context.contains("decisive") || context.contains("turning_point")) {
            switch (emotion) {
                case ECSTATIC:
                case DEVASTATED:
                    return 1.5f; // Extreme emotions become more extreme
                case THRILLED:
                case FRUSTRATED:
                    return 1.4f; // High emotions get amplified
                case EXCITED:
                case CONCERNED:
                    return 1.3f; // Medium emotions get moderate boost
                default:
                    return 1.2f; // All emotions get some amplification
            }
        } else if (context.contains("blunder") || context.contains("brilliant")) {
            // Exceptional moves create strong emotional reactions
            return 1.6f;
        }
        
        return 1.0f;
    }
    
    /**
     * 🎭 INTEGRATED: Enhanced emotional analysis with situational awareness
     */
    public EmotionalAnalysisResult analyzeEmotionalStateWithSituation(String masterName, String gameContext, 
                                                                     String conversationContext, Float currentEval, 
                                                                     Float evalChange, EmotionalContext emotionalContext,
                                                                     String positionThemes, int timeRemaining) {
        // Get base emotional analysis
        EmotionalAnalysisResult baseResult = analyzeEmotionalState(masterName, gameContext, conversationContext, 
                                                                   currentEval, evalChange, emotionalContext);
        
        // Apply situational amplifiers
        float amplifiedIntensity = applySituationalAmplifiers(masterName, baseResult.emotion, baseResult.intensity, 
                                                             gameContext, positionThemes, timeRemaining);
        
        // Apply emotional contagion if other masters are present
        EmotionalState finalEmotion = baseResult.emotion;
        if (emotionalContext != null && !emotionalContext.getOtherMaster().isEmpty()) {
            String otherMaster = emotionalContext.getOtherMaster();
            String otherEmotion = emotionalContext.getOtherMasterEmotion();
            float otherIntensity = emotionalContext.getOtherMasterIntensity();
            
            if (otherIntensity > 0.5f) {
                finalEmotion = applyEmotionalContagion(masterName, baseResult.emotion, otherMaster, otherEmotion, otherIntensity);
            }
        }
        
        // Create enhanced result
        EmotionalAnalysisResult enhancedResult = new EmotionalAnalysisResult(
            finalEmotion, 
            baseResult.expression, 
            amplifiedIntensity, 
            baseResult.momentum
        );
        
        return enhancedResult;
    }
}