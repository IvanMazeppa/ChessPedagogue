package com.example.chesspedagogue;

import java.util.List;

/**
 * Modern conversation management interface
 */
public interface ChessConversationController {
    void startConversation(ConversationContext context);
    void processUserInput(String userInput);
    void endConversation();
    void addConversationListener(ConversationListener listener);
    void removeConversationListener(ConversationListener listener);

    // Simple context class
    class ConversationContext {
        public String chessFen;
        public List<String> moveHistory;
        public String playerColor;
        public String chessMaster;
    }

    // Essential listener interface
    interface ConversationListener {
        void onConversationStarted();
        void onThinking();
        void onResponse(String response);
        void onError(String errorMessage);
        void onConversationEnded();
    }
}