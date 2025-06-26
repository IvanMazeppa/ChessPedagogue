package com.example.chesspedagogue;

/**
 * 🎯 PuzzleResult - Result of a Single Puzzle Attempt
 * 
 * Captures the outcome of a user or assistant attempting to solve
 * a tactical puzzle, including performance metrics and feedback.
 */
public class PuzzleResult {
    public final String puzzleId;           // ID of the puzzle attempted
    public final boolean correct;           // Whether the solution was correct
    public final long solveTime;           // Time taken in milliseconds
    public final String userMove;          // Move chosen by user
    public final String correctMove;       // The correct solution
    public final String assistantMove;     // Move chosen by assistant (if applicable)
    public final double confidence;        // Confidence score (0.0-1.0)
    public final String feedback;          // Feedback message for the user
    
    // Additional metadata
    public final long timestamp;           // When the attempt was made
    public final boolean wasHintUsed;      // Whether user requested a hint
    public final int attemptNumber;        // Which attempt this was (1, 2, 3...)
    
    /**
     * Primary constructor for puzzle results
     */
    public PuzzleResult(String puzzleId, boolean correct, long solveTime,
                       String userMove, String correctMove, String assistantMove,
                       double confidence, String feedback) {
        this.puzzleId = puzzleId;
        this.correct = correct;
        this.solveTime = solveTime;
        this.userMove = userMove;
        this.correctMove = correctMove;
        this.assistantMove = assistantMove;
        this.confidence = Math.max(0.0, Math.min(1.0, confidence)); // Clamp 0-1
        this.feedback = feedback;
        
        // Set metadata
        this.timestamp = System.currentTimeMillis();
        this.wasHintUsed = false;
        this.attemptNumber = 1;
    }
    
    /**
     * Extended constructor with metadata
     */
    public PuzzleResult(String puzzleId, boolean correct, long solveTime,
                       String userMove, String correctMove, String assistantMove,
                       double confidence, String feedback, boolean wasHintUsed,
                       int attemptNumber) {
        this.puzzleId = puzzleId;
        this.correct = correct;
        this.solveTime = solveTime;
        this.userMove = userMove;
        this.correctMove = correctMove;
        this.assistantMove = assistantMove;
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
        this.feedback = feedback;
        this.timestamp = System.currentTimeMillis();
        this.wasHintUsed = wasHintUsed;
        this.attemptNumber = attemptNumber;
    }
    
    /**
     * ⏱️ Get solve time in seconds
     */
    public double getSolveTimeSeconds() {
        return solveTime / 1000.0;
    }
    
    /**
     * 🏆 Get points earned for this result
     */
    public int getPointsEarned(TacticalPuzzle puzzle) {
        if (!correct) return 0;
        
        int basePoints = puzzle.difficulty * 10;
        
        // Time bonuses
        if (solveTime < 5000) basePoints += 20;        // Under 5 seconds
        else if (solveTime < 15000) basePoints += 10;  // Under 15 seconds
        else if (solveTime < 30000) basePoints += 5;   // Under 30 seconds
        
        // Confidence bonus
        basePoints += (int) (confidence * 10);
        
        // Attempt penalty
        if (attemptNumber > 1) {
            basePoints = (int) (basePoints * (1.0 - (attemptNumber - 1) * 0.2));
        }
        
        // Hint penalty
        if (wasHintUsed) {
            basePoints = (int) (basePoints * 0.8);
        }
        
        return Math.max(1, basePoints); // Minimum 1 point
    }
    
    /**
     * 📊 Get performance grade
     */
    public String getPerformanceGrade() {
        if (!correct) return "F";
        
        double timeSeconds = getSolveTimeSeconds();
        
        if (confidence >= 0.9 && timeSeconds < 10) return "A+";
        if (confidence >= 0.8 && timeSeconds < 20) return "A";
        if (confidence >= 0.7 && timeSeconds < 30) return "B+";
        if (confidence >= 0.6 && timeSeconds < 45) return "B";
        if (confidence >= 0.5 && timeSeconds < 60) return "C+";
        if (timeSeconds < 90) return "C";
        return "D";
    }
    
    /**
     * 🎯 Get display message for user feedback
     */
    public String getDisplayMessage() {
        if (correct) {
            String grade = getPerformanceGrade();
            double timeSeconds = getSolveTimeSeconds();
            
            if (grade.equals("A+")) {
                return String.format("🔥 BRILLIANT! Solved in %.1fs - %s", timeSeconds, feedback);
            } else if (grade.startsWith("A")) {
                return String.format("✨ EXCELLENT! Solved in %.1fs - %s", timeSeconds, feedback);
            } else if (grade.startsWith("B")) {
                return String.format("👍 GOOD! Solved in %.1fs - %s", timeSeconds, feedback);
            } else {
                return String.format("✅ CORRECT! Solved in %.1fs - %s", timeSeconds, feedback);
            }
        } else {
            return String.format("❌ %s (Try: %s)", feedback, correctMove);
        }
    }
    
    /**
     * 🤖 Compare with assistant performance
     */
    public String getAssistantComparison() {
        if (assistantMove == null || assistantMove.isEmpty()) {
            return "Assistant didn't attempt this puzzle";
        }
        
        boolean assistantCorrect = assistantMove.equals(correctMove);
        
        if (correct && assistantCorrect) {
            return "🤝 Both you and Alekhine found the solution!";
        } else if (correct && !assistantCorrect) {
            return "🏆 You solved it, but Alekhine missed it!";
        } else if (!correct && assistantCorrect) {
            return "🤖 Alekhine solved it - study the position more!";
        } else {
            return "😅 Neither you nor Alekhine found the best move!";
        }
    }
    
    /**
     * 📈 Get improvement suggestions
     */
    public String getImprovementSuggestion(TacticalPuzzle puzzle) {
        if (correct) {
            if (getSolveTimeSeconds() > 45) {
                return "Try to spot " + puzzle.theme + " patterns more quickly!";
            }
            return "Great job! Try harder puzzles to improve further.";
        } else {
            return String.format("Study %s tactics - look for %s patterns!", 
                puzzle.theme, puzzle.theme.toLowerCase());
        }
    }
    
    /**
     * 🏅 Check if this result deserves special recognition
     */
    public boolean isExceptional(TacticalPuzzle puzzle) {
        return correct && 
               confidence >= 0.9 && 
               getSolveTimeSeconds() < 10 && 
               puzzle.difficulty >= 3 &&
               !wasHintUsed &&
               attemptNumber == 1;
    }
    
    /**
     * 📊 Get detailed statistics string
     */
    public String getDetailedStats() {
        return String.format(
            "Result: %s | Time: %.1fs | Grade: %s | Confidence: %.1f | Attempt: #%d",
            correct ? "✅" : "❌",
            getSolveTimeSeconds(),
            getPerformanceGrade(),
            confidence,
            attemptNumber
        );
    }
    
    @Override
    public String toString() {
        return String.format(
            "PuzzleResult{puzzle=%s, correct=%s, time=%.1fs, move=%s, grade=%s}",
            puzzleId, correct, getSolveTimeSeconds(), userMove, getPerformanceGrade()
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PuzzleResult result = (PuzzleResult) obj;
        return puzzleId.equals(result.puzzleId) && 
               timestamp == result.timestamp;
    }
    
    @Override
    public int hashCode() {
        return puzzleId.hashCode() + Long.hashCode(timestamp);
    }
}