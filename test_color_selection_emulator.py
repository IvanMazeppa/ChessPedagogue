#!/usr/bin/env python3
"""
Color Selection Test on Emulator with Real-time Logging
======================================================

Tests the splash screen color selection on your existing emulator
while monitoring logs in real-time to debug the selection issue.
"""

import subprocess
import time
import threading
from datetime import datetime
from typing import Tuple, List

class EmulatorColorSelectionTester:
    def __init__(self):
        self.adb_path = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
        self.device_id = "emulator-5554"
        self.package_name = "com.example.chesspedagogue"
        self.splash_activity = f"{self.package_name}/.SplashActivity"
        
        # Screen dimensions (confirmed for your emulator)
        self.screen_width = 1440
        self.screen_height = 3088
        
        # UI coordinates for emulator
        self.white_selection_coords = (720, 1080)  # White king area
        self.black_selection_coords = (720, 1698)  # Black king area  
        self.start_button_coords = (720, 2624)     # Start button
        
        # Log monitoring
        self.log_entries = []
        self.monitoring = False
        self.log_thread = None
    
    def log(self, message: str):
        timestamp = datetime.now().strftime("%H:%M:%S")
        print(f"[{timestamp}] {message}")
    
    def run_adb_command(self, command: List[str]) -> Tuple[bool, str]:
        """Run ADB command"""
        try:
            full_command = [self.adb_path, "-s", self.device_id] + command
            result = subprocess.run(full_command, capture_output=True, text=True, timeout=30)
            return result.returncode == 0, result.stdout.strip()
        except Exception as e:
            self.log(f"❌ ADB command failed: {e}")
            return False, ""
    
    def start_log_monitoring(self):
        """Start monitoring relevant logs in background"""
        self.log("🔍 Starting log monitoring...")
        self.monitoring = True
        self.log_entries = []
        
        def monitor_logs():
            try:
                # Clear existing logs
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
                
                process = subprocess.Popen(cmd, stdout=subprocess.PIPE, text=True, bufsize=1)
                
                while self.monitoring:
                    line = process.stdout.readline()
                    if line:
                        line = line.strip()
                        # Filter for color selection related logs
                        if any(keyword in line.lower() for keyword in 
                              ["color", "selection", "radio", "splash", "configuration", "white", "black"]):
                            timestamp = datetime.now().strftime("%H:%M:%S")
                            log_entry = f"[{timestamp}] {line}"
                            self.log_entries.append(log_entry)
                            print(f"📋 {line}")
                
                process.terminate()
            except Exception as e:
                self.log(f"❌ Log monitoring error: {e}")
        
        self.log_thread = threading.Thread(target=monitor_logs, daemon=True)
        self.log_thread.start()
    
    def stop_log_monitoring(self):
        """Stop log monitoring"""
        self.monitoring = False
        if self.log_thread:
            self.log_thread.join(timeout=2)
        self.log("✅ Log monitoring stopped")
    
    def check_emulator_ready(self) -> bool:
        """Check if emulator is ready"""
        self.log("📱 Checking emulator status...")
        
        success, output = self.run_adb_command(["get-state"])
        if success and "device" in output:
            # Get device info
            success, model = self.run_adb_command(["shell", "getprop", "ro.product.model"])
            success, android_version = self.run_adb_command(["shell", "getprop", "ro.build.version.release"])
            
            self.log(f"✅ Emulator ready: {model} (Android {android_version})")
            return True
        else:
            self.log("❌ Emulator not ready")
            return False
    
    def install_latest_apk(self) -> bool:
        """Install latest APK on emulator"""
        self.log("📦 Installing latest ChessPedagogue APK...")
        
        apk_path = "app/build/outputs/apk/debug/app-debug.apk"
        success, output = self.run_adb_command(["install", "-r", apk_path])
        
        if success:
            self.log("✅ APK installed successfully")
            return True
        else:
            self.log(f"❌ APK installation failed: {output}")
            return False
    
    def launch_splash_screen(self) -> bool:
        """Launch app to splash screen"""
        self.log("🚀 Launching ChessPedagogue...")
        
        # Force stop existing instance
        self.run_adb_command(["shell", "am", "force-stop", self.package_name])
        time.sleep(1)
        
        # Launch splash activity
        success, output = self.run_adb_command([
            "shell", "am", "start",
            "-n", self.splash_activity,
            "-a", "android.intent.action.MAIN",
            "-c", "android.intent.category.LAUNCHER"
        ])
        
        if success:
            self.log("✅ App launched to splash screen")
            time.sleep(3)  # Wait for splash to load
            return True
        else:
            self.log(f"❌ Launch failed: {output}")
            return False
    
    def tap_screen(self, x: int, y: int, description: str = "") -> bool:
        """Tap screen coordinates"""
        desc = f" ({description})" if description else ""
        self.log(f"👆 Tapping ({x}, {y}){desc}")
        
        success, _ = self.run_adb_command(["shell", "input", "tap", str(x), str(y)])
        if success:
            time.sleep(2)  # Wait for UI response and logs
            return True
        return False
    
    def test_white_selection_with_logs(self) -> bool:
        """Test white selection while monitoring logs"""
        self.log("🤴 Testing WHITE selection with log monitoring...")
        
        x, y = self.white_selection_coords
        success = self.tap_screen(x, y, "WHITE king selection area")
        
        # Wait a moment for logs to capture
        time.sleep(2)
        
        return success
    
    def test_black_selection_with_logs(self) -> bool:
        """Test black selection while monitoring logs"""
        self.log("♛ Testing BLACK selection with log monitoring...")
        
        x, y = self.black_selection_coords
        success = self.tap_screen(x, y, "BLACK king selection area")
        
        # Wait a moment for logs to capture
        time.sleep(2)
        
        return success
    
    def test_start_button_with_logs(self) -> bool:
        """Test start button while monitoring logs"""
        self.log("▶️ Testing start button with log monitoring...")
        
        x, y = self.start_button_coords
        success = self.tap_screen(x, y, "Start/Challenge button")
        
        # Wait for transition and logs
        time.sleep(3)
        
        return success
    
    def analyze_captured_logs(self) -> dict:
        """Analyze the captured logs for issues"""
        self.log("🔬 Analyzing captured logs...")
        
        analysis = {
            "total_logs": len(self.log_entries),
            "color_mentions": [],
            "selection_events": [],
            "configuration_logs": [],
            "issues_found": []
        }
        
        for log_entry in self.log_entries:
            lower_log = log_entry.lower()
            
            # Look for color mentions
            if "color=" in lower_log:
                analysis["color_mentions"].append(log_entry)
            
            # Look for selection events
            if "selection" in lower_log or "radio" in lower_log:
                analysis["selection_events"].append(log_entry)
            
            # Look for configuration
            if "configuration from splash" in lower_log:
                analysis["configuration_logs"].append(log_entry)
        
        # Identify issues
        if not analysis["selection_events"]:
            analysis["issues_found"].append("No selection events detected - click listeners may not be working")
        
        if analysis["configuration_logs"]:
            for config_log in analysis["configuration_logs"]:
                if "color=white" in config_log.lower():
                    analysis["issues_found"].append("Configuration shows white even after black selection")
        
        return analysis
    
    def run_comprehensive_test(self) -> dict:
        """Run comprehensive color selection test with logging"""
        self.log("🧪 Starting Comprehensive Color Selection Test with Logging")
        self.log("=" * 70)
        
        results = {
            "timestamp": datetime.now().isoformat(),
            "device": "emulator-5554",
            "test_type": "color_selection_with_logging",
            "steps": {},
            "logs_captured": [],
            "analysis": {},
            "overall_success": True
        }
        
        try:
            # Start log monitoring first
            self.start_log_monitoring()
            time.sleep(1)  # Let monitoring start
            
            # Test steps
            steps = [
                ("check_emulator", self.check_emulator_ready),
                ("install_apk", self.install_latest_apk),
                ("launch_splash", self.launch_splash_screen),
                ("test_white_selection", self.test_white_selection_with_logs),
                ("test_black_selection", self.test_black_selection_with_logs),
                ("test_start_button", self.test_start_button_with_logs)
            ]
            
            for step_name, step_func in steps:
                self.log(f"🔄 Step: {step_name}")
                try:
                    success = step_func()
                    results["steps"][step_name] = {
                        "success": success,
                        "timestamp": datetime.now().isoformat()
                    }
                    
                    if success:
                        self.log(f"✅ {step_name} completed")
                    else:
                        self.log(f"❌ {step_name} failed")
                        results["overall_success"] = False
                        
                except Exception as e:
                    self.log(f"❌ {step_name} crashed: {e}")
                    results["steps"][step_name] = {
                        "success": False,
                        "error": str(e),
                        "timestamp": datetime.now().isoformat()
                    }
                    results["overall_success"] = False
            
            # Stop monitoring and analyze
            time.sleep(2)  # Let final logs come in
            self.stop_log_monitoring()
            
            # Analyze logs
            results["logs_captured"] = self.log_entries
            results["analysis"] = self.analyze_captured_logs()
            
        except Exception as e:
            self.log(f"❌ Test crashed: {e}")
            results["overall_success"] = False
            self.stop_log_monitoring()
        
        return results
    
    def generate_report(self, results: dict) -> str:
        """Generate detailed test report"""
        report = []
        report.append("=" * 70)
        report.append("🧪 Color Selection Test with Real-time Logging")
        report.append("=" * 70)
        report.append(f"📅 Test Time: {results['timestamp']}")
        report.append(f"📱 Device: {results['device']}")
        report.append(f"🎯 Overall Result: {'✅ PASS' if results['overall_success'] else '❌ FAIL'}")
        report.append("")
        
        # Test steps
        report.append("📋 Test Steps:")
        report.append("-" * 50)
        for step_name, step_result in results["steps"].items():
            status = "✅ PASS" if step_result["success"] else "❌ FAIL"
            report.append(f"{step_name:20} | {status}")
            if "error" in step_result:
                report.append(f"                     | Error: {step_result['error']}")
        
        # Log analysis
        report.append("")
        report.append("🔬 Log Analysis:")
        report.append("-" * 30)
        analysis = results.get("analysis", {})
        
        report.append(f"Total logs captured: {analysis.get('total_logs', 0)}")
        report.append(f"Color mentions: {len(analysis.get('color_mentions', []))}")
        report.append(f"Selection events: {len(analysis.get('selection_events', []))}")
        report.append(f"Configuration logs: {len(analysis.get('configuration_logs', []))}")
        
        # Issues found
        issues = analysis.get("issues_found", [])
        if issues:
            report.append("")
            report.append("🚨 Issues Detected:")
            for issue in issues:
                report.append(f"   • {issue}")
        
        # Key logs
        report.append("")
        report.append("🔍 Key Log Entries:")
        report.append("-" * 40)
        
        for log_type in ["color_mentions", "configuration_logs", "selection_events"]:
            logs = analysis.get(log_type, [])
            if logs:
                report.append(f"\\n{log_type.replace('_', ' ').title()}:")
                for log_entry in logs[-3:]:  # Show last 3 entries
                    report.append(f"   {log_entry}")
        
        return "\\n".join(report)


def main():
    """Main function"""
    print("🧪 ChessPedagogue Color Selection Test with Real-time Logging")
    print("=" * 70)
    print("📱 Testing on: emulator-5554")
    print("🔍 Will monitor logs in real-time to debug selection issues")
    print()
    
    tester = EmulatorColorSelectionTester()
    
    # Run comprehensive test
    results = tester.run_comprehensive_test()
    
    # Generate and display report
    report = tester.generate_report(results)
    print("\\n" + report)
    
    # Save results
    with open("color_selection_debug_results.json", "w") as f:
        import json
        json.dump(results, f, indent=2)
    
    print("\\n💾 Detailed results saved to color_selection_debug_results.json")
    
    return 0 if results["overall_success"] else 1

if __name__ == "__main__":
    exit(main())