package com.example.chesspedagogue;

import android.util.Log;

/**
 * 📊 ELO-Style Puzzle Rating System (Chess.com/Lichess compatible)
 * 
 * Features:
 * - User rating progression based on puzzle performance
 * - Assistant validation metrics
 * - Time-based performance bonuses
 * - Adaptive difficulty selection
 */
public class PuzzleRatingSystem {
    private static final String TAG = "PuzzleRatingSystem";
    
    // Rating system constants
    private static final int STARTING_RATING = 1200;
    private static final int K_FACTOR = 32;
    private static final double TIME_BONUS_THRESHOLD = 10.0; // seconds
    private static final double TIME_PENALTY_THRESHOLD = 60.0; // seconds
    
    /**
     * 🎯 Calculate new user rating after puzzle attempt
     */
    public RatingChange calculateUserRatingChange(int currentRating, int puzzleRating, 
                                                 boolean solved, long solveTimeSeconds) {
        // Expected score based on rating difference
        double expectedScore = getExpectedScore(currentRating, puzzleRating);
        double actualScore = solved ? 1.0 : 0.0;
        
        // Base rating change
        int baseChange = (int) Math.round(K_FACTOR * (actualScore - expectedScore));
        
        // Time modifiers
        double timeModifier = calculateTimeModifier(solveTimeSeconds, solved);
        int finalChange = (int) Math.round(baseChange * timeModifier);
        
        // Prevent extreme rating changes
        finalChange = Math.max(-50, Math.min(50, finalChange));
        
        int newRating = Math.max(600, Math.min(3000, currentRating + finalChange));
        
        Log.d(TAG, String.format("Rating change: %d -> %d (%+d) [puzzle:%d, solved:%s, time:%ds]",
            currentRating, newRating, finalChange, puzzleRating, solved, solveTimeSeconds));
        
        return new RatingChange(currentRating, newRating, finalChange, expectedScore, timeModifier);
    }
    
    /**
     * 🤖 Calculate assistant performance rating
     */
    public int calculatePerformance(int puzzleRating, boolean solved, long solveTimeMs) {
        if (!solved) {
            // If unsolved, performance is significantly below puzzle rating
            return Math.max(600, puzzleRating - 200);
        }
        
        double solveTimeSeconds = solveTimeMs / 1000.0;
        
        // Base performance at puzzle rating level
        int performance = puzzleRating;
        
        // Time bonuses/penalties
        if (solveTimeSeconds <= TIME_BONUS_THRESHOLD) {
            // Very fast solve = higher performance
            performance += (int) (100 * (TIME_BONUS_THRESHOLD - solveTimeSeconds) / TIME_BONUS_THRESHOLD);
        } else if (solveTimeSeconds >= TIME_PENALTY_THRESHOLD) {
            // Very slow solve = lower performance
            performance -= (int) (50 * Math.min(solveTimeSeconds - TIME_PENALTY_THRESHOLD, 60) / 60);
        }
        
        return Math.max(600, Math.min(3000, performance));
    }
    
    /**
     * 📈 Get recommended puzzle rating for user
     */
    public int getRecommendedPuzzleRating(int userRating, double recentAccuracy, int puzzlesSolved) {
        int baseRating = userRating;
        
        // Adjust based on recent performance
        if (puzzlesSolved >= 10) {
            if (recentAccuracy > 0.85) {
                baseRating += 100; // Too easy, increase difficulty
            } else if (recentAccuracy < 0.50) {
                baseRating -= 100; // Too hard, decrease difficulty
            }
        }
        
        // Add some randomness for variety (±50 points)
        int variation = (int) (Math.random() * 100) - 50;
        baseRating += variation;
        
        return Math.max(800, Math.min(2800, baseRating));
    }
    
    /**
     * 🏆 Calculate puzzle difficulty tier
     */
    public DifficultyTier getDifficultyTier(int rating) {
        if (rating < 1000) return new DifficultyTier("Beginner", "⭐", "#4CAF50");
        if (rating < 1300) return new DifficultyTier("Easy", "⭐⭐", "#8BC34A");
        if (rating < 1600) return new DifficultyTier("Medium", "⭐⭐⭐", "#FFC107");
        if (rating < 1900) return new DifficultyTier("Hard", "⭐⭐⭐⭐", "#FF9800");
        if (rating < 2200) return new DifficultyTier("Expert", "⭐⭐⭐⭐⭐", "#F44336");
        return new DifficultyTier("Master", "🏆", "#9C27B0");
    }
    
    /**
     * 📊 Calculate session performance statistics
     */
    public SessionStats calculateSessionStats(int startRating, int endRating, 
                                            int puzzlesSolved, int puzzlesCorrect, 
                                            long totalTimeMs) {
        double accuracy = puzzlesSolved > 0 ? (double) puzzlesCorrect / puzzlesSolved : 0.0;
        double averageTime = puzzlesSolved > 0 ? (totalTimeMs / 1000.0) / puzzlesSolved : 0.0;
        int ratingGain = endRating - startRating;
        
        String performance;
        if (accuracy >= 0.8 && ratingGain > 0) performance = "Excellent";
        else if (accuracy >= 0.6 && ratingGain >= 0) performance = "Good";
        else if (accuracy >= 0.4) performance = "Average";
        else performance = "Needs Practice";
        
        return new SessionStats(startRating, endRating, ratingGain, puzzlesSolved, 
                              puzzlesCorrect, accuracy, averageTime, performance);
    }
    
    // Private helper methods
    
    private double getExpectedScore(int playerRating, int puzzleRating) {
        double ratingDifference = puzzleRating - playerRating;
        return 1.0 / (1.0 + Math.pow(10, ratingDifference / 400.0));
    }
    
    private double calculateTimeModifier(long solveTimeSeconds, boolean solved) {
        if (!solved) return 1.0; // No time modifier for incorrect solutions
        
        if (solveTimeSeconds <= TIME_BONUS_THRESHOLD) {
            // Bonus for very fast solves
            return 1.0 + (0.3 * (TIME_BONUS_THRESHOLD - solveTimeSeconds) / TIME_BONUS_THRESHOLD);
        } else if (solveTimeSeconds >= TIME_PENALTY_THRESHOLD) {
            // Penalty for very slow solves
            double penalty = Math.min(0.3, (solveTimeSeconds - TIME_PENALTY_THRESHOLD) / 120.0);
            return 1.0 - penalty;
        }
        
        return 1.0; // No modifier for normal solve times
    }
    
    // Data classes
    
    public static class RatingChange {
        public final int oldRating;
        public final int newRating;
        public final int change;
        public final double expectedScore;
        public final double timeModifier;
        
        RatingChange(int oldRating, int newRating, int change, double expectedScore, double timeModifier) {
            this.oldRating = oldRating;
            this.newRating = newRating;
            this.change = change;
            this.expectedScore = expectedScore;
            this.timeModifier = timeModifier;
        }
        
        public String getDescription() {
            String direction = change >= 0 ? "gained" : "lost";
            return String.format("Rating %s %d points (%d → %d)", 
                direction, Math.abs(change), oldRating, newRating);
        }
    }
    
    public static class DifficultyTier {
        public final String name;
        public final String symbol;
        public final String color;
        
        DifficultyTier(String name, String symbol, String color) {
            this.name = name;
            this.symbol = symbol;
            this.color = color;
        }
        
        @Override
        public String toString() {
            return symbol + " " + name;
        }
    }
    
    public static class SessionStats {
        public final int startRating;
        public final int endRating;
        public final int ratingChange;
        public final int puzzlesSolved;
        public final int puzzlesCorrect;
        public final double accuracy;
        public final double averageTimeSeconds;
        public final String performanceGrade;
        
        SessionStats(int startRating, int endRating, int ratingChange, int puzzlesSolved,
                    int puzzlesCorrect, double accuracy, double averageTimeSeconds, String performanceGrade) {
            this.startRating = startRating;
            this.endRating = endRating;
            this.ratingChange = ratingChange;
            this.puzzlesSolved = puzzlesSolved;
            this.puzzlesCorrect = puzzlesCorrect;
            this.accuracy = accuracy;
            this.averageTimeSeconds = averageTimeSeconds;
            this.performanceGrade = performanceGrade;
        }
        
        public String getSummary() {
            return String.format("Session: %d/%d solved (%.1f%%) | Rating: %d → %d (%+d) | %s",
                puzzlesCorrect, puzzlesSolved, accuracy * 100, 
                startRating, endRating, ratingChange, performanceGrade);
        }
    }
}