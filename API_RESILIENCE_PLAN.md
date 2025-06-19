# API Resilience Plan

## Circuit Breaker Pattern

### OpenAI API Protection
```java
public class OpenAICircuitBreaker {
    private int failureCount = 0;
    private long lastFailureTime = 0;
    private final int threshold = 5;
    private final long timeout = 60000; // 1 minute
    
    private enum State { CLOSED, OPEN, HALF_OPEN }
    private State state = State.CLOSED;
    
    public boolean canExecute() {
        switch (state) {
            case CLOSED:
                return true;
            case OPEN:
                if (System.currentTimeMillis() - lastFailureTime > timeout) {
                    state = State.HALF_OPEN;
                    return true;
                }
                return false;
            case HALF_OPEN:
                return true;
            default:
                return false;
        }
    }
    
    public void recordSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }
    
    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        
        if (failureCount >= threshold) {
            state = State.OPEN;
        }
    }
}
```

### Retry with Exponential Backoff
```java
public class ApiRetryManager {
    public <T> T executeWithRetry(Callable<T> apiCall, int maxRetries) {
        int attempts = 0;
        long waitTime = 1000; // Start with 1 second
        
        while (attempts < maxRetries) {
            try {
                return apiCall.call();
            } catch (Exception e) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw new RuntimeException("API call failed after " + maxRetries + " attempts", e);
                }
                
                try {
                    Thread.sleep(waitTime);
                    waitTime *= 2; // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        return null;
    }
}
```

### Fallback Responses
```java
public class ResponseFallbackManager {
    private final Map<String, List<String>> fallbackResponses = new HashMap<>();
    
    public ResponseFallbackManager() {
        initializeFallbacks();
    }
    
    private void initializeFallbacks() {
        fallbackResponses.put("tal", Arrays.asList(
            "Ah, a fascinating position! Let me think about this carefully.",
            "This reminds me of some beautiful combinations I've seen before.",
            "There's definitely something tactical brewing here!"
        ));
        
        fallbackResponses.put("carlsen", Arrays.asList(
            "This is an interesting position. Let me calculate the best continuation.",
            "I need to evaluate this position more carefully.",
            "There are several candidate moves here worth considering."
        ));
    }
    
    public String getFallbackResponse(String masterName, String context) {
        List<String> responses = fallbackResponses.get(masterName.toLowerCase());
        if (responses != null && !responses.isEmpty()) {
            return responses.get(new Random().nextInt(responses.size()));
        }
        return "Let me think about this position...";
    }
}
```

## Rate Limiting Protection

### API Quota Manager
```java
public class ApiQuotaManager {
    private final Map<String, ApiQuota> quotas = new HashMap<>();
    
    public class ApiQuota {
        private int remaining;
        private long resetTime;
        private final int limit;
        
        public ApiQuota(int limit) {
            this.limit = limit;
            this.remaining = limit;
            this.resetTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
        }
        
        public boolean canMakeRequest() {
            if (System.currentTimeMillis() > resetTime) {
                remaining = limit;
                resetTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
            }
            
            if (remaining > 0) {
                remaining--;
                return true;
            }
            return false;
        }
    }
    
    public boolean checkQuota(String apiType) {
        ApiQuota quota = quotas.get(apiType);
        if (quota == null) {
            // Initialize quota based on API type
            switch (apiType) {
                case "openai":
                    quota = new ApiQuota(1000); // Example limit
                    break;
                case "groq":
                    quota = new ApiQuota(500);
                    break;
                case "elevenlabs":
                    quota = new ApiQuota(200);
                    break;
                default:
                    quota = new ApiQuota(100);
            }
            quotas.put(apiType, quota);
        }
        
        return quota.canMakeRequest();
    }
}
```