package com.example.chesspedagogue.ui.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import com.example.chesspedagogue.ChessBoardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SlidingChessPuzzleManager {
    private static final String TAG = "SlidingChessPuzzle";
    
    public interface AnimationCompleteCallback {
        void onAnimationComplete();
        void onAnimationStart();
    }
    
    private static final int GRID_SIZE = 8; // 8x8 chessboard
    private static final int TOTAL_SQUARES = 64;
    private static final long BASE_ANIMATION_DURATION = 600; // ms per square
    private static final long STAGGER_DELAY = 30; // ms between square starts
    
    public static void startPuzzleSolve(ViewGroup chessboardContainer, 
                                       AnimationCompleteCallback callback) {
        Log.d(TAG, "🧩 Starting 64-square puzzle solving animation on real ChessBoardView");
        
        if (callback != null) {
            callback.onAnimationStart();
        }
        
        // Find the ChessBoardView within the container
        ChessBoardView chessBoardView = findChessBoardView(chessboardContainer);
        if (chessBoardView == null) {
            Log.e(TAG, "❌ Could not find ChessBoardView in container");
            if (callback != null) {
                callback.onAnimationComplete();
            }
            return;
        }
        
        // Start the puzzle solving animation on the ChessBoardView
        startChessBoardPuzzleAnimation(chessBoardView, callback);
    }
    
    public static void startPuzzleSolveOnChessBoardView(ChessBoardView chessBoardView, 
                                                       AnimationCompleteCallback callback) {
        Log.d(TAG, "🧩 Starting 64-square puzzle solving animation on direct ChessBoardView reference");
        
        if (callback != null) {
            callback.onAnimationStart();
        }
        
        if (chessBoardView == null) {
            Log.e(TAG, "❌ ChessBoardView is null");
            if (callback != null) {
                callback.onAnimationComplete();
            }
            return;
        }
        
        // Start the puzzle solving animation on the ChessBoardView
        startChessBoardPuzzleAnimation(chessBoardView, callback);
    }
    
    private static ChessBoardView findChessBoardView(ViewGroup container) {
        Log.d(TAG, "🔍 Searching for ChessBoardView in container: " + container.getClass().getSimpleName());
        Log.d(TAG, "🔍 Container has " + container.getChildCount() + " children");
        
        // Search for ChessBoardView in the container hierarchy
        for (int i = 0; i < container.getChildCount(); i++) {
            android.view.View child = container.getChildAt(i);
            Log.d(TAG, "🔍 Child " + i + ": " + child.getClass().getSimpleName());
            
            if (child instanceof ChessBoardView) {
                Log.d(TAG, "✅ Found ChessBoardView at index " + i);
                return (ChessBoardView) child;
            } else if (child instanceof ViewGroup) {
                Log.d(TAG, "🔍 Searching in child ViewGroup: " + child.getClass().getSimpleName());
                ChessBoardView found = findChessBoardView((ViewGroup) child);
                if (found != null) {
                    Log.d(TAG, "✅ Found ChessBoardView in nested container");
                    return found;
                }
            }
        }
        
        Log.e(TAG, "❌ ChessBoardView not found in container hierarchy");
        return null;
    }
    
    private static void startChessBoardPuzzleAnimation(ChessBoardView chessBoardView, 
                                                      AnimationCompleteCallback callback) {
        
        Log.d(TAG, "🎯 Starting sliding puzzle animation on ChessBoardView");
        Log.d(TAG, "🎯 ChessBoardView null check: " + (chessBoardView == null ? "NULL" : "NOT NULL"));
        
        // Add sliding puzzle capability to ChessBoardView if it doesn't exist
        if (!chessBoardView.hasSlidingPuzzleCapability()) {
            Log.d(TAG, "🔧 Adding sliding puzzle capability to ChessBoardView");
            chessBoardView.enableSlidingPuzzleMode();
        }
        
        // Generate scrambled order for all 64 squares
        List<Integer> scrambledOrder = generateScrambledOrder();
        List<Integer> solvingOrder = createSolvingOrder();
        
        // Apply scrambled positions instantly
        chessBoardView.setSquarePositions(scrambledOrder);
        
        // Animate squares to their correct positions with staggered timing
        animateSquaresToTargetPositions(chessBoardView, solvingOrder, callback);
    }
    
    private static List<Integer> generateScrambledOrder() {
        List<Integer> order = new ArrayList<>();
        
        // Create normal order first (0-63)
        for (int i = 0; i < TOTAL_SQUARES; i++) {
            order.add(i);
        }
        
        // Scramble the order - each square gets a random target position
        Collections.shuffle(order);
        
        Log.d(TAG, "🎲 Generated scrambled order for " + order.size() + " squares");
        return order;
    }
    
    private static List<Integer> createSolvingOrder() {
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < TOTAL_SQUARES; i++) {
            order.add(i);
        }
        
        // Shuffle for random solving pattern
        Collections.shuffle(order);
        
        // Alternative patterns could be:
        // - Outside-in spiral: corners first, then edges, then center
        // - Row-by-row: complete rows from bottom to top
        // - Diagonal sweep: solve diagonally across the board
        
        Log.d(TAG, "🔀 Created randomized solving order");
        return order;
    }
    
    private static void animateSquaresToTargetPositions(ChessBoardView chessBoardView,
                                                       List<Integer> solvingOrder,
                                                       AnimationCompleteCallback callback) {
        
        Log.d(TAG, "🎬 Starting square animations with " + STAGGER_DELAY + "ms stagger");
        
        final int[] completedAnimations = {0};
        final int totalAnimations = TOTAL_SQUARES;
        
        for (int i = 0; i < TOTAL_SQUARES; i++) {
            int squareIndex = i;
            int solveIndex = solvingOrder.get(i);
            
            // Target position is the normal position (row/col from square index)
            int targetRow = i / 8;
            int targetCol = i % 8;
            
            // Calculate delay based on solving order
            long delay = solveIndex * STAGGER_DELAY;
            
            // Create animation for this square
            ValueAnimator squareAnimation = chessBoardView.createSquareSlideAnimation(
                squareIndex, targetRow, targetCol, BASE_ANIMATION_DURATION);
            
            squareAnimation.setInterpolator(new OvershootInterpolator(0.3f));
            squareAnimation.setStartDelay(delay);
            
            // Track completion
            squareAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    completedAnimations[0]++;
                    if (completedAnimations[0] == totalAnimations) {
                        Log.d(TAG, "✅ All squares solved! Puzzle animation complete");
                        
                        // Disable sliding puzzle mode and return to normal
                        chessBoardView.disableSlidingPuzzleMode();
                        
                        if (callback != null) {
                            callback.onAnimationComplete();
                        }
                    }
                }
            });
            
            squareAnimation.start();
            
            if (i % 16 == 0) { // Log progress every 16 squares
                Log.d(TAG, "🧩 Started animation for square " + i + " (solve order: " + solveIndex + ")");
            }
        }
        
        // Calculate total animation time
        long totalTime = (TOTAL_SQUARES * STAGGER_DELAY) + BASE_ANIMATION_DURATION;
        Log.d(TAG, "⏱️ Total puzzle solving time: " + totalTime + "ms");
    }
    
    public static void cleanup(ViewGroup chessboardContainer) {
        // Find the ChessBoardView and ensure it's in normal mode
        ChessBoardView chessBoardView = findChessBoardView(chessboardContainer);
        if (chessBoardView != null) {
            chessBoardView.disableSlidingPuzzleMode();
            Log.d(TAG, "🧹 Cleaned up sliding puzzle mode");
        }
    }
}