# Responses API SSE Streaming Fix

## Issue
While the Responses API was successfully processing requests (visible in the dashboard), the AI masters were not speaking in spectator mode. The SSE (Server-Sent Events) were not being received or parsed correctly.

## Fixes Applied

### 1. Enhanced SSE Event Debugging
Added comprehensive logging to understand what events are being received:
- Log all SSE events with type, ID, and data length
- Log the raw data for inspection
- Log parsed JSON keys to understand the structure

### 2. Flexible Event Parsing
Since the Responses API might send events in different formats than expected:
- Check for `[DONE]` marker to identify completion
- Look for text content in multiple possible JSON keys: `content`, `text`, `output`, `message`, `response`
- Handle plain text responses if JSON parsing fails
- Track whether any content has been received

### 3. Automatic Fallback Mechanism
Added multiple fallback strategies:
- If no content is received after 5 seconds in onFailure
- If empty response is received in done event
- 8-second timeout handler that forces fallback if no SSE events arrive
- Fallback uses the proven Chat Completions/Assistant API

### 4. State Tracking
- Track `hasReceivedContent` to know if any actual text was received
- Track `startTime` to implement timeouts
- Made request body accessible in inner class for fallback

## Code Changes

### ResponseListener Class
```java
class ResponseListener extends EventSourceListener {
    boolean hasReceivedContent = false;
    private long startTime = System.currentTimeMillis();
    // ... other fields
}
```

### Timeout Handler
```java
mainHandler.postDelayed(() -> {
    if (!listener.hasReceivedContent && session.lastActivityTime < System.currentTimeMillis() - 8000) {
        Log.w(TAG, "⏰ Timeout: No response received from Responses API after 8 seconds, forcing fallback");
        eventSource.cancel();
        fallbackToChatCompletions(session, requestBody, callback);
    }
}, 8000); // 8 second timeout
```

## Expected Behavior
1. The system will attempt to use the Responses API
2. If SSE events are received properly, they will be processed and spoken
3. If no events arrive or parsing fails, the system will automatically fallback to the working Chat Completions/Assistant API
4. Users will see AI dialogue regardless of which API is used

## Debugging Tips
Look for these log messages:
- `🔔 SSE Event` - Shows all received events
- `📄 SSE Data` - Shows the raw event data
- `📋 Parsed JSON keys` - Shows what fields are in the JSON
- `📝 Text chunk` - Indicates text was successfully extracted
- `⏰ Timeout` - Indicates fallback was triggered by timeout
- `⚠️ Empty response` - Indicates fallback due to empty content

## Next Steps
1. Monitor logs to see what SSE events are actually being received
2. Adjust parsing logic based on the actual event format
3. Once the Responses API SSE format is confirmed, optimize the parsing code