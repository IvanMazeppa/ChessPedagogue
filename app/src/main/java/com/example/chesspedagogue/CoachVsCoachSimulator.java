package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chesspedagogue.repository.GameRepository;
import com.example.chesspedagogue.ChessCoachManager;
import com.example.chesspedagogue.viewmodel.GameViewModel;

public class CoachVsCoachSimulator {

    private static final String TAG = "CoachVsCoachSimulator";

    private final Context context;
    private final GameRepository gameRepository;
    private final ChessCoachManager coachManager;
    private final Handler mainHandler;
    private final String whiteCoach;
    private final String blackCoach;
    private final int moveTimeMs;
    private final int moveLimit;

    private boolean whiteToMove = true;
    private int moveCount = 0;

    public CoachVsCoachSimulator(Context context,
                                 GameRepository gameRepository,
                                 ChessCoachManager coachManager,
                                 String whiteCoach,
                                 String blackCoach,
                                 int moveTimeMs,
                                 int moveLimit) {
        this.context = context;
        this.gameRepository = gameRepository;
        this.coachManager = coachManager;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.whiteCoach = whiteCoach;
        this.blackCoach = blackCoach;
        this.moveTimeMs = moveTimeMs;
        this.moveLimit = moveLimit;
    }

    public void start() {
        Log.i(TAG, "Starting Coach Match: " + whiteCoach + " vs " + blackCoach);
        gameRepository.newGame();
        moveCount = 0;
        whiteToMove = true;
        nextMove();
    }

    private void nextMove() {
        if (moveCount >= moveLimit) {
            Log.i(TAG, "Move limit reached (" + moveLimit + ")");
            endGame();
            return;
        }
        if (gameRepository.isCheckmate()) {
            Log.i(TAG, "Checkmate detected.");
            endGame();
            return;
        }
        // You can add stalemate/draw logic here if implemented in repo
        // if (gameRepository.isStalemate()) { ... }

        String currentCoach = whiteToMove ? whiteCoach : blackCoach;
        coachManager.selectChessMaster(currentCoach);

        Log.d(TAG, "Turn: " + (whiteToMove ? "White" : "Black") + " (" + currentCoach + ")");
        String fen = gameRepository.getCurrentFEN();

        // Calculate best move for this side (async)
        gameRepository.calculateBestMove(new GameRepository.MoveCallback() {
            @Override
            public void onMoveCalculated(String move) {
                Log.d(TAG, "Engine move for " + (whiteToMove ? "White" : "Black") + ": " + move);
                if (move == null || move.length() < 4) {
                    Log.e(TAG, "Invalid move received: " + move);
                    endGame();
                    return;
                }
                boolean applied = gameRepository.makeMove(move);
                if (!applied) {
                    Log.e(TAG, "Failed to apply move: " + move);
                    endGame();
                    return;
                }

                moveCount++;
                // Ask the coach to comment on their move (async)
                String moveDescription = move; // For now just UCI; could use SAN/FEN if you want
                String prompt = "I played " + moveDescription + ". Please provide a brief comment as " + currentCoach + ".";
                coachManager.sendMessage(prompt, new ChessCoachManager.ChessCoachCallback() {
                    @Override
                    public void onResponseReceived(String response) {
                        Log.d(TAG, currentCoach + " says: " + response);
                    }
                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Coach " + currentCoach + " error: " + errorMessage);
                    }
                    @Override
                    public void onSpeechCompleted() {
                        // After coach speaks, proceed to next move
                        whiteToMove = !whiteToMove;
                        // Post to main handler for clean thread handling
                        mainHandler.post(() -> nextMove());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error from engine: " + errorMessage);
                endGame();
            }
        });
    }

    private void endGame() {
        String resultMsg = "Game over! Moves played: " + moveCount;
        Log.i(TAG, resultMsg);
        coachManager.sendMessage(resultMsg, new ChessCoachManager.ChessCoachCallback() {
            @Override public void onResponseReceived(String response) {
                Log.i(TAG, "Final message: " + response);
            }
            @Override public void onError(String errorMessage) {
                Log.e(TAG, "Coach error on end: " + errorMessage);
            }
            @Override public void onSpeechCompleted() { }
        });
    }
}
