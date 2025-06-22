package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧪 POSITION FIX TEST HELPER
 * 
 * Simple helper to test the critical position evaluation fix.
 * Can be called from any activity to verify the fix is working.
 * 
 * Usage:
 * PositionFixTestHelper.testPositionFix(this);
 */
public class PositionFixTestHelper {
    private static final String TAG = "PositionFixTest";
    
    /**
     * Test the position evaluation fix with the exact scenario that was broken
     */
    public static void testPositionFix(Context context) {
        Log.d(TAG, "🧪 TESTING POSITION EVALUATION FIX");
        Log.d(TAG, "==========================================");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // Test positions from the user's original bug report
                String[] testPositions = {
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",  // Starting position
                    "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 2 2",  // After some moves
                    "rnbqkb1r/1pp1pppQ/p4n2/3p4/2PP4/8/PP2PPPP/RNB1KBNR b KQkq - 0 4"   // After queen sacrifice (should show material loss)
                };
                
                String[] testDescriptions = {
                    "Starting position (should be ~0.0)",
                    "After knight development (should be small advantage)",
                    "After queen sacrifice (should show MAJOR material disadvantage)"
                };
                
                for (int i = 0; i < testPositions.length; i++) {
                    final int testIndex = i;
                    final String position = testPositions[i];
                    final String description = testDescriptions[i];
                    
                    Log.d(TAG, "");
                    Log.d(TAG, "🎯 TEST " + (testIndex + 1) + ": " + description);
                    Log.d(TAG, "Position: " + position.substring(0, Math.min(40, position.length())) + "...");
                    
                    // 🔧 CRITICAL: Set the position BEFORE evaluating (this is the fix!)
                    unified.setPosition(position);
                    Log.d(TAG, "✅ Position set to: " + position.substring(0, 20) + "...");
                    
                    // Wait a moment for the position to be set
                    Thread.sleep(100);
                    
                    // Now evaluate the current position
                    unified.evaluateCurrentPosition(1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            String status = "UNKNOWN";
                            
                            // Analyze result based on test expectations
                            if (testIndex == 0) {
                                // Starting position should be close to 0
                                status = Math.abs(result.evaluation) < 0.5f ? "✅ CORRECT" : "❌ WRONG";
                            } else if (testIndex == 1) {
                                // Should be small advantage
                                status = Math.abs(result.evaluation) < 2.0f ? "✅ REASONABLE" : "❌ SUSPICIOUS";
                            } else if (testIndex == 2) {
                                // After queen sacrifice, should show material disadvantage
                                // Since queen was sacrificed, evaluation should be significantly negative for the side that sacrificed
                                status = Math.abs(result.evaluation) > 1.0f ? "✅ SHOWS MATERIAL LOSS" : "❌ DOESN'T REFLECT SACRIFICE";
                            }
                            
                            Log.d(TAG, String.format("📊 RESULT %d: %s evaluation=%.2f %s", 
                                  testIndex + 1, status, result.evaluation,
                                  result.fromCache ? "[CACHED]" : "[FRESH]"));
                            
                            if (testIndex == 0 && Math.abs(result.evaluation) > 2.0f) {
                                Log.e(TAG, "🚨 MAJOR ISSUE: Starting position shows " + result.evaluation + " - this suggests wrong position!");
                            }
                            
                            if (testIndex == 2 && Math.abs(result.evaluation) < 0.5f) {
                                Log.e(TAG, "🚨 POSITION BUG: Queen sacrifice shows " + result.evaluation + " - position not updated!");
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ TEST " + (testIndex + 1) + " FAILED: " + error);
                        }
                    });
                    
                    // Wait between tests
                    Thread.sleep(2000);
                }
                
                // Final verification
                Thread.sleep(1000);
                Log.d(TAG, "");
                Log.d(TAG, "🏁 POSITION FIX TEST COMPLETE");
                Log.d(TAG, "✅ If you see 'SHOWS MATERIAL LOSS' for test 3, the fix is working!");
                Log.d(TAG, "❌ If test 3 shows ~0.0 evaluation, the position bug still exists.");
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Position fix test failed", e);
            }
        }).start();
    }
    
    /**
     * Quick test to verify UnifiedEvaluationSystem is working
     */
    public static void quickTest(Context context) {
        Log.d(TAG, "🚀 QUICK UNIFIED SYSTEM TEST");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // Test the exact position from user's bug report
                String testPosition = "rnbqkb1r/1pp1pppQ/p4n2/3p4/2PP4/8/PP2PPPP/RNB1KBNR b KQkq - 0 4";
                
                Log.d(TAG, "🎯 Testing position: " + testPosition.substring(0, 30) + "...");
                
                unified.setPosition(testPosition);
                unified.evaluateCurrentPosition(500, new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        Log.d(TAG, "✅ QUICK TEST RESULT: " + result.evaluation);
                        
                        if (Math.abs(result.evaluation) > 1.0f) {
                            Log.d(TAG, "🎉 SUCCESS! Position fix is working - evaluation reflects material state");
                        } else {
                            Log.e(TAG, "⚠️  WARNING: Evaluation " + result.evaluation + " doesn't reflect queen sacrifice");
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Quick test failed: " + error);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Quick test exception", e);
            }
        }).start();
    }
}