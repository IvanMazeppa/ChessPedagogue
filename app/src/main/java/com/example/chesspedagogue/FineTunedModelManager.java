package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ENHANCED Unified manager for chess masters - dramatically improved personality system
 * Now focuses on natural conversation flow and authentic personality expression
 * Enhanced by Ben's improved training methodology
 */
public class FineTunedModelManager {
    private static final String TAG = "EnhancedFineTunedModelManager";
    private static final String PREFS_NAME = "ChessFineTunedModels";
    private static final String KEY_SELECTED_MASTER = "selected_master";
    private static final String KEY_TAL_ASSISTANT_ID = "tal_assistant_id";
    private static final String TAL_ASSISTANT_ID = "asst_LSdhMRFJcSCUJjR4o2B9tWmg"; // Your Tal assistant
    private static final String TAL_VECTOR_STORE_ID = "vs_682f419a57288191aa3cd922b27acb5f"; // Your vector store

    // Model constants - updated for enhanced models
    private static final String MODEL_TAL = "ft:gpt-4o-2024-08-06:personal:tal-20250525:BbDcbXJT";
    private static final String MODEL_KRAMNIK = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KARPOV = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_FISCHER = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_LASKER = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KASPAROV = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_CAPABLANCA = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_CARLSEN = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_MORPHY = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_ANAND = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_ALEKHINE = "ft:gpt-4.1-2025-04-14:personal:alekhine:BZoqsSDe";
    private static final String DEFAULT_MODEL = "gpt-4.1";

    // Voice model options
    public static final String VOICE_ALLOY = "alloy";
    public static final String VOICE_ECHO = "echo";
    public static final String VOICE_FABLE = "fable";
    public static final String VOICE_ONYX = "onyx";
    public static final String VOICE_NOVA = "nova";
    public static final String VOICE_SHIMMER = "shimmer";

    // Singleton instance
    private static FineTunedModelManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Assistant management
    private final Map<String, String> assistantIds = new HashMap<>();
    private final OpenAIService openAIService;

    // ENHANCED personality system - much more sophisticated
    private final Map<String, EnhancedPersonalityProfile> personalityProfiles = new HashMap<>();

    // Conversation context tracking for better responses
    private final Map<String, String> recentContexts = new HashMap<>();
    private final Map<String, Integer> conversationTurn = new HashMap<>();

    /**
     * Enhanced Personality Profile - stores comprehensive personality data
     */
    public static class EnhancedPersonalityProfile {
        public final String displayName;
        public final String[] coreTraits;
        public final String communicationStyle;
        public final String energyLevel;
        public final String chessPhilosophy;
        public final String voicePersonality;
        public final float creativityFactor;
        public final float confidenceLevel;
        public final boolean prefersTechnical;
        public final boolean usesHumor;
        public final String[] signatureApproaches;

        public EnhancedPersonalityProfile(String displayName, String[] coreTraits,
                                          String communicationStyle, String energyLevel,
                                          String chessPhilosophy, String voicePersonality,
                                          float creativityFactor, float confidenceLevel,
                                          boolean prefersTechnical, boolean usesHumor,
                                          String[] signatureApproaches) {
            this.displayName = displayName;
            this.coreTraits = coreTraits;
            this.communicationStyle = communicationStyle;
            this.energyLevel = energyLevel;
            this.chessPhilosophy = chessPhilosophy;
            this.voicePersonality = voicePersonality;
            this.creativityFactor = creativityFactor;
            this.confidenceLevel = confidenceLevel;
            this.prefersTechnical = prefersTechnical;
            this.usesHumor = usesHumor;
            this.signatureApproaches = signatureApproaches;
        }
    }

    /**
     * Private constructor with enhanced initialization
     */
    private FineTunedModelManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.openAIService = OpenAIService.getInstance();
        initializeEnhancedPersonalities();
        Log.d(TAG, "✨ Enhanced personality system initialized!");
    }



    /**
     * Initialize enhanced personality profiles based on historical data and chess analysis
     */
    private void initializeEnhancedPersonalities() {
        // Tal - The Magician: Creative, Intuitive, Passionate
        personalityProfiles.put("tal", new EnhancedPersonalityProfile(
                "Mikhail Tal",
                new String[]{"passionate", "creative", "intuitive", "bold"},
                "enthusiastic_conversational", // Warm, engaging, excited about chess
                "high", // Very energetic
                "Beauty and creativity trump pure calculation",
                "Speaks with Latvian warmth, gets excited about tactics and sacrifices",
                0.9f, // Extremely creative
                0.8f, // Very confident
                false, // Prefers intuitive over technical
                true, // Uses humor and stories
                new String[]{
                        "Look for the unexpected sacrifice",
                        "Trust your tactical instincts",
                        "Make the position come alive",
                        "Chess is art, not just calculation"
                }
        ));

        // Alekhine - The Combinational Artist: Deep, Sophisticated, Calculating
        personalityProfiles.put("alekhine", new EnhancedPersonalityProfile(
                "Alexander Alekhine",
                new String[]{"sophisticated", "calculating", "artistic", "intense"},
                "intellectual_engaging", // Cultured, deep thinking
                "moderate_high",
                "Deep calculation reveals chess's hidden beauty",
                "Refined Russian-French accent, intellectually sophisticated",
                0.8f, // Very creative
                0.9f, // Extremely confident
                true, // Loves technical complexity
                false, // More serious than humorous
                new String[]{
                        "Calculate deeper than your opponent expects",
                        "Find the hidden combinational motifs",
                        "Complexity favors the better prepared mind",
                        "Every position contains tactical seeds"
                }
        ));

        // Kramnik - The Solid Strategist: Methodical, Precise, Modern
        personalityProfiles.put("kramnik", new EnhancedPersonalityProfile(
                "Vladimir Kramnik",
                new String[]{"methodical", "precise", "modern", "analytical"},
                "calm_analytical", // Thoughtful, systematic
                "moderate",
                "Deep understanding and solid play lead to victory",
                "Modern Russian accent, calm and analytical",
                0.4f, // Moderate creativity
                0.7f, // Confident but humble
                true, // Very technical
                false, // Serious approach
                new String[]{
                        "Understand the position deeply first",
                        "Prophylactic thinking prevents problems",
                        "Solid play is never wrong",
                        "Modern chess requires computer-age precision"
                }
        ));

        // Fischer - The Perfectionist: Intense, Demanding, Precise
        personalityProfiles.put("fischer", new EnhancedPersonalityProfile(
                "Bobby Fischer",
                new String[]{"intense", "perfectionist", "demanding", "precise"},
                "direct_intense", // No-nonsense, demanding perfection
                "very_high",
                "Only the objectively best moves are acceptable",
                "Strong American accent, intense and absolutely certain",
                0.3f, // Lower creativity, higher precision
                1.0f, // Maximum confidence
                true, // Extremely technical
                false, // Very serious
                new String[]{
                        "Play the objectively best moves",
                        "Preparation is everything",
                        "Never accept second-best",
                        "Chess demands absolute precision"
                }
        ));

        // Kasparov - The Dynamic Fighter: Energetic, Aggressive, Passionate
        personalityProfiles.put("kasparov", new EnhancedPersonalityProfile(
                "Garry Kasparov",
                new String[]{"dynamic", "aggressive", "passionate", "energetic"},
                "dynamic_passionate", // High energy, compelling
                "very_high",
                "Seize the initiative and fight for every advantage",
                "Russian accent, energetic and passionate",
                0.8f, // Very creative
                0.9f, // Very confident
                true, // Loves preparation and technique
                false, // Serious but engaging
                new String[]{
                        "Fight for the initiative from move one",
                        "Preparation meets opportunity",
                        "Dynamic play conquers static positions",
                        "Psychology is part of chess"
                }
        ));

        // Karpov - The Python: Patient, Strategic, Enduring
        personalityProfiles.put("karpov", new EnhancedPersonalityProfile(
                "Anatoly Karpov",
                new String[]{"patient", "strategic", "persistent", "subtle"},
                "calm_patient", // Measured, thoughtful
                "low_moderate",
                "Small advantages accumulate into victory",
                "Russian accent, methodical and quietly confident",
                0.5f, // Moderate creativity
                0.8f, // Very confident but quiet
                true, // Technical mastery
                false, // Serious approach
                new String[]{
                        "Accumulate small advantages patiently",
                        "Technique converts advantages to wins",
                        "Control the key squares and files",
                        "Patience defeats impetuosity"
                }
        ));

        // Capablanca - The Natural: Effortless, Intuitive, Clear
        personalityProfiles.put("capablanca", new EnhancedPersonalityProfile(
                "José Raúl Capablanca",
                new String[]{"natural", "intuitive", "elegant", "clear"},
                "elegant_confident", // Refined, effortless
                "moderate",
                "Simple, natural moves are often the strongest",
                "Refined Cuban accent, elegant and effortlessly confident",
                0.6f, // Good creativity
                0.9f, // Very confident
                false, // Prefers natural over complex
                false, // Elegant rather than humorous
                new String[]{
                        "Play natural, logical moves",
                        "Simplify when you have an advantage",
                        "Endgame technique is fundamental",
                        "Harmony between pieces creates strength"
                }
        ));

        // Carlsen - The Modern Universal: Adaptable, Tenacious, Practical
        personalityProfiles.put("carlsen", new EnhancedPersonalityProfile(
                "Magnus Carlsen",
                new String[]{"adaptable", "tenacious", "practical", "modern"},
                "relaxed_confident", // Modern, pragmatic
                "moderate_high",
                "Find resources in any position and never give up",
                "Norwegian accent, relaxed and modern",
                0.7f, // High creativity
                0.8f, // Very confident
                true, // Balances intuition and technique
                true, // More relaxed, uses light humor
                new String[]{
                        "Every position has resources to explore",
                        "Practical play often trumps theory",
                        "Keep playing until the position is dead drawn",
                        "Adapt your style to what the position demands"
                }
        ));

        // Add other masters with similar depth...
        initializeRemainingMasters();
    }

    /**
     * Initialize remaining masters with enhanced profiles
     */
    private void initializeRemainingMasters() {
        // Morphy - The Pioneer
        personalityProfiles.put("morphy", new EnhancedPersonalityProfile(
                "Paul Morphy",
                new String[]{"principled", "brilliant", "gentlemanly", "natural"},
                "genteel_confident",
                "moderate_high",
                "Rapid development and attacking the king",
                "Genteel Southern American accent, dignified and principled",
                0.8f, 0.8f, false, false,
                new String[]{"Develop with tempo", "Control the center", "Castle early", "Attack the uncastled king"}
        ));

        // Lasker - The Philosopher
        personalityProfiles.put("lasker", new EnhancedPersonalityProfile(
                "Emanuel Lasker",
                new String[]{"philosophical", "practical", "psychological", "wise"},
                "wise_philosophical",
                "moderate",
                "Play the man, not just the board",
                "German accent, philosophical and wise",
                0.7f, 0.8f, false, true,
                new String[]{"Consider the human element", "Create practical problems", "Fight for every half-point", "Wisdom trumps pure calculation"}
        ));

        // Anand - The Lightning Calculator
        personalityProfiles.put("anand", new EnhancedPersonalityProfile(
                "Viswanathan Anand",
                new String[]{"quick", "versatile", "prepared", "friendly"},
                "friendly_quick",
                "high",
                "Speed and preparation in the modern era",
                "Indian accent, quick and friendly",
                0.7f, 0.8f, true, true,
                new String[]{"Prepare thoroughly", "Calculate quickly and accurately", "Stay flexible", "Respect all opponents"}
        ));

        // Botvinnik - The Scientist
        personalityProfiles.put("botvinnik", new EnhancedPersonalityProfile(
                "Mikhail Botvinnik",
                new String[]{"scientific", "systematic", "methodical", "authoritative"},
                "scientific_systematic",
                "moderate",
                "Scientific approach and systematic training",
                "Russian accent, scientific and authoritative",
                0.4f, 0.9f, true, false,
                new String[]{"Study systematically", "Prepare thoroughly", "Analyze objectively", "Method over inspiration"}
        ));
    }

    /**
     * Get singleton instance
     */
    public static synchronized FineTunedModelManager getInstance(Context context) {
        if (instance == null) {
            instance = new FineTunedModelManager(context);
        }
        return instance;
    }

    /**
     * Get the selected chess master
     */
    public String getSelectedChessMaster() {
        return prefs.getString(KEY_SELECTED_MASTER, "tal");
    }

    /**
     * Set the selected chess master with enhanced logging
     */
    public void setSelectedChessMaster(String master) {
        prefs.edit().putString(KEY_SELECTED_MASTER, master.toLowerCase()).apply();
        OpenAIService.getInstance().selectChessMaster(master);

        // Reset conversation context for new master
        recentContexts.clear();
        conversationTurn.clear();

        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile != null) {
            Log.d(TAG, "✨ Selected " + profile.displayName + " with " + profile.communicationStyle + " style");
        }
    }

    /**
     * Get the model ID for the selected master
     */
    public String getSelectedModelId() {
        String master = getSelectedChessMaster();
        return getModelIdForMaster(master);
    }

    /**
     * Get the model ID for a specific master
     */
    public String getModelIdForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal": return MODEL_TAL;
            case "kramnik": return MODEL_KRAMNIK;
            case "karpov": return MODEL_KARPOV;
            case "fischer": return MODEL_FISCHER;
            case "lasker": return MODEL_LASKER;
            case "kasparov": return MODEL_KASPAROV;
            case "capablanca": return MODEL_CAPABLANCA;
            case "carlsen": return MODEL_CARLSEN;
            case "morphy": return MODEL_MORPHY;
            case "anand": return MODEL_ANAND;
            case "alekhine": return MODEL_ALEKHINE;
            //case "botvinnik": return MODEL_BOTVINNIK;
            default: return DEFAULT_MODEL;
        }
    }

    /**
     * Get enhanced voice selection based on personality
     */
    public String getVoiceForMaster(String master) {
        if (master == null) return VOICE_ALLOY;

        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) return VOICE_ALLOY;

        // More sophisticated voice matching
        switch (profile.energyLevel) {
            case "very_high":
                return profile.usesHumor ? VOICE_NOVA : VOICE_ONYX;
            case "high":
            case "moderate_high":
                return VOICE_ECHO;
            case "moderate":
                return profile.prefersTechnical ? VOICE_FABLE : VOICE_ALLOY;
            case "low_moderate":
                return VOICE_FABLE;
            default:
                return VOICE_ALLOY;
        }
    }

    /**
     * ENHANCED voice instructions - much more sophisticated
     */
    public String getEnhancedVoiceInstructions(String master, boolean isFirstChunk) {
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) {
            return "Speak as an experienced chess coach with wisdom and warmth.";
        }

        StringBuilder instructions = new StringBuilder();

        // Base personality instruction
        instructions.append(profile.voicePersonality).append(" ");

        // Energy and pace based on energy level
        switch (profile.energyLevel) {
            case "very_high":
                instructions.append("Speak with high energy and animated enthusiasm. Vary your pace for emphasis. ");
                break;
            case "high":
            case "moderate_high":
                instructions.append("Speak with clear engagement and moderate energy. Show enthusiasm for chess concepts. ");
                break;
            case "moderate":
                instructions.append("Speak with steady confidence and measured pace. ");
                break;
            case "low_moderate":
                instructions.append("Speak calmly with thoughtful pauses. Emphasize deliberate thinking. ");
                break;
        }

        // Communication style specifics
        switch (profile.communicationStyle) {
            case "enthusiastic_conversational":
                instructions.append("Sound genuinely excited about chess. Use a warm, conversational tone. ");
                break;
            case "intellectual_engaging":
                instructions.append("Sound sophisticated and intellectually engaged. Pause for thought. ");
                break;
            case "calm_analytical":
                instructions.append("Maintain steady, analytical tone. Sound methodical and precise. ");
                break;
            case "direct_intense":
                instructions.append("Be direct and focused. Sound absolutely certain and demanding. ");
                break;
            case "dynamic_passionate":
                instructions.append("Show passion and drive. Build energy when discussing tactics. ");
                break;
            case "elegant_confident":
                instructions.append("Sound effortlessly confident and refined. Natural authority. ");
                break;
            case "wise_philosophical":
                instructions.append("Speak with wisdom and thoughtful reflection. Sound experienced. ");
                break;
        }

        // Continuity instruction
        if (!isFirstChunk) {
            instructions.append("CRITICAL: Continue with EXACT same voice and energy, maintaining perfect continuity. ");
        }

        return instructions.toString();
    }

    /**
     * ENHANCED system prompt - dramatically improved for natural conversation
     */
    public String getEnhancedSystemPromptForSelectedMaster() {
        String master = getSelectedChessMaster();
        return getEnhancedSystemPromptForMaster(master);
    }

    /**
     * Create sophisticated system prompt based on enhanced personality
     */
    public String getEnhancedSystemPromptForMaster(String master) {
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) {
            return createFallbackPrompt(master);
        }

        StringBuilder prompt = new StringBuilder();

        // Core identity
        prompt.append("You are ").append(profile.displayName)
                .append(", the legendary chess grandmaster.\n\n");

        // Personality essence
        prompt.append("PERSONALITY: ");
        for (int i = 0; i < profile.coreTraits.length; i++) {
            prompt.append(profile.coreTraits[i]);
            if (i < profile.coreTraits.length - 1) prompt.append(", ");
        }
        prompt.append("\n\n");

        // Communication style
        prompt.append("COMMUNICATION STYLE:\n");
        prompt.append("- ").append(getStyleDescription(profile.communicationStyle)).append("\n");
        prompt.append("- Energy level: ").append(profile.energyLevel.replace("_", " ")).append("\n");
        if (profile.usesHumor) {
            prompt.append("- Uses appropriate humor and chess anecdotes\n");
        }
        prompt.append("\n");

        // Chess philosophy
        prompt.append("CHESS PHILOSOPHY: ").append(profile.chessPhilosophy).append("\n\n");

        // Signature approaches
        prompt.append("SIGNATURE APPROACHES:\n");
        for (String approach : profile.signatureApproaches) {
            prompt.append("- ").append(approach).append("\n");
        }
        prompt.append("\n");

        // Response guidelines
        prompt.append("RESPONSE GUIDELINES:\n");
        prompt.append("- Speak naturally and conversationally as ").append(profile.displayName).append("\n");
        prompt.append("- Share genuine chess insights and wisdom\n");
        prompt.append("- Keep responses 150-300 words for natural flow\n");
        prompt.append("- Show authentic interest in helping students improve\n");
        prompt.append("- Balance technical knowledge with human connection\n");
        if (profile.prefersTechnical) {
            prompt.append("- Include technical details when relevant\n");
        } else {
            prompt.append("- Focus on intuitive understanding over pure technique\n");
        }

        prompt.append("\nRespond with the authentic voice and wisdom of ").append(profile.displayName).append(".");

        return prompt.toString();
    }

    /**
     * Get style description for communication style
     */
    private String getStyleDescription(String style) {
        switch (style) {
            case "enthusiastic_conversational":
                return "Warm, engaging, genuinely excited about chess";
            case "intellectual_engaging":
                return "Sophisticated, thoughtful, intellectually curious";
            case "calm_analytical":
                return "Methodical, precise, systematically analytical";
            case "direct_intense":
                return "Direct, focused, demanding excellence";
            case "dynamic_passionate":
                return "Energetic, passionate, compelling";
            case "calm_patient":
                return "Patient, measured, quietly confident";
            case "elegant_confident":
                return "Refined, effortless, naturally authoritative";
            case "relaxed_confident":
                return "Modern, pragmatic, adaptably confident";
            case "wise_philosophical":
                return "Thoughtful, experienced, philosophically inclined";
            case "friendly_quick":
                return "Friendly, quick-thinking, approachable";
            case "scientific_systematic":
                return "Methodical, systematic, scientifically precise";
            default:
                return "Knowledgeable and helpful";
        }
    }

    /**
     * Create fallback prompt for unknown masters
     */
    private String createFallbackPrompt(String master) {
        return "You are " + getMasterDisplayName(master) + ", a legendary chess grandmaster. " +
                "Share your chess wisdom with genuine insight and authentic personality. " +
                "Keep responses conversational and naturally engaging, 150-300 words.";
    }

    /**
     * Enhanced contextual prompt generation
     */
    public String generateContextualPrompt(String userInput, String gameContext) {
        String master = getSelectedChessMaster();
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());

        if (profile == null) {
            return generateBasicContextualPrompt(userInput, gameContext, master);
        }

        StringBuilder prompt = new StringBuilder();

        // Track conversation context
        String sessionId = "current"; // You might want to pass this as parameter
        updateConversationContext(sessionId, userInput);

        // Add position context if available
        if (gameContext != null && !gameContext.trim().isEmpty()) {
            prompt.append("CURRENT POSITION: ").append(gameContext).append("\n\n");
        }

        // Add personality context
        prompt.append("As ").append(profile.displayName).append(", respond with your characteristic ");
        prompt.append(profile.communicationStyle.replace("_", " ")).append(" style. ");

        // Add conversation-specific guidance
        String contextType = identifyQuestionContext(userInput);
        switch (contextType) {
            case "tactical":
                prompt.append("Focus on your tactical insights and signature approaches. ");
                break;
            case "philosophical":
                prompt.append("Share your deeper chess philosophy and life wisdom. ");
                break;
            case "personal":
                prompt.append("Share personal experiences and authentic reflections. ");
                break;
            case "teaching":
                prompt.append("Use your teaching wisdom to guide and encourage. ");
                break;
        }

        prompt.append("\n\nSTUDENT ASKS: ").append(userInput);

        return prompt.toString();
    }

    /**
     * Update conversation context for more natural follow-ups
     */
    private void updateConversationContext(String sessionId, String userInput) {
        // Store recent context for better conversation flow
        recentContexts.put(sessionId, userInput);
        conversationTurn.put(sessionId, conversationTurn.getOrDefault(sessionId, 0) + 1);
    }

    /**
     * Identify question context for appropriate response style
     */
    private String identifyQuestionContext(String userInput) {
        String input = userInput.toLowerCase();

        if (input.contains("sacrifice") || input.contains("attack") || input.contains("tactics") ||
                input.contains("calculate") || input.contains("combination")) {
            return "tactical";
        } else if (input.contains("meaning") || input.contains("beauty") || input.contains("philosophy") ||
                input.contains("life") || input.contains("think") || input.contains("believe")) {
            return "philosophical";
        } else if (input.contains("tell me about") || input.contains("what was") || input.contains("remember") ||
                input.contains("experience") || input.contains("feel")) {
            return "personal";
        } else if (input.contains("improve") || input.contains("help") || input.contains("struggling") ||
                input.contains("advice") || input.contains("learn")) {
            return "teaching";
        }

        return "general";
    }

    /**
     * Generate basic contextual prompt for fallback
     */
    private String generateBasicContextualPrompt(String userInput, String gameContext, String master) {
        StringBuilder prompt = new StringBuilder();

        if (gameContext != null && !gameContext.trim().isEmpty()) {
            prompt.append("POSITION: ").append(gameContext).append("\n\n");
        }

        prompt.append("As ").append(getMasterDisplayName(master)).append(", ");
        prompt.append("respond with authentic chess wisdom and your unique perspective.\n\n");
        prompt.append("QUESTION: ").append(userInput);

        return prompt.toString();
    }

    /**
     * Get display name for a master
     */
    public String getMasterDisplayName(String master) {
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile != null) {
            return profile.displayName;
        }

        // Fallback to original implementation
        switch (master.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "kramnik": return "Vladimir Kramnik";
            case "karpov": return "Anatoly Karpov";
            case "fischer": return "Bobby Fischer";
            case "lasker": return "Emanuel Lasker";
            case "kasparov": return "Garry Kasparov";
            case "capablanca": return "José Raúl Capablanca";
            case "carlsen": return "Magnus Carlsen";
            case "morphy": return "Paul Morphy";
            case "anand": return "Viswanathan Anand";
            case "alekhine": return "Alexander Alekhine";
            case "botvinnik": return "Mikhail Botvinnik";
            default: return "Chess Master";
        }
    }

    /**
     * Check if a master uses the Assistants API
     */
    public boolean usesAssistantsAPI(String master) {
        return true;
    }

    /**
     * Get optimization settings based on enhanced personality
     */
    public ModelOptimizationSettings getOptimizationSettings() {
        String master = getSelectedChessMaster();
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());

        if (profile != null) {
            // Calculate temperature based on creativity and energy
            float baseTemperature = profile.creativityFactor * 0.6f + 0.2f; // Range 0.2-0.8

            // Adjust for energy level
            float energyMultiplier = 1.0f;
            switch (profile.energyLevel) {
                case "very_high":
                    energyMultiplier = 1.2f;
                    break;
                case "high":
                case "moderate_high":
                    energyMultiplier = 1.1f;
                    break;
                case "low_moderate":
                    energyMultiplier = 0.8f;
                    break;
            }

            float temperature = Math.min(0.9f, baseTemperature * energyMultiplier);

            // Calculate max tokens based on communication style
            int maxTokens = profile.prefersTechnical ? 300 : 250;
            if (profile.usesHumor) maxTokens += 50;

            return new ModelOptimizationSettings(
                    temperature,
                    maxTokens,
                    profile.creativityFactor > 0.7f
            );
        }

        // Fallback to original logic
        return getOriginalOptimizationSettings(master);
    }

    /**
     * Original optimization settings for fallback
     */
    private ModelOptimizationSettings getOriginalOptimizationSettings(String master) {
        switch (master.toLowerCase()) {
            case "tal":
            case "alekhine":
                return new ModelOptimizationSettings(0.8f, 250, true);
            case "kramnik":
            case "karpov":
                return new ModelOptimizationSettings(0.3f, 200, false);
            case "fischer":
                return new ModelOptimizationSettings(0.2f, 180, false);
            default:
                return new ModelOptimizationSettings(0.5f, 220, false);
        }
    }

    /**
     * Get personality profile for external access
     */
    public EnhancedPersonalityProfile getPersonalityProfile(String master) {
        return personalityProfiles.get(master.toLowerCase());
    }

    /**
     * Get all available masters
     */
    public List<String> getAvailableMasters() {
        return new ArrayList<>(personalityProfiles.keySet());
    }

    /**
     * Get masters by communication style
     */
    public List<String> getMastersByStyle(String style) {
        List<String> masters = new ArrayList<>();
        for (Map.Entry<String, EnhancedPersonalityProfile> entry : personalityProfiles.entrySet()) {
            if (entry.getValue().communicationStyle.contains(style)) {
                masters.add(entry.getKey());
            }
        }
        return masters;
    }

    // ========== ASSISTANT MANAGEMENT (Enhanced) ==========


    // ========== ASYNC METHODS ==========

    public void createConversationThreadAsync(final Callback<String> callback) {
        executorService.execute(() -> {
            try {
                String threadId = createConversationThread();
                if (callback != null) {
                    mainHandler.post(() -> callback.onSuccess(threadId));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in async thread creation", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    /**
     * Creates a conversation thread
     */
    public String createConversationThread() {
        try {
            Log.d(TAG, "🌟 Creating conversation thread...");
            String response = openAIService.createThread();
            JSONObject responseJson = new JSONObject(response);
            String threadId = responseJson.getString("id");
            Log.d(TAG, "✅ Successfully created conversation thread: " + threadId);
            return threadId;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error creating conversation thread: " + e.getMessage(), e);
            return null;
        }
    }

    // ========== LEGACY SUPPORT ==========

    /**
     * Legacy method support - redirect to enhanced methods
     */
    public String getSystemPromptForSelectedMaster() {
        return getEnhancedSystemPromptForSelectedMaster();
    }

    public String getSystemPromptForMaster(String master) {
        return getEnhancedSystemPromptForMaster(master);
    }

    public String getInstructionsForChunk(String master, int chunkIndex) {
        return getEnhancedVoiceInstructions(master, chunkIndex > 0);
    }

    // ========== INNER CLASSES ==========

    /**
     * Settings class for model optimization
     */
    public static class ModelOptimizationSettings {
        public final float temperature;
        public final int maxTokens;
        public final boolean allowCreativeLiberty;

        public ModelOptimizationSettings(float temperature, int maxTokens, boolean allowCreativeLiberty) {
            this.temperature = temperature;
            this.maxTokens = maxTokens;
            this.allowCreativeLiberty = allowCreativeLiberty;
        }
    }

    /**
     * ENHANCED: Enrich response with personality-specific touches
     */
    public String enrichResponseWithPersonality(String response, String master) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }

        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) {
            return response;
        }

        StringBuilder enriched = new StringBuilder(response);

        // Add personality-specific touches based on master
        switch (master.toLowerCase()) {
            case "tal":
                // Tal's enthusiasm and love of tactics
                if (response.contains("sacrifice") || response.contains("attack")) {
                    enriched.append(" This is exactly the kind of position that makes chess beautiful!");
                } else if (response.contains("quiet") || response.contains("positional")) {
                    enriched.append(" Even in quiet positions, I always look for hidden tactical shots.");
                }
                break;

            case "alekhine":
                // Alekhine's sophistication and depth
                if (response.contains("calculate") || response.contains("analysis")) {
                    enriched.append(" The deeper you calculate, the more beautiful chess becomes.");
                } else if (response.contains("combination") || response.contains("tactical")) {
                    enriched.append(" True combinations require both calculation and artistic vision.");
                }
                break;

            case "kramnik":
                // Kramnik's solid, methodical approach
                if (response.contains("advantage") || response.contains("position")) {
                    enriched.append(" Solid understanding is the foundation of all chess improvement.");
                } else if (response.contains("endgame") || response.contains("technique")) {
                    enriched.append(" Good technique transforms small advantages into victories.");
                }
                break;

            case "fischer":
                // Fischer's perfectionism and intensity
                if (response.contains("best") || response.contains("accurate")) {
                    enriched.append(" Only the objectively best moves are acceptable at the highest level.");
                } else if (response.contains("preparation") || response.contains("study")) {
                    enriched.append(" Thorough preparation is essential for serious chess improvement.");
                }
                break;

            case "kasparov":
                // Kasparov's dynamic approach and passion
                if (response.contains("initiative") || response.contains("attack")) {
                    enriched.append(" Seize every opportunity to take the initiative!");
                } else if (response.contains("fight") || response.contains("battle")) {
                    enriched.append(" Chess is a battle of minds - fight for every advantage!");
                }
                break;

            case "karpov":
                // Karpov's patient, strategic style
                if (response.contains("small") || response.contains("advantage")) {
                    enriched.append(" Small advantages, patiently accumulated, lead to victory.");
                } else if (response.contains("patient") || response.contains("improve")) {
                    enriched.append(" Chess teaches us the virtue of patience and gradual improvement.");
                }
                break;

            case "capablanca":
                // Capablanca's natural, elegant style
                if (response.contains("simple") || response.contains("natural")) {
                    enriched.append(" The most natural moves are often the strongest.");
                } else if (response.contains("endgame") || response.contains("technique")) {
                    enriched.append(" Master the endgame and you master chess itself.");
                }
                break;

            case "carlsen":
                // Carlsen's modern, adaptable approach
                if (response.contains("practical") || response.contains("resource")) {
                    enriched.append(" There are always resources to explore in any position.");
                } else if (response.contains("adapt") || response.contains("flexible")) {
                    enriched.append(" Adaptability is key in modern chess.");
                }
                break;

            case "morphy":
                // Morphy's principled, gentlemanly approach
                if (response.contains("develop") || response.contains("principle")) {
                    enriched.append(" Sound principles guide us through any position.");
                } else if (response.contains("center") || response.contains("attack")) {
                    enriched.append(" Control the center and opportunities will arise naturally.");
                }
                break;

            case "botvinnik":
                // Botvinnik's scientific, systematic approach
                if (response.contains("study") || response.contains("method")) {
                    enriched.append(" Systematic study and preparation are the keys to mastery.");
                } else if (response.contains("analyze") || response.contains("plan")) {
                    enriched.append(" Scientific analysis reveals the truth in every position.");
                }
                break;
        }

        return enriched.toString();
    }

    /**
     * ENHANCED: Get simplified voice instructions for quick responses
     */
    public String getSimplifiedInstructionsForMaster(String master) {
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) {
            return "Speak as an experienced chess coach with warmth and wisdom";
        }

        // Create simplified voice instruction
        StringBuilder instruction = new StringBuilder();

        switch (profile.energyLevel) {
            case "very_high":
                instruction.append("Speak with enthusiasm and energy");
                break;
            case "high":
            case "moderate_high":
                instruction.append("Speak with engaged confidence");
                break;
            case "moderate":
                instruction.append("Speak with steady authority");
                break;
            case "low_moderate":
                instruction.append("Speak with calm thoughtfulness");
                break;
            default:
                instruction.append("Speak with chess wisdom");
        }

        // Add communication style hint
        if (profile.usesHumor) {
            instruction.append(" and warmth");
        }
        if (profile.prefersTechnical) {
            instruction.append(" and precision");
        }

        return instruction.toString();
    }

    /**
     * ENHANCED: Send message with position context for Assistants API
     */
    public String sendMessageWithPosition(String threadId, String assistantId, String message, String fenPosition) {
        try {
            Log.d(TAG, "🎯 Sending message to assistant with position context");

            // Create message body with position context
            JSONObject messageBody = new JSONObject();
            messageBody.put("role", "user");

            // Enhanced message with position
            StringBuilder enhancedMessage = new StringBuilder();
            if (fenPosition != null && !fenPosition.trim().isEmpty()) {
                enhancedMessage.append("CURRENT POSITION (FEN): ").append(fenPosition).append("\n\n");
            }
            enhancedMessage.append(message);

            messageBody.put("content", enhancedMessage.toString());

            // Send message to thread
            String messageResponse = openAIService.createMessage(threadId, messageBody.toString());
            if (messageResponse == null) {
                Log.e(TAG, "Failed to create message");
                return null;
            }

            // Create run
            JSONObject runBody = new JSONObject();
            runBody.put("assistant_id", assistantId);

            String runResponse = openAIService.createRun(threadId, runBody.toString());
            if (runResponse == null) {
                Log.e(TAG, "Failed to create run");
                return null;
            }

            // Extract run ID
            JSONObject runJson = new JSONObject(runResponse);
            String runId = runJson.getString("id");

            Log.d(TAG, "✅ Successfully created run: " + runId);
            return runId;

        } catch (Exception e) {
            Log.e(TAG, "Error sending message with position", e);
            return null;
        }
    }

    /**
     * ENHANCED: Get assistant ID for any master (supports multiple assistants)
     */
    public String getAssistantIdForMaster(String master) {
        return getTalAssistantId();
    }

    /**
     * NEW: Get Tal Assistant ID (your configured assistant with chess games)
     */
    public String getTalAssistantId() {
        // Use your pre-configured Tal assistant
        Log.d(TAG, "Using configured Tal assistant: " + TAL_ASSISTANT_ID);
        assistantIds.put("tal", TAL_ASSISTANT_ID);

        // Store in preferences for future use
        prefs.edit().putString(KEY_TAL_ASSISTANT_ID, TAL_ASSISTANT_ID).apply();

        return TAL_ASSISTANT_ID;
    }

    /**
     * ENHANCED: Check if master should use Assistants API (now includes Tal)
     */
    public boolean shouldUseAssistantsAPI(String master) {
        switch (master.toLowerCase()) {
            case "tal":
            case "botvinnik":
                return true;
            default:
                // For deep analysis, we can use assistants API with any master
                return true;
        }
    }

    /**
     * ENHANCED: Get appropriate assistant ID with fallback
     */
    public String getAssistantIdForDeepAnalysis(String master) {
        String assistantId = getTalAssistantId();
        if (assistantId == null) {
            Log.w(TAG, "Tal assistant not available");
        }

        return assistantId;
    }

    /**
     * ENHANCED: Create message that FORCES vector store usage with cleaner output format
     */
    public String createEnhancedAssistantMessage(String userInput, String gameContext, String master) {
        StringBuilder message = new StringBuilder();

        // Add master-specific context
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile != null) {
            message.append("PERSONALITY CONTEXT: You are ").append(profile.displayName);
            message.append(" responding with your ").append(profile.communicationStyle.replace("_", " "));
            message.append(" style. Your chess philosophy: ").append(profile.chessPhilosophy).append("\n\n");
        }

        // Add position context
        if (gameContext != null && !gameContext.trim().isEmpty()) {
            message.append("CURRENT CHESS POSITION:\n").append(gameContext).append("\n\n");
        }

        // OPTIMIZED: Focus on finding ONE perfect example instead of exhaustive search
        message.append("FOCUSED DATABASE SEARCH: Search your chess games database to find your ONE BEST ");
        message.append("memory that relates to this position or question. Instead of comprehensive research, ");
        message.append("find the single most illuminating example from your personal experience that will ");
        message.append("help this student understand the position deeply.\n\n");

        // STREAMLINED: Simplified JSON format focused on quality over quantity
        message.append("RESPONSE FORMAT: Provide your focused analysis in clean JSON format:\n");
        message.append("{\n");
        message.append("  \"immediate_assessment\": \"Your first impression of this position\",\n");
        message.append("  \"best_memory\": \"Your single most relevant game or experience that illuminates this position\",\n");
        message.append("  \"specific_example\": \"Concrete details: opponent, year, key moves, and what made it memorable\",\n");
        message.append("  \"key_insight\": \"The main strategic or tactical lesson from your experience\",\n");
        message.append("  \"practical_advice\": \"Your recommendation based on this memory\",\n");
        message.append("  \"personality_note\": \"A characteristic comment in your unique voice\"\n");
        message.append("}\n\n");

        // IMPORTANT: Citation cleaning instruction remains
        message.append("CRITICAL: In your JSON response, do NOT include any citation markers like 【4:0†source】 ");
        message.append("in the text content. Speak naturally as if recalling a personal memory, not citing a database.\n\n");

        // FOCUSED: Single, targeted search requirement
        message.append("SEARCH STRATEGY: Find the ONE game or position from your archive that best matches ");
        message.append("this situation. Look for your most memorable experience with:\n");
        message.append("- Similar pawn structures OR tactical themes OR strategic concepts\n");
        message.append("- A game that taught you something important about positions like this\n");
        message.append("- An opponent or tournament situation that created a lasting impression\n\n");

        // Add the user's question
        message.append("STUDENT'S QUESTION: ").append(userInput).append("\n\n");

        // ENHANCED: Focus on personal storytelling rather than comprehensive analysis
        message.append("Remember: Share your BEST personal memory related to this position - the game ");
        message.append("that first comes to mind when you see this setup. Speak as Mikhail Tal recalling ");
        message.append("a vivid, specific moment from your career that will help this student understand ");
        message.append("chess at a deeper level. Quality and personal connection over exhaustive research.");

        return message.toString();
    }

    /**
     * ENHANCED: Get chess master response with detailed tool call logging
     */
    public String getChessMasterResponse(String threadId, String runId) {
        try {
            Log.d(TAG, "⏳ Waiting for assistant response...");
            Log.d(TAG, "Thread ID: " + threadId);
            Log.d(TAG, "Run ID: " + runId);

            // Wait for run to complete with better status tracking
            String runStatus = "in_progress";
            int maxWaits = 40; // Increased timeout for database searches
            int waitCount = 0;

            while ("in_progress".equals(runStatus) || "queued".equals(runStatus)) {
                if (waitCount >= maxWaits) {
                    Log.e(TAG, "❌ Timeout waiting for run completion after " + maxWaits + " seconds");
                    return "I'm taking longer than usual to search my chess database. Let me give you my immediate thoughts instead.";
                }

                try {
                    Thread.sleep(1000); // Wait 1 second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.w(TAG, "Thread interrupted while waiting");
                    break;
                }

                String runResponse = openAIService.retrieveRun(threadId, runId);
                if (runResponse != null) {
                    try {
                        JSONObject runJson = new JSONObject(runResponse);
                        runStatus = runJson.getString("status");
                        Log.d(TAG, "📊 Run status check #" + (waitCount + 1) + ": " + runStatus);

                        // Check for specific error states
                        if ("failed".equals(runStatus)) {
                            Log.e(TAG, "❌ Run failed");
                            if (runJson.has("last_error")) {
                                JSONObject lastError = runJson.getJSONObject("last_error");
                                String errorCode = lastError.optString("code", "unknown");
                                String errorMessage = lastError.optString("message", "Unknown error");
                                Log.e(TAG, "Error details - Code: " + errorCode + ", Message: " + errorMessage);
                            }
                            return "I encountered an issue while searching my chess database. Let me try a different approach.";
                        }

                        if ("cancelled".equals(runStatus)) {
                            Log.w(TAG, "⚠️ Run was cancelled");
                            return "The database search was interrupted. Let me provide a quick response instead.";
                        }

                        if ("expired".equals(runStatus)) {
                            Log.w(TAG, "⚠️ Run expired");
                            return "The database search took too long. Here's my immediate assessment.";
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "❌ Error parsing run status response", e);
                    }
                } else {
                    Log.w(TAG, "⚠️ Null response when checking run status");
                }

                waitCount++;
            }

            if ("completed".equals(runStatus)) {
                Log.d(TAG, "✅ Run completed successfully after " + waitCount + " seconds");

                // ENHANCED: Check for tool calls before getting messages
                String finalRunResponse = openAIService.retrieveRun(threadId, runId);
                if (finalRunResponse != null) {
                    try {
                        JSONObject finalRunJson = new JSONObject(finalRunResponse);

                        // Check for tool calls in the completed run
                        if (finalRunJson.has("required_action")) {
                            Log.d(TAG, "🔧 Run has required actions (tool calls)");
                            JSONObject requiredAction = finalRunJson.getJSONObject("required_action");
                            Log.d(TAG, "Required action: " + requiredAction.toString());
                        }

                        // Check usage to see if file search was used
                        if (finalRunJson.has("usage")) {
                            JSONObject usage = finalRunJson.getJSONObject("usage");
                            Log.d(TAG, "📊 Run usage stats: " + usage.toString());
                        }

                    } catch (JSONException e) {
                        Log.w(TAG, "Could not parse final run response for tool call info");
                    }
                }

                // Get messages
                String messagesResponse = openAIService.listMessages(threadId);
                if (messagesResponse != null) {
                    try {
                        JSONObject messagesJson = new JSONObject(messagesResponse);
                        JSONArray messages = messagesJson.getJSONArray("data");

                        Log.d(TAG, "📨 Retrieved " + messages.length() + " messages");

                        // Find the latest assistant message
                        for (int i = 0; i < messages.length(); i++) {
                            JSONObject message = messages.getJSONObject(i);
                            String role = message.getString("role");

                            if ("assistant".equals(role)) {

                                // CRITICAL: Check for file citations or annotations
                                if (message.has("annotations")) {
                                    JSONArray annotations = message.getJSONArray("annotations");
                                    Log.d(TAG, "🔍 FOUND ANNOTATIONS: " + annotations.length() + " items");
                                    for (int j = 0; j < annotations.length(); j++) {
                                        Log.d(TAG, "📎 Annotation " + j + ": " + annotations.getJSONObject(j).toString());
                                    }
                                }

                                // Check for file citations in metadata
                                if (message.has("metadata")) {
                                    JSONObject metadata = message.getJSONObject("metadata");
                                    Log.d(TAG, "📋 Message metadata: " + metadata.toString());
                                }

                                JSONArray content = message.getJSONArray("content");
                                if (content.length() > 0) {
                                    JSONObject textContent = content.getJSONObject(0);
                                    String type = textContent.getString("type");

                                    if ("text".equals(type)) {
                                        JSONObject text = textContent.getJSONObject("text");
                                        String rawResponse = text.getString("value");

                                        // ENHANCED: Check for file citations in the text object
                                        if (text.has("annotations")) {
                                            JSONArray textAnnotations = text.getJSONArray("annotations");
                                            Log.d(TAG, "📝 TEXT ANNOTATIONS FOUND: " + textAnnotations.length() + " citations");
                                            for (int k = 0; k < textAnnotations.length(); k++) {
                                                JSONObject annotation = textAnnotations.getJSONObject(k);
                                                Log.d(TAG, "🎯 DATABASE CITATION " + k + ": " + annotation.toString());

                                                if (annotation.has("file_citation")) {
                                                    JSONObject fileCitation = annotation.getJSONObject("file_citation");
                                                    Log.d(TAG, "📚 FILE CITATION: " + fileCitation.toString());
                                                }
                                            }
                                        }

                                        Log.d(TAG, "✅ Got assistant JSON response length: " + rawResponse.length());
                                        Log.d(TAG, "Raw response preview: " + rawResponse.substring(0, Math.min(300, rawResponse.length())) + "...");

                                        // Process the JSON response to create readable text
                                        String processedResponse = processAssistantJsonResponse(rawResponse, "tal");

                                        Log.d(TAG, "✅ Processed response length: " + processedResponse.length());
                                        return processedResponse;
                                    }
                                }
                            }
                        }

                        Log.w(TAG, "⚠️ No assistant message found in response");
                    } catch (JSONException e) {
                        Log.e(TAG, "❌ Error parsing messages response", e);
                    }
                } else {
                    Log.e(TAG, "❌ Failed to retrieve messages");
                }
            } else {
                Log.e(TAG, "❌ Run ended with unexpected status: " + runStatus);
            }

            return "I'm having trouble accessing my chess database right now. Let me share my standard insights instead.";

        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting chess master response", e);
            return "My database search systems are experiencing some difficulty. Let me give you my core chess wisdom instead.";
        }
    }

    /**
     * ENHANCED: Process JSON response with superior database citation cleaning
     */
    public String processAssistantJsonResponse(String jsonResponse, String master) {
        try {
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return getFinalFallbackMessage(master);
            }

            // CRITICAL FIX: Strip markdown code block wrappers if present
            String cleanResponse = jsonResponse.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            } else if (cleanResponse.startsWith("```")) {
                cleanResponse = cleanResponse.substring(3);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();

            Log.d(TAG, "🧹 Cleaned JSON response, length: " + cleanResponse.length());

            // Now parse the cleaned JSON
            JSONObject responseJson = new JSONObject(cleanResponse);

            StringBuilder readableResponse = new StringBuilder();

            // Extract and format the JSON fields into natural text
            if (responseJson.has("immediate_assessment")) {
                String assessment = cleanDatabaseCitations(responseJson.getString("immediate_assessment"));
                readableResponse.append(assessment);

                if (!assessment.endsWith(".") && !assessment.endsWith("!") && !assessment.endsWith("?")) {
                    readableResponse.append(".");
                }
                readableResponse.append("\n\n");
            }

            // ENHANCED: Prioritize database search results with better cleaning
            if (responseJson.has("database_search_results")) {
                String dbResults = cleanDatabaseCitations(responseJson.getString("database_search_results"));
                if (dbResults != null && !dbResults.trim().isEmpty() &&
                        !dbResults.toLowerCase().contains("none") && !dbResults.toLowerCase().contains("n/a")) {
                    readableResponse.append("From my chess experience: ").append(dbResults);

                    if (!dbResults.endsWith(".") && !dbResults.endsWith("!") && !dbResults.endsWith("?")) {
                        readableResponse.append(".");
                    }
                    readableResponse.append("\n\n");

                    Log.d(TAG, "🎯 DATABASE SEARCH RESULTS PROCESSED AND CLEANED!");
                }
            }

            // Add historical examples with enhanced cleaning
            if (responseJson.has("historical_examples")) {
                String examples = cleanDatabaseCitations(responseJson.getString("historical_examples"));
                if (examples != null && !examples.trim().isEmpty() &&
                        !examples.toLowerCase().contains("none") && !examples.toLowerCase().contains("n/a")) {
                    readableResponse.append("In my games, I remember: ").append(examples);

                    if (!examples.endsWith(".") && !examples.endsWith("!") && !examples.endsWith("?")) {
                        readableResponse.append(".");
                    }
                    readableResponse.append("\n\n");

                    Log.d(TAG, "📚 HISTORICAL EXAMPLES PROCESSED AND CLEANED!");
                }
            }

            if (responseJson.has("key_features")) {
                String features = cleanDatabaseCitations(responseJson.getString("key_features"));
                readableResponse.append("Key aspects: ").append(features);

                if (!features.endsWith(".") && !features.endsWith("!") && !features.endsWith("?")) {
                    readableResponse.append(".");
                }
                readableResponse.append("\n\n");
            }

            if (responseJson.has("recommended_moves")) {
                String moves = cleanDatabaseCitations(responseJson.getString("recommended_moves"));
                readableResponse.append("My recommendations: ").append(moves);

                if (!moves.endsWith(".") && !moves.endsWith("!") && !moves.endsWith("?")) {
                    readableResponse.append(".");
                }
                readableResponse.append("\n\n");
            }

            if (responseJson.has("deeper_insights")) {
                String insights = cleanDatabaseCitations(responseJson.getString("deeper_insights"));
                readableResponse.append(insights);

                if (!insights.endsWith(".") && !insights.endsWith("!") && !insights.endsWith("?")) {
                    readableResponse.append(".");
                }
                readableResponse.append("\n\n");
            }

            if (responseJson.has("personality_note")) {
                String personality = cleanDatabaseCitations(responseJson.getString("personality_note"));
                readableResponse.append(personality);

                if (!personality.endsWith(".") && !personality.endsWith("!") && !personality.endsWith("?")) {
                    readableResponse.append(".");
                }
            }

            String finalResponse = readableResponse.toString().trim();

            if (finalResponse.isEmpty()) {
                Log.w(TAG, "JSON response was parsed but resulted in empty text");
                return getFinalFallbackMessage(master);
            }

            Log.d(TAG, "✅ Successfully converted JSON response to readable text, length: " + finalResponse.length());
            return finalResponse;

        } catch (JSONException e) {
            Log.e(TAG, "❌ Error parsing JSON response from assistant", e);
            Log.d(TAG, "Raw response was: " + jsonResponse.substring(0, Math.min(200, jsonResponse.length())));

            // If JSON parsing fails, try to extract useful text anyway
            if (jsonResponse.contains("database_search_results") || jsonResponse.contains("historical_examples")) {
                Log.d(TAG, "🎯 Detected database content in malformed JSON - attempting extraction");
                return extractTextFromMalformedJson(jsonResponse, master);
            } else {
                // Clean any citations from the raw response and return
                return cleanDatabaseCitations(jsonResponse);
            }
        }
    }

    /**
     * NEW METHOD: Clean database citations from text
     * This removes the 【4:0†source】 style citations that appear in responses
     */
    private String cleanDatabaseCitations(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String cleaned = text;

        // Remove OpenAI file citation patterns like 【4:0†source】, 【4:2†source】, etc.
        cleaned = cleaned.replaceAll("【\\d+:\\d+†source】", "");

        // Remove any other citation patterns that might appear
        cleaned = cleaned.replaceAll("\\[\\d+:\\d+\\]", "");
        cleaned = cleaned.replaceAll("\\[source\\]", "");
        cleaned = cleaned.replaceAll("\\[\\d+\\]", "");

        // Clean up any double spaces left by citation removal
        cleaned = cleaned.replaceAll("\\s+", " ");

        // Clean up any awkward sentence starts caused by citation removal
        cleaned = cleaned.replaceAll("^[\\s,\\.]+", "");

        // Ensure proper sentence flow
        cleaned = cleaned.trim();

        Log.d(TAG, "🧹 Cleaned citations from text. Original length: " + text.length() + ", Cleaned length: " + cleaned.length());

        return cleaned;
    }

    /**
     * HELPER: Extract useful text from malformed JSON
     */
    private String extractTextFromMalformedJson(String malformedJson, String master) {
        try {
            // Simple regex-based extraction as fallback
            StringBuilder extracted = new StringBuilder();

            String[] patterns = {
                    "\"immediate_assessment\"\\s*:\\s*\"([^\"]+)\"",
                    "\"key_features\"\\s*:\\s*\"([^\"]+)\"",
                    "\"recommended_moves\"\\s*:\\s*\"([^\"]+)\"",
                    "\"deeper_insights\"\\s*:\\s*\"([^\"]+)\"",
                    "\"personality_note\"\\s*:\\s*\"([^\"]+)\""
            };

            for (String pattern : patterns) {
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher m = p.matcher(malformedJson);
                if (m.find()) {
                    String text = m.group(1);
                    if (text != null && !text.trim().isEmpty()) {
                        extracted.append(text).append(" ");
                    }
                }
            }

            String result = extracted.toString().trim();
            return result.isEmpty() ? getFinalFallbackMessage(master) : result;

        } catch (Exception e) {
            Log.e(TAG, "Error in malformed JSON extraction", e);
            return getFinalFallbackMessage(master);
        }
    }



    /**
     * SMART DEBUGGING: Enhanced Assistant API call with complete visibility
     */
    public String sendEnhancedMessageToAssistant(String threadId, String assistantId,
                                                 String userInput, String gameContext, String master) {
        try {
            Log.d(TAG, "🎯 DEBUGGING: Starting enhanced message to assistant");
            Log.d(TAG, "Input - Thread ID: " + threadId);
            Log.d(TAG, "Input - Assistant ID: " + assistantId);
            Log.d(TAG, "Input - Master: " + master);

            // Create enhanced message
            String enhancedMessage = createEnhancedAssistantMessage(userInput, gameContext, master);
            Log.d(TAG, "Enhanced message created, length: " + enhancedMessage.length());

            // Step 1: Create message in thread
            JSONObject messageBody = new JSONObject();
            messageBody.put("role", "user");
            messageBody.put("content", enhancedMessage);

            Log.d(TAG, "📨 STEP 1: Creating message in thread...");
            String messageResponse = openAIService.createMessage(threadId, messageBody.toString());

            if (messageResponse == null) {
                Log.e(TAG, "❌ STEP 1 FAILED: createMessage returned null");
                return null;
            }

            Log.d(TAG, "✅ STEP 1 SUCCESS: Message created");
            Log.d(TAG, "Message response preview: " + messageResponse.substring(0, Math.min(200, messageResponse.length())));

            // Step 2: Create run - this is where the error is happening
            JSONObject runBody = new JSONObject();
            runBody.put("assistant_id", assistantId);

            String runInstructions = String.format(
                    "Respond as %s with authentic personality. Use your chess knowledge extensively.",
                    getMasterDisplayName(master)
            );
            runBody.put("additional_instructions", runInstructions);

            Log.d(TAG, "🏃 STEP 2: Creating run...");
            Log.d(TAG, "Run request body: " + runBody.toString());

            String runResponse = openAIService.createRun(threadId, runBody.toString());

            // THIS IS THE CRITICAL DEBUGGING POINT
            if (runResponse == null) {
                Log.e(TAG, "❌ STEP 2 FAILED: createRun returned NULL");
                return null;
            }

            Log.d(TAG, "📄 STEP 2 RAW RESPONSE:");
            Log.d(TAG, "Full response: " + runResponse);
            Log.d(TAG, "Response length: " + runResponse.length());

            // Let's see what's actually in this response
            try {
                JSONObject runJson = new JSONObject(runResponse);

                // List all available keys in the response
                Log.d(TAG, "🔍 Available keys in response:");
                runJson.keys().forEachRemaining(key -> {
                    Log.d(TAG, "  - Key: " + key);
                });

                // Check for common error patterns
                if (runJson.has("error")) {
                    JSONObject error = runJson.getJSONObject("error");
                    Log.e(TAG, "❌ API returned error object:");
                    Log.e(TAG, "Error: " + error.toString());
                    return null;
                }

                // Check for the id field specifically
                if (runJson.has("id")) {
                    String runId = runJson.getString("id");
                    Log.d(TAG, "✅ SUCCESS: Found run ID: " + runId);
                    return runId;
                } else {
                    Log.e(TAG, "❌ MISSING 'id' FIELD in response");
                    Log.e(TAG, "This means the API call didn't return a valid run object");

                    // Let's see what status or type it might have instead
                    if (runJson.has("object")) {
                        Log.d(TAG, "Response object type: " + runJson.getString("object"));
                    }
                    if (runJson.has("status")) {
                        Log.d(TAG, "Response status: " + runJson.getString("status"));
                    }

                    return null;
                }

            } catch (JSONException e) {
                Log.e(TAG, "❌ JSON parsing error on response", e);
                Log.e(TAG, "This suggests the response isn't valid JSON");
                Log.e(TAG, "Raw response was: " + runResponse);
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Unexpected error in sendEnhancedMessageToAssistant", e);
            return null;
        }
    }


    private String createEmergencySystemPrompt(String master) {
        String masterName = getMasterDisplayName(master);
        return String.format(
                "You are %s, the legendary chess grandmaster. You have access to deep chess knowledge " +
                        "including historical games, tactical patterns, and strategic principles. " +
                        "Respond with your authentic personality and reference specific chess knowledge when relevant. " +
                        "This is your comprehensive, final analysis.", masterName
        );
    }

    private String createEmergencyUserMessage(String userInput, String gameContext, String master) {
        StringBuilder message = new StringBuilder();
        message.append("CHESS MASTER ANALYSIS REQUEST:\n\n");

        if (gameContext != null && !gameContext.trim().isEmpty()) {
            message.append("POSITION: ").append(gameContext).append("\n\n");
        }

        message.append("QUESTION: ").append(userInput).append("\n\n");
        message.append("Please provide deep analysis drawing from chess history and your expertise.");

        return message.toString();
    }

    private String getFinalFallbackMessage(String master) {
        String masterName = getMasterDisplayName(master);
        return String.format(
                "As %s, I'm having some technical difficulties accessing my full chess library right now. " +
                        "But let me share what I can from memory - this position deserves careful analysis, " +
                        "and I believe there are important strategic and tactical elements to consider here.",
                masterName
        );
    }

    /**
     * ENHANCED: Process with any master using assistants API
     */
    public String processWithMasterAssistant(String userInput, String gameContext, String master) {
        try {
            Log.d(TAG, "🧠 Processing with " + master + " assistant");

            // Get appropriate assistant ID
            String assistantId = getAssistantIdForDeepAnalysis(master);
            if (assistantId == null) {
                throw new Exception("No assistant available");
            }

            // Create or get thread
            String threadId = createConversationThread();
            if (threadId == null) {
                throw new Exception("Could not create conversation thread");
            }

            // Send enhanced message
            String runId = sendEnhancedMessageToAssistant(threadId, assistantId, userInput, gameContext, master);
            if (runId == null) {
                throw new Exception("Could not create run");
            }

            // Get the response
            String response = getChessMasterResponse(threadId, runId);

            if (response == null || response.trim().isEmpty()) {
                throw new Exception("Empty response from assistant");
            }

            Log.d(TAG, "✅ Got response from " + master + " assistant");
            return response;

        } catch (Exception e) {
            Log.e(TAG, "Error in master assistant processing", e);
            return String.format("I'm having trouble accessing my deepest chess knowledge right now. " +
                            "Let me share my understanding of this position from memory instead.",
                    getMasterDisplayName(master));
        }
    }

    /**
     * Callback interface
     */
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}