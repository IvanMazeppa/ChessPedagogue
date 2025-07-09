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
    
    // Performance tracking
    private int totalApiCalls = 0;
    private int errorCount = 0;
    private long lastMessageTime = 0;
    
    private LiveMonitorClient(Context context) {
        this.context = context.getApplicationContext();
        this.serverUrl = buildServerUrl();
        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS) // Reduced to fail faster
                .writeTimeout(5, TimeUnit.SECONDS)   // Reduced to fail faster
                .readTimeout(30, TimeUnit.SECONDS)   // Set reasonable read timeout
                .build();
        
        Log.i(TAG, "LiveMonitorClient initialized with server URL: " + serverUrl);
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
        
        try {
            totalApiCalls++; // Increment for this call
            
            Runtime runtime = Runtime.getRuntime();
            long memoryUsage = runtime.totalMemory() - runtime.freeMemory();
            
            JsonObject data = new JsonObject();
            data.addProperty("type", "performance");
            data.addProperty("totalApiCalls", totalApiCalls);
            data.addProperty("errorCount", errorCount);
            data.addProperty("avgResponseTime", calculateAvgResponseTime());
            data.addProperty("memoryUsage", memoryUsage);
            data.addProperty("systemHealth", determineSystemHealth());
            data.addProperty("activeThreads", Thread.activeCount());
            data.addProperty("timestamp", System.currentTimeMillis());
            
            sendMessage(data);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending performance update: " + e.getMessage());
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
    
    private double calculateAvgResponseTime() {
        // Placeholder - implement actual response time tracking
        return 1.2; // seconds
    }
    
    private String determineSystemHealth() {
        if (errorCount == 0) return "Excellent";
        if (errorCount < 5) return "Good";
        if (errorCount < 10) return "Fair";
        return "Poor";
    }
    
    // Getters
    public boolean isConnected() {
        return isConnected;
    }
    
    public int getTotalApiCalls() {
        return totalApiCalls;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void incrementApiCalls() {
        totalApiCalls++;
    }
    
    public void incrementErrorCount() {
        errorCount++;
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