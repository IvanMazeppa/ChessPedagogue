# Responses API Vector Store Fix

## Issue
The Responses API was returning HTTP 400 error with the message:
```
Missing required parameter: 'tools[0].vector_store_ids'
```

## Root Cause
When using the `file_search` tool in the Responses API, the `vector_store_ids` parameter is **required**. The previous implementation was only specifying the tool type without providing the vector store IDs.

## Solution
Updated `ChessMasterResponsesManager.java` to include `vector_store_ids` when using the file search tool:

### 1. Added Vector Store IDs Method
```java
private String getVectorStoreIdForMaster(String masterName) {
    switch (masterName.toLowerCase()) {
        case "tal":
            return "vs_5c3c6db00ed48c09a56ee0c45d6b5fb8";
        case "fischer":
            return "vs_6e96708b0ad849b8bd3fd7bfb977f15f";
        case "carlsen":
            return "vs_68365028eb988191b09d8d50e6f11b5d";
        default:
            return null;
    }
}
```

### 2. Updated Tool Configuration
```java
// Add tools if needed (file_search for vector stores)
if (hasVectorStore(session.masterName)) {
    JSONArray tools = new JSONArray();
    JSONObject fileTool = new JSONObject();
    fileTool.put("type", "file_search");
    
    // Add vector_store_ids as required by the API
    JSONArray vectorStoreIds = new JSONArray();
    String vectorStoreId = getVectorStoreIdForMaster(session.masterName);
    if (vectorStoreId != null) {
        vectorStoreIds.put(vectorStoreId);
        fileTool.put("vector_store_ids", vectorStoreIds);
    }
    
    tools.put(fileTool);
    requestBody.put("tools", tools);
}
```

## Key Points from Research

1. **Required Parameter**: The `vector_store_ids` is a required parameter when using the `file_search` tool in the Responses API

2. **Array Format**: Although the parameter accepts an array, current limitation allows only one vector store ID per request

3. **Correct Request Format**:
```json
{
    "model": "gpt-4o-mini",
    "input": "Your prompt here",
    "tools": [{
        "type": "file_search",
        "vector_store_ids": ["vs_xxxxxx"]
    }]
}
```

## Result
With these changes, the Responses API should no longer return the "Missing required parameter" error. The system will continue to use the fallback mechanism to Assistant API/Chat Completions until the Responses API is fully available, but the request format is now correct.

## Next Steps
- Monitor logs to confirm the HTTP 400 error is resolved
- The system may still fall back to other APIs if the Responses API returns 404 (not found)
- Once the Responses API is fully available, the chess masters will automatically use it for enhanced stateful conversations