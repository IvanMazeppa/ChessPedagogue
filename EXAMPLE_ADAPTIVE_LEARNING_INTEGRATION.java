package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧠🔗 EXAMPLE: Adaptive Learning Integration
 * 
 * This file demonstrates how to integrate the new EmotionalStrategyLearner 
 * with existing classes like SpectatorConversationOrchestrator and 
 * EmotionalIntelligenceManager to enable adaptive learning capabilities.
 * 
 * Use this as a reference for updating your existing code to benefit from
 * the adaptive learning system while maintaining backward compatibility.
 */
public class ExampleAdaptiveLearningIntegration {
    private static final String TAG = "AdaptiveLearningExample";
    
    /**
     * Example 1: Enhancing SpectatorConversationOrchestrator
     * 
     * This shows how to modify the emotional analysis in spectator mode
     * to use learning-enhanced strategies.
     */
    public static class EnhancedSpectatorConversationOrchestrator {
        private final Context context;
        private final EmotionalStrategyIntegrationBridge learningBridge;
        private final EmotionalIntelligenceManager emotionalIntelligence;
        
        public EnhancedSpectatorConversationOrchestrator(Context context) {
            this.context = context;
            this.learningBridge = EmotionalStrategyIntegrationBridge.getInstance(context);
            this.emotionalIntelligence = EmotionalIntelligenceManager.getInstance(context);
        }
        
        /**
         * OLD METHOD: Standard emotional analysis
         */
        private EmotionalIntelligenceManager.EmotionalAnalysisResult analyzeEmotionOldWay(
                String masterName, String gameContext, String conversationTopic,
                Float currentEval, Float evalChange, EmotionalContext emotionalContext) {
            
            return emotionalIntelligence.analyzeEmotionalState(
                masterName, gameContext, conversationTopic, 
                currentEval, evalChange, emotionalContext
            );
        }
        
        /**
         * NEW METHOD: Learning-enhanced emotional analysis
         */
        private EmotionalIntelligenceManager.EmotionalAnalysisResult analyzeEmotionNewWay(
                String masterName, String opponentName, String gameContext, String conversationTopic,
                Float currentEval, Float evalChange, EmotionalContext emotionalContext) {
            
            // Use the integration bridge for learning-enhanced analysis
            return learningBridge.analyzeSpectatorEmotionalState(
                masterName, opponentName, gameContext, conversationTopic,
                currentEval, evalChange, emotionalContext
            );
        }
        
        /**
         * Enhanced conversation generation with adaptive learning
         */
        public void generateLearningEnhancedResponse(String whitePlayer, String blackPlayer, 
                                                   String currentSpeaker, String conversationTopic,
                                                   Float currentEval, Float evalChange,
                                                   EmotionalContext emotionalContext) {
            
            String opponent = currentSpeaker.equals(whitePlayer) ? blackPlayer : whitePlayer;
            String gameContext = determineGameContext(currentEval, evalChange);
            
            // Get learning-enhanced emotional analysis
            EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult = 
                learningBridge.analyzeSpectatorEmotionalState(
                    currentSpeaker, opponent, gameContext, conversationTopic,
                    currentEval, evalChange, emotionalContext
                );
            
            Log.d(TAG, String.format("🧠✨ Learning-enhanced analysis: %s vs %s - %s emotion with strategy guidance", 
                   currentSpeaker, opponent, emotionalResult.emotion.name));
            
            // The emotionalResult now contains strategy-enhanced prompt instructions
            // Use these enhanced instructions for more effective conversations
            
            // Generate response using enhanced emotional analysis
            generateResponseWithEnhancedStrategy(currentSpeaker, emotionalResult, conversationTopic);
            
            // After the response is generated and delivered, record learning feedback
            recordConversationOutcome(currentSpeaker, opponent, emotionalResult, gameContext);
        }
        
        private void generateResponseWithEnhancedStrategy(String masterName, 
                                                        EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult,
                                                        String conversationTopic) {
            // Use the enhanced prompt instructions that include learned strategies
            String enhancedPrompt = buildEnhancedPrompt(masterName, emotionalResult, conversationTopic);
            
            Log.d(TAG, String.format("🎯 Enhanced prompt includes: %s", 
                   emotionalResult.promptInstructions != null ? "Learning guidance" : "Standard emotional analysis"));
            
            // Continue with standard response generation...
        }
        
        private String buildEnhancedPrompt(String masterName, 
                                         EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult,
                                         String conversationTopic) {
            StringBuilder prompt = new StringBuilder();
            
            // Standard personality and context
            prompt.append(String.format("You are %s. Discuss %s.", masterName, conversationTopic));
            
            // Enhanced strategy instructions from learning system
            if (emotionalResult.promptInstructions != null) {
                prompt.append("\n\n").append(emotionalResult.promptInstructions);
            }
            
            return prompt.toString();
        }
        
        private void recordConversationOutcome(String masterName, String opponent, 
                                             EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult,
                                             String gameContext) {
            // Simulate conversation outcome analysis
            // In practice, you'd analyze the actual conversation effectiveness
            
            boolean wasEffective = simulateConversationEffectiveness(emotionalResult);
            float effectivenessScore = wasEffective ? 0.75f : 0.35f;
            String opponentResponse = wasEffective ? "engaged" : "dismissive";
            float relationshipImpact = wasEffective ? 0.1f : -0.05f;
            
            // Record the outcome for learning
            learningBridge.recordStrategyOutcome(
                masterName, opponent, wasEffective, effectivenessScore,
                opponentResponse, relationshipImpact, gameContext,
                "Spectator conversation about " + gameContext
            );
            
            Log.d(TAG, String.format("📊 Recorded learning outcome: %s vs %s - %s (%.1f%% effective)", 
                   masterName, opponent, wasEffective ? "SUCCESS" : "FAILURE", effectivenessScore * 100));
        }
        
        private boolean simulateConversationEffectiveness(EmotionalIntelligenceManager.EmotionalAnalysisResult result) {
            // Simulate effectiveness based on emotional state
            // In practice, you'd use real conversation analysis
            switch (result.emotion) {
                case THRILLED:
                case EXCITED:
                case IMPRESSED:
                case CONFIDENT:
                    return true;  // Positive emotions indicate effectiveness
                case FRUSTRATED:
                case CONCERNED:
                case DEVASTATED:
                    return false; // Negative emotions indicate ineffectiveness
                default:
                    return Math.random() > 0.4; // 60% success for neutral emotions
            }
        }
        
        private String determineGameContext(Float currentEval, Float evalChange) {
            if (evalChange != null && Math.abs(evalChange) > 100) {
                return "tactical_sequence";
            } else if (currentEval != null && Math.abs(currentEval) > 500) {
                return "decisive_advantage";
            } else {
                return "middlegame";
            }
        }
    }
    
    /**
     * Example 2: Enhanced Conversation Memory Integration
     * 
     * Shows how to combine adaptive learning with conversation memory
     */
    public static class LearningEnhancedConversationMemory {
        private final EmotionalStrategyIntegrationBridge learningBridge;
        private final ConversationMemoryManager memoryManager;
        
        public LearningEnhancedConversationMemory(Context context) {
            this.learningBridge = EmotionalStrategyIntegrationBridge.getInstance(context);
            this.memoryManager = ConversationMemoryManager.getInstance(context);
        }
        
        public void processConversationWithLearning(String master1, String master2, 
                                                   String conversationContent, String gameContext) {
            
            // Store in conversation memory as usual
            memoryManager.recordConversation(master1, master2, conversationContent, gameContext);
            
            // Analyze conversation for learning patterns
            analyzeLearningPatterns(master1, master2, conversationContent, gameContext);
            
            // Get learning analytics for progress tracking
            EmotionalStrategyLearner.LearningAnalytics analytics1 = learningBridge.getLearningAnalytics(master1);
            EmotionalStrategyLearner.LearningAnalytics analytics2 = learningBridge.getLearningAnalytics(master2);
            
            Log.i(TAG, String.format("🧠📚 Learning progress: %s", analytics1.toString()));
            Log.i(TAG, String.format("🧠📚 Learning progress: %s", analytics2.toString()));
        }
        
        private void analyzeLearningPatterns(String master1, String master2, 
                                           String conversationContent, String gameContext) {
            // Extract learning indicators from conversation content
            if (conversationContent.contains("interesting") || conversationContent.contains("intriguing")) {
                // Positive engagement - record as successful strategy
                learningBridge.recordStrategyOutcome(master1, master2, true, 0.8f, 
                    "interested", 0.1f, gameContext, "Positive engagement detected");
            } else if (conversationContent.contains("disagree") || conversationContent.contains("nonsense")) {
                // Negative response - record as unsuccessful strategy  
                learningBridge.recordStrategyOutcome(master1, master2, false, 0.3f,
                    "disagreeable", -0.05f, gameContext, "Negative response detected");
            }
        }
    }
    
    /**
     * Example 3: Session Management for Spectator Games
     * 
     * Shows proper session lifecycle management for learning
     */
    public static class LearningSessionManager {
        private final EmotionalStrategyIntegrationBridge learningBridge;
        
        public LearningSessionManager(Context context) {
            this.learningBridge = EmotionalStrategyIntegrationBridge.getInstance(context);
        }
        
        public void startSpectatorGame(String whitePlayer, String blackPlayer) {
            // Learning sessions start automatically, but we can track them
            Log.i(TAG, String.format("🎮 Starting spectator game: %s vs %s - Learning enabled", 
                   whitePlayer, blackPlayer));
            
            // Learning analytics show initial state
            logInitialLearningState(whitePlayer, blackPlayer);
        }
        
        public void endSpectatorGame(String whitePlayer, String blackPlayer) {
            // End learning sessions for both masters
            learningBridge.endLearningSession(whitePlayer, blackPlayer);
            learningBridge.endLearningSession(blackPlayer, whitePlayer);
            
            Log.i(TAG, String.format("🎮 Ended spectator game: %s vs %s - Learning data saved", 
                   whitePlayer, blackPlayer));
            
            // Show learning progress
            logFinalLearningState(whitePlayer, blackPlayer);
        }
        
        private void logInitialLearningState(String master1, String master2) {
            EmotionalStrategyLearner.LearningAnalytics analytics1 = learningBridge.getLearningAnalytics(master1);
            EmotionalStrategyLearner.LearningAnalytics analytics2 = learningBridge.getLearningAnalytics(master2);
            
            Log.i(TAG, String.format("📊 Initial learning state - %s: %d interactions, %.1f%% success", 
                   master1, analytics1.totalInteractions, analytics1.averageSuccessRate * 100));
            Log.i(TAG, String.format("📊 Initial learning state - %s: %d interactions, %.1f%% success", 
                   master2, analytics2.totalInteractions, analytics2.averageSuccessRate * 100));
        }
        
        private void logFinalLearningState(String master1, String master2) {
            EmotionalStrategyLearner.LearningAnalytics analytics1 = learningBridge.getLearningAnalytics(master1);
            EmotionalStrategyLearner.LearningAnalytics analytics2 = learningBridge.getLearningAnalytics(master2);
            
            Log.i(TAG, String.format("🎓 Final learning state - %s: %d interactions, %.1f%% success", 
                   master1, analytics1.totalInteractions, analytics1.averageSuccessRate * 100));
            Log.i(TAG, String.format("🎓 Final learning state - %s: %d interactions, %.1f%% success", 
                   master2, analytics2.totalInteractions, analytics2.averageSuccessRate * 100));
            
            // Log effective strategies learned
            if (!analytics1.effectiveStrategies.isEmpty()) {
                Log.i(TAG, String.format("✅ %s learned strategies: %s", master1, 
                       String.join(", ", analytics1.effectiveStrategies)));
            }
            if (!analytics2.effectiveStrategies.isEmpty()) {
                Log.i(TAG, String.format("✅ %s learned strategies: %s", master2, 
                       String.join(", ", analytics2.effectiveStrategies)));
            }
        }
    }
    
    /**
     * Example 4: Testing and Validation
     * 
     * Shows how to test the learning system
     */
    public static class AdaptiveLearningTester {
        private final EmotionalStrategyIntegrationBridge learningBridge;
        
        public AdaptiveLearningTester(Context context) {
            this.learningBridge = EmotionalStrategyIntegrationBridge.getInstance(context);
        }
        
        /**
         * Run a test scenario to validate learning
         */
        public void runLearningTest() {
            Log.i(TAG, "🧪 Starting adaptive learning test scenario...");
            
            // Test scenario: Fischer vs Carlsen interaction
            String fischer = "Fischer";
            String carlsen = "Carlsen";
            String gameContext = "middlegame";
            
            // Simulate several interactions with different outcomes
            simulateSuccessfulStrategy(fischer, carlsen, "dismissive_critical", gameContext);
            simulateSuccessfulStrategy(fischer, carlsen, "dismissive_critical", gameContext);
            simulateFailedStrategy(fischer, carlsen, "supportive", gameContext);
            simulateFailedStrategy(fischer, carlsen, "supportive", gameContext);
            simulateSuccessfulStrategy(fischer, carlsen, "brutally_honest", gameContext);
            
            // Check learning results
            EmotionalStrategyLearner.LearningAnalytics analytics = learningBridge.getLearningAnalytics(fischer);
            Log.i(TAG, String.format("🧪 Test results: %s", analytics.toString()));
            
            // Validate that Fischer learned to prefer dismissive_critical over supportive
            boolean testPassed = analytics.effectiveStrategies.stream()
                .anyMatch(strategy -> strategy.contains("dismissive_critical"));
            
            Log.i(TAG, String.format("🧪 Learning test %s: Fischer should prefer dismissive approaches with Carlsen", 
                   testPassed ? "PASSED" : "FAILED"));
        }
        
        private void simulateSuccessfulStrategy(String master, String opponent, String strategy, String context) {
            learningBridge.forceStrategyLearning(master, opponent, strategy, true, context);
            Log.d(TAG, String.format("🧪 Simulated successful %s strategy: %s vs %s", strategy, master, opponent));
        }
        
        private void simulateFailedStrategy(String master, String opponent, String strategy, String context) {
            learningBridge.forceStrategyLearning(master, opponent, strategy, false, context);
            Log.d(TAG, String.format("🧪 Simulated failed %s strategy: %s vs %s", strategy, master, opponent));
        }
    }
    
    /**
     * Example 5: Migration Helper
     * 
     * Shows how to gradually migrate existing code
     */
    public static class AdaptiveLearningMigrationHelper {
        
        /**
         * Step 1: Replace direct EmotionalIntelligenceManager calls
         */
        public static EmotionalIntelligenceManager.EmotionalAnalysisResult migrateEmotionalAnalysis(
                Context context, String masterName, String opponentName, String gameContext, 
                String conversationContext, Float currentEval, Float evalChange, EmotionalContext emotionalContext) {
            
            // OLD WAY (comment out gradually)
            // EmotionalIntelligenceManager emotionalManager = EmotionalIntelligenceManager.getInstance(context);
            // return emotionalManager.analyzeEmotionalState(masterName, gameContext, conversationContext, 
            //                                              currentEval, evalChange, emotionalContext);
            
            // NEW WAY (with learning enhancement)
            EmotionalStrategyIntegrationBridge bridge = EmotionalStrategyIntegrationBridge.getInstance(context);
            
            if (opponentName != null) {
                // Use learning-enhanced analysis when opponent is known
                return bridge.analyzeEmotionalStateWithLearning(masterName, opponentName, gameContext, 
                                                               conversationContext, currentEval, evalChange, emotionalContext);
            } else {
                // Fall back to standard analysis when no opponent
                return bridge.analyzeEmotionalState(masterName, gameContext, conversationContext, 
                                                   currentEval, evalChange, emotionalContext);
            }
        }
        
        /**
         * Step 2: Add learning session management to existing conversation flows
         */
        public static void addSessionManagement(Context context, String master1, String master2, 
                                               boolean gameStarting, boolean gameEnding) {
            EmotionalStrategyIntegrationBridge bridge = EmotionalStrategyIntegrationBridge.getInstance(context);
            
            if (gameStarting) {
                Log.i(TAG, String.format("🎮 Game starting - Learning sessions will auto-start for %s vs %s", master1, master2));
                // Sessions start automatically on first analysis call
            }
            
            if (gameEnding) {
                bridge.endLearningSession(master1, master2);
                bridge.endLearningSession(master2, master1);
                Log.i(TAG, String.format("🎮 Game ended - Learning sessions completed for %s vs %s", master1, master2));
            }
        }
        
        /**
         * Step 3: Add automatic feedback detection to existing emotion tracking
         */
        public static void addAutomaticFeedback(Context context, 
                                               EmotionalIntelligenceManager.EmotionalState newEmotionalState,
                                               EmotionalContext updatedEmotionalContext, String gameContext) {
            EmotionalStrategyIntegrationBridge bridge = EmotionalStrategyIntegrationBridge.getInstance(context);
            
            // Auto-detect strategy outcomes from emotional changes
            bridge.detectStrategyOutcomeFromEmotionalChange(newEmotionalState, updatedEmotionalContext, gameContext);
        }
    }
}

/*
INTEGRATION CHECKLIST:

1. Add EmotionalStrategyLearner.java to your project ✅
2. Add EmotionalStrategyIntegrationBridge.java to your project ✅  
3. Update GameDatabaseHelper with new learning table ✅
4. Replace emotional analysis calls with bridge methods
5. Add session management (start/end) to conversation flows
6. Implement feedback collection (automatic or manual)
7. Test learning with sample interactions
8. Monitor learning analytics for effectiveness
9. Validate master personality authenticity

EXPECTED BEHAVIOR AFTER INTEGRATION:

- Masters will start with 40% exploration rate, trying different approaches
- As they interact more, they'll learn what works with each opponent
- Effective strategies will be used more frequently (exploitation)
- Ineffective strategies will be avoided
- Learning data persists across app sessions
- Masters maintain their authentic personalities while optimizing effectiveness
- Detailed analytics show learning progress and effective strategies

TESTING RECOMMENDATIONS:

1. Use AdaptiveLearningTester to validate learning works
2. Monitor logs for learning events (🧠🎯 tags)
3. Check database for learning data accumulation
4. Verify strategy recommendations change over time
5. Ensure master personalities remain authentic
6. Test session management during full games
7. Validate learning persistence across app restarts

*/ 