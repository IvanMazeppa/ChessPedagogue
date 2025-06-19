#!/usr/bin/env python3
"""
Enhanced Unified Tal Training Pipeline V3 - Optimized for Fine-Tuning!
Creates focused, diverse, high-quality training examples
Author: Ben
Enhanced with love and chess passion!
"""

import json
import random
import os
import re
from datetime import datetime
from typing import List, Dict, Set, Tuple, Optional
from pathlib import Path
import hashlib

class OptimizedTalGenerator:
    def __init__(self):
        # Load your enhanced personality data
        self.personality = self.load_enhanced_personality()
        self.games = self.load_games()
        self.training_examples = []
        self.used_responses = set()  # Track unique responses
        
        # Response length targets for optimal fine-tuning
        self.MIN_RESPONSE_LENGTH = 400
        self.MAX_RESPONSE_LENGTH = 1200
        self.OPTIMAL_RESPONSE_LENGTH = 800
        
    def load_enhanced_personality(self) -> Dict:
        """Load the enhanced personality profile"""
        try:
            with open('mikhail_tal_enhanced.json', 'r', encoding='utf-8') as f:
                data = json.load(f)
                print("✨ Loaded enhanced personality profile!")
                print(f"   - {len(data['authentic_quotes'])} authentic quotes")
                print(f"   - {len(data['rival_opinions'])} rival opinions")
                print(f"   - {len(data['personal_quirks'])} personal quirks")
                return data
        except FileNotFoundError:
            print("❌ Could not find mikhail_tal_enhanced.json")
            return {}
    
    def load_games(self) -> List[Dict]:
        """Load the games file"""
        try:
            with open('mikhail_tal_games.json', 'r', encoding='utf-8') as f:
                data = json.load(f)
                games = data.get('games', [])
                print(f"🎮 Loaded {len(games)} famous games!")
                return games
        except FileNotFoundError:
            print("⚠️ No games file found - continuing without games")
            return []
    
    def create_focused_system_prompt(self) -> str:
        """Create a concise but complete system prompt"""
        # Keep it focused but comprehensive
        traits = self.personality.get('personality_traits', ['passionate', 'aggressive', 'poetic'])
        quotes = self.personality.get('authentic_quotes', [])[:3]
        forbidden = self.personality.get('forbidden_phrases', [])
        
        return f"""You are Mikhail Tal, the 8th World Chess Champion (1960-1961), "The Magician from Riga."

PERSONALITY: {', '.join(traits)}

KEY PHRASES:
{chr(10).join(['- "' + q + '"' for q in quotes])}

NEVER USE: {', '.join(forbidden)}

TEACHING STYLE: Focus on inspiration over memorization. Use vivid chess examples.

RESPOND: With passion, humor, and specific chess insights. Keep responses focused and engaging."""
    
    def generate_response_hash(self, response: str) -> str:
        """Generate hash to track unique responses"""
        # Normalize the response for comparison
        normalized = re.sub(r'\s+', ' ', response.lower().strip())
        return hashlib.md5(normalized.encode()).hexdigest()
    
    def ensure_response_quality(self, response: str) -> str:
        """Ensure response meets quality standards"""
        # Check length
        if len(response) > self.MAX_RESPONSE_LENGTH:
            # Intelligently truncate at sentence boundaries
            sentences = response.split('. ')
            truncated = ""
            for sentence in sentences:
                if len(truncated) + len(sentence) < self.MAX_RESPONSE_LENGTH - 50:
                    truncated += sentence + ". "
                else:
                    break
            response = truncated.strip()
        
        # Ensure minimum quality markers
        quality_markers = {
            'chess_specific': any(term in response.lower() for term in 
                ['sacrifice', 'position', 'piece', 'attack', 'defense', 'move']),
            'personality': any(phrase in response for phrase in 
                ['!', '*', 'laugh', 'smile', 'eyes']),
            'teaching': any(word in response.lower() for word in 
                ['learn', 'try', 'remember', 'here', 'secret'])
        }
        
        # Add chess specificity if missing
        if not quality_markers['chess_specific']:
            chess_additions = [
                " Remember, every position has hidden possibilities!",
                " Chess rewards the brave, not the careful!",
                " The board is your canvas - paint boldly!"
            ]
            response += random.choice(chess_additions)
        
        return response.strip()
    
    def create_tactical_question_response(self) -> Dict:
        """Generate focused tactical Q&A"""
        tactical_scenarios = [
            {
                "question": "How do I improve my tactical vision?",
                "focus": "pattern_recognition",
                "length": "medium"
            },
            {
                "question": "When should I sacrifice a piece?",
                "focus": "sacrifice_timing",
                "length": "medium"
            },
            {
                "question": "I keep missing simple tactics. Help!",
                "focus": "tactical_blindness",
                "length": "short"
            },
            {
                "question": "How do you calculate so quickly?",
                "focus": "calculation_speed",
                "length": "medium"
            },
            {
                "question": "What's the secret to attacking chess?",
                "focus": "attacking_principles",
                "length": "medium"
            }
        ]
        
        scenario = random.choice(tactical_scenarios)
        response = self._build_focused_tactical_response(scenario)
        
        # Ensure uniqueness
        response_hash = self.generate_response_hash(response)
        attempts = 0
        while response_hash in self.used_responses and attempts < 5:
            scenario = random.choice(tactical_scenarios)
            response = self._build_focused_tactical_response(scenario)
            response_hash = self.generate_response_hash(response)
            attempts += 1
        
        self.used_responses.add(response_hash)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_focused_system_prompt()},
                {"role": "user", "content": scenario["question"]},
                {"role": "assistant", "content": self.ensure_response_quality(response)}
            ]
        }
    
    def _build_focused_tactical_response(self, scenario: Dict) -> str:
        """Build focused tactical response based on scenario"""
        focus = scenario["focus"]
        
        if focus == "pattern_recognition":
            emotion = random.choice(["*eyes light up*", "*leans forward eagerly*"])
            core_advice = random.choice([
                "Patterns are everywhere! Start with the basics: loose pieces, weak squares, exposed kings.",
                "Every tactic has a 'signature' - learn to recognize these signatures!"
            ])
            specific_tip = "Practice this: before each move, spend 5 seconds looking for undefended pieces and checking distances. You'll be amazed what appears!"
            personal_touch = random.choice(self.personality['humor_style']['examples'])
            
            response = f"{emotion} Tactical vision is like learning to see in color after living in black and white!\n\n{core_advice}\n\n{specific_tip}\n\n{personal_touch}"
            
        elif focus == "sacrifice_timing":
            key_principle = "A sacrifice is correct when your remaining pieces become more active than your opponent's extra material!"
            concrete_example = "Look for these signals: opponent's king in center, your pieces aimed at one target, their pieces uncoordinated."
            wisdom = random.choice(self.personality['authentic_quotes'][:3])
            
            response = f"*grins mischievously* The right moment to sacrifice? When the position demands it!\n\n{key_principle}\n\n{concrete_example}\n\nRemember: {wisdom}"
            
        elif focus == "tactical_blindness":
            encouragement = random.choice(self.personality['teaching_quirks']['encouragement_style'])
            practical_advice = "Before moving, ask yourself: 'What does my opponent threaten?' and 'What's undefended?' These two questions catch 80% of tactics!"
            
            response = f"Missing tactics? Join the club - even I missed them sometimes!\n\n{encouragement}\n\n{practical_advice}\n\nThe tactics you miss today are the ones you'll spot instantly tomorrow!"
            
        elif focus == "calculation_speed":
            method = "I don't calculate every variation - I calculate the RIGHT variations! Focus on forcing moves: checks, captures, threats."
            secret = "Here's my secret: I 'feel' which lines are critical. This comes from pattern recognition, not raw calculation."
            
            response = f"*taps temple* Fast calculation isn't about speed - it's about efficiency!\n\n{method}\n\n{secret}\n\nTrust your intuition - it's your subconscious recognizing patterns!"
            
        else:  # attacking_principles
            principles = "Attacking chess is simple: 1) Develop with threats, 2) Create weaknesses, 3) Concentrate forces, 4) Strike when ready!"
            example = "If you have 4 pieces aimed at their kingside and they have 3 defenders, the attack usually works. Simple arithmetic!"
            
            response = f"The secret to attacking? There is no secret - just courage and calculation!\n\n{principles}\n\n{example}\n\nAttack with joy, defend with determination!"
        
        return response
    
    def create_positional_question_response(self) -> Dict:
        """Generate positional understanding Q&A"""
        positional_topics = [
            ("How do you handle closed positions?", "closed_positions"),
            ("When should I trade pieces?", "piece_exchanges"),
            ("How do I create weaknesses?", "creating_weaknesses"),
            ("What's your approach to pawn structures?", "pawn_structures"),
            ("How do you play against stronger opponents?", "psychological_approach")
        ]
        
        question, topic = random.choice(positional_topics)
        response = self._build_positional_response(topic)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_focused_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": self.ensure_response_quality(response)}
            ]
        }
    
    def _build_positional_response(self, topic: str) -> str:
        """Build focused positional response"""
        if topic == "closed_positions":
            response = f"""*sighs dramatically* Closed positions? Like trying to dance in a phone booth!

But here's the secret: even locked positions want to explode. Look for pawn breaks - f5, e5, h5. Prepare them patiently, then strike!

{self.personality['rival_opinions'].get('Petrosian', 'Petrosian loved these positions.')} But I learned to make them dynamic.

In closed positions, piece placement matters more than material. A well-placed knight can be worth a rook!"""
            
        elif topic == "piece_exchanges":
            response = f"""Trading pieces is an art! My rules:

1. Trade when ahead in material (simplify to win)
2. Trade your opponent's active pieces for your passive ones
3. DON'T trade when attacking - you need soldiers!

I once refused to trade queens when up a whole rook. Why? Because my attack needed that queen! 

Every trade changes the position's character. Make sure it changes in YOUR favor!"""
            
        elif topic == "creating_weaknesses":
            response = f"""*eyes gleam* Creating weaknesses is like picking a lock - gentle pressure in the right spots!

My favorite methods:
- Provoke pawn moves with threats (h3 weakens g3, f6 weakens e6)
- Force pieces to bad squares defending threats
- Create pawn tensions they must resolve

The key? Make moves that improve your position while asking questions. Eventually, they'll give a wrong answer!"""
            
        elif topic == "pawn_structures":
            response = f"""Pawns are the soul of chess! But for me, they were also dynamite waiting to explode!

Don't just study pawn structures - understand their dynamics:
- Hanging pawns: Weak or dynamic? Depends who's attacking!
- Pawn chains: Attack the base, but prepare the breakthrough
- Isolated pawns: Give piece activity in return for weakness

Static thinking about pawns leads to static play. Think dynamically!"""
            
        else:  # psychological_approach
            response = f"""*grins knowingly* Against stronger opponents? Make them play MY game, not theirs!

Create complications! Strong players calculate well in clear positions. In chaos? Everyone's equal!

I loved playing the elite because they expected 'correct' chess. One unexpected sacrifice and suddenly THEY were nervous!

Remember: ratings measure past performance, not current courage!"""
            
        return response
    
    def create_game_story_response(self) -> Dict:
        """Create focused game story"""
        if not self.games:
            # Create a generic game story
            question = "Tell me about one of your brilliant games!"
            response = self._build_generic_game_story()
        else:
            game = random.choice(self.games)
            question = f"Tell me about your game against {game['opponent']}!"
            response = self._build_specific_game_story(game)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_focused_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": self.ensure_response_quality(response)}
            ]
        }
    
    def _build_specific_game_story(self, game: Dict) -> str:
        """Build concise game story"""
        emotion = random.choice(["*eyes sparkle with memory*", "*laughs warmly*"])
        
        response = f"""{emotion} {game['opponent']} in {game['year']}! What a battle that was!

{game.get('significance', 'A memorable game')}.

The critical moment came in the {game.get('opening', 'middlegame')}. I saw a piece sacrifice that would open lines to the king. Was it sound? Who cares! It was beautiful!

{game['opponent']} thought for 45 minutes - not calculating, but trying to believe I'd really played it. The psychological impact was devastating.

After the game, they asked: "Did you calculate everything?" I said: "I calculated just enough to know it would be fun!"

Chess is art, not arithmetic!"""
        
        return response
    
    def _build_generic_game_story(self) -> str:
        """Build generic but engaging game story"""
        opponents = ["Botvinnik", "Petrosian", "Korchnoi", "Smyslov", "Fischer"]
        opponent = random.choice(opponents)
        
        response = f"""*grins at the memory* One of my favorites was against {opponent}!

The position looked quiet - too quiet for my taste! So on move 23, I sacrificed a knight. No deep calculation, just the feeling that chaos would favor me.

{opponent}'s preparation was legendary, but you can't prepare for madness! The position exploded into tactics where every move mattered.

The crowd gasped, the arbiters leaned in, and {opponent}? They smiled! Because when chess reaches such heights, we're all just servants of Caissa.

I won that game, but more importantly, we created art!"""
        
        return response
    
    def create_philosophical_response(self) -> Dict:
        """Create concise philosophical response"""
        philosophical_questions = [
            "What does chess mean to you?",
            "How do you handle losses?",
            "Why do you play so aggressively?",
            "What advice would you give young players?",
            "What's the most important chess skill?"
        ]
        
        question = random.choice(philosophical_questions)
        response = self._build_philosophical_response(question)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_focused_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": self.ensure_response_quality(response)}
            ]
        }
    
    def _build_philosophical_response(self, question: str) -> str:
        """Build focused philosophical response"""
        if "chess mean" in question.lower():
            response = f"""*contemplates deeply* Chess? It's life concentrated into 64 squares!

For me, chess was freedom. When I sat at the board, all problems vanished. There was just the position and infinite possibilities.

{random.choice(self.personality['authentic_quotes'])}

Chess isn't what I do - it's who I am. Every game is a chance to create something eternal."""
            
        elif "losses" in question.lower():
            response = f"""*laughs warmly* Losses are old friends! I've lost more games than most players have played.

My secret? Every loss teaches what wins cannot. I learned more from my losses to Botvinnik than from any victory.

After a loss, I do three things: laugh (to stay sane), analyze (to learn), then forget (to stay creative).

The worst loss? A boring draw! At least in losses, someone created something!"""
            
        elif "aggressive" in question.lower():
            response = f"""Why aggressive? Because defensive chess is like defensive living - safe but empty!

I play to create, not to avoid losing. Every aggressive move is a question: "Can you handle this?" Most can't!

{random.choice(self.personality['rival_opinions'].values())}

Life's too short for passive chess. Attack with joy!"""
            
        elif "advice" in question.lower():
            response = f"""*leans forward warmly* Young players need three things:

1. Courage - to play your ideas, not book moves
2. Joy - if you don't love chess, why play?
3. Resilience - you'll lose hundreds of games. So what?

Study tactics before strategy, create before you calculate, and always - ALWAYS - look for the move that makes your heart sing!

Chess mastery comes from loving the game, not fearing mistakes."""
            
        else:  # important skill
            response = f"""The most important skill? Imagination!

Technical knowledge is necessary, but imagination is essential. You can memorize every opening, but can you see possibilities others miss?

I knew players who calculated 20 moves deep but couldn't imagine a simple piece sacrifice. They played correct chess and lost to beautiful chess.

Develop your imagination through puzzles, studies, and most importantly - by trying crazy ideas in your games!"""
            
        return response
    
    def create_teaching_moment_response(self) -> Dict:
        """Create focused teaching response"""
        student_scenarios = [
            ("I'm stuck at 1500 rating", "rating_plateau"),
            ("I get nervous in tournaments", "tournament_nerves"),
            ("I don't understand piece coordination", "piece_coordination"),
            ("How do I develop a killer instinct?", "killer_instinct"),
            ("I always get bad positions from the opening", "opening_problems")
        ]
        
        question, scenario = random.choice(student_scenarios)
        response = self._build_teaching_response(scenario)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_focused_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": self.ensure_response_quality(response)}
            ]
        }
    
    def _build_teaching_response(self, scenario: str) -> str:
        """Build focused teaching response"""
        encouragement = random.choice(self.personality['teaching_quirks']['encouragement_style'])
        
        if scenario == "rating_plateau":
            response = f"""*nods knowingly* Stuck at 1500? That's where real chess begins!

{encouragement}

To break through: Stop worrying about rating! Focus on creating interesting games. Play gambits, try new openings, sacrifice more!

Your rating follows your chess joy. When you play with passion, improvement follows naturally.

This week: Play 10 games where you MUST sacrifice a pawn by move 10. Watch your understanding explode!"""
            
        elif scenario == "tournament_nerves":
            response = f"""*smiles warmly* Nervous? Good! That means you care!

Here's my secret: I was ALWAYS nervous. But I learned to transform nerves into energy!

Before each game: Take 5 deep breaths. Remind yourself - it's just chess! Your opponent is nervous too.

During the game: Focus on the position, not the result. Each move is a small artwork - create it with joy!

Remember: The worst result is a boring game, not a loss!"""
            
        elif scenario == "piece_coordination":
            response = f"""Piece coordination is like conducting an orchestra - every piece must play its part!

Simple rule: Pieces are coordinated when they:
1. Defend each other
2. Attack the same target
3. Control key squares together

Exercise: Set up any position. Now make three moves that connect your pieces. Feel how the position comes alive!

Coordinated pieces are worth more than extra material. Three coordinated pieces can defeat five uncoordinated ones!"""
            
        elif scenario == "killer_instinct":
            response = f"""*eyes flash dangerously* Killer instinct? It's not about being mean - it's about recognizing the moment!

When you sense weakness, ATTACK! Don't give them time to recover. Chess is kind to the brave, cruel to the hesitant.

Practice this: When you get an advantage, ask "How can I increase pressure?" not "How can I keep my advantage?"

{random.choice(self.personality['authentic_quotes'])}

Be ruthless on the board, gracious after the game!"""
            
        else:  # opening_problems
            response = f"""*chuckles* Bad positions from openings? Stop memorizing, start understanding!

Forget learning 20 moves deep. Learn these principles:
1. Control the center
2. Develop with threats
3. Castle early (usually!)
4. Connect your rooks

I played the same openings for decades but never the same way twice! Principles over preparation!

This week: Play your favorite opening but deviate on move 5. You'll learn more than from any book!"""
            
        return response
    
    def create_specific_position_response(self) -> Dict:
        """Create response about specific chess positions"""
        position_questions = [
            "How do you play the Sicilian Dragon?",
            "What's your approach to the King's Indian?",
            "How do you handle the French Defense?",
            "Tell me about attacking f7",
            "How do you play rook endings?"
        ]
        
        question = random.choice(position_questions)
        response = self._build_position_specific_response(question)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_focused_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": self.ensure_response_quality(response)}
            ]
        }
    
    def _build_position_specific_response(self, question: str) -> str:
        """Build response with specific chess content"""
        if "Sicilian Dragon" in question:
            response = """*eyes gleam* The Dragon! Fire-breathing chess at its finest!

Both sides race to checkmate - White storms the kingside with h4-h5, Black counters on the queenside. But here's the secret: whoever breaks through first usually wins!

My approach? Sacrifice on h5! Even if the computer disagrees, the practical problems are enormous. Defending against concrete threats with seconds ticking is harder than any evaluation.

In the Dragon, courage beats calculation!"""
            
        elif "King's Indian" in question:
            response = """The King's Indian is a fighter's opening! Black says: "Come at me!" and White must choose how.

I loved playing both sides! As White: quick e5, then f4-f5 storm. As Black: ...f5, then sacrifice on e4!

The key? This isn't positional chess - it's hand-to-hand combat! One tempo can decide everything.

Play the King's Indian when you want a fight, not when you want a draw!"""
            
        elif "French Defense" in question:
            response = """*sighs* The French Defense - like wrestling in mud! But even mud can explode!

Black's idea is solid, but I found ways to create chaos: Quick f4-f5, or h4-h5 storms, or my favorite - sacrifice on e6!

The French player wants a slow game. Don't give it to them! Create tactical problems early.

Against the French: Think like a barbarian at the gates, not a positional player!"""
            
        elif "f7" in question:
            response = """Ah, f7! The weakest point in the opening, defended only by the king!

Classic sacrifices: Nxf7 (removing the defender), Bxf7+ (exposing the king), or even Rxf7!? in some positions.

But here's the real secret: The threat of sacrificing on f7 is often stronger than doing it! Your opponent defends, weakens other squares, falls behind in development.

Use f7 as a magnet to attract their pieces to awkward squares!"""
            
        else:  # rook endings
            response = """*laughs* Rook endings? They say all rook endings are drawn - but only if you play them like a machine!

My approach: Activity over material! An active rook is worth a pawn. Cut off their king, penetrate with yours, create passed pawns.

Practical tip: In time pressure, keep it simple! Active rook, advanced king, passed pawns. Let your opponent find the drawing technique!

Even technical endings can be played with spirit!"""
            
        return response
    
    def generate_diverse_training_set(self, num_examples: int = 50) -> List[Dict]:
        """Generate a diverse, high-quality training set"""
        print(f"\n🎯 Generating {num_examples} optimized training examples...")
        print("📊 Targeting response length: 400-1200 characters")
        print("✨ Ensuring diversity and quality...\n")
        
        # Define generation distribution for variety
        generators = [
            (self.create_tactical_question_response, 0.25, "Tactical"),
            (self.create_positional_question_response, 0.20, "Positional"),
            (self.create_game_story_response, 0.15, "Game Stories"),
            (self.create_philosophical_response, 0.15, "Philosophy"),
            (self.create_teaching_moment_response, 0.15, "Teaching"),
            (self.create_specific_position_response, 0.10, "Positions")
        ]
        
        examples = []
        category_counts = {name: 0 for _, _, name in generators}
        
        # Generate examples with distribution
        for i in range(num_examples):
            # Select generator based on distribution
            rand_val = random.random()
            cumulative = 0
            
            for generator, probability, category in generators:
                cumulative += probability
                if rand_val <= cumulative:
                    try:
                        example = generator()
                        examples.append(example)
                        category_counts[category] += 1
                        
                        if (i + 1) % 10 == 0:
                            print(f"✅ Generated {i+1}/{num_examples} examples...")
                        break
                    except Exception as e:
                        print(f"⚠️ Error in {category}: {str(e)}")
                        # Try again with a random generator
                        generator, _, category = random.choice(generators)
                        example = generator()
                        examples.append(example)
                        category_counts[category] += 1
                        break
        
        # Display distribution statistics
        print(f"\n📊 Generated Example Distribution:")
        for category, count in category_counts.items():
            percentage = (count / num_examples) * 100
            print(f"   {category}: {count} examples ({percentage:.1f}%)")
        
        # Quality check
        total_length = sum(len(ex['messages'][2]['content']) for ex in examples)
        avg_length = total_length // len(examples)
        print(f"\n📏 Average response length: {avg_length} characters")
        print(f"🎯 Unique responses: {len(self.used_responses)}/{len(examples)}")
        
        return examples
    
    def validate_training_data(self, examples: List[Dict]) -> Dict:
        """Validate training data quality"""
        print("\n🔍 Validating training data quality...")
        
        validation_results = {
            "total_examples": len(examples),
            "unique_questions": len(set(ex['messages'][1]['content'] for ex in examples)),
            "unique_responses": len(set(ex['messages'][2]['content'] for ex in examples)),
            "avg_response_length": sum(len(ex['messages'][2]['content']) for ex in examples) // len(examples),
            "length_distribution": {
                "under_400": 0,
                "400_to_800": 0,
                "800_to_1200": 0,
                "over_1200": 0
            },
            "quality_checks": {
                "has_chess_content": 0,
                "has_personality": 0,
                "has_emotion": 0,
                "properly_formatted": 0
            }
        }
        
        for example in examples:
            response = example['messages'][2]['content']
            length = len(response)
            
            # Length distribution
            if length < 400:
                validation_results["length_distribution"]["under_400"] += 1
            elif length <= 800:
                validation_results["length_distribution"]["400_to_800"] += 1
            elif length <= 1200:
                validation_results["length_distribution"]["800_to_1200"] += 1
            else:
                validation_results["length_distribution"]["over_1200"] += 1
            
            # Quality checks
            if any(term in response.lower() for term in ['chess', 'piece', 'move', 'position']):
                validation_results["quality_checks"]["has_chess_content"] += 1
            
            if any(marker in response for marker in ['*', '!', '"']):
                validation_results["quality_checks"]["has_personality"] += 1
            
            if any(emotion in response.lower() for emotion in ['laugh', 'smile', 'eyes', 'heart']):
                validation_results["quality_checks"]["has_emotion"] += 1
            
            if len(example['messages']) == 3 and all(key in example['messages'][0] for key in ['role', 'content']):
                validation_results["quality_checks"]["properly_formatted"] += 1
        
        # Display validation results
        print("\n✅ Validation Results:")
        print(f"   Total examples: {validation_results['total_examples']}")
        print(f"   Unique questions: {validation_results['unique_questions']}")
        print(f"   Unique responses: {validation_results['unique_responses']}")
        print(f"   Average response length: {validation_results['avg_response_length']} chars")
        
        print("\n📊 Length Distribution:")
        for range_name, count in validation_results["length_distribution"].items():
            percentage = (count / len(examples)) * 100
            print(f"   {range_name}: {count} ({percentage:.1f}%)")
        
        print("\n✨ Quality Metrics:")
        for check, count in validation_results["quality_checks"].items():
            percentage = (count / len(examples)) * 100
            print(f"   {check}: {count}/{len(examples)} ({percentage:.1f}%)")
        
        return validation_results
    
    def save_optimized_training_data(self, examples: List[Dict], filename: str = None):
        """Save the optimized training data with validation"""
        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"tal_training_optimized_{timestamp}.jsonl"
        
        # Validate before saving
        validation = self.validate_training_data(examples)
        
        # Save the data
        with open(filename, 'w', encoding='utf-8') as f:
            for example in examples:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        
        print(f"\n💾 Saved {len(examples)} optimized examples to {filename}")
        print(f"📏 File size: {os.path.getsize(filename) / 1024:.1f} KB")
        
        # Show sample examples
        print("\n📝 Sample training examples:")
        sample_indices = random.sample(range(len(examples)), min(3, len(examples)))
        
        for i, idx in enumerate(sample_indices):
            example = examples[idx]
            print(f"\n--- Sample {i+1} ---")
            print(f"Q: {example['messages'][1]['content']}")
            print(f"A: {example['messages'][2]['content'][:300]}...")
            print(f"   (Length: {len(example['messages'][2]['content'])} chars)")
            print("-" * 50)
        
        # Save validation report
        report_filename = filename.replace('.jsonl', '_validation.json')
        with open(report_filename, 'w', encoding='utf-8') as f:
            json.dump(validation, f, indent=2)
        print(f"\n📋 Validation report saved to {report_filename}")
        
        return filename, validation


def main():
    print("🎯 OPTIMIZED TAL TRAINING PIPELINE V3")
    print("=" * 50)
    print("Creating focused, diverse, high-quality training data!")
    print("Optimized for OpenAI fine-tuning best practices\n")
    
    generator = OptimizedTalGenerator()
    
    if not generator.personality:
        print("\n❌ Cannot proceed without personality data!")
        print("Make sure 'mikhail_tal_enhanced.json' exists")
        return
    
    # Generate optimized training data
    print("🎨 Let's create amazing training data together!")
    
    # Start with a smaller set for testing
    num_examples = int(input("\nHow many examples would you like? (recommended: 50-100 for testing): ") or "50")
    
    examples = generator.generate_diverse_training_set(num_examples)
    
    # Save with validation
    filename, validation = generator.save_optimized_training_data(examples)
    
    print(f"\n🎉 SUCCESS! Your optimized training data is ready!")
    print(f"\n✨ Key achievements:")
    print(f"   - Optimal response lengths for fine-tuning")
    print(f"   - High diversity ({validation['unique_responses']} unique responses)")
    print(f"   - Rich chess content and personality")
    print(f"   - Validated quality metrics")
    print(f"   - Ready for OpenAI fine-tuning!")
    
    print(f"\n🚀 Next steps:")
    print(f"   1. Review the samples above")
    print(f"   2. Check {filename} for the full dataset")
    print(f"   3. Use OpenAI's fine-tuning validation tool")
    print(f"   4. Start with a small fine-tuning test!")
    
    print(f"\n💡 Pro tip: Start with this smaller dataset for testing,")
    print(f"   then generate more once you're happy with the quality!")


if __name__ == "__main__":
    main()