package com.chesspedagogue.configurator.tabs;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class LiveMonitorTab extends Tab {
    
    public LiveMonitorTab() {
        setText("📊 Live Monitor");
        setClosable(false);
        
        VBox content = new VBox(20);
        content.getChildren().add(new Label("Live Game Monitor - Coming Soon!"));
        content.getChildren().add(new Label("This will allow you to:"));
        content.getChildren().add(new Label("• Monitor active conversations"));
        content.getChildren().add(new Label("• View real-time game state"));
        content.getChildren().add(new Label("• Make quick adjustments"));
        content.getChildren().add(new Label("• Emergency controls"));
        
        setContent(content);
    }
}