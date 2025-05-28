# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Building the Project
```bash
# Build the entire project (from project root)
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build artifacts
./gradlew clean

# Install debug build on connected device
./gradlew installDebug
```

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.example.chesspedagogue.YourTestClass"
```

### Native Code Compilation
The project includes C++ code for Stockfish integration. The native libraries are built automatically during the Gradle build process using CMake. If you need to rebuild just the native components:
```bash
# Force rebuild of native libraries
./gradlew clean assembleDebug
```

## Architecture Overview

### MVVM Architecture
The app follows Model-View-ViewModel pattern with clear separation:
- **Views**: Activities (MainActivity, SpectatorGameActivity, etc.) and custom views (ChessBoardView)
- **ViewModels**: GameViewModel, SpectatorGameViewModel manage game state and expose LiveData
- **Repository**: GameRepository handles data operations and Stockfish communication
- **Models**: GameState, GameStateInfo contain chess game data

### Key Architectural Components

**1. Chess Engine Integration**
- `StockfishManager`: JNI bridge to native Stockfish engine
- Native C++ wrapper in `app/src/main/cpp/` handles engine communication
- Stockfish runs on background threads, communicating via UCI protocol
- `AIvsAIGameManager`: Manages AI vs AI gameplay in spectator mode

**2. AI Personality System**
- `PersonalityEngine`: Matches current positions against historical games database
- `GameDatabaseHelper`: SQLite database containing positions from chess masters
- `FineTunedModelManager`: Manages fine-tuned OpenAI models and assistants for each chess master
- Historical positions stored as JSON assets in `app/src/main/assets/`
- **Available Chess Masters**: Tal, Fischer, Carlsen, Kramnik, Kasparov, Karpov, Alekhine, Capablanca, Morphy, Lasker, Anand, Botvinnik
- **Enhanced Personality Profiles**: Each master has detailed traits, communication styles, and signature approaches

**3. Voice Interaction Pipeline**
- `SimpleRecordService`: Handles voice recording and orchestrates the full pipeline
- `GroqSpeechRecognizer`: Fast, accurate speech-to-text using Groq API
- `OpenAIService`: Unified service for chat completions with fine-tuned models
- `OpenAITTSService`: Text-to-speech with master-specific voices and accents
- `SpeechRecognitionManager`: Manages STT across different activities
- Voice processing happens on background threads with UI updates via Handler

**4. Real-time Game Analysis & Commentary**
- `EvaluationTracker`: Monitors position evaluations and detects significant swings
- `ThreeStageResponseManager`: Manages complex AI responses (analysis, commentary, suggestions)
- `AIDialogueManager`: Handles emotional responses and master conversations
- Auto-commentary triggered by evaluation changes or historical moves
- **Spectator Mode**: Two AI masters play against each other with real-time dialogue

**5. Advanced Features**
- **Spectator Mode**: AI vs AI games with master personalities commenting
- **Conversation System**: Masters can have back-and-forth discussions about moves
- **Emotional Engine**: Detects evaluation swings and triggers appropriate emotional responses
- **Master Selection**: Full roster of 12 chess legends with unique personalities
- **Voice Comments**: STT integration for commenting during spectator games

**6. Threading Model**
- UI operations on main thread
- Stockfish engine runs on dedicated background thread
- Network calls (OpenAI API, Groq STT) use ExecutorService thread pool
- Database queries run on background threads
- Handler/LiveData pattern for thread-safe UI updates

### API Integration

**OpenAI Services**
- Requires API key stored in SharedPreferences (never commit!)
- Chat completions use GPT-4 or fine-tuned models
- **Assistants API**: Tal, Fischer, and Carlsen use assistants with vector stores
- TTS uses OpenAI's voice API with master-specific accents and voices
- All API calls include proper error handling and retry logic

**Groq Speech Recognition**
- Fast, accurate speech-to-text for voice interactions
- Used for user input, comments, and conversations
- Integrated across main game, spectator mode, and analysis

**Voice Configuration**
- Each chess master has associated voice settings and accent instructions
- Voice personalization can be toggled on/off
- Automatic voice selection based on selected master
- **Enhanced Accents**: Norwegian (Carlsen), Latvian-Russian (Tal), New York (Fischer), etc.

### Database Schema

**master_positions table**
- Stores historical chess positions from famous players
- Columns: id, master_name, fen, move_played, annotation, game_info
- Indexed by master_name and fen for fast lookups
- Populated from JSON files in assets during first launch
- **Expanded Coverage**: Historical games from all 12 chess masters

### State Management

**GameViewModel LiveData**
- currentFEN: Current board position
- moveHistory: List of moves in algebraic notation
- currentEvaluation: Stockfish evaluation in centipawns
- personalityEngineEnabled: Whether historical move matching is active
- Multiple event-based LiveData for animations and UI updates

**SpectatorGameViewModel LiveData**
- Manages AI vs AI game state
- Handles move callbacks and board updates
- Controls master dialogue and conversations
- Tracks evaluation changes for emotional responses

**Persistent State**
- Game saves use GameRepository
- Settings stored in SharedPreferences
- Voice recordings temporarily cached in app's cache directory

### Custom UI Components

**ChessBoardView**
- Custom View with hardware-accelerated drawing
- Handles piece animations, move highlights, and touch input
- Supports board flipping for black's perspective
- Maintains internal board state synchronized with FEN
- **Enhanced Animations**: Smooth piece movement in spectator mode

**EvaluationBarView**
- Visual representation of position evaluation
- Animated transitions between evaluations
- Special handling for mate positions

**Master Selection UI**
- Spectator mode supports all 12 chess masters
- Visual master cards with ratings and portraits
- Enhanced master selection in main menu

### Recent Major Updates

**Spectator Mode Fixes (v0.7.7)**
- ✅ Fixed critical ANR and deadlock issues
- ✅ Resolved duplicate piece rendering
- ✅ Fixed move history accumulation
- ✅ Improved synchronization and thread safety
- ✅ Added timeout mechanisms to prevent infinite loops

**Magnus Carlsen Integration**
- ✅ Added fine-tuned model: `ftjob-GrBGWeWWaUsVtM7r1RuHyCdJ`
- ✅ Assistant ID: `asst_TTzxbfvJQz3e80FetQblJ0Gl`
- ✅ Vector store: `vs_68365028eb988191b09d8d50e6f11b5d`
- ✅ Enhanced personality with modern, practical approach
- ✅ Fixed Norwegian accent for TTS
- ✅ Added to spectator mode selection

**Voice & STT Enhancements**
- ✅ Added STT to spectator mode comments
- ✅ Improved voice instructions for all masters
- ✅ Enhanced emotional response system
- ✅ Better error handling and fallbacks

### Code Patterns and Conventions

- Use extensive logging with emoji prefixes (🎯, ✅, ❌, 🎭, 🏆, etc.)
- Singleton pattern for service classes (getInstance methods)
- Callback interfaces for async operations
- Builder pattern for complex object construction
- Repository pattern for data access
- Observer pattern via LiveData
- Minimal comments, self-documenting code preferred
- **Error Resilience**: Multiple fallback mechanisms for voice and AI features

### Known Issues & Future Enhancements

**Potential Wake Word Implementation**
- Research completed for "Hey Coach" wake word using Porcupine
- Would enable always-listening voice activation
- Requires Porcupine dependency and background service
- Privacy-focused, on-device wake word detection

**Performance Optimization Opportunities**
- Database query optimization for large position datasets
- Memory management for long spectator games
- Background processing improvements for AI responses

### Debugging Tips

**Voice Issues**
- Check API keys in SharedPreferences
- Verify microphone permissions
- Look for TTS payload logs with master-specific accents
- Use "FORCED INSTRUCTIONS" logs to verify voice configuration

**Spectator Mode Issues**
- Monitor move callback logs (🎯 MOVE CALLBACK)
- Check evaluation tracker for emotional triggers
- Verify FEN and move history consistency
- Watch for thread synchronization issues

**AI Personality Issues**
- Check database population for historical positions
- Verify assistant IDs and vector store configuration
- Monitor API response codes and error handling
- Test personality profile selection and traits