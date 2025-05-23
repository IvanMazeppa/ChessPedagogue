package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified conversation manager that handles all conversation functionality.
 * Merged from ConversationManager, EnhancedConversationManager, and ConversationStorage.
 */
public class ConversationManager {
    private static final String TAG = "ConversationManager";
    private static final int MAX_HISTORY_SIZE = 10;
    private static final String PREFS_NAME = "chess_conversations";

    private static ConversationManager instance;

    private final Context context;
    private final SharedPreferences prefs;
    private final Gson gson;

    private List<Message> conversationHistory = new ArrayList<>();
    private String currentSessionId;

    // For compatibility with old VoiceService constructor
    private SpeechToTextService sttService;
    private ChatService chatService;
    private TextToSpeechService ttsService;

    /**
     * Private constructor for singleton pattern
     */
    private ConversationManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.currentSessionId = generateSessionId();

        // Initialize with system message
        addSystemMessage("You are Coach Tal, a FIDE-rated chess expert analyzing games. When analyzing positions, you MUST:\n" +
                "1. Reference specific moves from the move history by number (e.g., \"After 15...Qd7, White missed...\")\n" +
                "2. Analyze how previous moves influenced the current position\n" +
                "3. Provide concrete calculations and variations, not just general principles\n" +
                "4. Evaluate alternatives to key moves played in the game\n" +
                "5. NEVER provide generic advice without referencing the specific move history provided\n\n" +
                "Always analyze the current position in the context of how the game developed. Keep responses concise as they will be spoken aloud.");

        Log.d(TAG, "ConversationManager initialized with session: " + currentSessionId);
    }

    /**
     * Constructor for VoiceService compatibility
     */
    public ConversationManager(SpeechToTextService sttService, ChatService chatService, TextToSpeechService ttsService) {
        this((Context)null); // Use the default context-less initialization
        this.sttService = sttService;
        this.chatService = chatService;
        this.ttsService = ttsService;
    }

    /**
     * Get singleton instance
     */
    public static synchronized ConversationManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConversationManager(context);
        }
        return instance;
    }

    /**
     * Add system message
     */
    public void addSystemMessage(String content) {
        // Replace existing system message if present
        for (int i = 0; i < conversationHistory.size(); i++) {
            if (conversationHistory.get(i).getRole().equals("system")) {
                conversationHistory.set(i, new Message("system", content));
                return;
            }
        }
        // Add new system message
        conversationHistory.add(new Message("system", content));
    }

    /**
     * Alias for compatibility
     */
    public void setSystemMessage(String message) {
        addSystemMessage(message);
    }

    /**
     * Add user message
     */
    public void addUserMessage(String content) {
        conversationHistory.add(new Message("user", content));
        trimHistory();
    }

    /**
     * Add assistant message
     */
    public void addAssistantMessage(String content) {
        conversationHistory.add(new Message("assistant", content));
        trimHistory();
    }

    /**
     * Add generic message (from Enhanced)
     */
    public void addMessage(String role, String content) {
        if ("system".equals(role)) {
            addSystemMessage(content);
        } else if ("user".equals(role)) {
            addUserMessage(content);
        } else if ("assistant".equals(role)) {
            addAssistantMessage(content);
        }
    }

    /**
     * Get conversation history
     */
    public List<Message> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    /**
     * Clear conversation while preserving system message
     */
    public void clear() {
        String systemMessage = "";

        // Save system message if it exists
        for (Message message : conversationHistory) {
            if (message.getRole().equals("system")) {
                systemMessage = message.getContent();
                break;
            }
        }

        conversationHistory.clear();

        // Re-add system message
        if (!systemMessage.isEmpty()) {
            addSystemMessage(systemMessage);
        }
    }

    /**
     * Save current conversation to storage
     */
    public void saveCurrentConversation() {
        saveConversation(currentSessionId, conversationHistory);
        Log.d(TAG, "Saved conversation with " + conversationHistory.size() + " messages");
    }

    /**
     * Save a conversation to SharedPreferences
     */
    public void saveConversation(String sessionId, List<Message> conversation) {
        String json = gson.toJson(conversation);
        prefs.edit().putString(sessionId, json).apply();

        // Update session list
        updateSessionList(sessionId);
        Log.d(TAG, "Saved conversation for session: " + sessionId + " with JSON length: " + json.length());
    }

    /**
     * Load a conversation from SharedPreferences
     */
    public List<Message> loadConversation(String sessionId) {
        String json = prefs.getString(sessionId, null);
        if (json == null) {
            Log.d(TAG, "No saved conversation found for session: " + sessionId);
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<ArrayList<Message>>(){}.getType();
            List<Message> result = gson.fromJson(json, type);
            Log.d(TAG, "Loaded conversation for session: " + sessionId + " with " + result.size() + " messages");
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error loading conversation", e);
            return new ArrayList<>();
        }
    }

    /**
     * Resume a previous conversation by ID
     */
    public void resumeConversation(String sessionId) {
        List<Message> savedConversation = loadConversation(sessionId);
        if (savedConversation != null && !savedConversation.isEmpty()) {
            conversationHistory.clear();
            conversationHistory.addAll(savedConversation);
            currentSessionId = sessionId;
            Log.d(TAG, "Resumed conversation with " + savedConversation.size() + " messages");
        } else {
            Log.d(TAG, "No saved conversation found for session: " + sessionId);
        }
    }

    /**
     * Start a new conversation
     */
    public void startNewConversation() {
        conversationHistory.clear();
        currentSessionId = generateSessionId();

        // Add the system prompt as the first message
        addSystemMessage("You are Coach Tal, a FIDE-rated chess expert analyzing games. " +
                "Be encouraging, supportive, and share insights about chess strategy in your responses. " +
                "Keep your answers concise and focused.");

        Log.d(TAG, "Started new conversation with session: " + currentSessionId);
    }

    /**
     * Get all available session IDs
     */
    public List<String> getAllSessionIds() {
        String sessionsJson = prefs.getString("session_list", "[]");
        try {
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> sessions = gson.fromJson(sessionsJson, type);
            Log.d(TAG, "Retrieved " + sessions.size() + " session IDs");
            return sessions;
        } catch (Exception e) {
            Log.e(TAG, "Error getting session list", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get all available sessions (alias for compatibility)
     */
    public List<String> getAvailableSessions() {
        return getAllSessionIds();
    }

    /**
     * Delete a saved conversation
     */
    public void deleteConversation(String sessionId) {
        // Remove from SharedPreferences
        prefs.edit().remove(sessionId).apply();

        // Update session list
        List<String> sessions = getAllSessionIds();
        sessions.remove(sessionId);
        String json = gson.toJson(sessions);
        prefs.edit().putString("session_list", json).apply();

        Log.d(TAG, "Deleted conversation: " + sessionId);
    }

    /**
     * Delete all saved conversations
     */
    public void deleteAllConversations() {
        // Get all session IDs
        List<String> sessions = getAllSessionIds();

        // Remove each conversation
        for (String sessionId : sessions) {
            prefs.edit().remove(sessionId).apply();
        }

        // Clear the session list
        prefs.edit().putString("session_list", "[]").apply();

        Log.d(TAG, "Deleted all " + sessions.size() + " conversations");
    }

    /**
     * Get conversation metadata (for display purposes)
     */
    public ConversationMetadata getConversationMetadata(String sessionId) {
        List<Message> conversation = loadConversation(sessionId);
        if (conversation.isEmpty()) {
            return null;
        }

        // Extract first user message as preview
        String preview = "New conversation";
        for (Message msg : conversation) {
            if ("user".equals(msg.getRole())) {
                preview = msg.getContent();
                if (preview.length() > 50) {
                    preview = preview.substring(0, 47) + "...";
                }
                break;
            }
        }

        // Parse timestamp from session ID
        long timestamp = 0;
        try {
            String timeStr = sessionId.replace("session_", "");
            timestamp = Long.parseLong(timeStr);
        } catch (Exception e) {
            // Ignore parsing errors
        }

        return new ConversationMetadata(sessionId, preview, timestamp, conversation.size());
    }

    /**
     * Add game state to current context
     */
    public void addGameStateToCurrentContext(GameStateInfo gameState) {
        // Store the current game state in the conversation context
        String gameStateContext = "Current position (FEN): " + gameState.getCurrentFen() + "\n" +
                "Playing as: " + gameState.getPlayerColor() + "\n" +
                "Game phase: " + gameState.getGamePhase() + "\n" +
                "Moves played: " + String.join(", ", gameState.getMoveHistory());

        // Add as a hidden system message that will be included in each turn
        addMessage("system", "CHESS_STATE: " + gameStateContext);
    }

    /**
     * Update the session list with a new session ID
     */
    private void updateSessionList(String sessionId) {
        List<String> sessions = getAllSessionIds();
        if (!sessions.contains(sessionId)) {
            sessions.add(sessionId);
            String json = gson.toJson(sessions);
            prefs.edit().putString("session_list", json).apply();
            Log.d(TAG, "Added session ID to list: " + sessionId);
        }
    }

    /**
     * Generate a new session ID
     */
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis();
    }

    /**
     * Trim history to maximum size
     */
    private void trimHistory() {
        if (conversationHistory.size() <= MAX_HISTORY_SIZE) {
            return;
        }

        // Keep system message and most recent exchanges
        List<Message> systemMessages = new ArrayList<>();
        for (Message message : conversationHistory) {
            if (message.getRole().equals("system")) {
                systemMessages.add(message);
            }
        }

        int nonSystemToKeep = MAX_HISTORY_SIZE - systemMessages.size();
        int totalNonSystem = conversationHistory.size() - systemMessages.size();

        if (nonSystemToKeep <= 0 || totalNonSystem <= 0) {
            return;
        }

        List<Message> newHistory = new ArrayList<>(systemMessages);
        newHistory.addAll(conversationHistory.subList(
                conversationHistory.size() - nonSystemToKeep,
                conversationHistory.size()));

        conversationHistory = newHistory;
    }

    /**
     * Message class for conversation history
     */
    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    /**
     * Metadata class for conversation display
     */
    public static class ConversationMetadata {
        private final String sessionId;
        private final String preview;
        private final long timestamp;
        private final int messageCount;

        public ConversationMetadata(String sessionId, String preview, long timestamp, int messageCount) {
            this.sessionId = sessionId;
            this.preview = preview;
            this.timestamp = timestamp;
            this.messageCount = messageCount;
        }

        public String getSessionId() { return sessionId; }
        public String getPreview() { return preview; }
        public long getTimestamp() { return timestamp; }
        public int getMessageCount() { return messageCount; }
    }
}