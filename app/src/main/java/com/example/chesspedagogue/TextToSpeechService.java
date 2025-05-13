package com.example.chesspedagogue;

import java.io.IOException;

/**
 * Interface for services that convert text to speech audio.
 */
public interface TextToSpeechService {
    /**
     * Convert text to speech audio
     *
     * @param text The text to convert to speech
     * @return The audio data as a byte array
     * @throws IOException If there's an error communicating with the API
     */
    byte[] synthesizeSpeech(String text) throws IOException;
}