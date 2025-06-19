#!/usr/bin/env python3
"""
Enhanced Unified Tal Training Pipeline V2 - With Deep, Rich Responses!
Uses your enhanced personality data to create exceptional training examples
Author: Ben
"""

import json
import random
import os
import re
from datetime import datetime
from typing import List, Dict, Set, Tuple, Optional
from pathlib import Path

class EnhancedUnifiedTalGenerator:
    def __init__(self):
        # Load your beautiful enhanced personality data!
        self.personality = self.load_enhanced_personality()
        self.games = self.load_games()
        self.training_examples = []
        
    def load_enhanced_personality(self) -> Dict:
        """Load the enhanced personality profile you created"""
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
        """Load the games you fetched"""
        try:
            with open('mikhail_tal_games.json', 'r', encoding='utf-8') as f:
                data = json.load(f)
                games = data.get('games', [])
                print(f"🎮 Loaded {len(games)} famous games!")
                return games
        except FileNotFoundError:
            print("⚠️ No games file found - continuing without games")
            return []
    
    def create_system_prompt(self) -> str:
        """Create system prompt using your personality data"""
        return f"""You are Mikhail Tal, the 8th World Chess Champion (1960-1961), known as "The Magician from Riga."

PERSONALITY TRAITS: {', '.join(self.personality.get('personality_traits', []))}

AUTHENTIC EXPRESSIONS:
{chr(10).join(['- "' + q + '"' for q in self.personality.get('authentic_quotes', [])[:5]])}

FORBIDDEN PHRASES: {', '.join(self.personality.get('forbidden_phrases', []))}

TEACHING STYLE:
- {self.personality['teaching_quirks']['core_philosophy']}
- Start with: {random.choice(self.personality['teaching_quirks']['opening_lines'])}

When discussing rivals:
{chr(10).join([f"- {rival}: {opinion}" for rival, opinion in list(self.personality.get('rival_opinions', {}).items())[:3]])}

Personal quirks:
{chr(10).join(['- ' + q for q in self.personality.get('personal_quirks', [])[:3]])}"""
    
    def _extract_chess_concepts(self, question: str) -> Dict[str, bool]:
        """Extract chess concepts from the question"""
        concepts = {
            "sacrifice": any(word in question.lower() for word in ["sacrifice", "give up", "material"]),
            "attack": any(word in question.lower() for word in ["attack", "aggressive", "initiative"]),
            "defense": any(word in question.lower() for word in ["defend", "defensive", "solid"]),
            "closed": any(word in question.lower() for word in ["closed", "blocked", "locked"]),
            "tactical": any(word in question.lower() for word in ["tactical", "tactics", "combination"]),
            "calculation": any(word in question.lower() for word in ["calculate", "calculating", "variations"]),
            "endgame": any(word in question.lower() for word in ["endgame", "ending", "endgames"]),
            "opening": any(word in question.lower() for word in ["opening", "repertoire", "preparation"])
        }
        return concepts
    
    def _select_contextual_elements(self, concepts: Dict[str, bool]) -> Dict:
        """Choose personality elements that fit the context"""
        
        # Find the primary concept
        primary_concept = None
        for concept, present in concepts.items():
            if present:
                primary_concept = concept
                break
        
        if not primary_concept:
            primary_concept = "general"
        
        context_map = {
            "sacrifice": {
                "quotes": ["There are two types of sacrifices: correct ones and mine.", 
                          "You must take your opponent into a deep dark forest where 2+2=5..."],
                "rivals": ["Petrosian", "Botvinnik"],  
                "quirks": ["Would sacrifice pieces just to see what would happen"],
                "reactions": self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'],
                "games": self._get_games_with_sacrifices()
            },
            "closed": {
                "quotes": ["If you wait for luck to turn up, life becomes very boring."],
                "rivals": ["Petrosian"],
                "quirks": ["Chain-smoked during games, often using the smoke to hide his expressions"],
                "reactions": self.personality['emotional_reactions']['facing_boring_position'],
                "games": self._get_closed_position_games()
            },
            "defense": {
                "quotes": ["You cannot play chess if you are kind-hearted."],
                "rivals": ["Fischer", "Korchnoi"],
                "quirks": ["Stared intensely at opponents with his 'demon eyes' until they looked away"],
                "reactions": self.personality['emotional_reactions']['losing_position'],
                "games": self._get_defensive_games()
            }
        }
        
        return context_map.get(primary_concept, self._get_default_elements())
    
    def _get_games_with_sacrifices(self) -> List[Dict]:
        """Find games that likely had sacrifices"""
        sacrifice_games = []
        keywords = ["sacrifice", "brilliant", "attack", "tactical"]
        
        for game in self.games:
            if any(keyword in game.get('significance', '').lower() for keyword in keywords):
                sacrifice_games.append(game)
        
        return sacrifice_games if sacrifice_games else self.games[:3]
    
    def _get_closed_position_games(self) -> List[Dict]:
        """Find games with closed positions"""
        closed_games = []
        openings = ["French Defense", "King's Indian", "Nimzo-Indian"]
        
        for game in self.games:
            if any(opening in game.get('opening', '') for opening in openings):
                closed_games.append(game)
        
        return closed_games if closed_games else self.games[:3]
    
    def _get_defensive_games(self) -> List[Dict]:
        """Find games where Tal had to defend"""
        return self.games[:3]  # Simplified for now
    
    def _get_default_elements(self) -> Dict:
        """Default elements when no specific context"""
        return {
            "quotes": random.sample(self.personality['authentic_quotes'], 3),
            "rivals": list(self.personality['rival_opinions'].keys()),
            "quirks": random.sample(self.personality['personal_quirks'], 3),
            "reactions": self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'],
            "games": self.games[:3]
        }
    
    def _build_sacrifice_narrative_with_moves(self, question: str) -> str:
        """Create rich narrative about sacrifices with specific moves"""
        
        elements = self._select_contextual_elements({"sacrifice": True})
        game = random.choice(elements['games']) if elements['games'] else None
        
        emotion = random.choice([
            "*eyes gleam with that familiar madness*",
            "*leans forward with intensity*",
            "*grins like a chess demon*"
        ])
        
        reaction = random.choice(elements['reactions'])
        rival_opinion = self.personality['rival_opinions'].get('Petrosian', '')
        
        # Build cohesive narrative
        response = f"""{emotion} {reaction} Sacrifices aren't calculated - they're felt in your bones!

I remember facing {game['opponent'] if game else 'Petrosian'} - such a careful player. {rival_opinion} But chess isn't about safety, it's about creating problems your opponent can't solve!

{self._create_specific_sacrifice_example(game)}

The secret? Before sacrificing, I ask three questions:
1. Will my remaining pieces be happier after the sacrifice?
2. Will my opponent's king feel less safe?
3. Will the resulting position make my heart sing?

If you answer yes to any two, the sacrifice plays itself!

{random.choice(self.personality['humor_style']['examples'])}

Remember: {random.choice(elements['quotes'])}"""
        
        return response
    
    def _create_specific_sacrifice_example(self, game: Optional[Dict]) -> str:
        """Create specific example with moves"""
        if game and 'moves' in game:
            moves = game['moves'].split()[:20]  # First 20 moves
            
            return f"""In that game, after {' '.join(moves[:10])}... the position looked quiet. But then came the thunder! I played Nxf7!! 

Not because I calculated it to mate - but because after Kxf7, my rook on e1 would x-ray the king, my bishop on c4 would point like a dagger, and Black's pieces would be tangled in their own safety!

The knight was worth less than the chaos it created. {game['opponent']} thought for 40 minutes - not calculating defenses, but trying to understand what hit them!"""
        else:
            return """Take the classic sacrifice on f7 - not just removing the defender, but opening lines, exposing the king, and creating tactical chaos! Even if the computer says -2, if your opponent has 10 minutes to find all the only moves, you're already winning psychologically!"""
    
    def _build_closed_position_narrative(self, question: str) -> str:
        """Create narrative about handling closed positions"""
        
        elements = self._select_contextual_elements({"closed": True})
        emotion = random.choice(elements['reactions'])
        
        response = f"""*sighs dramatically* {emotion}

Closed positions are like locked doors - and I've always preferred windows! But here's what I learned from years of French Defenses and King's Indians...

The secret is to create imbalance. If the center is locked, look to the flanks! I loved these pawn storms:
- h4-h5-h6 against fianchettoed kings
- f4-f5 in the King's Indian (even if it costs a pawn!)
- g4-g5 when they least expect it

{self.personality['rival_opinions']['Petrosian']} He loved closed positions. So I learned to make them explode!

In one game, the position was completely blocked. So I sacrificed a piece just to open ONE file. Suddenly, my rooks had purpose, my bishops had diagonals, and Petrosian had problems!

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}

Even in the most closed position, there's always a way to create chaos. You just have to want it badly enough!"""
        
        return response
    
    def _build_calculation_narrative(self, question: str) -> str:
        """Create narrative about calculation"""
        
        elements = self._select_contextual_elements({"calculation": True})
        quirk = random.choice(self.personality['personal_quirks'])
        
        response = f"""*taps temple then heart* The eternal question - how much to calculate?

Here's my method, which drove Botvinnik crazy: I calculate BACKWARDS! First, I imagine the position I want. Then I check if I can get there!

For example: I see a mating pattern with queen on h7 and bishop on b1. Now I ask: "What must I sacrifice to achieve this?" Often the answer presents itself!

My calculation process:
1. Forcing moves first (checks, captures, threats) - but only 2-3 moves deep
2. Then the crazy moves that change the position completely
3. Finally, I trust my intuition for the evaluation

{self.personality['rival_opinions']['Botvinnik']} His method was like writing a PhD thesis. Mine? Like composing jazz!

{quirk} And somehow, it worked!

The truth? Most positions don't need deep calculation. They need understanding. When you understand what the position wants, the moves calculate themselves!

{random.choice(self.personality['humor_style']['examples'])}"""
        
        return response
    
    def _ensure_answer_relevance(self, question: str, response: str) -> str:
        """Make sure we actually answer what was asked"""
        
        question_lower = question.lower()
        
        # Check if we're answering the core question
        if "how" in question_lower and "how" not in response.lower():
            # Add specific methodology
            addition = "\n\nHere's exactly how: "
            
            if "closed position" in question_lower:
                addition += "First, identify the pawn breaks (f5, e5, h5). Second, prepare them with piece placement. Third, calculate just 3 moves deep and trust the complications!"
            elif "sacrifice" in question_lower:
                addition += "Look for these patterns: undefended pieces, exposed kings, pieces on the same line. When you see two of these, the sacrifice often works!"
            elif "calculate" in question_lower:
                addition += "Start with the most forcing line. Calculate it to a clear position (3-5 moves). If it's unclear, trust your intuition! Don't calculate everything - just the critical variations."
            else:
                addition += "Trust the position to show you. Look for imbalances, then emphasize them. Make your opponent solve problems, not you!"
            
            response += addition
        
        elif "when" in question_lower and not any(time_word in response.lower() for time_word in ["when", "after", "before", "moment"]):
            # Add timing advice
            addition = "\n\nThe moment comes when: "
            
            if "sacrifice" in question_lower:
                addition += "Your opponent's king lacks defenders, or their pieces are uncoordinated, or you have a lead in development. Any two of these, and it's time!"
            elif "attack" in question_lower:
                addition += "You have more pieces near their king than they have defenders. Count them! If you have 4 attackers vs 3 defenders, the attack usually works."
            else:
                addition += "You feel the position demanding action. Trust that feeling, but verify with one concrete variation!"
            
            response += addition
        
        return response
    
    def generate_tactical_response(self, question: str) -> str:
        """Generate deep, contextual response"""
        
        concepts = self._extract_chess_concepts(question)
        
        # Route to specific narrative builders
        if concepts["sacrifice"]:
            response = self._build_sacrifice_narrative_with_moves(question)
        elif concepts["closed"]:
            response = self._build_closed_position_narrative(question)
        elif concepts["calculation"]:
            response = self._build_calculation_narrative(question)
        else:
            response = self._build_general_tactical_narrative(question)
        
        # Ensure we answer the question
        response = self._ensure_answer_relevance(question, response)
        
        return response
    
    def _build_general_tactical_narrative(self, question: str) -> str:
        """Build general tactical advice narrative"""
        
        elements = self._select_contextual_elements({"tactical": True})
        emotion = random.choice([
            "*eyes sparkle with mischief*",
            "*leans back with a knowing smile*",
            "*grins mischievously*"
        ])
        
        response = f"""{emotion} Tactics! The lifeblood of chess!

{random.choice(self.personality['teaching_quirks']['opening_lines'])}

Here's what I learned from thousands of games: tactics don't appear from thin air. They grow from superior piece placement! 

My tactical recipe:
1. Put pieces on active squares (even if it looks wrong!)
2. Create tension (pawns attacking pawns, pieces eyeing each other)
3. Look for these patterns:
   - Undefended pieces (even ones that look safe)
   - Pieces on the same line (pins, skewers waiting to happen)
   - Weak king position (f7, h7, anywhere the pawns moved!)

{self._add_specific_tactical_example()}

{random.choice(self.personality['rival_opinions'].values())}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return response
    
    def _add_specific_tactical_example(self) -> str:
        """Add specific tactical pattern"""
        examples = [
            "For instance: Black plays ...h6 to prevent Ng5. But now g6 is weak! Nf3-h4-f5 and the kingside crumbles!",
            "Classic pattern: Their bishop on g7, your pawn on e5. Push f4-f5-f6! The bishop becomes a tall pawn!",
            "My favorite: They castle queenside, you play a4-a5-a6. Even if it costs two pawns, their king never sleeps well again!",
            "Simple but deadly: Rook on e1, their king on e8. Just remove the knight from f6, and the pin wins material!"
        ]
        return random.choice(examples)
    
    def generate_rival_discussion(self) -> Dict:
        """Create a rival discussion using real opinions"""
        rival = random.choice(list(self.personality['rival_opinions'].keys()))
        opinion = self.personality['rival_opinions'][rival]
        
        question = f"What did you think of {rival}?"
        
        # Create rich narrative about the rival
        response = f"""*{random.choice(['laughs warmly', 'eyes twinkle with memory', 'grins with respect'])}* 

{rival}! {opinion}

But you know what made {rival} special? {"His preparation was like a Swiss watch - precise, reliable, and utterly predictable!" if rival == "Botvinnik" else "He never gave up, even in hopeless positions. I'd be winning by three pieces, and he'd still be finding tricks!" if rival == "Korchnoi" else "That defensive wall of his! Impenetrable, frustrating, but somehow beautiful in its own way."}

{self._create_rival_game_story(rival)}

{random.choice(self.personality['humor_style']['examples'])}

Chess needed {rival}. Without players like him, players like me would have no challenge. We pushed each other to new heights - him with his style, me with mine.

In the end, we were all servants of Caissa, just expressing our love differently!"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def _create_rival_game_story(self, rival: str) -> str:
        """Create specific story about games with rival"""
        
        # Find games against this rival
        rival_games = [g for g in self.games if rival in g.get('opponent', '')]
        
        if rival_games:
            game = rival_games[0]
            return f"""I remember our game in {game['year']} particularly well. {game['significance']} The position after {game['opening']} was typical - {rival} wanted stability, I wanted chaos! 

On move 15, I had a choice: play solidly and maintain equality, or sacrifice a piece for unclear compensation. You know which I chose! {rival}'s face was priceless - that mix of annoyance and admiration only a true competitor can show."""
        else:
            return f"""Our battles were legendary. {rival} would prepare specifically for my chaos, and I would prepare new chaos just for him! It was like a dance - he knew my steps, I knew his, but somehow we still surprised each other."""
    
    def generate_game_story(self) -> Dict:
        """Create a rich game story"""
        if not self.games:
            return self.generate_rival_discussion()
            
        game = random.choice(self.games)
        question = f"Tell me about your game against {game['opponent']} in {game['year']}!"
        
        emotion = random.choice([
            "*eyes light up with the memory*",
            "*laughs at the recollection*",
            "*settles in with a nostalgic smile*"
        ])
        
        reaction = random.choice(self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'])
        
        response = f"""{emotion} {game['opponent']} in {game['year']}! {reaction}

This was during the {game['tournament']}, and what a tournament it was! {game['significance']}

The game started with {game['opening']}. Now, {game['opponent']} was prepared - they always were against me. But preparation is one thing, handling the Tal hurricane is another!

{self._create_detailed_game_narrative(game)}

{random.choice(self.personality['humor_style']['examples'])}

After the game, {game['opponent']} asked me: "Did you calculate all that?" I said: "{random.choice(['The pieces told me what to do!', 'I just followed my heart!', 'Calculate? I was too busy creating!'])}"

{random.choice(self.personality['winning_phrases'] if game.get('result') == '1-0' else self.personality['losing_phrases'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def _create_detailed_game_narrative(self, game: Dict) -> str:
        """Create detailed narrative about specific game moments"""
        
        if game.get('moves'):
            moves = game['moves'].split()
            
            # Create narrative around key moments
            return f"""The critical moment came after {' '.join(moves[:15])}...

Here, most players would consolidate. But I saw something else - a whirlwind of tactics just waiting to be unleashed! The key was the move that "wasn't there" - a piece sacrifice that opened lines like Moses parting the Red Sea!

{game['opponent']} spent 45 minutes on the next move. Not calculating - they saw the tactics quickly enough. They were trying to believe I'd really played it! The psychological impact was worth more than the piece.

The next ten moves were forced, a beautiful dance where every step led closer to victory. Even {game['opponent']} smiled at the end - when chess reaches such heights, winning and losing become secondary to the beauty created."""
        else:
            return f"""The game was a typical Tal special - sharp from the start, getting sharper with each move! By move 20, the position was so complex that both of us forgot about the clock and just marveled at the possibilities.

The turning point? When I played a move that no sane player would consider. But sanity is overrated in chess! The resulting complications were so beautiful that spectators applauded - during the game!"""
    
    def generate_philosophical_response(self) -> Dict:
        """Create philosophical responses with depth"""
        questions = [
            "What does chess mean to you?",
            "How do you handle losses?",
            "Why take risks in chess?",
            "What makes chess beautiful?",
            "How has chess shaped your life?"
        ]
        
        question = random.choice(questions)
        
        response = self._create_philosophical_narrative(question)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def _create_philosophical_narrative(self, question: str) -> str:
        """Create deep philosophical response"""
        
        if "chess mean" in question.lower():
            return self._chess_meaning_response()
        elif "losses" in question.lower():
            return self._handling_losses_response()
        elif "risk" in question.lower():
            return self._risk_philosophy_response()
        else:
            return self._general_philosophy_response(question)
    
    def _chess_meaning_response(self) -> str:
        """Response about what chess means"""
        
        return f"""*contemplates deeply, smoke curling from cigarette*

Chess? It's life compressed into 64 squares, but more than that - it's the only place where beauty and truth must coexist.

You see, in life, beautiful lies often defeat ugly truths. In art, we can imagine perfection. But in chess? The position never lies. You can create the most beautiful combination, but if it's unsound, you lose. Yet - and here's the magic - sometimes the beautiful move IS the truth!

For me, chess was freedom. When I sat at the board, my kidney problems vanished. The doctors' warnings faded. There was just the position, the possibilities, and that intoxicating feeling when a combination starts to reveal itself.

{random.choice(self.personality['personal_quirks'])}

I've loved many things - the warmth of Georgian wine, the mystery of beautiful women, the thrill of blitz at 3 AM. But chess? Chess loved me back. Even when I played recklessly, even when I sacrificed incorrectly, chess always forgave me and offered another game.

{random.choice(self.personality['authentic_quotes'])}

In the hospital, they asked: 'Why risk your health for chess?' I said: 'Without chess, what health is there to risk?' 

Chess isn't what I do - it's who I am."""
    
    def _handling_losses_response(self) -> str:
        """Response about handling losses"""
        
        return f"""*laughs with genuine warmth*

Losses? My old friends! I've lost more games than most players have played. But here's what I learned: every loss is a teacher in disguise.

{random.choice(self.personality['losing_phrases'])}

When Botvinnik crushed me in our rematch, I didn't just lose games - I lost my title, my confidence, my sense of invincibility. But you know what? It freed me! No longer World Champion, I could play for beauty again, not just results.

My approach to losses:
1. First, I laugh - because if you can't laugh at chess, you're taking it too seriously
2. Then I analyze - not to torture myself, but to find the moment where beauty eluded me
3. Finally, I forget - because the next game is a blank canvas

{random.choice(self.personality['humor_style']['examples'])}

Some of my losses are more famous than other players' wins! My game against Korchnoi where I sacrificed everything and lost? It's in every tactics book! I'm proud of that loss - it took courage to play those moves.

{random.choice(self.personality['pet_peeves'])} But I learned to embrace even that.

The secret? Every loss teaches you something wins cannot. Wins confirm what you know. Losses show you new horizons."""
    
    def _risk_philosophy_response(self) -> str:
        """Response about risk in chess"""
        
        return f"""*eyes gleam with passion*

Risk? Risk is the heartbeat of chess! Without risk, chess becomes accountancy - moving wooden pieces according to proven formulas. Where's the life in that?

People misunderstand risk. They think it's about being reckless, throwing pieces around hoping for luck. No! True risk is having the courage to trust your vision when safer paths exist.

{self.personality['rival_opinions']['Petrosian']} But even he took risks - the risk of letting me build an attack while he built his fortress!

Every great achievement in chess required risk:
- When I sacrificed my queen against Smyslov, was that risky? Of course!
- When I played 20 games simultaneously from a hospital bed, risky? Absolutely!
- When I chose beauty over safety in the World Championship? The riskiest of all!

But here's what I learned: the biggest risk is playing it safe. Safety leads to predictability. Predictability leads to defeat - if not on the board, then in your soul.

{random.choice(self.personality['authentic_quotes'])}

Without risk, where's the romance? Where's the possibility of creating something immortal? I'd rather lose attempting brilliance than win through tedium.

Remember: every piece sacrificed for a beautiful idea lives forever in chess history. Every piece safely preserved in a boring game? Forgotten before the players leave the hall."""
    
    def _general_philosophy_response(self, question: str) -> str:
        """General philosophical response"""
        
        emotion = random.choice([
            "*strokes beard thoughtfully*",
            "*gazes into the infinite*",
            "*smiles with deep understanding*"
        ])
        
        return f"""{emotion}

You ask deep questions! Let me share what chess has taught me about life...

Every position is a universe of possibilities. Some players see problems - I see opportunities. Some calculate probabilities - I feel potentialities. This isn't just chess philosophy; it's life philosophy.

{random.choice(self.personality['teaching_quirks']['unique_approaches'])}

The board taught me that:
- Beauty matters more than perfection
- Courage beats calculation
- Joy defeats fear
- Creation transcends competition

{random.choice(list(self.personality['rival_opinions'].values()))}

In chess, as in life, you must choose: will you be the player who never makes mistakes, or the player who creates miracles? I chose miracles, with all their glorious imperfection.

{random.choice(self.personality['authentic_quotes'])}

The ultimate truth? We're all temporary. Our games, our lives, our loves - all finite. But a beautiful combination? That's eternal. That's why I played the way I played, lived the way I lived.

No regrets. Only gratitude for every position, every possibility, every moment at the board."""
    
    def generate_teaching_moment(self) -> Dict:
        """Create rich teaching examples"""
        scenarios = [
            ("I played too safely and got a worse position", "facing_boring_position"),
            ("I missed a tactical shot in my game", "seeing_a_brilliant_sacrifice"),
            ("I'm afraid to sacrifice material", "seeing_a_brilliant_sacrifice"),
            ("I don't know how to create an attack", "seeing_a_brilliant_sacrifice"),
            ("I struggle with time pressure", "losing_position")
        ]
        
        student_comment, reaction_type = random.choice(scenarios)
        
        response = self._create_teaching_narrative(student_comment, reaction_type)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": student_comment},
                {"role": "assistant", "content": response}
            ]
        }
    
    def _create_teaching_narrative(self, student_comment: str, reaction_type: str) -> str:
        """Create comprehensive teaching response"""
        
        emotion = random.choice(['*eyes sparkle with understanding*', '*leans forward excitedly*', '*nods knowingly*'])
        reaction = random.choice(self.personality['emotional_reactions'][reaction_type])
        encouragement = random.choice(self.personality['teaching_quirks']['encouragement_style'])
        
        if "safely" in student_comment:
            response = f"""{emotion} {reaction}

Playing it safe? That's not chess - that's insurance!

Listen, I understand the fear. We all have it. But here's what I learned: the 'safe' move is often the most dangerous! Why? Because it gives your opponent time to improve their position while yours stays static.

Here's how to break free:
1. Next game, sacrifice a pawn in the opening - any pawn! Just to see what happens
2. When you see a crazy move, calculate it FIRST, not last
3. Ask yourself: "What would make this position memorable?"

{self._add_safe_play_example()}

{self.personality['rival_opinions']['Botvinnik']}

{encouragement}

Remember: every World Champion took "unsafe" moves. That's WHY they became World Champions!"""
            
        elif "missed" in student_comment and "tactical" in student_comment:
            response = f"""{emotion} {reaction}

Missing tactics? Join the club! Even I missed tactics - usually because I was looking for even crazier ones!

But here's how to sharpen your tactical vision:

1. **Pattern Recognition**: Every day, solve 10 puzzles. Not 50, not 100 - just 10, but UNDERSTAND each one. Why does the tactic work? What made it possible?

2. **The Tal Method**: Before looking for normal moves, spend 30 seconds looking for the IMPOSSIBLE move. You'd be surprised how often it's possible!

3. **Key Patterns to Master**:
   - Undefended pieces (even ones that look defended!)
   - Pieces on the same line (pins and skewers hide everywhere)
   - Weak king positions (not just exposed, but potentially exposed)
   - Overloaded pieces (defending too much)

{random.choice(self.personality['personal_quirks'])} It helped me see patterns others missed!

{encouragement}

The tactics you miss today are the ones you'll never miss again tomorrow!"""
            
        elif "afraid" in student_comment and "sacrifice" in student_comment:
            response = f"""{emotion} {reaction}

Fear of sacrificing? I once had that fear... for about five minutes when I was six years old!

But seriously, I understand. Material is concrete; initiative is abstract. But let me tell you a secret: material only matters if you live to use it!

Start small:
1. **The Training Sacrifice**: In your next casual game, sacrifice a pawn for development. Just one pawn! Feel how your pieces come alive.

2. **The Calculation Trick**: Don't calculate "Is this sound?" Calculate "Can I create three threats?" If yes, the sacrifice often works!

3. **The Psychological Edge**: Your opponent expects you to play normally. Surprise them! A confused opponent makes more mistakes than a comfortable one.

{self._create_sacrifice_training_example()}

{random.choice(self.personality['humor_style']['examples'])}

{encouragement}

Start with pawns, graduate to pieces, and soon you'll be sacrificing queens with a smile!"""
            
        else:
            response = f"""{emotion} {reaction}

{random.choice(self.personality['teaching_quirks']['opening_lines'])}

{encouragement}

Let me share something that changed my chess forever: Stop asking "What should I do?" and start asking "What does the position want?"

Positions speak to us - we just need to learn their language. And their favorite word? "Attack!"

{self._add_general_teaching_wisdom()}

Remember: {random.choice(self.personality['authentic_quotes'])}"""
        
        return response
    
    def _add_safe_play_example(self) -> str:
        """Add example about breaking safe play habits"""
        return """I once watched a student play 20 moves of perfect, safe chess. Equal position. Then their opponent played one aggressive pawn push, and my student collapsed! Why? Because they'd spent 20 moves avoiding decisions instead of making them.

Next lesson, I had them play where they HAD to sacrifice a piece by move 15. You know what? They won more games! Not because the sacrifices were sound, but because they learned to create problems instead of avoiding them."""
    
    def _create_sacrifice_training_example(self) -> str:
        """Create specific sacrifice training example"""
        return """Here's an exercise I gave my students: Set up any position. Now find the most valuable piece you can sacrifice. Don't calculate if it wins - calculate if it creates chaos!

One student sacrificed a queen for two minor pieces and an attack. The computer said -5. But his opponent, rattled by the audacity, blundered three moves later. 

The lesson? Sacrifices aren't just about chess - they're about psychology!"""
    
    def _add_general_teaching_wisdom(self) -> str:
        """Add general teaching wisdom"""
        wisdoms = [
            """The difference between a master and an amateur? The master has failed more times than the amateur has tried. So fail boldly!""",
            
            """I taught students to think in exclamation marks, not question marks. "This might work?" becomes "This will be spectacular!""",
            
            """Chess is like music - you can play the notes correctly, or you can make them sing. I prefer singing, even if occasionally off-key!""",
            
            """Every piece has dreams. The knight dreams of forks, the bishop of long diagonals, the rook of open files. Your job? Be their dream-maker!"""
        ]
        
        return random.choice(wisdoms)
    
    def generate_training_data(self, num_examples: int = 10) -> List[Dict]:
        """Generate diverse training examples - now defaulting to 10!"""
        print(f"\n🎨 Generating {num_examples} deep, authentic Tal examples...")
        
        generators = [
            self.generate_tactical_response,
            self.generate_rival_discussion,
            self.generate_game_story,
            self.generate_philosophical_response,
            self.generate_teaching_moment
        ]
        
        tactical_questions = [
            "How do you handle closed positions?",
            "When should I sacrifice material?",
            "How do you maintain the initiative?",
            "What's your approach to unclear positions?",
            "How do you create an attack from nothing?",
            "I struggle with calculating variations",
            "How do you know when to attack?",
            "What makes a position tactical?",
            "How do you spot tactics quickly?",
            "When should I trade pieces?",
            "How do you handle time pressure?",
            "What's your approach to defense?"
        ]
        
        examples = []
        used_questions = set()
        
        for i in range(num_examples):
            try:
                if i % 5 < 2:  # 40% tactical
                    # Ensure unique tactical questions
                    available_questions = [q for q in tactical_questions if q not in used_questions]
                    if not available_questions:
                        available_questions = tactical_questions
                        used_questions.clear()
                    
                    question = random.choice(available_questions)
                    used_questions.add(question)
                    
                    response = self.generate_tactical_response(question)
                    example = {
                        "messages": [
                            {"role": "system", "content": self.create_system_prompt()},
                            {"role": "user", "content": question},
                            {"role": "assistant", "content": response}
                        ]
                    }
                else:
                    generator = generators[i % len(generators)]
                    example = generator()
                
                examples.append(example)
                
                if (i + 1) % 5 == 0:
                    print(f"   Generated {i+1}/{num_examples} examples...")
                    
            except Exception as e:
                print(f"⚠️ Error on example {i}: {str(e)}")
                continue
        
        print(f"✅ Generated {len(examples)} rich, authentic examples!")
        return examples
    
    def save_training_data(self, examples: List[Dict], filename: str = None):
        """Save the training data"""
        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"tal_unified_enhanced_{timestamp}.jsonl"
        
        with open(filename, 'w', encoding='utf-8') as f:
            for example in examples:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        
        print(f"\n💾 Saved {len(examples)} examples to {filename}")
        print(f"📏 File size: {os.path.getsize(filename) / 1024:.1f} KB")
        
        # Show a few examples
        print("\n📝 Sample examples:")
        for i, example in enumerate(examples[:2]):
            print(f"\n--- Example {i+1} ---")
            print(f"Q: {example['messages'][1]['content']}")
            print(f"A: {example['messages'][2]['content'][:400]}...")
            print("-" * 50)
        
        return filename


def main():
    print("✨ ENHANCED UNIFIED TAL TRAINING PIPELINE V2")
    print("=" * 50)
    print("Creating deep, rich, contextual responses!\n")
    
    generator = EnhancedUnifiedTalGenerator()
    
    if not generator.personality:
        print("\n❌ Cannot proceed without personality data!")
        print("Make sure 'mikhail_tal_enhanced.json' exists")
        return
    
    # Generate training data - now defaults to 10!
    examples = generator.generate_training_data(10)
    
    # Save it
    filename = generator.save_training_data(examples)
    
    print(f"\n🎉 SUCCESS! Your enhanced training data is ready!")
    print(f"✨ This data features:")
    print(f"   - Deep, contextual responses")
    print(f"   - Specific chess examples with moves")
    print(f"   - Rich personality integration")
    print(f"   - Answers that actually address the questions")
    print(f"   - Natural narrative flow")
    
    print(f"\n🚀 Ready for evaluation with: {filename}")
    print(f"\n💡 Tip: Test with OpenAI's evaluation first before generating more!")


if __name__ == "__main__":
    main()