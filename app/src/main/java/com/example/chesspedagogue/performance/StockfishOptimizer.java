package com.example.chesspedagogue.performance;

import android.util.Log;
import com.example.chesspedagogue.StockfishManager;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Optimizes Stockfish engine communication for better performance
 */
public class StockfishOptimizer {
    private static final String TAG = "StockfishOptimizer";
    
    // Batching configuration
    private static final int BATCH_SIZE = 3;
    private static final long BATCH_TIMEOUT_MS = 100;
    
    // Evaluation cache
    private final ConcurrentHashMap<String, CachedEvaluation> evaluationCache;
    private static final long EVAL_CACHE_DURATION_MS = 5000; // 5 seconds
    
    // Request queue for batching
    private final BlockingQueue<EvaluationRequest> requestQueue;
    private final ScheduledExecutorService batchProcessor;
    
    // Performance tracking
    private final AtomicInteger cacheSaves = new AtomicInteger(0);
    private final AtomicInteger batchedRequests = new AtomicInteger(0);
    
    private static StockfishOptimizer instance;
    private final StockfishManager stockfish;
    
    private StockfishOptimizer(StockfishManager stockfish) {
        this.stockfish = stockfish;
        this.evaluationCache = new ConcurrentHashMap<>();
        this.requestQueue = new LinkedBlockingQueue<>();
        
        // Start batch processor
        this.batchProcessor = Executors.newSingleThreadScheduledExecutor(r -> 
            new Thread(r, "StockfishBatch"));
        
        startBatchProcessor();
        Log.d(TAG, "✅ Stockfish optimizer initialized");
    }
    
    public static synchronized StockfishOptimizer getInstance(StockfishManager stockfish) {
        if (instance == null) {
            instance = new StockfishOptimizer(stockfish);
        }
        return instance;
    }
    
    /**
     * Get evaluation with caching and batching
     */
    public void getOptimizedEvaluation(String fen, int depth, EvaluationCallback callback) {
        // Check cache first
        CachedEvaluation cached = evaluationCache.get(fen);
        if (cached != null && cached.isValid()) {
            cacheSaves.incrementAndGet();
            Log.d(TAG, "✅ Cache hit for evaluation (saves: " + cacheSaves.get() + ")");
            callback.onEvaluation(cached.result);
            return;
        }
        
        // Add to batch queue
        EvaluationRequest request = new EvaluationRequest(fen, depth, callback);
        requestQueue.offer(request);
        
        Log.d(TAG, "📦 Queued evaluation request (queue size: " + requestQueue.size() + ")");
    }
    
    /**
     * Process batched requests
     */
    private void startBatchProcessor() {
        batchProcessor.scheduleWithFixedDelay(() -> {
            try {
                processBatch();
            } catch (Exception e) {
                Log.e(TAG, "❌ Batch processing error", e);
            }
        }, 0, BATCH_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
    
    private void processBatch() {
        if (requestQueue.isEmpty()) return;
        
        // Collect batch
        int batchCount = Math.min(BATCH_SIZE, requestQueue.size());
        EvaluationRequest[] batch = new EvaluationRequest[batchCount];
        
        for (int i = 0; i < batchCount; i++) {
            batch[i] = requestQueue.poll();
            if (batch[i] == null) break;
        }
        
        // Process batch efficiently
        if (batchCount > 0) {
            Log.d(TAG, "🎯 Processing batch of " + batchCount + " evaluations");
            batchedRequests.addAndGet(batchCount);
            
            for (EvaluationRequest req : batch) {
                if (req != null) {
                    processRequest(req);
                }
            }
        }
    }
    
    private void processRequest(EvaluationRequest request) {
        try {
            // Get evaluation from Stockfish
            StockfishManager.EvaluationResult result = 
                stockfish.getCurrentEvaluation(request.depth);
            
            if (result != null) {
                // Cache the result
                evaluationCache.put(request.fen, new CachedEvaluation(result));
                
                // Callback with result
                request.callback.onEvaluation(result);
                
                Log.d(TAG, "✅ Evaluation complete for FEN");
            } else {
                request.callback.onError("Null evaluation result");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Evaluation failed", e);
            request.callback.onError(e.getMessage());
        }
    }
    
    /**
     * Clear old cache entries
     */
    public void cleanupCache() {
        int removed = 0;
        long now = System.currentTimeMillis();
        
        for (String key : evaluationCache.keySet()) {
            CachedEvaluation cached = evaluationCache.get(key);
            if (cached != null && !cached.isValid()) {
                evaluationCache.remove(key);
                removed++;
            }
        }
        
        if (removed > 0) {
            Log.d(TAG, "🗑️ Cleaned " + removed + " expired cache entries");
        }
    }
    
    /**
     * Get performance statistics
     */
    public void logStatistics() {
        Log.d(TAG, String.format("📊 Stockfish Optimizer Stats: Cache Saves=%d, Batched=%d, Cache Size=%d",
            cacheSaves.get(), batchedRequests.get(), evaluationCache.size()));
    }
    
    /**
     * Shutdown optimizer
     */
    public void shutdown() {
        batchProcessor.shutdown();
        try {
            if (!batchProcessor.awaitTermination(1, TimeUnit.SECONDS)) {
                batchProcessor.shutdownNow();
            }
        } catch (InterruptedException e) {
            batchProcessor.shutdownNow();
        }
        evaluationCache.clear();
        instance = null;
        Log.d(TAG, "🛑 Stockfish optimizer shutdown");
    }
    
    /**
     * Evaluation request holder
     */
    private static class EvaluationRequest {
        final String fen;
        final int depth;
        final EvaluationCallback callback;
        
        EvaluationRequest(String fen, int depth, EvaluationCallback callback) {
            this.fen = fen;
            this.depth = depth;
            this.callback = callback;
        }
    }
    
    /**
     * Cached evaluation with expiry
     */
    private static class CachedEvaluation {
        final StockfishManager.EvaluationResult result;
        final long timestamp;
        
        CachedEvaluation(StockfishManager.EvaluationResult result) {
            this.result = result;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isValid() {
            return System.currentTimeMillis() - timestamp < EVAL_CACHE_DURATION_MS;
        }
    }
    
    /**
     * Evaluation callback interface
     */
    public interface EvaluationCallback {
        void onEvaluation(StockfishManager.EvaluationResult result);
        void onError(String error);
    }
}