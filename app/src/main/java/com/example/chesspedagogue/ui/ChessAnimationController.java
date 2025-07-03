package com.example.chesspedagogue.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.Map;

/**
 * Master Chess Animation Controller for Samsung S23 Ultra
 * Coordinates all physics animations, captured pieces, and visual effects
 */
public class ChessAnimationController {
    private static final String TAG = "ChessAnimationController";
    
    private final Context context;
    private final ViewGroup gameContainer;
    private final View boardView;
    
    // Animation system components
    private CapturedPiecesManager capturedPiecesManager;
    private CheckIndicator checkIndicator;
    
    // Track piece positions and states
    private final Map<String, View> currentPieces = new HashMap<>();
    private final Map<String, String> piecePositions = new HashMap<>(); // position -> pieceId
    private boolean isWhiteInCheck = false;
    private boolean isBlackInCheck = false;
    
    // Animation settings
    private boolean animationsEnabled = true;
    private float animationSpeedMultiplier = 1.0f;
    
    public ChessAnimationController(Context context, ViewGroup gameContainer, View boardView) {
        this.context = context;
        this.gameContainer = gameContainer;
        this.boardView = boardView;
        
        initializeAnimationSystems();
    }
    
    /**
     * Initialize all animation subsystems
     */
    private void initializeAnimationSystems() {
        Log.d(TAG, "🚀 Initializing chess animation systems for S23 Ultra");
        
        // Initialize captured pieces manager
        capturedPiecesManager = new CapturedPiecesManager(context, gameContainer);
        
        // Initialize check indicator
        checkIndicator = new CheckIndicator(context);
        
        Log.d(TAG, "✅ Animation systems initialized");
    }
    
    /**
     * Execute a chess move with full animation suite
     */
    public void executeAnimatedMove(String fromPosition, String toPosition, 
                                  String pieceType, boolean isWhitePiece,
                                  boolean isCapture, String capturedPieceType,
                                  Runnable onMoveComplete) {
        
        Log.d(TAG, "🎭 Executing animated move: " + fromPosition + " -> " + toPosition + 
                   (isCapture ? " (capture)" : ""));
        
        // Convert positions to board coordinates
        int[] fromCoords = positionToArrayCoords(fromPosition);
        int[] toCoords = positionToArrayCoords(toPosition);
        
        if (fromCoords == null || toCoords == null) {
            Log.w(TAG, "❌ Invalid coordinates for move");
            if (onMoveComplete != null) onMoveComplete.run();
            return;
        }
        
        // Verify we have a ChessBoardView to work with
        if (!(boardView instanceof com.example.chesspedagogue.ChessBoardView)) {
            Log.w(TAG, "❌ BoardView is not a ChessBoardView - animations disabled");
            if (onMoveComplete != null) onMoveComplete.run();
            return;
        }
        
        com.example.chesspedagogue.ChessBoardView chessBoardView = (com.example.chesspedagogue.ChessBoardView) boardView;
        
        // Check if there's actually a piece to move
        char movingPiece = chessBoardView.getPieceAt(fromCoords[0], fromCoords[1]);
        if (movingPiece == ' ') {
            Log.w(TAG, "❌ No piece found at " + fromPosition + " to animate");
            if (onMoveComplete != null) onMoveComplete.run();
            return;
        }
        
        Log.d(TAG, "✅ Found piece '" + movingPiece + "' to animate from " + fromPosition + " to " + toPosition);
        
        // Add spectacular movement trail effect for all moves
        float[] fromBoardCoords = positionToBoardCoordinates(fromPosition);
        float[] toBoardCoords = positionToBoardCoordinates(toPosition);
        addMovementTrail(fromBoardCoords[0], fromBoardCoords[1], toBoardCoords[0], toBoardCoords[1], 
                        pieceType, isWhitePiece);
        
        if (isCapture) {
            // Handle capture with physics animation
            handleCaptureAnimation(toPosition, capturedPieceType, !isWhitePiece, () -> {
                // Use ChessBoardView's built-in animation system
                chessBoardView.animateMove(fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);
                
                // Complete after animation duration
                chessBoardView.postDelayed(() -> {
                    if (onMoveComplete != null) onMoveComplete.run();
                }, 250); // Match ChessBoardView animation duration
            });
        } else {
            // Use ChessBoardView's built-in animation system for simple moves
            chessBoardView.animateMove(fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);
            
            // Complete after animation duration
            chessBoardView.postDelayed(() -> {
                if (onMoveComplete != null) onMoveComplete.run();
            }, 250); // Match ChessBoardView animation duration
        }
        
        // Trigger visual effects
        triggerMoveEffects(fromPosition, toPosition);
    }
    
    /**
     * Convert chess position to array coordinates
     */
    private int[] positionToArrayCoords(String position) {
        if (position == null || position.length() != 2) {
            return null;
        }
        
        char file = position.charAt(0); // a-h
        char rank = position.charAt(1); // 1-8
        
        int col = file - 'a'; // 0-7
        int row = 8 - (rank - '0'); // 0-7 (rank 8 is row 0)
        
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null;
        }
        
        return new int[]{row, col};
    }
    
    /**
     * Handle piece capture with physics animation
     */
    private void handleCaptureAnimation(String capturePosition, String capturedPieceType, 
                                      boolean capturedPieceIsWhite, Runnable onComplete) {
        
        Log.d(TAG, "💥 Animating piece capture at: " + capturePosition + " - " + capturedPieceType + 
                   " (white: " + capturedPieceIsWhite + ")");
        
        // Add captured piece to manager for display on edges
        if (capturedPieceType != null && capturedPiecesManager != null) {
            capturedPiecesManager.addCapturedPiece(capturedPieceType, capturedPieceIsWhite);
            Log.d(TAG, "✅ Added " + capturedPieceType + " to captured pieces display on edge");
        } else {
            Log.w(TAG, "⚠️ Cannot add captured piece - type: " + capturedPieceType + 
                       ", manager: " + (capturedPiecesManager != null ? "OK" : "NULL"));
        }
        
        // Trigger spectacular capture visual effects
        float[] coords = positionToBoardCoordinates(capturePosition);
        triggerEnhancedCaptureEffects(coords[0], coords[1], capturedPieceType, capturedPieceIsWhite);
        
        // Complete after longer delay for spectacular effects to finish
        if (boardView != null) {
            boardView.postDelayed(() -> {
                if (onComplete != null) onComplete.run();
            }, 800); // Extended delay for particle effects
        } else if (onComplete != null) {
            onComplete.run();
        }
    }
    
    /**
     * Animate piece movement with smooth curves
     */
    private void animatePieceMovement(View piece, float[] fromCoords, float[] toCoords, Runnable onComplete) {
        if (!animationsEnabled) {
            piece.setX(toCoords[0]);
            piece.setY(toCoords[1]);
            if (onComplete != null) onComplete.run();
            return;
        }
        
        PhysicsChessAnimations.animatePieceMovement(
            piece, 
            fromCoords[0], fromCoords[1], 
            toCoords[0], toCoords[1], 
            onComplete
        );
    }
    
    /**
     * Show check indicator for specified king
     */
    public void showCheckIndicator(boolean isWhiteKingInCheck) {
        Log.d(TAG, "🚨 Showing check indicator - White king: " + isWhiteKingInCheck);
        
        // Update check states
        isWhiteInCheck = isWhiteKingInCheck;
        isBlackInCheck = !isWhiteKingInCheck; // Assuming only one king can be in check
        
        // Find the king position on the board
        String kingPosition = findKingOnBoard(isWhiteKingInCheck);
        
        if (kingPosition != null && boardView instanceof com.example.chesspedagogue.ChessBoardView) {
            com.example.chesspedagogue.ChessBoardView chessBoardView = (com.example.chesspedagogue.ChessBoardView) boardView;
            
            // Convert position to coordinates for visual positioning
            float[] kingCoords = positionToBoardCoordinates(kingPosition);
            
            // Position check indicator near king
            positionCheckIndicator(kingCoords[0], kingCoords[1]);
            checkIndicator.showCheckIndicator();
            
            // Apply board-level check highlighting if available
            int[] arrayCoords = positionToArrayCoords(kingPosition);
            if (arrayCoords != null) {
                chessBoardView.setKingInCheck(true, arrayCoords[0], arrayCoords[1]);
            }
            
            Log.d(TAG, "✅ Check indicator positioned for " + (isWhiteKingInCheck ? "white" : "black") + " king at " + kingPosition);
        } else {
            Log.w(TAG, "❌ Could not locate king for check indicator");
        }
    }
    
    /**
     * Position check indicator at board coordinates
     */
    private void positionCheckIndicator(float x, float y) {
        // Add the check indicator to the game container if not already added
        if (checkIndicator.getParent() == null) {
            gameContainer.addView(checkIndicator);
        }
        
        // Calculate indicator size
        float indicatorSize = 48f * context.getResources().getDisplayMetrics().density;
        
        // Position the indicator next to the king square (to the right)
        float squareSize = boardView.getWidth() / 8.0f;
        float indicatorX = x + squareSize + 8f; // Right of the square with small margin
        float indicatorY = y + (squareSize - indicatorSize) / 2f; // Centered vertically on square
        
        checkIndicator.setX(indicatorX);
        checkIndicator.setY(indicatorY);
        
        // Set size using ConstraintLayout params if possible
        ViewGroup.LayoutParams params = checkIndicator.getLayoutParams();
        if (params == null) {
            if (gameContainer instanceof androidx.constraintlayout.widget.ConstraintLayout) {
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams constraintParams = 
                    new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams((int)indicatorSize, (int)indicatorSize);
                checkIndicator.setLayoutParams(constraintParams);
            } else {
                params = new ViewGroup.LayoutParams((int)indicatorSize, (int)indicatorSize);
                checkIndicator.setLayoutParams(params);
            }
        }
        
        Log.d(TAG, "📍 Check indicator positioned at (" + indicatorX + ", " + indicatorY + ") size " + indicatorSize);
    }
    
    /**
     * Hide check indicator
     */
    public void hideCheckIndicator() {
        Log.d(TAG, "✅ Hiding check indicator");
        
        isWhiteInCheck = false;
        isBlackInCheck = false;
        checkIndicator.hideCheckIndicator();
    }
    
    /**
     * Animate piece promotion
     */
    public void animatePiecePromotion(String position, String newPieceType, Runnable onComplete) {
        Log.d(TAG, "👑 Animating piece promotion at: " + position);
        
        View promotedPiece = findPieceAtPosition(position);
        if (promotedPiece != null) {
            PhysicsChessAnimations.animatePromotion(promotedPiece, onComplete);
        } else if (onComplete != null) {
            onComplete.run();
        }
    }
    
    /**
     * Add piece to board with entrance animation
     */
    public void addPieceToBoard(View pieceView, String position, String pieceType, boolean isWhite) {
        Log.d(TAG, "📦 Adding piece to board: " + pieceType + " at " + position);
        
        float[] coords = positionToBoardCoordinates(position);
        
        // Store piece information
        String pieceId = generatePieceId(pieceType, isWhite, position);
        currentPieces.put(pieceId, pieceView);
        piecePositions.put(position, pieceId);
        
        // Set piece tag for identification
        pieceView.setTag(pieceType + "_" + (isWhite ? "white" : "black"));
        
        // Animate entrance
        PhysicsChessAnimations.animatePiecePlacement(pieceView, coords[0], coords[1]);
    }
    
    /**
     * Trigger move effects (highlights and visual feedback)
     */
    private void triggerMoveEffects(String fromPosition, String toPosition) {
        if (boardView == null) return;
        
        float[] fromCoords = positionToBoardCoordinates(fromPosition);
        float[] toCoords = positionToBoardCoordinates(toPosition);
        
        // Highlight from square
        WorkingGlassEffects.applyMoveHighlight(
            boardView, 
            fromCoords[0] + 32f, fromCoords[1] + 32f, // Center of square
            0.8f, 
            new float[]{1.0f, 0.8f, 0.2f} // Golden
        );
        
        // Highlight to square with delay
        boardView.postDelayed(() -> {
            WorkingGlassEffects.applyMoveHighlight(
                boardView,
                toCoords[0] + 32f, toCoords[1] + 32f,
                0.6f,
                new float[]{0.2f, 0.8f, 1.0f} // Blue
            );
        }, 200);
        
        Log.d(TAG, "✨ Triggered move highlight effects");
    }
    
    /**
     * Trigger capture effects (explosions, particle effects)
     */
    /**
     * Trigger spectacular enhanced capture effects
     */
    private void triggerEnhancedCaptureEffects(float x, float y, String capturedPieceType, boolean isWhitePiece) {
        if (gameContainer == null) {
            Log.w(TAG, "❌ Game container is null, cannot trigger capture effects");
            return;
        }
        
        Log.d(TAG, "🎆 Triggering SPECTACULAR capture explosion at (" + x + ", " + y + ") for " + 
                   capturedPieceType + " (white: " + isWhitePiece + ")");
        
        // Create the spectacular explosion effect
        EnhancedCaptureEffects.createCaptureExplosion(
            gameContainer, 
            x + 32f, y + 32f,  // Center the explosion on the square
            capturedPieceType != null ? capturedPieceType : "pawn",
            isWhitePiece
        );
        
        // Also trigger the traditional glass effect as backup/additional layer
        if (boardView != null) {
            WorkingGlassEffects.applyMoveHighlight(
                boardView,
                x + 32f, y + 32f,
                1.2f, // Increased intensity
                isWhitePiece ? new float[]{1.0f, 0.9f, 0.2f} : new float[]{0.8f, 0.2f, 0.2f}
            );
        }
        
        Log.d(TAG, "✨ SPECTACULAR capture effects initiated!");
    }
    
    /**
     * Legacy capture effects method (kept for compatibility)
     */
    private void triggerCaptureEffects(float x, float y, boolean isWhitePiece) {
        // Redirect to enhanced version
        triggerEnhancedCaptureEffects(x, y, "pawn", isWhitePiece);
    }
    
    /**
     * Add spectacular movement trail effect
     */
    private void addMovementTrail(float startX, float startY, float endX, float endY, 
                                String pieceType, boolean isWhitePiece) {
        if (gameContainer == null) {
            Log.w(TAG, "❌ Game container is null, cannot add movement trail");
            return;
        }
        
        Log.d(TAG, "✨ Adding movement trail for " + pieceType + " from (" + startX + ", " + startY + 
                   ") to (" + endX + ", " + endY + ")");
        
        // Create the spectacular trail effect
        EnhancedCaptureEffects.createMovementTrail(
            gameContainer,
            startX + 32f, startY + 32f,  // Center on start square
            endX + 32f, endY + 32f,      // Center on end square
            pieceType != null ? pieceType : "pawn",
            isWhitePiece
        );
    }
    
    /**
     * Convert chess position (e.g., "e4") to board pixel coordinates
     */
    private float[] positionToBoardCoordinates(String position) {
        if (position == null || position.length() != 2) {
            return new float[]{0f, 0f};
        }
        
        char file = position.charAt(0); // a-h
        char rank = position.charAt(1); // 1-8
        
        // Convert to board coordinates (assuming 8x8 grid)
        int col = file - 'a'; // 0-7
        int row = 8 - (rank - '0'); // 0-7 (flipped for display)
        
        // Calculate pixel coordinates (assuming uniform square size)
        float squareSize = boardView.getWidth() / 8.0f;
        float x = col * squareSize;
        float y = row * squareSize;
        
        return new float[]{x, y};
    }
    
    /**
     * Find piece view at given position - Updated to work with ChessBoardView
     */
    private View findPieceAtPosition(String position) {
        // Convert chess position to board coordinates
        if (position == null || position.length() != 2) {
            Log.w(TAG, "Invalid position format: " + position);
            return null;
        }
        
        char file = position.charAt(0); // a-h
        char rank = position.charAt(1); // 1-8
        
        // Convert to array indices
        int col = file - 'a'; // 0-7
        int row = 8 - (rank - '0'); // 0-7 (rank 8 is row 0)
        
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            Log.w(TAG, "Position out of bounds: " + position + " -> (" + row + "," + col + ")");
            return null;
        }
        
        // Get piece from ChessBoardView if it has access to board state
        if (boardView instanceof com.example.chesspedagogue.ChessBoardView) {
            com.example.chesspedagogue.ChessBoardView chessBoardView = (com.example.chesspedagogue.ChessBoardView) boardView;
            char piece = chessBoardView.getPieceAt(row, col);
            
            if (piece != ' ') {
                Log.d(TAG, "✅ Found piece '" + piece + "' at " + position + " (" + row + "," + col + ")");
                // Return the board view itself as the "piece view" since pieces are drawn on canvas
                return chessBoardView;
            } else {
                Log.w(TAG, "❌ No piece found at " + position + " (" + row + "," + col + ")");
                return null;
            }
        }
        
        Log.w(TAG, "❌ BoardView is not a ChessBoardView instance");
        return null;
    }
    
    /**
     * Find king position on the actual board
     */
    private String findKingOnBoard(boolean isWhiteKing) {
        if (!(boardView instanceof com.example.chesspedagogue.ChessBoardView)) {
            return null;
        }
        
        com.example.chesspedagogue.ChessBoardView chessBoardView = (com.example.chesspedagogue.ChessBoardView) boardView;
        char targetKing = isWhiteKing ? 'K' : 'k';
        
        // Search the entire board for the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = chessBoardView.getPieceAt(row, col);
                if (piece == targetKing) {
                    // Convert array coordinates back to chess notation
                    char file = (char) ('a' + col);
                    char rank = (char) ('8' - row);
                    return "" + file + rank;
                }
            }
        }
        
        Log.w(TAG, "King not found on board: " + (isWhiteKing ? "White" : "Black"));
        return null;
    }
    
    /**
     * Find king position for check indicator (legacy method)
     */
    private String findKingPosition(boolean isWhiteKing) {
        String kingType = "king_" + (isWhiteKing ? "white" : "black");
        
        for (Map.Entry<String, String> entry : piecePositions.entrySet()) {
            String pieceId = entry.getValue();
            View piece = currentPieces.get(pieceId);
            if (piece != null && kingType.equals(piece.getTag())) {
                return entry.getKey();
            }
        }
        
        return null; // King not found
    }
    
    /**
     * Update piece position tracking
     */
    private void updatePiecePosition(View piece, String fromPosition, String toPosition) {
        String pieceId = piecePositions.remove(fromPosition);
        if (pieceId != null) {
            piecePositions.put(toPosition, pieceId);
        }
    }
    
    /**
     * Remove piece from board tracking
     */
    private void removePieceFromBoard(String position) {
        String pieceId = piecePositions.remove(position);
        if (pieceId != null) {
            currentPieces.remove(pieceId);
        }
    }
    
    /**
     * Generate unique piece ID
     */
    private String generatePieceId(String pieceType, boolean isWhite, String position) {
        return pieceType + "_" + (isWhite ? "white" : "black") + "_" + position + "_" + System.currentTimeMillis();
    }
    
    /**
     * Clear all pieces (for new game)
     */
    public void clearAllPieces() {
        Log.d(TAG, "🧹 Clearing all pieces and animations");
        
        currentPieces.clear();
        piecePositions.clear();
        capturedPiecesManager.clearAllCapturedPieces();
        hideCheckIndicator();
    }
    
    /**
     * Get captured pieces manager
     */
    public CapturedPiecesManager getCapturedPiecesManager() {
        return capturedPiecesManager;
    }
    
    /**
     * Enable/disable animations
     */
    public void setAnimationsEnabled(boolean enabled) {
        this.animationsEnabled = enabled;
        Log.d(TAG, "🎬 Animations " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Set animation speed multiplier
     */
    public void setAnimationSpeed(float multiplier) {
        this.animationSpeedMultiplier = Math.max(0.1f, Math.min(3.0f, multiplier));
        Log.d(TAG, "⚡ Animation speed set to: " + animationSpeedMultiplier + "x");
    }
    
    /**
     * Get material advantage
     */
    public int getMaterialAdvantage() {
        return capturedPiecesManager.getMaterialAdvantage();
    }
    
    /**
     * Check if king is in check
     */
    public boolean isKingInCheck(boolean isWhiteKing) {
        return isWhiteKing ? isWhiteInCheck : isBlackInCheck;
    }
    
    /**
     * Test method to show captured pieces and check indicator - DISABLED
     */
    public void testAnimationFeatures() {
        Log.d(TAG, "🧪 Test animation features disabled - no test pieces added");
        // Disabled to prevent test captured pieces appearing at startup
        // Real animations will occur during actual gameplay
    }
}