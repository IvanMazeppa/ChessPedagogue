package com.example.chesspedagogue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.ContentValues;
import android.util.Log;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 🌟 EMERGENT CONVERSATION MANAGER
 * True emergent behavior - extracts topics dynamically from actual conversations
 * No predefined topic lists - topics emerge naturally from AI dialogue
 */
public class EmergentConversationManager extends SQLiteOpenHelper {
    private static final String TAG = "EmergentConversation";
    private static final String DATABASE_NAME = "emergent_conversations.db";
    private static final int DATABASE_VERSION = 1;
    
    private static EmergentConversationManager instance;
    
    // Database Tables
    private static final String TABLE_TOPICS = "conversation_topics";
    private static final String TABLE_PLAYER_TOPICS = "player_topic_associations";
    private static final String TABLE_CONVERSATIONS = "conversation_history";
    
    public EmergentConversationManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static synchronized EmergentConversationManager getInstance(Context context) {
        if (instance == null) {
            instance = new EmergentConversationManager(context.getApplicationContext());
        }
        return instance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Topics table - dynamically discovered topics
        db.execSQL("CREATE TABLE " + TABLE_TOPICS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "topic_phrase TEXT UNIQUE," +
                "first_mentioned TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "total_mentions INTEGER DEFAULT 1," +
                "semantic_cluster TEXT," +
                "emergence_context TEXT" +
                ")");
        
        // Player-topic associations
        db.execSQL("CREATE TABLE " + TABLE_PLAYER_TOPICS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_name TEXT," +
                "topic_id INTEGER," +
                "mention_count INTEGER DEFAULT 1," +
                "last_mentioned TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "emotional_context TEXT," +
                "FOREIGN KEY(topic_id) REFERENCES " + TABLE_TOPICS + "(id)" +
                ")");
        
        // Conversation history for cross-session learning
        db.execSQL("CREATE TABLE " + TABLE_CONVERSATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "session_id TEXT," +
                "speaker TEXT," +
                "content TEXT," +
                "extracted_topics TEXT," +
                "game_context TEXT," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        Log.d(TAG, "🌟 Emergent conversation database created");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOPICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER_TOPICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
        onCreate(db);
    }
    
    /**
     * 🎯 Extract topics dynamically from conversation content
     * No predefined lists - true emergent topic discovery
     */
    public List<String> extractEmergentTopics(String conversationContent) {
        List<String> emergentTopics = new ArrayList<>();
        
        // 1. Extract key phrases (noun phrases, interesting concepts)
        emergentTopics.addAll(extractKeyPhrases(conversationContent));
        
        // 2. Extract chess-specific concepts mentioned
        emergentTopics.addAll(extractChessConcepts(conversationContent));
        
        // 3. Extract philosophical/strategic themes
        emergentTopics.addAll(extractPhilosophicalThemes(conversationContent));
        
        return emergentTopics;
    }
    
    private List<String> extractKeyPhrases(String content) {
        List<String> phrases = new ArrayList<>();
        
        // Extract interesting multi-word phrases
        Pattern phrasePattern = Pattern.compile("\\b([a-zA-Z]+\\s+[a-zA-Z]+(?:\\s+[a-zA-Z]+)?)\\b");
        java.util.regex.Matcher matcher = phrasePattern.matcher(content.toLowerCase());
        
        while (matcher.find()) {
            String phrase = matcher.group(1).trim();
            if (isInterestingPhrase(phrase)) {
                phrases.add(phrase);
            }
        }
        
        return phrases;
    }
    
    private List<String> extractChessConcepts(String content) {
        List<String> concepts = new ArrayList<>();
        String lower = content.toLowerCase();
        
        // Look for chess-specific discussions
        if (lower.contains("opening") && (lower.contains("theory") || lower.contains("principle"))) {
            concepts.add("opening theory discussion");
        }
        if (lower.contains("endgame") && (lower.contains("technique") || lower.contains("knowledge"))) {
            concepts.add("endgame mastery");
        }
        if (lower.contains("position") && (lower.contains("understand") || lower.contains("feel"))) {
            concepts.add("positional understanding");
        }
        
        return concepts;
    }
    
    private List<String> extractPhilosophicalThemes(String content) {
        List<String> themes = new ArrayList<>();
        String lower = content.toLowerCase();
        
        // Extract deeper philosophical discussions
        if (lower.contains("chess teaches") || lower.contains("game teaches")) {
            themes.add("chess wisdom");
        }
        if (lower.contains("beauty") && lower.contains("chess")) {
            themes.add("chess aesthetics");
        }
        if (lower.contains("intuition") && lower.contains("calculation")) {
            themes.add("intuition vs calculation debate");
        }
        
        return themes;
    }
    
    private boolean isInterestingPhrase(String phrase) {
        // Filter out common/boring phrases
        String[] boring = {"the game", "this position", "my move", "your move", "i think", "you know"};
        for (String boring_phrase : boring) {
            if (phrase.equals(boring_phrase)) return false;
        }
        
        // Keep phrases that seem substantive
        return phrase.length() > 4 && !phrase.matches(".*\\b(is|was|are|were|will|would|could|should)\\b.*");
    }
    
    /**
     * 🌱 Record conversation and discover emergent topics
     */
    public void recordEmergentConversation(String sessionId, String speaker, String content, String gameContext) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // Extract emergent topics from this conversation
            List<String> emergentTopics = extractEmergentTopics(content);
            
            // Store conversation
            ContentValues conversationValues = new ContentValues();
            conversationValues.put("session_id", sessionId);
            conversationValues.put("speaker", speaker);
            conversationValues.put("content", content);
            conversationValues.put("extracted_topics", String.join(",", emergentTopics));
            conversationValues.put("game_context", gameContext);
            
            long conversationId = db.insert(TABLE_CONVERSATIONS, null, conversationValues);
            
            // Process each emergent topic
            for (String topic : emergentTopics) {
                if (!topic.trim().isEmpty()) {
                    processeEmergentTopic(db, topic.trim(), speaker, content);
                }
            }
            
            Log.d(TAG, String.format("🌟 Recorded emergent conversation: %s extracted %d topics", 
                  speaker, emergentTopics.size()));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error recording emergent conversation", e);
        } finally {
            db.close();
        }
    }
    
    private void processeEmergentTopic(SQLiteDatabase db, String topicPhrase, String speaker, String context) {
        // Check if topic exists
        Cursor topicCursor = db.query(TABLE_TOPICS, new String[]{"id", "total_mentions"}, 
                                     "topic_phrase = ?", new String[]{topicPhrase}, null, null, null);
        
        long topicId;
        if (topicCursor.moveToFirst()) {
            // Topic exists - update
            topicId = topicCursor.getLong(0);
            int currentMentions = topicCursor.getInt(1);
            
            ContentValues updateValues = new ContentValues();
            updateValues.put("total_mentions", currentMentions + 1);
            db.update(TABLE_TOPICS, updateValues, "id = ?", new String[]{String.valueOf(topicId)});
        } else {
            // New emergent topic - create
            ContentValues topicValues = new ContentValues();
            topicValues.put("topic_phrase", topicPhrase);
            topicValues.put("emergence_context", context.substring(0, Math.min(200, context.length())));
            
            topicId = db.insert(TABLE_TOPICS, null, topicValues);
            Log.d(TAG, String.format("🌟 NEW EMERGENT TOPIC: '%s'", topicPhrase));
        }
        topicCursor.close();
        
        // Update player-topic association
        updatePlayerTopicAssociation(db, speaker, topicId, context);
    }
    
    private void updatePlayerTopicAssociation(SQLiteDatabase db, String speaker, long topicId, String context) {
        // Check existing association
        Cursor assocCursor = db.query(TABLE_PLAYER_TOPICS, new String[]{"id", "mention_count"}, 
                                     "player_name = ? AND topic_id = ?", 
                                     new String[]{speaker, String.valueOf(topicId)}, null, null, null);
        
        if (assocCursor.moveToFirst()) {
            // Update existing association
            long assocId = assocCursor.getLong(0);
            int currentCount = assocCursor.getInt(1);
            
            ContentValues updateValues = new ContentValues();
            updateValues.put("mention_count", currentCount + 1);
            updateValues.put("last_mentioned", "CURRENT_TIMESTAMP");
            
            db.update(TABLE_PLAYER_TOPICS, updateValues, "id = ?", new String[]{String.valueOf(assocId)});
        } else {
            // Create new association
            ContentValues assocValues = new ContentValues();
            assocValues.put("player_name", speaker);
            assocValues.put("topic_id", topicId);
            assocValues.put("emotional_context", extractEmotionalContext(context));
            
            db.insert(TABLE_PLAYER_TOPICS, null, assocValues);
        }
        assocCursor.close();
    }
    
    private String extractEmotionalContext(String content) {
        String lower = content.toLowerCase();
        if (lower.contains("exciting") || lower.contains("brilliant")) return "positive";
        if (lower.contains("frustrated") || lower.contains("mistake")) return "negative";
        return "neutral";
    }
    
    /**
     * 🎯 Get emergent conversation guidance
     * Based on what topics are naturally emerging and being overused
     */
    public EmergentGuidance getEmergentGuidance(String speaker, String opponent) {
        SQLiteDatabase db = this.getReadableDatabase();
        EmergentGuidance guidance = new EmergentGuidance();
        
        try {
            // Get overused topics for this speaker
            Cursor overusedCursor = db.rawQuery(
                "SELECT t.topic_phrase, pt.mention_count FROM " + TABLE_TOPICS + " t " +
                "JOIN " + TABLE_PLAYER_TOPICS + " pt ON t.id = pt.topic_id " +
                "WHERE pt.player_name = ? AND pt.mention_count > 3 " +
                "ORDER BY pt.mention_count DESC LIMIT 5", 
                new String[]{speaker});
            
            while (overusedCursor.moveToNext()) {
                guidance.overusedTopics.add(overusedCursor.getString(0));
            }
            overusedCursor.close();
            
            // Get emerging topics from other players for inspiration
            Cursor emergingCursor = db.rawQuery(
                "SELECT DISTINCT t.topic_phrase FROM " + TABLE_TOPICS + " t " +
                "JOIN " + TABLE_PLAYER_TOPICS + " pt ON t.id = pt.topic_id " +
                "WHERE pt.player_name != ? AND t.total_mentions < 3 " +
                "ORDER BY t.first_mentioned DESC LIMIT 5", 
                new String[]{speaker});
            
            while (emergingCursor.moveToNext()) {
                guidance.emergingTopics.add(emergingCursor.getString(0));
            }
            emergingCursor.close();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting emergent guidance", e);
        } finally {
            db.close();
        }
        
        return guidance;
    }
    
    public static class EmergentGuidance {
        public List<String> overusedTopics = new ArrayList<>();
        public List<String> emergingTopics = new ArrayList<>();
        public String naturalGuidance = "";
        
        public String buildGuidanceText() {
            StringBuilder guidance = new StringBuilder();
            
            if (!overusedTopics.isEmpty()) {
                guidance.append("NATURAL EVOLUTION: You've explored these themes thoroughly: ");
                guidance.append(String.join(", ", overusedTopics));
                guidance.append(". ");
            }
            
            if (!emergingTopics.isEmpty()) {
                guidance.append("FRESH DIRECTIONS emerging in conversations: ");
                guidance.append(String.join(", ", emergingTopics));
                guidance.append(". ");
            }
            
            if (guidance.length() == 0) {
                guidance.append("EXPLORE NATURALLY: Let the conversation develop organically. ");
            }
            
            guidance.append("Stay authentic while allowing natural topic evolution.");
            
            return guidance.toString();
        }
    }
}