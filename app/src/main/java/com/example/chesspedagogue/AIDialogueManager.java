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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.IOException;
import android.content.SharedPreferences;

/**
 * 🎭 FIXED AI DIALOGUE MANAGER - No More Infinite Loops!
 *
 * Creates authentic, flowing conversations between AI chess masters during spectator games
 * with full voice synthesis using personality-specific accents and speech patterns!
 *
 * FIXED: Smart conversation limits prevent infinite loops while maintaining emotional responses
 */
public class AIDialogueManager {
    private static final String TAG = "AIDialogueManager";

    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final OpenAIService openAIService;
    private final FineTunedModelManager modelManager;
    private final OpenAITTSService ttsService;

    // FIXED: Smart conversation state tracking to prevent infinite loops
    private String lastSpeaker = "";
    private String lastStatement = "";
    private List<ConversationTurn> conversationHistory = new ArrayList<>();
    private boolean conversationInProgress = false;
    private int conversationTurnCount = 0;
    private long conversationStartTime = 0;

    // ADDED: Circuit breaker to prevent infinite loops
    private static final int MAX_CONVERSATION_TURNS = 6; // Increased from 4 but still limited
    private static final long MAX_CONVERSATION_DURATION = 30000; // 30 seconds max per conversation
    private static final long MIN_TIME_BETWEEN_CONVERSATIONS = 15000; // 15 seconds between conversations
    private long lastConversationEndTime = 0;

    // TTS state tracking - ENHANCED with better cleanup
    private boolean isSpeaking = false;
    private String currentlySpeaking = "";
    private final Map<String, Integer> speakerQueueCount = new HashMap<>(); // Track queued items per speaker
    private static final int MAX_QUEUED_PER_SPEAKER = 2; // Maximum queued items per speaker

    // Conversation triggers and timing
    private static final long CONVERSATION_DELAY_MS = 2000;
    private static final long TTS_DELAY_MS = 500;
    private long lastDialogueTime = 0; // Track last dialogue time for cleanup

    // ENHANCED: Emotional state tracking
    private boolean userInteractionEnabled = true;
    private String lastUserComment = "";
    private long lastUserCommentTime = 0;
    private static final long USER_COMMENT_COOLDOWN = 10000;
    private Map<String, Float> lastPlayerEvaluations = new HashMap<>();
    private static final float EMOTIONAL_THRESHOLD = 0.5f;

    /**
     * Represents a turn in the conversation
     */
    private static class ConversationTurn {
        final String speaker;
        final String statement;
        final long timestamp;
        final String context;

        ConversationTurn(String speaker, String statement, String context) {
            this.speaker = speaker;
            this.statement = statement;
            this.context = context;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public interface DialogueCallback {
        void onDialogueGenerated(String speaker, String dialogue);
        void onConversationStarted(String respondingSpeaker, String triggerStatement);
        void onConversationComplete(String finalSpeaker, String finalStatement);
        void onError(String error);
    }

    public AIDialogueManager(Context context) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.openAIService = OpenAIService.getInstance();
        this.modelManager = FineTunedModelManager.getInstance(context);
        // Use TTSServiceManager to get the appropriate TTS service
        this.ttsService = TTSServiceManager.getOpenAITTSService(context);

        Log.d(TAG, "🎭 FIXED AI Dialogue Manager initialized - infinite loops prevented!");
    }

    /**
     * 🏁 Generate end game dialogue
     */
    public void generateEndGameDialogue(String result, String whitePlayer, String blackPlayer, DialogueCallback callback) {
        Log.d(TAG, "🏁 Generating end game dialogue for: " + result);

        resetConversation(); // Reset for end game

        executorService.execute(() -> {
            try {
                String winner = extractWinner(result);
                String firstSpeaker = winner != null ? winner : whitePlayer;
                String secondSpeaker = firstSpeaker.equals(whitePlayer) ? blackPlayer : whitePlayer;

                String endStatement = generateNaturalEndGameStatement(firstSpeaker, result);

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(firstSpeaker, endStatement);
                    speakDialogueWithPersonality(firstSpeaker, endStatement);

                    // FIXED: Only trigger response if conversation limits allow
                    if (canStartNewConversation()) {
                        scheduleConversationResponse(secondSpeaker, firstSpeaker, endStatement, "endgame_response", callback);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error generating end game dialogue", e);
                mainHandler.post(() -> callback.onError("Failed to generate end game dialogue"));
            }
        });
    }

    /**
     * 🎤 Process user comment and generate player responses
     */
    public void processUserComment(String userComment, String whitePlayer, String blackPlayer,
                                   String currentGamePhase, DialogueCallback callback) {

        Log.d(TAG, "🎤 Processing user comment: " + userComment);

        // Prevent spam comments
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUserCommentTime < USER_COMMENT_COOLDOWN) {
            Log.d(TAG, "⏰ Comment too soon, ignoring");
            return;
        }

        lastUserComment = userComment;
        lastUserCommentTime = currentTime;

        executorService.execute(() -> {
            try {
                CommentAnalysis analysis = analyzeUserComment(userComment);
                Log.d(TAG, "📊 Comment analysis: " + analysis.toString());

                String respondingPlayer = chooseRespondingPlayer(analysis, whitePlayer, blackPlayer);

                if (respondingPlayer != null && canStartNewConversation()) {
                    Log.d(TAG, "🎭 " + respondingPlayer + " will respond to user comment");

                    String response = generateUserCommentResponse(
                            userComment, respondingPlayer, currentGamePhase, analysis);

                    if (response != null && !response.trim().isEmpty()) {
                        mainHandler.post(() -> {
                            callback.onDialogueGenerated(respondingPlayer, response);

                            String emotionalState = determineEmotionalResponseToComment(analysis, respondingPlayer);
                            speakDialogueWithPersonalityAndEmotion(respondingPlayer, response, emotionalState, null);

                            // FIXED: Only trigger follow-up if conversation limits allow
                            if (analysis.likelihood > 0.7f && canStartNewConversation()) {
                                String otherPlayer = respondingPlayer.equals(whitePlayer) ? blackPlayer : whitePlayer;
                                scheduleUserCommentFollowUp(otherPlayer, userComment, response, callback);
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "🤐 No player chose to respond or conversation limit reached");
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Error processing user comment", e);
                mainHandler.post(() -> callback.onError("Error processing user comment"));
            }
        });
    }

    /**
     * CRITICAL FIX: Smart conversation limits to prevent infinite loops
     */
    private boolean canStartNewConversation() {
        long currentTime = System.currentTimeMillis();

        // Check if we're already in a conversation
        if (conversationInProgress) {
            // Check if conversation has been going too long
            if (currentTime - conversationStartTime > MAX_CONVERSATION_DURATION) {
                Log.w(TAG, "🛑 FORCE ENDING conversation - exceeded max duration");
                forceEndConversation();
                return false;
            }

            // Check if we've had too many turns
            if (conversationTurnCount >= MAX_CONVERSATION_TURNS) {
                Log.w(TAG, "🛑 ENDING conversation - max turns reached");
                endConversation();
                return false;
            }

            return true; // Can continue existing conversation
        }

        // Check if enough time has passed since last conversation
        if (currentTime - lastConversationEndTime < MIN_TIME_BETWEEN_CONVERSATIONS) {
            Log.d(TAG, "⏰ Too soon since last conversation - waiting");
            return false;
        }

        return true; // Can start new conversation
    }

    /**
     * CRITICAL FIX: Enhanced TTS with smart queuing limits
     */
    private void speakDialogueWithPersonality(String speaker, String dialogue) {
        // REDUCED LOGGING - only log important events
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "🎵 TTS request: " + speaker + " - " + dialogue.substring(0, Math.min(30, dialogue.length())) + "...");
        }

        try {
            // FIXED: Check speaker queue limits to prevent spam
            int currentQueueCount = speakerQueueCount.getOrDefault(speaker, 0);
            if (currentQueueCount >= MAX_QUEUED_PER_SPEAKER) {
                Log.w(TAG, "🚫 Skipping " + speaker + " TTS - queue full (" + currentQueueCount + ")");
                return;
            }

            if (isSpeaking && !currentlySpeaking.equals(speaker)) {
                // FIXED: Smart queuing with limits
                speakerQueueCount.put(speaker, currentQueueCount + 1);
                Log.d(TAG, "⏳ Queuing " + speaker + " (queue: " + (currentQueueCount + 1) + ")");

                mainHandler.postDelayed(() -> {
                    // Decrease queue count when actually processing
                    int queueCount = speakerQueueCount.getOrDefault(speaker, 1);
                    speakerQueueCount.put(speaker, Math.max(0, queueCount - 1));
                    cleanupTTSQueues(); // Cleanup after processing
                    speakDialogueWithPersonality(speaker, dialogue);
                }, 3000);
                return;
            }

            isSpeaking = true;
            currentlySpeaking = speaker;

            // Set master preference for TTS
            SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String currentMaster = masterPrefs.getString("selected_master", "tal");

            SharedPreferences.Editor editor = masterPrefs.edit();
            editor.putString("selected_master", speaker.toLowerCase());
            editor.apply();

            OpenAITTSService.OnSpeechCompletedListener enhancedCallback = new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    // CRITICAL FIX: Always clean up TTS state
                    Log.d(TAG, "✅ " + speaker + " finished speaking");

                    // Restore original master selection
                    SharedPreferences.Editor restoreEditor = masterPrefs.edit();
                    restoreEditor.putString("selected_master", currentMaster);
                    restoreEditor.apply();

                    isSpeaking = false;
                    currentlySpeaking = "";

                    // Clear this speaker's queue count
                    speakerQueueCount.put(speaker, 0);
                }
            };

            String voiceToUse = modelManager.getVoiceForMaster(speaker);
            Log.d(TAG, "🎭 Using voice '" + voiceToUse + "' for " + speaker);
            lastDialogueTime = System.currentTimeMillis();
            ttsService.speak(dialogue, enhancedCallback);

        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR speaking dialogue for " + speaker, e);
            cleanupTTSState();
        }
    }

    /**
     * ADDED: Cleanup TTS state in case of errors
     */
    private void cleanupTTSState() {
        isSpeaking = false;
        currentlySpeaking = "";
        speakerQueueCount.clear();

        try {
            SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            // Don't change master selection during cleanup
            Log.d(TAG, "🧹 TTS state cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Error during TTS cleanup", e);
        }
    }

    /**
     * 🧹 ENHANCED: Cleanup TTS queue buildup - ADD THIS METHOD
     */
    private void cleanupTTSQueues() {
        try {
            long currentTime = System.currentTimeMillis();

            // Clear excessive queue counts
            for (String speaker : speakerQueueCount.keySet()) {
                int count = speakerQueueCount.get(speaker);
                if (count > MAX_QUEUED_PER_SPEAKER) {
                    Log.w(TAG, "🧹 Clearing excessive queue for " + speaker + " (" + count + " items)");
                    speakerQueueCount.put(speaker, 0);
                }
            }

            // If someone has been speaking too long, reset TTS state
            if (isSpeaking && currentTime - lastDialogueTime > 30000) { // 30 seconds
                Log.w(TAG, "🧹 Force cleaning stuck TTS state for " + currentlySpeaking);
                cleanupTTSState();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during TTS queue cleanup", e);
        }
    }

    /**
     * 🎭 Enhanced emotional TTS method - FIXED
     */
    public void speakDialogueWithPersonalityAndEmotion(String speaker, String dialogue, String emotionalContext, Float currentEvaluation) {
        Log.d(TAG, "🎭 Emotional TTS: " + speaker + " (" + emotionalContext + ")");

        try {
            // Use the same smart queuing logic as regular TTS
            int currentQueueCount = speakerQueueCount.getOrDefault(speaker, 0);
            if (currentQueueCount >= MAX_QUEUED_PER_SPEAKER) {
                Log.w(TAG, "🚫 Skipping emotional " + speaker + " TTS - queue full");
                return;
            }

            if (isSpeaking && !currentlySpeaking.equals(speaker)) {
                speakerQueueCount.put(speaker, currentQueueCount + 1);
                mainHandler.postDelayed(() -> {
                    int queueCount = speakerQueueCount.getOrDefault(speaker, 1);
                    speakerQueueCount.put(speaker, Math.max(0, queueCount - 1));
                    speakDialogueWithPersonalityAndEmotion(speaker, dialogue, emotionalContext, currentEvaluation);
                }, 3000);
                return;
            }

            isSpeaking = true;
            currentlySpeaking = speaker;

            // Store evaluation for emotional tracking
            if (currentEvaluation != null) {
                lastPlayerEvaluations.put(speaker.toLowerCase(), currentEvaluation);
            }

            SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String currentMaster = masterPrefs.getString("selected_master", "tal");

            SharedPreferences.Editor editor = masterPrefs.edit();
            editor.putString("selected_master", speaker.toLowerCase());
            editor.apply();

            OpenAITTSService.OnSpeechCompletedListener emotionalCallback = new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    Log.d(TAG, "🎭 " + speaker + " finished emotional speech (" + emotionalContext + ")");

                    // Restore original master selection
                    SharedPreferences.Editor restoreEditor = masterPrefs.edit();
                    restoreEditor.putString("selected_master", currentMaster);
                    restoreEditor.apply();

                    isSpeaking = false;
                    currentlySpeaking = "";
                    speakerQueueCount.put(speaker, 0);
                }
            };

            ttsService.speak(dialogue, emotionalCallback);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in emotional TTS for " + speaker, e);
            cleanupTTSState();
        }
    }

    /**
     * ENHANCED: Generate opening dialogue with conversation limits
     */
    public void generateOpeningDialogue(String whitePlayer, String blackPlayer, DialogueCallback callback) {
        Log.d(TAG, "🎬 Generating opening dialogue: " + whitePlayer + " vs " + blackPlayer);

        resetConversation();

        executorService.execute(() -> {
            try {
                String firstSpeaker = Math.random() > 0.5 ? whitePlayer : blackPlayer;
                String opponent = firstSpeaker.equals(whitePlayer) ? blackPlayer : whitePlayer;

                String openingStatement = generateNaturalOpeningStatement(firstSpeaker, opponent);

                conversationHistory.add(new ConversationTurn(firstSpeaker, openingStatement, "game_opening"));
                lastSpeaker = firstSpeaker;
                lastStatement = openingStatement;

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(firstSpeaker, openingStatement);
                    speakDialogueWithPersonality(firstSpeaker, openingStatement);

                    // FIXED: Only trigger response if conversation limits allow
                    if (shouldTriggerResponse(openingStatement) && canStartNewConversation()) {
                        startConversation(); // Mark conversation as started
                        scheduleConversationResponse(opponent, firstSpeaker, openingStatement, "opening_response", callback);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Error generating opening dialogue", e);
                mainHandler.post(() -> callback.onError("Failed to generate opening dialogue"));
            }
        });
    }

    /**
     * ENHANCED: Generate move dialogue with conversation limits
     */
    public void generateMoveDialogueWithEmotion(String move, String playerWhoMoved, String whitePlayer,
                                                String blackPlayer, int moveNumber, Float currentEvaluation,
                                                DialogueCallback callback) {

        Log.d(TAG, "🎯 Move dialogue: " + move + " by " + playerWhoMoved + " (eval: " + currentEvaluation + ")");

        // Don't interrupt ongoing conversations unless it's a major emotional event
        if (conversationInProgress && !isMajorEmotionalEvent(currentEvaluation)) {
            Log.d(TAG, "⏸️ Skipping move dialogue - conversation in progress");
            return;
        }

        executorService.execute(() -> {
            try {
                String commentator = chooseCommentator(playerWhoMoved, whitePlayer, blackPlayer, moveNumber);
                String moveComment = generateNaturalMoveComment(move, commentator, playerWhoMoved, moveNumber);

                conversationHistory.add(new ConversationTurn(commentator, moveComment, "move_" + moveNumber));
                lastSpeaker = commentator;
                lastStatement = moveComment;

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(commentator, moveComment);
                    speakDialogueWithPersonalityAndEmotion(commentator, moveComment, "move", currentEvaluation);

                    // FIXED: Only trigger conversation if limits allow
                    String otherPlayer = commentator.equals(whitePlayer) ? blackPlayer : whitePlayer;
                    if (shouldTriggerResponse(moveComment) && canStartNewConversation()) {
                        startConversation();
                        scheduleConversationResponse(otherPlayer, commentator, moveComment, "move_response", callback);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ Error generating move dialogue", e);
                mainHandler.post(() -> callback.onError("Failed to generate move dialogue"));
            }
        });
    }

    /**
     * ADDED: Check if this is a major emotional event that should interrupt conversations
     */
    private boolean isMajorEmotionalEvent(Float currentEvaluation) {
        if (currentEvaluation == null) return false;

        // Major swing in evaluation should interrupt conversations
        return Math.abs(currentEvaluation) > 3.0f; // Major advantage/disadvantage
    }

    /**
     * ADDED: Start conversation tracking
     */
    private void startConversation() {
        conversationInProgress = true;
        conversationStartTime = System.currentTimeMillis();
        conversationTurnCount = 0;
        Log.d(TAG, "🎉 Started new conversation");
    }

    /**
     * ADDED: End conversation tracking
     */
    private void endConversation() {
        conversationInProgress = false;
        lastConversationEndTime = System.currentTimeMillis();
        conversationTurnCount = 0;
        Log.d(TAG, "✅ Ended conversation naturally");
    }

    /**
     * ADDED: Force end conversation (for timeouts)
     */
    private void forceEndConversation() {
        conversationInProgress = false;
        lastConversationEndTime = System.currentTimeMillis();
        conversationTurnCount = 0;
        cleanupTTSState(); // Also cleanup any stuck TTS
        Log.w(TAG, "🛑 FORCE ended conversation");
    }

    /**
     * ENHANCED: Generate conversation response with limits
     */
    private void generateConversationResponse(String responder, String originalSpeaker,
                                              String triggerStatement, String context, DialogueCallback callback) {

        if (!canStartNewConversation()) {
            Log.d(TAG, "🛑 Cannot continue conversation - limits reached");
            endConversation();
            return;
        }

        executorService.execute(() -> {
            try {
                conversationTurnCount++;
                Log.d(TAG, "💬 Conversation turn " + conversationTurnCount + "/" + MAX_CONVERSATION_TURNS);

                String responderName = modelManager.getMasterDisplayName(responder);
                String originalSpeakerName = modelManager.getMasterDisplayName(originalSpeaker);

                String conversationPrompt = createConversationalPrompt(responder, String.format(
                        "%s: \"%s\"\n\nRespond naturally as if you're having a conversation with your opponent during the game. Do NOT give advice or teach. You are a player talking to another player.",
                        originalSpeakerName, triggerStatement
                ));

                // Select the correct chess master for the fine-tuned model
                openAIService.selectChessMaster(responder);
                
                String response = cleanAndPersonalize(
                        openAIService.getChatCompletion(conversationPrompt, "Generate player-to-player response, not teaching"),
                        responder
                );

                conversationHistory.add(new ConversationTurn(responder, response, context));
                lastSpeaker = responder;
                lastStatement = response;

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(responder, response);
                    speakDialogueWithPersonality(responder, response);

                    // Check if conversation should continue
                    if (conversationTurnCount < MAX_CONVERSATION_TURNS &&
                            shouldContinueConversation(response) &&
                            canStartNewConversation()) {

                        scheduleConversationResponse(originalSpeaker, responder, response, "conversation_continue", callback);
                    } else {
                        // End the conversation
                        endConversation();
                        callback.onConversationComplete(responder, response);
                        Log.d(TAG, "🎭 Conversation completed naturally");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error generating conversation response", e);
                forceEndConversation();
                mainHandler.post(() -> callback.onError("Failed to generate conversation response"));
            }
        });
    }

    /**
     * ⏰ Schedule conversation response with limits
     */
    private void scheduleConversationResponse(String responder, String originalSpeaker,
                                              String triggerStatement, String context, DialogueCallback callback) {

        if (!canStartNewConversation()) {
            Log.d(TAG, "🛑 Not scheduling response - conversation limits reached");
            return;
        }

        Log.d(TAG, "⏰ Scheduling conversation response from " + responder);

        mainHandler.postDelayed(() -> {
            if (canStartNewConversation()) {
                callback.onConversationStarted(responder, triggerStatement);
                generateConversationResponse(responder, originalSpeaker, triggerStatement, context, callback);
            } else {
                Log.d(TAG, "⏸️ Response cancelled - conversation limits reached");
            }
        }, CONVERSATION_DELAY_MS);
    }

    /**
     * 🔄 Reset emotional state (call this when starting new games)
     */
    public void resetEmotionalState() {
        lastPlayerEvaluations.clear();
        resetConversation();
        cleanupTTSState();
        Log.d(TAG, "🔄 Emotional state reset for new game");
    }

    /**
     * 🔄 Reset conversation state
     */
    public void resetConversation() {
        conversationHistory.clear();
        forceEndConversation(); // Use force end to cleanup everything
        lastSpeaker = "";
        lastStatement = "";
        speakerQueueCount.clear();
        cleanupTTSState();

        Log.d(TAG, "🔄 Conversation state reset");
    }

    // KEEPING ALL YOUR EXISTING HELPER METHODS (shortened for space but they're all here)

    private String determineEmotionalResponseToComment(CommentAnalysis analysis, String respondingPlayer) {
        switch (analysis.sentiment) {
            case "positive":
                return analysis.topic.equals("praise") ? "pleased" : "neutral";
            case "negative":
                switch (respondingPlayer.toLowerCase()) {
                    case "fischer": return "frustrated";
                    case "tal": return "neutral";
                    case "kasparov": return "concerned";
                    case "karpov": return "neutral";
                    default: return "concerned";
                }
            default:
                return analysis.topic.equals("question") ? "pleased" : "neutral";
        }
    }

    private CommentAnalysis analyzeUserComment(String comment) {
        CommentAnalysis analysis = new CommentAnalysis();
        String lowerComment = comment.toLowerCase();

        if (lowerComment.contains("move") || lowerComment.contains("play")) {
            analysis.topic = "move_critique";
            analysis.likelihood = 0.8f;
        } else if (lowerComment.contains("good") || lowerComment.contains("nice")) {
            analysis.topic = "praise";
            analysis.sentiment = "positive";
            analysis.likelihood = 0.9f;
        } else if (lowerComment.contains("bad") || lowerComment.contains("mistake")) {
            analysis.topic = "criticism";
            analysis.sentiment = "negative";
            analysis.likelihood = 0.9f;
        } else {
            analysis.topic = "general";
            analysis.likelihood = 0.4f;
        }

        return analysis;
    }

    private String chooseRespondingPlayer(CommentAnalysis analysis, String whitePlayer, String blackPlayer) {
        Map<String, Float> engagementLikelihood = new HashMap<>();
        engagementLikelihood.put("tal", 0.9f);
        engagementLikelihood.put("carlsen", 0.8f);
        engagementLikelihood.put("kasparov", 0.85f);
        engagementLikelihood.put("fischer", 0.3f);
        engagementLikelihood.put("karpov", 0.6f);

        float whiteLikelihood = engagementLikelihood.getOrDefault(whitePlayer.toLowerCase(), 0.6f);
        float blackLikelihood = engagementLikelihood.getOrDefault(blackPlayer.toLowerCase(), 0.6f);

        return Math.random() < whiteLikelihood && Math.random() > blackLikelihood ? whitePlayer :
                Math.random() < blackLikelihood ? blackPlayer : null;
    }

    private String generateUserCommentResponse(String userComment, String respondingPlayer,
                                               String gamePhase, CommentAnalysis analysis) {
        String playerName = modelManager.getMasterDisplayName(respondingPlayer);
        String prompt = createUserCommentResponsePrompt(respondingPlayer, userComment, gamePhase, analysis);

        try {
            // Select the correct chess master for the fine-tuned model
            openAIService.selectChessMaster(respondingPlayer);
            
            String response = openAIService.getChatCompletion(prompt,
                    "A spectator said: \"" + userComment + "\"");
            return cleanAndPersonalize(response, respondingPlayer);
        } catch (Exception e) {
            Log.e(TAG, "Error generating user comment response", e);
            return getFallbackUserResponse(respondingPlayer, analysis);
        }
    }

    private String createUserCommentResponsePrompt(String master, String userComment,
                                                   String gamePhase, CommentAnalysis analysis) {
        StringBuilder prompt = new StringBuilder();

        prompt.append(getSpectatorPersonalityNote(master));
        prompt.append("\n\nDuring this game, a spectator said: \"").append(userComment).append("\"");
        prompt.append("\nRespond briefly as a player would during a game. Do NOT give lessons or advice to the spectator.");

        return prompt.toString();
    }

    private void scheduleUserCommentFollowUp(String followUpPlayer, String originalComment,
                                             String firstResponse, DialogueCallback callback) {
        if (!canStartNewConversation()) {
            Log.d(TAG, "🛑 Not scheduling follow-up - conversation limits reached");
            return;
        }

        mainHandler.postDelayed(() -> {
            if (canStartNewConversation()) {
                String followUpResponse = generateFollowUpResponse(followUpPlayer, originalComment, firstResponse);
                if (followUpResponse != null) {
                    callback.onDialogueGenerated(followUpPlayer, followUpResponse);
                    speakDialogueWithPersonality(followUpPlayer, followUpResponse);
                }
            }
        }, 3000);
    }

    private String generateFollowUpResponse(String followUpPlayer, String originalComment, String firstResponse) {
        try {
            String prompt = String.format(
                    "%s\n\nA spectator said: \"%s\" and another master responded: \"%s\"\nYou may add a brief comment as a fellow player, or stay silent. Do NOT teach or give advice.",
                    getSpectatorPersonalityNote(followUpPlayer), originalComment, firstResponse
            );

            // Select the correct chess master for the fine-tuned model
            openAIService.selectChessMaster(followUpPlayer);
            
            String response = openAIService.getChatCompletion(prompt, "Generate brief player comment or SKIP");

            if (response != null && !response.trim().equalsIgnoreCase("SKIP")) {
                return cleanAndPersonalize(response, followUpPlayer);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating follow-up response", e);
        }
        return null;
    }

    private String getFallbackUserResponse(String master, CommentAnalysis analysis) {
        switch (master.toLowerCase()) {
            case "tal":
                return "Thank you! Chess is so beautiful.";
            case "fischer":
                return "I'm focused on finding the best moves.";
            case "carlsen":
                return "Thanks for watching!";
            default:
                return "Thank you for your interest!";
        }
    }

    private static class CommentAnalysis {
        String topic = "general";
        String sentiment = "neutral";
        float likelihood = 0.5f;

        @Override
        public String toString() {
            return String.format("Topic: %s, Sentiment: %s, Likelihood: %.2f", topic, sentiment, likelihood);
        }
    }

    // ALL YOUR OTHER HELPER METHODS ARE PRESERVED...
    private String generateNaturalOpeningStatement(String speaker, String opponent) {
        String speakerName = modelManager.getMasterDisplayName(speaker);
        String opponentName = modelManager.getMasterDisplayName(opponent);

        String prompt = createConversationalPrompt(speaker, String.format(
                "About to play %s. Make a brief comment as if you're sitting at the board about to start the game. Do NOT address any audience or give teaching advice. Speak as a player, not a coach.",
                opponentName
        ));

        // Select the correct chess master for the fine-tuned model
        openAIService.selectChessMaster(speaker);
        
        return cleanAndPersonalize(openAIService.getChatCompletion(prompt, "Generate pre-game comment as a player, not a teacher"), speaker);
    }

    private String generateNaturalMoveComment(String move, String commentator, String playerWhoMoved, int moveNumber) {
        String commentatorName = modelManager.getMasterDisplayName(commentator);
        String playerName = modelManager.getMasterDisplayName(playerWhoMoved);

        boolean commentingOnOwnMove = commentator.equals(playerWhoMoved);
        String gameContext = getGameContext(moveNumber, move);

        String prompt = commentingOnOwnMove ?
                createConversationalPrompt(commentator, String.format(
                        "Just played %s. %s Comment briefly on your move as if you're at the board playing. Do NOT give advice or teach. You are the player, not a coach.",
                        move, gameContext)) :
                createConversationalPrompt(commentator, String.format(
                        "%s played %s. %s Comment briefly on your opponent's move as if you're at the board playing against them. Do NOT give advice or teach. You are a player, not a coach.",
                        playerName, move, gameContext));

        // Select the correct chess master for the fine-tuned model
        openAIService.selectChessMaster(commentator);
        
        return cleanAndPersonalize(openAIService.getChatCompletion(prompt, "Generate player comment, not teaching"), commentator);
    }

    private String getGameContext(int moveNumber, String move) {
        if (moveNumber <= 10) return "This is the opening. ";
        else if (moveNumber <= 25) return "The middlegame is developing. ";
        else return "We're in the endgame. ";
    }

    private String generateNaturalEndGameStatement(String speaker, String result) {
        String speakerName = modelManager.getMasterDisplayName(speaker);
        String prompt = createConversationalPrompt(speaker, String.format(
                "Game ended: %s. Comment on the game result as if you just finished playing. Do NOT give advice or lessons. You are a player reflecting on the game you just played, not a teacher.",
                result
        ));

        // Select the correct chess master for the fine-tuned model
        openAIService.selectChessMaster(speaker);
        
        return cleanAndPersonalize(openAIService.getChatCompletion(prompt, "Generate player's endgame reflection, not teaching"), speaker);
    }

    private boolean shouldTriggerResponse(String statement) {
        if (statement == null) return false;

        String lower = statement.toLowerCase();
        String[] triggers = {"interesting", "bold", "agree", "disagree", "remember", "always", "never"};

        for (String trigger : triggers) {
            if (lower.contains(trigger)) return true;
        }

        return lower.contains("?") || Math.random() > 0.7;
    }



    private String chooseCommentator(String playerWhoMoved, String whitePlayer, String blackPlayer, int moveNumber) {
        return moveNumber <= 10 ?
                (Math.random() > 0.3 ? playerWhoMoved : (playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer)) :
                (Math.random() > 0.4 ? (playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer) : playerWhoMoved);
    }

    private boolean shouldContinueConversation(String lastResponse) {
        if (lastResponse == null || lastResponse.length() < 20) return false;

        String lower = lastResponse.toLowerCase();
        return lower.contains("but") || lower.contains("however") || lower.contains("?") || lower.contains("what about");
    }

    private String createConversationalPrompt(String master, String instruction) {
        String personalityNote = getSpectatorPersonalityNote(master);
        // FIXED: Direct first-person prompt to prevent third-person responses
        // Instead of "You are Mikhail Tal", we just include personality traits
        return String.format("%s\n\n%s", personalityNote, instruction);
    }

    /**
     * Get personality note for spectator mode - focuses on player-to-player interaction
     */
    private String getSpectatorPersonalityNote(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "You are playing a chess game. Speak as the actual player Mikhail Tal would during a game - passionate about tactics and sacrifices. Comment on your own moves or respond to your opponent naturally, as if you're at the board.";
            case "fischer":
                return "You are playing a chess game. Speak as Bobby Fischer would during a game - intense, demanding perfection. Comment on moves with absolute certainty, as if you're at the board playing.";
            case "kasparov":
                return "You are playing a chess game. Speak as Garry Kasparov would during a game - dynamic and aggressive. Comment on the battle for initiative, as if you're at the board.";
            case "karpov":
                return "You are playing a chess game. Speak as Anatoly Karpov would during a game - patient and strategic. Comment on accumulating small advantages, as if you're at the board.";
            case "carlsen":
                return "You are playing a chess game. Speak as Magnus Carlsen would during a game - practical and adaptable. Comment on squeezing advantages from any position, as if you're at the board.";
            case "kramnik":
                return "You are playing a chess game. Speak as Vladimir Kramnik would during a game - methodical and precise. Comment on deep positional understanding, as if you're at the board.";
            case "alekhine":
                return "You are playing a chess game. Speak as Alexander Alekhine would during a game - calculating deeply. Comment on hidden combinations and complexity, as if you're at the board.";
            case "capablanca":
                return "You are playing a chess game. Speak as Capablanca would during a game - natural and elegant. Comment on simple, strong moves, as if you're at the board.";
            case "morphy":
                return "You are playing a chess game. Speak as Paul Morphy would during a game - focusing on rapid development. Comment on attacking chances, as if you're at the board.";
            case "lasker":
                return "You are playing a chess game. Speak as Emanuel Lasker would during a game - psychological and practical. Comment on your opponent's psychology, as if you're at the board.";
            case "anand":
                return "You are playing a chess game. Speak as Viswanathan Anand would during a game - versatile and intuitive. Comment on your preparation and feel for positions, as if you're at the board.";
            case "botvinnik":
                return "You are playing a chess game. Speak as Mikhail Botvinnik would during a game - scientific and disciplined. Comment on systematic play, as if you're at the board.";
            default:
                return "You are playing a chess game. Comment on your moves naturally, as if you're at the board.";
        }
    }

    private String getPersonalityNote(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "I'm passionate about beautiful chess and tactical brilliance.";
            case "fischer":
                return "I demand precision and uncompromising excellence in every move.";
            case "kasparov":
                return "I play dynamic, aggressive chess and fight for every advantage.";
            case "karpov":
                return "I employ patience and positional understanding to accumulate advantages.";
            case "carlsen":
                return "I adapt my style to squeeze maximum advantage from any position.";
            case "kramnik":
                return "I focus on deep understanding and solid, principled play.";
            case "alekhine":
                return "I seek hidden combinational beauty through deep calculation.";
            case "capablanca":
                return "I play natural, elegant moves that flow from understanding.";
            case "morphy":
                return "I develop quickly and attack the undefended king.";
            case "lasker":
                return "I play the man as much as the board, using psychology.";
            case "anand":
                return "I combine preparation with intuition and universal style.";
            case "botvinnik":
                return "I approach chess scientifically with iron discipline.";
            default:
                return "I have my own unique approach to chess mastery.";
        }
    }

    private String cleanAndPersonalize(String response, String master) {
        if (response == null) return getFallbackResponse(master);

        String cleaned = response.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        // CRITICAL FIX: Remove any system instruction leakage
        // Check if the response contains system instructions that shouldn't be spoken
        String[] systemPatterns = {
            "You are ",
            "As a chess master",
            "Be natural and conversational",
            "Keep your response",
            "Sound like you're actually",
            "not giving a lecture",
            "authentic to your personality",
            "1-2 sentences",
            "brief comment",
            "React naturally",
            "Respond in character",
            "single natural statement",
            "Reply as",
            "Comment.",
            "React.",
            "You're passionate about",
            "You're precise, demanding",
            "You're dynamic, aggressive",
            "You're patient, diplomatic"
        };
        
        // Check if the response starts with any system pattern
        for (String pattern : systemPatterns) {
            if (cleaned.toLowerCase().contains(pattern.toLowerCase())) {
                Log.w(TAG, "⚠️ System instruction leak detected in response: " + pattern);
                // Try to extract just the actual dialogue
                // Look for actual chess content after the instruction
                int contentStart = findActualContentStart(cleaned);
                if (contentStart > 0 && contentStart < cleaned.length()) {
                    cleaned = cleaned.substring(contentStart).trim();
                    Log.d(TAG, "✅ Extracted actual content: " + cleaned);
                } else {
                    // If we can't extract content, return a fallback
                    Log.w(TAG, "❌ Could not extract content, using fallback");
                    return getFallbackResponse(master);
                }
            }
        }
        
        // Additional cleanup: Remove any remaining quotes
        cleaned = cleaned.replaceAll("^\"|\"$", "");

        return cleaned;
    }
    
    /**
     * Find where the actual chess dialogue starts in a response that contains system instructions
     */
    private int findActualContentStart(String text) {
        // Look for sentence boundaries after system instructions
        String[] boundaries = {". ", "! ", "? ", "\n"};
        int earliestBoundary = text.length();
        
        for (String boundary : boundaries) {
            int index = text.indexOf(boundary);
            if (index > 0 && index < earliestBoundary) {
                // Check if this is after some system instruction keywords
                String beforeBoundary = text.substring(0, index).toLowerCase();
                if (beforeBoundary.contains("you are") || 
                    beforeBoundary.contains("as ") ||
                    beforeBoundary.contains("keep your") ||
                    beforeBoundary.contains("be ")) {
                    earliestBoundary = index + boundary.length();
                }
            }
        }
        
        return earliestBoundary < text.length() ? earliestBoundary : -1;
    }

    private String getFallbackResponse(String master) {
        switch (master.toLowerCase()) {
            case "tal": return "This is getting interesting!";
            case "fischer": return "Let's see what happens.";
            case "kasparov": return "The battle continues!";
            default: return "This should be interesting...";
        }
    }

    private String extractWinner(String result) {
        if (result == null) return null;
        String lower = result.toLowerCase();
        if (lower.contains("white wins")) return "white";
        if (lower.contains("black wins")) return "black";
        return null;
    }

    public void setUserInteractionEnabled(boolean enabled) {
        this.userInteractionEnabled = enabled;
    }

    public void handleUserComment(String comment, String whitePlayer, String blackPlayer,
                                  String gamePhase, DialogueCallback callback) {
        if (!userInteractionEnabled || comment == null || comment.trim().isEmpty()) return;
        processUserComment(comment.trim(), whitePlayer, blackPlayer, gamePhase, callback);
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public String getCurrentlySpeaking() {
        return currentlySpeaking;
    }

    public void stopCurrentSpeech() {
        if (ttsService != null && isSpeaking) {
            ttsService.interrupt();
        }
        cleanupTTSState();
    }

    /**
     * CRITICAL: Force stop all dialogue immediately to prevent ANR
     */
    public void forceStop() {
        Log.d(TAG, "🚨 FORCE STOPPING AIDialogueManager");
        
        // Stop all speech immediately
        if (ttsService != null) {
            ttsService.stopSpeaking();
        }
        
        // Stop conversations
        conversationInProgress = false;
        isSpeaking = false;
        
        // Clear all queues
        speakerQueueCount.clear();
        conversationHistory.clear();
        
        // Stop executors aggressively
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        
        // Clear handlers
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        
        Log.d(TAG, "✅ AIDialogueManager FORCE STOPPED");
    }

    public void cleanup() {
        stopCurrentSpeech();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        resetConversation();
        Log.d(TAG, "🧹 AIDialogueManager cleaned up");
    }
}