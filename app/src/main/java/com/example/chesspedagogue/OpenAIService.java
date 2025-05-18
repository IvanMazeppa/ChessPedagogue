package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
 * Service for communicating with OpenAI API
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

        return "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";

    }

    /**
     * Update the getChatCompletionWithHistory method
     */
    public String getChatCompletionWithHistory(ConversationManager conversationManager) {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return "Error: API key not configured.";
        }

        try {
            // Create request JSON
            JSONObject requestBody = new JSONObject();

            // Get the appropriate model - NEW CODE
            String modelToUse = getModelForRequest();
            requestBody.put("model", modelToUse);
            requestBody.put("max_tokens", 350); // Increased for more detailed responses

            // Get the appropriate system prompt based on selected master - NEW CODE
            String systemPrompt = "You are a helpful chess coach.";
            if (context != null) {
                systemPrompt = FineTunedModelManager.getInstance(context)
                        .getSystemPromptForSelectedMaster();
            }

            // Add messages
            JSONArray messagesArray = new JSONArray();

            // First add our system prompt - NEW CODE
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
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
            Log.d(TAG, "Using model: " + modelToUse);
            Log.d(TAG, "FULL OPENAI REQUEST: " + requestBody);

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
                Log.d(TAG, "OPENAI RESPONSE: " + responseBody);

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
            Log.e(TAG, "Error in chat completion: " + e.getMessage());
            return "I encountered an error processing your request: " + e.getMessage();
        }
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
        // Create a temporary conversation
        ConversationManager tempManager = new ConversationManager();

        // Add system message
        tempManager.addSystemMessage("You are Coach Tal, a FIDE-rated chess expert analyzing games. " +
                "Be encouraging, supportive, and share insights about chess strategy in your responses. " +
                "Keep your answers concise and focused.");

        // Add user message
        tempManager.addUserMessage(userMessage);

        // Get response
        return getChatCompletionWithHistory(tempManager);
    }
}