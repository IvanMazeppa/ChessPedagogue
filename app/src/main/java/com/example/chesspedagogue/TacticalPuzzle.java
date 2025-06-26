package com.example.chesspedagogue;

import java.util.List;

/**
 * 🧩 TacticalPuzzle - Individual Chess Puzzle Data Structure
 * 
 * Represents a single tactical chess puzzle with solution(s),
 * difficulty rating, thematic categorization, and educational content.
 */
public class TacticalPuzzle {
    public final String id;                    // Unique identifier
    public final String fen;                   // Position in FEN notation
    public final String primarySolution;       // Main correct move
    public final List<String> alternativeSolutions; // Other acceptable moves
    public final String theme;                 // Tactical theme (pin, fork, etc.)
    public final int difficulty;               // 1-5 star difficulty rating
    public final String description;           // User-friendly description
    public final String explanation;           // Educational explanation
    
    // Optional metadata
    public final String source;                // Game source (if from historical game)
    public final String masterStyle;          // Associated chess master
    public final boolean isFromMasterGame;     // True if from actual master game
    
    /**
     * Primary constructor for tactical puzzles
     */
    public TacticalPuzzle(String id, String fen, String primarySolution, 
                         List<String> alternativeSolutions, String theme, 
                         int difficulty, String description, String explanation) {
        this.id = id;
        this.fen = fen;
        this.primarySolution = primarySolution;
        this.alternativeSolutions = alternativeSolutions != null ? alternativeSolutions : new java.util.ArrayList<>();
        this.theme = theme;
        this.difficulty = Math.max(1, Math.min(5, difficulty)); // Clamp to 1-5
        this.description = description;
        this.explanation = explanation;
        
        // Default metadata
        this.source = "curated";
        this.masterStyle = null;
        this.isFromMasterGame = false;
    }
    
    /**
     * Extended constructor for master-specific puzzles
     */
    public TacticalPuzzle(String id, String fen, String primarySolution, 
                         List<String> alternativeSolutions, String theme, 
                         int difficulty, String description, String explanation,
                         String source, String masterStyle) {
        this.id = id;
        this.fen = fen;
        this.primarySolution = primarySolution;
        this.alternativeSolutions = alternativeSolutions != null ? alternativeSolutions : new java.util.ArrayList<>();
        this.theme = theme;
        this.difficulty = Math.max(1, Math.min(5, difficulty));
        this.description = description;
        this.explanation = explanation;
        this.source = source;
        this.masterStyle = masterStyle;
        this.isFromMasterGame = masterStyle != null;
    }
    
    /**
     * Get difficulty as stars for display
     */
    public String getDifficultyStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < difficulty; i++) {
            stars.append("⭐");
        }
        return stars.toString();
    }
    
    /**
     * Get theme emoji for display
     */
    public String getThemeEmoji() {
        switch (theme.toLowerCase()) {
            case "pin": return "📌";
            case "fork": return "🍴";
            case "skewer": return "🍢";
            case "discovery": return "🔍";
            case "deflection": return "🎯";
            case "decoy": return "🪤";
            case "sacrifice": return "💥";
            case "attack": return "⚔️";
            case "breakthrough": return "🚀";
            case "endgame": return "👑";
            case "combination": return "🎭";
            default: return "🧩";
        }
    }
    
    /**
     * Get display title for UI
     */
    public String getDisplayTitle() {
        return String.format("%s %s • %s", 
            getThemeEmoji(), 
            theme.toUpperCase(), 
            getDifficultyStars());
    }
    
    /**
     * Check if this puzzle matches search criteria
     */
    public boolean matches(String searchTheme, int searchDifficulty, String masterFilter) {
        boolean matchesTheme = searchTheme == null || searchTheme.equals("all") || 
                              theme.equalsIgnoreCase(searchTheme);
        
        boolean matchesDifficulty = searchDifficulty == 0 || difficulty == searchDifficulty;
        
        boolean matchesMaster = masterFilter == null || masterFilter.equals("all") ||
                               (masterStyle != null && masterStyle.equalsIgnoreCase(masterFilter));
        
        return matchesTheme && matchesDifficulty && matchesMaster;
    }
    
    /**
     * Get total number of acceptable solutions
     */
    public int getSolutionCount() {
        return 1 + alternativeSolutions.size(); // Primary + alternatives
    }
    
    /**
     * Check if a move is a valid solution
     */
    public boolean isValidSolution(String move) {
        if (move == null) return false;
        
        if (move.equals(primarySolution)) return true;
        
        for (String altSolution : alternativeSolutions) {
            if (move.equals(altSolution)) return true;
        }
        
        return false;
    }
    
    /**
     * Get hint for solving the puzzle
     */
    public String getHint() {
        return String.format("Look for a %s! %s", theme, 
            description.toLowerCase().contains("white") ? "White to move." : "Find the best move.");
    }
    
    @Override
    public String toString() {
        return String.format("TacticalPuzzle{id='%s', theme='%s', difficulty=%d, solution='%s'}", 
            id, theme, difficulty, primarySolution);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TacticalPuzzle puzzle = (TacticalPuzzle) obj;
        return id.equals(puzzle.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}