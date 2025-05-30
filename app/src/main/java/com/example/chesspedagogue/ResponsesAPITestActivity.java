package com.example.chesspedagogue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Test activity to demonstrate and validate Responses API integration
 */
public class ResponsesAPITestActivity extends AppCompatActivity {
    private static final String TAG = "ResponsesAPITest";
    
    private ChessMasterResponsesManager responsesManager;
    private ResponsesAPIIntegrationHelper integrationHelper;
    private TextView statusText;
    private TextView responseText;
    private Button testTalButton;
    private Button testFischerButton;
    private Button testCarlsenButton;
    private Button toggleApiButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses_api_test);
        
        // Initialize components
        responsesManager = ChessMasterResponsesManager.getInstance(this);
        integrationHelper = ResponsesAPIIntegrationHelper.getInstance(this);
        
        // Initialize views
        initializeViews();
        
        // Update status
        updateStatus();
    }
    
    private void initializeViews() {
        statusText = findViewById(R.id.statusText);
        responseText = findViewById(R.id.responseText);
        testTalButton = findViewById(R.id.testTalButton);
        testFischerButton = findViewById(R.id.testFischerButton);
        testCarlsenButton = findViewById(R.id.testCarlsenButton);
        toggleApiButton = findViewById(R.id.toggleApiButton);
        
        // Set click listeners
        testTalButton.setOnClickListener(v -> testMaster("tal"));
        testFischerButton.setOnClickListener(v -> testMaster("fischer"));
        testCarlsenButton.setOnClickListener(v -> testMaster("carlsen"));
        
        toggleApiButton.setOnClickListener(v -> {
            ResponsesAPIIntegrationHelper.MigrationStatus status = integrationHelper.getMigrationStatus();
            integrationHelper.setUseResponsesAPI(!status.responsesAPIEnabled);
            updateStatus();
        });
    }
    
    private void testMaster(String masterName) {
        Log.d(TAG, "Testing " + masterName + " with Responses API");
        
        responseText.setText("Generating response from " + masterName + "...");
        
        // Test with a simple chess position comment
        String testPrompt = "You just played e4. Comment on your opening choice.";
        
        if (integrationHelper.shouldUseResponsesAPI(masterName)) {
            // Use Responses API
            testResponsesAPI(masterName, testPrompt);
        } else {
            // Fallback to Chat Completions
            testChatCompletions(masterName, testPrompt);
        }
    }
    
    private void testResponsesAPI(String masterName, String prompt) {
        responsesManager.createResponseSession(masterName, "test_game", 
            new ChessMasterResponsesManager.ResponseCallback() {
                private StringBuilder response = new StringBuilder();
                
                @Override
                public void onResponseStart(String sessionId) {
                    runOnUiThread(() -> {
                        responseText.append("\n[Session started: " + sessionId + "]\n");
                    });
                }
                
                @Override
                public void onResponseChunk(String chunk, boolean isFirst) {
                    response.append(chunk);
                    runOnUiThread(() -> {
                        responseText.setText(response.toString());
                    });
                }
                
                @Override
                public void onResponseComplete(String fullResponse) {
                    runOnUiThread(() -> {
                        responseText.setText(masterName.toUpperCase() + " (via Responses API):\n\n" + fullResponse);
                        Toast.makeText(ResponsesAPITestActivity.this, 
                            "Response complete!", Toast.LENGTH_SHORT).show();
                    });
                }
                
                @Override
                public void onConversationTurn(String speaker, String message) {
                    Log.d(TAG, "Conversation turn: " + speaker + " - " + message);
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        responseText.setText("Error: " + error);
                        Toast.makeText(ResponsesAPITestActivity.this, 
                            "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        
        // Send the test message
        responsesManager.sendMessage("session_test", prompt, "opening", null);
    }
    
    private void testChatCompletions(String masterName, String prompt) {
        OpenAIService openAIService = OpenAIService.getInstance();
        openAIService.selectChessMaster(masterName);
        
        responseText.setText("Using Chat Completions fallback for " + masterName + "...");
        
        // Run on background thread
        new Thread(() -> {
            try {
                String response = openAIService.getChatCompletion(
                    "You are " + masterName + " playing chess. Be authentic to your personality.",
                    prompt
                );
                
                runOnUiThread(() -> {
                    responseText.setText(masterName.toUpperCase() + " (via Chat Completions):\n\n" + response);
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    responseText.setText("Error: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void updateStatus() {
        ResponsesAPIIntegrationHelper.MigrationStatus status = integrationHelper.getMigrationStatus();
        
        String statusMsg = "API Status:\n" +
                          "Responses API: " + (status.responsesAPIEnabled ? "ENABLED" : "DISABLED") + "\n" +
                          "Masters with Assistants: " + status.mastersWithAssistants + "/" + status.totalMasters + "\n" +
                          "Masters using API: " + status.mastersUsingResponsesAPI + "\n\n" +
                          "Tal: " + (integrationHelper.shouldUseResponsesAPI("tal") ? "✓" : "✗") + "\n" +
                          "Fischer: " + (integrationHelper.shouldUseResponsesAPI("fischer") ? "✓" : "✗") + "\n" +
                          "Carlsen: " + (integrationHelper.shouldUseResponsesAPI("carlsen") ? "✓" : "✗");
        
        statusText.setText(statusMsg);
        
        toggleApiButton.setText(status.responsesAPIEnabled ? "Disable Responses API" : "Enable Responses API");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        responsesManager.cleanup();
    }
}