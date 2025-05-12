// Create a new file: GameStateRepository.java
package com.example.chesspedagogue;
import com.example.chesspedagogue.GameStateRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Central repository for managing game state in a thread-safe manner
 */
public class GameStateRepository {
    private static GameStateInfo currentState = null;

    /**
     * Get the current game state (never returns null)
     */
    public static synchronized GameStateInfo getCurrentState() {
        if (currentState == null) {
            // Default to starting position
            currentState = new GameStateInfo(
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                    new ArrayList<>(),
                    "white"
            );
        }
        return currentState;
    }

    /**
     * Update the current game state
     */
    public static synchronized void updateState(String fen, List<String> moveHistory, String playerColor) {
        // Create a defensive copy of the move history to prevent modifications
        List<String> moveHistoryCopy = new ArrayList<>(moveHistory);

        // Update the state
        currentState = new GameStateInfo(fen, moveHistoryCopy, playerColor);

        // Log the update for debugging
        android.util.Log.d("GameState", "Updated state: FEN=" + fen +
                ", moves=" + moveHistoryCopy.size() +
                ", player=" + playerColor);
    }
}