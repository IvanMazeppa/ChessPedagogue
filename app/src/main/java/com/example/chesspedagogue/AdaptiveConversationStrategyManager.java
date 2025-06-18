package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Phase 3: Adaptive Conversation Strategy Manager
 * 
 * The orchestration layer that combines all Phase 3 components to enable masters
 * to intelligently select optimal emotional strategies based on:
 * - Individual learning from EmotionalStrategyLearner
 * - Cross-master insights from CrossMasterEffectivenessTracker  
 * - Relationship dynamics from DynamicRelationshipEvolution
 * - Conversation context and game state
 * 
 * This creates the "brain" that allows masters to adapt their emotional approaches
 * for maximum conversation quality and relationship development.
 * 
 * Key Features:
 * - Multi-source strategy recommendation engine
 * - Context-aware emotional approach selection
 * - Real-time strategy adaptation based on conversation flow
 * - Learning feedback loop integration
 * - Relationship-optimized conversation planning
 */
public class AdaptiveConversationStrategyManager {
    private static final String TAG = "AdaptiveStrategy";
    private static AdaptiveConversationStrategyManager instance;
    
    private final Context context;
    private final EmotionalStrategyLearner strategyLearner;
    private final CrossMasterEffectivenessTracker effectivenessTracker;
    private final DynamicRelationshipEvolution relationshipEvolution;
    private final ConversationMemoryManager conversationMemory;
    
    // Strategy selection parameters
    private static final float LEARNING_WEIGHT = 0.4f;        // Individual learning influence
    private static final float CROSS_LEARNING_WEIGHT = 0.3f;  // Cross-master learning influence
    private static final float RELATIONSHIP_WEIGHT = 0.3f;    // Relationship dynamics influence
    
    private static final float STRATEGY_CONFIDENCE_THRESHOLD = 0.7f;
    private static final int MAX_STRATEGY_RECOMMENDATIONS = 5;
    
    // Conversation quality monitoring
    private final Map<String, ConversationMonitor> activeConversations = new HashMap<>();
    
    private AdaptiveConversationStrategyManager(Context context) {
        this.context = context;
        this.strategyLearner = EmotionalStrategyLearner.getInstance(context);
        this.effectivenessTracker = CrossMasterEffectivenessTracker.getInstance(context);
        this.relationshipEvolution = DynamicRelationshipEvolution.getInstance(context);
        this.conversationMemory = ConversationMemoryManager.getInstance(context);
    }
    
    public static synchronized AdaptiveConversationStrategyManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdaptiveConversationStrategyManager(context);
        }
        return instance;
    }
    
    /**
     * Gets the optimal emotional strategy for a master in current context
     */
    public OptimalStrategyRecommendation getOptimalStrategy(String masterName, String opponentName,
                                                          ConversationContext context) {
        
        // Gather insights from all learning sources
        EmotionalStrategyLearner.StrategyRecommendation individualLearning = 
            strategyLearner.getOptimalStrategy(masterName, opponentName, context.gameContext);
        
        CrossMasterEffectivenessTracker.CrossLearningRecommendations crossLearning = 
            effectivenessTracker.getCrossLearningRecommendations(masterName, opponentName);
        
        DynamicRelationshipEvolution.RelationshipEmotionalPreferences relationshipPrefs = 
            relationshipEvolution.getEmotionalPreferences(masterName, opponentName);
        
        // Combine insights using weighted scoring
        Map<String, StrategyScore> strategyScores = combineStrategyInsights(
            individualLearning, crossLearning, relationshipPrefs, context
        );
        
        // Select optimal strategy
        OptimalStrategyRecommendation recommendation = selectOptimalStrategy(strategyScores, context);
        
        // Add contextual adaptations
        recommendation = enhanceWithContextualAdaptations(recommendation, masterName, opponentName, context);
        
        Log.d(TAG, String.format("🎯 Optimal strategy for %s vs %s: '%s' (confidence: %.2f)",
                                masterName, opponentName, recommendation.strategy, recommendation.confidence));
        
        return recommendation;
    }
    
    /**
     * Monitors conversation quality and adapts strategy in real-time
     */
    public void startConversationMonitoring(String conversationId, String master1, String master2, 
                                          ConversationContext initialContext) {
        
        ConversationMonitor monitor = new ConversationMonitor(conversationId, master1, master2, initialContext);
        activeConversations.put(conversationId, monitor);
        
        Log.d(TAG, String.format("🔍 Started monitoring conversation: %s (%s vs %s)",
                                conversationId, master1, master2));
    }
    
    /**
     * Provides real-time strategy adaptation based on conversation flow
     */
    public StrategyAdaptation getConversationAdaptation(String conversationId, 
                                                       String currentStrategy,
                                                       float recentQuality) {
        
        ConversationMonitor monitor = activeConversations.get(conversationId);
        if (monitor == null) {
            return StrategyAdaptation.noChange();
        }
        
        monitor.recordInteraction(currentStrategy, recentQuality);
        
        // Check if strategy change is needed
        if (monitor.shouldAdaptStrategy()) {
            // Get new strategy recommendation
            OptimalStrategyRecommendation newStrategy = getOptimalStrategy(
                monitor.master1, monitor.master2, monitor.getCurrentContext()
            );
            
            return new StrategyAdaptation(
                true,
                newStrategy.strategy,
                monitor.getAdaptationReason(),
                newStrategy.confidence
            );
        }
        
        return StrategyAdaptation.noChange();
    }
    
    /**
     * Records conversation outcome and updates all learning systems
     */
    public void recordConversationOutcome(String conversationId, ConversationOutcome outcome) {
        ConversationMonitor monitor = activeConversations.get(conversationId);
        if (monitor == null) {
            Log.w(TAG, "No monitor found for conversation: " + conversationId);
            return;
        }
        
        // Update individual learning
        strategyLearner.recordStrategyOutcome(
            monitor.master1, monitor.master2, outcome.finalStrategy, 
            outcome.gameContext, outcome.success, outcome.qualityMetrics
        );
        
        // Update cross-master effectiveness tracking
        effectivenessTracker.recordConversationOutcome(
            monitor.master1, monitor.master2, outcome.finalStrategy,
            outcome.conversationContext, outcome.qualityMetrics
        );
        
        // Update relationship evolution
        relationshipEvolution.recordRelationshipInteraction(
            monitor.master1, monitor.master2, outcome.finalStrategy,
            outcome.qualityMetrics.overallScore, outcome.conversationContext
        );
        
        // Clean up monitoring
        activeConversations.remove(conversationId);
        
        Log.d(TAG, String.format("✅ Recorded conversation outcome: %s (strategy: %s, quality: %.2f)",
                                conversationId, outcome.finalStrategy, outcome.qualityMetrics.overallScore));
    }
    
    /**
     * Provides comprehensive strategy insights for a master pair
     */
    public ComprehensiveStrategyInsights getStrategyInsights(String master1, String master2) {
        ComprehensiveStrategyInsights insights = new ComprehensiveStrategyInsights();
        
        // Individual learning insights
        insights.individualLearning = strategyLearner.getStrategyAnalytics(master1, master2);
        
        // Cross-learning insights
        insights.crossLearningOpportunities = effectivenessTracker.getCrossLearningRecommendations(master1, master2);
        
        // Relationship evolution insights
        insights.relationshipEvolution = relationshipEvolution.analyzeEvolution(master1, master2, 30);
        
        // Strategy optimization suggestions
        insights.optimizationSuggestions = relationshipEvolution.getOptimizationSuggestions(master1, master2);
        
        // Performance trends
        insights.performanceTrends = effectivenessTracker.analyzeConversationTrends(master1, master2, 14);
        
        return insights;
    }
    
    // Core data structures
    public static class ConversationContext {
        public final String gameContext;          // Current game situation
        public final String emotionalContext;     // Current emotional atmosphere
        public final List<String> recentTopics;   // Recently discussed topics
        public final String conversationPhase;    // Opening, middle, endgame, etc.
        public final float timeIntensity;         // Time pressure factor
        
        public ConversationContext(String gameContext, String emotionalContext, 
                                 List<String> recentTopics, String phase, float timeIntensity) {
            this.gameContext = gameContext;
            this.emotionalContext = emotionalContext;
            this.recentTopics = recentTopics != null ? recentTopics : new ArrayList<>();
            this.conversationPhase = phase;
            this.timeIntensity = timeIntensity;
        }
    }
    
    public static class OptimalStrategyRecommendation {
        public final String strategy;
        public final float confidence;
        public final String reasoning;
        public final List<String> alternativeStrategies;
        public final Map<String, String> contextualAdaptations;
        
        public OptimalStrategyRecommendation(String strategy, float confidence, String reasoning,
                                           List<String> alternatives, Map<String, String> adaptations) {
            this.strategy = strategy;
            this.confidence = confidence;
            this.reasoning = reasoning;
            this.alternativeStrategies = alternatives != null ? alternatives : new ArrayList<>();
            this.contextualAdaptations = adaptations != null ? adaptations : new HashMap<>();
        }
    }
    
    public static class StrategyAdaptation {
        public final boolean shouldAdapt;
        public final String newStrategy;
        public final String reason;
        public final float confidence;
        
        public StrategyAdaptation(boolean shouldAdapt, String newStrategy, String reason, float confidence) {
            this.shouldAdapt = shouldAdapt;
            this.newStrategy = newStrategy;
            this.reason = reason;
            this.confidence = confidence;
        }
        
        public static StrategyAdaptation noChange() {
            return new StrategyAdaptation(false, null, "Strategy performing well", 1.0f);
        }
    }
    
    public static class ConversationOutcome {
        public final String finalStrategy;
        public final String gameContext;
        public final String conversationContext;
        public final boolean success;
        public final CrossMasterEffectivenessTracker.ConversationQualityMetrics qualityMetrics;
        
        public ConversationOutcome(String strategy, String gameCtx, String convCtx, boolean success,
                                 CrossMasterEffectivenessTracker.ConversationQualityMetrics quality) {
            this.finalStrategy = strategy;
            this.gameContext = gameCtx;
            this.conversationContext = convCtx;
            this.success = success;
            this.qualityMetrics = quality;
        }
    }
    
    public static class ComprehensiveStrategyInsights {
        public EmotionalStrategyLearner.StrategyAnalytics individualLearning;
        public CrossMasterEffectivenessTracker.CrossLearningRecommendations crossLearningOpportunities;
        public DynamicRelationshipEvolution.RelationshipEvolutionAnalysis relationshipEvolution;
        public DynamicRelationshipEvolution.RelationshipOptimizationSuggestions optimizationSuggestions;
        public CrossMasterEffectivenessTracker.ConversationTrendAnalysis performanceTrends;
    }
    
    // Private helper classes
    private static class StrategyScore {
        float individualScore = 0f;
        float crossLearningScore = 0f;
        float relationshipScore = 0f;
        float totalScore = 0f;
        String reasoning = "";
        
        void calculateTotal() {
            totalScore = (individualScore * LEARNING_WEIGHT) + 
                        (crossLearningScore * CROSS_LEARNING_WEIGHT) + 
                        (relationshipScore * RELATIONSHIP_WEIGHT);
        }
    }
    
    private static class ConversationMonitor {
        public final String conversationId;
        public final String master1;
        public final String master2;
        private final ConversationContext initialContext;
        
        private final List<StrategyQualityPair> recentInteractions = new ArrayList<>();
        private float rollingQualityAverage = 0.5f;
        private int interactionCount = 0;
        private boolean needsAdaptation = false;
        
        private static final int ADAPTATION_WINDOW = 5;
        private static final float POOR_QUALITY_THRESHOLD = 0.4f;
        
        public ConversationMonitor(String id, String m1, String m2, ConversationContext context) {
            this.conversationId = id;
            this.master1 = m1;
            this.master2 = m2;
            this.initialContext = context;
        }
        
        public void recordInteraction(String strategy, float quality) {
            recentInteractions.add(new StrategyQualityPair(strategy, quality));
            
            // Maintain window size
            if (recentInteractions.size() > ADAPTATION_WINDOW) {
                recentInteractions.remove(0);
            }
            
            // Update rolling average
            rollingQualityAverage = (rollingQualityAverage * interactionCount + quality) / (interactionCount + 1);
            interactionCount++;
            
            // Check if adaptation is needed
            checkAdaptationNeed();
        }
        
        public boolean shouldAdaptStrategy() {
            return needsAdaptation;
        }
        
        public String getAdaptationReason() {
            if (rollingQualityAverage < POOR_QUALITY_THRESHOLD) {
                return "Overall conversation quality below threshold";
            }
            
            if (recentInteractions.size() >= 3) {
                float recentAverage = recentInteractions.stream()
                        .map(pair -> pair.quality)
                        .reduce(0f, Float::sum) / recentInteractions.size();
                
                if (recentAverage < POOR_QUALITY_THRESHOLD) {
                    return "Recent interaction quality declining";
                }
            }
            
            return "Strategy optimization opportunity detected";
        }
        
        public ConversationContext getCurrentContext() {
            // Create updated context based on conversation progress
            return new ConversationContext(
                initialContext.gameContext,
                "adaptive", // Updated emotional context
                initialContext.recentTopics,
                "adaptation_phase",
                initialContext.timeIntensity
            );
        }
        
        private void checkAdaptationNeed() {
            needsAdaptation = false;
            
            // Check recent quality trend
            if (recentInteractions.size() >= 3) {
                float recentAverage = recentInteractions.stream()
                        .map(pair -> pair.quality)
                        .reduce(0f, Float::sum) / recentInteractions.size();
                
                if (recentAverage < POOR_QUALITY_THRESHOLD) {
                    needsAdaptation = true;
                }
            }
            
            // Check for declining trend
            if (recentInteractions.size() >= ADAPTATION_WINDOW) {
                float firstHalf = recentInteractions.subList(0, ADAPTATION_WINDOW / 2).stream()
                        .map(pair -> pair.quality)
                        .reduce(0f, Float::sum) / (ADAPTATION_WINDOW / 2);
                
                float secondHalf = recentInteractions.subList(ADAPTATION_WINDOW / 2, ADAPTATION_WINDOW).stream()
                        .map(pair -> pair.quality)
                        .reduce(0f, Float::sum) / (ADAPTATION_WINDOW - ADAPTATION_WINDOW / 2);
                
                if (firstHalf - secondHalf > 0.2f) { // Significant decline
                    needsAdaptation = true;
                }
            }
        }
        
        private static class StrategyQualityPair {
            final String strategy;
            final float quality;
            
            StrategyQualityPair(String strategy, float quality) {
                this.strategy = strategy;
                this.quality = quality;
            }
        }
    }
    
    // Private implementation methods
    private Map<String, StrategyScore> combineStrategyInsights(
           EmotionalStrategyLearner.StrategyRecommendation individualLearning,
            CrossMasterEffectivenessTracker.CrossLearningRecommendations crossLearning,
            DynamicRelationshipEvolution.RelationshipEmotionalPreferences relationshipPrefs,
            ConversationContext context) {
        
        Map<String, StrategyScore> scores = new HashMap<>();
        
        // Add individual learning scores
        if (individualLearning != null) {
            StrategyScore score = scores.computeIfAbsent(individualLearning.strategy, k -> new StrategyScore());
            score.individualScore = individualLearning.confidence;
            score.reasoning += "Individual learning: " + individualLearning.reasoning + "; ";
        }
        
        // Add cross-learning scores
        for (CrossMasterEffectivenessTracker.CrossLearningStrategy crossStrategy : crossLearning.recommendedStrategies) {
            StrategyScore score = scores.computeIfAbsent(crossStrategy.strategy, k -> new StrategyScore());
            score.crossLearningScore = crossStrategy.effectiveness * crossStrategy.adaptationConfidence;
            score.reasoning += "Cross-learning: from " + String.join(",", crossStrategy.exemplarMasters) + "; ";
        }
        
        // Add relationship preference scores
        for (Map.Entry<String, Float> entry : relationshipPrefs.preferredStrategies.entrySet()) {
            StrategyScore score = scores.computeIfAbsent(entry.getKey(), k -> new StrategyScore());
            score.relationshipScore = entry.getValue();
            score.reasoning += "Relationship preference: " + entry.getValue() + "; ";
        }
        
        // Calculate total scores
        scores.values().forEach(StrategyScore::calculateTotal);
        
        return scores;
    }
    
    private OptimalStrategyRecommendation selectOptimalStrategy(Map<String, StrategyScore> strategyScores, 
                                                              ConversationContext context) {
        
        // Find highest scoring strategy
        Map.Entry<String, StrategyScore> bestEntry = strategyScores.entrySet().stream()
                .max((a, b) -> Float.compare(a.getValue().totalScore, b.getValue().totalScore))
                .orElse(null);
        
        if (bestEntry == null) {
            // Fallback to personality-based default
            return new OptimalStrategyRecommendation("balanced_approach", 0.5f, 
                "Fallback to personality default", new ArrayList<>(), new HashMap<>());
        }
        
        String bestStrategy = bestEntry.getKey();
        StrategyScore bestScore = bestEntry.getValue();
        
        // Get alternatives
        List<String> alternatives = strategyScores.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(bestStrategy))
                .sorted((a, b) -> Float.compare(b.getValue().totalScore, a.getValue().totalScore))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return new OptimalStrategyRecommendation(
            bestStrategy,
            Math.min(bestScore.totalScore, 1.0f),
            bestScore.reasoning,
            alternatives,
            new HashMap<>()
        );
    }
    
    private OptimalStrategyRecommendation enhanceWithContextualAdaptations(
            OptimalStrategyRecommendation recommendation, String masterName, String opponentName, 
            ConversationContext context) {
        
        Map<String, String> adaptations = new HashMap<>(recommendation.contextualAdaptations);
        
        // Add time pressure adaptations
        if (context.timeIntensity > 0.7f) {
            adaptations.put("time_pressure", "Use more direct emotional approach due to time constraints");
        }
        
        // Add game phase adaptations
        if (context.conversationPhase.contains("endgame")) {
            adaptations.put("endgame_focus", "Emphasize calculation and precision over creativity");
        }
        
        // Add emotional context adaptations
        if (context.emotionalContext.contains("tense")) {
            adaptations.put("tension_management", "Use calming or diplomatic emotional approach");
        }
        
        return new OptimalStrategyRecommendation(
            recommendation.strategy,
            recommendation.confidence,
            recommendation.reasoning,
            recommendation.alternativeStrategies,
            adaptations
        );
    }
}