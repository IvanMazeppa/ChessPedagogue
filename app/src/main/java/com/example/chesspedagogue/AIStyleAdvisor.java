package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🧠 AI Style Advisor - Revolutionary Chess Personality System
 * 
 * Uses OpenAI Assistants with vector stores to provide authentic playstyle advice
 * for chess masters. This system analyzes positions through the lens of historical
 * games and provides style-informed move suggestions.
 * 
 * Features:
 * - Lightning-fast local fallback for immediate gameplay
 * - Intelligent caching to minimize API calls
 * - Async processing to prevent gameplay delays
 * - Master-specific vector store integration
 * - Contextual chess position analysis
 */
public class AIStyleAdvisor {
    private static final String TAG = "AIStyleAdvisor";
    
    // Performance settings
    private static final int CACHE_SIZE = 100;
    private static final long CACHE_EXPIRY_MS = 300000; // 5 minutes
    private static final int MAX_ADVICE_REQUESTS_PER_GAME = 20;
    
    // Singleton instance
    private static AIStyleAdvisor instance;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    
    // Core services
    private final ResponsesAPIService responsesAPIService;
    private final FineTunedModelManager modelManager;
    
    // Caching and optimization
    private final LruCache<String, CachedAdvice> adviceCache;
    private int requestCount = 0;
    
    /**
     * Cached AI advice with expiry
     */
    private static class CachedAdvice {
        final AIStyleAdvice advice;
        final long timestamp;
        
        CachedAdvice(AIStyleAdvice advice) {
            this.advice = advice;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
    }
    
    /**
     * AI Style Advice response structure
     */
    public static class AIStyleAdvice {
        public final List<String> preferredMoves;
        public final float styleWeight;
        public final String reasoning;
        public final String masterInsight;
        public final boolean isAttackingPosition;
        public final boolean isPositionalPosition;
        
        public AIStyleAdvice(List<String> preferredMoves, float styleWeight, 
                           String reasoning, String masterInsight,
                           boolean isAttackingPosition, boolean isPositionalPosition) {
            this.preferredMoves = preferredMoves != null ? preferredMoves : new ArrayList<>();
            this.styleWeight = Math.max(0.0f, Math.min(1.0f, styleWeight));
            this.reasoning = reasoning != null ? reasoning : "";
            this.masterInsight = masterInsight != null ? masterInsight : "";
            this.isAttackingPosition = isAttackingPosition;
            this.isPositionalPosition = isPositionalPosition;
        }
        
        @Override
        public String toString() {
            return String.format("AIStyleAdvice{moves=%s, weight=%.2f, attacking=%s, positional=%s}", 
                    preferredMoves, styleWeight, isAttackingPosition, isPositionalPosition);
        }
    }
    
    /**
     * Callback interface for async AI advice
     */
    public interface AIStyleCallback {
        void onAdviceReceived(AIStyleAdvice advice);
        void onError(String error);
        void onCacheHit(AIStyleAdvice cachedAdvice);
    }
    
    private AIStyleAdvisor(Context context) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();
        this.responsesAPIService = ResponsesAPIService.getInstance();
        this.modelManager = FineTunedModelManager.getInstance(context);
        this.adviceCache = new LruCache<>(CACHE_SIZE);
        
        Log.d(TAG, "🧠 AIStyleAdvisor initialized - ready for chess personality enhancement!");
    }
    
    public static synchronized AIStyleAdvisor getInstance(Context context) {
        if (instance == null) {
            instance = new AIStyleAdvisor(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 🎯 MAIN METHOD: Get AI style advice for a chess position
     * 
     * This method provides style-informed advice while maintaining fast gameplay:
     * 1. Checks cache for instant responses
     * 2. Falls back to async AI analysis
     * 3. Provides contextual chess insights
     */
    public void getStyleAdvice(String fen, String master, List<String> candidateMoves, 
                              AIStyleCallback callback) {
        
        // Validate inputs
        if (fen == null || master == null || callback == null) {
            Log.e(TAG, "❌ Invalid input parameters for getStyleAdvice");
            callback.onError("Invalid parameters");
            return;
        }
        
        // Generate cache key based on position and master
        String cacheKey = generateCacheKey(fen, master);
        
        // Check cache first for instant response
        CachedAdvice cached = adviceCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            Log.d(TAG, "⚡ Cache hit for " + master + " - instant advice");
            mainHandler.post(() -> callback.onCacheHit(cached.advice));
            return;
        }
        
        // Rate limiting check
        if (requestCount >= MAX_ADVICE_REQUESTS_PER_GAME) {
            Log.w(TAG, "⚠️ Rate limit reached - providing fallback advice");
            AIStyleAdvice fallback = createFallbackAdvice(master, candidateMoves);
            mainHandler.post(() -> callback.onAdviceReceived(fallback));
            return;
        }
        
        // Async AI analysis
        executorService.execute(() -> {
            try {
                requestCount++;
                Log.d(TAG, "🧠 Requesting AI advice for " + master + " (request #" + requestCount + ")");
                
                AIStyleAdvice advice = requestAIAdvice(fen, master, candidateMoves);
                
                // Cache the result
                adviceCache.put(cacheKey, new CachedAdvice(advice));
                
                // Return to main thread
                mainHandler.post(() -> callback.onAdviceReceived(advice));
                
            } catch (Exception e) {
                Log.e(TAG, "💥 Error getting AI advice for " + master, e);
                mainHandler.post(() -> callback.onError("AI advice failed: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 🎭 Request AI advice from OpenAI Assistant with vector store
     */
    private AIStyleAdvice requestAIAdvice(String fen, String master, List<String> candidateMoves) {
        try {
            // Get assistant ID for the master
            String assistantId = getAssistantId(master);
            if (assistantId == null) {
                Log.w(TAG, "⚠️ No assistant available for " + master + " - using fallback");
                return createFallbackAdvice(master, candidateMoves);
            }
            
            // Construct chess-specific prompt
            String prompt = buildChessStylePrompt(fen, master, candidateMoves);
            
            Log.d(TAG, "🎯 Sending prompt to " + master + " assistant: " + assistantId);
            Log.d(TAG, "📝 Prompt preview: " + prompt.substring(0, Math.min(100, prompt.length())) + "...");
            Log.d(TAG, "🎲 Candidate moves for AI analysis: " + candidateMoves);
            
            // Use async API with blocking wait (we're already in background thread)
            final StringBuilder responseBuilder = new StringBuilder();
            final boolean[] isComplete = {false};
            final String[] errorMessage = {null};
            
            ResponsesAPIService.ResponseCallback callback = new ResponsesAPIService.ResponseCallback() {
                @Override
                public void onResponseChunk(String chunk) {
                    responseBuilder.append(chunk);
                }
                
                @Override
                public void onResponseComplete(String fullResponse, String responseId) {
                    responseBuilder.setLength(0); // Clear any chunks
                    responseBuilder.append(fullResponse);
                    synchronized (isComplete) {
                        isComplete[0] = true;
                        isComplete.notify();
                    }
                }
                
                @Override
                public void onError(String error) {
                    errorMessage[0] = error;
                    synchronized (isComplete) {
                        isComplete[0] = true;
                        isComplete.notify();
                    }
                }
            };
            
            // Start the async request
            Map<String, Object> tools = new HashMap<>(); // Empty tools for now
            responsesAPIService.createResponse(assistantId, prompt, null, tools, callback);
            
            // Wait for completion with timeout
            synchronized (isComplete) {
                try {
                    if (!isComplete[0]) {
                        isComplete.wait(10000); // 10 second timeout
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return createFallbackAdvice(master, candidateMoves);
                }
            }
            
            if (errorMessage[0] != null) {
                Log.w(TAG, "⚠️ AI request failed: " + errorMessage[0]);
                return createFallbackAdvice(master, candidateMoves);
            }
            
            String response = responseBuilder.toString();
            if (response != null && !response.trim().isEmpty()) {
                Log.d(TAG, "✅ Received AI response for " + master + ": " + response.substring(0, Math.min(100, response.length())) + "...");
                return parseAIResponse(response, master, candidateMoves);
            } else {
                Log.w(TAG, "⚠️ Empty response from AI assistant");
                return createFallbackAdvice(master, candidateMoves);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "💥 Exception in requestAIAdvice for " + master, e);
            return createFallbackAdvice(master, candidateMoves);
        }
    }
    
    /**
     * 🎨 Build chess-specific prompt for style analysis
     * 🔧 FIXED: Now includes color perspective to prevent evaluation inversion
     */
    private String buildChessStylePrompt(String fen, String master, List<String> candidateMoves) {
        StringBuilder prompt = new StringBuilder();
        
        // 🚨 FIX: Determine which color is to move from FEN
        String[] fenParts = fen.split(" ");
        String colorToMove = fenParts.length > 1 ? fenParts[1] : "w";
        String colorName = colorToMove.equals("w") ? "WHITE" : "BLACK";
        
        prompt.append("CHESS POSITION ANALYSIS REQUEST\n");
        prompt.append("Position (FEN): ").append(fen).append("\n");
        prompt.append("🔧 IMPORTANT: YOU ARE PLAYING AS ").append(colorName).append(" in this position.\n");
        prompt.append("Available moves: ").append(candidateMoves).append("\n\n");
        
        prompt.append("As ").append(master.toUpperCase()).append(" playing ").append(colorName).append(", analyze this position and provide:\n");
        prompt.append("1. Which moves best reflect your playing style AS ").append(colorName).append("?\n");
        prompt.append("2. What strategic themes appeal to you here?\n");
        prompt.append("3. How would you approach this position from ").append(colorName).append("'s perspective?\n\n");
        
        prompt.append("🚨 CRITICAL: Evaluate everything from ").append(colorName).append("'s perspective.\n");
        prompt.append("Material advantage means YOU (").append(colorName).append(") have more pieces.\n\n");
        
        prompt.append("Respond in JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"preferred_moves\": [\"move1\", \"move2\"],\n");
        prompt.append("  \"style_weight\": 0.7,\n");
        prompt.append("  \"reasoning\": \"Explanation of style preferences from ").append(colorName).append("'s perspective\",\n");
        prompt.append("  \"master_insight\": \"Personal insight or quote\",\n");
        prompt.append("  \"is_attacking\": true/false,\n");
        prompt.append("  \"is_positional\": true/false\n");
        prompt.append("}");
        
        return prompt.toString();
    }
    
    /**
     * 🔍 Parse AI response into structured advice
     */
    private AIStyleAdvice parseAIResponse(String response, String master, List<String> candidateMoves) {
        try {
            // Try to extract JSON from response
            String jsonStr = extractJSON(response);
            if (jsonStr == null) {
                Log.w(TAG, "⚠️ No JSON found in response, using text analysis");
                return parseTextResponse(response, master, candidateMoves);
            }
            
            JSONObject json = new JSONObject(jsonStr);
            
            // Extract preferred moves
            List<String> preferredMoves = new ArrayList<>();
            if (json.has("preferred_moves")) {
                JSONArray movesArray = json.getJSONArray("preferred_moves");
                for (int i = 0; i < movesArray.length(); i++) {
                    String move = movesArray.getString(i);
                    if (candidateMoves.contains(move)) {
                        preferredMoves.add(move);
                    }
                }
            }
            
            // Extract other fields with defaults
            float styleWeight = (float) json.optDouble("style_weight", 0.5);
            String reasoning = json.optString("reasoning", "Style-based preference");
            String masterInsight = json.optString("master_insight", "");
            boolean isAttacking = json.optBoolean("is_attacking", false);
            boolean isPositional = json.optBoolean("is_positional", false);
            
            Log.d(TAG, "✅ Parsed AI advice: " + preferredMoves.size() + " moves, weight=" + styleWeight);
            Log.d(TAG, "🎭 AI preferred moves: " + preferredMoves);
            Log.d(TAG, "💭 AI reasoning: " + reasoning);
            
            return new AIStyleAdvice(preferredMoves, styleWeight, reasoning, 
                                   masterInsight, isAttacking, isPositional);
            
        } catch (JSONException e) {
            Log.w(TAG, "⚠️ JSON parsing failed, falling back to text analysis", e);
            return parseTextResponse(response, master, candidateMoves);
        }
    }
    
    /**
     * 🔧 Extract JSON from AI response text
     */
    private String extractJSON(String response) {
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");
        
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            return response.substring(jsonStart, jsonEnd + 1);
        }
        
        return null;
    }
    
    /**
     * 📝 Parse non-JSON text response
     */
    private AIStyleAdvice parseTextResponse(String response, String master, List<String> candidateMoves) {
        List<String> preferredMoves = new ArrayList<>();
        
        // Simple text analysis to find mentioned moves
        String lowerResponse = response.toLowerCase();
        for (String move : candidateMoves) {
            if (lowerResponse.contains(move.toLowerCase())) {
                preferredMoves.add(move);
            }
        }
        
        // Determine style characteristics based on keywords
        boolean isAttacking = lowerResponse.contains("attack") || lowerResponse.contains("sacrifice") || 
                            lowerResponse.contains("aggressive") || lowerResponse.contains("tactical");
        boolean isPositional = lowerResponse.contains("positional") || lowerResponse.contains("strategic") || 
                             lowerResponse.contains("structure") || lowerResponse.contains("endgame");
        
        float styleWeight = preferredMoves.isEmpty() ? 0.3f : 0.6f;
        
        return new AIStyleAdvice(preferredMoves, styleWeight, response, "", isAttacking, isPositional);
    }
    
    /**
     * 🛡️ Create fallback advice when AI is unavailable
     */
    private AIStyleAdvice createFallbackAdvice(String master, List<String> candidateMoves) {
        List<String> preferred = new ArrayList<>();
        String reasoning = "Fallback style preferences for " + master;
        
        // Add basic style preferences based on master
        boolean isAttacking = false;
        boolean isPositional = false;
        
        switch (master.toLowerCase()) {
            case "alekhine":
                // Alekhine loved tactical complications and attacking play
                if (candidateMoves.size() > 0) {
                    preferred.add(candidateMoves.get(0)); // First candidate often most tactical
                }
                isAttacking = true;
                reasoning = "Alekhine favored dynamic, attacking positions with tactical complexity";
                break;
                
            case "tal":
                isAttacking = true;
                reasoning = "Tal preferred sacrificial attacks and tactical brilliance";
                break;
                
            case "carlsen":
                isPositional = true;
                reasoning = "Carlsen favors practical, endgame-oriented play";
                break;
                
            default:
                reasoning = "General style preferences";
                break;
        }
        
        return new AIStyleAdvice(preferred, 0.4f, reasoning, "", isAttacking, isPositional);
    }
    
    /**
     * 🎯 Get OpenAI Assistant ID for a master
     */
    private String getAssistantId(String master) {
        switch (master.toLowerCase()) {
            case "alekhine":
                return "asst_wnshRkbnaca2vkRxYqYZDcLu"; // From FineTunedModelManager
            case "tal":
                return "asst_LSdhMRFJcSCUJjR4o2B9tWmg";
            case "fischer":
                return "asst_2j5uMiqmEKRUNqHCtXdsaoY3";
            case "carlsen":
                return "asst_TTzxbfvJQz3e80FetQblJ0Gl";
            // Add other masters as needed
            default:
                return null;
        }
    }
    
    /**
     * 🔑 Generate cache key for position and master
     */
    private String generateCacheKey(String fen, String master) {
        // Use position structure (first part of FEN) + master for caching
        String positionPart = fen.split(" ")[0]; // Just piece placement
        return positionPart + "_" + master.toLowerCase();
    }
    
    /**
     * 🧹 Clear cache (call between games)
     */
    public void clearCache() {
        adviceCache.evictAll();
        requestCount = 0;
        Log.d(TAG, "🧹 Cache cleared - ready for new game");
    }
    
    /**
     * 📊 Get cache statistics
     */
    public String getCacheStats() {
        return String.format("Cache: %d/%d entries, %d requests this game", 
                adviceCache.size(), CACHE_SIZE, requestCount);
    }
}