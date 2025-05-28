package com.example.chesspedagogue;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

/**
 * Activity for testing ElevenLabs emotional voice synthesis
 * Provides quick testing interface for different emotions and chess masters
 */
public class VoiceTestActivity extends AppCompatActivity {
    private static final String TAG = "VoiceTestActivity";
    
    private ElevenLabsEmotionalTester emotionalTester;
    private Spinner masterSpinner, emotionSpinner;
    private TextView statusText, currentTestText;
    private ListView resultsListView;
    private Button testButton, testAllButton;
    private ArrayAdapter<String> resultsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_test);
        
        initializeViews();
        setupSpinners();
        setupTester();
        setupListeners();
    }
    
    private void initializeViews() {
        masterSpinner = findViewById(R.id.masterSpinner);
        emotionSpinner = findViewById(R.id.emotionSpinner);
        statusText = findViewById(R.id.statusText);
        currentTestText = findViewById(R.id.currentTestText);
        resultsListView = findViewById(R.id.resultsListView);
        testButton = findViewById(R.id.testButton);
        testAllButton = findViewById(R.id.testAllButton);
        
        resultsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        resultsListView.setAdapter(resultsAdapter);
    }
    
    private void setupSpinners() {
        // Chess masters
        String[] masters = {"tal", "fischer", "carlsen", "kasparov", "karpov", "kramnik"};
        ArrayAdapter<String> masterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, masters);
        masterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        masterSpinner.setAdapter(masterAdapter);
        
        // Emotion types
        String[] emotions = {"excitement", "analytical", "frustration", "confidence", "concern", "playful", "intense", "disappointment"};
        ArrayAdapter<String> emotionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emotions);
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionSpinner.setAdapter(emotionAdapter);
    }
    
    private void setupTester() {
        emotionalTester = new ElevenLabsEmotionalTester(this);
    }
    
    private void setupListeners() {
        testButton.setOnClickListener(v -> {
            String master = masterSpinner.getSelectedItem().toString();
            String emotion = emotionSpinner.getSelectedItem().toString();
            testSingleEmotion(master, emotion);
        });
        
        testAllButton.setOnClickListener(v -> {
            String master = masterSpinner.getSelectedItem().toString();
            testAllEmotions(master);
        });
    }
    
    private void testSingleEmotion(String master, String emotion) {
        statusText.setText("Testing " + emotion + " for " + master + "...");
        testButton.setEnabled(false);
        
        String chessContext = getRandomChessContext(master);
        emotionalTester.quickEmotionalTest(master, emotion, chessContext);
        
        // Re-enable button after a delay
        statusText.postDelayed(() -> {
            statusText.setText("Ready for testing");
            testButton.setEnabled(true);
        }, 3000);
    }
    
    private void testAllEmotions(String master) {
        statusText.setText("Running comprehensive emotional test for " + master + "...");
        testAllButton.setEnabled(false);
        resultsAdapter.clear();
        
        emotionalTester.testEmotionalStatesForMaster(master, new ElevenLabsEmotionalTester.EmotionalTestCallback() {
            @Override
            public void onTestStarted(String masterName, String emotionType, String text) {
                runOnUiThread(() -> {
                    statusText.setText("Testing: " + emotionType);
                    currentTestText.setText(text);
                });
            }
            
            @Override
            public void onTestCompleted(String masterName, String emotionType, boolean success) {
                runOnUiThread(() -> {
                    String result = emotionType + ": " + (success ? "✅" : "❌");
                    resultsAdapter.add(result);
                    resultsAdapter.notifyDataSetChanged();
                });
            }
            
            @Override
            public void onAllTestsCompleted(List<String> results) {
                runOnUiThread(() -> {
                    statusText.setText("All tests completed for " + master);
                    currentTestText.setText("Testing finished!");
                    testAllButton.setEnabled(true);
                });
            }
        });
    }
    
    private String getRandomChessContext(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "this brilliant tactical sacrifice";
            case "fischer":
                return "the most precise continuation";
            case "carlsen":
                return "squeezing advantage from nothing";
            case "kasparov":
                return "this dynamic attacking move";
            default:
                return "this interesting chess position";
        }
    }
}