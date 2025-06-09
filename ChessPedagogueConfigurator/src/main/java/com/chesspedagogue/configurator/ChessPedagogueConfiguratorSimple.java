package com.chesspedagogue.configurator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.chesspedagogue.configurator.tabs.ConversationTemplateTabSimple;
import com.chesspedagogue.configurator.tabs.TemplateLibraryTab;
import com.chesspedagogue.configurator.tabs.MasterPersonalityTab;
import com.chesspedagogue.configurator.tabs.GameFlowTab;
import com.chesspedagogue.configurator.tabs.LiveMonitorTab;
import com.chesspedagogue.configurator.tabs.AdvancedSettingsTab;
import com.chesspedagogue.configurator.tabs.ConversationAnalyticsTab;
import com.chesspedagogue.configurator.managers.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.CheckMenuItem;

public class ChessPedagogueConfiguratorSimple extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(ChessPedagogueConfiguratorSimple.class);
    private ConfigurationManager configManager;
    private Scene scene;
    private boolean isDarkMode = false;
    
    // Core tabs
    private ConversationTemplateTabSimple conversationTab;
    private TemplateLibraryTab templateLibraryTab;
    private MasterPersonalityTab personalityTab;
    private GameFlowTab gameFlowTab;
    private LiveMonitorTab liveMonitorTab;
    private AdvancedSettingsTab advancedTab;
    private ConversationAnalyticsTab analyticsTab;
    
    @Override
    public void start(Stage primaryStage) {
        logger.info("🎮 Starting Chess Pedagogue Configurator...");
        
        try {
            // Initialize configuration manager
            configManager = ConfigurationManager.getInstance();
            isDarkMode = configManager.isDarkMode();
            
            // Create main application layout
            BorderPane root = new BorderPane();
            
            // Create menu bar
            MenuBar menuBar = createMenuBar();
            root.setTop(menuBar);
            
            // Create main content area with tabs
            TabPane tabPane = createMainTabPane();
            root.setCenter(tabPane);
            
            // Create status bar
            Label statusBar = new Label("✅ Chess Pedagogue Configurator Ready");
            statusBar.setStyle("-fx-padding: 5px; -fx-background-color: #f0f0f0;");
            root.setBottom(statusBar);
            
            // Setup main scene
            scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            // Apply dark mode if enabled
            if (isDarkMode) {
                scene.getRoot().getStyleClass().add("dark-theme");
            }
            
            primaryStage.setTitle("🎮 Chess Pedagogue Configurator v1.0");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setOnCloseRequest(e -> {
                logger.info("👋 Shutting down Chess Pedagogue Configurator");
                configManager.saveConfiguration();
            });
            
            primaryStage.show();
            logger.info("✅ Chess Pedagogue Configurator started successfully!");
            
        } catch (Exception e) {
            logger.error("❌ Failed to start application", e);
            showErrorAlert("Startup Error", "Failed to start Chess Pedagogue Configurator", e.getMessage());
        }
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("💾 Save Configuration");
        saveItem.setOnAction(e -> configManager.saveConfiguration());
        
        MenuItem loadItem = new MenuItem("📂 Load Configuration");
        loadItem.setOnAction(e -> configManager.loadConfiguration());
        
        MenuItem exportItem = new MenuItem("📤 Export for Android");
        exportItem.setOnAction(e -> configManager.exportForAndroid());
        
        MenuItem exitItem = new MenuItem("🚪 Exit");
        exitItem.setOnAction(e -> {
            configManager.saveConfiguration();
            System.exit(0);
        });
        
        fileMenu.getItems().addAll(saveItem, loadItem, exportItem, new SeparatorMenuItem(), exitItem);
        
        // Edit menu
        Menu editMenu = new Menu("Edit");
        MenuItem resetItem = new MenuItem("🔄 Reset to Defaults");
        resetItem.setOnAction(e -> configManager.resetToDefaults());
        
        editMenu.getItems().add(resetItem);
        
        // View menu with theme options
        Menu viewMenu = new Menu("View");
        CheckMenuItem darkModeItem = new CheckMenuItem("🌙 Dark Mode");
        darkModeItem.setSelected(isDarkMode);
        darkModeItem.setOnAction(e -> toggleDarkMode());
        
        viewMenu.getItems().add(darkModeItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("ℹ️ About");
        aboutItem.setOnAction(e -> showAbout());
        
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
        return menuBar;
    }
    
    private TabPane createMainTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Create tabs
        conversationTab = new ConversationTemplateTabSimple(configManager);
        templateLibraryTab = new TemplateLibraryTab(configManager);
        personalityTab = new MasterPersonalityTab();
        gameFlowTab = new GameFlowTab();
        liveMonitorTab = new LiveMonitorTab();
        advancedTab = new AdvancedSettingsTab();
        analyticsTab = new ConversationAnalyticsTab();
        
        // Add tabs to pane
        tabPane.getTabs().addAll(
            conversationTab.getTab(),
            templateLibraryTab,
            personalityTab,
            gameFlowTab,
            liveMonitorTab,
            analyticsTab,
            advancedTab
        );
        
        return tabPane;
    }
    
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Chess Pedagogue Configurator");
        alert.setHeaderText("🎮 Chess Pedagogue Configurator v1.0");
        alert.setContentText(
            "Professional configuration tool for the Chess Pedagogue Android game.\\n\\n" +
            "Features:\\n" +
            "• 🎭 Conversation Template Designer\\n" +
            "• 📚 Template Library Management\\n" +
            "• 👥 Master Personality Editor\\n" +
            "• ⚙️ Game Flow Controller\\n" +
            "• 📊 Live Game Monitoring\\n" +
            "• 📈 Conversation Analytics & Sophistication Tracking\\n" +
            "• 🔧 Advanced Settings\\n\\n" +
            "Created with JavaFX and Maven\\n" +
            "© 2024 Chess Pedagogue Project"
        );
        alert.showAndWait();
    }
    
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        
        if (isDarkMode) {
            scene.getRoot().getStyleClass().add("dark-theme");
            logger.info("🌙 Switched to dark mode");
        } else {
            scene.getRoot().getStyleClass().remove("dark-theme");
            logger.info("☀️ Switched to light mode");
        }
        
        // Save theme preference
        configManager.setDarkMode(isDarkMode);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}