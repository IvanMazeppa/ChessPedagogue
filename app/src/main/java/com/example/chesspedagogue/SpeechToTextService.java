package com.example.chesspedagogue;

import java.io.IOException;

/**
 * Interface for services that convert speech audio to text.
 */
public interface SpeechToTextService {
    /**
     * Transcribe audio data to text
     *
     * @param audioData The raw audio data to transcribe
     * @return The transcribed text
     * @throws IOException If there's an error communicating with the API
     */
    String transcribeAudio(byte[] audioData) throws IOException;
}