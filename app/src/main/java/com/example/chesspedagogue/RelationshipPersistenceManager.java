package com.example.chesspedagogue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🎭 RELATIONSHIP PERSISTENCE MANAGER
 * Handles all database operations for emergent behavior tracking
 * Features:
 * - Master relationship evolution tracking
 * - Topic memory and fatigue management
 * - Emotional reaction history
 * - Conversation evolution and breakthrough detection
 * - Emergent event logging
 */
public class RelationshipPersistenceManager {
    private static final String TAG = "RelationshipPersistence";
    
    private static RelationshipPersistenceManager instance;
    private final GameDatabaseHelper dbHelper;
    private final Context context;
    
    // Topic fatigue thresholds
    private static final float TOPIC_FATIGUE_THRESHOLD = 0.7f;
    private static final int MAX_TOPIC_DISCUSSIONS = 5;
    private static final long TOPIC_COOLDOWN_MS = 300000; // 5 minutes
    
    private RelationshipPersistenceManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new GameDatabaseHelper(context);
    }
    
    public static synchronized RelationshipPersistenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new RelationshipPersistenceManager(context);
        }
        return instance;
    }
    
    // =========================== RELATIONSHIP TRACKING ===========================
    
    /**
     * 👥 Get or create relationship between two masters
     */
    public MasterRelationship getRelationship(String master1, String master2) {
        String[] masters = normalizeMasterOrder(master1, master2);
        String normalizedMaster1 = masters[0];
        String normalizedMaster2 = masters[1];
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM master_relationships WHERE master1 = ? AND master2 = ?";
            cursor = db.rawQuery(query, new String[]{normalizedMaster1, normalizedMaster2});
            
            MasterRelationship relationship;
            if (cursor.moveToFirst()) {
                // Existing relationship
                relationship = new MasterRelationship(
                    cursor.getString(cursor.getColumnIndex("master1")),
                    cursor.getString(cursor.getColumnIndex("master2")),
                    cursor.getFloat(cursor.getColumnIndex("respect_level")),
                    cursor.getFloat(cursor.getColumnIndex("rivalry_intensity")),
                    cursor.getFloat(cursor.getColumnIndex("friendship_bond")),
                    cursor.getString(cursor.getColumnIndex("communication_style")),
                    cursor.getInt(cursor.getColumnIndex("total_interactions")),
                    cursor.getInt(cursor.getColumnIndex("total_games")),
                    cursor.getString(cursor.getColumnIndex("last_major_event"))
                );
                
                Log.d(TAG, String.format("👥 Retrieved relationship: %s <-> %s (respect: %.2f, rivalry: %.2f, friendship: %.2f)", 
                       master1, master2, relationship.respectLevel, relationship.rivalryIntensity, relationship.friendshipBond));
            } else {
                // Create new relationship
                relationship = new MasterRelationship(normalizedMaster1, normalizedMaster2);
                createRelationship(relationship);
                
                Log.d(TAG, String.format("👥 Created new relationship: %s <-> %s (initial values)", master1, master2));
            }
            
            return relationship;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // 🔧 FIX: Don't close database - let SQLiteOpenHelper manage connections
        }
    }
    
    /**
     * 💾 Save relationship updates to database
     */
    public void updateRelationship(MasterRelationship relationship) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put("respect_level", relationship.respectLevel);
            values.put("rivalry_intensity", relationship.rivalryIntensity);
            values.put("friendship_bond", relationship.friendshipBond);
            values.put("communication_style", relationship.communicationStyle);
            values.put("total_interactions", relationship.totalInteractions);
            values.put("total_games", relationship.totalGames);
            values.put("last_major_event", relationship.lastMajorEvent);
            values.put("last_updated", System.currentTimeMillis());
            
            int rowsUpdated = db.update("master_relationships", values, 
                                       "master1 = ? AND master2 = ?", 
                                       new String[]{relationship.master1, relationship.master2});
            
            if (rowsUpdated > 0) {
                Log.d(TAG, String.format("💾 Updated relationship: %s <-> %s (respect: %.2f, rivalry: %.2f, friendship: %.2f)", 
                       relationship.master1, relationship.master2, relationship.respectLevel, 
                       relationship.rivalryIntensity, relationship.friendshipBond));
            } else {
                Log.w(TAG, String.format("⚠️ Failed to update relationship: %s <-> %s", 
                       relationship.master1, relationship.master2));
            }
        } finally {
            // 🔧 FIX: Don't close database - let SQLiteOpenHelper manage connections
        }
    }
    
    private void createRelationship(MasterRelationship relationship) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put("master1", relationship.master1);
            values.put("master2", relationship.master2);
            values.put("respect_level", relationship.respectLevel);
            values.put("rivalry_intensity", relationship.rivalryIntensity);
            values.put("friendship_bond", relationship.friendshipBond);
            values.put("communication_style", relationship.communicationStyle);
            values.put("total_interactions", relationship.totalInteractions);
            values.put("total_games", relationship.totalGames);
            values.put("last_major_event", relationship.lastMajorEvent);
            values.put("last_updated", System.currentTimeMillis());
            values.put("created_at", System.currentTimeMillis());
            
            long id = db.insert("master_relationships", null, values);
            
            if (id != -1) {
                Log.d(TAG, String.format("✅ Created relationship: %s <-> %s (ID: %d)", 
                       relationship.master1, relationship.master2, id));
            } else {
                Log.e(TAG, String.format("❌ Failed to create relationship: %s <-> %s", 
                       relationship.master1, relationship.master2));
            }
        } finally {
            // 🔧 FIX: Don't close database - let SQLiteOpenHelper manage connections
        }
    }
    
    // =========================== TOPIC MEMORY TRACKING ===========================
    
    /**
     * 🧠 Record topic discussion with emotional context
     */
    public void recordTopicDiscussion(String master1, String master2, String topic, 
                                     String emotion1, String emotion2, float intensity) {
        String[] masters = normalizeMasterOrder(master1, master2);
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Check if topic already exists
        String query = "SELECT * FROM topic_memory WHERE master1 = ? AND master2 = ? AND topic = ?";
        Cursor cursor = db.rawQuery(query, new String[]{masters[0], masters[1], topic.toLowerCase()});
        
        long timestamp = System.currentTimeMillis();
        
        if (cursor.moveToFirst()) {
            // Update existing topic
            int discussionCount = cursor.getInt(cursor.getColumnIndex("discussion_count")) + 1;
            float totalIntensity = cursor.getFloat(cursor.getColumnIndex("total_emotional_intensity")) + intensity;
            float avgIntensity = totalIntensity / discussionCount;
            
            // Calculate topic fatigue
            float fatigue = calculateTopicFatigue(discussionCount, avgIntensity, timestamp, 
                                                cursor.getLong(cursor.getColumnIndex("created_at")));
            
            ContentValues values = new ContentValues();
            values.put("discussion_count", discussionCount);
            values.put("total_emotional_intensity", totalIntensity);
            values.put("average_emotional_intensity", avgIntensity);
            values.put("last_emotion_master1", emotion1);
            values.put("last_emotion_master2", emotion2);
            values.put("topic_fatigue_level", fatigue);
            values.put("last_discussed", timestamp);
            
            db.update("topic_memory", values, "master1 = ? AND master2 = ? AND topic = ?", 
                     new String[]{masters[0], masters[1], topic.toLowerCase()});
            
            Log.d(TAG, String.format("🧠 Updated topic memory: %s between %s-%s (count: %d, fatigue: %.2f)", 
                   topic, master1, master2, discussionCount, fatigue));
        } else {
            // Create new topic record
            ContentValues values = new ContentValues();
            values.put("master1", masters[0]);
            values.put("master2", masters[1]);
            values.put("topic", topic.toLowerCase());
            values.put("discussion_count", 1);
            values.put("total_emotional_intensity", intensity);
            values.put("average_emotional_intensity", intensity);
            values.put("last_emotion_master1", emotion1);
            values.put("last_emotion_master2", emotion2);
            values.put("topic_fatigue_level", 0.0f);
            values.put("breakthrough_moments", 0);
            values.put("conflict_incidents", 0);
            values.put("last_discussed", timestamp);
            values.put("created_at", timestamp);
            
            db.insert("topic_memory", null, values);
            
            Log.d(TAG, String.format("🧠 Created topic memory: %s between %s-%s", 
                   topic, master1, master2));
        }
        
        cursor.close();
    }
    
    /**
     * 🚫 Get fatigued topics that should be avoided
     */
    public List<String> getFatiguedTopics(String master1, String master2) {
        String[] masters = normalizeMasterOrder(master1, master2);
        List<String> fatiguedTopics = new ArrayList<>();
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT topic FROM topic_memory WHERE master1 = ? AND master2 = ? AND topic_fatigue_level > ?";
            cursor = db.rawQuery(query, new String[]{masters[0], masters[1], String.valueOf(TOPIC_FATIGUE_THRESHOLD)});
            
            while (cursor.moveToNext()) {
                fatiguedTopics.add(cursor.getString(0));
            }
            
            Log.d(TAG, String.format("🚫 Found %d fatigued topics for %s-%s: %s", 
                   fatiguedTopics.size(), master1, master2, fatiguedTopics.toString()));
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Fixed: Don't close DB connection - managed by SQLiteOpenHelper
        }
        
        return fatiguedTopics;
    }
    
    /**
     * 💡 Get fresh topic suggestions based on relationship and history
     */
    public List<String> getFreshTopicSuggestions(String master1, String master2, String currentEmotion) {
        MasterRelationship relationship = getRelationship(master1, master2);
        List<String> suggestions = new ArrayList<>();
        
        // Add base fresh topics (avoiding recursion by implementing directly)
        suggestions.addAll(Arrays.asList(
            "chess_history", "learning_journey", "memorable_games", "chess_beauty",
            "competitive_psychology", "chess_evolution", "teaching_chess", "chess_patterns",
            "decision_making", "chess_culture", "famous_positions", "chess_mysteries"
        ));
        
        // Add relationship-based topics
        if (relationship.rivalryIntensity > 0.6f) {
            suggestions.add("competitive_spirit");
            suggestions.add("chess_rivalry");
        }
        
        if (relationship.respectLevel > 0.7f) {
            suggestions.add("mutual_appreciation");
            suggestions.add("learning_from_opponents");
        }
        
        if (relationship.friendshipBond > 0.5f) {
            suggestions.add("chess_friendship");
            suggestions.add("shared_experiences");
        }
        
        Log.d(TAG, String.format("💡 Generated %d fresh topic suggestions for %s-%s based on relationship dynamics", 
               suggestions.size(), master1, master2));
        
        return suggestions;
    }
    
    // =========================== EMOTIONAL REACTION TRACKING ===========================
    
    /**
     * 🎭 Record emotional reaction to topic
     */
    public void recordEmotionalReaction(String master, String opponent, String topic, String emotion, 
                                       float intensity, float momentum, String gameContext, 
                                       float positionEval, String conversationSnippet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            // Calculate relationship impact
            float relationshipImpact = calculateRelationshipImpact(emotion, intensity, momentum);
            
            ContentValues values = new ContentValues();
            values.put("master", master.toLowerCase());
            values.put("opponent", opponent.toLowerCase());
            values.put("topic", topic.toLowerCase());
            values.put("emotion", emotion);
            values.put("intensity", intensity);
            values.put("momentum", momentum);
            values.put("game_context", gameContext);
            values.put("position_evaluation", positionEval);
            values.put("conversation_snippet", conversationSnippet);
            values.put("relationship_impact", relationshipImpact);
            values.put("timestamp", System.currentTimeMillis());
            
            long id = db.insert("emotional_reactions", null, values);
            
            if (id != -1) {
                Log.d(TAG, String.format("🎭 Recorded emotional reaction: %s felt %s (%.2f) about %s with %s", 
                       master, emotion, intensity, topic, opponent));
                
                // Update relationship based on emotional impact
                if (Math.abs(relationshipImpact) > 0.1f) {
                    updateRelationshipFromEmotionalReaction(master, opponent, relationshipImpact, emotion, intensity);
                }
            }
        } finally {
            // 🔧 FIX: Don't close database - let SQLiteOpenHelper manage connections
        }
    }
    
    /**
     * 📊 Get emotional history for a master on specific topic
     */
    public List<EmotionalReaction> getEmotionalHistory(String master, String topic, int limit) {
        List<EmotionalReaction> reactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM emotional_reactions WHERE master = ? AND topic = ? " +
                          "ORDER BY timestamp DESC LIMIT ?";
            cursor = db.rawQuery(query, new String[]{master.toLowerCase(), topic.toLowerCase(), String.valueOf(limit)});
            
            while (cursor.moveToNext()) {
                EmotionalReaction reaction = new EmotionalReaction(
                    cursor.getString(cursor.getColumnIndex("master")),
                    cursor.getString(cursor.getColumnIndex("opponent")),
                    cursor.getString(cursor.getColumnIndex("topic")),
                    cursor.getString(cursor.getColumnIndex("emotion")),
                    cursor.getFloat(cursor.getColumnIndex("intensity")),
                    cursor.getFloat(cursor.getColumnIndex("momentum")),
                    cursor.getString(cursor.getColumnIndex("conversation_snippet")),
                    cursor.getLong(cursor.getColumnIndex("timestamp"))
                );
                reactions.add(reaction);
            }
            
            Log.d(TAG, String.format("📊 Retrieved %d emotional reactions for %s on topic %s", 
                   reactions.size(), master, topic));
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Fixed: Don't close DB connection - managed by SQLiteOpenHelper
        }
        
        return reactions;
    }
    
    /**
     * 🧠 NEW: Get emotional history between two masters
     */
    public List<EmotionalReaction> getEmotionalHistoryBetweenMasters(String master, String opponent, int limit) {
        List<EmotionalReaction> reactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM emotional_reactions WHERE master = ? AND opponent = ? " +
                          "ORDER BY timestamp DESC LIMIT ?";
            cursor = db.rawQuery(query, new String[]{master.toLowerCase(), opponent.toLowerCase(), String.valueOf(limit)});
            
            while (cursor.moveToNext()) {
                EmotionalReaction reaction = new EmotionalReaction(
                    cursor.getString(cursor.getColumnIndex("master")),
                    cursor.getString(cursor.getColumnIndex("opponent")),
                    cursor.getString(cursor.getColumnIndex("topic")),
                    cursor.getString(cursor.getColumnIndex("emotion")),
                    cursor.getFloat(cursor.getColumnIndex("intensity")),
                    cursor.getFloat(cursor.getColumnIndex("momentum")),
                    cursor.getString(cursor.getColumnIndex("conversation_snippet")),
                    cursor.getLong(cursor.getColumnIndex("timestamp"))
                );
                reactions.add(reaction);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Fixed: Don't close DB connection - managed by SQLiteOpenHelper
        }
        
        return reactions;
    }
    
    // =========================== EMERGENT EVENT TRACKING ===========================
    
    /**
     * 🌟 Record breakthrough or significant emergent event
     */
    public void recordEmergentEvent(String eventType, String master1, String master2, String description,
                                   String emotionalContext, String conversationContent, float impactLevel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues();
            values.put("event_type", eventType);
            values.put("master1", master1.toLowerCase());
            values.put("master2", master2.toLowerCase());
            values.put("description", description);
            values.put("emotional_context", emotionalContext);
            values.put("conversation_content", conversationContent);
            values.put("impact_level", impactLevel);
            values.put("timestamp", System.currentTimeMillis());
            
            long id = db.insert("emergent_events", null, values);
            
            if (id != -1) {
                Log.d(TAG, String.format("🌟 Recorded emergent event: %s between %s-%s (impact: %.2f)", 
                       eventType, master1, master2, impactLevel));
                
                // Update relationship with major event
                MasterRelationship relationship = getRelationship(master1, master2);
                relationship.lastMajorEvent = eventType;
                relationship.totalInteractions++;
                updateRelationship(relationship);
            }
        } finally {
            // 🔧 FIX: Don't close database - let SQLiteOpenHelper manage connections
        }
    }
    
    /**
     * 📈 Get recent emergent events for analysis
     */
    public List<EmergentEvent> getRecentEmergentEvents(int limit) {
        List<EmergentEvent> events = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM emergent_events ORDER BY timestamp DESC LIMIT ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});
            
            while (cursor.moveToNext()) {
                EmergentEvent event = new EmergentEvent(
                    cursor.getString(cursor.getColumnIndex("event_type")),
                    cursor.getString(cursor.getColumnIndex("master1")),
                    cursor.getString(cursor.getColumnIndex("master2")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getFloat(cursor.getColumnIndex("impact_level")),
                    cursor.getLong(cursor.getColumnIndex("timestamp"))
                );
                events.add(event);
            }
            
            Log.d(TAG, String.format("📈 Retrieved %d recent emergent events", events.size()));
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Fixed: Don't close DB connection - managed by SQLiteOpenHelper
        }
        
        return events;
    }
    
    // =========================== UTILITY METHODS ===========================
    
    /**
     * Normalize master order for consistent database keys
     */
    private String[] normalizeMasterOrder(String master1, String master2) {
        String m1 = master1.toLowerCase();
        String m2 = master2.toLowerCase();
        
        if (m1.compareTo(m2) <= 0) {
            return new String[]{m1, m2};
        } else {
            return new String[]{m2, m1};
        }
    }
    
    /**
     * Calculate topic fatigue based on usage patterns
     */
    private float calculateTopicFatigue(int discussionCount, float avgIntensity, long lastDiscussed, long createdAt) {
        // Base fatigue from discussion frequency
        float frequencyFatigue = Math.min(1.0f, (float) discussionCount / MAX_TOPIC_DISCUSSIONS);
        
        // Time-based recovery (topics become less fatigued over time)
        long timeSinceCreation = lastDiscussed - createdAt;
        float timeRecovery = Math.min(0.5f, timeSinceCreation / (7 * 24 * 60 * 60 * 1000f)); // 7 days for full recovery
        
        // Emotional intensity affects fatigue (high-intensity topics get more fatigued)
        float intensityFactor = avgIntensity * 0.3f;
        
        float totalFatigue = Math.max(0.0f, Math.min(1.0f, frequencyFatigue + intensityFactor - timeRecovery));
        
        return totalFatigue;
    }
    
    /**
     * Calculate relationship impact from emotional reaction
     */
    private float calculateRelationshipImpact(String emotion, float intensity, float momentum) {
        float baseImpact = intensity * 0.1f; // Base impact is 10% of intensity
        
        // Positive emotions improve relationships
        if (emotion.equals("impressed") || emotion.equals("pleased") || emotion.equals("excited")) {
            return baseImpact;
        }
        // Negative emotions can damage relationships
        else if (emotion.equals("frustrated") || emotion.equals("devastated") || emotion.equals("angry")) {
            return -baseImpact;
        }
        // Neutral emotions have minimal impact
        else {
            return baseImpact * 0.3f;
        }
    }
    
    /**
     * Update relationship based on emotional reaction
     */
    private void updateRelationshipFromEmotionalReaction(String master, String opponent, float impact, 
                                                        String emotion, float intensity) {
        MasterRelationship relationship = getRelationship(master, opponent);
        
        if (impact > 0) {
            // Positive emotional impact
            if (intensity > 0.7f) {
                relationship.respectLevel = Math.min(1.0f, relationship.respectLevel + impact);
                relationship.friendshipBond = Math.min(1.0f, relationship.friendshipBond + impact * 0.5f);
            }
        } else {
            // Negative emotional impact
            if (intensity > 0.7f && emotion.equals("frustrated")) {
                relationship.rivalryIntensity = Math.min(1.0f, relationship.rivalryIntensity + Math.abs(impact));
            }
        }
        
        relationship.totalInteractions++;
        updateRelationship(relationship);
        
        Log.d(TAG, String.format("🔄 Updated relationship from emotional reaction: %s <-> %s (impact: %.2f)", 
               master, opponent, impact));
    }
    
    // =========================== DATA CLASSES ===========================
    
    /**
     * 👥 Master Relationship data class
     */
    public static class MasterRelationship {
        public String master1;
        public String master2;
        public float respectLevel;      // 0.0 to 1.0
        public float rivalryIntensity;  // 0.0 to 1.0
        public float friendshipBond;    // 0.0 to 1.0
        public String communicationStyle; // 'formal', 'casual', 'competitive', 'collaborative'
        public int totalInteractions;
        public int totalGames;
        public String lastMajorEvent;
        
        public MasterRelationship(String master1, String master2) {
            this.master1 = master1;
            this.master2 = master2;
            this.respectLevel = 0.5f;  // Start neutral
            this.rivalryIntensity = 0.0f;
            this.friendshipBond = 0.0f;
            this.communicationStyle = "formal";
            this.totalInteractions = 0;
            this.totalGames = 0;
            this.lastMajorEvent = null;
        }
        
        public MasterRelationship(String master1, String master2, float respectLevel, float rivalryIntensity,
                                 float friendshipBond, String communicationStyle, int totalInteractions,
                                 int totalGames, String lastMajorEvent) {
            this.master1 = master1;
            this.master2 = master2;
            this.respectLevel = respectLevel;
            this.rivalryIntensity = rivalryIntensity;
            this.friendshipBond = friendshipBond;
            this.communicationStyle = communicationStyle;
            this.totalInteractions = totalInteractions;
            this.totalGames = totalGames;
            this.lastMajorEvent = lastMajorEvent;
        }
    }
    
    /**
     * 🎭 Emotional Reaction data class
     */
    public static class EmotionalReaction {
        public String master;
        public String opponent;
        public String topic;
        public String emotion;
        public float intensity;
        public float momentum;
        public String conversationSnippet;
        public long timestamp;
        
        public EmotionalReaction(String master, String opponent, String topic, String emotion,
                               float intensity, float momentum, String conversationSnippet, long timestamp) {
            this.master = master;
            this.opponent = opponent;
            this.topic = topic;
            this.emotion = emotion;
            this.intensity = intensity;
            this.momentum = momentum;
            this.conversationSnippet = conversationSnippet;
            this.timestamp = timestamp;
        }
        
        // Getter methods for EmotionalIntelligenceManager
        public String getTopic() { return topic; }
        public String getEmotion() { return emotion; }
        public float getIntensity() { return intensity; }
        public long getTimestamp() { return timestamp; }
        public float getMomentum() { return momentum; }
        public String getConversationSnippet() { return conversationSnippet; }
    }
    
    /**
     * 🌟 Emergent Event data class
     */
    public static class EmergentEvent {
        public String eventType;
        public String master1;
        public String master2;
        public String description;
        public float impactLevel;
        public long timestamp;
        
        public EmergentEvent(String eventType, String master1, String master2, String description,
                           float impactLevel, long timestamp) {
            this.eventType = eventType;
            this.master1 = master1;
            this.master2 = master2;
            this.description = description;
            this.impactLevel = impactLevel;
            this.timestamp = timestamp;
        }
    }
    
    // =========================== EXPRESSION PATTERN TRACKING ===========================
    
    /**
     * 🎭 Record expression pattern for anti-repetition system
     * 
     * This method stores how masters express concepts to enable the PersonalityExpressionManager
     * to track and prevent repetitive phrasing patterns.
     */
    public void recordExpressionPattern(String masterName, String concept, 
                                      EmotionalIntelligenceManager.ExpressionPattern pattern) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            // Convert lists to JSON for storage
            JSONArray phrasesJson = new JSONArray(pattern.usedPhrases);
            JSONArray anglesJson = new JSONArray(pattern.usedAngles);
            JSONArray tonesJson = new JSONArray(pattern.usedEmotionalTones);
            
            ContentValues values = new ContentValues();
            values.put("master_name", masterName.toLowerCase());
            values.put("concept", concept);
            values.put("used_phrases", phrasesJson.toString());
            values.put("used_angles", anglesJson.toString());
            values.put("used_tones", tonesJson.toString());
            values.put("times_used", pattern.timesUsed);
            values.put("diversity_score", pattern.diversityScore);
            values.put("last_used", pattern.lastUsed);
            values.put("updated_at", System.currentTimeMillis());
            
            // Use INSERT OR REPLACE to handle both new and existing patterns
            long result = db.insertWithOnConflict("expression_patterns", null, values, 
                                                 SQLiteDatabase.CONFLICT_REPLACE);
            
            if (result != -1) {
                Log.d(TAG, String.format("🎭 Recorded expression pattern: %s -> '%s' (diversity: %.2f)",
                       masterName, concept, pattern.diversityScore));
            } else {
                Log.e(TAG, "Failed to record expression pattern for " + masterName + " -> " + concept);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error recording expression pattern: " + e.getMessage(), e);
        } finally {
            // 🔧 FIX: Don't close database - let SQLiteOpenHelper manage connections
        }
    }
    
    /**
     * 🎯 Load expression patterns for a master to enable anti-repetition
     */
    public Map<String, EmotionalIntelligenceManager.ExpressionPattern> loadExpressionPatterns(String masterName) {
        Map<String, EmotionalIntelligenceManager.ExpressionPattern> patterns = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM expression_patterns WHERE master_name = ?";
            cursor = db.rawQuery(query, new String[]{masterName.toLowerCase()});
            
            while (cursor.moveToNext()) {
                try {
                    String concept = cursor.getString(cursor.getColumnIndex("concept"));
                    
                    // Parse JSON arrays
                    JSONArray phrasesJson = new JSONArray(cursor.getString(cursor.getColumnIndex("used_phrases")));
                    JSONArray anglesJson = new JSONArray(cursor.getString(cursor.getColumnIndex("used_angles")));
                    JSONArray tonesJson = new JSONArray(cursor.getString(cursor.getColumnIndex("used_tones")));
                    
                    // Create pattern object
                    EmotionalIntelligenceManager.ExpressionPattern pattern = 
                        new EmotionalIntelligenceManager.ExpressionPattern(concept);
                    
                    // Populate lists
                    for (int i = 0; i < phrasesJson.length(); i++) {
                        pattern.usedPhrases.add(phrasesJson.getString(i));
                    }
                    for (int i = 0; i < anglesJson.length(); i++) {
                        pattern.usedAngles.add(anglesJson.getString(i));
                    }
                    for (int i = 0; i < tonesJson.length(); i++) {
                        pattern.usedEmotionalTones.add(tonesJson.getString(i));
                    }
                    
                    // Set other fields
                    pattern.timesUsed = cursor.getInt(cursor.getColumnIndex("times_used"));
                    pattern.diversityScore = cursor.getFloat(cursor.getColumnIndex("diversity_score"));
                    pattern.lastUsed = cursor.getLong(cursor.getColumnIndex("last_used"));
                    
                    patterns.put(concept, pattern);
                    
                    Log.d(TAG, String.format("🎭 Loaded expression pattern: %s -> '%s' (%d uses, diversity: %.2f)",
                           masterName, concept, pattern.timesUsed, pattern.diversityScore));
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing expression pattern JSON: " + e.getMessage(), e);
                }
            }
            
            Log.d(TAG, String.format("🎭 Loaded %d expression patterns for %s", patterns.size(), masterName));
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Fixed: Don't close DB connection - managed by SQLiteOpenHelper
        }
        
        return patterns;
    }
}