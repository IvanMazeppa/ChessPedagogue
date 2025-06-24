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
     * 🧠 ENHANCED: Build style evaluation prompt for ranking ELO-appropriate moves  
     * This focuses on STYLE FIT rather than move generation
     */
    private String buildChessStylePrompt(String fen, String master, List<String> candidateMoves) {
        StringBuilder prompt = new StringBuilder();
        
        // Determine color to move
        String[] fenParts = fen.split(" ");
        String colorToMove = fenParts.length > 1 ? fenParts[1] : "w";
        String colorName = colorToMove.equals("w") ? "WHITE" : "BLACK";
        
        prompt.append("🎯 STYLE EVALUATION TASK FOR ").append(master.toUpperCase()).append("\n\n");
        
        prompt.append("Position (FEN): ").append(fen).append("\n");
        prompt.append("You are playing as ").append(colorName).append(" in this position.\n\n");
        
        prompt.append("CANDIDATE MOVES (all legal and appropriate for this playing strength):\n");
        for (int i = 0; i < candidateMoves.size(); i++) {
            prompt.append((i + 1)).append(". ").append(candidateMoves.get(i)).append("\n");
        }
        
        prompt.append("\n🎭 YOUR MISSION: Rank these moves by how well they fit ").append(master.toUpperCase()).append("'s playing style.\n\n");
        
        // Add master-specific style characteristics
        prompt.append(getMasterStyleCharacteristics(master));
        
        prompt.append("\n📊 For EACH move, provide a style score (0.0-1.0) where:\n");
        prompt.append("• 1.0 = Perfectly embodies ").append(master).append("'s style\n");
        prompt.append("• 0.5 = Neutral, could be any master\n");
        prompt.append("• 0.0 = Completely against ").append(master).append("'s style\n\n");
        
        prompt.append("Respond in JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"move_scores\": {\n");
        for (int i = 0; i < candidateMoves.size(); i++) {
            prompt.append("    \"").append(candidateMoves.get(i)).append("\": {\"score\": 0.0, \"reason\": \"why this fits/doesn't fit\"}");
            if (i < candidateMoves.size() - 1) prompt.append(",");
            prompt.append("\n");
        }
        prompt.append("  },\n");
        prompt.append("  \"top_choice\": \"").append(candidateMoves.get(0)).append("\",\n");
        prompt.append("  \"style_reasoning\": \"Overall style assessment\",\n");
        prompt.append("  \"confidence\": 0.8\n");
        prompt.append("}");
        
        return prompt.toString();
    }
    
    /**
     * 🎭 Get master-specific style characteristics for enhanced prompting
     */
    private String getMasterStyleCharacteristics(String master) {
        switch (master.toLowerCase()) {
            case "alekhine":
                return "ALEKHINE'S STYLE:\n" +
                      "• Aggressive, dynamic play with psychological pressure\n" +
                      "• Prefers complex, imbalanced positions\n" +
                      "• Willing to sacrifice material for initiative\n" +
                      "• Loves tactical complications and attacking play\n" +
                      "• Avoids simple, quiet, symmetrical positions\n" +
                      "• Seeks to create maximum difficulty for opponent";
                      
            case "tal":
                return "TAL'S STYLE:\n" +
                      "• Brilliant tactical sacrifices and combinations\n" +
                      "• Intuitive, attacking play over pure calculation\n" +
                      "• Prefers sharp, tactical positions\n" +
                      "• Will sacrifice material for attack potential\n" +
                      "• Avoids dry, positional grind-it-out games";
                      
            case "carlsen":
                return "CARLSEN'S STYLE:\n" +
                      "• Practical, endgame-oriented approach\n" +
                      "• Creates problems from seemingly equal positions\n" +
                      "• Prefers keeping pieces on the board\n" +
                      "• Excellent technique in simplified positions\n" +
                      "• Will choose the move that gives most practical chances";
                      
            case "fischer":
                return "FISCHER'S STYLE:\n" +
                      "• Precise, logical play with perfect technique\n" +
                      "• Seeks clear advantage and methodical improvement\n" +
                      "• Prefers principled development and central control\n" +
                      "• Avoids unnecessary complications\n" +
                      "• Values piece activity and king safety";
                      
            default:
                return "CLASSICAL STYLE:\n" +
                      "• Balanced approach between tactics and strategy\n" +
                      "• Sound development and central control\n" +
                      "• Looks for clear improvements to position";
        }
    }
    
    /**
     * 🧠 ENHANCED: Parse new style evaluation response format
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
            
            // 🎯 NEW: Parse move scores and rank by style fit
            List<String> preferredMoves = new ArrayList<>();
            Map<String, Float> moveScores = new HashMap<>();
            
            if (json.has("move_scores")) {
                JSONObject scoresObj = json.getJSONObject("move_scores");
                
                // Extract all move scores
                for (String move : candidateMoves) {
                    if (scoresObj.has(move)) {
                        JSONObject moveData = scoresObj.getJSONObject(move);
                        float score = (float) moveData.optDouble("score", 0.5);
                        moveScores.put(move, score);
                        
                        // Add to preferred moves if score > 0.6 (strong style fit)
                        if (score > 0.6) {
                            preferredMoves.add(move);
                            Log.d(TAG, "🎭 Style match: " + move + " = " + score);
                        }
                    }
                }
                
                // Sort preferred moves by score (highest first)
                preferredMoves.sort((a, b) -> Float.compare(moveScores.get(b), moveScores.get(a)));
            }
            
            // 🎯 NEW: Get top choice directly
            String topChoice = json.optString("top_choice", "");
            if (!topChoice.isEmpty() && candidateMoves.contains(topChoice) && !preferredMoves.contains(topChoice)) {
                preferredMoves.add(0, topChoice); // Add to front
            }
            
            // Extract other fields with defaults
            float confidence = (float) json.optDouble("confidence", 0.5);
            String reasoning = json.optString("style_reasoning", "Style-based preference");
            String masterInsight = "Style confidence: " + confidence;
            boolean isAttacking = reasoning.toLowerCase().contains("attack");
            boolean isPositional = reasoning.toLowerCase().contains("positional");
            
            Log.d(TAG, "🎭 Parsed style evaluation: " + preferredMoves.size() + " moves, confidence=" + confidence);
            Log.d(TAG, "🎭 AI preferred moves: " + preferredMoves);
            Log.d(TAG, "💭 AI reasoning: " + reasoning);
            
            return new AIStyleAdvice(preferredMoves, confidence, reasoning, 
                                   masterInsight, isAttacking, isPositional);
            
        } catch (JSONException e) {
            Log.w(TAG, "⚠️ JSON parsing failed, falling back to text analysis", e);
            return parseTextResponse(response, master, candidateMoves);
        }
    }
    
    /**
     * 🔧 Extract and fix JSON from AI response text
     */
    private String extractJSON(String response) {
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");
        
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            String rawJson = response.substring(jsonStart, jsonEnd + 1);
            
            // Fix common JSON formatting issues from AI responses
            String fixedJson = fixMalformedJSON(rawJson);
            Log.d(TAG, "🔧 Fixed JSON: " + fixedJson.substring(0, Math.min(100, fixedJson.length())) + "...");
            
            return fixedJson;
        }
        
        return null;
    }
    
    /**
     * 🛠️ Fix common JSON formatting issues from AI responses
     */
    private String fixMalformedJSON(String rawJson) {
        // CRITICAL FIX: Test if JSON is already valid before applying any fixes
        try {
            new JSONObject(rawJson);
            Log.d(TAG, "✅ JSON is already valid - no fixes needed");
            return rawJson; // Return original if it's already valid
        } catch (JSONException e) {
            Log.d(TAG, "🔧 JSON needs fixing: " + e.getMessage());
        }
        
        // Fix mixed quote types - normalize all quotes to standard double quotes
        String fixed = rawJson;
        
        // Replace escaped quotes that should be regular quotes
        // Pattern: \"reason\": \"text\" should become "reason": "text"
        fixed = fixed.replaceAll("\\\\\"([a-zA-Z_]+)\\\\\"\\s*:", "\"$1\":");
        
        // Fix other common escape issues
        fixed = fixed.replaceAll("\\\\\"([^\"]+)\\\\\"", "\"$1\"");
        
        // Ensure all keys and string values use proper double quotes
        // This regex finds unquoted keys and fixes them
        fixed = fixed.replaceAll("([{,]\\s*)([a-zA-Z_][a-zA-Z0-9_]*)\\s*:", "$1\"$2\":");
        
        // Fix trailing commas that break JSON parsing
        fixed = fixed.replaceAll(",\\s*([}\\]])", "$1");
        
        // Fix double quotes inside string values that break JSON
        // Pattern: "text with "quotes" inside" -> "text with 'quotes' inside"
        fixed = fixed.replaceAll("\"([^\"]*?)\"([^\"]*?)\"([^\"]*?)\"\\s*([,}])", "\"$1'$2'$3\"$4");
        
        // Fix specific issue from your log: extra quote at end of string
        fixed = fixed.replaceAll("\"\"\\s*([,}])", "\"$1");
        
        // ONLY apply the problematic regex if there's actually a quote mismatch
        if (fixed.contains("':")  || fixed.contains(":'")) {
            Log.d(TAG, "🔧 Applying quote mismatch fixes");
            // CRITICAL FIX: Handle the specific pattern from logs: "reason': ' instead of "reason": "
            fixed = fixed.replaceAll("\"([a-zA-Z_]+)'\\s*:\\s*'", "\"$1\": \"");
            
            // Fix other quote/colon mismatches
            fixed = fixed.replaceAll("'\\s*:\\s*'", "\": \"");
            fixed = fixed.replaceAll("'\\s*:", "\":");
            fixed = fixed.replaceAll(":\\s*'", ": \"");
        }
        
        Log.d(TAG, "🛠️ JSON fix applied - original length: " + rawJson.length() + ", fixed length: " + fixed.length());
        
        return fixed;
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