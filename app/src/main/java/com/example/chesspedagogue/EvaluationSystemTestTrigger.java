package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧪 EVALUATION SYSTEM TEST TRIGGER
 * 
 * Simple helper to integrate testing into existing activities.
 * Can be called from MainActivity, CompetitiveModeActivity, etc.
 */
public class EvaluationSystemTestTrigger {
    private static final String TAG = "EvalTestTrigger";
    
    /**
     * Run a quick test of the unified evaluation system
     */
    public static void runQuickTest(Context context) {
        Log.d(TAG, "🧪 Running quick unified evaluation test...");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // Test the exact position that caused the original bug
                String problemPosition = "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 2 2";
                
                unified.evaluatePosition(problemPosition, 1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        Log.d(TAG, "✅ Quick test PASSED: " + result);
                        Log.d(TAG, "🎯 Unified system is working correctly!");
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Quick test FAILED: " + error);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Quick test exception", e);
            }
        }).start();
    }
    
    /**
     * Run the full test suite
     */
    public static void runFullTestSuite(Context context, TestResultCallback callback) {
        Log.d(TAG, "🚀 Running full unified evaluation test suite...");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystemTest testSuite = new UnifiedEvaluationSystemTest(context);
                UnifiedEvaluationSystemTest.TestResults results = testSuite.runAllTests();
                
                if (callback != null) {
                    callback.onTestComplete(results);
                }
                
                Log.d(TAG, "🏁 Full test suite complete: " + results.passedTests + "/" + results.totalTests + " passed");
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Full test suite failed", e);
                if (callback != null) {
                    callback.onTestError(e.getMessage());
                }
            }
        }).start();
    }
    
    /**
     * Test specific regression case (the queen sacrifice bug)
     */
    public static void testQueenSacrificeBugFix(Context context) {
        Log.d(TAG, "🔥 Testing queen sacrifice bug fix...");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // The exact positions from your original bug report
                String[] testPositions = {
                    "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 2 2", // Before
                    "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQK1NR b Qkq - 3 2",  // After queen move
                    "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNB1K1NR w Qkq - 4 3"   // After queen captured
                };
                
                float[] evaluations = new float[testPositions.length];
                
                for (int i = 0; i < testPositions.length; i++) {
                    final int index = i;
                    
                    unified.evaluatePosition(testPositions[i], 1500, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            evaluations[index] = result.evaluation;
                            
                            // Check if all evaluations are done
                            boolean allDone = true;
                            for (float eval : evaluations) {
                                if (eval == 0.0f) allDone = false; // Assuming 0.0 means not set
                            }
                            
                            if (allDone) {
                                // Analyze for massive swings
                                boolean bugFixed = true;
                                for (int j = 1; j < evaluations.length; j++) {
                                    float swing = Math.abs(evaluations[j] - evaluations[j-1]);
                                    if (swing > 5.0f) {
                                        bugFixed = false;
                                        Log.e(TAG, String.format("🚨 MASSIVE SWING DETECTED: %.2f → %.2f (%.2f)", 
                                              evaluations[j-1], evaluations[j], swing));
                                    }
                                }
                                
                                if (bugFixed) {
                                    Log.d(TAG, "✅ QUEEN SACRIFICE BUG FIXED! No massive swings detected.");
                                    Log.d(TAG, String.format("📊 Evaluations: %.2f → %.2f → %.2f", 
                                          evaluations[0], evaluations[1], evaluations[2]));
                                } else {
                                    Log.e(TAG, "❌ Queen sacrifice bug still present!");
                                }
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Queen sacrifice test error: " + error);
                        }
                    });
                    
                    // Small delay between evaluations
                    Thread.sleep(500);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Queen sacrifice test failed", e);
            }
        }).start();
    }
    
    public interface TestResultCallback {
        void onTestComplete(UnifiedEvaluationSystemTest.TestResults results);
        void onTestError(String error);
    }
}