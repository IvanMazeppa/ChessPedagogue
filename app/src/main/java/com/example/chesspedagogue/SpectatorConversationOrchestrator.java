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

/**
 * Orchestrates spectator mode conversations using the Responses API
 * Manages multi-turn dialogues between chess masters with emotional awareness
 */
public class SpectatorConversationOrchestrator {
    private static final String TAG = "SpectatorConversationOrchestrator";
    
    private static SpectatorConversationOrchestrator instance;
    private final Context context;
    private final ChessMasterResponsesManager responsesManager;
    private final OpenAITTSService ttsService;
    private final EvaluationTracker evaluationTracker;
    private final PersonalityEngine personalityEngine;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    // Conversation state
    private final Map<String, ConversationState> activeConversations = new HashMap<>();
    private boolean conversationInProgress = false;
    
    // Conversation timing
    private static final long MIN_RESPONSE_DELAY = 2000; // 2 seconds
    private static final long MAX_RESPONSE_DELAY = 4000; // 4 seconds
    private static final int MAX_CONVERSATION_TURNS = 6;
    
    /**
     * Tracks conversation state between masters
     */
    private static class ConversationState {
        final String conversationId;
        final String whitePlayer;
        final String blackPlayer;
        final List<ConversationTurn> turns;
        String currentSpeaker;
        int turnCount;
        long startTime;
        boolean isActive;
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer) {
            this.conversationId = conversationId;
            this.whitePlayer = whitePlayer;
            this.blackPlayer = blackPlayer;
            this.turns = new ArrayList<>();
            this.turnCount = 0;
            this.startTime = System.currentTimeMillis();
            this.isActive = true;
        }
    }
    
    /**
     * Single conversation turn
     */
    private static class ConversationTurn {
        final String speaker;
        final String message;
        final String emotionalContext;
        final long timestamp;
        
        ConversationTurn(String speaker, String message, String emotionalContext) {
            this.speaker = speaker;
            this.message = message;
            this.emotionalContext = emotionalContext;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Callback for conversation events
     */
    public interface ConversationCallback {
        void onConversationStart(String speaker1, String speaker2);
        void onDialogueGenerated(String speaker, String dialogue);
        void onEmotionalResponse(String speaker, String emotion, String dialogue);
        void onConversationEnd(String finalSpeaker, String finalMessage);
        void onError(String error);
    }
    
    private SpectatorConversationOrchestrator(Context context) {
        this.context = context.getApplicationContext();
        this.responsesManager = ChessMasterResponsesManager.getInstance(context);
        this.ttsService = TTSServiceManager.getOpenAITTSService(context);
        this.evaluationTracker = EvaluationTracker.getInstance(context);
        // PersonalityEngine requires StockfishManager instance
        // Note: StockfishManager doesn't have getInstance(), create new instance
        StockfishManager stockfishManager = new StockfishManager();
        this.personalityEngine = PersonalityEngine.getInstance(context, stockfishManager);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized SpectatorConversationOrchestrator getInstance(Context context) {
        if (instance == null) {
            instance = new SpectatorConversationOrchestrator(context);
        }
        return instance;
    }
    
    /**
     * Start a new conversation based on game event
     */
    public void startConversation(String triggerType, String whitePlayer, String blackPlayer, 
                                 String gameContext, ConversationCallback callback) {
        if (conversationInProgress) {
            Log.d(TAG, "⏸️ Conversation already in progress, skipping");
            // Add timeout recovery - if conversation has been stuck for >30 seconds, reset it
            long currentTime = System.currentTimeMillis();
            boolean foundActiveConversation = false;
            for (ConversationState state : activeConversations.values()) {
                if (state.isActive && (currentTime - state.startTime) > 30000) {
                    Log.w(TAG, "🔄 Resetting stuck conversation: " + state.conversationId);
                    state.isActive = false;
                    activeConversations.remove(state.conversationId);
                    foundActiveConversation = true;
                }
            }
            if (foundActiveConversation) {
                conversationInProgress = false;
                Log.d(TAG, "🔄 Conversation timeout reset complete, proceeding with new conversation");
            } else {
                return; // Still have active conversations
            }
        }
        
        conversationInProgress = true;
        String conversationId = "conv_" + System.currentTimeMillis();
        ConversationState state = new ConversationState(conversationId, whitePlayer, blackPlayer);
        activeConversations.put(conversationId, state);
        
        // Determine first speaker based on trigger
        String firstSpeaker = determineFirstSpeaker(triggerType, whitePlayer, blackPlayer);
        state.currentSpeaker = firstSpeaker;
        
        Log.d(TAG, "🎬 Starting conversation: " + conversationId + " (" + triggerType + ")");
        callback.onConversationStart(whitePlayer, blackPlayer);
        
        // Generate initial statement
        generateInitialStatement(state, triggerType, gameContext, callback);
    }
    
    /**
     * Generate initial statement using Responses API
     */
    private void generateInitialStatement(ConversationState state, String triggerType, 
                                         String gameContext, ConversationCallback callback) {
        executorService.execute(() -> {
            try {
                String speaker = state.currentSpeaker;
                String opponent = speaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
                
                // Create response session
                responsesManager.createResponseSession(speaker, gameContext, new ChessMasterResponsesManager.ResponseCallback() {
                    private StringBuilder responseBuilder = new StringBuilder();
                    private String currentSessionId = null;
                    
                    @Override
                    public void onResponseStart(String sessionId) {
                        Log.d(TAG, "🎯 Response session started for " + speaker);
                        currentSessionId = sessionId;
                        
                        // Now that we have a valid session, send the initial prompt
                        String prompt = createInitialPrompt(speaker, opponent, triggerType, gameContext);
                        responsesManager.sendMessage(sessionId, prompt, gameContext, new ChessMasterResponsesManager.ResponseCallback() {
                            @Override
                            public void onResponseStart(String sessionId) {
                                Log.d(TAG, "📡 Initial prompt response started");
                            }

                            @Override
                            public void onResponseChunk(String chunk, boolean isFirst) {
                                responseBuilder.append(chunk);
                            }

                            @Override
                            public void onResponseComplete(String fullResponse) {
                                // Clean and personalize response
                                String cleanedResponse = cleanResponse(fullResponse, speaker);
                                
                                // Add to conversation history
                                state.turns.add(new ConversationTurn(speaker, cleanedResponse, triggerType));
                                state.turnCount++;
                                
                                // Deliver dialogue
                                mainHandler.post(() -> {
                                    callback.onDialogueGenerated(speaker, cleanedResponse);
                                    speakWithPersonality(speaker, cleanedResponse);
                                    
                                    // Schedule response if appropriate
                                    if (shouldTriggerResponse(cleanedResponse, state)) {
                                        scheduleResponse(state, opponent, cleanedResponse, callback);
                                    } else {
                                        endConversation(state, speaker, cleanedResponse, callback);
                                    }
                                });
                            }

                            @Override
                            public void onConversationTurn(String speaker, String message) {
                                // Track for context
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "❌ Initial prompt error: " + error);
                                mainHandler.post(() -> callback.onError("Failed to send initial prompt: " + error));
                                endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
                            }
                        });
                    }
                    
                    @Override
                    public void onResponseChunk(String chunk, boolean isFirst) {
                        // Not used for session creation
                    }
                    
                    @Override
                    public void onResponseComplete(String fullResponse) {
                        // Not used for session creation
                    }
                    
                    @Override
                    public void onConversationTurn(String speaker, String message) {
                        // Not used for session creation
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Session creation error: " + error);
                        mainHandler.post(() -> callback.onError("Failed to create session: " + error));
                        endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating initial statement", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
                endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
            }
        });
    }
    
    /**
     * Schedule a response from the other master
     */
    private void scheduleResponse(ConversationState state, String responder, 
                                 String previousStatement, ConversationCallback callback) {
        long delay = MIN_RESPONSE_DELAY + new Random().nextInt((int)(MAX_RESPONSE_DELAY - MIN_RESPONSE_DELAY));
        
        mainHandler.postDelayed(() -> {
            if (!state.isActive || state.turnCount >= MAX_CONVERSATION_TURNS) {
                endConversation(state, state.currentSpeaker, previousStatement, callback);
                return;
            }
            
            generateResponse(state, responder, previousStatement, callback);
        }, delay);
    }
    
    /**
     * Generate a response using Responses API
     */
    private void generateResponse(ConversationState state, String responder, 
                                 String previousStatement, ConversationCallback callback) {
        executorService.execute(() -> {
            try {
                state.currentSpeaker = responder;
                
                // Check for emotional context
                String emotionalContext = detectEmotionalContext(state);
                
                responsesManager.createResponseSession(responder, buildConversationContext(state), 
                    new ChessMasterResponsesManager.ResponseCallback() {
                        private StringBuilder responseBuilder = new StringBuilder();
                        
                        @Override
                        public void onResponseStart(String sessionId) {
                            Log.d(TAG, "💬 " + responder + " preparing response");
                            
                            // Create response prompt with valid session
                            String prompt = createResponsePrompt(responder, previousStatement, state);
                            responsesManager.sendMessage(sessionId, prompt, 
                                buildConversationContext(state), new ChessMasterResponsesManager.ResponseCallback() {
                                    @Override
                                    public void onResponseStart(String sessionId) {
                                        Log.d(TAG, "📡 Response prompt started for " + responder);
                                    }

                                    @Override
                                    public void onResponseChunk(String chunk, boolean isFirst) {
                                        responseBuilder.append(chunk);
                                    }

                                    @Override
                                    public void onResponseComplete(String fullResponse) {
                                        String cleanedResponse = cleanResponse(fullResponse, responder);
                                        
                                        // Add to conversation history
                                        state.turns.add(new ConversationTurn(responder, cleanedResponse, emotionalContext));
                                        state.turnCount++;
                                        
                                        mainHandler.post(() -> {
                                            if (emotionalContext != null) {
                                                callback.onEmotionalResponse(responder, emotionalContext, cleanedResponse);
                                            } else {
                                                callback.onDialogueGenerated(responder, cleanedResponse);
                                            }
                                            
                                            speakWithPersonality(responder, cleanedResponse);
                                            
                                            // Continue or end conversation
                                            String nextSpeaker = responder.equals(state.whitePlayer) ? 
                                                state.blackPlayer : state.whitePlayer;
                                                
                                            if (shouldContinueConversation(cleanedResponse, state)) {
                                                scheduleResponse(state, nextSpeaker, cleanedResponse, callback);
                                            } else {
                                                endConversation(state, responder, cleanedResponse, callback);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onConversationTurn(String speaker, String message) {
                                        // Handle conversation turn
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "❌ Response prompt error: " + error);
                                        mainHandler.post(() -> callback.onError("Failed to send response prompt: " + error));
                                        endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
                                    }
                                });
                        }
                        
                        @Override
                        public void onResponseChunk(String chunk, boolean isFirst) {
                            // Not used for session creation
                        }
                        
                        @Override
                        public void onResponseComplete(String fullResponse) {
                            // Not used for session creation
                        }
                        
                        @Override
                        public void onConversationTurn(String speaker, String message) {
                            // Track conversation flow
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Session creation error: " + error);
                            mainHandler.post(() -> callback.onError("Failed to create response session: " + error));
                            endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
                        }
                    });
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating response", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
                endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
            }
        });
    }
    
    /**
     * End the conversation
     */
    private void endConversation(ConversationState state, String finalSpeaker, 
                                 String finalMessage, ConversationCallback callback) {
        state.isActive = false;
        conversationInProgress = false;
        activeConversations.remove(state.conversationId);
        
        Log.d(TAG, "🎭 Conversation ended: " + state.conversationId);
        callback.onConversationEnd(finalSpeaker, finalMessage);
    }
    
    /**
     * Speak dialogue with master's personality
     */
    private void speakWithPersonality(String speaker, String dialogue) {
        try {
            // Save current master preference
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String currentMaster = prefs.getString("selected_master", "tal");
            
            // Set speaker's voice
            prefs.edit().putString("selected_master", speaker.toLowerCase()).apply();
            
            // Speak with callback to restore preference
            ttsService.speak(dialogue, new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    // Restore original master
                    prefs.edit().putString("selected_master", currentMaster).apply();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error speaking dialogue", e);
        }
    }
    
    /**
     * Helper methods
     */
    
    private String determineFirstSpeaker(String triggerType, String whitePlayer, String blackPlayer) {
        switch (triggerType) {
            case "opening":
                return Math.random() > 0.5 ? whitePlayer : blackPlayer;
            case "blunder":
            case "brilliant_move":
                // The player who made the move comments first
                return whitePlayer; // This should be passed in context
            case "endgame":
                // Winner speaks first if there is one
                return whitePlayer; // This should be determined from result
            default:
                return Math.random() > 0.5 ? whitePlayer : blackPlayer;
        }
    }
    
    private boolean shouldTriggerResponse(String statement, ConversationState state) {
        // Check conversation limits
        if (state.turnCount >= MAX_CONVERSATION_TURNS) return false;
        
        // Check statement triggers
        String lower = statement.toLowerCase();
        return lower.contains("?") || lower.contains("but") || 
               lower.contains("however") || lower.contains("interesting");
    }
    
    private boolean shouldContinueConversation(String response, ConversationState state) {
        // Check turn limit
        if (state.turnCount >= MAX_CONVERSATION_TURNS) return false;
        
        // Check response characteristics
        return response.length() > 50 && 
               (response.contains("?") || response.contains("..."));
    }
    
    private String detectEmotionalContext(ConversationState state) {
        // Check evaluation changes
        Float evalChange = evaluationTracker.getRecentEvaluationChange();
        if (evalChange != null) {
            if (Math.abs(evalChange) > 2.0f) {
                return evalChange > 0 ? "excited" : "concerned";
            }
        }
        
        // Check conversation content
        if (!state.turns.isEmpty()) {
            String lastMessage = state.turns.get(state.turns.size() - 1).message.toLowerCase();
            if (lastMessage.contains("brilliant") || lastMessage.contains("amazing")) {
                return "impressed";
            } else if (lastMessage.contains("mistake") || lastMessage.contains("blunder")) {
                return "critical";
            }
        }
        
        return null;
    }
    
    private String buildConversationContext(ConversationState state) {
        StringBuilder context = new StringBuilder();
        context.append("Game: ").append(state.whitePlayer).append(" vs ").append(state.blackPlayer);
        context.append("\nTurn ").append(state.turnCount).append(" of conversation");
        
        // Add recent turns
        int startIdx = Math.max(0, state.turns.size() - 3);
        for (int i = startIdx; i < state.turns.size(); i++) {
            ConversationTurn turn = state.turns.get(i);
            context.append("\n").append(turn.speaker).append(": ").append(turn.message);
        }
        
        return context.toString();
    }
    
    private String createInitialPrompt(String speaker, String opponent, String triggerType, String gameContext) {
        // Generate varied prompts to avoid repetitive responses
        java.util.Random rand = new java.util.Random();
        
        switch (triggerType) {
            case "opening":
                String[] openingPrompts = {
                    "You're facing " + opponent + " in this game. What's your opening strategy?",
                    "Playing against " + opponent + " today. Share your initial thoughts on the position.",
                    "The game begins against " + opponent + ". What's your approach?",
                    "Starting position against " + opponent + ". How do you feel about this matchup?",
                    "Here we go against " + opponent + ". What are your expectations?"
                };
                return openingPrompts[rand.nextInt(openingPrompts.length)];
                
            case "brilliant_move":
                String[] brilliantPrompts = {
                    "You just found a strong tactical sequence. What did you see?",
                    "That was a powerful move! Explain your calculation.",
                    "Nice tactical shot! What was the key idea?",
                    "Strong play! Share the concept behind this move.",
                    "Excellent move! What made you choose this continuation?"
                };
                return brilliantPrompts[rand.nextInt(brilliantPrompts.length)];
                
            case "blunder":
                String[] blunderPrompts = {
                    "A critical error just occurred. What's your assessment?",
                    "That move looks problematic. Share your thoughts.",
                    "Something went wrong there. How do you evaluate this?",
                    "A slip in the position. What's your take?",
                    "That doesn't look right. Comment on what just happened."
                };
                return blunderPrompts[rand.nextInt(blunderPrompts.length)];
                
            case "endgame":
                String[] endgamePrompts = {
                    "The game concludes. Your final thoughts?",
                    "It's over. How do you reflect on this game?",
                    "Game finished. What's your assessment?",
                    "The battle ends. Share your immediate reaction.",
                    "Final position reached. Your conclusion?"
                };
                return endgamePrompts[rand.nextInt(endgamePrompts.length)];
                
            default:
                String[] generalPrompts = {
                    "Analyze the current position against " + opponent + ".",
                    "What's your evaluation here against " + opponent + "?",
                    "Share your thoughts on this position versus " + opponent + ".",
                    "How do you assess this moment against " + opponent + "?",
                    "Your perspective on the current state against " + opponent + "?"
                };
                return generalPrompts[rand.nextInt(generalPrompts.length)];
        }
    }
    
    private String createResponsePrompt(String responder, String previousStatement, ConversationState state) {
        String opponent = responder.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
        
        // Varied response prompts to encourage natural dialogue
        java.util.Random rand = new java.util.Random();
        String[] responseFormats = {
            opponent + " just said: \"" + previousStatement + "\". Share your perspective.",
            "Responding to " + opponent + "'s comment: \"" + previousStatement + "\"",
            "After " + opponent + " said: \"" + previousStatement + "\", what's your take?",
            opponent + " commented: \"" + previousStatement + "\". Your thoughts?",
            "React to " + opponent + "'s statement: \"" + previousStatement + "\""
        };
        
        return responseFormats[rand.nextInt(responseFormats.length)];
    }
    
    private String cleanResponse(String response, String speaker) {
        if (response == null) return "";
        
        // Remove quotes
        response = response.trim();
        if (response.startsWith("\"") && response.endsWith("\"")) {
            response = response.substring(1, response.length() - 1);
        }
        
        // Remove system instruction leaks
        String[] systemPatterns = {"You are", "As a chess master", "Respond as", "Comment."};
        for (String pattern : systemPatterns) {
            if (response.contains(pattern)) {
                int idx = response.indexOf(pattern);
                if (idx == 0) {
                    // Find next sentence
                    int nextSentence = response.indexOf(". ", idx);
                    if (nextSentence > 0) {
                        response = response.substring(nextSentence + 2).trim();
                    }
                }
            }
        }
        
        return response;
    }
    
    /**
     * Force stop all conversations
     */
    public void forceStop() {
        conversationInProgress = false;
        for (ConversationState state : activeConversations.values()) {
            state.isActive = false;
        }
        activeConversations.clear();
        
        if (ttsService != null) {
            ttsService.stopSpeaking();
        }
        
        mainHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "🛑 Force stopped all conversations");
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        forceStop();
        executorService.shutdown();
        responsesManager.shutdown();
    }
}