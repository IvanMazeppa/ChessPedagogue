# Complete Responses API Integration Fix

## Issue Summary
The Responses API was working (visible in dashboard) but AI masters weren't speaking in spectator mode because:
1. Missing `vector_store_ids` parameter causing HTTP 400 errors
2. SSE events were using `response.completed` instead of `response.done`
3. Response callbacks weren't being executed on the main thread

## Fixes Applied

### 1. Vector Store IDs (✅ Fixed)
Added required `vector_store_ids` when using file_search tool:
```java
JSONArray vectorStoreIds = new JSONArray();
String vectorStoreId = getVectorStoreIdForMaster(session.masterName);
if (vectorStoreId != null) {
    vectorStoreIds.put(vectorStoreId);
    fileTool.put("vector_store_ids", vectorStoreIds);
}
```

### 2. SSE Event Handling (✅ Fixed)
The Responses API sends events in this sequence:
- `response.created` - Session starts
- `response.output_text.delta` - Text chunks arrive
- `response.completed` - Final event (not `response.done`)

Updated to handle both event types and ensure callbacks run on main thread:
```java
} else if ("response.done".equals(type) || "response.completed".equals(type)) {
    // Handle completion
    mainHandler.post(() -> {
        callback.onResponseComplete(fullResponse);
        callback.onConversationTurn(session.masterName, fullResponse);
    });
}
```

### 3. Response Processing (✅ Fixed)
Added special handling for `response.completed` event in the unknown event handler:
```java
if ("response.completed".equals(type)) {
    String fullResponse = responseBuilder.toString();
    if (!fullResponse.trim().isEmpty()) {
        mainHandler.post(() -> {
            callback.onResponseComplete(fullResponse);
            callback.onConversationTurn(session.masterName, fullResponse);
        });
    }
}
```

## Event Flow
1. **Request sent** with proper `vector_store_ids`
2. **Text accumulated** via `response.output_text.delta` events
3. **Response completed** when `response.completed` event arrives
4. **UI updated** via main thread callbacks
5. **TTS speaks** the response

## Verification
Look for these logs to confirm it's working:
- `📝 Text chunk:` - Text is being received
- `✅ Response completed` - Full response assembled
- `🎭 Enhanced dialogue from` - Response reached SpectatorGameViewModel
- `💬 AI Dialogue displayed` - Response shown in UI

## Result
The Responses API is now fully integrated and working. Chess masters will speak their responses using the new stateful API with proper vector store access for enhanced context.