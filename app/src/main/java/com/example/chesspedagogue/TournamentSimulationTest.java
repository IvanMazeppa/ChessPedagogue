package com.example.chesspedagogue;

import android.util.Log;
import java.util.Arrays;
import java.util.List;

/**
 * Test class to demonstrate tournament simulation engine
 * This shows how the psychological chess simulation works in practice
 */
public class TournamentSimulationTest {
    private static final String TAG = "🧪 TournamentTest";
    
    public static void runSimulationDemo() {
        Log.d(TAG, "🏆 Starting Tournament Simulation Demo");
        
        // Select 8 masters for Candidates Tournament
        List<String> candidates = Arrays.asList(
            "Garry Kasparov",
            "Bobby Fischer", 
            "Mikhail Tal",
            "Anatoly Karpov",
            "Alexander Alekhine",
            "José Raúl Capablanca",
            "Vladimir Kramnik",
            "Magnus Carlsen"  // Will be excluded as current champion
        );
        
        // Remove Magnus as current champion
        candidates = candidates.subList(0, 7);
        candidates.add("Viswanathan Anand"); // Add another legend
        
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        
        // Test individual game simulation
        Log.d(TAG, "🎯 Testing individual game simulation:");
        TournamentSimulationEngine.GameResult testGame = 
            engine.simulateGame("Bobby Fischer", "Mikhail Tal", 1);
        Log.d(TAG, "Sample game: " + testGame.description);
        
        // Test full tournament simulation
        Log.d(TAG, "🏆 Running full Candidates Tournament simulation:");
        TournamentSimulationEngine.TournamentResults results = 
            engine.simulateFullTournament(candidates);
        
        // Display final results
        Log.d(TAG, "🎊 FINAL TOURNAMENT RESULTS:");
        Log.d(TAG, "Winner: " + results.winner + " (challenges Magnus Carlsen!)");
        
        // Show top 3
        List<java.util.Map.Entry<String, Float>> sortedResults = 
            new java.util.ArrayList<>(results.standings.entrySet());
        sortedResults.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
        
        Log.d(TAG, "Final Standings:");
        for (int i = 0; i < Math.min(3, sortedResults.size()); i++) {
            java.util.Map.Entry<String, Float> entry = sortedResults.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : "🥉";
            Log.d(TAG, String.format("  %s %d. %s: %.1f points", 
                medal, i + 1, entry.getKey(), entry.getValue()));
        }
        
        // Show some interesting games
        Log.d(TAG, "🎭 Notable Game Results:");
        for (TournamentSimulationEngine.GameResult game : results.allResults) {
            if ((game.whiteMaster.equals("Bobby Fischer") || game.blackMaster.equals("Bobby Fischer")) &&
                (game.whiteMaster.equals("Mikhail Tal") || game.blackMaster.equals("Mikhail Tal"))) {
                Log.d(TAG, "  🔥 " + game.whiteMaster + " vs " + game.blackMaster + " = " + game.description);
            }
        }
        
        // Show emotional states
        Log.d(TAG, "🎭 Final Emotional States:");
        java.util.Map<String, String> emotions = engine.getCurrentEmotionalStates();
        for (java.util.Map.Entry<String, String> entry : emotions.entrySet()) {
            Log.d(TAG, "  " + entry.getKey() + ": " + entry.getValue());
        }
        
        Log.d(TAG, "✅ Tournament simulation demo complete!");
    }
    
    /**
     * Quick test for database population
     */
    public static void runQuickSimulationTest() {
        Log.d(TAG, "⚡ Running quick simulation for database population");
        
        List<String> masters = Arrays.asList(
            "Garry Kasparov", "Bobby Fischer", "Mikhail Tal", 
            "Anatoly Karpov", "Alexander Alekhine", "José Raúl Capablanca",
            "Vladimir Kramnik", "Viswanathan Anand"
        );
        
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        engine.resetTournament();
        
        long startTime = System.currentTimeMillis();
        TournamentSimulationEngine.TournamentResults results = 
            engine.simulateQuickTournament(masters);
        long duration = System.currentTimeMillis() - startTime;
        
        Log.d(TAG, String.format("⚡ Quick tournament completed in %dms", duration));
        Log.d(TAG, "Winner: " + results.winner);
        Log.d(TAG, "Total games: " + results.allResults.size());
        
        // This data could now be saved to database for tournament history
        // results.saveToDatabase();
    }
    
    /**
     * Test psychological factors in action
     */
    public static void testPsychologicalFactors() {
        Log.d(TAG, "🧠 Testing psychological factors");
        
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        engine.resetTournament();
        
        // Test the "Karpov Effect" - pressure affecting performance
        Log.d(TAG, "Testing Karpov under pressure:");
        for (int round = 1; round <= 14; round += 3) {
            TournamentSimulationEngine.GameResult game = 
                engine.simulateGame("Anatoly Karpov", "Garry Kasparov", round);
            Log.d(TAG, String.format("  Round %d: %s", round, game.description));
        }
        
        // Test Fischer's volatility
        Log.d(TAG, "Testing Fischer's emotional volatility:");
        for (int i = 0; i < 3; i++) {
            TournamentSimulationEngine.GameResult game = 
                engine.simulateGame("Bobby Fischer", "José Raúl Capablanca", 1);
            Log.d(TAG, "  " + game.description);
        }
    }
}