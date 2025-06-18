package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.*;

/**
 * Demo class to run tournament simulations and analyze results
 */
public class TournamentSimulationDemo {
    private static final String TAG = "🎪 TournamentDemo";
    
    public static void runFullTournamentDemo(Context context) {
        Log.d(TAG, "🏆 Starting comprehensive tournament simulation demo");
        
        TournamentManager manager = TournamentManager.getInstance(context);
        
        // Show initial leaderboard
        Log.d(TAG, "📊 INITIAL ELO RANKINGS:");
        List<TournamentManager.MasterRanking> initialBoard = manager.getCurrentLeaderboard();
        for (int i = 0; i < Math.min(8, initialBoard.size()); i++) {
            TournamentManager.MasterRanking master = initialBoard.get(i);
            Log.d(TAG, String.format("  %d. %s - %d ELO", 
                master.rank, master.masterName, master.currentElo));
        }
        
        // Show championship state
        TournamentManager.ChampionshipState state = manager.getChampionshipState();
        Log.d(TAG, "👑 Current Champion: " + state.currentChampion);
        Log.d(TAG, "🎯 Current Phase: " + state.currentPhase);
        
        // Run first Candidates Tournament
        Log.d(TAG, "\n🚀 RUNNING FIRST CANDIDATES TOURNAMENT");
        TournamentManager.TournamentResults results = manager.runCandidatesTournament();
        
        Log.d(TAG, "🎊 CANDIDATES TOURNAMENT RESULTS:");
        Log.d(TAG, "Winner: " + results.winner + " (challenges Magnus!)");
        
        // Show final standings
        List<Map.Entry<String, Float>> sortedStandings = 
            new ArrayList<>(results.simulationResults.standings.entrySet());
        sortedStandings.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
        
        for (int i = 0; i < sortedStandings.size(); i++) {
            Map.Entry<String, Float> entry = sortedStandings.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "  ";
            Log.d(TAG, String.format("%s %d. %s: %.1f points", 
                medal, i + 1, entry.getKey(), entry.getValue()));
        }
        
        // Show updated ELO rankings
        Log.d(TAG, "\n📈 UPDATED ELO RANKINGS AFTER TOURNAMENT:");
        List<TournamentManager.MasterRanking> updatedBoard = manager.getCurrentLeaderboard();
        for (TournamentManager.MasterRanking master : updatedBoard) {
            int eloChange = master.currentElo - getInitialElo(master.masterName);
            String changeStr = eloChange > 0 ? "+" + eloChange : String.valueOf(eloChange);
            Log.d(TAG, String.format("  %d. %s - %d ELO (%s)", 
                master.rank, master.masterName, master.currentElo, changeStr));
        }
        
        Log.d(TAG, "✅ Tournament demo complete!");
    }
    
    public static void runMultiSeasonSimulation(Context context, int seasons) {
        Log.d(TAG, "⚡ Running " + seasons + " season simulation");
        
        TournamentManager manager = TournamentManager.getInstance(context);
        
        // Record initial state
        Map<String, Integer> initialElos = new HashMap<>();
        List<TournamentManager.MasterRanking> initial = manager.getCurrentLeaderboard();
        for (TournamentManager.MasterRanking master : initial) {
            initialElos.put(master.masterName, master.currentElo);
        }
        
        // Run multiple seasons
        long startTime = System.currentTimeMillis();
        manager.simulateMultipleSeasons(seasons);
        long duration = System.currentTimeMillis() - startTime;
        
        Log.d(TAG, String.format("⚡ Completed %d seasons in %dms", seasons, duration));
        
        // Analyze results
        analyzeSimulationResults(manager, initialElos, seasons);
    }
    
    private static void analyzeSimulationResults(TournamentManager manager, 
                                               Map<String, Integer> initialElos, 
                                               int seasons) {
        Log.d(TAG, "\n📊 MULTI-SEASON ANALYSIS RESULTS:");
        
        // Current champion
        TournamentManager.ChampionshipState state = manager.getChampionshipState();
        Log.d(TAG, "👑 Final Champion: " + state.currentChampion);
        
        // Final leaderboard with changes
        Log.d(TAG, "\n🏆 FINAL LEADERBOARD AFTER " + seasons + " SEASONS:");
        List<TournamentManager.MasterRanking> finalBoard = manager.getCurrentLeaderboard();
        
        for (TournamentManager.MasterRanking master : finalBoard) {
            int initialElo = initialElos.get(master.masterName);
            int eloChange = master.currentElo - initialElo;
            String changeStr = eloChange >= 0 ? "+" + eloChange : String.valueOf(eloChange);
            
            String trophies = "";
            if (master.championshipWins > 0) trophies += "👑" + master.championshipWins + " ";
            if (master.tournamentWins > 0) trophies += "🏆" + master.tournamentWins + " ";
            
            Log.d(TAG, String.format("  %d. %s - %d ELO (%s) %s", 
                master.rank, master.masterName, master.currentElo, changeStr, trophies));
        }
        
        // Tournament winners analysis
        Log.d(TAG, "\n🎖️ TOURNAMENT PERFORMANCE ANALYSIS:");
        for (TournamentManager.MasterRanking master : finalBoard) {
            if (master.tournamentWins > 0 || master.championshipWins > 0) {
                float winRate = master.gamesPlayed > 0 ? 
                    (master.totalScore / master.gamesPlayed) * 100 : 0;
                Log.d(TAG, String.format("  %s: %d tournament wins, %d championship wins, %.1f%% score", 
                    master.masterName, master.tournamentWins, master.championshipWins, winRate));
            }
        }
        
        // Biggest movers
        Log.d(TAG, "\n📈 BIGGEST ELO MOVERS:");
        List<TournamentManager.MasterRanking> movers = new ArrayList<>(finalBoard);
        movers.sort((a, b) -> {
            int aChange = a.currentElo - initialElos.get(a.masterName);
            int bChange = b.currentElo - initialElos.get(b.masterName);
            return Integer.compare(Math.abs(bChange), Math.abs(aChange));
        });
        
        for (int i = 0; i < Math.min(5, movers.size()); i++) {
            TournamentManager.MasterRanking master = movers.get(i);
            int change = master.currentElo - initialElos.get(master.masterName);
            String emoji = change > 0 ? "📈" : "📉";
            Log.d(TAG, String.format("  %s %s: %+d ELO change", 
                emoji, master.masterName, change));
        }
    }
    
    public static void testPsychologicalFactors(Context context) {
        Log.d(TAG, "🧠 Testing psychological factors in tournament play");
        
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        
        // Test pressure effects on different masters
        Log.d(TAG, "\n🎯 PRESSURE TEST - Early vs Late Tournament:");
        
        String[] testMasters = {"Bobby Fischer", "Anatoly Karpov", "Magnus Carlsen", "Mikhail Tal"};
        
        for (String master : testMasters) {
            Log.d(TAG, "\n" + master + " performance analysis:");
            
            // Test early tournament games (low pressure)
            int earlyWins = 0;
            for (int i = 0; i < 10; i++) {
                TournamentSimulationEngine.GameResult result = 
                    engine.simulateGame(master, "José Raúl Capablanca", 1);
                if ((result.whiteMaster.equals(master) && result.score == 1.0f) ||
                    (result.blackMaster.equals(master) && result.score == 0.0f)) {
                    earlyWins++;
                }
            }
            
            // Test late tournament games (high pressure)
            engine.resetTournament();
            int lateWins = 0;
            for (int i = 0; i < 10; i++) {
                TournamentSimulationEngine.GameResult result = 
                    engine.simulateGame(master, "José Raúl Capablanca", 14);
                if ((result.whiteMaster.equals(master) && result.score == 1.0f) ||
                    (result.blackMaster.equals(master) && result.score == 0.0f)) {
                    lateWins++;
                }
            }
            
            Log.d(TAG, String.format("  Early rounds: %d/10 wins (%.0f%%)", 
                earlyWins, earlyWins * 10.0f));
            Log.d(TAG, String.format("  Late rounds: %d/10 wins (%.0f%%)", 
                lateWins, lateWins * 10.0f));
            Log.d(TAG, String.format("  Pressure impact: %+.0f%% performance change", 
                (lateWins - earlyWins) * 10.0f));
        }
    }
    
    // Helper method to get initial ELO (would be from your master profiles)
    private static int getInitialElo(String masterName) {
        switch (masterName) {
            case "Magnus Carlsen": return 2882;
            case "Garry Kasparov": return 2851;
            case "Vladimir Kramnik": return 2817;
            case "Viswanathan Anand": return 2817;
            case "Hikaru Nakamura": return 2800;
            case "Bobby Fischer": return 2785;
            case "Anatoly Karpov": return 2780;
            case "Tigran Petrosian": return 2745;
            case "Aron Nimzowitsch": return 2720;
            case "José Raúl Capablanca": return 2720;
            case "Mikhail Botvinnik": return 2720;
            case "Mikhail Tal": return 2705;
            case "Alexander Alekhine": return 2690;
            case "Emanuel Lasker": return 2660;
            case "Paul Morphy": return 2650;
            default: return 2700;
        }
    }
}