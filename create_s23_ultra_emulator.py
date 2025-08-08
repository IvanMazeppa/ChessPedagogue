#!/usr/bin/env python3
"""
Create Samsung Galaxy S23 Ultra Emulator for Testing
===================================================

This script creates a Samsung S23 Ultra emulator for more reliable testing
without video overlay interference.
"""

import subprocess
import time
import os

class S23UltraEmulatorCreator:
    def __init__(self):
        self.android_home = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk"
        self.avdmanager = f"{self.android_home}/cmdline-tools/latest/bin/avdmanager"
        self.emulator = f"{self.android_home}/emulator/emulator"
        self.adb = f"{self.android_home}/platform-tools/adb.exe"
        
        # S23 Ultra specifications
        self.avd_name = "Galaxy_S23_Ultra_API_34"
        self.device_profile = "Galaxy S23 Ultra"
        self.system_image = "system-images;android-34;google_apis;x86_64"
    
    def log(self, message: str):
        print(f"📱 {message}")
    
    def check_system_image(self):
        """Check if required system image is installed"""
        self.log("Checking for Android 34 system image...")
        
        try:
            result = subprocess.run([
                f"{self.android_home}/cmdline-tools/latest/bin/sdkmanager",
                "--list_installed"
            ], capture_output=True, text=True, timeout=30)
            
            if self.system_image in result.stdout:
                self.log("✅ Android 34 system image is installed")
                return True
            else:
                self.log("❌ Android 34 system image not found")
                return False
        except Exception as e:
            self.log(f"Error checking system image: {e}")
            return False
    
    def create_avd(self):
        """Create the S23 Ultra AVD"""
        self.log("Creating Galaxy S23 Ultra emulator...")
        
        try:
            # Create AVD command
            cmd = [
                self.avdmanager,
                "create", "avd",
                "-n", self.avd_name,
                "-k", self.system_image,
                "-d", self.device_profile
            ]
            
            # Run with 'no' input for custom hardware profile
            process = subprocess.Popen(cmd, stdin=subprocess.PIPE, 
                                     stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            
            stdout, stderr = process.communicate(input="no\n")
            
            if process.returncode == 0:
                self.log("✅ S23 Ultra emulator created successfully!")
                return True
            else:
                self.log(f"❌ Failed to create emulator: {stderr}")
                return False
                
        except Exception as e:
            self.log(f"Error creating AVD: {e}")
            return False
    
    def configure_avd(self):
        """Configure AVD settings for optimal performance"""
        self.log("Configuring emulator settings...")
        
        avd_path = f"{os.path.expanduser('~')}/.android/avd/{self.avd_name}.avd"
        config_file = f"{avd_path}/config.ini"
        
        try:
            # Read existing config
            if os.path.exists(config_file):
                with open(config_file, 'r') as f:
                    config = f.read()
                
                # Add optimizations
                optimizations = """
# Performance optimizations
hw.gpu.enabled=yes
hw.gpu.mode=host
hw.ramSize=8192
vm.heapSize=512
hw.accelerometer=yes
hw.sensors.orientation=yes
hw.sensors.proximity=yes
"""
                
                # Write back with optimizations
                with open(config_file, 'w') as f:
                    f.write(config + optimizations)
                
                self.log("✅ Emulator configured for optimal performance")
                return True
        except Exception as e:
            self.log(f"Warning: Could not optimize config: {e}")
            return True  # Don't fail for this
    
    def start_emulator(self):
        """Start the S23 Ultra emulator"""
        self.log("Starting Galaxy S23 Ultra emulator...")
        
        try:
            cmd = [
                self.emulator,
                "-avd", self.avd_name,
                "-gpu", "host",
                "-memory", "8192",
                "-cores", "4",
                "-no-snapshot-save",
                "-wipe-data"
            ]
            
            self.log("🚀 Launching emulator (this may take a few minutes)...")
            
            # Start emulator in background
            process = subprocess.Popen(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            
            # Wait for emulator to boot
            self.log("⏳ Waiting for emulator to boot...")
            
            max_wait = 120  # 2 minutes
            wait_time = 0
            
            while wait_time < max_wait:
                try:
                    result = subprocess.run([self.adb, "devices"], 
                                          capture_output=True, text=True, timeout=10)
                    
                    if "emulator-5554\tdevice" in result.stdout:
                        self.log("✅ Emulator is ready!")
                        self.log("📱 Device ID: emulator-5554")
                        return True
                        
                except:
                    pass
                
                time.sleep(5)
                wait_time += 5
                print(".", end="", flush=True)
            
            self.log("❌ Emulator failed to boot within 2 minutes")
            return False
            
        except Exception as e:
            self.log(f"Error starting emulator: {e}")
            return False
    
    def install_app(self):
        """Install ChessPedagogue on the emulator"""
        self.log("Installing ChessPedagogue on emulator...")
        
        apk_path = "app/build/outputs/apk/debug/app-debug.apk"
        
        if not os.path.exists(apk_path):
            self.log("❌ APK not found. Please build the app first:")
            self.log("   ./gradlew assembleDebug")
            return False
        
        try:
            result = subprocess.run([
                self.adb, "-s", "emulator-5554", 
                "install", "-r", apk_path
            ], capture_output=True, text=True, timeout=60)
            
            if result.returncode == 0:
                self.log("✅ ChessPedagogue installed on emulator")
                return True
            else:
                self.log(f"❌ Failed to install app: {result.stderr}")
                return False
                
        except Exception as e:
            self.log(f"Error installing app: {e}")
            return False
    
    def setup_complete_emulator(self):
        """Complete setup process"""
        self.log("🚀 Setting up Samsung Galaxy S23 Ultra emulator for testing")
        self.log("=" * 60)
        
        steps = [
            ("Checking system image", self.check_system_image),
            ("Creating AVD", self.create_avd),
            ("Configuring settings", self.configure_avd),
            ("Starting emulator", self.start_emulator),
            ("Installing app", self.install_app)
        ]
        
        for step_name, step_func in steps:
            self.log(f"🔄 {step_name}...")
            if not step_func():
                self.log(f"❌ Failed at: {step_name}")
                return False
        
        self.log("🎉 S23 Ultra emulator setup complete!")
        self.log("📱 You can now test on: emulator-5554")
        self.log("🧪 Ready for automated testing without video overlay interference")
        
        return True


def main():
    creator = S23UltraEmulatorCreator()
    
    print("📱 Samsung Galaxy S23 Ultra Emulator Setup")
    print("=" * 50)
    print("This will create a clean S23 Ultra emulator for testing")
    print("without video overlay interference.")
    print()
    
    choice = input("Continue? (y/n): ").lower().strip()
    
    if choice == 'y':
        success = creator.setup_complete_emulator()
        if success:
            print("\n🎯 Next steps:")
            print("1. Test color selection on clean emulator")
            print("2. Use log monitor to debug selection issues")
            print("3. Run: python3 monitor_app_logs.py")
        return 0 if success else 1
    else:
        print("👋 Setup cancelled")
        return 0

if __name__ == "__main__":
    exit(main())