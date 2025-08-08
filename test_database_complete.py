#!/usr/bin/env python3
"""
Comprehensive test to understand database initialization and issues with RelationshipPersistenceManager.
This script will help identify why queries are returning 0 results.
"""

import json
import os
import sqlite3
from datetime import datetime

def find_database_files():
    """Find actual SQLite database files"""
    print("🔍 Searching for SQLite database files...")
    
    # Common Android database locations (when run in emulator/device)
    potential_paths = [
        "/data/data/com.example.chesspedagogue/databases/",
        "/storage/emulated/0/Android/data/com.example.chesspedagogue/databases/",
        "./",  # Current directory
        "../",  # Parent directory
    ]
    
    database_files = []
    for path in potential_paths:
        if os.path.exists(path):
            for file in os.listdir(path):
                if file.endswith('.db') or 'chess_games' in file:
                    full_path = os.path.join(path, file)
                    database_files.append(full_path)
    
    if database_files:
        print(f"📁 Found database files:")
        for db_file in database_files:
            print(f"   - {db_file}")
    else:
        print("❌ No database files found (expected - database is created in Android app data directory)")
    
    return database_files

def test_database_schema():
    """Test what the database schema should look like"""
    print("\n🗄️ Expected Database Schema Analysis:")
    print("=" * 60)
    
    # From GameDatabaseHelper.java, these tables should exist:
    expected_tables = {
        'saved_games': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'date INTEGER',
            'player_color TEXT',
            'moves TEXT',
            'final_fen TEXT',
            'description TEXT'
        ],
        'master_positions': [
            'master_id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master_name TEXT NOT NULL',
            'fen TEXT NOT NULL',
            'opponent TEXT',
            'year TEXT',
            'tournament TEXT',
            'opening TEXT',
            'result TEXT',
            'significance TEXT',
            'move_number INTEGER',
            'annotation TEXT',
            'tags TEXT'
        ],
        'master_relationships': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master1 TEXT NOT NULL',
            'master2 TEXT NOT NULL',
            'respect_level REAL DEFAULT 0.5',
            'rivalry_intensity REAL DEFAULT 0.0',
            'friendship_bond REAL DEFAULT 0.0',
            'communication_style TEXT DEFAULT \'formal\'',
            'total_interactions INTEGER DEFAULT 0',
            'total_games INTEGER DEFAULT 0',
            'last_major_event TEXT',
            'last_updated INTEGER',
            'created_at INTEGER',
            'UNIQUE(master1, master2)'
        ],
        'topic_memory': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master1 TEXT NOT NULL',
            'master2 TEXT NOT NULL',
            'topic TEXT NOT NULL',
            'discussion_count INTEGER DEFAULT 1',
            'total_emotional_intensity REAL DEFAULT 0.0',
            'average_emotional_intensity REAL DEFAULT 0.0',
            'last_emotion_master1 TEXT',
            'last_emotion_master2 TEXT',
            'topic_fatigue_level REAL DEFAULT 0.0',
            'evolution_path TEXT',
            'breakthrough_moments INTEGER DEFAULT 0',
            'conflict_incidents INTEGER DEFAULT 0',
            'last_discussed INTEGER',
            'created_at INTEGER'
        ],
        'emotional_reactions': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master TEXT NOT NULL',
            'opponent TEXT NOT NULL',
            'topic TEXT NOT NULL',
            'emotion TEXT NOT NULL',
            'intensity REAL NOT NULL',
            'momentum REAL DEFAULT 0.0',
            'game_context TEXT',
            'position_evaluation REAL',
            'conversation_snippet TEXT',
            'relationship_impact REAL DEFAULT 0.0',
            'timestamp INTEGER NOT NULL'
        ],
        'conversation_evolution': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master1 TEXT NOT NULL',
            'master2 TEXT NOT NULL',
            'original_topic TEXT NOT NULL',
            'evolved_topic TEXT NOT NULL',
            'evolution_type TEXT NOT NULL',
            'catalyst_emotion TEXT',
            'breakthrough_content TEXT',
            'impact_score REAL DEFAULT 0.0',
            'subsequent_usage INTEGER DEFAULT 0',
            'timestamp INTEGER NOT NULL'
        ],
        'emergent_events': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'event_type TEXT NOT NULL',
            'master1 TEXT NOT NULL',
            'master2 TEXT NOT NULL',
            'description TEXT NOT NULL',
            'emotional_context TEXT',
            'conversation_content TEXT',
            'impact_level REAL DEFAULT 0.0',
            'follow_up_effects TEXT',
            'timestamp INTEGER NOT NULL'
        ],
        'expression_patterns': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master_name TEXT NOT NULL',
            'concept TEXT NOT NULL',
            'used_phrases TEXT',
            'used_angles TEXT',
            'used_tones TEXT',
            'times_used INTEGER DEFAULT 1',
            'diversity_score REAL DEFAULT 1.0',
            'last_used INTEGER',
            'created_at INTEGER',
            'updated_at INTEGER',
            'UNIQUE(master_name, concept)'
        ],
        'emotional_strategy_learning': [
            'id INTEGER PRIMARY KEY AUTOINCREMENT',
            'master_name TEXT NOT NULL',
            'opponent_name TEXT NOT NULL',
            'approach_type TEXT NOT NULL',
            'success_rate REAL DEFAULT 0.5',
            'attempts INTEGER DEFAULT 0',
            'exploration_rate REAL DEFAULT 0.4',
            'strategy_data TEXT',
            'last_updated INTEGER',
            'created_at INTEGER',
            'UNIQUE(master_name, opponent_name, approach_type)'
        ]
    }
    
    print(f"📊 Expected {len(expected_tables)} tables:")
    for table_name, columns in expected_tables.items():
        print(f"\n📋 Table: {table_name}")
        for column in columns:
            print(f"   • {column}")
    
    return expected_tables

def analyze_json_file_content():
    """Analyze the content of JSON files to understand data structure"""
    print("\n📊 JSON File Content Analysis:")
    print("=" * 60)
    
    assets_path = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/app/src/main/assets"
    json_files = [f for f in os.listdir(assets_path) if f.endswith('.json')]
    
    for json_file in json_files[:3]:  # Test first 3 files
        file_path = os.path.join(assets_path, json_file)
        print(f"\n📁 Analyzing: {json_file}")
        
        try:
            with open(file_path, 'r') as f:
                data = json.load(f)
            
            print(f"   📊 Entries: {len(data)}")
            
            if data and len(data) > 0:
                first_entry = data[0]
                print(f"   📋 Fields in first entry:")
                for key, value in first_entry.items():
                    # Truncate long values
                    display_value = str(value)[:50] + "..." if len(str(value)) > 50 else str(value)
                    print(f"      • {key}: '{display_value}'")
                    
                # Check player_name standardization
                player_name = first_entry.get('player_name', '')
                print(f"   🎯 Player name standardization:")
                print(f"      • Original: '{player_name}'")
                print(f"      • Should become: '{standardize_master_name(player_name)}'")
            
        except Exception as e:
            print(f"   ❌ Error reading {json_file}: {e}")

def standardize_master_name(full_name):
    """Replicate the standardization logic from GameDatabaseHelper"""
    if not full_name:
        return ""
    
    normalized = full_name.lower().strip()
    
    if "mikhail tal" in normalized or normalized == "tal":
        return "tal"
    elif "bobby fischer" in normalized or normalized == "fischer":
        return "fischer"
    elif "magnus carlsen" in normalized or normalized == "carlsen":
        return "carlsen"
    elif "garry kasparov" in normalized or normalized == "kasparov":
        return "kasparov"
    elif "anatoly karpov" in normalized or normalized == "karpov":
        return "karpov"
    elif "vladimir kramnik" in normalized or normalized == "kramnik":
        return "kramnik"
    elif "alexander alekhine" in normalized or normalized == "alekhine":
        return "alekhine"
    elif "josé raúl capablanca" in normalized or normalized == "capablanca":
        return "capablanca"
    elif "emanuel lasker" in normalized or normalized == "lasker":
        return "lasker"
    elif "paul morphy" in normalized or normalized == "morphy":
        return "morphy"
    elif "viswanathan anand" in normalized or normalized == "anand":
        return "anand"
    elif "mikhail botvinnik" in normalized or normalized == "botvinnik":
        return "botvinnik"
    
    return full_name

def test_database_initialization_logic():
    """Test the database initialization flow"""
    print("\n🚀 Database Initialization Flow Analysis:")
    print("=" * 60)
    
    print("📋 MainActivity Database Initialization Process:")
    print("1. MainActivity.onCreate() executes database check on background thread")
    print("2. Creates GameDatabaseHelper instance")
    print("3. Calls dbHelper.hasMasterData('tal') to check if data exists")
    print("4. If no data, imports from assets using these masters:")
    
    # From MainActivity.java
    masters_list = ["tal", "fischer", "kasparov", "carlsen", "anand", "kramnik", "karpov", "alekhine", "capablanca"]
    for master in masters_list:
        filename = f"{master}_full_positions.json"
        assets_path = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/app/src/main/assets"
        file_exists = os.path.exists(os.path.join(assets_path, filename))
        status = "✅" if file_exists else "❌"
        print(f"   {status} {master} -> {filename} {'(EXISTS)' if file_exists else '(MISSING)'}")
    
    print("\n📋 RelationshipPersistenceManager Issues:")
    print("1. RelationshipPersistenceManager.getRelationship() queries 'master_relationships' table")
    print("2. Table should be created by GameDatabaseHelper.onCreate()")
    print("3. If queries return 0 results, possible causes:")
    print("   • Table not created (database version mismatch?)")
    print("   • Master names not normalized correctly")
    print("   • Database not properly initialized")
    print("   • SQLite database file corruption")
    
    print("\n🔍 Critical Questions to Answer:")
    print("1. Is GameDatabaseHelper.onCreate() being called?")
    print("2. Are all tables being created successfully?")
    print("3. Is the database file being created in the right location?")
    print("4. Are there any SQLite errors during table creation?")
    print("5. Is RelationshipPersistenceManager using the correct database instance?")

def analyze_relationship_persistence_queries():
    """Analyze the specific queries that are failing"""
    print("\n🔍 RelationshipPersistenceManager Query Analysis:")
    print("=" * 60)
    
    print("📋 Key Methods and Their Queries:")
    
    print("\n1. getRelationship(master1, master2):")
    print("   Query: SELECT * FROM master_relationships WHERE master1 = ? AND master2 = ?")
    print("   Purpose: Get existing relationship or create new one")
    print("   Returns 0 results if:")
    print("      • Table doesn't exist")
    print("      • No relationship record exists (expected for first run)")
    print("      • Master names don't match (normalization issue)")
    
    print("\n2. getFatiguedTopics(master1, master2):")
    print("   Query: SELECT topic FROM topic_memory WHERE master1 = ? AND master2 = ? AND topic_fatigue_level > ?")
    print("   Purpose: Get topics that are overused")
    print("   Returns 0 results if:")
    print("      • Table doesn't exist") 
    print("      • No topics have been recorded yet (expected for first run)")
    
    print("\n3. getEmotionalHistory(master, topic, limit):")
    print("   Query: SELECT * FROM emotional_reactions WHERE master = ? AND topic = ? ORDER BY timestamp DESC LIMIT ?")
    print("   Purpose: Get past emotional reactions")
    print("   Returns 0 results if:")
    print("      • Table doesn't exist")
    print("      • No emotional reactions recorded yet (expected for first run)")
    
    print("\n🎯 First-Run vs Ongoing Issues:")
    print("• EXPECTED on first run: All relationship/topic/emotion queries return 0 results")
    print("• PROBLEM if persistent: Tables not being created or data not being inserted")
    
    print("\n🔧 Debugging Steps:")
    print("1. Check if GameDatabaseHelper.onCreate() is called (look for table creation logs)")
    print("2. Check if RelationshipPersistenceManager.createRelationship() succeeds")
    print("3. Check if any SQLite constraint violations or insertion failures occur")
    print("4. Verify database file permissions and location")

def main():
    print("🔬 ChessPedagogue Database Complete Analysis")
    print("=" * 80)
    print(f"🕒 Analysis Time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # Run all analysis functions
    find_database_files()
    test_database_schema()
    analyze_json_file_content()
    test_database_initialization_logic() 
    analyze_relationship_persistence_queries()
    
    print("\n" + "=" * 80)
    print("🎯 SUMMARY - Why RelationshipPersistenceManager Returns 0 Results:")
    print("=" * 80)
    
    print("\n✅ NORMAL CAUSES (Expected on first run):")
    print("• No relationship data exists yet - relationships are created on-demand")
    print("• No topic memory exists yet - topics are recorded during conversations")
    print("• No emotional reactions exist yet - reactions are recorded during interactions")
    
    print("\n❌ PROBLEMATIC CAUSES (Need investigation):")
    print("• Database tables not created (GameDatabaseHelper.onCreate() issues)")
    print("• SQLite database file not accessible or corrupted")
    print("• Master name normalization mismatches")
    print("• Database connection/transaction issues")
    print("• App permissions or storage access problems")
    
    print("\n🔍 NEXT STEPS:")
    print("1. Build and run the app in Android Studio")
    print("2. Check logcat for these specific logs:")
    print("   • 'Database created with game storage, personality engine, AND emergent behavior tracking!'")
    print("   • 'Created relationship: X <-> Y'")
    print("   • 'Retrieved relationship: X <-> Y'")
    print("3. Use Android Studio Database Inspector to view actual database content")
    print("4. Test RelationshipPersistenceManager methods with known master pairs (e.g., 'tal', 'fischer')")
    
    print("\n📱 TO TEST IN ANDROID:")
    print("1. Launch app and go to Competitive Mode")
    print("2. Select two masters (e.g., Tal vs Fischer)")
    print("3. Let them have a conversation")
    print("4. Check logs for relationship creation/updates")
    print("5. Use 'adb logcat -s RelationshipPersistence:D GameDatabaseHelper:D' to monitor")

if __name__ == "__main__":
    main()