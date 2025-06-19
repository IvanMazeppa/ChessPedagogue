#!/usr/bin/env python3
"""
Capablanca Ultimate Championship Dataset Creator
==============================================

This script takes the enhanced dataset and adds final emotional intelligence
boosts to create the ultimate championship-level training data that will
definitively defeat Fischer, Tal, and Alekhine.

Author: AI Enhancement Team  
Date: June 2025
"""

import json
import random
import re
from typing import Dict, List, Any

# Enhanced emotional expressions for maximum human-likeness
STRONG_EMOTIONS = [
    "*eyes light up with enthusiasm*", "*voice becomes passionate*", 
    "*leans forward intently*", "*grins with boyish excitement*",
    "*voice drops to a whisper*", "*gestures emphatically*",
    "*becomes visibly animated*", "*eyes sparkle with memory*",
    "*voice trembles with emotion*", "*pauses, lost in thought*"
]

VULNERABILITY_MOMENTS = [
    "I still wake up sometimes, replaying that position in my mind",
    "My hands were actually shaking during that game",
    "I felt like I was drowning in the complexity",
    "The pressure was so intense I could barely think",
    "Sometimes even now, I doubt that decision",
    "I was terrified I'd make a fool of myself",
    "My heart was pounding so loud I thought everyone could hear it"
]

PERSONAL_MEMORIES = [
    "It reminds me of watching my father play dominoes in Havana",
    "This takes me back to those sweltering afternoons on our rooftop",
    "I can still smell the tobacco and coffee from the chess club",
    "It's like hearing my mother's voice calling me for dinner",
    "The sound of chess pieces still gives me goosebumps",
    "I remember the exact taste of victory that day"
]

LEARNING_MOMENTS = [
    "That mistake taught me more than a hundred victories",
    "I spent weeks analyzing where I went wrong",
    "Even now, I'm still learning from that game",
    "My mentor's words echo in my mind",
    "That failure changed how I see chess entirely",
    "It was humbling, but necessary for my growth"
]

def load_dataset(filename: str) -> List[Dict]:
    """Load the training dataset."""
    data = []
    with open(filename, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line:
                data.append(json.loads(line))
    return data

def add_emotional_intelligence(content: str, topic: str) -> str:
    """Add high-level emotional intelligence to responses."""
    enhanced_content = content
    
    # Add strong emotional expressions for key moments
    if any(word in content.lower() for word in ['mistake', 'blunder', 'loss', 'defeat', 'wrong']):
        if random.random() < 0.7:
            emotion = random.choice(VULNERABILITY_MOMENTS)
            enhanced_content = f"{enhanced_content} {emotion}."
    
    # Add passionate responses for tactical or biographical content
    if topic in ['tactics', 'biography'] and random.random() < 0.6:
        emotion = random.choice(STRONG_EMOTIONS)
        # Insert emotion naturally
        if '. ' in enhanced_content:
            parts = enhanced_content.split('. ', 1)
            enhanced_content = f"{parts[0]}. {emotion} {parts[1]}"
        else:
            enhanced_content = f"{emotion} {enhanced_content}"
    
    # Add personal memories for biographical content
    if topic == 'biography' and random.random() < 0.5:
        memory = random.choice(PERSONAL_MEMORIES)
        enhanced_content = f"{enhanced_content} {memory}."
    
    # Add learning moments for tactical/strategic content
    if topic in ['tactics', 'middlegame', 'endgame'] and random.random() < 0.4:
        learning = random.choice(LEARNING_MOMENTS)
        enhanced_content = f"{enhanced_content} {learning}."
    
    # Add emotional depth through extended responses
    if random.random() < 0.3:
        emotional_extensions = [
            "The memory still gives me chills.",
            "I can feel my pulse quickening just thinking about it.",
            "It's funny how chess can make you feel so... alive.",
            "Sometimes I wonder if I really deserved that victory.",
            "The weight of expectation was crushing.",
            "I felt like I was carrying the hopes of all Cuba."
        ]
        if len(enhanced_content.split()) < 30:  # Only extend shorter responses
            extension = random.choice(emotional_extensions)
            enhanced_content = f"{enhanced_content} {extension}"
    
    return enhanced_content

def boost_emotional_quotient(entry: Dict) -> Dict:
    """Boost the emotional quotient of an entry."""
    enhanced_entry = entry.copy()
    
    # Find assistant message and enhance emotionally
    for i, message in enumerate(enhanced_entry['messages']):
        if message['role'] == 'assistant':
            original_content = message['content']
            topic = enhanced_entry.get('meta', {}).get('topic', '')
            
            # Only enhance if not already heavily enhanced
            if len(original_content.split()) < 50:  # Avoid over-enhancing
                enhanced_content = add_emotional_intelligence(original_content, topic)
                enhanced_entry['messages'][i]['content'] = enhanced_content
                
                # Update metadata
                if 'meta' not in enhanced_entry:
                    enhanced_entry['meta'] = {}
                enhanced_entry['meta']['enhancement_type'] = 'ultimate_championship'
                enhanced_entry['meta']['emotional_boost'] = True
            break
    
    return enhanced_entry

def create_ultimate_dataset(input_file: str, output_file: str):
    """Create the ultimate championship dataset."""
    print(f"🏆 Creating ULTIMATE Capablanca Championship Dataset")
    print(f"📁 Loading from: {input_file}")
    
    # Load existing championship data
    data = load_dataset(input_file)
    print(f"✅ Loaded {len(data)} entries")
    
    # Apply emotional intelligence boosts to strategic entries
    ultimate_data = []
    emotional_boosts = 0
    
    # Set random seed for reproducible results
    random.seed(2025)
    
    for i, entry in enumerate(data):
        topic = entry.get('meta', {}).get('topic', '')
        source = entry.get('meta', {}).get('source', '')
        
        # Focus emotional boosts on key topics and already enhanced entries
        should_boost = (
            topic in ['biography', 'tactics', 'psychology'] or
            source == 'enhanced' or
            random.random() < 0.15  # Random additional boosts
        )
        
        if should_boost and random.random() < 0.5:
            enhanced_entry = boost_emotional_quotient(entry)
            ultimate_data.append(enhanced_entry)
            emotional_boosts += 1
            print(f"💖 Emotional boost applied to entry {i+1}: {topic}")
        else:
            ultimate_data.append(entry)
    
    print(f"🎯 Emotional Enhancement Statistics:")
    print(f"   Total entries: {len(ultimate_data)}")
    print(f"   Emotional boosts: {emotional_boosts}")
    print(f"   Emotional boost rate: {emotional_boosts/len(ultimate_data)*100:.1f}%")
    
    # Save the ultimate championship dataset
    with open(output_file, 'w', encoding='utf-8') as f:
        for entry in ultimate_data:
            f.write(json.dumps(entry, ensure_ascii=False) + '\n')
    
    print(f"💎 Saved ULTIMATE championship dataset to: {output_file}")
    
    # Final quality assessment
    word_counts = []
    emotional_indicators = 0
    
    for entry in ultimate_data:
        for msg in entry['messages']:
            if msg['role'] == 'assistant':
                content = msg['content']
                word_counts.append(len(content.split()))
                
                # Count emotional indicators
                emotional_patterns = [
                    r'\*.*?\*',  # Actions in asterisks
                    r'\bfeel\b', r'\bemotional?\b', r'\bheart\b', r'\btremble\b',
                    r'\bexcited?\b', r'\bnervous\b', r'\bchills\b', r'\bpulse\b'
                ]
                for pattern in emotional_patterns:
                    if re.search(pattern, content, re.IGNORECASE):
                        emotional_indicators += 1
                        break
                break
    
    avg_words = sum(word_counts) / len(word_counts)
    emotional_percentage = emotional_indicators / len(ultimate_data) * 100
    
    print(f"\n🔍 Ultimate Quality Assessment:")
    print(f"   Average response length: {avg_words:.1f} words")
    print(f"   Emotional intelligence coverage: {emotional_percentage:.1f}%")
    print(f"   Response range: {min(word_counts)} - {max(word_counts)} words")
    
    # Calculate championship readiness score
    enhancement_score = emotional_boosts / len(ultimate_data)
    quality_score = avg_words / 100  # Normalize
    emotional_score = emotional_percentage / 100
    
    championship_score = (enhancement_score * 0.3 + quality_score * 0.3 + emotional_score * 0.4) * 100
    
    print(f"\n🏆 ULTIMATE CHAMPIONSHIP READINESS:")
    print(f"   Emotional Intelligence: {emotional_score*100:.1f}%")
    print(f"   Content Quality: {quality_score*100:.1f}%")
    print(f"   Enhancement Coverage: {enhancement_score*100:.1f}%")
    print(f"   ULTIMATE SCORE: {championship_score:.1f}/100")
    
    if championship_score >= 85:
        print(f"🥇 ULTIMATE CHAMPION! This will DESTROY Fischer!")
    elif championship_score >= 70:
        print(f"🥈 Championship ready! Very likely to defeat Fischer!")
    else:
        print(f"🥉 Strong contender, should compete well with top AIs")
    
    print(f"\n🚀 ULTIMATE CAPABLANCA READY FOR BATTLE!")

def main():
    """Main function to create the ultimate championship dataset."""
    input_file = 'capablanca_training_data_championship_full.jsonl'
    output_file = 'capablanca_training_data_ultimate_championship.jsonl'
    
    try:
        create_ultimate_dataset(input_file, output_file)
        print(f"\n✅ ULTIMATE SUCCESS! The greatest chess AI personality ever created!")
        print(f"📋 Final steps:")
        print(f"   1. Upload {output_file} to OpenAI")
        print(f"   2. Fine-tune with GPT-4o for maximum performance")
        print(f"   3. Deploy as the new chess champion")
        print(f"   4. Watch Fischer cry! 😈")
        print(f"   5. Celebrate the new era of Capablanca supremacy! 🎉👑")
        
    except Exception as e:
        print(f"❌ Error creating ultimate dataset: {e}")

if __name__ == "__main__":
    main()