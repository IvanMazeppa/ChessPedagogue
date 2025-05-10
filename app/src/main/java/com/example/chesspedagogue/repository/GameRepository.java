// GameRepository.java in com.example.chesspedagogue.repository
package com.example.chesspedagogue.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Context;

import com.example.chesspedagogue.StockfishManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRepository {
    private static final String TAG = "GameRepository";

    // This is the key - we need to make stockfishManager public for methods or add proper methods
    public final StockfishManager stockfishManager;
    private String cachedFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context;

    public GameRepository(Context context) {

        this.context = context;
        stockfishManager = new StockfishManager();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        initializeEngine();

        // Initialize Stockfish on a background thread
        executorService.execute(() -> {
            try {
                // To include the engine path (using your existing initialization code):
                File engineFile = new File(context.getApplicationInfo().nativeLibraryDir, "libstockfish.so");
                stockfishManager.startEngine(engineFile.getAbsolutePath());
                stockfishManager.newGame();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Stockfish", e);
            }
        });
    }

    // NEW METHOD - more robust initialization
    private void initializeEngine() {
        try {
            // Do the basic initialization on the main thread to avoid race conditions
            File engineFile = new File(context.getApplicationInfo().nativeLibraryDir, "libstockfish.so");
            if (!engineFile.exists()) {
                Log.e(TAG, "Engine file not found at: " + engineFile.getAbsolutePath());
                return;
            }

            // Start the engine synchronously - this is important!
            boolean success = stockfishManager.startEngine(engineFile.getAbsolutePath());
            if (success) {
                // Initialize with default settings after successful start
                stockfishManager.setSkillLevel(10); // Default skill level
                stockfishManager.newGame();
                Log.d(TAG, "Engine initialized successfully! 🎉");
            } else {
                Log.e(TAG, "Failed to initialize Stockfish engine");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Stockfish", e);
        }
    }

    public boolean isLegalMove(String move) {
        return stockfishManager.isLegalMove(move);
    }

    // In GameRepository.java
    // Fix the setPositionFromMoves method
    public boolean setPositionFromMoves(String... moves) {
        try {
            StringBuilder command = new StringBuilder("position startpos");
            if (moves.length > 0) {
                command.append(" moves");
                for (String move : moves) {
                    command.append(" ").append(move);
                }
            }

            // Use stockfishManager to send the command
            stockfishManager.sendCommand(command.toString());

            // Wait for the engine to be ready
            boolean success = stockfishManager.waitForReady(1000);

            // Update our cached FEN
            cachedFEN = stockfishManager.getCurrentFEN();

            return success;
        } catch (IOException e) {
            Log.e(TAG, "Error setting position from moves", e);
            return false;
        }
    }

    // Make this method return boolean so GameViewModel can check success
    public boolean makeMove(String move) {
        try {
            // Create a complete command with the move
            StringBuilder command = new StringBuilder("position startpos");

            // Get existing moves from history if needed
            // (We might need to add this logic depending on how you're tracking move history)

            // Add the new move
            command.append(" moves ").append(move);

            // Send the command to Stockfish
            stockfishManager.sendCommand(command.toString());

            // Wait for the engine to process
            boolean success = stockfishManager.waitForReady(1000);

            // Update our cached FEN after the move
            if (success) {
                cachedFEN = stockfishManager.getCurrentFEN();
            }

            return success;
        } catch (IOException e) {
            // Log the error
            Log.e(TAG, "Error making move: " + move, e);
            return false;
        }
    }

    // Add other necessary methods
    public List<String> getLegalMovesForPiece(int row, int col) {
        return stockfishManager.getLegalMovesForPiece(row, col);
    }

    public String getCurrentFEN() {
        return stockfishManager.getCurrentFEN();
    }

    // Replace these check/checkmate methods with simpler implementations:
    public boolean isCheckmate() {
        // Temporary implementation until we integrate with your existing methods
        String fen = stockfishManager.getCurrentFEN();
        return fen != null && fen.contains("#"); // Simple placeholder check
    }

    public boolean isStalemate() {
        // Temporary implementation
        return false; // We'll enhance this later
    }

    public interface MoveCallback {
        void onMoveCalculated(String move);
        void onError(String errorMessage); // Add this method
    }

    public void calculateBestMove(MoveCallback callback) {
        // Execute this on a background thread
        executorService.execute(() -> {
            try {
                // Let Stockfish think for a moment
                String bestMove = stockfishManager.getBestMove(1000); // 1 second think time

                // Return the result on the main thread
                mainHandler.post(() -> callback.onMoveCalculated(bestMove));
            } catch (Exception e) {
                Log.e(TAG, "Error calculating best move", e);
                mainHandler.post(() -> callback.onMoveCalculated("e2e4")); // Fallback move
            }
        });
    }

    // In GameRepository:
    public void makeEngineMove(MoveCallback callback) {
        executorService.submit(() -> {
            try {
                // Engine operations on background thread
                String engineMove = stockfishManager.getBestMove(1000);

                // Return to main thread for callback
                mainHandler.post(() -> callback.onMoveCalculated(engineMove));
            } catch (Exception e) {
                Log.e(TAG, "Engine error", e);
                mainHandler.post(() -> callback. onError("Engine error: " + e.getMessage()));
            }
        });
    }

    // Add this to GameRepository.java
    public boolean applyMoves(List<String> moves) {
        try {
            // Create a command with ALL moves
            StringBuilder command = new StringBuilder("position startpos");
            if (moves != null && !moves.isEmpty()) {
                command.append(" moves");
                for (String m : moves) {
                    command.append(" ").append(m);
                }
            }

            // Send the command
            stockfishManager.sendCommand(command.toString());

            // Wait for the engine to process
            boolean success = stockfishManager.waitForReady(1000);

            // Update our cached FEN after applying moves
            if (success) {
                cachedFEN = stockfishManager.getCurrentFEN();
            }

            return success;
        } catch (IOException e) {
            Log.e(TAG, "Error applying moves: " + e.getMessage(), e);
            return false;
        }
    }

    public void newGame() {
        try {
            stockfishManager.newGame();
        } catch (Exception e) {
            Log.e(TAG, "Error starting new game", e);
        }
    }

    public void cleanup() {
        executorService.shutdown();
        stockfishManager.stopEngine();
    }
}