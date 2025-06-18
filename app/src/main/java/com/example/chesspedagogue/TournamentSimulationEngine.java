package com.example.chesspedagogue;

import android.util.Log;
import java.util.*;

public class TournamentSimulationEngine {
    private static final String TAG = "🏆 TournamentSim";
    
    // Simplified emotional states for simulation
    public enum SimulationEmotion {
        CONFIDENT(1.1f, "feels in control"),
        FRUSTRATED(0.85f, "struggling with form"), // Less punishing
        EXCITED(1.05f, "energized by competition"),
        ANALYTICAL(1.0f, "focused and calculating"),
        CONCERNED(0.9f, "worried about standing"), // Less punishing
        PLEASED(1.08f, "satisfied with progress"),
        PRESSURED(0.88f, "feeling tournament stress"), // Less punishing
        CALM(1.02f, "composed and steady"),
        PHILOSOPHICAL(1.03f, "deep in theoretical thought");
        
        public final float performanceModifier;
        public final String description;
        
        SimulationEmotion(float modifier, String desc) {
            this.performanceModifier = modifier;
            this.description = desc;
        }
    }
    
    // Simplified master profiles for tournament simulation
    public static class TournamentMasterProfile {
        public String masterName;
        public int peakElo;
        public float psychologicalStability;  // 0.2 (Fischer) to 1.0 (Carlsen)
        public float clutchFactor;           // Performance under pressure
        public float emotionalVolatility;    // How much emotions affect play
        public String[] preferredEmotions;   // Most common emotional states
        public float[] styleStrengths;       // [tactical, positional, endgame, opening]
        
        public TournamentMasterProfile(String name, int elo, float stability, float clutch, 
                                     float volatility, String[] emotions, float[] strengths) {
            this.masterName = name;
            this.peakElo = elo;
            this.psychologicalStability = stability;
            this.clutchFactor = clutch;
            this.emotionalVolatility = volatility;
            this.preferredEmotions = emotions;
            this.styleStrengths = strengths;
        }
    }
    
    // Tournament state tracking
    public static class TournamentState {
        public Map<String, Integer> standings = new HashMap<>();
        public Map<String, Float> currentForm = new HashMap<>();
        public Map<String, SimulationEmotion> currentEmotions = new HashMap<>();
        public List<String> recentResults = new ArrayList<>();
        public int currentRound = 1;
        
        // Relationship dynamics (simplified from your full EQ system)
        public Map<String, Map<String, Float>> relationshipChemistry = new HashMap<>();
    }
    
    private static TournamentSimulationEngine instance;
    private TournamentState tournamentState;
    private Map<String, TournamentMasterProfile> masterProfiles;
    private Random random;
    
    public static TournamentSimulationEngine getInstance() {
        if (instance == null) {
            instance = new TournamentSimulationEngine();
        }
        return instance;
    }
    
    private TournamentSimulationEngine() {
        this.random = new Random();
        initializeMasterProfiles();
        this.tournamentState = new TournamentState();
    }
    
    private void initializeMasterProfiles() {
        masterProfiles = new HashMap<>();
        
        // Based on your existing emotional profiles but simplified
        masterProfiles.put("Magnus Carlsen", new TournamentMasterProfile(
            "Magnus Carlsen", 2882, 1.0f, 0.95f, 0.3f,
            new String[]{"ANALYTICAL", "CONFIDENT", "PLEASED"},
            new float[]{0.85f, 0.95f, 0.98f, 0.88f}
        ));
        
        masterProfiles.put("Garry Kasparov", new TournamentMasterProfile(
            "Garry Kasparov", 2851, 0.7f, 0.92f, 0.8f,
            new String[]{"CONFIDENT", "EXCITED", "ANALYTICAL"},
            new float[]{0.95f, 0.90f, 0.85f, 0.92f}
        ));
        
        masterProfiles.put("Bobby Fischer", new TournamentMasterProfile(
            "Bobby Fischer", 2785, 0.45f, 0.98f, 0.8f, // Improved stability, reduced volatility
            new String[]{"CONFIDENT", "ANALYTICAL", "EXCITED"}, // More balanced emotions
            new float[]{0.88f, 0.92f, 0.95f, 0.90f}
        ));
        
        masterProfiles.put("Mikhail Tal", new TournamentMasterProfile(
            "Mikhail Tal", 2705, 0.4f, 0.85f, 1.2f,
            new String[]{"EXCITED", "CONFIDENT", "PLEASED"},
            new float[]{0.98f, 0.75f, 0.70f, 0.82f}
        ));
        
        masterProfiles.put("Anatoly Karpov", new TournamentMasterProfile(
            "Anatoly Karpov", 2780, 0.9f, 0.70f, 0.6f, // Low clutch due to Kasparov battles
            new String[]{"ANALYTICAL", "CONCERNED", "FRUSTRATED"},
            new float[]{0.78f, 0.95f, 0.92f, 0.85f}
        ));
        
        masterProfiles.put("Alexander Alekhine", new TournamentMasterProfile(
            "Alexander Alekhine", 2690, 0.6f, 0.88f, 0.9f,
            new String[]{"CONFIDENT", "EXCITED", "ANALYTICAL"},
            new float[]{0.92f, 0.88f, 0.85f, 0.90f}
        ));
        
        masterProfiles.put("José Raúl Capablanca", new TournamentMasterProfile(
            "José Raúl Capablanca", 2720, 0.95f, 0.85f, 0.4f,
            new String[]{"CONFIDENT", "ANALYTICAL", "PLEASED"},
            new float[]{0.80f, 0.92f, 0.98f, 0.88f}
        ));
        
        masterProfiles.put("Vladimir Kramnik", new TournamentMasterProfile(
            "Vladimir Kramnik", 2817, 0.8f, 0.90f, 0.5f,
            new String[]{"ANALYTICAL", "CONFIDENT", "PLEASED"},
            new float[]{0.85f, 0.95f, 0.90f, 0.92f}
        ));
        
        // New legend additions
        masterProfiles.put("Hikaru Nakamura", new TournamentMasterProfile(
            "Hikaru Nakamura", 2800, 0.6f, 0.85f, 0.8f,
            new String[]{"EXCITED", "CONFIDENT", "ANALYTICAL"},
            new float[]{0.95f, 0.82f, 0.78f, 0.88f}  // Tactical beast, weaker in endgames
        ));
        
        masterProfiles.put("Tigran Petrosian", new TournamentMasterProfile(
            "Tigran Petrosian", 2745, 0.95f, 0.88f, 0.3f, // "Iron Tigran" - ultra stable
            new String[]{"ANALYTICAL", "CONFIDENT", "CALM"},
            new float[]{0.75f, 0.98f, 0.95f, 0.85f}  // Defensive master, positional genius
        ));
        
        masterProfiles.put("Aron Nimzowitsch", new TournamentMasterProfile(
            "Aron Nimzowitsch", 2720, 0.4f, 0.82f, 1.1f, // Hypermodern pioneer, eccentric
            new String[]{"ANALYTICAL", "EXCITED", "PHILOSOPHICAL"},
            new float[]{0.88f, 0.92f, 0.80f, 0.95f}  // Theoretical master, strong openings
        ));
        
        masterProfiles.put("Viswanathan Anand", new TournamentMasterProfile(
            "Viswanathan Anand", 2817, 0.85f, 0.88f, 0.5f, // Universal style, highly adaptable
            new String[]{"ANALYTICAL", "CONFIDENT", "PLEASED"},
            new float[]{0.88f, 0.92f, 0.95f, 0.90f}  // Universal master, strong in all phases
        ));
        
        masterProfiles.put("Gukesh Dommaraju", new TournamentMasterProfile(
            "Gukesh Dommaraju", 2760, 0.75f, 0.85f, 0.6f, // Young champion, still developing stability
            new String[]{"CONFIDENT", "EXCITED", "ANALYTICAL"},
            new float[]{0.90f, 0.85f, 0.80f, 0.92f}  // Strong in calculation and openings
        ));
        
        masterProfiles.put("Paul Morphy", new TournamentMasterProfile(
            "Paul Morphy", 2650, 0.80f, 0.90f, 0.7f, // Romantic era genius, natural talent
            new String[]{"CONFIDENT", "PLEASED", "EXCITED"},
            new float[]{0.95f, 0.80f, 0.85f, 0.88f}  // Brilliant tactician, weaker in endgames
        ));
        
        masterProfiles.put("Emanuel Lasker", new TournamentMasterProfile(
            "Emanuel Lasker", 2660, 0.88f, 0.92f, 0.5f, // Longest reigning champion, psychologically tough
            new String[]{"ANALYTICAL", "CONFIDENT", "PLEASED"},
            new float[]{0.85f, 0.90f, 0.95f, 0.87f}  // Endgame master, psychological play
        ));
        
        masterProfiles.put("Mikhail Botvinnik", new TournamentMasterProfile(
            "Mikhail Botvinnik", 2720, 0.90f, 0.88f, 0.4f, // Scientific approach, very methodical
            new String[]{"ANALYTICAL", "CONFIDENT", "PLEASED"},
            new float[]{0.82f, 0.93f, 0.88f, 0.90f}  // Strong preparation and positional play
        ));
        
        Log.d(TAG, "✅ Initialized tournament profiles for " + masterProfiles.size() + " masters");
    }
    
    /**
     * Simulates a single game between two masters
     * Uses simplified version of your EQ system for realistic results
     */
    public GameResult simulateGame(String whiteMaster, String blackMaster, int roundNumber) {
        TournamentMasterProfile whiteProfile = masterProfiles.get(whiteMaster);
        TournamentMasterProfile blackProfile = masterProfiles.get(blackMaster);
        
        if (whiteProfile == null || blackProfile == null) {
            Log.e(TAG, "❌ Missing profile for " + whiteMaster + " vs " + blackMaster);
            return new GameResult(whiteMaster, blackMaster, 0.5f, "Missing profile");
        }
        
        // Calculate base playing strength
        float whiteStrength = calculateCurrentStrength(whiteProfile, whiteMaster, roundNumber);
        float blackStrength = calculateCurrentStrength(blackProfile, blackMaster, roundNumber);
        
        // Apply emotional states and psychology
        whiteStrength *= getEmotionalModifier(whiteMaster);
        blackStrength *= getEmotionalModifier(blackMaster);
        
        // Apply relationship chemistry (simplified from your system)
        float chemistry = getRelationshipChemistry(whiteMaster, blackMaster);
        whiteStrength *= chemistry;
        blackStrength *= (2.0f - chemistry); // Inverse for black
        
        // Tournament pressure effects
        whiteStrength *= getTournamentPressure(whiteMaster, roundNumber);
        blackStrength *= getTournamentPressure(blackMaster, roundNumber);
        
        // Calculate result probability
        float strengthDiff = whiteStrength - blackStrength;
        float winProbability = 1.0f / (1.0f + (float)Math.pow(10, -strengthDiff/400.0f));
        
        // Generate result
        float roll = random.nextFloat();
        String result;
        float score;
        
        if (roll < winProbability * 0.6f) { // Reduce decisive game rate
            result = whiteMaster + " wins";
            score = 1.0f;
        } else if (roll < 1.0f - (1.0f - winProbability) * 0.6f) {
            result = blackMaster + " wins";
            score = 0.0f;
        } else {
            result = "Draw";
            score = 0.5f;
        }
        
        // Update emotional states based on result
        updateEmotionalStates(whiteMaster, blackMaster, score, roundNumber);
        
        Log.d(TAG, String.format("🎯 R%d: %s vs %s = %s (%.1f vs %.1f strength)", 
               roundNumber, whiteMaster, blackMaster, result, whiteStrength, blackStrength));
        
        return new GameResult(whiteMaster, blackMaster, score, result, roundNumber, "CANDIDATES");
    }
    
    private float calculateCurrentStrength(TournamentMasterProfile profile, String masterName, int round) {
        float baseStrength = profile.peakElo;
        
        // Apply current form (momentum from recent results)
        Float currentForm = tournamentState.currentForm.get(masterName);
        if (currentForm != null) {
            baseStrength += currentForm * 50; // Max 50 point swing from form
        }
        
        // Add some randomness for realistic variation
        baseStrength += (random.nextGaussian() * 30); // Std dev of 30 points
        
        return baseStrength;
    }
    
    private float getEmotionalModifier(String masterName) {
        SimulationEmotion emotion = tournamentState.currentEmotions.get(masterName);
        if (emotion == null) {
            // Assign random initial emotion based on master's profile
            TournamentMasterProfile profile = masterProfiles.get(masterName);
            String randomEmotion = profile.preferredEmotions[random.nextInt(profile.preferredEmotions.length)];
            emotion = SimulationEmotion.valueOf(randomEmotion);
            tournamentState.currentEmotions.put(masterName, emotion);
        }
        
        TournamentMasterProfile profile = masterProfiles.get(masterName);
        float emotionalImpact = emotion.performanceModifier;
        
        // Scale by master's emotional volatility
        float modifier = 1.0f + (emotionalImpact - 1.0f) * profile.emotionalVolatility;
        
        return Math.max(0.5f, Math.min(1.5f, modifier)); // Clamp between 0.5x and 1.5x
    }
    
    private float getRelationshipChemistry(String master1, String master2) {
        // This would tap into your existing relationship system
        // For now, simplified chemistry calculation
        Map<String, Float> master1Relations = tournamentState.relationshipChemistry.get(master1);
        if (master1Relations == null) {
            master1Relations = new HashMap<>();
            tournamentState.relationshipChemistry.put(master1, master1Relations);
        }
        
        Float chemistry = master1Relations.get(master2);
        if (chemistry == null) {
            // Generate initial chemistry based on playing styles
            chemistry = 0.95f + (float)(random.nextGaussian() * 0.1f); // Slight variation around neutral
            chemistry = Math.max(0.8f, Math.min(1.2f, chemistry));
            master1Relations.put(master2, chemistry);
        }
        
        return chemistry;
    }
    
    private float getTournamentPressure(String masterName, int round) {
        TournamentMasterProfile profile = masterProfiles.get(masterName);
        
        // Pressure increases in later rounds
        float pressureFactor = 1.0f + (round / 14.0f) * 0.3f; // Max 30% pressure increase
        
        // Apply master's clutch factor
        float pressureEffect = 1.0f - (pressureFactor - 1.0f) * (1.0f - profile.clutchFactor);
        
        return Math.max(0.7f, Math.min(1.3f, pressureEffect));
    }
    
    private void updateEmotionalStates(String whiteMaster, String blackMaster, float score, int round) {
        // Update based on game result
        if (score == 1.0f) { // White wins
            updateMasterEmotion(whiteMaster, true);
            updateMasterEmotion(blackMaster, false);
        } else if (score == 0.0f) { // Black wins
            updateMasterEmotion(whiteMaster, false);
            updateMasterEmotion(blackMaster, true);
        } else { // Draw
            // Slight emotional impact based on expectations
        }
        
        // Update form based on results
        updateForm(whiteMaster, score);
        updateForm(blackMaster, 1.0f - score);
    }
    
    private void updateMasterEmotion(String masterName, boolean won) {
        TournamentMasterProfile profile = masterProfiles.get(masterName);
        SimulationEmotion newEmotion;
        
        if (won) {
            newEmotion = random.nextFloat() < 0.7f ? SimulationEmotion.CONFIDENT : SimulationEmotion.PLEASED;
        } else {
            // Emotional reaction depends on master's stability
            if (profile.psychologicalStability < 0.5f) {
                newEmotion = random.nextFloat() < 0.6f ? SimulationEmotion.FRUSTRATED : SimulationEmotion.PRESSURED;
            } else {
                newEmotion = SimulationEmotion.ANALYTICAL; // Stable masters stay focused
            }
        }
        
        tournamentState.currentEmotions.put(masterName, newEmotion);
    }
    
    private void updateForm(String masterName, float result) {
        Float currentForm = tournamentState.currentForm.get(masterName);
        if (currentForm == null) currentForm = 0.0f;
        
        // Exponential moving average for form
        float formUpdate = (result - 0.5f) * 2.0f; // Convert to -1 to +1
        currentForm = currentForm * 0.8f + formUpdate * 0.2f;
        
        tournamentState.currentForm.put(masterName, currentForm);
    }
    
    /**
     * Callback interface for skip game decisions
     */
    public interface SkipGameCallback {
        boolean shouldSkipGame(String whiteMaster, String blackMaster, int roundNumber);
        void onGameSkipped(String whiteMaster, String blackMaster, int roundNumber);
    }
    
    /**
     * Simulates an entire tournament round (all masters play each other once)
     */
    public List<GameResult> simulateRound(List<String> masters, int roundNumber) {
        return simulateRound(masters, roundNumber, null);
    }
    
    /**
     * Simulates an entire tournament round with optional skip functionality
     */
    public List<GameResult> simulateRound(List<String> masters, int roundNumber, SkipGameCallback skipCallback) {
        List<GameResult> roundResults = new ArrayList<>();
        int gamesSkipped = 0;
        
        Log.d(TAG, "🎯 Simulating Round " + roundNumber + " with " + masters.size() + " masters");
        
        // Generate all pairings for this round
        for (int i = 0; i < masters.size(); i++) {
            for (int j = i + 1; j < masters.size(); j++) {
                String master1 = masters.get(i);
                String master2 = masters.get(j);
                
                // Alternate colors fairly
                boolean master1IsWhite = (i + j + roundNumber) % 2 == 0;
                String whiteMaster = master1IsWhite ? master1 : master2;
                String blackMaster = master1IsWhite ? master2 : master1;
                
                // Check if this game should be skipped
                if (skipCallback != null && skipCallback.shouldSkipGame(whiteMaster, blackMaster, roundNumber)) {
                    Log.d(TAG, "⏭️ Skipping game: " + whiteMaster + " vs " + blackMaster);
                    skipCallback.onGameSkipped(whiteMaster, blackMaster, roundNumber);
                    gamesSkipped++;
                    
                    // Add a default draw result for skipped games to maintain tournament integrity
                    GameResult skippedResult = new GameResult(whiteMaster, blackMaster, 0.5f, 
                        "Game skipped - recorded as draw", roundNumber, "CANDIDATES");
                    roundResults.add(skippedResult);
                    continue;
                }
                
                GameResult result = simulateGame(whiteMaster, blackMaster, roundNumber);
                roundResults.add(result);
            }
        }
        
        tournamentState.currentRound = roundNumber + 1;
        Log.d(TAG, String.format("✅ Round %d complete: %d games played, %d games skipped", 
               roundNumber, roundResults.size() - gamesSkipped, gamesSkipped));
        
        return roundResults;
    }
    
    /**
     * Simulates a complete double round-robin tournament (14 rounds)
     */
    public TournamentResults simulateFullTournament(List<String> masters) {
        return simulateFullTournament(masters, null);
    }
    
    /**
     * Simulates a complete double round-robin tournament with optional skip functionality
     */
    public TournamentResults simulateFullTournament(List<String> masters, SkipGameCallback skipCallback) {
        Log.d(TAG, "🏆 Starting full tournament simulation with " + masters.size() + " masters");
        
        TournamentResults results = new TournamentResults();
        results.masters = new ArrayList<>(masters);
        results.allResults = new ArrayList<>();
        
        // Initialize standings
        for (String master : masters) {
            results.standings.put(master, 0.0f);
            tournamentState.currentForm.put(master, 0.0f);
        }
        
        // Double round-robin: 14 rounds for 8 players
        for (int round = 1; round <= 14; round++) {
            List<GameResult> roundResults = simulateRound(masters, round, skipCallback);
            results.allResults.addAll(roundResults);
            
            // Update standings
            for (GameResult game : roundResults) {
                Float whiteScore = results.standings.get(game.whiteMaster);
                Float blackScore = results.standings.get(game.blackMaster);
                
                results.standings.put(game.whiteMaster, whiteScore + game.score);
                results.standings.put(game.blackMaster, blackScore + (1.0f - game.score));
            }
            
            // Log current standings after every few rounds
            if (round % 3 == 0) {
                logCurrentStandings(results.standings, round);
            }
        }
        
        results.winner = findTournamentWinner(results.standings);
        Log.d(TAG, "🏆 Tournament complete! Winner: " + results.winner);
        
        return results;
    }
    
    /**
     * Quick simulation for database population (skips detailed logging)
     */
    public TournamentResults simulateQuickTournament(List<String> masters) {
        TournamentResults results = new TournamentResults();
        results.masters = new ArrayList<>(masters);
        results.allResults = new ArrayList<>();
        
        // Initialize
        for (String master : masters) {
            results.standings.put(master, 0.0f);
            tournamentState.currentForm.put(master, 0.0f);
        }
        
        // Fast simulation - just calculate results without detailed logging
        for (int round = 1; round <= 14; round++) {
            for (int i = 0; i < masters.size(); i++) {
                for (int j = i + 1; j < masters.size(); j++) {
                    String master1 = masters.get(i);
                    String master2 = masters.get(j);
                    
                    boolean master1IsWhite = (i + j + round) % 2 == 0;
                    String whiteMaster = master1IsWhite ? master1 : master2;
                    String blackMaster = master1IsWhite ? master2 : master1;
                    
                    GameResult result = simulateGame(whiteMaster, blackMaster, round);
                    results.allResults.add(result);
                    
                    results.standings.put(whiteMaster, 
                        results.standings.get(whiteMaster) + result.score);
                    results.standings.put(blackMaster, 
                        results.standings.get(blackMaster) + (1.0f - result.score));
                }
            }
        }
        
        results.winner = findTournamentWinner(results.standings);
        return results;
    }
    
    private void logCurrentStandings(Map<String, Float> standings, int round) {
        Log.d(TAG, "📊 Standings after Round " + round + ":");
        
        // Sort by score
        List<Map.Entry<String, Float>> sortedStandings = new ArrayList<>(standings.entrySet());
        sortedStandings.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
        
        for (int i = 0; i < sortedStandings.size(); i++) {
            Map.Entry<String, Float> entry = sortedStandings.get(i);
            Log.d(TAG, String.format("  %d. %s: %.1f points", 
                i + 1, entry.getKey(), entry.getValue()));
        }
    }
    
    private String findTournamentWinner(Map<String, Float> standings) {
        return standings.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
    }
    
    /**
     * Resets tournament state for a fresh simulation
     */
    public void resetTournament() {
        tournamentState = new TournamentState();
        Log.d(TAG, "🔄 Tournament state reset");
    }
    
    /**
     * Gets current tournament emotional states (for UI display)
     */
    public Map<String, String> getCurrentEmotionalStates() {
        Map<String, String> states = new HashMap<>();
        for (Map.Entry<String, SimulationEmotion> entry : tournamentState.currentEmotions.entrySet()) {
            states.put(entry.getKey(), entry.getValue().description);
        }
        return states;
    }
    
    /**
     * Selects the best games from a tournament for highlight viewing
     */
    public List<GameResult> selectBestGames(List<GameResult> allGames, int count) {
        // Sort games by quality (highest first)
        List<GameResult> sortedGames = new ArrayList<>(allGames);
        sortedGames.sort((a, b) -> Float.compare(b.gameQuality, a.gameQuality));
        
        // Take top games, ensuring variety in players
        List<GameResult> bestGames = new ArrayList<>();
        Set<String> featuredPlayers = new HashSet<>();
        
        for (GameResult game : sortedGames) {
            if (bestGames.size() >= count) break;
            
            // Prefer games with new players for variety
            boolean hasNewPlayer = !featuredPlayers.contains(game.whiteMaster) || 
                                 !featuredPlayers.contains(game.blackMaster);
            
            if (hasNewPlayer || bestGames.size() < count / 2) {
                bestGames.add(game);
                featuredPlayers.add(game.whiteMaster);
                featuredPlayers.add(game.blackMaster);
            }
        }
        
        // Fill remaining spots if needed
        for (GameResult game : sortedGames) {
            if (bestGames.size() >= count) break;
            if (!bestGames.contains(game)) {
                bestGames.add(game);
            }
        }
        
        Log.d(TAG, String.format("✨ Selected %d best games from %d total games", 
               bestGames.size(), allGames.size()));
        
        return bestGames;
    }
    
    /**
     * Shows highlights from selected best games
     */
    public void showBestGamesHighlights(List<GameResult> bestGames, String tournamentType) {
        Log.d(TAG, String.format("🎬 %s TOURNAMENT HIGHLIGHTS - TOP %d GAMES:", 
               tournamentType, bestGames.size()));
        
        for (int i = 0; i < bestGames.size(); i++) {
            GameResult game = bestGames.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "⭐";
            
            Log.d(TAG, String.format("%s Game %d: Round %d - %s vs %s", 
                   medal, i + 1, game.round, game.whiteMaster, game.blackMaster));
            Log.d(TAG, String.format("   Result: %s (Quality: %.2f)", 
                   game.description, game.gameQuality));
        }
    }
    
    /**
     * Creates a skip callback that skips games based on specific criteria
     */
    public static SkipGameCallback createSkipCallback(String[] mastersToSkip, int[] roundsToSkip) {
        return new SkipGameCallback() {
            @Override
            public boolean shouldSkipGame(String whiteMaster, String blackMaster, int roundNumber) {
                // Skip if either master is in the skip list
                if (mastersToSkip != null) {
                    for (String masterToSkip : mastersToSkip) {
                        if (whiteMaster.equals(masterToSkip) || blackMaster.equals(masterToSkip)) {
                            return true;
                        }
                    }
                }
                
                // Skip if round is in the skip list
                if (roundsToSkip != null) {
                    for (int roundToSkip : roundsToSkip) {
                        if (roundNumber == roundToSkip) {
                            return true;
                        }
                    }
                }
                
                return false;
            }
            
            @Override
            public void onGameSkipped(String whiteMaster, String blackMaster, int roundNumber) {
                Log.d(TAG, String.format("⏭️ GAME SKIPPED: Round %d - %s vs %s", 
                       roundNumber, whiteMaster, blackMaster));
            }
        };
    }
    
    /**
     * Creates a random skip callback that skips a percentage of games
     */
    public static SkipGameCallback createRandomSkipCallback(float skipPercentage) {
        return new SkipGameCallback() {
            private final Random random = new Random();
            
            @Override
            public boolean shouldSkipGame(String whiteMaster, String blackMaster, int roundNumber) {
                return random.nextFloat() < skipPercentage;
            }
            
            @Override
            public void onGameSkipped(String whiteMaster, String blackMaster, int roundNumber) {
                Log.d(TAG, String.format("🎲 RANDOM SKIP: Round %d - %s vs %s", 
                       roundNumber, whiteMaster, blackMaster));
            }
        };
    }
    
    // Tournament results container
    public static class TournamentResults {
        public List<String> masters = new ArrayList<>();
        public Map<String, Float> standings = new HashMap<>();
        public List<GameResult> allResults = new ArrayList<>();
        public String winner;
        public Map<String, SimulationEmotion> finalEmotions = new HashMap<>();
        
        public void saveToDatabase() {
            // TODO: Implement database persistence
            // This would save all game results to populate your tournament database
        }
    }
    
    // Result class
    public static class GameResult {
        public String whiteMaster;
        public String blackMaster;
        public float score; // 1.0 = white wins, 0.5 = draw, 0.0 = black wins
        public String description;
        public int round;
        public float gameQuality; // For selecting best games
        public String gameType; // "CANDIDATES" or "CHAMPIONSHIP"
        
        public GameResult(String white, String black, float score, String desc) {
            this.whiteMaster = white;
            this.blackMaster = black;
            this.score = score;
            this.description = desc;
            this.round = 1;
            this.gameQuality = calculateGameQuality();
            this.gameType = "CANDIDATES";
        }
        
        public GameResult(String white, String black, float score, String desc, int round, String type) {
            this.whiteMaster = white;
            this.blackMaster = black;
            this.score = score;
            this.description = desc;
            this.round = round;
            this.gameType = type;
            this.gameQuality = calculateGameQuality();
        }
        
        private float calculateGameQuality() {
            // Quality factors: decisive games > draws, critical matchups get bonus
            float quality = 0.5f; // Base quality
            
            // Decisive games are more interesting
            if (score != 0.5f) {
                quality += 0.3f;
            }
            
            // Add randomness for variety (simulates complexity, tactics, etc.)
            quality += (float)(Math.random() * 0.2f);
            
            // High-profile matchups get bonus
            if (isHighProfileMatchup()) {
                quality += 0.15f;
            }
            
            return Math.min(1.0f, quality);
        }
        
        private boolean isHighProfileMatchup() {
            String[] legends = {"Magnus Carlsen", "Garry Kasparov", "Bobby Fischer", "Mikhail Tal", 
                              "José Raúl Capablanca", "Alexander Alekhine", "Vladimir Kramnik"};
            
            for (String legend : legends) {
                if ((whiteMaster.equals(legend) || blackMaster.equals(legend))) {
                    return true;
                }
            }
            return false;
        }
    }
}