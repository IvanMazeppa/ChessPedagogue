# Responses API Integration Guide

This document describes the integration of OpenAI's Responses API into the Chess Pedagogue app for enhanced AI dialogue and streaming responses.

## Overview

The Responses API integration provides:
- **Streaming responses** for lower latency conversations
- **Multi-turn dialogue management** with context awareness
- **Assistant-based responses** for Tal, Fischer, and Carlsen
- **Backward compatibility** with existing Chat Completions
- **Gradual migration path** from Chat Completions to Responses API

## Architecture

### New Components

1. **ChessMasterResponsesManager**
   - Manages Responses API sessions
   - Handles streaming SSE responses
   - Integrates with existing assistant configurations

2. **SpectatorConversationOrchestrator**
   - Orchestrates multi-turn conversations
   - Manages emotional responses based on game events
   - Controls conversation flow and timing

3. **ResponsesAPIIntegrationHelper**
   - Provides migration utilities
   - Manages fallback to Chat Completions
   - Tracks which masters use which API

4. **AIDialogueManagerV2** (Example)
   - Shows how to extend existing dialogue manager
   - Demonstrates backward compatibility
   - Uses Responses API when available

## Integration Steps

### 1. Enable Responses API

```java
// In your activity or initialization code
ResponsesAPIIntegrationHelper helper = ResponsesAPIIntegrationHelper.getInstance(context);
helper.setUseResponsesAPI(true);

// Enable for specific masters
helper.enableResponsesAPIForMaster("tal", true);
helper.enableResponsesAPIForMaster("fischer", true);
helper.enableResponsesAPIForMaster("carlsen", true);
```

### 2. Update AIDialogueManager

Replace AIDialogueManager with AIDialogueManagerV2 in SpectatorGameActivity:

```java
// Old
private AIDialogueManager dialogueManager;

// New
private AIDialogueManagerV2 dialogueManager;

// In onCreate or initialization
dialogueManager = new AIDialogueManagerV2(this);
```

### 3. Update Dependencies

Add SSE support to `app/build.gradle`:

```gradle
implementation 'com.squareup.okhttp3:okhttp-sse:4.10.0'
```

## Usage Examples

### Basic Response Generation

```java
ChessMasterResponsesManager manager = ChessMasterResponsesManager.getInstance(context);

// Create session and get response
manager.createResponseSession("tal", "game_context", new ChessMasterResponsesManager.ResponseCallback() {
    @Override
    public void onResponseStart(String sessionId) {
        // Session started
    }
    
    @Override
    public void onResponseChunk(String chunk, boolean isFirst) {
        // Handle streaming chunks for real-time display
        updateUI(chunk);
    }
    
    @Override
    public void onResponseComplete(String fullResponse) {
        // Full response received
        processResponse(fullResponse);
    }
    
    @Override
    public void onError(String error) {
        // Handle errors
    }
});

// Send message
manager.sendMessage(sessionId, "Your opponent played e4", "opening", callback);
```

### Spectator Conversations

```java
SpectatorConversationOrchestrator orchestrator = SpectatorConversationOrchestrator.getInstance(context);

// Start a conversation based on game event
orchestrator.startConversation("brilliant_move", "tal", "fischer", 
    "After 23.Rxf7!", new ConversationCallback() {
        @Override
        public void onDialogueGenerated(String speaker, String dialogue) {
            // Display dialogue
        }
        
        @Override
        public void onEmotionalResponse(String speaker, String emotion, String dialogue) {
            // Handle emotional responses
        }
    });
```

## Migration Strategy

### Phase 1: Testing (Current)
- Enable for Tal, Fischer, and Carlsen only
- Monitor performance and response quality
- Collect user feedback

### Phase 2: Gradual Rollout
- Enable for more masters as assistants are created
- Add streaming TTS for ultra-low latency
- Implement conversation memory

### Phase 3: Full Migration
- All masters use Responses API
- Remove Chat Completions fallback
- Optimize for streaming performance

## Configuration

### Assistant IDs (Hardcoded)
- Tal: `asst_LSdhMRFJcSCUJjR4o2B9tWmg`
- Fischer: `asst_2j5uMiqmEKRUNqHCtXdsaoY3`
- Carlsen: `asst_TTzxbfvJQz3e80FetQblJ0Gl`

### API Features
- Streaming: Enabled by default
- Max conversation turns: 6
- Response delay: 2-4 seconds between turns
- Session timeout: 30 minutes

## Testing

Use the ResponsesAPITestActivity to test the integration:

```java
Intent intent = new Intent(this, ResponsesAPITestActivity.class);
startActivity(intent);
```

## Troubleshooting

### Common Issues

1. **SSE Connection Errors**
   - Check internet connection
   - Verify API key is set
   - Ensure okhttp-sse dependency is added

2. **Fallback to Chat Completions**
   - Check logs for error messages
   - Verify assistant IDs are correct
   - Ensure master has assistant configured

3. **Response Quality**
   - Review assistant instructions
   - Check vector store content
   - Adjust temperature and variety parameters

### Debug Logging

Enable verbose logging:
```java
// In AIDialogueManagerV2 or ResponsesAPIIntegrationHelper
Log.d(TAG, integrationHelper.getMigrationStatus().toString());
```

## Future Enhancements

1. **Streaming TTS Integration**
   - Start speaking while response is still streaming
   - Reduce perceived latency

2. **Conversation Memory**
   - Maintain context across games
   - Reference previous conversations

3. **Enhanced Emotional Responses**
   - More nuanced emotional triggers
   - Master-specific emotional patterns

4. **Multi-language Support**
   - Responses in different languages
   - Cultural adaptation for international masters

## API Reference

See the source code documentation in:
- `ChessMasterResponsesManager.java`
- `SpectatorConversationOrchestrator.java`
- `ResponsesAPIIntegrationHelper.java`
- `AIDialogueManagerV2.java`