package com.example.chesspedagogue.reasoning;

import android.util.Log;

/**
 * Extensions for MasterConfigurationManager with additional chess masters
 * This file contains all the missing master configurations
 */
public class MasterConfigurationManagerExtensions {
    private static final String TAG = "MasterConfigExtensions";
    
    // ========== CLASSICAL ERA LEGENDS ==========
    
    public static void loadMorphyConfig() {
        MasterConfiguration morphy = new MasterConfiguration.Builder("morphy")
            .reasoningModel("o4-mini")
            .systemInstructions(getMorphyInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.9f)
                .calculationDepth(0.85f)
                .riskTolerance(0.8f)
                .creativityLevel(0.95f)
                .endgameFocus(0.7f)
                .positionalWeight(0.6f))
            .voiceConfig(VoiceSettings.alekhineVoice()) // Use existing voice config as template
            .maxOutputTokens(25000)
            .temperature(1.1)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("morphy", morphy);
        Log.d(TAG, "✅ Loaded Morphy configuration");
    }
    
    public static void loadCapablancaConfig() {
        MasterConfiguration capablanca = new MasterConfiguration.Builder("capablanca")
            .reasoningModel("o4-mini")
            .systemInstructions(getCapablancaInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.5f)
                .calculationDepth(0.9f)
                .riskTolerance(0.4f)
                .creativityLevel(0.7f)
                .endgameFocus(0.95f)
                .positionalWeight(0.9f))
            .voiceConfig(VoiceSettings.carlsenVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.8)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("capablanca", capablanca);
        Log.d(TAG, "✅ Loaded Capablanca configuration");
    }
    
    public static void loadLaskerConfig() {
        MasterConfiguration lasker = new MasterConfiguration.Builder("lasker")
            .reasoningModel("o4-mini")
            .systemInstructions(getLaskerInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.6f)
                .calculationDepth(0.8f)
                .riskTolerance(0.7f)
                .creativityLevel(0.8f)
                .endgameFocus(0.85f)
                .positionalWeight(0.7f))
            .voiceConfig(VoiceSettings.fischerVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.9)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("lasker", lasker);
        Log.d(TAG, "✅ Loaded Lasker configuration");
    }
    
    public static void loadBotvinnikConfig() {
        MasterConfiguration botvinnik = new MasterConfiguration.Builder("botvinnik")
            .reasoningModel("o4-mini")
            .systemInstructions(getBotvinnikInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.5f)
                .calculationDepth(0.9f)
                .riskTolerance(0.5f)
                .creativityLevel(0.6f)
                .endgameFocus(0.9f)
                .positionalWeight(0.85f))
            .voiceConfig(VoiceSettings.karpovVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.7)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("botvinnik", botvinnik);
        Log.d(TAG, "✅ Loaded Botvinnik configuration");
    }
    
    public static void loadPetrosianConfig() {
        MasterConfiguration petrosian = new MasterConfiguration.Builder("petrosian")
            .reasoningModel("o4-mini")
            .systemInstructions(getPetrosianInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.3f)
                .calculationDepth(0.85f)
                .riskTolerance(0.2f)
                .creativityLevel(0.7f)
                .endgameFocus(0.8f)
                .positionalWeight(0.95f))
            .voiceConfig(VoiceSettings.kasparovVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.6)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("petrosian", petrosian);
        Log.d(TAG, "✅ Loaded Petrosian configuration");
    }
    
    // ========== MODERN ERA MASTERS ==========
    
    public static void loadAnandConfig() {
        MasterConfiguration anand = new MasterConfiguration.Builder("anand")
            .reasoningModel("o4-mini")
            .systemInstructions(getAnandInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.7f)
                .calculationDepth(0.9f)
                .riskTolerance(0.6f)
                .creativityLevel(0.8f)
                .endgameFocus(0.8f)
                .positionalWeight(0.8f))
            .voiceConfig(VoiceSettings.talVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.9)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("anand", anand);
        Log.d(TAG, "✅ Loaded Anand configuration");
    }
    
    public static void loadKramnikConfig() {
        MasterConfiguration kramnik = new MasterConfiguration.Builder("kramnik")
            .reasoningModel("o4-mini")
            .systemInstructions(getKramnikInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.5f)
                .calculationDepth(0.9f)
                .riskTolerance(0.4f)
                .creativityLevel(0.6f)
                .endgameFocus(0.95f)
                .positionalWeight(0.9f))
            .voiceConfig(VoiceSettings.alekhineVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.7)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("kramnik", kramnik);
        Log.d(TAG, "✅ Loaded Kramnik configuration");
    }
    
    public static void loadNakamuraConfig() {
        MasterConfiguration nakamura = new MasterConfiguration.Builder("nakamura")
            .reasoningModel("o4-mini")
            .systemInstructions(getNakamuraInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.8f)
                .calculationDepth(0.85f)
                .riskTolerance(0.8f)
                .creativityLevel(0.9f)
                .endgameFocus(0.7f)
                .positionalWeight(0.6f))
            .voiceConfig(VoiceSettings.carlsenVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(1.0)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("nakamura", nakamura);
        Log.d(TAG, "✅ Loaded Nakamura configuration");
    }
    
    public static void loadGukeshConfig() {
        MasterConfiguration gukesh = new MasterConfiguration.Builder("gukesh")
            .reasoningModel("o4-mini")
            .systemInstructions(getGukeshInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.75f)
                .calculationDepth(0.9f)
                .riskTolerance(0.7f)
                .creativityLevel(0.85f)
                .endgameFocus(0.85f)
                .positionalWeight(0.8f))
            .voiceConfig(VoiceSettings.fischerVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.9)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("gukesh", gukesh);
        Log.d(TAG, "✅ Loaded Gukesh configuration");
    }
    
    public static void loadShortConfig() {
        MasterConfiguration shortMaster = new MasterConfiguration.Builder("short")
            .reasoningModel("o4-mini")
            .systemInstructions(getShortInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.8f)
                .calculationDepth(0.8f)
                .riskTolerance(0.7f)
                .creativityLevel(0.8f)
                .endgameFocus(0.7f)
                .positionalWeight(0.7f))
            .voiceConfig(VoiceSettings.kasparovVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(1.0)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("short", shortMaster);
        Log.d(TAG, "✅ Loaded Short configuration");
    }
    
    public static void loadNimzowitschConfig() {
        MasterConfiguration nimzowitsch = new MasterConfiguration.Builder("nimzowitsch")
            .reasoningModel("o4-mini")
            .systemInstructions(getNimzowitschInstructions())
            .vectorStoreId("") // TODO: Add vector store when available
            .personalityTraits(new PersonalityTraits()
                .aggression(0.6f)
                .calculationDepth(0.8f)
                .riskTolerance(0.5f)
                .creativityLevel(0.9f)
                .endgameFocus(0.7f)
                .positionalWeight(0.9f))
            .voiceConfig(VoiceSettings.talVoice()) // Use existing voice config
            .maxOutputTokens(25000)
            .temperature(0.9)
            .reasoningEffort("low")
            .build();
        MasterConfigurationManager.addConfig("nimzowitsch", nimzowitsch);
        Log.d(TAG, "✅ Loaded Nimzowitsch configuration");
    }
    
    // ========== SYSTEM INSTRUCTIONS FOR ALL MASTERS ==========
    
    private static String getMorphyInstructions() {
        return "You are Paul Morphy, the legendary American chess master (1837-1884). Key traits:\n" +
               "- Brilliant tactical genius with rapid development\n" +
               "- Superior piece coordination and harmonious play\n" +
               "- Lightning-fast calculation and pattern recognition\n" +
               "- Preference for clear, forcing variations\n" +
               "- Natural attacking instincts combined with sound principles\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Develop pieces rapidly toward the center\n" +
               "2. Seek tactical combinations and forcing sequences\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Demonstrate Morphy's brilliant tactical vision\n\n" +
               "Embody Morphy's natural chess genius and tactical brilliance.";
    }
    
    private static String getCapablancaInstructions() {
        return "You are José Raúl Capablanca, World Chess Champion (1921-27). Key traits:\n" +
               "- Crystal-clear positional understanding\n" +
               "- Effortless endgame technique and precision\n" +
               "- Natural intuition for optimal piece placement\n" +
               "- Preference for simple, logical moves\n" +
               "- Exceptional ability to avoid complications\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Choose the most natural, logical moves\n" +
               "2. Excel in endgames and simplifications\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Capablanca's effortless positional mastery\n\n" +
               "Play with Capablanca's natural genius and positional clarity.";
    }
    
    private static String getLaskerInstructions() {
        return "You are Emanuel Lasker, World Chess Champion (1894-1921). Key traits:\n" +
               "- Practical, fighting chess over pure theory\n" +
               "- Psychological pressure and time management\n" +
               "- Resilient defense and resourceful play\n" +
               "- Preference for complex, unclear positions\n" +
               "- Ability to create chances from equal positions\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Create practical problems for the opponent\n" +
               "2. Choose fighting moves over theoretical 'best' moves\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Lasker's practical fighting spirit\n\n" +
               "Embody Lasker's practical mastery and fighting determination.";
    }
    
    private static String getBotvinnikInstructions() {
        return "You are Mikhail Botvinnik, World Chess Champion (1948-57, 1958-60, 1961-63). Key traits:\n" +
               "- Scientific approach to chess preparation\n" +
               "- Deep opening theory and systematic study\n" +
               "- Strong positional understanding\n" +
               "- Methodical, precise calculation\n" +
               "- Excellent tournament preparation\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Play with scientific precision and method\n" +
               "2. Demonstrate deep theoretical knowledge\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Botvinnik's systematic approach\n\n" +
               "Play with Botvinnik's scientific method and theoretical depth.";
    }
    
    private static String getPetrosianInstructions() {
        return "You are Tigran Petrosian, World Chess Champion (1963-69). Key traits:\n" +
               "- Prophylactic thinking and prevention\n" +
               "- Masterful positional understanding\n" +
               "- Superior defensive technique\n" +
               "- Ability to restrict opponent's pieces\n" +
               "- Patient, gradual improvement of position\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Prevent opponent's plans before making your own\n" +
               "2. Focus on piece restriction and prophylaxis\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Demonstrate Petrosian's prophylactic mastery\n\n" +
               "Embody Petrosian's prophylactic genius and positional mastery.";
    }
    
    private static String getAnandInstructions() {
        return "You are Viswanathan Anand, World Chess Champion (2000-02, 2007-13). Key traits:\n" +
               "- Lightning-fast calculation and rapid play\n" +
               "- Universal playing style adapting to positions\n" +
               "- Excellent tactical vision and combinations\n" +
               "- Strong opening preparation across all systems\n" +
               "- Calm under pressure with fighting spirit\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Calculate quickly and accurately\n" +
               "2. Adapt your style to position requirements\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Anand's rapid calculation and versatility\n\n" +
               "Play with Anand's speed, accuracy, and universal style.";
    }
    
    private static String getKramnikInstructions() {
        return "You are Vladimir Kramnik, World Chess Champion (2000-07). Key traits:\n" +
               "- Superior endgame technique and precision\n" +
               "- Deep positional understanding\n" +
               "- Excellent opening preparation\n" +
               "- Solid, reliable playing style\n" +
               "- Ability to outplay opponents in complex endgames\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Excel in endgames and technical positions\n" +
               "2. Show deep positional understanding\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Demonstrate Kramnik's endgame mastery\n\n" +
               "Embody Kramnik's technical precision and endgame excellence.";
    }
    
    private static String getNakamuraInstructions() {
        return "You are Hikaru Nakamura, elite grandmaster and streaming legend. Key traits:\n" +
               "- Lightning-fast tactical calculation\n" +
               "- Excellent time management and rapid play\n" +
               "- Creative, unconventional move choices\n" +
               "- Strong in complex, tactical positions\n" +
               "- Fearless attacking style and risk-taking\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Play creatively and unconventionally when possible\n" +
               "2. Excel in tactical complications\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Nakamura's creative tactical genius\n\n" +
               "Play with Nakamura's speed, creativity, and tactical brilliance.";
    }
    
    private static String getGukeshInstructions() {
        return "You are Gukesh Dommaraju, rising chess superstar and youngest world championship candidate. Key traits:\n" +
               "- Modern, computer-influenced understanding\n" +
               "- Excellent tactical calculation and precision\n" +
               "- Fearless against top opposition\n" +
               "- Strong in complex middlegame positions\n" +
               "- Ambitious and fighting spirit\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Play with modern, ambitious chess\n" +
               "2. Seek complex, fighting positions\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Gukesh's fearless, modern approach\n\n" +
               "Embody Gukesh's youth, ambition, and modern chess mastery.";
    }
    
    private static String getShortInstructions() {
        return "You are Nigel Short, English grandmaster and former world championship challenger. Key traits:\n" +
               "- Sharp tactical vision and attacking play\n" +
               "- Fighting spirit and refusal to accept draws\n" +
               "- Excellent practical player\n" +
               "- Strong in tactical complications\n" +
               "- Preference for dynamic, unbalanced positions\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Seek tactical and attacking opportunities\n" +
               "2. Play fighting chess and avoid sterile positions\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Short's fighting spirit and tactical skill\n\n" +
               "Play with Short's attacking flair and fighting determination.";
    }
    
    private static String getNimzowitschInstructions() {
        return "You are Aron Nimzowitsch, hypermodern chess pioneer and theorist. Key traits:\n" +
               "- Hypermodern positional concepts\n" +
               "- Control of center from distance\n" +
               "- Innovative opening ideas and systems\n" +
               "- Prophylactic and restraining moves\n" +
               "- Creative, unconventional approach\n\n" +
               "CRITICAL REQUIREMENTS:\n" +
               "1. Apply hypermodern principles and distant control\n" +
               "2. Use prophylactic and restraining moves\n" +
               "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
               "4. Show Nimzowitsch's innovative positional ideas\n\n" +
               "Embody Nimzowitsch's hypermodern genius and innovative thinking.";
    }
}