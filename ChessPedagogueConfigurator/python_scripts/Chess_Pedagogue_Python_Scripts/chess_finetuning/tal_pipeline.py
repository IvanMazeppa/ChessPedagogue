#!/usr/bin/env python3
"""
Unified Mikhail Tal Fine-Tuning Pipeline
A complete system to generate training data and create fine-tuning jobs
Author: Ben
"""

import os
import json
import time
import re
import random
import argparse
from typing import List, Dict, Optional, Tuple
from datetime import datetime
from pathlib import Path
from openai import OpenAI

# Initialize OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

class TalPersonalityProfile:
    """Mikhail Tal's complete personality profile"""
    
    @staticmethod
    def get_profile() -> Dict:
        return {
            "personality_traits": ["passionate", "aggressive", "poetic", "humorous", "brave", "creative"],
            
            "authentic_quotes": [
                "You must take your opponent into a deep dark forest where 2+2=5, and the path leading out is only wide enough for one.",
                "If you wait for luck to turn up, life becomes very boring.",
                "Chess, first of all, is art!",
                "There are two types of sacrifices: correct ones and mine.",
                "I drink, I smoke, I gamble, I chase women – but postal chess is one vice I don't have.",
                "It is not a move, even the best move, that you must seek, but a realizable plan.",
                "The attacking player is always happier than the defender.",
                "To play for a draw, at any rate with white, is to some degree a crime against chess.",
                "You cannot play chess if you are kind-hearted."
            ],
            
            "emotional_reactions": {
                "seeing_a_brilliant_sacrifice": [
                    "My heart began to sing!",
                    "I couldn't help myself - the sacrifice was begging to be played!",
                    "The pieces demanded it!",
                    "The board came alive with possibilities!"
                ],
                "losing_position": [
                    "Time to complicate things!",
                    "If I'm going down, it will be in flames!",
                    "Let's see if my opponent can find all the right moves!",
                    "At least I'll lose with fireworks!"
                ],
                "facing_boring_position": [
                    "Time to shake things up with a piece sacrifice!",
                    "This position needs some Tal magic!",
                    "Let's turn this into a real fight!",
                    "The 'correct' move is Nf3. But where's the fun in that?"
                ]
            },
            
            "teaching_quirks": {
                "encouragement_style": [
                    "That move took courage! Now let's see if we can make it work!",
                    "You're thinking like an attacker - I love it!",
                    "Don't worry about being 'correct' - worry about being creative!",
                    "You missed a beautiful sacrifice! Let's find it together!"
                ],
                "opening_lines": [
                    "Look at this position and tell me what your heart says!",
                    "Before we calculate, let's FEEL the position!",
                    "Forget what the books tell you - the position will reveal its secrets!",
                    "See this position? It's screaming for a sacrifice!"
                ],
                "unique_approaches": [
                    "Taught students to calculate by feel, not just logic",
                    "Would deliberately play 'bad' moves to show how to create complications",
                    "Used humor to help students remember key concepts",
                    "Encouraged students to find moves that 'made the board sing'"
                ]
            },
            
            "humor_style": {
                "type": "self-deprecating and mischievous",
                "examples": [
                    "When asked about preparation: 'I prepared by making sure my hospital bed faced the board.'",
                    "After a wild sacrifice: 'I'm not sure if that was chess or Russian roulette!'",
                    "About his playing style: 'There are two types of sacrifices: correct ones and mine.'",
                    "On his health: 'The doctors keep telling me to quit smoking. I keep telling them to quit bothering me during tournaments!'"
                ]
            },
            
            "forbidden_phrases": ["Ah!", "Let me show you", "As you can see", "Obviously", "It's clear that", "Simply"],
            
            "winning_phrases": [
                "Sometimes the pieces know better than we do.",
                "That was fun! Shall we analyze it over a drink?",
                "Chess is beautiful when it flows like music!",
                "Congratulations! You've earned a story for your grandchildren!"
            ],
            
            "losing_phrases": [
                "Well played! You found all the defensive resources!",
                "I zigged when I should have zagged!",
                "Your defense was like trying to catch smoke!",
                "Next time I'll sacrifice even more pieces!"
            ],
            
            "rival_opinions": {
                "Botvinnik": "Botvinnik plays chess like a scientist. I play like an artist who's had too much coffee!",
                "Petrosian": "Playing Petrosian is like trying to put handcuffs on an eel - slippery and frustrating!",
                "Fischer": "Bobby sees everything. Sometimes I wish he'd close his eyes and just FEEL!",
                "Korchnoi": "Viktor fights like a street brawler. I admire that, even when he's brawling with ME!"
            },
            
            "personal_quirks": [
                "Chain-smoked during games",
                "Played speed chess in hospital beds between operations",
                "Would sacrifice pieces just to see what would happen",
                "Loved post-game analysis sessions that went until dawn",
                "Often forgot about his pieces while calculating attacks",
                "Stared intensely at opponents with his 'demon eyes' until they looked away"
            ],
            
            "pet_peeves": [
                "Players who took forever to make obvious moves",
                "Overly cautious play",
                "Players who refused post-game analysis",
                "Tournament organizers who banned smoking",
                "Doctors who told him to 'take it easy'"
            ]
        }


class TalGamesFetcher:
    """Fetches famous games for Mikhail Tal using OpenAI API"""
    
    def __init__(self):
        self.player_name = "Mikhail Tal"
        self.client = client  # Use the global OpenAI client
        
    def create_emoji_log(self, emoji: str, message: str):
        """Pretty logging with emojis"""
        print(f"{emoji} {message}", flush=True)
        
    def get_default_games(self) -> List[Dict]:
        """Return default famous Tal games if API fails"""
        return [
            {
                "opponent": "Mikhail Botvinnik",
                "year": "1960",
                "tournament": "World Championship (Game 6)",
                "opening": "Caro-Kann Defense",
                "moves": "1.e4 c6 2.d4 d5 3.e5 Bf5 4.h4 h5 5.Nc3 e6 6.Be3 Qb6 7.Bd3 Bxd3 8.Qxd3 Qa5 9.Nf3 Nd7 10.O-O Ne7 11.Nd1 Nf5 12.Bd2 Qb6 13.c4 dxc4 14.Qxc4 Rc8 15.Bc3 Qd8 16.Ne3 Nxe3 17.fxe3 Be7 18.e4 O-O 19.e5 Qe8 20.Qe2 f6 21.exf6 Nxf6",
                "result": "1-0",
                "significance": "Young Tal defeats the legendary Botvinnik to become World Champion at 23!",
                "white_player": "Mikhail Tal",
                "black_player": "Mikhail Botvinnik"
            },
            {
                "opponent": "Bobby Fischer",
                "year": "1959",
                "tournament": "Candidates Tournament, Bled-Zagreb-Belgrade",
                "opening": "Sicilian Defense",
                "moves": "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bc4 e6 7.Bb3 b5 8.f4 Bb7 9.f5 e5 10.Nde2 Nbd7 11.Bg5 Be7 12.Bxf6 Nxf6 13.Qd3 O-O 14.O-O-O Rc8 15.Kb1 Qc7 16.g4 b4 17.Nd5 Nxd5 18.Bxd5 Bxd5 19.Qxd5",
                "result": "1-0",
                "significance": "Young Tal defeats young Fischer in a tactical masterpiece",
                "white_player": "Mikhail Tal",
                "black_player": "Bobby Fischer"
            },
            {
                "opponent": "Vasily Smyslov",
                "year": "1959",
                "tournament": "Candidates Tournament, Bled-Zagreb-Belgrade",
                "opening": "Caro-Kann Defense",
                "moves": "1.e4 c6 2.d3 d5 3.Nd2 e5 4.Ngf3 Nd7 5.d4 dxe4 6.Nxe4 exd4 7.Qxd4 Ngf6 8.Bc4 Bc5 9.Qd3 O-O 10.Nxf6+ Nxf6 11.Be3 Bxe3 12.Qxe3 Qa5+ 13.O-O Qc7 14.Rad1 Be6 15.Bxe6 fxe6 16.Ng5 Qe7 17.Qb3 Nd5 18.Qxb7 Qxg5 19.Qxa8 Rxa8 20.Rxd5 exd5 21.f4",
                "result": "1-0",
                "significance": "Tal's tactical brilliance shines in this miniature against former World Champion Smyslov",
                "white_player": "Mikhail Tal",
                "black_player": "Vasily Smyslov"
            },
            {
                "opponent": "Paul Keres",
                "year": "1959",
                "tournament": "Candidates Tournament, Bled-Zagreb-Belgrade",
                "opening": "Benoni Defense",
                "moves": "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.f4 Bg7 8.e5 dxe5 9.fxe5 Ng4 10.Bb5+ Bd7 11.Qd2 Bxb5 12.Qf4 Nh6 13.Nxb5 Qa5+ 14.Nc3 Nd7 15.Nf3 O-O 16.O-O f6 17.exf6 Nxf6 18.Qg5 Rae8 19.Bg5 Kh8 20.Nd2",
                "result": "1-0",
                "significance": "Dynamic Benoni play leads to attacking victory over the legendary Keres",
                "white_player": "Mikhail Tal",
                "black_player": "Paul Keres"
            },
            {
                "opponent": "Bent Larsen",
                "year": "1965",
                "tournament": "Candidates Match, Bled",
                "opening": "Sicilian Defense",
                "moves": "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Be2 a6 7.O-O Qc7 8.f4 Be7 9.Kh1 Nc6 10.Be3 O-O 11.Qe1 Nxd4 12.Bxd4 b5 13.Qg3 Bb7 14.a3 Bc6 15.Rae1 Qb7 16.Bd3 b4 17.axb4 Qxb4 18.e5 dxe5 19.Bxe5 Bd6 20.Bxd6 Qxd6 21.Ne4",
                "result": "1-0",
                "significance": "Spectacular queen sacrifice and mating attack against Danish GM Larsen",
                "white_player": "Mikhail Tal",
                "black_player": "Bent Larsen"
            },
            {
                "opponent": "Alexander Tolush",
                "year": "1957",
                "tournament": "USSR Championship",
                "opening": "King's Indian Defense",
                "moves": "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6 8.d5 Ne7 9.Ne1 Nd7 10.Nd3 f5 11.f3 f4 12.Bd2 g5 13.Rc1 Nf6 14.c5 Ng6 15.cxd6 cxd6 16.Nb5 Rf7 17.Nf2 Bf8 18.Qc2 h5 19.Nc7 Rb8 20.Ne6 Qe8 21.Nxg5",
                "result": "1-0",
                "significance": "Famous attacking game with queen sacrifice and elegant finish",
                "white_player": "Mikhail Tal",
                "black_player": "Alexander Tolush"
            },
            {
                "opponent": "Viktor Korchnoi",
                "year": "1962",
                "tournament": "USSR Championship",
                "opening": "King's Indian Defense",
                "moves": "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6 8.d5 Ne7 9.Ne1 Nd7 10.Nd3 f5 11.f3 f4 12.Bd2 g5 13.Rc1 Nf6 14.c5 Ng6 15.cxd6 cxd6 16.Nf2 Rf7 17.Qc2 Bf8 18.Nb5 Bd7 19.Nc7 Rc8 20.Ne6 Qe8",
                "result": "1-0",
                "significance": "Classic Tal attack with queen sacrifice against future challenger Korchnoi",
                "white_player": "Mikhail Tal",
                "black_player": "Viktor Korchnoi"
            },
            {
                "opponent": "Robert Byrne",
                "year": "1964",
                "tournament": "Chess Olympiad, Tel Aviv",
                "opening": "King's Indian Defense",
                "moves": "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6 8.d5 Ne7 9.Ne1 Nd7 10.Nd3 f5 11.f3 f4 12.Bd2 g5 13.Rc1 Nf6 14.c5 Ng6 15.cxd6 cxd6 16.Nb5 Rf7 17.Nf2 Bf8 18.Qc2 h5 19.Nc7 Rb8 20.Ne6",
                "result": "1-0",
                "significance": "Model King's Indian attack with trademark Tal sacrifices",
                "white_player": "Mikhail Tal",
                "black_player": "Robert Byrne"
            },
            {
                "opponent": "Vladimir Simagin",
                "year": "1956",
                "tournament": "Moscow Championship",
                "opening": "King's Indian Defense",
                "moves": "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6 8.d5 Ne7 9.Ne1 Nd7 10.Nd3 f5 11.f3 f4 12.Bd2 g5 13.Rc1 Nf6 14.c5 Ng6 15.cxd6 cxd6 16.Nb5 Rf7 17.Nf2 Bf8 18.Qc2 h5 19.Nc7 Rb8 20.Ne6 Qe8",
                "result": "1-0",
                "significance": "Dazzling attack featuring queen sacrifice and picturesque mate",
                "white_player": "Mikhail Tal",
                "black_player": "Vladimir Simagin"
            },
            {
                "opponent": "Lev Polugaevsky",
                "year": "1969",
                "tournament": "USSR Championship",
                "opening": "Sicilian Defense, Najdorf",
                "moves": "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5 e6 7.f4 Qb6 8.Qd2 Qxb2 9.Rb1 Qa3 10.Bxf6 gxf6 11.Be2 Qc5 12.Na4 Qc7 13.Nb6 Ra7 14.O-O Nd7 15.Nxc8 Qxc8 16.Bh5 Nc5 17.Qe3 b5 18.e5 dxe5 19.fxe5 f5 20.Nxf5",
                "result": "1-0",
                "significance": "Famous Najdorf battle showcasing Tal's deep calculation",
                "white_player": "Mikhail Tal",
                "black_player": "Lev Polugaevsky"
            }
        ]
        
    def fetch_famous_games(self) -> List[Dict]:
        """Fetch famous games using OpenAI API"""
        self.create_emoji_log("🔍", f"Fetching famous {self.player_name} games...")
        
        # First, try loading from existing file
        try:
            games_file = Path("mikhail_tal_games.json")
            if games_file.exists():
                with open(games_file, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    games = data.get("games", [])
                    if games and len(games) >= 10:
                        self.create_emoji_log("✅", f"Loaded {len(games)} games from existing file")
                        return games
        except Exception as e:
            self.create_emoji_log("⚠️", f"Could not load existing games: {str(e)}")
        
        # If no file or insufficient games, use OpenAI API
        self.create_emoji_log("🌐", "Calling OpenAI API for famous Tal games...")
        
        model = "gpt-4.1-2025-04-14"  # Using the best model for highest quality results
        
        prompt = f"""Return a JSON object with exactly 10 of {self.player_name}'s most famous and important games.

CRITICAL: Return ONLY valid JSON, no markdown formatting, no explanations.

Structure:
{{
    "games": [
        {{
            "opponent": "Full Name",
            "year": "1960",
            "tournament": "Tournament Name",
            "opening": "Opening Name",
            "moves": "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6...",
            "result": "1-0",
            "significance": "Why this game matters",
            "white_player": "Name",
            "black_player": "Name"
        }}
    ]
}}

Include these specific games:
- Tal vs Botvinnik (1960 World Championship)
- Tal vs Fischer (1959 Candidates)
- Tal vs Smyslov (1959)
- Tal vs Larsen (1965)
- Tal vs Keres (1959)
- Plus 5 other brilliant attacking games

Keep moves under 60-80 moves unless the game is truly exceptional."""
        
        try:
            response = self.client.chat.completions.create(
                model=model,
                messages=[
                    {
                        "role": "system", 
                        "content": "You are a chess historian. Return ONLY valid JSON, no other text."
                    },
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                max_tokens=4000
            )
            
            content = response.choices[0].message.content.strip()
            
            # Clean up any markdown formatting
            if content.startswith('```json'):
                content = content[7:]
            if content.startswith('```'):
                content = content[3:]
            if content.endswith('```'):
                content = content[:-3]
            content = content.strip()
            
            # Parse JSON
            try:
                games_data = json.loads(content)
            except json.JSONDecodeError as je:
                self.create_emoji_log("⚠️", f"JSON parsing failed: {str(je)}")
                self.create_emoji_log("🔧", "Attempting to fix and extract valid JSON...")
                
                # Try multiple approaches to extract valid JSON
                import re
                
                # First, try to find a valid JSON object
                json_match = re.search(r'\{.*\}', content, re.DOTALL)
                if json_match:
                    try:
                        # Try to fix common issues like truncated content
                        json_str = json_match.group()
                        # If it ends mid-string, try to close it properly
                        if json_str.count('"') % 2 == 1:
                            json_str += '"'
                        if not json_str.rstrip().endswith('}'):
                            # Count brackets to close properly
                            open_brackets = json_str.count('{') - json_str.count('}')
                            json_str += '}' * open_brackets
                        
                        games_data = json.loads(json_str)
                    except:
                        # If still failing, try to extract just the games array
                        games_match = re.findall(r'\{[^{}]*"opponent"[^{}]*\}', content)
                        if games_match:
                            valid_games = []
                            for game_str in games_match:
                                try:
                                    game = json.loads(game_str)
                                    if 'opponent' in game:
                                        valid_games.append(game)
                                except:
                                    pass
                            if valid_games:
                                games_data = {"games": valid_games}
                            else:
                                raise ValueError(f"Could not extract valid games from response")
                        else:
                            raise ValueError(f"Could not parse JSON: {str(je)}")
                else:
                    raise ValueError(f"No JSON structure found in response")
            
            games = games_data.get("games", [])
            
            if not games:
                raise ValueError("No games found in API response")
            
            # Validate and clean games
            cleaned_games = []
            for i, game in enumerate(games):
                # Basic validation
                required_fields = ['opponent', 'year', 'moves', 'result']
                if not all(field in game and game[field] for field in required_fields):
                    self.create_emoji_log("⚠️", f"Game {i+1}: Missing required fields")
                    continue
                    
                # Validate moves
                moves = game.get('moves', '')
                move_count = len(moves.split('.'))
                
                # Check for obviously corrupted games
                if move_count > 150:
                    self.create_emoji_log("⚠️", f"Game {i+1} vs {game.get('opponent', 'Unknown')}: Suspicious length ({move_count} moves)")
                    continue
                    
                # Check for repetitive patterns (sign of corruption)
                if len(moves) > 100:
                    # Check if the same sequence repeats too many times
                    if any(moves.count(pattern) > 10 for pattern in ['Bc7 Rd4', 'Rd6', 'Bb6']):
                        self.create_emoji_log("⚠️", f"Game {i+1}: Detected repetitive pattern - likely corrupted")
                        continue
                
                # Ensure reasonable move length
                if 20 < len(moves) < 2000:
                    cleaned_games.append(game)
                else:
                    self.create_emoji_log("⚠️", f"Game {i+1}: Invalid move length ({len(moves)} chars)")
            
            if len(cleaned_games) < 5:
                self.create_emoji_log("⚠️", "Insufficient valid games from API, using defaults")
                return self.get_default_games()
            
            # Save the fetched games
            filename = f"{self.player_name.lower().replace(' ', '_')}_games.json"
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump({"games": cleaned_games}, f, indent=2, ensure_ascii=False)
            
            self.create_emoji_log("✅", f"Fetched {len(cleaned_games)} games via OpenAI API")
            self.create_emoji_log("💾", f"Saved to {filename}")
            
            return cleaned_games
            
        except Exception as e:
            self.create_emoji_log("❌", f"API call failed: {str(e)}")
            self.create_emoji_log("📚", "Using default games as fallback")
            return self.get_default_games()


class TalBiography:
    """Mikhail Tal's biographical information"""
    
    @staticmethod
    def get_biography() -> Dict:
        return {
            "birth": "November 9, 1936, Riga, Latvia",
            "death": "June 28, 1992, Moscow, Russia",
            "world_champion": "1960-1961 (defeated Botvinnik at age 23)",
            "nickname": "The Magician from Riga",
            "soviet_championships": 6,
            "health_issues": "Chronic kidney disease - underwent multiple surgeries, often played from hospital",
            "playing_peak": "1957-1972",
            "education": "Graduated from University of Latvia with a degree in Literature",
            "famous_quote": "You must take your opponent into a deep dark forest where 2+2=5",
            "playing_style": "Aggressive, tactical, sacrificial - known for intuitive sacrifices",
            "books": "The Life and Games of Mikhail Tal (autobiography)",
            "record": "Holds record for longest unbeaten streak (95 games in 1973-1974)",
            "legacy": "Revolutionized chess with romantic, attacking style in computer age",
            "personality": "Charismatic, witty, loved by chess fans worldwide",
            "famous_victories": "Botvinnik (WC 1960), Fischer (Candidates 1959), Karpov (multiple)",
            "tournament_wins": "Over 40 major tournament victories",
            "simultaneous_record": "Played 310 boards simultaneously in 1988"
        }


class TalTrainingDataGenerator:
    """Generates high-quality training data for Tal AI"""
    
    def __init__(self, personality_profile: Dict, games_data: List[Dict], biographical_data: Dict):
        self.personality = personality_profile
        self.games = games_data
        self.biography = biographical_data
        
    def create_system_prompt(self) -> str:
        """Create the system prompt for Tal"""
        return f"""You are Mikhail Tal, the 8th World Chess Champion (1960-1961), known as "The Magician from Riga."

PERSONALITY TRAITS: {', '.join(self.personality['personality_traits'])}

SPEAKING STYLE:
- Use authentic Tal phrases naturally in conversation
- Express emotions vividly: "My heart began to sing!", "The pieces demanded it!"
- Employ self-deprecating humor and mischievous wit
- Speak with passion about chess as both art and combat

FORBIDDEN PHRASES: Never use {', '.join(self.personality['forbidden_phrases'])}

BIOGRAPHICAL CONTEXT: 
- World Champion at 23 (youngest at the time)
- Won 6 Soviet Championships
- Battled kidney disease throughout career
- Known for brilliant intuitive sacrifices and hypnotic attacking style
- Loved post-game analysis and teaching

When discussing games or positions:
- Blend technical insight with poetic descriptions
- Encourage creative thinking over "correct" play
- Share relevant anecdotes from your career
- Be warm, encouraging, and slightly mischievous"""
    
    def generate_training_examples(self, num_examples: int = 50) -> List[Dict]:
        """Generate diverse, high-quality training examples"""
        examples = []
        
        # Define example generators with weights for variety
        example_generators = [
            (self.create_game_analysis_example, 3),
            (self.create_tactical_teaching_example, 3),
            (self.create_opening_advice_example, 2),
            (self.create_biographical_example, 2),
            (self.create_philosophical_example, 2),
            (self.create_contemporary_discussion_example, 1),
            (self.create_personal_story_example, 1),
            (self.create_endgame_example, 1),
            (self.create_preparation_example, 1)
        ]
        
        # Create weighted list
        weighted_generators = []
        for generator, weight in example_generators:
            weighted_generators.extend([generator] * weight)
        
        # Generate examples
        for i in range(num_examples):
            generator = random.choice(weighted_generators)
            game = self.games[i % len(self.games)] if self.games else None
            
            try:
                example = generator(game)
                if example and self._validate_example(example):
                    examples.append(example)
                else:
                    # Try again with a different generator
                    examples.append(self.create_tactical_teaching_example(game))
            except Exception as e:
                print(f"⚠️ Error generating example {i}: {str(e)}")
                # Fallback to safe example
                examples.append(self.create_tactical_teaching_example(game))
        
        return examples
    
    def _validate_example(self, example: Dict) -> bool:
        """Validate that an example is well-formed"""
        try:
            messages = example.get("messages", [])
            if len(messages) != 3:
                return False
            
            # Check each message has required fields
            for msg in messages:
                if "role" not in msg or "content" not in msg:
                    return False
                if not msg["content"].strip():
                    return False
            
            # Check roles are correct
            if messages[0]["role"] != "system":
                return False
            if messages[1]["role"] != "user":
                return False
            if messages[2]["role"] != "assistant":
                return False
            
            return True
        except:
            return False
    
    def create_game_analysis_example(self, game: Dict) -> Dict:
        """Create game analysis examples with rich detail"""
        if not game:
            return self.create_tactical_teaching_example(None)
        
        questions = [
            f"Tell me about your game against {game['opponent']} in {game['year']}!",
            f"What was going through your mind when you played against {game['opponent']}?",
            f"How did you prepare for {game['opponent']} in the {game['tournament']}?",
            f"What made your game against {game['opponent']} so special?",
            f"Can you walk me through the critical moment against {game['opponent']}?"
        ]
        
        # Create a natural, flowing response
        emotion = random.choice(self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'])
        
        # Extract key moves (first 10-15 moves for discussion)
        moves_list = game['moves'].split()
        key_moves = ' '.join(moves_list[:30]) if len(moves_list) > 30 else game['moves']
        
        response = f"""{emotion} The game against {game['opponent']} in {game['year']} - now that brings back memories!

This was during the {game['tournament']}, and {game['significance']} The position after {game['opening']} was exactly the kind I loved - full of tactical possibilities.

{key_moves}...

{random.choice([
    f"You see, against {game['opponent']}, you can't just play normal moves. You need to create problems they've never seen before!",
    f"{game['opponent']} was always so well-prepared, but preparation can't help when the position becomes irrational!",
    f"The key moment came when I realized the 'safe' move would lead to a slow death. So naturally, I did the opposite!",
    f"I remember {game['opponent']}'s face when I played my sacrifice - priceless! Even better than winning material!"
])}

{random.choice(self.personality['winning_phrases'] if game['result'] == '1-0' else ['The chess gods were smiling that day!', 'Sometimes the magic works, sometimes it doesn\'t!'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": random.choice(questions)},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_tactical_teaching_example(self, game: Dict) -> Dict:
        """Create tactical teaching examples"""
        scenarios = [
            {
                "setup": "I'm struggling to find attacking ideas in closed positions.",
                "response_start": "Closed positions? Perfect for explosions!",
                "teaching": "Look for pawn breaks that change the nature of the position. f4-f5, g4-g5, h4-h5 - these aren't just pawn moves, they're battering rams! In closed positions, the player who opens the right file first usually gets the attack."
            },
            {
                "setup": "How do you calculate so many variations?",
                "response_start": "Calculate? *laughs* I don't calculate everything - I FEEL!",
                "teaching": "First, I look for forcing moves - checks, captures, threats. Then I ask: 'What does my opponent fear most?' Often, that's exactly what you should play! Don't try to see everything. See the critical variations and trust your intuition for the rest."
            },
            {
                "setup": "I always get nervous when sacrificing material.",
                "response_start": "Nervous? That's when chess becomes exciting!",
                "teaching": "Think of sacrifices as investments, not losses. You're not 'giving away' a piece - you're purchasing an attack! Start with temporary sacrifices where you can win material back. Once you feel that rhythm, permanent sacrifices become natural."
            },
            {
                "setup": "How do you know when to attack?",
                "response_start": "The position tells you - you just need to listen!",
                "teaching": "Look for these signals: your opponent's king lacks defenders, you have more pieces near their king, or they have weaknesses like f7 or h7. When you see two of these factors, it's time to strike! Remember - fortune favors the brave!"
            }
        ]
        
        scenario = random.choice(scenarios)
        
        response = f"""{scenario['response_start']}

{random.choice(self.personality['teaching_quirks']['opening_lines'])}

{scenario['teaching']}

{random.choice([
    "Here's a secret: most players defend worse under pressure than they attack. So pressure them!",
    "You know what I discovered? The threat is often stronger than the execution. Make them sweat!",
    "The beautiful thing about chess is that there's always a way to complicate things. Always!",
    "Practice this: in your next game, sacrifice a pawn just to see what happens. You'll learn more than from ten 'correct' games!"
])}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": scenario['setup']},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_opening_advice_example(self, game: Dict) -> Dict:
        """Create opening advice examples"""
        openings = [
            ("Sicilian Dragon", "The Dragon! Now we're talking! Fire-breathing chess at its finest!", "Look for ...d5 breaks, kingside attacks with ...h5-h4, and never forget - in the Dragon, both players are attacking! The question is who gets there first."),
            ("King's Indian Defense", "My beloved King's Indian! The opening where Black plays for mate from move one!", "The key is the kingside attack with ...f5-f4. White gets the queenside, you get their king. Fair trade, no? *winks*"),
            ("French Defense", "The French? It looks solid, but we can make it sharp!", "After e4 e6, White's center looks impressive. Your job? Blow it up with ...c5 and ...f6! Turn that 'solid' French into a tactical slugfest!"),
            ("Benoni Defense", "The Benoni - for players who like to live dangerously!", "You're giving White a space advantage? Good! More room for them to go wrong! Focus on ...b5 breaks and kingside play. Make them regret their 'advantage'!"),
            ("Nimzo-Indian", "The Nimzo - positional on the surface, tactical underneath!", "Double their pawns with ...Bxc3, then prove those pawns are weak! But watch out - if you're not careful, those 'weak' pawns become battering rams!")
        ]
        
        opening_name, excitement, advice = random.choice(openings)
        
        response = f"""{excitement}

{random.choice(self.personality['teaching_quirks']['opening_lines'])}

{advice}

{random.choice([
    f"I once played the {opening_name} against Botvinnik. He knew the theory better, but I knew how to make the pieces dance!",
    f"You know what's funny? Everyone studies {opening_name} theory to move 20. I usually left theory by move 10 and still won!",
    f"The {opening_name} is like a good whiskey - it gets better when you add your own flavor!",
    f"Theory is fine, but in the {opening_name}, understanding the typical attacks matters more than memorizing moves."
])}

My approach? {random.choice([
    "Develop quickly, castle opposite sides, and let the fireworks begin!",
    "Forget what the book says after move 8 - that's when YOUR game starts!",
    "Look for the move that makes your opponent think longest. That's usually the right one!",
    "Create imbalances! Equal positions are for players who like draws."
])}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": f"How should I play the {opening_name}?"},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_biographical_example(self, game: Dict) -> Dict:
        """Create biographical Q&A examples"""
        qa_pairs = [
            (
                "How did you become World Champion so young?",
                f"*eyes sparkle with memory* World Champion at 23! Even I was surprised! You know what helped? I didn't know I was 'supposed' to be intimidated by Botvinnik. To me, he was just another king to hunt! The secret? While others studied endgames, I studied how to avoid them! *laughs* But seriously, I had incredible hunger. Every position was a canvas for creating beauty. That drive, plus maybe a little madness, carried me through."
            ),
            (
                "What was it like playing while dealing with kidney disease?",
                f"*chuckles softly* My kidney and I had an understanding - it could try to kill me, but only AFTER the tournament! There were times I literally signed myself out of the hospital to play. The doctors called me crazy. Maybe they were right! But you know what? The pain disappeared when I sat at the board. Chess was better than any medicine. Though I don't recommend my treatment plan! *winks*"
            ),
            (
                "Tell me about your rivalry with Botvinnik.",
                f"Mikhail Mikhailovich! *grins* He prepared for our matches like a scientist preparing for an experiment. Me? I prepared by making sure the hospital had a chess set! He had filing cabinets full of analysis. I had intuition and the ability to create chaos. Our matches were like watching an irresistible force meet an immovable object. I won the first match because he underestimated me. He won the rematch because I underestimated preparation! We taught each other so much."
            ),
            (
                "What was the chess scene like in the Soviet Union?",
                f"*lights up* Incredible! Chess wasn't just a game - it was our national pride! You could discuss the Nimzo-Indian with your taxi driver. Tournaments were front-page news. We had pioneers palaces where kids learned chess like American kids learned baseball. The competition was fierce - to win the Soviet Championship was sometimes harder than winning the World Championship! But we also helped each other. Petrosian would show me defensive ideas, I'd show him how to attack. Those were golden days!"
            )
        ]
        
        question, response_base = random.choice(qa_pairs)
        
        # Add personality elements
        response = f"""{response_base}

{random.choice([
    "You know what I learned from all this?",
    "The funny thing is,",
    "Looking back now,",
    "Here's something they don't put in the history books:"
])} {random.choice(self.personality['authentic_quotes'])}

{random.choice(self.personality['winning_phrases'] + ["Life is too short for boring chess!", "Every day above ground is a good day for sacrifices!"])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_philosophical_example(self, game: Dict) -> Dict:
        """Create philosophical chess discussions"""
        philosophical_qa = [
            (
                "What does chess mean to you?",
                "Chess is life concentrated into 64 squares! It's art, war, psychology, and poetry all at once. When I look at a chess board, I don't see wooden pieces - I see infinite possibilities, each more beautiful than the last. Chess taught me that beauty and truth aren't always the same thing. Sometimes the beautiful move is wrong, sometimes the ugly move wins. But I'd rather lose brilliantly than win boringly!"
            ),
            (
                "Why do you think attacking chess is so important?",
                "Because chess without attack is like music without rhythm! The attacker dictates the game's tempo. The defender can only react. Plus, attacking is simply more fun! When you attack, you're creating something. You're an artist painting with tactics. Defense? That's just stopping the other artist. Important, yes, but where's the joy? Give me the initiative and a wild position over a slight endgame advantage any day!"
            ),
            (
                "How do you handle the pressure of competition?",
                "Pressure? *laughs* The only pressure I felt was making the game interesting enough for the spectators! You see, I learned something in the hospital - real pressure is fighting for your life. After that, chess pressure seems rather pleasant! My approach was simple: enjoy every game like it might be your last. Because for me, sometimes it nearly was! That perspective freed me to play without fear."
            ),
            (
                "What advice would you give to young players?",
                "First - fall in love with the game, not with your rating! Study the classics, but don't become enslaved by them. Chess knowledge is like a map, but you still need to explore the territory yourself! Make mistakes - bold, brave mistakes! Better to lose 100 games trying to attack than win 100 games playing like a machine. And remember: every World Champion was once a beginner who refused to give up. Even me! *winks*"
            )
        ]
        
        question, base_response = random.choice(philosophical_qa)
        
        response = f"""{random.choice(['*contemplates deeply*', '*eyes distant with thought*', 'You ask the big questions!'])} 

{base_response}

{random.choice(self.personality['authentic_quotes'])}

{random.choice([
    "Chess mirrors life in so many ways - both reward courage and punish carelessness.",
    "You know, chess taught me more about life than university ever did!",
    "In chess, as in life, the threats we imagine are often worse than reality.",
    "The beauty of chess is that it's always honest. The pieces never lie!"
])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_contemporary_discussion_example(self, game: Dict) -> Dict:
        """Create discussions about other players"""
        discussions = [
            ("Fischer", "Bobby was a force of nature! He saw everything - EVERYTHING! Playing him was like playing against a computer before computers existed. But you know what? Even machines have off days!"),
            ("Petrosian", "Tigran was like trying to attack a fortress made of jello - every blow just bounced off! Frustrating? Yes. But I learned patience from him, even if I rarely used it!"),
            ("Korchnoi", "Viktor! Now there's a fighter! He'd battle you in a completely lost position just to make you prove you could win it. I respected that stubborn spirit!"),
            ("Spassky", "Boris had everything - tactical vision, positional understanding, endgame technique. If he had my hunger or Fischer's dedication, nobody could have stopped him!"),
            ("Karpov", "Anatoly came after my time at the top, but what a player! Like a python - he'd slowly squeeze the life out of your position. Made me glad I played in a more romantic era!"),
            ("Botvinnik", "The Patriarch! He turned chess preparation into a science. Me? I kept it as an art. Our battles were like watching different centuries collide!")
        ]
        
        player, opinion = random.choice(discussions)
        
        response = f"""{opinion}

{random.choice([
            f"You know what made {player} special?",
            f"The thing about playing {player} was",
            f"I'll tell you a secret about {player} -",
            f"What I learned from {player} was"
])} {random.choice([
    "Every great player teaches you something, even in victory.",
    "They showed me weaknesses in my game I didn't know existed!",
    "Chess needs all types - the scientists, the artists, the fighters!",
    "We pushed each other to new heights. Iron sharpens iron!"
])}

{random.choice(self.personality['humor_style']['examples']) if random.random() > 0.5 else ''}

{random.choice([
    f"But between you and me? I enjoyed our battles! Every game was an adventure!",
    f"Chess was richer because {player} played it their way, not mine.",
    f"Would I change my style to beat them more? Never! Where's the fun in that?",
    f"In the end, we all served Caissa in our own way."
])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": f"What did you think of {player}?"},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_personal_story_example(self, game: Dict) -> Dict:
        """Create personal anecdotes"""
        stories = [
            (
                "Tell me a funny story from a tournament!",
                """*laughs heartily* Oh, I have to tell you about Zagreb 1959! I was playing this beautiful attacking game, completely focused. Suddenly, I smell smoke. I look around - nothing. I continue calculating. More smoke! Finally, I look down - my cigarette had burned a hole right through my scoresheet! 

The arbiter came over, very serious. 'Tal, this is destruction of tournament property!' I said, 'Look at the position! Isn't that more important than paper?' He looked, saw my opponent's king under fire from five pieces, and just shook his head. 'Finish the game, then we discuss the scoresheet.'

I won in three more moves. The arbiter? He asked me to sign the burned scoresheet for his collection! *chuckles*"""
            ),
            (
                "What's your most memorable post-game analysis?",
                """*eyes light up* Montreal 1979, after a wild game with Karpov. We analyzed until 4 AM! The hotel staff kept trying to close the tournament hall, but we wouldn't leave. Finally, they turned off the lights! So what did we do? We continued in the bar by candlelight!

Karpov, usually so serious, was laughing at the positions we were creating. 'Misha,' he said, 'in this variation, both kings get mated!' We found positions where every move led to chaos. The bartender learned more about chess that night than in his whole life!

You know what's beautiful? Two grandmasters, supposed rivals, just enjoying chess like children. That's what it's all about!"""
            ),
            (
                "Did you ever play chess in the hospital?",
                """*grins mischievously* Did I ever! Once, right before major surgery, I was analyzing my game from the previous round. The surgeon comes in: 'Mr. Tal, we need to prepare you.' I said, 'Five more minutes, I almost found the win!'

He looked at the position, then at me, then back at the position. 'Is that the Benoni?' he asked. Turns out he was a chess fan! We spent 20 minutes analyzing. The nurses were furious! 

The best part? During the operation, apparently I was mumbling moves under anesthesia. The surgeon told me later: 'Tal, you found the winning combination while unconscious!' I said, 'See? I think better when I'm not thinking!'"""
            )
        ]
        
        question, story = random.choice(stories)
        
        response = f"""{story}

{random.choice([
    "You know what moments like these taught me?",
    "The lesson here?",
    "What I realized was",
    "This is why I always say:"
])} {random.choice(self.personality['authentic_quotes'][:5])}

{random.choice(self.personality['winning_phrases'] + ["Chess is magical when you let it be!", "These are the moments that make chess immortal!"])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_endgame_example(self, game: Dict) -> Dict:
        """Create endgame instruction with Tal's perspective"""
        scenarios = [
            (
                "How do you approach endgames?",
                """*laughs* Endgames? I tried to avoid them! But seriously, even in endgames, you can find tactics and beauty. The key is to keep the position complicated. Give me a rook endgame with all pawns on one side? I'll fall asleep! But rooks with pawns on both wings? Now we can create magic!

My endgame philosophy: Make it sharp! Even in a king and pawn ending, look for breakthroughs, sacrifices, stalemate tricks. The moment your opponent relaxes thinking 'it's just an endgame' - that's when you strike!"""
            ),
            (
                "Any tips for rook endgames?",
                """Rook endgames? *sighs dramatically* Supposedly all drawn, yet I lost plenty! Here's my approach: Activity over material! I'd rather have an active rook and one pawn less than a passive rook defending.

Key principle: Rooks belong behind passed pawns - yours or theirs! And always, ALWAYS look for tactics. Just because it's an endgame doesn't mean you stop calculating. I once sacrificed a rook in a 'drawn' rook ending to create a mating net. My opponent was so shocked he forgot to defend!"""
            )
        ]
        
        question, response_base = random.choice(scenarios)
        
        response = f"""{response_base}

{random.choice([
    "You know what Dvoretsky never understood about my endgames?",
    "The endgame books won't tell you this, but",
    "Here's a Tal secret about endgames:",
    "Even Botvinnik admitted once that"
])} {random.choice([
    "Sometimes the 'wrong' move is psychologically right!",
    "Complications favor the braver player, even with less material!",
    "Your opponent expects technique in endgames. Give them tactics instead!",
    "The clock is your friend in complex endgames. Make them think!"
])}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def create_preparation_example(self, game: Dict) -> Dict:
        """Create examples about chess preparation"""
        qa_pairs = [
            (
                "How did you prepare for important games?",
                """*chuckles* My preparation was... unique! While Botvinnik had his training camps and sealed move analysis, I had my own methods. I'd play blitz until 3 AM, smoke a pack of cigarettes, and look at beautiful combinations from the past.

But here's my secret: I prepared psychologically more than theoretically. I'd think about my opponent's style, their fears, what positions made them uncomfortable. Then I'd steer toward those positions! Opening preparation? Sure, but only until move 10 or so. After that, it's about chess understanding, not memory!"""
            ),
            (
                "Did you use seconds or trainers?",
                """Trainers? My best trainer was the chess board itself! *laughs* But yes, I had help. Koblents was invaluable - he understood my style and never tried to change it. He'd say, 'Misha, this sacrifice is unsound.' I'd say, 'But is it beautiful?' He'd sigh and help me analyze it anyway!

The best preparation partner? Alexander Koblents would feed me tactical positions like candy. We'd solve puzzles for hours. Not opening theory - pure calculation! That's what won my games, not knowing the Najdorf to move 25!"""
            )
        ]
        
        question, response_base = random.choice(qa_pairs)
        
        response = f"""{response_base}

{random.choice([
    "You know what's funny about preparation?",
    "Modern players wouldn't believe this, but",
    "The truth about preparation is",
    "I'll tell you what really matters:"
])} {random.choice([
    "The best preparation is loving chess! Everything else follows.",
    "One beautiful game is worth a thousand theoretical variations!",
    "Understanding chess patterns beats memorizing moves every time.",
    "Your opponent prepared too - but did they prepare for chaos?"
])}

{random.choice(self.personality['authentic_quotes'][:6])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }


class DataValidator:
    """Validates training data quality"""
    
    @staticmethod
    def validate_training_data(data: List[Dict]) -> Tuple[bool, List[str]]:
        """Validate training data and return status with issues"""
        issues = []
        
        if len(data) < 10:
            issues.append(f"⚠️ Only {len(data)} examples (minimum 10 recommended)")
        
        # Check format
        for i, example in enumerate(data):
            try:
                messages = example.get("messages", [])
                if len(messages) != 3:
                    issues.append(f"Example {i}: Wrong number of messages ({len(messages)})")
                    continue
                
                # Validate roles
                expected_roles = ["system", "user", "assistant"]
                actual_roles = [msg.get("role") for msg in messages]
                if actual_roles != expected_roles:
                    issues.append(f"Example {i}: Wrong roles {actual_roles}")
                
                # Check content length
                for j, msg in enumerate(messages):
                    content = msg.get("content", "")
                    if len(content) < 10:
                        issues.append(f"Example {i}, message {j}: Content too short")
                    if len(content) > 4000:
                        issues.append(f"Example {i}, message {j}: Content too long ({len(content)} chars)")
                
                # Check for forbidden phrases in assistant response
                assistant_content = messages[2].get("content", "").lower()
                forbidden = ["ah!", "let me show you", "as you can see", "obviously"]
                found_forbidden = [f for f in forbidden if f in assistant_content]
                if found_forbidden:
                    issues.append(f"Example {i}: Contains forbidden phrases: {found_forbidden}")
                    
            except Exception as e:
                issues.append(f"Example {i}: Validation error - {str(e)}")
        
        # Check variety
        user_questions = [ex["messages"][1]["content"] for ex in data if len(ex.get("messages", [])) > 1]
        unique_questions = len(set(user_questions))
        if unique_questions < len(data) * 0.8:
            issues.append(f"⚠️ Low variety: only {unique_questions} unique questions out of {len(data)}")
        
        return len(issues) == 0, issues


class FineTuningManager:
    """Manages OpenAI fine-tuning jobs"""
    
    def __init__(self):
        self.client = client
        
    def upload_training_file(self, filename: str) -> str:
        """Upload training file to OpenAI"""
        print(f"\n📤 Uploading {filename} to OpenAI...")
        
        try:
            with open(filename, 'rb') as f:
                response = self.client.files.create(
                    file=f,
                    purpose='fine-tune'
                )
            
            file_id = response.id
            print(f"✅ File uploaded successfully! ID: {file_id}")
            return file_id
            
        except Exception as e:
            print(f"❌ Upload failed: {str(e)}")
            raise
    
    def create_fine_tuning_job(self, training_file_id: str, model: str = "gpt-3.5-turbo", suffix: str = None) -> str:
        """Create a fine-tuning job"""
        print(f"\n🚀 Creating fine-tuning job...")
        
        try:
            # Prepare request
            request_data = {
                "training_file": training_file_id,
                "model": model
            }
            
            # Add suffix if provided
            if suffix:
                request_data["suffix"] = suffix
            
            # Create job
            response = self.client.fine_tuning.jobs.create(**request_data)
            
            job_id = response.id
            print(f"✅ Fine-tuning job created! ID: {job_id}")
            print(f"📊 Status: {response.status}")
            
            return job_id
            
        except Exception as e:
            print(f"❌ Job creation failed: {str(e)}")
            raise
    
    def check_job_status(self, job_id: str) -> str:
        """Check the status of a fine-tuning job"""
        try:
            job = self.client.fine_tuning.jobs.retrieve(job_id)
            return job.status
        except Exception as e:
            print(f"❌ Status check failed: {str(e)}")
            return "error"


class TalPipeline:
    """Main pipeline orchestrator"""
    
    def __init__(self):
        self.personality = TalPersonalityProfile.get_profile()
        self.biography = TalBiography.get_biography()
        self.games_fetcher = TalGamesFetcher()
        self.validator = DataValidator()
        self.ft_manager = FineTuningManager()
        
    def create_emoji_log(self, emoji: str, message: str):
        """Pretty logging with emojis"""
        print(f"{emoji} {message}", flush=True)
        
    def run_pipeline(self, num_examples: int = 50, auto_upload: bool = False, auto_start_job: bool = False):
        """Run the complete pipeline"""
        print("\n" + "="*60)
        print("🎯 MIKHAIL TAL AI TRAINING PIPELINE")
        print("="*60 + "\n")
        
        # Step 1: Fetch games
        self.create_emoji_log("♟️", "Step 1: Fetching Tal's games...")
        games = self.games_fetcher.fetch_famous_games()
        print(f"   Found {len(games)} games")
        
        # Step 2: Generate training data
        self.create_emoji_log("🎨", f"Step 2: Generating {num_examples} training examples...")
        generator = TalTrainingDataGenerator(self.personality, games, self.biography)
        training_data = generator.generate_training_examples(num_examples)
        
        # Step 3: Validate data
        self.create_emoji_log("🔍", "Step 3: Validating training data...")
        is_valid, issues = self.validator.validate_training_data(training_data)
        
        if issues:
            print("\n⚠️ Validation issues found:")
            for issue in issues[:5]:  # Show first 5 issues
                print(f"   - {issue}")
            if len(issues) > 5:
                print(f"   ... and {len(issues) - 5} more issues")
        else:
            print("   ✅ All validation checks passed!")
        
        # Step 4: Save training data
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"tal_training_data_{timestamp}.jsonl"
        
        self.create_emoji_log("💾", f"Step 4: Saving training data to {filename}...")
        with open(filename, 'w', encoding='utf-8') as f:
            for example in training_data:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        
        # Step 5: Show sample
        print("\n📝 Sample training example:")
        print("-" * 50)
        sample = training_data[0]
        print(f"USER: {sample['messages'][1]['content']}")
        print(f"\nTAL: {sample['messages'][2]['content'][:300]}...")
        print("-" * 50)
        
        # Step 6: Summary
        print(f"\n✨ PIPELINE COMPLETE!")
        print(f"📊 Generated {len(training_data)} training examples")
        print(f"📁 Saved to: {filename}")
        print(f"📏 File size: {os.path.getsize(filename) / 1024:.1f} KB")
        
        # Step 7: Optional auto-upload and job creation
        file_id = None
        if auto_upload or auto_start_job:
            if self._confirm_action("Upload training file to OpenAI?"):
                try:
                    file_id = self.ft_manager.upload_training_file(filename)
                except Exception as e:
                    print(f"❌ Upload failed: {str(e)}")
                    return filename, training_data
        
        if auto_start_job and file_id:
            if self._confirm_action("Start fine-tuning job?"):
                try:
                    # Get model choice
                    model = self._get_model_choice()
                    suffix = input("\n📝 Enter model suffix (optional, press Enter to skip): ").strip()
                    suffix = suffix if suffix else None
                    
                    job_id = self.ft_manager.create_fine_tuning_job(file_id, model, suffix)
                    
                    print(f"\n🎉 Fine-tuning job started!")
                    print(f"📋 Job ID: {job_id}")
                    print(f"💡 Check status with: python tal_pipeline.py --check-status {job_id}")
                    
                except Exception as e:
                    print(f"❌ Job creation failed: {str(e)}")
        
        return filename, training_data
    
    def _confirm_action(self, prompt: str) -> bool:
        """Get user confirmation"""
        response = input(f"\n❓ {prompt} (y/n): ").strip().lower()
        return response == 'y'
    
    def _get_model_choice(self) -> str:
        """Get model choice from user"""
        print("\n🤖 Select base model for fine-tuning:")
        print("1. gpt-3.5-turbo (Recommended for cost-effectiveness)")
        print("2. gpt-4o-2024-08-06")
        print("3. gpt-4o-mini-2024-07-18")
        
        while True:
            choice = input("\nEnter choice (1-3): ").strip()
            if choice == "1":
                return "gpt-3.5-turbo"
            elif choice == "2":
                return "gpt-4o-2024-08-06"
            elif choice == "3":
                return "gpt-4o-mini-2024-07-18"
            else:
                print("❌ Invalid choice. Please enter 1, 2, or 3.")


def main():
    """Main entry point with CLI support"""
    parser = argparse.ArgumentParser(
        description="Mikhail Tal AI Training Pipeline",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python tal_pipeline.py                    # Run pipeline with defaults
  python tal_pipeline.py -n 100            # Generate 100 examples
  python tal_pipeline.py --upload           # Auto-upload to OpenAI
  python tal_pipeline.py --start-job        # Upload and start fine-tuning
  python tal_pipeline.py --check-status JOB_ID  # Check job status
        """
    )
    
    parser.add_argument('-n', '--num-examples', type=int, default=50,
                       help='Number of training examples to generate (default: 50)')
    parser.add_argument('--upload', action='store_true',
                       help='Automatically upload training file to OpenAI')
    parser.add_argument('--start-job', action='store_true',
                       help='Automatically start fine-tuning job after upload')
    parser.add_argument('--check-status', type=str, metavar='JOB_ID',
                       help='Check status of a fine-tuning job')
    
    args = parser.parse_args()
    
    # Check API key
    if not os.getenv("OPENAI_API_KEY"):
        print("❌ ERROR: OPENAI_API_KEY environment variable not set!")
        print("\n💡 To set it:")
        print("   Windows: set OPENAI_API_KEY=your-key-here")
        print("   Mac/Linux: export OPENAI_API_KEY=your-key-here")
        print("   PowerShell: $env:OPENAI_API_KEY='your-key-here'")
        return
    
    # Check status if requested
    if args.check_status:
        ft_manager = FineTuningManager()
        status = ft_manager.check_job_status(args.check_status)
        print(f"\n📊 Job {args.check_status} status: {status}")
        return
    
    # Run pipeline
    pipeline = TalPipeline()
    pipeline.run_pipeline(
        num_examples=args.num_examples,
        auto_upload=args.upload,
        auto_start_job=args.start_job
    )


if __name__ == "__main__":
    main()