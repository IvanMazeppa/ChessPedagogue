package com.example.chesspedagogue;

import android.util.Log;

/**
 * Singleton manager for the chess board
 */
public class ChessBoardManager {
    private static final String TAG = "ChessBoardManager";

    private static ChessBoardManager instance;
    private ChessBoardView currentBoardView;

    private ChessBoardManager() {
        // Private constructor for singleton
    }

    public static synchronized ChessBoardManager getInstance() {
        if (instance == null) {
            instance = new ChessBoardManager();
            Log.d(TAG, "ChessBoardManager instance created");
        }
        return instance;
    }

    public void setCurrentBoardView(ChessBoardView boardView) {
        this.currentBoardView = boardView;
        Log.d(TAG, "Current board view set");
    }

    public ChessBoardView getCurrentBoardView() {
        return currentBoardView;
    }
}