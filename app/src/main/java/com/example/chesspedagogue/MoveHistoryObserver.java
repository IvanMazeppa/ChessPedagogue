package com.example.chesspedagogue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A thread-safe implementation of the observer pattern for move history.
 * This allows game state changes to be safely communicated to AI components.
 */
public class MoveHistoryObserver {
    // Using CopyOnWriteArrayList for thread safety without explicit synchronization
    private final CopyOnWriteArrayList<MoveHistoryListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Interface for listeners that want to be notified of move history changes
     */
    public interface MoveHistoryListener {
        void onMoveMade(String move, String fen, List<String> fullHistory);
        void onGameReset();
    }

    /**
     * Register a listener for move history events
     */
    public void addListener(MoveHistoryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener
     */
    public void removeListener(MoveHistoryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all listeners that a move has been made
     */
    public void notifyMoveMade(String move, String fen, List<String> fullHistory) {
        // Create immutable copy for thread safety
        final List<String> historyCopy = new ArrayList<>(fullHistory);

        for (MoveHistoryListener listener : listeners) {
            listener.onMoveMade(move, fen, historyCopy);

        }
    }

    /**
     * Notify all listeners that the game has been reset
     */
    public void notifyGameReset() {
        for (MoveHistoryListener listener : listeners) {
            listener.onGameReset();
        }
    }
}