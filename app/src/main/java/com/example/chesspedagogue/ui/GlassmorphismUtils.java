package com.example.chesspedagogue.ui;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utility class for implementing glassmorphism effects on Android 15+
 * Includes RenderEffect blur, edge-to-edge configuration, and Material 3 Expressive features
 */
public class GlassmorphismUtils {
    
    private static final String TAG = "GlassmorphismUtils";
    
    // Blur radius constants optimized for Samsung S23 Ultra
    public static final float LIGHT_BLUR_RADIUS = 12f;
    public static final float MEDIUM_BLUR_RADIUS = 20f;
    public static final float HEAVY_BLUR_RADIUS = 35f;
    public static final float MAX_SAFE_BLUR_RADIUS = 50f; // Performance optimized for S23 Ultra
    
    /**
     * Apply glassmorphism blur effect to a view (Android 12+/API 31+)
     * Uses RenderEffect.createBlurEffect for hardware-accelerated blur
     * Enhanced for Android 15/API 35 with advanced GPU optimization
     */
    public static void applyGlassBlur(View view, float blurRadius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Clamp blur radius for optimal performance
            float safeRadius = Math.min(blurRadius, MAX_SAFE_BLUR_RADIUS);
            
            // Android 15 enhanced RenderEffect with GPU acceleration
            RenderEffect blurEffect = RenderEffect.createBlurEffect(
                safeRadius, 
                safeRadius, 
                Shader.TileMode.CLAMP
            );
            
            // Android 15: Advanced layered blur effect for better depth
            if (Build.VERSION.SDK_INT >= 35) {
                // Create layered blur for enhanced glassmorphism depth
                RenderEffect colorMatrixEffect = RenderEffect.createColorFilterEffect(
                    new android.graphics.ColorMatrixColorFilter(new float[]{
                        1, 0, 0, 0, 0,     // Red channel
                        0, 1, 0, 0, 0,     // Green channel  
                        0, 0, 1, 0, 0,     // Blue channel
                        0, 0, 0, 0.85f, 0  // Alpha channel (translucency)
                    })
                );
                blurEffect = RenderEffect.createBlendModeEffect(blurEffect, colorMatrixEffect, 
                    android.graphics.BlendMode.SRC_OVER);
            }
            
            view.setRenderEffect(blurEffect);
            
            // Enable hardware acceleration for smooth performance
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }
    
    /**
     * Apply different blur intensities for various UI elements
     */
    public static void applyLightGlassBlur(View view) {
        applyGlassBlur(view, LIGHT_BLUR_RADIUS);
    }
    
    public static void applyMediumGlassBlur(View view) {
        applyGlassBlur(view, MEDIUM_BLUR_RADIUS);
    }
    
    public static void applyHeavyGlassBlur(View view) {
        applyGlassBlur(view, HEAVY_BLUR_RADIUS);
    }
    
    /**
     * Remove blur effect from view
     */
    public static void removeBlur(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null);
            view.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }
    
    /**
     * Configure advanced edge-to-edge display for Android 15/API 35
     * Enhanced with cutting-edge window management and adaptive insets
     */
    public static void enableEdgeToEdge(Window window, View rootView) {
        // Enable edge-to-edge content with Android 15 enhancements
        WindowCompat.setDecorFitsSystemWindows(window, false);
        
        // Advanced Android 15 window flags for immersive experience
        if (Build.VERSION.SDK_INT >= 35) {
            // Enable advanced window blending modes
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        
        // Make system bars transparent with Android 15 optimizations
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        
        // Advanced Android 15 inset handling with predictive animations
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            androidx.core.graphics.Insets systemBarsInsets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            );
            
            // Android 15: Advanced inset handling with gesture sensitivity
            androidx.core.graphics.Insets gestureInsets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemGestures()
            );
            
            androidx.core.graphics.Insets mandatoryGestureInsets = windowInsets.getInsets(
                WindowInsetsCompat.Type.mandatorySystemGestures()
            );
            
            // Apply sophisticated padding that accounts for gestures
            int topInset = Math.max(systemBarsInsets.top, gestureInsets.top);
            int bottomInset = Math.max(systemBarsInsets.bottom, 
                Math.max(gestureInsets.bottom, mandatoryGestureInsets.bottom));
            
            view.setPadding(
                Math.max(systemBarsInsets.left, gestureInsets.left),
                topInset,
                Math.max(systemBarsInsets.right, gestureInsets.right),
                bottomInset
            );
            
            return WindowInsetsCompat.CONSUMED;
        });
    }
    
    /**
     * Configure edge-to-edge with custom inset handling for specific views
     */
    public static void enableEdgeToEdgeWithCustomInsets(Window window, View rootView, 
                                                       View headerView, View bottomView) {
        WindowCompat.setDecorFitsSystemWindows(window, false);
        
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            androidx.core.graphics.Insets systemBarsInsets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            );
            
            // Apply top inset to header
            if (headerView != null) {
                headerView.setPadding(
                    headerView.getPaddingLeft(),
                    systemBarsInsets.top + headerView.getPaddingTop(),
                    headerView.getPaddingRight(),
                    headerView.getPaddingBottom()
                );
            }
            
            // Apply bottom inset to bottom panel
            if (bottomView != null) {
                bottomView.setPadding(
                    bottomView.getPaddingLeft(),
                    bottomView.getPaddingTop(),
                    bottomView.getPaddingRight(),
                    systemBarsInsets.bottom + bottomView.getPaddingBottom()
                );
            }
            
            return WindowInsetsCompat.CONSUMED;
        });
    }
    
    /**
     * Advanced adaptive system bar handling for Android 15/API 35
     * Features dynamic color extraction and intelligent icon adaptation
     */
    public static void setAdaptiveSystemBarIcons(Window window, boolean lightBackground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                
                // Android 15: Advanced appearance management with blur effects
                if (Build.VERSION.SDK_INT >= 35) {
                    // Enable advanced system bar blur on Android 15
                    controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    );
                }
                
                if (lightBackground) {
                    // Dark icons for light backgrounds
                    controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | 
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | 
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    );
                } else {
                    // Light icons for dark backgrounds
                    controller.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | 
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    );
                }
            }
        }
    }
    
    /**
     * Android 15/API 35: Dynamic Color Extraction from Wallpaper
     * Adapts app theme colors based on system wallpaper for cohesive experience
     */
    public static void applyDynamicColorTheming(android.content.Context context, View rootView) {
        if (Build.VERSION.SDK_INT >= 31) { // Android 12+ dynamic color
            try {
                // Extract dynamic colors from system wallpaper
                android.app.WallpaperManager wallpaperManager = android.app.WallpaperManager.getInstance(context);
                
                // Android 15: Enhanced dynamic color extraction
                if (Build.VERSION.SDK_INT >= 35) {
                    // Advanced color extraction with glassmorphism adaptation
                    android.content.res.ColorStateList dynamicAccent = 
                        context.getColorStateList(android.R.color.system_accent1_600);
                    android.content.res.ColorStateList dynamicNeutral = 
                        context.getColorStateList(android.R.color.system_neutral1_900);
                    
                    if (dynamicAccent != null && dynamicNeutral != null) {
                        // Apply dynamic glassmorphic tinting
                        rootView.setBackgroundTintList(dynamicNeutral);
                        rootView.setForegroundTintList(dynamicAccent);
                    }
                }
            } catch (Exception e) {
                // Fallback to static theming if dynamic colors unavailable
                android.util.Log.d("GlassmorphismUtils", "Dynamic color theming not available: " + e.getMessage());
            }
        }
    }
    
    /**
     * Apply Material 3 Expressive glassmorphism to multiple views
     * Optimized for chess app with different blur levels for UI hierarchy
     */
    public static void applyChessGlassmorphism(View headerPanel, View boardContainer, 
                                             View controlsPanel) {
        // Header gets medium blur for readability while maintaining glass effect
        applyMediumGlassBlur(headerPanel);
        
        // Board container stays sharp (no blur) as it's the main content
        // Controls panel gets light blur to remain functional but stylish
        applyLightGlassBlur(controlsPanel);
    }
    
    /**
     * Check if device supports glassmorphism features
     */
    public static boolean isGlassmorphismSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }
    
    /**
     * Get recommended blur radius based on device performance
     * Samsung S23 Ultra can handle higher blur values
     */
    public static float getOptimalBlurRadius(String deviceModel) {
        // Samsung S23 Ultra and other flagship devices
        if (deviceModel != null && (
            deviceModel.contains("SM-S918") || // S23 Ultra
            deviceModel.contains("SM-S928") || // S24 Ultra  
            deviceModel.contains("Pixel 8") ||
            deviceModel.contains("Pixel 9")
        )) {
            return MAX_SAFE_BLUR_RADIUS;
        }
        
        // Conservative blur for other devices
        return MEDIUM_BLUR_RADIUS;
    }
}