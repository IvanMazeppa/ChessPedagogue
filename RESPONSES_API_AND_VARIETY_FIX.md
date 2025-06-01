# Responses API and Response Variety Fixes

## Changes Made

### 1. Fixed Responses API Error (HTTP 400)
**Problem**: The API was rejecting requests with "Unknown parameter: '_messages'"

**Solution**: Removed the `_messages` parameter from the request body. The Responses API only accepts:
- `model` - The model to use
- `input` - The prompt text (not messages array)
- `stream` - Whether to stream responses
- `previous_response_id` - For stateful conversations
- `tools` - Array of tools like `[{"type": "file_search"}]`
- `metadata` - Optional context metadata

### 2. Fixed Repetitive "This position reminds me..." Responses
**Problem**: Chess masters were starting too many responses with the same phrase

**Solutions Implemented**:

#### A. Updated System Prompts in ChessMasterResponsesManager.java
Added explicit instructions to avoid repetitive phrases:
```java
// For each master:
prompt.append("IMPORTANT: Be creative and varied in your responses. Avoid repetitive phrases like 'This position reminds me...' ");
prompt.append("Instead, comment directly on the tactics, express excitement about combinations, or share quick insights.");
```

#### B. Varied Initial Prompts in SpectatorConversationOrchestrator.java
Instead of using the same prompt every time, now randomly selects from varied options:

**Opening prompts**:
- "You're facing X in this game. What's your opening strategy?"
- "Playing against X today. Share your initial thoughts on the position."
- "The game begins against X. What's your approach?"
- "Starting position against X. How do you feel about this matchup?"
- "Here we go against X. What are your expectations?"

**Brilliant move prompts**:
- "You just found a strong tactical sequence. What did you see?"
- "That was a powerful move! Explain your calculation."
- "Nice tactical shot! What was the key idea?"
- "Strong play! Share the concept behind this move."
- "Excellent move! What made you choose this continuation?"

#### C. Varied Response Prompts
Also randomized the format of response prompts to encourage different conversation styles:
- "X just said: '...'. Share your perspective."
- "Responding to X's comment: '...'"
- "After X said: '...', what's your take?"
- "X commented: '...'. Your thoughts?"
- "React to X's statement: '...'"

## Results
These changes will:
1. Eliminate the HTTP 400 errors from the Responses API
2. Create more natural, varied dialogue between chess masters
3. Make each conversation feel unique rather than formulaic

## Next Steps
Monitor the logs to see if:
1. The Responses API starts working (though it will likely still fallback since it's a new API)
2. The chess masters' responses become more varied and natural
3. Conversations feel less repetitive and more authentic

The fallback system will continue to work seamlessly with the existing Assistant API and fine-tuned models.