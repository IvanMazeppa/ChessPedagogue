package com.example.chesspedagogue.reasoning;

import android.content.Context;
import android.util.Log;

import com.example.chesspedagogue.ChessMasterRatings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central manager for chess master configurations
 */
public class MasterConfigurationManager {
    private static final String TAG = "MasterConfigManager";
    private static final Map<String, MasterConfiguration> configs = new HashMap<>();
    private static boolean initialized = false;

    public static void initialize(Context context) {
        if (initialized) return;
        
        Log.d(TAG, "🧠 Initializing master configurations for reasoning engine...");
        
        // Load all 17 chess masters with reasoning configurations
        loadAlekhineConfig();
        loadTalConfig();
        loadFischerConfig();
        loadCarlsenConfig();
        loadKasparovConfig();
        loadKarpovConfig();
        // Load missing masters from extensions
        MasterConfigurationManagerExtensions.loadMorphyConfig();
        MasterConfigurationManagerExtensions.loadCapablancaConfig();
        MasterConfigurationManagerExtensions.loadLaskerConfig();
        MasterConfigurationManagerExtensions.loadBotvinnikConfig();
        MasterConfigurationManagerExtensions.loadPetrosianConfig();
        MasterConfigurationManagerExtensions.loadAnandConfig();
        MasterConfigurationManagerExtensions.loadKramnikConfig();
        MasterConfigurationManagerExtensions.loadNakamuraConfig();
        MasterConfigurationManagerExtensions.loadGukeshConfig();
        MasterConfigurationManagerExtensions.loadShortConfig();
        MasterConfigurationManagerExtensions.loadNimzowitschConfig();
        
        initialized = true;
        Log.d(TAG, "✅ Master configurations loaded: " + configs.keySet());
    }

    public static MasterConfiguration getConfig(String masterName) {
        if (!initialized) {
            Log.w(TAG, "⚠️ MasterConfigurationManager not initialized!");
            return null;
        }
        return configs.get(masterName.toLowerCase());
    }

    public static boolean supportsReasoning(String masterName) {
        return initialized && configs.containsKey(masterName.toLowerCase());
    }
    
    /**
     * Get available master names
     */
    public static Set<String> getAvailableMasters() {
        return new HashSet<>(configs.keySet());
    }
    
    /**
     * Get peak historical rating for a master
     */
    public static int getPeakRating(String masterName) {
        return ChessMasterRatings.getPeakRating(masterName);
    }
    
    /**
     * Set master to peak historical rating
     */
    public static int setPeakRating(String masterName) {
        int peakRating = getPeakRating(masterName);
        Log.d(TAG, String.format("🏆 %s peak rating: %d", masterName, peakRating));
        return peakRating;
    }

    /**
     * Add configuration from external source (used by extensions)
     */
    public static void addConfig(String masterName, MasterConfiguration config) {
        configs.put(masterName.toLowerCase(), config);
    }

    private static void loadAlekhineConfig() {
        MasterConfiguration alekhine = new MasterConfiguration.Builder("alekhine")
            .reasoningModel("o4-mini")
            .systemInstructions(getAlekhineInstructions())
            .vectorStoreId("vs_685f5e39515481919229a059cd8a2d96")
            .personalityTraits(new PersonalityTraits()
                .aggression(0.8f)
                .calculationDepth(0.9f)
                .riskTolerance(0.7f)
                .creativityLevel(0.8f)
                .endgameFocus(0.7f)
                .positionalWeight(0.8f))
            .voiceConfig(VoiceSettings.alekhineVoice())
            .maxOutputTokens(25000)
            .temperature(1.0)
            .reasoningEffort("low")
            .build();
        configs.put("alekhine", alekhine);
        Log.d(TAG, "✅ Loaded Alekhine configuration");
    }

    private static void loadTalConfig() {
        MasterConfiguration tal = new MasterConfiguration.Builder("tal")
            .reasoningModel("o4-mini")
            .systemInstructions(getTalInstructions())
            .vectorStoreId("vs_682f419a57288191aa3cd922b27acb5f") // TODO: Add actual vector store ID
            .personalityTraits(new PersonalityTraits()
                .aggression(0.95f)
                .calculationDepth(0.9f)
                .riskTolerance(0.9f)
                .creativityLevel(0.95f)
                .endgameFocus(0.6f)
                .positionalWeight(0.5f))
            .voiceConfig(VoiceSettings.talVoice())
            .maxOutputTokens(25000)
            .temperature(1.2)
            .reasoningEffort("low")
            .build();
        configs.put("tal", tal);
        Log.d(TAG, "✅ Loaded Tal configuration");
    }

    private static void loadFischerConfig() {
        MasterConfiguration fischer = new MasterConfiguration.Builder("fischer")
            .reasoningModel("o4-mini")
            .systemInstructions(getFischerInstructions())
            .vectorStoreId("vs_fischer_games") // TODO: Add actual vector store ID
            .personalityTraits(new PersonalityTraits()
                .aggression(0.7f)
                .calculationDepth(0.95f)
                .riskTolerance(0.6f)
                .creativityLevel(0.7f)
                .endgameFocus(0.9f)
                .positionalWeight(0.8f))
            .voiceConfig(VoiceSettings.fischerVoice())
            .maxOutputTokens(25000)
            .temperature(0.8)
            .reasoningEffort("low")
            .build();
        configs.put("fischer", fischer);
        Log.d(TAG, "✅ Loaded Fischer configuration");
    }

    private static void loadCarlsenConfig() {
        MasterConfiguration carlsen = new MasterConfiguration.Builder("carlsen")
            .reasoningModel("o4-mini")
            .systemInstructions(getCarlsenInstructions())
            .vectorStoreId("vs_68365028eb988191b09d8d50e6f11b5d")
            .personalityTraits(new PersonalityTraits()
                .aggression(0.6f)
                .calculationDepth(0.8f)
                .riskTolerance(0.6f)
                .creativityLevel(0.7f)
                .endgameFocus(0.95f)
                .positionalWeight(0.9f))
            .voiceConfig(VoiceSettings.carlsenVoice())
            .maxOutputTokens(25000)
            .temperature(0.9)
            .reasoningEffort("low")
            .build();
        configs.put("carlsen", carlsen);
        Log.d(TAG, "✅ Loaded Carlsen configuration");
    }

    private static void loadKasparovConfig() {
        MasterConfiguration kasparov = new MasterConfiguration.Builder("kasparov")
            .reasoningModel("o4-mini")
            .systemInstructions(getKasparovInstructions())
            .vectorStoreId("vs_kasparov_games") // TODO: Add actual vector store ID
            .personalityTraits(new PersonalityTraits()
                .aggression(0.85f)
                .calculationDepth(0.9f)
                .riskTolerance(0.75f)
                .creativityLevel(0.8f)
                .endgameFocus(0.8f)
                .positionalWeight(0.8f))
            .voiceConfig(VoiceSettings.kasparovVoice())
            .maxOutputTokens(25000)
            .temperature(1.1)
            .reasoningEffort("low")
            .build();
        configs.put("kasparov", kasparov);
        Log.d(TAG, "✅ Loaded Kasparov configuration");
    }

    private static void loadKarpovConfig() {
        MasterConfiguration karpov = new MasterConfiguration.Builder("karpov")
            .reasoningModel("o4-mini")
            .systemInstructions(getKarpovInstructions())
            .vectorStoreId("vs_karpov_games") // TODO: Add actual vector store ID
            .personalityTraits(new PersonalityTraits()
                .aggression(0.4f)
                .calculationDepth(0.85f)
                .riskTolerance(0.3f)
                .creativityLevel(0.6f)
                .endgameFocus(0.9f)
                .positionalWeight(0.95f))
            .voiceConfig(VoiceSettings.karpovVoice())
            .maxOutputTokens(25000)
            .temperature(0.7)
            .reasoningEffort("low")
            .build();
        configs.put("karpov", karpov);
        Log.d(TAG, "✅ Loaded Karpov configuration");
    }

    private static String getAlekhineInstructions() {
        return "You are Alexander Alekhine, World Chess Champion (1927-35, 1937-46). Key traits:\n" +
               "- Aggressive attacking play with sound calculation\n" +
               "- Complex tactical combinations based on solid positional foundations\n" +
               "- Dynamic piece sacrifices when compensation is clear\n" +
               "- Excellent opening preparation and deep endgame technique\n" +
               "- Preference for winning chances over sterile equality\n\n" +
               "Play in your characteristic style, maintaining your fighting spirit and preference for active, dynamic positions. " +
               "Create tactical complications when the position allows, and always seek winning chances.\n\n" +
               "MANDATORY: You must call evaluate_move_strength() before selecting your final move to ensure " +
               "appropriate playing strength. Respond with UCI format only (e.g., e2e4, g1f3).";
    }

    private static String getTalInstructions() {
        return "You are Mikhail Tal, the 'Magician from Riga', World Chess Champion (1960-61). Key traits:\n" +
               "- Ultra-aggressive sacrificial play and brilliant combinations\n" +
               "- Intuitive tactical vision over pure calculation\n" +
               "- Willingness to sacrifice material for initiative and attack\n" +
               "- Creative, unorthodox moves that confuse opponents\n" +
               "- Preference for complicated, tactical positions\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Prioritize tactical complications and sacrificial opportunities\n" +
               "2. Choose creative, unexpected moves when tactically sound\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Accept calculated risks for winning chances\n\n" +
               "Embody Tal's brilliant tactical genius while maintaining chess strength.";
    }

    private static String getFischerInstructions() {
        return "You are Bobby Fischer, World Chess Champion (1972-75). Key traits:\n" +
               "- Perfect technique and precise calculation\n" +
               "- Exceptional endgame mastery\n" +
               "- Deep theoretical preparation in openings\n" +
               "- Relentless pursuit of optimal moves\n" +
               "- Clear, logical positional understanding\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Choose the most accurate, objectively best moves\n" +
               "2. Prioritize technical precision over flashy tactics\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Demonstrate Fischer's legendary accuracy\n\n" +
               "Play with Fischer's legendary precision and technical mastery.";
    }

    private static String getCarlsenInstructions() {
        return "You are Magnus Carlsen, World Chess Champion (2013-2023). Key traits:\n" +
               "- Modern intuitive understanding of positions\n" +
               "- Exceptional endgame technique\n" +
               "- Practical decision-making over pure theory\n" +
               "- Ability to create chances from equal positions\n" +
               "- Universal playing style adapting to position demands\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Make practical, result-oriented moves\n" +
               "2. Excel in complex endgames and transitions\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Carlsen's modern positional understanding\n\n" +
               "Demonstrate Carlsen's practical mastery and endgame excellence.";
    }

    private static String getKasparovInstructions() {
        return "You are Garry Kasparov, World Chess Champion (1985-2000). Key traits:\n" +
               "- Dynamic, aggressive playing style\n" +
               "- Deep theoretical preparation and opening innovation\n" +
               "- Excellent tactical calculation and intuition\n" +
               "- Fighting spirit and psychological pressure\n" +
               "- Strategic depth combined with tactical brilliance\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Play dynamically with constant fighting chances\n" +
               "2. Demonstrate theoretical knowledge and preparation\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Kasparov's aggressive, principled style\n\n" +
               "Embody Kasparov's dynamic fighting spirit and theoretical mastery.";
    }

    private static String getKarpovInstructions() {
        return "You are Anatoly Karpov, World Chess Champion (1975-85). Key traits:\n" +
               "- Positional mastery and strategic understanding\n" +
               "- Subtle, gradual advantage accumulation\n" +
               "- Exceptional endgame technique\n" +
               "- Patient, methodical approach to improvement\n" +
               "- Defensive solidity combined with precise technique\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Focus on positional improvements and strategic plans\n" +
               "2. Demonstrate patience and gradual advantage building\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Karpov's positional mastery and technique\n\n" +
               "Play with Karpov's positional understanding and strategic patience.";
    }
}