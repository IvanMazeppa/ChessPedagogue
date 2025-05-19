package com.example.chesspedagogue;

import android.util.Log;
import java.io.IOException;
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


    // Generic method to execute HTTP requests
    public Response executeRequest(Request request) throws IOException {
        return httpClient.newCall(request).execute();
    }
}