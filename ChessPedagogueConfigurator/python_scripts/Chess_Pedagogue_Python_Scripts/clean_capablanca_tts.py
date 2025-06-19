#!/usr/bin/env python3
"""
Clean TTS-problematic content from Capablanca training data.
Removes emotes, actions, and other content that sounds bad when spoken aloud.
"""

import json
import re

def clean_tts_content(text, is_user_question=False):
    """Clean text to be TTS-friendly."""
    
    # Skip cleaning certain user questions that are about the topic
    if is_user_question and any(phrase in text.lower() for phrase in ['pauses as a weapon', 'how do you use pauses']):
        return text
    
    # Patterns to clean (pattern, replacement)
    patterns = [
        (r'\*[^*]*\*', ''),          # Remove *actions*
        (r'—[^—]*—', ''),            # Remove —asides—
        (r'\bsmiles?\b', ''),        # Remove smile/smiles
        (r'\bchuckles?\b', ''),      # Remove chuckle/chuckles  
        (r'\bnods?\b', ''),          # Remove nod/nods
        (r'\bsighs?\b', ''),         # Remove sigh/sighs
        (r'\bfrowns?\b', ''),        # Remove frown/frowns
        (r'\bleans?\s+forward\b', ''), # Remove 'leans forward'
        (r'\bquietly\s*—', '—'),     # Clean up 'quietly—'
        (r'\bgraceful\s+', ''),      # Remove 'graceful smiles'
        (r'\bstiff\s+', ''),         # Remove 'stiff banter'
        (r'\s+', ' '),               # Clean up multiple spaces
    ]
    
    # Don't clean pause references in user questions about pausing
    if not (is_user_question and 'pause' in text.lower()):
        patterns.append((r'\bpauses?\b', ''))  # Remove pause/pauses
    
    cleaned = text
    for pattern, replacement in patterns:
        cleaned = re.sub(pattern, replacement, cleaned, flags=re.IGNORECASE)
    
    # Final cleanup
    cleaned = re.sub(r'\s+', ' ', cleaned).strip()
    
    return cleaned

def main():
    input_file = 'chess_finetuning/training_data/capablanca_training_data_final.jsonl'
    output_file = 'chess_finetuning/training_data/capablanca_training_data_clean.jsonl'
    
    print('🧹 Cleaning TTS-problematic content from Capablanca training data...')
    
    cleaned_entries = []
    changes_made = 0
    
    with open(input_file, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            if not line.strip():
                continue
                
            try:
                data = json.loads(line)
                entry_changed = False
                
                for msg in data.get('messages', []):
                    original_content = msg.get('content', '')
                    is_user = msg.get('role') == 'user'
                    
                    cleaned_content = clean_tts_content(original_content, is_user)
                    
                    if cleaned_content != original_content:
                        msg['content'] = cleaned_content
                        entry_changed = True
                        print(f'  ✅ Cleaned line {line_num} ({msg.get("role")})')
                
                if entry_changed:
                    changes_made += 1
                
                cleaned_entries.append(data)
                
            except Exception as e:
                print(f'  ❌ Error on line {line_num}: {e}')
                continue
    
    # Write cleaned data
    with open(output_file, 'w', encoding='utf-8') as f:
        for data in cleaned_entries:
            f.write(json.dumps(data, ensure_ascii=False) + '\n')
    
    print(f'\n✅ Cleaning complete!')
    print(f'   📊 Total entries: {len(cleaned_entries)}')
    print(f'   🧹 Entries cleaned: {changes_made}')
    print(f'   📁 Clean file: {output_file}')
    print(f'\n🎤 Now TTS-safe for ElevenLabs and OpenAI!')
    
    # Validate the cleaned data
    print(f'\n🔍 Validating cleaned data...')
    issues = 0
    with open(output_file, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            if not line.strip():
                continue
            try:
                data = json.loads(line)
                if 'messages' not in data or len(data['messages']) < 2:
                    issues += 1
            except:
                issues += 1
    
    if issues == 0:
        print(f'   ✅ Perfect! {len(cleaned_entries)} entries validated')
    else:
        print(f'   ⚠️ Found {issues} validation issues')

if __name__ == '__main__':
    main()