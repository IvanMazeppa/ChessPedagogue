#!/usr/bin/env python3
"""
Create Android Virtual Device for ChessPedagogue Testing
=======================================================
"""

import subprocess
import os

def run_command(cmd, shell=True):
    """Run command and return output"""
    try:
        result = subprocess.run(cmd, shell=shell, capture_output=True, text=True)
        return result.returncode == 0, result.stdout.strip()
    except:
        return False, ""

def main():
    print("🤖 Creating Android Virtual Device for ChessPedagogue Testing")
    print("=" * 60)
    
    sdk_path = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk"
    
    # Check available system images
    print("📋 Checking available system images...")
    success, output = run_command(f'"{sdk_path}/cmdline-tools/latest/bin/sdkmanager.bat" --list | grep "system-images"')
    
    if success:
        print("✅ System images available")
    else:
        print("⚠️ Installing Android 14 system image...")
        # Install Android 14 system image
        install_cmd = f'"{sdk_path}/cmdline-tools/latest/bin/sdkmanager.bat" "system-images;android-34;google_apis;x86_64"'
        success, _ = run_command(install_cmd)
        
        if success:
            print("✅ System image installed")
        else:
            print("❌ Failed to install system image")
            return
    
    # Create AVD
    print("🔧 Creating Android Virtual Device...")
    avd_name = "ChessPedagogue_Test_AVD"
    
    create_cmd = f'''"{sdk_path}/cmdline-tools/latest/bin/avdmanager.bat" create avd \\
        --name {avd_name} \\
        --package "system-images;android-34;google_apis;x86_64" \\
        --device "pixel_6" \\
        --force'''
    
    success, output = run_command(create_cmd)
    
    if success:
        print(f"✅ Virtual device '{avd_name}' created successfully")
        
        # Start emulator
        print("🚀 Starting emulator...")
        start_cmd = f'"{sdk_path}/emulator/emulator.exe" -avd {avd_name} -no-audio -no-boot-anim &'
        os.system(start_cmd)
        
        print("🎉 Emulator starting!")
        print("📱 You now have a virtual device with full testing access!")
        
    else:
        print("❌ Failed to create virtual device")
        print(f"Error: {output}")

if __name__ == "__main__":
    main()