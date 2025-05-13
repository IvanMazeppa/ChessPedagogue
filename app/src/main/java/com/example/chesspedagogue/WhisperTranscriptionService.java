package com.example.chesspedagogue;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WhisperTranscriptionService {
    private static final String TAG = "WhisperTranscription";
    private static final String WHISPER_API_URL = "https://api.openai.com/v1/audio/transcriptions";

    private final String apiKey;
    private final OkHttpClient client;

    public WhisperTranscriptionService(String apiKey) {
        this.apiKey = apiKey;

        // Configure OkHttpClient with longer timeouts for audio files
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String transcribeAudioFile(File audioFile) {
        try {
            Log.d(TAG, "Starting transcription of file: " + audioFile.getName());

            // Check if API key is valid before proceeding
            if (apiKey == null || apiKey.isEmpty()) {
                Log.e(TAG, "❌ API Key is empty! Cannot proceed with transcription.");
                return "Error: API key is not configured. Please check your OpenAI API key settings.";
            }

            Log.d(TAG, "Using API key with length: " + apiKey.length());

            // Create request body with audio file
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", audioFile.getName(),
                            RequestBody.create(MediaType.parse("audio/wav"), audioFile))
                    .addFormDataPart("model", "whisper-1")
                    .addFormDataPart("language", "en")
                    .addFormDataPart("response_format", "json")
                    .build();

            // Build request with API key in headers
            Request request = new Request.Builder()
                    .url(WHISPER_API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(requestBody)
                    .build();

            // Execute request
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API call failed with code: " + response.code());
                    Log.e(TAG, "Response: " + (response.body() != null ? response.body().string() : "null"));
                    return "Error transcribing audio. Please try again.";
                }

                // Parse the response JSON
                String responseBody = response.body().string();
                Log.d(TAG, "Received response: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                String transcription = jsonResponse.getString("text");

                Log.d(TAG, "Transcription successful: " + transcription);
                return transcription;
            }

        } catch (IOException e) {
            Log.e(TAG, "IO error during transcription", e);
            return "Error connecting to transcription service. Please check your internet connection.";
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error", e);
            return "Error processing transcription response.";
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during transcription", e);
            return "An unexpected error occurred. Please try again.";
        }

    }


}