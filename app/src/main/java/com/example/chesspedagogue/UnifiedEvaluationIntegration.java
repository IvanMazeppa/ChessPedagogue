package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🎯 UNIFIED EVALUATION INTEGRATION HELPER
 * 
 * Simplifies integration of UnifiedEvaluationSystem with UI components.
 * Provides easy-to-use methods for common evaluation scenarios.
 * 
 * Key Features:
 * ✅ EvaluationBarView integration
 * ✅ Real-time evaluation updates
 * ✅ Automatic perspective handling
 * ✅ Thread-safe operations
 * ✅ Caching for performance
 */
public class UnifiedEvaluationIntegration {
    private static final String TAG = "UnifiedEvalIntegration";
    
    private final UnifiedEvaluationSystem unifiedSystem;
    private final Context context;
    
    // UI Components
    private EvaluationBarView evaluationBarView;
    private EvaluationTracker evaluationTracker;
    
    // Current state
    private String currentPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private UnifiedEvaluationSystem.EvaluationResult lastEvaluation;
    
    public UnifiedEvaluationIntegration(Context context) {
        this.context = context;
        this.unifiedSystem = UnifiedEvaluationSystem.getInstance(context);
        Log.d(TAG, "🎯 Integration helper initialized");
    }
    
    /**
     * Connect an EvaluationBarView for automatic updates
     */
    public void connectEvaluationBar(EvaluationBarView evaluationBar) {
        this.evaluationBarView = evaluationBar;
        Log.d(TAG, "📊 EvaluationBarView connected");
        
        // Set initial position if we have a recent evaluation
        if (lastEvaluation != null) {
            updateEvaluationBar(lastEvaluation);
        }
    }
    
    /**
     * Connect an EvaluationTracker for swing detection
     */
    public void connectEvaluationTracker(EvaluationTracker tracker) {
        this.evaluationTracker = tracker;
        Log.d(TAG, "📈 EvaluationTracker connected");
    }
    
    /**
     * 🎯 PRIMARY METHOD: Update position and trigger evaluation
     * This should be called whenever the board position changes
     */
    public void updatePosition(String newFen, String lastMove) {
        if (newFen == null || newFen.equals(currentPosition)) {
            return; // No change needed
        }
        
        Log.d(TAG, "📍 Position update: " + newFen);
        currentPosition = newFen;
        
        // Update unified system position
        unifiedSystem.setPosition(newFen);
        
        // Trigger evaluation with UI updates
        evaluateWithUIUpdates(newFen, lastMove, 1000);
    }
    
    /**
     * Quick evaluation for UI responsiveness
     */
    public void quickEvaluation(String fen) {
        evaluateWithUIUpdates(fen, null, 500);
    }
    
    /**
     * Deep evaluation for accurate analysis
     */
    public void deepEvaluation(String fen, String lastMove) {
        evaluateWithUIUpdates(fen, lastMove, 2000);
    }
    
    /**
     * Core evaluation method with automatic UI updates
     */
    private void evaluateWithUIUpdates(String fen, String lastMove, int thinkTimeMs) {
        unifiedSystem.evaluatePosition(fen, thinkTimeMs, new UnifiedEvaluationSystem.EvaluationCallback() {
            @Override
            public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                Log.d(TAG, "✅ Evaluation complete: " + result + 
                          " (position: " + fen.substring(0, Math.min(20, fen.length())) + "...)");
                
                // Store result
                lastEvaluation = result;
                
                // Update UI components
                updateUIComponents(result, fen, lastMove);
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Evaluation failed: " + error);
                // Could show error state in UI if needed
            }
        });
    }
    
    /**
     * Update all connected UI components with new evaluation
     */
    private void updateUIComponents(UnifiedEvaluationSystem.EvaluationResult result, String fen, String lastMove) {
        // Update evaluation bar
        if (evaluationBarView != null) {
            updateEvaluationBar(result);
        }
        
        // Update evaluation tracker for swing detection
        if (evaluationTracker != null) {
            evaluationTracker.trackEvaluation(result, fen, lastMove);
        }
    }
    
    /**
     * Update the evaluation bar with proper perspective handling
     */
    private void updateEvaluationBar(UnifiedEvaluationSystem.EvaluationResult result) {
        if (result.isMate) {
            evaluationBarView.setMateEvaluation(result.mateInMoves);
            Log.d(TAG, "📊 Bar updated: M" + result.mateInMoves);
        } else {
            // Use the raw evaluation (already in correct UCI perspective)
            evaluationBarView.setEvaluation(result.evaluation);
            Log.d(TAG, "📊 Bar updated: " + result.evaluation);
        }
    }
    
    /**
     * Get the most recent evaluation synchronously
     */
    public UnifiedEvaluationSystem.EvaluationResult getLastEvaluation() {
        return lastEvaluation;
    }
    
    /**
     * Get current position FEN
     */
    public String getCurrentPosition() {
        return currentPosition;
    }
    
    /**
     * Check if an evaluation is currently in progress
     */
    public boolean isEvaluating() {
        return unifiedSystem.isEvaluating();
    }
    
    /**
     * Manual evaluation request (for special cases)
     */
    public void requestEvaluation(EvaluationCallback callback) {
        unifiedSystem.evaluateCurrentPosition(1000, new UnifiedEvaluationSystem.EvaluationCallback() {
            @Override
            public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                lastEvaluation = result;
                if (callback != null) {
                    callback.onEvaluationComplete(result);
                }
                updateUIComponents(result, currentPosition, null);
            }
            
            @Override
            public void onError(String error) {
                if (callback != null) {
                    callback.onEvaluationError(error);
                }
            }
        });
    }
    
    /**
     * Callback interface for manual evaluation requests
     */
    public interface EvaluationCallback {
        void onEvaluationComplete(UnifiedEvaluationSystem.EvaluationResult result);
        void onEvaluationError(String error);
    }
    
    /**
     * Reset to starting position
     */
    public void resetToStartingPosition() {
        String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        updatePosition(startingFen, null);
        
        // Reset evaluation bar
        if (evaluationBarView != null) {
            evaluationBarView.reset();
        }
        
        // Reset evaluation tracker
        if (evaluationTracker != null) {
            evaluationTracker.resetTracking();
        }
        
        Log.d(TAG, "🔄 Reset to starting position");
    }
    
    /**
     * Get evaluation cache statistics
     */
    public String getCacheStats() {
        return unifiedSystem.getCacheStats();
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        evaluationBarView = null;
        evaluationTracker = null;
        lastEvaluation = null;
        Log.d(TAG, "🧹 Integration helper cleanup complete");
    }
}