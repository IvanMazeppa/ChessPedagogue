package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Enhanced OpenAI service with optimized fine-tuned model integration
 */
public class OpenAIService {
    static final String API_URL = "https://api.openai.com/v1/chat/completions";
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "OpenAIService";
    private static final String DEFAULT_MODEL = "gpt-4.1";
    private static OpenAIService instance;
    private final Gson gson;
    private final OkHttpClient client;
    private final Map<String, String> fineTunedModels = new HashMap<>();
    private String apiKey;

    // Added context variable
    private Context context;

    // Default model - can be changed as needed
    private String model = "gpt-4.1";

    /**
     * Private constructor
     */
    private OpenAIService() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
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
     * Creates a new Assistant
     */
    // Helper method to add the required headers
    private Request.Builder getAssistantsApiRequestBuilder() {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("OpenAI-Beta", "assistants=v2");  // The magic key!
    }

    // Update all your methods to use this helper
    public String createAssistant(String requestBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/assistants")
                    .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating assistant", e);
            return null;
        }
    }

    private void initFineTunedModels() {
        // Use your actual fine-tuned model for all masters
        String actualModelId = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";

        // Initialize with your fine-tuned model IDs
        fineTunedModels.put("tal", "ft:gpt-4:chess-coach:tal:2025-05-01");
        fineTunedModels.put("kramnik", "ft:gpt-4:chess-coach:kramnik:2025-05-01");
        fineTunedModels.put("karpov", "ft:gpt-4:chess-coach:karpov:2025-05-01");
        fineTunedModels.put("fischer", "ft:gpt-4:chess-coach:fischer:2025-05-01");
        fineTunedModels.put("lasker", "ft:gpt-4:chess-coach:lasker:2025-05-01");
        fineTunedModels.put("kasparov", "ft:gpt-4:chess-coach:kasparov:2025-05-01");
        fineTunedModels.put("capablanca", "ft:gpt-4:chess-coach:capablanca:2025-05-01");
        fineTunedModels.put("carlsen", "ft:gpt-4:chess-coach:carlsen:2025-05-01");
        fineTunedModels.put("morphy", "ft:gpt-4:chess-coach:morphy:2025-05-01");
        fineTunedModels.put("anand", "ft:gpt-4:chess-coach:anand:2025-05-01");
    }

    /**
     * Creates a new thread for conversation
     */
    // Update createThread method
    public String createThread() {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads")
                    .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating thread", e);
            return null;
        }
    }

    // Update createMessage method
    public String createMessage(String threadId, String messageBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                    .post(RequestBody.create(MediaType.parse("application/json"), messageBody))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating message", e);
            return null;
        }
    }

    // Update createRun method
    public String createRun(String threadId, String runBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/runs")
                    .post(RequestBody.create(MediaType.parse("application/json"), runBody))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating run", e);
            return null;
        }
    }

    // Update retrieveRun method
    public String retrieveRun(String threadId, String runId) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving run", e);
            return null;
        }
    }

    // Update listMessages method
    public String listMessages(String threadId) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error listing messages", e);
            return null;
        }
    }

    /**
     * Initialize with context
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    private String getModelForRequest() {
        // Check if we have context
        if (context == null) {
            Log.w(TAG, "Context not initialized, using default model");
            return model;
        }

        // Get the correct model from FineTunedModelManager
        String selectedModel = FineTunedModelManager.getInstance(context).getSelectedModelId();
        Log.d(TAG, "Selected chess master model: " + selectedModel);
        return selectedModel;
    }

    /**
     * Enhanced chat completion method with fine-tuned model optimization
     */
    public String getChatCompletionWithHistory(ConversationManager conversationManager) {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return "Error: API key not configured.";
        }

        try {
            // Get the appropriate model and optimization settings
            String selectedMaster = FineTunedModelManager.getInstance(context).getSelectedChessMaster();
            String modelToUse = getModelForRequest();
            FineTunedModelManager.ModelOptimizationSettings optimizationSettings =
                    FineTunedModelManager.getInstance(context).getOptimizationSettings();

            Log.d(TAG, "🎯 Selected master: " + selectedMaster);
            Log.d(TAG, "🎯 Model being used: " + modelToUse);
            Log.d(TAG, "🎯 Temperature: " + optimizationSettings.temperature);

            // Create request JSON with optimization settings
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelToUse);

            // Apply optimization settings
            requestBody.put("temperature", optimizationSettings.temperature);
            requestBody.put("max_tokens", optimizationSettings.maxTokens);

            // Add top_p for creative masters
            if (optimizationSettings.allowCreativeLiberty) {
                requestBody.put("top_p", 0.9);
            }

            // Get the enhanced system prompt
            String enhancedSystemPrompt = FineTunedModelManager.getInstance(context)
                    .getEnhancedSystemPromptForSelectedMaster();

            // Add messages
            JSONArray messagesArray = new JSONArray();

            // First add our enhanced system prompt
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", enhancedSystemPrompt);
            messagesArray.put(systemMsg);

            // Then add conversation history
            List<ConversationManager.Message> messages = conversationManager.getConversationHistory();
            for (ConversationManager.Message message : messages) {
                if (!"system".equals(message.getRole())) { // Skip system messages in history
                    JSONObject messageObj = new JSONObject();
                    messageObj.put("role", message.getRole());
                    messageObj.put("content", message.getContent());
                    messagesArray.put(messageObj);
                }
            }

            requestBody.put("messages", messagesArray);

            // Log request for debugging
            Log.d(TAG, "Using model: " + modelToUse + " with temperature: " + optimizationSettings.temperature);

            // Create HTTP request
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            // Make API call
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API Error: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Error details: " + response.body().string());
                    }
                    return "Sorry, I had trouble connecting to my chess brain. Please try again.";
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Enhanced API response received for " + selectedMaster);

                // Parse response
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray choices = jsonResponse.getJSONArray("choices");

                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                } else {
                    return "Sorry, I couldn't generate a response. Please try again.";
                }
            }

        } catch (JSONException | IOException e) {
            Log.e(TAG, "Error in enhanced chat completion: " + e.getMessage());
            return "I encountered an error processing your request: " + e.getMessage();
        }
    }

    /**
     * Enhanced contextual chat completion for better fine-tuned model performance
     */
    public String getEnhancedChatCompletion(String userInput, String gameContext) {
        if (context == null) {
            Log.w(TAG, "Context not available for enhanced completion");
            return getChatCompletion("You are a helpful chess coach.", userInput);
        }

        FineTunedModelManager manager = FineTunedModelManager.getInstance(context);

        // Generate contextual prompt optimized for fine-tuned models
        String contextualPrompt = manager.generateContextualPrompt(userInput, gameContext);
        String enhancedSystemPrompt = manager.getEnhancedSystemPromptForSelectedMaster();

        // Create temporary conversation for this request
        ConversationManager tempManager = new ConversationManager();
        tempManager.clear();
        tempManager.addSystemMessage(enhancedSystemPrompt);
        tempManager.addUserMessage(contextualPrompt);

        return getChatCompletionWithHistory(tempManager);
    }

    // New method to select fine-tuned model
    public void selectChessMaster(String master) {
        if (fineTunedModels.containsKey(master.toLowerCase())) {
            this.model = fineTunedModels.get(master.toLowerCase());
        } else {
            this.model = DEFAULT_MODEL;
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get a direct completion with system prompt and user message
     */
    public String getChatCompletion(String systemPrompt, String userMessage) {
        ConversationManager tempManager = new ConversationManager();
        tempManager.clear();
        tempManager.addSystemMessage(systemPrompt);
        tempManager.addUserMessage(userMessage);

        return getChatCompletionWithHistory(tempManager);
    }

    /**
     * Method to match existing API - this helps fix the compilation error
     */
    public String sendMessage(String userMessage) {
        if (context != null) {
            // Use enhanced system prompt for better fine-tuned model performance
            String enhancedSystemPrompt = FineTunedModelManager.getInstance(context)
                    .getEnhancedSystemPromptForSelectedMaster();

            // Create a temporary conversation
            ConversationManager tempManager = new ConversationManager();
            tempManager.addSystemMessage(enhancedSystemPrompt);
            tempManager.addUserMessage(userMessage);

            return getChatCompletionWithHistory(tempManager);
        } else {
            // Fallback for when context is not available
            ConversationManager tempManager = new ConversationManager();
            tempManager.addSystemMessage("You are Coach Tal, a FIDE-rated chess expert analyzing games. " +
                    "Be encouraging, supportive, and share insights about chess strategy in your responses. " +
                    "Keep your answers concise and focused.");
            tempManager.addUserMessage(userMessage);

            return getChatCompletionWithHistory(tempManager);
        }
    }
}