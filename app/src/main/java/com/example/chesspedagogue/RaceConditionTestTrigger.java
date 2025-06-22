package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧪 RACE CONDITION TEST TRIGGER
 * 
 * Simple way to test the race condition fix without complex setup.
 * Call from any activity to verify the evaluation swing bug is fixed.
 * 
 * Usage:
 * RaceConditionTestTrigger.testRaceConditionFix(this);
 */
public class RaceConditionTestTrigger {
    private static final String TAG = "RaceConditionTest";
    
    /**
     * Test the race condition fix - Call this from any activity
     */
    public static void testRaceConditionFix(Context context) {
        Log.d(TAG, "🧪 TESTING RACE CONDITION FIX FOR EVALUATION SWINGS");
        Log.d(TAG, "==================================================");
        
        new Thread(() -> {
            try {
                // Run quick diagnostic
                EvaluationSwingDiagnostic.quickDiagnostic(context);
                
                // Wait a bit, then run full diagnostic
                Thread.sleep(2000);
                EvaluationSwingDiagnostic.runDiagnostic(context);
                
                Log.d(TAG, "🏁 Race condition test completed - check logs for results");
                Log.d(TAG, "✅ If you see consistent evaluations, the fix is working!");
                Log.d(TAG, "❌ If you see large swings (>3.0), the issue persists.");
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Race condition test failed", e);
            }
        }, "RaceConditionTestThread").start();
    }
    
    /**
     * Quick test that simulates the original bug scenario
     */
    public static void testOriginalBugScenario(Context context) {
        Log.d(TAG, "🎯 TESTING ORIGINAL BUG SCENARIO");
        Log.d(TAG, "Simulating the moves that caused +5.46 → -5.69 → +7.90 swings");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // The positions from your original log that caused wild swings
                String[] problematicPositions = {
                    "rn2kbnr/ppp2pp1/3q3p/8/2P3b1/4P3/PP1B1PPP/RN1QKBNR b KQkq - 0 6",  // Was: 0.78 → 5.46
                    "rn2kbnr/ppp2pp1/3q3p/8/2P5/4P3/PP1B1PPP/RN1bKBNR w KQkq - 0 7",   // Was: 5.46 → -5.69
                    "rn2kbnr/ppp2pp1/3q3p/8/2P5/3BP3/PP1B1PPP/RN1bK1NR b KQkq - 1 7"    // Was: -5.69 → 7.90
                };
                
                float[] evaluations = new float[problematicPositions.length];
                
                for (int i = 0; i < problematicPositions.length; i++) {
                    final int index = i;
                    final String position = problematicPositions[i];
                    
                    Log.d(TAG, String.format("🎯 Testing position %d: %s...", index + 1, position.substring(0, 30)));
                    
                    unified.setPosition(position);
                    unified.evaluateCurrentPosition(1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            evaluations[index] = result.evaluation;
                            
                            Log.d(TAG, String.format("📊 Position %d result: %.2f", index + 1, result.evaluation));
                            
                            // Check if all positions have been evaluated
                            boolean allEvaluated = true;
                            for (float eval : evaluations) {
                                if (eval == 0.0f) {
                                    allEvaluated = false;
                                    break;
                                }
                            }
                            
                            if (allEvaluated) {
                                // Analyze swings
                                Log.d(TAG, "📈 SWING ANALYSIS:");
                                for (int j = 1; j < evaluations.length; j++) {
                                    float swing = Math.abs(evaluations[j] - evaluations[j-1]);
                                    Log.d(TAG, String.format("   Position %d→%d: %.2f → %.2f (swing: %.2f)", 
                                          j, j+1, evaluations[j-1], evaluations[j], swing));
                                    
                                    if (swing > 5.0f) {
                                        Log.e(TAG, String.format("🚨 MASSIVE SWING DETECTED: %.2f (STILL BROKEN!)", swing));
                                    } else if (swing > 2.0f) {
                                        Log.w(TAG, String.format("⚠️ Large swing: %.2f (suspicious)", swing));
                                    } else {
                                        Log.d(TAG, String.format("✅ Reasonable swing: %.2f", swing));
                                    }
                                }
                                
                                // Overall verdict
                                float maxSwing = 0f;
                                for (int j = 1; j < evaluations.length; j++) {
                                    maxSwing = Math.max(maxSwing, Math.abs(evaluations[j] - evaluations[j-1]));
                                }
                                
                                if (maxSwing < 3.0f) {
                                    Log.d(TAG, "🎉 SUCCESS! Race condition fix is working - no impossible swings detected!");
                                } else {
                                    Log.e(TAG, "💥 FAILURE! Race condition still exists - impossible swings detected!");
                                }
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, String.format("❌ Position %d error: %s", index + 1, error));
                        }
                    });
                    
                    // Wait between evaluations to simulate real gameplay
                    Thread.sleep(1500);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Original bug scenario test failed", e);
            }
        }, "OriginalBugTest").start();
    }
    
    /**
     * Test that can be called from MainActivity onCreate() for automatic testing
     */
    public static void autoTest(Context context) {
        Log.d(TAG, "🤖 AUTO-TESTING RACE CONDITION FIX ON STARTUP");
        
        // Delay to let app initialize
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                
                // Quick test first
                testRaceConditionFix(context);
                
                // Wait, then test original scenario
                Thread.sleep(5000);
                testOriginalBugScenario(context);
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Auto-test failed", e);
            }
        }, "AutoTestThread").start();
    }
}