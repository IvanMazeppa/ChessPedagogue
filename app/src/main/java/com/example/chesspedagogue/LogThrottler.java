package com.example.chesspedagogue;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Smart logging throttler to reduce log verbosity and prevent spam
 * 
 * Features:
 * - Rate limiting for repetitive messages
 * - Configurable verbosity levels
 * - Message deduplication
 * - Performance-optimized with minimal overhead
 * 
 * Usage:
 * LogThrottler.d(TAG, "message"); // Instead of Log.d(TAG, "message");
 */
public class LogThrottler {
    
    // Global verbosity level
    public enum VerbosityLevel {
        SILENT,    // No debug logs
        MINIMAL,   // Only errors and important info
        NORMAL,    // Standard logging
        VERBOSE,   // All logs including debug
        ULTRA      // Everything including internal throttling logs
    }
    
    private static VerbosityLevel currentLevel = VerbosityLevel.NORMAL;
    private static final long DEFAULT_THROTTLE_MS = 1000; // 1 second
    private static final int MAX_BURST_COUNT = 3; // Allow 3 quick messages before throttling
    
    // Message tracking for throttling
    private static final Map<String, MessageTracker> messageHistory = new HashMap<>();
    private static final Object lock = new Object();
    
    // Throttling rules for different message patterns
    private static final Map<String, Long> throttleRules = new HashMap<>();
    
    static {
        // Configure throttling rules based on message patterns
        throttleRules.put("🎭", 2000L);  // Emotional/personality logs - 2 seconds
        throttleRules.put("🎯", 1500L);  // Analysis logs - 1.5 seconds  
        throttleRules.put("🔍", 1000L);  // Debug analysis - 1 second
        throttleRules.put("✅", 500L);   // Success logs - 0.5 seconds
        throttleRules.put("🔄", 1000L);  // State change logs - 1 second
        throttleRules.put("🛑", 2000L);  // Throttling logs - 2 seconds
        throttleRules.put("SSE", 3000L); // Server-sent events - 3 seconds
        throttleRules.put("PERSONALITY MOVE", 2000L); // Move analysis - 2 seconds
        throttleRules.put("evaluation", 1000L); // Evaluation logs - 1 second
    }
    
    private static class MessageTracker {
        long lastLogTime = 0;
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger suppressedCount = new AtomicInteger(0);
        String lastMessage = "";
        
        MessageTracker(String message) {
            this.lastMessage = message;
            this.lastLogTime = System.currentTimeMillis();
            this.count.set(1);
        }
    }
    
    /**
     * Set the global verbosity level
     */
    public static void setVerbosityLevel(VerbosityLevel level) {
        currentLevel = level;
        if (level == VerbosityLevel.ULTRA) {
            Log.i("LogThrottler", "📊 Verbosity level set to: " + level);
            Log.i("LogThrottler", "📊 Active throttle rules: " + throttleRules.size());
        }
    }
    
    /**
     * Get current verbosity level
     */
    public static VerbosityLevel getVerbosityLevel() {
        return currentLevel;
    }
    
    /**
     * Debug logging with smart throttling
     */
    public static void d(String tag, String message) {
        if (currentLevel.ordinal() < VerbosityLevel.NORMAL.ordinal()) {
            return; // Skip debug logs in minimal/silent mode
        }
        
        if (shouldThrottle(tag, message, Log.DEBUG)) {
            return; // Message was throttled
        }
        
        Log.d(tag, message);
    }
    
    /**
     * Info logging with minimal throttling
     */
    public static void i(String tag, String message) {
        if (currentLevel == VerbosityLevel.SILENT) {
            return;
        }
        
        if (shouldThrottle(tag, message, Log.INFO)) {
            return; // Message was throttled
        }
        
        Log.i(tag, message);
    }
    
    /**
     * Warning logging - rarely throttled
     */
    public static void w(String tag, String message) {
        if (currentLevel == VerbosityLevel.SILENT) {
            return;
        }
        
        // Only throttle warnings if they're very repetitive
        if (shouldThrottle(tag, message, Log.WARN, 5000L)) {
            return;
        }
        
        Log.w(tag, message);
    }
    
    /**
     * Error logging - never throttled
     */
    public static void e(String tag, String message) {
        Log.e(tag, message);
    }
    
    /**
     * Error logging with throwable - never throttled
     */
    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
    
    /**
     * Verbose logging - only in VERBOSE or ULTRA mode
     */
    public static void v(String tag, String message) {
        if (currentLevel.ordinal() < VerbosityLevel.VERBOSE.ordinal()) {
            return;
        }
        
        if (shouldThrottle(tag, message, Log.VERBOSE)) {
            return;
        }
        
        Log.v(tag, message);
    }
    
    /**
     * Force log a message regardless of throttling (use sparingly)
     */
    public static void force(String tag, String message) {
        Log.d(tag, message);
    }
    
    /**
     * Determine if a message should be throttled
     */
    private static boolean shouldThrottle(String tag, String message, int logLevel) {
        return shouldThrottle(tag, message, logLevel, getThrottleTime(message));
    }
    
    private static boolean shouldThrottle(String tag, String message, int logLevel, long throttleTimeMs) {
        // Never throttle errors
        if (logLevel == Log.ERROR) {
            return false;
        }
        
        String key = tag + ":" + getMessagePattern(message);
        long currentTime = System.currentTimeMillis();
        
        synchronized (lock) {
            MessageTracker tracker = messageHistory.get(key);
            
            if (tracker == null) {
                // First time seeing this message
                messageHistory.put(key, new MessageTracker(message));
                return false;
            }
            
            long timeSinceLastLog = currentTime - tracker.lastLogTime;
            int currentCount = tracker.count.incrementAndGet();
            
            // Allow burst of messages before throttling kicks in
            if (currentCount <= MAX_BURST_COUNT) {
                tracker.lastLogTime = currentTime;
                tracker.lastMessage = message;
                return false;
            }
            
            // Check if enough time has passed for throttling
            if (timeSinceLastLog < throttleTimeMs) {
                // Throttle this message
                tracker.suppressedCount.incrementAndGet();
                
                // Log throttling info in ULTRA mode
                if (currentLevel == VerbosityLevel.ULTRA && tracker.suppressedCount.get() % 10 == 1) {
                    Log.v("LogThrottler", "🛑 Throttled " + tracker.suppressedCount.get() + 
                         " similar messages for: " + key);
                }
                return true;
            }
            
            // Enough time has passed, allow the message
            tracker.lastLogTime = currentTime;
            tracker.lastMessage = message;
            
            // Reset burst counter after successful log
            tracker.count.set(1);
            
            // Log summary of suppressed messages if any
            int suppressed = tracker.suppressedCount.getAndSet(0);
            if (suppressed > 0 && currentLevel.ordinal() >= VerbosityLevel.VERBOSE.ordinal()) {
                Log.d(tag, "📊 [+" + suppressed + " similar] " + message);
                return true; // Don't log the original message since we logged the summary
            }
            
            return false;
        }
    }
    
    /**
     * Get the throttle time for a message based on content patterns
     */
    private static long getThrottleTime(String message) {
        // Check for emoji patterns first (most specific)
        for (Map.Entry<String, Long> rule : throttleRules.entrySet()) {
            if (message.contains(rule.getKey())) {
                return rule.getValue();
            }
        }
        
        return DEFAULT_THROTTLE_MS;
    }
    
    /**
     * Extract a pattern from the message for grouping similar messages
     */
    private static String getMessagePattern(String message) {
        if (message == null) return "";
        
        // Remove dynamic content like timestamps, IDs, numbers
        String pattern = message
            .replaceAll("\\d+", "#")  // Replace numbers with #
            .replaceAll("\\b[a-f0-9]{8,}\\b", "ID") // Replace hex IDs
            .replaceAll("\\d+\\.\\d+", "#.#"); // Replace decimals
        
        // Truncate very long messages to focus on the pattern
        if (pattern.length() > 100) {
            pattern = pattern.substring(0, 100) + "...";
        }
        
        return pattern;
    }
    
    /**
     * Clear throttling history (useful for testing or memory management)
     */
    public static void clearHistory() {
        synchronized (lock) {
            messageHistory.clear();
        }
        if (currentLevel == VerbosityLevel.ULTRA) {
            Log.i("LogThrottler", "🧹 Cleared throttling history");
        }
    }
    
    /**
     * Get throttling statistics
     */
    public static void printStats() {
        if (currentLevel.ordinal() < VerbosityLevel.VERBOSE.ordinal()) {
            return;
        }
        
        synchronized (lock) {
            int totalTracked = messageHistory.size();
            int totalSuppressed = messageHistory.values().stream()
                .mapToInt(t -> t.suppressedCount.get()).sum();
            
            Log.i("LogThrottler", "📊 Throttling Stats:");
            Log.i("LogThrottler", "   Tracked patterns: " + totalTracked);
            Log.i("LogThrottler", "   Total suppressed: " + totalSuppressed);
            Log.i("LogThrottler", "   Current level: " + currentLevel);
        }
    }
}