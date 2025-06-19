#!/usr/bin/env python3
"""
OpenAI Fine-tuning Data Cleaner and Validator
===========================================

This script cleans and validates Capablanca training data to ensure 100% 
compatibility with OpenAI's fine-tuning requirements.

Key fixes applied:
1. Remove problematic Unicode characters
2. Ensure proper JSON structure
3. Validate against OpenAI's exact requirements
4. Generate clean, compatible training data
"""

import json
import re
import unicodedata
from typing import Dict, List, Any

def clean_unicode_text(text: str) -> str:
    """Clean text of problematic Unicode characters."""
    # Normalize Unicode to NFD (decomposed form)
    text = unicodedata.normalize('NFD', text)
    
    # Remove combining characters (accents, etc.)
    text = ''.join(c for c in text if unicodedata.category(c) != 'Mn')
    
    # Replace common problematic characters
    replacements = {
        'José': 'Jose',
        'Raúl': 'Raul',
        'Capablanca': 'Capablanca',
        '—': '-',
        '"': '"',
        '"': '"',
        ''': "'",
        ''': "'",
        '…': '...',
        '–': '-'
    }
    
    for old, new in replacements.items():
        text = text.replace(old, new)
    
    # Remove any remaining non-ASCII characters
    text = ''.join(c for c in text if ord(c) < 128)
    
    return text

def validate_openai_format(entry: Dict) -> List[str]:
    """Validate entry against OpenAI's exact requirements."""
    errors = []
    
    # Must have 'messages' key
    if 'messages' not in entry:
        errors.append("Missing required 'messages' key")
        return errors
    
    messages = entry['messages']
    
    # Messages must be a list
    if not isinstance(messages, list):
        errors.append("'messages' must be a list")
        return errors
    
    # Must have at least 1 message
    if len(messages) == 0:
        errors.append("'messages' cannot be empty")
        return errors
    
    # Validate each message
    for i, msg in enumerate(messages):
        if not isinstance(msg, dict):
            errors.append(f"Message {i} must be a dictionary")
            continue
        
        # Must have 'role' and 'content'
        if 'role' not in msg:
            errors.append(f"Message {i} missing 'role'")
        if 'content' not in msg:
            errors.append(f"Message {i} missing 'content'")
        
        # Role must be valid
        valid_roles = ['system', 'user', 'assistant']
        role = msg.get('role', '')
        if role not in valid_roles:
            errors.append(f"Message {i} has invalid role '{role}'. Must be one of: {valid_roles}")
        
        # Content must be string
        content = msg.get('content', '')
        if not isinstance(content, str):
            errors.append(f"Message {i} content must be a string")
        
        # Content cannot be empty
        if not content.strip():
            errors.append(f"Message {i} content cannot be empty")
    
    return errors

def clean_entry(entry: Dict) -> Dict:
    """Clean a single training entry."""
    if 'messages' not in entry:
        return entry
    
    cleaned_entry = {
        'messages': []
    }
    
    for msg in entry['messages']:
        if isinstance(msg, dict) and 'role' in msg and 'content' in msg:
            cleaned_msg = {
                'role': msg['role'],
                'content': clean_unicode_text(msg['content'])
            }
            cleaned_entry['messages'].append(cleaned_msg)
    
    return cleaned_entry

def load_and_clean_jsonl(input_file: str) -> List[Dict]:
    """Load and clean JSONL training data."""
    cleaned_data = []
    errors_found = 0
    
    print(f"🔄 Loading {input_file}...")
    
    try:
        with open(input_file, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if not line:
                    continue
                
                try:
                    entry = json.loads(line)
                    cleaned_entry = clean_entry(entry)
                    
                    # Validate cleaned entry
                    validation_errors = validate_openai_format(cleaned_entry)
                    if validation_errors:
                        print(f"❌ Line {line_num} validation errors: {', '.join(validation_errors)}")
                        errors_found += 1
                    else:
                        cleaned_data.append(cleaned_entry)
                        
                except json.JSONDecodeError as e:
                    print(f"❌ JSON decode error on line {line_num}: {e}")
                    errors_found += 1
                    
    except FileNotFoundError:
        print(f"❌ File not found: {input_file}")
        return []
    
    print(f"✅ Loaded {len(cleaned_data)} valid entries")
    if errors_found > 0:
        print(f"⚠️ Found {errors_found} entries with errors")
    
    return cleaned_data

def save_clean_jsonl(data: List[Dict], output_file: str) -> bool:
    """Save cleaned data to JSONL format."""
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            for entry in data:
                json_line = json.dumps(entry, ensure_ascii=True, separators=(',', ':'))
                f.write(json_line + '\n')
        
        print(f"✅ Saved {len(data)} entries to {output_file}")
        return True
        
    except Exception as e:
        print(f"❌ Error saving file: {e}")
        return False

def generate_sample_validation(data: List[Dict], num_samples: int = 5) -> None:
    """Generate validation samples to check format."""
    print(f"\n🔍 SAMPLE VALIDATION (First {num_samples} entries):")
    print("=" * 60)
    
    for i, entry in enumerate(data[:num_samples]):
        print(f"\n📋 Entry {i+1}:")
        print(json.dumps(entry, indent=2, ensure_ascii=True))
        
        validation_errors = validate_openai_format(entry)
        if validation_errors:
            print(f"❌ Validation errors: {', '.join(validation_errors)}")
        else:
            print("✅ Valid format")

def main():
    """Main cleaning and validation function."""
    print("🧹 CAPABLANCA TRAINING DATA CLEANER")
    print("=" * 50)
    
    input_files = [
        'capablanca_training_data_ultimate_championship.jsonl',
        'capablanca_training_data.jsonl',
        'capablanca_openai_format.jsonl'
    ]
    
    # Try to find the input file
    input_file = None
    for filename in input_files:
        try:
            with open(filename, 'r') as f:
                input_file = filename
                break
        except FileNotFoundError:
            continue
    
    if not input_file:
        print(f"❌ Could not find any of these files: {input_files}")
        return
    
    print(f"📁 Using input file: {input_file}")
    
    # Load and clean data
    cleaned_data = load_and_clean_jsonl(input_file)
    
    if not cleaned_data:
        print("❌ No valid data found. Exiting.")
        return
    
    # Save cleaned data
    output_file = 'capablanca_training_clean_openai_ready.jsonl'
    if save_clean_jsonl(cleaned_data, output_file):
        print(f"\n🎉 SUCCESS! Clean training data saved to: {output_file}")
        
        # Generate sample validation
        generate_sample_validation(cleaned_data)
        
        # Final statistics
        print(f"\n📊 FINAL STATISTICS:")
        print(f"Original entries processed: Unknown")
        print(f"Clean entries created: {len(cleaned_data)}")
        print(f"File size: {sum(len(json.dumps(entry)) for entry in cleaned_data)} characters")
        
        # Minimum dataset size check
        if len(cleaned_data) >= 10:
            print("✅ Meets minimum dataset size requirement (10+ examples)")
        else:
            print("⚠️ Dataset may be too small for fine-tuning (need 10+ examples)")
        
        print(f"\n🚀 NEXT STEPS:")
        print(f"1. Upload '{output_file}' to OpenAI fine-tuning")
        print(f"2. Use this exact file for your fine-tuning job")
        print(f"3. The data is now ASCII-clean and fully compatible")
    else:
        print("❌ Failed to save cleaned data")

if __name__ == "__main__":
    main()