// Add this as a new file: GameStateInfo.java
package com.example.chesspedagogue;

import java.util.List;

public class GameStateInfo {
    private String currentFen;
    private List<String> moveHistory;
    private String playerColor;

    public GameStateInfo(String currentFen, List<String> moveHistory, String playerColor) {
        this.currentFen = currentFen;
        this.moveHistory = moveHistory;
        this.playerColor = playerColor;
    }

    // Getters
    public String getCurrentFen() { return currentFen; }
    public List<String> getMoveHistory() { return moveHistory; }
    public String getPlayerColor() { return playerColor; }
}