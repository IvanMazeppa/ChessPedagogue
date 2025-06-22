package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧪 EVALUATION TEST LAUNCHER
 * 
 * Quick test launcher that can be called from anywhere to verify 
 * the evaluation system is working correctly.
 * 
 * Usage:
 * EvaluationTestLauncher.quickTest(context);
 * EvaluationTestLauncher.fullTest(context);
 */
public class EvaluationTestLauncher {
    private static final String TAG = "EvaluationTestLauncher";
    
    /**
     * Run a quick subset of critical tests
     */
    public static void quickTest(Context context) {
        Log.d(TAG, "🚀 Starting QUICK evaluation tests...");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystemTest testSuite = new UnifiedEvaluationSystemTest(context);
                
                // Run only critical tests
                Log.d(TAG, "🧪 Running basic evaluation test...");
                // You could expose individual test methods if needed
                
                UnifiedEvaluationSystemTest.TestResults results = testSuite.runAllTests();
                
                Log.d(TAG, "🏁 QUICK TEST RESULTS:");
                Log.d(TAG, results.toString());
                
                if (results.allPassed) {
                    Log.d(TAG, "✅ ALL TESTS PASSED! Evaluation system is working correctly.");
                } else {
                    Log.e(TAG, "❌ SOME TESTS FAILED! Check the detailed results above.");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Quick test failed", e);
            }
        }).start();
    }
    
    /**
     * Run the complete test suite
     */
    public static void fullTest(Context context) {
        Log.d(TAG, "🚀 Starting FULL evaluation test suite...");
        
        EvaluationTestRunner.run(context, results -> {
            Log.d(TAG, "🏁 FULL TEST RESULTS:");
            Log.d(TAG, results.toString());
            
            if (results.allPassed) {
                Log.d(TAG, "✅ ALL TESTS PASSED! Evaluation system is fully verified.");
            } else {
                Log.e(TAG, "❌ SOME TESTS FAILED! Detailed results:");
                for (String result : results.results) {
                    Log.d(TAG, "  " + result);
                }
            }
        });
    }
    
    /**
     * Simple test that just checks if the system initializes
     */
    public static void smokeTest(Context context) {
        Log.d(TAG, "🔥 Running smoke test...");
        
        try {
            UnifiedEvaluationSystem system = UnifiedEvaluationSystem.getInstance(context);
            Log.d(TAG, "✅ UnifiedEvaluationSystem initialized successfully");
            
            UnifiedEvaluationIntegration integration = new UnifiedEvaluationIntegration(context);
            Log.d(TAG, "✅ UnifiedEvaluationIntegration initialized successfully");
            
            EvaluationTracker tracker = EvaluationTracker.getInstance(context);
            Log.d(TAG, "✅ EvaluationTracker initialized successfully");
            
            Log.d(TAG, "🎯 Smoke test PASSED - All components are initializing correctly");
            
        } catch (Exception e) {
            Log.e(TAG, "💥 Smoke test FAILED", e);
        }
    }
}