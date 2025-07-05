package com.example.chesspedagogue.ui;

import android.util.Log;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Visual Effects Debugging Helper
 * Provides detailed logging and monitoring for capture effects testing
 */
public class VisualEffectsDebugger {
    
    private static final String TAG = "VisualEffectsDebug";
    private static long lastCaptureTime = 0;
    private static int captureCount = 0;
    
    /**
     * Log capture effect with detailed information
     */
    public static void logCaptureEffect(String capturedPieceType, boolean isWhitePiece, 
                                       float x, float y, boolean agslEnabled) {
        captureCount++;
        long currentTime = System.currentTimeMillis();
        
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        
        Log.i(TAG, "🎆🎆🎆 CAPTURE EFFECT #" + captureCount + " 🎆🎆🎆");
        Log.i(TAG, "⏰ Time: " + timestamp);
        Log.i(TAG, "📍 Position: (" + x + ", " + y + ")");
        Log.i(TAG, "♟️ Piece: " + capturedPieceType + " (" + (isWhitePiece ? "White" : "Black") + ")");
        Log.i(TAG, "🎨 AGSL Shaders: " + (agslEnabled ? "ENABLED ✅" : "FALLBACK ⚠️"));
        Log.i(TAG, "📱 Device: " + Build.MODEL + " (API " + Build.VERSION.SDK_INT + ")");
        
        if (lastCaptureTime > 0) {
            long timeBetween = currentTime - lastCaptureTime;
            Log.i(TAG, "⏱️ Time since last capture: " + timeBetween + "ms");
        }
        
        lastCaptureTime = currentTime;
        
        // Device performance info
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        Log.i(TAG, "💾 Memory: " + (memoryUsed / 1024 / 1024) + "MB used");
        
        Log.i(TAG, "🎆🎆🎆 END CAPTURE EFFECT #" + captureCount + " 🎆🎆🎆");
        Log.i(TAG, " "); // Empty line for readability
    }
    
    /**
     * Log movement trail effect
     */
    public static void logMovementTrail(String pieceType, boolean isWhitePiece, 
                                       float startX, float startY, float endX, float endY) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        
        Log.i(TAG, "✨ MOVEMENT TRAIL: " + pieceType + " (" + (isWhitePiece ? "White" : "Black") + ")");
        Log.i(TAG, "⏰ Time: " + timestamp);
        Log.i(TAG, "🔄 Path: (" + startX + ", " + startY + ") → (" + endX + ", " + endY + ")");
        
        // Calculate distance for performance analysis
        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        Log.i(TAG, "📏 Distance: " + String.format("%.1f", distance) + "px");
    }
    
    /**
     * Log AGSL shader test results (DEPRECATED - Use AGSLManager instead)
     */
    @Deprecated
    public static void logAGSLTest(boolean supported, String errorMessage) {
        // This method is deprecated. AGSLManager now handles all AGSL testing and logging
        Log.d(TAG, "⚠️ Deprecated AGSL test called - Use AGSLManager.getInstance().isSupported() instead");
    }
    
    /**
     * Log particle system performance
     */
    public static void logParticlePerformance(int particleCount, long animationDuration, String pieceType) {
        Log.i(TAG, "🎇 PARTICLE SYSTEM PERFORMANCE");
        Log.i(TAG, "♟️ Piece Type: " + pieceType);
        Log.i(TAG, "🎆 Particle Count: " + particleCount);
        Log.i(TAG, "⏱️ Animation Duration: " + animationDuration + "ms");
        Log.i(TAG, "📊 Particles/second: " + String.format("%.1f", (float)particleCount / animationDuration * 1000));
    }
    
    /**
     * Test capture effects with detailed logging
     */
    public static void testCaptureEffects() {
        Log.i(TAG, "🧪 STARTING CAPTURE EFFECTS TEST SEQUENCE 🧪");
        Log.i(TAG, "📱 Device Info:");
        Log.i(TAG, "   Model: " + Build.MODEL);
        Log.i(TAG, "   Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        Log.i(TAG, "   Manufacturer: " + Build.MANUFACTURER);
        
        Log.i(TAG, "🎯 Test Sequence:");
        Log.i(TAG, "   1. Watch for capture effects when pieces are taken");
        Log.i(TAG, "   2. Look for particle explosions with piece-specific colors");
        Log.i(TAG, "   3. Check for movement trails during piece moves");
        Log.i(TAG, "   4. Verify AGSL shader effects (if supported)");
        
        Log.i(TAG, "📋 Expected Particle Counts:");
        Log.i(TAG, "   Queen: 25 particles, Rook: 20, Bishop: 18, Knight: 16, Pawn: 12");
        
        Log.i(TAG, "🎨 Expected Colors:");
        Log.i(TAG, "   White Queen: Golden (1.0, 0.9, 0.2)");
        Log.i(TAG, "   Black Queen: Deep Red (0.8, 0.2, 0.2)");
        Log.i(TAG, "   White Pieces: Light blue tints");
        Log.i(TAG, "   Black Pieces: Warm/dark tints");
        
        Log.i(TAG, "🧪 END TEST SEQUENCE SETUP 🧪");
        Log.i(TAG, " ");
    }
    
    /**
     * Get capture statistics
     */
    public static void logCaptureStats() {
        Log.i(TAG, "📊 CAPTURE EFFECTS STATISTICS");
        Log.i(TAG, "🎆 Total Captures: " + captureCount);
        
        if (captureCount > 0) {
            Log.i(TAG, "⏱️ Last Capture: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(lastCaptureTime)));
        }
        
        Log.i(TAG, "📊 END STATISTICS");
    }
}