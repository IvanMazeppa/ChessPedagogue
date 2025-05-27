package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🎭 CONVERSATIONAL AI DIALOGUE COORDINATOR
 *
 * Now with FULL conversation capabilities between chess masters!
 * - Memory of what each master said
 * - Intelligent response triggering
 * - Deep conversational context
 * - Ben's complete voice personality system
 */
public class AIDialogueManager {
    private static final String TAG = "AIDialogueManager";

    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final FineTunedModelManager modelManager;
    private final OpenAITTSService ttsService;
    private final Random random;

    // ENHANCED: Conversation management
    private final ConversationManager conversationManager;
    private int dialogueCount = 0;
    private String lastSpeaker = "";
    private final List<String> conversationHistory = new ArrayList<>();

    // NEW: Conversation flow control
    private boolean conversationsEnabled = true;
    private String currentWhitePlayer = "";
    private String currentBlackPlayer = "";

    public interface DialogueCallback {
        void onDialogueGenerated(String speaker, String dialogue);
        void onConversationStarted(String respondingSpeaker, String triggerStatement);
        void onError(String error);
    }

    public AIDialogueManager(Context context) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.modelManager = FineTunedModelManager.getInstance(context);
        this.ttsService = OpenAITTSService.getInstance(context);
        this.random = new Random();

        // CRITICAL: Initialize ConversationManager for memory!
        this.conversationManager = ConversationManager.getInstance(context);

        Log.d(TAG, "🎭 CONVERSATIONAL AIDialogueManager - ready for master conversations!");
    }

    /**
     * ENHANCED: Generate dialogue with conversation awareness!
     */
    public void generateMoveDialogue(String move, String playerWhoMoved, String whitePlayer,
                                     String blackPlayer, int moveNumber, DialogueCallback callback) {

        // Store current players for conversation context
        this.currentWhitePlayer = whitePlayer;
        this.currentBlackPlayer = blackPlayer;

        dialogueCount++;
        Log.d(TAG, "💬 Coordinating CONVERSATIONAL dialogue for move " + dialogueCount);

        executorService.execute(() -> {
            try {
                String speaker;
                String dialogue;

                // Decide who speaks based on your existing logic
                if (shouldPlayerRespond(playerWhoMoved)) {
                    speaker = playerWhoMoved;
                    dialogue = generateDialogueUsingBensSystem(speaker, "move_comment", move, moveNumber);
                } else {
                    speaker = playerWhoMoved.equals(whitePlayer) ? blackPlayer : whitePlayer;
                    dialogue = generateDialogueUsingBensSystem(speaker, "move_response", move, moveNumber);
                }

                // CRITICAL: Add to conversation memory!
                conversationManager.addMasterMessage(speaker, dialogue);
                conversationHistory.add(speaker + ": " + dialogue);
                lastSpeaker = speaker;

                if (callback != null) {
                    final String finalSpeaker = speaker;
                    final String finalDialogue = dialogue;
                    mainHandler.post(() -> {
                        callback.onDialogueGenerated(finalSpeaker, finalDialogue);

                        // Use Ben's complete voice personality system!
                        speakWithFullPersonalitySystem(finalSpeaker, finalDialogue);

                        // NEW: Check for conversation opportunity!
                        checkForConversationOpportunity(finalSpeaker, finalDialogue, callback);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error coordinating dialogue", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    /**
     * 🚀 NEW: The magic happens here - check if the other master should respond!
     */
    private void checkForConversationOpportunity(String speaker, String dialogue, DialogueCallback callback) {
        if (!conversationsEnabled) {
            Log.d(TAG, "💬 Conversations disabled - skipping opportunity check");
            return;
        }

        // Get the other master
        String otherMaster = getOtherMaster(speaker);
        if (otherMaster == null) {
            Log.d(TAG, "💬 No other master to respond");
            return;
        }

        // Check if they should respond using your ConversationManager logic
        if (conversationManager.shouldMasterRespond(speaker, dialogue)) {
            Log.d(TAG, "🎉 CONVERSATION OPPORTUNITY! " + otherMaster + " should respond to: \"" + dialogue + "\"");

            if (callback != null) {
                callback.onConversationStarted(otherMaster, dialogue);
            }

            // Generate a conversational response with a small delay for natural timing
            mainHandler.postDelayed(() -> {
                generateConversationalResponse(otherMaster, dialogue, callback);
            }, 2000 + random.nextInt(3000)); // 2-5 second delay for natural conversation flow
        }
    }

    /**
     * 🎭 NEW: Generate authentic conversational responses!
     */
    private void generateConversationalResponse(String respondingMaster, String triggerDialogue, DialogueCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🗣️ " + respondingMaster + " generating conversational response to: \"" + triggerDialogue + "\"");

                // Create conversation-aware prompt
                String conversationPrompt = createConversationPrompt(respondingMaster, triggerDialogue);

                // Get conversation context from memory
                String conversationContext = conversationManager.buildConversationContext();

                // Use Ben's sophisticated system with conversation context!
                String response = modelManager.processWithMasterAssistant(
                        conversationPrompt,
                        conversationContext,
                        respondingMaster
                );

                if (response == null || response.trim().isEmpty()) {
                    // Fallback to personality-based response
                    response = generateConversationalFallback(respondingMaster, triggerDialogue);
                }

                // Clean and enhance the response
                String cleanedResponse = cleanResponse(response);
                String enhancedResponse = modelManager.enrichResponseWithPersonality(cleanedResponse, respondingMaster);

                // Add to conversation memory
                conversationManager.addMasterMessage(respondingMaster, enhancedResponse);
                conversationHistory.add(respondingMaster + ": " + enhancedResponse);

                // Deliver the response
                if (callback != null) {
                    final String finalSpeaker = respondingMaster;
                    final String finalResponse = enhancedResponse;
                    mainHandler.post(() -> {
                        callback.onDialogueGenerated(finalSpeaker, finalResponse);

                        // Speak with full personality system
                        speakWithFullPersonalitySystem(finalSpeaker, finalResponse);
                    });
                }

                Log.d(TAG, "✅ Conversational response generated: " + enhancedResponse.substring(0, Math.min(50, enhancedResponse.length())) + "...");

            } catch (Exception e) {
                Log.e(TAG, "Error generating conversational response", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("Conversation error: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * 💡 NEW: Create intelligent conversation prompts
     */
    private String createConversationPrompt(String respondingMaster, String triggerDialogue) {
        String masterName = modelManager.getMasterDisplayName(respondingMaster);
        String conversationContext = conversationManager.buildConversationContext();

        StringBuilder prompt = new StringBuilder();

        prompt.append("CONVERSATION CONTEXT:\n");
        if (!conversationContext.trim().isEmpty()) {
            prompt.append(conversationContext).append("\n");
        }

        prompt.append("The other master just said: \"").append(triggerDialogue).append("\"\n\n");

        prompt.append("As ").append(masterName).append(", respond naturally to this comment. ");
        prompt.append("You can agree, disagree, build on the idea, share a related memory, or offer a different perspective. ");
        prompt.append("Keep your response conversational and authentic to your personality (15-25 words). ");

        // Add master-specific conversation guidance
        switch (respondingMaster.toLowerCase()) {
            case "fischer":
                prompt.append("Be direct and uncompromising about chess truth. If you disagree, explain why with conviction.");
                break;
            case "tal":
                prompt.append("Show enthusiasm and warmth. Find the beauty or excitement in what was said.");
                break;
            case "kasparov":
                prompt.append("Be dynamic and passionate. Build energy in the conversation.");
                break;
            case "kramnik":
                prompt.append("Be thoughtful and analytical. Provide measured, methodical insights.");
                break;
            default:
                prompt.append("Respond authentically in your characteristic style.");
                break;
        }

        return prompt.toString();
    }

    /**
     * 🛡️ NEW: Fallback conversational responses based on personality
     */
    private String generateConversationalFallback(String master, String triggerDialogue) {
        FineTunedModelManager.EnhancedPersonalityProfile profile = modelManager.getPersonalityProfile(master);

        // Create responses based on master personality
        switch (master.toLowerCase()) {
            case "fischer":
                if (triggerDialogue.toLowerCase().contains("beautiful") || triggerDialogue.toLowerCase().contains("sacrifice")) {
                    return "Beauty means nothing without objective accuracy. Show me the calculation.";
                } else {
                    return "That requires precise analysis. Let me think about the best moves here.";
                }

            case "tal":
                if (triggerDialogue.toLowerCase().contains("calculate") || triggerDialogue.toLowerCase().contains("precise")) {
                    return "Sometimes the most beautiful moves come from intuition, not just calculation!";
                } else {
                    return "I love how you see the position! There's always more beauty to discover.";
                }

            case "kasparov":
                return "Exactly! And that's what makes these positions so fascinating to analyze.";

            case "kramnik":
                return "A methodical approach will reveal the truth in this position.";

            default:
                return "That's an interesting perspective on this position.";
        }
    }

    /**
     * 🎯 HELPER: Get the other master in the conversation
     */
    private String getOtherMaster(String currentSpeaker) {
        if (currentSpeaker.equals(currentWhitePlayer)) {
            return currentBlackPlayer;
        } else if (currentSpeaker.equals(currentBlackPlayer)) {
            return currentWhitePlayer;
        }
        return null;
    }

    /**
     * Generate opening dialogue - ENHANCED with conversation setup
     */
    public void generateOpeningDialogue(String whitePlayer, String blackPlayer, DialogueCallback callback) {
        // Set up conversation participants
        this.currentWhitePlayer = whitePlayer;
        this.currentBlackPlayer = blackPlayer;

        // Clear conversation memory for new game
        conversationManager.startNewConversation();
        resetConversation();

        executorService.execute(() -> {
            try {
                String dialogue = generateDialogueUsingBensSystem(whitePlayer, "game_opening", "", 0);
                String speaker = whitePlayer;

                // Add to conversation memory
                conversationManager.addMasterMessage(speaker, dialogue);
                conversationHistory.add(speaker + ": " + dialogue);

                if (callback != null) {
                    final String finalSpeaker = speaker;
                    final String finalDialogue = dialogue;
                    mainHandler.post(() -> {
                        callback.onDialogueGenerated(finalSpeaker, finalDialogue);
                        speakWithFullPersonalitySystem(finalSpeaker, finalDialogue);

                        // Check if black player wants to respond to the opening greeting
                        checkForConversationOpportunity(finalSpeaker, finalDialogue, callback);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error generating opening dialogue", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    /**
     * Generate end game dialogue - ENHANCED with final conversation
     */
    public void generateEndGameDialogue(String result, String whitePlayer, String blackPlayer,
                                        DialogueCallback callback) {
        executorService.execute(() -> {
            try {
                String speaker;
                if (result.contains("White wins")) {
                    speaker = whitePlayer;
                } else if (result.contains("Black wins")) {
                    speaker = blackPlayer;
                } else {
                    speaker = random.nextBoolean() ? whitePlayer : blackPlayer;
                }

                String dialogue = generateDialogueUsingBensSystem(speaker, "game_end", result, 0);

                // Add to conversation memory
                conversationManager.addMasterMessage(speaker, dialogue);

                if (callback != null) {
                    final String finalSpeaker = speaker;
                    final String finalDialogue = dialogue;
                    mainHandler.post(() -> {
                        callback.onDialogueGenerated(finalSpeaker, finalDialogue);
                        speakWithFullPersonalitySystem(finalSpeaker, finalDialogue);

                        // Final conversation opportunity
                        checkForConversationOpportunity(finalSpeaker, finalDialogue, callback);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error generating end game dialogue", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    /**
     * 🎛️ NEW: Conversation control methods
     */
    public void enableConversations(boolean enabled) {
        this.conversationsEnabled = enabled;
        Log.d(TAG, conversationsEnabled ? "🗣️ Conversations ENABLED" : "🔇 Conversations DISABLED");
    }

    public boolean areConversationsEnabled() {
        return conversationsEnabled;
    }

    public void setConversationParticipants(String whitePlayer, String blackPlayer) {
        this.currentWhitePlayer = whitePlayer;
        this.currentBlackPlayer = blackPlayer;
        Log.d(TAG, "🎭 Conversation participants: " + whitePlayer + " vs " + blackPlayer);
    }

    // Keep all your existing brilliant methods below...

    /**
     * CORE METHOD: Generate dialogue using Ben's sophisticated FineTunedModelManager!
     * This is where ALL the personality magic happens - in Ben's existing system!
     */
    private String generateDialogueUsingBensSystem(String master, String dialogueType, String context, int moveNumber) {
        try {
            Log.d(TAG, "🎭 Using Ben's sophisticated personality system for " + master + " (" + dialogueType + ")");

            // Create context-specific prompts that leverage Ben's personality infrastructure
            String prompt = createContextPrompt(dialogueType, context, moveNumber);

            // Use Ben's sophisticated assistant processing with FULL personality context
            String response = modelManager.processWithMasterAssistant(prompt, context, master);

            if (response != null && !response.trim().isEmpty()) {
                // Apply Ben's personality enrichment system for authentic touches
                String enriched = modelManager.enrichResponseWithPersonality(response, master);
                return cleanResponse(enriched);
            }

            // Fallback to using Ben's detailed personality profiles
            return generateFallbackUsingBensProfiles(master, dialogueType, context);

        } catch (Exception e) {
            Log.w(TAG, "Ben's advanced system had an issue, using profile fallback", e);
            return generateFallbackUsingBensProfiles(master, dialogueType, context);
        }
    }

    /**
     * 🎤 REVOLUTIONARY: Use Ben's COMPLETE voice personality system!
     * This is the key enhancement that connects to all of Ben's voice magic!
     */
    private void speakWithFullPersonalitySystem(String speaker, String dialogue) {
        if (ttsService != null && dialogue != null && !dialogue.trim().isEmpty()) {
            Log.d(TAG, "🎤 ENHANCED: Using Ben's COMPLETE voice personality system for " +
                    modelManager.getMasterDisplayName(speaker));

            try {
                // STEP 1: Get the specific voice for this master using Ben's system
                String selectedVoice = modelManager.getVoiceForMaster(speaker);
                Log.d(TAG, "🎯 Selected voice for " + speaker + ": " + selectedVoice);

                // STEP 2: Get Ben's enhanced voice instructions for this master
                String voiceInstructions = modelManager.getEnhancedVoiceInstructions(speaker, true);
                Log.d(TAG, "📝 Voice instructions: " + voiceInstructions.substring(0, Math.min(100, voiceInstructions.length())));

                // STEP 4: Apply Ben's personality enrichment to the dialogue
                String enhancedDialogue = modelManager.enrichResponseWithPersonality(dialogue, speaker);

                // STEP 5: Create enhanced text with voice instructions naturally embedded
                String voiceEnhancedText = createVoiceEnhancedText(enhancedDialogue, speaker, voiceInstructions);

                Log.d(TAG, "✨ Using voice: " + selectedVoice + " with Ben's gpt-4o-mini-tts system for " +
                        modelManager.getMasterDisplayName(speaker));

                // STEP 6: Use Ben's TTS service with the correct method signature
                ttsService.speak(voiceEnhancedText, selectedVoice, "gpt-4o-mini-tts", new OpenAITTSService.TTSCallback() {
                    @Override
                    public void onSpeechStarted() {
                        Log.d(TAG, "🎵 " + modelManager.getMasterDisplayName(speaker) +
                                " started speaking with Ben's authentic voice system");
                    }

                    @Override
                    public void onSpeechReady(File audioFile) {
                        Log.d(TAG, "🎧 Audio ready for " + modelManager.getMasterDisplayName(speaker));
                    }

                    @Override
                    public void onSpeechCompleted() {
                        Log.d(TAG, "✅ " + modelManager.getMasterDisplayName(speaker) +
                                " finished speaking with Ben's complete personality voice system!");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "❌ Voice error for " + speaker + ": " + errorMessage);
                        // Fallback to basic speech
                        ttsService.speak(dialogue);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error in enhanced voice system, falling back to basic TTS", e);
                // Fallback to basic speech if something goes wrong
                ttsService.speak(dialogue);
            }
        }
    }

    /**
     * NEW: Create voice-enhanced text that incorporates personality instructions naturally
     */
    private String createVoiceEnhancedText(String originalText, String master, String voiceInstructions) {
        // For dialogue, we want to keep the text natural but can add subtle personality touches
        String enhanced = originalText;

        // Get personality profile for additional context
        FineTunedModelManager.EnhancedPersonalityProfile profile = modelManager.getPersonalityProfile(master);

        if (profile != null) {
            // Add personality-specific speech patterns naturally
            switch (master.toLowerCase()) {
                case "tal":
                    // Tal's enthusiasm and warmth
                    if (!enhanced.contains("!") && (enhanced.contains("beautiful") || enhanced.contains("brilliant"))) {
                        enhanced = enhanced.replace(".", "!");
                    }
                    break;

                case "fischer":
                    // Fischer's precision and intensity - keep text sharp and direct
                    // The voice instructions will handle the intensity
                    break;

                case "kasparov":
                    // Kasparov's dynamic energy
                    if (enhanced.contains("move") || enhanced.contains("position")) {
                        // Voice instructions will handle the dynamic delivery
                    }
                    break;

                case "kramnik":
                    // Kramnik's methodical approach - keep text measured
                    break;
            }
        }

        return enhanced;
    }

    /**
     * Create prompts that work with Ben's sophisticated system
     */
    private String createContextPrompt(String dialogueType, String context, int moveNumber) {
        switch (dialogueType) {
            case "move_comment":
                return String.format(
                        "I just played %s on move %d. Give me a brief, natural comment (10-15 words) " +
                                "that reflects my authentic personality and playing style.",
                        context, moveNumber
                );
            case "move_response":
                return String.format(
                        "My opponent just played %s on move %d. Give me a brief, authentic reaction " +
                                "(10-15 words) that fits my personality.",
                        context, moveNumber
                );
            case "game_opening":
                return "The game is about to begin. Give me a brief, characteristic greeting " +
                        "to my opponent (10-15 words) that reflects my personality.";
            case "game_end":
                return String.format(
                        "The game just ended with result: %s. Give me a brief, sportsmanlike comment " +
                                "(10-15 words) that fits my personality.",
                        context
                );
            default:
                return "Give me a brief, natural chess comment that reflects my authentic personality.";
        }
    }

    /**
     * Fallback using Ben's detailed personality profiles
     */
    private String generateFallbackUsingBensProfiles(String master, String dialogueType, String context) {
        FineTunedModelManager.EnhancedPersonalityProfile profile =
                modelManager.getPersonalityProfile(master);

        if (profile == null) {
            return "Interesting position here.";
        }

        // Use Ben's signature approaches for authentic fallbacks
        if (profile.signatureApproaches != null && profile.signatureApproaches.length > 0) {
            String approach = profile.signatureApproaches[random.nextInt(profile.signatureApproaches.length)];
            return approach;  // Ben's approaches are already perfectly phrased!
        }

        // Use Ben's communication styles
        switch (profile.communicationStyle) {
            case "direct_intense":
                return "This position demands precise calculation.";
            case "enthusiastic_conversational":
                return "What a fascinating position we have here!";
            case "intellectual_engaging":
                return "The strategic complexities here are quite remarkable.";
            case "calm_analytical":
                return "A methodical approach will serve us well here.";
            default:
                return "An interesting moment in our game.";
        }
    }

    /**
     * Simple response cleaning
     */
    private String cleanResponse(String response) {
        if (response == null) return "";

        response = response.trim();

        // Remove quotes if the entire response is quoted
        if (response.startsWith("\"") && response.endsWith("\"")) {
            response = response.substring(1, response.length() - 1);
        }

        return response;
    }

    /**
     * Simple turn management
     */
    private boolean shouldPlayerRespond(String playerWhoMoved) {
        // Don't let the same player talk twice in a row (usually)
        if (playerWhoMoved.equals(lastSpeaker) && random.nextFloat() > 0.3f) {
            return false;
        }

        // 60% chance the moving player comments
        return random.nextFloat() < 0.6f;
    }

    /**
     * Simple state management
     */
    public void resetConversation() {
        conversationHistory.clear();
        lastSpeaker = "";
        dialogueCount = 0;
        Log.d(TAG, "🔄 Conversation reset - ready for Ben's complete personality system");
    }

    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    public void cleanup() {
        executorService.shutdown();
        Log.d(TAG, "🧹 Enhanced dialogue coordinator cleaned up - Ben's voice systems continue working perfectly");
    }
}