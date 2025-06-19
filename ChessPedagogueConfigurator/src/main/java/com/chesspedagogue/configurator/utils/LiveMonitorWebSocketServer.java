package com.chesspedagogue.configurator.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class LiveMonitorWebSocketServer extends WebSocketServer {
    
    private static final Logger logger = LoggerFactory.getLogger(LiveMonitorWebSocketServer.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final Gson gson = new Gson();
    private final ConcurrentHashMap<WebSocket, String> connectedClients = new ConcurrentHashMap<>();
    
    // Callback handlers for different message types
    private Consumer<GameStateUpdate> gameStateHandler;
    private Consumer<MasterActivityUpdate> masterActivityHandler;
    private Consumer<ConversationUpdate> conversationHandler;
    private Consumer<PerformanceUpdate> performanceHandler;
    private Consumer<String> systemLogHandler;
    private Consumer<Boolean> connectionStatusHandler;
    
    public LiveMonitorWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        logger.info("🌐 LiveMonitor WebSocket server initialized on port {}", port);
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        connectedClients.put(conn, clientAddress);
        
        logger.info("📱 Android app connected from: {}", clientAddress);
        
        if (systemLogHandler != null) {
            systemLogHandler.accept(String.format("[%s] Android app connected from %s", 
                LocalDateTime.now().format(TIME_FORMAT), clientAddress));
        }
        
        if (connectionStatusHandler != null) {
            connectionStatusHandler.accept(true);
        }
        
        // Send welcome message
        JsonObject welcome = new JsonObject();
        welcome.addProperty("type", "welcome");
        welcome.addProperty("message", "Connected to Chess Pedagogue Live Monitor");
        welcome.addProperty("timestamp", LocalDateTime.now().toString());
        conn.send(gson.toJson(welcome));
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String clientAddress = connectedClients.remove(conn);
        
        logger.info("📱 Android app disconnected: {} (Code: {}, Reason: {})", 
                   clientAddress, code, reason);
        
        if (systemLogHandler != null) {
            systemLogHandler.accept(String.format("[%s] Android app disconnected (Code: %d)", 
                LocalDateTime.now().format(TIME_FORMAT), code));
        }
        
        if (connectionStatusHandler != null && connectedClients.isEmpty()) {
            connectionStatusHandler.accept(false);
        }
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
            String messageType = jsonMessage.get("type").getAsString();
            
            logger.debug("📩 Received message type: {}", messageType);
            
            switch (messageType) {
                case "game_state":
                    handleGameStateUpdate(jsonMessage);
                    break;
                case "master_activity":
                    handleMasterActivityUpdate(jsonMessage);
                    break;
                case "conversation":
                    handleConversationUpdate(jsonMessage);
                    break;
                case "performance":
                    handlePerformanceUpdate(jsonMessage);
                    break;
                case "system_log":
                    handleSystemLog(jsonMessage);
                    break;
                default:
                    logger.warn("❓ Unknown message type: {}", messageType);
            }
            
        } catch (Exception e) {
            logger.error("❌ Error processing message: {}", e.getMessage(), e);
            if (systemLogHandler != null) {
                systemLogHandler.accept(String.format("[%s] ERROR: Failed to process message - %s", 
                    LocalDateTime.now().format(TIME_FORMAT), e.getMessage()));
            }
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("🚨 WebSocket error: {}", ex.getMessage(), ex);
        if (systemLogHandler != null) {
            systemLogHandler.accept(String.format("[%s] ERROR: WebSocket error - %s", 
                LocalDateTime.now().format(TIME_FORMAT), ex.getMessage()));
        }
    }
    
    @Override
    public void onStart() {
        logger.info("🚀 LiveMonitor WebSocket server started successfully");
        if (systemLogHandler != null) {
            systemLogHandler.accept(String.format("[%s] WebSocket server started on port %d", 
                LocalDateTime.now().format(TIME_FORMAT), getPort()));
        }
    }
    
    private void handleGameStateUpdate(JsonObject data) {
        if (gameStateHandler != null) {
            GameStateUpdate update = gson.fromJson(data, GameStateUpdate.class);
            gameStateHandler.accept(update);
        }
    }
    
    private void handleMasterActivityUpdate(JsonObject data) {
        if (masterActivityHandler != null) {
            MasterActivityUpdate update = gson.fromJson(data, MasterActivityUpdate.class);
            masterActivityHandler.accept(update);
        }
    }
    
    private void handleConversationUpdate(JsonObject data) {
        if (conversationHandler != null) {
            ConversationUpdate update = gson.fromJson(data, ConversationUpdate.class);
            conversationHandler.accept(update);
        }
    }
    
    private void handlePerformanceUpdate(JsonObject data) {
        if (performanceHandler != null) {
            PerformanceUpdate update = gson.fromJson(data, PerformanceUpdate.class);
            performanceHandler.accept(update);
        }
    }
    
    private void handleSystemLog(JsonObject data) {
        if (systemLogHandler != null) {
            String logMessage = data.get("message").getAsString();
            String level = data.has("level") ? data.get("level").getAsString() : "INFO";
            systemLogHandler.accept(String.format("[%s] [%s] %s", 
                LocalDateTime.now().format(TIME_FORMAT), level, logMessage));
        }
    }
    
    // Setter methods for handlers
    public void setGameStateHandler(Consumer<GameStateUpdate> handler) {
        this.gameStateHandler = handler;
    }
    
    public void setMasterActivityHandler(Consumer<MasterActivityUpdate> handler) {
        this.masterActivityHandler = handler;
    }
    
    public void setConversationHandler(Consumer<ConversationUpdate> handler) {
        this.conversationHandler = handler;
    }
    
    public void setPerformanceHandler(Consumer<PerformanceUpdate> handler) {
        this.performanceHandler = handler;
    }
    
    public void setSystemLogHandler(Consumer<String> handler) {
        this.systemLogHandler = handler;
    }
    
    public void setConnectionStatusHandler(Consumer<Boolean> handler) {
        this.connectionStatusHandler = handler;
    }
    
    // Send command to Android app
    public void sendCommand(String command, JsonObject data) {
        if (connectedClients.isEmpty()) {
            logger.warn("⚠️ No connected clients to send command: {}", command);
            return;
        }
        
        JsonObject message = new JsonObject();
        message.addProperty("type", "command");
        message.addProperty("command", command);
        message.add("data", data);
        message.addProperty("timestamp", LocalDateTime.now().toString());
        
        String jsonMessage = gson.toJson(message);
        
        connectedClients.keySet().forEach(conn -> {
            try {
                conn.send(jsonMessage);
                logger.debug("📤 Sent command '{}' to client", command);
            } catch (Exception e) {
                logger.error("❌ Failed to send command to client: {}", e.getMessage());
            }
        });
    }
    
    public boolean hasConnectedClients() {
        return !connectedClients.isEmpty();
    }
    
    public int getConnectedClientCount() {
        return connectedClients.size();
    }
    
    // Data classes for different update types
    public static class GameStateUpdate {
        public String currentMove;
        public String fen;
        public double evaluation;
        public String gameMode;
        public boolean isGameActive;
        public String currentPlayer;
        public int moveNumber;
        public String lastMoveNotation;
        public String timestamp;
    }
    
    public static class MasterActivityUpdate {
        public String[] activeMasters;
        public String primaryMaster;
        public String secondaryMaster;
        public String emotionalState;
        public double sophisticationLevel;
        public boolean isThinking;
        public String currentActivity;
        public String timestamp;
    }
    
    public static class ConversationUpdate {
        public String speaker;
        public String message;
        public String emotion;
        public String messageType; // "comment", "analysis", "banter"
        public boolean isSystemMessage;
        public String timestamp;
    }
    
    public static class PerformanceUpdate {
        public int totalApiCalls;
        public int errorCount;
        public double avgResponseTime;
        public long memoryUsage;
        public String systemHealth;
        public int activeThreads;
        public String timestamp;
    }
}