import com.example.chesspedagogue.GameStateInfo;

import java.util.ArrayList;
import java.util.List;

// Create this new class
public class GameStateRepository {
    // Current game state (singleton pattern)
    private static GameStateInfo currentGameState;

    // Get the current state - never returns null
    public static synchronized GameStateInfo getCurrentState() {
        if (currentGameState == null) {
            // Initialize with starting position if not set
            currentGameState = new GameStateInfo(
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                    new ArrayList<>(),
                    "white"
            );
        }
        return currentGameState;
    }

    // Update state when moves are made
    public static synchronized void updateState(String fen, List<String> moveHistory, String playerColor) {
        currentGameState = new GameStateInfo(fen, new ArrayList<>(moveHistory), playerColor);

        // Log the update to verify state is changing
        Log.d("GameState", "Updated state: FEN=" + fen + ", moves=" + moveHistory.size());
    }
}