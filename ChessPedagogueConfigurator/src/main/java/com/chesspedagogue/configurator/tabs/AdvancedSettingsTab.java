package com.chesspedagogue.configurator.tabs;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class AdvancedSettingsTab extends Tab {
    
    public AdvancedSettingsTab() {
        setText("🔧 Advanced");
        setClosable(false);
        
        VBox content = new VBox(20);
        content.getChildren().add(new Label("Advanced Settings - Coming Soon!"));
        content.getChildren().add(new Label("This will allow you to configure:"));
        content.getChildren().add(new Label("• API settings and keys"));
        content.getChildren().add(new Label("• Debug and logging options"));
        content.getChildren().add(new Label("• Export/Import configurations"));
        content.getChildren().add(new Label("• Performance tuning"));
        
        setContent(content);
    }
}