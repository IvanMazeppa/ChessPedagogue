package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Integration class to test tournament system from existing activities
 * Can be called from MainActivity or SpectatorGameActivity
 */
public class TournamentTestIntegration {
    private static final String TAG = "🧪 TournamentTest";
    
    private ExecutorService executorService;
    private Handler mainHandler;
    
    public TournamentTestIntegration() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Quick test method that can be called from existing activities
     * Add this call to MainActivity onCreate() or a button click
     */
    public void runQuickTournamentTest(Context context) {
        Log.d(TAG, "🚀 Starting quick tournament test");
        
        executorService.execute(() -> {
            try {
                // Test basic tournament functionality
                TournamentManager manager = TournamentManager.getInstance(context);
                
                // Show initial leaderboard
                Log.d(TAG, "📊 INITIAL LEADERBOARD (Top 10):");
                var leaderboard = manager.getCurrentLeaderboard();
                for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
                    var master = leaderboard.get(i);
                    Log.d(TAG, String.format("  %d. %s - %d ELO", 
                        i + 1, master.masterName, master.currentElo));
                }
                
                // Test individual game simulation
                Log.d(TAG, "\n🎯 INDIVIDUAL GAME TESTS:");
                TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
                
                // Classic rivalry: Fischer vs Tal
                var game1 = engine.simulateGame("Bobby Fischer", "Mikhail Tal", 1);
                Log.d(TAG, "Fischer vs Tal: " + game1.description);
                
                // Modern vs Classic: Hikaru vs Capablanca  
                var game2 = engine.simulateGame("Hikaru Nakamura", "José Raúl Capablanca", 1);
                Log.d(TAG, "Hikaru vs Capablanca: " + game2.description);
                
                // Defensive battle: Petrosian vs Karpov
                var game3 = engine.simulateGame("Tigran Petrosian", "Anatoly Karpov", 1);
                Log.d(TAG, "Petrosian vs Karpov: " + game3.description);
                
                // Run one candidates tournament
                Log.d(TAG, "\n🏆 RUNNING ONE CANDIDATES TOURNAMENT:");
                var results = manager.runCandidatesTournament();
                
                Log.d(TAG, "🎊 Tournament Winner: " + results.winner);
                Log.d(TAG, "📝 Full standings:");
                
                var standings = results.simulationResults.standings.entrySet()
                    .stream()
                    .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                    .toList();
                
                for (int i = 0; i < standings.size(); i++) {
                    var entry = standings.get(i);
                    String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "  ";
                    Log.d(TAG, String.format("%s %d. %s: %.1f/14 points", 
                        medal, i + 1, entry.getKey(), entry.getValue()));
                }
                
                // Show updated ratings
                Log.d(TAG, "\n📈 UPDATED ELO RATINGS:");
                var newLeaderboard = manager.getCurrentLeaderboard();
                for (int i = 0; i < Math.min(8, newLeaderboard.size()); i++) {
                    var master = newLeaderboard.get(i);
                    Log.d(TAG, String.format("  %d. %s - %d ELO", 
                        i + 1, master.masterName, master.currentElo));
                }
                
                Log.d(TAG, "✅ Tournament test complete!");
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Tournament test failed", e);
            }
        });
    }
    
    /**
     * Full multi-season simulation for database population
     */
    public void runMultiSeasonSimulation(Context context, int seasons, 
                                       TournamentResultCallback callback) {
        Log.d(TAG, "⚡ Starting " + seasons + " season simulation");
        
        executorService.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                TournamentManager manager = TournamentManager.getInstance(context);
                
                // Record initial state
                var initialLeaderboard = manager.getCurrentLeaderboard();
                String initialChampion = manager.getChampionshipState().currentChampion;
                
                // Run simulation
                manager.simulateMultipleSeasons(seasons);
                
                // Analyze results
                var finalLeaderboard = manager.getCurrentLeaderboard();
                String finalChampion = manager.getChampionshipState().currentChampion;
                
                long duration = System.currentTimeMillis() - startTime;
                
                // Prepare results
                SimulationResults results = new SimulationResults();
                results.seasonsSimulated = seasons;
                results.durationMs = duration;
                results.initialChampion = initialChampion;
                results.finalChampion = finalChampion;
                results.championChanged = !initialChampion.equals(finalChampion);
                results.finalLeaderboard = finalLeaderboard;
                
                // Calculate biggest movers
                for (var finalMaster : finalLeaderboard) {
                    for (var initialMaster : initialLeaderboard) {
                        if (finalMaster.masterName.equals(initialMaster.masterName)) {
                            int eloChange = finalMaster.currentElo - initialMaster.currentElo;
                            results.eloChanges.put(finalMaster.masterName, eloChange);
                            break;
                        }
                    }
                }
                
                // Callback on main thread
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSimulationComplete(results);
                    }
                });
                
                Log.d(TAG, String.format("✅ %d seasons simulated in %.2fs", 
                    seasons, duration / 1000.0f));
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Multi-season simulation failed", e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSimulationError(e);
                    }
                });
            }
        });
    }
    
    /**
     * Test psychological factors specifically
     */
    public void testPsychologicalRealism(Context context) {
        Log.d(TAG, "🧠 Testing psychological realism");
        
        executorService.execute(() -> {
            TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
            
            // Test Karpov's pressure sensitivity
            Log.d(TAG, "\n🎯 KARPOV PRESSURE TEST:");
            testMasterUnderPressure("Anatoly Karpov", engine);
            
            // Test Fischer's volatility
            Log.d(TAG, "\n🎯 FISCHER VOLATILITY TEST:");
            testMasterVolatility("Bobby Fischer", engine);
            
            // Test Petrosian's stability
            Log.d(TAG, "\n🎯 PETROSIAN STABILITY TEST:");
            testMasterStability("Tigran Petrosian", engine);
            
            // Test Tal's excitement factor
            Log.d(TAG, "\n🎯 TAL EXCITEMENT TEST:");
            testMasterExcitement("Mikhail Tal", engine);
            
            Log.d(TAG, "🧠 Psychological testing complete!");
        });
    }
    
    private void testMasterUnderPressure(String masterName, TournamentSimulationEngine engine) {
        int earlyWins = 0, lateWins = 0;
        
        // Early tournament performance
        for (int i = 0; i < 20; i++) {
            var result = engine.simulateGame(masterName, "Magnus Carlsen", 1);
            if ((result.whiteMaster.equals(masterName) && result.score == 1.0f) ||
                (result.blackMaster.equals(masterName) && result.score == 0.0f)) {
                earlyWins++;
            }
        }
        
        engine.resetTournament();
        
        // Late tournament performance (high pressure)
        for (int i = 0; i < 20; i++) {
            var result = engine.simulateGame(masterName, "Magnus Carlsen", 14);
            if ((result.whiteMaster.equals(masterName) && result.score == 1.0f) ||
                (result.blackMaster.equals(masterName) && result.score == 0.0f)) {
                lateWins++;
            }
        }
        
        Log.d(TAG, String.format("  %s: Early %.0f%% vs Late %.0f%% vs Magnus", 
            masterName, earlyWins * 5.0f, lateWins * 5.0f));
        Log.d(TAG, String.format("  Pressure impact: %+.0f%% performance change", 
            (lateWins - earlyWins) * 5.0f));
    }
    
    private void testMasterVolatility(String masterName, TournamentSimulationEngine engine) {
        int[] results = new int[5]; // win, draw, loss counts across multiple tests
        
        for (int test = 0; test < 3; test++) {
            engine.resetTournament();
            int wins = 0, draws = 0, losses = 0;
            
            for (int i = 0; i < 10; i++) {
                var result = engine.simulateGame(masterName, "José Raúl Capablanca", 7);
                if (result.score == 1.0f && result.whiteMaster.equals(masterName)) wins++;
                else if (result.score == 0.0f && result.blackMaster.equals(masterName)) wins++;
                else if (result.score == 0.5f) draws++;
                else losses++;
            }
            
            Log.d(TAG, String.format("  %s Test %d: %d-%d-%d vs Capablanca", 
                masterName, test + 1, wins, draws, losses));
        }
    }
    
    private void testMasterStability(String masterName, TournamentSimulationEngine engine) {
        // Test consistency across multiple rounds
        int totalVariance = 0;
        int[] roundPerformance = new int[5];
        
        for (int round = 1; round <= 5; round++) {
            int wins = 0;
            for (int i = 0; i < 10; i++) {
                var result = engine.simulateGame(masterName, "Alexander Alekhine", round * 3);
                if ((result.whiteMaster.equals(masterName) && result.score >= 0.5f) ||
                    (result.blackMaster.equals(masterName) && result.score <= 0.5f)) {
                    wins++;
                }
            }
            roundPerformance[round - 1] = wins;
        }
        
        // Calculate variance
        float average = 0;
        for (int perf : roundPerformance) average += perf;
        average /= roundPerformance.length;
        
        float variance = 0;
        for (int perf : roundPerformance) {
            variance += Math.pow(perf - average, 2);
        }
        variance /= roundPerformance.length;
        
        Log.d(TAG, String.format("  %s consistency: %.1f average, %.1f variance", 
            masterName, average, variance));
    }
    
    private void testMasterExcitement(String masterName, TournamentSimulationEngine engine) {
        // Test if Tal gets more exciting results in tactical positions
        int dramaticGames = 0;
        
        for (int i = 0; i < 20; i++) {
            var result = engine.simulateGame(masterName, "Vladimir Kramnik", 5);
            // Consider non-draws as more "dramatic" for Tal
            if (result.score != 0.5f) {
                dramaticGames++;
            }
        }
        
        Log.d(TAG, String.format("  %s drama factor: %d/20 decisive games (%.0f%%)", 
            masterName, dramaticGames, dramaticGames * 5.0f));
    }
    
    // Result classes
    public static class SimulationResults {
        public int seasonsSimulated;
        public long durationMs;
        public String initialChampion;
        public String finalChampion;
        public boolean championChanged;
        public java.util.List<TournamentManager.MasterRanking> finalLeaderboard;
        public java.util.Map<String, Integer> eloChanges = new java.util.HashMap<>();
    }
    
    public interface TournamentResultCallback {
        void onSimulationComplete(SimulationResults results);
        void onSimulationError(Exception error);
    }
    
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}