#!/usr/bin/env python3
"""
Test script to verify database loading logic and file naming conventions.
This simulates what happens when CompetitiveModeActivity initializes PersonalityEngine.
"""

import json
import os

def test_master_filename_mapping():
    """Test that master names map to correct filenames"""
    
    # This is the logic from PersonalityEngine.getMasterPositionFilename()
    def get_master_position_filename(master_name):
        mapping = {
            "alekhine": "alekhine_full_positions.json",
            "capablanca": "capablanca_full_positions.json", 
            "anand": "anand_full_positions.json",
            "kramnik": "kramnik_full_positions.json",
            "carlsen": "carlsen_full_positions.json",
            "fischer": "fischer_full_positions.json",
            "tal": "tal_full_positions.json",
            "kasparov": "kasparov_full_positions.json",
            "karpov": "karpov_full_positions.json",
            "lasker": "lasker_full_positions.json",
            "morphy": "morphy_full_positions.json",
            "botvinnik": "botvinnik_full_positions.json"
        }
        return mapping.get(master_name.lower(), f"{master_name.lower()}_positions.json")
    
    # Test masters from CompetitiveModeActivity
    test_masters = ["alekhine", "tal", "fischer", "carlsen", "kasparov", "kramnik", "karpov", "capablanca", "morphy", "lasker", "anand", "botvinnik"]
    
    # Check what files actually exist in assets
    assets_path = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/app/src/main/assets"
    actual_files = []
    if os.path.exists(assets_path):
        actual_files = [f for f in os.listdir(assets_path) if f.endswith('.json')]
    
    print("🔍 Testing master filename mapping...")
    print(f"📁 Assets directory: {assets_path}")
    print(f"📋 Actual JSON files found: {sorted(actual_files)}")
    print()
    
    all_good = True
    for master in test_masters:
        expected_filename = get_master_position_filename(master)
        file_exists = expected_filename in actual_files
        
        status = "✅" if file_exists else "❌"
        print(f"{status} {master:12} -> {expected_filename:30} {'EXISTS' if file_exists else 'MISSING'}")
        
        if not file_exists:
            all_good = False
    
    print()
    if all_good:
        print("✅ All master files are properly mapped and exist!")
    else:
        print("❌ Some master files are missing or incorrectly named!")
    
    return all_good

def test_standardized_master_names():
    """Test the name standardization logic from GameDatabaseHelper"""
    
    # This is the logic from GameDatabaseHelper.getStandardizedMasterName()
    def get_standardized_master_name(full_name):
        if not full_name:
            return ""
        
        normalized = full_name.lower().strip()
        
        if "mikhail tal" in normalized or "tal" in normalized:
            return "tal"
        elif "bobby fischer" in normalized or "fischer" in normalized:
            return "fischer"
        elif "magnus carlsen" in normalized or "carlsen" in normalized:
            return "carlsen"
        elif "garry kasparov" in normalized or "kasparov" in normalized:
            return "kasparov"
        elif "anatoly karpov" in normalized or "karpov" in normalized:
            return "karpov"
        elif "vladimir kramnik" in normalized or "kramnik" in normalized:
            return "kramnik"
        elif "alexander alekhine" in normalized or "alekhine" in normalized:
            return "alekhine"
        elif "josé raúl capablanca" in normalized or "capablanca" in normalized:
            return "capablanca"
        elif "emanuel lasker" in normalized or "lasker" in normalized:
            return "lasker"
        elif "paul morphy" in normalized or "morphy" in normalized:
            return "morphy"
        elif "viswanathan anand" in normalized or "anand" in normalized:
            return "anand"
        elif "mikhail botvinnik" in normalized or "botvinnik" in normalized:
            return "botvinnik"
        
        return full_name  # fallback
    
    # Test cases from the JSON files
    test_cases = [
        ("Mikhail Tal", "tal"),
        ("Bobby Fischer", "fischer"), 
        ("Magnus Carlsen", "carlsen"),
        ("Garry Kasparov", "kasparov"),
        ("Anatoly Karpov", "karpov"),
        ("Vladimir Kramnik", "kramnik"),
        ("Alexander Alekhine", "alekhine"),
        ("José Raúl Capablanca", "capablanca"),
        ("Emanuel Lasker", "lasker"),
        ("Paul Morphy", "morphy"),
        ("Viswanathan Anand", "anand"),
        ("Mikhail Botvinnik", "botvinnik")
    ]
    
    print("🔍 Testing master name standardization...")
    print()
    
    all_good = True
    for full_name, expected in test_cases:
        actual = get_standardized_master_name(full_name)
        correct = actual == expected
        
        status = "✅" if correct else "❌"
        print(f"{status} '{full_name:20}' -> '{actual:12}' {'✓' if correct else f'(expected: {expected})'}")
        
        if not correct:
            all_good = False
    
    print()
    if all_good:
        print("✅ All master name standardization works correctly!")
    else:
        print("❌ Some master name standardization failed!")
    
    return all_good

def test_json_file_format():
    """Test that we can read and parse the JSON files"""
    
    assets_path = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/app/src/main/assets"
    test_file = os.path.join(assets_path, "tal_full_positions.json")
    
    print("🔍 Testing JSON file format...")
    print(f"📁 Testing file: {test_file}")
    
    if not os.path.exists(test_file):
        print("❌ Test file does not exist!")
        return False
    
    try:
        with open(test_file, 'r') as f:
            data = json.load(f)
        
        print(f"📊 Successfully loaded JSON with {len(data)} entries")
        
        # Check format of first entry
        if data and len(data) > 0:
            first_entry = data[0]
            required_fields = ["player_name", "fen", "opponent", "year", "tournament"]
            missing_fields = [field for field in required_fields if field not in first_entry]
            
            if missing_fields:
                print(f"❌ Missing required fields: {missing_fields}")
                return False
            
            print(f"✅ First entry has all required fields")
            print(f"   player_name: '{first_entry.get('player_name', '')}'")
            print(f"   opponent: '{first_entry.get('opponent', '')}'")
            print(f"   year: '{first_entry.get('year', '')}'")
            
            return True
        else:
            print("❌ JSON file is empty")
            return False
            
    except Exception as e:
        print(f"❌ Error reading JSON file: {e}")
        return False

def main():
    print("🚀 Testing ChessPedagogue Database Loading Logic")
    print("=" * 60)
    print()
    
    test1 = test_master_filename_mapping()
    print()
    test2 = test_standardized_master_names()
    print()
    test3 = test_json_file_format()
    print()
    
    print("=" * 60)
    if test1 and test2 and test3:
        print("🎉 ALL TESTS PASSED! Database loading should work correctly.")
    else:
        print("❌ SOME TESTS FAILED! Check the issues above.")
    print()
    
    print("📋 EXPECTED BEHAVIOR:")
    print("1. When CompetitiveModeActivity starts, it calls personalityEngine.initializeMasterData(selectedMaster)")
    print("2. PersonalityEngine checks if master data exists in database")
    print("3. If not, it calls getMasterPositionFilename() to get correct filename")
    print("4. It imports the JSON file using GameDatabaseHelper.importMasterPositionsFromAssets()")
    print("5. During import, player names like 'Mikhail Tal' get standardized to 'tal'")
    print("6. Future lookups use the standardized name 'tal' to find data")
    print()
    print("🔍 To see if this works, check the Android logs for:")
    print("   - PersonalityEngine: '🔍 Initializing master data for: <master>'")
    print("   - GameDatabaseHelper: '📁 Attempting to import from assets file: <filename>'")
    print("   - GameDatabaseHelper: '📊 Found X positions to import'")
    print("   - GameDatabaseHelper: '✅ Successfully imported X/Y master positions!'")

if __name__ == "__main__":
    main()