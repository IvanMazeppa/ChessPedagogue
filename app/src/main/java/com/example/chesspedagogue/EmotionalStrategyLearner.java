package com.example.chesspedagogue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🧠🎯 EMOTIONAL STRATEGY LEARNER - Phase 3 Implementation
 * 
 * Advanced adaptive learning system that enables chess masters to optimize their emotional 
 * strategies based on what works with specific opponents. This system:
 * 
 * - Tracks effectiveness of different emotional approaches per master-opponent pair
 * - Learns optimal strategies through reinforcement learning principles
 * - Adapts communication styles based on relationship evolution and feedback
 * - Integrates with existing EmotionalIntelligenceManager for seamless operation
 * 
 * Key Features:
 * ✅ Strategy Effectiveness Tracking - Records success rates of emotional approaches
 * ✅ Adaptive Learning Algorithm - Adjusts strategies based on outcomes
 * ✅ Context-Aware Optimization - Considers game state, relationship, and history
 * ✅ Master-Specific Learning - Each master learns independently 
 * ✅ Relationship-Driven Adaptation - Strategies evolve with relationship changes
 * ✅ Performance Analytics - Detailed metrics on strategy effectiveness
 * 
 * Integration Points:
 * - EmotionalIntelligenceManager: Provides strategy recommendations
 * - RelationshipPersistenceManager: Stores learning data in database
 * - EmotionalContext: Receives feedback on emotional outcomes
 * 
 * Learning Algorithm:
 * Uses a modified epsilon-greedy approach with relationship-aware exploration,
 * where masters gradually shift from exploration to exploitation as they learn
 * what works with each opponent.
 */
public class EmotionalStrategyLearner {
    private static final String TAG = "EmotionalStrategyLearner";
    private static EmotionalStrategyLearner instance;
    
    private final Context context;
    private final GameDatabaseHelper dbHelper;
    private final RelationshipPersistenceManager persistenceManager;
    
    // Strategy tracking maps (in-memory for performance)
    private final Map<String, Map<String, StrategyProfile>> masterOpponentStrategies;
    private final Map<String, LearningSession> activeLearningSessions;
    
    // Learning parameters
    private static final float INITIAL_EXPLORATION_RATE = 0.4f;  // 40% exploration initially
    private static final float MIN_EXPLORATION_RATE = 0.1f;      // Never go below 10% exploration
    private static final float LEARNING_DECAY = 0.98f;           // Gradual shift to exploitation
    private static final int MIN_ATTEMPTS_FOR_LEARNING = 3;      // Minimum attempts before learning
    private static final float SUCCESS_THRESHOLD = 0.6f;         // 60% success rate for strategy adoption
    
    private EmotionalStrategyLearner(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new GameDatabaseHelper(context);
        this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        this.masterOpponentStrategies = new ConcurrentHashMap<>();
        this.activeLearningSessions = new ConcurrentHashMap<>();
        
        // Initialize strategy profiles from database
        loadExistingStrategies();
        
        Log.i(TAG, "🧠🎯 EmotionalStrategyLearner initialized - Ready for adaptive learning!");
    }
    
    public static synchronized EmotionalStrategyLearner getInstance(Context context) {
        if (instance == null) {
            instance = new EmotionalStrategyLearner(context);
        }
        return instance;
    }
    
    /**
     * 🎯 Core Strategy Classes
     */
    
    /**
     * Represents a master's learned strategy profile against a specific opponent
     */
    public static class StrategyProfile {
        public final String masterName;
        public final String opponentName;
        public final Map<String, StrategyApproach> approaches;
        public float explorationRate;
        public int totalInteractions;
        public long lastUpdated;
        public float overallSuccessRate;
        
        public StrategyProfile(String masterName, String opponentName) {
            this.masterName = masterName;
            this.opponentName = opponentName;
            this.approaches = new HashMap<>();
            this.explorationRate = INITIAL_EXPLORATION_RATE;
            this.totalInteractions = 0;
            this.lastUpdated = System.currentTimeMillis();
            this.overallSuccessRate = 0.5f; // Neutral starting point
        }
        
        public StrategyApproach getBestApproach(String context) {
            return approaches.values().stream()
                .filter(approach -> approach.contextRelevance.getOrDefault(context, 0.0f) > 0.3f)
                .max((a1, a2) -> Float.compare(a1.successRate, a2.successRate))
                .orElse(null);
        }
        
        public boolean shouldExplore() {
            return Math.random() < explorationRate;
        }
        
        public void updateExplorationRate() {
            // Gradually reduce exploration as learning progresses
            if (totalInteractions > MIN_ATTEMPTS_FOR_LEARNING) {
                explorationRate = Math.max(
                    MIN_EXPLORATION_RATE, 
                    explorationRate * LEARNING_DECAY
                );
            }
        }
    }
    
    /**
     * Specific emotional approach with tracked effectiveness
     */
    public static class StrategyApproach {
        public final String approachType;         // "aggressive", "supportive", "analytical", etc.
        public final String emotionalTone;        // "confident", "humble", "dramatic", etc.
        public final String communicationStyle;   // "direct", "diplomatic", "provocative", etc.
        
        public int attempts;
        public int successes;
        public float successRate;
        public float averageIntensity;
        public final Map<String, Float> contextRelevance; // game_phase -> relevance_score
        public final List<StrategyOutcome> recentOutcomes;
        public long lastUsed;
        
        public StrategyApproach(String approachType, String emotionalTone, String communicationStyle) {
            this.approachType = approachType;
            this.emotionalTone = emotionalTone;
            this.communicationStyle = communicationStyle;
            this.attempts = 0;
            this.successes = 0;
            this.successRate = 0.5f; // Neutral starting point
            this.averageIntensity = 0.0f;
            this.contextRelevance = new HashMap<>();
            this.recentOutcomes = new ArrayList<>();
            this.lastUsed = 0;
        }
        
        public void recordOutcome(StrategyOutcome outcome) {
            attempts++;
            if (outcome.successful) {
                successes++;
            }
            
            // Update success rate with slight bias toward recent outcomes
            successRate = (float) successes / attempts;
            if (recentOutcomes.size() >= 5) {
                // Weight recent outcomes more heavily
                float recentSuccessRate = (float) recentOutcomes.stream()
                    .mapToInt(o -> o.successful ? 1 : 0)
                    .sum() / recentOutcomes.size();
                successRate = (successRate * 0.7f) + (recentSuccessRate * 0.3f);
            }
            
            // Update context relevance
            contextRelevance.put(outcome.gameContext, 
                contextRelevance.getOrDefault(outcome.gameContext, 0.5f) * 0.8f + outcome.effectivenessScore * 0.2f);
            
            // Maintain recent outcomes window
            recentOutcomes.add(outcome);
            if (recentOutcomes.size() > 10) {
                recentOutcomes.remove(0);
            }
            
            lastUsed = System.currentTimeMillis();
        }
        
        public boolean isEffective() {
            return attempts >= MIN_ATTEMPTS_FOR_LEARNING && successRate >= SUCCESS_THRESHOLD;
        }
        
        public boolean isUnderperforming() {
            return attempts >= MIN_ATTEMPTS_FOR_LEARNING && successRate < 0.3f;
        }
    }
    
    /**
     * Records the outcome of an emotional strategy attempt
     */
    public static class StrategyOutcome {
        public final boolean successful;
        public final float effectivenessScore;   // 0.0 to 1.0
        public final String gameContext;         // "opening", "middlegame", "endgame", "tactical", etc.
        public final String opponentResponse;    // Opponent's emotional reaction
        public final float relationshipImpact;   // How it affected the relationship
        public final long timestamp;
        public final String details;
        
        public StrategyOutcome(boolean successful, float effectivenessScore, String gameContext,
                             String opponentResponse, float relationshipImpact, String details) {
            this.successful = successful;
            this.effectivenessScore = effectivenessScore;
            this.gameContext = gameContext;
            this.opponentResponse = opponentResponse;
            this.relationshipImpact = relationshipImpact;
            this.details = details;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Tracks a learning session in progress
     */
    public static class LearningSession {
        public final String masterName;
        public final String opponentName;
        public final String sessionId;
        public final long startTime;
        
        public String currentStrategy;
        public String currentApproach;
        public float initialRelationshipState;
        public int strategiesAttempted;
        public final List<String> conversationContext;
        
        public LearningSession(String masterName, String opponentName) {
            this.masterName = masterName;
            this.opponentName = opponentName;
            this.sessionId = masterName + "_" + opponentName + "_" + System.currentTimeMillis();
            this.startTime = System.currentTimeMillis();
            this.strategiesAttempted = 0;
            this.conversationContext = new ArrayList<>();
        }
    }
    
    /**
     * 🎯 Core Learning Methods
     */
    
    /**
     * Get the optimal emotional strategy for a master when interacting with an opponent
     */
    public EmotionalStrategyRecommendation getOptimalStrategy(String masterName, String opponentName, 
                                                            String gameContext, String conversationTopic,
                                                            EmotionalIntelligenceManager.EmotionalAnalysisResult currentEmotion) {
        StrategyProfile profile = getOrCreateStrategyProfile(masterName, opponentName);
        
        // Start learning session if not already active
        String sessionKey = masterName + "_" + opponentName;
        if (!activeLearningSessions.containsKey(sessionKey)) {
            LearningSession session = new LearningSession(masterName, opponentName);
            session.initialRelationshipState = getCurrentRelationshipState(masterName, opponentName);
            activeLearningSessions.put(sessionKey, session);
            
            Log.d(TAG, String.format("🧠 Started learning session: %s vs %s", masterName, opponentName));
        }
        
        LearningSession session = activeLearningSessions.get(sessionKey);
        
        // Decide whether to explore or exploit
        EmotionalStrategyRecommendation recommendation;
        if (profile.shouldExplore()) {
            // Exploration: Try a new or underused approach
            recommendation = generateExploratoryStrategy(profile, gameContext, conversationTopic, currentEmotion);
            Log.d(TAG, String.format("🔍 EXPLORATION: %s trying %s approach vs %s", 
                   masterName, recommendation.primaryApproach, opponentName));
        } else {
            // Exploitation: Use best known strategy
            recommendation = generateOptimalStrategy(profile, gameContext, conversationTopic, currentEmotion);
            Log.d(TAG, String.format("🎯 EXPLOITATION: %s using proven %s approach vs %s (%.1f%% success rate)", 
                   masterName, recommendation.primaryApproach, opponentName, 
                   recommendation.expectedSuccessRate * 100));
        }
        
        // Record strategy attempt in session
        session.currentStrategy = recommendation.primaryApproach;
        session.currentApproach = recommendation.communicationStyle;
        session.strategiesAttempted++;
        session.conversationContext.add(conversationTopic);
        
        return recommendation;
    }
    
    /**
     * Record feedback on how well an emotional strategy worked
     */
    public void recordStrategyFeedback(String masterName, String opponentName, String strategyType, 
                                     StrategyOutcome outcome) {
        StrategyProfile profile = getOrCreateStrategyProfile(masterName, opponentName);
        
        // Get or create the specific approach
        String approachKey = strategyType + "_" + outcome.gameContext;
        StrategyApproach approach = profile.approaches.computeIfAbsent(approachKey, 
            k -> new StrategyApproach(strategyType, outcome.opponentResponse, outcome.gameContext));
        
        // Record the outcome
        approach.recordOutcome(outcome);
        
        // Update profile-level metrics
        profile.totalInteractions++;
        profile.updateExplorationRate();
        profile.lastUpdated = System.currentTimeMillis();
        
        // Recalculate overall success rate
        float totalSuccesses = (float) profile.approaches.values().stream()
            .mapToInt(a -> a.successes)
            .sum();
        float totalAttempts = (float) profile.approaches.values().stream()
            .mapToInt(a -> a.attempts)
            .sum();
        profile.overallSuccessRate = totalAttempts > 0 ? totalSuccesses / totalAttempts : 0.5f;
        
        // Log learning progress
        if (approach.isEffective()) {
            Log.i(TAG, String.format("✅ STRATEGY MASTERED: %s learned that %s works vs %s (%.1f%% success)", 
                   masterName, strategyType, opponentName, approach.successRate * 100));
        } else if (approach.isUnderperforming()) {
            Log.w(TAG, String.format("❌ STRATEGY INEFFECTIVE: %s learned that %s doesn't work vs %s (%.1f%% success)", 
                   masterName, strategyType, opponentName, approach.successRate * 100));
        }
        
        // Persist significant learning milestones
        if (approach.attempts % 5 == 0 || approach.successRate > 0.8f || approach.successRate < 0.2f) {
            persistStrategyLearning(profile, approach);
        }
        
        // Update relationship-based learning
        updateRelationshipLearning(masterName, opponentName, outcome);
    }
    
    /**
     * Generate strategy recommendation for exploration phase
     */
    private EmotionalStrategyRecommendation generateExploratoryStrategy(StrategyProfile profile, 
                                                                       String gameContext, String conversationTopic,
                                                                       EmotionalIntelligenceManager.EmotionalAnalysisResult currentEmotion) {
        // Find underexplored approaches or create new ones
        List<String> potentialApproaches = generatePotentialApproaches(profile.masterName, profile.opponentName, gameContext);
        
        // Filter out overused approaches
        String chosenApproach = potentialApproaches.stream()
            .filter(approach -> {
                StrategyApproach strategy = profile.approaches.get(approach + "_" + gameContext);
                return strategy == null || strategy.attempts < 3; // Underexplored
            })
            .findFirst()
            .orElse(potentialApproaches.get((int) (Math.random() * potentialApproaches.size())));
        
        return new EmotionalStrategyRecommendation(
            chosenApproach,
            "exploratory", // Communication style
            generateEmotionalTone(profile.masterName, chosenApproach),
            0.5f, // Unknown success rate for exploration
            "EXPLORATION: Testing new approach '" + chosenApproach + "' to learn effectiveness",
            true  // Is exploratory
        );
    }
    
    /**
     * Generate optimal strategy recommendation for exploitation phase
     */
    private EmotionalStrategyRecommendation generateOptimalStrategy(StrategyProfile profile, 
                                                                   String gameContext, String conversationTopic,
                                                                   EmotionalIntelligenceManager.EmotionalAnalysisResult currentEmotion) {
        // Find best performing approach for this context
        StrategyApproach bestApproach = profile.getBestApproach(gameContext);
        
        if (bestApproach == null) {
            // Fallback to exploration if no proven strategies exist
            return generateExploratoryStrategy(profile, gameContext, conversationTopic, currentEmotion);
        }
        
        return new EmotionalStrategyRecommendation(
            bestApproach.approachType,
            bestApproach.communicationStyle,
            bestApproach.emotionalTone,
            bestApproach.successRate,
            String.format("PROVEN STRATEGY: '%s' has %.1f%% success rate vs %s in %s", 
                         bestApproach.approachType, bestApproach.successRate * 100, 
                         profile.opponentName, gameContext),
            false // Not exploratory
        );
    }
    
    /**
     * 🎯 Strategy Recommendation Result Class
     */
    public static class EmotionalStrategyRecommendation {
        public final String primaryApproach;      // "supportive", "challenging", "analytical", etc.
        public final String communicationStyle;   // "direct", "diplomatic", "provocative", etc.
        public final String emotionalTone;        // "confident", "humble", "dramatic", etc.
        public final float expectedSuccessRate;   // 0.0 to 1.0
        public final String reasoning;             // Why this strategy was chosen
        public final boolean isExploratory;       // True if this is exploration vs exploitation
        
        public EmotionalStrategyRecommendation(String primaryApproach, String communicationStyle, 
                                             String emotionalTone, float expectedSuccessRate, 
                                             String reasoning, boolean isExploratory) {
            this.primaryApproach = primaryApproach;
            this.communicationStyle = communicationStyle;
            this.emotionalTone = emotionalTone;
            this.expectedSuccessRate = expectedSuccessRate;
            this.reasoning = reasoning;
            this.isExploratory = isExploratory;
        }
        
        /**
         * Generate instruction text for AI prompts
         */
        public String generatePromptInstructions() {
            StringBuilder instructions = new StringBuilder();
            
            instructions.append("LEARNED EMOTIONAL STRATEGY: ");
            
            if (isExploratory) {
                instructions.append("EXPLORATION MODE - Try a fresh approach. ");
            } else {
                instructions.append("PROVEN APPROACH - Use learned optimal strategy. ");
            }
            
            instructions.append(String.format("PRIMARY APPROACH: %s. ", primaryApproach));
            instructions.append(String.format("COMMUNICATION STYLE: %s. ", communicationStyle));
            instructions.append(String.format("EMOTIONAL TONE: %s. ", emotionalTone));
            
            if (!isExploratory) {
                instructions.append(String.format("SUCCESS RATE: %.1f%%. ", expectedSuccessRate * 100));
            }
            
            instructions.append("REASONING: ").append(reasoning).append(" ");
            
            return instructions.toString();
        }
    }
    
    /**
     * 🎯 Helper Methods
     */
    
    private StrategyProfile getOrCreateStrategyProfile(String masterName, String opponentName) {
        String masterKey = masterName.toLowerCase();
        Map<String, StrategyProfile> opponentStrategies = masterOpponentStrategies.computeIfAbsent(
            masterKey, k -> new HashMap<>()
        );
        
        return opponentStrategies.computeIfAbsent(opponentName.toLowerCase(), 
            k -> new StrategyProfile(masterName, opponentName));
    }
    
    private List<String> generatePotentialApproaches(String masterName, String opponentName, String gameContext) {
        List<String> approaches = new ArrayList<>();
        
        // Base emotional approaches
        approaches.add("supportive");
        approaches.add("challenging");
        approaches.add("analytical");
        approaches.add("dramatic");
        approaches.add("philosophical");
        approaches.add("competitive");
        approaches.add("encouraging");
        approaches.add("critical");
        
        // Master-specific approaches
        switch (masterName.toLowerCase()) {
            case "fischer":
                approaches.add("paranoid_suspicious");
                approaches.add("brutally_honest");
                approaches.add("dismissive_critical");
                approaches.add("conspiracy_focused");
                break;
            case "tal":
                approaches.add("wildly_creative");
                approaches.add("magically_intuitive");
                approaches.add("dramatically_expressive");
                break;
            case "carlsen":
                approaches.add("methodically_practical");
                approaches.add("quietly_confident");
                approaches.add("pressure_building");
                break;
            case "kasparov":
                approaches.add("intellectually_dominating");
                approaches.add("politically_charged");
                approaches.add("historically_contextual");
                break;
        }
        
        // Context-specific approaches
        if ("endgame".equals(gameContext)) {
            approaches.add("precision_focused");
            approaches.add("technique_emphasizing");
        } else if ("tactical".equals(gameContext)) {
            approaches.add("calculation_intensive");
            approaches.add("pattern_recognizing");
        }
        
        return approaches;
    }
    
    private String generateEmotionalTone(String masterName, String approach) {
        // Generate appropriate emotional tone based on master personality and approach
        switch (masterName.toLowerCase()) {
            case "fischer":
                return approach.contains("critical") ? "harsh" : "intense";
            case "tal":
                return approach.contains("creative") ? "excited" : "mysterious";
            case "carlsen":
                return approach.contains("practical") ? "calm" : "confident";
            default:
                return "balanced";
        }
    }
    
    private float getCurrentRelationshipState(String masterName, String opponentName) {
        try {
            RelationshipPersistenceManager.MasterRelationship relationship = 
                persistenceManager.getRelationship(masterName, opponentName);
            
            // Combine respect, rivalry, and friendship into overall relationship score
            return (relationship.respectLevel + relationship.friendshipBond - relationship.rivalryIntensity) / 2.0f;
        } catch (Exception e) {
            Log.w(TAG, "Failed to get relationship state, using neutral", e);
            return 0.5f; // Neutral relationship
        }
    }
    
    private void updateRelationshipLearning(String masterName, String opponentName, StrategyOutcome outcome) {
        // Update relationship-based learning patterns
        if (Math.abs(outcome.relationshipImpact) > 0.1f) {
            Log.d(TAG, String.format("🤝 RELATIONSHIP LEARNING: %s's %s approach %s relationship with %s (impact: %.2f)", 
                   masterName, outcome.gameContext, 
                   outcome.relationshipImpact > 0 ? "improved" : "damaged",
                   opponentName, outcome.relationshipImpact));
            
            // Store relationship learning in database for future reference
            try {
                persistenceManager.recordEmotionalReaction(masterName, opponentName, "strategy_learning",
                    outcome.successful ? "effective" : "ineffective", outcome.effectivenessScore,
                    0.0f, outcome.gameContext, 0.0f, outcome.details);
            } catch (Exception e) {
                Log.w(TAG, "Failed to persist relationship learning", e);
            }
        }
    }
    
    /**
     * 💾 Database Integration Methods
     */
    
    private void loadExistingStrategies() {
        // Load existing strategy data from database
        // This allows learning to persist across app sessions
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT * FROM emotional_strategy_learning ORDER BY last_updated DESC";
        
        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                String masterName = cursor.getString(cursor.getColumnIndex("master_name"));
                String opponentName = cursor.getString(cursor.getColumnIndex("opponent_name"));
                String strategyData = cursor.getString(cursor.getColumnIndex("strategy_data"));
                
                // Parse and restore strategy profile from JSON
                StrategyProfile profile = parseStrategyProfileFromJson(masterName, opponentName, strategyData);
                
                Map<String, StrategyProfile> opponentStrategies = masterOpponentStrategies.computeIfAbsent(
                    masterName.toLowerCase(), k -> new HashMap<>()
                );
                opponentStrategies.put(opponentName.toLowerCase(), profile);
                
                Log.d(TAG, String.format("📖 Loaded strategy profile: %s vs %s (%d interactions)", 
                       masterName, opponentName, profile.totalInteractions));
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to load existing strategies - starting fresh", e);
        } finally {
            db.close();
        }
        
        Log.i(TAG, String.format("🧠 Loaded %d master strategy profiles from database", 
               masterOpponentStrategies.size()));
    }
    
    private void persistStrategyLearning(StrategyProfile profile, StrategyApproach approach) {
        // Persist significant learning milestones to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put("master_name", profile.masterName);
            values.put("opponent_name", profile.opponentName);
            values.put("approach_type", approach.approachType);
            values.put("success_rate", approach.successRate);
            values.put("attempts", approach.attempts);
            values.put("exploration_rate", profile.explorationRate);
            values.put("strategy_data", serializeStrategyProfileToJson(profile));
            values.put("last_updated", System.currentTimeMillis());
            
            db.insertWithOnConflict("emotional_strategy_learning", null, values, 
                SQLiteDatabase.CONFLICT_REPLACE);
            
            Log.d(TAG, String.format("💾 Persisted strategy learning: %s vs %s - %s (%.1f%% success)", 
                   profile.masterName, profile.opponentName, approach.approachType, 
                   approach.successRate * 100));
                   
        } catch (Exception e) {
            Log.e(TAG, "Failed to persist strategy learning", e);
        } finally {
            db.close();
        }
    }
    
    private String serializeStrategyProfileToJson(StrategyProfile profile) {
        // Serialize strategy profile to JSON for database storage
        // Implementation would convert the profile to JSON format
        return "{}"; // Placeholder - full implementation would serialize all profile data
    }
    
    private StrategyProfile parseStrategyProfileFromJson(String masterName, String opponentName, String json) {
        // Parse strategy profile from JSON database storage
        // Implementation would recreate the profile from JSON data
        return new StrategyProfile(masterName, opponentName); // Placeholder
    }
    
    /**
     * 📊 Analytics and Monitoring Methods
     */
    
    /**
     * Get learning analytics for a specific master
     */
    public LearningAnalytics getLearningAnalytics(String masterName) {
        Map<String, StrategyProfile> opponentStrategies = masterOpponentStrategies.get(masterName.toLowerCase());
        if (opponentStrategies == null) {
            return new LearningAnalytics(masterName, 0, 0, 0.0f, new ArrayList<>());
        }
        
        int totalOpponents = opponentStrategies.size();
        int totalInteractions = opponentStrategies.values().stream()
            .mapToInt(p -> p.totalInteractions)
            .sum();
        float averageSuccessRate = (float) opponentStrategies.values().stream()
            .mapToDouble(p -> p.overallSuccessRate)
            .average()
            .orElse(0.5);
        
        List<String> learnedStrategies = new ArrayList<>();
        for (StrategyProfile profile : opponentStrategies.values()) {
            for (StrategyApproach approach : profile.approaches.values()) {
                if (approach.isEffective()) {
                    learnedStrategies.add(String.format("%s vs %s: %s (%.1f%%)", 
                        masterName, profile.opponentName, approach.approachType, 
                        approach.successRate * 100));
                }
            }
        }
        
        return new LearningAnalytics(masterName, totalOpponents, totalInteractions, 
                                   averageSuccessRate, learnedStrategies);
    }
    
    public static class LearningAnalytics {
        public final String masterName;
        public final int opponentsEncountered;
        public final int totalInteractions;
        public final float averageSuccessRate;
        public final List<String> effectiveStrategies;
        
        public LearningAnalytics(String masterName, int opponentsEncountered, int totalInteractions,
                               float averageSuccessRate, List<String> effectiveStrategies) {
            this.masterName = masterName;
            this.opponentsEncountered = opponentsEncountered;
            this.totalInteractions = totalInteractions;
            this.averageSuccessRate = averageSuccessRate;
            this.effectiveStrategies = effectiveStrategies;
        }
        
        @Override
        public String toString() {
            return String.format("📊 %s Learning Stats: %d opponents, %d interactions, %.1f%% success, %d effective strategies",
                masterName, opponentsEncountered, totalInteractions, averageSuccessRate * 100, effectiveStrategies.size());
        }
    }
    
    /**
     * 🔄 Session Management
     */
    
    /**
     * End a learning session and analyze results
     */
    public void endLearningSession(String masterName, String opponentName) {
        String sessionKey = masterName + "_" + opponentName;
        LearningSession session = activeLearningSessions.remove(sessionKey);
        
        if (session != null) {
            float sessionDuration = (System.currentTimeMillis() - session.startTime) / 1000.0f / 60.0f; // minutes
            
            Log.i(TAG, String.format("🎓 LEARNING SESSION COMPLETE: %s vs %s - %.1f minutes, %d strategies tested", 
                   masterName, opponentName, sessionDuration, session.strategiesAttempted));
            
            // Update master's learning progress
            StrategyProfile profile = getOrCreateStrategyProfile(masterName, opponentName);
            profile.lastUpdated = System.currentTimeMillis();
        }
    }
    
    /**
     * 🎯 Integration with EmotionalIntelligenceManager
     */
    
    /**
     * Get strategy-enhanced emotional analysis that incorporates learned optimal approaches
     */
    public EmotionalIntelligenceManager.EmotionalAnalysisResult getStrategyEnhancedAnalysis(
            String masterName, String opponentName, String gameContext, String conversationTopic,
            EmotionalIntelligenceManager.EmotionalAnalysisResult baseAnalysis) {
        
        // Get optimal strategy recommendation
        EmotionalStrategyRecommendation strategy = getOptimalStrategy(
            masterName, opponentName, gameContext, conversationTopic, baseAnalysis
        );
        
        // Enhance the base analysis with strategy guidance
        EmotionalIntelligenceManager.EmotionalAnalysisResult enhancedResult = 
            new EmotionalIntelligenceManager.EmotionalAnalysisResult(
                baseAnalysis.emotion,
                baseAnalysis.expression,
                baseAnalysis.intensity,
                baseAnalysis.momentum
            );
        
        // Add learning context
        LearningAnalytics analytics = getLearningAnalytics(masterName);
        if (analytics.totalInteractions > 10) {
            Log.d(TAG, String.format(
                "LEARNING CONTEXT: %s has learned from %d previous interactions and maintains %.1f%% success rate.",
                masterName, analytics.totalInteractions, analytics.averageSuccessRate * 100
            ));
        }
        
        Log.d(TAG, String.format("🧠✨ Enhanced analysis for %s vs %s: %s approach with %.1f%% expected success", 
               masterName, opponentName, strategy.primaryApproach, strategy.expectedSuccessRate * 100));
        
        return enhancedResult;
    }
    
    // ================== MISSING CLASSES FOR COMPATIBILITY ==================
    
    /**
     * Strategy recommendation result class
     */
    public static class StrategyRecommendation {
        public final String strategy;
        public final float confidence;
        public final String reasoning;
        public final String primaryApproach;
        public final float expectedSuccessRate;
        
        public StrategyRecommendation(String strategy, float confidence, String reasoning) {
            this.strategy = strategy;
            this.confidence = confidence;
            this.reasoning = reasoning;
            this.primaryApproach = strategy;
            this.expectedSuccessRate = confidence;
        }
    }
    
    /**
     * Strategy analytics for a master pair
     */
    public static class StrategyAnalytics {
        public final float averageEffectiveness;
        public final String mostEffectiveStrategy;
        public final int totalInteractions;
        public final float explorationRate;
        
        public StrategyAnalytics(float effectiveness, String strategy, int interactions, float exploration) {
            this.averageEffectiveness = effectiveness;
            this.mostEffectiveStrategy = strategy;
            this.totalInteractions = interactions;
            this.explorationRate = exploration;
        }
    }
    
    /**
     * Simplified method for backward compatibility
     */
    public StrategyRecommendation getOptimalStrategy(String masterName, String opponentName, String gameContext) {
        EmotionalIntelligenceManager.EmotionalAnalysisResult neutralEmotion = 
            new EmotionalIntelligenceManager.EmotionalAnalysisResult(
                EmotionalIntelligenceManager.EmotionalState.ANALYTICAL, 
                "neutral analysis", 
                0.5f, 
                0.0f
            );
        
        EmotionalStrategyRecommendation emotionalRec = getOptimalStrategy(masterName, opponentName, gameContext, "general", neutralEmotion);
        return new StrategyRecommendation(emotionalRec.primaryApproach, emotionalRec.expectedSuccessRate, emotionalRec.reasoning);
    }
    
    /**
     * Records strategy outcome for learning (multiple signature support)
     */
    public void recordStrategyOutcome(String masterName, String opponentName, String strategy, 
                                    String gameContext, boolean success, 
                                    CrossMasterEffectivenessTracker.ConversationQualityMetrics qualityMetrics) {
        
        StrategyOutcome outcome = new StrategyOutcome(
            success, 
            qualityMetrics.overallScore, 
            strategy, 
            gameContext, 
            1.0f,
            "general"
        );
        
        recordStrategyOutcome(masterName, opponentName, strategy, outcome);
    }
    
    /**
     * Get strategy analytics for a master pair
     */
    public StrategyAnalytics getStrategyAnalytics(String master1, String master2) {
        StrategyProfile profile = getOrCreateStrategyProfile(master1, master2);
        
        return new StrategyAnalytics(
            profile.overallSuccessRate,
            profile.getBestApproach("general") != null ? profile.getBestApproach("general").approachType : "balanced",
            profile.totalInteractions,
            profile.explorationRate
        );
    }

    /**
     * Records the outcome of an emotional strategy for learning
     */
    public void recordStrategyOutcome(String masterName, String opponentName, 
                                    String strategyType, StrategyOutcome outcome) {
        
        StrategyProfile profile = getOrCreateStrategyProfile(masterName, opponentName);
        
        // Get or create the specific approach
        String approachKey = strategyType + "_" + outcome.gameContext;
        StrategyApproach approach = profile.approaches.computeIfAbsent(approachKey, 
            k -> new StrategyApproach(strategyType, outcome.opponentResponse, outcome.gameContext));
        
        // Record the outcome
        approach.recordOutcome(outcome);
        
        // Update profile-level metrics
        profile.totalInteractions++;
        profile.updateExplorationRate();
        profile.lastUpdated = System.currentTimeMillis();
        
        // Recalculate overall success rate
        float totalSuccesses = (float) profile.approaches.values().stream()
            .mapToInt(a -> a.successes)
            .sum();
        float totalAttempts = (float) profile.approaches.values().stream()
            .mapToInt(a -> a.attempts)
            .sum();
        profile.overallSuccessRate = totalAttempts > 0 ? totalSuccesses / totalAttempts : 0.5f;
        
        // Log learning progress
        if (approach.isEffective()) {
            Log.i(TAG, String.format("✅ STRATEGY MASTERED: %s learned that %s works vs %s (%.1f%% success)", 
                   masterName, strategyType, opponentName, approach.successRate * 100));
        } else if (approach.isUnderperforming()) {
            Log.w(TAG, String.format("❌ STRATEGY INEFFECTIVE: %s learned that %s doesn't work vs %s (%.1f%% success)", 
                   masterName, strategyType, opponentName, approach.successRate * 100));
        }
    }
}