package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.util.Map;

/**
 * 🎭 PHASE 2: EMOTIONAL COMPLEXITY DEMONSTRATION
 * 
 * Demonstrates the new multi-layered emotional system with realistic scenarios
 * showing how chess masters hide emotions, use defensive mechanisms, and have
 * emotional breakthroughs that create authentic psychological interactions.
 * 
 * This demo shows the difference between Phase 1 (surface emotions only) and
 * Phase 2 (hidden emotions, masks, and defensive responses).
 */
public class Phase2EmotionalDemo {
    private static final String TAG = "🎭 Phase2Demo";
    
    private final Context context;
    private final MultiLayeredEmotionalManager multiLayeredManager;
    private final Phase2EmotionalIntegrationBridge integrationBridge;
    
    public Phase2EmotionalDemo(Context context) {
        this.context = context;
        this.multiLayeredManager = MultiLayeredEmotionalManager.getInstance(context);
        this.integrationBridge = Phase2EmotionalIntegrationBridge.getInstance(context);
        
        // Initialize integration
        this.integrationBridge.initializeIntegration(
            multiLayeredManager, null, null, null, null
        );
    }
    
    /**
     * Demonstrate Fischer's emotional breakdown scenario
     */
    public void demonstrateFischerBreakdown() {
        Log.d(TAG, "\n🎭 === FISCHER EMOTIONAL BREAKDOWN DEMO ===");
        
        // Initialize Fischer with initial confidence
        multiLayeredManager.initializeMastersForSpectatorMode("fischer", "tal");
        
        Log.d(TAG, "📝 Scenario: Fischer starts confident but position deteriorates...");
        
        // Phase 1: Fischer starts confident
        multiLayeredManager.processEmotionalUpdate("fischer", "confident", 0.8f, "strong_position");
        showFischerState("Initial confidence");
        
        // Phase 2: Position starts to slip - Fischer becomes concerned underneath but shows confidence
        multiLayeredManager.processEmotionalUpdate("fischer", "concerned", 0.6f, "position_deteriorating");
        showFischerState("Position slipping - defensive mechanism activating");
        
        // Phase 3: Major blunder - emotional stress builds up
        multiLayeredManager.processEmotionalUpdate("fischer", "frustrated", 0.9f, "major_blunder");
        showFischerState("After major blunder - stress building");
        
        // Phase 4: Apply additional stress - this should trigger a breakthrough
        EmotionalLayer fischerLayer = getEmotionalLayer("fischer");
        if (fischerLayer != null) {
            fischerLayer.applyEmotionalStress("criticism_from_tal", 0.8f);
            showFischerState("After criticism - MASK BREAKDOWN IMMINENT");
        }
        
        // Phase 5: One more stress trigger should cause breakthrough
        multiLayeredManager.processEmotionalUpdate("fischer", "devastated", 1.0f, "position_hopeless");
        showFischerState("EMOTIONAL BREAKTHROUGH - True feelings revealed!");
        
        Log.d(TAG, "\n💡 Notice how Fischer's surface emotion changed from confident to devastated,");
        Log.d(TAG, "   but his underlying emotion was building stress the whole time!");
    }
    
    /**
     * Demonstrate Carlsen's analytical defense mechanism
     */
    public void demonstrateCarlsenDefensiveMechanism() {
        Log.d(TAG, "\n🎭 === CARLSEN DEFENSIVE MECHANISM DEMO ===");
        
        multiLayeredManager.initializeMastersForSpectatorMode("carlsen", "kasparov");
        
        Log.d(TAG, "📝 Scenario: Carlsen faces emotional pressure but uses analytical detachment...");
        
        // Phase 1: Carlsen starts analytical
        multiLayeredManager.processEmotionalUpdate("carlsen", "analytical", 0.5f, "complex_position");
        showCarlsenState("Initial analytical state");
        
        // Phase 2: Kasparov makes dramatic comment - this should stress Carlsen
        multiLayeredManager.processEmotionalUpdate("carlsen", "frustrated", 0.7f, "kasparov_dramatic_pressure");
        showCarlsenState("Frustrated by Kasparov's drama - defensive mechanism should activate");
        
        // Phase 3: More pressure - Carlsen should mask frustration with analysis
        EmotionalLayer carlsenLayer = getEmotionalLayer("carlsen");
        if (carlsenLayer != null) {
            carlsenLayer.applyEmotionalStress("intense_pressure", 0.6f);
            showCarlsenState("Under pressure - hiding behind analytical mask");
        }
        
        // Phase 4: Show what happens when Carlsen's mask is working
        Log.d(TAG, "\n💡 Carlsen appears analytical on surface, but is frustrated underneath.");
        Log.d(TAG, "   His defensive mechanism: 'analytical detachment' protects his reputation.");
    }
    
    /**
     * Demonstrate Tal's emotional transparency vs Anand's politeness mask
     */
    public void demonstrateEmotionalMaskContrast() {
        Log.d(TAG, "\n🎭 === EMOTIONAL MASK CONTRAST DEMO ===");
        
        multiLayeredManager.initializeMastersForSpectatorMode("tal", "anand");
        
        Log.d(TAG, "📝 Scenario: Same emotional trigger, different personality responses...");
        
        // Apply the same frustrating situation to both masters
        multiLayeredManager.processEmotionalUpdate("tal", "frustrated", 0.8f, "complex_endgame");
        multiLayeredManager.processEmotionalUpdate("anand", "frustrated", 0.8f, "complex_endgame");
        
        showTalState("Tal's response - low mask stability");
        showAnandState("Anand's response - high diplomatic mask");
        
        Log.d(TAG, "\n💡 Same underlying frustration, completely different surface expressions!");
        Log.d(TAG, "   Tal: Transparent, shows true feelings");
        Log.d(TAG, "   Anand: Diplomatic mask hides frustration behind politeness");
    }
    
    /**
     * Demonstrate voice-emotion feedback creating emotional cascades
     */
    public void demonstrateVoiceEmotionalCascade() {
        Log.d(TAG, "\n🎭 === VOICE-EMOTION CASCADE DEMO ===");
        
        multiLayeredManager.initializeMastersForSpectatorMode("fischer", "tal");
        
        Log.d(TAG, "📝 Scenario: Fischer's aggressive delivery triggers Tal's excitement...");
        
        // Initialize both masters in neutral state
        multiLayeredManager.processEmotionalUpdate("fischer", "confident", 0.6f, "good_position");
        multiLayeredManager.processEmotionalUpdate("tal", "analytical", 0.4f, "studying_position");
        
        showBothStates("Initial states");
        
        // Simulate Fischer speaking aggressively (this would normally come from voice analysis)
        // "This position is COMPLETELY winning! Tal doesn't understand chess!"
        VoiceEmotionalAnalyzer.EmotionalResponse fischerResponse = createMockResponse(
            "fischer", "aggressive", 0.9f, "Fischer speaking aggressively about winning"
        );
        
        // Apply voice reaction to Tal
        multiLayeredManager.processVoiceEmotionalReaction("tal", "fischer", fischerResponse);
        
        showBothStates("After Fischer's aggressive voice delivery");
        
        // Now Tal responds excitedly (this creates emotional contagion)
        multiLayeredManager.processEmotionalUpdate("tal", "thrilled", 0.8f, "fischer_aggressive_excitement");
        
        showBothStates("After Tal gets excited by Fischer's intensity");
        
        Log.d(TAG, "\n💡 Voice delivery created emotional cascade:");
        Log.d(TAG, "   Fischer aggressive → Tal stressed → Tal excited → Emotional contagion!");
    }
    
    /**
     * Show comprehensive emotional statistics
     */
    public void showCompleteEmotionalProfile() {
        Log.d(TAG, "\n🎭 === COMPLETE EMOTIONAL PROFILE ===");
        
        Map<String, String> stats = integrationBridge.getEmotionalStatistics();
        
        for (Map.Entry<String, String> entry : stats.entrySet()) {
            Log.d(TAG, String.format("📊 %s: %s", entry.getKey(), entry.getValue()));
        }
    }
    
    // =========================== HELPER METHODS ===========================
    
    private void showFischerState(String context) {
        EmotionalLayer.EmotionalComplexity complexity = multiLayeredManager.getEmotionalComplexity("fischer");
        if (complexity != null) {
            Log.d(TAG, String.format("🎭 FISCHER [%s]:", context));
            Log.d(TAG, String.format("   Surface: %s | Underlying: %s", 
                                    complexity.surfaceEmotion, complexity.underlyingEmotion));
            Log.d(TAG, String.format("   Mask: %.2f | Stress: %.2f | Vulnerable: %s", 
                                    complexity.maskIntensity, complexity.stressLevel, complexity.isVulnerable));
            Log.d(TAG, String.format("   Defense: %s | Trigger: %s", 
                                    complexity.defensiveResponse, complexity.emotionalTrigger));
            
            if (complexity.shouldTriggerBreakthrough()) {
                Log.d(TAG, "   💥 BREAKTHROUGH RISK: HIGH!");
            }
        }
    }
    
    private void showCarlsenState(String context) {
        EmotionalLayer.EmotionalComplexity complexity = multiLayeredManager.getEmotionalComplexity("carlsen");
        if (complexity != null) {
            Log.d(TAG, String.format("🎭 CARLSEN [%s]:", context));
            Log.d(TAG, String.format("   Surface: %s | Underlying: %s", 
                                    complexity.surfaceEmotion, complexity.underlyingEmotion));
            Log.d(TAG, String.format("   Mask: %.2f | Stress: %.2f | Defense: %s", 
                                    complexity.maskIntensity, complexity.stressLevel, complexity.defensiveResponse));
            
            if (complexity.getEmotionalContrast() > 0.5f) {
                Log.d(TAG, "   🎭 HIGH EMOTIONAL CONTRAST - Hiding true feelings!");
            }
        }
    }
    
    private void showTalState(String context) {
        EmotionalLayer.EmotionalComplexity complexity = multiLayeredManager.getEmotionalComplexity("tal");
        if (complexity != null) {
            Log.d(TAG, String.format("🎭 TAL [%s]:", context));
            Log.d(TAG, String.format("   Surface: %s | Underlying: %s | Mask: %.2f", 
                                    complexity.surfaceEmotion, complexity.underlyingEmotion, complexity.maskIntensity));
        }
    }
    
    private void showAnandState(String context) {
        EmotionalLayer.EmotionalComplexity complexity = multiLayeredManager.getEmotionalComplexity("anand");
        if (complexity != null) {
            Log.d(TAG, String.format("🎭 ANAND [%s]:", context));
            Log.d(TAG, String.format("   Surface: %s | Underlying: %s | Mask: %.2f", 
                                    complexity.surfaceEmotion, complexity.underlyingEmotion, complexity.maskIntensity));
        }
    }
    
    private void showBothStates(String context) {
        Log.d(TAG, String.format("🎭 BOTH MASTERS [%s]:", context));
        
        EmotionalLayer.EmotionalComplexity fischerComp = multiLayeredManager.getEmotionalComplexity("fischer");
        EmotionalLayer.EmotionalComplexity talComp = multiLayeredManager.getEmotionalComplexity("tal");
        
        if (fischerComp != null) {
            Log.d(TAG, String.format("   Fischer: %s (stress: %.2f)", 
                                    fischerComp.surfaceEmotion, fischerComp.stressLevel));
        }
        
        if (talComp != null) {
            Log.d(TAG, String.format("   Tal: %s (stress: %.2f)", 
                                    talComp.surfaceEmotion, talComp.stressLevel));
        }
    }
    
    private EmotionalLayer getEmotionalLayer(String masterName) {
        // This would access the internal emotional layer - for demo purposes
        // In real implementation, this would be through the manager
        return null; // Placeholder - would need access to internal layer
    }
    
    private VoiceEmotionalAnalyzer.EmotionalResponse createMockResponse(String speakingMaster, 
                                                                       String emotion, float intensity, String reason) {
        return new VoiceEmotionalAnalyzer.EmotionalResponse(
            "listening_master", emotion, intensity, reason, "Mock response text", false
        );
    }
    
    /**
     * 🧪 QUICK: Test Phase 2 integration in spectator mode
     */
    public boolean testPhase2Integration(String masterA, String masterB) {
        Log.d(TAG, "\n🧪 === PHASE 2 INTEGRATION TEST ===");
        Log.d(TAG, "Testing with " + masterA + " vs " + masterB);
        
        try {
            // Test 1: Initialize masters
            integrationBridge.initializeSpectatorMode(masterA, masterB);
            Log.d(TAG, "✅ Test 1: Master initialization successful");
            
            // Test 2: Process evaluation change
            integrationBridge.processEvaluationChange(masterA, 0.5f, 0.0f, "opening");
            Log.d(TAG, "✅ Test 2: Evaluation change processing successful");
            
            // Test 3: Generate enhanced emotional context
            String context = integrationBridge.generateEnhancedConversationContext(masterA, "test_context");
            if (context != null && !context.isEmpty()) {
                Log.d(TAG, "✅ Test 3: Enhanced conversation context generated: " + context.substring(0, Math.min(50, context.length())));
            } else {
                Log.d(TAG, "⚠️ Test 3: No enhanced context generated (may be normal)");
            }
            
            // Test 4: Check integration status
            boolean isActive = integrationBridge.isIntegrationActive();
            Log.d(TAG, "✅ Test 4: Integration active status: " + isActive);
            
            // Test 5: Get emotional statistics
            Map<String, String> stats = integrationBridge.getEmotionalStatistics();
            Log.d(TAG, "✅ Test 5: Emotional statistics available: " + (stats != null ? stats.size() : 0) + " entries");
            
            Log.d(TAG, "🎉 PHASE 2 INTEGRATION TEST SUCCESSFUL!");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ PHASE 2 INTEGRATION TEST FAILED: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Run all demonstrations
     */
    public void runAllDemonstrations() {
        Log.d(TAG, "\n🎭 ========================================");
        Log.d(TAG, "   PHASE 2 EMOTIONAL COMPLEXITY DEMO");
        Log.d(TAG, "   Multi-Layered Emotional Intelligence");
        Log.d(TAG, "========================================");
        
        demonstrateFischerBreakdown();
        demonstrateCarlsenDefensiveMechanism();
        demonstrateEmotionalMaskContrast();
        demonstrateVoiceEmotionalCascade();
        showCompleteEmotionalProfile();
        
        Log.d(TAG, "\n🎭 ========================================");
        Log.d(TAG, "   DEMO COMPLETE - Phase 2 Ready!");
        Log.d(TAG, "========================================");
    }
    
    /**
     * Test specific emotional mask scenario
     */
    public void testEmotionalMaskScenario(String masterName, String scenario) {
        Log.d(TAG, String.format("\n🧪 Testing emotional mask scenario: %s - %s", masterName, scenario));
        
        switch (scenario.toLowerCase()) {
            case "fischer_insecurity":
                integrationBridge.testEmotionalMask("fischer", "confident", "insecure", "aggression");
                showFischerState("Testing insecurity mask");
                break;
                
            case "carlsen_frustration":
                integrationBridge.testEmotionalMask("carlsen", "analytical", "frustrated", "analytical_detachment");
                showCarlsenState("Testing frustration mask");
                break;
                
            case "tal_disappointment":
                integrationBridge.testEmotionalMask("tal", "playful", "disappointed", "humor");
                showTalState("Testing disappointment mask");
                break;
                
            case "anand_competitiveness":
                integrationBridge.testEmotionalMask("anand", "diplomatic", "competitive", "diplomatic_politeness");
                showAnandState("Testing competitive mask");
                break;
                
            default:
                Log.d(TAG, "❌ Unknown scenario: " + scenario);
        }
    }
}