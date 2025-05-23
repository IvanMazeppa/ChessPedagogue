package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Unified OpenAI service that handles all OpenAI API interactions.
 * Consolidated from OpenAIService, OpenAIClient, and UnifiedOpenAIService.
 */
public class OpenAIService {
    private static final String TAG = "OpenAIService";

    // API endpoints
    static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    private static final String TRANSCRIBE_URL = "https://api.openai.com/v1/audio/transcriptions";

    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DEFAULT_MODEL = "gpt-4-turbo-preview";

    private static OpenAIService instance;

    private final OkHttpClient client;
    private final OkHttpClient streamingClient;
    private final Gson gson;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private final Map<String, String> fineTunedModels = new HashMap<>();

    private String apiKey;
    private Context context;
    private String model = DEFAULT_MODEL;

    /**
     * Private constructor
     */
    private OpenAIService() {
        // Standard client for regular requests
        this.client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        // Streaming client with longer timeouts
        this.streamingClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 60, TimeUnit.SECONDS))
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();

        initFineTunedModels();
    }

    /**
     * Get singleton instance
     */
    public static synchronized OpenAIService getInstance() {
        if (instance == null) {
            instance = new OpenAIService();
        }
        return instance;
    }

    /**
     * Initialize with context
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Set the API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        Log.d(TAG, "API key set, length: " + (apiKey != null ? apiKey.length() : 0));
    }

    /**
     * Check if API key is set
     */
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * Get authorization header
     */
    private String getAuthorizationHeader() {
        if (!hasApiKey()) {
            Log.e(TAG, "Attempting to use API key before it's set!");
            // Try to load from config as a fallback
            if (context != null) {
                String configKey = ApiKeyConfig.getApiKey(context);
                if (configKey != null && !configKey.isEmpty()) {
                    setApiKey(configKey);
                    Log.d(TAG, "Loaded API key from config as fallback");
                }
            }
        }
        return "Bearer " + apiKey;
    }

    /**
     * Get the HTTP client
     */
    public OkHttpClient getHttpClient() {
        return client;
    }

    /**
     * Set the model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Select chess master (for fine-tuned models)
     */
    public void selectChessMaster(String master) {
        if (fineTunedModels.containsKey(master.toLowerCase())) {
            this.model = fineTunedModels.get(master.toLowerCase());
        } else {
            this.model = DEFAULT_MODEL;
        }
    }

    /**
     * Initialize fine-tuned models
     */
    private void initFineTunedModels() {
        // Use your actual fine-tuned model IDs here
        String actualModelId = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";

        fineTunedModels.put("tal", "ft:gpt-4.1-2025-04-14:personal:tal:BaC4mVTl");
        fineTunedModels.put("kramnik", actualModelId);
        fineTunedModels.put("karpov", actualModelId);
        fineTunedModels.put("fischer", actualModelId);
        fineTunedModels.put("lasker", actualModelId);
        fineTunedModels.put("kasparov", actualModelId);
        fineTunedModels.put("capablanca", actualModelId);
        fineTunedModels.put("carlsen", actualModelId);
        fineTunedModels.put("morphy", actualModelId);
        fineTunedModels.put("anand", actualModelId);
        fineTunedModels.put("alekhine", "ft:gpt-4.1-2025-04-14:personal:alekhine:BZoqsSDe");
        fineTunedModels.put("botvinnik", "gpt-4.1"); // Uses Assistants API
    }

    /**
     * Get chat completion with conversation history
     */
    public String getChatCompletionWithHistory(ConversationManager conversationManager) {
        if (!hasApiKey()) {
            Log.e(TAG, "API key not set");
            return "Error: API key not configured.";
        }

        try {
            String modelToUse = getModelForRequest();

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelToUse);

            // Add optimization settings if using fine-tuned model
            if (context != null) {
                FineTunedModelManager.ModelOptimizationSettings settings =
                        FineTunedModelManager.getInstance(context).getOptimizationSettings();
                requestBody.put("temperature", settings.temperature);
                requestBody.put("max_tokens", settings.maxTokens);
                if (settings.allowCreativeLiberty) {
                    requestBody.put("top_p", 0.9);
                }
            }

            // Add messages
            JSONArray messagesArray = new JSONArray();
            List<ConversationManager.Message> messages = conversationManager.getConversationHistory();

            for (ConversationManager.Message message : messages) {
                JSONObject messageObj = new JSONObject();
                messageObj.put("role", message.getRole());
                messageObj.put("content", message.getContent());
                messagesArray.put(messageObj);
            }

            requestBody.put("messages", messagesArray);

            // Make request
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", getAuthorizationHeader())
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API Error: " + response.code());
                    return "Sorry, I had trouble connecting. Please try again.";
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray choices = jsonResponse.getJSONArray("choices");

                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in chat completion: " + e.getMessage());
            return "I encountered an error: " + e.getMessage();
        }

        return "Sorry, I couldn't generate a response.";
    }

    /**
     * Streaming chat completion for ultra-low latency
     */
    public void generateStreamingChatResponse(String systemPrompt, String userMessage,
                                              StreamingChatCallback callback) {
        executorService.execute(() -> {
            try {
                JSONObject requestJson = new JSONObject();
                requestJson.put("model", getModelForRequest());
                requestJson.put("stream", true);

                JSONArray messages = new JSONArray();

                JSONObject systemMsg = new JSONObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                messages.put(systemMsg);

                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", userMessage);
                messages.put(userMsg);

                requestJson.put("messages", messages);

                RequestBody body = RequestBody.create(requestJson.toString(), JSON);
                Request request = new Request.Builder()
                        .url(API_URL)
                        .header("Authorization", getAuthorizationHeader())
                        .post(body)
                        .build();

                Response response = streamingClient.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.body().byteStream()));

                    StringBuilder completeResponse = new StringBuilder();
                    StringBuilder currentChunk = new StringBuilder();
                    String line;
                    boolean firstChunkSent = false;

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);

                            if ("[DONE]".equals(data)) {
                                break;
                            }

                            try {
                                JSONObject chunk = new JSONObject(data);
                                JSONArray choices = chunk.getJSONArray("choices");

                                if (choices.length() > 0) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");

                                    if (delta.has("content")) {
                                        String content = delta.getString("content");
                                        completeResponse.append(content);
                                        currentChunk.append(content);

                                        // Send chunks based on content
                                        if (!firstChunkSent && currentChunk.length() > 20) {
                                            String firstChunk = currentChunk.toString();
                                            mainHandler.post(() -> callback.onPartialResponse(firstChunk, true));
                                            currentChunk.setLength(0);
                                            firstChunkSent = true;
                                        } else if (firstChunkSent &&
                                                (currentChunk.length() > 60 ||
                                                        content.contains(".") ||
                                                        content.contains("!"))) {
                                            String chunkText = currentChunk.toString();
                                            if (chunkText.trim().length() > 0) {
                                                mainHandler.post(() -> callback.onPartialResponse(chunkText, false));
                                                currentChunk.setLength(0);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Error parsing streaming chunk: " + e.getMessage());
                            }
                        }
                    }

                    // Send any remaining content
                    if (currentChunk.length() > 0) {
                        String finalChunk = currentChunk.toString();
                        mainHandler.post(() -> callback.onPartialResponse(finalChunk, false));
                    }

                    // Signal completion
                    String fullResponse = completeResponse.toString();
                    mainHandler.post(() -> callback.onComplete(fullResponse));

                } else {
                    String errorMsg = "Streaming API error: " + response.code();
                    mainHandler.post(() -> callback.onError(new IOException(errorMsg)));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    /**
     * Transcribe audio
     */
    public void transcribeAudio(byte[] audioData, ApiCallback<String> callback) {
        executorService.execute(() -> {
            try {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "audio.wav",
                                RequestBody.create(MediaType.parse("audio/wav"), audioData))
                        .addFormDataPart("model", "whisper-1")
                        .addFormDataPart("language", "en")
                        .addFormDataPart("prompt", "Chess game analysis")
                        .build();

                Request request = new Request.Builder()
                        .url(TRANSCRIBE_URL)
                        .header("Authorization", getAuthorizationHeader())
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseJson = response.body().string();
                    JSONObject json = new JSONObject(responseJson);
                    String transcribedText = json.getString("text");
                    mainHandler.post(() -> callback.onSuccess(transcribedText));
                } else {
                    String errorMsg = "Transcription error: " + response.code();
                    mainHandler.post(() -> callback.onFailure(new IOException(errorMsg)));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    /**
     * Simple chat completion
     */
    public String getChatCompletion(String systemPrompt, String userMessage) {
        ConversationManager tempManager = new ConversationManager(null, null, null);
        tempManager.clear();
        tempManager.addSystemMessage(systemPrompt);
        tempManager.addUserMessage(userMessage);
        return getChatCompletionWithHistory(tempManager);
    }

    /**
     * Synchronous version of generateChatResponse for backward compatibility
     */
    public String generateChatResponseSync(String systemPrompt, String userMessage) throws IOException {
        final String[] result = new String[1];
        final Exception[] error = new Exception[1];
        final Object lock = new Object();

        // Create a temporary conversation manager
        ConversationManager tempManager = new ConversationManager(null, null, null);
        tempManager.clear();
        tempManager.addSystemMessage(systemPrompt);
        tempManager.addUserMessage(userMessage);

        // Use our existing method but wait for result
        String response = getChatCompletionWithHistory(tempManager);

        // For true async compatibility, we could use executeAsync, but since
        // getChatCompletionWithHistory is already synchronous, we can return directly
        return response;
    }

    /**
     * Send message (legacy compatibility)
     */
    public String sendMessage(String userMessage) {
        if (context != null) {
            String systemPrompt = FineTunedModelManager.getInstance(context)
                    .getEnhancedSystemPromptForSelectedMaster();
            return getChatCompletion(systemPrompt, userMessage);
        } else {
            return getChatCompletion("You are a helpful chess coach.", userMessage);
        }
    }

    /**
     * Assistants API methods
     */
    private Request.Builder getAssistantsApiRequestBuilder() {
        return new Request.Builder()
                .addHeader("Authorization", getAuthorizationHeader())
                .addHeader("Content-Type", "application/json")
                .addHeader("OpenAI-Beta", "assistants=v2");
    }

    public String createAssistant(String requestBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/assistants")
                    .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating assistant", e);
            return null;
        }
    }

    public String createThread() {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads")
                    .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating thread", e);
            return null;
        }
    }

    public String createMessage(String threadId, String messageBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                    .post(RequestBody.create(MediaType.parse("application/json"), messageBody))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating message", e);
            return null;
        }
    }

    public String createRun(String threadId, String runBody) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/runs")
                    .post(RequestBody.create(MediaType.parse("application/json"), runBody))
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error creating run", e);
            return null;
        }
    }

    public String retrieveRun(String threadId, String runId) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving run", e);
            return null;
        }
    }

    public String listMessages(String threadId) {
        try {
            Request request = getAssistantsApiRequestBuilder()
                    .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "Error listing messages", e);
            return null;
        }
    }

    /**
     * Helper methods
     */
    private String getModelForRequest() {
        if (context == null) {
            Log.w(TAG, "Context not initialized, using default model");
            return model;
        }

        String selectedModel = FineTunedModelManager.getInstance(context).getSelectedModelId();
        Log.d(TAG, "Selected model: " + selectedModel);
        return selectedModel;
    }

    /**
     * Execute async with callback
     */
    public <T> void executeAsync(Callable<T> apiCall, ApiCallback<T> callback) {
        executorService.execute(() -> {
            try {
                T result = apiCall.call();
                if (callback != null) {
                    mainHandler.post(() -> callback.onSuccess(result));
                }
            } catch (Exception e) {
                Log.e(TAG, "API error: " + e.getMessage(), e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e));
                }
            }
        });
    }

    /**
     * Callback interfaces
     */
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface StreamingChatCallback {
        void onPartialResponse(String partialText, boolean isFirst);
        void onComplete(String fullResponse);
        void onError(Exception e);
    }

    /**
     * For backward compatibility with UnifiedOpenAIService
     */
    public static class OpenAICallback<T> {
        public void onSuccess(T result) {}
        public void onFailure(Exception e) {}
    }
}