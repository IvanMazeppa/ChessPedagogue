import os
import json
from openai import OpenAI

# Constants
MODEL = "gpt-4.1-2025-04-14"
GAMES_PER_REQUEST = 5
CHUNKS_PER_GAME = 5
TOTAL_GAMES = 25
OUTPUT_FILE = "flat_chunks.json"
EMBEDDING_MODEL = "text-embedding-3-small"

# Prompt template
PROMPT_TEMPLATE = """
Return exactly {games_per_request} famous chess games played by {player_name}.
For each game, include:
- opponent
- year
- tournament
- opening
- result (1-0, 0-1, or 1/2-1/2)
- significance
- chunks: an array containing exactly {chunks_per_game} key moments, each with:
  - move_number (integer)
  - fen (FEN string)
  - annotation (short, 1-2 sentence description of why this position is key)
  - tags (array of 2-4 from: "attack", "sacrifice", "defense", "endgame", "positional", "initiative", "counterplay", "quiet move", "opening", "blunder", "brilliancy", "conversion")

Strict requirements:
- Each game must include exactly {chunks_per_game} key moments in the "chunks" array. No more, no less.
- Output MUST be a single valid JSON object in the following structure.
- Do not include any extra commentary, text, or formatting outside of the JSON object.
- If you cannot fit all the information, prioritize brevity in annotations but never omit or shorten the number of games or key moments per game.

Example output:
{{
  "player_name": "{player_name}",
  "games": [
    {{
      "opponent": "Opponent Name",
      "year": "YYYY",
      "tournament": "Tournament Name",
      "opening": "Opening",
      "result": "1-0",
      "significance": "Short reason this game is famous.",
      "chunks": [
        {{
          "move_number": 10,
          "fen": "FEN_STRING",
          "annotation": "Short annotation.",
          "tags": ["attack", "initiative"]
        }},
        ...
      ]
    }},
    ...
  ]
}}
"""

def get_openai_client():
    api_key = "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"
    if not api_key:
        raise RuntimeError("Set your OpenAI API key in the OPENAI_API_KEY environment variable.")
    return OpenAI(api_key=api_key)

def prompt_for_games(client, player_name):
    all_games = []
    batches = TOTAL_GAMES // GAMES_PER_REQUEST
    for batch in range(batches):
        print(f"Requesting batch {batch + 1} of {batches}...")
        prompt = PROMPT_TEMPLATE.format(
            games_per_request=GAMES_PER_REQUEST,
            player_name=player_name,
            chunks_per_game=CHUNKS_PER_GAME
        )
        try:
            response = client.chat.completions.create(
                model=MODEL,
                messages=[{"role": "user", "content": prompt}],
                temperature=0.6,
                max_tokens=4096
            )
            content = response.choices[0].message.content.strip()
            # Remove code block markers if present
            if content.startswith("```"):
                content = content.strip("` \n")
            data = json.loads(content)
            games = data.get("games", [])
            print(f"Received {len(games)} games.")
            all_games.extend(games)
        except Exception as e:
            print(f"Error during API call or JSON parsing: {e}")
    return all_games

def flatten_games(player_name, games):
    flat = []
    seen = set()  # To filter duplicates
    for game in games:
        game_key = (
            game.get("opponent", "").lower().strip(),
            str(game.get("year", "")).strip(),
            game.get("tournament", "").lower().strip()
        )
        if game_key in seen:
            continue  # Skip duplicate game
        seen.add(game_key)
        for chunk in game.get("chunks", []):
            flat.append({
                "player_name": player_name,
                "opponent": game.get("opponent", ""),
                "year": game.get("year", ""),
                "tournament": game.get("tournament", ""),
                "opening": game.get("opening", ""),
                "result": game.get("result", ""),
                "significance": game.get("significance", ""),
                "move_number": chunk.get("move_number", 0),
                "fen": chunk.get("fen", ""),
                "annotation": chunk.get("annotation", ""),
                "tags": chunk.get("tags", [])
            })
    return flat

def upsert_to_vector_store(client, flat_chunks, vector_store_id):
    print("Upserting to vector store...")
    try:
        from tqdm import tqdm
    except ImportError:
        tqdm = lambda x: x  # fallback if tqdm not installed

    batch_size = 50
    upsert_items = []
    for idx, chunk in enumerate(flat_chunks):
        embed_input = f"{chunk['annotation']} Tags: {', '.join(chunk['tags'])}"
        upsert_items.append({
            "id": f"{chunk['player_name']}_{chunk['opponent']}_{chunk['year']}_{chunk['move_number']}_{idx}",
            "input": embed_input,
            "metadata": chunk
        })

    # Retrieve the vector store object using the dynamic ID
    vs = client.beta.vector_stores.retrieve(vector_store_id)

    for i in tqdm(range(0, len(upsert_items), batch_size)):
        batch = upsert_items[i:i+batch_size]
        texts = [item["input"] for item in batch]
        # Get embeddings
        embeddings_response = client.embeddings.create(
            model=EMBEDDING_MODEL,
            input=texts
        )
        embeddings = [e.embedding for e in embeddings_response.data]
        upsert_vectors = [
            {
                "id": batch[j]["id"],
                "embedding": embeddings[j],
                "metadata": batch[j]["metadata"]
            }
            for j in range(len(batch))
        ]
        # Upsert to vector store
        vs.upsert(vectors=upsert_vectors)
    print("All chunks have been upserted to the vector store.")

def main():
    player_name = input("Enter the chess player's name: ").strip()
    vector_store_id = input("Enter the target vector store ID: ").strip()
    client = get_openai_client()
    games = prompt_for_games(client, player_name)
    print(f"Total games fetched: {len(games)}")
    flat_chunks = flatten_games(player_name, games)
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(flat_chunks, f, ensure_ascii=False, indent=2)
    print(f"Flattened data written to {OUTPUT_FILE} ({len(flat_chunks)} chunks)")
    upsert_to_vector_store(client, flat_chunks, vector_store_id)

if __name__ == "__main__":
    main()
