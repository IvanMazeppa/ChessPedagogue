# Configuration Resilience Plan

## Centralized Configuration Manager

### Unified Configuration System
```java
public class RobustConfigurationManager {
    private static final String CONFIG_FILE = "chess_pedagogue_config.json";
    private static final String BACKUP_CONFIG_FILE = "chess_pedagogue_config_backup.json";
    private static RobustConfigurationManager instance;
    
    private JSONObject config;
    private final Object configLock = new Object();
    
    public static synchronized RobustConfigurationManager getInstance(Context context) {
        if (instance == null) {
            instance = new RobustConfigurationManager(context);
        }
        return instance;
    }
    
    private RobustConfigurationManager(Context context) {
        loadConfiguration(context);
    }
    
    private void loadConfiguration(Context context) {
        synchronized (configLock) {
            try {
                // Try to load main config
                config = loadConfigFromFile(context, CONFIG_FILE);
            } catch (Exception e) {
                Log.w("Config", "Main config failed, trying backup", e);
                try {
                    // Fallback to backup
                    config = loadConfigFromFile(context, BACKUP_CONFIG_FILE);
                    Log.i("Config", "Loaded from backup configuration");
                } catch (Exception backupE) {
                    Log.e("Config", "Both configs failed, using defaults", backupE);
                    config = createDefaultConfiguration();
                }
            }
            
            // Validate configuration
            validateAndRepairConfiguration();
        }
    }
    
    private JSONObject createDefaultConfiguration() {
        JSONObject defaultConfig = new JSONObject();
        try {
            defaultConfig.put("api_timeout_ms", 30000);
            defaultConfig.put("max_retries", 3);
            defaultConfig.put("voice_enabled", true);
            defaultConfig.put("emotional_intelligence_enabled", true);
            defaultConfig.put("backup_interval_hours", 24);
            
            // Master settings
            JSONObject masterSettings = new JSONObject();
            masterSettings.put("default_master", "tal");
            masterSettings.put("personality_strength", 0.8);
            defaultConfig.put("master_settings", masterSettings);
            
            // API settings
            JSONObject apiSettings = new JSONObject();
            apiSettings.put("openai_model", "gpt-4");
            apiSettings.put("groq_model", "llama3-8b-8192");
            apiSettings.put("elevenlabs_voice", "auto");
            defaultConfig.put("api_settings", apiSettings);
            
        } catch (JSONException e) {
            Log.e("Config", "Error creating default config", e);
        }
        
        return defaultConfig;
    }
    
    private void validateAndRepairConfiguration() {
        // Check for required keys and add defaults if missing
        String[] requiredKeys = {
            "api_timeout_ms", "max_retries", "voice_enabled", 
            "emotional_intelligence_enabled", "master_settings", "api_settings"
        };
        
        boolean needsRepair = false;
        JSONObject defaults = createDefaultConfiguration();
        
        for (String key : requiredKeys) {
            if (!config.has(key)) {
                try {
                    config.put(key, defaults.get(key));
                    needsRepair = true;
                    Log.w("Config", "Repaired missing key: " + key);
                } catch (JSONException e) {
                    Log.e("Config", "Error repairing key: " + key, e);
                }
            }
        }
        
        if (needsRepair) {
            saveConfiguration();
            Log.i("Config", "Configuration repaired and saved");
        }
    }
    
    public void saveConfiguration() {
        synchronized (configLock) {
            try {
                // Save to both main and backup
                saveConfigToFile(CONFIG_FILE);
                saveConfigToFile(BACKUP_CONFIG_FILE);
            } catch (Exception e) {
                Log.e("Config", "Error saving configuration", e);
            }
        }
    }
    
    // Type-safe getters with defaults
    public String getString(String key, String defaultValue) {
        synchronized (configLock) {
            try {
                return config.optString(key, defaultValue);
            } catch (Exception e) {
                Log.w("Config", "Error getting string for key: " + key, e);
                return defaultValue;
            }
        }
    }
    
    public int getInt(String key, int defaultValue) {
        synchronized (configLock) {
            try {
                return config.optInt(key, defaultValue);
            } catch (Exception e) {
                Log.w("Config", "Error getting int for key: " + key, e);
                return defaultValue;
            }
        }
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        synchronized (configLock) {
            try {
                return config.optBoolean(key, defaultValue);
            } catch (Exception e) {
                Log.w("Config", "Error getting boolean for key: " + key, e);
                return defaultValue;
            }
        }
    }
}
```

## SharedPreferences Consolidation
```java
public class UnifiedPreferencesManager {
    private static final String PREFS_NAME = "ChessPedagoguePrefs"; // Single source of truth
    private static UnifiedPreferencesManager instance;
    private SharedPreferences prefs;
    
    public static synchronized UnifiedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new UnifiedPreferencesManager(context);
        }
        return instance;
    }
    
    private UnifiedPreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        migrateOldPreferences(context);
    }
    
    private void migrateOldPreferences(Context context) {
        // Migrate from old preference files
        String[] oldPrefNames = {
            "chess_pedagogue_prefs", 
            "ChessFineTunedModels", 
            "ChessAppPrefs"
        };
        
        SharedPreferences.Editor editor = prefs.edit();
        boolean needsSave = false;
        
        for (String oldPrefName : oldPrefNames) {
            try {
                SharedPreferences oldPrefs = context.getSharedPreferences(oldPrefName, Context.MODE_PRIVATE);
                Map<String, ?> oldValues = oldPrefs.getAll();
                
                for (Map.Entry<String, ?> entry : oldValues.entrySet()) {
                    String key = entry.getKey();
                    if (!prefs.contains(key)) { // Don't overwrite existing values
                        Object value = entry.getValue();
                        if (value instanceof String) {
                            editor.putString(key, (String) value);
                        } else if (value instanceof Boolean) {
                            editor.putBoolean(key, (Boolean) value);
                        } else if (value instanceof Integer) {
                            editor.putInt(key, (Integer) value);
                        } else if (value instanceof Long) {
                            editor.putLong(key, (Long) value);
                        } else if (value instanceof Float) {
                            editor.putFloat(key, (Float) value);
                        }
                        needsSave = true;
                    }
                }
                
                // Clear old preferences after migration
                oldPrefs.edit().clear().apply();
                
            } catch (Exception e) {
                Log.w("Migration", "Error migrating from " + oldPrefName, e);
            }
        }
        
        if (needsSave) {
            editor.apply();
            Log.i("Migration", "Preferences migrated to unified system");
        }
    }
    
    // Wrapper methods with validation
    public String getString(String key, String defaultValue) {
        try {
            return prefs.getString(key, defaultValue);
        } catch (Exception e) {
            Log.w("Prefs", "Error getting string for key: " + key, e);
            return defaultValue;
        }
    }
    
    public void putString(String key, String value) {
        try {
            prefs.edit().putString(key, value).apply();
        } catch (Exception e) {
            Log.e("Prefs", "Error saving string for key: " + key, e);
        }
    }
}
```

## Configuration Validation
```java
public class ConfigurationValidator {
    
    public static class ValidationResult {
        public final boolean isValid;
        public final List<String> errors;
        public final List<String> warnings;
        
        public ValidationResult(boolean isValid, List<String> errors, List<String> warnings) {
            this.isValid = isValid;
            this.errors = errors;
            this.warnings = warnings;
        }
    }
    
    public ValidationResult validateConfiguration(JSONObject config) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate API settings
        if (!config.has("api_settings")) {
            errors.add("Missing API settings");
        } else {
            try {
                JSONObject apiSettings = config.getJSONObject("api_settings");
                
                // Check for required API models
                if (!apiSettings.has("openai_model")) {
                    warnings.add("OpenAI model not specified, using default");
                }
                
                if (!apiSettings.has("groq_model")) {
                    warnings.add("Groq model not specified, using default");
                }
                
            } catch (JSONException e) {
                errors.add("Invalid API settings format");
            }
        }
        
        // Validate master settings
        if (!config.has("master_settings")) {
            errors.add("Missing master settings");
        } else {
            try {
                JSONObject masterSettings = config.getJSONObject("master_settings");
                
                String defaultMaster = masterSettings.optString("default_master", "");
                if (defaultMaster.isEmpty()) {
                    warnings.add("No default master specified");
                }
                
                double personalityStrength = masterSettings.optDouble("personality_strength", -1);
                if (personalityStrength < 0 || personalityStrength > 1) {
                    warnings.add("Personality strength should be between 0 and 1");
                }
                
            } catch (Exception e) {
                errors.add("Invalid master settings format");
            }
        }
        
        // Validate timeouts and limits
        int apiTimeout = config.optInt("api_timeout_ms", -1);
        if (apiTimeout <= 0) {
            warnings.add("API timeout not set or invalid, using default");
        }
        
        int maxRetries = config.optInt("max_retries", -1);
        if (maxRetries < 0 || maxRetries > 10) {
            warnings.add("Max retries should be between 0 and 10");
        }
        
        boolean isValid = errors.isEmpty();
        return new ValidationResult(isValid, errors, warnings);
    }
}
```