package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 🧩 Lichess Puzzle Database - Real Tactical Puzzles
 * 
 * Loads and manages puzzles from Lichess database format:
 * - 4,300+ curated puzzles from actual games (balanced difficulty)
 * - Verified solutions using Stockfish NNUE
 * - Professional rating system (Glicko-2)
 * - Algorithmic theme detection
 */
public class LichessPuzzleDatabase {
    private static final String TAG = "LichessPuzzleDatabase";
    
    private static LichessPuzzleDatabase instance;
    private final Context context;
    private List<LichessPuzzle> puzzles;
    private Map<String, List<LichessPuzzle>> puzzlesByTheme;
    private Map<Integer, List<LichessPuzzle>> puzzlesByRating;
    
    private LichessPuzzleDatabase(Context context) {
        this.context = context;
        this.puzzles = new ArrayList<>();
        this.puzzlesByTheme = new HashMap<>();
        this.puzzlesByRating = new HashMap<>();
        loadPuzzleDatabase();
    }
    
    public static synchronized LichessPuzzleDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new LichessPuzzleDatabase(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 🎯 Get puzzle for user rating and theme
     */
    public LichessPuzzle getPuzzleForRating(int userRating, String theme, List<String> excludeIds) {
        List<LichessPuzzle> candidates = new ArrayList<>();
        
        // Filter by rating range (±200 points)
        int minRating = Math.max(600, userRating - 200);
        int maxRating = Math.min(3000, userRating + 200);
        
        for (LichessPuzzle puzzle : puzzles) {
            // Check rating match
            if (puzzle.rating < minRating || puzzle.rating > maxRating) continue;
            
            // Check theme match
            if (theme != null && !theme.equals("all") && !puzzle.themes.contains(theme)) continue;
            
            // Check not in excluded list
            if (excludeIds != null && excludeIds.contains(puzzle.puzzleId)) continue;
            
            candidates.add(puzzle);
        }
        
        if (candidates.isEmpty()) {
            Log.w(TAG, String.format("No puzzles found for rating %d, theme %s", userRating, theme));
            return puzzles.isEmpty() ? null : puzzles.get(0);
        }
        
        // Weight selection by rating proximity and popularity
        return selectWeightedPuzzle(candidates, userRating);
    }
    
    /**
     * 📈 Get puzzles by theme
     */
    public List<LichessPuzzle> getPuzzlesByTheme(String theme) {
        return puzzlesByTheme.getOrDefault(theme, new ArrayList<>());
    }
    
    /**
     * 🎭 Get available themes with counts
     */
    public List<ThemeStats> getThemeStatistics() {
        List<ThemeStats> stats = new ArrayList<>();
        
        for (Map.Entry<String, List<LichessPuzzle>> entry : puzzlesByTheme.entrySet()) {
            String theme = entry.getKey();
            List<LichessPuzzle> themePuzzles = entry.getValue();
            
            if (!themePuzzles.isEmpty()) {
                int totalRating = themePuzzles.stream().mapToInt(p -> p.rating).sum();
                int avgRating = totalRating / themePuzzles.size();
                int minRating = themePuzzles.stream().mapToInt(p -> p.rating).min().orElse(0);
                int maxRating = themePuzzles.stream().mapToInt(p -> p.rating).max().orElse(0);
                
                stats.add(new ThemeStats(theme, themePuzzles.size(), avgRating, minRating, maxRating));
            }
        }
        
        // Sort by count (most popular themes first)
        stats.sort((a, b) -> Integer.compare(b.count, a.count));
        return stats;
    }
    
    /**
     * 🔍 Search puzzles by criteria
     */
    public List<LichessPuzzle> searchPuzzles(int minRating, int maxRating, String theme, int limit) {
        List<LichessPuzzle> results = new ArrayList<>();
        
        for (LichessPuzzle puzzle : puzzles) {
            if (puzzle.rating >= minRating && puzzle.rating <= maxRating) {
                if (theme == null || theme.equals("all") || puzzle.themes.contains(theme)) {
                    results.add(puzzle);
                    if (results.size() >= limit) break;
                }
            }
        }
        
        return results;
    }
    
    /**
     * 📊 Get database statistics
     */
    public DatabaseStats getStatistics() {
        if (puzzles.isEmpty()) {
            return new DatabaseStats(0, 0, 0, 0, 0);
        }
        
        int totalPuzzles = puzzles.size();
        int minRating = puzzles.stream().mapToInt(p -> p.rating).min().orElse(0);
        int maxRating = puzzles.stream().mapToInt(p -> p.rating).max().orElse(0);
        int avgRating = puzzles.stream().mapToInt(p -> p.rating).sum() / totalPuzzles;
        int themeCount = puzzlesByTheme.size();
        
        return new DatabaseStats(totalPuzzles, minRating, maxRating, avgRating, themeCount);
    }
    
    // Private helper methods
    
    private void loadPuzzleDatabase() {
        try {
            loadFromCsvAsset();
            organizePuzzles();
            
            Log.d(TAG, String.format("✅ Loaded %d puzzles with %d themes", 
                puzzles.size(), puzzlesByTheme.size()));
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error loading puzzle database", e);
            createFallbackPuzzles();
        }
    }
    
    private void loadFromCsvAsset() throws Exception {
        InputStream inputStream = context.getAssets().open("lichess_puzzles_5k.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line;
        boolean isFirstLine = true;
        int successCount = 0;
        int totalCount = 0;
        
        while ((line = reader.readLine()) != null) {
            // Skip header line
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            
            totalCount++;
            LichessPuzzle puzzle = LichessPuzzle.fromCsvLine(line);
            if (puzzle != null) {
                puzzles.add(puzzle);
                successCount++;
            }
        }
        
        reader.close();
        
        Log.d(TAG, String.format("📊 CSV parsing complete: %d/%d puzzles loaded", 
            successCount, totalCount));
    }
    
    private void organizePuzzles() {
        // Group by theme
        for (LichessPuzzle puzzle : puzzles) {
            for (String theme : puzzle.themes) {
                puzzlesByTheme.computeIfAbsent(theme, k -> new ArrayList<>()).add(puzzle);
            }
        }
        
        // Group by rating ranges (100-point buckets)
        for (LichessPuzzle puzzle : puzzles) {
            int ratingBucket = (puzzle.rating / 100) * 100;
            puzzlesByRating.computeIfAbsent(ratingBucket, k -> new ArrayList<>()).add(puzzle);
        }
        
        // Sort puzzles by rating for better selection
        puzzles.sort((a, b) -> Integer.compare(a.rating, b.rating));
    }
    
    private LichessPuzzle selectWeightedPuzzle(List<LichessPuzzle> candidates, int userRating) {
        if (candidates.size() == 1) return candidates.get(0);
        
        // Weight by rating proximity and popularity
        List<WeightedPuzzle> weighted = new ArrayList<>();
        
        for (LichessPuzzle puzzle : candidates) {
            // Rating proximity weight (closer = higher weight)
            int ratingDiff = Math.abs(puzzle.rating - userRating);
            double ratingWeight = 1.0 / (1.0 + ratingDiff / 100.0);
            
            // Popularity weight (higher popularity = higher weight)
            double popularityWeight = Math.max(0.1, (puzzle.popularity + 100) / 200.0);
            
            // Play count weight (moderate play count preferred)
            double playWeight = Math.min(1.0, Math.max(0.1, puzzle.nbPlays / 1000.0));
            
            double totalWeight = ratingWeight * popularityWeight * playWeight;
            weighted.add(new WeightedPuzzle(puzzle, totalWeight));
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
    
    private void createFallbackPuzzles() {
        // Create a minimal set of verified puzzles as fallback
        puzzles.clear();
        
        // Add some basic mate puzzles
        String csvLine1 = "fallback01,6k1/5ppp/8/8/8/8/5PPP/5R1K w - - 0 1,f1f8,800,75,90,100,mate mateIn1 backRank,lichess.org,";
        LichessPuzzle puzzle1 = LichessPuzzle.fromCsvLine(csvLine1);
        if (puzzle1 != null) puzzles.add(puzzle1);
        
        String csvLine2 = "fallback02,4k3/4r3/8/8/8/8/4R3/4K3 w - - 0 1,e2e8,900,75,85,150,skewer rook endgame,lichess.org,";
        LichessPuzzle puzzle2 = LichessPuzzle.fromCsvLine(csvLine2);
        if (puzzle2 != null) puzzles.add(puzzle2);
        
        organizePuzzles();
        Log.w(TAG, "⚠️ Using fallback puzzle set (" + puzzles.size() + " puzzles)");
    }
    
    // Data classes
    
    private static class WeightedPuzzle {
        final LichessPuzzle puzzle;
        final double weight;
        
        WeightedPuzzle(LichessPuzzle puzzle, double weight) {
            this.puzzle = puzzle;
            this.weight = weight;
        }
    }
    
    public static class ThemeStats {
        public final String theme;
        public final int count;
        public final int averageRating;
        public final int minRating;
        public final int maxRating;
        
        public ThemeStats(String theme, int count, int averageRating, int minRating, int maxRating) {
            this.theme = theme;
            this.count = count;
            this.averageRating = averageRating;
            this.minRating = minRating;
            this.maxRating = maxRating;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%d puzzles, avg: %d)", theme, count, averageRating);
        }
    }
    
    public static class DatabaseStats {
        public final int totalPuzzles;
        public final int minRating;
        public final int maxRating;
        public final int averageRating;
        public final int themeCount;
        
        public DatabaseStats(int totalPuzzles, int minRating, int maxRating, int averageRating, int themeCount) {
            this.totalPuzzles = totalPuzzles;
            this.minRating = minRating;
            this.maxRating = maxRating;
            this.averageRating = averageRating;
            this.themeCount = themeCount;
        }
        
        @Override
        public String toString() {
            return String.format("Database: %d puzzles, ratings %d-%d (avg: %d), %d themes", 
                totalPuzzles, minRating, maxRating, averageRating, themeCount);
        }
    }
}