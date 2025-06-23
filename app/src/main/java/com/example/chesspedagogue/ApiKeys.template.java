package com.example.chesspedagogue;

/**
 * API Keys configuration TEMPLATE
 * 
 * SETUP INSTRUCTIONS:
 * 1. Copy this file to "ApiKeys.java" in the same directory
 * 2. Replace the placeholder values with your actual API keys
 * 3. NEVER commit the actual ApiKeys.java file to git
 * 
 * The actual ApiKeys.java file is ignored by .gitignore for security
 */
class ApiKeysTemplate {
    
    // OpenAI API Key for Responses API and Chat Completions
    public static final String OPENAI_API_KEY = "YOUR_OPENAI_API_KEY_HERE";
    
    // ElevenLabs API Key for TTS (if used)
    public static final String ELEVENLABS_API_KEY = "YOUR_ELEVENLABS_API_KEY_HERE";
    
    // Groq API Key for Speech Recognition (if used)
    public static final String GROQ_API_KEY = "YOUR_GROQ_API_KEY_HERE";
    
    /**
     * Validate that API keys are properly configured
     */
    public static boolean isConfigured() {
        return !OPENAI_API_KEY.equals("YOUR_OPENAI_API_KEY_HERE") && 
               !OPENAI_API_KEY.trim().isEmpty();
    }
    
    /**
     * Get OpenAI API key with validation
     */
    public static String getOpenAIKey() {
        if (!isConfigured()) {
            throw new IllegalStateException("OpenAI API key not configured in ApiKeys.java");
        }
        return OPENAI_API_KEY;
    }
    
    /**
     * Get Groq API key with validation
     */
    public static String getGroqKey() {
        if (GROQ_API_KEY == null || GROQ_API_KEY.trim().isEmpty() || 
            GROQ_API_KEY.equals("YOUR_GROQ_API_KEY_HERE")) {
            throw new IllegalStateException("Groq API key not configured in ApiKeys.java");
        }
        return GROQ_API_KEY;
    }
    
    /**
     * Get ElevenLabs API key with validation
     */
    public static String getElevenLabsKey() {
        if (ELEVENLABS_API_KEY == null || ELEVENLABS_API_KEY.trim().isEmpty() || 
            ELEVENLABS_API_KEY.equals("YOUR_ELEVENLABS_API_KEY_HERE")) {
            throw new IllegalStateException("ElevenLabs API key not configured in ApiKeys.java");
        }
        return ELEVENLABS_API_KEY;
    }
}