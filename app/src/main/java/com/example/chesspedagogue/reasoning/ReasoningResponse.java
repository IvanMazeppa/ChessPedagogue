package com.example.chesspedagogue.reasoning;

/**
 * Response from the reasoning engine containing move selection and analysis
 */
public class ReasoningResponse {
    private final String move;              // Selected move in UCI format
    private final String explanation;       // Reasoning behind the move
    private final float confidence;         // Confidence level (0.0 to 1.0)
    private final String masterPersonality; // Personality-specific commentary
    private final boolean isHistoricalMatch; // If move matches historical games
    private final String historicalContext;  // Context from historical games
    private final long reasoningTimeMs;     // Time spent reasoning
    private final String rawResponse;       // Full API response for debugging

    public ReasoningResponse(String move, String explanation, float confidence, 
                           String masterPersonality, boolean isHistoricalMatch, 
                           String historicalContext, long reasoningTimeMs, String rawResponse) {
        this.move = move;
        this.explanation = explanation;
        this.confidence = confidence;
        this.masterPersonality = masterPersonality;
        this.isHistoricalMatch = isHistoricalMatch;
        this.historicalContext = historicalContext;
        this.reasoningTimeMs = reasoningTimeMs;
        this.rawResponse = rawResponse;
    }

    // Builder pattern for easy construction
    public static class Builder {
        private String move;
        private String explanation = "";
        private float confidence = 0.5f;
        private String masterPersonality = "";
        private boolean isHistoricalMatch = false;
        private String historicalContext = "";
        private long reasoningTimeMs = 0;
        private String rawResponse = "";

        public Builder(String move) {
            this.move = move;
        }

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder confidence(float confidence) {
            this.confidence = Math.max(0.0f, Math.min(1.0f, confidence));
            return this;
        }

        public Builder masterPersonality(String personality) {
            this.masterPersonality = personality;
            return this;
        }

        public Builder historicalMatch(boolean isMatch, String context) {
            this.isHistoricalMatch = isMatch;
            this.historicalContext = context;
            return this;
        }

        public Builder reasoningTime(long timeMs) {
            this.reasoningTimeMs = timeMs;
            return this;
        }

        public Builder rawResponse(String response) {
            this.rawResponse = response;
            return this;
        }

        public ReasoningResponse build() {
            return new ReasoningResponse(move, explanation, confidence, masterPersonality,
                    isHistoricalMatch, historicalContext, reasoningTimeMs, rawResponse);
        }
    }

    // Getters
    public String getMove() { return move; }
    public String getExplanation() { return explanation; }
    public float getConfidence() { return confidence; }
    public String getMasterPersonality() { return masterPersonality; }
    public boolean isHistoricalMatch() { return isHistoricalMatch; }
    public String getHistoricalContext() { return historicalContext; }
    public long getReasoningTimeMs() { return reasoningTimeMs; }
    public String getRawResponse() { return rawResponse; }

    /**
     * Get confidence level as text
     */
    public String getConfidenceText() {
        if (confidence >= 0.9f) return "Very High";
        if (confidence >= 0.7f) return "High";
        if (confidence >= 0.5f) return "Medium";
        if (confidence >= 0.3f) return "Low";
        return "Very Low";
    }

    /**
     * Check if this is a high-quality response
     */
    public boolean isHighQuality() {
        return confidence >= 0.7f && !explanation.isEmpty() && !move.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ReasoningResponse{move='%s', confidence=%s, historical=%s, timeMs=%d}",
                move, getConfidenceText(), isHistoricalMatch, reasoningTimeMs);
    }
}