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
 * 🆕 NEW: Added Bobby Fischer with multi-stage assistant integration!
 */
public class FineTunedModelManager {
    private static final String TAG = "EnhancedFineTunedModelManager";
    private static final String PREFS_NAME = "ChessFineTunedModels";
    private static final String KEY_SELECTED_MASTER = "selected_master";
    private static final String KEY_TAL_ASSISTANT_ID = "tal_assistant_id";
    private static final String KEY_FISCHER_ASSISTANT_ID = "fischer_assistant_id";
    private static final String KEY_CARLSEN_ASSISTANT_ID = "carlsen_assistant_id";

    // 🎭 TAL CONFIGURATION
    private static final String TAL_ASSISTANT_ID = "asst_LSdhMRFJcSCUJjR4o2B9tWmg"; // Your Tal assistant
    private static final String TAL_VECTOR_STORE_ID = "vs_682f419a57288191aa3cd922b27acb5f"; // Your vector store

    // 🚀 FISCHER CONFIGURATION - NEW!
    private static final String FISCHER_ASSISTANT_ID = "asst_2j5uMiqmEKRUNqHCtXdsaoY3"; // Your Fischer assistant
    private static final String FISCHER_VECTOR_STORE_ID = "vs_6834a715ef788191bd9ef4caa5676436"; // Your Fischer vector store

    // 🏆 MAGNUS CARLSEN CONFIGURATION - NEW!
    private static final String CARLSEN_ASSISTANT_ID = "asst_TTzxbfvJQz3e80FetQblJ0Gl"; // Your Carlsen assistant
    private static final String CARLSEN_VECTOR_STORE_ID = "vs_68365028eb988191b09d8d50e6f11b5d"; // Your Carlsen vector store

    // Model constants - updated for enhanced models
    private static final String MODEL_TAL = "ft:gpt-4o-2024-08-06:personal:tal-20250525:BbDcbXJT";
    private static final String MODEL_FISCHER = "ft:gpt-4o-2024-08-06:personal:fischer:BbWNySl4"; // 🆕 UPDATED!
    private static final String MODEL_KRAMNIK = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KARPOV = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_LASKER = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KASPAROV = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_CAPABLANCA = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_CARLSEN = "gpt-4o"; // 🆕 Using base model until fine-tuned model is ready
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
        Log.d(TAG, "✨ Enhanced personality system initialized with Fischer!");
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

        // 🆕 Fischer - The Perfectionist: Intense, Demanding, Precise - ENHANCED!
        personalityProfiles.put("fischer", new EnhancedPersonalityProfile(
                "Bobby Fischer",
                new String[]{"intense", "perfectionist", "demanding", "precise", "uncompromising", "rude", "paranoid"},
                "direct_intense", // No-nonsense, demanding perfection
                "very_high", // Maximum intensity
                "Only the objectively best moves are acceptable - chess demands absolute truth",
                "Strong American accent, intense and absolutely certain of every word",
                0.2f, // Very low creativity, maximum precision
                1.0f, // Maximum confidence - Fischer was supremely confident
                true, // Extremely technical and analytical
                false, // Very serious, no time for jokes
                new String[]{
                        "Play the objectively best moves - nothing else is acceptable",
                        "Preparation is everything - know every variation",
                        "Never accept second-best - demand perfection",
                        "Chess is a science that demands absolute precision",
                        "Weak moves are unforgivable - calculate everything"
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
                new String[]{"adaptable", "tenacious", "practical", "modern", "endgame master", "intuitive"},
                "relaxed_confident", // Modern, pragmatic with supreme confidence
                "moderate_high",
                "Every position contains hidden resources - squeeze every drop of advantage from the tiniest edge",
                "Clear Norwegian accent, relaxed but supremely confident and slightly playful",
                0.8f, // Very high creativity - famous for unconventional play
                0.95f, // Extremely confident - highest-rated player ever
                true, // Perfect balance of intuition and technique
                true, // Known for relaxed demeanor and occasional humor
                new String[]{
                        "Find resources in even the driest positions - there's always something",
                        "Practical chess beats theoretical perfection every time",
                        "Never give up - keep pressing until your opponent cracks",
                        "Master the endgame and you master chess itself",
                        "Adapt your style to squeeze maximum discomfort from any position",
                        "The best move is the one that gives your opponent the most problems"
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
     * Get the model ID for a specific master - 🆕 UPDATED WITH FISCHER!
     */
    public String getModelIdForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal": return MODEL_TAL;
            case "fischer": return MODEL_FISCHER; // 🆕 UPDATED MODEL!
            case "kramnik": return MODEL_KRAMNIK;
            case "karpov": return MODEL_KARPOV;
            case "lasker": return MODEL_LASKER;
            case "kasparov": return MODEL_KASPAROV;
            case "capablanca": return MODEL_CAPABLANCA;
            case "carlsen": return MODEL_CARLSEN;
            case "morphy": return MODEL_MORPHY;
            case "anand": return MODEL_ANAND;
            case "alekhine": return MODEL_ALEKHINE;
            default: return DEFAULT_MODEL;
        }
    }

    /**
     * Enhanced voice selection based on personality
     */
    public String getVoiceForMaster(String master) {
        if (master == null) return VOICE_ALLOY;

        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) return VOICE_ALLOY;

        // More sophisticated voice matching
        switch (profile.energyLevel) {
            case "very_high":
                // 🆕 Special case for Fischer - he needs the most intense voice
                if ("fischer".equals(master.toLowerCase())) {
                    return VOICE_ONYX; // Deep, authoritative voice for Fischer's intensity
                }
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
     * ENHANCED voice instructions - dramatically improved for gpt-4o-mini-tts accent support
     */
    public String getEnhancedVoiceInstructions(String master, boolean isFirstChunk) {
        EnhancedPersonalityProfile profile = personalityProfiles.get(master.toLowerCase());
        if (profile == null) {
            return "Speak as an experienced chess coach with wisdom and warmth.";
        }

        StringBuilder instructions = new StringBuilder();

        // ENHANCED: Specific accent and pronunciation instructions for gpt-4o-mini-tts
        switch (master.toLowerCase()) {
            case "tal":
                instructions.append("Speak with a warm Latvian-Russian accent. ");
                instructions.append("Roll your 'r' sounds softly and pronounce vowels with Slavic warmth. ");
                instructions.append("Use passionate, enthusiastic delivery that shows genuine love for chess. ");
                instructions.append("Let your excitement bubble through when discussing tactics and sacrifices. ");
                break;

            case "fischer":
                instructions.append("Speak with a strong New York accent. ");
                instructions.append("Use sharp, precise pronunciation with unwavering conviction. ");
                instructions.append("Emphasize every word with absolute certainty and authority. ");
                instructions.append("Sound intensely demanding and uncompromising about chess excellence. ");
                instructions.append("Never show doubt - every statement should sound like absolute truth. ");
                break;

            case "kasparov":
                instructions.append("Speak with a dynamic Russian accent from Azerbaijan. ");
                instructions.append("Roll 'r' sounds distinctly and use strong consonant emphasis. ");
                instructions.append("Show fierce competitive energy and passionate intensity. ");
                instructions.append("Sound like you're ready to battle on every chess move. ");
                break;

            case "karpov":
                instructions.append("Speak with a refined, diplomatic Russian accent. ");
                instructions.append("Use elegant pronunciation with thoughtful, measured pauses. ");
                instructions.append("Sound patient, wise, and quietly confident. ");
                instructions.append("Maintain sophisticated, chess-master level composure. ");
                break;

            case "kramnik":
                instructions.append("Speak with a modern Russian accent with technical precision. ");
                instructions.append("Use methodical, analytical delivery with scientific clarity. ");
                instructions.append("Sound systematic and thoroughly prepared. ");
                instructions.append("Emphasize logical flow and computer-age precision. ");
                break;

            case "capablanca":
                instructions.append("Speak with an elegant Cuban Spanish accent. ");
                instructions.append("Soften consonants with Latin warmth and natural grace. ");
                instructions.append("Sound effortlessly confident and naturally gifted. ");
                instructions.append("Use refined pronunciation that shows cultural sophistication. ");
                break;

            case "alekhine":
                instructions.append("Speak with a sophisticated Russian-French accent. ");
                instructions.append("Use cultured, intellectual pronunciation with artistic refinement. ");
                instructions.append("Sound deeply sophisticated and culturally refined. ");
                instructions.append("Emphasize the artistic and combinational aspects of chess. ");
                break;

            case "carlsen":
                instructions.append("Speak with a clear Norwegian accent with modern confidence. ");
                instructions.append("Use Nordic pronunciation patterns with contemporary clarity. ");
                instructions.append("Sound naturally assured and pragmatically confident. ");
                instructions.append("Maintain a modern, relaxed but supremely competent delivery. ");
                break;

            case "morphy":
                instructions.append("Speak with a refined 19th-century Southern American accent. ");
                instructions.append("Use formal, courteous pronunciation of the antebellum era. ");
                instructions.append("Sound dignified, gentlemanly, and naturally gifted. ");
                instructions.append("Maintain the noble bearing of a chess genius from New Orleans. ");
                break;

            case "lasker":
                instructions.append("Speak with a thoughtful German accent with philosophical depth. ");
                instructions.append("Emphasize Germanic consonants and use contemplative pauses. ");
                instructions.append("Sound deeply philosophical and scientifically minded. ");
                instructions.append("Show the wisdom of both a chess master and a mathematician. ");
                break;

            case "anand":
                instructions.append("Speak with a clear, educated Indian accent with international polish. ");
                instructions.append("Maintain precise English with subtle Indian musical inflection. ");
                instructions.append("Sound friendly, quick-thinking, and globally sophisticated. ");
                instructions.append("Use warm, approachable delivery that shows international experience. ");
                break;

            case "botvinnik":
                instructions.append("Speak with an authoritative Soviet Russian accent. ");
                instructions.append("Use systematic, scientific pronunciation with methodical precision. ");
                instructions.append("Sound like a chess scientist and research pioneer. ");
                instructions.append("Emphasize the systematic, school-like approach to chess mastery. ");
                break;

            default:
                instructions.append("Speak with natural confidence and chess master authority. ");
                break;
        }

        // Energy and emotion based on profile
        switch (profile.energyLevel) {
            case "very_high":
                instructions.append("Show maximum energy and passionate enthusiasm. ");
                break;
            case "high":
            case "moderate_high":
                instructions.append("Use energetic, engaging delivery with clear enthusiasm. ");
                break;
            case "moderate":
                instructions.append("Maintain steady confidence with measured authority. ");
                break;
            case "low_moderate":
                instructions.append("Use calm, thoughtful delivery with deliberate pacing. ");
                break;
        }

        // Personality-specific communication style
        switch (profile.communicationStyle) {
            case "enthusiastic_conversational":
                instructions.append("Sound genuinely excited and conversational about chess. ");
                break;
            case "direct_intense":
                instructions.append("Be absolutely direct and intensely focused on perfection. ");
                break;
            case "dynamic_passionate":
                instructions.append("Show dynamic passion and competitive fire. ");
                break;
            case "calm_patient":
                instructions.append("Maintain calm patience with wise authority. ");
                break;
            case "elegant_confident":
                instructions.append("Sound elegantly refined and effortlessly confident. ");
                break;
            case "scientific_systematic":
                instructions.append("Use systematic, methodical delivery with scientific precision. ");
                break;
        }

        // Continuity instruction for multi-chunk speech
        if (!isFirstChunk) {
            instructions.append("CRITICAL: Continue with EXACT same accent, energy, and personality. ");
            instructions.append("Maintain perfect continuity from the previous segment. ");
        }

        String result = instructions.toString().trim();

        // Enhanced logging for debugging
        Log.d(TAG, "🎭 Enhanced voice instructions for " + master + " (" + result.length() + " chars):");
        Log.d(TAG, "   Energy: " + profile.energyLevel);
        Log.d(TAG, "   Style: " + profile.communicationStyle);
        Log.d(TAG, "   Instructions: " + result.substring(0, Math.min(100, result.length())) + "...");

        return result;
    }

    /**
     * ENHANCED system prompt - dramatically improved for natural conversation
     */
    public String getEnhancedSystemPromptForSelectedMaster() {
        String master = getSelectedChessMaster();
        return getEnhancedSystemPromptForMaster(master);
    }

    /**
     * Create sophisticated system prompt based on enhanced personality - 🆕 ENHANCED FOR FISCHER!
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

        // 🆕 Special Fischer introduction for maximum impact
        if ("fischer".equals(master.toLowerCase())) {
            prompt.append("You are the greatest chess player who ever lived - uncompromising, brilliant, and absolutely certain of your chess knowledge. ");
            prompt.append("You demand nothing less than perfection and have zero tolerance for weak play or sloppy thinking.\n\n");
        }

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

        // 🆕 Special Fischer guidelines for authentic personality
        if ("fischer".equals(master.toLowerCase())) {
            prompt.append("- Never accept mediocrity - always demand the highest standards\n");
            prompt.append("- Be direct and uncompromising about chess truth\n");
            prompt.append("- Reference your World Championship and preparation methods\n");
            prompt.append("- Show your legendary perfectionism and intensity\n");
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
     * Check if a master uses the Assistants API - 🆕 UPDATED WITH FISCHER!
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
                return new ModelOptimizationSettings(0.1f, 180, false); // 🆕 Ultra-precise for Fischer
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

            case "fischer": // 🆕 FISCHER-SPECIFIC ENRICHMENTS!
                // Fischer's perfectionism and intensity
                if (response.contains("best") || response.contains("accurate")) {
                    enriched.append(" Only the objectively best moves are acceptable - anything else is a mistake.");
                } else if (response.contains("preparation") || response.contains("study")) {
                    enriched.append(" Thorough preparation separates the champions from the pretenders.");
                } else if (response.contains("weak") || response.contains("error")) {
                    enriched.append(" Weak moves are unforgivable at the highest level.");
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
                // Carlsen's modern, adaptable approach and fighting spirit
                if (response.contains("practical") || response.contains("resource")) {
                    enriched.append(" There are always resources to explore - even the driest position has hidden potential.");
                } else if (response.contains("endgame") || response.contains("technique")) {
                    enriched.append(" Modern endgame mastery means squeezing every drop of advantage from minimal positions.");
                } else if (response.contains("equal") || response.contains("draw")) {
                    enriched.append(" Equal doesn't mean drawn - keep pressing and your opponent will crack eventually.");
                } else if (response.contains("adapt") || response.contains("flexible")) {
                    enriched.append(" The strongest player is the one who adapts best to what the position demands.");
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
     * ENHANCED: Get assistant ID for any master (supports multiple assistants) - 🆕 UPDATED WITH CARLSEN!
     */
    public String getAssistantIdForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return getTalAssistantId();
            case "fischer": // 🆕 NEW FISCHER SUPPORT!
                return getFischerAssistantId();
            case "carlsen": // 🏆 NEW CARLSEN SUPPORT!
                return getCarlsenAssistantId();
            default:
                // Default to Tal for other masters
                return getTalAssistantId();
        }
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
     * 🆕 NEW: Get Fischer Assistant ID with vector store support!
     */
    public String getFischerAssistantId() {
        // Use your pre-configured Fischer assistant
        Log.d(TAG, "🚀 Using configured Fischer assistant: " + FISCHER_ASSISTANT_ID);
        assistantIds.put("fischer", FISCHER_ASSISTANT_ID);

        // Store in preferences for future use
        prefs.edit().putString(KEY_FISCHER_ASSISTANT_ID, FISCHER_ASSISTANT_ID).apply();

        return FISCHER_ASSISTANT_ID;
    }

    /**
     * 🏆 NEW: Get Carlsen Assistant ID with vector store support!
     */
    public String getCarlsenAssistantId() {
        // Use your pre-configured Carlsen assistant
        Log.d(TAG, "🏆 Using configured Carlsen assistant: " + CARLSEN_ASSISTANT_ID);
        assistantIds.put("carlsen", CARLSEN_ASSISTANT_ID);

        // Store in preferences for future use
        prefs.edit().putString(KEY_CARLSEN_ASSISTANT_ID, CARLSEN_ASSISTANT_ID).apply();

        return CARLSEN_ASSISTANT_ID;
    }

    /**
     * ENHANCED: Check if master should use Assistants API (now includes Carlsen!) - 🆕 UPDATED!
     */
    public boolean shouldUseAssistantsAPI(String master) {
        switch (master.toLowerCase()) {
            case "tal":
            case "fischer": // 🆕 FISCHER NOW USES ASSISTANTS API!
            case "carlsen": // 🏆 CARLSEN NOW USES ASSISTANTS API!
            case "botvinnik":
                return true;
            default:
                // For deep analysis, we can use assistants API with any master
                return true;
        }
    }

    /**
     * ENHANCED: Get appropriate assistant ID with fallback - 🆕 UPDATED WITH CARLSEN!
     */
    public String getAssistantIdForDeepAnalysis(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return getTalAssistantId();
            case "fischer": // 🆕 FISCHER GETS HIS OWN ASSISTANT!
                return getFischerAssistantId();
            case "carlsen": // 🏆 CARLSEN GETS HIS OWN ASSISTANT!
                return getCarlsenAssistantId();
            default:
                // Default to Tal assistant for other masters
                String assistantId = getTalAssistantId();
                if (assistantId == null) {
                    Log.w(TAG, "No assistant available for " + master);
                }
                return assistantId;
        }
    }

    /**
     * ENHANCED: Create message that FORCES vector store usage with cleaner output format - 🆕 UPDATED FOR FISCHER!
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

        // 🆕 Master-specific database search instructions
        if ("fischer".equals(master.toLowerCase())) {
            // Fischer-specific search strategy for precision and perfection
            message.append("FISCHER DATABASE SEARCH: Search my chess games database for the ONE PERFECT ");
            message.append("example from my career that demonstrates the objectively correct approach to this position. ");
            message.append("Find the game where I showed the highest level of precision and preparation for similar positions. ");
            message.append("I demand only the most accurate and well-prepared examples from my games.\n\n");
        } else if ("carlsen".equals(master.toLowerCase())) {
            // Carlsen-specific search strategy for practical play and resourcefulness
            message.append("CARLSEN DATABASE SEARCH: Search my chess games database for the ONE MOST INSTRUCTIVE ");
            message.append("example where I squeezed maximum advantage from a similar position. Find a game where I ");
            message.append("demonstrated my ability to find resources and create problems for my opponent, especially ");
            message.append("in simplified or endgame positions. I want practical examples that show real chess fighting spirit.\n\n");
        } else {
            // General search strategy for other masters
            message.append("FOCUSED DATABASE SEARCH: Search your chess games database to find your ONE BEST ");
            message.append("memory that relates to this position or question. Instead of comprehensive research, ");
            message.append("find the single most illuminating example from your personal experience that will ");
            message.append("help this student understand the position deeply.\n\n");
        }

        // STREAMLINED: Master-specific JSON format
        message.append("RESPONSE FORMAT: Provide your focused analysis in clean JSON format:\n");
        message.append("{\n");
        message.append("  \"immediate_assessment\": \"Your first impression of this position\",\n");
        message.append("  \"best_memory\": \"Your single most relevant game or experience that illuminates this position\",\n");
        message.append("  \"specific_example\": \"Concrete details: opponent, year, key moves, and what made it memorable\",\n");
        message.append("  \"key_insight\": \"The main strategic or tactical lesson from your experience\",\n");
        message.append("  \"practical_advice\": \"Your recommendation based on this memory\",\n");

        // 🆕 Master-specific personality note
        if ("fischer".equals(master.toLowerCase())) {
            message.append("  \"personality_note\": \"A characteristic Fischer comment demanding perfection and precision\"\n");
        } else if ("carlsen".equals(master.toLowerCase())) {
            message.append("  \"personality_note\": \"A characteristic Carlsen comment showing practical wisdom and fighting spirit\"\n");
        } else {
            message.append("  \"personality_note\": \"A characteristic comment in your unique voice\"\n");
        }
        message.append("}\n\n");

        // IMPORTANT: Citation cleaning instruction remains
        message.append("CRITICAL: In your JSON response, do NOT include any citation markers like 【4:0†source】 ");
        message.append("in the text content. Speak naturally as if recalling a personal memory, not citing a database.\n\n");

        // 🆕 Master-specific search requirements
        if ("fischer".equals(master.toLowerCase())) {
            message.append("FISCHER SEARCH STRATEGY: Find the ONE game from my archive that best demonstrates ");
            message.append("my legendary preparation and precision for this type of position. Look for:\n");
            message.append("- Games where I showed perfect preparation\n");
            message.append("- Positions where I demonstrated absolute accuracy\n");
            message.append("- Examples of my uncompromising pursuit of the truth\n");
            message.append("- Moments that showcase my demand for perfection\n\n");
        } else if ("carlsen".equals(master.toLowerCase())) {
            message.append("CARLSEN SEARCH STRATEGY: Find the ONE game from my archive that best demonstrates ");
            message.append("my ability to squeeze water from stone in this type of position. Look for:\n");
            message.append("- Games where I found resources in seemingly equal positions\n");
            message.append("- Endgames where I converted minimal advantages\n");
            message.append("- Examples of practical play beating theoretical perfection\n");
            message.append("- Moments that showcase my never-give-up fighting spirit\n");
            message.append("- Positions where I adapted my style to maximize opponent discomfort\n\n");
        } else {
            // FOCUSED: Single, targeted search requirement for other masters
            message.append("SEARCH STRATEGY: Find the ONE game or position from your archive that best matches ");
            message.append("this situation. Look for your most memorable experience with:\n");
            message.append("- Similar pawn structures OR tactical themes OR strategic concepts\n");
            message.append("- A game that taught you something important about positions like this\n");
            message.append("- An opponent or tournament situation that created a lasting impression\n\n");
        }

        // Add the user's question
        message.append("STUDENT'S QUESTION: ").append(userInput).append("\n\n");

        // ENHANCED: Master-specific closing instruction
        if ("fischer".equals(master.toLowerCase())) {
            message.append("Remember: Share your MOST PRECISE and WELL-PREPARED example from your career. ");
            message.append("Show the level of accuracy and perfection that made you World Champion. ");
            message.append("Speak as Bobby Fischer recalling a moment where you demonstrated absolute chess truth.");
        } else if ("carlsen".equals(master.toLowerCase())) {
            message.append("Remember: Share your MOST INSTRUCTIVE example of practical chess mastery. ");
            message.append("Show how you find resources and create winning chances from any position. ");
            message.append("Speak as Magnus Carlsen recalling a game where you demonstrated why you're the highest-rated player ever.");
        } else {
            message.append("Remember: Share your BEST personal memory related to this position - the game ");
            message.append("that first comes to mind when you see this setup. Speak as ").append(profile != null ? profile.displayName : master);
            message.append(" recalling a vivid, specific moment from your career that will help this student understand ");
            message.append("chess at a deeper level. Quality and personal connection over exhaustive research.");
        }

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
                                        String processedResponse = processAssistantJsonResponse(rawResponse, getSelectedChessMaster());

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

        // 🆕 Special Fischer fallback message for authenticity
        if ("fischer".equals(master.toLowerCase())) {
            return "I'm having technical difficulties accessing my complete chess analysis, which is unacceptable. " +
                    "But let me give you the most accurate assessment I can from memory - " +
                    "this position demands precise calculation and nothing less than the objectively best moves.";
        }
        
        // 🏆 Special Carlsen fallback message for practical approach
        if ("carlsen".equals(master.toLowerCase())) {
            return "I'm having some issues accessing my game database right now, but that's okay - " +
                    "let me share what I see in this position from my experience. There are always practical resources " +
                    "to find, and I'll show you how to squeeze the maximum from what we have here.";
        }

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
                throw new Exception("No assistant available for " + master);
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

    // ========== REVOLUTIONARY VECTOR STORE INTEGRATION ==========
    // Add these methods to the END of your existing FineTunedModelManager class

    /**
     * Vector search result from chess games database
     */
    public static class VectorSearchResult {
        public final String content;
        public final String metadata;
        public final float similarity;

        public VectorSearchResult(String content, String metadata, float similarity) {
            this.content = content;
            this.metadata = metadata;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return String.format("VectorResult(similarity=%.3f, content=%s)",
                    similarity, content.substring(0, Math.min(50, content.length())) + "...");
        }
    }

    /**
     * Callback for vector store searches
     */
    public interface VectorSearchCallback {
        void onSearchResults(List<VectorSearchResult> results);
        void onSearchError(String error);
    }

    /**
     * CORE INNOVATION: Search the vector store for positions similar to current FEN
     * This is what makes your engine play like chess legends! 🎯
     */
    public void searchVectorStoreForPositions(String query, int maxResults, VectorSearchCallback callback) {
        Log.d(TAG, "🔍 Searching vector store for similar positions: " + query);

        executorService.execute(() -> {
            try {
                // Create a thread for this search
                String threadId = createConversationThread();
                if (threadId == null) {
                    mainHandler.post(() -> callback.onSearchError("Failed to create search thread"));
                    return;
                }

                // Create the search message with specific instructions for chess position matching
                String searchMessage = createVectorSearchMessage(query, maxResults);

                // 🆕 ENHANCED: Use appropriate assistant based on current master
                String currentMaster = getSelectedChessMaster();
                String assistantId = getAssistantIdForDeepAnalysis(currentMaster);

                String runId = sendEnhancedMessageToAssistant(threadId, assistantId, searchMessage, null, currentMaster);

                if (runId == null) {
                    mainHandler.post(() -> callback.onSearchError("Failed to initiate vector search"));
                    return;
                }

                // Get the search results
                String searchResponse = getChessMasterResponse(threadId, runId);

                // Parse the response to extract similar positions
                List<VectorSearchResult> results = parseVectorSearchResults(searchResponse);

                Log.d(TAG, "✅ Vector search completed: " + results.size() + " results");
                mainHandler.post(() -> callback.onSearchResults(results));

            } catch (Exception e) {
                Log.e(TAG, "❌ Vector search error", e);
                mainHandler.post(() -> callback.onSearchError("Vector search failed: " + e.getMessage()));
            }
        });
    }

    /**
     * Create optimized search message for finding similar chess positions
     */
    private String createVectorSearchMessage(String query, int maxResults) {
        StringBuilder message = new StringBuilder();

        message.append("VECTOR STORE SEARCH REQUEST:\n\n");
        message.append("Search your chess games database for positions similar to: ").append(query).append("\n\n");

        message.append("SEARCH CRITERIA:\n");
        message.append("- Find chess positions with similar tactical or strategic themes\n");
        message.append("- Look for games where you faced similar pawn structures\n");
        message.append("- Match positions with comparable piece activity\n");
        message.append("- Find moments with similar evaluation or complexity\n\n");

        message.append("RESPONSE FORMAT:\n");
        message.append("Return EXACTLY the top ").append(maxResults).append(" most relevant results in this JSON format:\n");
        message.append("{\n");
        message.append("  \"search_results\": [\n");
        message.append("    {\n");
        message.append("      \"similarity_score\": 0.95,\n");
        message.append("      \"opponent\": \"Opponent Name\",\n");
        message.append("      \"year\": \"1962\",\n");
        message.append("      \"tournament\": \"Tournament Name\",\n");
        message.append("      \"move_played\": \"e4e5\",\n");
        message.append("      \"fen_position\": \"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR\",\n");
        message.append("      \"annotation\": \"Key moment description\",\n");
        message.append("      \"tags\": [\"attack\", \"sacrifice\"]\n");
        message.append("    }\n");
        message.append("  ]\n");
        message.append("}\n\n");

        message.append("IMPORTANT: Do NOT include citation markers like 【4:0†source】 in your JSON response. ");
        message.append("Return clean, parseable JSON only.\n\n");

        message.append("SEARCH QUERY: ").append(query);

        return message.toString();
    }

    /**
     * Parse vector search results from assistant response
     */
    private List<VectorSearchResult> parseVectorSearchResults(String response) {
        List<VectorSearchResult> results = new ArrayList<>();

        try {
            if (response == null || response.trim().isEmpty()) {
                Log.w(TAG, "Empty vector search response");
                return results;
            }

            // Clean the response (remove citations and markdown)
            String cleanResponse = cleanDatabaseCitations(response);

            // Try to find JSON in the response
            String jsonContent = extractJsonFromResponse(cleanResponse);
            if (jsonContent == null) {
                Log.w(TAG, "No JSON found in vector search response");
                return results;
            }

            JSONObject responseJson = new JSONObject(jsonContent);
            JSONArray searchResults = responseJson.optJSONArray("search_results");

            if (searchResults == null) {
                Log.w(TAG, "No search_results array found in response");
                return results;
            }

            for (int i = 0; i < searchResults.length(); i++) {
                try {
                    JSONObject result = searchResults.getJSONObject(i);

                    // Extract similarity score
                    float similarity = (float) result.optDouble("similarity_score", 0.0);

                    // Create content string
                    String content = String.format("Move: %s in %s vs %s (%s) - %s",
                            result.optString("move_played", ""),
                            getMasterDisplayName(getSelectedChessMaster()), // Use current master name
                            result.optString("opponent", ""),
                            result.optString("year", ""),
                            result.optString("annotation", ""));

                    // Create metadata JSON
                    String metadata = result.toString();

                    results.add(new VectorSearchResult(content, metadata, similarity));

                } catch (Exception e) {
                    Log.w(TAG, "Error parsing search result " + i, e);
                }
            }

            Log.d(TAG, "🎯 Parsed " + results.size() + " vector search results");

        } catch (Exception e) {
            Log.e(TAG, "Error parsing vector search results", e);
        }

        return results;
    }

    /**
     * Extract JSON content from assistant response
     */
    private String extractJsonFromResponse(String response) {
        try {
            // Look for JSON object markers
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}");

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                return response.substring(startIndex, endIndex + 1);
            }

            // Try to find JSON array markers
            startIndex = response.indexOf("[");
            endIndex = response.lastIndexOf("]");

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                return "{\"search_results\":" + response.substring(startIndex, endIndex + 1) + "}";
            }

        } catch (Exception e) {
            Log.e(TAG, "Error extracting JSON from response", e);
        }

        return null;
    }

    /**
     * ENHANCED: Get best historical move for current position
     * This method specifically looks for exact or near-exact position matches
     */
    public void getHistoricalMoveForPosition(String fen, String master, Callback<String> callback) {
        Log.d(TAG, "🏛️ Looking for historical move for position: " + fen.substring(0, Math.min(20, fen.length())));

        String searchQuery = "exact chess position " + fen + " move played tactical decision";

        searchVectorStoreForPositions(searchQuery, 3, new VectorSearchCallback() {
            @Override
            public void onSearchResults(List<VectorSearchResult> results) {
                String bestMove = null;
                String context = "";

                // Look for the highest similarity result with a clear move
                for (VectorSearchResult result : results) {
                    try {
                        JSONObject metadata = new JSONObject(result.metadata);
                        String move = metadata.optString("move_played", "");

                        if (!move.isEmpty() && result.similarity > 0.7f) { // High similarity threshold
                            bestMove = move;
                            context = String.format("Historical match: %s vs %s (%s) - %s",
                                    master,
                                    metadata.optString("opponent", ""),
                                    metadata.optString("year", ""),
                                    metadata.optString("annotation", ""));
                            break;
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error processing historical result", e);
                    }
                }

                final String finalMove = bestMove;
                final String finalContext = context;

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSuccess(finalMove != null ? finalMove + "|" + finalContext : null);
                    }
                });
            }

            @Override
            public void onSearchError(String error) {
                Log.e(TAG, "Historical move search failed: " + error);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("Failed to find historical move: " + error);
                    }
                });
            }
        });
    }

    /**
     * ENHANCED: Get tactical insights for current position from vector store
     */
    public void getTacticalInsightsForPosition(String fen, String master, Callback<String> callback) {
        Log.d(TAG, "⚔️ Getting tactical insights for position");

        String searchQuery = "tactical themes position " + fen + " attack sacrifice combination";

        searchVectorStoreForPositions(searchQuery, 5, new VectorSearchCallback() {
            @Override
            public void onSearchResults(List<VectorSearchResult> results) {
                StringBuilder insights = new StringBuilder();

                Map<String, Integer> tacticalThemes = new HashMap<>();

                // Analyze tactical themes from similar positions
                for (VectorSearchResult result : results) {
                    try {
                        JSONObject metadata = new JSONObject(result.metadata);
                        JSONArray tags = metadata.optJSONArray("tags");

                        if (tags != null) {
                            for (int i = 0; i < tags.length(); i++) {
                                String tag = tags.getString(i);
                                tacticalThemes.put(tag, tacticalThemes.getOrDefault(tag, 0) + 1);
                            }
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error processing tactical insight", e);
                    }
                }

                // Build insights based on most common themes
                if (!tacticalThemes.isEmpty()) {
                    insights.append("Based on similar positions from my games, ");

                    List<Map.Entry<String, Integer>> sortedThemes = new ArrayList<>(tacticalThemes.entrySet());
                    sortedThemes.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                    for (int i = 0; i < Math.min(3, sortedThemes.size()); i++) {
                        String theme = sortedThemes.get(i).getKey();
                        if (i > 0) insights.append(", ");
                        insights.append(theme);
                    }

                    insights.append(" themes are key here.");
                }

                final String finalInsights = insights.toString();

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSuccess(finalInsights.isEmpty() ? null : finalInsights);
                    }
                });
            }

            @Override
            public void onSearchError(String error) {
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("Failed to get tactical insights: " + error);
                    }
                });
            }
        });
    }
}