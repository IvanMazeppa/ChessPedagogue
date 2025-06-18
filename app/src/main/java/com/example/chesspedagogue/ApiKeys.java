package com.example.chesspedagogue;

/**
 * API Keys configuration
 * WARNING: This file contains sensitive API keys and should NEVER be committed to git
 * Add this file to .gitignore to prevent accidental exposure
 */
public class ApiKeys {
    
    // OpenAI API Key for Responses API and Chat Completions
    public static final String OPENAI_API_KEY = "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";
    
    // ElevenLabs API Key for TTS (if used) - ALTERNATE ACCOUNT
    public static final String ELEVENLABS_API_KEY = "sk_788fa3710ea8bb4363f71f110a7b360a50f48beb4168fbf4";
    
    // Groq API Key for Speech Recognition (if used)
    public static final String GROQ_API_KEY = "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";
    
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
        if (GROQ_API_KEY == null || GROQ_API_KEY.trim().isEmpty()) {
            throw new IllegalStateException("Groq API key not configured in ApiKeys.java");
        }
        return GROQ_API_KEY;
    }
}