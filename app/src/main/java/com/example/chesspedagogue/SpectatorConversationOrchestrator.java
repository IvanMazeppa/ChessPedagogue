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
    private final ConversationVarietyManager varietyManager;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    // 🎭 Phase 2: Multi-layered emotional complexity integration
    private Phase2EmotionalIntegrationBridge phase2Bridge;
    
    // Conversation state
    private final Map<String, ConversationState> activeConversations = new HashMap<>();
    private boolean conversationInProgress = false;
    private boolean userRecordingInProgress = false; // NEW: Flag to pause AI conversations during user recording
    
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
        String triggerType; // Schema: what triggered this conversation
        
        // 🎭 NEW: Evaluation data for emotional analysis
        Float currentEval;
        Float previousEval;
        
        // 🌟 ENHANCED: EmotionalContext for inter-master awareness
        EmotionalContext emotionalContext;
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer, String triggerType) {
            this.conversationId = conversationId;
            this.whitePlayer = whitePlayer;
            this.blackPlayer = blackPlayer;
            this.triggerType = triggerType;
            this.turns = new ArrayList<>();
            this.turnCount = 0;
            this.startTime = System.currentTimeMillis();
            this.isActive = true;
        }
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer, String triggerType, Float currentEval, Float previousEval) {
            this(conversationId, whitePlayer, blackPlayer, triggerType);
            this.currentEval = currentEval;
            this.previousEval = previousEval;
        }
        
        ConversationState(String conversationId, String whitePlayer, String blackPlayer, String triggerType, Float currentEval, Float previousEval, EmotionalContext emotionalContext) {
            this(conversationId, whitePlayer, blackPlayer, triggerType, currentEval, previousEval);
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
        this.conversationMemory = ConversationMemoryManager.getInstance(context);
        this.varietyManager = ConversationVarietyManager.getInstance(context);
        
        // 🎭 ENHANCED: Initialize emotional intelligence with default masters for relationship tracking
        this.emotionalIntelligence.initializeWithHistory("tal", "fischer"); // Default initialization
        
        Log.d(TAG, "🎭 Enhanced emotional intelligence initialized with relationship tracking");
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        // 🎭 INITIALIZE CONVERSATION TEMPLATES: Set engaging conversations for spectator mode
        ConversationFlowTester.optimizeForSpectatorMode();
        
        // 🎭 Phase 2: Initialize multi-layered emotional complexity integration
        try {
            this.phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(context);
            Log.d(TAG, "🎭 Phase 2 emotional complexity bridge initialized");
        } catch (Exception e) {
            Log.d(TAG, "Phase 2 not available, using Phase 1 emotional intelligence only");
            this.phase2Bridge = null;
        }
        
        Log.d(TAG, "✅ SpectatorConversationOrchestrator fully initialized with engaging conversation templates!");
    }
    
    public static synchronized SpectatorConversationOrchestrator getInstance(Context context) {
        if (instance == null) {
            instance = new SpectatorConversationOrchestrator(context);
        }
        return instance;
    }
    
    /**
     * 🎤 NEW: Pause AI conversations during user voice recording
     */
    public void setUserRecordingInProgress(boolean recording) {
        this.userRecordingInProgress = recording;
        if (recording) {
            Log.d(TAG, "🎤 User recording started - pausing AI conversations");
            // Stop any current TTS
            if (ttsService != null && ttsService.isSpeaking()) {
                ttsService.stopSpeaking();
            }
        } else {
            Log.d(TAG, "🎤 User recording ended - AI conversations can resume");
        }
    }
    
    /**
     * 🎤 NEW: Check if user is currently recording
     */
    public boolean isUserRecording() {
        return userRecordingInProgress;
    }
    
    /**
     * 🎭 NEW: Get current speaker from active conversation
     */
    public String getCurrentSpeaker() {
        for (ConversationState state : activeConversations.values()) {
            if (state.isActive) {
                return state.currentSpeaker;
            }
        }
        return null; // No active conversation
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
        
        // NEW: Check if user is recording - if so, skip AI conversation
        if (userRecordingInProgress) {
            Log.d(TAG, "🎤 User recording in progress - skipping AI conversation");
            return;
        }
        
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
            state = new ConversationState(conversationId, whitePlayer, blackPlayer, triggerType, currentEval, previousEval, emotionalContext);
        } else {
            state = new ConversationState(conversationId, whitePlayer, blackPlayer, triggerType, currentEval, previousEval);
        }
        
        activeConversations.put(conversationId, state);
        
        // 🎭 ENHANCED: Initialize emotional intelligence with actual masters for this conversation
        emotionalIntelligence.initializeWithHistory(whitePlayer, blackPlayer);
        Log.d(TAG, String.format("🎭 Emotional intelligence initialized for %s vs %s conversation", whitePlayer, blackPlayer));
        
        // Determine first speaker based on trigger
        String firstSpeaker = determineFirstSpeaker(triggerType, whitePlayer, blackPlayer);
        state.currentSpeaker = firstSpeaker;
        
        Log.d(TAG, "🎬 CONVERSATION START: " + conversationId + " (" + triggerType + ")");
        Log.d(TAG, "🎬 Players: WHITE=" + whitePlayer + ", BLACK=" + blackPlayer + ", FIRST_SPEAKER=" + firstSpeaker);
        Log.d(TAG, "🎬 conversationInProgress set to: " + conversationInProgress);
        
        callback.onConversationStart(whitePlayer, blackPlayer);
        
        // Generate initial statement
        generateInitialStatement(state, triggerType, gameContext, callback);
    }
    
    /**
     * Generate initial statement using Responses API with fallback
     */
    private void generateInitialStatement(ConversationState state, String triggerType, 
                                         String gameContext, ConversationCallback callback) {
        Log.d(TAG, "🎬 generateInitialStatement called: trigger=" + triggerType + ", speaker=" + state.currentSpeaker);
        
        // Check if user recording is in progress - if so, skip AI conversation generation
        if (userRecordingInProgress) {
            Log.d(TAG, "🎤 User recording in progress - skipping AI initial statement generation");
            return;
        }
        
        String speaker = state.currentSpeaker;
        String opponent = speaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
        
        // Check if speaker has Responses API configured
        if (!hasResponsesAPIConfigured(speaker)) {
            Log.w(TAG, "⚠️ " + speaker + " doesn't have Responses API configured, using fallback dialogue generation");
            generateFallbackDialogue(state, triggerType, gameContext, callback);
            return;
        }
        
        Log.d(TAG, "✅ " + speaker + " has Responses API configured - proceeding with API call");
        
        executorService.execute(() -> {
            try {
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
                            
                            // 🧠 ENHANCED: Pass emotional context to Responses API
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
                                
                                // 🎭 RECORD EXPRESSION PATTERNS: Track how master expressed this concept
                                String concept = extractConceptFromTrigger(triggerType, gameContext);
                                String emotionalTone = detectEmotionalContext(state, state.currentEval, state.previousEval);
                                String argumentativeAngle = extractArgumentativeAngle(cleanedResponse);
                                emotionalIntelligence.getExpressionManager().recordExpression(
                                    speaker, concept, cleanedResponse, emotionalTone != null ? emotionalTone : "neutral", argumentativeAngle
                                );
                                
                                // Add to conversation history
                                state.turns.add(new ConversationTurn(speaker, cleanedResponse, triggerType));
                                state.turnCount++;
                                
                                // 🧠 CONVERSATION MEMORY: Record conversation for topic tracking
                                conversationMemory.recordConversation(speaker, cleanedResponse, gameContext);
                                
                                // Check for emotional context using enhanced system
                                String emotionalState = detectEmotionalContext(state, state.currentEval, state.previousEval);
                                
                                // 🎭 ENHANCED: Record emotional event for relationship evolution
                                recordEmotionalEventForConversation(state, speaker, cleanedResponse, emotionalState);
                                
                                // 🎭 ENHANCED: Deliver dialogue with sophisticated emotional intelligence
                                mainHandler.post(() -> {
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
                                    Log.d(TAG, "🔍 CONVERSATION DEBUG: After " + speaker + " spoke, checking if " + opponent + " should respond");
                                    Log.d(TAG, "🔍 State: whitePlayer=" + state.whitePlayer + ", blackPlayer=" + state.blackPlayer + ", turnCount=" + state.turnCount);
                                    Log.d(TAG, "🔍 Response text: " + cleanedResponse.substring(0, Math.min(100, cleanedResponse.length())));
                                    
                                    boolean shouldTrigger = shouldTriggerResponse(cleanedResponse, state);
                                    Log.d(TAG, "🔍 shouldTriggerResponse(" + speaker + " -> " + opponent + ") = " + shouldTrigger);
                                    
                                    if (shouldTrigger) {
                                        Log.d(TAG, "✅ CONVERSATION FLOW: Scheduling response from " + opponent + " to " + speaker);
                                        scheduleResponse(state, opponent, cleanedResponse, callback);
                                    } else {
                                        Log.w(TAG, "❌ CONVERSATION END: shouldTriggerResponse=false, ending conversation after " + speaker);
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
        
        Log.d(TAG, "⏰ SCHEDULE RESPONSE: " + responder + " will respond in " + delay + "ms to: " + previousStatement.substring(0, Math.min(50, previousStatement.length())));
        Log.d(TAG, "⏰ State before delay: isActive=" + state.isActive + ", turnCount=" + state.turnCount + ", maxTurns=" + MAX_CONVERSATION_TURNS);
        
        mainHandler.postDelayed(() -> {
            Log.d(TAG, "🕐 DELAY EXPIRED: About to check if " + responder + " should still respond");
            Log.d(TAG, "🕐 State after delay: isActive=" + state.isActive + ", turnCount=" + state.turnCount);
            
            if (!state.isActive || state.turnCount >= MAX_CONVERSATION_TURNS) {
                Log.w(TAG, "❌ RESPONSE CANCELLED: state.isActive=" + state.isActive + ", turnCount=" + state.turnCount + " >= maxTurns=" + MAX_CONVERSATION_TURNS);
                endConversation(state, state.currentSpeaker, previousStatement, callback);
                return;
            }
            
            Log.d(TAG, "✅ GENERATING RESPONSE: Calling generateResponse for " + responder);
            generateResponse(state, responder, previousStatement, callback);
        }, delay);
    }
    
    /**
     * Generate a response using Responses API
     */
    private void generateResponse(ConversationState state, String responder, 
                                 String previousStatement, ConversationCallback callback) {
        Log.d(TAG, "🎯 GENERATE RESPONSE: Starting response generation for " + responder);
        Log.d(TAG, "🎯 Previous statement from " + state.currentSpeaker + ": " + previousStatement.substring(0, Math.min(100, previousStatement.length())));
        
        // Check if user recording is in progress - if so, skip AI response generation
        if (userRecordingInProgress) {
            Log.d(TAG, "🎤 User recording in progress - skipping AI response generation");
            return;
        }
        
        // Check if responder has Responses API configured
        boolean hasAPI = hasResponsesAPIConfigured(responder);
        Log.d(TAG, "🎯 API CHECK: " + responder + " hasResponsesAPI = " + hasAPI);
        
        if (!hasAPI) {
            Log.w(TAG, "⚠️ " + responder + " doesn't have Responses API configured, using fallback response generation");
            generateFallbackResponse(state, responder, previousStatement, callback);
            return;
        }
        
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
                                        
                                        // 🎭 RECORD EXPRESSION PATTERNS: Track how master responded to opponent
                                        String concept = extractConceptFromResponse(previousStatement, cleanedResponse);
                                        String argumentativeAngle = extractArgumentativeAngle(cleanedResponse);
                                        emotionalIntelligence.getExpressionManager().recordExpression(
                                            responder, concept, cleanedResponse, emotionalContext != null ? emotionalContext : "neutral", argumentativeAngle
                                        );
                                        
                                        // Add to conversation history
                                        state.turns.add(new ConversationTurn(responder, cleanedResponse, emotionalContext));
                                        state.turnCount++;
                                        
                                        // 🧠 CONVERSATION MEMORY: Record conversation for topic tracking
                                        conversationMemory.recordConversation(responder, cleanedResponse, 
                                                                             buildConversationContext(state));
                                        
                                        // 🎭 ENHANCED: Record emotional event for relationship evolution
                                        recordEmotionalEventForConversation(state, responder, cleanedResponse, emotionalContext);
                                        
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
                                            
                                            Log.d(TAG, "🔄 RESPONSE COMPLETE: " + responder + " finished speaking. Next speaker would be: " + nextSpeaker);
                                            Log.d(TAG, "🔄 State: responder=" + responder + ", whitePlayer=" + state.whitePlayer + ", blackPlayer=" + state.blackPlayer);
                                            
                                            boolean shouldContinue = shouldContinueConversation(cleanedResponse, state);
                                            Log.d(TAG, "🔄 shouldContinueConversation(" + responder + " -> " + nextSpeaker + ") = " + shouldContinue);
                                                
                                            if (shouldContinue) {
                                                Log.d(TAG, "✅ CONTINUING: Scheduling response from " + nextSpeaker);
                                                scheduleResponse(state, nextSpeaker, cleanedResponse, callback);
                                            } else {
                                                Log.w(TAG, "🛑 ENDING: shouldContinueConversation=false, ending after " + responder);
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
        Log.d(TAG, "🔚 END CONVERSATION: " + state.conversationId + " with final speaker: " + finalSpeaker);
        Log.d(TAG, "🔚 Final message: " + finalMessage.substring(0, Math.min(100, finalMessage.length())));
        Log.d(TAG, "🔚 Total turns in conversation: " + state.turnCount);
        Log.d(TAG, "🔚 Setting conversationInProgress = false");
        
        state.isActive = false;
        conversationInProgress = false;
        activeConversations.remove(state.conversationId);
        
        callback.onConversationEnd(finalSpeaker, finalMessage);
    }
    
    /**
     * 🎭 FIXED: Speak dialogue with master's personality (race condition fixed)
     */
    private void speakWithPersonality(String speaker, String dialogue) {
        try {
            // Check if TTS is enabled in spectator mode
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
            
            if (!ttsEnabled) {
                Log.d(TAG, "🔇 TTS disabled - skipping speech for " + speaker);
                return;
            }
            
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
            
            // Check if TTS is enabled in spectator mode
            boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
            if (!ttsEnabled) {
                Log.d(TAG, "🔇 TTS disabled - skipping emotional personality speech for " + speaker);
                return;
            }
            
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
        // Get schema from new template system for dynamic conversation control
        ConversationSchema.Schema schema = ConversationSchemaTemplate.getSchemaForTrigger(state.triggerType);
        
        // Use schema-based logic instead of overly permissive triggers
        return ConversationSchema.shouldRespondToStatement(schema, statement, state.turnCount);
    }
    
    private boolean shouldContinueConversation(String response, ConversationState state) {
        // Use schema from template system for natural conversation flow
        ConversationSchema.Schema schema = ConversationSchemaTemplate.getSchemaForTrigger(state.triggerType);
        return ConversationSchema.shouldRespondToStatement(schema, response, state.turnCount);
    }
    
    /**
     * 🎭 ENHANCED: Sophisticated emotional analysis using EmotionalIntelligenceManager
     */
    private String detectEmotionalContext(ConversationState state) {
        return detectEmotionalContext(state, null, null);
    }
    
    /**
     * 🎭 ENHANCED: Sophisticated emotional analysis with evaluation data and advanced intelligence
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
            
            // 🔥 ENHANCED: Determine position themes for situational amplifiers
            String positionThemes = determinePositionThemes(currentEval, evalChange, gameContext);
            int timeRemaining = 300; // Default spectator mode time (can be enhanced later)
            
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
                
                // 🔥 NEW: Use enhanced emotional analysis with situational awareness
                emotionalResult = emotionalIntelligence.analyzeEmotionalStateWithSituation(
                    state.currentSpeaker,
                    gameContext,
                    conversationContext,
                    currentEval,
                    evalChange,
                    state.emotionalContext,
                    positionThemes,
                    timeRemaining
                );
            } else {
                // Create temporary EmotionalContext for enhanced analysis
                String opponent = state.currentSpeaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
                EmotionalContext tempContext = new EmotionalContext(state.currentSpeaker, opponent);
                tempContext.updateGameContext(state.turnCount, "spectator_game");
                
                // 🔥 NEW: Use enhanced emotional analysis with situational awareness
                emotionalResult = emotionalIntelligence.analyzeEmotionalStateWithSituation(
                    state.currentSpeaker,
                    gameContext,
                    conversationContext,
                    currentEval,
                    evalChange,
                    tempContext,
                    positionThemes,
                    timeRemaining
                );
            }
            
            // 🌊 EMOTIONAL CONTAGION: Apply contagion effects between masters
            String opponent = state.currentSpeaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
            EmotionalIntelligenceManager.EmotionalState finalEmotion = applyEmotionalContagionInConversation(
                state, emotionalResult, opponent
            );
            
            Log.d(TAG, String.format("🎭 Enhanced emotional analysis for %s: %s (intensity: %.2f, momentum: %.2f)", 
                  state.currentSpeaker, finalEmotion.name, 
                  emotionalResult.intensity, emotionalResult.momentum));
            
            // Return emotion name if intensity is significant enough
            if (emotionalResult.intensity > 0.2f) { // Threshold for emotional expression
                return finalEmotion.name;
            }
            
            return null; // No significant emotional state
            
        } catch (Exception e) {
            Log.e(TAG, "Error in enhanced emotional analysis, falling back to basic detection", e);
            return basicEmotionalFallback(state);
        }
    }
    
    /**
     * 🎭 ENHANCED: Build conversation context for emotional analysis with Phase 2 integration
     */
    private String buildConversationContext(ConversationState state) {
        if (state.turns.isEmpty()) {
            return "conversation_start";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("spectator_conversation");
        
        // 🎭 Phase 2: Try to get enhanced emotional context
        Phase2EmotionalIntegrationBridge phase2Bridge = null;
        try {
            phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(this.context);
        } catch (Exception e) {
            Log.d(TAG, "Phase 2 not available, using Phase 1 conversation context");
        }
        
        // 🎭 Phase 2: Generate enhanced conversation context if available
        if (phase2Bridge != null && phase2Bridge.isIntegrationActive()) {
            String baseContext = context.toString();
            String enhancedContext = phase2Bridge.generateEnhancedConversationContext(
                state.currentSpeaker, baseContext);
            
            if (enhancedContext != null && !enhancedContext.equals(baseContext)) {
                Log.d(TAG, "🎭 Phase 2: Enhanced conversation context generated for " + state.currentSpeaker);
                return enhancedContext;
            }
        }
        
        // Phase 1: Continue with original conversation context analysis
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
     * 🔥 NEW: Apply emotional contagion effects in conversation
     */
    private EmotionalIntelligenceManager.EmotionalState applyEmotionalContagionInConversation(
            ConversationState state, EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult, String opponent) {
        
        try {
            // Get opponent's recent emotional state from conversation history
            String opponentLastEmotion = null;
            float opponentIntensity = 0.5f;
            
            // Look for opponent's last emotional state in recent turns
            for (int i = state.turns.size() - 1; i >= 0; i--) {
                ConversationTurn turn = state.turns.get(i);
                if (turn.speaker.equals(opponent) && turn.emotionalContext != null) {
                    opponentLastEmotion = turn.emotionalContext;
                    opponentIntensity = 0.7f; // Assume moderate intensity from context
                    break;
                }
            }
            
            // Apply emotional contagion if we have opponent's emotional state
            if (opponentLastEmotion != null) {
                Log.d(TAG, String.format("🌊 Applying emotional contagion: %s (%s) ← %s (%s, %.2f)", 
                      state.currentSpeaker, emotionalResult.emotion.name, 
                      opponent, opponentLastEmotion, opponentIntensity));
                
                EmotionalIntelligenceManager.EmotionalState contagionResult = 
                    emotionalIntelligence.applyEmotionalContagion(
                        state.currentSpeaker,
                        emotionalResult.emotion,
                        opponent,
                        opponentLastEmotion,
                        opponentIntensity
                    );
                
                return contagionResult;
            }
            
            return emotionalResult.emotion; // No contagion applied
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying emotional contagion", e);
            return emotionalResult.emotion; // Fallback to original emotion
        }
    }
    
    /**
     * 🎯 NEW: Determine position themes for situational amplifiers
     */
    private String determinePositionThemes(Float currentEval, Float evalChange, String gameContext) {
        List<String> themes = new ArrayList<>();
        
        if (currentEval != null && evalChange != null) {
            float absEval = Math.abs(currentEval);
            float absChange = Math.abs(evalChange);
            
            // Evaluation-based themes
            if (absEval > 3.0f) {
                themes.add("decisive");
            } else if (absEval > 1.5f) {
                themes.add("advantage");
            } else if (absEval < 0.5f) {
                themes.add("balanced");
            }
            
            // Change-based themes
            if (absChange > 2.0f) {
                themes.add("dramatic_swing");
                themes.add("critical");
            } else if (absChange > 1.0f) {
                themes.add("significant_change");
            }
            
            // Tactical vs positional themes based on change magnitude
            if (absChange > 1.5f) {
                themes.add("tactical");
            } else {
                themes.add("positional");
            }
        }
        
        // Game context themes
        if (gameContext != null) {
            String context = gameContext.toLowerCase();
            if (context.contains("opening")) {
                themes.add("opening");
            } else if (context.contains("endgame")) {
                themes.add("endgame");
            } else {
                themes.add("middlegame");
            }
            
            if (context.contains("blunder")) {
                themes.add("error");
                themes.add("critical");
            } else if (context.contains("brilliant")) {
                themes.add("creative");
                themes.add("artistic");
            }
        }
        
        return String.join(",", themes);
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
     * 🎭 NEW: Enhanced emotional TTS using full EmotionalIntelligenceManager results with situational awareness
     */
    private void speakWithEnhancedEmotionalIntelligence(String speaker, String dialogue, ConversationState state) {
        try {
            // Save current master preference
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            
            // Check if TTS is enabled in spectator mode
            boolean ttsEnabled = prefs.getBoolean("tts_enabled", true);
            if (!ttsEnabled) {
                Log.d(TAG, "🔇 TTS disabled - skipping enhanced emotional speech for " + speaker);
                return;
            }
            
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
            
            // 🔥 ENHANCED: Use full situational emotional analysis for TTS
            String positionThemes = determinePositionThemes(currentEval, evalChange, gameContext);
            int timeRemaining = 300; // Default spectator mode time
            
            EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult = 
                emotionalIntelligence.analyzeEmotionalStateWithSituation(
                    speaker,
                    gameContext,
                    conversationContext,
                    currentEval,
                    evalChange,
                    state.emotionalContext,
                    positionThemes,
                    timeRemaining
                );
            
            // 🌊 Apply emotional contagion for voice synthesis
            String opponent = speaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
            EmotionalIntelligenceManager.EmotionalState finalEmotion = applyEmotionalContagionInConversation(
                state, emotionalResult, opponent
            );
            
            // Update result with contagion effect
            EmotionalIntelligenceManager.EmotionalAnalysisResult enhancedResult = 
                new EmotionalIntelligenceManager.EmotionalAnalysisResult(
                    finalEmotion,
                    emotionalResult.expression,
                    emotionalResult.intensity,
                    emotionalResult.momentum
                );
            
            Log.d(TAG, String.format("🎭 Enhanced TTS with contagion for %s: %s (intensity: %.2f, momentum: %.2f)", 
                  speaker, enhancedResult.emotion.name, enhancedResult.intensity, enhancedResult.momentum));
            
            // Use ElevenLabs if available for superior emotional voice synthesis
            if (TTSServiceManager.isUsingElevenLabs(context)) {
                speakWithElevenLabsEmotionalIntelligence(speaker, dialogue, enhancedResult, currentMaster);
            } else {
                speakWithOpenAIEmotionalIntelligence(speaker, dialogue, enhancedResult, currentMaster);
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
        
        // 🎭 EXPRESSION PATTERNS: Get anti-repetition guidance to prevent repetitive phrasing
        EmotionalIntelligenceManager.ExpressionGuidance expressionGuidance = 
            emotionalIntelligence.getExpressionManager().getExpressionGuidance(speaker, triggerType, "neutral");
        
        // Generate varied prompts to avoid repetitive responses
        Random rand = new Random();
        
        // Add personality flavor to prompts
        String speakerLower = speaker.toLowerCase();
        
        // 🧠 Build enhanced prompt with conversation memory AND expression pattern guidance
        StringBuilder enhancedPrompt = new StringBuilder();
        
        // Add conversation memory instructions first
        if (!guidance.contextInstructions.isEmpty()) {
            enhancedPrompt.append(guidance.contextInstructions).append("\n\n");
        }
        
        // 🎭 NEW: Add expression diversity instructions
        if (expressionGuidance != null && expressionGuidance.shouldUseFreshApproach) {
            enhancedPrompt.append("🎭 EXPRESSION DIVERSITY GUIDANCE:\n");
            enhancedPrompt.append(expressionGuidance.buildAntiRepetitionInstructions()).append("\n\n");
        }
        
        // 🤝 PHASE 3: Add dynamic relationship evolution guidance
        String relationshipGuidance = emotionalIntelligence.getExpressionManager().buildRelationshipGuidance(speaker, opponent, triggerType);
        if (relationshipGuidance != null && !relationshipGuidance.trim().isEmpty()) {
            enhancedPrompt.append("🤝 RELATIONSHIP DYNAMICS:\n");
            enhancedPrompt.append(relationshipGuidance).append("\n\n");
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
        
        // 🎭 EXPRESSION PATTERNS: Get anti-repetition guidance for responses
        EmotionalIntelligenceManager.ExpressionGuidance expressionGuidance = 
            emotionalIntelligence.getExpressionManager().getExpressionGuidance(responder, "response", "conversational");
        
        // Detect emotional context for the prompt
        String emotionalContext = detectEmotionalContext(state, state.currentEval, state.previousEval);
        Float evalChange = evaluationTracker.getRecentEvaluationChange();
        
        // Build prompt with personality clash potential, conversation memory, AND expression guidance
        StringBuilder prompt = new StringBuilder();
        
        // 🧠 Add conversation memory instructions
        if (!guidance.contextInstructions.isEmpty()) {
            prompt.append(guidance.contextInstructions).append("\n\n");
        }
        
        // 🎭 NEW: Add expression diversity instructions for responses
        if (expressionGuidance != null && expressionGuidance.shouldUseFreshApproach) {
            prompt.append("🎭 RESPONSE VARIETY GUIDANCE:\n");
            prompt.append(expressionGuidance.buildAntiRepetitionInstructions()).append("\n\n");
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
     * 🎭 NEW: Record emotional events for relationship evolution tracking
     */
    private void recordEmotionalEventForConversation(ConversationState state, String speaker, String dialogue, String emotionalState) {
        try {
            if (emotionalState == null || emotionalState.isEmpty()) {
                return; // No emotional context to record
            }
            
            String opponent = speaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
            
            // Determine topic from dialogue content
            String topic = extractTopicFromDialogue(dialogue);
            
            // Calculate emotional intensity from the state
            float intensity = calculateIntensityFromEmotionalState(emotionalState);
            
            // Use EmotionalIntelligenceManager to persist this event
            EmotionalIntelligenceManager.EmotionalEvent event = new EmotionalIntelligenceManager.EmotionalEvent(
                topic,
                EmotionalIntelligenceManager.EmotionalState.valueOf(emotionalState.toUpperCase()),
                intensity
            );
            
            emotionalIntelligence.persistEmotionalEvent(event, dialogue);
            
            Log.d(TAG, String.format("🎭 Recorded emotional event: %s feels %s about %s (intensity: %.2f)", 
                  speaker, emotionalState, topic, intensity));
            
        } catch (Exception e) {
            Log.e(TAG, "Error recording emotional event for conversation", e);
        }
    }
    
    /**
     * Extract main topic from dialogue content
     */
    private String extractTopicFromDialogue(String dialogue) {
        String lower = dialogue.toLowerCase();
        
        // Chess-specific topics
        if (lower.contains("position") || lower.contains("move")) {
            return "position_analysis";
        } else if (lower.contains("tactic") || lower.contains("combination")) {
            return "tactical_discussion";
        } else if (lower.contains("strategy") || lower.contains("plan")) {
            return "strategic_planning";
        } else if (lower.contains("opening")) {
            return "opening_theory";
        } else if (lower.contains("endgame")) {
            return "endgame_technique";
        } else if (lower.contains("blunder") || lower.contains("mistake")) {
            return "error_analysis";
        } else if (lower.contains("brilliant") || lower.contains("creative")) {
            return "creative_play";
        } else if (lower.contains("style") || lower.contains("philosophy")) {
            return "chess_philosophy";
        } else {
            return "general_discussion";
        }
    }
    
    /**
     * Calculate emotional intensity from state name
     */
    private float calculateIntensityFromEmotionalState(String emotionalState) {
        switch (emotionalState.toLowerCase()) {
            case "ecstatic":
            case "devastated":
                return 1.0f;
            case "thrilled":
            case "frustrated":
                return 0.8f;
            case "excited":
            case "concerned":
                return 0.6f;
            case "pleased":
            case "uneasy":
                return 0.4f;
            case "content":
            case "focused":
                return 0.3f;
            default:
                return 0.5f; // Default moderate intensity
        }
    }
    
    /**
     * Cleanup resources
     */
    // =========================== EXPRESSION PATTERN HELPERS ===========================
    
    /**
     * 🎭 Extract the main concept being discussed from trigger type and context
     */
    private String extractConceptFromTrigger(String triggerType, String gameContext) {
        switch (triggerType) {
            case "opening":
                return "opening_assessment";
            case "brilliant_move":
                return "tactical_praise";
            case "blunder":
                return "error_criticism";
            case "endgame":
                return "game_conclusion";
            case "evaluation_swing":
                return "position_analysis";
            default:
                return "general_discussion";
        }
    }
    
    /**
     * 🎭 Extract concept from response context
     */
    private String extractConceptFromResponse(String previousStatement, String response) {
        String lowerPrev = previousStatement.toLowerCase();
        String lowerResp = response.toLowerCase();
        
        if (lowerPrev.contains("brilliant") || lowerResp.contains("brilliant")) {
            return "tactical_discussion";
        } else if (lowerPrev.contains("mistake") || lowerResp.contains("mistake") || 
                   lowerPrev.contains("blunder") || lowerResp.contains("blunder")) {
            return "error_analysis";
        } else if (lowerResp.contains("disagree") || lowerResp.contains("but") || 
                   lowerResp.contains("wrong") || lowerResp.contains("no,")) {
            return "counterargument";
        } else if (lowerResp.contains("exactly") || lowerResp.contains("agree") || 
                   lowerResp.contains("precisely") || lowerResp.contains("yes")) {
            return "agreement";
        } else if (lowerResp.contains("truth") || lowerResp.contains("analysis") || 
                   lowerResp.contains("perfect")) {
            return "perfectionism"; // Fischer's favorite topics
        } else {
            return "position_discussion";
        }
    }
    
    /**
     * 🎭 Extract argumentative angle from response text - critical for personality tracking
     */
    private String extractArgumentativeAngle(String response) {
        String lower = response.toLowerCase();
        
        // Fischer-specific patterns
        if (lower.contains("truth") && (lower.contains("board") || lower.contains("analysis"))) {
            return "truth_appeal";
        } else if (lower.contains("perfect") && (lower.contains("demand") || lower.contains("require"))) {
            return "standards_rant";
        } else if (lower.contains("disagree") || lower.contains("wrong") || lower.contains("nonsense")) {
            return "dismissive_criticism";
        } else if (lower.contains("conspiracy") || lower.contains("collusion") || lower.contains("cheating")) {
            return "paranoid_accusation";
        }
        
        // Carlsen-specific patterns  
        else if (lower.contains("practical") || lower.contains("step by step")) {
            return "methodical_approach";
        } else if (lower.contains("pressure") && lower.contains("build")) {
            return "pressure_builder";
        }
        
        // General argumentative patterns
        else if (lower.contains("exactly") || lower.contains("precisely") || lower.contains("agree")) {
            return "agreement";
        } else if (lower.contains("but") || lower.contains("however") || lower.contains("although")) {
            return "qualified_disagreement";
        } else if (lower.contains("?") && lower.contains("you")) {
            return "challenging_question";
        } else if (lower.contains("!") || lower.contains("brilliant") || lower.contains("incredible")) {
            return "emphatic_praise";
        } else if (lower.contains("mistake") || lower.contains("error") || lower.contains("blunder")) {
            return "error_criticism";
        } else {
            return "neutral_analysis";
        }
    }
    
    /**
     * Check if master has Responses API configured
     */
    private boolean hasResponsesAPIConfigured(String masterName) {
        switch (masterName.toLowerCase()) {
            case "alekhine":
            case "carlsen": 
            case "fischer":
            case "tal":
            case "anand":
            case "kasparov":
                return true;
            default:
                Log.w(TAG, "🚫 Master " + masterName + " doesn't have Responses API configured");
                return false;
        }
    }
    
    /**
     * Fallback dialogue generation for masters without Responses API
     */
    private void generateFallbackDialogue(ConversationState state, String triggerType, 
                                        String gameContext, ConversationCallback callback) {
        executorService.execute(() -> {
            try {
                String speaker = state.currentSpeaker;
                String opponent = speaker.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
                
                Log.d(TAG, "🔄 Generating fallback dialogue for " + speaker);
                
                // Create a simple dialogue based on master personality and trigger
                String dialogue = createFallbackDialogue(speaker, opponent, triggerType, gameContext);
                
                // 🎭 RECORD EXPRESSION PATTERNS: Track how master expressed this concept
                String concept = extractConceptFromTrigger(triggerType, gameContext);
                String emotionalTone = detectEmotionalContext(state, state.currentEval, state.previousEval);
                String argumentativeAngle = extractArgumentativeAngle(dialogue);
                emotionalIntelligence.getExpressionManager().recordExpression(
                    speaker, concept, dialogue, emotionalTone != null ? emotionalTone : "neutral", argumentativeAngle
                );
                
                // Add to conversation history
                state.turns.add(new ConversationTurn(speaker, dialogue, triggerType));
                state.turnCount++;
                
                // 🧠 CONVERSATION MEMORY: Record conversation for topic tracking
                conversationMemory.recordConversation(speaker, dialogue, gameContext);
                
                // Deliver dialogue
                mainHandler.post(() -> {
                    if (emotionalTone != null) {
                        callback.onEmotionalResponse(speaker, emotionalTone, dialogue);
                    } else {
                        callback.onDialogueGenerated(speaker, dialogue);
                    }
                    
                    // TTS
                    executorService.execute(() -> {
                        try {
                            speakWithPersonality(speaker, dialogue);
                        } catch (Exception e) {
                            Log.e(TAG, "Error in fallback TTS", e);
                        }
                    });
                    
                    // Schedule response if appropriate
                    if (shouldTriggerResponse(dialogue, state)) {
                        scheduleResponse(state, opponent, dialogue, callback);
                    } else {
                        endConversation(state, speaker, dialogue, callback);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error in fallback dialogue generation", e);
                mainHandler.post(() -> callback.onError("Fallback dialogue error: " + e.getMessage()));
                endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
            }
        });
    }
    
    /**
     * Fallback response generation for masters without Responses API
     */
    private void generateFallbackResponse(ConversationState state, String responder, 
                                        String previousStatement, ConversationCallback callback) {
        executorService.execute(() -> {
            try {
                state.currentSpeaker = responder;
                String opponent = responder.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
                
                Log.d(TAG, "🔄 Generating fallback response for " + responder);
                
                // Create a response based on the previous statement and master personality
                String response = createFallbackResponse(responder, opponent, previousStatement, state);
                
                // 🎭 RECORD EXPRESSION PATTERNS: Track how master expressed this concept
                String concept = extractConceptFromResponse(previousStatement);
                String emotionalTone = detectEmotionalContext(state, state.currentEval, state.previousEval);
                String argumentativeAngle = extractArgumentativeAngle(response);
                emotionalIntelligence.getExpressionManager().recordExpression(
                    responder, concept, response, emotionalTone != null ? emotionalTone : "neutral", argumentativeAngle
                );
                
                // Add to conversation history
                state.turns.add(new ConversationTurn(responder, response, "response"));
                state.turnCount++;
                
                // 🧠 CONVERSATION MEMORY: Record conversation for topic tracking
                conversationMemory.recordConversation(responder, response, buildConversationContext(state));
                
                // Deliver response
                mainHandler.post(() -> {
                    if (emotionalTone != null) {
                        callback.onEmotionalResponse(responder, emotionalTone, response);
                    } else {
                        callback.onDialogueGenerated(responder, response);
                    }
                    
                    // TTS
                    executorService.execute(() -> {
                        try {
                            speakWithPersonality(responder, response);
                        } catch (Exception e) {
                            Log.e(TAG, "Error in fallback response TTS", e);
                        }
                    });
                    
                    // Continue or end conversation
                    String nextSpeaker = responder.equals(state.whitePlayer) ? state.blackPlayer : state.whitePlayer;
                    if (shouldContinueConversation(response, state)) {
                        scheduleResponse(state, nextSpeaker, response, callback);
                    } else {
                        endConversation(state, responder, response, callback);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error in fallback response generation", e);
                mainHandler.post(() -> callback.onError("Fallback response error: " + e.getMessage()));
                endConversation(state, state.currentSpeaker, "[Error occurred]", callback);
            }
        });
    }
    
    /**
     * Create fallback response based on previous statement and master personality
     */
    private String createFallbackResponse(String responder, String opponent, String previousStatement, ConversationState state) {
        String masterLower = responder.toLowerCase();
        String statementLower = previousStatement.toLowerCase();
        
        // Analyze the previous statement to determine response type
        if (statementLower.contains("?")) {
            // Answer questions
            return createFallbackQuestionResponse(masterLower, opponent, previousStatement);
        } else if (statementLower.contains("brilliant") || statementLower.contains("excellent") || statementLower.contains("magnificent")) {
            // Respond to praise
            return createFallbackPraiseResponse(masterLower, opponent);
        } else if (statementLower.contains("mistake") || statementLower.contains("blunder") || statementLower.contains("error")) {
            // Respond to criticism
            return createFallbackCriticismResponse(masterLower, opponent);
        } else if (statementLower.contains("disagree") || statementLower.contains("wrong") || statementLower.contains("but")) {
            // Respond to disagreement
            return createFallbackDisagreementResponse(masterLower, opponent);
        } else {
            // General continuation
            return createFallbackGeneralResponse(masterLower, opponent, previousStatement);
        }
    }
    
    /**
     * Helper methods for different types of fallback responses
     */
    private String createFallbackQuestionResponse(String master, String opponent, String question) {
        switch (master) {
            case "kasparov": return "That's an excellent question! Chess demands both intuition and calculation.";
            case "karpov": return "The answer lies in careful positional evaluation and patience.";
            case "kramnik": return "Modern theory provides several insights into this matter.";
            case "botvinnik": return "Scientific analysis reveals the optimal approach here.";
            case "morphy": return "The natural principles of development guide us to the answer.";
            case "lasker": return "Human psychology and practical considerations suggest...";
            case "capablanca": return "The clearest and most logical response is evident.";
            default: return "An interesting point that deserves careful consideration.";
        }
    }
    
    private String createFallbackPraiseResponse(String master, String opponent) {
        switch (master) {
            case "kasparov": return "Indeed! Dynamic play and fighting spirit create such moments.";
            case "karpov": return "Precision and technique naturally lead to such positions.";
            case "kramnik": return "Computer-like accuracy produces these results.";
            case "botvinnik": return "Thorough preparation makes the complex appear simple.";
            case "morphy": return "Natural development reveals the beauty of chess.";
            case "lasker": return "Practical strength overcomes theoretical knowledge.";
            case "capablanca": return "Clarity of thought produces such natural combinations.";
            default: return "Thank you. Chess rewards deep understanding.";
        }
    }
    
    private String createFallbackCriticismResponse(String master, String opponent) {
        switch (master) {
            case "kasparov": return "Chess is unforgiving! We must fight harder to avoid such errors.";
            case "karpov": return "Such imprecision disrupts the positional harmony.";
            case "kramnik": return "The engines would immediately flag this as suboptimal.";
            case "botvinnik": return "Insufficient preparation leads to such tactical oversights.";
            case "morphy": return "Even strong players can fall victim to tactical blindness.";
            case "lasker": return "Human nature reveals itself in moments of pressure.";
            case "capablanca": return "The position was clear, yet confusion crept in somehow.";
            default: return "Chess has a way of punishing imprecision.";
        }
    }
    
    private String createFallbackDisagreementResponse(String master, String opponent) {
        // Generate multiple disagreement options to prevent repetition
        List<String> disagreementOptions = new ArrayList<>();
        
        switch (master) {
            case "kasparov": 
                disagreementOptions.add("I must respectfully disagree! Bold play is sometimes necessary.");
                disagreementOptions.add("With all respect, dynamic factors favor a different approach!");
                disagreementOptions.add("I see it differently - the position calls for more aggressive play!");
                disagreementOptions.add("Forgive me, but I believe we need maximum energy here!");
                break;
            case "karpov": 
                disagreementOptions.add("Perhaps, but positional factors suggest otherwise.");
                disagreementOptions.add("I respectfully see the structural elements differently.");
                disagreementOptions.add("With due consideration, the static factors point elsewhere.");
                disagreementOptions.add("Allow me to offer a different positional perspective.");
                break;
            case "kramnik": 
                disagreementOptions.add("The computer evaluation might tell a different story.");
                disagreementOptions.add("Modern analysis suggests an alternative approach.");
                disagreementOptions.add("I respectfully question this assessment based on deep preparation.");
                disagreementOptions.add("Technical analysis points to a different conclusion.");
                break;
            case "botvinnik": 
                disagreementOptions.add("Scientific analysis requires examining all variations.");
                disagreementOptions.add("Methodical study reveals alternative possibilities.");
                disagreementOptions.add("Systematic preparation suggests a different path.");
                disagreementOptions.add("Theoretical research indicates another direction.");
                break;
            case "morphy": 
                disagreementOptions.add("The natural flow of the game suggests a different path.");
                disagreementOptions.add("With respect, the classical principles guide us elsewhere.");
                disagreementOptions.add("Natural development points to an alternative approach.");
                disagreementOptions.add("Clear thinking reveals a different route forward.");
                break;
            case "lasker": 
                disagreementOptions.add("Chess allows for different approaches and philosophies.");
                disagreementOptions.add("The psychology of the position suggests otherwise.");
                disagreementOptions.add("Human understanding often differs from pure calculation.");
                disagreementOptions.add("Experience teaches us to consider alternative viewpoints.");
                break;
            case "capablanca": 
                disagreementOptions.add("I see the position differently, with respect.");
                disagreementOptions.add("Simplicity and clarity suggest another direction.");
                disagreementOptions.add("Natural judgment points to a different approach.");
                disagreementOptions.add("With all respect, logical thinking leads elsewhere.");
                break;
            default: 
                disagreementOptions.add("An interesting perspective, though I see it differently.");
                disagreementOptions.add("I respectfully offer an alternative viewpoint.");
                disagreementOptions.add("With consideration, I must present a different analysis.");
                disagreementOptions.add("Allow me to share a contrasting perspective.");
                break;
        }
        
        // Select a response that hasn't been used recently
        String selected = selectBestResponse(master, disagreementOptions, null);
        
        // Track this disagreement response
        varietyManager.trackResponse(master, selected);
        
        Log.d(TAG, String.format("🎯 Selected disagreement response for %s: %.40s...", master, selected));
        
        return selected;
    }
    
    private String createFallbackGeneralResponse(String master, String opponent, String statement) {
        switch (master) {
            case "kasparov": return "The fighting spirit must continue! Every position has potential.";
            case "karpov": return "Steady progress and sound technique will guide us forward.";
            case "kramnik": return "Let's analyze this position with modern methods.";
            case "botvinnik": return "Systematic study reveals the key ideas.";
            case "morphy": return "Natural development and sound principles apply here.";
            case "lasker": return "The psychological battle continues alongside the chess.";
            case "capablanca": return "Simple, clear moves often prove most effective.";
            default: return "The position offers interesting possibilities for both sides.";
        }
    }
    
    /**
     * Extract concept from response context
     */
    private String extractConceptFromResponse(String previousStatement) {
        String lower = previousStatement.toLowerCase();
        if (lower.contains("opening")) return "opening_theory";
        if (lower.contains("tactic")) return "tactical_discussion";
        if (lower.contains("position")) return "position_analysis";
        if (lower.contains("strategy")) return "strategic_planning";
        if (lower.contains("mistake") || lower.contains("blunder")) return "error_analysis";
        if (lower.contains("brilliant")) return "creative_play";
        return "general_discussion";
    }

    /**
     * Enhanced fallback dialogue with variety and repetition detection
     */
    private String createFallbackDialogue(String speaker, String opponent, String triggerType, String gameContext) {
        String masterLower = speaker.toLowerCase();
        
        // Generate multiple candidate responses and select one that hasn't been used recently
        List<String> candidateResponses = generateCandidateResponses(masterLower, triggerType);
        
        // Check conversation memory for guidance
        ConversationMemoryManager.ConversationGuidance guidance = 
            conversationMemory.getConversationGuidanceWithEmotion(speaker, opponent, gameContext, null, null);
        
        // Filter out overused phrases and select best response
        String selectedResponse = selectBestResponse(speaker, candidateResponses, guidance);
        
        // Track this response to prevent future repetition
        varietyManager.trackResponse(speaker, selectedResponse);
        
        // Record in conversation memory
        conversationMemory.recordConversationWithEmotionAndOpponent(
            speaker, selectedResponse, gameContext, "analytical", 0.6f, opponent, "listening");
        
        Log.d(TAG, String.format("🎯 Selected fallback response for %s: %.50s...", speaker, selectedResponse));
        
        return selectedResponse;
    }
    
    /**
     * Generate multiple candidate responses for variety
     */
    private List<String> generateCandidateResponses(String masterLower, String triggerType) {
        List<String> candidates = new ArrayList<>();
        
        // Get base responses for this master and trigger
        switch (masterLower) {
            case "kasparov":
                switch (triggerType) {
                    case "opening": 
                        candidates.add("This opening requires dynamic play and sharp calculation!");
                        candidates.add("We must seize the initiative from the very first moves!");
                        candidates.add("Bold opening choices define the character of the entire game!");
                        candidates.add("The opening battle sets the tone for tactical complications!");
                        break;
                    case "brilliant_move": 
                        candidates.add("Magnificent! This shows the true fighting spirit of chess!");
                        candidates.add("Spectacular! This is what I call concrete chess!");
                        candidates.add("Brilliant execution! The position comes alive with possibilities!");
                        candidates.add("Powerful play! This demonstrates championship-level thinking!");
                        break;
                    case "blunder": 
                        candidates.add("A serious mistake! Chess punishes such oversights mercilessly.");
                        candidates.add("Unforgivable! At this level, precision is everything!");
                        candidates.add("A critical error that shifts the entire evaluation!");
                        candidates.add("Such imprecision cannot be tolerated in serious chess!");
                        break;
                    case "response": 
                        candidates.add("I must respond with maximum energy and initiative!");
                        candidates.add("The position calls for dynamic counter-action!");
                        candidates.add("We cannot allow passive play in such sharp positions!");
                        candidates.add("Active piece play is the key to maintaining balance!");
                        break;
                    default: 
                        candidates.add("The position demands concrete analysis and bold decisions.");
                        candidates.add("Sharp tactical ideas emerge from every piece placement!");
                        candidates.add("Dynamic factors outweigh static considerations here!");
                        candidates.add("The struggle intensifies with each passing move!");
                        break;
                }
                break;
                
            case "karpov":
                switch (triggerType) {
                    case "opening": 
                        candidates.add("A solid positional approach will serve us well here.");
                        candidates.add("Methodical development creates lasting advantages.");
                        candidates.add("Patient maneuvering will reveal the position's secrets.");
                        candidates.add("Structural considerations guide our piece placement.");
                        break;
                    case "brilliant_move": 
                        candidates.add("Excellent technique. Every piece finds its perfect square.");
                        candidates.add("Masterful coordination! The pieces work in perfect harmony.");
                        candidates.add("Refined execution shows deep positional understanding.");
                        candidates.add("This demonstrates the beauty of precise technique.");
                        break;
                    case "blunder": 
                        candidates.add("Such imprecision disturbs the harmony of the position.");
                        candidates.add("Inaccurate play compromises the structural foundation.");
                        candidates.add("This error creates unnecessary weaknesses.");
                        candidates.add("Positional misjudgment leads to lasting problems.");
                        break;
                    case "response": 
                        candidates.add("Patience and accuracy will reveal the correct path.");
                        candidates.add("Careful evaluation prevents unnecessary risks.");
                        candidates.add("Steady improvement maintains the positional balance.");
                        candidates.add("Methodical play extracts maximum value from each move.");
                        break;
                    default: 
                        candidates.add("The position requires careful evaluation and precise technique.");
                        candidates.add("Positional factors determine the optimal continuation.");
                        candidates.add("Systematic improvement of piece coordination is key.");
                        candidates.add("Refined technique converts small advantages.");
                        break;
                }
                break;
                
            default:
                // Generic fallback for any other masters
                switch (triggerType) {
                    case "opening": 
                        candidates.add("An interesting opening choice with rich possibilities.");
                        candidates.add("The opening setup promises complex middlegame play.");
                        candidates.add("This approach leads to instructive strategic themes.");
                        break;
                    case "brilliant_move": 
                        candidates.add("Excellent play! This move shows deep understanding.");
                        candidates.add("Impressive! The calculation must have been demanding.");
                        candidates.add("Beautiful execution of a complex idea!");
                        break;
                    case "blunder": 
                        candidates.add("A critical error that changes the evaluation significantly.");
                        candidates.add("An unfortunate oversight in a crucial moment.");
                        candidates.add("Such mistakes are part of the human element in chess.");
                        break;
                    case "response": 
                        candidates.add("The position demands careful consideration.");
                        candidates.add("Multiple factors require thoughtful evaluation.");
                        candidates.add("This moment calls for precise judgment.");
                        break;
                    default: 
                        candidates.add("The game continues with interesting challenges ahead.");
                        candidates.add("Each move brings new possibilities to explore.");
                        candidates.add("The position evolves with fascinating complexity.");
                        break;
                }
                break;
        }
        
        return candidates;
    }
    
    /**
     * Select the best response from candidates, avoiding recently used phrases
     */
    private String selectBestResponse(String speaker, List<String> candidates, 
                                    ConversationMemoryManager.ConversationGuidance guidance) {
        if (candidates.isEmpty()) {
            return "The position offers many interesting possibilities.";
        }
        
        // Filter out responses that would be repetitive
        List<String> filteredCandidates = new ArrayList<>();
        for (String candidate : candidates) {
            if (!varietyManager.isPotentialRepetition(speaker, candidate)) {
                filteredCandidates.add(candidate);
            }
        }
        
        // If all candidates are potentially repetitive, use the least recent one
        if (filteredCandidates.isEmpty()) {
            Log.w(TAG, "⚠️ All candidate responses are potentially repetitive for " + speaker);
            filteredCandidates = candidates; // Use all candidates as fallback
        }
        
        // Select randomly from filtered candidates
        Random random = new Random();
        String selected = filteredCandidates.get(random.nextInt(filteredCandidates.size()));
        
        Log.d(TAG, String.format("🎯 Selected response for %s: %d candidates → %d filtered → %.30s...", 
               speaker, candidates.size(), filteredCandidates.size(), selected));
        
        return selected;
    }

    public void cleanup() {
        forceStop();
        executorService.shutdown();
        responsesManager.shutdown();
    }
}