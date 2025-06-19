import json

IN  = "capablanca_training_data_revised.jsonl"
OUT = "capablanca_prepared.jsonl"
SEP = " <|endofprompt|>"

seen = set()

with open(IN, 'r', encoding='utf-8') as fin, open(OUT, 'w', encoding='utf-8') as fout:
    for line in fin:
        data = json.loads(line)
        # Extract user and assistant text
        user = data['messages'][0]['content'].strip()
        asst = data['messages'][1]['content'].strip()
        # Build the prompt/completion
        prompt     = user + SEP
        completion = " " + asst  # leading space
        key = prompt + "|||" + completion
        # Dedupe
        if key in seen:
            continue
        seen.add(key)
        fout.write(json.dumps({
            "prompt": prompt,
            "completion": completion
        }) + "\n")

print(f"Written {len(seen)} unique entries to {OUT}")
