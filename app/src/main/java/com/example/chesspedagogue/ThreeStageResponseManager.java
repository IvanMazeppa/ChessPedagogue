package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.github.bassaer.chatmessageview.model.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ENHANCED 3-Stage AI Response System with Responses API Integration
 * 🎤 NOW WITH SUPERIOR ELEVENLABS TTS BY DEFAULT!
 * 🚀 UPGRADED: Uses OpenAI Responses API for stateful conversations
 *
 * Stage 1: Lightning-fast responses (under 2 seconds) - Uses Responses API when available
 * Stage 2: Enhanced personality responses (5-10 seconds) - Fine-tuned models via Responses API
 * Stage 3: Deep analysis with vector store (10-20 seconds) - Assistant API with context
 *
 * Compatible with Android API 26+ (no orTimeout dependency)
 * Uses TTSServiceManager to automatically select ElevenLabs TTS for superior voice quality
 * Seamlessly falls back to Chat Completions for unsupported masters
 */
public class ThreeStageResponseManager {
    private static final String TAG = "ThreeStageResponseManager";
    private static final int MAX_RETRIES = 3;

    // OPTIMIZED TIMEOUTS - Stage 1 is now truly fast!
    private static final long STAGE_1_TIMEOUT_MS = 3000;  // 3 seconds max for quick response
    private static final long STAGE_2_TIMEOUT_MS = 15000; // 15 seconds for enhanced
    private static final long STAGE_3_TIMEOUT_MS = 30000; // 30 seconds for deep analysis

    private static ThreeStageResponseManager instance;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;

    // Services
    private final OpenAIService openAIService;
    private final FineTunedModelManager modelManager;
    private final OpenAITTSService ttsService;
    private final EnhancedContextManager contextManager;
    
    // NEW: Responses API integration
    private final ChessMasterResponsesManager responsesManager;
    private final ResponsesAPIIntegrationHelper integrationHelper;
    
    // Session tracking for stateful conversations
    private final Map<String, String> activeSessions = new HashMap<>();

    // Current response tracking
    private final AtomicInteger currentResponseId = new AtomicInteger(0);
    private volatile boolean isStage1Speaking = false;
    private volatile boolean isStage2Speaking = false;

    /**
     * Response stages enumeration
     */
    public enum ResponseStage {
        STAGE_1_QUICK("Quick Response"),
        STAGE_2_ENHANCED("Enhanced Analysis"),
        STAGE_3_DEEP("Master Insights");

        private final String displayName;

        ResponseStage(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Response modes for different user preferences
     */
    public enum ResponseMode {
        FAST_ONLY,          // Stage 1 only
        ENHANCED_ONLY,      // Stages 1-2 only
        FULL_ANALYSIS,      // All 3 stages
        ADAPTIVE            // Auto-decide based on question complexity
    }

    /**
     * Callback interface for 3-stage responses
     */
    public interface ThreeStageCallback {
        void onStageResponse(ResponseStage stage, String response, boolean isFinal);
        void onStageError(ResponseStage stage, String error);
        void onAllStagesComplete(String finalResponse);

        // Optional methods with default implementations
        default void onStageStarted(ResponseStage stage) {
            Log.d(TAG, "🚀 Starting " + stage.getDisplayName());
        }

        default void onPerformanceMetric(ResponseStage stage, long durationMs) {
            Log.d(TAG, "⏱️ " + stage.getDisplayName() + " completed in " + durationMs + "ms");
        }
    }

    /**
     * Private constructor
     */
    private ThreeStageResponseManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();

        // Initialize services
        this.openAIService = OpenAIService.getInstance();
        this.modelManager = FineTunedModelManager.getInstance(context);
        this.ttsService = TTSServiceManager.getOpenAITTSService(context);
        this.contextManager = EnhancedContextManager.getInstance();
        
        // NEW: Initialize Responses API integration
        this.responsesManager = ChessMasterResponsesManager.getInstance(context);
        this.integrationHelper = ResponsesAPIIntegrationHelper.getInstance(context);
        
        Log.d(TAG, "🚀 Enhanced ThreeStageResponseManager initialized with Responses API integration!");
        
        // FORCE: Show integration status on initialization
        Log.d(TAG, "🔍 Integration Status Check:");
        Log.d(TAG, "🎯 Fischer eligible: " + integrationHelper.shouldUseResponsesAPI("fischer"));
        Log.d(TAG, "🎯 Tal eligible: " + integrationHelper.shouldUseResponsesAPI("tal"));
        Log.d(TAG, "🎯 Carlsen eligible: " + integrationHelper.shouldUseResponsesAPI("carlsen"));
    }

    /**
     * Get singleton instance
     */
    public static synchronized ThreeStageResponseManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThreeStageResponseManager(context);
        }
        return instance;
    }

    /**
     * MAIN ENTRY POINT: Process user input through optimized 3 stages
     * DISABLED IN SPECTATOR MODE: SpectatorConversationOrchestrator uses Responses API exclusively
     */
    public void processThreeStageResponse(String userInput, String gameContext, ThreeStageCallback callback) {
        // CRITICAL FIX: Disable ThreeStageResponseManager entirely in spectator mode
        android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        boolean isSpectatorMode = prefs.getBoolean("is_spectator_mode", false);
        
        if (isSpectatorMode) {
            Log.d(TAG, "🎭 BLOCKED: ThreeStageResponseManager disabled in spectator mode - Responses API handles all dialogue");
            if (callback != null) {
                callback.onStageError(ResponseStage.STAGE_1_QUICK, "Disabled in spectator mode - using Responses API");
            }
            return;
        }

        ResponseMode userMode = getUserResponseMode();
        Log.d(TAG, "🎮 RESPONSE MODE: " + userMode + " for user input: '" + userInput.substring(0, Math.min(50, userInput.length())) + "'");
        processThreeStageResponse(userInput, gameContext, userMode, callback);
    }

    /**
     * OPTIMIZED STAGE 1: Lightning-fast response (under 3 seconds) - API 26 COMPATIBLE
     */
    /**
     * OPTIMIZED STAGE 1: Lightning-fast response (under 3 seconds) - API 26 COMPATIBLE
     */
    private void processOptimizedStage1Quick(int responseId, String userInput, String gameContext,
                                             String selectedMaster, ThreeStageCallback callback) {

        final long startTime = System.currentTimeMillis();

        CompletableFuture<String> stage1Task = CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "⚡ OPTIMIZED Stage 1 starting for response #" + responseId);
                callback.onStageStarted(ResponseStage.STAGE_1_QUICK);

                // Get context for quick response
                List<Map<String, String>> context = contextManager.getContextForApiCall(selectedMaster, "");
                
                // Add minimal user message for speed
                Map<String, String> userMsg = new HashMap<>();
                userMsg.put("role", "user");
                userMsg.put("content", userInput);
                context.add(userMsg);
                
                // Keep context minimal for stage 1 (last 5 messages max)
                if (context.size() > 6) { // system + relationship + 4 recent
                    List<Map<String, String>> minimalContext = new ArrayList<>();
                    minimalContext.addAll(context.subList(0, Math.min(2, context.size()))); // Keep system messages
                    if (context.size() > 4) {
                        minimalContext.addAll(context.subList(context.size() - 4, context.size()));
                    }
                    context = minimalContext;
                }

                // NEW: Use Responses API for Stage 1 if available
                String normalizedMasterName = normalizeMasterName(selectedMaster);
                Log.d(TAG, "🔍 DECISION POINT: Checking Responses API for '" + normalizedMasterName + "'");
                boolean shouldUseResponses = integrationHelper.shouldUseResponsesAPI(normalizedMasterName);
                Log.d(TAG, "🎯 DECISION RESULT: shouldUseResponsesAPI='" + shouldUseResponses + "' for '" + normalizedMasterName + "'");
                
                if (shouldUseResponses) {
                    Log.d(TAG, "🚀 USING RESPONSES API for Stage 1 with " + normalizedMasterName);
                    
                    // Handle Responses API asynchronously - don't block this thread
                    executorService.execute(() -> {
                        handleResponsesAPIStage1(normalizedMasterName, userInput, gameContext, responseId, startTime, callback);
                    });
                    
                    // Return placeholder response immediately
                    return "Analyzing position with " + normalizedMasterName + "'s expertise...";
                } else {
                    Log.d(TAG, "⚠️ FALLBACK: Using Chat Completions for " + normalizedMasterName);
                }
                
                // Fallback: Use fast response with context
                String quickResponse = openAIService.getChatCompletionWithEnhancedContext(context, null);

                if (quickResponse == null || quickResponse.trim().isEmpty()) {
                    return getEmergencyQuickResponse(selectedMaster);
                }

                // Add to context manager
                contextManager.addConversationTurn(selectedMaster, quickResponse, "stage1_response");

                Log.d(TAG, "✅ OPTIMIZED Stage 1 success for response #" + responseId);
                return quickResponse.trim();

            } catch (Exception e) {
                Log.e(TAG, "❌ OPTIMIZED Stage 1 error for response #" + responseId, e);
                return getEmergencyQuickResponse(selectedMaster);
            }
        }, executorService);

        // API 26 COMPATIBLE: Custom timeout implementation for Stage 1
        implementCustomTimeout(stage1Task, STAGE_1_TIMEOUT_MS, new TimeoutCallback() {
            @Override
            public void onResult(String result) {
                final long duration = System.currentTimeMillis() - startTime;
                final String finalResult = (result != null) ? result : getEmergencyQuickResponse(selectedMaster);

                // Deliver response on main thread
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                        callback.onStageResponse(ResponseStage.STAGE_1_QUICK, finalResult, false);
                    }

                    // Start TTS for immediate feedback
                    startOptimizedTTS(finalResult, 1, responseId);
                });
            }

            @Override
            public void onTimeout() {
                Log.e(TAG, "⏰ OPTIMIZED Stage 1 timeout for response #" + responseId);
                final String timeoutResult = getEmergencyQuickResponse(selectedMaster);
                final long duration = System.currentTimeMillis() - startTime;

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                        callback.onStageResponse(ResponseStage.STAGE_1_QUICK, timeoutResult, false);
                    }
                    startOptimizedTTS(timeoutResult, 1, responseId);
                });
            }
        });
    }

    /**
     * ENHANCED: Process user input with PARALLEL stage execution for faster responses
     * DISABLED IN SPECTATOR MODE: SpectatorConversationOrchestrator uses Responses API exclusively
     */
    public void processThreeStageResponse(String userInput, String gameContext,
                                          ResponseMode mode, ThreeStageCallback callback) {
        // CRITICAL FIX: Disable ThreeStageResponseManager entirely in spectator mode
        // The Responses API via SpectatorConversationOrchestrator handles all dialogue with proper context
        android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        boolean isSpectatorMode = prefs.getBoolean("is_spectator_mode", false);
        
        if (isSpectatorMode) {
            Log.d(TAG, "🎭 BLOCKED: ThreeStageResponseManager disabled in spectator mode - Responses API handles all dialogue");
            // Don't create Chat Completions calls that lack position context and say "Show me the position"
            if (callback != null) {
                callback.onStageError(ResponseStage.STAGE_1_QUICK, "Disabled in spectator mode - using Responses API");
            }
            return;
        }

        int responseId = currentResponseId.incrementAndGet();
        Log.d(TAG, "🚀 Starting PARALLEL 3-stage response #" + responseId + " in " + mode + " mode");

        // Get current master and normalize name for Responses API
        String selectedMaster = modelManager.getSelectedChessMaster();
        String normalizedMaster = normalizeMasterName(selectedMaster);
        Log.d(TAG, "🎭 MASTER DEBUG: Original='" + selectedMaster + "' → Normalized='" + normalizedMaster + "'");
        Log.d(TAG, "🔍 RESPONSES API CHECK: About to check eligibility for '" + normalizedMaster + "'");

        // Reset speaking flags
        isStage1Speaking = false;
        isStage2Speaking = false;

        // Continue with stages based on mode
        switch (mode) {
            case FAST_ONLY:
                Log.d(TAG, "⚡ Fast-only mode: Stage 1 only");
                processOptimizedStage1Quick(responseId, userInput, gameContext, normalizedMaster, callback);
                return;

            case ENHANCED_ONLY:
                Log.d(TAG, "🎭 Enhanced mode: Stages 1-2");
                processOptimizedStage1Quick(responseId, userInput, gameContext, normalizedMaster, callback);
                processOptimizedStage2Enhanced(responseId, userInput, gameContext, normalizedMaster, callback);
                return;

            case FULL_ANALYSIS:
                Log.d(TAG, "🧠 Full analysis mode: All 3 stages - PARALLEL EXECUTION! 🚀");
                // PARALLEL EXECUTION: Start Stage 1, then both Stage 2 and Stage 3 simultaneously
                processOptimizedStage1Quick(responseId, userInput, gameContext, normalizedMaster, callback);
                processOptimizedStage2Enhanced(responseId, userInput, gameContext, normalizedMaster, callback);
                processOptimizedStage3Deep(responseId, userInput, gameContext, normalizedMaster, callback);
                return;

            case ADAPTIVE:
                if (isSimpleQuestion(userInput)) {
                    Log.d(TAG, "🤖 Adaptive: simple question, using Stage 1 (with Responses API support)");
                    processOptimizedStage1Quick(responseId, userInput, gameContext, normalizedMaster, callback);
                } else if (isChessAnalysisQuestion(userInput, gameContext)) {
                    Log.d(TAG, "🤖 Adaptive: chess analysis, Stages 1-2 with enhanced context");
                    processOptimizedStage1Quick(responseId, userInput, gameContext, normalizedMaster, callback);
                    processOptimizedStage2Enhanced(responseId, userInput, gameContext, normalizedMaster, callback);
                } else {
                    Log.d(TAG, "🤖 Adaptive: complex question, full 3-stage analysis");
                    processOptimizedStage1Quick(responseId, userInput, gameContext, normalizedMaster, callback);
                    processOptimizedStage2Enhanced(responseId, userInput, gameContext, normalizedMaster, callback);
                    processOptimizedStage3Deep(responseId, userInput, gameContext, normalizedMaster, callback);
                }
                return;
        }
    }

    /**
     * NEW: Process with fine-tuned model only (optimized for ElevenLabs speed)
     */
    private void processFineTunedResponse(int responseId, String userInput, String gameContext,
                                         String selectedMaster, ThreeStageCallback callback) {
        final long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> ftTask = CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "⚡ FT-only response starting for response #" + responseId);
                callback.onStageStarted(ResponseStage.STAGE_1_QUICK);
                
                String fineTunedModelId = modelManager.getSelectedModelId();
                String systemPrompt = createOptimizedFineTunedPrompt(selectedMaster);
                String userMessage = createOptimizedUserMessage(userInput, gameContext);
                
                String response;
                if (fineTunedModelId.startsWith("ft:")) {
                    Log.d(TAG, "✨ Using fine-tuned model: " + fineTunedModelId);
                    response = openAIService.getChatCompletionWithModel(fineTunedModelId, systemPrompt, userMessage);
                } else {
                    response = openAIService.getChatCompletion(systemPrompt, userMessage);
                }
                
                if (response == null || response.trim().isEmpty()) {
                    throw new Exception("Empty FT response");
                }
                
                String cleanedResponse = cleanMetaLanguage(response, selectedMaster);
                Log.d(TAG, "✅ FT-only response success for response #" + responseId);
                return cleanedResponse;
                
            } catch (Exception e) {
                Log.e(TAG, "❌ FT-only response error for response #" + responseId, e);
                return getEmergencyQuickResponse(selectedMaster);
            }
        }, executorService);
        
        implementCustomTimeout(ftTask, STAGE_1_TIMEOUT_MS, new TimeoutCallback() {
            @Override
            public void onResult(String result) {
                final long duration = System.currentTimeMillis() - startTime;
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                        callback.onStageResponse(ResponseStage.STAGE_1_QUICK, result, true);
                        callback.onAllStagesComplete(result);
                    }
                    startOptimizedTTS(result, 1, responseId);
                });
            }
            
            @Override
            public void onTimeout() {
                Log.e(TAG, "⏰ FT-only timeout for response #" + responseId);
                final String timeoutResult = getEmergencyQuickResponse(selectedMaster);
                final long duration = System.currentTimeMillis() - startTime;
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                        callback.onStageResponse(ResponseStage.STAGE_1_QUICK, timeoutResult, false);
                        callback.onAllStagesComplete(timeoutResult);
                    }
                    startOptimizedTTS(timeoutResult, 1, responseId);
                });
            }
        });
    }
    
    /**
     * NEW: Process with assistant (renamed from Stage 3)
     */
    private void processAssistantResponse(int responseId, String userInput, String gameContext,
                                        String selectedMaster, ThreeStageCallback callback) {
        final long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> assistantTask = CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "🧠 Assistant response starting for response #" + responseId);
                callback.onStageStarted(ResponseStage.STAGE_2_ENHANCED);
                
                String assistantResponse = modelManager.processWithMasterAssistant(userInput, gameContext, selectedMaster);
                
                if (assistantResponse == null || assistantResponse.trim().isEmpty()) {
                    throw new Exception("Empty assistant response");
                }
                
                String cleanedResponse = cleanMetaLanguage(assistantResponse, selectedMaster);
                Log.d(TAG, "✅ Assistant response success for response #" + responseId);
                return cleanedResponse;
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Assistant response error for response #" + responseId, e);
                return getDefaultDeepResponse(selectedMaster);
            }
        }, executorService);
        
        implementCustomTimeout(assistantTask, STAGE_2_TIMEOUT_MS, new TimeoutCallback() {
            @Override
            public void onResult(String result) {
                final long duration = System.currentTimeMillis() - startTime;
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_2_ENHANCED, duration);
                        callback.onStageResponse(ResponseStage.STAGE_2_ENHANCED, result, true);
                        callback.onAllStagesComplete(result);
                    }
                    interruptPreviousTTSAndStart(result, 2, responseId);
                });
            }
            
            @Override
            public void onTimeout() {
                Log.e(TAG, "⏰ Assistant timeout for response #" + responseId);
                final String timeoutResult = getDefaultDeepResponse(selectedMaster);
                final long duration = System.currentTimeMillis() - startTime;
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_2_ENHANCED, duration);
                        callback.onStageResponse(ResponseStage.STAGE_2_ENHANCED, timeoutResult, false);
                        callback.onAllStagesComplete(timeoutResult);
                    }
                    interruptPreviousTTSAndStart(timeoutResult, 2, responseId);
                });
            }
        });
    }
    
    /**
     * NEW: Process with assistant + vector/DB lookup for chess analysis
     */
    private void processAssistantWithLookup(int responseId, String userInput, String gameContext,
                                          String selectedMaster, ThreeStageCallback callback) {
        final long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> lookupTask = CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "🔍 Assistant+DB response starting for response #" + responseId);
                callback.onStageStarted(ResponseStage.STAGE_3_DEEP);
                
                // Enhanced context with DB lookup
                String enhancedContext = gameContext;
                if (gameContext != null && gameContext.contains("POSITION:")) {
                    // Add historical position data from local DB
                    enhancedContext += "\n" + getHistoricalPositionContext(gameContext, selectedMaster);
                }
                
                String assistantResponse = modelManager.processWithMasterAssistant(userInput, enhancedContext, selectedMaster);
                
                if (assistantResponse == null || assistantResponse.trim().isEmpty()) {
                    throw new Exception("Empty assistant+lookup response");
                }
                
                String cleanedResponse = cleanMetaLanguage(assistantResponse, selectedMaster);
                Log.d(TAG, "✅ Assistant+DB response success for response #" + responseId);
                return cleanedResponse;
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Assistant+DB response error for response #" + responseId, e);
                return getDefaultDeepResponse(selectedMaster);
            }
        }, executorService);
        
        implementCustomTimeout(lookupTask, STAGE_3_TIMEOUT_MS, new TimeoutCallback() {
            @Override
            public void onResult(String result) {
                final long duration = System.currentTimeMillis() - startTime;
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_3_DEEP, duration);
                        callback.onStageResponse(ResponseStage.STAGE_3_DEEP, result, true);
                        callback.onAllStagesComplete(result);
                    }
                    if (isSignificantlyDifferent(result)) {
                        interruptPreviousTTSAndStart(result, 3, responseId);
                    }
                });
            }
            
            @Override
            public void onTimeout() {
                Log.e(TAG, "⏰ Assistant+DB timeout for response #" + responseId);
                final String timeoutResult = getDefaultDeepResponse(selectedMaster);
                final long duration = System.currentTimeMillis() - startTime;
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_3_DEEP, duration);
                        callback.onStageResponse(ResponseStage.STAGE_3_DEEP, timeoutResult, false);
                        callback.onAllStagesComplete(timeoutResult);
                    }
                    interruptPreviousTTSAndStart(timeoutResult, 3, responseId);
                });
            }
        });
    }
    
    /**
     * NEW: Detect chess analysis questions
     */
    private boolean isChessAnalysisQuestion(String userInput, String gameContext) {
        if (gameContext != null && gameContext.contains("POSITION:") && !gameContext.contains("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR")) {
            return true; // Non-starting position
        }
        
        String input = userInput.toLowerCase();
        return input.contains("position") || input.contains("move") || input.contains("analyze") || 
               input.contains("evaluation") || input.contains("tactic") || input.contains("strategy") ||
               input.contains("what should i play") || input.contains("best move");
    }
    
    /**
     * NEW: Get historical position context from local DB
     */
    private String getHistoricalPositionContext(String gameContext, String selectedMaster) {
        // This will hook into your existing personality engine DB lookup
        // For now, return empty - you can enhance this with actual DB queries
        return "";
    }

    /**
     * ENHANCED: Better meta-language cleaning with more patterns
     */
    private String cleanMetaLanguage(String response, String master) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }

        String cleaned = response.trim();

        // Remove common meta-language patterns (case-insensitive)
        cleaned = cleaned.replaceAll("(?i)^Let me analyze.*?\\.", "");
        cleaned = cleaned.replaceAll("(?i)^As your.*?assistant,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^I (would\\s+)?(recommend|suggest|advise)", "In my experience, I");
        cleaned = cleaned.replaceAll("(?i)You should", "I would");
        cleaned = cleaned.replaceAll("(?i)^Analyzing this position,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^Looking at this,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^Interesting position\\.?\\s*", "");

        // ENHANCED: Additional meta-commentary patterns
        cleaned = cleaned.replaceAll("(?i)^In analyzing this.*?,\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^When I examine this.*?,\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^From a strategic standpoint,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^From my analysis,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^Based on my evaluation,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^In my assessment,?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)I believe that\\s*", "");
        cleaned = cleaned.replaceAll("(?i)It appears that\\s*", "");
        cleaned = cleaned.replaceAll("(?i)It seems that\\s*", "");
        cleaned = cleaned.replaceAll("(?i)I think that\\s*", "");
        cleaned = cleaned.replaceAll("(?i)I would say that\\s*", "");
        cleaned = cleaned.replaceAll("(?i)In this position,?\\s*", "");

        // Clean up any double spaces or awkward starts
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        // DISABLED: Auto-opener addition was corrupting biographical responses
        // Enhanced natural opening replacement for Tal
        // if (cleaned.length() > 10 &&
        //         !cleaned.toLowerCase().matches("^(you know|when i|in my|during my|against \\w+|i remember|this reminds me).*")) {
        //
        //     String[] talOpeners = {
        //             "You know, ",
        //             "In my experience, ",
        //             "I've always believed that ",
        //             "This reminds me of when ",
        //             "During my career, I learned that ",
        //             "Against strong opponents, I found that ",
        //             "In positions like this, "
        //     };
        //
        //     String opener = talOpeners[(int)(Math.random() * talOpeners.length)];
        //
        //     // Ensure first letter is lowercase for natural flow
        //     if (cleaned.length() > 0) {
        //         cleaned = opener + Character.toLowerCase(cleaned.charAt(0)) + cleaned.substring(1);
        //     }
        // }

        Log.d(TAG, "🧹 Enhanced meta-language cleaning applied to " + master);
        return cleaned.trim();
    }

    /**
     * ENHANCED: Stage 2 with better response tracking for parallel execution
     */
    private void processOptimizedStage2Enhanced(int responseId, String userInput, String gameContext,
                                                String selectedMaster, ThreeStageCallback callback) {

        final long startTime = System.currentTimeMillis();
        final String stageId = "Stage2-" + responseId;

        CompletableFuture<String> stage2Task = CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "🎭 PARALLEL Stage 2 starting for response #" + responseId);
                callback.onStageStarted(ResponseStage.STAGE_2_ENHANCED);

                // Get the fine-tuned model ID
                String fineTunedModelId = modelManager.getSelectedModelId();

                // Create ANTI-META prompts for fine-tuned model
                String fineTunedSystemPrompt = createOptimizedFineTunedPrompt(selectedMaster);
                String contextualUserMessage = createOptimizedUserMessage(userInput, gameContext);

                String enhancedResponse;

                // Use fine-tuned model if available
                if (fineTunedModelId.startsWith("ft:")) {
                    Log.d(TAG, "✨ Using fine-tuned model: " + fineTunedModelId);
                    enhancedResponse = openAIService.getChatCompletionWithModel(
                            fineTunedModelId,
                            fineTunedSystemPrompt,
                            contextualUserMessage
                    );
                } else {
                    Log.d(TAG, "🔄 Using enhanced prompting fallback");
                    String enhancedSystemPrompt = modelManager.getEnhancedSystemPromptForSelectedMaster();
                    enhancedResponse = openAIService.getChatCompletion(enhancedSystemPrompt, contextualUserMessage);
                }

                // 🧹 ENHANCED: Better meta-language cleaning
                String cleanedResponse = cleanMetaLanguage(enhancedResponse, selectedMaster);
                Log.d(TAG, "🧹 Enhanced meta-language cleaning completed for Stage 2");

                // Enrich with personality touches (after cleaning)
                String enrichedResponse = modelManager.enrichResponseWithPersonality(cleanedResponse, selectedMaster);

                Log.d(TAG, "✅ PARALLEL Stage 2 success for response #" + responseId);
                return enrichedResponse;

            } catch (Exception e) {
                Log.e(TAG, "❌ PARALLEL Stage 2 error for response #" + responseId, e);
                return getPersonalityErrorMessage(selectedMaster);
            }
        }, executorService);

        // API 26 COMPATIBLE: Custom timeout implementation
        implementCustomTimeout(stage2Task, STAGE_2_TIMEOUT_MS, new TimeoutCallback() {
            @Override
            public void onResult(String result) {
                final long duration = System.currentTimeMillis() - startTime;
                final String finalResult = (result != null) ? result : getPersonalityErrorMessage(selectedMaster);

                // Deliver response on main thread
                mainHandler.post(() -> {
                    Log.d(TAG, "🎭 PARALLEL Stage 2 completed in " + duration + "ms");
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_2_ENHANCED, duration);
                        callback.onStageResponse(ResponseStage.STAGE_2_ENHANCED, finalResult, false);
                    }

                    // Interrupt previous TTS and start Stage 2 TTS
                    interruptPreviousTTSAndStart(finalResult, 2, responseId);
                });
            }

            @Override
            public void onTimeout() {
                Log.e(TAG, "⏰ PARALLEL Stage 2 timeout for response #" + responseId);
                final String timeoutResult = getPersonalityErrorMessage(selectedMaster);
                final long duration = System.currentTimeMillis() - startTime;

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_2_ENHANCED, duration);
                        callback.onStageResponse(ResponseStage.STAGE_2_ENHANCED, timeoutResult, false);
                    }
                    interruptPreviousTTSAndStart(timeoutResult, 2, responseId);
                });
            }
        });
    }

    /**
     * ENHANCED: Stage 3 with parallel execution support
     */
    private void processOptimizedStage3Deep(int responseId, String userInput, String gameContext,
                                            String selectedMaster, ThreeStageCallback callback) {

        final long startTime = System.currentTimeMillis();
        final String stageId = "Stage3-" + responseId;

        CompletableFuture<String> stage3Task = CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "🧠 PARALLEL Stage 3 starting for response #" + responseId);
                callback.onStageStarted(ResponseStage.STAGE_3_DEEP);

                // Use your existing assistant processing
                String deepResponse = modelManager.processWithMasterAssistant(userInput, gameContext, selectedMaster);

                if (deepResponse == null || deepResponse.trim().isEmpty()) {
                    throw new Exception("Empty response from assistant");
                }

                // 🧹 ENHANCED: Better meta-language cleaning for Stage 3 too
                String cleanedResponse = cleanMetaLanguage(deepResponse, selectedMaster);
                Log.d(TAG, "🧹 Enhanced meta-language cleaning completed for Stage 3");

                // Add personality flourish (after cleaning)
                String enhancedResponse = addPersonalityFlourish(cleanedResponse, selectedMaster);

                Log.d(TAG, "✅ PARALLEL Stage 3 success for response #" + responseId);
                return enhancedResponse;

            } catch (Exception e) {
                Log.e(TAG, "❌ PARALLEL Stage 3 error for response #" + responseId, e);
                String masterName = modelManager.getMasterDisplayName(selectedMaster);
                return String.format("Drawing from my years of experience as %s, let me share some deeper insights about this position...", masterName);
            }
        }, executorService);

        // Custom timeout for Stage 3
        implementCustomTimeout(stage3Task, STAGE_3_TIMEOUT_MS, new TimeoutCallback() {
            @Override
            public void onResult(String result) {
                final long duration = System.currentTimeMillis() - startTime;
                final String finalResult = (result != null) ? result : getDefaultDeepResponse(selectedMaster);

                mainHandler.post(() -> {
                    Log.d(TAG, "🧠 PARALLEL Stage 3 completed in " + duration + "ms");
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_3_DEEP, duration);
                        callback.onStageResponse(ResponseStage.STAGE_3_DEEP, finalResult, true);
                        callback.onAllStagesComplete(finalResult);
                    }

                    // Only interrupt if this response is significantly different and better
                    if (isSignificantlyDifferent(finalResult)) {
                        interruptPreviousTTSAndStart(finalResult, 3, responseId);
                    }
                });
            }

            @Override
            public void onTimeout() {
                Log.e(TAG, "⏰ PARALLEL Stage 3 timeout for response #" + responseId);
                final String timeoutResult = getDefaultDeepResponse(selectedMaster);
                final long duration = System.currentTimeMillis() - startTime;

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_3_DEEP, duration);
                        callback.onStageResponse(ResponseStage.STAGE_3_DEEP, timeoutResult, true);
                        callback.onAllStagesComplete(timeoutResult);
                    }
                    interruptPreviousTTSAndStart(timeoutResult, 3, responseId);
                });
            }
        });
    }

    /**
     * ENHANCED: Anti-meta prompt for fine-tuned model with historically accurate references
     */
    private String createOptimizedFineTunedPrompt(String master) {
        String masterName = modelManager.getMasterDisplayName(master);
        String historicalContext = getHistoricalContextForMaster(master);

        // ENHANCED: Direct instruction to answer the specific question asked
        return "You are " + masterName + " speaking directly to a chess student. " +
                "Answer the specific question they ask about your chess career, games, or experiences. " +
                "If they ask about losses, defeats, or difficult games, share specific examples from your career. " +
                "If they ask about wins or successes, share those instead. " +
                "NEVER give generic chess advice when asked about specific biographical details. " +
                "Instead, share your actual memories: 'I remember my loss against...', 'My worst defeat was...', " +
                "'My greatest victory came against...'. " +
                "Be specific about opponents, tournaments, years, and what happened. " +
                historicalContext + " " +
                "Always directly address what they asked about.";
    }

    /**
     * Get historically accurate context for each chess master
     */
    private String getHistoricalContextForMaster(String master) {
        switch (master.toLowerCase()) {
            case "carlsen":
                return "Reference your career from 2004 onwards, World Champion 2013-2023. Mention opponents like Anand, Caruana, Nepomniachtchi, or your early games against Kramnik.";
            case "kasparov":
                return "Reference your career from 1980-2005, World Champion 1985-2000. Mention your battles with Karpov, Kramnik, Anand, or Deep Blue.";
            case "karpov":
                return "Reference your career from 1970-2009, World Champion 1975-1985. Mention your matches with Kasparov, Korchnoi, or Spassky.";
            case "fischer":
                return "Reference your career from 1955-1972, World Champion 1972-1975. Mention your path through the Candidates, games against Spassky, Petrosian, or Tal.";
            case "tal":
                return "Reference your career from 1953-1992, World Champion 1960-1961. Mention your games against Botvinnik, Petrosian, Fischer, or your attacking style.";
            case "kramnik":
                return "Reference your career from 1992-2019, World Champion 2000-2007. Mention your victory over Kasparov, matches with Topalov, Anand, or your solid style.";
            case "anand":
                return "Reference your career from 1987-2017, World Champion 2007-2013. Mention your matches with Kramnik, Gelfand, Carlsen, or your versatile style.";
            case "alekhine":
                return "Reference your career from 1909-1946, World Champion 1927-1935, 1937-1946. Mention your games against Capablanca, Euwe, or your deep calculations.";
            case "capablanca":
                return "Reference your career from 1904-1939, World Champion 1921-1927. Mention your games against Lasker, Alekhine, Marshall, or your endgame technique.";
            case "lasker":
                return "Reference your career from 1889-1924, World Champion 1894-1921. Mention your games against Steinitz, Capablanca, Tarrasch, or your fighting spirit.";
            case "morphy":
                return "Reference your brief but brilliant career from 1850s-1860s. Mention your games in the 1857 American Chess Congress, your European tour, or your tactical genius.";
            case "botvinnik":
                return "Reference your career from 1927-1970, World Champion 1948-1957, 1958-1960, 1961-1963. Mention your matches with Smyslov, Tal, Petrosian, or your scientific approach.";
            default:
                return "Reference specific games, tournaments, or opponents from your actual career.";
        }
    }

    /**
     * ENHANCED: Context message that reinforces persona and handles different question types
     */
    private String createOptimizedUserMessage(String userInput, String gameContext) {
        StringBuilder message = new StringBuilder();

        // Add position context only if provided (for position-related questions)
        if (gameContext != null && !gameContext.trim().isEmpty()) {
            message.append("Looking at this position: ");

            String[] contextLines = gameContext.split("\n");
            for (String line : contextLines) {
                if (line.contains("POSITION:")) {
                    String fen = line.replace("POSITION:", "").trim();
                    message.append("FEN: ").append(fen).append("\n");
                }
                if (line.contains("RECENT_MOVES:")) {
                    String moves = line.replace("RECENT_MOVES:", "").trim();
                    message.append("Recent moves: ").append(moves).append("\n");
                }
            }
            message.append("\nWhat are your thoughts? What does this position remind you of from your career?");
        } else {
            // For biographical/general questions, pass the question directly without position framing
            message.append(userInput);
        }

        return message.toString();
    }

    /**
     * API 26 COMPATIBLE: Custom timeout implementation (replaces orTimeout)
     */
    private interface TimeoutCallback {
        void onResult(String result);
        void onTimeout();
    }

    private void implementCustomTimeout(CompletableFuture<String> future, long timeoutMs, TimeoutCallback callback) {
        // Use Handler to implement timeout (works on API 26+)
        mainHandler.postDelayed(() -> {
            if (!future.isDone()) {
                Log.d(TAG, "⏰ Custom timeout triggered after " + timeoutMs + "ms");
                future.cancel(true);
                callback.onTimeout();
            }
        }, timeoutMs);

        // Handle successful completion
        future.whenComplete((result, throwable) -> {
            if (!future.isCancelled()) {
                if (throwable == null) {
                    callback.onResult(result);
                } else {
                    Log.e(TAG, "CompletableFuture completed with error", throwable);
                    callback.onTimeout(); // Treat errors as timeouts for simplicity
                }
            }
        });
    }

    /**
     * OPTIMIZED: Lightning-quick prompt for Stage 1 (minimal context)
     */
    private String createLightningQuickPrompt(String master) {
        String masterName = modelManager.getMasterDisplayName(master);

        // ULTRA-SIMPLE prompt for maximum speed
        return "You are " + masterName + ". Give a brief, encouraging first response (1-2 sentences) to help this chess student.";
    }

    /**
     * OPTIMIZED: Simple user message for Stage 1 (no context overload)
     */
    private String createSimpleUserMessage(String userInput) {
        // Just the question - no context to slow things down
        return userInput;
    }

    /**
     * ENHANCED: Smart TTS management with precise state tracking
     */
    private void startOptimizedTTS(String text, int stage, int responseId) {
        Log.d(TAG, "🎵 Starting optimized TTS for stage " + stage + ", response #" + responseId);

        if (stage == 1) {
            Log.d(TAG, "🎤 Stage 1 TTS starting - will play for minimum duration");

            // Don't mark as speaking until we actually start
            ttsService.speak(text, new OpenAITTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    isStage1Speaking = false;
                    Log.d(TAG, "🎵 Stage 1 TTS completed naturally for response #" + responseId);
                }
            });

            // Mark as speaking after a small delay to ensure audio actually starts
            mainHandler.postDelayed(() -> {
                isStage1Speaking = true;
                Log.d(TAG, "✅ Stage 1 TTS confirmed playing");
            }, 200); // Small delay to ensure playback actually starts
        }
    }

    /**
     * ADAPTIVE: Intelligent TTS coordination based on actual audio length
     */
    private void interruptPreviousTTSAndStart(String text, int stage, int responseId) {
        Log.d(TAG, "🔄 Adaptive TTS transition to stage " + stage + " for response #" + responseId);
        Log.d(TAG, "🔍 Stage states - Stage1Speaking: " + isStage1Speaking + ", Stage2Speaking: " + isStage2Speaking);

        if (stage == 2) {
            // With ElevenLabs' ultra-low latency, allow minimal Stage 1 play time
            if (isStage1Speaking) {
                // Optimized delay for ElevenLabs - ultra-fast transitions
                int estimatedRemainingSeconds = Math.max(1, getCurrentAudioRemainingTime());

                Log.d(TAG, "⏳ Stage 1 playing - allowing " + estimatedRemainingSeconds + " seconds before Stage 2 (ElevenLabs optimized)");

                mainHandler.postDelayed(() -> {
                    performTTSTransition(text, stage, responseId);
                }, estimatedRemainingSeconds * 1000);
                return;
            }
        }

        if (stage == 3) {
            // For Stage 3, be more selective about interrupting
            if (isStage2Speaking) {
                // Optimized timing for ElevenLabs - much faster transitions
                int minStage2PlayTime = Math.max(2000, text.length() * 8); // Optimized for ElevenLabs speed

                Log.d(TAG, "⏳ Stage 2 playing - ensuring " + (minStage2PlayTime/1000) + " seconds minimum (ElevenLabs optimized)");

                mainHandler.postDelayed(() -> {
                    // Additional check: only interrupt if Stage 3 offers significantly more value
                    if (isStage3SignificantlyBetter(text)) {
                        performTTSTransition(text, stage, responseId);
                    } else {
                        Log.d(TAG, "🎵 Stage 2 is sufficient - allowing it to complete naturally");
                    }
                }, minStage2PlayTime);
                return;
            }
        }

        // Immediate transition for other cases
        performTTSTransition(text, stage, responseId);
    }

    /**
     * SMART: Determine if Stage 3 offers enough additional value to interrupt
     */
    private boolean isStage3SignificantlyBetter(String stage3Text) {
        // ENHANCED: More generous criteria for Fischer's deep analysis
        return stage3Text.length() > 150 &&
                (stage3Text.toLowerCase().contains("game") ||
                        stage3Text.toLowerCase().contains("remember") ||
                        stage3Text.toLowerCase().contains("facing") ||
                        stage3Text.toLowerCase().contains("tournament") ||
                        stage3Text.toLowerCase().contains("preparation") ||  // Fischer-specific!
                        stage3Text.toLowerCase().contains("precision") ||   // Fischer-specific!
                        stage3Text.toLowerCase().contains("analysis") ||
                        stage3Text.toLowerCase().contains("database") ||
                        stage3Text.toLowerCase().contains("archive"));
    }



    /**
     * HELPER: Estimate remaining audio time - optimized for ElevenLabs' low latency
     */
    private int getCurrentAudioRemainingTime() {
        // ElevenLabs optimized - much shorter minimum time needed
        return 1; // Reduced from 3 to 1 second for ElevenLabs
    }

    /**
     * ENHANCED: Perform the actual TTS transition with better state management
     */
    private void performTTSTransition(String text, int stage, int responseId) {
        Log.d(TAG, "🎵 Performing TTS transition to stage " + stage);

        // Only interrupt if there's actually something playing
        boolean shouldInterrupt = (stage == 2 && isStage1Speaking) ||
                (stage == 3 && (isStage1Speaking || isStage2Speaking));

        if (shouldInterrupt) {
            Log.d(TAG, "🛑 Interrupting previous stage for stage " + stage);
            if (ttsService != null) {
                ttsService.interrupt();
            }
        }

        // Small delay to ensure clean interruption
        mainHandler.postDelayed(() -> {
            if (stage == 2) {
                isStage1Speaking = false; // Mark Stage 1 as done
                isStage2Speaking = true;
                Log.d(TAG, "🎭 Starting Stage 2 TTS for response #" + responseId);
                ttsService.speak(text, new OpenAITTSService.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        isStage2Speaking = false;
                        Log.d(TAG, "🎵 Stage 2 TTS completed for response #" + responseId);
                    }
                });
            } else if (stage == 3) {
                isStage2Speaking = false; // Mark Stage 2 as done
                Log.d(TAG, "🧠 Starting Stage 3 TTS for response #" + responseId);
                ttsService.speak(text, new OpenAITTSService.OnSpeechCompletedListener() {
                    @Override
                    public void onSpeechCompleted() {
                        Log.d(TAG, "🎵 Stage 3 TTS completed for response #" + responseId);
                    }
                });
            }
        }, shouldInterrupt ? 150 : 50); // Longer delay if we interrupted something
    }

    /**
     * Emergency quick responses for when Stage 1 fails
     */
    private String getEmergencyQuickResponse(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "Ah, let me look at this position! Give me just a moment to analyze...";
            case "fischer":
                return "Interesting position. Let me calculate the best moves here.";
            case "kasparov":
                return "This position has potential! Let me examine the possibilities.";
            case "kramnik":
                return "I see the strategic elements here. Allow me to evaluate this carefully.";
            default:
                return "Let me analyze this chess position for you...";
        }
    }

    /**
     * Get personality-specific error messages
     */
    private String getPersonalityErrorMessage(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "The position is complex, but that's what makes chess beautiful! Let me share my thoughts...";
            case "fischer":
                return "This requires precise calculation. Let me work through this systematically.";
            case "kasparov":
                return "Every position has hidden resources. Let me find them for you!";
            case "kramnik":
                return "Patience is key in chess analysis. Let me examine this methodically.";
            default:
                return "Chess positions often reveal their secrets with careful study. Let me help...";
        }
    }

    /**
     * Get default deep response for Stage 3
     */
    private String getDefaultDeepResponse(String master) {
        String masterName = modelManager.getMasterDisplayName(master);
        return String.format("Drawing from my years of experience as %s, this position contains fascinating strategic and tactical elements that deserve careful study. Let me share the deeper insights I've gained through decades of chess mastery.", masterName);
    }

    /**
     * Add personality-specific flourishes
     */
    private String addPersonalityFlourish(String response, String master) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }

        StringBuilder enhanced = new StringBuilder(response);

        switch (master.toLowerCase()) {
            case "tal":
                if (!response.toLowerCase().contains("beautiful") && !response.toLowerCase().contains("sacrifice")) {
                    enhanced.append(" This position has the kind of hidden beauty that makes chess truly magical!");
                }
                break;
            case "alekhine":
                enhanced.append(" Such positions require the deep calculation that separates masters from amateurs.");
                break;
            case "kramnik":
                enhanced.append(" This demonstrates the importance of methodical analysis in modern chess.");
                break;
            case "fischer":
                enhanced.append(" Only precise moves will achieve the objective truth of this position.");
                break;
        }

        return enhanced.toString();
    }

    /**
     * Check if responses are significantly different (FIXED: simplified signature)
     */
    private boolean isSignificantlyDifferent(String response) {
        // Simple heuristic - in a real implementation you'd track actual previous responses
        return response != null && response.length() > 100;
    }

    /**
     * Intelligent question classification
     */
    private boolean isSimpleQuestion(String userInput) {
        String input = userInput.toLowerCase();

        String[] simplePatterns = {
                "what is", "who is", "when did", "where was", "how to",
                "tell me about", "explain", "what does", "why do"
        };

        for (String pattern : simplePatterns) {
            if (input.startsWith(pattern)) {
                return true;
            }
        }

        return userInput.length() < 50;
    }

    /**
     * User preference methods
     */
    public void setUserResponsePreference(ResponseMode mode) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("response_mode", mode.name()).apply();
        Log.d(TAG, "✅ User response mode set to: " + mode);
    }

    private ResponseMode getUserResponseMode() {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String modeName = prefs.getString("response_mode", ResponseMode.ADAPTIVE.name());

        try {
            return ResponseMode.valueOf(modeName);
        } catch (IllegalArgumentException e) {
            return ResponseMode.ADAPTIVE;
        }
    }

    /**
     * Interrupt current processing
     */
    public void interruptCurrentResponse() {
        int currentId = currentResponseId.get();
        Log.d(TAG, "🛑 Interrupting response #" + currentId);

        // Stop TTS
        if (ttsService != null) {
            ttsService.interrupt();
        }

        // Reset flags
        isStage1Speaking = false;
        isStage2Speaking = false;

        // Increment response ID to effectively cancel callbacks for current response
        currentResponseId.incrementAndGet();
    }

    /**
     * Utility methods
     */
    public boolean isReady() {
        return openAIService != null && openAIService.hasApiKey() &&
                modelManager != null && ttsService != null;
    }

    public ResponseMode[] getAvailableResponseModes() {
        return ResponseMode.values();
    }

    public ResponseMode getCurrentResponseMode() {
        return getUserResponseMode();
    }
    
    /**
     * NEW: Handle Responses API Stage 1 asynchronously
     */
    private void handleResponsesAPIStage1(String masterName, String userInput, String gameContext,
                                        int responseId, long startTime, ThreeStageCallback callback) {
        try {
            Log.d(TAG, "🚀 Async Responses API Stage 1 with " + masterName);
            
            // Build context message with game information
            String contextualMessage = buildContextualMessage(userInput, gameContext);
            
            // Create session first, then send message in the callback (like spectator mode)
            responsesManager.createResponseSession(masterName, "main_game", 
                new ChessMasterResponsesManager.ResponseCallback() {
                    
                    @Override
                    public void onResponseStart(String sessionId) {
                        Log.d(TAG, "✅ Main game session created for " + masterName + ": " + sessionId);
                        
                        // Now that session is created, send the message
                        responsesManager.sendMessage(sessionId, contextualMessage, gameContext,
                            new ChessMasterResponsesManager.ResponseCallback() {
                                private StringBuilder response = new StringBuilder();
                                
                                @Override
                                public void onResponseStart(String sessionId) {
                                    Log.d(TAG, "📡 Responses API message processing started: " + sessionId);
                                }
                                
                                @Override
                                public void onResponseChunk(String chunk, boolean isFirst) {
                                    // FIXED: Send only individual chunks to TTS, don't accumulate
                                    if (isFirst) {
                                        response.append(chunk);
                                        String partialResponse = response.toString().trim();
                                        
                                        // Only process if we have meaningful content
                                        if (partialResponse.length() > 10) {
                                            Log.d(TAG, "⚡ First Responses API chunk: " + partialResponse.substring(0, Math.min(50, partialResponse.length())) + "...");
                                            
                                            // Add to context manager
                                            contextManager.addConversationTurn(masterName, partialResponse, "stage1_responses_api");
                                            
                                            final long duration = System.currentTimeMillis() - startTime;
                                            mainHandler.post(() -> {
                                                if (callback != null) {
                                                    callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                                                    callback.onStageResponse(ResponseStage.STAGE_1_QUICK, partialResponse, false);
                                                }
                                                // FIXED: Send only the first chunk to TTS, then stop
                                                startOptimizedTTS(partialResponse, 1, responseId);
                                            });
                                            
                                            // CRITICAL: Clear and stop processing to prevent progressive accumulation
                                            response.setLength(0);
                                            return;
                                        }
                                    }
                                    // For subsequent chunks after first, ignore them for Stage 1 (speed optimization)
                                    Log.d(TAG, "🔇 Ignoring subsequent chunk for Stage 1: " + chunk.substring(0, Math.min(20, chunk.length())) + "...");
                                }
                                
                                @Override
                                public void onResponseComplete(String fullResponse) {
                                    // Only use if we haven't already delivered a chunk response
                                    if (response.length() <= 20 && fullResponse != null && !fullResponse.trim().isEmpty()) {
                                        String finalResponse = fullResponse.trim();
                                        Log.d(TAG, "✅ Complete Responses API response: " + finalResponse.substring(0, Math.min(50, finalResponse.length())) + "...");
                                        
                                        // Add to context manager
                                        contextManager.addConversationTurn(masterName, finalResponse, "stage1_responses_api");
                                        
                                        final long duration = System.currentTimeMillis() - startTime;
                                        mainHandler.post(() -> {
                                            if (callback != null) {
                                                callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                                                callback.onStageResponse(ResponseStage.STAGE_1_QUICK, finalResponse, false);
                                            }
                                            startOptimizedTTS(finalResponse, 1, responseId);
                                        });
                                    }
                                }
                                
                                @Override
                                public void onConversationTurn(String speaker, String message) {
                                    // Track conversation turns
                                }
                                
                                @Override
                                public void onError(String error) {
                                    Log.w(TAG, "⚠️ Responses API message error: " + error);
                                    handleResponsesAPIFallback(masterName, userInput, gameContext, responseId, startTime, callback);
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
                        handleResponsesAPIFallback(masterName, userInput, gameContext, responseId, startTime, callback);
                    }
                });
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in async Responses API Stage 1: " + e.getMessage(), e);
            handleResponsesAPIFallback(masterName, userInput, gameContext, responseId, startTime, callback);
        }
    }
    
    /**
     * Handle fallback when Responses API fails
     */
    private void handleResponsesAPIFallback(String masterName, String userInput, String gameContext,
                                          int responseId, long startTime, ThreeStageCallback callback) {
        Log.d(TAG, "🔄 Falling back to Chat Completions for " + masterName);
        
        executorService.execute(() -> {
            try {
                // Get context for fallback
                List<Map<String, String>> context = contextManager.getContextForApiCall(masterName, "");
                
                // Add minimal user message
                Map<String, String> userMsg = new HashMap<>();
                userMsg.put("role", "user");
                userMsg.put("content", userInput);
                context.add(userMsg);
                
                // Use Chat Completions as fallback
                String fallbackResponse = openAIService.getChatCompletionWithEnhancedContext(context, null);
                
                if (fallbackResponse == null || fallbackResponse.trim().isEmpty()) {
                    fallbackResponse = getEmergencyQuickResponse(masterName);
                }
                
                // Add to context manager
                contextManager.addConversationTurn(masterName, fallbackResponse, "stage1_fallback");
                
                final String finalResponse = fallbackResponse.trim();
                final long duration = System.currentTimeMillis() - startTime;
                
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                        callback.onStageResponse(ResponseStage.STAGE_1_QUICK, finalResponse, false);
                    }
                    startOptimizedTTS(finalResponse, 1, responseId);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Fallback also failed", e);
                final String errorResponse = getEmergencyQuickResponse(masterName);
                final long duration = System.currentTimeMillis() - startTime;
                
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onPerformanceMetric(ResponseStage.STAGE_1_QUICK, duration);
                        callback.onStageResponse(ResponseStage.STAGE_1_QUICK, errorResponse, false);
                    }
                    startOptimizedTTS(errorResponse, 1, responseId);
                });
            }
        });
    }
    
    /**
     * OLD: Process Stage 1 using Responses API for enhanced performance and state management
     * DEPRECATED: This method is no longer used, replaced by handleResponsesAPIStage1
     */
    private String processStage1WithResponsesAPI(String masterName, String userInput, String gameContext) {
        try {
            Log.d(TAG, "🚀 Using Responses API for Stage 1 with " + masterName);
            
            // Build context message with game information
            String contextualMessage = buildContextualMessage(userInput, gameContext);
            
            // Use a CompletableFuture to make this synchronous but with timeout
            CompletableFuture<String> responseFuture = new CompletableFuture<>();
            
            // FIXED: Create session first, then send message in the callback (like spectator mode)
            responsesManager.createResponseSession(masterName, "main_game", 
                new ChessMasterResponsesManager.ResponseCallback() {
                    
                    @Override
                    public void onResponseStart(String sessionId) {
                        Log.d(TAG, "✅ Main game session created for " + masterName + ": " + sessionId);
                        
                        // Now that session is created, send the message
                        responsesManager.sendMessage(sessionId, contextualMessage, gameContext,
                            new ChessMasterResponsesManager.ResponseCallback() {
                                private StringBuilder response = new StringBuilder();
                                
                                @Override
                                public void onResponseStart(String sessionId) {
                                    Log.d(TAG, "📡 Responses API message processing started: " + sessionId);
                                }
                                
                                @Override
                                public void onResponseChunk(String chunk, boolean isFirst) {
                                    response.append(chunk);
                                    // For Stage 1, we want immediate response, so complete on first substantial chunk
                                    if (response.length() > 20 && !responseFuture.isDone()) {
                                        responseFuture.complete(response.toString());
                                    }
                                }
                                
                                @Override
                                public void onResponseComplete(String fullResponse) {
                                    if (!responseFuture.isDone()) {
                                        responseFuture.complete(fullResponse);
                                    }
                                }
                                
                                @Override
                                public void onConversationTurn(String speaker, String message) {
                                    // Add to context manager for next turn
                                    contextManager.addConversationTurn(speaker, message, "responses_api_stage1");
                                }
                                
                                @Override
                                public void onError(String error) {
                                    Log.w(TAG, "⚠️ Responses API message error: " + error);
                                    if (!responseFuture.isDone()) {
                                        responseFuture.complete(getEmergencyQuickResponse(masterName));
                                    }
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
                        if (!responseFuture.isDone()) {
                            responseFuture.complete(getEmergencyQuickResponse(masterName));
                        }
                    }
                });
            
            // Wait for response with timeout (synchronous for this method)
            String response = responseFuture.get(STAGE_1_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            if (response != null && !response.trim().isEmpty()) {
                Log.d(TAG, "✅ Responses API Stage 1 success: " + response.substring(0, Math.min(50, response.length())) + "...");
                return response.trim();
            } else {
                Log.w(TAG, "⚠️ Empty response from Responses API, using fallback");
                return getEmergencyQuickResponse(masterName);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in Responses API Stage 1: " + e.getMessage(), e);
            // Fallback to emergency response
            return getEmergencyQuickResponse(masterName);
        }
    }
    
    /**
     * Get or create a session for the master, ensuring state continuity
     */
    private String getOrCreateSession(String masterName) {
        String sessionId = activeSessions.get(masterName);
        if (sessionId == null) {
            sessionId = "main_game_" + masterName + "_" + System.currentTimeMillis();
            activeSessions.put(masterName, sessionId);
            
            // Create the session asynchronously
            responsesManager.createResponseSession(masterName, "main_game", 
                new ChessMasterResponsesManager.ResponseCallback() {
                    @Override
                    public void onResponseStart(String sessionId) {
                        Log.d(TAG, "✅ Main game session created for " + masterName + ": " + sessionId);
                    }
                    
                    @Override
                    public void onResponseChunk(String chunk, boolean isFirst) {}
                    @Override
                    public void onResponseComplete(String fullResponse) {}
                    @Override
                    public void onConversationTurn(String speaker, String message) {}
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Failed to create session for " + masterName + ": " + error);
                    }
                });
        }
        return sessionId;
    }
    
    /**
     * Build contextual message with game state for better responses
     */
    private String buildContextualMessage(String userInput, String gameContext) {
        StringBuilder message = new StringBuilder();
        
        if (gameContext != null && !gameContext.trim().isEmpty()) {
            message.append("Current game context: ").append(gameContext).append("\n\n");
        }
        
        message.append("User: ").append(userInput);
        
        return message.toString();
    }
    
    /**
     * Cleanup sessions periodically
     */
    public void cleanupSessions() {
        responsesManager.cleanupOldSessions();
        
        // Also clean our local session tracking
        if (activeSessions.size() > 10) {
            Log.d(TAG, "🧹 Cleaning up old sessions");
            activeSessions.clear();
        }
    }
    
    /**
     * Normalize master names from full names to short names for API consistency
     */
    private String normalizeMasterName(String masterName) {
        if (masterName == null) return "tal"; // Default fallback
        
        String normalized = masterName.toLowerCase().trim();
        
        // Handle common variations and full names
        if (normalized.contains("bobby") || normalized.contains("fischer")) {
            return "fischer";
        } else if (normalized.contains("magnus") || normalized.contains("carlsen")) {
            return "carlsen";
        } else if (normalized.contains("tal") || normalized.contains("mikhail")) {
            return "tal";
        } else if (normalized.contains("kasparov") || normalized.contains("garry")) {
            return "kasparov";
        } else if (normalized.contains("kramnik") || normalized.contains("vladimir")) {
            return "kramnik";
        } else if (normalized.contains("karpov") || normalized.contains("anatoly")) {
            return "karpov";
        } else if (normalized.contains("alekhine") || normalized.contains("alexander")) {
            return "alekhine";
        } else if (normalized.contains("capablanca") || normalized.contains("jose")) {
            return "capablanca";
        } else if (normalized.contains("morphy") || normalized.contains("paul")) {
            return "morphy";
        } else if (normalized.contains("lasker") || normalized.contains("emanuel")) {
            return "lasker";
        } else if (normalized.contains("anand") || normalized.contains("viswanathan")) {
            return "anand";
        } else if (normalized.contains("botvinnik") || normalized.contains("mikhail bot")) {
            return "botvinnik";
        }
        
        // If already normalized or unknown, return as is (default to tal if empty)
        return normalized.isEmpty() ? "tal" : normalized;
    }
}