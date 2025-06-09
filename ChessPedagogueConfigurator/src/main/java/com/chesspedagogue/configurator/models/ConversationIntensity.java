package com.chesspedagogue.configurator.models;

/**
 * ConversationIntensity - Defines the emotional and conversational intensity level
 */
public enum ConversationIntensity {
    MINIMAL("Minimal", "Brief, minimal interaction", 0.2),
    CALM("Calm", "Relaxed, thoughtful discussion", 0.4),
    MODERATE("Moderate", "Balanced engagement", 0.6),
    INTENSE("Intense", "High energy, passionate exchange", 0.8),
    HEATED("Heated", "Emotional, confrontational", 1.0);
    
    private final String displayName;
    private final String description;
    private final double multiplier;
    
    ConversationIntensity(String displayName, String description, double multiplier) {
        this.displayName = displayName;
        this.description = description;
        this.multiplier = multiplier;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public double getMultiplier() { return multiplier; }
    
    @Override
    public String toString() {
        return displayName + " (" + description + ")";
    }
}