package com.example.chesspedagogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to hold game state information
 */
public class GameStateInfo {
    private String currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private String playerColor = "white";
    private String gamePhase = "opening";
    private boolean isCheck = false;
    private List<String> moveHistory = new ArrayList<>();

    public String getCurrentFen() {
        return currentFen;
    }

    public void setCurrentFen(String currentFen) {
        this.currentFen = currentFen;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public String getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(String gamePhase) {
        this.gamePhase = gamePhase;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public List<String> getMoveHistory() {
        return moveHistory;
    }

    public void setMoveHistory(List<String> moveHistory) {
        this.moveHistory = moveHistory;
    }

    public void addMove(String move) {
        moveHistory.add(move);
    }

    // Add this method to fix the compilation error
    public int getMoveCount() {
        return moveHistory != null ? moveHistory.size() : 0;
    }
}