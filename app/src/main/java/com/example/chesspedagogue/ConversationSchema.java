package com.example.chesspedagogue;

/**
 * Enhanced conversation schema for natural, flowing chess master dialogue
 */
public class ConversationSchema {
    
    public enum SchemaType {
        OPENING_CHAT,      // Game start - substantial discussion
        QUICK_REACTION,    // Brilliancy/blunder - short burst  
        POSITION_ANALYSIS, // Mid-game analysis - moderate
        ENDGAME_WRAP      // Game conclusion - reflective
    }
    
    public static class ResponseLength {
        public final int minWords;
        public final int maxWords;
        public final double variationFactor; // 0.0-1.0 for natural variation
        public final String lengthStyle; // "terse", "conversational", "elaborate"
        
        // Enhanced: Percentage-based follow-up control
        public final boolean usePercentageOfInitial;
        public final double followupPercentageMin; // 0.3 = 30% of initial response
        public final double followupPercentageMax; // 0.8 = 80% of initial response
        
        public ResponseLength(int minWords, int maxWords, double variationFactor, String lengthStyle) {
            this.minWords = minWords;
            this.maxWords = maxWords;
            this.variationFactor = variationFactor;
            this.lengthStyle = lengthStyle;
            this.usePercentageOfInitial = false;
            this.followupPercentageMin = 0.5;
            this.followupPercentageMax = 0.8;
        }
        
        // Enhanced constructor with percentage-based follow-up control
        public ResponseLength(int minWords, int maxWords, double variationFactor, String lengthStyle,
                             double followupPercentageMin, double followupPercentageMax) {
            this.minWords = minWords;
            this.maxWords = maxWords;
            this.variationFactor = variationFactor;
            this.lengthStyle = lengthStyle;
            this.usePercentageOfInitial = true;
            this.followupPercentageMin = followupPercentageMin;
            this.followupPercentageMax = followupPercentageMax;
        }
        
        /**
         * Get target word count with natural variation
         */
        public int getTargetWordCount() {
            int baseLength = minWords + (int)((maxWords - minWords) * Math.random());
            if (variationFactor > 0) {
                int variation = (int)(baseLength * variationFactor * (Math.random() - 0.5));
                baseLength += variation;
            }
            return Math.max(minWords, Math.min(maxWords, baseLength));
        }
        
        /**
         * Get target word count as percentage of initial response (for follow-ups)
         */
        public int getTargetWordCountAsPercentage(int initialWordCount) {
            if (!usePercentageOfInitial) {
                return getTargetWordCount();
            }
            
            double percentage = followupPercentageMin + 
                (followupPercentageMax - followupPercentageMin) * Math.random();
            int baseLength = (int)(initialWordCount * percentage);
            
            if (variationFactor > 0) {
                int variation = (int)(baseLength * variationFactor * (Math.random() - 0.5));
                baseLength += variation;
            }
            
            // Ensure minimum bounds
            return Math.max(Math.min(minWords, 8), baseLength);
        }
        
        /**
         * Get length guidance string for API
         */
        public String getLengthGuidance() {
            return String.format("Target %d-%d words (%s style)", minWords, maxWords, lengthStyle);
        }
    }
    
    public enum ConversationFlowPattern {
        STEADY,     // Consistent length throughout
        DECLINING,  // Start long, get shorter (natural conversation fade)
        BUILDING,   // Start short, build up intensity
        WAVE,       // Up and down like natural conversation
        EXPLOSIVE,  // Short-long-short (dramatic peak)
        FADE_IN     // Gradual increase then gentle decline
    }
    
    public static class ConversationRhythm {
        public final double accelerationFactor; // Responses get shorter/faster over time
        public final double emotionalIntensityMultiplier; // High emotion = longer responses
        public final boolean allowInterruptions; // Can cut off for urgent moves
        public final double pauseProbability; // Chance of natural pause (0.0-1.0)
        
        // Enhanced: Natural conversation flow patterns
        public final ConversationFlowPattern flowPattern;
        public final double flowIntensity; // How dramatic the pattern effect is (0.1-2.0)
        
        public ConversationRhythm(double acceleration, double emotionalMultiplier, 
                                 boolean interruptions, double pauseProb) {
            this.accelerationFactor = acceleration;
            this.emotionalIntensityMultiplier = emotionalMultiplier;
            this.allowInterruptions = interruptions;
            this.pauseProbability = pauseProb;
            this.flowPattern = ConversationFlowPattern.STEADY;
            this.flowIntensity = 1.0;
        }
        
        // Enhanced constructor with flow patterns
        public ConversationRhythm(double acceleration, double emotionalMultiplier, 
                                 boolean interruptions, double pauseProb,
                                 ConversationFlowPattern pattern, double flowIntensity) {
            this.accelerationFactor = acceleration;
            this.emotionalIntensityMultiplier = emotionalMultiplier;
            this.allowInterruptions = interruptions;
            this.pauseProbability = pauseProb;
            this.flowPattern = pattern;
            this.flowIntensity = Math.max(0.1, Math.min(2.0, flowIntensity));
        }
        
        /**
         * Get length multiplier based on conversation flow pattern
         */
        public double getFlowMultiplier(int turnNumber, int maxTurns) {
            if (maxTurns <= 1) return 1.0;
            
            double position = (double) turnNumber / (maxTurns - 1); // 0.0 to 1.0
            
            switch (flowPattern) {
                case DECLINING:
                    // Start at 1.0 + intensity, decline to 1.0 - intensity
                    return (1.0 + flowIntensity * 0.5) - (flowIntensity * position);
                    
                case BUILDING:
                    // Start at 1.0 - intensity, build to 1.0 + intensity
                    return (1.0 - flowIntensity * 0.3) + (flowIntensity * 0.8 * position);
                    
                case WAVE:
                    // Sine wave pattern: starts medium, peaks mid-conversation, ends medium
                    return 1.0 + (flowIntensity * 0.4 * Math.sin(position * Math.PI));
                    
                case EXPLOSIVE:
                    // Sharp peak in the middle
                    double distanceFromCenter = Math.abs(position - 0.5) * 2; // 0 at center, 1 at edges
                    return 1.0 + (flowIntensity * 0.6 * (1.0 - distanceFromCenter));
                    
                case FADE_IN:
                    // Gradual increase to 70%, then gentle decline
                    if (position < 0.7) {
                        return (1.0 - flowIntensity * 0.3) + (flowIntensity * 0.7 * (position / 0.7));
                    } else {
                        double fadePosition = (position - 0.7) / 0.3;
                        return (1.0 + flowIntensity * 0.4) - (flowIntensity * 0.2 * fadePosition);
                    }
                    
                case STEADY:
                default:
                    return 1.0;
            }
        }
    }
    
    public static class Schema {
        public final SchemaType type;
        public final int minTurns;
        public final int maxTurns;
        public final ResponseLength initialResponse;
        public final ResponseLength followupResponse;
        public final ConversationRhythm rhythm;
        public final double responseChance; // 0.0-1.0 probability of responding
        public final long minInterval; // ms between responses
        public final long maxInterval; // ms maximum delay for natural pauses
        
        // Legacy support
        public final String initialLengthGuide;
        public final String responseLengthGuide;
        
        public Schema(SchemaType type, int minTurns, int maxTurns,
                     ResponseLength initialResponse, ResponseLength followupResponse,
                     ConversationRhythm rhythm, double responseChance, 
                     long minInterval, long maxInterval) {
            this.type = type;
            this.minTurns = minTurns;
            this.maxTurns = maxTurns;
            this.initialResponse = initialResponse;
            this.followupResponse = followupResponse;
            this.rhythm = rhythm;
            this.responseChance = responseChance;
            this.minInterval = minInterval;
            this.maxInterval = maxInterval;
            
            // Legacy support
            this.initialLengthGuide = initialResponse.getLengthGuidance();
            this.responseLengthGuide = followupResponse.getLengthGuidance();
        }
        
        /**
         * Get response length for current turn with natural flow
         */
        public ResponseLength getResponseLengthForTurn(int turnNumber, double emotionalIntensity) {
            return getResponseLengthForTurn(turnNumber, emotionalIntensity, -1);
        }
        
        /**
         * Enhanced: Get response length with initial response word count for percentage calculations
         */
        public ResponseLength getResponseLengthForTurn(int turnNumber, double emotionalIntensity, int initialWordCount) {
            ResponseLength baseLength = (turnNumber == 0) ? initialResponse : followupResponse;
            
            // Apply conversation flow pattern
            double flowMultiplier = rhythm.getFlowMultiplier(turnNumber, maxTurns);
            
            // Apply rhythm adjustments (acceleration over time)
            if (rhythm.accelerationFactor != 1.0 && turnNumber > 0) {
                double factor = Math.pow(rhythm.accelerationFactor, turnNumber);
                flowMultiplier *= factor;
            }
            
            // Calculate adjusted word counts
            int adjustedMin, adjustedMax;
            
            if (turnNumber > 0 && baseLength.usePercentageOfInitial && initialWordCount > 0) {
                // Use percentage-based calculation for follow-ups
                int percentageTarget = baseLength.getTargetWordCountAsPercentage(initialWordCount);
                adjustedMin = (int)(percentageTarget * 0.8 * flowMultiplier);
                adjustedMax = (int)(percentageTarget * 1.2 * flowMultiplier);
            } else {
                // Use absolute word counts (for initial response or when percentage not available)
                adjustedMin = (int)(baseLength.minWords * flowMultiplier);
                adjustedMax = (int)(baseLength.maxWords * flowMultiplier);
            }
            
            // Apply emotional intensity
            if (emotionalIntensity > 0.7 && rhythm.emotionalIntensityMultiplier > 1.0) {
                adjustedMin = (int)(adjustedMin * rhythm.emotionalIntensityMultiplier);
                adjustedMax = (int)(adjustedMax * rhythm.emotionalIntensityMultiplier);
            }
            
            // Ensure minimum bounds
            adjustedMin = Math.max(5, adjustedMin);
            adjustedMax = Math.max(adjustedMin + 3, adjustedMax);
            
            return new ResponseLength(
                adjustedMin, adjustedMax,
                baseLength.variationFactor,
                emotionalIntensity > 0.7 ? "passionate" : baseLength.lengthStyle
            );
        }
        
        /**
         * Get interval with natural variation and pauses
         */
        public long getIntervalForTurn(int turnNumber) {
            long baseInterval = minInterval + (long)((maxInterval - minInterval) * Math.random());
            
            // Natural pause chance
            if (Math.random() < rhythm.pauseProbability) {
                baseInterval += (long)(baseInterval * 0.5); // 50% longer for natural pause
            }
            
            return baseInterval;
        }
    }
    
    // Enhanced schemas with natural flow controls
    public static final Schema OPENING_SCHEMA = new Schema(
        SchemaType.OPENING_CHAT,
        2, 4, // 2-4 exchanges
        new ResponseLength(40, 80, 0.3, "elaborate"), // Initial: thoughtful 40-80 words
        new ResponseLength(15, 35, 0.4, "conversational"), // Follow-up: 15-35 words
        new ConversationRhythm(0.9, 1.2, false, 0.15), // Slight acceleration, emotion boost
        0.8, // 80% chance to respond
        2500, 4000 // 2.5-4 second intervals
    );
    
    public static final Schema QUICK_REACTION_SCHEMA = new Schema(
        SchemaType.QUICK_REACTION,
        1, 3, // 1-3 exchanges  
        new ResponseLength(8, 25, 0.5, "terse"), // Initial: quick 8-25 words
        new ResponseLength(5, 15, 0.6, "terse"), // Follow-up: very brief 5-15 words
        new ConversationRhythm(0.8, 1.5, true, 0.05), // Fast acceleration, high emotion
        0.6, // 60% chance to respond
        1500, 3000 // 1.5-3 second intervals
    );
    
    public static final Schema POSITION_ANALYSIS_SCHEMA = new Schema(
        SchemaType.POSITION_ANALYSIS,
        2, 5, // 2-5 exchanges
        new ResponseLength(25, 60, 0.25, "analytical"), // Initial: analytical 25-60 words
        new ResponseLength(12, 40, 0.35, "conversational"), // Follow-up: 12-40 words
        new ConversationRhythm(0.95, 1.1, false, 0.2), // Minimal acceleration, slight emotion
        0.7, // 70% chance to respond
        2800, 5000 // 2.8-5 second intervals
    );
    
    public static final Schema ENDGAME_SCHEMA = new Schema(
        SchemaType.ENDGAME_WRAP,
        2, 4, // 2-4 exchanges
        new ResponseLength(35, 85, 0.2, "reflective"), // Initial: thoughtful 35-85 words
        new ResponseLength(18, 45, 0.3, "contemplative"), // Follow-up: 18-45 words
        new ConversationRhythm(1.0, 1.0, false, 0.25), // No acceleration, natural emotion
        0.9, // 90% chance to respond  
        3500, 6000 // 3.5-6 second intervals
    );
    
    /**
     * Get appropriate schema based on trigger type
     */
    public static Schema getSchemaForTrigger(String triggerType) {
        switch (triggerType.toLowerCase()) {
            case "opening":
            case "game_start":
                return OPENING_SCHEMA;
                
            case "brilliant_move":
            case "blunder": 
            case "tactical_shot":
                return QUICK_REACTION_SCHEMA;
                
            case "position_change":
            case "middlegame":
            case "evaluation_swing":
                return POSITION_ANALYSIS_SCHEMA;
                
            case "endgame":
            case "game_over":
            case "checkmate":
                return ENDGAME_SCHEMA;
                
            default:
                return QUICK_REACTION_SCHEMA; // Safe default
        }
    }
    
    /**
     * Should this statement trigger a response based on schema rules?
     */
    public static boolean shouldRespondToStatement(Schema schema, String statement, int currentTurns, 
                                                  double emotionalIntensity) {
        // Check for interruption conditions (urgent moves)
        if (schema.rhythm.allowInterruptions && isUrgentMove(statement)) {
            return currentTurns < schema.minTurns; // Only interrupt if we haven't met minimum
        }
        
        // Always allow minimum turns
        if (currentTurns < schema.minTurns) {
            return true;
        }
        
        // Stop at maximum turns
        if (currentTurns >= schema.maxTurns) {
            return false;
        }
        
        // Adjust response chance based on emotional intensity
        double adjustedChance = schema.responseChance;
        if (emotionalIntensity > 0.8) {
            adjustedChance = Math.min(1.0, adjustedChance * 1.3); // More likely to respond when emotional
        } else if (emotionalIntensity < 0.3) {
            adjustedChance *= 0.8; // Less likely when calm
        }
        
        // Use probability for middle range
        return Math.random() < adjustedChance;
    }
    
    /**
     * Legacy overload for backwards compatibility
     */
    public static boolean shouldRespondToStatement(Schema schema, String statement, int currentTurns) {
        return shouldRespondToStatement(schema, statement, currentTurns, 0.5);
    }
    
    /**
     * Check if a move/statement is urgent (requires immediate interruption)
     */
    private static boolean isUrgentMove(String statement) {
        String lower = statement.toLowerCase();
        return lower.contains("checkmate") || lower.contains("blunder") || 
               lower.contains("brillian") || lower.contains("sacrifice") ||
               lower.contains("time pressure") || lower.contains("flag");
    }
    
    /**
     * Get enhanced length guidance for current turn with natural flow
     */
    public static String getEnhancedLengthGuidance(Schema schema, int turnNumber, double emotionalIntensity) {
        ResponseLength responseLength = schema.getResponseLengthForTurn(turnNumber, emotionalIntensity);
        int targetWords = responseLength.getTargetWordCount();
        
        return String.format("Target exactly %d words in %s style. %s", 
            targetWords, responseLength.lengthStyle,
            getStyleInstructions(responseLength.lengthStyle, emotionalIntensity));
    }
    
    /**
     * Get style instructions based on length style and emotional intensity
     */
    private static String getStyleInstructions(String lengthStyle, double emotionalIntensity) {
        switch (lengthStyle) {
            case "terse":
                return emotionalIntensity > 0.7 ? "Sharp, intense delivery." : "Brief and direct.";
            case "conversational":
                return emotionalIntensity > 0.7 ? "Animated discussion." : "Natural, flowing response.";
            case "elaborate":
                return emotionalIntensity > 0.7 ? "Passionate analysis." : "Thoughtful explanation.";
            case "analytical":
                return emotionalIntensity > 0.7 ? "Intense scrutiny." : "Clear technical analysis.";
            case "reflective":
                return emotionalIntensity > 0.7 ? "Deep emotional insight." : "Contemplative reflection.";
            case "passionate":
                return "Emotionally charged response.";
            case "contemplative":
                return "Thoughtful, measured reflection.";
            default:
                return "Natural response.";
        }
    }
    
    /**
     * Legacy method for backwards compatibility
     */
    public static String getLengthGuidance(Schema schema, boolean isInitial) {
        if (isInitial) {
            return schema.initialLengthGuide;
        } else {
            return schema.responseLengthGuide;
        }
    }
    
    /**
     * Get conversation flow summary for debugging
     */
    public static String getFlowSummary(Schema schema) {
        return String.format("Schema %s: %d-%d turns, initial %d-%d words, followup %d-%d words, " +
                           "rhythm: accel=%.2f, emotion=%.2f, pauses=%.0f%%",
            schema.type,
            schema.minTurns, schema.maxTurns,
            schema.initialResponse.minWords, schema.initialResponse.maxWords,
            schema.followupResponse.minWords, schema.followupResponse.maxWords,
            schema.rhythm.accelerationFactor, schema.rhythm.emotionalIntensityMultiplier,
            schema.rhythm.pauseProbability * 100);
    }
}