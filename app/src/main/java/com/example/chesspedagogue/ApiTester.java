package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to test OpenAI API connections.
 */
public class ApiTester {
    private static final String TAG = "ApiTester";

    /**
     * Test if the OpenAI API key is working properly
     *
     * @param context The application context
     * @param callback The callback to handle the result
     */
    public static void testApiKey(Context context, ApiTestCallback callback) {
        String apiKey = ApiKeyConfig.getApiKey(context);
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onResult(false, "API key not set");
            return;
        }

        // Create a simple test thread
        new Thread(() -> {
            try {
                // Create a simple chat service instance
                ChatService chatService = new OpenAIChatService(apiKey);

                // Create a test message
                List<ChatMessage> messages = new ArrayList<>();
                messages.add(new ChatMessage("system", "You are a helpful chess coach."));
                messages.add(new ChatMessage("user", "Say hello in one word."));

                // Try to get a response
                String response = chatService.generateReply(messages);

                boolean success = response != null && !response.isEmpty();
                String message = success ? "API key is working!" : "Failed to get a response";

                // Call the callback on the main thread
                final boolean finalSuccess = success;
                final String finalMessage = message;

                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> callback.onResult(finalSuccess, finalMessage));

            } catch (IOException e) {
                Log.e(TAG, "API test failed", e);

                // Call the callback on the main thread
                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> callback.onResult(false, "Error: " + e.getMessage()));
            }
        }).start();
    }

    public interface ApiTestCallback {
        void onResult(boolean success, String message);
    }
}