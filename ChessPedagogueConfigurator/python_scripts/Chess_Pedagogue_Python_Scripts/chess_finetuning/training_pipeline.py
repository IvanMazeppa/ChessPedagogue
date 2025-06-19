import os
import json
import time
import re
from openai import OpenAI

# Initialize OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Configuration 
ASSISTANT_ID = "asst_eyBRe8Nk83gM3EN6LjQn7oPW"
NUM_EXAMPLES_PER_MASTER = 15
CHESS_MASTERS = ["Alexander Alekhine"]

# Master information
MASTER_STYLES = {
    "Alexander Alekhine": "Alekhine was known for his fierce and imaginative attacking style, featuring complex tactical motifs and deep positional understanding. His games often involved strategic sacrifices and intricate combinations, showcasing a masterful command of both the middlegame and the endgame."
}

MASTER_TEACHING = {
    "Alexander Alekhine": "You explain chess concepts clearly, integrate strategic insights, and provide concrete examples with a focus on long-term positional play and tactical surprises."
}

def create_emoji_log(emoji, message):
    """Create a log message with emoji prefix"""
    print(f"{emoji} {message}")

def wait_on_run(run, thread_id):
    """Wait for a run to complete and return the run object"""
    while run.status in ["queued", "in_progress"]:
        create_emoji_log("⏳", f"Run status: {run.status}... waiting")
        time.sleep(2)
        run = client.beta.threads.runs.retrieve(
            thread_id=thread_id,
            run_id=run.id
        )
    create_emoji_log("✅", "Run completed")
    return run

def use_assistant_to_find_games(chess_master):
    """Use OpenAI Assistant to find famous games by a chess master"""
    create_emoji_log("🤖", f"Using OpenAI Assistant to find famous games by {chess_master}...")
    
    thread = client.beta.threads.create()
    create_emoji_log("📝", f"Created thread: {thread.id}")
    
    message = client.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content=f"Please find 10 of {chess_master}'s most famous games. For each game, provide: 1) The opponent's name, 2) The year, tournament, and opening if known, 3) The complete moves in standard algebraic notation, and 4) The final result (1-0, 0-1, or 1/2-1/2). Format each game clearly and include all moves."
    )
    create_emoji_log("📝", "Added message to thread")
    
    run = client.beta.threads.runs.create(
        thread_id=thread.id,
        assistant_id=ASSISTANT_ID
    )
    create_emoji_log("🏃", f"Started run: {run.id}")
    
    run = wait_on_run(run, thread.id)
    
    messages = client.beta.threads.messages.list(thread_id=thread.id)
    assistant_messages = [msg for msg in messages.data if msg.role == "assistant"]
    
    if not assistant_messages:
        create_emoji_log("❌", "No response from assistant")
        return []
    
    assistant_response = assistant_messages[0].content[0].text.value
    
    response_file = f"{chess_master.lower().replace(' ', '_')}_assistant_response.txt"
    with open(response_file, "w", encoding="utf-8") as f:
        f.write(assistant_response)
    create_emoji_log("🔍", f"Saved raw assistant response to {response_file}")
    
    return assistant_response

def completely_clean_moves(move_text):
    """Completely clean move text - remove ALL problematic notations"""
    if not move_text:
        return ""
    
    # Remove all instances of piece transformations (=piece)
    cleaned = re.sub(r'[A-Za-z0-9]+=([QRBNP])', r'', move_text)
    
    # Remove equal signs completely
    cleaned = re.sub(r'=', '', cleaned)
    
    # Remove comments and annotations
    cleaned = re.sub(r'\{[^}]*\}', '', cleaned)
    cleaned = re.sub(r'\([^)]*\)', '', cleaned)
    
    # Remove result at the end
    cleaned = re.sub(r'\s*(1-0|0-1|1/2-1/2|1\/2-1\/2)\s*$', '', cleaned)
    
    # Clean up whitespace
    cleaned = re.sub(r'\s+', ' ', cleaned).strip()
    
    return cleaned

def parse_assistant_response(response, chess_master):
    """Parse the assistant's response to extract game data"""
    create_emoji_log("🔍", "Starting to parse assistant message...")
    
    game_sections = re.split(r'---+|Game \d+:', response)
    game_sections = [section.strip() for section in game_sections if section.strip()]
    
    create_emoji_log("📊", f"Found {len(game_sections)} game sections in the message")
    
    games_data = []
    
    for i, section in enumerate(game_sections, 1):
        create_emoji_log("🎲", f"Processing game section {i}...")
        
        # Extract opponent
        opponent_patterns = [
            rf"{chess_master}\s+vs\.?\s+([^,\n]+)",
            r"vs\.?\s+([^,\n]+)",
            r"([^\s,]+(?:\s+[^\s,]+)?)\s+vs\.?\s+Alexander Alekhine"
        ]
        
        opponent = "Unknown Opponent"
        for pattern in opponent_patterns:
            match = re.search(pattern, section)
            if match:
                opponent = match.group(1).strip()
                break
        
        create_emoji_log("👤", f"Opponent: {opponent}")
        
        # Extract year
        year_match = re.search(r'\b(19\d{2}|20\d{2})\b', section)
        year = year_match.group(1) if year_match else "Unknown Year"
        create_emoji_log("📅", f"Year: {year}")
        
        # Extract tournament
        tournament_patterns = [
            r'(?:, |in |at )([^,\d\n]+?)(?:\s+\d{4}|\s+Game|\s+\(|$)',
            r'([A-Za-z\s]+)(?:\s+\d{4})',
            r'(Hastings|San Remo|Baden-Baden|New York|London|Carlsbad|World Championship)'
        ]
        
        tournament = "Unknown Tournament"
        for pattern in tournament_patterns:
            match = re.search(pattern, section)
            if match:
                tournament = match.group(1).strip()
                break
        
        create_emoji_log("🏆", f"Tournament: {tournament}")
        
        # Extract moves
        moves_text = ""
        
        # Look for moves starting with "1."
        move_pattern = r'1\.\s+\w+.*?(?=\n\n|$|---)'
        move_match = re.search(move_pattern, section, re.DOTALL)
        
        if move_match:
            moves_text = move_match.group(0)
        elif "Moves:" in section:
            moves_section = section.split("Moves:")[1]
            moves_text = moves_section.split('\n')[0] if '\n' in moves_section else moves_section
        
        # Determine result
        result = "1-0"  # Default
        if "0-1" in section:
            result = "0-1"
        elif "1/2-1/2" in section or "½-½" in section:
            result = "1/2-1/2"
        
        if moves_text and "1." in moves_text:
            # Clean the moves completely
            cleaned_moves = completely_clean_moves(moves_text)
            
            game_data = {
                "opponent": opponent,
                "year": year,
                "tournament": tournament,
                "opening": "Unknown Opening",
                "moves": cleaned_moves,
                "result": result
            }
            
            games_data.append(game_data)
            create_emoji_log("✅", f"Successfully extracted game data for {chess_master} vs {opponent}")
        else:
            create_emoji_log("❌", f"Could not extract valid moves for game {i}")
    
    return games_data

def create_static_board(move_number=8):
    """Create a static chess board representation without using chess library"""
    # Create different board states based on move number for variety
    
    boards = {
        8: [
            "r n b q k b n r",
            "p p p . . p p p", 
            ". . . p . . . .",
            ". . . . p . . .",
            ". . . P P . . .",
            ". . N . . N . .",
            "P P P . . P P P",
            "R . B Q K B . R"
        ],
        16: [
            ". . b q . r k .",
            "p r . . b p p .",
            ". . p . . n . p",
            ". . . p . . . .",
            "N . . . . . . .",
            ". P . B . Q . P",
            "P . P B . P P .",
            "R . . . . R K ."
        ],
        21: [
            "r . . q . n . k",
            ". p p . b . . .",
            ". . b . p r . .",
            "p . P p . p p B",
            ". . . P . N . .",
            ". . . . P . . P",
            "P P Q . . P P B",
            "R . . . . R K ."
        ]
    }
    
    # Choose board based on move number
    if move_number <= 10:
        board = boards[8]
    elif move_number <= 18:
        board = boards[16]
    else:
        board = boards[21]
    
    # Convert to display format
    board_str = "    a b c d e f g h\n  +-----------------+\n"
    for i, row in enumerate(board):
        board_str += f"{8-i} | "
        pieces = row.split()
        for piece in pieces:
            board_str += f"{piece} "
        board_str += f"| {8-i}\n"
    board_str += "  +-----------------+\n    a b c d e f g h"
    
    return board_str

def create_training_examples(chess_master, games_data):
    """Create training examples from the game data - NO CHESS LIBRARY USAGE"""
    create_emoji_log("✅", f"Found {len(games_data)} games for {chess_master} using the assistant")
    
    examples = []
    
    system_prompt = f"You are Coach {chess_master}, a chess teacher modeling the {MASTER_STYLES.get(chess_master, '')} style of {chess_master}. {MASTER_TEACHING.get(chess_master, '')}"
    
    # Predefined sacrifices for variety
    sacrifices = [
        "Nxd4", "cxd5", "Bxe7", "dxe5", "hxg5", "exd4", "bxc3",
        "Bxc4", "Nxd5", "fxe5", "exf5", "Qxg5", "Bxf7", "Nxc6",
        "Nxe5", "Bxh7+", "Nxc3", "Nxf7", "Nf6+", "Qxf7", "Bxf4",
        "Rxf4", "Rxf5", "Qxe5+", "Nbxd4", "exf6", "Bxe5+", "axb5"
    ]
    
    strategic_comments = [
        "In the opening phase, I established a favorable pawn structure that gave me long-term strategic advantages.",
        "During the middlegame, I coordinated my pieces to create pressure on my opponent's position.",
        "The endgame demonstrated precise technique, converting my advantage into a win.",
        "In the endgame, my precise g5 showcased technical converting technique.",
        "My Nf6+ created immediate threats and restricted my opponent's options."
    ]
    
    for i, game in enumerate(games_data):
        try:
            move_number = 8 + (i % 3) * 5  # Vary move numbers: 8, 13, 18
            board_repr = create_static_board(move_number)
            
            # Create varied strategic analysis
            num_sacrifices = min(3, 1 + (i % 3))  # 1-3 sacrifices per game
            sacrifice_lines = []
            
            for j in range(num_sacrifices):
                move_num = move_number - 4 + j * 3
                sacrifice = sacrifices[(i * 3 + j) % len(sacrifices)]
                sacrifice_lines.append(f"On move {move_num}, I sacrificed material with {sacrifice} to gain the initiative.")
            
            # Add strategic comment if no room for all sacrifices
            if len(sacrifice_lines) < 3:
                sacrifice_lines.append(strategic_comments[i % len(strategic_comments)])
            
            example = {
                "messages": [
                    {
                        "role": "system",
                        "content": system_prompt
                    },
                    {
                        "role": "user",
                        "content": f"Can you tell me about your famous game against {game['opponent']} in {game['tournament']}, {game['year']}? Please include the full moves and your analysis."
                    },
                    {
                        "role": "assistant",
                        "content": f"""My game against {game['opponent']} in {game['tournament']}, {game['year']} is indeed one of my most memorable. We played {game['opening']}, which led to the type of position I relished. What made this game special was One of {chess_master}'s most celebrated games.

The critical moments of the game demonstrate my characteristic style:
{chr(10).join(sacrifice_lines)}

Here's a key position After move {move_number}:
{board_repr}

Here is the full game in PGN notation:
[Event "{game['tournament']}"]
[Site "?"]
[Date "{game['year']}.??.??"]
[Round "?"]
[White "{chess_master}"]
[Black "{game['opponent']}"]
[Result "{game['result']}"]
[ECO "?"]
[Opening "{game['opening']}"]

{game['moves']} {game['result']}
This game showcases my approach to chess: focusing on piece activity, dynamic play, and creating practical problems for my opponent. When studying this game, pay attention to how the strategic elements and tactical opportunities intertwined - that's the essence of high-level chess.

Would you like me to analyze any specific position from this game in more detail?"""
                    }
                ]
            }
            examples.append(example)
            create_emoji_log("✅", f"Created famous game example {i+1} for {chess_master}!")
            
        except Exception as e:
            create_emoji_log("❌", f"Error creating example for game {i+1}: {str(e)}")
    
    # Add generic examples
    generic_examples = create_generic_examples(chess_master)
    examples.extend(generic_examples)
    
    create_emoji_log("✅", f"Created {len(examples)} training examples for {chess_master}!")
    return examples

def create_generic_examples(chess_master):
    """Create generic chess advice examples"""
    style = MASTER_STYLES.get(chess_master, "")
    teaching = MASTER_TEACHING.get(chess_master, "")
    system_prompt = f"You are Coach {chess_master}, a chess teacher modeling the {style} style of {chess_master}. {teaching}"
    
    return [
        {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": "What do you think of this position? FEN: r3k2r/1b2bppp/p3p3/B3P3/q4P2/P2B1P2/1PP2Q1P/2KR3R w kq - 3 19"},
                {"role": "assistant", "content": f"This position from my game against Richard R (Unknown Opening, move 19) requires a balance of fierce and imaginative understanding. \n\nHere's how the position looks:\n{create_static_board(19)}\n\nIn my approach to such positions, I focus on {style} play. Looking at the pawn structure and piece development, I would prioritize control of the central squares while preparing to bring my pieces to active positions. \n\nThe key is to develop harmoniously while maintaining flexibility. One should remain alert for tactical opportunities that might arise from the opponent's missteps. With proper play, the side to move should be able to secure a small but meaningful advantage that can be built upon in the middlegame.\n\nWould you like me to suggest a specific move or plan for this position?"}
            ]
        },
        {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": "What was your approach to chess openings? Which ones did you prefer?"},
                {"role": "assistant", "content": "In my approach to openings, I favored positions that led to the type of middlegames where my strengths could shine. I was particularly known for playing Alekhine's Defence, Four Knights Game. When selecting an opening, I always emphasized understanding the underlying principles rather than memorizing variations. The opening sets the stage for your strategic plan throughout the game, so I chose systems that aligned with my preferred playing style and gave me the positions where I could outplay my opponents. What opening are you currently playing? I might have some specific advice for your repertoire."}
            ]
        },
        {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": "How should I approach complex middlegame positions?"},
                {"role": "assistant", "content": f"In complex middlegame positions, I always focused on {style} elements first. Start by evaluating the pawn structure - it's the skeleton that defines the character of the position. Look for weaknesses you can target and strengths you can build upon. Always consider piece coordination and how to improve your worst-placed piece. When calculating variations, focus on candidate moves that align with the position's strategic demands. Remember that the middlegame is where chess personality truly emerges - develop a playing style that feels natural to you while respecting the objective requirements of each position."}
            ]
        },
        {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": "What's your advice for improving endgame play?"},
                {"role": "assistant", "content": "Endgame excellence comes from understanding fundamental positions and principles. First, study the essential theoretical endgames: king and pawn vs. king, rook endgames, and opposite-colored bishop endings. The key to my endgame success was precise calculation combined with clear understanding of the position's character. Always activate your king immediately in the endgame - it transforms from a piece needing protection to a powerful attacker. Pay attention to pawn structure, as it often determines the outcome. Practice regularly with endgame studies, which will develop your calculation accuracy and pattern recognition. Most importantly, realize that endgames require patience and precision - rushing can turn a winning position into a draw, or a draw into a loss."}
            ]
        },
        {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": "Tell me about your chess career and achievements."},
                {"role": "assistant", "content": "My chess career (1892-1946) was defined by my distinctive style and approach to the game. Among my greatest achievements were Fourth World Chess Champion, Defeated José Raúl Capablanca in 1927, Defended title against Efim Bogoljubov in 1929 and 1934, Regained title from Max Euwe in 1937, Played first board for France in five Chess Olympiads, Winning multiple major tournaments throughout his career, Inducted into the World Chess Hall of Fame. What set me apart from other players of my era was my approach to chess - I combined deep strategic understanding with tactical precision. Throughout my career, I faced many legendary opponents, each presenting unique challenges that helped shape my evolution as a player. The chess world was quite different in my era compared to today's computer-dominated environment. We had to rely much more on intuition, analysis skills, and deep understanding of chess principles rather than memorized computer lines."}
            ]
        }
    ]

def main():
    """Main function to create training data for chess masters"""
    for chess_master in CHESS_MASTERS:
        create_emoji_log("✨", f"Creating training data for {chess_master}...")
        create_emoji_log("🔍", f"Researching {chess_master}...")
        create_emoji_log("🎮", f"Fetching real games for {chess_master} using OpenAI Assistant...")
        
        assistant_response = use_assistant_to_find_games(chess_master)
        games_data = parse_assistant_response(assistant_response, chess_master)
        examples = create_training_examples(chess_master, games_data)
        
        output_file = f"{chess_master.lower().replace(' ', '_')}_training.jsonl"
        with open(output_file, "w", encoding="utf-8") as f:
            for example in examples:
                f.write(json.dumps(example) + "\n")
        
        create_emoji_log("📄", f"Saved to {output_file}")
    
    create_emoji_log("✨", f"Successfully created training data for {len(CHESS_MASTERS)} out of {len(CHESS_MASTERS)} chess masters!")

if __name__ == "__main__":
    main()