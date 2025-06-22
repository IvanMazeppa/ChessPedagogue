package com.example.chesspedagogue;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 🔒 THREAD-SAFE STOCKFISH MANAGER
 * 
 * CRITICAL FIX for the evaluation race condition bug that was causing
 * impossible evaluation swings (+5.46 → -5.69 → +7.90 etc.)
 * 
 * Key improvements:
 * ✅ Synchronized UCI communication  
 * ✅ Per-evaluation output buffers
 * ✅ Atomic operation guarantees
 * ✅ Prevents mixed-up evaluations
 * ✅ Thread-safe position setting
 * 
 * This replaces the problematic StockfishManager.getCurrentEvaluation() method
 * that was using a shared outputBuffer causing race conditions.
 */
public class ThreadSafeStockfishManager {
    private static final String TAG = "ThreadSafeStockfish";
    
    private final ReentrantLock engineLock = new ReentrantLock();
    private final AtomicInteger evaluationId = new AtomicInteger(0);
    
    private Process stockfishProcess;
    private BufferedWriter engineInput;
    private BufferedReader engineOutput;
    private boolean isInitialized = false;
    
    private String currentPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    /**
     * Initialize the Stockfish engine
     */
    public boolean initialize(String enginePath) {
        engineLock.lock();
        try {
            if (isInitialized) {
                Log.d(TAG, "✅ Engine already initialized");
                return true;
            }
            
            Log.d(TAG, "🚀 Initializing thread-safe Stockfish engine...");
            
            // Start Stockfish process
            ProcessBuilder processBuilder = new ProcessBuilder(enginePath);
            stockfishProcess = processBuilder.start();
            
            // Set up I/O streams
            engineInput = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
            engineOutput = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            
            // Send UCI initialization
            sendCommand("uci");
            waitForResponse("uciok", 5000);
            
            sendCommand("isready");
            waitForResponse("readyok", 2000);
            
            // Set starting position
            sendCommand("position startpos");
            sendCommand("isready");
            waitForResponse("readyok", 1000);
            
            isInitialized = true;
            Log.d(TAG, "✅ Thread-safe Stockfish initialized successfully!");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initialize Stockfish", e);
            return false;
        } finally {
            engineLock.unlock();
        }
    }
    
    /**
     * 🔒 THREAD-SAFE EVALUATION - Fixed the race condition bug!
     * 
     * This method is fully synchronized to prevent the outputBuffer race
     * condition that was causing impossible evaluation swings.
     */
    public StockfishManager.EvaluationResult getCurrentEvaluation(int thinkTimeMs) {
        if (!isInitialized) {
            Log.e(TAG, "❌ Engine not initialized");
            return new StockfishManager.EvaluationResult(0.0f, false, 0);
        }
        
        // CRITICAL: Full synchronization to prevent race conditions
        engineLock.lock();
        try {
            int evalId = evaluationId.incrementAndGet();
            Log.d(TAG, String.format("🔒 EVAL %d: Starting thread-safe evaluation for position: %s", 
                  evalId, currentPosition.substring(0, Math.min(30, currentPosition.length()))));
            
            // Create dedicated output buffer for this evaluation
            List<String> evaluationOutput = new ArrayList<>();
            
            // Send evaluation command
            sendCommand("go depth 15 movetime " + thinkTimeMs);
            
            // Collect output until completion
            long endTime = System.currentTimeMillis() + thinkTimeMs + 1000;
            boolean analysisComplete = false;
            
            while (System.currentTimeMillis() < endTime && !analysisComplete) {
                try {
                    if (engineOutput.ready()) {
                        String line = engineOutput.readLine();
                        if (line != null) {
                            evaluationOutput.add(line);
                            Log.v(TAG, String.format("EVAL %d UCI: %s", evalId, line));
                            
                            if (line.startsWith("bestmove")) {
                                analysisComplete = true;
                                Log.d(TAG, String.format("🏁 EVAL %d: Analysis complete", evalId));
                            }
                        }
                    } else {
                        Thread.sleep(10); // Short sleep to avoid busy waiting
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException e) {
                    Log.e(TAG, "❌ Error reading from engine", e);
                    break;
                }
            }
            
            if (!analysisComplete) {
                Log.w(TAG, String.format("⏰ EVAL %d: Analysis timed out", evalId));
            }
            
            // Parse evaluation from dedicated output buffer
            StockfishManager.EvaluationResult result = parseEvaluationFromOutput(evaluationOutput, evalId);
            
            Log.d(TAG, String.format("✅ EVAL %d: Completed with result: %s", evalId, result));
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in thread-safe evaluation", e);
            return new StockfishManager.EvaluationResult(0.0f, false, 0);
        } finally {
            engineLock.unlock();
        }
    }
    
    /**
     * 🔒 THREAD-SAFE POSITION SETTING
     */
    public boolean setPosition(String fen) {
        if (!isInitialized) {
            Log.e(TAG, "❌ Engine not initialized");
            return false;
        }
        
        engineLock.lock();
        try {
            if (fen.equals(currentPosition)) {
                Log.d(TAG, "📍 Position unchanged, skipping set");
                return true;
            }
            
            Log.d(TAG, "📍 Setting position: " + fen.substring(0, Math.min(40, fen.length())));
            
            sendCommand("position fen " + fen);
            sendCommand("isready");
            
            boolean ready = waitForResponse("readyok", 2000);
            if (ready) {
                currentPosition = fen;
                Log.d(TAG, "✅ Position set successfully");
            } else {
                Log.e(TAG, "❌ Failed to set position - engine not ready");
            }
            
            return ready;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting position", e);
            return false;
        } finally {
            engineLock.unlock();
        }
    }
    
    /**
     * Parse evaluation from dedicated output buffer (no shared state!)
     */
    private StockfishManager.EvaluationResult parseEvaluationFromOutput(List<String> output, int evalId) {
        float bestScore = 0.0f;
        boolean isMate = false;
        int mateInMoves = 0;
        int bestDepth = 0;
        
        Log.d(TAG, String.format("🔍 EVAL %d: Parsing %d lines of output", evalId, output.size()));
        
        for (String line : output) {
            if (line.contains("info depth") && line.contains("score")) {
                try {
                    int depth = extractIntValue(line, "depth");
                    
                    if (depth >= bestDepth) {
                        bestDepth = depth;
                        
                        if (line.contains("score cp")) {
                            int centipawns = extractIntValue(line, "score cp");
                            bestScore = centipawns / 100.0f;
                            isMate = false;
                            Log.d(TAG, String.format("📊 EVAL %d: Depth %d evaluation: %.2f", evalId, depth, bestScore));
                            
                        } else if (line.contains("score mate")) {
                            mateInMoves = extractIntValue(line, "score mate");
                            isMate = true;
                            bestScore = 0.0f;
                            Log.d(TAG, String.format("♔ EVAL %d: Depth %d mate: M%d", evalId, depth, mateInMoves));
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, String.format("⚠️ EVAL %d: Error parsing line: %s", evalId, line), e);
                }
            }
        }
        
        StockfishManager.EvaluationResult result = new StockfishManager.EvaluationResult(bestScore, isMate, mateInMoves);
        Log.d(TAG, String.format("🎯 EVAL %d: Final result from depth %d: %s", evalId, bestDepth, result));
        
        return result;
    }
    
    /**
     * Send command to engine (must be called within lock)
     */
    private void sendCommand(String command) throws IOException {
        engineInput.write(command + "\n");
        engineInput.flush();
        Log.v(TAG, "→ " + command);
    }
    
    /**
     * Wait for specific response (must be called within lock)
     */
    private boolean waitForResponse(String expectedResponse, long timeoutMs) throws IOException {
        long endTime = System.currentTimeMillis() + timeoutMs;
        
        while (System.currentTimeMillis() < endTime) {
            if (engineOutput.ready()) {
                String line = engineOutput.readLine();
                if (line != null) {
                    Log.v(TAG, "← " + line);
                    if (line.equals(expectedResponse)) {
                        return true;
                    }
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        Log.w(TAG, "⏰ Timeout waiting for: " + expectedResponse);
        return false;
    }
    
    /**
     * Extract integer value from UCI line
     */
    private int extractIntValue(String line, String key) {
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
     * Clean shutdown
     */
    public void cleanup() {
        engineLock.lock();
        try {
            if (stockfishProcess != null) {
                Log.d(TAG, "🧹 Shutting down thread-safe Stockfish");
                
                try {
                    sendCommand("quit");
                } catch (IOException e) {
                    Log.w(TAG, "Error sending quit command", e);
                }
                
                stockfishProcess.destroy();
                isInitialized = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        } finally {
            engineLock.unlock();
        }
    }
}