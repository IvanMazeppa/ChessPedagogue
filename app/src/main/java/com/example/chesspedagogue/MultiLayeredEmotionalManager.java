package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🎭 PHASE 2: MULTI-LAYERED EMOTIONAL COMPLEXITY MANAGER
 * 
 * Integrates the new EmotionalLayer system with existing emotional intelligence.
 * Manages surface vs underlying emotions, defensive mechanisms, and emotional
 * breakthroughs for authentic psychological complexity.
 * 
 * Features:
 * - Hidden emotions and defensive responses
 * - Personality-specific emotional masks
 * - Emotional breakthrough detection
 * - Integration with voice-emotion feedback
 * - Enhanced conversation generation with psychological depth
 */
public class MultiLayeredEmotionalManager {
    private static final String TAG = "🎭 MultiLayeredManager";
    private static MultiLayeredEmotionalManager instance;
    
    private final Context context;
    private final Handler mainHandler;
    
    // Integration with existing systems
    private EmotionalIntelligenceManager emotionalIntelligenceManager;
    private VoiceEmotionalAnalyzer voiceEmotionalAnalyzer;
    private EmotionalContext emotionalContext;
    
    // Multi-layered emotional state for each master
    private final Map<String, EmotionalLayer> masterEmotionalLayers;
    private final Map<String, EmotionalTrendTracker> emotionalTrends;
    
    // Callbacks for emotional events
    private final List<MultiLayeredEmotionalCallback> callbacks;
    
    // Recovery and equilibrium settings
    private static final float EMOTIONAL_RECOVERY_RATE = 0.05f;  // How fast stress reduces
    private static final long EQUILIBRIUM_UPDATE_INTERVAL = 2000; // 2 seconds
    
    public static MultiLayeredEmotionalManager getInstance(Context context) {
        if (instance == null) {
            instance = new MultiLayeredEmotionalManager(context);
        }
        return instance;
    }
    
    public MultiLayeredEmotionalManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.masterEmotionalLayers = new ConcurrentHashMap<>();
        this.emotionalTrends = new ConcurrentHashMap<>();
        this.callbacks = new ArrayList<>();
        
        // Start emotional equilibrium restoration timer
        startEmotionalEquilibriumTimer();
        
        Log.d(TAG, "🚀 Multi-Layered Emotional Manager initialized");
    }
    
    /**
     * Tracks emotional trends and patterns for breakthrough detection
     */
    public static class EmotionalTrendTracker {
        private final List<EmotionalSnapshot> recentEmotions;
        private static final int MAX_HISTORY = 20;
        private float stressBuildupRate = 0.0f;
        
        public EmotionalTrendTracker() {
            this.recentEmotions = new ArrayList<>();
        }
        
        public void addSnapshot(String surfaceEmotion, String underlyingEmotion, 
                              float stressLevel, float maskIntensity) {
            recentEmotions.add(0, new EmotionalSnapshot(surfaceEmotion, underlyingEmotion, 
                                                       stressLevel, maskIntensity));
            if (recentEmotions.size() > MAX_HISTORY) {
                recentEmotions.remove(recentEmotions.size() - 1);
            }
            
            // Calculate stress buildup rate
            calculateStressBuildupRate();
        }
        
        private void calculateStressBuildupRate() {
            if (recentEmotions.size() < 3) return;
            
            float recentStress = recentEmotions.get(0).stressLevel;
            float oldStress = recentEmotions.get(Math.min(5, recentEmotions.size() - 1)).stressLevel;
            
            stressBuildupRate = (recentStress - oldStress) / Math.min(5, recentEmotions.size());
        }
        
        public boolean isStressIncreasing() {
            return stressBuildupRate > 0.1f;
        }
        
        public float getStressBuildupRate() {
            return stressBuildupRate;
        }
        
        private static class EmotionalSnapshot {
            final String surfaceEmotion;
            final String underlyingEmotion;
            final float stressLevel;
            final float maskIntensity;
            final long timestamp;
            
            EmotionalSnapshot(String surfaceEmotion, String underlyingEmotion, 
                            float stressLevel, float maskIntensity) {
                this.surfaceEmotion = surfaceEmotion;
                this.underlyingEmotion = underlyingEmotion;
                this.stressLevel = stressLevel;
                this.maskIntensity = maskIntensity;
                this.timestamp = System.currentTimeMillis();
            }
        }
    }
    
    /**
     * Initialize emotional layers for spectator mode masters
     */
    public void initializeMastersForSpectatorMode(String masterA, String masterB) {
        masterEmotionalLayers.put(masterA, new EmotionalLayer(masterA, "analytical"));
        masterEmotionalLayers.put(masterB, new EmotionalLayer(masterB, "analytical"));
        
        emotionalTrends.put(masterA, new EmotionalTrendTracker());
        emotionalTrends.put(masterB, new EmotionalTrendTracker());
        
        Log.d(TAG, String.format("🎭 Initialized emotional layers for spectator mode: %s vs %s", 
                                masterA, masterB));
    }
    
    /**
     * Process emotional update with multi-layered complexity
     */
    public void processEmotionalUpdate(String masterName, String detectedEmotion, 
                                     float intensity, String trigger) {
        EmotionalLayer layer = masterEmotionalLayers.get(masterName);
        if (layer == null) {
            layer = new EmotionalLayer(masterName, detectedEmotion);
            masterEmotionalLayers.put(masterName, layer);
            emotionalTrends.put(masterName, new EmotionalTrendTracker());
        }
        
        // Update underlying emotion
        layer.updateUnderlyingEmotion(detectedEmotion, trigger);
        
        // Apply stress based on emotion intensity and type
        float stressImpact = calculateStressImpact(detectedEmotion, intensity, trigger);
        if (stressImpact > 0.1f) {
            layer.applyEmotionalStress(trigger, stressImpact);
        }
        
        // Track emotional trends
        EmotionalTrendTracker tracker = emotionalTrends.get(masterName);
        if (tracker != null) {
            EmotionalLayer.EmotionalComplexity complexity = layer.getCurrentComplexity();
            tracker.addSnapshot(complexity.surfaceEmotion, complexity.underlyingEmotion,
                              complexity.stressLevel, complexity.maskIntensity);
        }
        
        // Check for emotional breakthroughs
        checkForEmotionalBreakthroughs(masterName, layer);
        
        // Notify callbacks
        notifyEmotionalUpdate(masterName, layer.getCurrentComplexity());
        
        Log.d(TAG, String.format("🎭 Processed emotional update for %s: %s -> %s (stress: %.2f)", 
                                masterName, detectedEmotion, layer.getSurfaceEmotion(), layer.getStressLevel()));
    }
    
    /**
     * Calculate stress impact based on emotion type and intensity
     */
    private float calculateStressImpact(String emotion, float intensity, String trigger) {
        float baseStress = intensity * 0.3f; // Base stress from emotional intensity
        
        // Emotion-specific stress modifiers
        switch (emotion.toLowerCase()) {
            case "devastated":
            case "frustrated":
                return baseStress * 1.5f; // High stress emotions
                
            case "concerned":
            case "uneasy":
                return baseStress * 1.2f; // Moderate stress emotions
                
            case "excited":
            case "thrilled":
                return baseStress * 0.8f; // Positive emotions still create some stress
                
            case "analytical":
            case "focused":
                return baseStress * 0.5f; // Low stress emotions
                
            default:
                return baseStress;
        }
    }
    
    /**
     * Check for emotional breakthroughs and mask slippage
     */
    private void checkForEmotionalBreakthroughs(String masterName, EmotionalLayer layer) {
        EmotionalLayer.EmotionalComplexity complexity = layer.getCurrentComplexity();
        
        if (complexity.shouldTriggerBreakthrough()) {
            Log.d(TAG, String.format("💥 EMOTIONAL BREAKTHROUGH DETECTED: %s's mask is slipping!", masterName));
            
            // Notify about the breakthrough
            for (MultiLayeredEmotionalCallback callback : callbacks) {
                callback.onEmotionalBreakthrough(masterName, complexity);
            }
        }
        
        // Check for interesting emotional contrasts
        if (complexity.getEmotionalContrast() > 0.6f) {
            Log.d(TAG, String.format("🎭 High emotional contrast for %s: showing %s, feeling %s", 
                                    masterName, complexity.surfaceEmotion, complexity.underlyingEmotion));
            
            for (MultiLayeredEmotionalCallback callback : callbacks) {
                callback.onEmotionalContrast(masterName, complexity);
            }
        }
    }
    
    /**
     * Process voice emotional reactions with multi-layered complexity
     */
    public void processVoiceEmotionalReaction(String reactingMaster, String speakingMaster,
                                            VoiceEmotionalAnalyzer.EmotionalResponse response) {
        EmotionalLayer reactingLayer = masterEmotionalLayers.get(reactingMaster);
        if (reactingLayer == null) return;
        
        // Voice reactions can trigger stress depending on the reaction type
        String trigger = "voice_reaction_to_" + speakingMaster;
        float stressImpact = 0.0f;
        
        // Calculate stress based on reaction type and master personality
        if (response.responseIntensity > 0.7f) {
            stressImpact = 0.2f; // High intensity reactions create stress
        }
        
        // Master-specific voice reaction stress
        switch (reactingMaster.toLowerCase()) {
            case "fischer":
                if (response.triggeredEmotion.equals("competitive")) {
                    stressImpact += 0.3f; // Fischer gets stressed when competitive
                }
                break;
                
            case "tal":
                if (response.triggeredEmotion.equals("analytical")) {
                    stressImpact += 0.1f; // Tal slightly stressed by analytical talk
                }
                break;
                
            case "carlsen":
                if (response.triggeredEmotion.equals("dramatic")) {
                    stressImpact += 0.2f; // Carlsen stressed by drama
                }
                break;
        }
        
        if (stressImpact > 0.1f) {
            reactingLayer.applyEmotionalStress(trigger, stressImpact);
            
            Log.d(TAG, String.format("🎤 Voice reaction stress: %s reacting to %s (stress: %.2f)", 
                                    reactingMaster, speakingMaster, stressImpact));
        }
        
        // Update underlying emotion based on the reaction
        reactingLayer.updateUnderlyingEmotion(response.triggeredEmotion, trigger);
    }
    
    /**
     * Generate enhanced emotional context for Responses API
     */
    public String generateEnhancedEmotionalContext(String masterName) {
        EmotionalLayer layer = masterEmotionalLayers.get(masterName);
        if (layer == null) {
            return "You are feeling analytical and focused.";
        }
        
        EmotionalLayer.EmotionalComplexity complexity = layer.getCurrentComplexity();
        StringBuilder context = new StringBuilder();
        
        // Add the basic emotional complexity context
        context.append(complexity.generateEmotionalContext());
        
        // Add trend information
        EmotionalTrendTracker tracker = emotionalTrends.get(masterName);
        if (tracker != null && tracker.isStressIncreasing()) {
            context.append("Your stress has been building up over recent moves. ");
        }
        
        // Add breakthrough probability
        if (complexity.shouldTriggerBreakthrough()) {
            context.append("You're at risk of an emotional breakthrough - your true feelings might burst through. ");
        }
        
        // Add defensive mechanism context
        if (!complexity.defensiveResponse.equals("none")) {
            context.append(String.format("You're currently using %s as a coping mechanism. ", 
                                        complexity.defensiveResponse));
        }
        
        return context.toString();
    }
    
    /**
     * Get the current emotional state for conversation orchestration
     */
    public EmotionalLayer.EmotionalComplexity getEmotionalComplexity(String masterName) {
        EmotionalLayer layer = masterEmotionalLayers.get(masterName);
        return layer != null ? layer.getCurrentComplexity() : null;
    }
    
    /**
     * Force an emotional state (for testing or special scenarios)
     */
    public void forceEmotionalState(String masterName, String surfaceEmotion, 
                                  String underlyingEmotion, String defensiveResponse) {
        EmotionalLayer layer = masterEmotionalLayers.get(masterName);
        if (layer == null) {
            layer = new EmotionalLayer(masterName, surfaceEmotion);
            masterEmotionalLayers.put(masterName, layer);
        }
        
        layer.forceEmotionalMask(surfaceEmotion, underlyingEmotion, defensiveResponse);
        
        Log.d(TAG, String.format("🎭 Forced emotional state for %s: surface=%s, underlying=%s, defense=%s", 
                                masterName, surfaceEmotion, underlyingEmotion, defensiveResponse));
    }
    
    /**
     * Start the emotional equilibrium restoration timer
     */
    private void startEmotionalEquilibriumTimer() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // Restore emotional equilibrium for all masters
                for (EmotionalLayer layer : masterEmotionalLayers.values()) {
                    layer.restoreEmotionalEquilibrium(EMOTIONAL_RECOVERY_RATE);
                }
                
                // Schedule next update
                mainHandler.postDelayed(this, EQUILIBRIUM_UPDATE_INTERVAL);
            }
        });
    }
    
    /**
     * Integration points
     */
    public void setEmotionalIntelligenceManager(EmotionalIntelligenceManager manager) {
        this.emotionalIntelligenceManager = manager;
    }
    
    public void setVoiceEmotionalAnalyzer(VoiceEmotionalAnalyzer analyzer) {
        this.voiceEmotionalAnalyzer = analyzer;
    }
    
    public void setEmotionalContext(EmotionalContext context) {
        this.emotionalContext = context;
    }
    
    /**
     * Callback interface for multi-layered emotional events
     */
    public interface MultiLayeredEmotionalCallback {
        void onEmotionalBreakthrough(String masterName, EmotionalLayer.EmotionalComplexity complexity);
        void onEmotionalContrast(String masterName, EmotionalLayer.EmotionalComplexity complexity);
        void onEmotionalUpdate(String masterName, EmotionalLayer.EmotionalComplexity complexity);
        void onDefensiveMechanismActivated(String masterName, String mechanism, String trigger);
    }
    
    public void addCallback(MultiLayeredEmotionalCallback callback) {
        callbacks.add(callback);
    }
    
    public void removeCallback(MultiLayeredEmotionalCallback callback) {
        callbacks.remove(callback);
    }
    
    private void notifyEmotionalUpdate(String masterName, EmotionalLayer.EmotionalComplexity complexity) {
        for (MultiLayeredEmotionalCallback callback : callbacks) {
            try {
                callback.onEmotionalUpdate(masterName, complexity);
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in emotional update callback", e);
            }
        }
    }
    
    /**
     * Clear emotional state for new games
     */
    public void clearEmotionalState(String masterName) {
        masterEmotionalLayers.remove(masterName);
        emotionalTrends.remove(masterName);
        
        Log.d(TAG, "🧹 Cleared emotional state for " + masterName);
    }
    
    public void clearAllEmotionalStates() {
        masterEmotionalLayers.clear();
        emotionalTrends.clear();
        
        Log.d(TAG, "🧹 Cleared all emotional states");
    }
    
    /**
     * Get emotional statistics for debugging
     */
    public Map<String, String> getEmotionalStatistics() {
        Map<String, String> stats = new HashMap<>();
        
        for (Map.Entry<String, EmotionalLayer> entry : masterEmotionalLayers.entrySet()) {
            String masterName = entry.getKey();
            EmotionalLayer.EmotionalComplexity complexity = entry.getValue().getCurrentComplexity();
            
            stats.put(masterName + "_surface", complexity.surfaceEmotion);
            stats.put(masterName + "_underlying", complexity.underlyingEmotion);
            stats.put(masterName + "_stress", String.format("%.2f", complexity.stressLevel));
            stats.put(masterName + "_mask", String.format("%.2f", complexity.maskIntensity));
            stats.put(masterName + "_vulnerable", String.valueOf(complexity.isVulnerable));
        }
        
        return stats;
    }
}