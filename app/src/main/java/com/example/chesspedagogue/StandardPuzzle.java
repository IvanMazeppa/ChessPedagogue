package com.example.chesspedagogue;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 🧩 Standard Puzzle Format (Chess.com/Lichess compatible)
 * 
 * Represents a tactical puzzle with:
 * - ELO rating for difficulty
 * - Multiple theme tags
 * - Alternative solutions
 * - Performance metrics
 */
public class StandardPuzzle {
    public final String id;
    public final String fen;
    public final String solution;
    public final List<String> alternativeSolutions;
    public final List<String> themes;
    public final int rating;
    public final String description;
    public final int popularity;
    public final double solutionRate;
    
    public StandardPuzzle(String id, String fen, String solution, List<String> themes, 
                         int rating, String description) {
        this(id, fen, solution, new ArrayList<>(), themes, rating, description, 0, 0.0);
    }
    
    public StandardPuzzle(String id, String fen, String solution, List<String> alternativeSolutions,
                         List<String> themes, int rating, String description, 
                         int popularity, double solutionRate) {
        this.id = id;
        this.fen = fen;
        this.solution = solution;
        this.alternativeSolutions = alternativeSolutions != null ? alternativeSolutions : new ArrayList<>();
        this.themes = themes != null ? themes : new ArrayList<>();
        this.rating = rating;
        this.description = description;
        this.popularity = popularity;
        this.solutionRate = solutionRate;
    }
    
    /**
     * ✅ Check if a move is a correct solution
     */
    public boolean isCorrectSolution(String move) {
        if (move == null) return false;
        
        // Normalize move (remove check/mate symbols)
        String normalizedMove = move.replaceAll("[+#]", "").trim();
        String normalizedSolution = solution.replaceAll("[+#]", "").trim();
        
        // Check primary solution
        if (normalizedMove.equals(normalizedSolution)) {
            return true;
        }
        
        // Check alternative solutions
        for (String altSolution : alternativeSolutions) {
            String normalizedAlt = altSolution.replaceAll("[+#]", "").trim();
            if (normalizedMove.equals(normalizedAlt)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 🎯 Get difficulty description
     */
    public String getDifficultyDescription() {
        if (rating < 1000) return "Beginner";
        if (rating < 1300) return "Easy";
        if (rating < 1600) return "Medium";
        if (rating < 1900) return "Hard";
        if (rating < 2200) return "Expert";
        return "Master";
    }
    
    /**
     * 🏷️ Get primary theme
     */
    public String getPrimaryTheme() {
        return themes.isEmpty() ? "tactical" : themes.get(0);
    }
    
    /**
     * 📊 Get puzzle statistics summary
     */
    public String getStatsDescription() {
        return String.format("Rating: %d | Solved: %.1f%% | Theme: %s", 
            rating, solutionRate * 100, getPrimaryTheme());
    }
    
    /**
     * 🔄 Create from JSON format (Lichess/Chess.com style)
     */
    public static StandardPuzzle fromJson(JSONObject json) {
        try {
            String id = json.getString("id");
            String fen = json.getString("fen");
            String solution = json.getString("solution");
            
            // Parse themes
            List<String> themes = new ArrayList<>();
            if (json.has("themes")) {
                JSONArray themesArray = json.getJSONArray("themes");
                for (int i = 0; i < themesArray.length(); i++) {
                    themes.add(themesArray.getString(i));
                }
            }
            
            // Parse alternative solutions
            List<String> alternatives = new ArrayList<>();
            if (json.has("alternatives")) {
                JSONArray altArray = json.getJSONArray("alternatives");
                for (int i = 0; i < altArray.length(); i++) {
                    alternatives.add(altArray.getString(i));
                }
            }
            
            int rating = json.optInt("rating", 1500);
            String description = json.optString("description", "Tactical puzzle");
            int popularity = json.optInt("popularity", 0);
            double solutionRate = json.optDouble("solutionRate", 0.5);
            
            return new StandardPuzzle(id, fen, solution, alternatives, themes, 
                                    rating, description, popularity, solutionRate);
                                    
        } catch (Exception e) {
            android.util.Log.e("StandardPuzzle", "❌ Failed to parse puzzle from JSON: " + json.toString(), e);
            return null;
        }
    }
    
    /**
     * 📄 Convert to JSON format
     */
    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("fen", fen);
            json.put("solution", solution);
            json.put("rating", rating);
            json.put("description", description);
            json.put("popularity", popularity);
            json.put("solutionRate", solutionRate);
            
            JSONArray themesArray = new JSONArray();
            for (String theme : themes) {
                themesArray.put(theme);
            }
            json.put("themes", themesArray);
            
            JSONArray altArray = new JSONArray();
            for (String alt : alternativeSolutions) {
                altArray.put(alt);
            }
            json.put("alternatives", altArray);
            
            return json;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
    
    /**
     * 🎮 Get display title for UI
     */
    public String getDisplayTitle() {
        return String.format("%s (%d) - %s", 
            getPrimaryTheme().toUpperCase(), rating, getDifficultyDescription());
    }
    
    /**
     * 💡 Get hint for user
     */
    public String getHint() {
        String primaryTheme = getPrimaryTheme();
        
        switch (primaryTheme.toLowerCase()) {
            case "mate":
            case "mateIn1":
            case "mateIn2":
                return "Look for checkmate! Force the enemy king into a corner.";
            case "fork":
                return "Find a move that attacks two pieces at once.";
            case "pin":
                return "Look for a piece that cannot move without exposing a more valuable piece.";
            case "skewer":
                return "Attack a valuable piece that will expose a less valuable piece behind it.";
            case "discoveredAttack":
                return "Move a piece to reveal an attack from another piece.";
            case "deflection":
                return "Force the defender away from protecting an important square or piece.";
            case "removal":
                return "Eliminate the piece that's defending something important.";
            case "sacrifice":
                return "Consider giving up material for a bigger advantage.";
            case "endgame":
                return "Use king activity and pawn promotion threats.";
            default:
                return "Look for the best tactical move in this position.";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Puzzle[%s] %s (%d) - %s", 
            id, getPrimaryTheme(), rating, description);
    }
}