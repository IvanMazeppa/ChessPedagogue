@echo off
echo 🔧 Rebuilding ChessPedagogue with null pointer fixes...
echo.

echo Step 1: Stopping Gradle daemon...
gradlew --stop

echo.
echo Step 2: Cleaning project...
gradlew clean

echo.
echo Step 3: Building debug APK...
gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo.
echo Step 4: Installing on device (if connected)...
adb devices
gradlew installDebug
if %errorlevel% neq 0 (
    echo ⚠️ Install failed - no device connected or other error
    echo You can manually install: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ✅ App installed successfully!
)

echo.
echo 🎯 Fixed issues:
echo   - Null pointer crash in ChessMasterResponsesManager
echo   - Missing callbacks in SpectatorConversationOrchestrator  
echo   - Added null checking for robustness
echo.
echo ✅ Ready to test spectator mode!
echo.
pause