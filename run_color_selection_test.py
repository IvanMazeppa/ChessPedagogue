#!/usr/bin/env python3
"""
Quick runner for the Splash Color Selection Test
==============================================

This script helps you quickly run the color selection test on your physical device.
"""

import subprocess
import sys
import os

def main():
    print("🎨 ChessPedagogue Color Selection Test Runner")
    print("=" * 50)
    
    # Check if we're in the right directory
    if not os.path.exists("automated_testing/splash_color_selection_test.py"):
        print("❌ Please run this script from the project root directory")
        return 1
    
    print("🔍 Checking for connected devices...")
    
    # Check for connected devices
    try:
        # Try Windows ADB path first (for WSL users)
        adb_paths = [
            "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe",
            "adb"  # Fallback
        ]
        
        adb_cmd = None
        for path in adb_paths:
            try:
                subprocess.run([path, "version"], capture_output=True, timeout=5)
                adb_cmd = path
                break
            except:
                continue
        
        if not adb_cmd:
            adb_cmd = "adb"
        
        result = subprocess.run([adb_cmd, "devices"], capture_output=True, text=True, timeout=10)
        lines = result.stdout.strip().split('\n')[1:]  # Skip header
        devices = []
        
        for line in lines:
            if '\tdevice' in line:
                device_id = line.split('\t')[0]
                devices.append(device_id)
        
        if not devices:
            print("❌ No devices connected!")
            print("")
            print("📱 To connect your device:")
            print("   1. Connect via USB cable")
            print("   2. Enable Developer Options")
            print("   3. Enable USB Debugging")
            print("   4. Authorize the computer when prompted")
            print("   5. Run 'adb devices' to verify connection")
            return 1
        
        if len(devices) == 1:
            device_id = devices[0]
            print(f"✅ Found device: {device_id}")
        else:
            print(f"🔍 Found {len(devices)} devices:")
            for i, device in enumerate(devices):
                print(f"   {i+1}. {device}")
            
            # Automatically select physical device if available
            physical_devices = [d for d in devices if not d.startswith("emulator-")]
            if physical_devices:
                device_id = physical_devices[0]
                print(f"🎯 Automatically selected physical device: {device_id}")
            else:
                device_id = devices[0]
                print(f"🎯 Using device: {device_id}")
        
        print(f"🎯 Testing on device: {device_id}")
        print("=" * 50)
        
        # Run the test
        test_script = "automated_testing/splash_color_selection_test.py"
        result = subprocess.run([sys.executable, test_script, device_id])
        
        return result.returncode
        
    except FileNotFoundError:
        print("❌ ADB not found! Please install Android SDK platform-tools")
        return 1
    except Exception as e:
        print(f"❌ Error: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())