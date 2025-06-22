package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * 🧪 EVALUATION TEST RUNNER
 * 
 * Simple test runner that can be called from anywhere in the app
 * to execute the UnifiedEvaluationSystemTest suite.
 * 
 * Usage:
 * EvaluationTestRunner.run(context, results -> {
 *     Log.d("TEST", results.toString());
 * });
 */
public class EvaluationTestRunner {
    private static final String TAG = "EvaluationTestRunner";
    
    public interface TestCompletionCallback {
        void onTestsComplete(UnifiedEvaluationSystemTest.TestResults results);
    }
    
    /**
     * Run the evaluation tests asynchronously
     */
    public static void run(Context context, TestCompletionCallback callback) {
        Log.d(TAG, "🚀 Starting evaluation test suite...");
        
        // Run tests on background thread
        new Thread(() -> {
            try {
                UnifiedEvaluationSystemTest testSuite = new UnifiedEvaluationSystemTest(context);
                UnifiedEvaluationSystemTest.TestResults results = testSuite.runAllTests();
                
                // Return results on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onTestsComplete(results);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Test suite failed", e);
                
                // Return error on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    UnifiedEvaluationSystemTest.TestResults errorResults = 
                        new UnifiedEvaluationSystemTest.TestResults(0, 0, 
                            java.util.Arrays.asList("💥 Test suite failed: " + e.getMessage()));
                    callback.onTestsComplete(errorResults);
                });
            }
        }).start();
    }
    
    /**
     * Run tests synchronously (blocks current thread)
     */
    public static UnifiedEvaluationSystemTest.TestResults runSync(Context context) {
        Log.d(TAG, "🧪 Running evaluation tests synchronously...");
        
        try {
            UnifiedEvaluationSystemTest testSuite = new UnifiedEvaluationSystemTest(context);
            return testSuite.runAllTests();
        } catch (Exception e) {
            Log.e(TAG, "💥 Synchronous test failed", e);
            return new UnifiedEvaluationSystemTest.TestResults(0, 0, 
                java.util.Arrays.asList("💥 Test suite failed: " + e.getMessage()));
        }
    }
}