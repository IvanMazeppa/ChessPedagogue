package com.chesspedagogue.configurator.managers;

import com.chesspedagogue.configurator.models.ConversationTemplate;
import com.chesspedagogue.configurator.models.TemplateCategory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ConfigurationManager {
    private static final String CONFIG_FILE = "chess_pedagogue_config.json";
    private static final String TEMPLATE_LIBRARY_FILE = "template_library.json";
    private static final String SHARED_CONFIG_DIR = "../shared_config";
    
    private static ConfigurationManager instance;
    private Gson gson;
    private Configuration currentConfig;
    private List<ConversationTemplate> templateLibrary;
    private List<TemplateCategory> templateCategories;
    
    private ConfigurationManager() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
        
        this.templateLibrary = new ArrayList<>();
        this.templateCategories = new ArrayList<>();
        
        createSharedConfigDirectory();
        loadConfiguration();
        loadTemplateLibrary();
    }
    
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    private void createSharedConfigDirectory() {
        Path sharedDir = Paths.get(SHARED_CONFIG_DIR);
        try {
            if (!Files.exists(sharedDir)) {
                Files.createDirectories(sharedDir);
                System.out.println("Created shared config directory: " + sharedDir.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create shared config directory: " + e.getMessage());
        }
    }
    
    public void loadConfiguration() {
        Path configPath = Paths.get(SHARED_CONFIG_DIR, CONFIG_FILE);
        
        if (!Files.exists(configPath)) {
            System.out.println("No configuration file found, creating default configuration");
            currentConfig = Configuration.getDefaults();
            saveConfiguration();
            return;
        }
        
        try (FileReader reader = new FileReader(configPath.toFile())) {
            currentConfig = gson.fromJson(reader, Configuration.class);
            if (currentConfig == null) {
                currentConfig = Configuration.getDefaults();
            }
            System.out.println("Loaded configuration from: " + configPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to load configuration, using defaults: " + e.getMessage());
            currentConfig = Configuration.getDefaults();
        }
    }
    
    public void saveConfiguration() {
        Path configPath = Paths.get(SHARED_CONFIG_DIR, CONFIG_FILE);
        
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            gson.toJson(currentConfig, writer);
            System.out.println("Saved configuration to: " + configPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }
    
    public Configuration getConfiguration() {
        return currentConfig;
    }
    
    public void updateConfiguration(Configuration config) {
        this.currentConfig = config;
        saveConfiguration();
    }
    
    public ConversationTemplate getConversationTemplate(String templateName) {
        return currentConfig.conversationTemplates.get(templateName);
    }
    
    public void setConversationTemplate(String templateName, ConversationTemplate template) {
        currentConfig.conversationTemplates.put(templateName, template);
        saveConfiguration();
    }
    
    public void resetToDefaults() {
        currentConfig = Configuration.getDefaults();
        saveConfiguration();
        System.out.println("Configuration reset to defaults");
    }
    
    public void exportForAndroid() {
        // Export configuration in Android-compatible format
        System.out.println("Exporting configuration for Android app");
        saveConfiguration();
        saveTemplateLibrary(templateLibrary);
    }
    
    // Template Library Management Methods
    public void loadTemplateLibrary() {
        Path libraryPath = Paths.get(SHARED_CONFIG_DIR, TEMPLATE_LIBRARY_FILE);
        
        try {
            if (Files.exists(libraryPath)) {
                FileReader reader = new FileReader(libraryPath.toFile());
                Type listType = new TypeToken<List<ConversationTemplate>>() {}.getType();
                List<ConversationTemplate> loaded = gson.fromJson(reader, listType);
                
                if (loaded != null) {
                    templateLibrary.clear();
                    templateLibrary.addAll(loaded);
                }
                reader.close();
                System.out.println("Loaded " + templateLibrary.size() + " templates from library");
            } else {
                System.out.println("No template library found, starting with empty library");
                templateLibrary.clear();
            }
        } catch (Exception e) {
            System.err.println("Failed to load template library: " + e.getMessage());
            templateLibrary.clear();
        }
        
        // Always ensure we have system categories
        templateCategories.clear();
        templateCategories.addAll(TemplateCategory.getSystemCategories());
    }
    
    public void saveTemplateLibrary(List<ConversationTemplate> templates) {
        this.templateLibrary = new ArrayList<>(templates);
        Path libraryPath = Paths.get(SHARED_CONFIG_DIR, TEMPLATE_LIBRARY_FILE);
        
        try (FileWriter writer = new FileWriter(libraryPath.toFile())) {
            gson.toJson(templateLibrary, writer);
            System.out.println("Saved " + templateLibrary.size() + " templates to library: " + libraryPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save template library: " + e.getMessage());
        }
    }
    
    public List<ConversationTemplate> getTemplateLibrary() {
        return new ArrayList<>(templateLibrary);
    }
    
    public List<TemplateCategory> getTemplateCategories() {
        return new ArrayList<>(templateCategories);
    }
    
    public void addTemplate(ConversationTemplate template) {
        templateLibrary.add(template);
        saveTemplateLibrary(templateLibrary);
    }
    
    public void removeTemplate(ConversationTemplate template) {
        templateLibrary.remove(template);
        saveTemplateLibrary(templateLibrary);
    }
    
    public void updateTemplate(ConversationTemplate template) {
        for (int i = 0; i < templateLibrary.size(); i++) {
            if (templateLibrary.get(i).getId().equals(template.getId())) {
                templateLibrary.set(i, template);
                saveTemplateLibrary(templateLibrary);
                break;
            }
        }
    }
    
    public boolean validateConfiguration() {
        if (currentConfig == null) return false;
        if (currentConfig.conversationTemplates == null) return false;
        return !currentConfig.conversationTemplates.isEmpty();
    }
    
    public void setDarkMode(boolean darkMode) {
        currentConfig.darkMode = darkMode;
        saveConfiguration();
    }
    
    public boolean isDarkMode() {
        return currentConfig.darkMode;
    }
    
    // Configuration data class
    public static class Configuration {
        public Map<String, ConversationTemplate> conversationTemplates;
        public String activeTemplate;
        public boolean voiceEnabled;
        public boolean realTimeUpdates;
        public boolean darkMode;
        
        public Configuration() {
            this.conversationTemplates = new HashMap<>();
            this.activeTemplate = "engaging";
            this.voiceEnabled = true;
            this.realTimeUpdates = true;
            this.darkMode = false;
        }
        
        public static Configuration getDefaults() {
            Configuration config = new Configuration();
            
            // Add default conversation templates
            config.conversationTemplates.put("minimal", ConversationTemplate.minimal());
            config.conversationTemplates.put("engaging", ConversationTemplate.engaging());
            config.conversationTemplates.put("intense", ConversationTemplate.intense());
            config.conversationTemplates.put("collaborative", ConversationTemplate.collaborative());
            
            return config;
        }
    }
}