package com.example.chesspedagogue;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

/**
 * Unified client for OpenAI API interactions.
 * This class centralizes HTTP client management and API key handling.
 */
public class OpenAIClient {
    private static final String TAG = "OpenAIClient";
    private static OpenAIClient instance;

    // One shared HTTP client with optimized settings
    private final OkHttpClient httpClient;
    private String apiKey;



    // Private constructor for singleton
    private OpenAIClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Log.d(TAG, "OpenAIClient initialized with optimized HTTP client");
    }

    // Singleton accessor
    public static synchronized OpenAIClient getInstance() {
        if (instance == null) {
            instance = new OpenAIClient();
        }
        return instance;
    }

    // Set the API key for all services
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        Log.d(TAG, "API key set, length: " + (apiKey != null ? apiKey.length() : 0));
    }

    // Add this to OpenAIClient.java
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    // Also improve the getAuthorizationHeader method to include validation
    public String getAuthorizationHeader() {
        if (!hasApiKey()) {
            Log.e(TAG, "Attempting to use API key before it's set!");
            // Try to load from config as a fallback
            String configKey = ApiKeyConfig.getApiKey(null);
            if (configKey != null && !configKey.isEmpty()) {
                setApiKey(configKey);
                Log.d(TAG, "Loaded API key from config as fallback");
            }
        }
        return "Bearer " + apiKey;
    }

    // Get the HTTP client (for existing services to use during transition)
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    // Add these methods to OpenAIClient.java

    /**
     * Performs a chat completion request directly
     */
    public String createChatCompletion(String systemPrompt, String userPrompt) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "gpt-4.1");

            JSONArray messages = new JSONArray();

            // Add system message
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.put(systemMessage);

            // Add user message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            messages.put(userMessage);

            requestBody.put("messages", messages);
        } catch (JSONException e) {
            throw new IOException("Error creating request JSON: " + e.getMessage());
        }

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", getAuthorizationHeader())
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API request failed: " + response.code());
            }

            String responseJson = response.body().string();
            try {
                JSONObject json = new JSONObject(responseJson);
                JSONArray choices = json.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                }
            } catch (JSONException e) {
                throw new IOException("Error parsing API response: " + e.getMessage());
            }
        }

        return "No response generated";
    }

    // Add these methods to OpenAIClient.java

    /**
     * Executes a request on a background thread and returns the result
     * This solves the NetworkOnMainThreadException problem!
     */
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    /**
     * Safely executes any API call on a background thread
     */
    public void executeAsync(Runnable backgroundTask, Runnable onComplete) {
        new Thread(() -> {
            try {
                backgroundTask.run();
                if (onComplete != null) {
                    new Handler(Looper.getMainLooper()).post(onComplete);
                }
            } catch (Exception e) {
                Log.e(TAG, "API error: " + e.getMessage(), e);
            }
        }).start();
    }

    /**
     * Helper for making any API call safely in the background
     */
    public <T> void executeAsync(Callable<T> apiCall, ApiCallback<T> callback) {
        new Thread(() -> {
            try {
                T result = apiCall.call();
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(result));
                }
            } catch (Exception e) {
                Log.e(TAG, "API error: " + e.getMessage(), e);
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure(e));
                }
            }
        }).start();
    }

    // Generic method to execute HTTP requests
    public Response executeRequest(Request request) throws IOException {
        return httpClient.newCall(request).execute();
    }
}