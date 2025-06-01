Chess Board Coordinate Calibration System

  Key Discovery Method:

  1. Enable Developer Options → "Pointer location" shows X,Y coordinates at top of screen
  2. Reference Points Mapping:
    - a1 (bottom-left): (163, 1993)
    - h8 (top-right): (1331, 827)
    - Board dimensions: 1168×1166 pixels
    - Each square: 167 pixels wide × 166 pixels tall

  Coordinate Formula:

  X = 163 + (file_number * 167)  // files: a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7
  Y = 1993 - (rank_number * 166) // ranks: 1=0, 2=1, 3=2, 4=3, 5=4, 6=5, 7=6, 8=7

  Example Coordinates:

  - e2 pawn: (831, 1823)
  - e4 square: (831, 1489)
  - c1 bishop: (497, 1993)
  - g5 square: (1163, 1323)

  Testing Process:

  1. Enable "Show taps" in Developer Options for visual feedback
  2. Test with simple moves like e2-e4
  3. Use pointer location overlay to verify exact tap positions
  4. Adjust coordinates if pieces don't select properly

  Critical Settings:

  - USB Debugging enabled
  - USB debugging (Security settings) enabled
  - Show taps enabled
  - Pointer location enabled

  This system achieved perfect center-square precision for automated chess gameplay! 🎯