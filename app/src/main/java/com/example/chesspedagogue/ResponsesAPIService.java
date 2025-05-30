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
    private final ExecutorService executor = Executors.newCachedThreadPool();
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
    }

    public static synchronized ResponsesAPIService getInstance() {
        if (instance == null) {
            instance = new ResponsesAPIService();
        }
        return instance;
    }

    public void createResponse(String assistantId, String message, String previousResponseId, 
                             Map<String, Object> tools, ResponseCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "🚀 Creating response with assistant: " + assistantId);
                
                JSONObject requestBody = new JSONObject();
                requestBody.put("assistant_id", assistantId);
                requestBody.put("stream", true);
                
                // Add messages
                JSONArray messages = new JSONArray();
                JSONObject userMessage = new JSONObject();
                userMessage.put("role", "user");
                userMessage.put("content", message);
                messages.put(userMessage);
                requestBody.put("messages", messages);
                
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
                        
                        // Extract response ID from first chunk
                        if (responseId == null && chunk.has("id")) {
                            responseId = chunk.getString("id");
                            Log.d(TAG, "📋 Response ID: " + responseId);
                        }
                        
                        // Extract content delta
                        if (chunk.has("choices")) {
                            JSONArray choices = chunk.getJSONArray("choices");
                            if (choices.length() > 0) {
                                JSONObject choice = choices.getJSONObject(0);
                                if (choice.has("delta") && choice.getJSONObject("delta").has("content")) {
                                    String content = choice.getJSONObject("delta").getString("content");
                                    fullResponse.append(content);
                                    callback.onResponseChunk(content);
                                }
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
        executor.shutdown();
    }
}