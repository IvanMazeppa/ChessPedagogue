package com.example.chesspedagogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive game state information class that provides all context
 * needed for AI analysis, with optimized formatting for LLM understanding.
 */
public class GameStateInfo {
    private final String currentFen;
    private final List<String> moveHistory;
    private final String playerColor;
    private final int moveCount;
    private final String gamePhase;
    private final boolean isCheck;
    private final boolean isCheckmate;

    /**
     * Create a complete game state object with all required context
     * @param currentFen The current position in FEN format
     * @param moveHistory The complete list of moves in algebraic notation
     * @param playerColor The player's color ("white" or "black")
     */
    public GameStateInfo(String currentFen, List<String> moveHistory, String playerColor) {
        this.currentFen = currentFen;
        this.moveHistory = moveHistory != null ? moveHistory : new ArrayList<>();
        this.playerColor = playerColor;

        // Calculate derived properties
        this.moveCount = this.moveHistory.size();

        // Determine game phase based on move count
        if (moveCount < 10) {
            this.gamePhase = "opening";
        } else if (moveCount < 30) {
            this.gamePhase = "middlegame";
        } else {
            this.gamePhase = "endgame";
        }

        // Determine check/checkmate status from FEN
        this.isCheck = currentFen != null && currentFen.contains("+");
        this.isCheckmate = currentFen != null && currentFen.contains("#");
    }

    /**
     * Format this game state into a structured, AI-friendly representation
     * that maximizes the probability of move-specific analysis
     */
    public String formatForAI() {
        StringBuilder context = new StringBuilder();

        // Include current FEN position - CRITICAL for accuracy
        context.append("Current position (FEN): ").append(currentFen).append("\n\n");

        // Format move history with proper numbering and EXTRA EMPHASIS
        context.append("*** COMPLETE MOVE HISTORY: ***\n");
        if (moveHistory.isEmpty()) {
            context.append("No moves have been played yet.\n");
        } else {
            int moveNumber = 1;
            for (int i = 0; i < moveHistory.size(); i += 2) {
                context.append(moveNumber).append(". ");
                context.append(moveHistory.get(i));

                if (i + 1 < moveHistory.size()) {
                    context.append(" ").append(moveHistory.get(i + 1));
                }
                context.append("\n");
                moveNumber++;
            }
        }

        // Include player color and game phase with emphasis
        context.append("\nPlayer is playing as: ").append(playerColor).append("\n");
        context.append("Current game phase: ").append(gamePhase).append("\n");

        // Add status indicators
        if (isCheck) {
            context.append("IMPORTANT: King is in check.\n");
        }
        if (isCheckmate) {
            context.append("IMPORTANT: Position is checkmate.\n");
        }

        // Add explicit instruction with strong emphasis
        context.append("\n*** YOUR ANALYSIS MUST BEGIN BY REFERRING TO A SPECIFIC MOVE NUMBER FROM THE ABOVE HISTORY ***");

        return context.toString();
    }

    // Getters
    public String getCurrentFen() { return currentFen; }
    public List<String> getMoveHistory() { return moveHistory; }
    public String getPlayerColor() { return playerColor; }
    public int getMoveCount() { return moveCount; }
    public String getGamePhase() { return gamePhase; }
    public boolean isCheck() { return isCheck; }
    public boolean isCheckmate() { return isCheckmate; }

    /**
     * Get the most recent move, or null if no moves
     */
    public String getLastMove() {
        if (moveHistory != null && !moveHistory.isEmpty()) {
            return moveHistory.get(moveHistory.size() - 1);
        }
        return null;
    }

    /**
     * Get formatted most recent move with number
     */
    public String getLastMoveFormatted() {
        if (moveHistory == null || moveHistory.isEmpty()) {
            return "No moves played yet";
        }

        int moveIndex = moveHistory.size() - 1;
        int moveNumber = (moveIndex / 2) + 1;
        boolean isWhiteMove = (moveIndex % 2 == 0);

        if (isWhiteMove) {
            return moveNumber + ". " + moveHistory.get(moveIndex);
        } else {
            return moveNumber + "... " + moveHistory.get(moveIndex);
        }
    }
}