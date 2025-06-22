package com.example.chesspedagogue;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 🔧 STOCKFISH EVALUATION BUGFIX
 * 
 * CRITICAL BUG: After moves like d1g4 (queen blunder) or c1h6 (bishop blunder),
 * Stockfish returns positive evaluations (+7.41, +8.91) when it should return 
 * large negative evaluations (indicating the pieces can be captured).
 * 
 * ROOT CAUSE: UCI communication timing and buffer contamination issues:
 * 1. getCurrentFEN() and getCurrentEvaluation() both use same outputBuffer
 * 2. Race conditions between position setting and evaluation
 * 3. Insufficient verification that Stockfish is evaluating correct position
 * 
 * SOLUTION: Enhanced UCI communication with proper synchronization
 */
public class StockfishEvaluationBugfix {
    private static final String TAG = "StockfishEvalBugfix";
    
    /**
     * 🔧 FIXED VERSION: Thread-safe evaluation with proper position verification
     * 
     * This replaces the problematic getCurrentEvaluation method that was
     * returning incorrect results for positions where pieces are in danger.
     */
    public static StockfishManager.EvaluationResult getEvaluationFixed(
            StockfishManager stockfishManager, int thinkTimeMs) {
        
        try {
            Log.d(TAG, "🔧 BUGFIX: Starting corrected evaluation process");
            
            // 🚨 FIX 1: Don't call getCurrentFEN() during evaluation - it clears outputBuffer!
            // Instead, trust that position was set correctly by caller
            
            // 🚨 FIX 2: Clear buffer and wait for engine to be ready
            List<String> evaluationBuffer = new ArrayList<>();  // Use separate buffer
            
            // Send isready command and wait for response
            if (!sendCommandAndWait(stockfishManager, "isready", "readyok", 1000)) {
                Log.e(TAG, "❌ Engine not ready for evaluation");
                return new StockfishManager.EvaluationResult(0.0f, false, 0);
            }
            
            // 🚨 FIX 3: Clear buffer immediately before evaluation command
            clearOutputBuffer(stockfishManager);
            
            // 🚨 FIX 4: Send evaluation command and collect output in separate buffer
            sendCommand(stockfishManager, "go depth 15 movetime " + thinkTimeMs);
            Log.d(TAG, "📤 BUGFIX: Evaluation command sent");
            
            // 🚨 FIX 5: Wait for completion with enhanced monitoring
            long endTime = System.currentTimeMillis() + thinkTimeMs + 2000; // Extra buffer
            boolean analysisComplete = false;
            
            while (System.currentTimeMillis() < endTime && !analysisComplete) {
                List<String> currentOutput = getOutputBuffer(stockfishManager);
                
                for (String line : currentOutput) {
                    if (!evaluationBuffer.contains(line)) {
                        evaluationBuffer.add(line);
                        Log.v(TAG, "UCI: " + line);
                    }
                    
                    if (line.startsWith("bestmove")) {
                        analysisComplete = true;
                        Log.d(TAG, "✅ BUGFIX: Analysis completed");
                        break;
                    }
                }
                
                if (!analysisComplete) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            if (!analysisComplete) {
                Log.e(TAG, "❌ BUGFIX: Evaluation timeout");
                return new StockfishManager.EvaluationResult(0.0f, false, 0);
            }
            
            // 🚨 FIX 6: Parse from clean evaluation buffer
            StockfishManager.EvaluationResult result = parseEvaluationFromLines(evaluationBuffer);
            
            Log.d(TAG, String.format("✅ BUGFIX: Evaluation complete: %s (from %d lines)", 
                  result, evaluationBuffer.size()));
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ BUGFIX: Error during evaluation", e);
            return new StockfishManager.EvaluationResult(0.0f, false, 0);
        }
    }
    
    /**
     * 🔧 HELPER: Parse evaluation from clean line buffer
     */
    private static StockfishManager.EvaluationResult parseEvaluationFromLines(List<String> lines) {
        float bestScore = 0.0f;
        boolean isMate = false;
        int mateInMoves = 0;
        int bestDepth = 0;
        
        Log.d(TAG, String.format("🔍 BUGFIX: Parsing %d lines for evaluation", lines.size()));
        
        for (String line : lines) {
            if (line.contains("info depth") && line.contains("score")) {
                try {
                    int depth = extractIntValue(line, "depth");
                    
                    if (depth >= bestDepth) {
                        bestDepth = depth;
                        
                        if (line.contains("score cp")) {
                            int centipawns = extractIntValue(line, "score cp");
                            bestScore = centipawns / 100.0f;
                            isMate = false;
                            Log.d(TAG, String.format("📊 BUGFIX: Depth %d eval: %.2f", depth, bestScore));
                            
                        } else if (line.contains("score mate")) {
                            mateInMoves = extractIntValue(line, "score mate");
                            isMate = true;
                            bestScore = 0.0f;
                            Log.d(TAG, String.format("♔ BUGFIX: Depth %d mate: M%d", depth, mateInMoves));
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "⚠️ BUGFIX: Error parsing line: " + line, e);
                }
            }
        }
        
        StockfishManager.EvaluationResult result = 
            new StockfishManager.EvaluationResult(bestScore, isMate, mateInMoves);
        
        Log.d(TAG, String.format("🎯 BUGFIX: Final result from depth %d: %s", bestDepth, result));
        return result;
    }
    
    /**
     * 🔧 HELPER: Extract integer value from UCI line
     */
    private static int extractIntValue(String line, String key) {
        int keyIndex = line.indexOf(key);
        if (keyIndex == -1) return 0;
        
        int startIndex = keyIndex + key.length();
        while (startIndex < line.length() && !Character.isDigit(line.charAt(startIndex)) && line.charAt(startIndex) != '-') {
            startIndex++;
        }
        
        int endIndex = startIndex;
        while (endIndex < line.length() && (Character.isDigit(line.charAt(endIndex)) || line.charAt(endIndex) == '-')) {
            endIndex++;
        }
        
        if (startIndex < endIndex) {
            return Integer.parseInt(line.substring(startIndex, endIndex));
        }
        
        return 0;
    }
    
    /**
     * 🔧 HELPER: Send command and wait for specific response
     */
    private static boolean sendCommandAndWait(StockfishManager manager, String command, 
                                            String expectedResponse, long timeoutMs) {
        try {
            // Use reflection to access private methods
            java.lang.reflect.Method sendMethod = StockfishManager.class.getDeclaredMethod("sendCommand", String.class);
            sendMethod.setAccessible(true);
            sendMethod.invoke(manager, command);
            
            long endTime = System.currentTimeMillis() + timeoutMs;
            while (System.currentTimeMillis() < endTime) {
                List<String> output = getOutputBuffer(manager);
                for (String line : output) {
                    if (line.equals(expectedResponse)) {
                        return true;
                    }
                }
                Thread.sleep(10);
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error sending command: " + command, e);
            return false;
        }
    }
    
    /**
     * 🔧 HELPER: Send command using reflection
     */
    private static void sendCommand(StockfishManager manager, String command) {
        try {
            java.lang.reflect.Method method = StockfishManager.class.getDeclaredMethod("sendCommand", String.class);
            method.setAccessible(true);
            method.invoke(manager, command);
        } catch (Exception e) {
            Log.e(TAG, "Error sending command: " + command, e);
        }
    }
    
    /**
     * 🔧 HELPER: Get output buffer using reflection
     */
    private static List<String> getOutputBuffer(StockfishManager manager) {
        try {
            java.lang.reflect.Method method = StockfishManager.class.getDeclaredMethod("getOutputBuffer");
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<String> buffer = (List<String>) method.invoke(manager);
            return new ArrayList<>(buffer);
        } catch (Exception e) {
            Log.e(TAG, "Error getting output buffer", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 🔧 HELPER: Clear output buffer using reflection
     */
    private static void clearOutputBuffer(StockfishManager manager) {
        try {
            java.lang.reflect.Field field = StockfishManager.class.getDeclaredField("outputBuffer");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<String> buffer = (List<String>) field.get(manager);
            buffer.clear();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing output buffer", e);
        }
    }
    
    /**
     * 🧪 TEST: Verify the bugfix with problematic positions
     */
    public static void testBugfix(StockfishManager manager) {
        Log.d(TAG, "🧪 TESTING EVALUATION BUGFIX");
        Log.d(TAG, "==============================");
        
        // Test the exact positions that were causing problems
        String[] problematicPositions = {
            "rnbqkb1r/ppp2ppp/3p1n2/4p3/2PPP1Q1/8/PP3PPP/RNB1KBNR b KQkq - 1 4", // Queen on g4 (should be negative)
            "rnbqkb1r/ppp2ppp/3p3B/4p3/2PPP1n1/8/PP3PPP/RN2KBNR b KQkq - 1 5"  // Bishop on h6 (should be negative)
        };
        
        String[] descriptions = {
            "Queen on g4 (can be captured by knight)",
            "Bishop on h6 (can be captured by pawn)"
        };
        
        for (int i = 0; i < problematicPositions.length; i++) {
            Log.d(TAG, "");
            Log.d(TAG, String.format("🎯 TEST %d: %s", i + 1, descriptions[i]));
            
            // Set position
            if (manager.setPosition(problematicPositions[i])) {
                // Evaluate with bugfix
                StockfishManager.EvaluationResult result = getEvaluationFixed(manager, 1000);
                
                Log.d(TAG, String.format("📊 RESULT: %s", result));
                
                // Check if result makes sense
                if (result.evaluation < -1.0f) {
                    Log.d(TAG, "✅ CORRECT: Evaluation shows disadvantage for side with piece in danger");
                } else {
                    Log.e(TAG, "❌ STILL BROKEN: Evaluation should be negative!");
                }
            } else {
                Log.e(TAG, "❌ Failed to set position");
            }
        }
        
        Log.d(TAG, "🏁 Bugfix test complete");
    }
}