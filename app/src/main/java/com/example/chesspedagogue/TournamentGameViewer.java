package com.example.chesspedagogue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

/**
 * Tournament Game Viewer - Shows best games from tournaments in spectator mode
 * Allows users to skip individual games and see master dialogue
 */
public class TournamentGameViewer {
    private static final String TAG = "🎬 TournamentViewer";
    
    private Context context;
    private Handler uiHandler;
    private boolean isViewingGames = false;
    private int currentGameIndex = 0;
    private List<TournamentSimulationEngine.GameResult> gamesToView;
    private String tournamentType;
    
    // Viewer callback interface
    public interface GameViewerCallback {
        void onGameStarted(TournamentSimulationEngine.GameResult game, int gameNumber, int totalGames);
        void onGameSkipped(TournamentSimulationEngine.GameResult game, int gameNumber);
        void onGameCompleted(TournamentSimulationEngine.GameResult game, int gameNumber);
        void onAllGamesViewed(String tournamentType, int gamesViewed, int gamesSkipped);
        void onViewingCancelled(String tournamentType, int gamesViewed);
    }
    
    // Game viewing statistics
    public static class ViewingStats {
        public int totalGames;
        public int gamesViewed;
        public int gamesSkipped;
        public long totalViewTime;
        public String tournamentType;
        
        public ViewingStats(String type, int total) {
            this.tournamentType = type;
            this.totalGames = total;
            this.gamesViewed = 0;
            this.gamesSkipped = 0;
            this.totalViewTime = 0;
        }
    }
    
    public TournamentGameViewer(Context context) {
        this.context = context;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Starts viewing the best games from a tournament
     */
    public void viewBestGames(List<TournamentSimulationEngine.GameResult> bestGames, 
                             String tournamentType, GameViewerCallback callback) {
        
        if (bestGames == null || bestGames.isEmpty()) {
            Log.w(TAG, "❌ No games to view for " + tournamentType);
            return;
        }
        
        this.gamesToView = new ArrayList<>(bestGames);
        this.tournamentType = tournamentType;
        this.currentGameIndex = 0;
        this.isViewingGames = true;
        
        Log.d(TAG, String.format("🎬 Starting game viewer for %s tournament: %d best games", 
               tournamentType, bestGames.size()));
        
        // Show viewer introduction dialog
        showViewerIntroDialog(callback);
    }
    
    /**
     * Shows introduction dialog with viewing options
     */
    private void showViewerIntroDialog(GameViewerCallback callback) {
        if (!(context instanceof AppCompatActivity)) {
            Log.e(TAG, "❌ Context must be AppCompatActivity for viewer");
            return;
        }
        
        AppCompatActivity activity = (AppCompatActivity) context;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("🎬 " + tournamentType + " Tournament Highlights");
        builder.setMessage(String.format(
            "Watch the %d best games from the %s tournament!\\n\\n" +
            "Features:\\n" +
            "🎭 Master dialogue during games\\n" +
            "⏭️ Skip individual games\\n" +
            "🎯 Real-time spectator format\\n" +
            "📊 Game quality ratings\\n\\n" +
            "Each game will be shown in spectator mode with dialogue between the masters.",
            gamesToView.size(), tournamentType));
        
        builder.setPositiveButton("🎬 Start Viewing", (dialog, which) -> {
            startGameViewing(callback);
        });
        
        builder.setNegativeButton("❌ Cancel", (dialog, which) -> {
            callback.onViewingCancelled(tournamentType, 0);
            isViewingGames = false;
        });
        
        builder.setNeutralButton("📋 Game List", (dialog, which) -> {
            showGameListDialog(callback);
        });
        
        builder.show();
    }
    
    /**
     * Shows list of games with quality ratings
     */
    private void showGameListDialog(GameViewerCallback callback) {
        AppCompatActivity activity = (AppCompatActivity) context;
        
        StringBuilder gameList = new StringBuilder();
        gameList.append(String.format("%s Tournament - Best %d Games:\\n\\n", 
                       tournamentType, gamesToView.size()));
        
        for (int i = 0; i < gamesToView.size(); i++) {
            TournamentSimulationEngine.GameResult game = gamesToView.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "⭐";
            
            gameList.append(String.format("%s Game %d: %s vs %s\\n", 
                           medal, i + 1, game.whiteMaster, game.blackMaster));
            gameList.append(String.format("   Round %d • Quality: %.2f • %s\\n\\n", 
                           game.round, game.gameQuality, game.description));
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("📋 " + tournamentType + " Best Games");
        builder.setMessage(gameList.toString());
        
        builder.setPositiveButton("🎬 Start Viewing", (dialog, which) -> {
            startGameViewing(callback);
        });
        
        builder.setNegativeButton("❌ Cancel", (dialog, which) -> {
            callback.onViewingCancelled(tournamentType, 0);
            isViewingGames = false;
        });
        
        builder.show();
    }
    
    /**
     * Starts the actual game viewing process
     */
    private void startGameViewing(GameViewerCallback callback) {
        Log.d(TAG, String.format("🎬 Beginning game viewing session: %d games", gamesToView.size()));
        currentGameIndex = 0;
        showNextGame(callback);
    }
    
    /**
     * Shows the current game with skip/continue options
     */
    private void showNextGame(GameViewerCallback callback) {
        if (!isViewingGames || currentGameIndex >= gamesToView.size()) {
            // All games viewed
            ViewingStats stats = new ViewingStats(tournamentType, gamesToView.size());
            callback.onAllGamesViewed(tournamentType, currentGameIndex, 0);
            isViewingGames = false;
            return;
        }
        
        TournamentSimulationEngine.GameResult currentGame = gamesToView.get(currentGameIndex);
        
        Log.d(TAG, String.format("🎬 Showing game %d/%d: %s vs %s", 
               currentGameIndex + 1, gamesToView.size(), 
               currentGame.whiteMaster, currentGame.blackMaster));
        
        // Notify callback that game is starting
        callback.onGameStarted(currentGame, currentGameIndex + 1, gamesToView.size());
        
        // Show game dialog with skip option
        showGameDialog(currentGame, callback);
    }
    
    /**
     * Shows individual game dialog with spectator options
     */
    private void showGameDialog(TournamentSimulationEngine.GameResult game, GameViewerCallback callback) {
        AppCompatActivity activity = (AppCompatActivity) context;
        
        String medal = currentGameIndex == 0 ? "🥇" : currentGameIndex == 1 ? "🥈" : currentGameIndex == 2 ? "🥉" : "⭐";
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(String.format("%s Game %d of %d", medal, currentGameIndex + 1, gamesToView.size()));
        builder.setMessage(String.format(
            "🎭 %s vs %s\\n" +
            "Round: %d • Quality: %.2f\\n" +
            "Result: %s\\n\\n" +
            "🎬 This game will be shown in spectator mode with master dialogue.\\n" +
            "The masters will discuss their moves and strategies as they play.\\n\\n" +
            "⚠️ Note: Tournament dialogue is for entertainment and EQ system demonstration.",
            game.whiteMaster, game.blackMaster, game.round, game.gameQuality, game.description));
        
        builder.setPositiveButton("🎬 Watch Game", (dialog, which) -> {
            // Launch spectator mode for this game
            launchSpectatorMode(game, callback);
        });
        
        builder.setNeutralButton("⏭️ Skip Game", (dialog, which) -> {
            // Skip this game
            Log.d(TAG, String.format("⏭️ User skipped game %d: %s vs %s", 
                   currentGameIndex + 1, game.whiteMaster, game.blackMaster));
            
            callback.onGameSkipped(game, currentGameIndex + 1);
            currentGameIndex++;
            
            // Move to next game
            uiHandler.postDelayed(() -> showNextGame(callback), 500);
        });
        
        builder.setNegativeButton("❌ Stop Viewing", (dialog, which) -> {
            callback.onViewingCancelled(tournamentType, currentGameIndex);
            isViewingGames = false;
        });
        
        builder.show();
    }
    
    /**
     * Launches spectator mode for the selected game
     */
    private void launchSpectatorMode(TournamentSimulationEngine.GameResult game, GameViewerCallback callback) {
        Log.d(TAG, String.format("🎯 Launching spectator mode: %s vs %s", 
               game.whiteMaster, game.blackMaster));
        
        try {
            // Launch actual SpectatorGameActivity with the specific masters
            Intent spectatorIntent = new Intent(context, SpectatorGameActivity.class);
            
            // Pass the masters as extras
            spectatorIntent.putExtra("MASTER_1", game.whiteMaster);
            spectatorIntent.putExtra("MASTER_2", game.blackMaster);
            spectatorIntent.putExtra("TOURNAMENT_MODE", true);
            spectatorIntent.putExtra("GAME_ROUND", game.round);
            spectatorIntent.putExtra("GAME_TYPE", game.gameType);
            spectatorIntent.putExtra("GAME_RESULT", game.description);
            spectatorIntent.putExtra("GAME_QUALITY", game.gameQuality);
            
            // Start the activity
            context.startActivity(spectatorIntent);
            
            // Complete the current game viewing
            callback.onGameCompleted(game, currentGameIndex + 1);
            currentGameIndex++;
            
            // Continue to next game after a delay (user will return from spectator mode)
            uiHandler.postDelayed(() -> showNextGame(callback), 1000);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to launch spectator mode", e);
            // Fallback to simulation if launch fails
            simulateSpectatorMode(game, callback);
        }
    }
    
    /**
     * Simulates spectator mode experience (placeholder for actual implementation)
     */
    private void simulateSpectatorMode(TournamentSimulationEngine.GameResult game, GameViewerCallback callback) {
        Log.d(TAG, String.format("🎭 Simulating spectator mode for %s vs %s", 
               game.whiteMaster, game.blackMaster));
        
        // Show spectator mode dialog
        AppCompatActivity activity = (AppCompatActivity) context;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("🎭 Spectator Mode");
        builder.setMessage(String.format(
            "Now showing: %s vs %s\\n\\n" +
            "🎬 Spectator Mode Features:\\n" +
            "• Real-time game replay\\n" +
            "• Master dialogue and commentary\\n" +
            "• Emotional reactions and conversations\\n" +
            "• Strategic discussions between moves\\n\\n" +
            "Result: %s\\n" +
            "Quality: %.2f/1.0\\n\\n" +
            "🚀 Integration Point: This would launch SpectatorGameActivity\\n" +
            "with tournament dialogue enabled for entertainment value.",
            game.whiteMaster, game.blackMaster, game.description, game.gameQuality));
        
        builder.setPositiveButton("✅ Game Complete", (dialog, which) -> {
            // Game viewing completed
            callback.onGameCompleted(game, currentGameIndex + 1);
            currentGameIndex++;
            
            // Continue to next game
            uiHandler.postDelayed(() -> showNextGame(callback), 1000);
        });
        
        builder.setNegativeButton("⏭️ Skip Rest", (dialog, which) -> {
            // Skip this and remaining games
            callback.onViewingCancelled(tournamentType, currentGameIndex);
            isViewingGames = false;
        });
        
        builder.show();
    }
    
    /**
     * Creates a simple callback implementation for testing
     */
    public static GameViewerCallback createSimpleCallback() {
        return new GameViewerCallback() {
            @Override
            public void onGameStarted(TournamentSimulationEngine.GameResult game, int gameNumber, int totalGames) {
                Log.d(TAG, String.format("🎬 Started viewing game %d/%d: %s vs %s", 
                       gameNumber, totalGames, game.whiteMaster, game.blackMaster));
            }
            
            @Override
            public void onGameSkipped(TournamentSimulationEngine.GameResult game, int gameNumber) {
                Log.d(TAG, String.format("⏭️ Skipped game %d: %s vs %s", 
                       gameNumber, game.whiteMaster, game.blackMaster));
            }
            
            @Override
            public void onGameCompleted(TournamentSimulationEngine.GameResult game, int gameNumber) {
                Log.d(TAG, String.format("✅ Completed viewing game %d: %s vs %s", 
                       gameNumber, game.whiteMaster, game.blackMaster));
            }
            
            @Override
            public void onAllGamesViewed(String tournamentType, int gamesViewed, int gamesSkipped) {
                Log.d(TAG, String.format("🎊 All games viewed! %s tournament: %d viewed, %d skipped", 
                       tournamentType, gamesViewed, gamesSkipped));
            }
            
            @Override
            public void onViewingCancelled(String tournamentType, int gamesViewed) {
                Log.d(TAG, String.format("❌ Viewing cancelled for %s tournament after %d games", 
                       tournamentType, gamesViewed));
            }
        };
    }
    
    /**
     * Integration method to be called from WorkingMastersTournamentDemo
     */
    public static void viewTournamentHighlights(Context context, 
                                              List<TournamentSimulationEngine.GameResult> bestGames,
                                              String tournamentType) {
        
        Log.d(TAG, String.format("🎬 Creating tournament viewer for %s with %d games", 
               tournamentType, bestGames.size()));
        
        TournamentGameViewer viewer = new TournamentGameViewer(context);
        GameViewerCallback callback = createSimpleCallback();
        
        viewer.viewBestGames(bestGames, tournamentType, callback);
    }
    
    /**
     * Stops current viewing session
     */
    public void stopViewing() {
        isViewingGames = false;
        Log.d(TAG, "🛑 Tournament game viewing stopped");
    }
    
    /**
     * Returns true if currently viewing games
     */
    public boolean isViewing() {
        return isViewingGames;
    }
}