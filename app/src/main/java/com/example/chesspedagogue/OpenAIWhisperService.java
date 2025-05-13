package com.example.chesspedagogue;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of SpeechToTextService using OpenAI's Whisper API.
 */
public class OpenAIWhisperService implements SpeechToTextService {
    private static final String TRANSCRIBE_URL = "https://api.openai.com/v1/audio/transcriptions";
    private final OkHttpClient httpClient;
    private final String apiKey;

    private static final String TAG = "OpenAIWhisperService";

    public OpenAIWhisperService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }


    @Override
    public String transcribeAudio(byte[] audioData) throws IOException {
        // Log the request attempt
        Log.d(TAG, "Attempting to transcribe " + audioData.length + " bytes of audio");

        try {
            // Convert PCM audio to WAV if needed
            byte[] wavData = pcmToWav(audioData, 16000, 1, 16);
            Log.d(TAG, "Converted to WAV format: " + wavData.length + " bytes");

            // Build the multipart request with the audio file
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "audio.wav",
                            RequestBody.create(MediaType.parse("audio/wav"), wavData))
                    .addFormDataPart("model", "whisper-1")
                    .addFormDataPart("language", "en") // Optimized for English
                    .build();

            // Create the request
            Request request = new Request.Builder()
                    .url(TRANSCRIBE_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(requestBody)
                    .build();

            // Log that we're sending the request
            Log.d(TAG, "Sending transcription request to OpenAI API");

            // Execute the request
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    return "Error: " + response.code();
                }

                String responseJson = response.body().string();
                Log.d(TAG, "Received response: " + responseJson);

                // Parse JSON response
                JSONObject json = new JSONObject(responseJson);
                String transcribedText = json.optString("text", "");

                Log.d(TAG, "Transcription successful: " + transcribedText);
                return transcribedText;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in transcription process", e);
            throw new IOException("Transcription failed: " + e.getMessage(), e);
        }
    }

    // Utility: Convert raw PCM audio data to WAV format bytes
    private byte[] pcmToWav(byte[] pcmData, int sampleRate, int channels, int bitsPerSample) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // WAV header fields
        int dataLength = pcmData.length;
        int riffChunkSize = 36 + dataLength;
        int byteRate = sampleRate * channels * (bitsPerSample / 8);
        short blockAlign = (short) (channels * (bitsPerSample / 8));

        // Write RIFF header
        out.write(new byte[]{ 'R','I','F','F' });
        out.write(intToLE(riffChunkSize));
        out.write(new byte[]{ 'W','A','V','E' });

        // fmt subchunk
        out.write(new byte[]{ 'f','m','t',' ' });
        out.write(intToLE(16)); // PCM format chunk size
        out.write(shortToLE((short)1)); // audio format 1 = PCM
        out.write(shortToLE((short)channels));
        out.write(intToLE(sampleRate));
        out.write(intToLE(byteRate));
        out.write(shortToLE(blockAlign));
        out.write(shortToLE((short)bitsPerSample));

        // data subchunk
        out.write(new byte[]{ 'd','a','t','a' });
        out.write(intToLE(dataLength));
        out.write(pcmData);

        return out.toByteArray();
    }

    private byte[] intToLE(int value) {
        return new byte[]{
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF),
                (byte)((value >> 16) & 0xFF),
                (byte)((value >> 24) & 0xFF)
        };
    }

    private byte[] shortToLE(short value) {
        return new byte[]{
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF)
        };
    }
}