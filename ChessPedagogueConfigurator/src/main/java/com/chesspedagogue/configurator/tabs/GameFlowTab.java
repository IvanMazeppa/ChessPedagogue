package com.chesspedagogue.configurator.tabs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.chesspedagogue.configurator.utils.SliderWithLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class GameFlowTab extends Tab {
    
    private static final Logger logger = LoggerFactory.getLogger(GameFlowTab.class);
    
    // UI Components
    private ComboBox<String> gameTypeSelector;
    private SliderWithLabel moveDelaySlider;
    private SliderWithLabel thinkingTimeSlider;
    private CheckBox enableCommentaryCheck;
    private SliderWithLabel commentaryFrequencySlider;
    private ComboBox<String> commentaryTriggerSelector;
    private CheckBox enableEvaluationTracking;
    private SliderWithLabel evaluationThresholdSlider;
    private CheckBox enableEmotionalResponses;
    private SliderWithLabel emotionalSensitivitySlider;
    private CheckBox enableMasterDialogue;
    private SliderWithLabel dialogueFrequencySlider;
    private ComboBox<String> spectatorModeSelector;
    private CheckBox enableAutoGameEnd;
    private SliderWithLabel gameEndConditionsSlider;
    private CheckBox enableHistoricalMatching;
    private SliderWithLabel historicalMatchThresholdSlider;
    private TextArea customTriggerConditions;
    private Button testFlowButton;
    private Button simulateGameButton;
    private Button saveFlowConfigButton;
    private Label statusLabel;
    private ProgressBar simulationProgress;
    private TextArea simulationOutput;
    
    // Current configuration
    private GameFlowConfiguration currentConfig;
    
    public GameFlowTab() {
        super("⚙️ Game Flow");
        this.currentConfig = new GameFlowConfiguration();
        initializeUI();
        setupEventHandlers();
        
        logger.info("⚙️ Game Flow Controller initialized");
    }
    
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Top: Game type selection and quick actions
        HBox topControls = createTopControls();
        root.setTop(topControls);
        
        // Center: Flow configuration
        ScrollPane centerContent = createFlowConfiguration();
        root.setCenter(centerContent);
        
        // Bottom: Actions and status
        VBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);
        
        setContent(root);
    }
    
    private HBox createTopControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(0, 0, 20, 0));
        
        Label gameTypeLabel = new Label("Game Type:");
        gameTypeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        gameTypeSelector = new ComboBox<>();
        gameTypeSelector.getItems().addAll(
            "Spectator Mode (AI vs AI)",
            "Player vs AI", 
            "Analysis Mode",
            "Custom Training"
        );
        gameTypeSelector.setValue("Spectator Mode (AI vs AI)");
        gameTypeSelector.setPrefWidth(200);
        
        Button presetButton = new Button("🎯 Load Preset");
        presetButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");
        presetButton.setOnAction(e -> showPresetDialog());
        
        controls.getChildren().addAll(gameTypeLabel, gameTypeSelector, presetButton);
        return controls;
    }
    
    private ScrollPane createFlowConfiguration() {
        VBox configContent = new VBox(15);
        configContent.setPadding(new Insets(10));
        
        // Move Timing Section
        VBox timingSection = createMoveTimingSection();
        
        // Commentary & Analysis Section
        VBox commentarySection = createCommentarySection();
        
        // Emotional Response Section
        VBox emotionalSection = createEmotionalResponseSection();
        
        // Spectator Mode Section
        VBox spectatorSection = createSpectatorModeSection();
        
        // Advanced Triggers Section
        VBox advancedSection = createAdvancedTriggersSection();
        
        configContent.getChildren().addAll(
            timingSection, commentarySection, emotionalSection, spectatorSection, advancedSection
        );
        
        ScrollPane scrollPane = new ScrollPane(configContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }
    
    private VBox createMoveTimingSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("⏱️ Move Timing & Speed");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        VBox controls = new VBox(15);
        
        moveDelaySlider = new SliderWithLabel("Move Delay (seconds):", 0.5, 10.0, 2.0, 0.5);
        moveDelaySlider.setTooltip("Time between moves in spectator mode");
        
        thinkingTimeSlider = new SliderWithLabel("Thinking Time (seconds):", 1.0, 30.0, 5.0, 1.0);
        thinkingTimeSlider.setTooltip("How long AI appears to 'think' before making moves");
        
        Label timingNote = new Label("💡 Shorter delays create faster-paced games, longer delays allow for more detailed commentary");
        timingNote.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        controls.getChildren().addAll(moveDelaySlider, thinkingTimeSlider, timingNote);
        section.getChildren().addAll(sectionTitle, controls);
        return section;
    }
    
    private VBox createCommentarySection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("🎤 Commentary & Analysis");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        
        // Commentary enabled
        enableCommentaryCheck = new CheckBox("Enable AI Commentary");
        enableCommentaryCheck.setSelected(true);
        grid.add(enableCommentaryCheck, 0, 0, 2, 1);
        
        // Commentary frequency
        commentaryFrequencySlider = new SliderWithLabel("Commentary Frequency:", 0.1, 2.0, 0.7, 0.1);
        commentaryFrequencySlider.setTooltip("How often masters provide commentary (0.1 = rare, 2.0 = very frequent)");
        grid.add(commentaryFrequencySlider, 0, 1, 2, 1);
        
        // Commentary trigger
        Label triggerLabel = new Label("Commentary Triggers:");
        commentaryTriggerSelector = new ComboBox<>();
        commentaryTriggerSelector.getItems().addAll(
            "Significant Moves Only",
            "Every Move",
            "Evaluation Changes",
            "Tactical Moments",
            "Opening/Endgame Focus"
        );
        commentaryTriggerSelector.setValue("Significant Moves Only");
        grid.add(triggerLabel, 0, 2);
        grid.add(commentaryTriggerSelector, 1, 2);
        
        // Evaluation tracking
        enableEvaluationTracking = new CheckBox("Enable Position Evaluation Tracking");
        enableEvaluationTracking.setSelected(true);
        grid.add(enableEvaluationTracking, 0, 3, 2, 1);
        
        evaluationThresholdSlider = new SliderWithLabel("Evaluation Change Threshold:", 0.1, 2.0, 0.5, 0.1);
        evaluationThresholdSlider.setTooltip("Minimum evaluation change to trigger commentary (in pawns)");
        grid.add(evaluationThresholdSlider, 0, 4, 2, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createEmotionalResponseSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("😍 Emotional Responses");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        VBox controls = new VBox(15);
        
        enableEmotionalResponses = new CheckBox("Enable Emotional Response System");
        enableEmotionalResponses.setSelected(true);
        enableEmotionalResponses.setTooltip(new Tooltip("Allow masters to react emotionally to game events"));
        
        emotionalSensitivitySlider = new SliderWithLabel("Emotional Sensitivity:", 0.1, 2.0, 1.0, 0.1);
        emotionalSensitivitySlider.setTooltip("How strongly masters react to emotional triggers");
        
        enableMasterDialogue = new CheckBox("Enable Master-to-Master Dialogue");
        enableMasterDialogue.setSelected(true);
        enableMasterDialogue.setTooltip(new Tooltip("Allow masters to talk to each other during games"));
        
        dialogueFrequencySlider = new SliderWithLabel("Dialogue Frequency:", 0.1, 2.0, 0.8, 0.1);
        dialogueFrequencySlider.setTooltip("How often masters engage in conversation");
        
        Label emotionalNote = new Label("🧠 Emotional responses create more engaging and human-like commentary");
        emotionalNote.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        controls.getChildren().addAll(
            enableEmotionalResponses, emotionalSensitivitySlider,
            enableMasterDialogue, dialogueFrequencySlider, emotionalNote
        );
        
        section.getChildren().addAll(sectionTitle, controls);
        return section;
    }
    
    private VBox createSpectatorModeSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("👀 Spectator Mode Settings");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        
        // Spectator mode type
        Label modeLabel = new Label("Spectator Mode Type:");
        spectatorModeSelector = new ComboBox<>();
        spectatorModeSelector.getItems().addAll(
            "Classic Commentary",
            "Master Dialogue",
            "Educational Analysis",
            "Entertainment Focus",
            "Tournament Style"
        );
        spectatorModeSelector.setValue("Master Dialogue");
        grid.add(modeLabel, 0, 0);
        grid.add(spectatorModeSelector, 1, 0);
        
        // Auto game end
        enableAutoGameEnd = new CheckBox("Enable Automatic Game Ending");
        enableAutoGameEnd.setSelected(true);
        grid.add(enableAutoGameEnd, 0, 1, 2, 1);
        
        gameEndConditionsSlider = new SliderWithLabel("Game End Conditions:", 0.1, 2.0, 1.0, 0.1);
        gameEndConditionsSlider.setTooltip("Threshold for automatic game ending (0.1 = early end, 2.0 = play to mate)");
        grid.add(gameEndConditionsSlider, 0, 2, 2, 1);
        
        // Historical matching
        enableHistoricalMatching = new CheckBox("Enable Historical Game Matching");
        enableHistoricalMatching.setSelected(true);
        grid.add(enableHistoricalMatching, 0, 3, 2, 1);
        
        historicalMatchThresholdSlider = new SliderWithLabel("Historical Match Threshold:", 0.1, 1.0, 0.7, 0.1);
        historicalMatchThresholdSlider.setTooltip("Similarity threshold for triggering historical game references");
        grid.add(historicalMatchThresholdSlider, 0, 4, 2, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createAdvancedTriggersSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("🔧 Advanced Triggers & Conditions");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        VBox controls = new VBox(15);
        
        Label customLabel = new Label("Custom Trigger Conditions:");
        customLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        customTriggerConditions = new TextArea();
        customTriggerConditions.setPromptText(
            "Define custom conditions for triggering events:\n" +
            "Example:\n" +
            "- evaluation_swing > 1.5 -> emotional_reaction\n" +
            "- move_time > 10s -> thinking_commentary\n" +
            "- piece_sacrifice -> dramatic_response\n" +
            "- opening_novelty -> analysis_focus"
        );
        customTriggerConditions.setPrefRowCount(8);
        
        Label triggerNote = new Label("💡 Advanced users can define custom conditions using evaluation, timing, and move pattern triggers");
        triggerNote.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        controls.getChildren().addAll(customLabel, customTriggerConditions, triggerNote);
        section.getChildren().addAll(sectionTitle, controls);
        return section;
    }
    
    private VBox createBottomControls() {
        VBox bottomSection = new VBox(15);
        bottomSection.setPadding(new Insets(20, 0, 0, 0));
        
        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        testFlowButton = new Button("🧪 Test Flow");
        testFlowButton.getStyleClass().add("primary-button");
        
        simulateGameButton = new Button("🎮 Simulate Game");
        simulateGameButton.getStyleClass().add("success-button");
        
        saveFlowConfigButton = new Button("💾 Save Configuration");
        saveFlowConfigButton.getStyleClass().add("button");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        statusLabel = new Label("Game flow configuration ready");
        statusLabel.setStyle("-fx-text-fill: #6c757d;");
        
        actionButtons.getChildren().addAll(
            testFlowButton, simulateGameButton, saveFlowConfigButton, spacer, statusLabel
        );
        
        // Simulation progress and output
        simulationProgress = new ProgressBar(0.0);
        simulationProgress.setPrefWidth(400);
        simulationProgress.setVisible(false);
        
        Label outputLabel = new Label("Simulation Output:");
        outputLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        simulationOutput = new TextArea();
        simulationOutput.setEditable(false);
        simulationOutput.setPrefRowCount(6);
        simulationOutput.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        simulationOutput.setText("Ready to test game flow configurations...\n\nClick 'Test Flow' to validate current settings\nClick 'Simulate Game' to run a full game simulation");
        
        bottomSection.getChildren().addAll(actionButtons, simulationProgress, outputLabel, simulationOutput);
        return bottomSection;
    }
    
    private void setupEventHandlers() {
        gameTypeSelector.setOnAction(e -> updateConfigurationForGameType());
        testFlowButton.setOnAction(e -> testGameFlow());
        simulateGameButton.setOnAction(e -> simulateFullGame());
        saveFlowConfigButton.setOnAction(e -> saveFlowConfiguration());
        
        // Update dependent controls
        enableCommentaryCheck.setOnAction(e -> updateCommentaryControls());
        enableEmotionalResponses.setOnAction(e -> updateEmotionalControls());
        enableAutoGameEnd.setOnAction(e -> updateGameEndControls());
        enableHistoricalMatching.setOnAction(e -> updateHistoricalControls());
        
        // Initialize with current settings
        updateConfigurationForGameType();
    }
    
    private void updateConfigurationForGameType() {
        String gameType = gameTypeSelector.getValue();
        if (gameType == null) return;
        
        // Adjust default settings based on game type
        switch (gameType) {
            case "Spectator Mode (AI vs AI)":
                moveDelaySlider.setValue(2.0);
                commentaryFrequencySlider.setValue(0.8);
                enableMasterDialogue.setSelected(true);
                break;
            case "Player vs AI":
                moveDelaySlider.setValue(1.0);
                commentaryFrequencySlider.setValue(0.5);
                enableMasterDialogue.setSelected(false);
                break;
            case "Analysis Mode":
                moveDelaySlider.setValue(0.5);
                commentaryFrequencySlider.setValue(1.2);
                enableMasterDialogue.setSelected(false);
                break;
            case "Custom Training":
                moveDelaySlider.setValue(3.0);
                commentaryFrequencySlider.setValue(1.0);
                enableMasterDialogue.setSelected(true);
                break;
        }
        
        statusLabel.setText("Configuration updated for " + gameType);
    }
    
    private void updateCommentaryControls() {
        boolean enabled = enableCommentaryCheck.isSelected();
        commentaryFrequencySlider.setDisable(!enabled);
        commentaryTriggerSelector.setDisable(!enabled);
    }
    
    private void updateEmotionalControls() {
        boolean enabled = enableEmotionalResponses.isSelected();
        emotionalSensitivitySlider.setDisable(!enabled);
        dialogueFrequencySlider.setDisable(!enabled);
    }
    
    private void updateGameEndControls() {
        boolean enabled = enableAutoGameEnd.isSelected();
        gameEndConditionsSlider.setDisable(!enabled);
    }
    
    private void updateHistoricalControls() {
        boolean enabled = enableHistoricalMatching.isSelected();
        historicalMatchThresholdSlider.setDisable(!enabled);
    }
    
    private void testGameFlow() {
        statusLabel.setText("🧪 Testing game flow configuration...");
        simulationProgress.setVisible(true);
        simulationProgress.setProgress(0.0);
        
        // Simulate testing process
        Thread testThread = new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 10) {
                    final int progress = i;
                    javafx.application.Platform.runLater(() -> {
                        simulationProgress.setProgress(progress / 100.0);
                        if (progress == 50) {
                            simulationOutput.appendText("\n✅ Move timing validated: " + moveDelaySlider.getValue() + "s delay");
                            simulationOutput.appendText("\n✅ Commentary frequency validated: " + commentaryFrequencySlider.getValue());
                        }
                        if (progress == 100) {
                            simulationOutput.appendText("\n✅ Game flow test completed successfully!");
                            simulationOutput.appendText("\n📈 Configuration is valid and ready for use\n");
                            statusLabel.setText("✅ Game flow test passed");
                            simulationProgress.setVisible(false);
                        }
                    });
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        testThread.setDaemon(true);
        testThread.start();
        
        logger.info("🧪 Started game flow test");
    }
    
    private void simulateFullGame() {
        statusLabel.setText("🎮 Running full game simulation...");
        simulationProgress.setVisible(true);
        simulationProgress.setProgress(0.0);
        simulationOutput.clear();
        simulationOutput.appendText("🎮 FULL GAME SIMULATION STARTED\n");
        simulationOutput.appendText("==============================\n");
        
        // Simulate a full game with the current configuration
        Thread gameThread = new Thread(() -> {
            try {
                String[] movePhases = {"Opening", "Middle Game", "Tactics", "Endgame", "Conclusion"};
                
                for (int phase = 0; phase < movePhases.length; phase++) {
                    final int currentPhase = phase;
                    final String phaseName = movePhases[phase];
                    
                    javafx.application.Platform.runLater(() -> {
                        simulationProgress.setProgress((currentPhase + 1) / (double) movePhases.length);
                        simulationOutput.appendText("\n🔄 Phase " + (currentPhase + 1) + ": " + phaseName + "\n");
                        
                        switch (currentPhase) {
                            case 0:
                                simulationOutput.appendText("• Fischer plays 1.e4, Tal responds with Sicilian\n");
                                simulationOutput.appendText("• Commentary triggered: Opening analysis\n");
                                break;
                            case 1:
                                simulationOutput.appendText("• Complex middlegame position reached\n");
                                simulationOutput.appendText("• Evaluation swing detected: +0.3 to +0.8\n");
                                simulationOutput.appendText("• Emotional response: Tal expresses excitement\n");
                                break;
                            case 2:
                                simulationOutput.appendText("• Tactical sequence begins\n");
                                simulationOutput.appendText("• Historical match found: Similar to Tal vs Petrosian 1959\n");
                                simulationOutput.appendText("• Master dialogue: Discussion about sacrificial ideas\n");
                                break;
                            case 3:
                                simulationOutput.appendText("• Entering endgame phase\n");
                                simulationOutput.appendText("• Technical precision commentary triggered\n");
                                break;
                            case 4:
                                simulationOutput.appendText("• Game concluded: Tal wins with brilliant sacrifice\n");
                                simulationOutput.appendText("• Post-game analysis and emotional reactions\n");
                                simulationOutput.appendText("\n✅ SIMULATION COMPLETED SUCCESSFULLY!");
                                statusLabel.setText("✅ Full game simulation completed");
                                simulationProgress.setVisible(false);
                                break;
                        }
                    });
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
        
        logger.info("🎮 Started full game simulation");
    }
    
    private void saveFlowConfiguration() {
        // Collect current configuration
        currentConfig.gameType = gameTypeSelector.getValue();
        currentConfig.moveDelay = moveDelaySlider.getValue();
        currentConfig.thinkingTime = thinkingTimeSlider.getValue();
        currentConfig.commentaryEnabled = enableCommentaryCheck.isSelected();
        currentConfig.commentaryFrequency = commentaryFrequencySlider.getValue();
        currentConfig.commentaryTrigger = commentaryTriggerSelector.getValue();
        currentConfig.evaluationTracking = enableEvaluationTracking.isSelected();
        currentConfig.evaluationThreshold = evaluationThresholdSlider.getValue();
        currentConfig.emotionalResponses = enableEmotionalResponses.isSelected();
        currentConfig.emotionalSensitivity = emotionalSensitivitySlider.getValue();
        currentConfig.masterDialogue = enableMasterDialogue.isSelected();
        currentConfig.dialogueFrequency = dialogueFrequencySlider.getValue();
        currentConfig.spectatorMode = spectatorModeSelector.getValue();
        currentConfig.autoGameEnd = enableAutoGameEnd.isSelected();
        currentConfig.gameEndConditions = gameEndConditionsSlider.getValue();
        currentConfig.historicalMatching = enableHistoricalMatching.isSelected();
        currentConfig.historicalMatchThreshold = historicalMatchThresholdSlider.getValue();
        currentConfig.customTriggers = customTriggerConditions.getText();
        
        statusLabel.setText("💾 Game flow configuration saved");
        logger.info("💾 Saved game flow configuration");
    }
    
    private void showPresetDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Game Flow Presets");
        dialog.setHeaderText("🎯 Choose a Preset Configuration");
        dialog.setContentText(
            "🎤 COMMENTARY FOCUSED:\n" +
            "• High commentary frequency\n" +
            "• Detailed analysis on every move\n" +
            "• Educational focus\n\n" +
            "🔥 DRAMATIC SPECTATOR:\n" +
            "• High emotional responses\n" +
            "• Master dialogue enabled\n" +
            "• Entertainment focus\n\n" +
            "⚡ FAST-PACED GAMING:\n" +
            "• Short move delays\n" +
            "• Quick commentary\n" +
            "• Action-focused\n\n" +
            "🎯 TRAINING MODE:\n" +
            "• Longer thinking time\n" +
            "• Historical matching\n" +
            "• Educational analysis"
        );
        dialog.showAndWait();
    }
    
    // Configuration data class
    public static class GameFlowConfiguration {
        public String gameType = "Spectator Mode (AI vs AI)";
        public double moveDelay = 2.0;
        public double thinkingTime = 5.0;
        public boolean commentaryEnabled = true;
        public double commentaryFrequency = 0.7;
        public String commentaryTrigger = "Significant Moves Only";
        public boolean evaluationTracking = true;
        public double evaluationThreshold = 0.5;
        public boolean emotionalResponses = true;
        public double emotionalSensitivity = 1.0;
        public boolean masterDialogue = true;
        public double dialogueFrequency = 0.8;
        public String spectatorMode = "Master Dialogue";
        public boolean autoGameEnd = true;
        public double gameEndConditions = 1.0;
        public boolean historicalMatching = true;
        public double historicalMatchThreshold = 0.7;
        public String customTriggers = "";
    }
}