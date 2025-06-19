#!/usr/bin/env python3
"""
Debug script to test conversation generation in ChessPedagogue
"""

import subprocess
import time
import os

def run_adb_command(command):
    """Run an ADB command and return output"""
    try:
        windows_adb = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
        if os.path.exists(windows_adb):
            full_command = command.replace("adb ", f'"{windows_adb}" ')
        else:
            full_command = command
            
        result = subprocess.run(full_command, capture_output=True, text=True, shell=True)
        return result.returncode == 0, result.stdout.strip()
    except Exception as e:
        print(f"Command failed: {e}")
        return False, ""

def monitor_conversation_logs():
    """Monitor logs for conversation activity"""
    print("🔍 Monitoring conversation logs...")
    try:
        windows_adb = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
        
        # Get recent logs
        result = subprocess.run([
            windows_adb, "-s", "emulator-5554", "logcat", "-d", "-s",
            "SpectatorGameViewModel:*", "SpectatorConversationOrchestrator:*", 
            "ChessMasterResponsesManager:*"
        ], capture_output=True, text=True)
        
        if result.returncode == 0:
            lines = result.stdout.split('\n')
            print("📊 Recent conversation-related logs:")
            conversation_count = 0
            for line in lines[-100:]:  # Last 100 lines
                if any(keyword in line.lower() for keyword in [
                    'conversation', 'opening', 'dialogue', 'generateenhanced',
                    'startconversation', 'responsestart', 'responsechunk'
                ]):
                    print(f"  {line}")
                    conversation_count += 1
            
            if conversation_count == 0:
                print("⚠️ No conversation-related logs found!")
            else:
                print(f"✅ Found {conversation_count} conversation-related log entries")
            
            return conversation_count > 0
        else:
            print("❌ Failed to get logs")
            return False
    except Exception as e:
        print(f"❌ Error monitoring logs: {e}")
        return False

def trigger_test_conversation():
    """Try to trigger a test conversation"""
    print("🧪 Attempting to trigger test conversation...")
    
    # Start spectator game (this should trigger opening conversation)
    success, output = run_adb_command(
        'adb -s emulator-5554 shell am start -n com.example.chesspedagogue/.SpectatorGameActivity'
    )
    
    if success:
        print("✅ Launched SpectatorGameActivity")
        print("⏱️ Waiting 10 seconds for game to initialize...")
        time.sleep(10)
        
        # Monitor for conversation activity
        return monitor_conversation_logs()
    else:
        print(f"❌ Failed to launch SpectatorGameActivity: {output}")
        return False

def main():
    print("🧪 ChessPedagogue Conversation Debug Tool")
    print("=" * 50)
    
    # Check if app is installed
    print("📱 Checking app installation...")
    success, output = run_adb_command("adb -s emulator-5554 shell pm list packages | grep chesspedagogue")
    if not success or "chesspedagogue" not in output:
        print("❌ ChessPedagogue not installed. Please install the app first.")
        return 1
    
    print("✅ ChessPedagogue is installed")
    
    # Clear logs first
    print("🧹 Clearing old logs...")
    run_adb_command("adb -s emulator-5554 logcat -c")
    
    # Test conversation triggering
    if trigger_test_conversation():
        print("\n🎉 SUCCESS: Conversation activity detected!")
        print("✅ The conversation system appears to be working.")
    else:
        print("\n❌ ISSUE: No conversation activity detected!")
        print("🔍 Possible issues:")
        print("   1. Opening conversations not triggering")
        print("   2. Responses API connection problems")
        print("   3. Conversation orchestrator initialization issues")
        print("   4. Missing API keys")
    
    # Show recent logs one more time
    print("\n📊 Final log check...")
    monitor_conversation_logs()
    
    return 0

if __name__ == "__main__":
    exit(main())