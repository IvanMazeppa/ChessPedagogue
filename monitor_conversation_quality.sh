#!/bin/bash

echo "🎭 ChessPedagogue Conversation Quality Monitor"
echo "=============================================="
echo "Monitoring Alekhine vs Kasparov conversation improvements..."
echo "Focus: Repetition fixes, fine-tuned model usage, topic variety"
echo ""
echo "🎯 Key indicators to watch for:"
echo "  ✅ ConversationMemory: Topic fatigue and variety"
echo "  ✅ ResponsesAPI: Fine-tuned model calls (not assistant API)"
echo "  ✅ ElevenLabsTTS: Speech deduplication working"
echo "  ✅ ConversationVariety: Phrase repetition prevention"
echo "  ✅ EmotionalIntelligence: Authentic emotional reactions"
echo ""
echo "🚫 What should NOT appear:"
echo "  ❌ Identical phrases within 30 seconds"
echo "  ❌ Assistant API calls for Alekhine/Kasparov"
echo "  ❌ Repeated topics without cooldown"
echo ""
echo "Press Ctrl+C to stop monitoring"
echo "=============================================="
echo ""

# Clear logcat buffer first
adb logcat -c

# Monitor with focused filters for conversation quality
adb logcat | grep -E "(SpectatorConversation|ConversationMemory|ConversationVariety|ResponsesAPI|ElevenLabsTTS|ThreeStage|FineTunedModel|EmotionalIntelligence|EmotionalContext)" | while read line; do
    # Color code important log types
    if echo "$line" | grep -q "ConversationMemory"; then
        echo -e "\033[1;34m🧠 $line\033[0m"  # Blue for memory
    elif echo "$line" | grep -q "ConversationVariety"; then
        echo -e "\033[1;32m🎨 $line\033[0m"  # Green for variety
    elif echo "$line" | grep -q "ResponsesAPI"; then
        echo -e "\033[1;33m🤖 $line\033[0m"  # Yellow for API
    elif echo "$line" | grep -q "ElevenLabsTTS"; then
        echo -e "\033[1;35m🎵 $line\033[0m"  # Magenta for TTS
    elif echo "$line" | grep -q "EmotionalIntelligence\|EmotionalContext"; then
        echo -e "\033[1;31m🎭 $line\033[0m"  # Red for emotions
    elif echo "$line" | grep -q "SpectatorConversation"; then
        echo -e "\033[1;36m👥 $line\033[0m"  # Cyan for spectator
    else
        echo "$line"
    fi
done