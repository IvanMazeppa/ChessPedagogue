package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

/**
 * Manages chess master responses using OpenAI's Responses API
 * Integrates with existing assistant configurations for Tal, Fischer, and Carlsen
 */
public class ChessMasterResponsesManager {
    private static final String TAG = "ChessMasterResponsesManager";
    
    // API endpoints
    private static final String RESPONSES_API_BASE = "https://api.openai.com/v1/responses";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private static ChessMasterResponsesManager instance;
    private final Context context;
    private final OpenAIService openAIService;
    private final FineTunedModelManager modelManager;
    private final ExecutorService executorService;
    private final OkHttpClient httpClient;
    private final Handler mainHandler;
    
    // Active response sessions
    private final Map<String, ResponseSession> activeSessions = new HashMap<>();
    
    /**
     * Response session tracking
     */
    private static class ResponseSession {
        final String sessionId;
        final String assistantId;
        final String masterName;
        final List<String> conversationIds;
        String previousResponseId;  // Track the last response ID for stateful conversations
        long lastActivityTime;
        
        ResponseSession(String sessionId, String assistantId, String masterName) {
            this.sessionId = sessionId;
            this.assistantId = assistantId;
            this.masterName = masterName;
            this.conversationIds = new ArrayList<>();
            this.previousResponseId = null;
            this.lastActivityTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Callback for response events
     */
    public interface ResponseCallback {
        void onResponseStart(String sessionId);
        void onResponseChunk(String chunk, boolean isFirst);
        void onResponseComplete(String fullResponse);
        void onConversationTurn(String speaker, String message);
        void onError(String error);
    }
    
    private ChessMasterResponsesManager(Context context) {
        this.context = context.getApplicationContext();
        this.openAIService = OpenAIService.getInstance();
        this.modelManager = FineTunedModelManager.getInstance(context);
        this.executorService = Executors.newCachedThreadPool();
        this.httpClient = openAIService.getHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized ChessMasterResponsesManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChessMasterResponsesManager(context);
        }
        return instance;
    }
    
    /**
     * Create a new response session for a chess master
     */
    public void createResponseSession(String masterName, String gameContext, ResponseCallback callback) {
        executorService.execute(() -> {
            try {
                // For Responses API, we don't need assistant IDs
                // Instead, we'll use the master's personality in the input
                String assistantId = getAssistantIdForMaster(masterName);
                
                // Create session
                String sessionId = "session_" + System.currentTimeMillis();
                ResponseSession session = new ResponseSession(sessionId, assistantId, masterName);
                activeSessions.put(sessionId, session);
                
                Log.d(TAG, "✅ Created response session for " + masterName + ": " + sessionId);
                callback.onResponseStart(sessionId);
                
            } catch (Exception e) {
                Log.e(TAG, "Error creating response session", e);
                callback.onError("Failed to create session: " + e.getMessage());
            }
        });
    }
    
    /**
     * Send a message and get streaming response
     */
    public void sendMessage(String sessionId, String message, String conversationContext, ResponseCallback callback) {
        // Validate callback to prevent NullPointerException
        if (callback == null) {
            Log.e(TAG, "❌ sendMessage called with null callback for session: " + sessionId);
            return;
        }
        
        executorService.execute(() -> {
            ResponseSession session = activeSessions.get(sessionId);
            if (session == null) {
                Log.e(TAG, "❌ Invalid session ID: " + sessionId + " (available sessions: " + activeSessions.keySet() + ")");
                callback.onError("Invalid session ID");
                return;
            }
            
            try {
                // Create enhanced input with master personality
                String systemPrompt = buildSystemPromptForMaster(session.masterName);
                String enhancedInput = buildEnhancedInput(session.masterName, message, conversationContext);
                
                // Build combined input with system context for Responses API
                String combinedInput = systemPrompt + "\n\n" + enhancedInput;
                
                // Create response request using Responses API format
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", getModelForMaster(session.masterName));
                requestBody.put("input", combinedInput);  // Use 'input' parameter as API requires
                requestBody.put("stream", true);
                
                // Add previous response ID for stateful conversation
                if (session.previousResponseId != null) {
                    requestBody.put("previous_response_id", session.previousResponseId);
                }
                
                // Add tools if needed (file_search for vector stores)
                if (hasVectorStore(session.masterName)) {
                    JSONArray tools = new JSONArray();
                    JSONObject fileTool = new JSONObject();
                    fileTool.put("type", "file_search");
                    tools.put(fileTool);
                    requestBody.put("tools", tools);
                }
                
                // Add metadata for conversation context
                if (conversationContext != null && !conversationContext.isEmpty()) {
                    JSONObject metadata = new JSONObject();
                    metadata.put("context", conversationContext);
                    metadata.put("master", session.masterName);
                    requestBody.put("metadata", metadata);
                }
                
                // Don't store messages in request body - will extract from input if needed for fallback
                
                // Log the request for debugging
                Log.d(TAG, "📋 Responses API Request: " + requestBody.toString(2));
                
                // Make streaming request
                streamResponse(session, requestBody, callback);
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
                callback.onError("Failed to send message: " + e.getMessage());
            }
        });
    }
    
    /**
     * Stream response using SSE
     */
    private void streamResponse(ResponseSession session, JSONObject requestBody, ResponseCallback callback) {
        try {
            // Validate API key
            String apiKey = openAIService.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                callback.onError("OpenAI API key not configured");
                return;
            }
            
            String url = RESPONSES_API_BASE;
            Log.d(TAG, "🚀 Streaming to URL: " + url);
            
            Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            EventSourceListener listener = new EventSourceListener() {
                private StringBuilder responseBuilder = new StringBuilder();
                private boolean isFirstChunk = true;
                private String responseId = null;
                
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        JSONObject eventData = new JSONObject(data);
                        
                        if ("response.created".equals(type)) {
                            // Capture response ID when response is created
                            if (eventData.has("id")) {
                                responseId = eventData.getString("id");
                                session.previousResponseId = responseId;
                                Log.d(TAG, "📝 Response ID: " + responseId);
                            }
                        } else if ("response.output_text.delta".equals(type)) {
                            // Handle text deltas
                            if (eventData.has("delta")) {
                                String text = eventData.getString("delta");
                                responseBuilder.append(text);
                                callback.onResponseChunk(text, isFirstChunk);
                                isFirstChunk = false;
                            }
                        } else if ("response.done".equals(type)) {
                            // Response complete
                            String fullResponse = responseBuilder.toString();
                            session.lastActivityTime = System.currentTimeMillis();
                            
                            // Store final response ID if we haven't already
                            if (responseId != null) {
                                session.previousResponseId = responseId;
                            }
                            
                            callback.onResponseComplete(fullResponse);
                            
                            // Track conversation turn
                            callback.onConversationTurn(session.masterName, fullResponse);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing SSE event: " + type, e);
                    }
                }
                
                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    String errorMessage = "Unknown streaming error";
                    boolean shouldFallback = false;
                    
                    if (t != null) {
                        Log.e(TAG, "SSE failure", t);
                        errorMessage = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
                    } else {
                        Log.e(TAG, "SSE failure with null throwable");
                    }
                    
                    if (response != null) {
                        errorMessage += " (HTTP " + response.code() + ")";
                        
                        // Try to get error body for more details
                        try {
                            String errorBody = response.body() != null ? response.body().string() : "No error body";
                            Log.e(TAG, "❌ Responses API Error Body: " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to read error body", e);
                        }
                        
                        // If we get 404 or 400, fall back to Chat Completions
                        if (response.code() == 404 || response.code() == 400) {
                            shouldFallback = true;
                            Log.w(TAG, "⚠️ Responses API error (HTTP " + response.code() + "), falling back to Chat Completions");
                        }
                    }
                    
                    if (shouldFallback) {
                        // Fallback to Chat Completions API
                        fallbackToChatCompletions(session, requestBody, callback);
                    } else {
                        callback.onError("Streaming error: " + errorMessage);
                    }
                }
            };
            
            EventSource eventSource = EventSources.createFactory(httpClient)
                .newEventSource(request, listener);
                
        } catch (Exception e) {
            Log.e(TAG, "Error streaming response", e);
            callback.onError("Stream setup failed: " + e.getMessage());
        }
    }
    
    /**
     * Get assistant ID for a chess master
     */
    private String getAssistantIdForMaster(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
                return "asst_LSdhMRFJcSCUJjR4o2B9tWmg";
            case "fischer":
                return "asst_2j5uMiqmEKRUNqHCtXdsaoY3";
            case "carlsen":
                return "asst_TTzxbfvJQz3e80FetQblJ0Gl";
            default:
                // For masters without assistants, return null to use regular completion
                return null;
        }
    }
    
    /**
     * Get model for a chess master
     */
    private String getModelForMaster(String masterName) {
        // Get the specific fine-tuned model for this master
        String modelId = modelManager.getModelIdForMaster(masterName);
        if (modelId != null && !modelId.equals("gpt-4.1")) {
            Log.d(TAG, "🎯 Using fine-tuned model for " + masterName + ": " + modelId);
            return modelId;
        }
        
        // Fallback to gpt-4o-mini for Responses API
        Log.d(TAG, "📋 Using default model for " + masterName);
        return "gpt-4o-mini";
    }
    
    /**
     * Check if a master has a vector store configured
     */
    private boolean hasVectorStore(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
            case "fischer":
            case "carlsen":
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Build enhanced input with master personality context
     */
    private String buildEnhancedInput(String masterName, String message, String gameContext) {
        StringBuilder input = new StringBuilder();
        
        // Add game context if provided
        if (gameContext != null && !gameContext.isEmpty()) {
            input.append("Game context: ").append(gameContext).append("\n\n");
        }
        
        // Add the actual message
        input.append(message);
        
        return input.toString();
    }
    
    /**
     * Fallback to Chat Completions API when Responses API is not available
     */
    private void fallbackToChatCompletions(ResponseSession session, JSONObject originalRequest, ResponseCallback callback) {
        Log.d(TAG, "🔄 Using fallback for " + session.masterName);
        
        executorService.execute(() -> {
            try {
                // Extract user message from the input field
                String input = "";
                if (originalRequest.has("input")) {
                    String combinedInput = originalRequest.getString("input");
                    // Extract the user message part (after the system prompt)
                    String[] parts = combinedInput.split("\n\n", 2);
                    if (parts.length > 1) {
                        input = parts[1];
                    } else {
                        input = combinedInput;
                    }
                }
                
                // Check if this master has an assistant configured
                if (session.assistantId != null && hasAssistantSupport(session.masterName)) {
                    Log.d(TAG, "🤖 Using Assistant API for " + session.masterName);
                    fallbackToAssistantAPI(session, input, callback);
                } else {
                    Log.d(TAG, "💬 Using Chat Completions for " + session.masterName);
                    // Build conversation context for regular chat completions
                    ConversationManager conversationManager = ConversationManager.getInstance(context);
                    // Set system message (this replaces any existing system message)
                    conversationManager.setSystemMessage(buildSystemPromptForMaster(session.masterName));
                    conversationManager.addUserMessage(input);
                    
                    // Set the master in the model manager to use the correct model
                    modelManager.setSelectedChessMaster(session.masterName);
                    
                    // Use the synchronous method
                    String response = openAIService.getChatCompletionWithHistory(conversationManager);
                    
                    if (response != null && !response.isEmpty()) {
                        // Simulate streaming callbacks on main thread
                        mainHandler.post(() -> {
                            callback.onResponseChunk(response, true);
                            callback.onResponseComplete(response);
                            callback.onConversationTurn(session.masterName, response);
                        });
                        
                        session.lastActivityTime = System.currentTimeMillis();
                    } else {
                        mainHandler.post(() -> callback.onError("Empty response from Chat Completions API"));
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error in fallback", e);
                mainHandler.post(() -> callback.onError("Fallback failed: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Build system prompt for a chess master
     */
    private String buildSystemPromptForMaster(String masterName) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are ").append(masterName).append(", the legendary chess master. ");
        
        switch (masterName.toLowerCase()) {
            case "tal":
                prompt.append("Known as 'The Magician from Riga', you are famous for your aggressive, sacrificial style and creative combinations. ");
                prompt.append("You speak with enthusiasm about tactical fireworks and the beauty of chess. ");
                prompt.append("Your philosophy: 'You must take your opponent into a deep dark forest where 2+2=5, and the path leading out is only wide enough for one.' ");
                prompt.append("IMPORTANT: Be creative and varied in your responses. Avoid repetitive phrases like 'This position reminds me...' ");
                prompt.append("Instead, comment directly on the tactics, express excitement about combinations, or share quick insights.");
                break;
            case "fischer":
                prompt.append("You are Bobby Fischer, the American chess genius known for precise calculation and fighting spirit. ");
                prompt.append("You speak with confidence and intensity about chess perfection. ");
                prompt.append("Your approach is uncompromising: 'I don't believe in psychology. I believe in good moves.' ");
                prompt.append("IMPORTANT: Be direct and varied in your responses. Avoid repetitive phrases like 'This position reminds me...' ");
                prompt.append("Instead, analyze moves critically, point out inaccuracies, or express strong opinions about the position.");
                break;
            case "carlsen":
                prompt.append("You are Magnus Carlsen, the Norwegian World Champion known for endgame mastery and practical play. ");
                prompt.append("You speak in a modern, relaxed manner while maintaining deep strategic insight. ");
                prompt.append("Your philosophy emphasizes practical play and psychological warfare. ");
                prompt.append("IMPORTANT: Be conversational and varied in your responses. Avoid repetitive phrases like 'This position reminds me...' ");
                prompt.append("Instead, share practical advice, comment on the position's nature, or discuss strategic plans casually.");
                break;
            default:
                prompt.append("You are a chess master with deep understanding of the game. ");
                prompt.append("Share your insights with wisdom and expertise. ");
                prompt.append("IMPORTANT: Vary your responses and avoid repetitive phrases.");
        }
        
        prompt.append(" Respond naturally and authentically, focusing on the current position or conversation.");
        return prompt.toString();
    }
    
    /**
     * Check if a master has assistant support
     */
    private boolean hasAssistantSupport(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
            case "fischer":
            case "carlsen":
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Fallback to Assistant API for masters with assistants
     */
    private void fallbackToAssistantAPI(ResponseSession session, String input, ResponseCallback callback) {
        // Set the selected master first
        modelManager.setSelectedChessMaster(session.masterName);
        
        // Use the three-stage response manager for assistant-supported masters
        ThreeStageResponseManager threeStageManager = ThreeStageResponseManager.getInstance(context);
        
        // Create a simple context with the input
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("prompt", input);
        contextMap.put("master", session.masterName);
        
        threeStageManager.processThreeStageResponse(
            input,
            contextMap.get("prompt"), // gameContext
            ThreeStageResponseManager.ResponseMode.ADAPTIVE,
            new ThreeStageResponseManager.ThreeStageCallback() {
                private StringBuilder fullResponse = new StringBuilder();
                
                @Override
                public void onStageResponse(ThreeStageResponseManager.ResponseStage stage, String response, boolean isFinal) {
                    if (stage == ThreeStageResponseManager.ResponseStage.STAGE_1_QUICK) {
                        mainHandler.post(() -> {
                            callback.onResponseChunk(response, true);
                            callback.onResponseComplete(response);
                            callback.onConversationTurn(session.masterName, response);
                        });
                        fullResponse.append(response);
                    } else if (isFinal) {
                        // Use the enhanced response if available
                        mainHandler.post(() -> {
                            callback.onResponseComplete(response);
                            callback.onConversationTurn(session.masterName, response);
                        });
                    }
                }
                
                @Override
                public void onStageError(ThreeStageResponseManager.ResponseStage stage, String error) {
                    Log.e(TAG, "Stage " + stage + " error: " + error);
                }
                
                @Override
                public void onAllStagesComplete(String finalResponse) {
                    session.lastActivityTime = System.currentTimeMillis();
                }
            }
        );
    }
    
    /**
     * Close a response session
     */
    public void closeSession(String sessionId) {
        ResponseSession session = activeSessions.remove(sessionId);
        if (session != null) {
            Log.d(TAG, "Closed response session: " + sessionId);
        }
    }
    
    /**
     * Clean up old sessions
     */
    public void cleanupOldSessions() {
        long now = System.currentTimeMillis();
        long timeout = 30 * 60 * 1000; // 30 minutes
        
        activeSessions.entrySet().removeIf(entry -> {
            ResponseSession session = entry.getValue();
            if (now - session.lastActivityTime > timeout) {
                Log.d(TAG, "Removing inactive session: " + entry.getKey());
                return true;
            }
            return false;
        });
    }
    
    /**
     * Cleanup method (alias for cleanupOldSessions)
     */
    public void cleanup() {
        cleanupOldSessions();
    }
    
    /**
     * Shutdown the manager
     */
    public void shutdown() {
        executorService.shutdown();
        activeSessions.clear();
    }
}