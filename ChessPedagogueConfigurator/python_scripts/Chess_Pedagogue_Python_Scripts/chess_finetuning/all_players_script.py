import json

players = ["tal", "kasparov", "carlsen", "capablanca", "alekhine", "botvinnik", "fischer", "karpov", "kramnik", "anand", "lasker", "morphy"]

combined_examples = []

for player in players:
    filename = f"{player}_training_examples.jsonl"
    try:
        with open(filename, 'r') as f:
            for line in f:
                combined_examples.append(json.loads(line))
    except FileNotFoundError:
        print(f"Warning: File {filename} not found")

# Save combined examples
with open('chess_masters_training.jsonl', 'w') as f:
    for example in combined_examples:
        f.write(json.dumps(example) + '\n')