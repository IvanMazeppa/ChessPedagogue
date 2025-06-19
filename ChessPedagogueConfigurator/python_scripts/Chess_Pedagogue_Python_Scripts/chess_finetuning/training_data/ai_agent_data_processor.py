#!/usr/bin/env python3
"""
AI Agent Training Data Processor
===============================

This script implements the AI agent approach to automatically clean and convert
the Capablanca training data to the proper OpenAI fine-tuning format.

Based on the evaluation recommendations:
1. Convert to strict prompt/completion JSONL format
2. Add proper separators
3. Deduplicate entries
4. Balance thematic coverage
5. Validate structure

Author: AI Enhancement Team
Date: June 2025
"""

import json
import hashlib
import os
from typing import Dict, List, Tuple, Any
from collections import defaultdict, Counter

def load_training_data(filename: str) -> List[Dict]:
    """Load the current training data."""
    data = []
    print(f"📁 Loading training data from: {filename}")
    
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if line:
                    try:
                        entry = json.loads(line)
                        data.append(entry)
                    except json.JSONDecodeError as e:
                        print(f"⚠️  JSON error on line {line_num}: {e}")
        
        print(f"✅ Loaded {len(data)} entries")
        return data
        
    except FileNotFoundError:
        print(f"❌ File not found: {filename}")
        return []

def extract_conversation_pair(entry: Dict) -> Tuple[str, str]:
    """Extract user prompt and assistant completion from an entry."""
    user_text = ""
    assistant_text = ""
    
    messages = entry.get('messages', [])
    
    # Find the last user message and assistant response
    for msg in messages:
        role = msg.get('role', '')
        content = msg.get('content', '')
        
        if role == 'user':
            user_text = content
        elif role == 'assistant' and user_text:  # Only if we have a user message
            assistant_text = content
            break
    
    return user_text.strip(), assistant_text.strip()

def create_openai_format_entry(user_text: str, assistant_text: str) -> Dict[str, str]:
    """Convert to OpenAI fine-tuning format with proper separator."""
    # Add the separator token to the prompt
    prompt = f"{user_text}<|endofprompt|>"
    
    # Add leading space to completion for better tokenization
    completion = f" {assistant_text}"
    
    return {
        "prompt": prompt,
        "completion": completion
    }

def calculate_entry_hash(entry: Dict[str, str]) -> str:
    """Calculate hash for deduplication."""
    content = f"{entry['prompt']}||{entry['completion']}"
    return hashlib.md5(content.encode('utf-8')).hexdigest()

def classify_topic(user_text: str, assistant_text: str) -> str:
    """Classify the topic of an entry based on content."""
    combined_text = (user_text + " " + assistant_text).lower()
    
    # Topic classification keywords
    if any(word in combined_text for word in ['opening', 'gambit', 'variation', 'development', 'castling']):
        return 'openings'
    elif any(word in combined_text for word in ['endgame', 'pawn ending', 'king and pawn', 'opposition', 'lucena', 'philidor']):
        return 'endgame'
    elif any(word in combined_text for word in ['pin', 'fork', 'skewer', 'tactic', 'combination', 'sacrifice']):
        return 'tactics'
    elif any(word in combined_text for word in ['middlegame', 'strategy', 'plan', 'attack', 'defense']):
        return 'middlegame'
    elif any(word in combined_text for word in ['childhood', 'cuba', 'havana', 'wife', 'life', 'career', 'remember']):
        return 'biography'
    elif any(word in combined_text for word in ['fen', 'position', 'game', 'move', 'analysis']):
        return 'game-analysis'
    elif any(word in combined_text for word in ['psychology', 'pressure', 'confidence', 'mental', 'emotion']):
        return 'psychology'
    else:
        return 'general'

def process_training_data(input_file: str) -> List[Dict]:
    """Process the training data using AI agent approach."""
    print("🤖 AI AGENT DATA PROCESSOR STARTING")
    print("=" * 50)
    
    # Load original data
    original_data = load_training_data(input_file)
    if not original_data:
        return []
    
    # Process each entry
    processed_entries = []
    seen_hashes = set()
    topic_counts = defaultdict(int)
    
    print(f"\n🔄 Processing {len(original_data)} entries...")
    
    for i, entry in enumerate(original_data):
        try:
            # Extract conversation pair
            user_text, assistant_text = extract_conversation_pair(entry)
            
            if not user_text or not assistant_text:
                print(f"⚠️  Skipping entry {i+1}: Missing user text or assistant text")
                continue
            
            # Convert to OpenAI format
            openai_entry = create_openai_format_entry(user_text, assistant_text)
            
            # Check for duplicates
            entry_hash = calculate_entry_hash(openai_entry)
            if entry_hash in seen_hashes:
                print(f"🔄 Skipping duplicate entry {i+1}")
                continue
            
            seen_hashes.add(entry_hash)
            
            # Classify topic for metadata tracking
            topic = classify_topic(user_text, assistant_text)
            topic_counts[topic] += 1
            
            # Add to processed entries
            processed_entries.append(openai_entry)
            
            if (i + 1) % 50 == 0:
                print(f"✅ Processed {i+1}/{len(original_data)} entries")
                
        except Exception as e:
            print(f"❌ Error processing entry {i+1}: {e}")
            continue
    
    print(f"\n📊 Processing Complete!")
    print(f"   Original entries: {len(original_data)}")
    print(f"   Processed entries: {len(processed_entries)}")
    print(f"   Duplicates removed: {len(original_data) - len(processed_entries)}")
    print(f"   Deduplication rate: {((len(original_data) - len(processed_entries)) / len(original_data) * 100):.1f}%")
    
    # Show topic distribution
    print(f"\n📚 Topic Distribution:")
    for topic, count in sorted(topic_counts.items(), key=lambda x: x[1], reverse=True):
        percentage = count / len(processed_entries) * 100
        print(f"   {topic}: {count} entries ({percentage:.1f}%)")
    
    return processed_entries

def validate_openai_format(entries: List[Dict]) -> Tuple[bool, List[str]]:
    """Validate entries against OpenAI fine-tuning requirements."""
    errors = []
    
    for i, entry in enumerate(entries):
        # Check required fields
        if 'prompt' not in entry:
            errors.append(f"Entry {i+1}: Missing 'prompt' field")
        if 'completion' not in entry:
            errors.append(f"Entry {i+1}: Missing 'completion' field")
        
        # Check for extra fields
        allowed_fields = {'prompt', 'completion'}
        extra_fields = set(entry.keys()) - allowed_fields
        if extra_fields:
            errors.append(f"Entry {i+1}: Extra fields not allowed: {extra_fields}")
        
        # Check separator
        if 'prompt' in entry and '<|endofprompt|>' not in entry['prompt']:
            errors.append(f"Entry {i+1}: Missing separator token in prompt")
        
        # Check completion format
        if 'completion' in entry and not entry['completion'].startswith(' '):
            errors.append(f"Entry {i+1}: Completion should start with space")
    
    return len(errors) == 0, errors

def analyze_quality_metrics(entries: List[Dict]) -> Dict[str, Any]:
    """Analyze quality metrics of the processed dataset."""
    if not entries:
        return {}
    
    prompt_lengths = [len(entry['prompt'].split()) for entry in entries]
    completion_lengths = [len(entry['completion'].split()) for entry in entries]
    
    metrics = {
        'total_entries': len(entries),
        'avg_prompt_length': sum(prompt_lengths) / len(prompt_lengths),
        'avg_completion_length': sum(completion_lengths) / len(completion_lengths),
        'min_prompt_length': min(prompt_lengths),
        'max_prompt_length': max(prompt_lengths),
        'min_completion_length': min(completion_lengths),
        'max_completion_length': max(completion_lengths),
        'total_tokens_estimate': sum(prompt_lengths) + sum(completion_lengths)
    }
    
    return metrics

def save_processed_data(entries: List[Dict], output_file: str):
    """Save the processed data in OpenAI format."""
    print(f"\n💾 Saving processed data to: {output_file}")
    
    with open(output_file, 'w', encoding='utf-8') as f:
        for entry in entries:
            f.write(json.dumps(entry, ensure_ascii=False) + '\n')
    
    print(f"✅ Saved {len(entries)} entries")

def create_metadata_index(original_data: List[Dict], processed_entries: List[Dict], output_file: str):
    """Create an external metadata index for tracking."""
    print(f"\n📋 Creating metadata index: {output_file}")
    
    metadata_index = []
    
    for i, (original, processed) in enumerate(zip(original_data, processed_entries)):
        # Extract metadata from original entry
        meta = original.get('meta', {})
        
        # Create metadata entry
        metadata_entry = {
            'entry_id': i + 1,
            'prompt_hash': hashlib.md5(processed['prompt'].encode('utf-8')).hexdigest()[:8],
            'topic': meta.get('topic', 'unknown'),
            'difficulty': meta.get('difficulty', 'unknown'),
            'source': meta.get('source', 'unknown'),
            'word_count': len(processed['completion'].split()),
            'character_count': len(processed['completion'])
        }
        
        metadata_index.append(metadata_entry)
    
    # Save as JSON for easy analysis
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(metadata_index, f, indent=2)
    
    print(f"✅ Created metadata index with {len(metadata_index)} entries")

def main():
    """Main function to run the AI agent data processor."""
    print("🤖 AI AGENT TRAINING DATA PROCESSOR")
    print("🎯 Converting to OpenAI Fine-Tuning Format")
    print("=" * 50)
    
    # File paths
    input_file = 'capablanca_training_data_ultimate_championship.jsonl'
    output_file = 'capablanca_openai_format.jsonl'
    metadata_file = 'capablanca_metadata_index.json'
    
    try:
        # Process the training data
        processed_entries = process_training_data(input_file)
        
        if not processed_entries:
            print("❌ No entries were processed successfully. Exiting.")
            return
        
        # Validate OpenAI format
        print(f"\n🔍 Validating OpenAI format...")
        is_valid, errors = validate_openai_format(processed_entries)
        
        if not is_valid:
            print(f"❌ Validation errors found:")
            for error in errors[:10]:  # Show first 10 errors
                print(f"   {error}")
            if len(errors) > 10:
                print(f"   ... and {len(errors) - 10} more errors")
            return
        
        print(f"✅ All entries passed OpenAI format validation!")
        
        # Analyze quality metrics
        print(f"\n📊 Quality Analysis:")
        metrics = analyze_quality_metrics(processed_entries)
        for key, value in metrics.items():
            if isinstance(value, float):
                print(f"   {key}: {value:.1f}")
            else:
                print(f"   {key}: {value}")
        
        # Save processed data
        save_processed_data(processed_entries, output_file)
        
        # Create metadata index (if we have original data)
        try:
            original_data = load_training_data(input_file)
            if len(original_data) >= len(processed_entries):
                create_metadata_index(original_data[:len(processed_entries)], processed_entries, metadata_file)
        except Exception as e:
            print(f"⚠️  Could not create metadata index: {e}")
        
        print(f"\n🎉 SUCCESS! Data processing complete!")
        print(f"📋 Next steps:")
        print(f"   1. Validate with: openai tools fine_tunes.prepare_data -f {output_file}")
        print(f"   2. Upload to OpenAI: openai files create -f {output_file} -p fine-tune")
        print(f"   3. Create fine-tuning job with GPT-4o")
        print(f"   4. Deploy the ultimate Capablanca!")
        
        # Final readiness assessment
        readiness_score = min(100, (len(processed_entries) / 250) * 80 + 20)  # Scale to 250 target entries
        print(f"\n🏆 CHAMPIONSHIP READINESS: {readiness_score:.1f}%")
        
        if readiness_score >= 90:
            print(f"🥇 READY TO CRUSH FISCHER!")
        elif readiness_score >= 80:
            print(f"🥈 Very strong championship contender!")
        else:
            print(f"🥉 Good foundation, consider adding more entries")
        
    except Exception as e:
        print(f"❌ Critical error: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()