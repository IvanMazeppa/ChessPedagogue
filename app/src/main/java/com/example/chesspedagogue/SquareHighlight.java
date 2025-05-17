package com.example.chesspedagogue;

import android.graphics.Color;

/**
 * Represents a highlighted square on the chess board
 */
public class SquareHighlight {
    private final String square; // algebraic notation (e.g., "e4")
    private final int color;
    private final long creationTime;
    private final int duration; // in milliseconds

    public SquareHighlight(String square, int color, int duration) {
        this.square = square;
        this.color = color;
        this.creationTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > duration;
    }

    public String getSquare() {
        return square;
    }

    public int getColor() {
        return color;
    }
}