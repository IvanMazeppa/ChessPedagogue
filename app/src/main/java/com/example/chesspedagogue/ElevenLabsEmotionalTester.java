package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Testing framework for ElevenLabs emotional voice synthesis
 * Tests different emotional states and contexts for chess master voices
 * 
 * ElevenLabs uses contextual text cues to generate emotions:
 * - Descriptive text (e.g., "he said excitedly")
 * - Punctuation (!, ?, ...)
 * - Context words and narrative cues
 * - Emotional vocabulary
 */
public class ElevenLabsEmotionalTester {
    private static final String TAG = "ElevenLabsEmotionalTester";
    
    private final Context context;
    private final ElevenLabsTTSService ttsService;
    private final List<EmotionalTestCase> testCases;
    private final Random random = new Random();
    
    /**
     * Emotional test case with context and expected result
     */
    public static class EmotionalTestCase {
        public final String emotionType;
        public final String textTemplate;
        public final String description;
        public final String expectedResult;
        
        public EmotionalTestCase(String emotionType, String textTemplate, String description, String expectedResult) {
            this.emotionType = emotionType;
            this.textTemplate = textTemplate;
            this.description = description;
            this.expectedResult = expectedResult;
        }
        
        public String generateText(String chessContent) {
            return textTemplate.replace("{content}", chessContent);
        }
    }
    
    public interface EmotionalTestCallback {
        void onTestStarted(String masterName, String emotionType, String text);
        void onTestCompleted(String masterName, String emotionType, boolean success);
        void onAllTestsCompleted(List<String> results);
    }
    
    public ElevenLabsEmotionalTester(Context context) {
        this.context = context;
        this.ttsService = ElevenLabsTTSService.getInstance(context);
        this.testCases = initializeTestCases();
    }
    
    /**
     * Initialize comprehensive emotional test cases
     */
    private List<EmotionalTestCase> initializeTestCases() {
        List<EmotionalTestCase> cases = new ArrayList<>();
        
        // Excitement and enthusiasm
        cases.add(new EmotionalTestCase("excitement", 
            "{content}! This is absolutely brilliant!",
            "High excitement with exclamations",
            "Energetic, enthusiastic delivery"));
            
        cases.add(new EmotionalTestCase("excitement",
            "Oh my goodness, {content} - what a fantastic move!",
            "Surprised excitement",
            "Surprised and delighted tone"));
        
        // Analytical and calm
        cases.add(new EmotionalTestCase("analytical",
            "Let me think about this carefully. {content}. Yes, I see the logic here.",
            "Thoughtful analysis",
            "Measured, contemplative tone"));
            
        cases.add(new EmotionalTestCase("analytical",
            "From a strategic perspective, {content} makes perfect sense.",
            "Professional analysis",
            "Professional, confident delivery"));
        
        // Frustration and annoyance
        cases.add(new EmotionalTestCase("frustration",
            "Ugh, {content}... that's not what I was hoping for.",
            "Mild frustration",
            "Slightly irritated tone"));
            
        cases.add(new EmotionalTestCase("frustration",
            "This is frustrating! {content} just ruins everything I was planning.",
            "Strong frustration",
            "Clearly annoyed and upset"));
        
        // Confidence and authority
        cases.add(new EmotionalTestCase("confidence",
            "Of course! {content} - exactly as I predicted.",
            "Confident validation",
            "Self-assured, authoritative"));
            
        cases.add(new EmotionalTestCase("confidence",
            "Trust me, {content} is the only move here. I'm absolutely certain.",
            "Strong confidence",
            "Commanding, definitive tone"));
        
        // Concern and worry
        cases.add(new EmotionalTestCase("concern",
            "Hmm, I'm worried about {content}... this could be problematic.",
            "Growing concern",
            "Cautious, worried tone"));
            
        cases.add(new EmotionalTestCase("concern",
            "Oh no... {content} might be a serious mistake here.",
            "Alarm and worry",
            "Anxious, concerned delivery"));
        
        // Playful and humorous
        cases.add(new EmotionalTestCase("playful",
            "Ha! {content} - now that's what I call a cheeky move!",
            "Playful humor",
            "Light-hearted, amused tone"));
            
        cases.add(new EmotionalTestCase("playful",
            "Well, well, well... {content}. Someone's feeling adventurous today!",
            "Teasing playfulness",
            "Mischievous, playful delivery"));
        
        // Intense and dramatic
        cases.add(new EmotionalTestCase("intense",
            "This is it! {content} - the moment of truth!",
            "High drama",
            "Intense, dramatic emphasis"));
            
        cases.add(new EmotionalTestCase("intense",
            "The tension is unbearable... {content} could change everything!",
            "Building tension",
            "Suspenseful, gripping tone"));
        
        // Disappointment
        cases.add(new EmotionalTestCase("disappointment",
            "Sigh... {content}. I really thought this would work out differently.",
            "Resigned disappointment",
            "Deflated, disappointed tone"));
            
        cases.add(new EmotionalTestCase("disappointment",
            "That's disappointing. {content} wasn't what I was expecting at all.",
            "Clear disappointment",
            "Clearly let down and upset"));
        
        return cases;
    }
    
    /**
     * Test all emotional states for a specific chess master
     */
    public void testEmotionalStatesForMaster(String masterName, EmotionalTestCallback callback) {
        Log.d(TAG, "🎭 Starting emotional state testing for " + masterName);
        
        List<String> chessContexts = getChessContextsForMaster(masterName);
        List<String> results = new ArrayList<>();
        
        for (EmotionalTestCase testCase : testCases) {
            String chessContent = chessContexts.get(random.nextInt(chessContexts.size()));
            String testText = testCase.generateText(chessContent);
            
            Log.d(TAG, "🧪 Testing " + testCase.emotionType + " for " + masterName);
            Log.d(TAG, "📝 Text: " + testText);
            
            if (callback != null) {
                callback.onTestStarted(masterName, testCase.emotionType, testText);
            }
            
            // Test with TTS
            testEmotionalTTS(masterName, testCase, testText, (success) -> {
                results.add(String.format("%s - %s: %s", 
                    masterName, testCase.emotionType, success ? "SUCCESS" : "FAILED"));
                
                if (callback != null) {
                    callback.onTestCompleted(masterName, testCase.emotionType, success);
                }
                
                // Check if all tests completed
                if (results.size() >= testCases.size()) {
                    if (callback != null) {
                        callback.onAllTestsCompleted(results);
                    }
                }
            });
            
            // Small delay between tests
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Test specific emotional state with TTS
     */
    private void testEmotionalTTS(String masterName, EmotionalTestCase testCase, String text, TestResultCallback resultCallback) {
        // Set the chess master for voice selection
        setCurrentMaster(masterName);
        
        ttsService.speak(text, new ElevenLabsTTSService.OnSpeechCompletedListener() {
            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "✅ Emotional test completed: " + testCase.emotionType);
                if (resultCallback != null) {
                    resultCallback.onResult(true);
                }
            }
        });
    }
    
    /**
     * Get chess-specific content for each master
     */
    private List<String> getChessContextsForMaster(String masterName) {
        List<String> contexts = new ArrayList<>();
        
        switch (masterName.toLowerCase()) {
            case "tal":
                contexts.add("that brilliant knight sacrifice on f7");
                contexts.add("this beautiful tactical sequence");
                contexts.add("the stunning queen sacrifice");
                contexts.add("this creative attacking move");
                break;
                
            case "fischer":
                contexts.add("this precisely calculated continuation");
                contexts.add("the objectively best move here");
                contexts.add("this theoretically sound approach");
                contexts.add("the most accurate line");
                break;
                
            case "carlsen":
                contexts.add("this practical endgame approach");
                contexts.add("squeezing water from a stone");
                contexts.add("finding resources in this position");
                contexts.add("the modern way to handle this");
                break;
                
            case "kasparov":
                contexts.add("seizing the initiative with this move");
                contexts.add("this dynamic breakthrough");
                contexts.add("the aggressive continuation");
                contexts.add("fighting for every advantage");
                break;
                
            default:
                contexts.add("this interesting chess move");
                contexts.add("the strategic decision here");
                contexts.add("this positional choice");
                contexts.add("the tactical opportunity");
                break;
        }
        
        return contexts;
    }
    
    /**
     * Set current master for voice testing
     */
    private void setCurrentMaster(String masterName) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("selected_master", masterName.toLowerCase()).apply();
    }
    
    /**
     * Test accent variations for foreign language voices speaking English
     */
    public void testAccentVariations(String masterName, EmotionalTestCallback callback) {
        List<String> accentTests = new ArrayList<>();
        
        switch (masterName.toLowerCase()) {
            case "tal":
                accentTests.add("In Latvia, we say chess is poetry in motion, you know?");
                accentTests.add("My dear friend, this reminds me of games in Riga.");
                break;
                
            case "carlsen":
                accentTests.add("In Norway, we call this 'praktisk sjakkspill' - practical chess.");
                accentTests.add("This is how we play in Scandinavia, very solid approach.");
                break;
                
            case "fischer":
                accentTests.add("Listen, in America we don't mess around with second-rate moves.");
                accentTests.add("That's not how you play serious chess, period.");
                break;
        }
        
        for (String text : accentTests) {
            Log.d(TAG, "🌍 Testing accent for " + masterName + ": " + text);
            setCurrentMaster(masterName);
            
            if (callback != null) {
                callback.onTestStarted(masterName, "accent", text);
            }
            
            ttsService.speak(text, new ElevenLabsTTSService.OnSpeechCompletedListener() {
                @Override
                public void onSpeechCompleted() {
                    if (callback != null) {
                        callback.onTestCompleted(masterName, "accent", true);
                    }
                }
            });
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Quick emotional test with single phrase
     */
    public void quickEmotionalTest(String masterName, String emotion, String chessContext) {
        EmotionalTestCase testCase = findTestCaseForEmotion(emotion);
        if (testCase != null) {
            String text = testCase.generateText(chessContext);
            Log.d(TAG, "🚀 Quick test: " + masterName + " - " + emotion);
            Log.d(TAG, "📝 " + text);
            
            setCurrentMaster(masterName);
            ttsService.speak(text, () -> Log.d(TAG, "✅ Quick test completed"));
        }
    }
    
    private EmotionalTestCase findTestCaseForEmotion(String emotion) {
        for (EmotionalTestCase testCase : testCases) {
            if (testCase.emotionType.equalsIgnoreCase(emotion)) {
                return testCase;
            }
        }
        return testCases.get(0); // Default fallback
    }
    
    private interface TestResultCallback {
        void onResult(boolean success);
    }
}