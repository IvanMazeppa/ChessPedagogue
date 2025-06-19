import json
from pathlib import Path
from openai import OpenAI
import re

# Configuration
FINE_TUNE_FILE = Path("/mnt/data/capablanca_training_data.jsonl")
OUTPUT_FILE = Path("/mnt/data/capablanca_full_dataset.jsonl")
TARGET_TOTAL = 350

# Buckets and target counts
BUCKET_TARGETS = {
    "openings": 30,
    "middlegame": 30,
    "endgame": 50,
    "match_anecdote": 60,
    "biography": 50,
    "tactics": 50,
    "strategy_psychology": 25,
    "position_analysis": 25
}

# Initialize OpenAI client
client = OpenAI()

def load_existing_entries(path):
    entries = []
    for line in path.read_text().splitlines():
        if not line.strip():
            continue
        entries.append(json.loads(line))
    return entries

def bucket_entry(entry):
    topic = entry.get("meta", {}).get("topic")
    if not topic:
        # fallback: infer from content patterns
        user = entry["messages"][0]["content"].lower()
        if re.search(r"\b(move|fen|\[|\d\.\.|position)\b", user):
            return "position_analysis"
        # simple keyword-based fallback
        for key in BUCKET_TARGETS:
            if key in user:
                return key
        return "misc"
    return topic

def count_buckets(entries):
    counts = {k:0 for k in BUCKET_TARGETS}
    counts["misc"] = 0
    for e in entries:
        b = bucket_entry(e)
        counts.setdefault(b, 0)
        counts[b] += 1
    return counts

def generate_missing_entries(bucket, n):
    system_prompt = (
        "You are José Raúl Capablanca (1888–1942), third World Chess Champion. "
        "Answer as if speaking to a close student or rival, in candid first-person voice, "
        "warts-and-all. Provide a Q&A pair on " + bucket.replace("_", " ") + "."
    )
    new_entries = []
    for _ in range(n):
        resp = client.chat.completions.create(
            model="capablanca-finetuned",
            messages=[
                {"role":"system", "content": system_prompt},
                {"role":"user","content": f"Please generate one JSONL entry with messages and a meta.topic of \"{bucket}\"."}
            ]
        )
        entry = json.loads(resp.choices[0].message.content)
        new_entries.append(entry)
    return new_entries

# Load and bucket existing entries
existing = load_existing_entries(FINE_TUNE_FILE)
counts = count_buckets(existing)

# Generate missing entries per bucket
all_entries = existing.copy()
for bucket, target in BUCKET_TARGETS.items():
    have = counts.get(bucket, 0)
    missing = target - have
    if missing > 0:
        print(f"Generating {missing} entries for bucket '{bucket}'")
        new = generate_missing_entries(bucket, missing)
        all_entries.extend(new)

# Final dedupe
seen = set()
unique = []
for e in all_entries:
    key = json.dumps(e, sort_keys=True)
    if key not in seen:
        seen.add(key)
        unique.append(e)

# Write full dataset
with open(OUTPUT_FILE, "w") as f:
    for e in unique:
        f.write(json.dumps(e) + "\n")

print(f"Final dataset: {len(unique)} entries -> {OUTPUT_FILE}")
