# ChessPedagogue 🏆♟️ – AI Chess Masters Come Alive

**Experience chess like never before.** ChessPedagogue brings legendary chess masters back to life through advanced AI technology, letting you learn from the greatest minds in chess history through natural voice conversations and immersive gameplay.

> *"It's like having Mikhail Tal, Bobby Fischer, and Magnus Carlsen as your personal chess coaches, ready to share their insights whenever you need them."*

## ✨ What Makes ChessPedagogue Special

ChessPedagogue isn't just another chess app—it's a revolutionary learning platform that combines:

- **🎭 12 Legendary Chess Masters** with authentic personalities and playing styles
- **🗣️ Natural Voice Conversations** powered by cutting-edge AI
- **🤖 Spectator Mode** where AI masters play against each other with live commentary
- **📊 Real-time Analysis** using the world-class Stockfish engine
- **🎯 Personality-Driven Gameplay** based on historical move preferences

## 🏆 Meet Your Chess Masters

### **Fine-tuned AI Personalities** (Advanced Models)
- **🎪 Mikhail Tal** - *"The Magician"* | Intuitive, creative sacrifices with Latvian charm
- **🇺🇸 Bobby Fischer** - *"The Perfectionist"* | Demanding precision with intense analysis
- **🇳🇴 Magnus Carlsen** - *"The Pragmatist"* | Modern, practical approach to chess mastery
- **🎨 Alexander Alekhine** - *"The Artist"* | Sophisticated combination play

### **Classic Personalities**
- **⚡ Garry Kasparov** - Dynamic attacking play
- **🛡️ Anatoly Karpov** - Master of positional chess
- **🏰 Vladimir Kramnik** - Solid strategic foundation
- **🌟 José Raúl Capablanca** - Natural, effortless style
- **🧠 Emanuel Lasker** - Psychological warfare expert
- **⚔️ Paul Morphy** - Romantic attacking genius
- **🌏 Viswanathan Anand** - Versatile champion
- **🔬 Mikhail Botvinnik** - Scientific approach to chess

## 🚀 Core Features

### **🎮 Multiple Game Modes**

**Interactive Learning Mode**
- Play against any chess master with their authentic style
- Real-time voice coaching and move explanations
- Position evaluation with emotional responses
- Personalized difficulty adjustment

**Spectator Mode** ⭐ *Flagship Feature*
- Watch two AI masters battle with live commentary
- Masters discuss moves, strategies, and react to brilliant plays
- Ask questions during games using voice input
- Experience the drama of chess at the highest level

**Game Analysis Mode**
- Deep dive into any position with master insights
- Move-by-move breakdowns with Stockfish analysis
- Learn why masters made specific choices
- Voice-powered explanations in each master's style

### **🗣️ Advanced Voice Technology**

**Natural Conversations**
- Speak questions in plain English, get expert answers
- Master-specific accents and speech patterns
- Emotional responses to critical moments
- Three-stage AI response system for depth

**Smart Recognition**
- Groq-powered speech-to-text for accuracy
- Automatic silence detection
- Multi-language support ready
- Noise filtering for clear communication

### **🧠 Intelligent Chess Engine**

**Stockfish Integration**
- World-class chess engine analysis
- Real-time position evaluation
- Configurable strength (800-3200 ELO)
- Historical move matching system

**Personality Engine**
- Database of thousands of historical positions
- Masters play moves they actually played in real games
- Weighted decision making based on playing style
- Authentic recreation of legendary games

## 📱 Getting Started

### **Prerequisites**
- Android 8.0+ (API 26)
- Internet connection for AI features
- Microphone permission for voice interaction
- 2GB+ storage space recommended

### **Installation**

#### Option 1: Direct APK Install
1. Download the latest APK from [Releases](../../releases)
2. Enable "Install from unknown sources" in Android settings
3. Open the APK file to install
4. Launch ChessPedagogue and enjoy!

#### Option 2: Build from Source
```bash
# Clone the repository
git clone https://github.com/IvanMazeppa/ChessPedagogue.git
cd ChessPedagogue

# Switch to the latest stable branch
git checkout research-ready-v0.7.8

# Open in Android Studio
# File → Open → Select project folder
```

### **API Setup** 🔑
ChessPedagogue requires API keys for the full experience:

1. **OpenAI API Key** (Required for AI personalities and voice)
   - Visit [OpenAI Platform](https://platform.openai.com/api-keys)
   - Create an account and generate an API key
   - Add to app settings or `local.properties`

2. **Groq API Key** (Required for speech recognition)
   - Sign up at [Groq Console](https://console.groq.com/)
   - Generate your API key
   - Add to app settings

```properties
# Add to local.properties (not committed to git)
OPENAI_API_KEY=sk-your-openai-key-here
GROQ_API_KEY=gsk_your-groq-key-here
```

## 🎯 How to Use ChessPedagogue

### **🎪 Starting Your First Session**

1. **Choose Your Master** - Select from 12 legendary personalities
2. **Set Up the Board** - Start with any position or use the default
3. **Tap the Microphone** - Ask questions or make moves by voice
4. **Listen and Learn** - Your chosen master responds with personalized insights

### **🎬 Spectator Mode Experience**

1. **Select Two Masters** - Choose which legends will face off
2. **Start the Game** - Watch as they play with authentic styles
3. **Listen to Commentary** - Masters explain their thinking in real-time
4. **Join the Conversation** - Ask questions during the game
5. **Learn from Legends** - Absorb insights from chess history's greatest minds

### **💡 Example Interactions**

**Beginner Questions:**
- *"Tal, why did you sacrifice your queen?"*
- *"Fischer, what's wrong with my position?"*
- *"Magnus, should I castle now?"*

**Advanced Analysis:**
- *"Explain the pawn structure in this position"*
- *"What would you play in this endgame?"*
- *"Show me the tactical motifs here"*

**During Spectator Games:**
- *"Why didn't Kasparov take the knight?"*
- *"What's the evaluation of this position?"*
- *"Who do you think is winning?"*

## 🛠️ Technical Highlights

### **🏗️ Architecture**
- **MVVM Pattern** with LiveData and Repository
- **Native Stockfish** integration via JNI
- **Multi-threaded** design for smooth performance
- **SQLite Database** for game saves and historical data

### **🤖 AI Technology Stack**
- **OpenAI GPT-4** with fine-tuned chess models
- **Assistants API** with vector stores for top masters
- **Custom personality engine** with historical position matching
- **Advanced prompt engineering** for authentic responses

### **🎙️ Voice Pipeline**
- **Groq Whisper** for fast, accurate speech-to-text
- **OpenAI TTS** with master-specific voices and accents
- **Queue management** for smooth conversation flow
- **Emotional tone** adaptation based on game state

## 🏅 Current Version: v0.7.8

### **✅ Stable Features**
- Full chess gameplay with all masters
- Working spectator mode with live commentary
- Voice interaction in all modes
- Game saving and analysis
- Real-time evaluation tracking

### **🔧 Recent Improvements**
- Fixed spectator mode ANR and deadlock issues
- Resolved duplicate piece rendering
- Enhanced thread synchronization
- Added robust timeout mechanisms
- Improved voice recognition accuracy

### **🚀 Coming Soon**
- Wake word activation ("Hey Coach")
- Offline analysis mode
- Tournament simulation
- Master vs Master championship mode
- Extended historical position database

## 🤝 Contributing

We welcome contributions from chess enthusiasts and developers! Whether you're interested in:

- **🎭 Adding new chess personalities**
- **🗣️ Improving voice recognition**
- **📊 Enhancing analysis features**
- **🐛 Bug fixes and optimizations**
- **📚 Documentation improvements**

Check our [Contributing Guidelines](CONTRIBUTING.md) and open an issue to get started!

### **🌟 For Beginners**
Look for issues labeled `good first issue` or `help wanted`. We're committed to mentoring new contributors and making chess AI accessible to all skill levels.

## 📊 Performance & Requirements

### **💾 System Requirements**
- **RAM**: 2GB minimum, 4GB recommended
- **Storage**: 500MB for app, 2GB for full voice data
- **CPU**: Dual-core 1.5GHz minimum
- **Network**: Stable internet for AI features

### **⚡ Performance Features**
- Hardware-accelerated graphics
- Efficient memory management
- Background processing optimization
- Smart caching for voice data

## 🔒 Privacy & Security

ChessPedagogue respects your privacy:
- **Voice data** is processed securely via encrypted APIs
- **API keys** are stored locally and never transmitted
- **Game data** stays on your device unless you choose to export
- **No tracking** or analytics beyond crash reporting

## 📄 License

This project is currently under evaluation for open-source licensing. Contributors retain rights to their contributions under the future chosen license (likely MIT or Apache 2.0).

## 🙏 Acknowledgments

- **Stockfish Team** for the incredible chess engine
- **OpenAI** for revolutionary AI technology
- **Groq** for lightning-fast speech recognition
- **Chess History** for inspiration from the greatest players ever
- **Chess Community** for feedback and support

## 🎯 Our Vision

*"To make learning chess as natural as having a conversation with a friend, while preserving the wisdom and personalities of chess's greatest masters for future generations."*

ChessPedagogue bridges the gap between human intuition and computer precision, creating an educational experience that's both deeply analytical and warmly personal.

---

**Ready to learn from the masters?** 

Download ChessPedagogue today and start your journey with the greatest chess minds in history! 🏆

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen.svg)](../../releases)
[![Join Discussion](https://img.shields.io/badge/Join-Discussion-blue.svg)](../../discussions)
[![Report Bug](https://img.shields.io/badge/Report-Bug-red.svg)](../../issues)

*Happy learning, and may your chess improve as quickly as your conversations with the masters! ♟️✨*