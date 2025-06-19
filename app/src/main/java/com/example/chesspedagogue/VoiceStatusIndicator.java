package com.example.chesspedagogue;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

/**
 * 🚦 Professional Voice Status Indicator
 * Clean, minimal visual feedback for voice system state
 */
public class VoiceStatusIndicator extends FrameLayout {
    private View statusLight;
    private TextView statusText;
    private AlwaysListeningService.VoiceStatus currentStatus;
    
    public VoiceStatusIndicator(Context context) {
        super(context);
        init();
    }
    
    public VoiceStatusIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        // Create circular status light
        statusLight = new View(getContext());
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setSize(32, 32); // Small, unobtrusive
        statusLight.setBackground(circle);
        
        // Create status text
        statusText = new TextView(getContext());
        statusText.setTextSize(10);
        statusText.setTextColor(Color.WHITE);
        statusText.setPadding(8, 2, 8, 2);
        
        // Layout
        LayoutParams lightParams = new LayoutParams(32, 32);
        lightParams.setMargins(8, 8, 8, 2);
        addView(statusLight, lightParams);
        
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.setMargins(8, 34, 8, 8);
        addView(statusText, textParams);
        
        // Start with listening state
        updateStatus(AlwaysListeningService.VoiceStatus.LISTENING_FOR_INITIATION);
    }
    
    public void updateStatus(AlwaysListeningService.VoiceStatus status) {
        if (status == currentStatus) return;
        
        currentStatus = status;
        GradientDrawable background = (GradientDrawable) statusLight.getBackground();
        
        switch (status) {
            case LISTENING_FOR_INITIATION:
                background.setColor(Color.WHITE);
                statusText.setText("Speak UP");
                statusText.setTextColor(Color.GRAY);
                break;
                
            case INITIATION_TRIGGERED:
                background.setColor(Color.YELLOW);
                statusText.setText("Triggered");
                statusText.setTextColor(Color.BLACK);
                // Brief pulse effect
                statusLight.animate().scaleX(1.3f).scaleY(1.3f).setDuration(300)
                    .withEndAction(() -> statusLight.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300));
                break;
                
            case RECORDING_ACTIVE:
                background.setColor(Color.GREEN);
                statusText.setText("Active");
                statusText.setTextColor(Color.WHITE);
                break;
                
            case PROCESSING_SPEECH:
                background.setColor(Color.RED);
                statusText.setText("Processing");
                statusText.setTextColor(Color.WHITE);
                // Brief flash
                statusLight.animate().alpha(0.5f).setDuration(200)
                    .withEndAction(() -> statusLight.animate().alpha(1.0f).setDuration(200));
                break;
        }
    }
    
    /**
     * Get current status for external queries
     */
    public AlwaysListeningService.VoiceStatus getCurrentStatus() {
        return currentStatus;
    }
    
    /**
     * Show/hide the indicator
     */
    public void setVoiceSystemEnabled(boolean enabled) {
        setVisibility(enabled ? VISIBLE : GONE);
    }
}