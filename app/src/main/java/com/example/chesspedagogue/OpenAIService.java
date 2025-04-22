package com.example.chesspedagogue;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Service for communicating with OpenAI API
 */
public class OpenAIService {
    private static final String TAG = "OpenAIService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OpenAIService instance;
    private final OkHttpClient client;
    private final Gson gson;
    private String apiKey;

    // Model to use - can be changed based on your preference
    private String model = "gpt-4-turbo-preview";

    // Chess context for continuity in conversations
    private final List<Message> conversationHistory = new ArrayList<>();
    private static final int MAX_CONVERSATION_LENGTH = 10;

    private OpenAIService() {
        // Configure OkHttpClient with timeouts
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        gson = new GsonBuilder().create();

        // Initialize conversation with system message defining the coach's role
        Message systemMessage = new Message("system",
                "You are an encouraging and patient chess coach. " +
                        "Your name is Coach Magnus. You provide clear, concise advice about chess " +
                        "positions and strategies in an accessible way. " +
                        "Tailor your advice to beginners and intermediate players. " +
                        "Use simple language and explain chess concepts briefly. " +
                        "Be encouraging even when pointing out mistakes. " +
                        "Keep responses under 3 sentences when possible.");

        conversationHistory.add(systemMessage);
    }

    public static synchronized OpenAIService getInstance() {
        if (instance == null) {
            instance = new OpenAIService();
        }
        return instance;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Sends a user message to the API and returns the response
     */
    public String sendMessage(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "API key not set");
            return "Error: API key not configured.";
        }

        try {
            // Add the user message to the conversation history
            conversationHistory.add(new Message("user", userMessage));

            // Trim conversation if it gets too long
            if (conversationHistory.size() > MAX_CONVERSATION_LENGTH + 1) { // +1 for system message
                conversationHistory.subList(1, 2).clear(); // Remove oldest user/assistant pair
            }

            // Create the API request
            ChatRequest chatRequest = new ChatRequest(model, conversationHistory);
            String requestJson = gson.toJson(chatRequest);

            RequestBody body = RequestBody.create(requestJson, JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            // Execute the request
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                if (response.body() != null) {
                    Log.e(TAG, "API Error: " + response.body().string());
                }
                return "Sorry, I had trouble connecting to my chess brain. Please try again.";
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return "Sorry, I received an empty response. Please try again.";
            }

            // Parse the response
            String responseJson = responseBody.string();
            ChatResponse chatResponse = gson.fromJson(responseJson, ChatResponse.class);

            if (chatResponse != null && chatResponse.choices != null && !chatResponse.choices.isEmpty()) {
                String assistantResponse = chatResponse.choices.get(0).message.content;

                // Add the assistant's response to the conversation history
                conversationHistory.add(new Message("assistant", assistantResponse));

                return assistantResponse;
            } else {
                return "Sorry, I couldn't generate a response. Please try again.";
            }

        } catch (IOException e) {
            Log.e(TAG, "Error sending message to OpenAI", e);
            return "Sorry, there was a problem communicating with the chess coach. Please check your internet connection.";
        }
    }

    /**
     * Generate chess advice based on the current position
     */
    public String generateChessAdvice(String fen, String lastMove, String playerColor) {
        String prompt = "The current chess position in FEN notation is: " + fen + ". ";

        if (lastMove != null && !lastMove.isEmpty()) {
            prompt += "The last move was " + lastMove + ". ";
        }

        prompt += "I'm playing as " + playerColor + ". ";
        prompt += "Please give me brief advice about my position and what I should be focusing on.";

        return sendMessage(prompt);
    }

    // First, let's update OpenAIService.java with an enhanced method
    public String generateEnhancedChessAdvice(String fen, List<String> moveHistory, String playerColor) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("I'm analyzing a chess game in progress.\n\n");
        prompt.append("Current position (FEN): ").append(fen).append("\n\n");

        // Add move history with proper formatting
        if (moveHistory != null && !moveHistory.isEmpty()) {
            prompt.append("Game moves so far:\n");
            int moveNum = 1;
            for (int i = 0; i < moveHistory.size(); i += 2) {
                prompt.append(moveNum).append(". ");
                prompt.append(moveHistory.get(i));
                if (i + 1 < moveHistory.size()) {
                    prompt.append(" ").append(moveHistory.get(i + 1));
                }
                prompt.append("\n");
                moveNum++;
            }
        }

        prompt.append("\nI'm playing as ").append(playerColor);
        prompt.append(".\n\nPlease analyze my position and suggest what I should focus on next. Consider the opening principles, piece development, pawn structure, tactical opportunities, and my overall strategic direction.");

        return sendMessage(prompt.toString());
    }

    /**
     * Evaluate a specific move
     */
    public String evaluateMove(String fen, String move, String playerColor) {
        String prompt = "In this chess position: " + fen + ", ";
        prompt += "I'm playing as " + playerColor + " and considering the move " + move + ". ";
        prompt += "Is this a good move? Why or why not? Please be concise.";

        return sendMessage(prompt);
    }

    /**
     * Reset the conversation history, keeping only the system message
     */
    public void resetConversation() {
        Message systemMessage = conversationHistory.get(0);
        conversationHistory.clear();
        conversationHistory.add(systemMessage);
    }

    // Request and response classes for OpenAI API
    private static class Message {
        @SerializedName("role")
        String role;

        @SerializedName("content")
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private static class ChatRequest {
        @SerializedName("model")
        String model;

        @SerializedName("messages")
        List<Message> messages;

        public ChatRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    private static class ChatResponse {
        @SerializedName("choices")
        List<Choice> choices;

        private static class Choice {
            @SerializedName("message")
            Message message;
        }
    }
}