# Responses API Integration - Complete Documentation

## Overview
Successfully integrated OpenAI's Responses API (released March 2025) into the Chess Pedagogue spectator mode. The integration enables stateful conversations between AI chess masters with enhanced context management and vector store access.

## Integration Status: ✅ COMPLETE

### Working Features
- ✅ Responses API request format corrected
- ✅ Vector store IDs properly configured
- ✅ SSE event streaming functioning
- ✅ AI masters speaking responses
- ✅ Fallback mechanisms operational
- ✅ Response variety improved

## Technical Implementation

### 1. API Request Format
The Responses API uses a different format than Chat Completions:

```java
// Correct format for Responses API
JSONObject requestBody = new JSONObject();
requestBody.put("model", getModelForMaster(session.masterName));
requestBody.put("input", combinedInput);  // NOT "messages" - use "input"
requestBody.put("stream", true);

// Stateful conversation tracking
if (session.previousResponseId != null) {
    requestBody.put("previous_response_id", session.previousResponseId);
}
```

### 2. Vector Store Configuration
Each chess master with a vector store requires explicit ID specification:

```java
private String getVectorStoreIdForMaster(String masterName) {
    switch (masterName.toLowerCase()) {
        case "tal":
            return "vs_5c3c6db00ed48c09a56ee0c45d6b5fb8";
        case "fischer":
            return null; // Vector store not found - works without file_search
        case "carlsen":
            return "vs_68365028eb988191b09d8d50e6f11b5d";
        default:
            return null;
    }
}
```

### 3. SSE Event Handling
The Responses API sends events in a specific sequence:

```java
// Event sequence:
// 1. response.created - Session initialized
// 2. response.output_text.delta - Text chunks
// 3. response.completed - Final event (NOT response.done)

} else if ("response.done".equals(type) || "response.completed".equals(type)) {
    mainHandler.post(() -> {
        callback.onResponseComplete(fullResponse);
        callback.onConversationTurn(session.masterName, fullResponse);
    });
}
```

### 4. Enhanced Response Variety
Eliminated repetitive "This position reminds me..." patterns:

```java
// System prompts now include:
prompt.append("IMPORTANT: Be creative and varied in your responses. ");
prompt.append("Avoid repetitive phrases like 'This position reminds me...' ");
prompt.append("Instead, comment directly on the tactics, express excitement about combinations, or share quick insights.");
```

## Model and Voice Configuration

### Fine-tuned Models
- **Tal**: `ft:gpt-4o-2024-08-06:personal:tal:BbDcbXJT`
- **Fischer**: `ft:gpt-4o-2024-08-06:personal:fischer:BbWNySl4`
- **Carlsen**: `ft:gpt-4.1-mini-2025-04-14:personal:carlsen:Bbxb6sUe`

### Assistant IDs
- **Tal**: `asst_LSdhMRFJcSCUJjR4o2B9tWmg`
- **Fischer**: `asst_2j5uMiqmEKRUNqHCtXdsaoY3`
- **Carlsen**: `asst_TTzxbfvJQz3e80FetQblJ0Gl`

### ElevenLabs Voice IDs
- **Carlsen**: Updated to `ygiXC2Oa1BiHksD3WkJZ` (Norwegian accent)

## Error Handling and Fallbacks

### Multi-layer Fallback System
1. **Primary**: Responses API with streaming
2. **Secondary**: Assistant API (for Tal, Fischer, Carlsen)
3. **Tertiary**: Chat Completions API

### Automatic Fallback Triggers
- HTTP 404/400 errors
- No SSE events received after 8 seconds
- Empty response content
- SSE streaming failures

## Debugging and Monitoring

### Key Log Messages
- `📋 Responses API Request` - Shows full request JSON
- `🔔 SSE Event` - Indicates events are being received
- `📝 Text chunk` - Text is being accumulated
- `✅ Response completed` - Full response ready
- `🎭 Enhanced dialogue` - Response reached ViewModel
- `💬 AI Dialogue displayed` - UI updated

### Common Issues and Solutions

#### Issue: "Missing required parameter: 'tools[0].vector_store_ids'"
**Solution**: Added vector_store_ids array when using file_search tool

#### Issue: AI not speaking despite API responses
**Solution**: Fixed SSE event handling for "response.completed" event

#### Issue: Repetitive responses
**Solution**: Enhanced system prompts and varied conversation starters

## Current Architecture Flow

1. **SpectatorConversationOrchestrator** initiates conversation
2. **ChessMasterResponsesManager** creates response session
3. **Responses API** processes request with context
4. **SSE events** stream response chunks
5. **Callbacks** update UI on main thread
6. **ElevenLabsTTSService** speaks the response

## Testing the Integration

### Verify Responses API is Working
1. Start spectator game (Magnus vs Bobby)
2. Check logs for `📋 Responses API Request`
3. Look for `🔔 SSE Event` entries
4. Confirm `✅ Response completed` appears
5. Verify masters are speaking

### Dashboard Verification
- Check OpenAI dashboard for Responses API usage
- Confirm requests are being logged
- Verify vector store access (when available)

## Future Enhancements

### Potential Improvements
1. Update Fischer's vector store ID when available
2. Add more chess masters to Responses API
3. Implement conversation memory across games
4. Enhanced emotional response triggers

### Performance Optimizations
1. Cache response sessions for faster subsequent calls
2. Implement connection pooling for SSE
3. Optimize fallback decision timing

## Summary

The Responses API integration is complete and operational. The system successfully:
- Formats requests correctly for the new API
- Handles SSE streaming events properly
- Provides intelligent fallbacks for reliability
- Delivers varied and engaging AI responses
- Maintains backward compatibility with existing features

Chess masters now engage in more natural, contextual conversations with improved response variety and proper voice synthesis.