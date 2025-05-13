package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EnhancedConversationManager {
    private static final String TAG = "EnhancedConversationManager";
    private static EnhancedConversationManager instance;
    private final Context context;
    private final List<ConversationManager.Message> activeConversation = new ArrayList<>();
    private final ConversationStorage storage;
    private String currentSessionId;

    // Private constructor for singleton
    private EnhancedConversationManager(Context context) {
        this.context = context.getApplicationContext();
        this.storage = new ConversationStorage(context);
        this.currentSessionId = generateSessionId();
        Log.d(TAG, "EnhancedConversationManager initialized with session: " + currentSessionId);
    }

    // Get singleton instance
    public static synchronized EnhancedConversationManager getInstance(Context context) {
        if (instance == null) {
            instance = new EnhancedConversationManager(context);
        }
        return instance;
    }

    // Add a message to the conversation and save it
    public void addMessage(String role, String content) {
        ConversationManager.Message message = new ConversationManager.Message(role, content);
        activeConversation.add(message);
        Log.d(TAG, "Added message with role: " + role + ", length: " + (content != null ? content.length() : 0));
    }

    // Get the current conversation history
    public List<ConversationManager.Message> getConversationHistory() {
        return new ArrayList<>(activeConversation);
    }

    // Save the current conversation to storage
    public void saveCurrentConversation() {
        storage.saveConversation(currentSessionId, activeConversation);
        Log.d(TAG, "Saved conversation with " + activeConversation.size() + " messages");
    }

    // Resume a previous conversation by ID
    public void resumeConversation(String sessionId) {
        List<ConversationManager.Message> savedConversation = storage.loadConversation(sessionId);
        if (savedConversation != null && !savedConversation.isEmpty()) {
            activeConversation.clear();
            activeConversation.addAll(savedConversation);
            currentSessionId = sessionId;
            Log.d(TAG, "Resumed conversation with " + savedConversation.size() + " messages");
        } else {
            Log.d(TAG, "No saved conversation found for session: " + sessionId);
        }
    }

    // Generate a new session ID
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis();
    }

    // In EnhancedConversationManager.java - add this method
    public void addGameStateToCurrentContext(GameStateInfo gameState) {
        // Store the current game state in the conversation context
        String gameStateContext = "Current position (FEN): " + gameState.getCurrentFen() + "\n" +
                "Playing as: " + gameState.getPlayerColor() + "\n" +
                "Game phase: " + gameState.getGamePhase() + "\n" +
                "Moves played: " + String.join(", ", gameState.getMoveHistory());

        // Add as a hidden system message that will be included in each turn
        addMessage("system", "CHESS_STATE: " + gameStateContext);
    }

// Call this method each time before processing a user query

    // Start a new conversation
    public void startNewConversation() {
        activeConversation.clear();
        currentSessionId = generateSessionId();

        // Add the system prompt as the first message
        addMessage("system", "You are Coach Tal, a FIDE-rated chess expert analyzing games. " +
                "Be encouraging, supportive, and share insights about chess strategy in your responses. " +
                "Keep your answers concise and focused.");

        Log.d(TAG, "Started new conversation with session: " + currentSessionId);
    }

    // Get all available conversation sessions
    public List<String> getAvailableSessions() {
        return storage.getAllSessionIds();
    }
}