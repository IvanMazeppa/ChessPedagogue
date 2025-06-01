# Emergent Behavior Guide for Chess Pedagogue

## Overview
This guide explains how the enhanced context management system enables emergent behavior, competitive banter, and nickname usage between AI chess masters.

## Problem Statement
The original AI dialogue system was losing contextual awareness, preventing natural emergent behaviors like:
- Masters using nicknames (e.g., Tal calling Fischer "Bobby")
- Competitive banter and inside jokes developing over time
- References to previous games or conversations
- Personality-driven reactions to game situations

## Solution Architecture

### 1. EnhancedContextManager
The core of the solution is the `EnhancedContextManager` class which maintains:
- **Persistent conversation history** across dialogue turns
- **Relationship memories** between masters
- **Nickname tracking** and usage patterns
- **Conversation summarization** for long games

### 2. Key Features

#### Conversation Context
```java
// Every API call now includes:
- System message with personality and relationship context
- Conversation summary (if conversation is long)
- Relationship memories between the two masters
- Recent conversation history (up to 20 messages)
```

#### Relationship Building
```java
// The system tracks memorable interactions:
- References to past games
- Accusations of luck
- Compliments on play
- Comments on playing style
- Nickname usage
```

#### Nickname System
Pre-configured nicknames that emerge naturally:
- Tal → Fischer: "Bobby"
- Fischer → Tal: "Misha"
- Kasparov → Karpov: "Tolya"
- Karpov → Kasparov: "Garry"

### 3. Implementation Details

#### API Calls with Context
All OpenAI API calls now use the enhanced context:
```java
// Get context for current speakers
List<Map<String, String>> context = contextManager.getContextForApiCall(speaker, opponent);

// Add current prompt
Map<String, String> prompt = new HashMap<>();
prompt.put("role", "user");
prompt.put("content", userMessage);
context.add(prompt);

// Make API call with full context
String response = openAIService.getChatCompletionWithEnhancedContext(context, modelId);
```

#### Context Structure
Each API call includes:
1. **System Message**: Personality traits, competitive instructions, nickname preferences
2. **Relationship Context**: Past memorable interactions between these masters
3. **Conversation Summary**: Key points from earlier in the game
4. **Recent Messages**: Last 20 exchanges with proper role mapping

### 4. Enabling Emergent Behavior

#### Temperature and Penalties
```java
requestBody.put("temperature", 0.8);      // Higher for more personality
requestBody.put("presence_penalty", 0.6); // Encourage variety
requestBody.put("frequency_penalty", 0.3); // Reduce repetition
```

#### Personality Preservation
- Context is never fully cleared during a game
- Relationship memories persist across games
- Nicknames and themes are reinforced in system messages

### 5. Testing and Verification

Use the `ContextTestActivity` to verify:
```java
// Run test to see:
- Context generation with proper role mapping
- Conversation summarization after 15 turns
- Relationship memory accumulation
- Nickname usage in generated responses
```

### 6. Best Practices

1. **Never Clear Context Mid-Game**: Always use `resetConversation()` only between games
2. **Track All Turns**: Every dialogue should be added to context manager
3. **Maintain Speaker Consistency**: Always track who said what
4. **Allow Natural Flow**: Don't force nicknames - let them emerge from context

### 7. Troubleshooting

If emergent behavior isn't appearing:
1. Check context size - should be 10-20 messages
2. Verify system message includes personality traits
3. Ensure relationship memories are being tracked
4. Confirm temperature is set to 0.8 or higher
5. Check that conversation history includes both speakers

### 8. Future Enhancements

- Track frequency of nickname usage
- Develop new nicknames based on game events
- Build longer-term memory across multiple games
- Add emotional state tracking to relationship memories

## Code Examples

### Adding a Conversation Turn
```java
contextManager.addConversationTurn(speaker, message, context);
```

### Getting Context for API Call
```java
List<Map<String, String>> context = contextManager.getContextForApiCall(currentSpeaker, opponent);
```

### Resetting Between Games
```java
contextManager.resetConversation(); // Clears current game but preserves relationships
```

## Conclusion
The enhanced context management system restores the emergent behaviors that made the AI chess masters feel alive and competitive. By maintaining conversation history, tracking relationships, and providing rich context to every API call, the system enables natural, personality-driven interactions that evolve over time.