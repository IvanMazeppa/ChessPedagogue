package com.example.chesspedagogue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.ContentValues;
import android.util.Log;
import java.util.*;

/**
 * Manages the ongoing Candidates Tournament system
 * Creates a persistent championship cycle where legends compete for the right to challenge Magnus
 */
public class TournamentManager extends SQLiteOpenHelper {
    private static final String TAG = "🏆 TournamentMgr";
    
    private static final String DATABASE_NAME = "tournament_system.db";
    private static final int DATABASE_VERSION = 1;
    
    // Tournament cycle phases
    public enum TournamentPhase {
        QUALIFICATION,    // Determining 8 candidates
        CANDIDATES,       // 8-player double round-robin
        CHAMPIONSHIP,     // Winner vs Magnus
        SEASON_BREAK      // Between cycles
    }
    
    // Current championship system state
    public static class ChampionshipState {
        public String currentChampion = "Magnus Carlsen";
        public TournamentPhase currentPhase = TournamentPhase.QUALIFICATION;
        public int currentSeason = 1;
        public int currentRound = 0;
        public List<String> qualifiedCandidates = new ArrayList<>();
        public String challengerName = "";
        public long lastUpdateTimestamp = System.currentTimeMillis();
    }
    
    private static TournamentManager instance;
    private ChampionshipState championshipState;
    private TournamentSimulationEngine simulationEngine;
    
    // Database tables
    private static final String TABLE_MASTERS = "tournament_masters";
    private static final String TABLE_TOURNAMENT_HISTORY = "tournament_history";
    private static final String TABLE_GAME_RESULTS = "game_results";
    private static final String TABLE_CHAMPIONSHIP_STATE = "championship_state";
    
    public static TournamentManager getInstance(Context context) {
        if (instance == null) {
            instance = new TournamentManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private TournamentManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.simulationEngine = TournamentSimulationEngine.getInstance();
        loadChampionshipState();
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "🗃️ Creating tournament database tables");
        
        // Masters with dynamic ELO ratings
        db.execSQL("CREATE TABLE " + TABLE_MASTERS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT UNIQUE NOT NULL," +
            "current_elo INTEGER NOT NULL," +
            "peak_elo INTEGER NOT NULL," +
            "tournament_wins INTEGER DEFAULT 0," +
            "championship_wins INTEGER DEFAULT 0," +
            "games_played INTEGER DEFAULT 0," +
            "total_score REAL DEFAULT 0.0," +
            "current_form REAL DEFAULT 0.0," +
            "last_updated INTEGER DEFAULT 0" +
            ")");
        
        // Tournament history (seasons/cycles)
        db.execSQL("CREATE TABLE " + TABLE_TOURNAMENT_HISTORY + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "season INTEGER NOT NULL," +
            "tournament_type TEXT NOT NULL," + // 'CANDIDATES' or 'CHAMPIONSHIP'
            "winner TEXT NOT NULL," +
            "runner_up TEXT," +
            "final_standings TEXT," + // JSON of all standings
            "start_date INTEGER NOT NULL," +
            "end_date INTEGER NOT NULL" +
            ")");
        
        // Individual game results
        db.execSQL("CREATE TABLE " + TABLE_GAME_RESULTS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "season INTEGER NOT NULL," +
            "tournament_round INTEGER NOT NULL," +
            "white_master TEXT NOT NULL," +
            "black_master TEXT NOT NULL," +
            "result REAL NOT NULL," + // 1.0 = white wins, 0.5 = draw, 0.0 = black wins
            "game_description TEXT," +
            "white_elo_before INTEGER," +
            "black_elo_before INTEGER," +
            "white_elo_after INTEGER," +
            "black_elo_after INTEGER," +
            "timestamp INTEGER NOT NULL" +
            ")");
        
        // Championship system state persistence
        db.execSQL("CREATE TABLE " + TABLE_CHAMPIONSHIP_STATE + " (" +
            "id INTEGER PRIMARY KEY," +
            "current_champion TEXT NOT NULL," +
            "current_phase TEXT NOT NULL," +
            "current_season INTEGER NOT NULL," +
            "current_round INTEGER NOT NULL," +
            "qualified_candidates TEXT," + // JSON array
            "challenger_name TEXT," +
            "last_updated INTEGER NOT NULL" +
            ")");
        
        initializeMasterRatings(db);
        Log.d(TAG, "✅ Tournament database created successfully");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "🔄 Upgrading tournament database from v" + oldVersion + " to v" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAMPIONSHIP_STATE);
        onCreate(db);
    }
    
    private void initializeMasterRatings(SQLiteDatabase db) {
        Log.d(TAG, "🎯 Initializing master ratings");
        
        // Initialize all 16 masters with their peak historical ELO as starting point
        String[][] masters = {
            {"Magnus Carlsen", "2882", "2882"},
            {"Garry Kasparov", "2851", "2851"},
            {"Vladimir Kramnik", "2817", "2817"},
            {"Viswanathan Anand", "2817", "2817"},
            {"Hikaru Nakamura", "2800", "2800"},
            {"Bobby Fischer", "2785", "2785"},
            {"Anatoly Karpov", "2780", "2780"},
            {"Gukesh Dommaraju", "2760", "2760"}, // Current World Champion (2024)
            {"Tigran Petrosian", "2745", "2745"},
            {"Aron Nimzowitsch", "2720", "2720"},
            {"José Raúl Capablanca", "2720", "2720"},
            {"Mikhail Botvinnik", "2720", "2720"},
            {"Mikhail Tal", "2705", "2705"},
            {"Alexander Alekhine", "2690", "2690"},
            {"Emanuel Lasker", "2660", "2660"},
            {"Paul Morphy", "2650", "2650"}
        };
        
        for (String[] master : masters) {
            ContentValues values = new ContentValues();
            values.put("name", master[0]);
            values.put("current_elo", Integer.parseInt(master[1]));
            values.put("peak_elo", Integer.parseInt(master[2]));
            values.put("last_updated", System.currentTimeMillis());
            
            db.insert(TABLE_MASTERS, null, values);
        }
        
        // Initialize championship state
        ContentValues stateValues = new ContentValues();
        stateValues.put("id", 1);
        stateValues.put("current_champion", "Magnus Carlsen");
        stateValues.put("current_phase", TournamentPhase.QUALIFICATION.name());
        stateValues.put("current_season", 1);
        stateValues.put("current_round", 0);
        stateValues.put("qualified_candidates", "[]");
        stateValues.put("challenger_name", "");
        stateValues.put("last_updated", System.currentTimeMillis());
        
        db.insert(TABLE_CHAMPIONSHIP_STATE, null, stateValues);
    }
    
    /**
     * Gets current tournament leaderboard (ELO rankings)
     */
    public List<MasterRanking> getCurrentLeaderboard() {
        List<MasterRanking> leaderboard = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MASTERS, null, null, null, null, null, "current_elo DESC");
        
        int rank = 1;
        while (cursor.moveToNext()) {
            MasterRanking ranking = new MasterRanking();
            ranking.rank = rank++;
            ranking.masterName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            ranking.currentElo = cursor.getInt(cursor.getColumnIndexOrThrow("current_elo"));
            ranking.peakElo = cursor.getInt(cursor.getColumnIndexOrThrow("peak_elo"));
            ranking.tournamentWins = cursor.getInt(cursor.getColumnIndexOrThrow("tournament_wins"));
            ranking.championshipWins = cursor.getInt(cursor.getColumnIndexOrThrow("championship_wins"));
            ranking.gamesPlayed = cursor.getInt(cursor.getColumnIndexOrThrow("games_played"));
            ranking.totalScore = cursor.getFloat(cursor.getColumnIndexOrThrow("total_score"));
            ranking.currentForm = cursor.getFloat(cursor.getColumnIndexOrThrow("current_form"));
            
            leaderboard.add(ranking);
        }
        
        cursor.close();
        return leaderboard;
    }
    
    /**
     * Qualifies 8 candidates for next tournament based on current ELO
     * Excludes current champion
     */
    public List<String> qualifyCandidates() {
        Log.d(TAG, "🎯 Qualifying candidates for Season " + championshipState.currentSeason);
        
        List<MasterRanking> leaderboard = getCurrentLeaderboard();
        List<String> candidates = new ArrayList<>();
        
        for (MasterRanking ranking : leaderboard) {
            if (!ranking.masterName.equals(championshipState.currentChampion) && 
                candidates.size() < 8) {
                candidates.add(ranking.masterName);
            }
        }
        
        Log.d(TAG, "✅ Qualified candidates: " + candidates);
        return candidates;
    }
    
    /**
     * Runs a complete Candidates Tournament
     */
    public TournamentResults runCandidatesTournament() {
        Log.d(TAG, "🏆 Starting Candidates Tournament Season " + championshipState.currentSeason);
        
        championshipState.currentPhase = TournamentPhase.CANDIDATES;
        championshipState.qualifiedCandidates = qualifyCandidates();
        saveChampionshipState();
        
        // Run tournament simulation
        simulationEngine.resetTournament();
        TournamentSimulationEngine.TournamentResults results = 
            simulationEngine.simulateFullTournament(championshipState.qualifiedCandidates);
        
        // Save all game results to database
        saveGameResults(results);
        
        // Update master ELOs based on performance
        updateMasterRatings(results);
        
        // Set challenger
        championshipState.challengerName = results.winner;
        championshipState.currentPhase = TournamentPhase.CHAMPIONSHIP;
        saveChampionshipState();
        
        // Save tournament to history
        saveTournamentHistory("CANDIDATES", results);
        
        Log.d(TAG, "🎊 Candidates Tournament complete! Winner: " + results.winner);
        
        return new TournamentResults(results, championshipState);
    }
    
    /**
     * Simulates multiple tournament seasons for database population
     */
    public void simulateMultipleSeasons(int numberOfSeasons) {
        Log.d(TAG, "⚡ Simulating " + numberOfSeasons + " tournament seasons");
        
        for (int season = 1; season <= numberOfSeasons; season++) {
            championshipState.currentSeason = season;
            
            // Candidates Tournament
            TournamentResults candidatesResult = runCandidatesTournament();
            
            // Championship Match (simplified - could be expanded to multi-game match)
            String challenger = candidatesResult.winner;
            String champion = championshipState.currentChampion;
            
            // Simulate championship match (best of 6 games)
            ChampionshipResult champResult = simulateChampionshipMatch(champion, challenger);
            
            // Update champion if challenger won
            if (champResult.newChampion.equals(challenger)) {
                championshipState.currentChampion = challenger;
                updateChampionshipWins(challenger);
                Log.d(TAG, "👑 NEW CHAMPION: " + challenger + " defeats " + champion + "!");
            } else {
                Log.d(TAG, "🛡️ " + champion + " retains championship against " + challenger);
            }
            
            // Season break
            championshipState.currentPhase = TournamentPhase.SEASON_BREAK;
            saveChampionshipState();
        }
        
        Log.d(TAG, "✅ Simulated " + numberOfSeasons + " seasons complete!");
    }
    
    private ChampionshipResult simulateChampionshipMatch(String champion, String challenger) {
        Log.d(TAG, "👑 Championship Match: " + champion + " vs " + challenger);
        
        // Best of 6 classical games + rapid playoff if tied
        float championScore = 0.0f;
        float challengerScore = 0.0f;
        
        for (int game = 1; game <= 6; game++) {
            String white = (game % 2 == 1) ? champion : challenger;
            String black = (game % 2 == 1) ? challenger : champion;
            
            TournamentSimulationEngine.GameResult result = 
                simulationEngine.simulateGame(white, black, game);
            
            if (white.equals(champion)) {
                championScore += result.score;
                challengerScore += (1.0f - result.score);
            } else {
                championScore += (1.0f - result.score);
                challengerScore += result.score;
            }
            
            Log.d(TAG, String.format("  Game %d: %s (Score: %.1f - %.1f)", 
                game, result.description, championScore, challengerScore));
            
            // Early finish if someone gets 3.5 points
            if (championScore >= 3.5f || challengerScore >= 3.5f) {
                break;
            }
        }
        
        String winner = (championScore > challengerScore) ? champion : challenger;
        return new ChampionshipResult(winner, championScore, challengerScore);
    }
    
    // Helper classes and methods
    
    public static class MasterRanking {
        public int rank;
        public String masterName;
        public int currentElo;
        public int peakElo;
        public int tournamentWins;
        public int championshipWins;
        public int gamesPlayed;
        public float totalScore;
        public float currentForm;
    }
    
    public static class TournamentResults {
        public TournamentSimulationEngine.TournamentResults simulationResults;
        public ChampionshipState championshipState;
        public String winner;
        public int season;
        
        public TournamentResults(TournamentSimulationEngine.TournamentResults simResults, 
                               ChampionshipState state) {
            this.simulationResults = simResults;
            this.championshipState = state;
            this.winner = simResults.winner;
            this.season = state.currentSeason;
        }
    }
    
    public static class ChampionshipResult {
        public String newChampion;
        public float championScore;
        public float challengerScore;
        
        public ChampionshipResult(String winner, float champScore, float challScore) {
            this.newChampion = winner;
            this.championScore = champScore;
            this.challengerScore = challScore;
        }
    }
    
    // Database persistence methods
    private void saveGameResults(TournamentSimulationEngine.TournamentResults results) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        for (TournamentSimulationEngine.GameResult game : results.allResults) {
            ContentValues values = new ContentValues();
            values.put("season", championshipState.currentSeason);
            values.put("tournament_round", 1); // Could track actual round
            values.put("white_master", game.whiteMaster);
            values.put("black_master", game.blackMaster);
            values.put("result", game.score);
            values.put("game_description", game.description);
            values.put("timestamp", System.currentTimeMillis());
            
            db.insert(TABLE_GAME_RESULTS, null, values);
        }
    }
    
    private void updateMasterRatings(TournamentSimulationEngine.TournamentResults results) {
        // Simple ELO update based on tournament performance
        SQLiteDatabase db = this.getWritableDatabase();
        
        for (Map.Entry<String, Float> entry : results.standings.entrySet()) {
            String masterName = entry.getKey();
            float tournamentScore = entry.getValue();
            
            // Calculate ELO change based on expected vs actual performance
            int eloChange = (int)((tournamentScore / 14.0f - 0.5f) * 50); // Max 25 point swing
            
            db.execSQL("UPDATE " + TABLE_MASTERS + 
                " SET current_elo = current_elo + ?, " +
                "games_played = games_played + 14, " +
                "total_score = total_score + ?, " +
                "last_updated = ? " +
                "WHERE name = ?",
                new Object[]{eloChange, tournamentScore, System.currentTimeMillis(), masterName});
        }
        
        // Update tournament wins for winner
        db.execSQL("UPDATE " + TABLE_MASTERS + 
            " SET tournament_wins = tournament_wins + 1 " +
            "WHERE name = ?", new Object[]{results.winner});
    }
    
    private void updateChampionshipWins(String champion) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_MASTERS + 
            " SET championship_wins = championship_wins + 1 " +
            "WHERE name = ?", new Object[]{champion});
    }
    
    private void saveTournamentHistory(String type, TournamentSimulationEngine.TournamentResults results) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put("season", championshipState.currentSeason);
        values.put("tournament_type", type);
        values.put("winner", results.winner);
        values.put("final_standings", results.standings.toString());
        values.put("start_date", System.currentTimeMillis());
        values.put("end_date", System.currentTimeMillis());
        
        db.insert(TABLE_TOURNAMENT_HISTORY, null, values);
    }
    
    private void loadChampionshipState() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAMPIONSHIP_STATE, null, "id = ?", 
            new String[]{"1"}, null, null, null);
        
        championshipState = new ChampionshipState();
        
        if (cursor.moveToFirst()) {
            championshipState.currentChampion = cursor.getString(cursor.getColumnIndexOrThrow("current_champion"));
            championshipState.currentPhase = TournamentPhase.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow("current_phase")));
            championshipState.currentSeason = cursor.getInt(cursor.getColumnIndexOrThrow("current_season"));
            championshipState.currentRound = cursor.getInt(cursor.getColumnIndexOrThrow("current_round"));
            championshipState.challengerName = cursor.getString(cursor.getColumnIndexOrThrow("challenger_name"));
            championshipState.lastUpdateTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow("last_updated"));
        }
        
        cursor.close();
    }
    
    private void saveChampionshipState() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put("current_champion", championshipState.currentChampion);
        values.put("current_phase", championshipState.currentPhase.name());
        values.put("current_season", championshipState.currentSeason);
        values.put("current_round", championshipState.currentRound);
        values.put("qualified_candidates", championshipState.qualifiedCandidates.toString());
        values.put("challenger_name", championshipState.challengerName);
        values.put("last_updated", System.currentTimeMillis());
        
        db.update(TABLE_CHAMPIONSHIP_STATE, values, "id = ?", new String[]{"1"});
    }
    
    public ChampionshipState getChampionshipState() {
        return championshipState;
    }
}