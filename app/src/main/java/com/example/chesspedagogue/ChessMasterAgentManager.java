package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChessMasterAgentManager {
    private static final String TAG = "ChessMasterAgentManager";
    private static final String PREFS_NAME = "ChessMasterPrefs";
    private static final String KEY_TAL_ASSISTANT_ID = "tal_assistant_id";
    private static final String KEY_BOTVINNIK_ASSISTANT_ID = "botvinnik_assistant_id";
    private final Context context;
    private final OpenAIService openAIService;
    // Store assistant IDs for each master
    private final Map<String, String> assistantIds = new HashMap<>();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ChessMasterAgentManager(OpenAIService openAIService, Context context) {
        this.openAIService = openAIService;
        this.context = context.getApplicationContext();
    }


     public String getBotvinnikAssistantId() {
        // Check memory cache first
        if (assistantIds.containsKey("botvinnik")) {
            Log.d(TAG, "Using cached Botvinnik assistant ID");
            return assistantIds.get("botvinnik");
        }

        // Check persistent storage next
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String assistantId = prefs.getString(KEY_BOTVINNIK_ASSISTANT_ID, null);

        if (assistantId != null && !assistantId.isEmpty()) {
            Log.d(TAG, "Using stored Botvinnik assistant ID: " + assistantId);
            assistantIds.put("botvinnik", assistantId);
            return assistantId;
        }

        // Create new if none exists
        assistantId = createBotvinnikAssistant();

        // Store it if creation was successful
        if (assistantId != null) {
            prefs.edit().putString(KEY_BOTVINNIK_ASSISTANT_ID, assistantId).apply();
        }

        return assistantId;
    }

    // Locate the part in getBotvinnikAssistantId() that creates a new assistant:
// assistantId = createBotvinnikAssistant();

    // We won't change this yet since other code depends on it,
// but we'll make a new fully async version of getBotvinnikAssistantId()
    public void getBotvinnikAssistantIdFullyAsync(final Callback<String> callback) {
        // Run all of this on a background thread
        new Thread(() -> {
            try {
                // Check memory cache first
                if (assistantIds.containsKey("botvinnik")) {
                    Log.d(TAG, "Using cached Botvinnik assistant ID");
                    String cachedId = assistantIds.get("botvinnik");
                    mainHandler.post(() -> callback.onSuccess(cachedId));
                    return;
                }

                // Check persistent storage next
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String assistantId = prefs.getString(KEY_BOTVINNIK_ASSISTANT_ID, null);

                if (assistantId != null && !assistantId.isEmpty()) {
                    Log.d(TAG, "Using stored Botvinnik assistant ID: " + assistantId);
                    assistantIds.put("botvinnik", assistantId);
                    mainHandler.post(() -> callback.onSuccess(assistantId));
                    return;
                }

                // Create new if none exists - using our async method!
                createBotvinnikAssistantAsync(new Callback<String>() {
                    @Override
                    public void onSuccess(String newAssistantId) {
                        // Store it if creation was successful
                        if (newAssistantId != null) {
                            prefs.edit().putString(KEY_BOTVINNIK_ASSISTANT_ID, newAssistantId).apply();
                        }

                        // Return the result to original caller
                        callback.onSuccess(newAssistantId);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        callback.onError(errorMessage);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in fully async Botvinnik ID retrieval", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }

    public void getBotvinnikAssistantIdAsync(final Callback<String> callback) {
        // Run this on a background thread to avoid blocking the UI
        new Thread(() -> {
            try {
                // Use our existing method
                String assistantId = getBotvinnikAssistantId();

                // Call back on the main thread with the result
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(assistantId));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting Botvinnik assistant ID", e);
                // Call back with the error
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
                }
            }
        }).start();
    }

    public String createBotvinnikAssistant() {
        if (assistantIds.containsKey("botvinnik")) {
            Log.d(TAG, "Using existing Botvinnik Assistant: " + assistantIds.get("botvinnik"));
            return assistantIds.get("botvinnik");
        }

        String response = null;

        try {
            Log.d(TAG, "🌟 STARTING to create Botvinnik Assistant...");

            // Use the system prompt we created
            String botvinnikSystemPrompt = "You are Coach Botvinnik, a chess grandmaster known for your methodical, scientific approach to chess..."; // Full prompt here

            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", "Chess Coach Botvinnik");
            requestBody.put("instructions", botvinnikSystemPrompt);
            requestBody.put("model", "gpt-4.1-2025-04-14");

            // Add tools
            JSONArray tools = new JSONArray();
            JSONObject codeInterpreter = new JSONObject();
            codeInterpreter.put("type", "code_interpreter");
            tools.put(codeInterpreter);

            JSONObject fileSearch = new JSONObject();
            fileSearch.put("type", "file_search");
            tools.put(fileSearch);
            requestBody.put("tools", tools);

            // Add tool resources with Botvinnik's vector store
            JSONObject toolResources = new JSONObject();
            JSONObject fileSearchResources = new JSONObject();
            JSONArray vectorStoreIds = new JSONArray();
            vectorStoreIds.put("vs_682a4788c5508191949808a00cb6c4b7"); // Your Botvinnik vector store
            fileSearchResources.put("vector_store_ids", vectorStoreIds);
            toolResources.put("file_search", fileSearchResources);
            requestBody.put("tool_resources", toolResources);

            // Call OpenAI API
            response = openAIService.createAssistant(requestBody.toString());

            // Parse and store the assistant ID
            JSONObject responseJson = new JSONObject(response);
            String assistantId = responseJson.getString("id");
            assistantIds.put("botvinnik", assistantId);

            // Save to SharedPreferences
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString("botvinnik_assistant_id", assistantId)
                    .apply();

            return assistantId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating Botvinnik Assistant: " + e.getMessage(), e);
            return null;
        }
    }

    public void createBotvinnikAssistantAsync(final Callback<String> callback) {
        // Run on a background thread for smoothness
        new Thread(() -> {
            try {
                // Use our existing method
                String assistantId = createBotvinnikAssistant();

                // Call back on the main thread with the result
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(assistantId));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating Botvinnik assistant", e);
                // Call back with the error
                if (callback != null) {
                    final String errorMsg = e.getMessage();
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(errorMsg));
                }
            }
        }).start();
    }



    /**
     * Creates a new thread for conversation
     */
    public String createConversationThread() {
        try {
            Log.d(TAG, "🌟 STARTING to create conversation thread...");

            String response = openAIService.createThread();
            Log.d(TAG, "Thread creation response: " + response);

            JSONObject responseJson = new JSONObject(response);
            String threadId = responseJson.getString("id");

            Log.d(TAG, "✅ Successfully created conversation thread with ID: " + threadId);
            return threadId;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error creating conversation thread: " + e.getMessage(), e);
            return null;
        }
    }

    // STEP 2: Add this NEW method below it (don't delete the original yet)
    public void createConversationThreadAsync(final Callback<String> callback) {
        // Run on a background thread
        new Thread(() -> {
            try {
                // Use the existing method
                String threadId = createConversationThread();

                // Call back on the main thread with the result
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(threadId));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in async thread creation", e);
                // Call back with the error
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
                }
            }
        }).start();
    }


    /**
     * Creates a message in a thread
     */
    public String createMessage(String threadId, String messageBody) {
        try {
            String response = openAIService.createMessage(threadId, messageBody);
            return response;
        } catch (Exception e) {
            Log.e(TAG, "Error creating message", e);
            return null;
        }
    }

    /**
     * Sends a chess question to the assistant
     */
    public String sendMessageWithPosition(String threadId, String assistantId,
                                          String userMessage, String fenPosition) {
        try {
            // Combine chess position with user's message
            String fullMessage = "CHESS POSITION: " + fenPosition + "\n\n" + userMessage;

            JSONObject messageRequest = new JSONObject();
            messageRequest.put("role", "user");
            messageRequest.put("content", fullMessage);

            openAIService.createMessage(threadId, messageRequest.toString());

            // Run the assistant on this thread
            JSONObject runRequest = new JSONObject();
            runRequest.put("assistant_id", assistantId);

            // Get the run ID from the response
            String runResponse = openAIService.createRun(threadId, runRequest.toString());
            JSONObject runJson = new JSONObject(runResponse);
            String runId = runJson.getString("id");

            Log.d(TAG, "Created run with ID: " + runId);

            return runId;
        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            return null;
        }
    }

    /**
     * Checks run status and retrieves response when complete
     */
    public String getChessMasterResponse(String threadId, String runId) {
        try {
            // Poll for completion
            boolean completed = false;
            int maxAttempts = 30; // Adjust as needed
            int attempts = 0;

            while (!completed && attempts < maxAttempts) {
                String runResponse = openAIService.retrieveRun(threadId, runId);
                JSONObject runJson = new JSONObject(runResponse);
                String status = runJson.getString("status");

                if (status.equals("completed")) {
                    completed = true;
                } else if (status.equals("failed") || status.equals("cancelled")) {
                    return "Sorry, I couldn't analyze this position. Let's try again.";
                } else {
                    // Wait before polling again
                    Thread.sleep(1000);
                    attempts++;
                }
            }

            if (!completed) {
                return "It's taking longer than expected to analyze this position. Let's try again.";
            }

            // Get the assistant's message
            String messagesResponse = openAIService.listMessages(threadId);
            JSONObject messagesJson = new JSONObject(messagesResponse);
            JSONArray data = messagesJson.getJSONArray("data");

            // Find the most recent assistant message
            for (int i = 0; i < data.length(); i++) {
                JSONObject message = data.getJSONObject(i);
                if (message.getString("role").equals("assistant")) {
                    JSONArray content = message.getJSONArray("content");
                    JSONObject textContent = content.getJSONObject(0);
                    return textContent.getJSONObject("text").getString("value");
                }
            }

            return "I seem to have lost my train of thought. Can you repeat your question?";

        } catch (Exception e) {
            Log.e(TAG, "Error getting response", e);
            return "Sorry, I had trouble processing that. Let's try again.";
        }
    }

    /**
     * Gets the chess master's response asynchronously, with proper background threading
     */
    public void getChessMasterResponseAsync(String threadId, String runId, Callback<String> callback) {
        // Run everything on a background thread
        new Thread(() -> {
            try {
                // Use our existing method
                String response = getChessMasterResponse(threadId, runId);

                // Return the result on the main thread
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                Log.e(TAG, "Error getting chess master response", e);
                // Call back with the error
                final String errorMsg = e.getMessage();
                mainHandler.post(() -> callback.onError(errorMsg != null ? errorMsg : "Unknown error"));
            }
        }).start();
    }

    // STEP 3: Add this interface at the bottom of your class (if it doesn't exist)
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}