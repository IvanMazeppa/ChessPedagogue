package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class for Responses API integration
 * Manages chess master response sessions and conversation orchestration
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
    private final ChessMasterResponseManager responsesManager;
    private final SpectatorConversationOrchestrator conversationOrchestrator;
    
    private ResponsesAPIIntegrationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.openAIService = OpenAIService.getInstance();
        this.responsesManager = ChessMasterResponseManager.getInstance(context);
        this.conversationOrchestrator = SpectatorConversationOrchestrator.getInstance(context);
        
        // Load migration settings - default to true for Responses API
        this.useResponsesAPI = new AtomicBoolean(prefs.getBoolean(KEY_USE_RESPONSES_API, true));
        this.masterResponsesEnabled = loadMasterSettings();
        
        // Enable Responses API by default for masters with assistants
        if (masterResponsesEnabled.isEmpty()) {
            enableResponsesAPIForMaster("tal", true);
            enableResponsesAPIForMaster("fischer", true);
            enableResponsesAPIForMaster("carlsen", true);
            enableResponsesAPIForMaster("anand", true);
            enableResponsesAPIForMaster("alekhine", true); // 🏛️ ALEKHINE ENABLED
            
            // FUTURE: Enable for other masters when assistants are created
            // These masters need proper Responses API assistant configuration
            Log.d(TAG, "🚀 Enabled Responses API by default for masters with assistants: Tal, Fischer, Carlsen, Anand, Alekhine");
            Log.d(TAG, "📋 Other masters (Kasparov, Kramnik, Karpov, etc.) need Responses API configuration");
        }
        
        // IMPORTANT: Enable for main game mode by setting a preference flag
        prefs.edit().putBoolean("responses_api_main_game_enabled", true).apply();
        
        Log.d(TAG, "🔄 Integration helper initialized. Responses API enabled: " + useResponsesAPI.get());
        Log.d(TAG, "🎯 Masters with Responses API enabled: " + masterResponsesEnabled.toString());
        
        // FORCE LOG: Show current configuration
        for (String master : masterResponsesEnabled.keySet()) {
            Log.d(TAG, "📋 Master '" + master + "' → Responses API: " + masterResponsesEnabled.get(master));
        }
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
        Log.d(TAG, "🔍 Checking Responses API eligibility for: " + masterName);
        
        // Check if globally enabled
        if (!useResponsesAPI.get()) {
            Log.d(TAG, "❌ Responses API globally disabled");
            return false;
        }
        
        // Check if master has assistant configured
        if (!hasAssistantConfigured(masterName)) {
            Log.d(TAG, "❌ No assistant configured for: " + masterName);
            return false;
        }
        
        // Check master-specific setting
        boolean enabled = masterResponsesEnabled.getOrDefault(masterName.toLowerCase(), false);
        Log.d(TAG, "🎯 Master " + masterName + " Responses API enabled: " + enabled);
        return enabled;
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
                new ChessMasterResponseManager.ResponseCallback() {
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
                        Log.e(TAG, "❌ Responses API error (no fallback): " + error);
                        originalCallback.onError("Responses API failed: " + error);
                    }
                });
            
            // Send the message
            responsesManager.sendMessage("session_" + System.currentTimeMillis(), 
                message, context, null);
            
        } else {
            // Responses API not enabled for this master
            Log.w(TAG, "⚠️ Responses API not enabled for " + masterName + " - no fallback available");
            originalCallback.onError("Responses API not available for " + masterName);
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
    
    // Fallback to Chat Completions removed - using Responses API only
    
    /**
     * Check if master has assistant configured
     */
    private boolean hasAssistantConfigured(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
            case "fischer":
            case "carlsen":
            case "anand":
            case "alekhine": // 🏛️ ALEKHINE ASSISTANT CONFIGURED
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
        if (mastersJson.contains("anand")) settings.put("anand", true);
        if (mastersJson.contains("alekhine")) settings.put("alekhine", true); // 🏛️ ALEKHINE PERSISTENCE
        
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
        int mastersWithAssistants = 5; // Tal, Fischer, Carlsen, Anand, Alekhine
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