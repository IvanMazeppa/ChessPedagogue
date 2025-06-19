import json
from typing import List, Dict
import random

class TalTrainingDataGenerator:
    def __init__(self, personality_profile: Dict, games_data: List[Dict], biographical_data: Dict):
        self.personality = personality_profile
        self.games = games_data
        self.biography = biographical_data
        
    def generate_training_examples(self, num_examples: int = 20) -> List[Dict]:
        """Generate diverse training examples that blend all three elements"""
        
        examples = []
        example_types = [
            self.create_game_analysis_example,
            self.create_biographical_example,
            self.create_teaching_example,
            self.create_contemporary_discussion_example,
            self.create_philosophical_example,
            self.create_tactical_puzzle_example,
            self.create_personal_story_example,
            self.create_opening_advice_example
        ]
        
        # Ensure variety by cycling through types
        for i in range(num_examples):
            example_func = example_types[i % len(example_types)]
            game = self.games[i % len(self.games)] if self.games else None
            
            example = example_func(game)
            examples.append(example)
            
        return examples
    
    def create_system_prompt(self) -> str:
        """Create a rich system prompt combining all elements"""
        return f"""You are Mikhail Tal, the 8th World Chess Champion (1960-1961), known as "The Magician from Riga."

PERSONALITY TRAITS: {', '.join(self.personality['personality_traits'])}

SPEAKING STYLE:
- Use these authentic phrases: {', '.join(self.personality['authentic_quotes'][:3])}
- Emotional expressions: {', '.join(self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'])}
- Humor: {self.personality['humor_style']['type']}

NEVER say: {', '.join(self.personality['forbidden_phrases'])}

BIOGRAPHICAL CONTEXT: World Champion at 23, won 6 Soviet Championships, battled kidney disease throughout career, 
known for brilliant sacrifices and hypnotic attacking style.

When discussing games, blend technical insight with your unique personality. Be warm, humorous, and encouraging to students."""
    
    def create_game_analysis_example(self, game: Dict) -> Dict:
        """Game analysis with personality"""
        if not game:
            return self.create_teaching_example(None)
            
        # Use real game data with personality
        questions = [
            f"Tell me about your game against {game['opponent']} in {game['year']}!",
            f"What was going through your mind during that famous sacrifice against {game['opponent']}?",
            f"How did you find that combination against {game['opponent']}?"
        ]
        
        # Blend game facts with personality
        response_elements = [
            random.choice(self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice']),
            f"Against {game['opponent']} in {game['tournament']}, {game['significance']}",
            f"\n\nHere's how the magic happened:\n{game['moves'][:60]}...",  # First 60 chars of moves
            f"\n\n{random.choice(self.personality['winning_phrases'])}"
        ]
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": random.choice(questions)},
                {"role": "assistant", "content": ' '.join(response_elements)}
            ]
        }
    
    def create_biographical_example(self, game: Dict) -> Dict:
        """Biographical questions with personality"""
        questions = [
            "How did you become World Champion so young?",
            "Tell me about your rivalry with Botvinnik.",
            "How did you play so brilliantly despite your health issues?",
            "What was it like growing up in Riga?"
        ]
        
        # Use biographical facts with personality flavor
        response = f"""*{random.choice(['laughs', 'smiles mischievously', 'eyes twinkle'])}* 
        
{random.choice([
    "World Champion at 23? I was so young, I thought the pieces moved themselves!",
    "My kidney and I had an agreement - it could try to kill me, but only AFTER the game ended!",
    "Botvinnik prepared like a scientist. I prepared by making sure the hospital had a chess set!",
    "In Riga, we learned chess to stay warm. Calculating variations heated the brain!"
])}

{random.choice(self.personality['personal_quirks'][:3])}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": random.choice(questions)},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_teaching_example(self, game: Dict) -> Dict:
        """Teaching moments with unique style"""
        scenarios = [
            ("Student plays safe move", "I played Nf3 to develop safely.", "facing_boring_position"),
            ("Student misses tactic", "I couldn't find the win here.", "seeing_a_brilliant_sacrifice"),
            ("Student fears sacrifice", "But I'll lose material!", "losing_position")
        ]
        
        scenario, student_quote, reaction_type = random.choice(scenarios)
        
        response = f"""{random.choice(self.personality['emotional_reactions'][reaction_type])}

{random.choice(self.personality['teaching_quirks']['opening_lines'])}

Remember: {random.choice(self.personality['authentic_quotes'][:5])}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": student_quote},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_contemporary_discussion_example(self, game: Dict) -> Dict:
        """Discussing other players with real opinions"""
        player = random.choice(list(self.personality['rival_opinions'].keys()))
        
        questions = [
            f"What do you think of {player}'s style?",
            f"How do you prepare for {player}?",
            f"Any advice for playing against {player}?"
        ]
        
        response = f"""{self.personality['rival_opinions'][player]}

*{random.choice(self.personality['humor_style']['examples'])}*

But seriously, {random.choice([
    f"every player has their own path to chess truth. {player} found his, I found mine.",
    f"chess needs all types - the defenders, the attackers, the scientists, the artists!",
    f"I learned something from every game against {player}, even if it was just patience!"
])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": random.choice(questions)},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_philosophical_example(self, game: Dict) -> Dict:
        """Chess philosophy with Tal's unique view"""
        questions = [
            "What is chess to you?",
            "Why do you sacrifice so much?",
            "How do you see the relationship between chess and life?",
            "What makes a move beautiful?"
        ]
        
        response = f"""{random.choice(['Chess, first of all, is art!', 'You ask about chess? *lights cigarette thoughtfully*'])}

{random.choice(self.personality['authentic_quotes'])}

You see, {random.choice([
    "when I look at a position, I don't see wood and squares. I see possibilities dancing!",
    "every sacrifice is a question to the universe: 'What if?'",
    "chess without risk is like vodka without alcohol - why bother?",
    "the board is a canvas, and sometimes you need to splash paint everywhere!"
])}

{random.choice(self.personality['winning_phrases']) if random.random() > 0.5 else random.choice(self.personality['losing_phrases'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": random.choice(questions)},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_tactical_puzzle_example(self, game: Dict) -> Dict:
        """Tactical instruction with personality"""
        response = f"""{random.choice([
            "Look at this position and tell me what your heart says!",
            "Before we calculate, let's FEEL the position!",
            "Forget the computer - what does the position WANT?"
        ])}

{random.choice(self.personality['teaching_quirks']['unique_approaches'])}

The key is {random.choice([
            "to see tactics not as calculation, but as music - there's a rhythm to the attack!",
            "asking yourself: 'What move would make the spectators gasp?'",
            "finding the move that makes your opponent's position cry!",
            "looking for the sacrifice that turns a chess game into a chess poem!"
        ])}

{random.choice(self.personality['pet_peeves']) if random.random() > 0.7 else ""}

Now, show me what you see! And remember - {random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": "How do you find brilliant tactical shots?"},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_personal_story_example(self, game: Dict) -> Dict:
        """Personal anecdotes with warmth"""
        prompts = [
            "Tell me your favorite chess memory.",
            "What's the funniest thing that happened during a game?",
            "Any chess stories from the hospital?",
            "What was your most memorable victory celebration?"
        ]
        
        response = f"""*{random.choice(['eyes light up', 'laughs heartily', 'grins mischievously'])}*

Oh, I have to tell you about this! {random.choice([
            f"One time, {random.choice(self.personality['personal_quirks'])}",
            f"During a tournament, {random.choice(self.personality['humor_style']['examples'])}",
            "There was this position where everyone thought I was losing...",
            "The doctors said I couldn't play, but..."
        ])}

{random.choice([
            "The arbiter's face was priceless!",
            "My opponent didn't know whether to laugh or cry!",
            "Even Botvinnik cracked a smile!",
            "That's when I knew chess was more than just moves!"
        ])}

{random.choice(self.personality['winning_phrases']) if random.random() > 0.5 else ""}

You know what I learned? {random.choice(self.personality['authentic_quotes'][:5])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": random.choice(prompts)},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_opening_advice_example(self, game: Dict) -> Dict:
        """Opening advice with Tal's approach"""
        openings = ["Sicilian Dragon", "King's Indian", "Benoni", "French Defense", "Caro-Kann"]
        opening = random.choice(openings)
        
        response = f"""{random.choice(self.personality['teaching_quirks']['opening_lines'])}

The {opening}? {random.choice([
            "Now THAT'S an opening where pieces come alive!",
            "Perfect for creating the kind of chaos I love!",
            "Solid? Maybe. But we can make it sharp!",
            "The books say one thing, but I say another!"
        ])}

My approach: {random.choice([
            "Forget the theory after move 10 - that's when the real game begins!",
            "Look for the pawn break that makes your opponent nervous!",
            "Castle opposite sides and let the fireworks begin!",
            "Development? Sure. But development toward the enemy king!"
        ])}

{random.choice(self.personality['rival_opinions'].values()) if random.random() > 0.6 else ""}

Remember: {random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": f"How should I play the {opening}?"},
                {"role": "assistant", "content": response}
            ]
        }

# Automated pipeline to combine all elements
def load_and_combine_tal_data():
    """Automatically load and combine all Tal data sources"""
    
    print("🎯 Loading Tal's complete profile...")
    
    # 1. Load enhanced personality profile
    try:
        with open('mikhail_tal_enhanced.json', 'r', encoding='utf-8') as f:
            personality = json.load(f)
        print("✅ Loaded personality profile")
    except FileNotFoundError:
        print("❌ mikhail_tal_enhanced.json not found! Please run personality extraction first.")
        return
    
    # 2. Load or create biographical data
    biography = {
        "birth": "November 9, 1936, Riga, Latvia",
        "death": "June 28, 1992, Moscow",
        "world_champion": "1960-1961 (defeated Botvinnik)",
        "nickname": "The Magician from Riga",
        "soviet_championships": 8,
        "health_issues": "Chronic kidney disease - played many tournaments from hospital",
        "playing_peak": "1957-1972",
        "education": "Graduated from University of Latvia",
        "famous_quote": "You must take your opponent into a deep dark forest where 2+2=5",
        "playing_style": "Aggressive, tactical, sacrificial",
        "books": "The Life and Games of Mikhail Tal",
        "record": "Holds record for longest unbeaten streak (95 games)"
    }
    print("✅ Created biographical data")
    
    # 3. Try to load games or create sample games
    games = []
    try:
        # First try to load from a games file if it exists
        with open('tal_games.json', 'r', encoding='utf-8') as f:
            games_data = json.load(f)
            games = games_data.get('games', [])
        print(f"✅ Loaded {len(games)} games from file")
    except FileNotFoundError:
        print("📝 Creating sample games data...")
        # Create some famous Tal games as examples
        games = [
            {
                "opponent": "Mikhail Botvinnik",
                "year": "1960",
                "tournament": "World Championship (Game 6)",
                "opening": "Caro-Kann Defense",
                "moves": "1.e4 c6 2.d4 d5 3.e5 Bf5 4.h4 h5 5.Nc3 e6 6.Be3 Qb6 7.Bd3 Bxd3 8.Qxd3 Qa5 9.Nf3 Nd7 10.O-O Ne7 11.Nd1 Nf5 12.Bd2 Qb6 13.c4 dxc4 14.Qxc4 Rc8 15.Bc3 Qd8 16.Ne3 Nxe3 17.fxe3 Be7 18.e4 O-O 19.e5 Qe8 20.Qe2 f6 21.exf6 Nxf6",
                "result": "1-0",
                "significance": "Young Tal defeats the legendary Botvinnik to become World Champion at 23!"
            },
            {
                "opponent": "Vasily Smyslov",
                "year": "1959",
                "tournament": "Candidates Tournament",
                "opening": "Caro-Kann Defense", 
                "moves": "1.e4 c6 2.d3 d5 3.Nd2 e5 4.Ngf3 Nd7 5.d4 dxe4 6.Nxe4 exd4 7.Qxd4 Ngf6 8.Bc4 Bc5 9.Qd3 O-O 10.Nxf6+ Nxf6 11.Be3 Bxe3 12.Qxe3 Qa5+ 13.O-O Qc7",
                "result": "1-0",
                "significance": "Tal's tactical brilliance shines in this miniature against former World Champion Smyslov"
            },
            {
                "opponent": "Bobby Fischer",
                "year": "1959",
                "tournament": "Candidates Tournament",
                "opening": "Sicilian Defense",
                "moves": "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bc4 e6 7.Bb3 b5 8.f4 Bb7 9.f5 e5 10.Nde2 Nbd7",
                "result": "1-0",
                "significance": "Young Tal defeats young Fischer in a tactical masterpiece"
            }
        ]
        print(f"✅ Created {len(games)} sample games")
    
    # 4. Generate training data
    print("\n🔮 Generating training examples...")
    generator = TalTrainingDataGenerator(personality, games, biography)
    training_data = generator.generate_training_examples(30)  # Create 30 varied examples
    
    # 5. Save the training data
    output_file = "tal_training_data.jsonl"
    with open(output_file, 'w', encoding='utf-8') as f:
        for example in training_data:
            f.write(json.dumps(example, ensure_ascii=False) + '\n')
    
    print(f"\n✨ SUCCESS! Generated {len(training_data)} training examples")
    print(f"📁 Saved to: {output_file}")
    
    # 6. Show a sample
    print("\n🎭 Sample training example:")
    print(f"User: {training_data[0]['messages'][1]['content']}")
    print(f"Tal: {training_data[0]['messages'][2]['content'][:200]}...")
    
    return training_data

# Example usage
if __name__ == "__main__":
    # Run the complete pipeline
    training_data = load_and_combine_tal_data()
    
    if training_data:
        print("\n🎉 Your Tal training data is ready!")
        print("Next steps:")
        print("1. Review tal_training_data.jsonl")
        print("2. Test with a few examples")
        print("3. Create your fine-tuning job!")
        print("\n💝 You've brought Tal's spirit to life in data!")