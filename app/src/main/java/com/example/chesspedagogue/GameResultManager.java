package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * 🏆 Game Result Manager
 * 
 * Handles game endings with proper PGN notation, victory celebrations,
 * and result display for both regular and spectator modes.
 * 
 * Features:
 * - Appends PGN result notation (1-0, 0-1, 1/2-1/2) to move list
 * - Victory celebrations for user wins and AI triumphs
 * - Master-specific reactions in spectator mode
 * - Proper game ending detection and display
 */
public class GameResultManager {
    private static final String TAG = "🏆 GameResultManager";
    private static GameResultManager instance;
    
    private final Context context;
    
    // Game result types with PGN notation
    public enum GameResult {
        ONGOING("", "Game in progress"),
        WHITE_WINS("1-0", "White wins"),
        BLACK_WINS("0-1", "Black wins"),
        DRAW("1/2-1/2", "Draw");
        
        public final String pgnNotation;
        public final String description;
        
        GameResult(String pgnNotation, String description) {
            this.pgnNotation = pgnNotation;
            this.description = description;
        }
    }
    
    // Game ending reasons
    public enum EndingReason {
        CHECKMATE("by checkmate"),
        STALEMATE("by stalemate"),
        RESIGNATION("by resignation"),
        TIME_OUT("on time"),
        INSUFFICIENT_MATERIAL("insufficient material"),
        FIFTY_MOVE_RULE("by fifty-move rule"),
        THREEFOLD_REPETITION("by repetition"),
        MUTUAL_AGREEMENT("by agreement");
        
        public final String description;
        
        EndingReason(String description) {
            this.description = description;
        }
    }
    
    // Current game state
    private GameResult currentResult = GameResult.ONGOING;
    private EndingReason endingReason = null;
    private String winnerName = "";
    private boolean gameEnded = false;
    
    public static GameResultManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameResultManager(context);
        }
        return instance;
    }
    
    private GameResultManager(Context context) {
        this.context = context.getApplicationContext();
        Log.d(TAG, "🚀 Game Result Manager initialized");
    }
    
    /**
     * End the game with a specific result and reason
     */
    public void endGame(GameResult result, EndingReason reason, String winner) {
        if (gameEnded) return; // Prevent multiple endings
        
        this.currentResult = result;
        this.endingReason = reason;
        this.winnerName = winner;
        this.gameEnded = true;
        
        Log.d(TAG, String.format("🏁 Game ended: %s %s - Winner: %s", 
                                result.description, reason.description, winner));
    }
    
    /**
     * Update move history display with PGN result notation
     */
    public String formatMoveHistoryWithResult(List<String> moves) {
        if (moves == null || moves.isEmpty()) {
            return "Game begins...";
        }
        
        StringBuilder historyBuilder = new StringBuilder(moves.size() * 10);
        
        // Format moves in standard PGN notation
        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                // White move - start new line with move number
                int moveNumber = (i / 2) + 1;
                historyBuilder.append(moveNumber).append(". ").append(moves.get(i));
                
                // Add space after white's move if there's a black move coming
                if (i + 1 < moves.size()) {
                    historyBuilder.append(" ");
                }
            } else {
                // Black move - complete the line
                historyBuilder.append(moves.get(i));
                
                // Add newline after black's move (except for the last move)
                if (i + 1 < moves.size()) {
                    historyBuilder.append("\n");
                }
            }
        }
        
        // ✨ NEW: Append PGN result notation if game has ended
        if (gameEnded && currentResult != GameResult.ONGOING) {
            // Add extra newline if last move was black (even number of moves)
            if (moves.size() % 2 == 0) {
                historyBuilder.append("\n");
            } else {
                historyBuilder.append(" ");
            }
            
            // Add the PGN result notation
            historyBuilder.append(currentResult.pgnNotation);
            
            Log.d(TAG, String.format("📝 Added PGN result notation: %s", currentResult.pgnNotation));
        }
        
        return historyBuilder.toString();
    }
    
    /**
     * Update move history display for spectator mode with enhanced formatting
     */
    public String formatSpectatorMoveHistoryWithResult(List<String> moves, String whitePlayer, String blackPlayer) {
        if (moves == null || moves.isEmpty()) {
            return "🎭 Epic battle begins...";
        }
        
        StringBuilder historyBuilder = new StringBuilder();
        historyBuilder.append(String.format("🎭 %s vs %s\n\n", whitePlayer, blackPlayer));
        
        // Format moves
        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                // White move
                int moveNumber = (i / 2) + 1;
                historyBuilder.append(moveNumber).append(". ").append(moves.get(i));
                if (i + 1 < moves.size()) {
                    historyBuilder.append(" ");
                }
            } else {
                // Black move
                historyBuilder.append(moves.get(i));
                if (i + 1 < moves.size()) {
                    historyBuilder.append("\n");
                }
            }
        }
        
        // ✨ NEW: Add result notation and celebration for spectator mode
        if (gameEnded && currentResult != GameResult.ONGOING) {
            // Add spacing
            if (moves.size() % 2 == 0) {
                historyBuilder.append("\n");
            } else {
                historyBuilder.append(" ");
            }
            
            // Add result with celebration
            historyBuilder.append(currentResult.pgnNotation);
            historyBuilder.append("\n\n🏆 ").append(generateSpectatorCelebration());
            
            Log.d(TAG, String.format("🎭 Added spectator result: %s with celebration", currentResult.pgnNotation));
        }
        
        return historyBuilder.toString();
    }
    
    /**
     * Generate celebration message for spectator mode
     */
    private String generateSpectatorCelebration() {
        if (currentResult == GameResult.DRAW) {
            return "What an incredible battle! Both masters showed their brilliance!";
        }
        
        String winner = currentResult == GameResult.WHITE_WINS ? "White" : "Black";
        String loser = currentResult == GameResult.WHITE_WINS ? "Black" : "White";
        
        switch (endingReason) {
            case CHECKMATE:
                return String.format("Magnificent checkmate! %s's victory is complete! %s played valiantly!", winner, loser);
            case RESIGNATION:
                return String.format("%s's position was so strong that %s had to concede! Masterful play!", winner, loser);
            case TIME_OUT:
                return String.format("%s wins on time! The clock is a ruthless opponent!", winner);
            default:
                return String.format("Victory to %s! What an incredible game between these chess legends!", winner);
        }
    }
    
    /**
     * Generate victory celebration for user vs AI games
     */
    public String generateUserVictoryCelebration(boolean userWon, String userColor) {
        if (currentResult == GameResult.DRAW) {
            return "🤝 A hard-fought draw! You held your own against a strong opponent!";
        }
        
        if (userWon) {
            switch (endingReason) {
                case CHECKMATE:
                    return "🎉 CHECKMATE! Brilliant victory! You've shown true chess mastery!";
                case RESIGNATION:
                    return "🏆 The AI resigned! Your position was completely winning! Excellent play!";
                case TIME_OUT:
                    return "⏰ Victory on time! You managed your clock perfectly!";
                default:
                    return "🌟 Congratulations! You've achieved a fantastic victory!";
            }
        } else {
            switch (endingReason) {
                case CHECKMATE:
                    return "♟️ A tough loss, but great effort! Learn from this game and come back stronger!";
                case RESIGNATION:
                    return "🤖 The AI's position was too strong. Keep practicing - you're improving!";
                case TIME_OUT:
                    return "⏰ Time pressure got you this time. Work on your time management!";
                default:
                    return "💪 Good game! Every loss is a learning opportunity!";
            }
        }
    }
    
    /**
     * Display game result in GameResultView
     */
    public void displayGameResult(GameResultView gameResultView) {
        if (gameResultView == null || !gameEnded) return;
        
        String celebration = generateUserVictoryCelebration(
            isUserWin(), getCurrentUserColor()
        );
        
        switch (currentResult) {
            case WHITE_WINS:
                gameResultView.showWhiteWins(winnerName, endingReason.description);
                break;
            case BLACK_WINS:
                gameResultView.showBlackWins(winnerName, endingReason.description);
                break;
            case DRAW:
                gameResultView.showDraw(endingReason.description);
                break;
        }
        
        Log.d(TAG, String.format("🎭 Game result displayed: %s %s", 
                                currentResult.description, endingReason.description));
    }
    
    /**
     * Quick game ending methods
     */
    public void endGameCheckmate(String winner, boolean isWhite) {
        GameResult result = isWhite ? GameResult.WHITE_WINS : GameResult.BLACK_WINS;
        endGame(result, EndingReason.CHECKMATE, winner);
    }
    
    public void endGameResignation(String winner, boolean isWhite) {
        GameResult result = isWhite ? GameResult.WHITE_WINS : GameResult.BLACK_WINS;
        endGame(result, EndingReason.RESIGNATION, winner);
    }
    
    public void endGameStalemate() {
        endGame(GameResult.DRAW, EndingReason.STALEMATE, "");
    }
    
    public void endGameTimeout(String winner, boolean isWhite) {
        GameResult result = isWhite ? GameResult.WHITE_WINS : GameResult.BLACK_WINS;
        endGame(result, EndingReason.TIME_OUT, winner);
    }
    
    public void endGameDraw(EndingReason reason) {
        endGame(GameResult.DRAW, reason, "");
    }
    
    /**
     * Check if user won (for victory celebrations)
     */
    private boolean isUserWin() {
        String userColor = getCurrentUserColor();
        if (userColor == null) return false;
        
        if ("white".equals(userColor) && currentResult == GameResult.WHITE_WINS) {
            return true;
        } else if ("black".equals(userColor) && currentResult == GameResult.BLACK_WINS) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get current user color (would need to be set by calling activity)
     */
    private String getCurrentUserColor() {
        // This would be set by the calling activity
        // For now, return null (spectator mode) or implement based on game state
        return null; // Placeholder
    }
    
    /**
     * Reset for new game
     */
    public void resetForNewGame() {
        currentResult = GameResult.ONGOING;
        endingReason = null;
        winnerName = "";
        gameEnded = false;
        
        Log.d(TAG, "🔄 Game result manager reset for new game");
    }
    
    /**
     * Getters
     */
    public boolean isGameEnded() { return gameEnded; }
    public GameResult getCurrentResult() { return currentResult; }
    public EndingReason getEndingReason() { return endingReason; }
    public String getWinnerName() { return winnerName; }
    public String getPgnNotation() { return currentResult.pgnNotation; }
    
    /**
     * Get result description for display
     */
    public String getResultDescription() {
        if (!gameEnded) return "Game in progress";
        
        if (currentResult == GameResult.DRAW) {
            return String.format("Draw %s", endingReason.description);
        } else {
            return String.format("%s %s", winnerName, endingReason.description);
        }
    }
}