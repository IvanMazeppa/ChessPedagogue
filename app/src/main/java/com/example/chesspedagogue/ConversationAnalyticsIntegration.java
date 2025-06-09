package com.example.chesspedagogue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * ConversationAnalyticsIntegration - Bridge between Android app and analytics system
 * 
 * This class provides the analytics data that can be consumed by the JavaFX
 * configurator or other external analytics tools to track conversation evolution.
 */
public class ConversationAnalyticsIntegration {
    private static final String TAG = "ConversationAnalytics";
    
    private final Context context;
    private final GameDatabaseHelper databaseHelper;
    private ConversationAnalytics analytics;
    
    public ConversationAnalyticsIntegration(Context context) {
        this.context = context;
        this.databaseHelper = new GameDatabaseHelper(context);
        this.analytics = new ConversationAnalytics(context, databaseHelper);
    }
    
    /**
     * Generate analytics report for a specific master
     */
    public String generateAnalyticsReport(String masterName) {
        try {
            ConversationAnalytics.SophisticationAnalysis analysis = 
                analytics.analyzeMasterSophistication(masterName);
            
            return analytics.generateDetailedReport(masterName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating analytics report for " + masterName, e);
            return "Error generating report: " + e.getMessage();
        }
    }
    
    /**
     * Get JSON data for external analytics tools
     */
    public String getAnalyticsDataAsJson(String masterName) {
        try {
            ConversationAnalytics.SophisticationAnalysis analysis = 
                analytics.analyzeMasterSophistication(masterName);
            
            JSONObject json = new JSONObject();
            json.put("masterName", analysis.masterName);
            json.put("sophisticationLevel", analysis.getSophisticationLevel());
            json.put("overallScore", analysis.getOverallSophistication());
            json.put("diversityScore", analysis.diversityScore);
            json.put("emotionalRange", analysis.emotionalRange);
            json.put("relationshipDepth", analysis.relationshipDepth);
            json.put("memoryRichness", analysis.memoryRichness);
            json.put("totalInteractions", analysis.totalInteractions);
            
            JSONArray repetitive = new JSONArray();
            for (String topic : analysis.repetitiveTopics) {
                repetitive.put(topic);
            }
            json.put("repetitiveTopics", repetitive);
            
            JSONArray emergent = new JSONArray();
            for (String behavior : analysis.emergentBehaviors) {
                emergent.put(behavior);
            }
            json.put("emergentBehaviors", emergent);
            
            return json.toString(2);
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating JSON analytics for " + masterName, e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Compare all masters and return results
     */
    public String compareAllMasters() {
        try {
            Map<String, ConversationAnalytics.SophisticationAnalysis> comparisons = 
                analytics.compareAllMasters();
            
            StringBuilder report = new StringBuilder();
            report.append("📊 MASTER SOPHISTICATION COMPARISON\n");
            report.append("==================================\n\n");
            
            // Sort by sophistication level
            comparisons.entrySet().stream()
                .sorted((a, b) -> Float.compare(
                    b.getValue().getOverallSophistication(), 
                    a.getValue().getOverallSophistication()))
                .forEach(entry -> {
                    String master = entry.getKey();
                    ConversationAnalytics.SophisticationAnalysis analysis = entry.getValue();
                    
                    report.append(String.format("🎭 %s: %.3f (%s)\n", 
                        master.toUpperCase(), 
                        analysis.getOverallSophistication(),
                        analysis.getSophisticationLevel()));
                    
                    if (!analysis.repetitiveTopics.isEmpty()) {
                        report.append("   ⚠️ Repetitive: ").append(analysis.repetitiveTopics.size()).append(" topics\n");
                    }
                    
                    if (!analysis.emergentBehaviors.isEmpty()) {
                        report.append("   🌟 Emergent: ").append(analysis.emergentBehaviors.size()).append(" behaviors\n");
                    }
                    
                    report.append("\n");
                });
            
            report.append("💡 Higher scores indicate more sophisticated, varied conversations\n");
            report.append("   that adapt based on relationships and emotional memory.\n");
            
            return report.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Error comparing all masters", e);
            return "Error generating comparison: " + e.getMessage();
        }
    }
    
    /**
     * Get evolution timeline for a master
     */
    public String getEvolutionTimeline(String masterName) {
        try {
            ConversationAnalytics.EvolutionTimeline timeline = 
                analytics.generateEvolutionTimeline(masterName);
            
            StringBuilder report = new StringBuilder();
            report.append("📈 SOPHISTICATION EVOLUTION: ").append(masterName.toUpperCase()).append("\n");
            report.append("=============================================\n\n");
            
            if (timeline.timeline.isEmpty()) {
                report.append("No conversation data available yet.\n");
                report.append("💡 Start some spectator games to build conversation history!\n");
            } else {
                for (ConversationAnalytics.EvolutionTimeline.TimePoint point : timeline.timeline) {
                    report.append(String.format("🕐 %s: %.3f sophistication (%d topics, %d emotions)\n",
                        new java.util.Date(point.timestamp).toString(),
                        point.sophisticationScore,
                        point.topicCount,
                        point.uniqueEmotions));
                    
                    if (point.significantEvent != null && !point.significantEvent.isEmpty()) {
                        report.append("   📝 ").append(point.significantEvent).append("\n");
                    }
                    report.append("\n");
                }
            }
            
            return report.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating evolution timeline for " + masterName, e);
            return "Error generating timeline: " + e.getMessage();
        }
    }
    
    /**
     * Debug method to trigger analytics update (for testing)
     */
    public void triggerAnalyticsUpdate() {
        Log.d(TAG, "🔄 Triggering analytics update...");
        
        // This could trigger a background task to refresh analytics
        // Or send data to external analytics dashboard
        Intent intent = new Intent("com.example.chesspedagogue.ANALYTICS_UPDATE");
        context.sendBroadcast(intent);
    }
    
    /**
     * Get quick sophistication summary for UI display
     */
    public String getQuickSummary(String masterName) {
        try {
            ConversationAnalytics.SophisticationAnalysis analysis = 
                analytics.analyzeMasterSophistication(masterName);
            
            return String.format("%s: %.2f (%s)", 
                masterName, 
                analysis.getOverallSophistication(),
                analysis.getSophisticationLevel());
                
        } catch (Exception e) {
            Log.e(TAG, "Error getting quick summary for " + masterName, e);
            return masterName + ": Error";
        }
    }
    
    /**
     * Check if a master shows signs of repetitive conversation
     */
    public boolean isRepetitive(String masterName) {
        try {
            ConversationAnalytics.SophisticationAnalysis analysis = 
                analytics.analyzeMasterSophistication(masterName);
            
            return analysis.diversityScore < 0.4f || !analysis.repetitiveTopics.isEmpty();
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking repetitiveness for " + masterName, e);
            return false;
        }
    }
    
    /**
     * Get improvement suggestions for a master
     */
    public String getImprovementSuggestions(String masterName) {
        try {
            ConversationAnalytics.SophisticationAnalysis analysis = 
                analytics.analyzeMasterSophistication(masterName);
            
            StringBuilder suggestions = new StringBuilder();
            suggestions.append("💡 IMPROVEMENT SUGGESTIONS FOR ").append(masterName.toUpperCase()).append(":\n\n");
            
            if (analysis.diversityScore < 0.5f) {
                suggestions.append("📚 Add more varied conversation topics and responses\n");
            }
            
            if (analysis.emotionalRange < 0.4f) {
                suggestions.append("🎭 Expand emotional expression range in conversations\n");
            }
            
            if (analysis.relationshipDepth < 0.3f) {
                suggestions.append("👥 Develop deeper relationships through more interactions\n");
            }
            
            if (analysis.memoryRichness < 0.4f) {
                suggestions.append("🧠 Add more historical context and position data\n");
            }
            
            if (analysis.totalInteractions < 50) {
                suggestions.append("🎮 Increase total interactions through spectator games\n");
            }
            
            if (!analysis.repetitiveTopics.isEmpty()) {
                suggestions.append("⚠️ Address repetitive topics: ").append(analysis.repetitiveTopics.size()).append(" detected\n");
            }
            
            if (suggestions.length() == 0) {
                suggestions.append("✅ This master shows good conversation sophistication!\n");
                suggestions.append("   Continue developing through varied interactions.\n");
            }
            
            return suggestions.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating improvement suggestions for " + masterName, e);
            return "Error generating suggestions: " + e.getMessage();
        }
    }
}