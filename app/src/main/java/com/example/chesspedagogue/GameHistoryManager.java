package com.example.chesspedagogue;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the history of chess moves for the current game
 */
public class GameHistoryManager {
    private static final String TAG = "GameHistoryManager";

    private static GameHistoryManager instance;
    private final List<String> currentGameMoves = new ArrayList<>();

    private GameHistoryManager() {
        // Private constructor for singleton
    }

    public static synchronized GameHistoryManager getInstance() {
        if (instance == null) {
            instance = new GameHistoryManager();
            Log.d(TAG, "GameHistoryManager instance created");
        }
        return instance;
    }

    /**
     * Add a move to the current game history
     */
    public void addMove(String move) {
        currentGameMoves.add(move);
        Log.d(TAG, "Move added: " + move);
    }

    /**
     * Clear the current game history
     */
    public void clearHistory() {
        currentGameMoves.clear();
        Log.d(TAG, "Game history cleared");
    }

    /**
     * Get the list of moves from the current game
     */
    public List<String> getCurrentGameMoves() {
        return new ArrayList<>(currentGameMoves);
    }
}