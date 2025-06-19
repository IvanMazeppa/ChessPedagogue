#!/usr/bin/env python3
"""
Capablanca Full Championship Dataset Creator
==========================================

This script takes the original 308-entry dataset and systematically enhances
targeted entries with human-like qualities while preserving all original content.

Author: AI Enhancement Team
Date: June 2025
"""

import json
import random
import re
from typing import Dict, List, Any

# Human-like enhancement patterns
UNCERTAINTY_PHRASES = [
    "Well, ", "Let me think... ", "I believe ", "I think ", "Perhaps ", 
    "It seems to me ", "If I recall correctly, ", "Actually, ", "Hmm, "
]

EMOTIONAL_ACTIONS = [
    "*adjusts my cigar*", "*pauses thoughtfully*", "*takes a slow puff*", 
    "*leans back in chair*", "*rubs temples*", "*sighs deeply*", 
    "*chuckles softly*", "*looks distant*", "*winces slightly*",
    "*grins with enthusiasm*", "*touches chest absently*"
]

CONVERSATIONAL_FILLERS = [
    "you know", "well", "anyway", "so", "but honestly", "actually",
    "to be frank", "between you and me", "the truth is"
]

VULNERABILITY_PHRASES = [
    "Sometimes I get it wrong", "I've made this mistake before",
    "When I'm tired, I sometimes", "Even champions make errors",
    "I learned this the hard way", "My memory isn't perfect"
]

CUBAN_REFERENCES = [
    "reminds me of Havana", "those hot Cuban afternoons", "back in Cuba",
    "the warmth of Caribbean chess", "my Cuban mentor taught me"
]

def load_original_dataset(filename: str) -> List[Dict]:
    """Load the original training dataset."""
    data = []
    with open(filename, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line:
                data.append(json.loads(line))
    return data

def should_enhance_entry(entry: Dict, enhancement_rate: float = 0.25) -> bool:
    """Determine if an entry should be enhanced based on criteria."""
    meta = entry.get('meta', {})
    topic = meta.get('topic', '')
    difficulty = meta.get('difficulty', '')
    
    # Higher enhancement rate for biographical and tactical content
    if topic in ['biography', 'tactics']:
        return random.random() < 0.4
    elif topic in ['endgame', 'middlegame']:
        return random.random() < 0.3
    elif topic in ['openings', 'game-analysis']:
        return random.random() < 0.2
    else:
        return random.random() < enhancement_rate

def add_uncertainty_marker(text: str) -> str:
    """Add uncertainty markers to make responses more human."""
    if random.random() < 0.7:
        return random.choice(UNCERTAINTY_PHRASES) + text.lower()
    return text

def add_emotional_action(text: str) -> str:
    """Add emotional actions to responses."""
    if random.random() < 0.6:
        action = random.choice(EMOTIONAL_ACTIONS)
        # Insert at beginning or after first sentence
        if '.' in text:
            parts = text.split('.', 1)
            return f"{parts[0]}. {action} {parts[1]}"
        else:
            return f"{action} {text}"
    return text

def add_conversational_elements(text: str) -> str:
    """Add conversational fillers and natural speech patterns."""
    if random.random() < 0.5:
        filler = random.choice(CONVERSATIONAL_FILLERS)
        # Insert filler naturally
        if ', ' in text:
            text = text.replace(', ', f', {filler}, ', 1)
        elif '. ' in text:
            text = text.replace('. ', f'. {filler.capitalize()}, ', 1)
        else:
            text = f"{text}, {filler}."
    return text

def add_vulnerability(text: str) -> str:
    """Add moments of vulnerability and human imperfection."""
    if random.random() < 0.3:
        vulnerability = random.choice(VULNERABILITY_PHRASES)
        return f"{text} {vulnerability}."
    return text

def add_cuban_heritage(text: str) -> str:
    """Add references to Cuban heritage where appropriate."""
    if 'remember' in text.lower() or 'childhood' in text.lower() or random.random() < 0.1:
        if random.random() < 0.4:
            reference = random.choice(CUBAN_REFERENCES)
            return f"{text} This {reference}."
    return text

def add_self_correction(text: str) -> str:
    """Add self-corrections and natural speech hesitations."""
    if random.random() < 0.25:
        # Find a good place to insert self-correction
        words = text.split()
        if len(words) > 5:
            pos = random.randint(3, min(8, len(words)-2))
            correction_phrases = ["Actually, wait...", "No, that's not quite right.", "Let me rephrase that."]
            correction = random.choice(correction_phrases)
            words.insert(pos, correction)
            return ' '.join(words)
    return text

def enhance_response_content(content: str, topic: str) -> str:
    """Apply comprehensive enhancements to response content."""
    original_content = content
    
    # Apply enhancements with decreasing probability to avoid over-enhancement
    if random.random() < 0.8:
        content = add_uncertainty_marker(content)
    
    if random.random() < 0.6:
        content = add_emotional_action(content)
    
    if random.random() < 0.5:
        content = add_conversational_elements(content)
    
    if random.random() < 0.3:
        content = add_vulnerability(content)
    
    if topic == 'biography' and random.random() < 0.4:
        content = add_cuban_heritage(content)
    
    if random.random() < 0.2:
        content = add_self_correction(content)
    
    # Ensure the enhanced content is still reasonable length
    if len(content.split()) > len(original_content.split()) * 2.5:
        return original_content
    
    return content

def enhance_entry(entry: Dict) -> Dict:
    """Enhance a single training entry with human-like qualities."""
    enhanced_entry = entry.copy()
    
    # Find the assistant message and enhance it
    for i, message in enumerate(enhanced_entry['messages']):
        if message['role'] == 'assistant':
            original_content = message['content']
            topic = enhanced_entry.get('meta', {}).get('topic', '')
            
            enhanced_content = enhance_response_content(original_content, topic)
            enhanced_entry['messages'][i]['content'] = enhanced_content
            
            # Update metadata to indicate enhancement
            if 'meta' not in enhanced_entry:
                enhanced_entry['meta'] = {}
            enhanced_entry['meta']['source'] = 'enhanced'
            enhanced_entry['meta']['enhancement_type'] = 'human_like_v1'
            break
    
    return enhanced_entry

def create_championship_dataset(original_file: str, output_file: str, target_enhancement_rate: float = 0.25):
    """Create the full championship dataset with enhancements."""
    print(f"🏆 Creating Capablanca Championship Dataset")
    print(f"📁 Loading from: {original_file}")
    
    # Load original data
    original_data = load_original_dataset(original_file)
    print(f"✅ Loaded {len(original_data)} original entries")
    
    # Process each entry
    championship_data = []
    enhanced_count = 0
    
    # Set random seed for reproducible results
    random.seed(42)
    
    for i, entry in enumerate(original_data):
        if should_enhance_entry(entry, target_enhancement_rate):
            enhanced_entry = enhance_entry(entry)
            championship_data.append(enhanced_entry)
            enhanced_count += 1
            print(f"🎭 Enhanced entry {i+1}: {entry.get('meta', {}).get('topic', 'unknown')} topic")
        else:
            championship_data.append(entry)
    
    print(f"🎯 Enhancement Statistics:")
    print(f"   Total entries: {len(championship_data)}")
    print(f"   Enhanced entries: {enhanced_count}")
    print(f"   Enhancement rate: {enhanced_count/len(championship_data)*100:.1f}%")
    
    # Save the championship dataset
    with open(output_file, 'w', encoding='utf-8') as f:
        for entry in championship_data:
            f.write(json.dumps(entry, ensure_ascii=False) + '\n')
    
    print(f"💾 Saved championship dataset to: {output_file}")
    
    # Quality validation
    print(f"\n🔍 Quality Validation:")
    word_counts = []
    for entry in championship_data:
        for msg in entry['messages']:
            if msg['role'] == 'assistant':
                word_counts.append(len(msg['content'].split()))
                break
    
    avg_words = sum(word_counts) / len(word_counts)
    print(f"   Average response length: {avg_words:.1f} words")
    print(f"   Response range: {min(word_counts)} - {max(word_counts)} words")
    
    # Topic distribution
    topics = {}
    for entry in championship_data:
        topic = entry.get('meta', {}).get('topic', 'unknown')
        topics[topic] = topics.get(topic, 0) + 1
    
    print(f"\n📚 Topic Distribution:")
    for topic, count in sorted(topics.items(), key=lambda x: x[1], reverse=True):
        print(f"   {topic}: {count} entries ({count/len(championship_data)*100:.1f}%)")
    
    print(f"\n🏆 CHAMPIONSHIP DATASET COMPLETE!")
    print(f"🚀 Ready for OpenAI fine-tuning to defeat Fischer!")

def main():
    """Main function to create the championship dataset."""
    original_file = 'capablanca_training_data_final.jsonl'
    output_file = 'capablanca_training_data_championship_full.jsonl'
    
    try:
        create_championship_dataset(original_file, output_file, target_enhancement_rate=0.25)
        print(f"\n✅ SUCCESS! Championship dataset created.")
        print(f"📋 Next steps:")
        print(f"   1. Run validation: python3 validate_championship_data.py")
        print(f"   2. Upload to OpenAI for fine-tuning")
        print(f"   3. Create fine-tuning job with GPT-4o")
        print(f"   4. Monitor training progress")
        print(f"   5. Deploy and defeat Fischer! 🎉")
        
    except Exception as e:
        print(f"❌ Error creating championship dataset: {e}")

if __name__ == "__main__":
    main()