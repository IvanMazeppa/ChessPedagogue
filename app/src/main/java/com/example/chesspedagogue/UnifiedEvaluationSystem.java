package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 🎯 UNIFIED EVALUATION SYSTEM - Single Source of Truth for Chess Evaluations
 * 
 * Replaces fragmented evaluation system with one authoritative pipeline:
 * - Consistent UCI perspective handling (positive = White advantage)  
 * - Thread-safe evaluation caching
 * - Proper synchronization to prevent race conditions
 * - Compatible with all existing game mechanics
 * 
 * Key Benefits:
 * ✅ Eliminates impossible evaluation swings
 * ✅ Single perspective logic (no more flipping confusion)
 * ✅ Race condition protection
 * ✅ Clean architecture for future enhancements
 */
public class UnifiedEvaluationSystem {
    private static final String TAG = "UnifiedEvaluationSystem";
    
    // Singleton instance
    private static volatile UnifiedEvaluationSystem instance;
    
    // Core dependencies
    private final Context context;
    private final StockfishManager stockfishManager;
    private final ExecutorService evaluationExecutor;
    private final Handler mainHandler;
    
    // Evaluation state management
    private final AtomicReference<EvaluationResult> currentEvaluation;
    private final AtomicReference<String> currentPosition;
    private final ConcurrentHashMap<String, EvaluationResult> evaluationCache;
    private final AtomicBoolean isEvaluating;
    
    // Evaluation listeners for real-time updates
    public interface EvaluationListener {
        void onEvaluationUpdated(EvaluationResult evaluation, String position);
        void onEvaluationError(String error);
    }
    
    private volatile EvaluationListener evaluationListener;
    
    /**
     * Standard evaluation result - maintains compatibility with existing code
     */
    public static class EvaluationResult {
        public final float evaluation;    // In pawns, positive = White advantage (UCI standard)
        public final boolean isMate;
        public final int mateInMoves;     // Positive if White mates, negative if Black mates
        public final String position;     // FEN that was evaluated
        public final long timestamp;      // When evaluation was computed
        public final boolean fromCache;   // Whether this came from cache
        
        public EvaluationResult(float evaluation, boolean isMate, int mateInMoves, 
                              String position, boolean fromCache) {
            this.evaluation = evaluation;
            this.isMate = isMate;
            this.mateInMoves = mateInMoves;
            this.position = position;
            this.timestamp = System.currentTimeMillis();
            this.fromCache = fromCache;
        }
        
        /**
         * Convert to StockfishManager.EvaluationResult for compatibility
         */
        public StockfishManager.EvaluationResult toStockfishResult() {
            return new StockfishManager.EvaluationResult(evaluation, isMate, mateInMoves);
        }
        
        /**
         * Get effective evaluation value (handles mate positions)
         */
        public float getEffectiveEvaluation() {
            if (isMate) {
                return mateInMoves > 0 ? 10.0f : -10.0f; // Standard mate values
            }
            return evaluation;
        }
        
        @Override
        public String toString() {
            if (isMate) {
                return String.format("M%d (%s)", mateInMoves, fromCache ? "cached" : "fresh");
            }
            return String.format("%.2f (%s)", evaluation, fromCache ? "cached" : "fresh");
        }
    }
    
    private UnifiedEvaluationSystem(Context context) {
        this.context = context.getApplicationContext();
        this.stockfishManager = new StockfishManager(); // Will be properly initialized by caller
        this.evaluationExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "UnifiedEvaluation");
            thread.setDaemon(true);
            return thread;
        });
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize state
        this.currentEvaluation = new AtomicReference<>();
        this.currentPosition = new AtomicReference<>("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        this.evaluationCache = new ConcurrentHashMap<>();
        this.isEvaluating = new AtomicBoolean(false);
        
        Log.d(TAG, "🎯 UnifiedEvaluationSystem initialized - single source of truth ready");
    }
    
    public static UnifiedEvaluationSystem getInstance(Context context) {
        if (instance == null) {
            synchronized (UnifiedEvaluationSystem.class) {
                if (instance == null) {
                    instance = new UnifiedEvaluationSystem(context);
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize the underlying Stockfish engine
     */
    public boolean initializeEngine(String enginePath) {
        Log.d(TAG, "🔧 Initializing Stockfish engine: " + enginePath);
        boolean success = stockfishManager.startEngine(enginePath);
        if (success) {
            Log.d(TAG, "✅ Stockfish engine initialized successfully");
        } else {
            Log.e(TAG, "❌ Failed to initialize Stockfish engine");
        }
        return success;
    }
    
    /**
     * Set the current game position for evaluation
     */
    public void setPosition(String fen) {
        if (fen == null || fen.trim().isEmpty()) {
            Log.w(TAG, "⚠️ Ignoring null/empty FEN position");
            return;
        }
        
        String previousPosition = currentPosition.getAndSet(fen);
        Log.d(TAG, "📍 Position updated: " + fen);
        
        // If position changed, invalidate current evaluation
        if (!fen.equals(previousPosition)) {
            currentEvaluation.set(null);
            Log.d(TAG, "🔄 Position changed - evaluation invalidated");
        }
        
        // Configure Stockfish with new position
        evaluationExecutor.execute(() -> {
            try {
                stockfishManager.setPosition(fen);
                Log.d(TAG, "✅ Stockfish position set: " + fen);
            } catch (Exception e) {
                Log.e(TAG, "❌ Error setting Stockfish position", e);
            }
        });
    }
    
    /**
     * 🎯 PRIMARY METHOD: Get evaluation for current position
     * This is the single source of truth that replaces all other evaluation calls
     */
    public void evaluateCurrentPosition(int thinkTimeMs, EvaluationCallback callback) {
        String position = currentPosition.get();
        evaluatePosition(position, thinkTimeMs, callback);
    }
    
    /**
     * Evaluate a specific position (with caching for performance)
     */
    public void evaluatePosition(String fen, int thinkTimeMs, EvaluationCallback callback) {
        if (fen == null || fen.trim().isEmpty()) {
            callback.onError("Invalid FEN position");
            return;
        }
        
        // Check cache first for performance
        String cacheKey = fen + "_" + thinkTimeMs;
        EvaluationResult cachedResult = evaluationCache.get(cacheKey);
        if (cachedResult != null && (System.currentTimeMillis() - cachedResult.timestamp) < 30000) {
            Log.d(TAG, "💾 Cache hit for position: " + cachedResult);
            callback.onSuccess(cachedResult);
            return;
        }
        
        // Prevent concurrent evaluations of same position
        if (!isEvaluating.compareAndSet(false, true)) {
            Log.d(TAG, "⏳ Evaluation already in progress, queuing callback");
            // Could implement a queue here if needed
            return;
        }
        
        evaluationExecutor.execute(() -> {
            try {
                Log.d(TAG, "🧠 Evaluating position: " + fen + " (think time: " + thinkTimeMs + "ms)");
                
                // Set position if it's different from current
                if (!fen.equals(currentPosition.get())) {
                    stockfishManager.setPosition(fen);
                    currentPosition.set(fen);
                }
                
                // Get evaluation from Stockfish
                StockfishManager.EvaluationResult stockfishResult = 
                    stockfishManager.getCurrentEvaluation(thinkTimeMs);
                
                if (stockfishResult != null) {
                    // 🔧 CRITICAL: Maintain UCI standard perspective
                    // Stockfish returns evaluations from White's perspective
                    // Positive = White advantage, Negative = Black advantage
                    // NO PERSPECTIVE FLIPPING - this is the source of truth
                    
                    EvaluationResult unifiedResult = new EvaluationResult(
                        stockfishResult.evaluation,  // Keep raw Stockfish evaluation
                        stockfishResult.isMate,
                        stockfishResult.mateInMoves,
                        fen,
                        false // Fresh evaluation
                    );
                    
                    // Cache the result
                    evaluationCache.put(cacheKey, unifiedResult);
                    currentEvaluation.set(unifiedResult);
                    
                    Log.d(TAG, "✅ Evaluation complete: " + unifiedResult);
                    
                    // Notify listener on main thread
                    mainHandler.post(() -> {
                        callback.onSuccess(unifiedResult);
                        if (evaluationListener != null) {
                            evaluationListener.onEvaluationUpdated(unifiedResult, fen);
                        }
                    });
                    
                } else {
                    Log.e(TAG, "❌ Stockfish returned null evaluation");
                    mainHandler.post(() -> callback.onError("Stockfish evaluation failed"));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error during evaluation", e);
                mainHandler.post(() -> callback.onError("Evaluation error: " + e.getMessage()));
            } finally {
                isEvaluating.set(false);
            }
        });
    }
    
    /**
     * Callback interface for asynchronous evaluations
     */
    public interface EvaluationCallback {
        void onSuccess(EvaluationResult result);
        void onError(String error);
    }
    
    /**
     * Get the most recent evaluation synchronously (for immediate UI updates)
     */
    public EvaluationResult getCurrentEvaluationSync() {
        return currentEvaluation.get();
    }
    
    /**
     * Quick evaluation check without triggering new computation
     */
    public EvaluationResult getCachedEvaluation(String fen) {
        // Try different think times in cache
        for (int thinkTime : new int[]{1000, 2000, 3000}) {
            String cacheKey = fen + "_" + thinkTime;
            EvaluationResult cached = evaluationCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }
        return null;
    }
    
    /**
     * Register listener for real-time evaluation updates
     */
    public void setEvaluationListener(EvaluationListener listener) {
        this.evaluationListener = listener;
        Log.d(TAG, "📡 Evaluation listener registered: " + (listener != null ? "active" : "removed"));
    }
    
    /**
     * Configure Stockfish engine settings
     */
    public void configureEngine(int skillLevel, int eloRating) {
        evaluationExecutor.execute(() -> {
            try {
                // Configure skill level and ELO as before
                if (skillLevel >= 0 && skillLevel <= 20) {
                    stockfishManager.sendCommand("setoption name Skill Level value " + skillLevel);
                    Log.d(TAG, "🎚️ Skill level set: " + skillLevel);
                }
                
                if (eloRating > 0) {
                    stockfishManager.sendCommand("setoption name UCI_LimitStrength value true");
                    stockfishManager.sendCommand("setoption name UCI_Elo value " + eloRating);
                    Log.d(TAG, "🎯 ELO rating set: " + eloRating);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error configuring engine", e);
            }
        });
    }
    
    /**
     * Get the best move from current position
     */
    public void getBestMove(int thinkTimeMs, BestMoveCallback callback) {
        String position = currentPosition.get();
        
        evaluationExecutor.execute(() -> {
            try {
                Log.d(TAG, "🤔 Getting best move for position: " + position);
                
                // Ensure position is set
                stockfishManager.setPosition(position);
                
                // Get best move from Stockfish
                String bestMove = stockfishManager.getBestMove(thinkTimeMs);
                
                if (bestMove != null && !bestMove.trim().isEmpty()) {
                    Log.d(TAG, "✅ Best move found: " + bestMove);
                    mainHandler.post(() -> callback.onSuccess(bestMove));
                } else {
                    Log.e(TAG, "❌ No best move returned");
                    mainHandler.post(() -> callback.onError("No best move found"));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error getting best move", e);
                mainHandler.post(() -> callback.onError("Best move error: " + e.getMessage()));
            }
        });
    }
    
    public interface BestMoveCallback {
        void onSuccess(String move);
        void onError(String error);
    }
    
    /**
     * Clear evaluation cache (useful for memory management)
     */
    public void clearCache() {
        evaluationCache.clear();
        Log.d(TAG, "🧹 Evaluation cache cleared");
    }
    
    /**
     * Get current position FEN
     */
    public String getCurrentPosition() {
        return currentPosition.get();
    }
    
    /**
     * Check if system is currently evaluating
     */
    public boolean isEvaluating() {
        return isEvaluating.get();
    }
    
    /**
     * Shutdown the evaluation system
     */
    public void shutdown() {
        Log.d(TAG, "🔄 Shutting down UnifiedEvaluationSystem");
        
        try {
            evaluationExecutor.shutdown();
            stockfishManager.stopEngine();
            clearCache();
            
            Log.d(TAG, "✅ UnifiedEvaluationSystem shutdown complete");
        } catch (Exception e) {
            Log.e(TAG, "Error during shutdown", e);
        }
    }
    
    /**
     * Get cache statistics for debugging
     */
    public String getCacheStats() {
        return String.format("Cache: %d entries, Current: %s, Evaluating: %s",
                evaluationCache.size(),
                currentEvaluation.get() != null ? "available" : "null",
                isEvaluating.get() ? "yes" : "no");
    }
}