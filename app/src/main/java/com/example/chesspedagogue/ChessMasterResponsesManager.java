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
    
    // Retry configuration
    private static final int MAX_RETRIES = 2;
    private static final long INITIAL_RETRY_DELAY = 1000; // 1 second
    
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
                // Only add file_search tool if we have a valid vector store ID
                String vectorStoreId = getVectorStoreIdForMaster(session.masterName);
                if (vectorStoreId != null && !vectorStoreId.isEmpty()) {
                    JSONArray tools = new JSONArray();
                    JSONObject fileTool = new JSONObject();
                    fileTool.put("type", "file_search");
                    
                    // Add vector_store_ids as required by the API
                    JSONArray vectorStoreIds = new JSONArray();
                    vectorStoreIds.put(vectorStoreId);
                    fileTool.put("vector_store_ids", vectorStoreIds);
                    
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
                
                // Make streaming request with retry logic
                streamResponseWithRetry(session, requestBody, callback, 0);
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
                callback.onError("Failed to send message: " + e.getMessage());
            }
        });
    }
    
    /**
     * Stream response with retry logic for transient errors
     */
    private void streamResponseWithRetry(ResponseSession session, JSONObject requestBody, ResponseCallback callback, int retryCount) {
        // Create a wrapper callback that handles retries
        ResponseCallback retryCallback = new ResponseCallback() {
            @Override
            public void onResponseStart(String sessionId) {
                callback.onResponseStart(sessionId);
            }
            
            @Override
            public void onResponseChunk(String chunk, boolean isFirst) {
                callback.onResponseChunk(chunk, isFirst);
            }
            
            @Override
            public void onResponseComplete(String fullResponse) {
                callback.onResponseComplete(fullResponse);
            }
            
            @Override
            public void onConversationTurn(String speaker, String message) {
                callback.onConversationTurn(speaker, message);
            }
            
            @Override
            public void onError(String error) {
                // Check if we should retry for transient errors
                if (error.contains("HTTP 500") || error.contains("HTTP 502") || 
                    error.contains("HTTP 503") || error.contains("HTTP 504")) {
                    if (retryCount < MAX_RETRIES) {
                        long retryDelay = INITIAL_RETRY_DELAY * (long)Math.pow(2, retryCount);
                        Log.w(TAG, "🔄 Retrying after " + retryDelay + "ms (attempt " + (retryCount + 1) + "/" + MAX_RETRIES + ")");
                        
                        mainHandler.postDelayed(() -> {
                            streamResponseWithRetry(session, requestBody, callback, retryCount + 1);
                        }, retryDelay);
                        return;
                    }
                }
                // If no retry or max retries reached, propagate error
                callback.onError(error);
            }
        };
        
        streamResponse(session, requestBody, retryCallback);
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
            
            // Store requestBody as final for use in inner class
            final JSONObject finalRequestBody = requestBody;
            
            Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            // Create a custom listener class to track state
            class ResponseListener extends EventSourceListener {
                private StringBuilder responseBuilder = new StringBuilder();
                private boolean isFirstChunk = true;
                private String responseId = null;
                boolean hasReceivedContent = false;
                private long startTime = System.currentTimeMillis();
                
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    // Log all events for debugging
                    Log.d(TAG, "🔔 SSE Event - Type: " + type + ", ID: " + id + ", Data length: " + 
                        (data != null ? data.length() : 0));
                    
                    // The Responses API might send events differently than expected
                    // Let's check if we're still getting text content but not parsing it correctly
                    if (data != null && !data.trim().isEmpty()) {
                        Log.d(TAG, "📄 SSE Data: " + (data.length() > 200 ? data.substring(0, 200) + "..." : data));
                    }
                    
                    try {
                        // Check if data is [DONE]
                        if ("[DONE]".equals(data)) {
                            // Response complete
                            String fullResponse = responseBuilder.toString();
                            Log.d(TAG, "✅ Response complete: " + fullResponse);
                            session.lastActivityTime = System.currentTimeMillis();
                            
                            if (responseId != null) {
                                session.previousResponseId = responseId;
                            }
                            
                            // Only process if we have actual content
                            if (!fullResponse.trim().isEmpty()) {
                                callback.onResponseComplete(fullResponse);
                                callback.onConversationTurn(session.masterName, fullResponse);
                            } else {
                                Log.w(TAG, "⚠️ Empty response received, falling back");
                                fallbackToChatCompletions(session, finalRequestBody, callback);
                            }
                            return;
                        }
                        
                        JSONObject eventData = new JSONObject(data);
                        
                        // Log the parsed JSON structure
                        Log.d(TAG, "📋 Parsed JSON keys: " + eventData.keys());
                        
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
                                hasReceivedContent = true;
                                Log.d(TAG, "📝 Text chunk: " + text);
                            }
                        } else if ("response.done".equals(type) || "response.completed".equals(type)) {
                            // Response complete - handle both possible event names
                            String fullResponse = responseBuilder.toString();
                            Log.d(TAG, "✅ Response complete event - Full response: " + fullResponse);
                            session.lastActivityTime = System.currentTimeMillis();
                            
                            // Store final response ID if we haven't already
                            if (responseId != null) {
                                session.previousResponseId = responseId;
                            }
                            
                            // Only process if we have actual content
                            if (!fullResponse.trim().isEmpty()) {
                                mainHandler.post(() -> {
                                    callback.onResponseComplete(fullResponse);
                                    callback.onConversationTurn(session.masterName, fullResponse);
                                });
                            } else {
                                Log.w(TAG, "⚠️ Empty response in done event, falling back");
                                fallbackToChatCompletions(session, finalRequestBody, callback);
                            }
                        } else {
                            // Log any other event types we might be missing
                            Log.d(TAG, "📌 Unknown event type: " + type);
                            
                            // Handle response.output_text.done event which contains the complete text
                            if ("response.output_text.done".equals(type)) {
                                // This event contains the full text but we've already accumulated it
                                // via response.output_text.delta events, so just log it
                                if (eventData.has("text")) {
                                    String fullText = eventData.getString("text");
                                    Log.d(TAG, "📄 Output text done event (already accumulated via deltas): " + fullText);
                                }
                            }
                            // Since the Responses API might be sending data in a different format,
                            // let's check if the response text is directly in the event data
                            else if (eventData.has("content") || eventData.has("output")) {
                                String[] possibleKeys = {"content", "output", "message", "response"};
                                for (String key : possibleKeys) {
                                    if (eventData.has(key)) {
                                        String text = eventData.getString(key);
                                        if (!text.trim().isEmpty()) {
                                            Log.d(TAG, "💡 Found text in '" + key + "': " + text);
                                            responseBuilder.append(text);
                                            callback.onResponseChunk(text, isFirstChunk);
                                            isFirstChunk = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing SSE event: " + type, e);
                        Log.e(TAG, "Raw data that failed to parse: " + data);
                        
                        // If we can't parse as JSON, maybe it's a plain text response
                        if (data != null && !data.trim().isEmpty() && !data.startsWith("{")) {
                            Log.d(TAG, "🔄 Treating as plain text response");
                            responseBuilder.append(data);
                            callback.onResponseChunk(data, isFirstChunk);
                            isFirstChunk = false;
                        }
                    }
                }
                
                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    String errorMessage = "Unknown streaming error";
                    boolean shouldFallback = false;
                    
                    // Check if we timed out without receiving content
                    long elapsed = System.currentTimeMillis() - startTime;
                    if (!hasReceivedContent && elapsed > 5000) {
                        Log.w(TAG, "⏱️ No content received after 5 seconds, forcing fallback");
                        shouldFallback = true;
                    }
                    
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
                        
                        // If we get 404, 400, or 500+ errors, fall back to Chat Completions
                        if (response.code() == 404 || response.code() == 400 || response.code() >= 500) {
                            shouldFallback = true;
                            Log.w(TAG, "⚠️ Responses API error (HTTP " + response.code() + "), falling back to Chat Completions");
                        }
                    }
                    
                    if (shouldFallback) {
                        // Fallback to Chat Completions API
                        fallbackToChatCompletions(session, finalRequestBody, callback);
                    } else {
                        // Include HTTP error code in the error message for retry logic
                        callback.onError(errorMessage);
                    }
                }
            }
            
            ResponseListener listener = new ResponseListener();
            EventSource eventSource = EventSources.createFactory(httpClient)
                .newEventSource(request, listener);
            
            // Set up a timeout to fallback if no response is received
            mainHandler.postDelayed(() -> {
                if (!listener.hasReceivedContent && session.lastActivityTime < System.currentTimeMillis() - 8000) {
                    Log.w(TAG, "⏰ Timeout: No response received from Responses API after 8 seconds, forcing fallback");
                    eventSource.cancel();
                    fallbackToChatCompletions(session, requestBody, callback);
                }
            }, 8000); // 8 second timeout
                
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
        String vectorStoreId = getVectorStoreIdForMaster(masterName);
        return vectorStoreId != null && !vectorStoreId.isEmpty();
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
        
        // Common forbidden phrases that apply to all masters
        String forbiddenPhrases = "CRITICAL RULES - NEVER use these phrases: " +
            "'Show me the position', 'I'll tell you what it reminds me of', " +
            "'This position reminds me', 'Without seeing the position', " +
            "'Let me analyze', 'Let me see', 'Looking at the position', " +
            "'First, let me look', 'I need to see', 'From what I can see', " +
            "'This reminds me of', 'It reminds me of', 'Similar to', " +
            "'Just like in my game', 'In my database', 'positions in database'. " +
            "AVOID: Starting responses with questions, asking to see positions, or referencing your database. " +
            "ALWAYS: Jump straight into analysis, commentary, or banter about the CURRENT move. ";
        
        switch (masterName.toLowerCase()) {
            case "tal":
                prompt.append("Known as 'The Magician from Riga', you are famous for your aggressive, sacrificial style and creative combinations. ");
                prompt.append("You speak with passion about tactical fireworks and the beauty of chess. ");
                prompt.append("Your philosophy: 'You must take your opponent into a deep dark forest where 2+2=5.' ");
                prompt.append(forbiddenPhrases);
                prompt.append("INSTEAD: Express immediate excitement about tactics ('What a position for complications!'), ");
                prompt.append("discuss sacrificial themes ('The pieces are begging to be sacrificed!'), ");
                prompt.append("or share your tactical vision ('I see fantastic tactical motifs here!'). ");
                prompt.append("Be spontaneous, passionate, and varied - each response unique to the moment.");
                break;
            case "fischer":
                prompt.append("You are Bobby Fischer, the American chess genius known for precise calculation and fighting spirit. ");
                prompt.append("You speak with confidence and intensity about chess perfection. ");
                prompt.append("Your approach is uncompromising: 'I don't believe in psychology. I believe in good moves.' ");
                prompt.append(forbiddenPhrases);
                prompt.append("INSTEAD: Make definitive judgments ('This move is simply best!'), ");
                prompt.append("criticize imprecision ('That's not accurate chess!'), ");
                prompt.append("or assert your standards ('Only perfect play is acceptable!'). ");
                prompt.append("Be direct, assertive, and uncompromising in your analysis.");
                break;
            case "carlsen":
                prompt.append("You are Magnus Carlsen, the Norwegian World Champion known for endgame mastery and practical play. ");
                prompt.append("You speak in a modern, casual manner while maintaining deep strategic insight. ");
                prompt.append("Your philosophy emphasizes practical play and grinding out wins. ");
                prompt.append(forbiddenPhrases);
                prompt.append("INSTEAD: Comment on practical chances ('I like White's practical chances here'), ");
                prompt.append("discuss endgame prospects ('This structure will be pleasant to play'), ");
                prompt.append("or share strategic insights ('The key is improving that knight'). ");
                prompt.append("Be relaxed, insightful, and focused on practical play.");
                break;
            case "kasparov":
                prompt.append("You are Garry Kasparov, the dynamic attacking player and longest-reigning World Champion. ");
                prompt.append("You speak with energy and passion about dynamic chess and the initiative. ");
                prompt.append("Your philosophy: 'I see chess as a clash of ideas, not just pieces.' ");
                prompt.append(forbiddenPhrases);
                prompt.append("INSTEAD: Emphasize dynamics ('The initiative is everything here!'), ");
                prompt.append("discuss attacking chances ('Time to launch an offensive!'), ");
                prompt.append("or analyze energy flow ('White must act energetically or lose momentum!'). ");
                prompt.append("Be energetic, analytical, and focused on dynamic factors.");
                break;
            case "karpov":
                prompt.append("You are Anatoly Karpov, the positional genius known for subtle maneuvering. ");
                prompt.append("You speak calmly about strategic refinements and small advantages. ");
                prompt.append("Your style emphasizes positional pressure and prophylaxis. ");
                prompt.append(forbiddenPhrases);
                prompt.append("INSTEAD: Discuss positional nuances ('The bishop pair gives lasting pressure'), ");
                prompt.append("evaluate pawn structures ('This structure favors patient maneuvering'), ");
                prompt.append("or suggest refinements ('First improve all pieces, then strike'). ");
                prompt.append("Be calm, strategic, and focused on long-term factors.");
                break;
            case "kramnik":
                prompt.append("You are Vladimir Kramnik, the deep positional player who dethroned Kasparov. ");
                prompt.append("You speak thoughtfully about deep preparation and technical precision. ");
                prompt.append("Your approach combines classical chess with computer-age preparation. ");
                prompt.append(forbiddenPhrases);
                prompt.append("INSTEAD: Share opening insights ('This move order avoids theoretical problems'), ");
                prompt.append("discuss technical aspects ('The resulting endgame is technically winning'), ");
                prompt.append("or evaluate structures ('Black's setup is very solid but passive'). ");
                prompt.append("Be analytical, precise, and focused on technical excellence.");
                break;
            default:
                prompt.append("You are a chess master with deep understanding of the game. ");
                prompt.append("Share your insights with wisdom and expertise. ");
                prompt.append(forbiddenPhrases);
                prompt.append("Focus on the specific position and moves being played.");
        }
        
        prompt.append(" Keep responses concise (2-3 sentences max) and directly relevant. ");
        prompt.append("IMPORTANT: Vary your responses - never repeat the same phrases or patterns.");
        return prompt.toString();
    }
    
    /**
     * Get vector store ID for a chess master
     */
    private String getVectorStoreIdForMaster(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
                return "vs_5c3c6db00ed48c09a56ee0c45d6b5fb8";
            case "fischer":
                // If vector store not found, return null to skip file_search tool
                // This will allow Responses API to work without vector store
                return null; // "vs_6e96708b0ad849b8bd3fd7bfb977f15f" not found
            case "carlsen":
                return "vs_68365028eb988191b09d8d50e6f11b5d";
            default:
                return null;
        }
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