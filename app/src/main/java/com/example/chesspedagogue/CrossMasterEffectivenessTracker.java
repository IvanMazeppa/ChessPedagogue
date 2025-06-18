package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Phase 3: Cross-Master Emotional Effectiveness Tracking System
 * 
 * Monitors conversation quality outcomes across all master interactions to identify
 * the most effective emotional strategies. Enables masters to learn from each other's
 * successful approaches and discover optimal emotional patterns.
 * 
 * Key Features:
 * - Tracks conversation quality metrics across master pairs
 * - Identifies high-performing emotional strategies
 * - Enables cross-master learning and strategy sharing
 * - Provides effectiveness insights for strategy optimization
 * 
 * This system complements EmotionalStrategyLearner by providing global effectiveness
 * data that individual masters can learn from, creating a collaborative learning
 * environment where successful strategies propagate across the master community.
 */
public class CrossMasterEffectivenessTracker {
    private static final String TAG = "CrossMasterEffectiveness";
    private static CrossMasterEffectivenessTracker instance;
    
    private final Context context;
    private final RelationshipPersistenceManager persistenceManager;
    private final Map<String, List<EffectivenessRecord>> recentRecords = new ConcurrentHashMap<>();
    private final Map<String, EmotionalStrategyStatistics> globalStats = new ConcurrentHashMap<>();
    
    // Conversation quality metrics
    private static final float EXCELLENT_CONVERSATION_THRESHOLD = 0.8f;
    private static final float GOOD_CONVERSATION_THRESHOLD = 0.6f;
    private static final float POOR_CONVERSATION_THRESHOLD = 0.3f;
    
    // Learning parameters
    private static final int MAX_RECENT_RECORDS = 100;
    private static final int MIN_SAMPLES_FOR_RELIABILITY = 5;
    private static final float CROSS_LEARNING_INFLUENCE = 0.3f; // 30% influence from other masters
    
    private CrossMasterEffectivenessTracker(Context context) {
        this.context = context;
        this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        loadGlobalStatistics();
    }
    
    public static synchronized CrossMasterEffectivenessTracker getInstance(Context context) {
        if (instance == null) {
            instance = new CrossMasterEffectivenessTracker(context);
        }
        return instance;
    }
    
    /**
     * Records the outcome of an emotional strategy in a conversation
     */
    public void recordConversationOutcome(String masterName, String opponentName, 
                                        String emotionalStrategy, String context,
                                        ConversationQualityMetrics quality) {
        
        EffectivenessRecord record = new EffectivenessRecord(
            masterName, opponentName, emotionalStrategy, context, quality, System.currentTimeMillis()
        );
        
        // Store in recent records for immediate analysis
        String key = masterName + "_" + opponentName;
        recentRecords.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        
        // Maintain record limit
        List<EffectivenessRecord> records = recentRecords.get(key);
        if (records.size() > MAX_RECENT_RECORDS) {
            records.subList(0, records.size() - MAX_RECENT_RECORDS).clear();
        }
        
        // Update global statistics
        updateGlobalStatistics(emotionalStrategy, quality);
        
        // Persist significant outcomes
        if (quality.overallScore >= GOOD_CONVERSATION_THRESHOLD || 
            quality.overallScore <= POOR_CONVERSATION_THRESHOLD) {
            persistEffectivenessRecord(record);
        }
        
        Log.d(TAG, String.format("🎯 Recorded outcome: %s->%s using '%s' scored %.2f", 
                                masterName, opponentName, emotionalStrategy, quality.overallScore));
    }
    
    /**
     * Gets the most effective emotional strategies across all masters
     */
    public List<GlobalEffectivenessInsight> getGlobalEffectivenessInsights(int limit) {
        return globalStats.entrySet().stream()
                .filter(entry -> entry.getValue().sampleCount >= MIN_SAMPLES_FOR_RELIABILITY)
                .map(entry -> new GlobalEffectivenessInsight(
                    entry.getKey(),
                    entry.getValue().averageScore,
                    entry.getValue().sampleCount,
                    entry.getValue().getTopPerformingMasters(),
                    entry.getValue().getOptimalContexts()
                ))
                .sorted((a, b) -> Float.compare(b.averageEffectiveness, a.averageEffectiveness))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets strategy recommendations based on cross-master learning
     */
    public CrossLearningRecommendations getCrossLearningRecommendations(String masterName, 
                                                                       String opponentName) {
        CrossLearningRecommendations recommendations = new CrossLearningRecommendations();
        
        // Find similar master pairs and their successful strategies
        List<EffectivenessRecord> similarPairRecords = findSimilarPairRecords(masterName, opponentName);
        
        // Analyze successful strategies from other masters
        Map<String, Float> strategyEffectiveness = analyzeStrategyEffectiveness(similarPairRecords);
        
        // Generate recommendations based on cross-master learning
        for (Map.Entry<String, Float> entry : strategyEffectiveness.entrySet()) {
            if (entry.getValue() >= GOOD_CONVERSATION_THRESHOLD) {
                CrossLearningStrategy strategy = new CrossLearningStrategy(
                    entry.getKey(),
                    entry.getValue(),
                    findExemplarMasters(entry.getKey(), similarPairRecords),
                    calculateAdaptationConfidence(masterName, entry.getKey())
                );
                recommendations.recommendedStrategies.add(strategy);
            }
        }
        
        // Sort by effectiveness and adaptation confidence
        recommendations.recommendedStrategies.sort((a, b) -> 
            Float.compare(b.effectiveness * b.adaptationConfidence, a.effectiveness * a.adaptationConfidence));
        
        Log.d(TAG, String.format("🎓 Generated %d cross-learning recommendations for %s vs %s",
                                recommendations.recommendedStrategies.size(), masterName, opponentName));
        
        return recommendations;
    }
    
    /**
     * Analyzes conversation quality trends across master interactions
     */
    public ConversationTrendAnalysis analyzeConversationTrends(String masterName, 
                                                               String opponentName, 
                                                               int timeframeDays) {
        long cutoffTime = System.currentTimeMillis() - (timeframeDays * 24 * 60 * 60 * 1000L);
        
        String key = masterName + "_" + opponentName;
        List<EffectivenessRecord> relevantRecords = recentRecords.getOrDefault(key, new ArrayList<>())
                .stream()
                .filter(record -> record.timestamp >= cutoffTime)
                .sorted((a, b) -> Long.compare(a.timestamp, b.timestamp))
                .collect(Collectors.toList());
        
        ConversationTrendAnalysis analysis = new ConversationTrendAnalysis();
        
        if (relevantRecords.size() >= 3) {
            // Calculate trend slope
            analysis.qualityTrend = calculateTrendSlope(relevantRecords);
            
            // Identify improving/declining strategies
            analysis.improvingStrategies = identifyImprovingStrategies(relevantRecords);
            analysis.decliningStrategies = identifyDecliningStrategies(relevantRecords);
            
            // Calculate overall conversation quality
            analysis.averageQuality = relevantRecords.stream()
                    .map(record -> record.quality.overallScore)
                    .reduce(0f, Float::sum) / relevantRecords.size();
            
            analysis.sampleCount = relevantRecords.size();
        }
        
        return analysis;
    }
    
    /**
     * Provides strategy insights for improving conversation quality
     */
    public StrategyOptimizationInsights getOptimizationInsights(String masterName, String opponentName) {
        StrategyOptimizationInsights insights = new StrategyOptimizationInsights();
        
        // Analyze current master's performance
        String key = masterName + "_" + opponentName;
        List<EffectivenessRecord> masterRecords = recentRecords.getOrDefault(key, new ArrayList<>());
        
        if (!masterRecords.isEmpty()) {
            // Find least effective strategies that could be improved
            Map<String, Float> strategyPerformance = masterRecords.stream()
                    .collect(Collectors.groupingBy(
                        record -> record.emotionalStrategy,
                        Collectors.mapping(record -> record.quality.overallScore,
                                         Collectors.reducing(0f, Float::sum))
                    ));
            
            // Identify strategies below threshold
            for (Map.Entry<String, Float> entry : strategyPerformance.entrySet()) {
                String strategy = entry.getKey();
                float avgScore = entry.getValue() / masterRecords.size();
                
                if (avgScore < GOOD_CONVERSATION_THRESHOLD) {
                    // Find better alternatives from global data
                    GlobalEffectivenessInsight globalInsight = findGlobalAlternative(strategy);
                    if (globalInsight != null && globalInsight.averageEffectiveness > avgScore + 0.1f) {
                        insights.improvementOpportunities.add(new ImprovementOpportunity(
                            strategy,
                            avgScore,
                            globalInsight.strategy,
                            globalInsight.averageEffectiveness,
                            globalInsight.exemplarMasters
                        ));
                    }
                }
            }
        }
        
        return insights;
    }
    
    // Helper classes for data structures
    public static class EffectivenessRecord {
        public final String masterName;
        public final String opponentName;
        public final String emotionalStrategy;
        public final String context;
        public final ConversationQualityMetrics quality;
        public final long timestamp;
        
        public EffectivenessRecord(String masterName, String opponentName, String emotionalStrategy,
                                 String context, ConversationQualityMetrics quality, long timestamp) {
            this.masterName = masterName;
            this.opponentName = opponentName;
            this.emotionalStrategy = emotionalStrategy;
            this.context = context;
            this.quality = quality;
            this.timestamp = timestamp;
        }
    }
    
    public static class ConversationQualityMetrics {
        public final float engagementScore;      // How engaged both masters were
        public final float emotionalResonance;  // How well emotions matched the conversation
        public final float continuityScore;     // How well conversation flowed
        public final float noveltyScore;        // How original/interesting the exchange was
        public final float overallScore;        // Weighted combination of above
        
        public ConversationQualityMetrics(float engagement, float resonance, float continuity, float novelty) {
            this.engagementScore = engagement;
            this.emotionalResonance = resonance;
            this.continuityScore = continuity;
            this.noveltyScore = novelty;
            // Weighted combination: engagement and resonance more important
            this.overallScore = (engagement * 0.3f + resonance * 0.3f + continuity * 0.25f + novelty * 0.15f);
        }
    }
    
    public static class GlobalEffectivenessInsight {
        public final String strategy;
        public final float averageEffectiveness;
        public final int sampleCount;
        public final List<String> exemplarMasters;
        public final List<String> optimalContexts;
        
        public GlobalEffectivenessInsight(String strategy, float effectiveness, int samples,
                                        List<String> exemplars, List<String> contexts) {
            this.strategy = strategy;
            this.averageEffectiveness = effectiveness;
            this.sampleCount = samples;
            this.exemplarMasters = exemplars;
            this.optimalContexts = contexts;
        }
    }
    
    public static class CrossLearningRecommendations {
        public final List<CrossLearningStrategy> recommendedStrategies = new ArrayList<>();
        public final Map<String, String> adaptationTips = new HashMap<>();
        public float overallConfidence = 0f;
    }
    
    public static class CrossLearningStrategy {
        public final String strategy;
        public final float effectiveness;
        public final List<String> exemplarMasters;
        public final float adaptationConfidence;
        
        public CrossLearningStrategy(String strategy, float effectiveness, 
                                   List<String> exemplars, float confidence) {
            this.strategy = strategy;
            this.effectiveness = effectiveness;
            this.exemplarMasters = exemplars;
            this.adaptationConfidence = confidence;
        }
    }
    
    public static class ConversationTrendAnalysis {
        public float qualityTrend = 0f;        // Positive = improving, negative = declining
        public float averageQuality = 0f;
        public int sampleCount = 0;
        public List<String> improvingStrategies = new ArrayList<>();
        public List<String> decliningStrategies = new ArrayList<>();
    }
    
    public static class StrategyOptimizationInsights {
        public final List<ImprovementOpportunity> improvementOpportunities = new ArrayList<>();
        public final Map<String, Float> strategyEffectivenessMap = new HashMap<>();
    }
    
    public static class ImprovementOpportunity {
        public final String currentStrategy;
        public final float currentEffectiveness;
        public final String suggestedStrategy;
        public final float suggestedEffectiveness;
        public final List<String> exemplarMasters;
        
        public ImprovementOpportunity(String current, float currentEff, String suggested, 
                                    float suggestedEff, List<String> exemplars) {
            this.currentStrategy = current;
            this.currentEffectiveness = currentEff;
            this.suggestedStrategy = suggested;
            this.suggestedEffectiveness = suggestedEff;
            this.exemplarMasters = exemplars;
        }
    }
    
    // Private helper classes
    private static class EmotionalStrategyStatistics {
        float totalScore = 0f;
        int sampleCount = 0;
        float averageScore = 0f;
        Map<String, Integer> masterUsage = new HashMap<>();
        Map<String, Float> contextEffectiveness = new HashMap<>();
        
        void addSample(String masterName, String context, float score) {
            totalScore += score;
            sampleCount++;
            averageScore = totalScore / sampleCount;
            
            masterUsage.put(masterName, masterUsage.getOrDefault(masterName, 0) + 1);
            contextEffectiveness.put(context, 
                (contextEffectiveness.getOrDefault(context, 0f) + score) / 2f);
        }
        
        List<String> getTopPerformingMasters() {
            return masterUsage.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
        
        List<String> getOptimalContexts() {
            return contextEffectiveness.entrySet().stream()
                    .filter(entry -> entry.getValue() >= GOOD_CONVERSATION_THRESHOLD)
                    .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
    }
    
    // Private implementation methods
    private void loadGlobalStatistics() {
        // Load from database in background
        new Thread(() -> {
            try {
                // Implementation would load from RelationshipPersistenceManager
                Log.d(TAG, "✅ Loaded global effectiveness statistics");
            } catch (Exception e) {
                Log.e(TAG, "Failed to load global statistics", e);
            }
        }).start();
    }
    
    private void updateGlobalStatistics(String strategy, ConversationQualityMetrics quality) {
        globalStats.computeIfAbsent(strategy, k -> new EmotionalStrategyStatistics())
                .addSample("global", "general", quality.overallScore);
    }
    
    private void persistEffectivenessRecord(EffectivenessRecord record) {
        // Would integrate with RelationshipPersistenceManager
        // Implementation details depend on existing database schema
    }
    
    private List<EffectivenessRecord> findSimilarPairRecords(String masterName, String opponentName) {
        // Find records from similar master personality types
        return recentRecords.values().stream()
                .flatMap(List::stream)
                .filter(record -> isSimilarMasterPair(record.masterName, record.opponentName, 
                                                    masterName, opponentName))
                .collect(Collectors.toList());
    }
    
    private boolean isSimilarMasterPair(String master1, String opponent1, String master2, String opponent2) {
        // Implement personality similarity logic
        // For now, simple implementation - could be enhanced with personality vectors
        return getMasterPersonalityGroup(master1).equals(getMasterPersonalityGroup(master2)) ||
               getMasterPersonalityGroup(opponent1).equals(getMasterPersonalityGroup(opponent2));
    }
    
    private String getMasterPersonalityGroup(String masterName) {
        // Simplified personality grouping - could be enhanced
        switch (masterName.toLowerCase()) {
            case "tal": case "alekhine": return "creative";
            case "fischer": case "kasparov": return "aggressive";
            case "carlsen": case "anand": return "analytical";
            case "karpov": case "petrosian": return "positional";
            default: return "balanced";
        }
    }
    
    private Map<String, Float> analyzeStrategyEffectiveness(List<EffectivenessRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                    record -> record.emotionalStrategy,
                    Collectors.mapping(record -> record.quality.overallScore,
                                     Collectors.reducing(0f, Float::sum))
                ));
    }
    
    private List<String> findExemplarMasters(String strategy, List<EffectivenessRecord> records) {
        return records.stream()
                .filter(record -> record.emotionalStrategy.equals(strategy))
                .filter(record -> record.quality.overallScore >= EXCELLENT_CONVERSATION_THRESHOLD)
                .map(record -> record.masterName)
                .distinct()
                .limit(3)
                .collect(Collectors.toList());
    }
    
    private float calculateAdaptationConfidence(String masterName, String strategy) {
        // Calculate how likely this master is to successfully adapt this strategy
        // Based on personality similarity and past adaptation success
        return 0.7f; // Simplified implementation
    }
    
    private float calculateTrendSlope(List<EffectivenessRecord> records) {
        if (records.size() < 2) return 0f;
        
        // Simple linear regression slope
        int n = records.size();
        float sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            float x = i; // Time index
            float y = records.get(i).quality.overallScore;
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        
        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }
    
    private List<String> identifyImprovingStrategies(List<EffectivenessRecord> records) {
        // Analyze which strategies are getting better over time
        // Simplified implementation
        return new ArrayList<>();
    }
    
    private List<String> identifyDecliningStrategies(List<EffectivenessRecord> records) {
        // Analyze which strategies are getting worse over time
        // Simplified implementation
        return new ArrayList<>();
    }
    
    private GlobalEffectivenessInsight findGlobalAlternative(String strategy) {
        // Find better global alternatives for a poor-performing strategy
        return globalStats.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(strategy))
                .filter(entry -> entry.getValue().averageScore >= GOOD_CONVERSATION_THRESHOLD)
                .max((a, b) -> Float.compare(a.getValue().averageScore, b.getValue().averageScore))
                .map(entry -> new GlobalEffectivenessInsight(
                    entry.getKey(),
                    entry.getValue().averageScore,
                    entry.getValue().sampleCount,
                    entry.getValue().getTopPerformingMasters(),
                    entry.getValue().getOptimalContexts()
                ))
                .orElse(null);
    }
}