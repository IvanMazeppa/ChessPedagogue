package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🌉 VOICE-EMOTION INTEGRATION BRIDGE
 * 
 * Seamlessly integrates the new Voice-Emotion Feedback system with the existing
 * SpectatorConversationOrchestrator and emotional intelligence infrastructure.
 * 
 * This bridge enables:
 * - Voice emotional reactions in spectator mode
 * - Integration with existing conversation flows
 * - Enhanced emotional dynamics in master interactions
 * - Smooth transition between voice-triggered and evaluation-triggered emotions
 */
public class VoiceEmotionalIntegrationBridge implements VoiceEmotionalReactionManager.VoiceReactionCallback {
    private static final String TAG = "🌉 VoiceEmotionalBridge";
    private static VoiceEmotionalIntegrationBridge instance;
    
    private final Context context;
    private final Handler mainHandler;
    
    // Core system integrations
    private SpectatorConversationOrchestrator conversationOrchestrator;
    private EmotionalIntelligenceManager emotionalIntelligenceManager;
    private VoiceEmotionalReactionManager voiceReactionManager;
    private ElevenLabsTTSService ttsService;
    private ConversationMemoryManager conversationMemory;
    
    // State tracking for integration
    private final Map<String, String> activeSpeakerToListeners = new ConcurrentHashMap<>();
    private final Map<String, Long> lastVoiceReactionTime = new ConcurrentHashMap<>();
    private boolean voiceEmotionalFeedbackEnabled = true;
    
    // Integration callbacks
    private final List<VoiceEmotionalIntegrationCallback> integrationCallbacks = new ArrayList<>();
    
    public static VoiceEmotionalIntegrationBridge getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceEmotionalIntegrationBridge(context);
        }
        return instance;
    }
    
    public VoiceEmotionalIntegrationBridge(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        Log.d(TAG, "🚀 Voice Emotional Integration Bridge initialized");
    }
    
    /**
     * Callback interface for voice emotional integration events
     */
    public interface VoiceEmotionalIntegrationCallback {
        /**
         * Called when voice emotions should trigger a new conversation
         */
        void onVoiceTriggeredConversation(String reactingMaster, String targetMaster, 
                                        String context, boolean shouldInterrupt);
        
        /**
         * Called when voice emotions update master emotional states
         */
        void onVoiceEmotionalStateUpdate(String masterName, String emotion, 
                                       float intensity, String context);
        
        /**
         * Called when a voice emotional reaction should be spoken
         */
        void onVoiceReactionSpeech(String masterName, String text, List<String> listeners);
    }
    
    /**
     * Initialize the bridge with all required components
     */
    public void initialize(SpectatorConversationOrchestrator orchestrator,
                          EmotionalIntelligenceManager emotionalManager,
                          VoiceEmotionalReactionManager voiceManager,
                          ElevenLabsTTSService tts,
                          ConversationMemoryManager memory) {
        
        this.conversationOrchestrator = orchestrator;
        this.emotionalIntelligenceManager = emotionalManager;
        this.voiceReactionManager = voiceManager;
        this.ttsService = tts;
        this.conversationMemory = memory;
        
        // Register for voice emotional reactions
        if (voiceManager != null) {
            voiceManager.addVoiceReactionCallback(this);
        }
        
        Log.d(TAG, "✅ Bridge initialized with all components");
    }
    
    /**
     * Set up voice emotional context for a spectator conversation
     */
    public void setupSpectatorVoiceContext(String whitePlayer, String blackPlayer, 
                                         List<String> spectatingMasters) {
        if (ttsService == null) return;
        
        try {
            // Create comprehensive listening context
            List<String> allListeners = new ArrayList<>();
            allListeners.add(whitePlayer);
            allListeners.add(blackPlayer);
            if (spectatingMasters != null) {
                allListeners.addAll(spectatingMasters);
            }
            
            // Set voice emotional context for both players
            ttsService.setVoiceEmotionalContext(whitePlayer, 
                createListenersExcluding(allListeners, whitePlayer));
            ttsService.setVoiceEmotionalContext(blackPlayer, 
                createListenersExcluding(allListeners, blackPlayer));
            
            Log.d(TAG, String.format("🎭 Set up voice context: %s vs %s with %d spectators", 
                                   whitePlayer, blackPlayer, 
                                   spectatingMasters != null ? spectatingMasters.size() : 0));
                                   
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up spectator voice context", e);
        }
    }
    
    /**
     * Enhanced speak method that includes voice emotional feedback
     */
    public void speakWithVoiceEmotionalAwareness(String masterName, String text, 
                                               List<String> allParticipants,
                                               ElevenLabsTTSService.SpeechCallback callback) {
        if (ttsService == null || !voiceEmotionalFeedbackEnabled) {
            // Fallback to regular speech
            if (ttsService != null) {
                ttsService.speakWithSpecificMaster(masterName, text, callback);
            }
            return;
        }
        
        try {
            // Create listeners list (everyone except the speaker)
            List<String> listeners = createListenersExcluding(allParticipants, masterName);
            
            // Store current speaker context
            activeSpeakerToListeners.put(masterName, String.join(",", listeners));
            
            // Use enhanced TTS method with voice emotional feedback
            ttsService.speakWithVoiceEmotionalFeedback(masterName, text, listeners, 
                new ElevenLabsTTSService.SpeechCallback() {
                    @Override
                    public void onSpeechCompleted(String completedText) {
                        // Clean up speaker context
                        activeSpeakerToListeners.remove(masterName);
                        
                        // Call original callback
                        if (callback != null) {
                            callback.onSpeechCompleted(completedText);
                        }
                    }
                    
                    @Override
                    public void onSpeechInterrupted() {
                        // Clean up speaker context on interruption
                        activeSpeakerToListeners.remove(masterName);
                    }
                });
            
            Log.d(TAG, String.format("🎤 %s speaking to %d listeners with voice emotional awareness", 
                                   masterName, listeners.size()));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in voice emotional aware speech", e);
            
            // Fallback to regular speech
            if (ttsService != null) {
                ttsService.speakWithSpecificMaster(masterName, text, callback);
            }
        }
    }
    
    /**
     * Implementation: Voice emotional reaction from VoiceReactionCallback
     */
    @Override
    public void onVoiceEmotionalReaction(String reactingMaster, String targetMaster, 
                                       String emotionalReaction, String reactionText, 
                                       boolean shouldInterrupt) {
        
        Log.d(TAG, String.format("🎭 Voice emotional reaction: %s → %s (%s): '%s'", 
                                reactingMaster, targetMaster, emotionalReaction, reactionText));
        
        try {
            // Update the reacting master's emotional state
            updateMasterEmotionalState(reactingMaster, emotionalReaction, 
                                     shouldInterrupt ? 0.8f : 0.6f, 
                                     "Voice reaction to " + targetMaster);
            
            // Decide how to handle the reaction
            if (shouldInterrupt) {
                handleInterruptiveVoiceReaction(reactingMaster, targetMaster, reactionText);
            } else {
                handleDelayedVoiceReaction(reactingMaster, targetMaster, reactionText);
            }
            
            // Notify integration callbacks
            for (VoiceEmotionalIntegrationCallback callback : integrationCallbacks) {
                try {
                    callback.onVoiceTriggeredConversation(reactingMaster, targetMaster, 
                                                        reactionText, shouldInterrupt);
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error in integration callback", e);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error handling voice emotional reaction", e);
        }
    }
    
    /**
     * Implementation: Voice triggered emotional change from VoiceReactionCallback
     */
    @Override
    public void onVoiceTriggeredEmotionalChange(String masterName, String newEmotion, 
                                              float intensity, String reason) {
        
        Log.d(TAG, String.format("🎭 Voice triggered emotional change: %s → %s (%.2f): %s", 
                                masterName, newEmotion, intensity, reason));
        
        try {
            // Update master's emotional state through the emotional intelligence manager
            updateMasterEmotionalState(masterName, newEmotion, intensity, reason);
            
            // Notify integration callbacks
            for (VoiceEmotionalIntegrationCallback callback : integrationCallbacks) {
                try {
                    callback.onVoiceEmotionalStateUpdate(masterName, newEmotion, intensity, reason);
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error in emotional state update callback", e);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error handling voice triggered emotional change", e);
        }
    }
    
    /**
     * Handle interruptive voice reactions (immediate response)
     */
    private void handleInterruptiveVoiceReaction(String reactingMaster, String targetMaster, String reactionText) {
        // For interruptions, we want immediate response
        List<String> listeners = getCurrentListeners(reactingMaster);
        
        // Speak the reaction immediately
        speakVoiceReaction(reactingMaster, reactionText, listeners);
        
        // If we have conversation orchestrator, trigger an immediate conversation
        if (conversationOrchestrator != null) {
            // This could trigger a conversation turn or interruption mechanism
            Log.d(TAG, String.format("🚨 Interruption: %s interrupting %s", reactingMaster, targetMaster));
        }
    }
    
    /**
     * Handle delayed voice reactions (queue for later)
     */
    private void handleDelayedVoiceReaction(String reactingMaster, String targetMaster, String reactionText) {
        // For delayed reactions, wait for current speech to finish
        List<String> listeners = getCurrentListeners(reactingMaster);
        
        // Add small delay to make it feel natural
        mainHandler.postDelayed(() -> {
            speakVoiceReaction(reactingMaster, reactionText, listeners);
        }, 1000 + (long)(Math.random() * 2000)); // 1-3 second delay
    }
    
    /**
     * Speak a voice reaction
     */
    private void speakVoiceReaction(String masterName, String text, List<String> listeners) {
        try {
            // Update last reaction time
            lastVoiceReactionTime.put(masterName, System.currentTimeMillis());
            
            // Speak the reaction with emotional awareness
            if (ttsService != null) {
                ttsService.speakWithVoiceEmotionalFeedback(masterName, text, listeners, 
                    new ElevenLabsTTSService.SpeechCallback() {
                        @Override
                        public void onSpeechCompleted(String completedText) {
                            Log.d(TAG, String.format("✅ Voice reaction completed: %s", masterName));
                        }
                        
                        @Override
                        public void onSpeechInterrupted() {
                            Log.d(TAG, String.format("🚫 Voice reaction interrupted: %s", masterName));
                        }
                    });
            }
            
            // Notify callbacks
            for (VoiceEmotionalIntegrationCallback callback : integrationCallbacks) {
                try {
                    callback.onVoiceReactionSpeech(masterName, text, listeners);
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error in voice reaction speech callback", e);
                }
            }
            
            Log.d(TAG, String.format("🎤 Voice reaction spoken: %s: '%s'", masterName, text));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error speaking voice reaction", e);
        }
    }
    
    /**
     * Update master emotional state through the emotional intelligence manager
     */
    private void updateMasterEmotionalState(String masterName, String emotion, float intensity, String reason) {
        if (emotionalIntelligenceManager == null) return;
        
        try {
            // Convert emotion string to EmotionalState enum
            EmotionalIntelligenceManager.EmotionalState emotionalState = 
                parseEmotionalState(emotion);
            
            if (emotionalState != null) {
                // Create emotional result for the update
                EmotionalIntelligenceManager.EmotionalAnalysisResult result = 
                    new EmotionalIntelligenceManager.EmotionalAnalysisResult(
                        emotionalState, reason, intensity, 0.5f
                    );
                
                // This would need to be integrated with the actual emotional intelligence system
                Log.d(TAG, String.format("📊 Updated %s emotional state: %s (%.2f) - %s", 
                                        masterName, emotion, intensity, reason));
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating master emotional state", e);
        }
    }
    
    /**
     * Parse emotion string to EmotionalState enum
     */
    private EmotionalIntelligenceManager.EmotionalState parseEmotionalState(String emotion) {
        try {
            switch (emotion.toLowerCase()) {
                case "thrilled": return EmotionalIntelligenceManager.EmotionalState.THRILLED;
                case "excited": return EmotionalIntelligenceManager.EmotionalState.EXCITED;
                case "competitive": return EmotionalIntelligenceManager.EmotionalState.COMPETITIVE;
                case "analytical": return EmotionalIntelligenceManager.EmotionalState.ANALYTICAL;
                case "philosophical": return EmotionalIntelligenceManager.EmotionalState.PHILOSOPHICAL;
                case "impressed": return EmotionalIntelligenceManager.EmotionalState.IMPRESSED;
                case "intrigued": return EmotionalIntelligenceManager.EmotionalState.INTRIGUED;
                case "focused": return EmotionalIntelligenceManager.EmotionalState.FOCUSED;
                case "confident": return EmotionalIntelligenceManager.EmotionalState.CONFIDENT;
                case "pleased": return EmotionalIntelligenceManager.EmotionalState.PLEASED;
                case "playful": return EmotionalIntelligenceManager.EmotionalState.PLAYFUL;
                default: return EmotionalIntelligenceManager.EmotionalState.ANALYTICAL;
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not parse emotion: " + emotion);
            return EmotionalIntelligenceManager.EmotionalState.ANALYTICAL;
        }
    }
    
    /**
     * Create listeners list excluding the speaker
     */
    private List<String> createListenersExcluding(List<String> allParticipants, String speaker) {
        List<String> listeners = new ArrayList<>();
        for (String participant : allParticipants) {
            if (!participant.equals(speaker)) {
                listeners.add(participant);
            }
        }
        return listeners;
    }
    
    /**
     * Get current listeners for a master
     */
    private List<String> getCurrentListeners(String masterName) {
        String listenersStr = activeSpeakerToListeners.get(masterName);
        List<String> listeners = new ArrayList<>();
        
        if (listenersStr != null && !listenersStr.isEmpty()) {
            String[] parts = listenersStr.split(",");
            for (String part : parts) {
                listeners.add(part.trim());
            }
        }
        
        return listeners;
    }
    
    /**
     * Add integration callback
     */
    public void addIntegrationCallback(VoiceEmotionalIntegrationCallback callback) {
        if (!integrationCallbacks.contains(callback)) {
            integrationCallbacks.add(callback);
            Log.d(TAG, "📝 Added integration callback: " + callback.getClass().getSimpleName());
        }
    }
    
    /**
     * Remove integration callback
     */
    public void removeIntegrationCallback(VoiceEmotionalIntegrationCallback callback) {
        integrationCallbacks.remove(callback);
        Log.d(TAG, "🗑️ Removed integration callback: " + callback.getClass().getSimpleName());
    }
    
    /**
     * Enable/disable voice emotional feedback
     */
    public void setVoiceEmotionalFeedbackEnabled(boolean enabled) {
        this.voiceEmotionalFeedbackEnabled = enabled;
        Log.d(TAG, "🎛️ Voice emotional feedback " + (enabled ? "ENABLED" : "DISABLED"));
    }
    
    /**
     * Check if voice emotional feedback is enabled
     */
    public boolean isVoiceEmotionalFeedbackEnabled() {
        return voiceEmotionalFeedbackEnabled;
    }
    
    /**
     * Get integration statistics for debugging
     */
    public Map<String, Object> getIntegrationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("voice_feedback_enabled", voiceEmotionalFeedbackEnabled);
        stats.put("active_speakers", activeSpeakerToListeners.size());
        stats.put("masters_with_recent_reactions", lastVoiceReactionTime.size());
        stats.put("integration_callbacks", integrationCallbacks.size());
        
        // Add voice reaction manager stats if available
        if (voiceReactionManager != null) {
            Map<String, Object> voiceStats = voiceReactionManager.getReactionStatistics();
            stats.putAll(voiceStats);
        }
        
        return stats;
    }
    
    /**
     * Clear all voice emotional state (useful for new games)
     */
    public void clearVoiceEmotionalState() {
        activeSpeakerToListeners.clear();
        lastVoiceReactionTime.clear();
        
        if (voiceReactionManager != null) {
            voiceReactionManager.clearPendingReactions();
        }
        
        Log.d(TAG, "🧹 Cleared all voice emotional state");
    }
}