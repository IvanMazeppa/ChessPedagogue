package com.chesspedagogue.configurator.tabs;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class MasterPersonalityTab extends Tab {
    
    public MasterPersonalityTab() {
        setText("🎭 Master Personalities");
        setClosable(false);
        
        VBox content = new VBox(20);
        content.getChildren().add(new Label("Master Personality Editor - Coming Soon!"));
        content.getChildren().add(new Label("This will allow you to configure:"));
        content.getChildren().add(new Label("• Initial emotional states"));
        content.getChildren().add(new Label("• Voice settings"));
        content.getChildren().add(new Label("• Conversation preferences"));
        content.getChildren().add(new Label("• Personality traits"));
        
        setContent(content);
    }
}