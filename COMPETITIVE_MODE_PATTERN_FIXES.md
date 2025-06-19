# CompetitiveModeActivity Pattern Fixes

## Key Patterns to Copy from MainActivity.java

### 1. Chess Board Setup Pattern

From MainActivity `setupChessBoard()` method (lines 1378-1527):

**Critical Observers to Add:**
```java
private void setupChessBoard() {
    // ENHANCED: Add null check at the beginning
    if (chessBoardView == null) {
        Log.e(TAG, "❌ setupChessBoard called but chessBoardView is null!");
        return;
    }

    // Set up observers to watch for changes in the game state
    gameViewModel.getCurrentFEN().observe(this, fen -> {
        // Update the board view when the FEN changes
        chessBoardView.updateBoardFromFen(fen);
        // NEW: Also update the real FEN for turn checking! 🎯
        chessBoardView.setRealCurrentFEN(fen);
        Log.d(TAG, "📝 Updated board with new FEN: " + fen);
    });

    gameViewModel.getAnimateMoveEvent().observe(this, coords -> {
        if (coords != null && coords.length == 4) {
            // Animate the piece movement
            chessBoardView.animateMove(coords[0], coords[1], coords[2], coords[3]);
        }
    });

    gameViewModel.getClearSelectionEvent().observe(this, shouldClear -> {
        if (shouldClear != null && shouldClear) {
            Log.d(TAG, "Clearing selection due to invalid move");
            chessBoardView.clearSelectionHighlight();
            chessBoardView.clearHighlightedSquares();
            gameViewModel.resetClearSelectionEvent();
        }
    });

    gameViewModel.getLastMoveEvent().observe(this, coords -> {
        if (coords != null && coords.length == 4) {
            // Highlight the last move
            chessBoardView.setLastMove(coords[0], coords[1], coords[2], coords[3]);
        }
    });

    gameViewModel.getKingInCheckEvent().observe(this, coords -> {
        if (coords != null && coords.length == 2) {
            // Highlight the king in check
            chessBoardView.setKingInCheck(true, coords[0], coords[1]);
        } else {
            chessBoardView.setKingInCheck(false, -1, -1);
        }
    });
}
```

### 2. Proper Move Handling Pattern

From MainActivity `chessBoardView.setOnSquareTapListener()` (lines 1430-1527):

**Key Points:**
- Uses `gameViewModel.makePlayerMove(move)` NOT direct repository access
- Proper turn validation with `isPlayersTurn`
- Legal move checking with `gameViewModel.getLegalMovesForSquare()`
- Piece ownership validation
- Proper selection logic preventing "boomerang effect"

```java
chessBoardView.setOnSquareTapListener((row, col) -> {
    Log.d(TAG, "🎯 SQUARE TAPPED: row=" + row + ", col=" + col);
    Log.d(TAG, "📝 Player color: " + configuredPlayerColor);
    Log.d(TAG, "🔍 Selected row/col: " + chessBoardView.getSelectedRow() + "/" + chessBoardView.getSelectedCol());

    // If we already have a piece selected...
    if (chessBoardView.getSelectedRow() != -1) {
        int fromRow = chessBoardView.getSelectedRow();
        int fromCol = chessBoardView.getSelectedCol();

        // If tapping the same square, deselect it
        if (fromRow == row && fromCol == col) {
            chessBoardView.clearSelectionHighlight();
            chessBoardView.clearHighlightedSquares();
            return;
        }

        // Check if the tapped square has one of our pieces
        char tappedPiece = chessBoardView.getPieceAt(row, col);

        // FIXED: Only check piece ownership for actual pieces, not empty squares
        if (tappedPiece != ' ') {
            // NEW: Improved piece ownership logic for player perspective
            boolean isPlayerWhite = "white".equals(configuredPlayerColor);
            boolean isPieceWhite = Character.isUpperCase(tappedPiece);
            boolean isOurPiece = (isPlayerWhite && isPieceWhite) || (!isPlayerWhite && !isPieceWhite);

            if (isOurPiece) {
                // Tapped another of our pieces, so select this one instead
                chessBoardView.clearSelectionHighlight();
                chessBoardView.clearHighlightedSquares();
                chessBoardView.setSelectedSquare(row, col);

                // Show legal moves for newly selected piece
                gameViewModel.getLegalMovesForSquare(algebraicNotation(row, col),
                        moves -> {
                            for (String move : moves) {
                                int destRow = 8 - Character.getNumericValue(move.charAt(3));
                                int destCol = move.charAt(2) - 'a';
                                chessBoardView.addHighlightedSquare(destRow, destCol);
                            }
                        });
                return;
            }
        }

        // Otherwise, try to make a move from the selected piece to this square
        String move = algebraicNotation(fromRow, fromCol) + algebraicNotation(row, col);

        boolean isPlayerWhite = "white".equals(configuredPlayerColor);
        boolean isPlayersTurn = (isPlayerWhite && chessBoardView.isWhiteTurn()) ||
                (!isPlayerWhite && !chessBoardView.isWhiteTurn());

        Log.d(TAG, "🎯 ATTEMPTING MOVE: " + move);
        Log.d(TAG, "📝 Player color: " + configuredPlayerColor);
        Log.d(TAG, "🔄 Is player's turn: " + isPlayersTurn);
        Log.d(TAG, "🔄 Is white's turn: " + chessBoardView.isWhiteTurn());

        if (isPlayersTurn) {
            Log.d(TAG, "✅ Turn validation passed, making move: " + move);
            gameViewModel.makePlayerMove(move);  // KEY: Use ViewModel, not repository directly
        } else {
            Log.d(TAG, "❌ Turn validation failed - not player's turn!");
            Toast.makeText(MainActivity.this, "Wait for your turn!", Toast.LENGTH_SHORT).show();
        }

        chessBoardView.clearSelectionHighlight();
        chessBoardView.clearHighlightedSquares();
    } else {
        // No piece is selected yet, select if it's our piece and our turn
        char piece = chessBoardView.getPieceAt(row, col);

        // NEW: Improved piece ownership and turn logic
        boolean isPlayerWhite = "white".equals(configuredPlayerColor);
        boolean isPieceWhite = Character.isUpperCase(piece);
        boolean isOurPiece = (isPlayerWhite && isPieceWhite) || (!isPlayerWhite && !isPieceWhite);
        boolean isPlayersTurn = (isPlayerWhite && chessBoardView.isWhiteTurn()) ||
                (!isPlayerWhite && !chessBoardView.isWhiteTurn());

        if (piece != ' ' && isOurPiece && isPlayersTurn) {
            chessBoardView.setSelectedSquare(row, col);
            gameViewModel.getLegalMovesForSquare(algebraicNotation(row, col),
                    moves -> {
                        chessBoardView.clearHighlightedSquares();
                        for (String move : moves) {
                            int destRow = 8 - Character.getNumericValue(move.charAt(3));
                            int destCol = move.charAt(2) - 'a';
                            chessBoardView.addHighlightedSquare(destRow, destCol);
                        }
                    });
        }
    }
});
```

### 3. Proper TTS Initialization Pattern

From MainActivity button setup (lines 410-431):

```java
// Set context for competitive mode (ultra-fast eleven_flash_v2_5)
TTSServiceManager.setUsageContext(this, "competitive_mode");

OpenAITTSService tts = TTSServiceManager.getOpenAITTSService(this);

if (tts != null && tts.isSpeaking()) {
    tts.stopSpeech();
    tts.setSpeechCallback(new OpenAITTSService.SpeechCallback() {
        @Override
        public void onSpeechCompleted(String text) {
            // Handle completion
        }

        @Override
        public void onSpeechInterrupted() {
            // Handle interruption
        }
    });
}
```

### 4. Board Size Management Pattern

From MainActivity layout - NO special size handling needed:
- Use standard layout constraints in XML
- Let ChessBoardView handle its own sizing
- No manual size manipulation required

### 5. Helper Method for Move Notation

From MainActivity (referenced in move handling):

```java
// Helper method for converting coordinates to algebraic notation
private String algebraicNotation(int row, int col) {
    char file = (char) ('a' + col);
    int rank = 8 - row;
    return "" + file + rank;
}
```

## Issues in Current CompetitiveModeActivity

1. **❌ Direct Repository Access**: Uses `gameViewModel.getGameRepository().makeMove(move)`
2. **❌ Missing Turn Validation**: No check if it's player's turn
3. **❌ Missing Legal Move Highlighting**: No legal move preview
4. **❌ Missing Animation Observers**: No move animations
5. **❌ Missing Piece Ownership Logic**: No validation of piece ownership
6. **❌ Incomplete Selection Logic**: Can cause "boomerang effect"
7. **❌ Missing Critical Observers**: No observers for highlights, checks, etc.

## Required Changes

Replace the current `handleSquareTap()` method and `setupChessBoardInteraction()` method in CompetitiveModeActivity with the exact patterns from MainActivity, adapting only the variable names and context.

Key changes needed:
1. Add all the critical observers from MainActivity's `setupChessBoard()`
2. Replace direct repository access with `gameViewModel.makePlayerMove()`
3. Add proper turn validation and piece ownership logic
4. Add legal move highlighting
5. Use proper selection/deselection logic
6. Add the `algebraicNotation()` helper method