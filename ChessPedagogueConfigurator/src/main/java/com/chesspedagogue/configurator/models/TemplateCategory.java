package com.chesspedagogue.configurator.models;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * TemplateCategory - Organizational structure for conversation templates
 * 
 * Categories help organize templates by scenario, intensity, or master type
 */
public class TemplateCategory {
    private String name;
    private String description;
    private String icon;
    private List<ConversationTemplate> templates;
    private TemplateCategory parent;
    private List<TemplateCategory> children;
    private LocalDateTime created;
    private boolean isSystem; // Built-in vs user-created
    
    public TemplateCategory() {
        this.templates = new ArrayList<>();
        this.children = new ArrayList<>();
        this.created = LocalDateTime.now();
        this.isSystem = false;
    }
    
    public TemplateCategory(String name, String description, String icon) {
        this();
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    
    public TemplateCategory(String name, String description, String icon, boolean isSystem) {
        this(name, description, icon);
        this.isSystem = isSystem;
    }
    
    // Template management
    public void addTemplate(ConversationTemplate template) {
        if (!templates.contains(template)) {
            templates.add(template);
            template.setCategory(this);
        }
    }
    
    public void removeTemplate(ConversationTemplate template) {
        templates.remove(template);
        template.setCategory(null);
    }
    
    public List<ConversationTemplate> getTemplates() {
        return new ArrayList<>(templates);
    }
    
    // Category hierarchy
    public void addChild(TemplateCategory child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
        }
    }
    
    public void removeChild(TemplateCategory child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public List<TemplateCategory> getChildren() {
        return new ArrayList<>(children);
    }
    
    // Get all templates including from children
    public List<ConversationTemplate> getAllTemplates() {
        List<ConversationTemplate> allTemplates = new ArrayList<>(templates);
        for (TemplateCategory child : children) {
            allTemplates.addAll(child.getAllTemplates());
        }
        return allTemplates;
    }
    
    // Tree path for display
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " / " + name;
    }
    
    // Statistics
    public int getTotalTemplateCount() {
        int count = templates.size();
        for (TemplateCategory child : children) {
            count += child.getTotalTemplateCount();
        }
        return count;
    }
    
    // Predefined system categories
    public static List<TemplateCategory> getSystemCategories() {
        List<TemplateCategory> categories = new ArrayList<>();
        
        // Opening Discussions
        TemplateCategory openings = new TemplateCategory(
            "Opening Discussions", 
            "Templates for opening theory and early game philosophy",
            "♟️", true
        );
        
        // Tactical Battles
        TemplateCategory tactics = new TemplateCategory(
            "Tactical Battles",
            "High-intensity tactical sequence conversations", 
            "⚔️", true
        );
        
        // Endgame Philosophy
        TemplateCategory endgames = new TemplateCategory(
            "Endgame Philosophy",
            "Deep strategic discussions about endgame principles",
            "👑", true
        );
        
        // Master Rivalries
        TemplateCategory rivalries = new TemplateCategory(
            "Master Rivalries", 
            "Competitive banter between historical rivals",
            "🥊", true
        );
        
        // Teaching Moments
        TemplateCategory teaching = new TemplateCategory(
            "Teaching Moments",
            "Educational exchanges and mentoring conversations",
            "🎓", true
        );
        
        // Historical References
        TemplateCategory historical = new TemplateCategory(
            "Historical References",
            "Conversations referencing famous games and moments",
            "📚", true
        );
        
        categories.add(openings);
        categories.add(tactics);
        categories.add(endgames);
        categories.add(rivalries);
        categories.add(teaching);
        categories.add(historical);
        
        return categories;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public TemplateCategory getParent() { return parent; }
    public void setParent(TemplateCategory parent) { this.parent = parent; }
    
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    
    public boolean isSystem() { return isSystem; }
    public void setSystem(boolean system) { this.isSystem = system; }
    
    @Override
    public String toString() {
        return String.format("%s %s (%d templates)", 
            icon != null ? icon : "📁", name, getTotalTemplateCount());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TemplateCategory that = (TemplateCategory) obj;
        return name != null && name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}