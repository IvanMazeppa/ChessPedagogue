package com.chesspedagogue.configurator.tabs;

import com.chesspedagogue.configurator.models.*;
import com.chesspedagogue.configurator.managers.ConfigurationManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TemplateLibraryTab - Comprehensive template management interface
 * 
 * Features:
 * - Hierarchical template organization with categories
 * - Search and filtering capabilities
 * - Template CRUD operations (Create, Read, Update, Delete)
 * - Import/Export functionality
 * - Batch operations for multiple templates
 * - Live preview of template properties
 */
public class TemplateLibraryTab extends Tab {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateLibraryTab.class);
    
    // Core components
    private final ConfigurationManager configManager;
    
    // UI Components
    private TreeView<Object> categoryTree;
    private ListView<ConversationTemplate> templateList;
    private TextField searchField;
    private ComboBox<String> filterComboBox;
    private TextArea templatePreview;
    private Label statusLabel;
    
    // Template editing
    private VBox templateEditor;
    private TextField nameField;
    private TextArea descriptionField;
    private ComboBox<ConversationIntensity> intensityComboBox;
    private ListView<String> mastersList;
    private TextField tagsField;
    
    // Action buttons
    private Button createButton, duplicateButton, deleteButton;
    private Button importButton, exportButton;
    private Button saveButton, cancelButton;
    
    // Data
    private final ObservableList<TemplateCategory> categories;
    private final ObservableList<ConversationTemplate> templates;
    private final ObservableList<ConversationTemplate> filteredTemplates;
    private ConversationTemplate currentTemplate;
    private boolean isEditing = false;
    
    public TemplateLibraryTab(ConfigurationManager configManager) {
        super("📚 Template Library");
        this.configManager = configManager;
        this.categories = FXCollections.observableArrayList();
        this.templates = FXCollections.observableArrayList();
        this.filteredTemplates = FXCollections.observableArrayList();
        
        initializeData();
        createUI();
        setupEventHandlers();
        refreshTemplateList();
        
        logger.info("📚 Template Library tab initialized with {} categories, {} templates", 
                   categories.size(), templates.size());
    }
    
    private void initializeData() {
        // Load system categories
        categories.addAll(TemplateCategory.getSystemCategories());
        
        // Load existing templates from configuration
        loadTemplatesFromConfig();
        
        // If no templates exist, create some examples
        if (templates.isEmpty()) {
            createExampleTemplates();
        }
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Top: Search and filters
        HBox topControls = createTopControls();
        root.setTop(topControls);
        
        // Center: Main content split pane
        SplitPane centerSplit = createCenterContent();
        root.setCenter(centerSplit);
        
        // Bottom: Status and actions
        HBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);
        
        setContent(root);
    }
    
    private HBox createTopControls() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(0, 0, 10, 0));
        controls.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        Label searchLabel = new Label("🔍 Search:");
        searchLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        searchField = new TextField();
        searchField.setPromptText("Search templates by name, description, or tags...");
        searchField.setPrefWidth(300);
        
        // Filter dropdown
        Label filterLabel = new Label("Filter:");
        filterLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll(
            "All Templates", "Recently Used", "High Success Rate", 
            "Opening Discussions", "Tactical Battles", "Endgame Philosophy",
            "Master Rivalries", "Teaching Moments", "Historical References"
        );
        filterComboBox.setValue("All Templates");
        filterComboBox.setPrefWidth(150);
        
        // Action buttons
        createButton = new Button("➕ New Template");
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        importButton = new Button("📥 Import");
        importButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        exportButton = new Button("📤 Export");
        exportButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        
        controls.getChildren().addAll(
            searchLabel, searchField,
            new Separator(Orientation.VERTICAL),
            filterLabel, filterComboBox,
            new Separator(Orientation.VERTICAL),
            createButton, importButton, exportButton
        );
        
        return controls;
    }
    
    private SplitPane createCenterContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        
        // Left: Category tree and template list
        VBox leftPanel = createLeftPanel();
        
        // Right: Template editor and preview
        VBox rightPanel = createRightPanel();
        
        splitPane.getItems().addAll(leftPanel, rightPanel);
        splitPane.setDividerPositions(0.4);
        
        return splitPane;
    }
    
    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(400);
        
        // Category tree
        Label treeLabel = new Label("📁 Categories");
        treeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        categoryTree = new TreeView<>();
        categoryTree.setPrefHeight(200);
        categoryTree.setShowRoot(false);
        setupCategoryTree();
        
        // Template list
        Label listLabel = new Label("📋 Templates");
        listLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        templateList = new ListView<>();
        templateList.setPrefHeight(300);
        templateList.setItems(filteredTemplates);
        setupTemplateList();
        
        // Template actions
        HBox templateActions = createTemplateActions();
        
        panel.getChildren().addAll(
            treeLabel, categoryTree,
            new Separator(),
            listLabel, templateList,
            templateActions
        );
        
        return panel;
    }
    
    private VBox createRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(500);
        
        // Template editor
        Label editorLabel = new Label("✏️ Template Editor");
        editorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        templateEditor = createTemplateEditor();
        
        // Template preview
        Label previewLabel = new Label("👁️ Preview");
        previewLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        templatePreview = new TextArea();
        templatePreview.setEditable(false);
        templatePreview.setPrefHeight(150);
        templatePreview.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        
        // Editor actions
        HBox editorActions = createEditorActions();
        
        panel.getChildren().addAll(
            editorLabel, templateEditor,
            new Separator(),
            previewLabel, templatePreview,
            editorActions
        );
        
        return panel;
    }
    
    private VBox createTemplateEditor() {
        VBox editor = new VBox(10);
        editor.setPadding(new Insets(10));
        editor.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        
        // Basic properties
        GridPane basicProps = new GridPane();
        basicProps.setHgap(10);
        basicProps.setVgap(8);
        
        // Name
        basicProps.add(new Label("Name:"), 0, 0);
        nameField = new TextField();
        nameField.setPromptText("Enter template name...");
        basicProps.add(nameField, 1, 0);
        
        // Description
        basicProps.add(new Label("Description:"), 0, 1);
        descriptionField = new TextArea();
        descriptionField.setPromptText("Describe when and how this template should be used...");
        descriptionField.setPrefRowCount(3);
        basicProps.add(descriptionField, 1, 1);
        
        // Intensity
        basicProps.add(new Label("Intensity:"), 0, 2);
        intensityComboBox = new ComboBox<>();
        intensityComboBox.getItems().addAll(ConversationIntensity.values());
        intensityComboBox.setValue(ConversationIntensity.MODERATE);
        basicProps.add(intensityComboBox, 1, 2);
        
        // Compatible Masters
        basicProps.add(new Label("Masters:"), 0, 3);
        mastersList = new ListView<>();
        mastersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mastersList.getItems().addAll("tal", "fischer", "carlsen", "kasparov", "alekhine", 
                                      "capablanca", "kramnik", "karpov", "anand", "lasker", "morphy", "botvinnik");
        mastersList.setPrefHeight(120);
        basicProps.add(mastersList, 1, 3);
        
        // Tags
        basicProps.add(new Label("Tags:"), 0, 4);
        tagsField = new TextField();
        tagsField.setPromptText("Enter tags separated by commas...");
        basicProps.add(tagsField, 1, 4);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        basicProps.getColumnConstraints().addAll(col1, col2);
        
        editor.getChildren().add(basicProps);
        editor.setDisable(true); // Initially disabled
        
        return editor;
    }
    
    private void setupCategoryTree() {
        TreeItem<Object> root = new TreeItem<>("Root");
        
        for (TemplateCategory category : categories) {
            TreeItem<Object> categoryItem = new TreeItem<>(category);
            root.getChildren().add(categoryItem);
            
            // Add subcategories if any
            for (TemplateCategory child : category.getChildren()) {
                TreeItem<Object> childItem = new TreeItem<>(child);
                categoryItem.getChildren().add(childItem);
            }
        }
        
        categoryTree.setRoot(root);
        categoryTree.setCellFactory(tv -> new TreeCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof TemplateCategory) {
                    TemplateCategory category = (TemplateCategory) item;
                    setText(category.toString());
                } else {
                    setText(item.toString());
                }
            }
        });
    }
    
    private void setupTemplateList() {
        templateList.setCellFactory(listView -> new ListCell<ConversationTemplate>() {
            @Override
            protected void updateItem(ConversationTemplate template, boolean empty) {
                super.updateItem(template, empty);
                if (empty || template == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox content = new VBox(2);
                    
                    Label nameLabel = new Label(template.getName());
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                    
                    Label detailsLabel = new Label(String.format(
                        "%d-%d turns • %.0f%% response • %s",
                        template.getMinTurns(), template.getMaxTurns(),
                        template.getResponseChance() * 100,
                        template.getIntensity() != null ? template.getIntensity().getDisplayName() : "Default"
                    ));
                    detailsLabel.setFont(Font.font("System", 10));
                    detailsLabel.setTextFill(Color.GRAY);
                    
                    if (template.getDescription() != null && !template.getDescription().isEmpty()) {
                        Label descLabel = new Label(template.getDescription());
                        descLabel.setFont(Font.font("System", 10));
                        descLabel.setWrapText(true);
                        descLabel.setMaxWidth(350);
                        content.getChildren().add(descLabel);
                    }
                    
                    content.getChildren().addAll(nameLabel, detailsLabel);
                    setGraphic(content);
                }
            }
        });
    }
    
    private HBox createTemplateActions() {
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(10, 0, 0, 0));
        
        duplicateButton = new Button("📄 Duplicate");
        duplicateButton.setDisable(true);
        
        deleteButton = new Button("🗑️ Delete");
        deleteButton.setDisable(true);
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        actions.getChildren().addAll(duplicateButton, deleteButton);
        return actions;
    }
    
    private HBox createEditorActions() {
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(10, 0, 0, 0));
        
        saveButton = new Button("💾 Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setDisable(true);
        
        cancelButton = new Button("❌ Cancel");
        cancelButton.setDisable(true);
        
        actions.getChildren().addAll(cancelButton, saveButton);
        return actions;
    }
    
    private HBox createBottomControls() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(10, 0, 0, 0));
        controls.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready - " + templates.size() + " templates loaded");
        statusLabel.setStyle("-fx-text-fill: green;");
        
        Label statsLabel = new Label("📊 Usage Stats Available");
        statsLabel.setStyle("-fx-text-fill: #666;");
        
        controls.getChildren().addAll(statusLabel, new Separator(Orientation.VERTICAL), statsLabel);
        return controls;
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> filterTemplates());
        filterComboBox.setOnAction(e -> filterTemplates());
        
        // Category tree selection
        categoryTree.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && newItem.getValue() instanceof TemplateCategory) {
                TemplateCategory category = (TemplateCategory) newItem.getValue();
                showTemplatesForCategory(category);
            }
        });
        
        // Template list selection
        templateList.getSelectionModel().selectedItemProperty().addListener((obs, oldTemplate, newTemplate) -> {
            selectTemplate(newTemplate);
        });
        
        // Template editor changes
        nameField.textProperty().addListener((obs, oldText, newText) -> updatePreview());
        descriptionField.textProperty().addListener((obs, oldText, newText) -> updatePreview());
        intensityComboBox.setOnAction(e -> updatePreview());
        
        // Action buttons
        createButton.setOnAction(e -> createNewTemplate());
        duplicateButton.setOnAction(e -> duplicateTemplate());
        deleteButton.setOnAction(e -> deleteTemplate());
        importButton.setOnAction(e -> importTemplates());
        exportButton.setOnAction(e -> exportTemplates());
        saveButton.setOnAction(e -> saveTemplate());
        cancelButton.setOnAction(e -> cancelEditing());
    }
    
    private void loadTemplatesFromConfig() {
        // This would load from the configuration manager
        // For now, we'll create some example templates
        logger.info("Loading templates from configuration...");
    }
    
    private void createExampleTemplates() {
        // Create example templates for each category
        List<TemplateCategory> systemCategories = TemplateCategory.getSystemCategories();
        
        for (TemplateCategory category : systemCategories) {
            categories.add(category);
            
            // Create 2-3 example templates per category
            switch (category.getName()) {
                case "Opening Discussions":
                    createExampleTemplate(category, "Opening Theory Debate", 
                        "Masters discuss opening principles and theory", 
                        3, 5, 0.8, 2500, ConversationIntensity.MODERATE);
                    createExampleTemplate(category, "Surprise Opening", 
                        "Reaction to unexpected opening choices", 
                        2, 4, 0.9, 2000, ConversationIntensity.INTENSE);
                    break;
                    
                case "Tactical Battles":
                    createExampleTemplate(category, "Tactical Storm", 
                        "High-intensity tactical sequence discussion", 
                        4, 8, 0.95, 1800, ConversationIntensity.HEATED);
                    createExampleTemplate(category, "Brilliant Combination", 
                        "Appreciation of exceptional tactical play", 
                        3, 6, 0.85, 2200, ConversationIntensity.INTENSE);
                    break;
                    
                case "Endgame Philosophy":
                    createExampleTemplate(category, "Endgame Mastery", 
                        "Deep discussion of endgame principles", 
                        4, 7, 0.75, 3000, ConversationIntensity.CALM);
                    createExampleTemplate(category, "Technique vs Intuition", 
                        "Debate over endgame approach", 
                        3, 6, 0.8, 2800, ConversationIntensity.MODERATE);
                    break;
                    
                default:
                    createExampleTemplate(category, "General Discussion", 
                        "Standard conversation for " + category.getName(), 
                        3, 5, 0.75, 2500, ConversationIntensity.MODERATE);
            }
        }
    }
    
    private void createExampleTemplate(TemplateCategory category, String name, String description, 
                                     int minTurns, int maxTurns, double responseChance, 
                                     int intervalMs, ConversationIntensity intensity) {
        ConversationTemplate template = new ConversationTemplate(name, minTurns, maxTurns, responseChance, intervalMs);
        template.setDescription(description);
        template.setIntensity(intensity);
        template.setAuthor("System");
        template.setReadOnly(true);
        
        category.addTemplate(template);
        templates.add(template);
    }
    
    private void filterTemplates() {
        String searchText = searchField.getText().toLowerCase();
        String filter = filterComboBox.getValue();
        
        List<ConversationTemplate> filtered = templates.stream()
            .filter(template -> {
                boolean matchesSearch = searchText.isEmpty() || 
                    template.getName().toLowerCase().contains(searchText) ||
                    (template.getDescription() != null && template.getDescription().toLowerCase().contains(searchText)) ||
                    template.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(searchText));
                
                boolean matchesFilter = "All Templates".equals(filter) || 
                    (template.getCategory() != null && filter.equals(template.getCategory().getName()));
                
                return matchesSearch && matchesFilter;
            })
            .collect(Collectors.toList());
        
        Platform.runLater(() -> {
            filteredTemplates.clear();
            filteredTemplates.addAll(filtered);
            statusLabel.setText(String.format("Showing %d of %d templates", filtered.size(), templates.size()));
        });
    }
    
    private void showTemplatesForCategory(TemplateCategory category) {
        List<ConversationTemplate> categoryTemplates = category.getAllTemplates();
        Platform.runLater(() -> {
            filteredTemplates.clear();
            filteredTemplates.addAll(categoryTemplates);
            statusLabel.setText(String.format("Category: %s (%d templates)", 
                               category.getName(), categoryTemplates.size()));
        });
    }
    
    private void selectTemplate(ConversationTemplate template) {
        currentTemplate = template;
        
        if (template != null) {
            // Enable template actions
            duplicateButton.setDisable(false);
            deleteButton.setDisable(template.isReadOnly());
            
            // Load template into editor
            loadTemplateIntoEditor(template);
            updatePreview();
            
            // Enable editing if not read-only
            if (!template.isReadOnly()) {
                enableEditing();
            }
        } else {
            // Disable actions
            duplicateButton.setDisable(true);
            deleteButton.setDisable(true);
            disableEditing();
        }
    }
    
    private void loadTemplateIntoEditor(ConversationTemplate template) {
        nameField.setText(template.getName());
        descriptionField.setText(template.getDescription());
        intensityComboBox.setValue(template.getIntensity() != null ? template.getIntensity() : ConversationIntensity.MODERATE);
        
        // Select compatible masters
        mastersList.getSelectionModel().clearSelection();
        for (String master : template.getCompatibleMasters()) {
            int index = mastersList.getItems().indexOf(master);
            if (index >= 0) {
                mastersList.getSelectionModel().select(index);
            }
        }
        
        // Set tags
        tagsField.setText(String.join(", ", template.getTags()));
    }
    
    private void enableEditing() {
        if (currentTemplate != null && !currentTemplate.isReadOnly()) {
            templateEditor.setDisable(false);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
            isEditing = true;
        }
    }
    
    private void disableEditing() {
        templateEditor.setDisable(true);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        isEditing = false;
    }
    
    private void updatePreview() {
        if (currentTemplate == null) {
            templatePreview.clear();
            return;
        }
        
        StringBuilder preview = new StringBuilder();
        preview.append("📋 TEMPLATE PREVIEW\n");
        preview.append("==================\n\n");
        preview.append("Name: ").append(nameField.getText()).append("\n");
        preview.append("Description: ").append(descriptionField.getText()).append("\n");
        preview.append("Intensity: ").append(intensityComboBox.getValue()).append("\n");
        preview.append("Turns: ").append(currentTemplate.getMinTurns()).append("-").append(currentTemplate.getMaxTurns()).append("\n");
        preview.append("Response Rate: ").append(String.format("%.1f%%", currentTemplate.getResponseChance() * 100)).append("\n");
        preview.append("Interval: ").append(currentTemplate.getIntervalMs()).append("ms\n");
        
        List<String> selectedMasters = mastersList.getSelectionModel().getSelectedItems();
        if (!selectedMasters.isEmpty()) {
            preview.append("Compatible Masters: ").append(String.join(", ", selectedMasters)).append("\n");
        }
        
        String tags = tagsField.getText();
        if (!tags.trim().isEmpty()) {
            preview.append("Tags: ").append(tags).append("\n");
        }
        
        preview.append("\n📊 USAGE STATISTICS\n");
        preview.append("Uses: ").append(currentTemplate.getUsageCount()).append("\n");
        preview.append("Success Rate: ").append(String.format("%.1f%%", currentTemplate.getSuccessRate() * 100)).append("\n");
        preview.append("Created: ").append(currentTemplate.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        
        templatePreview.setText(preview.toString());
    }
    
    private void refreshTemplateList() {
        filterTemplates();
        statusLabel.setText("Ready - " + templates.size() + " templates loaded");
    }
    
    // Action methods
    private void createNewTemplate() {
        ConversationTemplate newTemplate = new ConversationTemplate();
        newTemplate.setName("New Template");
        newTemplate.setDescription("Enter description...");
        newTemplate.setIntensity(ConversationIntensity.MODERATE);
        
        templates.add(newTemplate);
        
        Platform.runLater(() -> {
            refreshTemplateList();
            templateList.getSelectionModel().select(newTemplate);
            nameField.selectAll();
            nameField.requestFocus();
        });
        
        statusLabel.setText("✅ New template created");
    }
    
    private void duplicateTemplate() {
        if (currentTemplate != null) {
            ConversationTemplate duplicate = currentTemplate.duplicate();
            templates.add(duplicate);
            
            Platform.runLater(() -> {
                refreshTemplateList();
                templateList.getSelectionModel().select(duplicate);
            });
            
            statusLabel.setText("✅ Template duplicated: " + duplicate.getName());
        }
    }
    
    private void deleteTemplate() {
        if (currentTemplate != null && !currentTemplate.isReadOnly()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Template");
            confirmation.setHeaderText("Delete Template: " + currentTemplate.getName());
            confirmation.setContentText("Are you sure you want to delete this template? This action cannot be undone.");
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                templates.remove(currentTemplate);
                
                if (currentTemplate.getCategory() != null) {
                    currentTemplate.getCategory().removeTemplate(currentTemplate);
                }
                
                Platform.runLater(() -> {
                    refreshTemplateList();
                    templateList.getSelectionModel().clearSelection();
                });
                
                statusLabel.setText("🗑️ Template deleted");
            }
        }
    }
    
    private void saveTemplate() {
        if (currentTemplate != null && isEditing) {
            // Update template with editor values
            currentTemplate.setName(nameField.getText());
            currentTemplate.setDescription(descriptionField.getText());
            currentTemplate.setIntensity(intensityComboBox.getValue());
            
            // Update compatible masters
            List<String> selectedMasters = new ArrayList<>(mastersList.getSelectionModel().getSelectedItems());
            currentTemplate.setCompatibleMasters(selectedMasters);
            
            // Update tags
            String[] tags = tagsField.getText().split(",");
            List<String> tagList = new ArrayList<>();
            for (String tag : tags) {
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    tagList.add(trimmed);
                }
            }
            currentTemplate.setTags(tagList);
            
            // Save to configuration
            configManager.saveTemplateLibrary(templates);
            
            Platform.runLater(() -> {
                refreshTemplateList();
                updatePreview();
            });
            
            statusLabel.setText("💾 Template saved: " + currentTemplate.getName());
        }
    }
    
    private void cancelEditing() {
        if (currentTemplate != null) {
            loadTemplateIntoEditor(currentTemplate);
            updatePreview();
            statusLabel.setText("❌ Changes cancelled");
        }
    }
    
    private void importTemplates() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Templates");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        File file = fileChooser.showOpenDialog(getTabPane().getScene().getWindow());
        if (file != null) {
            // TODO: Implement template import
            statusLabel.setText("📥 Import functionality coming soon...");
        }
    }
    
    private void exportTemplates() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Templates");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        File file = fileChooser.showSaveDialog(getTabPane().getScene().getWindow());
        if (file != null) {
            // TODO: Implement template export
            statusLabel.setText("📤 Export functionality coming soon...");
        }
    }
}