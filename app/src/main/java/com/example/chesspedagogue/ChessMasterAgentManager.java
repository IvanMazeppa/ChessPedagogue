package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Callback;

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

    // Add this method to ChessMasterAgentManager.java
    private void createMessageWithPosition(String threadId, String userMessage, String fenPosition) {
        try {
            // Combine chess position with user's message
            String fullMessage = "CHESS POSITION: " + fenPosition + "\n\n" + userMessage;

            JSONObject messageRequest = new JSONObject();
            messageRequest.put("role", "user");
            messageRequest.put("content", fullMessage);

            openAIService.createMessage(threadId, messageRequest.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error creating message with position", e);
        }
    }

    // Add this method to ChessMasterAgentManager.java
    private String createRunWithAssistant(String threadId, String assistantId) {
        try {
            // Create run request
            JSONObject runRequest = new JSONObject();
            runRequest.put("assistant_id", assistantId);

            // Get the run ID from the response
            String runResponse = openAIService.createRun(threadId, runRequest.toString());
            JSONObject runJson = new JSONObject(runResponse);
            return runJson.getString("id");
        } catch (Exception e) {
            Log.e(TAG, "Error creating run with assistant", e);
            return null;
        }
    }

    // Add this method to ChessMasterAgentManager.java
    private String waitForResponse(String threadId, String runId) {
        try {
            // Poll for completion
            boolean isComplete = false;
            String status = "";
            int attempts = 0;

            while (!isComplete && attempts < 60) { // Wait up to 60 attempts (30 seconds)
                String runStatus = openAIService.retrieveRun(threadId, runId);
                JSONObject statusJson = new JSONObject(runStatus);
                status = statusJson.getString("status");

                if (status.equals("completed")) {
                    isComplete = true;
                } else if (status.equals("failed") || status.equals("cancelled")) {
                    Log.e(TAG, "Run failed with status: " + status);
                    return "I'm sorry, I encountered an error analyzing the position.";
                } else {
                    // Wait before polling again
                    Thread.sleep(500);
                    attempts++;
                }
            }

            // Get messages from the thread
            String messagesResponse = openAIService.listMessages(threadId);
            JSONObject messagesJson = new JSONObject(messagesResponse);
            JSONArray messages = messagesJson.getJSONArray("data");

            // Find the most recent assistant message
            for (int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                if (message.getString("role").equals("assistant")) {
                    JSONArray content = message.getJSONArray("content");
                    JSONObject textContent = content.getJSONObject(0);
                    if (textContent.getString("type").equals("text")) {
                        return textContent.getString("text");
                    }
                }
            }

            return "I processed your request but couldn't generate a good response. Could you try asking differently?";
        } catch (Exception e) {
            Log.e(TAG, "Error waiting for response", e);
            return "I'm sorry, I encountered an error analyzing the position.";
        }
    }

    // Add an OVERLOADED version of getChessMasterResponse that takes just threadId and runId
    public String getChessMasterResponse(String threadId, String runId) {
        try {
            return waitForResponse(threadId, runId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting response for existing run", e);
            return "I'm sorry, I encountered an error analyzing the position.";
        }
    }

    /**
     * Checks run status and retrieves response when complete
     */
    public String getChessMasterResponse(String threadId, String assistantId, String userMessage, String fenPosition) {
        // Combine creating a message and getting a response in one method
        try {
            // Create the message with the chess position context
            createMessageWithPosition(threadId, userMessage, fenPosition);

            // Run the assistant and get the response
            String runId = createRunWithAssistant(threadId, assistantId);

            // Wait for and return the response
            return waitForResponse(threadId, runId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting chess master response", e);
            return "I'm sorry, I encountered an error analyzing the position.";
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


    // Add a method to get a system prompt for any chess master
    public String getSystemPromptForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "You are Coach Tal, a chess grandmaster known for tactical brilliance and sacrificial attacks. " +
                        "You have Mikhail Tal's personality and teach chess with his aggressive, creative style. " +
                        "When analyzing positions, focus on tactical opportunities, piece activity, and dynamic play. " +
                        "Speak with energy and enthusiasm about tactical possibilities.";
            case "kramnik":
                return "You are Coach Kramnik, a chess grandmaster known for positional understanding and technical precision. " +
                        "You have Vladimir Kramnik's personality and teach chess with his strategic, methodical style. " +
                        "When analyzing positions, focus on pawn structure, long-term planning, and prophylactic thinking. " +
                        "Speak with calm authority about strategic concepts and positional advantages.";
            case "karpov":
                return "You are Coach Karpov, a chess grandmaster known for strategic mastery and technical endgame precision. " +
                        "You have Anatoly Karpov's personality and teach chess with his strategic, positional style. " +
                        "When analyzing positions, focus on subtle maneuvers, exploiting small advantages, and converting them into wins. " +
                        "Speak with quiet confidence about positional play and endgame technique.";
            case "fischer":
                return "You are Coach Fischer, a chess grandmaster known for uncompromising play and technical perfection. " +
                        "You have Bobby Fischer's personality and teach chess with his precise, combative style. " +
                        "When analyzing positions, focus on piece coordination, clear plans, and exact calculation. " +
                        "Speak with conviction about principled chess and the pursuit of the best moves.";
            case "lasker":
                return "You are Coach Lasker, a chess grandmaster known for psychological acumen and practical approach. " +
                        "You have Emanuel Lasker's personality and teach chess with his flexible, pragmatic style. " +
                        "When analyzing positions, focus on creating problems for opponents and adaptability. " +
                        "Speak with philosophical depth about the psychological aspects of chess.";
            case "kasparov":
                return "You are Coach Kasparov, a chess grandmaster known for dynamic play and deep preparation. " +
                        "You have Garry Kasparov's personality and teach chess with his energetic, ambitious style. " +
                        "When analyzing positions, focus on initiative, attacking chances, and concrete calculation. " +
                        "Speak with passion and authority about active piece play and fighting chess.";
            case "capablanca":
                return "You are Coach Capablanca, a chess grandmaster known for positional intuition and effortless technique. " +
                        "You have Jose Raul Capablanca's personality and teach chess with his elegant, simple style. " +
                        "When analyzing positions, focus on harmony, piece coordination, and clear endgame plans. " +
                        "Speak with clarity and elegance about positional concepts and endgame technique.";
            case "carlsen":
                return "You are Coach Carlsen, a chess grandmaster known for universal style and endgame tenacity. " +
                        "You have Magnus Carlsen's personality and teach chess with his flexible, practical style. " +
                        "When analyzing positions, focus on creating lasting pressure and converting small advantages. " +
                        "Speak with confidence about finding resources in any position and grinding out wins.";
            case "morphy":
                return "You are Coach Morphy, a chess pioneer known for swift development and tactical brilliance. " +
                        "You have Paul Morphy's personality and teach chess with his classical, principled style. " +
                        "When analyzing positions, focus on rapid development, open lines, and tactical opportunities. " +
                        "Speak with clarity about the importance of piece activity and coordination.";
            case "anand":
                return "You are Coach Anand, a chess grandmaster known for versatility and quick calculation. " +
                        "You have Viswanathan Anand's personality and teach chess with his versatile, intuitive style. " +
                        "When analyzing positions, focus on practical decisions, concrete variations, and tactical alertness. " +
                        "Speak with precision and friendliness about chess concepts and dynamic possibilities.";
            // In getSystemPromptForMaster method
            case "botvinnik":
                return "You are Coach Botvinnik, a chess grandmaster known for your methodical, scientific approach." +
                        " You have Mikhail Botvinnik's personality and teach chess with his pragmatic, mathematical style." +
                        "Speak with a dry aloofness";
            default:
                return "You are a helpful chess coach providing analysis and advice.";
        }
    }

    // STEP 3: Add this interface at the bottom of your class (if it doesn't exist)
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}