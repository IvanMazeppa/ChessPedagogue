package com.example.chesspedagogue;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🧩 Tactical Puzzle Activity - Interactive Chess Puzzle Training
 * 
 * Features:
 * - Solo puzzle practice with multiple difficulty levels
 * - Real-time competition against AI assistant
 * - Themed tactical training (pins, forks, skewers, etc.)
 * - Performance tracking and statistics
 * - Achievement system and progress monitoring
 */
public class TacticalPuzzleActivity extends AppCompatActivity {
    private static final String TAG = "TacticalPuzzleActivity";
    
    // UI Components
    private TextView puzzleTitleView;
    private TextView puzzleDescriptionView;
    private TextView puzzleStatsView;
    private ChessBoardView chessBoardView;
    private EditText moveInputView;
    private Button submitMoveButton;
    private Button hintButton;
    private Button nextPuzzleButton;
    private LinearLayout resultLayout;
    private TextView resultTextView;
    private TextView assistantResultView;
    private ProgressBar loadingProgressBar;
    private TextView timerView;
    
    // Core components - UPGRADED TO PROFESSIONAL SYSTEM
    private ModernTacticalEngine tacticalEngine;
    private LichessPuzzle currentPuzzle;
    private ModernTacticalEngine.UserTacticalStats userStats;
    
    // Multi-move solution tracking
    private int currentSolutionIndex = 0;
    private boolean waitingForNextMove = false;
    private String currentBoardPosition; // Tracks current FEN after moves
    
    // Touch coordinates for UCI conversion
    private int lastMoveFromRow = -1, lastMoveFromCol = -1;
    private int lastMoveToRow = -1, lastMoveToCol = -1;
    
    // Game state
    private String selectedMode = "practice"; // "practice", "competitive", "themed"
    private String selectedTheme = "all";
    private int selectedDifficulty = 0; // 0 = all
    private boolean isAssistantMode = false;
    private long puzzleStartTime;
    private Handler timerHandler;
    private Runnable timerRunnable;
    
    // Touch-based move input
    private int selectedFromRow = -1;
    private int selectedFromCol = -1;
    private boolean pieceSelected = false;
    
    // Threading
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactical_puzzle);
        
        initializeComponents();
        setupUI();
        setupEventHandlers();
        
        // Get mode from intent
        Intent intent = getIntent();
        selectedMode = intent.getStringExtra("mode");
        if (selectedMode == null) selectedMode = "practice";
        
        selectedTheme = intent.getStringExtra("theme");
        if (selectedTheme == null) selectedTheme = "all";
        
        selectedDifficulty = intent.getIntExtra("difficulty", 0);
        isAssistantMode = intent.getBooleanExtra("assistant_mode", false);
        
        Log.d(TAG, String.format("🎯 Starting tactical puzzles: mode=%s, theme=%s, difficulty=%d", 
            selectedMode, selectedTheme, selectedDifficulty));
        
        startNewPuzzle();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        // ModernTacticalEngine handles cleanup automatically
    }
    
    private void initializeComponents() {
        // Initialize PROFESSIONAL puzzle system
        tacticalEngine = ModernTacticalEngine.getInstance(this);
        userStats = tacticalEngine.getUserStatistics();
        executorService = Executors.newSingleThreadExecutor();
        timerHandler = new Handler(Looper.getMainLooper());
        
        // Find UI components
        puzzleTitleView = findViewById(R.id.puzzle_title);
        puzzleDescriptionView = findViewById(R.id.puzzle_description);
        puzzleStatsView = findViewById(R.id.puzzle_stats);
        chessBoardView = findViewById(R.id.chess_board);
        moveInputView = findViewById(R.id.move_input);
        submitMoveButton = findViewById(R.id.submit_move_button);
        hintButton = findViewById(R.id.hint_button);
        nextPuzzleButton = findViewById(R.id.next_puzzle_button);
        resultLayout = findViewById(R.id.result_layout);
        resultTextView = findViewById(R.id.result_text);
        assistantResultView = findViewById(R.id.assistant_result);
        loadingProgressBar = findViewById(R.id.loading_progress);
        timerView = findViewById(R.id.timer_view);
        
        Log.d(TAG, "✅ Components initialized");
    }
    
    private void setupUI() {
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getModeTitle());
        }
        
        // Setup chess board with touch input enabled
        if (chessBoardView != null) {
            chessBoardView.setClickable(true); // Enable touch input for move entry
            chessBoardView.setOnSquareTapListener(this::onSquareTapped);
        }
        
        // Initial UI state
        resultLayout.setVisibility(View.GONE);
        nextPuzzleButton.setVisibility(View.GONE);
        
        updateStatsDisplay();
        
        Log.d(TAG, "✅ UI setup complete");
    }
    
    private void setupEventHandlers() {
        submitMoveButton.setOnClickListener(v -> submitMove());
        
        hintButton.setOnClickListener(v -> showHint());
        
        nextPuzzleButton.setOnClickListener(v -> startNewPuzzle());
        
        // Enter key in move input
        moveInputView.setOnEditorActionListener((v, actionId, event) -> {
            submitMove();
            return true;
        });
    }
    
    private void startNewPuzzle() {
        Log.d(TAG, "🚀 Starting new puzzle...");
        
        // Reset UI
        resultLayout.setVisibility(View.GONE);
        nextPuzzleButton.setVisibility(View.GONE);
        moveInputView.setText("");
        moveInputView.setEnabled(true);
        submitMoveButton.setEnabled(true);
        loadingProgressBar.setVisibility(View.VISIBLE);
        
        // Reset multi-move solution tracking
        currentSolutionIndex = 0;
        waitingForNextMove = false;
        currentBoardPosition = null;
        
        // Clear any previous piece selection
        clearSelection();
        
        // Get new puzzle using PROFESSIONAL system
        executorService.execute(() -> {
            try {
                currentPuzzle = tacticalEngine.getNextPuzzle(selectedTheme);
                
                if (currentPuzzle == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "❌ No puzzles available for selected criteria", Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }
                
                // Puzzle session automatically started by ModernTacticalEngine
                puzzleStartTime = System.currentTimeMillis();
                
                runOnUiThread(() -> {
                    displayPuzzle(currentPuzzle);
                    startTimer();
                    loadingProgressBar.setVisibility(View.GONE);
                    
                    // Get assistant solution if in competitive mode
                    if (isAssistantMode) {
                        getAssistantSolution();
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error starting new puzzle", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading puzzle: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loadingProgressBar.setVisibility(View.GONE);
                });
            }
        });
    }
    
    private void displayPuzzle(LichessPuzzle puzzle) {
        puzzleTitleView.setText(puzzle.getDisplayTitle());
        
        // Initialize current board position with the puzzle's initial position
        currentBoardPosition = puzzle.initialPosition;
        
        // Determine whose turn it is from the position
        boolean isWhiteTurn = isWhiteToMove(currentBoardPosition);
        String turnInfo = isWhiteTurn ? "White to move" : "Black to move";
        
        puzzleDescriptionView.setText(String.format("🎯 %s | %s | %s", 
            turnInfo, puzzle.getDifficultyDescription(), puzzle.getHint()));
        
        // Setup chess board with correct orientation
        if (chessBoardView != null) {
            chessBoardView.updateBoardFromFen(currentBoardPosition);
            
            // Flip board if black to move (so black pieces are at bottom)
            chessBoardView.setFlipped(!isWhiteTurn);
            
            Log.d(TAG, String.format("🎯 Board setup: Original FEN=%s", puzzle.fen));
            Log.d(TAG, String.format("🎯 Board setup: After move %s: %s, WhiteTurn=%s, BoardFlipped=%s", 
                puzzle.opponentMove, currentBoardPosition, isWhiteTurn, !isWhiteTurn));
        }
        
        // Update stats with professional system
        updateStatsDisplay();
        
        Log.d(TAG, String.format("🎯 Displaying puzzle: %s (Rating: %d, Turn: %s, Solution: %s)", 
            puzzle.puzzleId, puzzle.rating, turnInfo, puzzle.getFirstSolutionMove()));
    }
    
    /**
     * 🔍 Determine if it's white's turn from FEN string
     */
    private boolean isWhiteToMove(String fen) {
        String[] parts = fen.split(" ");
        return parts.length > 1 && "w".equals(parts[1]);
    }
    
    private void submitMove() {
        String userMove = moveInputView.getText().toString().trim();
        
        if (userMove.isEmpty()) {
            Toast.makeText(this, "Please enter a move (e.g., Nf3, e4, 0-0)", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "🎯 Checking move: " + userMove);
        
        // Disable input during checking
        moveInputView.setEnabled(false);
        submitMoveButton.setEnabled(false);
        
        // Check move against current solution index
        executorService.execute(() -> {
            try {
                // Instead of relying on LichessPuzzle conversion, validate by expected UCI move
                String expectedUci = currentPuzzle.getSolutionMove(currentSolutionIndex);
                String userUci = convertUserMoveToUci(userMove, lastMoveFromRow, lastMoveFromCol, lastMoveToRow, lastMoveToCol);
                
                boolean isCorrect = expectedUci.equals(userUci);
                Log.d(TAG, String.format("🔍 Direct UCI validation: expected='%s', user='%s', match=%s", 
                    expectedUci, userUci, isCorrect));
                
                runOnUiThread(() -> {
                    if (isCorrect) {
                        // Apply the user's move to the board
                        applyMoveToBoard(userMove);
                        currentSolutionIndex++;
                        
                        if (currentPuzzle.isSolutionComplete(currentSolutionIndex)) {
                            // Puzzle completely solved!
                            ModernTacticalEngine.PuzzleResult result = tacticalEngine.submitSolution(userMove);
                            displayResult(result);
                            stopTimer();
                            
                            // Update stats with professional system
                            userStats = tacticalEngine.getUserStatistics();
                            updateStatsDisplay();
                        } else {
                            // Check if next move is opponent's response (should be auto-played)
                            if (isOpponentMove(currentSolutionIndex)) {
                                // Auto-play opponent's response
                                autoPlayOpponentMove();
                            } else {
                                // More user moves needed - continue sequence
                                updateBoardForNextMove();
                                displayContinuationResult(currentSolutionIndex);
                            }
                        }
                    } else {
                        // Incorrect move
                        displayIncorrectMoveResult(userMove);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error checking solution", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error checking move: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    moveInputView.setEnabled(true);
                    submitMoveButton.setEnabled(true);
                });
            }
        });
    }
    
    private void displayResult(ModernTacticalEngine.PuzzleResult result) {
        resultTextView.setText(result.getResultMessage());
        
        if (result.isCorrect) {
            resultTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            
            // Show rating change
            String ratingText = String.format(" (%s)", result.ratingChange.getDescription());
            resultTextView.append(ratingText);
            
            nextPuzzleButton.setVisibility(View.VISIBLE);
        } else {
            resultTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            
            // Show hint
            String hintText = String.format("\n💡 Hint: %s", result.hint);
            resultTextView.append(hintText);
            
            // Allow another attempt
            moveInputView.setEnabled(true);
            submitMoveButton.setEnabled(true);
            moveInputView.setText("");
            moveInputView.requestFocus();
        }
        
        resultLayout.setVisibility(View.VISIBLE);
        
        Log.d(TAG, String.format("📊 Result: %s | Rating: %d → %d (%+d)", 
            result.isCorrect ? "CORRECT" : "INCORRECT",
            result.ratingChange.oldRating, result.ratingChange.newRating, result.ratingChange.change));
    }
    
    private void displayContinuationResult(int moveIndex) {
        resultTextView.setText(String.format("✅ Correct! Continue with move %d of %d", 
            moveIndex + 1, currentPuzzle.getSolutionLength()));
        resultTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        
        // Show next expected move as hint
        String nextMove = currentPuzzle.getSolutionMove(moveIndex);
        if (!nextMove.isEmpty()) {
            String hintText = String.format("\n💡 Next: Find the continuation...");
            resultTextView.append(hintText);
        }
        
        resultLayout.setVisibility(View.VISIBLE);
        
        // Re-enable input for next move
        moveInputView.setEnabled(true);
        submitMoveButton.setEnabled(true);
        moveInputView.setText("");
        moveInputView.requestFocus();
        
        Log.d(TAG, String.format("📊 Continuation: Move %d/%d correct, waiting for next move", 
            moveIndex, currentPuzzle.getSolutionLength()));
    }
    
    private void displayIncorrectMoveResult(String userMove) {
        resultTextView.setText("❌ Incorrect move");
        resultTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        
        // Show hint for current move
        String expectedMove = currentPuzzle.getSolutionMove(currentSolutionIndex);
        String hintText = String.format("\n💡 Hint for move %d: Look for %s", 
            currentSolutionIndex + 1, expectedMove);
        resultTextView.append(hintText);
        
        resultLayout.setVisibility(View.VISIBLE);
        
        // Allow another attempt
        moveInputView.setEnabled(true);
        submitMoveButton.setEnabled(true);
        moveInputView.setText("");
        moveInputView.requestFocus();
        
        Log.d(TAG, String.format("📊 Incorrect: Move %d/%d wrong, expected: %s, got: %s", 
            currentSolutionIndex + 1, currentPuzzle.getSolutionLength(), expectedMove, userMove));
    }
    
    private void getAssistantSolution() {
        assistantResultView.setText("🎭 Alekhine is analyzing this position...");
        assistantResultView.setVisibility(View.VISIBLE);
        
        long assistantStartTime = System.currentTimeMillis();
        
        tacticalEngine.validateAssistant("alekhine").thenAccept(assistantResult -> {
            if (assistantResult != null) {
                long assistantTime = System.currentTimeMillis() - assistantStartTime;
                runOnUiThread(() -> {
                    String resultIcon = assistantResult.isCorrect ? "✅" : "❌";
                    String performanceEmoji = getPerformanceEmoji(assistantResult.performanceRating);
                    
                    String assistantText = String.format(
                        "🎭 Alekhine: %s %s\n" +
                        "⏱️ Time: %.1fs | 📊 Performance: %s (%d)\n" +
                        "🎯 Puzzle Rating: %d | 🏷️ Theme: %s",
                        assistantResult.assistantMove, resultIcon,
                        assistantTime / 1000.0, assistantResult.performanceGrade, assistantResult.performanceRating,
                        assistantResult.puzzleRating, String.join(", ", assistantResult.themes)
                    );
                    assistantResultView.setText(assistantText);
                    
                    Log.d(TAG, String.format("🎭 Alekhine validation: %s | %s | Rating: %d | Time: %.1fs",
                        assistantResult.assistantMove, 
                        assistantResult.isCorrect ? "CORRECT" : "INCORRECT",
                        assistantResult.performanceRating, assistantTime / 1000.0));
                });
            }
        }).exceptionally(throwable -> {
            Log.e(TAG, "❌ Error getting Alekhine's analysis", throwable);
            runOnUiThread(() -> {
                assistantResultView.setText("🎭 Alekhine: 'This position is too complex for quick analysis.'");
            });
            return null;
        });
    }
    
    private String getPerformanceEmoji(int rating) {
        if (rating < 1000) return "📉";
        if (rating < 1300) return "📊";
        if (rating < 1600) return "📈";
        if (rating < 1900) return "🔥";
        if (rating < 2200) return "⭐";
        return "👑";
    }
    
    private void showHint() {
        if (currentPuzzle != null) {
            String hint = currentPuzzle.getHint();
            String detailedHint = String.format("%s\n\n🎭 Themes: %s\n📊 Difficulty: %s (Rating: %d)\n🎮 From: %s", 
                hint, String.join(", ", currentPuzzle.themes), 
                currentPuzzle.getDifficultyDescription(), currentPuzzle.rating,
                currentPuzzle.gameUrl.contains("lichess") ? "Lichess Game" : "Chess Game");
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("💡 Alekhine's Hint")
                   .setMessage(detailedHint)
                   .setPositiveButton("Thanks, Alekhine!", null)
                   .show();
            
            Log.d(TAG, "💡 Showed hint for puzzle: " + currentPuzzle.puzzleId);
        }
    }
    
    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - puzzleStartTime;
                int seconds = (int) (elapsedTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                
                timerView.setText(String.format("⏱️ %d:%02d", minutes, seconds));
                
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }
    
    private void stopTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
    
    private void updateStatsDisplay() {
        if (userStats != null) {
            puzzleStatsView.setText(userStats.getOverview());
        }
    }
    
    private String getModeTitle() {
        switch (selectedMode) {
            case "competitive": return "🏆 Tactical Duel";
            case "themed": return "🎯 Themed Training";
            case "validation": return "🎭 Alekhine Validation";
            default: return "🧩 Tactical Puzzles";
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tactical_puzzle_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_change_difficulty) {
            showDifficultyDialog();
            return true;
        } else if (itemId == R.id.action_change_theme) {
            showThemeDialog();
            return true;
        } else if (itemId == R.id.action_view_stats) {
            showStatsDialog();
            return true;
        } else if (itemId == R.id.action_competitive_mode) {
            toggleCompetitiveMode();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showDifficultyDialog() {
        String[] difficulties = {"All Levels", "⭐ Beginner", "⭐⭐ Easy", "⭐⭐⭐ Medium", "⭐⭐⭐⭐ Hard", "⭐⭐⭐⭐⭐ Expert"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Difficulty")
               .setItems(difficulties, (dialog, which) -> {
                   selectedDifficulty = which;
                   Toast.makeText(this, "Difficulty changed to: " + difficulties[which], Toast.LENGTH_SHORT).show();
               })
               .show();
    }
    
    private void showThemeDialog() {
        List<LichessPuzzleDatabase.ThemeStats> themeStats = tacticalEngine.getAvailableThemes();
        List<String> themes = new ArrayList<>();
        themes.add("all");
        
        for (LichessPuzzleDatabase.ThemeStats stat : themeStats) {
            themes.add(stat.theme + " (" + stat.count + " puzzles)");
        }
        
        String[] themeArray = themes.toArray(new String[0]);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎯 Select Tactical Theme")
               .setItems(themeArray, (dialog, which) -> {
                   if (which == 0) {
                       selectedTheme = "all";
                   } else {
                       selectedTheme = themeStats.get(which - 1).theme;
                   }
                   Toast.makeText(this, "Theme changed to: " + selectedTheme, Toast.LENGTH_SHORT).show();
                   startNewPuzzle(); // Restart with new theme
               })
               .show();
    }
    
    private void showStatsDialog() {
        String statsText = String.format(
            "📊 TACTICAL PERFORMANCE\n\n" +
            "%s\n\n" +
            "🎯 Strongest: %s\n" +
            "📈 Weakest: %s\n" +
            "🔥 Current Streak: %d\n" +
            "⏱️ Average Time: %.1fs\n" +
            "🏆 Rating: %d (%s)\n" +
            "📈 30-day Gain: %+d\n" +
            "🏅 Best Rating: %d",
            userStats.getOverview(),
            userStats.strongestTheme,
            userStats.weakestTheme,
            userStats.currentStreak,
            userStats.averageTime,
            userStats.rating,
            userStats.tier,
            userStats.ratingGain30Days,
            userStats.bestRating
        );
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📊 Your Statistics")
               .setMessage(statsText)
               .setPositiveButton("Nice!", null)
               .show();
    }
    
    private void toggleCompetitiveMode() {
        isAssistantMode = !isAssistantMode;
        
        if (isAssistantMode) {
            Toast.makeText(this, "🤖 Competitive mode ON - Racing against Alekhine!", Toast.LENGTH_SHORT).show();
            assistantResultView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "🧩 Practice mode ON - Solo training", Toast.LENGTH_SHORT).show();
            assistantResultView.setVisibility(View.GONE);
        }
        
        // Reset current puzzle to apply new mode
        if (currentPuzzle != null) {
            startNewPuzzle();
        }
    }
    
    /**
     * 🎯 Handle touch-based move input on the chess board
     */
    private void onSquareTapped(int row, int col) {
        if (currentPuzzle == null) return;
        
        // Get turn information from the current board position
        boolean isWhiteTurn = isWhiteToMove(currentBoardPosition);
        boolean boardFlipped = !isWhiteTurn; // Board is flipped for black-to-move puzzles
        
        Log.d(TAG, String.format("🎯 Square tapped: %d,%d (piece selected: %s, white turn: %s, board flipped: %s)", 
            row, col, pieceSelected, isWhiteTurn, boardFlipped));
        
        if (!pieceSelected) {
            // First tap - select a piece
            char piece = chessBoardView.getPieceAt(row, col);
            if (piece != ' ') {
                // Check if it's the right color to move
                boolean isWhitePiece = Character.isUpperCase(piece);
                
                if (isWhiteTurn == isWhitePiece) {
                    selectedFromRow = row;
                    selectedFromCol = col;
                    pieceSelected = true;
                    
                    // Highlight the selected square
                    chessBoardView.setSelectedSquare(row, col);
                    
                    Log.d(TAG, String.format("✅ Piece selected: %c at %d,%d (turn: %s)", 
                        piece, row, col, isWhiteTurn ? "white" : "black"));
                    Toast.makeText(this, "Piece selected. Tap destination square.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Select a " + (isWhiteTurn ? "white" : "black") + " piece to move.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Select a piece to move.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Second tap - complete the move
            if (row == selectedFromRow && col == selectedFromCol) {
                // Deselect the piece if tapping the same square
                clearSelection();
                return;
            }
            
            // Store coordinates for UCI conversion
            lastMoveFromRow = selectedFromRow;
            lastMoveFromCol = selectedFromCol;
            lastMoveToRow = row;
            lastMoveToCol = col;
            
            // Convert board coordinates to algebraic notation
            String move = boardCoordsToAlgebraic(selectedFromRow, selectedFromCol, row, col, boardFlipped);
            if (move != null) {
                Log.d(TAG, String.format("🎯 Move completed: %s (%d,%d -> %d,%d, flipped: %s)", 
                    move, selectedFromRow, selectedFromCol, row, col, boardFlipped));
                
                // Update the text input field to show the move (for compatibility)
                moveInputView.setText(move);
                
                // Clear selection
                clearSelection();
                
                // Submit the move
                submitMove();
            } else {
                Log.e(TAG, "❌ Failed to convert coordinates to algebraic notation");
                Toast.makeText(this, "Invalid move format", Toast.LENGTH_SHORT).show();
                clearSelection();
            }
        }
    }
    
    /**
     * 🧹 Clear piece selection
     */
    private void clearSelection() {
        selectedFromRow = -1;
        selectedFromCol = -1;
        pieceSelected = false;
        chessBoardView.clearSelectionHighlight();
        Log.d(TAG, "🧹 Selection cleared");
    }
    
    /**
     * 🎯 Apply user's move to the visual board
     */
    private void applyMoveToBoard(String move) {
        try {
            // Get the UCI move from the puzzle solution (use current index, not index-1)
            String uciMove = currentPuzzle.getSolutionMove(currentSolutionIndex);
            
            if (uciMove == null || uciMove.isEmpty()) {
                Log.e(TAG, String.format("❌ Could not get UCI move for index %d", currentSolutionIndex));
                return;
            }
            
            Log.d(TAG, String.format("🎯 Applying move to board: %s (UCI: %s)", move, uciMove));
            Log.d(TAG, String.format("🔍 Current position before move: %s", currentBoardPosition));
            
            // Update board position using the correct UCI move from solution
            String newPosition = applyUciMoveToPosition(currentBoardPosition, uciMove);
            if (newPosition != null) {
                currentBoardPosition = newPosition;
                chessBoardView.updateBoardFromFen(newPosition);
                Log.d(TAG, String.format("✅ Board updated with new position: %s", newPosition));
                
                // Debug: Verify the piece is in the expected location
                char pieceAtDestination = getPieceAtSquare(newPosition, uciMove.substring(2, 4));
                Log.d(TAG, String.format("🔍 Piece at destination %s: '%c'", uciMove.substring(2, 4), pieceAtDestination));
            } else {
                Log.e(TAG, "❌ Failed to apply UCI move to position");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error applying move to board", e);
        }
    }
    
    /**
     * 🤖 Check if the current move index is an opponent move (should be auto-played)
     * 
     * Lichess puzzle solution array (after setup move already applied):
     * - solution[0] = h2f1 ← USER'S first move (currentSolutionIndex = 0)
     * - solution[1] = d2e2 ← OPPONENT'S response (currentSolutionIndex = 1, auto-played)
     * - solution[2] = f1g3 ← USER'S second move (currentSolutionIndex = 2)
     * - solution[3] = ... ← OPPONENT'S next response (currentSolutionIndex = 3, auto-played)
     */
    private boolean isOpponentMove(int moveIndex) {
        // User plays at EVEN indices (0, 2, 4...) = solution[0], solution[2], solution[4]...
        // Opponent plays at ODD indices (1, 3, 5...) = solution[1], solution[3], solution[5]...
        return moveIndex % 2 == 1;
    }
    
    /**
     * 🤖 Auto-play the opponent's response move
     */
    private void autoPlayOpponentMove() {
        try {
            String opponentMove = currentPuzzle.getSolutionMove(currentSolutionIndex);
            if (opponentMove.isEmpty()) {
                Log.e(TAG, "❌ No opponent move found at index " + currentSolutionIndex);
                return;
            }
            
            Log.d(TAG, String.format("🤖 Auto-playing opponent move: %s (index %d)", opponentMove, currentSolutionIndex));
            
            // Add a small delay to make auto-play feel more natural
            timerHandler.postDelayed(() -> {
                // Apply opponent's move to the board (UCI format)
                String newPosition = applyUciMoveToPosition(currentBoardPosition, opponentMove);
                if (newPosition != null) {
                    currentBoardPosition = newPosition;
                    chessBoardView.updateBoardFromFen(newPosition);
                    
                    // Advance to next move in sequence
                    currentSolutionIndex++;
                    
                    // Update UI for next user move
                    updateBoardForNextMove();
                    
                    // Show continuation message
                    displayOpponentResponseAndContinuation(opponentMove);
                    
                    Log.d(TAG, String.format("✅ Opponent move applied: %s, next user move: %d/%d", 
                        opponentMove, currentSolutionIndex + 1, currentPuzzle.getSolutionLength()));
                }
            }, 800); // 800ms delay for natural feel
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error auto-playing opponent move", e);
        }
    }
    
    /**
     * 📢 Display opponent's response and ask for continuation
     */
    private void displayOpponentResponseAndContinuation(String opponentMove) {
        // Convert UCI to algebraic for display (simplified conversion)
        String algebraicMove = convertUciToAlgebraic(opponentMove);
        
        resultTextView.setText(String.format("✅ Correct! Opponent played %s", algebraicMove));
        resultTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        
        if (currentSolutionIndex < currentPuzzle.getSolutionLength()) {
            String continuationText = String.format("\\n🎯 Continue with move %d of %d", 
                currentSolutionIndex + 1, currentPuzzle.getSolutionLength());
            resultTextView.append(continuationText);
        }
        
        resultLayout.setVisibility(View.VISIBLE);
        
        // Re-enable input for next user move
        moveInputView.setEnabled(true);
        submitMoveButton.setEnabled(true);
        moveInputView.setText("");
        moveInputView.requestFocus();
    }
    
    /**
     * 🔢 Convert UCI to algebraic notation (simplified for display)
     */
    private String convertUciToAlgebraic(String uciMove) {
        if (uciMove == null || uciMove.length() < 4) return uciMove;
        
        try {
            String from = uciMove.substring(0, 2);
            String to = uciMove.substring(2, 4);
            
            // Get piece at source position from current board
            char piece = getPieceAtSquare(currentBoardPosition, from);
            if (piece == ' ') return uciMove;
            
            // Check if destination has a piece (capture)
            char capturedPiece = getPieceAtSquare(currentBoardPosition, to);
            boolean isCapture = capturedPiece != ' ';
            
            // Convert to simple algebraic notation
            char pieceSymbol = Character.toUpperCase(piece);
            
            if (pieceSymbol == 'P') {
                // Pawn moves
                if (isCapture) {
                    return from.charAt(0) + "x" + to;
                } else {
                    return to;
                }
            } else {
                // Piece moves
                if (isCapture) {
                    return pieceSymbol + "x" + to;
                } else {
                    return pieceSymbol + to;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error converting UCI to algebraic: " + uciMove, e);
            return uciMove;
        }
    }
    
    /**
     * 🔍 Get piece at a specific square in FEN
     */
    private char getPieceAtSquare(String fen, String square) {
        if (square.length() != 2) return ' ';
        
        try {
            String position = fen.split(" ")[0];
            int file = square.charAt(0) - 'a';
            int rank = 8 - (square.charAt(1) - '0');
            
            char[][] board = fenToBoard(position);
            if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
                return board[rank][file];
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting piece at square: " + square, e);
        }
        return ' ';
    }
    
    /**
     * 🔄 Update board for next move in sequence
     */
    private void updateBoardForNextMove() {
        try {
            // Determine whose turn it should be for the next move
            boolean isWhiteTurn = isWhiteToMove(currentBoardPosition);
            
            // Update board orientation if needed
            chessBoardView.setFlipped(!isWhiteTurn);
            
            // Update puzzle description with correct turn info
            String turnInfo = isWhiteTurn ? "White to move" : "Black to move";
            puzzleDescriptionView.setText(String.format("🎯 %s | %s | Move %d of %d", 
                turnInfo, currentPuzzle.getDifficultyDescription(), 
                currentSolutionIndex + 1, currentPuzzle.getSolutionLength()));
            
            Log.d(TAG, String.format("🔄 Updated for next move: %s (move %d/%d)", 
                turnInfo, currentSolutionIndex + 1, currentPuzzle.getSolutionLength()));
                
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating board for next move", e);
        }
    }
    
    /**
     * 🎯 Apply UCI move to FEN position (simple implementation)
     */
    private String applyUciMoveToPosition(String fen, String uciMove) {
        try {
            if (uciMove == null || uciMove.length() < 4) return fen;
            
            // Parse FEN components
            String[] fenParts = fen.split(" ");
            if (fenParts.length < 2) return fen;
            
            String position = fenParts[0];
            String activeColor = fenParts[1];
            
            // Parse UCI move
            String fromSquare = uciMove.substring(0, 2);
            String toSquare = uciMove.substring(2, 4);
            
            // Convert squares to array indices
            int fromCol = fromSquare.charAt(0) - 'a';
            int fromRow = 8 - (fromSquare.charAt(1) - '0');
            int toCol = toSquare.charAt(0) - 'a';
            int toRow = 8 - (toSquare.charAt(1) - '0');
            
            // Create board array from FEN
            char[][] board = fenToBoard(position);
            
            // Apply the move
            char piece = board[fromRow][fromCol];
            board[fromRow][fromCol] = ' ';
            board[toRow][toCol] = piece;
            
            // Convert back to FEN position string
            String newPosition = boardToFen(board);
            
            // Toggle active color
            String newActiveColor = "w".equals(activeColor) ? "b" : "w";
            
            // Rebuild FEN
            return newPosition + " " + newActiveColor + 
                   (fenParts.length > 2 ? " " + String.join(" ", java.util.Arrays.copyOfRange(fenParts, 2, fenParts.length)) : "");
                   
        } catch (Exception e) {
            Log.e(TAG, "❌ Error applying UCI move to position", e);
            return fen;
        }
    }
    
    /**
     * 🏗️ Convert FEN position string to 8x8 board array
     */
    private char[][] fenToBoard(String position) {
        char[][] board = new char[8][8];
        
        String[] ranks = position.split("/");
        for (int rank = 0; rank < 8 && rank < ranks.length; rank++) {
            int file = 0;
            for (char c : ranks[rank].toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptySquares = c - '0';
                    for (int i = 0; i < emptySquares && file < 8; i++) {
                        board[rank][file++] = ' ';
                    }
                } else if (file < 8) {
                    board[rank][file++] = c;
                }
            }
        }
        return board;
    }
    
    /**
     * 🏗️ Convert 8x8 board array back to FEN position string
     */
    private String boardToFen(char[][] board) {
        StringBuilder fen = new StringBuilder();
        
        for (int rank = 0; rank < 8; rank++) {
            int emptyCount = 0;
            for (int file = 0; file < 8; file++) {
                char piece = board[rank][file];
                if (piece == ' ') {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece);
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (rank < 7) {
                fen.append('/');
            }
        }
        return fen.toString();
    }
    
    /**
     * 🔢 Convert user's move to UCI based on coordinates (RELIABLE)
     */
    private String convertUserMoveToUci(String algebraicMove, int fromRow, int fromCol, int toRow, int toCol) {
        try {
            // Convert coordinates directly to UCI notation
            char fromFile = (char)('a' + fromCol);
            int fromRank = 8 - fromRow;
            char toFile = (char)('a' + toCol);
            int toRank = 8 - toRow;
            
            String uciMove = "" + fromFile + fromRank + toFile + toRank;
            Log.d(TAG, String.format("🎯 Coordinate→UCI: (%d,%d)→(%d,%d) = %s", 
                fromRow, fromCol, toRow, toCol, uciMove));
            
            return uciMove;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error converting coordinates to UCI", e);
            return null;
        }
    }
    
    /**
     * 🔢 Convert algebraic notation to UCI notation (DEPRECATED - using coordinates instead)
     */
    private String convertAlgebraicToUci(String algebraicMove, String fen) {
        // This method is no longer used since we get UCI directly from coordinates
        Log.d(TAG, String.format("🔄 Skipping conversion: %s in position %s", algebraicMove, fen));
        return null;
    }
    
    /**
     * 🔍 Find source square for a piece move
     */
    private String findSourceSquare(String fen, char pieceType, String destination, boolean isCapture) {
        try {
            String position = fen.split(" ")[0];
            char[][] board = fenToBoard(position);
            
            // Determine active color
            boolean isWhite = fen.split(" ")[1].equals("w");
            char targetPiece = isWhite ? pieceType : Character.toLowerCase(pieceType);
            
            // Search all squares for the piece
            for (int rank = 0; rank < 8; rank++) {
                for (int file = 0; file < 8; file++) {
                    if (board[rank][file] == targetPiece) {
                        String sourceSquare = "" + (char)('a' + file) + (8 - rank);
                        
                        // Check if this piece can move to destination
                        if (canPieceMoveTo(board, rank, file, destination, pieceType)) {
                            return sourceSquare;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error finding source square", e);
        }
        return null;
    }
    
    /**
     * 🎯 Check if piece can move to destination (basic validation)
     */
    private boolean canPieceMoveTo(char[][] board, int fromRank, int fromFile, String destination, char pieceType) {
        if (destination.length() != 2) return false;
        
        int toFile = destination.charAt(0) - 'a';
        int toRank = 8 - (destination.charAt(1) - '0');
        
        if (toRank < 0 || toRank >= 8 || toFile < 0 || toFile >= 8) return false;
        
        // Basic piece movement validation
        int deltaRank = Math.abs(toRank - fromRank);
        int deltaFile = Math.abs(toFile - fromFile);
        
        switch (Character.toUpperCase(pieceType)) {
            case 'N': // Knight moves in L-shape
                return (deltaRank == 2 && deltaFile == 1) || (deltaRank == 1 && deltaFile == 2);
                
            case 'B': // Bishop moves diagonally
                return deltaRank == deltaFile && deltaRank > 0;
                
            case 'R': // Rook moves horizontally/vertically
                return (deltaRank == 0 && deltaFile > 0) || (deltaFile == 0 && deltaRank > 0);
                
            case 'Q': // Queen combines rook and bishop
                return (deltaRank == deltaFile && deltaRank > 0) || 
                       (deltaRank == 0 && deltaFile > 0) || 
                       (deltaFile == 0 && deltaRank > 0);
                       
            case 'K': // King moves one square in any direction
                return deltaRank <= 1 && deltaFile <= 1 && (deltaRank + deltaFile > 0);
                
            case 'P': // Pawn moves (simplified - forward 1 or 2, diagonal captures)
                // For puzzles, assume any reasonable pawn move is valid
                return deltaRank <= 2 && deltaFile <= 1;
                
            default:
                return true; // Unknown piece type, assume valid
        }
    }
    
    /**
     * 🔢 Convert board coordinates to algebraic notation
     */
    private String boardCoordsToAlgebraic(int fromRow, int fromCol, int toRow, int toCol, boolean boardFlipped) {
        try {
            // The ChessBoardView handles flipping internally, so we need to convert 
            // the visual coordinates back to logical board coordinates
            int logicalFromRow, logicalFromCol, logicalToRow, logicalToCol;
            
            // The ChessBoardView internal flipping means the coordinates we receive
            // are already relative to the current board orientation
            // For a flipped board, we don't need to transform coordinates
            // because the touch coordinates already correspond to the visual layout
            logicalFromRow = fromRow;
            logicalFromCol = fromCol;
            logicalToRow = toRow;
            logicalToCol = toCol;
            
            // Convert logical coordinates to chess notation (a1 = bottom-left)
            char fromFile = (char) ('a' + logicalFromCol);
            int fromRank = 8 - logicalFromRow;
            char toFile = (char) ('a' + logicalToCol);
            int toRank = 8 - logicalToRow;
            
            // Get the piece that's moving (using visual coordinates for board view)
            char piece = chessBoardView.getPieceAt(fromRow, fromCol);
            if (piece == ' ') {
                Log.e(TAG, "❌ No piece at source square");
                return null;
            }
            
            // Check if it's a capture (using visual coordinates for board view)
            char capturedPiece = chessBoardView.getPieceAt(toRow, toCol);
            boolean isCapture = capturedPiece != ' ';
            
            // Convert to proper algebraic notation
            String move;
            char pieceSymbol = Character.toUpperCase(piece);
            String destination = "" + toFile + toRank;
            
            if (pieceSymbol == 'P') {
                // Pawn moves
                if (isCapture) {
                    move = fromFile + "x" + destination;
                } else {
                    move = destination;
                }
            } else {
                // Piece moves
                if (isCapture) {
                    move = pieceSymbol + "x" + destination;
                } else {
                    move = pieceSymbol + destination;
                }
            }
            
            Log.d(TAG, String.format("🔢 Touch coords: %d,%d -> %d,%d (flipped: %s)", 
                fromRow, fromCol, toRow, toCol, boardFlipped));
            Log.d(TAG, String.format("🔢 Logical coords: %d,%d -> %d,%d", 
                logicalFromRow, logicalFromCol, logicalToRow, logicalToCol));
            Log.d(TAG, String.format("🔢 Chess notation: %c%d -> %c%d (piece=%c, move=%s)", 
                fromFile, fromRank, toFile, toRank, piece, move));
            
            return move;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error converting coordinates to algebraic", e);
            return null;
        }
    }
}