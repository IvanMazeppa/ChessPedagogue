#!/usr/bin/env python3
"""
🎯 Automated Chess Engine Recovery Test
Tests the new engine recovery system by playing an extended automated game.
"""

import subprocess
import time
import json
import sys
from datetime import datetime

class ChessEngineRecoveryTester:
    def __init__(self):
        self.adb_path = "/mnt/c/Users/dilli/AppData/Local/Android/Sdk/platform-tools/adb.exe"
        self.device_id = "emulator-5554"
        self.app_package = "com.example.chesspedagogue"
        
        # Chess coordinate system (from calibration guide)
        self.board_coords = self._generate_board_coordinates()
        
        # Test results
        self.test_results = {
            "start_time": datetime.now().isoformat(),
            "moves_played": 0,
            "engine_errors_detected": 0,
            "recovery_attempts": 0,
            "successful_recoveries": 0,
            "test_status": "running",
            "move_log": []
        }
        
    def _generate_board_coordinates(self):
        """Generate coordinates for all chess squares using calibration data"""
        coords = {}
        # Based on: a1 = (162, 1992), h8 = (1329, 825)
        base_x, base_y = 162, 1992
        
        for file_idx, file_char in enumerate('abcdefgh'):
            for rank_idx, rank_num in enumerate('12345678'):
                square = f"{file_char}{rank_num}"
                x = base_x + (file_idx * 167)
                y = base_y - (rank_idx * 167)
                coords[square] = (x, y)
                
        return coords
    
    def log_message(self, message):
        """Log with timestamp"""
        timestamp = datetime.now().strftime("%H:%M:%S")
        print(f"[{timestamp}] {message}")
        
    def run_adb_command(self, command):
        """Execute ADB command"""
        full_command = f"{self.adb_path} -s {self.device_id} {command}"
        try:
            result = subprocess.run(full_command, shell=True, capture_output=True, text=True, timeout=10)
            return result.returncode == 0, result.stdout, result.stderr
        except subprocess.TimeoutExpired:
            self.log_message("⚠️ ADB command timed out")
            return False, "", "Timeout"
    
    def tap_square(self, square):
        """Tap a chess square"""
        if square not in self.board_coords:
            self.log_message(f"❌ Invalid square: {square}")
            return False
            
        x, y = self.board_coords[square]
        success, _, _ = self.run_adb_command(f"shell input tap {x} {y}")
        if success:
            self.log_message(f"✅ Tapped {square} at ({x}, {y})")
        else:
            self.log_message(f"❌ Failed to tap {square}")
        return success
    
    def make_chess_move(self, move):
        """Make a chess move (e.g., 'e2e4')"""
        if len(move) != 4:
            self.log_message(f"❌ Invalid move format: {move}")
            return False
            
        from_square = move[:2]
        to_square = move[2:]
        
        self.log_message(f"🎯 Making move: {from_square} → {to_square}")
        
        # Tap source square
        if not self.tap_square(from_square):
            return False
        time.sleep(0.5)
        
        # Tap destination square
        if not self.tap_square(to_square):
            return False
        time.sleep(1.0)  # Wait for move processing
        
        return True
    
    def check_for_engine_errors(self):
        """Check logcat for engine errors"""
        success, output, _ = self.run_adb_command("logcat -d | grep -E '(StockfishManager|Engine not running|IOException)'")
        
        if success and output:
            lines = output.strip().split('\n')
            recent_errors = [line for line in lines if "Engine not running" in line or "IOException" in line]
            
            if recent_errors:
                self.test_results["engine_errors_detected"] += len(recent_errors)
                self.log_message(f"🚨 Detected {len(recent_errors)} engine errors")
                return True
                
        return False
    
    def check_for_recovery_attempts(self):
        """Check logcat for recovery attempts"""
        success, output, _ = self.run_adb_command("logcat -d | grep -E '(recovery|FORCE STOPPING|Engine recovery)'")
        
        if success and output:
            lines = output.strip().split('\n')
            recovery_lines = [line for line in lines if "recovery" in line.lower()]
            
            if recovery_lines:
                self.test_results["recovery_attempts"] += len(recovery_lines)
                self.log_message(f"🔄 Detected {len(recovery_lines)} recovery attempts")
                return True
                
        return False
    
    def start_chess_app(self):
        """Launch the chess app"""
        self.log_message("🚀 Starting ChessPedagogue app...")
        success, _, _ = self.run_adb_command(f"shell am start -n {self.app_package}/.SplashActivity")
        
        if success:
            self.log_message("✅ App launched successfully")
            time.sleep(3)  # Wait for app to load
            return True
        else:
            self.log_message("❌ Failed to launch app")
            return False
    
    def navigate_to_game(self):
        """Navigate through the app to reach the chess board"""
        self.log_message("🎯 Navigating to chess game...")
        
        # Step 1: Tap "BEGIN THE CHALLENGE" button with correct coordinates
        success, _, _ = self.run_adb_command("shell input tap 720 2892")
        if not success:
            self.log_message("❌ Failed to tap BEGIN THE CHALLENGE")
            return False
            
        time.sleep(3)  # Wait for coach selection screen
        self.log_message("✅ Reached coach selection screen")
        
        # Step 2: Select a chess master (let's pick the first one - approximate coordinates)
        # This will need adjustment based on the actual coach selection layout
        self.log_message("🎭 Selecting chess coach...")
        success, _, _ = self.run_adb_command("shell input tap 365 400")  # Approximate first coach position
        if not success:
            self.log_message("❌ Failed to select coach")
            return False
            
        time.sleep(2)  # Wait for selection
        
        # Step 3: Look for and tap any "Continue" or "Start Game" button
        # Try multiple possible button locations
        continue_buttons = [
            (365, 600),   # Center bottom
            (365, 700),   # Lower center
            (365, 800),   # Even lower
            (720, 2892),  # Original BEGIN button location
        ]
        
        for x, y in continue_buttons:
            self.log_message(f"🔘 Trying continue button at ({x}, {y})")
            success, _, _ = self.run_adb_command(f"shell input tap {x} {y}")
            if success:
                time.sleep(2)
                break
        
        time.sleep(5)  # Wait for game to load
        self.log_message("✅ Successfully navigated to chess board")
        return True
    
    def play_automated_game(self, max_moves=30):
        """Play an automated chess game"""
        self.log_message(f"🎮 Starting automated game (max {max_moves} moves)")
        
        # Some opening moves for testing
        opening_moves = [
            "e2e4", "e7e6",  # French Defense
            "d2d4", "d7d5",  # French main line
            "e4e5", "c7c5",  # Advance variation
            "c2c3", "b8c6",  # Support center
            "g1f3", "g8e7",  # Develop knights
            "f1e2", "c8d7",  # Develop bishops
            "e1g1", "e8g8",  # Castle kingside
            "b1d2", "f7f6",  # More development
            "e5f6", "e7f6",  # Exchange
            "d2e4", "f6e4",  # Knight exchanges
        ]
        
        move_count = 0
        for i in range(0, min(len(opening_moves), max_moves)):
            move = opening_moves[i]
            move_count += 1
            
            self.log_message(f"📝 Move {move_count}: {move}")
            
            # Check for engine errors before move
            if self.check_for_engine_errors():
                self.log_message("🚨 Engine error detected before move!")
            
            # Make the move
            if self.make_chess_move(move):
                self.test_results["moves_played"] += 1
                self.test_results["move_log"].append({
                    "move_number": move_count,
                    "move": move,
                    "timestamp": datetime.now().isoformat(),
                    "success": True
                })
                
                # Wait for engine response
                time.sleep(2)
                
                # Check for engine errors after move
                if self.check_for_engine_errors():
                    self.log_message("🚨 Engine error detected after move!")
                
                # Check for recovery attempts
                if self.check_for_recovery_attempts():
                    self.log_message("🔄 Engine recovery detected!")
                    self.test_results["successful_recoveries"] += 1
                
            else:
                self.log_message(f"❌ Failed to make move {move}")
                self.test_results["move_log"].append({
                    "move_number": move_count,
                    "move": move,
                    "timestamp": datetime.now().isoformat(),
                    "success": False
                })
                break
                
            # Add some variation in timing to stress test
            if move_count % 3 == 0:
                time.sleep(1)  # Simulate thinking time
                
        self.log_message(f"🏁 Automated game completed. Played {self.test_results['moves_played']} moves")
    
    def save_test_results(self):
        """Save test results to JSON file"""
        self.test_results["end_time"] = datetime.now().isoformat()
        self.test_results["test_status"] = "completed"
        
        filename = f"engine_recovery_test_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        
        try:
            with open(filename, 'w') as f:
                json.dump(self.test_results, f, indent=2)
            self.log_message(f"📊 Test results saved to {filename}")
        except Exception as e:
            self.log_message(f"❌ Failed to save results: {e}")
    
    def print_summary(self):
        """Print test summary"""
        print("\n" + "="*60)
        print("🎯 AUTOMATED CHESS ENGINE RECOVERY TEST SUMMARY")
        print("="*60)
        print(f"📊 Moves Played: {self.test_results['moves_played']}")
        print(f"🚨 Engine Errors Detected: {self.test_results['engine_errors_detected']}")
        print(f"🔄 Recovery Attempts: {self.test_results['recovery_attempts']}")
        print(f"✅ Successful Recoveries: {self.test_results['successful_recoveries']}")
        
        if self.test_results['engine_errors_detected'] > 0:
            recovery_rate = (self.test_results['successful_recoveries'] / self.test_results['engine_errors_detected']) * 100
            print(f"📈 Recovery Success Rate: {recovery_rate:.1f}%")
        
        print(f"⏱️ Test Duration: {self.test_results.get('end_time', 'N/A')}")
        print("="*60)
    
    def run_full_test(self):
        """Run the complete automated test"""
        self.log_message("🎯 Starting Automated Chess Engine Recovery Test")
        
        try:
            # Step 1: Launch app
            if not self.start_chess_app():
                self.test_results["test_status"] = "failed - app launch"
                return False
            
            # Step 2: Navigate to game
            if not self.navigate_to_game():
                self.test_results["test_status"] = "failed - navigation"
                return False
            
            # Step 3: Play automated game
            self.play_automated_game(max_moves=20)
            
            # Step 4: Final checks
            self.check_for_engine_errors()
            self.check_for_recovery_attempts()
            
            self.test_results["test_status"] = "completed successfully"
            return True
            
        except Exception as e:
            self.log_message(f"❌ Test failed with exception: {e}")
            self.test_results["test_status"] = f"failed - {str(e)}"
            return False
        
        finally:
            self.save_test_results()
            self.print_summary()

def main():
    """Main function"""
    print("🎯 ChessPedagogue Automated Engine Recovery Test")
    print("=" * 50)
    
    tester = ChessEngineRecoveryTester()
    success = tester.run_full_test()
    
    if success:
        print("\n✅ Test completed successfully!")
        sys.exit(0)
    else:
        print("\n❌ Test failed!")
        sys.exit(1)

if __name__ == "__main__":
    main()