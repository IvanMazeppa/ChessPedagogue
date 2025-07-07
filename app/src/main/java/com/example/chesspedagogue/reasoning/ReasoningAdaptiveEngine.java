package com.example.chesspedagogue.reasoning;

import android.util.Log;
import com.example.chesspedagogue.StockfishManager;
import com.example.chesspedagogue.ChessMasterRatings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates strength-appropriate candidate moves for reasoning engine
 */
public class ReasoningAdaptiveEngine {
    private static final String TAG = "ReasoningAdaptive";
    
    private final StockfishManager stockfishManager;
    
    // Stockfish analysis patterns
    private static final Pattern MOVE_PATTERN = Pattern.compile("^info.*?pv ([a-h][1-8][a-h][1-8][qrbn]?)");
    private static final Pattern EVAL_PATTERN = Pattern.compile("score (cp|mate) (-?\\d+)");
    private static final Pattern DEPTH_PATTERN = Pattern.compile("depth (\\d+)");
    private static final Pattern NODES_PATTERN = Pattern.compile("nodes (\\d+)");
    private static final Pattern MULTIPV_PATTERN = Pattern.compile("multipv (\\d+)");

    public ReasoningAdaptiveEngine(StockfishManager stockfishManager) {
        this.stockfishManager = stockfishManager;
    }

    /**
     * Generate candidate moves appropriate for target ELO
     */
    public List<CandidateMove> generateCandidates(String fen, int targetElo, int maxCandidates) {
        return generateCandidates(fen, targetElo, maxCandidates, null);
    }
    
    /**
     * Generate candidate moves with master-specific personality weighting
     */
    public List<CandidateMove> generateCandidates(String fen, int targetElo, int maxCandidates, String masterName) {
        Log.d(TAG, String.format("🎯 Generating candidates for ELO %d (max %d candidates)", targetElo, maxCandidates));
        
        try {
            // Configure Stockfish for target strength
            configureStockfishForElo(targetElo);
            
            // Set MultiPV to get multiple candidate moves
            try {
                stockfishManager.sendCommand("setoption name MultiPV value " + maxCandidates);
            } catch (Exception e) {
                Log.w(TAG, "⚠️ Could not set MultiPV, using default", e);
            }
            
            // Analyze position
            stockfishManager.setPosition(fen);
            
            // Calculate appropriate search time based on ELO
            int searchTimeMs = calculateSearchTime(targetElo);
            
            // Get detailed analysis instead of raw results
            String analysisResults = stockfishManager.getDetailedAnalysis(searchTimeMs);
            
            // Parse the analysis results
            List<CandidateMove> candidates = parseMultiPVResults(analysisResults, maxCandidates);
            
            // Apply master-specific personality weighting
            if (masterName != null) {
                candidates = applyPersonalityWeighting(candidates, masterName, fen);
            }
            
            return candidates;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error generating candidates: " + e.getMessage(), e);
            return generateFallbackCandidates(fen);
        }
    }

    /**
     * Configure Stockfish UCI options for target ELO strength
     */
    private void configureStockfishForElo(int targetElo) {
        try {
            // Enable UCI_LimitStrength for ELO-based play
            stockfishManager.sendCommand("setoption name UCI_LimitStrength value true");
            
            // Set target ELO (clamped to Stockfish limits)
            int clampedElo = Math.max(1350, Math.min(2850, targetElo));
            stockfishManager.sendCommand("setoption name UCI_Elo value " + clampedElo);
            
            // Configure skill level based on ELO
            int skillLevel = ChessMasterRatings.getSkillLevelForElo(targetElo);
            stockfishManager.sendCommand("setoption name Skill Level value " + skillLevel);
            
            // Set hash size appropriate for strength
            int hashSize = targetElo > 2400 ? 128 : (targetElo > 2000 ? 64 : 32);
            stockfishManager.sendCommand("setoption name Hash value " + hashSize);
            
            Log.d(TAG, String.format("⚙️ Configured Stockfish: ELO=%d, Skill=%d, Hash=%dMB", 
                    clampedElo, skillLevel, hashSize));
        } catch (Exception e) {
            Log.e(TAG, "❌ Error configuring Stockfish for ELO " + targetElo, e);
        }
    }

    /**
     * Calculate search time based on target ELO
     */
    private int calculateSearchTime(int targetElo) {
        if (targetElo >= 2600) return 3000;      // 3 seconds for super-GM level
        if (targetElo >= 2400) return 2000;      // 2 seconds for GM level  
        if (targetElo >= 2200) return 1500;      // 1.5 seconds for IM level
        if (targetElo >= 2000) return 1000;      // 1 second for expert level
        if (targetElo >= 1800) return 750;       // 0.75 seconds for advanced
        return 500;                              // 0.5 seconds for intermediate
    }

    /**
     * Parse MultiPV results from Stockfish analysis
     */
    private List<CandidateMove> parseMultiPVResults(String analysis, int maxCandidates) {
        List<CandidateMove> candidates = new ArrayList<>();
        String[] lines = analysis.split("\n");
        
        for (String line : lines) {
            if (line.contains("info") && line.contains("pv")) {
                CandidateMove candidate = parseAnalysisLine(line);
                if (candidate != null && candidates.size() < maxCandidates) {
                    candidates.add(candidate);
                }
            }
        }
        
        Log.d(TAG, String.format("✅ Parsed %d candidates from analysis", candidates.size()));
        return candidates;
    }

    /**
     * Parse individual analysis line into CandidateMove
     */
    private CandidateMove parseAnalysisLine(String line) {
        try {
            // Extract move
            Matcher moveMatcher = MOVE_PATTERN.matcher(line);
            if (!moveMatcher.find()) return null;
            String move = moveMatcher.group(1);
            
            // Extract evaluation
            float evaluation = 0;
            Matcher evalMatcher = EVAL_PATTERN.matcher(line);
            if (evalMatcher.find()) {
                String scoreType = evalMatcher.group(1);
                int scoreValue = Integer.parseInt(evalMatcher.group(2));
                
                if ("mate".equals(scoreType)) {
                    // Convert mate scores to centipawns (mate in N = 1000 - N*10)
                    evaluation = scoreValue > 0 ? 1000 - scoreValue * 10 : -1000 - scoreValue * 10;
                } else {
                    evaluation = scoreValue; // Already in centipawns
                }
            }
            
            // Extract depth
            int depth = 0;
            Matcher depthMatcher = DEPTH_PATTERN.matcher(line);
            if (depthMatcher.find()) {
                depth = Integer.parseInt(depthMatcher.group(1));
            }
            
            // Extract nodes
            long nodes = 0;
            Matcher nodesMatcher = NODES_PATTERN.matcher(line);
            if (nodesMatcher.find()) {
                nodes = Long.parseLong(nodesMatcher.group(1));
            }
            
            // Extract MultiPV rank
            int multiPvRank = 1;
            Matcher multiPvMatcher = MULTIPV_PATTERN.matcher(line);
            if (multiPvMatcher.find()) {
                multiPvRank = Integer.parseInt(multiPvMatcher.group(1));
            }
            
            // Extract principal variation
            String variation = extractPrincipalVariation(line);
            
            return new CandidateMove(move, evaluation, depth, variation, nodes, multiPvRank);
            
        } catch (Exception e) {
            Log.w(TAG, "⚠️ Failed to parse analysis line: " + line, e);
            return null;
        }
    }

    /**
     * Extract principal variation from analysis line
     */
    private String extractPrincipalVariation(String line) {
        int pvIndex = line.indexOf(" pv ");
        if (pvIndex == -1) return "";
        
        String pvPart = line.substring(pvIndex + 4);
        // Take first 5 moves of PV for brevity
        String[] moves = pvPart.split(" ");
        StringBuilder variation = new StringBuilder();
        
        for (int i = 0; i < Math.min(5, moves.length); i++) {
            if (i > 0) variation.append(" ");
            variation.append(moves[i]);
        }
        
        return variation.toString();
    }

    /**
     * Generate fallback candidates when analysis fails
     */
    private List<CandidateMove> generateFallbackCandidates(String fen) {
        Log.w(TAG, "⚠️ Generating fallback candidates");
        
        List<CandidateMove> fallbacks = new ArrayList<>();
        
        // Basic fallback moves (would be better to use a simple move generator)
        fallbacks.add(new CandidateMove("e2e4", 25, 1, "e2e4", 0, 1));
        fallbacks.add(new CandidateMove("d2d4", 20, 1, "d2d4", 0, 2));
        fallbacks.add(new CandidateMove("g1f3", 15, 1, "g1f3", 0, 3));
        fallbacks.add(new CandidateMove("c2c4", 10, 1, "c2c4", 0, 4));
        
        return fallbacks;
    }

    /**
     * Validate that candidates are legal moves
     */
    public boolean validateCandidates(String fen, List<CandidateMove> candidates) {
        for (CandidateMove candidate : candidates) {
            if (!isLegalMove(fen, candidate.getMove())) {
                Log.w(TAG, "⚠️ Illegal candidate move detected: " + candidate.getMove());
                return false;
            }
        }
        return true;
    }

    /**
     * Check if move is legal in given position
     */
    private boolean isLegalMove(String fen, String move) {
        try {
            stockfishManager.setPosition(fen);
            return stockfishManager.isLegalMove(move);
        } catch (Exception e) {
            Log.w(TAG, "⚠️ Could not validate move legality: " + move, e);
            return true; // Assume legal if we can't check
        }
    }
    
    /**
     * Apply master-specific personality weighting to candidate moves
     */
    private List<CandidateMove> applyPersonalityWeighting(List<CandidateMove> candidates, String masterName, String fen) {
        if ("alekhine".equalsIgnoreCase(masterName)) {
            return applyAlekhineWeighting(candidates, fen);
        }
        // Add other masters here as needed
        return candidates;
    }
    
    /**
     * Apply Alekhine-specific personality weighting to candidates
     * Alekhine preferences: aggressive attacking, tactical complexity, piece activity
     */
    private List<CandidateMove> applyAlekhineWeighting(List<CandidateMove> candidates, String fen) {
        Log.d(TAG, "🎭 Applying Alekhine personality weighting to " + candidates.size() + " candidates");
        
        List<CandidateMove> weightedCandidates = new ArrayList<>();
        
        for (CandidateMove candidate : candidates) {
            float personalityScore = calculateAlekhineScore(candidate, fen);
            
            // Create new candidate with adjusted evaluation based on personality
            float adjustedEval = candidate.getEvaluation() + personalityScore;
            
            CandidateMove weightedCandidate = new CandidateMove(
                candidate.getMove(),
                adjustedEval,
                candidate.getDepth(),
                candidate.getVariation(),
                candidate.getNodes(),
                candidate.getMultiPvRank()
            );
            
            weightedCandidates.add(weightedCandidate);
            
            Log.d(TAG, String.format("🎯 %s: eval %.1f -> %.1f (personality +%.1f)", 
                candidate.getMove(), candidate.getEvaluation(), adjustedEval, personalityScore));
        }
        
        // Sort by adjusted evaluation (higher is better)
        weightedCandidates.sort((a, b) -> Float.compare(b.getEvaluation(), a.getEvaluation()));
        
        Log.d(TAG, "✅ Alekhine personality weighting applied");
        return weightedCandidates;
    }
    
    /**
     * Calculate Alekhine personality score for a candidate move
     * Positive scores favor the move, negative scores discourage it
     */
    private float calculateAlekhineScore(CandidateMove candidate, String fen) {
        float score = 0;
        String move = candidate.getMove();
        String variation = candidate.getVariation();
        
        // TACTICAL COMPLEXITY BONUS (+0.2 to +0.4)
        if (isTacticalMove(move, variation)) {
            score += 30; // +0.3 pawns for tactical moves
            Log.d(TAG, "📊 " + move + ": +30cp tactical complexity");
        }
        
        // PIECE ACTIVITY BONUS (+0.1 to +0.3)
        if (isPieceActivatingMove(move)) {
            score += 20; // +0.2 pawns for piece activity
            Log.d(TAG, "📊 " + move + ": +20cp piece activity");
        }
        
        // AGGRESSIVE MOVE BONUS (+0.1 to +0.2)
        if (isAggressiveMove(move, variation)) {
            score += 15; // +0.15 pawns for aggression
            Log.d(TAG, "📊 " + move + ": +15cp aggression");
        }
        
        // SACRIFICIAL MOVE BONUS (+0.2 to +0.5) - If compensation unclear
        if (isSacrificialMove(move, variation, candidate.getEvaluation())) {
            score += 25; // +0.25 pawns for interesting sacrifices
            Log.d(TAG, "📊 " + move + ": +25cp sacrificial play");
        }
        
        // PASSIVE MOVE PENALTY (-0.2 to -0.4)
        if (isPassiveMove(move, fen)) {
            score -= 25; // -0.25 pawns for passive moves
            Log.d(TAG, "📊 " + move + ": -25cp passive play");
        }
        
        // KING SAFETY MODERATE PENALTY (-0.1) - Alekhine takes calculated risks
        if (weakensKingSafety(move, fen)) {
            score -= 10; // -0.1 pawns (less penalty than other masters)
            Log.d(TAG, "📊 " + move + ": -10cp king safety risk");
        }
        
        return score;
    }
    
    /**
     * Check if move involves tactical complexity (checks, captures, threats)
     */
    private boolean isTacticalMove(String move, String variation) {
        // Check for captures (x in variation)
        if (variation.contains("x")) return true;
        
        // Check for checks (+ in variation)
        if (variation.contains("+")) return true;
        
        // Check for promotion
        if (move.length() == 5) return true;
        
        // Check for piece moves to central squares (tactical potential)
        String toSquare = move.substring(2, 4);
        String[] centralSquares = {"d4", "d5", "e4", "e5", "c4", "c5", "f4", "f5"};
        for (String square : centralSquares) {
            if (square.equals(toSquare)) return true;
        }
        
        return false;
    }
    
    /**
     * Check if move activates pieces (knights, bishops to good squares)
     */
    private boolean isPieceActivatingMove(String move) {
        String fromSquare = move.substring(0, 2);
        String toSquare = move.substring(2, 4);
        
        // Knight moves to good squares
        if (isKnightMove(fromSquare, toSquare)) {
            String[] goodKnightSquares = {"c3", "f3", "c6", "f6", "d5", "e5", "d4", "e4"};
            for (String square : goodKnightSquares) {
                if (square.equals(toSquare)) return true;
            }
        }
        
        // Bishop moves to long diagonals
        if (isBishopMove(fromSquare, toSquare)) {
            String[] goodBishopSquares = {"c4", "f4", "c5", "f5", "b5", "g5", "b2", "g2"};
            for (String square : goodBishopSquares) {
                if (square.equals(toSquare)) return true;
            }
        }
        
        // Rook moves to open files (simplified heuristic)
        if (isRookMove(fromSquare, toSquare)) {
            char file = toSquare.charAt(0);
            if (file == 'd' || file == 'e' || file == 'c' || file == 'f') return true;
        }
        
        return false;
    }
    
    /**
     * Check if move is aggressive (advances toward opponent)
     */
    private boolean isAggressiveMove(String move, String variation) {
        String toSquare = move.substring(2, 4);
        int toRank = Character.getNumericValue(toSquare.charAt(1));
        
        // Moves advancing toward opponent (assuming white perspective for simplicity)
        if (toRank >= 5) return true;
        
        // Pawn advances
        String fromSquare = move.substring(0, 2);
        if (isPawnMove(fromSquare, toSquare)) {
            int fromRank = Character.getNumericValue(fromSquare.charAt(1));
            if (toRank > fromRank) return true; // Advancing pawn
        }
        
        return false;
    }
    
    /**
     * Check if move involves a sacrifice with unclear compensation
     */
    private boolean isSacrificialMove(String move, String variation, float evaluation) {
        // Look for captures where evaluation drops (indicating sacrifice)
        if (variation.contains("x")) {
            // If evaluation is negative but move contains capture, might be sacrifice
            if (evaluation < -50) return true;
        }
        
        // Check for typical sacrificial patterns
        String toSquare = move.substring(2, 4);
        String[] sacrificialSquares = {"f7", "f2", "g7", "g2", "h7", "h2"};
        for (String square : sacrificialSquares) {
            if (square.equals(toSquare) && variation.contains("x")) return true;
        }
        
        return false;
    }
    
    /**
     * Check if move is passive (retreating or defensive)
     */
    private boolean isPassiveMove(String move, String fen) {
        String fromSquare = move.substring(0, 2);
        String toSquare = move.substring(2, 4);
        
        int fromRank = Character.getNumericValue(fromSquare.charAt(1));
        int toRank = Character.getNumericValue(toSquare.charAt(1));
        
        // Piece retreating toward own back rank
        if (toRank < fromRank && toRank <= 3) return true;
        
        // King moves (often defensive)
        if (isKingMove(fromSquare, toSquare)) return true;
        
        return false;
    }
    
    /**
     * Check if move weakens king safety
     */
    private boolean weakensKingSafety(String move, String fen) {
        String toSquare = move.substring(2, 4);
        
        // Moves that weaken castling position
        String[] kingSideWeakening = {"g3", "h3", "g4", "h4", "f3", "f4"};
        String[] queenSideWeakening = {"a3", "b3", "c3", "a4", "b4", "c4"};
        
        for (String square : kingSideWeakening) {
            if (square.equals(toSquare)) return true;
        }
        
        for (String square : queenSideWeakening) {
            if (square.equals(toSquare)) return true;
        }
        
        return false;
    }
    
    // Helper methods for piece movement detection
    private boolean isKnightMove(String from, String to) {
        int dx = Math.abs(from.charAt(0) - to.charAt(0));
        int dy = Math.abs(from.charAt(1) - to.charAt(1));
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }
    
    private boolean isBishopMove(String from, String to) {
        int dx = Math.abs(from.charAt(0) - to.charAt(0));
        int dy = Math.abs(from.charAt(1) - to.charAt(1));
        return dx == dy && dx > 0;
    }
    
    private boolean isRookMove(String from, String to) {
        return (from.charAt(0) == to.charAt(0)) || (from.charAt(1) == to.charAt(1));
    }
    
    private boolean isPawnMove(String from, String to) {
        return from.charAt(0) == to.charAt(0) || Math.abs(from.charAt(0) - to.charAt(0)) == 1;
    }
    
    private boolean isKingMove(String from, String to) {
        int dx = Math.abs(from.charAt(0) - to.charAt(0));
        int dy = Math.abs(from.charAt(1) - to.charAt(1));
        return dx <= 1 && dy <= 1;
    }
}