package com.example.chesspedagogue.reasoning;

/**
 * Voice configuration for ElevenLabs TTS per chess master
 * Aligns with existing ElevenLabsTTSService implementation
 */
public class VoiceSettings {
    private final String voiceId;           // ElevenLabs voice ID
    private final String model;             // ElevenLabs model (eleven_flash_v2_5, eleven_turbo_v2_5, etc.)
    private final String accent;            // Accent description for voice instructions
    private final float stability;          // ElevenLabs stability setting (0.0-1.0)
    private final float similarityBoost;    // ElevenLabs similarity boost (0.0-1.0)
    private final float style;              // ElevenLabs style setting (0.0-1.0)

    public VoiceSettings(String voiceId, String model, String accent, float stability, float similarityBoost, float style) {
        this.voiceId = voiceId;
        this.model = model;
        this.accent = accent;
        this.stability = stability;
        this.similarityBoost = similarityBoost;
        this.style = style;
    }

    public static VoiceSettings alekhineVoice() {
        return new VoiceSettings(
            "3EuKHIEZbSzrHGNmdYsx", // Ivan - Russian, calm
            "eleven_turbo_v2_5", 
            "Russian-influenced English with slight Slavic undertones", 
            0.75f, 0.75f, 0.8f
        );
    }

    public static VoiceSettings talVoice() {
        return new VoiceSettings(
            "txnCCHHGKmYIwrn7HfHQ", // Alexandr Vlasov
            "eleven_turbo_v2_5", 
            "Latvian-Russian influenced English with dramatic flair", 
            0.6f, 0.8f, 0.9f
        );
    }

    public static VoiceSettings fischerVoice() {
        return new VoiceSettings(
            "KLjqUZMleyr58nTJqW99", // Fischer - intense and precise
            "eleven_turbo_v2_5", 
            "American English with Brooklyn/New York inflection", 
            0.8f, 0.85f, 0.7f
        );
    }

    public static VoiceSettings carlsenVoice() {
        return new VoiceSettings(
            "9pRpxWU0T7UFt2oEMH6n", // Carlsen - calm and modern
            "eleven_turbo_v2_5", 
            "Norwegian-influenced English with Scandinavian undertones", 
            0.85f, 0.8f, 0.6f
        );
    }

    public static VoiceSettings kasparovVoice() {
        return new VoiceSettings(
            "rT6zdbVnOt0GO9v5OiWr", // azeri - dynamic and energetic
            "eleven_turbo_v2_5", 
            "Russian-Azerbaijani influenced English with dynamic delivery", 
            0.7f, 0.85f, 0.85f
        );
    }

    public static VoiceSettings karpovVoice() {
        return new VoiceSettings(
            "pNInz6obpgDQGcFmaJgB", // Adam - refined, measured
            "eleven_turbo_v2_5", 
            "Refined Russian English with measured, thoughtful delivery", 
            0.9f, 0.8f, 0.5f
        );
    }

    // Getters aligned with ElevenLabs API
    public String getVoiceId() { return voiceId; }
    public String getModel() { return model; }
    public String getAccent() { return accent; }
    public float getStability() { return stability; }
    public float getSimilarityBoost() { return similarityBoost; }
    public float getStyle() { return style; }

    @Override
    public String toString() {
        return String.format("VoiceSettings{voiceId='%s', model='%s', accent='%s', stability=%.2f, similarity=%.2f, style=%.2f}",
                voiceId, model, accent, stability, similarityBoost, style);
    }
}