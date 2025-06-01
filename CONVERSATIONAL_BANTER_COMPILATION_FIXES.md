# Conversational Banter Compilation Fixes

## Fixed Compilation Errors

### 1. TextToSpeechService Interface Issues
**Error**: `cannot find symbol: class OnSpeechCompletedListener`
**Fix**: Changed from using `TextToSpeechService` interface to using `OpenAITTSService` directly, which can be a wrapper around ElevenLabs.

```java
// Before:
private final TextToSpeechService ttsService;
ttsService.speak(dialogue, new TextToSpeechService.OnSpeechCompletedListener() {...});

// After:
private final OpenAITTSService ttsService;
ttsService.speak(dialogue, new OpenAITTSService.OnSpeechCompletedListener() {...});
```

### 2. Missing getCurrentEvaluation Method
**Error**: `cannot find symbol: method getCurrentEvaluation()`
**Fix**: Added the method to EvaluationTracker.java:

```java
/**
 * Get the current evaluation (most recent)
 * @return The current evaluation, or null if no evaluations tracked
 */
public Float getCurrentEvaluation() {
    if (evaluationHistory.isEmpty()) {
        return null;
    }
    
    // Get the most recent evaluation
    EvaluationSnapshot current = evaluationHistory.get(evaluationHistory.size() - 1);
    return current.getEffectiveEvaluation();
}
```

### 3. TTSServiceManager Return Type
**Error**: `incompatible types: OpenAITTSService cannot be converted to TextToSpeechService`
**Fix**: Changed return type from `TextToSpeechService` to `OpenAITTSService`:

```java
// Before:
public TextToSpeechService getPreferredTTSService() {
    return getOpenAITTSService(context);
}

// After:
public OpenAITTSService getPreferredTTSService() {
    return getOpenAITTSService(context);
}
```

### 4. ElevenLabs Listener Adaptation
**Fix**: When using ElevenLabs, properly cast and use ElevenLabs-specific listener:

```java
if (ttsService instanceof ElevenLabsTTSService) {
    ((ElevenLabsTTSService) ttsService).speak(dialogue, 
        new ElevenLabsTTSService.OnSpeechCompletedListener() {
            @Override
            public void onSpeechCompleted() {
                // Restore original master
                prefs.edit().putString("selected_master", currentMaster).apply();
            }
        });
}
```

## Summary of Changes

1. **SpectatorConversationOrchestrator.java**:
   - Changed TTS service type from interface to concrete OpenAITTSService
   - Updated all listener types to use concrete class listeners
   - Fixed evaluation getter calls to use new getCurrentEvaluation() method
   - Removed unnecessary helper method getCurrentEvaluationFromTracker()

2. **EvaluationTracker.java**:
   - Added getCurrentEvaluation() method to get the most recent evaluation

3. **TTSServiceManager.java**:
   - Changed getPreferredTTSService() return type to OpenAITTSService

## Testing
After these fixes, the code should compile successfully. The conversational banter system will:
- Use ElevenLabs TTS when available (with emotional voice modulation)
- Fall back to OpenAI TTS when ElevenLabs is not configured
- Properly track evaluation changes for emotional context
- Generate varied, personality-specific dialogue