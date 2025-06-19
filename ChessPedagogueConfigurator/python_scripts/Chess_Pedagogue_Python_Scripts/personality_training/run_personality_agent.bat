@echo off
echo 🎭 Claude Personality Agent Launcher
echo ===================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python not found! Please install Python from https://python.org
    pause
    exit /b 1
)

REM Check if anthropic package is installed
python -c "import anthropic" >nul 2>&1
if %errorlevel% neq 0 (
    echo 📦 Installing required package: anthropic
    pip install anthropic
    if %errorlevel% neq 0 (
        echo ❌ Failed to install anthropic package
        pause
        exit /b 1
    )
)

REM Check for API key
if "%ANTHROPIC_API_KEY%"=="" (
    echo 🔑 Claude API key not found in environment variables
    echo Please set ANTHROPIC_API_KEY or the script will prompt you
    echo.
    echo To set permanently: 
    echo   set ANTHROPIC_API_KEY=sk-ant-your-key-here
    echo.
)

echo ✅ All checks passed! Starting personality agent...
echo.

REM Run the personality agent
python claude_personality_agent.py

echo.
echo 🎉 Personality agent finished!
pause