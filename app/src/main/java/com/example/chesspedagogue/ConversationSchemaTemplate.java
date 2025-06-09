package com.example.chesspedagogue;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * 🎭 DYNAMIC CONVERSATION SCHEMA TEMPLATE SYSTEM
 * 
 * Rapid testing and configuration of conversation flow patterns.
 * Allows quick switching between conversation intensities and styles
 * without code changes - perfect for finding the sweet spot!
 */
public class ConversationSchemaTemplate {
    private static final String TAG = "ConversationSchemaTemplate";
    
    /**
     * Conversation intensity levels for rapid testing
     */
    public enum ConversationIntensity {
        MINIMAL,     // Very conservative - current behavior
        MODERATE,    // Balanced conversations
        ENGAGING,    // Active multi-turn dialogues  
        INTENSE,     // Long, passionate exchanges
        MAXIMUM      // Maximum chess master banter
    }
    
    /**
     * Template for different conversation styles
     */
    public enum ConversationStyle {
        ANALYTICAL,   // Technical, position-focused
        DRAMATIC,     // Emotional, personality-driven
        EDUCATIONAL,  // Teaching-focused exchanges
        COMPETITIVE,  // Rivalry and debate
        COLLABORATIVE // Working together on analysis
    }
    
    /**
     * Complete conversation template with all parameters
     */
    public static class ConversationTemplate {
        public final String name;
        public final ConversationIntensity intensity;
        public final ConversationStyle style;
        public final ConversationSchema.Schema openingSchema;
        public final ConversationSchema.Schema reactionSchema;
        public final ConversationSchema.Schema analysisSchema;
        public final ConversationSchema.Schema endgameSchema;
        
        public ConversationTemplate(String name, ConversationIntensity intensity, ConversationStyle style,
                                   ConversationSchema.Schema opening, ConversationSchema.Schema reaction,
                                   ConversationSchema.Schema analysis, ConversationSchema.Schema endgame) {
            this.name = name;
            this.intensity = intensity;
            this.style = style;
            this.openingSchema = opening;
            this.reactionSchema = reaction;
            this.analysisSchema = analysis;
            this.endgameSchema = endgame;
        }
    }
    
    // 🎯 PREDEFINED TEMPLATES FOR RAPID TESTING
    
    /**
     * MINIMAL - Current conservative behavior (too quiet)
     */
    public static final ConversationTemplate MINIMAL_TEMPLATE = new ConversationTemplate(
        "Minimal Conversations",
        ConversationIntensity.MINIMAL,
        ConversationStyle.ANALYTICAL,
        new ConversationSchema.Schema(ConversationSchema.SchemaType.OPENING_CHAT, 1, 2,
            new ConversationSchema.ResponseLength(10, 25, 0.2, "terse"),
            new ConversationSchema.ResponseLength(5, 15, 0.3, "terse"),
            new ConversationSchema.ConversationRhythm(1.0, 1.0, false, 0.05),
            0.5, 3500, 5000),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.QUICK_REACTION, 1, 2,
            new ConversationSchema.ResponseLength(5, 18, 0.4, "terse"),
            new ConversationSchema.ResponseLength(3, 10, 0.5, "terse"),
            new ConversationSchema.ConversationRhythm(0.9, 1.1, true, 0.02),
            0.4, 2500, 4000),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.POSITION_ANALYSIS, 1, 3,
            new ConversationSchema.ResponseLength(12, 30, 0.25, "analytical"),
            new ConversationSchema.ResponseLength(6, 20, 0.35, "analytical"),
            new ConversationSchema.ConversationRhythm(0.95, 1.05, false, 0.08),
            0.5, 3000, 4500),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.ENDGAME_WRAP, 1, 2,
            new ConversationSchema.ResponseLength(15, 35, 0.2, "reflective"),
            new ConversationSchema.ResponseLength(8, 20, 0.3, "contemplative"),
            new ConversationSchema.ConversationRhythm(1.0, 1.0, false, 0.1),
            0.6, 3500, 5000)
    );
    
    /**
     * ENGAGING - Enhanced with natural flow patterns and percentage-based follow-ups
     * Features: 4-6 turns, 90% response rate, declining conversation pattern for natural flow
     */
    public static final ConversationTemplate ENGAGING_TEMPLATE = new ConversationTemplate(
        "Engaging Chess Dialogue",
        ConversationIntensity.ENGAGING,
        ConversationStyle.DRAMATIC,
        new ConversationSchema.Schema(ConversationSchema.SchemaType.OPENING_CHAT, 4, 6,
            new ConversationSchema.ResponseLength(45, 85, 0.35, "elaborate"), // Rich opening 45-85 words
            new ConversationSchema.ResponseLength(8, 50, 0.4, "conversational", 0.4, 0.7), // Follow-up: 40-70% of initial
            new ConversationSchema.ConversationRhythm(0.88, 1.25, false, 0.15, 
                ConversationSchema.ConversationFlowPattern.DECLINING, 0.6), // Natural declining flow
            0.90, 2000, 3500),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.QUICK_REACTION, 4, 6,
            new ConversationSchema.ResponseLength(25, 60, 0.5, "dramatic"), // Dramatic reaction 25-60 words
            new ConversationSchema.ResponseLength(5, 35, 0.6, "conversational", 0.3, 0.6), // Follow-up: 30-60% of initial
            new ConversationSchema.ConversationRhythm(0.82, 1.4, true, 0.08,
                ConversationSchema.ConversationFlowPattern.EXPLOSIVE, 0.8), // Explosive pattern for reactions
            0.90, 1800, 3000),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.POSITION_ANALYSIS, 4, 6,
            new ConversationSchema.ResponseLength(35, 75, 0.3, "analytical"), // Deep analysis 35-75 words
            new ConversationSchema.ResponseLength(10, 45, 0.35, "conversational", 0.5, 0.8), // Follow-up: 50-80% of initial
            new ConversationSchema.ConversationRhythm(0.92, 1.15, false, 0.18,
                ConversationSchema.ConversationFlowPattern.WAVE, 0.5), // Wave pattern for sustained analysis
            0.90, 2200, 4000),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.ENDGAME_WRAP, 4, 6,
            new ConversationSchema.ResponseLength(50, 95, 0.25, "reflective"), // Thoughtful summary 50-95 words
            new ConversationSchema.ResponseLength(15, 55, 0.3, "contemplative", 0.4, 0.7), // Follow-up: 40-70% of initial
            new ConversationSchema.ConversationRhythm(0.98, 1.1, false, 0.22,
                ConversationSchema.ConversationFlowPattern.FADE_IN, 0.4), // Build up then gentle decline
            0.90, 2800, 4500)
    );
    
    /**
     * INTENSE - Maximum chess master personality clash with dynamic flow
     * Features: Long initial responses, rapid-fire follow-ups, high emotional intensity
     */
    public static final ConversationTemplate INTENSE_TEMPLATE = new ConversationTemplate(
        "Intense Master Debates",
        ConversationIntensity.INTENSE,
        ConversationStyle.COMPETITIVE,
        new ConversationSchema.Schema(ConversationSchema.SchemaType.OPENING_CHAT, 4, 10,
            new ConversationSchema.ResponseLength(70, 120, 0.4, "passionate"), // Explosive opening 70-120 words
            new ConversationSchema.ResponseLength(35, 75, 0.5, "dramatic"), // Strong counter-arguments 35-75 words
            new ConversationSchema.ConversationRhythm(0.75, 1.6, true, 0.12), // Rapid acceleration, high emotion
            0.90, 1500, 3000),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.QUICK_REACTION, 3, 8,
            new ConversationSchema.ResponseLength(45, 85, 0.6, "explosive"), // Explosive reaction 45-85 words
            new ConversationSchema.ResponseLength(15, 50, 0.7, "heated"), // Rapid-fire exchange 15-50 words
            new ConversationSchema.ConversationRhythm(0.65, 1.8, true, 0.05), // Very fast acceleration, maximum emotion
            0.85, 1000, 2200),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.POSITION_ANALYSIS, 5, 12,
            new ConversationSchema.ResponseLength(60, 140, 0.35, "comprehensive"), // Deep analysis 60-140 words
            new ConversationSchema.ResponseLength(30, 85, 0.45, "debative"), // Detailed debate 30-85 words
            new ConversationSchema.ConversationRhythm(0.85, 1.4, false, 0.15), // Moderate acceleration, sustained emotion
            0.88, 1800, 3200),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.ENDGAME_WRAP, 4, 8,
            new ConversationSchema.ResponseLength(80, 140, 0.3, "extensive"), // Comprehensive review 80-140 words
            new ConversationSchema.ResponseLength(40, 90, 0.4, "argumentative"), // Final arguments 40-90 words
            new ConversationSchema.ConversationRhythm(0.92, 1.2, false, 0.18), // Slow wind-down, maintained passion
            0.95, 2200, 4000)
    );
    
    /**
     * COLLABORATIVE - Masters working together on analysis with natural building
     * Features: Thoughtful pacing, building responses, educational flow
     */
    public static final ConversationTemplate COLLABORATIVE_TEMPLATE = new ConversationTemplate(
        "Collaborative Analysis",
        ConversationIntensity.MODERATE,
        ConversationStyle.EDUCATIONAL,
        new ConversationSchema.Schema(ConversationSchema.SchemaType.OPENING_CHAT, 3, 7,
            new ConversationSchema.ResponseLength(40, 75, 0.3, "thoughtful"), // Thoughtful setup 40-75 words
            new ConversationSchema.ResponseLength(25, 55, 0.35, "building"), // Building ideas 25-55 words
            new ConversationSchema.ConversationRhythm(1.05, 1.1, false, 0.2), // Gentle expansion, calm emotion
            0.80, 2500, 4000),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.QUICK_REACTION, 2, 6,
            new ConversationSchema.ResponseLength(22, 50, 0.4, "insightful"), // Insightful observation 22-50 words
            new ConversationSchema.ResponseLength(15, 35, 0.45, "perspective"), // Adding perspective 15-35 words
            new ConversationSchema.ConversationRhythm(0.95, 1.2, true, 0.12), // Slight deceleration, mild excitement
            0.70, 2000, 3500),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.POSITION_ANALYSIS, 4, 9,
            new ConversationSchema.ResponseLength(55, 100, 0.25, "educational"), // Detailed examination 55-100 words
            new ConversationSchema.ResponseLength(30, 70, 0.3, "expanding"), // Expanding analysis 30-70 words
            new ConversationSchema.ConversationRhythm(1.02, 1.05, false, 0.25), // Gentle growth, minimal emotion
            0.85, 2400, 4200),
        new ConversationSchema.Schema(ConversationSchema.SchemaType.ENDGAME_WRAP, 3, 7,
            new ConversationSchema.ResponseLength(65, 110, 0.2, "comprehensive"), // Comprehensive review 65-110 words
            new ConversationSchema.ResponseLength(35, 65, 0.25, "insightful"), // Learning insights 35-65 words
            new ConversationSchema.ConversationRhythm(1.0, 1.0, false, 0.3), // Stable pacing, natural reflection
            0.88, 3000, 5000)
    );
    
    // 🔧 TEMPLATE REGISTRY FOR EASY SWITCHING
    private static final Map<String, ConversationTemplate> TEMPLATE_REGISTRY = new HashMap<>();
    static {
        TEMPLATE_REGISTRY.put("minimal", MINIMAL_TEMPLATE);
        TEMPLATE_REGISTRY.put("engaging", ENGAGING_TEMPLATE);
        TEMPLATE_REGISTRY.put("intense", INTENSE_TEMPLATE);
        TEMPLATE_REGISTRY.put("collaborative", COLLABORATIVE_TEMPLATE);
        
        // Enhanced flow pattern templates
        TEMPLATE_REGISTRY.put("natural_decline", createNaturalFlowTemplate(
            "Natural Decline", ConversationSchema.ConversationFlowPattern.DECLINING, 
            60, 0.6, 4, 7, 0.85));
        TEMPLATE_REGISTRY.put("building_excitement", createNaturalFlowTemplate(
            "Building Excitement", ConversationSchema.ConversationFlowPattern.BUILDING,
            35, 0.8, 3, 8, 0.9));
        TEMPLATE_REGISTRY.put("wave_discussion", createNaturalFlowTemplate(
            "Wave Discussion", ConversationSchema.ConversationFlowPattern.WAVE,
            50, 0.7, 4, 6, 0.8));
        TEMPLATE_REGISTRY.put("explosive_debate", createNaturalFlowTemplate(
            "Explosive Debate", ConversationSchema.ConversationFlowPattern.EXPLOSIVE,
            40, 0.5, 3, 7, 0.85));
    }
    
    // 🎯 CURRENT ACTIVE TEMPLATE (for rapid testing)
    private static ConversationTemplate currentTemplate = ENGAGING_TEMPLATE; // Start with engaging!
    
    /**
     * 🚀 RAPID TESTING: Switch conversation template instantly
     */
    public static void setConversationTemplate(String templateName) {
        ConversationTemplate template = TEMPLATE_REGISTRY.get(templateName.toLowerCase());
        if (template != null) {
            currentTemplate = template;
            Log.d(TAG, "🎭 CONVERSATION TEMPLATE CHANGED: " + template.name + 
                  " (Intensity: " + template.intensity + ", Style: " + template.style + ")");
        } else {
            Log.e(TAG, "❌ Unknown template: " + templateName + ". Available: " + TEMPLATE_REGISTRY.keySet());
        }
    }
    
    /**
     * 🎯 Get current template
     */
    public static ConversationTemplate getCurrentTemplate() {
        return currentTemplate;
    }
    
    /**
     * 📊 Get schema for trigger type using current template
     */
    public static ConversationSchema.Schema getSchemaForTrigger(String triggerType) {
        switch (triggerType.toLowerCase()) {
            case "opening":
            case "game_start":
                return currentTemplate.openingSchema;
                
            case "brilliant_move":
            case "blunder": 
            case "tactical_shot":
                return currentTemplate.reactionSchema;
                
            case "position_change":
            case "middlegame":
            case "evaluation_swing":
                return currentTemplate.analysisSchema;
                
            case "endgame":
            case "game_over":
            case "checkmate":
                return currentTemplate.endgameSchema;
                
            default:
                return currentTemplate.reactionSchema; // Safe default
        }
    }
    
    /**
     * 🔄 QUICK TEST METHODS FOR DEVELOPMENT
     */
    
    /**
     * Test minimal conversations (current behavior)
     */
    public static void useMinimalConversations() {
        setConversationTemplate("minimal");
        Log.d(TAG, "🔧 TESTING: Minimal conversations (current conservative behavior)");
    }
    
    /**
     * Test engaging conversations (recommended)
     */
    public static void useEngagingConversations() {
        setConversationTemplate("engaging");
        Log.d(TAG, "🔧 TESTING: Engaging conversations (recommended for spectator mode)");
    }
    
    /**
     * Test intense conversations (maximum drama)
     */
    public static void useIntenseConversations() {
        setConversationTemplate("intense");
        Log.d(TAG, "🔧 TESTING: Intense conversations (maximum chess master drama)");
    }
    
    /**
     * Test collaborative conversations (educational)
     */
    public static void useCollaborativeConversations() {
        setConversationTemplate("collaborative");
        Log.d(TAG, "🔧 TESTING: Collaborative conversations (educational analysis)");
    }
    
    /**
     * 🌊 QUICK FLOW PATTERN TESTING
     */
    public static void useNaturalDeclineConversations() {
        setConversationTemplate("natural_decline");
        Log.d(TAG, "🔧 TESTING: Natural decline conversations (realistic conversation fade)");
    }
    
    public static void useBuildingExcitementConversations() {
        setConversationTemplate("building_excitement");
        Log.d(TAG, "🔧 TESTING: Building excitement conversations (crescendo pattern)");
    }
    
    public static void useWaveDiscussionConversations() {
        setConversationTemplate("wave_discussion");
        Log.d(TAG, "🔧 TESTING: Wave discussion conversations (natural ebb and flow)");
    }
    
    public static void useExplosiveDebateConversations() {
        setConversationTemplate("explosive_debate");
        Log.d(TAG, "🔧 TESTING: Explosive debate conversations (dramatic peak pattern)");
    }
    
    /**
     * 📈 DYNAMIC INTENSITY ADJUSTMENT with enhanced natural flow controls
     */
    public static ConversationTemplate createCustomTemplate(String name, 
                                                           int minTurns, int maxTurns, 
                                                           double responseChance, 
                                                           long intervalMs) {
        Log.d(TAG, "🎨 CREATING CUSTOM TEMPLATE: " + name + 
              " (Turns: " + minTurns + "-" + maxTurns + 
              ", Response: " + (responseChance * 100) + "%" + 
              ", Interval: " + intervalMs + "ms)");
        
        // Calculate word ranges based on turn intensity
        int baseInitialWords = 30 + (maxTurns - minTurns) * 8; // More turns = longer responses
        int baseFollowupWords = 20 + (maxTurns - minTurns) * 5;
        
        return new ConversationTemplate(
            name,
            ConversationIntensity.MODERATE, // Default
            ConversationStyle.DRAMATIC,     // Default
            new ConversationSchema.Schema(ConversationSchema.SchemaType.OPENING_CHAT, 
                minTurns, maxTurns,
                new ConversationSchema.ResponseLength(baseInitialWords, baseInitialWords + 40, 0.3, "conversational"),
                new ConversationSchema.ResponseLength(baseFollowupWords, baseFollowupWords + 25, 0.4, "conversational"),
                new ConversationSchema.ConversationRhythm(0.9, 1.2, false, 0.15),
                responseChance, intervalMs, intervalMs + 2000),
            new ConversationSchema.Schema(ConversationSchema.SchemaType.QUICK_REACTION, 
                Math.max(1, minTurns-1), Math.max(2, maxTurns-2),
                new ConversationSchema.ResponseLength(baseInitialWords - 10, baseInitialWords + 20, 0.5, "reactive"),
                new ConversationSchema.ResponseLength(baseFollowupWords - 8, baseFollowupWords + 15, 0.6, "quick"),
                new ConversationSchema.ConversationRhythm(0.8, 1.4, true, 0.08),
                responseChance * 0.8, (long) (intervalMs * 0.8), intervalMs + 1500),
            new ConversationSchema.Schema(ConversationSchema.SchemaType.POSITION_ANALYSIS, 
                minTurns+1, maxTurns+2,
                new ConversationSchema.ResponseLength(baseInitialWords + 15, baseInitialWords + 60, 0.25, "analytical"),
                new ConversationSchema.ResponseLength(baseFollowupWords + 8, baseFollowupWords + 30, 0.35, "analytical"),
                new ConversationSchema.ConversationRhythm(0.95, 1.1, false, 0.2),
                responseChance * 0.9, intervalMs, intervalMs + 2500),
            new ConversationSchema.Schema(ConversationSchema.SchemaType.ENDGAME_WRAP, 
                minTurns, maxTurns,
                new ConversationSchema.ResponseLength(baseInitialWords + 20, baseInitialWords + 50, 0.2, "reflective"),
                new ConversationSchema.ResponseLength(baseFollowupWords + 10, baseFollowupWords + 35, 0.3, "contemplative"),
                new ConversationSchema.ConversationRhythm(1.0, 1.0, false, 0.25),
                Math.min(1.0, responseChance * 1.1), (long) (intervalMs * 1.2), intervalMs + 3000)
        );
    }
    
    /**
     * 🎯 NATURAL FLOW PRESETS: Quick templates for common conversation patterns
     */
    public static ConversationTemplate createNaturalFlowTemplate(String name, 
                                                                ConversationSchema.ConversationFlowPattern pattern,
                                                                int firstResponseWords, double followupPercentage,
                                                                int minTurns, int maxTurns, double responseChance) {
        Log.d(TAG, "🌊 CREATING NATURAL FLOW TEMPLATE: " + name + 
              " (Pattern: " + pattern + ", First: " + firstResponseWords + " words, " +
              "Follow-up: " + (followupPercentage * 100) + "% of first)");
        
        return createAdvancedTemplateWithFlow(
            name, minTurns, maxTurns,
            (int)(firstResponseWords * 0.8), (int)(firstResponseWords * 1.2), // ±20% variation on first response
            8, Math.max(15, (int)(firstResponseWords * followupPercentage)), // Follow-up based on percentage
            responseChance, 1.2, // Standard emotional multiplier
            2000, 4000, // Standard intervals
            pattern, 0.8 // Moderate flow intensity
        );
    }
    
    /**
     * 🎯 ADVANCED: Create template with specific length controls (legacy)
     */
    public static ConversationTemplate createAdvancedTemplate(String name,
                                                             int minTurns, int maxTurns,
                                                             int initialMinWords, int initialMaxWords,
                                                             int followupMinWords, int followupMaxWords,
                                                             double responseChance, double emotionalMultiplier,
                                                             long minInterval, long maxInterval) {
        return createAdvancedTemplateWithFlow(name, minTurns, maxTurns, initialMinWords, initialMaxWords,
                                            followupMinWords, followupMaxWords, responseChance, emotionalMultiplier,
                                            minInterval, maxInterval, ConversationSchema.ConversationFlowPattern.STEADY, 1.0);
    }
    
    /**
     * 🚀 ULTIMATE: Create template with flow patterns and percentage-based follow-ups
     */
    public static ConversationTemplate createAdvancedTemplateWithFlow(String name,
                                                                     int minTurns, int maxTurns,
                                                                     int initialMinWords, int initialMaxWords,
                                                                     int followupMinWords, int followupMaxWords,
                                                                     double responseChance, double emotionalMultiplier,
                                                                     long minInterval, long maxInterval,
                                                                     ConversationSchema.ConversationFlowPattern flowPattern,
                                                                     double flowIntensity) {
        Log.d(TAG, "🎨 CREATING ADVANCED FLOW TEMPLATE: " + name + 
              " (Initial: " + initialMinWords + "-" + initialMaxWords + " words, " +
              "Followup: " + followupMinWords + "-" + followupMaxWords + " words, " +
              "Flow: " + flowPattern + ", Intensity: " + flowIntensity + ")");
        
        // Calculate percentage range for follow-ups (based on absolute word ranges)
        double followupPercentageMin = Math.max(0.2, (double)followupMinWords / initialMaxWords);
        double followupPercentageMax = Math.min(1.2, (double)followupMaxWords / initialMinWords);
        
        ConversationSchema.ConversationRhythm rhythm = new ConversationSchema.ConversationRhythm(
            0.9, emotionalMultiplier, true, 0.15, flowPattern, flowIntensity
        );
        
        return new ConversationTemplate(
            name,
            ConversationIntensity.MODERATE,
            ConversationStyle.DRAMATIC,
            new ConversationSchema.Schema(ConversationSchema.SchemaType.OPENING_CHAT, 
                minTurns, maxTurns,
                new ConversationSchema.ResponseLength(initialMinWords, initialMaxWords, 0.3, "conversational"),
                new ConversationSchema.ResponseLength(followupMinWords, followupMaxWords, 0.4, "conversational", 
                    followupPercentageMin, followupPercentageMax),
                rhythm, responseChance, minInterval, maxInterval),
            new ConversationSchema.Schema(ConversationSchema.SchemaType.QUICK_REACTION, 
                Math.max(1, minTurns-1), Math.max(2, maxTurns-1),
                new ConversationSchema.ResponseLength(initialMinWords - 15, initialMaxWords - 20, 0.5, "reactive"),
                new ConversationSchema.ResponseLength(followupMinWords - 10, followupMaxWords - 15, 0.6, "quick",
                    followupPercentageMin * 0.7, followupPercentageMax * 0.8),
                new ConversationSchema.ConversationRhythm(0.8, emotionalMultiplier * 1.2, true, 0.08, 
                    ConversationSchema.ConversationFlowPattern.EXPLOSIVE, flowIntensity * 1.2),
                responseChance * 0.8, (long)(minInterval * 0.8), (long)(maxInterval * 0.8)),
            new ConversationSchema.Schema(ConversationSchema.SchemaType.POSITION_ANALYSIS, 
                minTurns, maxTurns + 2,
                new ConversationSchema.ResponseLength(initialMinWords + 10, initialMaxWords + 30, 0.25, "analytical"),
                new ConversationSchema.ResponseLength(followupMinWords + 5, followupMaxWords + 20, 0.35, "analytical",
                    followupPercentageMin * 1.1, followupPercentageMax * 1.0),
                new ConversationSchema.ConversationRhythm(0.95, emotionalMultiplier * 0.8, false, 0.2,
                    ConversationSchema.ConversationFlowPattern.WAVE, flowIntensity * 0.8),
                responseChance * 0.9, minInterval, maxInterval),
            new ConversationSchema.Schema(ConversationSchema.SchemaType.ENDGAME_WRAP, 
                minTurns, maxTurns,
                new ConversationSchema.ResponseLength(initialMinWords + 20, initialMaxWords + 40, 0.2, "reflective"),
                new ConversationSchema.ResponseLength(followupMinWords + 10, followupMaxWords + 25, 0.3, "contemplative",
                    followupPercentageMin, followupPercentageMax * 0.9),
                new ConversationSchema.ConversationRhythm(1.0, emotionalMultiplier * 0.9, false, 0.25,
                    ConversationSchema.ConversationFlowPattern.DECLINING, flowIntensity * 0.6),
                Math.min(1.0, responseChance * 1.1), (long)(minInterval * 1.2), (long)(maxInterval * 1.3))
        );
    }
    
    /**
     * 📊 Get current conversation stats for debugging
     */
    public static String getCurrentTemplateStats() {
        ConversationTemplate t = currentTemplate;
        return String.format("🎭 CURRENT: %s | Opening: %d-%d turns (%.0f%% chance) | " +
                           "Analysis: %d-%d turns (%.0f%% chance) | Style: %s",
                           t.name, 
                           t.openingSchema.minTurns, t.openingSchema.maxTurns, t.openingSchema.responseChance * 100,
                           t.analysisSchema.minTurns, t.analysisSchema.maxTurns, t.analysisSchema.responseChance * 100,
                           t.style);
    }
    
    /**
     * 🚀 INSTANT TESTING: Log all available templates
     */
    public static void logAvailableTemplates() {
        Log.d(TAG, "🎭 AVAILABLE CONVERSATION TEMPLATES:");
        for (Map.Entry<String, ConversationTemplate> entry : TEMPLATE_REGISTRY.entrySet()) {
            ConversationTemplate t = entry.getValue();
            Log.d(TAG, String.format("  📋 %s: %s (%s intensity, %s style)", 
                  entry.getKey(), t.name, t.intensity, t.style));
        }
        Log.d(TAG, "🔧 CURRENT: " + getCurrentTemplateStats());
    }
}