package com.example.chesspedagogue.reasoning;

import android.content.Context;
import android.util.Log;
import com.example.chesspedagogue.StockfishManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central manager for the reasoning engine system
 */
public class ReasoningEngineManager {
    private static final String TAG = "ReasoningEngineManager";
    
    private static ReasoningEngineManager instance;
    
    private final UnifiedReasoningAgent reasoningAgent;
    private ReasoningAdaptiveEngine adaptiveEngine; // Not final - set after StockfishManager is available
    private final ExecutorService executorService;
    private final Context context;
    
    private boolean initialized = false;
    private boolean enabled = true;

    public interface ReasoningCallback {
        void onMoveSelected(String move, String explanation, float confidence);
        void onError(String error);
        void onProgress(String status); // Optional progress updates
    }

    private ReasoningEngineManager(Context context) {
        this.context = context;
        this.executorService = Executors.newCachedThreadPool();
        this.reasoningAgent = new UnifiedReasoningAgent(context);
        
        // NOTE: adaptiveEngine will be initialized when setStockfishManager is called
        this.adaptiveEngine = null;
        
        Log.d(TAG, "🧠 ReasoningEngineManager created (Stockfish pending)");
    }

    public static synchronized ReasoningEngineManager getInstance(Context context) {
        if (instance == null) {
            instance = new ReasoningEngineManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Set the StockfishManager from GameRepository
     */
    public void setStockfishManager(StockfishManager stockfishManager) {
        if (stockfishManager != null) {
            this.adaptiveEngine = new ReasoningAdaptiveEngine(stockfishManager);
            Log.d(TAG, "✅ Stockfish manager set for reasoning engine");
        } else {
            Log.w(TAG, "⚠️ Null StockfishManager provided");
        }
    }

    /**
     * Initialize the reasoning engine system
     */
    public void initialize() {
        if (initialized) return;
        
        Log.d(TAG, "🚀 Initializing reasoning engine system...");
        
        // Initialize master configurations
        MasterConfigurationManager.initialize(context);
        
        initialized = true;
        Log.d(TAG, "✅ Reasoning engine system initialized");
    }

    /**
     * Calculate move using reasoning engine
     */
    public void calculateMove(String fen, String masterName, int targetElo, 
                             String gameContext, ReasoningCallback callback) {
        
        if (!initialized) {
            Log.w(TAG, "⚠️ Reasoning engine not initialized");
            callback.onError("Reasoning engine not initialized");
            return;
        }
        
        if (!enabled) {
            Log.d(TAG, "🔄 Reasoning engine disabled, using fallback");
            callback.onError("Reasoning engine disabled");
            return;
        }
        
        if (adaptiveEngine == null) {
            Log.w(TAG, "⚠️ Stockfish manager not set - cannot generate candidates");
            callback.onError("Stockfish not available");
            return;
        }
        
        if (!supportsReasoning(masterName)) {
            Log.d(TAG, String.format("🔄 Master %s does not support reasoning", masterName));
            callback.onError("Master does not support reasoning");
            return;
        }
        
        Log.d(TAG, String.format("🎯 Calculating move for %s (ELO %d)", masterName, targetElo));
        
        executorService.execute(() -> {
            try {
                // Step 1: Generate strength-appropriate candidates
                callback.onProgress("Generating candidate moves...");
                List<CandidateMove> candidates = adaptiveEngine.generateCandidates(fen, targetElo, 8, masterName);
                
                if (candidates.isEmpty()) {
                    Log.e(TAG, "❌ No candidates generated");
                    callback.onError("No candidate moves generated");
                    return;
                }
                
                Log.d(TAG, String.format("✅ Generated %d candidates", candidates.size()));
                
                // Step 2: Validate candidates are legal
                callback.onProgress("Validating candidate moves...");
                if (!adaptiveEngine.validateCandidates(fen, candidates)) {
                    Log.w(TAG, "⚠️ Some candidates may be illegal");
                    // Continue anyway - reasoning model might filter them
                }
                
                // Step 3: Configure reasoning agent for master
                callback.onProgress("Configuring reasoning model...");
                reasoningAgent.setMaster(masterName);
                reasoningAgent.setTargetElo(targetElo); // Set adaptive strength
                
                // Step 4: Let reasoning model select best move
                callback.onProgress("Analyzing position with reasoning model...");
                ReasoningResponse response = reasoningAgent.selectMove(fen, candidates, gameContext);
                
                if (response.isHighQuality()) {
                    Log.d(TAG, String.format("✅ High-quality move selected: %s (confidence: %.2f)", 
                            response.getMove(), response.getConfidence()));
                    
                    callback.onMoveSelected(response.getMove(), response.getExplanation(), response.getConfidence());
                } else {
                    Log.w(TAG, String.format("⚠️ Low-quality response: %s (confidence: %.2f)", 
                            response.getMove(), response.getConfidence()));
                    
                    // Still return the move but with warning
                    callback.onMoveSelected(response.getMove(), 
                            "Warning: Low confidence response. " + response.getExplanation(), 
                            response.getConfidence());
                }
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Reasoning calculation failed: " + e.getMessage(), e);
                callback.onError("Reasoning failed: " + e.getMessage());
            }
        });
    }

    /**
     * Check if master supports reasoning
     */
    public static boolean supportsReasoning(String masterName) {
        return MasterConfigurationManager.supportsReasoning(masterName);
    }

    /**
     * Get configuration for a master
     */
    public MasterConfiguration getMasterConfiguration(String masterName) {
        return MasterConfigurationManager.getConfig(masterName);
    }

    /**
     * Enable or disable reasoning engine
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        Log.d(TAG, String.format("🔧 Reasoning engine %s", enabled ? "enabled" : "disabled"));
    }

    public boolean isEnabled() {
        return enabled && initialized;
    }

    /**
     * Get engine status for debugging
     */
    public String getStatus() {
        if (!initialized) return "Not initialized";
        if (!enabled) return "Disabled";
        return "Ready";
    }

    /**
     * Shutdown reasoning engine
     */
    public void shutdown() {
        Log.d(TAG, "🛑 Shutting down reasoning engine...");
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        initialized = false;
        Log.d(TAG, "✅ Reasoning engine shutdown complete");
    }

    /**
     * Simple synchronous method for testing
     */
    public ReasoningResponse calculateMoveSync(String fen, String masterName, int targetElo, String gameContext) {
        if (!initialized) {
            Log.w(TAG, "⚠️ Reasoning engine not initialized");
            return null;
        }
        
        try {
            // Generate candidates
            List<CandidateMove> candidates = adaptiveEngine.generateCandidates(fen, targetElo, 8);
            if (candidates.isEmpty()) return null;
            
            // Configure agent and get response
            reasoningAgent.setMaster(masterName);
            return reasoningAgent.selectMove(fen, candidates, gameContext);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Synchronous calculation failed: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get reasoning statistics for monitoring
     */
    public ReasoningStats getStats() {
        // TODO: Implement statistics tracking
        return new ReasoningStats();
    }

    /**
     * Basic statistics class for monitoring
     */
    public static class ReasoningStats {
        public int totalRequests = 0;
        public int successfulRequests = 0;
        public int failedRequests = 0;
        public long averageResponseTime = 0;
        public String lastError = "";
        
        public double getSuccessRate() {
            return totalRequests > 0 ? (double) successfulRequests / totalRequests : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("ReasoningStats{requests=%d, success=%.2f%%, avgTime=%dms}", 
                    totalRequests, getSuccessRate() * 100, averageResponseTime);
        }
    }
}