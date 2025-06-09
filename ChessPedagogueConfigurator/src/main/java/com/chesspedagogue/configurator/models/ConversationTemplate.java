package com.chesspedagogue.configurator.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class ConversationTemplate {
    private String id;
    private String name;
    private String description;
    private String author;
    private String version;
    private int minTurns;
    private int maxTurns;
    private double responseChance;
    private int intervalMs;
    private String triggerType;
    
    // Enhanced response length controls
    private int initialMinWords;
    private int initialMaxWords;
    private int followupMinWords;
    private int followupMaxWords;
    private double variationFactor;
    private double emotionalMultiplier;
    private double accelerationFactor;
    private double pauseProbability;
    
    // Enhanced metadata
    private TemplateCategory category;
    private List<String> compatibleMasters;
    private List<String> tags;
    private LocalDateTime created;
    private LocalDateTime modified;
    private int usageCount;
    private double successRate;
    private boolean isReadOnly;
    
    // Advanced configuration
    private Map<String, Object> customProperties;
    private ConversationIntensity intensity;
    private List<TriggerCondition> triggers;
    
    public ConversationTemplate() {
        this.id = UUID.randomUUID().toString();
        this.compatibleMasters = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.customProperties = new HashMap<>();
        this.triggers = new ArrayList<>();
        this.usageCount = 0;
        this.successRate = 0.0;
        this.isReadOnly = false;
        this.version = "1.0";
        this.author = "User";
        
        // Default enhanced values
        this.initialMinWords = 30;
        this.initialMaxWords = 60;
        this.followupMinWords = 15;
        this.followupMaxWords = 35;
        this.variationFactor = 0.3;
        this.emotionalMultiplier = 1.2;
        this.accelerationFactor = 0.9;
        this.pauseProbability = 0.15;
    }
    
    public ConversationTemplate(String name, int minTurns, int maxTurns, double responseChance, int intervalMs) {
        this();
        this.name = name;
        this.minTurns = minTurns;
        this.maxTurns = maxTurns;
        this.responseChance = responseChance;
        this.intervalMs = intervalMs;
        this.triggerType = "general";
    }
    
    public ConversationTemplate(String name, int minTurns, int maxTurns, double responseChance, int intervalMs, String triggerType) {
        this();
        this.name = name;
        this.minTurns = minTurns;
        this.maxTurns = maxTurns;
        this.responseChance = responseChance;
        this.intervalMs = intervalMs;
        this.triggerType = triggerType;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getMinTurns() {
        return minTurns;
    }
    
    public void setMinTurns(int minTurns) {
        this.minTurns = minTurns;
    }
    
    public int getMaxTurns() {
        return maxTurns;
    }
    
    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }
    
    public double getResponseChance() {
        return responseChance;
    }
    
    public void setResponseChance(double responseChance) {
        this.responseChance = responseChance;
    }
    
    public int getIntervalMs() {
        return intervalMs;
    }
    
    public void setIntervalMs(int intervalMs) {
        this.intervalMs = intervalMs;
    }
    
    public String getTriggerType() {
        return triggerType;
    }
    
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
    
    // Enhanced response length getters and setters
    public int getInitialMinWords() { return initialMinWords; }
    public void setInitialMinWords(int initialMinWords) { 
        this.initialMinWords = initialMinWords;
        updateModified();
    }
    
    public int getInitialMaxWords() { return initialMaxWords; }
    public void setInitialMaxWords(int initialMaxWords) { 
        this.initialMaxWords = initialMaxWords;
        updateModified();
    }
    
    public int getFollowupMinWords() { return followupMinWords; }
    public void setFollowupMinWords(int followupMinWords) { 
        this.followupMinWords = followupMinWords;
        updateModified();
    }
    
    public int getFollowupMaxWords() { return followupMaxWords; }
    public void setFollowupMaxWords(int followupMaxWords) { 
        this.followupMaxWords = followupMaxWords;
        updateModified();
    }
    
    public double getVariationFactor() { return variationFactor; }
    public void setVariationFactor(double variationFactor) { 
        this.variationFactor = variationFactor;
        updateModified();
    }
    
    public double getEmotionalMultiplier() { return emotionalMultiplier; }
    public void setEmotionalMultiplier(double emotionalMultiplier) { 
        this.emotionalMultiplier = emotionalMultiplier;
        updateModified();
    }
    
    public double getAccelerationFactor() { return accelerationFactor; }
    public void setAccelerationFactor(double accelerationFactor) { 
        this.accelerationFactor = accelerationFactor;
        updateModified();
    }
    
    public double getPauseProbability() { return pauseProbability; }
    public void setPauseProbability(double pauseProbability) { 
        this.pauseProbability = pauseProbability;
        updateModified();
    }
    
    // New enhanced getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
        updateModified();
    }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public TemplateCategory getCategory() { return category; }
    public void setCategory(TemplateCategory category) { this.category = category; }
    
    public List<String> getCompatibleMasters() { return new ArrayList<>(compatibleMasters); }
    public void setCompatibleMasters(List<String> masters) { this.compatibleMasters = new ArrayList<>(masters); }
    public void addCompatibleMaster(String master) { 
        if (!compatibleMasters.contains(master)) {
            compatibleMasters.add(master);
            updateModified();
        }
    }
    
    public List<String> getTags() { return new ArrayList<>(tags); }
    public void setTags(List<String> tags) { this.tags = new ArrayList<>(tags); }
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            updateModified();
        }
    }
    
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    
    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public boolean isReadOnly() { return isReadOnly; }
    public void setReadOnly(boolean readOnly) { this.isReadOnly = readOnly; }
    
    public Map<String, Object> getCustomProperties() { return new HashMap<>(customProperties); }
    public void setCustomProperty(String key, Object value) { 
        customProperties.put(key, value);
        updateModified();
    }
    
    public ConversationIntensity getIntensity() { return intensity; }
    public void setIntensity(ConversationIntensity intensity) { this.intensity = intensity; }
    
    public List<TriggerCondition> getTriggers() { return new ArrayList<>(triggers); }
    public void setTriggers(List<TriggerCondition> triggers) { this.triggers = new ArrayList<>(triggers); }
    
    // Utility methods
    private void updateModified() {
        this.modified = LocalDateTime.now();
    }
    
    public void incrementUsage() {
        this.usageCount++;
        updateModified();
    }
    
    public ConversationTemplate duplicate() {
        ConversationTemplate copy = new ConversationTemplate();
        copy.name = this.name + " (Copy)";
        copy.description = this.description;
        copy.author = this.author;
        copy.version = this.version;
        copy.minTurns = this.minTurns;
        copy.maxTurns = this.maxTurns;
        copy.responseChance = this.responseChance;
        copy.intervalMs = this.intervalMs;
        copy.triggerType = this.triggerType;
        copy.compatibleMasters = new ArrayList<>(this.compatibleMasters);
        copy.tags = new ArrayList<>(this.tags);
        copy.customProperties = new HashMap<>(this.customProperties);
        copy.intensity = this.intensity;
        copy.triggers = new ArrayList<>(this.triggers);
        
        // Copy enhanced fields
        copy.initialMinWords = this.initialMinWords;
        copy.initialMaxWords = this.initialMaxWords;
        copy.followupMinWords = this.followupMinWords;
        copy.followupMaxWords = this.followupMaxWords;
        copy.variationFactor = this.variationFactor;
        copy.emotionalMultiplier = this.emotionalMultiplier;
        copy.accelerationFactor = this.accelerationFactor;
        copy.pauseProbability = this.pauseProbability;
        
        return copy;
    }
    
    public boolean isCompatibleWith(String master) {
        return compatibleMasters.isEmpty() || compatibleMasters.contains(master);
    }
    
    public String getDisplayName() {
        if (category != null) {
            return category.getName() + " / " + name;
        }
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d-%d turns, %.0f%% chance, %dms)", 
            name, minTurns, maxTurns, responseChance * 100, intervalMs);
    }
    
    // Enhanced predefined templates with natural flow controls
    public static ConversationTemplate minimal() {
        ConversationTemplate template = new ConversationTemplate("Minimal", 1, 2, 0.5, 4000);
        template.setInitialMinWords(10);
        template.setInitialMaxWords(25);
        template.setFollowupMinWords(5);
        template.setFollowupMaxWords(15);
        template.setVariationFactor(0.2);
        template.setEmotionalMultiplier(1.0);
        template.setAccelerationFactor(1.0);
        template.setPauseProbability(0.05);
        return template;
    }
    
    public static ConversationTemplate engaging() {
        ConversationTemplate template = new ConversationTemplate("Engaging", 3, 6, 0.85, 2500);
        template.setInitialMinWords(45);
        template.setInitialMaxWords(85);
        template.setFollowupMinWords(20);
        template.setFollowupMaxWords(50);
        template.setVariationFactor(0.35);
        template.setEmotionalMultiplier(1.25);
        template.setAccelerationFactor(0.88);
        template.setPauseProbability(0.15);
        return template;
    }
    
    public static ConversationTemplate intense() {
        ConversationTemplate template = new ConversationTemplate("Intense", 4, 10, 0.9, 2000);
        template.setInitialMinWords(70);
        template.setInitialMaxWords(120);
        template.setFollowupMinWords(35);
        template.setFollowupMaxWords(75);
        template.setVariationFactor(0.4);
        template.setEmotionalMultiplier(1.6);
        template.setAccelerationFactor(0.75);
        template.setPauseProbability(0.12);
        return template;
    }
    
    public static ConversationTemplate collaborative() {
        ConversationTemplate template = new ConversationTemplate("Collaborative", 3, 7, 0.8, 2800);
        template.setInitialMinWords(40);
        template.setInitialMaxWords(75);
        template.setFollowupMinWords(25);
        template.setFollowupMaxWords(55);
        template.setVariationFactor(0.3);
        template.setEmotionalMultiplier(1.1);
        template.setAccelerationFactor(1.05);
        template.setPauseProbability(0.2);
        return template;
    }
}