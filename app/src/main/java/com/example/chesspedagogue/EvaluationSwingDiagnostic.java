package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 🩺 EVALUATION SWING DIAGNOSTIC
 * 
 * This tool diagnoses and tests the evaluation swing bug that was causing
 * impossible swings like +5.46 → -5.69 → +7.90 in normal gameplay.
 * 
 * Use this to verify that the race condition fix is working correctly.
 */
public class EvaluationSwingDiagnostic {
    private static final String TAG = "EvalSwingDiagnostic";
    
    /**
     * Run a comprehensive diagnostic to check for evaluation swing issues
     */
    public static void runDiagnostic(Context context) {
        Log.d(TAG, "🩺 EVALUATION SWING DIAGNOSTIC STARTING");
        Log.d(TAG, "================================================");
        
        try {
            // Test 1: Thread safety test
            testThreadSafety(context);
            
            // Test 2: Position consistency test  
            testPositionConsistency(context);
            
            // Test 3: Realistic swing detection
            testRealisticSwings(context);
            
            Log.d(TAG, "🏁 DIAGNOSTIC COMPLETE - Check logs for results");
            
        } catch (Exception e) {
            Log.e(TAG, "💥 Diagnostic failed", e);
        }
    }
    
    /**
     * Test thread safety - the main cause of evaluation swings
     */
    private static void testThreadSafety(Context context) {
        Log.d(TAG, "🧵 TEST 1: Thread Safety (Race Condition Detection)");
        
        try {
            UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
            
            // Test position that should have consistent evaluation
            String testPosition = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1";
            
            final int NUM_THREADS = 5;
            final CountDownLatch latch = new CountDownLatch(NUM_THREADS);
            final List<Float> results = new ArrayList<>();
            final AtomicInteger completedThreads = new AtomicInteger(0);
            
            // Run multiple concurrent evaluations
            for (int i = 0; i < NUM_THREADS; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        Log.d(TAG, String.format("🧵 Thread %d: Starting evaluation", threadId));
                        
                        unified.setPosition(testPosition);
                        unified.evaluateCurrentPosition(800, new UnifiedEvaluationSystem.EvaluationCallback() {
                            @Override
                            public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                                synchronized (results) {
                                    results.add(result.evaluation);
                                }
                                int completed = completedThreads.incrementAndGet();
                                Log.d(TAG, String.format("✅ Thread %d: Result %.2f (completed: %d/%d)", 
                                      threadId, result.evaluation, completed, NUM_THREADS));
                                latch.countDown();
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.e(TAG, String.format("❌ Thread %d: Error - %s", threadId, error));
                                latch.countDown();
                            }
                        });
                        
                    } catch (Exception e) {
                        Log.e(TAG, String.format("💥 Thread %d: Exception", threadId), e);
                        latch.countDown();
                    }
                }, "DiagnosticThread-" + i).start();
                
                // Small delay between thread starts
                Thread.sleep(100);
            }
            
            // Wait for completion
            latch.await();
            
            // Analyze results
            if (results.size() > 1) {
                float min = results.stream().min(Float::compare).orElse(0f);
                float max = results.stream().max(Float::compare).orElse(0f);
                float swing = Math.abs(max - min);
                
                Log.d(TAG, String.format("📊 THREAD SAFETY RESULTS: %d evaluations", results.size()));
                Log.d(TAG, String.format("   Min: %.2f, Max: %.2f, Swing: %.2f", min, max, swing));
                
                if (swing > 1.0f) {
                    Log.e(TAG, "🚨 THREAD SAFETY FAILURE: Large swing detected! Race condition still exists.");
                } else {
                    Log.d(TAG, "✅ THREAD SAFETY PASSED: Consistent results across threads");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Thread safety test failed", e);
        }
    }
    
    /**
     * Test position consistency - same position should give same evaluation
     */
    private static void testPositionConsistency(Context context) {
        Log.d(TAG, "📍 TEST 2: Position Consistency");
        
        try {
            UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
            
            String[] testPositions = {
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",  // Starting position
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1"   // e4
            };
            
            for (int posIndex = 0; posIndex < testPositions.length; posIndex++) {
                final String position = testPositions[posIndex];
                final int posNum = posIndex + 1;
                final List<Float> evaluations = new ArrayList<>();
                final CountDownLatch posLatch = new CountDownLatch(3);
                
                Log.d(TAG, String.format("📍 Testing position %d: %s...", posNum, position.substring(0, 30)));
                
                // Evaluate same position multiple times
                for (int i = 0; i < 3; i++) {
                    final int evalNum = i + 1;
                    
                    new Thread(() -> {
                        unified.setPosition(position);
                        unified.evaluateCurrentPosition(600, new UnifiedEvaluationSystem.EvaluationCallback() {
                            @Override
                            public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                                synchronized (evaluations) {
                                    evaluations.add(result.evaluation);
                                }
                                Log.d(TAG, String.format("   📊 Pos %d Eval %d: %.2f", posNum, evalNum, result.evaluation));
                                posLatch.countDown();
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.e(TAG, String.format("   ❌ Pos %d Eval %d: %s", posNum, evalNum, error));
                                posLatch.countDown();
                            }
                        });
                    }, "ConsistencyTest-" + posNum + "-" + evalNum).start();
                    
                    Thread.sleep(200);
                }
                
                posLatch.await();
                
                // Check consistency
                if (evaluations.size() >= 2) {
                    float maxDiff = 0f;
                    for (int i = 0; i < evaluations.size() - 1; i++) {
                        maxDiff = Math.max(maxDiff, Math.abs(evaluations.get(i) - evaluations.get(i + 1)));
                    }
                    
                    if (maxDiff > 0.5f) {
                        Log.e(TAG, String.format("❌ Position %d INCONSISTENT: Max diff %.2f", posNum, maxDiff));
                    } else {
                        Log.d(TAG, String.format("✅ Position %d CONSISTENT: Max diff %.2f", posNum, maxDiff));
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Position consistency test failed", e);
        }
    }
    
    /**
     * Test realistic swing detection - normal moves shouldn't cause huge swings
     */
    private static void testRealisticSwings(Context context) {
        Log.d(TAG, "⚖️ TEST 3: Realistic Swing Detection");
        
        try {
            UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
            
            // Sequence of normal opening moves - should not cause massive swings
            String[] gameSequence = {
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",           // Starting
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1",          // e4
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2",       // e5
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"    // Nf3
            };
            
            final List<Float> sequenceEvals = new ArrayList<>();
            final CountDownLatch seqLatch = new CountDownLatch(gameSequence.length);
            
            for (int i = 0; i < gameSequence.length; i++) {
                final String position = gameSequence[i];
                final int moveNum = i + 1;
                
                new Thread(() -> {
                    unified.setPosition(position);
                    unified.evaluateCurrentPosition(700, new UnifiedEvaluationSystem.EvaluationCallback() {
                        @Override
                        public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                            synchronized (sequenceEvals) {
                                sequenceEvals.add(result.evaluation);
                            }
                            Log.d(TAG, String.format("⚖️ Move %d: %.2f", moveNum, result.evaluation));
                            seqLatch.countDown();
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, String.format("❌ Move %d: %s", moveNum, error));
                            seqLatch.countDown();
                        }
                    });
                }, "SwingTest-" + i).start();
                
                Thread.sleep(300); // Simulate normal game pace
            }
            
            seqLatch.await();
            
            // Analyze swings
            if (sequenceEvals.size() >= 2) {
                float maxSwing = 0f;
                for (int i = 1; i < sequenceEvals.size(); i++) {
                    float swing = Math.abs(sequenceEvals.get(i) - sequenceEvals.get(i - 1));
                    maxSwing = Math.max(maxSwing, swing);
                    Log.d(TAG, String.format("   📈 Move %d→%d swing: %.2f", i, i + 1, swing));
                }
                
                Log.d(TAG, String.format("📊 REALISTIC SWING ANALYSIS: Max swing %.2f", maxSwing));
                
                if (maxSwing > 3.0f) {
                    Log.e(TAG, "🚨 UNREALISTIC SWING DETECTED! Normal moves causing huge evaluation changes.");
                } else {
                    Log.d(TAG, "✅ REALISTIC SWINGS: Normal moves show reasonable evaluation changes");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Realistic swing test failed", e);
        }
    }
    
    /**
     * Quick diagnostic that can be called from anywhere
     */
    public static void quickDiagnostic(Context context) {
        Log.d(TAG, "⚡ QUICK EVALUATION DIAGNOSTIC");
        
        new Thread(() -> {
            try {
                UnifiedEvaluationSystem unified = UnifiedEvaluationSystem.getInstance(context);
                
                // Check race condition fix status
                boolean fixApplied = EvaluationRaceConditionFix.isFixApplied();
                String syncStats = EvaluationRaceConditionFix.getSynchronizationStats();
                
                Log.d(TAG, "🔧 Race condition fix status: " + (fixApplied ? "APPLIED" : "NOT APPLIED"));
                Log.d(TAG, "📊 Synchronization stats: " + syncStats);
                
                // Quick evaluation test
                String testPos = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1";
                unified.setPosition(testPos);
                unified.evaluateCurrentPosition(500, new UnifiedEvaluationSystem.EvaluationCallback() {
                    @Override
                    public void onSuccess(UnifiedEvaluationSystem.EvaluationResult result) {
                        Log.d(TAG, "✅ Quick test result: " + result.evaluation);
                        
                        if (Math.abs(result.evaluation) < 2.0f) {
                            Log.d(TAG, "✅ Evaluation appears reasonable for opening position");
                        } else {
                            Log.w(TAG, "⚠️ Unusual evaluation for opening position");
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Quick test failed: " + error);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Quick diagnostic failed", e);
            }
        }, "QuickDiagnostic").start();
    }
}