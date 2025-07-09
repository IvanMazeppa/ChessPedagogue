package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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
        PLAYFUL(0, "playful", "Let's have some fun!"),
        CONTEMPLATIVE(0, "contemplative", "Let me ponder this deeply..."),
        CALM(0, "calm", "Everything is under control."),
        RESPONSIVE(0, "responsive", "I'm listening and ready to respond."),
        ENGAGED(0, "engaged", "I'm fully engaged with this position."),
        THOUGHTFUL(0, "thoughtful", "This requires careful thought."),
        LISTENING(0, "listening", "I'm listening to what you're saying."),
        ANALYZING(0, "analyzing", "Let me analyze this situation."),
        CONSIDERING(0, "considering", "I'm considering the options."),
        PASSIONATE(0, "passionate", "This is what I live for!"),
        TRIUMPHANT(0, "triumphant", "Victory is within reach!");
        
        public final int intensity;
        public final String name;
        public final String defaultExpression;
        
        EmotionalState(int intensity, String name, String defaultExpression) {
            this.intensity = intensity;
            this.name = name;
            this.defaultExpression = defaultExpression;
        }
        
        // Safe valueOf method that returns ANALYTICAL for unknown states
        public static EmotionalState safeValueOf(String name) {
            try {
                return EmotionalState.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                Log.w("EmotionalIntelligence", "Unknown emotional state: " + name + ", using ANALYTICAL");
                return ANALYTICAL;
            }
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
     * 🧠 ENHANCED: Track emotional patterns over time with persistent memory
     */
    public static class EmotionalHistory {
        private final List<EmotionalEvent> sessionEvents;          // Current session only
        private final List<EmotionalIntelligenceManager.EmotionalEvent> historicalEvents; // From database
        private final Map<String, EmotionalPattern> patterns;      // Topic + emotion patterns
        private EmotionalState currentDominantEmotion;
        private float emotionalMomentum; // -1.0 to 1.0
        private float historicalMomentum; // From previous sessions
        private int consecutiveSimilarEmotions;
        
        public EmotionalHistory() {
            this.sessionEvents = new ArrayList<>();
            this.historicalEvents = new ArrayList<>();
            this.patterns = new HashMap<>();
            this.currentDominantEmotion = EmotionalState.ANALYTICAL;
            this.emotionalMomentum = 0.0f;
            this.historicalMomentum = 0.0f;
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
        
        /**
         * 🧠 ENHANCED: Add session event and update patterns
         */
        public void addSessionEvent(EmotionalState emotion, float intensity, String trigger) {
            EmotionalEvent event = new EmotionalEvent(emotion, intensity, trigger);
            sessionEvents.add(event);
            updateEmotionalMomentum(emotion, intensity);
            updateDominantEmotion(emotion);
            
            // Update patterns for this topic + emotion combination
            updatePatterns(emotion.name, intensity, trigger);
            
            // Keep session history manageable
            if (sessionEvents.size() > 20) {
                sessionEvents.remove(0);
            }
        }
        
        /**
         * 🧠 NEW: Add historical event from database (don't affect current session state)
         */
        public void addHistoricalEvent(EmotionalIntelligenceManager.EmotionalEvent event) {
            historicalEvents.add(event);
            // Update patterns but don't affect current session momentum
            updatePatterns(event.emotion.name, event.intensity, event.topic);
        }
        
        /**
         * 🧠 NEW: Update emotional patterns for topic + emotion combinations
         */
        private void updatePatterns(String emotion, float intensity, String topic) {
            String patternKey = topic + "_" + emotion;
            EmotionalPattern pattern = patterns.computeIfAbsent(patternKey, k -> new EmotionalPattern());
            
            // Create a temporary event for pattern updating
            EmotionalIntelligenceManager.EmotionalEvent tempEvent = 
                new EmotionalIntelligenceManager.EmotionalEvent(topic, 
                    EmotionalState.safeValueOf(emotion.toUpperCase()), intensity);
            pattern.addOccurrence(tempEvent);
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
        
        // 🧠 ENHANCED: Methods for enhanced memory integration
        public void setHistoricalMomentum(float momentum) {
            this.historicalMomentum = Math.max(-1.0f, Math.min(1.0f, momentum));
        }
        
        /**
         * 🧠 NEW: Calculate combined momentum from session + historical data
         */
        public float calculateCombinedMomentum() {
            // Weight recent session events more heavily than historical
            return (emotionalMomentum * 0.7f) + (historicalMomentum * 0.3f);
        }
        
        /**
         * 🧠 NEW: Get emotional pattern for topic + emotion combination
         */
        public EmotionalPattern getPattern(String topic, String emotion) {
            return patterns.get(topic + "_" + emotion);
        }
        
        /**
         * 🧠 NEW: Get all events (historical + session)
         */
        public List<EmotionalIntelligenceManager.EmotionalEvent> getAllEvents() {
            List<EmotionalIntelligenceManager.EmotionalEvent> allEvents = new ArrayList<>();
            allEvents.addAll(historicalEvents);
            
            // Convert session events to the main EmotionalEvent format
            for (EmotionalEvent sessionEvent : sessionEvents) {
                allEvents.add(new EmotionalIntelligenceManager.EmotionalEvent(
                    sessionEvent.trigger, sessionEvent.emotion, sessionEvent.intensity, sessionEvent.timestamp));
            }
            
            return allEvents;
        }
        
        /**
         * 🧠 NEW: Get session events only
         */
        public List<EmotionalEvent> getSessionEvents() {
            return new ArrayList<>(sessionEvents);
        }
        
        /**
         * 🧠 NEW: Get historical events only
         */
        public List<EmotionalIntelligenceManager.EmotionalEvent> getHistoricalEvents() {
            return new ArrayList<>(historicalEvents);
        }
        
        /**
         * 🧠 NEW: Check if should persist this emotional event
         */
        public boolean shouldPersist(EmotionalEvent event) {
            return event.intensity > 0.7f || consecutiveSimilarEmotions >= 3;
        }
        
        /**
         * 🧠 NEW: Trim historical events to prevent memory bloat
         */
        public void trimHistoricalEvents() {
            if (historicalEvents.size() > 50) {
                // Keep only recent and significant events
                historicalEvents.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
                while (historicalEvents.size() > 50) {
                    historicalEvents.remove(historicalEvents.size() - 1);
                }
            }
        }
        
        // 🧠 LEGACY: Keep backward compatibility
        public void addEmotionalEvent(EmotionalState emotion, float intensity, String trigger) {
            addSessionEvent(emotion, intensity, trigger);
        }
    }
    
    private EmotionalIntelligenceManager(Context context) {
        Log.d(TAG, "🚀 CONSTRUCTOR CALLED: EmotionalIntelligenceManager initialization starting...");
        
        this.context = context.getApplicationContext();
        this.evaluationTracker = EvaluationTracker.getInstance(context);
        this.masterProfiles = new HashMap<>();
        this.sessionHistory = new HashMap<>();
        
        // 🔧 CRITICAL FIX: Initialize the persistence manager for EQ database operations
        this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        Log.d(TAG, "✅ Persistence manager initialized for EQ database operations");
        
        // 🔧 Verify database integrity on initialization
        Log.d(TAG, "🔧 About to call verifyDatabaseIntegrity()...");
        persistenceManager.verifyDatabaseIntegrity();
        Log.d(TAG, "🔧 verifyDatabaseIntegrity() completed");
        
        // 🧪 Test database writes to identify issues
        Log.d(TAG, "🧪 About to call testDatabaseWrites()...");
        persistenceManager.testDatabaseWrites();
        Log.d(TAG, "🧪 testDatabaseWrites() completed");
        
        initializeMasterProfiles();
        Log.d(TAG, "🎭 Enhanced Emotional Intelligence Manager initialized with database persistence");
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
        EmotionalState baseEmotion = analyzeEvaluationBasedEmotion(masterName, currentEval, evalChange);
        
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
    private EmotionalState analyzeEvaluationBasedEmotion(String masterName, Float currentEval, Float evalChange) {
        // Use passed parameters first, then fallback to tracker
        if (currentEval == null || evalChange == null) {
            evalChange = evaluationTracker.getRecentEvaluationChange();
            currentEval = evaluationTracker.getCurrentEvaluation();
        }
        
        if (evalChange == null || currentEval == null) {
            Log.d(TAG, "🎭 No evaluation data available - using personality-driven emotion for " + masterName);
            // FIXED: Use personality-driven emotions instead of always ANALYTICAL
            return getCurrentEmotionalState(masterName);
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
        public ExpressionGuidance expressionGuidance; // 🎭 NEW: Expression diversity guidance
        
        public EmotionalAnalysisResult(EmotionalState emotion, String expression, 
                                     float intensity, float momentum) {
            this.emotion = emotion;
            this.expression = expression;
            this.intensity = intensity;
            this.momentum = momentum;
            this.expressionGuidance = null; // Will be set by analyzeWithExpressionGuidance
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
        if (history != null) {
            return history.getCurrentDominantEmotion();
        }
        
        // FIXED: Use personality-driven default emotions instead of always ANALYTICAL
        switch (masterName.toLowerCase()) {
            case "fischer":
                return EmotionalState.CONFIDENT; // Fischer's natural intensity
            case "tal":
                return EmotionalState.EXCITED; // Tal's natural enthusiasm  
            case "alekhine":
                return EmotionalState.PLEASED; // Alekhine's artistic satisfaction
            case "kasparov":
                return EmotionalState.CONFIDENT; // Kasparov's natural confidence
            case "kramnik":
                return EmotionalState.CONTEMPLATIVE; // Kramnik's deep thinking
            case "capablanca":
                return EmotionalState.CALM; // Capablanca's natural ease
            default:
                return EmotionalState.ANALYTICAL; // Safe fallback for others
        }
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
    /**
     * 🧠 ENHANCED: Initialize with historical emotional data from database
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
            
            // Get or create emotional history for this master
            EmotionalHistory history = sessionHistory.computeIfAbsent(masterName.toLowerCase(), 
                                        k -> new EmotionalHistory());
            
            // Convert database reactions to EmotionalEvent objects and load as historical events
            for (RelationshipPersistenceManager.EmotionalReaction reaction : recentReactions) {
                EmotionalEvent event = new EmotionalEvent(
                    reaction.getTopic(),
                    EmotionalState.safeValueOf(reaction.getEmotion().toUpperCase()),
                    reaction.getIntensity(),
                    reaction.getTimestamp()
                );
                history.addHistoricalEvent(event);
            }
            
            // Calculate initial emotional momentum from history
            float historicalMomentum = calculateHistoricalMomentum(masterName, opponentName);
            history.setHistoricalMomentum(historicalMomentum);
            
            // Trim historical events to prevent memory bloat
            history.trimHistoricalEvents();
            
            Log.d(TAG, String.format("🧠 Loaded emotional history for %s vs %s: %d events, momentum: %.2f", 
                   masterName, opponentName, recentReactions.size(), historicalMomentum));
                   
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to load emotional history, using session-only mode", e);
            // Continue without historical data - graceful degradation
        }
    }
    
    /**
     * 🧠 NEW: Get emotional pattern for a master's topic + emotion combination
     */
    public EmotionalPattern getEmotionalPattern(String topic, String emotion) {
        if (currentMaster == null) return null;
        
        EmotionalHistory history = sessionHistory.get(currentMaster.toLowerCase());
        if (history != null) {
            return history.getPattern(topic, emotion);
        }
        return null;
    }
    
    /**
     * 🧠 NEW: Get combined emotional momentum (session + historical)
     */
    public float getCombinedEmotionalMomentum(String masterName) {
        EmotionalHistory history = sessionHistory.get(masterName.toLowerCase());
        if (history != null) {
            return history.calculateCombinedMomentum();
        }
        return 0.0f;
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
     * 📊 Calculate historical emotional momentum from database
     */
    private float calculateHistoricalMomentum(String masterName, String opponentName) {
        if (persistenceManager == null) return 0.0f;
        
        try {
            // Get recent emotional reactions and calculate trend
            List<RelationshipPersistenceManager.EmotionalReaction> recentReactions = persistenceManager.getEmotionalHistoryBetweenMasters(
                masterName, opponentName, 10
            );
            
            // FIXED: Allow personality-based momentum even without database history
            if (recentReactions.size() < 3) {
                // Use master personality traits to generate initial momentum
                switch (masterName.toLowerCase()) {
                    case "fischer":
                        return 0.3f; // Fischer's natural intensity creates positive momentum
                    case "tal":
                        return 0.4f; // Tal's excitement generates high momentum
                    case "alekhine":
                        return 0.25f; // Alekhine's artistic passion builds momentum
                    case "kasparov":
                        return 0.35f; // Kasparov's confidence builds momentum
                    default:
                        return 0.1f; // Small positive momentum for personality expression
                }
            }
            
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
            EmotionalState state = EmotionalState.safeValueOf(emotion.toUpperCase());
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
        void onExpressionRepetitionDetected(String master, String concept, float diversityScore);
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
    
    // ==========================================
    // 🎭 PERSONALITY EXPRESSION MANAGER
    // ==========================================
    
    /**
     * 🎭 PersonalityExpressionManager: Tracks HOW masters express concepts to prevent repetitive phrasing
     * 
     * Solves the repetition problem where Fischer always says "truth on the board" or 
     * "analysis cuts through" when discussing similar concepts. This system tracks expression
     * patterns and provides guidance to use fresh approaches and varied personality facets.
     */
    public static class PersonalityExpressionManager {
        private static final String TAG = "PersonalityExpression";
        private static final long PHRASE_COOLDOWN = 15 * 60 * 1000; // 15 minutes
        private static final float DIVERSITY_THRESHOLD = 0.3f; // Below this = too repetitive
        
        private static PersonalityExpressionManager instance;
        private final Map<String, Map<String, ExpressionPattern>> masterExpressions = new HashMap<>();
        private final RelationshipPersistenceManager persistenceManager;
        
        private PersonalityExpressionManager(Context context) {
            this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        }
        
        public static synchronized PersonalityExpressionManager getInstance(Context context) {
            if (instance == null) {
                instance = new PersonalityExpressionManager(context);
            }
            return instance;
        }
        
        /**
         * 📝 Record how a master expressed a specific concept
         */
        public void recordExpression(String master, String concept, String actualPhrase, 
                                   String emotionalTone, String argumentativeAngle) {
            Map<String, ExpressionPattern> expressions = masterExpressions.computeIfAbsent(
                master.toLowerCase(), k -> new HashMap<>()
            );
            
            ExpressionPattern pattern = expressions.computeIfAbsent(concept, k -> new ExpressionPattern(concept));
            pattern.addExpression(actualPhrase, emotionalTone, argumentativeAngle);
            
            // Persist significant expression patterns
            if (pattern.timesUsed >= 3) {
                persistenceManager.recordExpressionPattern(master, concept, pattern);
            }
            
            Log.d(TAG, String.format("🎭 Recorded expression: %s expressed '%s' via '%s' (diversity: %.2f)",
                   master, concept, argumentativeAngle, pattern.diversityScore));
        }
        
        /**
         * 🎯 Get expression guidance to avoid repetitive phrasing
         */
        public ExpressionGuidance getExpressionGuidance(String master, String concept, String emotion) {
            ExpressionGuidance guidance = new ExpressionGuidance();
            
            Map<String, ExpressionPattern> expressions = masterExpressions.get(master.toLowerCase());
            if (expressions != null) {
                ExpressionPattern pattern = expressions.get(concept);
                if (pattern != null) {
                    guidance.avoidPhrases = new ArrayList<>(pattern.usedPhrases);
                    guidance.usedAngles = new ArrayList<>(pattern.usedAngles);
                    guidance.suggestedAlternatives = generateAlternatives(master, concept, emotion, pattern);
                    guidance.diversityScore = pattern.diversityScore;
                    guidance.shouldUseFreshApproach = pattern.diversityScore < DIVERSITY_THRESHOLD;
                    
                    // Filter recent phrases (cooldown period)
                    long currentTime = System.currentTimeMillis();
                    guidance.recentPhrases = pattern.usedPhrases.stream()
                        .filter(phrase -> (currentTime - pattern.lastUsed) < PHRASE_COOLDOWN)
                        .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
                }
            }
            
            return guidance;
        }
        
        /**
         * 🤝 PHASE 3: Dynamic Relationship Evolution - Analyze expression similarity between masters
         */
        public RelationshipAdjustment analyzeExpressionSimilarity(String master1, String master2, String concept) {
            Map<String, ExpressionPattern> expressions1 = masterExpressions.get(master1.toLowerCase());
            Map<String, ExpressionPattern> expressions2 = masterExpressions.get(master2.toLowerCase());
            
            if (expressions1 == null || expressions2 == null) {
                return new RelationshipAdjustment(); // Default neutral adjustment
            }
            
            ExpressionPattern pattern1 = expressions1.get(concept);
            ExpressionPattern pattern2 = expressions2.get(concept);
            
            if (pattern1 == null || pattern2 == null) {
                return new RelationshipAdjustment(); // No patterns to compare
            }
            
            // Calculate similarity scores
            float phraseSimilarity = calculateListSimilarity(pattern1.usedPhrases, pattern2.usedPhrases);
            float angleSimilarity = calculateListSimilarity(pattern1.usedAngles, pattern2.usedAngles);
            float toneSimilarity = calculateListSimilarity(pattern1.usedEmotionalTones, pattern2.usedEmotionalTones);
            
            // Weight the similarities (angles are most important for personality)
            float overallSimilarity = (phraseSimilarity * 0.2f + angleSimilarity * 0.6f + toneSimilarity * 0.2f);
            
            RelationshipAdjustment adjustment = new RelationshipAdjustment();
            adjustment.similarityScore = overallSimilarity;
            adjustment.concept = concept;
            
            // Generate relationship guidance based on similarity
            if (overallSimilarity > 0.7f) {
                // High similarity - masters might bond or compete
                adjustment.addressingStyle = generateHighSimilarityAddressing(master1, master2, concept, overallSimilarity);
                adjustment.relationshipImpact = 0.1f; // Positive bond from shared approaches
                adjustment.personalityInfluence = "convergent"; // Masters are influencing each other
                
            } else if (overallSimilarity < 0.3f) {
                // Low similarity - masters have contrasting approaches
                adjustment.addressingStyle = generateLowSimilarityAddressing(master1, master2, concept, overallSimilarity);
                adjustment.relationshipImpact = 0.05f; // Slight respect for different approach
                adjustment.personalityInfluence = "divergent"; // Masters maintaining distinct styles
                
            } else {
                // Moderate similarity - balanced interaction
                adjustment.addressingStyle = generateModerateSimilarityAddressing(master1, master2, concept, overallSimilarity);
                adjustment.relationshipImpact = 0.02f; // Minimal impact
                adjustment.personalityInfluence = "neutral"; // No strong influence
            }
            
            Log.d(TAG, String.format("🤝 EXPRESSION SIMILARITY: %s ↔ %s on '%s' = %.2f (influence: %s)", 
                   master1, master2, concept, overallSimilarity, adjustment.personalityInfluence));
            
            return adjustment;
        }
        
        /**
         * 📊 Calculate similarity between two lists of expressions
         */
        private float calculateListSimilarity(List<String> list1, List<String> list2) {
            if (list1.isEmpty() || list2.isEmpty()) return 0.0f;
            
            Set<String> set1 = new HashSet<>(list1);
            Set<String> set2 = new HashSet<>(list2);
            
            // Calculate Jaccard similarity
            Set<String> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);
            
            Set<String> union = new HashSet<>(set1);
            union.addAll(set2);
            
            return union.isEmpty() ? 0.0f : (float) intersection.size() / union.size();
        }
        
        /**
         * 🔥 Generate addressing style for masters with high expression similarity
         */
        private String generateHighSimilarityAddressing(String master1, String master2, String concept, float similarity) {
            String addressingGuidance = "";
            
            // Masters recognize shared approaches
            if (master1.equalsIgnoreCase("fischer") && master2.equalsIgnoreCase("carlsen")) {
                addressingGuidance = "ADDRESSING STYLE: Fischer acknowledges Carlsen's analytical precision, calling him 'the Norwegian calculator' or 'surprisingly methodical for a modern player.' Shows grudging respect for shared perfectionism.";
            } else if (master1.equalsIgnoreCase("tal") && master2.equalsIgnoreCase("alekhine")) {
                addressingGuidance = "ADDRESSING STYLE: Tal recognizes Alekhine's artistic flair, addressing him as 'master of combinations' or 'fellow artist.' Bonds over shared love of creative chess.";
            } else if (master1.equalsIgnoreCase("kasparov") && master2.equalsIgnoreCase("fischer")) {
                addressingGuidance = "ADDRESSING STYLE: Kasparov acknowledges Fischer's intensity, calling him 'the American perfectionist' or 'Bobby.' Shows competitive respect for shared drive.";
            } else {
                // Generic high similarity addressing
                addressingGuidance = String.format("ADDRESSING STYLE: %s recognizes %s's similar approach to %s, using respectful but competitive terms. Shows appreciation for shared methodology.", master1, master2, concept);
            }
            
            return addressingGuidance + String.format(" (Similarity: %.2f - kindred spirits in %s)", similarity, concept);
        }
        
        /**
         * ⚔️ Generate addressing style for masters with low expression similarity
         */
        private String generateLowSimilarityAddressing(String master1, String master2, String concept, float similarity) {
            String addressingGuidance = "";
            
            // Masters clash over different approaches
            if (master1.equalsIgnoreCase("fischer") && master2.equalsIgnoreCase("tal")) {
                addressingGuidance = "ADDRESSING STYLE: Fischer dismisses Tal's intuitive approach as 'sloppy' or 'unscientific.' Addresses him as 'Mikhail' with condescending tone about his 'random sacrifices.'";
            } else if (master1.equalsIgnoreCase("carlsen") && master2.equalsIgnoreCase("alekhine")) {
                addressingGuidance = "ADDRESSING STYLE: Carlsen politely critiques Alekhine's romantic approach as 'impractical in modern chess.' Addresses him respectfully but highlights efficiency vs artistry.";
            } else if (master1.equalsIgnoreCase("tal") && master2.equalsIgnoreCase("fischer")) {
                addressingGuidance = "ADDRESSING STYLE: Tal teases Fischer's rigid approach, calling him 'Bobby the machine' or 'too serious.' Shows amusement at Fischer's obsession with perfection.";
            } else {
                // Generic low similarity addressing
                addressingGuidance = String.format("ADDRESSING STYLE: %s contrasts their approach to %s with %s's method, using terms that highlight the philosophical differences. Respectful but emphasizes distinct styles.", master1, concept, master2);
            }
            
            return addressingGuidance + String.format(" (Similarity: %.2f - contrasting philosophies on %s)", similarity, concept);
        }
        
        /**
         * ⚖️ Generate addressing style for masters with moderate expression similarity
         */
        private String generateModerateSimilarityAddressing(String master1, String master2, String concept, float similarity) {
            return String.format("ADDRESSING STYLE: %s maintains neutral professional respect toward %s regarding %s. Uses formal address with occasional acknowledgment of %s's competence. (Similarity: %.2f - balanced interaction)", 
                               master1, master2, concept, master2, similarity);
        }
        
        /**
         * 🎭 Apply relationship-based conversation modifiers
         */
        public String buildRelationshipGuidance(String speaker, String listener, String concept) {
            RelationshipAdjustment adjustment = analyzeExpressionSimilarity(speaker, listener, concept);
            
            StringBuilder guidance = new StringBuilder();
            guidance.append(adjustment.addressingStyle).append(" ");
            
            // Add personality influence guidance
            switch (adjustment.personalityInfluence) {
                case "convergent":
                    guidance.append("PERSONALITY INFLUENCE: Masters are unconsciously adopting similar expression patterns. ");
                    guidance.append(speaker).append(" might echo ").append(listener).append("'s terminology or approach slightly. ");
                    break;
                    
                case "divergent":
                    guidance.append("PERSONALITY INFLUENCE: Masters are maintaining distinct approaches. ");
                    guidance.append(speaker).append(" should emphasize their unique perspective in contrast to ").append(listener).append(". ");
                    break;
                    
                case "neutral":
                    guidance.append("PERSONALITY INFLUENCE: Standard professional interaction without strong mutual influence. ");
                    break;
            }
            
            return guidance.toString();
        }

        /**
         * 🎨 Generate fresh alternatives for expressing concepts
         */
        private List<String> generateAlternatives(String master, String concept, String emotion, 
                                                ExpressionPattern pattern) {
            List<String> alternatives = new ArrayList<>();
            
            // Fischer-specific alternatives for common repetitive concepts
            if (master.equalsIgnoreCase("fischer")) {
                switch (concept.toLowerCase()) {
                    case "perfectionism":
                    case "truth":
                    case "analysis":
                        if (!pattern.usedAngles.contains("standards_rant")) {
                            alternatives.add("ANGLE: standards_rant - Focus on impossibly high standards and technical precision");
                        }
                        if (!pattern.usedAngles.contains("dismissive_criticism")) {
                            alternatives.add("ANGLE: dismissive_criticism - Harshly criticize opponent's 'amateur' approach");
                        }
                        if (!pattern.usedAngles.contains("paranoid_conspiracy")) {
                            alternatives.add("ANGLE: paranoid_conspiracy - Suspicious of opponent's motives or methods");
                        }
                        if (!pattern.usedAngles.contains("brutal_honesty")) {
                            alternatives.add("ANGLE: brutal_honesty - Bluntly state uncomfortable truths about the position");
                        }
                        break;
                        
                    case "soviet_criticism":
                    case "politics":
                        if (!pattern.usedAngles.contains("conspiracy_theory")) {
                            alternatives.add("ANGLE: conspiracy_theory - Paranoid suspicions about collusion");
                        }
                        if (!pattern.usedAngles.contains("collusion_accusation")) {
                            alternatives.add("ANGLE: collusion_accusation - Direct accusations of cheating or manipulation");
                        }
                        if (!pattern.usedAngles.contains("system_corruption")) {
                            alternatives.add("ANGLE: system_corruption - Claims about corrupt chess establishment");
                        }
                        break;
                        
                    case "confidence":
                    case "superiority":
                        if (!pattern.usedAngles.contains("arrogant_dismissal")) {
                            alternatives.add("ANGLE: arrogant_dismissal - Condescendingly dismiss opponent's abilities");
                        }
                        if (!pattern.usedAngles.contains("intellectual_superiority")) {
                            alternatives.add("ANGLE: intellectual_superiority - Claim mental/analytical dominance");
                        }
                        break;
                }
            }
            
            // Carlsen-specific alternatives
            if (master.equalsIgnoreCase("carlsen")) {
                switch (concept.toLowerCase()) {
                    case "practical_play":
                    case "endgame":
                        if (!pattern.usedAngles.contains("step_by_step")) {
                            alternatives.add("ANGLE: step_by_step - Methodical, patient approach");
                        }
                        if (!pattern.usedAngles.contains("pressure_builder")) {
                            alternatives.add("ANGLE: pressure_builder - Gradually increasing positional pressure");
                        }
                        break;
                        
                    case "modern_chess":
                        if (!pattern.usedAngles.contains("computer_age")) {
                            alternatives.add("ANGLE: computer_age - Reference to modern preparation and analysis");
                        }
                        break;
                }
            }
            
            return alternatives;
        }
        
        /**
         * 📊 Calculate diversity score using Shannon entropy
         */
        private float calculateDiversityScore(List<String> expressions) {
            if (expressions.isEmpty()) return 1.0f;
            
            // Count frequency of each expression type
            Map<String, Integer> frequency = new HashMap<>();
            for (String expr : expressions) {
                frequency.put(expr, frequency.getOrDefault(expr, 0) + 1);
            }
            
            // Calculate Shannon entropy
            int total = expressions.size();
            float entropy = 0;
            
            for (int count : frequency.values()) {
                float probability = (float) count / total;
                if (probability > 0) {
                    entropy -= probability * Math.log(probability) / Math.log(2);
                }
            }
            
            // Normalize to 0-1 range (max entropy for N items is log2(N))
            float maxEntropy = (float) (Math.log(frequency.size()) / Math.log(2));
            return maxEntropy > 0 ? entropy / maxEntropy : 0;
        }
    }
    
    /**
     * 📋 ExpressionPattern: Tracks how a master has expressed a specific concept
     */
    public static class ExpressionPattern {
        public String concept;
        public List<String> usedPhrases = new ArrayList<>();
        public List<String> usedAngles = new ArrayList<>();
        public List<String> usedEmotionalTones = new ArrayList<>();
        public int timesUsed = 0;
        public long lastUsed = 0;
        public float diversityScore = 1.0f;
        
        public ExpressionPattern(String concept) {
            this.concept = concept;
        }
        
        public void addExpression(String phrase, String emotionalTone, String argumentativeAngle) {
            usedPhrases.add(phrase.toLowerCase());
            usedAngles.add(argumentativeAngle);
            usedEmotionalTones.add(emotionalTone);
            timesUsed++;
            lastUsed = System.currentTimeMillis();
            
            // Recalculate diversity score
            diversityScore = calculateCombinedDiversity();
        }
        
        public boolean shouldAvoidPhrase(String phrase) {
            return usedPhrases.contains(phrase.toLowerCase()) && 
                   (System.currentTimeMillis() - lastUsed) < PersonalityExpressionManager.PHRASE_COOLDOWN;
        }
        
        private float calculateCombinedDiversity() {
            // Combine diversity of phrases, angles, and tones
            float phraseDiversity = calculateListDiversity(usedPhrases);
            float angleDiversity = calculateListDiversity(usedAngles);
            float toneDiversity = calculateListDiversity(usedEmotionalTones);
            
            // Weighted average (angles matter most for personality expression)
            return (phraseDiversity * 0.3f + angleDiversity * 0.5f + toneDiversity * 0.2f);
        }
        
        private float calculateListDiversity(List<String> items) {
            if (items.isEmpty()) return 1.0f;
            
            Map<String, Integer> frequency = new HashMap<>();
            for (String item : items) {
                frequency.put(item, frequency.getOrDefault(item, 0) + 1);
            }
            
            int total = items.size();
            float entropy = 0;
            
            for (int count : frequency.values()) {
                float probability = (float) count / total;
                if (probability > 0) {
                    entropy -= probability * Math.log(probability) / Math.log(2);
                }
            }
            
            float maxEntropy = (float) (Math.log(frequency.size()) / Math.log(2));
            return maxEntropy > 0 ? entropy / maxEntropy : 0;
        }
    }
    
    /**
     * 🎯 ExpressionGuidance: Provides anti-repetition instructions for conversations
     */
    public static class ExpressionGuidance {
        public List<String> avoidPhrases = new ArrayList<>();
        public List<String> recentPhrases = new ArrayList<>();
        public List<String> usedAngles = new ArrayList<>();
        public List<String> suggestedAlternatives = new ArrayList<>();
        public float diversityScore = 1.0f;
        public boolean shouldUseFreshApproach = false;
        
        /**
         * 📝 Build anti-repetition instructions for AI prompts
         */
        public String buildAntiRepetitionInstructions() {
            StringBuilder instructions = new StringBuilder();
            
            if (shouldUseFreshApproach) {
                instructions.append("CRITICAL: Avoid repetitive phrasing! Express this concept in a completely fresh way. ");
            }
            
            if (!recentPhrases.isEmpty()) {
                instructions.append("RECENTLY USED PHRASES TO AVOID: ");
                instructions.append(String.join(", ", recentPhrases.subList(0, Math.min(3, recentPhrases.size()))));
                instructions.append(". ");
            }
            
            if (!usedAngles.isEmpty()) {
                instructions.append("PREVIOUSLY USED APPROACHES: ");
                instructions.append(String.join(", ", usedAngles.subList(0, Math.min(3, usedAngles.size()))));
                instructions.append(". ");
            }
            
            if (!suggestedAlternatives.isEmpty()) {
                instructions.append("FRESH APPROACHES TO TRY: ");
                instructions.append(String.join(" OR ", suggestedAlternatives));
                instructions.append(". ");
            }
            
            return instructions.toString();
        }
        
        /**
         * 🔍 Get expression variety analysis
         */
        public String getVarietyAnalysis() {
            if (diversityScore > 0.7f) {
                return "High expression variety - continue natural conversation";
            } else if (diversityScore > 0.4f) {
                return "Moderate variety - consider fresh approaches occasionally";
            } else {
                return "Low variety detected - actively avoid repetitive patterns";
            }
        }
    }
    
    
    // PersonalityExpressionManager instance
    private PersonalityExpressionManager expressionManager;
    
    /**
     * 🎭 Get the PersonalityExpressionManager instance
     */
    public PersonalityExpressionManager getExpressionManager() {
        if (expressionManager == null) {
            expressionManager = PersonalityExpressionManager.getInstance(context);
        }
        return expressionManager;
    }
    
    /**
     * 📊 Combined analysis including expression diversity
     */
    public EmotionalAnalysisResult analyzeWithExpressionGuidance(String masterName, String concept, 
                                                               String gameContext, String conversationContext,
                                                               Float currentEval, Float evalChange, 
                                                               EmotionalContext emotionalContext) {
        // Get base emotional analysis
        EmotionalAnalysisResult baseResult = analyzeEmotionalState(masterName, gameContext, conversationContext, 
                                                                   currentEval, evalChange, emotionalContext);
        
        // Get expression guidance for this concept
        ExpressionGuidance guidance = getExpressionManager().getExpressionGuidance(
            masterName, concept, baseResult.emotion.name
        );
        
        // Enhance result with expression guidance
        baseResult.expressionGuidance = guidance;
        
        // Log diversity analysis
        if (guidance.shouldUseFreshApproach) {
            Log.w(TAG, String.format("🎭 LOW EXPRESSION DIVERSITY: %s discussing '%s' (score: %.2f) - %s",
                    masterName, concept, guidance.diversityScore, guidance.getVarietyAnalysis()));
            
            // Notify callbacks about repetition
            for (EmotionalMemoryCallback callback : callbacks) {
                callback.onExpressionRepetitionDetected(masterName, concept, guidance.diversityScore);
            }
        }
        
        return baseResult;
    }
    
    /**
     * 🤝 RelationshipAdjustment: Data class for dynamic relationship evolution
     */
    public static class RelationshipAdjustment {
        public float similarityScore = 0.0f;
        public String concept = "";
        public String addressingStyle = "";
        public float relationshipImpact = 0.0f;
        public String personalityInfluence = "neutral"; // convergent, divergent, neutral
        
        public RelationshipAdjustment() {
            this.similarityScore = 0.0f;
            this.addressingStyle = "ADDRESSING STYLE: Standard professional interaction.";
            this.relationshipImpact = 0.0f;
            this.personalityInfluence = "neutral";
        }
        
        public boolean hasSignificantImpact() {
            return Math.abs(relationshipImpact) > 0.05f;
        }
        
        public boolean isConvergent() {
            return "convergent".equals(personalityInfluence);
        }
        
        public boolean isDivergent() {
            return "divergent".equals(personalityInfluence);
        }
    }
}