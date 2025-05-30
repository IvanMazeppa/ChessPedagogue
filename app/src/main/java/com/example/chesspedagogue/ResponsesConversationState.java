package com.example.chesspedagogue;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponsesConversationState {
    private static final String TAG = "ConversationState";
    
    // Conversation metadata
    private String conversationId;
    private long startTime;
    private String whiteMaster;
    private String blackMaster;
    private boolean isActive;
    
    // Response tracking
    private final Map<String, String> lastResponseIds;
    private final Map<String, List<ConversationMessage>> messageHistory;
    private final Map<String, ConversationMetrics> metrics;
    
    // Conversation branching support
    private String parentConversationId;
    private List<String> childConversationIds;
    
    public static class ConversationMessage {
        public final String speaker;
        public final String content;
        public final String responseId;
        public final long timestamp;
        public final float evaluation; // Position evaluation at time of message
        
        public ConversationMessage(String speaker, String content, String responseId, float evaluation) {
            this.speaker = speaker;
            this.content = content;
            this.responseId = responseId;
            this.timestamp = System.currentTimeMillis();
            this.evaluation = evaluation;
        }
    }
    
    public static class ConversationMetrics {
        public int messageCount = 0;
        public int totalTokens = 0;
        public long totalResponseTime = 0;
        public float averageResponseTime = 0;
        public List<String> topicsDiscussed = new ArrayList<>();
    }
    
    public ResponsesConversationState() {
        this.conversationId = generateConversationId();
        this.startTime = System.currentTimeMillis();
        this.lastResponseIds = new ConcurrentHashMap<>();
        this.messageHistory = new ConcurrentHashMap<>();
        this.metrics = new ConcurrentHashMap<>();
        this.childConversationIds = new ArrayList<>();
        this.isActive = true;
    }
    
    // Start a new conversation between two masters
    public void startConversation(String whiteMaster, String blackMaster) {
        Log.d(TAG, "🎬 Starting conversation: " + whiteMaster + " vs " + blackMaster);
        
        this.whiteMaster = whiteMaster;
        this.blackMaster = blackMaster;
        
        // Initialize message history for both masters
        messageHistory.put(whiteMaster, new ArrayList<>());
        messageHistory.put(blackMaster, new ArrayList<>());
        
        // Initialize metrics
        metrics.put(whiteMaster, new ConversationMetrics());
        metrics.put(blackMaster, new ConversationMetrics());
    }
    
    // Update response ID for conversation continuity
    public void updateResponseId(String masterName, String responseId) {
        if (responseId != null && !responseId.isEmpty()) {
            lastResponseIds.put(masterName, responseId);
            Log.d(TAG, "📋 Updated response ID for " + masterName + ": " + responseId);
        }
    }
    
    // Get the last response ID for a master
    public String getLastResponseId(String masterName) {
        return lastResponseIds.get(masterName);
    }
    
    // Add a message to the conversation history
    public void addMessage(String speaker, String content) {
        addMessage(speaker, content, getLastResponseId(speaker), 0.0f);
    }
    
    public void addMessage(String speaker, String content, String responseId, float evaluation) {
        List<ConversationMessage> messages = messageHistory.computeIfAbsent(speaker, k -> new ArrayList<>());
        ConversationMessage message = new ConversationMessage(speaker, content, responseId, evaluation);
        messages.add(message);
        
        // Update metrics
        ConversationMetrics speakerMetrics = metrics.computeIfAbsent(speaker, k -> new ConversationMetrics());
        speakerMetrics.messageCount++;
        speakerMetrics.totalTokens += estimateTokenCount(content);
        
        // Extract topics (simple keyword extraction)
        extractTopics(content, speakerMetrics.topicsDiscussed);
        
        Log.d(TAG, "💬 Added message from " + speaker + " (msg #" + speakerMetrics.messageCount + ")");
    }
    
    // Get conversation history for a specific master
    public List<ConversationMessage> getMessageHistory(String masterName) {
        return new ArrayList<>(messageHistory.getOrDefault(masterName, new ArrayList<>()));
    }
    
    // Get full conversation history in chronological order
    public List<ConversationMessage> getFullConversation() {
        List<ConversationMessage> allMessages = new ArrayList<>();
        
        for (List<ConversationMessage> messages : messageHistory.values()) {
            allMessages.addAll(messages);
        }
        
        // Sort by timestamp
        allMessages.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
        
        return allMessages;
    }
    
    // Create a branch from current conversation
    public ResponsesConversationState branch(String branchReason) {
        Log.d(TAG, "🌿 Creating conversation branch: " + branchReason);
        
        ResponsesConversationState branch = new ResponsesConversationState();
        branch.parentConversationId = this.conversationId;
        branch.whiteMaster = this.whiteMaster;
        branch.blackMaster = this.blackMaster;
        
        // Copy current state to branch
        branch.lastResponseIds.putAll(this.lastResponseIds);
        
        // Track child conversation
        this.childConversationIds.add(branch.conversationId);
        
        return branch;
    }
    
    // Merge a branch back into main conversation
    public void merge(ResponsesConversationState branch) {
        if (!branch.parentConversationId.equals(this.conversationId)) {
            Log.w(TAG, "⚠️ Cannot merge unrelated conversation");
            return;
        }
        
        Log.d(TAG, "🔄 Merging conversation branch: " + branch.conversationId);
        
        // Merge message histories
        for (Map.Entry<String, List<ConversationMessage>> entry : branch.messageHistory.entrySet()) {
            List<ConversationMessage> mainMessages = messageHistory.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
            mainMessages.addAll(entry.getValue());
        }
        
        // Update response IDs to latest from branch
        lastResponseIds.putAll(branch.lastResponseIds);
    }
    
    // Get conversation summary
    public ConversationSummary getSummary() {
        ConversationSummary summary = new ConversationSummary();
        summary.conversationId = conversationId;
        summary.duration = System.currentTimeMillis() - startTime;
        summary.participants = List.of(whiteMaster, blackMaster);
        summary.totalMessages = getFullConversation().size();
        summary.isActive = isActive;
        
        // Aggregate metrics
        for (ConversationMetrics m : metrics.values()) {
            summary.totalTokens += m.totalTokens;
        }
        
        return summary;
    }
    
    public static class ConversationSummary {
        public String conversationId;
        public long duration;
        public List<String> participants;
        public int totalMessages;
        public int totalTokens;
        public boolean isActive;
    }
    
    // Helper methods
    private String generateConversationId() {
        return "conv_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    private int estimateTokenCount(String text) {
        // Rough estimation: ~1 token per 4 characters
        return text.length() / 4;
    }
    
    private void extractTopics(String content, List<String> topics) {
        String[] chessTerms = {
            "opening", "endgame", "middlegame", "tactics", "strategy",
            "sacrifice", "fork", "pin", "skewer", "checkmate",
            "pawn structure", "king safety", "development", "tempo",
            "initiative", "compensation", "weakness", "attack", "defense"
        };
        
        String lowerContent = content.toLowerCase();
        for (String term : chessTerms) {
            if (lowerContent.contains(term) && !topics.contains(term)) {
                topics.add(term);
            }
        }
    }
    
    // State management
    public void endConversation() {
        isActive = false;
        Log.d(TAG, "🏁 Conversation ended: " + conversationId);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    // Getters for conversation participants
    public String getWhiteMaster() {
        return whiteMaster;
    }
    
    public String getBlackMaster() {
        return blackMaster;
    }
    
    // Export conversation for analysis or storage
    public String exportAsJson() {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("conversationId", conversationId);
            json.put("whiteMaster", whiteMaster);
            json.put("blackMaster", blackMaster);
            json.put("startTime", startTime);
            json.put("isActive", isActive);
            
            // Add messages
            org.json.JSONArray messagesArray = new org.json.JSONArray();
            for (ConversationMessage msg : getFullConversation()) {
                org.json.JSONObject msgJson = new org.json.JSONObject();
                msgJson.put("speaker", msg.speaker);
                msgJson.put("content", msg.content);
                msgJson.put("timestamp", msg.timestamp);
                msgJson.put("evaluation", msg.evaluation);
                messagesArray.put(msgJson);
            }
            json.put("messages", messagesArray);
            
            return json.toString(2);
        } catch (Exception e) {
            Log.e(TAG, "Failed to export conversation", e);
            return "{}";
        }
    }
}