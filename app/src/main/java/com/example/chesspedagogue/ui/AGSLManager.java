package com.example.chesspedagogue.ui;

import android.graphics.RuntimeShader;
import android.os.Build;
import android.util.Log;

/**
 * Unified AGSL Manager - Single source of truth for Android Graphics Shading Language support
 * Prevents duplicate testing and logging spam while providing optimal shader effects
 */
public class AGSLManager {
    
    private static final String TAG = "AGSLManager";
    private static AGSLManager instance;
    
    // Single test state tracking
    private boolean tested = false;
    private boolean supported = false;
    private String failureReason = null;
    
    private AGSLManager() {
        // Private constructor for singleton
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized AGSLManager getInstance() {
        if (instance == null) {
            instance = new AGSLManager();
        }
        return instance;
    }
    
    /**
     * Check AGSL support with single test and logging
     */
    public boolean isSupported() {
        if (tested) {
            return supported;
        }
        
        Log.d(TAG, "🧪 Testing AGSL support...");
        
        // Check API level first
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            supported = false;
            failureReason = "API level " + Build.VERSION.SDK_INT + " < 33 (Android 13)";
            tested = true;
            logResult();
            return false;
        }
        
        // Test basic AGSL functionality
        try {
            String testShader = "half4 main(float2 coord) { return half4(1.0, 0.0, 0.0, 1.0); }";
            RuntimeShader shader = new RuntimeShader(testShader);
            supported = true;
            tested = true;
            logResult();
            return true;
        } catch (Exception e) {
            supported = false;
            failureReason = e.getMessage();
            tested = true;
            logResult();
            return false;
        }
    }
    
    /**
     * Get device information for effects optimization
     */
    public String getDeviceInfo() {
        return Build.MODEL + " (Android " + Build.VERSION.RELEASE + ", API " + Build.VERSION.SDK_INT + ")";
    }
    
    /**
     * Get failure reason if AGSL is not supported
     */
    public String getFailureReason() {
        return failureReason;
    }
    
    /**
     * Log test result (only once)
     */
    private void logResult() {
        Log.i(TAG, "🎨 AGSL Support Test Complete 🎨");
        Log.i(TAG, "📱 Device: " + getDeviceInfo());
        
        if (supported) {
            Log.i(TAG, "✅ AGSL SUPPORTED - Hardware-accelerated shaders enabled!");
            Log.i(TAG, "🚀 Ready for spectacular visual effects");
        } else {
            Log.i(TAG, "❌ AGSL NOT SUPPORTED - Using fallback particle effects");
            Log.i(TAG, "⚠️ Reason: " + failureReason);
            Log.i(TAG, "🔄 Visual effects will use standard Android animations");
        }
        
        Log.i(TAG, "🎨 End AGSL Test 🎨");
    }
    
    /**
     * Create a RuntimeShader safely (only if supported)
     */
    public RuntimeShader createShader(String shaderCode) {
        if (!isSupported()) {
            Log.w(TAG, "Cannot create shader - AGSL not supported");
            return null;
        }
        
        try {
            return new RuntimeShader(shaderCode);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create shader: " + e.getMessage());
            return null;
        }
    }
}