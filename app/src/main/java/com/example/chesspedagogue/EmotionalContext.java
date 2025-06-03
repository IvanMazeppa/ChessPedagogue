package com.example.chesspedagogue;

import android.util.Log;

public class EmotionalContext {
    private static final String TAG = "EmotionalContext";
    
    private String currentMaster;
    private String otherMaster;
    private String currentMasterEmotion;
    private String otherMasterEmotion;
    private float currentMasterIntensity;
    private float otherMasterIntensity;
    private float currentMasterMomentum;
    private float otherMasterMomentum;
    private String lastSpeaker;
    private boolean isConversationActive;
    private long lastEmotionalUpdate;
    private String gamePhase;
    private int moveCount;
    
    public EmotionalContext(String masterA, String masterB) {
        this.currentMaster = masterA;
        this.otherMaster = masterB;
        this.currentMasterEmotion = "neutral";
        this.otherMasterEmotion = "neutral";
        this.currentMasterIntensity = 0.0f;
        this.otherMasterIntensity = 0.0f;
        this.currentMasterMomentum = 0.0f;
        this.otherMasterMomentum = 0.0f;
        this.lastSpeaker = null;
        this.isConversationActive = false;
        this.lastEmotionalUpdate = System.currentTimeMillis();
        this.gamePhase = "opening";
        this.moveCount = 0;
    }
    
    public void updateEmotionalState(String master, String emotion, float intensity, float momentum) {
        Log.d(TAG, String.format("🎭 Updating emotional state: %s -> %s (%.2f intensity, %.2f momentum)", 
            master, emotion, intensity, momentum));
            
        if (master.equals(currentMaster)) {
            this.currentMasterEmotion = emotion;
            this.currentMasterIntensity = intensity;
            this.currentMasterMomentum = momentum;
        } else if (master.equals(otherMaster)) {
            this.otherMasterEmotion = emotion;
            this.otherMasterIntensity = intensity;
            this.otherMasterMomentum = momentum;
        }
        
        this.lastEmotionalUpdate = System.currentTimeMillis();
        analyzeEmotionalDynamics();
    }
    
    public void switchPerspective() {
        String temp = currentMaster;
        currentMaster = otherMaster;
        otherMaster = temp;
        
        String tempEmotion = currentMasterEmotion;
        currentMasterEmotion = otherMasterEmotion;
        otherMasterEmotion = tempEmotion;
        
        float tempIntensity = currentMasterIntensity;
        currentMasterIntensity = otherMasterIntensity;
        otherMasterIntensity = tempIntensity;
        
        float tempMomentum = currentMasterMomentum;
        currentMasterMomentum = otherMasterMomentum;
        otherMasterMomentum = tempMomentum;
        
        Log.d(TAG, String.format("🔄 Switched perspective: now viewing from %s's perspective", currentMaster));
    }
    
    public void updateGameContext(int moveCount, String gamePhase) {
        this.moveCount = moveCount;
        this.gamePhase = gamePhase;
    }
    
    public void setLastSpeaker(String speaker) {
        this.lastSpeaker = speaker;
        this.isConversationActive = true;
    }
    
    private void analyzeEmotionalDynamics() {
        if (shouldTriggerEmergentBehavior()) {
            Log.d(TAG, String.format("🌟 EMERGENT BEHAVIOR DETECTED: %s (%s, %.2f) vs %s (%s, %.2f)", 
                currentMaster, currentMasterEmotion, currentMasterIntensity,
                otherMaster, otherMasterEmotion, otherMasterIntensity));
        }
    }
    
    public boolean shouldTriggerEmergentBehavior() {
        float combinedIntensity = currentMasterIntensity + otherMasterIntensity;
        boolean highEmotionalStates = combinedIntensity > 1.0f; // LOWERED from 1.5f to 1.0f
        boolean contrastingEmotions = areEmotionsContrasting();
        boolean recentUpdate = (System.currentTimeMillis() - lastEmotionalUpdate) < 8000; // INCREASED from 5000 to 8000
        boolean moderateEmotion = Math.max(currentMasterIntensity, otherMasterIntensity) > 0.6f; // NEW: Single master with moderate emotion
        
        return (highEmotionalStates && (contrastingEmotions || recentUpdate)) || 
               (moderateEmotion && contrastingEmotions); // NEW: More triggers for conversations
    }
    
    private boolean areEmotionsContrasting() {
        // Strong contrasts
        if ("confident".equals(currentMasterEmotion) && "devastated".equals(otherMasterEmotion)) return true;
        if ("devastated".equals(currentMasterEmotion) && "confident".equals(otherMasterEmotion)) return true;
        if ("ecstatic".equals(currentMasterEmotion) && "focused".equals(otherMasterEmotion)) return true;
        if ("focused".equals(currentMasterEmotion) && "ecstatic".equals(otherMasterEmotion)) return true;
        
        // ENHANCED: More contrasting combinations detected in logs
        if ("concerned".equals(currentMasterEmotion) && "frustrated".equals(otherMasterEmotion)) return true;
        if ("frustrated".equals(currentMasterEmotion) && "concerned".equals(otherMasterEmotion)) return true;
        if ("pleased".equals(currentMasterEmotion) && "uneasy".equals(otherMasterEmotion)) return true;
        if ("uneasy".equals(currentMasterEmotion) && "pleased".equals(otherMasterEmotion)) return true;
        if ("thrilled".equals(currentMasterEmotion) && "analytical".equals(otherMasterEmotion)) return true;
        if ("analytical".equals(currentMasterEmotion) && "thrilled".equals(otherMasterEmotion)) return true;
        if ("excited".equals(currentMasterEmotion) && "focused".equals(otherMasterEmotion)) return true;
        if ("focused".equals(currentMasterEmotion) && "excited".equals(otherMasterEmotion)) return true;
        
        return false;
    }
    
    public String generateContextualPrompt() {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append(String.format("Your opponent %s currently seems %s with %.0f%% intensity", 
            otherMaster, otherMasterEmotion, otherMasterIntensity * 100));
            
        if (otherMasterMomentum > 0.5f) {
            prompt.append(" and strong emotional momentum");
        } else if (otherMasterMomentum < 0.2f) {
            prompt.append(" but their emotion is fading");
        }
        
        if (lastSpeaker != null && !lastSpeaker.equals(currentMaster)) {
            prompt.append(String.format(". %s just spoke", lastSpeaker));
        }
        
        if (shouldTriggerEmergentBehavior()) {
            prompt.append(". React naturally to both the position and their emotional state");
            
            if (areEmotionsContrasting()) {
                prompt.append(" - there's clear emotional contrast between you both");
            }
        }
        
        return prompt.toString();
    }
    
    public EmotionalDynamics getEmotionalDynamics() {
        return new EmotionalDynamics(
            currentMaster, otherMaster,
            currentMasterEmotion, otherMasterEmotion,
            currentMasterIntensity, otherMasterIntensity,
            currentMasterMomentum, otherMasterMomentum,
            shouldTriggerEmergentBehavior(),
            areEmotionsContrasting()
        );
    }
    
    public static class EmotionalDynamics {
        public final String currentMaster;
        public final String otherMaster;
        public final String currentEmotion;
        public final String otherEmotion;
        public final float currentIntensity;
        public final float otherIntensity;
        public final float currentMomentum;
        public final float otherMomentum;
        public final boolean emergentBehaviorTriggered;
        public final boolean emotionsContrasting;
        
        public EmotionalDynamics(String currentMaster, String otherMaster,
                               String currentEmotion, String otherEmotion,
                               float currentIntensity, float otherIntensity,
                               float currentMomentum, float otherMomentum,
                               boolean emergentBehaviorTriggered, boolean emotionsContrasting) {
            this.currentMaster = currentMaster;
            this.otherMaster = otherMaster;
            this.currentEmotion = currentEmotion;
            this.otherEmotion = otherEmotion;
            this.currentIntensity = currentIntensity;
            this.otherIntensity = otherIntensity;
            this.currentMomentum = currentMomentum;
            this.otherMomentum = otherMomentum;
            this.emergentBehaviorTriggered = emergentBehaviorTriggered;
            this.emotionsContrasting = emotionsContrasting;
        }
    }
    
    public String getCurrentMaster() { return currentMaster; }
    public String getOtherMaster() { return otherMaster; }
    public String getCurrentMasterEmotion() { return currentMasterEmotion; }
    public String getOtherMasterEmotion() { return otherMasterEmotion; }
    public float getCurrentMasterIntensity() { return currentMasterIntensity; }
    public float getOtherMasterIntensity() { return otherMasterIntensity; }
    public boolean isConversationActive() { return isConversationActive; }
    public String getLastSpeaker() { return lastSpeaker; }
}