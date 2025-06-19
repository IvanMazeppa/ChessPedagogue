#!/usr/bin/env python3
"""
Database Export Utility for ChessPedagogue
Creates a readable export of database contents for inspection
"""

import subprocess
import json
import sys
from datetime import datetime

def run_adb_command(command):
    """Execute ADB command and return output"""
    try:
        result = subprocess.run(
            f"adb shell {command}", 
            shell=True, 
            capture_output=True, 
            text=True
        )
        if result.returncode == 0:
            return result.stdout.strip()
        else:
            print(f"ADB Error: {result.stderr}")
            return None
    except Exception as e:
        print(f"Command failed: {e}")
        return None

def export_database_contents():
    """Export database contents to readable format"""
    
    print("🔍 ChessPedagogue Database Export Utility")
    print("=" * 50)
    
    # Check if device is connected
    devices = run_adb_command("devices")
    if not devices or "device" not in devices:
        print("❌ No Android device/emulator connected!")
        print("Please start your emulator or connect your device first.")
        return
    
    print("✅ Device connected")
    
    # Create exports directory
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    export_dir = f"database_export_{timestamp}"
    
    try:
        subprocess.run(f"mkdir -p {export_dir}", shell=True)
        print(f"📁 Created export directory: {export_dir}")
        
        # Export database file
        print("📥 Copying database from device...")
        copy_cmd = f"adb exec-out run-as com.example.chesspedagogue cat databases/chess_games.db > {export_dir}/chess_games.db"
        result = subprocess.run(copy_cmd, shell=True)
        
        if result.returncode == 0:
            print("✅ Database copied successfully")
            
            # Try to inspect the database
            print("🔍 Inspecting database contents...")
            
            # List tables
            tables_cmd = f"sqlite3 {export_dir}/chess_games.db '.tables'"
            tables_result = subprocess.run(tables_cmd, shell=True, capture_output=True, text=True)
            
            if tables_result.returncode == 0:
                print(f"📊 Tables found: {tables_result.stdout}")
                
                # Export table schemas
                schema_cmd = f"sqlite3 {export_dir}/chess_games.db '.schema' > {export_dir}/database_schema.sql"
                subprocess.run(schema_cmd, shell=True)
                print(f"📝 Schema exported to {export_dir}/database_schema.sql")
                
                # Export sample data from key tables
                tables = ["master_relationships", "emotional_reactions", "topic_memory", "master_positions"]
                
                for table in tables:
                    print(f"📋 Exporting sample data from {table}...")
                    sample_cmd = f"sqlite3 {export_dir}/chess_games.db 'SELECT * FROM {table} LIMIT 10;' > {export_dir}/{table}_sample.txt"
                    subprocess.run(sample_cmd, shell=True, capture_output=True)
                
                # Get database statistics
                stats_cmd = f"sqlite3 {export_dir}/chess_games.db \"SELECT name, COUNT(*) as count FROM (SELECT 'master_relationships' as name UNION SELECT 'emotional_reactions' UNION SELECT 'topic_memory' UNION SELECT 'master_positions') LEFT JOIN master_relationships ON name = 'master_relationships' LEFT JOIN emotional_reactions ON name = 'emotional_reactions' LEFT JOIN topic_memory ON name = 'topic_memory' LEFT JOIN master_positions ON name = 'master_positions' GROUP BY name;\""
                
                print("📊 Database export completed!")
                print(f"📁 Check the '{export_dir}' directory for all exported files")
                print()
                print("Files created:")
                print(f"  - {export_dir}/chess_games.db (database file)")
                print(f"  - {export_dir}/database_schema.sql (table schemas)")
                for table in tables:
                    print(f"  - {export_dir}/{table}_sample.txt (sample data)")
                
            else:
                print("❌ Database file appears to be corrupted or invalid")
                print("This might be a file format issue or the database is locked")
                
        else:
            print("❌ Failed to copy database from device")
            print("Make sure your app is not running or try 'adb root' if on debug device")
            
    except Exception as e:
        print(f"❌ Export failed: {e}")

if __name__ == "__main__":
    export_database_contents()