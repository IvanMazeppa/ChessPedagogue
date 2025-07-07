package com.example.chesspedagogue;

import android.util.Log;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * 🧩 Lichess Puzzle Format - Professional Chess Tactics
 * 
 * Based on the real Lichess database format with 4.9M+ verified puzzles:
 * - Positions are from actual games analyzed by Stockfish NNUE
 * - Solutions are "only moves" - any other move worsens the position
 * - Themes are algorithmically tagged and verified by player votes
 * - Ratings use Glicko-2 system treating each solve as a rated game
 */
public class LichessPuzzle {
    public final String puzzleId;
    public final String fen;              // Position BEFORE opponent's move
    public final String moves;            // UCI format: first move = opponent, rest = solution
    public final int rating;
    public final int ratingDeviation;
    public final int popularity;          // -100 to 100 based on player votes
    public final int nbPlays;
    public final List<String> themes;
    public final String gameUrl;
    public final List<String> openingTags;
    
    // Parsed move data
    public final String opponentMove;     // First move (what opponent just played)
    public final List<String> solutionMoves; // Remaining moves (the solution)
    public final String initialPosition;  // FEN after opponent's move (what player sees)
    
    public LichessPuzzle(String puzzleId, String fen, String moves, int rating, 
                        int ratingDeviation, int popularity, int nbPlays, 
                        String themes, String gameUrl, String openingTags) {
        this.puzzleId = puzzleId;
        this.fen = fen;
        this.moves = moves;
        this.rating = rating;
        this.ratingDeviation = ratingDeviation;
        this.popularity = popularity;
        this.nbPlays = nbPlays;
        this.themes = parseThemes(themes);
        this.gameUrl = gameUrl;
        this.openingTags = parseOpeningTags(openingTags);
        
        // Parse moves: first move is opponent's, rest are solution
        String[] moveArray = moves.split(" ");
        if (moveArray.length > 0) {
            this.opponentMove = moveArray[0];
            this.solutionMoves = new ArrayList<>();
            for (int i = 1; i < moveArray.length; i++) {
                this.solutionMoves.add(moveArray[i]);
            }
        } else {
            this.opponentMove = "";
            this.solutionMoves = new ArrayList<>();
        }
        
        // Calculate initial position (FEN after opponent's move)
        this.initialPosition = applyMoveToFen(fen, opponentMove);
    }
    
    /**
     * 🎯 Get the first move in the solution (what player should play)
     */
    public String getFirstSolutionMove() {
        return solutionMoves.isEmpty() ? "" : solutionMoves.get(0);
    }
    
    /**
     * 🎯 Get solution move at specific index
     */
    public String getSolutionMove(int index) {
        return (index >= 0 && index < solutionMoves.size()) ? solutionMoves.get(index) : "";
    }
    
    /**
     * 📊 Get total number of moves in solution
     */
    public int getSolutionLength() {
        return solutionMoves.size();
    }
    
    /**
     * ✅ Check if solution is complete (all moves played)
     */
    public boolean isSolutionComplete(int moveIndex) {
        return moveIndex >= solutionMoves.size();
    }
    
    /**
     * ✅ Check if a move is the correct move at the given index in the solution
     */
    public boolean isCorrectMove(String move, int moveIndex) {
        if (solutionMoves.isEmpty() || moveIndex >= solutionMoves.size()) return false;
        
        String expectedMove = solutionMoves.get(moveIndex);
        Log.d("LichessPuzzle", String.format("🔍 Checking move %d: '%s' vs solution: '%s'", moveIndex, move, expectedMove));
        
        // Direct comparison first (for UCI input)
        if (move.equals(expectedMove)) {
            Log.d("LichessPuzzle", "✅ Direct match found");
            return true;
        }
        
        // Convert user's algebraic move to UCI for comparison
        String userMoveAsUci = algebraicToUci(move, initialPosition);
        Log.d("LichessPuzzle", String.format("🔄 User move '%s' → UCI: '%s'", move, userMoveAsUci));
        if (userMoveAsUci != null && userMoveAsUci.equals(expectedMove)) {
            Log.d("LichessPuzzle", "✅ Algebraic→UCI match found");
            return true;
        }
        
        // Convert UCI solution to algebraic for comparison
        String solutionAsAlgebraic = uciToAlgebraic(expectedMove, initialPosition);
        Log.d("LichessPuzzle", String.format("🔄 Solution '%s' → Algebraic: '%s'", expectedMove, solutionAsAlgebraic));
        if (solutionAsAlgebraic != null && solutionAsAlgebraic.equals(move)) {
            Log.d("LichessPuzzle", "✅ UCI→Algebraic match found");
            return true;
        }
        
        Log.d("LichessPuzzle", "❌ No notation matches found");
        return false;
    }
    
    /**
     * ✅ Check if a move is the correct first solution move (legacy method)
     */
    public boolean isCorrectFirstMove(String move) {
        if (solutionMoves.isEmpty()) return false;
        
        String firstSolution = solutionMoves.get(0);
        Log.d("LichessPuzzle", String.format("🔍 Checking move: '%s' vs solution: '%s'", move, firstSolution));
        
        // Direct comparison first (for UCI input)
        if (move.equals(firstSolution)) {
            Log.d("LichessPuzzle", "✅ Direct match found");
            return true;
        }
        
        // Convert user's algebraic move to UCI for comparison
        String userMoveAsUci = algebraicToUci(move, initialPosition);
        Log.d("LichessPuzzle", String.format("🔄 User move '%s' → UCI: '%s'", move, userMoveAsUci));
        if (userMoveAsUci != null && userMoveAsUci.equals(firstSolution)) {
            Log.d("LichessPuzzle", "✅ Algebraic→UCI match found");
            return true;
        }
        
        // Convert UCI solution to algebraic for comparison
        String solutionAsAlgebraic = uciToAlgebraic(firstSolution, initialPosition);
        Log.d("LichessPuzzle", String.format("🔄 Solution '%s' → Algebraic: '%s'", firstSolution, solutionAsAlgebraic));
        if (solutionAsAlgebraic != null && solutionAsAlgebraic.equals(move)) {
            Log.d("LichessPuzzle", "✅ UCI→Algebraic match found");
            return true;
        }
        
        Log.d("LichessPuzzle", "❌ No notation matches found");
        return false;
    }
    
    /**
     * 📈 Get difficulty description based on rating
     */
    public String getDifficultyDescription() {
        if (rating < 1000) return "Beginner";
        if (rating < 1300) return "Easy";
        if (rating < 1600) return "Medium";
        if (rating < 1900) return "Hard";
        if (rating < 2200) return "Expert";
        return "Master";
    }
    
    /**
     * 🏷️ Get primary theme
     */
    public String getPrimaryTheme() {
        return themes.isEmpty() ? "tactical" : themes.get(0);
    }
    
    /**
     * 💡 Get themed hint based on puzzle themes
     */
    public String getHint() {
        String primaryTheme = getPrimaryTheme();
        
        switch (primaryTheme.toLowerCase()) {
            case "mate":
            case "matein1":
            case "matein2":
                return "Look for checkmate! Force the enemy king into a corner.";
            case "fork":
            case "royalfork":
                return "Find a move that attacks two pieces at once.";
            case "pin":
                return "Look for a piece that cannot move without exposing a more valuable piece.";
            case "skewer":
                return "Attack a valuable piece that will expose a less valuable piece behind it.";
            case "discoveredattack":
                return "Move a piece to reveal an attack from another piece.";
            case "deflection":
                return "Force the defender away from protecting an important square or piece.";
            case "sacrifice":
                return "Consider giving up material for a bigger advantage.";
            case "backrank":
                return "Look for back rank weaknesses and mating patterns.";
            case "endgame":
                return "Use king activity and pawn promotion threats.";
            case "zugzwang":
                return "Put your opponent in a position where any move worsens their position.";
            case "promotion":
            case "underpromotion":
                return "Consider pawn promotion, possibly to a piece other than queen.";
            default:
                return "Look for the best tactical move in this position.";
        }
    }
    
    /**
     * 🎮 Get display title for UI
     */
    public String getDisplayTitle() {
        return String.format("%s (%d) - %s", 
            getPrimaryTheme().toUpperCase(), rating, getDifficultyDescription());
    }
    
    /**
     * 📊 Get puzzle statistics summary
     */
    public String getStatsDescription() {
        double playRate = nbPlays > 0 ? Math.min(100.0, nbPlays / 100.0) : 0;
        return String.format("Rating: %d | Popularity: %+d | Played: %.0f%%", 
            rating, popularity, playRate);
    }
    
    // Helper methods
    
    private List<String> parseThemes(String themesStr) {
        if (themesStr == null || themesStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(themesStr.split(" "));
    }
    
    private List<String> parseOpeningTags(String openingStr) {
        if (openingStr == null || openingStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(openingStr.split(" "));
    }
    
    /**
     * 🔄 Apply a UCI move to a FEN position (basic implementation)
     * For puzzles, we'll use a simplified approach to get the correct starting position
     */
    private String applyMoveToFen(String fen, String uciMove) {
        if (uciMove == null || uciMove.isEmpty()) {
            return fen;
        }
        
        try {
            // Parse FEN components
            String[] fenParts = fen.split(" ");
            if (fenParts.length < 2) return fen;
            
            String position = fenParts[0];
            String activeColor = fenParts[1];
            
            // Parse UCI move (e.g., "e2e4", "g1f3")
            if (uciMove.length() < 4) return fen;
            
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
            
            // Toggle active color and update FEN
            String newActiveColor = "w".equals(activeColor) ? "b" : "w";
            
            // Rebuild FEN (simplified - keep other parts unchanged for puzzles)
            return newPosition + " " + newActiveColor + 
                   (fenParts.length > 2 ? " " + String.join(" ", java.util.Arrays.copyOfRange(fenParts, 2, fenParts.length)) : "");
                   
        } catch (Exception e) {
            Log.e("LichessPuzzle", "Error applying UCI move: " + uciMove + " to FEN: " + fen, e);
            return fen; // Return original if move application fails
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
     * 🔢 Convert UCI notation to algebraic notation using position
     */
    private String uciToAlgebraic(String uciMove, String fen) {
        if (uciMove == null || uciMove.length() < 4 || fen == null) return null;
        
        try {
            String from = uciMove.substring(0, 2);
            String to = uciMove.substring(2, 4);
            
            // Get piece at source square
            char piece = getPieceAtSquare(fen, from);
            if (piece == ' ') return null;
            
            // Check if destination has a piece (capture)
            char capturedPiece = getPieceAtSquare(fen, to);
            boolean isCapture = capturedPiece != ' ';
            
            // Convert to algebraic notation
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
            Log.e("LichessPuzzle", "Error converting UCI to algebraic: " + uciMove, e);
            return null;
        }
    }
    
    /**
     * 🔢 Convert algebraic notation to UCI notation using position
     */
    private String algebraicToUci(String algebraicMove, String fen) {
        if (algebraicMove == null || fen == null) return null;
        
        try {
            // Parse the algebraic move
            String cleanMove = algebraicMove.replaceAll("[+#]", ""); // Remove check/mate symbols
            boolean isCapture = cleanMove.contains("x");
            cleanMove = cleanMove.replace("x", "");
            
            // Determine piece type and destination
            char pieceType = 'P'; // Default to pawn
            String destination = "";
            
            if (cleanMove.length() >= 2) {
                // Check if first character is a piece
                char firstChar = cleanMove.charAt(0);
                if ("KQRBN".indexOf(firstChar) >= 0) {
                    pieceType = firstChar;
                    destination = cleanMove.substring(1);
                } else {
                    // Pawn move
                    destination = cleanMove;
                }
            }
            
            if (destination.length() < 2) return null;
            
            // Find the source square by looking for the piece that can move to destination
            String sourceSquare = findSourceSquare(fen, pieceType, destination, isCapture);
            if (sourceSquare == null) return null;
            
            return sourceSquare + destination;
            
        } catch (Exception e) {
            Log.e("LichessPuzzle", "Error converting algebraic to UCI: " + algebraicMove, e);
            return null;
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
            Log.e("LichessPuzzle", "Error getting piece at square: " + square, e);
        }
        return ' ';
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
            Log.e("LichessPuzzle", "Error finding source square", e);
        }
        return null;
    }
    
    /**
     * 🎯 Check if piece can move to destination (simplified)
     */
    private boolean canPieceMoveTo(char[][] board, int fromRank, int fromFile, String destination, char pieceType) {
        if (destination.length() != 2) return false;
        
        int toFile = destination.charAt(0) - 'a';
        int toRank = 8 - (destination.charAt(1) - '0');
        
        if (toRank < 0 || toRank >= 8 || toFile < 0 || toFile >= 8) return false;
        
        // For kings, check if it's a valid king move (1 square in any direction)
        if (Character.toUpperCase(pieceType) == 'K') {
            int rankDiff = Math.abs(toRank - fromRank);
            int fileDiff = Math.abs(toFile - fromFile);
            return rankDiff <= 1 && fileDiff <= 1 && (rankDiff > 0 || fileDiff > 0);
        }
        
        // Add more piece-specific logic as needed
        // For now, assume the move is valid (puzzles should have valid moves)
        return true;
    }
    
    /**
     * 📋 Create from CSV line
     */
    public static LichessPuzzle fromCsvLine(String csvLine) {
        try {
            // Split CSV line, handling quoted fields
            String[] fields = parseCsvLine(csvLine);
            
            if (fields.length >= 10) {
                return new LichessPuzzle(
                    fields[0].trim(), // puzzleId
                    fields[1].trim(), // fen
                    fields[2].trim(), // moves
                    Integer.parseInt(fields[3].trim()), // rating
                    Integer.parseInt(fields[4].trim()), // ratingDeviation
                    Integer.parseInt(fields[5].trim()), // popularity
                    Integer.parseInt(fields[6].trim()), // nbPlays
                    fields[7].trim(), // themes
                    fields[8].trim(), // gameUrl
                    fields[9].trim()  // openingTags
                );
            }
        } catch (Exception e) {
            android.util.Log.e("LichessPuzzle", "❌ Failed to parse CSV line: " + csvLine, e);
        }
        return null;
    }
    
    /**
     * 📝 Parse CSV line handling quoted fields
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        
        // Add the last field
        fields.add(field.toString());
        
        return fields.toArray(new String[0]);
    }
    
    @Override
    public String toString() {
        return String.format("LichessPuzzle[%s] %s (%d) - %s", 
            puzzleId, getPrimaryTheme(), rating, gameUrl);
    }
}