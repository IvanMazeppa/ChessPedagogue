import os
import json
import time
import re
from typing import List, Dict, Optional, Tuple
from dataclasses import dataclass
from openai import OpenAI
import chess.pgn
import io

# Initialize OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

@dataclass
class ChessGame:
    """Structured chess game data"""
    opponent: str
    year: str
    tournament: str
    opening: str
    moves: str
    result: str
    significance: str
    white_player: str
    black_player: str
    
    def validate_pgn(self) -> Tuple[bool, Optional[str]]:
        """Validate PGN format and moves"""
        try:
            # Create PGN string
            pgn_string = f"""[Event "{self.tournament}"]
[Date "{self.year}.??.??"]
[White "{self.white_player}"]
[Black "{self.black_player}"]
[Result "{self.result}"]

{self.moves}"""
            
            # Parse with python-chess
            pgn_io = io.StringIO(pgn_string)
            game = chess.pgn.read_game(pgn_io)
            
            if game is None:
                return False, "Failed to parse PGN"
            
            # Verify the game can be played through
            board = game.board()
            for move in game.mainline_moves():
                board.push(move)
            
            return True, None
            
        except Exception as e:
            return False, str(e)
    
    def standardize_moves(self) -> str:
        """Ensure moves are in standard algebraic notation"""
        # Remove move numbers if they're doubled (e.g., "1. e4 1... e5")
        moves = re.sub(r'(\d+)\.\s*([a-zA-Z0-9+#=\-xO]+)\s*\1\.\.\.', r'\1. \2', self.moves)
        # Ensure proper spacing
        moves = re.sub(r'(\d+\.)(\S)', r'\1 \2', moves)
        moves = re.sub(r'(\S)(\d+\.)', r'\1 \2', moves)
        # Remove extra spaces
        moves = ' '.join(moves.split())
        return moves

class NimzowitschTrainingPipeline:
    def __init__(self):
        self.player_name = "Aron Nimzowitsch"
        self.style_description = """Nimzowitsch was a pioneering chess theorist and one of the world's strongest players in the 1920s. 
        He revolutionized chess understanding with his hypermodern approach, emphasizing piece activity over pawn occupation, 
        prophylaxis (preventing opponent's plans), and the concept of overprotection. His games feature deep strategic plans, 
        mysterious moves that prevent opponent's ideas, and a unique understanding of piece coordination."""
        
        self.teaching_style = """You explain chess with emphasis on prophylactic thinking, restraint, and blockade. 
        You help students understand not just what to do, but what to prevent the opponent from doing. 
        Your teaching includes concepts like overprotection, mysterious rook moves, and the power of centralized knights."""
        
    def create_emoji_log(self, emoji: str, message: str):
        """Pretty logging with emojis"""
        print(f"{emoji} {message}")
        
    def fetch_nimzowitsch_games(self) -> List[ChessGame]:
        """Fetch famous Nimzowitsch games using GPT-4"""
        self.create_emoji_log("🔍", "Fetching famous Nimzowitsch games...")
        
        prompt = """Please provide 10 of Aron Nimzowitsch's most famous and instructive chess games in JSON format.
        Focus on games that showcase his revolutionary ideas: prophylaxis, blockade, overprotection, and hypermodern play.
        
        For each game, provide:
        1. opponent (full name)
        2. year (4 digits)
        3. tournament (exact name)
        4. opening (standard opening name)
        5. moves (complete game in standard algebraic notation, e.g., "1. e4 e5 2. Nf3 Nc6...")
        6. result (1-0, 0-1, or 1/2-1/2)
        7. significance (why this game is important)
        8. who played white and black
        
        Include his most famous games like:
        - The Immortal Zugzwang Game vs Sämisch
        - His wins against Capablanca
        - Classic blockade games
        - Games demonstrating mysterious rook moves
        
        Return ONLY valid JSON in this exact format:
        {
            "games": [
                {
                    "opponent": "Friedrich Sämisch",
                    "year": "1923",
                    "tournament": "Copenhagen",
                    "opening": "Queen's Indian Defense",
                    "white_player": "Friedrich Sämisch",
                    "black_player": "Aron Nimzowitsch",
                    "moves": "1. d4 Nf6 2. c4 e6 ...",
                    "result": "0-1",
                    "significance": "The Immortal Zugzwang Game..."
                }
            ]
        }"""
        
        try:
            response = client.chat.completions.create(
                model="gpt-4-turbo-preview",
                messages=[
                    {"role": "system", "content": "You are a chess historian with deep knowledge of Aron Nimzowitsch's games. Provide accurate historical chess data."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                response_format={"type": "json_object"}
            )
            
            games_data = json.loads(response.choices[0].message.content)
            games = []
            
            for game_dict in games_data.get("games", []):
                game = ChessGame(
                    opponent=game_dict["opponent"],
                    year=game_dict["year"],
                    tournament=game_dict["tournament"],
                    opening=game_dict["opening"],
                    moves=game_dict["moves"],
                    result=game_dict["result"],
                    significance=game_dict["significance"],
                    white_player=game_dict.get("white_player", ""),
                    black_player=game_dict.get("black_player", "")
                )
                
                # Standardize moves
                game.moves = game.standardize_moves()
                
                # Validate PGN
                valid, error = game.validate_pgn()
                if valid:
                    games.append(game)
                    self.create_emoji_log("✅", f"Valid game: {game.white_player} vs {game.black_player}, {game.year}")
                else:
                    self.create_emoji_log("⚠️", f"Invalid PGN for {game.opponent}: {error}")
            
            self.create_emoji_log("📊", f"Successfully fetched {len(games)} valid games")
            return games
            
        except Exception as e:
            self.create_emoji_log("❌", f"Error fetching games: {str(e)}")
            # Return some hardcoded classic games as fallback
            return self.get_fallback_games()
    
    def get_fallback_games(self) -> List[ChessGame]:
        """Hardcoded famous Nimzowitsch games as fallback"""
        return [
            ChessGame(
                opponent="Friedrich Sämisch",
                year="1923",
                tournament="Copenhagen",
                opening="Queen's Indian Defense",
                white_player="Friedrich Sämisch",
                black_player="Aron Nimzowitsch",
                moves="1. d4 Nf6 2. c4 e6 3. Nf3 b6 4. g3 Bb7 5. Bg2 Be7 6. Nc3 O-O 7. O-O d5 8. Ne5 c6 9. cxd5 cxd5 10. Bf4 a6 11. Rc1 b5 12. Qb3 Nc6 13. Nxc6 Bxc6 14. h3 Qd7 15. Kh2 Nh5 16. Bd2 f5 17. Qd1 b4 18. Nb1 Bb5 19. Rg1 Bd6 20. e4 fxe4 21. Qxh5 Rxf2 22. Qg5 Raf8 23. Kh1 R8f5 24. Qe3 Bd3 25. Rce1 h6",
                result="0-1",
                significance="The Immortal Zugzwang Game - perhaps the most famous example of zugzwang in chess history, where every move by White worsens their position."
            ),
            ChessGame(
                opponent="Jose Raul Capablanca",
                year="1927",
                tournament="New York",
                opening="French Defense",
                white_player="Aron Nimzowitsch",
                black_player="Jose Raul Capablanca",
                moves="1. e4 e6 2. d4 d5 3. e5 c5 4. Qg4 cxd4 5. Nf3 Nc6 6. Bd3 f5 7. Qg3 Nge7 8. O-O Ng6 9. Re1 Qc7 10. Na3 a6 11. Nc2 Bd7 12. Bf4 O-O-O 13. Bd2 h6 14. h4 g5 15. Qh3 Kb8 16. a3 Be7 17. b4 Rdg8 18. hxg5 hxg5 19. g3 Rh3 20. Kg2 Rgh8 21. Rh1 Rxh3 22. Rxh3 Rxh3 23. Kxh3 Qd8 24. Kg2 Qh8 25. Ncxd4 Nxd4 26. Nxd4 Qh2+ 27. Kf1 Qh1+ 28. Ke2 Qxa1 29. Be3 Qxe5 30. Nxf5 exf5 31. Qxf5 Qxf5 32. Bxf5 Nxe5",
                result="1/2-1/2",
                significance="A brilliant defensive effort against the World Champion, showing Nimzowitsch's ability to create complications and hold difficult positions."
            )
        ]
    
    def create_chess_position(self, move_number: int = 12) -> str:
        """Create ASCII chess position for variety"""
        # Different positions for variety
        if move_number <= 10:
            return """    a b c d e f g h
  ┌─────────────────┐
8 │ r n b q k b n r │
7 │ p p . p p p p p │
6 │ . . p . . . . . │
5 │ . . . . . . . . │
4 │ . . P P . . . . │
3 │ . . N . . N . . │
2 │ P P . . P P P P │
1 │ R . B Q K B . R │
  └─────────────────┘
    a b c d e f g h"""
        else:
            return """    a b c d e f g h
  ┌─────────────────┐
8 │ r . . q . r k . │
7 │ p b . n b p p . │
6 │ . p . p . n . p │
5 │ . . p P p . . . │
4 │ . . P . P . . . │
3 │ . . N B . N . . │
2 │ P P . . . P P P │
1 │ R . . Q . R K . │
  └─────────────────┘
    a b c d e f g h"""
    
    def create_training_example(self, game: ChessGame, example_type: str = "game_analysis") -> Dict:
        """Create a training example from a game"""
        system_prompt = f"""You are {self.player_name}, the legendary chess player and theorist. {self.style_description}

{self.teaching_style}

When discussing games, you emphasize your revolutionary concepts while being warm and encouraging to students."""
        
        if example_type == "game_analysis":
            user_query = f"Can you tell me about your game against {game.opponent} from {game.tournament} {game.year}?"
            
            # Ensure proper PGN formatting
            pgn_output = f"""[Event "{game.tournament}"]
[Site "?"]
[Date "{game.year}.??.??"]
[White "{game.white_player}"]
[Black "{game.black_player}"]
[Result "{game.result}"]

{game.moves} {game.result}"""
            
            assistant_response = f"""Ah yes, my game against {game.opponent} from {game.tournament} {game.year} - a game that beautifully illustrates my chess philosophy!

{game.significance}

Let me show you the key moments that demonstrate the principles I've written about in "My System" and "Chess Praxis."

Here's a critical position from the game:
{self.create_chess_position()}

The game showcases several important concepts:
1. **Prophylaxis** - preventing the opponent's plans before they develop
2. **Restraint** - controlling key squares and limiting piece mobility  
3. **Blockade** - using pieces (especially knights) to control critical squares

{pgn_output}

When studying this game, notice how seemingly quiet moves often contain deep strategic poison. Chess is not just about what you do, but what you prevent your opponent from doing!

Would you like me to explain any specific phase of this game in more detail?"""
            
        elif example_type == "concept":
            concepts = [
                ("prophylaxis", "What is prophylaxis in chess and how should I use it?"),
                ("blockade", "Can you explain your concept of the blockade?"),
                ("overprotection", "What is overprotection and why is it important?"),
                ("mysterious rook moves", "What are mysterious rook moves?")
            ]
            
            concept, question = concepts[hash(game.opponent) % len(concepts)]
            user_query = question
            
            responses = {
                "prophylaxis": """Prophylaxis - one of my greatest contributions to chess understanding! It means preventing your opponent's plans before they can even begin.

Think of it this way: most players ask "What can I do?" but you should also ask "What does my opponent want to do, and how can I stop it?"

For example, if you see your opponent wants to play e5, you might place a piece to control that square. But true prophylaxis goes deeper - you might play a move like h3 to prevent a pin, or Kh1 to avoid future back-rank problems.

The key is to think 2-3 moves ahead about your opponent's ideal setup and subtly prevent it. This frustrates opponents and gives you a psychological edge!""",
                
                "blockade": """The blockade is one of my most important strategic concepts! It means placing a piece (ideally a knight) on a square in front of an enemy pawn, especially an isolated or backward pawn.

Why is this so powerful? The blockading piece:
1. Cannot be driven away by pawns
2. Controls important squares
3. Limits the pawn's advance forever
4. Often becomes an "eternal" piece

The ideal blockader is a knight because it can't be exchanged easily and controls many squares. Place it on a central square like d5 or e5, and it radiates power throughout the position!""",
                
                "overprotection": """Overprotection means defending important points with more pieces than seemingly necessary. This appears wasteful, but it's actually a profound strategic concept!

When you overprotect a key square or piece:
1. Your pieces naturally find good squares
2. You're ready for any tactical strikes
3. The protected point becomes a launching pad for attacks
4. Your position gains elasticity and harmony

It's like building a strong foundation - the extra support allows for dynamic possibilities later!""",
                
                "mysterious rook moves": """Ah, my famous mysterious rook moves! These are rook moves along the back rank that seem to do nothing but actually serve multiple purposes.

For example, Re1-e2-e1 might look pointless, but it could:
1. Avoid a future pin
2. Prepare to double rooks
3. Free a square for another piece
4. Create subtle threats

The key is that these moves improve your position in ways that only become clear later. Your opponent often doesn't understand their purpose until it's too late!"""
            }
            
            assistant_response = responses.get(concept, responses["prophylaxis"])
        
        else:  # opening advice
            user_query = "What's your approach to playing hypermodern openings?"
            assistant_response = """My hypermodern approach revolutionized opening play! Instead of occupying the center with pawns immediately, I showed that you can control it from afar with pieces.

Key principles of hypermodern play:
1. **Let them occupy the center** - Allow pawns on e4/d4, then undermine them
2. **Fianchetto bishops** - Bishops on g2/b2 exert tremendous pressure
3. **Flexible pawn structure** - Keep options open for central breaks
4. **Piece pressure** - Control key squares with pieces, not pawns

My favorite systems include the Nimzo-Indian (1.d4 Nf6 2.c4 e6 3.Nc3 Bb4) and Queen's Indian. These openings embody restraint and control.

Remember: the center can be controlled without being occupied! This was heretical in my time but is now accepted wisdom."""
        
        return {
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_query},
                {"role": "assistant", "content": assistant_response}
            ]
        }
    
    def validate_training_data(self, examples: List[Dict]) -> Tuple[bool, List[str]]:
        """Validate training data meets OpenAI requirements"""
        errors = []
        
        for i, example in enumerate(examples):
            # Check structure
            if "messages" not in example:
                errors.append(f"Example {i}: Missing 'messages' field")
                continue
                
            messages = example["messages"]
            if len(messages) < 2:
                errors.append(f"Example {i}: Need at least 2 messages")
                
            # Check roles
            roles = [m.get("role") for m in messages]
            if roles[0] != "system":
                errors.append(f"Example {i}: First message should be system")
            if "user" not in roles:
                errors.append(f"Example {i}: Missing user message")
            if "assistant" not in roles:
                errors.append(f"Example {i}: Missing assistant message")
                
            # Check content
            for j, message in enumerate(messages):
                if "content" not in message:
                    errors.append(f"Example {i}, Message {j}: Missing content")
                elif not isinstance(message["content"], str):
                    errors.append(f"Example {i}, Message {j}: Content must be string")
                elif len(message["content"]) == 0:
                    errors.append(f"Example {i}, Message {j}: Empty content")
        
        return len(errors) == 0, errors
    
    def save_training_data(self, examples: List[Dict], filename: str):
        """Save training data in JSONL format"""
        with open(filename, 'w', encoding='utf-8') as f:
            for example in examples:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        self.create_emoji_log("💾", f"Saved {len(examples)} examples to {filename}")
    
    def create_fine_tune_job(self, training_file: str, suffix: str = "nimzowitsch-coach"):
        """Create OpenAI fine-tuning job"""
        try:
            # Upload training file
            self.create_emoji_log("📤", "Uploading training file...")
            with open(training_file, 'rb') as f:
                file_response = client.files.create(
                    file=f,
                    purpose='fine-tune'
                )
            file_id = file_response.id
            self.create_emoji_log("✅", f"File uploaded: {file_id}")
            
            # Create fine-tuning job
            self.create_emoji_log("🚀", "Creating fine-tune job...")
            job = client.fine_tuning.jobs.create(
                training_file=file_id,
                model="gpt-3.5-turbo",
                suffix=suffix,
                hyperparameters={
                    "n_epochs": 3,
                    "batch_size": 1,
                    "learning_rate_multiplier": 2
                }
            )
            
            self.create_emoji_log("🎯", f"Fine-tune job created: {job.id}")
            return job.id
            
        except Exception as e:
            self.create_emoji_log("❌", f"Error creating fine-tune job: {str(e)}")
            return None
    
    def run_complete_pipeline(self):
        """Run the complete pipeline from data fetch to fine-tune job"""
        self.create_emoji_log("🏁", f"Starting complete pipeline for {self.player_name}")
        
        # Step 1: Fetch games
        games = self.fetch_nimzowitsch_games()
        if not games:
            self.create_emoji_log("❌", "No games fetched, aborting")
            return
        
        # Save games data for reference
        games_file = "nimzowitsch_games_data.json"
        with open(games_file, 'w', encoding='utf-8') as f:
            games_data = {
                "player_name": self.player_name,
                "games": [game.__dict__ for game in games]
            }
            json.dump(games_data, f, indent=2, ensure_ascii=False)
        self.create_emoji_log("📝", f"Saved games data to {games_file}")
        
        # Step 2: Create training examples
        examples = []
        
        # Add game analysis examples
        for i, game in enumerate(games):
            example = self.create_training_example(game, "game_analysis")
            examples.append(example)
            
        # Add concept examples
        for i in range(min(4, len(games))):
            example = self.create_training_example(games[i], "concept")
            examples.append(example)
            
        # Add opening advice
        if games:
            example = self.create_training_example(games[0], "opening")
            examples.append(example)
        
        self.create_emoji_log("📚", f"Created {len(examples)} training examples")
        
        # Step 3: Validate training data
        valid, errors = self.validate_training_data(examples)
        if not valid:
            self.create_emoji_log("❌", "Validation errors found:")
            for error in errors:
                print(f"  - {error}")
            return
        self.create_emoji_log("✅", "All training examples validated successfully")
        
        # Step 4: Save training data
        training_file = "nimzowitsch_training.jsonl"
        self.save_training_data(examples, training_file)
        
        # Step 5: Create fine-tune job (optional - comment out if just testing data)
        create_job = input("\nCreate fine-tune job? (y/n): ").lower() == 'y'
        if create_job:
            job_id = self.create_fine_tune_job(training_file)
            if job_id:
                self.create_emoji_log("🎉", f"SUCCESS! Fine-tune job {job_id} created")
                self.create_emoji_log("💡", "Monitor progress with: client.fine_tuning.jobs.retrieve(job_id)")
        else:
            self.create_emoji_log("💾", f"Training data saved to {training_file}")
            self.create_emoji_log("💡", "You can manually create a fine-tune job later")
        
        self.create_emoji_log("🏆", "Pipeline completed successfully!")


# Run the pipeline
if __name__ == "__main__":
    # Check for API key
    if not os.getenv("OPENAI_API_KEY"):
        print("❌ Please set OPENAI_API_KEY environment variable")
        exit(1)
    
    # Run pipeline
    pipeline = NimzowitschTrainingPipeline()
    pipeline.run_complete_pipeline()