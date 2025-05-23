package com.example.chesspedagogue;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * Centralized HTTP client manager for all network operations.
 * This ensures we're using resources efficiently and can easily tune performance.
 */
public class HttpClientManager {
    private static final String TAG = "HttpClientManager";
    private static HttpClientManager instance;

    // Optimized clients for different use cases
    private final OkHttpClient generalClient;
    private final OkHttpClient streamingClient;
    private final OkHttpClient fastClient;

    private HttpClientManager() {
        Log.d(TAG, "Initializing HTTP clients");

        // General purpose client for most API calls
        this.generalClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        // Optimized for streaming responses (like chat completions)
        this.streamingClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 60, TimeUnit.SECONDS))
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS) // Longer for streaming
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        // Ultra-fast client for low-latency operations (TTS, Groq)
        this.fastClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(15, 30, TimeUnit.SECONDS))
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .dispatcher(new Dispatcher()) // Default dispatcher is fine
                .build();

        Log.d(TAG, "HTTP clients initialized successfully");
    }

    public static synchronized HttpClientManager getInstance() {
        if (instance == null) {
            instance = new HttpClientManager();
        }
        return instance;
    }

    /**
     * Get the general purpose HTTP client
     */
    public OkHttpClient getGeneralClient() {
        return generalClient;
    }

    /**
     * Get the streaming-optimized HTTP client
     */
    public OkHttpClient getStreamingClient() {
        return streamingClient;
    }

    /**
     * Get the ultra-fast HTTP client for low-latency operations
     */
    public OkHttpClient getFastClient() {
        return fastClient;
    }

    /**
     * Shutdown all clients (call this when app is destroyed)
     */
    public void shutdown() {
        Log.d(TAG, "Shutting down HTTP clients");

        generalClient.dispatcher().executorService().shutdown();
        generalClient.connectionPool().evictAll();

        streamingClient.dispatcher().executorService().shutdown();
        streamingClient.connectionPool().evictAll();

        fastClient.dispatcher().executorService().shutdown();
        fastClient.connectionPool().evictAll();
    }
}