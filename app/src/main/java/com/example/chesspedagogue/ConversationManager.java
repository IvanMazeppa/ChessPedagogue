package com.example.chesspedagogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the conversation history between the user and Coach Tal
 */
public class ConversationManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private List<Message> conversationHistory = new ArrayList<>();

    // Default constructor that works with your SimpleRecordService
    public ConversationManager() {
        // Initialize with system message
        addSystemMessage("You are Coach Tal, a FIDE-rated chess expert analyzing games. When analyzing positions, you MUST:\n" +
                "1. Reference specific moves from the move history by number (e.g., \"After 15...Qd7, White missed...\")\n" +
                "2. Analyze how previous moves influenced the current position\n" +
                "3. Provide concrete calculations and variations, not just general principles\n" +
                "4. Evaluate alternatives to key moves played in the game\n" +
                "5. NEVER provide generic advice without referencing the specific move history provided\n\n" +
                "Always analyze the current position in the context of how the game developed. Keep responses concise as they will be spoken aloud.");
    }

    // Constructor for VoiceService - matches your existing code
    public ConversationManager(SpeechToTextService sttService, ChatService chatService, TextToSpeechService ttsService) {
        this(); // Call default constructor to initialize system message
        // Here we would store the services if needed, but for now we'll just keep the constructor
    }


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

    // Add this method to match existing code
    public void setSystemMessage(String message) {
        addSystemMessage(message);
    }

    public void addUserMessage(String content) {
        conversationHistory.add(new Message("user", content));
        trimHistory();
    }

    public void addAssistantMessage(String content) {
        conversationHistory.add(new Message("assistant", content));
        trimHistory();
    }

    public List<Message> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

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

    // This should exist in your ConversationManager class
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
}