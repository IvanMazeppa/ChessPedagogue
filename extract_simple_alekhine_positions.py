#!/usr/bin/env python3
"""
🏆 Simple Alekhine Position Extractor (No Dependencies)

Extracts genuine validation positions from Alekhine.pgn using basic PGN parsing.
This provides authentic game data for reliable personality validation without external dependencies.

Input: game_collections_full/Alekhine.pgn (1,661 historical games)
Output: alekhine_authentic_validation_positions.json
"""

import json
import re
from datetime import datetime
from typing import List, Dict, Optional

def parse_pgn_headers(pgn_text: str) -> Dict[str, str]:
    """Extract PGN headers from game text"""
    headers = {}
    header_pattern = r'\[(\w+)\s+"([^"]+)"\]'
    
    for match in re.finditer(header_pattern, pgn_text):
        key, value = match.groups()
        headers[key] = value
    
    return headers

def extract_moves_from_pgn(pgn_text: str) -> List[str]:
    """Extract move sequence from PGN game"""
    # Remove headers
    moves_section = re.sub(r'\[.*?\]\n*', '', pgn_text)
    
    # Remove comments and annotations
    moves_section = re.sub(r'\{[^}]*\}', '', moves_section)
    moves_section = re.sub(r'\([^)]*\)', '', moves_section)
    
    # Remove result and extra whitespace
    moves_section = re.sub(r'\s*(1-0|0-1|1/2-1/2|\*)\s*$', '', moves_section)
    
    # Extract moves using regex
    move_pattern = r'\d+\.+\s*([KQRBN]?[a-h]?[1-8]?x?[a-h][1-8](?:=[QRBN])?[+#]?|O-O(?:-O)?)'
    moves = re.findall(move_pattern, moves_section)
    
    return moves

def is_interesting_position(move_number: int, move: str, opponent: str) -> bool:
    """Determine if a position is worth extracting for validation"""
    
    # Focus on middle game (moves 15-35)
    if not (15 <= move_number <= 35):
        return False
    
    # Prioritize famous opponents
    famous_opponents = [
        "Capablanca", "Lasker", "Nimzowitsch", "Botvinnik", "Euwe", 
        "Reti", "Rubinstein", "Tartakower", "Marshall", "Keres"
    ]
    
    is_famous = any(name in opponent for name in famous_opponents)
    
    # Interesting move patterns
    is_capture = 'x' in move
    is_check = '+' in move or '#' in move
    is_castle = 'O-O' in move
    is_major_piece = move.startswith(('Q', 'R', 'B', 'N'))
    
    # Extract if it's a famous opponent OR has interesting tactical elements
    return is_famous or is_capture or is_check or is_major_piece

def create_strategic_context(move: str, move_number: int, opponent: str) -> str:
    """Generate strategic context for the move"""
    
    contexts = []
    
    if 'x' in move:
        contexts.append("tactical capture")
    if '+' in move:
        contexts.append("forcing check")
    if '#' in move:
        contexts.append("decisive checkmate")
    if 'O-O' in move:
        contexts.append("king safety")
    if move.startswith('Q'):
        contexts.append("queen activity")
    elif move.startswith('R'):
        contexts.append("rook deployment")
    elif move.startswith('B'):
        contexts.append("bishop coordination")
    elif move.startswith('N'):
        contexts.append("knight maneuver")
    
    if not contexts:
        contexts.append("positional refinement")
    
    famous_note = ""
    famous_opponents = ["Capablanca", "Lasker", "Nimzowitsch", "Botvinnik", "Euwe"]
    if any(name in opponent for name in famous_opponents):
        famous_note = f" against the legendary {opponent}"
    
    return f"Alekhine demonstrates {', '.join(contexts)} in move {move_number}{famous_note}, showcasing his dynamic playing style."

def identify_tactical_elements(move: str) -> List[str]:
    """Identify tactical themes in the move"""
    
    elements = []
    
    if 'x' in move:
        elements.append("capture")
    if '+' in move:
        elements.append("check")
    if '#' in move:
        elements.append("checkmate")
    if 'O-O-O' in move:
        elements.append("queenside_castling")
    elif 'O-O' in move:
        elements.append("kingside_castling")
    if move.startswith('Q'):
        elements.append("queen_activity")
    elif move.startswith('R'):
        elements.append("rook_activity")
    elif move.startswith('B'):
        elements.append("bishop_activity")
    elif move.startswith('N'):
        elements.append("knight_maneuver")
    elif re.match(r'^[a-h][1-8]', move):
        elements.append("pawn_advance")
    
    if not elements:
        elements.append("positional")
    
    return elements

def extract_alekhine_positions(pgn_file_path: str, max_positions: int = 50) -> Dict:
    """Extract authentic positions from Alekhine PGN collection"""
    
    positions = []
    games_processed = 0
    positions_extracted = 0
    
    print(f"🔍 Analyzing {pgn_file_path} for authentic Alekhine positions...")
    
    with open(pgn_file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()
    
    # Split into individual games
    games = re.split(r'\n\s*\n(?=\[Event)', content)
    
    for game_text in games:
        if not game_text.strip() or positions_extracted >= max_positions:
            break
            
        try:
            # Parse headers
            headers = parse_pgn_headers(game_text)
            
            if not headers:
                continue
                
            games_processed += 1
            
            white = headers.get("White", "")
            black = headers.get("Black", "")
            result = headers.get("Result", "*")
            event = headers.get("Event", "")
            date = headers.get("Date", "")
            
            # Check if Alekhine is playing
            if "Alekhine" in white:
                alekhine_color = "white"
                opponent = black
            elif "Alekhine" in black:
                alekhine_color = "black" 
                opponent = white
            else:
                continue
            
            # Extract moves
            moves = extract_moves_from_pgn(game_text)
            
            if len(moves) < 20:  # Skip very short games
                continue
            
            # Look for interesting positions where Alekhine is to move
            for i, move in enumerate(moves):
                move_number = (i // 2) + 1
                is_white_move = (i % 2) == 0
                
                # Check if it's Alekhine's turn
                is_alekhine_turn = (alekhine_color == "white" and is_white_move) or \
                                 (alekhine_color == "black" and not is_white_move)
                
                if is_alekhine_turn and is_interesting_position(move_number, move, opponent):
                    
                    # Build PGN context (previous 10 moves or so)
                    context_start = max(0, i - 10)
                    context_moves = moves[context_start:i]
                    
                    pgn_context = []
                    for j, ctx_move in enumerate(context_moves):
                        ctx_move_num = ((context_start + j) // 2) + 1
                        is_white_ctx = ((context_start + j) % 2) == 0
                        
                        if is_white_ctx:
                            pgn_context.append(f"{ctx_move_num}.{ctx_move}")
                        else:
                            pgn_context.append(ctx_move)
                    
                    full_pgn_context = " ".join(pgn_context)
                    
                    # Create position entry
                    position = {
                        "game_info": {
                            "white_player": white,
                            "black_player": black,
                            "alekhine_color": alekhine_color,
                            "opponent": opponent,
                            "event": event,
                            "date": date,
                            "result": result,
                            "significance": f"Historical game - move {move_number}"
                        },
                        "full_pgn_context": full_pgn_context,
                        "move_number": move_number,
                        "alekhine_move_san": move,
                        "alekhine_annotation": f"Historical move by Alekhine in actual game against {opponent}",
                        "strategic_context": create_strategic_context(move, move_number, opponent),
                        "tactical_elements": identify_tactical_elements(move),
                        "game_phase": "middlegame",
                        "validation_type": "authentic_historical_pgn_simple"
                    }
                    
                    positions.append(position)
                    positions_extracted += 1
                    
                    print(f"✅ Position {positions_extracted}: {white} vs {black} (move {move_number}) - {move}")
                    
                    # Check if we have enough positions
                    if positions_extracted >= max_positions:
                        break
            
            # Progress report
            if games_processed % 200 == 0:
                print(f"📊 Processed {games_processed} games, extracted {positions_extracted} positions")
                
        except Exception as e:
            print(f"⚠️ Error processing game {games_processed}: {e}")
            continue
    
    # Create output structure
    output = {
        "metadata": {
            "created_at": datetime.now().isoformat(),
            "source_file": pgn_file_path,
            "total_games_analyzed": games_processed,
            "total_positions_extracted": positions_extracted,
            "format": "authentic_alekhine_validation",
            "purpose": "AI personality validation with genuine historical data",
            "description": "Positions extracted from authentic Alekhine PGN collection using simple parsing",
            "validation_approach": "Historical game context + authentic move analysis",
            "note": "Simplified extraction without chess engine - focuses on move patterns and context"
        },
        "positions": positions
    }
    
    print(f"\n🏆 Extraction Complete!")
    print(f"📈 Analyzed {games_processed} games from authentic Alekhine collection")
    print(f"🎯 Extracted {positions_extracted} authentic validation positions")
    print(f"💡 Focus on famous opponents and tactical positions in middle game")
    
    return output

if __name__ == "__main__":
    # Extract positions from the authentic Alekhine collection
    pgn_file = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/game_collections_full/Alekhine.pgn"
    output_file = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/alekhine_authentic_validation_positions.json"
    
    print("🏆 Authentic Alekhine Position Extraction (Simple)")
    print("=" * 55)
    
    # Extract high-quality positions for validation
    validation_data = extract_alekhine_positions(pgn_file, max_positions=50)
    
    # Save to JSON file
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(validation_data, f, indent=2, ensure_ascii=False)
    
    print(f"💾 Saved authentic validation positions to: {output_file}")
    print(f"🎯 Ready for integration with Kotlin validation framework!")
    print(f"📋 Contains genuine historical data from {validation_data['metadata']['total_games_analyzed']} Alekhine games")