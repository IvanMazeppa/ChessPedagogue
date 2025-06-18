package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 🎭 PHASE 2: EMOTIONAL INTEGRATION BRIDGE
 * 
 * Seamlessly integrates the new multi-layered emotional system with existing
 * conversation orchestrators, evaluation trackers, and voice systems.
 * 
 * This bridge ensures that:
 * - Existing conversation flows now include emotional complexity
 * - Voice reactions trigger appropriate stress and defensive mechanisms
 * - Evaluation changes cause realistic psychological responses
 * - Emotional breakthroughs enhance conversation authenticity
 */
public class Phase2EmotionalIntegrationBridge implements 
    MultiLayeredEmotionalManager.MultiLayeredEmotionalCallback,
    VoiceEmotionalAnalyzer.VoiceEmotionalCallback {
    
    private static final String TAG = "🎭 Phase2Bridge";
    private static Phase2EmotionalIntegrationBridge instance;
    
    private final Context context;
    
    // System integrations
    private MultiLayeredEmotionalManager multiLayeredManager;
    private SpectatorConversationOrchestrator spectatorOrchestrator;
    private EmotionalIntelligenceManager emotionalIntelligenceManager;
    private VoiceEmotionalAnalyzer voiceEmotionalAnalyzer;
    private EvaluationTracker evaluationTracker;
    private EmotionalMomentumManager momentumManager;
    
    // Integration state
    private final Map<String, String> lastKnownEmotions;
    private boolean integrationActive = false;
    
    public static Phase2EmotionalIntegrationBridge getInstance(Context context) {
        if (instance == null) {
            instance = new Phase2EmotionalIntegrationBridge(context);
        }
        return instance;
    }
    
    public Phase2EmotionalIntegrationBridge(Context context) {
        this.context = context.getApplicationContext();
        this.lastKnownEmotions = new HashMap<>();
        
        // Initialize momentum manager
        this.momentumManager = EmotionalMomentumManager.getInstance(context);
        
        Log.d(TAG, "🚀 Phase 2 Emotional Integration Bridge initialized with momentum system");
    }
    
    /**
     * Initialize the bridge with all required systems
     */
    public void initializeIntegration(MultiLayeredEmotionalManager multiLayeredManager,
                                    SpectatorConversationOrchestrator spectatorOrchestrator,
                                    EmotionalIntelligenceManager emotionalIntelligenceManager,
                                    VoiceEmotionalAnalyzer voiceEmotionalAnalyzer,
                                    EvaluationTracker evaluationTracker) {
        
        this.multiLayeredManager = multiLayeredManager;
        this.spectatorOrchestrator = spectatorOrchestrator;
        this.emotionalIntelligenceManager = emotionalIntelligenceManager;
        this.voiceEmotionalAnalyzer = voiceEmotionalAnalyzer;
        this.evaluationTracker = evaluationTracker;
        
        // Register callbacks
        if (multiLayeredManager != null) {
            multiLayeredManager.addCallback(this);
        }
        
        if (voiceEmotionalAnalyzer != null) {
            voiceEmotionalAnalyzer.addVoiceEmotionalCallback(this);
        }
        
        integrationActive = true;
        
        Log.d(TAG, "✅ Phase 2 integration bridge fully connected to all systems");
    }
    
    /**
     * Initialize masters for spectator mode with Phase 2 emotional layers
     */
    public void initializeSpectatorMode(String masterA, String masterB) {
        if (multiLayeredManager != null) {
            multiLayeredManager.initializeMastersForSpectatorMode(masterA, masterB);
            
            // Initialize emotional intelligence for both masters
            if (emotionalIntelligenceManager != null) {
                // Trigger initial emotional analysis
                triggerInitialEmotionalStates(masterA, masterB);
            }
            
            // Initialize momentum tracking for both masters
            momentumManager.initializeMaster(masterA);
            momentumManager.initializeMaster(masterB);
            
            Log.d(TAG, String.format("🎭 Spectator mode initialized with Phase 2 emotions and momentum: %s vs %s", 
                                    masterA, masterB));
        }
    }
    
    /**
     * Process evaluation changes with enhanced emotional complexity and momentum
     */
    public void processEvaluationChange(String currentMaster, float currentEval, 
                                      float previousEval, String gamePhase) {
        if (!integrationActive || multiLayeredManager == null) return;
        
        // Calculate emotional impact of evaluation change
        float evalDifference = currentEval - previousEval;
        String emotionalTrigger = determineEvaluationTrigger(evalDifference, gamePhase);
        String detectedEmotion = mapEvaluationToEmotion(evalDifference, currentMaster);
        float intensity = Math.min(1.0f, Math.abs(evalDifference) / 200.0f); // Normalize to 0-1
        
        // Apply stress based on evaluation shock
        String stressTrigger = String.format("eval_change_%.1f", evalDifference);
        
        // Process through multi-layered emotional system
        multiLayeredManager.processEmotionalUpdate(currentMaster, detectedEmotion, 
                                                  intensity, stressTrigger);
        
        // Update last known emotion
        lastKnownEmotions.put(currentMaster, detectedEmotion);
        
        Log.d(TAG, String.format("🎯 Evaluation emotion for %s: %.1f->%.1f = %s (intensity: %.2f)", 
                                currentMaster, previousEval, currentEval, detectedEmotion, intensity));
    }
    
    /**
     * NEW: Process game progression with momentum system
     */
    public void processGameProgression(String currentMaster, int moveCount, 
                                     float currentEval, float previousEval) {
        if (!integrationActive) return;
        
        // Update momentum based on game progression
        momentumManager.updateGameProgression(currentMaster, moveCount, currentEval, previousEval);
        
        // Get enhanced emotional state from momentum
        EmotionalMomentumManager.EmotionalState momentumState = 
            momentumManager.getCurrentEmotionalState(currentMaster);
        
        // If momentum suggests different emotion than base system, blend them
        if (multiLayeredManager != null && !momentumState.emotion.equals("analytical")) {
            multiLayeredManager.processEmotionalUpdate(currentMaster, momentumState.emotion, 
                                                      momentumState.intensity, "momentum_driven");
        }
        
        Log.d(TAG, String.format("🚀 Game progression momentum for %s (move %d): %s", 
                                currentMaster, moveCount, momentumState));
    }
    
    /**
     * NEW: Process conversation momentum
     */
    public void processConversationMomentum(String masterName, String dialogueContent, String opponentName) {
        if (!integrationActive) return;
        
        // Update conversation momentum
        momentumManager.updateConversationMomentum(masterName, dialogueContent, opponentName);
        
        // Get current momentum state
        EmotionalMomentumManager.EmotionalState momentumState = 
            momentumManager.getCurrentEmotionalState(masterName);
        
        // Apply momentum-driven emotions to the multi-layered system
        if (multiLayeredManager != null && momentumState.intensity > 0.7f) {
            multiLayeredManager.processEmotionalUpdate(masterName, momentumState.emotion, 
                                                      momentumState.intensity, "conversation_momentum");
        }
        
        Log.d(TAG, String.format("💬 Conversation momentum for %s: %s (talking to %s)", 
                                masterName, momentumState, opponentName));
    }
    
    /**
     * Map evaluation changes to emotional states
     */
    private String mapEvaluationToEmotion(float evalDifference, String masterName) {
        if (evalDifference > 150) {
            return "ecstatic";
        } else if (evalDifference > 50) {
            return "thrilled";
        } else if (evalDifference > 20) {
            return "pleased";
        } else if (evalDifference < -150) {
            return "devastated";
        } else if (evalDifference < -50) {
            return "frustrated";
        } else if (evalDifference < -20) {
            return "concerned";
        } else {
            return "analytical";
        }
    }
    
    /**
     * Determine the trigger type for evaluation changes
     */
    private String determineEvaluationTrigger(float evalDifference, String gamePhase) {
        if (Math.abs(evalDifference) > 100) {
            return "major_evaluation_swing";
        } else if (Math.abs(evalDifference) > 50) {
            return "significant_position_change";
        } else {
            return "position_adjustment";
        }
    }
    
    /**
     * Generate enhanced conversation context with Phase 2 emotional complexity and momentum
     */
    public String generateEnhancedConversationContext(String masterName, String gameContext) {
        if (multiLayeredManager == null) {
            return gameContext;
        }
        
        // Get emotional complexity from Phase 2 system
        String emotionalContext = multiLayeredManager.generateEnhancedEmotionalContext(masterName);
        
        // Get momentum-enhanced emotional context
        String momentumContext = momentumManager.getEnhancedEmotionalContext(masterName);
        
        // Combine with game context
        StringBuilder enhancedContext = new StringBuilder(gameContext);
        enhancedContext.append(" ").append(emotionalContext);
        enhancedContext.append(" ").append(momentumContext);
        
        // Add breakthrough warnings
        EmotionalLayer.EmotionalComplexity complexity = 
            multiLayeredManager.getEmotionalComplexity(masterName);
        
        if (complexity != null && complexity.shouldTriggerBreakthrough()) {
            enhancedContext.append("IMPORTANT: You're experiencing emotional pressure that might cause you to reveal your true feelings unexpectedly. ");
        }
        
        Log.d(TAG, String.format("📝 Enhanced context for %s: added emotional complexity and momentum", masterName));
        
        return enhancedContext.toString();
    }
    
    /**
     * Trigger initial emotional states for new games
     */
    private void triggerInitialEmotionalStates(String masterA, String masterB) {
        if (multiLayeredManager == null) return;
        
        // Set initial analytical state with low stress
        multiLayeredManager.processEmotionalUpdate(masterA, "analytical", 0.3f, "game_start");
        multiLayeredManager.processEmotionalUpdate(masterB, "analytical", 0.3f, "game_start");
        
        Log.d(TAG, String.format("🎯 Initial emotional states set for %s and %s", masterA, masterB));
    }
    
    // =========================== CALLBACK IMPLEMENTATIONS ===========================
    
    /**
     * Handle emotional breakthroughs from Phase 2 system
     */
    @Override
    public void onEmotionalBreakthrough(String masterName, EmotionalLayer.EmotionalComplexity complexity) {
        Log.d(TAG, String.format("💥 EMOTIONAL BREAKTHROUGH: %s's %s mask slipped, revealing %s!", 
                                masterName, complexity.surfaceEmotion, complexity.underlyingEmotion));
        
        // Trigger conversation about the breakthrough
        if (spectatorOrchestrator != null && integrationActive) {
            // This could trigger a conversation where the master acts more vulnerable
            String triggerType = "emotional_breakthrough";
            String context = String.format("BREAKTHROUGH CONTEXT: %s just had their emotional mask slip, showing their true %s feelings.", 
                                          masterName, complexity.underlyingEmotion);
            
            // Note: This would need the opponent master name - you might need to track current game state
            Log.d(TAG, "🎭 Breakthrough could trigger special conversation type");
        }
    }
    
    /**
     * Handle emotional contrasts (high mask intensity)
     */
    @Override
    public void onEmotionalContrast(String masterName, EmotionalLayer.EmotionalComplexity complexity) {
        Log.d(TAG, String.format("🎭 HIGH EMOTIONAL CONTRAST: %s showing %s but feeling %s (mask: %.2f)", 
                                masterName, complexity.surfaceEmotion, complexity.underlyingEmotion, 
                                complexity.maskIntensity));
        
        // This creates opportunity for more nuanced conversation generation
        // The master might say one thing but their underlying emotion affects the delivery
    }
    
    /**
     * Handle general emotional updates
     */
    @Override
    public void onEmotionalUpdate(String masterName, EmotionalLayer.EmotionalComplexity complexity) {
        // Track emotional changes for conversation timing
        String previousEmotion = lastKnownEmotions.get(masterName);
        if (previousEmotion != null && !previousEmotion.equals(complexity.surfaceEmotion)) {
            Log.d(TAG, String.format("🔄 Emotional transition for %s: %s -> %s", 
                                    masterName, previousEmotion, complexity.surfaceEmotion));
        }
        
        lastKnownEmotions.put(masterName, complexity.surfaceEmotion);
    }
    
    /**
     * Handle defensive mechanism activation
     */
    @Override
    public void onDefensiveMechanismActivated(String masterName, String mechanism, String trigger) {
        Log.d(TAG, String.format("🛡️ DEFENSIVE MECHANISM: %s activated %s defense (trigger: %s)", 
                                masterName, mechanism, trigger));
        
        // This could influence conversation tone and content
        // Masters using defensive mechanisms might be more guarded or aggressive
    }
    
    /**
     * Handle voice emotional reactions with Phase 2 integration
     */
    @Override
    public void onVoiceEmotionalReaction(String listeningMaster, VoiceEmotionalAnalyzer.EmotionalResponse response) {
        Log.d(TAG, String.format("🎤 Voice reaction: %s -> %s (%s, intensity: %.2f)", 
                                response.responseReason, listeningMaster, 
                                response.triggeredEmotion, response.responseIntensity));
        
        // Process voice reaction through Phase 2 system
        if (multiLayeredManager != null) {
            String speakingMaster = extractSpeakingMaster(response.responseReason);
            multiLayeredManager.processVoiceEmotionalReaction(listeningMaster, speakingMaster, response);
        }
    }
    
    /**
     * Handle voice emotional cue detection
     */
    @Override
    public void onVoiceEmotionalCueDetected(VoiceEmotionalAnalyzer.EmotionalCue cue) {
        Log.d(TAG, String.format("🎭 Voice cue detected: %s spoke with %s delivery", 
                                cue.speakingMaster, cue.voiceChars.dominantTone));
        
        // Voice delivery can trigger stress in Phase 2 system
        if (multiLayeredManager != null && cue.emotionalIntensity > 0.6f) {
            String stressTrigger = "intense_voice_delivery";
            float stressImpact = cue.emotionalIntensity * 0.2f;
            
            // Apply stress to the speaking master for high-intensity delivery
            multiLayeredManager.processEmotionalUpdate(cue.speakingMaster, cue.detectedEmotion, 
                                                      cue.emotionalIntensity, stressTrigger);
        }
    }
    
    /**
     * Extract speaking master name from response reason
     */
    private String extractSpeakingMaster(String responseReason) {
        // Simple extraction - could be enhanced
        if (responseReason.contains("tal")) return "tal";
        if (responseReason.contains("fischer")) return "fischer";
        if (responseReason.contains("carlsen")) return "carlsen";
        if (responseReason.contains("kasparov")) return "kasparov";
        if (responseReason.contains("anand")) return "anand";
        if (responseReason.contains("karpov")) return "karpov";
        return "unknown";
    }
    
    /**
     * Get current emotional statistics for debugging
     */
    public Map<String, String> getEmotionalStatistics() {
        if (multiLayeredManager != null) {
            return multiLayeredManager.getEmotionalStatistics();
        }
        return new HashMap<>();
    }
    
    /**
     * Get momentum statistics for debugging
     */
    public String getMomentumStatistics() {
        return momentumManager.getMomentumStatistics();
    }
    
    /**
     * Clear emotional state for new games
     */
    public void clearEmotionalState() {
        if (multiLayeredManager != null) {
            multiLayeredManager.clearAllEmotionalStates();
        }
        
        // Reset momentum for new game
        momentumManager.resetGameMomentum();
        
        lastKnownEmotions.clear();
        
        Log.d(TAG, "🧹 Cleared Phase 2 emotional states and momentum");
    }
    
    /**
     * Test Phase 2 system with forced emotional states
     */
    public void testEmotionalMask(String masterName, String surfaceEmotion, 
                                String underlyingEmotion, String defensiveResponse) {
        if (multiLayeredManager != null) {
            multiLayeredManager.forceEmotionalState(masterName, surfaceEmotion, 
                                                   underlyingEmotion, defensiveResponse);
            
            Log.d(TAG, String.format("🧪 Testing emotional mask for %s: %s hiding %s (%s)", 
                                    masterName, surfaceEmotion, underlyingEmotion, defensiveResponse));
        }
    }
    
    /**
     * Check if Phase 2 integration is active
     */
    public boolean isIntegrationActive() {
        return integrationActive && multiLayeredManager != null;
    }
    
    /**
     * Disable integration for testing Phase 1 only
     */
    public void disableIntegration() {
        integrationActive = false;
        Log.d(TAG, "⏸️ Phase 2 integration disabled");
    }
    
    /**
     * Re-enable integration
     */
    public void enableIntegration() {
        integrationActive = true;
        Log.d(TAG, "▶️ Phase 2 integration enabled");
    }
}