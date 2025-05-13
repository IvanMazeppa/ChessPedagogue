package com.example.chesspedagogue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Activity for testing the voice integration components separately.
 * This makes debugging easier before integrating with the main chess app.
 */
public class VoiceTestActivity extends AppCompatActivity {
    private static final String TAG = "VoiceTest";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;

    private Button testApiButton;
    private Button voiceButton;
    private Button conversationButton;
    private TextView statusText;
    private TextView transcriptText;

    private boolean conversationActive = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_test);

        // Initialize views
        testApiButton = findViewById(R.id.testApiButton);
        voiceButton = findViewById(R.id.voiceButton);
        conversationButton = findViewById(R.id.conversationButton);
        statusText = findViewById(R.id.statusText);
        transcriptText = findViewById(R.id.transcriptText);

        // Set up scrolling for the transcript
        transcriptText.setMovementMethod(new ScrollingMovementMethod());

        // Make sure the API key is set (for testing purposes only - store securely in production)
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "YOUR_API_KEY"; // For testing only
            ApiKeyConfig.saveApiKey(this, apiKey);
        }

        // Set up listeners
        testApiButton.setOnClickListener(v -> testApiConnection());
        voiceButton.setOnClickListener(v -> testVoice());
        conversationButton.setOnClickListener(v -> toggleConversation());

        // Check permissions
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showStatus("Microphone permission granted!");
            } else {
                showStatus("Microphone permission denied. Voice features will not work.");
            }
        }
    }

    private void testApiConnection() {
        showStatus("Testing API connection...");

        ApiTester.testApiKey(this, (success, message) -> {
            showStatus(message);
            if (success) {
                appendTranscript("✓ API connection successful!");
            } else {
                appendTranscript("✗ API connection failed: " + message);
            }
        });
    }

    private void testVoice() {
        showStatus("Testing voice...");

        ChessCoachManager coachManager = ChessCoachManager.getInstance(this);
        coachManager.testVoice("Hello, I'm your chess coach. I'm ready to help you improve your game!",
                new ChessCoachManager.ChessCoachCallback() {
                    @Override
                    public void onResponseReceived(String response) {
                        // Not used for voice test
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showStatus("Voice test failed: " + errorMessage);
                        appendTranscript("✗ Voice test error: " + errorMessage);
                    }

                    @Override
                    public void onSpeechCompleted() {
                        showStatus("Voice test completed!");
                        appendTranscript("✓ Voice playback successful!");
                    }
                });
    }

    private void toggleConversation() {
        if (!conversationActive) {
            startConversation();
        } else {
            stopConversation();
        }
    }

    private void startConversation() {
        showStatus("Starting conversation...");

        // Check permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            showStatus("Microphone permission required for conversation");
            checkAndRequestPermissions();
            return;
        }

        // Start the voice service
        Intent serviceIntent = new Intent(this, VoiceService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        conversationActive = true;
        conversationButton.setText("Stop Conversation");

        appendTranscript("Started conversation service - I'm listening for your voice...");
    }

    private void stopConversation() {
        showStatus("Stopping conversation...");

        // Stop the voice service
        Intent serviceIntent = new Intent(this, VoiceService.class);
        stopService(serviceIntent);

        conversationActive = false;
        conversationButton.setText("Start Conversation");

        appendTranscript("Stopped conversation service");
    }

    private void showStatus(String message) {
        handler.post(() -> statusText.setText(message));
    }

    private void appendTranscript(String message) {
        handler.post(() -> {
            transcriptText.append(message + "\n\n");

            // Scroll to the bottom
            final int scrollAmount = transcriptText.getLayout().getLineTop(transcriptText.getLineCount())
                    - transcriptText.getHeight();
            if (scrollAmount > 0) {
                transcriptText.scrollTo(0, scrollAmount);
            } else {
                transcriptText.scrollTo(0, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop conversation if it's active
        if (conversationActive) {
            stopConversation();
        }
    }
}