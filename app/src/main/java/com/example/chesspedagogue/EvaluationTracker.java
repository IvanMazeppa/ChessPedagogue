package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EVALUATION SWING DETECTOR - Automatically detects blunders and brilliancies!
 *
 * This tracks Stockfish evaluations between moves and triggers automatic
 * commentary when significant swings occur. Perfect for learning!
 */
public class EvaluationTracker {
    private static final String TAG = "EvaluationTracker";
    private static final float BLUNDER_THRESHOLD = -2.0f;     // Lost 2+ pawns
    private static final float MISTAKE_THRESHOLD = -1.0f;     // Lost 1+ pawns
    private static final float INACCURACY_THRESHOLD = -0.5f;  // Lost 0.5+ pawns
    private static final float GOOD_THRESHOLD = 0.3f;         // Gained 0.3+ pawns
    private static final float EXCELLENT_THRESHOLD = 1.0f;    // Gained 1+ pawns
    private static final float BRILLIANT_THRESHOLD = 2.0f;    // Gained 2+ pawns

    // Mate evaluation constants
    private static final float MATE_VALUE = 10.0f;

    private static EvaluationTracker instance;

    private final Context context;
    private final List<EvaluationSnapshot> evaluationHistory;
    private EmotionalIntelligenceManager emotionalIntelligence; // Lazy-initialized to avoid circular dependency

    // Current tracking state
    private EvaluationSnapshot lastEvaluation;
    private boolean isTrackingEnabled = true;
    private int moveCount = 0;
    private long lastCommentaryTime = 0;
    private static final long MIN_COMMENTARY_INTERVAL = 8000; // 8 seconds between comments
    private final ExecutorService executorService;

    /**
     * Represents an evaluation at a specific point in the game
     */
    public static class EvaluationSnapshot {
        public final float evaluation;
        public final boolean isMate;
        public final int mateInMoves;
        public final int moveNumber;
        public final long timestamp;
        public final String position; // FEN for reference

        public EvaluationSnapshot(float evaluation, boolean isMate, int mateInMoves,
                                  int moveNumber, String position) {
            this.evaluation = evaluation;
            this.isMate = isMate;
            this.mateInMoves = mateInMoves;
            this.moveNumber = moveNumber;
            this.position = position;
            this.timestamp = System.currentTimeMillis();
        }

        public float getEffectiveEvaluation() {
            if (isMate) {
                return mateInMoves > 0 ? MATE_VALUE : -MATE_VALUE;
            }
            return evaluation;
        }

        @Override
        public String toString() {
            if (isMate) {
                return String.format("M%d (move %d)", mateInMoves, moveNumber);
            }
            return String.format("%.2f (move %d)", evaluation, moveNumber);
        }
    }

    /**
     * Classification of move quality based on evaluation change
     */
    public enum MoveQuality {
        BRILLIANT("Brilliant!", "An absolutely stunning move!"),
        EXCELLENT("Excellent", "A very strong move"),
        GOOD("Good move", "A solid choice"),
        NORMAL("", ""), // No comment needed
        INACCURACY("Inaccuracy", "Not the most accurate"),
        MISTAKE("Mistake", "This loses material"),
        BLUNDER("Blunder!", "A serious error");

        public final String shortDescription;
        public final String comment;

        MoveQuality(String shortDescription, String comment) {
            this.shortDescription = shortDescription;
            this.comment = comment;
        }
    }

    /**
     * Result of move evaluation analysis
     */
    public static class EvaluationSwing {
        public final EvaluationSnapshot previousEval;
        public final EvaluationSnapshot currentEval;
        public final float swingAmount;
        public final MoveQuality quality;
        public final boolean shouldComment;
        public final String move;

        public EvaluationSwing(EvaluationSnapshot previousEval, EvaluationSnapshot currentEval,
                               float swingAmount, MoveQuality quality, boolean shouldComment, String move) {
            this.previousEval = previousEval;
            this.currentEval = currentEval;
            this.swingAmount = swingAmount;
            this.quality = quality;
            this.shouldComment = shouldComment;
            this.move = move;
        }

        public boolean isSignificant() {
            return quality != MoveQuality.NORMAL && shouldComment;
        }

        @Override
        public String toString() {
            return String.format("%s: %.2f → %.2f (swing: %+.2f)",
                    quality.shortDescription,
                    previousEval.getEffectiveEvaluation(),
                    currentEval.getEffectiveEvaluation(),
                    swingAmount);
        }
    }

    /**
     * Callback interface for evaluation swing notifications
     */
    public interface EvaluationSwingListener {
        void onEvaluationSwingDetected(EvaluationSwing swing);
        void onSignificantSwingDetected(EvaluationSwing swing, String autoCommentary);
    }

    private EvaluationSwingListener swingListener;

    private EvaluationTracker(Context context) {
        this.context = context.getApplicationContext();
        this.evaluationHistory = new ArrayList<>();
        // Don't initialize EmotionalIntelligenceManager here to avoid circular dependency
        this.emotionalIntelligence = null;

        this.executorService = Executors.newSingleThreadExecutor();
        Log.d(TAG, "🎯 EvaluationTracker initialized - ready to detect brilliancies and blunders!");
    }

    public static synchronized EvaluationTracker getInstance(Context context) {
        if (instance == null) {
            instance = new EvaluationTracker(context);
        }
        return instance;
    }

    /**
     * OPTIMIZED: Track evaluation with reduced UI thread impact
     */
    /**
     * OPTIMIZED: Track evaluation with reduced UI thread impact - LAMBDA FIXED!
     */
    public EvaluationSwing trackEvaluation(StockfishManager.EvaluationResult evaluation,
                                           String currentFen, String lastMove) {

        if (!isTrackingEnabled) {
            return null;
        }

        // OPTIMIZATION: Do heavy lifting off UI thread
        executorService.execute(() -> {
            moveCount++;

            // Create current snapshot
            EvaluationSnapshot currentSnapshot = new EvaluationSnapshot(
                    evaluation.evaluation,
                    evaluation.isMate,
                    evaluation.mateInMoves,
                    moveCount,
                    currentFen
            );

            Log.d(TAG, "📊 Tracking evaluation for move " + moveCount + ": " + currentSnapshot);

            EvaluationSwing swing = null;

            // Analyze swing if we have a previous evaluation
            if (lastEvaluation != null) {
                swing = analyzeEvaluationSwing(lastEvaluation, currentSnapshot, lastMove);

                if (swing != null && swing.isSignificant()) {
                    Log.d(TAG, "🎯 SIGNIFICANT SWING DETECTED: " + swing);

                    // LAMBDA FIX: Create final reference for use in inner lambda
                    final EvaluationSwing finalSwing = swing;

                    // 🎭 NEW: Trigger emotional analysis for the swing
                    triggerEmotionalAnalysisForSwing(finalSwing);

                    // Check if enough time has passed since last commentary
                    long now = System.currentTimeMillis();
                    if (now - lastCommentaryTime >= MIN_COMMENTARY_INTERVAL) {
                        // OPTIMIZATION: Trigger commentary on background thread
                        triggerAutoCommentary(finalSwing);
                        lastCommentaryTime = now;
                    } else {
                        Log.d(TAG, "⏳ Skipping commentary - too soon since last comment");
                    }

                    // OPTIMIZATION: Notify listener on UI thread but without blocking
                    if (swingListener != null) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> swingListener.onEvaluationSwingDetected(finalSwing)); // FIXED!
                    }
                }
            }

            // Store current evaluation for next comparison
            lastEvaluation = currentSnapshot;
            evaluationHistory.add(currentSnapshot);

            // Keep history manageable
            if (evaluationHistory.size() > 50) {
                evaluationHistory.remove(0);
            }
        });

        return null; // Return immediately to avoid blocking
    }

    /**
     * Analyze the evaluation change between two positions
     */
    private EvaluationSwing analyzeEvaluationSwing(EvaluationSnapshot previous,
                                                   EvaluationSnapshot current, String move) {

        float prevEval = previous.getEffectiveEvaluation();
        float currEval = current.getEffectiveEvaluation();

        // Calculate swing (from previous player's perspective)
        // If it's move 2, 4, 6... (even), we're looking at White's move, so positive swing is good for White
        // If it's move 1, 3, 5... (odd), we're looking at Black's move, so negative swing is good for Black
        boolean isWhiteMove = (moveCount % 2 == 0);
        float swingAmount = currEval - prevEval;

        // Adjust swing perspective for the player who just moved
        if (!isWhiteMove) {
            swingAmount = -swingAmount; // Flip for Black's perspective
        }

        Log.d(TAG, String.format("📈 Swing analysis: %.2f → %.2f = %+.2f (%s move)",
                prevEval, currEval, swingAmount, isWhiteMove ? "White" : "Black"));

        // Classify the move
        MoveQuality quality = classifyMove(swingAmount, previous.isMate, current.isMate);

        // Determine if we should comment (avoid spam on normal moves)
        boolean shouldComment = shouldTriggerCommentary(quality, swingAmount);

        return new EvaluationSwing(previous, current, swingAmount, quality, shouldComment, move);
    }

    /**
     * Classify move quality based on evaluation swing
     */
    private MoveQuality classifyMove(float swingAmount, boolean wasMate, boolean isMate) {
        // Special handling for mate scenarios
        if (wasMate && !isMate) {
            return MoveQuality.BLUNDER; // Lost a mate
        }
        if (!wasMate && isMate && swingAmount > 0) {
            return MoveQuality.BRILLIANT; // Found a mate
        }

        // Standard evaluation-based classification
        if (swingAmount >= BRILLIANT_THRESHOLD) {
            return MoveQuality.BRILLIANT;
        } else if (swingAmount >= EXCELLENT_THRESHOLD) {
            return MoveQuality.EXCELLENT;
        } else if (swingAmount >= GOOD_THRESHOLD) {
            return MoveQuality.GOOD;
        } else if (swingAmount <= BLUNDER_THRESHOLD) {
            return MoveQuality.BLUNDER;
        } else if (swingAmount <= MISTAKE_THRESHOLD) {
            return MoveQuality.MISTAKE;
        } else if (swingAmount <= INACCURACY_THRESHOLD) {
            return MoveQuality.INACCURACY;
        } else {
            return MoveQuality.NORMAL;
        }
    }

    /**
     * Determine if we should trigger automatic commentary
     */
    private boolean shouldTriggerCommentary(MoveQuality quality, float swingAmount) {
        // FIXED: Disabled automatic commentary to eliminate canned phrases
        // Emotional system and personality engine handle all natural responses
        return false;
    }

    /**
     * FIXED: Trigger automatic commentary using fine-tuned model directly
     * DISABLED IN SPECTATOR MODE: SpectatorConversationOrchestrator handles all dialogue via Responses API
     */
    private void triggerAutoCommentary(EvaluationSwing swing) {
        try {
            // CRITICAL FIX: Check if we're in spectator mode - if so, skip this auto-commentary
            // because SpectatorConversationOrchestrator is already handling all dialogue via Responses API
            android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
            boolean isSpectatorMode = prefs.getBoolean("is_spectator_mode", false);
            
            if (isSpectatorMode) {
                Log.d(TAG, "🎭 SKIPPING EvaluationTracker auto-commentary in spectator mode - Responses API handles all dialogue");
                // Still notify the listener for UI updates, but don't generate duplicate API calls
                if (swingListener != null) {
                    swingListener.onSignificantSwingDetected(swing, null);
                }
                return;
            }

            // Generate context-aware commentary prompt
            String commentaryPrompt = generateCommentaryPrompt(swing);

            Log.d(TAG, "🎙️ Triggering auto-commentary with fine-tuned model: " + swing.quality.shortDescription);

            // Use fine-tuned model directly instead of 3-stage system
            String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();
            String systemPrompt = FineTunedModelManager.getInstance(context)
                    .getEnhancedSystemPromptForMaster(selectedMaster);

            // Execute on background thread
            new Thread(() -> {
                try {
                    // Use fine-tuned model directly for faster, more personality-rich responses
                    String response = OpenAIService.getInstance().getChatCompletion(systemPrompt, commentaryPrompt);

                    if (response != null && !response.trim().isEmpty()) {
                        Log.d(TAG, "✅ Auto-commentary generated with fine-tuned model: " +
                                response.substring(0, Math.min(100, response.length())));

                        // Speak using TTS directly
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            OpenAITTSService ttsService = TTSServiceManager.getOpenAITTSService(context);
                            ttsService.speak(response, new OpenAITTSService.OnSpeechCompletedListener() {
                                @Override
                                public void onSpeechCompleted() {
                                    Log.d(TAG, "🎵 Auto-commentary speech completed");
                                }
                            });

                            // Notify listener with the generated commentary
                            if (swingListener != null) {
                                swingListener.onSignificantSwingDetected(swing, response);
                            }
                        });
                    } else {
                        Log.w(TAG, "Empty response from fine-tuned model for auto-commentary");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in auto-commentary generation", e);
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Error triggering auto-commentary", e);
        }
    }

    /**
     * Generate an appropriate commentary prompt based on the evaluation swing
     */
    private String generateCommentaryPrompt(EvaluationSwing swing) {
        StringBuilder prompt = new StringBuilder();

        // Get current master for personalized commentary
        String currentMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();
        String masterName = FineTunedModelManager.getInstance(context).getMasterDisplayName(currentMaster);

        prompt.append("The evaluation just changed from ");
        prompt.append(String.format("%.1f to %.1f",
                swing.previousEval.getEffectiveEvaluation(),
                swing.currentEval.getEffectiveEvaluation()));

        if (swing.move != null && !swing.move.isEmpty()) {
            prompt.append(" after the move ").append(swing.move);
        }

        prompt.append(". ");

        // Add specific prompt based on move quality
        switch (swing.quality) {
            case BRILLIANT:
                prompt.append("This was a brilliant move! Comment on why this move is so strong and what makes it brilliant. ");
                prompt.append("As ").append(masterName).append(", share your excitement about this excellent play!");
                break;

            case EXCELLENT:
                prompt.append("This was an excellent move! Briefly explain what makes this move so good.");
                break;

            case BLUNDER:
                prompt.append("Oh no, this was a blunder! Gently explain what went wrong and offer encouragement. ");
                prompt.append("As ").append(masterName).append(", be supportive while pointing out the missed opportunity.");
                break;

            case MISTAKE:
                prompt.append("This move loses material. Briefly explain the problem and suggest how to improve.");
                break;

            default:
                prompt.append("Comment briefly on this position change.");
        }

        prompt.append(" Keep your response to 1-2 sentences - this is automatic commentary during play.");

        return prompt.toString();
    }

    /**
     * 🎭 ENHANCED: Trigger emotional analysis for evaluation swings with Phase 2 integration
     * This analyzes how chess masters would emotionally react to blunders, brilliancies, etc.
     */
    private void triggerEmotionalAnalysisForSwing(EvaluationSwing swing) {
        try {
            // Lazy initialization of EmotionalIntelligenceManager to avoid circular dependency
            if (emotionalIntelligence == null) {
                emotionalIntelligence = EmotionalIntelligenceManager.getInstance(context);
            }
            
            // 🎭 Phase 2: Try to get the integration bridge for advanced emotional processing
            Phase2EmotionalIntegrationBridge phase2Bridge = null;
            try {
                phase2Bridge = Phase2EmotionalIntegrationBridge.getInstance(context);
            } catch (Exception e) {
                Log.d(TAG, "Phase 2 not available, using Phase 1 emotional analysis");
            }
            
            // Get current master from preferences
            String currentMaster = getCurrentSelectedMaster();
            
            // 🎭 Phase 2: Process evaluation change through multi-layered emotional system
            if (phase2Bridge != null && phase2Bridge.isIntegrationActive()) {
                String gamePhase = determineGamePhase(swing.currentEval.moveNumber);
                phase2Bridge.processEvaluationChange(
                    currentMaster, 
                    swing.currentEval.getEffectiveEvaluation(),
                    swing.previousEval.getEffectiveEvaluation(),
                    gamePhase
                );
                
                Log.d(TAG, String.format("🎭 Phase 2: Evaluation change processed for %s - %.1f->%.1f (%s)", 
                      currentMaster, swing.previousEval.getEffectiveEvaluation(), 
                      swing.currentEval.getEffectiveEvaluation(), swing.quality.shortDescription));
            }
            
            // Phase 1: Continue with original emotional analysis for compatibility
            String gameContext = buildSwingGameContext(swing);
            String conversationContext = buildSwingConversationContext(swing);
            
            // Analyze emotional response using EmotionalIntelligenceManager
            EmotionalIntelligenceManager.EmotionalAnalysisResult emotionalResult = 
                emotionalIntelligence.analyzeEmotionalState(
                    currentMaster,
                    gameContext,
                    conversationContext,
                    swing.currentEval.getEffectiveEvaluation(),
                    swing.swingAmount,
                    null
                );
            
            Log.d(TAG, String.format("🎭 Phase 1: Emotional analysis for %s %s: %s (intensity: %.2f, momentum: %.2f)", 
                  currentMaster, swing.quality.shortDescription, emotionalResult.emotion.name, 
                  emotionalResult.intensity, emotionalResult.momentum));
            
            // Update emotional state for the master
            // This helps track emotional momentum over the course of the game
            if (emotionalResult.intensity > 0.3f) {
                Log.d(TAG, String.format("🎭 Significant emotional response: %s is %s about the %s", 
                      currentMaster, emotionalResult.emotion.name, swing.quality.shortDescription.toLowerCase()));
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in emotional analysis for evaluation swing", e);
        }
    }
    
    /**
     * 🎭 Phase 2: Determine game phase for emotional context
     */
    private String determineGamePhase(int moveNumber) {
        if (moveNumber < 12) {
            return "opening";
        } else if (moveNumber < 40) {
            return "middlegame";
        } else if (moveNumber < 60) {
            return "endgame";
        } else {
            return "endgame_critical";
        }
    }
    
    /**
     * Build game context for emotional analysis of evaluation swings
     */
    private String buildSwingGameContext(EvaluationSwing swing) {
        StringBuilder context = new StringBuilder();
        
        switch (swing.quality) {
            case BRILLIANT:
                context.append("brilliant_move_made");
                break;
            case EXCELLENT:
                context.append("excellent_move_made");
                break;
            case GOOD:
                context.append("good_move_made");
                break;
            case BLUNDER:
                context.append("blunder_made");
                break;
            case MISTAKE:
                context.append("mistake_made");
                break;
            case INACCURACY:
                context.append("inaccuracy_made");
                break;
            default:
                context.append("position_change");
                break;
        }
        
        // Add magnitude context
        float absSwing = Math.abs(swing.swingAmount);
        if (absSwing > 3.0f) {
            context.append("_major");
        } else if (absSwing > 1.5f) {
            context.append("_significant");
        } else {
            context.append("_minor");
        }
        
        return context.toString();
    }
    
    /**
     * Build conversation context for emotional analysis
     */
    private String buildSwingConversationContext(EvaluationSwing swing) {
        StringBuilder context = new StringBuilder("evaluation_swing");
        
        // Add timing context
        if (swing.currentEval.moveNumber < 10) {
            context.append("_opening");
        } else if (swing.currentEval.moveNumber < 30) {
            context.append("_middlegame");
        } else {
            context.append("_endgame");
        }
        
        // Add positional context
        float currentEval = swing.currentEval.getEffectiveEvaluation();
        if (Math.abs(currentEval) > 5.0f) {
            context.append("_decisive");
        } else if (Math.abs(currentEval) > 2.0f) {
            context.append("_advantage");
        } else {
            context.append("_balanced");
        }
        
        return context.toString();
    }
    
    /**
     * Get the currently selected chess master from preferences
     */
    private String getCurrentSelectedMaster() {
        android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("selected_master", "tal");
    }

    /**
     * Configuration and control methods
     */
    public void setEvaluationSwingListener(EvaluationSwingListener listener) {
        this.swingListener = listener;
    }

    public void setTrackingEnabled(boolean enabled) {
        this.isTrackingEnabled = enabled;
        Log.d(TAG, "🎯 Evaluation tracking " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public boolean isTrackingEnabled() {
        return isTrackingEnabled;
    }

    public void resetTracking() {
        evaluationHistory.clear();
        lastEvaluation = null;
        moveCount = 0;
        lastCommentaryTime = 0;
        Log.d(TAG, "🔄 Evaluation tracking reset");
    }

    public List<EvaluationSnapshot> getEvaluationHistory() {
        return new ArrayList<>(evaluationHistory);
    }

    public EvaluationSnapshot getLastEvaluation() {
        return lastEvaluation;
    }

    /**
     * Get statistics about the current game
     */
    public GameEvaluationStats getGameStats() {
        if (evaluationHistory.size() < 2) {
            return new GameEvaluationStats(0, 0, 0, 0, 0);
        }

        int blunders = 0, mistakes = 0, inaccuracies = 0, good = 0, excellent = 0;

        // Analyze all moves in the game
        for (int i = 1; i < evaluationHistory.size(); i++) {
            EvaluationSnapshot prev = evaluationHistory.get(i - 1);
            EvaluationSnapshot curr = evaluationHistory.get(i);

            float swing = curr.getEffectiveEvaluation() - prev.getEffectiveEvaluation();
            boolean isWhiteMove = (i % 2 == 0);
            if (!isWhiteMove) swing = -swing;

            MoveQuality quality = classifyMove(swing, prev.isMate, curr.isMate);

            switch (quality) {
                case BLUNDER: blunders++; break;
                case MISTAKE: mistakes++; break;
                case INACCURACY: inaccuracies++; break;
                case GOOD: good++; break;
                case EXCELLENT:
                case BRILLIANT: excellent++; break;
            }
        }

        return new GameEvaluationStats(blunders, mistakes, inaccuracies, good, excellent);
    }

    /**
     * Game statistics data class
     */
    public static class GameEvaluationStats {
        public final int blunders;
        public final int mistakes;
        public final int inaccuracies;
        public final int goodMoves;
        public final int excellentMoves;

        public GameEvaluationStats(int blunders, int mistakes, int inaccuracies, int goodMoves, int excellentMoves) {
            this.blunders = blunders;
            this.mistakes = mistakes;
            this.inaccuracies = inaccuracies;
            this.goodMoves = goodMoves;
            this.excellentMoves = excellentMoves;
        }

        public int getTotalMoves() {
            return blunders + mistakes + inaccuracies + goodMoves + excellentMoves;
        }

        public float getAccuracy() {
            int total = getTotalMoves();
            if (total == 0) return 100.0f;

            int goodMovesTotal = goodMoves + excellentMoves;
            return (goodMovesTotal * 100.0f) / total;
        }

        @Override
        public String toString() {
            return String.format("Accuracy: %.1f%% (Blunders: %d, Mistakes: %d, Good moves: %d)",
                    getAccuracy(), blunders, mistakes, goodMoves + excellentMoves);
        }
    }

    /**
     * Get the recent evaluation change if any significant swing occurred
     * @return The evaluation change amount, or null if no recent significant change
     */
    public Float getRecentEvaluationChange() {
        if (evaluationHistory.size() < 2) {
            return null;
        }
        
        // Get the last two evaluations
        EvaluationSnapshot prev = evaluationHistory.get(evaluationHistory.size() - 2);
        EvaluationSnapshot curr = evaluationHistory.get(evaluationHistory.size() - 1);
        
        // Calculate the swing
        float prevEval = prev.getEffectiveEvaluation();
        float currEval = curr.getEffectiveEvaluation();
        float swingAmount = currEval - prevEval;
        
        // Adjust for player perspective
        boolean isWhiteMove = (moveCount % 2 == 0);
        if (!isWhiteMove) {
            swingAmount = -swingAmount;
        }
        
        // Only return if significant
        if (Math.abs(swingAmount) >= INACCURACY_THRESHOLD) {
            return swingAmount;
        }
        
        return null;
    }
    
    /**
     * Get the current evaluation (most recent)
     * @return The current evaluation, or null if no evaluations tracked
     */
    public Float getCurrentEvaluation() {
        if (evaluationHistory.isEmpty()) {
            return null;
        }
        
        // Get the most recent evaluation
        EvaluationSnapshot current = evaluationHistory.get(evaluationHistory.size() - 1);
        return current.getEffectiveEvaluation();
    }

    /**
     * Clean up resources when no longer needed
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            Log.d(TAG, "🧹 EvaluationTracker cleanup completed");
        }
    }
}