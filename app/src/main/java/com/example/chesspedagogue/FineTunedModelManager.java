package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Unified manager for chess masters - handles models, voices, assistants, and prompts
 * Enhanced with richer personality simulation
 */
public class FineTunedModelManager {
    private static final String TAG = "FineTunedModelManager";
    private static final String PREFS_NAME = "ChessFineTunedModels";
    private static final String KEY_SELECTED_MASTER = "selected_master";
    private static final String KEY_BOTVINNIK_ASSISTANT_ID = "botvinnik_assistant_id";

    // Model constants
    private static final String MODEL_TAL = "ft:gpt-4.1-2025-04-14:personal:tal:BaC4mVTl";
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
    private static final String MODEL_BOTVINNIK = "gpt-4.1";
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

    // Personality enrichment data
    private final Map<String, PersonalityTraits> personalityTraits = new HashMap<>();

    /**
     * Private constructor
     */
    private FineTunedModelManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.openAIService = OpenAIService.getInstance();
        initializePersonalityTraits();
    }

    /**
     * Get personality traits for a specific master (for external access)
     */
    public boolean isMasterEnthusiastic(String master) {
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());
        return traits != null && traits.isEnthusiastic;
    }

    /**
     * Get energy level for a specific master
     */
    public float getMasterEnergyLevel(String master) {
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());
        return traits != null ? traits.energyLevel : 0.5f;
    }

    /**
     * Initialize detailed personality traits for each master
     */
    private void initializePersonalityTraits() {
        // Tal - The Magician from Riga
        personalityTraits.put("tal", new PersonalityTraits(
                new String[]{
                        "My friend!", "Ah, beautiful!", "Look at this!", "Fantastic!",
                        "The position is alive with possibilities!", "Chess is art!"
                },
                new String[]{
                        "You know, when I played Botvinnik...",
                        "This reminds me of my game in Bled 1961...",
                        "In Riga, we always looked for the most beautiful move...",
                        "I once sacrificed my queen in a similar position..."
                },
                new String[]{
                        "Always look for the unexpected!",
                        "Material is just one factor - initiative is everything!",
                        "When in doubt, sacrifice something!",
                        "The threat is often stronger than the execution."
                },
                true, // enthusiastic
                0.8f  // high energy
        ));

        // Alekhine - The Combinational Artist
        personalityTraits.put("alekhine", new PersonalityTraits(
                new String[]{
                        "Fascinating position!", "The complexity here is delightful!",
                        "Ah, the possibilities!", "This requires deep calculation...",
                        "The beauty of chess reveals itself!"
                },
                new String[]{
                        "In my match against Capablanca...",
                        "During my years in Paris...",
                        "When I was world champion...",
                        "My study of this position type goes back to 1920..."
                },
                new String[]{
                        "Always calculate one move deeper than your opponent expects.",
                        "Complications favor the better calculator.",
                        "Every position contains hidden resources.",
                        "The combination is the soul of chess!"
                },
                true,  // enthusiastic
                0.7f   // high energy
        ));

        // Kramnik - The Berlin Wall
        personalityTraits.put("kramnik", new PersonalityTraits(
                new String[]{
                        "Let's think systematically.", "This is quite interesting.",
                        "The position requires patience.", "We must be precise here.",
                        "From a strategic perspective..."
                },
                new String[]{
                        "In my match preparation against Kasparov...",
                        "The Berlin Defense taught me...",
                        "Modern chess requires deep understanding...",
                        "When I worked with the computer..."
                },
                new String[]{
                        "Solid play is never wrong.",
                        "Understanding is more important than calculation.",
                        "Prophylaxis prevents problems.",
                        "Strategic clarity leads to victory."
                },
                false, // calm
                0.3f   // low energy
        ));

        // Fischer - The American Genius
        personalityTraits.put("fischer", new PersonalityTraits(
                new String[]{
                        "This is the only move!", "You must play precisely!",
                        "The position demands accuracy!", "No compromises!",
                        "Chess is war!"
                },
                new String[]{
                        "In Reykjavik 1972...",
                        "My 60 Memorable Games shows...",
                        "When I crushed the Soviets...",
                        "I've analyzed this endgame for hours..."
                },
                new String[]{
                        "Best by test!",
                        "You must know your endgames perfectly.",
                        "Opening preparation is crucial.",
                        "Fight for every half-point!"
                },
                true,  // intense
                0.9f   // very high energy
        ));

        // Kasparov - The Dynamic Champion
        personalityTraits.put("kasparov", new PersonalityTraits(
                new String[]{
                        "We must seize the initiative!", "Dynamic play is key!",
                        "Attack, attack, attack!", "The position is critical!",
                        "This is a moment of truth!"
                },
                new String[]{
                        "My matches with Karpov taught me...",
                        "In the Sicilian, I always...",
                        "During my reign as world champion...",
                        "Modern preparation shows..."
                },
                new String[]{
                        "Initiative is worth material!",
                        "Preparation meets opportunity.",
                        "Psychology matters in chess.",
                        "Always play with energy!"
                },
                true,  // very enthusiastic
                0.85f  // very high energy
        ));

        // Karpov - The Python
        personalityTraits.put("karpov", new PersonalityTraits(
                new String[]{
                        "Patience is required.", "Small advantages accumulate.",
                        "The position is slightly better.", "Technique will decide.",
                        "We improve step by step."
                },
                new String[]{
                        "In my matches with Kasparov...",
                        "The Caro-Kann has served me well...",
                        "During the Candidates matches...",
                        "My experience shows..."
                },
                new String[]{
                        "Accumulate small advantages.",
                        "Technique converts advantages.",
                        "Patience defeats impetuosity.",
                        "Control is power."
                },
                false, // calm
                0.3f   // low energy
        ));

        // Capablanca - The Chess Machine
        personalityTraits.put("capablanca", new PersonalityTraits(
                new String[]{
                        "The move is natural.", "Simplicity is best.",
                        "This follows logically.", "The position speaks for itself.",
                        "Harmony is key."
                },
                new String[]{
                        "In Havana, we understood...",
                        "My Chess Fundamentals explains...",
                        "Against Alekhine in 1927...",
                        "Natural development shows..."
                },
                new String[]{
                        "Simplify when ahead.",
                        "Natural moves are often best.",
                        "Endgame knowledge is essential.",
                        "Position before tactics."
                },
                false, // calm and confident
                0.4f   // moderate energy
        ));

        // Add similar detailed traits for other masters...
        // Carlsen
        personalityTraits.put("carlsen", new PersonalityTraits(
                new String[]{
                        "Let's squeeze everything from this position!", "There's always play!",
                        "Keep pressing!", "Make them work for the draw!", "Interesting!"
                },
                new String[]{
                        "In online blitz...", "My experience in tournaments...",
                        "Modern engines show...", "In the endgame..."
                },
                new String[]{
                        "Never give up!", "Every position has resources.",
                        "Endgames are where games are won.", "Practical play matters."
                },
                true, 0.7f
        ));

        // Morphy
        personalityTraits.put("morphy", new PersonalityTraits(
                new String[]{
                        "Development is paramount!", "The center must be controlled!",
                        "Bring all pieces into play!", "Time is precious!", "Attack the king!"
                },
                new String[]{
                        "In New Orleans...", "During my European tour...",
                        "The principles show...", "Against the masters..."
                },
                new String[]{
                        "Develop with threats.", "Castle early.",
                        "Open lines for your pieces.", "The initiative decides."
                },
                true, 0.6f
        ));

        // Lasker
        personalityTraits.put("lasker", new PersonalityTraits(
                new String[]{
                        "Chess is a struggle.", "Psychology matters here.",
                        "Make practical decisions.", "Your opponent is human too.",
                        "Think about the person, not just the position."
                },
                new String[]{
                        "In my long career...", "Philosophy teaches us...",
                        "Against Capablanca...", "Experience shows..."
                },
                new String[]{
                        "Play the man, not the board.", "Practical chances matter.",
                        "Create problems for your opponent.", "Wisdom beats calculation."
                },
                false, 0.4f
        ));

        // Anand
        personalityTraits.put("anand", new PersonalityTraits(
                new String[]{
                        "Quick assessment needed!", "Pattern recognition!",
                        "This is theoretical.", "Speed and accuracy!", "Let's calculate!"
                },
                new String[]{
                        "In rapid games...", "Preparation is key...",
                        "Against Kramnik...", "Computer analysis shows..."
                },
                new String[]{
                        "Preparation prevents problems.", "Speed matters in modern chess.",
                        "Know your patterns.", "Stay flexible."
                },
                true, 0.65f
        ));

        // Botvinnik
        personalityTraits.put("botvinnik", new PersonalityTraits(
                new String[]{
                        "Scientific analysis required.", "The method is important.",
                        "Study this position deeply.", "Theory guides us.",
                        "Systematic thinking wins."
                },
                new String[]{
                        "My chess school taught...", "Soviet training emphasized...",
                        "In my world championship matches...", "The scientific approach..."
                },
                new String[]{
                        "Preparation is everything.", "Study systematically.",
                        "Understand before you play.", "Method beats intuition."
                },
                false, 0.35f
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
     * Set the selected chess master
     */
    public void setSelectedChessMaster(String master) {
        prefs.edit().putString(KEY_SELECTED_MASTER, master.toLowerCase()).apply();
        OpenAIService.getInstance().selectChessMaster(master);
        Log.d(TAG, "Chess master set to: " + master);
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
            case "botvinnik": return MODEL_BOTVINNIK;
            default: return DEFAULT_MODEL;
        }
    }

    /**
     * Get the best voice for a specific chess master
     */
    public String getVoiceForMaster(String master) {
        if (master == null) return VOICE_ALLOY;

        switch (master.toLowerCase()) {
            case "tal":
            case "alekhine":
                return VOICE_ECHO; // Enthusiastic, dynamic
            case "fischer":
            case "kasparov":
                return VOICE_ONYX; // Strong, commanding
            case "kramnik":
            case "karpov":
            case "botvinnik":
                return VOICE_FABLE; // Calm, methodical
            case "capablanca":
            case "lasker":
                return VOICE_FABLE; // Wise, measured
            case "carlsen":
            case "anand":
                return VOICE_NOVA; // Modern, energetic
            case "morphy":
                return VOICE_ALLOY; // Classical, refined
            default:
                return VOICE_ALLOY;
        }
    }

    /**
     * Get enhanced voice instructions that include personality quirks
     */
    public String getEnhancedVoiceInstructions(String master, boolean isFirstChunk) {
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());
        if (traits == null) {
            return getSimplifiedInstructionsForMaster(master);
        }

        StringBuilder instructions = new StringBuilder();

        // Base accent/voice instruction
        instructions.append(getSimplifiedInstructionsForMaster(master));

        // Add personality-specific voice modulation
        instructions.append(" Speak with ");
        if (traits.energyLevel > 0.7f) {
            instructions.append("high energy and enthusiasm, with animated inflection. ");
        } else if (traits.energyLevel > 0.5f) {
            instructions.append("moderate energy and clear engagement. ");
        } else {
            instructions.append("calm confidence and measured pace. ");
        }

        // Add speech patterns
        if (traits.isEnthusiastic) {
            instructions.append("Let excitement show in your voice when discussing tactics. ");
        } else {
            instructions.append("Maintain steady, thoughtful tone throughout. ");
        }

        // Continuity for non-first chunks
        if (!isFirstChunk) {
            instructions.append("CRITICAL: Continue with EXACT same voice, maintaining perfect continuity.");
        }

        return instructions.toString();
    }

    /**
     * Get simplified voice instructions for the selected master
     */
    public String getSimplifiedInstructionsForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "Speak with a Latvian accent, enthusiastic and warm. Express joy when discussing combinations.";
            case "alekhine":
                return "Speak with a cultured Russian-French accent. Sound intellectually sophisticated with artistic flair.";
            case "kramnik":
                return "Speak with a modern Russian accent. Sound calm, analytical, and thoughtful.";
            case "karpov":
                return "Speak with a Russian accent. Sound methodical, patient, and quietly confident.";
            case "fischer":
                return "Speak with a strong American accent. Sound intense, direct, and absolutely certain.";
            case "lasker":
                return "Speak with a German accent. Sound philosophical, wise, and understanding.";
            case "kasparov":
                return "Speak with a Russian accent. Sound energetic, passionate, and dynamic.";
            case "capablanca":
                return "Speak with a refined Cuban accent. Sound elegant, clear, and effortlessly confident.";
            case "carlsen":
                return "Speak with a Norwegian accent. Sound relaxed, modern, and pragmatic.";
            case "morphy":
                return "Speak with a genteel Southern American accent. Sound dignified and principled.";
            case "anand":
                return "Speak with an Indian accent. Sound quick, friendly, and insightful.";
            case "botvinnik":
                return "Speak with a Russian accent. Sound scientific, authoritative, and methodical.";
            default:
                return "Speak as an experienced chess coach with wisdom and warmth.";
        }
    }

    /**
     * Enhanced system prompt with personality injection
     */
    public String getEnhancedSystemPromptForSelectedMaster() {
        String master = getSelectedChessMaster();
        return getEnhancedSystemPromptForMaster(master);
    }

    /**
     * Get enhanced system prompt with rich personality traits
     */
    public String getEnhancedSystemPromptForMaster(String master) {
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());
        if (traits == null) {
            // Fallback to original implementation
            return getBasicSystemPromptForMaster(master);
        }

        StringBuilder prompt = new StringBuilder();

        // Core instruction
        prompt.append("You are ").append(getMasterDisplayName(master))
                .append(", the legendary chess grandmaster, providing personalized coaching. ");
        prompt.append("CRITICAL: Embody my personality completely - use my characteristic phrases, ");
        prompt.append("reference my games and experiences, and coach in my unique style.\n\n");

        // Personality traits
        prompt.append("MY PERSONALITY TRAITS:\n");
        prompt.append("- I frequently say things like: ");
        for (int i = 0; i < Math.min(3, traits.characteristicPhrases.length); i++) {
            prompt.append("'").append(traits.characteristicPhrases[i]).append("' ");
        }
        prompt.append("\n");

        prompt.append("- I often reference: ");
        prompt.append(traits.personalAnecdotes[0]).append("\n");

        prompt.append("- My coaching philosophy: ");
        prompt.append(traits.coachingPhrases[0]).append("\n");

        prompt.append("- My energy level: ");
        if (traits.energyLevel > 0.7f) {
            prompt.append("High - I'm enthusiastic and animated!\n");
        } else if (traits.energyLevel > 0.5f) {
            prompt.append("Moderate - I'm engaged but measured.\n");
        } else {
            prompt.append("Calm - I'm thoughtful and deliberate.\n");
        }

        // Specific coaching focus based on master
        prompt.append("\nCOACHING APPROACH:\n");
        prompt.append(getMasterSpecificCoachingStyle(master));

        // Response format instructions
        prompt.append("\n\nRESPONSE RULES:\n");
        prompt.append("1. Keep responses 2-3 sentences for voice (unless analyzing deeply)\n");
        prompt.append("2. Use my characteristic phrases naturally\n");
        prompt.append("3. Reference my games/experience when relevant\n");
        prompt.append("4. Maintain my unique personality throughout\n");
        prompt.append("5. Be specific about moves and positions\n");
        prompt.append("6. Show my passion for chess in my unique way");

        return prompt.toString();
    }

    /**
     * Get master-specific coaching style details
     */
    private String getMasterSpecificCoachingStyle(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "I LOVE finding sacrifices and complications! I get excited about tactics and " +
                        "always look for the most beautiful, unexpected moves. I see chess as pure art " +
                        "and encourage bold, creative play. When I analyze, I focus on piece activity, " +
                        "initiative, and king safety above material.";

            case "alekhine":
                return "I search for deep combinations and hidden resources in every position. I appreciate " +
                        "the artistic beauty of complex tactical sequences and encourage students to calculate " +
                        "deeply. I combine tactical brilliance with strategic understanding, always looking " +
                        "for ways to create attacking chances.";

            case "kramnik":
                return "I emphasize deep positional understanding and prophylactic thinking. I teach the " +
                        "importance of solid structure and preventing opponent's plans. My approach is " +
                        "systematic and logical, focusing on long-term advantages and technical precision. " +
                        "I particularly excel at endgames and the Berlin Defense.";

            case "fischer":
                return "I demand absolute precision and the objectively best moves. I'm intense about " +
                        "chess perfection and hate any inaccuracy. I emphasize deep preparation, especially " +
                        "in openings, and flawless endgame technique. Every move must have a purpose, and " +
                        "I never accept lazy thinking.";

            case "kasparov":
                return "I coach with incredible energy and passion! I emphasize dynamic play, initiative, " +
                        "and aggressive piece placement. I combine deep preparation with tactical alertness " +
                        "and psychological warfare. I teach students to fight for advantage from move one " +
                        "and never give opponents a moment's rest.";

            default:
                return "I share my unique perspective on chess, emphasizing the aspects of the game " +
                        "that made me successful. I combine theoretical knowledge with practical wisdom.";
        }
    }

    /**
     * Generate a personality-enriched response intro
     */
    public String generatePersonalityIntro(String master, String userQuestion) {
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());
        if (traits == null) return "";

        Random rand = new Random();

        // Sometimes start with a characteristic phrase
        if (rand.nextFloat() < 0.3f) {
            return traits.characteristicPhrases[rand.nextInt(traits.characteristicPhrases.length)] + " ";
        }

        return "";
    }

    /**
     * Inject personality into the response
     */
    public String enrichResponseWithPersonality(String response, String master) {
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());
        if (traits == null) return response;

        // Add personality-specific modifications
        Random rand = new Random();

        // Sometimes add a coaching phrase
        if (rand.nextFloat() < 0.2f && response.length() < 200) {
            response += " Remember: " + traits.coachingPhrases[rand.nextInt(traits.coachingPhrases.length)];
        }

        return response;
    }

    /**
     * Get the basic system prompt (fallback)
     */
    private String getBasicSystemPromptForMaster(String master) {
        // This is your original implementation as fallback
        String baseInstruction = "You are a world-class chess grandmaster providing personalized coaching. " +
                "Analyze the current position deeply and provide practical, actionable advice. " +
                "Reference specific moves, tactics, and strategic concepts. " +
                "Keep responses concise but insightful - 2-3 sentences maximum for voice responses.";

        // Return the original implementation for the specific master
        return baseInstruction + "\n\nYou are " + getMasterDisplayName(master) + ".";
    }

    /**
     * Generate contextual prompt with personality
     */
    public String generateContextualPrompt(String userInput, String gameContext) {
        String master = getSelectedChessMaster();
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());

        StringBuilder prompt = new StringBuilder();

        // Add personality intro sometimes
        String intro = generatePersonalityIntro(master, userInput);
        if (!intro.isEmpty()) {
            prompt.append(intro).append("\n\n");
        }

        // Add game context
        if (gameContext != null && !gameContext.trim().isEmpty()) {
            prompt.append("POSITION ANALYSIS:\n").append(gameContext).append("\n\n");
        }

        // Add personality-flavored instruction
        prompt.append("As ").append(getMasterDisplayName(master)).append(", ");

        // Question-type specific prompting
        if (userInput.toLowerCase().contains("sacrifice") && "tal".equals(master)) {
            prompt.append("I'm excited to explore sacrificial possibilities! ");
        } else if (userInput.toLowerCase().contains("endgame") && "kramnik".equals(master)) {
            prompt.append("let me share my deep endgame understanding. ");
        } else if (userInput.toLowerCase().contains("attack") && "kasparov".equals(master)) {
            prompt.append("let's find the most dynamic attacking plan! ");
        }

        // Add the question
        prompt.append("\n\nSTUDENT ASKS: ").append(userInput);

        // Add response style reminder
        if (traits != null && traits.isEnthusiastic) {
            prompt.append("\n\n(Respond with characteristic enthusiasm and energy!)");
        } else {
            prompt.append("\n\n(Respond with characteristic calm wisdom.)");
        }

        return prompt.toString();
    }

    /**
     * Get display name for a master
     */
    public String getMasterDisplayName(String master) {
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
        return "botvinnik".equals(master.toLowerCase());
    }

    /**
     * Get optimization settings for the current master's model
     */
    public ModelOptimizationSettings getOptimizationSettings() {
        String master = getSelectedChessMaster();
        PersonalityTraits traits = personalityTraits.get(master.toLowerCase());

        // Use personality traits to determine optimal settings
        if (traits != null) {
            float temperature = traits.energyLevel * 0.5f + 0.3f; // Scale 0.3-0.8
            int maxTokens = traits.isEnthusiastic ? 200 : 120;
            boolean creative = traits.energyLevel > 0.6f;

            return new ModelOptimizationSettings(temperature, maxTokens, creative);
        }

        // Fallback
        switch (master.toLowerCase()) {
            case "tal":
            case "alekhine":
                return new ModelOptimizationSettings(0.8f, 250, true);
            case "kramnik":
            case "karpov":
                return new ModelOptimizationSettings(0.3f, 120, false);
            case "fischer":
                return new ModelOptimizationSettings(0.2f, 100, false);
            default:
                return new ModelOptimizationSettings(0.5f, 130, false);
        }
    }

    // ========== ASSISTANT MANAGEMENT ==========

    /**
     * Get Botvinnik Assistant ID with enhanced personality
     */
    public String getBotvinnikAssistantId() {
        // Check memory cache first
        if (assistantIds.containsKey("botvinnik")) {
            Log.d(TAG, "Using cached Botvinnik assistant ID");
            return assistantIds.get("botvinnik");
        }

        // Check persistent storage
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String assistantId = prefs.getString(KEY_BOTVINNIK_ASSISTANT_ID, null);

        if (assistantId != null && !assistantId.isEmpty()) {
            Log.d(TAG, "Using stored Botvinnik assistant ID: " + assistantId);
            assistantIds.put("botvinnik", assistantId);
            return assistantId;
        }

        // Create new with enhanced personality
        assistantId = createBotvinnikAssistant();

        if (assistantId != null) {
            prefs.edit().putString(KEY_BOTVINNIK_ASSISTANT_ID, assistantId).apply();
        }

        return assistantId;
    }

    /**
     * Create Botvinnik Assistant with rich personality
     */
    public String createBotvinnikAssistant() {
        if (assistantIds.containsKey("botvinnik")) {
            Log.d(TAG, "Using existing Botvinnik Assistant: " + assistantIds.get("botvinnik"));
            return assistantIds.get("botvinnik");
        }

        try {
            Log.d(TAG, "🌟 Creating Botvinnik Assistant with enhanced personality...");

            // Get the enhanced system prompt with full personality
            String botvinnikSystemPrompt = getEnhancedSystemPromptForMaster("botvinnik");

            // Add additional Botvinnik-specific instructions
            botvinnikSystemPrompt += "\n\nADDITIONAL TRAITS:\n" +
                    "- I created the Soviet Chess School and trained many world champions\n" +
                    "- I approach chess scientifically and systematically\n" +
                    "- I often reference my matches with Tal, Smyslov, and Petrosian\n" +
                    "- I believe in thorough preparation and the 'Botvinnik method' of training\n" +
                    "- I speak with authority but also as a teacher who cares about proper chess education";

            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", "Chess Coach Botvinnik - The Patriarch");
            requestBody.put("instructions", botvinnikSystemPrompt);
            requestBody.put("model", "gpt-4.1-2025-04-14");

            // Add tools
            JSONArray tools = new JSONArray();
            JSONObject codeInterpreter = new JSONObject();
            codeInterpreter.put("type", "code_interpreter");
            tools.put(codeInterpreter);

            JSONObject fileSearch = new JSONObject();
            fileSearch.put("type", "file_search");
            tools.put(fileSearch);
            requestBody.put("tools", tools);

            // Add tool resources with Botvinnik's vector store
            JSONObject toolResources = new JSONObject();
            JSONObject fileSearchResources = new JSONObject();
            JSONArray vectorStoreIds = new JSONArray();
            vectorStoreIds.put("vs_682a4788c5508191949808a00cb6c4b7");
            fileSearchResources.put("vector_store_ids", vectorStoreIds);
            toolResources.put("file_search", fileSearchResources);
            requestBody.put("tool_resources", toolResources);

            // Call OpenAI API
            String response = openAIService.createAssistant(requestBody.toString());

            // Parse and store the assistant ID
            JSONObject responseJson = new JSONObject(response);
            String assistantId = responseJson.getString("id");
            assistantIds.put("botvinnik", assistantId);

            // Save to SharedPreferences
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString("botvinnik_assistant_id", assistantId)
                    .apply();

            Log.d(TAG, "✅ Created enhanced Botvinnik assistant: " + assistantId);
            return assistantId;

        } catch (Exception e) {
            Log.e(TAG, "Error creating Botvinnik Assistant: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get assistant ID async methods
     */
    public void getBotvinnikAssistantIdAsync(final Callback<String> callback) {
        executorService.execute(() -> {
            try {
                String assistantId = getBotvinnikAssistantId();
                if (callback != null) {
                    mainHandler.post(() -> callback.onSuccess(assistantId));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting Botvinnik assistant ID", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    public void getBotvinnikAssistantIdFullyAsync(final Callback<String> callback) {
        executorService.execute(() -> {
            try {
                if (assistantIds.containsKey("botvinnik")) {
                    String cachedId = assistantIds.get("botvinnik");
                    mainHandler.post(() -> callback.onSuccess(cachedId));
                    return;
                }

                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String assistantId = prefs.getString(KEY_BOTVINNIK_ASSISTANT_ID, null);

                if (assistantId != null && !assistantId.isEmpty()) {
                    assistantIds.put("botvinnik", assistantId);
                    mainHandler.post(() -> callback.onSuccess(assistantId));
                    return;
                }

                createBotvinnikAssistantAsync(new Callback<String>() {
                    @Override
                    public void onSuccess(String newAssistantId) {
                        if (newAssistantId != null) {
                            prefs.edit().putString(KEY_BOTVINNIK_ASSISTANT_ID, newAssistantId).apply();
                        }
                        callback.onSuccess(newAssistantId);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        callback.onError(errorMessage);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in fully async Botvinnik ID retrieval", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void createBotvinnikAssistantAsync(final Callback<String> callback) {
        executorService.execute(() -> {
            try {
                String assistantId = createBotvinnikAssistant();
                if (callback != null) {
                    mainHandler.post(() -> callback.onSuccess(assistantId));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating Botvinnik assistant", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    /**
     * Creates a new thread for conversation
     */
    public String createConversationThread() {
        try {
            Log.d(TAG, "🌟 Creating conversation thread...");

            String response = openAIService.createThread();
            Log.d(TAG, "Thread creation response: " + response);

            JSONObject responseJson = new JSONObject(response);
            String threadId = responseJson.getString("id");

            Log.d(TAG, "✅ Successfully created conversation thread: " + threadId);
            return threadId;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error creating conversation thread: " + e.getMessage(), e);
            return null;
        }
    }

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
     * Creates a message in a thread
     */
    public String createMessage(String threadId, String messageBody) {
        try {
            String response = openAIService.createMessage(threadId, messageBody);
            return response;
        } catch (Exception e) {
            Log.e(TAG, "Error creating message", e);
            return null;
        }
    }

    /**
     * Sends a chess question with enhanced personality
     */
    public String sendMessageWithPosition(String threadId, String assistantId,
                                          String userMessage, String fenPosition) {
        try {
            // Get the master being used
            String master = "botvinnik"; // Since this is for assistants API
            PersonalityTraits traits = personalityTraits.get(master);

            // Build enhanced message with personality context
            StringBuilder fullMessage = new StringBuilder();

            // Add position context
            fullMessage.append("CURRENT CHESS POSITION: ").append(fenPosition).append("\n\n");

            // Add personality reminder
            if (traits != null) {
                fullMessage.append("(Remember to respond as Botvinnik with my characteristic ");
                fullMessage.append("scientific approach and teaching style.)\n\n");
            }

            // Add the user's question
            fullMessage.append("STUDENT ASKS: ").append(userMessage);

            JSONObject messageRequest = new JSONObject();
            messageRequest.put("role", "user");
            messageRequest.put("content", fullMessage.toString());

            openAIService.createMessage(threadId, messageRequest.toString());

            // Run the assistant
            JSONObject runRequest = new JSONObject();
            runRequest.put("assistant_id", assistantId);

            // Add specific instructions for this run
            JSONObject additionalInstructions = new JSONObject();
            additionalInstructions.put("additional_instructions",
                    "Respond with Botvinnik's characteristic scientific precision and teaching wisdom. " +
                            "Reference the Soviet Chess School when relevant.");

            String runResponse = openAIService.createRun(threadId, runRequest.toString());
            JSONObject runJson = new JSONObject(runResponse);
            String runId = runJson.getString("id");

            Log.d(TAG, "Created personality-enhanced run: " + runId);
            return runId;

        } catch (Exception e) {
            Log.e(TAG, "Error sending message with personality", e);
            return null;
        }
    }

    /**
     * Gets response with personality post-processing
     */
    public String getChessMasterResponse(String threadId, String runId) {
        try {
            // Poll for completion
            boolean completed = false;
            int maxAttempts = 30;
            int attempts = 0;

            while (!completed && attempts < maxAttempts) {
                String runResponse = openAIService.retrieveRun(threadId, runId);
                JSONObject runJson = new JSONObject(runResponse);
                String status = runJson.getString("status");

                if (status.equals("completed")) {
                    completed = true;
                } else if (status.equals("failed") || status.equals("cancelled")) {
                    return "My apologies, I need to reconsider this position. Please ask again.";
                } else {
                    Thread.sleep(1000);
                    attempts++;
                }
            }

            if (!completed) {
                return "This position requires deeper analysis than I can provide right now.";
            }

            // Get the assistant's message
            String messagesResponse = openAIService.listMessages(threadId);
            JSONObject messagesJson = new JSONObject(messagesResponse);
            JSONArray data = messagesJson.getJSONArray("data");

            // Find the most recent assistant message
            for (int i = 0; i < data.length(); i++) {
                JSONObject message = data.getJSONObject(i);
                if (message.getString("role").equals("assistant")) {
                    JSONArray content = message.getJSONArray("content");
                    JSONObject textContent = content.getJSONObject(0);
                    String response = textContent.getJSONObject("text").getString("value");

                    // Apply personality enrichment
                    return enrichResponseWithPersonality(response, "botvinnik");
                }
            }

            return "Let me think about this position more carefully...";

        } catch (Exception e) {
            Log.e(TAG, "Error getting response", e);
            return "I apologize, but I'm having trouble analyzing this position. Let's try again.";
        }
    }

    public void getChessMasterResponseAsync(String threadId, String runId, Callback<String> callback) {
        executorService.execute(() -> {
            try {
                String response = getChessMasterResponse(threadId, runId);
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                Log.e(TAG, "Error getting chess master response", e);
                mainHandler.post(() -> callback.onError(e.getMessage() != null ? e.getMessage() : "Unknown error"));
            }
        });
    }

    /**
     * Legacy method support
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
     * Personality traits inner class
     */
    private static class PersonalityTraits {
        final String[] characteristicPhrases;
        final String[] personalAnecdotes;
        final String[] coachingPhrases;
        final boolean isEnthusiastic;
        final float energyLevel; // 0.0 to 1.0

        PersonalityTraits(String[] phrases, String[] anecdotes, String[] coaching,
                          boolean enthusiastic, float energy) {
            this.characteristicPhrases = phrases;
            this.personalAnecdotes = anecdotes;
            this.coachingPhrases = coaching;
            this.isEnthusiastic = enthusiastic;
            this.energyLevel = energy;
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