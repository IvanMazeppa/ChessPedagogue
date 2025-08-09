package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import okhttp3.*;
import okio.ByteString;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.example.chesspedagogue.performance.PerformanceMonitor;
import com.example.chesspedagogue.performance.APIPerformanceTracker;
import com.example.chesspedagogue.performance.StockfishPerformanceMonitor;
import com.example.chesspedagogue.performance.UIPerformanceTracker;

public class LiveMonitorClient extends WebSocketListener {
    
    private static final String TAG = "🔗 LiveMonitorClient";
    private static final String DEFAULT_SERVER_IP = "192.168.0.237"; // Fallback IP
    private static final int WEBSOCKET_PORT = 8080;
    private static final int RECONNECT_DELAY_MS = 10000; // Increased to 10 seconds to reduce spam
    private static final int MAX_RECONNECT_ATTEMPTS = 3; // Limit reconnection attempts
    private static final boolean ENABLE_LIVE_MONITOR = true; // Set to false to disable WebSocket entirely
    
    private static LiveMonitorClient instance;
    private final Gson gson = new Gson();
    private final OkHttpClient client;
    private WebSocket webSocket;
    private String serverUrl;
    private boolean isConnected = false;
    private boolean shouldReconnect = true;
    private Context context;
    private int reconnectAttempts = 0;
    
    // Enhanced performance tracking with monitoring package
    private PerformanceMonitor performanceMonitor;
    private APIPerformanceTracker apiPerformanceTracker;
    private StockfishPerformanceMonitor stockfishMonitor;
    private UIPerformanceTracker uiTracker;
    private long lastPerformanceUpdate = 0;
    private static final long PERFORMANCE_UPDATE_INTERVAL = 2000; // 2 seconds
    
    // Legacy compatibility variables
    private long lastMessageTime = 0;
    private int errorCount = 0;
    
    private LiveMonitorClient(Context context) {
        this.context = context.getApplicationContext();
        this.serverUrl = buildServerUrl();
        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS) // Reduced to fail faster
                .writeTimeout(5, TimeUnit.SECONDS)   // Reduced to fail faster
                .readTimeout(30, TimeUnit.SECONDS)   // Set reasonable read timeout
                .build();
        
        // Initialize performance monitors
        this.performanceMonitor = PerformanceMonitor.getInstance(context);
        this.apiPerformanceTracker = APIPerformanceTracker.getInstance(context);
        this.stockfishMonitor = StockfishPerformanceMonitor.getInstance(context);
        this.uiTracker = UIPerformanceTracker.getInstance(context);
        
        Log.i(TAG, "LiveMonitorClient initialized with server URL: " + serverUrl);
        Log.i(TAG, "Performance monitoring system initialized");
    }
    
    public static synchronized LiveMonitorClient getInstance(Context context) {
        if (instance == null) {
            instance = new LiveMonitorClient(context);
        }
        return instance;
    }
    
    /**
     * Builds the WebSocket server URL from SharedPreferences settings
     */
    private String buildServerUrl() {
        try {
            SharedPreferences prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
            String serverIp = prefs.getString("live_monitor_server_ip", "").trim();
            
            // If no IP is configured, use default
            if (serverIp.isEmpty()) {
                serverIp = DEFAULT_SERVER_IP;
                Log.w(TAG, "No live monitor IP configured, using default: " + serverIp);
            }
            
            String url = "ws://" + serverIp + ":" + WEBSOCKET_PORT;
            Log.i(TAG, "Built server URL: " + url);
            return url;
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error building server URL, using default: " + e.getMessage());
            return "ws://" + DEFAULT_SERVER_IP + ":" + WEBSOCKET_PORT;
        }
    }
    
    /**
     * Refreshes the server URL from current SharedPreferences settings
     */
    public void refreshServerUrl() {
        String oldUrl = this.serverUrl;
        this.serverUrl = buildServerUrl();
        
        if (!oldUrl.equals(this.serverUrl)) {
            Log.i(TAG, "🔄 Server URL updated from " + oldUrl + " to " + this.serverUrl);
            
            // If we're currently connected, reconnect with new URL
            if (isConnected) {
                Log.i(TAG, "🔄 Reconnecting to new server URL...");
                disconnect();
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                handler.postDelayed(this::connect, 1000); // Wait 1 second before reconnecting
            }
        }
    }
    
    // Connect to configurator WebSocket server
    public void connect(String customServerUrl) {
        if (!ENABLE_LIVE_MONITOR) {
            Log.i(TAG, "🚫 Live monitor disabled via ENABLE_LIVE_MONITOR flag - skipping connection");
            return;
        }
        
        if (customServerUrl != null && !customServerUrl.isEmpty()) {
            this.serverUrl = customServerUrl;
        }
        
        if (isConnected) {
            Log.w(TAG, "Already connected to live monitor");
            return;
        }
        
        try {
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .build();
            
            webSocket = client.newWebSocket(request, this);
            Log.i(TAG, "🔌 Attempting to connect to: " + serverUrl);
            Log.d(TAG, "📋 Connection attempt details: OkHttp WebSocket to " + serverUrl);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to initiate connection: " + e.getMessage(), e);
            scheduleReconnect();
        }
    }
    
    public void connect() {
        connect(null);
    }
    
    public void disconnect() {
        shouldReconnect = false;
        
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnect");
            webSocket = null;
        }
        
        isConnected = false;
        Log.i(TAG, "🚫 Disconnected from live monitor");
    }
    
    // WebSocket event handlers
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        isConnected = true;
        reconnectAttempts = 0; // Reset on successful connection
        Log.i(TAG, "✅ Connected to live monitor successfully");
        
        // Send initial connection message
        sendSystemLog("Android app connected to live monitor", "INFO");
    }
    
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "📩 Received message: " + text);
        lastMessageTime = System.currentTimeMillis();
        
        try {
            JsonObject message = gson.fromJson(text, JsonObject.class);
            String type = message.get("type").getAsString();
            
            switch (type) {
                case "welcome":
                    Log.i(TAG, "🎉 Welcome message received");
                    break;
                case "command":
                    handleCommand(message);
                    break;
                default:
                    Log.d(TAG, "Unknown message type: " + type);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error processing message: " + e.getMessage());
        }
    }
    
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.w(TAG, "🔄 Connection closing: " + code + " - " + reason);
        isConnected = false;
    }
    
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        isConnected = false;
        Log.w(TAG, "🔴 Connection closed: " + code + " - " + reason);
        
        if (shouldReconnect) {
            scheduleReconnect();
        }
    }
    
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        isConnected = false;
        errorCount++;
        
        String responseInfo = response != null ? 
            " | Response: " + response.code() + " " + response.message() : "";
        
        Log.e(TAG, "❌ Connection failed: " + t.getMessage() + responseInfo, t);
        Log.e(TAG, "🔍 Failure details - Server URL: " + serverUrl + " | Error type: " + t.getClass().getSimpleName());
        
        if (shouldReconnect) {
            scheduleReconnect();
        }
    }
    
    private void handleCommand(JsonObject message) {
        try {
            String command = message.get("command").getAsString();
            JsonObject data = message.has("data") ? message.getAsJsonObject("data") : new JsonObject();
            
            Log.d(TAG, "📡 Received command: " + command);
            
            // Handle commands from configurator (e.g., emergency stop, settings changes)
            switch (command) {
                case "emergency_stop":
                    Log.w(TAG, "⚠️ Emergency stop received from configurator");
                    // Implement emergency stop logic
                    break;
                case "toggle_feature":
                    String feature = data.get("feature").getAsString();
                    boolean enabled = data.get("enabled").getAsBoolean();
                    Log.i(TAG, "🔧 Feature toggle: " + feature + " = " + enabled);
                    break;
                case "query_database":
                    handleDatabaseQuery(data);
                    break;
                case "list_tables":
                    handleListTables();
                    break;
                case "table_info":
                    String tableName = data.get("table").getAsString();
                    handleTableInfo(tableName);
                    break;
                case "query_table":
                    handleQueryTable(data);
                    break;
                default:
                    Log.d(TAG, "❓ Unknown command: " + command);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error handling command: " + e.getMessage());
        }
    }
    
    private void scheduleReconnect() {
        if (!shouldReconnect) return;
        
        reconnectAttempts++;
        if (reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            Log.w(TAG, "⚠️ Max reconnection attempts reached (" + MAX_RECONNECT_ATTEMPTS + "), stopping reconnect attempts for this session");
            shouldReconnect = false;
            return;
        }
        
        Log.i(TAG, "🔄 Scheduling reconnect attempt " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + " in " + RECONNECT_DELAY_MS + "ms");
        
        // Use handler to delay reconnection
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (shouldReconnect && !isConnected) {
                connect();
            }
        }, RECONNECT_DELAY_MS);
    }
    
    // Methods to send different types of data to the live monitor
    
    public void sendGameState(String currentMove, String fen, double evaluation, String gameMode, 
                             boolean isGameActive, String currentPlayer, int moveNumber) {
        if (!ENABLE_LIVE_MONITOR || !isConnected || !shouldReconnect) return; // Don't try to send if disabled or given up
        
        try {
            JsonObject data = new JsonObject();
            data.addProperty("type", "game_state");
            data.addProperty("currentMove", currentMove);
            data.addProperty("fen", fen);
            data.addProperty("evaluation", evaluation);
            data.addProperty("gameMode", gameMode);
            data.addProperty("isGameActive", isGameActive);
            data.addProperty("currentPlayer", currentPlayer);
            data.addProperty("moveNumber", moveNumber);
            data.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(data);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending game state: " + e.getMessage());
        }
    }
    
    public void sendMasterActivity(String[] activeMasters, String primaryMaster, String emotionalState, 
                                 double sophisticationLevel, boolean isThinking, String currentActivity) {
        if (!ENABLE_LIVE_MONITOR || !isConnected || !shouldReconnect) return;
        
        try {
            JsonObject data = new JsonObject();
            data.addProperty("type", "master_activity");
            data.add("activeMasters", gson.toJsonTree(activeMasters));
            data.addProperty("primaryMaster", primaryMaster);
            data.addProperty("emotionalState", emotionalState);
            data.addProperty("sophisticationLevel", sophisticationLevel);
            data.addProperty("isThinking", isThinking);
            data.addProperty("currentActivity", currentActivity);
            data.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(data);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending master activity: " + e.getMessage());
        }
    }
    
    public void sendConversation(String speaker, String message, String emotion, String messageType) {
        if (!ENABLE_LIVE_MONITOR || !isConnected || !shouldReconnect) return;
        
        try {
            JsonObject data = new JsonObject();
            data.addProperty("type", "conversation");
            data.addProperty("speaker", speaker);
            data.addProperty("message", message);
            data.addProperty("emotion", emotion);
            data.addProperty("messageType", messageType);
            data.addProperty("isSystemMessage", false);
            data.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(data);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending conversation: " + e.getMessage());
        }
    }
    
    public void sendPerformanceUpdate() {
        if (!ENABLE_LIVE_MONITOR || !isConnected || !shouldReconnect) return;
        
        // Only send performance updates at specified intervals
        long now = System.currentTimeMillis();
        if (now - lastPerformanceUpdate < PERFORMANCE_UPDATE_INTERVAL) {
            return;
        }
        lastPerformanceUpdate = now;
        
        try {
            JsonObject data = new JsonObject();
            data.addProperty("type", "performance");
            data.addProperty("timestamp", now);
            
            // Memory metrics from PerformanceMonitor
            PerformanceMonitor.MemoryInfo memInfo = performanceMonitor.getMemoryInfo();
            JsonObject memoryMetrics = new JsonObject();
            memoryMetrics.addProperty("memoryUsed", memInfo.usedMemory);
            memoryMetrics.addProperty("memoryTotal", memInfo.totalMemory);
            memoryMetrics.addProperty("memoryMax", memInfo.maxMemory);
            memoryMetrics.addProperty("pssMemory", memInfo.pssMemory);
            data.add("memoryMetrics", memoryMetrics);
            
            // UI Performance metrics
            if (uiTracker != null) {
                JsonObject uiMetrics = new JsonObject();
                uiMetrics.addProperty("fps", uiTracker.getAverageFPS());
                uiMetrics.addProperty("avgFPS", uiTracker.getAverageFPS());
                uiMetrics.addProperty("droppedFrames", uiTracker.getDroppedFramePercentage());
                uiMetrics.addProperty("avgBoardRedrawTime", uiTracker.getAverageBoardRedrawTime());
                uiMetrics.addProperty("avgAnimationTime", uiTracker.getAverageAnimationTime());
                data.add("uiMetrics", uiMetrics);
            }
            
            // API Performance metrics
            JsonObject apiMetrics = new JsonObject();
            for (Map.Entry<String, APIPerformanceTracker.APIMetrics> entry : 
                 apiPerformanceTracker.getAllMetrics().entrySet()) {
                    
                APIPerformanceTracker.APIMetrics metrics = entry.getValue();
                JsonObject apiData = new JsonObject();
                apiData.addProperty("calls", metrics.totalCalls.get());
                apiData.addProperty("successRate", metrics.getSuccessRate());
                apiData.addProperty("avgResponseTime", metrics.getAverageResponseTime());
                apiData.addProperty("totalCost", metrics.totalCost.get() / 1000000.0); // Convert to dollars
                apiData.addProperty("totalTokens", metrics.totalTokensUsed.get());
                apiData.addProperty("timeouts", metrics.timeoutCalls.get());
                
                apiMetrics.add(entry.getKey(), apiData);
            }
            data.add("apiMetrics", apiMetrics);
            
            // Stockfish Performance metrics
            if (stockfishMonitor != null) {
                JsonObject stockfishMetrics = new JsonObject();
                stockfishMetrics.addProperty("nps", stockfishMonitor.getAverageNodesPerSecond());
                stockfishMetrics.addProperty("avgEvalTime", stockfishMonitor.getAverageEvalTime());
                stockfishMetrics.addProperty("avgMoveGenTime", stockfishMonitor.getAverageMoveGenTime());
                stockfishMetrics.addProperty("isUnderHeavyLoad", stockfishMonitor.isUnderHeavyLoad());
                data.add("stockfish", stockfishMetrics);
            }
            
            // System health summary
            data.addProperty("systemHealth", calculateSystemHealth());
            data.addProperty("activeThreads", Thread.activeCount());
            
            sendMessage(data);
            Log.d(TAG, "📊 Performance metrics sent to configurator");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending performance update: " + e.getMessage(), e);
        }
    }
    
    public void sendSystemLog(String message, String level) {
        if (!ENABLE_LIVE_MONITOR || !isConnected || !shouldReconnect) return;
        
        try {
            JsonObject data = new JsonObject();
            data.addProperty("type", "system_log");
            data.addProperty("message", message);
            data.addProperty("level", level);
            data.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(data);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending system log: " + e.getMessage());
        }
    }
    
    private void sendMessage(JsonObject data) {
        if (webSocket != null && isConnected) {
            String jsonString = gson.toJson(data);
            webSocket.send(jsonString);
            Log.d(TAG, "📤 Sent: " + data.get("type").getAsString());
        }
    }
    
    private String calculateSystemHealth() {
        // Calculate system health based on multiple factors
        try {
            double memoryUsage = (double) performanceMonitor.getMemoryInfo().usedMemory / 
                                performanceMonitor.getMemoryInfo().maxMemory;
            double avgFPS = uiTracker != null ? uiTracker.getAverageFPS() : 60;
            
            // Get overall API success rate
            double avgApiSuccess = 100.0;
            int apiCount = 0;
            for (APIPerformanceTracker.APIMetrics metrics : apiPerformanceTracker.getAllMetrics().values()) {
                avgApiSuccess = (avgApiSuccess * apiCount + metrics.getSuccessRate()) / (apiCount + 1);
                apiCount++;
            }
            
            // Calculate health score (0-100)
            double healthScore = 100;
            
            // Memory usage penalty
            if (memoryUsage > 0.8) healthScore -= 30;
            else if (memoryUsage > 0.6) healthScore -= 15;
            
            // FPS penalty
            if (avgFPS < 30) healthScore -= 30;
            else if (avgFPS < 45) healthScore -= 15;
            
            // API success rate penalty
            if (avgApiSuccess < 90) healthScore -= 20;
            else if (avgApiSuccess < 95) healthScore -= 10;
            
            // Determine health category
            if (healthScore >= 90) return "Excellent";
            if (healthScore >= 75) return "Good";
            if (healthScore >= 60) return "Fair";
            if (healthScore >= 40) return "Poor";
            return "Critical";
            
        } catch (Exception e) {
            Log.w(TAG, "Error calculating system health: " + e.getMessage());
            return "Unknown";
        }
    }
    
    // Getters
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Get the PerformanceMonitor instance for other components to use
     */
    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
    
    /**
     * Get the APIPerformanceTracker instance for other components to use
     */
    public APIPerformanceTracker getAPIPerformanceTracker() {
        return apiPerformanceTracker;
    }
    
    /**
     * Get the StockfishPerformanceMonitor instance for other components to use
     */
    public StockfishPerformanceMonitor getStockfishMonitor() {
        return stockfishMonitor;
    }
    
    /**
     * Get the UIPerformanceTracker instance for other components to use
     */
    public UIPerformanceTracker getUITracker() {
        return uiTracker;
    }
    
    /**
     * Reset reconnection attempts and re-enable reconnection
     * Useful for manual retry after max attempts reached
     */
    public void resetReconnection() {
        reconnectAttempts = 0;
        shouldReconnect = true;
        Log.i(TAG, "🔄 Reconnection attempts reset, re-enabling WebSocket connections");
    }

    // ===== DATABASE INSPECTION METHODS =====
    
    /**
     * Handle database query commands from the configurator
     */
    private void handleDatabaseQuery(JsonObject data) {
        try {
            String query = data.get("query").getAsString();
            Log.i(TAG, "🗃️ Executing database query: " + query);
            
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            List<Map<String, Object>> results = dbHelper.executeQuery(query);
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "database_query_result");
            response.addProperty("query", query);
            response.add("results", gson.toJsonTree(results));
            response.addProperty("rowCount", results.size());
            response.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(response);
            Log.i(TAG, "📊 Database query result sent: " + results.size() + " rows");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Database query error: " + e.getMessage());
            sendDatabaseError("Database query failed: " + e.getMessage());
        }
    }
    
    /**
     * List all tables in the database
     */
    private void handleListTables() {
        try {
            Log.i(TAG, "📋 Listing database tables");
            
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            List<String> tables = dbHelper.getAllTableNames();
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "database_tables");
            response.add("tables", gson.toJsonTree(tables));
            response.addProperty("tableCount", tables.size());
            response.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(response);
            Log.i(TAG, "📊 Database tables sent: " + tables.size() + " tables");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ List tables error: " + e.getMessage());
            sendDatabaseError("Failed to list tables: " + e.getMessage());
        }
    }
    
    /**
     * Get table information (schema, row count, etc.)
     */
    private void handleTableInfo(String tableName) {
        try {
            Log.i(TAG, "🗂️ Getting table info for: " + tableName);
            
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            Map<String, Object> tableInfo = dbHelper.getTableInfo(tableName);
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "table_info");
            response.addProperty("tableName", tableName);
            response.add("info", gson.toJsonTree(tableInfo));
            response.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(response);
            Log.i(TAG, "📊 Table info sent for: " + tableName);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Table info error: " + e.getMessage());
            sendDatabaseError("Failed to get table info for " + tableName + ": " + e.getMessage());
        }
    }
    
    /**
     * Query a specific table with optional filters
     */
    private void handleQueryTable(JsonObject data) {
        try {
            String tableName = data.get("table").getAsString();
            int limit = data.has("limit") ? data.get("limit").getAsInt() : 100;
            int offset = data.has("offset") ? data.get("offset").getAsInt() : 0;
            String whereClause = data.has("where") ? data.get("where").getAsString() : null;
            String orderBy = data.has("orderBy") ? data.get("orderBy").getAsString() : null;
            
            Log.i(TAG, "🗃️ Querying table: " + tableName + " (limit: " + limit + ", offset: " + offset + ")");
            
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            List<Map<String, Object>> results = dbHelper.queryTable(tableName, whereClause, orderBy, limit, offset);
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "table_query_result");
            response.addProperty("tableName", tableName);
            response.add("results", gson.toJsonTree(results));
            response.addProperty("rowCount", results.size());
            response.addProperty("limit", limit);
            response.addProperty("offset", offset);
            response.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(response);
            Log.i(TAG, "📊 Table query result sent: " + results.size() + " rows from " + tableName);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Table query error: " + e.getMessage());
            sendDatabaseError("Table query failed: " + e.getMessage());
        }
    }
    
    /**
     * Send database error response
     */
    private void sendDatabaseError(String errorMessage) {
        try {
            JsonObject response = new JsonObject();
            response.addProperty("type", "database_error");
            response.addProperty("error", errorMessage);
            response.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(response);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to send database error: " + e.getMessage());
        }
    }
    
    /**
     * Send database statistics to configurator
     */
    public void sendDatabaseStats() {
        if (!ENABLE_LIVE_MONITOR || !isConnected || !shouldReconnect) return;
        
        try {
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            Map<String, Object> stats = dbHelper.getComprehensiveDatabaseStats();
            
            JsonObject data = new JsonObject();
            data.addProperty("type", "database_stats");
            data.add("stats", gson.toJsonTree(stats));
            data.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(data);
            Log.d(TAG, "📊 Database stats sent");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending database stats: " + e.getMessage());
        }
    }
}