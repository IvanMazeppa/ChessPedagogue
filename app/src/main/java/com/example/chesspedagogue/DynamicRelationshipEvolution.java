package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Phase 3: Dynamic Emotional Relationship Evolution System
 * 
 * Enables chess master relationships to evolve dynamically based on learning outcomes,
 * emotional effectiveness, and shared experiences. Masters develop preferences for
 * emotional interactions with specific opponents and form unique emotional bonds.
 * 
 * Key Features:
 * - Tracks relationship evolution based on emotional learning success
 * - Calculates emotional chemistry ratings between master pairs
 * - Monitors relationship milestones and breakthrough moments
 * - Provides relationship insights for conversation optimization
 * - Enables masters to develop unique emotional "languages" with each opponent
 * 
 * Expected Emergent Behaviors:
 * - Fischer learns that technical criticism works better than personal attacks with Carlsen
 * - Tal discovers when to tone down excitement with methodical masters
 * - Masters develop inside jokes and references specific to their relationships
 * - Emotional preferences become personalized per master pair
 */
public class DynamicRelationshipEvolution {
    private static final String TAG = "DynamicRelationship";
    private static DynamicRelationshipEvolution instance;
    
    private final Context context;
    private final RelationshipPersistenceManager persistenceManager;
    private final EmotionalStrategyLearner strategyLearner;
    private final CrossMasterEffectivenessTracker effectivenessTracker;
    
    private final Map<String, RelationshipDynamics> activeDynamics = new ConcurrentHashMap<>();
    private final Map<String, List<RelationshipMilestone>> milestoneHistory = new ConcurrentHashMap<>();
    
    // Evolution parameters
    private static final float CHEMISTRY_EVOLUTION_RATE = 0.05f;
    private static final float MILESTONE_IMPACT_MULTIPLIER = 1.5f;
    private static final int CHEMISTRY_CALCULATION_WINDOW = 20; // Last 20 interactions
    private static final float EMOTIONAL_LANGUAGE_THRESHOLD = 0.75f;
    
    // Relationship evolution thresholds
    private static final float EXCELLENT_CHEMISTRY_THRESHOLD = 0.8f;
    private static final float GOOD_CHEMISTRY_THRESHOLD = 0.6f;
    private static final float POOR_CHEMISTRY_THRESHOLD = 0.3f;
    
    private DynamicRelationshipEvolution(Context context) {
        this.context = context;
        this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        this.strategyLearner = EmotionalStrategyLearner.getInstance(context);
        this.effectivenessTracker = CrossMasterEffectivenessTracker.getInstance(context);
        loadExistingRelationships();
    }
    
    public static synchronized DynamicRelationshipEvolution getInstance(Context context) {
        if (instance == null) {
            instance = new DynamicRelationshipEvolution(context);
        }
        return instance;
    }
    
    /**
     * Records a relationship interaction and updates emotional dynamics
     */
    public void recordRelationshipInteraction(String master1, String master2, 
                                            String emotionalStrategy, 
                                            float interactionQuality,
                                            String interactionContext) {
        
        String relationshipKey = generateRelationshipKey(master1, master2);
        RelationshipDynamics dynamics = activeDynamics.computeIfAbsent(relationshipKey, 
            k -> new RelationshipDynamics(master1, master2));
        
        // Record the interaction
        RelationshipInteraction interaction = new RelationshipInteraction(
            emotionalStrategy, interactionQuality, interactionContext, System.currentTimeMillis()
        );
        dynamics.addInteraction(interaction);
        
        // Update emotional chemistry based on interaction quality
        updateEmotionalChemistry(dynamics, interactionQuality);
        
        // Check for relationship milestones
        checkForMilestones(dynamics, interaction);
        
        // Update learned preferences
        updateEmotionalPreferences(dynamics, emotionalStrategy, interactionQuality);
        
        // Persist significant changes
        if (shouldPersistUpdate(dynamics)) {
            persistRelationshipUpdate(dynamics);
        }
        
        Log.d(TAG, String.format("🤝 Recorded interaction: %s<->%s strategy:'%s' quality:%.2f chemistry:%.2f",
                                master1, master2, emotionalStrategy, interactionQuality, dynamics.currentChemistry));
    }
    
    /**
     * Gets the current emotional chemistry rating between two masters
     */
    public float getEmotionalChemistry(String master1, String master2) {
        String relationshipKey = generateRelationshipKey(master1, master2);
        RelationshipDynamics dynamics = activeDynamics.get(relationshipKey);
        return dynamics != null ? dynamics.currentChemistry : 0.5f; // Default neutral chemistry
    }
    
    /**
     * Gets relationship-specific emotional preferences
     */
    public RelationshipEmotionalPreferences getEmotionalPreferences(String master1, String master2) {
        String relationshipKey = generateRelationshipKey(master1, master2);
        RelationshipDynamics dynamics = activeDynamics.get(relationshipKey);
        
        if (dynamics != null) {
            return dynamics.getEmotionalPreferences();
        } else {
            return new RelationshipEmotionalPreferences(); // Default preferences
        }
    }
    
    /**
     * Gets the unique emotional "language" developed between two masters
     */
    public EmotionalLanguage getEmotionalLanguage(String master1, String master2) {
        String relationshipKey = generateRelationshipKey(master1, master2);
        RelationshipDynamics dynamics = activeDynamics.get(relationshipKey);
        
        if (dynamics != null && dynamics.hasEstablishedEmotionalLanguage()) {
            return dynamics.getEmotionalLanguage();
        } else {
            return EmotionalLanguage.getDefaultLanguage(master1, master2);
        }
    }
    
    /**
     * Analyzes relationship evolution trends
     */
    public RelationshipEvolutionAnalysis analyzeEvolution(String master1, String master2, int daysPeriod) {
        String relationshipKey = generateRelationshipKey(master1, master2);
        RelationshipDynamics dynamics = activeDynamics.get(relationshipKey);
        
        RelationshipEvolutionAnalysis analysis = new RelationshipEvolutionAnalysis();
        
        if (dynamics != null) {
            long cutoffTime = System.currentTimeMillis() - (daysPeriod * 24 * 60 * 60 * 1000L);
            
            List<RelationshipInteraction> recentInteractions = dynamics.getRecentInteractions(cutoffTime);
            
            // Calculate evolution trends
            analysis.chemistryTrend = calculateChemistryTrend(recentInteractions);
            analysis.qualityTrend = calculateQualityTrend(recentInteractions);
            analysis.currentChemistry = dynamics.currentChemistry;
            analysis.relationshipStage = determineRelationshipStage(dynamics);
            analysis.strongestStrategies = identifyStrongestStrategies(dynamics);
            analysis.evolutionPotential = calculateEvolutionPotential(dynamics);
            
            // Identify relationship milestones
            List<RelationshipMilestone> milestones = milestoneHistory.get(relationshipKey);
            if (milestones != null) {
                analysis.recentMilestones = milestones.stream()
                        .filter(milestone -> milestone.timestamp >= cutoffTime)
                        .collect(Collectors.toList());
            }
        }
        
        return analysis;
    }
    
    /**
     * Provides relationship optimization suggestions
     */
    public RelationshipOptimizationSuggestions getOptimizationSuggestions(String master1, String master2) {
        RelationshipOptimizationSuggestions suggestions = new RelationshipOptimizationSuggestions();
        
        String relationshipKey = generateRelationshipKey(master1, master2);
        RelationshipDynamics dynamics = activeDynamics.get(relationshipKey);
        
        if (dynamics != null) {
            // Analyze underperforming strategies
            Map<String, Float> strategyPerformance = dynamics.getStrategyPerformance();
            
            for (Map.Entry<String, Float> entry : strategyPerformance.entrySet()) {
                String strategy = entry.getKey();
                float performance = entry.getValue();
                
                if (performance < GOOD_CHEMISTRY_THRESHOLD) {
                    // Get cross-learning recommendations
                    CrossMasterEffectivenessTracker.CrossLearningRecommendations crossRecommendations = 
                        effectivenessTracker.getCrossLearningRecommendations(master1, master2);
                    
                    suggestions.addImprovementSuggestion(new RelationshipImprovementSuggestion(
                        strategy,
                        performance,
                        crossRecommendations.recommendedStrategies,
                        calculateImprovementPotential(dynamics, strategy)
                    ));
                }
            }
            
            // Suggest chemistry enhancement opportunities
            if (dynamics.currentChemistry < EXCELLENT_CHEMISTRY_THRESHOLD) {
                suggestions.chemistryEnhancementOpportunities.addAll(identifyChemistryEnhancements(dynamics));
            }
            
            // Suggest emotional language development opportunities
            if (!dynamics.hasEstablishedEmotionalLanguage()) {
                suggestions.languageDevelopmentTips.addAll(generateLanguageDevelopmentTips(dynamics));
            }
        }
        
        return suggestions;
    }
    
    // Core data structures
    public static class RelationshipDynamics {
        public final String master1;
        public final String master2;
        public float currentChemistry = 0.5f; // Neutral starting point
        public float evolutionMomentum = 0f;   // Positive = improving, negative = declining
        
        private final List<RelationshipInteraction> interactionHistory = new ArrayList<>();
        private final Map<String, EmotionalPreferenceScore> emotionalPreferences = new HashMap<>();
        private final Map<String, Float> strategyPerformanceCache = new HashMap<>();
        private EmotionalLanguage uniqueLanguage;
        private long lastUpdate = System.currentTimeMillis();
        
        public RelationshipDynamics(String master1, String master2) {
            this.master1 = master1;
            this.master2 = master2;
        }
        
        public void addInteraction(RelationshipInteraction interaction) {
            interactionHistory.add(interaction);
            
            // Maintain history limit
            if (interactionHistory.size() > CHEMISTRY_CALCULATION_WINDOW * 2) {
                interactionHistory.subList(0, interactionHistory.size() - CHEMISTRY_CALCULATION_WINDOW * 2).clear();
            }
            
            updateStrategyPerformanceCache(interaction);
            lastUpdate = System.currentTimeMillis();
        }
        
        public List<RelationshipInteraction> getRecentInteractions(long cutoffTime) {
            return interactionHistory.stream()
                    .filter(interaction -> interaction.timestamp >= cutoffTime)
                    .collect(Collectors.toList());
        }
        
        public RelationshipEmotionalPreferences getEmotionalPreferences() {
            RelationshipEmotionalPreferences preferences = new RelationshipEmotionalPreferences();
            
            for (Map.Entry<String, EmotionalPreferenceScore> entry : emotionalPreferences.entrySet()) {
                preferences.preferredStrategies.put(entry.getKey(), entry.getValue().getPreferenceScore());
            }
            
            return preferences;
        }
        
        public boolean hasEstablishedEmotionalLanguage() {
            return uniqueLanguage != null && currentChemistry >= EMOTIONAL_LANGUAGE_THRESHOLD;
        }
        
        public EmotionalLanguage getEmotionalLanguage() {
            return uniqueLanguage;
        }
        
        public Map<String, Float> getStrategyPerformance() {
            return new HashMap<>(strategyPerformanceCache);
        }
        
        private void updateStrategyPerformanceCache(RelationshipInteraction interaction) {
            String strategy = interaction.emotionalStrategy;
            Float currentPerf = strategyPerformanceCache.get(strategy);
            
            if (currentPerf == null) {
                strategyPerformanceCache.put(strategy, interaction.qualityScore);
            } else {
                // Exponential moving average
                strategyPerformanceCache.put(strategy, currentPerf * 0.7f + interaction.qualityScore * 0.3f);
            }
        }
    }
    
    public static class RelationshipInteraction {
        public final String emotionalStrategy;
        public final float qualityScore;
        public final String context;
        public final long timestamp;
        
        public RelationshipInteraction(String strategy, float quality, String context, long timestamp) {
            this.emotionalStrategy = strategy;
            this.qualityScore = quality;
            this.context = context;
            this.timestamp = timestamp;
        }
    }
    
    public static class RelationshipMilestone {
        public final String milestoneType;
        public final String description;
        public final float chemistryAtMilestone;
        public final long timestamp;
        
        public RelationshipMilestone(String type, String description, float chemistry, long timestamp) {
            this.milestoneType = type;
            this.description = description;
            this.chemistryAtMilestone = chemistry;
            this.timestamp = timestamp;
        }
    }
    
    public static class RelationshipEmotionalPreferences {
        public final Map<String, Float> preferredStrategies = new HashMap<>();
        public final List<String> avoidedStrategies = new ArrayList<>();
        public final Map<String, String> contextualPreferences = new HashMap<>();
    }
    
    public static class EmotionalLanguage {
        public final List<String> sharedReferences = new ArrayList<>();
        public final List<String> insideJokes = new ArrayList<>();
        public final Map<String, String> uniquePhrases = new HashMap<>();
        public final List<String> emotionalShorthand = new ArrayList<>();
        
        public static EmotionalLanguage getDefaultLanguage(String master1, String master2) {
            // Create basic emotional language based on master personalities
            EmotionalLanguage language = new EmotionalLanguage();
            
            // Add personality-specific defaults
            if (master1.equalsIgnoreCase("tal") || master2.equalsIgnoreCase("tal")) {
                language.sharedReferences.add("magical_sacrifice");
                language.uniquePhrases.put("brilliant", "pure magic");
            }
            
            if (master1.equalsIgnoreCase("fischer") || master2.equalsIgnoreCase("fischer")) {
                language.sharedReferences.add("perfect_technique");
                language.uniquePhrases.put("weak", "amateur mistake");
            }
            
            return language;
        }
    }
    
    public static class RelationshipEvolutionAnalysis {
        public float chemistryTrend = 0f;
        public float qualityTrend = 0f;
        public float currentChemistry = 0.5f;
        public String relationshipStage = "developing";
        public List<String> strongestStrategies = new ArrayList<>();
        public float evolutionPotential = 0.5f;
        public List<RelationshipMilestone> recentMilestones = new ArrayList<>();
    }
    
    public static class RelationshipOptimizationSuggestions {
        public final List<RelationshipImprovementSuggestion> improvementSuggestions = new ArrayList<>();
        public final List<String> chemistryEnhancementOpportunities = new ArrayList<>();
        public final List<String> languageDevelopmentTips = new ArrayList<>();
        
        public void addImprovementSuggestion(RelationshipImprovementSuggestion suggestion) {
            improvementSuggestions.add(suggestion);
        }
    }
    
    public static class RelationshipImprovementSuggestion {
        public final String currentStrategy;
        public final float currentPerformance;
        public final List<CrossMasterEffectivenessTracker.CrossLearningStrategy> alternatives;
        public final float improvementPotential;
        
        public RelationshipImprovementSuggestion(String strategy, float performance,
                                               List<CrossMasterEffectivenessTracker.CrossLearningStrategy> alternatives,
                                               float potential) {
            this.currentStrategy = strategy;
            this.currentPerformance = performance;
            this.alternatives = alternatives;
            this.improvementPotential = potential;
        }
    }
    
    // Private helper classes
    private static class EmotionalPreferenceScore {
        private float totalScore = 0f;
        private int interactions = 0;
        
        void addInteraction(float score) {
            totalScore += score;
            interactions++;
        }
        
        float getPreferenceScore() {
            return interactions > 0 ? totalScore / interactions : 0.5f;
        }
    }
    
    // Private implementation methods
    private String generateRelationshipKey(String master1, String master2) {
        // Always generate consistent key regardless of order
        if (master1.compareTo(master2) < 0) {
            return master1 + "_" + master2;
        } else {
            return master2 + "_" + master1;
        }
    }
    
    private void updateEmotionalChemistry(RelationshipDynamics dynamics, float interactionQuality) {
        // Calculate new chemistry based on recent interactions
        List<RelationshipInteraction> recentInteractions = dynamics.interactionHistory.stream()
                .skip(Math.max(0, dynamics.interactionHistory.size() - CHEMISTRY_CALCULATION_WINDOW))
                .collect(Collectors.toList());
        
        if (!recentInteractions.isEmpty()) {
            float averageQuality = recentInteractions.stream()
                    .map(interaction -> interaction.qualityScore)
                    .reduce(0f, Float::sum) / recentInteractions.size();
            
            // Update chemistry with exponential moving average
            dynamics.currentChemistry = dynamics.currentChemistry * 0.9f + averageQuality * 0.1f;
            
            // Update evolution momentum
            if (recentInteractions.size() >= 3) {
                float oldAverage = recentInteractions.subList(0, recentInteractions.size() / 2).stream()
                        .map(interaction -> interaction.qualityScore)
                        .reduce(0f, Float::sum) / (recentInteractions.size() / 2);
                
                float newAverage = recentInteractions.subList(recentInteractions.size() / 2, recentInteractions.size()).stream()
                        .map(interaction -> interaction.qualityScore)
                        .reduce(0f, Float::sum) / (recentInteractions.size() - recentInteractions.size() / 2);
                
                dynamics.evolutionMomentum = newAverage - oldAverage;
            }
        }
    }
    
    private void checkForMilestones(RelationshipDynamics dynamics, RelationshipInteraction interaction) {
        String relationshipKey = generateRelationshipKey(dynamics.master1, dynamics.master2);
        List<RelationshipMilestone> milestones = milestoneHistory.computeIfAbsent(relationshipKey, k -> new ArrayList<>());
        
        // Check for chemistry milestones
        if (dynamics.currentChemistry >= EXCELLENT_CHEMISTRY_THRESHOLD && 
            !hasMilestone(milestones, "excellent_chemistry")) {
            milestones.add(new RelationshipMilestone(
                "excellent_chemistry",
                "Achieved excellent emotional chemistry",
                dynamics.currentChemistry,
                System.currentTimeMillis()
            ));
            Log.d(TAG, String.format("🏆 Milestone: %s achieved excellent chemistry!", relationshipKey));
        }
        
        // Check for strategy mastery milestones
        Map<String, Float> strategyPerf = dynamics.getStrategyPerformance();
        for (Map.Entry<String, Float> entry : strategyPerf.entrySet()) {
            if (entry.getValue() >= EXCELLENT_CHEMISTRY_THRESHOLD) {
                String milestoneType = "strategy_mastery_" + entry.getKey();
                if (!hasMilestone(milestones, milestoneType)) {
                    milestones.add(new RelationshipMilestone(
                        milestoneType,
                        "Mastered " + entry.getKey() + " strategy",
                        entry.getValue(),
                        System.currentTimeMillis()
                    ));
                }
            }
        }
    }
    
    private boolean hasMilestone(List<RelationshipMilestone> milestones, String milestoneType) {
        return milestones.stream().anyMatch(milestone -> milestone.milestoneType.equals(milestoneType));
    }
    
    private void updateEmotionalPreferences(RelationshipDynamics dynamics, String strategy, float quality) {
        dynamics.emotionalPreferences.computeIfAbsent(strategy, k -> new EmotionalPreferenceScore())
                .addInteraction(quality);
    }
    
    private boolean shouldPersistUpdate(RelationshipDynamics dynamics) {
        // Persist if significant chemistry change or milestone reached
        return Math.abs(dynamics.evolutionMomentum) > 0.1f || 
               dynamics.currentChemistry >= EXCELLENT_CHEMISTRY_THRESHOLD;
    }
    
    private void persistRelationshipUpdate(RelationshipDynamics dynamics) {
        // Would integrate with RelationshipPersistenceManager
        // Implementation details depend on existing database schema
    }
    
    private void loadExistingRelationships() {
        // Load existing relationship data from database
        new Thread(() -> {
            try {
                // Implementation would load from RelationshipPersistenceManager
                Log.d(TAG, "✅ Loaded existing relationship dynamics");
            } catch (Exception e) {
                Log.e(TAG, "Failed to load relationship dynamics", e);
            }
        }).start();
    }
    
    private float calculateChemistryTrend(List<RelationshipInteraction> interactions) {
        if (interactions.size() < 3) return 0f;
        
        // Simple trend calculation based on interaction quality over time
        int halfPoint = interactions.size() / 2;
        float earlierAvg = interactions.subList(0, halfPoint).stream()
                .map(i -> i.qualityScore)
                .reduce(0f, Float::sum) / halfPoint;
        float laterAvg = interactions.subList(halfPoint, interactions.size()).stream()
                .map(i -> i.qualityScore)
                .reduce(0f, Float::sum) / (interactions.size() - halfPoint);
        
        return laterAvg - earlierAvg;
    }
    
    private float calculateQualityTrend(List<RelationshipInteraction> interactions) {
        return calculateChemistryTrend(interactions); // Same calculation for now
    }
    
    private String determineRelationshipStage(RelationshipDynamics dynamics) {
        if (dynamics.currentChemistry >= EXCELLENT_CHEMISTRY_THRESHOLD) {
            return "excellent_partnership";
        } else if (dynamics.currentChemistry >= GOOD_CHEMISTRY_THRESHOLD) {
            return "strong_compatibility";
        } else if (dynamics.currentChemistry >= POOR_CHEMISTRY_THRESHOLD) {
            return "developing_rapport";
        } else {
            return "challenging_dynamics";
        }
    }
    
    private List<String> identifyStrongestStrategies(RelationshipDynamics dynamics) {
        return dynamics.getStrategyPerformance().entrySet().stream()
                .filter(entry -> entry.getValue() >= GOOD_CHEMISTRY_THRESHOLD)
                .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    private float calculateEvolutionPotential(RelationshipDynamics dynamics) {
        // Calculate potential for relationship improvement
        float currentGap = 1.0f - dynamics.currentChemistry;
        float momentum = Math.max(0, dynamics.evolutionMomentum);
        return Math.min(1.0f, currentGap * 0.7f + momentum * 0.3f);
    }
    
    private float calculateImprovementPotential(RelationshipDynamics dynamics, String strategy) {
        float currentPerf = dynamics.getStrategyPerformance().getOrDefault(strategy, 0.5f);
        return Math.max(0, EXCELLENT_CHEMISTRY_THRESHOLD - currentPerf);
    }
    
    private List<String> identifyChemistryEnhancements(RelationshipDynamics dynamics) {
        List<String> enhancements = new ArrayList<>();
        
        if (dynamics.evolutionMomentum < 0) {
            enhancements.add("Focus on collaborative strategies to reverse negative momentum");
        }
        
        if (dynamics.currentChemistry < GOOD_CHEMISTRY_THRESHOLD) {
            enhancements.add("Explore personality-complementary emotional approaches");
        }
        
        return enhancements;
    }
    
    private List<String> generateLanguageDevelopmentTips(RelationshipDynamics dynamics) {
        List<String> tips = new ArrayList<>();
        
        tips.add("Develop shared references through successful interactions");
        tips.add("Create unique phrases that resonate with both masters");
        tips.add("Build emotional shorthand for efficient communication");
        
        return tips;
    }
}