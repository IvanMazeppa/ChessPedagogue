package com.example.chesspedagogue;

import android.widget.TextView;
import android.util.Log;

import java.util.List;

/**
 * 🏆 Game Result Integration Helper
 * 
 * Provides easy integration methods for MainActivity and SpectatorGameActivity
 * to add PGN result notation and victory celebrations.
 * 
 * This class bridges the gap between the new GameResultManager and existing
 * move history display systems.
 */
public class GameResultIntegration {
    private static final String TAG = "🏆 GameResultIntegration";
    
    /**
     * Update MainActivity move history with PGN result notation
     */
    public static void updateMainActivityMoveHistory(TextView moveHistoryTextView, 
                                                   List<String> moves,
                                                   GameResultManager gameResultManager) {
        if (moveHistoryTextView == null) return;
        
        // Use GameResultManager to format moves with PGN notation
        String formattedHistory = gameResultManager.formatMoveHistoryWithResult(moves);
        moveHistoryTextView.setText(formattedHistory);
        
        // Log if result was added
        if (gameResultManager.isGameEnded()) {
            Log.d(TAG, String.format("📝 Main activity move history updated with result: %s", 
                                    gameResultManager.getPgnNotation()));
        }
    }
    
    /**
     * Update SpectatorGameActivity move history with enhanced formatting
     */
    public static void updateSpectatorMoveHistory(TextView moveHistoryTextView,
                                                List<String> moves,
                                                String whitePlayer,
                                                String blackPlayer,
                                                GameResultManager gameResultManager) {
        if (moveHistoryTextView == null) return;
        
        // Use GameResultManager to format spectator moves with celebrations
        String formattedHistory = gameResultManager.formatSpectatorMoveHistoryWithResult(
            moves, whitePlayer, blackPlayer
        );
        moveHistoryTextView.setText(formattedHistory);
        
        // Log if result was added
        if (gameResultManager.isGameEnded()) {
            Log.d(TAG, String.format("🎭 Spectator move history updated with result: %s", 
                                    gameResultManager.getPgnNotation()));
        }
    }
    
    /**
     * Handle game ending in MainActivity with full celebration
     */
    public static void handleMainActivityGameEnd(GameResultManager gameResultManager,
                                               GameResultView gameResultView,
                                               String winner,
                                               String gameEndType,
                                               boolean isWhite,
                                               boolean isUserWin) {
        
        // Determine ending reason
        GameResultManager.EndingReason reason = parseEndingReason(gameEndType);
        
        // End the game in result manager
        if (gameEndType.contains("draw") || gameEndType.contains("stalemate")) {
            gameResultManager.endGameDraw(reason);
        } else if (isWhite) {
            gameResultManager.endGameCheckmate(winner, true);
        } else {
            gameResultManager.endGameCheckmate(winner, false);
        }
        
        // Display celebration
        if (gameResultView != null) {
            if (isUserWin) {
                String celebration = gameResultManager.generateUserVictoryCelebration(true, isWhite ? "white" : "black");
                gameResultView.showVictoryCelebration(
                    isWhite ? GameResultView.GameResult.WHITE_WINS : GameResultView.GameResult.BLACK_WINS,
                    celebration
                );
            } else {
                gameResultManager.displayGameResult(gameResultView);
            }
        }
        
        Log.d(TAG, String.format("🎊 Main activity game ended: %s wins %s", 
                                winner, reason.description));
    }
    
    /**
     * Handle game ending in SpectatorGameActivity with master celebrations
     */
    public static void handleSpectatorGameEnd(GameResultManager gameResultManager,
                                            GameResultView gameResultView,
                                            String winner,
                                            String gameEndType,
                                            boolean isWhite,
                                            String whitePlayer,
                                            String blackPlayer) {
        
        // Determine ending reason
        GameResultManager.EndingReason reason = parseEndingReason(gameEndType);
        
        // End the game in result manager
        if (gameEndType.contains("draw") || gameEndType.contains("stalemate")) {
            gameResultManager.endGameDraw(reason);
        } else if (isWhite) {
            gameResultManager.endGameCheckmate(winner, true);
        } else {
            gameResultManager.endGameCheckmate(winner, false);
        }
        
        // Display spectator celebration
        if (gameResultView != null) {
            String celebration = generateSpectatorCelebration(winner, reason, whitePlayer, blackPlayer);
            
            if (gameEndType.contains("draw")) {
                gameResultView.showVictoryCelebration(GameResultView.GameResult.DRAW, celebration);
            } else {
                gameResultView.showVictoryCelebration(
                    isWhite ? GameResultView.GameResult.WHITE_WINS : GameResultView.GameResult.BLACK_WINS,
                    celebration
                );
            }
        }
        
        Log.d(TAG, String.format("🎭 Spectator game ended: %s vs %s, %s wins %s", 
                                whitePlayer, blackPlayer, winner, reason.description));
    }
    
    /**
     * Generate master-specific celebration messages
     */
    private static String generateSpectatorCelebration(String winner, 
                                                     GameResultManager.EndingReason reason,
                                                     String whitePlayer,
                                                     String blackPlayer) {
        
        if (reason == GameResultManager.EndingReason.STALEMATE) {
            return String.format("🎭 Incredible stalemate! Both %s and %s showed masterful defense!", 
                                whitePlayer, blackPlayer);
        }
        
        String loser = winner.equals(whitePlayer) ? blackPlayer : whitePlayer;
        
        switch (reason) {
            case CHECKMATE:
                return String.format("🏆 Magnificent checkmate by %s! %s fought valiantly to the end!", 
                                   winner, loser);
            case RESIGNATION:
                return String.format("⚡ %s's position was so dominant that %s conceded! Masterful play!", 
                                   winner, loser);
            case TIME_OUT:
                return String.format("⏰ %s wins on time! Clock management proved decisive!", winner);
            default:
                return String.format("🌟 Victory to %s! What an epic battle between chess legends!", winner);
        }
    }
    
    /**
     * Parse game ending type to EndingReason enum
     */
    private static GameResultManager.EndingReason parseEndingReason(String gameEndType) {
        String lowerType = gameEndType.toLowerCase();
        
        if (lowerType.contains("checkmate")) {
            return GameResultManager.EndingReason.CHECKMATE;
        } else if (lowerType.contains("resignation") || lowerType.contains("resign")) {
            return GameResultManager.EndingReason.RESIGNATION;
        } else if (lowerType.contains("stalemate")) {
            return GameResultManager.EndingReason.STALEMATE;
        } else if (lowerType.contains("time") || lowerType.contains("timeout")) {
            return GameResultManager.EndingReason.TIME_OUT;
        } else if (lowerType.contains("insufficient")) {
            return GameResultManager.EndingReason.INSUFFICIENT_MATERIAL;
        } else if (lowerType.contains("fifty")) {
            return GameResultManager.EndingReason.FIFTY_MOVE_RULE;
        } else if (lowerType.contains("repetition")) {
            return GameResultManager.EndingReason.THREEFOLD_REPETITION;
        } else if (lowerType.contains("agreement")) {
            return GameResultManager.EndingReason.MUTUAL_AGREEMENT;
        } else {
            return GameResultManager.EndingReason.CHECKMATE; // Default
        }
    }
    
    /**
     * Quick method to add PGN result to existing move history string
     */
    public static String addPgnResultToMoveHistory(String existingHistory, String pgnResult) {
        if (existingHistory == null || existingHistory.isEmpty()) {
            return pgnResult;
        }
        
        // Check if result already exists
        if (existingHistory.contains("1-0") || existingHistory.contains("0-1") || 
            existingHistory.contains("1/2-1/2")) {
            return existingHistory; // Already has result
        }
        
        // Add result to the end
        return existingHistory + " " + pgnResult;
    }
    
    /**
     * Create GameResultManager instance and initialize for new game
     */
    public static GameResultManager createAndInitializeGameResultManager(android.content.Context context) {
        GameResultManager manager = GameResultManager.getInstance(context);
        manager.resetForNewGame();
        
        Log.d(TAG, "🔄 GameResultManager created and initialized for new game");
        return manager;
    }
    
    /**
     * Quick game ending methods for common scenarios
     */
    public static class QuickGameEnding {
        
        public static void userWinsCheckmate(GameResultManager manager, GameResultView view, 
                                           String userColor) {
            boolean isWhite = "white".equals(userColor);
            handleMainActivityGameEnd(manager, view, "User", "checkmate", isWhite, true);
        }
        
        public static void aiWinsCheckmate(GameResultManager manager, GameResultView view, 
                                         String aiName, String userColor) {
            boolean isWhite = !"white".equals(userColor); // AI has opposite color
            handleMainActivityGameEnd(manager, view, aiName, "checkmate", isWhite, false);
        }
        
        public static void drawByStalemate(GameResultManager manager, GameResultView view) {
            manager.endGameStalemate();
            if (view != null) {
                manager.displayGameResult(view);
            }
        }
        
        public static void spectatorCheckmate(GameResultManager manager, GameResultView view,
                                            String winner, String whitePlayer, String blackPlayer) {
            boolean isWhite = winner.equals(whitePlayer);
            handleSpectatorGameEnd(manager, view, winner, "checkmate", isWhite, 
                                 whitePlayer, blackPlayer);
        }
    }
}