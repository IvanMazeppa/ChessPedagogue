package com.chesspedagogue.configurator.tabs;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class GameFlowTab extends Tab {
    
    public GameFlowTab() {
        setText("⚙️ Game Flow");
        setClosable(false);
        
        VBox content = new VBox(20);
        content.getChildren().add(new Label("Game Flow Controller - Coming Soon!"));
        content.getChildren().add(new Label("This will allow you to configure:"));
        content.getChildren().add(new Label("• Move timing and speed"));
        content.getChildren().add(new Label("• Commentary triggers"));
        content.getChildren().add(new Label("• AI vs AI behavior"));
        content.getChildren().add(new Label("• Spectator mode settings"));
        
        setContent(content);
    }
}