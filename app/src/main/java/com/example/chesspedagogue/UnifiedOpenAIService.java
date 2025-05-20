package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
 * Unified service for all OpenAI API interactions including chat completions,
 * speech-to-text, and text-to-speech capabilities.
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
            // Update context if it was null initially
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

    // CHAT COMPLETION METHODS

    /**
     * Generate a chat completion using the conversation history
     */
    public void generateChatCompletion(List<ChatMessage> messages,
                                       OpenAICallback<String> callback) {
        executorService.execute(() -> {
            try {
                // Create request payload
                MediaType json = MediaType.parse("application/json; charset=utf-8");

                // Build the messages array
                StringBuilder messageJson = new StringBuilder("[");
                boolean first = true;

                for (ChatMessage msg : messages) {
                    if (!first) messageJson.append(",");
                    first = false;

                    messageJson.append("{\"role\":\"")
                            .append(msg.role)
                            .append("\",\"content\":\"")
                            .append(escapeJson(msg.content))
                            .append("\"}");
                }
                messageJson.append("]");

                String requestBody = "{"
                        + "\"model\": \"gpt-4.1-2025-04-14\","
                        + "\"messages\": " + messageJson.toString()
                        + "}";

                RequestBody body = RequestBody.create(requestBody, json);
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

    /**
     * Generate a chat completion with system prompt and user message
     * This simpler interface is perfect for quick interactions
     */

    public void generateChatResponse(String systemPrompt, String userMessage,
                                     OpenAICallback<String> callback) {
        // Create a simple message list
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", systemPrompt));
        messages.add(new ChatMessage("user", userMessage));

        // Use our existing method
        generateChatCompletion(messages, callback);
    }

    /**
     * Synchronous version for compatibility with existing code
     */
    public String generateChatResponseSync(String systemPrompt, String userMessage)
            throws IOException {
        // Create a way to wait for and return the result
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

        // Wait for result
        synchronized (lock) {
            try {
                lock.wait(30000); // Wait up to 30 seconds
            } catch (InterruptedException e) {
                throw new IOException("Chat completion interrupted", e);
            }
        }

        // Check for errors
        if (error[0] != null) {
            throw new IOException("Chat completion failed", error[0]);
        }

        return result[0];
    }

    // TEXT-TO-SPEECH METHODS


    // Add this method to UnifiedOpenAIService:

    /**
     * Generate a reply based on message history
     * This matches the OpenAIChatService interface for easy migration
     */
    public String generateReply(List<ChatMessage> messages) throws IOException {
        // Create synchronous wrapper around our async implementation
        final String[] result = new String[1];
        final Exception[] error = new Exception[1];
        final Object lock = new Object();

        // Call our async method
        generateChatCompletion(messages, new OpenAICallback<String>() {
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

        // Wait for result
        synchronized (lock) {
            try {
                lock.wait(30000); // Wait up to 30 seconds
            } catch (InterruptedException e) {
                throw new IOException("Chat completion interrupted", e);
            }
        }

        // Check for errors
        if (error[0] != null) {
            throw new IOException("Chat completion failed", error[0]);
        }

        return result[0];
    }

    // SPEECH-TO-TEXT METHODS

    /**
     * Transcribe audio to text
     */
    // Add or update this method in UnifiedOpenAIService.java
    public void transcribeAudio(byte[] audioData, OpenAICallback<String> callback) {
        executorService.execute(() -> {
            try {
                // Create multipart request with WAV data
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "audio.wav",
                                RequestBody.create(MediaType.parse("audio/wav"), audioData))
                        .addFormDataPart("model", "gpt-4o-mini-transcribe") // Update to new model
                        .addFormDataPart("language", "en")
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/audio/transcriptions")
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



    // HELPER METHODS

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractContentFromChatResponse(String json) {
        // Simple extraction - you might want to use proper JSON parsing
        int contentStart = json.indexOf("\"content\":\"") + 11;
        int contentEnd = json.indexOf("\"", contentStart);
        return json.substring(contentStart, contentEnd);
    }

    private String extractTextFromTranscriptionResponse(String json) {
        // Simple extraction - you might want to use proper JSON parsing
        int textStart = json.indexOf("\"text\":\"") + 8;
        int textEnd = json.indexOf("\"", textStart);
        return json.substring(textStart, textEnd);
    }

    // Add this method to UnifiedOpenAIService if not already present
    public void generateSpeech(String text, String voice, OpenAICallback<File> callback) {
        executorService.execute(() -> {
            try {
                // Create request payload
                MediaType json = MediaType.parse("application/json; charset=utf-8");

                String requestBody = "{"
                        + "\"model\": \"gpt-4o-mini-tts\","
                        + "\"input\": \"" + escapeJson(text) + "\","
                        + "\"voice\": \"" + voice + "\","
                        + "\"response_format\": \"mp3\""
                        + "}";

                RequestBody body = RequestBody.create(requestBody, json);
                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/audio/speech")
                        .header("Authorization", client.getAuthorizationHeader())
                        .post(body)
                        .build();

                Response response = client.executeRequest(request);

                if (response.isSuccessful() && response.body() != null) {
                    byte[] audioData = response.body().bytes();
                    File audioFile = saveAudioToFile(audioData);
                    mainHandler.post(() -> callback.onSuccess(audioFile));
                } else {
                    String errorMsg = "TTS API error: " + response.code();
                    mainHandler.post(() -> callback.onFailure(new IOException(errorMsg)));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    private File saveAudioToFile(byte[] audioData) throws IOException {
        File cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
        File audioFile = new File(cacheDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioData);
        }

        return audioFile;
    }

    // Add this method to UnifiedOpenAIService.java

    /**
     * Synchronous version of transcribeAudio for compatibility with existing code
     * This helps us transition gradually to the async pattern
     */
    public String transcribeAudioSync(byte[] audioData) throws IOException {
        // Create a simple way to wait for and return the result
        final String[] result = new String[1];
        final Exception[] error = new Exception[1];
        final Object lock = new Object();

        // Call our async version
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

        // Wait for result
        synchronized (lock) {
            try {
                lock.wait(30000); // Wait up to 30 seconds
            } catch (InterruptedException e) {
                throw new IOException("Transcription interrupted", e);
            }
        }

        // Check for errors
        if (error[0] != null) {
            throw new IOException("Transcription failed", error[0]);
        }

        // Return the result
        return result[0];
    }

    /**
     * Callback interface for OpenAI API operations
     */
    public interface OpenAICallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}