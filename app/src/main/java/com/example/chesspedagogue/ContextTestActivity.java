package com.example.chesspedagogue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

/**
 * Test activity to verify enhanced context management and emergent behavior
 */
public class ContextTestActivity extends AppCompatActivity {
    private static final String TAG = "ContextTest";
    
    private EnhancedContextManager contextManager;
    private AIDialogueManager dialogueManager;
    private TextView statusText;
    private TextView contextSizeText;
    private Button testButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple UI setup
        setContentView(createTestLayout());
        
        // Initialize managers
        contextManager = EnhancedContextManager.getInstance();
        dialogueManager = new AIDialogueManager(this);
        
        testButton.setOnClickListener(v -> runContextTest());
    }
    
    private View createTestLayout() {
        // Create a simple vertical layout programmatically
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        
        statusText = new TextView(this);
        statusText.setText("Context Test Ready");
        statusText.setTextSize(16);
        layout.addView(statusText);
        
        contextSizeText = new TextView(this);
        contextSizeText.setText("Context size: 0");
        contextSizeText.setTextSize(14);
        layout.addView(contextSizeText);
        
        testButton = new Button(this);
        testButton.setText("Run Context Test");
        layout.addView(testButton);
        
        return layout;
    }
    
    private void runContextTest() {
        Log.d(TAG, "=== CONTEXT TEST STARTED ===");
        statusText.setText("Running test...");
        
        // Test 1: Add some conversation turns
        contextManager.resetConversation();
        
        // Simulate an opening conversation
        contextManager.addConversationTurn("tal", "Ah, Fischer! Ready for another battle?", "opening");
        contextManager.addConversationTurn("fischer", "I'm always ready, Misha. Let's see if your sacrifices work today.", "opening");
        contextManager.addConversationTurn("tal", "You know me too well, Bobby! But chess is art, not just calculation.", "opening");
        
        // Test 2: Check context generation
        List<Map<String, String>> context = contextManager.getContextForApiCall("fischer", "tal");
        
        Log.d(TAG, "Generated context with " + context.size() + " messages:");
        for (Map<String, String> msg : context) {
            Log.d(TAG, "  Role: " + msg.get("role") + ", Content preview: " + 
                  msg.get("content").substring(0, Math.min(50, msg.get("content").length())) + "...");
        }
        
        // Test 3: Add more turns to trigger summarization
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                contextManager.addConversationTurn("tal", 
                    "Move " + i + ": This position has beautiful tactical possibilities!", "midgame");
            } else {
                contextManager.addConversationTurn("fischer", 
                    "Move " + i + ": Precision is what matters, not beauty.", "midgame");
            }
        }
        
        // Test 4: Check if summarization happened
        int conversationSize = contextManager.getConversationSize();
        int relationshipMemories = contextManager.getRelationshipMemoryCount();
        
        Log.d(TAG, "After many turns:");
        Log.d(TAG, "  Conversation size: " + conversationSize);
        Log.d(TAG, "  Relationship memories: " + relationshipMemories);
        
        // Test 5: Generate a response with full context
        dialogueManager.generateMoveDialogueWithEmotion("e4", "tal", "tal", "fischer", 
            20, 0.5f, new AIDialogueManager.DialogueCallback() {
                @Override
                public void onDialogueGenerated(String speaker, String dialogue) {
                    Log.d(TAG, "Generated dialogue: " + dialogue);
                    statusText.setText("Test completed! Check logs.");
                    contextSizeText.setText("Context: " + conversationSize + " turns, " + 
                                          relationshipMemories + " memories");
                }
                
                @Override
                public void onConversationStarted(String respondingSpeaker, String triggerStatement) {
                    Log.d(TAG, "Conversation started: " + respondingSpeaker);
                }
                
                @Override
                public void onConversationComplete(String finalSpeaker, String finalStatement) {
                    Log.d(TAG, "Conversation complete: " + finalSpeaker);
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error: " + error);
                    statusText.setText("Error: " + error);
                }
            });
        
        Log.d(TAG, "=== CONTEXT TEST COMPLETED ===");
    }
}