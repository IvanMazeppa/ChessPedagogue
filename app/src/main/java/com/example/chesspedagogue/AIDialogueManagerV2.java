package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example of updated AIDialogueManager that integrates with the Responses API
 * This shows how to gradually migrate from Chat Completions to Responses API
 */
public class AIDialogueManagerV2 extends AIDialogueManager {
    private static final String TAG = "AIDialogueManagerV2";
    
    private final ResponsesAPIIntegrationHelper integrationHelper;
    private final SpectatorConversationOrchestrator conversationOrchestrator;
    private final Context context;
    
    public AIDialogueManagerV2(Context context) {
        super(context);
        this.context = context;
        this.integrationHelper = ResponsesAPIIntegrationHelper.getInstance(context);
        this.conversationOrchestrator = SpectatorConversationOrchestrator.getInstance(context);
        
        // Enable Responses API for masters with assistants
        integrationHelper.setUseResponsesAPI(true);
        integrationHelper.enableResponsesAPIForMaster("tal", true);
        integrationHelper.enableResponsesAPIForMaster("fischer", true);
        integrationHelper.enableResponsesAPIForMaster("carlsen", true);
        
        Log.d(TAG, "🚀 AIDialogueManagerV2 initialized with Responses API support");
        Log.d(TAG, integrationHelper.getMigrationStatus().toString());
    }
    
    @Override
    public void generateOpeningDialogue(String whitePlayer, String blackPlayer, DialogueCallback callback) {
        // Check if we should use the new orchestrator
        if (integrationHelper.shouldUseResponsesAPI(whitePlayer) || 
            integrationHelper.shouldUseResponsesAPI(blackPlayer)) {
            
            Log.d(TAG, "🎭 Using Responses API for opening dialogue");
            
            conversationOrchestrator.startConversation("opening", whitePlayer, blackPlayer, 
                "Game starting", new SpectatorConversationOrchestrator.ConversationCallback() {
                    @Override
                    public void onConversationStart(String speaker1, String speaker2) {
                        // No mapping needed
                    }
                    
                    @Override
                    public void onDialogueGenerated(String speaker, String dialogue) {
                        callback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    @Override
                    public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
                        callback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    @Override
                    public void onConversationEnd(String finalSpeaker, String finalMessage) {
                        callback.onConversationComplete(finalSpeaker, finalMessage);
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Responses API error, falling back: " + error);
                        // Fallback to parent implementation
                        AIDialogueManagerV2.super.generateOpeningDialogue(whitePlayer, blackPlayer, callback);
                    }
                });
            
        } else {
            // Use parent implementation
            super.generateOpeningDialogue(whitePlayer, blackPlayer, callback);
        }
    }
    
    @Override
    public void generateMoveDialogueWithEmotion(String move, String playerWhoMoved, String whitePlayer,
                                                String blackPlayer, int moveNumber, Float currentEvaluation,
                                                DialogueCallback callback) {
        
        // For move dialogue, check if the commentator can use Responses API
        String commentator = chooseCommentator(playerWhoMoved, whitePlayer, blackPlayer, moveNumber);
        
        if (integrationHelper.shouldUseResponsesAPI(commentator)) {
            Log.d(TAG, "🎯 Using Responses API for move dialogue by " + commentator);
            
            String gameContext = String.format("Move %d: %s played %s (eval: %.2f)", 
                moveNumber, playerWhoMoved, move, currentEvaluation != null ? currentEvaluation : 0.0f);
            
            integrationHelper.generateDialogue(commentator, gameContext, 
                "Comment on this move", callback);
            
        } else {
            // Use parent implementation
            super.generateMoveDialogueWithEmotion(move, playerWhoMoved, whitePlayer, 
                blackPlayer, moveNumber, currentEvaluation, callback);
        }
    }
    
    @Override
    public void generateEndGameDialogue(String result, String whitePlayer, String blackPlayer, DialogueCallback callback) {
        // For endgame, prefer orchestrator if available
        if (integrationHelper.shouldUseResponsesAPI(whitePlayer) || 
            integrationHelper.shouldUseResponsesAPI(blackPlayer)) {
            
            Log.d(TAG, "🏁 Using Responses API for endgame dialogue");
            
            conversationOrchestrator.startConversation("endgame", whitePlayer, blackPlayer,
                "Game result: " + result, new SpectatorConversationOrchestrator.ConversationCallback() {
                    @Override
                    public void onConversationStart(String speaker1, String speaker2) {
                        // No mapping needed
                    }
                    
                    @Override
                    public void onDialogueGenerated(String speaker, String dialogue) {
                        callback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    @Override
                    public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
                        callback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    @Override
                    public void onConversationEnd(String finalSpeaker, String finalMessage) {
                        callback.onConversationComplete(finalSpeaker, finalMessage);
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Responses API error, falling back: " + error);
                        // Fallback to parent implementation
                        AIDialogueManagerV2.super.generateEndGameDialogue(result, whitePlayer, blackPlayer, callback);
                    }
                });
            
        } else {
            // Use parent implementation
            super.generateEndGameDialogue(result, whitePlayer, blackPlayer, callback);
        }
    }
    
    // Helper method to choose commentator (copied from parent)
    private String chooseCommentator(String playerWhoMoved, String whitePlayer, String blackPlayer, int moveNumber) {
        return moveNumber <= 10 ?
                (Math.random() > 0.3 ? playerWhoMoved : (playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer)) :
                (Math.random() > 0.4 ? (playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer) : playerWhoMoved);
    }
    
    @Override
    public void forceStop() {
        super.forceStop();
        conversationOrchestrator.forceStop();
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        integrationHelper.cleanup();
    }
    
    /**
     * Get migration status for monitoring
     */
    public ResponsesAPIIntegrationHelper.MigrationStatus getMigrationStatus() {
        return integrationHelper.getMigrationStatus();
    }
    
    /**
     * Configure which masters use Responses API
     */
    public void configureMasterAPI(String masterName, boolean useResponsesAPI) {
        integrationHelper.enableResponsesAPIForMaster(masterName, useResponsesAPI);
    }
}