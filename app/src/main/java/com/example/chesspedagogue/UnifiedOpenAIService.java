package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Enhanced unified service with streaming support for ultra-low latency responses
 */
public class UnifiedOpenAIService {
    private static final String TAG = "UnifiedOpenAIService";
    private static UnifiedOpenAIService instance;

    // Endpoints
    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    private static final String TRANSCRIBE_URL = "https://api.openai.com/v1/audio/transcriptions";

    // Context and handlers
    private Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;

    // Client using our shared implementation
    private final OpenAIClient client;

    // Private constructor
    private UnifiedOpenAIService(Context context) {
        this.context = context != null ? context.getApplicationContext() : null;
        this.client = OpenAIClient.getInstance();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();
    }

    // Singleton access
    public static synchronized UnifiedOpenAIService getInstance(Context context) {
        if (instance == null) {
            instance = new UnifiedOpenAIService(context);
        } else if (context != null && instance.context == null) {
            instance.context = context.getApplicationContext();
        }
        return instance;
    }

    /**
     * Set the API key for all OpenAI services
     */
    public void setApiKey(String apiKey) {
        client.setApiKey(apiKey);
    }

    /**
     * NEW: Streaming chat completion for ultra-low latency
     * This starts processing and speaking as soon as we get partial responses
     */
    public void generateStreamingChatResponse(String systemPrompt, String userMessage,
                                              StreamingChatCallback callback) {
        executorService.execute(() -> {
            try {
                // Create request payload with streaming enabled
                JSONObject requestJson = new JSONObject();
                requestJson.put("model", "gpt-4.1-2025-04-14");
                requestJson.put("stream", true); // Enable streaming!

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

                MediaType json = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(requestJson.toString(), json);

                Request request = new Request.Builder()
                        .url(CHAT_URL)
                        .header("Authorization", client.getAuthorizationHeader())
                        .post(body)
                        .build();

                Response response = client.executeRequest(request);

                if (response.isSuccessful() && response.body() != null) {
                    // Process streaming response
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

                                        // Send first chunk ASAP (even if very small)
                                        if (!firstChunkSent && currentChunk.length() > 20) {
                                            String firstChunk = currentChunk.toString();
                                            mainHandler.post(() -> callback.onPartialResponse(firstChunk, true));
                                            currentChunk.setLength(0);
                                            firstChunkSent = true;
                                        }
                                        // Send subsequent chunks when we have enough content
                                        else if (firstChunkSent && (currentChunk.length() > 60 || content.contains(".") || content.contains("!"))) {
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
     * Enhanced transcribe audio with better error handling
     */
    public void transcribeAudio(byte[] audioData, OpenAICallback<String> callback) {
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
                        .header("Authorization", client.getAuthorizationHeader())
                        .post(requestBody)
                        .build();

                Response response = client.executeRequest(request);

                if (response.isSuccessful() && response.body() != null) {
                    String responseJson = response.body().string();
                    String transcribedText = extractTextFromTranscriptionResponse(responseJson);
                    mainHandler.post(() -> callback.onSuccess(transcribedText));
                } else {
                    String errorMsg = "Transcription API error: " + response.code();
                    mainHandler.post(() -> callback.onFailure(new IOException(errorMsg)));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    /**
     * Synchronous version for backward compatibility
     */
    public String transcribeAudioSync(byte[] audioData) throws IOException {
        final String[] result = new String[1];
        final Exception[] error = new Exception[1];
        final Object lock = new Object();

        transcribeAudio(audioData, new OpenAICallback<String>() {
            @Override
            public void onSuccess(String text) {
                synchronized (lock) {
                    result[0] = text;
                    lock.notify();
                }
            }

            @Override
            public void onFailure(Exception e) {
                synchronized (lock) {
                    error[0] = e;
                    lock.notify();
                }
            }
        });

        synchronized (lock) {
            try {
                lock.wait(30000);
            } catch (InterruptedException e) {
                throw new IOException("Transcription interrupted", e);
            }
        }

        if (error[0] != null) {
            throw new IOException("Transcription failed", error[0]);
        }

        return result[0];
    }

    /**
     * Legacy methods for backward compatibility
     */
    public void generateChatCompletion(List<ChatMessage> messages, OpenAICallback<String> callback) {
        if (messages.size() < 2) {
            callback.onFailure(new IllegalArgumentException("Need at least system and user message"));
            return;
        }

        String systemPrompt = messages.get(0).content;
        String userMessage = messages.get(messages.size() - 1).content;

        generateChatResponse(systemPrompt, userMessage, callback);
    }

    public void generateChatResponse(String systemPrompt, String userMessage, OpenAICallback<String> callback) {
        executorService.execute(() -> {
            try {
                JSONObject requestJson = new JSONObject();
                requestJson.put("model", "gpt-4.1-2025-04-14");

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

                MediaType json = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(requestJson.toString(), json);

                Request request = new Request.Builder()
                        .url(CHAT_URL)
                        .header("Authorization", client.getAuthorizationHeader())
                        .post(body)
                        .build();

                Response response = client.executeRequest(request);

                if (response.isSuccessful() && response.body() != null) {
                    String responseJson = response.body().string();
                    String content = extractContentFromChatResponse(responseJson);
                    mainHandler.post(() -> callback.onSuccess(content));
                } else {
                    String errorMsg = "API error: " + response.code();
                    mainHandler.post(() -> callback.onFailure(new IOException(errorMsg)));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    public String generateChatResponseSync(String systemPrompt, String userMessage) throws IOException {
        final String[] result = new String[1];
        final Exception[] error = new Exception[1];
        final Object lock = new Object();

        generateChatResponse(systemPrompt, userMessage, new OpenAICallback<String>() {
            @Override
            public void onSuccess(String response) {
                synchronized (lock) {
                    result[0] = response;
                    lock.notify();
                }
            }

            @Override
            public void onFailure(Exception e) {
                synchronized (lock) {
                    error[0] = e;
                    lock.notify();
                }
            }
        });

        synchronized (lock) {
            try {
                lock.wait(30000);
            } catch (InterruptedException e) {
                throw new IOException("Chat completion interrupted", e);
            }
        }

        if (error[0] != null) {
            throw new IOException("Chat completion failed", error[0]);
        }

        return result[0];
    }

    // Helper methods
    private String extractContentFromChatResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            JSONArray choices = response.getJSONArray("choices");
            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            return message.getString("content");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing chat response", e);
            return "Error parsing response";
        }
    }

    private String extractTextFromTranscriptionResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            return response.getString("text");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing transcription response", e);
            return "Error parsing transcription";
        }
    }

    /**
     * Callback interfaces
     */
    public interface OpenAICallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface StreamingChatCallback {
        void onPartialResponse(String partialText, boolean isFirst);
        void onComplete(String fullResponse);
        void onError(Exception e);
    }
}