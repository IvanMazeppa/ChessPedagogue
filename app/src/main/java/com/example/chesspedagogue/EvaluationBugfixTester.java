package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧪 EVALUATION BUGFIX TESTER
 * 
 * Simple tester for the UCI communication bugfix.
 * Can be called from any activity to verify the fix is working.
 * 
 * Usage:
 * EvaluationBugfixTester.testFix(this);
 */
public class EvaluationBugfixTester {
    private static final String TAG = "EvalBugfixTester";
    
    /**
     * Test the evaluation bugfix with the exact problematic positions
     */
    public static void testFix(Context context) {
        Log.d(TAG, "🧪 TESTING EVALUATION BUGFIX FOR UCI COMMUNICATION");
        Log.d(TAG, "====================================================");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // Test the exact positions from the user's log that were problematic
                TestCase[] testCases = {
                    new TestCase(
                        "Queen blunder: d1g4",
                        "rnbqkb1r/ppp2ppp/3p1n2/4p3/2PPP1Q1/8/PP3PPP/RNB1KBNR b KQkq - 1 4",
                        "Should be NEGATIVE (Black can capture queen)",
                        -2.0f, -8.0f
                    ),
                    new TestCase(
                        "Bishop blunder: c1h6", 
                        "rnbqkb1r/ppp2ppp/3p3B/4p3/2PPP1n1/8/PP3PPP/RN2KBNR b KQkq - 1 5",
                        "Should be NEGATIVE (Black can capture bishop)",
                        -3.0f, -8.0f
                    ),
                    new TestCase(
                        "After queen captured",
                        "rnbqkb1r/ppp2ppp/3p4/4p3/2PPP1n1/8/PP3PPP/RNB1KBNR w KQkq - 0 5",
                        "Should be NEGATIVE (Black has extra material)",
                        -5.0f, -10.0f
                    )
                };
                
                boolean allTestsPassed = true;
                
                for (int i = 0; i < testCases.length; i++) {
                    TestCase test = testCases[i];
                    
                    Log.d(TAG, "");
                    Log.d(TAG, String.format("🎯 TEST %d: %s", i + 1, test.description));
                    Log.d(TAG, "Expected: " + test.expectation);
                    
                    unified.setPosition(test.fen);
                    
                    // Wait a moment for position to be set
                    Thread.sleep(200);
                    
                    unified.evaluateCurrentPosition(1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            float eval = result.evaluation;
                            
                            Log.d(TAG, String.format("📊 RESULT: %.2f", eval));
                            
                            boolean testPassed = eval >= test.minExpected && eval <= test.maxExpected;
                            
                            if (testPassed) {
                                Log.d(TAG, "✅ TEST PASSED: Evaluation in expected range");
                            } else {
                                Log.e(TAG, String.format("❌ TEST FAILED: Expected %.1f to %.1f, got %.2f", 
                                      test.minExpected, test.maxExpected, eval));
                                
                                if (eval > 0 && test.maxExpected < 0) {
                                    Log.e(TAG, "🚨 CRITICAL: Still showing wrong player advantage!");
                                }
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Evaluation error: " + error);
                        }
                    });
                    
                    // Wait for evaluation to complete
                    Thread.sleep(2000);
                }
                
                Log.d(TAG, "");
                Log.d(TAG, "🏁 BUGFIX TEST COMPLETE");
                Log.d(TAG, "Expected results:");
                Log.d(TAG, "✅ All evaluations should be NEGATIVE (favoring Black)");
                Log.d(TAG, "✅ No more impossible +7.41 or +8.91 evaluations");
                Log.d(TAG, "✅ Positions with pieces in danger show correct disadvantage");
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Test failed", e);
            }
        }, "EvaluationBugfixTest").start();
    }
    
    /**
     * Quick comparison test: Old vs New evaluation system
     */
    public static void compareOldVsNew(Context context) {
        Log.d(TAG, "🔄 COMPARING OLD VS NEW EVALUATION SYSTEM");
        
        new Thread(() -> {
            try {
                // Test with a problematic position
                String problematicPosition = "rnbqkb1r/ppp2ppp/3p1n2/4p3/2PPP1Q1/8/PP3PPP/RNB1KBNR b KQkq - 1 4";
                
                Log.d(TAG, "🎯 Testing position: Queen on g4 (can be captured)");
                Log.d(TAG, "Expected: Large negative evaluation (Black advantage)");
                
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                unified.setPosition(problematicPosition);
                
                Thread.sleep(500);
                
                unified.evaluateCurrentPosition(1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        Log.d(TAG, String.format("📊 NEW SYSTEM: %.2f", result.evaluation));
                        
                        if (result.evaluation < -1.0f) {
                            Log.d(TAG, "✅ SUCCESS: New system correctly shows Black advantage");
                        } else if (result.evaluation > 1.0f) {
                            Log.e(TAG, "❌ FAILURE: Still showing impossible positive evaluation");
                        } else {
                            Log.w(TAG, "⚠️ NEUTRAL: Evaluation close to zero - may need further investigation");
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ New system error: " + error);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Comparison test failed", e);
            }
        }, "ComparisonTest").start();
    }
    
    /**
     * Test case data structure
     */
    private static class TestCase {
        final String description;
        final String fen;
        final String expectation;
        final float minExpected;
        final float maxExpected;
        
        TestCase(String description, String fen, String expectation, float minExpected, float maxExpected) {
            this.description = description;
            this.fen = fen;
            this.expectation = expectation;
            this.minExpected = minExpected;
            this.maxExpected = maxExpected;
        }
    }
}