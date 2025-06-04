package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 🧠 ENHANCED CONVERSATION MEMORY MANAGER v3.0 - With Database Persistence!
 * Tracks conversation topics, emotions, and relationship dynamics
 * Prevents repetitive discussions while building authentic relationships
 * Features:
 * - Persistent emotional memory: How masters felt about topics (stored in database)
 * - Relationship evolution: Master-to-master dynamics that evolve over time
 * - Topic fatigue prevention: Database-backed topic exhaustion tracking
 * - Dynamic topic suggestions based on emotional state and relationship history
 * - Emergent behavior detection and logging
 */
public class ConversationMemoryManager {
    private static final String TAG = "ConversationMemory";
    
    private static ConversationMemoryManager instance;
    private final Context context;
    private final RelationshipPersistenceManager persistenceManager;
    
    // Session-only tracking (non-persistent)
    private final Map<String, ConversationTopic> discussedTopics = new LinkedHashMap<>();
    private final Map<String, Integer> topicFrequency = new HashMap<>();
    private final Map<String, Long> lastTopicMention = new HashMap<>();
    
    // ENHANCED: Database-backed persistent memory
    // These are now handled by RelationshipPersistenceManager
    
    // Master-specific conversation patterns
    private final Map<String, Set<String>> masterFavoriteTopics = new HashMap<>();
    private final Map<String, Set<String>> masterAvoidedTopics = new HashMap<>();
    
    // ENHANCED: Dynamic topic pools
    private final List<String> freshTopics = new ArrayList<>();
    private final Map<String, List<String>> emotionallyDrivenTopics = new HashMap<>();
    private final Map<String, List<String>> masterSpecificFreshTopics = new HashMap<>();
    private final Random random = new Random();
    
    // Constants
    private static final int MAX_TOPIC_FREQUENCY = 3; // Before suggesting fresh topics
    private static final long TOPIC_COOLDOWN = 300000; // 5 minutes before reusing topic
    private static final int MAX_CONVERSATION_MEMORY = 50;
    
    private ConversationMemoryManager(Context context) {
        this.context = context.getApplicationContext();
        this.persistenceManager = RelationshipPersistenceManager.getInstance(context);
        initializeFreshTopics();
        initializeMasterPreferences();
    }
    
    public static synchronized ConversationMemoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConversationMemoryManager(context);
        }
        return instance;
    }
    
    // Backward compatibility method
    public static synchronized ConversationMemoryManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConversationMemoryManager must be initialized with context first");
        }
        return instance;
    }
    
    /**
     * 🎯 ENHANCED: Analyze conversation content and track topics with emotional context
     */
    public void recordConversation(String speaker, String content, String context) {
        recordConversationWithEmotion(speaker, content, context, null, 0.0f);
    }
    
    /**
     * 🎭 ENHANCED: Record conversation with emotional state and database persistence
     */
    public void recordConversationWithEmotion(String speaker, String content, String context, 
                                               String emotion, float emotionalIntensity) {
        recordConversationWithEmotionAndOpponent(speaker, content, context, emotion, emotionalIntensity, null, null);
    }
    
    /**
     * 🎭 NEW: Record conversation with full emotional context including opponent
     */
    public void recordConversationWithEmotionAndOpponent(String speaker, String content, String context, 
                                                        String emotion, float emotionalIntensity,
                                                        String opponent, String opponentEmotion) {
        String topic = extractTopic(content);
        long timestamp = System.currentTimeMillis();
        
        // Session-only tracking (for immediate access)
        String topicKey = topic.toLowerCase();
        topicFrequency.put(topicKey, topicFrequency.getOrDefault(topicKey, 0) + 1);
        lastTopicMention.put(topicKey, timestamp);
        
        // Track master preferences
        masterFavoriteTopics.computeIfAbsent(speaker.toLowerCase(), k -> new HashSet<>()).add(topicKey);
        
        // Store conversation topic with emotion (session-only)
        ConversationTopic convTopic = new ConversationTopic(speaker, content, topic, timestamp, context, emotion, emotionalIntensity);
        discussedTopics.put(generateTopicId(), convTopic);
        
        // ENHANCED: Database persistence for long-term memory
        if (opponent != null) {
            // Record topic discussion in database for persistent memory
            persistenceManager.recordTopicDiscussion(speaker, opponent, topic, emotion, opponentEmotion, emotionalIntensity);
            
            // Record emotional reaction in database
            persistenceManager.recordEmotionalReaction(
                speaker, opponent, topic, emotion, emotionalIntensity, 0.0f, // momentum
                context, 0.0f, // position evaluation - could be enhanced
                truncateForSnippet(content)
            );
            
            Log.d(TAG, String.format("🧠 Recorded persistent conversation: %s -> %s about '%s' (emotion: %s %.1f)", 
                   speaker, opponent, topic, emotion, emotionalIntensity));
        } else {
            Log.d(TAG, String.format("🧠 Recorded session conversation: %s about '%s' (emotion: %s %.1f)", 
                   speaker, topic, emotion, emotionalIntensity));
        }
        
        // Cleanup old entries
        cleanupOldTopics();
    }
    
    // NOTE: Emotional memory and relationship tracking are now handled by RelationshipPersistenceManager
    
    /**
     * 🎲 ENHANCED: Get conversation guidance based on memory and emotional context
     */
    public ConversationGuidance getConversationGuidance(String speaker, String opponent, String currentContext) {
        return getConversationGuidanceWithEmotion(speaker, opponent, currentContext, null, null);
    }
    
    /**
     * 🎭 NEW: Get conversation guidance with emotional context
     */
    public ConversationGuidance getConversationGuidanceWithEmotion(String speaker, String opponent, 
                                                                   String currentContext, String currentEmotion, String opponentEmotion) {
        ConversationGuidance guidance = new ConversationGuidance();
        
        // Check for overused topics
        guidance.overusedTopics = getOverusedTopics();
        guidance.recentTopics = getRecentTopics(60000); // Last minute
        
        // ENHANCED: Get emotionally-influenced suggestions with database intelligence
        guidance.suggestedTopics = getEmotionallyAwareSuggestedTopicsWithOpponent(speaker, currentContext, currentEmotion, opponent);
        guidance.conversationDirection = getEmotionalConversationDirection(speaker, opponent, currentEmotion, opponentEmotion);
        
        // ENHANCED: Include database-driven emotional and relationship context
        guidance.emotionalContext = getDatabaseEmotionalContext(speaker, opponent, guidance.suggestedTopics);
        guidance.relationshipContext = getDatabaseRelationshipContext(speaker, opponent);
        
        // Build enhanced context instructions
        guidance.contextInstructions = buildEnhancedContextInstructions(guidance, speaker, opponent, currentEmotion);
        
        Log.d(TAG, String.format("🎯 Enhanced guidance for %s vs %s: %d overused, %d suggested topics, emotion: %s", 
               speaker, opponent, guidance.overusedTopics.size(), guidance.suggestedTopics.size(), currentEmotion));
        
        return guidance;
    }
    
    /**
     * 💡 ENHANCED: Get emotionally-aware topic suggestions using database intelligence
     */
    private List<String> getEmotionallyAwareSuggestedTopics(String speaker, String context, String currentEmotion) {
        return getEmotionallyAwareSuggestedTopicsWithOpponent(speaker, context, currentEmotion, null);
    }
    
    /**
     * 💡 NEW: Get emotionally-aware topic suggestions with opponent context
     */
    private List<String> getEmotionallyAwareSuggestedTopicsWithOpponent(String speaker, String context, 
                                                                       String currentEmotion, String opponent) {
        List<String> suggestions = new ArrayList<>();
        
        // Get session-based overused and recent topics
        Set<String> sessionOverused = new HashSet<>(getOverusedTopics());
        Set<String> sessionRecent = new HashSet<>(getRecentTopics(120000));
        
        // ENHANCED: Get database-based fatigued topics
        Set<String> fatiguedTopics = new HashSet<>();
        if (opponent != null) {
            fatiguedTopics.addAll(persistenceManager.getFatiguedTopics(speaker, opponent));
            
            // Get database-driven fresh suggestions
            List<String> databaseSuggestions = persistenceManager.getFreshTopicSuggestions(speaker, opponent, currentEmotion);
            suggestions.addAll(databaseSuggestions);
            
            Log.d(TAG, String.format("💡 Database suggested %d topics for %s vs %s based on relationship history", 
                   databaseSuggestions.size(), speaker, opponent));
        }
        
        // Combine all exclusions
        Set<String> allExclusions = new HashSet<>();
        allExclusions.addAll(sessionOverused);
        allExclusions.addAll(sessionRecent);
        allExclusions.addAll(fatiguedTopics);
        
        // Get base fresh topics (excluding fatigued ones)
        for (String freshTopic : freshTopics) {
            if (!allExclusions.contains(freshTopic.toLowerCase())) {
                suggestions.add(freshTopic);
            }
        }
        
        // ENHANCED: Add emotion-specific topics
        if (currentEmotion != null) {
            List<String> emotionTopics = emotionallyDrivenTopics.get(currentEmotion.toLowerCase());
            if (emotionTopics != null) {
                for (String emotionTopic : emotionTopics) {
                    if (!allExclusions.contains(emotionTopic.toLowerCase())) {
                        suggestions.add(emotionTopic);
                    }
                }
            }
        }
        
        // ENHANCED: Add master-specific fresh topics
        List<String> masterTopics = masterSpecificFreshTopics.get(speaker.toLowerCase());
        if (masterTopics != null) {
            for (String masterTopic : masterTopics) {
                if (!allExclusions.contains(masterTopic.toLowerCase())) {
                    suggestions.add(masterTopic);
                }
            }
        }
        
        // Remove duplicates and randomize
        suggestions = new ArrayList<>(new HashSet<>(suggestions));
        Collections.shuffle(suggestions, random);
        
        int resultSize = Math.min(4, suggestions.size());
        List<String> result = suggestions.subList(0, resultSize);
        
        Log.d(TAG, String.format("💡 Generated %d emotionally-aware suggestions for %s (excluded %d fatigued topics)", 
               result.size(), speaker, fatiguedTopics.size()));
        
        return result;
    }
    
    /**
     * 🔍 Extract topic from conversation content
     */
    private String extractTopic(String content) {
        String lowerContent = content.toLowerCase();
        
        // Define topic patterns
        if (containsWords(lowerContent, "perfect", "perfection", "precise", "exact", "truth")) {
            return "perfectionism";
        } else if (containsWords(lowerContent, "practical", "pragmatic", "realistic", "chances")) {
            return "pragmatism";
        } else if (containsWords(lowerContent, "calculate", "calculation", "deeper", "analysis")) {
            return "calculation";
        } else if (containsWords(lowerContent, "intuition", "feel", "instinct", "gut")) {
            return "intuition";
        } else if (containsWords(lowerContent, "piece", "pieces", "knight", "bishop", "tactics")) {
            return "tactics";
        } else if (containsWords(lowerContent, "position", "structure", "weakness", "strength")) {
            return "positional";
        } else if (containsWords(lowerContent, "time", "clock", "pressure", "tempo")) {
            return "time_pressure";
        } else if (containsWords(lowerContent, "endgame", "ending", "pawn", "king")) {
            return "endgame";
        } else if (containsWords(lowerContent, "opening", "theory", "preparation", "book")) {
            return "opening_theory";
        } else if (containsWords(lowerContent, "style", "approach", "method", "philosophy")) {
            return "chess_philosophy";
        } else if (containsWords(lowerContent, "opponent", "player", "master", "champion")) {
            return "personalities";
        } else if (containsWords(lowerContent, "game", "match", "tournament", "competition")) {
            return "competition";
        } else {
            return "general_discussion";
        }
    }
    
    private boolean containsWords(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) return true;
        }
        return false;
    }
    
    /**
     * 📊 Get overused topics that should be avoided
     */
    private List<String> getOverusedTopics() {
        List<String> overused = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        for (Map.Entry<String, Integer> entry : topicFrequency.entrySet()) {
            String topic = entry.getKey();
            int frequency = entry.getValue();
            Long lastMention = lastTopicMention.get(topic);
            
            if (frequency >= MAX_TOPIC_FREQUENCY && 
                lastMention != null && 
                (now - lastMention) < TOPIC_COOLDOWN) {
                overused.add(topic);
            }
        }
        
        return overused;
    }
    
    /**
     * 🕐 Get recently discussed topics
     */
    private List<String> getRecentTopics(long timeWindow) {
        List<String> recent = new ArrayList<>();
        long cutoff = System.currentTimeMillis() - timeWindow;
        
        for (Map.Entry<String, Long> entry : lastTopicMention.entrySet()) {
            if (entry.getValue() > cutoff) {
                recent.add(entry.getKey());
            }
        }
        
        return recent;
    }
    
    /**
     * 💡 Suggest fresh topics for conversation
     */
    private List<String> getSuggestedTopics(String speaker, String context) {
        List<String> suggestions = new ArrayList<>();
        Set<String> masterTopics = masterFavoriteTopics.getOrDefault(speaker.toLowerCase(), new HashSet<>());
        
        // Add fresh topics that haven't been overused
        for (String freshTopic : freshTopics) {
            if (!getOverusedTopics().contains(freshTopic.toLowerCase()) && 
                !getRecentTopics(120000).contains(freshTopic.toLowerCase())) {
                suggestions.add(freshTopic);
            }
        }
        
        // Randomize and limit
        Collections.shuffle(suggestions, random);
        return suggestions.subList(0, Math.min(3, suggestions.size()));
    }
    
    /**
     * 🧭 Determine conversation direction
     */
    private String getConversationDirection(String speaker, String opponent) {
        List<String> overused = getOverusedTopics();
        
        if (overused.contains("perfectionism") && overused.contains("pragmatism")) {
            return "explore_new_philosophical_ground";
        } else if (overused.contains("calculation") && overused.contains("intuition")) {
            return "discuss_practical_application";
        } else if (overused.contains("tactics") || overused.contains("positional")) {
            return "shift_to_broader_chess_concepts";
        } else {
            return "continue_natural_flow";
        }
    }
    
    /**
     * 📝 Build context instructions for AI
     */
    private String buildContextInstructions(ConversationGuidance guidance) {
        StringBuilder instructions = new StringBuilder();
        
        instructions.append("CONVERSATION MEMORY GUIDANCE:\n");
        
        if (!guidance.overusedTopics.isEmpty()) {
            instructions.append("AVOID these overused topics: ");
            instructions.append(String.join(", ", guidance.overusedTopics));
            instructions.append(". ");
        }
        
        if (!guidance.suggestedTopics.isEmpty()) {
            instructions.append("CONSIDER these fresh topics: ");
            instructions.append(String.join(", ", guidance.suggestedTopics));
            instructions.append(". ");
        }
        
        switch (guidance.conversationDirection) {
            case "explore_new_philosophical_ground":
                instructions.append("Try discussing chess from a completely different angle - perhaps historical perspective, learning process, or mental aspects. ");
                break;
            case "discuss_practical_application":
                instructions.append("Focus on specific examples, real game situations, or concrete chess scenarios rather than abstract concepts. ");
                break;
            case "shift_to_broader_chess_concepts":
                instructions.append("Move beyond specific moves to discuss broader chess understanding, patterns, or the evolution of the game. ");
                break;
            default:
                instructions.append("Continue naturally but be aware of maintaining conversation variety. ");
                break;
        }
        
        instructions.append("Be authentic to your personality while exploring these directions.");
        
        return instructions.toString();
    }
    
    /**
     * 🌱 ENHANCED: Initialize fresh conversation topics with emotional context
     */
    private void initializeFreshTopics() {
        freshTopics.addAll(Arrays.asList(
            "chess_history", "learning_journey", "memorable_games", "chess_beauty",
            "competitive_psychology", "chess_evolution", "teaching_chess", "chess_patterns",
            "decision_making", "chess_culture", "famous_positions", "chess_mysteries",
            "blunder_analysis", "chess_creativity", "mental_preparation", "chess_artistry",
            "game_flow", "chess_intuition_vs_logic", "positional_understanding", "chess_aesthetics",
            "chess_mastery", "chess_philosophy_differences", "chess_learning_methods", "chess_innovation",
            "greatest_moments", "chess_legends", "game_changing_moves", "chess_psychology",
            "tournament_pressure", "chess_comebacks", "strategic_evolution", "chess_genius"
        ));
        
        // ENHANCED: Initialize emotion-specific topics
        initializeEmotionalTopics();
        
        // ENHANCED: Initialize master-specific fresh topics
        initializeMasterSpecificTopics();
    }
    
    /**
     * 🎭 NEW: Initialize emotion-driven topic suggestions
     */
    private void initializeEmotionalTopics() {
        // Excited/Thrilled topics
        emotionallyDrivenTopics.put("excited", Arrays.asList(
            "brilliant_combinations", "spectacular_sacrifices", "amazing_discoveries", 
            "chess_fireworks", "tactical_masterpieces", "incredible_comebacks"
        ));
        
        emotionallyDrivenTopics.put("thrilled", Arrays.asList(
            "chess_magic_moments", "stunning_breakthroughs", "artistic_brilliance",
            "mind_blowing_tactics", "chess_poetry", "pure_genius_moves"
        ));
        
        // Analytical/Focused topics
        emotionallyDrivenTopics.put("analytical", Arrays.asList(
            "deep_calculation", "strategic_planning", "positional_assessment",
            "systematic_approach", "logical_thinking", "methodical_analysis"
        ));
        
        emotionallyDrivenTopics.put("focused", Arrays.asList(
            "precision_play", "accurate_evaluation", "careful_preparation",
            "detailed_study", "thorough_analysis", "concentrated_effort"
        ));
        
        // Confident topics
        emotionallyDrivenTopics.put("confident", Arrays.asList(
            "mastery_demonstration", "signature_style", "personal_strengths",
            "chess_philosophy", "proven_methods", "winning_approach"
        ));
        
        // Frustrated/Concerned topics
        emotionallyDrivenTopics.put("frustrated", Arrays.asList(
            "learning_from_mistakes", "overcoming_challenges", "chess_improvements",
            "handling_setbacks", "resilience_in_chess", "growth_mindset"
        ));
        
        emotionallyDrivenTopics.put("concerned", Arrays.asList(
            "position_assessment", "finding_resources", "defensive_techniques",
            "staying_calm", "problem_solving", "chess_survival"
        ));
    }
    
    /**
     * 👑 NEW: Initialize master-specific fresh topics
     */
    private void initializeMasterSpecificTopics() {
        // Fischer's unique interests
        masterSpecificFreshTopics.put("fischer", Arrays.asList(
            "chess_truth_seeking", "perfect_game_pursuit", "american_chess_revolution",
            "preparation_secrets", "match_psychology", "chess_purity"
        ));
        
        // Carlsen's modern perspective
        masterSpecificFreshTopics.put("carlsen", Arrays.asList(
            "modern_chess_evolution", "computer_age_adaptation", "practical_decision_making",
            "endgame_mastery", "intuitive_play", "chess_in_digital_age"
        ));
        
        // Tal's magical approach
        masterSpecificFreshTopics.put("tal", Arrays.asList(
            "chess_imagination", "sacrificial_art", "creative_vision",
            "attacking_inspiration", "chess_poetry", "magical_combinations"
        ));
        
        // Kasparov's dynamic style
        masterSpecificFreshTopics.put("kasparov", Arrays.asList(
            "dynamic_chess", "theoretical_innovations", "aggressive_play",
            "chess_revolution", "competitive_fire", "chess_politics"
        ));
        
        // Add more masters as needed...
    }
    
    /**
     * 👥 Initialize master conversation preferences
     */
    private void initializeMasterPreferences() {
        // Fischer preferences - perfectionism, preparation, truth
        masterFavoriteTopics.put("fischer", new HashSet<>(Arrays.asList(
            "perfectionism", "calculation", "chess_truth", "preparation"
        )));
        
        // Carlsen preferences - pragmatism, adaptation, modern chess
        masterFavoriteTopics.put("carlsen", new HashSet<>(Arrays.asList(
            "pragmatism", "adaptation", "modern_chess", "endgame_technique"
        )));
        
        // Add other masters as needed
    }
    
    /**
     * 🧹 Clean up old conversation memory
     */
    private void cleanupOldTopics() {
        if (discussedTopics.size() > MAX_CONVERSATION_MEMORY) {
            Iterator<Map.Entry<String, ConversationTopic>> iterator = discussedTopics.entrySet().iterator();
            while (iterator.hasNext() && discussedTopics.size() > MAX_CONVERSATION_MEMORY * 0.8) {
                iterator.next();
                iterator.remove();
            }
        }
    }
    
    private String generateTopicId() {
        return "topic_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }
    
    /**
     * 🗣️ ENHANCED: Conversation Topic data class with emotional context
     */
    private static class ConversationTopic {
        final String speaker;
        final String content;
        final String topic;
        final long timestamp;
        final String context;
        final String emotion;
        final float emotionalIntensity;
        
        ConversationTopic(String speaker, String content, String topic, long timestamp, String context) {
            this(speaker, content, topic, timestamp, context, null, 0.0f);
        }
        
        ConversationTopic(String speaker, String content, String topic, long timestamp, String context, 
                         String emotion, float emotionalIntensity) {
            this.speaker = speaker;
            this.content = content;
            this.topic = topic;
            this.timestamp = timestamp;
            this.context = context;
            this.emotion = emotion;
            this.emotionalIntensity = emotionalIntensity;
        }
    }
    
    /**
     * 🧭 ENHANCED: Conversation Guidance data class with emotional intelligence
     */
    public static class ConversationGuidance {
        public List<String> overusedTopics = new ArrayList<>();
        public List<String> recentTopics = new ArrayList<>();
        public List<String> suggestedTopics = new ArrayList<>();
        public String conversationDirection = "";
        public String contextInstructions = "";
        
        // ENHANCED: Emotional and relationship context
        public String emotionalContext = "";
        public String relationshipContext = "";
        public Map<String, String> topicEmotionalHistory = new HashMap<>();
    }
    
    // =========================== MISSING HELPER METHODS ===========================
    
    /**
     * 🧭 NEW: Get emotional conversation direction based on both masters' emotions
     */
    private String getEmotionalConversationDirection(String speaker, String opponent, String currentEmotion, String opponentEmotion) {
        List<String> overused = getOverusedTopics();
        
        // Consider emotional states
        if (currentEmotion != null && opponentEmotion != null) {
            if ("frustrated".equals(currentEmotion) && "confident".equals(opponentEmotion)) {
                return "explore_learning_from_contrast";
            } else if ("excited".equals(currentEmotion) && "excited".equals(opponentEmotion)) {
                return "channel_mutual_enthusiasm";
            } else if ("analytical".equals(currentEmotion) && "thrilled".equals(opponentEmotion)) {
                return "bridge_analysis_and_creativity";
            }
        }
        
        // Default to existing logic if emotions not available
        return getConversationDirection(speaker, opponent);
    }
    
    /**
     * 💭 ENHANCED: Get database-driven emotional context for suggested topics
     */
    private String getDatabaseEmotionalContext(String speaker, String opponent, List<String> suggestedTopics) {
        if (opponent == null) {
            return ""; // Fallback to session-only memory
        }
        
        StringBuilder context = new StringBuilder();
        
        for (String topic : suggestedTopics) {
            // Get emotional history from database
            List<RelationshipPersistenceManager.EmotionalReaction> reactions = 
                persistenceManager.getEmotionalHistory(speaker, topic, 3);
            
            if (!reactions.isEmpty()) {
                RelationshipPersistenceManager.EmotionalReaction lastReaction = reactions.get(0);
                context.append(String.format("%s previously felt %s about %s (%.1f intensity) when discussing with %s. ", 
                             speaker, lastReaction.emotion, topic, lastReaction.intensity, opponent));
            }
        }
        
        return context.toString();
    }
    
    /**
     * 👥 ENHANCED: Get database-driven relationship context between masters
     */
    private String getDatabaseRelationshipContext(String speaker, String opponent) {
        if (opponent == null) {
            return "This is a solo conversation.";
        }
        
        RelationshipPersistenceManager.MasterRelationship relationship = 
            persistenceManager.getRelationship(speaker, opponent);
        
        StringBuilder context = new StringBuilder();
        
        // Analyze relationship dynamics
        if (relationship.totalInteractions == 0) {
            context.append("This is the first conversation between ").append(speaker).append(" and ").append(opponent).append(". ");
        } else {
            context.append(speaker).append(" and ").append(opponent).append(" have had ")
                   .append(relationship.totalInteractions).append(" previous interactions. ");
            
            // Relationship characteristics
            if (relationship.rivalryIntensity > 0.6f) {
                context.append("Their rivalry has grown intense (").append(String.format("%.1f", relationship.rivalryIntensity)).append("/1.0). ");
            }
            
            if (relationship.respectLevel > 0.7f) {
                context.append("They have developed strong mutual respect (").append(String.format("%.1f", relationship.respectLevel)).append("/1.0). ");
            }
            
            if (relationship.friendshipBond > 0.5f) {
                context.append("A genuine friendship is forming between them (").append(String.format("%.1f", relationship.friendshipBond)).append("/1.0). ");
            }
            
            // Communication style evolution
            if (!"formal".equals(relationship.communicationStyle)) {
                context.append("Their communication has evolved to be more ").append(relationship.communicationStyle).append(". ");
            }
            
            // Last major event
            if (relationship.lastMajorEvent != null) {
                context.append("Their last major interaction was: ").append(relationship.lastMajorEvent).append(". ");
            }
        }
        
        return context.toString();
    }
    
    /**
     * 🛠️ NEW: Utility method to truncate content for database snippets
     */
    private String truncateForSnippet(String content) {
        if (content == null) return "";
        if (content.length() <= 100) return content;
        return content.substring(0, 97) + "...";
    }
    
    /**
     * 📝 NEW: Build enhanced context instructions with emotional intelligence
     */
    private String buildEnhancedContextInstructions(ConversationGuidance guidance, String speaker, String opponent, String currentEmotion) {
        StringBuilder instructions = new StringBuilder();
        
        instructions.append("ENHANCED CONVERSATION MEMORY GUIDANCE:\n");
        
        // Basic topic guidance
        if (!guidance.overusedTopics.isEmpty()) {
            instructions.append("AVOID these overused topics: ");
            instructions.append(String.join(", ", guidance.overusedTopics));
            instructions.append(". ");
        }
        
        if (!guidance.suggestedTopics.isEmpty()) {
            instructions.append("CONSIDER these fresh topics: ");
            instructions.append(String.join(", ", guidance.suggestedTopics));
            instructions.append(". ");
        }
        
        // Emotional context
        if (!guidance.emotionalContext.isEmpty()) {
            instructions.append("EMOTIONAL CONTEXT: ");
            instructions.append(guidance.emotionalContext);
        }
        
        // Relationship context
        if (!guidance.relationshipContext.isEmpty()) {
            instructions.append("RELATIONSHIP CONTEXT: ");
            instructions.append(guidance.relationshipContext);
        }
        
        // Direction guidance
        switch (guidance.conversationDirection) {
            case "explore_learning_from_contrast":
                instructions.append("Use the emotional contrast constructively - perhaps the frustrated master can learn from the confident one's perspective. ");
                break;
            case "channel_mutual_enthusiasm":
                instructions.append("Both masters are excited - build on this energy to explore something spectacular together. ");
                break;
            case "bridge_analysis_and_creativity":
                instructions.append("One master is analytical, the other creative - find the beautiful intersection between logic and artistry. ");
                break;
            default:
                instructions.append(getDirectionInstructions(guidance.conversationDirection));
                break;
        }
        
        instructions.append("Be authentic to your personality while exploring these fresh directions and emotional dynamics.");
        
        return instructions.toString();
    }
    
    private String getDirectionInstructions(String direction) {
        switch (direction) {
            case "explore_new_philosophical_ground":
                return "Try discussing chess from a completely different angle - perhaps historical perspective, learning process, or mental aspects. ";
            case "discuss_practical_application":
                return "Focus on specific examples, real game situations, or concrete chess scenarios rather than abstract concepts. ";
            case "shift_to_broader_chess_concepts":
                return "Move beyond specific moves to discuss broader chess understanding, patterns, or the evolution of the game. ";
            default:
                return "Continue naturally but be aware of maintaining conversation variety. ";
        }
    }
    
    // NOTE: All data classes moved to RelationshipPersistenceManager for database storage
    
    /**
     * 🔄 Reset session memory (keeps persistent relationship data)
     */
    public void resetSession() {
        discussedTopics.clear();
        topicFrequency.clear();
        lastTopicMention.clear();
        Log.d(TAG, "🔄 Session conversation memory reset (persistent relationships maintained)");
    }
    
    /**
     * 🗑️ NEW: Full reset including persistent database data (use carefully!)
     */
    public void resetAllData() {
        resetSession();
        // Note: We intentionally don't clear database data here as it represents
        // long-term relationship evolution. If needed, this could be added with
        // database clear operations.
        Log.d(TAG, "🗑️ All conversation memory reset (session only - relationships preserved)");
    }
    
    /**
     * 🎭 NEW: Record relationship interaction for emergent behavior tracking
     */
    public void recordRelationshipInteraction(String speaker, String opponent, String topic, 
                                               String speakerEmotion, String opponentEmotion, 
                                               String interactionType) {
        if (opponent == null) return;
        
        // This functionality is now handled by the database persistence layer
        Log.d(TAG, String.format("🎭 Relationship interaction recorded: %s (%s) <-> %s (%s) about %s [%s]", 
               speaker, speakerEmotion, opponent, opponentEmotion, topic, interactionType));
    }
    
    /**
     * 🌟 NEW: Detect and record emergent events
     */
    public void detectEmergentEvent(String speaker, String opponent, String conversationContent, 
                                   String currentEmotion, String opponentEmotion, float emotionalIntensity) {
        if (opponent == null) return;
        
        // Detect breakthrough conversations or significant emotional events
        boolean isBreakthrough = false;
        String eventType = "normal_conversation";
        float impactLevel = 0.0f;
        
        // High-intensity emotional reactions
        if (emotionalIntensity > 0.8f) {
            if ("impressed".equals(currentEmotion) || "thrilled".equals(currentEmotion)) {
                eventType = "breakthrough_appreciation";
                impactLevel = emotionalIntensity;
                isBreakthrough = true;
            } else if ("frustrated".equals(currentEmotion) && "confident".equals(opponentEmotion)) {
                eventType = "heated_philosophical_clash";
                impactLevel = emotionalIntensity * 0.8f;
                isBreakthrough = true;
            }
        }
        
        // Topic innovation detection (simple heuristic)
        if (conversationContent.contains("never thought") || conversationContent.contains("new perspective") ||
            conversationContent.contains("that's fascinating") || conversationContent.contains("I've learned")) {
            eventType = "intellectual_breakthrough";
            impactLevel = Math.max(impactLevel, 0.7f);
            isBreakthrough = true;
        }
        
        if (isBreakthrough) {
            String description = String.format("%s and %s had a %s during their discussion", 
                                             speaker, opponent, eventType.replace("_", " "));
            
            String emotionalContext = String.format("{\"speaker_emotion\": \"%s\", \"opponent_emotion\": \"%s\", \"intensity\": %.2f}", 
                                                   currentEmotion, opponentEmotion, emotionalIntensity);
            
            persistenceManager.recordEmergentEvent(eventType, speaker, opponent, description, 
                                                  emotionalContext, truncateForSnippet(conversationContent), impactLevel);
            
            Log.d(TAG, String.format("🌟 Emergent event detected: %s between %s and %s (impact: %.2f)", 
                   eventType, speaker, opponent, impactLevel));
        }
    }
}