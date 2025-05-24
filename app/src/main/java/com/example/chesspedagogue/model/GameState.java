// GameState.java in com.example.chesspedagogue.model
package com.example.chesspedagogue.model;

import java.util.ArrayList;
import java.util.List;

public class

GameState {
    private String fen;
    private boolean isPlayerTurn;
    private boolean isGameOver;
    private List<String> moveHistory;
    private String playerColor;

    public GameState() {
        // Default initial state
        this.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        this.isPlayerTurn = true;
        this.isGameOver = false;
        this.moveHistory = new ArrayList<>();
        this.playerColor = "white";
    }

    // Getters and setters
    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public void setPlayerTurn(boolean playerTurn) { isPlayerTurn = playerTurn; }

    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }

    public List<String> getMoveHistory() { return moveHistory; }
    public void setMoveHistory(List<String> moveHistory) { this.moveHistory = moveHistory; }

    public String getPlayerColor() { return playerColor; }
    public void setPlayerColor(String playerColor) { this.playerColor = playerColor; }
}