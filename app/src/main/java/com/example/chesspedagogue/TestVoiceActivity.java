package com.example.chesspedagogue;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chesspedagogue.ChessVoiceConversationHandler;
import com.example.chesspedagogue.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// In a new TestVoiceActivity.java file
public class TestVoiceActivity extends AppCompatActivity {
    private Button testButton;
    private TextView statusText;
    private ChessVoiceConversationHandler conversationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_voice); // Create a simple layout with a button

        testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(v -> {
            statusText.setText("Testing voice with gpt-4o-mini-tts...");

            String apiKey = ApiKeyConfig.getApiKey(this);
            if (apiKey == null || apiKey.isEmpty()) {
                statusText.setText("API key not set. Please configure in settings.");
                return;
            }

            // Create a background thread for the API call
            new Thread(() -> {
                try {
                    // Create the JSON payload
                    JSONObject payload = new JSONObject();
                    payload.put("model", "gpt-4o-mini-tts");
                    payload.put("input", "Hello from Coach Tal. I'm testing my voice system.");
                    payload.put("voice", "ash");
                    payload.put("instructions", "Speak like you're very old and have a Latvian accent");


                    // Try with no voice parameter first since it might be integrated
                    // If this fails, we can try adding "voice": "onyx" later

                    // Log the request details for debugging
                    String jsonPayload = payload.toString();
                    Log.d("TTS_TEST", "Sending payload: " + jsonPayload);

                    // Create request
                    RequestBody body = RequestBody.create(
                            MediaType.parse("application/json"),
                            payload.toString());

                    Request request = new Request.Builder()
                            .url("https://api.openai.com/v1/audio/speech")
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Content-Type", "application/json")
                            .post(body)
                            .build();

                    // Update UI
                    runOnUiThread(() -> statusText.setText("Sending TTS request..."));

                    // Make the API call
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> {
                            statusText.setText("TTS API Error: " + response.code());
                            if (response.body() != null) {
                                try {
                                    Log.e("TTS_TEST", "Error response: " + response.body().string());
                                } catch (Exception e) {
                                    Log.e("TTS_TEST", "Error reading response body", e);
                                }
                            }
                        });
                        return;
                    }

                    // Success! Process the audio data
                    byte[] audioData = response.body().bytes();

                    runOnUiThread(() -> {
                        statusText.setText("Playing audio response...");
                        // Play the audio
                        playAudio(audioData);
                    });

                } catch (Exception e) {
                    Log.e("TTS_TEST", "Error in TTS test", e);
                    runOnUiThread(() -> {
                        statusText.setText("Error: " + e.getMessage());
                    });
                }
            }).start();
        });
        statusText = findViewById(R.id.statusText);

        // Initialize the handler
        conversationHandler = new ChessVoiceConversationHandler(this);

        // Set callbacks
        conversationHandler.setConversationCallback(new ChessVoiceConversationHandler.ConversationCallback() {
            @Override
            public void onListeningStarted() {
                statusText.setText("Listening...");
            }

            @Override
            public void onSpeechTranscribed(String text) {
                statusText.setText("Heard: " + text);
            }

            // Implement other required callbacks...
            @Override
            public void onProcessingStarted() {
                statusText.setText("Processing...");
            }

            @Override
            public void onResponseReady(String response) {
                statusText.setText("Response ready");
            }

            @Override
            public void onSpeakingStarted() {
                statusText.setText("Coach speaking...");
            }

            @Override
            public void onSpeakingCompleted() {
                statusText.setText("Done speaking");
            }

            @Override
            public void onError(String error) {
                statusText.setText("Error: " + error);
            }
        });

    }

    private void playAudio(byte[] audioData) {
        try {
            // Save audio to a temporary file
            File tempFile = File.createTempFile("tts_test", ".pcm", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(audioData);
            fos.close();

            // Play the audio with MediaPlayer
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(tempFile.getPath());
            player.prepare();
            player.start();

            player.setOnCompletionListener(mp -> {
                mp.release();
                tempFile.delete();
                statusText.setText("Audio playback completed!");
            });
        } catch (Exception e) {
            statusText.setText("Audio playback error: " + e.getMessage());
        }
    }
}