package com.example.chesspedagogue;

import android.util.Log;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 🧠 ENHANCED CONVERSATION MEMORY MANAGER v2.0
 * Tracks conversation topics, emotions, and relationship dynamics
 * Prevents repetitive discussions while building authentic relationships
 * Features:
 * - Emotional memory: How masters felt about topics
 * - Relationship tracking: Master-to-master interaction history
 * - Topic evolution: Building on previous conversations
 * - Dynamic topic suggestions based on emotional state
 */
public class ConversationMemoryManager {
    private static final String TAG = "ConversationMemory";
    
    private static ConversationMemoryManager instance;
    
    // Topic tracking
    private final Map<String, ConversationTopic> discussedTopics = new LinkedHashMap<>();
    private final Map<String, Integer> topicFrequency = new HashMap<>();
    private final Map<String, Long> lastTopicMention = new HashMap<>();
    
    // ENHANCED: Emotional memory tracking
    private final Map<String, EmotionalTopicMemory> emotionalTopicHistory = new HashMap<>();
    private final Map<String, MasterRelationshipMemory> relationshipMemories = new HashMap<>();
    
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
    
    private ConversationMemoryManager() {
        initializeFreshTopics();
        initializeMasterPreferences();
    }
    
    public static synchronized ConversationMemoryManager getInstance() {
        if (instance == null) {
            instance = new ConversationMemoryManager();
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
     * 🎭 NEW: Record conversation with emotional state
     */
    public void recordConversationWithEmotion(String speaker, String content, String context, 
                                               String emotion, float emotionalIntensity) {
        String topic = extractTopic(content);
        long timestamp = System.currentTimeMillis();
        
        // Record topic usage
        String topicKey = topic.toLowerCase();
        topicFrequency.put(topicKey, topicFrequency.getOrDefault(topicKey, 0) + 1);
        lastTopicMention.put(topicKey, timestamp);
        
        // Track master preferences
        masterFavoriteTopics.computeIfAbsent(speaker.toLowerCase(), k -> new HashSet<>()).add(topicKey);
        
        // ENHANCED: Record emotional context with topic
        recordEmotionalTopicMemory(speaker, topic, emotion, emotionalIntensity, content);
        
        // Store conversation topic with emotion
        ConversationTopic convTopic = new ConversationTopic(speaker, content, topic, timestamp, context, emotion, emotionalIntensity);
        discussedTopics.put(generateTopicId(), convTopic);
        
        // Cleanup old entries
        cleanupOldTopics();
        
        Log.d(TAG, String.format("🧠 Recorded topic '%s' for %s (frequency: %d, emotion: %s %.1f)", 
               topic, speaker, topicFrequency.get(topicKey), emotion, emotionalIntensity));
    }
    
    /**
     * 💭 NEW: Record emotional memory about topics
     */
    private void recordEmotionalTopicMemory(String speaker, String topic, String emotion, float intensity, String content) {
        String key = speaker.toLowerCase() + ":" + topic.toLowerCase();
        
        EmotionalTopicMemory memory = emotionalTopicHistory.get(key);
        if (memory == null) {
            memory = new EmotionalTopicMemory(speaker, topic);
            emotionalTopicHistory.put(key, memory);
        }
        
        memory.addEmotionalInstance(emotion, intensity, content, System.currentTimeMillis());
        
        Log.d(TAG, String.format("💭 %s emotional memory about '%s': %s (%.1f)", 
               speaker, topic, emotion, intensity));
    }
    
    /**
     * 👥 NEW: Record relationship interaction between masters
     */
    public void recordRelationshipInteraction(String speaker, String opponent, String topic, 
                                               String speakerEmotion, String opponentEmotion, 
                                               String interactionType) {
        String relationshipKey = generateRelationshipKey(speaker, opponent);
        
        MasterRelationshipMemory relationship = relationshipMemories.get(relationshipKey);
        if (relationship == null) {
            relationship = new MasterRelationshipMemory(speaker, opponent);
            relationshipMemories.put(relationshipKey, relationship);
        }
        
        relationship.addInteraction(topic, speakerEmotion, opponentEmotion, interactionType, System.currentTimeMillis());
        
        Log.d(TAG, String.format("👥 Relationship interaction: %s (%s) <-> %s (%s) about %s", 
               speaker, speakerEmotion, opponent, opponentEmotion, topic));
    }
    
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
        
        // ENHANCED: Get emotionally-influenced suggestions
        guidance.suggestedTopics = getEmotionallyAwareSuggestedTopics(speaker, currentContext, currentEmotion);
        guidance.conversationDirection = getEmotionalConversationDirection(speaker, opponent, currentEmotion, opponentEmotion);
        
        // ENHANCED: Include emotional and relationship context
        guidance.emotionalContext = getEmotionalTopicContext(speaker, guidance.suggestedTopics);
        guidance.relationshipContext = getRelationshipContext(speaker, opponent);
        
        // Build enhanced context instructions
        guidance.contextInstructions = buildEnhancedContextInstructions(guidance, speaker, opponent, currentEmotion);
        
        Log.d(TAG, String.format("🎯 Enhanced guidance for %s vs %s: %d overused, %d suggested topics, emotion: %s", 
               speaker, opponent, guidance.overusedTopics.size(), guidance.suggestedTopics.size(), currentEmotion));
        
        return guidance;
    }
    
    /**
     * 💡 ENHANCED: Get emotionally-aware topic suggestions
     */
    private List<String> getEmotionallyAwareSuggestedTopics(String speaker, String context, String currentEmotion) {
        List<String> suggestions = new ArrayList<>();
        Set<String> overused = new HashSet<>(getOverusedTopics());
        Set<String> recent = new HashSet<>(getRecentTopics(120000));
        
        // Get base fresh topics
        for (String freshTopic : freshTopics) {
            if (!overused.contains(freshTopic.toLowerCase()) && !recent.contains(freshTopic.toLowerCase())) {
                suggestions.add(freshTopic);
            }
        }
        
        // ENHANCED: Add emotion-specific topics
        if (currentEmotion != null) {
            List<String> emotionTopics = emotionallyDrivenTopics.get(currentEmotion.toLowerCase());
            if (emotionTopics != null) {
                for (String emotionTopic : emotionTopics) {
                    if (!overused.contains(emotionTopic.toLowerCase()) && !recent.contains(emotionTopic.toLowerCase())) {
                        suggestions.add(emotionTopic);
                    }
                }
            }
        }
        
        // ENHANCED: Add master-specific fresh topics
        List<String> masterTopics = masterSpecificFreshTopics.get(speaker.toLowerCase());
        if (masterTopics != null) {
            for (String masterTopic : masterTopics) {
                if (!overused.contains(masterTopic.toLowerCase()) && !recent.contains(masterTopic.toLowerCase())) {
                    suggestions.add(masterTopic);
                }
            }
        }
        
        // Randomize and limit
        Collections.shuffle(suggestions, random);
        return suggestions.subList(0, Math.min(4, suggestions.size()));
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
     * 💭 NEW: Get emotional context for suggested topics
     */
    private String getEmotionalTopicContext(String speaker, List<String> suggestedTopics) {
        StringBuilder context = new StringBuilder();
        
        for (String topic : suggestedTopics) {
            String key = speaker.toLowerCase() + ":" + topic.toLowerCase();
            EmotionalTopicMemory memory = emotionalTopicHistory.get(key);
            
            if (memory != null && !memory.emotionalInstances.isEmpty()) {
                EmotionalInstance lastInstance = memory.getLastEmotionalInstance();
                context.append(String.format("%s has felt %s about %s (%.1f intensity). ", 
                             speaker, lastInstance.emotion, topic, lastInstance.intensity));
            }
        }
        
        return context.toString();
    }
    
    /**
     * 👥 NEW: Get relationship context between masters
     */
    private String getRelationshipContext(String speaker, String opponent) {
        String relationshipKey = generateRelationshipKey(speaker, opponent);
        MasterRelationshipMemory relationship = relationshipMemories.get(relationshipKey);
        
        if (relationship == null || relationship.interactions.isEmpty()) {
            return "This is a fresh conversation between " + speaker + " and " + opponent + ".";
        }
        
        StringBuilder context = new StringBuilder();
        
        // Analyze recent interactions
        int recentInteractions = Math.min(3, relationship.interactions.size());
        List<RelationshipInteraction> recent = relationship.interactions.subList(
            relationship.interactions.size() - recentInteractions, 
            relationship.interactions.size()
        );
        
        Map<String, Integer> interactionTypes = new HashMap<>();
        for (RelationshipInteraction interaction : recent) {
            interactionTypes.put(interaction.interactionType, 
                               interactionTypes.getOrDefault(interaction.interactionType, 0) + 1);
        }
        
        if (interactionTypes.getOrDefault("disagreement", 0) >= 2) {
            context.append(speaker).append(" and ").append(opponent)
                   .append(" have had some philosophical disagreements recently. ");
        } else if (interactionTypes.getOrDefault("agreement", 0) >= 2) {
            context.append(speaker).append(" and ").append(opponent)
                   .append(" have been finding common ground in their discussions. ");
        }
        
        return context.toString();
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
    
    private String generateRelationshipKey(String master1, String master2) {
        // Ensure consistent key regardless of order
        if (master1.compareTo(master2) < 0) {
            return master1.toLowerCase() + "_" + master2.toLowerCase();
        } else {
            return master2.toLowerCase() + "_" + master1.toLowerCase();
        }
    }
    
    // =========================== NEW DATA CLASSES ===========================
    
    /**
     * 💭 Emotional Topic Memory - tracks how a master feels about specific topics
     */
    private static class EmotionalTopicMemory {
        final String master;
        final String topic;
        final List<EmotionalInstance> emotionalInstances = new ArrayList<>();
        
        EmotionalTopicMemory(String master, String topic) {
            this.master = master;
            this.topic = topic;
        }
        
        void addEmotionalInstance(String emotion, float intensity, String content, long timestamp) {
            emotionalInstances.add(new EmotionalInstance(emotion, intensity, content, timestamp));
            
            // Keep only recent instances
            if (emotionalInstances.size() > 10) {
                emotionalInstances.remove(0);
            }
        }
        
        EmotionalInstance getLastEmotionalInstance() {
            return emotionalInstances.isEmpty() ? null : emotionalInstances.get(emotionalInstances.size() - 1);
        }
        
        float getAverageIntensity() {
            if (emotionalInstances.isEmpty()) return 0.0f;
            float sum = 0.0f;
            for (EmotionalInstance instance : emotionalInstances) {
                sum += instance.intensity;
            }
            return sum / emotionalInstances.size();
        }
    }
    
    /**
     * 🎭 Emotional Instance - a specific emotional reaction to a topic
     */
    private static class EmotionalInstance {
        final String emotion;
        final float intensity;
        final String content;
        final long timestamp;
        
        EmotionalInstance(String emotion, float intensity, String content, long timestamp) {
            this.emotion = emotion;
            this.intensity = intensity;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * 👥 Master Relationship Memory - tracks interactions between two masters
     */
    private static class MasterRelationshipMemory {
        final String master1;
        final String master2;
        final List<RelationshipInteraction> interactions = new ArrayList<>();
        
        MasterRelationshipMemory(String master1, String master2) {
            this.master1 = master1;
            this.master2 = master2;
        }
        
        void addInteraction(String topic, String master1Emotion, String master2Emotion, 
                           String interactionType, long timestamp) {
            interactions.add(new RelationshipInteraction(topic, master1Emotion, master2Emotion, 
                                                        interactionType, timestamp));
            
            // Keep only recent interactions
            if (interactions.size() > 20) {
                interactions.remove(0);
            }
        }
    }
    
    /**
     * 🤝 Relationship Interaction - a specific interaction between two masters
     */
    private static class RelationshipInteraction {
        final String topic;
        final String master1Emotion;
        final String master2Emotion;
        final String interactionType; // "agreement", "disagreement", "mutual_excitement", etc.
        final long timestamp;
        
        RelationshipInteraction(String topic, String master1Emotion, String master2Emotion, 
                              String interactionType, long timestamp) {
            this.topic = topic;
            this.master1Emotion = master1Emotion;
            this.master2Emotion = master2Emotion;
            this.interactionType = interactionType;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * 🔄 Reset memory for new session
     */
    public void resetSession() {
        discussedTopics.clear();
        topicFrequency.clear();
        lastTopicMention.clear();
        emotionalTopicHistory.clear();
        relationshipMemories.clear();
        Log.d(TAG, "🔄 Enhanced conversation memory reset for new session");
    }
}