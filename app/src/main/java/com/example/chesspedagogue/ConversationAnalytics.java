package com.example.chesspedagogue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConversationAnalytics - Track conversation evolution and sophistication over time
 * 
 * This system analyzes how AI masters evolve from repetitive responses to rich,
 * contextual conversations as their databases populate with relationships,
 * memories, and emotional history.
 * 
 * Key metrics:
 * - Topic diversity and repetition patterns
 * - Emotional range and authenticity
 * - Relationship complexity evolution
 * - Conversation breakthrough moments
 */
public class ConversationAnalytics {
    private static final String TAG = "ConversationAnalytics";
    
    private final GameDatabaseHelper databaseHelper;
    private final Context context;
    
    public ConversationAnalytics(Context context, GameDatabaseHelper databaseHelper) {
        this.context = context;
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Analyze a master's conversation sophistication level
     */
    public static class SophisticationAnalysis {
        public final String masterName;
        public final float diversityScore;        // 0.0 = very repetitive, 1.0 = highly varied
        public final float emotionalRange;       // How many different emotions expressed
        public final float relationshipDepth;    // How complex their relationships are
        public final float memoryRichness;      // How much historical context they use
        public final int totalInteractions;
        public final List<String> repetitiveTopics;
        public final List<String> emergentBehaviors;
        
        public SophisticationAnalysis(String masterName, float diversityScore, 
                                    float emotionalRange, float relationshipDepth, 
                                    float memoryRichness, int totalInteractions,
                                    List<String> repetitiveTopics, List<String> emergentBehaviors) {
            this.masterName = masterName;
            this.diversityScore = diversityScore;
            this.emotionalRange = emotionalRange;
            this.relationshipDepth = relationshipDepth;
            this.memoryRichness = memoryRichness;
            this.totalInteractions = totalInteractions;
            this.repetitiveTopics = repetitiveTopics;
            this.emergentBehaviors = emergentBehaviors;
        }
        
        public float getOverallSophistication() {
            return (diversityScore + emotionalRange + relationshipDepth + memoryRichness) / 4.0f;
        }
        
        public String getSophisticationLevel() {
            float score = getOverallSophistication();
            if (score >= 0.8f) return "Highly Sophisticated";
            if (score >= 0.6f) return "Moderately Sophisticated";
            if (score >= 0.4f) return "Developing";
            if (score >= 0.2f) return "Basic";
            return "Repetitive";
        }
    }
    
    /**
     * Track how a master's responses evolve over time
     */
    public static class EvolutionTimeline {
        public final List<TimePoint> timeline;
        
        public static class TimePoint {
            public final long timestamp;
            public final float sophisticationScore;
            public final int topicCount;
            public final int uniqueEmotions;
            public final String significantEvent;
            
            public TimePoint(long timestamp, float sophisticationScore, int topicCount, 
                           int uniqueEmotions, String significantEvent) {
                this.timestamp = timestamp;
                this.sophisticationScore = sophisticationScore;
                this.topicCount = topicCount;
                this.uniqueEmotions = uniqueEmotions;
                this.significantEvent = significantEvent;
            }
        }
        
        public EvolutionTimeline(List<TimePoint> timeline) {
            this.timeline = timeline;
        }
    }
    
    /**
     * Main analysis method - generate comprehensive sophistication report
     */
    public SophisticationAnalysis analyzeMasterSophistication(String masterName) {
        Log.d(TAG, "🔍 Analyzing conversation sophistication for " + masterName);
        
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            // 1. Calculate topic diversity score
            float diversityScore = calculateTopicDiversity(db, masterName);
            
            // 2. Calculate emotional range
            float emotionalRange = calculateEmotionalRange(db, masterName);
            
            // 3. Calculate relationship depth
            float relationshipDepth = calculateRelationshipDepth(db, masterName);
            
            // 4. Calculate memory richness
            float memoryRichness = calculateMemoryRichness(db, masterName);
            
            // 5. Get total interactions
            int totalInteractions = getTotalInteractions(db, masterName);
            
            // 6. Find repetitive topics
            List<String> repetitiveTopics = findRepetitiveTopics(db, masterName);
            
            // 7. Find emergent behaviors
            List<String> emergentBehaviors = findEmergentBehaviors(db, masterName);
            
            SophisticationAnalysis analysis = new SophisticationAnalysis(
                masterName, diversityScore, emotionalRange, relationshipDepth,
                memoryRichness, totalInteractions, repetitiveTopics, emergentBehaviors
            );
            
            Log.d(TAG, String.format("📊 %s sophistication: %.2f (%s)", 
                masterName, analysis.getOverallSophistication(), analysis.getSophisticationLevel()));
            
            return analysis;
            
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing sophistication for " + masterName, e);
            return new SophisticationAnalysis(masterName, 0.0f, 0.0f, 0.0f, 0.0f, 0, 
                new ArrayList<>(), new ArrayList<>());
        }
    }
    
    /**
     * Calculate how varied a master's topic usage is (prevents "perfectionism all the time")
     */
    private float calculateTopicDiversity(SQLiteDatabase db, String masterName) {
        try {
            // Get expression patterns diversity scores
            String diversityQuery = "SELECT AVG(diversity_score) FROM expression_patterns WHERE master_name = ?";
            Cursor cursor = db.rawQuery(diversityQuery, new String[]{masterName});
            
            float avgDiversity = 0.5f; // Default baseline
            if (cursor.moveToFirst()) {
                avgDiversity = cursor.getFloat(0);
            }
            cursor.close();
            
            // Get topic usage distribution to check for over-used topics
            String topicQuery = "SELECT topic, discussion_count FROM topic_memory WHERE master1 = ? OR master2 = ?";
            cursor = db.rawQuery(topicQuery, new String[]{masterName, masterName});
            
            List<Integer> usageCounts = new ArrayList<>();
            while (cursor.moveToNext()) {
                usageCounts.add(cursor.getInt(1));
            }
            cursor.close();
            
            // Calculate variance in topic usage (high variance = some topics overused)
            float variancePenalty = calculateUsageVariance(usageCounts);
            
            float diversityScore = Math.max(0.0f, avgDiversity - variancePenalty);
            Log.d(TAG, String.format("📈 %s topic diversity: %.3f (avg: %.3f, penalty: %.3f)", 
                masterName, diversityScore, avgDiversity, variancePenalty));
            
            return diversityScore;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating topic diversity", e);
            return 0.5f;
        }
    }
    
    /**
     * Calculate emotional range - how many different emotions the master expresses
     */
    private float calculateEmotionalRange(SQLiteDatabase db, String masterName) {
        try {
            String emotionQuery = "SELECT COUNT(DISTINCT emotion) FROM emotional_reactions WHERE master = ?";
            Cursor cursor = db.rawQuery(emotionQuery, new String[]{masterName});
            
            int uniqueEmotions = 0;
            if (cursor.moveToFirst()) {
                uniqueEmotions = cursor.getInt(0);
            }
            cursor.close();
            
            // Max expected emotions: analytical, excited, frustrated, confident, disappointed, etc.
            float emotionalRange = Math.min(1.0f, uniqueEmotions / 8.0f);
            
            Log.d(TAG, String.format("🎭 %s emotional range: %.3f (%d unique emotions)", 
                masterName, emotionalRange, uniqueEmotions));
            
            return emotionalRange;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating emotional range", e);
            return 0.0f;
        }
    }
    
    /**
     * Calculate relationship complexity - how developed their relationships are
     */
    private float calculateRelationshipDepth(SQLiteDatabase db, String masterName) {
        try {
            String relationshipQuery = "SELECT AVG(respect_level + rivalry_intensity + friendship_bond), " +
                "AVG(total_interactions) FROM master_relationships WHERE master1 = ? OR master2 = ?";
            Cursor cursor = db.rawQuery(relationshipQuery, new String[]{masterName, masterName});
            
            float avgRelationshipScore = 0.0f;
            float avgInteractions = 0.0f;
            
            if (cursor.moveToFirst()) {
                avgRelationshipScore = cursor.getFloat(0);
                avgInteractions = cursor.getFloat(1);
            }
            cursor.close();
            
            // Normalize relationship depth (higher interactions + emotional bonds = deeper relationships)
            float relationshipDepth = Math.min(1.0f, (avgRelationshipScore / 3.0f) * 0.7f + 
                Math.min(avgInteractions / 50.0f, 1.0f) * 0.3f);
            
            Log.d(TAG, String.format("👥 %s relationship depth: %.3f (score: %.2f, interactions: %.1f)", 
                masterName, relationshipDepth, avgRelationshipScore, avgInteractions));
            
            return relationshipDepth;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating relationship depth", e);
            return 0.0f;
        }
    }
    
    /**
     * Calculate how much historical/contextual memory the master uses
     */
    private float calculateMemoryRichness(SQLiteDatabase db, String masterName) {
        try {
            // Check if master has position data (personality engine)
            int positionCount = databaseHelper.getMasterPositionCount(masterName);
            
            // Check conversation evolution (topic innovation)
            String evolutionQuery = "SELECT COUNT(*) FROM conversation_evolution WHERE master1 = ? OR master2 = ?";
            Cursor cursor = db.rawQuery(evolutionQuery, new String[]{masterName, masterName});
            
            int evolutionCount = 0;
            if (cursor.moveToFirst()) {
                evolutionCount = cursor.getInt(0);
            }
            cursor.close();
            
            // Check emergent events
            String emergentQuery = "SELECT COUNT(*) FROM emergent_events WHERE master1 = ? OR master2 = ?";
            cursor = db.rawQuery(emergentQuery, new String[]{masterName, masterName});
            
            int emergentCount = 0;
            if (cursor.moveToFirst()) {
                emergentCount = cursor.getInt(0);
            }
            cursor.close();
            
            // Combine historical data, conversation innovation, and emergent behaviors
            float memoryRichness = Math.min(1.0f, 
                (positionCount > 0 ? 0.4f : 0.0f) +  // Has historical games
                Math.min(evolutionCount / 10.0f, 0.3f) +  // Topic evolution
                Math.min(emergentCount / 5.0f, 0.3f)       // Emergent behaviors
            );
            
            Log.d(TAG, String.format("🧠 %s memory richness: %.3f (positions: %d, evolution: %d, emergent: %d)", 
                masterName, memoryRichness, positionCount, evolutionCount, emergentCount));
            
            return memoryRichness;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating memory richness", e);
            return 0.0f;
        }
    }
    
    /**
     * Get total interactions for baseline metrics
     */
    private int getTotalInteractions(SQLiteDatabase db, String masterName) {
        try {
            String query = "SELECT SUM(total_interactions) FROM master_relationships WHERE master1 = ? OR master2 = ?";
            Cursor cursor = db.rawQuery(query, new String[]{masterName, masterName});
            
            int total = 0;
            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }
            cursor.close();
            
            return total;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting total interactions", e);
            return 0;
        }
    }
    
    /**
     * Find topics that are overused (repetitive patterns)
     */
    private List<String> findRepetitiveTopics(SQLiteDatabase db, String masterName) {
        List<String> repetitiveTopics = new ArrayList<>();
        
        try {
            // Find topics with high fatigue levels or excessive usage
            String query = "SELECT topic, discussion_count, topic_fatigue_level FROM topic_memory " +
                "WHERE (master1 = ? OR master2 = ?) AND (topic_fatigue_level > 0.7 OR discussion_count > 10) " +
                "ORDER BY topic_fatigue_level DESC, discussion_count DESC LIMIT 5";
            
            Cursor cursor = db.rawQuery(query, new String[]{masterName, masterName});
            
            while (cursor.moveToNext()) {
                String topic = cursor.getString(0);
                int count = cursor.getInt(1);
                float fatigue = cursor.getFloat(2);
                
                repetitiveTopics.add(String.format("%s (used %d times, fatigue: %.2f)", topic, count, fatigue));
            }
            cursor.close();
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding repetitive topics", e);
        }
        
        return repetitiveTopics;
    }
    
    /**
     * Find emergent behaviors and breakthrough moments
     */
    private List<String> findEmergentBehaviors(SQLiteDatabase db, String masterName) {
        List<String> emergentBehaviors = new ArrayList<>();
        
        try {
            // Find significant emergent events
            String query = "SELECT event_type, description, impact_level FROM emergent_events " +
                "WHERE (master1 = ? OR master2 = ?) AND impact_level > 0.5 " +
                "ORDER BY impact_level DESC, timestamp DESC LIMIT 5";
            
            Cursor cursor = db.rawQuery(query, new String[]{masterName, masterName});
            
            while (cursor.moveToNext()) {
                String eventType = cursor.getString(0);
                String description = cursor.getString(1);
                float impact = cursor.getFloat(2);
                
                emergentBehaviors.add(String.format("%s: %s (impact: %.2f)", eventType, description, impact));
            }
            cursor.close();
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding emergent behaviors", e);
        }
        
        return emergentBehaviors;
    }
    
    /**
     * Calculate variance in topic usage to detect over-repetition
     */
    private float calculateUsageVariance(List<Integer> usageCounts) {
        if (usageCounts.size() < 2) return 0.0f;
        
        float mean = 0.0f;
        for (int count : usageCounts) {
            mean += count;
        }
        mean /= usageCounts.size();
        
        float variance = 0.0f;
        for (int count : usageCounts) {
            variance += Math.pow(count - mean, 2);
        }
        variance /= usageCounts.size();
        
        // Normalize variance as penalty (high variance = uneven distribution = repetitive)
        return Math.min(0.3f, variance / (mean * mean) * 0.1f);
    }
    
    /**
     * Generate evolution timeline to show conversation improvement over time
     */
    public EvolutionTimeline generateEvolutionTimeline(String masterName) {
        List<EvolutionTimeline.TimePoint> timeline = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            // Get emotional reactions over time as proxy for conversation activity
            String timelineQuery = "SELECT timestamp, emotion, topic FROM emotional_reactions " +
                "WHERE master = ? ORDER BY timestamp ASC";
            
            Cursor cursor = db.rawQuery(timelineQuery, new String[]{masterName});
            
            long lastTimestamp = 0;
            int topicCount = 0;
            Map<String, Boolean> uniqueEmotions = new HashMap<>();
            
            while (cursor.moveToNext()) {
                long timestamp = cursor.getLong(0);
                String emotion = cursor.getString(1);
                String topic = cursor.getString(2);
                
                // Create timeline points at regular intervals
                if (timestamp - lastTimestamp > 300000) { // 5 minutes
                    topicCount++;
                    uniqueEmotions.put(emotion, true);
                    
                    // Calculate sophistication at this point
                    float sophistication = Math.min(1.0f, 
                        (topicCount / 10.0f * 0.4f) + 
                        (uniqueEmotions.size() / 8.0f * 0.6f));
                    
                    timeline.add(new EvolutionTimeline.TimePoint(
                        timestamp, sophistication, topicCount, 
                        uniqueEmotions.size(), "Emotional interaction: " + emotion
                    ));
                    
                    lastTimestamp = timestamp;
                }
            }
            cursor.close();
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating evolution timeline", e);
        }
        
        return new EvolutionTimeline(timeline);
    }
    
    /**
     * Compare sophistication between masters
     */
    public Map<String, SophisticationAnalysis> compareAllMasters() {
        Map<String, SophisticationAnalysis> analyses = new HashMap<>();
        
        String[] masters = {"tal", "fischer", "carlsen", "kasparov", "alekhine", 
                          "capablanca", "kramnik", "karpov", "anand", "lasker", "morphy", "botvinnik"};
        
        for (String master : masters) {
            analyses.put(master, analyzeMasterSophistication(master));
        }
        
        return analyses;
    }
    
    /**
     * Generate a detailed report for debugging conversation quality
     */
    public String generateDetailedReport(String masterName) {
        SophisticationAnalysis analysis = analyzeMasterSophistication(masterName);
        
        StringBuilder report = new StringBuilder();
        report.append("📊 CONVERSATION SOPHISTICATION REPORT\n");
        report.append("=====================================\n\n");
        
        report.append("Master: ").append(masterName.toUpperCase()).append("\n");
        report.append("Overall Level: ").append(analysis.getSophisticationLevel()).append("\n");
        report.append("Sophistication Score: ").append(String.format("%.3f", analysis.getOverallSophistication())).append("/1.000\n\n");
        
        report.append("📈 DETAILED METRICS:\n");
        report.append("- Topic Diversity: ").append(String.format("%.3f", analysis.diversityScore)).append("/1.000\n");
        report.append("- Emotional Range: ").append(String.format("%.3f", analysis.emotionalRange)).append("/1.000\n");
        report.append("- Relationship Depth: ").append(String.format("%.3f", analysis.relationshipDepth)).append("/1.000\n");
        report.append("- Memory Richness: ").append(String.format("%.3f", analysis.memoryRichness)).append("/1.000\n");
        report.append("- Total Interactions: ").append(analysis.totalInteractions).append("\n\n");
        
        if (!analysis.repetitiveTopics.isEmpty()) {
            report.append("⚠️ REPETITIVE TOPICS:\n");
            for (String topic : analysis.repetitiveTopics) {
                report.append("- ").append(topic).append("\n");
            }
            report.append("\n");
        }
        
        if (!analysis.emergentBehaviors.isEmpty()) {
            report.append("🌟 EMERGENT BEHAVIORS:\n");
            for (String behavior : analysis.emergentBehaviors) {
                report.append("- ").append(behavior).append("\n");
            }
            report.append("\n");
        }
        
        // Add improvement suggestions
        report.append("💡 IMPROVEMENT SUGGESTIONS:\n");
        if (analysis.diversityScore < 0.5f) {
            report.append("- Increase topic variety to reduce repetitive responses\n");
        }
        if (analysis.emotionalRange < 0.4f) {
            report.append("- Expand emotional expression range\n");
        }
        if (analysis.relationshipDepth < 0.3f) {
            report.append("- Develop deeper relationships with other masters\n");
        }
        if (analysis.memoryRichness < 0.4f) {
            report.append("- Add more historical context and position data\n");
        }
        
        String result = report.toString();
        Log.d(TAG, "📋 Generated detailed report for " + masterName + ":\n" + result);
        
        return result;
    }
}