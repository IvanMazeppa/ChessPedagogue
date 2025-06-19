#!/usr/bin/env python3
"""
ALL-IN-ONE Chess Personality Generator
Creates complete chess master personalities and training data from simple inputs
No external files required - everything built in one script!
Author: Ben (Complete self-contained version)
"""

import json
import random
import re
from datetime import datetime
from typing import List, Dict, Set, Optional, Any
import hashlib
from collections import defaultdict, Counter

class AllInOnePersonalityGenerator:
    def __init__(self):
        self.player_data = {}
        self.personality_profile = {}
        self.training_examples = []
        
        # Training parameters
        self.MIN_RESPONSE_LENGTH = 120
        self.MAX_RESPONSE_LENGTH = 300
        self.QUOTE_USAGE_RATE = 0.20
        
        # Quality tracking
        self.used_response_hashes = set()
        self.context_distribution = Counter()
        
        print("🎭 ALL-IN-ONE CHESS PERSONALITY GENERATOR")
        print("=" * 50)
        print("✨ Create authentic chess master personalities from scratch!")
        print("🚀 No external files needed - everything built right here!\n")
    
    def collect_player_data(self):
        """Interactive data collection for chess master"""
        print("📝 Let's create your chess master's personality profile!\n")
        
        # Basic info
        self.player_data['name'] = input("Chess master's name (e.g., 'Mikhail Tal'): ").strip()
        if not self.player_data['name']:
            self.player_data['name'] = "Chess Master"
        
        print(f"\n🌟 Creating personality for {self.player_data['name']}!")
        
        # Chess style
        print("\n🎯 CHESS STYLE:")
        print("1. Attacking (aggressive, tactical, sacrificial)")
        print("2. Positional (strategic, patient, structural)")
        print("3. Tactical (pattern-focused, calculating, sharp)")
        print("4. Balanced (adaptable, well-rounded, flexible)")
        print("5. Endgame Specialist (technical, precise, patient)")
        
        style_choice = input("\nSelect chess style (1-5, or type custom): ").strip()
        style_map = {
            '1': 'attacking', '2': 'positional', '3': 'tactical', 
            '4': 'balanced', '5': 'endgame_specialist'
        }
        self.player_data['chess_style'] = style_map.get(style_choice, style_choice)
        
        # Communication style
        print("\n🗣️ COMMUNICATION STYLE:")
        print("1. Enthusiastic (energetic, passionate, animated)")
        print("2. Calm (measured, thoughtful, steady)")
        print("3. Analytical (logical, systematic, precise)")
        print("4. Philosophical (wise, reflective, deep)")
        print("5. Friendly (warm, approachable, encouraging)")
        
        comm_choice = input("\nSelect communication style (1-5, or type custom): ").strip()
        comm_map = {
            '1': 'enthusiastic', '2': 'calm', '3': 'analytical',
            '4': 'philosophical', '5': 'friendly'
        }
        self.player_data['communication_style'] = comm_map.get(comm_choice, comm_choice)
        
        # Core philosophy
        print(f"\n💭 CHESS PHILOSOPHY:")
        print("What drives this master's approach to chess?")
        philosophy = input("Enter core philosophy (or press Enter for default): ").strip()
        if not philosophy:
            philosophy = f"Chess is a beautiful combination of art, science, and human struggle"
        self.player_data['core_philosophy'] = philosophy
        
        # Key quotes
        print(f"\n📚 KEY EXPRESSIONS:")
        print("Enter 2-4 authentic quotes or sayings (press Enter on empty line to finish):")
        quotes = []
        for i in range(6):  # Max 6 quotes
            quote = input(f"Quote {i+1}: ").strip()
            if not quote:
                break
            quotes.append(quote)
        
        if not quotes:
            # Provide some generic but useful quotes based on style
            quotes = self.generate_default_quotes()
        
        self.player_data['key_quotes'] = quotes
        
        # Personality traits
        print(f"\n🎭 PERSONALITY TRAITS:")
        print("Enter 3-5 key personality traits (or press Enter for auto-generated):")
        traits_input = input("Traits (comma-separated): ").strip()
        
        if traits_input:
            traits = [trait.strip() for trait in traits_input.split(',')]
        else:
            traits = self.generate_default_traits()
        
        self.player_data['personality_traits'] = traits
        
        # Decision approach
        print(f"\n🧠 DECISION MAKING:")
        print("1. Intuitive (feels the right move, pattern recognition)")
        print("2. Analytical (calculates deeply, systematic)")
        print("3. Balanced (combines intuition and calculation)")
        
        decision_choice = input("\nSelect decision approach (1-3): ").strip()
        decision_map = {'1': 'intuitive', '2': 'analytical', '3': 'balanced'}
        self.player_data['decision_approach'] = decision_map.get(decision_choice, 'balanced')
        
        print(f"\n✅ Personality profile created for {self.player_data['name']}!")
        self.display_personality_summary()
    
    def generate_default_quotes(self) -> List[str]:
        """Generate default quotes based on chess style"""
        style = self.player_data.get('chess_style', 'balanced')
        
        quote_sets = {
            'attacking': [
                "The best defense is a good attack",
                "When you see a good move, look for a better one",
                "Complications favor the better prepared mind"
            ],
            'positional': [
                "Small advantages accumulate into victory",
                "Control the center and the game controls itself", 
                "Patience is the highest virtue in chess"
            ],
            'tactical': [
                "Tactics flow from a superior position",
                "Pattern recognition is the foundation of tactical vision",
                "Calculate forcing moves first"
            ],
            'balanced': [
                "Every position demands its own unique approach",
                "Understanding trumps memorization",
                "Chess teaches us about life itself"
            ],
            'endgame_specialist': [
                "The endgame is where games are really won",
                "Technique conquers talent in the endgame",
                "King and pawn versus king - this is the foundation"
            ]
        }
        
        return quote_sets.get(style, quote_sets['balanced'])
    
    def generate_default_traits(self) -> List[str]:
        """Generate default traits based on style and communication"""
        style = self.player_data.get('chess_style', 'balanced')
        comm = self.player_data.get('communication_style', 'friendly')
        
        trait_pools = {
            'attacking': ['aggressive', 'bold', 'creative', 'intuitive'],
            'positional': ['patient', 'strategic', 'methodical', 'wise'],
            'tactical': ['sharp', 'calculating', 'precise', 'focused'],
            'balanced': ['adaptable', 'insightful', 'experienced', 'thoughtful'],
            'endgame_specialist': ['technical', 'precise', 'persistent', 'masterful']
        }
        
        comm_traits = {
            'enthusiastic': ['passionate', 'energetic'],
            'calm': ['composed', 'steady'],
            'analytical': ['logical', 'systematic'],
            'philosophical': ['wise', 'reflective'],
            'friendly': ['warm', 'encouraging']
        }
        
        base_traits = trait_pools.get(style, trait_pools['balanced'])
        additional_traits = comm_traits.get(comm, ['thoughtful'])
        
        # Combine and limit to 4 traits
        all_traits = base_traits + additional_traits
        return list(set(all_traits))[:4]
    
    def display_personality_summary(self):
        """Display the created personality profile"""
        print(f"\n🎨 PERSONALITY PROFILE: {self.player_data['name']}")
        print("=" * 50)
        print(f"♟️  Chess Style: {self.player_data['chess_style']}")
        print(f"🗣️  Communication: {self.player_data['communication_style']}")
        print(f"🧠 Decision Style: {self.player_data['decision_approach']}")
        print(f"💭 Philosophy: {self.player_data['core_philosophy']}")
        print(f"🎭 Traits: {', '.join(self.player_data['personality_traits'])}")
        print(f"📚 Key Quotes ({len(self.player_data['key_quotes'])}):")
        for i, quote in enumerate(self.player_data['key_quotes'], 1):
            print(f"   {i}. \"{quote}\"")
        print("=" * 50)
    
    def build_personality_profile(self):
        """Build the internal personality profile for training generation"""
        self.personality_profile = {
            'core_identity': {
                'name': self.player_data['name'],
                'chess_style': self.player_data['chess_style'],
                'communication_style': self.player_data['communication_style'],
                'decision_approach': self.player_data['decision_approach']
            },
            'expression_patterns': {
                'energy_level': self.infer_energy_level(),
                'teaching_style': self.infer_teaching_style(),
                'emotional_tone': self.infer_emotional_tone()
            },
            'chess_philosophy': {
                'core_belief': self.player_data['core_philosophy'],
                'approach_principles': self.generate_approach_principles(),
                'teaching_priorities': self.generate_teaching_priorities()
            },
            'authentic_expressions': self.player_data['key_quotes'],
            'personality_traits': self.player_data['personality_traits']
        }
        
        print(f"🧠 Built comprehensive personality profile for {self.player_data['name']}")
    
    def infer_energy_level(self) -> str:
        """Infer energy level from communication style and traits"""
        comm_style = self.player_data['communication_style']
        traits = self.player_data['personality_traits']
        
        high_energy_indicators = ['enthusiastic', 'passionate', 'energetic', 'bold', 'aggressive']
        low_energy_indicators = ['calm', 'methodical', 'patient', 'steady', 'composed']
        
        high_score = sum(1 for trait in traits + [comm_style] if trait in high_energy_indicators)
        low_score = sum(1 for trait in traits + [comm_style] if trait in low_energy_indicators)
        
        if high_score > low_score:
            return 'high'
        elif low_score > high_score:
            return 'low'
        else:
            return 'moderate'
    
    def infer_teaching_style(self) -> str:
        """Infer teaching approach from personality"""
        comm_style = self.player_data['communication_style']
        traits = self.player_data['personality_traits']
        
        if comm_style == 'enthusiastic' or 'passionate' in traits:
            return 'inspiring'
        elif comm_style == 'analytical' or 'systematic' in traits:
            return 'methodical'
        elif comm_style == 'philosophical' or 'wise' in traits:
            return 'contemplative'
        elif comm_style == 'friendly' or 'encouraging' in traits:
            return 'supportive'
        else:
            return 'balanced'
    
    def infer_emotional_tone(self) -> str:
        """Infer overall emotional approach"""
        chess_style = self.player_data['chess_style']
        comm_style = self.player_data['communication_style']
        
        if chess_style == 'attacking' and comm_style == 'enthusiastic':
            return 'excited'
        elif chess_style == 'positional' and comm_style == 'calm':
            return 'serene'
        elif comm_style == 'analytical':
            return 'focused'
        elif comm_style == 'philosophical':
            return 'reflective'
        else:
            return 'warm'
    
    def generate_approach_principles(self) -> List[str]:
        """Generate chess approach principles based on style"""
        style = self.player_data['chess_style']
        
        principles = {
            'attacking': [
                "Create threats and maintain initiative",
                "Sacrifice material for attacking chances when justified", 
                "Keep the opponent's king in the center when possible",
                "Look for tactical shots in every position"
            ],
            'positional': [
                "Improve your worst-placed piece first",
                "Control key squares and important files",
                "Small advantages accumulate over time",
                "Understand pawn structures and their implications"
            ],
            'tactical': [
                "Calculate forcing moves systematically",
                "Practice pattern recognition daily",
                "Look for pins, forks, and discovered attacks",
                "Trust your tactical instincts but verify with calculation"
            ],
            'balanced': [
                "Adapt your style to what the position demands",
                "Balance tactical awareness with strategic planning",
                "Every position has its own unique requirements",
                "Flexibility and adaptation are strengths"
            ],
            'endgame_specialist': [
                "Master fundamental endgame positions",
                "Activity trumps material in many endgames",
                "King and pawn endgames are the foundation",
                "Technique and precision decide close endgames"
            ]
        }
        
        return principles.get(style, principles['balanced'])
    
    def generate_teaching_priorities(self) -> List[str]:
        """Generate teaching priorities based on style and approach"""
        decision_approach = self.player_data['decision_approach']
        teaching_style = self.personality_profile['expression_patterns']['teaching_style']
        
        priorities = []
        
        if decision_approach == 'intuitive':
            priorities.extend([
                "Develop pattern recognition through practice",
                "Trust your instincts but understand why they work"
            ])
        elif decision_approach == 'analytical':
            priorities.extend([
                "Build systematic calculation skills",
                "Learn to evaluate positions objectively"
            ])
        else:
            priorities.extend([
                "Balance intuition with concrete analysis",
                "Develop both pattern recognition and calculation"
            ])
        
        if teaching_style == 'inspiring':
            priorities.append("Find the beauty and artistry in every position")
        elif teaching_style == 'methodical':
            priorities.append("Build skills step by step with systematic practice")
        elif teaching_style == 'contemplative':
            priorities.append("Understand the deeper principles behind each move")
        else:
            priorities.append("Enjoy the journey of chess improvement")
        
        return priorities
    
    def create_system_prompt(self) -> str:
        """Create optimized system prompt for fine-tuning"""
        profile = self.personality_profile
        core = profile['core_identity']
        expression = profile['expression_patterns']
        philosophy = profile['chess_philosophy']
        
        prompt = f"""You are {core['name']}, the legendary chess grandmaster.

PERSONALITY: {', '.join(profile['personality_traits'])}
CHESS STYLE: {core['chess_style']} with {core['decision_approach']} decision-making
COMMUNICATION: {core['communication_style']} and {expression['teaching_style']} teaching approach

CORE PHILOSOPHY: {philosophy['core_belief']}

KEY EXPRESSIONS (use contextually, not repetitively):
{chr(10).join(['- "' + quote + '"' for quote in profile['authentic_expressions']])}

CHESS APPROACH PRINCIPLES:
{chr(10).join(['- ' + principle for principle in philosophy['approach_principles']])}

RESPONSE GUIDELINES:
- Embody my authentic {expression['emotional_tone']} personality naturally
- Express my {core['chess_style']} approach to chess organically  
- Show {expression['energy_level']} energy level in communication
- Keep responses 120-300 words for natural conversation flow
- Use function calls when engine analysis would strengthen commentary
- Share genuine chess wisdom from my unique perspective

AVAILABLE FUNCTIONS:
- analyze_position(fen, depth): Get detailed engine analysis
- get_best_moves(fen, count): Get top candidate moves  
- evaluate_move(fen, move): Analyze specific move quality

Respond as {core['name']} with authentic wisdom, personality, and chess insight."""
        
        return prompt
    
    def generate_training_contexts(self) -> List[Dict]:
        """Generate training contexts tailored to the master's style"""
        profile = self.personality_profile
        chess_style = profile['core_identity']['chess_style']
        
        # Base contexts that work for everyone
        base_contexts = [
            {
                'type': 'learning_guidance',
                'weight': 25,
                'questions': [
                    "How should I study chess effectively?",
                    "I'm struggling to improve my rating",
                    "What's the best way to analyze my games?",
                    "How do I overcome chess plateaus?",
                    "Give me advice for consistent improvement"
                ]
            },
            {
                'type': 'philosophical_discussion', 
                'weight': 20,
                'questions': [
                    "What does chess mean to you?",
                    "How has chess shaped your thinking?",
                    "What's the most important lesson chess teaches?",
                    "Why do you love this game?",
                    "What advice would you give to young players?"
                ]
            },
            {
                'type': 'general_position_analysis',
                'weight': 20,
                'questions': [
                    "How do you approach new positions?",
                    "What do you look for first in any position?",
                    "How do you decide between candidate moves?",
                    "What makes a position good or bad?",
                    "How do you create plans in complex positions?"
                ]
            }
        ]
        
        # Style-specific contexts
        style_contexts = {
            'attacking': [
                {
                    'type': 'attacking_play',
                    'weight': 20,
                    'questions': [
                        "How do you create attacking chances?",
                        "When should I sacrifice material?", 
                        "How do you build successful attacks?",
                        "What are the key principles of attacking play?",
                        "How do you sense when it's time to attack?"
                    ]
                },
                {
                    'type': 'tactical_vision',
                    'weight': 15,
                    'questions': [
                        "How do I improve my tactical vision?",
                        "What's the secret to finding combinations?",
                        "How do you calculate complex variations?",
                        "How do I develop tactical intuition?",
                        "What tactical patterns should I master first?"
                    ]
                }
            ],
            'positional': [
                {
                    'type': 'positional_understanding',
                    'weight': 20,
                    'questions': [
                        "How do I improve my positional play?",
                        "What makes a position strategically winning?",
                        "How do you evaluate pawn structures?",
                        "When should I trade pieces?",
                        "How do you build long-term advantages?"
                    ]
                },
                {
                    'type': 'strategic_planning',
                    'weight': 15,
                    'questions': [
                        "How do you create effective plans?",
                        "What are the key strategic principles?",
                        "How do you handle closed positions?",
                        "When should I focus on piece activity vs pawn structure?",
                        "How do you improve piece coordination?"
                    ]
                }
            ],
            'tactical': [
                {
                    'type': 'calculation_skills',
                    'weight': 20,
                    'questions': [
                        "How do you calculate variations accurately?",
                        "What's your method for finding tactics?",
                        "How do I avoid calculation errors?",
                        "How deep should I calculate?",
                        "What's the best way to practice calculation?"
                    ]
                },
                {
                    'type': 'pattern_recognition',
                    'weight': 15,
                    'questions': [
                        "How do I develop pattern recognition?",
                        "What tactical motifs are most important?",
                        "How do you see combinations quickly?",
                        "How do I train my tactical eye?",
                        "What patterns should beginners learn first?"
                    ]
                }
            ],
            'endgame_specialist': [
                {
                    'type': 'endgame_mastery',
                    'weight': 20,
                    'questions': [
                        "How do I master chess endgames?",
                        "What endgames should I study first?",
                        "How do you approach endgame positions?",
                        "Why are endgames so important?",
                        "How do I improve my endgame technique?"
                    ]
                },
                {
                    'type': 'technical_precision',
                    'weight': 15,
                    'questions': [
                        "How do you maintain accuracy in long games?",
                        "What's the key to precise technique?",
                        "How do you convert winning positions?",
                        "How do I avoid technical mistakes?",
                        "What separates good technique from great technique?"
                    ]
                }
            ],
            'balanced': [
                {
                    'type': 'adaptive_play',
                    'weight': 20,
                    'questions': [
                        "How do you adapt to different position types?",
                        "How do you decide what style to play?",
                        "What makes a well-rounded chess player?",
                        "How do you handle unfamiliar positions?",
                        "How do I develop versatility in my play?"
                    ]
                },
                {
                    'type': 'decision_making',
                    'weight': 15,
                    'questions': [
                        "How do you make difficult decisions?",
                        "How do you choose between equally good moves?",
                        "What factors do you consider in complex positions?",
                        "How do you handle time pressure decisions?",
                        "How do I trust my chess judgment?"
                    ]
                }
            ]
        }
        
        # Add function calling context for all styles
        function_context = {
            'type': 'function_calling_analysis',
            'weight': 0,  # Will be set based on total
            'questions': [
                "Analyze this position: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                "What are the best moves here: r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
                "Should I play this move: rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq d6 0 4 move d5",
                "Help me understand this position: r2qkbnr/ppp1pppp/2n5/3p1b2/3P4/2N2N2/PPP1PPPP/R1BQKB1R w KQkq - 4 4",
                "What's the evaluation of this middlegame?"
            ]
        }
        
        # Combine contexts
        all_contexts = base_contexts.copy()
        style_specific = style_contexts.get(chess_style, style_contexts['balanced'])
        all_contexts.extend(style_specific)
        
        # Calculate function calling weight
        total_weight = sum(ctx['weight'] for ctx in all_contexts)
        function_context['weight'] = max(10, total_weight // 6)  # About 15% function calling
        all_contexts.append(function_context)
        
        return all_contexts
    
    def generate_response_for_context(self, question: str, context_type: str) -> str:
        """Generate authentic response for the context"""
        profile = self.personality_profile
        core = profile['core_identity']
        expression = profile['expression_patterns']
        
        # Generate response components
        response_parts = []
        
        # Opening gesture based on energy and communication style
        opening = self.generate_contextual_opening(expression['energy_level'], 
                                                 core['communication_style'], 
                                                 context_type)
        response_parts.append(opening)
        
        # Core insight based on master's approach and the question
        core_insight = self.generate_core_insight(question, context_type)
        response_parts.append(core_insight)
        
        # Add chess-specific guidance
        if context_type in ['attacking_play', 'tactical_vision', 'calculation_skills']:
            chess_guidance = self.generate_chess_guidance(context_type)
            response_parts.append(chess_guidance)
        
        # Function call for analysis contexts
        if context_type == 'function_calling_analysis' and 'rnbq' in question:
            function_call = self.generate_function_call(question)
            response_parts.append(function_call)
        
        # Occasional authentic quote
        if random.random() < self.QUOTE_USAGE_RATE:
            quote = self.select_relevant_quote(question, context_type)
            if quote:
                response_parts.append(f'As I always believed: "{quote}"')
        
        # Encouraging conclusion based on teaching style
        conclusion = self.generate_teaching_conclusion(expression['teaching_style'])
        response_parts.append(conclusion)
        
        response = ' '.join(response_parts)
        return self.ensure_response_quality(response)
    
    def generate_contextual_opening(self, energy_level: str, comm_style: str, context_type: str) -> str:
        """Generate opening appropriate to energy, style, and context"""
        
        # Energy-based base openings
        energy_openings = {
            'high': ["*lights up with enthusiasm*", "*becomes animated*", "*leans forward eagerly*"],
            'moderate': ["*considers thoughtfully*", "*nods with interest*", "*settles in to discuss*"],
            'low': ["*speaks calmly*", "*reflects quietly*", "*responds thoughtfully*"]
        }
        
        # Context modifications
        if context_type == 'philosophical_discussion':
            if comm_style == 'philosophical':
                return "*contemplates deeply*"
            elif energy_level == 'high':
                return "*eyes light up with passion*"
        elif context_type in ['attacking_play', 'tactical_vision']:
            if energy_level in ['high', 'moderate']:
                return "*becomes excited about tactics*"
        elif context_type == 'function_calling_analysis':
            return "*prepares to analyze the position*"
        
        return random.choice(energy_openings[energy_level])
    
    def generate_core_insight(self, question: str, context_type: str) -> str:
        """Generate core insight based on master's style and question"""
        profile = self.personality_profile
        chess_style = profile['core_identity']['chess_style']
        decision_approach = profile['core_identity']['decision_approach']
        
        question_lower = question.lower()
        
        # Match insights to chess style and question type
        if 'improve' in question_lower or 'study' in question_lower:
            if decision_approach == 'analytical':
                return "Systematic study with clear goals will accelerate your chess development."
            elif decision_approach == 'intuitive':
                return "Play lots of games and trust your developing chess instincts."
            else:
                return "Balance systematic study with practical play for well-rounded improvement."
        
        elif 'attack' in question_lower or 'sacrifice' in question_lower:
            if chess_style == 'attacking':
                return "Attacking chess flows from superior development and active piece play."
            elif chess_style == 'positional':
                return "The strongest attacks arise from good positional foundations."
            else:
                return "Look for the right moment when your pieces are perfectly coordinated."
        
        elif 'position' in question_lower or 'plan' in question_lower:
            if chess_style == 'positional':
                return "Every position tells a story through its pawn structure and piece placement."
            elif decision_approach == 'analytical':
                return "Break down positions into key elements: structure, activity, and safety."
            else:
                return "Feel the natural flow of the position and what it's asking you to do."
        
        elif 'meaning' in question_lower or 'love' in question_lower:
            return profile['chess_philosophy']['core_belief']
        
        # Default insight based on style
        style_insights = {
            'attacking': "Chess is about creating problems your opponent cannot solve.",
            'positional': "Small advantages carefully nursed grow into decisive superiority.",
            'tactical': "Pattern recognition combined with precise calculation unlocks chess mastery.",
            'endgame_specialist': "The endgame reveals chess's deepest truths about precision and technique.",
            'balanced': "Every position demands its own unique approach and understanding."
        }
        
        return style_insights.get(chess_style, "Understanding chess deeply transforms how you see the entire game.")
    
    def generate_chess_guidance(self, context_type: str) -> str:
        """Generate specific chess guidance for the context"""
        profile = self.personality_profile
        principles = profile['chess_philosophy']['approach_principles']
        
        # Select relevant principle
        relevant_principle = random.choice(principles)
        
        context_guidance = {
            'attacking_play': f"In attacking play, {relevant_principle.lower()}",
            'tactical_vision': f"For tactical improvement, {relevant_principle.lower()}", 
            'calculation_skills': f"When calculating, {relevant_principle.lower()}",
            'positional_understanding': f"Positionally speaking, {relevant_principle.lower()}",
            'endgame_mastery': f"In endgames, {relevant_principle.lower()}"
        }
        
        return context_guidance.get(context_type, relevant_principle)
    
    def generate_function_call(self, question: str) -> str:
        """Generate function call for position analysis"""
        # Extract FEN if present
        fen_match = re.search(r'([rnbqkpRNBQKP0-9/\s]+\s+[wb]\s+[KQkq-]+\s+[a-h0-9-]+\s+\d+\s+\d+)', question)
        
        if fen_match:
            fen = fen_match.group(1).strip()
            return f"Let me analyze this position for you. analyze_position('{fen}', 15)"
        elif 'best moves' in question.lower():
            return "I'll find the top candidate moves. get_best_moves(current_position, 5)"
        elif 'evaluation' in question.lower():
            return "Let me evaluate this position. analyze_position(current_fen, 12)"
        else:
            return "Let me get some engine analysis to provide the best insight."
    
    def select_relevant_quote(self, question: str, context_type: str) -> Optional[str]:
        """Select most relevant quote for the context"""
        quotes = self.personality_profile['authentic_expressions']
        if not quotes:
            return None
        
        question_lower = question.lower()
        
        # Simple relevance matching
        for quote in quotes:
            quote_lower = quote.lower()
            if ('sacrifice' in question_lower and 'sacrifice' in quote_lower) or \
               ('chess' in question_lower and 'chess' in quote_lower) or \
               ('play' in question_lower and 'play' in quote_lower):
                return quote
        
        # Return a random quote if no perfect match
        return random.choice(quotes)
    
    def generate_teaching_conclusion(self, teaching_style: str) -> str:
        """Generate conclusion based on teaching approach"""
        conclusions = {
            'inspiring': [
                "Let your passion for chess guide your journey forward.",
                "Trust in your growing understanding and keep exploring.",
                "The beauty of chess reveals itself to dedicated students."
            ],
            'methodical': [
                "Apply this systematically and you'll see steady progress.", 
                "Consistent practice with this approach builds mastery.",
                "Step by step, these principles will transform your play."
            ],
            'contemplative': [
                "Reflect on these ideas and let them deepen your understanding.",
                "The wisdom of chess unfolds gradually to those who seek it.",
                "Take time to truly absorb these concepts."
            ],
            'supportive': [
                "You have everything you need to succeed with this approach.",
                "I believe in your ability to master these concepts.",
                "Keep working with patience and confidence."
            ],
            'balanced': [
                "This understanding will serve you well in your chess journey.",
                "Practice these ideas and watch your chess flourish.",
                "These principles will guide you to stronger play."
            ]
        }
        
        return random.choice(conclusions.get(teaching_style, conclusions['balanced']))
    
    def ensure_response_quality(self, response: str) -> str:
        """Ensure response meets quality standards"""
        # Length adjustment
        if len(response) < self.MIN_RESPONSE_LENGTH:
            response += " Understanding these fundamentals deeply is what separates good players from truly great ones."
        elif len(response) > self.MAX_RESPONSE_LENGTH:
            # Intelligent truncation
            sentences = response.split('. ')
            truncated = []
            current_length = 0
            
            for sentence in sentences:
                if current_length + len(sentence) + 2 <= self.MAX_RESPONSE_LENGTH:
                    truncated.append(sentence)
                    current_length += len(sentence) + 2
                else:
                    break
            
            if truncated:
                response = '. '.join(truncated)
                if not response.endswith(('.', '!', '?')):
                    response += '.'
        
        # Clean up spacing and formatting
        response = re.sub(r'\s+', ' ', response).strip()
        return response
    
    def generate_complete_training_set(self, num_examples: int = 50) -> List[Dict]:
        """Generate complete training set"""
        print(f"\n🎯 Generating {num_examples} training examples...")
        print(f"🚀 Creating authentic {self.player_data['name']} personality training data...\n")
        
        contexts = self.generate_training_contexts()
        examples = []
        
        # Calculate examples per context based on weights
        total_weight = sum(ctx['weight'] for ctx in contexts)
        
        for context in contexts:
            # Calculate how many examples for this context
            context_proportion = context['weight'] / total_weight
            context_examples = max(1, int(num_examples * context_proportion))
            
            for _ in range(context_examples):
                try:
                    question = random.choice(context['questions'])
                    response = self.generate_response_for_context(question, context['type'])
                    
                    # Check for uniqueness
                    response_hash = hashlib.md5(response.encode()).hexdigest()
                    if response_hash in self.used_response_hashes:
                        continue
                    
                    self.used_response_hashes.add(response_hash)
                    self.context_distribution[context['type']] += 1
                    
                    example = {
                        "messages": [
                            {"role": "system", "content": self.create_system_prompt()},
                            {"role": "user", "content": question},
                            {"role": "assistant", "content": response}
                        ]
                    }
                    
                    examples.append(example)
                    
                except Exception as e:
                    print(f"⚠️ Error generating example: {e}")
                    continue
            
            print(f"✅ Generated {context_examples} examples for {context['type']}")
        
        # Trim to exact number requested
        examples = examples[:num_examples]
        
        print(f"\n📊 Training set complete: {len(examples)} examples")
        return examples
    
    def display_training_statistics(self, examples: List[Dict]):
        """Display comprehensive training statistics"""
        print(f"\n📊 TRAINING STATISTICS FOR {self.player_data['name']}")
        print("=" * 60)
        
        # Basic stats
        print(f"📈 Total examples: {len(examples)}")
        print(f"🎯 Unique responses: {len(self.used_response_hashes)}")
        
        # Context distribution
        print(f"\n📋 Context Distribution:")
        total_contexts = sum(self.context_distribution.values())
        for context_type, count in self.context_distribution.most_common():
            percentage = (count / total_contexts) * 100 if total_contexts > 0 else 0
            print(f"   {context_type}: {count} ({percentage:.1f}%)")
        
        # Response quality metrics
        lengths = [len(ex['messages'][2]['content']) for ex in examples]
        avg_length = sum(lengths) / len(lengths) if lengths else 0
        in_range = sum(1 for l in lengths if self.MIN_RESPONSE_LENGTH <= l <= self.MAX_RESPONSE_LENGTH)
        quality_percentage = (in_range / len(lengths)) * 100 if lengths else 0
        
        print(f"\n📏 Response Quality:")
        print(f"   Average length: {avg_length:.0f} characters")
        print(f"   Optimal range: {quality_percentage:.1f}% ({in_range}/{len(lengths)})")
        print(f"   Target range: {self.MIN_RESPONSE_LENGTH}-{self.MAX_RESPONSE_LENGTH} chars")
        
        # Function calling stats
        function_examples = sum(1 for ex in examples if any(func in ex['messages'][2]['content'] 
                               for func in ['analyze_position', 'get_best_moves', 'evaluate_move']))
        func_percentage = (function_examples / len(examples)) * 100 if examples else 0
        print(f"🔧 Function calling: {function_examples} examples ({func_percentage:.1f}%)")
        
        # Quote usage
        quote_examples = sum(1 for ex in examples if any(quote in ex['messages'][2]['content'] 
                            for quote in self.player_data['key_quotes']))
        quote_percentage = (quote_examples / len(examples)) * 100 if examples else 0
        print(f"💬 Quote usage: {quote_examples} examples ({quote_percentage:.1f}%)")
        
        print("=" * 60)
    
    def save_training_data(self, examples: List[Dict]) -> str:
        """Save training data and metadata"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        safe_name = self.player_data['name'].lower().replace(' ', '_').replace("'", "")
        filename = f"{safe_name}_complete_training_{timestamp}.jsonl"
        
        # Save training examples
        with open(filename, 'w', encoding='utf-8') as f:
            for example in examples:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        
        print(f"\n💾 Saved {len(examples)} training examples to {filename}")
        
        # Save complete metadata including personality profile
        metadata = {
            'player_data': self.player_data,
            'personality_profile': self.personality_profile,
            'training_stats': {
                'total_examples': len(examples),
                'context_distribution': dict(self.context_distribution),
                'generation_date': datetime.now().isoformat(),
                'approach': 'all_in_one_minimal_data'
            },
            'file_info': {
                'training_file': filename,
                'examples_count': len(examples),
                'ready_for_fine_tuning': True
            }
        }
        
        metadata_filename = filename.replace('.jsonl', '_complete_profile.json')
        with open(metadata_filename, 'w', encoding='utf-8') as f:
            json.dump(metadata, f, indent=2, ensure_ascii=False)
        
        print(f"📋 Saved complete profile to {metadata_filename}")
        
        return filename
    
    def show_sample_examples(self, examples: List[Dict]):
        """Show sample training examples"""
        print(f"\n📝 SAMPLE TRAINING EXAMPLES:")
        print("=" * 50)
        
        # Show 2-3 diverse examples
        sample_indices = [0, len(examples)//2, -1] if len(examples) > 2 else [0]
        
        for i, idx in enumerate(sample_indices[:3], 1):
            if idx < len(examples):
                example = examples[idx]
                print(f"\n--- Sample {i} ---")
                print(f"Q: {example['messages'][1]['content']}")
                print(f"A: {example['messages'][2]['content'][:200]}...")
                
                # Check for special features
                has_quote = any(quote in example['messages'][2]['content'] 
                               for quote in self.player_data['key_quotes'])
                has_function = any(func in example['messages'][2]['content'] 
                                  for func in ['analyze_position', 'get_best_moves'])
                
                features = []
                if has_quote: features.append("Quote")
                if has_function: features.append("Function call")
                if features:
                    print(f"Features: {', '.join(features)}")
                
                print("-" * 30)
    
    def run_complete_generation(self):
        """Run the complete personality creation and training generation process"""
        try:
            # Step 1: Collect player data
            self.collect_player_data()
            
            # Step 2: Build personality profile
            self.build_personality_profile()
            
            # Step 3: Get training parameters
            print(f"\n🎓 TRAINING DATA GENERATION")
            print("=" * 40)
            num_examples = input("How many training examples to generate? (recommended 40-80): ").strip()
            try:
                num_examples = int(num_examples)
                if num_examples < 10:
                    num_examples = 40
                    print(f"Using minimum recommended: {num_examples}")
                elif num_examples > 200:
                    num_examples = 200
                    print(f"Using maximum recommended: {num_examples}")
            except ValueError:
                num_examples = 50
                print(f"Using default: {num_examples}")
            
            # Step 4: Generate training data
            examples = self.generate_complete_training_set(num_examples)
            
            if not examples:
                print("❌ No training examples generated!")
                return
            
            # Step 5: Display statistics
            self.display_training_statistics(examples)
            
            # Step 6: Show samples
            self.show_sample_examples(examples)
            
            # Step 7: Save everything
            filename = self.save_training_data(examples)
            
            # Step 8: Success summary
            print(f"\n🎉 SUCCESS! Complete training data created!")
            print(f"✨ {self.player_data['name']} personality is ready for fine-tuning!")
            print(f"\n🚀 Next steps:")
            print(f"   1. Upload {filename} to OpenAI fine-tuning")
            print(f"   2. Train your custom model")
            print(f"   3. Use in your chess application")
            print(f"   4. Enjoy authentic chess master conversations!")
            
            return filename
            
        except KeyboardInterrupt:
            print(f"\n\n👋 Generation cancelled. Your progress has been saved!")
        except Exception as e:
            print(f"\n❌ Error during generation: {e}")
            print(f"💡 Please try again or check your inputs.")


def main():
    """Main execution function"""
    generator = AllInOnePersonalityGenerator()
    generator.run_complete_generation()


if __name__ == "__main__":
    main()