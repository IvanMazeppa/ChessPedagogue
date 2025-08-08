package com.example.chesspedagogue.performance;

import android.util.Log;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized thread pool manager to reduce thread overhead and improve performance
 */
public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";
    private static ThreadPoolManager instance;
    
    // Core thread pools for different workloads
    private final ExecutorService networkPool;      // For API calls (OpenAI, Groq, etc)
    private final ExecutorService evaluationPool;   // For Stockfish evaluation
    private final ExecutorService databasePool;     // For database operations
    private final ExecutorService audioPool;        // For TTS/STT operations
    private final ScheduledExecutorService scheduledPool; // For periodic tasks
    
    // Thread pool configurations
    private static final int NETWORK_CORE_THREADS = 4;
    private static final int NETWORK_MAX_THREADS = 8;
    private static final int EVALUATION_THREADS = 2;  // Limited for CPU-intensive work
    private static final int DATABASE_THREADS = 2;
    private static final int AUDIO_THREADS = 3;
    private static final int SCHEDULED_THREADS = 2;
    
    private static final long KEEP_ALIVE_TIME = 60L;
    
    private ThreadPoolManager() {
        // Network pool - handles API calls
        networkPool = new ThreadPoolExecutor(
            NETWORK_CORE_THREADS,
            NETWORK_MAX_THREADS,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new NamedThreadFactory("Network"),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Log.w(TAG, "⚠️ Network task rejected - queue full");
                    // Fallback: run on calling thread (synchronous)
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                }
            }
        );
        
        // Evaluation pool - CPU intensive Stockfish operations
        evaluationPool = Executors.newFixedThreadPool(
            EVALUATION_THREADS,
            new NamedThreadFactory("Evaluation")
        );
        
        // Database pool - handles SQLite operations
        databasePool = Executors.newFixedThreadPool(
            DATABASE_THREADS,
            new NamedThreadFactory("Database")
        );
        
        // Audio pool - TTS/STT operations
        audioPool = Executors.newFixedThreadPool(
            AUDIO_THREADS,
            new NamedThreadFactory("Audio")
        );
        
        // Scheduled pool - periodic tasks like evaluation tracking
        scheduledPool = Executors.newScheduledThreadPool(
            SCHEDULED_THREADS,
            new NamedThreadFactory("Scheduled")
        );
        
        Log.d(TAG, "✅ Thread pools initialized with optimized configurations");
    }
    
    public static synchronized ThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }
    
    /**
     * Submit network task (API calls)
     */
    public Future<?> submitNetworkTask(Runnable task) {
        return networkPool.submit(wrapWithErrorHandling(task, "Network"));
    }
    
    /**
     * Submit network task with callback
     */
    public <T> Future<T> submitNetworkTask(Callable<T> task) {
        return networkPool.submit(wrapWithErrorHandling(task, "Network"));
    }
    
    /**
     * Submit evaluation task (Stockfish)
     */
    public Future<?> submitEvaluationTask(Runnable task) {
        return evaluationPool.submit(wrapWithErrorHandling(task, "Evaluation"));
    }
    
    /**
     * Submit database task
     */
    public Future<?> submitDatabaseTask(Runnable task) {
        return databasePool.submit(wrapWithErrorHandling(task, "Database"));
    }
    
    /**
     * Submit audio task (TTS/STT)
     */
    public Future<?> submitAudioTask(Runnable task) {
        return audioPool.submit(wrapWithErrorHandling(task, "Audio"));
    }
    
    /**
     * Schedule periodic task
     */
    public ScheduledFuture<?> scheduleTask(Runnable task, long delay, long period, TimeUnit unit) {
        return scheduledPool.scheduleAtFixedRate(
            wrapWithErrorHandling(task, "Scheduled"),
            delay, period, unit
        );
    }
    
    /**
     * Schedule one-time delayed task
     */
    public ScheduledFuture<?> scheduleDelayed(Runnable task, long delay, TimeUnit unit) {
        return scheduledPool.schedule(
            wrapWithErrorHandling(task, "Scheduled"),
            delay, unit
        );
    }
    
    /**
     * Wrap tasks with error handling
     */
    private Runnable wrapWithErrorHandling(final Runnable task, final String poolName) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in " + poolName + " pool", e);
            }
        };
    }
    
    /**
     * Wrap callables with error handling
     */
    private <T> Callable<T> wrapWithErrorHandling(final Callable<T> task, final String poolName) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                Log.e(TAG, "❌ Error in " + poolName + " pool", e);
                throw e;
            }
        };
    }
    
    /**
     * Get thread pool statistics
     */
    public void logStatistics() {
        Log.d(TAG, "📊 Thread Pool Statistics:");
        logPoolStats("Network", (ThreadPoolExecutor) networkPool);
        logPoolStats("Evaluation", (ThreadPoolExecutor) evaluationPool);
        logPoolStats("Database", (ThreadPoolExecutor) databasePool);
        logPoolStats("Audio", (ThreadPoolExecutor) audioPool);
    }
    
    private void logPoolStats(String name, ThreadPoolExecutor pool) {
        Log.d(TAG, String.format("  %s: Active=%d, Queued=%d, Completed=%d",
            name,
            pool.getActiveCount(),
            pool.getQueue().size(),
            pool.getCompletedTaskCount()
        ));
    }
    
    /**
     * Shutdown all thread pools gracefully
     */
    public void shutdown() {
        Log.d(TAG, "🛑 Shutting down thread pools...");
        
        networkPool.shutdown();
        evaluationPool.shutdown();
        databasePool.shutdown();
        audioPool.shutdown();
        scheduledPool.shutdown();
        
        try {
            if (!networkPool.awaitTermination(5, TimeUnit.SECONDS)) {
                networkPool.shutdownNow();
            }
            if (!evaluationPool.awaitTermination(5, TimeUnit.SECONDS)) {
                evaluationPool.shutdownNow();
            }
            if (!databasePool.awaitTermination(5, TimeUnit.SECONDS)) {
                databasePool.shutdownNow();
            }
            if (!audioPool.awaitTermination(5, TimeUnit.SECONDS)) {
                audioPool.shutdownNow();
            }
            if (!scheduledPool.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread pool shutdown interrupted", e);
        }
        
        instance = null;
    }
    
    /**
     * Custom thread factory for named threads
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        
        NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, 
                "ChessPedagogue-" + namePrefix + "-" + threadNumber.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}