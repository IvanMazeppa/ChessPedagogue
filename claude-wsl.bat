@echo off
cd /d "%~dp0"
wsl -d Ubuntu-24.04 -e bash -c "cd  ~/.claude/local/bin/claude && source ~/.nvm/nvm.sh && nvm use 24.1.0 && claude sonnet"