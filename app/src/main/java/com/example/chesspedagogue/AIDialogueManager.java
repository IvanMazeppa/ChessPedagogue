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
 * 🎭 ENHANCED AI DIALOGUE MANAGER with TTS Integration
 *
 * Creates authentic, flowing conversations between AI chess masters during spectator games
 * with full voice synthesis using personality-specific accents and speech patterns!
 */
public class AIDialogueManager {
    private static final String TAG = "AIDialogueManager";

    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final OpenAIService openAIService;
    private final FineTunedModelManager modelManager;
    private final OpenAITTSService ttsService; // 🎵 ADDED: TTS Integration!

    // Conversation state tracking
    private String lastSpeaker = "";
    private String lastStatement = "";
    private List<ConversationTurn> conversationHistory = new ArrayList<>();
    private boolean conversationInProgress = false;
    private int conversationTurnCount = 0;

    // TTS state tracking - ADDED
    private boolean isSpeaking = false;
    private String currentlySpeaking = "";

    // Conversation triggers and timing
    private static final int MAX_CONVERSATION_TURNS = 4;
    private static final long CONVERSATION_DELAY_MS = 2000;
    private static final long TTS_DELAY_MS = 500; // Brief pause before speaking

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
        this.ttsService = OpenAITTSService.getInstance(context); // 🎵 INITIALIZE TTS!

        Log.d(TAG, "🎭 Enhanced AI Dialogue Manager initialized with TTS support!");
    }

    /**
     * 🏁 Generate end game dialogue
     */
    public void generateEndGameDialogue(String result, String whitePlayer, String blackPlayer, DialogueCallback callback) {
        Log.d(TAG, "🏁 Generating end game dialogue for: " + result);

        resetConversation(); // Reset for end game

        executorService.execute(() -> {
            try {
                // Both players should comment on the end
                String winner = extractWinner(result);
                String firstSpeaker = winner != null ? winner : whitePlayer;
                String secondSpeaker = firstSpeaker.equals(whitePlayer) ? blackPlayer : whitePlayer;

                String endStatement = generateNaturalEndGameStatement(firstSpeaker, result);

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(firstSpeaker, endStatement);

                    // 🎵 TRIGGER TTS with personality voice!
                    speakDialogueWithPersonality(firstSpeaker, endStatement);

                    // Always trigger a response for game end
                    scheduleConversationResponse(secondSpeaker, firstSpeaker, endStatement, "endgame_response", callback);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error generating end game dialogue", e);
                mainHandler.post(() -> callback.onError("Failed to generate end game dialogue"));
            }
        });
    }

    /**
     * CRITICAL FIX: Enhanced speakDialogueWithPersonality method in AIDialogueManager.java
     * This ensures Tal (and all masters) properly trigger TTS with accent instructions
     */
    private void speakDialogueWithPersonality(String speaker, String dialogue) {
        Log.d(TAG, "🎵 DEBUGGING TTS CALL FOR SPEAKER: " + speaker);
        Log.d(TAG, "   Dialogue: " + dialogue.substring(0, Math.min(50, dialogue.length())) + "...");
        Log.d(TAG, "   Current isSpeaking state: " + isSpeaking);
        Log.d(TAG, "   Currently speaking: " + currentlySpeaking);

        try {
            if (isSpeaking) {
                Log.d(TAG, "⏸️ Someone is already speaking (" + currentlySpeaking + "), queuing " + speaker + "'s dialogue for later");
                mainHandler.postDelayed(() -> speakDialogueWithPersonality(speaker, dialogue), 2000);
                return;
            }

            isSpeaking = true;
            currentlySpeaking = speaker;

            Log.d(TAG, "🎭 STARTING TTS FOR: " + speaker);

            // CRITICAL FIX: Force the TTS service to use the correct master
            // Set the master preference before calling TTS
            SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String currentMaster = masterPrefs.getString("selected_master", "tal");

            Log.d(TAG, "   Original selected master: " + currentMaster);
            Log.d(TAG, "   Speaker requesting TTS: " + speaker);

            // TEMPORARILY set the master to the speaker for this TTS call
            SharedPreferences.Editor editor = masterPrefs.edit();
            editor.putString("selected_master", speaker.toLowerCase());
            editor.apply();

            Log.d(TAG, "   ✅ TEMPORARILY SET MASTER TO: " + speaker);

            // Create a completion callback that restores the original master
            OpenAITTSService.TTSCallback enhancedCallback = new OpenAITTSService.TTSCallback() {
                @Override
                public void onSpeechStarted() {
                    Log.d(TAG, "🎤 " + speaker + " started speaking with enhanced accent system");
                }

                @Override
                public void onSpeechReady(File audioFile) {
                    Log.d(TAG, "🎧 Enhanced audio ready for " + modelManager.getMasterDisplayName(speaker));
                }

                @Override
                public void onSpeechCompleted() {
                    Log.d(TAG, "🎵 " + speaker + " finished speaking with authentic voice");

                    // CRITICAL: Restore the original master selection
                    SharedPreferences.Editor restoreEditor = masterPrefs.edit();
                    restoreEditor.putString("selected_master", currentMaster);
                    restoreEditor.apply();

                    Log.d(TAG, "   ✅ RESTORED ORIGINAL MASTER TO: " + currentMaster);

                    isSpeaking = false;
                    currentlySpeaking = "";
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "❌ Enhanced TTS error for " + speaker + ": " + errorMessage);

                    // CRITICAL: Restore the original master even on error
                    SharedPreferences.Editor restoreEditor = masterPrefs.edit();
                    restoreEditor.putString("selected_master", currentMaster);
                    restoreEditor.apply();

                    Log.d(TAG, "   ✅ RESTORED ORIGINAL MASTER AFTER ERROR TO: " + currentMaster);

                    isSpeaking = false;
                    currentlySpeaking = "";
                }
            };

            // Get the appropriate voice for this specific speaker
            String voiceToUse = modelManager.getVoiceForMaster(speaker);

            Log.d(TAG, "🎭 Using voice '" + voiceToUse + "' for " + speaker + " with enhanced accent instructions");

            // CRITICAL: Use the enhanced TTS method that should have accent instructions
            Log.d(TAG, "🚀 CALLING TTS SERVICE FOR " + speaker.toUpperCase());

            // Use the direct speak method which should trigger our enhanced generateTTSChunk
            ttsService.speak(dialogue, new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    // Delegate to our enhanced callback
                    enhancedCallback.onSpeechCompleted();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR speaking dialogue for " + speaker, e);
            isSpeaking = false;
            currentlySpeaking = "";

            // Restore master selection on any error
            try {
                SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
                String originalMaster = masterPrefs.getString("selected_master", "tal");
                Log.d(TAG, "   🔄 Exception recovery: restoring master to " + originalMaster);
            } catch (Exception restoreError) {
                Log.e(TAG, "Error during master selection recovery", restoreError);
            }
        }
    }

    /**
     * ENHANCED: Generate opening dialogue with explicit speaker identification
     */
    public void generateOpeningDialogue(String whitePlayer, String blackPlayer, DialogueCallback callback) {
        Log.d(TAG, "🎬 ENHANCED: Generating opening dialogue: " + whitePlayer + " vs " + blackPlayer);

        resetConversation();

        executorService.execute(() -> {
            try {
                // Randomly choose who speaks first
                String firstSpeaker = Math.random() > 0.5 ? whitePlayer : blackPlayer;
                String opponent = firstSpeaker.equals(whitePlayer) ? blackPlayer : whitePlayer;

                Log.d(TAG, "   First speaker selected: " + firstSpeaker);
                Log.d(TAG, "   Opponent: " + opponent);

                String openingStatement = generateNaturalOpeningStatement(firstSpeaker, opponent);

                Log.d(TAG, "   Generated statement: " + openingStatement);

                // Record this as the first turn
                conversationHistory.add(new ConversationTurn(firstSpeaker, openingStatement, "game_opening"));
                lastSpeaker = firstSpeaker;
                lastStatement = openingStatement;

                mainHandler.post(() -> {
                    Log.d(TAG, "🎭 TRIGGERING DIALOGUE CALLBACK FOR: " + firstSpeaker);
                    callback.onDialogueGenerated(firstSpeaker, openingStatement);

                    // 🎵 CRITICAL: Trigger TTS with explicit speaker identification
                    Log.d(TAG, "🚀 ABOUT TO CALL speakDialogueWithPersonality FOR: " + firstSpeaker);
                    speakDialogueWithPersonality(firstSpeaker, openingStatement);

                    // Check if this should trigger a response
                    if (shouldTriggerResponse(openingStatement)) {
                        Log.d(TAG, "   📝 Scheduling response from: " + opponent);
                        scheduleConversationResponse(opponent, firstSpeaker, openingStatement, "opening_response", callback);
                    } else {
                        Log.d(TAG, "   ⏸️ No response triggered for this statement");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ CRITICAL ERROR generating opening dialogue", e);
                mainHandler.post(() -> callback.onError("Failed to generate opening dialogue"));
            }
        });
    }

    /**
     * ENHANCED: Generate move dialogue with explicit speaker tracking
     */
    public void generateMoveDialogue(String move, String playerWhoMoved, String whitePlayer,
                                     String blackPlayer, int moveNumber, DialogueCallback callback) {

        Log.d(TAG, "🎯 ENHANCED: Generating move dialogue for " + move + " by " + playerWhoMoved);
        Log.d(TAG, "   White: " + whitePlayer + ", Black: " + blackPlayer);
        Log.d(TAG, "   Move number: " + moveNumber);

        // Don't interrupt ongoing conversations
        if (conversationInProgress) {
            Log.d(TAG, "⏸️ Conversation in progress, skipping move dialogue");
            return;
        }

        executorService.execute(() -> {
            try {
                // Choose who comments on the move
                String commentator = chooseCommentator(playerWhoMoved, whitePlayer, blackPlayer, moveNumber);
                Log.d(TAG, "   Selected commentator: " + commentator);

                String moveComment = generateNaturalMoveComment(move, commentator, playerWhoMoved, moveNumber);
                Log.d(TAG, "   Generated comment: " + moveComment);

                // Record this turn
                conversationHistory.add(new ConversationTurn(commentator, moveComment, "move_" + moveNumber));
                lastSpeaker = commentator;
                lastStatement = moveComment;

                mainHandler.post(() -> {
                    Log.d(TAG, "🎭 TRIGGERING MOVE DIALOGUE CALLBACK FOR: " + commentator);
                    callback.onDialogueGenerated(commentator, moveComment);

                    // 🎵 CRITICAL: Trigger TTS with explicit speaker identification
                    Log.d(TAG, "🚀 ABOUT TO CALL speakDialogueWithPersonality FOR MOVE BY: " + commentator);
                    speakDialogueWithPersonality(commentator, moveComment);

                    // Check if this should trigger a conversation
                    String otherPlayer = commentator.equals(whitePlayer) ? blackPlayer : whitePlayer;
                    if (shouldTriggerResponse(moveComment)) {
                        Log.d(TAG, "   📝 Scheduling conversation response from: " + otherPlayer);
                        scheduleConversationResponse(otherPlayer, commentator, moveComment, "move_response", callback);
                    } else {
                        Log.d(TAG, "   ⏸️ No conversation response triggered");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "❌ CRITICAL ERROR generating move dialogue", e);
                mainHandler.post(() -> callback.onError("Failed to generate move dialogue"));
            }
        });
    }

    /**
     * 🎬 ENHANCED: Generate opening statements that set up game-specific banter
     */
    private String generateNaturalOpeningStatement(String speaker, String opponent) {
        String speakerName = modelManager.getMasterDisplayName(speaker);
        String opponentName = modelManager.getMasterDisplayName(opponent);

        String prompt = createConversationalPrompt(speaker, String.format(
                "You are %s about to play a chess game against %s. Make a natural pre-game comment " +
                        "that shows your personality and perhaps hints at your playing style or approach. " +
                        "Sound like you're sitting across from %s before the first move. " +
                        "Be authentic to your historical personality - reference your style, famous games, or approach to chess. " +
                        "Examples: 'Ready for some tactical fireworks, %s?' or 'Let's see if you can handle my preparation' " +
                        "Keep it conversational and true to who you are as a chess master. 1-2 sentences maximum.",
                speakerName, opponentName, opponentName, opponentName
        ));

        return cleanAndPersonalize(openAIService.getChatCompletion(prompt, "Generate a personality-rich pre-game comment"), speaker);
    }

    /**
     * 🎯 ENHANCED: Generate natural move comments that reference the actual game
     */
    private String generateNaturalMoveComment(String move, String commentator, String playerWhoMoved, int moveNumber) {
        String commentatorName = modelManager.getMasterDisplayName(commentator);
        String playerName = modelManager.getMasterDisplayName(playerWhoMoved);

        boolean commentingOnOwnMove = commentator.equals(playerWhoMoved);

        // Get some game context for more interesting dialogue
        String gameContext = getGameContext(moveNumber, move);

        String prompt;
        if (commentingOnOwnMove) {
            prompt = createConversationalPrompt(commentator, String.format(
                    "You just played %s (move %d). %s " +
                            "Make a brief, natural comment about your move as if talking to your opponent. " +
                            "Reference the move itself or the position. Be authentic to %s's personality. " +
                            "Examples: 'This %s looks promising' or 'Time to put pressure on your kingside' or 'I like how this opens things up' " +
                            "Sound confident but conversational. Keep it to 1-2 sentences and mention the actual chess situation.",
                    move, moveNumber, gameContext, commentatorName, move
            ));
        } else {
            prompt = createConversationalPrompt(commentator, String.format(
                    "%s just played %s (move %d). %s " +
                            "React naturally to their move as if you're analyzing it in real-time. " +
                            "Reference what the move does or how it affects the position. Be authentic to %s's personality. " +
                            "Examples: 'Interesting %s' or 'I see you're going for the center' or 'That puts pressure on my knight' " +
                            "Sound like you're genuinely responding to what just happened on the board.",
                    playerName, move, moveNumber, gameContext, commentatorName, move
            ));
        }

        return cleanAndPersonalize(openAIService.getChatCompletion(prompt, "Generate a game-aware move comment"), commentator);
    }

    /**
     * 🎲 NEW: Get game context to make dialogue more specific
     */
    private String getGameContext(int moveNumber, String move) {
        if (moveNumber <= 10) {
            return "This is still the opening phase. ";
        } else if (moveNumber <= 25) {
            return "The middlegame is developing. ";
        } else {
            return "We're entering the endgame. ";
        }

        // Could enhance this further by analyzing the actual move
        // For now, keep it simple but effective
    }

    /**
     * 🏁 Generate natural end game statements
     */
    private String generateNaturalEndGameStatement(String speaker, String result) {
        String speakerName = modelManager.getMasterDisplayName(speaker);

        String prompt = createConversationalPrompt(speaker, String.format(
                "The game just ended with result: %s. As %s, make a natural comment about the game ending. " +
                        "Speak as if you're talking to your opponent right after the game. " +
                        "Be gracious whether you won, lost, or drew. Keep it authentic to your personality. " +
                        "Examples: 'Good game!' or 'Well fought' or 'That was quite a battle' " +
                        "Sound like a real person, not a computer analyzing the game!",
                result, speakerName
        ));

        return cleanAndPersonalize(openAIService.getChatCompletion(prompt, "Generate a natural endgame comment"), speaker);
    }

    /**
     * 🎭 Create conversational response
     */
    private void generateConversationResponse(String responder, String originalSpeaker,
                                              String triggerStatement, String context, DialogueCallback callback) {

        executorService.execute(() -> {
            try {
                conversationInProgress = true;
                conversationTurnCount++;

                String responderName = modelManager.getMasterDisplayName(responder);
                String originalSpeakerName = modelManager.getMasterDisplayName(originalSpeaker);

                String conversationPrompt = createConversationalPrompt(responder, String.format(
                        "%s just said: \"%s\"\n\n" +
                                "As %s, respond naturally to what they said. This is a conversation between two chess masters. " +
                                "Keep your response brief (1-2 sentences) and authentic to your personality. " +
                                "You might agree, disagree, add your own perspective, or ask a question. " +
                                "Sound like you're actually talking to %s, not giving a lecture. " +
                                "Examples: 'I agree!' or 'That's one way to look at it' or 'You always were aggressive' " +
                                "BE CONVERSATIONAL, not analytical!",
                        originalSpeakerName, triggerStatement, responderName, originalSpeakerName
                ));

                String response = cleanAndPersonalize(
                        openAIService.getChatCompletion(conversationPrompt, "Generate a conversational response"),
                        responder
                );

                // Record this turn
                conversationHistory.add(new ConversationTurn(responder, response, context));
                lastSpeaker = responder;
                lastStatement = response;

                mainHandler.post(() -> {
                    callback.onDialogueGenerated(responder, response);

                    // 🎵 SPEAK the response with personality voice!
                    speakDialogueWithPersonality(responder, response);

                    // Check if this conversation should continue
                    if (conversationTurnCount < MAX_CONVERSATION_TURNS && shouldContinueConversation(response)) {
                        scheduleConversationResponse(originalSpeaker, responder, response, "conversation_continue", callback);
                    } else {
                        // End the conversation
                        conversationInProgress = false;
                        conversationTurnCount = 0;
                        callback.onConversationComplete(responder, response);
                        Log.d(TAG, "🎭 Conversation completed naturally");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error generating conversation response", e);
                conversationInProgress = false;
                conversationTurnCount = 0;
                mainHandler.post(() -> callback.onError("Failed to generate conversation response"));
            }
        });
    }

    /**
     * ⏰ Schedule a conversation response with natural timing
     */
    private void scheduleConversationResponse(String responder, String originalSpeaker,
                                              String triggerStatement, String context, DialogueCallback callback) {

        Log.d(TAG, "⏰ Scheduling conversation response from " + responder);

        mainHandler.postDelayed(() -> {
            if (!conversationInProgress && conversationTurnCount >= MAX_CONVERSATION_TURNS) {
                Log.d(TAG, "⏸️ Conversation limit reached, not scheduling response");
                return;
            }

            callback.onConversationStarted(responder, triggerStatement);
            generateConversationResponse(responder, originalSpeaker, triggerStatement, context, callback);

        }, CONVERSATION_DELAY_MS);
    }

    /**
     * 🎯 Determine if a statement should trigger a response
     */
    private boolean shouldTriggerResponse(String statement) {
        if (statement == null || statement.trim().isEmpty()) {
            return false;
        }

        String lowerStatement = statement.toLowerCase();

        // Conversation triggers
        String[] triggers = {
                "interesting", "bold", "what do you think", "agree", "disagree",
                "remember", "always", "never", "brilliant", "mistake",
                "classic", "typical", "style", "approach", "strategy",
                "battle", "fight", "challenge", "ready", "see what", "make of this"
        };

        for (String trigger : triggers) {
            if (lowerStatement.contains(trigger)) {
                return true;
            }
        }

        // Questions always trigger responses
        if (lowerStatement.contains("?")) {
            return true;
        }

        // Random chance for natural conversation flow
        return Math.random() > 0.7; // 30% chance of spontaneous response
    }

    /**
     * 🎲 Choose who comments on a move
     */
    private String chooseCommentator(String playerWhoMoved, String whitePlayer, String blackPlayer, int moveNumber) {
        // Early game: more likely to comment on own moves
        // Later game: more likely to comment on opponent moves

        if (moveNumber <= 10) {
            // Early game - 70% chance player comments on own move
            return Math.random() > 0.3 ? playerWhoMoved :
                    (playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer);
        } else {
            // Later game - 60% chance opponent comments
            return Math.random() > 0.4 ?
                    (playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer) : playerWhoMoved;
        }
    }

    /**
     * 🔄 Check if conversation should continue
     */
    private boolean shouldContinueConversation(String lastResponse) {
        if (lastResponse == null) return false;

        String lower = lastResponse.toLowerCase();

        // Don't continue if response is very short or seems final
        if (lastResponse.length() < 20) return false;

        // Continue if it seems like there's more to discuss
        return lower.contains("but") || lower.contains("however") ||
                lower.contains("though") || lower.contains("?") ||
                lower.contains("what about") || lower.contains("consider");
    }

    /**
     * 🎭 Create personality-specific prompts
     */
    private String createConversationalPrompt(String master, String instruction) {
        String personalityNote = getPersonalityNote(master);

        return String.format(
                "You are %s, the chess grandmaster. %s\n\n%s\n\n" +
                        "Remember: You're having a conversation with another human being, not analyzing a position. " +
                        "Sound natural, authentic, and true to your personality. Keep responses conversational and brief.",
                modelManager.getMasterDisplayName(master), personalityNote, instruction
        );
    }

    /**
     * 🎭 Get personality notes for each master
     */
    private String getPersonalityNote(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "You're known for your brilliant tactical play and love of beautiful combinations. " +
                        "You're enthusiastic, creative, and passionate about chess. Speak with warmth and excitement.";
            case "fischer":
                return "You're known for your incredible precision and preparation. You're confident, direct, " +
                        "and uncompromising about chess excellence. Speak with authority and conviction.";
            case "kasparov":
                return "You're known for your aggressive, dynamic style and psychological warfare. " +
                        "You're intense, passionate, and competitive. Speak with energy and determination.";
            case "karpov":
                return "You're known for your patient, positional style and psychological insight. " +
                        "You're calm, methodical, and diplomatic. Speak with quiet confidence and wisdom.";
            case "kramnik":
                return "You're known for your solid, technical approach and deep preparation. " +
                        "You're analytical, measured, and precise. Speak thoughtfully and systematically.";
            default:
                return "You're a chess grandmaster with your own unique style and personality. " +
                        "Be authentic and speak naturally about chess.";
        }
    }

    /**
     * 🧹 Clean and personalize responses
     */
    private String cleanAndPersonalize(String response, String master) {
        if (response == null || response.trim().isEmpty()) {
            return getFallbackResponse(master);
        }

        String cleaned = response.trim();

        // Remove quotation marks if present
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        // Remove meta-language
        cleaned = cleaned.replaceAll("(?i)^(Let me |I would |I think |I believe |As .+, )", "");
        cleaned = cleaned.replaceAll("(?i)^(This |Looking at |Analyzing )", "");

        // Add personality touches
        return addPersonalityTouch(cleaned, master);
    }


    /**
     * ✨ FIXED: Add personality-specific touches WITHOUT excessive repetition
     */
    private String addPersonalityTouch(String response, String master) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }

        // MUCH MORE SELECTIVE: Only add personality touches rarely and contextually
        switch (master.toLowerCase()) {
            case "tal":
                // Only add "Beautiful chess!" for truly brilliant tactical content
                if (response.toLowerCase().contains("sacrifice") &&
                        response.toLowerCase().contains("brilliant") &&
                        !response.toLowerCase().contains("beautiful") &&
                        Math.random() > 0.95) { // Only 5% chance
                    return response + " Beautiful chess!";
                }
                break;

            case "fischer":
                // Only add perfectionist touch for accuracy-related content
                if (response.toLowerCase().contains("precise") &&
                        response.toLowerCase().contains("best") &&
                        Math.random() > 0.95) { // Only 5% chance
                    return response + " Perfection is essential.";
                }
                break;

            case "kasparov":
                // Only add fighting spirit for battle-related content
                if (response.toLowerCase().contains("fight") &&
                        response.toLowerCase().contains("battle") &&
                        Math.random() > 0.95) { // Only 5% chance
                    return response + " We must fight for every advantage!";
                }
                break;

            // Leave other masters clean to prevent any repetition
        }

        return response; // Return unchanged response most of the time
    }

    /**
     * 🆘 Fallback responses when generation fails
     */
    private String getFallbackResponse(String master) {
        String[][] fallbacks = {
                {"tal", "This is getting interesting!"},
                {"fischer", "Let's see what happens."},
                {"kasparov", "The battle continues!"},
                {"karpov", "Patience will decide this game."},
                {"kramnik", "Solid play is key here."}
        };

        for (String[] fallback : fallbacks) {
            if (fallback[0].equals(master.toLowerCase())) {
                return fallback[1];
            }
        }

        return "This should be interesting...";
    }

    /**
     * 🏆 Extract winner from result string
     */
    private String extractWinner(String result) {
        if (result == null) return null;

        String lower = result.toLowerCase();
        if (lower.contains("white wins") || lower.contains("black wins")) {
            return lower.contains("white") ? "white" : "black";
        }

        // Try to extract from result patterns
        if (lower.contains("tal") && lower.contains("wins")) return "tal";
        if (lower.contains("fischer") && lower.contains("wins")) return "fischer";
        if (lower.contains("kasparov") && lower.contains("wins")) return "kasparov";

        return null; // Draw or unclear
    }

    /**
     * 🔄 Reset conversation state
     */
    public void resetConversation() {
        conversationHistory.clear();
        conversationInProgress = false;
        conversationTurnCount = 0;
        lastSpeaker = "";
        lastStatement = "";

        // Reset TTS state
        isSpeaking = false;
        currentlySpeaking = "";

        Log.d(TAG, "🔄 Conversation state and TTS state reset");
    }

    /**
     * 🛑 NEW: Stop current speech (useful for interruptions or game pause)
     */
    public void stopCurrentSpeech() {
        Log.d(TAG, "🛑 Stopping current speech");
        if (ttsService != null && isSpeaking) {
            ttsService.interrupt();
            isSpeaking = false;
            currentlySpeaking = "";
        }
    }

    /**
     * ℹ️ NEW: Get current speaking state
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }

    public String getCurrentlySpeaking() {
        return currentlySpeaking;
    }

    /**
     * 🧹 Cleanup resources
     */
    public void cleanup() {
        stopCurrentSpeech(); // Stop any ongoing speech

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        resetConversation();
        Log.d(TAG, "🧹 AIDialogueManager cleaned up");
    }
}