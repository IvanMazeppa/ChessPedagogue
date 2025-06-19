import os
import json
import tempfile
import re
from pathlib import Path
from openai import OpenAI
from dataclasses import dataclass
from typing import List, Optional, Dict, Set
import logging
from datetime import datetime

# Enhanced configuration with logging and validation
CONFIG_FILE = "chess_db_config.json"

@dataclass
class ChessPosition:
    """Enhanced data structure for chess positions with validation"""
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
    
    def __post_init__(self):
        """Validate FEN and other data"""
        if not self.is_valid_fen(self.fen):
            raise ValueError(f"Invalid FEN: {self.fen}")
        if not self.is_valid_result(self.result):
            raise ValueError(f"Invalid result: {self.result}")
    
    @staticmethod
    def is_valid_fen(fen: str) -> bool:
        """Basic FEN validation"""
        if not fen or len(fen.split()) < 4:
            return False
        
        board_part = fen.split()[0]
        ranks = board_part.split('/')
        
        if len(ranks) != 8:
            return False
            
        for rank in ranks:
            count = 0
            for char in rank:
                if char.isdigit():
                    count += int(char)
                elif char.lower() in 'prnbqk':
                    count += 1
                else:
                    return False
            if count != 8:
                return False
        
        return True
    
    @staticmethod
    def is_valid_result(result: str) -> bool:
        """Validate chess game result"""
        return result in ["1-0", "0-1", "1/2-1/2", "*"]

class EnhancedChessDBCreator:
    """Enhanced chess database creator with better error handling and features"""
    
    def __init__(self):
        self.setup_logging()
        self.valid_tags = {
            "attack", "sacrifice", "defense", "endgame", "positional", 
            "initiative", "counterplay", "quiet move", "opening", "blunder", 
            "brilliancy", "conversion", "tactics", "strategy", "pawn storm",
            "piece coordination", "weak squares", "king safety", "material advantage"
        }
        self.seen_positions: Set[str] = set()  # Track FEN duplicates
        
    def setup_logging(self):
        """Setup comprehensive logging"""
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler('chess_db_creation.log'),
                logging.StreamHandler()
            ]
        )
        self.logger = logging.getLogger(__name__)

    def load_or_prompt_config(self) -> Dict:
        """Enhanced configuration loading with better error handling"""
        config = {}
        
        if Path(CONFIG_FILE).exists():
            try:
                with open(CONFIG_FILE, 'r') as f:
                    config = json.load(f)
                self.logger.info("📖 Using saved configuration...")
            except Exception as e:
                self.logger.warning(f"💙 Config file issue, creating fresh: {e}")
        
        if 'api_key' not in config or not config['api_key']:
            print("\n🔑 I'll need your OpenAI API key to get started...")
            api_key = input("Please enter your OpenAI API key: ").strip()
            config['api_key'] = api_key
            
            try:
                with open(CONFIG_FILE, 'w') as f:
                    json.dump(config, f, indent=2)
                self.logger.info("✅ API key saved safely!")
            except Exception as e:
                self.logger.warning(f"⚠️  Couldn't save config: {e}")
        
        return config

    def get_enhanced_preferences(self) -> tuple:
        """Enhanced user preferences with smart defaults"""
        print("\n🏰 Let's set up your enhanced chess database creation...")
        
        # Player name with suggestions
        print("🌟 Available masters with existing data:")
        existing_masters = self.get_existing_masters()
        for i, master in enumerate(existing_masters[:5], 1):
            print(f"   {i}. {master}")
        
        player_name = input("Enter chess player name (or select number above): ").strip()
        
        # Handle numeric selection
        if player_name.isdigit() and 1 <= int(player_name) <= len(existing_masters):
            player_name = existing_masters[int(player_name) - 1]
        elif not player_name:
            player_name = "Garry Kasparov"
        
        # Enhanced batch configuration
        print(f"\n📊 Recommended batch sizes:")
        print("   • Quick test: 2 batches (10 games)")
        print("   • Standard: 5 batches (25 games)")  
        print("   • Comprehensive: 10 batches (50 games)")
        
        try:
            batch_input = input("Choose batch size (default: 5): ").strip()
            num_batches = int(batch_input) if batch_input else 5
            total_games = num_batches * 5
            self.logger.info(f"📊 Configured for {total_games} games total")
        except ValueError:
            self.logger.info("💙 Using default: 5 batches (25 games)")
            num_batches = 5
            total_games = 25
        
        # Vector store configuration
        vector_store_id = input("Enter your vector store ID: ").strip()
        if not vector_store_id:
            self.logger.error("⚠️  Vector store ID required!")
            vector_store_id = input("Please enter your vector store ID: ").strip()
        
        # Advanced options
        include_analysis = input("Include engine analysis? (y/N): ").strip().lower() == 'y'
        
        return player_name, total_games, vector_store_id, include_analysis

    def get_existing_masters(self) -> List[str]:
        """Get list of existing chess masters from data files"""
        masters = []
        json_files = Path(".").glob("*_chess_positions.json")
        
        for file_path in json_files:
            name = file_path.stem.replace("_chess_positions", "").replace("_", " ").title()
            masters.append(name)
        
        return sorted(masters)

    def create_enhanced_prompt(self, player_name: str, games_per_request: int, 
                             chunks_per_game: int, include_analysis: bool = False) -> str:
        """Enhanced prompt with better instructions and analysis options"""
        analysis_section = ""
        if include_analysis:
            analysis_section = """
  - engine_eval (optional): centipawn evaluation if available
  - tactical_motif (optional): specific tactical pattern if present"""
        
        return f"""
You are a chess historian creating a comprehensive database for {player_name}.

Return exactly {games_per_request} historically significant games by {player_name}.

For each game, provide:
- opponent: Full name
- year: 4-digit year
- tournament: Official tournament name  
- opening: Specific opening variation
- result: Exact format (1-0, 0-1, 1/2-1/2)
- significance: Why this game is historically important (2-3 sentences)
- chunks: Array of exactly {chunks_per_game} critical moments, each with:
  - move_number: Integer (actual move in game)
  - fen: Valid FEN position string
  - annotation: Insightful description (2-3 sentences) from {player_name}'s perspective
  - tags: 2-4 tags from the approved list{analysis_section}

APPROVED TAGS: "attack", "sacrifice", "defense", "endgame", "positional", "initiative", 
"counterplay", "quiet move", "opening", "blunder", "brilliancy", "conversion", "tactics", 
"strategy", "pawn storm", "piece coordination", "weak squares", "king safety", "material advantage"

CRITICAL REQUIREMENTS:
- Each game MUST have exactly {chunks_per_game} chunks
- FEN strings must be syntactically valid
- Focus on {player_name}'s most famous and instructive games
- Include diverse game types (wins, losses, draws)
- Annotations should reflect {player_name}'s playing style and thought process

Output as valid JSON only, no additional text:

{{
  "player_name": "{player_name}",
  "games": [...]
}}
"""

    def validate_and_enhance_data(self, games: List[Dict]) -> List[ChessPosition]:
        """Validate and enhance the received game data"""
        validated_positions = []
        
        for game_idx, game in enumerate(games):
            try:
                for chunk_idx, chunk in enumerate(game.get("chunks", [])):
                    # Create and validate position
                    position = ChessPosition(
                        player_name=game.get("player_name", ""),
                        opponent=game.get("opponent", ""),
                        year=str(game.get("year", "")),
                        tournament=game.get("tournament", ""),
                        opening=game.get("opening", ""),
                        result=game.get("result", ""),
                        significance=game.get("significance", ""),
                        move_number=chunk.get("move_number", 0),
                        fen=chunk.get("fen", ""),
                        annotation=chunk.get("annotation", ""),
                        tags=self.validate_tags(chunk.get("tags", []))
                    )
                    
                    # Check for duplicate positions
                    fen_key = position.fen.strip()
                    if fen_key in self.seen_positions:
                        self.logger.warning(f"Skipping duplicate FEN: {fen_key}")
                        continue
                    
                    self.seen_positions.add(fen_key)
                    validated_positions.append(position)
                    
            except ValueError as e:
                self.logger.error(f"Game {game_idx}, chunk {chunk_idx}: {e}")
                continue
        
        self.logger.info(f"✅ Validated {len(validated_positions)} positions")
        return validated_positions

    def validate_tags(self, tags: List[str]) -> List[str]:
        """Validate and clean up tags"""
        valid_tags = []
        for tag in tags:
            clean_tag = tag.strip().lower()
            if clean_tag in self.valid_tags:
                valid_tags.append(clean_tag)
            else:
                self.logger.warning(f"Invalid tag skipped: {tag}")
        
        return valid_tags[:4]  # Limit to 4 tags max

    def fetch_games_with_retry(self, client: OpenAI, player_name: str, 
                             total_games: int, include_analysis: bool = False) -> List[Dict]:
        """Enhanced game fetching with retry logic and better error handling"""
        all_games = []
        games_per_request = 5
        chunks_per_game = 5
        batches = (total_games + games_per_request - 1) // games_per_request
        
        self.logger.info(f"🎯 Fetching {total_games} games in {batches} batches...")
        
        for batch in range(batches):
            games_this_batch = min(games_per_request, total_games - len(all_games))
            if games_this_batch <= 0:
                break
            
            success = False
            retry_count = 0
            max_retries = 3
            
            while not success and retry_count < max_retries:
                try:
                    self.logger.info(f"📊 Batch {batch + 1}/{batches} (attempt {retry_count + 1})")
                    
                    prompt = self.create_enhanced_prompt(
                        player_name, games_this_batch, chunks_per_game, include_analysis
                    )
                    
                    response = client.chat.completions.create(
                        model="gpt-4-turbo-2024-04-09",  # More recent model
                        messages=[{"role": "user", "content": prompt}],
                        temperature=0.2,  # Even lower for consistency
                        max_tokens=6000,  # More tokens for detailed analysis
                        timeout=60  # Add timeout
                    )
                    
                    content = self.clean_response_content(response.choices[0].message.content)
                    data = json.loads(content)
                    games = data.get("games", [])
                    
                    if len(games) == games_this_batch:
                        self.logger.info(f"✅ Batch {batch + 1}: {len(games)} games received")
                        all_games.extend(games)
                        success = True
                    else:
                        self.logger.warning(f"Expected {games_this_batch} games, got {len(games)}")
                        retry_count += 1
                        
                except (json.JSONDecodeError, KeyError) as e:
                    self.logger.error(f"Batch {batch + 1}: JSON error - {e}")
                    retry_count += 1
                except Exception as e:
                    self.logger.error(f"Batch {batch + 1}: {e}")
                    retry_count += 1
            
            if not success:
                self.logger.warning(f"Failed to get valid data for batch {batch + 1} after {max_retries} attempts")
        
        self.logger.info(f"🎉 Successfully collected {len(all_games)} games total!")
        return all_games

    def clean_response_content(self, content: str) -> str:
        """Enhanced content cleaning"""
        content = content.strip()
        
        # Remove code block markers
        if content.startswith("```json"):
            content = content[7:]
        elif content.startswith("```"):
            content = content[3:]
        
        if content.endswith("```"):
            content = content[:-3]
        
        # Remove any leading/trailing whitespace and non-JSON text
        lines = content.split('\n')
        json_start = -1
        json_end = -1
        
        for i, line in enumerate(lines):
            if line.strip().startswith('{'):
                json_start = i
                break
        
        for i in range(len(lines) - 1, -1, -1):
            if lines[i].strip().endswith('}'):
                json_end = i
                break
        
        if json_start >= 0 and json_end >= 0:
            content = '\n'.join(lines[json_start:json_end + 1])
        
        return content.strip()

    def create_enhanced_vector_text(self, positions: List[ChessPosition]) -> str:
        """Create enhanced text format for vector store with better search capabilities"""
        chunks = []
        
        for pos in positions:
            # Enhanced chunk with more searchable information
            chunk = f"""=== CHESS POSITION ===
Player: {pos.player_name}
Opponent: {pos.opponent}
Tournament: {pos.tournament} ({pos.year})
Opening: {pos.opening}
Result: {pos.result}

Move {pos.move_number}: {pos.annotation}

FEN Position: {pos.fen}

Tactical Elements: {', '.join(pos.tags)}
Historical Context: {pos.significance}

Playing Style Keywords: {self.generate_style_keywords(pos)}

---
"""
            chunks.append(chunk)
        
        return '\n'.join(chunks)

    def generate_style_keywords(self, position: ChessPosition) -> str:
        """Generate style-specific keywords for better search"""
        style_map = {
            "Magnus Carlsen": "endgame mastery, positional grinding, universal style",
            "Garry Kasparov": "dynamic play, computer analysis, opening preparation",
            "Mikhail Tal": "sacrificial attacks, tactical brilliance, imaginative play",
            "Bobby Fischer": "precise calculation, opening theory, systematic approach",
            "Hikaru Nakamura": "speed chess, tactical awareness, modern online play",
            # Add more as needed
        }
        
        return style_map.get(position.player_name, "classical chess, strategic play")

    def save_enhanced_backup(self, positions: List[ChessPosition], player_name: str) -> str:
        """Save enhanced backup with metadata"""
        timestamp = datetime.now().isoformat()
        
        backup_data = {
            "metadata": {
                "player_name": player_name,
                "created_at": timestamp,
                "total_positions": len(positions),
                "unique_games": len(set((p.opponent, p.year, p.tournament) for p in positions)),
                "script_version": "v7_enhanced"
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
                    "tags": pos.tags
                } for pos in positions
            ]
        }
        
        filename = f"{player_name.lower().replace(' ', '_')}_enhanced_positions_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        
        try:
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(backup_data, f, ensure_ascii=False, indent=2)
            self.logger.info(f"💾 Enhanced backup saved: {filename}")
            return filename
        except Exception as e:
            self.logger.error(f"Backup save failed: {e}")
            return ""

def main():
    """Enhanced main function"""
    creator = EnhancedChessDBCreator()
    
    print("🏰♟️  Welcome to the ENHANCED Chess Database Creator! ♟️🏰")
    print("Now with improved validation, deduplication, and smart features!\n")
    
    try:
        # Enhanced workflow
        config = creator.load_or_prompt_config()
        player_name, total_games, vector_store_id, include_analysis = creator.get_enhanced_preferences()
        
        client = OpenAI(api_key=config['api_key'])
        
        # Fetch and validate games
        games = creator.fetch_games_with_retry(client, player_name, total_games, include_analysis)
        
        if not games:
            creator.logger.error("No games collected. Please try again.")
            return
        
        # Enhanced validation and processing
        positions = creator.validate_and_enhance_data(games)
        
        if not positions:
            creator.logger.error("No valid positions after validation.")
            return
        
        # Save enhanced backup
        backup_file = creator.save_enhanced_backup(positions, player_name)
        
        # Create enhanced vector store content
        vector_text = creator.create_enhanced_vector_text(positions)
        
        # Upload to vector store
        try:
            with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False, encoding='utf-8') as temp_file:
                temp_file.write(vector_text)
                temp_file_path = temp_file.name
            
            with open(temp_file_path, 'rb') as file:
                uploaded_file = client.files.create(file=file, purpose='assistants')
            
            vector_store_file = client.vector_stores.files.create(
                vector_store_id=vector_store_id,
                file_id=uploaded_file.id
            )
            
            # Cleanup
            os.unlink(temp_file_path)
            
            # Success summary
            print(f"\n🎉 Enhanced database creation complete!")
            print(f"   👤 Player: {player_name}")
            print(f"   🎯 Unique games: {len(set((p.opponent, p.year, p.tournament) for p in positions))}")
            print(f"   📊 Total positions: {len(positions)}")
            print(f"   🔍 Duplicate positions filtered: {len(creator.seen_positions) - len(positions)}")
            print(f"   💾 Enhanced backup: {backup_file}")
            print(f"   ☁️  Vector store updated: {vector_store_id}")
            
        except Exception as e:
            creator.logger.error(f"Vector store upload failed: {e}")
            print(f"💙 Data saved locally in: {backup_file}")
            
    except KeyboardInterrupt:
        print("\n💙 Process interrupted. All data saved!")
    except Exception as e:
        creator.logger.error(f"Unexpected error: {e}")

if __name__ == "__main__":
    main()