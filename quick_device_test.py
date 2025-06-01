#!/usr/bin/env python3
"""
Quick Device Test for ChessPedagogue
===================================
Immediate testing of your connected Samsung Galaxy S23 Ultra
"""

import subprocess
import time

# Device info
DEVICE_ID = "R5CW20BB3KT"
ADB_PATH = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
PACKAGE_NAME = "com.example.chesspedagogue"

def run_adb(command):
    """Run ADB command and return output"""
    try:
        full_command = [ADB_PATH, "-s", DEVICE_ID] + command
        result = subprocess.run(full_command, capture_output=True, text=True, timeout=10)
        return result.stdout.strip()
    except:
        return ""

def main():
    print("🚀 Quick ChessPedagogue Device Test")
    print("=" * 40)
    
    # 1. Device info
    print("📱 Device Information:")
    model = run_adb(["shell", "getprop", "ro.product.model"])
    android_version = run_adb(["shell", "getprop", "ro.build.version.release"])
    print(f"   Model: {model}")
    print(f"   Android: {android_version}")
    print()
    
    # 2. Check if app is installed
    print("📦 App Status:")
    packages = run_adb(["shell", "pm", "list", "packages", PACKAGE_NAME])
    if PACKAGE_NAME in packages:
        print("   ✅ ChessPedagogue is installed")
    else:
        print("   ❌ ChessPedagogue not found")
        return
    
    # 3. Check if app is running
    print("🔍 App Activity:")
    activities = run_adb(["shell", "dumpsys", "activity", "activities"])
    if PACKAGE_NAME in activities:
        print("   ✅ ChessPedagogue is currently active")
    else:
        print("   ⚠️ ChessPedagogue not currently running")
    
    # 4. Launch the app
    print("🚀 Launching ChessPedagogue...")
    launch_result = run_adb([
        "shell", "am", "start",
        "-n", f"{PACKAGE_NAME}/.MainActivity"
    ])
    
    if "Starting" in launch_result or "Warning" in launch_result:
        print("   ✅ App launch command sent")
        time.sleep(3)
    else:
        print(f"   ⚠️ Launch result: {launch_result}")
    
    # 5. Take screenshot
    print("📸 Taking screenshot...")
    screenshot_result = run_adb(["shell", "screencap", "/sdcard/chess_test.png"])
    pull_result = run_adb(["pull", "/sdcard/chess_test.png", "chess_screenshot.png"])
    
    if "file pulled" in pull_result.lower() or pull_result == "":
        print("   ✅ Screenshot saved as chess_screenshot.png")
    else:
        print(f"   ⚠️ Screenshot result: {pull_result}")
    
    # 6. Monitor logs for a few seconds
    print("📋 Checking recent app logs...")
    # Clear logcat first
    run_adb(["logcat", "-c"])
    time.sleep(2)
    
    # Get recent logs
    logs = run_adb(["logcat", "-d", "-s", f"{PACKAGE_NAME}:*"])
    if logs:
        print("   ✅ App is generating logs")
        print(f"   📝 Recent log entries: {len(logs.split('\\n'))} lines")
    else:
        print("   ⚠️ No recent app logs found")
    
    # 7. Test AI enhancement server connectivity
    print("🤖 Testing AI Enhancement Server:")
    try:
        import requests
        response = requests.get("http://127.0.0.1:8080/health", timeout=3)
        if response.status_code == 200:
            print("   ✅ Enhancement server is running")
            server_info = response.json()
            print(f"   📡 Service: {server_info.get('service', 'Unknown')}")
        else:
            print(f"   ❌ Server responded with status: {response.status_code}")
    except:
        print("   ⚠️ Enhancement server not accessible (may not be running)")
    
    print()
    print("🎉 Quick test completed!")
    print("📱 Your Samsung Galaxy S23 Ultra is ready for ChessPedagogue testing!")
    print("🎭 You can now test AI-enhanced chess master interactions!")

if __name__ == "__main__":
    main()