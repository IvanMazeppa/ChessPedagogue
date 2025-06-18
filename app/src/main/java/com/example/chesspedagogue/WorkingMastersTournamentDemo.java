package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

/**
 * Tournament simulation demo using only the currently working chess masters
 * Perfect for testing the system with your existing functional configurations
 */
public class WorkingMastersTournamentDemo {
    private static final String TAG = "🏆 WorkingMastersDemo";
    
    // All configured masters (including those without fine-tuned models)
    private static final List<String> ALL_CONFIGURED_MASTERS = Arrays.asList(
        "Magnus Carlsen",        // ✅ Full setup: Assistant + Vector Store + Fine-tuned
        "Bobby Fischer",         // ✅ Full setup: Assistant + Vector Store + Fine-tuned
        "Mikhail Tal",           // ✅ Full setup: Assistant + Vector Store + Fine-tuned
        "Alexander Alekhine",    // ✅ Full setup: Assistant + Vector Store + Fine-tuned
        "José Raúl Capablanca",  // ✅ Full setup: Assistant + Vector Store + Fine-tuned
        "Garry Kasparov",        // ✅ Assistant + Fine-tuned (vector store needs fix)
        "Viswanathan Anand",     // ✅ Assistant (needs vector store + fine-tuned model)
        "Gukesh Dommaraju",      // ✅ Assistant created! (needs vector store)
        "Hikaru Nakamura",       // ✅ Assistant created! (needs vector store)
        "Tigran Petrosian",      // ✅ Assistant created! (needs vector store)
        "Aron Nimzowitsch",      // ✅ Assistant created! (needs vector store)
        "Vladimir Kramnik",      // 🔧 Configured master (no assistant yet)
        "Anatoly Karpov",        // 🔧 Configured master (no assistant yet)
        "Paul Morphy",           // 🔧 Configured master (no assistant yet)
        "Emanuel Lasker",        // 🔧 Configured master (no assistant yet)
        "Mikhail Botvinnik"      // 🔧 Configured master (no assistant yet)
    );
    
    // Core working masters with fine-tuned models (for priority testing)
    private static final List<String> FINE_TUNED_MASTERS = Arrays.asList(
        "Magnus Carlsen", "Bobby Fischer", "Mikhail Tal", 
        "Alexander Alekhine", "José Raúl Capablanca", "Garry Kasparov"
    );
    
    // Masters with newly created assistants (ready for system instruction testing)
    private static final List<String> NEW_ASSISTANT_MASTERS = Arrays.asList(
        "Gukesh Dommaraju", "Hikaru Nakamura", "Tigran Petrosian", "Aron Nimzowitsch"
    );
    
    /**
     * Run full tournament with all configured masters (expanded roster)
     * Magnus excluded as current champion - others compete for challenger spot
     */
    public static void runExpandedTournament(Context context) {
        Log.d(TAG, "🚀 Starting EXPANDED Candidates Tournament with all configured masters");
        
        // Exclude Magnus as current champion - use all other configured masters
        List<String> candidates = new ArrayList<>(ALL_CONFIGURED_MASTERS);
        candidates.remove("Magnus Carlsen");
        
        Log.d(TAG, "👑 Current Champion: Magnus Carlsen (excluded from Candidates)");
        Log.d(TAG, "🎯 Expanded Candidates (" + candidates.size() + " masters):");
        for (int i = 0; i < candidates.size(); i++) {
            String master = candidates.get(i);
            boolean hasFineTuned = FINE_TUNED_MASTERS.contains(master);
            String status = hasFineTuned ? "✅ Fine-tuned" : "🔧 Base model";
            Log.d(TAG, String.format("  %d. %s (%s)", i + 1, master, status));
        }
        
        runTournamentWithMasters(context, candidates, "EXPANDED");
    }
    
    /**
     * Run tournament with just fine-tuned masters (focused testing)
     */
    public static void runFineTunedMastersTournament(Context context) {
        Log.d(TAG, "🚀 Starting FINE-TUNED Masters Tournament");
        
        // Exclude Magnus as current champion
        List<String> candidates = new ArrayList<>(FINE_TUNED_MASTERS);
        candidates.remove("Magnus Carlsen");
        
        Log.d(TAG, "👑 Current Champion: Magnus Carlsen (excluded from Candidates)");
        Log.d(TAG, "🎯 Fine-tuned Candidates (" + candidates.size() + " masters):");
        for (int i = 0; i < candidates.size(); i++) {
            Log.d(TAG, String.format("  %d. %s ✅", i + 1, candidates.get(i)));
        }
        
        runTournamentWithMasters(context, candidates, "FINE-TUNED");
    }
    
    /**
     * Run tournament with newly created assistants (system instruction testing)
     */
    public static void runNewAssistantsTournament(Context context) {
        Log.d(TAG, "🚀 Starting NEW ASSISTANTS Tournament");
        
        List<String> candidates = new ArrayList<>(NEW_ASSISTANT_MASTERS);
        
        Log.d(TAG, "🎯 New Assistant Candidates (" + candidates.size() + " masters):");
        for (int i = 0; i < candidates.size(); i++) {
            Log.d(TAG, String.format("  %d. %s 🆕", i + 1, candidates.get(i)));
        }
        
        Log.d(TAG, "📝 Testing system instructions without vector stores or fine-tuned models");
        runTournamentWithMasters(context, candidates, "NEW-ASSISTANTS");
    }
    
    /**
     * Core tournament logic (extracted for reuse)
     */
    private static void runTournamentWithMasters(Context context, List<String> candidates, String tournamentType) {
        
        // Initialize tournament manager
        TournamentManager manager = TournamentManager.getInstance(context);
        
        // Show initial ELO rankings for tournament participants
        Log.d(TAG, String.format("\n📊 INITIAL ELO RANKINGS (%s Tournament):", tournamentType));
        List<TournamentManager.MasterRanking> leaderboard = manager.getCurrentLeaderboard();
        
        int rank = 1;
        for (TournamentManager.MasterRanking master : leaderboard) {
            if (candidates.contains(master.masterName)) {
                boolean hasFineTuned = FINE_TUNED_MASTERS.contains(master.masterName);
                String status = hasFineTuned ? "✅" : "🔧";
                Log.d(TAG, String.format("  %d. %s - %d ELO %s", 
                    rank++, master.masterName, master.currentElo, status));
            }
        }
        
        // Run tournament with working masters
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        engine.resetTournament();
        
        Log.d(TAG, String.format("\n🏆 STARTING %s CANDIDATES TOURNAMENT", tournamentType));
        TournamentSimulationEngine.TournamentResults results = 
            engine.simulateFullTournament(candidates);
        
        // Display results
        displayTournamentResults(results, tournamentType);
        
        // Show ELO rankings after tournament
        showEloRankingsUpdate(context, candidates);
        
        // Select and show best games from candidates tournament
        List<TournamentSimulationEngine.GameResult> bestCandidatesGames = 
            engine.selectBestGames(results.allResults, 3);
        
        // Launch interactive game viewer for candidates highlights
        TournamentGameViewer.viewTournamentHighlights(context, bestCandidatesGames, "CANDIDATES");
        
        // Show psychological insights
        showPsychologicalInsights(engine, candidates);
        
        // Simulate championship match
        String challenger = results.winner;
        List<TournamentSimulationEngine.GameResult> championshipGames = 
            simulateChampionshipMatch("Magnus Carlsen", challenger);
        
        // Select and show best championship games
        List<TournamentSimulationEngine.GameResult> bestChampionshipGames = 
            engine.selectBestGames(championshipGames, 3);
        
        // Launch interactive game viewer for championship highlights
        TournamentGameViewer.viewTournamentHighlights(context, bestChampionshipGames, "CHAMPIONSHIP");
        
        Log.d(TAG, String.format("✅ %s tournament demo complete!", tournamentType));
    }
    
    /**
     * Display detailed tournament results
     */
    private static void displayTournamentResults(TournamentSimulationEngine.TournamentResults results, String tournamentType) {
        Log.d(TAG, String.format("\n🎊 %s CANDIDATES TOURNAMENT RESULTS:", tournamentType));
        Log.d(TAG, "🏆 Winner: " + results.winner + " (earns right to challenge Magnus!)");
        
        // Final standings
        List<Map.Entry<String, Float>> sortedStandings = 
            new ArrayList<>(results.standings.entrySet());
        sortedStandings.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
        
        int totalGamesPerMaster = (sortedStandings.size() - 1) * 2; // Each master plays others twice
        
        Log.d(TAG, "\n📊 FINAL STANDINGS:");
        for (int i = 0; i < sortedStandings.size(); i++) {
            Map.Entry<String, Float> entry = sortedStandings.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "  ";
            boolean hasFineTuned = FINE_TUNED_MASTERS.contains(entry.getKey());
            String status = hasFineTuned ? "✅" : "🔧";
            float percentage = (entry.getValue() / totalGamesPerMaster) * 100;
            
            Log.d(TAG, String.format("%s %d. %s: %.1f/%d points (%.1f%%) %s", 
                medal, i + 1, entry.getKey(), entry.getValue(), totalGamesPerMaster, percentage, status));
        }
        
        // Show some notable game results
        Log.d(TAG, "\n🎯 NOTABLE RESULTS:");
        showNotableGames(results.allResults);
        
        // Tournament statistics
        showTournamentStats(results);
    }
    
    /**
     * Show interesting game results from the tournament
     */
    private static void showNotableGames(List<TournamentSimulationEngine.GameResult> allResults) {
        Map<String, List<TournamentSimulationEngine.GameResult>> masterGames = new HashMap<>();
        
        // Group games by participants
        for (TournamentSimulationEngine.GameResult game : allResults) {
            String key = game.whiteMaster + " vs " + game.blackMaster;
            masterGames.computeIfAbsent(key, k -> new ArrayList<>()).add(game);
        }
        
        // Show classic rivalries
        showClassicRivalry("Bobby Fischer", "Garry Kasparov", allResults);
        showClassicRivalry("Mikhail Tal", "José Raúl Capablanca", allResults);
        showClassicRivalry("Alexander Alekhine", "Viswanathan Anand", allResults);
    }
    
    /**
     * Show head-to-head results for classic rivalries
     */
    private static void showClassicRivalry(String master1, String master2, 
                                         List<TournamentSimulationEngine.GameResult> allResults) {
        int master1Wins = 0, master2Wins = 0, draws = 0;
        
        for (TournamentSimulationEngine.GameResult game : allResults) {
            if ((game.whiteMaster.equals(master1) && game.blackMaster.equals(master2)) ||
                (game.whiteMaster.equals(master2) && game.blackMaster.equals(master1))) {
                
                if (game.score == 1.0f) {
                    if (game.whiteMaster.equals(master1)) master1Wins++;
                    else master2Wins++;
                } else if (game.score == 0.0f) {
                    if (game.blackMaster.equals(master1)) master1Wins++;
                    else master2Wins++;
                } else {
                    draws++;
                }
            }
        }
        
        if (master1Wins + master2Wins + draws > 0) {
            Log.d(TAG, String.format("  🔥 %s vs %s: %d-%d-%d", 
                master1, master2, master1Wins, draws, master2Wins));
        }
    }
    
    /**
     * Show tournament statistics
     */
    private static void showTournamentStats(TournamentSimulationEngine.TournamentResults results) {
        int totalGames = results.allResults.size();
        int decisiveGames = 0;
        int draws = 0;
        
        for (TournamentSimulationEngine.GameResult game : results.allResults) {
            if (game.score == 0.5f) {
                draws++;
            } else {
                decisiveGames++;
            }
        }
        
        float drawRate = (draws / (float) totalGames) * 100;
        
        Log.d(TAG, "\n📈 TOURNAMENT STATISTICS:");
        Log.d(TAG, String.format("  Total games: %d", totalGames));
        Log.d(TAG, String.format("  Decisive games: %d (%.1f%%)", decisiveGames, 100 - drawRate));
        Log.d(TAG, String.format("  Draws: %d (%.1f%%)", draws, drawRate));
        
        // Expected for high-level play: 50-60% draws
        if (drawRate > 60) {
            Log.d(TAG, "  🎯 High draw rate - masters playing solid chess");
        } else if (drawRate < 40) {
            Log.d(TAG, "  ⚔️ Low draw rate - aggressive tournament play");
        } else {
            Log.d(TAG, "  ✅ Realistic draw rate for elite competition");
        }
    }
    
    /**
     * Show psychological insights from the tournament
     */
    private static void showPsychologicalInsights(TournamentSimulationEngine engine, 
                                                 List<String> candidates) {
        Log.d(TAG, "\n🧠 PSYCHOLOGICAL INSIGHTS:");
        
        Map<String, String> emotionalStates = engine.getCurrentEmotionalStates();
        
        for (String master : candidates) {
            String emotion = emotionalStates.get(master);
            if (emotion != null) {
                Log.d(TAG, String.format("  🎭 %s: %s", master, emotion));
            }
        }
        
        // Test specific psychological factors
        testFischerVolatility();
        testTalExcitement();
        testCapablancaStability();
    }
    
    /**
     * Test Fischer's emotional volatility in tournament pressure
     */
    private static void testFischerVolatility() {
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        
        // Test Fischer in early vs late tournament rounds
        Log.d(TAG, "\n🎯 FISCHER VOLATILITY TEST:");
        
        int earlyWins = 0, lateWins = 0;
        
        // Early tournament (low pressure)
        for (int i = 0; i < 10; i++) {
            TournamentSimulationEngine.GameResult result = 
                engine.simulateGame("Bobby Fischer", "José Raúl Capablanca", 1);
            if ((result.whiteMaster.equals("Bobby Fischer") && result.score == 1.0f) ||
                (result.blackMaster.equals("Bobby Fischer") && result.score == 0.0f)) {
                earlyWins++;
            }
        }
        
        engine.resetTournament();
        
        // Late tournament (high pressure)
        for (int i = 0; i < 10; i++) {
            TournamentSimulationEngine.GameResult result = 
                engine.simulateGame("Bobby Fischer", "José Raúl Capablanca", 14);
            if ((result.whiteMaster.equals("Bobby Fischer") && result.score == 1.0f) ||
                (result.blackMaster.equals("Bobby Fischer") && result.score == 0.0f)) {
                lateWins++;
            }
        }
        
        Log.d(TAG, String.format("  Fischer vs Capablanca: Early %d/10, Late %d/10", 
            earlyWins, lateWins));
        Log.d(TAG, String.format("  Pressure impact: %+d%% performance change", 
            (lateWins - earlyWins) * 10));
    }
    
    /**
     * Test Tal's excitement factor in tactical positions
     */
    private static void testTalExcitement() {
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        
        Log.d(TAG, "\n🎯 TAL EXCITEMENT TEST:");
        
        int decisiveGames = 0;
        
        // Tal tends to create more decisive results due to tactical style
        for (int i = 0; i < 20; i++) {
            TournamentSimulationEngine.GameResult result = 
                engine.simulateGame("Mikhail Tal", "Viswanathan Anand", 7);
            if (result.score != 0.5f) { // Non-draw = more "exciting"
                decisiveGames++;
            }
        }
        
        Log.d(TAG, String.format("  Tal vs Anand: %d/20 decisive games (%.0f%%)", 
            decisiveGames, decisiveGames * 5.0f));
        
        if (decisiveGames > 12) {
            Log.d(TAG, "  🎭 High decisiveness - Tal's tactical style creating complications");
        } else {
            Log.d(TAG, "  📊 Normal decisiveness for elite play");
        }
    }
    
    /**
     * Test Capablanca's legendary stability
     */
    private static void testCapablancaStability() {
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        
        Log.d(TAG, "\n🎯 CAPABLANCA STABILITY TEST:");
        
        int[] roundPerformance = new int[5];
        
        // Test consistency across tournament rounds
        for (int round = 1; round <= 5; round++) {
            int wins = 0;
            for (int i = 0; i < 10; i++) {
                TournamentSimulationEngine.GameResult result = 
                    engine.simulateGame("José Raúl Capablanca", "Alexander Alekhine", round * 3);
                if ((result.whiteMaster.equals("José Raúl Capablanca") && result.score >= 0.5f) ||
                    (result.blackMaster.equals("José Raúl Capablanca") && result.score <= 0.5f)) {
                    wins++;
                }
            }
            roundPerformance[round - 1] = wins;
            Log.d(TAG, String.format("  Round %d: %d/10 points vs Alekhine", round * 3, wins));
        }
        
        // Calculate variance to measure stability
        float average = 0;
        for (int perf : roundPerformance) average += perf;
        average /= roundPerformance.length;
        
        float variance = 0;
        for (int perf : roundPerformance) {
            variance += Math.pow(perf - average, 2);
        }
        variance /= roundPerformance.length;
        
        Log.d(TAG, String.format("  Consistency: %.1f average, %.1f variance", average, variance));
        
        if (variance < 1.5) {
            Log.d(TAG, "  🎯 Excellent stability - true to Capablanca's legendary consistency");
        } else {
            Log.d(TAG, "  📊 Normal variation for tournament play");
        }
    }
    
    /**
     * Simulate championship match between Magnus and the challenger
     */
    private static List<TournamentSimulationEngine.GameResult> simulateChampionshipMatch(String champion, String challenger) {
        Log.d(TAG, "\n👑 WORLD CHAMPIONSHIP MATCH");
        Log.d(TAG, "Champion: " + champion + " vs Challenger: " + challenger);
        
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        float championScore = 0.0f;
        float challengerScore = 0.0f;
        List<TournamentSimulationEngine.GameResult> championshipGames = new ArrayList<>();
        
        // Best of 14 classical games (like actual championships)
        for (int game = 1; game <= 14; game++) {
            String white = (game % 2 == 1) ? champion : challenger;
            String black = (game % 2 == 1) ? challenger : champion;
            
            TournamentSimulationEngine.GameResult gameResult = engine.simulateGame(white, black, game);
            TournamentSimulationEngine.GameResult result = 
                new TournamentSimulationEngine.GameResult(white, black, 
                    gameResult.score, gameResult.description, game, "CHAMPIONSHIP");
            
            championshipGames.add(result);
            
            if (white.equals(champion)) {
                championScore += result.score;
                challengerScore += (1.0f - result.score);
            } else {
                championScore += (1.0f - result.score);
                challengerScore += result.score;
            }
            
            Log.d(TAG, String.format("  Game %d: %s (Score: %.1f - %.1f)", 
                game, result.description, championScore, challengerScore));
            
            // Early finish if someone gets 7.5 points
            if (championScore >= 7.5f || challengerScore >= 7.5f) {
                break;
            }
        }
        
        if (championScore > challengerScore) {
            Log.d(TAG, "🛡️ " + champion + " retains the championship!");
        } else {
            Log.d(TAG, "👑 NEW WORLD CHAMPION: " + challenger + "!");
        }
        
        Log.d(TAG, String.format("Final Score: %s %.1f - %.1f %s", 
            champion, championScore, challengerScore, challenger));
        
        return championshipGames;
    }
    
    /**
     * Show ELO rankings update after tournament
     */
    private static void showEloRankingsUpdate(Context context, List<String> participants) {
        Log.d(TAG, "\n📊 ELO RANKINGS UPDATE:");
        
        TournamentManager manager = TournamentManager.getInstance(context);
        List<TournamentManager.MasterRanking> leaderboard = manager.getCurrentLeaderboard();
        
        int rank = 1;
        for (TournamentManager.MasterRanking master : leaderboard) {
            if (participants.contains(master.masterName)) {
                String arrow = getEloArrow(master.currentElo, master.peakElo);
                Log.d(TAG, String.format("  %d. %s - %d ELO %s", 
                    rank++, master.masterName, master.currentElo, arrow));
            }
        }
        
        // Show biggest gainers and losers
        showEloChanges(participants, leaderboard);
    }
    
    /**
     * Show biggest ELO changes from tournament
     */
    private static void showEloChanges(List<String> participants, List<TournamentManager.MasterRanking> leaderboard) {
        Log.d(TAG, "\n🔄 TOURNAMENT ELO CHANGES:");
        
        List<TournamentManager.MasterRanking> tournamentPlayers = new ArrayList<>();
        for (TournamentManager.MasterRanking ranking : leaderboard) {
            if (participants.contains(ranking.masterName)) {
                tournamentPlayers.add(ranking);
            }
        }
        
        // Sort by form (which tracks recent performance)
        tournamentPlayers.sort((a, b) -> Float.compare(b.currentForm, a.currentForm));
        
        // Show top 3 performers
        Log.d(TAG, "  📈 TOP PERFORMERS:");
        for (int i = 0; i < Math.min(3, tournamentPlayers.size()); i++) {
            TournamentManager.MasterRanking master = tournamentPlayers.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : "🥉";
            Log.d(TAG, String.format("    %s %s - Current form: +%.1f", 
                medal, master.masterName, master.currentForm * 50)); // Convert to ELO equivalent
        }
        
        // Show bottom 3 performers
        Log.d(TAG, "  📉 STRUGGLING PLAYERS:");
        int start = Math.max(0, tournamentPlayers.size() - 3);
        for (int i = start; i < tournamentPlayers.size(); i++) {
            TournamentManager.MasterRanking master = tournamentPlayers.get(i);
            Log.d(TAG, String.format("    ⚠️ %s - Current form: %.1f", 
                master.masterName, master.currentForm * 50));
        }
    }
    
    /**
     * Get arrow indicating ELO trend
     */
    private static String getEloArrow(int currentElo, int peakElo) {
        if (currentElo > peakElo + 10) {
            return "📈 NEW PEAK!";
        } else if (currentElo > peakElo - 5) {
            return "↗️ Rising";
        } else if (currentElo > peakElo - 20) {
            return "→ Stable";
        } else if (currentElo > peakElo - 50) {
            return "↘️ Declining";
        } else {
            return "📉 Struggling";
        }
    }
    
    /**
     * Quick multi-season simulation to populate database
     */
    public static void runMultiSeasonSimulation(Context context, int seasons) {
        Log.d(TAG, "⚡ Running " + seasons + " season simulation with working masters");
        
        long startTime = System.currentTimeMillis();
        
        for (int season = 1; season <= seasons; season++) {
            Log.d(TAG, "\n🏆 SEASON " + season);
            
            // Use all configured masters as candidates (excluding current champion)
            List<String> candidates = new ArrayList<>(ALL_CONFIGURED_MASTERS);
            candidates.remove("Magnus Carlsen"); // Current champion
            
            TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
            engine.resetTournament();
            
            TournamentSimulationEngine.TournamentResults results = 
                engine.simulateQuickTournament(candidates);
            
            Log.d(TAG, "Season " + season + " winner: " + results.winner);
            
            // Simple championship result (could expand to full match)
            String champion = "Magnus Carlsen";
            boolean challengerWins = Math.random() < 0.3; // 30% chance challenger wins
            
            if (challengerWins) {
                Log.d(TAG, "👑 NEW CHAMPION: " + results.winner + " defeats Magnus!");
                champion = results.winner;
            } else {
                Log.d(TAG, "🛡️ Magnus retains championship vs " + results.winner);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        Log.d(TAG, String.format("✅ %d seasons completed in %.2fs", 
            seasons, duration / 1000.0f));
    }
    
    /**
     * Show tournament selection dialog for menu integration
     */
    public static void showTournamentSelectionDialog(Context context) {
        if (!(context instanceof AppCompatActivity)) {
            Log.e(TAG, "❌ Context must be an AppCompatActivity for dialog");
            return;
        }
        
        AppCompatActivity activity = (AppCompatActivity) context;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("🏆 Tournament Simulation");
        builder.setMessage("Choose tournament type:\n\n" +
                          "🎯 Fine-tuned Masters: 5 masters with custom models\n" +
                          "🆕 New Assistants: Gukesh, Hikaru, Petrosian, Nimzowitsch\n" +
                          "🌟 All Configured Masters: 15 masters (full roster)\n" +
                          "⚡ Multi-season Simulation: 5 seasons for database");
        
        builder.setPositiveButton("🎯 Fine-tuned", (dialog, which) -> {
            runFineTunedMastersTournament(context);
            Toast.makeText(context, "🏆 Fine-tuned tournament started! Check logs.", Toast.LENGTH_LONG).show();
        });
        
        builder.setNeutralButton("🆕 New Assistants", (dialog, which) -> {
            runNewAssistantsTournament(context);
            Toast.makeText(context, "🆕 New assistants tournament started! Check logs.", Toast.LENGTH_LONG).show();
        });
        
        builder.setNegativeButton("🌟 More Options", (dialog, which) -> {
            // Show sub-dialog for additional options
            showAdvancedTournamentDialog(context);
        });
        
        builder.show();
    }
    
    /**
     * Sub-dialog for expanded tournament options
     */
    private static void showAdvancedTournamentDialog(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("🌟 Advanced Tournament Options");
        builder.setMessage("Choose tournament type:\n\n" +
                          "🏆 Full Roster: All 15 masters tournament\n" +
                          "⚡ Multi-season: 5 seasons for database\n" +
                          "⏭️ Skip Games Demo: Tournament with game skipping");
        
        builder.setPositiveButton("🏆 Full Roster", (dialog, which) -> {
            runExpandedTournament(context);
            Toast.makeText(context, "🌟 Full roster tournament started! Check logs.", Toast.LENGTH_LONG).show();
        });
        
        builder.setNeutralButton("⚡ Multi-season", (dialog, which) -> {
            runMultiSeasonSimulation(context, 5);
            Toast.makeText(context, "⚡ Multi-season simulation started! Check logs.", Toast.LENGTH_LONG).show();
        });
        
        builder.setNegativeButton("⏭️ Skip Games Demo", (dialog, which) -> {
            runSkipGamesDemoTournament(context);
            Toast.makeText(context, "⏭️ Skip games demo started! Check logs.", Toast.LENGTH_LONG).show();
        });
        
        builder.show();
    }
    
    /**
     * Demonstration of skip games functionality
     */
    public static void runSkipGamesDemoTournament(Context context) {
        Log.d(TAG, "🚀 Starting SKIP GAMES DEMO tournament");
        
        // Use fine-tuned masters for demo
        List<String> candidates = new ArrayList<>(FINE_TUNED_MASTERS);
        candidates.remove("Magnus Carlsen"); // Exclude Magnus as current champion
        
        Log.d(TAG, "👑 Current Champion: Magnus Carlsen (excluded from demo)");
        Log.d(TAG, "🎯 Demo Candidates (with game skipping): " + candidates);
        
        // Create skip callback that skips specific scenarios
        String[] mastersToSkip = {"Bobby Fischer"}; // Skip all Fischer games for demo
        int[] roundsToSkip = {3, 7, 11}; // Skip rounds 3, 7, and 11
        
        TournamentSimulationEngine.SkipGameCallback skipCallback = 
            TournamentSimulationEngine.createSkipCallback(mastersToSkip, roundsToSkip);
        
        // Also demo random skipping for some variety
        TournamentSimulationEngine.SkipGameCallback randomSkipCallback = 
            TournamentSimulationEngine.createRandomSkipCallback(0.1f); // Skip 10% randomly
        
        // Combine skip callbacks
        TournamentSimulationEngine.SkipGameCallback combinedCallback = new TournamentSimulationEngine.SkipGameCallback() {
            @Override
            public boolean shouldSkipGame(String whiteMaster, String blackMaster, int roundNumber) {
                return skipCallback.shouldSkipGame(whiteMaster, blackMaster, roundNumber) ||
                       randomSkipCallback.shouldSkipGame(whiteMaster, blackMaster, roundNumber);
            }
            
            @Override
            public void onGameSkipped(String whiteMaster, String blackMaster, int roundNumber) {
                if (skipCallback.shouldSkipGame(whiteMaster, blackMaster, roundNumber)) {
                    Log.d(TAG, String.format("⏭️ CRITERIA SKIP: Round %d - %s vs %s (Fischer or specific round)", 
                           roundNumber, whiteMaster, blackMaster));
                } else {
                    Log.d(TAG, String.format("🎲 RANDOM SKIP: Round %d - %s vs %s", 
                           roundNumber, whiteMaster, blackMaster));
                }
            }
        };
        
        // Initialize tournament manager
        TournamentManager manager = TournamentManager.getInstance(context);
        
        // Run tournament with skip functionality
        TournamentSimulationEngine engine = TournamentSimulationEngine.getInstance();
        engine.resetTournament();
        
        Log.d(TAG, "\n🏆 STARTING SKIP GAMES DEMO TOURNAMENT");
        Log.d(TAG, "📋 Skip Rules:");
        Log.d(TAG, "  - All Bobby Fischer games skipped");
        Log.d(TAG, "  - Rounds 3, 7, 11 entirely skipped");
        Log.d(TAG, "  - 10% random game skipping");
        
        TournamentSimulationEngine.TournamentResults results = 
            engine.simulateFullTournament(candidates, combinedCallback);
        
        // Display results
        displayTournamentResults(results, "SKIP-DEMO");
        
        // Show ELO rankings after tournament
        showEloRankingsUpdate(context, candidates);
        
        // Select and show best games from remaining games
        List<TournamentSimulationEngine.GameResult> bestCandidatesGames = 
            engine.selectBestGames(results.allResults, 3);
        
        // Launch interactive game viewer for skip demo highlights
        TournamentGameViewer.viewTournamentHighlights(context, bestCandidatesGames, "SKIP-DEMO");
        
        // Count skipped vs played games
        int skippedGames = 0;
        int playedGames = 0;
        for (TournamentSimulationEngine.GameResult game : results.allResults) {
            if (game.description.contains("skipped")) {
                skippedGames++;
            } else {
                playedGames++;
            }
        }
        
        Log.d(TAG, String.format("\n📊 SKIP DEMO STATISTICS:"));
        Log.d(TAG, String.format("  Games played: %d", playedGames));
        Log.d(TAG, String.format("  Games skipped: %d", skippedGames));
        Log.d(TAG, String.format("  Skip rate: %.1f%%", (skippedGames / (float)(playedGames + skippedGames)) * 100));
        
        Log.d(TAG, "✅ Skip games demo tournament complete!");
    }
    
    /**
     * Integration method - call this from your MainActivity or existing activities
     */
    public static void runQuickTest(Context context) {
        Log.d(TAG, "🧪 Running quick masters test");
        
        try {
            // Show selection dialog instead of running everything
            showTournamentSelectionDialog(context);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Test failed", e);
        }
    }
}