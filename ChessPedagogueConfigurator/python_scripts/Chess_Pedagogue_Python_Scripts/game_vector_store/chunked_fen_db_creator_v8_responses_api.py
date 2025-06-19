import os
import json
import tempfile
import asyncio
import aiohttp
from pathlib import Path
from openai import OpenAI
from dataclasses import dataclass
from typing import List, Optional, Dict, Set
import logging
from datetime import datetime
import hashlib
from collections import defaultdict
import time

# Enhanced configuration for Responses API
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

class ResponsesAPIChessCreator:
    """Revolutionary chess database creator using Responses API and optimized deduplication"""
    
    def __init__(self):
        self.setup_logging()
        self.client = None
        self.position_hashes: Set[str] = set()  # Fast O(1) deduplication
        self.batch_positions: List[ChessPosition] = []
        self.master_configs = self.load_master_configs()
        
    def setup_logging(self):
        """Setup optimized logging"""
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(f'chess_db_{datetime.now().strftime("%Y%m%d")}.log'),
                logging.StreamHandler()
            ]
        )
        self.logger = logging.getLogger(__name__)

    def load_master_configs(self) -> Dict:
        """Load chess master configurations with vector store IDs"""
        default_configs = {
            "Magnus Carlsen": {
                "assistant_id": "asst_TTzxbfvJQz3e80FetQblJ0Gl",
                "vector_store_id": "vs_68365028eb988191b09d8d50e6f11b5d",
                "model": "ft:gpt-4o-mini-2024-07-18:personal:magnus-carlsen-chess-master:AIHj5PaY",
                "style_keywords": "endgame grinding, positional mastery, universal style, calculation depth"
            },
            "Mikhail Tal": {
                "assistant_id": "asst_VKHRwLqOD8dqvQUmRq2vf8OL", 
                "vector_store_id": "vs_cqJaJ2ylgzjFCqz8VmGBHIgz",
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
                "vector_store_id": "vs_auto_detect",  # Will auto-detect or create
                "style_keywords": "dynamic play, computer analysis, deep preparation, fighting spirit"
            },
            "Hikaru Nakamura": {
                "vector_store_id": "vs_auto_detect",
                "style_keywords": "speed chess mastery, tactical alertness, modern streaming, online dominance"
            },
            "Gukesh Dommaraju": {
                "vector_store_id": "vs_auto_detect", 
                "style_keywords": "youthful brilliance, modern preparation, engine-assisted analysis, rising star"
            }
        }
        
        # Try to load from config file
        config_path = Path("master_configs.json")
        if config_path.exists():
            try:
                with open(config_path, 'r') as f:
                    saved_configs = json.load(f)
                    default_configs.update(saved_configs)
                self.logger.info("📖 Loaded master configurations from file")
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

    def auto_detect_vector_store(self, player_name: str) -> Optional[str]:
        """Auto-detect vector store ID for a player"""
        if not self.client:
            return None
            
        try:
            # List all vector stores
            vector_stores = self.client.vector_stores.list()
            
            # Look for stores that match player name
            player_key = player_name.lower().replace(" ", "_")
            
            for store in vector_stores.data:
                store_name = store.name.lower() if store.name else ""
                if player_key in store_name or any(word in store_name for word in player_key.split("_")):
                    self.logger.info(f"🎯 Auto-detected vector store for {player_name}: {store.id}")
                    return store.id
                    
        except Exception as e:
            self.logger.warning(f"Could not auto-detect vector store: {e}")
        
        return None

    def create_responses_api_prompt(self, player_name: str, games_per_request: int) -> Dict:
        """Create enhanced prompt for Responses API"""
        style_info = self.master_configs.get(player_name, {}).get("style_keywords", "classical chess mastery")
        
        return {
            "model": "gpt-4.1-2025-04-14",  # Using the specified model
            "messages": [
                {
                    "role": "system",
                    "content": f"""You are a chess historian specializing in {player_name}. You have deep knowledge of their games, playing style, and historical significance. 

Key aspects of {player_name}'s style: {style_info}

Your task is to provide historically accurate, diverse chess games with detailed annotations that capture {player_name}'s unique perspective and thought process."""
                },
                {
                    "role": "user", 
                    "content": f"""Generate exactly {games_per_request} of {player_name}'s most significant and diverse historical games.

CRITICAL REQUIREMENTS:
- Each game must be historically accurate and different
- Include games from different periods and tournaments  
- Vary opening systems significantly
- Focus on games that showcase {player_name}'s distinctive style

For each game, provide:
- opponent: (exact name)
- year: (YYYY format)
- tournament: (official name)
- opening: (specific variation)
- result: (1-0, 0-1, or 1/2-1/2)
- significance: (why historically important, 2-3 sentences)
- key_positions: Array of 5-7 critical moments, each with:
  - move_number: (actual move number)
  - fen: (valid FEN string)
  - annotation: ({player_name}'s perspective on this position, 2-3 sentences)
  - tactical_elements: (2-4 tags: "attack", "sacrifice", "defense", "endgame", "positional", "initiative", "counterplay", "quiet move", "opening", "blunder", "brilliancy", "conversion", "tactics", "strategy")

Output as valid JSON:
{{
  "player_name": "{player_name}",
  "games": [...]
}}

Ensure annotations reflect {player_name}'s actual playing style and historical perspective."""
                }
            ],
            "temperature": 0.1,  # Very low for consistency
            "max_tokens": 8000,
            "response_format": {"type": "json_object"}  # Force JSON output
        }

    async def fetch_with_responses_api(self, player_name: str, total_games: int) -> List[Dict]:
        """Use Responses API for better quality and structured output"""
        all_games = []
        games_per_request = 3  # Smaller batches for better quality
        total_batches = (total_games + games_per_request - 1) // games_per_request
        
        self.logger.info(f"🚀 Using Responses API for {total_games} games in {total_batches} batches")
        
        for batch_num in range(total_batches):
            games_needed = min(games_per_request, total_games - len(all_games))
            if games_needed <= 0:
                break
                
            try:
                self.logger.info(f"📊 Batch {batch_num + 1}/{total_batches} - requesting {games_needed} games")
                
                prompt_data = self.create_responses_api_prompt(player_name, games_needed)
                
                # Use Responses API
                response = self.client.chat.completions.create(**prompt_data)
                
                content = response.choices[0].message.content.strip()
                
                # Parse JSON response
                try:
                    data = json.loads(content)
                    games = data.get("games", [])
                    
                    if len(games) == games_needed:
                        self.logger.info(f"✅ Batch {batch_num + 1}: Perfect! Got {len(games)} games")
                        all_games.extend(games)
                    else:
                        self.logger.warning(f"Batch {batch_num + 1}: Expected {games_needed}, got {len(games)}")
                        all_games.extend(games)  # Use what we got
                        
                except json.JSONDecodeError as e:
                    self.logger.error(f"Batch {batch_num + 1}: JSON parsing failed - {e}")
                    # Try to extract JSON from content
                    json_match = re.search(r'\\{.*\\}', content, re.DOTALL)
                    if json_match:
                        try:
                            data = json.loads(json_match.group())
                            games = data.get("games", [])
                            all_games.extend(games)
                            self.logger.info(f"🔧 Recovered {len(games)} games from response")
                        except:
                            self.logger.error("Could not recover games from response")
                
                # Add delay between requests
                await asyncio.sleep(1)
                
            except Exception as e:
                self.logger.error(f"Batch {batch_num + 1} failed: {e}")
                continue
        
        self.logger.info(f"🎉 Responses API completed: {len(all_games)} games collected")
        return all_games

    def fast_deduplicate_positions(self, games: List[Dict], player_name: str) -> List[ChessPosition]:
        """Lightning-fast deduplication using hashes - solves the 30+ game bottleneck"""
        self.logger.info("⚡ Starting optimized deduplication...")
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
        self.logger.info(f"⚡ Deduplication completed in {elapsed:.2f}s: {processed_count} unique positions")
        return positions

    def create_batch_json_files(self, positions: List[ChessPosition], player_name: str, batch_size: int = 100) -> List[str]:
        """Create multiple JSON batch files for easier management"""
        self.logger.info(f"📦 Creating batch JSON files (batch size: {batch_size})")
        
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
                    "script_version": "v8_responses_api"
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
                self.logger.info(f"💾 Created batch file: {filename}")
                
            except Exception as e:
                self.logger.error(f"Failed to create batch file {filename}: {e}")
        
        # Create master index file
        index_file = f"{player_name.lower().replace(' ', '_')}_batch_index_{timestamp}.json"
        index_data = {
            "player_name": player_name,
            "total_positions": len(positions),
            "total_batches": len(batch_files),
            "batch_files": batch_files,
            "created_at": timestamp,
            "vector_store_id": self.master_configs.get(player_name, {}).get("vector_store_id", "")
        }
        
        with open(index_file, 'w', encoding='utf-8') as f:
            json.dump(index_data, f, indent=2)
        
        self.logger.info(f"📋 Created index file: {index_file}")
        return batch_files

    def get_or_create_vector_store_id(self, player_name: str) -> str:
        """Get vector store ID automatically or create if needed"""
        # Check config first
        config = self.master_configs.get(player_name, {})
        vector_store_id = config.get("vector_store_id", "")
        
        # If we have a specific ID, use it
        if vector_store_id and vector_store_id != "vs_auto_detect":
            self.logger.info(f"🎯 Using configured vector store for {player_name}: {vector_store_id}")
            return vector_store_id
        
        # Try auto-detection
        detected_id = self.auto_detect_vector_store(player_name)
        if detected_id:
            # Update config
            if player_name not in self.master_configs:
                self.master_configs[player_name] = {}
            self.master_configs[player_name]["vector_store_id"] = detected_id
            self.save_master_configs()
            return detected_id
        
        # Ask user
        print(f"\n🤔 No vector store found for {player_name}")
        print("Options:")
        print("1. Enter existing vector store ID")
        print("2. Create new vector store") 
        print("3. Skip vector store upload")
        
        choice = input("Choose option (1/2/3): ").strip()
        
        if choice == "1":
            vector_store_id = input("Enter vector store ID: ").strip()
            # Update config
            if player_name not in self.master_configs:
                self.master_configs[player_name] = {}
            self.master_configs[player_name]["vector_store_id"] = vector_store_id
            self.save_master_configs()
            return vector_store_id
        elif choice == "2":
            try:
                # Create new vector store
                vector_store = self.client.vector_stores.create(
                    name=f"{player_name} Chess Positions",
                    expires_after={"anchor": "last_active_at", "days": 365}
                )
                vector_store_id = vector_store.id
                self.logger.info(f"✨ Created new vector store: {vector_store_id}")
                
                # Update config
                if player_name not in self.master_configs:
                    self.master_configs[player_name] = {}
                self.master_configs[player_name]["vector_store_id"] = vector_store_id
                self.save_master_configs()
                return vector_store_id
            except Exception as e:
                self.logger.error(f"Failed to create vector store: {e}")
                return ""
        else:
            return ""

    async def run_enhanced_creation(self):
        """Main enhanced creation workflow"""
        print("🚀♟️  Welcome to the REVOLUTIONARY Chess Database Creator v8! ♟️🚀")
        print("✨ Now with Responses API, auto vector store detection, and lightning-fast deduplication!\n")
        
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
            print("🌟 Available masters:")
            masters = list(self.master_configs.keys())
            for i, master in enumerate(masters, 1):
                status = "✅" if self.master_configs[master].get("vector_store_id", "").startswith("vs_") else "🔍"
                print(f"   {i}. {master} {status}")
            
            player_input = input("\nSelect player (number or name): ").strip()
            
            if player_input.isdigit() and 1 <= int(player_input) <= len(masters):
                player_name = masters[int(player_input) - 1]
            else:
                player_name = player_input if player_input else "Magnus Carlsen"
            
            # Get total games
            total_games = int(input("Total games to fetch (default 25): ").strip() or "25")
            
            # Get batch size for JSON files
            batch_size = int(input("JSON batch size (default 100): ").strip() or "100")
            
            self.logger.info(f"🎯 Starting creation for {player_name} - {total_games} games")
            
            # Fetch games using Responses API
            games = await self.fetch_with_responses_api(player_name, total_games)
            
            if not games:
                self.logger.error("❌ No games fetched successfully")
                return
            
            # Fast deduplication
            positions = self.fast_deduplicate_positions(games, player_name)
            
            if not positions:
                self.logger.error("❌ No valid positions after processing")
                return
            
            # Create batch JSON files
            batch_files = self.create_batch_json_files(positions, player_name, batch_size)
            
            # Handle vector store
            vector_store_id = self.get_or_create_vector_store_id(player_name)
            
            if vector_store_id:
                # Upload to vector store
                success = await self.upload_to_vector_store(positions, vector_store_id)
                
                if success:
                    print(f"\n🎉 CREATION COMPLETE!")
                    print(f"   👤 Player: {player_name}")
                    print(f"   📊 Total positions: {len(positions)}")
                    print(f"   📦 Batch files created: {len(batch_files)}")
                    print(f"   ☁️  Vector store updated: {vector_store_id}")
                else:
                    print(f"\n💾 Data saved locally in {len(batch_files)} batch files")
            else:
                print(f"\n💾 Data saved locally in {len(batch_files)} batch files")
                print("   (Skipped vector store upload)")
                
        except Exception as e:
            self.logger.error(f"Creation failed: {e}")

    async def upload_to_vector_store(self, positions: List[ChessPosition], vector_store_id: str) -> bool:
        """Optimized vector store upload"""
        try:
            # Create enhanced text content
            content_parts = []
            for pos in positions:
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

Style Keywords: {self.master_configs.get(pos.player_name, {}).get('style_keywords', '')}
---
"""
                content_parts.append(part)
            
            full_content = '\n'.join(content_parts)
            
            # Upload to vector store
            with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False, encoding='utf-8') as temp_file:
                temp_file.write(full_content)
                temp_path = temp_file.name
            
            with open(temp_path, 'rb') as file:
                uploaded_file = self.client.files.create(file=file, purpose='assistants')
            
            self.client.vector_stores.files.create(
                vector_store_id=vector_store_id,
                file_id=uploaded_file.id
            )
            
            os.unlink(temp_path)
            self.logger.info(f"✅ Successfully uploaded to vector store: {vector_store_id}")
            return True
            
        except Exception as e:
            self.logger.error(f"Vector store upload failed: {e}")
            return False

def main():
    """Run the enhanced creator"""
    creator = ResponsesAPIChessCreator()
    asyncio.run(creator.run_enhanced_creation())

if __name__ == "__main__":
    main()