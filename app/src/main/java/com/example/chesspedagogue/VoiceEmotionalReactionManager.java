package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🎭 REAL-TIME VOICE REACTION SYSTEM - PHASE 1 IMPLEMENTATION
 * 
 * Manages real-time emotional reactions from listening masters based on 
 * voice delivery cues. Integrates with existing conversation system to
 * create emergent voice-based emotional interactions.
 * 
 * This enables masters to interrupt, react, and respond to HOW other
 * masters speak, creating authentic emotional dynamics.
 */
public class VoiceEmotionalReactionManager implements VoiceEmotionalAnalyzer.VoiceEmotionalCallback {
    private static final String TAG = "🎭 VoiceReactionManager";
    private static VoiceEmotionalReactionManager instance;
    
    private final Context context;
    private final Handler mainHandler;
    private final Random random;
    
    // Integration with existing systems
    private EmotionalIntelligenceManager emotionalIntelligenceManager;
    private ElevenLabsTTSService ttsService;
    
    // Current active voice reactions
    private final Map<String, PendingVoiceReaction> pendingReactions;
    private final Map<String, Long> lastReactionTime;
    
    // Callbacks for integration with conversation orchestrators
    private final List<VoiceReactionCallback> callbacks;
    
    // Reaction timing and probability settings
    private static final long MIN_REACTION_INTERVAL_MS = 3000;  // 3 seconds between reactions per master
    private static final long REACTION_DELAY_RANGE_MS = 2000;   // 0-2 second random delay
    private static final float BASE_REACTION_PROBABILITY = 0.4f; // 40% base chance of reaction
    
    public static VoiceEmotionalReactionManager getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceEmotionalReactionManager(context);
        }
        return instance;
    }
    
    public VoiceEmotionalReactionManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.random = new Random();
        this.pendingReactions = new ConcurrentHashMap<>();
        this.lastReactionTime = new ConcurrentHashMap<>();
        this.callbacks = new ArrayList<>();
        
        Log.d(TAG, "🚀 Voice Emotional Reaction Manager initialized");
    }
    
    /**
     * Represents a pending voice reaction that will be triggered
     */
    public static class PendingVoiceReaction {
        public final String listeningMaster;
        public final String speakingMaster;
        public final VoiceEmotionalAnalyzer.EmotionalResponse response;
        public final VoiceEmotionalAnalyzer.EmotionalCue triggeringCue;
        public final long scheduledTime;
        public final String reactionText;
        public boolean hasBeenTriggered = false;
        
        public PendingVoiceReaction(String listeningMaster, String speakingMaster,
                                  VoiceEmotionalAnalyzer.EmotionalResponse response,
                                  VoiceEmotionalAnalyzer.EmotionalCue triggeringCue,
                                  long scheduledTime, String reactionText) {
            this.listeningMaster = listeningMaster;
            this.speakingMaster = speakingMaster;
            this.response = response;
            this.triggeringCue = triggeringCue;
            this.scheduledTime = scheduledTime;
            this.reactionText = reactionText;
        }
    }
    
    /**
     * Callback interface for voice emotional reactions
     */
    public interface VoiceReactionCallback {
        /**
         * Called when a master wants to react to voice delivery
         */
        void onVoiceEmotionalReaction(String reactingMaster, String targetMaster, 
                                    String emotionalReaction, String reactionText, 
                                    boolean shouldInterrupt);
        
        /**
         * Called when a master's emotional state should be updated due to voice cues
         */
        void onVoiceTriggeredEmotionalChange(String masterName, String newEmotion, 
                                           float intensity, String reason);
    }
    
    /**
     * Initialize with existing managers
     */
    public void initialize(EmotionalIntelligenceManager emotionalManager, ElevenLabsTTSService tts) {
        this.emotionalIntelligenceManager = emotionalManager;
        this.ttsService = tts;
        
        // Register for voice emotional callbacks
        if (tts != null) {
            tts.addVoiceEmotionalCallback(this);
        }
        
        Log.d(TAG, "✅ Initialized with emotional intelligence and TTS integration");
    }
    
    /**
     * Add a callback to receive voice reaction events
     */
    public void addVoiceReactionCallback(VoiceReactionCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            Log.d(TAG, "📝 Added voice reaction callback: " + callback.getClass().getSimpleName());
        }
    }
    
    /**
     * Remove a voice reaction callback
     */
    public void removeVoiceReactionCallback(VoiceReactionCallback callback) {
        callbacks.remove(callback);
        Log.d(TAG, "🗑️ Removed voice reaction callback: " + callback.getClass().getSimpleName());
    }
    
    /**
     * Implementation of VoiceEmotionalCallback - receives voice emotional cues
     */
    @Override
    public void onVoiceEmotionalCueDetected(VoiceEmotionalAnalyzer.EmotionalCue cue) {
        Log.d(TAG, String.format("🎤 Voice cue detected: %s (%s tone, intensity=%.2f)", 
                                cue.speakingMaster, cue.voiceChars.dominantTone, cue.emotionalIntensity));
        
        // Update speaker's emotional state based on their own voice delivery
        updateMasterEmotionalStateFromVoice(cue);
    }
    
    /**
     * Implementation of VoiceEmotionalCallback - receives emotional reactions
     */
    @Override
    public void onVoiceEmotionalReaction(String listeningMaster, VoiceEmotionalAnalyzer.EmotionalResponse response) {
        if (response == null) return;
        
        Log.d(TAG, String.format("🎭 Voice reaction: %s → %s (%s, intensity=%.2f)", 
                                response.listeningMaster, 
                                getCurrentSpeakingMaster(), 
                                response.triggeredEmotion, 
                                response.responseIntensity));
        
        // Decide whether to process this reaction
        if (shouldProcessReaction(response)) {
            scheduleVoiceReaction(response);
        }
    }
    
    /**
     * Update master's emotional state based on their own voice delivery
     */
    private void updateMasterEmotionalStateFromVoice(VoiceEmotionalAnalyzer.EmotionalCue cue) {
        try {
            // Update the master's emotional state through the emotional intelligence manager
            if (emotionalIntelligenceManager != null) {
                // Create a voice-based emotional update
                String reason = String.format("Voice delivery: %s tone", cue.voiceChars.dominantTone);
                
                // Notify callbacks about emotional state change
                for (VoiceReactionCallback callback : callbacks) {
                    try {
                        callback.onVoiceTriggeredEmotionalChange(
                            cue.speakingMaster, 
                            cue.detectedEmotion, 
                            cue.emotionalIntensity, 
                            reason
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error in voice triggered emotional change callback", e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating master emotional state from voice", e);
        }
    }
    
    /**
     * Determine if this reaction should be processed
     */
    private boolean shouldProcessReaction(VoiceEmotionalAnalyzer.EmotionalResponse response) {
        String masterKey = response.listeningMaster;
        long currentTime = System.currentTimeMillis();
        
        // Check minimum interval between reactions
        Long lastReaction = lastReactionTime.get(masterKey);
        if (lastReaction != null && (currentTime - lastReaction) < MIN_REACTION_INTERVAL_MS) {
            Log.d(TAG, String.format("⏰ %s reaction too soon (%.1fs ago)", 
                                   masterKey, (currentTime - lastReaction) / 1000.0f));
            return false;
        }
        
        // Calculate reaction probability based on intensity and master personality
        float reactionProbability = calculateReactionProbability(response);
        boolean shouldReact = random.nextFloat() < reactionProbability;
        
        Log.d(TAG, String.format("🎲 %s reaction probability: %.2f, decided: %s", 
                                masterKey, reactionProbability, shouldReact ? "REACT" : "SKIP"));
        
        return shouldReact;
    }
    
    /**
     * Calculate probability of reaction based on response characteristics
     */
    private float calculateReactionProbability(VoiceEmotionalAnalyzer.EmotionalResponse response) {
        float probability = BASE_REACTION_PROBABILITY;
        
        // Increase probability based on response intensity
        probability += response.responseIntensity * 0.3f;
        
        // Increase probability for interruption-worthy responses
        if (response.shouldInterrupt) {
            probability += 0.2f;
        }
        
        // Master-specific reaction tendencies
        switch (response.listeningMaster.toLowerCase()) {
            case "tal":
                probability += 0.3f; // Tal is highly reactive
                break;
            case "fischer":
                probability += 0.2f; // Fischer reacts strongly to challenges
                break;
            case "kasparov":
                probability += 0.1f; // Kasparov is passionate but controlled
                break;
            case "anand":
                probability -= 0.1f; // Anand is more diplomatic
                break;
            case "carlsen":
                probability -= 0.2f; // Carlsen is typically calm
                break;
            case "karpov":
                probability -= 0.3f; // Karpov is very controlled
                break;
        }
        
        // Cap probability
        return Math.min(Math.max(probability, 0.05f), 0.8f);
    }
    
    /**
     * Schedule a voice reaction to be triggered
     */
    private void scheduleVoiceReaction(VoiceEmotionalAnalyzer.EmotionalResponse response) {
        try {
            // Generate reaction text
            String reactionText = generateReactionText(response);
            
            // Calculate delay (immediate for interruptions, delayed for others)
            long delay = response.shouldInterrupt ? 
                random.nextInt(500) : // 0-500ms for interruptions
                random.nextInt((int)REACTION_DELAY_RANGE_MS); // 0-2s for normal reactions
            
            long scheduledTime = System.currentTimeMillis() + delay;
            
            // Create pending reaction
            PendingVoiceReaction pendingReaction = new PendingVoiceReaction(
                response.listeningMaster,
                getCurrentSpeakingMaster(),
                response,
                null, // We don't have the original cue here
                scheduledTime,
                reactionText
            );
            
            // Store and schedule
            String reactionKey = response.listeningMaster + "_" + System.currentTimeMillis();
            pendingReactions.put(reactionKey, pendingReaction);
            
            // Schedule execution
            mainHandler.postDelayed(() -> {
                executeVoiceReaction(reactionKey);
            }, delay);
            
            Log.d(TAG, String.format("⏰ Scheduled %s reaction in %dms: %s", 
                                   response.listeningMaster, delay, reactionText));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error scheduling voice reaction", e);
        }
    }
    
    /**
     * Execute a scheduled voice reaction
     */
    private void executeVoiceReaction(String reactionKey) {
        PendingVoiceReaction reaction = pendingReactions.remove(reactionKey);
        if (reaction == null || reaction.hasBeenTriggered) {
            return;
        }
        
        reaction.hasBeenTriggered = true;
        
        // Update last reaction time
        lastReactionTime.put(reaction.listeningMaster, System.currentTimeMillis());
        
        // Notify all callbacks
        for (VoiceReactionCallback callback : callbacks) {
            try {
                callback.onVoiceEmotionalReaction(
                    reaction.listeningMaster,
                    reaction.speakingMaster,
                    reaction.response.triggeredEmotion,
                    reaction.reactionText,
                    reaction.response.shouldInterrupt
                );
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in voice reaction callback", e);
            }
        }
        
        Log.d(TAG, String.format("🎭 Executed voice reaction: %s reacted to %s with '%s'", 
                                reaction.listeningMaster, reaction.speakingMaster, reaction.reactionText));
    }
    
    /**
     * Generate contextual reaction text based on the emotional response
     */
    private String generateReactionText(VoiceEmotionalAnalyzer.EmotionalResponse response) {
        String masterName = response.listeningMaster.toLowerCase();
        String emotion = response.triggeredEmotion;
        
        // Master-specific reaction patterns
        Map<String, String[]> masterReactions = getMasterReactionTemplates(masterName);
        
        if (masterReactions.containsKey(emotion)) {
            String[] templates = masterReactions.get(emotion);
            return templates[random.nextInt(templates.length)];
        }
        
        // Use suggested response from the analyzer
        if (response.suggestedResponse != null && !response.suggestedResponse.isEmpty()) {
            return response.suggestedResponse;
        }
        
        // Fallback emotional reactions
        switch (emotion) {
            case "thrilled": return "This is exciting!";
            case "competitive": return "I disagree with that approach.";
            case "analytical": return "Let me think about this differently.";
            case "philosophical": return "There's more to consider here.";
            case "impressed": return "That's quite interesting.";
            case "intrigued": return "Tell me more about that.";
            case "focused": return "We need to be more precise.";
            default: return "Hmm, interesting point.";
        }
    }
    
    /**
     * Get master-specific reaction templates
     */
    private Map<String, String[]> getMasterReactionTemplates(String masterName) {
        Map<String, String[]> reactions = new HashMap<>();
        
        switch (masterName) {
            case "tal":
                reactions.put("thrilled", new String[]{
                    "Yes! This is the kind of magic I love to see!",
                    "Beautiful! The pieces are dancing!",
                    "This is why we play chess!"
                });
                reactions.put("playful", new String[]{
                    "Why be so serious? Let's have some fun!",
                    "Chess is art, not just calculation!",
                    "Feel the position, don't just analyze it!"
                });
                break;
                
            case "fischer":
                reactions.put("competitive", new String[]{
                    "That's not the strongest continuation.",
                    "I would handle this completely differently.",
                    "There's a better way to approach this position."
                });
                reactions.put("confident", new String[]{
                    "The correct move is obvious.",
                    "This position demands precision.",
                    "Only one move makes sense here."
                });
                break;
                
            case "carlsen":
                reactions.put("analytical", new String[]{
                    "Let's examine this more carefully.",
                    "I think there's a more accurate approach.",
                    "The position requires deeper calculation."
                });
                reactions.put("focused", new String[]{
                    "We need to stay objective here.",
                    "Let's focus on what the position demands.",
                    "Practical considerations are important."
                });
                break;
                
            case "anand":
                reactions.put("philosophical", new String[]{
                    "Both perspectives have merit.",
                    "It's interesting how different styles approach this.",
                    "There's wisdom in that view."
                });
                reactions.put("pleased", new String[]{
                    "I appreciate that insight.",
                    "That's a thoughtful observation.",
                    "Excellent point about the position."
                });
                break;
                
            case "kasparov":
                reactions.put("passionate", new String[]{
                    "This is what chess is truly about!",
                    "The position demands everything from us!",
                    "We must fight for every advantage!"
                });
                reactions.put("competitive", new String[]{
                    "I challenge that assessment.",
                    "The truth of the position is different.",
                    "Dynamic play is required here!"
                });
                break;
        }
        
        return reactions;
    }
    
    /**
     * Get the current speaking master (placeholder - should be integrated with conversation system)
     */
    private String getCurrentSpeakingMaster() {
        // This should be integrated with the actual conversation orchestrator
        return "unknown";
    }
    
    /**
     * Clear all pending reactions (useful when starting new conversations)
     */
    public void clearPendingReactions() {
        pendingReactions.clear();
        lastReactionTime.clear();
        Log.d(TAG, "🧹 Cleared all pending voice reactions");
    }
    
    /**
     * Clear reactions for a specific master
     */
    public void clearMasterReactions(String masterName) {
        pendingReactions.entrySet().removeIf(entry -> 
            entry.getValue().listeningMaster.equals(masterName));
        lastReactionTime.remove(masterName);
        Log.d(TAG, "🧹 Cleared voice reactions for " + masterName);
    }
    
    /**
     * Get reaction statistics for debugging
     */
    public Map<String, Object> getReactionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending_reactions", pendingReactions.size());
        stats.put("masters_with_recent_reactions", lastReactionTime.size());
        stats.put("active_callbacks", callbacks.size());
        return stats;
    }
}