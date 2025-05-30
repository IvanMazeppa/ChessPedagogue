package com.example.chesspedagogue;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Example of updated SpectatorGameViewModel that uses the new Responses API integration
 * Shows how to gradually migrate while maintaining backward compatibility
 */
public class SpectatorGameViewModelV2 extends SpectatorGameViewModel {
    private static final String TAG = "SpectatorGameViewModelV2";
    
    private final AIDialogueManagerV2 dialogueManagerV2;
    private final ResponsesAPIIntegrationHelper integrationHelper;
    private boolean useEnhancedConversations = true;
    
    // Additional LiveData for enhanced features
    private final MutableLiveData<String> apiStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUsingResponsesAPI = new MutableLiveData<>();
    
    public SpectatorGameViewModelV2(Application application) {
        super(application);
        
        // Initialize V2 components
        this.dialogueManagerV2 = new AIDialogueManagerV2(application);
        this.integrationHelper = ResponsesAPIIntegrationHelper.getInstance(application);
        
        // Update status
        updateAPIStatus();
        
        Log.d(TAG, "✨ SpectatorGameViewModelV2 initialized with Responses API support");
    }
    
    @Override
    public void startSpectatorGame(String whitePlayer, String blackPlayer) {
        Log.d(TAG, "🎭 Starting enhanced spectator game: " + whitePlayer + " vs " + blackPlayer);
        
        // Check if we can use enhanced features
        boolean canUseEnhanced = integrationHelper.shouldUseResponsesAPI(whitePlayer) || 
                                integrationHelper.shouldUseResponsesAPI(blackPlayer);
        
        isUsingResponsesAPI.setValue(canUseEnhanced);
        
        if (canUseEnhanced && useEnhancedConversations) {
            Log.d(TAG, "🚀 Using Responses API for enhanced conversations");
            apiStatus.setValue("Using Responses API");
        } else {
            Log.d(TAG, "📝 Using standard Chat Completions");
            apiStatus.setValue("Using Chat Completions");
        }
        
        // Call parent implementation - it will use our overridden dialogue manager
        super.startSpectatorGame(whitePlayer, blackPlayer);
    }
    
    /**
     * Override to use V2 dialogue manager for opening dialogue
     */
    @Override
    protected void generateEnhancedOpeningDialogue() {
        Log.d(TAG, "🎬 Generating enhanced opening dialogue with V2 manager");
        
        dialogueManagerV2.generateOpeningDialogue(whitePlayer, blackPlayer, 
            new AIDialogueManager.DialogueCallback() {
                @Override
                public void onDialogueGenerated(String speaker, String dialogue) {
                    aiDialogue.setValue(speaker + ": " + dialogue);
                    conversationSpeaker.setValue(speaker);
                }
                
                @Override
                public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                    conversationActive.setValue(true);
                    Log.d(TAG, "💬 Conversation started: " + respondingSpeaker);
                }
                
                @Override
                public void onConversationComplete(String finalSpeaker, String finalStatement) {
                    conversationActive.setValue(false);
                    Log.d(TAG, "✅ Conversation complete");
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "❌ Dialogue error: " + error);
                    gameStatus.setValue("error: " + error);
                }
            });
    }
    
    /**
     * Toggle between Responses API and Chat Completions
     */
    public void setUseEnhancedConversations(boolean useEnhanced) {
        this.useEnhancedConversations = useEnhanced;
        integrationHelper.setUseResponsesAPI(useEnhanced);
        updateAPIStatus();
        
        Log.d(TAG, "Enhanced conversations: " + (useEnhanced ? "ENABLED" : "DISABLED"));
    }
    
    /**
     * Configure API usage for specific master
     */
    public void configureMasterAPI(String masterName, boolean useResponsesAPI) {
        dialogueManagerV2.configureMasterAPI(masterName, useResponsesAPI);
        updateAPIStatus();
    }
    
    /**
     * Update API status
     */
    private void updateAPIStatus() {
        ResponsesAPIIntegrationHelper.MigrationStatus status = integrationHelper.getMigrationStatus();
        apiStatus.setValue(status.toString());
    }
    
    /**
     * Get API status LiveData
     */
    public LiveData<String> getApiStatus() {
        return apiStatus;
    }
    
    /**
     * Get whether using Responses API
     */
    public LiveData<Boolean> getIsUsingResponsesAPI() {
        return isUsingResponsesAPI;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        dialogueManagerV2.cleanup();
        Log.d(TAG, "🧹 SpectatorGameViewModelV2 cleaned up");
    }
}