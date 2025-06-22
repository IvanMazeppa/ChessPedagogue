package com.example.chesspedagogue;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * 🚀 OPTIMIZED STOCKFISH EVALUATION BUGFIX
 * 
 * PERFORMANCE OPTIMIZATIONS:
 * - Reduced reflection usage
 * - Minimal logging (only errors and key results)
 * - Streamlined UCI communication
 * - Cache reflection methods for reuse
 */
public class OptimizedStockfishEvaluationBugfix {
    private static final String TAG = "OptimizedEvalBugfix";
    private static final boolean ENABLE_DEBUG_LOGS = false; // Set to false for production
    
    // Cache reflection methods to avoid repeated lookups
    private static java.lang.reflect.Method cachedSendMethod;
    private static java.lang.reflect.Method cachedGetOutputMethod;
    private static java.lang.reflect.Field cachedOutputField;
    private static boolean reflectionInitialized = false;
    
    /**
     * 🚀 OPTIMIZED VERSION: Minimal overhead UCI communication fix
     */
    public static StockfishManager.EvaluationResult getEvaluationOptimized(
            StockfishManager stockfishManager, int thinkTimeMs) {
        
        try {
            if (ENABLE_DEBUG_LOGS) {
                Log.d(TAG, "🚀 Starting optimized evaluation");
            }
            
            // Initialize reflection cache once
            if (!reflectionInitialized) {
                initializeReflectionCache();
            }
            
            // Quick engine readiness check
            if (!sendCommandCached(stockfishManager, "isready")) {
                return new StockfishManager.EvaluationResult(0.0f, false, 0);
            }
            
            // Clear buffer efficiently
            clearOutputBufferCached(stockfishManager);
            
            // Send evaluation command
            sendCommandCached(stockfishManager, "go depth 15 movetime " + thinkTimeMs);
            
            // Wait for completion with minimal overhead
            long endTime = System.currentTimeMillis() + thinkTimeMs + 1000; // Reduced buffer
            List<String> evaluationLines = new ArrayList<>();
            
            while (System.currentTimeMillis() < endTime) {
                List<String> currentOutput = getOutputBufferCached(stockfishManager);
                
                for (String line : currentOutput) {
                    if (line.startsWith("info depth") && line.contains("score")) {
                        evaluationLines.add(line);
                    } else if (line.startsWith("bestmove")) {
                        // Parse and return immediately
                        return parseEvaluationOptimized(evaluationLines);
                    }
                }
                
                // Reduced sleep for faster response
                Thread.sleep(25);
            }
            
            // Timeout fallback
            return parseEvaluationOptimized(evaluationLines);
            
        } catch (Exception e) {
            if (ENABLE_DEBUG_LOGS) {
                Log.e(TAG, "❌ Optimized evaluation error", e);
            }
            return new StockfishManager.EvaluationResult(0.0f, false, 0);
        }
    }
    
    /**
     * 🏗️ Initialize reflection cache once for better performance
     */
    private static void initializeReflectionCache() {
        try {
            cachedSendMethod = StockfishManager.class.getDeclaredMethod("sendCommand", String.class);
            cachedSendMethod.setAccessible(true);
            
            cachedGetOutputMethod = StockfishManager.class.getDeclaredMethod("getOutputBuffer");
            cachedGetOutputMethod.setAccessible(true);
            
            cachedOutputField = StockfishManager.class.getDeclaredField("outputBuffer");
            cachedOutputField.setAccessible(true);
            
            reflectionInitialized = true;
            
            if (ENABLE_DEBUG_LOGS) {
                Log.d(TAG, "✅ Reflection cache initialized");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize reflection cache", e);
        }
    }
    
    /**
     * 🚀 Cached reflection method calls
     */
    private static boolean sendCommandCached(StockfishManager manager, String command) {
        try {
            if (cachedSendMethod != null) {
                cachedSendMethod.invoke(manager, command);
                return true;
            }
        } catch (Exception e) {
            if (ENABLE_DEBUG_LOGS) {
                Log.e(TAG, "Send command error: " + command, e);
            }
        }
        return false;
    }
    
    private static List<String> getOutputBufferCached(StockfishManager manager) {
        try {
            if (cachedGetOutputMethod != null) {
                @SuppressWarnings("unchecked")
                List<String> buffer = (List<String>) cachedGetOutputMethod.invoke(manager);
                return new ArrayList<>(buffer);
            }
        } catch (Exception e) {
            if (ENABLE_DEBUG_LOGS) {
                Log.e(TAG, "Get output buffer error", e);
            }
        }
        return new ArrayList<>();
    }
    
    private static void clearOutputBufferCached(StockfishManager manager) {
        try {
            if (cachedOutputField != null) {
                @SuppressWarnings("unchecked")
                List<String> buffer = (List<String>) cachedOutputField.get(manager);
                buffer.clear();
            }
        } catch (Exception e) {
            if (ENABLE_DEBUG_LOGS) {
                Log.e(TAG, "Clear buffer error", e);
            }
        }
    }
    
    /**
     * 🚀 Optimized evaluation parsing - minimal logging
     */
    private static StockfishManager.EvaluationResult parseEvaluationOptimized(List<String> lines) {
        float bestScore = 0.0f;
        boolean isMate = false;
        int mateInMoves = 0;
        int bestDepth = 0;
        
        for (String line : lines) {
            try {
                int depth = extractIntValue(line, "depth");
                
                if (depth >= bestDepth) {
                    bestDepth = depth;
                    
                    if (line.contains("score cp")) {
                        int centipawns = extractIntValue(line, "score cp");
                        bestScore = centipawns / 100.0f;
                        isMate = false;
                        
                    } else if (line.contains("score mate")) {
                        mateInMoves = extractIntValue(line, "score mate");
                        isMate = true;
                        bestScore = 0.0f;
                    }
                }
            } catch (Exception e) {
                // Silent error handling for performance
            }
        }
        
        StockfishManager.EvaluationResult result = 
            new StockfishManager.EvaluationResult(bestScore, isMate, mateInMoves);
        
        if (ENABLE_DEBUG_LOGS) {
            Log.d(TAG, String.format("🚀 Optimized result: %s (depth %d)", result, bestDepth));
        }
        
        return result;
    }
    
    /**
     * 🚀 Optimized integer extraction
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
}