import json
import os
import glob

def combine_chess_masters():
    """Combines individual chess master JSONL files into one comprehensive file"""
    
    print("✨ Beginning to combine your chess masters collection! ✨")
    
    # Find all the individual chess master files
    master_files = glob.glob("*_training.jsonl")
    
    if not master_files:
        print("I couldn't find any training files in this directory. Are they named with _training.jsonl?")
        return
    
    print(f"Found {len(master_files)} chess master files to combine!")
    
    # Prepare our combined collection
    all_examples = []
    master_counts = {}
    
    # Read each file and collect examples
    for filename in master_files:
        try:
            # Extract the master's name for our records only
            master_name = filename.replace("_training.jsonl", "").replace("_", " ").title()
            
            examples_count = 0
            with open(filename, 'r', encoding='utf-8') as f:
                for line in f:
                    try:
                        # Parse each line as a training example
                        example = json.loads(line)
                        
                        # IMPORTANT: DON'T add the extra master field this time!
                        # Just add the example as-is
                        all_examples.append(example)
                        examples_count += 1
                    except json.JSONDecodeError:
                        print(f"Found an invalid JSON line in {filename}, skipping it")
            
            master_counts[master_name] = examples_count
            print(f"Added {examples_count} examples from {master_name}")
            
        except Exception as e:
            print(f"Had a challenge with {filename}: {e}")
    
    # Save all examples to a combined file
    combined_filename = "chess_masters_combined.jsonl"
    with open(combined_filename, 'w', encoding='utf-8') as f:
        for example in all_examples:
            f.write(json.dumps(example) + '\n')
    
    print(f"\n✨ Success! Created {combined_filename} with {len(all_examples)} training examples!")
    print("\nChess master breakdown:")
    for master, count in master_counts.items():
        print(f"  • {master}: {count} examples")
    
    return combined_filename

# Run the combination process
if __name__ == "__main__":
    combined_file = combine_chess_masters()