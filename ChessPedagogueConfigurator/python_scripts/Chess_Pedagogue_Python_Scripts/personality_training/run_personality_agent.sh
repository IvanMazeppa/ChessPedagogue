#!/bin/bash

echo "🎭 Claude Personality Agent Launcher"
echo "==================================="
echo

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    if ! command -v python &> /dev/null; then
        echo "❌ Python not found! Please install Python from https://python.org"
        exit 1
    else
        PYTHON_CMD=python
    fi
else
    PYTHON_CMD=python3
fi

echo "✅ Python found: $($PYTHON_CMD --version)"

# Check if anthropic package is installed
$PYTHON_CMD -c "import anthropic" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "📦 Installing required package: anthropic"
    $PYTHON_CMD -m pip install anthropic
    if [ $? -ne 0 ]; then
        echo "❌ Failed to install anthropic package"
        exit 1
    fi
fi

# Check for API key
if [ -z "$ANTHROPIC_API_KEY" ] && [ -z "$CLAUDE_API_KEY" ]; then
    echo "🔑 Claude API key not found in environment variables"
    echo "Please set ANTHROPIC_API_KEY or the script will prompt you"
    echo
    echo "To set temporarily:"
    echo "  export ANTHROPIC_API_KEY=sk-ant-your-key-here"
    echo
    echo "To set permanently (add to ~/.bashrc or ~/.zshrc):"
    echo "  echo 'export ANTHROPIC_API_KEY=sk-ant-your-key-here' >> ~/.bashrc"
    echo
fi

echo "✅ All checks passed! Starting personality agent..."
echo

# Run the personality agent
$PYTHON_CMD claude_personality_agent.py

echo
echo "🎉 Personality agent finished!"
read -p "Press Enter to continue..."