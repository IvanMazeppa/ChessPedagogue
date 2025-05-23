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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Unified manager for chess masters - handles models, voices, assistants, and prompts
 * Consolidated from FineTunedModelManager, ChessMasterVoiceManager, and ChessMasterAgentManager
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

    // Voice model options from ChessMasterVoiceManager
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

    // Assistant management from ChessMasterAgentManager
    private final Map<String, String> assistantIds = new HashMap<>();
    private final OpenAIService openAIService;

    /**
     * Private constructor
     */
    private FineTunedModelManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.openAIService = OpenAIService.getInstance();
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
     * (Merged from ChessMasterVoiceManager)
     */
    public String getVoiceForMaster(String master) {
        if (master == null) return VOICE_ALLOY;

        switch (master.toLowerCase()) {
            case "karpov":
            case "kasparov":
                return VOICE_ONYX;
            case "fischer":
            case "tal":
            case "carlsen":
            case "anand":
                return VOICE_ECHO;
            case "capablanca":
            case "lasker":
            case "morphy":
            case "kramnik":
            case "alekhine":
                return VOICE_FABLE;
            default:
                return VOICE_ALLOY;
        }
    }

    /**
     * Get simplified voice instructions for the selected master
     * (Merged from ChessMasterVoiceManager)
     */
    public String getSimplifiedInstructionsForMaster(String master) {
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
            case "alekhine":
                return "Speak with a cultured Russian-French accent. Sound intellectually sophisticated.";
            case "botvinnik":
                return "Speak with a Russian accent. Sound scientific and authoritative.";
            default:
                return "Speak as an experienced chess coach.";
        }
    }

    /**
     * Add continuity for chunks after the first
     * (Merged from ChessMasterVoiceManager)
     */
    public String getInstructionsForChunk(String master, int chunkIndex) {
        String baseInstruction = getSimplifiedInstructionsForMaster(master);

        if (chunkIndex > 0) {
            return baseInstruction + " CRITICAL: Continue with the EXACT same voice, tone, pacing, and accent as the previous audio segment. Maintain perfect continuity as if this is one continuous recording.";
        }

        return baseInstruction;
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
     * Generate contextual prompt that maximizes fine-tuned model effectiveness
     */
    public String generateContextualPrompt(String userInput, String gameContext) {
        String master = getSelectedChessMaster();

        StringBuilder prompt = new StringBuilder();

        // Add game context first
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

    // ========== ASSISTANT MANAGEMENT (from ChessMasterAgentManager) ==========

    /**
     * Get Botvinnik Assistant ID
     */
    public String getBotvinnikAssistantId() {
        // Check memory cache first
        if (assistantIds.containsKey("botvinnik")) {
            Log.d(TAG, "Using cached Botvinnik assistant ID");
            return assistantIds.get("botvinnik");
        }

        // Check persistent storage next
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String assistantId = prefs.getString(KEY_BOTVINNIK_ASSISTANT_ID, null);

        if (assistantId != null && !assistantId.isEmpty()) {
            Log.d(TAG, "Using stored Botvinnik assistant ID: " + assistantId);
            assistantIds.put("botvinnik", assistantId);
            return assistantId;
        }

        // Create new if none exists
        assistantId = createBotvinnikAssistant();

        // Store it if creation was successful
        if (assistantId != null) {
            prefs.edit().putString(KEY_BOTVINNIK_ASSISTANT_ID, assistantId).apply();
        }

        return assistantId;
    }

    /**
     * Get Botvinnik Assistant ID asynchronously
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

    /**
     * Get Botvinnik Assistant ID fully async
     */
    public void getBotvinnikAssistantIdFullyAsync(final Callback<String> callback) {
        executorService.execute(() -> {
            try {
                // Check memory cache first
                if (assistantIds.containsKey("botvinnik")) {
                    Log.d(TAG, "Using cached Botvinnik assistant ID");
                    String cachedId = assistantIds.get("botvinnik");
                    mainHandler.post(() -> callback.onSuccess(cachedId));
                    return;
                }

                // Check persistent storage next
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String assistantId = prefs.getString(KEY_BOTVINNIK_ASSISTANT_ID, null);

                if (assistantId != null && !assistantId.isEmpty()) {
                    Log.d(TAG, "Using stored Botvinnik assistant ID: " + assistantId);
                    assistantIds.put("botvinnik", assistantId);
                    mainHandler.post(() -> callback.onSuccess(assistantId));
                    return;
                }

                // Create new if none exists
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

    /**
     * Create Botvinnik Assistant
     */
    public String createBotvinnikAssistant() {
        if (assistantIds.containsKey("botvinnik")) {
            Log.d(TAG, "Using existing Botvinnik Assistant: " + assistantIds.get("botvinnik"));
            return assistantIds.get("botvinnik");
        }

        try {
            Log.d(TAG, "🌟 STARTING to create Botvinnik Assistant...");

            String botvinnikSystemPrompt = getEnhancedSystemPromptForMaster("botvinnik");

            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", "Chess Coach Botvinnik");
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

            return assistantId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating Botvinnik Assistant: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Create Botvinnik Assistant asynchronously
     */
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
            Log.d(TAG, "🌟 STARTING to create conversation thread...");

            String response = openAIService.createThread();
            Log.d(TAG, "Thread creation response: " + response);

            JSONObject responseJson = new JSONObject(response);
            String threadId = responseJson.getString("id");

            Log.d(TAG, "✅ Successfully created conversation thread with ID: " + threadId);
            return threadId;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error creating conversation thread: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Create conversation thread asynchronously
     */
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
     * Sends a chess question to the assistant
     */
    public String sendMessageWithPosition(String threadId, String assistantId,
                                          String userMessage, String fenPosition) {
        try {
            // Combine chess position with user's message
            String fullMessage = "CHESS POSITION: " + fenPosition + "\n\n" + userMessage;

            JSONObject messageRequest = new JSONObject();
            messageRequest.put("role", "user");
            messageRequest.put("content", fullMessage);

            openAIService.createMessage(threadId, messageRequest.toString());

            // Run the assistant on this thread
            JSONObject runRequest = new JSONObject();
            runRequest.put("assistant_id", assistantId);

            // Get the run ID from the response
            String runResponse = openAIService.createRun(threadId, runRequest.toString());
            JSONObject runJson = new JSONObject(runResponse);
            String runId = runJson.getString("id");

            Log.d(TAG, "Created run with ID: " + runId);

            return runId;
        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            return null;
        }
    }

    /**
     * Checks run status and retrieves response when complete
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
                    return "Sorry, I couldn't analyze this position. Let's try again.";
                } else {
                    Thread.sleep(1000);
                    attempts++;
                }
            }

            if (!completed) {
                return "It's taking longer than expected to analyze this position. Let's try again.";
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
                    return textContent.getJSONObject("text").getString("value");
                }
            }

            return "I seem to have lost my train of thought. Can you repeat your question?";

        } catch (Exception e) {
            Log.e(TAG, "Error getting response", e);
            return "Sorry, I had trouble processing that. Let's try again.";
        }
    }

    /**
     * Gets the chess master's response asynchronously
     */
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
     * Callback interface for async operations
     */
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}