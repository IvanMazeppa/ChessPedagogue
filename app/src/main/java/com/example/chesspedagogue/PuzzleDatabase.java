package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 🧩 Professional Puzzle Database System
 * 
 * Features:
 * - Import from standard puzzle formats (Lichess, Chess.com style)
 * - ELO-based puzzle rating system
 * - Algorithmic puzzle selection based on user performance
 * - Assistant validation metrics
 * - Statistics tracking similar to chess.com/lichess
 */
public class PuzzleDatabase {
    private static final String TAG = "PuzzleDatabase";
    
    private static PuzzleDatabase instance;
    private final Context context;
    private List<StandardPuzzle> puzzles;
    private PuzzleRatingSystem ratingSystem;
    
    private PuzzleDatabase(Context context) {
        this.context = context;
        this.puzzles = new ArrayList<>();
        this.ratingSystem = new PuzzleRatingSystem();
        loadPuzzleDatabase();
    }
    
    public static synchronized PuzzleDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new PuzzleDatabase(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 📊 Get puzzle by user rating (ELO-based selection)
     */
    public StandardPuzzle getPuzzleForRating(int userRating, String theme) {
        return getPuzzleForRating(userRating, theme, new ArrayList<>());
    }
    
    /**
     * 📊 Get puzzle by user rating (ELO-based selection) excluding recent puzzles
     */
    public StandardPuzzle getPuzzleForRating(int userRating, String theme, List<String> excludePuzzleIds) {
        List<StandardPuzzle> candidatePuzzles = new ArrayList<>();
        
        // Select puzzles within ±200 rating points of user
        int minRating = Math.max(800, userRating - 200);
        int maxRating = Math.min(3000, userRating + 200);
        
        for (StandardPuzzle puzzle : puzzles) {
            boolean ratingMatch = puzzle.rating >= minRating && puzzle.rating <= maxRating;
            boolean themeMatch = theme == null || theme.equals("all") || puzzle.themes.contains(theme);
            boolean notRecent = excludePuzzleIds == null || !excludePuzzleIds.contains(puzzle.id);
            
            if (ratingMatch && themeMatch && notRecent) {
                candidatePuzzles.add(puzzle);
            }
        }
        
        if (candidatePuzzles.isEmpty()) {
            Log.w(TAG, String.format("No new puzzles found for rating %d, theme %s (excluded: %d recent)", 
                userRating, theme, excludePuzzleIds != null ? excludePuzzleIds.size() : 0));
            
            // If no candidates due to recent exclusions, try without exclusions
            if (excludePuzzleIds != null && !excludePuzzleIds.isEmpty()) {
                Log.d(TAG, "🔄 Retrying puzzle selection without recent exclusions");
                return getPuzzleForRating(userRating, theme, new ArrayList<>());
            }
            
            return !puzzles.isEmpty() ? puzzles.get(0) : null;
        }
        
        // Weight selection toward puzzles closer to user rating
        return selectWeightedPuzzle(candidatePuzzles, userRating);
    }
    
    /**
     * 🎯 Validate assistant performance on specific puzzle
     */
    public ValidationResult validateAssistant(StandardPuzzle puzzle, String assistantMove, long solveTimeMs) {
        boolean isCorrect = puzzle.isCorrectSolution(assistantMove);
        int expectedRating = puzzle.rating;
        
        // Calculate assistant performance rating
        int performanceRating = ratingSystem.calculatePerformance(expectedRating, isCorrect, solveTimeMs);
        
        return new ValidationResult(
            puzzle.id,
            assistantMove,
            puzzle.solution,
            isCorrect,
            solveTimeMs,
            expectedRating,
            performanceRating,
            puzzle.themes
        );
    }
    
    /**
     * 📈 Get available themes with puzzle counts
     */
    public List<ThemeStats> getThemeStatistics() {
        List<ThemeStats> themeStats = new ArrayList<>();
        
        // Count puzzles by theme
        for (StandardPuzzle puzzle : puzzles) {
            for (String theme : puzzle.themes) {
                ThemeStats existing = findThemeStats(themeStats, theme);
                if (existing != null) {
                    existing.count++;
                    existing.averageRating = (existing.averageRating * (existing.count - 1) + puzzle.rating) / existing.count;
                } else {
                    themeStats.add(new ThemeStats(theme, 1, puzzle.rating, puzzle.rating, puzzle.rating));
                }
            }
        }
        
        return themeStats;
    }
    
    /**
     * 🔍 Search puzzles by specific criteria
     */
    public List<StandardPuzzle> searchPuzzles(SearchCriteria criteria) {
        List<StandardPuzzle> results = new ArrayList<>();
        
        for (StandardPuzzle puzzle : puzzles) {
            if (matchesCriteria(puzzle, criteria)) {
                results.add(puzzle);
            }
        }
        
        return results;
    }
    
    // Private helper methods
    
    private void loadPuzzleDatabase() {
        try {
            // Try to load from assets first, then from network if needed
            loadFromAssets();
            
            Log.d(TAG, "✅ Loaded " + puzzles.size() + " puzzles from database");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error loading puzzle database", e);
            // Fallback to minimal hardcoded set
            createFallbackPuzzles();
        }
    }
    
    private void loadFromAssets() throws Exception {
        // Load puzzle data from JSON file in assets
        InputStream inputStream = context.getAssets().open("tactical_puzzles.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        reader.close();
        
        // Parse JSON puzzle data
        JSONObject jsonData = new JSONObject(jsonBuilder.toString());
        JSONArray puzzleArray = jsonData.getJSONArray("puzzles");
        
        int totalPuzzles = puzzleArray.length();
        int successfullyParsed = 0;
        
        for (int i = 0; i < puzzleArray.length(); i++) {
            JSONObject puzzleJson = puzzleArray.getJSONObject(i);
            StandardPuzzle puzzle = StandardPuzzle.fromJson(puzzleJson);
            if (puzzle != null) {
                puzzles.add(puzzle);
                successfullyParsed++;
            }
        }
        
        Log.d(TAG, String.format("📊 JSON parsing complete: %d/%d puzzles loaded successfully", 
            successfullyParsed, totalPuzzles));
        
        if (successfullyParsed < totalPuzzles) {
            Log.w(TAG, String.format("⚠️ %d puzzles failed to parse - check JSON format", 
                totalPuzzles - successfullyParsed));
        }
    }
    
    private void createFallbackPuzzles() {
        // Minimal fallback set with proper ratings
        puzzles.add(new StandardPuzzle(
            "basic_001",
            "6k1/5ppp/8/8/8/8/5PPP/5RK1 w - - 0 1",
            "Rf8#",
            List.of("mate", "back-rank"),
            1200,
            "Back rank mate - beginner level"
        ));
        
        puzzles.add(new StandardPuzzle(
            "basic_002", 
            "4k3/4r3/8/8/8/8/4R3/4K3 w - - 0 1",
            "Re8+",
            List.of("skewer", "endgame"),
            1400,
            "Simple rook skewer"
        ));
        
        puzzles.add(new StandardPuzzle(
            "basic_003",
            "rnbqkb1r/pppp1ppp/8/4p3/4P3/3P1N2/PPP2PPP/RNBQKB1R w KQkq - 0 4",
            "Ng5",
            List.of("fork", "opening"),
            1600,
            "Knight fork in opening"
        ));
        
        Log.d(TAG, "⚠️ Using fallback puzzle set (" + puzzles.size() + " puzzles)");
    }
    
    private StandardPuzzle selectWeightedPuzzle(List<StandardPuzzle> candidates, int userRating) {
        // Weight puzzles by how close they are to user rating
        List<WeightedPuzzle> weighted = new ArrayList<>();
        
        for (StandardPuzzle puzzle : candidates) {
            int ratingDifference = Math.abs(puzzle.rating - userRating);
            double weight = 1.0 / (1.0 + ratingDifference / 100.0); // Closer rating = higher weight
            weighted.add(new WeightedPuzzle(puzzle, weight));
        }
        
        // Select randomly based on weights
        double totalWeight = weighted.stream().mapToDouble(wp -> wp.weight).sum();
        double random = new Random().nextDouble() * totalWeight;
        
        double currentWeight = 0;
        for (WeightedPuzzle wp : weighted) {
            currentWeight += wp.weight;
            if (random <= currentWeight) {
                return wp.puzzle;
            }
        }
        
        // Fallback to first candidate
        return candidates.get(0);
    }
    
    private ThemeStats findThemeStats(List<ThemeStats> stats, String theme) {
        for (ThemeStats stat : stats) {
            if (stat.theme.equals(theme)) {
                return stat;
            }
        }
        return null;
    }
    
    private boolean matchesCriteria(StandardPuzzle puzzle, SearchCriteria criteria) {
        if (criteria.minRating != null && puzzle.rating < criteria.minRating) return false;
        if (criteria.maxRating != null && puzzle.rating > criteria.maxRating) return false;
        if (criteria.theme != null && !puzzle.themes.contains(criteria.theme)) return false;
        if (criteria.excludeThemes != null) {
            for (String excludeTheme : criteria.excludeThemes) {
                if (puzzle.themes.contains(excludeTheme)) return false;
            }
        }
        return true;
    }
    
    // Helper classes
    
    private static class WeightedPuzzle {
        final StandardPuzzle puzzle;
        final double weight;
        
        WeightedPuzzle(StandardPuzzle puzzle, double weight) {
            this.puzzle = puzzle;
            this.weight = weight;
        }
    }
    
    public static class ThemeStats {
        public final String theme;
        public int count;
        public int averageRating;
        public final int minRating;
        public final int maxRating;
        
        ThemeStats(String theme, int count, int averageRating, int minRating, int maxRating) {
            this.theme = theme;
            this.count = count;
            this.averageRating = averageRating;
            this.minRating = minRating;
            this.maxRating = maxRating;
        }
    }
    
    public static class SearchCriteria {
        public Integer minRating;
        public Integer maxRating;
        public String theme;
        public List<String> excludeThemes;
        
        public SearchCriteria() {}
    }
    
    public static class ValidationResult {
        public final String puzzleId;
        public final String assistantMove;
        public final String correctMove;
        public final boolean isCorrect;
        public final long solveTimeMs;
        public final int puzzleRating;
        public final int performanceRating;
        public final List<String> themes;
        
        ValidationResult(String puzzleId, String assistantMove, String correctMove, 
                        boolean isCorrect, long solveTimeMs, int puzzleRating, 
                        int performanceRating, List<String> themes) {
            this.puzzleId = puzzleId;
            this.assistantMove = assistantMove;
            this.correctMove = correctMove;
            this.isCorrect = isCorrect;
            this.solveTimeMs = solveTimeMs;
            this.puzzleRating = puzzleRating;
            this.performanceRating = performanceRating;
            this.themes = themes;
        }
        
        public String getPerformanceDescription() {
            if (performanceRating >= puzzleRating + 100) return "Excellent";
            if (performanceRating >= puzzleRating) return "Good";
            if (performanceRating >= puzzleRating - 100) return "Average";
            return "Below Expected";
        }
    }
}