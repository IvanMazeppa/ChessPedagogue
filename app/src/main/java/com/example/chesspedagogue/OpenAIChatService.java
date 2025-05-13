package com.example.chesspedagogue;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of ChatService that uses OpenAI's GPT-4 API.
 */
public class OpenAIChatService implements ChatService {
    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient httpClient;
    private final String apiKey;

    // Optionally define the model and parameters
    private final String model = "gpt-4";
    private final double temperature = 0.7;

    public OpenAIChatService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public String generateReply(List<ChatMessage> messageHistory) throws IOException {
        // Construct JSON payload for chat completion
        JSONObject payload = new JSONObject();
        try {
            payload.put("model", model);
            payload.put("temperature", temperature);
            JSONArray messagesArray = new JSONArray();
            for (ChatMessage msg : messageHistory) {
                JSONObject m = new JSONObject();
                m.put("role", msg.role);
                m.put("content", msg.content);
                messagesArray.put(m);
            }
            payload.put("messages", messagesArray);
        } catch (JSONException e) {
            Log.e("ChatService", "JSON construction error", e);
            return null;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());
        Request request = new Request.Builder()
                .url(CHAT_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e("ChatService", "Chat API error: " + response.code());
                return null;
            }

            String responseJson = response.body().string();
            // Parse the assistant's reply from JSON
            JSONObject json = new JSONObject(responseJson);
            JSONArray choices = json.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.optString("content", "");
            }
            return "";
        } catch (JSONException e) {
            Log.e("ChatService", "Failed to parse Chat response", e);
            return null;
        }
    }
}