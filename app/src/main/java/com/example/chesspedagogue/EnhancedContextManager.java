package com.example.chesspedagogue;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Enhanced Context Manager for richer, more varied conversational context
 * Provides sophisticated context rotation, historical awareness, and emergent behavior
 * to prevent repetitive responses and enable more natural chess master interactions.
 */
public class EnhancedContextManager {
    private static final String TAG = "EnhancedContextManager";
    
    private static EnhancedContextManager instance;
    private final Random random;
    
    // Context tracking and rotation
    private final List<ConversationTurn> conversationHistory = new ArrayList<>();
    private final Map<String, List<String>> masterContextRotations = new HashMap<>();
    private final Map<String, Integer> contextRotationIndexes = new HashMap<>();
    
    // Emergent behavior tracking
    private final Map<String, String> lastInteractionTopics = new HashMap<>();
    private final Map<String, Long> lastInteractionTimes = new HashMap<>();
    private final Map<String, Integer> repetitionCounts = new HashMap<>();
    
    // Context variety settings
    private static final int MAX_CONVERSATION_HISTORY = 15;
    private static final int CONTEXT_ROTATION_SIZE = 4;
    private static final long CONTEXT_FRESHNESS_THRESHOLD = 30000; // 30 seconds
    
    /**
     * Represents a turn in the conversation with enhanced metadata
     */
    private static class ConversationTurn {
        final String speaker;
        final String content;
        final String contextType;
        final long timestamp;
        final String mood;
        final String topic;
        
        ConversationTurn(String speaker, String content, String contextType) {
            this.speaker = speaker;
            this.content = content;
            this.contextType = contextType;
            this.timestamp = System.currentTimeMillis();
            this.mood = extractMood(content);
            this.topic = extractTopic(content);
        }
        
        private String extractMood(String content) {
            String lower = content.toLowerCase();
            if (lower.contains("excellent") || lower.contains("brilliant") || lower.contains("beautiful")) {
                return "enthusiastic";
            } else if (lower.contains("mistake") || lower.contains("blunder") || lower.contains("wrong")) {
                return "critical";
            } else if (lower.contains("interesting") || lower.contains("curious") || lower.contains("wonder")) {
                return "contemplative";
            } else if (lower.contains("attack") || lower.contains("pressure") || lower.contains("strike")) {
                return "aggressive";
            } else {
                return "neutral";
            }
        }
        
        private String extractTopic(String content) {
            String lower = content.toLowerCase();
            if (lower.contains("tactic") || lower.contains("combination") || lower.contains("sacrifice")) {
                return "tactical";
            } else if (lower.contains("position") || lower.contains("structure") || lower.contains("pawn")) {
                return "positional";
            } else if (lower.contains("endgame") || lower.contains("ending")) {
                return "endgame";
            } else if (lower.contains("opening") || lower.contains("development")) {
                return "opening";
            } else if (lower.contains("time") || lower.contains("clock") || lower.contains("pressure")) {
                return "time_pressure";
            } else {
                return "general";
            }
        }
    }
    
    private EnhancedContextManager() {
        this.random = new Random(System.currentTimeMillis());
        initializeContextRotations();
    }
    
    public static synchronized EnhancedContextManager getInstance() {
        if (instance == null) {
            instance = new EnhancedContextManager();
        }
        return instance;
    }
    
    /**
     * Initialize context rotation patterns for each chess master
     */
    private void initializeContextRotations() {
        // Tal - Tactical and artistic variety
        List<String> talContexts = new ArrayList<>();
        talContexts.add("You're feeling the tactical tension in the position, sensing hidden combinations.");
        talContexts.add("Your artistic chess vision is searching for beautiful sacrificial possibilities.");
        talContexts.add("The position speaks to your intuitive understanding of piece coordination.");
        talContexts.add("You're in your element, where calculation meets pure chess artistry.");
        masterContextRotations.put("tal", talContexts);
        
        // Fischer - Analytical precision variety
        List<String> fischerContexts = new ArrayList<>();
        fischerContexts.add("Your precise analytical mind is calculating all variations systematically.");
        fischerContexts.add("You're applying your deep understanding of chess truth to find the objective best move.");
        fischerContexts.add("Your preparation and pattern recognition are guiding your evaluation.");
        fischerContexts.add("You're focused on finding moves that maintain maximum pressure and accuracy.");
        masterContextRotations.put("fischer", fischerContexts);
        
        // Carlsen - Adaptive modern variety
        List<String> carlsenContexts = new ArrayList<>();
        carlsenContexts.add("You're adapting your style to squeeze maximum practical chances from the position.");
        carlsenContexts.add("Your modern understanding is finding ways to create imbalances and complications.");
        carlsenContexts.add("You're using your exceptional endgame knowledge to evaluate long-term prospects.");
        carlsenContexts.add("Your intuitive feel for positions is guiding you toward the most challenging continuations.");
        masterContextRotations.put("carlsen", carlsenContexts);
        
        // Kasparov - Dynamic aggression variety
        List<String> kasparovContexts = new ArrayList<>();
        kasparovContexts.add("Your fighting spirit is looking for ways to seize the initiative and create threats.");
        kasparovContexts.add("You're channeling your aggressive energy into concrete tactical and strategic plans.");
        kasparovContexts.add("Your deep theoretical knowledge is combining with your instinct for complications.");
        kasparovContexts.add("You're in full competitive mode, seeking to dominate the position through active play.");
        masterContextRotations.put("kasparov", kasparovContexts);
        
        // Karpov - Positional patience variety
        List<String> karpovContexts = new ArrayList<>();
        karpovContexts.add("Your positional understanding is identifying subtle advantages to nurture and develop.");
        karpovContexts.add("You're employing your systematic approach to gradually improve your position.");
        karpovContexts.add("Your patience and technique are focused on converting small advantages into decisive ones.");
        karpovContexts.add("You're using your exceptional positional sense to restrict your opponent's counterplay.");
        masterContextRotations.put("karpov", karpovContexts);
        
        // Default contexts for other masters
        List<String> defaultContexts = new ArrayList<>();
        defaultContexts.add("You're drawing on your deep chess experience to evaluate this position.");
        defaultContexts.add("Your understanding of chess principles is guiding your assessment.");
        defaultContexts.add("You're applying your unique playing style to find the most characteristic moves.");
        defaultContexts.add("Your chess intuition is working to identify the key features of this position.");
        
        // Apply defaults to remaining masters
        String[] otherMasters = {"kramnik", "alekhine", "capablanca", "morphy", "lasker", "anand", "botvinnik"};
        for (String master : otherMasters) {
            masterContextRotations.put(master, new ArrayList<>(defaultContexts));
        }
    }
    
    /**
     * Add a conversation turn with enhanced metadata tracking
     */
    public void addConversationTurn(String speaker, String content, String contextType) {
        ConversationTurn turn = new ConversationTurn(speaker, content, contextType);
        conversationHistory.add(turn);
        
        // Track interaction patterns
        String lowerSpeaker = speaker.toLowerCase();
        lastInteractionTopics.put(lowerSpeaker, turn.topic);
        lastInteractionTimes.put(lowerSpeaker, turn.timestamp);
        
        // Track potential repetition
        String contentKey = lowerSpeaker + ":" + turn.topic + ":" + turn.mood;
        repetitionCounts.put(contentKey, repetitionCounts.getOrDefault(contentKey, 0) + 1);
        
        // Maintain conversation history size
        if (conversationHistory.size() > MAX_CONVERSATION_HISTORY) {
            conversationHistory.remove(0);
        }
        
        Log.d(TAG, "🎭 Added turn: " + speaker + " (" + turn.topic + "/" + turn.mood + ")");
    }
    
    /**
     * Get enhanced context for API calls with variety and emergent behavior
     */
    public List<Map<String, String>> getContextForApiCall(String speaker, String opponent) {
        List<Map<String, String>> context = new ArrayList<>();
        
        // 1. Add personality-based system message with rotation
        context.add(createRotatingPersonalityContext(speaker));
        
        // 2. Add opponent awareness context
        if (opponent != null && !opponent.isEmpty()) {
            context.add(createOpponentAwarenessContext(speaker, opponent));
        }
        
        // 3. Add conversation flow context
        context.addAll(createConversationFlowContext(speaker));
        
        // 4. Add emergent behavior context based on recent patterns
        context.add(createEmergentBehaviorContext(speaker));
        
        // 5. Add variety instructions to prevent repetition
        context.add(createVarietyInstructions(speaker));
        
        Log.d(TAG, "🎯 Generated " + context.size() + " context messages for " + speaker);
        return context;
    }
    
    /**
     * Create rotating personality context to add variety
     */
    private Map<String, String> createRotatingPersonalityContext(String speaker) {
        Map<String, String> context = new HashMap<>();
        context.put("role", "system");
        
        String lowerSpeaker = speaker.toLowerCase();
        List<String> rotations = masterContextRotations.get(lowerSpeaker);
        
        if (rotations != null && !rotations.isEmpty()) {
            // Get current rotation index
            int currentIndex = contextRotationIndexes.getOrDefault(lowerSpeaker, 0);
            String rotatingContext = rotations.get(currentIndex);
            
            // Update rotation index for next time
            contextRotationIndexes.put(lowerSpeaker, (currentIndex + 1) % rotations.size());
            
            context.put("content", getBasePersonality(speaker) + " " + rotatingContext);
        } else {
            context.put("content", getBasePersonality(speaker));
        }
        
        return context;
    }
    
    /**
     * Create opponent awareness context for richer interactions
     */
    private Map<String, String> createOpponentAwarenessContext(String speaker, String opponent) {
        Map<String, String> context = new HashMap<>();
        context.put("role", "system");
        
        String relationshipContext = getOpponentRelationshipDynamic(speaker, opponent);
        context.put("content", "In this game against " + opponent + ": " + relationshipContext);
        
        return context;
    }
    
    /**
     * Create conversation flow context based on recent history
     */
    private List<Map<String, String>> createConversationFlowContext(String speaker) {
        List<Map<String, String>> flowContext = new ArrayList<>();
        
        // Add recent relevant conversation turns as assistant/user pairs
        int added = 0;
        for (int i = conversationHistory.size() - 1; i >= 0 && added < 6; i--) {
            ConversationTurn turn = conversationHistory.get(i);
            
            // Add contextual conversation turns
            Map<String, String> turnContext = new HashMap<>();
            turnContext.put("role", turn.speaker.equals(speaker) ? "assistant" : "user");
            turnContext.put("content", turn.content);
            
            // Add turn to beginning of list to maintain chronological order
            flowContext.add(0, turnContext);
            added++;
        }
        
        return flowContext;
    }
    
    /**
     * Create emergent behavior context based on interaction patterns
     */
    private Map<String, String> createEmergentBehaviorContext(String speaker) {
        Map<String, String> context = new HashMap<>();
        context.put("role", "system");
        
        String lowerSpeaker = speaker.toLowerCase();
        String lastTopic = lastInteractionTopics.get(lowerSpeaker);
        Long lastTime = lastInteractionTimes.get(lowerSpeaker);
        
        StringBuilder emergentContext = new StringBuilder();
        
        // Add topic evolution context
        if (lastTopic != null) {
            if (lastTopic.equals("tactical")) {
                emergentContext.append("Building on your recent tactical focus, ");
            } else if (lastTopic.equals("positional")) {
                emergentContext.append("Continuing your positional analysis, ");
            } else if (lastTopic.equals("endgame")) {
                emergentContext.append("Following your endgame evaluation, ");
            } else {
                emergentContext.append("Developing your previous thoughts, ");
            }
        }
        
        // Add freshness context
        if (lastTime != null) {
            long timeSinceLastInteraction = System.currentTimeMillis() - lastTime;
            if (timeSinceLastInteraction > CONTEXT_FRESHNESS_THRESHOLD) {
                emergentContext.append("with fresh perspective on the position. ");
            } else {
                emergentContext.append("maintaining your current analytical thread. ");
            }
        }
        
        // Add variety based on repetition patterns
        String repetitionKey = lowerSpeaker + ":" + (lastTopic != null ? lastTopic : "general");
        Integer count = repetitionCounts.get(repetitionKey);
        if (count != null && count > 2) {
            emergentContext.append("Express your thoughts differently than before, ");
            emergentContext.append("exploring new angles of this position. ");
        }
        
        if (emergentContext.length() == 0) {
            emergentContext.append("Approach this position with your characteristic insight. ");
        }
        
        context.put("content", emergentContext.toString());
        return context;
    }
    
    /**
     * Create variety instructions to prevent repetitive responses
     */
    private Map<String, String> createVarietyInstructions(String speaker) {
        Map<String, String> context = new HashMap<>();
        context.put("role", "system");
        
        String[] varietyInstructions = {
            "Vary your expression and avoid repeating exact phrases from earlier in the conversation.",
            "Use different terminology and examples while maintaining your authentic personality.",
            "Approach this response from a fresh angle, even if the topic is similar to previous comments.",
            "Express your thoughts with new vocabulary and phrasing to keep the conversation engaging.",
            "Find a unique way to articulate your chess insights that differs from your recent responses."
        };
        
        // Select a random variety instruction
        String instruction = varietyInstructions[random.nextInt(varietyInstructions.length)];
        context.put("content", instruction);
        
        return context;
    }
    
    /**
     * Get base personality for each master
     */
    private String getBasePersonality(String speaker) {
        switch (speaker.toLowerCase()) {
            case "tal":
                return "You are Mikhail Tal, the magician from Riga. Your chess is about intuition, sacrifices, and tactical brilliance.";
            case "fischer":
                return "You are Bobby Fischer, demanding perfection and objective truth in every position.";
            case "carlsen":
                return "You are Magnus Carlsen, the modern chess genius who adapts to squeeze advantage from any position.";
            case "kasparov":
                return "You are Garry Kasparov, the aggressive competitor who fights for initiative in every game.";
            case "karpov":
                return "You are Anatoly Karpov, the positional virtuoso who accumulates small advantages with patience.";
            case "kramnik":
                return "You are Vladimir Kramnik, the deep thinker who seeks truth through methodical analysis.";
            case "alekhine":
                return "You are Alexander Alekhine, the combinatorial artist who sees hidden tactical depths.";
            case "capablanca":
                return "You are José Capablanca, the natural player who finds the most elegant and simple solutions.";
            case "morphy":
                return "You are Paul Morphy, the attacking genius who develops rapidly and strikes decisively.";
            case "lasker":
                return "You are Emanuel Lasker, the psychological player who understands both chess and human nature.";
            case "anand":
                return "You are Viswanathan Anand, the versatile champion who combines preparation with intuition.";
            case "botvinnik":
                return "You are Mikhail Botvinnik, the scientific player who approaches chess with systematic discipline.";
            default:
                return "You are a chess master with deep understanding and unique insights.";
        }
    }
    
    /**
     * Get opponent relationship dynamic for enhanced interaction
     */
    private String getOpponentRelationshipDynamic(String speaker, String opponent) {
        String key = speaker.toLowerCase() + "_" + opponent.toLowerCase();
        
        // Sample of key dynamics - can be expanded
        switch (key) {
            case "tal_fischer":
                return "You appreciate Fischer's precision but believe your tactical vision can break through his methodical style.";
            case "fischer_tal":
                return "You respect Tal's genius but trust that accurate calculation will neutralize his combinations.";
            case "kasparov_karpov":
                return "Your eternal rival - every move is psychological warfare built on years of competition.";
            case "karpov_kasparov":
                return "The dynamic opponent who pushes you to your limits - you counter his aggression with precision.";
            case "carlsen_kramnik":
                return "You respect his deep understanding while bringing your modern practical approach.";
            default:
                return "You understand " + opponent + "'s style and are ready to demonstrate your own chess philosophy.";
        }
    }
    
    /**
     * Get context statistics for debugging and optimization
     */
    public Map<String, Object> getContextStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("conversation_turns", conversationHistory.size());
        stats.put("tracked_masters", lastInteractionTopics.size());
        stats.put("repetition_patterns", repetitionCounts.size());
        
        // Topic distribution
        Map<String, Integer> topicCounts = new HashMap<>();
        for (ConversationTurn turn : conversationHistory) {
            topicCounts.put(turn.topic, topicCounts.getOrDefault(turn.topic, 0) + 1);
        }
        stats.put("topic_distribution", topicCounts);
        
        // Mood distribution
        Map<String, Integer> moodCounts = new HashMap<>();
        for (ConversationTurn turn : conversationHistory) {
            moodCounts.put(turn.mood, moodCounts.getOrDefault(turn.mood, 0) + 1);
        }
        stats.put("mood_distribution", moodCounts);
        
        return stats;
    }
    
    /**
     * Reset conversation state for new games
     */
    public void resetConversation() {
        conversationHistory.clear();
        lastInteractionTopics.clear();
        lastInteractionTimes.clear();
        repetitionCounts.clear();
        contextRotationIndexes.clear();
        Log.d(TAG, "🔄 Enhanced context manager reset for new conversation");
    }
    
    /**
     * Force context refresh for a specific master to prevent staleness
     */
    public void refreshMasterContext(String master) {
        String lowerMaster = master.toLowerCase();
        lastInteractionTopics.remove(lowerMaster);
        lastInteractionTimes.remove(lowerMaster);
        
        // Reset rotation index to get fresh personality context
        contextRotationIndexes.put(lowerMaster, 0);
        
        Log.d(TAG, "🔄 Refreshed context for " + master);
    }
    
    /**
     * Check if a master needs context refresh based on interaction patterns
     */
    public boolean needsContextRefresh(String master) {
        String lowerMaster = master.toLowerCase();
        Long lastTime = lastInteractionTimes.get(lowerMaster);
        
        if (lastTime == null) return false;
        
        long timeSinceLastInteraction = System.currentTimeMillis() - lastTime;
        return timeSinceLastInteraction > CONTEXT_FRESHNESS_THRESHOLD * 2; // 60 seconds
    }
    
    /**
     * Get conversation size for debugging
     */
    public int getConversationSize() {
        return conversationHistory.size();
    }
    
    /**
     * Get relationship memory count for debugging
     */
    public int getRelationshipMemoryCount() {
        // Count unique master pairs that have interacted
        return lastInteractionTopics.size();
    }
}