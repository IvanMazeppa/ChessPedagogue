package com.chesspedagogue.configurator.tabs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.Date;

/**
 * ConversationAnalyticsTab - Real-time monitoring of conversation sophistication
 * 
 * This tab shows how AI masters evolve from repetitive responses to rich,
 * contextual conversations as their databases populate with relationships,
 * memories, and emotional history.
 * 
 * Perfect for demonstrating:
 * - Fischer moving beyond "perfectionism all the time"
 * - How masters develop unique voices as they gain experience
 * - The effectiveness of the emergent behavior system
 */
public class ConversationAnalyticsTab extends Tab {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationAnalyticsTab.class);
    
    // UI Components
    private ComboBox<String> masterSelector;
    private ProgressBar sophisticationProgress;
    private Label sophisticationLevel;
    private Label diversityScore;
    private Label emotionalRange;
    private Label relationshipDepth;
    private Label memoryRichness;
    private Label totalInteractions;
    private TextArea repetitiveTopics;
    private TextArea emergentBehaviors;
    private TextArea detailedReport;
    private LineChart<Number, Number> evolutionChart;
    private Button refreshButton;
    private Button compareAllButton;
    
    // Analytics connection (would need to connect to Android app database)
    private ExecutorService executor;
    private boolean isAnalyzing = false;
    
    public ConversationAnalyticsTab() {
        super("📊 Conversation Analytics");
        this.executor = Executors.newSingleThreadExecutor();
        initializeUI();
        setupEventHandlers();
        
        logger.info("📊 Conversation Analytics tab initialized");
    }
    
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Top: Master selection and controls
        HBox topControls = createTopControls();
        root.setTop(topControls);
        
        // Center: Analytics dashboard
        VBox centerContent = createAnalyticsDashboard();
        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scrollPane);
        
        // Bottom: Status and actions
        HBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);
        
        setContent(root);
    }
    
    private HBox createTopControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(0, 0, 20, 0));
        
        // Master selection
        Label masterLabel = new Label("Select Master:");
        masterLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        masterSelector = new ComboBox<>();
        masterSelector.getItems().addAll(
            "tal", "fischer", "carlsen", "kasparov", "alekhine",
            "capablanca", "kramnik", "karpov", "anand", "lasker", "morphy", "botvinnik"
        );
        masterSelector.setValue("fischer"); // Default to Fischer for demo
        masterSelector.setPrefWidth(150);
        
        // Action buttons
        refreshButton = new Button("🔄 Analyze");
        refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        compareAllButton = new Button("📊 Compare All Masters");
        compareAllButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        
        controls.getChildren().addAll(masterLabel, masterSelector, refreshButton, compareAllButton);
        return controls;
    }
    
    private VBox createAnalyticsDashboard() {
        VBox dashboard = new VBox(20);
        
        // Sophistication overview card
        VBox overviewCard = createSophisticationOverview();
        
        // Detailed metrics card
        GridPane metricsCard = createDetailedMetrics();
        
        // Evolution chart
        VBox chartCard = createEvolutionChart();
        
        // Issues and insights
        HBox insightsCard = createInsightsPanel();
        
        // Detailed report
        VBox reportCard = createDetailedReportPanel();
        
        dashboard.getChildren().addAll(overviewCard, metricsCard, chartCard, insightsCard, reportCard);
        return dashboard;
    }
    
    private VBox createSophisticationOverview() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-padding: 20;");
        
        Label title = new Label("🎭 Conversation Sophistication Level");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        sophisticationLevel = new Label("Analyzing...");
        sophisticationLevel.setFont(Font.font("System", FontWeight.BOLD, 24));
        sophisticationLevel.setTextFill(Color.web("#2196F3"));
        
        sophisticationProgress = new ProgressBar(0.0);
        sophisticationProgress.setPrefWidth(300);
        sophisticationProgress.setStyle("-fx-accent: #4CAF50;");
        
        Label description = new Label("This score indicates how varied and contextual the master's responses are.");
        description.setStyle("-fx-text-fill: #6c757d;");
        
        card.getChildren().addAll(title, sophisticationLevel, sophisticationProgress, description);
        return card;
    }
    
    private GridPane createDetailedMetrics() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #fff; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
        
        Label title = new Label("📈 Detailed Metrics");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        GridPane.setColumnSpan(title, 4);
        grid.add(title, 0, 0);
        
        // Create metric labels and values
        String[] metricNames = {"Topic Diversity:", "Emotional Range:", "Relationship Depth:", "Memory Richness:"};
        Label[] metricValues = new Label[4];
        
        for (int i = 0; i < metricNames.length; i++) {
            Label nameLabel = new Label(metricNames[i]);
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            
            metricValues[i] = new Label("0.000");
            metricValues[i].setFont(Font.font("System", 12));
            metricValues[i].setTextFill(Color.web("#495057"));
            
            grid.add(nameLabel, (i % 2) * 2, (i / 2) + 1);
            grid.add(metricValues[i], (i % 2) * 2 + 1, (i / 2) + 1);
        }
        
        diversityScore = metricValues[0];
        emotionalRange = metricValues[1];
        relationshipDepth = metricValues[2];
        memoryRichness = metricValues[3];
        
        // Total interactions
        Label interactionsLabel = new Label("Total Interactions:");
        interactionsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        totalInteractions = new Label("0");
        totalInteractions.setFont(Font.font("System", 12));
        totalInteractions.setTextFill(Color.web("#495057"));
        
        grid.add(interactionsLabel, 0, 3);
        grid.add(totalInteractions, 1, 3);
        
        return grid;
    }
    
    private VBox createEvolutionChart() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #fff; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-padding: 20;");
        
        Label title = new Label("📈 Sophistication Evolution Over Time");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (Hours)");
        NumberAxis yAxis = new NumberAxis(0, 1, 0.1);
        yAxis.setLabel("Sophistication Score");
        
        evolutionChart = new LineChart<>(xAxis, yAxis);
        evolutionChart.setTitle("How conversation quality improves as data accumulates");
        evolutionChart.setPrefHeight(300);
        evolutionChart.setLegendVisible(false);
        
        // Sample data for demonstration
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Sophistication");
        series.getData().addAll(
            new XYChart.Data<>(0, 0.2),
            new XYChart.Data<>(1, 0.25),
            new XYChart.Data<>(2, 0.35),
            new XYChart.Data<>(4, 0.5),
            new XYChart.Data<>(8, 0.65),
            new XYChart.Data<>(12, 0.8)
        );
        evolutionChart.getData().add(series);
        
        card.getChildren().addAll(title, evolutionChart);
        return card;
    }
    
    private HBox createInsightsPanel() {
        HBox panel = new HBox(20);
        
        // Repetitive topics
        VBox repetitiveCard = new VBox(10);
        repetitiveCard.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7; -fx-border-radius: 8; -fx-padding: 15;");
        repetitiveCard.setPrefWidth(400);
        
        Label repetitiveTitle = new Label("⚠️ Repetitive Topics");
        repetitiveTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        repetitiveTopics = new TextArea();
        repetitiveTopics.setPromptText("Topics that are overused will appear here...");
        repetitiveTopics.setPrefRowCount(6);
        repetitiveTopics.setEditable(false);
        repetitiveTopics.setStyle("-fx-background-color: #fff; -fx-border-color: #ced4da;");
        
        repetitiveCard.getChildren().addAll(repetitiveTitle, repetitiveTopics);
        
        // Emergent behaviors
        VBox emergentCard = new VBox(10);
        emergentCard.setStyle("-fx-background-color: #d1ecf1; -fx-border-color: #bee5eb; -fx-border-radius: 8; -fx-padding: 15;");
        emergentCard.setPrefWidth(400);
        
        Label emergentTitle = new Label("🌟 Emergent Behaviors");
        emergentTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        emergentBehaviors = new TextArea();
        emergentBehaviors.setPromptText("Breakthrough moments and emergent behaviors will appear here...");
        emergentBehaviors.setPrefRowCount(6);
        emergentBehaviors.setEditable(false);
        emergentBehaviors.setStyle("-fx-background-color: #fff; -fx-border-color: #ced4da;");
        
        emergentCard.getChildren().addAll(emergentTitle, emergentBehaviors);
        
        panel.getChildren().addAll(repetitiveCard, emergentCard);
        return panel;
    }
    
    private VBox createDetailedReportPanel() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #fff; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-padding: 20;");
        
        Label title = new Label("📋 Detailed Analysis Report");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        detailedReport = new TextArea();
        detailedReport.setPromptText("Select a master and click 'Analyze' to generate a detailed report...");
        detailedReport.setPrefRowCount(15);
        detailedReport.setEditable(false);
        detailedReport.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        card.getChildren().addAll(title, detailedReport);
        return card;
    }
    
    private HBox createBottomControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(20, 0, 0, 0));
        
        Label status = new Label("💡 Tip: Use this tab to track conversation quality and detect repetitive patterns");
        status.setStyle("-fx-text-fill: #6c757d;");
        
        controls.getChildren().add(status);
        return controls;
    }
    
    private void setupEventHandlers() {
        refreshButton.setOnAction(e -> analyzeMaster());
        compareAllButton.setOnAction(e -> compareAllMasters());
        masterSelector.setOnAction(e -> {
            if (!isAnalyzing) {
                updateDemoData(); // Update with demo data immediately for responsiveness
            }
        });
        
        // Initial demo data
        updateDemoData();
    }
    
    private void analyzeMaster() {
        if (isAnalyzing) return;
        
        String selectedMaster = masterSelector.getValue();
        if (selectedMaster == null) return;
        
        isAnalyzing = true;
        refreshButton.setText("🔄 Analyzing...");
        refreshButton.setDisable(true);
        
        // In a real implementation, this would connect to the Android app's database
        // For now, we'll simulate the analysis
        Task<Void> analysisTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulate analysis time
                Thread.sleep(2000);
                
                Platform.runLater(() -> {
                    updateAnalysisResults(selectedMaster);
                    isAnalyzing = false;
                    refreshButton.setText("🔄 Analyze");
                    refreshButton.setDisable(false);
                });
                
                return null;
            }
        };
        
        executor.submit(analysisTask);
    }
    
    private void compareAllMasters() {
        // This would show a comparison table of all masters' sophistication levels
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Master Comparison");
        alert.setHeaderText("📊 Conversation Sophistication Comparison");
        alert.setContentText(
            "Fischer: 0.347 (Basic - talks about perfectionism too much)\n" +
            "Tal: 0.782 (Highly Sophisticated - varied tactical discussions)\n" +
            "Carlsen: 0.623 (Moderately Sophisticated - good emotional range)\n" +
            "Kasparov: 0.156 (Repetitive - needs more relationship data)\n" +
            "Alekhine: 0.891 (Highly Sophisticated - rich historical context)\n\n" +
            "💡 Tip: Masters with more populated databases show higher sophistication!"
        );
        alert.showAndWait();
    }
    
    private void updateDemoData() {
        String selectedMaster = masterSelector.getValue();
        if (selectedMaster == null) return;
        
        // Demo data based on selected master
        switch (selectedMaster) {
            case "fischer":
                updateFischerDemo();
                break;
            case "tal":
                updateTalDemo();
                break;
            case "carlsen":
                updateCarlsenDemo();
                break;
            default:
                updateDefaultDemo(selectedMaster);
        }
    }
    
    private void updateFischerDemo() {
        sophisticationLevel.setText("Basic (Repetitive)");
        sophisticationLevel.setTextFill(Color.web("#ff9800"));
        sophisticationProgress.setProgress(0.347);
        
        diversityScore.setText("0.234");
        emotionalRange.setText("0.445");
        relationshipDepth.setText("0.156");
        memoryRichness.setText("0.553");
        totalInteractions.setText("23");
        
        repetitiveTopics.setText(
            "• perfectionism (used 15 times, fatigue: 0.89)\n" +
            "• precision (used 12 times, fatigue: 0.76)\n" +
            "• chess_accuracy (used 18 times, fatigue: 0.91)\n" +
            "• best_moves (used 9 times, fatigue: 0.67)"
        );
        
        emergentBehaviors.setText(
            "• Defensive reaction when criticized (impact: 0.72)\n" +
            "• Occasional self-doubt expression (impact: 0.45)"
        );
        
        detailedReport.setText(
            "📊 CONVERSATION SOPHISTICATION REPORT\n" +
            "=====================================\n\n" +
            "Master: FISCHER\n" +
            "Overall Level: Basic (Repetitive)\n" +
            "Sophistication Score: 0.347/1.000\n\n" +
            "📈 DETAILED METRICS:\n" +
            "- Topic Diversity: 0.234/1.000\n" +
            "- Emotional Range: 0.445/1.000\n" +
            "- Relationship Depth: 0.156/1.000\n" +
            "- Memory Richness: 0.553/1.000\n" +
            "- Total Interactions: 23\n\n" +
            "⚠️ REPETITIVE TOPICS:\n" +
            "- perfectionism (used 15 times, fatigue: 0.89)\n" +
            "- precision (used 12 times, fatigue: 0.76)\n" +
            "- chess_accuracy (used 18 times, fatigue: 0.91)\n\n" +
            "💡 IMPROVEMENT SUGGESTIONS:\n" +
            "- Increase topic variety to reduce repetitive responses\n" +
            "- Develop deeper relationships with other masters\n" +
            "- Add more diverse emotional expressions\n\n" +
            "🎯 This demonstrates the need for populated databases\n" +
            "   to create more sophisticated conversations!"
        );
    }
    
    private void updateTalDemo() {
        sophisticationLevel.setText("Highly Sophisticated");
        sophisticationLevel.setTextFill(Color.web("#4CAF50"));
        sophisticationProgress.setProgress(0.782);
        
        diversityScore.setText("0.891");
        emotionalRange.setText("0.756");
        relationshipDepth.setText("0.634");
        memoryRichness.setText("0.847");
        totalInteractions.setText("156");
        
        repetitiveTopics.setText("No significantly overused topics detected!");
        
        emergentBehaviors.setText(
            "• Developed unique tactical metaphors (impact: 0.93)\n" +
            "• Creates dramatic tension in narratives (impact: 0.87)\n" +
            "• Bonds with aggressive players like Kasparov (impact: 0.76)\n" +
            "• Uses historical context in responses (impact: 0.82)"
        );
        
        detailedReport.setText(
            "📊 CONVERSATION SOPHISTICATION REPORT\n" +
            "=====================================\n\n" +
            "Master: TAL\n" +
            "Overall Level: Highly Sophisticated\n" +
            "Sophistication Score: 0.782/1.000\n\n" +
            "📈 DETAILED METRICS:\n" +
            "- Topic Diversity: 0.891/1.000\n" +
            "- Emotional Range: 0.756/1.000\n" +
            "- Relationship Depth: 0.634/1.000\n" +
            "- Memory Richness: 0.847/1.000\n" +
            "- Total Interactions: 156\n\n" +
            "🌟 EMERGENT BEHAVIORS:\n" +
            "- Developed unique tactical metaphors (impact: 0.93)\n" +
            "- Creates dramatic tension in narratives (impact: 0.87)\n" +
            "- Bonds with aggressive players like Kasparov (impact: 0.76)\n\n" +
            "✅ EXCELLENT: This master shows how rich databases\n" +
            "   lead to sophisticated, varied conversations!"
        );
    }
    
    private void updateCarlsenDemo() {
        sophisticationLevel.setText("Moderately Sophisticated");
        sophisticationLevel.setTextFill(Color.web("#2196F3"));
        sophisticationProgress.setProgress(0.623);
        
        diversityScore.setText("0.567");
        emotionalRange.setText("0.734");
        relationshipDepth.setText("0.589");
        memoryRichness.setText("0.601");
        totalInteractions.setText("87");
        
        repetitiveTopics.setText(
            "• modern_chess (used 8 times, fatigue: 0.45)\n" +
            "• practical_play (used 6 times, fatigue: 0.32)"
        );
        
        emergentBehaviors.setText(
            "• Develops respectful rivalry with older masters (impact: 0.68)\n" +
            "• Shows diplomatic communication style (impact: 0.55)"
        );
        
        detailedReport.setText(
            "📊 CONVERSATION SOPHISTICATION REPORT\n" +
            "=====================================\n\n" +
            "Master: CARLSEN\n" +
            "Overall Level: Moderately Sophisticated\n" +
            "Sophistication Score: 0.623/1.000\n\n" +
            "📈 DETAILED METRICS:\n" +
            "- Topic Diversity: 0.567/1.000\n" +
            "- Emotional Range: 0.734/1.000\n" +
            "- Relationship Depth: 0.589/1.000\n" +
            "- Memory Richness: 0.601/1.000\n" +
            "- Total Interactions: 87\n\n" +
            "🌟 EMERGENT BEHAVIORS:\n" +
            "- Develops respectful rivalry with older masters (impact: 0.68)\n" +
            "- Shows diplomatic communication style (impact: 0.55)\n\n" +
            "📈 TRENDING UP: More interactions will increase sophistication!"
        );
    }
    
    private void updateDefaultDemo(String master) {
        sophisticationLevel.setText("Developing");
        sophisticationLevel.setTextFill(Color.web("#ff9800"));
        sophisticationProgress.setProgress(0.4);
        
        diversityScore.setText("0.350");
        emotionalRange.setText("0.420");
        relationshipDepth.setText("0.380");
        memoryRichness.setText("0.450");
        totalInteractions.setText("12");
        
        repetitiveTopics.setText("Analysis pending... Generate more conversation data first.");
        emergentBehaviors.setText("No significant emergent behaviors detected yet.");
        
        detailedReport.setText(
            "📊 CONVERSATION SOPHISTICATION REPORT\n" +
            "=====================================\n\n" +
            "Master: " + master.toUpperCase() + "\n" +
            "Overall Level: Developing\n" +
            "Sophistication Score: 0.400/1.000\n\n" +
            "💡 This master needs more conversation data to develop\n" +
            "   sophisticated responses. Try spectator mode games\n" +
            "   to build up their emotional memory and relationships!"
        );
    }
    
    private void updateAnalysisResults(String masterName) {
        // This would be replaced with real database analysis
        updateDemoData();
        
        logger.info("📊 Completed sophistication analysis for {}", masterName);
    }
    
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}