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

import java.util.HashMap;
import java.util.Map;

public class MasterPersonalityTab extends Tab {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterPersonalityTab.class);
    
    // Chess masters available in the system
    private static final String[] CHESS_MASTERS = {
        "tal", "fischer", "carlsen", "kasparov", "alekhine",
        "capablanca", "kramnik", "karpov", "anand", "lasker", "morphy", "botvinnik"
    };
    
    // UI Components
    private ComboBox<String> masterSelector;
    private TextField masterNameField;
    private TextArea personalityDescription;
    private SliderWithLabel aggressionSlider;
    private SliderWithLabel creativitySlider;
    private SliderWithLabel analysisDepthSlider;
    private SliderWithLabel emotionalIntensitySlider;
    private SliderWithLabel dramaFactorSlider;
    private CheckBox voiceEnabledCheck;
    private ComboBox<String> voiceStyleSelector;
    private TextField accentDescription; // Now used for custom voice ID
    private ComboBox<String> elevenLabsModelSelector;
    private SliderWithLabel stabilitySlider;
    private SliderWithLabel similarityBoostSlider;
    private ComboBox<String> outputFormatSelector;
    private CheckBox emotionalContagionEnabled;
    private SliderWithLabel relationshipFactorSlider;
    private TextArea conversationStyle;
    private TextArea signaturePhrasesArea;
    private CheckBox assistantModeEnabled;
    private TextField vectorStoreIdField;
    private Button savePersonalityButton;
    private Button loadPersonalityButton;
    private Button resetToDefaultButton;
    private Label statusLabel;
    
    // Current personality data
    private Map<String, PersonalityProfile> personalityProfiles;
    
    public MasterPersonalityTab() {
        super("🎭 Master Personalities");
        this.personalityProfiles = new HashMap<>();
        initializeDefaultProfiles();
        initializeUI();
        setupEventHandlers();
        
        logger.info("🎭 Master Personality Editor initialized with {} masters", CHESS_MASTERS.length);
    }
    
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Top: Master selection and quick actions
        HBox topControls = createTopControls();
        root.setTop(topControls);
        
        // Center: Personality configuration
        ScrollPane centerContent = createPersonalityEditor();
        root.setCenter(centerContent);
        
        // Bottom: Save/Load actions and status
        HBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);
        
        setContent(root);
    }
    
    private HBox createTopControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(0, 0, 20, 0));
        
        Label masterLabel = new Label("Select Master:");
        masterLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        masterSelector = new ComboBox<>();
        masterSelector.getItems().addAll(CHESS_MASTERS);
        masterSelector.setValue("fischer"); // Default to Fischer
        masterSelector.setPrefWidth(150);
        
        Button quickConfigButton = new Button("⚡ Quick Config");
        quickConfigButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");
        quickConfigButton.setOnAction(e -> showQuickConfigDialog());
        
        Button exportButton = new Button("📤 Export Profile");
        exportButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-weight: bold;");
        exportButton.setOnAction(e -> exportPersonalityProfile());
        
        controls.getChildren().addAll(masterLabel, masterSelector, quickConfigButton, exportButton);
        return controls;
    }
    
    private ScrollPane createPersonalityEditor() {
        VBox editorContent = new VBox(15);
        editorContent.setPadding(new Insets(10));
        
        // Basic Information Section
        VBox basicInfoSection = createBasicInfoSection();
        
        // Personality Traits Section
        VBox traitsSection = createPersonalityTraitsSection();
        
        // Voice & Communication Section
        VBox voiceSection = createVoiceCommunicationSection();
        
        // Emotional Intelligence Section
        VBox emotionalSection = createEmotionalIntelligenceSection();
        
        // OpenAI Integration Section
        VBox apiSection = createAPIIntegrationSection();
        
        editorContent.getChildren().addAll(
            basicInfoSection, traitsSection, voiceSection, emotionalSection, apiSection
        );
        
        ScrollPane scrollPane = new ScrollPane(editorContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }
    
    private VBox createBasicInfoSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("📝 Basic Information");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        
        // Master name
        Label nameLabel = new Label("Master Name:");
        masterNameField = new TextField();
        masterNameField.setPromptText("e.g., Bobby Fischer");
        grid.add(nameLabel, 0, 0);
        grid.add(masterNameField, 1, 0);
        
        // Personality description
        Label descLabel = new Label("Personality Description:");
        personalityDescription = new TextArea();
        personalityDescription.setPromptText("Describe the master's personality, approach to chess, and notable characteristics...");
        personalityDescription.setPrefRowCount(4);
        grid.add(descLabel, 0, 1);
        grid.add(personalityDescription, 1, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createPersonalityTraitsSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("🎯 Personality Traits");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        VBox slidersBox = new VBox(15);
        
        aggressionSlider = new SliderWithLabel("Aggression:", 0.0, 2.0, 1.0, 0.1);
        aggressionSlider.setTooltip("How aggressive the master is in their play and commentary (Tal: 1.8, Carlsen: 0.6)");
        
        creativitySlider = new SliderWithLabel("Creativity:", 0.0, 2.0, 1.0, 0.1);
        creativitySlider.setTooltip("Level of creative and unconventional thinking (Tal: 2.0, Karpov: 0.7)");
        
        analysisDepthSlider = new SliderWithLabel("Analysis Depth:", 0.0, 2.0, 1.0, 0.1);
        analysisDepthSlider.setTooltip("How deeply the master analyzes positions (Kasparov: 1.9, Tal: 1.2)");
        
        emotionalIntensitySlider = new SliderWithLabel("Emotional Intensity:", 0.0, 2.0, 1.0, 0.1);
        emotionalIntensitySlider.setTooltip("Overall emotional intensity in conversations (Fischer: 1.7, Carlsen: 0.9)");
        
        dramaFactorSlider = new SliderWithLabel("Drama Factor:", 0.0, 3.0, 1.0, 0.1);
        dramaFactorSlider.setTooltip("How dramatic the master's commentary becomes (Tal: 2.0, others: 1.0)");
        
        slidersBox.getChildren().addAll(
            aggressionSlider, creativitySlider, analysisDepthSlider,
            emotionalIntensitySlider, dramaFactorSlider
        );
        
        section.getChildren().addAll(sectionTitle, slidersBox);
        return section;
    }
    
    private VBox createVoiceCommunicationSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("🎤 ElevenLabs Voice & Communication");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        
        // Voice enabled
        voiceEnabledCheck = new CheckBox("Enable ElevenLabs TTS");
        voiceEnabledCheck.setSelected(true);
        grid.add(voiceEnabledCheck, 0, 0, 2, 1);
        
        // Voice ID selection
        Label voiceIdLabel = new Label("ElevenLabs Voice ID:");
        voiceStyleSelector = new ComboBox<>();
        voiceStyleSelector.getItems().addAll(
            "Adam (pNInz6obpgDQGcFmaJgB)", "Clyde (2EiwWnXFnvU5JabPnv8n)", 
            "Daniel (onwK4e9ZLuTAKqWW03F9)", "Josh (TxGEqnHWrfWFTfGW9XjX)",
            "Antoni (ErXwobaYiN019PkySvjV)", "Arnold (VR6AewLTigWG4xSOukaG)",
            "Sam (yoZ06aMxZJJ28mfd3POQ)", "Serena (pMsXgVXv3BLzUgSXRplE)",
            "Custom Voice ID..."
        );
        voiceStyleSelector.setValue("Adam (pNInz6obpgDQGcFmaJgB)");
        voiceStyleSelector.setPrefWidth(300);
        grid.add(voiceIdLabel, 0, 1);
        grid.add(voiceStyleSelector, 1, 1);
        
        // Custom Voice ID field
        Label customVoiceLabel = new Label("Custom Voice ID:");
        accentDescription = new TextField();
        accentDescription.setPromptText("Enter your custom ElevenLabs voice ID (e.g., abc123xyz)");
        accentDescription.setDisable(true);
        grid.add(customVoiceLabel, 0, 2);
        grid.add(accentDescription, 1, 2);
        
        // ElevenLabs Model Selection
        Label modelLabel = new Label("ElevenLabs Model:");
        elevenLabsModelSelector = new ComboBox<>();
        elevenLabsModelSelector.getItems().addAll(
            "eleven_flash_v2_5", "eleven_turbo_v2_5", "eleven_multilingual_v2",
            "eleven_monolingual_v1", "eleven_flash_v2", "eleven_turbo_v2"
        );
        elevenLabsModelSelector.setValue("eleven_flash_v2_5");
        elevenLabsModelSelector.setTooltip(new Tooltip("Flash models provide ~75ms ultra-low latency"));
        grid.add(modelLabel, 0, 3);
        grid.add(elevenLabsModelSelector, 1, 3);
        
        // Voice Settings - Stability
        stabilitySlider = new SliderWithLabel("Stability:", 0.0, 1.0, 0.5, 0.05);
        stabilitySlider.setTooltip("Lower = more expressive, Higher = more consistent");
        grid.add(stabilitySlider, 0, 4, 2, 1);
        
        // Voice Settings - Similarity Boost
        similarityBoostSlider = new SliderWithLabel("Similarity Boost:", 0.0, 1.0, 0.75, 0.05);
        similarityBoostSlider.setTooltip("Recommended: 0.75 for optimal voice quality");
        grid.add(similarityBoostSlider, 0, 5, 2, 1);
        
        // Output Format
        Label formatLabel = new Label("Output Format:");
        outputFormatSelector = new ComboBox<>();
        outputFormatSelector.getItems().addAll(
            "mp3_44100_128", "mp3_44100_64", "mp3_44100_96", 
            "mp3_22050_32", "pcm_16000", "pcm_22050", "pcm_24000", "pcm_44100"
        );
        outputFormatSelector.setValue("mp3_44100_128");
        outputFormatSelector.setTooltip(new Tooltip("MP3 44100Hz 128kbps recommended for quality"));
        grid.add(formatLabel, 0, 6);
        grid.add(outputFormatSelector, 1, 6);
        
        // Conversation style
        Label convStyleLabel = new Label("Conversation Style:");
        conversationStyle = new TextArea();
        conversationStyle.setPromptText("Describe how this master communicates, their typical phrases, tone, etc.");
        conversationStyle.setPrefRowCount(3);
        grid.add(convStyleLabel, 0, 7);
        grid.add(conversationStyle, 1, 7);
        
        // Signature phrases
        Label phrasesLabel = new Label("Signature Phrases:");
        signaturePhrasesArea = new TextArea();
        signaturePhrasesArea.setPromptText("One phrase per line (e.g., 'This position is like a beautiful flower about to bloom!')");
        signaturePhrasesArea.setPrefRowCount(4);
        grid.add(phrasesLabel, 0, 8);
        grid.add(signaturePhrasesArea, 1, 8);
        
        // Cost Management Section
        Label costHeader = new Label("💰 Cost Management ($100/month):");
        costHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        costHeader.setStyle("-fx-text-fill: #d83b01;"); // Warning red
        grid.add(costHeader, 0, 9, 2, 1);
        
        // Usage Priority
        Label priorityLabel = new Label("Usage Priority:");
        ComboBox<String> prioritySelector = new ComboBox<>();
        prioritySelector.getItems().addAll(
            "Critical Only (Game-changing moments)",
            "Important (Key moves & emotions)", 
            "Normal (Most commentary)",
            "High (All voice enabled)",
            "Disabled (No ElevenLabs)"
        );
        prioritySelector.setValue("Important (Key moves & emotions)");
        prioritySelector.setTooltip(new Tooltip("Control when this master uses expensive ElevenLabs vs free fallback"));
        grid.add(priorityLabel, 0, 10);
        grid.add(prioritySelector, 1, 10);
        
        // Character limits
        SliderWithLabel characterLimitSlider = new SliderWithLabel("Max Characters per Call:", 50.0, 500.0, 150.0, 25.0);
        characterLimitSlider.setTooltip("Limit length to control costs (~$0.30 per 1000 chars)");
        grid.add(characterLimitSlider, 0, 11, 2, 1);
        
        // Daily budget
        SliderWithLabel dailyBudgetSlider = new SliderWithLabel("Daily Budget (characters):", 1000.0, 20000.0, 5000.0, 500.0);
        dailyBudgetSlider.setTooltip("Stop ElevenLabs when daily character limit reached");
        grid.add(dailyBudgetSlider, 0, 12, 2, 1);
        
        // Caching settings
        CheckBox enableCaching = new CheckBox("Enable Voice Caching (Reuse common phrases)");
        enableCaching.setSelected(true);
        enableCaching.setTooltip(new Tooltip("Cache frequent phrases to reduce API calls"));
        grid.add(enableCaching, 0, 13, 2, 1);
        
        // Fallback TTS
        Label fallbackLabel = new Label("Fallback TTS (when budget exceeded):");
        ComboBox<String> fallbackSelector = new ComboBox<>();
        fallbackSelector.getItems().addAll("Android System TTS", "Espeak", "Silent Mode");
        fallbackSelector.setValue("Android System TTS");
        grid.add(fallbackLabel, 0, 14);
        grid.add(fallbackSelector, 1, 14);
        
        // Cost warning
        Label costWarning = new Label("⚠️ ElevenLabs: $100/month = ~$0.30 per 1000 characters. Monitor usage carefully!");
        costWarning.setStyle("-fx-text-fill: #d83b01; -fx-font-weight: bold; -fx-font-style: italic;");
        grid.add(costWarning, 0, 15, 2, 1);
        
        // Setup voice ID selection handler
        voiceStyleSelector.setOnAction(e -> {
            boolean isCustom = voiceStyleSelector.getValue() != null && 
                              voiceStyleSelector.getValue().equals("Custom Voice ID...");
            accentDescription.setDisable(!isCustom);
            if (!isCustom) {
                accentDescription.clear();
            }
        });
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createEmotionalIntelligenceSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("🧠 Emotional Intelligence");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        VBox emotionalControls = new VBox(15);
        
        emotionalContagionEnabled = new CheckBox("Enable Emotional Contagion");
        emotionalContagionEnabled.setSelected(true);
        emotionalContagionEnabled.setTooltip(new Tooltip("Allow this master to influence and be influenced by other masters' emotions"));
        
        relationshipFactorSlider = new SliderWithLabel("Relationship Sensitivity:", 0.0, 2.0, 1.0, 0.1);
        relationshipFactorSlider.setTooltip("How much relationships with other masters affect this master's behavior");
        
        Label emotionalTip = new Label("💡 Tip: Higher relationship sensitivity creates more dynamic conversations between masters");
        emotionalTip.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        
        emotionalControls.getChildren().addAll(
            emotionalContagionEnabled, relationshipFactorSlider, emotionalTip
        );
        
        section.getChildren().addAll(sectionTitle, emotionalControls);
        return section;
    }
    
    private VBox createAPIIntegrationSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("config-section");
        
        Label sectionTitle = new Label("🤖 Responses API Integration");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.getStyleClass().add("title");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        
        // Vector Store ID
        Label vectorLabel = new Label("Vector Store ID:");
        vectorStoreIdField = new TextField();
        vectorStoreIdField.setPromptText("vs_example123...");
        grid.add(vectorLabel, 0, 0);
        grid.add(vectorStoreIdField, 1, 0);
        
        // Enhanced mode checkbox
        assistantModeEnabled = new CheckBox("Enable Enhanced Knowledge Base");
        assistantModeEnabled.setSelected(true);
        assistantModeEnabled.setTooltip(new Tooltip("Use vector store for enhanced historical knowledge and context"));
        grid.add(assistantModeEnabled, 0, 1, 2, 1);
        
        // System instructions preview
        Label instructionsLabel = new Label("System Instructions Preview:");
        TextArea instructionsPreview = new TextArea();
        instructionsPreview.setEditable(false);
        instructionsPreview.setPrefRowCount(4);
        instructionsPreview.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        instructionsPreview.setText(
            "You are Bobby Fischer, the perfectionist American chess genius...\n" +
            "Your personality traits:\n" +
            "- Aggression: 1.3x\n" +
            "- Analysis Depth: 1.8x\n" +
            "- Emotional Intensity: 1.7x\n" +
            "\n" +
            "Voice: New York accent, direct delivery\n" +
            "Access to vector store: vs_fischer_games for historical context"
        );
        grid.add(instructionsLabel, 0, 2);
        grid.add(instructionsPreview, 1, 2);
        
        Label apiNote = new Label("💡 Each master uses the Responses API with tool-enabled vector store access for rich historical context");
        apiNote.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        grid.add(apiNote, 0, 3, 2, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private HBox createBottomControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(20, 0, 0, 0));
        
        savePersonalityButton = new Button("💾 Save Personality");
        savePersonalityButton.getStyleClass().add("success-button");
        
        loadPersonalityButton = new Button("📂 Load Profile");
        loadPersonalityButton.getStyleClass().add("primary-button");
        
        resetToDefaultButton = new Button("🔄 Reset to Default");
        resetToDefaultButton.getStyleClass().add("danger-button");
        
        statusLabel = new Label("Ready to configure personalities");
        statusLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        controls.getChildren().addAll(
            savePersonalityButton, loadPersonalityButton, resetToDefaultButton,
            spacer, statusLabel
        );
        
        return controls;
    }
    
    private void setupEventHandlers() {
        masterSelector.setOnAction(e -> loadPersonalityProfile(masterSelector.getValue()));
        savePersonalityButton.setOnAction(e -> saveCurrentPersonality());
        loadPersonalityButton.setOnAction(e -> loadPersonalityProfile(masterSelector.getValue()));
        resetToDefaultButton.setOnAction(e -> resetToDefaultPersonality());
        
        assistantModeEnabled.setOnAction(e -> {
            boolean enabled = assistantModeEnabled.isSelected();
            vectorStoreIdField.setDisable(!enabled);
        });
        
        // Load initial profile
        loadPersonalityProfile("fischer");
    }
    
    private void initializeDefaultProfiles() {
        // Initialize with some example personality profiles
        PersonalityProfile fischer = new PersonalityProfile();
        fischer.masterName = "Bobby Fischer";
        fischer.description = "Perfectionist American chess genius. Obsessed with finding the absolute best moves. Can be harsh about inferior play but deeply passionate about chess excellence.";
        fischer.aggression = 1.3;
        fischer.creativity = 1.1;
        fischer.analysisDepth = 1.8;
        fischer.emotionalIntensity = 1.7;
        fischer.dramaFactor = 1.0;
        fischer.elevenLabsVoiceId = "Clyde (2EiwWnXFnvU5JabPnv8n)";
        fischer.elevenLabsModel = "eleven_flash_v2_5";
        fischer.stability = 0.65; // More consistent, authoritative
        fischer.similarityBoost = 0.75;
        fischer.outputFormat = "mp3_44100_128";
        fischer.conversationStyle = "Direct, analytical, sometimes critical. Values precision above all else. Can be encouraging when impressed by good moves.";
        fischer.signaturePhrases = "That's not the best move\nPrecision is everything\nChess demands perfection";
        fischer.assistantMode = true;
        fischer.vectorStoreId = "vs_fischer_historical_games";
        personalityProfiles.put("fischer", fischer);
        
        PersonalityProfile tal = new PersonalityProfile();
        tal.masterName = "Mikhail Tal";
        tal.description = "The Magician of Riga. Creative tactical genius who sees beauty in sacrificial attacks. Dramatic, artistic approach to chess with infectious enthusiasm.";
        tal.aggression = 1.8;
        tal.creativity = 2.0;
        tal.analysisDepth = 1.2;
        tal.emotionalIntensity = 1.6;
        tal.dramaFactor = 2.0;
        tal.elevenLabsVoiceId = "Adam (pNInz6obpgDQGcFmaJgB)";
        tal.elevenLabsModel = "eleven_flash_v2_5";
        tal.stability = 0.35; // More expressive, passionate
        tal.similarityBoost = 0.75;
        tal.outputFormat = "mp3_44100_128";
        tal.conversationStyle = "Poetic, dramatic, loves sacrificial themes. Uses artistic metaphors. Infectious enthusiasm for brilliant tactical ideas.";
        tal.signaturePhrases = "This position is like a beautiful flower about to bloom!\nSacrifice everything for beauty!\nIn chess, as in life, the most beautiful move wins the heart";
        tal.assistantMode = true;
        tal.vectorStoreId = "vs_tal_tactical_masterpieces";
        personalityProfiles.put("tal", tal);
        
        // Add more masters as needed...
    }
    
    private void loadPersonalityProfile(String masterKey) {
        PersonalityProfile profile = personalityProfiles.get(masterKey);
        if (profile == null) {
            profile = new PersonalityProfile(); // Create empty profile
            profile.masterName = capitalize(masterKey);
        }
        
        // Load profile data into UI
        masterNameField.setText(profile.masterName);
        personalityDescription.setText(profile.description);
        aggressionSlider.setValue(profile.aggression);
        creativitySlider.setValue(profile.creativity);
        analysisDepthSlider.setValue(profile.analysisDepth);
        emotionalIntensitySlider.setValue(profile.emotionalIntensity);
        dramaFactorSlider.setValue(profile.dramaFactor);
        voiceEnabledCheck.setSelected(profile.voiceEnabled);
        voiceStyleSelector.setValue(profile.elevenLabsVoiceId);
        accentDescription.setText(profile.customVoiceId);
        elevenLabsModelSelector.setValue(profile.elevenLabsModel);
        stabilitySlider.setValue(profile.stability);
        similarityBoostSlider.setValue(profile.similarityBoost);
        outputFormatSelector.setValue(profile.outputFormat);
        emotionalContagionEnabled.setSelected(profile.emotionalContagionEnabled);
        relationshipFactorSlider.setValue(profile.relationshipFactor);
        conversationStyle.setText(profile.conversationStyle);
        signaturePhrasesArea.setText(profile.signaturePhrases);
        assistantModeEnabled.setSelected(profile.assistantMode);
        vectorStoreIdField.setText(profile.vectorStoreId);
        
        vectorStoreIdField.setDisable(!profile.assistantMode);
        
        statusLabel.setText("Loaded profile for " + profile.masterName);
        logger.info("📂 Loaded personality profile for {}", masterKey);
    }
    
    private void saveCurrentPersonality() {
        String masterKey = masterSelector.getValue();
        if (masterKey == null) return;
        
        PersonalityProfile profile = new PersonalityProfile();
        profile.masterName = masterNameField.getText();
        profile.description = personalityDescription.getText();
        profile.aggression = aggressionSlider.getValue();
        profile.creativity = creativitySlider.getValue();
        profile.analysisDepth = analysisDepthSlider.getValue();
        profile.emotionalIntensity = emotionalIntensitySlider.getValue();
        profile.dramaFactor = dramaFactorSlider.getValue();
        profile.voiceEnabled = voiceEnabledCheck.isSelected();
        profile.elevenLabsVoiceId = voiceStyleSelector.getValue();
        profile.customVoiceId = accentDescription.getText();
        profile.elevenLabsModel = elevenLabsModelSelector.getValue();
        profile.stability = stabilitySlider.getValue();
        profile.similarityBoost = similarityBoostSlider.getValue();
        profile.outputFormat = outputFormatSelector.getValue();
        profile.emotionalContagionEnabled = emotionalContagionEnabled.isSelected();
        profile.relationshipFactor = relationshipFactorSlider.getValue();
        profile.conversationStyle = conversationStyle.getText();
        profile.signaturePhrases = signaturePhrasesArea.getText();
        profile.assistantMode = assistantModeEnabled.isSelected();
        profile.vectorStoreId = vectorStoreIdField.getText();
        
        personalityProfiles.put(masterKey, profile);
        statusLabel.setText("✅ Saved personality for " + profile.masterName);
        logger.info("💾 Saved personality profile for {}", masterKey);
    }
    
    private void resetToDefaultPersonality() {
        String masterKey = masterSelector.getValue();
        if (masterKey == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Personality");
        alert.setHeaderText("Reset to Default?");
        alert.setContentText("This will reset " + capitalize(masterKey) + "'s personality to default settings. Continue?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                personalityProfiles.remove(masterKey);
                initializeDefaultProfiles(); // Recreate defaults
                loadPersonalityProfile(masterKey);
                statusLabel.setText("🔄 Reset " + capitalize(masterKey) + " to default");
            }
        });
    }
    
    private void showQuickConfigDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Quick Configuration Guide");
        dialog.setHeaderText("⚡ Master Personality Quick Setup");
        dialog.setContentText(
            "🎯 PERSONALITY TRAITS:\n" +
            "• Aggression: How forceful in analysis (Tal: High, Carlsen: Medium)\n" +
            "• Creativity: Unconventional thinking (Tal: Max, Karpov: Low)\n" +
            "• Analysis Depth: Detail level (Kasparov: High, Tal: Medium)\n" +
            "• Emotional Intensity: Passion level (Fischer: High, Carlsen: Medium)\n" +
            "• Drama Factor: Commentary excitement (Tal: 2.0x, others: 1.0x)\n\n" +
            "🎤 VOICE SETTINGS:\n" +
            "• Use appropriate voice style for each master\n" +
            "• Add accent instructions for authenticity\n" +
            "• Include signature phrases they would actually say\n\n" +
            "🤖 API INTEGRATION:\n" +
            "• Enable Assistant mode for masters with complex databases\n" +
            "• Tal, Fischer, and Carlsen use assistants with vector stores"
        );
        dialog.showAndWait();
    }
    
    private void exportPersonalityProfile() {
        String masterKey = masterSelector.getValue();
        if (masterKey == null) return;
        
        PersonalityProfile profile = personalityProfiles.get(masterKey);
        if (profile == null) {
            statusLabel.setText("❌ No profile to export for " + masterKey);
            return;
        }
        
        // In a real implementation, this would save to a file
        statusLabel.setText("📤 Exported profile for " + profile.masterName + " (functionality coming soon)");
        logger.info("📤 Exported personality profile for {}", masterKey);
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    // Data class for personality profiles
    public static class PersonalityProfile {
        public String masterName = "";
        public String description = "";
        public double aggression = 1.0;
        public double creativity = 1.0;
        public double analysisDepth = 1.0;
        public double emotionalIntensity = 1.0;
        public double dramaFactor = 1.0;
        public boolean voiceEnabled = true;
        public String elevenLabsVoiceId = "Adam (pNInz6obpgDQGcFmaJgB)";
        public String customVoiceId = "";
        public String elevenLabsModel = "eleven_flash_v2_5";
        public double stability = 0.5;
        public double similarityBoost = 0.75;
        public String outputFormat = "mp3_44100_128";
        public boolean emotionalContagionEnabled = true;
        public double relationshipFactor = 1.0;
        public String conversationStyle = "";
        public String signaturePhrases = "";
        public boolean assistantMode = false;
        public String vectorStoreId = "";
    }
}