package com.example.chesspedagogue;

/**
 * Represents a message in the chat conversation between user and AI.
 */
public class ChatMessage {
    // Message types for UI display
    public static final int TYPE_USER = 1;
    public static final int TYPE_COACH = 2;

    private final int type;
    private final String message;

    /**
     * Create a new chat message
     *
     * @param type    The type of message (TYPE_USER or TYPE_COACH)
     * @param message The message content
     */
    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Create a message for API conversation (not UI display)
     *
     * @param role    The role ("user", "assistant", or "system")
     * @param content The message content
     */
    public ChatMessage(String role, String content) {
        this.type = role.equals("user") ? TYPE_USER : TYPE_COACH;
        this.message = content;
        this.role = role;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    // For API compatibility
    public String role;
    public String content;
}