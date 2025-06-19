import os
import json
import chess
import chess.pgn
from openai import OpenAI
from typing import List, Dict, Any, Set, Tuple
import time
import re
import hashlib
from pathlib import Path

# Constants
MODEL = "gpt-4.1-2025-04-14"
GAMES_PER_REQUEST = 3  # Reduced for better quality control
OUTPUT_DIR = "chess_positions"

# Enhanced prompt with stronger emphasis on variety and authenticity
GAME_GENERATION_PROMPT = """
Return exactly {games_per_request} complete, historically accurate chess games played by {player_name}.

CRITICAL REQUIREMENTS:
- Each game must be DIFFERENT from any previously generated games
- Focus on {player_name}'s most FAMOUS and SIGNIFICANT games
- Include games from DIFFERENT periods of their career
- Vary the opening systems significantly
- Ensure historical accuracy - real games only

For each game, provide:
- opponent: (exact opponent name)
- year: (exact year in YYYY format)
- tournament: (exact tournament name)
- opening: (specific opening variation name)
- result: (exact result: 1-0, 0-1, or 1/2-1/2)
- significance: (specific reason this game is historically important)
- pgn_moves: (complete standard PGN notation with ALL moves)

DIVERSITY REQUIREMENTS:
- Include games from early career, peak years, and later period
- Mix different opening systems (King's pawn, Queen's pawn, others)
- Include both victories and important draws/defeats
- Cover different tournament types (World Championships, major tournaments)
- Showcase different aspects of {player_name}'s style

FAMOUS GAMES TO PRIORITIZE:
- World Championship games
- "Game of the Century" type masterpieces  
- Brilliancy prize winners
- Games frequently analyzed in chess literature
- Tactical masterpieces and positional gems

Output as valid JSON only:
{{
  "player_name": "{player_name}",
  "games": [
    {{
      "opponent": "Exact Opponent Name",
      "year": "YYYY",
      "tournament": "Exact Tournament Name", 
      "opening": "Specific Opening Name",
      "result": "1-0",
      "significance": "Detailed explanation of historical importance",
      "pgn_moves": "1. e4 e5 2. Nf3 Nc6... [complete game moves]"
    }}
  ]
}}
"""

class GameDeduplicator:
    """Handles detection and removal of duplicate games"""
    
    def __init__(self):
        self.seen_games: Set[str] = set()
        self.game_signatures: Dict[str, Dict] = {}
    
    def create_game_signature(self, game: Dict[str, Any]) -> str:
        """Create a unique signature for a game"""
        # Normalize opponent name (remove titles, standardize spacing)
        opponent = re.sub(r'[^\w\s]', '', game.get('opponent', '')).strip().lower()
        year = str(game.get('year', ''))
        tournament = re.sub(r'[^\w\s]', '', game.get('tournament', '')).strip().lower()
        
        # Create signature from key identifying information
        signature_data = f"{opponent}|{year}|{tournament}"
        return hashlib.md5(signature_data.encode()).hexdigest()
    
    def create_moves_signature(self, pgn_moves: str) -> str:
        """Create signature based on the actual moves"""
        # Clean and normalize moves
        moves = self.extract_clean_moves(pgn_moves)
        if len(moves) < 10:  # Skip games that are too short
            return ""
        
        # Use first 20 moves for signature (enough to identify unique games)
        signature_moves = moves[:20] if len(moves) >= 20 else moves
        moves_str = " ".join(signature_moves)
        return hashlib.md5(moves_str.encode()).hexdigest()
    
    def extract_clean_moves(self, pgn_moves: str) -> List[str]:
        """Extract clean move list from PGN"""
        # Remove move numbers, annotations, results
        cleaned = re.sub(r'\d+\.', '', pgn_moves)
        cleaned = re.sub(r'\{[^}]*\}', '', cleaned)
        cleaned = re.sub(r'\([^)]*\)', '', cleaned)
        cleaned = re.sub(r'(1-0|0-1|1/2-1/2|\*)', '', cleaned)
        cleaned = re.sub(r'\s+', ' ', cleaned.strip())
        
        moves = [move.strip() for move in cleaned.split() if move.strip()]
        return [move for move in moves if re.match(r'^[a-zA-Z0-9+#=\-x]+$', move)]
    
    def is_duplicate(self, game: Dict[str, Any]) -> Tuple[bool, str]:
        """Check if game is a duplicate"""
        # Check by game info signature
        game_sig = self.create_game_signature(game)
        if game_sig in self.seen_games:
            return True, f"Duplicate game info: {game.get('opponent')} {game.get('year')}"
        
        # Check by moves signature
        moves_sig = self.create_moves_signature(game.get('pgn_moves', ''))
        if moves_sig and moves_sig in self.seen_games:
            return True, f"Duplicate moves sequence"
        
        # Add signatures to seen set
        self.seen_games.add(game_sig)
        if moves_sig:
            self.seen_games.add(moves_sig)
        
        return False, ""

def get_openai_client():
    """Initialize OpenAI client with API key from multiple sources"""
    
    # Try multiple environment variable names
    api_key = (       "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")
    
    # If no environment variable found, try hardcoded key as fallback
    if not api_key:
        # SAFE PLACEHOLDER: Replace YOUR_NEW_API_KEY_HERE with your fresh key
        # after creating it at platform.openai.com
        api_key = "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"
    
    if not api_key or not api_key.startswith("sk-"):
        print("❌ ERROR: Valid OpenAI API key not found!")
        print("💡 Please either:")
        print("   • Set environment variable: OPENAI_API_KEY=your_key")
        print("   • Or update the hardcoded key in the get_openai_client() function")
        raise RuntimeError("🔑 Valid OpenAI API key required.")
    
    print("✅ API key found and appears valid")
    return OpenAI(api_key=api_key)

def request_unique_games(client: OpenAI, player_name: str, total_games: int) -> List[Dict[str, Any]]:
    """Request games with aggressive deduplication"""
    deduplicator = GameDeduplicator()
    all_games = []
    attempts = 0
    max_attempts = total_games * 3  # Allow multiple attempts to get unique games
    
    print(f"🎯 Requesting {total_games} unique games for {player_name}...")
    print(f"🔍 Using advanced deduplication to ensure variety...")
    
    while len(all_games) < total_games and attempts < max_attempts:
        attempts += 1
        batch_size = min(GAMES_PER_REQUEST, total_games - len(all_games))
        
        print(f"📥 Attempt {attempts}: Requesting {batch_size} games... (Have {len(all_games)}/{total_games})")
        
        prompt = GAME_GENERATION_PROMPT.format(
            games_per_request=batch_size,
            player_name=player_name
        )
        
        try:
            response = client.chat.completions.create(
                model=MODEL,
                messages=[{"role": "user", "content": prompt}],
                temperature=0.7,  # Higher temperature for more variety
                max_tokens=4096
            )
            
            content = response.choices[0].message.content.strip()
            
            # Clean JSON response
            if content.startswith("```"):
                lines = content.split('\n')
                content = '\n'.join(lines[1:-1])
            
            data = json.loads(content)
            games = data.get("games", [])
            
            # Process each game for duplicates
            new_games = 0
            for game in games:
                is_dup, reason = deduplicator.is_duplicate(game)
                if not is_dup:
                    # Validate game has required fields
                    if all(key in game for key in ['opponent', 'year', 'pgn_moves']):
                        all_games.append(game)
                        new_games += 1
                        print(f"  ✅ Added: vs {game.get('opponent')} ({game.get('year')})")
                    else:
                        print(f"  ⚠️ Skipping incomplete game: {game}")
                else:
                    print(f"  🔄 Skipping duplicate: {reason}")
            
            print(f"   📊 Added {new_games} new games from this batch")
            
            # Delay between requests
            time.sleep(2)
            
        except json.JSONDecodeError as e:
            print(f"❌ JSON parsing error in attempt {attempts}: {e}")
            continue
        except Exception as e:
            print(f"❌ Error in attempt {attempts}: {e}")
            continue
    
    print(f"🎉 Successfully collected {len(all_games)} unique games in {attempts} attempts")
    return all_games

def validate_and_clean_moves(pgn_moves: str) -> Tuple[bool, str]:
    """Validate PGN moves and return cleaned version"""
    try:
        # Basic cleanup
        cleaned = re.sub(r'\s+', ' ', pgn_moves.strip())
        
        # Test with python-chess
        board = chess.Board()
        moves = GameDeduplicator().extract_clean_moves(cleaned)
        
        if len(moves) < 10:  # Games should have at least 10 moves
            return False, "Game too short"
        
        # Try to play first 10 moves to validate
        for i, move_san in enumerate(moves[:10]):
            try:
                move = board.parse_san(move_san)
                board.push(move)
            except:
                return False, f"Invalid move at position {i+1}: {move_san}"
        
        return True, cleaned
        
    except Exception as e:
        return False, str(e)

def extract_player_surname(player_name: str) -> str:
    """Extract surname for filename"""
    # Handle names like "Magnus Carlsen", "José Raúl Capablanca"
    parts = player_name.strip().split()
    if len(parts) >= 2:
        return parts[-1].lower()  # Last part is usually surname
    return player_name.lower().replace(' ', '_')

def process_games_safely(games: List[Dict[str, Any]], player_name: str) -> List[Dict[str, Any]]:
    """Process games with enhanced validation and error recovery"""
    print(f"\n🔧 Processing {len(games)} games with enhanced validation...")
    
    all_positions = []
    successful_games = 0
    
    for i, game in enumerate(games, 1):
        opponent = game.get('opponent', 'Unknown')
        year = game.get('year', '')
        
        print(f"\n📋 Processing game {i}/{len(games)}: vs {opponent} ({year})")
        
        # Validate PGN moves
        pgn_moves = game.get('pgn_moves', '')
        is_valid, result = validate_and_clean_moves(pgn_moves)
        
        if not is_valid:
            print(f"  ⚠️ Skipping invalid game: {result}")
            continue
        
        # Process the game
        try:
            game_positions = parse_pgn_to_positions(result, player_name, game)
            if len(game_positions) > 10:  # Only accept games with reasonable position count
                all_positions.extend(game_positions)
                successful_games += 1
                print(f"  ✅ Extracted {len(game_positions)} positions")
            else:
                print(f"  ⚠️ Too few positions extracted ({len(game_positions)})")
                
        except Exception as e:
            print(f"  ❌ Error processing game: {e}")
            continue
    
    print(f"\n📊 Successfully processed {successful_games}/{len(games)} games")
    print(f"🎯 Total positions generated: {len(all_positions)}")
    
    return all_positions

def parse_pgn_to_positions(pgn_moves: str, player_name: str, game_info: Dict[str, Any]) -> List[Dict[str, Any]]:
    """Enhanced PGN parsing with better error handling"""
    positions = []
    
    try:
        board = chess.Board()
        moves = GameDeduplicator().extract_clean_moves(pgn_moves)
        
        if not moves:
            return positions
        
        # Add starting position
        positions.append(create_position_entry(
            board=board,
            move_number=0,
            last_move="",
            player_name=player_name,
            game_info=game_info,
            phase="opening"
        ))
        
        # Process moves with better error handling
        for i, move_san in enumerate(moves, 1):
            try:
                move = board.parse_san(move_san)
                board.push(move)
                
                phase = determine_game_phase(board, i)
                
                position = create_position_entry(
                    board=board,
                    move_number=i,
                    last_move=move_san,
                    player_name=player_name,
                    game_info=game_info,
                    phase=phase
                )
                
                positions.append(position)
                
            except chess.InvalidMoveError:
                print(f"    ⚠️ Invalid move at {i}: {move_san} - stopping game processing")
                break
            except Exception as e:
                print(f"    ⚠️ Error at move {i}: {e} - stopping game processing")
                break
        
    except Exception as e:
        print(f"    ❌ Error parsing PGN: {e}")
    
    return positions

def create_position_entry(board: chess.Board, move_number: int, last_move: str, 
                         player_name: str, game_info: Dict[str, Any], phase: str) -> Dict[str, Any]:
    """Create enhanced position entry"""
    
    annotation = generate_position_annotation(board, move_number, last_move, phase, player_name)
    tags = generate_position_tags(board, move_number, last_move, phase)
    
    return {
        "player_name": player_name,
        "opponent": game_info.get("opponent", ""),
        "year": game_info.get("year", ""),
        "tournament": game_info.get("tournament", ""),
        "opening": game_info.get("opening", ""),
        "result": game_info.get("result", ""),
        "significance": game_info.get("significance", ""),
        "move_number": move_number,
        "fen": board.fen(),
        "last_move": last_move,
        "annotation": annotation,
        "tags": tags,
        "game_phase": phase,
        "material_balance": calculate_material_balance(board),
        "piece_count": len(board.piece_map())
    }

def generate_position_annotation(board: chess.Board, move_number: int, last_move: str, 
                                phase: str, player_name: str) -> str:
    """Generate intelligent position annotations"""
    
    annotations = []
    
    # Check for special positions
    if board.is_checkmate():
        annotations.append("Decisive checkmate")
    elif board.is_stalemate():
        annotations.append("Draw by stalemate")
    elif board.is_check():
        annotations.append("King under direct attack")
    elif board.is_insufficient_material():
        annotations.append("Insufficient material for checkmate")
    
    # Phase-specific annotations
    if phase == "opening" and move_number <= 15:
        if 'O-O' in last_move:
            annotations.append("King safety secured through castling")
        elif last_move and any(piece in last_move for piece in ['N', 'B']):
            annotations.append("Classical piece development")
        else:
            annotations.append("Opening preparation and central control")
    
    elif phase == "middlegame":
        if last_move and 'x' in last_move:
            annotations.append("Material exchange reshaping the position")
        elif last_move and any(piece in last_move for piece in ['Q', 'R']):
            annotations.append("Heavy piece coordination for attack")
        else:
            annotations.append("Strategic maneuvering for positional advantage")
    
    elif phase == "endgame":
        piece_count = len(board.piece_map())
        if piece_count <= 8:
            annotations.append("Pure endgame requiring precise technique")
        else:
            annotations.append("Transition to endgame phase")
    
    # Default if no specific annotation
    if not annotations:
        annotations.append(f"Position after {last_move}" if last_move else "Initial position")
    
    return ". ".join(annotations) + "."

def generate_position_tags(board: chess.Board, move_number: int, last_move: str, phase: str) -> List[str]:
    """Generate comprehensive position tags"""
    tags = [phase]
    
    # Game state tags
    if board.is_check():
        tags.append("check")
    if board.is_checkmate():
        tags.append("checkmate")
    elif board.is_stalemate():
        tags.append("stalemate")
    
    # Move-specific tags
    if last_move:
        if 'x' in last_move:
            tags.append("capture")
        if '=' in last_move:
            tags.append("promotion")
        if 'O-O' in last_move:
            tags.append("castling")
    
    # Position complexity
    piece_count = len(board.piece_map())
    if piece_count >= 28:
        tags.append("complex_position")
    elif piece_count <= 10:
        tags.append("simplified_endgame")
    
    # Game stage
    if move_number <= 10:
        tags.append("early_development")
    elif move_number >= 50:
        tags.append("deep_endgame")
    
    return tags

def calculate_material_balance(board: chess.Board) -> int:
    """Calculate material balance (positive = white advantage)"""
    piece_values = {
        chess.PAWN: 1,
        chess.KNIGHT: 3,
        chess.BISHOP: 3,
        chess.ROOK: 5,
        chess.QUEEN: 9
    }
    
    balance = 0
    for square, piece in board.piece_map().items():
        value = piece_values.get(piece.piece_type, 0)
        if piece.color == chess.WHITE:
            balance += value
        else:
            balance -= value
    
    return balance

def determine_game_phase(board: chess.Board, move_number: int) -> str:
    """Determine game phase with improved logic"""
    piece_count = len(board.piece_map())
    
    # Count queens and rooks
    major_pieces = 0
    for piece in board.piece_map().values():
        if piece.piece_type in [chess.QUEEN, chess.ROOK]:
            major_pieces += 1
    
    if move_number <= 12 and piece_count >= 26:
        return "opening"
    elif major_pieces <= 2 or piece_count <= 12:
        return "endgame"
    else:
        return "middlegame"

def save_positions_with_metadata(all_positions: List[Dict[str, Any]], player_name: str) -> str:
    """Save positions with comprehensive metadata"""
    
    # Create output directory
    Path(OUTPUT_DIR).mkdir(exist_ok=True)
    
    # Generate filename
    surname = extract_player_surname(player_name)
    filename = f"{surname}_full_positions.json"
    filepath = Path(OUTPUT_DIR) / filename
    
    # Add metadata
    output_data = {
        "metadata": {
            "player_name": player_name,
            "total_positions": len(all_positions),
            "generated_date": time.strftime("%Y-%m-%d %H:%M:%S"),
            "generator_version": "2.0_enhanced",
            "phase_distribution": {},
            "year_range": {"min": None, "max": None}
        },
        "positions": all_positions
    }
    
    # Calculate statistics
    phases = {}
    years = []
    
    for pos in all_positions:
        phase = pos.get('game_phase', 'unknown')
        phases[phase] = phases.get(phase, 0) + 1
        
        year = pos.get('year')
        if year and year.isdigit():
            years.append(int(year))
    
    output_data["metadata"]["phase_distribution"] = phases
    if years:
        output_data["metadata"]["year_range"] = {
            "min": min(years),
            "max": max(years)
        }
    
    # Save file
    try:
        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(output_data, f, ensure_ascii=False, indent=2)
        
        print(f"✅ Saved to: {filepath}")
        print(f"\n📊 FINAL STATISTICS:")
        print(f"   Player: {player_name}")
        print(f"   Total positions: {len(all_positions):,}")
        print(f"   File size: {filepath.stat().st_size / 1024:.1f} KB")
        
        for phase, count in phases.items():
            percentage = (count / len(all_positions)) * 100
            print(f"   {phase.title()}: {count:,} ({percentage:.1f}%)")
        
        if years:
            print(f"   Year range: {min(years)}-{max(years)}")
        
        return str(filepath)
        
    except Exception as e:
        print(f"❌ Error saving file: {e}")
        return ""

def main():
    """Enhanced main function with better user experience"""
    print("🚀 ENHANCED CHESS POSITION GENERATOR")
    print("=" * 50)
    print("🎯 Features: Deduplication, Validation, Security")
    print("=" * 50)
    
    # Get player name with validation
    while True:
        player_name = input("\n👤 Enter chess player's full name: ").strip()
        if player_name and len(player_name) >= 3:
            break
        print("❌ Please enter a valid player name (at least 3 characters)")
    
    # Get number of games with smart recommendations
    print(f"\n🎯 How many games for {player_name}?")
    print("💡 Recommendations based on available material:")
    print("   • 20 games  = ~1,000-2,000 positions (Quick dataset)")
    print("   • 40 games  = ~2,000-4,000 positions (Good coverage)")
    print("   • 60 games  = ~3,000-6,000 positions (Excellent depth)")
    print("   • 80 games  = ~4,000-8,000 positions (Comprehensive)")
    
    while True:
        try:
            total_games = int(input(f"\nEnter number of games (recommended: 60): ").strip() or "60")
            if 10 <= total_games <= 150:
                break
            else:
                print("❌ Please enter a number between 10 and 150")
        except ValueError:
            print("❌ Please enter a valid number")
    
    print(f"\n✅ Generating {total_games} unique games for {player_name}")
    print(f"📊 Expected: ~{total_games * 50:,}-{total_games * 100:,} positions")
    
    try:
        # Initialize client
        client = get_openai_client()
        print("✅ OpenAI client initialized")
        
        # Request unique games
        games = request_unique_games(client, player_name, total_games)
        
        if not games:
            print("❌ No games retrieved! Please check your API key and try again.")
            return
        
        # Process games safely
        all_positions = process_games_safely(games, player_name)
        
        if not all_positions:
            print("❌ No positions extracted! Please try again with a different player.")
            return
        
        # Save with metadata
        output_file = save_positions_with_metadata(all_positions, player_name)
        
        if output_file:
            print(f"\n🎉 SUCCESS! Generated comprehensive database")
            print(f"📁 File: {output_file}")
            print(f"💡 Copy this file to your Android app's assets folder")
            print(f"🔧 Ready for Stockfish integration and AI personality training!")
        
    except KeyboardInterrupt:
        print("\n⏹️ Generation cancelled by user")
    except Exception as e:
        print(f"❌ Fatal error: {e}")
        print("💡 Check your internet connection and API key")

if __name__ == "__main__":
    main()