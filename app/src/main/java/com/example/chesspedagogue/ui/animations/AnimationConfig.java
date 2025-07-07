package com.example.chesspedagogue.ui.animations;

public class AnimationConfig {
    
    // Timing Constants
    public static final long PUZZLE_SOLVE_DURATION = 800L; // Base duration per square
    public static final long PUZZLE_STAGGER_DELAY = 40L;   // Delay between squares
    public static final long GLASS_FADE_DURATION = 2000L;  // Glass overlay fade
    public static final long PANEL_ASSEMBLY_DURATION = 600L; // Panel slide-hover-drop
    
    // Assembly Sequence Delays (3-phase system)
    public static final int HEADER_DELAY = 300;
    public static final int CAPTURED_DELAY = 600;
    public static final int MOVELIST_DELAY = 900;
    public static final int CONTROLS_DELAY = 1200;
    public static final int CHESSBOARD_DELAY = 1500; // Last element
    
    // Animation Distances
    public static final float PANEL_SLIDE_DISTANCE = 800f;  // Panel off-screen distance
    public static final float HOVER_OFFSET = 24f;           // Hover height above target
    public static final float PUZZLE_SCATTER_RADIUS = 600f; // Square scatter distance
    
    // Visual Effects
    public static final float GLASS_OPACITY = 0.18f;        // 18% glassmorphism
    public static final float GLASS_BLUR_RADIUS = 20f;      // 20px blur
    public static final float ELEVATION_HIGH = 24f;         // High elevation panels
    public static final float ELEVATION_MID = 12f;          // Mid elevation panels
    public static final float CORNER_RADIUS = 24f;          // Panel corner radius
    
    // Performance Settings
    public static final boolean HARDWARE_ACCELERATION = true;
    public static final boolean REDUCED_ANIMATIONS_MODE = false; // For older devices
    
    // Chess Colors
    public static final int LIGHT_SQUARE_COLOR = 0xFFF0D9B5;
    public static final int DARK_SQUARE_COLOR = 0xFFB58863;
    
    // Electrical Flash Effects (future enhancement)
    public static final int FLASH_COLOR_PRIMARY = 0xFFFFEB3B;   // Yellow
    public static final int FLASH_COLOR_SECONDARY = 0xFFFFFFFF; // White
    public static final long FLASH_DURATION = 150L;
    
    private AnimationConfig() {
        // Utility class - no instantiation
    }
}