package com.example.chesspedagogue;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    private static final String TAG = "OpenAIService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OpenAIService instance;
    private final OkHttpClient client;
    private String apiKey;

    // Default model - can be changed as needed
    private String model = "gpt-4.1";

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
     * Private constructor
     */
    private OpenAIService() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get a completion from the conversation history
     */
    public String getChatCompletionWithHistory(ConversationManager conversationManager) {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return "Error: API key not configured.";
        }

        try {
            // Create request JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("max_tokens", 150);

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

            // Log request for debugging
            Log.d(TAG, "FULL OPENAI REQUEST: " + requestBody.toString());

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