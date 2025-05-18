package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChessMasterAgentManager {
    private final Context context;
    private static final String TAG = "ChessMasterAgentManager";
    private final OpenAIService openAIService;


    private static final String PREFS_NAME = "ChessMasterPrefs";
    private static final String KEY_TAL_ASSISTANT_ID = "tal_assistant_id";


    // Store assistant IDs for each master
    private Map<String, String> assistantIds = new HashMap<>();

    public ChessMasterAgentManager(OpenAIService openAIService, Context context) {
        this.openAIService = openAIService;
        this.context = context.getApplicationContext();
    }

    /**
     * Creates a Tal Assistant if not already created
     */
    /**
     * Creates a Tal Assistant if not already created
     */
    public String createTalAssistant() {
        if (assistantIds.containsKey("tal")) {
            Log.d(TAG, "Using existing Tal Assistant: " + assistantIds.get("tal"));
            return assistantIds.get("tal");
        }

        String response = null;

        try {
            Log.d(TAG, "🌟 STARTING to create Tal Assistant...");

            // Get Tal's system prompt from your existing manager
            String talSystemPrompt = FineTunedModelManager.getInstance(context)
                    .getSystemPromptForMaster("tal");

            Log.d(TAG, "Using system prompt: " + talSystemPrompt.substring(0, Math.min(100, talSystemPrompt.length())) + "...");

            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", "Chess Coach Tal");
            requestBody.put("instructions", talSystemPrompt);
            requestBody.put("model", "gpt-4.1-2025-04-14");

            // Add tools with detailed logging
            Log.d(TAG, "Adding tools to Assistant...");
            JSONArray tools = new JSONArray();
// In ChessMasterAgentManager.java - createTalAssistant() method
            JSONObject codeInterpreter = new JSONObject();
            codeInterpreter.put("type", "code_interpreter");
            tools.put(codeInterpreter);

// Add file_search tool (without vector_store_ids here)
            JSONObject fileSearch = new JSONObject();
            fileSearch.put("type", "file_search");
            tools.put(fileSearch);
            requestBody.put("tools", tools);

// Create the tool_resources structure
            JSONObject toolResources = new JSONObject();
            JSONObject fileSearchResources = new JSONObject();
            JSONArray vectorStoreIds = new JSONArray();
            vectorStoreIds.put("vs_6829ffe34a9881919fe788eb94e8b107"); // your Tal vector store
            fileSearchResources.put("vector_store_ids", vectorStoreIds);
            toolResources.put("file_search", fileSearchResources);
            requestBody.put("tool_resources", toolResources);

// Remove the separate retrieval_tool_config
// No need for retrievalToolConfig anymore!

            // Log the complete request body
            Log.d(TAG, "Assistant creation request: " + requestBody.toString());

            // Call OpenAI API to create assistant
            Log.d(TAG, "Calling OpenAI API to create Assistant...");
            response = openAIService.createAssistant(requestBody.toString());

            // Log the complete response
            Log.d(TAG, "Assistant creation response: " + response);

            // Parse the assistant ID from response
            JSONObject responseJson = new JSONObject(response);
            String assistantId = responseJson.getString("id");

            // Store the ID for future use
            assistantIds.put("tal", assistantId);

            Log.d(TAG, "✅ Successfully created Tal Assistant with ID: " + assistantId);
            return assistantId;
// Add this in the catch block
        } catch (Exception e) {
            Log.e(TAG, "❌ Error creating Tal Assistant: " + e.getMessage(), e);

            // Add this to see the exact API error
            if (e instanceof JSONException && response != null && response.contains("error")) {
                try {
                    JSONObject errorJson = new JSONObject(response);
                    if (errorJson.has("error")) {
                        JSONObject error = errorJson.getJSONObject("error");
                        Log.e(TAG, "API Error Details: " + error.getString("message"));
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "Error parsing API error: " + e2.getMessage());
                }
            }

            return null;
        }
    }

    // Add this method to get an existing assistant or create a new one
    public String getTalAssistantId() {
        // Check memory cache first
        if (assistantIds.containsKey("tal")) {
            Log.d(TAG, "Using cached Tal assistant ID");
            return assistantIds.get("tal");
        }

        // Check persistent storage next
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String assistantId = prefs.getString(KEY_TAL_ASSISTANT_ID, null);

        if (assistantId != null && !assistantId.isEmpty()) {
            Log.d(TAG, "Using stored Tal assistant ID: " + assistantId);
            assistantIds.put("tal", assistantId);
            return assistantId;
        }

        // Create new if none exists
        assistantId = createTalAssistant();

        // Store it if creation was successful
        if (assistantId != null) {
            prefs.edit().putString(KEY_TAL_ASSISTANT_ID, assistantId).apply();
        }

        return assistantId;
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
}