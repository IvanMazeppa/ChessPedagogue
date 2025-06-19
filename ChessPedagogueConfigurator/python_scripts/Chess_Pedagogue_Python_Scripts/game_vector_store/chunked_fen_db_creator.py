import os
import json
from openai import OpenAI

# Constants
MODEL = "gpt-4.1-2025-04-14"
GAMES_PER_REQUEST = 5
CHUNKS_PER_GAME = 5
TOTAL_GAMES = 25
OUTPUT_FILE = "flat_chunks.json"

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
    api_key = os.getenv("OPENAI_API_KEY")
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
    for game in games:
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

def main():
    player_name = input("Enter the chess player's name: ").strip()
    client = get_openai_client()
    games = prompt_for_games(client, player_name)
    print(f"Total games fetched: {len(games)}")
    flat_chunks = flatten_games(player_name, games)
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(flat_chunks, f, ensure_ascii=False, indent=2)
    print(f"Flattened data written to {OUTPUT_FILE} ({len(flat_chunks)} chunks)")

if __name__ == "__main__":
    main()
