package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧠🔗 EMOTIONAL STRATEGY INTEGRATION BRIDGE
 * 
 * Seamlessly integrates the new EmotionalStrategyLearner with the existing 
 * EmotionalIntelligenceManager to provide adaptive learning capabilities
 * while maintaining compatibility with the current emotional intelligence system.
 * 
 * This bridge:
 * ✅ Intercepts emotional analysis requests and enhances them with learned strategies
 * ✅ Captures emotional feedback and feeds it back to the learning system
 * ✅ Translates between existing emotional intelligence concepts and learning framework
 * ✅ Provides backward compatibility for existing code
 * ✅ Enables gradual adoption of adaptive learning features
 * 
 * Integration Points:
 * - Wraps EmotionalIntelligenceManager.analyzeEmotionalState() with learning enhancement
 * - Automatically records strategy outcomes based on emotional context changes
 * - Provides learning-enhanced recommendations for master personalities
 * - Maintains existing API contracts while adding adaptive capabilities
 */
public class EmotionalStrategyIntegrationBridge {
    private static final String TAG = "EmotionalStrategyBridge";
    private static EmotionalStrategyIntegrationBridge instance;
    
    private final Context context;
    private final EmotionalIntelligenceManager emotionalManager;
    private final EmotionalStrategyLearner strategyLearner;
    
    // Track ongoing interactions for feedback collection
    private String currentMaster;
    private String currentOpponent;
    private String currentStrategy;
    private EmotionalIntelligenceManager.EmotionalState lastEmotionalState;
    private long interactionStartTime;
    
    private EmotionalStrategyIntegrationBridge(Context context) {
        this.context = context.getApplicationContext();
        this.emotionalManager = EmotionalIntelligenceManager.getInstance(context);
        this.strategyLearner = EmotionalStrategyLearner.getInstance(context);
        
        Log.i(TAG, "🧠🔗 EmotionalStrategyIntegrationBridge initialized - Learning-enhanced emotional intelligence ready!");
    }
    
    public static synchronized EmotionalStrategyIntegrationBridge getInstance(Context context) {
        if (instance == null) {
            instance = new EmotionalStrategyIntegrationBridge(context);
        }
        return instance;
    }
    
    /**
     * 🎯 Enhanced Emotional Analysis with Adaptive Learning
     * 
     * This is the main integration point - it wraps the existing emotional analysis
     * with learning-enhanced strategy recommendations.
     */
    public EmotionalIntelligenceManager.EmotionalAnalysisResult analyzeEmotionalStateWithLearning(
            String masterName, String opponentName, String gameContext, String conversationContext,
            Float currentEval, Float evalChange, EmotionalContext emotionalContext) {
        
        // Get base emotional analysis from existing system
        EmotionalIntelligenceManager.EmotionalAnalysisResult baseResult = 
            emotionalManager.analyzeEmotionalState(masterName, gameContext, conversationContext, 
                                                  currentEval, evalChange, emotionalContext);
        
        // Enhance with adaptive learning if opponent is specified
        if (opponentName != null && !opponentName.isEmpty()) {
            // Get learning-enhanced analysis
            EmotionalIntelligenceManager.EmotionalAnalysisResult enhancedResult = 
                strategyLearner.getStrategyEnhancedAnalysis(
                    masterName, opponentName, gameContext, conversationContext, baseResult
                );
            
            // Track this interaction for learning feedback
            startInteractionTracking(masterName, opponentName, enhancedResult);
            
            Log.d(TAG, String.format("🎯 Enhanced emotional analysis: %s vs %s - %s approach with learning guidance", 
                   masterName, opponentName, enhancedResult.emotion.name));
            
            return enhancedResult;
        } else {
            // No opponent specified, return base analysis
            Log.d(TAG, String.format("🧠 Standard emotional analysis: %s - %s emotion", 
                   masterName, baseResult.emotion.name));
            
            return baseResult;
        }
    }
    
    /**
     * 🔄 Record Strategy Feedback
     * 
     * Call this method when you have feedback on how well an emotional strategy worked.
     * This feeds learning data back to the adaptive system.
     */
    public void recordStrategyOutcome(String masterName, String opponentName, 
                                    boolean successful, float effectivenessScore, 
                                    String opponentEmotionalResponse, float relationshipImpact,
                                    String gameContext, String details) {
        
        if (currentStrategy != null) {
            EmotionalStrategyLearner.StrategyOutcome outcome = 
                new EmotionalStrategyLearner.StrategyOutcome(
                    successful, effectivenessScore, gameContext, 
                    opponentEmotionalResponse, relationshipImpact, details
                );
            
            strategyLearner.recordStrategyFeedback(masterName, opponentName, currentStrategy, outcome);
            
            Log.d(TAG, String.format("📊 Recorded strategy outcome: %s vs %s - %s %s (%.1f%% effective)", 
                   masterName, opponentName, currentStrategy, 
                   successful ? "SUCCESS" : "FAILURE", effectivenessScore * 100));
        }
    }
    
    /**
     * 🎭 Auto-detect Strategy Outcomes from Emotional Context Changes
     * 
     * This method automatically infers strategy effectiveness by observing
     * changes in emotional context and relationship dynamics.
     */
    public void detectStrategyOutcomeFromEmotionalChange(
            EmotionalIntelligenceManager.EmotionalState newEmotionalState,
            EmotionalContext updatedEmotionalContext, String gameContext) {
        
        if (currentMaster == null || currentOpponent == null || currentStrategy == null) {
            return; // No active interaction to analyze
        }
        
        // Calculate interaction duration
        long interactionDuration = System.currentTimeMillis() - interactionStartTime;
        
        // Analyze emotional progression
        boolean positiveEmotionalChange = analyzeEmotionalProgression(lastEmotionalState, newEmotionalState);
        
        // Calculate effectiveness score based on emotional outcome
        float effectivenessScore = calculateEffectivenessFromEmotionalChange(
            lastEmotionalState, newEmotionalState, interactionDuration
        );
        
        // Determine relationship impact
        float relationshipImpact = estimateRelationshipImpact(newEmotionalState, updatedEmotionalContext);
        
        // Auto-record the outcome
        recordStrategyOutcome(
            currentMaster, currentOpponent, positiveEmotionalChange, effectivenessScore,
            newEmotionalState.name, relationshipImpact, gameContext,
            String.format("Auto-detected from emotional progression: %s -> %s", 
                         lastEmotionalState.name, newEmotionalState.name)
        );
        
        Log.d(TAG, String.format("🔍 Auto-detected strategy outcome: %s strategy resulted in %s emotional change", 
               currentStrategy, positiveEmotionalChange ? "positive" : "negative"));
    }
    
    /**
     * 📈 Get Learning Analytics for a Master
     */
    public EmotionalStrategyLearner.LearningAnalytics getLearningAnalytics(String masterName) {
        return strategyLearner.getLearningAnalytics(masterName);
    }
    
    /**
     * 🎓 End Learning Session
     */
    public void endLearningSession(String masterName, String opponentName) {
        strategyLearner.endLearningSession(masterName, opponentName);
        
        // Clear tracking
        currentMaster = null;
        currentOpponent = null;
        currentStrategy = null;
        lastEmotionalState = null;
    }
    
    /**
     * 🎯 Private Helper Methods
     */
    
    private void startInteractionTracking(String masterName, String opponentName, 
                                        EmotionalIntelligenceManager.EmotionalAnalysisResult result) {
        currentMaster = masterName;
        currentOpponent = opponentName;
        currentStrategy = extractStrategyFromResult(result);
        lastEmotionalState = result.emotion;
        interactionStartTime = System.currentTimeMillis();
        
        Log.d(TAG, String.format("📝 Started tracking interaction: %s vs %s using %s strategy", 
               masterName, opponentName, currentStrategy));
    }
    
    private String extractStrategyFromResult(EmotionalIntelligenceManager.EmotionalAnalysisResult result) {
        // Extract strategy type from the emotional analysis result
        if (result.expressionGuidance != null) {
            Log.d(TAG, "Strategy guidance available for extraction");
        }
        
        // Fallback to emotion-based strategy
        return result.emotion.name.toLowerCase() + "_approach";
    }
    
    private boolean analyzeEmotionalProgression(EmotionalIntelligenceManager.EmotionalState oldState, 
                                              EmotionalIntelligenceManager.EmotionalState newState) {
        if (oldState == null || newState == null) return false;
        
        // Consider intensity changes
        int oldIntensity = oldState.intensity;
        int newIntensity = newState.intensity;
        
        // Positive progression: moving to higher positive intensity or lower negative intensity
        if (oldIntensity < 0 && newIntensity > oldIntensity) return true; // Less negative
        if (oldIntensity >= 0 && newIntensity > oldIntensity) return true; // More positive
        
        // Special cases for contextual emotions
        if (newState == EmotionalIntelligenceManager.EmotionalState.IMPRESSED ||
            newState == EmotionalIntelligenceManager.EmotionalState.INTRIGUED ||
            newState == EmotionalIntelligenceManager.EmotionalState.CONFIDENT) {
            return true;
        }
        
        return false;
    }
    
    private float calculateEffectivenessFromEmotionalChange(
            EmotionalIntelligenceManager.EmotionalState oldState,
            EmotionalIntelligenceManager.EmotionalState newState, long duration) {
        
        if (oldState == null || newState == null) return 0.5f;
        
        // Base effectiveness on emotional intensity change
        float intensityChange = newState.intensity - oldState.intensity;
        
        // Normalize to 0-1 scale
        float effectiveness = 0.5f + (intensityChange * 0.1f);
        
        // Adjust for interaction duration (longer interactions that maintain engagement are better)
        if (duration > 30000) { // 30+ seconds
            effectiveness += 0.1f;
        }
        
        // Cap between 0 and 1
        return Math.max(0.0f, Math.min(1.0f, effectiveness));
    }
    
    private float estimateRelationshipImpact(EmotionalIntelligenceManager.EmotionalState emotionalState,
                                           EmotionalContext emotionalContext) {
        // Estimate how the emotional outcome affects the relationship
        switch (emotionalState) {
            case THRILLED:
            case IMPRESSED:
            case PLEASED:
                return 0.2f; // Positive impact
                
            case FRUSTRATED:
            case CONCERNED:
            case DEVASTATED:
                return -0.1f; // Negative impact
                
            case INTRIGUED:
            case ANALYTICAL:
            case CONFIDENT:
                return 0.05f; // Slight positive impact
                
            default:
                return 0.0f; // Neutral impact
        }
    }
    
    /**
     * 🔧 Convenience Methods for Easy Integration
     */
    
    /**
     * Simple wrapper for existing code that doesn't specify opponents
     */
    public EmotionalIntelligenceManager.EmotionalAnalysisResult analyzeEmotionalState(
            String masterName, String gameContext, String conversationContext,
            Float currentEval, Float evalChange, EmotionalContext emotionalContext) {
        
        // Use standard analysis without learning enhancement
        return emotionalManager.analyzeEmotionalState(masterName, gameContext, conversationContext,
                                                     currentEval, evalChange, emotionalContext);
    }
    
    /**
     * Enhanced analysis for spectator mode with two masters
     */
    public EmotionalIntelligenceManager.EmotionalAnalysisResult analyzeSpectatorEmotionalState(
            String masterName, String opponentName, String gameContext, String conversationContext,
            Float currentEval, Float evalChange, EmotionalContext emotionalContext) {
        
        return analyzeEmotionalStateWithLearning(masterName, opponentName, gameContext, 
                                                conversationContext, currentEval, evalChange, 
                                                emotionalContext);
    }
    
    /**
     * 📊 Learning Status and Debug Information
     */
    
    public String getLearningStatus() {
        StringBuilder status = new StringBuilder();
        status.append("🧠🔗 Emotional Strategy Learning Status:\n");
        
        if (currentMaster != null && currentOpponent != null) {
            status.append(String.format("Active Session: %s vs %s\n", currentMaster, currentOpponent));
            status.append(String.format("Current Strategy: %s\n", currentStrategy));
            status.append(String.format("Session Duration: %.1f minutes\n", 
                (System.currentTimeMillis() - interactionStartTime) / 60000.0f));
        } else {
            status.append("No active learning session\n");
        }
        
        return status.toString();
    }
    
    /**
     * Force strategy learning for testing
     */
    public void forceStrategyLearning(String masterName, String opponentName, String strategyType,
                                    boolean successful, String context) {
        EmotionalStrategyLearner.StrategyOutcome outcome = 
            new EmotionalStrategyLearner.StrategyOutcome(
                successful, successful ? 0.8f : 0.2f, context,
                successful ? "positive" : "negative", 
                successful ? 0.1f : -0.1f, "Manual test outcome"
            );
        
        strategyLearner.recordStrategyFeedback(masterName, opponentName, strategyType, outcome);
        
        Log.i(TAG, String.format("🧪 MANUAL LEARNING: %s vs %s - %s strategy marked as %s", 
               masterName, opponentName, strategyType, successful ? "effective" : "ineffective"));
    }
}