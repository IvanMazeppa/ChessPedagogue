package com.example.chesspedagogue.reasoning;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for a chess master's reasoning behavior
 */
public class MasterConfiguration {
    private final String masterName;
    private final String reasoningModel;
    private final String systemInstructions;
    private final String vectorStoreId;
    private final List<String> availableFunctions;
    private final PersonalityTraits traits;
    private final VoiceSettings voiceConfig;
    private final int maxOutputTokens;
    private final double temperature;
    private final String reasoningEffort;

    private MasterConfiguration(Builder builder) {
        this.masterName = builder.masterName;
        this.reasoningModel = builder.reasoningModel;
        this.systemInstructions = builder.systemInstructions;
        this.vectorStoreId = builder.vectorStoreId;
        this.availableFunctions = new ArrayList<>(builder.availableFunctions);
        this.traits = builder.traits;
        this.voiceConfig = builder.voiceConfig;
        this.maxOutputTokens = builder.maxOutputTokens;
        this.temperature = builder.temperature;
        this.reasoningEffort = builder.reasoningEffort;
    }

    // Getters
    public String getMasterName() { return masterName; }
    public String getReasoningModel() { return reasoningModel; }
    public String getSystemInstructions() { return systemInstructions; }
    public String getVectorStoreId() { return vectorStoreId; }
    public List<String> getAvailableFunctions() { return new ArrayList<>(availableFunctions); }
    public PersonalityTraits getTraits() { return traits; }
    public VoiceSettings getVoiceConfig() { return voiceConfig; }
    public int getMaxOutputTokens() { return maxOutputTokens; }
    public double getTemperature() { return temperature; }
    public String getReasoningEffort() { return reasoningEffort; }

    public static class Builder {
        private String masterName;
        private String reasoningModel = "o4-mini";
        private String systemInstructions = "";
        private String vectorStoreId = "";
        private List<String> availableFunctions = new ArrayList<>();
        private PersonalityTraits traits = new PersonalityTraits();
        private VoiceSettings voiceConfig = VoiceSettings.fischerVoice(); // Default to Fischer voice
        private int maxOutputTokens = 25000;
        private double temperature = 1.0;
        private String reasoningEffort = "low";

        public Builder(String masterName) {
            this.masterName = masterName;
        }

        public Builder reasoningModel(String model) {
            this.reasoningModel = model;
            return this;
        }

        public Builder systemInstructions(String instructions) {
            this.systemInstructions = instructions;
            return this;
        }

        public Builder vectorStoreId(String id) {
            this.vectorStoreId = id;
            return this;
        }

        public Builder availableFunctions(List<String> functions) {
            this.availableFunctions = new ArrayList<>(functions);
            return this;
        }

        public Builder personalityTraits(PersonalityTraits traits) {
            this.traits = traits;
            return this;
        }

        public Builder voiceConfig(VoiceSettings config) {
            this.voiceConfig = config;
            return this;
        }

        public Builder maxOutputTokens(int tokens) {
            this.maxOutputTokens = tokens;
            return this;
        }

        public Builder temperature(double temp) {
            this.temperature = temp;
            return this;
        }

        public Builder reasoningEffort(String effort) {
            this.reasoningEffort = effort;
            return this;
        }

        public MasterConfiguration build() {
            return new MasterConfiguration(this);
        }
    }

    @Override
    public String toString() {
        return String.format("MasterConfiguration{name='%s', model='%s', effort='%s', tokens=%d, temp=%.2f}",
                masterName, reasoningModel, reasoningEffort, maxOutputTokens, temperature);
    }
}