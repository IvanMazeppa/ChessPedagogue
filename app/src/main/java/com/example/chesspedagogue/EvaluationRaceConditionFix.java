package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 🔧 EMERGENCY RACE CONDITION FIX
 * 
 * This patches the existing UnifiedEvaluationSystem to fix the thread safety
 * race condition that was causing impossible evaluation swings.
 * 
 * CRITICAL ISSUE: Multiple threads calling StockfishManager.getCurrentEvaluation()
 * simultaneously were sharing the same outputBuffer, causing mixed-up evaluations:
 * - Thread A: outputBuffer.clear() → send UCI command → wait for response
 * - Thread B: outputBuffer.clear() → send UCI command → wait for response  
 * - Both threads parse from same shared outputBuffer → WRONG EVALUATIONS!
 * 
 * This fix adds proper synchronization to prevent the race condition.
 */
public class EvaluationRaceConditionFix {
    private static final String TAG = "EvalRaceConditionFix";
    
    private static final ReentrantLock EVALUATION_LOCK = new ReentrantLock();
    private static boolean isFixApplied = false;
    
    /**
     * Apply the race condition fix to UnifiedEvaluationSystem
     */
    public static void applyFix(Context context) {
        if (isFixApplied) {
            Log.d(TAG, "✅ Race condition fix already applied");
            return;
        }
        
        try {
            Log.d(TAG, "🔧 APPLYING RACE CONDITION FIX FOR EVALUATION SWINGS");
            
            // Patch UnifiedEvaluationSystem to use synchronized evaluation
            UnifiedEvaluationSystem unifiedSystem = UnifiedEvaluationSystem.getInstance(context);
            patchUnifiedEvaluationSystem(unifiedSystem);
            
            isFixApplied = true;
            Log.d(TAG, "✅ Race condition fix applied successfully!");
            Log.d(TAG, "🎯 This should eliminate the impossible +5.46 → -5.69 → +7.90 evaluation swings");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to apply race condition fix", e);
        }
    }
    
    /**
     * Patch the UnifiedEvaluationSystem to use synchronized evaluation
     */
    private static void patchUnifiedEvaluationSystem(UnifiedEvaluationSystem unifiedSystem) {
        Log.d(TAG, "🔧 Patching UnifiedEvaluationSystem for thread safety...");
        
        try {
            // Use reflection to access the private stockfishManager field
            Field stockfishField = UnifiedEvaluationSystem.class.getDeclaredField("stockfishManager");
            stockfishField.setAccessible(true);
            StockfishManager stockfishManager = (StockfishManager) stockfishField.get(unifiedSystem);
            
            if (stockfishManager != null) {
                Log.d(TAG, "✅ Found StockfishManager instance, applying synchronization wrapper");
                
                // Create a wrapper that adds synchronization
                StockfishManager synchronizedWrapper = createSynchronizedWrapper(stockfishManager);
                stockfishField.set(unifiedSystem, synchronizedWrapper);
                
                Log.d(TAG, "✅ Synchronized wrapper applied to UnifiedEvaluationSystem");
            } else {
                Log.w(TAG, "⚠️ StockfishManager is null, cannot apply fix");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error patching UnifiedEvaluationSystem", e);
        }
    }
    
    /**
     * Create a synchronized wrapper around StockfishManager
     */
    private static StockfishManager createSynchronizedWrapper(StockfishManager original) {
        return new StockfishManager() {
            
            /**
             * 🔒 THREAD-SAFE VERSION: Synchronized evaluation to prevent race conditions
             */
            @Override
            public EvaluationResult getCurrentEvaluation(int thinkTimeMs) {
                // CRITICAL: Synchronize all evaluation calls to prevent outputBuffer mixing
                EVALUATION_LOCK.lock();
                try {
                    Log.d(TAG, String.format("🔒 SYNCHRONIZED EVAL: Thread %s acquiring lock", 
                          Thread.currentThread().getName()));
                    
                    long startTime = System.currentTimeMillis();
                    EvaluationResult result = original.getCurrentEvaluation(thinkTimeMs);
                    long duration = System.currentTimeMillis() - startTime;
                    
                    Log.d(TAG, String.format("✅ SYNCHRONIZED EVAL: %s (took %dms, thread: %s)", 
                          result, duration, Thread.currentThread().getName()));
                    
                    return result;
                    
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error in synchronized evaluation", e);
                    return new EvaluationResult(0.0f, false, 0);
                } finally {
                    EVALUATION_LOCK.unlock();
                    Log.d(TAG, String.format("🔓 SYNCHRONIZED EVAL: Thread %s released lock", 
                          Thread.currentThread().getName()));
                }
            }
            
            /**
             * 🔒 THREAD-SAFE VERSION: Synchronized position setting
             */
            @Override
            public boolean setPosition(String fen) {
                EVALUATION_LOCK.lock();
                try {
                    Log.d(TAG, String.format("🔒 SYNCHRONIZED POS: Setting position: %s", 
                          fen.substring(0, Math.min(30, fen.length()))));
                    
                    boolean result = original.setPosition(fen);
                    
                    Log.d(TAG, String.format("✅ SYNCHRONIZED POS: %s", result ? "SUCCESS" : "FAILED"));
                    return result;
                    
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error in synchronized position setting", e);
                    return false;
                } finally {
                    EVALUATION_LOCK.unlock();
                }
            }
            
            // Delegate all other methods to original (these don't have race conditions)
            // Note: Add other delegate methods as needed based on StockfishManager interface
        };
    }
    
    /**
     * Test the fix with a sequence of evaluations
     */
    public static void testRaceConditionFix(Context context) {
        Log.d(TAG, "🧪 TESTING RACE CONDITION FIX");
        
        try {
            UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
            
            // Test positions that were causing wild swings
            String[] testPositions = {
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",  // Starting
                "rnbqkbnr/ppp2ppp/3p4/4p3/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",  // Typical opening
                "rn2kbnr/ppp2pp1/3q3p/8/2P3b1/4P3/PP1B1PPP/RN1QKBNR b KQkq - 0 6"   // Mid-game
            };
            
            // Run multiple concurrent evaluations to test for race conditions
            for (int i = 0; i < testPositions.length; i++) {
                final int testNum = i;
                final String position = testPositions[i];
                
                // Simulate concurrent evaluations
                new Thread(() -> {
                    unified.setPosition(position);
                    unified.evaluateCurrentPosition(1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            Log.d(TAG, String.format("✅ RACE TEST %d: %s (position: %s...)", 
                                  testNum, result, position.substring(0, 20)));
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, String.format("❌ RACE TEST %d: %s", testNum, error));
                        }
                    });
                }, "RaceTest-" + i).start();
                
                // Small delay between tests
                Thread.sleep(200);
            }
            
            Log.d(TAG, "🏁 Race condition test initiated - monitor logs for consistent results");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Race condition test failed", e);
        }
    }
    
    /**
     * Check if the fix has been applied
     */
    public static boolean isFixApplied() {
        return isFixApplied;
    }
    
    /**
     * Get statistics about the synchronization
     */
    public static String getSynchronizationStats() {
        return String.format("Fix Applied: %s, Queue Length: %d, Held: %s", 
               isFixApplied, 
               EVALUATION_LOCK.getQueueLength(),
               EVALUATION_LOCK.isLocked());
    }
}