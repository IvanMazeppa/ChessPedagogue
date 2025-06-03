package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
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
    private final TTSServiceManager ttsServiceManager;
    private final OpenAITTSService ttsService; // Can be wrapper around ElevenLabs
    private final EvaluationTracker evaluationTracker;
    private final PersonalityEngine personalityEngine;
    private final EmotionalIntelligenceManager emotionalIntelligence;
    private final ConversationMemoryManager conversationMemory;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    // Conversation state
    private final Map<String, ConversationState> activeConversations = new HashMap<>();
    private boolean conversationInProgress = false;
    
    // Conversation timing
    private static final long MIN_RESPONSE_DELAY = 2000; // 2 seconds
    private static final long MAX_RESPONSE_DELAY = 4000; // 4 seconds
    private static final int MAX_CONVERSATION_TURNS = 12; // Increased for more natural conversations
    
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
        
        // 🎭 NEW: Evaluation data for emotional analysis
        Float currentEval;
        Float previousEval;
        
        // 🌟 ENHANCED: EmotionalContext for inter-master awareness
        EmotionalContext emotionalContext;
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer) {
            this.conversationId = conversationId;
            this.whitePlayer = whitePlayer;
            this.blackPlayer = blackPlayer;
            this.turns = new ArrayList<>();
            this.turnCount = 0;
            this.startTime = System.currentTimeMillis();
            this.isActive = true;
        }
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer, Float currentEval, Float previousEval) {
            this(conversationId, whitePlayer, blackPlayer);
            this.currentEval = currentEval;
            this.previousEval = previousEval;
        }
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer, Float currentEval, Float previousEval, EmotionalContext emotionalContext) {
            this(conversationId, whitePlayer, blackPlayer, currentEval, previousEval);
            this.emotionalContext = emotionalContext;
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
        Log.d(TAG, "🔧 Initializing SpectatorConversationOrchestrator...");
        
        Log.d(TAG, "📡 Getting ChessMasterResponsesManager instance...");
        this.responsesManager = ChessMasterResponsesManager.getInstance(context);
        Log.d(TAG, "✅ ChessMasterResponsesManager initialized: " + (responsesManager != null));
        
        this.ttsServiceManager = TTSServiceManager.getInstance(context);
        // Get OpenAI TTS service (which may be a wrapper around ElevenLabs)
        this.ttsService = TTSServiceManager.getOpenAITTSService(context);
        
        // Set usage context for ElevenLabs if that's what we're using
        if (TTSServiceManager.isUsingElevenLabs(context)) {
            TTSServiceManager.setUsageContext(context, "spectator_mode");
            Log.d(TAG, "🎭 Using ElevenLabs TTS for spectator mode");
        } else {
            Log.d(TAG, "🎤 Using OpenAI TTS for spectator mode");
        }
        
        this.evaluationTracker = EvaluationTracker.getInstance(context);
        // PersonalityEngine requires StockfishManager instance
        // Note: StockfishManager doesn't have getInstance(), create new instance
        StockfishManager stockfishManager = new StockfishManager();
        this.personalityEngine = PersonalityEngine.getInstance(context, stockfishManager);
        this.emotionalIntelligence = EmotionalIntelligenceManager.getInstance(context);
        this.conversationMemory = ConversationMemoryManager.getInstance();
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        Log.d(TAG, "✅ SpectatorConversationOrchestrator fully initialized!");
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
    /**
     * 🎭 NEW: Start conversation with evaluation data for enhanced emotional analysis
     */
    public void startConversationWithEvaluation(String triggerType, String whitePlayer, String blackPlayer, 
                                               String gameContext, ConversationCallback callback,
                                               Float currentEval, Float previousEval) {
        startConversationWithEvaluation(triggerType, whitePlayer, blackPlayer, gameContext, callback, 
                                       currentEval, previousEval, null);
    }
    
    /**
     * 🎭 ENHANCED: Start conversation with EmotionalContext for inter-master awareness
     */
    public void startConversationWithEvaluation(String triggerType, String whitePlayer, String blackPlayer, 
                                               String gameContext, ConversationCallback callback,
                                               Float currentEval, Float previousEval, EmotionalContext emotionalContext) {
        // Store evaluation data for emotional analysis
        if (currentEval != null && previousEval != null) {
            Log.d(TAG, String.format("🎭 Starting conversation with evaluation data: current=%.2f, previous=%.2f", 
                  currentEval, previousEval));
        }
        
        if (emotionalContext != null) {
            Log.d(TAG, "🌟 Enhanced conversation with EmotionalContext for inter-master awareness");
        }
        
        // Call regular startConversation but with evaluation context
        startConversation(triggerType, whitePlayer, blackPlayer, gameContext, callback, currentEval, previousEval, emotionalContext);
    }
    
    public void startConversation(String triggerType, String whitePlayer, String blackPlayer, 
                                 String gameContext, ConversationCallback callback) {
        startConversation(triggerType, whitePlayer, blackPlayer, gameContext, callback, null, null, null);
    }
    
    private void startConversation(String triggerType, String whitePlayer, String blackPlayer, 
                                 String gameContext, ConversationCallback callback,
                                 Float currentEval, Float previousEval, EmotionalContext emotionalContext) {
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
        ConversationState state;
        
        // Create ConversationState with or without EmotionalContext
        if (emotionalContext != null) {
            Log.d(TAG, "🌟 Creating conversation with EmotionalContext for inter-master awareness");
            state = new ConversationState(conversationId, whitePlayer, blackPlayer, currentEval, previousEval, emotionalContext);
        } else {
            state = new ConversationState(conversationId, whitePlayer, blackPlayer, currentEval, previousEval);
        }
        
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
        Log.d(TAG, "🎬 generateInitialStatement called: trigger=" + triggerType + ", speaker=" + state.currentSpeaker);
        
        executorService.execute(() -> {
            try {
                String speaker = state.currentSpeaker;
                String opponent = speaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
                
                Log.d(TAG, "📡 About to call responsesManager.createResponseSession for " + speaker);
                Log.d(TAG, "📡 responsesManager is: " + (responsesManager != null ? "AVAILABLE" : "NULL"));
                
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
                        Log.d(TAG, "📨 About to call responsesManager.sendMessage with sessionId: " + sessionId);
                        Log.d(TAG, "📨 Prompt: " + prompt.substring(0, Math.min(100, prompt.length())) + "...");
                        
                        try {
                            Log.d(TAG, "🚨 ORCHESTRATOR: Calling sendMessage NOW");
                            Log.d(TAG, "🔧 ORCHESTRATOR: responsesManager class: " + responsesManager.getClass().getSimpleName());
                            Log.d(TAG, "🔧 ORCHESTRATOR: responsesManager instance: " + responsesManager);
                            
                            // DEBUG: Test static method call first
                            Log.d(TAG, "🔧 ORCHESTRATOR: Testing static debugTest method...");
                            ChessMasterResponsesManager.debugTest();
                            Log.d(TAG, "🔧 ORCHESTRATOR: Static debugTest call completed");
                            
                            responsesManager.sendMessage(sessionId, prompt, gameContext, new ChessMasterResponsesManager.ResponseCallback() {
                            @Override
                            public void onResponseStart(String sessionId) {
                                Log.d(TAG, "📡 CALLBACK: onResponseStart called for sessionId: " + sessionId);
                            }

                            @Override
                            public void onResponseChunk(String chunk, boolean isFirst) {
                                Log.d(TAG, "📡 CALLBACK: onResponseChunk called, chunk length: " + (chunk != null ? chunk.length() : 0));
                                responseBuilder.append(chunk);
                            }

                            @Override
                            public void onResponseComplete(String fullResponse) {
                                Log.d(TAG, "📡 CALLBACK: onResponseComplete called, response length: " + (fullResponse != null ? fullResponse.length() : 0));
                                // Clean and personalize response
                                String cleanedResponse = cleanResponse(fullResponse, speaker);
                                
                                // Add to conversation history
                                state.turns.add(new ConversationTurn(speaker, cleanedResponse, triggerType));
                                state.turnCount++;
                                
                                // 🧠 CONVERSATION MEMORY: Record conversation for topic tracking
                                conversationMemory.recordConversation(speaker, cleanedResponse, gameContext);
                                
                                // 🎭 ENHANCED: Deliver dialogue with sophisticated emotional intelligence
                                mainHandler.post(() -> {
                                    // Check for emotional context using enhanced system
                                    String emotionalState = detectEmotionalContext(state, state.currentEval, state.previousEval);
                                    if (emotionalState != null) {
                                        callback.onEmotionalResponse(speaker, emotionalState, cleanedResponse);
                                    } else {
                                        callback.onDialogueGenerated(speaker, cleanedResponse);
                                    }
                                    
                                    // FIXED: Run TTS asynchronously to prevent blocking
                                    executorService.execute(() -> {
                                        try {
                                            if (emotionalState != null) {
                                                speakWithEnhancedEmotionalIntelligence(speaker, cleanedResponse, state);
                                            } else {
                                                speakWithPersonality(speaker, cleanedResponse);
                                            }
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error in async TTS, falling back to basic speech", e);
                                            speakWithPersonality(speaker, cleanedResponse);
                                        }
                                    });
                                    
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
                                Log.e(TAG, "❌ CALLBACK: onError called with error: " + error);
                                Log.e(TAG, "❌ CALLBACK: This error from sendMessage will trigger Chat Completions fallback!");
                                mainHandler.post(() -> callback.onError("Failed to send initial prompt: " + error));
                                endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
                            }
                        });
                        
                        Log.d(TAG, "🚨 ORCHESTRATOR: sendMessage call completed successfully");
                        Log.d(TAG, "🔧 ORCHESTRATOR: sendMessage method invocation finished - now waiting for callbacks");
                        
                        } catch (Exception e) {
                            Log.e(TAG, "❌ ORCHESTRATOR EXCEPTION when calling sendMessage: " + e.getMessage(), e);
                            mainHandler.post(() -> callback.onError("Exception in sendMessage: " + e.getMessage()));
                            endConversation(state, speaker, "[Exception occurred]", callback);
                        }
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
                String emotionalContext = detectEmotionalContext(state, state.currentEval, state.previousEval);
                
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
                                        
                                        // 🧠 CONVERSATION MEMORY: Record conversation for topic tracking
                                        conversationMemory.recordConversation(responder, cleanedResponse, 
                                                                             buildConversationContext(state));
                                        
                                        // 🎭 ENHANCED: Use sophisticated emotional intelligence for responses
                                        mainHandler.post(() -> {
                                            if (emotionalContext != null) {
                                                callback.onEmotionalResponse(responder, emotionalContext, cleanedResponse);
                                            } else {
                                                callback.onDialogueGenerated(responder, cleanedResponse);
                                            }
                                            
                                            // FIXED: Run TTS asynchronously to prevent blocking
                                            executorService.execute(() -> {
                                                try {
                                                    if (emotionalContext != null) {
                                                        speakWithEnhancedEmotionalIntelligence(responder, cleanedResponse, state);
                                                    } else {
                                                        speakWithPersonality(responder, cleanedResponse);
                                                    }
                                                } catch (Exception e) {
                                                    Log.e(TAG, "Error in async TTS, falling back to basic speech", e);
                                                    speakWithPersonality(responder, cleanedResponse);
                                                }
                                            });
                                            
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
     * 🎭 FIXED: Speak dialogue with master's personality (race condition fixed)
     */
    private void speakWithPersonality(String speaker, String dialogue) {
        try {
            Log.d(TAG, "🎭 Speaking with " + speaker + "'s voice (bypassing global preference)");
            
            // Use the new thread-safe method that bypasses global preferences
            TTSServiceManager.speakWithSpecificMaster(context, speaker, dialogue, new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    Log.d(TAG, "✅ Speech completed for " + speaker);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error speaking dialogue", e);
        }
    }
    
    /**
     * 🎭 ENHANCED: Speak dialogue with sophisticated emotional personality
     * Uses EmotionalIntelligenceManager results for nuanced voice modulation
     */
    private void speakWithEmotionalPersonality(String speaker, String dialogue, String emotionalState) {
        try {
            // Save current master preference
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String currentMaster = prefs.getString("selected_master", "tal");
            
            // Set speaker's voice
            prefs.edit().putString("selected_master", speaker.toLowerCase()).apply();
            
            // Check if we're using ElevenLabs for better emotional voice
            if (TTSServiceManager.isUsingElevenLabs(context)) {
                Log.d(TAG, "🎭 Using ElevenLabs emotional voice for " + speaker + " (" + emotionalState + ")");
                
                // Get evaluation context for more nuanced emotions
                Float evalChange = evaluationTracker.getRecentEvaluationChange();
                Float currentEval = evaluationTracker.getCurrentEvaluation();
                
                // When using ElevenLabs through the wrapper, just use the normal speak method
                // The wrapper handles the adaptation
                ttsService.speak(dialogue, new OpenAITTSService.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        // Restore original master
                        prefs.edit().putString("selected_master", currentMaster).apply();
                    }
                });
            } else {
                Log.d(TAG, "🎤 Using OpenAI emotional voice for " + speaker + " (" + emotionalState + ")");
                
                // OpenAI TTS has emotional methods we can use
                OpenAITTSService openAITTS = (OpenAITTSService) ttsService;
                
                // Get evaluation context
                Float evalChange = evaluationTracker.getRecentEvaluationChange();
                if (evalChange == null) evalChange = 0f;
                Float currentEval = evaluationTracker.getCurrentEvaluation();
                if (currentEval == null) currentEval = 0f;
                
                // Determine more specific emotional state
                String specificEmotion = OpenAITTSService.determineEmotionalState(evalChange, currentEval);
                
                // Use generateTTSChunkWithEmotion if available (through reflection to avoid compilation issues)
                try {
                    java.lang.reflect.Method emotionalMethod = openAITTS.getClass().getDeclaredMethod(
                        "generateTTSChunkWithEmotion", String.class, int.class, boolean.class, 
                        OpenAITTSService.TTSCallback.class, String.class, float.class
                    );
                    emotionalMethod.setAccessible(true);
                    
                    OpenAITTSService.TTSCallback emotionalCallback = new OpenAITTSService.TTSCallback() {
                        @Override
                        public void onSpeechStarted() {
                            Log.d(TAG, "🎭 Emotional speech started");
                        }
                        
                        @Override
                        public void onSpeechReady(File audioFile) {
                            Log.d(TAG, "🎭 Emotional audio ready");
                        }
                        
                        @Override
                        public void onSpeechCompleted() {
                            // Restore original master
                            prefs.edit().putString("selected_master", currentMaster).apply();
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Emotional TTS error: " + errorMessage);
                            // Fallback to regular speech
                            speakWithPersonality(speaker, dialogue);
                        }
                    };
                    
                    emotionalMethod.invoke(openAITTS, dialogue, 0, true, emotionalCallback, specificEmotion, evalChange);
                    
                } catch (Exception e) {
                    Log.w(TAG, "Could not use emotional TTS method, falling back to regular speech", e);
                    // Fallback to regular speech
                    speakWithPersonality(speaker, dialogue);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error speaking emotional dialogue", e);
            // Fallback to regular speech
            speakWithPersonality(speaker, dialogue);
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
        
        // Always continue for first few turns to establish banter
        if (state.turnCount < 5) return true; // Increased minimum turns
        
        // Check statement triggers - be more permissive
        String lower = statement.toLowerCase();
        return lower.contains("?") || lower.contains("!") || 
               lower.contains("but") || lower.contains("however") || 
               lower.contains("interesting") || lower.contains("you") ||
               lower.contains("your") || lower.length() > 40;
    }
    
    private boolean shouldContinueConversation(String response, ConversationState state) {
        // Check turn limit
        if (state.turnCount >= MAX_CONVERSATION_TURNS) return false;
        
        // Continue for at least 4-5 exchanges for natural flow
        if (state.turnCount < 8) return true; // Increased minimum exchanges
        
        // Check response characteristics - be more permissive
        return response.length() > 20 && 
               (response.contains("?") || response.contains("!") || 
                response.contains("...") || response.contains(",") ||
                Math.random() < 0.7); // 70% chance to continue for natural flow
    }
    
    /**
     * 🎭 ENHANCED: Sophisticated emotional analysis using EmotionalIntelligenceManager
     */
    private String detectEmotionalContext(ConversationState state) {
        return detectEmotionalContext(state, null, null);
    }
    
    /**
     * 🎭 ENHANCED: Sophisticated emotional analysis with evaluation data
     */
    private String detectEmotionalContext(ConversationState state, Float currentEval, Float previousEval) {
        try {
            // CRITICAL FIX: Use passed evaluation data ONLY to prevent recursive API calls
            Float evalChange = null;
            if (currentEval != null && previousEval != null) {
                evalChange = currentEval - previousEval;
                Log.d(TAG, String.format("🎭 Using passed evaluation data: current=%.2f, previous=%.2f, change=%.2f", 
                      currentEval, previousEval, evalChange));
            } else {
                // CRITICAL FIX: Do NOT call tracker methods that trigger more emotional analysis!
                // Instead, skip emotional analysis if we don't have direct evaluation data
                Log.d(TAG, "🎭 No evaluation data passed - skipping emotional analysis to prevent recursive calls");
                return null;
            }
            
            // Build conversation context for the emotional analysis
            String conversationContext = buildConversationContext(state);
            String gameContext = buildGameContext(state, evalChange, currentEval);
            
            // Use EmotionalIntelligenceManager for sophisticated analysis
            EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult;
            
            // 🌟 ENHANCED: Use EmotionalContext if available for inter-master awareness
            if (state.emotionalContext != null) {
                Log.d(TAG, "🌟 Using EmotionalContext for enhanced inter-master emotional analysis");
                
                // Update EmotionalContext with current game state
                state.emotionalContext.updateGameContext(state.turnCount, "spectator_game");
                
                // Switch perspective based on current speaker
                if (!state.currentSpeaker.equals(state.emotionalContext.getCurrentMaster())) {
                    state.emotionalContext.switchPerspective();
                }
                
                emotionalResult = emotionalIntelligence.analyzeEmotionalState(
                    state.currentSpeaker,
                    gameContext,
                    conversationContext,
                    state.emotionalContext
                );
            } else {
                // Fallback to original method without EmotionalContext
                emotionalResult = emotionalIntelligence.analyzeEmotionalState(
                    state.currentSpeaker,
                    gameContext,
                    conversationContext,
                    currentEval,
                    evalChange,
                    null
                );
            }
            
            Log.d(TAG, String.format("🎭 Emotional analysis for %s: %s (intensity: %.2f, momentum: %.2f)", 
                  state.currentSpeaker, emotionalResult.emotion.name, 
                  emotionalResult.intensity, emotionalResult.momentum));
            
            // Return emotion name if intensity is significant enough
            if (emotionalResult.intensity > 0.2f) { // Threshold for emotional expression
                return emotionalResult.emotion.name;
            }
            
            return null; // No significant emotional state
            
        } catch (Exception e) {
            Log.e(TAG, "Error in emotional analysis, falling back to basic detection", e);
            return basicEmotionalFallback(state);
        }
    }
    
    /**
     * Build conversation context for emotional analysis
     */
    private String buildConversationContext(ConversationState state) {
        if (state.turns.isEmpty()) {
            return "conversation_start";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("spectator_conversation");
        
        // Add recent conversation themes
        if (state.turns.size() >= 2) {
            String lastMessage = state.turns.get(state.turns.size() - 1).message.toLowerCase();
            if (lastMessage.contains("brilliant") || lastMessage.contains("amazing")) {
                context.append("_praise");
            } else if (lastMessage.contains("mistake") || lastMessage.contains("blunder")) {
                context.append("_criticism");  
            } else if (lastMessage.contains("interesting") || lastMessage.contains("creative")) {
                context.append("_analysis");
            }
        }
        
        return context.toString();
    }
    
    /**
     * Build game context for emotional analysis
     */
    private String buildGameContext(ConversationState state, Float evalChange, Float currentEval) {
        StringBuilder context = new StringBuilder();
        
        // Add evaluation context
        if (evalChange != null && currentEval != null) {
            float absChange = Math.abs(evalChange);
            if (absChange > 2.0f) {
                context.append("major_evaluation_swing");
            } else if (absChange > 1.0f) {
                context.append("significant_evaluation_change");
            } else {
                context.append("minor_evaluation_change");
            }
            
            // Add position assessment
            if (Math.abs(currentEval) > 3.0f) {
                context.append("_decisive_position");
            } else if (Math.abs(currentEval) > 1.5f) {
                context.append("_advantage_position");
            } else {
                context.append("_balanced_position");
            }
        } else {
            context.append("positional_discussion");
        }
        
        return context.toString();
    }
    
    /**
     * Fallback emotional detection (simplified version of old logic)
     */
    private String basicEmotionalFallback(ConversationState state) {
        // CRITICAL FIX: Do NOT call tracker methods that trigger more emotional analysis!
        // Use conversation state evaluation data instead
        Float evalChange = null;
        if (state.currentEval != null && state.previousEval != null) {
            evalChange = state.currentEval - state.previousEval;
        }
        
        if (evalChange != null && Math.abs(evalChange) > 2.0f) {
            return evalChange > 0 ? "pleased" : "concerned";
        }
        
        // Check conversation content for basic emotional cues
        if (!state.turns.isEmpty()) {
            String lastMessage = state.turns.get(state.turns.size() - 1).message.toLowerCase();
            if (lastMessage.contains("brilliant") || lastMessage.contains("amazing")) {
                return "impressed";
            } else if (lastMessage.contains("terrible") || lastMessage.contains("blunder")) {
                return "frustrated";
            }
        }
        
        return null;
    }
    
    /**
     * 🎭 NEW: Enhanced emotional TTS using full EmotionalIntelligenceManager results
     */
    private void speakWithEnhancedEmotionalIntelligence(String speaker, String dialogue, ConversationState state) {
        try {
            // Save current master preference
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            String currentMaster = prefs.getString("selected_master", "tal");
            
            // Set speaker's voice
            prefs.edit().putString("selected_master", speaker.toLowerCase()).apply();
            
            // CRITICAL FIX: Use state evaluation data to prevent recursive API calls
            Float evalChange = null;
            Float currentEval = state.currentEval;
            if (state.currentEval != null && state.previousEval != null) {
                evalChange = state.currentEval - state.previousEval;
            }
            
            String conversationContext = buildConversationContext(state);
            String gameContext = buildGameContext(state, evalChange, currentEval);
            
            EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult = 
                emotionalIntelligence.analyzeEmotionalState(
                    speaker,
                    gameContext,
                    conversationContext,
                    currentEval,
                    evalChange,
                    null
                );
            
            Log.d(TAG, String.format("🎭 Enhanced TTS for %s: %s (intensity: %.2f, momentum: %.2f)", 
                  speaker, emotionalResult.emotion.name, emotionalResult.intensity, emotionalResult.momentum));
            
            // Use ElevenLabs if available for superior emotional voice synthesis
            if (TTSServiceManager.isUsingElevenLabs(context)) {
                speakWithElevenLabsEmotionalIntelligence(speaker, dialogue, emotionalResult, currentMaster);
            } else {
                speakWithOpenAIEmotionalIntelligence(speaker, dialogue, emotionalResult, currentMaster);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in enhanced emotional TTS, falling back to basic speech", e);
            speakWithPersonality(speaker, dialogue);
        }
    }
    
    /**
     * 🎤 🎭 FIXED: ElevenLabs emotional voice synthesis with EmotionalIntelligence (race condition fixed)
     */
    private void speakWithElevenLabsEmotionalIntelligence(String speaker, String dialogue, 
                                                         EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult,
                                                         String originalMaster) {
        
        // Format dialogue with emotional context for ElevenLabs
        String emotionallyFormattedDialogue = formatDialogueForEmotionalTTS(dialogue, emotionalResult);
        
        // Get ElevenLabs service directly for more control
        ElevenLabsTTSService elevenLabsService = ElevenLabsTTSService.getInstance(context);
        
        // 🎭 CRITICAL FIX: Set emotional state for voice modulation
        elevenLabsService.setEmotionalState(emotionalResult);
        
        // Set emotional context for ElevenLabs
        String emotionalContext = buildElevenLabsEmotionalContext(emotionalResult);
        elevenLabsService.setUsageContext(emotionalContext);
        
        Log.d(TAG, String.format("🎭 ElevenLabs emotional synthesis: %s speaking with %s (intensity: %.2f)", 
              speaker, emotionalResult.emotion.name, emotionalResult.intensity));
        
        // 🎭 FIXED: Use speaker-specific method to avoid race conditions
        elevenLabsService.speakWithSpecificMaster(speaker, emotionallyFormattedDialogue, new ElevenLabsTTSService.SpeechCallback() {
            @Override
            public void onSpeechCompleted(String text) {
                // Reset context but don't change global master preference
                elevenLabsService.setUsageContext("spectator_mode"); // Reset to default
                Log.d(TAG, "✅ Emotional ElevenLabs speech completed for " + speaker);
            }
            
            @Override
            public void onSpeechInterrupted() {
                // Reset context but don't change global master preference
                elevenLabsService.setUsageContext("spectator_mode"); // Reset to default
                Log.d(TAG, "⚠️ Emotional ElevenLabs speech interrupted for " + speaker);
            }
        });
    }
    
    /**
     * 🎤 OpenAI emotional voice synthesis with EmotionalIntelligence  
     */
    private void speakWithOpenAIEmotionalIntelligence(String speaker, String dialogue,
                                                     EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult,
                                                     String originalMaster) {
        
        Log.d(TAG, String.format("🎭 OpenAI emotional synthesis: %s with %s (intensity: %.2f)", 
              speaker, emotionalResult.emotion.name, emotionalResult.intensity));
        
        // Use regular TTS service (may be OpenAI or wrapper)
        ttsService.speak(dialogue, new OpenAITTSService.OnSpeechCompletedListener() {
            @Override
            public void onSpeechCompleted() {
                // Restore original master
                android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("selected_master", originalMaster).apply();
                Log.d(TAG, "✅ Emotional OpenAI speech completed for " + speaker);
            }
        });
    }
    
    /**
     * Format dialogue text with emotional cues for TTS engines
     */
    private String formatDialogueForEmotionalTTS(String dialogue, EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult) {
        // Use the emotional expression from the result if available
        if (emotionalResult.expression != null && !emotionalResult.expression.isEmpty() && 
            !emotionalResult.expression.equals(emotionalResult.emotion.defaultExpression)) {
            
            // If the emotional result has a specific expression, use it
            return emotionalResult.expression;
        }
        
        // Otherwise, enhance the original dialogue with emotional context
        return enhanceDialogueWithEmotion(dialogue, emotionalResult);
    }
    
    /**
     * Enhance dialogue with emotional context cues for TTS
     */
    private String enhanceDialogueWithEmotion(String dialogue, EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult) {
        // Add emotional intensity markers based on the analysis
        if (emotionalResult.intensity > 0.7f) {
            // High intensity emotions get strong markers
            switch (emotionalResult.emotion) {
                case ECSTATIC:
                case THRILLED:
                    return dialogue + "!"; // Add excitement
                case DEVASTATED:
                case FRUSTRATED:
                    return dialogue.replace(".", "..."); // Add hesitation/frustration
                default:
                    return dialogue;
            }
        } else if (emotionalResult.intensity > 0.4f) {
            // Medium intensity gets subtle markers
            switch (emotionalResult.emotion) {
                case EXCITED:
                case PLEASED:
                    return dialogue; // Natural delivery
                case CONCERNED:
                case UNEASY:
                    return dialogue.replace("!", "."); // Tone down excitement
                default:
                    return dialogue;
            }
        }
        
        return dialogue; // Low intensity - natural delivery
    }
    
    /**
     * Build ElevenLabs-specific emotional context
     */
    private String buildElevenLabsEmotionalContext(EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult) {
        StringBuilder context = new StringBuilder("spectator_mode");
        
        // Add emotional context for ElevenLabs model selection and voice settings
        if (emotionalResult.intensity > 0.5f) {
            context.append("_").append(emotionalResult.emotion.name.toLowerCase());
            
            if (emotionalResult.shouldInterruptConversation()) {
                context.append("_urgent");
            } else if (emotionalResult.shouldInfluenceVoice()) {
                context.append("_expressive");
            }
        }
        
        return context.toString();
    }
    
    private String createInitialPrompt(String speaker, String opponent, String triggerType, String gameContext) {
        // 🧠 CONVERSATION MEMORY: Get guidance to prevent repetitive topics
        ConversationMemoryManager.ConversationGuidance guidance = 
            conversationMemory.getConversationGuidance(speaker, opponent, gameContext);
        
        // Generate varied prompts to avoid repetitive responses
        Random rand = new Random();
        
        // Add personality flavor to prompts
        String speakerLower = speaker.toLowerCase();
        
        // 🧠 Build enhanced prompt with conversation memory guidance
        StringBuilder enhancedPrompt = new StringBuilder();
        
        // Add conversation memory instructions first
        if (!guidance.contextInstructions.isEmpty()) {
            enhancedPrompt.append(guidance.contextInstructions).append("\n\n");
        }
        
        // Get base prompt based on trigger type
        String basePrompt;
        switch (triggerType) {
            case "opening":
                String[] openingPrompts = getOpeningPrompts(speaker, opponent);
                basePrompt = openingPrompts[rand.nextInt(openingPrompts.length)];
                break;
                
            case "brilliant_move":
                String[] brilliantPrompts = getBrilliantMovePrompts(speaker, opponent);
                basePrompt = brilliantPrompts[rand.nextInt(brilliantPrompts.length)];
                break;
                
            case "blunder":
                String[] blunderPrompts = getBlunderPrompts(speaker, opponent);
                basePrompt = blunderPrompts[rand.nextInt(blunderPrompts.length)];
                break;
                
            case "endgame":
                String[] endgamePrompts = getEndgamePrompts(speaker, opponent);
                basePrompt = endgamePrompts[rand.nextInt(endgamePrompts.length)];
                break;
                
            default:
                String[] generalPrompts = getGeneralPrompts(speaker, opponent);
                basePrompt = generalPrompts[rand.nextInt(generalPrompts.length)];
                break;
        }
        
        enhancedPrompt.append(basePrompt);
        
        Log.d(TAG, String.format("🧠 Enhanced prompt for %s with conversation memory guidance: overused=%d, suggested=%d",
              speaker, guidance.overusedTopics.size(), guidance.suggestedTopics.size()));
        
        return enhancedPrompt.toString();
    }
    
    private String[] getOpeningPrompts(String speaker, String opponent) {
        String speakerLower = speaker.toLowerCase();
        
        if (speakerLower.contains("tal")) {
            return new String[] {
                "Facing " + opponent + "! I can already sense the tactical storms brewing. What complications shall we create?",
                "Ah, " + opponent + " sits across from me! Time to paint another masterpiece on the chessboard!",
                "The game begins with " + opponent + "! I feel the pieces wanting to dance - shall we give them wings?",
                "Playing " + opponent + " today! My heart races with the possibilities for beautiful sacrifices!",
                "Here we go against " + opponent + "! The empty board is like a blank canvas waiting for our art!"
            };
        } else if (speakerLower.contains("fischer")) {
            return new String[] {
                "Playing " + opponent + ". I've prepared everything. Time to prove who plays the most accurate chess.",
                "Facing " + opponent + " now. My preparation is perfect. Let's see if they can handle precision.",
                "Game against " + opponent + " starts. I demand nothing less than perfect play from myself.",
                "" + opponent + " is my opponent. Good. I'll show them what real chess accuracy looks like.",
                "Starting against " + opponent + ". Every move must be the best. No compromises."
            };
        } else if (speakerLower.contains("carlsen")) {
            return new String[] {
                "Playing " + opponent + " today. Let's see where the game takes us - I'm ready for anything.",
                "Facing " + opponent + ". Time to play some chess and find practical solutions.",
                "Game with " + opponent + " begins. I'll keep it simple and look for my chances.",
                "Starting against " + opponent + ". No need to force anything - good moves will come.",
                "Here we go versus " + opponent + ". Let's play solid chess and see what happens."
            };
        } else {
            return new String[] {
                "You're facing " + opponent + " in this game. What's your opening strategy?",
                "Playing against " + opponent + " today. Share your initial thoughts on the position.",
                "The game begins against " + opponent + ". What's your approach?",
                "Starting position against " + opponent + ". How do you feel about this matchup?",
                "Here we go against " + opponent + ". What are your expectations?"
            };
        }
    }
    
    private String[] getBrilliantMovePrompts(String speaker, String opponent) {
        String speakerLower = speaker.toLowerCase();
        
        if (speakerLower.contains("tal")) {
            return new String[] {
                "Did you see that combination? The pieces sang in harmony! Let me show you the magic!",
                "Ah! The sacrifice reveals itself! Beauty triumphs over material once again!",
                "This tactical blow! It's like lightning striking the board! Feel the energy!",
                "The pieces dance to my will! This combination - it's pure chess poetry!",
                "Look at this! When you truly love chess, the tactics find you!"
            };
        } else if (speakerLower.contains("fischer")) {
            return new String[] {
                "That's it! The only move! Calculated to perfection. This is how you play chess.",
                "Found it! The best move, as always. Precision beats everything else.",
                "This move wins by force. No luck, just superior calculation.",
                "Perfect! This is what happens when you play accurately. The position collapses.",
                "There! The crushing blow. This is what real chess looks like."
            };
        } else {
            return new String[] {
                "You just found a strong tactical sequence. What did you see?",
                "That was a powerful move! Explain your calculation.",
                "Nice tactical shot! What was the key idea?",
                "Strong play! Share the concept behind this move.",
                "Excellent move! What made you choose this continuation?"
            };
        }
    }
    
    private String[] getBlunderPrompts(String speaker, String opponent) {
        return new String[] {
            "A critical error just occurred. What's your assessment?",
            "That move looks problematic. Share your thoughts.",
            "Something went wrong there. How do you evaluate this?",
            "A slip in the position. What's your take?",
            "That doesn't look right. Comment on what just happened.",
            "A mistake has been made. Analyze the consequences.",
            "The position shifted dramatically. What went wrong?",
            "That's a serious error. How do you see the position now?"
        };
    }
    
    private String[] getEndgamePrompts(String speaker, String opponent) {
        return new String[] {
            "The game concludes. Your final thoughts?",
            "It's over. How do you reflect on this game?",
            "Game finished. What's your assessment?",
            "The battle ends. Share your immediate reaction.",
            "Final position reached. Your conclusion?",
            "The dust settles. What are your takeaways?",
            "Game over. Sum up this chess battle.",
            "The result is decided. Your closing comments?"
        };
    }
    
    private String[] getGeneralPrompts(String speaker, String opponent) {
        return new String[] {
            "Analyze the current position against " + opponent + ".",
            "What's your evaluation here against " + opponent + "?",
            "Share your thoughts on this position versus " + opponent + ".",
            "How do you assess this moment against " + opponent + "?",
            "Your perspective on the current state against " + opponent + "?",
            "Break down this position in your game with " + opponent + ".",
            "What's happening in this phase against " + opponent + "?",
            "Give us your read on the position versus " + opponent + "."
        };
    }
    
    private String createResponsePrompt(String responder, String previousStatement, ConversationState state) {
        String opponent = responder.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
        
        // 🧠 CONVERSATION MEMORY: Get guidance for fresh conversation
        ConversationMemoryManager.ConversationGuidance guidance = 
            conversationMemory.getConversationGuidance(responder, opponent, buildConversationContext(state));
        
        // Detect emotional context for the prompt
        String emotionalContext = detectEmotionalContext(state, state.currentEval, state.previousEval);
        Float evalChange = evaluationTracker.getRecentEvaluationChange();
        
        // Build prompt with personality clash potential and conversation memory guidance
        StringBuilder prompt = new StringBuilder();
        
        // 🧠 Add conversation memory instructions
        if (!guidance.contextInstructions.isEmpty()) {
            prompt.append(guidance.contextInstructions).append("\n\n");
        }
        
        // Add emotional context if relevant
        if (emotionalContext != null && evalChange != null) {
            if (Math.abs(evalChange) > 2.0f) {
                if (evalChange > 0 && responder.equals(state.whitePlayer)) {
                    prompt.append("(You just gained a big advantage!) ");
                } else if (evalChange < 0 && responder.equals(state.blackPlayer)) {
                    prompt.append("(You just gained a big advantage!) ");
                } else {
                    prompt.append("(Your position just got worse!) ");
                }
            }
        }
        
        // Varied response prompts that encourage banter and personality clashes
        Random rand = new Random();
        
        // Different prompt styles based on emotional context
        String[] responseFormats;
        
        if ("thrilled".equals(emotionalContext) || "pleased".equals(emotionalContext)) {
            responseFormats = new String[] {
                "You're doing well! Your opponent just said: \"" + previousStatement + "\". Give them a confident response directly!",
                "You have the advantage. Respond directly to your opponent who said: \"" + previousStatement + "\". Show your superiority!",
                "Your opponent just commented: \"" + previousStatement + "\". You're winning - address them personally and let them know it!",
                "With your strong position, respond directly to your opponent after they said: \"" + previousStatement + "\"",
                "You have the edge! Address your opponent personally after their claim: \"" + previousStatement + "\""
            };
        } else if ("frustrated".equals(emotionalContext) || "desperate".equals(emotionalContext)) {
            responseFormats = new String[] {
                "You're under pressure! Your opponent just taunted you: \"" + previousStatement + "\". Defend yourself by responding directly to them!",
                "You're fighting for survival. Respond directly to your opponent who said: \"" + previousStatement + "\"",
                "Your opponent is pressing you with: \"" + previousStatement + "\". Show your fighting spirit by addressing them personally!",
                "You're in a tough spot. Counter your opponent directly after they said: \"" + previousStatement + "\"",
                "You're struggling but not beaten! React personally to your opponent who told you: \"" + previousStatement + "\""
            };
        } else {
            responseFormats = new String[] {
                "Respond directly to your opponent who just told you: \"" + previousStatement + "\". Address them personally using 'you' and 'your'. Mix position analysis with personal commentary naturally.",
                "Your opponent just said to you: \"" + previousStatement + "\". Give them a direct response as if you're having a face-to-face conversation. Blend chess insights with personality.",
                "Address your opponent directly after they said: \"" + previousStatement + "\". Speak to them, not about them. Feel free to comment on both their words and the position.",
                "Your opponent told you: \"" + previousStatement + "\". Respond directly to them with your honest reaction. Let the conversation flow between chess and philosophy.",
                "Respond personally to your opponent's statement: \"" + previousStatement + "\". Make it feel like a real conversation between two masters analyzing a game together.",
                "Your opponent just challenged you with: \"" + previousStatement + "\". Give them a direct, personal response that may touch on the position, their comment, or both.",
                "Address your opponent who said: \"" + previousStatement + "\". Speak directly to them as if they're sitting across from you discussing this fascinating position.",
                "Your opponent stated: \"" + previousStatement + "\". Respond to them personally and directly, letting the conversation evolve naturally between moves and ideas."
            };
        }
        
        prompt.append(responseFormats[rand.nextInt(responseFormats.length)]);
        
        // 🧠 Add fresh topic suggestions if available
        if (!guidance.suggestedTopics.isEmpty()) {
            prompt.append(" Consider exploring these fresh angles: ");
            prompt.append(String.join(", ", guidance.suggestedTopics));
            prompt.append(".");
        }
        
        // Add personality-specific encouragement with more variety
        String responderLower = responder.toLowerCase();
        if (responderLower.contains("fischer")) {
            String[] fischerPrompts = {
                " Remember: only perfect moves matter!",
                " Show them the objective truth!",
                " Precision beats everything!",
                " No compromises - only the best!"
            };
            prompt.append(fischerPrompts[rand.nextInt(fischerPrompts.length)]);
        } else if (responderLower.contains("carlsen")) {
            String[] carlsenPrompts = {
                " Keep it practical but confident!",
                " Modern chess wisdom prevails!",
                " Show your endgame mastery!",
                " Pragmatic excellence wins!"
            };
            prompt.append(carlsenPrompts[rand.nextInt(carlsenPrompts.length)]);
        } else if (responderLower.contains("tal")) {
            String[] talPrompts = {
                " Find the creative angle!",
                " Where's the magic in this position?",
                " Show them chess poetry!",
                " Tactics and imagination!"
            };
            prompt.append(talPrompts[rand.nextInt(talPrompts.length)]);
        } else if (responderLower.contains("kasparov")) {
            String[] kasparovPrompts = {
                " Dynamic play conquers all!",
                " Show your fighting spirit!",
                " Initiative is everything!",
                " Attack with fury!"
            };
            prompt.append(kasparovPrompts[rand.nextInt(kasparovPrompts.length)]);
        } else if (responderLower.contains("karpov")) {
            String[] karpovPrompts = {
                " Positional mastery speaks!",
                " Subtle refinement wins!",
                " Strategic depth prevails!",
                " Patience and precision!"
            };
            prompt.append(karpovPrompts[rand.nextInt(karpovPrompts.length)]);
        } else if (responderLower.contains("kramnik")) {
            String[] kramnikPrompts = {
                " Technical precision is key!",
                " Deep preparation shows!",
                " Systematic approach wins!",
                " Modern theory prevails!"
            };
            prompt.append(kramnikPrompts[rand.nextInt(kramnikPrompts.length)]);
        }
        
        return prompt.toString();
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
     * 🧠 CONVERSATION MEMORY: Reset for new session
     */
    public void resetConversationMemory() {
        conversationMemory.resetSession();
        Log.d(TAG, "🧠 Conversation memory reset for new session");
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