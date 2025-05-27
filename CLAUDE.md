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
- **ViewModels**: GameViewModel manages game state and exposes LiveData
- **Repository**: GameRepository handles data operations and Stockfish communication
- **Models**: GameState, GameStateInfo contain chess game data

### Key Architectural Components

**1. Chess Engine Integration**
- `StockfishManager`: JNI bridge to native Stockfish engine
- Native C++ wrapper in `app/src/main/cpp/` handles engine communication
- Stockfish runs on background threads, communicating via UCI protocol

**2. AI Personality System**
- `PersonalityEngine`: Matches current positions against historical games database
- `GameDatabaseHelper`: SQLite database containing positions from chess masters
- `FineTunedModelManager`: Manages fine-tuned OpenAI models for each chess master
- Historical positions stored as JSON assets in `app/src/main/assets/`

**3. Voice Interaction Pipeline**
- `SimpleRecordService`: Handles voice recording and orchestrates the full pipeline
- `OpenAIService`: Unified service for chat completions
- `OpenAITTSService`: Text-to-speech with master-specific voices
- Voice processing happens on background threads with UI updates via Handler

**4. Real-time Game Analysis**
- `EvaluationTracker`: Monitors position evaluations and detects significant swings
- `ThreeStageResponseManager`: Manages complex AI responses (analysis, commentary, suggestions)
- Auto-commentary triggered by evaluation changes or historical moves

**5. Threading Model**
- UI operations on main thread
- Stockfish engine runs on dedicated background thread
- Network calls (OpenAI API) use ExecutorService thread pool
- Database queries run on background threads
- Handler/LiveData pattern for thread-safe UI updates

### API Integration

**OpenAI Services**
- Requires API key stored in SharedPreferences (never commit!)
- Chat completions use GPT-4 or fine-tuned models
- TTS uses OpenAI's voice API with custom voices per chess master
- All API calls include proper error handling and retry logic

**Voice Configuration**
- Each chess master has associated voice settings
- Voice personalization can be toggled on/off
- Automatic voice selection based on selected master

### Database Schema

**master_positions table**
- Stores historical chess positions from famous players
- Columns: id, master_name, fen, move_played, annotation, game_info
- Indexed by master_name and fen for fast lookups
- Populated from JSON files in assets during first launch

### State Management

**GameViewModel LiveData**
- currentFEN: Current board position
- moveHistory: List of moves in algebraic notation
- currentEvaluation: Stockfish evaluation in centipawns
- personalityEngineEnabled: Whether historical move matching is active
- Multiple event-based LiveData for animations and UI updates

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

**EvaluationBarView**
- Visual representation of position evaluation
- Animated transitions between evaluations
- Special handling for mate positions

### Code Patterns and Conventions

- Use extensive logging with emoji prefixes (🎯, ✅, ❌, etc.)
- Singleton pattern for service classes (getInstance methods)
- Callback interfaces for async operations
- Builder pattern for complex object construction
- Repository pattern for data access
- Observer pattern via LiveData
- Minimal comments, self-documenting code preferred