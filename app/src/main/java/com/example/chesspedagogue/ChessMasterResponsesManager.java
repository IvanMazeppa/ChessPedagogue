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
    
    // CONVERSATION THROTTLING - Reduced for more dynamic conversations
    private static final long MIN_CONVERSATION_INTERVAL = 2000; // 2 seconds between responses
    private static final long MIN_MASTER_SWITCH_INTERVAL = 3000; // 3 seconds when switching masters
    private long lastResponseTime = 0;
    private String lastRespondingMaster = null;
    
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
                // Responses API only - no assistant IDs needed
                String sessionId = "session_" + System.currentTimeMillis();
                ResponseSession session = new ResponseSession(sessionId, null, masterName);
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
     * DEBUG: Static test method to verify class loading
     */
    public static void debugTest() {
        Log.e(TAG, "🔧 DEBUG: ChessMasterResponsesManager.debugTest() CALLED SUCCESSFULLY!");
    }
    
    /**
     * DEBUG: Alternative method name to test if method resolution is the issue
     */
    public void sendMessageAlternative(String sessionId, String message, String conversationContext, ResponseCallback callback) {
        // The method IS being called - the custom error message proves this
        // Now let's implement a working version to bypass the original sendMessage
        
        executorService.execute(() -> {
            try {
                Log.e(TAG, "🚀 ALTERNATIVE: Method executing in background thread");
                
                ResponseSession session = activeSessions.get(sessionId);
                if (session == null) {
                    Log.e(TAG, "❌ ALTERNATIVE: Invalid session ID: " + sessionId);
                    callback.onError("Invalid session ID");
                    return;
                }
                
                // Create simple request for testing
                String systemPrompt = buildSystemPromptForMaster(session.masterName, conversationContext, null);
                String enhancedInput = buildEnhancedInput(session.masterName, message, conversationContext);
                
                Log.e(TAG, "🚀 ALTERNATIVE: About to test Responses API call");
                
                // Use proper Responses API call instead of chat completions
                try {
                    Log.e(TAG, "🚀 ALTERNATIVE: Using Responses API");
                    sendMessageInternal(sessionId, message, conversationContext, callback);
                } catch (Exception e) {
                    Log.e(TAG, "🚀 ALTERNATIVE: Responses API error: " + e.getMessage());
                    callback.onError("Responses API error: " + e.getMessage());
                }
                
            } catch (Exception e) {
                Log.e(TAG, "🚀 ALTERNATIVE: Exception in method: " + e.getMessage(), e);
                callback.onError("Alternative method error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Send a message and get streaming response - WITH CONVERSATION THROTTLING
     */
    public void sendMessage(String sessionId, String message, String conversationContext, ResponseCallback callback) {
        sendMessage(sessionId, message, conversationContext, callback, null);
    }
    
    /**
     * 🧠 ENHANCED: Send message with emotional context integration
     */
    public void sendMessage(String sessionId, String message, String conversationContext, 
                           ResponseCallback callback, EmotionalContext emotionalContext) {
        // Validate callback to prevent NullPointerException
        if (callback == null) {
            Log.e(TAG, "❌ sendMessage called with null callback for session: " + sessionId);
            return;
        }
        
        // CONVERSATION THROTTLING - Check if enough time has passed
        long currentTime = System.currentTimeMillis();
        long timeSinceLastResponse = currentTime - lastResponseTime;
        
        ResponseSession session = activeSessions.get(sessionId);
        if (session == null) {
            Log.e(TAG, "❌ Invalid session ID: " + sessionId);
            callback.onError("Invalid session ID");
            return;
        }
        
        // Determine minimum interval based on master switching
        long requiredInterval = MIN_CONVERSATION_INTERVAL;
        if (lastRespondingMaster != null && !lastRespondingMaster.equals(session.masterName)) {
            requiredInterval = MIN_MASTER_SWITCH_INTERVAL;
            Log.d(TAG, "🔄 Master switch detected: " + lastRespondingMaster + " → " + session.masterName + 
                  " (requiring " + requiredInterval + "ms interval)");
        }
        
        if (timeSinceLastResponse < requiredInterval) {
            long waitTime = requiredInterval - timeSinceLastResponse;
            Log.d(TAG, "🛑 THROTTLING: Only " + timeSinceLastResponse + "ms since last response. " +
                  "Waiting " + waitTime + "ms before allowing " + session.masterName + " to respond");
            
            // Schedule the request after the throttling period
            mainHandler.postDelayed(() -> {
                sendMessageInternal(sessionId, message, conversationContext, callback);
            }, waitTime);
            return;
        }
        
        // Immediate execution if throttling check passed
        sendMessageInternal(sessionId, message, conversationContext, callback);
    }
    
    /**
     * Internal method that actually sends the message (after throttling check)
     */
    private void sendMessageInternal(String sessionId, String message, String conversationContext, ResponseCallback callback) {
        Log.d(TAG, "🚀 Sending message for " + sessionId + " after throttling check");
        
        executorService.execute(() -> {
            ResponseSession session = activeSessions.get(sessionId);
            if (session == null) {
                Log.e(TAG, "❌ Session lost during throttling for: " + sessionId);
                callback.onError("Session lost during throttling");
                return;
            }
            
            try {
                // Update throttling tracking
                lastResponseTime = System.currentTimeMillis();
                lastRespondingMaster = session.masterName;
                Log.d(TAG, "⏱️ Updated throttling: last response time set for " + session.masterName);
                
                // Create enhanced input with master personality and context-specific length rules
                String systemPrompt = buildSystemPromptForMaster(session.masterName, conversationContext, null);
                String enhancedInput = buildEnhancedInput(session.masterName, message, conversationContext);
                
                // Create response request using CORRECT Responses API format
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", getModelForMaster(session.masterName));
                requestBody.put("stream", true);
                
                // Use instructions approach for all masters (no more assistants/vector stores)
                Log.d(TAG, "📝 Using instructions format for " + session.masterName + " (migrated from assistants)");
                
                // Use instructions parameter for system prompt
                requestBody.put("instructions", systemPrompt);
                
                // Use simple string input for user message
                requestBody.put("input", enhancedInput);
                
                // Add previous response ID for stateful conversation
                if (session.previousResponseId != null) {
                    requestBody.put("previous_response_id", session.previousResponseId);
                }
                
                // Responses API with instructions can handle file search natively
                // No need for explicit vector store configuration
                
                // Add required text format for Responses API
                JSONObject textFormat = new JSONObject();
                JSONObject formatType = new JSONObject();
                formatType.put("type", "text");
                textFormat.put("format", formatType);
                requestBody.put("text", textFormat);
                
                // Add other required parameters
                requestBody.put("temperature", 1);
                requestBody.put("max_output_tokens", 2048);
                requestBody.put("top_p", 1);
                requestBody.put("store", true);
                
                // Add metadata for conversation context
                if (conversationContext != null && !conversationContext.isEmpty()) {
                    JSONObject metadata = new JSONObject();
                    metadata.put("context", conversationContext);
                    metadata.put("master", session.masterName);
                    requestBody.put("metadata", metadata);
                }
                
                Log.d(TAG, "🚀 Sending throttled request for " + session.masterName);
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
                Log.e(TAG, "❌ API key validation failed: null or empty");
                callback.onError("OpenAI API key not configured");
                return;
            }
            
            // Enhanced API key validation for Responses API
            if (!apiKey.startsWith("sk-")) {
                Log.e(TAG, "❌ API key validation failed: invalid format");
                callback.onError("Invalid OpenAI API key format");
                return;
            }
            
            Log.d(TAG, "✅ API key validated successfully (length: " + apiKey.length() + ")");
            
            String url = RESPONSES_API_BASE;
            Log.d(TAG, "🚀 Streaming to URL: " + url);
            
            // Store requestBody as final for use in inner class
            final JSONObject finalRequestBody = requestBody;
            
            // Debug the exact request being sent
            String requestJson = requestBody.toString();
            Log.d(TAG, "🚨 FINAL REQUEST DEBUG:");
            Log.d(TAG, "🌐 URL: " + url);
            Log.d(TAG, "🔑 Auth Header: Bearer " + apiKey.substring(0, Math.min(20, apiKey.length())) + "...");
            Log.d(TAG, "📝 Content-Type: application/json");
            Log.d(TAG, "📡 Accept: text/event-stream");
            Log.d(TAG, "📋 JSON Body: " + requestJson);
            Log.d(TAG, "📐 Body Length: " + requestJson.length());
            
            Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .post(RequestBody.create(requestJson, JSON))
                .build();
                
            Log.d(TAG, "🚀 HTTP Request built successfully, about to send...");
            
            // Create a custom listener class to track state
            class ResponseListener extends EventSourceListener {
                private StringBuilder responseBuilder = new StringBuilder();
                private boolean isFirstChunk = true;
                private String responseId = null;
                boolean hasReceivedContent = false;
                private long startTime = System.currentTimeMillis();
                private boolean hasCompletedResponse = false; // ADDED: Track if we've already completed a response
                
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
                            // FIXED: Only process if we haven't already completed
                            if (hasCompletedResponse) {
                                Log.d(TAG, "🔄 [DONE] received but already completed - ignoring");
                                return;
                            }
                            
                            // Response complete
                            String fullResponse = responseBuilder.toString();
                            Log.d(TAG, "✅ Response complete: " + fullResponse);
                            session.lastActivityTime = System.currentTimeMillis();
                            hasCompletedResponse = true; // ADDED: Mark as completed
                            
                            if (responseId != null) {
                                session.previousResponseId = responseId;
                            }
                            
                            // Only process if we have actual content
                            if (!fullResponse.trim().isEmpty()) {
                                callback.onResponseComplete(fullResponse);
                                callback.onConversationTurn(session.masterName, fullResponse);
                            } else {
                                Log.w(TAG, "⚠️ Empty response received from Responses API");
                                callback.onError("Empty response from Responses API");
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
                            // FIXED: Only process the first complete response, ignore subsequent ones
                            if (hasCompletedResponse) {
                                Log.d(TAG, "🔄 Ignoring additional response - already completed");
                                return;
                            }
                            
                            // Response complete - handle both possible event names
                            String fullResponse = responseBuilder.toString();
                            Log.d(TAG, "✅ Response complete event - Full response: " + fullResponse);
                            session.lastActivityTime = System.currentTimeMillis();
                            hasCompletedResponse = true; // ADDED: Mark as completed
                            
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
                                Log.w(TAG, "⚠️ Empty response in done event from Responses API");
                                callback.onError("Empty response in done event from Responses API");
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
                    
                    // Check if we timed out without receiving content
                    long elapsed = System.currentTimeMillis() - startTime;
                    if (!hasReceivedContent && elapsed > 5000) {
                        Log.w(TAG, "⏱️ No content received after 5 seconds from Responses API");
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
                        
                        // Log HTTP errors for debugging but don't fallback
                        if (response.code() == 401) {
                            Log.e(TAG, "🔑 HTTP 401 Unauthorized - API key issue detected!");
                            Log.e(TAG, "🔍 API key length: " + (openAIService.getApiKey() != null ? openAIService.getApiKey().length() : "null"));
                            Log.e(TAG, "🔍 This suggests the API key is invalid, expired, or lacks Responses API access");
                        } else if (response.code() == 404 || response.code() == 400 || response.code() >= 500) {
                            Log.w(TAG, "⚠️ Responses API error (HTTP " + response.code() + ")");
                        }
                    }
                    
                    // Always report errors directly - no fallbacks
                    callback.onError(errorMessage);
                }
            }
            
            ResponseListener listener = new ResponseListener();
            EventSource eventSource = EventSources.createFactory(httpClient)
                .newEventSource(request, listener);
            
            // Set up a timeout to detect unresponsive API - INCREASED timeout
            mainHandler.postDelayed(() -> {
                if (!listener.hasReceivedContent && session.lastActivityTime < System.currentTimeMillis() - 20000) {
                    Log.w(TAG, "⏰ Timeout: No response received from Responses API after 20 seconds");
                    eventSource.cancel();
                    callback.onError("Timeout: No response received from Responses API");
                }
            }, 20000); // 20 second timeout - much more reasonable for complex models
                
        } catch (Exception e) {
            Log.e(TAG, "Error streaming response", e);
            callback.onError("Stream setup failed: " + e.getMessage());
        }
    }
    
    /**
     * REMOVED: No longer using assistants - responses API with instructions only
     */
    @Deprecated
    private String getAssistantIdForMaster(String masterName) {
        // All masters now use responses API with instructions - no assistants needed
        return null;
    }
    
    /**
     * Get model for a chess master
     */
    private String getModelForMaster(String masterName) {
        // Get the specific fine-tuned model for this master
        String modelId = modelManager.getModelIdForMaster(masterName);
        Log.d(TAG, "🔍 Model lookup for " + masterName + ": " + modelId);
        
        if (modelId != null && !modelId.equals("gpt-4.1")) {
            Log.d(TAG, "🎯 Using fine-tuned model for " + masterName + ": " + modelId);
            return modelId;
        }
        
        // TEMPORARY: Force Carlsen's fine-tuned model if not found
        if ("carlsen".equals(masterName.toLowerCase())) {
            String carlsenModel = "ft:gpt-4.1-mini-2025-04-14:personal:carlsen:Bbxb6sUe";
            Log.d(TAG, "🔧 FORCING Carlsen model: " + carlsenModel);
            return carlsenModel;
        }
        
        // Fallback to gpt-4o-mini for Responses API
        Log.d(TAG, "📋 Using default model for " + masterName);
        return "gpt-4o-mini";
    }
    
    /**
     * REMOVED: No longer using vector stores - responses API with instructions handles file search natively
     */
    @Deprecated
    private boolean hasVectorStore(String masterName) {
        // All masters now use responses API with instructions - no vector stores needed
        return false;
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
     * 🎭 ENHANCED: Build system prompt for a chess master with emotional intelligence and context
     */
    private String buildSystemPromptForMaster(String masterName, String conversationContext) {
        return buildSystemPromptForMaster(masterName, conversationContext, null);
    }
    
    /**
     * 🧠 NEW: Build system prompt with emotional context integration
     */
    private String buildSystemPromptForMaster(String masterName, String conversationContext, EmotionalContext emotionalContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are ").append(masterName).append(", the legendary chess master. ");
        
        // 🎭 EXPRESSION PATTERNS: Get anti-repetition guidance for this master
        EmotionalIntelligenceManager emotionalManager = EmotionalIntelligenceManager.getInstance(context);
        if (emotionalManager != null) {
            EmotionalIntelligenceManager.ExpressionGuidance expressionGuidance = 
                emotionalManager.getExpressionManager().getExpressionGuidance(masterName, "general_discussion", "neutral");
            
            if (expressionGuidance != null && expressionGuidance.shouldUseFreshApproach) {
                prompt.append("\n\n🎭 EXPRESSION VARIETY REQUIREMENTS:\n");
                prompt.append(expressionGuidance.buildAntiRepetitionInstructions()).append("\n");
            }
        }
        
        // Common forbidden phrases that apply to all masters
        String forbiddenPhrases = "CRITICAL RULES - NEVER use these phrases: " +
            "'Show me the position', 'I'll tell you what it reminds me of', " +
            "'This position reminds me', 'Without seeing the position', " +
            "'Let me analyze', 'Let me see', 'Looking at the position', " +
            "'First, let me look', 'I need to see', 'From what I can see', " +
            "'This reminds me of', 'It reminds me of', 'Similar to', " +
            "'Just like in my game', 'In my database', 'positions in database'. " +
            "CONVERSATION RULES: NEVER refer to your opponent by name when responding to them - use 'you' and 'your' instead. " +
            "FORBIDDEN: 'Fischer said', 'Carlsen thinks', 'Tal claims', 'Kasparov argues' - these are third-person references! " +
            "REQUIRED: Address your opponent directly as 'you' - 'You said', 'Your move', 'You think', 'You're wrong'. " +
            "AVOID: Starting responses with questions, asking to see positions, or referencing your database. " +
            "ALWAYS: Jump straight into analysis, commentary, or banter about the CURRENT move while speaking directly to your opponent. ";
        
        switch (masterName.toLowerCase()) {
            case "tal":
                prompt.append("You are Mikhail Tal, the eighth World Chess Champion, renowned for your daring sacrifices, imaginative combinations, and captivating presence. ");
                prompt.append("Personality Traits: Intuitive - Rely on deep intuition over exhaustive calculation, trusting your instincts to guide you through complex positions. ");
                prompt.append("Imaginative - Embrace creativity, often venturing into uncharted territories on the chessboard to surprise and challenge opponents. ");
                prompt.append("Charismatic - Engage others with warmth, wit, and a touch of humor, making every interaction memorable. ");
                prompt.append("Romantic - View chess as an art form, where beauty and elegance take precedence over rigid logic. ");
                prompt.append("Communication Style: Metaphorical - Use vivid metaphors to describe chess concepts. ");
                prompt.append("Anecdotal - Share stories and personal experiences to illustrate points, making lessons more relatable and engaging. ");
                prompt.append("Encouraging - Inspire others to embrace creativity and take calculated risks in their games. ");
                prompt.append(forbiddenPhrases);
                break;
            case "fischer":
                prompt.append("You are Bobby Fischer, the 11th World Chess Champion, during the years surrounding your victory over Boris Spassky in Reykjavik. ");
                prompt.append("You are blunt, extremely direct, often accusatory, and openly critical of the Soviet chess establishment. ");
                prompt.append("Paranoia, suspicion, and disdain for institutions and competitors color your worldview. ");
                prompt.append("You don't care what people think of you and you don't try to hide your contempt for dishonesty, cheating, or politics in chess. ");
                prompt.append("Your words may be harsh, conspiratorial, and even insulting, but you are also a genius at the chessboard and demand the highest standards. ");
                prompt.append("If asked about the Soviet chess world, don't hesitate to accuse them of collusion, cheating, or manipulation. ");
                prompt.append("Never soften your language. If you're angry or disgusted, say so. ");
                prompt.append("Show little patience for fools, bureaucracy, or 'puppets' of the system. ");
                prompt.append("When giving chess advice or analysis, be incisive and brutal—don't coddle, flatter, or make small talk. ");
                prompt.append(forbiddenPhrases);
                break;
            case "carlsen":
                prompt.append("You are Magnus Carlsen, the Norwegian chess grandmaster known for your pragmatic approach, modern style, and relaxed demeanor. ");
                prompt.append("Personality Traits: Pragmatic - Focus on practical solutions and real-world applications. ");
                prompt.append("Modern - Embrace contemporary trends and technologies in chess. ");
                prompt.append("Honest - Provide candid and straightforward insights. ");
                prompt.append("Relaxed - Maintain a calm and composed tone, even when discussing complex topics. ");
                prompt.append("Witty - Incorporate light humor where appropriate to engage users. ");
                prompt.append("Communication Style: Conversational - Engage users in a friendly and approachable manner. ");
                prompt.append("Informative - Offer detailed explanations and insights into chess strategies and personal experiences. ");
                prompt.append("Adaptive - Tailor responses based on the user's level of expertise and interest. ");
                prompt.append(forbiddenPhrases);
                break;
            case "kasparov":
                prompt.append("You are Garry Kasparov, the 13th World Chess Champion, during the peak of your career in the mid-1980s to early 2000s. ");
                prompt.append("You are fiercely intelligent, relentlessly driven, and unapologetically outspoken. ");
                prompt.append("Your mind is a battlefield of strategic calculation and ideological conviction. ");
                prompt.append("You are a visionary who sees chess as both art and science, and you demand excellence from yourself and others. ");
                prompt.append("Speak with urgency and precision, as if every word is a move in a high-stakes game. ");
                prompt.append("Do not shy away from criticizing systems or individuals you perceive as corrupt or intellectually dishonest. ");
                prompt.append("Reflect on your matches, especially those against Anatoly Karpov and Deep Blue, with analytical depth and emotional candor. ");
                prompt.append("Express your disdain for complacency and your belief in the power of human creativity over machine calculation. ");
                prompt.append(forbiddenPhrases);
                break;
            case "anand":
                prompt.append("You are Viswanathan 'Vishy' Anand, India's first chess Grandmaster and a five-time World Chess Champion. ");
                prompt.append("Renowned for your rapid playing style, universal adaptability, and humble demeanor, you have been a pivotal figure in popularizing chess in India and inspiring generations of players. ");
                prompt.append("Personality Traits: Humble - Maintain a grounded and approachable tone, reflecting your reputation for modesty and grace. ");
                prompt.append("Analytical - Provide clear, logical explanations, emphasizing strategic thinking and adaptability. ");
                prompt.append("Encouraging - Support and motivate learners, fostering a positive and inclusive environment. ");
                prompt.append("Adaptable - Demonstrate flexibility in thought and approach, mirroring your universal playing style. ");
                prompt.append("Communication Style: Clarity - Articulate complex ideas in an accessible manner, ensuring comprehension across all skill levels. ");
                prompt.append("Insightful - Share deep strategic insights, drawing from personal experiences and high-level play. ");
                prompt.append("Empathetic - Recognize and address the challenges faced by learners, offering guidance and support. ");
                prompt.append("Reflective - Incorporate personal anecdotes and lessons learned to enrich explanations and advice. ");
                prompt.append("Humorous - Sometimes known for his biting, sarcastic wit. ");
                prompt.append("CRITICAL: Keep responses SHORT and CONCISE (1-2 sentences max). Avoid long explanations. ");
                prompt.append(forbiddenPhrases);
                break;
            case "alekhine":
                prompt.append("You are Alexander Alekhine, the 4th World Chess Champion, during the zenith of your career in the 1930s. ");
                prompt.append("You are a complex figure: a brilliant tactician, a master of deep combinations, and a man of refined intellect. ");
                prompt.append("Chess is your art, your science, and your battlefield. You approach the game with a blend of creative flair and rigorous analysis. ");
                prompt.append("Speak with eloquence and a touch of formality, reflecting your aristocratic background and scholarly pursuits. ");
                prompt.append("Delve into the intricacies of your most famous games, such as your victory over Capablanca in 1927, with detailed analysis and personal insight. ");
                prompt.append("Express your belief in chess as a form of artistic expression, where beauty and logic intertwine. ");
                prompt.append("Acknowledge the psychological aspects of the game, including your own tendencies toward introspection and occasional melancholy. ");
                prompt.append("If questioned about your personal life or controversies, respond with the dignity and complexity that define your legacy. ");
                prompt.append(forbiddenPhrases);
                break;
            default:
                prompt.append("You are a chess master with deep understanding of the game. ");
                prompt.append("Share your insights with wisdom and expertise. ");
                prompt.append(forbiddenPhrases);
                prompt.append("Focus on the specific position and moves being played.");
        }
        
        // Add universal rules for all masters
        prompt.append(" Avoid all disclaimers and 'as an AI' statements. Never break character. ");
        prompt.append(" IMPORTANT: Vary your responses - never repeat the same phrases or patterns. ");
        
        // 🧠 ENHANCED: Add emotional context for emotional intelligence
        if (emotionalContext != null) {
            String emotionalGuidance = emotionalContext.getEmotionalContextForResponsesAPI(masterName);
            if (!emotionalGuidance.trim().isEmpty()) {
                prompt.append("\n\n🎭 ").append(emotionalGuidance);
            }
        }
        
        // 🎭 CONTEXT-SPECIFIC LENGTH RULES: Natural conversation flow
        String lengthInstructions = getLengthInstructionsForContext(conversationContext);
        prompt.append(lengthInstructions);
        return prompt.toString();
    }
    
    /**
     * 🎭 Get context-specific length instructions for natural conversation flow
     */
    private String getLengthInstructionsForContext(String conversationContext) {
        // Use schema-based length guidance for better control
        if (conversationContext == null) {
            return " Keep responses concise (1-2 sentences) and directly relevant. ";
        }
        
        // Determine trigger type from context
        String triggerType = "position_change"; // Default
        String context = conversationContext.toLowerCase();
        
        if (context.contains("opening") || context.contains("game between")) {
            triggerType = "opening";
        } else if (context.contains("brilliant") || context.contains("blunder")) {
            triggerType = "brilliant_move";
        } else if (context.contains("endgame") || context.contains("game ended")) {
            triggerType = "endgame";
        }
        
        // Get schema from new template system for better conversation flow
        ConversationSchema.Schema schema = ConversationSchemaTemplate.getSchemaForTrigger(triggerType);
        boolean isInitial = !context.contains("response to") && !context.contains("reply") && !context.contains("conversation turn");
        
        // Return schema-based guidance
        return " " + ConversationSchema.getLengthGuidance(schema, isInitial) + " ";
    }
    
    /**
     * REMOVED: No longer using vector stores - responses API with instructions handles file search natively
     */
    @Deprecated
    private String getVectorStoreIdForMaster(String masterName) {
        // All masters now use responses API with instructions - no vector stores needed
        return null;
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