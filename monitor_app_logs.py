#!/usr/bin/env python3
"""
Real-time ChessPedagogue Log Monitor
==================================

This script monitors your app's logcat in real-time to help debug issues
without you having to paste large logs.
"""

import subprocess
import time
import threading
import signal
import sys
from datetime import datetime

class LogcatMonitor:
    def __init__(self):
        self.adb_path = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
        self.device_id = "adb-R5CW20BB3KT-VEDwWk._adb-tls-connect._tcp"
        self.package_name = "com.example.chesspedagogue"
        self.monitoring = False
        self.process = None
        
        # Key areas to monitor
        self.key_filters = [
            "SplashActivity",
            "MainActivity", 
            "Color=",
            "Configuration from splash",
            "radioWhite",
            "radioBlack",
            "selection",
            "whiteSelectionLayout",
            "blackSelectionLayout"
        ]
    
    def log(self, message: str):
        timestamp = datetime.now().strftime("%H:%M:%S")
        print(f"[{timestamp}] {message}")
    
    def start_monitoring(self, filter_type="splash_debug"):
        """Start monitoring logcat for specific patterns"""
        self.log("🔍 Starting logcat monitoring for ChessPedagogue...")
        
        if filter_type == "splash_debug":
            # Focus on splash screen color selection
            cmd = [
                self.adb_path, "-s", self.device_id, 
                "logcat", "-s", 
                "SplashActivity:*",
                "MainActivity:*"
            ]
        elif filter_type == "all_app":
            # Monitor all app logs
            cmd = [
                self.adb_path, "-s", self.device_id,
                "logcat", "-s", f"{self.package_name}:*"
            ]
        else:
            # Custom filter
            cmd = [
                self.adb_path, "-s", self.device_id,
                "logcat"
            ]
        
        try:
            # Clear existing logs first
            subprocess.run([self.adb_path, "-s", self.device_id, "logcat", "-c"], 
                         capture_output=True, timeout=5)
            
            self.log(f"✅ Starting logcat with filter: {filter_type}")
            self.monitoring = True
            
            # Start logcat process
            self.process = subprocess.Popen(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
                bufsize=1,
                universal_newlines=True
            )
            
            # Monitor output
            for line in self.process.stdout:
                if not self.monitoring:
                    break
                
                line = line.strip()
                if line:
                    # Check if line contains relevant information
                    if any(filter_word.lower() in line.lower() for filter_word in self.key_filters):
                        self.log(f"🎯 {line}")
                    elif filter_type == "all_app":
                        print(line)
                        
        except KeyboardInterrupt:
            self.log("🛑 Monitoring stopped by user")
        except Exception as e:
            self.log(f"❌ Error monitoring logcat: {e}")
        finally:
            self.stop_monitoring()
    
    def stop_monitoring(self):
        """Stop logcat monitoring"""
        self.monitoring = False
        if self.process:
            self.process.terminate()
            self.process = None
        self.log("✅ Logcat monitoring stopped")
    
    def capture_splash_test_logs(self, duration=30):
        """Capture logs specifically during splash screen testing"""
        self.log("🧪 Starting focused splash screen test log capture...")
        self.log(f"⏱️ Will monitor for {duration} seconds")
        self.log("👆 Please perform your splash screen interactions now...")
        
        try:
            # Clear logs
            subprocess.run([self.adb_path, "-s", self.device_id, "logcat", "-c"], 
                         capture_output=True, timeout=5)
            
            # Start monitoring
            cmd = [
                self.adb_path, "-s", self.device_id,
                "logcat", "-s",
                "SplashActivity:*",
                "MainActivity:*",
                "RadioGroup:*"
            ]
            
            end_time = time.time() + duration
            process = subprocess.Popen(cmd, stdout=subprocess.PIPE, text=True)
            
            relevant_logs = []
            
            while time.time() < end_time:
                if process.poll() is not None:
                    break
                
                line = process.stdout.readline()
                if line:
                    line = line.strip()
                    # Filter for color selection related logs
                    if any(keyword in line.lower() for keyword in 
                          ["color", "selection", "radio", "splash", "configuration", "white", "black"]):
                        timestamp = datetime.now().strftime("%H:%M:%S")
                        relevant_logs.append(f"[{timestamp}] {line}")
                        print(f"🎯 {line}")
            
            process.terminate()
            
            self.log(f"✅ Captured {len(relevant_logs)} relevant log entries")
            
            # Save logs to file
            with open("splash_test_logs.txt", "w") as f:
                f.write(f"Splash Screen Test Logs - {datetime.now()}\n")
                f.write("=" * 50 + "\n")
                for log_entry in relevant_logs:
                    f.write(log_entry + "\n")
            
            self.log("💾 Logs saved to splash_test_logs.txt")
            return relevant_logs
            
        except Exception as e:
            self.log(f"❌ Error capturing logs: {e}")
            return []


def main():
    monitor = LogcatMonitor()
    
    def signal_handler(sig, frame):
        print("\n🛑 Stopping log monitor...")
        monitor.stop_monitoring()
        sys.exit(0)
    
    signal.signal(signal.SIGINT, signal_handler)
    
    print("🔍 ChessPedagogue Log Monitor")
    print("=" * 40)
    print("Commands:")
    print("  1 - Monitor splash screen debugging")
    print("  2 - Monitor all app logs") 
    print("  3 - Capture splash test logs (30 seconds)")
    print("  q - Quit")
    
    choice = input("\nSelect option: ").strip()
    
    if choice == "1":
        monitor.start_monitoring("splash_debug")
    elif choice == "2":
        monitor.start_monitoring("all_app")
    elif choice == "3":
        monitor.capture_splash_test_logs(30)
    elif choice.lower() == "q":
        print("👋 Goodbye!")
        sys.exit(0)
    else:
        print("❌ Invalid option")

if __name__ == "__main__":
    main()