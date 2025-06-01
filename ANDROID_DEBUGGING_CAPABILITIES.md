# Android Debugging Capabilities for Claude Code

## 🤖 ADB (Android Debug Bridge) Access

Claude Code has full access to Android debugging tools through the Windows ADB daemon running in Android Studio.

### 📱 **Device Management**
```bash
# List connected devices/emulators
adb.exe devices -l

# Current setup: S23 Ultra emulator
# Device ID: emulator-5554
# Model: sdk_gphone64_x86_64
```

### 📸 **Screenshot Capabilities**
```bash
# Take screenshot of current screen
adb.exe -s emulator-5554 exec-out screencap -p > /tmp/current_screen.png

# Then use Read tool to view the screenshot
```

### 📋 **Logcat Access**
```bash
# View real-time logs
adb.exe -s emulator-5554 logcat

# Filter by app
adb.exe -s emulator-5554 logcat | grep "ChessPedagogue"

# Filter by tag
adb.exe -s emulator-5554 logcat -s "EmotionalIntelligence"

# Clear logcat buffer
adb.exe -s emulator-5554 logcat -c
```

### 🎮 **Device Control**
```bash
# Tap at coordinates (x, y)
adb.exe -s emulator-5554 shell input tap 365 1465

# Swipe gesture
adb.exe -s emulator-5554 shell input swipe x1 y1 x2 y2 duration

# Send text input
adb.exe -s emulator-5554 shell input text "hello"

# Press keys (BACK, HOME, MENU, etc.)
adb.exe -s emulator-5554 shell input keyevent KEYCODE_BACK
```

### 📦 **App Management**
```bash
# Install APK
adb.exe -s emulator-5554 install app/build/outputs/apk/debug/app-debug.apk

# Start app
adb.exe -s emulator-5554 shell am start -n com.example.chesspedagogue/.MainActivity

# Stop app
adb.exe -s emulator-5554 shell am force-stop com.example.chesspedagogue

# Clear app data
adb.exe -s emulator-5554 shell pm clear com.example.chesspedagogue
```

### 🏗️ **Build & Deploy Workflow**
```bash
# 1. Build APK
./gradlew assembleDebug

# 2. Install on device
adb.exe -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk

# 3. Launch app
adb.exe -s emulator-5554 shell am start -n com.example.chesspedagogue/.MainActivity

# 4. Monitor logs
adb.exe -s emulator-5554 logcat | grep "EmotionalIntelligence\|EvaluationTracker\|ChessPedagogue"
```

### 🎭 **Emotional Intelligence Testing**
```bash
# Monitor emotional intelligence logs specifically
adb.exe -s emulator-5554 logcat -s "EmotionalIntelligence" "EvaluationTracker" "ElevenLabsTTSService"

# Watch for specific emotional states
adb.exe -s emulator-5554 logcat | grep "🎭\|BLUNDER\|BRILLIANT\|emotional"
```

## 📝 **Current Test Environment**
- **Device**: S23 Ultra Emulator (emulator-5554)
- **App**: ChessPedagogue with Emotional Intelligence System
- **Status**: Ready for testing
- **Key Features to Test**:
  - Blunder detection → Emotional responses
  - Brilliant move recognition → Celebration reactions  
  - Voice modulation based on emotional states
  - Master personality differences (Tal vs Fischer vs Carlsen)

## 🚨 **Debugging Tips**
1. **Always take screenshots** before and after interactions
2. **Use filtered logcat** to focus on relevant logs
3. **Clear logcat buffer** before starting new tests
4. **Monitor specific tags** for emotional intelligence features
5. **Test different chess masters** to see personality differences

## 📱 **Quick Commands Reference**
```bash
# Current device check
adb.exe devices

# Screenshot
adb.exe -s emulator-5554 exec-out screencap -p > /tmp/screen.png

# Start app
adb.exe -s emulator-5554 shell am start -n com.example.chesspedagogue/.MainActivity

# Monitor emotions
adb.exe -s emulator-5554 logcat | grep "🎭"
```

---
*This file serves as a reminder that Claude Code has full Android debugging capabilities and can directly interact with connected devices and emulators.*