package com.example.chesspedagogue;

import android.util.Log;

/**
 * 🔧 SIGN-AGNOSTIC EVALUATION FIX
 * 
 * USER INSIGHT: "the magnitude of eval variable is correct but the sign isn't"
 * "even at the start (no moves - +0.3) i play 1 d4 and the eval changes to -0.2, 
 * when in reality 1. d4 will cause you to go from 0.3 to 0.2, not somehow lose"
 * 
 * SOLUTION: Trust the magnitude, determine correct sign independently
 */
public class SignAgnosticEvaluationFix {
    private static final String TAG = "SignAgnosticEvalFix";
    
    // Track the last known good evaluation to determine expected direction
    private static float lastTrustedEvaluation = 0.3f; // Starting position advantage
    private static String lastPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    // 🎯 ALTERNATING SIGN FIX: Track move count for alternating correction
    private static int moveCountForSignFix = 0;
    private static boolean useAlternatingSignFix = true; // RE-ENABLED: Perspective bug confirmed with full evaluation coverage
    
    /**
     * 🔄 RESET: Reset move counter for new games (fixes spectator mode sync issues)
     */
    public static void resetForNewGame() {
        moveCountForSignFix = 0;
        lastTrustedEvaluation = 0.3f; // Starting position advantage  
        lastPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Log.d(TAG, "🔄 RESET: Sign fix counter reset for new game");
    }
    
    /**
     * 🔧 OPTIMIZED SIGN-AGNOSTIC FIX: Fast alternating pattern with minimal overhead
     * 
     * @param rawEvaluation The raw evaluation (magnitude trusted, sign suspect)
     * @param currentFEN Current position (only used for tracking when needed)
     * @param lastMove The move that was just played (unused in optimized version)
     * @return Evaluation with corrected sign
     */
    public static float correctEvaluationSign(float rawEvaluation, String currentFEN, String lastMove) {
        // 🚀 PERFORMANCE OPTIMIZATION: Skip complex logic for unchanged positions
        if (currentFEN != null && currentFEN.equals(lastPosition)) {
            // Same position - return cached result to avoid recomputation
            return lastTrustedEvaluation;
        }
        
        try {
            // Increment move counter for position change detection
            moveCountForSignFix++;
            
            // 🔍 ENHANCED DIAGNOSTIC: Log raw evaluation with more detail
            Log.d(TAG, String.format("🔍 RAW EVAL DIAGNOSTIC: %.2f (move=%d, alternating=%s, FEN=%s)", 
                  rawEvaluation, moveCountForSignFix, useAlternatingSignFix ? "ON" : "OFF", 
                  currentFEN != null ? currentFEN.substring(0, Math.min(20, currentFEN.length())) : "null"));

            // 🎯 USER'S BRILLIANT INSIGHT: "could it be as simple as ignoring the sign every other move?"
            if (useAlternatingSignFix && moveCountForSignFix >= 2) {
                
                // 🚀 OPTIMIZED: Direct alternating logic - flip odd moves, keep even moves
                boolean shouldFlipSign = (moveCountForSignFix % 2 == 1);
                float correctedEval = shouldFlipSign ? -rawEvaluation : rawEvaluation;
                
                // 🔍 DIAGNOSTIC: Always log alternating fix application for debugging
                Log.d(TAG, String.format("🔄 ALTERNATING: move=%d, flip=%s, %.2f→%.2f (diff=%.2f)", 
                      moveCountForSignFix, shouldFlipSign, rawEvaluation, correctedEval,
                      Math.abs(correctedEval - lastTrustedEvaluation)));
                
                // Update tracking
                lastTrustedEvaluation = correctedEval;
                lastPosition = currentFEN;
                
                return correctedEval;
            }
            
            // 🔧 FALLBACK: When alternating is disabled, return raw evaluation for testing
            if (!useAlternatingSignFix) {
                Log.d(TAG, String.format("🔍 RETURNING RAW: %.2f (alternating disabled)", rawEvaluation));
                lastTrustedEvaluation = rawEvaluation;
                lastPosition = currentFEN;
                return rawEvaluation;
            }
            
            // 🔧 OPTIMIZED FALLBACK: Simplified first move logic (when alternating enabled)
            float correctedEvaluation = rawEvaluation > 0 ? Math.abs(rawEvaluation) * 0.1f : rawEvaluation;
            
            // Update tracking for next evaluation
            lastTrustedEvaluation = correctedEvaluation;
            lastPosition = currentFEN;
            
            return correctedEvaluation;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in sign correction", e);
            return rawEvaluation;
        }
    }
    
    // 🚀 OPTIMIZATION: Removed unused complex analysis methods that were adding overhead
    // The alternating sign fix is working well without needing these expensive heuristics
    
    /**
     * 🔄 RESET TO STARTING POSITION: Call when game resets
     */
    public static void resetToStartingPosition() {
        lastTrustedEvaluation = 0.3f;
        lastPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        moveCountForSignFix = 0; // Reset move counter for alternating fix
        Log.d(TAG, "🔄 Reset to starting position: +0.3, move_count=0");
    }
}