import json

def validate_training_file(filename):
    print(f"Validating {filename}...")
    line_number = 0
    valid_examples = 0
    
    try:
        with open(filename, 'r') as f:
            for line in f:
                line_number += 1
                try:
                    # Parse the JSON
                    data = json.loads(line)
                    
                    # Check for required fields
                    if 'messages' not in data:
                        print(f"  Line {line_number}: Missing 'messages' field")
                        continue
                    
                    # Check message structure
                    messages = data['messages']
                    if not any(msg.get('role') == 'system' for msg in messages):
                        print(f"  Line {line_number}: Missing system message")
                    
                    if not any(msg.get('role') == 'user' for msg in messages):
                        print(f"  Line {line_number}: Missing user message")
                    
                    if not any(msg.get('role') == 'assistant' for msg in messages):
                        print(f"  Line {line_number}: Missing assistant message")
                    else:
                        valid_examples += 1
                        
                except json.JSONDecodeError:
                    print(f"  Line {line_number}: Invalid JSON format")
        
        print(f"Validation complete! Found {valid_examples} valid examples out of {line_number} lines.")
        
    except FileNotFoundError:
        print(f"Error: Could not find file {filename}")

# Validate your combined file
validate_training_file('chess_masters_combined.jsonl')