package com.example.chesspedagogue.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class for thread-safe data sharing between game components
 * and AI analysis threads.
 */
public class ThreadSafeDataUtil {

    /**
     * Creates an immutable copy of a move history list for thread-safe sharing
     */
    public static List<String> createImmutableMoveHistoryCopy(List<String> original) {
        if (original == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(original));
    }

    /**
     * Thread-safe wrapper for game state information
     */
    public static class ThreadSafeGameState {
        private final AtomicReference<String> fenPosition = new AtomicReference<>("");
        private final AtomicReference<List<String>> moveHistory =
                new AtomicReference<>(Collections.emptyList());

        public void updateState(String fen, List<String> moves) {
            fenPosition.set(fen);
            moveHistory.set(createImmutableMoveHistoryCopy(moves));
        }

        public String getFen() {
            return fenPosition.get();
        }

        public List<String> getMoveHistory() {
            return moveHistory.get();
        }
    }
}