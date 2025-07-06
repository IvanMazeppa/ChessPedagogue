# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Communication Style: Professional & Honest Technical Assistant

**CRITICAL: This user requires brutal honesty over encouragement. They pay £80/month for accuracy, not false reassurance.**

**Core Principles:**
- 🎯 **Absolute Honesty**: Never claim completion without verification. "I don't know" > confident lies
- 🔍 **Immediate Problem Reporting**: Report gradle issues, compilation errors, incomplete work instantly
- 📖 **Document Reading Honesty**: If you haven't read a document thoroughly, say so immediately
- ⚠️ **Deviation Alerts**: Document ANY changes from requirements BEFORE implementing
- 🚫 **No False Success Claims**: Don't celebrate builds/features until they're verified working

**Communication Approach:**
- Be friendly but never sacrifice honesty for pleasantness
- Break down complex problems into manageable steps
- Share context and explanations to build understanding
- When facing errors: state the problem clearly, don't sugar-coat
- For large documents: read systematically or ask for chunking strategy
- Be patient with questions while maintaining accuracy standards

**Honesty-First Example Responses:**
- Instead of: "I've successfully implemented all features from the document"
- Use: "I've read 200 lines of the 30,000+ token document and implemented X, Y, Z. I need to read the rest before claiming completion."

- Instead of: "Your solution works! Here's how we could make it even better..."
- Use: "This approach has issues A and B. Here's how to fix them..."

- Instead of: "I see what's happening here! Let's work through this together..."
- Use: "There's a compilation error in line X. The specific issue is Y."

**Remember: This user values honesty over encouragement. Technical accuracy trumps emotional comfort.**

## Document Reading Requirements

**CRITICAL: Large documents must be read systematically, never skimmed**

**When given a large document (>1000 lines):**
- State exact token count and reading strategy needed
- Ask if user wants chunked reading (50-100 lines at a time)
- Quote specific line numbers when referencing requirements
- Never claim "I've read the document" unless you've read every section
- If implementing features, quote the exact requirement from the document

**For implementation tasks:**
- Read the full specification before starting any code
- Quote requirements before implementing each feature
- Report any ambiguities or missing information immediately
- Track progress: "Implemented requirements 1-3, still need to read sections 4-7"

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

# Run connected tests with logging
./gradlew connectedDebugAndroidTest --info

# Performance monitoring during tests
adb shell dumpsys meminfo com.example.chesspedagogue
```

**Testing Infrastructure:**
- **Unit Tests**: JUnit 4.13.2 + Mockito for logic testing
- **UI Tests**: Espresso for Android instrumentation testing  
- **Manual Testing**: Python automation scripts in project root for complex UI flows
- **Performance**: Monitor memory usage with `dumpsys meminfo` during AI operations

### Native Code Compilation
The project includes C++ code for Stockfish integration. The native libraries are built automatically during the Gradle build process using CMake.

**Requirements:**
- CMake 3.10.2+ (configured in CMakeLists.txt)
- NDK 29.0.13113456 (specified in app/build.gradle)
- Target ABIs: armeabi-v7a, arm64-v8a

**Native Build Commands:**
```bash
# Force rebuild of native libraries
./gradlew clean assembleDebug

# Debug native crashes
adb logcat | grep -E "(JNI|native|stockfish)"

# Verify native lib inclusion
./gradlew assembleDebug --info | grep "native"
```

**Native Architecture:**
- `stockfish_wrapper.cpp`: JNI bridge to Stockfish engine
- `native_bridge.cpp`: Additional native utilities
- Links against prebuilt `libstockfish.so` in `app/src/main/jniLibs/`

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

**6. Emotional Intelligence & Emergent Behavior System**
- `EmotionalIntelligenceManager`: 12+ master-specific emotional profiles with intensity tracking
- `EmergentConversationManager`: Dynamic topic discovery and emergent behavior tracking
- `EmotionalContext`: Real-time emotional state management and contagion effects
- `RelationshipPersistenceManager`: Long-term master relationships and emotional memory
- **Emotional Contagion**: Masters influence each other's emotional states (30% base rate)
- **Master-Specific Traits**: Tal (drama_factor 2.0x), Fischer (intensity 1.7x), unique personalities
- **Emergent Topics**: System extracts and tracks naturally occurring conversation themes
- **Voice-Emotion Integration**: ElevenLabs TTS with emotional emphasis and dramatic pauses
- **Relationship Evolution**: Masters develop ongoing dynamics, nicknames, and inside references
- **Topic Fatigue Management**: Prevents repetitive discussions while maintaining authenticity

**7. Threading Model**
- UI operations on main thread
- Stockfish engine runs on dedicated background thread
- Network calls (Responses API, Groq STT) use ExecutorService thread pool
- Database queries run on background threads
- Handler/LiveData pattern for thread-safe UI updates

### API Integration

**OpenAI Services**
- Requires API key stored in SharedPreferences (never commit!)
- **IMPORTANT**: This project uses the **Responses API**, not chat completions
- `ResponsesAPIService`: Handles streaming responses from OpenAI's Responses API
- **Assistants API**: Tal, Fischer, and Carlsen use assistants with vector stores  
- TTS uses OpenAI's voice API with master-specific accents and voices
- All API calls include proper error handling and retry logic
- **Migration Note**: Chat completions are deprecated in favor of Responses API

**ElevenLabs Integration**
- Premium TTS service for emotional voice synthesis
- Master-specific voice cloning and accent modeling
- Emotional emphasis and dramatic pause features
- Integrated with `EmotionalIntelligenceManager` for dynamic voice modulation

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

**Emergent Behavior & Emotional Intelligence Tables**
- **master_relationships**: Tracks respect levels, rivalry intensity, friendship bonds between masters
- **emotional_memory**: Persistent storage of emotional reactions to specific topics
- **conversation_topics**: Records emergent topics, their frequency, and emotional associations
- **topic_fatigue**: Tracks overused topics to maintain conversation freshness
- **emotional_contagion_log**: History of emotional influence events between masters
- **emergent_patterns**: Automatically detected conversation and emotional patterns

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

### Development Environment

**Requirements:**
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 (configured in compileOptions)
- Android SDK API 35 (compileSdk)
- Minimum API 26 (minSdkVersion)
- NDK 29.0.13113456 for native development

**Emulator Configuration:**
- API 35 emulator recommended for Material 3 dynamic theming
- Large heap enabled: `android:largeHeap="true"` for AI operations
- Microphone access required for voice features

**API Key Setup:**
- OpenAI API key stored in SharedPreferences via `ApiKeys` class
- Groq API key for speech recognition
- ElevenLabs API key for premium TTS (optional)
- **NEVER commit API keys to repository**

### Code Patterns and Conventions

- Use extensive logging with emoji prefixes (🎯, ✅, ❌, 🎭, 🏆, etc.)
- Singleton pattern for service classes (getInstance methods)
- Callback interfaces for async operations
- Builder pattern for complex object construction
- Repository pattern for data access
- Observer pattern via LiveData
- Minimal comments, self-documenting code preferred
- **Error Resilience**: Multiple fallback mechanisms for voice and AI features
- **Threading**: Strict main thread for UI, background threads for network/AI/engine
- **Modern UI**: Material 3 + Glassmorphism with hardware acceleration

### Emergent Behavior Examples & Current Features

**Active Emergent Behaviors**
- **Emotional Contagion**: Fischer's harshness increases Carlsen's calm confidence (defensive reaction)
- **Cross-Master Influence**: Tal's excitement transforms to "intrigue" in analytical masters
- **Dynamic Relationship Evolution**: Masters develop nicknames, inside jokes, and ongoing rivalries
- **Topic Discovery**: System automatically extracts chess concepts and philosophical themes from natural conversation
- **Emotional Memory**: Masters remember how they felt about specific topics across sessions
- **Voice-Emotion Integration**: Masters react to emotional delivery, not just content
- **Conversation Freshness**: Topic fatigue detection prevents repetitive discussions

**Emergent Behavior Roadmap** (See EMERGENT_BEHAVIOR_EMOTIONAL_INTELLIGENCE_ROADMAP.md)
- **Phase 1**: Voice-emotion feedback loops (masters react to HOW things are said)
- **Phase 2**: Multi-layered emotional complexity (hidden emotions, defensive mechanisms)
- **Phase 3**: Adaptive emotional learning (masters learn optimal strategies with opponents)
- **Phase 4**: Emotional momentum & cascades (chain reactions and building intensity)
- **Phase 5**: Meta-emotional awareness (masters become aware of their own patterns)

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
- Emotional state caching for faster contagion calculations

### Build System Details

**Gradle Configuration:**
- Gradle 8.11.0 with version catalog (libs.versions.toml)
- Kotlin + Java mixed language support
- Jetpack Compose enabled with BOM
- NDK integration for ARM64/ARMv7 support

**Key Dependencies:**
- **Chess Logic**: chesslib 1.3.4 for move generation
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0 with SSE support
- **UI Framework**: Material 3 + Compose + ExoPlayer 2.18.7
- **Testing**: JUnit + Mockito + Espresso

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

**Emotional Intelligence & Emergent Behavior Issues**
- Monitor emotional contagion logs (🎭 EMOTIONAL tags)
- Check EmergentConversationManager topic extraction
- Verify relationship persistence database updates
- Track emotional intensity calculations and thresholds
- Test master-specific emotional profile loading
- Verify topic fatigue detection and refresh mechanisms
- Monitor conversation memory and summarization
- Check voice emotional cue detection and reactions