#!/usr/bin/env python3
"""
Test script to debug missing database loading logs in competitive mode.
This script will simulate the exact flow and check for issues.
"""

import subprocess
import time
import sys
import os

def run_adb_command(cmd):
    """Run an ADB command and return output"""
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=10)
        return result.stdout, result.stderr, result.returncode
    except subprocess.TimeoutExpired:
        print(f"Command timed out: {cmd}")
        return "", "Timeout", 1

def test_database_logging():
    """Test the database loading logs"""
    print("🔍 Testing database loading logs in competitive mode...")
    
    # Check if device is connected
    stdout, stderr, code = run_adb_command("adb devices")
    if "device" not in stdout:
        print("❌ No Android device connected!")
        return False
    
    # Clear logs first
    print("🧹 Clearing logcat...")
    run_adb_command("adb logcat -c")
    
    # Start filtering for database logs
    print("📡 Starting logcat monitoring for database logs...")
    
    # Filter for the specific logs we're looking for
    filter_cmd = 'adb logcat -s PersonalityEngine:D GameDatabaseHelper:D CompetitiveModeActivity:D'
    
    print(f"🎯 Running: {filter_cmd}")
    print("📱 Now launch competitive mode in the app...")
    print("⏰ Monitoring for 30 seconds...")
    
    # Run logcat for 30 seconds
    try:
        process = subprocess.Popen(filter_cmd, shell=True, stdout=subprocess.PIPE, 
                                 stderr=subprocess.PIPE, text=True)
        
        start_time = time.time()
        found_logs = []
        
        while time.time() - start_time < 30:
            line = process.stdout.readline()
            if line:
                print(line.strip())
                
                # Check for specific logs we're looking for
                if "Initializing master data for:" in line:
                    found_logs.append("✅ Found: PersonalityEngine initialization")
                elif "Attempting to import from assets file:" in line:
                    found_logs.append("✅ Found: Database import attempt")
                elif "Found X positions to import" in line or "positions to import" in line:
                    found_logs.append("✅ Found: Position count log")
                
        process.terminate()
        
        print("\n" + "="*50)
        print("📊 RESULTS:")
        if found_logs:
            for log in found_logs:
                print(log)
        else:
            print("❌ No expected database logs found!")
            print("\n🔍 This suggests:")
            print("1. PersonalityEngine.initializeMasterData() is not being called")
            print("2. The logs are being filtered out")
            print("3. The method is failing before reaching the log statements")
        
        return len(found_logs) > 0
        
    except KeyboardInterrupt:
        print("\n⚠️ Monitoring stopped by user")
        return False
    except Exception as e:
        print(f"❌ Error during monitoring: {e}")
        return False

def check_log_filters():
    """Check if there are any log filters that might hide our logs"""
    print("\n🔍 Checking for log filters...")
    
    # Check current log buffer size
    stdout, stderr, code = run_adb_command("adb logcat -g")
    print(f"📊 Log buffer info: {stdout.strip()}")
    
    # Check if our tags are being filtered
    test_tags = ["PersonalityEngine", "GameDatabaseHelper", "CompetitiveModeActivity"]
    
    for tag in test_tags:
        stdout, stderr, code = run_adb_command(f"adb logcat -s {tag}:* --since='2 minutes ago'")
        lines = stdout.strip().split('\n') if stdout.strip() else []
        print(f"📱 Recent logs for {tag}: {len(lines)} lines")

def main():
    print("🧪 Database Logging Test Script")
    print("="*50)
    
    # Check ADB connection
    stdout, stderr, code = run_adb_command("adb devices")
    if code != 0:
        print("❌ ADB not available or no devices connected")
        return
    
    # Check log filters first
    check_log_filters()
    
    # Run the main test
    test_database_logging()
    
    print("\n💡 DEBUGGING TIPS:")
    print("1. Make sure the app is in debug mode")
    print("2. Try manually triggering competitive mode")
    print("3. Check if logs appear with different filter levels")
    print("4. Verify the selected master name is correct")

if __name__ == "__main__":
    main()