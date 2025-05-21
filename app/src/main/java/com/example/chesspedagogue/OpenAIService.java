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

// Similarly update createThread, createMessage, etc.


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

        return "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";

    }

    /**
     * Get a chat completion using the conversation history
     */
    public String getChatCompletionWithHistory(ConversationManager conversationManager) {
        try {
            // Get messages from conversation manager
            List<ConversationManager.Message> messages = conversationManager.getConversationHistory();

            // Add helpful debugging to see what messages we're processing
            Log.d(TAG, "Retrieved " + messages.size() + " messages from conversation history");
            for (ConversationManager.Message msg : messages) {
                Log.d(TAG, "Message role: " + msg.getRole() + ", content preview: " +
                        (msg.getContent().length() > 20 ? msg.getContent().substring(0, 20) + "..." : msg.getContent()));
            }

            // Create the JSON objects we need
            JSONObject requestBody = new JSONObject();
            JSONArray messagesArray = new JSONArray();

            // RIGHT HERE! Add the new code snippet exactly here! 👇
            if (messages.size() <= 1) {  // If we only got a system message
                Log.w(TAG, "Only found system message, adding user message manually!");
                // Get the user's question from the prompt
                String userQuestion = "What is your name?";  // Default fallback

                // Add the user message manually
                JSONObject userMessage = new JSONObject();
                userMessage.put("role", "user");
                userMessage.put("content", userQuestion);
                messagesArray.put(userMessage);
            }

            // Check if we have our API key
            if (apiKey == null || apiKey.isEmpty()) {
                Log.e(TAG, "API key not set");
                return "Error: API key not configured.";
            }


            // Add system message first (only one)
            boolean foundSystemMessage = false;
            for (ConversationManager.Message message : messages) {
                if ("system".equals(message.getRole())) {
                    JSONObject messageObj = new JSONObject();
                    messageObj.put("role", message.getRole());
                    messageObj.put("content", message.getContent());
                    messagesArray.put(messageObj);
                    foundSystemMessage = true;
                    break; // Just use the first system message
                }
            }

            // If no system message found, add a default one
            if (!foundSystemMessage) {
                JSONObject systemMsg = new JSONObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", "You are Coach Tal, a chess grandmaster with deep knowledge...");
                messagesArray.put(systemMsg);
            }

            // Now add all non-system messages in order
            for (ConversationManager.Message message : messages) {
                if (!"system".equals(message.getRole())) {
                    JSONObject messageObj = new JSONObject();
                    messageObj.put("role", message.getRole());
                    messageObj.put("content", message.getContent());
                    messagesArray.put(messageObj);
                }
            }

            // Build the complete request body
            requestBody.put("model", getModelForRequest());
            requestBody.put("max_tokens", 350);
            requestBody.put("messages", messagesArray);

            // Log the request for debugging
            String requestString = requestBody.toString();
            Log.d(TAG, "FULL OPENAI REQUEST: " + requestString);

            // Create and execute the HTTP request
            RequestBody body = RequestBody.create(JSON, requestString);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API request failed: " + response.code());
                    return "Error communicating with OpenAI: " + response.code();
                }

                // Parse the response
                String responseJson = response.body().string();
                Log.d(TAG, "OPENAI RESPONSE: " + responseJson);

                JSONObject json = new JSONObject(responseJson);
                JSONArray choices = json.getJSONArray("choices");

                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                } else {
                    return "No response generated by the AI.";
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage(), e);
            return "Error formatting request: " + e.getMessage();
        } catch (IOException e) {
            Log.e(TAG, "Network error: " + e.getMessage(), e);
            return "Network error: " + e.getMessage();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            return "Error: " + e.getMessage();
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