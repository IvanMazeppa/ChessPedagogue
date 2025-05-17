package com.example.chesspedagogue;

public class ChallengeData {
    private String description;
    private String correctMove;
    private String opponentResponse;
    private String winningSecondMove;
    private String explanation;
    private String tacticalMotif;

    public ChallengeData(String description, String correctMove,
                         String opponentResponse, String winningSecondMove,
                         String explanation, String tacticalMotif) {
        this.description = description;
        this.correctMove = correctMove;
        this.opponentResponse = opponentResponse;
        this.winningSecondMove = winningSecondMove;
        this.explanation = explanation;
        this.tacticalMotif = tacticalMotif;
    }

    public String getDescription() {
        return description;
    }

    public String getCorrectMove() {
        return correctMove;
    }

    public String getOpponentResponse() {
        return opponentResponse;
    }

    public String getWinningSecondMove() {
        return winningSecondMove;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getTacticalMotif() {
        return tacticalMotif;
    }
}