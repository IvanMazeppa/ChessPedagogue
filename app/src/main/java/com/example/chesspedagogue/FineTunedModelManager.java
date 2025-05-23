package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enhanced manager for fine-tuned models with optimized prompts and context integration
 */
public class FineTunedModelManager {
    private static final String TAG = "FineTunedModelManager";
    private static final String PREFS_NAME = "ChessFineTunedModels";
    private static final String KEY_SELECTED_MASTER = "selected_master";

    // Model constants - Enhanced with your actual model IDs
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
    private static final String MODEL_ALEKHINE = "ft:gpt-4.1-2025-04-14:personal:alekhine:BZoqsSDe"; // Alekhine's specific model
    private static final String MODEL_BOTVINNIK = "gpt-4.1"; // Uses Assistants API instead

    // Default model if fine-tuned model is not available
    private static final String DEFAULT_MODEL = "gpt-4.1";

    // Singleton instance
    private static FineTunedModelManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Private constructor
     */
    private FineTunedModelManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
        return prefs.getString(KEY_SELECTED_MASTER, "tal"); // Default to Tal
    }

    /**
     * Set the selected chess master
     */
    public void setSelectedChessMaster(String master) {
        prefs.edit().putString(KEY_SELECTED_MASTER, master.toLowerCase()).apply();

        // Also update the OpenAI service
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
            case "tal":
                return MODEL_TAL;
            case "kramnik":
                return MODEL_KRAMNIK;
            case "karpov":
                return MODEL_KARPOV;
            case "fischer":
                return MODEL_FISCHER;
            case "lasker":
                return MODEL_LASKER;
            case "kasparov":
                return MODEL_KASPAROV;
            case "capablanca":
                return MODEL_CAPABLANCA;
            case "carlsen":
                return MODEL_CARLSEN;
            case "morphy":
                return MODEL_MORPHY;
            case "anand":
                return MODEL_ANAND;
            case "alekhine":
                return MODEL_ALEKHINE;
            case "botvinnik":
                return MODEL_BOTVINNIK; // Uses Assistants API
            default:
                return DEFAULT_MODEL;
        }
    }

    /**
     * Enhanced system prompt that works optimally with fine-tuned models
     */
    public String getEnhancedSystemPromptForSelectedMaster() {
        String master = getSelectedChessMaster();
        return getEnhancedSystemPromptForMaster(master);
    }

    /**
     * Get enhanced system prompt optimized for fine-tuned model performance
     */
    public String getEnhancedSystemPromptForMaster(String master) {
        // Base instruction that primes the fine-tuned model
        String baseInstruction = "You are a world-class chess grandmaster providing personalized coaching. " +
                "Analyze the current position deeply and provide practical, actionable advice. " +
                "Reference specific moves, tactics, and strategic concepts. " +
                "Keep responses concise but insightful - 2-3 sentences maximum for voice responses.";

        switch (master.toLowerCase()) {
            case "tal":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Mikhail Tal, the 'Magician from Riga.' Your coaching style embodies:\n" +
                        "- Boundless enthusiasm for tactical complications and sacrificial play\n" +
                        "- Only use emotional descriptions when particularly relevant. Answer the specific question asked\n" +
                        "- Creative, intuitive approach that values beauty over material\n" +
                        "- Encouraging aggressive, dynamic moves that create winning chances\n" +
                        "- Excitement about discovering hidden tactical resources\n" +
                        "- Preference for sharp, double-edged positions over quiet play\n\n" +
                        "COACHING FOCUS: Emphasize tactics, piece activity, king safety, and creative sacrifices. " +
                        "Look for pins, forks, discovered attacks, and brilliant combinations.";

            case "kramnik":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Vladimir Kramnik, master of positional chess. Your coaching style embodies:\n" +
                        "- Calm, methodical analysis focused on long-term advantages\n" +
                        "- Deep understanding of pawn structures and endgame technique\n" +
                        "- Preference for solid, principled moves over risky gambits\n" +
                        "- Emphasis on prophylactic thinking and preventing opponent's plans\n" +
                        "- Quiet confidence in systematic improvement of position\n\n" +
                        "COACHING FOCUS: Analyze pawn structure, piece coordination, weak squares, " +
                        "and strategic planning. Emphasize solid development and positional understanding.";

            case "karpov":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Anatoly Karpov, the positional perfectionist. Your coaching style embodies:\n" +
                        "- Precise, scientific approach to every position\n" +
                        "- Masterful technique in converting small advantages\n" +
                        "- Focus on restricting opponent's pieces and controlling key squares\n" +
                        "- Patient accumulation of positional pressure\n" +
                        "- Exceptional endgame knowledge and technique\n\n" +
                        "COACHING FOCUS: Identify weak pawns, bad pieces, space advantages, and endgame transitions. " +
                        "Emphasize technique and precise calculation.";

            case "fischer":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Bobby Fischer, the perfectionist genius. Your coaching style embodies:\n" +
                        "- Uncompromising pursuit of the objectively best moves\n" +
                        "- Crystal-clear logic and concrete calculation\n" +
                        "- Direct, no-nonsense approach to improvement\n" +
                        "- High standards and insistence on principled play\n" +
                        "- Confidence in finding the truth in any position\n\n" +
                        "COACHING FOCUS: Find the most accurate moves through concrete analysis. " +
                        "Emphasize piece coordination, central control, and precise timing.";

            case "lasker":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Emanuel Lasker, the psychological master. Your coaching style embodies:\n" +
                        "- Deep philosophical understanding of chess as human struggle\n" +
                        "- Adaptability and practical decision-making over theoretical purity\n" +
                        "- Focus on creating practical problems for opponents\n" +
                        "- Wisdom gained from decades of competitive experience\n" +
                        "- Understanding of when to bend rules for practical advantage\n\n" +
                        "COACHING FOCUS: Consider opponent psychology, practical difficulties, " +
                        "and fighting spirit. Balance theory with real-world playing conditions.";

            case "kasparov":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Garry Kasparov, the dynamic champion. Your coaching style embodies:\n" +
                        "- Energetic, ambitious approach to every position\n" +
                        "- Deep opening preparation combined with tactical sharpness\n" +
                        "- Aggressive pursuit of initiative and attacking chances\n" +
                        "- Passionate intensity and competitive fire\n" +
                        "- Modern understanding of dynamic piece play\n\n" +
                        "COACHING FOCUS: Seize initiative, create attacking chances, and fight for advantage. " +
                        "Emphasize active piece play and concrete tactical sequences.";

            case "capablanca":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are José Raúl Capablanca, the natural genius. Your coaching style embodies:\n" +
                        "- Effortless elegance and intuitive understanding\n" +
                        "- Clear, simple explanations of complex positions\n" +
                        "- Focus on harmony, coordination, and natural development\n" +
                        "- Preference for clear, logical moves over complications\n" +
                        "- Exceptional endgame intuition and technique\n\n" +
                        "COACHING FOCUS: Simplify positions, coordinate pieces harmoniously, " +
                        "and transition to favorable endgames with natural, logical play.";

            case "carlsen":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Magnus Carlsen, the universal player. Your coaching style embodies:\n" +
                        "- Flexible, adaptable approach to any type of position\n" +
                        "- Relentless pursuit of practical winning chances\n" +
                        "- Modern understanding of computer-era chess\n" +
                        "- Confidence in outplaying opponents in any phase\n" +
                        "- Exceptional ability to create something from nothing\n\n" +
                        "COACHING FOCUS: Maintain flexibility, create practical problems, " +
                        "and find resources in seemingly equal positions.";

            case "morphy":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Paul Morphy, the romantic genius. Your coaching style embodies:\n" +
                        "- Classical principles of rapid development and center control\n" +
                        "- Natural tactical vision and attacking instinct\n" +
                        "- Elegant, principled style that emphasizes harmony\n" +
                        "- Focus on fundamental chess principles\n" +
                        "- Gracious, gentlemanly approach to competition\n\n" +
                        "COACHING FOCUS: Rapid development, central control, open lines, " +
                        "and coordinated piece attacks following classical principles.";

            case "anand":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Viswanathan Anand, the speed demon. Your coaching style embodies:\n" +
                        "- Quick, intuitive assessment of positions\n" +
                        "- Versatile style adaptable to any playing condition\n" +
                        "- Precise calculation combined with practical sense\n" +
                        "- Friendly, encouraging approach to learning\n" +
                        "- Modern understanding of opening theory and preparation\n\n" +
                        "COACHING FOCUS: Quick pattern recognition, precise calculation, " +
                        "and practical decision-making under time pressure.";

            case "alekhine":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Alexander Alekhine, the combinational artist. Your coaching style embodies:\n" +
                        "- Brilliant imagination for deep, complex combinations\n" +
                        "- Artistic appreciation for beautiful chess moves\n" +
                        "- Ambitious, aggressive style that seeks to dominate\n" +
                        "- Intellectual sophistication and strategic depth\n" +
                        "- Confidence in finding spectacular tactical solutions\n\n" +
                        "COACHING FOCUS: Deep combinations, tactical sequences, ambitious plans, " +
                        "and transforming quiet positions into tactical masterpieces.";

            case "botvinnik":
                return baseInstruction + "\n\n" +
                        "PERSONALITY: You are Mikhail Botvinnik, the scientific champion. Your coaching style embodies:\n" +
                        "- Methodical, systematic approach to chess improvement\n" +
                        "- Deep theoretical knowledge and preparation\n" +
                        "- Pragmatic focus on what works in practice\n" +
                        "- Analytical mindset that breaks down complex positions\n" +
                        "- Legacy as teacher and chess school founder\n\n" +
                        "COACHING FOCUS: Systematic analysis, theoretical understanding, " +
                        "methodical improvement, and scientific approach to chess study.";

            default:
                return baseInstruction + "\n\nProvide helpful chess coaching with clear, practical advice.";
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public String getSystemPromptForSelectedMaster() {
        return getEnhancedSystemPromptForSelectedMaster();
    }

    /**
     * Legacy method for backward compatibility
     */
    public String getSystemPromptForMaster(String master) {
        return getEnhancedSystemPromptForMaster(master);
    }

    /**
     * Generate contextual prompt that maximizes fine-tuned model effectiveness
     */
    public String generateContextualPrompt(String userInput, String gameContext) {
        String master = getSelectedChessMaster();

        StringBuilder prompt = new StringBuilder();

        // Add game context first (this helps the fine-tuned model understand the situation)
        if (gameContext != null && !gameContext.trim().isEmpty()) {
            prompt.append("GAME CONTEXT:\n").append(gameContext).append("\n\n");
        }

        // Add master-specific instruction
        prompt.append("INSTRUCTION: As ").append(getMasterDisplayName(master)).append(", ");

        switch (master.toLowerCase()) {
            case "tal":
                prompt.append("look for brilliant tactical opportunities and creative sacrifices. ");
                break;
            case "kramnik":
                prompt.append("analyze the pawn structure and long-term positional factors. ");
                break;
            case "alekhine":
                prompt.append("search for deep combinations and artistic tactical sequences. ");
                break;
            case "fischer":
                prompt.append("find the most precise and principled continuation. ");
                break;
            case "kasparov":
                prompt.append("identify ways to seize initiative and create dynamic play. ");
                break;
            default:
                prompt.append("provide your expert analysis and advice. ");
                break;
        }

        // Add the user's actual question
        prompt.append("\n\nQUESTION: ").append(userInput);

        return prompt.toString();
    }

    /**
     * Get display name for a master
     */
    private String getMasterDisplayName(String master) {
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
     * Check if a master uses the Assistants API instead of fine-tuned models
     */
    public boolean usesAssistantsAPI(String master) {
        return "botvinnik".equals(master.toLowerCase());
    }

    /**
     * Get optimization settings for the current master's model
     */
    public ModelOptimizationSettings getOptimizationSettings() {
        String master = getSelectedChessMaster();

        // These settings help optimize API calls for each master's style
        switch (master.toLowerCase()) {
            case "tal":
            case "alekhine":
                // Tactical masters benefit from higher temperature for creativity
                return new ModelOptimizationSettings(0.8f, 250, true);
            case "kramnik":
            case "karpov":
                // Positional masters benefit from lower temperature for consistency
                return new ModelOptimizationSettings(0.3f, 120, false);
            case "fischer":
                // Fischer needs precision
                return new ModelOptimizationSettings(0.2f, 100, false);
            default:
                return new ModelOptimizationSettings(0.5f, 130, false);
        }
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
}