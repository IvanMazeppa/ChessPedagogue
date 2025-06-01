package com.example.chesspedagogue;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats text for ElevenLabs TTS with appropriate controls for better speech output
 * Based on ElevenLabs best practices: https://elevenlabs.io/docs/best-practices/prompting/controls
 */
public class ElevenLabsTTSFormatter {
    private static final String TAG = "ElevenLabsTTSFormatter";
    
    /**
     * Format text with ElevenLabs controls based on chess master personality and context
     */
    public static String formatForTTS(String text, String masterName, boolean isEmotional) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        StringBuilder formatted = new StringBuilder();
        
        // Add emotional context if needed
        if (isEmotional) {
            formatted.append(getEmotionalPrefix(masterName));
        }
        
        // Process the main text
        String processedText = text;
        
        // Add pauses for dramatic effect
        processedText = addDramaticPauses(processedText, masterName);
        
        // Add emphasis to key chess terms
        processedText = emphasizeChessTerms(processedText);
        
        // Handle move notation with proper pacing
        processedText = formatMoveNotation(processedText);
        
        // Add personality-specific styling
        processedText = addPersonalityStyling(processedText, masterName);
        
        formatted.append(processedText);
        
        // Add emotional suffix if needed
        if (isEmotional) {
            formatted.append(getEmotionalSuffix(masterName));
        }
        
        return formatted.toString();
    }
    
    /**
     * Add dramatic pauses based on punctuation and context
     */
    private static String addDramaticPauses(String text, String masterName) {
        // CRITICAL FIX: First, clean any existing malformed break tags
        text = cleanMalformedBreakTags(text);
        
        // Replace ellipses with break tags for dramatic effect
        text = text.replaceAll("\\.\\.\\.+", "<break time=\"0.8s\" />");
        
        // Add pauses after exclamation marks for emphasis
        text = text.replaceAll("!\\s+", "! <break time=\"0.5s\" /> ");
        
        // Add pauses after questions for thoughtful delivery
        text = text.replaceAll("\\?\\s+", "? <break time=\"0.6s\" /> ");
        
        // Master-specific pauses for commas
        String pauseTime;
        switch (masterName.toLowerCase()) {
            case "tal":
                pauseTime = "0.2s"; // Tal speaks with excitement, shorter pauses
                break;
            case "karpov":
                pauseTime = "0.4s"; // Karpov is methodical, longer pauses
                break;
            default:
                pauseTime = "0.3s";
        }
        
        // Replace commas with pauses
        text = text.replaceAll(",\\s+", ", <break time=\"" + pauseTime + "\" /> ");
        
        // FINAL CLEANUP: Remove any duplicate or malformed break tags that might have been created
        text = cleanMalformedBreakTags(text);
        
        return text;
    }
    
    /**
     * Clean malformed break tags to prevent ElevenLabs from speaking them
     */
    private static String cleanMalformedBreakTags(String text) {
        // Remove nested break tags like: <break time="0. <break time="0.2s" /> 3s" />
        text = text.replaceAll("<break time=\"[^\"]*<break[^>]*>[^\"]*\"[^>]*>", "");
        
        // Remove any break tags that contain other break tags inside them
        text = text.replaceAll("<break[^>]*<break[^>]*>[^>]*>", "");
        
        // Fix malformed time attributes (contains periods, spaces, letters)
        text = text.replaceAll("<break time=\"[^\"]*[a-zA-Z][^\"]*\"[^>]*>", "");
        text = text.replaceAll("<break time=\"[^\"]*\\.[^0-9][^\"]*\"[^>]*>", "");
        
        // Remove any break tags with empty or invalid time values
        text = text.replaceAll("<break time=\"\"[^>]*>", "");
        text = text.replaceAll("<break time=\"\\s*\"[^>]*>", "");
        
        return text;
    }
    
    /**
     * Emphasize key chess terms and evaluations
     */
    private static String emphasizeChessTerms(String text) {
        // Emphasize tactical terms
        text = text.replaceAll("\\b(sacrifice|combination|attack|defense|checkmate|mate)\\b", 
                              "*$1*");
        
        // Emphasize evaluations
        text = text.replaceAll("\\b(winning|losing|equal|advantage|disadvantage)\\b", 
                              "*$1*");
        
        // Emphasize piece names when they're the focus
        text = text.replaceAll("\\b(The )?(knight|bishop|rook|queen|king|pawn)\\b", 
                              "$1*$2*");
        
        return text;
    }
    
    /**
     * Format chess move notation for clearer speech
     */
    private static String formatMoveNotation(String text) {
        // Add slight pauses around move numbers
        text = text.replaceAll("\\b(\\d+)\\.\\s*", "$1. <break time=\"0.2s\" /> ");
        
        // Spell out captures
        text = text.replaceAll("([A-Ra-h][1-8])x([A-Ra-h][1-8])", "$1 takes $2");
        text = text.replaceAll("([NBRQK])x([a-h][1-8])", "$1 takes $2");
        
        // Spell out check and checkmate
        text = text.replaceAll("\\+", " check");
        text = text.replaceAll("#", " checkmate!");
        
        // Handle castling
        text = text.replaceAll("O-O-O", "queenside castling");
        text = text.replaceAll("O-O", "kingside castling");
        
        return text;
    }
    
    /**
     * Add personality-specific speech styling
     */
    private static String addPersonalityStyling(String text, String masterName) {
        switch (masterName.toLowerCase()) {
            case "tal":
                // Tal: Excited, passionate delivery - rely on voice settings instead of spoken instructions
                break;
                
            case "fischer":
                // Fischer: Intense, assertive delivery - rely on voice settings instead of spoken instructions
                break;
                
            case "carlsen":
                // Carlsen: Casual, confident delivery - rely on voice settings instead of spoken instructions
                break;
                
            case "kasparov":
                // Kasparov: Dynamic, energetic delivery - rely on voice settings instead of spoken instructions
                break;
                
            case "karpov":
                // Karpov: Calm, measured delivery - rely on voice settings instead of spoken instructions
                break;
        }
        
        return text;
    }
    
    /**
     * Get emotional prefix for dramatic moments - DISABLED to prevent TTS speaking instructions
     */
    private static String getEmotionalPrefix(String masterName) {
        // Disabled to prevent TTS from speaking stage directions
        return "";
    }
    
    /**
     * Get emotional suffix for dramatic moments
     */
    private static String getEmotionalSuffix(String masterName) {
        return " <break time=\"0.5s\" />";
    }
    
    /**
     * Wrap text with emotion descriptor - DISABLED to prevent TTS speaking instructions
     */
    private static String wrapWithEmotion(String text, String emotionPrefix) {
        // Disabled to prevent TTS from speaking stage directions
        return text;
    }
    
    /**
     * Remove any existing TTS controls for clean processing
     */
    public static String cleanForReprocessing(String text) {
        // Remove break tags
        text = text.replaceAll("<break[^>]*>", "");
        
        // Remove emphasis markers
        text = text.replaceAll("\\*([^*]+)\\*", "$1");
        
        // Remove emotion descriptors
        text = text.replaceAll("\\([^)]+\\)\\s*", "");
        
        return text.trim();
    }
}