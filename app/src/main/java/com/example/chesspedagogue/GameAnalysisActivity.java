package com.example.chesspedagogue;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity for analyzing chess games move by move with Stockfish engine.
 */
public class GameAnalysisActivity extends AppCompatActivity {

    private static final String TAG = "GameAnalysisActivity";

    // Core components
    private StockfishManager engine;
    private ChessBoardView boardView;
    private String selectedChessMaster = "tal";
    private Button changeMasterButton;

    private Button speakAnalysisButton;
    private OpenAITTSService ttsService;

    // Game data
    private ArrayList<String> moveHistory;
    private int currentMoveIndex = -1; // -1 means initial position
    private String[] positions; // FEN positions for each move

    // UI components
    private TextView moveInfoTextView;
    private TextView analysisTextView;
    private Button prevButton;
    private Button nextButton;
    private Button analyzeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_analysis);

        String receivedFen = getIntent().getStringExtra("FEN");
        moveHistory = getIntent().getStringArrayListExtra("MOVE_HISTORY");


        // Find UI components
        boardView = findViewById(R.id.analysisBoardView);
        moveInfoTextView = findViewById(R.id.moveInfoTextView);
        analysisTextView = findViewById(R.id.analysisTextView);
        prevButton = findViewById(R.id.prevMoveButton);
        nextButton = findViewById(R.id.nextMoveButton);
        analyzeButton = findViewById(R.id.analyzeButton);
        changeMasterButton = findViewById(R.id.changeMasterButton);
        changeMasterButton.setOnClickListener(v -> showChessMasterSelector());


        speakAnalysisButton = findViewById(R.id.speakAnalysisButton);


        Log.d(TAG, "Received FEN: " + (receivedFen != null ? receivedFen : "NULL"));
        Log.d(TAG, "Received move history: " + (moveHistory != null ? moveHistory.size() + " moves" : "NULL"));

        if (receivedFen == null) {
            // Use default starting position if nothing was passed
            receivedFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            Log.d(TAG, "Using default starting position");
        }

        if (moveHistory == null) {
            moveHistory = new ArrayList<>();
            Log.d(TAG, "No move history received, using empty list");
        }

        // Initialize the engine
        initializeStockfishEngine();

        // Make sure to use the received FEN
        positions = new String[moveHistory.size() + 1];
        positions[0] = receivedFen;

        // Generate remaining positions if we have moves
        if (!moveHistory.isEmpty()) {
            generatePositions();
        }

        // Show the initial position
        updateToPosition(0);

        if (speakAnalysisButton != null) {
            speakAnalysisButton.setOnClickListener(v -> speakCurrentAnalysis());
            Log.d(TAG, "Speak analysis button initialized");
        } else {
            Log.e(TAG, "Could not find speakAnalysisButton in layout!");
        }
        ttsService = new OpenAITTSService(this);
        ttsService.setApiKey(ApiKeyConfig.getApiKey(this));

        // Get move history from intent - with extra logging
        moveHistory = getIntent().getStringArrayListExtra("MOVE_HISTORY");
        if (moveHistory == null) {
            moveHistory = new ArrayList<>();
            Log.d(TAG, "No move history received from intent");
        } else {
            Log.d(TAG, "Received move history with " + moveHistory.size() + " moves");
            for (int i = 0; i < moveHistory.size(); i++) {
                Log.d(TAG, "Move " + (i + 1) + ": " + moveHistory.get(i));
            }
        }

        // Initialize the engine with detailed logging
        try {
            initializeStockfishEngine();

            // Generate all positions from the moves
            generatePositions();

            // Set up initial position
            updateToPosition(0);

            // Set up button listeners
            prevButton.setOnClickListener(v -> showPreviousMove());
            nextButton.setOnClickListener(v -> showNextMove());
            analyzeButton.setOnClickListener(v -> analyzeCurrentPosition());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing analysis: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        // Check if we're loading a saved game
        long gameId = getIntent().getLongExtra("GAME_ID", -1);
        if (gameId != -1) {
            // We're loading a saved game
            loadSavedGame(gameId);
        } else {
            // Normal behavior - loading from move history passed in intent
            moveHistory = getIntent().getStringArrayListExtra("MOVE_HISTORY");
            if (moveHistory == null) {
                moveHistory = new ArrayList<>();
            }
            generatePositions();
            updateToPosition(0);
        }

    }

    private void speakCurrentAnalysis() {
        String analysis = analysisTextView.getText().toString();
        if (analysis.isEmpty() || analysis.contains("analyzing")) {
            Toast.makeText(this, "No analysis available to speak", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show speaking indicator
        Toast.makeText(this, "Coach " + selectedChessMaster + " is speaking...", Toast.LENGTH_SHORT).show();

        // Get appropriate voice for the selected master
        String voice = ChessMasterVoiceManager.getVoiceForMaster(selectedChessMaster);

        // Speak the analysis with your improved chunking
        ttsService.speakWithChunking(analysis, new OpenAITTSService.TTSCallback() {
            @Override
            public void onSpeechStarted() {
                // Update UI if needed
            }

            @Override
            public void onSpeechReady(File audioFile) {
                // Not needed here
            }

            @Override
            public void onSpeechCompleted() {
                // Update UI when speech is done
                runOnUiThread(() -> {
                    Toast.makeText(GameAnalysisActivity.this,
                            "Analysis complete", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(GameAnalysisActivity.this,
                            "Speech error: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Add this method to show a dialog for selecting a chess master
    private void showChessMasterSelector() {
        String[] masters = {"Tal", "Botvinnik", "Kramnik", "Fischer", "Kasparov", "Karpov"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Chess Coach")
                .setItems(masters, (dialog, which) -> {
                    selectedChessMaster = masters[which].toLowerCase();
                    // Update UI to show selected master
                    analyzeButton.setText("Analyze with " + masters[which]);
                    // Clear previous analysis
                    analysisTextView.setText("");

                    Toast.makeText(this, masters[which] + " will analyze your position",
                            Toast.LENGTH_SHORT).show();
                });
        builder.create().show();
    }

    /**
     * Initialize the Stockfish chess engine
     */
    private void initializeStockfishEngine() {
        try {
            // Try using the native library first
            File engineFile = new File(getApplicationInfo().nativeLibraryDir, "libstockfish.so");

            // If not found, try extracting from assets as fallback
            if (!engineFile.exists()) {
                Log.d(TAG, "Stockfish not found in native library dir, trying assets fallback");
                try {
                    engineFile = Utils.copyAssetToExecutableDir(this, "stockfish", "stockfish");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to extract Stockfish from assets", e);
                }
            }

            if (!engineFile.exists() || !engineFile.canExecute()) {
                Toast.makeText(this, "Stockfish engine not found", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "Initializing Stockfish engine from: " + engineFile.getAbsolutePath());

            // Initialize the engine
            engine = new StockfishManager();
            if (engine.startEngine(engineFile.getAbsolutePath())) {
                // Set a higher skill level for analysis
                engine.setSkillLevel(20); // Use maximum strength for analysis
                engine.newGame();
                Log.d(TAG, "Engine initialized successfully");
            } else {
                Toast.makeText(this, "Failed to start Stockfish engine",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing engine", e);
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    private void getAIEnhancedAnalysis() {
        // Show loading state
        analysisTextView.setText("Coach " + selectedChessMaster.substring(0, 1).toUpperCase() +
                selectedChessMaster.substring(1) + " is analyzing...");

        // Get the current position and Stockfish evaluation
        String currentFen = positions[currentMoveIndex];

        // Run this on a background thread
        new Thread(() -> {
            try {
                // Get Stockfish technical analysis
                engine.setPosition(currentFen);
                String engineAnalysis = getDetailedEngineAnalysis(2000); // 2 seconds analysis

                // Format the analysis for the AI
                StringBuilder prompt = new StringBuilder();
                prompt.append("You are analyzing a chess position as ")
                        .append(getFullNameForMaster(selectedChessMaster))
                        .append(".\n\nFEN: ")
                        .append(currentFen)
                        .append("\n\nEngine analysis: ")
                        .append(engineAnalysis);

                // Add context about the move history
                if (currentMoveIndex > 0) {
                    prompt.append("\n\nPrevious moves: ");
                    for (int i = 0; i < currentMoveIndex; i++) {
                        int moveNum = (i / 2) + 1;
                        if (i % 2 == 0) {
                            prompt.append(moveNum).append(". ");
                        }
                        prompt.append(convertToAlgebraic(moveHistory.get(i))).append(" ");
                    }
                }

                // Add specific instructions based on the selected master's style
                prompt.append("\n\n").append(getStyleInstructionsForMaster(selectedChessMaster));

                // Get response from OpenAI
                String assistantResponse = getPersonalizedAnalysis(prompt.toString());

                // Update UI on the main thread
                runOnUiThread(() -> {
                    // Display the analysis
                    analysisTextView.setText(assistantResponse);

                    // Process the analysis for visual highlighting
                    processAnalysisForHighlights(assistantResponse);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error getting AI analysis", e);
                runOnUiThread(() -> {
                    analysisTextView.setText("Analysis error: " + e.getMessage());
                });
            }
        }).start();
    }

    // Helper method to get the full name of the chess master
    private String getFullNameForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "botvinnik": return "Mikhail Botvinnik";
            case "kramnik": return "Vladimir Kramnik";
            case "fischer": return "Bobby Fischer";
            case "kasparov": return "Garry Kasparov";
            case "karpov": return "Anatoly Karpov";
            default: return "Chess Master";
        }
    }

    // Get specific instructions based on the master's style
    private String getStyleInstructionsForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "As Mikhail Tal, focus on tactical opportunities, sacrifices, and attacking chances. " +
                        "Look for creative combinations and dynamic piece play. Emphasize the beauty of sacrifices " +
                        "when they lead to tactical advantages. Your analysis should highlight tactical motifs " +
                        "like pins, forks, and discovered attacks. Speak with enthusiasm about tactical possibilities!";

            case "botvinnik":
                return "As Mikhail Botvinnik, analyze this position with scientific precision and methodical evaluation. " +
                        "Focus on long-term strategic considerations, pawn structure analysis, and positional advantages. " +
                        "Emphasize prophylactic thinking and thorough planning. Your analysis should be logical and systematic, " +
                        "discussing key squares, weak pawns, and piece coordination. Speak with measured, authoritative tone.";

            // Add more masters with their unique styles

            default:
                return "Provide a detailed analysis of this chess position. Discuss the strengths and weaknesses " +
                        "of both sides, potential plans, and concrete calculations where relevant.";
        }
    }

    // Add this method to process the analysis and highlight key squares
    private void processAnalysisForHighlights(String analysis) {
        // Clear any existing highlights on the board
        boardView.clearHighlights();

        // Extract chess squares mentioned in the analysis
        List<String> mentionedSquares = extractChessSquares(analysis);

        // Apply appropriate highlighting based on context
        for (String square : mentionedSquares) {
            // Determine color based on the context in the analysis
            int highlightColor;

            if (containsNearby(analysis, square, "weak", "vulnerability", "problem")) {
                // Red for weaknesses
                highlightColor = Color.parseColor("#FF6B6B");
            } else if (containsNearby(analysis, square, "strong", "advantage", "control")) {
                // Green for strengths
                highlightColor = Color.parseColor("#4CAF50");
            } else if (containsNearby(analysis, square, "key", "important", "critical")) {
                // Purple for key squares
                highlightColor = Color.parseColor("#9C27B0");
            } else {
                // Default blue for other mentioned squares
                highlightColor = Color.parseColor("#2196F3");
            }

            // Apply the highlight to the board
            boardView.highlightSquare(square, highlightColor, 10000); // 10 seconds duration
        }
    }

    // Helper method to extract chess squares from text
    private List<String> extractChessSquares(String text) {
        List<String> squares = new ArrayList<>();

        // Pattern for chess squares (a1-h8)
        Pattern pattern = Pattern.compile("\\b[a-h][1-8]\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            squares.add(matcher.group());
        }

        return squares;
    }

    // Helper to check if specific terms appear near a square in the text
    private boolean containsNearby(String text, String square, String... terms) {
        // Find the position of the square in the text
        int squarePos = text.indexOf(square);
        if (squarePos == -1) return false;

        // Check 50 characters before and after for the terms
        int start = Math.max(0, squarePos - 50);
        int end = Math.min(text.length(), squarePos + 50);
        String context = text.substring(start, end);

        // Check if any of the terms appear in this context
        for (String term : terms) {
            if (context.contains(term)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generate FEN positions for each move in the game
     */
    private void generatePositions() {
        Log.d(TAG, "Generating positions for " + moveHistory.size() + " moves");

        // Create array for all positions (initial + after each move)
        positions = new String[moveHistory.size() + 1];

        // Starting position is always the same
        positions[0] = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Log.d(TAG, "Position 0 (initial): " + positions[0]);

        try {
            // Reset engine to initial position
            engine.newGame();
            engine.setPositionFromMoves();

            // For each move, apply it and save resulting position
            for (int i = 0; i < moveHistory.size(); i++) {
                // Build the list of moves up to this point
                List<String> movesUpToNow = moveHistory.subList(0, i + 1);
                String[] movesArray = movesUpToNow.toArray(new String[0]);

                // Apply moves and get position
                engine.setPositionFromMoves(movesArray);
                positions[i + 1] = engine.getCurrentFEN();

                Log.d(TAG, "Position " + (i + 1) + " after move " + moveHistory.get(i) + ": " + positions[i + 1]);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating positions", e);
            Toast.makeText(this, "Error preparing positions: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update the board to a specific position in the game
     */
    @SuppressLint("SetTextI18n")
    private void updateToPosition(int index) {
        if (index < 0 || index > moveHistory.size()) {
            Log.e(TAG, "Invalid position index: " + index);
            return;
        }

        Log.d(TAG, "Updating to position at index " + index);
        currentMoveIndex = index;

        try {
            // Update the board with the FEN for this position
            String fen = positions[index];
            Log.d(TAG, "Setting board to FEN: " + fen);
            boardView.updateBoardFromFen(fen);

            // Update the move info
            if (index == 0) {
                moveInfoTextView.setText("Initial Position");
            } else {
                int moveNumber = (index + 1) / 2;
                boolean isWhiteMove = (index % 2 == 1);
                String moveText = String.format("Move %d%s: %s",
                        moveNumber,
                        isWhiteMove ? "" : "...",
                        convertToAlgebraic(moveHistory.get(index - 1)));
                moveInfoTextView.setText(moveText);
                Log.d(TAG, "Set move text to: " + moveText);
            }

            // Enable/disable navigation buttons
            prevButton.setEnabled(index > 0);
            nextButton.setEnabled(index < moveHistory.size());

            // Clear previous analysis
            analysisTextView.setText("");
        } catch (Exception e) {
            Log.e(TAG, "Error updating to position", e);
            Toast.makeText(this, "Error showing position: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show the previous move in the game
     */
    private void showPreviousMove() {
        if (currentMoveIndex > 0) {
            updateToPosition(currentMoveIndex - 1);
        }
    }

    /**
     * Show the next move in the game
     */
    private void showNextMove() {
        if (currentMoveIndex < moveHistory.size()) {
            updateToPosition(currentMoveIndex + 1);
        }
    }

    /**
     * Analyze the current position with Stockfish - Enhanced Version
     */
    // Update your analyzeCurrentPosition method with more detailed logging
    private void analyzeCurrentPosition() {
        Log.d(TAG, "🔍 analyzeCurrentPosition called - showing dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Analysis Type")
                .setItems(new String[]{"Engine Only", "AI Coach Analysis"}, (dialog, which) -> {
                    if (which == 0) {
                        // Use your existing engine-only analysis
                        Log.d(TAG, "🔍 User selected Engine Only analysis");
                        performEngineOnlyAnalysis();
                    } else {
                        // Use our new AI-enhanced analysis
                        Log.d(TAG, "🔍 User selected AI Coach analysis");
                        getAIEnhancedAnalysis();
                    }
                });
        builder.create().show();
    }

    // Rename your current analyzeCurrentPosition method to:
    private void performEngineOnlyAnalysis() {
        // Your existing engine analysis code here
    }

    /**
     * Convert UCI format to algebraic notation
     */
    private String convertToAlgebraic(String uciMove) {
        if (uciMove == null || uciMove.length() < 4) return "?";

        // Get source and destination squares
        char fromFile = uciMove.charAt(0);
        int fromRank = Character.getNumericValue(uciMove.charAt(1));
        char toFile = uciMove.charAt(2);
        int toRank = Character.getNumericValue(uciMove.charAt(3));

        // Simplified algebraic notation - just show the destination square for pawns
        // or piece letter + destination for other pieces
        try {
            // Get the piece type from the current board
            char piece = ' ';
            int fromRow = 8 - fromRank;
            int fromCol = fromFile - 'a';

            // Check if we can get the piece from the board view
            if (boardView != null) {
                piece = boardView.getPieceAt(fromRow, fromCol);
            }

            if (piece == 'p' || piece == 'P' || piece == ' ') {
                // For pawns or unknown pieces, just show the destination square
                return "" + toFile + toRank;
            } else {
                // For other pieces, show the piece letter + destination
                char pieceChar = ' ';
                switch (Character.toUpperCase(piece)) {
                    case 'R':
                        pieceChar = 'R';
                        break;
                    case 'N':
                        pieceChar = 'N';
                        break;
                    case 'B':
                        pieceChar = 'B';
                        break;
                    case 'Q':
                        pieceChar = 'Q';
                        break;
                    case 'K':
                        pieceChar = 'K';
                        break;
                    default:
                        return toFile + "" + toRank; // Default to just the destination
                }

                return pieceChar + "" + toFile + toRank;
            }
        } catch (Exception e) {
            // If anything goes wrong, just return the destination square
            Log.e(TAG, "Error converting to algebraic notation", e);
            return toFile + "" + toRank;
        }
    }

    /**
     * A simple helper method to wait for best move
     */
    private String waitForBestMove(int timeoutMs) throws IOException {
        long endTime = System.currentTimeMillis() + timeoutMs;
        List<String> buffer = new ArrayList<>();

        while (System.currentTimeMillis() < endTime) {
            // Get the current output buffer from the engine
            List<String> currentBuffer = engine.getOutputBuffer();
            for (String line : currentBuffer) {
                if (!buffer.contains(line)) {
                    buffer.add(line);

                    if (line.startsWith("bestmove")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 2) {
                            return parts[1];
                        }
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // If we timed out, try stopping the analysis
        engine.sendCommand("stop");
        return "none";
    }

    // Add these methods to GameAnalysisActivity.java (not OpenAIService)

    // Get analysis from OpenAI
    private String getPersonalizedAnalysis(String prompt) throws IOException {
        Log.d(TAG, "💬 Starting personalized analysis request");

        // Get API key with clear error message if missing
        String apiKey = ApiKeyConfig.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e(TAG, "❌ API key not configured");
            throw new IOException("API key not configured in settings");
        }

        // Use your unified OpenAI service with detailed logging
        OpenAIService openAIService = OpenAIService.getInstance();
        openAIService.setApiKey(apiKey);
        Log.d(TAG, "💬 API key set, unified service ready");

        // Format the system prompt for the selected master
        String systemPrompt = "You are " + getFullNameForMaster(selectedChessMaster) +
                ", a chess grandmaster analyzing a position.";
        Log.d(TAG, "💬 System prompt: " + systemPrompt);

        // Generate the response with careful error handling
        try {
            Log.d(TAG, "💬 Sending request to OpenAI");
            String response = openAIService.generateChatResponseSync(systemPrompt, prompt);
            Log.d(TAG, "💬 Received response: " + (response.length() > 100 ?
                    response.substring(0, 100) + "..." : response));
            return response;

        } catch (Exception e) {
            Log.e(TAG, "❌ OpenAI API error: " + e.getMessage(), e);
            // Rethrow with a more helpful message
            throw new IOException("Could not connect to AI service: " + e.getMessage());
        }
    }
    // Get detailed engine analysis
    private String getDetailedEngineAnalysis(int thinkTimeMs) throws IOException {
        StringBuilder analysis = new StringBuilder();

        // Clear previous output
        engine.sendCommand("stop");

        // Start analysis with multipv to get multiple lines
        engine.sendCommand("go depth 15 multipv 3 movetime " + thinkTimeMs);

        // Wait for completion
        try {
            Thread.sleep(thinkTimeMs + 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Stop analysis
        engine.sendCommand("stop");

        // Get the engine output
        List<String> outputBuffer = engine.getOutputBuffer();

        // Process the output to extract the main evaluation and lines
        float evaluation = 0;
        List<String> bestLines = new ArrayList<>();

        for (String line : outputBuffer) {
            if (line.contains("score cp ") && line.contains(" pv ")) {
                // Extract evaluation
                try {
                    int scoreIndex = line.indexOf("score cp ") + 9;
                    int endIndex = line.indexOf(" ", scoreIndex);
                    if (endIndex > scoreIndex) {
                        float score = Float.parseFloat(line.substring(scoreIndex, endIndex)) / 100.0f;

                        if (bestLines.size() == 0) {
                            evaluation = score; // Save the top evaluation
                        }

                        // Extract the move sequence
                        int pvIndex = line.indexOf(" pv ") + 4;
                        String moveSequence = line.substring(pvIndex);

                        // Convert moves to more readable form
                        String[] moves = moveSequence.split(" ");
                        StringBuilder prettyLine = new StringBuilder();
                        for (int i = 0; i < Math.min(moves.length, 5); i++) { // Show first 5 moves
                            prettyLine.append(convertToAlgebraic(moves[i])).append(" ");
                        }

                        // Add this line to our best lines with its evaluation
                        bestLines.add(String.format("Line %d (%.2f): %s",
                                bestLines.size() + 1, score, prettyLine.toString()));

                        if (bestLines.size() >= 3) break; // Keep top 3 lines
                    }
                } catch (Exception e) {
                    // Skip problematic lines
                }
            }
        }

        // Format the analysis output
        analysis.append(String.format("Evaluation: %.2f pawns", evaluation))
                .append(evaluation > 0 ? " (White advantage)" :
                        evaluation < 0 ? " (Black advantage)" : " (Equal)")
                .append("\n\n");

        analysis.append("Best lines:\n");
        for (String line : bestLines) {
            analysis.append(line).append("\n");
        }

        return analysis.toString();
    }

    private void loadSavedGame(long gameId) {
        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        GameDatabaseHelper.SavedGame savedGame = dbHelper.getGame(gameId);

        if (savedGame != null) {
            // Set the move history from the saved game
            moveHistory = new ArrayList<>(savedGame.getMoves());

            // Generate positions based on these moves
            generatePositions();

            // Update the display to show the initial position
            updateToPosition(0);

            Toast.makeText(this, "Loaded: " + savedGame.getDescription(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Could not load the saved game", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.stopEngine();
        }
    }
}