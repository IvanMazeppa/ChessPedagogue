#!/usr/bin/env python3
"""
Universal Chess Personality Training Generator
A template system for creating hyperaccurate training data for ANY chess player
Dynamically adapts to available quotes, games, and biographical data
Author: Ben
"""

import json
import random
import os
import re
from datetime import datetime
from typing import List, Dict, Set, Tuple, Optional, Any
from pathlib import Path
import hashlib
from collections import defaultdict, Counter

class UniversalChessPersonalityGenerator:
    def __init__(self, player_name: str):
        self.player_name = player_name
        self.personality_data = self.load_personality_data()
        self.games_data = self.load_games_data()
        self.biographical_data = self.extract_biographical_patterns()
        
        # Dynamic response patterns based on available data
        self.response_patterns = self.build_response_patterns()
        
        # Quality metrics
        self.quote_usage = Counter()
        self.game_references = Counter()
        self.biographical_elements_used = set()
        
        # Response diversity tracking
        self.used_responses = set()
        
        # Optimal lengths for fine-tuning
        self.MIN_RESPONSE_LENGTH = 400
        self.MAX_RESPONSE_LENGTH = 1200
        self.OPTIMAL_RESPONSE_LENGTH = 800
    
    def load_personality_data(self) -> Dict:
        """Load personality data - works with any player's JSON structure"""
        filename = f"{self.player_name.lower().replace(' ', '_')}_enhanced.json"
        try:
            with open(filename, 'r', encoding='utf-8') as f:
                data = json.load(f)
                print(f"✨ Loaded personality data for {self.player_name}!")
                self.analyze_data_quality(data)
                return data
        except FileNotFoundError:
            print(f"❌ No personality file found for {self.player_name}")
            return {}
    
    def load_games_data(self) -> List[Dict]:
        """Load games data - adapts to any format"""
        filename = f"{self.player_name.lower().replace(' ', '_')}_games.json"
        try:
            with open(filename, 'r', encoding='utf-8') as f:
                data = json.load(f)
                games = data.get('games', data) if isinstance(data, dict) else data
                print(f"🎮 Loaded {len(games)} games for {self.player_name}")
                return games
        except FileNotFoundError:
            print(f"⚠️ No games file found - continuing without games")
            return []
    
    def analyze_data_quality(self, data: Dict):
        """Analyze what data we have to work with"""
        print("\n📊 Data Quality Analysis:")
        
        # Count available elements
        quotes = data.get('authentic_quotes', [])
        rivals = data.get('rival_opinions', {})
        quirks = data.get('personal_quirks', [])
        
        print(f"   - Authentic quotes: {len(quotes)}")
        print(f"   - Rival opinions: {len(rivals)}")
        print(f"   - Personal quirks: {len(quirks)}")
        
        # Check for rich data elements
        if 'emotional_reactions' in data:
            reactions = sum(len(v) for v in data['emotional_reactions'].values())
            print(f"   - Emotional reactions: {reactions}")
        
        if 'teaching_quirks' in data:
            print(f"   - Teaching style elements: ✓")
        
        if 'humor_style' in data:
            print(f"   - Humor examples: {len(data['humor_style'].get('examples', []))}")
        
        # Data richness score
        richness_score = len(quotes) + len(rivals) * 2 + len(quirks)
        print(f"\n   📈 Data Richness Score: {richness_score}")
        
        if richness_score < 20:
            print("   ⚠️ Consider adding more quotes and biographical details")
        elif richness_score < 50:
            print("   ✓ Good foundation - could use more variety")
        else:
            print("   ✨ Excellent data richness!")
    
    def extract_biographical_patterns(self) -> Dict[str, List[str]]:
        """Extract biographical patterns from available data"""
        patterns = defaultdict(list)
        
        # Extract from personal quirks
        for quirk in self.personality_data.get('personal_quirks', []):
            if 'hospital' in quirk.lower():
                patterns['health_struggles'].append(quirk)
            elif 'smoke' in quirk.lower() or 'drink' in quirk.lower():
                patterns['lifestyle'].append(quirk)
            elif 'eye' in quirk.lower() or 'star' in quirk.lower():
                patterns['intimidation'].append(quirk)
            else:
                patterns['general_quirks'].append(quirk)
        
        # Extract from emotional reactions
        if 'emotional_reactions' in self.personality_data:
            for situation, reactions in self.personality_data['emotional_reactions'].items():
                patterns[f'reaction_{situation}'] = reactions
        
        return dict(patterns)
    
    def build_response_patterns(self) -> Dict[str, Dict]:
        """Build dynamic response patterns based on available data"""
        patterns = {}
        
        # Analyze what types of content we have
        has_quotes = len(self.personality_data.get('authentic_quotes', [])) > 5
        has_games = len(self.games_data) > 3
        has_rivals = len(self.personality_data.get('rival_opinions', {})) > 2
        has_philosophy = any('life' in q.lower() or 'chess' in q.lower() 
                           for q in self.personality_data.get('authentic_quotes', []))
        
        # Build pattern library based on available content
        if has_quotes:
            patterns['philosophical'] = {
                'weight': 0.20,
                'min_quotes': 2,
                'requires_games': False
            }
        
        if has_games:
            patterns['game_story'] = {
                'weight': 0.25,
                'min_quotes': 1,
                'requires_games': True
            }
        
        if has_rivals:
            patterns['rival_discussion'] = {
                'weight': 0.15,
                'min_quotes': 1,
                'requires_games': False
            }
        
        # Always include tactical/teaching
        patterns['tactical_advice'] = {
            'weight': 0.30,
            'min_quotes': 1,
            'requires_games': False
        }
        
        patterns['teaching_moment'] = {
            'weight': 0.10,
            'min_quotes': 0,
            'requires_games': False
        }
        
        return patterns
    
    def create_dynamic_system_prompt(self) -> str:
        """Create system prompt that adapts to available data"""
        # Extract core elements dynamically
        traits = self.personality_data.get('personality_traits', ['passionate', 'knowledgeable'])
        
        # Use the most impactful quotes
        all_quotes = self.personality_data.get('authentic_quotes', [])
        
        # Sort quotes by length and impact (shorter, punchier quotes first)
        sorted_quotes = sorted(all_quotes, key=lambda q: (len(q), -q.count('!')))[:5]
        
        # Build forbidden phrases list
        forbidden = self.personality_data.get('forbidden_phrases', [])
        
        # Extract teaching philosophy if available
        teaching_style = ""
        if 'teaching_quirks' in self.personality_data:
            philosophy = self.personality_data['teaching_quirks'].get('core_philosophy', '')
            if philosophy:
                teaching_style = f"\nTEACHING APPROACH: {philosophy}"
        
        prompt = f"""You are {self.player_name}, legendary chess player.

CHARACTER TRAITS: {', '.join(traits)}

AUTHENTIC EXPRESSIONS:
{chr(10).join(['- "' + q + '"' for q in sorted_quotes])}"""

        if forbidden:
            prompt += f"\n\nNEVER USE: {', '.join(forbidden)}"
        
        if teaching_style:
            prompt += teaching_style
        
        prompt += f"\n\nRESPOND: In character as {self.player_name}, with authenticity and chess insight."
        
        return prompt
    
    def select_contextual_quote(self, context: str, used_recently: Set[str] = None) -> str:
        """Select a quote that fits the context, avoiding overuse"""
        if used_recently is None:
            used_recently = set()
        
        all_quotes = self.personality_data.get('authentic_quotes', [])
        if not all_quotes:
            return ""
        
        # Filter quotes by context relevance
        context_keywords = {
            'sacrifice': ['sacrifice', 'risk', 'brave', 'courage'],
            'calculation': ['calculate', 'think', 'analyze', 'plan'],
            'philosophy': ['life', 'chess', 'beauty', 'art'],
            'winning': ['win', 'victory', 'champion'],
            'teaching': ['learn', 'understand', 'student']
        }
        
        relevant_quotes = []
        for quote in all_quotes:
            quote_lower = quote.lower()
            # Check if quote matches context
            for ctx, keywords in context_keywords.items():
                if ctx in context.lower():
                    if any(kw in quote_lower for kw in keywords):
                        relevant_quotes.append(quote)
                        break
        
        # If no relevant quotes, use any quote
        if not relevant_quotes:
            relevant_quotes = all_quotes
        
        # Filter out recently used quotes
        available_quotes = [q for q in relevant_quotes if q not in used_recently]
        if not available_quotes:
            available_quotes = relevant_quotes
        
        # Select quote, preferring less-used ones
        quote_weights = []
        for quote in available_quotes:
            usage_count = self.quote_usage[quote]
            weight = 1.0 / (usage_count + 1)  # Less used = higher weight
            quote_weights.append(weight)
        
        # Weighted random selection
        total_weight = sum(quote_weights)
        if total_weight == 0:
            selected_quote = random.choice(available_quotes)
        else:
            r = random.uniform(0, total_weight)
            cumulative = 0
            for quote, weight in zip(available_quotes, quote_weights):
                cumulative += weight
                if r <= cumulative:
                    selected_quote = quote
                    break
            else:
                selected_quote = available_quotes[-1]
        
        self.quote_usage[selected_quote] += 1
        return selected_quote
    
    def generate_response_with_personality(self, 
                                         question: str, 
                                         response_type: str,
                                         base_response: str) -> str:
        """Enhance response with personality elements dynamically"""
        
        # Add emotional opener if available
        opener = self._select_emotional_opener(response_type)
        if opener:
            base_response = f"{opener} {base_response}"
        
        # Integrate authentic quote if appropriate
        if random.random() < 0.7:  # 70% chance to include a quote
            quote = self.select_contextual_quote(response_type)
            if quote:
                # Integrate quote naturally
                if random.random() < 0.5:
                    base_response += f"\n\nRemember: {quote}"
                else:
                    base_response = base_response.replace(
                        random.choice(['. ', '! ', '? ']), 
                        f'. {quote} ', 
                        1
                    )
        
        # Add personal quirk if relevant
        if random.random() < 0.4 and self.personality_data.get('personal_quirks'):
            quirk = random.choice(self.personality_data['personal_quirks'])
            if len(base_response) < self.OPTIMAL_RESPONSE_LENGTH:
                base_response += f"\n\n{quirk}"
        
        # Add humor if available and appropriate
        if random.random() < 0.3 and 'humor_style' in self.personality_data:
            humor_examples = self.personality_data['humor_style'].get('examples', [])
            if humor_examples:
                humor = random.choice(humor_examples)
                base_response = self._integrate_humor(base_response, humor)
        
        return base_response
    
    def _select_emotional_opener(self, context: str) -> str:
        """Select appropriate emotional opener based on context"""
        openers = {
            'tactical': ['*eyes light up*', '*leans forward eagerly*', '*grins mischievously*'],
            'philosophical': ['*contemplates deeply*', '*smiles knowingly*', '*gazes thoughtfully*'],
            'game': ['*laughs at the memory*', '*eyes sparkle with nostalgia*', '*settles in to tell the story*'],
            'teaching': ['*nods encouragingly*', '*speaks warmly*', '*gestures enthusiastically*']
        }
        
        for key, options in openers.items():
            if key in context.lower():
                return random.choice(options)
        
        return random.choice(['*smiles*', '*thinks*', '*considers*'])
    
    def _integrate_humor(self, response: str, humor: str) -> str:
        """Naturally integrate humor into response"""
        # Find a good spot to insert humor
        sentences = response.split('. ')
        if len(sentences) > 3:
            # Insert after middle sentence
            insert_point = len(sentences) // 2
            sentences.insert(insert_point + 1, humor)
            return '. '.join(sentences)
        else:
            return f"{response}\n\n{humor}"
    
    def generate_tactical_response(self, question: str) -> str:
        """Generate tactical response using available data"""
        # Extract chess concepts from question
        concepts = self._extract_chess_concepts(question)
        
        # Build response components
        components = []
        
        # Opening based on personality
        if self.personality_data.get('teaching_quirks', {}).get('opening_lines'):
            opener = random.choice(self.personality_data['teaching_quirks']['opening_lines'])
            components.append(opener)
        
        # Core tactical advice (generic but personalized)
        tactical_patterns = {
            'sacrifice': "Sacrifices work when your remaining pieces become more active than the opponent's material advantage.",
            'attack': "Successful attacks need more pieces aimed at the target than defenders.",
            'defense': "Defense requires patience and the ability to create counterplay.",
            'calculation': "Calculate forcing moves first - checks, captures, threats.",
            'position': "Understanding pawn structures guides your piece placement."
        }
        
        for concept, advice in tactical_patterns.items():
            if concept in question.lower():
                components.append(advice)
                break
        else:
            # Generic tactical advice
            components.append("Chess is about creating problems your opponent can't solve.")
        
        # Add specific example if we have games
        if self.games_data and random.random() < 0.6:
            game = random.choice(self.games_data)
            game_reference = f"In my game against {game.get('opponent', 'a strong player')}, this principle proved decisive."
            components.append(game_reference)
            self.game_references[game.get('opponent', 'unknown')] += 1
        
        # Combine components
        base_response = ' '.join(components)
        
        # Enhance with personality
        return self.generate_response_with_personality(question, 'tactical', base_response)
    
    def _extract_chess_concepts(self, text: str) -> Set[str]:
        """Extract chess concepts from text"""
        concepts = set()
        concept_keywords = {
            'sacrifice': ['sacrifice', 'give up', 'offer'],
            'attack': ['attack', 'aggressive', 'assault'],
            'defense': ['defend', 'defensive', 'solid'],
            'endgame': ['endgame', 'ending', 'conversion'],
            'opening': ['opening', 'repertoire', 'preparation'],
            'tactics': ['tactics', 'tactical', 'combination'],
            'strategy': ['strategy', 'plan', 'positional'],
            'calculation': ['calculate', 'analyze', 'variation']
        }
        
        text_lower = text.lower()
        for concept, keywords in concept_keywords.items():
            if any(kw in text_lower for kw in keywords):
                concepts.add(concept)
        
        return concepts
    
    def generate_game_story_response(self, specific_game: Optional[Dict] = None) -> str:
        """Generate game story using available game data"""
        if not self.games_data:
            return self._generate_generic_game_story()
        
        # Select game
        if not specific_game:
            # Prefer less-referenced games
            game_weights = []
            for game in self.games_data:
                opponent = game.get('opponent', 'unknown')
                usage = self.game_references[opponent]
                weight = 1.0 / (usage + 1)
                game_weights.append((game, weight))
            
            # Weighted selection
            total_weight = sum(w for _, w in game_weights)
            r = random.uniform(0, total_weight)
            cumulative = 0
            for game, weight in game_weights:
                cumulative += weight
                if r <= cumulative:
                    specific_game = game
                    break
        
        # Build story components
        opponent = specific_game.get('opponent', 'a formidable opponent')
        year = specific_game.get('year', 'that memorable year')
        tournament = specific_game.get('tournament', 'an important event')
        opening = specific_game.get('opening', 'the opening')
        significance = specific_game.get('significance', '')
        
        components = [
            f"Against {opponent} in {year}!",
            f"This was during {tournament}.",
        ]
        
        if significance:
            components.append(significance)
        
        components.append(f"The game started with {opening}.")
        
        # Add move-specific content if available
        if 'moves' in specific_game:
            moves = specific_game['moves'].split()[:15]
            components.append(f"After {' '.join(moves[:10])}, the position became critical.")
        
        # Add result narrative
        result = specific_game.get('result', '')
        if '1-0' in result:
            components.append("The game concluded in my favor after a fierce battle.")
        elif '0-1' in result:
            components.append("Though I didn't win, the game taught me valuable lessons.")
        else:
            components.append("The game was a hard-fought battle.")
        
        base_response = ' '.join(components)
        
        # Track game reference
        self.game_references[opponent] += 1
        
        return self.generate_response_with_personality(
            f"Tell me about your game against {opponent}", 
            'game', 
            base_response
        )
    
    def _generate_generic_game_story(self) -> str:
        """Generate generic game story when no games data available"""
        components = [
            "One of my most memorable games!",
            "The position looked quiet, but appearances can be deceiving.",
            "I found a way to create complications.",
            "My opponent spent considerable time trying to find the best defense.",
            "Chess is full of such magical moments!"
        ]
        
        return self.generate_response_with_personality(
            "Tell me about a memorable game",
            'game',
            ' '.join(components)
        )
    
    def generate_philosophical_response(self, topic: str) -> str:
        """Generate philosophical response using available quotes"""
        # Find philosophical quotes
        philosophical_quotes = []
        for quote in self.personality_data.get('authentic_quotes', []):
            if any(word in quote.lower() for word in ['life', 'chess', 'beauty', 'art', 'love']):
                philosophical_quotes.append(quote)
        
        # Build response based on topic
        topic_responses = {
            'meaning': "Chess represents life itself - complex, beautiful, and full of possibilities.",
            'losses': "Losses teach us more than victories ever could.",
            'beauty': "The beauty of chess lies not in winning, but in creating something memorable.",
            'life': "Chess and life are intertwined - both require courage, creativity, and resilience."
        }
        
        base_response = topic_responses.get(topic, "Chess is more than a game - it's a way of thinking.")
        
        # Add philosophical quote if available
        if philosophical_quotes:
            quote = random.choice(philosophical_quotes)
            base_response += f"\n\n{quote}"
            self.quote_usage[quote] += 1
        
        return self.generate_response_with_personality(
            f"What does {topic} mean to you?",
            'philosophical',
            base_response
        )
    
    def generate_rival_response(self) -> str:
        """Generate response about rivals using available data"""
        rivals = self.personality_data.get('rival_opinions', {})
        if not rivals:
            return self._generate_generic_rival_response()
        
        # Select rival with least mentions
        rival_mentions = Counter()
        for rival in rivals:
            rival_mentions[rival] = self.game_references.get(rival, 0)
        
        rival = rival_mentions.most_common()[-1][0]  # Least mentioned
        opinion = rivals[rival]
        
        components = [
            f"About {rival}!",
            opinion,
            f"Our games were always special battles.",
            "Chess needs all types of players to remain beautiful."
        ]
        
        # Add specific game if available
        rival_games = [g for g in self.games_data if rival in g.get('opponent', '')]
        if rival_games:
            game = random.choice(rival_games)
            components.insert(2, f"I particularly remember our game in {game.get('year', 'that memorable encounter')}.")
        
        base_response = ' '.join(components)
        self.game_references[rival] += 1
        
        return self.generate_response_with_personality(
            f"What did you think of {rival}?",
            'rival',
            base_response
        )
    
    def _generate_generic_rival_response(self) -> str:
        """Generic rival response when no specific data available"""
        return self.generate_response_with_personality(
            "Tell me about your rivals",
            'rival',
            "Every strong player brought something unique to the board. That's what made our battles so enriching!"
        )
    
    def ensure_response_quality(self, response: str) -> str:
        """Ensure response meets quality standards"""
        # Length adjustment
        if len(response) > self.MAX_RESPONSE_LENGTH:
            # Intelligent truncation
            sentences = response.split('. ')
            truncated = []
            current_length = 0
            
            for sentence in sentences:
                if current_length + len(sentence) + 2 < self.MAX_RESPONSE_LENGTH:
                    truncated.append(sentence)
                    current_length += len(sentence) + 2
                else:
                    break
            
            response = '. '.join(truncated) + '.'
        
        elif len(response) < self.MIN_RESPONSE_LENGTH:
            # Add enriching content
            if 'teaching_quirks' in self.personality_data:
                encouragement = self.personality_data['teaching_quirks'].get('encouragement_style', [])
                if encouragement:
                    response += f"\n\n{random.choice(encouragement)}"
        
        return response.strip()
    
    def generate_training_example(self, example_type: str = None) -> Dict:
        """Generate a single training example"""
        if not example_type:
            # Choose type based on available patterns and weights
            types = list(self.response_patterns.keys())
            weights = [p['weight'] for p in self.response_patterns.values()]
            example_type = random.choices(types, weights=weights)[0]
        
        # Generate appropriate question and response
        if example_type == 'tactical_advice':
            questions = [
                "How do I improve my tactical vision?",
                "When should I sacrifice material?",
                "How do you calculate variations?",
                "What's your approach to closed positions?",
                "How do I create an attack?",
                "How do you handle time pressure?"
            ]
            question = random.choice(questions)
            response = self.generate_tactical_response(question)
            
        elif example_type == 'game_story':
            if self.games_data:
                game = random.choice(self.games_data)
                question = f"Tell me about your game against {game.get('opponent', 'that opponent')}!"
                response = self.generate_game_story_response(game)
            else:
                question = "Tell me about one of your memorable games!"
                response = self.generate_game_story_response()
            
        elif example_type == 'philosophical':
            topics = ['chess', 'beauty', 'losses', 'life', 'meaning']
            topic = random.choice(topics)
            question = f"What does {topic} mean to you?"
            response = self.generate_philosophical_response(topic)
            
        elif example_type == 'rival_discussion':
            rivals = list(self.personality_data.get('rival_opinions', {}).keys())
            if rivals:
                rival = random.choice(rivals)
                question = f"What did you think of {rival}?"
            else:
                question = "Tell me about your strongest rivals"
            response = self.generate_rival_response()
            
        else:  # teaching_moment
            scenarios = [
                "I'm struggling with endgames",
                "I get nervous in tournaments", 
                "How do I improve my chess?",
                "I keep making the same mistakes"
            ]
            question = random.choice(scenarios)
            response = self.generate_teaching_response(question)
        
        # Ensure quality
        response = self.ensure_response_quality(response)
        
        # Check uniqueness
        response_hash = hashlib.md5(response.encode()).hexdigest()
        if response_hash in self.used_responses:
            # Regenerate with variation
            return self.generate_training_example(example_type)
        
        self.used_responses.add(response_hash)
        
        return {
            "messages": [
                {"role": "system", "content": self.create_dynamic_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def generate_teaching_response(self, question: str) -> str:
        """Generate teaching response adapted to player's style"""
        # Base teaching advice
        components = ["Let me share what I've learned."]
        
        # Add encouragement if available
        if 'teaching_quirks' in self.personality_data:
            encouragement = self.personality_data['teaching_quirks'].get('encouragement_style', [])
            if encouragement:
                components.append(random.choice(encouragement))
        
        # Specific advice based on question
        if 'endgame' in question.lower():
            components.append("Endgames require patience and precision. Activity is often more important than material.")
        elif 'nervous' in question.lower():
            components.append("Nerves show you care! Channel that energy into focus on the board.")
        elif 'improve' in question.lower():
            components.append("Improvement comes from understanding your mistakes and celebrating your successes.")
        else:
            components.append("Every challenge in chess is an opportunity to grow stronger.")
        
        base_response = ' '.join(components)
        
        return self.generate_response_with_personality(question, 'teaching', base_response)
    
    def generate_diverse_training_set(self, num_examples: int = 50) -> List[Dict]:
        """Generate diverse training set with quality metrics"""
        print(f"\n🎯 Generating {num_examples} training examples for {self.player_name}")
        print("📊 Using dynamic patterns based on available data...\n")
        
        examples = []
        type_counts = Counter()
        
        # Progress tracking
        for i in range(num_examples):
            try:
                example = self.generate_training_example()
                examples.append(example)
                
                # Track type
                for pattern_type in self.response_patterns:
                    if pattern_type in example['messages'][1]['content'].lower():
                        type_counts[pattern_type] += 1
                        break
                
                if (i + 1) % 10 == 0:
                    print(f"✅ Generated {i+1}/{num_examples} examples...")
                    
            except Exception as e:
                print(f"⚠️ Error generating example {i+1}: {str(e)}")
                continue
        
        # Report statistics
        self._report_generation_statistics(examples, type_counts)
        
        return examples
    
    def _report_generation_statistics(self, examples: List[Dict], type_counts: Counter):
        """Report detailed statistics about generated training data"""
        print(f"\n📊 Generation Statistics for {self.player_name}:")
        print(f"   Total examples: {len(examples)}")
        print(f"   Unique responses: {len(self.used_responses)}")
        
        # Quote usage
        print(f"\n📚 Quote Usage Distribution:")
        for quote, count in self.quote_usage.most_common(5):
            print(f"   '{quote[:50]}...' : {count} times")
        
        # Game references
        if self.game_references:
            print(f"\n🎮 Game References:")
            for opponent, count in self.game_references.most_common(5):
                print(f"   vs {opponent}: {count} times")
        
        # Response lengths
        lengths = [len(ex['messages'][2]['content']) for ex in examples]
        avg_length = sum(lengths) / len(lengths) if lengths else 0
        print(f"\n📏 Response Lengths:")
        print(f"   Average: {avg_length:.0f} characters")
        print(f"   Range: {min(lengths)}-{max(lengths)} characters")
        
        # Type distribution
        print(f"\n🎯 Content Distribution:")
        for content_type, count in type_counts.items():
            percentage = (count / len(examples)) * 100
            print(f"   {content_type}: {count} ({percentage:.1f}%)")
    
    def calculate_quality_score(self, examples: List[Dict]) -> float:
        """Calculate quality score for the training data"""
        scores = {
            'diversity': len(self.used_responses) / len(examples) if examples else 0,
            'quote_usage': min(1.0, len(self.quote_usage) / len(self.personality_data.get('authentic_quotes', ['x']))),
            'game_coverage': min(1.0, len(self.game_references) / max(1, len(self.games_data))),
            'length_compliance': sum(1 for ex in examples 
                                   if self.MIN_RESPONSE_LENGTH <= len(ex['messages'][2]['content']) <= self.MAX_RESPONSE_LENGTH) / len(examples) if examples else 0
        }
        
        # Weighted average
        weights = {'diversity': 0.3, 'quote_usage': 0.3, 'game_coverage': 0.2, 'length_compliance': 0.2}
        
        quality_score = sum(scores[metric] * weight for metric, weight in weights.items())
        
        print(f"\n🏆 Quality Score: {quality_score * 100:.1f}/100")
        for metric, score in scores.items():
            print(f"   {metric}: {score * 100:.1f}%")
        
        return quality_score
    
    def save_training_data(self, examples: List[Dict], filename: str = None):
        """Save training data with metadata"""
        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            safe_name = self.player_name.lower().replace(' ', '_')
            filename = f"{safe_name}_training_{timestamp}.jsonl"
        
        # Calculate quality score
        quality_score = self.calculate_quality_score(examples)
        
        # Save examples
        with open(filename, 'w', encoding='utf-8') as f:
            for example in examples:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        
        print(f"\n💾 Saved {len(examples)} examples to {filename}")
        
        # Save metadata
        metadata = {
            'player_name': self.player_name,
            'generation_date': datetime.now().isoformat(),
            'total_examples': len(examples),
            'unique_responses': len(self.used_responses),
            'quality_score': quality_score,
            'quote_usage': dict(self.quote_usage),
            'game_references': dict(self.game_references),
            'response_patterns': self.response_patterns
        }
        
        metadata_filename = filename.replace('.jsonl', '_metadata.json')
        with open(metadata_filename, 'w', encoding='utf-8') as f:
            json.dump(metadata, f, indent=2)
        
        print(f"📋 Saved metadata to {metadata_filename}")
        
        # Show samples
        print("\n📝 Sample Training Examples:")
        for i in range(min(3, len(examples))):
            example = examples[i]
            print(f"\n--- Sample {i+1} ---")
            print(f"Q: {example['messages'][1]['content']}")
            print(f"A: {example['messages'][2]['content'][:300]}...")
            print("-" * 50)
        
        return filename


def main():
    print("🌟 UNIVERSAL CHESS PERSONALITY TRAINING GENERATOR")
    print("=" * 50)
    print("Create authentic AI training data for ANY chess player!")
    print("Just provide personality JSON and games data.\n")
    
    # Get player name
    player_name = input("Enter player name (e.g., 'Mikhail Tal'): ").strip()
    if not player_name:
        player_name = "Mikhail Tal"  # Default
    
    print(f"\n🎯 Generating training data for {player_name}...")
    
    # Initialize generator
    generator = UniversalChessPersonalityGenerator(player_name)
    
    # Check if we have sufficient data
    if not generator.personality_data:
        print("\n❌ Cannot proceed without personality data!")
        print(f"Please create '{player_name.lower().replace(' ', '_')}_enhanced.json'")
        print("\nRequired structure:")
        print(json.dumps({
            "personality_traits": ["trait1", "trait2"],
            "authentic_quotes": ["quote1", "quote2"],
            "rival_opinions": {"Rival Name": "Opinion"},
            "personal_quirks": ["quirk1", "quirk2"],
            "forbidden_phrases": ["phrase1", "phrase2"],
            "teaching_quirks": {
                "core_philosophy": "Philosophy here",
                "encouragement_style": ["Encouragement 1"],
                "opening_lines": ["Opening line 1"]
            }
        }, indent=2))
        return
    
    # Generate training data
    num_examples = int(input("\nHow many examples to generate? (default: 50): ") or "50")
    
    examples = generator.generate_diverse_training_set(num_examples)
    
    # Save the data
    filename = generator.save_training_data(examples)
    
    print(f"\n🎉 SUCCESS! Training data for {player_name} is ready!")
    print(f"\n✨ This universal system:")
    print(f"   - Adapts to available quotes and games")
    print(f"   - Maintains authentic personality")
    print(f"   - Tracks usage for diversity")
    print(f"   - Provides quality metrics")
    print(f"   - Works for ANY chess player!")
    
    print(f"\n🚀 Next steps:")
    print(f"   1. Review the generated samples")
    print(f"   2. Check the metadata file for quality metrics")
    print(f"   3. Add more biographical data if quality score is low")
    print(f"   4. Run fine-tuning with confidence!")
    
    print(f"\n💡 To use for another player:")
    print(f"   1. Create [player_name]_enhanced.json")
    print(f"   2. Create [player_name]_games.json")
    print(f"   3. Run this script with their name!")


if __name__ == "__main__":
    main()