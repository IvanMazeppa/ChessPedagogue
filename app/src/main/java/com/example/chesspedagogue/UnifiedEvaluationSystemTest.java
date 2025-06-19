package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 🧪 UNIFIED EVALUATION SYSTEM TEST SUITE
 * 
 * Comprehensive testing to verify that the unified system:
 * ✅ Eliminates impossible evaluation swings
 * ✅ Maintains consistent UCI perspective
 * ✅ Integrates properly with UI components
 * ✅ Handles edge cases gracefully
 * 
 * Run this after implementing the unified system to ensure quality.
 */
public class UnifiedEvaluationSystemTest {
    private static final String TAG = "UnifiedEvalTest";
    
    private final Context context;
    private final UnifiedEvaluationSystem unifiedSystem;
    private final UnifiedEvaluationIntegration integration;
    
    // Test results
    private final List<String> testResults = new ArrayList<>();
    private int testsRun = 0;
    private int testsPassed = 0;
    
    public UnifiedEvaluationSystemTest(Context context) {
        this.context = context;
        this.unifiedSystem = UnifiedEvaluationSystem.getInstance(context);
        this.integration = new UnifiedEvaluationIntegration(context);
        Log.d(TAG, "🧪 Test suite initialized");
    }
    
    /**
     * Run all tests and return comprehensive results
     */
    public TestResults runAllTests() {
        Log.d(TAG, "🚀 Starting comprehensive unified evaluation system tests...");
        
        testResults.clear();
        testsRun = 0;
        testsPassed = 0;
        
        // Core functionality tests
        testBasicEvaluation();
        testPerspectiveConsistency();
        testCaching();
        testErrorHandling();
        
        // Integration tests
        testUIIntegration();
        testEvaluationTracker();
        
        // Regression tests for the original bug
        testQueenSacrificeBug();
        testMassiveSwingsPrevention();
        
        // Performance tests
        testConcurrentEvaluations();
        
        Log.d(TAG, String.format("🏁 Test suite complete: %d/%d tests passed", testsPassed, testsRun));
        
        return new TestResults(testsRun, testsPassed, new ArrayList<>(testResults));
    }
    
    /**
     * Test basic evaluation functionality
     */
    private void testBasicEvaluation() {
        runTest("Basic Evaluation", () -> {
            CountDownLatch latch = new CountDownLatch(1);
            final boolean[] success = {false};
            
            unifiedSystem.evaluatePosition(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 
                1000,
                new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        // Starting position should be close to 0.0
                        success[0] = Math.abs(result.evaluation) < 0.5f;
                        Log.d(TAG, "Basic evaluation result: " + result.evaluation);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Basic evaluation failed: " + error);
                        latch.countDown();
                    }
                });
            
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return success[0];
        });
    }
    
    /**
     * Test that perspective remains consistent (UCI standard)
     */
    private void testPerspectiveConsistency() {
        runTest("Perspective Consistency", () -> {
            CountDownLatch latch = new CountDownLatch(2);
            final Float[] whiteAdvantageEval = {null};
            final Float[] blackAdvantageEval = {null};
            
            // Test position where White has clear advantage
            unifiedSystem.evaluatePosition(
                "rnbqkb1r/pppp1ppp/5n2/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 2 3", // Italian Game
                1000,
                new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        whiteAdvantageEval[0] = result.evaluation;
                        Log.d(TAG, "White advantage position: " + result.evaluation);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "White advantage test failed: " + error);
                        latch.countDown();
                    }
                });
            
            // Test position where Black has clear advantage
            unifiedSystem.evaluatePosition(
                "rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 2 2", // Black to play with advantage
                1000,
                new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        blackAdvantageEval[0] = result.evaluation;
                        Log.d(TAG, "Black advantage position: " + result.evaluation);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Black advantage test failed: " + error);
                        latch.countDown();
                    }
                });
            
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Both evaluations should be from White's perspective
            // Positive = White advantage, Negative = Black advantage
            boolean consistent = whiteAdvantageEval[0] != null && blackAdvantageEval[0] != null &&
                               whiteAdvantageEval[0] > 0 && blackAdvantageEval[0] < 0;
            
            Log.d(TAG, String.format("Perspective test: White=%.2f, Black=%.2f, Consistent=%s",
                  whiteAdvantageEval[0], blackAdvantageEval[0], consistent));
            
            return consistent;
        });
    }
    
    /**
     * Test evaluation caching
     */
    private void testCaching() {
        runTest("Evaluation Caching", () -> {
            String testFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
            CountDownLatch latch = new CountDownLatch(2);
            final long[] firstTime = {0};
            final long[] secondTime = {0};
            final boolean[] sameResult = {false};
            
            // First evaluation (should be fresh)
            long start1 = System.currentTimeMillis();
            unifiedSystem.evaluatePosition(testFen, 1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                @Override
                public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                    firstTime[0] = System.currentTimeMillis() - start1;
                    Log.d(TAG, "First eval: " + result.evaluation + " (fromCache: " + result.fromCache + ")");
                    
                    // Second evaluation (should use cache)
                    long start2 = System.currentTimeMillis();
                    unifiedSystem.evaluatePosition(testFen, 1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result2) {
                            secondTime[0] = System.currentTimeMillis() - start2;
                            sameResult[0] = Math.abs(result.evaluation - result2.evaluation) < 0.01f;
                            Log.d(TAG, "Second eval: " + result2.evaluation + " (fromCache: " + result2.fromCache + ")");
                            latch.countDown();
                        }
                        
                        @Override
                        public void onError(String error) {
                            latch.countDown();
                        }
                    });
                    latch.countDown();
                }
                
                @Override
                public void onError(String error) {
                    latch.countDown();
                }
            });
            
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Second evaluation should be faster (cached) and same result
            boolean cachingWorks = secondTime[0] < firstTime[0] && sameResult[0];
            Log.d(TAG, String.format("Caching test: First=%dms, Second=%dms, Same=%s, Works=%s",
                  firstTime[0], secondTime[0], sameResult[0], cachingWorks));
            
            return cachingWorks;
        });
    }
    
    /**
     * Test error handling
     */
    private void testErrorHandling() {
        runTest("Error Handling", () -> {
            CountDownLatch latch = new CountDownLatch(1);
            final boolean[] errorHandled = {false};
            
            // Test with invalid FEN
            unifiedSystem.evaluatePosition("invalid_fen", 1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                @Override
                public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                    Log.w(TAG, "Unexpected success with invalid FEN");
                    latch.countDown();
                }
                
                @Override
                public void onError(String error) {
                    errorHandled[0] = error != null && !error.isEmpty();
                    Log.d(TAG, "Error correctly handled: " + error);
                    latch.countDown();
                }
            });
            
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return errorHandled[0];
        });
    }
    
    /**
     * Test UI integration
     */
    private void testUIIntegration() {
        runTest("UI Integration", () -> {
            // Test integration helper
            String testFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
            integration.updatePosition(testFen, "e2e4");
            
            // Check if position was updated
            String currentPos = integration.getCurrentPosition();
            boolean positionUpdated = testFen.equals(currentPos);
            
            Log.d(TAG, "UI Integration test: Position updated = " + positionUpdated);
            return positionUpdated;
        });
    }
    
    /**
     * Test EvaluationTracker integration
     */
    private void testEvaluationTracker() {
        runTest("EvaluationTracker Integration", () -> {
            try {
                EvaluationTracker tracker = EvaluationTracker.getInstance(context);
                integration.connectEvaluationTracker(tracker);
                
                // This is more of a connectivity test
                Log.d(TAG, "EvaluationTracker connected successfully");
                return true;
            } catch (Exception e) {
                Log.e(TAG, "EvaluationTracker integration failed", e);
                return false;
            }
        });
    }
    
    /**
     * 🔥 REGRESSION TEST: Verify queen sacrifice doesn't cause impossible swings
     */
    private void testQueenSacrificeBug() {
        runTest("Queen Sacrifice Bug Fix", () -> {
            CountDownLatch latch = new CountDownLatch(3);
            final List<Float> evaluations = new ArrayList<>();
            
            // Test the exact scenario from the original bug report
            String[] positions = {
                "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 2 2", // Before sacrifice
                "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQK1NR b Qkq - 3 2",  // After queen moved
                "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNB1K1NR w Qkq - 4 3"   // After queen captured
            };
            
            for (String fen : positions) {
                unifiedSystem.evaluatePosition(fen, 1000, new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        evaluations.add(result.evaluation);
                        Log.d(TAG, "Queen sacrifice test eval: " + result.evaluation);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Queen sacrifice test error: " + error);
                        latch.countDown();
                    }
                });
            }
            
            try {
                latch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Check for impossible swings (>5.0 evaluation change)
            boolean noMassiveSwings = true;
            for (int i = 1; i < evaluations.size(); i++) {
                float swing = Math.abs(evaluations.get(i) - evaluations.get(i-1));
                if (swing > 5.0f) {
                    noMassiveSwings = false;
                    Log.e(TAG, String.format("MASSIVE SWING DETECTED: %.2f -> %.2f (swing: %.2f)", 
                          evaluations.get(i-1), evaluations.get(i), swing));
                }
            }
            
            Log.d(TAG, String.format("Queen sacrifice test: %s (evaluations: %s)", 
                  noMassiveSwings ? "PASSED" : "FAILED", evaluations));
            
            return noMassiveSwings;
        });
    }
    
    /**
     * Test prevention of massive evaluation swings
     */
    private void testMassiveSwingsPrevention() {
        runTest("Massive Swings Prevention", () -> {
            CountDownLatch latch = new CountDownLatch(5);
            final List<Float> evaluations = new ArrayList<>();
            
            // Test rapid position changes
            String[] rapidPositions = {
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", 
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
                "rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3"
            };
            
            for (String fen : rapidPositions) {
                unifiedSystem.evaluatePosition(fen, 500, new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        evaluations.add(result.evaluation);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String error) {
                        latch.countDown();
                    }
                });
            }
            
            try {
                latch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Verify no impossible swings between consecutive evaluations
            boolean stable = true;
            for (int i = 1; i < evaluations.size(); i++) {
                float swing = Math.abs(evaluations.get(i) - evaluations.get(i-1));
                if (swing > 3.0f) { // More lenient but still catching major issues
                    stable = false;
                    Log.w(TAG, String.format("Large swing: %.2f -> %.2f (%.2f)", 
                          evaluations.get(i-1), evaluations.get(i), swing));
                }
            }
            
            Log.d(TAG, String.format("Swing prevention test: %s (max swing detected)", 
                  stable ? "PASSED" : "NEEDS_ATTENTION"));
            
            return stable;
        });
    }
    
    /**
     * Test concurrent evaluations
     */
    private void testConcurrentEvaluations() {
        runTest("Concurrent Evaluations", () -> {
            CountDownLatch latch = new CountDownLatch(3);
            final int[] successCount = {0};
            
            // Fire three evaluations simultaneously
            for (int i = 0; i < 3; i++) {
                unifiedSystem.evaluatePosition(
                    "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                    1000,
                    new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            successCount[0]++;
                            latch.countDown();
                        }
                        
                        @Override
                        public void onError(String error) {
                            latch.countDown();
                        }
                    });
            }
            
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            boolean concurrentHandled = successCount[0] >= 1; // At least one should succeed
            Log.d(TAG, "Concurrent test: " + successCount[0] + "/3 succeeded");
            
            return concurrentHandled;
        });
    }
    
    /**
     * Helper method to run individual tests
     */
    private void runTest(String testName, TestFunction test) {
        testsRun++;
        Log.d(TAG, "🧪 Running test: " + testName);
        
        try {
            boolean passed = test.run();
            if (passed) {
                testsPassed++;
                testResults.add("✅ " + testName + ": PASSED");
                Log.d(TAG, "✅ " + testName + ": PASSED");
            } else {
                testResults.add("❌ " + testName + ": FAILED");
                Log.e(TAG, "❌ " + testName + ": FAILED");
            }
        } catch (Exception e) {
            testResults.add("💥 " + testName + ": EXCEPTION - " + e.getMessage());
            Log.e(TAG, "💥 " + testName + ": EXCEPTION", e);
        }
    }
    
    private interface TestFunction {
        boolean run() throws Exception;
    }
    
    /**
     * Test results container
     */
    public static class TestResults {
        public final int totalTests;
        public final int passedTests;
        public final List<String> results;
        public final boolean allPassed;
        
        public TestResults(int totalTests, int passedTests, List<String> results) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.results = results;
            this.allPassed = passedTests == totalTests;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("🧪 TEST RESULTS: %d/%d passed (%s)\n", 
                     passedTests, totalTests, allPassed ? "ALL PASSED" : "SOME FAILED"));
            
            for (String result : results) {
                sb.append(result).append("\n");
            }
            
            return sb.toString();
        }
    }
}