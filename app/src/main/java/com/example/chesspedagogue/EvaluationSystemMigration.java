package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🔄 EVALUATION SYSTEM MIGRATION HELPER
 * 
 * Provides compatibility layer between old evaluation calls and new UnifiedEvaluationSystem.
 * This allows gradual migration without breaking existing functionality.
 * 
 * Usage:
 * 1. Replace StockfishManager evaluation calls with EvaluationSystemMigration
 * 2. Test functionality with unified system
 * 3. Remove migration layer once everything works
 */
public class EvaluationSystemMigration {
    private static final String TAG = "EvaluationMigration";
    
    private final UnifiedEvaluationSystem unifiedSystem;
    private final Context context;
    
    public EvaluationSystemMigration(Context context) {
        this.context = context;
        this.unifiedSystem = UnifiedEvaluationSystem.getInstance(context);
        Log.d(TAG, "🔄 Migration helper initialized");
    }
    
    /**
     * 🎯 COMPATIBILITY: Replace StockfishManager.getCurrentEvaluation() calls
     */
    public static class EvaluationRequest {
        private final UnifiedEvaluationSystem system;
        private final String position;
        private final int thinkTime;
        
        public EvaluationRequest(UnifiedEvaluationSystem system, String position, int thinkTime) {
            this.system = system;
            this.position = position;
            this.thinkTime = thinkTime;
        }
        
        /**
         * Execute synchronously (blocks until evaluation complete)
         * ⚠️ Use sparingly - prefer async methods for better performance
         */
        public StockfishManager.EvaluationResult executeSync() {
            final Object lock = new Object();
            final StockfishManager.EvaluationResult[] result = new StockfishManager.EvaluationResult[1];
            final Exception[] error = new Exception[1];
            
            system.evaluatePosition(position, thinkTime, new UnifiedEvaluationSystem.EvaluationCallback() {
                @Override
                public void onSuccess(UnifiedEvaluationSystem.EvaluationResult evaluation) {
                    synchronized (lock) {
                        result[0] = evaluation.toStockfishResult();
                        lock.notify();
                    }
                }
                
                @Override
                public void onError(String errorMessage) {
                    synchronized (lock) {
                        error[0] = new RuntimeException("Evaluation failed: " + errorMessage);
                        lock.notify();
                    }
                }
            });
            
            synchronized (lock) {
                try {
                    lock.wait(thinkTime + 5000); // Wait for result with timeout
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            
            if (error[0] != null) {
                Log.e(TAG, "Sync evaluation failed", error[0]);
                return null;
            }
            
            return result[0];
        }
        
        /**
         * Execute asynchronously (recommended)
         */
        public void executeAsync(EvaluationResultCallback callback) {
            system.evaluatePosition(position, thinkTime, new UnifiedEvaluationSystem.EvaluationCallback() {
                @Override
                public void onSuccess(UnifiedEvaluationSystem.EvaluationResult evaluation) {
                    callback.onResult(evaluation.toStockfishResult());
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Async evaluation failed: " + errorMessage);
                    callback.onResult(null);
                }
            });
        }
    }
    
    public interface EvaluationResultCallback {
        void onResult(StockfishManager.EvaluationResult result);
    }
    
    /**
     * 🔄 MIGRATION: Replace direct StockfishManager calls
     */
    public EvaluationRequest createEvaluationRequest(String fen, int thinkTimeMs) {
        return new EvaluationRequest(unifiedSystem, fen, thinkTimeMs);
    }
    
    /**
     * 🔄 MIGRATION: Replace StockfishManager.getCurrentEvaluation()
     */
    public StockfishManager.EvaluationResult getCurrentEvaluation(String fen, int thinkTimeMs) {
        Log.d(TAG, "🔄 MIGRATION: getCurrentEvaluation -> UnifiedEvaluationSystem");
        
        // Set position in unified system
        unifiedSystem.setPosition(fen);
        
        // Create request and execute synchronously
        return createEvaluationRequest(fen, thinkTimeMs).executeSync();
    }
    
    /**
     * 🔄 MIGRATION: Async version for better performance
     */
    public void getCurrentEvaluationAsync(String fen, int thinkTimeMs, EvaluationResultCallback callback) {
        Log.d(TAG, "🔄 MIGRATION: getCurrentEvaluationAsync -> UnifiedEvaluationSystem");
        
        // Set position in unified system
        unifiedSystem.setPosition(fen);
        
        // Create request and execute asynchronously
        createEvaluationRequest(fen, thinkTimeMs).executeAsync(callback);
    }
    
    /**
     * 🔄 MIGRATION: Engine configuration
     */
    public void configureEngine(int skillLevel, int eloRating) {
        Log.d(TAG, "🔄 MIGRATION: configureEngine -> UnifiedEvaluationSystem");
        unifiedSystem.configureEngine(skillLevel, eloRating);
    }
    
    /**
     * 🔄 MIGRATION: Initialize engine
     */
    public boolean initializeEngine(String enginePath) {
        Log.d(TAG, "🔄 MIGRATION: initializeEngine -> UnifiedEvaluationSystem");
        return unifiedSystem.initializeEngine(enginePath);
    }
    
    /**
     * 🔄 MIGRATION: Set position
     */
    public void setPosition(String fen) {
        Log.d(TAG, "🔄 MIGRATION: setPosition -> UnifiedEvaluationSystem");
        unifiedSystem.setPosition(fen);
    }
    
    /**
     * 🔄 MIGRATION: Get best move
     */
    public void getBestMove(String fen, int thinkTimeMs, BestMoveCallback callback) {
        Log.d(TAG, "🔄 MIGRATION: getBestMove -> UnifiedEvaluationSystem");
        
        unifiedSystem.setPosition(fen);
        unifiedSystem.getBestMove(thinkTimeMs, new UnifiedEvaluationSystem.BestMoveCallback() {
            @Override
            public void onSuccess(String move) {
                callback.onSuccess(move);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public interface BestMoveCallback {
        void onSuccess(String move);
        void onError(String error);
    }
    
    /**
     * Get the unified system instance for direct access
     */
    public UnifiedEvaluationSystem getUnifiedSystem() {
        return unifiedSystem;
    }
    
    /**
     * 🧹 Cleanup
     */
    public void shutdown() {
        Log.d(TAG, "🔄 Migration helper shutting down");
        unifiedSystem.shutdown();
    }
}