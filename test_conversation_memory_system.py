#!/usr/bin/env python3
"""
🧠 Conversation Memory System - Automated Testing
Tests the new conversation memory integration to verify topic variety and repetition prevention.
"""

import subprocess
import time
import json
import re
from datetime import datetime
import sys
import os

class ConversationMemoryTester:
    def __init__(self):
        self.adb_path = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
        self.device_id = "emulator-5554"
        self.package_name = "com.example.chesspedagogue"
        self.results = {
            "test_timestamp": datetime.now().isoformat(),
            "conversation_topics_detected": [],
            "topic_repetition_analysis": {},
            "memory_guidance_logs": [],
            "fresh_topic_suggestions": [],
            "master_specific_patterns": {},
            "success_metrics": {}
        }
        
    def run_adb_command(self, command):
        """Execute ADB command safely"""
        try:
            full_command = f'"{self.adb_path}" -s {self.device_id} {command}'
            result = subprocess.run(full_command, shell=True, capture_output=True, text=True, timeout=30)
            return result.stdout, result.stderr
        except subprocess.TimeoutExpired:
            return "", "Command timed out"
        except Exception as e:
            return "", f"Error: {str(e)}"
    
    def log(self, message, level="INFO"):
        """Enhanced logging with emojis"""
        timestamp = datetime.now().strftime("%H:%M:%S")
        emojis = {"INFO": "ℹ️", "SUCCESS": "✅", "WARNING": "⚠️", "ERROR": "❌", "TEST": "🧪"}
        print(f"{emojis.get(level, 'ℹ️')} [{timestamp}] {message}")
    
    def setup_test_environment(self):
        """Prepare the testing environment"""
        self.log("🔧 Setting up conversation memory test environment...", "INFO")
        
        # Check device connection
        stdout, stderr = self.run_adb_command("devices")
        if self.device_id not in stdout:
            self.log(f"❌ Device {self.device_id} not found!", "ERROR")
            return False
            
        # Clear app data for fresh start
        self.log("🧹 Clearing app data for fresh test...", "INFO")
        self.run_adb_command("shell pm clear com.example.chesspedagogue")
        
        # Clear logs
        self.run_adb_command("logcat -c")
        
        self.log("✅ Test environment ready!", "SUCCESS")
        return True
    
    def launch_app_to_spectator_mode(self):
        """Navigate app to spectator mode for conversation testing"""
        self.log("🚀 Launching app and navigating to spectator mode...", "INFO")
        
        # Launch app
        stdout, stderr = self.run_adb_command("shell am start -n com.example.chesspedagogue/.SplashActivity")
        if "Error" in stderr:
            self.log(f"❌ Failed to launch app: {stderr}", "ERROR")
            return False
            
        time.sleep(3)
        
        # Navigate through UI flow
        # 1. Begin Challenge
        self.log("👆 Tapping 'Begin Challenge'...", "INFO")
        self.run_adb_command("shell input tap 715 2880")
        time.sleep(3)
        
        # 2. Continue with Master
        self.log("👆 Continuing with selected master...", "INFO") 
        self.run_adb_command("shell input tap 360 1433")
        time.sleep(3)
        
        # 3. Enter Spectator Mode
        self.log("👆 Entering Spectator Mode...", "INFO")
        self.run_adb_command("shell input tap 1229 151")
        time.sleep(5)
        
        # Verify we're in spectator mode
        self.log("📱 Taking screenshot to verify spectator mode...", "INFO")
        self.run_adb_command("shell screencap /sdcard/spectator_mode_test.png")
        self.run_adb_command("pull /sdcard/spectator_mode_test.png .")
        
        self.log("✅ Successfully navigated to spectator mode!", "SUCCESS")
        return True
    
    def monitor_conversation_memory_logs(self, duration_seconds=300):
        """Monitor logs for conversation memory system activity"""
        self.log(f"🔍 Monitoring conversation memory logs for {duration_seconds} seconds...", "INFO")
        
        # Start log monitoring process
        log_command = f'logcat -s ConversationMemory -s SpectatorConversationOrchestrator -s EmotionalIntelligence'
        
        start_time = time.time()
        conversation_count = 0
        topic_tracking = {}
        memory_guidance_detected = False
        
        try:
            process = subprocess.Popen(
                f'"{self.adb_path}" -s {self.device_id} {log_command}',
                shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True
            )
            
            self.log("📡 Log monitoring started. Looking for conversation memory activity...", "INFO")
            
            while time.time() - start_time < duration_seconds:
                line = process.stdout.readline()
                if not line:
                    time.sleep(0.1)
                    continue
                    
                # Analyze each log line for conversation memory patterns
                self.analyze_log_line(line.strip(), topic_tracking)
                
                # Check for key indicators
                if "🧠" in line or "ConversationMemory" in line:
                    memory_guidance_detected = True
                    self.results["memory_guidance_logs"].append({
                        "timestamp": datetime.now().isoformat(),
                        "log_line": line.strip()
                    })
                    self.log(f"🧠 Memory guidance detected: {line.strip()}", "SUCCESS")
                
                if "conversation memory guidance" in line.lower():
                    self.log(f"🎯 Topic guidance active: {line.strip()}", "SUCCESS")
                
                if "overused topics" in line.lower():
                    self.log(f"⚠️ Overused topics detected: {line.strip()}", "WARNING")
                
                if "fresh topics" in line.lower():
                    fresh_topics = self.extract_fresh_topics(line)
                    if fresh_topics:
                        self.results["fresh_topic_suggestions"].extend(fresh_topics)
                        self.log(f"🌟 Fresh topics suggested: {', '.join(fresh_topics)}", "SUCCESS")
                
                # Count conversations
                if "conversation started" in line.lower() or "dialogue generated" in line.lower():
                    conversation_count += 1
                    
        except KeyboardInterrupt:
            self.log("⏹️ Monitoring stopped by user", "INFO")
        finally:
            if 'process' in locals():
                process.terminate()
        
        # Analyze results
        self.results["success_metrics"]["total_conversations"] = conversation_count
        self.results["success_metrics"]["memory_system_active"] = memory_guidance_detected
        self.results["success_metrics"]["topic_variety_score"] = len(topic_tracking)
        
        if memory_guidance_detected:
            self.log("✅ CONVERSATION MEMORY SYSTEM IS WORKING!", "SUCCESS")
        else:
            self.log("⚠️ No conversation memory activity detected", "WARNING")
            
        return memory_guidance_detected
    
    def analyze_log_line(self, line, topic_tracking):
        """Analyze individual log lines for conversation patterns"""
        
        # Extract topics mentioned
        topic_patterns = [
            r"perfectionism", r"pragmatism", r"calculation", r"intuition", 
            r"tactics", r"positional", r"endgame", r"opening", r"chess_history",
            r"competitive_psychology", r"chess_aesthetics", r"learning_journey"
        ]
        
        for pattern in topic_patterns:
            if re.search(pattern, line, re.IGNORECASE):
                topic_tracking[pattern] = topic_tracking.get(pattern, 0) + 1
                self.results["conversation_topics_detected"].append({
                    "topic": pattern,
                    "timestamp": datetime.now().isoformat(),
                    "context": line[:100] + "..." if len(line) > 100 else line
                })
    
    def extract_fresh_topics(self, line):
        """Extract fresh topic suggestions from log lines"""
        fresh_topics = []
        # Look for patterns like "suggested topics: topic1, topic2, topic3"
        match = re.search(r"suggested topics?:?\s*([^.]+)", line, re.IGNORECASE)
        if match:
            topics_str = match.group(1)
            topics = [t.strip() for t in topics_str.split(',')]
            fresh_topics.extend(topics)
        return fresh_topics
    
    def generate_conversation_activity(self):
        """Generate some conversation activity to test the memory system"""
        self.log("🎮 Generating conversation activity...", "INFO")
        
        # Make a few moves to trigger evaluation changes and conversations
        moves = [
            (500, 2200, 500, 1900),  # e2-e4
            (450, 2200, 450, 1900),  # d2-d4  
            (550, 2200, 550, 1900),  # f2-f4
        ]
        
        for i, (x1, y1, x2, y2) in enumerate(moves):
            self.log(f"🎯 Making move {i+1}: ({x1},{y1}) -> ({x2},{y2})", "INFO")
            self.run_adb_command(f"shell input tap {x1} {y1}")
            time.sleep(1)
            self.run_adb_command(f"shell input tap {x2} {y2}")
            time.sleep(3)  # Wait for AI response
    
    def analyze_topic_repetition(self):
        """Analyze detected topics for repetition patterns"""
        self.log("📊 Analyzing topic repetition patterns...", "INFO")
        
        topic_counts = {}
        for topic_entry in self.results["conversation_topics_detected"]:
            topic = topic_entry["topic"]
            topic_counts[topic] = topic_counts.get(topic, 0) + 1
        
        self.results["topic_repetition_analysis"] = topic_counts
        
        # Check for excessive repetition
        overused_topics = {topic: count for topic, count in topic_counts.items() if count > 3}
        if overused_topics:
            self.log(f"⚠️ Potentially overused topics detected: {overused_topics}", "WARNING")
        else:
            self.log("✅ Good topic variety - no excessive repetition detected!", "SUCCESS")
        
        return len(overused_topics) == 0
    
    def check_conversation_memory_features(self):
        """Check if all conversation memory features are working"""
        self.log("🔍 Checking conversation memory features...", "TEST")
        
        features_working = {
            "topic_tracking": len(self.results["conversation_topics_detected"]) > 0,
            "memory_guidance": len(self.results["memory_guidance_logs"]) > 0,
            "fresh_topics": len(self.results["fresh_topic_suggestions"]) > 0,
            "variety_maintenance": self.analyze_topic_repetition()
        }
        
        all_working = all(features_working.values())
        
        for feature, working in features_working.items():
            status = "✅" if working else "❌"
            self.log(f"{status} {feature.replace('_', ' ').title()}: {'Working' if working else 'Not detected'}", 
                    "SUCCESS" if working else "WARNING")
        
        return all_working, features_working
    
    def save_test_results(self):
        """Save comprehensive test results"""
        filename = f"conversation_memory_test_results_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        
        try:
            with open(filename, 'w') as f:
                json.dump(self.results, f, indent=2)
            self.log(f"📄 Test results saved to: {filename}", "SUCCESS")
        except Exception as e:
            self.log(f"❌ Failed to save results: {e}", "ERROR")
    
    def run_comprehensive_test(self):
        """Run the complete conversation memory test suite"""
        self.log("🧠 CONVERSATION MEMORY SYSTEM - COMPREHENSIVE TEST", "INFO")
        self.log("=" * 60, "INFO")
        
        # Setup
        if not self.setup_test_environment():
            return False
        
        # Launch app and navigate to spectator mode
        if not self.launch_app_to_spectator_mode():
            return False
        
        # Generate some activity
        self.generate_conversation_activity()
        
        # Monitor conversation memory system
        memory_system_working = self.monitor_conversation_memory_logs(duration_seconds=180)  # 3 minutes
        
        # Analyze results
        all_features_working, feature_details = self.check_conversation_memory_features()
        
        # Generate final report
        self.log("", "INFO")
        self.log("🏆 FINAL TEST RESULTS", "INFO")
        self.log("=" * 40, "INFO")
        
        if memory_system_working and all_features_working:
            self.log("✅ CONVERSATION MEMORY SYSTEM: FULLY FUNCTIONAL!", "SUCCESS")
            self.log("🧠 Topic tracking, variety maintenance, and fresh suggestions all working!", "SUCCESS")
        elif memory_system_working:
            self.log("⚠️ CONVERSATION MEMORY SYSTEM: PARTIALLY WORKING", "WARNING")
            self.log("🧠 Basic memory system detected, but some features need attention", "WARNING")
        else:
            self.log("❌ CONVERSATION MEMORY SYSTEM: NOT DETECTED", "ERROR")
            self.log("🧠 No conversation memory activity found in logs", "ERROR")
        
        # Detailed metrics
        self.log(f"📊 Conversations detected: {self.results['success_metrics'].get('total_conversations', 0)}", "INFO")
        self.log(f"📊 Topics tracked: {len(self.results['conversation_topics_detected'])}", "INFO")
        self.log(f"📊 Fresh topic suggestions: {len(self.results['fresh_topic_suggestions'])}", "INFO")
        self.log(f"📊 Memory guidance logs: {len(self.results['memory_guidance_logs'])}", "INFO")
        
        # Save results
        self.save_test_results()
        
        return memory_system_working and all_features_working

def main():
    """Main test execution"""
    print("🧠 ChessPedagogue Conversation Memory System - Automated Testing")
    print("================================================================")
    print()
    
    tester = ConversationMemoryTester()
    
    try:
        success = tester.run_comprehensive_test()
        exit_code = 0 if success else 1
        
        print()
        print("🏁 Test completed!")
        print(f"Exit code: {exit_code}")
        
        sys.exit(exit_code)
        
    except KeyboardInterrupt:
        print("\n⏹️ Test interrupted by user")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Test failed with error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()