package com.example.chesspedagogue;

/**
 * Manages voice profiles for different chess masters
 */
public class ChessMasterVoiceManager {

    /**
     * Voice model options available in OpenAI API
     */
    public static final String VOICE_ALLOY = "alloy";     // Neutral voice
    public static final String VOICE_ECHO = "echo";       // American male
    public static final String VOICE_FABLE = "fable";     // British male
    public static final String VOICE_ONYX = "onyx";       // Deep, authoritative
    public static final String VOICE_NOVA = "nova";       // Female voice
    public static final String VOICE_SHIMMER = "shimmer"; // Female voice


    /**
     * Get the best voice for a specific chess master
     */
    public static String getVoiceForMaster(String master) {
        // Default to a neutral voice if master is unknown
        if (master == null) return VOICE_ALLOY;

        switch (master.toLowerCase()) {
            // Deep, authoritative voice for the commanding Russian players
            case "karpov":
                return VOICE_ONYX;
            case "kasparov":
                return VOICE_ONYX;

            // American voice for American players
            case "fischer":
                return VOICE_ECHO;

            // British voice for classical/elegant players
            case "capablanca":
                return VOICE_FABLE;
            case "lasker":
                return VOICE_FABLE;
            case "morphy":
                return VOICE_FABLE;

            // More energetic, modern players with American voice
            case "tal":
                return VOICE_ECHO;
            case "carlsen":
                return VOICE_ECHO;
            case "anand":
                return VOICE_ECHO;

            // Measured, analytical players with British voice
            case "kramnik":
                return VOICE_FABLE;

            // Default fallback
            default:
                return VOICE_ALLOY;
        }
    }

    /**
     * Get simplified voice instructions for the selected master
     */
    // Simplified master instructions
    public static String getSimplifiedInstructionsForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "Speak with a Latvian accent. Sound enthusiastic about chess.";

            case "kramnik":
                return "Speak with a Russian accent. Sound calm and thoughtful.";

            case "karpov":
                return "Speak with a Russian accent. Sound methodical and patient.";

            case "fischer":
                return "Speak with an American accent. Sound confident and direct.";

            case "lasker":
                return "Speak with a German accent. Sound philosophical and wise.";

            case "kasparov":
                return "Speak with a Russian accent. Sound energetic and passionate.";

            case "capablanca":
                return "Speak with a Cuban accent. Sound elegant and clear.";

            case "carlsen":
                return "Speak with a Norwegian accent. Sound modern and practical.";

            case "morphy":
                return "Speak with a slight Southern American accent. Sound dignified.";

            case "anand":
                return "Speak with an Indian accent. Sound quick and insightful.";

            default:
                return "Speak as an experienced chess coach.";
        }
    }

    // Add continuity for chunks after the first

    public static String getInstructionsForChunk(String master, int chunkIndex) {
        String baseInstruction = getSimplifiedInstructionsForMaster(master);

        if (chunkIndex > 0) {
            return baseInstruction + " CRITICAL: Continue with the EXACT same voice, tone, pacing, and accent as the previous audio segment. Maintain perfect continuity as if this is one continuous recording.";
        }

        return baseInstruction;
    }
}