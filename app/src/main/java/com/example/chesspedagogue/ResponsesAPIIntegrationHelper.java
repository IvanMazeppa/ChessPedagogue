package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class to facilitate migration from Chat Completions to Responses API
 * Provides fallback mechanisms and gradual migration support
 */
public class ResponsesAPIIntegrationHelper {
    private static final String TAG = "ResponsesAPIIntegrationHelper";
    private static final String PREFS_NAME = "ResponsesAPIMigration";
    private static final String KEY_USE_RESPONSES_API = "use_responses_api";
    private static final String KEY_MASTERS_WITH_RESPONSES = "masters_with_responses";
    
    private static ResponsesAPIIntegrationHelper instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final AtomicBoolean useResponsesAPI;
    private final Map<String, Boolean> masterResponsesEnabled;
    
    // Services
    private final OpenAIService openAIService;
    private final ChessMasterResponsesManager responsesManager;
    private final SpectatorConversationOrchestrator conversationOrchestrator;
    
    private ResponsesAPIIntegrationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.openAIService = OpenAIService.getInstance();
        this.responsesManager = ChessMasterResponsesManager.getInstance(context);
        this.conversationOrchestrator = SpectatorConversationOrchestrator.getInstance(context);
        
        // Load migration settings
        this.useResponsesAPI = new AtomicBoolean(prefs.getBoolean(KEY_USE_RESPONSES_API, false));
        this.masterResponsesEnabled = loadMasterSettings();
        
        Log.d(TAG, "🔄 Integration helper initialized. Responses API enabled: " + useResponsesAPI.get());
    }
    
    public static synchronized ResponsesAPIIntegrationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ResponsesAPIIntegrationHelper(context);
        }
        return instance;
    }
    
    /**
     * Enable or disable Responses API globally
     */
    public void setUseResponsesAPI(boolean enabled) {
        useResponsesAPI.set(enabled);
        prefs.edit().putBoolean(KEY_USE_RESPONSES_API, enabled).apply();
        Log.d(TAG, "Responses API " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Enable Responses API for specific chess master
     */
    public void enableResponsesAPIForMaster(String masterName, boolean enabled) {
        masterResponsesEnabled.put(masterName.toLowerCase(), enabled);
        saveMasterSettings();
        Log.d(TAG, "Responses API for " + masterName + ": " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Check if Responses API should be used for a master
     */
    public boolean shouldUseResponsesAPI(String masterName) {
        // Check if globally enabled
        if (!useResponsesAPI.get()) return false;
        
        // Check if master has assistant configured
        if (!hasAssistantConfigured(masterName)) return false;
        
        // Check master-specific setting
        return masterResponsesEnabled.getOrDefault(masterName.toLowerCase(), false);
    }
    
    /**
     * Convert existing dialogue manager call to use Responses API if appropriate
     */
    public void generateDialogue(String masterName, String context, String message,
                                AIDialogueManager.DialogueCallback originalCallback) {
        
        if (shouldUseResponsesAPI(masterName)) {
            // Use new Responses API
            Log.d(TAG, "🚀 Using Responses API for " + masterName);
            
            responsesManager.createResponseSession(masterName, context, 
                new ChessMasterResponsesManager.ResponseCallback() {
                    private StringBuilder responseBuilder = new StringBuilder();
                    
                    @Override
                    public void onResponseStart(String sessionId) {
                        Log.d(TAG, "Response session started: " + sessionId);
                    }
                    
                    @Override
                    public void onResponseChunk(String chunk, boolean isFirst) {
                        responseBuilder.append(chunk);
                    }
                    
                    @Override
                    public void onResponseComplete(String fullResponse) {
                        originalCallback.onDialogueGenerated(masterName, fullResponse);
                    }
                    
                    @Override
                    public void onConversationTurn(String speaker, String message) {
                        // Track conversation
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Responses API error, falling back: " + error);
                        // Fallback to Chat Completions
                        fallbackToChatCompletions(masterName, context, message, originalCallback);
                    }
                });
            
            // Send the message
            responsesManager.sendMessage("session_" + System.currentTimeMillis(), 
                message, context, null);
            
        } else {
            // Use existing Chat Completions
            Log.d(TAG, "📝 Using Chat Completions for " + masterName);
            fallbackToChatCompletions(masterName, context, message, originalCallback);
        }
    }
    
    /**
     * Convert spectator conversation to use orchestrator if enabled
     */
    public void generateSpectatorConversation(String triggerType, String whitePlayer, String blackPlayer,
                                             String gameContext, AIDialogueManager.DialogueCallback originalCallback) {
        
        if (useResponsesAPI.get() && canUseOrchestrator(whitePlayer, blackPlayer)) {
            Log.d(TAG, "🎭 Using conversation orchestrator");
            
            conversationOrchestrator.startConversation(triggerType, whitePlayer, blackPlayer, gameContext,
                new SpectatorConversationOrchestrator.ConversationCallback() {
                    @Override
                    public void onConversationStart(String speaker1, String speaker2) {
                        // No direct mapping needed
                    }
                    
                    @Override
                    public void onDialogueGenerated(String speaker, String dialogue) {
                        originalCallback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    @Override
                    public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
                        // Map emotional response to regular dialogue
                        originalCallback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    @Override
                    public void onConversationEnd(String finalSpeaker, String finalMessage) {
                        originalCallback.onConversationComplete(finalSpeaker, finalMessage);
                    }
                    
                    @Override
                    public void onError(String error) {
                        originalCallback.onError(error);
                    }
                });
        } else {
            // Use existing dialogue manager (it will handle the conversation internally)
            Log.d(TAG, "📝 Using existing dialogue manager");
        }
    }
    
    /**
     * Fallback to Chat Completions API
     */
    private void fallbackToChatCompletions(String masterName, String context, String message,
                                          AIDialogueManager.DialogueCallback callback) {
        try {
            // Select the chess master
            openAIService.selectChessMaster(masterName);
            
            // Generate response using existing method
            String response = openAIService.getChatCompletion(
                getSystemPromptForMaster(masterName, context),
                message
            );
            
            callback.onDialogueGenerated(masterName, response);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in chat completion fallback", e);
            callback.onError("Failed to generate response: " + e.getMessage());
        }
    }
    
    /**
     * Check if master has assistant configured
     */
    private boolean hasAssistantConfigured(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
            case "fischer":
            case "carlsen":
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check if both masters can use orchestrator
     */
    private boolean canUseOrchestrator(String player1, String player2) {
        // For now, at least one player should have assistant configured
        return hasAssistantConfigured(player1) || hasAssistantConfigured(player2);
    }
    
    /**
     * Get system prompt for master
     */
    private String getSystemPromptForMaster(String masterName, String context) {
        String basePrompt = "You are playing a chess game. ";
        
        switch (masterName.toLowerCase()) {
            case "tal":
                return basePrompt + "Speak as Mikhail Tal would - passionate about tactics and sacrifices.";
            case "fischer":
                return basePrompt + "Speak as Bobby Fischer would - intense, demanding perfection.";
            case "carlsen":
                return basePrompt + "Speak as Magnus Carlsen would - practical and adaptable.";
            default:
                return basePrompt + "Comment on the position naturally.";
        }
    }
    
    /**
     * Load master-specific settings
     */
    private Map<String, Boolean> loadMasterSettings() {
        Map<String, Boolean> settings = new HashMap<>();
        String mastersJson = prefs.getString(KEY_MASTERS_WITH_RESPONSES, "{}");
        
        // Simple parsing - in production use proper JSON parsing
        if (mastersJson.contains("tal")) settings.put("tal", true);
        if (mastersJson.contains("fischer")) settings.put("fischer", true);
        if (mastersJson.contains("carlsen")) settings.put("carlsen", true);
        
        return settings;
    }
    
    /**
     * Save master-specific settings
     */
    private void saveMasterSettings() {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Boolean> entry : masterResponsesEnabled.entrySet()) {
            if (entry.getValue()) {
                json.append("\"").append(entry.getKey()).append("\":true,");
            }
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.setLength(json.length() - 1);
        }
        json.append("}");
        
        prefs.edit().putString(KEY_MASTERS_WITH_RESPONSES, json.toString()).apply();
    }
    
    /**
     * Get migration status
     */
    public MigrationStatus getMigrationStatus() {
        int totalMasters = 12;
        int mastersWithAssistants = 3; // Tal, Fischer, Carlsen
        int enabledMasters = 0;
        
        for (Boolean enabled : masterResponsesEnabled.values()) {
            if (enabled) enabledMasters++;
        }
        
        return new MigrationStatus(
            useResponsesAPI.get(),
            mastersWithAssistants,
            enabledMasters,
            totalMasters
        );
    }
    
    /**
     * Migration status info
     */
    public static class MigrationStatus {
        public final boolean responsesAPIEnabled;
        public final int mastersWithAssistants;
        public final int mastersUsingResponsesAPI;
        public final int totalMasters;
        
        MigrationStatus(boolean responsesAPIEnabled, int mastersWithAssistants,
                       int mastersUsingResponsesAPI, int totalMasters) {
            this.responsesAPIEnabled = responsesAPIEnabled;
            this.mastersWithAssistants = mastersWithAssistants;
            this.mastersUsingResponsesAPI = mastersUsingResponsesAPI;
            this.totalMasters = totalMasters;
        }
        
        @Override
        public String toString() {
            return String.format("Responses API: %s, Masters with assistants: %d/%d, Using API: %d",
                responsesAPIEnabled ? "ON" : "OFF", mastersWithAssistants, totalMasters, mastersUsingResponsesAPI);
        }
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        responsesManager.cleanup();
        conversationOrchestrator.cleanup();
    }
}