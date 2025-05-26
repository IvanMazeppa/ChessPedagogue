package com.example.chesspedagogue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ENHANCED GameDatabaseHelper - Now with lightning-fast FEN lookups!
 * Handles both saved games AND instant personality engine queries.
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "GameDatabaseHelper";
    private static final String DATABASE_NAME = "chess_games.db";
    private static final int DATABASE_VERSION = 2; // Incremented for new features!

    // EXISTING: Saved games table
    private static final String TABLE_GAMES = "saved_games";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PLAYER_COLOR = "player_color";
    private static final String COLUMN_MOVES = "moves";
    private static final String COLUMN_FINAL_FEN = "final_fen";
    private static final String COLUMN_DESCRIPTION = "description";

    // NEW: Master positions table for instant FEN lookups!
    private static final String TABLE_MASTER_POSITIONS = "master_positions";
    private static final String COLUMN_MASTER_ID = "master_id";
    private static final String COLUMN_MASTER_NAME = "master_name";
    private static final String COLUMN_FEN = "fen";
    private static final String COLUMN_OPPONENT = "opponent";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_TOURNAMENT = "tournament";
    private static final String COLUMN_OPENING = "opening";
    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_SIGNIFICANCE = "significance";
    private static final String COLUMN_MOVE_NUMBER = "move_number";
    private static final String COLUMN_ANNOTATION = "annotation";
    private static final String COLUMN_TAGS = "tags";

    // EXISTING: Create saved games table
    private static final String CREATE_GAMES_TABLE =
            "CREATE TABLE " + TABLE_GAMES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DATE + " INTEGER,"
                    + COLUMN_PLAYER_COLOR + " TEXT,"
                    + COLUMN_MOVES + " TEXT,"
                    + COLUMN_FINAL_FEN + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT"
                    + ")";

    // NEW: Create master positions table for personality engine
    private static final String CREATE_MASTER_POSITIONS_TABLE =
            "CREATE TABLE " + TABLE_MASTER_POSITIONS + "("
                    + COLUMN_MASTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER_NAME + " TEXT NOT NULL,"
                    + COLUMN_FEN + " TEXT NOT NULL,"
                    + COLUMN_OPPONENT + " TEXT,"
                    + COLUMN_YEAR + " TEXT,"
                    + COLUMN_TOURNAMENT + " TEXT,"
                    + COLUMN_OPENING + " TEXT,"
                    + COLUMN_RESULT + " TEXT,"
                    + COLUMN_SIGNIFICANCE + " TEXT,"
                    + COLUMN_MOVE_NUMBER + " INTEGER,"
                    + COLUMN_ANNOTATION + " TEXT,"
                    + COLUMN_TAGS + " TEXT"
                    + ")";

    // NEW: Lightning-fast indexes for instant lookups!
    private static final String CREATE_FEN_INDEX =
            "CREATE INDEX idx_fen ON " + TABLE_MASTER_POSITIONS + "(" + COLUMN_FEN + ")";

    private static final String CREATE_MASTER_INDEX =
            "CREATE INDEX idx_master ON " + TABLE_MASTER_POSITIONS + "(" + COLUMN_MASTER_NAME + ")";

    private final Context context;

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create existing games table
        db.execSQL(CREATE_GAMES_TABLE);

        // Create new master positions table
        db.execSQL(CREATE_MASTER_POSITIONS_TABLE);

        // Create lightning-fast indexes
        db.execSQL(CREATE_FEN_INDEX);
        db.execSQL(CREATE_MASTER_INDEX);

        Log.d(TAG, "✅ Database created with both game storage AND lightning-fast FEN lookups!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add the new master positions functionality
            db.execSQL(CREATE_MASTER_POSITIONS_TABLE);
            db.execSQL(CREATE_FEN_INDEX);
            db.execSQL(CREATE_MASTER_INDEX);
            Log.d(TAG, "🚀 Database upgraded with personality engine support!");
        }
    }

    // ========== EXISTING SAVED GAMES FUNCTIONALITY ==========
    // (All your existing methods remain exactly the same!)

    public long saveGame(String playerColor, List<String> moves, String finalFen, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, System.currentTimeMillis());
        values.put(COLUMN_PLAYER_COLOR, playerColor);
        values.put(COLUMN_MOVES, convertMovesToString(moves));
        values.put(COLUMN_FINAL_FEN, finalFen);
        values.put(COLUMN_DESCRIPTION, description);

        long id = db.insert(TABLE_GAMES, null, values);
        db.close();

        return id;
    }

    private String convertMovesToString(List<String> moves) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); i++) {
            sb.append(moves.get(i));
            if (i < moves.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private List<String> convertStringToMoves(String movesString) {
        List<String> movesList = new ArrayList<>();
        if (movesString != null && !movesString.isEmpty()) {
            String[] moves = movesString.split(",");
            Collections.addAll(movesList, moves);
        }
        return movesList;
    }

    public List<SavedGame> getAllGames() {
        List<SavedGame> games = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_GAMES + " ORDER BY " + COLUMN_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    SavedGame game = new SavedGame();

                    int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                    int dateColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE);
                    int colorColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PLAYER_COLOR);
                    int movesColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_MOVES);
                    int fenColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_FINAL_FEN);
                    int descColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);

                    game.setId(cursor.getLong(idColumnIndex));
                    game.setDate(cursor.getLong(dateColumnIndex));
                    game.setPlayerColor(cursor.getString(colorColumnIndex));
                    game.setMoves(convertStringToMoves(cursor.getString(movesColumnIndex)));
                    game.setFinalFen(cursor.getString(fenColumnIndex));
                    game.setDescription(cursor.getString(descColumnIndex));

                    games.add(game);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Column not found in database", e);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return games;
    }

    public SavedGame getGame(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_GAMES,
                new String[] { COLUMN_ID, COLUMN_DATE, COLUMN_PLAYER_COLOR, COLUMN_MOVES, COLUMN_FINAL_FEN, COLUMN_DESCRIPTION },
                COLUMN_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null
        );

        SavedGame game = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                game = new SavedGame();

                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int dateColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE);
                int colorColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PLAYER_COLOR);
                int movesColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_MOVES);
                int fenColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_FINAL_FEN);
                int descColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);

                game.setId(cursor.getLong(idColumnIndex));
                game.setDate(cursor.getLong(dateColumnIndex));
                game.setPlayerColor(cursor.getString(colorColumnIndex));
                game.setMoves(convertStringToMoves(cursor.getString(movesColumnIndex)));
                game.setFinalFen(cursor.getString(fenColumnIndex));
                game.setDescription(cursor.getString(descColumnIndex));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Column not found in database for game ID: " + id, e);
                game = null;
            }

            cursor.close();
        }

        db.close();
        return game;
    }

    public void deleteGame(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAMES, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    // ========== NEW: LIGHTNING-FAST PERSONALITY ENGINE SUPPORT! ==========

    /**
     * 🚀 INSTANT FEN LOOKUP - The heart of the speed improvement!
     * This replaces slow API calls with blazing-fast local queries.
     */
    public List<HistoricalPosition> findSimilarPositions(String fen, String masterName, int maxResults) {
        List<HistoricalPosition> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Log.d(TAG, "⚡ Lightning-fast FEN lookup for " + masterName + "...");

            // First try exact FEN match for the specific master
            String exactQuery = "SELECT * FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_FEN + " = ? AND " + COLUMN_MASTER_NAME + " = ?" +
                    " LIMIT " + maxResults;

            Cursor cursor = db.rawQuery(exactQuery, new String[]{fen, masterName});

            if (cursor.getCount() > 0) {
                Log.d(TAG, "🎯 EXACT FEN MATCH FOUND for " + masterName + "!");
                results.addAll(cursorToHistoricalPositions(cursor));
                cursor.close();
                return results;
            }
            cursor.close();

            // If no exact match, try board structure similarity (first part of FEN)
            String boardPart = fen.split(" ")[0]; // Get just the piece placement
            String similarQuery = "SELECT * FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_FEN + " LIKE ? AND " + COLUMN_MASTER_NAME + " = ?" +
                    " LIMIT " + maxResults;

            cursor = db.rawQuery(similarQuery, new String[]{boardPart + "%", masterName});

            if (cursor.getCount() > 0) {
                Log.d(TAG, "🔍 Similar position found for " + masterName);
                results.addAll(cursorToHistoricalPositions(cursor));
                cursor.close();
                return results;
            }
            cursor.close();

            // Fallback: get any positions from this master with similar tactical themes
            String fallbackQuery = "SELECT * FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_MASTER_NAME + " = ?" +
                    " ORDER BY RANDOM() LIMIT " + Math.min(maxResults, 3);

            cursor = db.rawQuery(fallbackQuery, new String[]{masterName});
            results.addAll(cursorToHistoricalPositions(cursor));
            cursor.close();

            Log.d(TAG, "📚 Found " + results.size() + " reference positions for " + masterName);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in lightning-fast FEN lookup", e);
        }

        return results;
    }

    /**
     * Get the best historical move for a position (if available)
     */
    public String getHistoricalMove(String fen, String masterName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String move = null;

        try {
            String query = "SELECT " + COLUMN_ANNOTATION + " FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_FEN + " = ? AND " + COLUMN_MASTER_NAME + " = ?" +
                    " LIMIT 1";

            Cursor cursor = db.rawQuery(query, new String[]{fen, masterName});

            if (cursor.moveToFirst()) {
                String annotation = cursor.getString(0);
                move = extractMoveFromAnnotation(annotation);
                Log.d(TAG, "🎯 Historical move found: " + move);
            }

            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error getting historical move", e);
        }

        return move;
    }

    /**
     * Get tactical themes for a position - used by PersonalityEngine
     */
    public List<String> getTacticalThemes(String fen, String masterName) {
        List<String> themes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT " + COLUMN_TAGS + " FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_FEN + " = ? AND " + COLUMN_MASTER_NAME + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{fen, masterName});

            if (cursor.moveToFirst()) {
                String tagsJson = cursor.getString(0);
                themes = parseTagsFromJson(tagsJson);
            }

            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error getting tactical themes", e);
        }

        return themes;
    }

    /**
     * 🚀 Import chess master data from your chunked_fen_db_creator script!
     * This is how you'll load all the FEN data into your local database.
     */
    public boolean importMasterPositions(String jsonData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Log.d(TAG, "🚀 Starting master positions import...");

            JSONArray chunks = new JSONArray(jsonData);

            db.beginTransaction();

            for (int i = 0; i < chunks.length(); i++) {
                JSONObject chunk = chunks.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(COLUMN_MASTER_NAME, chunk.optString("player_name", ""));
                values.put(COLUMN_FEN, chunk.optString("fen", ""));
                values.put(COLUMN_OPPONENT, chunk.optString("opponent", ""));
                values.put(COLUMN_YEAR, chunk.optString("year", ""));
                values.put(COLUMN_TOURNAMENT, chunk.optString("tournament", ""));
                values.put(COLUMN_OPENING, chunk.optString("opening", ""));
                values.put(COLUMN_RESULT, chunk.optString("result", ""));
                values.put(COLUMN_SIGNIFICANCE, chunk.optString("significance", ""));
                values.put(COLUMN_MOVE_NUMBER, chunk.optInt("move_number", 0));
                values.put(COLUMN_ANNOTATION, chunk.optString("annotation", ""));
                values.put(COLUMN_TAGS, chunk.optJSONArray("tags").toString());

                db.insert(TABLE_MASTER_POSITIONS, null, values);
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "✅ Successfully imported " + chunks.length() + " master positions!");

            return true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error importing master positions", e);
            return false;
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    /**
     * Import from assets folder (put your JSON file in assets/)
     */
    public boolean importMasterPositionsFromAssets(String filename) {
        try {
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            reader.close();
            inputStream.close();

            return importMasterPositions(jsonBuilder.toString());

        } catch (IOException e) {
            Log.e(TAG, "Error reading from assets: " + filename, e);
            return false;
        }
    }

    /**
     * Check if we have master data (used by PersonalityEngine)
     */
    public boolean hasMasterData(String masterName) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_MASTER_POSITIONS + " WHERE " + COLUMN_MASTER_NAME + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{masterName});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                Log.d(TAG, "📊 " + masterName + " has " + count + " positions in database");
                return count > 0;
            }

            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error checking master data", e);
        }

        return false;
    }

    /**
     * Get cool database statistics for debugging
     */
    public Map<String, Integer> getDatabaseStats() {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            // Total positions
            Cursor totalCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MASTER_POSITIONS, null);
            if (totalCursor.moveToFirst()) {
                stats.put("total_positions", totalCursor.getInt(0));
            }
            totalCursor.close();

            // Positions per master
            Cursor masterCursor = db.rawQuery(
                    "SELECT " + COLUMN_MASTER_NAME + ", COUNT(*) FROM " + TABLE_MASTER_POSITIONS +
                            " GROUP BY " + COLUMN_MASTER_NAME, null);

            while (masterCursor.moveToNext()) {
                String master = masterCursor.getString(0);
                int count = masterCursor.getInt(1);
                stats.put(master, count);
            }
            masterCursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error getting database stats", e);
        }

        return stats;
    }

    // ========== HELPER METHODS ==========

    private List<HistoricalPosition> cursorToHistoricalPositions(Cursor cursor) {
        List<HistoricalPosition> positions = new ArrayList<>();

        try {
            int masterNameIndex = cursor.getColumnIndex(COLUMN_MASTER_NAME);
            int fenIndex = cursor.getColumnIndex(COLUMN_FEN);
            int opponentIndex = cursor.getColumnIndex(COLUMN_OPPONENT);
            int yearIndex = cursor.getColumnIndex(COLUMN_YEAR);
            int tournamentIndex = cursor.getColumnIndex(COLUMN_TOURNAMENT);
            int annotationIndex = cursor.getColumnIndex(COLUMN_ANNOTATION);
            int tagsIndex = cursor.getColumnIndex(COLUMN_TAGS);
            int moveNumberIndex = cursor.getColumnIndex(COLUMN_MOVE_NUMBER);

            while (cursor.moveToNext()) {
                HistoricalPosition position = new HistoricalPosition();
                position.masterName = cursor.getString(masterNameIndex);
                position.fen = cursor.getString(fenIndex);
                position.opponent = cursor.getString(opponentIndex);
                position.year = cursor.getString(yearIndex);
                position.tournament = cursor.getString(tournamentIndex);
                position.annotation = cursor.getString(annotationIndex);
                position.moveNumber = cursor.getInt(moveNumberIndex);
                position.tags = parseTagsFromJson(cursor.getString(tagsIndex));

                positions.add(position);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to historical positions", e);
        }

        return positions;
    }

    private List<String> parseTagsFromJson(String tagsJson) {
        List<String> tags = new ArrayList<>();

        try {
            if (tagsJson != null && !tagsJson.isEmpty()) {
                JSONArray jsonArray = new JSONArray(tagsJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    tags.add(jsonArray.getString(i));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error parsing tags JSON: " + tagsJson);
        }

        return tags;
    }

    private String extractMoveFromAnnotation(String annotation) {
        if (annotation == null) return null;

        String[] words = annotation.split("\\s+");
        for (String word : words) {
            // Look for UCI format moves (e2e4, g1f3, etc.)
            if (word.matches("[a-h][1-8][a-h][1-8]")) {
                return word;
            }
            // Look for algebraic notation
            if (word.matches("[KQRBN]?[a-h]?[1-8]?x?[a-h][1-8][+#]?")) {
                return word;
            }
        }

        return null;
    }

    // ========== EXISTING SAVED GAME CLASS ==========

    public static class SavedGame {
        private long id;
        private long date;
        private String playerColor;
        private List<String> moves;
        private String finalFen;
        private String description;

        // All your existing getters and setters stay exactly the same!
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public long getDate() { return date; }
        public void setDate(long date) { this.date = date; }

        public String getPlayerColor() { return playerColor; }
        public void setPlayerColor(String playerColor) { this.playerColor = playerColor; }

        public List<String> getMoves() { return moves; }
        public void setMoves(List<String> moves) { this.moves = moves; }

        public String getFinalFen() { return finalFen; }
        public void setFinalFen(String finalFen) { this.finalFen = finalFen; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getFormattedDate() {
            return new Date(date).toString();
        }
    }

    // ========== NEW: HISTORICAL POSITION CLASS ==========

    /**
     * Data class for historical chess positions from masters
     */
    public static class HistoricalPosition {
        public String masterName;
        public String fen;
        public String opponent;
        public String year;
        public String tournament;
        public String annotation;
        public int moveNumber;
        public List<String> tags;

        public HistoricalPosition() {
            this.tags = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("%s vs %s (%s) - %s", masterName, opponent, year, annotation);
        }

        /**
         * Convert to the format expected by PersonalityEngine
         */
        public PersonalityEngine.VectorSearchResult toVectorSearchResult() {
            return new PersonalityEngine.VectorSearchResult(
                    this.annotation,
                    this.toString(),
                    1.0f // High similarity since it's a local match
            );
        }
    }
}