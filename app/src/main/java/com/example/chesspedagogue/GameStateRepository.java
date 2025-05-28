package com.example.chesspedagogue;

import java.util.List;

/**
 * Repository for accessing game state
 */
public class GameStateRepository {
    private static GameStateInfo currentState = new GameStateInfo();

    public static GameStateInfo getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(GameStateInfo state) {
        currentState = state;
    }

    // Add this method to fix compilation errors
    public static void updateState(String newFen, List<String> moveHistory, String playerColor) {
        currentState.setCurrentFen(newFen);
        currentState.setMoveHistory(moveHistory);
        currentState.setPlayerColor(playerColor);

        // Set game phase based on move count
        int moveCount = moveHistory.size();
        if (moveCount < 10) {
            currentState.setGamePhase("opening");
        } else if (moveCount < 30) {
            currentState.setGamePhase("middlegame");
        } else {
            currentState.setGamePhase("endgame");
        }
    }
}