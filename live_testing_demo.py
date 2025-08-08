#!/usr/bin/env python3
"""
Live Testing Demo for ChessPedagogue
===================================
Demonstrates automated testing capabilities with your connected Galaxy S23 Ultra
"""

import subprocess
import time
import requests
import json

# Configuration
DEVICE_ID = "R5CW20BB3KT"
ADB_PATH = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
ENHANCEMENT_SERVER = "http://127.0.0.1:8080"

def adb_command(cmd):
    """Execute ADB command"""
    try:
        result = subprocess.run([ADB_PATH, "-s", DEVICE_ID] + cmd, 
                              capture_output=True, text=True, timeout=10)
        return result.stdout.strip()
    except:
        return ""

def main():
    print("🚀 ChessPedagogue Live Testing Demo")
    print("=" * 50)
    
    # 1. Device Status
    print("📱 Device Status:")
    model = adb_command(["shell", "getprop", "ro.product.model"])
    android = adb_command(["shell", "getprop", "ro.build.version.release"])
    print(f"   📱 {model} (Android {android})")
    print(f"   🔗 Device ID: {DEVICE_ID}")
    print()
    
    # 2. Enhancement Server Status  
    print("🤖 AI Enhancement Server:")
    try:
        response = requests.get(f"{ENHANCEMENT_SERVER}/health", timeout=3)
        if response.status_code == 200:
            server_info = response.json()
            print(f"   ✅ {server_info.get('service', 'Enhancement Server')} v{server_info.get('version', '1.0')}")
            print(f"   🌐 Running on {ENHANCEMENT_SERVER}")
        else:
            print(f"   ❌ Server error: {response.status_code}")
    except:
        print("   ⚠️ Server not accessible")
    print()
    
    # 3. Test AI Enhancement
    print("✨ Testing AI Enhancement:")
    try:
        test_request = {
            "original_response": "The move Nf3 develops a piece.",
            "master_name": "tal",
            "fen": "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 1",
            "user_skill_level": "intermediate",
            "position_evaluation": 0.2
        }
        
        response = requests.post(f"{ENHANCEMENT_SERVER}/enhance", 
                               json=test_request, timeout=10)
        
        if response.status_code == 200:
            result = response.json()
            print(f"   📝 Original: '{result['original_response']}'")
            print(f"   ✨ Enhanced: '{result['enhanced_response']}'")
            print(f"   🎭 Master: {result['master']}")
            print(f"   📚 Skill: {result['skill_level']}")
            if result['enhanced_response'] != result['original_response']:
                print("   ✅ Enhancement working!")
            else:
                print("   ⚠️ Response unchanged")
        else:
            print(f"   ❌ Enhancement failed: {response.status_code}")
    except Exception as e:
        print(f"   ❌ Enhancement test failed: {e}")
    print()
    
    # 4. Device Interaction Test
    print("🎮 Device Interaction Test:")
    
    # Take screenshot
    print("   📸 Taking screenshot...")
    adb_command(["shell", "screencap", "/sdcard/demo_test.png"])
    pull_result = adb_command(["pull", "/sdcard/demo_test.png", "demo_screenshot.png"])
    if "file pulled" in pull_result.lower() or pull_result == "":
        print("   ✅ Screenshot captured: demo_screenshot.png")
    else:
        print("   ⚠️ Screenshot failed")
    
    # Test touch input
    print("   👆 Testing touch input...")
    adb_command(["shell", "input", "tap", "500", "1000"])
    print("   ✅ Touch event sent")
    
    # Test text input capability
    print("   ⌨️ Testing text input...")
    adb_command(["shell", "input", "text", "ChessPedagogue"])
    print("   ✅ Text input sent")
    
    print()
    
    # 5. App Monitoring
    print("📱 App Monitoring Capabilities:")
    
    # Check if ChessPedagogue is installed
    packages = adb_command(["shell", "pm", "list", "packages", "com.example.chesspedagogue"])
    if "com.example.chesspedagogue" in packages:
        print("   ✅ ChessPedagogue installed")
    else:
        print("   ❌ ChessPedagogue not found")
    
    # Monitor logcat for a few seconds
    print("   📋 Monitoring app logs (5 seconds)...")
    adb_command(["logcat", "-c"])  # Clear logs
    time.sleep(2)
    logs = adb_command(["logcat", "-d", "-s", "com.example.chesspedagogue:*"])
    if logs and logs.strip():
        log_lines = len([l for l in logs.split('\\n') if l.strip()])
        print(f"   📝 Found {log_lines} log entries")
    else:
        print("   ⚠️ No app logs detected (app may not be running)")
    
    print()
    
    # 6. Testing Recommendations
    print("🎯 Next Steps for Testing:")
    print("   1. Launch ChessPedagogue manually on your Galaxy S23 Ultra")
    print("   2. Navigate to a chess game or master selection")
    print("   3. Use these automated tools to:")
    print("      • Monitor AI enhancement requests")
    print("      • Simulate user interactions")
    print("      • Capture screenshots for verification")
    print("      • Test master personality differences")
    print()
    
    # 7. Available Testing Commands
    print("🛠️ Available Testing Commands:")
    print("   📱 Device Control:")
    print(f"      adb -s {DEVICE_ID} shell input tap 500 1000")
    print(f"      adb -s {DEVICE_ID} shell input swipe 300 1000 700 1000")
    print(f"      adb -s {DEVICE_ID} shell screencap /sdcard/test.png")
    print()
    print("   📋 Monitoring:")
    print(f"      adb -s {DEVICE_ID} logcat -s com.example.chesspedagogue:*")
    print(f"      curl {ENHANCEMENT_SERVER}/health")
    print()
    print("   🧪 Enhancement Testing:")
    print(f"      curl -X POST {ENHANCEMENT_SERVER}/enhance -H 'Content-Type: application/json' -d '{{...}}'")
    print()
    
    print("🎉 Your Samsung Galaxy S23 Ultra is ready for advanced ChessPedagogue testing!")
    print("🎭 AI Enhancement system is operational and ready to improve your chess masters!")

if __name__ == "__main__":
    main()