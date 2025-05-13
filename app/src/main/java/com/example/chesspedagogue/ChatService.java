package com.example.chesspedagogue;

import java.io.IOException;
import java.util.List;

/**
 * Interface for services that generate AI chat replies.
 */
public interface ChatService {
    /**
     * Generate a reply based on the message history
     *
     * @param messageHistory The history of messages in the conversation
     * @return The generated reply text
     * @throws IOException If there's an error communicating with the API
     */
    String generateReply(List<ChatMessage> messageHistory) throws IOException;
}