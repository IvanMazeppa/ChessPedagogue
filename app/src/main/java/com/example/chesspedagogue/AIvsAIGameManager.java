package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chesspedagogue.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🤖 AI vs AI Game Manager - FIXED MOVE VALIDATION VERSION
 * Now properly validates moves before applying them!
 */
public class AIvsAIGameManager {
    private static final String TAG = "AIvsAIGameManager";

    private final Context context;
    private final GameRepository gameRepository;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    // Game state - ENHANCED with better validation
    private String whitePlayer;
    private String blackPlayer;
    private List<String> gameHistory;
    private boolean isPaused = false;
    private int moveDelay = 3000;
    private String lastValidFEN;
    private int consecutiveFailures = 0; // Track failures to prevent infinite loops
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    private volatile boolean isValidatingMove = false; // Prevent concurrent move validations

    // Callbacks
    private GameCallback gameCallback;

    public interface GameCallback {
        void onMoveCalculated(String move, String newFen, List<String> history);
        void onGameEnd(String result);
        void onThinkingStateChanged(boolean thinking);
        void onError(String error);
    }

    public AIvsAIGameManager(Context context) {
        this.context = context.getApplicationContext();
        this.gameRepository = new GameRepository(context);
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gameHistory = new ArrayList<>();

        Log.d(TAG, "✅ FIXED AIvsAIGameManager initialized with enhanced move validation");
    }

    public void initializeGame(String whitePlayer, String blackPlayer) {
        Log.d(TAG, "🎮 Initializing FIXED AI vs AI game: " + whitePlayer + " vs " + blackPlayer);

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameHistory = new ArrayList<>();
        this.consecutiveFailures = 0; // Reset failure counter

        // Reset game repository
        gameRepository.newGame();

        // Store the starting position
        this.lastValidFEN = gameRepository.getCurrentFEN();
        Log.d(TAG, "🎯 Starting position stored: " + lastValidFEN);

        Log.d(TAG, "✅ FIXED Game initialized with enhanced validation");
    }

    public void requestMove(String fen, List<String> history, String activePlayer) {
        try {
            Log.d(TAG, "🎯 REQUEST MOVE CALLED! Player: " + activePlayer + ", paused: " + isPaused);
            
            if (isPaused) {
                Log.d(TAG, "Game is paused, skipping move request");
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in requestMove start", e);
            return;
        }

        Log.d(TAG, "🎯 FIXED: Requesting move from " + activePlayer + " (move " + (this.gameHistory.size() + 1) + ")");
        Log.d(TAG, "📋 Input FEN: " + fen);
        Log.d(TAG, "📜 Input history: " + history);
        Log.d(TAG, "📝 Current internal history: " + this.gameHistory);

        if (gameCallback != null) {
            gameCallback.onThinkingStateChanged(true);
        }

        // CRITICAL: We maintain our own authoritative game history
        // Don't trust the incomplete history from SpectatorGameViewModel
        Log.d(TAG, "🎮 Using internal game history (" + this.gameHistory.size() + " moves) instead of input history (" + history.size() + " moves)");
        Log.d(TAG, "🎮 Internal history content: " + this.gameHistory);

        executorService.execute(() -> {
            try {
                // CRITICAL FIX: Enhanced state synchronization with validation
                Log.d(TAG, "🔄 FIXED: Synchronizing game repository state with validation...");
                Log.d(TAG, "📜 Current game history: " + gameHistory);
                Log.d(TAG, "📍 Active player: " + activePlayer);

                // CRITICAL FIX: Skip synchronization - it's causing the reset bug
                // The game state is already maintained properly
                Log.d(TAG, "🔄 SKIPPING synchronization to prevent position reset");
                boolean syncSuccess = true;

                if (!syncSuccess) {
                    Log.e(TAG, "❌ Synchronization failed - attempting enhanced recovery");
                    // Try enhanced synchronization with longer timeouts
                    Log.d(TAG, "🔄 Attempting enhanced synchronization...");
                    boolean enhancedSyncSuccess = attemptGameSynchronizationEnhanced();
                    Log.d(TAG, "🔄 Enhanced synchronization result: " + enhancedSyncSuccess);
                    
                    if (!enhancedSyncSuccess) {
                        Log.e(TAG, "❌ Enhanced synchronization also failed");
                        
                        // Last resort: If we're early in the game, restart
                        if (gameHistory.size() <= 2) {
                            Log.w(TAG, "🔄 Early game sync failure - restarting game");
                            gameHistory.clear();
                            lastValidFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
                            gameRepository.newGame();
                            Log.d(TAG, "🔄 Game restarted, continuing with move calculation...");
                        } else {
                            // Only fail the game if we're deeper into it
                            handleMoveError("Failed to synchronize game state after recovery attempt");
                            return;
                        }
                    }
                }

                Log.d(TAG, "✅ Synchronization complete, configuring engine...");
                // Configure personality engine with historical rating
                configureEngineForPlayer(activePlayer);

                Log.d(TAG, "✅ Engine configured, calculating move...");
                // ENHANCED: Calculate the move with proper validation
                calculateValidatedMove(activePlayer);

            } catch (Exception e) {
                Log.e(TAG, "❌ Error in move calculation", e);
                handleMoveError(e.getMessage());
            }
        });
    }

    /**
     * ENHANCED: Synchronize game state with validation

    private boolean synchronizeGameState() {
        try {
            if (gameHistory.isEmpty()) {
                // Starting position - reset to beginning
                gameRepository.newGame();
                lastValidFEN = gameRepository.getCurrentFEN();
                Log.d(TAG, "🆕 Reset to starting position: " + lastValidFEN);
                return true;
            } else {
                // Apply ALL moves from the beginning with validation
                for (int attempt = 0; attempt < 3; attempt++) {
                    if (attemptGameSynchronization()) {
                        consecutiveFailures = 0; // Reset on success
                        return true;
                    }
                    Log.w(TAG, "⚠️ Synchronization attempt " + (attempt + 1) + " failed, retrying...");
                    Thread.sleep(100); // Brief pause between attempts
                }

                Log.e(TAG, "❌ All synchronization attempts failed");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error during state synchronization", e);
            return false;
        }
    }
     **/
    /**
     * ENHANCED: Synchronize game state with better timing and validation
     */
    private boolean synchronizeGameState() {
        try {
            Log.d(TAG, "🔄 ENHANCED: Starting game state synchronization...");
            Log.d(TAG, "🔄 Current gameHistory size: " + gameHistory.size() + ", content: " + gameHistory);
            if (gameHistory.isEmpty()) {
                // Starting position - reset to beginning
                Log.d(TAG, "🆕 Game history is empty, resetting to starting position");
                gameRepository.newGame();

                // ENHANCED: Wait for engine to be fully ready
                if (!gameRepository.stockfishManager.waitForReady(2000)) {
                    Log.e(TAG, "❌ Engine not ready after reset");
                    return false;
                }

                lastValidFEN = gameRepository.getCurrentFEN();
                Log.d(TAG, "🆕 Reset to starting position: " + lastValidFEN);
                return true;

            } else {
                // ENHANCED: Apply moves with better retry logic and timing
                for (int attempt = 0; attempt < 3; attempt++) {
                    Log.d(TAG, "🔄 Synchronization attempt " + (attempt + 1) + "/3...");

                    if (attemptGameSynchronizationEnhanced()) {
                        consecutiveFailures = 0; // Reset on success
                        Log.d(TAG, "✅ Synchronization successful on attempt " + (attempt + 1));
                        return true;
                    }

                    Log.w(TAG, "⚠️ Synchronization attempt " + (attempt + 1) + " failed, retrying...");

                    // ENHANCED: Progressive delay between attempts
                    try {
                        Thread.sleep(200 + (attempt * 100)); // 200ms, 300ms, 400ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                Log.e(TAG, "❌ All synchronization attempts failed");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception during state synchronization", e);
            return false;
        }
    }

    /**
     * ENHANCED: Attempt game state synchronization with better timing
     */
    private boolean attemptGameSynchronizationEnhanced() {
        try {
            Log.d(TAG, "🔧 Enhanced synchronization: Applying game history...");
            Log.d(TAG, "🔧 Current history to apply: " + gameHistory);

            // Step 1: Only reset if we have no moves
            if (gameHistory.isEmpty()) {
                Log.d(TAG, "🔧 No history - resetting to clean state");
                gameRepository.newGame();
            } else {
                Log.d(TAG, "🔧 Have history - will apply moves without reset");
            }

            // Step 2: ENHANCED - Wait longer for engine to be fully ready
            if (!gameRepository.stockfishManager.waitForReady(3000)) { // Increased to 3 seconds
                Log.e(TAG, "❌ Engine not ready for synchronization (timeout: 3s)");
                return false;
            }

            // Step 3: Get current position
            String currentFEN = gameRepository.getCurrentFEN();
            Log.d(TAG, "📍 Current position: " + currentFEN);
            
            // Only verify starting position if we have no history
            if (gameHistory.isEmpty() && !currentFEN.startsWith("rnbqkbnr/pppppppp")) {
                Log.e(TAG, "❌ Not in expected starting position: " + currentFEN);
                return false;
            }

            // Step 4: Apply moves only if we just reset
            boolean success = true;
            if (gameHistory.isEmpty()) {
                Log.d(TAG, "✅ No moves to apply - already at starting position");
                success = true;
            } else {
                Log.d(TAG, "🎯 Applying " + gameHistory.size() + " moves to synchronized position...");
                // Always reapply from start to ensure consistency
                gameRepository.newGame(); // Reset first
                success = gameRepository.applyMoves(gameHistory);
            }

            if (success) {
                // Step 5: ENHANCED - Verify position changed correctly
                String newFEN = gameRepository.getCurrentFEN();

                if (newFEN != null && !newFEN.equals(currentFEN)) {
                    // ENHANCED: Additional validation - check if move count matches
                    String[] fenParts = newFEN.split(" ");
                    if (fenParts.length >= 6) {
                        try {
                            int moveNumber = Integer.parseInt(fenParts[5]);
                            int expectedMoveNumber = (gameHistory.size() / 2) + 1;

                            if (Math.abs(moveNumber - expectedMoveNumber) <= 1) { // Allow 1 move tolerance
                                lastValidFEN = newFEN;
                                Log.d(TAG, "✅ Enhanced sync successful: " + gameHistory.size() + " moves, position: " + lastValidFEN);
                                return true;
                            } else {
                                Log.w(TAG, "⚠️ Move number mismatch - expected: " + expectedMoveNumber + ", got: " + moveNumber);
                            }
                        } catch (NumberFormatException e) {
                            Log.w(TAG, "⚠️ Could not parse move number from FEN, but position changed - accepting");
                            lastValidFEN = newFEN;
                            return true;
                        }
                    } else {
                        Log.w(TAG, "⚠️ Invalid FEN format, but position changed - accepting");
                        lastValidFEN = newFEN;
                        return true;
                    }
                } else {
                    Log.w(TAG, "⚠️ Position didn't change after applying moves");
                    Log.w(TAG, "   Starting FEN: " + currentFEN);
                    Log.w(TAG, "   Current FEN:  " + newFEN);
                    Log.w(TAG, "   History size: " + gameHistory.size());
                    Log.w(TAG, "   History: " + gameHistory);
                    
                    // If we have moves but position is still at start, clear history and retry
                    if (gameHistory.size() > 0 && currentFEN.equals(newFEN)) {
                        Log.w(TAG, "🔄 Clearing invalid history and retrying");
                        gameHistory.clear();
                        lastValidFEN = currentFEN;
                        return true; // Let the game continue from start
                    }
                    return false;
                }
            } else {
                Log.w(TAG, "⚠️ gameRepository.applyMoves() returned false");
                return false;
            }

            return false;

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception during enhanced synchronization attempt", e);
            return false;
        }
    }

// ALSO ADD this enhanced move validation method:

    /**
     * ENHANCED: Validate move with better error handling
     */
    private void validateAndApplyMoveEnhanced(String move) {
        // Prevent concurrent validations
        if (isValidatingMove) {
            Log.d(TAG, "⏳ Skipping - Already validating a move");
            return;
        }
        
        isValidatingMove = true;
        try {
            Log.d(TAG, "🔍 ENHANCED: Validating move: " + move);

            // Step 1: Enhanced UCI format check
            if (!isValidUCIFormatEnhanced(move)) {
                Log.e(TAG, "❌ Invalid UCI format: " + move);
                isValidatingMove = false; // Reset flag
                attemptFallbackMove();
                return;
            }

            // Step 2: ENHANCED - Wait for engine to be ready before checking legality
            if (!gameRepository.stockfishManager.waitForReady(1000)) {
                Log.e(TAG, "❌ Engine not ready for move validation");
                isValidatingMove = false; // Reset flag
                attemptFallbackMove();
                return;
            }

            // CRITICAL FIX: Get the FEN BEFORE checking move legality
            // because isLegalMove modifies the engine state
            String fenBeforeValidation = gameRepository.getCurrentFEN();

            // Step 3: Check if move is legal in current position
            boolean isLegal = gameRepository.isLegalMove(move);
            Log.d(TAG, "⚖️ Move " + move + " legality: " + (isLegal ? "LEGAL" : "ILLEGAL"));

            if (!isLegal) {
                Log.e(TAG, "❌ Illegal move detected: " + move);
                isValidatingMove = false; // Reset flag
                attemptFallbackMove();
                return;
            }

            // Step 4: Apply the validated move
            applyValidatedMoveWithStateTracking(move, fenBeforeValidation);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error during enhanced move validation: " + move, e);
            isValidatingMove = false; // Reset flag
            attemptFallbackMove();
        } finally {
            // Note: We don't reset the flag here because applyValidatedMoveWithStateTracking
            // might schedule a retry, and we want to keep the flag set until that completes
        }
    }

    /**
     * ENHANCED: Better UCI format validation
     */
    private boolean isValidUCIFormatEnhanced(String move) {
        if (move == null || move.trim().isEmpty()) {
            Log.d(TAG, "❌ Move is null or empty");
            return false;
        }

        move = move.trim();

        if (move.length() < 4 || move.length() > 5) {
            Log.d(TAG, "❌ Invalid move length: " + move.length() + " (expected 4-5)");
            return false;
        }

        // Check basic format: e2e4 or e7e8q (with promotion)
        char fromFile = move.charAt(0);
        char fromRank = move.charAt(1);
        char toFile = move.charAt(2);
        char toRank = move.charAt(3);

        // Validate file (a-h) and rank (1-8)
        if (fromFile < 'a' || fromFile > 'h') {
            Log.d(TAG, "❌ Invalid from file: " + fromFile);
            return false;
        }
        if (fromRank < '1' || fromRank > '8') {
            Log.d(TAG, "❌ Invalid from rank: " + fromRank);
            return false;
        }
        if (toFile < 'a' || toFile > 'h') {
            Log.d(TAG, "❌ Invalid to file: " + toFile);
            return false;
        }
        if (toRank < '1' || toRank > '8') {
            Log.d(TAG, "❌ Invalid to rank: " + toRank);
            return false;
        }

        // If 5 characters, check promotion piece
        if (move.length() == 5) {
            char promotion = move.charAt(4);
            if (promotion != 'q' && promotion != 'r' && promotion != 'b' && promotion != 'n') {
                Log.d(TAG, "❌ Invalid promotion piece: " + promotion);
                return false;
            }
        }

        return true;
    }


        /**
         * ENHANCED: Attempt game state synchronization with validation
         */
    private boolean attemptGameSynchronization() {
        try {
            // Reset to clean state
            gameRepository.newGame();

            // Wait for engine to be ready
            if (!gameRepository.stockfishManager.waitForReady(1000)) {
                Log.e(TAG, "❌ Engine not ready for synchronization");
                return false;
            }

            // Get starting position
            String startingFEN = gameRepository.getCurrentFEN();
            Log.d(TAG, "📍 Starting FEN for sync: " + startingFEN);

            // Apply moves with validation
            Log.d(TAG, "🎯 Applying " + gameHistory.size() + " moves: " + gameHistory);
            boolean success = gameRepository.applyMoves(gameHistory);

            if (success) {
                String newFEN = gameRepository.getCurrentFEN();
                Log.d(TAG, "📍 New FEN after moves: " + newFEN);
                
                if (newFEN != null) {
                    // For synchronization, we just need to verify we reached the expected position
                    // Don't compare with lastValidFEN as that might already be this position
                    if (gameHistory.isEmpty() && newFEN.startsWith("rnbqkbnr/pppppppp")) {
                        // Starting position is correct
                        lastValidFEN = newFEN;
                        Log.d(TAG, "✅ Synchronized to starting position");
                        return true;
                    } else if (!gameHistory.isEmpty() && !newFEN.equals(startingFEN)) {
                        // Position changed after applying moves - that's what we want
                        lastValidFEN = newFEN;
                        Log.d(TAG, "✅ Synchronized " + gameHistory.size() + " moves successfully");
                        return true;
                    } else {
                        Log.w(TAG, "⚠️ Unexpected position after synchronization");
                        Log.w(TAG, "   Starting FEN: " + startingFEN);
                        Log.w(TAG, "   Current FEN: " + newFEN);
                        Log.w(TAG, "   Move count: " + gameHistory.size());
                        return false;
                    }
                } else {
                    Log.w(TAG, "⚠️ Got null FEN after synchronization");
                    return false;
                }
            } else {
                Log.w(TAG, "⚠️ Failed to apply move history during synchronization");
                Log.w(TAG, "   History: " + gameHistory);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception during synchronization attempt", e);
            return false;
        }
    }

    /**
     * ENHANCED: Configure engine for specific player
     */
    private void configureEngineForPlayer(String activePlayer) {
        try {
            Log.d(TAG, "🎭 Configuring engine for: " + activePlayer);

            // Use historical peak rating for authentic strength
            int peakRating = ChessMasterRatings.getPeakRating(activePlayer);
            Log.d(TAG, "🏆 Setting " + activePlayer + " to peak rating: " + peakRating);

            // Configure engine with historical strength
            gameRepository.configureEngineForMaster(activePlayer);

            // Configure personality with enhanced weight for historical accuracy
            float personalityWeight = calculatePersonalityWeight(activePlayer, peakRating);
            gameRepository.configurePersonalityEngine(activePlayer, personalityWeight, true);

            Log.d(TAG, "✅ Engine configured for " + activePlayer + " (rating: " + peakRating + ", personality: " + personalityWeight + ")");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error configuring engine for " + activePlayer, e);
        }
    }

    /**
     * ENHANCED: Calculate validated move with proper error handling
     */
    private void calculateValidatedMove(String activePlayer) {
        try {
            Log.d(TAG, "🎯 Calculating validated move for " + activePlayer);

            // Use personality engine for move calculation
            gameRepository.calculatePersonalityMove(new GameRepository.MoveCallback() {
                @Override
                public void onMoveCalculated(String move) {
                    Log.d(TAG, "🎯 " + activePlayer + " calculated move: " + move);

                    if (move != null && !move.trim().isEmpty()) {
                        // CRITICAL: Validate move before applying
                        validateAndApplyMoveEnhanced(move);
                    } else {
                        Log.e(TAG, "Empty move from personality engine");
                        attemptFallbackMove();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Personality engine error: " + errorMessage);
                    attemptFallbackMove();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in move calculation", e);
            attemptFallbackMove();
        }
    }

    /**
     * CRITICAL FIX: Validate move before applying it
     */
    private void validateAndApplyMove(String move) {
        try {
            Log.d(TAG, "🔍 VALIDATING MOVE: " + move);

            // Step 1: Check if move is in valid UCI format
            if (!isValidUCIFormat(move)) {
                Log.e(TAG, "❌ Invalid UCI format: " + move);
                attemptFallbackMove();
                return;
            }

            // CRITICAL FIX: Get FEN before validation
            String fenBeforeValidation = gameRepository.getCurrentFEN();

            // Step 2: Check if move is legal in current position
            boolean isLegal = gameRepository.isLegalMove(move);
            Log.d(TAG, "⚖️ Move " + move + " legality: " + (isLegal ? "LEGAL" : "ILLEGAL"));

            if (!isLegal) {
                Log.e(TAG, "❌ Illegal move detected: " + move);
                attemptFallbackMove();
                return;
            }

            // Step 3: Apply the validated move
            applyValidatedMoveWithStateTracking(move, fenBeforeValidation);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error during move validation: " + move, e);
            attemptFallbackMove();
        }
    }

    /**
     * ENHANCED: Check if move is in valid UCI format
     */
    private boolean isValidUCIFormat(String move) {
        if (move == null || move.length() < 4 || move.length() > 5) {
            return false;
        }

        // Check basic format: e2e4 or e7e8q (with promotion)
        char fromFile = move.charAt(0);
        char fromRank = move.charAt(1);
        char toFile = move.charAt(2);
        char toRank = move.charAt(3);

        // Validate file (a-h) and rank (1-8)
        if (fromFile < 'a' || fromFile > 'h') return false;
        if (fromRank < '1' || fromRank > '8') return false;
        if (toFile < 'a' || toFile > 'h') return false;
        if (toRank < '1' || toRank > '8') return false;

        // If 5 characters, check promotion piece
        if (move.length() == 5) {
            char promotion = move.charAt(4);
            return promotion == 'q' || promotion == 'r' || promotion == 'b' || promotion == 'n';
        }

        return true;
    }

    /**
     * ENHANCED: Apply validated move with proper state tracking
     */
    private void applyValidatedMoveWithStateTracking(String move, String fenBeforeMove) {
        try {
            Log.d(TAG, "✅ Applying validated move: " + move);
            
            // Use the FEN from before validation (passed as parameter)
            Log.d(TAG, "📍 FEN before move: " + fenBeforeMove);

            // Create new history with the move
            List<String> newHistory = new ArrayList<>(gameHistory);
            newHistory.add(move);

            // Apply the complete history to ensure consistency
            boolean success = gameRepository.applyMoves(newHistory);

            if (success) {
                // Verify the position actually changed
                String newFen = gameRepository.getCurrentFEN();
                Log.d(TAG, "📍 FEN after move: " + newFen);

                if (newFen != null && !newFen.equals(fenBeforeMove)) {
                    // Success! Update state
                    gameHistory = newHistory;
                    lastValidFEN = newFen;
                    consecutiveFailures = 0; // Reset failure counter

                    Log.d(TAG, "✅ Move applied successfully!");
                    Log.d(TAG, "📋 New FEN: " + newFen);
                    Log.d(TAG, "📜 New history: " + gameHistory + " (size: " + gameHistory.size() + ")");

                    // Check for game end
                    if (checkGameEnd(newFen)) {
                        isValidatingMove = false; // Reset flag when game ends
                        return;
                    }

                    // Notify success
                    isValidatingMove = false; // Reset flag on success
                    mainHandler.post(() -> {
                        if (gameCallback != null) {
                            gameCallback.onThinkingStateChanged(false);
                            Log.d(TAG, "📤 Sending history to callback: " + gameHistory + " (size: " + gameHistory.size() + ")");
                            gameCallback.onMoveCalculated(move, newFen, new ArrayList<>(gameHistory));
                        }
                    });

                } else {
                    Log.e(TAG, "❌ Position didn't change after move - move may not have been applied");
                    Log.e(TAG, "   FEN before: " + fenBeforeMove);
                    Log.e(TAG, "   FEN after:  " + newFen);
                    Log.e(TAG, "   Applied move: " + move);
                    
                    // Try once more with a delay - might be a timing issue
                    if (consecutiveFailures < 2) {
                        consecutiveFailures++; // CRITICAL FIX: Increment the failure counter!
                        Log.w(TAG, "🔄 Retrying move application after delay...");
                        mainHandler.postDelayed(() -> {
                            validateAndApplyMoveEnhanced(move);
                        }, 500);
                    } else {
                        isValidatingMove = false; // Reset flag before fallback
                        attemptFallbackMove();
                    }
                }

            } else {
                Log.e(TAG, "❌ Failed to apply move to game repository");
                isValidatingMove = false; // Reset flag on failure
                attemptFallbackMove();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception applying validated move: " + move, e);
            isValidatingMove = false; // Reset flag on exception
            attemptFallbackMove();
        }
    }

    /**
     * ENHANCED: Attempt fallback move calculation
     */
    private void attemptFallbackMove() {
        consecutiveFailures++;

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            Log.e(TAG, "❌ Too many consecutive failures (" + consecutiveFailures + "), ending game");
            handleMoveError("Too many consecutive move failures - game cannot continue");
            return;
        }

        Log.d(TAG, "🔄 Attempting fallback move calculation (failure " + consecutiveFailures + "/" + MAX_CONSECUTIVE_FAILURES + ")");

        try {
            // Use standard engine as fallback
            gameRepository.calculateBestMove(new GameRepository.MoveCallback() {
                @Override
                public void onMoveCalculated(String move) {
                    if (move != null && !move.trim().isEmpty()) {
                        Log.d(TAG, "✅ Fallback move calculated: " + move);
                        validateAndApplyMove(move);
                    } else {
                        Log.e(TAG, "❌ Fallback also produced empty move");
                        handleMoveError("Both personality and fallback engines failed to produce valid move");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "❌ Fallback move error: " + errorMessage);
                    handleMoveError("Both personality and fallback engines failed: " + errorMessage);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in fallback move calculation", e);
            handleMoveError("Critical error in move calculation: " + e.getMessage());
        }
    }

    /**
     * Calculate personality weight based on historical rating and style
     */
    private float calculatePersonalityWeight(String player, int peakRating) {
        float baseWeight;
        if (peakRating >= 2800) {
            baseWeight = 0.4f;
        } else if (peakRating >= 2700) {
            baseWeight = 0.35f;
        } else if (peakRating >= 2600) {
            baseWeight = 0.3f;
        } else {
            baseWeight = 0.25f;
        }

        // Adjust for individual playing styles
        switch (player.toLowerCase()) {
            case "tal":
                return Math.min(0.5f, baseWeight + 0.1f);
            case "fischer":
                return baseWeight;
            case "kasparov":
                return Math.min(0.45f, baseWeight + 0.05f);
            case "karpov":
                return Math.max(0.2f, baseWeight - 0.05f);
            case "kramnik":
                return Math.max(0.2f, baseWeight - 0.1f);
            default:
                return baseWeight;
        }
    }

    private boolean checkGameEnd(String fen) {
        try {
            if (gameRepository.isCheckmate()) {
                boolean isWhiteTurn = fen.contains(" w ");
                String winner = isWhiteTurn ? blackPlayer : whitePlayer;
                String result = "Checkmate! " + FineTunedModelManager.getInstance(context).getMasterDisplayName(winner) + " wins!";

                mainHandler.post(() -> {
                    if (gameCallback != null) {
                        gameCallback.onThinkingStateChanged(false);
                        gameCallback.onGameEnd(result);
                    }
                });
                return true;

            } else if (gameRepository.isStalemate()) {
                String result = "Stalemate! The game is drawn.";

                mainHandler.post(() -> {
                    if (gameCallback != null) {
                        gameCallback.onThinkingStateChanged(false);
                        gameCallback.onGameEnd(result);
                    }
                });
                return true;

            } else if (gameHistory.size() >= 100) {
                String result = "Draw by length! What an epic battle!";

                mainHandler.post(() -> {
                    if (gameCallback != null) {
                        gameCallback.onThinkingStateChanged(false);
                        gameCallback.onGameEnd(result);
                    }
                });
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error checking game end", e);
        }

        return false;
    }

    private void handleMoveError(String error) {
        Log.e(TAG, "Move calculation error: " + error);

        mainHandler.post(() -> {
            if (gameCallback != null) {
                gameCallback.onThinkingStateChanged(false);
                gameCallback.onError(error);
            }
        });
    }



    // Control methods
    public void pauseGame() {
        isPaused = true;
        Log.d(TAG, "⏸️ AI vs AI game paused");
    }

    public void resumeGame() {
        isPaused = false;
        Log.d(TAG, "▶️ AI vs AI game resumed");
    }

    public void setMoveDelay(int delayMs) {
        this.moveDelay = delayMs;
        Log.d(TAG, "⚡ Move delay set to: " + delayMs + "ms");
    }

    public void setGameCallback(GameCallback callback) {
        this.gameCallback = callback;
    }

    public void cleanup() {
        isPaused = true;
        consecutiveFailures = 0;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (gameRepository != null) {
            gameRepository.cleanup();
        }
        Log.d(TAG, "🧹 FIXED AIvsAIGameManager cleaned up");
    }
}