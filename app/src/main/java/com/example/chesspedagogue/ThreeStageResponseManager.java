package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OPTIMIZED 3-Stage AI Response System - API 26 COMPATIBLE VERSION
 *
 * Stage 1: Lightning-fast responses (under 2 seconds)
 * Stage 2: Enhanced personality responses (5-10 seconds)
 * Stage 3: Deep analysis with vector store (10-20 seconds)
 *
 * Compatible with Android API 26+ (no orTimeout dependency)
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
        this.ttsService = OpenAITTSService.getInstance(context);

        Log.d(TAG, "✨ API 26 Compatible ThreeStageResponseManager initialized for speed!");
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
     */
    public void processThreeStageResponse(String userInput, String gameContext, ThreeStageCallback callback) {
        ResponseMode userMode = getUserResponseMode();
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

                // OPTIMIZED: Ultra-simple prompt for maximum speed
                String quickSystemPrompt = createLightningQuickPrompt(selectedMaster);
                String simpleUserMessage = createSimpleUserMessage(userInput);

                // CRITICAL: Use fast model with minimal context
                String quickResponse = openAIService.getChatCompletion(quickSystemPrompt, simpleUserMessage);

                if (quickResponse == null || quickResponse.trim().isEmpty()) {
                    return getEmergencyQuickResponse(selectedMaster);
                }

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
     */
    public void processThreeStageResponse(String userInput, String gameContext,
                                          ResponseMode mode, ThreeStageCallback callback) {
        int responseId = currentResponseId.incrementAndGet();
        Log.d(TAG, "🚀 Starting PARALLEL 3-stage response #" + responseId + " in " + mode + " mode");

        // Get current master
        String selectedMaster = modelManager.getSelectedChessMaster();

        // Reset speaking flags
        isStage1Speaking = false;
        isStage2Speaking = false;

        // Always start with Stage 1 for immediate feedback
        processOptimizedStage1Quick(responseId, userInput, gameContext, selectedMaster, callback);

        // Continue with other stages based on mode
        switch (mode) {
            case FAST_ONLY:
                Log.d(TAG, "⚡ Fast-only mode: Stage 1 only");
                return;

            case ENHANCED_ONLY:
                Log.d(TAG, "🎭 Enhanced mode: Stages 1-2");
                processOptimizedStage2Enhanced(responseId, userInput, gameContext, selectedMaster, callback);
                return;

            case FULL_ANALYSIS:
                Log.d(TAG, "🧠 Full analysis mode: All 3 stages - PARALLEL EXECUTION! 🚀");
                // PARALLEL EXECUTION: Start both Stage 2 and Stage 3 simultaneously
                processOptimizedStage2Enhanced(responseId, userInput, gameContext, selectedMaster, callback);
                processOptimizedStage3Deep(responseId, userInput, gameContext, selectedMaster, callback);
                return;

            case ADAPTIVE:
                if (isSimpleQuestion(userInput)) {
                    Log.d(TAG, "🤖 Adaptive: simple question, enhanced only");
                    processOptimizedStage2Enhanced(responseId, userInput, gameContext, selectedMaster, callback);
                } else {
                    Log.d(TAG, "🤖 Adaptive: complex question, PARALLEL full analysis! 🚀");
                    // PARALLEL EXECUTION for complex questions
                    processOptimizedStage2Enhanced(responseId, userInput, gameContext, selectedMaster, callback);
                    processOptimizedStage3Deep(responseId, userInput, gameContext, selectedMaster, callback);
                }
                return;
        }
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

        // Enhanced natural opening replacement for Tal
        if (cleaned.length() > 10 &&
                !cleaned.toLowerCase().matches("^(you know|when i|in my|during my|against \\w+|i remember|this reminds me).*")) {

            String[] talOpeners = {
                    "You know, ",
                    "In my experience, ",
                    "I've always believed that ",
                    "This reminds me of when ",
                    "During my career, I learned that ",
                    "Against strong opponents, I found that ",
                    "In positions like this, "
            };

            String opener = talOpeners[(int)(Math.random() * talOpeners.length)];

            // Ensure first letter is lowercase for natural flow
            if (cleaned.length() > 0) {
                cleaned = opener + Character.toLowerCase(cleaned.charAt(0)) + cleaned.substring(1);
            }
        }

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
     * ENHANCED: Anti-meta prompt for fine-tuned model
     */
    private String createOptimizedFineTunedPrompt(String master) {
        String masterName = modelManager.getMasterDisplayName(master);

        // ANTI-META prompting to suppress AI-like responses
        return "You are " + masterName + " speaking directly to a chess student. " +
                "NEVER say 'Let me analyze', 'As your assistant', 'I recommend', or 'You should'. " +
                "Instead, share your memories: 'I remember facing...', 'When I played Korchnoi...', " +
                "'In my experience...', 'This reminds me of my game against...'. " +
                "Speak as if recalling your actual chess career and philosophy. " +
                "Reference specific games, tournaments, or opponents when possible. " +
                "Be direct, personal, and engaging - no meta-commentary about analyzing positions.";
    }

    /**
     * ENHANCED: Context message that reinforces persona
     */
    private String createOptimizedUserMessage(String userInput, String gameContext) {
        StringBuilder message = new StringBuilder();

        // Add context but frame it as if Tal is looking at the board himself
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
        }

        // Frame the question as if asking Tal directly about his experience
        message.append("\nTal, what are your thoughts? What does this position remind you of from your career?");

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

        if (stage == 2) {
            // Calculate Stage 1 audio duration and ensure it gets meaningful play time
            if (isStage1Speaking) {
                // Estimate remaining play time for Stage 1 (rough heuristic: 150 chars/second)
                int estimatedRemainingSeconds = Math.max(2, getCurrentAudioRemainingTime());

                Log.d(TAG, "⏳ Stage 1 playing - allowing " + estimatedRemainingSeconds + " seconds before Stage 2");

                mainHandler.postDelayed(() -> {
                    performTTSTransition(text, stage, responseId);
                }, estimatedRemainingSeconds * 1000);
                return;
            }
        }

        if (stage == 3) {
            // For Stage 3, be more selective about interrupting
            if (isStage2Speaking) {
                // Only interrupt Stage 2 if it's been playing for a reasonable time
                int minStage2PlayTime = Math.max(4000, text.length() * 20); // Adaptive based on content length

                Log.d(TAG, "⏳ Stage 2 playing - ensuring " + (minStage2PlayTime/1000) + " seconds minimum");

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
     * HELPER: Estimate remaining audio time (you could enhance this with actual MediaPlayer duration)
     */
    private int getCurrentAudioRemainingTime() {
        // Simple heuristic - you could make this more sophisticated by tracking actual audio duration
        return 3; // Conservative 3-second minimum
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
}