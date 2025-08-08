package com.example.chesspedagogue.performance;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import java.io.IOException;

/**
 * Optimized video background manager for smooth accretion disc playback
 */
public class OptimizedVideoBackgroundManager implements TextureView.SurfaceTextureListener {
    private static final String TAG = "OptimizedVideoBG";
    
    private final Context context;
    private MediaPlayer introPlayer;
    private MediaPlayer loopPlayer;
    private TextureView textureView;
    private Surface surface;
    
    // Video states
    private boolean isIntroComplete = false;
    private boolean isPrepared = false;
    private boolean isPaused = false;
    
    // Performance optimizations
    private static final float REDUCED_PLAYBACK_SPEED = 0.75f; // Slower for less CPU
    private static final int VIDEO_BUFFER_SIZE = 1024 * 1024; // 1MB buffer
    
    public OptimizedVideoBackgroundManager(Context context, TextureView textureView) {
        this.context = context;
        this.textureView = textureView;
        this.textureView.setSurfaceTextureListener(this);
        
        // Enable hardware acceleration
        textureView.setOpaque(false);
        Log.d(TAG, "✅ Video background manager initialized");
    }
    
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
        prepareVideos();
    }
    
    private void prepareVideos() {
        try {
            // Prepare intro video
            introPlayer = new MediaPlayer();
            introPlayer.setDataSource(context, getIntroVideoUri());
            introPlayer.setSurface(surface);
            introPlayer.setVolume(0, 0); // Mute for performance
            
            // Pre-buffer for smooth start
            introPlayer.prepareAsync();
            introPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "✅ Intro video prepared");
                if (!isPaused) {
                    mp.start();
                }
            });
            
            // Prepare loop video in background
            loopPlayer = new MediaPlayer();
            loopPlayer.setDataSource(context, getLoopVideoUri());
            loopPlayer.setSurface(surface);
            loopPlayer.setVolume(0, 0);
            loopPlayer.setLooping(true);
            
            // Pre-buffer loop video
            loopPlayer.prepareAsync();
            loopPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "✅ Loop video prepared and ready");
                isPrepared = true;
            });
            
            // Seamless transition from intro to loop
            introPlayer.setOnCompletionListener(mp -> {
                Log.d(TAG, "🔄 Transitioning to loop video");
                isIntroComplete = true;
                mp.release();
                introPlayer = null;
                
                if (isPrepared && !isPaused) {
                    loopPlayer.start();
                }
            });
            
        } catch (IOException e) {
            Log.e(TAG, "❌ Failed to prepare videos", e);
            // Fallback to static background
            fallbackToStaticBackground();
        }
    }
    
    private android.net.Uri getIntroVideoUri() {
        // Use optimized H.264 video for intro
        return android.net.Uri.parse("android.resource://" + 
            context.getPackageName() + "/raw/side_on_loop_orange_512");
    }
    
    private android.net.Uri getLoopVideoUri() {
        // Use optimized loop video
        return android.net.Uri.parse("android.resource://" + 
            context.getPackageName() + "/raw/side_on_loop_orange_fullrange_looped");
    }
    
    /**
     * Pause video playback to save resources
     */
    public void pause() {
        isPaused = true;
        if (introPlayer != null && introPlayer.isPlaying()) {
            introPlayer.pause();
        }
        if (loopPlayer != null && loopPlayer.isPlaying()) {
            loopPlayer.pause();
        }
        Log.d(TAG, "⏸️ Video playback paused");
    }
    
    /**
     * Resume video playback
     */
    public void resume() {
        isPaused = false;
        if (!isIntroComplete && introPlayer != null) {
            introPlayer.start();
        } else if (loopPlayer != null && isPrepared) {
            loopPlayer.start();
        }
        Log.d(TAG, "▶️ Video playback resumed");
    }
    
    /**
     * Release resources
     */
    public void release() {
        if (introPlayer != null) {
            introPlayer.release();
            introPlayer = null;
        }
        if (loopPlayer != null) {
            loopPlayer.release();
            loopPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
        Log.d(TAG, "🛑 Video resources released");
    }
    
    /**
     * Fallback to static background if video fails
     */
    private void fallbackToStaticBackground() {
        Log.w(TAG, "⚠️ Falling back to static background");
        textureView.setVisibility(android.view.View.GONE);
        // The activity should show a static drawable instead
    }
    
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Handle size changes if needed
    }
    
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        release();
        return true;
    }
    
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Frame updates - no action needed
    }
}