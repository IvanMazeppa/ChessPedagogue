@echo off
echo 🎮 Starting Chess Pedagogue Configurator...
echo.

cd /d "%~dp0"

echo 📁 Current directory: %CD%
echo.

echo 🔍 Checking Java version...
java -version
if %ERRORLEVEL% neq 0 (
    echo ❌ Java not found! Please make sure Java is installed and in PATH.
    pause
    exit /b 1
)
echo.

echo 📦 Compiling Java sources...
if not exist "target\classes" mkdir "target\classes"

javac -cp "target\lib\*" -d "target\classes" ^
    src\main\java\com\chesspedagogue\configurator\*.java ^
    src\main\java\com\chesspedagogue\configurator\managers\*.java ^
    src\main\java\com\chesspedagogue\configurator\models\*.java ^
    src\main\java\com\chesspedagogue\configurator\tabs\*.java ^
    src\main\java\com\chesspedagogue\configurator\utils\*.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo ⚠️ Compilation failed. Let's try a different approach...
    echo We'll set up IntelliJ properly instead.
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

echo 🚀 Launching GUI...
java --module-path "target\lib" --add-modules javafx.controls,javafx.fxml ^
    -cp "target\classes;target\lib\*" ^
    com.chesspedagogue.configurator.ChessPedagogueConfiguratorSimple

if %ERRORLEVEL% neq 0 (
    echo.
    echo ⚠️ GUI launch failed. This is expected - we need Maven to download JavaFX.
    echo Let's set up IntelliJ properly instead!
    echo.
)

pause