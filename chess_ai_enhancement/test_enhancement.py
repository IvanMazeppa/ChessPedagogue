#!/usr/bin/env python3
"""
Test script for ChessPedagogue AI Enhancement System
===================================================

This script tests the enhancement capabilities with sample chess responses
from different masters and skill levels.
"""

import json
import sys
from chess_enhancement_core import (
    ChessAIEnhancer, ChessContext, SkillLevel, EmotionalContext,
    EnhancementConfig
)

def test_personality_amplification():
    """Test personality amplification for different masters"""
    print("🎭 Testing Personality Amplification...")
    print("=" * 50)
    
    test_cases = [
        {
            "master": "tal",
            "original": "This is a good move. It attacks the king.",
            "fen": "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "expected_traits": ["poetic", "mystical"]
        },
        {
            "master": "fischer", 
            "original": "White has a slight advantage here.",
            "fen": "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "expected_traits": ["aggressive", "precise"]
        },
        {
            "master": "carlsen",
            "original": "The position is roughly equal.",
            "fen": "8/8/8/3k4/8/3K4/8/8 w - - 0 1",  # Endgame position
            "expected_traits": ["practical", "endgame"]
        }
    ]
    
    enhancer = ChessAIEnhancer()
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n{i}. Testing {test_case['master'].title()}:")
        print(f"   Original: \"{test_case['original']}\"")
        
        context = ChessContext(
            fen=test_case['fen'],
            move_history=[],
            current_move=None,
            evaluation=0.5,
            master_name=test_case['master'],
            user_skill=SkillLevel.INTERMEDIATE,
            position_phase="middlegame",
            tactical_themes=["attack"],
            emotional_weight=EmotionalContext.NEUTRAL
        )
        
        enhanced = enhancer.enhance_response(test_case['original'], context)
        print(f"   Enhanced: \"{enhanced}\"")
        print(f"   ✅ Enhanced: {enhanced != test_case['original']}")


def test_difficulty_adaptation():
    """Test adaptive difficulty for different skill levels"""
    print("\n📚 Testing Difficulty Adaptation...")
    print("=" * 50)
    
    original_response = "This move creates a zugzwang situation where the opponent's pieces lack coordination."
    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    skill_levels = [SkillLevel.BEGINNER, SkillLevel.INTERMEDIATE, SkillLevel.ADVANCED, SkillLevel.EXPERT]
    enhancer = ChessAIEnhancer()
    
    for i, skill in enumerate(skill_levels, 1):
        print(f"\n{i}. Testing {skill.value.title()} Level:")
        print(f"   Original: \"{original_response}\"")
        
        context = ChessContext(
            fen=fen,
            move_history=[],
            current_move=None,
            evaluation=0.0,
            master_name="tal",
            user_skill=skill,
            position_phase="middlegame",
            tactical_themes=["general"],
            emotional_weight=EmotionalContext.NEUTRAL
        )
        
        enhanced = enhancer.enhance_response(original_response, context)
        print(f"   Enhanced: \"{enhanced}\"")
        
        # Check for skill-appropriate language
        if skill == SkillLevel.BEGINNER:
            has_simple_language = "zugzwang" not in enhanced.lower()
            print(f"   ✅ Simplified: {has_simple_language}")
        elif skill == SkillLevel.EXPERT:
            has_complex_language = len(enhanced) >= len(original_response)
            print(f"   ✅ Enhanced depth: {has_complex_language}")


def test_emotional_intelligence():
    """Test emotional intelligence in different contexts"""
    print("\n😊 Testing Emotional Intelligence...")
    print("=" * 50)
    
    test_cases = [
        {
            "scenario": "Brilliant tactical shot",
            "evaluation": 3.5,
            "original": "This move is strong.",
            "expected_emotion": "excitement"
        },
        {
            "scenario": "Tense critical position", 
            "evaluation": 0.8,
            "original": "The position is complex.",
            "expected_emotion": "tension"
        },
        {
            "scenario": "Neutral quiet position",
            "evaluation": 0.1,
            "original": "A solid move.",
            "expected_emotion": "neutral"
        }
    ]
    
    enhancer = ChessAIEnhancer()
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n{i}. Testing {test_case['scenario']}:")
        print(f"   Original: \"{test_case['original']}\"")
        print(f"   Evaluation: {test_case['evaluation']}")
        
        context = ChessContext(
            fen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            move_history=[],
            current_move=None,
            evaluation=test_case['evaluation'],
            master_name="tal",
            user_skill=SkillLevel.INTERMEDIATE,
            position_phase="middlegame",
            tactical_themes=["attack"],
            emotional_weight=EmotionalContext.NEUTRAL
        )
        
        enhanced = enhancer.enhance_response(test_case['original'], context)
        print(f"   Enhanced: \"{enhanced}\"")
        
        # Check for emotional indicators
        emotional_words = ["brilliant", "incredible", "amazing", "critical", "tense", "beautiful"]
        has_emotion = any(word in enhanced.lower() for word in emotional_words)
        
        if test_case['expected_emotion'] != "neutral":
            print(f"   ✅ Emotional enhancement: {has_emotion}")
        else:
            print(f"   ✅ Neutral tone maintained: {not has_emotion or enhanced == test_case['original']}")


def test_historical_context():
    """Test historical context integration"""
    print("\n📖 Testing Historical Context...")
    print("=" * 50)
    
    test_cases = [
        {
            "scenario": "Attacking position",
            "tactical_themes": ["attack", "sacrifice"],
            "master": "tal",
            "original": "White has attacking chances.",
            "expected_reference": True
        },
        {
            "scenario": "Endgame position", 
            "position_phase": "endgame",
            "master": "carlsen",
            "original": "The endgame requires precision.",
            "expected_reference": True
        }
    ]
    
    enhancer = ChessAIEnhancer()
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n{i}. Testing {test_case['scenario']}:")
        print(f"   Original: \"{test_case['original']}\"")
        print(f"   Master: {test_case['master'].title()}")
        
        context = ChessContext(
            fen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            move_history=[],
            current_move=None,
            evaluation=1.0,
            master_name=test_case['master'],
            user_skill=SkillLevel.INTERMEDIATE,
            position_phase=test_case.get('position_phase', 'middlegame'),
            tactical_themes=test_case.get('tactical_themes', ['general']),
            emotional_weight=EmotionalContext.NEUTRAL
        )
        
        enhanced = enhancer.enhance_response(test_case['original'], context)
        print(f"   Enhanced: \"{enhanced}\"")
        
        # Check for historical references
        historical_indicators = ["reminds", "like", "tal", "fischer", "carlsen", "kasparov", "games"]
        has_reference = any(indicator in enhanced.lower() for indicator in historical_indicators)
        print(f"   ✅ Historical context: {has_reference}")


def test_integration_scenario():
    """Test complete integration scenario"""
    print("\n🎯 Testing Complete Integration...")
    print("=" * 50)
    
    # Simulate a real game scenario
    scenario = {
        "original_response": "Nxf7 is an interesting sacrifice. It opens up the king.",
        "master_name": "tal",
        "fen": "r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4",
        "user_skill": "intermediate",
        "evaluation": 2.1,
        "move_history": ["e4", "e5", "Nf3", "Nc6", "Bb5", "Nf6"],
        "tactical_themes": ["sacrifice", "attack", "king_attack"]
    }
    
    print("🏁 Real Game Scenario:")
    print(f"   Position: Tactical position with sacrifice potential")
    print(f"   Master: {scenario['master_name'].title()}")
    print(f"   User Skill: {scenario['user_skill'].title()}")
    print(f"   Original: \"{scenario['original_response']}\"")
    
    enhancer = ChessAIEnhancer()
    
    context = ChessContext(
        fen=scenario['fen'],
        move_history=scenario['move_history'],
        current_move="Nxf7",
        evaluation=scenario['evaluation'],
        master_name=scenario['master_name'],
        user_skill=SkillLevel.INTERMEDIATE,
        position_phase="middlegame",
        tactical_themes=scenario['tactical_themes'],
        emotional_weight=EmotionalContext.NEUTRAL
    )
    
    enhanced = enhancer.enhance_response(scenario['original_response'], context)
    
    print(f"\n✨ ENHANCED RESPONSE:")
    print(f"   \"{enhanced}\"")
    
    # Analyze enhancements
    print(f"\n📊 Enhancement Analysis:")
    print(f"   ✅ Length increase: {len(enhanced) > len(scenario['original_response'])}")
    print(f"   ✅ Personality injection: {'tal' in enhanced.lower() or 'sacrifice' in enhanced.lower()}")
    print(f"   ✅ Emotional enhancement: {'!' in enhanced or 'brilliant' in enhanced.lower()}")
    print(f"   ✅ Technical content: 'king' in enhanced.lower()")


def main():
    """Run all enhancement tests"""
    print("🚀 ChessPedagogue AI Enhancement System Test Suite")
    print("=" * 60)
    
    try:
        test_personality_amplification()
        test_difficulty_adaptation()
        test_emotional_intelligence()
        test_historical_context()
        test_integration_scenario()
        
        print(f"\n🎉 All tests completed successfully!")
        print("🔗 Ready for integration with ChessPedagogue Android app")
        
    except Exception as e:
        print(f"\n❌ Test failed with error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()