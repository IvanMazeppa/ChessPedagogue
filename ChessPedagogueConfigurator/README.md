# 🎮 Chess Pedagogue Configurator

A standalone JavaFX GUI application for managing all configuration aspects of the Chess Pedagogue Android game.

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Running the Application

```bash
# Clone or navigate to the configurator directory
cd ChessPedagogueConfigurator

# Run with Maven
mvn clean javafx:run

# Or build and run JAR
mvn clean package
java -jar target/chess-pedagogue-configurator-1.0.0.jar
```

## ✨ Features

### 🎭 Conversation Template Designer
- Visual sliders for turn counts, response rates, and timing
- Pre-built templates: Minimal, Engaging, Intense, Collaborative
- Real-time preview of configuration changes
- Template simulation and testing
- Export templates for Android app

### 🎭 Master Personality Editor (Coming Soon)
- Configure AI personalities for all 12 chess masters
- Emotional state controls (confidence, aggression, humor)
- Voice settings and TTS configuration
- Conversation preferences and behavioral traits

### ⚙️ Game Flow Controller (Coming Soon)
- Move timing and pacing controls
- Commentary trigger thresholds
- AI vs AI behavior settings
- Real-time game flow adjustment

### 📊 Live Game Monitor (Coming Soon)
- Real-time connection to Android app
- Live conversation monitoring
- Instant configuration changes
- Emergency controls and overrides

## 🏗️ Project Structure

```
ChessPedagogueConfigurator/
├── src/main/java/
│   └── com/chesspedagogue/configurator/
│       ├── ChessPedagogueConfigurator.java    # Main application
│       ├── tabs/                              # UI tab components
│       ├── models/                            # Data models
│       ├── managers/                          # Configuration management
│       └── utils/                             # Utility classes
├── src/main/resources/
│   ├── css/application.css                    # Styling
│   └── fxml/                                  # UI layouts (if needed)
└── pom.xml                                    # Maven configuration
```

## 🔧 Configuration Integration

### Option 1: Shared Configuration File
The configurator saves settings to `chess_pedagogue_config.json` which the Android app monitors:

```java
// Android app watches for config changes
ConfigurationWatcher watcher = new ConfigurationWatcher("path/to/config");
watcher.startWatching();
```

### Option 2: WebSocket Communication
Real-time bidirectional communication between GUI and Android app:

```java
// GUI sends commands to Android app
LiveGameMonitor monitor = new LiveGameMonitor();
monitor.sendCommand("updateTemplate", templateData);
```

## 🎯 Current Template System Integration

The configurator seamlessly integrates with your existing template system:

```java
// Templates are immediately usable in Android app
ConversationFlowTester.enableEngagingConversations();
ConversationFlowTester.adjustConversationIntensity(3);
```

## 📈 Conversation Template Examples

### Engaging Template (Recommended)
- Opening: 3-6 turns, 85% response rate, 2.5s intervals
- Analysis: 4-8 turns, 80% response rate, 2.5s intervals
- Perfect for active spectator mode dialogues

### Intense Template (Maximum Drama)
- Opening: 4-10 turns, 90% response rate, 2s intervals
- Analysis: 5-12 turns, 88% response rate, 2s intervals
- For passionate chess master debates

### Minimal Template (Conservative)
- Opening: 1-2 turns, 50% response rate, 4s intervals
- Analysis: 1-3 turns, 50% response rate, 3.5s intervals
- Current behavior (too quiet)

## 🔄 Development Workflow

1. **Design templates** in the GUI with visual sliders
2. **Test configurations** with built-in simulation
3. **Apply to game** via configuration export
4. **Monitor live** (when Android integration is complete)
5. **Iterate quickly** without code changes

## 🚧 Development Status

- ✅ **Foundation**: JavaFX application structure complete
- ✅ **Template Designer**: Visual conversation template editor
- ✅ **Styling**: Professional UI theme
- 🚧 **Model Classes**: Configuration data models (in progress)
- 🚧 **Manager Classes**: File I/O and integration (in progress)
- 🚧 **Additional Tabs**: Personality, game flow, live monitor (planned)

## 🎨 Custom Styling

The application uses a modern, professional theme with:
- Clean card-based layout for configuration sections
- Color-coded buttons (primary, success, danger)
- Responsive sliders with real-time value display
- Professional typography and spacing
- Hover effects and focus indicators

## 🔌 Extension Points

The configurator is designed for easy extension:

- **New Configuration Categories**: Add tabs in `tabs/` package
- **Custom Templates**: Extend template system with new parameters
- **Integration Methods**: Add WebSocket, file watching, or HTTP APIs
- **Validation Rules**: Implement custom configuration validation
- **Export Formats**: Support multiple output formats

## 💡 Usage Tips

1. **Start with Engaging template** for immediate improvement over current conservative settings
2. **Use simulation** to predict conversation flow before applying
3. **Monitor response rates** - 80%+ for active dialogue, 50% for minimal
4. **Adjust intervals** - 2-3s for natural pacing, 4s+ for thoughtful exchanges
5. **Test incrementally** - make small changes and observe results

This configurator transforms the complex task of managing thousands of chess AI variables into an intuitive, visual experience perfect for rapid iteration and fine-tuning!