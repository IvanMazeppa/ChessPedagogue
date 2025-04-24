// Create this new file: GameDatabaseHelper.java
package com.example.chesspedagogue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chess_games.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_GAMES = "saved_games";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PLAYER_COLOR = "player_color";
    private static final String COLUMN_MOVES = "moves";
    private static final String COLUMN_FINAL_FEN = "final_fen";
    private static final String COLUMN_DESCRIPTION = "description";

    // Create table SQL query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_GAMES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DATE + " INTEGER,"
                    + COLUMN_PLAYER_COLOR + " TEXT,"
                    + COLUMN_MOVES + " TEXT,"
                    + COLUMN_FINAL_FEN + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT"
                    + ")";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For future database schema upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    // Add these methods to GameDatabaseHelper.java

    // Save a game to the database
    public long saveGame(String playerColor, List<String> moves, String finalFen, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, System.currentTimeMillis());
        values.put(COLUMN_PLAYER_COLOR, playerColor);
        values.put(COLUMN_MOVES, convertMovesToString(moves));
        values.put(COLUMN_FINAL_FEN, finalFen);
        values.put(COLUMN_DESCRIPTION, description);

        // Insert row
        long id = db.insert(TABLE_GAMES, null, values);
        db.close();

        return id;
    }

    // Convert moves list to a string for storage
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

    // Convert string back to moves list
    private List<String> convertStringToMoves(String movesString) {
        List<String> movesList = new ArrayList<>();
        if (movesString != null && !movesString.isEmpty()) {
            String[] moves = movesString.split(",");
            for (String move : moves) {
                movesList.add(move);
            }
        }
        return movesList;
    }

    // Game class to hold saved game data
    public static class SavedGame {
        private long id;
        private long date;
        private String playerColor;
        private List<String> moves;
        private String finalFen;
        private String description;

        // Getters and setters
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

    // Get all saved games
    public List<SavedGame> getAllGames() {
        List<SavedGame> games = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_GAMES + " ORDER BY " + COLUMN_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SavedGame game = new SavedGame();
                game.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                game.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
                game.setPlayerColor(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_COLOR)));
                game.setMoves(convertStringToMoves(cursor.getString(cursor.getColumnIndex(COLUMN_MOVES))));
                game.setFinalFen(cursor.getString(cursor.getColumnIndex(COLUMN_FINAL_FEN)));
                game.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));

                games.add(game);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return games;
    }

    // Get a specific game by ID
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
            game = new SavedGame();
            game.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            game.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
            game.setPlayerColor(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_COLOR)));
            game.setMoves(convertStringToMoves(cursor.getString(cursor.getColumnIndex(COLUMN_MOVES))));
            game.setFinalFen(cursor.getString(cursor.getColumnIndex(COLUMN_FINAL_FEN)));
            game.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));

            cursor.close();
        }

        db.close();
        return game;
    }

    // Delete a game
    public void deleteGame(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAMES, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }
}