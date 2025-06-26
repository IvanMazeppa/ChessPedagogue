package com.example.chesspedagogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 📊 PuzzleStats - Performance Statistics for Tactical Puzzles
 * 
 * Tracks comprehensive performance metrics for users and AI assistants
 * across different puzzle themes, difficulty levels, and time periods.
 */
public class PuzzleStats {
    public int puzzlesSolved = 0;
    public int correctSolutions = 0;
    public long totalTime = 0; // Total time in milliseconds
    public List<PuzzleResult> recentResults = new ArrayList<>();
    
    // Theme-specific performance
    public Map<String, Integer> themeCorrect = new HashMap<>();
    public Map<String, Integer> themeTotal = new HashMap<>();
    
    // Difficulty tracking
    public Map<Integer, Integer> difficultyCorrect = new HashMap<>();
    public Map<Integer, Integer> difficultyTotal = new HashMap<>();
    
    // Streak tracking
    public int currentStreak = 0;
    public int bestStreak = 0;
    public int streaksOf5Plus = 0;
    public int streaksOf10Plus = 0;
    
    // Time-based metrics
    public long fastestSolve = Long.MAX_VALUE;
    public long slowestSolve = 0;
    public double averageTime = 0.0;
    
    // Rating/Elo system
    public int tacticalRating = 1200; // Starting rating
    public int peakRating = 1200;
    public List<Integer> ratingHistory = new ArrayList<>();
    
    // Additional computed fields
    public double accuracy = 0.0; // Computed from correctSolutions/puzzlesSolved
    public double averageDifficulty = 3.0; // Average difficulty of solved puzzles
    
    // Achievement tracking
    public Map<String, Boolean> achievements = new HashMap<>();
    public int totalPoints = 0;
    
    public PuzzleStats() {
        initializeAchievements();
    }
    
    /**
     * 📈 Get accuracy percentage
     */
    public double getAccuracy() {
        if (puzzlesSolved == 0) return 0.0;
        return (double) correctSolutions / puzzlesSolved;
    }
    
    /**
     * ⏱️ Get average solve time in seconds
     */
    public double getAverageTimeSeconds() {
        if (puzzlesSolved == 0) return 0.0;
        return (totalTime / 1000.0) / puzzlesSolved;
    }
    
    /**
     * 🎯 Get theme-specific accuracy
     */
    public double getThemeAccuracy(String theme) {
        int correct = themeCorrect.getOrDefault(theme, 0);
        int total = themeTotal.getOrDefault(theme, 0);
        if (total == 0) return 0.0;
        return (double) correct / total;
    }
    
    /**
     * ⭐ Get difficulty-specific accuracy
     */
    public double getDifficultyAccuracy(int difficulty) {
        int correct = difficultyCorrect.getOrDefault(difficulty, 0);
        int total = difficultyTotal.getOrDefault(difficulty, 0);
        if (total == 0) return 0.0;
        return (double) correct / total;
    }
    
    /**
     * 📊 Update stats with new puzzle result
     */
    public void updateWithResult(PuzzleResult result, TacticalPuzzle puzzle) {
        // Basic stats
        puzzlesSolved++;
        if (result.correct) correctSolutions++;
        totalTime += result.solveTime;
        
        // Update computed fields
        accuracy = getAccuracy();
        averageDifficulty = calculateAverageDifficulty(puzzle.difficulty);
        
        // Theme tracking
        String theme = puzzle.theme;
        themeTotal.put(theme, themeTotal.getOrDefault(theme, 0) + 1);
        if (result.correct) {
            themeCorrect.put(theme, themeCorrect.getOrDefault(theme, 0) + 1);
        }
        
        // Difficulty tracking
        int difficulty = puzzle.difficulty;
        difficultyTotal.put(difficulty, difficultyTotal.getOrDefault(difficulty, 0) + 1);
        if (result.correct) {
            difficultyCorrect.put(difficulty, difficultyCorrect.getOrDefault(difficulty, 0) + 1);
        }
        
        // Streak tracking
        if (result.correct) {
            currentStreak++;
            bestStreak = Math.max(bestStreak, currentStreak);
            
            if (currentStreak >= 5) streaksOf5Plus++;
            if (currentStreak >= 10) streaksOf10Plus++;
        } else {
            currentStreak = 0;
        }
        
        // Time tracking
        if (result.correct) {
            fastestSolve = Math.min(fastestSolve, result.solveTime);
            slowestSolve = Math.max(slowestSolve, result.solveTime);
        }
        
        // Recent results (keep last 20)
        recentResults.add(result);
        if (recentResults.size() > 20) {
            recentResults.remove(0);
        }
        
        // Update rating
        updateRating(result, puzzle);
        
        // Update points
        updatePoints(result, puzzle);
        
        // Check achievements
        checkAchievements();
    }
    
    /**
     * 🏆 Update tactical rating (Elo-like system)
     */
    private void updateRating(PuzzleResult result, TacticalPuzzle puzzle) {
        // Simple rating adjustment based on puzzle difficulty and performance
        int baseChange = puzzle.difficulty * 5; // 5-25 points base
        
        if (result.correct) {
            // Bonus for fast solutions
            double timeBonus = 1.0;
            if (result.solveTime < 10000) timeBonus = 1.5; // Under 10 seconds
            else if (result.solveTime < 30000) timeBonus = 1.2; // Under 30 seconds
            
            tacticalRating += (int) (baseChange * timeBonus);
        } else {
            tacticalRating -= baseChange / 2; // Smaller penalty for incorrect
        }
        
        tacticalRating = Math.max(800, Math.min(2800, tacticalRating)); // Bounds
        peakRating = Math.max(peakRating, tacticalRating);
        ratingHistory.add(tacticalRating);
        
        // Keep rating history manageable
        if (ratingHistory.size() > 100) {
            ratingHistory.remove(0);
        }
    }
    
    /**
     * 💰 Update points based on performance
     */
    private void updatePoints(PuzzleResult result, TacticalPuzzle puzzle) {
        if (!result.correct) return;
        
        int points = puzzle.difficulty * 10; // Base points
        
        // Time bonuses
        if (result.solveTime < 5000) points += 20; // Lightning fast
        else if (result.solveTime < 15000) points += 10; // Quick
        
        // Streak bonuses
        if (currentStreak >= 5) points += 5;
        if (currentStreak >= 10) points += 10;
        
        // Difficulty multipliers
        if (puzzle.difficulty >= 4) points += 15; // Hard puzzles
        if (puzzle.difficulty == 5) points += 25; // Expert puzzles
        
        totalPoints += points;
    }
    
    /**
     * 🏅 Check and unlock achievements
     */
    private void checkAchievements() {
        // Solving achievements
        setAchievement("first_solve", puzzlesSolved >= 1);
        setAchievement("puzzle_novice", puzzlesSolved >= 10);
        setAchievement("puzzle_solver", puzzlesSolved >= 50);
        setAchievement("puzzle_master", puzzlesSolved >= 100);
        setAchievement("puzzle_grandmaster", puzzlesSolved >= 500);
        
        // Accuracy achievements
        double accuracy = getAccuracy();
        setAchievement("sharp_eye", accuracy >= 0.8 && puzzlesSolved >= 20);
        setAchievement("tactical_genius", accuracy >= 0.9 && puzzlesSolved >= 50);
        
        // Streak achievements
        setAchievement("on_fire", bestStreak >= 5);
        setAchievement("unstoppable", bestStreak >= 10);
        setAchievement("legendary", bestStreak >= 20);
        
        // Speed achievements
        if (fastestSolve < 5000 && fastestSolve != Long.MAX_VALUE) {
            setAchievement("lightning_fast", true);
        }
        if (fastestSolve < 2000 && fastestSolve != Long.MAX_VALUE) {
            setAchievement("speed_demon", true);
        }
        
        // Theme mastery
        for (String theme : themeCorrect.keySet()) {
            if (getThemeAccuracy(theme) >= 0.85 && themeTotal.getOrDefault(theme, 0) >= 10) {
                setAchievement(theme + "_master", true);
            }
        }
        
        // Rating achievements
        setAchievement("rated_player", tacticalRating >= 1400);
        setAchievement("strong_player", tacticalRating >= 1600);
        setAchievement("expert_player", tacticalRating >= 1800);
        setAchievement("master_level", tacticalRating >= 2000);
    }
    
    /**
     * 🎯 Get strongest and weakest themes
     */
    public String getStrongestTheme() {
        String strongest = null;
        double bestAccuracy = 0.0;
        
        for (String theme : themeCorrect.keySet()) {
            if (themeTotal.getOrDefault(theme, 0) >= 3) { // Minimum attempts
                double accuracy = getThemeAccuracy(theme);
                if (accuracy > bestAccuracy) {
                    bestAccuracy = accuracy;
                    strongest = theme;
                }
            }
        }
        
        return strongest;
    }
    
    public String getWeakestTheme() {
        String weakest = null;
        double worstAccuracy = 1.0;
        
        for (String theme : themeCorrect.keySet()) {
            if (themeTotal.getOrDefault(theme, 0) >= 3) { // Minimum attempts
                double accuracy = getThemeAccuracy(theme);
                if (accuracy < worstAccuracy) {
                    worstAccuracy = accuracy;
                    weakest = theme;
                }
            }
        }
        
        return weakest;
    }
    
    /**
     * 📈 Get recent performance trend
     */
    public String getPerformanceTrend() {
        if (recentResults.size() < 5) return "insufficient_data";
        
        int recentCorrect = 0;
        for (int i = recentResults.size() - 5; i < recentResults.size(); i++) {
            if (recentResults.get(i).correct) recentCorrect++;
        }
        
        double recentAccuracy = (double) recentCorrect / 5;
        double overallAccuracy = getAccuracy();
        
        if (recentAccuracy > overallAccuracy + 0.1) return "improving";
        if (recentAccuracy < overallAccuracy - 0.1) return "declining";
        return "stable";
    }
    
    /**
     * 🏆 Get user rank based on rating
     */
    public String getRank() {
        if (tacticalRating >= 2200) return "Grandmaster";
        if (tacticalRating >= 2000) return "Master";
        if (tacticalRating >= 1800) return "Expert";
        if (tacticalRating >= 1600) return "Advanced";
        if (tacticalRating >= 1400) return "Intermediate";
        if (tacticalRating >= 1200) return "Beginner";
        return "Novice";
    }
    
    /**
     * 📊 Calculate average difficulty of solved puzzles
     */
    private double calculateAverageDifficulty(int newDifficulty) {
        if (puzzlesSolved <= 1) {
            return newDifficulty;
        }
        
        // Simple running average calculation
        double currentTotal = averageDifficulty * (puzzlesSolved - 1);
        return (currentTotal + newDifficulty) / puzzlesSolved;
    }
    
    /**
     * 📊 Get performance summary for display
     */
    public String getPerformanceSummary() {
        return String.format(
            "📊 %d solved (%.1f%% accuracy) • ⏱️ %.1fs avg • 🏆 %d rating (%s) • 🔥 %d streak",
            puzzlesSolved,
            getAccuracy() * 100,
            getAverageTimeSeconds(),
            tacticalRating,
            getRank(),
            currentStreak
        );
    }
    
    private void initializeAchievements() {
        achievements.put("first_solve", false);
        achievements.put("puzzle_novice", false);
        achievements.put("puzzle_solver", false);
        achievements.put("puzzle_master", false);
        achievements.put("on_fire", false);
        achievements.put("sharp_eye", false);
        achievements.put("lightning_fast", false);
        // Add more as needed...
    }
    
    private void setAchievement(String key, boolean unlocked) {
        achievements.put(key, unlocked);
    }
    
    public boolean hasAchievement(String key) {
        return achievements.getOrDefault(key, false);
    }
    
    /**
     * 📊 Get comprehensive stats for display
     */
    @Override
    public String toString() {
        return String.format(
            "PuzzleStats{solved=%d, accuracy=%.1f%%, rating=%d, streak=%d}",
            puzzlesSolved, getAccuracy() * 100, tacticalRating, currentStreak
        );
    }
}