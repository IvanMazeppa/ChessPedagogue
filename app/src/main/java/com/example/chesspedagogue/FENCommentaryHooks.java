package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FEN-driven commentary hooks that trigger contextual analysis and commentary
 * based on specific chess positions and patterns.
 * 
 * Integrates with existing PersonalityEngine and EvaluationTracker systems
 * to provide position-specific insights from chess masters.
 */
public class FENCommentaryHooks {
    private static final String TAG = "FENCommentaryHooks";
    
    // Commentary triggers based on position characteristics
    public enum CommentaryTrigger {
        HISTORICAL_POSITION_MATCH,  // Exact or similar position from master's games
        TACTICAL_PATTERN,          // Specific tactical themes (pins, forks, etc.)
        OPENING_THEORY,            // Theoretical opening positions
        ENDGAME_TECHNIQUE,         // Critical endgame positions
        POSITIONAL_THEME,          // Strategic concepts (weak squares, pawn structure)
        EVALUATION_SWING,          // Significant position evaluation changes
        GAME_PHASE_TRANSITION,     // Opening → Middlegame → Endgame
        MASTER_STYLE_PATTERN      // Characteristic playing style detection
    }
    
    // Commentary context data
    public static class CommentaryContext {
        public String currentFEN;
        public String lastMove;
        public String masterName;
        public CommentaryTrigger trigger;
        public Map<String, Object> metadata;
        public List<String> tacticalThemes;
        public String gamePhase;
        public double evaluationChange;
        
        public CommentaryContext(String fen, String move, String master) {
            this.currentFEN = fen;
            this.lastMove = move;
            this.masterName = master;
            this.metadata = new HashMap<>();
            this.tacticalThemes = new ArrayList<>();
        }
    }
    
    // Callback interface for commentary generation
    public interface CommentaryCallback {
        void onCommentaryTriggered(CommentaryContext context, String commentary);
        void onHistoricalMatchFound(CommentaryContext context, GameDatabaseHelper.HistoricalPosition match);
        void onTacticalPatternDetected(CommentaryContext context, List<String> patterns);
    }
    
    private final Context context;
    private final GameDatabaseHelper databaseHelper;
    private final PersonalityEngine personalityEngine;
    private CommentaryCallback callback;
    
    // Position pattern recognition
    private final Map<String, String> tacticalPatterns = new HashMap<>();
    private final Map<String, String> positionalThemes = new HashMap<>();
    
    public FENCommentaryHooks(Context context) {
        this.context = context;
        this.databaseHelper = new GameDatabaseHelper(context);
        // Don't initialize PersonalityEngine here to avoid circular dependency
        this.personalityEngine = null;
        initializePatterns();
    }
    
    /**
     * Set callback for commentary events
     */
    public void setCommentaryCallback(CommentaryCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Main entry point: analyze position and trigger appropriate commentary
     */
    public void analyzePosition(String currentFEN, String lastMove, String masterName, 
                              double currentEvaluation, double previousEvaluation) {
        LogThrottler.d(TAG, "🎯 Analyzing position for commentary hooks: " + masterName);
        
        CommentaryContext context = new CommentaryContext(currentFEN, lastMove, masterName);
        context.evaluationChange = currentEvaluation - previousEvaluation;
        context.gamePhase = determineGamePhase(currentFEN);
        
        // 1. Check for historical position matches
        checkHistoricalPositions(context);
        
        // 2. Analyze tactical patterns
        analyzeTacticalPatterns(context);
        
        // 3. Check for positional themes
        analyzePositionalThemes(context);
        
        // 4. Evaluate significant position changes
        if (Math.abs(context.evaluationChange) > 1.5) {
            context.trigger = CommentaryTrigger.EVALUATION_SWING;
            triggerEvaluationCommentary(context);
        }
        
        // 5. Check for game phase transitions
        checkGamePhaseTransition(context);
        
        // 6. Detect master-specific playing patterns
        detectMasterStylePatterns(context);
    }
    
    /**
     * Check for historical position matches in master's database
     */
    private void checkHistoricalPositions(CommentaryContext context) {
        try {
            // Database verification logging
            LogThrottler.d(TAG, "🔍 DATABASE VERIFICATION - Starting historical position check");
            LogThrottler.d(TAG, "🔍 FEN: " + context.currentFEN);
            LogThrottler.d(TAG, "🔍 Master: " + context.masterName);
            
            // Check if database helper is properly initialized
            if (databaseHelper == null) {
                LogThrottler.e(TAG, "❌ DATABASE ERROR: databaseHelper is null!");
                return;
            }
            LogThrottler.d(TAG, "✅ DATABASE: databaseHelper initialized");
            
            // Check database connection
            try {
                int totalPositions = databaseHelper.getTotalPositionCount();
                LogThrottler.d(TAG, "✅ DATABASE: Total positions in database: " + totalPositions);
                
                int masterPositions = databaseHelper.getMasterPositionCount(context.masterName);
                LogThrottler.d(TAG, "✅ DATABASE: Positions for " + context.masterName + ": " + masterPositions);
                
                if (masterPositions == 0) {
                    LogThrottler.w(TAG, "⚠️ DATABASE WARNING: No positions found for master " + context.masterName);
                }
            } catch (Exception dbCheck) {
                LogThrottler.e(TAG, "❌ DATABASE ERROR: Failed database connection check", dbCheck);
                return;
            }
            
            // Use existing PersonalityEngine logic for position matching
            LogThrottler.d(TAG, "🔍 DATABASE: Calling findSimilarPositions...");
            List<GameDatabaseHelper.HistoricalPosition> similarPositions = 
                databaseHelper.findSimilarPositions(context.currentFEN, context.masterName, 3);
            
            LogThrottler.d(TAG, "🔍 DATABASE: Query returned " + similarPositions.size() + " similar positions");
            
            if (!similarPositions.isEmpty()) {
                LogThrottler.d(TAG, "✅ DATABASE: Found similar positions, processing matches...");
                
                GameDatabaseHelper.HistoricalPosition bestMatch = similarPositions.get(0);
                LogThrottler.d(TAG, "🔍 Best match FEN: " + bestMatch.fen);
                LogThrottler.d(TAG, "🔍 Best match annotation: " + bestMatch.annotation);
                
                // Calculate position similarity (simple comparison for now)
                double similarity = calculatePositionSimilarity(context.currentFEN, bestMatch.fen);
                LogThrottler.d(TAG, "🔍 Calculated similarity: " + similarity);
                
                if (similarity > 0.85) { // High similarity threshold
                    LogThrottler.d(TAG, "✅ HIGH SIMILARITY MATCH: " + similarity + " > 0.85 threshold");
                    
                    context.trigger = CommentaryTrigger.HISTORICAL_POSITION_MATCH;
                    context.metadata.put("historical_game", bestMatch.toString());
                    context.metadata.put("historical_move", "move_from_position");
                    context.metadata.put("similarity", similarity);
                    
                    String commentary = generateHistoricalCommentary(context, bestMatch);
                    LogThrottler.d(TAG, "🔍 Generated commentary: " + commentary);
                    
                    LogThrottler.d(TAG, "🎭 Historical position match found: " + similarity);
                    
                    if (callback != null) {
                        LogThrottler.d(TAG, "✅ Calling callback with historical match");
                        callback.onHistoricalMatchFound(context, bestMatch);
                        callback.onCommentaryTriggered(context, commentary);
                    } else {
                        LogThrottler.w(TAG, "⚠️ Callback is null - cannot trigger commentary");
                    }
                } else {
                    LogThrottler.d(TAG, "⚠️ SIMILARITY TOO LOW: " + similarity + " <= 0.85 threshold");
                }
            } else {
                LogThrottler.d(TAG, "⚠️ DATABASE: No similar positions found for this FEN and master");
            }
        } catch (Exception e) {
            LogThrottler.e(TAG, "❌ ERROR in checkHistoricalPositions", e);
        }
    }
    
    /**
     * Analyze FEN for tactical patterns
     */
    private void analyzeTacticalPatterns(CommentaryContext context) {
        String fen = context.currentFEN;
        List<String> detectedPatterns = new ArrayList<>();
        
        // Pattern detection based on FEN analysis
        if (detectPinPattern(fen)) {
            detectedPatterns.add("pin");
        }
        if (detectForkPattern(fen)) {
            detectedPatterns.add("fork");
        }
        if (detectSkewPattern(fen)) {
            detectedPatterns.add("skewer");
        }
        if (detectDiscoveredAttack(fen)) {
            detectedPatterns.add("discovered_attack");
        }
        
        if (!detectedPatterns.isEmpty()) {
            context.trigger = CommentaryTrigger.TACTICAL_PATTERN;
            context.tacticalThemes = detectedPatterns;
            
            String commentary = generateTacticalCommentary(context, detectedPatterns);
            
            LogThrottler.d(TAG, "🎯 Tactical patterns detected: " + detectedPatterns);
            
            if (callback != null) {
                callback.onTacticalPatternDetected(context, detectedPatterns);
                callback.onCommentaryTriggered(context, commentary);
            }
        }
    }
    
    /**
     * Analyze positional themes and strategic concepts
     */
    private void analyzePositionalThemes(CommentaryContext context) {
        String fen = context.currentFEN;
        
        // Detect positional themes
        if (detectIsolatedPawns(fen)) {
            context.metadata.put("isolated_pawns", true);
        }
        if (detectDoubledPawns(fen)) {
            context.metadata.put("doubled_pawns", true);
        }
        if (detectWeakSquares(fen)) {
            context.metadata.put("weak_squares", true);
        }
        if (detectOpenFiles(fen)) {
            context.metadata.put("open_files", true);
        }
        
        // Generate positional commentary if themes detected
        if (!context.metadata.isEmpty()) {
            context.trigger = CommentaryTrigger.POSITIONAL_THEME;
            String commentary = generatePositionalCommentary(context);
            
            if (callback != null) {
                callback.onCommentaryTriggered(context, commentary);
            }
        }
    }
    
    /**
     * Generate commentary for historical position matches
     */
    private String generateHistoricalCommentary(CommentaryContext context, 
                                               GameDatabaseHelper.HistoricalPosition match) {
        return String.format(
            "This position reminds me of my game against %s in %s. " +
            "In that position, %s. %s",
            match.opponent != null ? match.opponent : "my opponent",
            match.year != null ? match.year : "that tournament",
            match.annotation != null ? match.annotation : "I found a good continuation",
            getPersonalityTrait(context.masterName)
        );
    }
    
    /**
     * Generate commentary for tactical patterns
     */
    private String generateTacticalCommentary(CommentaryContext context, List<String> patterns) {
        String patternDescription = String.join(" and ", patterns);
        return String.format(
            "I see a %s forming in this position. %s %s",
            patternDescription,
            getTacticalAdvice(patterns.get(0)),
            getPersonalityTrait(context.masterName)
        );
    }
    
    /**
     * Generate commentary for positional themes
     */
    private String generatePositionalCommentary(CommentaryContext context) {
        StringBuilder commentary = new StringBuilder();
        
        if (context.metadata.containsKey("isolated_pawns")) {
            commentary.append("The isolated pawns create both weakness and dynamic potential. ");
        }
        if (context.metadata.containsKey("open_files")) {
            commentary.append("These open files offer excellent opportunities for rook activity. ");
        }
        
        commentary.append(getPersonalityTrait(context.masterName));
        return commentary.toString();
    }
    
    /**
     * Check for evaluation-based commentary triggers
     */
    private void triggerEvaluationCommentary(CommentaryContext context) {
        String commentary;
        
        if (context.evaluationChange > 2.0) {
            commentary = "Brilliant! This move completely transforms the position in our favor. " +
                        getPersonalityTrait(context.masterName);
        } else if (context.evaluationChange < -2.0) {
            commentary = "That was a serious mistake. The position has shifted dramatically. " +
                        "We must be more careful. " + getPersonalityTrait(context.masterName);
        } else {
            commentary = "An interesting position has developed. " +
                        getPersonalityTrait(context.masterName);
        }
        
        if (callback != null) {
            callback.onCommentaryTriggered(context, commentary);
        }
    }
    
    /**
     * Check for game phase transitions
     */
    private void checkGamePhaseTransition(CommentaryContext context) {
        // Implementation would check for phase changes and trigger appropriate commentary
        // For example: "We're transitioning into the endgame now. Precision is crucial."
    }
    
    /**
     * Detect master-specific playing patterns
     */
    private void detectMasterStylePatterns(CommentaryContext context) {
        // Implementation would analyze positions for master-specific characteristics
        // For example, Tal's sacrificial style, Capablanca's endgame technique, etc.
    }
    
    // Helper methods for pattern detection (simplified implementations)
    private boolean detectPinPattern(String fen) {
        // Simple heuristic - would need more sophisticated analysis
        return fen.contains("q") && fen.contains("k") && fen.contains("r");
    }
    
    private boolean detectForkPattern(String fen) {
        return fen.contains("n") || fen.contains("N"); // Knight present
    }
    
    private boolean detectSkewPattern(String fen) {
        return fen.contains("q") || fen.contains("r"); // Major pieces present
    }
    
    private boolean detectDiscoveredAttack(String fen) {
        return fen.contains("b") || fen.contains("r"); // Long-range pieces
    }
    
    private boolean detectIsolatedPawns(String fen) {
        // Would analyze pawn structure
        return false; // Placeholder
    }
    
    private boolean detectDoubledPawns(String fen) {
        // Would analyze pawn structure
        return false; // Placeholder
    }
    
    private boolean detectWeakSquares(String fen) {
        // Would analyze square control
        return false; // Placeholder
    }
    
    private boolean detectOpenFiles(String fen) {
        // Would analyze file control
        return false; // Placeholder
    }
    
    /**
     * Calculate position similarity (simplified)
     */
    private double calculatePositionSimilarity(String fen1, String fen2) {
        if (fen1.equals(fen2)) return 1.0;
        
        // Simple character-based similarity (would need chess-specific logic)
        String[] parts1 = fen1.split(" ");
        String[] parts2 = fen2.split(" ");
        
        if (parts1.length > 0 && parts2.length > 0) {
            String board1 = parts1[0];
            String board2 = parts2[0];
            
            int matches = 0;
            int minLength = Math.min(board1.length(), board2.length());
            
            for (int i = 0; i < minLength; i++) {
                if (board1.charAt(i) == board2.charAt(i)) {
                    matches++;
                }
            }
            
            return (double) matches / Math.max(board1.length(), board2.length());
        }
        
        return 0.0;
    }
    
    /**
     * Determine game phase from FEN
     */
    private String determineGamePhase(String fen) {
        // Count pieces to determine phase
        String board = fen.split(" ")[0];
        int pieceCount = 0;
        
        for (char c : board.toCharArray()) {
            if (Character.isLetter(c)) {
                pieceCount++;
            }
        }
        
        if (pieceCount > 24) return "opening";
        else if (pieceCount > 12) return "middlegame";
        else return "endgame";
    }
    
    /**
     * Get personality-specific traits for commentary
     */
    private String getPersonalityTrait(String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
                return "The position calls for creative sacrifices!";
            case "fischer":
                return "Precision and calculation are everything here.";
            case "capablanca":
                return "Simple, clear play usually wins.";
            case "kasparov":
                return "Dynamic piece play is the key to this position.";
            case "carlsen":
                return "Let's find the most practical continuation.";
            default:
                return "An interesting position to analyze.";
        }
    }
    
    /**
     * Get tactical advice for specific patterns
     */
    private String getTacticalAdvice(String pattern) {
        switch (pattern) {
            case "pin":
                return "Look for ways to increase pressure on the pinned piece.";
            case "fork":
                return "The knight fork is a powerful weapon - use it wisely.";
            case "skewer":
                return "Force the valuable piece to move and win material.";
            case "discovered_attack":
                return "The piece that moves reveals a devastating attack.";
            default:
                return "This tactical motif requires careful calculation.";
        }
    }
    
    /**
     * Initialize pattern recognition data
     */
    private void initializePatterns() {
        // Initialize tactical and positional pattern recognition data
        LogThrottler.d(TAG, "🎯 FEN Commentary Hooks initialized");
    }
}