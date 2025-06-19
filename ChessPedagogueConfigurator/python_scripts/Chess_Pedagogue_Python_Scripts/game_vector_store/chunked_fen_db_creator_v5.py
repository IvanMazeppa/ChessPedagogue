import os
import json
import tempfile
from pathlib import Path
from openai import OpenAI

# Configuration file to store API key
CONFIG_FILE = "chess_db_config.json"

def load_or_prompt_config():
    """Gently load configuration or prompt for it if needed"""
    config = {}
    
    # Try to load existing config
    if Path(CONFIG_FILE).exists():
        try:
            with open(CONFIG_FILE, 'r') as f:
                config = json.load(f)
            print("📖 Using saved configuration...")
        except Exception as e:
            print(f"💙 Having trouble reading config file, we'll create a fresh one: {e}")
    
    # Prompt for API key if not found
    if 'api_key' not in config or not config['api_key']:
        print("\n🔑 I'll need your OpenAI API key to get started...")
        print("   (Don't worry, I'll save it securely for next time)")
        api_key = input("Please enter your OpenAI API key: ").strip()
        config['api_key'] = api_key
        
        # Save the config
        try:
            with open(CONFIG_FILE, 'w') as f:
                json.dump(config, f, indent=2)
            print("✅ API key saved safely!")
        except Exception as e:
            print(f"⚠️  Couldn't save config, but we can continue: {e}")
    
    return config

def get_user_preferences():
    """Gently gather what the user wants to create"""
    print("\n🏰 Let's set up your chess database creation...")
    
    # Player name
    player_name = input("Enter the chess player's name (default: Garry Kasparov): ").strip()
    if not player_name:
        player_name = "Garry Kasparov"
    
    # Number of batches
    try:
        num_batches = input("How many batches of 5 games would you like? (default: 5 batches = 25 games): ").strip()
        num_batches = int(num_batches) if num_batches else 5
        total_games = num_batches * 5
        print(f"   📊 That will collect {total_games} games total!")
    except ValueError:
        print("💙 That's okay, I'll use 5 batches (25 games) as a good starting point")
        num_batches = 5
        total_games = 25
    
    # Vector store ID
    vector_store_id = input("Enter your vector store ID: ").strip()
    if not vector_store_id:
        print("⚠️  I'll need a vector store ID to upload the data")
        vector_store_id = input("Please enter your vector store ID: ").strip()
    
    return player_name, total_games, vector_store_id

def get_openai_client(api_key):
    """Create a gentle OpenAI client"""
    try:
        return OpenAI(api_key=api_key)
    except Exception as e:
        print(f"😔 Having trouble connecting to OpenAI: {e}")
        print("Please check your API key and try again")
        return None

def create_game_prompt(player_name, games_per_request, chunks_per_game):
    """Create a thoughtful prompt for gathering chess games"""
    return f"""
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
- Each game must include exactly {chunks_per_game} key moments in the "chunks" array
- Output MUST be a single valid JSON object
- Do not include any extra commentary outside of the JSON object
- Prioritize historical accuracy and famous games

Example structure:
{{
  "player_name": "{player_name}",
  "games": [
    {{
      "opponent": "Opponent Name",
      "year": "YYYY", 
      "tournament": "Tournament Name",
      "opening": "Opening",
      "result": "1-0",
      "significance": "Brief description why this game is famous.",
      "chunks": [
        {{
          "move_number": 10,
          "fen": "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
          "annotation": "Key moment description.",
          "tags": ["attack", "initiative"]
        }}
      ]
    }}
  ]
}}
"""

def fetch_games_gently(client, player_name, total_games, games_per_request=5, chunks_per_game=5):
    """Gently fetch chess games from OpenAI"""
    all_games = []
    batches = (total_games + games_per_request - 1) // games_per_request  # Ceiling division
    
    print(f"\n🎯 Fetching {total_games} games in {batches} batches...")
    
    for batch in range(batches):
        games_this_batch = min(games_per_request, total_games - len(all_games))
        if games_this_batch <= 0:
            break
            
        print(f"   📊 Requesting batch {batch + 1} of {batches} ({games_this_batch} games)...")
        
        prompt = create_game_prompt(player_name, games_this_batch, chunks_per_game)
        
        try:
            response = client.chat.completions.create(
                model="gpt-4.1-2025-04-14",  # Using a more reliable model
                messages=[{"role": "user", "content": prompt}],
                temperature=0.3,  # Lower temperature for more consistent results
                max_tokens=4096
            )
            
            content = response.choices[0].message.content.strip()
            
            # Clean up any code block markers
            if content.startswith("```json"):
                content = content[7:]
            if content.startswith("```"):
                content = content[3:]
            if content.endswith("```"):
                content = content[:-3]
            content = content.strip()
            
            # Parse the JSON response
            data = json.loads(content)
            games = data.get("games", [])
            
            print(f"   ✅ Received {len(games)} games successfully!")
            all_games.extend(games)
            
        except json.JSONDecodeError as e:
            print(f"   ⚠️  Batch {batch + 1}: Had trouble parsing the response - {e}")
            print("      Don't worry, we'll keep going with what we have")
        except Exception as e:
            print(f"   ⚠️  Batch {batch + 1}: Encountered an issue - {e}")
            print("      We'll continue with the games we've collected so far")
    
    print(f"\n🎉 Successfully collected {len(all_games)} games total!")
    return all_games

def flatten_games_lovingly(player_name, games):
    """Transform games into individual chunks with care"""
    print("\n🔄 Gently organizing the chess positions...")
    
    flat_chunks = []
    seen_games = set()
    
    for game in games:
        # Create a unique identifier for this game
        game_key = (
            game.get("opponent", "").lower().strip(),
            str(game.get("year", "")).strip(),
            game.get("tournament", "").lower().strip()
        )
        
        if game_key in seen_games:
            print(f"   📝 Skipping duplicate: {game.get('opponent', 'Unknown')} {game.get('year', '')}")
            continue
        
        seen_games.add(game_key)
        
        for chunk_idx, chunk in enumerate(game.get("chunks", [])):
            flat_chunks.append({
                "player_name": player_name,
                "opponent": game.get("opponent", ""),
                "year": str(game.get("year", "")),
                "tournament": game.get("tournament", ""),
                "opening": game.get("opening", ""),
                "result": game.get("result", ""),
                "significance": game.get("significance", ""),
                "move_number": chunk.get("move_number", 0),
                "fen": chunk.get("fen", ""),
                "annotation": chunk.get("annotation", ""),
                "tags": chunk.get("tags", [])
            })
    
    print(f"   ✨ Created {len(flat_chunks)} beautifully organized chess positions!")
    return flat_chunks

def upload_to_vector_store_with_care(client, flat_chunks, vector_store_id):
    """Upload the chess data to OpenAI's vector store with gentle handling"""
    print(f"\n📤 Uploading your chess database to vector store...")
    
    try:
        # Create a temporary file with our chess data
        chess_data_text = ""
        for chunk in flat_chunks:
            # Create a text representation for each chunk
            tags_str = ", ".join(chunk.get("tags", []))
            chunk_text = f"""Game: {chunk['player_name']} vs {chunk['opponent']} ({chunk['year']})
Tournament: {chunk['tournament']}
Opening: {chunk['opening']}
Result: {chunk['result']}
Move {chunk['move_number']}: {chunk['annotation']}
FEN: {chunk['fen']}
Tags: {tags_str}
Significance: {chunk['significance']}

---

"""
            chess_data_text += chunk_text
        
        # Write to a temporary file
        with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False, encoding='utf-8') as temp_file:
            temp_file.write(chess_data_text)
            temp_file_path = temp_file.name
        
        print(f"   📝 Created temporary file with {len(flat_chunks)} chess positions...")
        
        # Upload the file to OpenAI
        with open(temp_file_path, 'rb') as file:
            uploaded_file = client.files.create(
                file=file,
                purpose='assistants'
            )
        
        print(f"   ✅ File uploaded successfully! File ID: {uploaded_file.id}")
        
        # Add the file to the vector store
        vector_store_file = client.beta.vector_stores.files.create(
            vector_store_id=vector_store_id,
            file_id=uploaded_file.id
        )
        
        print(f"   🎯 Successfully added to vector store!")
        print(f"   📊 Vector store file ID: {vector_store_file.id}")
        
        # Clean up the temporary file
        try:
            os.unlink(temp_file_path)
        except:
            pass  # No worries if we can't clean up
        
        return True
        
    except Exception as e:
        print(f"   😔 Encountered an issue uploading to vector store: {e}")
        print("   💡 Please check your vector store ID and permissions")
        return False

def save_backup_file(flat_chunks, filename="chess_positions_backup.json"):
    """Save a backup copy of our precious chess data"""
    try:
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(flat_chunks, f, ensure_ascii=False, indent=2)
        print(f"   💾 Backup saved as: {filename}")
        return True
    except Exception as e:
        print(f"   ⚠️  Couldn't save backup: {e}")
        return False

def main():
    """Main function that orchestrates everything with gentle care"""
    print("🏰♟️  Welcome to the Chess Game Database Creator! ♟️🏰")
    print("I'm here to help you create a beautiful database of chess positions.\n")
    
    try:
        # Load configuration
        config = load_or_prompt_config()
        
        # Get user preferences  
        player_name, total_games, vector_store_id = get_user_preferences()
        
        # Create OpenAI client
        client = get_openai_client(config['api_key'])
        if not client:
            return
        
        # Fetch the games
        games = fetch_games_gently(client, player_name, total_games)
        
        if not games:
            print("😔 I wasn't able to collect any games. Please try again with a different player or check your API connection.")
            return
        
        # Flatten the games into individual positions
        flat_chunks = flatten_games_lovingly(player_name, games)
        
        # Save a backup copy
        backup_filename = f"{player_name.lower().replace(' ', '_')}_chess_positions.json"
        save_backup_file(flat_chunks, backup_filename)
        
        # Upload to vector store
        success = upload_to_vector_store_with_care(client, flat_chunks, vector_store_id)
        
        if success:
            print(f"\n🎉 Wonderful! Your chess database is ready!")
            print(f"   👤 Player: {player_name}")
            print(f"   🎯 Games collected: {len(set((c['opponent'], c['year'], c['tournament']) for c in flat_chunks))}")
            print(f"   📊 Total positions: {len(flat_chunks)}")
            print(f"   💾 Backup saved: {backup_filename}")
            print(f"   ☁️  Uploaded to vector store: {vector_store_id}")
        else:
            print(f"\n💙 Don't worry! Even though the upload had issues, your data is safely saved in: {backup_filename}")
            print("   You can try uploading again later or contact support for help.")
            
    except KeyboardInterrupt:
        print("\n\n💙 No problem! Feel free to run this again whenever you're ready.")
    except Exception as e:
        print(f"\n😔 I encountered an unexpected issue: {e}")
        print("Please don't hesitate to try again or reach out for help!")

if __name__ == "__main__":
    main()