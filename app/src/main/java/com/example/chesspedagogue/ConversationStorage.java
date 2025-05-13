package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConversationStorage {
    private static final String TAG = "ConversationStorage";
    private static final String PREFS_NAME = "chess_conversations";
    private final SharedPreferences prefs;
    private final Gson gson;

    public ConversationStorage(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        Log.d(TAG, "ConversationStorage initialized");
    }

    // Save a conversation to SharedPreferences
    public void saveConversation(String sessionId, List<ConversationManager.Message> conversation) {
        String json = gson.toJson(conversation);
        prefs.edit().putString(sessionId, json).apply();

        // Also update the session list
        updateSessionList(sessionId);
        Log.d(TAG, "Saved conversation for session: " + sessionId + " with JSON length: " + json.length());
    }

    // Load a conversation from SharedPreferences
    public List<ConversationManager.Message> loadConversation(String sessionId) {
        String json = prefs.getString(sessionId, null);
        if (json == null) {
            Log.d(TAG, "No saved conversation found for session: " + sessionId);
            return new ArrayList<>();
        }

        try {
            // Use TypeToken to handle the generic type
            Type type = new TypeToken<ArrayList<ConversationManager.Message>>(){}.getType();
            List<ConversationManager.Message> result = gson.fromJson(json, type);
            Log.d(TAG, "Loaded conversation for session: " + sessionId + " with " + result.size() + " messages");
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error loading conversation", e);
            return new ArrayList<>();
        }
    }

    // Get all available session IDs
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

    // Update the session list with a new session ID
    private void updateSessionList(String sessionId) {
        List<String> sessions = getAllSessionIds();
        if (!sessions.contains(sessionId)) {
            sessions.add(sessionId);
            String json = gson.toJson(sessions);
            prefs.edit().putString("session_list", json).apply();
            Log.d(TAG, "Added session ID to list: " + sessionId);
        }
    }
}