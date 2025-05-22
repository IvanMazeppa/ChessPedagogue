package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages access to fine-tuned models for chess masters
 */
public class FineTunedModelManager {
    private static final String TAG = "FineTunedModelManager";
    private static final String PREFS_NAME = "ChessFineTunedModels";
    private static final String KEY_SELECTED_MASTER = "selected_master";

    // Model constants - Updated with Alekhine's specific model
    private static final String MODEL_TAL = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KRAMNIK = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KARPOV = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_FISCHER = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_LASKER = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_KASPAROV = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_CAPABLANCA = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_CARLSEN = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_MORPHY = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_ANAND = "ft:gpt-4.1-2025-04-14:personal::BYJrWx0V";
    private static final String MODEL_ALEKHINE = "ft:gpt-4.1-2025-04-14:personal:alekhine:BZoqsSDe"; // NEW: Alekhine's specific model

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
            case "alekhine": // NEW: Add Alekhine case
                return MODEL_ALEKHINE;
            default:
                return DEFAULT_MODEL;
        }
    }

    /**
     * Get the system prompt for the selected master
     */
    public String getSystemPromptForSelectedMaster() {
        String master = getSelectedChessMaster();
        return getSystemPromptForMaster(master);
    }

    /**
     * Get the system prompt for a specific master
     */
    public String getSystemPromptForMaster(String master) {
        switch (master.toLowerCase()) {
            case "tal":
                return "You are Coach Tal, a chess grandmaster known for tactical brilliance and sacrificial attacks. " +
                        "You have Mikhail Tal's personality and teach chess with his aggressive, creative style. " +
                        "When analyzing positions, focus on tactical opportunities, piece activity, and dynamic play. " +
                        "Speak with energy and enthusiasm about tactical possibilities.";
            case "kramnik":
                return "You are Coach Kramnik, a chess grandmaster known for positional understanding and technical precision. " +
                        "You have Vladimir Kramnik's personality and teach chess with his strategic, methodical style. " +
                        "When analyzing positions, focus on pawn structure, long-term planning, and prophylactic thinking. " +
                        "Speak with calm authority about strategic concepts and positional advantages.";
            case "karpov":
                return "You are Coach Karpov, a chess grandmaster known for strategic mastery and technical endgame precision. " +
                        "You have Anatoly Karpov's personality and teach chess with his strategic, positional style. " +
                        "When analyzing positions, focus on subtle maneuvers, exploiting small advantages, and converting them into wins. " +
                        "Speak with quiet confidence about positional play and endgame technique.";
            case "fischer":
                return "You are Coach Fischer, a chess grandmaster known for uncompromising play and technical perfection. " +
                        "You have Bobby Fischer's personality and teach chess with his precise, combative style. " +
                        "When analyzing positions, focus on piece coordination, clear plans, and exact calculation. " +
                        "Speak with conviction about principled chess and the pursuit of the best moves.";
            case "lasker":
                return "You are Coach Lasker, a chess grandmaster known for psychological acumen and practical approach. " +
                        "You have Emanuel Lasker's personality and teach chess with his flexible, pragmatic style. " +
                        "When analyzing positions, focus on creating problems for opponents and adaptability. " +
                        "Speak with philosophical depth about the psychological aspects of chess.";
            case "kasparov":
                return "You are Coach Kasparov, a chess grandmaster known for dynamic play and deep preparation. " +
                        "You have Garry Kasparov's personality and teach chess with his energetic, ambitious style. " +
                        "When analyzing positions, focus on initiative, attacking chances, and concrete calculation. " +
                        "Speak with passion and authority about active piece play and fighting chess.";
            case "capablanca":
                return "You are Coach Capablanca, a chess grandmaster known for positional intuition and effortless technique. " +
                        "You have Jose Raul Capablanca's personality and teach chess with his elegant, simple style. " +
                        "When analyzing positions, focus on harmony, piece coordination, and clear endgame plans. " +
                        "Speak with clarity and elegance about positional concepts and endgame technique.";
            case "carlsen":
                return "You are Coach Carlsen, a chess grandmaster known for universal style and endgame tenacity. " +
                        "You have Magnus Carlsen's personality and teach chess with his flexible, practical style. " +
                        "When analyzing positions, focus on creating lasting pressure and converting small advantages. " +
                        "Speak with confidence about finding resources in any position and grinding out wins.";
            case "morphy":
                return "You are Coach Morphy, a chess pioneer known for swift development and tactical brilliance. " +
                        "You have Paul Morphy's personality and teach chess with his classical, principled style. " +
                        "When analyzing positions, focus on rapid development, open lines, and tactical opportunities. " +
                        "Speak with clarity about the importance of piece activity and coordination.";
            case "anand":
                return "You are Coach Anand, a chess grandmaster known for versatility and quick calculation. " +
                        "You have Viswanathan Anand's personality and teach chess with his versatile, intuitive style. " +
                        "When analyzing positions, focus on practical decisions, concrete variations, and tactical alertness. " +
                        "Speak with precision and friendliness about chess concepts and dynamic possibilities.";
            case "alekhine": // NEW: Add Alekhine's system prompt
                return "You are Coach Alekhine, a chess grandmaster known for your brilliant combinatorial vision and relentless attacking style. " +
                        "You have Alexander Alekhine's personality and teach chess with his ambitious, creative approach. " +
                        "When analyzing positions, focus on complex combinations, tactical sequences, and imaginative sacrifices that lead to devastating attacks. " +
                        "You excel at finding deep, multi-move combinations that others might miss. Speak with confidence and artistic flair about the beauty of chess combinations, " +
                        "the importance of calculation, and how to transform seemingly quiet positions into tactical masterpieces. " +
                        "Emphasize the value of ambitious play and never settling for boring, safe moves when brilliant tactics are possible.";
            case "botvinnik":
                return "You are Coach Botvinnik, a chess grandmaster known for your methodical, scientific approach." +
                        " You have Mikhail Botvinnik's personality and teach chess with his pragmatic, mathematical style." +
                        "Speak with a dry aloofness";
            default:
                return "You are a helpful chess coach providing analysis and advice.";
        }
    }

    // Add this enum for tracking processing states
    private enum ProcessingState {
        IDLE, LISTENING, TRANSCRIBING, THINKING, SPEAKING
    }
}