package com.example.chesspedagogue.performance;

import android.util.Log;
import android.util.LruCache;
import com.example.chesspedagogue.GameDatabaseHelper;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-performance cache for master positions to reduce database queries
 */
public class PositionCache {
    private static final String TAG = "PositionCache";
    private static PositionCache instance;
    
    // LRU cache for recent positions (fast lookup)
    private final LruCache<String, List<GameDatabaseHelper.HistoricalPosition>> positionCache;
    
    // Bloom filter simulation for quick negative lookups
    private final ConcurrentHashMap<String, Boolean> knownPositions;
    
    // Cache statistics
    private int hits = 0;
    private int misses = 0;
    
    // Cache configuration
    private static final int MAX_CACHE_SIZE = 500; // positions
    private static final long CACHE_VALIDITY_MS = 30 * 60 * 1000; // 30 minutes
    
    private PositionCache() {
        // Initialize LRU cache with 10MB max size
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8; // Use 1/8 of available memory
        
        positionCache = new LruCache<String, List<GameDatabaseHelper.HistoricalPosition>>(cacheSize) {
            @Override
            protected int sizeOf(String key, List<GameDatabaseHelper.HistoricalPosition> positions) {
                // Rough estimate: 200 bytes per position
                return positions.size() * 200;
            }
        };
        
        knownPositions = new ConcurrentHashMap<>();
        Log.d(TAG, "✅ Position cache initialized with size: " + cacheSize + "KB");
    }
    
    public static synchronized PositionCache getInstance() {
        if (instance == null) {
            instance = new PositionCache();
        }
        return instance;
    }
    
    /**
     * Get positions from cache
     */
    public List<GameDatabaseHelper.HistoricalPosition> get(String fen) {
        List<GameDatabaseHelper.HistoricalPosition> positions = positionCache.get(fen);
        
        if (positions != null) {
            hits++;
            Log.d(TAG, "✅ Cache hit for FEN (hit rate: " + getHitRate() + "%)");
            return positions;
        } else {
            misses++;
            return null;
        }
    }
    
    /**
     * Store positions in cache
     */
    public void put(String fen, List<GameDatabaseHelper.HistoricalPosition> positions) {
        if (fen != null && positions != null) {
            positionCache.put(fen, positions);
            knownPositions.put(fen, true);
            Log.d(TAG, "📦 Cached " + positions.size() + " positions for FEN");
        }
    }
    
    /**
     * Check if position exists (fast negative lookup)
     */
    public boolean mightExist(String fen) {
        return knownPositions.containsKey(fen);
    }
    
    /**
     * Clear cache
     */
    public void clear() {
        positionCache.evictAll();
        knownPositions.clear();
        hits = 0;
        misses = 0;
        Log.d(TAG, "🗑️ Cache cleared");
    }
    
    /**
     * Get cache hit rate
     */
    public float getHitRate() {
        int total = hits + misses;
        if (total == 0) return 0;
        return (hits * 100f) / total;
    }
    
    /**
     * Log cache statistics
     */
    public void logStatistics() {
        Log.d(TAG, String.format("📊 Cache Stats: Hits=%d, Misses=%d, Hit Rate=%.1f%%, Size=%d",
            hits, misses, getHitRate(), positionCache.size()));
    }
}