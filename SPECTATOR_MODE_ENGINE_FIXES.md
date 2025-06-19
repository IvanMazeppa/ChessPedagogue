# Spectator Mode Engine Fixes - Chess Engine & TTS Issues

## Issues Identified:

### 1. ❌ Chess Engine Failure: "Too many consecutive move failures"
**Root Cause**: Stockfish engine fails to generate moves after several attempts, causing spectator game to end prematurely.

**Error Location**: `AIvsAIGameManager.java:741`

**Failure Chain**:
1. Personality engine timeout → Move validation failure → Stockfish fallback failure
2. After 3 consecutive failures → Game ends with error

### 2. 🔇 No AI Speech (TTS Issues)
**Symptoms**: Text dialogue appears but no audio speech from masters
**Logs Show**: TTS commands issued but no actual speech output

## Fixes Applied:

### 🔧 Chess Engine Reliability Improvements

#### 1. **Increased Failure Tolerance**
```java
// Before: MAX_CONSECUTIVE_FAILURES = 3
// After: MAX_CONSECUTIVE_FAILURES = 8  
```
- Gives engine more chances to recover from temporary issues
- Reduces premature game endings

#### 2. **Added Engine Recovery System**
```java
private boolean attemptEngineRecovery() {
    // Step 1: Re-synchronize game state
    // Step 2: Test engine responsiveness  
    // Step 3: Restart engine if needed
    // Step 4: Re-synchronize after restart
}
```
- Automatically attempts engine recovery before giving up
- Resets failure counter after successful recovery
- Tries multiple recovery strategies

#### 3. **Enhanced Error Handling**
```java
if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
    if (attemptEngineRecovery()) {
        consecutiveFailures = 0; // Reset counter
        requestMove(); // Retry
        return;
    }
    // Only end game if recovery fails
}
```

### 🎤 TTS (Speech) Issue Diagnosis

**Possible Causes**:
1. **ElevenLabs API Issues**: Service might be down or rate-limited
2. **Audio Output Problems**: Device audio settings or permissions
3. **TTS Service Conflicts**: Multiple TTS services interfering
4. **Network Connectivity**: API calls failing silently

**Logs Show TTS Commands Issued**:
```
TTSServiceManager: 🎭 Speaking with specific master: kasparov
TTSServiceManager: 🎭 Setting emotional state for kasparov: analytical
```

**But Missing**: Actual speech completion/error logs

## Testing Recommendations:

### 1. **Chess Engine Test**
- Run spectator mode with the fixes
- Monitor logs for engine recovery attempts
- Should see: `🔧 Attempting engine recovery...` if issues occur
- Games should continue longer before any failures

### 2. **TTS Debugging**
Add to SpectatorGameActivity to test TTS:
```java
// Test TTS directly
TTSServiceManager.getElevenLabsService(this).speak("Test speech", "male", emotion -> {
    Log.d(TAG, "TTS Test completed");
}, error -> {
    Log.e(TAG, "TTS Test failed: " + error);
});
```

### 3. **Network Check**
- Verify internet connection for API calls
- Check if ElevenLabs service is accessible
- Monitor API response logs

## Expected Results:

### ✅ **Chess Engine**
- Spectator games should run much longer without engine failures
- When failures occur, system attempts automatic recovery
- Better logging of failure reasons and recovery attempts

### 🎤 **TTS Speech**
- May need additional debugging to identify root cause
- Could be API, network, or device audio issue
- Text dialogue will continue working regardless

## Quick TTS Test:
1. Check device volume and audio output
2. Test other apps with audio to confirm device audio works
3. Check if internet connection is stable
4. Look for any TTS error messages in logs during speech attempts

The chess engine fixes should resolve the "Too many consecutive move failures" issue. For TTS, we may need to see specific error logs to diagnose the speech problem.