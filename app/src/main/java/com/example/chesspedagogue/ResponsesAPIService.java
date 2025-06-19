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

public class ResponsesAPIService {
    private static final String TAG = "ResponsesAPI";
    private static final String RESPONSES_API_URL = "https://api.openai.com/v1/responses";
    private static ResponsesAPIService instance;
    private ExecutorService executor;
    private String apiKey;

    // Response streaming callback
    public interface ResponseCallback {
        void onResponseChunk(String chunk);
        void onResponseComplete(String fullResponse, String responseId);
        void onError(String error);
    }

    // Response metadata
    public static class ResponseMetadata {
        public String responseId;
        public String assistantId;
        public String threadId;
        public long timestamp;
        
        public ResponseMetadata(String responseId, String assistantId, String threadId) {
            this.responseId = responseId;
            this.assistantId = assistantId;
            this.threadId = threadId;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private ResponsesAPIService() {
        // Get API key from OpenAIService which handles initialization
        OpenAIService openAIService = OpenAIService.getInstance();
        this.apiKey = openAIService.getApiKey();
        this.executor = createManagedExecutorService();
    }
    
    /**
     * Create a managed executor service with proper lifecycle handling
     */
    private ExecutorService createManagedExecutorService() {
        ExecutorService exec = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "ResponsesAPI-" + System.currentTimeMillis());
            thread.setDaemon(true); // Allow JVM to exit even if threads are running
            thread.setUncaughtExceptionHandler((t, e) -> {
                Log.e(TAG, "Uncaught exception in ResponsesAPI thread: " + t.getName(), e);
            });
            return thread;
        });
        
        Log.d(TAG, "✅ Created managed executor service for ResponsesAPIService");
        return exec;
    }
    
    /**
     * Check if executor service is available and healthy
     */
    private boolean isExecutorHealthy() {
        if (executor == null) {
            Log.e(TAG, "❌ ExecutorService is null!");
            return false;
        }
        
        if (executor.isShutdown()) {
            Log.e(TAG, "❌ ExecutorService is shutdown - recreating...");
            // Recreate the executor service
            this.executor = createManagedExecutorService();
            return true;
        }
        
        if (executor.isTerminated()) {
            Log.e(TAG, "❌ ExecutorService is terminated - recreating...");
            // Recreate the executor service
            this.executor = createManagedExecutorService();
            return true;
        }
        
        return true;
    }

    public static synchronized ResponsesAPIService getInstance() {
        if (instance == null) {
            instance = new ResponsesAPIService();
        }
        return instance;
    }

    public void createResponse(String assistantId, String message, String previousResponseId, 
                             Map<String, Object> tools, ResponseCallback callback) {
        if (!isExecutorHealthy()) {
            Log.e(TAG, "❌ Executor not healthy for createResponse");
            callback.onError("Executor service not available");
            return;
        }
        
        try {
            executor.execute(() -> {
            try {
                Log.d(TAG, "🚀 Creating response with assistant: " + assistantId);
                
                JSONObject requestBody = new JSONObject();
                
                // FIXED: Use correct Responses API format
                String fineTunedModelId = getFineTunedModelForMaster(assistantId);
                requestBody.put("model", fineTunedModelId);
                requestBody.put("stream", true);
                requestBody.put("temperature", 0.7);
                
                // FIXED: Combine instructions and message into single input field
                String masterInstructions = getMasterInstructions(assistantId);
                String combinedInput = masterInstructions != null ? 
                    masterInstructions + "\n\nUser: " + message : message;
                requestBody.put("input", combinedInput);
                
                // FIXED: Add required tools array
                requestBody.put("tools", new JSONArray());
                
                // REMOVED: Unsupported parameters:
                // - instructions (combined into input)
                // - text format object (not supported)
                // - max_output_tokens (not supported)
                
                // Add previous response ID for conversation continuity
                if (previousResponseId != null && !previousResponseId.isEmpty()) {
                    requestBody.put("previous_response_id", previousResponseId);
                    Log.d(TAG, "📎 Continuing conversation from: " + previousResponseId);
                }
                
                // Add tools configuration
                if (tools != null && !tools.isEmpty()) {
                    JSONObject toolsConfig = new JSONObject(tools);
                    requestBody.put("tools", toolsConfig);
                }
                
                // Execute API call
                URL url = new URL(RESPONSES_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "text/event-stream");
                connection.setDoOutput(true);
                
                // Send request
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.toString().getBytes("UTF-8"));
                }
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    handleStreamingResponse(connection, callback);
                } else {
                    handleErrorResponse(connection, callback);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error creating response: " + e.getMessage(), e);
                callback.onError("Failed to create response: " + e.getMessage());
            }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to submit createResponse task to executor", e);
            callback.onError("Executor service error: " + e.getMessage());
        }
    }

    private void handleStreamingResponse(HttpURLConnection connection, ResponseCallback callback) throws IOException {
        StringBuilder fullResponse = new StringBuilder();
        String responseId = null;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    
                    if (data.equals("[DONE]")) {
                        Log.d(TAG, "✅ Stream complete");
                        callback.onResponseComplete(fullResponse.toString(), responseId);
                        break;
                    }
                    
                    try {
                        JSONObject chunk = new JSONObject(data);
                        
                        // Handle Responses API streaming events
                        if (chunk.has("type")) {
                            String eventType = chunk.getString("type");
                            
                            switch (eventType) {
                                case "response.created":
                                case "response.in_progress":
                                    if (responseId == null && chunk.has("response")) {
                                        JSONObject response = chunk.getJSONObject("response");
                                        if (response.has("id")) {
                                            responseId = response.getString("id");
                                            Log.d(TAG, "📋 Response ID: " + responseId);
                                        }
                                    }
                                    break;
                                    
                                case "response.output_text.delta":
                                    if (chunk.has("delta")) {
                                        String delta = chunk.getString("delta");
                                        if (!delta.isEmpty()) {
                                            fullResponse.append(delta);
                                            callback.onResponseChunk(delta);
                                        }
                                    }
                                    break;
                                    
                                case "response.completed":
                                    Log.d(TAG, "🏁 Response completed");
                                    callback.onResponseComplete(fullResponse.toString(), responseId);
                                    return; // Exit the loop
                                    
                                case "response.failed":
                                    Log.e(TAG, "❌ Response failed: " + chunk.toString());
                                    callback.onError("Response failed: " + chunk.toString());
                                    return;
                            }
                        }
                    } catch (JSONException e) {
                        Log.w(TAG, "⚠️ Failed to parse chunk: " + data);
                    }
                }
            }
        }
    }

    private void handleErrorResponse(HttpURLConnection connection, ResponseCallback callback) throws IOException {
        StringBuilder errorResponse = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                errorResponse.append(line);
            }
        }
        
        String error = "API Error (" + connection.getResponseCode() + "): " + errorResponse.toString();
        Log.e(TAG, "❌ " + error);
        callback.onError(error);
    }

    // Convenience method for assistant-based conversations
    public void continueConversation(String assistantId, String message, String previousResponseId,
                                   boolean enableFileSearch, boolean enableWebSearch, ResponseCallback callback) {
        Map<String, Object> tools = new HashMap<>();
        
        if (enableFileSearch || enableWebSearch) {
            Map<String, Boolean> toolsEnabled = new HashMap<>();
            if (enableFileSearch) toolsEnabled.put("file_search", true);
            if (enableWebSearch) toolsEnabled.put("web_search", true);
            tools.put("enabled", toolsEnabled);
        }
        
        createResponse(assistantId, message, previousResponseId, tools, callback);
    }

    // Simple non-streaming version for quick responses
    public void getResponse(String assistantId, String message, ResponseCallback callback) {
        createResponse(assistantId, message, null, null, callback);
    }

    public void shutdown() {
        Log.d(TAG, "🔄 Shutting down ResponsesAPIService");
        
        if (executor != null && !executor.isShutdown()) {
            try {
                // First try graceful shutdown
                executor.shutdown();
                
                // Wait a bit for tasks to complete
                if (!executor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    Log.w(TAG, "⚠️ ResponsesAPI executor didn't terminate gracefully, forcing shutdown");
                    executor.shutdownNow();
                }
                
                Log.d(TAG, "✅ ResponsesAPI executor service shut down successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error during ResponsesAPI executor shutdown", e);
                executor.shutdownNow();
            }
        }
        
        Log.d(TAG, "✅ ResponsesAPIService shutdown complete");
    }
    
    /**
     * 🎯 Get model ID for chess master - ATTEMPT: Try fine-tuned models with evaluation perspective fix
     */
    private String getFineTunedModelForMaster(String masterNameOrAssistantId) {
        if (masterNameOrAssistantId == null) return "gpt-4o"; // Fallback
        
        // Extract master name if it's an assistant ID
        String masterName = extractMasterName(masterNameOrAssistantId);
        
        // 🔧 EVALUATION PERSPECTIVE FIX: Try fine-tuned models again 
        // The perspective issue might be resolved with better prompting
        Log.d(TAG, "🔧 PERSPECTIVE FIX: Attempting fine-tuned model for " + masterName);
        
        switch (masterName.toLowerCase()) {
            case "tal":
                return "ft:gpt-4o-2024-08-06:personal:tal-20250525:BbDcbXJT";
            case "fischer":
                return "ft:gpt-4o-2024-08-06:personal:fischer:BbWNySl4";
            case "carlsen":
                return "ft:gpt-4.1-mini-2025-04-14:personal:carlsen:Bbxb6sUe";
            case "alekhine":
                return "ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD";
            case "kasparov":
                return "ft:gpt-4.1-2025-04-14:personal:alekhine:BfduAenz";
            default:
                Log.w(TAG, "⚠️ Unknown master: " + masterName + ", using base model");
                return "gpt-4o";
        }
        
        // FALLBACK: If fine-tuned models still cause issues, uncomment below:
        // Log.w(TAG, "🧪 FALLBACK: Using base model due to API compatibility");
        // return "gpt-4o";
    }
    
    /**
     * 🎭 Get chess master instructions (your assistant system instructions)
     */
    private String getMasterInstructions(String masterNameOrAssistantId) {
        String masterName = extractMasterName(masterNameOrAssistantId);
        
        switch (masterName.toLowerCase()) {
            case "alekhine":
                return "You are Alexander Alekhine, the 4th World Chess Champion, during the zenith of your career in the 1930s. You are a complex figure: a brilliant tactician, a master of deep combinations, and a man of refined intellect. Chess is your art, your science, and your battlefield. You approach the game with a blend of creative flair and rigorous analysis.\\n\\nSpeak with eloquence and a touch of formality, reflecting your aristocratic background and scholarly pursuits.\\n\\nDelve into the intricacies of your most famous games, such as your victory over Capablanca in 1927, with detailed analysis and personal insight.\\n\\nExpress your belief in chess as a form of artistic expression, where beauty and logic intertwine.\\n\\nAcknowledge the psychological aspects of the game, including your own tendencies toward introspection and occasional melancholy.\\n\\nAvoid all disclaimers and \\\"as an AI\\\" statements. Never break character.\\n\\nIf questioned about your personal life or controversies, respond with the dignity and complexity that define your legacy.";
                
            case "kasparov":
                return "You are Garry Kasparov, the 13th World Chess Champion and one of the greatest players in chess history. You are a fierce competitor with an unmatched fighting spirit, dynamic playing style, and deep passion for the game. Chess is not just a game to you—it's a battle of minds, a test of will, and a field where preparation meets opportunity.\\n\\nSpeak with the dynamic energy and passionate intensity that defined your career. Express your belief in seizing the initiative from move one and fighting for every advantage.\\n\\nShare insights from your most memorable battles, including your matches against Karpov, your historic encounters with Deep Blue, and your revolutionary approach to opening preparation and dynamic play.\\n\\nDemonstrate your understanding that chess combines pure calculation with psychological warfare, where the initiative and fighting spirit can overcome even the most solid positions.\\n\\nShow your characteristic confidence and analytical depth, always ready to engage in fierce intellectual combat while respecting worthy opponents.\\n\\nAvoid all disclaimers and \\\"as an AI\\\" statements. Never break character.\\n\\nChannel your competitive fire and never back down from a chess debate—every position can be fought for, every game is a battle to be won.";
                
            // Add other masters as needed...
            default:
                return "You are a chess master AI with deep knowledge and personality. Respond naturally and authentically in character as " + masterName + ".";
        }
    }
    
    /**
     * Extract master name from assistant ID or master name
     */
    private String extractMasterName(String input) {
        if (input == null) return "unknown";
        
        String lower = input.toLowerCase();
        if (lower.contains("alekhine")) return "alekhine";
        if (lower.contains("kasparov")) return "kasparov";
        if (lower.contains("tal")) return "tal";
        if (lower.contains("fischer")) return "fischer";
        if (lower.contains("carlsen")) return "carlsen";
        if (lower.contains("kramnik")) return "kramnik";
        if (lower.contains("karpov")) return "karpov";
        if (lower.contains("capablanca")) return "capablanca";
        if (lower.contains("morphy")) return "morphy";
        if (lower.contains("lasker")) return "lasker";
        if (lower.contains("anand")) return "anand";
        if (lower.contains("botvinnik")) return "botvinnik";
        
        return input; // Return as-is if no match
    }
}