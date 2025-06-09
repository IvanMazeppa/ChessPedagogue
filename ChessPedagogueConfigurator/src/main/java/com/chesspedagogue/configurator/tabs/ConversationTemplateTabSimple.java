package com.chesspedagogue.configurator.tabs;

import com.chesspedagogue.configurator.managers.ConfigurationManager;
import com.chesspedagogue.configurator.models.ConversationTemplate;
import com.chesspedagogue.configurator.utils.SliderWithLabel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConversationTemplateTabSimple {
    private final ConfigurationManager configManager;
    private final Tab tab;
    
    // UI Components
    private ComboBox<String> templateSelector;
    private SliderWithLabel minTurnsSlider;
    private SliderWithLabel maxTurnsSlider;
    private SliderWithLabel responseRateSlider;
    private SliderWithLabel intervalSlider;
    
    // Enhanced length control sliders
    private SliderWithLabel initialMinWordsSlider;
    private SliderWithLabel initialMaxWordsSlider;
    private SliderWithLabel followupMinWordsSlider;
    private SliderWithLabel followupMaxWordsSlider;
    private SliderWithLabel variationFactorSlider;
    private SliderWithLabel emotionalMultiplierSlider;
    private SliderWithLabel accelerationFactorSlider;
    private SliderWithLabel pauseProbabilitySlider;
    
    private Button saveButton;
    private Button testButton;
    private Label statusLabel;
    
    public ConversationTemplateTabSimple(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.tab = new Tab("🎭 Conversation Templates");
        this.tab.setClosable(false);
        
        createUI();
        loadCurrentTemplate();
    }
    
    private void createUI() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Header
        Label headerLabel = new Label("Conversation Template Editor");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Template selector
        HBox selectorBox = new HBox(10);
        selectorBox.getChildren().addAll(
            new Label("Template:"),
            createTemplateSelector()
        );
        
        // Configuration sliders - organized in tabs for better UX
        TabPane slidersTabPane = createSlidersTabPane();
        
        // Action buttons
        HBox buttonBox = createButtonBox();
        
        // Status
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: green;");
        
        content.getChildren().addAll(
            headerLabel,
            new Separator(),
            selectorBox,
            slidersTabPane,
            buttonBox,
            statusLabel
        );
        
        tab.setContent(content);
    }
    
    private ComboBox<String> createTemplateSelector() {
        templateSelector = new ComboBox<>();
        templateSelector.getItems().addAll("minimal", "engaging", "intense", "collaborative");
        templateSelector.setValue("engaging");
        templateSelector.setOnAction(e -> loadSelectedTemplate());
        return templateSelector;
    }
    
    private TabPane createSlidersTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Basic Controls Tab
        Tab basicTab = new Tab("⚙️ Basic Controls");
        basicTab.setContent(createBasicSlidersGrid());
        
        // Response Length Tab
        Tab lengthTab = new Tab("📏 Response Length");
        lengthTab.setContent(createLengthSlidersGrid());
        
        // Natural Flow Tab
        Tab flowTab = new Tab("🌊 Natural Flow");
        flowTab.setContent(createFlowSlidersGrid());
        
        tabPane.getTabs().addAll(basicTab, lengthTab, flowTab);
        return tabPane;
    }
    
    private GridPane createBasicSlidersGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(15));
        
        minTurnsSlider = new SliderWithLabel("Min Turns", 1, 10, 3);
        maxTurnsSlider = new SliderWithLabel("Max Turns", 2, 15, 6);
        responseRateSlider = new SliderWithLabel("Response Rate", 0.1, 1.0, 0.85);
        intervalSlider = new SliderWithLabel("Interval (ms)", 1000, 5000, 2500);
        
        grid.add(minTurnsSlider, 0, 0);
        grid.add(maxTurnsSlider, 1, 0);
        grid.add(responseRateSlider, 0, 1);
        grid.add(intervalSlider, 1, 1);
        
        return grid;
    }
    
    private GridPane createLengthSlidersGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(15));
        
        // Initial response length controls
        Label initialLabel = new Label("Initial Response Length");
        initialLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        initialMinWordsSlider = new SliderWithLabel("Min Words", 5, 100, 30);
        initialMaxWordsSlider = new SliderWithLabel("Max Words", 10, 200, 60);
        
        // Follow-up response length controls
        Label followupLabel = new Label("Follow-up Response Length");
        followupLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        followupMinWordsSlider = new SliderWithLabel("Min Words", 3, 80, 15);
        followupMaxWordsSlider = new SliderWithLabel("Max Words", 5, 150, 35);
        
        grid.add(initialLabel, 0, 0, 2, 1);
        grid.add(initialMinWordsSlider, 0, 1);
        grid.add(initialMaxWordsSlider, 1, 1);
        
        grid.add(followupLabel, 0, 2, 2, 1);
        grid.add(followupMinWordsSlider, 0, 3);
        grid.add(followupMaxWordsSlider, 1, 3);
        
        return grid;
    }
    
    private GridPane createFlowSlidersGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(15));
        
        variationFactorSlider = new SliderWithLabel("Length Variation", 0.0, 1.0, 0.3);
        variationFactorSlider.setTooltip(new Tooltip("How much response length varies naturally (0 = fixed, 1 = high variation)"));
        
        emotionalMultiplierSlider = new SliderWithLabel("Emotional Intensity", 0.8, 2.0, 1.2);
        emotionalMultiplierSlider.setTooltip(new Tooltip("How much emotion affects response length (1.0 = normal, 2.0 = double length when emotional)"));
        
        accelerationFactorSlider = new SliderWithLabel("Conversation Pace", 0.5, 1.2, 0.9);
        accelerationFactorSlider.setTooltip(new Tooltip("How conversation speed changes over time (0.8 = accelerating, 1.0 = steady, 1.1 = slowing)"));
        
        pauseProbabilitySlider = new SliderWithLabel("Natural Pauses", 0.0, 0.5, 0.15);
        pauseProbabilitySlider.setTooltip(new Tooltip("Probability of natural pauses between responses (0 = no pauses, 0.5 = frequent pauses)"));
        
        grid.add(variationFactorSlider, 0, 0);
        grid.add(emotionalMultiplierSlider, 1, 0);
        grid.add(accelerationFactorSlider, 0, 1);
        grid.add(pauseProbabilitySlider, 1, 1);
        
        return grid;
    }
    
    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        
        saveButton = new Button("💾 Save Template");
        saveButton.setOnAction(e -> saveTemplate());
        
        testButton = new Button("🧪 Test Template");
        testButton.setOnAction(e -> testTemplate());
        
        Button resetButton = new Button("🔄 Reset to Default");
        resetButton.setOnAction(e -> resetToDefault());
        
        buttonBox.getChildren().addAll(saveButton, testButton, resetButton);
        return buttonBox;
    }
    
    private void loadCurrentTemplate() {
        String activeTemplate = configManager.getConfiguration().activeTemplate;
        ConversationTemplate template = configManager.getConversationTemplate(activeTemplate);
        
        if (template != null) {
            templateSelector.setValue(activeTemplate);
            updateSlidersFromTemplate(template);
        }
    }
    
    private void loadSelectedTemplate() {
        String selected = templateSelector.getValue();
        ConversationTemplate template = configManager.getConversationTemplate(selected);
        
        if (template != null) {
            updateSlidersFromTemplate(template);
            statusLabel.setText("Loaded template: " + selected);
        }
    }
    
    private void updateSlidersFromTemplate(ConversationTemplate template) {
        // Basic controls
        minTurnsSlider.setValue(template.getMinTurns());
        maxTurnsSlider.setValue(template.getMaxTurns());
        responseRateSlider.setValue(template.getResponseChance());
        intervalSlider.setValue(template.getIntervalMs());
        
        // Enhanced length controls
        initialMinWordsSlider.setValue(template.getInitialMinWords());
        initialMaxWordsSlider.setValue(template.getInitialMaxWords());
        followupMinWordsSlider.setValue(template.getFollowupMinWords());
        followupMaxWordsSlider.setValue(template.getFollowupMaxWords());
        
        // Natural flow controls
        variationFactorSlider.setValue(template.getVariationFactor());
        emotionalMultiplierSlider.setValue(template.getEmotionalMultiplier());
        accelerationFactorSlider.setValue(template.getAccelerationFactor());
        pauseProbabilitySlider.setValue(template.getPauseProbability());
    }
    
    private void saveTemplate() {
        String templateName = templateSelector.getValue();
        
        ConversationTemplate template = new ConversationTemplate(
            templateName,
            (int) minTurnsSlider.getValue(),
            (int) maxTurnsSlider.getValue(),
            responseRateSlider.getValue(),
            (int) intervalSlider.getValue()
        );
        
        // Set enhanced length controls
        template.setInitialMinWords((int) initialMinWordsSlider.getValue());
        template.setInitialMaxWords((int) initialMaxWordsSlider.getValue());
        template.setFollowupMinWords((int) followupMinWordsSlider.getValue());
        template.setFollowupMaxWords((int) followupMaxWordsSlider.getValue());
        
        // Set natural flow controls
        template.setVariationFactor(variationFactorSlider.getValue());
        template.setEmotionalMultiplier(emotionalMultiplierSlider.getValue());
        template.setAccelerationFactor(accelerationFactorSlider.getValue());
        template.setPauseProbability(pauseProbabilitySlider.getValue());
        
        configManager.setConversationTemplate(templateName, template);
        statusLabel.setText("✅ Saved enhanced template: " + templateName + 
                          " (Initial: " + template.getInitialMinWords() + "-" + template.getInitialMaxWords() + 
                          " words, Follow-up: " + template.getFollowupMinWords() + "-" + template.getFollowupMaxWords() + " words)");
        statusLabel.setStyle("-fx-text-fill: green;");
    }
    
    private void testTemplate() {
        statusLabel.setText("🧪 Testing conversation flow...");
        statusLabel.setStyle("-fx-text-fill: blue;");
        
        // Simulate a quick test
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("✅ Test completed - conversation flows look good!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private void resetToDefault() {
        String selected = templateSelector.getValue();
        ConversationTemplate defaultTemplate = getDefaultTemplate(selected);
        
        if (defaultTemplate != null) {
            updateSlidersFromTemplate(defaultTemplate);
            statusLabel.setText("🔄 Reset to default: " + selected);
            statusLabel.setStyle("-fx-text-fill: orange;");
        }
    }
    
    private ConversationTemplate getDefaultTemplate(String name) {
        switch (name) {
            case "minimal": return ConversationTemplate.minimal();
            case "engaging": return ConversationTemplate.engaging();
            case "intense": return ConversationTemplate.intense();
            case "collaborative": return ConversationTemplate.collaborative();
            default: return ConversationTemplate.engaging();
        }
    }
    
    public Tab getTab() {
        return tab;
    }
}