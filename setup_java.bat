@echo off
echo Setting up Java environment for Android development...

REM Check if Java is already installed
java -version >nul 2>&1
if %errorlevel% == 0 (
    echo Java is already installed!
    java -version
    goto :config_gradle
)

echo Java not found. Please install OpenJDK 17 or later.
echo.
echo Download from: https://adoptium.net/temurin/releases/
echo Choose "OpenJDK 17 LTS" for Windows x64
echo.
echo After installation, come back and run this script again.
pause
exit /b 1

:config_gradle
echo.
echo Setting up Gradle configuration for better memory management...

REM Create gradle.properties if it doesn't exist
if not exist gradle.properties (
    echo Creating gradle.properties...
    (
        echo # Gradle JVM Options for better performance
        echo org.gradle.jvmargs=-Xmx6g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8
        echo org.gradle.parallel=true
        echo org.gradle.caching=true
        echo org.gradle.configureondemand=true
        echo.
        echo # Android build optimizations
        echo android.enableJetifier=true
        echo android.useAndroidX=true
        echo android.enableR8.fullMode=true
        echo android.enableBuildCache=true
    ) > gradle.properties
    echo gradle.properties created successfully!
) else (
    echo gradle.properties already exists. Updating JVM options...
    REM Backup existing file
    copy gradle.properties gradle.properties.backup >nul
    
    REM Update or add JVM args
    powershell -Command "(Get-Content gradle.properties) -replace '^org\.gradle\.jvmargs=.*', 'org.gradle.jvmargs=-Xmx6g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8' | Out-File -encoding ASCII gradle.properties"
)

echo.
echo Configuration complete! You can now build the project with:
echo   gradlew clean assembleDebug
echo.
echo If you still get memory errors, try:
echo   gradlew clean assembleDebug --max-workers=2
echo.
pause