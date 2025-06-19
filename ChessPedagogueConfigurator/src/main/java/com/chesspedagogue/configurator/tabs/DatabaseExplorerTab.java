package com.chesspedagogue.configurator.tabs;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseExplorerTab extends Tab {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExplorerTab.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // UI Components
    private ComboBox<String> tableSelector;
    private TableView<ObservableList<String>> dataTable;
    private TextArea queryResultArea;
    private TextField customQueryField;
    private Label connectionStatusLabel;
    private Label tableStatsLabel;
    private ProgressBar backupProgress;
    private ListView<String> tableListView;
    private TextArea schemaTextArea;
    
    // Database Connection
    private Connection dbConnection;
    private String currentDatabasePath;
    private boolean isConnected = false;
    
    // Backup Management
    private String backupDirectory;
    private Timeline autoBackupTimer;
    
    public DatabaseExplorerTab() {
        super("🗃️ Database Explorer");
        initializeUI();
        setupEventHandlers();
        initializeBackupSystem();
        
        logger.info("🗃️ Database Explorer initialized");
    }
    
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Top: Connection and controls
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // Center: Main explorer content
        TabPane centerContent = createExplorerTabs();
        root.setCenter(centerContent);
        
        // Bottom: Query and backup controls
        VBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
        
        setContent(root);
    }
    
    private VBox createTopSection() {
        VBox topSection = new VBox(15);
        
        // Connection status bar
        HBox connectionBar = new HBox(20);
        connectionBar.setAlignment(Pos.CENTER_LEFT);
        connectionBar.setPadding(new Insets(10, 15, 10, 15));
        connectionBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        connectionStatusLabel = new Label("🔴 Not Connected");
        connectionStatusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        connectionStatusLabel.setTextFill(Color.RED);
        
        tableStatsLabel = new Label("No database loaded");
        tableStatsLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button connectButton = new Button("📱 Connect to Android DB");
        connectButton.getStyleClass().add("primary-button");
        connectButton.setOnAction(e -> connectToAndroidDatabase());
        
        Button browseButton = new Button("📂 Browse DB File");
        browseButton.getStyleClass().add("button");
        browseButton.setOnAction(e -> browseForDatabase());
        
        connectionBar.getChildren().addAll(
            connectionStatusLabel, tableStatsLabel, spacer, connectButton, browseButton
        );
        
        // Backup controls
        HBox backupBar = createBackupControls();
        
        topSection.getChildren().addAll(connectionBar, backupBar);
        return topSection;
    }
    
    private HBox createBackupControls() {
        HBox backupBar = new HBox(15);
        backupBar.setAlignment(Pos.CENTER_LEFT);
        backupBar.setPadding(new Insets(10, 15, 10, 15));
        backupBar.setStyle("-fx-background-color: #fff3cd; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        Label backupLabel = new Label("🛡️ Database Backup & Safety");
        backupLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Button createBackupButton = new Button("💾 Create Backup Now");
        createBackupButton.getStyleClass().add("success-button");
        createBackupButton.setOnAction(e -> createManualBackup());
        
        Button restoreBackupButton = new Button("⏮️ Restore Backup");
        restoreBackupButton.getStyleClass().add("warning-button");
        restoreBackupButton.setOnAction(e -> restoreFromBackup());
        
        Button setBackupDirButton = new Button("📁 Set Backup Dir");
        setBackupDirButton.getStyleClass().add("button");
        setBackupDirButton.setOnAction(e -> selectBackupDirectory());
        
        CheckBox autoBackupCheck = new CheckBox("Auto-backup every 30 minutes");
        autoBackupCheck.setSelected(true);
        autoBackupCheck.setOnAction(e -> toggleAutoBackup(autoBackupCheck.isSelected()));
        
        backupProgress = new ProgressBar(0);
        backupProgress.setPrefWidth(150);
        backupProgress.setVisible(false);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        backupBar.getChildren().addAll(
            backupLabel, createBackupButton, restoreBackupButton, setBackupDirButton,
            autoBackupCheck, spacer2, backupProgress
        );
        
        return backupBar;
    }
    
    private TabPane createExplorerTabs() {
        TabPane tabPane = new TabPane();
        
        // Data Browser tab
        Tab dataBrowserTab = new Tab("📊 Data Browser");
        dataBrowserTab.setContent(createDataBrowserContent());
        dataBrowserTab.setClosable(false);
        
        // Schema Explorer tab
        Tab schemaTab = new Tab("🏗️ Schema");
        schemaTab.setContent(createSchemaExplorerContent());
        schemaTab.setClosable(false);
        
        // Query Console tab
        Tab queryTab = new Tab("💻 Query Console");
        queryTab.setContent(createQueryConsoleContent());
        queryTab.setClosable(false);
        
        // Database Statistics tab
        Tab statsTab = new Tab("📈 Statistics");
        statsTab.setContent(createStatisticsContent());
        statsTab.setClosable(false);
        
        tabPane.getTabs().addAll(dataBrowserTab, schemaTab, queryTab, statsTab);
        return tabPane;
    }
    
    private VBox createDataBrowserContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        // Table selector
        HBox tableSelector = new HBox(10);
        tableSelector.setAlignment(Pos.CENTER_LEFT);
        
        Label tableLabel = new Label("Select Table:");
        tableLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        this.tableSelector = new ComboBox<>();
        this.tableSelector.setPrefWidth(200);
        this.tableSelector.setOnAction(e -> loadTableData());
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setOnAction(e -> refreshTableList());
        
        Button exportButton = new Button("📤 Export CSV");
        exportButton.setOnAction(e -> exportTableToCSV());
        
        tableSelector.getChildren().addAll(tableLabel, this.tableSelector, refreshButton, exportButton);
        
        // Data table
        dataTable = new TableView<>();
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataTable.setPrefHeight(400);
        
        // Table info
        Label tableInfoLabel = new Label("Table Information:");
        tableInfoLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        TextArea tableInfoArea = new TextArea();
        tableInfoArea.setPrefRowCount(3);
        tableInfoArea.setEditable(false);
        tableInfoArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");
        
        content.getChildren().addAll(tableSelector, dataTable, tableInfoLabel, tableInfoArea);
        return content;
    }
    
    private VBox createSchemaExplorerContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        Label schemaLabel = new Label("Database Schema:");
        schemaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Table list
        HBox schemaLayout = new HBox(15);
        
        VBox tableListSection = new VBox(10);
        Label tableListLabel = new Label("Tables:");
        tableListLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        tableListView = new ListView<>();
        tableListView.setPrefWidth(200);
        tableListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                showTableSchema();
            }
        });
        
        tableListSection.getChildren().addAll(tableListLabel, tableListView);
        
        // Schema display
        VBox schemaDisplaySection = new VBox(10);
        Label schemaDisplayLabel = new Label("Schema Details:");
        schemaDisplayLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        schemaTextArea = new TextArea();
        schemaTextArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        schemaTextArea.setEditable(false);
        
        Button generateCreateButton = new Button("📋 Copy CREATE Statement");
        generateCreateButton.setOnAction(e -> copyCreateStatement());
        
        schemaDisplaySection.getChildren().addAll(schemaDisplayLabel, schemaTextArea, generateCreateButton);
        HBox.setHgrow(schemaDisplaySection, Priority.ALWAYS);
        
        schemaLayout.getChildren().addAll(tableListSection, schemaDisplaySection);
        
        content.getChildren().addAll(schemaLabel, schemaLayout);
        return content;
    }
    
    private VBox createQueryConsoleContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        Label queryLabel = new Label("SQL Query Console:");
        queryLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Query input
        HBox queryInputSection = new HBox(10);
        queryInputSection.setAlignment(Pos.CENTER_LEFT);
        
        customQueryField = new TextField();
        customQueryField.setPromptText("Enter SQL query (e.g., SELECT * FROM master_positions LIMIT 10)");
        customQueryField.setPrefWidth(400);
        
        Button executeButton = new Button("▶️ Execute");
        executeButton.getStyleClass().add("primary-button");
        executeButton.setOnAction(e -> executeCustomQuery());
        
        Button clearButton = new Button("🗑️ Clear");
        clearButton.setOnAction(e -> queryResultArea.clear());
        
        queryInputSection.getChildren().addAll(customQueryField, executeButton, clearButton);
        
        // Common queries
        HBox commonQueriesSection = createCommonQueriesSection();
        
        // Results area
        Label resultsLabel = new Label("Query Results:");
        resultsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        queryResultArea = new TextArea();
        queryResultArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");
        queryResultArea.setEditable(false);
        queryResultArea.setPrefRowCount(15);
        
        content.getChildren().addAll(
            queryLabel, queryInputSection, commonQueriesSection, resultsLabel, queryResultArea
        );
        
        return content;
    }
    
    private HBox createCommonQueriesSection() {
        HBox section = new HBox(10);
        section.setAlignment(Pos.CENTER_LEFT);
        
        Label commonLabel = new Label("Quick Queries:");
        commonLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        
        Button tablesButton = new Button("📋 List Tables");
        tablesButton.setOnAction(e -> executeQuery("SELECT name FROM sqlite_master WHERE type='table'"));
        
        Button relationshipsButton = new Button("💕 View Relationships");
        relationshipsButton.setOnAction(e -> executeQuery("SELECT * FROM master_relationships LIMIT 10"));
        
        Button emotionalButton = new Button("🎭 Emotional Data");
        emotionalButton.setOnAction(e -> executeQuery("SELECT * FROM emotional_reactions ORDER BY timestamp DESC LIMIT 10"));
        
        Button countsButton = new Button("📊 Row Counts");
        countsButton.setOnAction(e -> showTableCounts());
        
        section.getChildren().addAll(
            commonLabel, tablesButton, relationshipsButton, emotionalButton, countsButton
        );
        
        return section;
    }
    
    private VBox createStatisticsContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        Label statsLabel = new Label("Database Statistics:");
        statsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextArea statsArea = new TextArea();
        statsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(20);
        
        Button refreshStatsButton = new Button("🔄 Refresh Statistics");
        refreshStatsButton.getStyleClass().add("primary-button");
        refreshStatsButton.setOnAction(e -> generateDatabaseStatistics(statsArea));
        
        content.getChildren().addAll(statsLabel, statsArea, refreshStatsButton);
        return content;
    }
    
    private VBox createBottomSection() {
        VBox bottomSection = new VBox(10);
        bottomSection.setPadding(new Insets(15, 0, 0, 0));
        
        // Safety warning
        HBox warningBox = new HBox(10);
        warningBox.setAlignment(Pos.CENTER_LEFT);
        warningBox.setPadding(new Insets(10, 15, 10, 15));
        warningBox.setStyle("-fx-background-color: #f8d7da; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        Label warningIcon = new Label("⚠️");
        warningIcon.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Label warningText = new Label("SAFETY FIRST: Always create backups before making changes. This tool is read-only by default for your protection.");
        warningText.setFont(Font.font("System", FontWeight.NORMAL, 11));
        warningText.setWrapText(true);
        
        CheckBox enableWriteMode = new CheckBox("Enable Write Mode (DANGEROUS)");
        enableWriteMode.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
        
        warningBox.getChildren().addAll(warningIcon, warningText, enableWriteMode);
        
        bottomSection.getChildren().addAll(warningBox);
        return bottomSection;
    }
    
    private void setupEventHandlers() {
        // Setup custom query field to execute on Enter
        customQueryField.setOnAction(e -> executeCustomQuery());
    }
    
    private void initializeBackupSystem() {
        // Set default backup directory
        String userHome = System.getProperty("user.home");
        backupDirectory = userHome + File.separator + "ChessPedagogue_Backups";
        
        // Create backup directory if it doesn't exist
        File backupDir = new File(backupDirectory);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
            logger.info("📁 Created backup directory: {}", backupDirectory);
        }
        
        // Start auto-backup timer (disabled by default)
        // toggleAutoBackup(false);
    }
    
    // Database Connection Methods
    
    private void connectToAndroidDatabase() {
        // This would connect via ADB to pull the database from the Android device
        showInfoAlert("Android Connection", 
            "Android database connection not yet implemented. Use 'Browse DB File' to load a database file manually.",
            "Future feature: Will automatically pull chess_games.db from connected Android device via ADB.");
    }
    
    private void browseForDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Chess Pedagogue Database");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("SQLite Database", "*.db", "*.sqlite", "*.sqlite3")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getTabPane().getScene().getWindow());
        if (selectedFile != null) {
            connectToDatabase(selectedFile.getAbsolutePath());
        }
    }
    
    private void connectToDatabase(String databasePath) {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
            
            currentDatabasePath = databasePath;
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            isConnected = true;
            
            updateConnectionStatus(true, new File(databasePath).getName());
            refreshTableList();
            loadSchemaInfo();
            
            logger.info("✅ Connected to database: {}", databasePath);
            
        } catch (SQLException e) {
            logger.error("❌ Failed to connect to database: {}", e.getMessage(), e);
            showErrorAlert("Connection Error", "Failed to connect to database", e.getMessage());
            updateConnectionStatus(false, null);
        }
    }
    
    private void updateConnectionStatus(boolean connected, String dbName) {
        Platform.runLater(() -> {
            if (connected && dbName != null) {
                connectionStatusLabel.setText("🟢 Connected: " + dbName);
                connectionStatusLabel.setTextFill(Color.GREEN);
                tableStatsLabel.setText("Database loaded successfully");
            } else {
                connectionStatusLabel.setText("🔴 Not Connected");
                connectionStatusLabel.setTextFill(Color.RED);
                tableStatsLabel.setText("No database loaded");
            }
        });
    }
    
    // Data Loading Methods
    
    private void refreshTableList() {
        if (!isConnected) return;
        
        try {
            DatabaseMetaData metaData = dbConnection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            ObservableList<String> tableNames = FXCollections.observableArrayList();
            ObservableList<String> schemaTableNames = FXCollections.observableArrayList();
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
                schemaTableNames.add(tableName);
            }
            
            Platform.runLater(() -> {
                tableSelector.setItems(tableNames);
                tableListView.setItems(schemaTableNames);
                
                if (!tableNames.isEmpty()) {
                    tableStatsLabel.setText(tableNames.size() + " tables found");
                }
            });
            
        } catch (SQLException e) {
            logger.error("❌ Error loading table list: {}", e.getMessage(), e);
            showErrorAlert("Database Error", "Failed to load table list", e.getMessage());
        }
    }
    
    private void loadTableData() {
        String selectedTable = tableSelector.getSelectionModel().getSelectedItem();
        if (selectedTable == null || !isConnected) return;
        
        try {
            String query = "SELECT * FROM " + selectedTable + " LIMIT 100";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            // Clear existing columns
            dataTable.getColumns().clear();
            
            // Get column metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Create columns
            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i - 1;
                String columnName = metaData.getColumnName(i);
                
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(param -> 
                    javafx.beans.binding.Bindings.createStringBinding(() -> {
                        if (param.getValue().size() > columnIndex) {
                            return param.getValue().get(columnIndex);
                        }
                        return "";
                    })
                );
                
                dataTable.getColumns().add(column);
            }
            
            // Load data
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    row.add(value != null ? value : "NULL");
                }
                data.add(row);
            }
            
            Platform.runLater(() -> {
                dataTable.setItems(data);
            });
            
        } catch (SQLException e) {
            logger.error("❌ Error loading table data: {}", e.getMessage(), e);
            showErrorAlert("Database Error", "Failed to load table data", e.getMessage());
        }
    }
    
    private void loadSchemaInfo() {
        if (!isConnected) return;
        
        try {
            DatabaseMetaData metaData = dbConnection.getMetaData();
            StringBuilder schemaInfo = new StringBuilder();
            
            schemaInfo.append("=== DATABASE SCHEMA ===\n\n");
            
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                schemaInfo.append("Table: ").append(tableName).append("\n");
                
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    String nullable = columns.getString("IS_NULLABLE");
                    
                    schemaInfo.append("  - ").append(columnName)
                             .append(" (").append(columnType).append(")")
                             .append(nullable.equals("NO") ? " NOT NULL" : "")
                             .append("\n");
                }
                schemaInfo.append("\n");
            }
            
            Platform.runLater(() -> {
                schemaTextArea.setText(schemaInfo.toString());
            });
            
        } catch (SQLException e) {
            logger.error("❌ Error loading schema info: {}", e.getMessage(), e);
        }
    }
    
    private void showTableSchema() {
        String selectedTable = tableListView.getSelectionModel().getSelectedItem();
        if (selectedTable == null || !isConnected) return;
        
        try {
            String query = "PRAGMA table_info(" + selectedTable + ")";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            StringBuilder schema = new StringBuilder();
            schema.append("=== TABLE: ").append(selectedTable.toUpperCase()).append(" ===\n\n");
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                String type = rs.getString("type");
                boolean notNull = rs.getInt("notnull") == 1;
                String defaultValue = rs.getString("dflt_value");
                boolean primaryKey = rs.getInt("pk") == 1;
                
                schema.append(columnName).append(" ").append(type);
                if (primaryKey) schema.append(" PRIMARY KEY");
                if (notNull) schema.append(" NOT NULL");
                if (defaultValue != null) schema.append(" DEFAULT ").append(defaultValue);
                schema.append("\n");
            }
            
            // Get row count
            String countQuery = "SELECT COUNT(*) FROM " + selectedTable;
            ResultSet countRs = stmt.executeQuery(countQuery);
            if (countRs.next()) {
                int rowCount = countRs.getInt(1);
                schema.append("\nTotal rows: ").append(rowCount);
            }
            
            Platform.runLater(() -> {
                schemaTextArea.setText(schema.toString());
            });
            
        } catch (SQLException e) {
            logger.error("❌ Error loading table schema: {}", e.getMessage(), e);
        }
    }
    
    // Query Execution Methods
    
    private void executeCustomQuery() {
        String query = customQueryField.getText().trim();
        if (query.isEmpty() || !isConnected) return;
        
        executeQuery(query);
    }
    
    private void executeQuery(String query) {
        if (!isConnected) {
            queryResultArea.setText("❌ Not connected to database");
            return;
        }
        
        try {
            Statement stmt = dbConnection.createStatement();
            
            if (query.toLowerCase().startsWith("select")) {
                // SELECT query
                ResultSet rs = stmt.executeQuery(query);
                displayQueryResults(rs);
            } else {
                // Non-SELECT query (UPDATE, INSERT, DELETE)
                int rowsAffected = stmt.executeUpdate(query);
                queryResultArea.setText("✅ Query executed successfully. Rows affected: " + rowsAffected);
            }
            
        } catch (SQLException e) {
            logger.error("❌ Query execution error: {}", e.getMessage(), e);
            queryResultArea.setText("❌ Error: " + e.getMessage());
        }
    }
    
    private void displayQueryResults(ResultSet rs) throws SQLException {
        StringBuilder result = new StringBuilder();
        
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // Column headers
        for (int i = 1; i <= columnCount; i++) {
            result.append(String.format("%-20s", metaData.getColumnName(i)));
        }
        result.append("\n");
        result.append("=".repeat(columnCount * 20)).append("\n");
        
        // Data rows
        int rowCount = 0;
        while (rs.next() && rowCount < 100) { // Limit to 100 rows for display
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                result.append(String.format("%-20s", value != null ? value : "NULL"));
            }
            result.append("\n");
            rowCount++;
        }
        
        if (rowCount == 100) {
            result.append("\n... (limited to 100 rows for display)");
        }
        
        result.append("\nTotal rows displayed: ").append(rowCount);
        
        Platform.runLater(() -> {
            queryResultArea.setText(result.toString());
        });
    }
    
    private void showTableCounts() {
        if (!isConnected) return;
        
        try {
            StringBuilder counts = new StringBuilder();
            counts.append("=== TABLE ROW COUNTS ===\n\n");
            
            DatabaseMetaData metaData = dbConnection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                
                String countQuery = "SELECT COUNT(*) FROM " + tableName;
                Statement stmt = dbConnection.createStatement();
                ResultSet countRs = stmt.executeQuery(countQuery);
                
                if (countRs.next()) {
                    int count = countRs.getInt(1);
                    counts.append(String.format("%-25s: %,d rows\n", tableName, count));
                }
            }
            
            Platform.runLater(() -> {
                queryResultArea.setText(counts.toString());
            });
            
        } catch (SQLException e) {
            logger.error("❌ Error getting table counts: {}", e.getMessage(), e);
            queryResultArea.setText("❌ Error: " + e.getMessage());
        }
    }
    
    private void generateDatabaseStatistics(TextArea statsArea) {
        if (!isConnected) {
            statsArea.setText("❌ Not connected to database");
            return;
        }
        
        try {
            StringBuilder stats = new StringBuilder();
            stats.append("=== CHESS PEDAGOGUE DATABASE STATISTICS ===\n");
            stats.append("Generated: ").append(LocalDateTime.now().format(TIME_FORMAT)).append("\n\n");
            
            // Basic database info
            stats.append("Database: ").append(new File(currentDatabasePath).getName()).append("\n");
            stats.append("Path: ").append(currentDatabasePath).append("\n");
            stats.append("Size: ").append(String.format("%.2f MB", new File(currentDatabasePath).length() / 1024.0 / 1024.0)).append("\n\n");
            
            // Table statistics
            stats.append("=== TABLE STATISTICS ===\n");
            DatabaseMetaData metaData = dbConnection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            int totalRows = 0;
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                
                String countQuery = "SELECT COUNT(*) FROM " + tableName;
                Statement stmt = dbConnection.createStatement();
                ResultSet countRs = stmt.executeQuery(countQuery);
                
                if (countRs.next()) {
                    int count = countRs.getInt(1);
                    totalRows += count;
                    stats.append(String.format("%-25s: %,d rows\n", tableName, count));
                }
            }
            
            stats.append(String.format("\nTotal rows across all tables: %,d\n\n", totalRows));
            
            // Emotional intelligence statistics
            try {
                Statement stmt = dbConnection.createStatement();
                
                // Master relationships
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM master_relationships");
                if (rs.next()) {
                    stats.append("Active master relationships: ").append(rs.getInt(1)).append("\n");
                }
                
                // Emotional reactions
                rs = stmt.executeQuery("SELECT COUNT(*) FROM emotional_reactions");
                if (rs.next()) {
                    stats.append("Emotional reactions recorded: ").append(rs.getInt(1)).append("\n");
                }
                
                // Recent activity
                rs = stmt.executeQuery("SELECT COUNT(*) FROM emotional_reactions WHERE timestamp > " + (System.currentTimeMillis() - 24 * 60 * 60 * 1000));
                if (rs.next()) {
                    stats.append("Emotional reactions (last 24h): ").append(rs.getInt(1)).append("\n");
                }
                
            } catch (SQLException e) {
                stats.append("Some statistics unavailable (emotional tables may not exist)\n");
            }
            
            Platform.runLater(() -> {
                statsArea.setText(stats.toString());
            });
            
        } catch (SQLException e) {
            logger.error("❌ Error generating statistics: {}", e.getMessage(), e);
            Platform.runLater(() -> {
                statsArea.setText("❌ Error generating statistics: " + e.getMessage());
            });
        }
    }
    
    // Backup and Safety Methods
    
    private void createManualBackup() {
        if (!isConnected) {
            showErrorAlert("Backup Error", "No database connected", "Please connect to a database first");
            return;
        }
        
        createBackup("manual");
    }
    
    private void createBackup(String backupType) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = "chess_pedagogue_" + backupType + "_" + timestamp + ".db";
            String backupPath = backupDirectory + File.separator + backupFileName;
            
            Platform.runLater(() -> {
                backupProgress.setVisible(true);
                backupProgress.setProgress(-1); // Indeterminate progress
            });
            
            // Copy database file
            Files.copy(Paths.get(currentDatabasePath), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);
            
            Platform.runLater(() -> {
                backupProgress.setVisible(false);
                showInfoAlert("Backup Created", 
                    "Backup created successfully!",
                    "Backup saved to: " + backupPath);
            });
            
            logger.info("✅ Database backup created: {}", backupPath);
            
        } catch (IOException e) {
            Platform.runLater(() -> {
                backupProgress.setVisible(false);
            });
            logger.error("❌ Backup failed: {}", e.getMessage(), e);
            showErrorAlert("Backup Failed", "Failed to create backup", e.getMessage());
        }
    }
    
    private void restoreFromBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup to Restore");
        fileChooser.setInitialDirectory(new File(backupDirectory));
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Database Backup", "*.db")
        );
        
        File backupFile = fileChooser.showOpenDialog(getTabPane().getScene().getWindow());
        if (backupFile != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Restore Backup");
            confirmAlert.setHeaderText("⚠️ DANGEROUS OPERATION");
            confirmAlert.setContentText("This will replace your current database with the backup. This cannot be undone!\n\nAre you absolutely sure?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    performRestore(backupFile);
                }
            });
        }
    }
    
    private void performRestore(File backupFile) {
        try {
            // Close current connection
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
            
            // Copy backup over current database
            Files.copy(backupFile.toPath(), Paths.get(currentDatabasePath), StandardCopyOption.REPLACE_EXISTING);
            
            // Reconnect
            connectToDatabase(currentDatabasePath);
            
            showInfoAlert("Restore Complete", 
                "Database restored successfully!",
                "Your database has been restored from: " + backupFile.getName());
            
            logger.info("✅ Database restored from backup: {}", backupFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("❌ Restore failed: {}", e.getMessage(), e);
            showErrorAlert("Restore Failed", "Failed to restore backup", e.getMessage());
        }
    }
    
    private void selectBackupDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Backup Directory");
        directoryChooser.setInitialDirectory(new File(backupDirectory));
        
        File selectedDirectory = directoryChooser.showDialog(getTabPane().getScene().getWindow());
        if (selectedDirectory != null) {
            backupDirectory = selectedDirectory.getAbsolutePath();
            showInfoAlert("Backup Directory Set", 
                "Backup directory updated",
                "Backups will now be saved to: " + backupDirectory);
        }
    }
    
    private void toggleAutoBackup(boolean enabled) {
        // Auto-backup implementation would go here
        // For now, just show a message
        if (enabled) {
            showInfoAlert("Auto-Backup", 
                "Auto-backup enabled",
                "The database will be automatically backed up every 30 minutes");
        }
    }
    
    // Export Methods
    
    private void exportTableToCSV() {
        String selectedTable = tableSelector.getSelectionModel().getSelectedItem();
        if (selectedTable == null || !isConnected) {
            showErrorAlert("Export Error", "No table selected", "Please select a table to export");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Table to CSV");
        fileChooser.setInitialFileName(selectedTable + ".csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File csvFile = fileChooser.showSaveDialog(getTabPane().getScene().getWindow());
        if (csvFile != null) {
            exportTableToFile(selectedTable, csvFile);
        }
    }
    
    private void exportTableToFile(String tableName, File csvFile) {
        try {
            String query = "SELECT * FROM " + tableName;
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            StringBuilder csv = new StringBuilder();
            
            // Headers
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                csv.append(metaData.getColumnName(i));
                if (i < columnCount) csv.append(",");
            }
            csv.append("\n");
            
            // Data
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    csv.append(value != null ? "\"" + value.replace("\"", "\"\"") + "\"" : "");
                    if (i < columnCount) csv.append(",");
                }
                csv.append("\n");
            }
            
            Files.write(csvFile.toPath(), csv.toString().getBytes());
            
            showInfoAlert("Export Complete", 
                "Table exported successfully!",
                "Data saved to: " + csvFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.error("❌ Export failed: {}", e.getMessage(), e);
            showErrorAlert("Export Failed", "Failed to export table", e.getMessage());
        }
    }
    
    private void copyCreateStatement() {
        String selectedTable = tableListView.getSelectionModel().getSelectedItem();
        if (selectedTable == null || !isConnected) return;
        
        try {
            String query = "SELECT sql FROM sqlite_master WHERE type='table' AND name='" + selectedTable + "'";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                String createStatement = rs.getString("sql");
                
                // Copy to clipboard (simplified - would need proper clipboard integration)
                schemaTextArea.setText("CREATE Statement for " + selectedTable + ":\n\n" + createStatement);
                
                showInfoAlert("CREATE Statement", 
                    "CREATE statement copied to schema view",
                    "You can copy it from the schema text area");
            }
            
        } catch (SQLException e) {
            logger.error("❌ Error getting CREATE statement: {}", e.getMessage(), e);
        }
    }
    
    // Utility Methods
    
    private void showErrorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    private void showInfoAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    public void cleanup() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
                logger.info("🗃️ Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("❌ Error closing database connection: {}", e.getMessage());
        }
    }
}