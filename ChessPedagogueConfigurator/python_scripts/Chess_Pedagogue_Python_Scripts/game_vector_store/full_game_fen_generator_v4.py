import os
import json
import chess
import chess.pgn
from openai import OpenAI
from typing import List, Dict, Any
import time
import re

# Constants
MODEL = "gpt-4.1-2025-04-14"
GAMES_PER_REQUEST = 5  # Good batch size for API requests
OUTPUT_FILE = "full_game_positions.json"

# Enhanced prompt for complete games with full move sequences
GAME_GENERATION_PROMPT = """
Return exactly {games_per_request} complete, famous chess games played by {player_name}.

For each game, provide:
- opponent: (opponent's name)
- year: (YYYY format)
- tournament: (tournament name)
- opening: (opening name)
- result: (1-0, 0-1, or 1/2-1/2)
- significance: (why this game is famous/important)
- pgn_moves: (complete PGN move sequence like "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6..." - include ALL moves of the complete game)

Requirements:
- Each game must be a real, historically accurate game
- Include the COMPLETE move sequence from start to finish
- Games should be among the player's most famous/instructive games
- Focus on games that showcase the player's distinctive style
- PGN moves should be in standard algebraic notation

IMPORTANT: Ensure variety across:
- Different time periods (early career, peak, late career)
- Various opening systems (1.e4, 1.d4, 1.Nf3, 1.c4, etc.)
- Different game types (attacking, positional, endgame masterpieces)
- Major tournaments and matches (World Championships, Candidates, etc.)
- Both victories and instructive defeats

Output as valid JSON only:
{{
  "player_name": "{player_name}",
  "games": [
    {{
      "opponent": "Opponent Name",
      "year": "YYYY",
      "tournament": "Tournament Name", 
      "opening": "Opening Name",
      "result": "1-0",
      "significance": "Brief description of why this game is famous",
      "pgn_moves": "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8 10. d4 Nbd7..."
    }}
  ]
}}
"""

def get_openai_client():
    """Initialize OpenAI client with API key from environment"""
    api_key = "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"
    if not api_key:
        raise RuntimeError("🔑 Set your OpenAI API key in the OPENAI_API_KEY environment variable.")
    return OpenAI(api_key=api_key)

def request_complete_games(client: OpenAI, player_name: str, total_games: int) -> List[Dict[str, Any]]:
    """Request complete games from OpenAI with full move sequences"""
    all_games = []
    batches = total_games // GAMES_PER_REQUEST
    remaining_games = total_games % GAMES_PER_REQUEST
    
    print(f"🎯 Requesting {total_games} complete games for {player_name}...")
    print(f"📊 This will generate approximately {total_games * 50}-{total_games * 100} positions!")
    
    # Process full batches
    for batch in range(batches):
        progress_pct = ((batch + 1) / (batches + (1 if remaining_games > 0 else 0))) * 100
        print(f"📥 Batch {batch + 1}/{batches + (1 if remaining_games > 0 else 0)} ({progress_pct:.1f}%) - Requesting {GAMES_PER_REQUEST} games...")
        
        prompt = GAME_GENERATION_PROMPT.format(
            games_per_request=GAMES_PER_REQUEST,
            player_name=player_name
        )
        
        try:
            response = client.chat.completions.create(
                model=MODEL,
                messages=[{"role": "user", "content": prompt}],
                temperature=0.4,  # Lower temperature for more accurate historical data
                max_tokens=4096
            )
            
            content = response.choices[0].message.content.strip()
            
            # Clean up any code block markers
            if content.startswith("```"):
                lines = content.split('\n')
                content = '\n'.join(lines[1:-1])  # Remove first and last lines
            
            data = json.loads(content)
            games = data.get("games", [])
            
            print(f"✅ Received {len(games)} complete games")
            all_games.extend(games)
            
            # Small delay to be respectful to the API
            time.sleep(1)
            
        except json.JSONDecodeError as e:
            print(f"❌ JSON parsing error in batch {batch + 1}: {e}")
            print(f"Raw response: {content[:200]}...")
            continue
        except Exception as e:
            print(f"❌ Error in batch {batch + 1}: {e}")
            continue
    
    # Handle remaining games if any
    if remaining_games > 0:
        print(f"📥 Final batch - Requesting {remaining_games} games...")
        
        prompt = GAME_GENERATION_PROMPT.format(
            games_per_request=remaining_games,
            player_name=player_name
        )
        
        try:
            response = client.chat.completions.create(
                model=MODEL,
                messages=[{"role": "user", "content": prompt}],
                temperature=0.4,
                max_tokens=4096
            )
            
            content = response.choices[0].message.content.strip()
            
            if content.startswith("```"):
                lines = content.split('\n')
                content = '\n'.join(lines[1:-1])
            
            data = json.loads(content)
            games = data.get("games", [])
            
            print(f"✅ Received {len(games)} complete games")
            all_games.extend(games)
            
        except json.JSONDecodeError as e:
            print(f"❌ JSON parsing error in final batch: {e}")
            print(f"Raw response: {content[:200]}...")
        except Exception as e:
            print(f"❌ Error in final batch: {e}")
    
    print(f"🎉 Total games collected: {len(all_games)}")
    return all_games

def parse_pgn_moves(pgn_moves: str) -> List[str]:
    """Extract individual moves from PGN string with enhanced validation"""
    try:
        # Remove move numbers like "1. " "2. " etc.
        cleaned = re.sub(r'\d+\.', '', pgn_moves)
        
        # Remove annotations like {comments}, (variations), etc.
        cleaned = re.sub(r'\{[^}]*\}', '', cleaned)
        cleaned = re.sub(r'\([^)]*\)', '', cleaned)
        
        # Remove result indicators
        cleaned = re.sub(r'(1-0|0-1|1/2-1/2|\*)', '', cleaned)
        
        # Remove extra whitespace and split
        cleaned = re.sub(r'\s+', ' ', cleaned.strip())
        
        # Split into individual moves and filter out empty strings
        moves = [move.strip() for move in cleaned.split() if move.strip()]
        
        # Basic move validation - filter out obviously invalid moves
        valid_moves = []
        for move in moves:
            # Skip moves that are clearly not chess notation
            if len(move) < 2 or len(move) > 8:
                continue
            # Skip moves with weird characters
            if not re.match(r'^[a-zA-Z0-9+#=\-x]+$', move):
                continue
            valid_moves.append(move)
        
        return valid_moves
        
    except Exception as e:
        print(f"Error parsing moves: {e}")
        return []

def parse_pgn_to_positions(pgn_moves: str, player_name: str, game_info: Dict[str, Any]) -> List[Dict[str, Any]]:
    """Parse PGN moves and generate FEN + annotations for every position with enhanced error handling"""
    positions = []
    
    try:
        # Create a chess board
        board = chess.Board()
        
        # Parse the PGN moves
        moves = parse_pgn_moves(pgn_moves)
        
        if not moves:
            print(f"⚠️ No moves found in: {pgn_moves[:50]}...")
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
        
        # Process each move with better error recovery
        consecutive_errors = 0
        max_consecutive_errors = 5  # Stop if too many errors in a row
        
        for i, move_san in enumerate(moves, 1):
            try:
                # Parse and make the move
                move = board.parse_san(move_san)
                board.push(move)
                
                # Reset error counter on successful move
                consecutive_errors = 0
                
                # Determine game phase
                phase = determine_game_phase(board, i)
                
                # Create position entry
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
                consecutive_errors += 1
                print(f"⚠️ Invalid move at position {i}: {move_san}")
                
                # If too many consecutive errors, likely the game sequence is corrupted
                if consecutive_errors >= max_consecutive_errors:
                    print(f"🛑 Too many consecutive errors, stopping game processing at move {i}")
                    break
                    
                continue
            except Exception as e:
                consecutive_errors += 1
                print(f"⚠️ Error processing move {i} ({move_san}): {e}")
                
                if consecutive_errors >= max_consecutive_errors:
                    print(f"🛑 Too many consecutive errors, stopping game processing at move {i}")
                    break
                    
                continue
        
        print(f"✅ Generated {len(positions)} positions from game vs {game_info.get('opponent', 'Unknown')}")
        
    except Exception as e:
        print(f"❌ Error parsing PGN: {e}")
        print(f"PGN snippet: {pgn_moves[:100]}...")
    
    return positions

def create_position_entry(board: chess.Board, move_number: int, last_move: str, 
                         player_name: str, game_info: Dict[str, Any], phase: str) -> Dict[str, Any]:
    """Create a position entry with rich annotations"""
    
    # Generate tactical and positional annotations
    annotation = generate_position_annotation(board, move_number, last_move, phase, player_name)
    
    # Generate tactical tags
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
        "game_phase": phase
    }

def generate_position_annotation(board: chess.Board, move_number: int, last_move: str, 
                                phase: str, player_name: str) -> str:
    """Generate intelligent annotations for each position"""
    
    annotations = []
    
    # Opening phase annotations
    if phase == "opening" and move_number <= 15:
        if board.is_check():
            annotations.append("Early check creates tactical complications")
        elif last_move and any(piece in last_move for piece in ['N', 'B']):
            annotations.append("Piece development following classical principles")
        elif 'O-O' in last_move:
            annotations.append("King safety secured with castling")
        else:
            annotations.append("Opening preparation and piece coordination")
    
    # Middlegame annotations
    elif phase == "middlegame":
        if board.is_check():
            annotations.append("Tactical check increasing pressure")
        elif last_move and 'x' in last_move:
            annotations.append("Material exchange altering the position")
        elif last_move and any(piece in last_move for piece in ['Q', 'R']):
            annotations.append("Heavy piece activation for attack")
        else:
            annotations.append("Strategic maneuvering for advantage")
    
    # Endgame annotations  
    elif phase == "endgame":
        piece_count = len(board.piece_map())
        if piece_count <= 10:
            annotations.append("Pure endgame technique required")
        elif board.is_check():
            annotations.append("Forcing moves in the endgame")
        else:
            annotations.append("Endgame conversion and king activity")
    
    # Add check/mate annotations
    if board.is_checkmate():
        annotations.append("CHECKMATE - game concludes")
    elif board.is_stalemate():
        annotations.append("Stalemate draw")
    elif board.is_check():
        annotations.append("King under attack")
    
    # Default annotation if none generated
    if not annotations:
        annotations.append(f"Position after {last_move}" if last_move else "Starting position")
    
    return ". ".join(annotations) + "."

def generate_position_tags(board: chess.Board, move_number: int, last_move: str, phase: str) -> List[str]:
    """Generate tactical/strategic tags for each position"""
    tags = []
    
    # Phase tags
    tags.append(phase)
    
    # Tactical tags
    if board.is_check():
        tags.append("check")
    
    if board.is_checkmate():
        tags.append("checkmate")
    elif board.is_stalemate():
        tags.append("stalemate")
    
    if last_move:
        if 'x' in last_move:
            tags.append("capture")
        if '+' in last_move:
            tags.append("check")
        if '#' in last_move:
            tags.append("checkmate")
        if '=' in last_move:
            tags.append("promotion")
        if 'O-O' in last_move:
            tags.append("castling")
    
    # Positional tags based on piece count
    piece_count = len(board.piece_map())
    if piece_count >= 28:
        tags.append("full_board")
    elif piece_count <= 10:
        tags.append("endgame")
    elif piece_count <= 20:
        tags.append("late_middlegame")
    else:
        tags.append("middlegame")
    
    # Strategic tags
    if move_number <= 10:
        tags.append("early_game")
    elif move_number >= 40:
        tags.append("late_game")
    
    return tags

def determine_game_phase(board: chess.Board, move_number: int) -> str:
    """Determine what phase of the game we're in"""
    piece_count = len(board.piece_map())
    
    if move_number <= 15 and piece_count >= 24:
        return "opening"
    elif piece_count <= 12:
        return "endgame"
    else:
        return "middlegame"

def save_positions_to_file(all_positions: List[Dict[str, Any]], filename: str):
    """Save all positions to JSON file"""
    try:
        with open(filename, "w", encoding="utf-8") as f:
            json.dump(all_positions, f, ensure_ascii=False, indent=2)
        
        print(f"✅ Saved {len(all_positions)} positions to {filename}")
        
        # Print statistics
        print(f"\n📊 STATISTICS:")
        print(f"   Total positions: {len(all_positions)}")
        
        # Count by phase
        phases = {}
        for pos in all_positions:
            phase = pos.get('game_phase', 'unknown')
            phases[phase] = phases.get(phase, 0) + 1
        
        for phase, count in phases.items():
            print(f"   {phase.title()}: {count} positions")
        
        # Count by player
        players = {}
        for pos in all_positions:
            player = pos.get('player_name', 'unknown')
            players[player] = players.get(player, 0) + 1
            
        for player, count in players.items():
            print(f"   {player}: {count} positions")
            
    except Exception as e:
        print(f"❌ Error saving to file: {e}")

def main():
    """Main execution function"""
    print("🚀 FULL GAME FEN GENERATOR - Creating comprehensive chess databases!")
    print("=" * 60)
    
    # Get player name
    player_name = input("Enter the chess player's name: ").strip()
    if not player_name:
        print("❌ Player name is required!")
        return
    
    # Get number of games with helpful suggestions
    print(f"\n🎯 How many games would you like to generate for {player_name}?")
    print("💡 Suggestions:")
    print("   • 25 games  = ~1,250-2,500 positions (Quick test)")
    print("   • 50 games  = ~2,500-5,000 positions (Good coverage)")
    print("   • 75 games  = ~3,750-7,500 positions (Excellent depth)")
    print("   • 100 games = ~5,000-10,000 positions (Maximum coverage)")
    
    while True:
        try:
            total_games = int(input("\nEnter number of games (recommended: 75): ").strip())
            if total_games <= 0:
                print("❌ Please enter a positive number!")
                continue
            elif total_games > 200:
                confirm = input(f"⚠️ {total_games} games will take a long time. Continue? (y/n): ").strip().lower()
                if confirm != 'y' and confirm != 'yes':
                    continue
            break
        except ValueError:
            print("❌ Please enter a valid number!")
    
    print(f"\n✅ Generating {total_games} games for {player_name}")
    print(f"📊 Expected output: ~{total_games * 50:,}-{total_games * 100:,} positions")
    
    try:
        # Initialize OpenAI client
        client = get_openai_client()
        print(f"✅ OpenAI client initialized")
        
        # Request complete games
        games = request_complete_games(client, player_name, total_games)
        
        if not games:
            print("❌ No games retrieved!")
            return
        
        print(f"\n🎯 Processing {len(games)} complete games...")
        
        # Process each game to extract all positions
        all_positions = []
        
        for i, game in enumerate(games, 1):
            print(f"\n📋 Processing game {i}/{len(games)}: vs {game.get('opponent', 'Unknown')}")
            
            pgn_moves = game.get('pgn_moves', '')
            if not pgn_moves:
                print(f"⚠️ No moves found for game {i}")
                continue
            
            # Extract all positions from this game
            game_positions = parse_pgn_to_positions(pgn_moves, player_name, game)
            all_positions.extend(game_positions)
            
            print(f"   ✅ Extracted {len(game_positions)} positions")
        
        if not all_positions:
            print("❌ No positions extracted from any games!")
            return
        
        # Generate output filename based on player
        output_filename = f"{player_name.lower().replace(' ', '_')}_full_positions.json"
        
        # Save to file
        save_positions_to_file(all_positions, output_filename)
        
        print(f"\n🎉 SUCCESS! Generated comprehensive database for {player_name}")
        print(f"📁 Output file: {output_filename}")
        print(f"🎯 Total positions: {len(all_positions):,}")
        print(f"📊 Average positions per game: {len(all_positions) // len(games)}")
        print(f"\n💡 Next step: Copy {output_filename} to your Android app's assets folder!")
        
    except Exception as e:
        print(f"❌ Fatal error: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()