package com.example.chesspedagogue.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * Captured Pieces Manager for Samsung S23 Ultra Chess Game
 * Manages the display of captured pieces lined up on board edges like real chess
 */
public class CapturedPiecesManager {
    private static final String TAG = "CapturedPiecesManager";
    
    // Display constants optimized for S23 Ultra screen
    private static final int CAPTURED_PIECE_SIZE_DP = 24; // Smaller than board pieces
    private static final int PIECE_SPACING_DP = 4;
    private static final float CAPTURED_PIECE_ALPHA = 0.8f;
    
    private final Context context;
    private final ViewGroup parentContainer;
    private LinearLayout whiteCapturedContainer;
    private LinearLayout blackCapturedContainer;
    
    // Track captured pieces for scoring
    private final List<CapturedPiece> whiteCapturedPieces = new ArrayList<>();
    private final List<CapturedPiece> blackCapturedPieces = new ArrayList<>();
    
    /**
     * Represents a captured piece with its type and display info
     */
    public static class CapturedPiece {
        public final String pieceType; // "pawn", "rook", "knight", "bishop", "queen", "king"
        public final boolean isWhite;
        public final ImageView imageView;
        public final int pointValue;
        
        public CapturedPiece(String pieceType, boolean isWhite, ImageView imageView) {
            this.pieceType = pieceType;
            this.isWhite = isWhite;
            this.imageView = imageView;
            this.pointValue = getPieceValue(pieceType);
        }
        
        private int getPieceValue(String type) {
            switch (type.toLowerCase()) {
                case "pawn": return 1;
                case "knight": case "bishop": return 3;
                case "rook": return 5;
                case "queen": return 9;
                case "king": return 0; // King has no point value
                default: return 0;
            }
        }
    }
    
    public CapturedPiecesManager(Context context, ViewGroup parentContainer) {
        this.context = context;
        this.parentContainer = parentContainer;
        initializeCaptureContainers();
    }
    
    /**
     * Initialize the capture display containers from layout
     */
    private void initializeCaptureContainers() {
        Log.d(TAG, "🏗️ Finding captured pieces containers from layout");
        
        // Find existing containers from layout
        whiteCapturedContainer = parentContainer.findViewById(com.example.chesspedagogue.R.id.whiteCapturedContainer);
        blackCapturedContainer = parentContainer.findViewById(com.example.chesspedagogue.R.id.blackCapturedContainer);
        
        if (whiteCapturedContainer == null || blackCapturedContainer == null) {
            Log.w(TAG, "⚠️ Layout containers not found, creating fallback containers");
            createFallbackContainers();
        } else {
            Log.d(TAG, "✅ Found existing captured pieces containers in layout");
        }
    }
    
    /**
     * Create fallback containers if layout ones are missing
     */
    private void createFallbackContainers() {
        // Create container for white captured pieces (top edge)
        if (whiteCapturedContainer == null) {
            whiteCapturedContainer = new LinearLayout(context);
            whiteCapturedContainer.setOrientation(LinearLayout.HORIZONTAL);
            whiteCapturedContainer.setId(View.generateViewId());
        }
        
        // Create container for black captured pieces (bottom edge)
        if (blackCapturedContainer == null) {
            blackCapturedContainer = new LinearLayout(context);
            blackCapturedContainer.setOrientation(LinearLayout.HORIZONTAL);
            blackCapturedContainer.setId(View.generateViewId());
        }
        
        // Set layout parameters for positioning on edges
        ViewGroup.LayoutParams whiteParams = createEdgeLayoutParams(true);
        ViewGroup.LayoutParams blackParams = createEdgeLayoutParams(false);
        
        whiteCapturedContainer.setLayoutParams(whiteParams);
        blackCapturedContainer.setLayoutParams(blackParams);
        
        // Add containers to parent
        parentContainer.addView(whiteCapturedContainer);
        parentContainer.addView(blackCapturedContainer);
        
        Log.d(TAG, "✅ Fallback captured pieces containers created");
    }
    
    /**
     * Add a captured piece to the display (overloaded for direct piece type)
     */
    public void addCapturedPiece(String pieceType, boolean isWhitePiece) {
        Log.d(TAG, "📦 Adding captured " + pieceType + " - White: " + isWhitePiece);
        
        // Create captured piece view directly from piece type
        ImageView capturedPieceView = createCapturedPieceViewFromType(pieceType, isWhitePiece);
        
        // Create captured piece record
        CapturedPiece capturedPiece = new CapturedPiece(pieceType, isWhitePiece, capturedPieceView);
        
        // Add to appropriate container and list
        LinearLayout targetContainer = isWhitePiece ? whiteCapturedContainer : blackCapturedContainer;
        List<CapturedPiece> targetList = isWhitePiece ? whiteCapturedPieces : blackCapturedPieces;
        
        // Add to list and sort by piece value (pawns first, then by value)
        targetList.add(capturedPiece);
        sortCapturedPieces(targetList);
        
        // Clear container and re-add in proper order
        targetContainer.removeAllViews();
        for (CapturedPiece piece : targetList) {
            targetContainer.addView(piece.imageView);
        }
        
        // Animate the captured piece entrance
        animateCapturedPieceEntrance(capturedPieceView);
        
        Log.d(TAG, "✅ Added captured " + pieceType + " to edge display in sorted order");
    }

    /**
     * Add a captured piece to the display (original method)
     */
    public void addCapturedPiece(View originalPieceView, boolean isWhitePiece) {
        if (originalPieceView == null) {
            Log.w(TAG, "Cannot add null captured piece");
            return;
        }
        
        Log.d(TAG, "📦 Adding captured piece - White: " + isWhitePiece);
        
        // Determine piece type from the original view
        String pieceType = determinePieceType(originalPieceView);
        
        // Create smaller version for captured display
        ImageView capturedPieceView = createCapturedPieceView(originalPieceView, pieceType, isWhitePiece);
        
        // Create captured piece record
        CapturedPiece capturedPiece = new CapturedPiece(pieceType, isWhitePiece, capturedPieceView);
        
        // Add to appropriate container and list
        LinearLayout targetContainer = isWhitePiece ? whiteCapturedContainer : blackCapturedContainer;
        List<CapturedPiece> targetList = isWhitePiece ? whiteCapturedPieces : blackCapturedPieces;
        
        // Add with entrance animation
        targetContainer.addView(capturedPieceView);
        targetList.add(capturedPiece);
        
        // Animate the captured piece entrance
        animateCapturedPieceEntrance(capturedPieceView);
        
        // Update material advantage display
        updateMaterialAdvantage();
        
        Log.d(TAG, "✅ Captured piece added - Total captured: " + 
                   (whiteCapturedPieces.size() + blackCapturedPieces.size()));
    }
    
    /**
     * Create a smaller ImageView for captured piece display from piece type
     */
    private ImageView createCapturedPieceViewFromType(String pieceType, boolean isWhite) {
        ImageView capturedView = new ImageView(context);
        
        // Set size for captured piece display
        int sizePixels = (int) (CAPTURED_PIECE_SIZE_DP * context.getResources().getDisplayMetrics().density);
        int spacingPixels = (int) (PIECE_SPACING_DP * context.getResources().getDisplayMetrics().density);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePixels, sizePixels);
        params.setMargins(spacingPixels, spacingPixels, spacingPixels, spacingPixels);
        capturedView.setLayoutParams(params);
        
        // Get drawable resource for piece type and color
        int resourceId = getPieceResourceId(pieceType, isWhite);
        if (resourceId != 0) {
            Drawable drawable = ContextCompat.getDrawable(context, resourceId);
            if (drawable != null) {
                capturedView.setImageDrawable(drawable);
            }
        }
        
        // Set visual properties for captured piece
        capturedView.setAlpha(CAPTURED_PIECE_ALPHA);
        capturedView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        // Add subtle shadow for depth
        capturedView.setElevation(4f);
        
        return capturedView;
    }

    /**
     * Create a smaller ImageView for the captured piece display
     */
    private ImageView createCapturedPieceView(View originalView, String pieceType, boolean isWhite) {
        ImageView capturedView = new ImageView(context);
        
        // Set size for captured piece display
        int sizePixels = (int) (CAPTURED_PIECE_SIZE_DP * context.getResources().getDisplayMetrics().density);
        int spacingPixels = (int) (PIECE_SPACING_DP * context.getResources().getDisplayMetrics().density);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePixels, sizePixels);
        params.setMargins(spacingPixels, spacingPixels, spacingPixels, spacingPixels);
        capturedView.setLayoutParams(params);
        
        // Copy the drawable from original piece
        if (originalView instanceof ImageView) {
            ImageView originalImageView = (ImageView) originalView;
            Drawable drawable = originalImageView.getDrawable();
            if (drawable != null) {
                capturedView.setImageDrawable(drawable.getConstantState().newDrawable());
            }
        }
        
        // Set visual properties for captured piece
        capturedView.setAlpha(CAPTURED_PIECE_ALPHA);
        capturedView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        // Add subtle shadow for depth
        capturedView.setElevation(4f);
        
        return capturedView;
    }
    
    /**
     * Animate captured piece entrance with bounce
     */
    private void animateCapturedPieceEntrance(ImageView capturedView) {
        // Start invisible and small
        capturedView.setAlpha(0f);
        capturedView.setScaleX(0.3f);
        capturedView.setScaleY(0.3f);
        
        // Animate to final state with bounce
        capturedView.animate()
            .alpha(CAPTURED_PIECE_ALPHA)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(400)
            .setInterpolator(new android.view.animation.OvershootInterpolator(1.5f))
            .start();
        
        Log.d(TAG, "🎬 Captured piece entrance animation started");
    }
    
    /**
     * Determine piece type from view properties
     */
    private String determinePieceType(View pieceView) {
        // Try to get piece type from tag first
        Object tag = pieceView.getTag();
        if (tag instanceof String) {
            String tagStr = (String) tag;
            if (tagStr.contains("pawn")) return "pawn";
            if (tagStr.contains("rook")) return "rook";
            if (tagStr.contains("knight")) return "knight";
            if (tagStr.contains("bishop")) return "bishop";
            if (tagStr.contains("queen")) return "queen";
            if (tagStr.contains("king")) return "king";
        }
        
        // Fallback to analyzing the drawable resource
        if (pieceView instanceof ImageView) {
            ImageView imageView = (ImageView) pieceView;
            Drawable drawable = imageView.getDrawable();
            // Could analyze drawable to determine piece type
            // For now, default to pawn if unknown
        }
        
        return "pawn"; // Default fallback
    }
    
    /**
     * Create layout parameters for edge positioning
     */
    private ViewGroup.LayoutParams createEdgeLayoutParams(boolean isTopEdge) {
        if (parentContainer instanceof androidx.constraintlayout.widget.ConstraintLayout) {
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = 
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            
            if (isTopEdge) {
                params.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.topMargin = (int) (8 * context.getResources().getDisplayMetrics().density);
            } else {
                params.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomMargin = (int) (8 * context.getResources().getDisplayMetrics().density);
            }
            
            return params;
        }
        
        // Fallback for other layout types
        return new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    
    /**
     * Update material advantage display
     */
    private void updateMaterialAdvantage() {
        int whitePoints = calculateTotalPoints(whiteCapturedPieces);
        int blackPoints = calculateTotalPoints(blackCapturedPieces);
        int advantage = whitePoints - blackPoints;
        
        Log.d(TAG, "📊 Material advantage - White: " + whitePoints + ", Black: " + blackPoints + 
                   ", Advantage: " + (advantage > 0 ? "White +" + advantage : 
                                      advantage < 0 ? "Black +" + Math.abs(advantage) : "Equal"));
        
        // Could trigger UI updates here to show material advantage
        // For example, highlight the advantaged player's captured pieces
        highlightMaterialAdvantage(advantage);
    }
    
    /**
     * Calculate total point value of captured pieces
     */
    private int calculateTotalPoints(List<CapturedPiece> pieces) {
        return pieces.stream().mapToInt(piece -> piece.pointValue).sum();
    }
    
    /**
     * Highlight material advantage visually
     */
    private void highlightMaterialAdvantage(int advantage) {
        LinearLayout advantageContainer = null;
        
        if (advantage > 0) {
            // White has advantage
            advantageContainer = whiteCapturedContainer;
        } else if (advantage < 0) {
            // Black has advantage
            advantageContainer = blackCapturedContainer;
        }
        
        if (advantageContainer != null) {
            // Add subtle glow effect to advantaged side
            advantageContainer.animate()
                .alpha(1.0f)
                .setDuration(300)
                .start();
        }
    }
    
    /**
     * Clear all captured pieces (for new game)
     */
    public void clearAllCapturedPieces() {
        Log.d(TAG, "🧹 Clearing all captured pieces");
        
        whiteCapturedContainer.removeAllViews();
        blackCapturedContainer.removeAllViews();
        whiteCapturedPieces.clear();
        blackCapturedPieces.clear();
        
        Log.d(TAG, "✅ All captured pieces cleared");
    }
    
    /**
     * Get material advantage for display
     */
    public int getMaterialAdvantage() {
        int whitePoints = calculateTotalPoints(whiteCapturedPieces);
        int blackPoints = calculateTotalPoints(blackCapturedPieces);
        return whitePoints - blackPoints;
    }
    
    /**
     * Get captured pieces count
     */
    public int getCapturedPiecesCount(boolean forWhite) {
        return forWhite ? whiteCapturedPieces.size() : blackCapturedPieces.size();
    }
    
    /**
     * Add test captured pieces for demonstration
     */
    public void addTestCapturedPieces() {
        Log.d(TAG, "🧪 Adding test captured pieces for demonstration");
        
        // Add some white pieces captured by black
        addCapturedPiece("pawn", true);
        addCapturedPiece("knight", true);
        addCapturedPiece("pawn", true);
        
        // Add some black pieces captured by white  
        addCapturedPiece("pawn", false);
        addCapturedPiece("bishop", false);
        addCapturedPiece("pawn", false);
        addCapturedPiece("rook", false);
        
        Log.d(TAG, "✅ Test captured pieces added");
    }
    
    /**
     * Sort captured pieces in proper chess order: pawns, knights, bishops, rooks, queen, king
     */
    private void sortCapturedPieces(List<CapturedPiece> pieces) {
        pieces.sort((p1, p2) -> {
            int order1 = getPieceOrder(p1.pieceType);
            int order2 = getPieceOrder(p2.pieceType);
            return Integer.compare(order1, order2);
        });
    }
    
    /**
     * Get piece order for sorting (lower number = displayed first)
     */
    private int getPieceOrder(String pieceType) {
        switch (pieceType.toLowerCase()) {
            case "pawn": return 1;
            case "knight": return 2;
            case "bishop": return 3;
            case "rook": return 4;
            case "queen": return 5;
            case "king": return 6;
            default: return 99; // Unknown pieces go last
        }
    }
    
    /**
     * Get piece resource ID based on type and color
     */
    private int getPieceResourceId(String pieceType, boolean isWhite) {
        String prefix = isWhite ? "ic_white_" : "ic_black_";
        String resourceName = prefix + pieceType.toLowerCase();
        
        // Get resource ID using reflection or hardcoded mapping
        switch (resourceName) {
            case "ic_white_pawn": return com.example.chesspedagogue.R.drawable.ic_white_pawn;
            case "ic_white_rook": return com.example.chesspedagogue.R.drawable.ic_white_rook;
            case "ic_white_knight": return com.example.chesspedagogue.R.drawable.ic_white_knight;
            case "ic_white_bishop": return com.example.chesspedagogue.R.drawable.ic_white_bishop;
            case "ic_white_queen": return com.example.chesspedagogue.R.drawable.ic_white_queen;
            case "ic_white_king": return com.example.chesspedagogue.R.drawable.ic_white_king;
            case "ic_black_pawn": return com.example.chesspedagogue.R.drawable.ic_black_pawn;
            case "ic_black_rook": return com.example.chesspedagogue.R.drawable.ic_black_rook;
            case "ic_black_knight": return com.example.chesspedagogue.R.drawable.ic_black_knight;
            case "ic_black_bishop": return com.example.chesspedagogue.R.drawable.ic_black_bishop;
            case "ic_black_queen": return com.example.chesspedagogue.R.drawable.ic_black_queen;
            case "ic_black_king": return com.example.chesspedagogue.R.drawable.ic_black_king;
            default:
                Log.w(TAG, "Unknown piece resource: " + resourceName);
                return 0;
        }
    }
}