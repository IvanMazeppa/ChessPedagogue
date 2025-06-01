# Device Testing Setup Guide

## Quick Start

1. **Connect your physical device:**
   ```bash
   # Check connection
   adb devices
   
   # Should show your device like:
   # List of devices attached
   # R5CW20BB3KT    device
   ```

2. **Run the color selection test:**
   ```bash
   python3 run_color_selection_test.py
   ```

## Detailed Setup

### Prerequisites
- Android device with USB debugging enabled
- USB cable
- ADB installed and in PATH

### Enable USB Debugging
1. Go to **Settings > About Phone**
2. Tap **Build Number** 7 times to enable Developer Options
3. Go to **Settings > Developer Options**
4. Enable **USB Debugging**
5. Connect USB cable and authorize computer when prompted

### Test the Color Selection Bug Fix

The automated test will:
1. ✅ Launch ChessPedagogue to splash screen
2. ✅ Test white king selection area (entire clickable area)
3. ✅ Test black king selection area (entire clickable area)  
4. ✅ Verify visual feedback changes
5. ✅ Test start button functionality
6. ✅ Take screenshots for verification

### Manual Testing Steps

If you prefer to test manually:

1. **Launch the app** - it should open to splash screen
2. **Tap anywhere on the white king area** - should highlight with gold background
3. **Tap anywhere on the black king area** - should highlight with gold background
4. **Tap the Start button** - should proceed to main game
5. **Verify the selected color** - check if your selection was preserved

### Test Results

After running the automated test, you'll get:
- ✅ **Test report** showing pass/fail status
- 📸 **Screenshots** in `screenshots/` folder
- 📄 **JSON results** in `splash_color_test_results.json`

### Troubleshooting

#### Device Not Detected
```bash
# Check if device is connected
adb devices

# If no devices, try:
adb kill-server
adb start-server
adb devices
```

#### App Not Installed
```bash
# Install the latest build
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### Permission Issues
- Grant microphone permission if prompted
- Allow app to run in background
- Disable battery optimization for ChessPedagogue

## Advanced Testing

### Test with Emulator Fallback
If physical device testing fails, we can use an emulator:

```bash
# Create emulator (if needed)
python3 create_test_emulator.py

# Run virtual device test
python3 virtual_device_test_demo.py
```

### Monitor App Logs
```bash
# Monitor ChessPedagogue logs during testing
adb logcat -s com.example.chesspedagogue:*
```

### Test Different Screen Sizes
The test automatically adjusts coordinates based on screen dimensions, but you can also test on different devices to ensure compatibility.

## Expected Results

✅ **Before the fix:** Users could only tap the small radio button circles
✅ **After the fix:** Users can tap anywhere in the large selection areas (white king image, black king image, or surrounding area)

The test verifies that the entire selection areas are now clickable and provide proper visual feedback.