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
import android.util.LruCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ADVANCED GameDatabaseHelper - Now with Emergent Behavior Tracking!
 * Handles saved games, personality engine queries, AND relationship evolution.
 * Features:
 * - Lightning-fast FEN lookups for master positions
 * - Persistent emotional memory and relationship tracking
 * - Topic evolution and conversation innovation
 * - Emergent behavior detection and logging
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "GameDatabaseHelper";
    private static final String DATABASE_NAME = "chess_games.db";
    private static final int DATABASE_VERSION = 5; // Incremented for emotional strategy learning!

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

    // =========================== EMERGENT BEHAVIOR COLUMNS ===========================
    
    // Table: master_relationships - Track evolving relationships between masters
    private static final String TABLE_RELATIONSHIPS = "master_relationships";
    private static final String COLUMN_MASTER1 = "master1";
    private static final String COLUMN_MASTER2 = "master2";
    private static final String COLUMN_RESPECT_LEVEL = "respect_level";
    private static final String COLUMN_RIVALRY_INTENSITY = "rivalry_intensity";
    private static final String COLUMN_FRIENDSHIP_BOND = "friendship_bond";
    private static final String COLUMN_COMMUNICATION_STYLE = "communication_style";
    private static final String COLUMN_TOTAL_INTERACTIONS = "total_interactions";
    private static final String COLUMN_TOTAL_GAMES = "total_games";
    private static final String COLUMN_LAST_MAJOR_EVENT = "last_major_event";
    private static final String COLUMN_LAST_UPDATED = "last_updated";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Table: topic_memory - Track topic usage and fatigue
    private static final String TABLE_TOPIC_MEMORY = "topic_memory";
    private static final String COLUMN_TOPIC = "topic";
    private static final String COLUMN_DISCUSSION_COUNT = "discussion_count";
    private static final String COLUMN_TOTAL_EMOTIONAL_INTENSITY = "total_emotional_intensity";
    private static final String COLUMN_AVERAGE_EMOTIONAL_INTENSITY = "average_emotional_intensity";
    private static final String COLUMN_LAST_EMOTION_MASTER1 = "last_emotion_master1";
    private static final String COLUMN_LAST_EMOTION_MASTER2 = "last_emotion_master2";
    private static final String COLUMN_TOPIC_FATIGUE_LEVEL = "topic_fatigue_level";
    private static final String COLUMN_EVOLUTION_PATH = "evolution_path";
    private static final String COLUMN_BREAKTHROUGH_MOMENTS = "breakthrough_moments";
    private static final String COLUMN_CONFLICT_INCIDENTS = "conflict_incidents";
    private static final String COLUMN_LAST_DISCUSSED = "last_discussed";

    // Table: emotional_reactions - Detailed emotional history
    private static final String TABLE_EMOTIONAL_REACTIONS = "emotional_reactions";
    private static final String COLUMN_MASTER = "master";
    private static final String COLUMN_EMOTION_OPPONENT = "opponent";
    private static final String COLUMN_EMOTION = "emotion";
    private static final String COLUMN_INTENSITY = "intensity";
    private static final String COLUMN_MOMENTUM = "momentum";
    private static final String COLUMN_GAME_CONTEXT = "game_context";
    private static final String COLUMN_POSITION_EVALUATION = "position_evaluation";
    private static final String COLUMN_CONVERSATION_SNIPPET = "conversation_snippet";
    private static final String COLUMN_RELATIONSHIP_IMPACT = "relationship_impact";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Table: conversation_evolution - Track topic innovation
    private static final String TABLE_CONVERSATION_EVOLUTION = "conversation_evolution";
    private static final String COLUMN_ORIGINAL_TOPIC = "original_topic";
    private static final String COLUMN_EVOLVED_TOPIC = "evolved_topic";
    private static final String COLUMN_EVOLUTION_TYPE = "evolution_type";
    private static final String COLUMN_CATALYST_EMOTION = "catalyst_emotion";
    private static final String COLUMN_BREAKTHROUGH_CONTENT = "breakthrough_content";
    private static final String COLUMN_IMPACT_SCORE = "impact_score";
    private static final String COLUMN_SUBSEQUENT_USAGE = "subsequent_usage";

    // Table: emergent_events - Log breakthrough moments
    private static final String TABLE_EMERGENT_EVENTS = "emergent_events";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_EVENT_DESCRIPTION = "description";
    private static final String COLUMN_EMOTIONAL_CONTEXT = "emotional_context";
    private static final String COLUMN_CONVERSATION_CONTENT = "conversation_content";
    private static final String COLUMN_IMPACT_LEVEL = "impact_level";
    private static final String COLUMN_FOLLOW_UP_EFFECTS = "follow_up_effects";

    // =========================== TABLE CREATION STATEMENTS ===========================

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

    // NEW: Create relationships table
    private static final String CREATE_RELATIONSHIPS_TABLE =
            "CREATE TABLE " + TABLE_RELATIONSHIPS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER1 + " TEXT NOT NULL,"
                    + COLUMN_MASTER2 + " TEXT NOT NULL,"
                    + COLUMN_RESPECT_LEVEL + " REAL DEFAULT 0.5,"
                    + COLUMN_RIVALRY_INTENSITY + " REAL DEFAULT 0.0,"
                    + COLUMN_FRIENDSHIP_BOND + " REAL DEFAULT 0.0,"
                    + COLUMN_COMMUNICATION_STYLE + " TEXT DEFAULT 'formal',"
                    + COLUMN_TOTAL_INTERACTIONS + " INTEGER DEFAULT 0,"
                    + COLUMN_TOTAL_GAMES + " INTEGER DEFAULT 0,"
                    + COLUMN_LAST_MAJOR_EVENT + " TEXT,"
                    + COLUMN_LAST_UPDATED + " INTEGER,"
                    + COLUMN_CREATED_AT + " INTEGER,"
                    + "UNIQUE(" + COLUMN_MASTER1 + ", " + COLUMN_MASTER2 + ")"
                    + ")";

    // NEW: Create topic memory table
    private static final String CREATE_TOPIC_MEMORY_TABLE =
            "CREATE TABLE " + TABLE_TOPIC_MEMORY + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER1 + " TEXT NOT NULL,"
                    + COLUMN_MASTER2 + " TEXT NOT NULL,"
                    + COLUMN_TOPIC + " TEXT NOT NULL,"
                    + COLUMN_DISCUSSION_COUNT + " INTEGER DEFAULT 1,"
                    + COLUMN_TOTAL_EMOTIONAL_INTENSITY + " REAL DEFAULT 0.0,"
                    + COLUMN_AVERAGE_EMOTIONAL_INTENSITY + " REAL DEFAULT 0.0,"
                    + COLUMN_LAST_EMOTION_MASTER1 + " TEXT,"
                    + COLUMN_LAST_EMOTION_MASTER2 + " TEXT,"
                    + COLUMN_TOPIC_FATIGUE_LEVEL + " REAL DEFAULT 0.0,"
                    + COLUMN_EVOLUTION_PATH + " TEXT,"
                    + COLUMN_BREAKTHROUGH_MOMENTS + " INTEGER DEFAULT 0,"
                    + COLUMN_CONFLICT_INCIDENTS + " INTEGER DEFAULT 0,"
                    + COLUMN_LAST_DISCUSSED + " INTEGER,"
                    + COLUMN_CREATED_AT + " INTEGER"
                    + ")";

    // NEW: Create emotional reactions table
    private static final String CREATE_EMOTIONAL_REACTIONS_TABLE =
            "CREATE TABLE " + TABLE_EMOTIONAL_REACTIONS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER + " TEXT NOT NULL,"
                    + COLUMN_EMOTION_OPPONENT + " TEXT NOT NULL,"
                    + COLUMN_TOPIC + " TEXT NOT NULL,"
                    + COLUMN_EMOTION + " TEXT NOT NULL,"
                    + COLUMN_INTENSITY + " REAL NOT NULL,"
                    + COLUMN_MOMENTUM + " REAL DEFAULT 0.0,"
                    + COLUMN_GAME_CONTEXT + " TEXT,"
                    + COLUMN_POSITION_EVALUATION + " REAL,"
                    + COLUMN_CONVERSATION_SNIPPET + " TEXT,"
                    + COLUMN_RELATIONSHIP_IMPACT + " REAL DEFAULT 0.0,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL"
                    + ")";

    // NEW: Create conversation evolution table
    private static final String CREATE_CONVERSATION_EVOLUTION_TABLE =
            "CREATE TABLE " + TABLE_CONVERSATION_EVOLUTION + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER1 + " TEXT NOT NULL,"
                    + COLUMN_MASTER2 + " TEXT NOT NULL,"
                    + COLUMN_ORIGINAL_TOPIC + " TEXT NOT NULL,"
                    + COLUMN_EVOLVED_TOPIC + " TEXT NOT NULL,"
                    + COLUMN_EVOLUTION_TYPE + " TEXT NOT NULL,"
                    + COLUMN_CATALYST_EMOTION + " TEXT,"
                    + COLUMN_BREAKTHROUGH_CONTENT + " TEXT,"
                    + COLUMN_IMPACT_SCORE + " REAL DEFAULT 0.0,"
                    + COLUMN_SUBSEQUENT_USAGE + " INTEGER DEFAULT 0,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL"
                    + ")";

    // NEW: Create emergent events table
    private static final String CREATE_EMERGENT_EVENTS_TABLE =
            "CREATE TABLE " + TABLE_EMERGENT_EVENTS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EVENT_TYPE + " TEXT NOT NULL,"
                    + COLUMN_MASTER1 + " TEXT NOT NULL,"
                    + COLUMN_MASTER2 + " TEXT NOT NULL,"
                    + COLUMN_EVENT_DESCRIPTION + " TEXT NOT NULL,"
                    + COLUMN_EMOTIONAL_CONTEXT + " TEXT,"
                    + COLUMN_CONVERSATION_CONTENT + " TEXT,"
                    + COLUMN_IMPACT_LEVEL + " REAL DEFAULT 0.0,"
                    + COLUMN_FOLLOW_UP_EFFECTS + " TEXT,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL"
                    + ")";

    // NEW: Expression patterns table for anti-repetition system
    private static final String TABLE_EXPRESSION_PATTERNS = "expression_patterns";
    private static final String COLUMN_CONCEPT = "concept";
    private static final String COLUMN_USED_PHRASES = "used_phrases";
    private static final String COLUMN_USED_ANGLES = "used_angles";
    private static final String COLUMN_USED_TONES = "used_tones";
    private static final String COLUMN_TIMES_USED = "times_used";
    private static final String COLUMN_DIVERSITY_SCORE = "diversity_score";
    private static final String COLUMN_LAST_USED = "last_used";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    private static final String CREATE_EXPRESSION_PATTERNS_TABLE =
            "CREATE TABLE " + TABLE_EXPRESSION_PATTERNS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER_NAME + " TEXT NOT NULL,"
                    + COLUMN_CONCEPT + " TEXT NOT NULL,"
                    + COLUMN_USED_PHRASES + " TEXT,"  // JSON array of used phrases
                    + COLUMN_USED_ANGLES + " TEXT,"   // JSON array of argumentative angles
                    + COLUMN_USED_TONES + " TEXT,"    // JSON array of emotional tones
                    + COLUMN_TIMES_USED + " INTEGER DEFAULT 1,"
                    + COLUMN_DIVERSITY_SCORE + " REAL DEFAULT 1.0,"
                    + COLUMN_LAST_USED + " INTEGER,"
                    + COLUMN_CREATED_AT + " INTEGER,"
                    + COLUMN_UPDATED_AT + " INTEGER,"
                    + "UNIQUE(" + COLUMN_MASTER_NAME + ", " + COLUMN_CONCEPT + ")"
                    + ")";

    // =========================== INDEXES FOR PERFORMANCE ===========================

    // EXISTING: Lightning-fast indexes for instant lookups!
    private static final String CREATE_FEN_INDEX =
            "CREATE INDEX idx_fen ON " + TABLE_MASTER_POSITIONS + "(" + COLUMN_FEN + ")";

    private static final String CREATE_MASTER_INDEX =
            "CREATE INDEX idx_master ON " + TABLE_MASTER_POSITIONS + "(" + COLUMN_MASTER_NAME + ")";
    
    // NEW: Emergent behavior indexes for fast relationship and topic queries
    private static final String CREATE_RELATIONSHIPS_INDEX =
            "CREATE INDEX idx_relationships ON " + TABLE_RELATIONSHIPS + "(" + COLUMN_MASTER1 + ", " + COLUMN_MASTER2 + ")";
    
    private static final String CREATE_TOPIC_MEMORY_INDEX =
            "CREATE INDEX idx_topic_memory ON " + TABLE_TOPIC_MEMORY + "(" + COLUMN_MASTER1 + ", " + COLUMN_MASTER2 + ", " + COLUMN_TOPIC + ")";
    
    private static final String CREATE_EMOTIONAL_REACTIONS_INDEX =
            "CREATE INDEX idx_emotional_reactions ON " + TABLE_EMOTIONAL_REACTIONS + "(" + COLUMN_MASTER + ", " + COLUMN_TOPIC + ", " + COLUMN_TIMESTAMP + ")";
    
    private static final String CREATE_EMERGENT_EVENTS_INDEX =
            "CREATE INDEX idx_emergent_events ON " + TABLE_EMERGENT_EVENTS + "(" + COLUMN_EVENT_TYPE + ", " + COLUMN_TIMESTAMP + ")";
    
    private static final String CREATE_EXPRESSION_PATTERNS_INDEX =
            "CREATE INDEX idx_expression_patterns ON " + TABLE_EXPRESSION_PATTERNS + "(" + COLUMN_MASTER_NAME + ", " + COLUMN_CONCEPT + ")";

    // Table: emotional_strategy_learning - Track adaptive learning for optimal emotional strategies
    private static final String TABLE_EMOTIONAL_STRATEGY_LEARNING = "emotional_strategy_learning";
    private static final String COLUMN_APPROACH_TYPE = "approach_type";
    private static final String COLUMN_SUCCESS_RATE = "success_rate";
    private static final String COLUMN_ATTEMPTS = "attempts";
    private static final String COLUMN_EXPLORATION_RATE = "exploration_rate";
    private static final String COLUMN_STRATEGY_DATA = "strategy_data";

    private static final String CREATE_EMOTIONAL_STRATEGY_LEARNING_TABLE =
            "CREATE TABLE " + TABLE_EMOTIONAL_STRATEGY_LEARNING + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MASTER_NAME + " TEXT NOT NULL,"
                    + "opponent_name TEXT NOT NULL,"
                    + COLUMN_APPROACH_TYPE + " TEXT NOT NULL,"
                    + COLUMN_SUCCESS_RATE + " REAL DEFAULT 0.5,"
                    + COLUMN_ATTEMPTS + " INTEGER DEFAULT 0,"
                    + COLUMN_EXPLORATION_RATE + " REAL DEFAULT 0.4,"
                    + COLUMN_STRATEGY_DATA + " TEXT,"  // JSON data for full strategy profile
                    + COLUMN_LAST_UPDATED + " INTEGER,"
                    + COLUMN_CREATED_AT + " INTEGER,"
                    + "UNIQUE(" + COLUMN_MASTER_NAME + ", opponent_name, " + COLUMN_APPROACH_TYPE + ")"
                    + ")";

    private static final String CREATE_EMOTIONAL_STRATEGY_LEARNING_INDEX =
            "CREATE INDEX idx_emotional_strategy_learning ON " + TABLE_EMOTIONAL_STRATEGY_LEARNING + "(" + COLUMN_MASTER_NAME + ", opponent_name, " + COLUMN_LAST_UPDATED + ")";

    private final Context context;
    
    // Performance optimization: LRU cache for position lookups
    // Caches the last 100 position queries to avoid database hits
    private final LruCache<String, List<HistoricalPosition>> positionCache;

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
        Log.d(TAG, "🔧 GameDatabaseHelper constructor starting...");
        this.context = context.getApplicationContext();
        Log.d(TAG, "✅ SQLiteOpenHelper and context initialized");
        
        // Initialize the cache with 100 entries max
        // Each entry uses approximately 1KB, so total cache size ~100KB
        this.positionCache = new LruCache<>(100);
        Log.d(TAG, "✅ Position cache initialized");
        
        Log.d(TAG, "🗄️ GameDatabaseHelper constructor completed!");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create existing games table
        db.execSQL(CREATE_GAMES_TABLE);

        // Create master positions table
        db.execSQL(CREATE_MASTER_POSITIONS_TABLE);

        // Create NEW emergent behavior tables
        db.execSQL(CREATE_RELATIONSHIPS_TABLE);
        db.execSQL(CREATE_TOPIC_MEMORY_TABLE);
        db.execSQL(CREATE_EMOTIONAL_REACTIONS_TABLE);
        db.execSQL(CREATE_CONVERSATION_EVOLUTION_TABLE);
        db.execSQL(CREATE_EMERGENT_EVENTS_TABLE);
        db.execSQL(CREATE_EXPRESSION_PATTERNS_TABLE);
        db.execSQL(CREATE_EMOTIONAL_STRATEGY_LEARNING_TABLE);

        // Create all indexes for performance
        db.execSQL(CREATE_FEN_INDEX);
        db.execSQL(CREATE_MASTER_INDEX);
        db.execSQL(CREATE_RELATIONSHIPS_INDEX);
        db.execSQL(CREATE_TOPIC_MEMORY_INDEX);
        db.execSQL(CREATE_EMOTIONAL_REACTIONS_INDEX);
        db.execSQL(CREATE_EMERGENT_EVENTS_INDEX);
        db.execSQL(CREATE_EXPRESSION_PATTERNS_INDEX);
        db.execSQL(CREATE_EMOTIONAL_STRATEGY_LEARNING_INDEX);

        Log.d(TAG, "✅ Database created with game storage, personality engine, AND emergent behavior tracking!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add the master positions functionality
            db.execSQL(CREATE_MASTER_POSITIONS_TABLE);
            db.execSQL(CREATE_FEN_INDEX);
            db.execSQL(CREATE_MASTER_INDEX);
            Log.d(TAG, "🚀 Database upgraded with personality engine support!");
        }
        
        if (oldVersion < 3) {
            // Add emergent behavior tracking tables
            db.execSQL(CREATE_RELATIONSHIPS_TABLE);
            db.execSQL(CREATE_TOPIC_MEMORY_TABLE);
            db.execSQL(CREATE_EMOTIONAL_REACTIONS_TABLE);
            db.execSQL(CREATE_CONVERSATION_EVOLUTION_TABLE);
            db.execSQL(CREATE_EMERGENT_EVENTS_TABLE);
            
            // Add new indexes
            db.execSQL(CREATE_RELATIONSHIPS_INDEX);
            db.execSQL(CREATE_TOPIC_MEMORY_INDEX);
            db.execSQL(CREATE_EMOTIONAL_REACTIONS_INDEX);
            db.execSQL(CREATE_EMERGENT_EVENTS_INDEX);
            
            Log.d(TAG, "🎭 Database upgraded with emergent behavior tracking - relationships will now evolve!");
        }
        
        if (oldVersion < 4) {
            // Add expression patterns table for anti-repetition system
            db.execSQL(CREATE_EXPRESSION_PATTERNS_TABLE);
            db.execSQL(CREATE_EXPRESSION_PATTERNS_INDEX);
            
            Log.d(TAG, "🎭 Database upgraded with expression pattern tracking - Fischer will sound much more varied!");
        }
        
        if (oldVersion < 5) {
            // Add emotional strategy learning table for adaptive learning
            db.execSQL(CREATE_EMOTIONAL_STRATEGY_LEARNING_TABLE);
            db.execSQL(CREATE_EMOTIONAL_STRATEGY_LEARNING_INDEX);
            
            Log.d(TAG, "🧠🎯 Database upgraded with emotional strategy learning - Masters will now adapt and learn optimal approaches!");
        }
    }

    // ========== EXISTING SAVED GAMES FUNCTIONALITY ==========
    // (All your existing methods remain exactly the same!)

    public long saveGame(String playerColor, List<String> moves, String finalFen, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, System.currentTimeMillis());
            values.put(COLUMN_PLAYER_COLOR, playerColor);
            values.put(COLUMN_MOVES, convertMovesToString(moves));
            values.put(COLUMN_FINAL_FEN, finalFen);
            values.put(COLUMN_DESCRIPTION, description);

            long id = db.insert(TABLE_GAMES, null, values);
            return id;
        } finally {
            // Don't close - SQLiteOpenHelper manages database connections
        }
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_GAMES + " ORDER BY " + COLUMN_DATE + " DESC";
            cursor = db.rawQuery(selectQuery, null);

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
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Don't close - SQLiteOpenHelper manages database connections
        }

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

        // Don't close - SQLiteOpenHelper manages database connections
        return game;
    }

    public void deleteGame(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_GAMES, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        } finally {
            // Don't close - SQLiteOpenHelper manages database connections
        }
    }

    // ========== NEW: LIGHTNING-FAST PERSONALITY ENGINE SUPPORT! ==========

    /**
     * 🚀 INSTANT FEN LOOKUP - The heart of the speed improvement!
     * This replaces slow API calls with blazing-fast local queries.
     */
    public List<HistoricalPosition> findSimilarPositions(String fen, String masterName, int maxResults) {
        Log.d(TAG, "🔧 DEBUG ENTRY: findSimilarPositions called with fen=" + fen.substring(0, Math.min(30, fen.length())) + ", masterName='" + masterName + "', maxResults=" + maxResults);
        
        // First check the cache!
        String cacheKey = masterName + ":" + fen + ":" + maxResults;
        List<HistoricalPosition> cachedResult = positionCache.get(cacheKey);
        
        if (cachedResult != null) {
            Log.d(TAG, "🎯 CACHE HIT! Returning instant result for " + masterName);
            return new ArrayList<>(cachedResult); // Return a copy to prevent external modifications
        }
        
        // Not in cache, perform database lookup
        List<HistoricalPosition> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "🔧 DEBUG: Database opened, about to perform lookup queries");

        try {
            Log.d(TAG, "⚡ Database FEN lookup for " + masterName + " (will be cached)...");

            // First try exact FEN match for the specific master
            String exactQuery = "SELECT * FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_FEN + " = ? AND " + COLUMN_MASTER_NAME + " = ?" +
                    " LIMIT " + maxResults;
            Log.d(TAG, "🔧 DEBUG: Executing exact FEN query: " + exactQuery);
            Log.d(TAG, "🔧 DEBUG: Query parameters: fen='" + fen + "', masterName='" + masterName + "'");

            Cursor cursor = db.rawQuery(exactQuery, new String[]{fen, masterName});
            Log.d(TAG, "🔧 DEBUG: Exact FEN query returned " + cursor.getCount() + " results");

            if (cursor.getCount() > 0) {
                Log.d(TAG, "🎯 EXACT FEN MATCH FOUND for " + masterName + "!");
                results.addAll(cursorToHistoricalPositions(cursor));
                cursor.close();
                
                // Cache the results before returning
                if (!results.isEmpty()) {
                    positionCache.put(cacheKey, new ArrayList<>(results));
                    Log.d(TAG, "💾 Cached exact match results for future lookups");
                }
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
                
                // Cache the results before returning
                if (!results.isEmpty()) {
                    positionCache.put(cacheKey, new ArrayList<>(results));
                    Log.d(TAG, "💾 Cached similar match results for future lookups");
                }
                return results;
            }
            cursor.close();

            // Try opening-pattern matching for common openings
            String openingPattern = getOpeningPattern(fen);
            if (openingPattern != null) {
                String openingQuery = "SELECT * FROM " + TABLE_MASTER_POSITIONS +
                        " WHERE " + COLUMN_OPENING + " LIKE ? AND " + COLUMN_MASTER_NAME + " = ?" +
                        " LIMIT " + maxResults;
                
                cursor = db.rawQuery(openingQuery, new String[]{"%" + openingPattern + "%", masterName});
                
                if (cursor.getCount() > 0) {
                    Log.d(TAG, "🎯 Opening pattern match found for " + masterName + " (" + openingPattern + ")");
                    results.addAll(cursorToHistoricalPositions(cursor));
                    cursor.close();
                    
                    // Cache the results before returning
                    if (!results.isEmpty()) {
                        positionCache.put(cacheKey, new ArrayList<>(results));
                        Log.d(TAG, "💾 Cached opening pattern results for future lookups");
                    }
                    return results;
                }
                cursor.close();
            }

            // Fallback: get any positions from this master with similar tactical themes
            String fallbackQuery = "SELECT * FROM " + TABLE_MASTER_POSITIONS +
                    " WHERE " + COLUMN_MASTER_NAME + " = ?" +
                    " ORDER BY RANDOM() LIMIT " + maxResults;

            cursor = db.rawQuery(fallbackQuery, new String[]{masterName});
            results.addAll(cursorToHistoricalPositions(cursor));
            cursor.close();

            Log.d(TAG, "📚 Found " + results.size() + " reference positions for " + masterName);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in lightning-fast FEN lookup for " + masterName + " with FEN: " + fen, e);
        }
        
        // Cache the results before returning
        if (!results.isEmpty()) {
            positionCache.put(cacheKey, new ArrayList<>(results));
            Log.d(TAG, "💾 Cached results for future lookups");
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
     * Detect opening patterns from FEN positions for better matching
     */
    private String getOpeningPattern(String fen) {
        try {
            String boardPart = fen.split(" ")[0];
            
            // King's Pawn openings (1.e4)
            if (boardPart.contains("3P4/8/8/PPPPPPPP") || 
                boardPart.contains("4p3/8/8/pppppppp")) {
                return "King";
            }
            
            // Queen's Pawn openings (1.d4)  
            if (boardPart.contains("8/3P4/8/PPP1PPPP") ||
                boardPart.contains("8/3p4/8/ppp1pppp")) {
                return "Queen";
            }
            
            // English Opening (1.c4)
            if (boardPart.contains("8/2P5/8/PP1PPPPP") ||
                boardPart.contains("8/2p5/8/pp1ppppp")) {
                return "English";
            }
            
            // Sicilian Defense (1.e4 c5)
            if (boardPart.contains("2p5/pp1ppppp/8/3P4") ||
                boardPart.contains("2P5/PP1PPPPP/8/3p4")) {
                return "Sicilian";
            }
            
            // French Defense (1.e4 e6)  
            if (boardPart.contains("4p3/pppp1ppp/8/3P4") ||
                boardPart.contains("4P3/PPPP1PPP/8/3p4")) {
                return "French";
            }
            
            // Indian Defenses (includes King's Indian, Nimzo-Indian, etc.)
            if (boardPart.contains("5n2") || boardPart.contains("5N2")) {
                return "Indian";
            }
            
        } catch (Exception e) {
            Log.w(TAG, "Error parsing opening pattern from FEN: " + fen, e);
        }
        
        return null;
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
        // 🚨 CRITICAL FIX: Add immediate entry logging
        Log.d(TAG, "🎯 ENTRY: importMasterPositions called with data length: " + 
              (jsonData != null ? jsonData.length() : "NULL") + " characters");
        
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Log.d(TAG, "🚀 Starting master positions import...");

            JSONArray chunks = new JSONArray(jsonData);
            Log.d(TAG, "📊 Found " + chunks.length() + " positions to import");

            db.beginTransaction();

            int importedCount = 0;
            for (int i = 0; i < chunks.length(); i++) {
                JSONObject chunk = chunks.getJSONObject(i);

                ContentValues values = new ContentValues();
                // 🎯 CRITICAL FIX: Map full names to standardized short names for lookup compatibility
                String playerName = chunk.optString("player_name", "");
                String standardizedName = getStandardizedMasterName(playerName);
                
                // Log the first few entries for debugging
                if (i < 3 || (i % 100 == 0)) {
                    Log.d(TAG, "📥 Entry " + i + ": '" + playerName + "' -> '" + standardizedName + "'");
                }
                
                values.put(COLUMN_MASTER_NAME, standardizedName);
                values.put(COLUMN_FEN, chunk.optString("fen", ""));
                values.put(COLUMN_OPPONENT, chunk.optString("opponent", ""));
                values.put(COLUMN_YEAR, chunk.optString("year", ""));
                values.put(COLUMN_TOURNAMENT, chunk.optString("tournament", ""));
                values.put(COLUMN_OPENING, chunk.optString("opening", ""));
                values.put(COLUMN_RESULT, chunk.optString("result", ""));
                values.put(COLUMN_SIGNIFICANCE, chunk.optString("significance", ""));
                values.put(COLUMN_MOVE_NUMBER, chunk.optInt("move_number", 0));
                values.put(COLUMN_ANNOTATION, chunk.optString("annotation", ""));
                // 🚨 SAFE: Handle potentially null tags array
                JSONArray tagsArray = chunk.optJSONArray("tags");
                if (tagsArray != null) {
                    values.put(COLUMN_TAGS, tagsArray.toString());
                } else {
                    values.put(COLUMN_TAGS, "[]"); // Empty array as fallback
                }

                long insertId = db.insert(TABLE_MASTER_POSITIONS, null, values);
                if (insertId != -1) {
                    importedCount++;
                } else {
                    Log.w(TAG, "❌ Failed to insert position " + i + " for " + standardizedName);
                }
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "✅ Successfully imported " + importedCount + "/" + chunks.length() + " master positions!");
            
            // Clear the cache since we have new data
            positionCache.evictAll();
            Log.d(TAG, "🧹 Cleared position cache after import");

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
     * ENHANCED: With detailed logging for debugging
     */
    public boolean importMasterPositionsFromAssets(String filename) {
        // 🚨 CRITICAL FIX: Add immediate entry logging
        Log.d(TAG, "🎯 ENTRY: importMasterPositionsFromAssets called with filename: " + filename);
        
        try {
            Log.d(TAG, "📁 Attempting to import from assets file: " + filename);
            
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
                lineCount++;
            }

            reader.close();
            inputStream.close();
            
            Log.d(TAG, "📖 Read " + lineCount + " lines from " + filename + " (total " + jsonBuilder.length() + " characters)");

            // 🎯 CRITICAL FIX: Handle different JSON structures (array vs object with positions)
            String jsonDataForImport = extractPositionsFromJson(jsonBuilder.toString(), filename);
            if (jsonDataForImport == null) {
                Log.e(TAG, "❌ Failed to extract valid positions data from " + filename);
                return false;
            }

            boolean result = importMasterPositions(jsonDataForImport);
            Log.d(TAG, "📥 Import result for " + filename + ": " + (result ? "SUCCESS" : "FAILED"));
            
            return result;

        } catch (IOException e) {
            Log.e(TAG, "❌ IOException reading from assets: " + filename, e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "❌ General exception importing from assets: " + filename, e);
            return false;
        }
    }

    /**
     * Check if we have master data (used by PersonalityEngine)
     * ENHANCED: With detailed logging for debugging
     */
    public boolean hasMasterData(String masterName) {
        // 🚨 CRITICAL FIX: Add immediate entry logging
        Log.d(TAG, "🎯 ENTRY: hasMasterData called for master: " + masterName);
        
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT 1 FROM " + TABLE_MASTER_POSITIONS + " WHERE " + COLUMN_MASTER_NAME + " = ? LIMIT 1";
            Log.d(TAG, "🔍 Executing query: " + query + " with parameter: " + masterName);
            
            Cursor cursor = db.rawQuery(query, new String[]{masterName});

            boolean hasData = cursor.moveToFirst();
            cursor.close();
            
            Log.d(TAG, "🔍 hasMasterData('" + masterName + "'): " + hasData);
            
            // If no data found, let's see what master names we DO have
            if (!hasData) {
                String statsQuery = "SELECT DISTINCT " + COLUMN_MASTER_NAME + " FROM " + TABLE_MASTER_POSITIONS + " LIMIT 10";
                Cursor statsCursor = db.rawQuery(statsQuery, null);
                StringBuilder availableMasters = new StringBuilder();
                while (statsCursor.moveToNext()) {
                    if (availableMasters.length() > 0) availableMasters.append(", ");
                    availableMasters.append("'").append(statsCursor.getString(0)).append("'");
                }
                statsCursor.close();
                Log.d(TAG, "📋 Available master names in database: [" + availableMasters.toString() + "]");
            }
            
            return hasData;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking master data for '" + masterName + "'", e);
        }

        return false;
    }

    /**
     * Get detailed position count for a specific master (for debugging only)
     */
    public int getMasterPositionCount(String masterName) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_MASTER_POSITIONS + " WHERE " + COLUMN_MASTER_NAME + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{masterName});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                return count;
            }

            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error getting master position count", e);
        }

        return 0;
    }
    
    /**
     * Get total position count across all masters (for debugging only)
     */
    public int getTotalPositionCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_MASTER_POSITIONS;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                return count;
            }

            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error getting total position count", e);
        }

        return 0;
    }
    
    /**
     * 🔄 Force clear and reimport all master data - use when database structure changes
     */
    public boolean clearAndReimportAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            Log.d(TAG, "🗑️ Clearing existing master positions data...");
            db.delete(TABLE_MASTER_POSITIONS, null, null);
            
            // Clear cache
            positionCache.evictAll();
            
            Log.d(TAG, "✅ All master data cleared. Ready for fresh import.");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error clearing master data", e);
            return false;
        }
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
    
    /**
     * Get cache performance statistics
     */
    public String getCacheStats() {
        int currentSize = positionCache.size();
        int maxSize = positionCache.maxSize();
        int hitCount = positionCache.hitCount();
        int missCount = positionCache.missCount();
        float hitRate = (hitCount + missCount) > 0 ? 
            (float) hitCount / (hitCount + missCount) * 100 : 0;
        
        return String.format("Cache: %d/%d entries, %.1f%% hit rate (%d hits, %d misses)",
            currentSize, maxSize, hitRate, hitCount, missCount);
    }

    // ========== HELPER METHODS ==========
    
    /**
     * 🎯 CRITICAL: Map full master names to standardized short names for lookup compatibility
     * This ensures PersonalityEngine lookups work correctly with the imported data
     */
    private String getStandardizedMasterName(String fullName) {
        if (fullName == null) return "";
        
        String normalized = fullName.toLowerCase().trim();
        
        // Map full names to the exact strings PersonalityEngine expects
        if (normalized.contains("mikhail tal") || normalized.contains("tal")) {
            return "tal";
        } else if (normalized.contains("bobby fischer") || normalized.contains("fischer")) {
            return "fischer";  
        } else if (normalized.contains("magnus carlsen") || normalized.contains("carlsen")) {
            return "carlsen";
        } else if (normalized.contains("garry kasparov") || normalized.contains("kasparov")) {
            return "kasparov";
        } else if (normalized.contains("anatoly karpov") || normalized.contains("karpov")) {
            return "karpov";
        } else if (normalized.contains("vladimir kramnik") || normalized.contains("kramnik")) {
            return "kramnik";
        } else if (normalized.contains("alexander alekhine") || normalized.contains("alekhine")) {
            return "alekhine";
        } else if (normalized.contains("josé raúl capablanca") || normalized.contains("jose raul capablanca") || normalized.contains("capablanca")) {
            return "capablanca";
        } else if (normalized.contains("emanuel lasker") || normalized.contains("lasker")) {
            return "lasker";
        } else if (normalized.contains("paul morphy") || normalized.contains("morphy")) {
            return "morphy";
        } else if (normalized.contains("viswanathan anand") || normalized.contains("anand")) {
            return "anand";
        } else if (normalized.contains("mikhail botvinnik") || normalized.contains("botvinnik")) {
            return "botvinnik";
        }
        
        // Fallback - return original name
        Log.w(TAG, "⚠️ Unknown master name, using as-is: " + fullName);
        return fullName;
    }

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

    /**
     * 🎯 CRITICAL FIX: Extract positions array from different JSON structures
     * Handles both direct array format and object with "positions" property
     */
    private String extractPositionsFromJson(String jsonData, String filename) {
        try {
            Log.d(TAG, "🔍 Analyzing JSON structure for " + filename);
            
            // First, try to parse as JSON to determine structure
            String trimmedData = jsonData.trim();
            
            if (trimmedData.startsWith("[")) {
                // Direct array format (tal, fischer, etc.)
                Log.d(TAG, "✅ Detected direct array format for " + filename);
                JSONArray testArray = new JSONArray(jsonData); // Validate it's proper JSON
                Log.d(TAG, "📊 Array contains " + testArray.length() + " positions");
                return jsonData;
                
            } else if (trimmedData.startsWith("{")) {
                // Object format (carlsen with metadata)
                Log.d(TAG, "🔍 Detected object format for " + filename + " - looking for 'positions' array");
                JSONObject rootObject = new JSONObject(jsonData);
                
                if (rootObject.has("positions")) {
                    JSONArray positionsArray = rootObject.getJSONArray("positions");
                    Log.d(TAG, "✅ Found 'positions' array with " + positionsArray.length() + " entries");
                    
                    // Log metadata if present for debugging
                    if (rootObject.has("metadata")) {
                        JSONObject metadata = rootObject.getJSONObject("metadata");
                        String playerName = metadata.optString("player_name", "Unknown");
                        int totalPositions = metadata.optInt("total_positions", 0);
                        Log.d(TAG, "📊 Metadata: player=" + playerName + ", total_positions=" + totalPositions);
                    }
                    
                    return positionsArray.toString();
                    
                } else {
                    Log.e(TAG, "❌ Object format detected but no 'positions' array found in " + filename);
                    Log.e(TAG, "📋 Available keys: " + rootObject.keys().toString());
                    return null;
                }
                
            } else {
                Log.e(TAG, "❌ Unrecognized JSON format for " + filename + " - must start with '[' or '{'");
                return null;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error analyzing JSON structure for " + filename, e);
            return null;
        }
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
    
    /**
     * 🔍 Comprehensive database diagnostics - call this to debug empty database issues
     */
    public String getDatabaseDiagnostics() {
        StringBuilder diagnostics = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();
        
        try {
            // Check if table exists
            String checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_MASTER_POSITIONS + "'";
            Cursor tableCursor = db.rawQuery(checkTableQuery, null);
            boolean tableExists = tableCursor.getCount() > 0;
            tableCursor.close();
            
            diagnostics.append("📊 DATABASE DIAGNOSTICS:\n");
            diagnostics.append("- Table exists: ").append(tableExists ? "✅ YES" : "❌ NO").append("\n");
            
            if (tableExists) {
                // Total positions
                Cursor totalCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MASTER_POSITIONS, null);
                if (totalCursor.moveToFirst()) {
                    int total = totalCursor.getInt(0);
                    diagnostics.append("- Total positions: ").append(total).append("\n");
                }
                totalCursor.close();
                
                // Sample data
                Cursor sampleCursor = db.rawQuery("SELECT " + COLUMN_MASTER_NAME + ", COUNT(*) FROM " + 
                    TABLE_MASTER_POSITIONS + " GROUP BY " + COLUMN_MASTER_NAME + " LIMIT 10", null);
                diagnostics.append("- Masters with data:\n");
                while (sampleCursor.moveToNext()) {
                    String master = sampleCursor.getString(0);
                    int count = sampleCursor.getInt(1);
                    diagnostics.append("  • ").append(master).append(": ").append(count).append(" positions\n");
                }
                sampleCursor.close();
            }
            
        } catch (Exception e) {
            diagnostics.append("❌ Error during diagnostics: ").append(e.getMessage()).append("\n");
        }
        
        String result = diagnostics.toString();
        Log.d(TAG, result);
        return result;
    }
}