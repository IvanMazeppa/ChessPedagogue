#!/usr/bin/env python3
"""
Fix Capablanca training data by converting mixed formats to consistent OpenAI Chat format.
Addresses issues found by OpenAI's fine-tuning preparation tool.
"""

import json
import sys
from pathlib import Path

def fix_training_data(input_file, output_file):
    """Convert mixed JSONL formats to consistent OpenAI Chat format."""
    
    fixed_lines = []
    issues_fixed = 0
    
    print(f"🔧 Processing {input_file}...")
    
    with open(input_file, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line:
                continue
                
            try:
                data = json.loads(line)
                
                # Check if this is already in correct format
                if 'messages' in data:
                    # Already correct format - just clean up
                    fixed_lines.append(data)
                    
                elif 'prompt' in data and 'completion' in data:
                    # Legacy format - convert to messages format
                    prompt = data['prompt']
                    completion = data['completion'].strip()
                    
                    # Remove trailing \n from completions
                    if completion.endswith('\n'):
                        completion = completion[:-1]
                    
                    # Create new format
                    new_data = {
                        "messages": [
                            {"role": "user", "content": prompt},
                            {"role": "assistant", "content": completion}
                        ]
                    }
                    
                    # Add meta if it exists
                    if 'meta' in data:
                        new_data['meta'] = data['meta']
                    else:
                        # Add default meta for converted entries
                        new_data['meta'] = {
                            "topic": "game-analysis",
                            "difficulty": "advanced", 
                            "source": "converted"
                        }
                    
                    fixed_lines.append(new_data)
                    issues_fixed += 1
                    print(f"  ✅ Fixed line {line_num}: prompt/completion → messages")
                    
                else:
                    print(f"  ❌ Line {line_num}: Unknown format, skipping")
                    continue
                    
            except json.JSONDecodeError as e:
                print(f"  ❌ Line {line_num}: JSON error - {e}")
                continue
    
    # Write fixed data
    print(f"\n📝 Writing fixed data to {output_file}...")
    with open(output_file, 'w', encoding='utf-8') as f:
        for data in fixed_lines:
            f.write(json.dumps(data, ensure_ascii=False) + '\n')
    
    print(f"\n✅ Conversion complete!")
    print(f"   📊 Total entries: {len(fixed_lines)}")
    print(f"   🔧 Issues fixed: {issues_fixed}")
    print(f"   📁 Output: {output_file}")
    
    return len(fixed_lines), issues_fixed

def validate_training_data(file_path):
    """Validate the training data against OpenAI requirements."""
    
    print(f"\n🔍 Validating {file_path}...")
    
    issues = []
    total_lines = 0
    total_chars = 0
    
    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line:
                continue
                
            total_lines += 1
            total_chars += len(line)
            
            try:
                data = json.loads(line)
                
                # Check required fields
                if 'messages' not in data:
                    issues.append(f"Line {line_num}: Missing 'messages' field")
                    continue
                
                messages = data['messages']
                if not isinstance(messages, list):
                    issues.append(f"Line {line_num}: 'messages' must be a list")
                    continue
                
                if len(messages) < 2:
                    issues.append(f"Line {line_num}: Need at least 2 messages")
                    continue
                
                # Validate each message
                for i, msg in enumerate(messages):
                    if 'role' not in msg:
                        issues.append(f"Line {line_num}, msg {i}: Missing 'role'")
                    if 'content' not in msg:
                        issues.append(f"Line {line_num}, msg {i}: Missing 'content'")
                    
                    role = msg.get('role')
                    if role not in ['system', 'user', 'assistant']:
                        issues.append(f"Line {line_num}, msg {i}: Invalid role '{role}'")
                    
                    content = msg.get('content', '')
                    if len(content.strip()) == 0:
                        issues.append(f"Line {line_num}, msg {i}: Empty content")
                    
                    # Check for excessively long content
                    if len(content) > 8000:
                        issues.append(f"Line {line_num}, msg {i}: Content too long ({len(content)} chars)")
                
                # Check conversation structure
                if messages[0]['role'] != 'user':
                    issues.append(f"Line {line_num}: First message should be 'user'")
                
                if messages[-1]['role'] != 'assistant':
                    issues.append(f"Line {line_num}: Last message should be 'assistant'")
                
            except json.JSONDecodeError as e:
                issues.append(f"Line {line_num}: JSON parse error - {e}")
    
    # Print validation results
    print(f"📊 Validation Results:")
    print(f"   📝 Total entries: {total_lines}")
    print(f"   📏 Average size: {total_chars // total_lines if total_lines > 0 else 0} chars/entry")
    print(f"   ❌ Issues found: {len(issues)}")
    
    if issues:
        print(f"\n🔍 First 10 issues:")
        for issue in issues[:10]:
            print(f"   - {issue}")
        if len(issues) > 10:
            print(f"   ... and {len(issues) - 10} more")
    else:
        print(f"   ✅ No issues found - ready for fine-tuning!")
    
    return len(issues) == 0

def add_system_message_variants(file_path, output_file):
    """Add system message variants to make Capablanca more distinctive."""
    
    system_messages = [
        "You are José Raúl Capablanca, the Cuban chess master known for your crystal-clear positional understanding and endgame precision. Respond with your characteristic elegance and practical wisdom.",
        "You are Capablanca, the 'Chess Machine' - respond with your legendary clarity, focusing on simple, principled moves and deep endgame understanding.",
        "You are José Raúl Capablanca, World Chess Champion 1921-1927. Your responses should reflect your preference for natural, logical play and your reputation for making the game look effortless.",
        "You are Capablanca, master of the endgame and positional play. Respond with your characteristic precision and natural understanding of chess principles."
    ]
    
    print(f"🎭 Adding system message variants...")
    
    processed_lines = []
    
    with open(file_path, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line:
                continue
            
            data = json.loads(line)
            
            # Add system message to some entries (every 4th one)
            if line_num % 4 == 1:
                system_msg = system_messages[(line_num // 4) % len(system_messages)]
                
                # Insert system message at the beginning
                data['messages'].insert(0, {
                    "role": "system",
                    "content": system_msg
                })
            
            processed_lines.append(data)
    
    # Write enhanced data
    with open(output_file, 'w', encoding='utf-8') as f:
        for data in processed_lines:
            f.write(json.dumps(data, ensure_ascii=False) + '\n')
    
    print(f"✅ Enhanced {len(processed_lines)} entries with system messages")
    return len(processed_lines)

def main():
    # File paths
    base_dir = Path(__file__).parent
    input_file = base_dir / "chess_finetuning/training_data/capablanca_training_data_revised.jsonl"
    fixed_file = base_dir / "chess_finetuning/training_data/capablanca_training_data_fixed.jsonl"
    final_file = base_dir / "chess_finetuning/training_data/capablanca_training_data_final.jsonl"
    
    print("🏆 Capablanca Training Data Fixer")
    print("=" * 50)
    
    # Step 1: Fix format issues
    total_entries, issues_fixed = fix_training_data(input_file, fixed_file)
    
    # Step 2: Validate the fixed data
    is_valid = validate_training_data(fixed_file)
    
    if is_valid:
        # Step 3: Add system message variants
        enhanced_entries = add_system_message_variants(fixed_file, final_file)
        
        # Step 4: Final validation
        print(f"\n🔍 Final validation...")
        final_valid = validate_training_data(final_file)
        
        if final_valid:
            print(f"\n🎉 SUCCESS! Your Capablanca training data is ready!")
            print(f"📁 Final file: {final_file}")
            print(f"📊 Total entries: {enhanced_entries}")
            print(f"\n🚀 Next steps:")
            print(f"   1. Upload to OpenAI: openai files create -f {final_file} -p fine-tune")
            print(f"   2. Create fine-tune job with GPT-4o-mini")
            print(f"   3. Test Capablanca vs Alekhine matchup!")
        else:
            print(f"\n❌ Final validation failed - check the issues above")
    else:
        print(f"\n❌ Validation failed - fix the issues above first")

if __name__ == "__main__":
    main()