package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * 🧪 CONVERSATION FLOW TESTING UTILITY
 * 
 * Rapid prototyping and testing of conversation schemas.
 * Perfect for finding the right balance for spectator mode!
 */
public class ConversationFlowTester {
    private static final String TAG = "ConversationFlowTester";
    
    /**
     * 🚀 QUICK TEST: Switch to engaging conversations for spectator mode
     */
    public static void enableEngagingConversations() {
        ConversationSchemaTemplate.useEngagingConversations();
        Log.d(TAG, "🎭 CONVERSATION MODE: Engaging (3-6 opening turns, 4-8 analysis turns, 85% response rate)");
        Log.d(TAG, "📊 " + ConversationSchemaTemplate.getCurrentTemplateStats());
    }
    
    /**
     * 🔥 QUICK TEST: Switch to intense conversations for maximum drama
     */
    public static void enableIntenseConversations() {
        ConversationSchemaTemplate.useIntenseConversations();
        Log.d(TAG, "🎭 CONVERSATION MODE: Intense (4-10 opening turns, 5-12 analysis turns, 90% response rate)");
        Log.d(TAG, "📊 " + ConversationSchemaTemplate.getCurrentTemplateStats());
    }
    
    /**
     * 📚 QUICK TEST: Switch to collaborative conversations for educational style
     */
    public static void enableCollaborativeConversations() {
        ConversationSchemaTemplate.useCollaborativeConversations();
        Log.d(TAG, "🎭 CONVERSATION MODE: Collaborative (3-7 opening turns, 4-9 analysis turns, 80% response rate)");
        Log.d(TAG, "📊 " + ConversationSchemaTemplate.getCurrentTemplateStats());
    }
    
    /**
     * 🔧 QUICK TEST: Revert to minimal conversations (current conservative behavior)
     */
    public static void revertToMinimalConversations() {
        ConversationSchemaTemplate.useMinimalConversations();
        Log.d(TAG, "🎭 CONVERSATION MODE: Minimal (1-2 opening turns, 1-3 analysis turns, 50% response rate)");
        Log.d(TAG, "📊 " + ConversationSchemaTemplate.getCurrentTemplateStats());
    }
    
    /**
     * 🎯 CUSTOM TEST: Create and test specific conversation parameters
     */
    public static void testCustomConversation(String name, int minTurns, int maxTurns, 
                                            double responseChance, long intervalMs) {
        ConversationSchemaTemplate.ConversationTemplate customTemplate = 
            ConversationSchemaTemplate.createCustomTemplate(name, minTurns, maxTurns, responseChance, intervalMs);
        
        Log.d(TAG, "🧪 TESTING CUSTOM CONVERSATION: " + name);
        Log.d(TAG, "📋 Parameters: " + minTurns + "-" + maxTurns + " turns, " + 
              (responseChance * 100) + "% response rate, " + intervalMs + "ms intervals");
    }
    
    /**
     * 📊 SIMULATION: Test conversation flow without actual AI calls
     */
    public static void simulateConversationFlow(String triggerType, int maxSimulatedTurns) {
        Log.d(TAG, "🔬 SIMULATING CONVERSATION FLOW:");
        Log.d(TAG, "🎯 Trigger: " + triggerType);
        
        ConversationSchema.Schema schema = ConversationSchemaTemplate.getSchemaForTrigger(triggerType);
        
        Log.d(TAG, "📋 Schema: " + schema.minTurns + "-" + schema.maxTurns + " turns, " + 
              (schema.responseChance * 100) + "% response chance");
        
        int turnCount = 0;
        boolean continuing = true;
        
        while (continuing && turnCount < maxSimulatedTurns) {
            turnCount++;
            continuing = ConversationSchema.shouldRespondToStatement(schema, "test statement", turnCount);
            
            String status = continuing ? "✅ CONTINUE" : "🛑 STOP";
            Log.d(TAG, String.format("  Turn %d: %s (Current: %d/%d-%d)", 
                  turnCount, status, turnCount, schema.minTurns, schema.maxTurns));
        }
        
        Log.d(TAG, "🏁 Conversation ended after " + turnCount + " turns");
    }
    
    /**
     * 🧪 BATCH TEST: Run simulations with all templates
     */
    public static void runBatchConversationTests() {
        Log.d(TAG, "🧪 RUNNING BATCH CONVERSATION TESTS:");
        
        String[] templates = {"minimal", "engaging", "intense", "collaborative"};
        String[] triggers = {"opening", "brilliant_move", "position_change", "endgame"};
        
        for (String template : templates) {
            ConversationSchemaTemplate.setConversationTemplate(template);
            Log.d(TAG, "\n📋 TESTING TEMPLATE: " + template.toUpperCase());
            
            for (String trigger : triggers) {
                Log.d(TAG, "  🎯 Trigger: " + trigger);
                simulateConversationFlow(trigger, 15); // Max 15 simulated turns
            }
        }
        
        // Reset to engaging for actual use
        ConversationSchemaTemplate.useEngagingConversations();
        Log.d(TAG, "\n✅ BATCH TESTS COMPLETE - Reset to engaging conversations");
    }
    
    /**
     * 🎛️ QUICK CONVERSATION INTENSITY ADJUSTMENT
     */
    public static void adjustConversationIntensity(int level) {
        switch (level) {
            case 1:
                revertToMinimalConversations();
                Log.d(TAG, "🔧 INTENSITY LEVEL 1: Minimal conversations");
                break;
            case 2:
                enableCollaborativeConversations();
                Log.d(TAG, "🔧 INTENSITY LEVEL 2: Collaborative conversations");
                break;
            case 3:
                enableEngagingConversations();
                Log.d(TAG, "🔧 INTENSITY LEVEL 3: Engaging conversations (RECOMMENDED)");
                break;
            case 4:
                enableIntenseConversations();
                Log.d(TAG, "🔧 INTENSITY LEVEL 4: Intense conversations");
                break;
            case 5:
                testCustomConversation("Maximum Drama", 6, 15, 0.95, 1500);
                Log.d(TAG, "🔧 INTENSITY LEVEL 5: Maximum drama mode");
                break;
            default:
                enableEngagingConversations();
                Log.d(TAG, "🔧 DEFAULT: Engaging conversations (level 3)");
        }
    }
    
    /**
     * 📈 DIAGNOSTIC: Get current conversation health metrics
     */
    public static void logConversationHealthMetrics() {
        Log.d(TAG, "🏥 CONVERSATION HEALTH METRICS:");
        Log.d(TAG, ConversationSchemaTemplate.getCurrentTemplateStats());
        
        // Test all trigger types
        String[] triggers = {"opening", "brilliant_move", "position_change", "endgame"};
        for (String trigger : triggers) {
            ConversationSchema.Schema schema = ConversationSchemaTemplate.getSchemaForTrigger(trigger);
            Log.d(TAG, String.format("  📊 %s: %d-%d turns, %.0f%% response chance, %dms intervals",
                  trigger, schema.minTurns, schema.maxTurns, 
                  schema.responseChance * 100, schema.minInterval));
        }
    }
    
    /**
     * 🎭 SPECTATOR MODE OPTIMIZATION: Find perfect balance for spectator games
     */
    public static void optimizeForSpectatorMode() {
        Log.d(TAG, "🎭 OPTIMIZING CONVERSATIONS FOR SPECTATOR MODE:");
        
        // Test current template effectiveness
        Log.d(TAG, "📊 Current template analysis:");
        logConversationHealthMetrics();
        
        // Run quick simulations to check conversation length
        Log.d(TAG, "\n🔬 Running spectator mode simulation:");
        simulateConversationFlow("opening", 10);
        simulateConversationFlow("position_change", 10);
        
        // Recommend optimal settings
        Log.d(TAG, "\n💡 SPECTATOR MODE RECOMMENDATIONS:");
        Log.d(TAG, "  🎯 Use 'engaging' template for balanced multi-turn dialogues");
        Log.d(TAG, "  🎯 Opening conversations: 3-6 turns (current spectator games start strong)");
        Log.d(TAG, "  🎯 Position analysis: 4-8 turns (masters debate moves actively)");
        Log.d(TAG, "  🎯 85% response rate (masters usually engage with each other)");
        Log.d(TAG, "  🎯 2.5s intervals (natural conversation pacing)");
        
        // Apply recommended settings
        enableEngagingConversations();
        Log.d(TAG, "✅ APPLIED: Engaging conversation template for spectator mode");
    }
}