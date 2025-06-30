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
            return parseMultiPVResults(analysisResults, maxCandidates);
            
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
}