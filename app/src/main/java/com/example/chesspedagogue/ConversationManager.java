package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
/**
 * Unified conversation manager that handles all conversation functionality.
 * Merged from ConversationManager, EnhancedConversationManager, and ConversationStorage.
 * FIXED: Proper context handling and thread-safe operations
 */
public class ConversationManager {
    private static final String TAG = "ConversationManager";
    private static final int MAX_HISTORY_SIZE = 10;
    private static final String PREFS_NAME = "chess_conversations";

    private static ConversationManager instance;

    private final Context context;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final Handler mainHandler; // ADDED: For thread-safe operations

    private List<Message> conversationHistory = new ArrayList<>();
    private String currentSessionId;
    // Add to ConversationManager
    private Map<String, List<Message>> masterConversations = new HashMap<>();
    private String lastSpeaker = "";
    private String conversationTopic = "";

    // For compatibility with old VoiceService constructor - FIXED

    /**
     * Private constructor for singleton pattern - FIXED: Proper context handling
     */
    private ConversationManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null for ConversationManager");
        }

        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper()); // ADDED
        this.currentSessionId = generateSessionId();

        // Initialize with system message
        addSystemMessage("You are a chess expert analyzing games. When analyzing positions, you MUST:\n" +
                "1. Reference specific moves from the move history by number (e.g., \"After 15...Qd7, White missed...\")\n" +
                "2. Analyze how previous moves influenced the current position\n" +
                "3. Provide concrete calculations and variations, not just general principles\n" +
                "4. Evaluate alternatives to key moves played in the game\n" +
                "5. NEVER provide generic advice without referencing the specific move history provided\n\n" +
                "Always analyze the current position in the context of how the game developed. Keep responses concise as they will be spoken aloud.");

        Log.d(TAG, "ConversationManager initialized with session: " + currentSessionId);
    }

    /**
     * FIXED: Constructor for VoiceService compatibility - now requires context
     */
    public ConversationManager(Context context, SpeechToTextService sttService, ChatService chatService, TextToSpeechService ttsService) {
        this(context);
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


    public boolean shouldRespondToMaster(String speaker, String content) {
        // Logic to determine if the other master should respond
        return !speaker.equals(lastSpeaker) &&
                containsConversationTriggers(content);
    }

    /**
     * Callback interface for AI master conversations
     */
    public interface ConversationCallback {
        void onMasterResponse(String master, String response);
        void onConversationError(String error);
        void onConversationComplete();
    }

    /**
     * Check if content contains conversation triggers
     */
    private boolean containsConversationTriggers(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        String lowerContent = content.toLowerCase();

        // Conversation trigger words/phrases
        String[] triggers = {
                "what do you think", "your opinion", "agree", "disagree",
                "remember when", "similar position", "reminds me",
                "sacrifice", "attack", "defense", "strategy", "tactics",
                "brilliant", "mistake", "interesting", "fascinating"
        };

        for (String trigger : triggers) {
            if (lowerContent.contains(trigger)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get recent messages for conversation context
     */
    public List<Message> getRecentMessages(int count) {
        List<Message> recent = new ArrayList<>();
        List<Message> history = getConversationHistory();

        int start = Math.max(0, history.size() - count);
        for (int i = start; i < history.size(); i++) {
            recent.add(history.get(i));
        }

        return recent;
    }

    /**
     * Add a master's message to the conversation
     */
    public void addMasterMessage(String master, String content) {
        addMessage(master, content);
        lastSpeaker = master;
        saveCurrentConversation();
    }

    /**
     * Check if a master should respond to another master's comment
     */
    public boolean shouldMasterRespond(String speaker, String content) {
        // Don't respond to yourself
        if (speaker.equals(lastSpeaker)) {
            return false;
        }

        // Check for conversation triggers
        return containsConversationTriggers(content);
    }

    /**
     * Build conversation context from recent messages
     */
    public String buildConversationContext() {
        StringBuilder context = new StringBuilder();

        List<Message> recentMessages = getRecentMessages(5);
        for (Message msg : recentMessages) {
            if (!msg.getRole().equals("system")) {
                context.append(msg.getRole())
                        .append(" said: \"")
                        .append(msg.getContent())
                        .append("\"\n");
            }
        }

        return context.toString();
    }

    /**
     * Add system message
     */
    public void addSystemMessage(String content) {
        // Replace existing system message if present
        for (int i = 0; i < conversationHistory.size(); i++) {
            if (conversationHistory.get(i).getRole().equals("system")) {
                conversationHistory.set(i, new Message("system", content));
                Log.d(TAG, "Updated system message");
                return;
            }
        }
        // Add new system message
        conversationHistory.add(new Message("system", content));
        Log.d(TAG, "Added new system message");
    }

    /**
     * Alias for compatibility
     */
    public void setSystemMessage(String message) {
        addSystemMessage(message);
    }

    /**
     * Add user message - THREAD SAFE
     */
    public void addUserMessage(String content) {
        addMessageThreadSafe("user", content);
    }

    /**
     * Add assistant message - THREAD SAFE
     */
    public void addAssistantMessage(String content) {
        addMessageThreadSafe("assistant", content);
    }

    /**
     * ADDED: Thread-safe message addition
     */
    private void addMessageThreadSafe(String role, String content) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on main thread
            addMessageInternal(role, content);
        } else {
            // Post to main thread
            mainHandler.post(() -> addMessageInternal(role, content));
        }
    }

    /**
     * ADDED: Internal message addition method
     */
    private void addMessageInternal(String role, String content) {
        conversationHistory.add(new Message(role, content));
        trimHistory();
        Log.d(TAG, "Added " + role + " message (history size: " + conversationHistory.size() + ")");
    }

    /**
     * Add generic message (from Enhanced) - THREAD SAFE
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

        Log.d(TAG, "Conversation cleared, system message preserved");
    }

    /**
     * Save current conversation to storage - THREAD SAFE
     */
    public void saveCurrentConversation() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on main thread
            saveCurrentConversationInternal();
        } else {
            // Post to main thread
            mainHandler.post(this::saveCurrentConversationInternal);
        }
    }

    /**
     * ADDED: Internal save method that runs on main thread
     */
    private void saveCurrentConversationInternal() {
        try {
            saveConversation(currentSessionId, conversationHistory);
            Log.d(TAG, "✅ Successfully saved conversation with " + conversationHistory.size() + " messages");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error saving current conversation", e);
        }
    }

    /**
     * Save a conversation to SharedPreferences - ENHANCED with error handling
     */
    public void saveConversation(String sessionId, List<Message> conversation) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                Log.e(TAG, "Cannot save conversation: sessionId is null or empty");
                return;
            }

            if (conversation == null) {
                Log.e(TAG, "Cannot save conversation: conversation list is null");
                return;
            }

            String json = gson.toJson(conversation);

            if (json == null || json.isEmpty()) {
                Log.e(TAG, "Cannot save conversation: JSON serialization failed");
                return;
            }

            // Save to SharedPreferences
            boolean success = prefs.edit().putString(sessionId, json).commit(); // Using commit() for immediate write

            if (success) {
                // Update session list
                updateSessionList(sessionId);
                Log.d(TAG, "✅ Saved conversation for session: " + sessionId + " with JSON length: " + json.length());
            } else {
                Log.e(TAG, "❌ Failed to write conversation to SharedPreferences");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception while saving conversation", e);
        }
    }

    /**
     * Load a conversation from SharedPreferences - ENHANCED with error handling
     */
    public List<Message> loadConversation(String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                Log.w(TAG, "Cannot load conversation: sessionId is null or empty");
                return new ArrayList<>();
            }

            String json = prefs.getString(sessionId, null);
            if (json == null) {
                Log.d(TAG, "No saved conversation found for session: " + sessionId);
                return new ArrayList<>();
            }

            Type type = new TypeToken<ArrayList<Message>>(){}.getType();
            List<Message> result = gson.fromJson(json, type);

            if (result == null) {
                Log.w(TAG, "JSON deserialization returned null for session: " + sessionId);
                return new ArrayList<>();
            }

            Log.d(TAG, "✅ Loaded conversation for session: " + sessionId + " with " + result.size() + " messages");
            return result;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error loading conversation for session: " + sessionId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Resume a previous conversation by ID - THREAD SAFE
     */
    public void resumeConversation(String sessionId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on main thread
            resumeConversationInternal(sessionId);
        } else {
            // Post to main thread
            mainHandler.post(() -> resumeConversationInternal(sessionId));
        }
    }

    /**
     * ADDED: Internal resume method
     */
    private void resumeConversationInternal(String sessionId) {
        try {
            List<Message> savedConversation = loadConversation(sessionId);
            if (savedConversation != null && !savedConversation.isEmpty()) {
                conversationHistory.clear();
                conversationHistory.addAll(savedConversation);
                currentSessionId = sessionId;
                Log.d(TAG, "✅ Resumed conversation with " + savedConversation.size() + " messages");
            } else {
                Log.d(TAG, "No saved conversation found for session: " + sessionId + ", starting new");
                startNewConversation();
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error resuming conversation", e);
            startNewConversation();
        }
    }

    /**
     * Start a new conversation - THREAD SAFE
     */
    public void startNewConversation() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on main thread
            startNewConversationInternal();
        } else {
            // Post to main thread
            mainHandler.post(this::startNewConversationInternal);
        }
    }

    /**
     * ADDED: Internal start new conversation method
     */
    private void startNewConversationInternal() {
        conversationHistory.clear();
        currentSessionId = generateSessionId();

        // Add the system prompt as the first message
        addSystemMessage("You are a chess expert analyzing games. " +
                "Be encouraging, supportive, and share insights about chess strategy in your responses. " +
                "Keep your answers concise and focused.");

        Log.d(TAG, "✅ Started new conversation with session: " + currentSessionId);
    }

    /**
     * Get all available session IDs - ENHANCED with error handling
     */
    public List<String> getAllSessionIds() {
        try {
            String sessionsJson = prefs.getString("session_list", "[]");
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> sessions = gson.fromJson(sessionsJson, type);

            if (sessions == null) {
                Log.w(TAG, "Session list deserialization returned null, returning empty list");
                return new ArrayList<>();
            }

            Log.d(TAG, "Retrieved " + sessions.size() + " session IDs");
            return sessions;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting session list", e);
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
     * Delete a saved conversation - ENHANCED with error handling
     */
    public void deleteConversation(String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                Log.w(TAG, "Cannot delete conversation: sessionId is null or empty");
                return;
            }

            // Remove from SharedPreferences
            prefs.edit().remove(sessionId).apply();

            // Update session list
            List<String> sessions = getAllSessionIds();
            sessions.remove(sessionId);
            String json = gson.toJson(sessions);
            prefs.edit().putString("session_list", json).apply();

            Log.d(TAG, "✅ Deleted conversation: " + sessionId);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error deleting conversation: " + sessionId, e);
        }
    }

    /**
     * Delete all saved conversations
     */
    public void deleteAllConversations() {
        try {
            // Get all session IDs
            List<String> sessions = getAllSessionIds();

            // Remove each conversation
            SharedPreferences.Editor editor = prefs.edit();
            for (String sessionId : sessions) {
                editor.remove(sessionId);
            }

            // Clear the session list
            editor.putString("session_list", "[]");
            editor.apply();

            Log.d(TAG, "✅ Deleted all " + sessions.size() + " conversations");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error deleting all conversations", e);
        }
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
     * Update the session list with a new session ID - ENHANCED with error handling
     */
    private void updateSessionList(String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                Log.w(TAG, "Cannot update session list: sessionId is null or empty");
                return;
            }

            List<String> sessions = getAllSessionIds();
            if (!sessions.contains(sessionId)) {
                sessions.add(sessionId);
                String json = gson.toJson(sessions);

                boolean success = prefs.edit().putString("session_list", json).commit();
                if (success) {
                    Log.d(TAG, "✅ Added session ID to list: " + sessionId);
                } else {
                    Log.e(TAG, "❌ Failed to update session list");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating session list", e);
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
        Log.d(TAG, "Trimmed conversation history to " + conversationHistory.size() + " messages");
    }

    /**
     * ADDED: Get current session ID for debugging
     */
    public String getCurrentSessionId() {
        return currentSessionId;
    }

    /**
     * ADDED: Get conversation history size for debugging
     */
    public int getConversationSize() {
        return conversationHistory.size();
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