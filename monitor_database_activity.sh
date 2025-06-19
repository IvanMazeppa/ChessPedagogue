#!/bin/bash

echo "🔍 ChessPedagogue Database Activity Monitor"
echo "=========================================="
echo "Waiting for device to connect..."

# Wait for device to be ready
while [[ $(adb devices | grep -v "List of devices" | wc -l) -eq 0 ]]; do
    echo "⏳ Waiting for emulator/device..."
    sleep 2
done

echo "✅ Device connected!"
echo "🧹 Clearing logcat buffer..."
adb logcat -c

echo "🎭 Starting database activity monitoring..."
echo "   Watching for: GameDatabaseHelper, RelationshipPersistence, EmotionalIntelligence"
echo "   Press Ctrl+C to stop monitoring"
echo ""

# Monitor specific database-related logs with timestamps
adb logcat -v time -s \
    GameDatabaseHelper:* \
    RelationshipPersistence:* \
    EmotionalIntelligence:* \
    SpectatorConversationOrchestrator:* \
    ChessMasterResponsesManager:* \
    | grep -E "(🧠|🎭|👥|🌟|💾|📊|✅|❌|🔄)" \
    | while read line; do
        echo "📝 $line"
    done