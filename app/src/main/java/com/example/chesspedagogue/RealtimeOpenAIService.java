package com.example.chesspedagogue;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okio.BufferedSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service for communicating with OpenAI's GPT-4o API
 * with support for streaming responses
 */
public class RealtimeOpenAIService {
    private static final String TAG = "RealtimeOpenAIService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client;
    private final Gson gson;
    private String apiKey;

    // Strong chess coach system prompt
// Find this constant in RealtimeOpenAIService.java
    private static final String SYSTEM_PROMPT =
            "You are Coach Tal, a brilliant attacking chess master and the 8th World Chess Champion. Your tactical vision and creativity are legendary. When analyzing positions, you MUST:\n\n" +
                    "CRITICAL INSTRUCTIONS - YOU ABSOLUTELY MUST START EVERY RESPONSE WITH A MOVE REFERENCE:\n" +
                    "- Begin your VERY FIRST SENTENCE by directly referencing a specific move from the history\n" +
                    "- Example: \"After 1.e4, you established central control...\"\n" +
                    "- If at starting position: \"I see we're at the starting position with no moves played yet.\"\n" +
                    "- NEVER give generic advice without tying it to specific moves in THIS game\n" +
                    "- Look for tactical opportunities and creative possibilities, just as Tal would\n\n" +
                    "Your analysis should feel personally tailored to this exact position and move history.";

    public interface StreamingResponseCallback {
        void onResponseStarted();
        void onResponseChunk(String chunk);
        void onResponseComplete(String fullResponse);
        void onError(String errorMessage);
    }

    public RealtimeOpenAIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Generate a streaming response from GPT-4o
     */
    public void generateStreamingResponse(String userInput, GameStateInfo gameState,
                                          StreamingResponseCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onError("API key not set");
            return;
        }

        try {
            // Create message list with system prompt, game context, and user input
            List<Message> messages = new ArrayList<>();

            // System prompt
            messages.add(new Message("system", SYSTEM_PROMPT));

            // Game context
            String gameContext = formatGameContext(gameState);
            messages.add(new Message("system", gameContext));

            // User input
            messages.add(new Message("user", userInput));

            // Create request
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-4o");
            requestBody.add("messages", gson.toJsonTree(messages));
            requestBody.addProperty("stream", true);
            requestBody.addProperty("max_tokens", 300);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestBody.toString()
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            // Execute streaming request
            callback.onResponseStarted();
            StringBuilder completeResponse = new StringBuilder();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError("API error: " + response.code());
                        return;
                    }

                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            callback.onError("Empty response");
                            return;
                        }

                        // Process the streaming response
                        String line;
                        try (BufferedSource source = responseBody.source()) {
                            while ((line = source.readUtf8Line()) != null) {
                                // Skip empty lines and [DONE]
                                if (line.isEmpty() || line.equals("[\"DONE\"]")) {
                                    continue;
                                }

                                // Remove "data: " prefix
                                if (line.startsWith("data: ")) {
                                    line = line.substring(6);
                                }

                                // Parse delta content
                                try {
                                    JsonObject json = JsonParser.parseString(line).getAsJsonObject();
                                    if (json.has("choices") && json.getAsJsonArray("choices").size() > 0) {
                                        JsonObject choice = json.getAsJsonArray("choices").get(0).getAsJsonObject();
                                        if (choice.has("delta")) {
                                            JsonObject delta = choice.getAsJsonObject("delta");
                                            if (delta.has("content")) {
                                                String content = delta.get("content").getAsString();
                                                completeResponse.append(content);
                                                callback.onResponseChunk(content);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing streaming response: " + e.getMessage());
                                }
                            }

                            // Notify completion
                            callback.onResponseComplete(completeResponse.toString());
                        }
                    }
                }
            });

        } catch (Exception e) {
            callback.onError("Error generating response: " + e.getMessage());
        }
    }

    /**
     * Format game context for the AI
     */
    private String formatGameContext(GameStateInfo gameState) {
        StringBuilder context = new StringBuilder();

        // Make move history visually prominent
        context.append("===============================================\n");
        context.append("⚠️ CRITICAL - GAME MOVE HISTORY (MUST REFERENCE) ⚠️\n");
        context.append("===============================================\n");

        List<String> moves = gameState.getMoveHistory();
        if (moves == null || moves.isEmpty()) {
            context.append("*** NO MOVES PLAYED YET - THIS IS THE STARTING POSITION ***\n");
        } else {
            context.append("The following moves have been played in sequence:\n\n");
            int moveNumber = 1;
            for (int i = 0; i < moves.size(); i += 2) {
                String whiteMove = moves.get(i);
                String blackMove = (i + 1 < moves.size()) ? moves.get(i + 1) : "";

                context.append(moveNumber).append(". ").append(whiteMove);
                if (!blackMove.isEmpty()) {
                    context.append(" ").append(blackMove);
                }
                context.append("\n");
                moveNumber++;
            }

            // Extra emphasis on the most recent move
            context.append("\n*** MOST RECENT MOVE: ").append(moves.get(moves.size() - 1)).append(" ***\n");
        }

        // Current position in FEN
        context.append("\nCurrent position (FEN): ").append(gameState.getCurrentFen()).append("\n");

        // Player color and game phase
        context.append("Player is playing as: ").append(gameState.getPlayerColor()).append("\n");
        context.append("Current game phase: ").append(gameState.getGamePhase()).append("\n");

        // Final explicit instruction with visual emphasis
        context.append("\n===============================================\n");
        context.append("YOU MUST BEGIN YOUR VERY FIRST SENTENCE BY\n");
        context.append("SPECIFICALLY MENTIONING A MOVE FROM THE GAME\n");
        context.append("===============================================\n");

        return context.toString();
    }

    /**
     * Inner class for API message formatting
     */
    private static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}