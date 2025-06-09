package com.chesspedagogue.configurator.models;

/**
 * TriggerCondition - Defines when a conversation template should activate
 */
public class TriggerCondition {
    private TriggerType type;
    private String parameter;
    private double threshold;
    private ComparisonOperator operator;
    private boolean isActive;
    
    public enum TriggerType {
        EVALUATION_SWING("Evaluation Change", "Activates when position evaluation changes significantly"),
        MOVE_QUALITY("Move Quality", "Triggers on brilliant moves, blunders, or mistakes"),
        TIME_PRESSURE("Time Pressure", "Activates when clock runs low"),
        PIECE_SACRIFICE("Material Sacrifice", "Triggers on piece sacrifices or exchanges"),
        OPENING_THEORY("Opening Phase", "Activates during opening moves"),
        ENDGAME_TRANSITION("Endgame", "Triggers when entering endgame"),
        EMOTIONAL_STATE("Emotional Trigger", "Based on master emotional state"),
        RELATIONSHIP_DYNAMIC("Relationship", "Triggered by master relationship changes"),
        HISTORICAL_PARALLEL("Historical Reference", "When position matches famous games"),
        GAME_PHASE("Game Phase", "Based on opening/middlegame/endgame"),
        MOVE_COUNT("Move Count", "Triggers after specific number of moves"),
        POSITION_TYPE("Position Type", "Tactical, positional, or balanced positions");
        
        private final String displayName;
        private final String description;
        
        TriggerType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() { return displayName; }
    }
    
    public enum ComparisonOperator {
        GREATER_THAN(">", "Greater than"),
        LESS_THAN("<", "Less than"),
        EQUALS("=", "Equals"),
        GREATER_EQUAL(">=", "Greater than or equal"),
        LESS_EQUAL("<=", "Less than or equal"),
        NOT_EQUALS("!=", "Not equals"),
        CONTAINS("contains", "Contains (for text)"),
        MATCHES("matches", "Matches pattern");
        
        private final String symbol;
        private final String description;
        
        ComparisonOperator(String symbol, String description) {
            this.symbol = symbol;
            this.description = description;
        }
        
        public String getSymbol() { return symbol; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() { return symbol + " (" + description + ")"; }
    }
    
    public TriggerCondition() {
        this.isActive = true;
    }
    
    public TriggerCondition(TriggerType type, String parameter, ComparisonOperator operator, double threshold) {
        this();
        this.type = type;
        this.parameter = parameter;
        this.operator = operator;
        this.threshold = threshold;
    }
    
    // Getters and setters
    public TriggerType getType() { return type; }
    public void setType(TriggerType type) { this.type = type; }
    
    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter; }
    
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    
    public ComparisonOperator getOperator() { return operator; }
    public void setOperator(ComparisonOperator operator) { this.operator = operator; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    @Override
    public String toString() {
        return String.format("%s %s %s %.2f", 
            type != null ? type.getDisplayName() : "Unknown",
            parameter != null ? parameter : "",
            operator != null ? operator.getSymbol() : "=",
            threshold);
    }
    
    // Factory methods for common triggers
    public static TriggerCondition evaluationSwing(double minSwing) {
        return new TriggerCondition(TriggerType.EVALUATION_SWING, "evaluation_change", 
                                   ComparisonOperator.GREATER_THAN, minSwing);
    }
    
    public static TriggerCondition blunderDetected() {
        return new TriggerCondition(TriggerType.MOVE_QUALITY, "blunder", 
                                   ComparisonOperator.EQUALS, 1.0);
    }
    
    public static TriggerCondition brilliantMove() {
        return new TriggerCondition(TriggerType.MOVE_QUALITY, "brilliant", 
                                   ComparisonOperator.EQUALS, 1.0);
    }
    
    public static TriggerCondition timeUnder(int seconds) {
        return new TriggerCondition(TriggerType.TIME_PRESSURE, "remaining_time", 
                                   ComparisonOperator.LESS_THAN, seconds);
    }
    
    public static TriggerCondition endgamePhase() {
        return new TriggerCondition(TriggerType.GAME_PHASE, "phase", 
                                   ComparisonOperator.EQUALS, 3.0); // 3 = endgame
    }
}