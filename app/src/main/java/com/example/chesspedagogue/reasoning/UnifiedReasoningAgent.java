package com.example.chesspedagogue.reasoning;

import android.content.Context;
import android.util.Log;
import com.example.chesspedagogue.ApiKeys;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unified reasoning agent that handles all chess masters via configuration
 */
public class UnifiedReasoningAgent {
    private static final String TAG = "UnifiedReasoning";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/responses";
    private static final Pattern UCI_MOVE_PATTERN = Pattern.compile("\\b([a-h][1-8][a-h][1-8][qrbnkQRBNK]?)\\b");
    
    private MasterConfiguration currentConfig;
    private final String apiKey;
    private final Context context;
    private int currentTargetElo = 2200; // Default Elo level

    public UnifiedReasoningAgent(Context context) {
        this.context = context;
        
        // Get API key from ApiKeys class
        String tempApiKey;
        try {
            tempApiKey = ApiKeys.getOpenAIKey();
            Log.d(TAG, "✅ UnifiedReasoningAgent initialized with API key from ApiKeys class");
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to get OpenAI API key from ApiKeys class: " + e.getMessage());
            tempApiKey = "";
        }
        this.apiKey = tempApiKey;
    }

    /**
     * Set the current chess master configuration
     */
    public void setMaster(String masterName) {
        this.currentConfig = MasterConfigurationManager.getConfig(masterName);
        if (currentConfig != null) {
            Log.d(TAG, String.format("🎭 Configured for master: %s (%s)", 
                    currentConfig.getMasterName(), currentConfig.getReasoningModel()));
        } else {
            Log.w(TAG, "⚠️ No configuration found for master: " + masterName);
        }
    }
    
    /**
     * Set target playing strength for adaptive difficulty
     */
    public void setTargetElo(int targetElo) {
        this.currentTargetElo = Math.max(1200, Math.min(3200, targetElo)); // Clamp to valid range (1200-3200)
        Log.d(TAG, String.format("🎯 Target Elo set to: %d", this.currentTargetElo));
    }

    /**
     * Select best move using reasoning model
     */
    public ReasoningResponse selectMove(String fen, List<CandidateMove> candidates, String gameContext) {
        if (currentConfig == null) {
            Log.e(TAG, "❌ No master configuration set");
            return createErrorResponse("No master configuration");
        }

        if (apiKey.isEmpty()) {
            Log.e(TAG, "❌ OpenAI API key not configured");
            return createErrorResponse("API key not configured");
        }

        long startTime = System.currentTimeMillis();
        
        try {
            Log.d(TAG, String.format("🧠 Making reasoning API call for %s (model: %s)", 
                    currentConfig.getMasterName(), currentConfig.getReasoningModel()));
            
            // Build request payload
            JSONObject requestPayload = buildRequestPayload(fen, candidates, gameContext);
            
            // Make HTTP request to Responses API
            String responseBody = makeHttpRequest(requestPayload);
            
            // Parse response and extract move
            ReasoningResponse response = parseResponse(responseBody, startTime);
            
            Log.d(TAG, String.format("✅ Reasoning complete: move=%s, confidence=%.2f, time=%dms", 
                    response.getMove(), response.getConfidence(), response.getReasoningTimeMs()));
            
            return response;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Reasoning API call failed: " + e.getMessage(), e);
            return createErrorResponse("API call failed: " + e.getMessage());
        }
    }

    /**
     * Build JSON payload for Responses API request
     */
    private JSONObject buildRequestPayload(String fen, List<CandidateMove> candidates, String gameContext) throws Exception {
        JSONObject payload = new JSONObject();
        
        // Core request parameters - MATCH WORKING OPTIMIZED ALEKHINE AGENT
        payload.put("model", currentConfig.getReasoningModel());
        
        // Build input as string (Responses API format)
        payload.put("input", buildUserPrompt(fen, candidates, gameContext));
        
        // Instructions (system message) separate from input
        payload.put("instructions", currentConfig.getSystemInstructions());
        payload.put("max_output_tokens", currentConfig.getMaxOutputTokens());
        payload.put("store", true); // Match working agent
        // NOTE: o4-mini doesn't support temperature parameter in Responses API
        
        // Reasoning configuration
        JSONObject reasoning = new JSONObject();
        reasoning.put("effort", currentConfig.getReasoningEffort());
        payload.put("reasoning", reasoning);
        
        // Function calling and vector store integration
        JSONArray tools = new JSONArray();
        
        // Add move evaluation function (correct Responses API format)
        JSONObject moveEvalFunction = new JSONObject();
        moveEvalFunction.put("type", "function");
        moveEvalFunction.put("name", "evaluate_move_strength");
        moveEvalFunction.put("description", "REQUIRED: Call this to evaluate if your selected move matches the target playing strength before responding");
        moveEvalFunction.put("strict", true);
        
        JSONObject moveEvalParams = new JSONObject();
        moveEvalParams.put("type", "object");
        moveEvalParams.put("additionalProperties", false);
        
        JSONObject moveEvalProperties = new JSONObject();
        
        JSONObject moveParam = new JSONObject();
        moveParam.put("type", "string");
        moveParam.put("description", "The chess move in UCI notation (e.g., e2e4)");
        moveEvalProperties.put("move", moveParam);
        
        JSONObject strengthParam = new JSONObject();
        strengthParam.put("type", "integer");
        strengthParam.put("description", "Target Elo rating (1200-3200)");
        moveEvalProperties.put("target_elo", strengthParam);
        
        JSONObject reasonParam = new JSONObject();
        reasonParam.put("type", "string");
        reasonParam.put("description", "Brief explanation of why this move fits the target strength");
        moveEvalProperties.put("reasoning", reasonParam);
        
        moveEvalParams.put("properties", moveEvalProperties);
        JSONArray requiredParams = new JSONArray();
        requiredParams.put("move");
        requiredParams.put("target_elo");
        requiredParams.put("reasoning");
        moveEvalParams.put("required", requiredParams);
        
        moveEvalFunction.put("parameters", moveEvalParams);
        tools.put(moveEvalFunction);
        
        // Add vector store if available
        if (!currentConfig.getVectorStoreId().isEmpty()) {
            JSONObject vectorTool = new JSONObject();
            vectorTool.put("type", "file_search");
            
            JSONArray vectorStores = new JSONArray();
            vectorStores.put(currentConfig.getVectorStoreId());
            vectorTool.put("vector_store_ids", vectorStores);
            vectorTool.put("max_num_results", 5);
            
            tools.put(vectorTool);
            
            // Include file search results
            JSONArray include = new JSONArray();
            include.put("file_search_call.results");
            payload.put("include", include);
        }
        
        payload.put("tools", tools);
        
        Log.d(TAG, String.format("📤 Request payload: model=%s, effort=%s, tokens=%d", 
                currentConfig.getReasoningModel(), 
                currentConfig.getReasoningEffort(), currentConfig.getMaxOutputTokens()));
        
        return payload;
    }

    /**
     * Build user prompt with position and candidate moves
     */
    private String buildUserPrompt(String fen, List<CandidateMove> candidates, String gameContext) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("CHESS POSITION ANALYSIS\n\n");
        prompt.append("Current Position (FEN): ").append(fen).append("\n\n");
        
        if (gameContext != null && !gameContext.isEmpty()) {
            prompt.append("Game Context:\n").append(gameContext).append("\n\n");
        }
        
        // Add candidate moves
        prompt.append("Available Candidate Moves:\n");
        for (int i = 0; i < candidates.size(); i++) {
            CandidateMove candidate = candidates.get(i);
            prompt.append(String.format("%d. %s (eval: %s, depth: %d, quality: %s)\n", 
                    i + 1, candidate.getMove(), candidate.getEvaluationText(), 
                    candidate.getDepth(), candidate.getQualityAssessment()));
            
            if (!candidate.getVariation().isEmpty()) {
                prompt.append("   Variation: ").append(candidate.getVariation()).append("\n");
            }
        }
        
        prompt.append("\nCURRENT TARGET ELO: ").append(getCurrentTargetElo()).append("\n\n");
        
        prompt.append("ANALYSIS PROCESS:\n");
        prompt.append("1. Analyze the position using your chess knowledge and characteristic style\n");
        prompt.append("2. Consider each candidate move based on your playing preferences\n");
        prompt.append("3. REQUIRED: Call evaluate_move_strength() to validate your choice meets the target strength\n");
        prompt.append("4. Return ONLY the UCI move notation (e.g., e2e4, g1f3)\n");
        
        return prompt.toString();
    }
    
    /**
     * Get current target Elo for strength enforcement
     */
    private int getCurrentTargetElo() {
        // This will be set by the ReasoningEngineManager
        return currentTargetElo > 0 ? currentTargetElo : 2200; // Default fallback
    }

    /**
     * Make HTTP request to OpenAI Responses API
     */
    private String makeHttpRequest(JSONObject payload) throws IOException {
        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set request properties
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        
        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        // Read response
        int responseCode = connection.getResponseCode();
        Log.d(TAG, "📡 HTTP response code: " + responseCode);
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode + ": " + response.toString());
        }
        
        return response.toString();
    }

    /**
     * Parse response from Responses API
     */
    private ReasoningResponse parseResponse(String responseBody, long startTime) throws Exception {
        JSONObject response = new JSONObject(responseBody);
        
        // Check for API errors first
        if (response.has("error") && !response.isNull("error")) {
            JSONObject error = response.getJSONObject("error");
            throw new Exception("OpenAI API Error: " + error.getString("message"));
        }
        
        // Extract content using Responses API format (match OptimizedAlekhineAgent)
        String content = extractResponseContent(response);
        
        // Log token usage for monitoring optimization
        String reasoning = "";
        try {
            JSONObject usage = response.optJSONObject("usage");
            if (usage != null) {
                int totalTokens = usage.optInt("total_tokens");
                JSONObject outputDetails = usage.optJSONObject("output_tokens_details");
                int reasoningTokens = outputDetails != null ? outputDetails.optInt("reasoning_tokens") : 0;
                Log.d(TAG, String.format("🧠 REASONING: %d total tokens (%d reasoning)", totalTokens, reasoningTokens));
                reasoning = String.format("Used %d reasoning tokens for analysis", reasoningTokens);
            }
        } catch (Exception e) {
            // Ignore token logging errors
        }
        
        // Parse move from content
        String move = extractMoveFromContent(content);
        if (move.isEmpty()) {
            throw new Exception("No valid move found in response: " + content);
        }
        
        // Calculate confidence based on response quality
        float confidence = calculateConfidence(content, reasoning, response);
        
        long endTime = System.currentTimeMillis();
        
        return new ReasoningResponse.Builder(move)
                .explanation(reasoning.isEmpty() ? content : reasoning)
                .confidence(confidence)
                .masterPersonality(extractPersonalityComments(content))
                .reasoningTime(endTime - startTime)
                .rawResponse(responseBody)
                .build();
    }

    /**
     * Extract content from Responses API response (matching OptimizedAlekhineAgent)
     */
    private String extractResponseContent(JSONObject response) throws Exception {
        if (!response.has("output")) {
            throw new Exception("No output found in Responses API response");
        }
        
        JSONArray output = response.getJSONArray("output");
        if (output.length() == 0) {
            if (response.optString("status").equals("incomplete")) {
                Log.w(TAG, "⚠️ Incomplete response detected");
                throw new Exception("Response incomplete - reasoning tokens exceeded limit");
            } else {
                throw new Exception("No output found");
            }
        }
        
        // Check for function calls first (new adaptive strength system)
        String functionCallMove = null;
        for (int i = 0; i < output.length(); i++) {
            JSONObject outputItem = output.getJSONObject(i);
            if ("function_call".equals(outputItem.optString("type"))) {
                String functionName = outputItem.optString("name");
                if ("evaluate_move_strength".equals(functionName)) {
                    try {
                        String arguments = outputItem.optString("arguments");
                        JSONObject args = new JSONObject(arguments);
                        functionCallMove = args.optString("move");
                        String reasoning = args.optString("reasoning", "Move strength evaluation completed");
                        int targetElo = args.optInt("target_elo", currentTargetElo);
                        
                        Log.d(TAG, String.format("🎯 Function call: move=%s, target_elo=%d", functionCallMove, targetElo));
                        Log.d(TAG, String.format("📝 Reasoning: %s", reasoning));
                        
                        if (!functionCallMove.isEmpty()) {
                            return functionCallMove; // Return the move from function call
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "⚠️ Error parsing function call arguments: " + e.getMessage());
                    }
                }
            }
        }
        
        // Find message output (fallback for non-function responses)
        for (int i = 0; i < output.length(); i++) {
            JSONObject outputItem = output.getJSONObject(i);
            if ("message".equals(outputItem.optString("type"))) {
                JSONArray content = outputItem.optJSONArray("content");
                if (content != null && content.length() > 0) {
                    JSONObject contentItem = content.getJSONObject(0);
                    
                    // Try text field first
                    if (contentItem.has("text")) {
                        return contentItem.getString("text");
                    }
                    
                    // Try output_text.text structure
                    if (contentItem.has("output_text")) {
                        JSONObject outputText = contentItem.getJSONObject("output_text");
                        if (outputText.has("text")) {
                            return outputText.getString("text");
                        }
                    }
                }
            }
        }
        
        // If we found a function call move but no message, return the function call result
        if (functionCallMove != null && !functionCallMove.isEmpty()) {
            Log.d(TAG, "✅ Using move from function call: " + functionCallMove);
            return functionCallMove;
        }
        
        throw new Exception("No text content found in message output");
    }

    /**
     * Extract UCI move from response content
     */
    private String extractMoveFromContent(String content) {
        Matcher matcher = UCI_MOVE_PATTERN.matcher(content);
        if (matcher.find()) {
            String move = matcher.group(1).toLowerCase();
            Log.d(TAG, "🎯 Extracted move from response: " + move);
            return move;
        }
        
        Log.w(TAG, "⚠️ No UCI move found in content: " + content);
        return "";
    }

    /**
     * Calculate confidence score based on response quality
     */
    private float calculateConfidence(String content, String reasoning, JSONObject response) {
        float confidence = 0.5f; // Base confidence
        
        // Higher confidence if move is clearly stated
        if (UCI_MOVE_PATTERN.matcher(content).find()) {
            confidence += 0.2f;
        }
        
        // Higher confidence if reasoning is provided
        if (!reasoning.isEmpty() && reasoning.length() > 50) {
            confidence += 0.2f;
        }
        
        // Check usage statistics for quality indicators
        try {
            if (response.has("usage")) {
                JSONObject usage = response.getJSONObject("usage");
                int reasoningTokens = usage.optInt("reasoning_tokens", 0);
                
                // More reasoning tokens usually indicate better analysis
                if (reasoningTokens > 1000) confidence += 0.1f;
                if (reasoningTokens > 5000) confidence += 0.1f;
            }
        } catch (Exception e) {
            // Ignore usage parsing errors
        }
        
        return Math.min(1.0f, confidence);
    }

    /**
     * Extract personality-specific comments from response
     */
    private String extractPersonalityComments(String content) {
        // Look for personality indicators in the response
        String[] personalityKeywords = {
            "aggressive", "tactical", "sacrifice", "attack", "dynamic",
            "positional", "strategic", "patient", "precise", "calculated"
        };
        
        StringBuilder personalityComment = new StringBuilder();
        String lowerContent = content.toLowerCase();
        
        for (String keyword : personalityKeywords) {
            if (lowerContent.contains(keyword)) {
                if (personalityComment.length() > 0) personalityComment.append(", ");
                personalityComment.append(keyword);
            }
        }
        
        return personalityComment.toString();
    }

    /**
     * Create error response for fallback
     */
    private ReasoningResponse createErrorResponse(String error) {
        return new ReasoningResponse.Builder("e2e4") // Default fallback move
                .explanation("Error: " + error)
                .confidence(0.1f)
                .build();
    }
}