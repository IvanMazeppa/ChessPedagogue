# 🔐 API Key Setup Instructions for ChessPedagogue v0.8.0

## Quick Setup

To get the emotional intelligence system working, you need to configure your OpenAI API key:

### 1. Create API Key File
```bash
cd app/src/main/java/com/example/chesspedagogue/
cp ApiKeys.java.template ApiKeys.java
```

### 2. Edit with Your API Key
Open `ApiKeys.java` and replace:
```java
public static final String OPENAI_API_KEY = "YOUR_OPENAI_API_KEY_HERE";
```

With your actual OpenAI API key:
```java
public static final String OPENAI_API_KEY = "sk-proj-your-actual-key-here";
```

### 3. Build and Test
```bash
./gradlew assembleDebug
```

## 🧪 Testing the Emotional Intelligence System

1. **Launch the app** on your device/emulator
2. **Navigate to spectator mode**: Splash → Master Selection → Main Game → Spectator Mode
3. **Watch the AI vs AI game**: Carlsen vs Fischer will play automatically
4. **Monitor logs** for emotional intelligence activity:
   ```bash
   adb logcat -s com.example.chesspedagogue | grep -E "(EMOTIONAL|conversation|ORCHESTRATOR)"
   ```

## ✅ Expected Behavior

When working correctly, you should see logs like:
```
🎭 EMOTIONAL DIALOGUE: move=e8g8, eval=-0.2, lastEval=0.4
🎬 Starting conversation: conv_1748881209136 (brilliant_move)
🎯 Response session started for carlsen
📨 About to call responsesManager.sendMessage
✅ Received AI response: "Excellent castling! This move secures my king..."
```

## 🔒 Security Notes

- ✅ `ApiKeys.java` is automatically excluded from git (protected by .gitignore)
- ✅ Never commit your actual API keys
- ✅ The template file can be safely shared
- ✅ Multiple fallback methods: SharedPreferences → Environment → ApiKeys class

## 🎯 Current Status

**Working Features:**
- ✅ App launches without crashes
- ✅ Null pointer fixes implemented  
- ✅ Spectator mode functional
- ✅ AI vs AI gameplay working
- ✅ Emotional intelligence system triggering
- ✅ Move evaluation and conversation orchestration
- ⏳ **Pending**: API key configuration for actual AI responses

**Next Steps:**
1. Configure your OpenAI API key using instructions above
2. Test the full emotional intelligence system
3. Enjoy AI masters commenting on games in real-time!

## 📚 Documentation

- **Testing**: See `EMULATOR_AUTOMATED_TESTING_GUIDE.md` for comprehensive testing
- **Coordinates**: All verified emulator coordinates documented
- **Troubleshooting**: Common issues and solutions included

---

🎉 **You're ready to experience ChessPedagogue's emotional AI chess masters!**