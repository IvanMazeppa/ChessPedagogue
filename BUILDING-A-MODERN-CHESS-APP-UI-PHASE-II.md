● Chess Captured Pieces Logic - Critical Issues Documentation

  ⚠️ CRITICAL GUARDRAIL REQUIREMENT ⚠️

  THIS DOCUMENT MUST BE READ IN ITS ENTIRETY BEFORE ANY CODE CHANGES ARE MADE.

  - NO PARTIAL READING: Do not claim to have read this document if you have only read the first section or skipped parts
  - NO FALSE CLAIMS: Do not say "I've read the document" unless you have read every single section from start to finish
  - BRUTAL HONESTY REQUIRED: If you haven't read the full document, explicitly state what sections you have read
  - COMPLETION VERIFICATION: Reference specific details from the "Testing Requirements" and "Implementation Details" sections to      
  prove full reading
  - TECHNICAL ACCURACY OVER SPEED: This user pays £80/month for accuracy, not quick responses based on partial information

  Violation of this guardrail will result in incorrect implementations that waste development time.

  ---
  Overview

  This session focuses on fixing the captured pieces detection and display system in the Chess Pedagogue Android app. The system      
  is designed to show captured pieces in a compact format (e.g., "2x p") in containers below the chessboard, but multiple
  critical issues prevent it from working correctly.

  Current System Architecture

  Components Involved

  1. CapturedPiecesManager (/app/src/main/java/com/example/chesspedagogue/ui/CapturedPiecesManager.java)
  2. CompetitiveModeActivity - Contains capture detection logic
  3. Layout containers: blackCapturedCompactContainer and whiteCapturedCompactContainer in activity_competitive_mode_modern.xml       
  4. ChessBoardView - Provides FEN strings and board state

  Current Implementation Flow

  1. Move is made → FEN observer fires → Board updates with new position
  2. Animation observer fires → Calls detectCapturedPiece()                                                                           
  3. detectCapturedPiece() checks previousBoardFEN for piece at destination
  4. If piece found → Add to CapturedPiecesManager → Display in compact format

  Critical Issues Identified

  1. False Capture Detection

  Problem: The system incorrectly detects captures when pieces move to empty squares.

  Evidence from Log:
  05:40:51.360 🎬 Animation observer: Move from 7,3 to 6,3 (chess: d1 to d2)
  05:40:51.361 🎯 Capture detection: Found piece 'P' at 6,3 in previous position
  05:40:51.361 🎯 CAPTURE DETECTED! Piece 'P' captured at 6,3

  Analysis: The Queen moved from d1 to d2 (empty square), but the system thinks it captured a white pawn. This shows the
  previousBoardFEN contains incorrect data.

  2. Missing Real Captures

  Problem: Actual captures (like c8xf5 where bishop takes queen) are not being detected.

  Evidence: The log shows moves like c8f5 where the black bishop should capture the white queen on f5, but no capture detection       
  occurs. The move d3f5 places white queen on f5, then c8f5 should capture it, but no capture is logged.

  Root Cause: The capture detection is checking against the wrong board state.

  3. Stale FEN Data Issue

  Critical Problem: The capture detection system uses a stale "previous FEN" that appears to always be the starting position:

  🔍 Previous FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq...

  Analysis: This is the starting position FEN, not the actual previous move position. This explains why:
  - d1→d2 move finds a pawn at d2 (because starting position has pawn on d2)
  - Real captures aren't detected (because starting position doesn't have pieces on advanced squares)

  4. Container Placement Logic

  Problem: The user reports incorrect container assignment. The correct logic should be:
  - Left container (blackCapturedCompactContainer) → Shows WHITE pieces captured BY black player
  - Right container (whiteCapturedCompactContainer) → Shows BLACK pieces captured BY white player

  Current Implementation Check Needed: Verify if addCapturedPiece(pieceType, isWhitePiece) correctly routes to proper containers.     

  5. Observer Timing Issues

  Technical Problem: The FEN observer and animation observer have a race condition:

  // FEN observer fires first                                                                                                         
  gameViewModel.getCurrentFEN().observe(this, fen -> {
      previousBoardFEN = chessBoardView.getCurrentFEN(); // Wrong timing!                                                             
      chessBoardView.updateBoardFromFen(fen);
  });

  // Animation observer fires second                                                                                                  
  gameViewModel.getAnimateMoveEvent().observe(this, moveCoords -> {
      char capturedPiece = detectCapturedPiece(toRow, toCol); // Uses stale data                                                      
  });

  Issue: By the time the animation observer runs, chessBoardView.getCurrentFEN() already returns the NEW position, not the
  previous one.

  Technical Root Cause Analysis

  FEN Storage Logic Flaw

  The current implementation in setupObservers():

  // Store current board state as previous BEFORE updating                                                                            
  if (chessBoardView.getCurrentFEN() != null) {
      previousBoardFEN = chessBoardView.getCurrentFEN(); // WRONG!                                                                    
      Log.d(TAG, "🔍 Stored previous FEN for capture detection");
  }
  chessBoardView.updateBoardFromFen(fen);

  Problem: chessBoardView.getCurrentFEN() at this point returns the FEN that was set in the PREVIOUS call to this observer, which     
   could be many moves old or the starting position.

  Animation Observer Execution

  The animation observer correctly receives move coordinates but uses corrupted previous FEN data:

  🎬 Animation observer: Move from 7,3 to 6,3 (chess: d1 to d2)
  🔍 Capture detection: Checking destination 6,3 (chess: d2)
  🔍 Previous FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq...

  The previous FEN should show the position BEFORE the d1→d2 move, but instead shows the starting position.

  Proposed Solutions

  Solution 1: Fix FEN Storage Timing (Recommended)

  Store the FEN BEFORE the new one is applied:

  private String previousBoardFEN = null;

  gameViewModel.getCurrentFEN().observe(this, newFen -> {
      if (newFen != null && chessBoardView != null) {
          // Store CURRENT board FEN as previous BEFORE updating                                                                      
          if (chessBoardView.getCurrentFEN() != null) {
              String currentFEN = chessBoardView.getCurrentFEN();
              if (!currentFEN.equals(newFen)) {
                  previousBoardFEN = currentFEN;
                  Log.d(TAG, "🔍 Stored previous FEN: " + currentFEN.substring(0, 20) + "...");
              }
          }

          chessBoardView.updateBoardFromFen(newFen);
          Log.d(TAG, "🎯 Board updated with FEN: " + newFen);
      }
  });

  Solution 2: Move History Tracking (Alternative)

  Track actual moves and detect captures from move history:

  private String lastMoveMade = null;

  // In move history observer                                                                                                         
  gameViewModel.getMoveHistory().observe(this, moveHistory -> {
      if (moveHistory != null && !moveHistory.isEmpty()) {
          String latestMove = moveHistory.get(moveHistory.size() - 1);
          if (latestMove.contains("x")) {
              // This is a capture move                                                                                               
              handleCaptureFromNotation(latestMove);
          }
          lastMoveMade = latestMove;
      }
  });

  Solution 3: Direct Board State Comparison

  Compare piece counts between board states:

  private void detectCapturesByComparison(String oldFEN, String newFEN) {
      Map<Character, Integer> oldPieces = countPiecesInFEN(oldFEN);
      Map<Character, Integer> newPieces = countPiecesInFEN(newFEN);

      for (char piece : oldPieces.keySet()) {
          int oldCount = oldPieces.get(piece);
          int newCount = newPieces.getOrDefault(piece, 0);
          if (newCount < oldCount) {
              // This piece type was captured                                                                                         
              addCapturedPieceFromChar(piece);
          }
      }
  }

  Implementation Details

  Files to Modify

  1. CompetitiveModeActivity.java:
    - Fix setupObservers() method
    - Fix detectCapturedPiece() method
    - Add proper FEN storage logic
  2. CapturedPiecesManager.java:
    - Verify container assignment logic
    - Ensure correct piece routing to left/right containers

  Container Assignment Verification

  Check that the containers are correctly assigned:
  - blackCapturedCompactContainer (LEFT) should receive WHITE pieces
  - whiteCapturedCompactContainer (RIGHT) should receive BLACK pieces

  Format Requirements

  - Single piece: just piece icon (♔, ♕, ♖, etc.)
  - Multiple pieces: "2x ♔" format
  - Start with empty containers
  - Update dynamically as captures occur

  Testing Requirements

  Test Scenarios

  1. Normal Move Test:
    - Move: Qd1→d2 (empty square)
    - Expected: No capture detected
    - Current Issue: False positive detection
  2. Real Capture Test:
    - Setup: White Queen on f5, Black Bishop on c8
    - Move: Bc8xf5 (bishop captures queen)
    - Expected: White queen added to LEFT container (captured by black)
    - Current Issue: No capture detected
  3. Multiple Captures Test:
    - Capture 2 white pawns
    - Expected: "2x ♙" in LEFT container
    - Verify format correctness
  4. Promotion Scenario:
    - Promote pawn to queen, then capture original queen
    - Expected: Both queens can be captured and counted
    - Test "2x ♕" display

  Verification Steps

  1. Start new game → Containers should be empty
  2. Make normal moves → No false captures
  3. Make real capture → Piece appears in correct container
  4. Multiple captures → Count displays correctly
  5. Container assignment → Left=white pieces, Right=black pieces

  Debug Logging Requirements

  Add comprehensive logging to track:
  - FEN storage timing: "Stored previous FEN: [first 50 chars]"
  - Capture detection calls: "Checking destination [coords] in FEN [first 50 chars]"
  - Container routing: "Adding [piece] to [container] side"

  Current Status

  - False positives: System detects captures on normal moves
  - Missing captures: Real captures not being detected
  - Stale data: Previous FEN always shows starting position
  - Container logic: Needs verification for left/right assignment
  - Root cause: Observer timing and FEN storage logic fundamentally broken

  Next Implementation Steps

  1. Immediate Fix: Correct FEN storage timing in setupObservers()                                                                    
  2. Add Debugging: Comprehensive logging for move tracking
  3. Test Scenarios: Verify with real capture moves
  4. Container Logic: Confirm left/right assignment is correct
  5. Format Verification: Test "2x" display for multiple pieces
  6. Edge Cases: Test promotion scenarios and multiple queens

  Risk Assessment

  - High Risk: Current implementation completely unreliable
  - User Impact: Core feature non-functional
  - Development Time: Requires careful testing of observer timing
  - Regression Risk: Changes affect move animation system

  Success Criteria

  - ✅ No false capture detection on normal moves
  - ✅ All real captures properly detected and displayed
  - ✅ Correct container assignment (left/right)
  - ✅ Proper "2x" format for multiple pieces
  - ✅ Clean debug logs showing correct FEN tracking
  - ✅ No impact on existing move animation system

  ---
  END OF DOCUMENT - Confirm you have read all sections including Testing Requirements and Implementation Details before 
  proceeding with any code changes.