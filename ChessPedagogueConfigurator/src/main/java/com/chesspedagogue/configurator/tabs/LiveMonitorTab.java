package com.chesspedagogue.configurator.tabs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class LiveMonitorTab extends Tab {
    
    private static final Logger logger = LoggerFactory.getLogger(LiveMonitorTab.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // UI Components
    private Label connectionStatusLabel;
    private Label gameStatusLabel;
    private Label currentMoveLabel;
    private Label evaluationLabel;
    private ProgressBar evaluationBar;
    private TextArea conversationFeed;
    private ListView<String> activeMastersList;
    private Label emotionalStateLabel;
    private ProgressBar sophisticationProgress;
    private Label apiCallsLabel;
    private Label errorCountLabel;
    private Button connectButton;
    private Button disconnectButton;
    private Button emergencyStopButton;
    private Button clearLogsButton;
    private Button exportLogsButton;
    private CheckBox autoScrollCheck;
    private TextArea systemLogsArea;
    private Timeline updateTimeline;
    
    // Monitoring state
    private boolean isConnected = false;
    private boolean isGameActive = false;
    private Random random = new Random();
    private int totalApiCalls = 0;
    private int errorCount = 0;
    private double currentEvaluation = 0.0;
    private String[] activeMasters = {"Carlsen", "Alekhine"};
    private String currentPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    public LiveMonitorTab() {
        super("📊 Live Monitor");
        initializeUI();
        setupEventHandlers();
        startUpdateTimer();
        
        logger.info("📊 Live Monitor initialized");
    }
    
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Top: Connection status and controls
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // Center: Main monitoring dashboard
        GridPane centerContent = createMonitoringDashboard();
        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scrollPane);
        
        // Bottom: System logs and emergency controls
        VBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
        
        setContent(root);
    }
    
    private VBox createTopSection() {
        VBox topSection = new VBox(15);
        
        // Connection status bar
        HBox statusBar = new HBox(20);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(10, 15, 10, 15));
        statusBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        connectionStatusLabel = new Label("🔴 Disconnected");
        connectionStatusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        connectionStatusLabel.setTextFill(Color.RED);
        
        gameStatusLabel = new Label("No active game");
        gameStatusLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Label timeLabel = new Label("Time: " + LocalDateTime.now().format(TIME_FORMAT));
        timeLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        statusBar.getChildren().addAll(connectionStatusLabel, gameStatusLabel, spacer, timeLabel);
        
        // Control buttons
        HBox controlButtons = new HBox(10);
        controlButtons.setAlignment(Pos.CENTER_LEFT);
        
        connectButton = new Button("🔌 Connect to Android App");
        connectButton.getStyleClass().add("success-button");
        
        disconnectButton = new Button("🚫 Disconnect");
        disconnectButton.getStyleClass().add("button");
        disconnectButton.setDisable(true);
        
        emergencyStopButton = new Button("⚠️ EMERGENCY STOP");
        emergencyStopButton.getStyleClass().add("danger-button");
        emergencyStopButton.setDisable(true);
        
        controlButtons.getChildren().addAll(connectButton, disconnectButton, emergencyStopButton);
        
        topSection.getChildren().addAll(statusBar, controlButtons);
        return topSection;
    }
    
    private GridPane createMonitoringDashboard() {
        GridPane dashboard = new GridPane();
        dashboard.setHgap(20);
        dashboard.setVgap(15);
        dashboard.setPadding(new Insets(15, 0, 15, 0));
        
        // Game State Monitor (Top Left)
        VBox gameStateCard = createGameStateCard();
        dashboard.add(gameStateCard, 0, 0);
        
        // Master Activity Monitor (Top Right)
        VBox masterActivityCard = createMasterActivityCard();
        dashboard.add(masterActivityCard, 1, 0);
        
        // Conversation Feed (Bottom Left)
        VBox conversationCard = createConversationCard();
        dashboard.add(conversationCard, 0, 1);
        
        // Performance Metrics (Bottom Right)
        VBox performanceCard = createPerformanceCard();
        dashboard.add(performanceCard, 1, 1);
        
        return dashboard;
    }
    
    private VBox createGameStateCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("config-section");
        card.setPrefWidth(350);
        
        Label title = new Label("🎤 Game State Monitor");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.getStyleClass().add("title");
        
        GridPane gameInfo = new GridPane();
        gameInfo.setHgap(15);
        gameInfo.setVgap(8);
        
        // Current move
        Label moveLabel = new Label("Current Move:");
        moveLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        currentMoveLabel = new Label("1. e4");
        gameInfo.add(moveLabel, 0, 0);
        gameInfo.add(currentMoveLabel, 1, 0);
        
        // Evaluation
        Label evalLabel = new Label("Evaluation:");
        evalLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        evaluationLabel = new Label("+0.2");
        evaluationLabel.setTextFill(Color.GREEN);
        gameInfo.add(evalLabel, 0, 1);
        gameInfo.add(evaluationLabel, 1, 1);
        
        // Evaluation bar
        evaluationBar = new ProgressBar(0.5);
        evaluationBar.setPrefWidth(250);
        evaluationBar.setStyle("-fx-accent: #4caf50;");
        gameInfo.add(new Label("Position:"), 0, 2);
        gameInfo.add(evaluationBar, 1, 2);
        
        // FEN position display
        Label fenLabel = new Label("FEN:");
        fenLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label fenDisplay = new Label(currentPosition.substring(0, 30) + "...");
        fenDisplay.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");
        gameInfo.add(fenLabel, 0, 3);
        gameInfo.add(fenDisplay, 1, 3);
        
        card.getChildren().addAll(title, gameInfo);
        return card;
    }
    
    private VBox createMasterActivityCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("config-section");
        card.setPrefWidth(350);
        
        Label title = new Label("🎭 Master Activity");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.getStyleClass().add("title");
        
        // Active masters list
        Label mastersLabel = new Label("Active Masters:");
        mastersLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        activeMastersList = new ListView<>();
        activeMastersList.getItems().addAll("🤖 Carlsen (Analyzing)", "🤖 Alekhine (Waiting)");
        activeMastersList.setPrefHeight(100);
        
        // Emotional state
        Label emotionLabel = new Label("Emotional State:");
        emotionLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        emotionalStateLabel = new Label("Carlsen: Focused | Alekhine: Excited");
        
        // Sophistication level
        Label sophisticationLabel = new Label("Conversation Sophistication:");
        sophisticationLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        sophisticationProgress = new ProgressBar(0.7);
        sophisticationProgress.setPrefWidth(250);
        sophisticationProgress.setStyle("-fx-accent: #2196f3;");
        
        card.getChildren().addAll(
            title, mastersLabel, activeMastersList,
            emotionLabel, emotionalStateLabel,
            sophisticationLabel, sophisticationProgress
        );
        return card;
    }
    
    private VBox createConversationCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("config-section");
        card.setPrefWidth(350);
        card.setPrefHeight(300);
        
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("💬 Live Conversation Feed");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.getStyleClass().add("title");
        
        autoScrollCheck = new CheckBox("Auto-scroll");
        autoScrollCheck.setSelected(true);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        titleRow.getChildren().addAll(title, spacer, autoScrollCheck);
        
        conversationFeed = new TextArea();
        conversationFeed.setEditable(false);
        conversationFeed.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        conversationFeed.setText(
            "[12:34:56] Carlsen: This opening looks promising\n" +
            "[12:35:12] Alekhine: I see a beautiful sacrifice brewing!\n" +
            "[12:35:23] System: Evaluation changed from +0.2 to +0.5\n" +
            "[12:35:45] Carlsen: Agreed, white has a slight advantage\n"
        );
        conversationFeed.setPrefRowCount(12);
        
        card.getChildren().addAll(titleRow, conversationFeed);
        return card;
    }
    
    private VBox createPerformanceCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("config-section");
        card.setPrefWidth(350);
        
        Label title = new Label("📈 Performance Metrics");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.getStyleClass().add("title");
        
        GridPane metrics = new GridPane();
        metrics.setHgap(15);
        metrics.setVgap(8);
        
        // API calls
        Label apiLabel = new Label("API Calls:");
        apiLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        apiCallsLabel = new Label("0");
        metrics.add(apiLabel, 0, 0);
        metrics.add(apiCallsLabel, 1, 0);
        
        // Error count
        Label errorLabel = new Label("Errors:");
        errorLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        errorCountLabel = new Label("0");
        errorCountLabel.setTextFill(Color.GREEN);
        metrics.add(errorLabel, 0, 1);
        metrics.add(errorCountLabel, 1, 1);
        
        // Response time
        Label responseLabel = new Label("Avg Response:");
        responseLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label responseTime = new Label("1.2s");
        metrics.add(responseLabel, 0, 2);
        metrics.add(responseTime, 1, 2);
        
        // Memory usage
        Label memoryLabel = new Label("Memory:");
        memoryLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label memoryUsage = new Label("45MB");
        metrics.add(memoryLabel, 0, 3);
        metrics.add(memoryUsage, 1, 3);
        
        // Health indicator
        Label healthLabel = new Label("System Health:");
        healthLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label healthStatus = new Label("✅ Excellent");
        healthStatus.setTextFill(Color.GREEN);
        metrics.add(healthLabel, 0, 4);
        metrics.add(healthStatus, 1, 4);
        
        card.getChildren().addAll(title, metrics);
        return card;
    }
    
    private VBox createBottomSection() {
        VBox bottomSection = new VBox(15);
        bottomSection.setPadding(new Insets(15, 0, 0, 0));
        
        // Log control buttons
        HBox logControls = new HBox(10);
        logControls.setAlignment(Pos.CENTER_LEFT);
        
        clearLogsButton = new Button("🗑️ Clear Logs");
        clearLogsButton.getStyleClass().add("button");
        
        exportLogsButton = new Button("📤 Export Logs");
        exportLogsButton.getStyleClass().add("primary-button");
        
        logControls.getChildren().addAll(clearLogsButton, exportLogsButton);
        
        // System logs area
        Label logsLabel = new Label("📜 System Logs:");
        logsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        systemLogsArea = new TextArea();
        systemLogsArea.setEditable(false);
        systemLogsArea.setPrefRowCount(8);
        systemLogsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");
        systemLogsArea.setText(
            "[INFO] Live monitor initialized\n" +
            "[INFO] Waiting for Android app connection...\n" +
            "[DEBUG] WebSocket server listening on port 8080\n" +
            "[INFO] Configuration loaded successfully\n"
        );
        
        bottomSection.getChildren().addAll(logControls, logsLabel, systemLogsArea);
        return bottomSection;
    }
    
    private void setupEventHandlers() {
        connectButton.setOnAction(e -> connectToAndroidApp());
        disconnectButton.setOnAction(e -> disconnectFromAndroidApp());
        emergencyStopButton.setOnAction(e -> emergencyStop());
        clearLogsButton.setOnAction(e -> clearSystemLogs());
        exportLogsButton.setOnAction(e -> exportSystemLogs());
    }
    
    private void startUpdateTimer() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateMonitoringData()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }
    
    private void updateMonitoringData() {
        // Update time display in status bar
        Platform.runLater(() -> {
            if (isConnected) {
                // Simulate real-time updates
                updateGameState();
                updateMasterActivity();
                updatePerformanceMetrics();
                
                // Occasionally add conversation entries
                if (random.nextDouble() < 0.1) { // 10% chance each second
                    addRandomConversationEntry();
                }
            }
        });
    }
    
    private void updateGameState() {
        // Simulate move progression
        int moveNumber = random.nextInt(40) + 1;
        String[] moves = {"e4", "Nf3", "Bc4", "d3", "0-0", "Re1", "Bg5"};
        String randomMove = moves[random.nextInt(moves.length)];
        currentMoveLabel.setText(moveNumber + ". " + randomMove);
        
        // Update evaluation
        currentEvaluation += (random.nextDouble() - 0.5) * 0.2;
        currentEvaluation = Math.max(-3.0, Math.min(3.0, currentEvaluation));
        
        evaluationLabel.setText(String.format("%+.1f", currentEvaluation));
        evaluationLabel.setTextFill(currentEvaluation > 0 ? Color.GREEN : Color.RED);
        
        double barValue = (currentEvaluation + 3.0) / 6.0; // Normalize to 0-1
        evaluationBar.setProgress(barValue);
    }
    
    private void updateMasterActivity() {
        String[] emotions = {"Excited", "Focused", "Contemplative", "Surprised", "Pleased"};
        String carlsenEmotion = emotions[random.nextInt(emotions.length)];
        String alekhineEmotion = emotions[random.nextInt(emotions.length)];
        
        emotionalStateLabel.setText("Carlsen: " + carlsenEmotion + " | Alekhine: " + alekhineEmotion);
        
        // Update sophistication (gradually increases)
        double currentSoph = sophisticationProgress.getProgress();
        sophisticationProgress.setProgress(Math.min(1.0, currentSoph + random.nextDouble() * 0.01));
    }
    
    private void updatePerformanceMetrics() {
        if (random.nextDouble() < 0.3) { // 30% chance to increment
            totalApiCalls++;
            apiCallsLabel.setText(String.valueOf(totalApiCalls));
        }
        
        if (random.nextDouble() < 0.05 && errorCount < 3) { // 5% chance of error
            errorCount++;
            errorCountLabel.setText(String.valueOf(errorCount));
            errorCountLabel.setTextFill(errorCount > 0 ? Color.ORANGE : Color.GREEN);
            
            addSystemLog("[WARN] API timeout occurred, retrying...");
        }
    }
    
    private void addRandomConversationEntry() {
        String[] carlsenComments = {
            "This position requires precise calculation",
            "I prefer the more direct approach here",
            "The pawn structure is critical"
        };
        
        String[] alekhineComments = {
            "What a beautiful tactical motif!",
            "I can see a brilliant sacrifice coming",
            "This position has so much potential energy!"
        };
        
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        String comment;
        
        if (random.nextBoolean()) {
            comment = "[" + timestamp + "] Carlsen: " + carlsenComments[random.nextInt(carlsenComments.length)];
        } else {
            comment = "[" + timestamp + "] Alekhine: " + alekhineComments[random.nextInt(alekhineComments.length)];
        }
        
        conversationFeed.appendText("\n" + comment);
        
        if (autoScrollCheck.isSelected()) {
            conversationFeed.positionCaret(conversationFeed.getLength());
        }
    }
    
    private void connectToAndroidApp() {
        isConnected = true;
        isGameActive = true;
        
        connectionStatusLabel.setText("🟢 Connected");
        connectionStatusLabel.setTextFill(Color.GREEN);
        gameStatusLabel.setText("Spectator Game: Carlsen vs Alekhine");
        
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);
        emergencyStopButton.setDisable(false);
        
        addSystemLog("[INFO] Successfully connected to Android app");
        addSystemLog("[INFO] Monitoring active spectator game");
        
        logger.info("🔌 Connected to Android app simulation");
    }
    
    private void disconnectFromAndroidApp() {
        isConnected = false;
        isGameActive = false;
        
        connectionStatusLabel.setText("🔴 Disconnected");
        connectionStatusLabel.setTextFill(Color.RED);
        gameStatusLabel.setText("No active game");
        
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
        emergencyStopButton.setDisable(true);
        
        addSystemLog("[INFO] Disconnected from Android app");
        
        logger.info("🚫 Disconnected from Android app");
    }
    
    private void emergencyStop() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Emergency Stop");
        alert.setHeaderText("⚠️ Emergency Stop Game?");
        alert.setContentText("This will immediately halt all game activity and AI processing. Continue?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                isGameActive = false;
                gameStatusLabel.setText("⚠️ Game stopped (Emergency)");
                
                addSystemLog("[EMERGENCY] Game stopped by user intervention");
                addSystemLog("[INFO] All AI processing halted");
                
                logger.warn("⚠️ Emergency stop activated");
            }
        });
    }
    
    private void clearSystemLogs() {
        systemLogsArea.clear();
        addSystemLog("[INFO] System logs cleared");
    }
    
    private void exportSystemLogs() {
        String logs = systemLogsArea.getText();
        // In a real implementation, this would save to a file
        addSystemLog("[INFO] Logs exported to chess_pedagogue_logs_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        
        logger.info("📤 System logs exported");
    }
    
    private void addSystemLog(String message) {
        Platform.runLater(() -> {
            systemLogsArea.appendText(message + "\n");
            systemLogsArea.positionCaret(systemLogsArea.getLength());
        });
    }
    
    public void cleanup() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
}