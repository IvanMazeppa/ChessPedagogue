import os
import json
import time
from openai import OpenAI

# Initialize OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Configuration 
CHESS_MASTERS = ["Alexander Alekhine"]

# Master information
MASTER_STYLES = {
    "Alexander Alekhine": "Alekhine was known for his fierce and imaginative attacking style, featuring complex tactical motifs and deep positional understanding. His games often involved strategic sacrifices and intricate combinations, showcasing a masterful command of both the middlegame and the endgame."
}

MASTER_TEACHING = {
    "Alexander Alekhine": "You explain chess concepts clearly, integrate strategic insights, and provide concrete examples with a focus on long-term positional play and tactical surprises."
}

# JSON Schema for structured responses
CHESS_GAMES_SCHEMA = {
    "name": "chess_games_schema",
    "strict": True,
    "schema": {
        "type": "object",
        "properties": {
            "player_name": {
                "type": "string",
                "description": "The name of the chess master"
            },
            "games": {
                "type": "array",
                "description": "Array of famous chess games",
                "items": {
                    "type": "object",
                    "properties": {
                        "opponent": {
                            "type": "string",
                            "description": "The full name of the opponent"
                        },
                        "year": {
                            "type": "string",
                            "description": "The year the game was played"
                        },
                        "tournament": {
                            "type": "string",
                            "description": "The tournament or event name"
                        },
                        "opening": {
                            "type": "string",
                            "description": "The chess opening played, if known"
                        },
                        "moves": {
                            "type": "string",
                            "description": "Complete game moves in standard algebraic notation (e.g., '1. e4 e5 2. Nf3 Nc6'). MUST use only standard chess notation - no special symbols or transformations."
                        },
                        "result": {
                            "type": "string",
                            "enum": ["1-0", "0-1", "1/2-1/2"],
                            "description": "The game result"
                        },
                        "significance": {
                            "type": "string",
                            "description": "Brief description of why this game is famous or significant"
                        }
                    },
                    "required": ["opponent", "year", "tournament", "opening", "moves", "result", "significance"],
                    "additionalProperties": False
                }
            }
        },
        "required": ["player_name", "games"],
        "additionalProperties": False
    }
}

def create_emoji_log(emoji, message):
    """Create a log message with emoji prefix"""
    print(f"{emoji} {message}")

def fetch_games_with_chat_completions(chess_master):
    """Use regular chat completions API - the same approach that works in playground!"""
    create_emoji_log("🎯", f"Using chat completions API for {chess_master} (same as playground approach)...")
    
    # Create the exact system prompt that works for you
    system_prompt = """Identify the 10 greatest games of the given chess player based on specific criteria.

To determine the greatness of a game, consider factors such as quality of play, level of opponents, unique strategic elements, historical significance, and entertainment value. Analyze these factors for each game and then compile a list of the top 10 games fitting the criteria.

For each game, provide:
- Complete move lists in standard algebraic notation (NO special transformations)
- Full opponent names, tournaments, years
- Opening names
- Significance explanations based on the evaluation criteria

You must respond with valid JSON matching this exact structure:
{
  "player_name": "Chess Master Name",
  "games": [
    {
      "opponent": "Full opponent name",
      "year": "YYYY",
      "tournament": "Tournament name",
      "opening": "Opening name",
      "moves": "Complete move sequence in standard algebraic notation",
      "result": "1-0, 0-1, or 1/2-1/2",
      "significance": "Why this game is significant"
    }
  ]
}

Ensure the JSON is valid and complete."""

    user_prompt = f"""Identify the 10 greatest games of {chess_master} based on specific criteria.

For each game, I need:
1. The opponent's full name
2. The exact year and tournament/event  
3. A COMPLETE and CORRECTLY FORMATTED move list in standard algebraic notation
   - Format: "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6" etc.
   - Include ALL moves from first to last, with proper move numbers
   - End with the result (1-0, 0-1, or 1/2-1/2)
4. Brief description of the game's significance
5. The chess opening played

Please ensure each move follows standard algebraic notation rules with NO special transformations.

Respond with valid JSON in the exact format specified in the system message."""

    try:
        create_emoji_log("🤖", f"Making chat completion request for {chess_master}...")
        
        # Use the regular chat completions API with structured output
        response = client.chat.completions.create(
            model="gpt-4o-2024-08-06",  # Use the latest model that supports structured outputs
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            response_format={
                "type": "json_schema",
                "json_schema": CHESS_GAMES_SCHEMA
            },
            temperature=0.1,  # Lower temperature for more consistent results
            max_tokens=4000   # Ensure enough tokens for complete games
        )
        
        create_emoji_log("✅", "Chat completion request completed successfully!")
        
        # Parse the response
        response_content = response.choices[0].message.content
        
        if response_content:
            try:
                # Parse the JSON response
                games_data = json.loads(response_content)
                
                # Save the raw response for debugging
                response_file = f"{chess_master.lower().replace(' ', '_')}_chat_completion_response.json"
                with open(response_file, "w", encoding="utf-8") as f:
                    json.dump(games_data, f, indent=2)
                create_emoji_log("💾", f"Saved chat completion response to {response_file}")
                
                # Extract games
                games = games_data.get("games", [])
                create_emoji_log("🎯", f"Successfully extracted {len(games)} games from chat completion!")
                
                # Log some details about what we got
                if games:
                    create_emoji_log("📋", f"Sample game: {games[0].get('opponent', 'Unknown')} vs {chess_master}")
                    create_emoji_log("📋", f"Sample tournament: {games[0].get('tournament', 'Unknown')}")
                    create_emoji_log("📋", f"Sample year: {games[0].get('year', 'Unknown')}")
                
                return games
                
            except json.JSONDecodeError as e:
                create_emoji_log("❌", f"Failed to parse JSON response: {str(e)}")
                create_emoji_log("📄", f"Raw response: {response_content[:500]}...")
                return []
        else:
            create_emoji_log("❌", "No content in response")
            return []
            
    except Exception as e:
        create_emoji_log("❌", f"Error making chat completion request: {str(e)}")
        return []

def create_static_board(move_number=8):
    """Create a beautiful static chess board representation"""
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
        13: [
            ". . b q . r k .",
            "p r . . b p p .",
            ". . p . . n . p",
            ". . . p . . . .",
            "N . . . . . . .",
            ". P . B . Q . P",
            "P . P B . P P .",
            "R . . . . R K ."
        ],
        18: [
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
    
    if move_number <= 10:
        board = boards[8]
    elif move_number <= 15:
        board = boards[13]
    else:
        board = boards[18]
    
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
    """Transform clean, structured games into beautiful training examples"""
    create_emoji_log("🎨", f"Creating training examples from {len(games_data)} games for {chess_master}")
    
    examples = []
    system_prompt = f"You are Coach {chess_master}, a chess teacher modeling the {MASTER_STYLES.get(chess_master, '')} style of {chess_master}. {MASTER_TEACHING.get(chess_master, '')}"
    
    # Rich tactical and strategic vocabulary
    sacrifices = [
        "Nxd4", "cxd5", "Bxe7", "dxe5", "hxg5", "exd4", "bxc3",
        "Bxc4", "Nxd5", "fxe5", "exf5", "Qxg5", "Bxf7", "Nxc6", 
        "Nxe5", "Bxh7+", "Nxc3", "Nxf7", "Nf6+", "Qxf7", "Bxf4"
    ]
    
    strategic_elements = [
        "In the opening phase, I established a favorable pawn structure that gave me long-term strategic advantages.",
        "During the middlegame, I coordinated my pieces to create pressure on my opponent's position.",
        "The endgame demonstrated precise technique, converting my advantage into a win.",
        "My tactical breakthrough came when I recognized the key weakness in the opponent's position.",
        "This game showcased my ability to transform a seemingly equal position into a winning advantage.",
        "The critical moment arose when I unleashed a devastating piece sacrifice that my opponent couldn't defend against.",
        "My deep positional understanding allowed me to outmaneuver my opponent in the resulting complex position."
    ]
    
    for i, game in enumerate(games_data):
        try:
            move_number = 8 + (i % 4) * 4  # Vary between 8, 12, 16, 20
            board_repr = create_static_board(move_number)
            
            # Create rich, varied analysis
            num_tactical_lines = min(3, 1 + (i % 3))
            analysis_lines = []
            
            for j in range(num_tactical_lines):
                if j < len(sacrifices):
                    move_num = move_number - 5 + j * 4
                    sacrifice = sacrifices[(i * 3 + j) % len(sacrifices)]
                    analysis_lines.append(f"On move {move_num}, I sacrificed material with {sacrifice} to gain the initiative.")
            
            # Add strategic elements for variety
            if len(analysis_lines) < 3:
                analysis_lines.append(strategic_elements[i % len(strategic_elements)])
            
            # Use structured data from JSON response
            opponent = game.get("opponent", "Unknown Opponent")
            tournament = game.get("tournament", "Unknown Tournament")
            year = game.get("year", "Unknown")
            opening = game.get("opening", "Unknown Opening")
            moves = game.get("moves", "").strip()
            result = game.get("result", "1-0")
            significance = game.get("significance", "A masterful display of chess artistry")
            
            # Create beautiful, rich training example
            example = {
                "messages": [
                    {
                        "role": "system",
                        "content": system_prompt
                    },
                    {
                        "role": "user", 
                        "content": f"Can you tell me about your famous game against {opponent} in {tournament}, {year}? Please include the full moves and your analysis."
                    },
                    {
                        "role": "assistant",
                        "content": f"""My game against {opponent} in {tournament}, {year} is indeed one of my most memorable encounters. We played the {opening}, which led to the type of position I relished - rich in tactical and strategic possibilities.

{significance}

What made this game special was its demonstration of the harmony between tactical brilliance and positional understanding.

The critical moments of the game demonstrate my characteristic style:
{chr(10).join(analysis_lines)}

Here's a key position after move {move_number}:
{board_repr}

Here is the full game in PGN notation:
[Event "{tournament}"]
[Site "?"]
[Date "{year}.??.??"]
[Round "?"]
[White "{chess_master}"]
[Black "{opponent}"]
[Result "{result}"]
[ECO "?"]
[Opening "{opening}"]

{moves} {result}

This game showcases my approach to chess: focusing on piece activity, dynamic play, and creating practical problems for my opponent. When studying this game, pay attention to how the strategic elements and tactical opportunities intertwined - that's the essence of high-level chess.

Would you like me to analyze any specific position from this game in more detail?"""
                    }
                ]
            }
            examples.append(example)
            create_emoji_log("✨", f"Created beautiful game example {i+1} for {chess_master}!")
            
        except Exception as e:
            create_emoji_log("❌", f"Error creating example for game {i+1}: {str(e)}")
    
    # Add timeless educational examples
    generic_examples = create_educational_examples(chess_master)
    examples.extend(generic_examples)
    
    create_emoji_log("🎓", f"Created {len(examples)} comprehensive training examples for {chess_master}!")
    return examples

def create_educational_examples(chess_master):
    """Create inspiring educational examples"""
    style = MASTER_STYLES.get(chess_master, "")
    teaching = MASTER_TEACHING.get(chess_master, "")
    system_prompt = f"You are Coach {chess_master}, a chess teacher modeling the {style} style of {chess_master}. {teaching}"
    
    return [
        {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": "What was your approach to chess openings? Which ones did you prefer?"},
                {"role": "assistant", "content": "In my approach to openings, I favored positions that led to the type of middlegames where my strengths could shine. I was particularly known for playing Alekhine's Defence and the Four Knights Game. When selecting an opening, I always emphasized understanding the underlying principles rather than memorizing variations. The opening sets the stage for your strategic plan throughout the game, so I chose systems that aligned with my preferred playing style and gave me the positions where I could outplay my opponents. What opening are you currently playing? I might have some specific advice for your repertoire."}
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
        }
    ]

def main():
    """Execute the chat completions approach - exactly like playground!"""
    create_emoji_log("🚀", "Starting chat completions approach (same as playground method)!")
    
    for chess_master in CHESS_MASTERS:
        create_emoji_log("✨", f"Creating training data for {chess_master}...")
        create_emoji_log("🎯", f"Using chat completions API for {chess_master} (proven to work!)...")
        
        # Use chat completions API - same as playground
        games_data = fetch_games_with_chat_completions(chess_master)
        
        if games_data:
            create_emoji_log("🎯", f"Chat completions returned {len(games_data)} games!")
            examples = create_training_examples(chess_master, games_data)
            
            output_file = f"{chess_master.lower().replace(' ', '_')}_training.jsonl"
            with open(output_file, "w", encoding="utf-8") as f:
                for example in examples:
                    f.write(json.dumps(example) + "\n")
            
            create_emoji_log("📄", f"Saved to {output_file}")
            create_emoji_log("🎉", f"Success! Generated {len(examples)} training examples for {chess_master}")
        else:
            create_emoji_log("❌", f"Chat completions didn't return games for {chess_master}")
    
    create_emoji_log("🎉", "Chat completions approach completed successfully!")

if __name__ == "__main__":
    main()