package com.example.chesspedagogue;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages communication with the Stockfish chess engine using the UCI protocol.
 * Provides methods to send commands and receive responses.
 * Enhanced with position evaluation capabilities for the evaluation bar.
 */
public class StockfishManager {
    private static final String TAG = "StockfishManager";
    private final List<String> outputBuffer = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isRecovering = new AtomicBoolean(false);
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread readerThread;
    private boolean isReady = false;
    private String enginePath;
    private List<String> moveHistory = new ArrayList<>();

    // Add this field to track the current FEN
    private String currentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /**
     * Starts the Stockfish engine process.
     *
     * @param enginePath Path to the Stockfish executable
     * @return true if the engine started successfully
     */
    public boolean startEngine(String enginePath) {
        this.enginePath = enginePath; // Store for recovery
        return startEngineInternal(enginePath);
    }

    private boolean startEngineInternal(String enginePath) {
        try {
            // Start the engine using the provided path
            File engineFile = new File(enginePath);
            if (!engineFile.exists() || !engineFile.canExecute()) {
                Log.e(TAG, "Engine file does not exist or is not executable: " + enginePath);
                return false;
            }

            ProcessBuilder builder = new ProcessBuilder(enginePath);
            builder.redirectErrorStream(true);
            process = builder.start();

            // Set up communication channels
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            // Start reader thread
            isRunning.set(true);
            readerThread = new Thread(this::readOutputContinuously);
            readerThread.start();

            // Initialize UCI mode
            sendCommand("uci");

            // Wait for "uciok" response
            if (!waitForResponse("uciok", 5000)) {
                Log.e(TAG, "Engine did not respond with 'uciok'");
                stopEngine();
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error starting engine", e);
            return false;
        }
    }

    /**
     * Continuously reads output from the engine process.
     */
    private void readOutputContinuously() {
        try {
            String line;
            while (isRunning.get() && (line = reader.readLine()) != null) {
                Log.d(TAG, "Engine: " + line);
                outputBuffer.add(line);

                // Check for readyok
                if (line.equals("readyok")) {
                    isReady = true;
                }
            }
        } catch (IOException e) {
            if (isRunning.get()) {
                Log.e(TAG, "Error reading from engine", e);
            }
        } finally {
            Log.d(TAG, "Reader thread exiting");
        }
    }

    /**
     * Sends a command to the engine.
     *
     * @param command UCI command to send
     * @throws IOException if an I/O error occurs
     */
    public void sendCommand(String command) throws IOException {
        // First, try to send the command normally
        if (writer == null || !isRunning.get()) {
            Log.w(TAG, "🚨 Engine not running, attempting recovery...");
            if (attemptEngineRecovery()) {
                Log.i(TAG, "✅ Engine recovery successful, retrying command: " + command);
            } else {
                throw new IOException("Engine not running and recovery failed");
            }
        }

        try {
            Log.d(TAG, "Sending: " + command);
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "❌ Failed to send command, attempting recovery: " + e.getMessage());
            if (attemptEngineRecovery()) {
                Log.i(TAG, "✅ Recovery after IOException successful, retrying: " + command);
                writer.write(command + "\n");
                writer.flush();
            } else {
                throw new IOException("Engine communication failed and recovery failed", e);
            }
        }
    }

    /**
     * Waits for the engine to be ready.
     *
     * @param timeoutMs Timeout in milliseconds
     * @return true if the engine is ready within the timeout
     */
    public boolean waitForReady(long timeoutMs) {
        isReady = false;
        try {
            sendCommand("isready");
            return waitForResponse("readyok", timeoutMs);
        } catch (IOException e) {
            Log.e(TAG, "Error sending isready command", e);
            return false;
        }
    }

    /**
     * Waits for a specific response from the engine.
     *
     * @param responsePrefix The response prefix to wait for
     * @param timeoutMs      Timeout in milliseconds
     * @return true if the response was received within the timeout
     */
    public boolean waitForResponse(String responsePrefix, long timeoutMs) {
        long endTime = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < endTime) {
            for (String line : outputBuffer) {
                if (line.contains(responsePrefix)) {
                    return true;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    /**
     * Gets the best move from the engine for the current position.
     *
     * @param thinkTimeMs Time in milliseconds for the engine to think
     * @return The best move in UCI notation (e.g., "e2e4") or null if no move was found
     */
    public String getBestMove(int thinkTimeMs) {
        try {
            // Clear output buffer
            outputBuffer.clear();

            // Send command to find best move with a specific time
            sendCommand("go movetime " + thinkTimeMs);

            // Wait for "bestmove" response
            long endTime = System.currentTimeMillis() + thinkTimeMs + 2000; // Add 2 seconds grace period
            String bestMove = null;

            // Keep checking for bestmove response
            while (System.currentTimeMillis() < endTime) {
                for (String line : outputBuffer) {
                    if (line.startsWith("bestmove")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 2) {
                            bestMove = parts[1];
                            break;
                        }
                    }
                }

                if (bestMove != null) {
                    break;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            return bestMove;
        } catch (IOException e) {
            Log.e(TAG, "Error getting best move", e);
            return null;
        }
    }

    /**
     * NEW METHOD: Gets the current position evaluation
     * This is the key method for your evaluation bar!
     *
     * @param thinkTimeMs Time for engine to analyze (recommended: 500-1000ms)
     * @return Evaluation result containing score and mate information
     */
    public EvaluationResult getCurrentEvaluation(int thinkTimeMs) {
        try {
            Log.d(TAG, "🔧 FIXED: Getting evaluation for current position with proper synchronization...");

            // 🚨 CRITICAL FIX 1: Verify engine is ready before starting evaluation
            if (!waitForReady(500)) {
                Log.e(TAG, "❌ Engine not ready for evaluation");
                return new EvaluationResult(0.0f, false, 0);
            }

            // 🚨 CRITICAL FIX 2: Get and log the current position to verify we're evaluating the right one
            String currentPosition = getCurrentFEN();
            Log.d(TAG, "🎯 POSITION VERIFICATION: Evaluating position: " + currentPosition);

            // 🚨 CRITICAL FIX 3: Clear output buffer AFTER position verification
            outputBuffer.clear();

            // 🚨 CRITICAL FIX 4: Add a small delay to ensure position is fully processed
            try {
                Thread.sleep(100); // 100ms should be enough for position processing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Send evaluation command
            sendCommand("go depth 15 movetime " + thinkTimeMs);
            Log.d(TAG, "📤 Evaluation command sent, waiting for analysis...");

            // Wait for analysis to complete
            long endTime = System.currentTimeMillis() + thinkTimeMs + 1000; // Add buffer time
            boolean analysisComplete = false;

            while (System.currentTimeMillis() < endTime && !analysisComplete) {
                for (String line : outputBuffer) {
                    if (line.startsWith("bestmove")) {
                        analysisComplete = true;
                        Log.d(TAG, "✅ Analysis completed: " + line);
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

            // 🚨 CRITICAL FIX 5: Verify we got a response
            if (!analysisComplete) {
                Log.e(TAG, "❌ Evaluation timeout - analysis did not complete");
                return new EvaluationResult(0.0f, false, 0);
            }

            // Parse the evaluation from the output
            EvaluationResult result = parseEvaluationFromOutput();
            
            // 🚨 CRITICAL FIX 6: Log the final result with position for debugging
            Log.d(TAG, String.format("✅ EVALUATION RESULT: %.2f pawns for position %s", 
                result.evaluation, currentPosition.substring(0, Math.min(30, currentPosition.length()))));
            
            return result;

        } catch (IOException e) {
            Log.e(TAG, "Error getting evaluation", e);
            return new EvaluationResult(0.0f, false, 0);
        }
    }

    /**
     * NEW METHOD: Parses evaluation from engine output
     */
    private EvaluationResult parseEvaluationFromOutput() {
        float bestScore = 0.0f;
        boolean isMate = false;
        int mateInMoves = 0;
        int bestDepth = 0;
        int evaluationLinesFound = 0;

        Log.d(TAG, "🔍 PARSING: Analyzing " + outputBuffer.size() + " output lines for evaluation...");

        // Look through output for the best (deepest) evaluation
        for (String line : outputBuffer) {
            if (line.contains("info depth") && line.contains("score")) {
                evaluationLinesFound++;
                Log.d(TAG, "🔍 EVAL LINE: " + line);
                
                try {
                    // Parse depth
                    int depth = extractIntValue(line, "depth");

                    // Only use evaluations from deeper searches
                    if (depth >= bestDepth) {
                        bestDepth = depth;

                        if (line.contains("score cp")) {
                            // Centipawn score (normal evaluation)
                            int centipawns = extractIntValue(line, "score cp");
                            bestScore = centipawns / 100.0f; // Convert to pawns
                            isMate = false;
                            Log.d(TAG, "✅ PARSED CP: depth=" + depth + ", centipawns=" + centipawns + ", score=" + bestScore);

                        } else if (line.contains("score mate")) {
                            // Mate in N moves
                            mateInMoves = extractIntValue(line, "score mate");
                            isMate = true;
                            bestScore = 0.0f;
                            Log.d(TAG, "✅ PARSED MATE: depth=" + depth + ", mate_in=" + mateInMoves);
                        }
                    } else {
                        Log.d(TAG, "⏩ Skipping lower depth: " + depth + " < " + bestDepth);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "❌ Error parsing evaluation line: " + line, e);
                }
            }
        }

        Log.d(TAG, String.format("📊 PARSE SUMMARY: Found %d evaluation lines, best depth %d, final score %.2f", 
            evaluationLinesFound, bestDepth, bestScore));

        // 🚨 CRITICAL: Warn if no evaluation lines were found - this indicates a problem
        if (evaluationLinesFound == 0) {
            Log.e(TAG, "❌ CRITICAL: No evaluation lines found in Stockfish output!");
            Log.e(TAG, "❌ OUTPUT BUFFER CONTENTS:");
            for (int i = 0; i < Math.min(outputBuffer.size(), 10); i++) {
                Log.e(TAG, "    [" + i + "] " + outputBuffer.get(i));
            }
        }

        return new EvaluationResult(bestScore, isMate, mateInMoves);
    }

    /**
     * NEW HELPER METHOD: Extracts integer values from UCI output
     */
    private int extractIntValue(String line, String key) {
        int keyIndex = line.indexOf(key);
        if (keyIndex == -1) return 0;

        // Find the start of the number
        int startIndex = keyIndex + key.length();
        while (startIndex < line.length() && !Character.isDigit(line.charAt(startIndex)) && line.charAt(startIndex) != '-') {
            startIndex++;
        }

        // Find the end of the number
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
     * NEW CLASS: Holds evaluation results
     */
    public static class EvaluationResult {
        public final float evaluation;  // In pawns (positive = white advantage)
        public final boolean isMate;
        public final int mateInMoves;   // Positive if white mates, negative if black mates

        public EvaluationResult(float evaluation, boolean isMate, int mateInMoves) {
            this.evaluation = evaluation;
            this.isMate = isMate;
            this.mateInMoves = mateInMoves;
        }

        @Override
        public String toString() {
            if (isMate) {
                return "M" + mateInMoves;
            } else {
                return String.format("%.2f", evaluation);
            }
        }
    }

    /**
     * Sets the position on the internal engine board from FEN notation.
     *
     * @param fen FEN string representing the position
     * @return true if the position was set successfully
     */
    public boolean setPosition(String fen) {
        try {
            Log.d(TAG, "🎯 POSITION: Setting position to: " + fen);
            sendCommand("position fen " + fen);
            
            // 🚨 CRITICAL FIX: Wait longer and verify the position was set correctly
            boolean ready = waitForReady(2000); // Increased timeout
            
            if (ready) {
                // Verify the position was actually set by getting it back
                String verifyFEN = getCurrentFEN();
                if (verifyFEN != null && verifyFEN.split(" ")[0].equals(fen.split(" ")[0])) {
                    Log.d(TAG, "✅ POSITION: Successfully set and verified");
                    return true;
                } else {
                    Log.e(TAG, "❌ POSITION: Set but verification failed");
                    Log.e(TAG, "    Expected: " + fen);
                    Log.e(TAG, "    Got:      " + verifyFEN);
                    return false;
                }
            } else {
                Log.e(TAG, "❌ POSITION: Engine not ready after setting position");
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error setting position", e);
            return false;
        }
    }

    public boolean setPositionFromMoves(String... moves) {
        try {
            // Build a complete command with ALL previous moves plus the new one
            StringBuilder command = new StringBuilder("position startpos");
            if (moves.length > 0) {
                command.append(" moves");
                for (String move : moves) {
                    command.append(" ").append(move);
                }
            }

            // Send the complete position to Stockfish
            sendCommand(command.toString());

            // Wait for engine to process
            boolean success = waitForReady(1000);

            // This is crucial - update the cached FEN after the move
            if (success) {
                currentFEN = getCurrentFEN();
            }

            return success;
        } catch (IOException e) {
            Log.e(TAG, "Error setting position from moves", e);
            return false;
        }
    }

    // 🚨 CRITICAL FIX: Enhanced single move application with proper verification
    public boolean makeSingleMove(String move) {
        try {
            Log.d(TAG, "🎯 MOVE: Applying single move: " + move);
            
            // First get the current FEN
            String currentPosition = getCurrentFEN();
            Log.d(TAG, "🎯 MOVE: From position: " + currentPosition);

            // Apply just this one move from the current position
            String command = "position fen " + currentPosition + " moves " + move;
            sendCommand(command);

            // 🚨 CRITICAL FIX: Wait longer for engine to process the move
            boolean success = waitForReady(1000); // Increased from 100ms to 1000ms

            if (success) {
                // 🚨 CRITICAL FIX: Get and verify the new position
                String newPosition = getCurrentFEN();
                
                if (newPosition != null && !newPosition.equals(currentPosition)) {
                    currentFEN = newPosition;
                    Log.d(TAG, "✅ MOVE: Successfully applied. New position: " + newPosition);
                    
                    // 🚨 ADDITIONAL FIX: Add a small delay to ensure the position is fully processed
                    try {
                        Thread.sleep(50); // 50ms buffer
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    return true;
                } else {
                    Log.e(TAG, "❌ MOVE: Position didn't change after move application");
                    Log.e(TAG, "    Move: " + move);
                    Log.e(TAG, "    Before: " + currentPosition);
                    Log.e(TAG, "    After:  " + newPosition);
                    return false;
                }
            } else {
                Log.e(TAG, "❌ MOVE: Engine not ready after move: " + move);
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making move", e);
            return false;
        }
    }

    /**
     * Sets the skill level of the engine (0-20).
     *
     * @param level Skill level (0 = weakest, 20 = strongest)
     * @return true if the skill level was set successfully
     */
    public boolean setSkillLevel(int level) {
        try {
            sendCommand("setoption name Skill Level value " + level);
            return waitForReady(1000);
        } catch (IOException e) {
            Log.e(TAG, "Error setting skill level", e);
            return false;
        }
    }

    // Add these methods to your existing StockfishManager.java class

    /**
     * Sets a UCI option for the engine.
     *
     * @param name  Option name
     * @param value Option value
     * @return true if the option was set successfully
     */
    public boolean setOption(String name, String value) {
        try {
            sendCommand("setoption name " + name + " value " + value);
            return waitForReady(1000);
        } catch (IOException e) {
            Log.e(TAG, "Error setting option: " + name, e);
            return false;
        }
    }

    /**
     * Gets a detailed analysis of the current position.
     *
     * @param thinkTimeMs Time in milliseconds for the engine to analyze
     * @return Detailed analysis including multiple best moves and evaluations
     */
    public String getDetailedAnalysis(int thinkTimeMs) {
        try {
            // Clear output buffer
            outputBuffer.clear();

            // Tell engine to analyze
            Log.d(TAG, "Starting analysis with time: " + thinkTimeMs + "ms");
            sendCommand("go depth 15 multipv 3 movetime " + thinkTimeMs);

            // Wait for analysis to complete
            long endTime = System.currentTimeMillis() + thinkTimeMs + 1000;  // Add buffer
            boolean foundBestMove = false;

            while (System.currentTimeMillis() < endTime && !foundBestMove) {
                for (String line : outputBuffer) {
                    if (line.startsWith("bestmove")) {
                        foundBestMove = true;
                        break;
                    }
                }

                if (!foundBestMove) {
                    try {
                        Thread.sleep(50);  // Short sleep to prevent CPU spinning
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            // Stop analysis if it's still running
            if (!foundBestMove) {
                sendCommand("stop");
            }

            // Collect all the relevant output
            StringBuilder analysis = new StringBuilder();
            for (String line : outputBuffer) {
                if (line.contains("info depth") && line.contains("score") && line.contains("pv")) {
                    analysis.append(line).append("\n");
                }
            }

            Log.d(TAG, "Analysis complete, found " + analysis.toString().split("\n").length + " lines");
            return analysis.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error getting detailed analysis", e);
            return "Error analyzing position: " + e.getMessage();
        }
    }

    /**
     * Gets the current output buffer from the engine.
     * This is useful for analyzing the engine's responses.
     *
     * @return A copy of the current output buffer
     */
    public List<String> getOutputBuffer() {
        return new ArrayList<>(outputBuffer);
    }

    /**
     * Check if analysis is complete (bestmove received)
     */
    private boolean containsBestMove() {
        for (String line : outputBuffer) {
            if (line.startsWith("bestmove")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if analysis is complete (bestmove received)
     */
    private boolean isAnalysisDone() {
        for (String line : outputBuffer) {
            if (line.startsWith("bestmove")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the skill level to limit engine strength
     *
     * @param elo The desired Elo rating (1320-3190)
     */
    public boolean setEngineStrength(int elo) {
        try {
            // Ensure value is within valid range
            elo = Math.max(1320, Math.min(3190, elo));

            // Enable strength limiting
            sendCommand("setoption name UCI_LimitStrength value true");
            waitForReady(100);

            // Set the Elo rating
            sendCommand("setoption name UCI_Elo value " + elo);
            return waitForReady(100);
        } catch (IOException e) {
            Log.e(TAG, "Error setting engine strength", e);
            return false;
        }
    }

    /**
     * Get the evaluation of a specific move.
     *
     * @param move        The move to evaluate in UCI format
     * @param thinkTimeMs Time to analyze
     * @return The evaluation score in centipawns
     */
    public float evaluateMove(String move, int thinkTimeMs) {
        try {
            // Make the move
            String fen = getCurrentFEN();
            sendCommand("position fen " + fen + " moves " + move);

            // Clear output buffer
            outputBuffer.clear();

            // Analyze the resulting position
            sendCommand("go depth 16 movetime " + thinkTimeMs);

            // Wait for analysis to complete
            long endTime = System.currentTimeMillis() + thinkTimeMs + 2000;
            while (System.currentTimeMillis() < endTime && !isAnalysisDone()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Stop the analysis if it's still running
            sendCommand("stop");

            // Find the score
            float score = 0.0f;
            for (String line : outputBuffer) {
                if (line.contains("score cp ")) {
                    int scoreIndex = line.indexOf("score cp ") + 9;
                    int endIndex = line.indexOf(" ", scoreIndex);
                    if (endIndex > scoreIndex) {
                        try {
                            score = Float.parseFloat(line.substring(scoreIndex, endIndex)) / 100.0f;
                            // 🔧 FIX: Keep Stockfish's standard White perspective (positive = White advantage)
                            // DO NOT negate - Stockfish UCI evaluations are always from White's perspective
                            break;
                        } catch (NumberFormatException e) {
                            // Skip this line
                        }
                    }
                }
            }

            // Restore the original position
            sendCommand("position fen " + fen);

            return score;
        } catch (IOException e) {
            Log.e(TAG, "Error evaluating move", e);
            return 0.0f;
        }
    }

    /**
     * Find the best move with an explanation of why it's good.
     *
     * @param thinkTimeMs Time to analyze
     * @return A description of the best move and why it's good
     */
    public String getBestMoveWithExplanation(int thinkTimeMs) {
        String bestMove = getBestMove(thinkTimeMs);
        if (bestMove == null || bestMove.isEmpty()) {
            return "No best move found";
        }

        float evaluation = evaluateMove(bestMove, thinkTimeMs / 2);

        StringBuilder explanation = new StringBuilder();
        explanation.append("Best move: ").append(bestMove);
        explanation.append(" (Evaluation: ").append(String.format("%.2f", evaluation)).append(")");

        // Add some basic positional understanding
        // (This would be expanded with more sophisticated pattern recognition)
        if (evaluation > 2.0) {
            explanation.append("\nThis move gives a winning advantage!");
        } else if (evaluation > 0.5) {
            explanation.append("\nThis move gives a clear advantage.");
        } else if (evaluation > 0.2) {
            explanation.append("\nThis move gives a slight advantage.");
        } else if (evaluation < -2.0) {
            explanation.append("\nTrying to minimize a losing position.");
        } else if (evaluation < -0.5) {
            explanation.append("\nTrying to equalize from a worse position.");
        } else {
            explanation.append("\nThis move keeps the position balanced.");
        }

        return explanation.toString();
    }

    /**
     * Stops the engine process.
     */
    public void stopEngine() {
        if (isRunning.get()) {
            isRunning.set(false);

            try {
                // Send quit command
                sendCommand("quit");

                // Wait for the process to exit
                process.waitFor(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                Log.w(TAG, "Error stopping engine gracefully", e);
            } finally {
                // Force close everything
                try {
                    if (reader != null) reader.close();
                } catch (Exception e) {
                    Log.w(TAG, "Error closing reader", e);
                }

                try {
                    if (writer != null) writer.close();
                } catch (Exception e) {
                    Log.w(TAG, "Error closing writer", e);
                }

                if (process != null) {
                    process.destroy();
                }

                process = null;
                reader = null;
                writer = null;
            }
        }
    }

    /**
     * Tells the engine that a new game is about to begin.
     *
     * @return true if the command was successful
     */
    public boolean newGame() {
        try {
            sendCommand("ucinewgame");
            return waitForReady(1000);
        } catch (IOException e) {
            Log.e(TAG, "Error starting new game", e);
            return false;
        }
    }

    /**
     * Checks if a move is legal from the current position.
     */
    public boolean isLegalMove(String move) {
        try {
            Log.d(TAG, "Checking if move is legal: " + move);
            String posCommand = "position fen " + currentFEN + " moves " + move;
            sendCommand(posCommand);

            // Log the result
            boolean isValid = waitForReady(100);
            Log.d(TAG, "Move " + move + " is " + (isValid ? "legal" : "illegal"));
            return isValid;
        } catch (IOException e) {
            Log.e(TAG, "Error checking legal move", e);
            return false;
        }
    }

    public List<String> getLegalMovesForPiece(int row, int col) {
        List<String> moves = new ArrayList<>();
        try {
            // Convert board coordinates to algebraic notation
            char file = (char) ('a' + col);
            int rank = 8 - row;
            String fromSquare = "" + file + rank;

            Log.d(TAG, "Getting legal moves for piece at " + fromSquare);

            // Get current FEN
            String currentFen = getCurrentFEN();
            if (currentFen == null) {
                Log.w(TAG, "No current FEN available");
                return moves;
            }

            // Set the position
            sendCommand("position fen " + currentFen);
            waitForReady(100);

            // Clear output buffer
            outputBuffer.clear();

            // Use Stockfish's "go perft 1" command to get all legal moves
            sendCommand("go perft 1");

            // Wait for response
            Thread.sleep(200);

            // NEW: Better parsing approach
            for (String line : outputBuffer) {
                // Look for lines that contain move information
                // Perft output typically shows: "move: count"
                if (line.contains(":") && line.length() >= 4) {
                    String[] parts = line.split(":");
                    if (parts.length >= 1) {
                        String moveCandidate = parts[0].trim();
                        // Check if this move starts from our square
                        if (moveCandidate.length() >= 4 &&
                                moveCandidate.startsWith(fromSquare)) {
                            moves.add(moveCandidate);
                            Log.d(TAG, "Found legal move: " + moveCandidate);
                        }
                    }
                }

                // Also check for simple space-separated format
                String[] tokens = line.trim().split("\\s+");
                for (String token : tokens) {
                    if (token.length() >= 4 &&
                            token.startsWith(fromSquare) &&
                            token.matches("[a-h][1-8][a-h][1-8][qrbn]?")) {
                        if (!moves.contains(token)) {
                            moves.add(token);
                            Log.d(TAG, "Found legal move (token): " + token);
                        }
                    }
                }
            }

            // If we still don't have moves, try a different approach
            if (moves.isEmpty()) {
                Log.d(TAG, "Perft parsing failed, trying manual generation for " + fromSquare);

                // Generate candidate moves and test them
                char piece = getPieceAtSquare(row, col);
                if (piece != ' ') {
                    moves = generateCandidateMovesForPiece(piece, fromSquare, currentFen);
                }
            }

            Log.d(TAG, "Found " + moves.size() + " legal moves for piece at " + fromSquare);
            return moves;

        } catch (Exception e) {
            Log.e(TAG, "Error getting legal moves for piece at " + row + "," + col, e);
            return moves;
        }
    }

    /**
     * NEW HELPER METHOD: Generate candidate moves for a specific piece type
     */
    private List<String> generateCandidateMovesForPiece(char piece, String fromSquare, String fen) {
        List<String> candidateMoves = new ArrayList<>();

        char file = fromSquare.charAt(0);
        int rank = Character.getNumericValue(fromSquare.charAt(1));

        // Only generate reasonable candidate moves based on piece type
        switch (Character.toLowerCase(piece)) {
            case 'p': // Pawn
                generatePawnMoves(candidateMoves, file, rank, Character.isUpperCase(piece));
                break;
            case 'r': // Rook
                generateRookMoves(candidateMoves, file, rank);
                break;
            case 'n': // Knight
                generateKnightMoves(candidateMoves, file, rank);
                break;
            case 'b': // Bishop
                generateBishopMoves(candidateMoves, file, rank);
                break;
            case 'q': // Queen
                generateQueenMoves(candidateMoves, file, rank);
                break;
            case 'k': // King
                generateKingMoves(candidateMoves, file, rank);
                break;
        }

        // Test each candidate move to see if it's legal
        List<String> legalMoves = new ArrayList<>();
        for (String move : candidateMoves) {
            if (isLegalMove(move)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Helper methods for generating candidate moves by piece type
     */
    private void generatePawnMoves(List<String> moves, char file, int rank, boolean isWhite) {
        String from = "" + file + rank;

        // Forward moves
        if (isWhite && rank < 8) {
            moves.add(from + file + (rank + 1)); // One forward
            if (rank == 2) {
                moves.add(from + file + (rank + 2)); // Two forward from starting position
            }
        } else if (!isWhite && rank > 1) {
            moves.add(from + file + (rank - 1)); // One forward
            if (rank == 7) {
                moves.add(from + file + (rank - 2)); // Two forward from starting position
            }
        }

        // Captures
        for (int df = -1; df <= 1; df += 2) { // Left and right
            char newFile = (char) (file + df);
            if (newFile >= 'a' && newFile <= 'h') {
                if (isWhite && rank < 8) {
                    moves.add(from + newFile + (rank + 1));
                } else if (!isWhite && rank > 1) {
                    moves.add(from + newFile + (rank - 1));
                }
            }
        }
    }

    private void generateRookMoves(List<String> moves, char file, int rank) {
        String from = "" + file + rank;

        // Horizontal and vertical moves
        for (int r = 1; r <= 8; r++) {
            if (r != rank) moves.add(from + file + r);
        }
        for (char f = 'a'; f <= 'h'; f++) {
            if (f != file) moves.add(from + f + rank);
        }
    }

    private void generateKnightMoves(List<String> moves, char file, int rank) {
        String from = "" + file + rank;

        // Knight moves: 2+1 in all combinations
        int[] dr = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] df = {-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < 8; i++) {
            char newFile = (char) (file + df[i]);
            int newRank = rank + dr[i];

            if (newFile >= 'a' && newFile <= 'h' && newRank >= 1 && newRank <= 8) {
                moves.add(from + newFile + newRank);
            }
        }
    }

    private void generateBishopMoves(List<String> moves, char file, int rank) {
        String from = "" + file + rank;

        // Diagonal moves
        for (int d = 1; d < 8; d++) {
            // Four diagonal directions
            char[] newFiles = {(char)(file + d), (char)(file - d), (char)(file + d), (char)(file - d)};
            int[] newRanks = {rank + d, rank + d, rank - d, rank - d};

            for (int i = 0; i < 4; i++) {
                if (newFiles[i] >= 'a' && newFiles[i] <= 'h' &&
                        newRanks[i] >= 1 && newRanks[i] <= 8) {
                    moves.add(from + newFiles[i] + newRanks[i]);
                }
            }
        }
    }

    private void generateQueenMoves(List<String> moves, char file, int rank) {
        // Queen = Rook + Bishop
        generateRookMoves(moves, file, rank);
        generateBishopMoves(moves, file, rank);
    }

    private void generateKingMoves(List<String> moves, char file, int rank) {
        String from = "" + file + rank;

        // One square in all directions
        for (int dr = -1; dr <= 1; dr++) {
            for (int df = -1; df <= 1; df++) {
                if (dr == 0 && df == 0) continue; // Skip current position

                char newFile = (char) (file + df);
                int newRank = rank + dr;

                if (newFile >= 'a' && newFile <= 'h' && newRank >= 1 && newRank <= 8) {
                    moves.add(from + newFile + newRank);
                }
            }
        }
    }

    /**
     * Helper to get the piece at a specific square
     */
    private char getPieceAtSquare(int row, int col) {
        try {
            String fen = getCurrentFEN();
            if (fen == null) return ' ';

            String[] parts = fen.split(" ");
            String boardPart = parts[0];
            String[] ranks = boardPart.split("/");

            if (row >= 0 && row < ranks.length) {
                String rank = ranks[row];
                int currentCol = 0;

                for (char c : rank.toCharArray()) {
                    if (Character.isDigit(c)) {
                        currentCol += Character.getNumericValue(c);
                    } else {
                        if (currentCol == col) {
                            return c;
                        }
                        currentCol++;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting piece at square", e);
        }

        return ' ';
    }

    /**
     * Get the current position in FEN notation
     */
    public String getCurrentFEN() {
        try {
            outputBuffer.clear();
            sendCommand("d");

            // Wait for the response
            Thread.sleep(100);

            // Look for the FEN line in the output
            for (String line : outputBuffer) {
                if (line.startsWith("Fen: ")) {
                    currentFEN = line.substring(5).trim();
                    return currentFEN;
                }
            }

            return currentFEN; // Return the cached value if we can't get a new one
        } catch (Exception e) {
            Log.e(TAG, "Error getting current FEN", e);
            return currentFEN;
        }
    }

    /**
     * 🚨 DIAGNOSTIC METHOD: Test evaluation bug with specific positions
     * This method can be called to reproduce and debug the evaluation bug
     */
    public void debugEvaluationBug() {
        try {
            Log.d(TAG, "🔍 DEBUGGING: Starting evaluation bug reproduction test...");
            
            // Test Case 1: Position after d1g4 (queen in danger)
            String dangerousQueenPosition = "rnbqkb1r/ppp2ppp/3p1n2/4p3/2PPP1Q1/8/PP3PPP/RNB1KBNR b KQkq - 1 4";
            
            Log.d(TAG, "🔍 TEST 1: Setting dangerous queen position...");
            setPosition(dangerousQueenPosition);
            
            EvaluationResult result1 = getCurrentEvaluation(1000);
            Log.d(TAG, String.format("🔍 TEST 1 RESULT: %.2f (should be negative since Black can capture queen)", result1.evaluation));
            
            // Test Case 2: Position after c1h6 (bishop in danger)  
            String dangerousBishopPosition = "rnbqkb1r/ppp2ppp/3p3B/4p3/2PPP1n1/8/PP3PPP/RN2KBNR b KQkq - 1 5";
            
            Log.d(TAG, "🔍 TEST 2: Setting dangerous bishop position...");
            setPosition(dangerousBishopPosition);
            
            EvaluationResult result2 = getCurrentEvaluation(1000);
            Log.d(TAG, String.format("🔍 TEST 2 RESULT: %.2f (should be negative since Black can capture bishop)", result2.evaluation));
            
            // Test Case 3: Position after Black captures the queen
            String afterQueenCapture = "rnbqkb1r/ppp2ppp/3p1n2/4p3/2PPP1q1/8/PP3PPP/RNB1KBNR w KQkq - 0 5";
            
            Log.d(TAG, "🔍 TEST 3: Setting position after queen capture...");
            setPosition(afterQueenCapture);
            
            EvaluationResult result3 = getCurrentEvaluation(1000);
            Log.d(TAG, String.format("🔍 TEST 3 RESULT: %.2f (should be very negative for White)", result3.evaluation));
            
            Log.d(TAG, "🔍 DEBUGGING: Bug reproduction test completed!");
            
        } catch (Exception e) {
            Log.e(TAG, "🔍 ERROR in debugging test", e);
        }
    }

    /**
     * CRITICAL: Force stop all engine operations immediately to prevent ANR
     */
    public void forceStop() {
        Log.d(TAG, "🚨 FORCE STOPPING StockfishManager");
        
        isRunning.set(false);
        isReady = false;
        
        // Force kill the process
        if (process != null) {
            process.destroyForcibly();
            process = null;
        }
        
        // Interrupt reader thread
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
        }
        
        // Close streams
        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } catch (Exception e) {
            Log.w(TAG, "Error closing streams during force stop", e);
        }
        
        // Clear output buffer
        outputBuffer.clear();
        
        Log.d(TAG, "✅ StockfishManager FORCE STOPPED");
    }

    /**
     * 🚨 NEW: Attempts to recover the engine when it crashes
     */
    private synchronized boolean attemptEngineRecovery() {
        if (isRecovering.get()) {
            Log.d(TAG, "Recovery already in progress, waiting...");
            return waitForRecovery();
        }

        if (enginePath == null) {
            Log.e(TAG, "❌ Cannot recover: no engine path stored");
            return false;
        }

        isRecovering.set(true);
        try {
            Log.i(TAG, "🔄 Starting engine recovery process...");

            // Step 1: Force stop current engine
            forceStop();
            Thread.sleep(500); // Brief pause

            // Step 2: Restart engine
            if (!startEngineInternal(enginePath)) {
                Log.e(TAG, "❌ Failed to restart engine during recovery");
                return false;
            }

            // Step 3: Restore game position
            if (!restoreGamePosition()) {
                Log.e(TAG, "❌ Failed to restore game position during recovery");
                return false;
            }

            Log.i(TAG, "🎉 Engine recovery completed successfully!");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "💥 Engine recovery failed", e);
            return false;
        } finally {
            isRecovering.set(false);
        }
    }

    /**
     * 🔄 Restores the current game position after engine restart
     */
    private boolean restoreGamePosition() {
        try {
            if (moveHistory.isEmpty()) {
                Log.d(TAG, "📋 No move history to restore, using starting position");
                currentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
                return true;
            }

            Log.d(TAG, "📋 Restoring position with " + moveHistory.size() + " moves");
            
            // Build position command with all moves
            StringBuilder posCommand = new StringBuilder("position startpos");
            if (!moveHistory.isEmpty()) {
                posCommand.append(" moves");
                for (String move : moveHistory) {
                    posCommand.append(" ").append(move);
                }
            }

            // Send the position and wait for ready
            sendCommandDirect(posCommand.toString());
            if (waitForReady(2000)) {
                // Update FEN after successful restoration
                currentFEN = getCurrentFEN();
                Log.i(TAG, "✅ Position restored successfully");
                return true;
            } else {
                Log.e(TAG, "❌ Engine not ready after position restoration");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "💥 Failed to restore game position", e);
            return false;
        }
    }

    /**
     * 📡 Sends command directly without recovery (used during recovery process)
     */
    private void sendCommandDirect(String command) throws IOException {
        if (writer == null || !isRunning.get()) {
            throw new IOException("Engine not running (direct send)");
        }
        Log.d(TAG, "Sending (direct): " + command);
        writer.write(command + "\n");
        writer.flush();
    }

    /**
     * ⏳ Waits for recovery to complete
     */
    private boolean waitForRecovery() {
        int attempts = 0;
        while (isRecovering.get() && attempts < 50) { // Max 5 seconds
            try {
                Thread.sleep(100);
                attempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return !isRecovering.get() && isRunning.get();
    }

    /**
     * 💓 Checks if the engine process is actually alive
     */
    public boolean isEngineAlive() {
        return process != null && process.isAlive() && isRunning.get();
    }

    /**
     * 📋 Adds a move to the history for recovery purposes
     */
    public void addMoveToHistory(String move) {
        if (move != null && !move.trim().isEmpty()) {
            moveHistory.add(move);
            Log.d(TAG, "📝 Added move to history: " + move + " (total: " + moveHistory.size() + ")");
        }
    }

    /**
     * 🗑️ Clears move history (for new games)
     */
    public void clearMoveHistory() {
        moveHistory.clear();
        currentFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Log.d(TAG, "🗑️ Move history cleared");
    }

    /**
     * 📊 Gets current move history
     */
    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }
}