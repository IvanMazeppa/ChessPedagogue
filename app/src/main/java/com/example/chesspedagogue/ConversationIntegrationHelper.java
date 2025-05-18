package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Integration adapter that bridges your existing codebase with the new realtime conversation system.
 * This class ensures compatibility between the different interface methods.
 */
public class ConversationIntegrationHelper {
    private static final String TAG = "ConversationIntegration";

    private final Context context;
    private final Handler mainHandler;
    private final RealtimeConversationManager realtimeManager;
    private AudioConversationManager.ConversationListener originalListener;

    public ConversationIntegrationHelper(Context context, RealtimeConversationManager realtimeManager) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.realtimeManager = realtimeManager;

        // Create an adapter for the existing conversation state listener
        setupConversationAdapter();
    }

    /**
     * Set up an adapter between the old and new conversation interfaces
     */
    private void setupConversationAdapter() {
        realtimeManager.setConversationStateListener(new RealtimeConversationManager.ConversationStateListener() {
            @Override
            public void onPassiveListening() {
                if (originalListener != null) {
                    mainHandler.post(() -> originalListener.onStateChanged(AudioConversationManager.State.IDLE));
                }
            }

            @Override
            public void onListening() {
                if (originalListener != null) {
                    mainHandler.post(() -> originalListener.onStateChanged(AudioConversationManager.State.LISTENING));
                }
            }

            @Override
            public void onProcessing() {
                if (originalListener != null) {
                    mainHandler.post(() -> originalListener.onStateChanged(AudioConversationManager.State.PROCESSING));
                }
            }

            @Override
            public void onSpeaking(String text) {
                if (originalListener != null) {
                    mainHandler.post(() -> {
                        originalListener.onStateChanged(AudioConversationManager.State.SPEAKING);
                        originalListener.onTextResponse(text);
                    });
                }
            }

            @Override
            public void onPartialResponse(String partialText) {
                if (originalListener != null) {
                    mainHandler.post(() -> originalListener.onPartialResponse(partialText));
                }
            }

            @Override
            public void onError(String message) {
                if (originalListener != null) {
                    mainHandler.post(() -> originalListener.onError(message));
                }
            }

            @Override
            public void onConversationEnded() {
                if (originalListener != null) {
                    mainHandler.post(() -> originalListener.onStateChanged(AudioConversationManager.State.IDLE));
                }
            }
        });
    }

    /**
     * Set the original conversation listener from your existing codebase
     */
    public void setOriginalListener(AudioConversationManager.ConversationListener listener) {
        this.originalListener = listener;
    }

    /**
     * When the WebSocket is connected, call this method to simulate the onConnected callback
     * in your original interface
     */
    public void notifyConnected() {
        if (originalListener != null) {
            mainHandler.post(() -> originalListener.onConnected());
        }
    }

    /**
     * Start the conversation with game context
     */
    public void startConversation(GameStateInfo gameState) {
        // First notify connected to maintain compatibility with your code
        notifyConnected();

        // Then start the actual conversation
        realtimeManager.startConversation(gameState);
    }

    /**
     * This helper method makes it easier to show toast messages for debugging
     */
    public void showToast(String message) {
        mainHandler.post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }
}