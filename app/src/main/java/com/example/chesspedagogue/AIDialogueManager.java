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
import java.util.HashMap;

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
    private final ThreeStageResponseManager threeStageManager;
    private final EnhancedContextManager contextManager;
    private final ConversationVarietyManager varietyManager;

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
        
        // Initialize ThreeStageResponseManager for rich, multi-stage commentary
        this.threeStageManager = ThreeStageResponseManager.getInstance(context);
        
        // Initialize Enhanced Context Manager for emergent behavior
        this.contextManager = EnhancedContextManager.getInstance();
        
        // Initialize Conversation Variety Manager for response diversification
        this.varietyManager = ConversationVarietyManager.getInstance(context);
        
        // Set context for spectator mode (enhanced quality eleven_turbo_v2_5)
        TTSServiceManager.setUsageContext(context, "spectator_mode");

        Log.d(TAG, "🎭 ENHANCED AI Dialogue Manager initialized with contextual awareness!");
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
                
                // Add to context manager
                contextManager.addConversationTurn(firstSpeaker, endStatement, "endgame");

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

                // Add to both local and enhanced context
                conversationHistory.add(new ConversationTurn(firstSpeaker, openingStatement, "game_opening"));
                contextManager.addConversationTurn(firstSpeaker, openingStatement, "game_opening");
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
                
                // Reset conversation history periodically to prevent repetitive context
                if (conversationHistory.size() > 8) {
                    Log.d(TAG, "🔄 Clearing conversation history to prevent repetition");
                    conversationHistory.clear();
                }

                // Use async ThreeStageResponseManager for enhanced commentary
                generateNaturalMoveCommentAsync(move, commentator, playerWhoMoved, whitePlayer, blackPlayer, moveNumber, 
                    new DialogueCallback() {
                        @Override
                        public void onDialogueGenerated(String speaker, String dialogue) {
                            // Add to both conversation tracking systems
                            conversationHistory.add(new ConversationTurn(speaker, dialogue, "move_" + moveNumber));
                            contextManager.addConversationTurn(speaker, dialogue, "move_" + moveNumber);
                            lastSpeaker = speaker;
                            lastStatement = dialogue;
                            
                            callback.onDialogueGenerated(speaker, dialogue);
                            speakDialogueWithPersonalityAndEmotion(speaker, dialogue, "move", currentEvaluation);

                            // FIXED: Only trigger conversation if limits allow
                            String otherPlayer = speaker.equals(whitePlayer) ? blackPlayer : whitePlayer;
                            if (shouldTriggerResponse(dialogue) && canStartNewConversation()) {
                                startConversation();
                                scheduleConversationResponse(otherPlayer, speaker, dialogue, "move_response", callback);
                            }
                        }
                        
                        @Override
                        public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                            callback.onConversationStarted(respondingSpeaker, triggerStatement);
                        }
                        
                        @Override
                        public void onConversationComplete(String finalSpeaker, String finalStatement) {
                            callback.onConversationComplete(finalSpeaker, finalStatement);
                        }
                        
                        @Override
                        public void onError(String error) {
                            callback.onError(error);
                        }
                    }
                );

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

                // Add the response to context manager first
                contextManager.addConversationTurn(originalSpeaker, triggerStatement, context);
                
                // Get enhanced context for API call
                List<Map<String, String>> enhancedContext = contextManager.getContextForApiCall(responder, originalSpeaker);
                
                // Add the current prompt as a user message
                Map<String, String> currentPrompt = new HashMap<>();
                currentPrompt.put("role", "user");
                currentPrompt.put("content", String.format("%s just said: \"%s\". Respond naturally as %s in this competitive game.",
                        originalSpeakerName, triggerStatement, responderName));
                enhancedContext.add(currentPrompt);
                
                // Select the correct model
                String modelId = modelManager.getSelectedModelId();
                if (!modelId.startsWith("ft:")) {
                    modelId = null; // Use default if not fine-tuned
                }
                
                // Use enhanced context for response generation
                String response = openAIService.getChatCompletionWithEnhancedContext(enhancedContext, modelId);
                String cleanedResponse = cleanAndPersonalize(response, responder);
                
                // Add response to context
                contextManager.addConversationTurn(responder, cleanedResponse, context);
                
                conversationHistory.add(new ConversationTurn(responder, cleanedResponse, context));
                lastSpeaker = responder;
                lastStatement = cleanedResponse;

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(responder, cleanedResponse);
                    speakDialogueWithPersonality(responder, cleanedResponse);

                    // Check if conversation should continue
                    if (conversationTurnCount < MAX_CONVERSATION_TURNS &&
                            shouldContinueConversation(cleanedResponse) &&
                            canStartNewConversation()) {

                        scheduleConversationResponse(originalSpeaker, responder, cleanedResponse, "conversation_continue", callback);
                    } else {
                        // End the conversation
                        endConversation();
                        callback.onConversationComplete(responder, cleanedResponse);
                        Log.d(TAG, "🎭 Conversation completed naturally");
                    }
                });
                
                /* REMOVED: Old ThreeStageResponseManager code
                // Use ThreeStageResponseManager for competitive back-and-forth
                threeStageManager.processThreeStageResponse(
                    conversationPrompt,
                    String.format("Chess conversation: %s responding to %s", responderName, originalSpeakerName),
                    ThreeStageResponseManager.ResponseMode.FAST_ONLY, // Fast responses for dynamic conversation
                    new ThreeStageResponseManager.ThreeStageCallback() {
                        @Override
                        public void onStageResponse(ThreeStageResponseManager.ResponseStage stage, String response, boolean isFinal) {
                            if (stage == ThreeStageResponseManager.ResponseStage.STAGE_1_QUICK || isFinal) {
                                String cleanedResponse = cleanAndPersonalize(response, responder);
                                
                                conversationHistory.add(new ConversationTurn(responder, cleanedResponse, context));
                                lastSpeaker = responder;
                                lastStatement = cleanedResponse;

                                mainHandler.post(() -> {
                                    callback.onDialogueGenerated(responder, cleanedResponse);
                                    speakDialogueWithPersonality(responder, cleanedResponse);

                                    // Check if conversation should continue
                                    if (conversationTurnCount < MAX_CONVERSATION_TURNS &&
                                            shouldContinueConversation(cleanedResponse) &&
                                            canStartNewConversation()) {

                                        scheduleConversationResponse(originalSpeaker, responder, cleanedResponse, "conversation_continue", callback);
                                    } else {
                                        // End the conversation
                                        endConversation();
                                        callback.onConversationComplete(responder, cleanedResponse);
                                        Log.d(TAG, "🎭 Conversation completed naturally");
                                    }
                                });
                            }
                        }
                        
                        @Override
                        public void onAllStagesComplete(String finalResponse) {
                            Log.d(TAG, "✅ ThreeStage conversation response completed for " + responder);
                        }
                        
                        @Override
                        public void onStageError(ThreeStageResponseManager.ResponseStage stage, String error) {
                            Log.e(TAG, "❌ ThreeStage conversation error: " + error);
                            // Fallback to end conversation
                            mainHandler.post(() -> {
                                endConversation();
                                callback.onError("Conversation error: " + error);
                            });
                */ // End of removed code
                
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
        contextManager.resetConversation();
        Log.d(TAG, "🔄 Emotional state and context reset for new game");
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
        prompt.append("\nRespond briefly as a competitive chess player focused on the current position. " +
                     "Reference specific aspects of the game - pieces, squares, tactical ideas, or strategic themes. " +
                     "Show your chess personality through your analysis of the position, not through generic teaching. " +
                     "Speak as a player engaged in competitive analysis, not as a coach giving advice.");

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
                    "%s\n\nA spectator said: \"%s\" and another master responded: \"%s\"\n" +
                    "You may add a brief competitive comment focusing on the chess position, tactical ideas, or strategic assessment. " +
                    "Speak as a fellow competitive player engaged in position analysis, or stay silent. " +
                    "Reference specific chess concepts, pieces, or plans - avoid generic teaching.",
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

        // Get enhanced context
        List<Map<String, String>> context = contextManager.getContextForApiCall(speaker, opponent);
        
        // Add specific prompt for opening
        Map<String, String> openingPrompt = new HashMap<>();
        openingPrompt.put("role", "user");
        openingPrompt.put("content", String.format(
                "You're about to start a chess game against %s. Give a brief pre-game comment showing your competitive spirit and personality. Reference your history with this opponent if relevant.",
                opponentName
        ));
        context.add(openingPrompt);
        
        // Select the correct model
        String modelId = modelManager.getSelectedModelId();
        if (!modelId.startsWith("ft:")) {
            modelId = null;
        }
        
        String response = openAIService.getChatCompletionWithEnhancedContext(context, modelId);
        return cleanAndPersonalize(response, speaker);
    }

    private void generateNaturalMoveCommentAsync(String move, String commentator, String playerWhoMoved, 
                                                String whitePlayer, String blackPlayer, int moveNumber, DialogueCallback callback) {
        String commentatorName = modelManager.getMasterDisplayName(commentator);
        String playerName = modelManager.getMasterDisplayName(playerWhoMoved);

        boolean commentingOnOwnMove = commentator.equals(playerWhoMoved);
        String opponent = commentator.equals(whitePlayer) ? blackPlayer : whitePlayer;
        
        executorService.execute(() -> {
            try {
                // Get enhanced context
                List<Map<String, String>> context = contextManager.getContextForApiCall(commentator, opponent);
                
                // Create move-specific prompt
                Map<String, String> movePrompt = new HashMap<>();
                movePrompt.put("role", "user");
                
                if (commentingOnOwnMove) {
                    movePrompt.put("content", String.format(
                        "You just played %s on move %d. Give your immediate reaction and explain your thinking.",
                        move, moveNumber
                    ));
                } else {
                    movePrompt.put("content", String.format(
                        "%s just played %s on move %d. What's your reaction to this move?",
                        playerName, move, moveNumber
                    ));
                }
                context.add(movePrompt);
                
                // Get model for this master
                String modelId = modelManager.getSelectedModelId();
                if (!modelId.startsWith("ft:")) {
                    modelId = null;
                }
                
                // Generate response with context
                String response = openAIService.getChatCompletionWithEnhancedContext(context, modelId);
                String cleanedResponse = cleanAndPersonalize(response, commentator);
                
                mainHandler.post(() -> callback.onDialogueGenerated(commentator, cleanedResponse));
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating move comment", e);
                String fallback = getFallbackResponse(commentator);
                mainHandler.post(() -> callback.onDialogueGenerated(commentator, fallback));
            }
        });
        
        /* REMOVED: Old ThreeStageResponseManager code
        // Use ThreeStageResponseManager for rich, progressive commentary
        threeStageManager.processThreeStageResponse(
            moveAnalysisPrompt,
            String.format("Chess move analysis: %s by %s (move %d)", move, commentatorName, moveNumber),
            ThreeStageResponseManager.ResponseMode.ENHANCED_ONLY, // Perfect for spectator mode
            new ThreeStageResponseManager.ThreeStageCallback() {
                @Override
                public void onStageResponse(ThreeStageResponseManager.ResponseStage stage, String response, boolean isFinal) {
                    if (stage == ThreeStageResponseManager.ResponseStage.STAGE_2_ENHANCED || isFinal) {
                        // Use enhanced response for more personality and depth
                        String cleanedResponse = cleanAndPersonalize(response, commentator);
                        mainHandler.post(() -> callback.onDialogueGenerated(commentator, cleanedResponse));
                    }
                }
                
                @Override
                public void onAllStagesComplete(String finalResponse) {
                    Log.d(TAG, "✅ ThreeStage commentary completed for " + commentator);
                }
                
                @Override
                public void onStageError(ThreeStageResponseManager.ResponseStage stage, String error) {
                    Log.e(TAG, "❌ ThreeStage error in " + stage + ": " + error);
                    // Fallback to basic response
                    String fallback = getFallbackResponse(commentator);
                    mainHandler.post(() -> callback.onDialogueGenerated(commentator, fallback));
                } // End removed callback methods
            }
        ); // End removed processThreeStageResponse
        */
    }

    private String getGameContext(int moveNumber, String move) {
        if (moveNumber <= 10) return "This is the opening. ";
        else if (moveNumber <= 25) return "The middlegame is developing. ";
        else return "We're in the endgame. ";
    }

    private String generateNaturalEndGameStatement(String speaker, String result) {
        String speakerName = modelManager.getMasterDisplayName(speaker);
        
        // Get enhanced context - this will include the whole game conversation
        List<Map<String, String>> context = contextManager.getContextForApiCall(speaker, "");
        
        // Add specific prompt for endgame
        Map<String, String> endgamePrompt = new HashMap<>();
        endgamePrompt.put("role", "user");
        endgamePrompt.put("content", String.format(
                "The game has ended with result: %s. Give your immediate reaction and reflection on the game, mentioning any key moments or memorable exchanges.",
                result
        ));
        context.add(endgamePrompt);
        
        // Select the correct model
        String modelId = modelManager.getSelectedModelId();
        if (!modelId.startsWith("ft:")) {
            modelId = null;
        }
        
        String response = openAIService.getChatCompletionWithEnhancedContext(context, modelId);
        return cleanAndPersonalize(response, speaker);
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
     * Create enhanced conversational prompt with opponent awareness
     */
    private String createContextAwarePrompt(String speaker, String opponent, String instruction) {
        String speakerPersonality = getSpectatorPersonalityNote(speaker);
        String opponentContext = getOpponentRelationshipContext(speaker, opponent);
        
        return String.format("%s\n\n%s\n\n%s", speakerPersonality, opponentContext, instruction);
    }
    

    /**
     * Get opponent relationship context - provides rich historical and stylistic awareness
     */
    private String getOpponentRelationshipContext(String speaker, String opponent) {
        String key = speaker.toLowerCase() + "_vs_" + opponent.toLowerCase();
        
        switch (key) {
            // Tal relationships
            case "tal_vs_fischer":
                return "You remember Fischer as a formidable opponent who respects tactical brilliance but plays with cold precision. You've always enjoyed the contrast between his calculating style and your intuitive sacrifices.";
            case "tal_vs_kasparov":
                return "Kasparov reminds you of yourself in some ways - dynamic and attacking. You appreciate his fighting spirit, though you sometimes find his approach less purely artistic than your own.";
            case "tal_vs_karpov":
                return "Karpov represents everything opposite to your style - patient, positional, methodical. You've always relished the challenge of breaking through his solid defenses with tactical shots.";
            case "tal_vs_carlsen":
                return "This young Norwegian has a practical style that's hard to crack. You respect his ability to find resources in any position, though you sometimes wish he'd take more artistic risks.";
            case "tal_vs_kramnik":
                return "Kramnik's deep positional understanding is impressive, but you've always believed that tactical vision can overcome even the soundest positional play.";

            // Fischer relationships  
            case "fischer_vs_tal":
                return "Tal's tactical wizardry is undeniable, but you know that precise calculation and sound positional play can neutralize even the most brilliant combinations. You respect his genius while trusting your superior preparation.";
            case "fischer_vs_kasparov":
                return "Kasparov has the dynamism and fighting spirit you appreciate. You see him as perhaps the only player who could match your level of preparation and competitive fire.";
            case "fischer_vs_karpov":
                return "Karpov's style is solid but predictable. You've always felt confident that your superior understanding of chess truth would eventually break through his defenses.";
            case "fischer_vs_carlsen":
                return "This Norwegian plays with a practical strength that you respect. His endgame technique reminds you of your own pursuit of objective truth in chess.";
            case "fischer_vs_kramnik":
                return "Kramnik's methodical approach and deep preparation resonate with your own style. You see him as someone who truly understands positional chess.";

            // Kasparov relationships
            case "kasparov_vs_tal":
                return "Tal was your inspiration - pure chess artistry in motion. You learned from his tactical genius while developing your own more universal style. Fighting him is both an honor and a challenge.";
            case "kasparov_vs_fischer":
                return "Fischer set the standard for chess excellence. You've studied every game, respecting his precision while believing your dynamic style could challenge his methodical approach.";
            case "kasparov_vs_karpov":
                return "Your eternal rival Karpov - you know each other's games better than anyone. Every move is a psychological battle built on years of competition and mutual respect.";
            case "kasparov_vs_carlsen":
                return "Carlsen represents the new generation, combining your fighting spirit with modern technique. You're curious to test your experience against his natural talent.";
            case "kasparov_vs_kramnik":
                return "Kramnik, your former student who eventually defeated you. You respect his growth while still believing your dynamic style can create the complications he prefers to avoid.";

            // Karpov relationships
            case "karpov_vs_tal":
                return "Tal's brilliance always challenged your methodical approach. You've learned to appreciate his artistry while trusting that patient, precise play will ultimately prevail.";
            case "karpov_vs_fischer":
                return "Fischer's preparation and precision earned your deep respect. You see similarities in your positional understanding, though your styles developed differently.";
            case "karpov_vs_kasparov":
                return "Kasparov, your greatest rival and perhaps the only player who truly pushed you to your limits. Every game is a battle between his dynamism and your precision.";
            case "karpov_vs_carlsen":
                return "Carlsen plays with a modern understanding that reminds you of your own systematic approach, though with more willingness to take risks in equal positions.";
            case "karpov_vs_kramnik":
                return "Kramnik's deep positional sense resonates with your own style. You appreciate his methodical approach to chess improvement and understanding.";

            // Carlsen relationships
            case "carlsen_vs_tal":
                return "Tal's games were pure magic - the kind of chess that made you fall in love with the game. You try to channel some of that creativity while staying practical.";
            case "carlsen_vs_fischer":
                return "Fischer was the ultimate perfectionist. You admire his pursuit of chess truth, though you prefer a more flexible, adaptive approach to different positions.";
            case "carlsen_vs_kasparov":
                return "Kasparov was chess dynamism personified. You learned from studying his aggressive style while developing your own more patient but no less fighting approach.";
            case "carlsen_vs_karpov":
                return "Karpov showed how to win through small advantages and perfect technique. Your style incorporates his patience but with more willingness to complicate when needed.";
            case "carlsen_vs_kramnik":
                return "Kramnik's deep understanding and solid style earned your respect early in your career. You appreciate his contribution to modern positional chess.";

            // Kramnik relationships
            case "kramnik_vs_tal":
                return "Tal represented pure chess art. You admire his tactical vision while preferring the deep, methodical analysis that reveals chess truth through calculation.";
            case "kramnik_vs_fischer":
                return "Fischer was the master of chess objectivity. You studied his games extensively, learning how proper preparation and sound judgment lead to victory.";
            case "kramnik_vs_kasparov":
                return "Kasparov, your former mentor and the player you eventually defeated for the world championship. You learned his dynamic style before developing your own more solid approach.";
            case "kramnik_vs_karpov":
                return "Karpov's systematic positional play influenced your development. You see similarities in your methodical approaches to chess understanding.";
            case "kramnik_vs_carlsen":
                return "Carlsen combines practical strength with deep understanding. You respect his ability to find resources and his patient approach to improving positions.";

            // Additional masters - Alekhine
            case "alekhine_vs_tal":
                return "Tal's tactical magic reminds you of your own combinatorial style, though you prefer deeper calculation. You appreciate his artistic approach to chess.";
            case "alekhine_vs_fischer":
                return "Fischer's precision and preparation echo your own dedication to chess truth. You respect his methodical approach while trusting your combinatorial vision.";
            case "alekhine_vs_kasparov":
                return "Kasparov's dynamic style and deep preparation remind you of your own approach. You see him as a kindred spirit in the pursuit of chess complexity.";
            case "alekhine_vs_karpov":
                return "Karpov's patient positional play challenges your combinatorial style. You relish the opportunity to create the complications he seeks to avoid.";
            case "alekhine_vs_carlsen":
                return "Carlsen's practical strength and endgame technique are impressive. You're curious how your combinatorial style would fare against his resourcefulness.";
            case "alekhine_vs_kramnik":
                return "Kramnik's deep positional understanding resonates with your own analytical approach. You appreciate his methodical style while preferring more tactical complexity.";

            // Capablanca relationships  
            case "capablanca_vs_tal":
                return "Tal's tactical brilliance was entertaining, though you believe in the superiority of simple, natural moves over complicated tactics.";
            case "capablanca_vs_fischer":
                return "Fischer understood chess truth as clearly as you did. You respect his pursuit of objective play, seeing similarities in your natural approaches.";
            case "capablanca_vs_kasparov":
                return "Kasparov's energy and preparation are impressive, though you believe natural understanding often trumps extensive preparation.";
            case "capablanca_vs_alekhine":
                return "Alekhine was your greatest rival, the one player whose combinatorial depth could challenge your natural understanding. Every game was a battle of styles.";

            // Morphy relationships
            case "morphy_vs_tal":
                return "Tal's sacrificial style echoes your own attacking instincts, though you prefer the direct approach of rapid development and piece activity.";
            case "morphy_vs_fischer":
                return "Fischer's precision reminds you of your own pursuit of chess truth, though you achieved it through natural development rather than deep preparation.";
            case "morphy_vs_kasparov":
                return "Kasparov's attacking fervor and fighting spirit mirror your own approach to chess combat. You appreciate his refusal to accept easy draws.";

            // Lasker relationships
            case "lasker_vs_tal":
                return "Tal's brilliance was undeniable, though you know that practical chess often requires psychological understanding beyond pure tactics.";
            case "lasker_vs_fischer":
                return "Fischer's objectivity was impressive, but you've always believed that understanding your opponent's psychology is as important as understanding the position.";
            case "lasker_vs_kasparov":
                return "Kasparov's fighting spirit reminds you of your own refusal to accept inferior positions. You appreciate his psychological approach to chess competition.";

            // Anand relationships
            case "anand_vs_tal":
                return "Tal's games were pure inspiration - the kind of creative chess that made you love the game. You try to channel that artistry in your own play.";
            case "anand_vs_fischer":
                return "Fischer's preparation and accuracy set the standard you've always tried to match. You admire his pursuit of objective truth in chess.";
            case "anand_vs_kasparov":
                return "Kasparov was the ultimate competitor - someone who could adapt any style needed to win. You learned much from studying his versatility.";
            case "anand_vs_karpov":
                return "Karpov's technique and patience influenced your positional understanding. You appreciate his ability to win from seemingly equal positions.";
            case "anand_vs_carlsen":
                return "Carlsen represents the new generation of chess understanding. You respect his practical strength while drawing on your broader experience.";

            // Botvinnik relationships
            case "botvinnik_vs_tal":
                return "Tal was a brilliant pupil whose tactical genius sometimes overshadowed the importance of systematic preparation. You admire his artistry while preferring scientific method.";
            case "botvinnik_vs_fischer":
                return "Fischer's systematic approach to chess improvement echoes your own scientific method. You respect his dedication to finding chess truth through preparation.";
            case "botvinnik_vs_kasparov":
                return "Kasparov was perhaps your greatest student, combining your systematic approach with his own dynamic energy. You're proud of his development while ready to compete.";

            default:
                // Generic fallback for other combinations
                return String.format("You know %s well from studying their games and style. You understand their strengths and are prepared for their typical approaches.", 
                    modelManager.getMasterDisplayName(opponent));
        }
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
            case "tal": return "The position is calling for a sacrifice! I can feel the combinations brewing.";
            case "fischer": return "I see the winning continuation. This position demands absolute precision.";
            case "kasparov": return "The initiative is what matters here. I'm going to increase the pressure!";
            case "carlsen": return "I'll find the most practical solution. There's always a way to squeeze an advantage.";
            case "karpov": return "Patience is key. I'll accumulate small advantages until they become decisive.";
            case "kramnik": return "The position structure tells me everything I need to know about the best plan.";
            case "capablanca": return "The natural move here is obvious once you understand the position's demands.";
            case "alekhine": return "The complications favor me. I see patterns that my opponent has missed.";
            case "morphy": return "Development and attack! The pieces must coordinate for maximum effect.";
            case "lasker": return "Psychology is as important as calculation. What would my opponent least expect?";
            case "anand": return "My intuition tells me this is the critical moment. Time to strike!";
            case "botvinnik": return "The scientific approach demands systematic evaluation of all candidate moves.";
            default: return "The position holds many secrets. Let me show you what I see.";
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