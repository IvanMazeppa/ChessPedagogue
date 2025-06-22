package com.example.chesspedagogue;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;

/**
 * 🧪 UNIFIED EVALUATION SYSTEM TEST ACTIVITY
 * 
 * Simple test interface to run the comprehensive test suite
 * and verify that the unified system is working correctly.
 */
public class UnifiedEvaluationTestActivity extends Activity {
    private static final String TAG = "UnifiedEvalTestActivity";
    
    private TextView resultsTextView;
    private Button runTestsButton;
    private ScrollView scrollView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple UI
        createTestUI();
        
        Log.d(TAG, "🧪 Unified Evaluation Test Activity started");
    }
    
    private void createTestUI() {
        // Use our custom layout
        setContentView(R.layout.activity_unified_evaluation_test);
        
        // Get references to UI components
        runTestsButton = findViewById(R.id.button_run_tests);
        resultsTextView = findViewById(R.id.text_results);
        scrollView = findViewById(R.id.scroll_view); // We'll add this ID if needed
        
        // Set up button click listener
        runTestsButton.setOnClickListener(v -> runTests());
    }
    
    private void runTests() {
        if (runTestsButton != null) {
            runTestsButton.setEnabled(false);
            runTestsButton.setText("🔄 Running Tests...");
        }
        
        if (resultsTextView != null) {
            resultsTextView.setText("🚀 Starting comprehensive test suite...\n\n");
        }
        
        // Run tests on background thread
        new Thread(() -> {
            try {
                UnifiedEvaluationSystemTest testSuite = new UnifiedEvaluationSystemTest(this);
                UnifiedEvaluationSystemTest.TestResults results = testSuite.runAllTests();
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    if (resultsTextView != null) {
                        resultsTextView.setText(results.toString());
                    }
                    
                    if (runTestsButton != null) {
                        runTestsButton.setEnabled(true);
                        runTestsButton.setText(results.allPassed ? "✅ All Tests Passed!" : "⚠️ Some Tests Failed - See Results");
                    }
                    
                    Log.d(TAG, "🏁 Test results: " + results);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Test suite failed", e);
                
                runOnUiThread(() -> {
                    if (resultsTextView != null) {
                        resultsTextView.setText("💥 Test suite failed: " + e.getMessage());
                    }
                    
                    if (runTestsButton != null) {
                        runTestsButton.setEnabled(true);
                        runTestsButton.setText("❌ Test Failed - Try Again");
                    }
                });
            }
        }).start();
    }
}