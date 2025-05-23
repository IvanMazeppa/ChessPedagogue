package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Simplified voice management system that handles both voice selection
 * and personality instructions for all chess masters.
 */
public class VoiceManager {
    private static final String TAG = "VoiceManager";
    private static VoiceManager instance;

    private final Context context;
    private final SharedPreferences prefs;

    // Voice mapping for each chess master
    private static final Map<String, VoiceProfile> VOICE_PROFILES = new HashMap<>();

    static {
        // Initialize voice profiles for each master
        VOICE_PROFILES.put("tal", new VoiceProfile("echo",
                "Speak with enthusiasm and a Latvian accent. Sound excited about tactical possibilities."));

        VOICE_PROFILES.put("kramnik", new VoiceProfile("fable",
                "Speak with a calm Russian accent. Sound thoughtful and analytical."));

        VOICE_PROFILES.put("karpov", new VoiceProfile("onyx",
                "Speak with a methodical Russian accent. Sound patient and precise."));

        VOICE_PROFILES.put("fischer", new VoiceProfile("echo",
                "Speak with an American accent. Sound confident and direct."));

        VOICE_PROFILES.put("lasker", new VoiceProfile("fable",
                "Speak with a German accent. Sound philosophical and wise."));

        VOICE_PROFILES.put("kasparov", new VoiceProfile("onyx",
                "Speak with an energetic Russian accent. Sound passionate about chess."));

        VOICE_PROFILES.put("capablanca", new VoiceProfile("fable",
                "Speak with a Cuban accent. Sound elegant and clear."));

        VOICE_PROFILES.put("carlsen", new VoiceProfile("echo",
                "Speak with a Norwegian accent. Sound modern and practical."));

        VOICE_PROFILES.put("morphy", new VoiceProfile("fable",
                "Speak with a slight Southern American accent. Sound dignified."));

        VOICE_PROFILES.put("anand", new VoiceProfile("echo",
                "Speak with an Indian accent. Sound quick and insightful."));

        VOICE_PROFILES.put("alekhine", new VoiceProfile("fable",
                "Speak with a cultured Russian-French accent. Sound sophisticated and passionate."));

        VOICE_PROFILES.put("botvinnik", new VoiceProfile("onyx",
                "Speak with a Russian accent. Sound scientific and authoritative."));
    }

    private VoiceManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
    }

    public static synchronized VoiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceManager(context);
        }
        return instance;
    }

    /**
     * Get the voice ID for the current chess master
     */
    public String getVoiceForCurrentMaster() {
        String currentMaster = getCurrentChessMaster();
        String voiceOverride = prefs.getString("voice_style", "auto");

        // If user has selected a specific voice, use that
        if (!"auto".equals(voiceOverride)) {
            return voiceOverride;
        }

        // Otherwise use the master's default voice
        VoiceProfile profile = VOICE_PROFILES.get(currentMaster.toLowerCase());
        return profile != null ? profile.voiceId : "alloy";
    }

    /**
     * Get personality instructions for the current master
     */
    public String getPersonalityInstructions() {
        boolean usePersonality = prefs.getBoolean("use_master_personality", true);
        if (!usePersonality) {
            return null;
        }

        String currentMaster = getCurrentChessMaster();
        VoiceProfile profile = VOICE_PROFILES.get(currentMaster.toLowerCase());
        return profile != null ? profile.instructions : null;
    }

    /**
     * Get instructions for a specific chunk (with continuity)
     */
    public String getInstructionsForChunk(int chunkIndex) {
        String baseInstructions = getPersonalityInstructions();
        if (baseInstructions == null) {
            return null;
        }

        if (chunkIndex > 0) {
            return baseInstructions + " CRITICAL: Continue with the EXACT same voice, " +
                    "tone, pacing, and accent as the previous audio segment.";
        }

        return baseInstructions;
    }

    /**
     * Get the currently selected chess master
     */
    private String getCurrentChessMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        return masterPrefs.getString("selected_master", "tal");
    }

    /**
     * Update voice settings
     */
    public void updateSettings(String voiceStyle, boolean usePersonality) {
        prefs.edit()
                .putString("voice_style", voiceStyle)
                .putBoolean("use_master_personality", usePersonality)
                .apply();

        Log.d(TAG, "Updated voice settings: style=" + voiceStyle + ", personality=" + usePersonality);
    }

    /**
     * Inner class to hold voice profile data
     */
    private static class VoiceProfile {
        final String voiceId;
        final String instructions;

        VoiceProfile(String voiceId, String instructions) {
            this.voiceId = voiceId;
            this.instructions = instructions;
        }
    }
}