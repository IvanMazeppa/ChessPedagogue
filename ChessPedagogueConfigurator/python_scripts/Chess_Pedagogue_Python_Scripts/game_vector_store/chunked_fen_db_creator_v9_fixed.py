import os
import json
import tempfile
import asyncio
from pathlib import Path
from openai import OpenAI
from dataclasses import dataclass
from typing import List, Optional, Dict, Set
import logging
from datetime import datetime
import hashlib
from collections import defaultdict
import time
import sys

# Enhanced configuration for Responses API - FIXED VERSION
CONFIG_FILE = "chess_db_config.json"

@dataclass
class ChessPosition:
    """Optimized data structure for chess positions"""
    player_name: str
    opponent: str
    year: str
    tournament: str
    opening: str
    result: str
    significance: str
    move_number: int
    fen: str
    annotation: str
    tags: List[str]
    position_hash: str = ""
    
    def __post_init__(self):
        """Create lightweight hash for fast deduplication"""
        if not self.position_hash:
            # Create hash from FEN + move context (much faster than full validation)
            hash_input = f"{self.fen}|{self.move_number}|{self.player_name}"
            self.position_hash = hashlib.md5(hash_input.encode()).hexdigest()[:16]

class FixedResponsesAPIChessCreator:
    """FIXED chess database creator with corrected vector store handling and Windows compatibility"""
    
    def __init__(self):
        self.setup_logging()
        self.client = None
        self.position_hashes: Set[str] = set()  # Fast O(1) deduplication
        self.batch_positions: List[ChessPosition] = []
        self.master_configs = self.load_master_configs()
        
    def setup_logging(self):
        """Setup Windows-compatible logging without emojis"""
        # Configure logging to avoid Unicode issues on Windows
        log_format = '%(asctime)s - %(levelname)s - %(message)s'
        
        # Use UTF-8 encoding for file handler
        file_handler = logging.FileHandler(
            f'chess_db_{datetime.now().strftime("%Y%m%d")}.log',
            encoding='utf-8'
        )
        file_handler.setFormatter(logging.Formatter(log_format))
        
        # For console, use simple ASCII-friendly format
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setFormatter(logging.Formatter(log_format))
        
        # Configure logger
        logger = logging.getLogger(__name__)
        logger.setLevel(logging.INFO)
        logger.addHandler(file_handler)
        logger.addHandler(console_handler)
        
        self.logger = logger

    def load_master_configs(self) -> Dict:
        """Load chess master configurations with CORRECTED vector store IDs"""
        default_configs = {
            "Magnus Carlsen": {
                "assistant_id": "asst_TTzxbfvJQz3e80FetQblJ0Gl",
                "vector_store_id": "vs_68365028eb988191b09d8d50e6f11b5d",
                "model": "ft:gpt-4o-mini-2024-07-18:personal:magnus-carlsen-chess-master:AIHj5PaY",
                "style_keywords": "endgame grinding, positional mastery, universal style, calculation depth"
            },
            "Mikhail Tal": {
                "assistant_id": "asst_VKHRwLqOD8dqvQUmRq2vf8OL", 
                "vector_store_id": "vs_682f419a57288191aa3cd922b27acb5f",  # CORRECTED ID
                "model": "ft:gpt-4o-mini-2024-07-18:personal:mikhail-tal-chess-master:AHkZonl6",
                "style_keywords": "sacrificial brilliance, tactical genius, intuitive play, artistic combinations"
            },
            "Bobby Fischer": {
                "assistant_id": "asst_s8LrE2n5qWCBF6ZLMeDNLmGd",
                "vector_store_id": "vs_kGfkR5qRFZO1VXBkVZ6TU9a3", 
                "model": "ft:gpt-4o-mini-2024-07-18:personal:bobby-fischer-chess-master:AIHjN5qJ",
                "style_keywords": "precise calculation, opening mastery, systematic approach, clarity"
            },
            "Garry Kasparov": {
                "vector_store_id": "vs_auto_detect",
                "style_keywords": "dynamic play, computer analysis, deep preparation, fighting spirit"
            },
            "Hikaru Nakamura": {
                "vector_store_id": "vs_auto_detect",
                "style_keywords": "speed chess mastery, tactical alertness, modern streaming, online dominance"
            },
            "Gukesh Dommaraju": {
                "vector_store_id": "vs_auto_detect", 
                "style_keywords": "youthful brilliance, modern preparation, engine-assisted analysis, rising star"
            },
            "Nigel Short": {
                "vector_store_id": "vs_auto_detect",
                "style_keywords": "fighting chess, tactical sharpness, English pragmatism, counterattacking"
            }
        }
        
        # Try to load from config file
        config_path = Path("master_configs.json")
        if config_path.exists():
            try:
                with open(config_path, 'r') as f:
                    saved_configs = json.load(f)
                    default_configs.update(saved_configs)
                self.logger.info("Loaded master configurations from file")
            except Exception as e:
                self.logger.warning(f"Could not load master configs: {e}")
        
        return default_configs

    def save_master_configs(self):
        """Save master configurations"""
        try:
            with open("master_configs.json", 'w') as f:
                json.dump(self.master_configs, f, indent=2)
        except Exception as e:
            self.logger.warning(f"Could not save configs: {e}")

    def list_vector_stores(self) -> List[Dict]:
        """List all available vector stores for debugging"""
        if not self.client:
            return []
            
        try:
            stores = self.client.vector_stores.list()
            store_list = []
            
            print("\n=== AVAILABLE VECTOR STORES ===")
            for i, store in enumerate(stores.data, 1):
                store_info = {
                    "id": store.id,
                    "name": store.name or "Unnamed",
                    "created_at": store.created_at,
                    "file_counts": getattr(store, 'file_counts', None)
                }
                store_list.append(store_info)
                print(f"{i}. ID: {store.id}")
                print(f"   Name: {store_info['name']}")
                print(f"   Created: {datetime.fromtimestamp(store_info['created_at'])}")
                if store_info['file_counts']:
                    print(f"   Files: {store_info['file_counts']}")
                print()
            
            return store_list
            
        except Exception as e:
            self.logger.error(f"Could not list vector stores: {e}")
            return []

    def auto_detect_vector_store(self, player_name: str) -> Optional[str]:
        """Auto-detect vector store ID for a player with improved matching"""
        if not self.client:
            return None
            
        try:
            stores = self.client.vector_stores.list()
            player_key = player_name.lower().replace(" ", "_")
            
            # Try exact and partial matches
            candidates = []
            
            for store in stores.data:
                store_name = (store.name or "").lower()
                
                # Exact match
                if player_key in store_name:
                    candidates.append((store.id, store_name, 3))  # High priority
                
                # Partial match (first/last name)
                name_parts = player_key.split("_")
                for part in name_parts:
                    if len(part) > 3 and part in store_name:
                        candidates.append((store.id, store_name, 2))  # Medium priority
            
            # Sort by priority and return best match
            if candidates:
                best_match = sorted(candidates, key=lambda x: x[2], reverse=True)[0]
                self.logger.info(f"Auto-detected vector store for {player_name}: {best_match[0]} ({best_match[1]})")
                return best_match[0]
                
        except Exception as e:
            self.logger.warning(f"Could not auto-detect vector store: {e}")
        
        return None

    def create_enhanced_prompt(self, player_name: str, games_per_request: int) -> Dict:
        """Create enhanced prompt for better game diversity"""
        style_info = self.master_configs.get(player_name, {}).get("style_keywords", "classical chess mastery")
        
        return {
            "model": "gpt-4.1-2025-04-14",
            "messages": [
                {
                    "role": "system",
                    "content": f"""You are a chess historian specializing in {player_name}. You have deep knowledge of their games, playing style, and historical significance. 

Key aspects of {player_name}'s style: {style_info}

Your task is to provide historically accurate, maximally diverse chess games with detailed annotations that capture {player_name}'s unique perspective and thought process.

CRITICAL: Each game must be COMPLETELY DIFFERENT from others. Vary:
- Opening systems (King's pawn, Queen's pawn, English, Reti, etc.)
- Tournament types (World Championships, Olympiads, club matches, etc.)
- Time periods (early career, peak, late career)
- Game types (wins, losses, draws)
- Opponents (different skill levels and styles)"""
                },
                {
                    "role": "user", 
                    "content": f"""Generate exactly {games_per_request} of {player_name}'s most significant and DIVERSE historical games.

MAXIMUM DIVERSITY REQUIREMENTS:
- Use DIFFERENT opening systems for each game (no repetition)
- Include games from DIFFERENT decades if possible
- Mix different result types (wins/draws/losses)
- Choose opponents with different playing styles
- Include rapid/blitz games if historically significant

For each game, provide:
- opponent: (exact name)
- year: (YYYY format)
- tournament: (official name, be specific)
- opening: (specific variation name, avoid repetition)
- result: (1-0, 0-1, or 1/2-1/2)
- significance: (why historically important, 2-3 sentences)
- key_positions: Array of 5-7 critical moments, each with:
  - move_number: (actual move number)
  - fen: (valid, DIFFERENT FEN string - avoid similar positions)
  - annotation: ({player_name}'s perspective, 2-3 sentences, authentic voice)
  - tactical_elements: (2-4 tags: "attack", "sacrifice", "defense", "endgame", "positional", "initiative", "counterplay", "quiet move", "opening", "blunder", "brilliancy", "conversion", "tactics", "strategy")

Output as valid JSON:
{{
  "player_name": "{player_name}",
  "games": [...]
}}

Remember: MAXIMUM DIVERSITY is key. Each game should feel completely different."""
                }
            ],
            "temperature": 0.3,  # Slightly higher for more diversity
            "max_tokens": 8000,
            "response_format": {"type": "json_object"}
        }

    async def fetch_with_responses_api(self, player_name: str, total_games: int) -> List[Dict]:
        """Enhanced API fetching with better error handling"""
        all_games = []
        games_per_request = 3
        total_batches = (total_games + games_per_request - 1) // games_per_request
        
        self.logger.info(f"Using Responses API for {total_games} games in {total_batches} batches")
        
        for batch_num in range(total_batches):
            games_needed = min(games_per_request, total_games - len(all_games))
            if games_needed <= 0:
                break
                
            retry_count = 0
            max_retries = 3
            success = False
            
            while not success and retry_count < max_retries:
                try:
                    self.logger.info(f"Batch {batch_num + 1}/{total_batches} - requesting {games_needed} games (attempt {retry_count + 1})")
                    
                    prompt_data = self.create_enhanced_prompt(player_name, games_needed)
                    
                    response = self.client.chat.completions.create(**prompt_data)
                    content = response.choices[0].message.content.strip()
                    
                    # Parse JSON response
                    try:
                        data = json.loads(content)
                        games = data.get("games", [])
                        
                        if len(games) == games_needed:
                            self.logger.info(f"Batch {batch_num + 1}: SUCCESS! Got {len(games)} games")
                            all_games.extend(games)
                            success = True
                        else:
                            self.logger.warning(f"Batch {batch_num + 1}: Expected {games_needed}, got {len(games)}")
                            if games:  # Use partial results
                                all_games.extend(games)
                                success = True
                            else:
                                retry_count += 1
                                
                    except json.JSONDecodeError as e:
                        self.logger.error(f"Batch {batch_num + 1}: JSON parsing failed - {e}")
                        retry_count += 1
                
                except Exception as e:
                    self.logger.error(f"Batch {batch_num + 1}: API error - {e}")
                    retry_count += 1
                
                if not success and retry_count < max_retries:
                    await asyncio.sleep(2)  # Wait before retry
        
        self.logger.info(f"Responses API completed: {len(all_games)} games collected")
        return all_games

    def fast_deduplicate_positions(self, games: List[Dict], player_name: str) -> List[ChessPosition]:
        """Lightning-fast deduplication using hashes"""
        self.logger.info("Starting optimized deduplication...")
        start_time = time.time()
        
        positions = []
        game_signatures = set()
        processed_count = 0
        
        for game_idx, game in enumerate(games):
            # Quick game-level deduplication
            game_sig = f"{game.get('opponent', '')}{game.get('year', '')}{game.get('tournament', '')}"
            game_hash = hashlib.md5(game_sig.encode()).hexdigest()[:12]
            
            if game_hash in game_signatures:
                self.logger.debug(f"Skipping duplicate game: {game.get('opponent')} {game.get('year')}")
                continue
                
            game_signatures.add(game_hash)
            
            # Process positions with fast hashing
            for pos_data in game.get("key_positions", []):
                try:
                    position = ChessPosition(
                        player_name=player_name,
                        opponent=game.get("opponent", ""),
                        year=str(game.get("year", "")),
                        tournament=game.get("tournament", ""),
                        opening=game.get("opening", ""),
                        result=game.get("result", ""),
                        significance=game.get("significance", ""),
                        move_number=pos_data.get("move_number", 0),
                        fen=pos_data.get("fen", ""),
                        annotation=pos_data.get("annotation", ""),
                        tags=pos_data.get("tactical_elements", [])[:4]
                    )
                    
                    # O(1) hash-based deduplication
                    if position.position_hash not in self.position_hashes:
                        self.position_hashes.add(position.position_hash)
                        positions.append(position)
                        processed_count += 1
                        
                except Exception as e:
                    self.logger.debug(f"Skipping invalid position: {e}")
                    continue
        
        elapsed = time.time() - start_time
        self.logger.info(f"Deduplication completed in {elapsed:.2f}s: {processed_count} unique positions")
        return positions

    def create_batch_json_files(self, positions: List[ChessPosition], player_name: str, batch_size: int = 100) -> List[str]:
        """Create multiple JSON batch files for easier management"""
        self.logger.info(f"Creating batch JSON files (batch size: {batch_size})")
        
        batch_files = []
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        
        for i in range(0, len(positions), batch_size):
            batch_positions = positions[i:i + batch_size]
            batch_num = (i // batch_size) + 1
            
            batch_data = {
                "metadata": {
                    "player_name": player_name,
                    "batch_number": batch_num,
                    "batch_size": len(batch_positions),
                    "created_at": timestamp,
                    "total_batches": (len(positions) + batch_size - 1) // batch_size,
                    "script_version": "v9_fixed"
                },
                "positions": [
                    {
                        "player_name": pos.player_name,
                        "opponent": pos.opponent,
                        "year": pos.year,
                        "tournament": pos.tournament,
                        "opening": pos.opening,
                        "result": pos.result,
                        "significance": pos.significance,
                        "move_number": pos.move_number,
                        "fen": pos.fen,
                        "annotation": pos.annotation,
                        "tags": pos.tags,
                        "position_hash": pos.position_hash
                    } for pos in batch_positions
                ]
            }
            
            filename = f"{player_name.lower().replace(' ', '_')}_batch_{batch_num:03d}_{timestamp}.json"
            
            try:
                with open(filename, 'w', encoding='utf-8') as f:
                    json.dump(batch_data, f, ensure_ascii=False, indent=2)
                
                batch_files.append(filename)
                self.logger.info(f"Created batch file: {filename}")
                
            except Exception as e:
                self.logger.error(f"Failed to create batch file {filename}: {e}")
        
        # Create master index file
        index_file = f"{player_name.lower().replace(' ', '_')}_batch_index_{timestamp}.json"
        
        # Get vector store ID
        vector_store_id = self.master_configs.get(player_name, {}).get("vector_store_id", "")
        
        index_data = {
            "player_name": player_name,
            "total_positions": len(positions),
            "total_batches": len(batch_files),
            "batch_files": batch_files,
            "created_at": timestamp,
            "vector_store_id": vector_store_id
        }
        
        with open(index_file, 'w', encoding='utf-8') as f:
            json.dump(index_data, f, indent=2)
        
        self.logger.info(f"Created index file: {index_file}")
        return batch_files

    def get_or_create_vector_store_id(self, player_name: str) -> str:
        """Get vector store ID with better debugging and options"""
        # Check config first
        config = self.master_configs.get(player_name, {})
        vector_store_id = config.get("vector_store_id", "")
        
        # If we have a specific ID, verify it exists
        if vector_store_id and vector_store_id != "vs_auto_detect":
            try:
                # Test if vector store exists
                store = self.client.vector_stores.retrieve(vector_store_id)
                self.logger.info(f"Using configured vector store for {player_name}: {vector_store_id}")
                return vector_store_id
            except Exception as e:
                self.logger.error(f"Configured vector store {vector_store_id} not found: {e}")
                print(f"\nERROR: Vector store {vector_store_id} not found!")
                
        # Show available stores and let user choose
        print(f"\nVector store selection for {player_name}:")
        available_stores = self.list_vector_stores()
        
        if available_stores:
            print("Available options:")
            print("0. Skip vector store upload")
            for i, store in enumerate(available_stores, 1):
                print(f"{i}. {store['name']} ({store['id']})")
            print(f"{len(available_stores) + 1}. Create new vector store")
            print(f"{len(available_stores) + 2}. Auto-detect by name")
            
            choice = input(f"\nSelect option (0-{len(available_stores) + 2}): ").strip()
            
            try:
                choice_num = int(choice)
                if choice_num == 0:
                    return ""
                elif 1 <= choice_num <= len(available_stores):
                    selected_store = available_stores[choice_num - 1]
                    vector_store_id = selected_store['id']
                elif choice_num == len(available_stores) + 1:
                    # Create new
                    vector_store_id = self.create_new_vector_store(player_name)
                elif choice_num == len(available_stores) + 2:
                    # Auto-detect
                    vector_store_id = self.auto_detect_vector_store(player_name) or ""
                else:
                    self.logger.warning("Invalid choice, skipping upload")
                    return ""
            except ValueError:
                self.logger.warning("Invalid input, skipping upload")
                return ""
        else:
            print("No vector stores found. Create new? (y/N): ")
            if input().strip().lower() == 'y':
                vector_store_id = self.create_new_vector_store(player_name)
            else:
                return ""
        
        # Update config if we got a valid ID
        if vector_store_id:
            if player_name not in self.master_configs:
                self.master_configs[player_name] = {}
            self.master_configs[player_name]["vector_store_id"] = vector_store_id
            self.save_master_configs()
            
        return vector_store_id

    def create_new_vector_store(self, player_name: str) -> str:
        """Create a new vector store"""
        try:
            vector_store = self.client.vector_stores.create(
                name=f"{player_name} Chess Positions Database",
                expires_after={"anchor": "last_active_at", "days": 365}
            )
            self.logger.info(f"Created new vector store: {vector_store.id}")
            return vector_store.id
        except Exception as e:
            self.logger.error(f"Failed to create vector store: {e}")
            return ""

    async def upload_to_vector_store(self, positions: List[ChessPosition], vector_store_id: str) -> bool:
        """Optimized vector store upload with better error handling"""
        try:
            self.logger.info(f"Uploading {len(positions)} positions to vector store {vector_store_id}")
            
            # Create enhanced text content
            content_parts = []
            for pos in positions:
                style_keywords = self.master_configs.get(pos.player_name, {}).get('style_keywords', '')
                
                part = f"""=== CHESS POSITION ===
Player: {pos.player_name}
vs {pos.opponent} ({pos.year})
Tournament: {pos.tournament}
Opening: {pos.opening}
Result: {pos.result}

Move {pos.move_number}: {pos.annotation}
FEN: {pos.fen}
Elements: {', '.join(pos.tags)}
Context: {pos.significance}

Style Keywords: {style_keywords}
---
"""
                content_parts.append(part)
            
            full_content = '\n'.join(content_parts)
            
            # Upload to vector store
            with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False, encoding='utf-8') as temp_file:
                temp_file.write(full_content)
                temp_path = temp_file.name
            
            self.logger.info(f"Uploading file to OpenAI...")
            with open(temp_path, 'rb') as file:
                uploaded_file = self.client.files.create(file=file, purpose='assistants')
            
            self.logger.info(f"File uploaded: {uploaded_file.id}")
            
            # Add to vector store
            self.logger.info(f"Adding file to vector store...")
            vector_store_file = self.client.vector_stores.files.create(
                vector_store_id=vector_store_id,
                file_id=uploaded_file.id
            )
            
            # Cleanup
            os.unlink(temp_path)
            
            self.logger.info(f"SUCCESS! File added to vector store: {vector_store_file.id}")
            return True
            
        except Exception as e:
            self.logger.error(f"Vector store upload failed: {e}")
            return False

    async def run_enhanced_creation(self):
        """Main enhanced creation workflow with better error handling"""
        print("=== FIXED Chess Database Creator v9 ===")
        print("Fixed vector store handling and Windows compatibility!\n")
        
        try:
            # Load config
            config = {}
            if Path(CONFIG_FILE).exists():
                with open(CONFIG_FILE, 'r') as f:
                    config = json.load(f)
            
            if 'api_key' not in config:
                config['api_key'] = input("Enter OpenAI API key: ").strip()
                with open(CONFIG_FILE, 'w') as f:
                    json.dump(config, f, indent=2)
            
            self.client = OpenAI(api_key=config['api_key'])
            
            # Enhanced player selection
            print("Available masters:")
            masters = list(self.master_configs.keys())
            for i, master in enumerate(masters, 1):
                status = "OK" if self.master_configs[master].get("vector_store_id", "").startswith("vs_") else "AUTO"
                print(f"   {i}. {master} ({status})")
            
            player_input = input("\nSelect player (number or name): ").strip()
            
            if player_input.isdigit() and 1 <= int(player_input) <= len(masters):
                player_name = masters[int(player_input) - 1]
            else:
                player_name = player_input if player_input else "Magnus Carlsen"
            
            # Get parameters
            total_games = int(input("Total games to fetch (default 25): ").strip() or "25")
            batch_size = int(input("JSON batch size (default 100): ").strip() or "100")
            
            self.logger.info(f"Starting creation for {player_name} - {total_games} games")
            
            # Fetch games
            games = await self.fetch_with_responses_api(player_name, total_games)
            
            if not games:
                self.logger.error("No games fetched successfully")
                return
            
            # Fast deduplication
            positions = self.fast_deduplicate_positions(games, player_name)
            
            if not positions:
                self.logger.error("No valid positions after processing")
                return
            
            # Create batch JSON files
            batch_files = self.create_batch_json_files(positions, player_name, batch_size)
            
            # Handle vector store with proper error checking
            vector_store_id = self.get_or_create_vector_store_id(player_name)
            
            if vector_store_id:
                success = await self.upload_to_vector_store(positions, vector_store_id)
                
                if success:
                    print(f"\n=== CREATION COMPLETE! ===")
                    print(f"Player: {player_name}")
                    print(f"Total positions: {len(positions)}")
                    print(f"Batch files created: {len(batch_files)}")
                    print(f"Vector store updated: {vector_store_id}")
                else:
                    print(f"\n=== PARTIAL SUCCESS ===")
                    print(f"Data saved locally in {len(batch_files)} batch files")
                    print("Vector store upload failed - check logs for details")
            else:
                print(f"\n=== LOCAL SAVE COMPLETE ===")
                print(f"Data saved locally in {len(batch_files)} batch files")
                print("(Vector store upload skipped)")
                
        except Exception as e:
            self.logger.error(f"Creation failed: {e}")
            print(f"\nERROR: {e}")

def main():
    """Run the fixed creator"""
    creator = FixedResponsesAPIChessCreator()
    asyncio.run(creator.run_enhanced_creation())

if __name__ == "__main__":
    main()