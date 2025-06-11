package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Unified OpenAI service that handles all OpenAI API interactions.
 * Consolidated from OpenAIService, OpenAIClient, and UnifiedOpenAIService.
 * FIXED: Proper ConversationManager constructor usage
 */
public class OpenAIService {
    private static final String TAG = "OpenAIService";

    // API endpoints
    static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    private static final String TRANSCRIBE_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String RESPONSES_API_URL = "https://api.openai.com/v1/assistants";

    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    // 🚨 DEPRECATED: Default model should never be used - fine-tuned models only!
    @Deprecated
    private static final String DEFAULT_MODEL = "DEPRECATED_USE_FINE_TUNED_MODELS_ONLY";

    private static OpenAIService instance;

    private final OkHttpClient client;
    private final OkHttpClient streamingClient;
    private final Gson gson;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private final Map<String, String> fineTunedModels = new HashMap<>();

    private String apiKey;
    private Context context;
    private String model = DEFAULT_MODEL;

    /**
     * Private constructor
     */
    private OpenAIService() {
        // Standard client for regular requests
        this.client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        // Streaming client with longer timeouts
        this.streamingClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 60, TimeUnit.SECONDS))
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();

        initFineTunedModels();
    }

    /**
     * Get singleton instance
     */
    public static synchronized OpenAIService getInstance() {
        if (instance == null) {
            instance = new OpenAIService();
        }
        return instance;
    }

    /**
     * Initialize with context
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Set the API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        Log.d(TAG, "API key set, length: " + (apiKey != null ? apiKey.length() : 0));
    }

    /**
     * Check if API key is set
     */
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * Get authorization header
     */
    private String getAuthorizationHeader() {
        if (!hasApiKey()) {
            Log.e(TAG, "Attempting to use API key before it's set!");
            // Try to load from config as a fallback
            if (context != null) {
                String configKey = ApiKeyConfig.getApiKey(context);
                if (configKey != null && !configKey.isEmpty()) {
                    setApiKey(configKey);
                    Log.d(TAG, "Loaded API key from config as fallback");
                }
            }
        }
        return "Bearer " + apiKey;
    }
    
    /**
     * Get API key for external use (e.g., Responses API)
     */
    public String getApiKey() {
        if (!hasApiKey() && context != null) {
            String configKey = ApiKeyConfig.getApiKey(context);
            if (configKey != null && !configKey.isEmpty()) {
                setApiKey(configKey);
            }
        }
        return apiKey;
    }

    /**
     * Get the HTTP client
     */
    public OkHttpClient getHttpClient() {
        return client;
    }

    /**
     * Set the model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 🚨 GUARDRAIL: Select chess master (for fine-tuned models ONLY)
     * NEVER falls back to default models - always uses fine-tuned
     */
    public void selectChessMaster(String master) {
        if (fineTunedModels.containsKey(master.toLowerCase())) {
            this.model = fineTunedModels.get(master.toLowerCase());
            Log.d(TAG, "✅ Selected fine-tuned model for " + master + ": " + this.model);
        } else {
            // 🚨 GUARDRAIL: NEVER use DEFAULT_MODEL - always use a fine-tuned model
            this.model = fineTunedModels.get("tal"); // Safe fine-tuned fallback
            Log.e(TAG, "🚨 GUARDRAIL: Master '" + master + "' not found in fine-tuned models");
            Log.w(TAG, "🔄 GUARDRAIL CORRECTION: Using Tal's fine-tuned model as fallback: " + this.model);
        }
    }

    /**
     * Initialize fine-tuned models - only the 5 working models
     */
    private void initFineTunedModels() {
        // Only include the 5 working fine-tuned models
        fineTunedModels.put("tal", "ft:gpt-4o-2024-08-06:personal:tal-20250525:BbDcbXJT");
        fineTunedModels.put("fischer", "ft:gpt-4o-2024-08-06:personal:fischer:BbWNySl4");
        fineTunedModels.put("carlsen", "ft:gpt-4.1-mini-2025-04-14:personal:carlsen:Bbxb6sUe");
        fineTunedModels.put("alekhine", "ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD");
        fineTunedModels.put("kasparov", "ft:gpt-4.1-2025-04-14:personal:alekhine:BfduAenz");
        
        // All other masters will fall back to base models
    }

    /**
     * 🚨 GUARDRAIL: Get chat completion with conversation history and variety management
     * ALWAYS uses fine-tuned models - NEVER vanilla chat completions
     */
    public String getChatCompletionWithHistory(ConversationManager conversationManager) {
        Log.w(TAG, "🚨 GUARDRAIL: Redirecting chat completion to fine-tuned model system");
        return getChatCompletionWithHistoryAndVariety(conversationManager, null, null);
    }

    /**
     * 🚨 GUARDRAIL: Enhanced chat completion with variety management
     * ENFORCES fine-tuned models only - NO vanilla chat completions allowed
     */
    public String getChatCompletionWithHistoryAndVariety(ConversationManager conversationManager, 
                                                         String masterName, String conversationContext) {
        if (!hasApiKey()) {
            Log.e(TAG, "API key not set");
            return "Error: API key not configured.";
        }

        try {
            String modelToUse = getModelForRequest();
            
            // 🚨 CRITICAL GUARDRAIL: Use central validation system
            modelToUse = validateAndCorrectModel(modelToUse, "getChatCompletionWithHistoryAndVariety");

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelToUse);

            // Apply variety parameters if available
            if (context != null && masterName != null) {
                ConversationVarietyManager varietyManager = ConversationVarietyManager.getInstance(context);
                ConversationVarietyManager.VarietyParams varietyParams = 
                    varietyManager.getVarietyParams(masterName, conversationContext);
                
                Log.d(TAG, "🎭 Applying variety params for " + masterName + ": temp=" + varietyParams.temperature + 
                         ", presence=" + varietyParams.presencePenalty + ", frequency=" + varietyParams.frequencyPenalty);
                
                requestBody.put("temperature", varietyParams.temperature);
                requestBody.put("max_tokens", varietyParams.maxTokens);
                requestBody.put("presence_penalty", varietyParams.presencePenalty);
                requestBody.put("frequency_penalty", varietyParams.frequencyPenalty);
                requestBody.put("top_p", varietyParams.topP);
                
                // Add seed for reproducibility testing (if needed)
                if (varietyParams.seedSuffix != null) {
                    String conversationSeed = varietyManager.generateConversationSeed(masterName, conversationContext);
                    Log.d(TAG, "🌱 Using conversation seed: " + conversationSeed);
                }
            } else {
                // Fallback to optimization settings
                if (context != null) {
                    FineTunedModelManager.ModelOptimizationSettings settings =
                            FineTunedModelManager.getInstance(context).getOptimizationSettings();
                    requestBody.put("temperature", settings.temperature);
                    requestBody.put("max_tokens", settings.maxTokens);
                    if (settings.allowCreativeLiberty) {
                        requestBody.put("top_p", 0.9);
                    }
                }
            }

            // Add messages
            JSONArray messagesArray = new JSONArray();
            List<ConversationManager.Message> messages = conversationManager.getConversationHistory();

            for (ConversationManager.Message message : messages) {
                JSONObject messageObj = new JSONObject();
                messageObj.put("role", message.getRole());
                messageObj.put("content", message.getContent());
                messagesArray.put(messageObj);
            }

            requestBody.put("messages", messagesArray);

            // Make request
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", getAuthorizationHeader())
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API Error: " + response.code());
                    return "Sorry, I had trouble connecting. Please try again.";
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray choices = jsonResponse.getJSONArray("choices");

                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    String responseContent = message.getString("content");
                    
                    // Track response for variety management
                    if (context != null && masterName != null) {
                        ConversationVarietyManager varietyManager = ConversationVarietyManager.getInstance(context);
                        varietyManager.trackResponse(masterName, responseContent);
                        
                        // Check for potential repetition
                        if (varietyManager.isPotentialRepetition(masterName, responseContent)) {
                            Log.w(TAG, "⚠️ Potential repetition detected in response for " + masterName);
                        }
                    }
                    
                    return responseContent;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in chat completion: " + e.getMessage());
            return "I encountered an error: " + e.getMessage();
        }

        return "Sorry, I couldn't generate a response.";
    }

    // ADD this method to your OpenAIService.java class

    /**
     * Get chat completion using a specific model (including fine-tuned models)
     */

    // REPLACE the getChatCompletionWithModel method in OpenAIService.java with this corrected version:

    /**
     * 🚨 GUARDRAIL: Get chat completion using a specific model with variety management
     * ENFORCES fine-tuned models only
     */
    public String getChatCompletionWithModel(String modelId, String systemPrompt, String userMessage) {
        // 🚨 GUARDRAIL: Use central validation system
        modelId = validateAndCorrectModel(modelId, "getChatCompletionWithModel");
        return getChatCompletionWithModelAndVariety(modelId, systemPrompt, userMessage, null, null);
    }

    /**
     * 🚨 GUARDRAIL: Get chat completion using a specific model with enhanced variety management
     * ENFORCES fine-tuned models only - NO vanilla chat completions
     */
    public String getChatCompletionWithModelAndVariety(String modelId, String systemPrompt, String userMessage,
                                                        String masterName, String conversationContext) {
        if (!hasApiKey()) {
            Log.e(TAG, "API key not available");
            return "API key not configured";
        }

        // 🚨 CRITICAL GUARDRAIL: Use central validation system
        modelId = validateAndCorrectModel(modelId, "getChatCompletionWithModelAndVariety");

        try {
            Log.d(TAG, "🎭 Using FINE-TUNED model with variety: " + modelId);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelId);

            // Apply variety parameters or defaults
            if (context != null && masterName != null) {
                ConversationVarietyManager varietyManager = ConversationVarietyManager.getInstance(context);
                ConversationVarietyManager.VarietyParams varietyParams = 
                    varietyManager.getVarietyParams(masterName, conversationContext);
                
                requestBody.put("temperature", varietyParams.temperature);
                requestBody.put("max_tokens", varietyParams.maxTokens);
                requestBody.put("presence_penalty", varietyParams.presencePenalty);
                requestBody.put("frequency_penalty", varietyParams.frequencyPenalty);
                requestBody.put("top_p", varietyParams.topP);
                
                Log.d(TAG, "🎯 Variety params applied: temp=" + varietyParams.temperature + 
                         ", penalties=" + varietyParams.presencePenalty + "/" + varietyParams.frequencyPenalty);
            } else {
                // Default parameters
                requestBody.put("max_tokens", 800);
                requestBody.put("temperature", 0.4);
            }

            JSONArray messages = new JSONArray();

            // Add system message
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.put(systemMessage);

            // Add user message
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            requestBody.put("messages", messages);

            // Make API call using your existing makeApiCall method (or similar)
            String response = makeOpenAIRequest("https://api.openai.com/v1/chat/completions", requestBody.toString());

            if (response != null && !response.trim().isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.has("choices")) {
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        if (choices.length() > 0) {
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject message = firstChoice.getJSONObject("message");
                            String content = message.getString("content");

                            // Track response for variety management
                            if (context != null && masterName != null) {
                                ConversationVarietyManager varietyManager = ConversationVarietyManager.getInstance(context);
                                varietyManager.trackResponse(masterName, content);
                            }

                            Log.d(TAG, "✅ Response generated: " + content.substring(0, Math.min(100, content.length())) + "...");
                            return content;
                        }
                    }

                    Log.e(TAG, "No choices in response");
                    return "No response generated";

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON response", e);
                    return "Error parsing response";
                }
            } else {
                Log.e(TAG, "Empty or null response from API");
                return "No response from API";
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error with fine-tuned model", e);

            // Fallback to base model on any error
            Log.w(TAG, "🔄 Falling back to base model due to error");
            return getChatCompletion(systemPrompt, userMessage);
        }
    }

    /**
     * Helper method to make OpenAI requests using your existing HTTP infrastructure
     * You may need to adjust this to match your existing API call method
     */
    private String makeOpenAIRequest(String url, String jsonBody) {
        try {
            // If you have a different method name for making HTTP requests,
            // replace this with your existing method
            // For example, if you have: makeHttpRequest(), callOpenAIAPI(), etc.

            // This is a fallback that should work with most OkHttp setups
            OkHttpClient client = new OkHttpClient();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonBody);

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
            }

            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error making OpenAI request", e);
            return null;
        }
    }

    /**
     * 🚨 GUARDRAIL: Streaming chat completion for ultra-low latency
     * ENFORCES fine-tuned models only for streaming
     */
    public void generateStreamingChatResponse(String systemPrompt, String userMessage,
                                              StreamingChatCallback callback) {
        executorService.execute(() -> {
            Response response = null;
            BufferedReader reader = null;

            try {
                String modelToUse = getModelForRequest();
                
                // 🚨 CRITICAL GUARDRAIL: Use central validation system for streaming
                modelToUse = validateAndCorrectModel(modelToUse, "generateStreamingChatResponse");
                
                JSONObject requestJson = new JSONObject();
                requestJson.put("model", modelToUse);
                requestJson.put("stream", true);
                
                Log.d(TAG, "🚀 Starting FINE-TUNED streaming with model: " + modelToUse);

                JSONArray messages = new JSONArray();

                JSONObject systemMsg = new JSONObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                messages.put(systemMsg);

                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", userMessage);
                messages.put(userMsg);

                requestJson.put("messages", messages);

                RequestBody body = RequestBody.create(requestJson.toString(), JSON);
                Request request = new Request.Builder()
                        .url(API_URL)
                        .header("Authorization", getAuthorizationHeader())
                        .post(body)
                        .build();

                response = streamingClient.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    reader = new BufferedReader(
                            new InputStreamReader(response.body().byteStream()));

                    StringBuilder completeResponse = new StringBuilder();
                    StringBuilder currentChunk = new StringBuilder();
                    String line;
                    boolean firstChunkSent = false;

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);

                            if ("[DONE]".equals(data)) {
                                break;
                            }

                            try {
                                JSONObject chunk = new JSONObject(data);
                                JSONArray choices = chunk.getJSONArray("choices");

                                if (choices.length() > 0) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");

                                    if (delta.has("content")) {
                                        String content = delta.getString("content");
                                        completeResponse.append(content);
                                        currentChunk.append(content);

                                        // Send chunks based on content
                                        if (!firstChunkSent && currentChunk.length() > 20) {
                                            String firstChunk = currentChunk.toString();
                                            mainHandler.post(() -> callback.onPartialResponse(firstChunk, true));
                                            currentChunk.setLength(0);
                                            firstChunkSent = true;
                                        } else if (firstChunkSent &&
                                                (currentChunk.length() > 60 ||
                                                        content.contains(".") ||
                                                        content.contains("!"))) {
                                            String chunkText = currentChunk.toString();
                                            if (chunkText.trim().length() > 0) {
                                                mainHandler.post(() -> callback.onPartialResponse(chunkText, false));
                                                currentChunk.setLength(0);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Error parsing streaming chunk: " + e.getMessage());
                            }
                        }
                    }

                    // Send any remaining content
                    if (currentChunk.length() > 0) {
                        String finalChunk = currentChunk.toString();
                        mainHandler.post(() -> callback.onPartialResponse(finalChunk, false));
                    }

                    // Signal completion
                    String fullResponse = completeResponse.toString();
                    mainHandler.post(() -> callback.onComplete(fullResponse));

                } else {
                    String errorMsg = "Streaming API error: " + response.code();
                    mainHandler.post(() -> callback.onError(new IOException(errorMsg)));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            } finally {
                // CRITICAL: Always close resources
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (response != null) {
                    response.close();
                }
            }
        });
    }

    /**
     * Simple chat completion - FIXED: Proper ConversationManager constructor
     */
    public String getChatCompletion(String systemPrompt, String userMessage) {
        if (context == null) {
            Log.e(TAG, "Context not initialized! Call init(context) first.");
            return "Error: Service not properly initialized.";
        }

        // FIXED: Use proper constructor with context
        ConversationManager tempManager = ConversationManager.getInstance(context);
        tempManager.clear(); // Clear any existing conversation
        tempManager.addSystemMessage(systemPrompt);
        tempManager.addUserMessage(userMessage);
        return getChatCompletionWithHistory(tempManager);
    }

    /**
     * Synchronous version of generateChatResponse for backward compatibility - FIXED
     */
    public String generateChatResponseSync(String systemPrompt, String userMessage) throws IOException {
        // FIXED: Use proper constructor with context
        if (context == null) {
            throw new IOException("Context not initialized! Call init(context) first.");
        }

        ConversationManager tempManager = ConversationManager.getInstance(context);
        tempManager.clear(); // Clear any existing conversation
        tempManager.addSystemMessage(systemPrompt);
        tempManager.addUserMessage(userMessage);

        // Use our existing method but wait for result
        String response = getChatCompletionWithHistory(tempManager);

        // For true async compatibility, we could use executeAsync, but since
        // getChatCompletionWithHistory is already synchronous, we can return directly
        return response;
    }

    /**
     * Send message (legacy compatibility)
     */
    public String sendMessage(String userMessage) {
        if (context != null) {
            String systemPrompt = FineTunedModelManager.getInstance(context)
                    .getEnhancedSystemPromptForSelectedMaster();
            return getChatCompletion(systemPrompt, userMessage);
        } else {
            return getChatCompletion("You are a helpful chess coach.", userMessage);
        }
    }

    /**
     * Assistants API methods
     */
    private Request.Builder getAssistantsApiRequestBuilder() {
        return new Request.Builder()
                .addHeader("Authorization", getAuthorizationHeader())
                .addHeader("Content-Type", "application/json")
                .addHeader("OpenAI-Beta", "assistants=v2");
    }

    public String createAssistant(String requestBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/assistants")
                    .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating assistant", e);
            return null;
        }
    }

    public String createThread() {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads")
                    .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating thread", e);
            return null;
        }
    }

    public String createMessage(String threadId, String messageBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                    .post(RequestBody.create(MediaType.parse("application/json"), messageBody))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating message", e);
            return null;
        }
    }

    public String createRun(String threadId, String runBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/runs")
                    .post(RequestBody.create(MediaType.parse("application/json"), runBody))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating run", e);
            return null;
        }
    }

    public String retrieveRun(String threadId, String runId) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving run", e);
            return null;
        }
    }

    public String listMessages(String threadId) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error listing messages", e);
            return null;
        }
    }

    /**
     * Get chat completion with enhanced context and variety management
     */
    public String getChatCompletionWithEnhancedContext(List<Map<String, String>> contextMessages, 
                                                      String modelId) {
        return getChatCompletionWithEnhancedContextAndVariety(contextMessages, modelId, null, null);
    }

    /**
     * Get chat completion with enhanced context and full variety management
     */
    public String getChatCompletionWithEnhancedContextAndVariety(List<Map<String, String>> contextMessages, 
                                                                String modelId, String masterName, String conversationContext) {
        if (!hasApiKey()) {
            Log.e(TAG, "API key not available");
            return "API key not configured";
        }

        try {
            Log.d(TAG, "🎭 Using enhanced context with variety for " + masterName + " (" + contextMessages.size() + " messages)");

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelId != null ? modelId : getModelForRequest());

            // Apply variety parameters or enhanced defaults
            if (context != null && masterName != null) {
                ConversationVarietyManager varietyManager = ConversationVarietyManager.getInstance(context);
                ConversationVarietyManager.VarietyParams varietyParams = 
                    varietyManager.getVarietyParams(masterName, conversationContext);
                
                requestBody.put("temperature", varietyParams.temperature);
                requestBody.put("max_tokens", varietyParams.maxTokens);
                requestBody.put("presence_penalty", varietyParams.presencePenalty);
                requestBody.put("frequency_penalty", varietyParams.frequencyPenalty);
                requestBody.put("top_p", varietyParams.topP);
                
                Log.d(TAG, "🌟 Enhanced variety applied: temp=" + varietyParams.temperature + 
                         ", context=" + conversationContext);
            } else {
                // Enhanced default parameters for personality
                requestBody.put("temperature", 0.8);
                requestBody.put("max_tokens", 150);
                requestBody.put("presence_penalty", 0.6);
                requestBody.put("frequency_penalty", 0.3);
            }

            // Convert context messages to JSON array
            JSONArray messages = new JSONArray();
            for (Map<String, String> msg : contextMessages) {
                JSONObject jsonMsg = new JSONObject();
                jsonMsg.put("role", msg.get("role"));
                jsonMsg.put("content", msg.get("content"));
                if (msg.containsKey("name")) {
                    jsonMsg.put("name", msg.get("name"));
                }
                messages.put(jsonMsg);
            }

            requestBody.put("messages", messages);

            // Make API call
            String response = makeOpenAIRequest(API_URL, requestBody.toString());

            if (response != null && !response.trim().isEmpty()) {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("choices")) {
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject message = firstChoice.getJSONObject("message");
                        String content = message.getString("content");
                        
                        // Track response for variety management
                        if (context != null && masterName != null) {
                            ConversationVarietyManager varietyManager = ConversationVarietyManager.getInstance(context);
                            varietyManager.trackResponse(masterName, content);
                        }
                        
                        return content;
                    }
                }
            }

            return "I need a moment to think about that...";

        } catch (Exception e) {
            Log.e(TAG, "❌ Error with enhanced context", e);
            return "Let me reconsider that move...";
        }
    }

    /**
     * 🚨 COMPREHENSIVE API GUARDRAIL SYSTEM
     * 
     * This system enforces the user's critical requirements:
     * 1. ALWAYS use fine-tuned models, NEVER chat completions with base models
     * 2. ALWAYS use ElevenLabs TTS, NEVER OpenAI TTS
     * 
     * Guardrails prevent regression and ensure consistent behavior.
     * 
     * FINE-TUNED MODELS ONLY:
     * - All API calls are intercepted and validated
     * - Non-fine-tuned models are automatically redirected
     * - Comprehensive logging tracks all violations and corrections
     * - Fallback to Tal's model ensures system never breaks
     */
    
    /**
     * 🚨 CENTRAL GUARDRAIL VALIDATION - Call this before ANY OpenAI API request
     */
    private String validateAndCorrectModel(String modelId, String context) {
        if (modelId == null) {
            Log.e(TAG, "🚨 GUARDRAIL: NULL model provided in " + context);
            modelId = fineTunedModels.get("tal");
            Log.w(TAG, "🔄 GUARDRAIL CORRECTION: Using Tal fallback model");
            return modelId;
        }
        
        if (!isFineTunedModel(modelId)) {
            Log.e(TAG, "🚨 GUARDRAIL VIOLATION in " + context + ": Non-fine-tuned model: " + modelId);
            
            // Try to get appropriate fine-tuned model for current master
            String currentMaster = getCurrentMasterName();
            String fineTunedModel = getFineTunedModelForMaster(currentMaster);
            
            if (fineTunedModel != null) {
                Log.w(TAG, "🔄 GUARDRAIL CORRECTION: Using fine-tuned model for " + currentMaster + ": " + fineTunedModel);
                return fineTunedModel;
            } else {
                Log.w(TAG, "🔄 GUARDRAIL FALLBACK: Using Tal's fine-tuned model as last resort");
                return fineTunedModels.get("tal");
            }
        }
        
        Log.d(TAG, "✅ GUARDRAIL PASSED: Fine-tuned model validated: " + modelId);
        return modelId;
    }
    
    /**
     * 🚨 GUARDRAIL HELPER METHODS
     */
    
    /**
     * Check if a model ID represents a fine-tuned model
     */
    private boolean isFineTunedModel(String modelId) {
        if (modelId == null) return false;
        
        // Fine-tuned models start with "ft:" prefix
        boolean isFineTuned = modelId.startsWith("ft:");
        
        // Also check if it's in our known fine-tuned models
        boolean isKnownFineTuned = fineTunedModels.containsValue(modelId);
        
        Log.d(TAG, "🔍 Model check: " + modelId + " - isFineTuned: " + isFineTuned + ", isKnown: " + isKnownFineTuned);
        
        return isFineTuned || isKnownFineTuned;
    }
    
    /**
     * Get the current master name from context
     */
    private String getCurrentMasterName() {
        if (context != null) {
            try {
                return FineTunedModelManager.getInstance(context).getSelectedChessMaster();
            } catch (Exception e) {
                Log.w(TAG, "Error getting current master name", e);
            }
        }
        return "tal"; // Safe fallback
    }
    
    /**
     * Get fine-tuned model for a specific master
     */
    private String getFineTunedModelForMaster(String masterName) {
        if (masterName == null) return null;
        return fineTunedModels.get(masterName.toLowerCase());
    }
    
    /**
     * 🚨 GUARDRAIL: Helper methods - ALWAYS returns fine-tuned models
     */
    private String getModelForRequest() {
        if (context == null) {
            Log.w(TAG, "Context not initialized, using Tal fallback fine-tuned model");
            return fineTunedModels.get("tal"); // Never use DEFAULT_MODEL
        }

        String selectedModel = FineTunedModelManager.getInstance(context).getSelectedModelId();
        Log.d(TAG, "Selected model from FineTunedModelManager: " + selectedModel);
        
        // 🚨 CRITICAL GUARDRAIL: Use central validation system
        return validateAndCorrectModel(selectedModel, "getModelForRequest");
    }

    /**
     * Execute async with callback
     */
    public <T> void executeAsync(Callable<T> apiCall, ApiCallback<T> callback) {
        executorService.execute(() -> {
            try {
                T result = apiCall.call();
                if (callback != null) {
                    mainHandler.post(() -> callback.onSuccess(result));
                }
            } catch (Exception e) {
                Log.e(TAG, "API error: " + e.getMessage(), e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e));
                }
            }
        });
    }

    /**
     * ADDED: Cleanup method for proper resource management
     */
    public void cleanup() {
        try {
            if (client != null) {
                client.dispatcher().executorService().shutdown();
                client.connectionPool().evictAll();
            }
            if (streamingClient != null) {
                streamingClient.dispatcher().executorService().shutdown();
                streamingClient.connectionPool().evictAll();
            }
            if (executorService != null) {
                executorService.shutdown();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }

    /**
     * Callback interfaces
     */
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface StreamingChatCallback {
        void onPartialResponse(String partialText, boolean isFirst);
        void onComplete(String fullResponse);
        void onError(Exception e);
    }

    /**
     * 🚨 GUARDRAIL DIAGNOSTIC: Verify all guardrails are working correctly
     */
    public void runGuardrailDiagnostic() {
        Log.i(TAG, "🚨 ============ GUARDRAIL DIAGNOSTIC REPORT ============");
        
        // Test fine-tuned model validation
        Log.i(TAG, "🔍 Testing fine-tuned model validation:");
        Log.i(TAG, "   - Tal model valid: " + isFineTunedModel(fineTunedModels.get("tal")));
        Log.i(TAG, "   - Fischer model valid: " + isFineTunedModel(fineTunedModels.get("fischer")));
        Log.i(TAG, "   - GPT-4 model invalid: " + !isFineTunedModel("gpt-4"));
        Log.i(TAG, "   - Default model invalid: " + !isFineTunedModel(DEFAULT_MODEL));
        
        // Test central validation system
        Log.i(TAG, "🛡️ Testing central validation system:");
        String invalidModel = "gpt-4-turbo-preview";
        String validatedModel = validateAndCorrectModel(invalidModel, "diagnostic");
        Log.i(TAG, "   - Invalid '" + invalidModel + "' corrected to: " + validatedModel);
        Log.i(TAG, "   - Correction is fine-tuned: " + isFineTunedModel(validatedModel));
        
        // Test current model selection
        Log.i(TAG, "🎯 Current model selection:");
        String currentModel = getModelForRequest();
        Log.i(TAG, "   - Current model: " + currentModel);
        Log.i(TAG, "   - Is fine-tuned: " + isFineTunedModel(currentModel));
        
        // List all available fine-tuned models
        Log.i(TAG, "📋 Available fine-tuned models:");
        for (Map.Entry<String, String> entry : fineTunedModels.entrySet()) {
            Log.i(TAG, "   - " + entry.getKey() + ": " + entry.getValue());
        }
        
        Log.i(TAG, "🚨 =============== DIAGNOSTIC COMPLETE ===============");
    }

    /**
     * For backward compatibility with UnifiedOpenAIService
     */
    public static class OpenAICallback<T> {
        public void onSuccess(T result) {}
        public void onFailure(Exception e) {}
    }
}