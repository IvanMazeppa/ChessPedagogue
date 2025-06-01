@echo off
echo Fixing build issues for ChessPedagogue...
echo.

echo Step 1: Killing any existing Gradle daemons...
gradlew --stop

echo.
echo Step 2: Cleaning build cache...
rmdir /s /q .gradle 2>nul
rmdir /s /q build 2>nul
rmdir /s /q app\build 2>nul

echo.
echo Step 3: Verifying Java installation...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java not found in PATH
    echo Please set JAVA_HOME and add Java to PATH
    pause
    exit /b 1
)

echo.
echo Step 4: Testing Gradle configuration...
gradlew --version
if %errorlevel% neq 0 (
    echo ERROR: Gradle configuration failed
    pause
    exit /b 1
)

echo.
echo Step 5: Attempting clean build...
gradlew clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed - check the output above
    pause
    exit /b 1
)

echo.
echo Step 6: Building debug APK...
gradlew assembleDebug --max-workers=2
if %errorlevel% neq 0 (
    echo ERROR: Build failed - check the output above
    echo.
    echo Try these solutions:
    echo 1. Install Java 17 from https://adoptium.net/temurin/releases/
    echo 2. Update gradle.properties with correct Java 17 path
    echo 3. Run: gradlew assembleDebug --stacktrace for more details
    pause
    exit /b 1
)

echo.
echo ✅ SUCCESS! Build completed successfully!
echo APK location: app\build\outputs\apk\debug\app-debug.apk
echo.
pause