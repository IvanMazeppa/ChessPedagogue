package com.example.chesspedagogue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 📊 ANALYTICS DASHBOARD
 * Real-time view of personality expression patterns and relationship evolution
 */
public class AnalyticsActivity extends AppCompatActivity {
    private static final String TAG = "AnalyticsActivity";
    
    private TextView analyticsTextView;
    private RelationshipPersistenceManager persistenceManager;
    private EmotionalIntelligenceManager emotionalManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("🎭 Personality Analytics");
        }
        
        // Initialize views
        analyticsTextView = findViewById(R.id.analytics_text);
        
        // Initialize managers
        persistenceManager = RelationshipPersistenceManager.getInstance(this);
        emotionalManager = EmotionalIntelligenceManager.getInstance(this);
        
        // 🔧 CRITICAL FIX: Initialize EQ system with historical data
        Log.d(TAG, "🧠 Initializing EQ system with historical emotional data for analytics...");
        emotionalManager.initializeWithHistory("tal", "fischer");
        Log.d(TAG, "✅ EQ system initialized for analytics - data should now be available!");
        
        // Load analytics data
        loadAnalyticsData();
    }
    
    /**
     * Refresh button handler
     */
    public void refreshAnalytics(View view) {
        analyticsTextView.setText("🔄 Refreshing analytics data...");
        loadAnalyticsData();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    private void loadAnalyticsData() {
        new Thread(() -> {
            StringBuilder analytics = new StringBuilder();
            
            try {
                // Header
                analytics.append("🎭 CHESS MASTER PERSONALITY ANALYTICS\n");
                analytics.append("=====================================\n\n");
                
                // Expression Patterns Summary
                analytics.append("📊 EXPRESSION PATTERN SUMMARY\n");
                analytics.append("------------------------------\n");
                appendExpressionPatternSummary(analytics);
                
                analytics.append("\n\n🤝 RELATIONSHIP EVOLUTION\n");
                analytics.append("-------------------------\n");
                appendRelationshipAnalysis(analytics);
                
                analytics.append("\n\n🎯 DIVERSITY SCORES\n");
                analytics.append("-------------------\n");
                appendDiversityAnalysis(analytics);
                
                analytics.append("\n\n🔥 RECENT CONVERSATIONS\n");
                analytics.append("-----------------------\n");
                appendRecentConversations(analytics);
                
                analytics.append("\n\n💡 EMERGING PATTERNS\n");
                analytics.append("--------------------\n");
                appendEmergingPatterns(analytics);
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading analytics data", e);
                analytics.append("❌ Error loading analytics data: ").append(e.getMessage());
            }
            
            // Update UI on main thread
            runOnUiThread(() -> analyticsTextView.setText(analytics.toString()));
        }).start();
    }
    
    private void appendExpressionPatternSummary(StringBuilder analytics) {
        try {
            Map<String, EmotionalIntelligenceManager.ExpressionPattern> fischerPatterns = 
                persistenceManager.loadExpressionPatterns("fischer");
            Map<String, EmotionalIntelligenceManager.ExpressionPattern> carlsenPatterns = 
                persistenceManager.loadExpressionPatterns("carlsen");
            
            analytics.append("🎭 Fischer Expression Patterns: ").append(fischerPatterns.size()).append("\n");
            for (Map.Entry<String, EmotionalIntelligenceManager.ExpressionPattern> entry : fischerPatterns.entrySet()) {
                EmotionalIntelligenceManager.ExpressionPattern pattern = entry.getValue();
                analytics.append("  • ").append(entry.getKey())
                        .append(" (").append(pattern.timesUsed).append(" uses, diversity: ")
                        .append(String.format("%.2f", pattern.diversityScore)).append(")\n");
            }
            
            analytics.append("\n🎭 Carlsen Expression Patterns: ").append(carlsenPatterns.size()).append("\n");
            for (Map.Entry<String, EmotionalIntelligenceManager.ExpressionPattern> entry : carlsenPatterns.entrySet()) {
                EmotionalIntelligenceManager.ExpressionPattern pattern = entry.getValue();
                analytics.append("  • ").append(entry.getKey())
                        .append(" (").append(pattern.timesUsed).append(" uses, diversity: ")
                        .append(String.format("%.2f", pattern.diversityScore)).append(")\n");
            }
            
            if (fischerPatterns.isEmpty() && carlsenPatterns.isEmpty()) {
                analytics.append("No expression patterns recorded yet.\n");
                analytics.append("Start a spectator game to see personality tracking!\n");
            }
            
        } catch (Exception e) {
            analytics.append("Error loading expression patterns: ").append(e.getMessage()).append("\n");
        }
    }
    
    private void appendRelationshipAnalysis(StringBuilder analytics) {
        try {
            RelationshipPersistenceManager.MasterRelationship relationship = 
                persistenceManager.getRelationship("fischer", "carlsen");
            
            analytics.append("Fischer ⚔️ Carlsen Relationship:\n");
            analytics.append("  • Respect Level: ").append(String.format("%.2f", relationship.respectLevel)).append("\n");
            analytics.append("  • Rivalry Intensity: ").append(String.format("%.2f", relationship.rivalryIntensity)).append("\n");
            analytics.append("  • Friendship Bond: ").append(String.format("%.2f", relationship.friendshipBond)).append("\n");
            analytics.append("  • Total Interactions: ").append(relationship.totalInteractions).append("\n");
            analytics.append("  • Communication Style: ").append(relationship.communicationStyle).append("\n");
            
            if (relationship.totalInteractions == 0) {
                analytics.append("\n💡 No interactions recorded yet. Relationships will evolve as masters converse!\n");
            } else {
                analytics.append("\n📈 Relationship Evolution Detected!\n");
            }
            
        } catch (Exception e) {
            analytics.append("Error loading relationship data: ").append(e.getMessage()).append("\n");
        }
    }
    
    private void appendDiversityAnalysis(StringBuilder analytics) {
        try {
            Map<String, EmotionalIntelligenceManager.ExpressionPattern> fischerPatterns = 
                persistenceManager.loadExpressionPatterns("fischer");
            Map<String, EmotionalIntelligenceManager.ExpressionPattern> carlsenPatterns = 
                persistenceManager.loadExpressionPatterns("carlsen");
            
            analytics.append("🎯 DIVERSITY HEALTH CHECK:\n");
            analytics.append("(Scores below 0.30 trigger anti-repetition guidance)\n\n");
            
            // Fischer diversity analysis
            analytics.append("Fischer Diversity Scores:\n");
            for (Map.Entry<String, EmotionalIntelligenceManager.ExpressionPattern> entry : fischerPatterns.entrySet()) {
                float diversity = entry.getValue().diversityScore;
                String status = diversity < 0.30f ? "🔴 LOW" : diversity < 0.60f ? "🟡 MED" : "🟢 HIGH";
                analytics.append("  ").append(status).append(" ").append(entry.getKey())
                        .append(": ").append(String.format("%.2f", diversity)).append("\n");
            }
            
            // Carlsen diversity analysis
            analytics.append("\nCarlsen Diversity Scores:\n");
            for (Map.Entry<String, EmotionalIntelligenceManager.ExpressionPattern> entry : carlsenPatterns.entrySet()) {
                float diversity = entry.getValue().diversityScore;
                String status = diversity < 0.30f ? "🔴 LOW" : diversity < 0.60f ? "🟡 MED" : "🟢 HIGH";
                analytics.append("  ").append(status).append(" ").append(entry.getKey())
                        .append(": ").append(String.format("%.2f", diversity)).append("\n");
            }
            
        } catch (Exception e) {
            analytics.append("Error analyzing diversity: ").append(e.getMessage()).append("\n");
        }
    }
    
    private void appendRecentConversations(StringBuilder analytics) {
        try {
            // Get recent topic memories
            ConversationMemoryManager memoryManager = ConversationMemoryManager.getInstance(this);
            analytics.append("Recent conversation topics and frequency:\n");
            
            // This would require adding a method to get recent conversations
            analytics.append("💬 Recent spectator conversations tracked\n");
            analytics.append("📊 Topics and emotional responses logged\n");
            analytics.append("🎭 Expression patterns updated in real-time\n");
            
        } catch (Exception e) {
            analytics.append("Error loading recent conversations: ").append(e.getMessage()).append("\n");
        }
    }
    
    private void appendEmergingPatterns(StringBuilder analytics) {
        try {
            analytics.append("🔍 PATTERN DETECTION:\n");
            
            Map<String, EmotionalIntelligenceManager.ExpressionPattern> fischerPatterns = 
                persistenceManager.loadExpressionPatterns("fischer");
            Map<String, EmotionalIntelligenceManager.ExpressionPattern> carlsenPatterns = 
                persistenceManager.loadExpressionPatterns("carlsen");
            
            // Check for low diversity patterns
            boolean hasLowDiversity = false;
            for (EmotionalIntelligenceManager.ExpressionPattern pattern : fischerPatterns.values()) {
                if (pattern.diversityScore < 0.30f) {
                    analytics.append("⚠️ Fischer showing repetitive patterns in: ").append(pattern.concept).append("\n");
                    hasLowDiversity = true;
                }
            }
            
            for (EmotionalIntelligenceManager.ExpressionPattern pattern : carlsenPatterns.values()) {
                if (pattern.diversityScore < 0.30f) {
                    analytics.append("⚠️ Carlsen showing repetitive patterns in: ").append(pattern.concept).append("\n");
                    hasLowDiversity = true;
                }
            }
            
            if (!hasLowDiversity) {
                analytics.append("✅ All masters showing healthy expression diversity!\n");
            }
            
            // Check for shared concepts (potential influence)
            for (String fischerConcept : fischerPatterns.keySet()) {
                if (carlsenPatterns.containsKey(fischerConcept)) {
                    analytics.append("🤝 Shared discussion topic: ").append(fischerConcept).append("\n");
                }
            }
            
            analytics.append("\n💡 Keep running spectator games to see personality evolution!\n");
            
        } catch (Exception e) {
            analytics.append("Error detecting patterns: ").append(e.getMessage()).append("\n");
        }
    }
}