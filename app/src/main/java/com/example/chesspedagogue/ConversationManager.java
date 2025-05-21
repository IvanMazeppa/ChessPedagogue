package com.example.chesspedagogue;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the conversation history between the user and Coach Tal
 */
public class ConversationManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private List<Message> conversationHistory = new ArrayList<>();
    private EnhancedConversationManager enhancedManager;

    // Default constructor that works with your SimpleRecordService
    public ConversationManager() {
        // Initialize with system message
        enhancedManager = EnhancedConversationManager.getInstance(null);
        addSystemMessage("You are Coach Tal, a FIDE-rated chess expert analyzing games. When analyzing positions, you MUST:\n" +
                "1. Reference specific moves from the move history by number (e.g., \"After 15...Qd7, White missed...\")\n" +
                "2. Analyze how previous moves influenced the current position\n" +
                "3. Provide concrete calculations and variations, not just general principles\n" +
                "4. Evaluate alternatives to key moves played in the game\n" +
                "5. NEVER provide generic advice without referencing the specific move history provided\n\n" +
                "Always analyze the current position in the context of how the game developed. Keep responses concise as they will be spoken aloud.");
    }


    public void addSystemMessage(String content) {
        enhancedManager.addMessage("system", content);
    }

    public void addUserMessage(String content) {
        enhancedManager.addMessage("user", content);
    }

    // In ConversationManager.java
    public List<Message> getConversationHistory() {
        // Make sure we have a properly initialized list
        List<Message> messages = new ArrayList<>();

        // Add the system message first
        messages.add(new Message("system",
                "You are Coach Tal, a FIDE-rated chess expert analyzing games. " +
                        "Be encouraging, supportive, and share insights about chess strategy in your responses. " +
                        "Keep your answers concise and focused."));

        // This is critical: make sure we add ALL messages from enhancedManager
        if (enhancedManager != null) {
            try {
                List<?> enhancedMessages = enhancedManager.getConversationHistory();
                if (enhancedMessages != null) {
                    for (Object msg : enhancedMessages) {
                        if (msg instanceof Message) {
                            messages.add((Message) msg);
                        } else if (msg != null) {
                            // Try to extract role and content using reflection
                            try {
                                String role = msg.getClass().getMethod("getRole").invoke(msg).toString();
                                String content = msg.getClass().getMethod("getContent").invoke(msg).toString();
                                messages.add(new Message(role, content));
                            } catch (Exception e) {
                                Log.e("ConversationManager", "Failed to convert message", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ConversationManager", "Error getting history from enhanced manager", e);
            }
        }

        // Manually add user and assistant messages for safety
        addUserMessage("What is your name?");

        Log.d("ConversationManager", "Returning " + messages.size() + " messages");
        for (Message msg : messages) {
            Log.d("ConversationManager", "Message: " + msg.getRole() + " - " + msg.getContent());
        }

        return messages;
    }

    public void clear() {
        enhancedManager.startNewConversation();
    }

    // Constructor for VoiceService - matches your existing code
    public ConversationManager(SpeechToTextService sttService, ChatService chatService, TextToSpeechService ttsService) {
        this(); // Call default constructor to initialize system message
        // Here we would store the services if needed, but for now we'll just keep the constructor
    }



    // Add this method to match existing code
    public void setSystemMessage(String message) {
        addSystemMessage(message);
    }


    public void addAssistantMessage(String content) {
        conversationHistory.add(new Message("assistant", content));
        trimHistory();
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