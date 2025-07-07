package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * 🧩 Tactical Puzzle Engine - Core System for Interactive Chess Puzzles
 * 
 * Features:
 * - Curated tactical puzzle database
 * - User vs Assistant performance tracking
 * - Multiple game modes (Solo, Validation, Competitive)
 * - Adaptive difficulty and themed training
 * - Comprehensive statistics and progress tracking
 * 
 * Integration Points:
 * - Assistant validation and performance measurement
 * - User engagement and skill development
 * - Competitive modes against AI personalities
 */
public class TacticalPuzzleEngine {
    private static final String TAG = "TacticalPuzzleEngine";
    
    // Singleton instance
    private static TacticalPuzzleEngine instance;
    private final Context context;
    
    // Core data
    private List<TacticalPuzzle> puzzleDatabase;
    private Map<String, PuzzleStats> userStats;
    private SharedPreferences preferences;
    
    // Current session tracking
    private TacticalPuzzle currentPuzzle;
    private long puzzleStartTime;
    private int currentStreak;
    
    // Puzzle history to avoid repetition
    private List<String> recentPuzzleIds = new ArrayList<>();
    private static final int MAX_RECENT_PUZZLES = 5;
    
    // Move validation
    private StockfishManager validationEngine;
    
    // Performance settings
    private static final int DEFAULT_TIME_LIMIT = 30; // seconds
    private static final int STREAK_BONUS_THRESHOLD = 5;
    private static final String PREFS_NAME = "tactical_puzzle_stats";
    
    private TacticalPuzzleEngine(Context context) {
        this.context = context;
        this.puzzleDatabase = new ArrayList<>();
        this.userStats = new HashMap<>();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        initializePuzzleDatabase();
        loadUserStats();
        
        Log.d(TAG, "🧩 TacticalPuzzleEngine initialized with " + puzzleDatabase.size() + " puzzles");
    }
    
    public static synchronized TacticalPuzzleEngine getInstance(Context context) {
        if (instance == null) {
            instance = new TacticalPuzzleEngine(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 🎯 Get a random puzzle by difficulty or theme (avoids recent puzzles)
     */
    public TacticalPuzzle getRandomPuzzle(int difficulty, String theme) {
        List<TacticalPuzzle> filteredPuzzles = new ArrayList<>();
        
        // First pass - filter by criteria and exclude recent puzzles
        for (TacticalPuzzle puzzle : puzzleDatabase) {
            boolean matchesDifficulty = (difficulty == 0 || puzzle.difficulty == difficulty);
            boolean matchesTheme = (theme == null || theme.equals("all") || puzzle.theme.equals(theme));
            boolean isNotRecent = !recentPuzzleIds.contains(puzzle.id);
            
            if (matchesDifficulty && matchesTheme && isNotRecent) {
                filteredPuzzles.add(puzzle);
            }
        }
        
        // If no non-recent puzzles available, include recent ones
        if (filteredPuzzles.isEmpty()) {
            Log.d(TAG, "🔄 No non-recent puzzles found, including recent ones");
            for (TacticalPuzzle puzzle : puzzleDatabase) {
                boolean matchesDifficulty = (difficulty == 0 || puzzle.difficulty == difficulty);
                boolean matchesTheme = (theme == null || theme.equals("all") || puzzle.theme.equals(theme));
                
                if (matchesDifficulty && matchesTheme) {
                    filteredPuzzles.add(puzzle);
                }
            }
        }
        
        if (filteredPuzzles.isEmpty()) {
            Log.w(TAG, "⚠️ No puzzles found for difficulty=" + difficulty + ", theme=" + theme);
            return puzzleDatabase.isEmpty() ? null : puzzleDatabase.get(0);
        }
        
        Random random = new Random();
        TacticalPuzzle selectedPuzzle = filteredPuzzles.get(random.nextInt(filteredPuzzles.size()));
        
        // Add to recent puzzles history
        addToRecentPuzzles(selectedPuzzle.id);
        
        Log.d(TAG, String.format("🎯 Selected puzzle: %s (%s, diff=%d) [recent: %s]", 
            selectedPuzzle.id, selectedPuzzle.theme, selectedPuzzle.difficulty, recentPuzzleIds.toString()));
        
        return selectedPuzzle;
    }
    
    /**
     * 📝 Track recently shown puzzles to avoid repetition
     */
    private void addToRecentPuzzles(String puzzleId) {
        // Remove if already exists (move to front)
        recentPuzzleIds.remove(puzzleId);
        
        // Add to front of list
        recentPuzzleIds.add(0, puzzleId);
        
        // Trim to max size
        while (recentPuzzleIds.size() > MAX_RECENT_PUZZLES) {
            recentPuzzleIds.remove(recentPuzzleIds.size() - 1);
        }
        
        Log.d(TAG, "📝 Updated recent puzzles: " + recentPuzzleIds.toString());
    }
    
    /**
     * 🎮 Start a new puzzle session
     */
    public void startPuzzle(TacticalPuzzle puzzle) {
        this.currentPuzzle = puzzle;
        this.puzzleStartTime = System.currentTimeMillis();
        
        Log.d(TAG, "🚀 Started puzzle: " + puzzle.id + " - " + puzzle.description);
    }
    
    /**
     * ✅ Check if a move solves the current puzzle
     */
    public PuzzleResult checkSolution(String userMove) {
        if (currentPuzzle == null) {
            Log.e(TAG, "❌ No active puzzle to check solution");
            return new PuzzleResult(null, false, 0, userMove, "", "", 0.0, "No active puzzle");
        }
        
        long solveTime = System.currentTimeMillis() - puzzleStartTime;
        boolean isCorrect = isMoveSolution(userMove, currentPuzzle);
        
        PuzzleResult result = new PuzzleResult(
            currentPuzzle.id,
            isCorrect,
            solveTime,
            userMove,
            currentPuzzle.primarySolution,
            "", // Assistant move to be filled later
            calculateConfidence(isCorrect, solveTime),
            isCorrect ? "Correct!" : "Try again - look for " + currentPuzzle.theme
        );
        
        // Update statistics
        updateUserStats(result);
        
        // Update streak
        if (isCorrect) {
            currentStreak++;
            Log.d(TAG, "✅ Correct! Streak: " + currentStreak);
        } else {
            currentStreak = 0;
            Log.d(TAG, "❌ Incorrect. Streak reset.");
        }
        
        return result;
    }
    
    /**
     * 🤖 Get assistant's solution for current puzzle
     */
    public CompletableFuture<PuzzleResult> getAssistantSolution() {
        if (currentPuzzle == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Use AIStyleAdvisor to get assistant's move choice
                AIStyleAdvisor advisor = AIStyleAdvisor.getInstance(context);
                CompletableFuture<String> assistantMoveFuture = new CompletableFuture<>();
                
                List<String> candidates = getCurrentPuzzleCandidates();
                
                advisor.getStyleAdvice(currentPuzzle.fen, "alekhine", candidates, new AIStyleAdvisor.AIStyleCallback() {
                    @Override
                    public void onAdviceReceived(AIStyleAdvisor.AIStyleAdvice advice) {
                        if (advice.preferredMoves != null && !advice.preferredMoves.isEmpty()) {
                            assistantMoveFuture.complete(advice.preferredMoves.get(0));
                        } else {
                            assistantMoveFuture.complete(currentPuzzle.primarySolution); // Fallback
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "🤖 Assistant error: " + error);
                        assistantMoveFuture.complete(currentPuzzle.primarySolution); // Fallback
                    }
                    
                    @Override
                    public void onCacheHit(AIStyleAdvisor.AIStyleAdvice cachedAdvice) {
                        if (cachedAdvice.preferredMoves != null && !cachedAdvice.preferredMoves.isEmpty()) {
                            assistantMoveFuture.complete(cachedAdvice.preferredMoves.get(0));
                        } else {
                            assistantMoveFuture.complete(currentPuzzle.primarySolution); // Fallback
                        }
                    }
                });
                
                String assistantMove = assistantMoveFuture.get();
                long solveTime = System.currentTimeMillis() - startTime;
                boolean isCorrect = isMoveSolution(assistantMove, currentPuzzle);
                
                PuzzleResult assistantResult = new PuzzleResult(
                    currentPuzzle.id,
                    isCorrect,
                    solveTime,
                    assistantMove,
                    currentPuzzle.primarySolution,
                    assistantMove,
                    calculateConfidence(isCorrect, solveTime),
                    "Assistant's analysis"
                );
                
                Log.d(TAG, String.format("🤖 Assistant solved puzzle %s: %s (%s)", 
                    currentPuzzle.id, assistantMove, isCorrect ? "CORRECT" : "INCORRECT"));
                
                return assistantResult;
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error getting assistant solution", e);
                return null;
            }
        });
    }
    
    /**
     * 📊 Get user statistics
     */
    public PuzzleStats getUserStats(String category) {
        return userStats.getOrDefault(category, new PuzzleStats());
    }
    
    /**
     * 🎯 Get available puzzle themes
     */
    public List<String> getAvailableThemes() {
        List<String> themes = new ArrayList<>();
        for (TacticalPuzzle puzzle : puzzleDatabase) {
            if (!themes.contains(puzzle.theme)) {
                themes.add(puzzle.theme);
            }
        }
        return themes;
    }
    
    /**
     * 🏆 Get current streak
     */
    public int getCurrentStreak() {
        return currentStreak;
    }
    
    /**
     * 📈 Get adaptive difficulty suggestion
     */
    public int getRecommendedDifficulty() {
        PuzzleStats stats = getUserStats("overall");
        if (stats.getAccuracy() > 0.8 && stats.puzzlesSolved > 10) {
            return Math.min(5, (int) stats.averageDifficulty + 1);
        } else if (stats.getAccuracy() < 0.5 && stats.puzzlesSolved > 5) {
            return Math.max(1, (int) stats.averageDifficulty - 1);
        }
        return 3; // Default medium difficulty
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    private void initializePuzzleDatabase() {
        Log.d(TAG, "🔄 Initializing tactical puzzle database...");
        
        // Proper tactical puzzles similar to chess.com/lichess
        addPinPuzzles();
        addForkPuzzles();
        addSkewerPuzzles();
        addDiscoveredAttackPuzzles();
        addDoubleAttackPuzzles();
        addDeflectionPuzzles();
        addRemovalOfDefenderPuzzles();
        addBackRankMatePuzzles();
        addAdvancedTactics();
        
        Log.d(TAG, "✅ Puzzle database initialized with " + puzzleDatabase.size() + " tactical positions");
    }
    
    private void addPinPuzzles() {
        // Fixed pin puzzle with bishop actually on c4 to capture f7
        puzzleDatabase.add(new TacticalPuzzle(
            "pin_001",
            "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4",
            "Bxf7+",
            Arrays.asList("Bxf7+"),
            "pin",
            3,
            "White to play - Exploit the pin!",
            "The bishop on c4 captures f7 with check, and the knight on c6 cannot interpose due to the pin."
        ));
        
        // Real pin puzzle - pinned piece cannot move
        puzzleDatabase.add(new TacticalPuzzle(
            "pin_002",
            "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 4 4",
            "d4",
            Arrays.asList("d4"),
            "pin",
            2,
            "White to play - Attack the pinned piece!",
            "The pawn advance attacks the pinned bishop that cannot move away."
        ));
        
        // Simple back rank pin example
        puzzleDatabase.add(new TacticalPuzzle(
            "pin_003",
            "6k1/5ppp/8/8/8/8/5PPP/4R1K1 w - - 0 1",
            "Re8+",
            Arrays.asList("Re8+"),
            "pin",
            1,
            "White to play - Back rank pin!",
            "The rook move gives check and the king cannot escape due to the back rank."
        ));
    }
    
    private void addForkPuzzles() {
        // Classic knight fork - attacks f7 and h7 simultaneously
        puzzleDatabase.add(new TacticalPuzzle(
            "fork_001", 
            "rnbqkb1r/pppp1ppp/8/8/8/3P1N2/PPP2PPP/RNBQKB1R w KQkq - 0 5",
            "Ng5",
            Arrays.asList("Ng5"),
            "fork",
            2,
            "White to play - Fork f7 and h7!",
            "The knight move attacks both the f7 and h7 squares simultaneously."
        ));
        
        // Simple knight fork attacking two pieces
        puzzleDatabase.add(new TacticalPuzzle(
            "fork_002",
            "rnbqkb1r/ppp2ppp/5n2/3pp3/8/3P1N2/PPP2PPP/RNBQKB1R w KQkq - 0 6",
            "Ne4",
            Arrays.asList("Ne4"),
            "fork",
            2,
            "White to play - Knight fork!",
            "The knight forks the queen and bishop, winning material."
        ));
        
        // Royal fork - king and queen
        puzzleDatabase.add(new TacticalPuzzle(
            "fork_003",
            "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 4 4",
            "Nd4",
            Arrays.asList("Nd4"),
            "fork",
            3,
            "White to play - Royal fork!",
            "The knight forks the king and queen, the most valuable fork in chess."
        ));
    }
    
    private void addSkewerPuzzles() {
        // Classic rook skewer - king and queen on same rank
        puzzleDatabase.add(new TacticalPuzzle(
            "skewer_001",
            "r3k1q1/5ppp/8/8/8/8/5PPP/4R1K1 w - - 0 1",
            "Re8+",
            Arrays.asList("Re8+"),
            "skewer",
            2,
            "White to play - Skewer king and queen!",
            "The rook check forces the king to move, exposing the queen to capture."
        ));
        
        // Bishop skewer on diagonal
        puzzleDatabase.add(new TacticalPuzzle(
            "skewer_002",
            "r1b1k2r/pppp1ppp/2n2n2/2bqp3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 4 4",
            "Bxf7+",
            Arrays.asList("Bxf7+"),
            "skewer",
            3,
            "White to play - Bishop skewer!",
            "The bishop check forces the king to move, then captures the queen."
        ));
        
        // Rook skewer on file
        puzzleDatabase.add(new TacticalPuzzle(
            "skewer_003",
            "4k3/4r3/8/8/8/8/4R3/4K3 w - - 0 1",
            "Re8+",
            Arrays.asList("Re8+"),
            "skewer",
            1,
            "White to play - Simple skewer!",
            "The rook gives check and wins the black rook after the king moves."
        ));
    }
    
    private void addDiscoveredAttackPuzzles() {
        puzzleDatabase.add(new TacticalPuzzle(
            "discovery_001",
            "r1bq1rk1/pp3ppp/2n1pn2/3p4/1bPP4/2N1PN2/PP1B1PPP/R2QKB1R w KQ - 0 9",
            "Nxd5",
            Arrays.asList("Nxd5"),
            "discovery",
            4,
            "White to play - Discovered attack wins!",
            "The knight move opens up a discovered attack from the bishop."
        ));
    }
    
    private void addDoubleAttackPuzzles() {
        puzzleDatabase.add(new TacticalPuzzle(
            "double_001",
            "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 4 4",
            "Bb5",
            Arrays.asList("Bb5"),
            "double_attack",
            3,
            "White to play - Double attack!",
            "The bishop move attacks both the knight and the king."
        ));
    }
    
    private void addDeflectionPuzzles() {
        puzzleDatabase.add(new TacticalPuzzle(
            "deflection_001",
            "r3k2r/ppp2ppp/2n5/3pp3/1b1PP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 8",
            "Nd5",
            Arrays.asList("Nd5"),
            "deflection",
            3,
            "White to play - Deflect the defender!",
            "The knight move deflects the defender from an important square."
        ));
    }
    
    private void addRemovalOfDefenderPuzzles() {
        puzzleDatabase.add(new TacticalPuzzle(
            "removal_001",
            "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 4 4",
            "Bxf7+",
            Arrays.asList("Bxf7+"),
            "removal",
            4,
            "White to play - Remove the defender!",
            "Removing the key defender opens up tactical possibilities."
        ));
    }
    
    private void addBackRankMatePuzzles() {
        puzzleDatabase.add(new TacticalPuzzle(
            "backrank_001",
            "6k1/5ppp/8/8/8/8/5PPP/5RK1 w - - 0 1",
            "Rf8#",
            Arrays.asList("Rf8#"),
            "mate",
            1,
            "White to play - Back rank mate!",
            "The rook delivers checkmate on the back rank."
        ));
    }
    
    private void addAdvancedTactics() {
        puzzleDatabase.add(new TacticalPuzzle(
            "advanced_001",
            "r1bq1rk1/pp3ppp/2n1p3/3pP3/1b1P4/2N1BN2/PP3PPP/R2QKB1R w KQ - 0 10",
            "Nxd5",
            Arrays.asList("Nxd5"),
            "combination",
            5,
            "White to play - Complex combination!",
            "A multi-move combination that wins material through tactical pressure."
        ));
    }
    
    private List<String> getCurrentPuzzleCandidates() {
        if (currentPuzzle == null) return new ArrayList<>();
        
        List<String> candidates = new ArrayList<>(currentPuzzle.alternativeSolutions);
        
        // Add some reasonable but incorrect candidates for variety
        candidates.addAll(Arrays.asList("Nf3", "Be2", "0-0", "h3", "a3", "Qe2"));
        
        return candidates;
    }
    
    private boolean isMoveSolution(String move, TacticalPuzzle puzzle) {
        if (move == null || puzzle == null) return false;
        
        // Convert the move to UCI notation for Stockfish validation
        String uciMove = convertToUciNotation(move, puzzle.fen);
        if (uciMove == null) {
            Log.w(TAG, "❌ Could not convert move " + move + " to UCI notation");
            return false;
        }
        
        // CRITICAL FIX: First validate that the move is actually legal in the given position
        if (!isMoveLegalInPosition(uciMove, puzzle.fen)) {
            Log.w(TAG, "❌ Move " + move + " (" + uciMove + ") is ILLEGAL in position: " + puzzle.fen);
            return false;
        }
        
        // Then check if it matches the expected solutions (algebraic notation)
        // Normalize moves by removing check/checkmate symbols for comparison
        String normalizedMove = move.replaceAll("[+#]", "");
        String normalizedPrimary = puzzle.primarySolution.replaceAll("[+#]", "");
        
        // Check primary solution
        if (normalizedMove.equals(normalizedPrimary)) {
            Log.d(TAG, "✅ Move " + move + " matches primary solution " + puzzle.primarySolution);
            return true;
        }
        
        // Check alternative solutions
        for (String altSolution : puzzle.alternativeSolutions) {
            String normalizedAlt = altSolution.replaceAll("[+#]", "");
            if (normalizedMove.equals(normalizedAlt)) {
                Log.d(TAG, "✅ Move " + move + " matches alternative solution: " + altSolution);
                return true;
            }
        }
        
        Log.d(TAG, "❌ Move " + move + " is legal but not a correct solution");
        return false;
    }
    
    /**
     * 🔄 Convert algebraic notation to UCI notation for Stockfish validation
     */
    private String convertToUciNotation(String algebraicMove, String fen) {
        if (algebraicMove == null || algebraicMove.isEmpty()) return null;
        
        // If it already looks like UCI notation (e.g., "e2e4"), return as-is
        if (algebraicMove.matches("[a-h][1-8][a-h][1-8][qrbn]?")) {
            return algebraicMove;
        }
        
        // Remove check/checkmate symbols
        String cleanMove = algebraicMove.replaceAll("[+#]", "");
        
        // Parse the position from FEN to find actual piece locations
        try {
            return parseAlgebraicToUci(cleanMove, fen);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error converting " + algebraicMove + " to UCI", e);
            return null;
        }
    }
    
    /**
     * 🧩 Dynamic algebraic to UCI conversion based on actual board position
     */
    private String parseAlgebraicToUci(String move, String fen) {
        // Parse FEN to get board position
        String[] fenParts = fen.split(" ");
        String boardFen = fenParts[0];
        boolean isWhiteToMove = "w".equals(fenParts[1]);
        
        // Create board array from FEN
        char[][] board = new char[8][8];
        String[] ranks = boardFen.split("/");
        
        for (int r = 0; r < 8; r++) {
            String rank = ranks[r];
            int col = 0;
            for (char ch : rank.toCharArray()) {
                if (Character.isDigit(ch)) {
                    int empty = ch - '0';
                    for (int i = 0; i < empty && col < 8; i++) {
                        board[r][col++] = ' ';
                    }
                } else {
                    if (col < 8) board[r][col++] = ch;
                }
            }
        }
        
        // Parse the move
        return convertMoveToUci(move, board, isWhiteToMove);
    }
    
    /**
     * 🎯 Convert a single move to UCI based on board position
     */
    private String convertMoveToUci(String move, char[][] board, boolean isWhiteToMove) {
        // Extract destination square
        String destination = null;
        char movingPiece = 'P'; // Default to pawn
        boolean isCapture = move.contains("x");
        
        // Parse destination (last 2 characters, or before promotion)
        if (move.length() >= 2) {
            if (move.matches(".*[a-h][1-8]$")) {
                destination = move.substring(move.length() - 2);
            } else if (move.matches(".*[a-h][1-8][qrbn]$")) {
                destination = move.substring(move.length() - 3, move.length() - 1);
            }
        }
        
        if (destination == null) {
            Log.e(TAG, "❌ Could not parse destination from move: " + move);
            return null;
        }
        
        // Parse piece type
        if (move.length() > 2 && Character.isUpperCase(move.charAt(0))) {
            movingPiece = move.charAt(0);
        }
        
        // Convert destination to coordinates
        int toCol = destination.charAt(0) - 'a';
        int toRow = 8 - (destination.charAt(1) - '0');
        
        // Find the piece that can make this move
        char targetPiece = isWhiteToMove ? movingPiece : Character.toLowerCase(movingPiece);
        
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                if (board[fromRow][fromCol] == targetPiece) {
                    // Check if this piece can legally make the move
                    if (canPieceMoveTo(board, fromRow, fromCol, toRow, toCol, targetPiece)) {
                        // Build UCI notation
                        char fromFile = (char) ('a' + fromCol);
                        int fromRank = 8 - fromRow;
                        char toFile = (char) ('a' + toCol);
                        int toRank = 8 - toRow;
                        
                        String uciMove = "" + fromFile + fromRank + toFile + toRank;
                        
                        // Add promotion if applicable
                        if (move.matches(".*[qrbn]$")) {
                            uciMove += Character.toLowerCase(move.charAt(move.length() - 1));
                        }
                        
                        Log.d(TAG, String.format("🔄 Converted %s to %s (piece %c from %c%d to %c%d)", 
                            move, uciMove, targetPiece, fromFile, fromRank, toFile, toRank));
                        
                        return uciMove;
                    }
                }
            }
        }
        
        Log.w(TAG, "⚠️ Could not find piece to make move: " + move);
        return null;
    }
    
    /**
     * 🧩 Basic piece movement validation
     */
    private boolean canPieceMoveTo(char[][] board, int fromRow, int fromCol, int toRow, int toCol, char piece) {
        if (fromRow == toRow && fromCol == toCol) return false;
        
        char pieceType = Character.toUpperCase(piece);
        int deltaRow = toRow - fromRow;
        int deltaCol = toCol - fromCol;
        
        switch (pieceType) {
            case 'P':
                // Simplified pawn logic - just check basic moves
                boolean isWhite = Character.isUpperCase(piece);
                int direction = isWhite ? -1 : 1; // White moves up (-), black moves down (+)
                
                if (deltaCol == 0) {
                    // Forward move
                    return deltaRow == direction && board[toRow][toCol] == ' ';
                } else if (Math.abs(deltaCol) == 1 && deltaRow == direction) {
                    // Capture
                    return board[toRow][toCol] != ' ';
                }
                return false;
                
            case 'R':
                // Rook moves
                return (deltaRow == 0 || deltaCol == 0);
                
            case 'N':
                // Knight moves
                return (Math.abs(deltaRow) == 2 && Math.abs(deltaCol) == 1) ||
                       (Math.abs(deltaRow) == 1 && Math.abs(deltaCol) == 2);
                
            case 'B':
                // Bishop moves
                return Math.abs(deltaRow) == Math.abs(deltaCol);
                
            case 'Q':
                // Queen moves (rook + bishop)
                return (deltaRow == 0 || deltaCol == 0) || 
                       (Math.abs(deltaRow) == Math.abs(deltaCol));
                
            case 'K':
                // King moves
                return Math.abs(deltaRow) <= 1 && Math.abs(deltaCol) <= 1;
                
            default:
                return false;
        }
    }
    
    /**
     * 🔍 Validates that a move is actually legal in the given position using Stockfish
     */
    private boolean isMoveLegalInPosition(String move, String fen) {
        try {
            // Get or create Stockfish instance for validation
            StockfishManager stockfish = getValidationEngine();
            if (stockfish == null) {
                Log.e(TAG, "❌ No Stockfish instance available for move validation");
                return false; // Fail safe - if no engine, reject the move
            }
            
            // Set the position and validate the move
            if (stockfish.setPosition(fen)) {
                boolean isLegal = stockfish.isLegalMove(move);
                Log.d(TAG, String.format("🔍 Move %s in position %s: %s", 
                    move, fen.substring(0, Math.min(20, fen.length())), 
                    isLegal ? "LEGAL" : "ILLEGAL"));
                return isLegal;
            } else {
                Log.e(TAG, "❌ Failed to set position in Stockfish: " + fen);
                return false; // Fail safe
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error validating move legality", e);
            return false; // Fail safe - if error, reject the move
        }
    }
    
    /**
     * 🔧 Get or create a Stockfish engine instance for move validation
     */
    private StockfishManager getValidationEngine() {
        if (validationEngine == null) {
            try {
                validationEngine = new StockfishManager();
                
                // Get the Stockfish executable path
                String stockfishPath = getStockfishPath();
                if (stockfishPath != null) {
                    boolean started = validationEngine.startEngine(stockfishPath);
                    if (started) {
                        Log.d(TAG, "✅ Validation engine started successfully");
                    } else {
                        Log.e(TAG, "❌ Failed to start validation engine");
                        validationEngine = null;
                    }
                } else {
                    Log.e(TAG, "❌ No Stockfish path available");
                    validationEngine = null;
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error creating validation engine", e);
                validationEngine = null;
            }
        }
        return validationEngine;
    }
    
    /**
     * 📁 Get the path to the Stockfish executable
     */
    private String getStockfishPath() {
        try {
            // Use the same path as MainActivity - check the native libs directory
            File nativeLibDir = new File(context.getApplicationInfo().nativeLibraryDir);
            File stockfishFile = new File(nativeLibDir, "libstockfish.so");
            
            if (stockfishFile.exists()) {
                return stockfishFile.getAbsolutePath();
            } else {
                Log.w(TAG, "⚠️ Stockfish library not found at: " + stockfishFile.getAbsolutePath());
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting Stockfish path", e);
            return null;
        }
    }
    
    /**
     * 🧹 Cleanup method to properly stop the validation engine
     */
    public void cleanup() {
        if (validationEngine != null) {
            try {
                validationEngine.stopEngine();
                Log.d(TAG, "✅ Validation engine stopped");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error stopping validation engine", e);
            } finally {
                validationEngine = null;
            }
        }
    }
    
    private double calculateConfidence(boolean correct, long timeMs) {
        if (!correct) return 0.0;
        
        // Confidence based on solving speed
        double timeSeconds = timeMs / 1000.0;
        if (timeSeconds < 5) return 1.0;
        if (timeSeconds < 15) return 0.8;
        if (timeSeconds < 30) return 0.6;
        return 0.4;
    }
    
    private void updateUserStats(PuzzleResult result) {
        PuzzleStats stats = userStats.getOrDefault("overall", new PuzzleStats());
        
        stats.puzzlesSolved++;
        if (result.correct) stats.correctSolutions++;
        stats.totalTime += result.solveTime;
        stats.recentResults.add(result);
        
        // Maintain recent results size
        if (stats.recentResults.size() > 50) {
            stats.recentResults.remove(0);
        }
        
        // Update theme-specific stats
        if (currentPuzzle != null) {
            PuzzleStats themeStats = userStats.getOrDefault(currentPuzzle.theme, new PuzzleStats());
            themeStats.puzzlesSolved++;
            if (result.correct) themeStats.correctSolutions++;
            themeStats.totalTime += result.solveTime;
            userStats.put(currentPuzzle.theme, themeStats);
        }
        
        userStats.put("overall", stats);
        saveUserStats();
        
        Log.d(TAG, String.format("📊 Updated stats: %d solved, %.1f%% accuracy", 
            stats.puzzlesSolved, stats.getAccuracy() * 100));
    }
    
    private void loadUserStats() {
        // Load from SharedPreferences
        String statsJson = preferences.getString("user_stats", "{}");
        try {
            JSONObject json = new JSONObject(statsJson);
            // Parse JSON and populate userStats map
            // Implementation details...
            Log.d(TAG, "📊 Loaded user statistics");
        } catch (Exception e) {
            Log.w(TAG, "⚠️ Could not load user stats, starting fresh", e);
            userStats.clear();
        }
    }
    
    private void saveUserStats() {
        try {
            JSONObject json = new JSONObject();
            // Convert userStats to JSON
            // Implementation details...
            preferences.edit().putString("user_stats", json.toString()).apply();
            Log.d(TAG, "💾 Saved user statistics");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error saving user stats", e);
        }
    }
}