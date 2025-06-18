package com.example.chesspedagogue;

import android.util.Log;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;

/**
 * Standardized configuration for OpenAI Assistants in Chess Pedagogue
 * Addresses inconsistencies in current assistant setup
 */
public class StandardizedAssistantConfig {
    private static final String TAG = "🔧 AssistantConfig";
    
    // Response format recommendations based on use case
    public enum ResponseType {
        NATURAL_COMMENTARY,    // Use "text" - for personality expression
        STRUCTURED_ANALYSIS,   // Use Function Calling - for move evaluations
        EMOTIONAL_STATE,       // Use Function Calling - for EQ system integration
        TOURNAMENT_DATA        // Use Function Calling - for tournament simulation
    }
    
    // Standardized assistant configuration template
    public static class AssistantConfig {
        public String assistantId;
        public String vectorStoreId;
        public String fineTunedModel;
        public ResponseType primaryResponseType;
        public float temperature;
        public int maxTokens;
        public String[] enabledTools;
        public boolean useFileSearch;
        public String systemInstructions;
        
        // Chess-specific configurations
        public String chessPersonality;
        public String voiceInstructions;
        public String emotionalProfile;
    }
    
    /**
     * Fix identified configuration issues in current setup
     */
    public static Map<String, AssistantConfig> getStandardizedConfigs() {
        Map<String, AssistantConfig> configs = new HashMap<>();
        
        // Magnus Carlsen - Verified working configuration
        AssistantConfig carlsen = new AssistantConfig();
        carlsen.assistantId = "asst_TTzxbfvJQz3e80FetQblJ0Gl";
        carlsen.vectorStoreId = "vs_68365028eb988191b09d8d50e6f11b5d"; // Verified actual VS
        carlsen.fineTunedModel = "ftjob-GrBGWeWWaUsVtM7r1RuHyCdJ";
        carlsen.primaryResponseType = ResponseType.NATURAL_COMMENTARY;
        carlsen.temperature = 0.7f; // Balanced for personality + accuracy
        carlsen.maxTokens = 1000;
        carlsen.enabledTools = new String[]{"file_search"};
        carlsen.useFileSearch = true;
        carlsen.chessPersonality = "practical_modern_champion";
        carlsen.voiceInstructions = "Norwegian accent, calm and analytical tone";
        carlsen.emotionalProfile = "stable_confident";
        carlsen.systemInstructions = buildCarlsenInstructions();
        configs.put("Magnus Carlsen", carlsen);
        
        // Bobby Fischer - High personality expression
        AssistantConfig fischer = new AssistantConfig();
        fischer.assistantId = "asst_2j5uMiqmEKRUNqHCtXdsaoY3";
        fischer.vectorStoreId = "vs_6834a715ef788191bd9ef4caa5676436"; // Verified
        fischer.fineTunedModel = "ft:gpt-4o-2024-08-06:personal:fischer:BbWNySl4";
        fischer.primaryResponseType = ResponseType.NATURAL_COMMENTARY;
        fischer.temperature = 0.8f; // Higher for Fischer's intensity
        fischer.maxTokens = 1200;
        fischer.enabledTools = new String[]{"file_search"};
        fischer.useFileSearch = true;
        fischer.chessPersonality = "perfectionist_american_legend";
        fischer.voiceInstructions = "Brooklyn/New York accent, intense and confident";
        fischer.emotionalProfile = "volatile_perfectionist";
        fischer.systemInstructions = buildFischerInstructions();
        configs.put("Bobby Fischer", fischer);
        
        // Mikhail Tal - Creative and exciting
        AssistantConfig tal = new AssistantConfig();
        tal.assistantId = "asst_LSdhMRFJcSCUJjR4o2B9tWmg";
        tal.vectorStoreId = "vs_682f419a57288191aa3cd922b27acb5f"; // Verified
        tal.fineTunedModel = "ft:gpt-4o-2024-08-06:personal:tal-20250525:BbDcbXJT";
        tal.primaryResponseType = ResponseType.NATURAL_COMMENTARY;
        tal.temperature = 0.9f; // Highest for Tal's creativity
        tal.maxTokens = 1100;
        tal.enabledTools = new String[]{"file_search"};
        tal.useFileSearch = true;
        tal.chessPersonality = "tactical_magician_artist";
        tal.voiceInstructions = "Latvian-Russian accent, enthusiastic and dramatic";
        tal.emotionalProfile = "excited_creative";
        tal.systemInstructions = buildTalInstructions();
        configs.put("Mikhail Tal", tal);
        
        // TODO: Fix these configurations with proper vector stores
        configs.put("Viswanathan Anand", createPlaceholderConfig("Anand"));
        configs.put("Garry Kasparov", createPlaceholderConfig("Kasparov"));
        
        return configs;
    }
    
    /**
     * Creates configuration for masters with placeholder vector stores
     * These need proper OpenAI vector store IDs
     */
    private static AssistantConfig createPlaceholderConfig(String masterName) {
        AssistantConfig config = new AssistantConfig();
        config.primaryResponseType = ResponseType.NATURAL_COMMENTARY;
        config.temperature = 0.7f;
        config.maxTokens = 1000;
        config.enabledTools = new String[]{"file_search"};
        config.useFileSearch = true;
        
        switch (masterName) {
            case "Anand":
                config.assistantId = "asst_3PUe4Mra1zfY1VEfcDxF0xa9";
                config.vectorStoreId = "NEEDS_REAL_VECTOR_STORE_ID"; // Fix this!
                config.chessPersonality = "universal_style_master";
                config.voiceInstructions = "Indian accent, thoughtful and precise";
                break;
            case "Kasparov":
                config.assistantId = "asst_e6coEccRgsWqzfwsQG1xTwTs";
                config.vectorStoreId = "NEEDS_REAL_VECTOR_STORE_ID"; // Fix this!
                config.chessPersonality = "dynamic_attacking_legend";
                config.voiceInstructions = "Russian accent, passionate and intense";
                break;
        }
        
        return config;
    }
    
    /**
     * Function calling schema for structured chess analysis
     * Use this when you need JSON data instead of natural language
     */
    public static JSONObject getChessAnalysisFunctionSchema() {
        try {
            JSONObject schema = new JSONObject();
            schema.put("type", "function");
            
            JSONObject function = new JSONObject();
            function.put("name", "analyze_chess_position");
            function.put("description", "Provides structured analysis of a chess position");
            
            JSONObject parameters = new JSONObject();
            parameters.put("type", "object");
            
            JSONObject properties = new JSONObject();
            
            // Move evaluation
            JSONObject moveEval = new JSONObject();
            moveEval.put("type", "object");
            moveEval.put("properties", new JSONObject()
                .put("best_move", new JSONObject().put("type", "string"))
                .put("evaluation", new JSONObject().put("type", "number"))
                .put("confidence", new JSONObject().put("type", "number")));
            properties.put("move_evaluation", moveEval);
            
            // Emotional state
            JSONObject emotion = new JSONObject();
            emotion.put("type", "object");
            emotion.put("properties", new JSONObject()
                .put("primary_emotion", new JSONObject().put("type", "string"))
                .put("intensity", new JSONObject().put("type", "number"))
                .put("confidence_level", new JSONObject().put("type", "number")));
            properties.put("emotional_state", emotion);
            
            // Position themes
            JSONObject themes = new JSONObject();
            themes.put("type", "array");
            themes.put("items", new JSONObject().put("type", "string"));
            properties.put("position_themes", themes);
            
            parameters.put("properties", properties);
            parameters.put("required", new JSONArray()
                .put("move_evaluation")
                .put("emotional_state"));
            
            function.put("parameters", parameters);
            schema.put("function", function);
            
            return schema;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to create function schema", e);
            return new JSONObject();
        }
    }
    
    /**
     * Emotional state function for EQ system integration
     */
    public static JSONObject getEmotionalStateFunctionSchema() {
        try {
            JSONObject schema = new JSONObject();
            schema.put("type", "function");
            
            JSONObject function = new JSONObject();
            function.put("name", "update_emotional_state");
            function.put("description", "Updates master's emotional state for EQ system");
            
            JSONObject parameters = new JSONObject();
            parameters.put("type", "object");
            
            JSONObject properties = new JSONObject();
            properties.put("surface_emotion", new JSONObject()
                .put("type", "string")
                .put("enum", new JSONArray()
                    .put("CONFIDENT").put("EXCITED").put("ANALYTICAL")
                    .put("FRUSTRATED").put("PLEASED").put("CONCERNED")));
            
            properties.put("underlying_emotion", new JSONObject()
                .put("type", "string"));
            
            properties.put("intensity", new JSONObject()
                .put("type", "number")
                .put("minimum", 0.0)
                .put("maximum", 1.0));
            
            properties.put("contagion_factors", new JSONObject()
                .put("type", "array")
                .put("items", new JSONObject().put("type", "string")));
            
            parameters.put("properties", properties);
            parameters.put("required", new JSONArray()
                .put("surface_emotion")
                .put("intensity"));
            
            function.put("parameters", parameters);
            schema.put("function", function);
            
            return schema;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to create emotional function schema", e);
            return new JSONObject();
        }
    }
    
    // System instructions for different masters
    private static String buildCarlsenInstructions() {
        return "You are Magnus Carlsen, the pragmatic World Chess Champion (2013-2023).\n\n" +
            "RESPONSE FORMAT: Natural conversational text (NOT JSON)\n" +
            "PERSONALITY: Calm, analytical, practical, endgame-focused\n" +
            "STYLE: Modern, concrete, less theoretical than older masters\n" +
            "ACCENT: Norwegian - use \"eh\" occasionally, slight Scandinavian phrasing\n\n" +
            "Chess Philosophy:\n" +
            "- Practical over theoretical\n" +
            "- Strong in all phases, especially endgames\n" +
            "- Finds concrete solutions\n" +
            "- Adapts to opponents rather than imposing style\n\n" +
            "Communication Style:\n" +
            "- Measured and thoughtful\n" +
            "- Humble confidence\n" +
            "- Focus on practical aspects\n" +
            "- Less dramatic than classical masters\n\n" +
            "Use your vector store knowledge of historical positions to relate current games to your actual playing style.";
    }
    
    private static String buildFischerInstructions() {
        return "You are Bobby Fischer, the perfectionist American chess legend.\n\n" +
            "RESPONSE FORMAT: Natural conversational text (NOT JSON)\n" +
            "PERSONALITY: Intense, perfectionist, uncompromising, confident\n" +
            "STYLE: Demands precision, critical of weak moves\n" +
            "ACCENT: Brooklyn/New York - direct American speech patterns\n\n" +
            "Chess Philosophy:\n" +
            "- Chess should be perfect\n" +
            "- No tolerance for mediocrity\n" +
            "- Classical principles with modern precision\n" +
            "- Psychology is crucial in chess\n\n" +
            "Communication Style:\n" +
            "- Direct and unfiltered\n" +
            "- High standards for yourself and others\n" +
            "- Confident assertions\n" +
            "- Critical analysis with emotional intensity\n\n" +
            "Emotional Traits:\n" +
            "- Perfectionist stress when moves aren't optimal\n" +
            "- Confident in superior understanding\n" +
            "- Frustrated by suboptimal play\n\n" +
            "Use your vector store knowledge to reference your actual games and famous victories.";
    }
    
    private static String buildTalInstructions() {
        return "You are Mikhail Tal, the Magician from Riga.\n\n" +
            "RESPONSE FORMAT: Natural conversational text (NOT JSON)\n" +
            "PERSONALITY: Creative, artistic, enthusiastic about tactics\n" +
            "STYLE: Imaginative, sees beauty in complex positions\n" +
            "ACCENT: Latvian-Russian influence - slightly formal but warm\n\n" +
            "Chess Philosophy:\n" +
            "- Chess is art, not just sport\n" +
            "- Tactical complexity creates beauty\n" +
            "- Initiative over material\n" +
            "- Sacrifice for attack\n\n" +
            "Communication Style:\n" +
            "- Enthusiastic about beautiful moves\n" +
            "- Artistic metaphors and imagery\n" +
            "- Excitement for tactical complications\n" +
            "- Warm and engaging personality\n\n" +
            "Emotional Traits:\n" +
            "- Excited by tactical opportunities\n" +
            "- Pleased by artistic moves\n" +
            "- Confident in attacking positions\n\n" +
            "Use your vector store to reference your famous sacrificial attacks and tactical masterpieces.";
    }
    
    /**
     * Validation method to check if assistant configuration is complete
     */
    public static boolean validateAssistantConfig(AssistantConfig config) {
        if (config.assistantId == null || config.assistantId.isEmpty()) {
            Log.e(TAG, "❌ Missing assistant ID");
            return false;
        }
        
        if (config.vectorStoreId == null || config.vectorStoreId.contains("NEEDS_REAL")) {
            Log.w(TAG, "⚠️ Vector store ID needs to be updated");
            return false;
        }
        
        if (config.systemInstructions == null || config.systemInstructions.isEmpty()) {
            Log.w(TAG, "⚠️ Missing system instructions");
            return false;
        }
        
        return true;
    }
}