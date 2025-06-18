package com.example.chesspedagogue;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enhanced Responses API Service with frequency_penalty and presence_penalty support
 * Optimized for chess master personalities
 */
public class EnhancedResponsesAPIService {
    private static final String TAG = "🎯 EnhancedResponsesAPI";
    private static final String RESPONSES_API_URL = "https://api.openai.com/v1/responses";
    private static EnhancedResponsesAPIService instance;
    private ExecutorService executor;
    private String apiKey;
    
    // Enhanced response callback with master personality context
    public interface EnhancedResponseCallback {
        void onResponseChunk(String chunk, String masterName);
        void onResponseComplete(String fullResponse, String responseId, ResponseMetrics metrics);
        void onError(String error, String masterName);
    }
    
    // Response metrics including penalty effects
    public static class ResponseMetrics {
        public String responseId;
        public String masterName;
        public long responseTimeMs;
        public int tokenCount;
        public float frequencyPenaltyUsed;
        public float presencePenaltyUsed;
        public float temperatureUsed;
        public boolean repetitionDetected;
        
        public ResponseMetrics(String responseId, String masterName) {
            this.responseId = responseId;
            this.masterName = masterName;
            this.responseTimeMs = System.currentTimeMillis();
        }
    }
    
    // Master-specific API configuration
    public static class MasterRequestConfig {
        public String masterName;
        public String assistantId;
        public String fineTunedModel;
        public float temperature;
        public float topP;
        public float frequencyPenalty;
        public float presencePenalty;
        public int maxTokens;
        public String systemInstructions;
        public Map<String, Object> tools;
        
        public MasterRequestConfig(String masterName) {
            this.masterName = masterName;
            // Default values
            this.temperature = 0.7f;
            this.topP = 0.9f;
            this.frequencyPenalty = 0.3f;
            this.presencePenalty = 0.2f;
            this.maxTokens = 1000;
            this.tools = new HashMap<>();
        }
    }
    
    private EnhancedResponsesAPIService() {
        OpenAIService openAIService = OpenAIService.getInstance();
        this.apiKey = openAIService.getApiKey();
        this.executor = createManagedExecutorService();
    }
    
    public static synchronized EnhancedResponsesAPIService getInstance() {
        if (instance == null) {
            instance = new EnhancedResponsesAPIService();
        }
        return instance;
    }
    
    private ExecutorService createManagedExecutorService() {
        ExecutorService exec = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "EnhancedResponsesAPI-" + System.currentTimeMillis());
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler((t, e) -> {
                Log.e(TAG, "Uncaught exception in Enhanced ResponsesAPI thread: " + t.getName(), e);
            });
            return thread;
        });
        
        Log.d(TAG, "✅ Created enhanced managed executor service");
        return exec;
    }
    
    /**
     * Create response with enhanced master configuration including penalties
     */
    public void createEnhancedResponse(MasterRequestConfig config, String message, 
                                     String previousResponseId, EnhancedResponseCallback callback) {
        if (!isExecutorHealthy()) {
            Log.e(TAG, "❌ Executor not healthy for " + config.masterName);
            callback.onError("Executor service not available", config.masterName);
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        executor.execute(() -> {
            try {
                Log.d(TAG, String.format("🚀 Creating response for %s with penalties: freq=%.2f, pres=%.2f", 
                    config.masterName, config.frequencyPenalty, config.presencePenalty));
                
                JSONObject requestBody = buildEnhancedRequestBody(config, message, previousResponseId);
                
                ResponseMetrics metrics = new ResponseMetrics(null, config.masterName);
                metrics.frequencyPenaltyUsed = config.frequencyPenalty;
                metrics.presencePenaltyUsed = config.presencePenalty;
                metrics.temperatureUsed = config.temperature;
                
                // Send request and handle streaming response
                String responseId = sendEnhancedRequest(requestBody, callback, config, metrics, startTime);
                
                Log.d(TAG, String.format("✅ Response completed for %s (ID: %s)", 
                    config.masterName, responseId));
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to create enhanced response for " + config.masterName, e);
                callback.onError("Request failed: " + e.getMessage(), config.masterName);
            }
        });
    }
    
    /**
     * Builds request body with enhanced parameters including penalties
     */
    private JSONObject buildEnhancedRequestBody(MasterRequestConfig config, String message, 
                                              String previousResponseId) throws JSONException {
        JSONObject requestBody = new JSONObject();
        
        // Core model configuration
        requestBody.put("model", config.fineTunedModel);
        requestBody.put("stream", true);
        
        // Enhanced parameters with penalties
        requestBody.put("temperature", config.temperature);
        requestBody.put("top_p", config.topP);
        requestBody.put("frequency_penalty", config.frequencyPenalty);
        requestBody.put("presence_penalty", config.presencePenalty);
        requestBody.put("max_tokens", config.maxTokens);
        
        // Combine system instructions with user message
        String combinedInput = config.systemInstructions != null ? 
            config.systemInstructions + "\n\nUser: " + message : message;
        requestBody.put("input", combinedInput);
        
        // Add tools configuration
        if (config.tools != null && !config.tools.isEmpty()) {
            requestBody.put("tools", new JSONObject(config.tools));
        } else {
            requestBody.put("tools", new JSONArray());
        }
        
        // Conversation continuity
        if (previousResponseId != null && !previousResponseId.isEmpty()) {
            requestBody.put("previous_response_id", previousResponseId);
            Log.d(TAG, "📎 Continuing conversation for " + config.masterName + " from: " + previousResponseId);
        }
        
        // Log configuration for debugging
        Log.d(TAG, String.format("📋 %s config: temp=%.2f, top_p=%.2f, freq_pen=%.2f, pres_pen=%.2f, max_tok=%d", 
            config.masterName, config.temperature, config.topP, 
            config.frequencyPenalty, config.presencePenalty, config.maxTokens));
        
        return requestBody;
    }
    
    /**
     * Sends request and handles streaming response with enhanced metrics
     */
    private String sendEnhancedRequest(JSONObject requestBody, EnhancedResponseCallback callback, 
                                     MasterRequestConfig config, ResponseMetrics metrics, 
                                     long startTime) throws IOException, JSONException {
        
        URL url = new URL(RESPONSES_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Configure connection
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("User-Agent", "ChessPedagogue/1.0");
        connection.setDoOutput(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        
        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        // Handle response
        int responseCode = connection.getResponseCode();
        Log.d(TAG, "🌐 Response code for " + config.masterName + ": " + responseCode);
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return handleStreamingResponse(connection, callback, config, metrics, startTime);
        } else {
            handleErrorResponse(connection, callback, config.masterName);
            return null;
        }
    }
    
    /**
     * Handles streaming response with repetition detection
     */
    private String handleStreamingResponse(HttpURLConnection connection, EnhancedResponseCallback callback,
                                         MasterRequestConfig config, ResponseMetrics metrics,
                                         long startTime) throws IOException, JSONException {
        
        StringBuilder fullResponse = new StringBuilder();
        String responseId = null;
        int tokenCount = 0;
        Map<String, Integer> wordCounts = new HashMap<>(); // For repetition detection
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    
                    if ("[DONE]".equals(data)) {
                        break;
                    }
                    
                    try {
                        JSONObject json = new JSONObject(data);
                        
                        // Extract response ID
                        if (responseId == null && json.has("id")) {
                            responseId = json.getString("id");
                            metrics.responseId = responseId;
                        }
                        
                        // Process content chunks
                        if (json.has("choices")) {
                            JSONArray choices = json.getJSONArray("choices");
                            if (choices.length() > 0) {
                                JSONObject choice = choices.getJSONObject(0);
                                if (choice.has("delta")) {
                                    JSONObject delta = choice.getJSONObject("delta");
                                    if (delta.has("content")) {
                                        String content = delta.getString("content");
                                        fullResponse.append(content);
                                        tokenCount++;
                                        
                                        // Track word repetition for penalty effectiveness
                                        trackWordUsage(content, wordCounts);
                                        
                                        // Send chunk to callback
                                        callback.onResponseChunk(content, config.masterName);
                                    }
                                }
                            }
                        }
                        
                    } catch (JSONException e) {
                        Log.w(TAG, "⚠️ Non-JSON data in stream for " + config.masterName + ": " + data);
                    }
                }
            }
        }
        
        // Finalize metrics
        metrics.responseTimeMs = System.currentTimeMillis() - startTime;
        metrics.tokenCount = tokenCount;
        metrics.repetitionDetected = detectRepetition(wordCounts);
        
        // Log penalty effectiveness
        if (metrics.repetitionDetected) {
            Log.w(TAG, String.format("⚠️ %s: Repetition detected despite penalties (freq=%.2f, pres=%.2f)", 
                config.masterName, config.frequencyPenalty, config.presencePenalty));
        } else {
            Log.d(TAG, String.format("✅ %s: Good variety with penalties (freq=%.2f, pres=%.2f)", 
                config.masterName, config.frequencyPenalty, config.presencePenalty));
        }
        
        // Complete callback
        callback.onResponseComplete(fullResponse.toString(), responseId, metrics);
        
        return responseId;
    }
    
    /**
     * Tracks word usage for repetition detection
     */
    private void trackWordUsage(String content, Map<String, Integer> wordCounts) {
        String[] words = content.toLowerCase().split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", ""); // Remove punctuation
            if (word.length() > 3) { // Only track meaningful words
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }
    }
    
    /**
     * Detects if response has excessive repetition
     */
    private boolean detectRepetition(Map<String, Integer> wordCounts) {
        int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (totalWords < 10) return false; // Too short to analyze
        
        // Check for words used more than 20% of the time
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            float frequency = (float) entry.getValue() / totalWords;
            if (frequency > 0.2f && entry.getValue() > 3) {
                Log.d(TAG, String.format("🔄 Repetition detected: '%s' used %.1f%% (%d times)", 
                    entry.getKey(), frequency * 100, entry.getValue()));
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Creates optimized configuration for specific chess masters
     */
    public static MasterRequestConfig createMasterConfig(String masterName) {
        // Get enhanced config from our new master configurations
        EnhancedAssistantConfigForNewMasters.MasterAPIConfig enhancedConfig = 
            EnhancedAssistantConfigForNewMasters.getNewMasterConfigs().get(masterName);
        
        if (enhancedConfig != null) {
            MasterRequestConfig config = new MasterRequestConfig(masterName);
            config.assistantId = enhancedConfig.assistantId;
            config.fineTunedModel = enhancedConfig.fineTunedModel;
            config.temperature = enhancedConfig.temperature;
            config.topP = enhancedConfig.topP;
            config.frequencyPenalty = enhancedConfig.frequencyPenalty;
            config.presencePenalty = enhancedConfig.presencePenalty;
            config.maxTokens = enhancedConfig.maxTokens;
            config.systemInstructions = enhancedConfig.systemInstructions;
            
            Log.d(TAG, String.format("📋 Created optimized config for %s: freq_pen=%.2f, pres_pen=%.2f", 
                masterName, config.frequencyPenalty, config.presencePenalty));
            
            return config;
        }
        
        // Fallback for existing masters
        return createLegacyMasterConfig(masterName);
    }
    
    /**
     * Creates configuration for existing masters with optimized penalties
     */
    private static MasterRequestConfig createLegacyMasterConfig(String masterName) {
        MasterRequestConfig config = new MasterRequestConfig(masterName);
        
        // Set penalties based on master personality
        switch (masterName) {
            case "Magnus Carlsen":
                config.frequencyPenalty = 0.3f; // Moderate - practical consistency
                config.presencePenalty = 0.2f; // Low - methodical approach
                config.temperature = 0.7f;
                break;
                
            case "Bobby Fischer":
                config.frequencyPenalty = 0.6f; // High - avoid repetitive intensity
                config.presencePenalty = 0.4f; // Medium-high - varied expressions
                config.temperature = 0.8f;
                break;
                
            case "Mikhail Tal":
                config.frequencyPenalty = 0.7f; // Highest - creative variety
                config.presencePenalty = 0.5f; // High - artistic expression
                config.temperature = 0.9f;
                break;
                
            case "Garry Kasparov":
                config.frequencyPenalty = 0.5f; // Medium-high - dynamic variety
                config.presencePenalty = 0.4f; // Medium-high - passionate range
                config.temperature = 0.8f;
                break;
                
            default:
                // Default moderate penalties
                config.frequencyPenalty = 0.3f;
                config.presencePenalty = 0.2f;
                config.temperature = 0.7f;
                break;
        }
        
        Log.d(TAG, String.format("📋 Created legacy config for %s: freq_pen=%.2f, pres_pen=%.2f", 
            masterName, config.frequencyPenalty, config.presencePenalty));
        
        return config;
    }
    
    /**
     * Handle error responses
     */
    private void handleErrorResponse(HttpURLConnection connection, EnhancedResponseCallback callback, 
                                   String masterName) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
            
            StringBuilder error = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                error.append(line);
            }
            
            Log.e(TAG, "❌ Error response for " + masterName + ": " + error.toString());
            callback.onError("API Error: " + error.toString(), masterName);
        }
    }
    
    /**
     * Check executor health
     */
    private boolean isExecutorHealthy() {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            Log.w(TAG, "⚠️ Recreating executor service");
            this.executor = createManagedExecutorService();
            return true;
        }
        return true;
    }
    
    /**
     * Cleanup resources
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            Log.d(TAG, "🔄 Enhanced ResponsesAPI service shutdown");
        }
    }
}