package com.example.chesspedagogue.reasoning;

/**
 * Represents a candidate move with evaluation data from Stockfish
 */
public class CandidateMove {
    private final String move;           // UCI notation (e2e4, g1f3, etc.)
    private final float evaluation;      // Centipawns (positive = white advantage)
    private final int depth;            // Analysis depth
    private final String variation;      // Principal variation
    private final long nodes;           // Nodes searched
    private final int multiPvRank;      // MultiPV ranking (1 = best, 2 = second best, etc.)

    public CandidateMove(String move, float evaluation, int depth, String variation, long nodes, int multiPvRank) {
        this.move = move;
        this.evaluation = evaluation;
        this.depth = depth;
        this.variation = variation;
        this.nodes = nodes;
        this.multiPvRank = multiPvRank;
    }

    // Getters
    public String getMove() { return move; }
    public float getEvaluation() { return evaluation; }
    public int getDepth() { return depth; }
    public String getVariation() { return variation; }
    public long getNodes() { return nodes; }
    public int getMultiPvRank() { return multiPvRank; }

    /**
     * Convert evaluation to human-readable format
     */
    public String getEvaluationText() {
        if (evaluation > 900) {
            return "M" + (int)((1000 - evaluation) / 100);  // Mate in X
        } else if (evaluation < -900) {
            return "M-" + (int)((1000 + evaluation) / 100); // Mate in X for opponent
        } else {
            return String.format("%.2f", evaluation / 100.0f);
        }
    }

    /**
     * Check if this is a forcing move (check, capture, or significant material gain)
     */
    public boolean isForcingMove() {
        return variation.contains("+") || variation.contains("x") || Math.abs(evaluation) > 50;
    }

    /**
     * Get move quality assessment based on evaluation and rank
     */
    public String getQualityAssessment() {
        if (multiPvRank == 1 && evaluation > 100) return "Excellent";
        if (multiPvRank == 1 && evaluation > 0) return "Good";
        if (multiPvRank <= 2 && evaluation > -50) return "Acceptable";
        if (evaluation < -100) return "Poor";
        return "Neutral";
    }

    @Override
    public String toString() {
        return String.format("CandidateMove{move='%s', eval=%s, depth=%d, rank=%d, quality=%s}",
                move, getEvaluationText(), depth, multiPvRank, getQualityAssessment());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CandidateMove that = (CandidateMove) obj;
        return move.equals(that.move);
    }

    @Override
    public int hashCode() {
        return move.hashCode();
    }
}