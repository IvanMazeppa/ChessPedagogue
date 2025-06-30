package com.example.chesspedagogue.reasoning;

/**
 * Personality traits configuration for chess masters
 */
public class PersonalityTraits {
    private float aggression = 0.5f;           // 0.0 = defensive, 1.0 = ultra-aggressive
    private float calculationDepth = 0.5f;     // 0.0 = intuitive, 1.0 = deep calculation
    private float riskTolerance = 0.5f;        // 0.0 = safe, 1.0 = high risk
    private float creativityLevel = 0.5f;      // 0.0 = conventional, 1.0 = creative
    private float endgameFocus = 0.5f;         // 0.0 = tactical, 1.0 = endgame specialist
    private float positionalWeight = 0.5f;     // 0.0 = tactical, 1.0 = positional

    public PersonalityTraits() {}

    public PersonalityTraits aggression(float value) {
        this.aggression = Math.max(0.0f, Math.min(1.0f, value));
        return this;
    }

    public PersonalityTraits calculationDepth(float value) {
        this.calculationDepth = Math.max(0.0f, Math.min(1.0f, value));
        return this;
    }

    public PersonalityTraits riskTolerance(float value) {
        this.riskTolerance = Math.max(0.0f, Math.min(1.0f, value));
        return this;
    }

    public PersonalityTraits creativityLevel(float value) {
        this.creativityLevel = Math.max(0.0f, Math.min(1.0f, value));
        return this;
    }

    public PersonalityTraits endgameFocus(float value) {
        this.endgameFocus = Math.max(0.0f, Math.min(1.0f, value));
        return this;
    }

    public PersonalityTraits positionalWeight(float value) {
        this.positionalWeight = Math.max(0.0f, Math.min(1.0f, value));
        return this;
    }

    // Getters
    public float getAggression() { return aggression; }
    public float getCalculationDepth() { return calculationDepth; }
    public float getRiskTolerance() { return riskTolerance; }
    public float getCreativityLevel() { return creativityLevel; }
    public float getEndgameFocus() { return endgameFocus; }
    public float getPositionalWeight() { return positionalWeight; }

    @Override
    public String toString() {
        return String.format("PersonalityTraits{aggression=%.2f, calc=%.2f, risk=%.2f, creativity=%.2f, endgame=%.2f, positional=%.2f}",
                aggression, calculationDepth, riskTolerance, creativityLevel, endgameFocus, positionalWeight);
    }
}